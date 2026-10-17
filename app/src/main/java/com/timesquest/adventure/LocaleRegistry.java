package com.timesquest.adventure;

import android.content.Context;
import java.util.*;
import org.json.*;

/** Only bundled base or explicitly reviewed packs can enter a release language picker. */
final class LocaleRegistry {
  record Language(String tag, String label, boolean preview) {}

  static List<Language> available(Context context) {
    List<Language> result = new ArrayList<>();
    try {
      JSONArray entries =
          GameText.readJson(context, "i18n/registry.json").getJSONArray("languages");
      for (int i = 0; i < entries.length(); i++) {
        JSONObject entry = entries.getJSONObject(i);
        String tag = entry.getString("tag");
        if (!tag.matches("[a-z]{2,3}(?:-[A-Za-z0-9]{2,8})*")
            || !entry.optBoolean("reviewed", false) && !tag.equals("en-GB")) continue;
        GameText.readJson(context, "i18n/" + tag + ".json");
        result.add(new Language(tag, entry.getString("label"), false));
      }
      if (BuildConfig.DEBUG && result.stream().noneMatch(l -> l.tag().equals("en-US"))) {
        GameText.readJson(context, "i18n/en-US-preview.json");
        result.add(new Language("en-US", "English (US) · unreviewed preview", true));
      }
    } catch (Exception failure) {
      throw new IllegalStateException(
          "Language registry or bundled resources are invalid", failure);
    }
    if (result.stream().noneMatch(l -> l.tag().equals("en-GB")))
      throw new IllegalStateException("UK English base language is required");
    return Collections.unmodifiableList(result);
  }
}
