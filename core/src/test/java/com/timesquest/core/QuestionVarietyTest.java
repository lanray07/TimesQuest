package com.timesquest.core;

import static org.junit.Assert.*;

import java.util.*;
import org.junit.Test;

public class QuestionVarietyTest {
  @Test
  public void sameFactHasVariedPlausibleAnswerSetsAndNoFixedMagnitudeRank() {
    Random random = new Random(2048);
    var q = new LearningEngine.Question(7, 8);
    Set<Set<Integer>> sets = new HashSet<>();
    Set<Integer> ranks = new HashSet<>();
    int[] positions = new int[4];
    for (int i = 0; i < 1000; i++) {
      List<Integer> choices = q.choices(random);
      sets.add(new HashSet<>(choices));
      List<Integer> sorted = new ArrayList<>(choices);
      Collections.sort(sorted);
      ranks.add(sorted.indexOf(q.answer()));
      positions[choices.indexOf(q.answer())]++;
    }
    assertTrue("Repeated facts need different wrong-answer sets", sets.size() >= 10);
    assertEquals("Magnitude must not identify the correct answer", 4, ranks.size());
    for (int count : positions)
      assertTrue("No preferred correct button", count > 150 && count < 350);
  }

  @Test
  public void everyFactKeepsExactlyOneCorrectAnswerAndValidDistinctChoices() {
    Random random = new Random(19);
    for (int a = 1; a <= 12; a++)
      for (int b = 1; b <= 12; b++) {
        var q = new LearningEngine.Question(a, b);
        for (int i = 0; i < 100; i++) {
          List<Integer> choices = q.choices(random);
          assertEquals(4, choices.size());
          assertEquals(4, new HashSet<>(choices).size());
          assertEquals(1, Collections.frequency(choices, q.answer()));
          for (int choice : choices) assertTrue(choice >= 1 && choice <= 144);
        }
      }
  }

  @Test
  public void weakFactsDoNotTrapPracticeInATwoQuestionLoop() {
    LearningEngine e = new LearningEngine(new Random(91));
    for (int b = 1; b <= 12; b++) {
      var q = new LearningEngine.Question(2, b);
      for (int i = 0; i < 3; i++) e.record(q, b <= 2 ? 99 : q.answer(), 500, false, 1000);
    }
    Set<Integer> seen = new HashSet<>();
    int previous = -1, weak = 0;
    for (int i = 0; i < 200; i++) {
      var q = e.next(2, 12, 100000, previous);
      assertEquals(2, q.a());
      assertNotEquals(previous, q.id());
      seen.add(q.id());
      if (q.b() <= 2) weak++;
      previous = q.id();
    }
    assertTrue("Review needs variety beyond just the two weakest facts", seen.size() >= 5);
    assertTrue("Weak facts must still get extra practice", weak > 100);
  }
}
