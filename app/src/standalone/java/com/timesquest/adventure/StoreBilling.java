package com.timesquest.adventure;

import android.app.Activity;

final class StoreBilling implements Billing {
  StoreBilling(Activity a, Runnable changed) {}

  public boolean supported() {
    return false;
  }

  public boolean entitled() {
    return false;
  }

  public boolean canPurchase() {
    return false;
  }

  public boolean canRestore() {
    return false;
  }

  public String statusKey() {
    return "store_unavailable";
  }

  public String price() {
    return "";
  }

  public void refresh() {}

  public void purchase() {}

  public void restore() {}
}
