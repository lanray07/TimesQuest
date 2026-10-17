package com.timesquest.adventure;

import android.Manifest;
import android.app.*;
import android.content.*;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.*;
import android.speech.*;
import android.speech.tts.TextToSpeech;
import android.view.*;
import android.widget.*;
import com.timesquest.core.*;
import java.util.*;
import org.json.*;

public class MainActivity extends Activity {
  static final int INK = Color.rgb(26, 62, 65),
      CREAM = Color.rgb(250, 247, 236),
      TEAL = Color.rgb(27, 105, 96);
  GameStore store;
  Billing billing;
  LinearLayout root, content;
  TextView status;
  SceneView scene;
  TextToSpeech tts;
  boolean ttsReady;
  SpeechRecognizer recognizer;
  boolean listening;
  int voiceSession = 0;
  boolean atMap = false;
  boolean backgroundSuspended = false, parentLocked = false;
  long parentUntil = 0;
  int hintKind = 0, completedMode = 0;
  boolean focusHint = false;
  String route = "map";
  android.media.MediaPlayer effect;
  int mode = -1, world = 0, table = 0, step = 0, kitchen = 0, robotColour = 0;
  boolean hinted = false, answered = false, firstRecorded = false, paused = false, timed = false;
  LearningEngine.Question question;
  List<Integer> choices;
  long started, accumulated;
  String feedback = "";
  boolean parentVisible = false;
  final Handler handler = new Handler(Looper.getMainLooper());
  final Random random = new Random();
  Locale locale = Locale.UK;
  GameText language;
  final String[] modeKeys = {"battle", "treasure", "jump", "race", "kitchen", "robot"};
  final String[] worlds = {
    "Mosslight Meadow", "Sunstone Sands", "Mooncap Grove", "Starlight Summit"
  };

  String text(String key) {
    return language.get(key);
  }

  int dp(float n) {
    return Math.round(getResources().getDisplayMetrics().density * n);
  }

  String number(int n) {
    return language.number(n);
  }

  @Override
  public void onCreate(Bundle state) {
    super.onCreate(state);
    store = new GameStore(this);
    loadLanguage();
    if (Build.VERSION.SDK_INT >= 33)
      getOnBackInvokedDispatcher()
          .registerOnBackInvokedCallback(
              android.window.OnBackInvokedDispatcher.PRIORITY_DEFAULT, this::back);
    getWindow().setStatusBarColor(INK);
    getWindow().setNavigationBarColor(INK);
    billing = new StoreBilling(this, this::onBillingChanged);
    tts =
        new TextToSpeech(
            this,
            s -> {
              if (s == TextToSpeech.SUCCESS) {
                int result = tts.setLanguage(locale);
                ttsReady = result >= 0;
                if (tts.getVoices() != null)
                  for (android.speech.tts.Voice v : tts.getVoices())
                    if (v.getLocale().getLanguage().equals("en")
                        && !v.isNetworkConnectionRequired()
                        && !(v.getFeatures() != null
                            && v.getFeatures()
                                .contains(TextToSpeech.Engine.KEY_FEATURE_NOT_INSTALLED))) {
                      tts.setVoice(v);
                      ttsReady = true;
                      if (v.getLocale().equals(locale)) break;
                    }
                tts.setSpeechRate(.85f);
              }
            });
    boolean hadMission = store.prefs.contains("session");
    if (!resumeSession()) {
      showMap();
      if (hadMission) Toast.makeText(this, text("save_recovered"), Toast.LENGTH_LONG).show();
    }
    billing.refresh();
  }

  void loadLanguage() {
    language = new GameText(this, store.prefs.getString("language", "en-GB"));
    locale = language.locale;
  }

  TextView label(String value, int size, int colour) {
    TextView v = new TextView(this);
    v.setText(value);
    v.setTextColor(colour);
    v.setTextLocale(locale);
    v.setTextDirection(View.TEXT_DIRECTION_LOCALE);
    v.setTextSize(size);
    v.setPadding(dp(4), dp(4), dp(4), dp(4));
    return v;
  }

  TextView heading(String value, int size) {
    TextView v = label(value, size, INK);
    v.setTypeface(Typeface.create("sans-serif-rounded", Typeface.BOLD));
    return v;
  }

  GradientDrawable bg(int colour, int radius) {
    GradientDrawable d = new GradientDrawable();
    d.setColor(colour);
    d.setCornerRadius(dp(radius));
    return d;
  }

