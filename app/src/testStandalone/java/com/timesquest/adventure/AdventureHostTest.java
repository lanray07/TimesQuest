package com.timesquest.adventure;

import static org.junit.Assert.*;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.Button;
import android.widget.EditText;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.*;
import org.robolectric.shadows.ShadowAlertDialog;

/** Host Android behaviour tests. These use framework shadows, not a Fire device. */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28, qualifiers = "w800dp-h1280dp")
@LooperMode(LooperMode.Mode.PAUSED)
public class AdventureHostTest {
  ActivityController<MainActivity> controller;
  MainActivity app;

  @Before
  public void open() {
    Context c = RuntimeEnvironment.getApplication();
    c.getSharedPreferences("timesquest-local-v1", 0).edit().clear().commit();
    c.getSharedPreferences("parent-store", 0).edit().clear().commit();
    controller = Robolectric.buildActivity(MainActivity.class).setup();
    app = controller.get();
    app.store.flag("narration-off", true);
    app.store.flag("sound-off", true);
    app.store.flag("reduced-motion", true);
  }

  @After
  public void close() {
    controller.pause().stop().destroy();
  }

  void start(int mode) {
    app.mode = mode;
    app.world = 0;
    app.table = 2;
    app.startSession();
  }

  @Test
  public void allSixMissionsCompleteAndKeepFirstAnswerAccounting() {
    for (int mode = 0; mode < (BuildConfig.DEBUG ? 6 : 3); mode++) {
      start(mode);
      assertTrue("Every mode has answer, pause and learning controls", countButtons(app.root) >= 6);
      int previous = app.store.engine.attempts(2);
      app.answer(-1);
      assertTrue(app.hinted);
      app.answer(app.question.answer());
      assertEquals(previous + 1, app.store.engine.attempts(2));
      assertTrue(app.answered);
      for (int step = 1; step < 5; step++) {
        app.nextStep();
        app.answer(app.question.answer());
      }
      app.nextStep();
      assertTrue(app.store.flag("badge-" + mode));
    }
    assertEquals(BuildConfig.DEBUG ? 6 : 3, app.store.stars());
  }

  @Test
  public void sessionSurvivesActivityRecreationWithoutCountingRetry() {
    start(1);
    app.answer(-1);
    int id = app.question.id();
    int attempts = app.store.engine.attempts(2);
    controller.pause().stop().destroy();
    controller = Robolectric.buildActivity(MainActivity.class).setup();
    app = controller.get();
    assertEquals(id, app.question.id());
    assertTrue(app.hinted);
    app.answer(app.question.answer());
    assertEquals(attempts, app.store.engine.attempts(2));
  }

  @Test
  public void wrongParentAnswerCannotOpenSettings() {
    app.parentGate();
    org.robolectric.Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();
    AlertDialog gate = ShadowAlertDialog.getLatestAlertDialog();
    EditText input =
        (EditText) gate.findViewById(android.R.id.custom).findViewWithTag("parent-answer");
    // Locate the single input independently of its generated Android resource ID.
    if (input == null) input = findInput((android.view.ViewGroup) gate.getWindow().getDecorView());
    assertNotNull(input);
    input.setText("0");
    gate.getButton(AlertDialog.BUTTON_POSITIVE).performClick();
    assertFalse(app.parentVisible);
    assertNotNull(input.getError());
    assertTrue(gate.isShowing());
    gate.dismiss();
  }

  EditText findInput(android.view.ViewGroup group) {
    for (int i = 0; i < group.getChildCount(); i++) {
      android.view.View v = group.getChildAt(i);
      if (v instanceof EditText) return (EditText) v;
      if (v instanceof android.view.ViewGroup) {
        EditText found = findInput((android.view.ViewGroup) v);
        if (found != null) return found;
      }
    }
    return null;
  }

  @Test
  public void deletingLearningLeavesPreferencesAndStoreOwnershipSeparate() {
    start(0);
    app.answer(app.question.answer());
    app.store.flag("reduced-motion", true);
    app.getSharedPreferences("parent-store", 0).edit().putBoolean("owned-parent", true).commit();
    app.store.clearLearning();
    assertEquals(0, app.store.engine.attempts(2));
    assertEquals(0, app.store.stars());
    assertTrue(app.store.flag("reduced-motion"));
    assertTrue(app.getSharedPreferences("parent-store", 0).getBoolean("owned-parent", false));
  }

  @Test
  public void unavailableStoreNeverOffersPurchase() {
    assertFalse(app.billing.supported());
    assertFalse(app.billing.entitled());
  }

  @Test
  public void explicitPauseSurvivesBackgroundAndForeground() {
    start(0);
    app.pauseGame();
    controller.pause().resume();
    assertTrue("A visible pause dialog must keep the mission paused", app.paused);
    ShadowAlertDialog.getLatestAlertDialog().dismiss();
  }

  @Test
  public void completedMissionCannotAwardTwice() {
    start(0);
    for (int i = 0; i < 5; i++) {
      app.answer(app.question.answer());
      app.nextStep();
    }
    assertEquals(1, app.store.stars());
    app.nextStep();
    assertEquals(1, app.store.stars());
  }

  @Test
  public void damagedChoiceSetCannotRestore() {
    start(0);
    try {
      org.json.JSONObject s = new org.json.JSONObject(app.store.prefs.getString("session", ""));
      int answer = app.question.answer();
      s.put("choices", new org.json.JSONArray(new int[] {answer, answer, answer, answer}));
      app.store.prefs.edit().putString("session", s.toString()).commit();
      assertFalse(app.resumeSession());
    } catch (org.json.JSONException e) {
      throw new AssertionError(e);
    }
  }

  int countButtons(android.view.View view) {
    int count = view instanceof Button ? 1 : 0;
    if (view instanceof android.view.ViewGroup group)
      for (int i = 0; i < group.getChildCount(); i++) count += countButtons(group.getChildAt(i));
    return count;
  }

  void openParent() {
    app.parentGate();
    org.robolectric.Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();
    AlertDialog gate = ShadowAlertDialog.getLatestAlertDialog();
    String message = org.robolectric.Shadows.shadowOf(gate).getMessage().toString();
    java.util.regex.Matcher m =
        java.util.regex.Pattern.compile("(\\d+) \\+ (\\d+)").matcher(message);
    assertTrue("Gate has an arithmetic challenge: " + message, m.find());
    findInput((android.view.ViewGroup) gate.getWindow().getDecorView())
        .setText(String.valueOf(Integer.parseInt(m.group(1)) + Integer.parseInt(m.group(2))));
    gate.getButton(AlertDialog.BUTTON_POSITIVE).performClick();
    assertTrue(
        "Correct answer opens parent area; expiry="
            + app.parentUntil
            + ", now="
            + android.os.SystemClock.elapsedRealtime(),
        app.parentVisible);
  }

  @Test
  public void parentAccessLocksWhenAppLeavesForeground() {
    openParent();
    controller.pause().resume();
    assertFalse("Parent settings must lock after backgrounding", app.parentVisible);
  }

  @Test
  public void staleProtectedActionCannotPurchaseOrDelete() {
    openParent();
    controller.pause().resume();
    boolean[] invoked = {false};
    app.protectedAction(() -> invoked[0] = true);
    assertFalse(invoked[0]);
  }

  @Test
  public void parentAccessExpiresAfterTwoMinutes() {
    openParent();
    org.robolectric.Shadows.shadowOf(android.os.Looper.getMainLooper())
        .idleFor(java.time.Duration.ofSeconds(121));
    assertFalse(app.parentVisible);
    assertFalse(app.hasParentAccess());
  }
}
