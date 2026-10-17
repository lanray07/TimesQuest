package com.timesquest.adventure;

import static org.junit.Assert.*;

import android.graphics.*;
import android.view.*;
import android.widget.Button;
import java.io.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.*;

/** Internal Skia renders of real Android views. Never represent these as tested device captures. */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 35, qualifiers = "w1280dp-h800dp-land-mdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@LooperMode(LooperMode.Mode.PAUSED)
public class HostLayoutTest {
  @Test
  @Config(qualifiers = "w360dp-h800dp-port-mdpi")
  public void largeTextRemainsScrollableWithLargeControls() throws Exception {
    RuntimeEnvironment.setFontScale(2.0f);
    var context = RuntimeEnvironment.getApplication();
    context
        .getSharedPreferences("timesquest-local-v1", 0)
        .edit()
        .clear()
        .putBoolean("reduced-motion", true)
        .putBoolean("narration-off", true)
        .putBoolean("sound-off", true)
        .commit();
    try (var controller = Robolectric.buildActivity(MainActivity.class).setup()) {
      MainActivity app = controller.get();
      app.mode = 0;
      app.world = 0;
      app.table = 12;
      app.startSession();
      render(app, "large-text-phone", 360, 800);
      assertLargeButtons(app.root);
    } finally {
      RuntimeEnvironment.setFontScale(1.0f);
    }
  }

  @Test
  @Config(qualifiers = "w360dp-h800dp-port-mdpi")
  public void phoneLayoutKeepsControlsAccessible() throws Exception {
    var context = RuntimeEnvironment.getApplication();
    context
        .getSharedPreferences("timesquest-local-v1", 0)
        .edit()
        .clear()
        .putBoolean("reduced-motion", true)
        .putBoolean("narration-off", true)
        .putBoolean("sound-off", true)
        .commit();
    try (var controller = Robolectric.buildActivity(MainActivity.class).setup()) {
      MainActivity app = controller.get();
      app.mode = 4;
      app.world = 0;
      app.table = 12;
      app.startSession();
      render(app, "kitchen-phone", 360, 800);
      assertLargeButtons(app.root);
      app.mode = 0;
      app.startSession();
      app.hint();
      render(app, "hint-phone", 360, 800);
      assertLargeButtons(app.root);
    }
  }

  @Test
  public void tabletLayoutsRenderAndKeepLargeTouchTargets() throws Exception {
    var context = RuntimeEnvironment.getApplication();
    context
        .getSharedPreferences("timesquest-local-v1", 0)
        .edit()
        .clear()
        .putBoolean("reduced-motion", true)
        .putBoolean("narration-off", true)
        .putBoolean("sound-off", true)
        .commit();
    try (var controller = Robolectric.buildActivity(MainActivity.class).setup()) {
      MainActivity app = controller.get();
      render(app, "map-tablet", 1280, 800);
      for (int mode = 0; mode < 6; mode++) {
        app.mode = mode;
        app.world = 0;
        app.table = 7;
        app.startSession();
        render(app, "mode-" + mode + "-tablet", 1280, 800);
        assertLargeButtons(app.root);
      }
      app.hint();
      render(app, "visual-hint-tablet", 1280, 800);
    }
  }

  void render(MainActivity app, String name, int width, int height) throws IOException {
    app.root.measure(
        View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
        View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY));
    app.root.layout(0, 0, width, height);
    org.robolectric.Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();
    Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(bitmap);
    app.root.draw(canvas);
    Paint stamp = new Paint(Paint.ANTI_ALIAS_FLAG);
    stamp.setColor(Color.rgb(26, 62, 65));
    canvas.drawRect(0, height - 30, width, height, stamp);
    stamp.setColor(Color.WHITE);
    stamp.setTextSize(14);
    canvas.drawText("HOST QA · not a device/store screenshot", 14, height - 9, stamp);
    File folder =
        new File(System.getProperty("user.dir"), "../artifacts/host-previews").getCanonicalFile();
    if (!folder.exists() && !folder.mkdirs()) throw new IOException("Cannot create QA output");
    try (FileOutputStream output = new FileOutputStream(new File(folder, name + ".png"))) {
      assertTrue(bitmap.compress(Bitmap.CompressFormat.PNG, 100, output));
    }
    bitmap.recycle();
  }

  void assertLargeButtons(View view) {
    if (view instanceof Button && view.getVisibility() == View.VISIBLE)
      assertTrue(
          "Small touch target: " + ((Button) view).getText(), view.getMeasuredHeight() >= 48);
    if (view instanceof ViewGroup group)
      for (int i = 0; i < group.getChildCount(); i++) assertLargeButtons(group.getChildAt(i));
  }
}
