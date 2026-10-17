package com.timesquest.adventure;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import com.amazon.device.iap.*;
import com.amazon.device.iap.model.*;
import com.timesquest.core.EntitlementRestore;
import java.util.*;

/**
 * Amazon receipt adapter. Tests substitute transport only; runtime never fabricates entitlements.
 */
final class StoreBilling implements Billing, PurchasingListener {
  static final String SKU = "timesquest.premium";

  interface Gateway {
    void register(Context context, PurchasingListener listener);

    RequestId user();

    RequestId products(Set<String> skus);

    RequestId purchase(String sku);

    RequestId updates(boolean reset);

    void fulfil(String receipt);
  }

  private static final class AmazonGateway implements Gateway {
    public void register(Context c, PurchasingListener l) {
      PurchasingService.registerListener(c, l);
      PurchasingService.enablePendingPurchases();
    }

    public RequestId user() {
      return PurchasingService.getUserData();
    }

    public RequestId products(Set<String> skus) {
      return PurchasingService.getProductData(skus);
    }

    public RequestId purchase(String sku) {
      return PurchasingService.purchase(sku);
    }

    public RequestId updates(boolean reset) {
      return PurchasingService.getPurchaseUpdates(reset);
    }

    public void fulfil(String id) {
      PurchasingService.notifyFulfillment(id, FulfillmentResult.FULFILLED);
    }
  }

  final Runnable changed;
  final SharedPreferences prefs;
  final Gateway gateway;
  String user = "", localPrice = "", status = "store_connecting";
  boolean configured, validUser, confirmedUser, productAvailable, purchaseInFlight, pendingPurchase;
  boolean ownershipConfirmed;
  RequestId userRequest, productRequest, purchaseRequest, restoreRequest;
  EntitlementRestore restored;
  final Map<String, Receipt> restoreReceipts = new LinkedHashMap<>();

  StoreBilling(Activity a, Runnable changed) {
    this(a, changed, new AmazonGateway(), hasPublicKey(a));
  }

  StoreBilling(Activity a, Runnable changed, Gateway gateway, boolean configured) {
    this.changed = changed;
    this.gateway = gateway;
    this.configured = configured;
    prefs = a.getSharedPreferences("parent-store", 0);
    user = prefs.getString("last-user", "");
    validUser = !user.isEmpty();
    if (!configured) {
      status = "store_missing_key";
      return;
    }
    try {
      gateway.register(a.getApplicationContext(), this);
    } catch (RuntimeException unavailable) {
      this.configured = false;
      status = "store_unavailable";
    }
  }

  private static boolean hasPublicKey(Context c) {
    try (java.io.InputStream key = c.getAssets().open("AppstoreAuthenticationKey.pem")) {
      return key.read() != -1;
    } catch (java.io.IOException missing) {
      return false;
    }
  }

  public boolean supported() {
    return configured;
  }

  public boolean entitled() {
    return validUser && prefs.getBoolean("owned-" + user, false);
  }

  public boolean canPurchase() {
    return configured
        && confirmedUser
        && productAvailable
        && !purchaseInFlight
        && ownershipConfirmed
        && restoreRequest == null
        && !entitled();
  }

  public boolean canRestore() {
    return configured && confirmedUser && restoreRequest == null && !purchaseInFlight;
  }

  public String price() {
    return localPrice;
  }

  public String statusKey() {
    return status;
  }

  public void refresh() {
    if (!configured || purchaseInFlight) return;
    confirmedUser = false;
    ownershipConfirmed = false;
    productAvailable = false;
    localPrice = "";
    status = "store_connecting";
    try {
      userRequest = gateway.user();
      productRequest = gateway.products(Collections.singleton(SKU));
    } catch (RuntimeException unavailable) {
      status = "store_unavailable";
    }
    changed.run();
  }

  public void purchase() {
    if (!canPurchase()) return;
    purchaseInFlight = true;
    status = "purchase_pending";
    try {
      purchaseRequest = gateway.purchase(SKU);
    } catch (RuntimeException unavailable) {
      purchaseInFlight = false;
      status = "purchase_failed";
    }
    changed.run();
  }

  public void restore() {
    if (!configured || !confirmedUser || purchaseInFlight) return;
    ownershipConfirmed = false;
    restored = new EntitlementRestore(user, SKU);
    restoreReceipts.clear();
    status = "store_restoring";
    try {
      restoreRequest = gateway.updates(true);
    } catch (RuntimeException unavailable) {
      restoreRequest = null;
      status = "store_restore_failed";
    }
    changed.run();
  }

