package com.timesquest.adventure;

import android.content.Context;
import android.graphics.*;
import android.view.View;

/**
 * Original procedural character art with a painted backdrop; frame-capped, no per-frame bitmaps.
 */
final class SceneView extends View {
  final Paint p = new Paint(3);
  final int mode, step, colour;
  final boolean reduced;
  boolean running = true;
  float tick = 0;
  int kitchenScoops = 0;
  boolean solved = false;
  Bitmap art;
  static Bitmap sharedArt;
  final RectF target = new RectF();
  final Path curve = new Path(), horn = new Path(), wing = new Path();
  final int teal = Color.rgb(46, 154, 132), gold = Color.rgb(247, 195, 89), ink = MainActivity.INK;

  public SceneView(Context c) {
    this(c, -1, 0, 0, true);
  }

  SceneView(Context c, int m, int s, int col, boolean reduce) {
    super(c);
    mode = m;
    step = s;
    colour = col;
    reduced = reduce;
    try {
      if (sharedArt == null) {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inSampleSize = 2;
        try (java.io.InputStream stream = c.getAssets().open("art/archipelago.png")) {
          sharedArt = BitmapFactory.decodeStream(stream, null, o);
        }
      }
      art = sharedArt;
    } catch (Exception ignored) {
    }
    setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_YES);
  }

  void stop() {
    running = false;
  }

  void start() {
    running = true;
    invalidate();
  }

  void rect(Canvas c, float x, float y, float w, float h, int color, float radius) {
    p.setColor(color);
    c.drawRoundRect(x, y, x + w, y + h, radius, radius, p);
  }

  void circle(Canvas c, float x, float y, float r, int color) {
    p.setColor(color);
    c.drawCircle(x, y, r, p);
  }

  void line(Canvas c, float x, float y, float x2, float y2, int color, float stroke) {
    p.setColor(color);
    p.setStrokeWidth(stroke);
    c.drawLine(x, y, x2, y2, p);
  }

  void words(Canvas c, String text, float x, float y, int size, int color) {
    p.setColor(color);
    p.setTextSize(size);
    p.setTypeface(Typeface.create("sans-serif-rounded", Typeface.BOLD));
    c.drawText(text, x, y, p);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    canvas.save();
    float sx = getWidth() / 900f, sy = getHeight() / 300f;
    canvas.scale(sx, sy);
    rect(canvas, 0, 0, 900, 300, Color.rgb(215, 235, 218), 26);
    if (art != null) {
      p.setAlpha(mode < 0 ? 255 : 100);
      target.set(0, 0, 900, 900f * art.getHeight() / art.getWidth());
      canvas.drawBitmap(art, null, target, p);
      p.setAlpha(255);
    }
    float bob = reduced ? 0 : (float) Math.sin(tick) * 4;
    if (mode < 0) {
      for (int i = 0; i < 4; i++) {
        float x = 130 + i * 207, y = 155 + (i % 2) * 38;
        circle(canvas, x, y, 30, gold);
        circle(canvas, x, y, 23, ink);
        words(canvas, "" + (i + 1), x - 6, y + 8, 23, Color.WHITE);
        if (i < 3) {
          p.setStyle(Paint.Style.STROKE);
          p.setColor(gold);
          p.setStrokeWidth(4);
          Path path = curve;
          path.reset();
          path.moveTo(x + 30, y);
          path.cubicTo(x + 80, y + 60, x + 125, y - 60, x + 177, 155 + ((i + 1) % 2) * 38);
          canvas.drawPath(path, p);
          p.setStyle(Paint.Style.FILL);
        }
      }
      dragon(canvas, 55, 250 + bob, .7f);
    } else if (mode == 0) {
      rect(canvas, 0, 248, 900, 52, Color.rgb(79, 120, 83), 20);
      dragon(canvas, 185, 198 + bob, 1.2f);
      monster(canvas, 650, 185 - bob, step);
      for (int i = 0; i < step; i++) circle(canvas, 360 + i * 35, 145, 9, gold);
      words(canvas, "MOSSY", 610, 55, 21, ink);
    } else if (mode == 1) {
      rect(canvas, 75, 235, 750, 30, Color.rgb(110, 89, 68), 10);
      for (int i = 0; i < 5; i++) {
        float x = 140 + i * 145;
        rect(canvas, x, 140, 92, 74, Color.rgb(171, 107, 53), 13);
        rect(canvas, x, 134, 92, 24, gold, 8);
        rect(canvas, x + 38, 164, 16, 25, gold, 4);
        if (i < step) circle(canvas, x + 46, 103 + bob, 13, gold);
      }
      dragon(canvas, 55, 205, .55f);
    } else if (mode == 2) {
      for (int i = 0; i < 6; i++) {
        float x = 70 + i * 140, y = 225 - (i % 2) * 48;
        rect(canvas, x, y, 105, 25, Color.rgb(99, 127, 99), 12);
        rect(canvas, x, y - 7, 105, 12, teal, 6);
      }
      dragon(canvas, 115 + step * 140, 163 - (step % 2) * 48 + bob, .7f);
    } else if (mode == 3) {
      rect(canvas, 25, 115, 850, 140, Color.rgb(231, 188, 119), 60);
      line(canvas, 65, 183, 835, 183, Color.WHITE, 5);
      float x = 95 + step * 132;
      rect(canvas, x, 140 + bob, 120, 47, teal, 18);
      rect(canvas, x + 30, 118 + bob, 54, 37, gold, 12);
      circle(canvas, x + 24, 188, 16, ink);
      circle(canvas, x + 96, 188, 16, ink);
      line(canvas, 820, 60, 820, 250, ink, 6);
      for (int i = 0; i < 4; i++)
        for (int j = 0; j < 3; j++)
          rect(canvas, 820 + j * 16, 60 + i * 16, 16, 16, (i + j) % 2 == 0 ? ink : Color.WHITE, 0);
    } else if (mode == 4) {
      rect(canvas, 50, 213, 800, 60, Color.rgb(167, 116, 78), 12);
      for (int i = 0; i < 5; i++) {
        circle(canvas, 150 + i * 150, 185, 45, Color.rgb(250, 245, 221));
        for (int j = 0; j < 4; j++)
          circle(
              canvas, 132 + i * 150 + (j % 2) * 30, 174 + (j / 2) * 22, 7, i < step ? gold : teal);
      }
      for (int i = 0; i < kitchenScoops; i++) circle(canvas, 120 + i * 55, 94 + (int) bob, 9, gold);
    } else {
      int body =
          colour == 0 ? teal : colour == 1 ? Color.rgb(210, 110, 98) : Color.rgb(145, 123, 190);
      rect(canvas, 355, 125, 190, 120, body, 30);
      rect(canvas, 375, 45 + bob, 150, 92, body, 25);
      circle(canvas, 416, 84 + bob, 15, ink);
      circle(canvas, 485, 84 + bob, 15, ink);
      circle(canvas, 420, 80 + bob, 4, Color.WHITE);
      circle(canvas, 489, 80 + bob, 4, Color.WHITE);
      line(canvas, 450, 45 + bob, 450, 20 + bob, ink, 5);
      circle(canvas, 450, 16 + bob, 9, gold);
      if (step >= 1) rect(canvas, 320, 140, 30, 85, gold, 12);
      if (step >= 2) rect(canvas, 550, 140, 30, 85, gold, 12);
      if (step >= 3) rect(canvas, 380, 245, 40, 40, ink, 8);
      if (step >= 4) rect(canvas, 480, 245, 40, 40, ink, 8);
      circle(canvas, 450, 179, 25, gold);
      words(canvas, "×", 435, 191, 40, ink);
    }
    if (solved) {
      circle(canvas, 450, 45, 23, gold);
      words(canvas, "✓", 435, 56, 30, ink);
    }
    if (step == 5 && mode >= 0)
      for (int i = 0; i < 24; i++) {
        float x = (i * 79) % 900, y = (i * 43 + tick * 20) % 260;
        rect(canvas, x, y, 6, 12, i % 2 == 0 ? gold : teal, 2);
      }
    canvas.restore();
    if (running && !reduced && isShown()) {
      tick += .08f;
      postInvalidateDelayed(33);
    }
  }

  void dragon(Canvas c, float x, float y, float scale) {
    c.save();
    c.translate(x, y);
    c.scale(scale, scale);
    int body =
        colour == 0 ? teal : colour == 1 ? Color.rgb(210, 110, 98) : Color.rgb(145, 123, 190);
    circle(c, 0, 0, 49, body);
    circle(c, 13, -42, 43, body);
    circle(c, 13, 9, 30, Color.rgb(249, 229, 171));
    circle(c, -1, -50, 7, ink);
    circle(c, 29, -50, 7, ink);
    circle(c, 1, -53, 2, Color.WHITE);
    circle(c, 31, -53, 2, Color.WHITE);
    rect(c, -37, 33, 29, 15, body, 8);
    rect(c, 14, 33, 29, 15, body, 8);
    horn.reset();
    horn.moveTo(-9, -78);
    horn.lineTo(-9, -101);
    horn.lineTo(7, -82);
    p.setColor(gold);
    c.drawPath(horn, p);
    wing.reset();
    wing.moveTo(-32, -20);
    wing.lineTo(-74, -44);
    wing.lineTo(-59, 14);
    wing.close();
    p.setColor(body);
    c.drawPath(wing, p);
    line(c, 8, -25, 23, -25, ink, 3);
    c.restore();
  }

  void monster(Canvas c, float x, float y, int stage) {
    float shrink = 1 - stage * .07f;
    c.save();
    c.translate(x, y);
    c.scale(shrink, shrink);
    circle(c, 0, 0, 70, Color.rgb(153, 137, 187));
    circle(c, -36, -50, 30, Color.rgb(153, 137, 187));
    circle(c, 35, -50, 30, Color.rgb(153, 137, 187));
    circle(c, -22, -14, 15, Color.WHITE);
    circle(c, 22, -14, 15, Color.WHITE);
    circle(c, -20, -12, 7, ink);
    circle(c, 20, -12, 7, ink);
    rect(c, -23, 21, 46, 12, ink, 6);
    circle(c, -47, 10, 11, Color.rgb(230, 159, 157));
    circle(c, 47, 10, 11, Color.rgb(230, 159, 157));
    c.restore();
  }
}
