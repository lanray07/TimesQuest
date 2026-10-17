package com.timesquest.adventure;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/** Physical-device diagnostics for the isolated .dev app; never initiates checkout or microphone capture. */
public class DeviceChecksInstrumentation extends Instrumentation {
  final StringBuilder evidence = new StringBuilder();

  void require(boolean ok, String message) {
    if (!ok) throw new AssertionError(message);
    evidence.append("PASS: ").append(message).append('\n');
  }

  int checkTargets(View view, float density) {
    int count = 0;
    if (view instanceof Button && view.getVisibility() == View.VISIBLE) {
      require(view.getHeight() / density >= 48, "button >=48dp: " + ((Button) view).getText());
      count++;
    }
    if (view instanceof ViewGroup) {
      ViewGroup group = (ViewGroup) view;
      for (int i = 0; i < group.getChildCount(); i++)
        count += checkTargets(group.getChildAt(i), density);
    }
    return count;
  }

  @Override
  public void onStart() {
    Bundle result = new Bundle();
    try {
      require(getTargetContext().getPackageName().endsWith(".dev"), "isolated debug target");
      MainActivity activity =
          (MainActivity)
              startActivitySync(
                  new Intent(getTargetContext(), MainActivity.class)
                      .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
      for (int i = 0; i < 100 && !activity.ttsReady; i++) Thread.sleep(100);
      require(activity.ttsReady, "TTS initialized on device");
      android.speech.tts.Voice voice = activity.tts.getVoice();
      require(
          voice != null && !voice.isNetworkConnectionRequired(),
          "installed offline TTS voice selected");
      evidence
          .append("engine=")
          .append(activity.tts.getDefaultEngine())
          .append(" voice=")
          .append(voice.getName())
          .append(" locale=")
          .append(voice.getLocale())
          .append('\n');
      File wav =
          new File(getTargetContext().getExternalFilesDir(null), "timesquest-tts-diagnostic.wav");
      CountDownLatch synthesized = new CountDownLatch(1);
      AtomicInteger synthesisResult = new AtomicInteger(-1);
      activity.tts.setOnUtteranceProgressListener(
          new UtteranceProgressListener() {
            @Override
            public void onStart(String id) {}

            @Override
            public void onDone(String id) {
              synthesisResult.set(0);
              synthesized.countDown();
            }

            @Override
            public void onError(String id) {
              synthesisResult.set(-2);
              synthesized.countDown();
            }
          });
      require(
          activity.tts.synthesizeToFile(
                  "Three times five. Three equal groups of five make fifteen. Five plus five plus"
                      + " five equals fifteen.",
                  new Bundle(),
                  wav,
                  "device-check")
              != TextToSpeech.ERROR,
          "question and hint synthesis accepted");
      require(
          synthesized.await(20, TimeUnit.SECONDS)
              && synthesisResult.get() == 0
              && wav.length() > 44,
          "offline question/hint synthesis completed with audio file");
      evidence.append("wavBytes=").append(wav.length()).append('\n');
      runOnMainSync(
          () -> {
            activity.mode = 0;
            activity.world = 0;
            activity.table = 3;
            activity.startSession();
          });
      waitForIdleSync();
      Thread.sleep(300);
      runOnMainSync(
          () -> {
            require(
                checkTargets(activity.root, activity.getResources().getDisplayMetrics().density)
                    >= 7,
                "mission touch controls measured");
            require(
                activity.scene.getContentDescription() != null
                    && activity.scene.getContentDescription().length() > 0,
                "game scene has spoken description");
            require(
                activity.root.getResources().getConfiguration().fontScale >= 1,
                "font scale recorded");
            evidence
                .append("fontScale=")
                .append(activity.getResources().getConfiguration().fontScale)
                .append(" widthDp=")
                .append(activity.getResources().getConfiguration().screenWidthDp)
                .append('\n');
            require(
                !activity.voiceAvailable(), "unsupported Fire recognition keeps touch fallback");
            activity.store.flag("reduced-motion", true);
            activity.showGame();
            require(activity.scene.reduced, "reduced-motion setting reaches scene");
            StringBuilder longSpeech = new StringBuilder();
            for (int i = 0; i < 30; i++)
              longSpeech.append("Three equal groups of five make fifteen. ");
            activity.speak(longSpeech.toString());
          });
      boolean speaking = false;
      for (int i = 0; i < 30 && !speaking; i++) {
        Thread.sleep(100);
        speaking = activity.tts.isSpeaking();
      }
      require(speaking, "device narration starts");
      runOnMainSync(() -> activity.moveTaskToBack(true));
      for (int i = 0; i < 30 && activity.tts.isSpeaking(); i++) Thread.sleep(100);
      require(!activity.tts.isSpeaking(), "narration stops on real task background");
      runOnMainSync(
          () -> {
            require(!activity.scene.running, "background stops scene animation");
          });
      result.putString(
          "stream",
          evidence + "PASS: physical-device speech/accessibility/lifecycle diagnostics.\n");
    } catch (Throwable failure) {
      result.putString(
          "stream", evidence + "FAIL: " + android.util.Log.getStackTraceString(failure));
    }
    finish(Activity.RESULT_OK, result);
  }
}
