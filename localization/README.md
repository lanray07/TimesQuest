# Locale release workflow

Run `python tools/localize.py extract`, `scaffold`, then `validate` from the repo root.
Non-English packs intentionally contain nulls until translation is supplied. They are not packaged in the APK or advertised as supported.

Set TIMESQUEST_TRANSLATION_URL to a trusted HTTPS adapter and TIMESQUEST_TRANSLATION_KEY in the development process only. `translate --locale fr` sends developer-authored strings, glossary and protected terms to that provider and caches the result by source hash. It never sends learning records. Translation does not approve a language.

The app now loads staged languages through LocaleRegistry and a parent language selector. GameText provides resource-based mathematical explanations, narration, billing messages, locale-aware numbers, ICU plural categories and localised voice vocabulary. UK English is the base. US English is a complete unreviewed debug preview and is excluded from release; the other ten packs still contain untranslated entries. Runtime infrastructure does not establish language quality or actual device voice support. Arabic bidirectional layout and pronunciation still require native/device review.

Review all educational and narration text with a competent speaker/educator. Store the named reviewer and actual education, narration, gameplay, voice and screenshots evidence in `localization/reviews/gameplay/<locale>.json`, using `reviews/review.example.json` as the schema. Evidence paths must resolve to nonempty files inside this repository. Both `sourceSha256` and `packSha256` must match the current source and target pack; editing either invalidates approval. `python tools/localize.py release-check --locale fr` fails if resources, matching hashes or evidence are absent and prints the current hashes. A voice-unavailable outcome may be documented as such; marketing must then omit voice answering.

After approval, `python tools/localize.py stage --locale fr` validates the pack and evidence, copies it into the app assets and registers its native label as reviewed. Rebuild and test the resulting app. Staging does not publish anything. No language, including the base English pack, currently has completed human/device release approval.

Check 40% text expansion, 200% font size, tablet and phone layouts, correct product/factor terminology, Arabic bidirectional maths, numerals, pluralisation, and all voice commands. Generate screenshots only after gameplay in that locale passes. Review English educational articles before starting translated versions.

Marketing copy and screenshot captions use the same adapter: add `--domain marketing` to scaffold, translate, validate or release-check, and use `localization/reviews/marketing/<locale>.json` for evidence. Staging applies only to gameplay. The HTTPS translation transport rejects redirects and credential-bearing URLs, bounds response size/time and validates a complete response before replacing a pack or its cache. Provider credentials remain development-only; no real provider has been contacted.