  public void onUserDataResponse(UserDataResponse response) {
    if (userRequest != null && !userRequest.equals(response.getRequestId())) return;
    userRequest = null;
    if (response.getRequestStatus() == UserDataResponse.RequestStatus.SUCCESSFUL
        && response.getUserData() != null) {
      String current = response.getUserData().getUserId();
      if (!current.equals(user)) {
        pendingPurchase = false;
        restoreRequest = null;
        restored = null;
        restoreReceipts.clear();
      }
      user = current;
      validUser = !current.isEmpty();
      confirmedUser = validUser;
      prefs.edit().putString("last-user", user).apply();
      if (validUser) restore();
    } else {
      confirmedUser = false;
      status = "store_unavailable";
      if (response.getRequestStatus() == UserDataResponse.RequestStatus.NOT_SUPPORTED) {
        validUser = false;
        user = "";
        prefs.edit().remove("last-user").apply();
      }
    }
    changed.run();
  }

  public void onProductDataResponse(ProductDataResponse response) {
    if (productRequest != null && !productRequest.equals(response.getRequestId())) return;
    productRequest = null;
    productAvailable = false;
    localPrice = "";
    if (response.getRequestStatus() == ProductDataResponse.RequestStatus.SUCCESSFUL) {
      Product product = response.getProductData().get(SKU);
      if (product != null && product.getProductType() == ProductType.ENTITLED) {
        localPrice = product.getPrice();
        productAvailable = localPrice != null && !localPrice.trim().isEmpty();
      }
    }
    if (!productAvailable) status = "store_unavailable";
    else if (restoreRequest == null) status = "store_ready";
    changed.run();
  }

  public void onPurchaseResponse(PurchaseResponse response) {
    if (purchaseRequest != null && !purchaseRequest.equals(response.getRequestId())) return;
    purchaseInFlight = false;
    purchaseRequest = null;
    switch (response.getRequestStatus()) {
      case SUCCESSFUL -> {
        pendingPurchase = false;
        Receipt receipt = response.getReceipt();
        UserData owner = response.getUserData();
        if (owner != null
            && owner.getUserId().equals(user)
            && validReceipt(receipt)
            && persistOwnership(user, !receipt.isCanceled())) {
          status = receipt.isCanceled() ? "purchase_cancelled" : "purchase_success";
          if (!receipt.isCanceled()) fulfil(receipt);
          restore();
        } else status = "purchase_failed";
      }
      case ALREADY_PURCHASED -> restore();
      case PENDING -> {
        pendingPurchase = true;
        status = "purchase_pending";
        restore();
      }
      default -> status = "purchase_failed";
    }
    changed.run();
  }

  private boolean validReceipt(Receipt receipt) {
    return receipt != null
        && SKU.equals(receipt.getSku())
        && receipt.getProductType() == ProductType.ENTITLED;
  }

  @android.annotation.SuppressLint(
      "ApplySharedPref") // Durable ownership must precede Amazon fulfilment.
  private boolean persistOwnership(String owner, boolean owned) {
    return prefs.edit().putBoolean("owned-" + owner, owned).commit();
  }

  private void fulfil(Receipt receipt) {
    try {
      gateway.fulfil(receipt.getReceiptId());
    } catch (RuntimeException retryLater) {
      /* Amazon can redeliver an unfulfilled receipt. Ownership is already durable. */
    }
  }

  public void onPurchaseUpdatesResponse(PurchaseUpdatesResponse response) {
    if (restoreRequest == null
        || !restoreRequest.equals(response.getRequestId())
        || restored == null) return;
    if (response.getRequestStatus() != PurchaseUpdatesResponse.RequestStatus.SUCCESSFUL) {
      restoreRequest = null;
      status = "store_restore_failed";
      changed.run();
      return;
    }
    String owner = response.getUserData().getUserId();
    if (!owner.equals(user) || !restored.accepts(owner)) return;
    for (Receipt receipt : response.getReceipts()) {
      restored.receipt(
          owner,
          receipt.getReceiptId(),
          receipt.getSku(),
          receipt.getProductType() == ProductType.ENTITLED,
          receipt.isCanceled());
      restoreReceipts.put(receipt.getReceiptId(), receipt);
    }
    if (response.hasMore()) {
      try {
        restoreRequest = gateway.updates(false);
      } catch (RuntimeException unavailable) {
        restoreRequest = null;
        status = "store_restore_failed";
      }
    } else {
      boolean saved = persistOwnership(owner, restored.owned());
      // Amazon sends no receipt for declined/expired requests. Pending is informational:
      // after a complete, durable snapshot, parents may explicitly try checkout again.
      // Only a valid receipt grants ownership; in-flight checkout still blocks duplicates.
      ownershipConfirmed = saved;
      restoreRequest = null;
      if (saved && restored.owned()) pendingPurchase = false;
      status =
          saved
              ? (pendingPurchase ? "purchase_pending" : "store_restored")
              : "store_restore_failed";
      if (saved)
        for (Receipt receipt : restoreReceipts.values())
          if (validReceipt(receipt) && !receipt.isCanceled()) fulfil(receipt);
    }
    changed.run();
  }
}
