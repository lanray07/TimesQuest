package com.timesquest.adventure;

import static org.junit.Assert.*;

import android.app.Activity;
import android.content.Context;
import com.amazon.device.iap.PurchasingListener;
import com.amazon.device.iap.internal.model.*;
import com.amazon.device.iap.model.*;
import java.util.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.*;

/** Actual Amazon SDK response objects; recording transport cannot contact or charge a store. */
@RunWith(RobolectricTestRunner.class)
@Config(
    sdk = 28,
    manifest = Config.NONE,
    instrumentedPackages = {"com.amazon"})
@LooperMode(LooperMode.Mode.PAUSED)
public class AmazonBillingHostTest {
  @Test
  public void foregroundRefreshDoesNotInterruptCheckout() {
    user("a");
    updates("a", false);
    product();
    billing.purchase();
    int calls = gateway.calls;
    RequestId request = billing.purchaseRequest;
    billing.refresh();
    assertEquals(calls, gateway.calls);
    assertSame(request, billing.purchaseRequest);
    assertTrue(billing.confirmedUser);
    assertTrue(billing.purchaseInFlight);
  }

  @Test
  public void foregroundRefreshRestoresApprovalOfPendingPurchase() {
    user("a");
    updates("a", false);
    product();
    billing.purchase();
    billing.onPurchaseResponse(
        new PurchaseResponseBuilder()
            .setRequestId(gateway.purchase)
            .setRequestStatus(PurchaseResponse.RequestStatus.PENDING)
            .build());
    updates("a", false);
    assertFalse(billing.entitled());
    user("a");
    updates("a", false, receipt("approved", ProductType.ENTITLED, false));
    assertTrue(billing.entitled());
    assertFalse(billing.pendingPurchase);
    assertEquals(Collections.singletonList("approved"), gateway.fulfilled);
  }

  @Test
  public void pendingPurchaseCannotUnlockOrStartAnotherCheckout() {
    user("a");
    updates("a", false);
    product();
    billing.purchase();
    billing.onPurchaseResponse(
        new PurchaseResponseBuilder()
            .setRequestId(gateway.purchase)
            .setRequestStatus(PurchaseResponse.RequestStatus.PENDING)
            .build());
    assertFalse(billing.entitled());
    assertFalse(billing.canPurchase());
    updates("a", false);
    assertEquals("purchase_pending", billing.statusKey());
    assertFalse(billing.canPurchase());
  }

  Activity activity;
  StoreBilling billing;
  RecordingGateway gateway;

  static class RecordingGateway implements StoreBilling.Gateway {
    int calls, purchases;
    RequestId user, product, purchase, updates;
    boolean reset;
    List<String> fulfilled = new ArrayList<>();

    public void register(Context c, PurchasingListener l) {}

    public RequestId user() {
      calls++;
      return user = new RequestId();
    }

    public RequestId products(Set<String> skus) {
      calls++;
      return product = new RequestId();
    }

    public RequestId purchase(String sku) {
      purchases++;
      return purchase = new RequestId();
    }

    public RequestId updates(boolean reset) {
      this.reset = reset;
      calls++;
      return updates = new RequestId();
    }

    public void fulfil(String id) {
      fulfilled.add(id);
    }
  }

  @Before
  public void setup() {
    activity = Robolectric.buildActivity(Activity.class).setup().get();
    activity.getSharedPreferences("parent-store", 0).edit().clear().commit();
    gateway = new RecordingGateway();
    billing = new StoreBilling(activity, () -> {}, gateway, true);
  }

  UserData owner(String id) {
    return new UserDataBuilder().setUserId(id).setMarketplace("GB").setCountryCode("GB").build();
  }

  void user(String id) {
    billing.refresh();
    billing.onUserDataResponse(
        new UserDataResponseBuilder()
            .setRequestId(gateway.user)
            .setRequestStatus(UserDataResponse.RequestStatus.SUCCESSFUL)
            .setUserData(owner(id))
            .build());
  }

  Receipt receipt(String id, ProductType type, boolean cancelled) {
    return new ReceiptBuilder()
        .setReceiptId(id)
        .setSku(StoreBilling.SKU)
        .setProductType(type)
        .setPurchaseDate(new Date(1000))
        .setCancelDate(cancelled ? new Date(2000) : null)
        .build();
  }

  void updates(String owner, boolean more, Receipt... receipts) {
    billing.onPurchaseUpdatesResponse(
        new PurchaseUpdatesResponseBuilder()
            .setRequestId(gateway.updates)
            .setRequestStatus(PurchaseUpdatesResponse.RequestStatus.SUCCESSFUL)
            .setUserData(owner(owner))
            .setReceipts(Arrays.asList(receipts))
            .setHasMore(more)
            .build());
  }

