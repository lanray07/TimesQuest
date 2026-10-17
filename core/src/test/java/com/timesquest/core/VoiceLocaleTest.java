package com.timesquest.core;

import static org.junit.Assert.*;

import java.util.*;
import org.junit.Test;

public class VoiceLocaleTest {
  @Test
  public void rejectsNegativeAndDecimalTranscripts() {
    assertEquals(VoiceCommands.Action.UNKNOWN, VoiceCommands.parse("-1").action());
    assertEquals(VoiceCommands.Action.UNKNOWN, VoiceCommands.parse("2.4").action());
    assertEquals(VoiceCommands.Action.UNKNOWN, VoiceCommands.parse(null).action());
  }

  @Test
  public void localizedVocabularyAndUnicodeNumbersUseSameActionContract() {
    var words = Map.of("vingt-quatre", 24);
    var commands = Map.of("répète la question", VoiceCommands.Action.REPEAT);
    assertEquals(
        24,
        VoiceCommands.parseLocalized(
                "Ma réponse est vingt-quatre!", commands, words, List.of("ma réponse est"))
            .answer());
    assertEquals(
        VoiceCommands.Action.REPEAT,
        VoiceCommands.parseLocalized("Répète la question.", commands, words, List.of()).action());
    assertEquals(24, VoiceCommands.parseLocalized("٢٤", commands, words, List.of()).answer());
  }
}
