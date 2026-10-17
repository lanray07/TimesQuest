package com.timesquest.adventure;

import android.content.Context;
import android.graphics.*;
import android.view.View;

final class ArrayView extends View {
  final int rows, cols;
  final Paint paint = new Paint(3);

  public ArrayView(Context c) {
    this(c, 3, 4, new GameText(c, "en-GB"));
  }

  ArrayView(Context c, int a, int b, GameText language) {
    super(c);
    rows = a;
    cols = b;
    setContentDescription(
        language.format(
            "array_accessibility",
            "a",
            language.number(a),
            "b",
            language.number(b),
            "answer",
            language.number(a * b)));
    setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_YES);
  }

  @Override
  protected void onDraw(Canvas c) {
    float cell = Math.min(getWidth() / (cols + 2f), getHeight() / (rows + 1f));
    float left = (getWidth() - cols * cell) / 2, top = (getHeight() - rows * cell) / 2;
    for (int y = 0; y < rows; y++)
      for (int x = 0; x < cols; x++) {
        paint.setColor(y % 2 == 0 ? MainActivity.TEAL : MainActivity.INK);
        c.drawCircle(left + (x + .5f) * cell, top + (y + .5f) * cell, cell * .3f, paint);
      }
  }
}
