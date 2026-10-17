package com.timesquest.adventure;

import android.app.*;
import android.content.Intent;
import android.os.Bundle;
import com.timesquest.core.LearningEngine;

/** Runs against the isolated .dev app. Does not trigger purchases, voice, or external links. */
public class QuestInstrumentation extends Instrumentation {
  @Override
  public void onCreate(Bundle args) {
    super.onCreate(args);
    start();
  }

  void check(boolean condition, String message) {
    if (!condition) throw new AssertionError(message);
  }

  @Override
  public void onStart() {
    Bundle result = new Bundle();
    Bundle status = new Bundle();
    status.putString("id", "TimesQuest");
    status.putString("class", getClass().getName());
    status.putString("test", "adventureSmoke");
    status.putInt("numtests", 1);
    status.putInt("current", 1);
    sendStatus(1, status);
    try {
      MainActivity a =
          (MainActivity)
              startActivitySync(
                  new Intent(getTargetContext(), MainActivity.class)
                      .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
      runOnMainSync(
          () -> {
            a.store.clearLearning();
            for (int mode = 0; mode < 6; mode++) {
              a.mode = mode;
              a.world = 0;
              a.table = 2;
              a.startSession();
              check(
                  a.question != null && a.choices.size() == 4,
                  "Mode must render a valid challenge");
              int before = a.store.engine.attempts(2);
              a.answer(-1);
              check(a.hinted, "Wrong answer must show explanation");
              a.answer(a.question.answer());
              check(
                  a.store.engine.attempts(2) == before + 1,
                  "Retry must not double-count first attempt");
              check(a.answered, "Correct retry completes step");
              for (int i = 0; i < 4; i++) {
                a.nextStep();
                a.answer(a.question.answer());
              }
              a.nextStep();
              check(a.store.flag("badge-" + mode), "Mode completion awards badge");
            }
            check(a.store.stars() == 6, "Six missions yield six crystals");
            a.mode = 0;
            a.table = 12;
            a.startSession();
            LearningEngine.Question q = a.question;
            a.hint();
            a.saveSession();
            check(a.resumeSession(), "Session reloads");
            check(a.question.id() == q.id() && a.hinted, "Question and hint survive reload");
            String saved = a.store.engine.serialize();
            GameStore reloaded = new GameStore(a);
            check(saved.equals(reloaded.engine.serialize()), "Learning save roundtrip");
            a.store.clearLearning();
            check(a.store.stars() == 0, "Delete clears mission progress");
            check(!a.store.flag("badge-0"), "Delete clears badges");
            a.showMap();
            check(!a.parentVisible, "Parent area requires gate");
          });
      result.putString(
          "stream",
          "PASS: six game modes, first-answer tracking, explanations, mission rewards, save reload"
              + " and deletion.\n");
      sendStatus(0, status);
      finish(Activity.RESULT_OK, result);
    } catch (Throwable e) {
      status.putString("stack", android.util.Log.getStackTraceString(e));
      sendStatus(-2, status);
      result.putString("stream", "FAIL: " + e.toString() + "\n");
      finish(Activity.RESULT_OK, result);
    }
  }
}
