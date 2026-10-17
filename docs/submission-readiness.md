# Submission readiness — 17 October 2026

**Status: polished development build; release blocked by external inputs and validation.** Source and a technical privacy notice are published in the owner's GitHub repository. An Amazon app/listing and GBP 4.99 entitlement draft have been filled, but no app or IAP has been submitted or made live, no marketing website has been deployed and no purchase has been made. Host tests cannot establish the full production-quality/device-compliance bar by themselves.

## Completed in the polishing pass

- Actual Android activity/lifecycle tests without a connected device, plus Android Skia host renders of all six modes, phone/tablet layouts, hints and 200% text. Internal renders are watermarked and are never treated as real device/store screenshots.
- Fixed explicit pause becoming unpaused after backgrounding, duplicate completion rewards, weak saved-choice validation and parent access remaining open after leaving the app. Complete snapshots validate before replacing live state; first-answer accounting remains atomic.
- Parent access expires after two minutes, locks on background and is checked again at protected actions. Parents can resume a mission without losing it. Backgrounding stops speech and releases game audio.
- Responsive scene/question panels, clearer mission contexts, equal-scoop feedback, turned arrays, automatic hint visibility, accessible button states/focus, large-text treatment and adaptive/themed icons.
- Personalised table revision and practice details. Adaptive selection caps historical-error weighting and strictly excludes the immediately previous fact.
- Catalogued UI, mathematical explanations/narration and voice vocabulary; ICU plurals, locale-aware numbers, reviewed-pack registry, isolated debug US English, and an approved-pack staging command. Decimal/negative transcripts are rejected rather than mistaken for positive integers.
- Stronger Amazon adapter: confirmed-user checkout, typed status, pending purchases, duplicate-tap prevention, scoped cached ownership, complete paginated snapshots, cancellations, stale-request rejection and durable fulfilment. Actual SDK response objects are covered with a transport that cannot contact or charge the store.
- Translation requests cannot redirect with an authorisation header. Responses are bounded and validated before replacing packs. Approval binds to source and target hashes and actual review-file references.
- Publisher signing environment support, pinned Gradle checksum, pinned CI actions, executable verification, release audit, artifact hashes and private review packaging.
- Website production configuration requires factual publisher/contact details, name clearance and approved privacy/education evidence. Those values populate visible content and structured data. Local previews stay noindex. Added exhaustive array, configuration, server/URL and 404 checks.

## Executed verification

| Suite | Executions | Result |
|---|---:|---|
| Core Java learning/voice/entitlement tests | 16 | Passed |
| Standalone debug Android host tests/native renders | 22 | Passed |
| Standalone release Android host tests | 19 | Passed |
| Amazon adapter host tests | 9 | Passed |
| Python localisation/transport/review tests | 11 | Passed |
| Website HTML/array/configuration/server tests | 9 | Passed |
| Standalone and Amazon lint | — | No issues |
| Debug/release/instrumentation assembly | — | Passed |

The 66 JVM executions include repeated scenarios under debug and release configurations. There are **86 successful test executions in total**, not 86 physical-device scenarios. Evidence is in `build-artifacts.json`, `artifacts/verification.json`, Gradle XML reports and logs. CI was prepared locally and has not run remotely.

Robolectric uses framework shadows/native host graphics, not a Fire OS image. SDK host tests instrument Amazon classes so its bytecode can run on the JVM; the production SDK is unmodified. Upstream Amazon D8 stack-map warnings remain in assembly output. No deployed SDK/platform certification is inferred.

## Remaining blockers

The precise owner inputs are listed in [needed-from-owner.md](needed-from-owner.md):

1. Cleared/replacement name, actual publisher/contact/domain/launch-country details, and launch languages.
2. Signed app-file/package validation and actual App Tester/Live App Testing results. The public Amazon PEM is now installed; the SKU/price draft is complete and all Amazon-supported countries are owner-approved. See amazon-console-draft.md.
3. Dedicated signing keystore configured privately through all four TIMESQUEST environment variables. Current releases are unsigned.
4. A real Fire/Android target for gameplay, lifecycle, offline, accessibility, speech and performance validation, plus authentic phone/tablet captures.
5. Human educational/narration review and reviewed translations/evidence for additional launch languages.
6. Publisher-specific privacy/SDK/legal review, final privacy/support notices, rating/privacy labels and current store checks.
7. Explicit approval of the concrete signed app submission and public website deployment after verification.

UK English is the base, not a completed human/device approval. US English remains an unreviewed debug preview. The other ten packs are incomplete; runtime preparation does not equal translation or validation. Unsupported Fire speech services retain touch/text fallback and must not advertise voice answering.

The 16 screenshot SVGs remain editable blueprints. No authentic gameplay screenshot was fabricated or exported. Child/parent playtesting and physical-device profiling may identify further polish work that cannot be assessed locally.

## Artifacts

Five Android outputs have paths, sizes and SHA-256 in `build-artifacts.json`: standalone/Amazon debug APKs, standalone/Amazon unsigned releases and standalone instrumentation. The private review ZIP adds documentation, icons, draft metadata and marked screenshot sources; it is not upload-ready. Debug signing and unsigned Amazon APK alignment have been checked. US preview resources are excluded from release APKs.

The [existing name-use finding](https://www.sparkanvil.org/apps/timesquest/), [Amazon image/metadata requirements](https://developer.amazon.com/docs/app-submission/appstore-details.html), [public-key setup](https://developer.amazon.com/docs/appstore-sdk/integrate-appstore-sdk.html) and [Fire/general-Android distribution distinction](https://developer.amazon.com/apps-and-games/blogs/2025/02/upcoming-changes-to-amazon-appstore-for-android-devices-and-coins-program) remain documented. Recheck immediately before submission; no legal clearance or certification is inferred.
