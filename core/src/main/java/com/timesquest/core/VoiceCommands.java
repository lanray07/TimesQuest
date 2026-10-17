package com.timesquest.core;

import java.util.*;

public final class VoiceCommands {
  private VoiceCommands() {}

  public enum Action {
    START,
    REPEAT,
    HINT,
    NEXT,
    PAUSE,
    PROGRESS,
    ANSWER,
    UNKNOWN
  }

  public record Command(Action action, int answer) {}

  private static String normalize(String text) {
    return text.trim()
        .toLowerCase(Locale.ROOT)
        .replaceAll("[.,!?]+$", "")
        .replaceAll("(?<=\\p{L})[-–](?=\\p{L})", " ")
        .trim()
        .replaceAll("\\s+", " ");
  }

  public static Command parseLocalized(
      String text,
      Map<String, Action> commands,
      Map<String, Integer> numbers,
      List<String> prefixes) {
    if (text == null) return new Command(Action.UNKNOWN, -1);
    String s = normalize(text);
    for (var entry : commands.entrySet())
      if (s.equals(normalize(entry.getKey()))) return new Command(entry.getValue(), -1);
    for (String prefix : prefixes) {
      String p = normalize(prefix) + " ";
      if (s.startsWith(p)) {
        s = s.substring(p.length());
        break;
      }
    }
    try {
      int n = Integer.parseInt(s);
      return new Command(n >= 0 && n <= 144 ? Action.ANSWER : Action.UNKNOWN, n);
    } catch (NumberFormatException ignored) {
    }
    for (var entry : numbers.entrySet())
      if (s.equals(normalize(entry.getKey())) && entry.getValue() >= 0 && entry.getValue() <= 144)
        return new Command(Action.ANSWER, entry.getValue());
    return new Command(Action.UNKNOWN, -1);
  }

  public static Command parse(String text) {
    if (text == null) return new Command(Action.UNKNOWN, -1);
    String s = normalize(text);
    Action a =
        switch (s) {
          case "start adventure" -> Action.START;
          case "repeat question" -> Action.REPEAT;
          case "give me a hint" -> Action.HINT;
          case "next level" -> Action.NEXT;
          case "pause game" -> Action.PAUSE;
          case "show my progress" -> Action.PROGRESS;
          default -> Action.UNKNOWN;
        };
    if (a != Action.UNKNOWN) return new Command(a, -1);
    s = s.replaceFirst("^(my answer is|the answer is|answer) ", "");
    try {
      int n = Integer.parseInt(s);
      return new Command(n >= 0 && n <= 144 ? Action.ANSWER : Action.UNKNOWN, n);
    } catch (NumberFormatException ignored) {
    }
    String[] ones = {
      "zero",
      "one",
      "two",
      "three",
      "four",
      "five",
      "six",
      "seven",
      "eight",
      "nine",
      "ten",
      "eleven",
      "twelve",
      "thirteen",
      "fourteen",
      "fifteen",
      "sixteen",
      "seventeen",
      "eighteen",
      "nineteen"
    };
    String[] tens = {
      "", "", "twenty", "thirty", "forty", "fifty", "sixty", "seventy", "eighty", "ninety"
    };
    for (int n = 0; n <= 144; n++) {
      int rest = n % 100;
      String words =
          rest < 20 ? ones[rest] : tens[rest / 10] + (rest % 10 == 0 ? "" : " " + ones[rest % 10]);
      if (n >= 100) words = "one hundred" + (rest == 0 ? "" : " " + words);
      if (s.replace(" and ", " ").equals(words)) return new Command(Action.ANSWER, n);
    }
    return new Command(Action.UNKNOWN, -1);
  }
}
