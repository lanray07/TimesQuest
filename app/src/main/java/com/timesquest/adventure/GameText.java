package com.timesquest.adventure;

import android.content.Context;
import com.timesquest.core.LearningEngine;
import com.timesquest.core.VoiceCommands;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.*;
import org.json.*;

/** One catalogue for interface text, mathematical narration and voice vocabulary. */
final class GameText {
  final JSONObject strings;
  final Locale locale;

  GameText(Context context, String requested) {
    LocaleRegistry.Language selected =
        LocaleRegistry.available(context).stream()
            .filter(value -> value.tag().equals(requested))
            .findFirst()
            .orElse(new LocaleRegistry.Language("en-GB", "English (UK)", false));
    String tag = selected.tag();
    locale = Locale.forLanguageTag(tag);
    try {
      strings = readJson(context, "i18n/" + (selected.preview() ? "en-US-preview" : tag) + ".json");
    } catch (IOException | JSONException e) {
      throw new IllegalStateException("Required packaged language is missing", e);
    }
  }

  static JSONObject readJson(Context context, String path) throws IOException, JSONException {
    try (InputStream in = context.getAssets().open(path);
        ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      byte[] buffer = new byte[4096];
      int read;
      while ((read = in.read(buffer)) != -1) out.write(buffer, 0, read);
      return new JSONObject(new String(out.toByteArray(), StandardCharsets.UTF_8));
    }
  }

  String pluralKey(String base, int count) {
    String category = android.icu.text.PluralRules.forLocale(locale).select(count);
    String key = base + "_" + category;
    return strings.has(key) ? key : base + "_other";
  }

  String get(String key) {
    try {
      return strings.getString(key);
    } catch (JSONException e) {
      throw new IllegalArgumentException("Missing language key: " + key, e);
    }
  }

  String number(long value) {
    return NumberFormat.getIntegerInstance(locale).format(value);
  }

  String format(String key, Object... pairs) {
    String result = get(key);
    for (int i = 0; i < pairs.length; i += 2)
      result = result.replace("{" + pairs[i] + "}", String.valueOf(pairs[i + 1]));
    return result;
  }

  String question(LearningEngine.Question q) {
    return format("math_question", "a", number(q.a()), "b", number(q.b()));
  }

  String explanation(LearningEngine.Question q) {
    List<String> addition = new ArrayList<>();
    for (int i = 0; i < q.a(); i++) addition.add(number(q.b()));
    return format(
            pluralKey("math_groups", q.a()),
            "a",
            number(q.a()),
            "b",
            number(q.b()),
            "answer",
            number(q.answer()),
            "groups",
            get(q.a() == 1 ? "group_one" : "group_many"))
        + "\n"
        + String.join(" + ", addition)
        + " = "
        + number(q.answer())
        + "\n"
        + format("math_swap", "a", number(q.a()), "b", number(q.b()), "answer", number(q.answer()))
        + "\n"
        + format(
            "math_family", "a", number(q.a()), "b", number(q.b()), "answer", number(q.answer()));
  }

  String skipCount(LearningEngine.Question q) {
    List<String> values = new ArrayList<>();
    for (int i = 1; i <= q.a(); i++) values.add(number(i * q.b()));
    return String.join(get("number_separator"), values);
  }

  String hint(LearningEngine.Question q, int given) {
    if (given == q.a() + q.b() && given != q.answer())
      return get("math_added_factors") + " " + explanation(q);
    if (Math.abs((long) given - q.answer()) == q.a()
        || Math.abs((long) given - q.answer()) == q.b())
      return format("math_one_group", "b", number(q.b()), "count", skipCount(q));
    return explanation(q);
  }

  String narratedExplanation(LearningEngine.Question q) {
    return format(
            pluralKey("math_narrated_groups", q.a()),
            "a",
            number(q.a()),
            "b",
            number(q.b()),
            "answer",
            number(q.answer()),
            "count",
            skipCount(q))
        + " "
        + format(
            "math_narrated_swap",
            "a",
            number(q.a()),
            "b",
            number(q.b()),
            "answer",
            number(q.answer()));
  }

  VoiceCommands.Command command(String spoken) {
    if (locale.getLanguage().equals("en")) return VoiceCommands.parse(spoken);
    Map<String, VoiceCommands.Action> commands = new HashMap<>();
    for (VoiceCommands.Action action : VoiceCommands.Action.values())
      if (action != VoiceCommands.Action.ANSWER && action != VoiceCommands.Action.UNKNOWN)
        commands.put(get("voice_command_" + action.name().toLowerCase(Locale.ROOT)), action);
    Map<String, Integer> numbers = new HashMap<>();
    for (int n = 0; n <= 144; n++)
      for (String word : get("voice_number_" + n).split("\\|")) numbers.put(word, n);
    return VoiceCommands.parseLocalized(
        spoken, commands, numbers, Arrays.asList(get("voice_answer_prefixes").split("\\|")));
  }
}
