# Marketing deliverables and approval boundaries

UK store copy is a draft and must reflect final tested behaviour. The localisation catalogue includes draft title/summary translations for all target locales, but these are **not** approved listings or language support claims.

The screenshot manifest uses the Amazon specification checked on 17 October 2026. Tablet exports are planned at 1920×1200 and Android phone exports at 720×1280; capture the actual layouts rather than resizing one to impersonate the other. Amazon accepts 3–10 screenshots. The 16 SVG sources are visibly marked composition blueprints because no device capture was available. They are not submission assets. Do not upload them.

After device QA and human locale approval, add the locale to the screenshot manifest and use `tools/capture_gameplay.py` to capture each real screen with a QA evidence record. Keep the native raw images. Marketing compositions may add accurate headlines with enough space for text expansion. Review Arabic direction and Hindi glyph coverage. Omit shot 6's answering claim on devices without tested recognition.

Store icon geometry is `marketing/icons/icon.svg`, with the original Android vector preserved as `marketing/icons/launcher-legacy.xml`. Android now uses adaptive launcher resources under `app/src/main/res/mipmap-anydpi/` with foreground and monochrome drawable geometry. The deterministic icon export tool produces original 114px and 512px raster assets. Optional promotional artwork has not been exported; it must be 1024×500 under the current Amazon guidance and should not be a screenshot.

No app name has been reserved and no public store page, domain, support address or publisher identity has been invented.

Four authentic physical KFTRWI Fire captures (1920×1200) are now saved under `screenshots/captures/en-GB/` and uploaded to the owner-requested Amazon draft. Evidence and outstanding human review are in `docs/fire-device-qa-2026-10-17.md`. They are draft assets, not a full locale-review approval; `approvedLocales` remains unchanged.
