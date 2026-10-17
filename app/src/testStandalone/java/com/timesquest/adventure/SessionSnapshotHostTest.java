package com.timesquest.adventure;

import static org.junit.Assert.*;

import org.json.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class SessionSnapshotHostTest {
  JSONObject valid() throws JSONException {
    return new JSONObject(
        "{\"mode\":4,\"world\":0,\"table\":7,\"step\":0,\"a\":7,\"b\":8,\"choices\":[48,56,64,15],\"hinted\":false,\"answered\":false,\"recorded\":false,\"feedback\":\"\",\"kitchen\":16,\"timed\":false,\"elapsed\":2000}");
  }

  void rejects(JSONObject value) {
    try {
      SessionSnapshot.decode(value.toString());
      fail("Damaged snapshot accepted");
    } catch (JSONException expected) {
    }
  }

  @Test
  public void completeSnapshotRoundTrips() throws Exception {
    SessionSnapshot s = SessionSnapshot.decode(valid().toString());
    assertEquals(56, s.question().answer());
    assertEquals(16, s.kitchen());
    assertEquals(2000, s.elapsed());
  }

  @Test
  public void inconsistentAnsweredAndFractionalStateAreRejected() throws Exception {
    rejects(valid().put("answered", true));
    rejects(valid().put("step", 1.5));
    rejects(valid().put("elapsed", -1));
    rejects(valid().put("kitchen", 17));
  }

  @Test
  public void corruptedChoicesAndOversizedFeedbackAreRejected() throws Exception {
    rejects(valid().put("choices", new JSONArray("[56,56,56,56]")));
    rejects(valid().put("choices", new JSONArray("[1,2,3,4]")));
    rejects(valid().put("feedback", "x".repeat(4001)));
  }
}
