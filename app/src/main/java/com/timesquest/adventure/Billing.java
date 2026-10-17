package com.timesquest.adventure;

interface Billing {
  boolean supported();

  boolean entitled();

  boolean canPurchase();

  boolean canRestore();

  String statusKey();

  String price();

  void refresh();

  void purchase();

  void restore();
}