  void product() {
    Product p =
        new ProductBuilder()
            .setSku(StoreBilling.SKU)
            .setProductType(ProductType.ENTITLED)
            .setTitle("Full adventure")
            .setDescription("Test fixture")
            .setPrice("€5.49")
            .setSmallIconUrl("https://example.invalid/icon.png")
            .build();
    billing.onProductDataResponse(
        new ProductDataResponseBuilder()
            .setRequestId(gateway.product)
            .setRequestStatus(ProductDataResponse.RequestStatus.SUCCESSFUL)
            .setUnavailableSkus(Collections.emptySet())
            .setProductData(Collections.singletonMap(StoreBilling.SKU, p))
            .build());
  }

  @Test
  public void absentConfigurationMakesNoSdkRequests() {
    RecordingGateway g = new RecordingGateway();
    StoreBilling missing = new StoreBilling(activity, () -> {}, g, false);
    missing.refresh();
    missing.purchase();
    missing.restore();
    assertEquals(0, g.calls);
    assertEquals(0, g.purchases);
    assertFalse(missing.canPurchase());
    assertEquals("store_missing_key", missing.statusKey());
  }

  @Test
  public void storePriceAndConfirmedUserControlCheckout() {
    billing.refresh();
    product();
    assertEquals("€5.49", billing.price());
    assertFalse(billing.canPurchase());
    billing.onUserDataResponse(
        new UserDataResponseBuilder()
            .setRequestId(gateway.user)
            .setRequestStatus(UserDataResponse.RequestStatus.SUCCESSFUL)
            .setUserData(owner("a"))
            .build());
    updates("a", false);
    assertTrue(billing.canPurchase());
    billing.purchase();
    billing.purchase();
    assertEquals(1, gateway.purchases);
    assertFalse(billing.entitled());
  }

  @Test
  public void paginatedRestorationCommitsOnlyAfterLastPage() {
    user("a");
    updates("a", true, receipt("r1", ProductType.ENTITLED, false));
    assertFalse(billing.entitled());
    assertFalse(gateway.reset);
    assertTrue(gateway.fulfilled.isEmpty());
    updates("a", false);
    assertTrue(billing.entitled());
    assertEquals(Collections.singletonList("r1"), gateway.fulfilled);
  }

  @Test
  public void cancellationAcrossPagesRevokesAndDoesNotFulfil() {
    user("a");
    updates("a", true, receipt("r1", ProductType.ENTITLED, false));
    updates("a", false, receipt("r1", ProductType.ENTITLED, true));
    assertFalse(billing.entitled());
    assertTrue(gateway.fulfilled.isEmpty());
  }

  @Test
  public void consumableAndFailedPurchaseNeverGrant() {
    user("a");
    updates("a", false, receipt("c1", ProductType.CONSUMABLE, false));
    assertFalse(billing.entitled());
    billing.onPurchaseResponse(
        new PurchaseResponseBuilder()
            .setRequestId(new RequestId())
            .setRequestStatus(PurchaseResponse.RequestStatus.FAILED)
            .build());
    assertFalse(billing.entitled());
    assertTrue(gateway.fulfilled.isEmpty());
  }

  @Test
  public void accountChangeRejectsStaleRestoration() {
    user("a");
    RequestId old = gateway.updates;
    user("b");
    billing.onPurchaseUpdatesResponse(
        new PurchaseUpdatesResponseBuilder()
            .setRequestId(old)
            .setRequestStatus(PurchaseUpdatesResponse.RequestStatus.SUCCESSFUL)
            .setUserData(owner("a"))
            .setReceipts(Collections.singletonList(receipt("r", ProductType.ENTITLED, false)))
            .setHasMore(false)
            .build());
    assertFalse(billing.entitled());
    updates("b", false);
    assertFalse(billing.entitled());
  }

  @Test
  public void offlineUserFailurePreservesCachedOwnershipAndDisablesCheckout() {
    activity
        .getSharedPreferences("parent-store", 0)
        .edit()
        .putString("last-user", "a")
        .putBoolean("owned-a", true)
        .commit();
    billing = new StoreBilling(activity, () -> {}, gateway, true);
    assertTrue(billing.entitled());
    billing.refresh();
    billing.onUserDataResponse(
        new UserDataResponseBuilder()
            .setRequestId(gateway.user)
            .setRequestStatus(UserDataResponse.RequestStatus.FAILED)
            .build());
    assertTrue(billing.entitled());
    assertFalse(billing.canPurchase());
    assertFalse(billing.canRestore());
  }

  @Test
  public void successfulPurchasePersistsBeforeAcknowledgement() {
    user("a");
    updates("a", false);
    product();
    billing.purchase();
    assertFalse(billing.entitled());
    billing.onPurchaseResponse(
        new PurchaseResponseBuilder()
            .setRequestId(gateway.purchase)
            .setRequestStatus(PurchaseResponse.RequestStatus.SUCCESSFUL)
            .setUserData(owner("a"))
            .setReceipt(receipt("paid", ProductType.ENTITLED, false))
            .build());
    assertTrue(billing.entitled());
    assertTrue(activity.getSharedPreferences("parent-store", 0).getBoolean("owned-a", false));
    assertEquals(Collections.singletonList("paid"), gateway.fulfilled);
  }
}
