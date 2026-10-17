package com.timesquest.adventure;

import static org.junit.Assert.*;

import com.timesquest.core.LearningEngine;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class LanguageHostTest {
  @Test
  public void unknownAndPathLikeLocaleNamesCannotLoadUnapprovedResources() {
    var c = RuntimeEnvironment.getApplication();
    assertEquals("en-GB", new GameText(c, "../../other").locale.toLanguageTag());
    assertEquals("en-GB", new GameText(c, "ar").locale.toLanguageTag());
  }

  @Test
  public void unreviewedUsPreviewNeverLeaksIntoRelease() {
    var text = new GameText(RuntimeEnvironment.getApplication(), "en-US");
    assertEquals(BuildConfig.DEBUG ? "en-US" : "en-GB", text.locale.toLanguageTag());
    assertEquals(
        BuildConfig.DEBUG,
        LocaleRegistry.available(RuntimeEnvironment.getApplication()).stream()
            .anyMatch(l -> l.preview()));
  }

  @Test
  public void mathsUsesCataloguedGroupsAndCorrectFactFamily() {
    var text = new GameText(RuntimeEnvironment.getApplication(), "en-GB");
    assertTrue(
        text.explanation(new LearningEngine.Question(1, 4))
            .startsWith("1 equal group of 4 makes 4."));
    String explanation = text.explanation(new LearningEngine.Question(7, 8));
    assertTrue(explanation.contains("8 × 7 = 56"));
    assertTrue(explanation.contains("56 ÷ 8 = 7"));
    assertTrue(
        text.narratedExplanation(new LearningEngine.Question(7, 8))
            .contains("8 times 7 is also 56"));
  }
}
