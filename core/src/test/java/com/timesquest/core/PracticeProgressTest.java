package com.timesquest.core;

import static org.junit.Assert.*;

import java.util.Random;
import org.junit.Test;

public class PracticeProgressTest {
  @Test
  public void repeatedErrorsNeverForceAnImmediateRepeat() {
    LearningEngine e = new LearningEngine(new Random(4));
    var q = new LearningEngine.Question(2, 3);
    for (int i = 0; i < 80; i++) e.record(q, 5, 900, false, 1000);
    for (int i = 0; i < 30; i++) assertNotEquals(q.id(), e.next(2, 12, 100_000, q.id()).id());
  }

  @Test
  public void progressSeparatesHintsAccuracyAndDueReviews() {
    LearningEngine e = new LearningEngine(new Random(4));
    var q = new LearningEngine.Question(7, 8);
    e.record(q, 56, 2000, true, 1000);
    e.record(new LearningEngine.Question(7, 9), 62, 4000, false, 1000);
    var p = e.progress(7, 61_000);
    assertEquals(2, p.attemptedFacts());
    assertEquals(2, p.attempts());
    assertEquals(1, p.correct());
    assertEquals(1, p.assisted());
    assertEquals(2, p.dueReviews());
    assertEquals(3000, p.averageMillis());
    assertEquals(7, e.suggestedTable(61_000));
  }

  @Test
  public void initialSuggestionIsGentleAndDataFree() {
    LearningEngine e = new LearningEngine(new Random(4));
    assertEquals(2, e.suggestedTable(0));
    assertEquals(0, e.progress(12, 0).averageMillis());
  }
}
