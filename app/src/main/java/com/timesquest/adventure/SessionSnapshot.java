package com.timesquest.adventure;

import com.timesquest.core.LearningEngine;
import java.util.*;
import org.json.*;

/** Validates an entire local mission before any live Activity state is replaced. */
record SessionSnapshot(
    int mode,
    int world,
    int table,
    int step,
    LearningEngine.Question question,
    List<Integer> choices,
    boolean hinted,
    boolean answered,
    boolean recorded,
    String feedback,
    int kitchen,
    boolean timed,
    long elapsed) {
  static SessionSnapshot decode(String data) throws JSONException {
    JSONObject s = new JSONObject(data);
    int mode = integer(s, "mode"),
        world = integer(s, "world"),
        table = integer(s, "table"),
        step = integer(s, "step");
    if (mode < 0
        || mode > 5
        || world < 0
        || world > 3
        || table < -1
        || table > 12
        || step < 0
        || step > 4) throw new JSONException("Invalid mission position");
    LearningEngine.Question q;
    try {
      q = new LearningEngine.Question(integer(s, "a"), integer(s, "b"));
    } catch (IllegalArgumentException e) {
      throw new JSONException("Invalid fact");
    }
    JSONArray raw = s.getJSONArray("choices");
    List<Integer> choices = new ArrayList<>();
    for (int i = 0; i < raw.length(); i++) {
      Object n = raw.get(i);
      if (!(n instanceof Integer) || ((Integer) n) < 1 || ((Integer) n) > 156)
        throw new JSONException("Invalid answer choice");
      choices.add((Integer) n);
    }
    if (choices.size() != 4 || new HashSet<>(choices).size() != 4 || !choices.contains(q.answer()))
      throw new JSONException("Invalid answer set");
    boolean hinted = s.getBoolean("hinted"),
        answered = s.getBoolean("answered"),
        recorded = s.getBoolean("recorded");
    if (answered && !recorded || recorded && !answered && !hinted)
      throw new JSONException("Inconsistent first-answer state");
    int kitchen = integer(s, "kitchen");
    long elapsed = s.getLong("elapsed");
    if (kitchen < 0 || kitchen > 156 || elapsed < 0 || mode == 4 && kitchen % q.b() != 0)
      throw new JSONException("Invalid mission counters");
    String feedback = s.getString("feedback");
    if (feedback.length() > 4000) throw new JSONException("Oversized feedback");
    return new SessionSnapshot(
        mode,
        world,
        table,
        step,
        q,
        Collections.unmodifiableList(choices),
        hinted,
        answered,
        recorded,
        feedback,
        kitchen,
        s.getBoolean("timed"),
        Math.min(elapsed, 600_000));
  }

  private static int integer(JSONObject json, String key) throws JSONException {
    Object value = json.get(key);
    if (!(value instanceof Integer)) throw new JSONException("Expected an integer: " + key);
    return (Integer) value;
  }
}
