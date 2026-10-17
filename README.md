# TimesQuest – Multiplication Adventure

Native Android educational adventure for children around 6–11, with an independent static marketing site. **Local polishing and host verification are complete; production submission still requires owner inputs and real-device/store validation.** Read [submission readiness](docs/submission-readiness.md), [polishing notes](docs/polish-release-notes.md) and [exactly what is needed from the owner](docs/needed-from-owner.md).

Implemented:

- Shared 1–12 multiplication engine: 144 facts, unique choices, equal groups, arrays, repeated addition, skip counting, fact families, mistake-specific hints, adaptive selection and spaced review.
- Four connected islands; five-step missions, crystals, unlock thresholds, local badges, companion colours and persistent sessions.
- Six mission types: friendly battles, treasure locks, stepping-stone jumps, racing, equal-scoop recipes and robot assembly.
- Parent dashboard, deletion, sound/narration/reduced-motion settings and protected purchase access. No child accounts, adverts, chat or child-facing external links. Learning records remain local; Amazon SDK store telemetry is disclosed separately in the privacy notice.
- Android offline TextToSpeech and optional parent-enabled on-device push-to-talk with complete touch fallback. Hardware speech behaviour remains unverified.
- Real Amazon SDK integration for product prices, non-consumable receipts and paginated purchase restoration. Store access stays unavailable until the app-specific public key and product are configured. No purchases were made.
- Original generated world artwork, procedural character animation, original vector icon and synthesised sound effects.
- Translation adapter, caching, glossary, protected terms, placeholder checks and review gates. UK English is implemented. US English and multilingual marketing summaries are unreviewed drafts; ten further gameplay packs are incomplete scaffolds.
- Twenty static website pages, including one substantive 1–12 table guide and eight educational article drafts, with crawlable HTML, structured data, sitemap and robots controls.

## Build and run

Requirements: Java 17, Android SDK platform 36, Gradle wrapper (included). Set `ANDROID_HOME` or an ignored `local.properties` containing your SDK location.

```powershell
.\gradlew.bat :core:test :app:assembleStandaloneDebug
.\gradlew.bat :app:assembleAmazonDebug :app:assembleAmazonRelease
.\gradlew.bat :app:lintStandaloneDebug :app:lintAmazonDebug
```

Install the standalone debug APK on a connected compatible test device:

```powershell
adb install -r app/build/outputs/apk/standalone/debug/app-standalone-debug.apk
```

Debug builds use application ID `com.timesquest.adventure.dev`, visibly identify development access and expose all six modes for testing. Release builds use `com.timesquest.adventure`; premium modes require a real Amazon entitlement. The Amazon release is now signed with a dedicated TimesQuest key and accepted in the existing Amazon draft, with 12 compatible Fire tablet profiles selected. It has not been submitted. See [signing handover](docs/release-signing.md) and [exact signed artifact](docs/signed-release.json). The standalone flavour has no purchase provider and no INTERNET permission; its release output remains unsigned.

## Checks

```powershell
python -m unittest discover -s tools -p test_*.py -v
python tools/localize.py extract
python tools/localize.py validate --locale en-GB
python tools/verify_metadata.py
cd website
npm run build
npm test
npm run dev
```

The latest checks passed: 66 JVM test executions across core, standalone debug/release and Amazon host configurations, 11 Python tests and 9 website tests. Both Android lint checks report no issues. Host Android/Skia renders cover tablet, phone and 200% text layouts. Physical Fire compatibility, actual speech, real Amazon transactions, performance and offline device behaviour still require a test target; host tests do not certify them.

Run `python tools/verify.py` for credential-free verification, `python tools/delivery_report.py` for artifact hashes, and `python tools/release_audit.py` for the remaining blockers. The audit intentionally exits non-zero while required inputs are absent. `python tools/package_review.py` creates a private review ZIP. CI is configured in `.github/workflows/verify.yml` and runs on the authorised GitHub push; consult Actions for its actual result. No Appstore submission or marketing-site deployment has been performed.

Parent settings now lock on backgrounding and after two minutes, and protected actions recheck authorisation. Missions have safer snapshot validation, duplicate-reward protection, personalised revision, responsive panels, interactive array turning and hint scrolling. Mathematical narration, ICU plural forms, billing states and voice vocabulary use the language catalogue. US English is a labelled debug-only preview; release language staging requires review of the exact source and translation hashes.

## Project map

| Location | Purpose |
|---|---|
| `core/` | Pure Java learning engine, voice parser and entitlement restore reducer |
| `app/` | Native Android gameplay, local state, graphics, parent settings and store variants |
| `website/` | Independent static website and original educational content |
| `localization/` | Target packs, glossary, configurable translator and review reports |
| `marketing/` | Draft store copy, native icons, screenshot manifest and editable sources |
| `tools/` | Validation, original audio/icon generation, localisation and authentic device capture |
| `docs/` | Architecture, privacy assessment, device QA, provenance, signing and release status |

See [architecture](docs/architecture.md), [voice/device QA](docs/device-qa.md), [localisation workflow](localization/README.md), [marketing status](marketing/README.md), [website instructions](website/README.md), [privacy review](docs/privacy-review.md), [asset provenance](docs/asset-provenance.md) and [signing/submission](docs/signing-and-submission.md).

The source and technical privacy notice are published to the owner's GitHub repository. No app has been submitted/published on Amazon, no marketing website has been deployed and no purchase has been made. Amazon Appstore now targets Fire devices rather than general Android distribution; the standalone build is provided for separate Android testing/distribution planning. See [Amazon draft setup](docs/amazon-console-draft.md) for the saved listing/IAP state and remaining form requirements.