  Button button(String title, boolean primary, Runnable action) {
    Button b = new Button(this);
    b.setText(title);
    b.setAllCaps(false);
    b.setTextSize(17);
    b.setTextColor(primary ? Color.WHITE : INK);
    b.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
    android.graphics.drawable.StateListDrawable backgrounds =
        new android.graphics.drawable.StateListDrawable();
    backgrounds.addState(
        new int[] {-android.R.attr.state_enabled}, bg(Color.rgb(225, 228, 218), 18));
    backgrounds.addState(
        new int[] {android.R.attr.state_pressed}, bg(primary ? INK : Color.rgb(215, 224, 210), 18));
    GradientDrawable focus = bg(primary ? TEAL : Color.rgb(234, 234, 219), 18);
    focus.setStroke(dp(3), Color.rgb(174, 116, 32));
    backgrounds.addState(new int[] {android.R.attr.state_focused}, focus);
    backgrounds.addState(new int[] {}, bg(primary ? TEAL : Color.rgb(234, 234, 219), 18));
    b.setBackground(backgrounds);
    b.setTextColor(
        new android.content.res.ColorStateList(
            new int[][] {new int[] {-android.R.attr.state_enabled}, new int[] {}},
            new int[] {Color.rgb(84, 104, 99), primary ? Color.WHITE : INK}));
    b.setMinHeight(dp(56));
    b.setPadding(dp(18), dp(10), dp(18), dp(10));
    LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, -2);
    p.setMargins(dp(4), dp(5), dp(4), dp(5));
    b.setLayoutParams(p);
    b.setOnClickListener(v -> action.run());
    return b;
  }

  LinearLayout row() {
    LinearLayout r = new LinearLayout(this);
    r.setOrientation(LinearLayout.HORIZONTAL);
    r.setGravity(Gravity.CENTER_VERTICAL);
    return r;
  }

  void cell(LinearLayout r, View v) {
    LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, -2, 1);
    p.setMargins(dp(4), dp(4), dp(4), dp(4));
    r.addView(v, p);
  }

  void screen(String subtitle) {
    cancelVoice();
    atMap = false;
    parentVisible = false;
    if (scene != null) scene.stop();
    root = new LinearLayout(this);
    root.setOrientation(LinearLayout.VERTICAL);
    root.setLayoutDirection(android.text.TextUtils.getLayoutDirectionFromLocale(locale));
    root.setBackgroundColor(CREAM);
    root.setPadding(dp(14), dp(6), dp(14), 0);
    root.setOnApplyWindowInsetsListener(
        (v, insets) -> {
          root.setPadding(
              dp(14),
              dp(6) + insets.getSystemWindowInsetTop(),
              dp(14),
              insets.getSystemWindowInsetBottom());
          return insets;
        });
    LinearLayout top = row();
    boolean compactHeader =
        getResources().getConfiguration().fontScale > 1.4f
            && getResources().getConfiguration().screenWidthDp < 600;
    TextView brand =
        heading(compactHeader ? "TimesQuest" : "✦ TimesQuest", compactHeader ? 20 : 25);
    top.addView(brand, new LinearLayout.LayoutParams(0, -2, 1));
    Button parents = button(text("parents"), false, this::parentGate);
    parents.setTextSize(14);
    parents.setMaxLines(1);
    parents.setAutoSizeTextTypeUniformWithConfiguration(
        12, 14, 1, android.util.TypedValue.COMPLEX_UNIT_SP);
    top.addView(parents, new LinearLayout.LayoutParams(dp(120), -2));
    root.addView(top);
    root.addView(label(subtitle, 14, TEAL));
    ScrollView scroll = new ScrollView(this);
    scroll.setFillViewport(true);
    content = new LinearLayout(this);
    content.setOrientation(LinearLayout.VERTICAL);
    content.setPadding(dp(3), dp(4), dp(3), dp(20));
    scroll.addView(content);
    root.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));
    setContentView(root);
  }

  boolean premium() {
    return billing.entitled() || BuildConfig.DEBUG;
  }

  boolean canPlayMode(int mode) {
    return mode >= 0 && mode < 6 && (mode < 3 || premium());
  }

  void showMap() {
    route = "map";
    parentUntil = 0;
    parentLocked = false;
    mode = -1;
    question = null;
    paused = false;
    store.prefs.edit().remove("session").apply();
    screen(text("promise"));
    atMap = true;
    LinearLayout intro = row();
    LinearLayout words = new LinearLayout(this);
    words.setOrientation(LinearLayout.VERTICAL);
    words.addView(label(text("welcome"), 14, TEAL));
    words.addView(heading(text("map_title"), 32));
    words.addView(label(text("map_story"), 17, INK));
    intro.addView(words, new LinearLayout.LayoutParams(0, -2, 1));
    intro.addView(label("✦ " + number(store.stars()), 24, TEAL));
    content.addView(intro);
    scene = new SceneView(this, -1, store.stars(), robotColour, store.flag("reduced-motion"));
    scene.setContentDescription(text("map_description"));
    content.addView(
        scene,
        new LinearLayout.LayoutParams(
            -1, dp(getResources().getConfiguration().screenWidthDp > 700 ? 280 : 210)));
    for (int i = 0; i < 4; i++) {
      final int w = i;
      boolean unlocked = i == 0 || (premium() && store.stars() >= i * 2);
      String line =
          (i + 1)
              + "  "
              + worlds[i]
              + "   "
              + (unlocked ? text("explore") : "◇ " + text("island_locked"));
      content.addView(
          button(
              line,
              i == world,
              () -> {
                if (unlocked) {
                  world = w;
                  showModes();
                } else
                  new AlertDialog.Builder(this)
                      .setTitle(worlds[w])
                      .setMessage(text("unlock_explanation"))
                      .setPositiveButton(text("ok"), null)
                      .show();
              }));
    }
    content.addView(
        button(
            text("practice_all"),
            false,
            () -> {
              table = 0;
              showTablePicker();
            }));
    content.addView(button(text("explorer"), false, this::customise));
    if (store.flag("voice")) content.addView(button(text("push_talk"), false, this::pushToTalk));
    if (BuildConfig.DEBUG) content.addView(label(text("development"), 12, TEAL));
  }

  void customise() {
    route = "customise";
    screen(text("explorer"));
    content.addView(heading(text("choose_companion"), 28));
    scene = new SceneView(this, 5, 3, robotColour, store.flag("reduced-motion"));
    content.addView(scene, new LinearLayout.LayoutParams(-1, dp(220)));
    String[] names = {text("colour_mint"), text("colour_coral"), text("colour_lavender")};
    for (int i = 0; i < 3; i++) {
      int colour = i;
      content.addView(
          button(
              names[i],
              robotColour == i,
              () -> {
                if (colour > 0 && !premium()) {
                  Toast.makeText(this, text("premium_parent"), Toast.LENGTH_LONG).show();
                  return;
                }
                robotColour = colour;
                store.prefs.edit().putInt("companion", colour).apply();
                customise();
              }));
    }
    content.addView(heading(text("badges"), 23));
    for (int i = 0; i < 6; i++)
      content.addView(label((store.flag("badge-" + i) ? "✦ " : "◇ ") + text(modeKeys[i]), 17, INK));
    content.addView(button(text("map"), true, this::showMap));
  }

  void showModes() {
    route = "modes";
    screen(worlds[world] + "  /  " + text("missions"));
    content.addView(heading(text("choose_mission"), 30));
    content.addView(label(text("island_story_" + world), 18, INK));
    for (int i = 0; i < 6; i++) {
      final int m = i;
      boolean available = canPlayMode(i);
      content.addView(
          button(
              (i + 1) + "  " + text(modeKeys[i]) + (available ? "" : "  ◇"),
              i == 0,
              () -> {
                if (available) {
                  mode = m;
                  table = 0;
                  if (m == 3) raceOptions();
                  else startSession();
                } else
                  new AlertDialog.Builder(this)
                      .setMessage(text("premium_parent"))
                      .setPositiveButton(text("ok"), null)
                      .show();
              }));
      content.addView(label(text(modeKeys[i] + "_description"), 14, INK));
    }
    content.addView(button(text("map"), false, this::showMap));
  }

  void raceOptions() {
    new AlertDialog.Builder(this)
        .setTitle(text("race"))
        .setItems(
            new String[] {text("untimed"), text("timed")},
            (d, which) -> {
              timed = which == 1;
              startSession();
            })
        .show();
  }

  void showTablePicker() {
    route = "tables";
    screen(text("practice_all"));
    content.addView(heading(text("choose_table"), 28));
    for (int a = 1; a <= 12; a += 3) {
      LinearLayout r = row();
      for (int b = a; b < a + 3; b++) {
        final int t = b;
        cell(
            r,
            button(
                "× " + number(t),
                false,
                () -> {
                  table = t;
                  mode = 0;
                  world = 0;
                  startSession();
                }));
      }
      content.addView(r);
    }
    content.addView(
        button(
            text("adaptive"),
            true,
            () -> {
              table = -1;
              mode = 0;
              startSession();
            }));
    content.addView(button(text("map"), false, this::showMap));
  }

  void startSession() {
    if (!canPlayMode(mode)) {
      showModes();
      return;
    }
    if (world > 0 && !premium()) world = 0;
    step = 0;
    robotColour = store.prefs.getInt("companion", 0);
    newQuestion(-1);
    speak(text("mission_intro_" + mode));
  }

  void newQuestion(int previous) {
    question =
        store.engine.next(
            Math.max(0, table),
            table != 0 ? 12 : new int[] {2, 5, 9, 12}[world],
            System.currentTimeMillis(),
            previous);
    choices = question.choices(random);
    hinted = false;
    hintKind = 0;
    answered = false;
    firstRecorded = false;
    paused = false;
    feedback = "";
    kitchen = 0;
    accumulated = 0;
    started = SystemClock.elapsedRealtime();
    saveSession();
    showGame();
  }

  void showGame() {
    if (question == null) return;
    route = "game";
    screen(worlds[world] + "  /  " + text(modeKeys[mode]));
    content.addView(heading(text(modeKeys[mode]), 27));
    LinearLayout progress = row();
    cell(progress, label(text("mission_step") + " " + number(step + 1) + " / 5", 16, TEAL));
    cell(progress, button(text("pause"), false, this::pauseGame));
    content.addView(progress);
    boolean wide = getResources().getConfiguration().screenWidthDp >= 840;
    LinearLayout challenge = new LinearLayout(this);
    challenge.setOrientation(LinearLayout.VERTICAL);
    LinearLayout visual = new LinearLayout(this);
    visual.setOrientation(LinearLayout.VERTICAL);
    scene = new SceneView(this, mode, step, robotColour, store.flag("reduced-motion"));
    scene.setContentDescription(text(modeKeys[mode] + "_scene"));
    scene.kitchenScoops = mode == 4 ? kitchen / question.b() : 0;
    scene.solved = answered;
    visual.addView(
        scene,
        new LinearLayout.LayoutParams(
            -1, dp(wide ? 255 : getResources().getConfiguration().fontScale > 1.4f ? 120 : 185)));
    visual.addView(
        label(
            language.format(
                "mission_context_" + mode, "a", number(question.a()), "b", number(question.b())),
            17,
            INK));
    if (wide) {
      LinearLayout panels = row();
      panels.setGravity(Gravity.TOP);
      cell(panels, visual);
      cell(panels, challenge);
      content.addView(panels);
    } else {
      content.addView(visual);
      content.addView(challenge);
    }
    TextView q = heading(number(question.a()) + " × " + number(question.b()) + " = ?", 40);
    q.setGravity(Gravity.CENTER);
    q.setMaxLines(1);
    q.setAutoSizeTextTypeUniformWithConfiguration(
        22, 40, 2, android.util.TypedValue.COMPLEX_UNIT_SP);
    q.setTextDirection(View.TEXT_DIRECTION_LTR);
    q.setContentDescription(language.question(question));
    challenge.addView(q);
    if (mode == 4 && !answered) {
      LinearLayout cooking = row();
      cell(
          cooking,
          button(
              "− " + number(question.b()),
              false,
              () -> {
                kitchen = Math.max(0, kitchen - question.b());
                showGame();
              }));
      cell(cooking, heading(number(kitchen), 27));
      cell(
          cooking,
          button(
              "+ " + number(question.b()),
              true,
              () -> {
                kitchen = Math.min(12 * question.b(), kitchen + question.b());
                showGame();
              }));
      challenge.addView(cooking);
      challenge.addView(
          label(
              language.format(
                  "count_groups",
                  "count",
                  number(kitchen / question.b()),
                  "total",
                  number(kitchen)),
              16,
              TEAL));
      challenge.addView(button(text("serve"), true, () -> answer(kitchen)));
    } else if (!answered) {
      for (int i = 0; i < 4; i += 2) {
        LinearLayout answers = row();
        for (int j = i; j < i + 2; j++) {
          int value = choices.get(j);
          String prefix = mode == 1 ? "▣ " : mode == 2 ? "▰ " : mode == 5 ? "⚙ " : "";
          Button answer = button(prefix + number(value), true, () -> answer(value));
          answer.setContentDescription(number(value));
          cell(answers, answer);
        }
        challenge.addView(answers);
      }
    }
    status = label(feedback, 18, INK);
    status.setAccessibilityLiveRegion(View.ACCESSIBILITY_LIVE_REGION_POLITE);
    challenge.addView(status);
    if (answered)
      challenge.addView(
          button(step == 4 ? text("restore_island") : text("continue"), true, this::nextStep));
    LinearLayout tools = row();
    cell(tools, button(text("hint"), false, this::hint));
    cell(tools, button(text("listen"), false, () -> speak(language.question(question))));
    challenge.addView(tools);
    if (hinted) {
      LinearLayout hints = row();
      cell(
          hints,
          button(
              text("groups_view"),
              false,
              () -> {
                hintKind = 0;
                showGame();
              }));
      cell(
          hints,
          button(
              text("turn_array"),
              false,
              () -> {
                hintKind = hintKind == 1 ? 0 : 1;
                showGame();
              }));
      challenge.addView(hints);
      int rows = hintKind == 1 ? question.b() : question.a(),
          columns = hintKind == 1 ? question.a() : question.b();
      ArrayView array = new ArrayView(this, rows, columns, language);
      challenge.addView(array, new LinearLayout.LayoutParams(-1, dp(160)));
      if (focusHint) {
        focusHint = false;
        array.post(
            () -> {
              if (array.isAttachedToWindow() && route.equals("game")) {
                android.graphics.Rect bounds = new android.graphics.Rect();
                array.getDrawingRect(bounds);
                content.offsetDescendantRectToMyCoords(array, bounds);
                ((ScrollView) content.getParent())
                    .requestChildRectangleOnScreen(content, bounds, store.flag("reduced-motion"));
              }
            });
      }

      challenge.addView(
          label(
              language.explanation(question)
                  + "\n"
                  + text("skip_count")
                  + " "
                  + language.skipCount(question),
              17,
              INK));
    }
    if (store.flag("voice")) challenge.addView(button(text("push_talk"), false, this::pushToTalk));
    if (mode == 3 && timed) challenge.addView(label(text("timer_note"), 14, TEAL));
    saveSession();
  }

  void answer(int value) {
    if (answered || paused || question == null) return;
    long elapsed = accumulated + SystemClock.elapsedRealtime() - started;
    if (!firstRecorded) {
      store.engine.record(question, value, elapsed, hinted, System.currentTimeMillis());
      firstRecorded = true;
    }
    if (value == question.answer()) {
      answered = true;
      feedback =
          text("correct")
              + (mode == 3 && timed
                  ? " " + String.format(Locale.UK, "%.1f s", elapsed / 1000.0)
                  : "");
      sound(true);
      speak(
          text("correct")
              + " "
              + language.format(
                  "math_answer",
                  "a",
                  number(question.a()),
                  "b",
                  number(question.b()),
                  "answer",
                  number(question.answer())));
    } else {
      hinted = true;
      focusHint = true;
      feedback = text("try_again") + " " + language.hint(question, value);
      sound(false);
      speak(feedback);
    }
    saveSession();
    showGame();
  }

  void hint() {
    if (question == null) return;
    hinted = true;
    focusHint = true;
    saveSession();
    showGame();
    speak(language.narratedExplanation(question));
  }

  void nextStep() {
    if (!answered || question == null || mode < 0) return;
    if (step == 4) {
      int finished = mode;
      store.complete(mode, world);
      question = null;
      mode = -1;
      answered = false;
      showCompletion(finished);
    } else {
      step++;
      newQuestion(question.id());
    }
  }

  void showCompletion(int finished) {
    completedMode = finished;
    route = "complete";
    screen(text("mission_complete"));
    scene = new SceneView(this, finished, 5, robotColour, store.flag("reduced-motion"));
    content.addView(scene, new LinearLayout.LayoutParams(-1, dp(230)));
    content.addView(heading(text("mission_complete"), 32));
    content.addView(label(text("complete_story"), 19, INK));
    content.addView(
        label(
            "✦ "
                + number(store.stars())
                + "  ·  "
                + text("badge_earned")
                + ": "
                + text(modeKeys[finished]),
            20,
            TEAL));
    content.addView(button(text("map"), true, this::showMap));
    speak(text("complete_story"));
  }

  void pauseGame() {
    if (question == null) return;
    if (!paused) {
      accumulated += SystemClock.elapsedRealtime() - started;
      paused = true;
    }
    if (tts != null) tts.stop();
    cancelVoice();
    saveSession();
    new AlertDialog.Builder(this)
        .setTitle(text("paused"))
        .setMessage(text("pause_message"))
        .setCancelable(false)
        .setPositiveButton(
            text("resume"),
            (d, w) -> {
              paused = false;
              started = SystemClock.elapsedRealtime();
              showGame();
            })
        .setNegativeButton(text("map"), (d, w) -> showMap())
        .show();
  }

  @android.annotation.SuppressLint(
      "ApplySharedPref") // First-answer state and facts must cross one durable boundary.
  void saveSession() {
    if (question == null || mode < 0) return;
    try {
      JSONObject s = new JSONObject();
      s.put("mode", mode);
      s.put("world", world);
      s.put("table", table);
      s.put("step", step);
      s.put("a", question.a());
      s.put("b", question.b());
      s.put("hinted", hinted);
      s.put("answered", answered);
      s.put("recorded", firstRecorded);
      s.put("feedback", feedback);
      s.put("kitchen", kitchen);
      s.put("timed", timed);
      s.put(
          "elapsed",
          Math.min(
              600_000,
              Math.max(0, accumulated + (paused ? 0 : SystemClock.elapsedRealtime() - started))));
      s.put("choices", new JSONArray(choices));
      store
          .prefs
          .edit()
          .putString("session", s.toString())
          .putString("facts", store.engine.serialize())
          .commit();
    } catch (JSONException ignored) {
    }
  }

  boolean resumeSession() {
    robotColour = Math.max(0, Math.min(2, store.prefs.getInt("companion", 0)));
    try {
      SessionSnapshot saved = SessionSnapshot.decode(store.prefs.getString("session", ""));
      if (!canPlayMode(saved.mode()) || saved.world() > 0 && !premium()) return false;
      mode = saved.mode();
      world = saved.world();
      table = saved.table();
      step = saved.step();
      question = saved.question();
      choices = saved.choices();
      hinted = saved.hinted();
      answered = saved.answered();
      firstRecorded = saved.recorded();
      feedback = saved.feedback();
      kitchen = saved.kitchen();
      timed = saved.timed();
      accumulated = saved.elapsed();
      started = SystemClock.elapsedRealtime();
      showGame();
      return true;
    } catch (JSONException invalid) {
      return false;
    }
  }

  boolean hasParentAccess() {
    return parentUntil > SystemClock.elapsedRealtime();
  }

  void protectedAction(Runnable action) {
    if (!parentVisible || !hasParentAccess()) {
      showParentLock();
      return;
    }
    action.run();
  }

  void freezeClock() {
    if (question != null && !paused) {
      accumulated += SystemClock.elapsedRealtime() - started;
      paused = true;
      saveSession();
    }
  }

  void resumeAdventure() {
    if (question == null || !canPlayMode(mode)) {
      showMap();
      return;
    }
    parentUntil = 0;
    parentLocked = false;
    paused = false;
    backgroundSuspended = false;
    started = SystemClock.elapsedRealtime();
    showGame();
  }

  void parentGate() {
    cancelVoice();
    boolean resumeOnCancel = question != null && !paused;
    freezeClock();
    EditText input = new EditText(this);
    input.setTag("parent-answer");
    input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
    input.setHint(text("parent_answer"));
    int a = 20 + random.nextInt(40), b = 13 + random.nextInt(25);
    Runnable cancelled =
        () -> {
          if (resumeOnCancel) {
            paused = false;
            started = SystemClock.elapsedRealtime();
          }
        };
    AlertDialog dialog =
        new AlertDialog.Builder(this)
            .setTitle(text("parent_gate"))
            .setMessage(text("parent_gate_help") + "\n" + a + " + " + b + " = ?")
            .setView(input)
            .setPositiveButton(text("continue"), null)
            .setNegativeButton(text("cancel"), (d, w) -> cancelled.run())
            .create();
    dialog.setOnCancelListener(d -> cancelled.run());
    dialog.setOnShowListener(
        d ->
            dialog
                .getButton(-1)
                .setOnClickListener(
                    v -> {
                      boolean correctGate = false;
                      try {
                        correctGate = Integer.parseInt(input.getText().toString()) == a + b;
                      } catch (NumberFormatException invalid) {
                      }
                      if (correctGate) {
                        dialog.dismiss();
                        parentLocked = false;
                        parentUntil = SystemClock.elapsedRealtime() + 120_000;
                        long ticket = parentUntil;
                        handler.postDelayed(
                            () -> {
                              if (parentUntil == ticket && parentVisible) showParentLock();
                            },
                            120_000);
                        showParent();
                      } else input.setError(text("gate_error"));
                    }));
    dialog.show();
  }

  void showParentLock() {
    parentUntil = 0;
    parentLocked = false;
    freezeClock();
    screen(text("parents"));
    route = "parent-lock";
    content.addView(heading(text("parent_locked"), 28));
    content.addView(label(text("parent_locked_help"), 17, INK));
    content.addView(button(text("parent_unlock"), true, this::parentGate));
    if (question != null)
      content.addView(button(text("resume_adventure"), false, this::resumeAdventure));
    content.addView(button(text("map"), false, this::showMap));
  }

  void showParent() {
    if (!hasParentAccess()) {
      showParentLock();
      return;
    }
    route = "parent";
    screen(text("parent_dashboard"));
    parentVisible = true;
    content.addView(heading(text("parent_dashboard"), 29));
    content.addView(label(text("progress_explanation"), 16, INK));
    for (int t = 1; t <= 12; t++) {
      int count = store.engine.attempts(t);
      content.addView(
          label(
              "× "
                  + t
                  + "     "
                  + store.engine.mastered(t)
                  + " / 12 "
                  + text("mastered")
                  + "    ·    "
                  + (count == 0
                      ? text("not_practised")
                      : store.engine.accuracy(t)
                          + "% "
                          + text("accuracy")
                          + " · "
                          + count
                          + " "
                          + text("attempts")),
              16,
              INK));
      ProgressBar bar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
      bar.setMax(12);
      bar.setProgress(store.engine.mastered(t));
      bar.setContentDescription(
          language.format(
              "progress_accessibility",
              "table",
              number(t),
              "mastered",
              number(store.engine.mastered(t))));
      content.addView(bar, new LinearLayout.LayoutParams(-1, dp(12)));
    }
    addRevision();
    content.addView(button(text("language"), false, this::chooseLanguage));
    if (question != null)
      content.addView(button(text("resume_adventure"), true, this::resumeAdventure));
    toggle(text("narration"), "narration-off", true);
    toggle(text("sounds"), "sound-off", true);
    toggle(text("reduced_motion"), "reduced-motion", false);
    content.addView(button(text("voice_settings"), false, this::voiceSettings));
    content.addView(heading(text("premium"), 23));
    content.addView(label(text("premium_details"), 16, INK));
    content.addView(
        label(billing.entitled() ? text("owned") : text(billing.statusKey()), 18, TEAL));
    if (!billing.price().isEmpty()) content.addView(label(billing.price(), 20, INK));
    Button buy =
        button(
            text("unlock"),
            true,
            () ->
                new AlertDialog.Builder(this)
                    .setTitle(text("premium"))
                    .setMessage(billing.price() + "\n" + text("one_time"))
                    .setPositiveButton(
                        text("purchase"), (d, w) -> protectedAction(billing::purchase))
                    .setNegativeButton(text("cancel"), null)
                    .show());
    buy.setEnabled(billing.canPurchase());
    content.addView(buy);
    Button restore =
        button(
            text("restore"),
            false,
            () ->
                protectedAction(
                    () -> {
                      billing.restore();
                      Toast.makeText(this, text("restore_requested"), Toast.LENGTH_LONG).show();
                    }));
    restore.setEnabled(billing.canRestore());
    content.addView(restore);
    content.addView(
        button(
            text("privacy"),
            false,
            () ->
                new AlertDialog.Builder(this)
                    .setTitle(text("privacy"))
                    .setMessage(text("privacy_detail"))
                    .setPositiveButton(text("ok"), null)
                    .show()));
    content.addView(
        button(
            text("delete"),
            false,
            () ->
                new AlertDialog.Builder(this)
                    .setTitle(text("delete"))
                    .setMessage(text("delete_confirm"))
                    .setPositiveButton(
                        text("delete"),
                        (d, w) ->
                            protectedAction(
                                () -> {
                                  store.clearLearning();
                                  question = null;
                                  mode = -1;
                                  showParent();
                                }))
                    .setNegativeButton(text("cancel"), null)
                    .show()));
    content.addView(button(text("map"), true, this::showMap));
  }

  void addRevision() {
    int table = store.engine.suggestedTable(System.currentTimeMillis());
    content.addView(heading(text("revision_title"), 23));
    content.addView(label(language.format("revision_suggestion", "table", number(table)), 16, INK));
    content.addView(
        button(
            language.format("revision_start", "table", number(table)),
            false,
            () ->
                protectedAction(
                    () -> {
                      parentUntil = 0;
                      this.table = table;
                      mode = 0;
                      world = 0;
                      startSession();
                    })));
    if (premium())
      for (int t = 1; t <= 12; t++) {
        LearningEngine.TableProgress p = store.engine.progress(t, System.currentTimeMillis());
        if (p.attempts() > 0)
          content.addView(
              label(
                  "× "
                      + number(t)
                      + ": "
                      + language.format(
                          "table_details",
                          "facts",
                          number(p.attemptedFacts()),
                          "hints",
                          number(p.assisted()),
                          "due",
                          number(p.dueReviews())),
                  14,
                  INK));
      }
  }

  void chooseLanguage() {
    protectedAction(
        () -> {
          java.util.List<LocaleRegistry.Language> available = LocaleRegistry.available(this);
          String[] labels =
              available.stream().map(LocaleRegistry.Language::label).toArray(String[]::new);
          new AlertDialog.Builder(this)
              .setTitle(text("language"))
              .setItems(
                  labels,
                  (dialog, which) ->
                      protectedAction(
                          () -> {
                            store
                                .prefs
                                .edit()
                                .putString("language", available.get(which).tag())
                                .apply();
                            loadLanguage();
                            if (tts != null) {
                              tts.stop();
                              ttsReady = false;
                              int ready = tts.setLanguage(locale);
                              ttsReady = ready >= 0;
                            }
                            showParent();
                          }))
              .setNegativeButton(text("cancel"), null)
              .show();
        });
  }

  void toggle(String title, String key, boolean inverted) {
    Switch s = new Switch(this);
    s.setText(title);
    s.setTextSize(17);
    s.setTextColor(INK);
    s.setMinHeight(dp(56));
    s.setChecked(inverted ? !store.flag(key) : store.flag(key));
    s.setOnCheckedChangeListener(
        (v, on) ->
            protectedAction(
                () -> {
                  store.flag(key, inverted ? !on : on);
                  if (key.equals("narration-off") && store.flag(key) && tts != null) tts.stop();
                  if (key.equals("sound-off") && store.flag(key)) stopSound();
                }));
    content.addView(s);
  }

  boolean voiceAvailable() {
    return Build.VERSION.SDK_INT >= 31 && SpeechRecognizer.isOnDeviceRecognitionAvailable(this);
  }

  void voiceSettings() {
    if (!hasParentAccess()) {
      showParentLock();
      return;
    }
    new AlertDialog.Builder(this)
        .setTitle(text("voice_settings"))
        .setMessage(
            text("voice_disclosure")
                + "\n\n"
                + (voiceAvailable() ? text("voice_available") : text("voice_unavailable")))
        .setPositiveButton(
            voiceAvailable() ? text("enable_voice") : text("ok"),
            (d, w) -> {
              if (hasParentAccess() && voiceAvailable()) {
                store.flag("voice", true);
                if (checkSelfPermission(Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED)
                  requestPermissions(new String[] {Manifest.permission.RECORD_AUDIO}, 4);
              }
            })
        .setNeutralButton(
            text("disable_voice"),
            (d, w) ->
                protectedAction(
                    () -> {
                      store.flag("voice", false);
                      cancelVoice();
                    }))
        .show();
  }

  void pushToTalk() {
    if (listening && recognizer != null) recognizer.stopListening();
    else beginVoice();
  }

  void beginVoice() {
    if (listening || !store.flag("voice")) return;
    if (Build.VERSION.SDK_INT < 31
        || !voiceAvailable()
        || checkSelfPermission(Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED) {
      Toast.makeText(this, text("voice_unavailable"), Toast.LENGTH_LONG).show();
      return;
    }
    try {
      final int voiceTicket = ++voiceSession;
      if (tts != null) tts.stop();
      if (recognizer == null) recognizer = SpeechRecognizer.createOnDeviceSpeechRecognizer(this);
      recognizer.setRecognitionListener(
          new RecognitionListener() {
            public void onReadyForSpeech(Bundle b) {
              if (voiceTicket != voiceSession) return;
              listening = true;
              if (status != null) status.setText(text("listening"));
            }

            public void onBeginningOfSpeech() {}

            public void onRmsChanged(float f) {}

            public void onBufferReceived(byte[] b) {}

            public void onEndOfSpeech() {}

            public void onError(int e) {
              if (voiceTicket != voiceSession) return;
              listening = false;
              Toast.makeText(
                      MainActivity.this, text("voice_error") + " (" + e + ")", Toast.LENGTH_LONG)
                  .show();
            }

            public void onResults(Bundle b) {
              if (voiceTicket != voiceSession) return;
              listening = false;
              ArrayList<String> words = b.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
              if (words != null && !words.isEmpty()) handleCommand(language.command(words.get(0)));
            }

            public void onPartialResults(Bundle b) {}

            public void onEvent(int e, Bundle b) {}
          });
      Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
      i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
      i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, locale.toLanguageTag());
      i.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false);
      recognizer.startListening(i);
      listening = true;
      handler.postDelayed(
          () -> {
            if (voiceTicket == voiceSession && listening) cancelVoice();
          },
          8000);
    } catch (Exception e) {
      cancelVoice();
      Toast.makeText(this, text("voice_error"), Toast.LENGTH_LONG).show();
    }
  }

  void handleCommand(VoiceCommands.Command command) {
    switch (command.action()) {
      case START -> {
        if (question == null) {
          mode = 0;
          startSession();
        }
      }
      case REPEAT -> {
        if (question != null) speak(language.question(question));
      }
      case HINT -> hint();
      case NEXT -> {
        if (question != null && answered) nextStep();
      }
      case PAUSE -> pauseGame();
      case PROGRESS -> parentGate();
      case ANSWER -> answer(command.answer());
      default -> Toast.makeText(this, text("voice_error"), Toast.LENGTH_SHORT).show();
    }
  }

  void cancelVoice() {
    voiceSession++;
    listening = false;
    if (recognizer != null)
      try {
        recognizer.cancel();
      } catch (RuntimeException unavailable) {
      }
  }

  void onBillingChanged() {
    if (parentVisible) showParent();
    else if (question != null && !canPlayMode(mode)) {
      freezeClock();
      showMap();
    }
  }

  void speak(String words) {
    if (store.flag("narration-off")) return;
    if (tts == null || !ttsReady) {
      Toast.makeText(this, text("tts_initializing"), Toast.LENGTH_SHORT).show();
      return;
    }
    android.speech.tts.Voice voice = tts.getVoice();
    if (voice == null
        || voice.isNetworkConnectionRequired()
        || voice.getFeatures() != null
            && voice.getFeatures().contains(TextToSpeech.Engine.KEY_FEATURE_NOT_INSTALLED)) {
      Toast.makeText(this, text("tts_unavailable"), Toast.LENGTH_SHORT).show();
      return;
    }
    if (tts.speak(words, TextToSpeech.QUEUE_FLUSH, null, "quest") == TextToSpeech.ERROR)
      Toast.makeText(this, text("tts_unavailable"), Toast.LENGTH_SHORT).show();
  }

  @Override
  public void onRequestPermissionsResult(int code, String[] permissions, int[] granted) {
    super.onRequestPermissionsResult(code, permissions, granted);
    if (code == 4 && (granted.length == 0 || granted[0] != PackageManager.PERMISSION_GRANTED)) {
      store.flag("voice", false);
      cancelVoice();
    }
  }

  void stopSound() {
    if (effect != null) {
      effect.release();
      effect = null;
    }
  }

  void sound(boolean good) {
    if (store.flag("sound-off")) return;
    stopSound();
    try {
      effect = android.media.MediaPlayer.create(this, good ? R.raw.chime : R.raw.soft_note);
      if (effect != null) {
        effect.setOnCompletionListener(
            p -> {
              p.release();
              if (effect == p) effect = null;
            });
        effect.start();
      }
    } catch (RuntimeException unavailable) {
      stopSound();
    }
  }

  @Override
  protected void onPause() {
    super.onPause();
    stopSound();
    backgroundSuspended = question != null && !paused;
    if (backgroundSuspended) freezeClock();
    if (parentVisible) {
      parentLocked = true;
      parentVisible = false;
      parentUntil = 0;
    }
    cancelVoice();
    if (tts != null) tts.stop();
    if (scene != null) scene.stop();
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (parentLocked) {
      showParentLock();
      return;
    }
    if (question != null && backgroundSuspended) {
      paused = false;
      started = SystemClock.elapsedRealtime();
      backgroundSuspended = false;
    }
    if (scene != null) scene.start();
  }

  @Override
  public void onConfigurationChanged(android.content.res.Configuration config) {
    super.onConfigurationChanged(config);
    switch (route) {
      case "game" -> showGame();
      case "modes" -> showModes();
      case "tables" -> showTablePicker();
      case "customise" -> customise();
      case "parent" -> showParent();
      case "parent-lock" -> showParentLock();
      case "complete" -> showCompletion(completedMode);
      default -> showMap();
    }
  }

  @Override
  protected void onDestroy() {
    stopSound();
    if (tts != null) tts.shutdown();
    if (recognizer != null) recognizer.destroy();
    handler.removeCallbacksAndMessages(null);
    super.onDestroy();
  }

  void back() {
    if (atMap) finish();
    else if (question != null && !parentVisible) pauseGame();
    else showMap();
  }

  @android.annotation.SuppressLint(
      "GestureBackNavigation") // API 33+ uses the dispatcher registered in onCreate.
  @Override
  public void onBackPressed() {
    back();
  }
}
