package com.timesquest.adventure;

import android.content.*;
import com.timesquest.core.LearningEngine;
import java.util.Random;

final class GameStore {
  final SharedPreferences prefs;
  final LearningEngine engine = new LearningEngine(new Random());

  GameStore(Context c) {
    prefs = c.getSharedPreferences("timesquest-local-v1", Context.MODE_PRIVATE);
    engine.restore(prefs.getString("facts", ""));
  }

  int stars() {
    return prefs.getInt("stars", 0);
  }

  @android.annotation.SuppressLint(
      "ApplySharedPref") // Persist rewards before a completion screen can be shown.
  void complete(int mode, int world) {
    if (mode < 0 || mode > 5 || world < 0 || world > 3)
      throw new IllegalArgumentException("Invalid mission");
    prefs
        .edit()
        .remove("session")
        .putInt("stars", stars() + 1)
        .putBoolean("badge-" + mode, true)
        .putInt("missions-" + world, prefs.getInt("missions-" + world, 0) + 1)
        .commit();
  }

  boolean flag(String key) {
    return prefs.getBoolean(key, false);
  }

  void flag(String key, boolean value) {
    prefs.edit().putBoolean(key, value).apply();
  }

  @android.annotation.SuppressLint(
      "ApplySharedPref") // Finish deletion before returning to the dashboard.
  void clearLearning() {
    SharedPreferences.Editor e = prefs.edit().remove("facts").remove("stars").remove("session");
    for (int i = 0; i < 6; i++) e.remove("badge-" + i);
    for (int i = 0; i < 4; i++) e.remove("missions-" + i);
    e.commit();
    for (int i = 0; i < 144; i++) engine.facts[i] = new LearningEngine.Fact();
  }
}
