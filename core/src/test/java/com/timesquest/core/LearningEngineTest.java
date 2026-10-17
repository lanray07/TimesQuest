package com.timesquest.core;

import static org.junit.Assert.*;

import java.util.*;
import org.junit.Test;

public class LearningEngineTest {
  @Test
  public void allFactsAndChoices() {
    Random r = new Random(43);
    for (int a = 1; a <= 12; a++)
      for (int b = 1; b <= 12; b++) {
        var q = new LearningEngine.Question(a, b);
        assertEquals(a * b, q.answer());
        assertEquals(b * a, q.answer());
        for (int i = 0; i < 20; i++) {
          var c = q.choices(r);
          assertEquals(4, new HashSet<>(c).size());
          assertTrue(c.contains(a * b));
          assertTrue(c.stream().allMatch(x -> x > 0));
        }
        assertEquals(a * b, Integer.parseInt(q.skipCount().split(", ")[a - 1]));
      }
  }

  @Test
  public void masteryRequiresUnassistedFirstAnswers() {
    var e = new LearningEngine(new Random(1));
    var q = new LearningEngine.Question(7, 8);
    for (int i = 0; i < 3; i++) e.record(q, 56, 800, true, 1000);
    assertEquals(0, e.mastered(7));
    for (int i = 0; i < 3; i++) e.record(q, 56, 800, false, 1000);
    assertEquals(1, e.mastered(7));
    e.record(q, 55, 800, false, 1000);
    assertEquals(0, e.mastered(7));
    assertEquals(7, e.attempts(7));
  }

  @Test
  public void spacedReviewAndAdaptiveSelection() {
    var e = new LearningEngine(new Random(1));
    var q = new LearningEngine.Question(7, 8);
    e.record(q, 56, 500, false, 1000);
    assertEquals(1000 + LearningEngine.DAY, e.facts[q.id()].due);
    assertNotEquals(q.id(), e.next(7, 12, 1000, q.id()).id());
    for (int i = 0; i < 200; i++) assertEquals(7, e.next(7, 12, 1000, -1).a());
  }

  @Test
  public void savesRoundTripAndCorruptionIsAtomic() {
    var e = new LearningEngine(new Random());
    e.record(new LearningEngine.Question(12, 12), 144, 1900, false, 100);
    var fresh = new LearningEngine(new Random());
    assertTrue(fresh.restore(e.serialize()));
    assertEquals(e.serialize(), fresh.serialize());
    assertFalse(fresh.restore(e.serialize().replace("1,1,1,0", "1,2,1,0")));
    assertEquals(e.serialize(), fresh.serialize());
    assertFalse(fresh.restore("broken"));
  }

  @Test
  public void errorSpecificHints() {
    var q = new LearningEngine.Question(3, 4);
    assertTrue(q.hint(7).contains("adds"));
    assertTrue(q.hint(16).contains("one group"));
    assertTrue(q.explanation().contains("4 × 3 = 12"));
    assertEquals("4 + 4 + 4 = 12", q.repeatedAddition());
  }

  @Test
  public void invalidTablesRejected() {
    for (int n : new int[] {0, 13, -1}) {
      try {
        new LearningEngine.Question(n, 2);
        fail();
      } catch (IllegalArgumentException expected) {
      }
    }
  }

  @Test
  public void voiceNumbersAndCommands() {
    assertEquals(24, VoiceCommands.parse("My answer is twenty-four.").answer());
    assertEquals(144, VoiceCommands.parse("one hundred and forty four").answer());
    assertEquals(56, VoiceCommands.parse("my answer is fifty six").answer());
    assertEquals(100, VoiceCommands.parse("one hundred").answer());
    assertEquals(0, VoiceCommands.parse("zero").answer());
    assertEquals(VoiceCommands.Action.START, VoiceCommands.parse("Start adventure.").action());
    assertEquals(VoiceCommands.Action.REPEAT, VoiceCommands.parse("Repeat question").action());
    assertEquals(VoiceCommands.Action.HINT, VoiceCommands.parse("Give me a hint!").action());
    assertEquals(VoiceCommands.Action.NEXT, VoiceCommands.parse("Next level").action());
    assertEquals(VoiceCommands.Action.PAUSE, VoiceCommands.parse("Pause game").action());
    assertEquals(VoiceCommands.Action.PROGRESS, VoiceCommands.parse("Show my progress").action());
    assertEquals(VoiceCommands.Action.UNKNOWN, VoiceCommands.parse("buy premium").action());
    assertEquals(VoiceCommands.Action.UNKNOWN, VoiceCommands.parse("145").action());
  }
}
