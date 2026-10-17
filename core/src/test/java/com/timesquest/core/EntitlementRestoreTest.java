package com.timesquest.core;

import static org.junit.Assert.*;

import org.junit.Test;

public class EntitlementRestoreTest {
  @Test
  public void onlyMatchingAccountAndProductGrant() {
    var r = new EntitlementRestore("parent-a", "full");
    r.receipt("parent-b", "1", "full", true, false);
    r.receipt("parent-a", "2", "other", true, false);
    r.receipt("parent-a", "3", "full", false, false);
    assertFalse(r.owned());
    r.receipt("parent-a", "4", "full", true, false);
    assertTrue(r.owned());
  }

  @Test
  public void cancellationAndDuplicateReceiptsAreIdempotent() {
    var r = new EntitlementRestore("a", "full");
    r.receipt("a", "1", "full", true, false);
    r.receipt("a", "1", "full", true, false);
    assertTrue(r.owned());
    r.receipt("a", "1", "full", true, true);
    assertFalse(r.owned());
  }

  @Test
  public void multiplePagesPreserveOtherActiveReceipts() {
    var r = new EntitlementRestore("a", "full");
    r.receipt("a", "1", "full", true, false);
    r.receipt("a", "2", "full", true, true);
    assertTrue(r.owned());
  }

  @Test
  public void emptySuccessfulSnapshotRevokes() {
    assertFalse(new EntitlementRestore("a", "full").owned());
  }
}
