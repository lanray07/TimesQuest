package com.timesquest.core;

import java.util.*;

/** Accumulates one complete store-user receipt snapshot before replacing a cached entitlement. */
public final class EntitlementRestore {
  private final String owner, sku;
  private final Map<String, Boolean> receipts = new HashMap<>();

  public EntitlementRestore(String owner, String sku) {
    if (owner.isEmpty()) throw new IllegalArgumentException("Store user required");
    this.owner = owner;
    this.sku = sku;
  }

  public boolean accepts(String user) {
    return owner.equals(user);
  }

  public void receipt(
      String user, String id, String product, boolean entitledType, boolean canceled) {
    if (accepts(user) && sku.equals(product) && entitledType && !id.isEmpty())
      receipts.put(id, !canceled);
  }

  public boolean owned() {
    return receipts.containsValue(true);
  }
}
