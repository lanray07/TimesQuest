# Submission readiness — 17 October 2026

**Status: polished development build; release blocked by external inputs and validation.** Source and a technical privacy notice are published in the owner's GitHub repository. An Amazon app/listing and GBP 4.99 entitlement draft have been filled, but no app or IAP has been submitted or made live, no marketing website has been deployed and no paid purchase has been made. Host tests cannot establish the full production-quality/device-compliance bar by themselves.

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
| Standalone debug Android host tests/native renders | 23 | Passed |
| Standalone release Android host tests | 20 | Passed |
| Amazon adapter host tests | 11 | Passed |
| Python localisation/transport/review tests | 11 | Passed |
| Website HTML/array/configuration/server tests | 9 | Passed |
| Standalone and Amazon lint | — | No issues |
| Debug/release/instrumentation assembly | — | Passed |

The 70 JVM executions include repeated scenarios under debug and release configurations. There are **90 successful local test executions in total**, not 90 physical-device scenarios. Evidence is in `build-artifacts.json`, `artifacts/verification.json`, Gradle XML reports and logs. GitHub CI is triggered by the authorised source push; consult Actions for its actual result.

Robolectric uses framework shadows/native host graphics, not a Fire OS image. SDK host tests instrument Amazon classes so its bytecode can run on the JVM; the production SDK is unmodified. Upstream Amazon D8 stack-map warnings remain in assembly output. No deployed SDK/platform certification is inferred.

## Remaining blockers

The precise owner inputs are listed in [needed-from-owner.md](needed-from-owner.md):

1. Cleared/replacement name, actual publisher/contact/domain/launch-country details, and launch languages.
2. Signed-candidate Live App Testing results; physical debug App Tester cases now passed, with a pending-decline retry limitation. The public Amazon PEM is installed; the signed APK has been accepted into the Amazon draft, the SKU/price draft is complete, and all Amazon-supported countries are owner-approved. See amazon-console-draft.md.
3. Owner backup of the dedicated TimesQuest signing key/password. Key creation, signed build, signature/alignment verification and draft upload are complete; see release-signing.md and signed-release.json. Only the standalone release remains unsigned.
4. Remaining device validation: a physical KFTRWI Fire now passed three free signed-release missions, hint/correction, force-stop recovery and an offline launch/question check. The isolated physical-device instrumentation also passed all six modes, first-answer tracking, rewards, save/reload and deletion. Four authentic tablet captures are uploaded and Amazon shows Ready to Submit. Offline speech and focused VoiceView/large-text checks now passed. Broader accessibility, sustained performance, other models and signed LAT validation remain; see fire-device-qa-2026-10-17.md.
5. Human educational/narration review and reviewed translations/evidence for additional launch languages.
6. Final publisher/legal review and actual store checks. The SDK's code/telemetry paths have now been reviewed, all 39 privacy data types and applicable purposes answered, and the questionnaire saved for review. The in-app/public notices now describe SDK operational telemetry. This technical completion does not replace legal or physical-device/service validation; see sdk-privacy-disclosures.md.
7. Explicit approval of the concrete signed app submission and public website deployment after verification.

UK English is the base, not a completed human/device approval. US English remains an unreviewed debug preview. The other ten packs are incomplete; runtime preparation does not equal translation or validation. Unsupported Fire speech services retain touch/text fallback and must not advertise voice answering.

The 16 screenshot SVGs remain editable blueprints. Four authentic unedited physical Fire screenshots have now been captured and uploaded to the unsubmitted Amazon draft. Child/parent playtesting and physical-device profiling may identify further polish work that cannot be assessed locally.

## Artifacts

Six Android outputs have paths, sizes and SHA-256 in `build-artifacts.json`: standalone/Amazon debug APKs, the dedicated-key signed Amazon release, an unsigned standalone release and standalone instrumentation. The private review ZIP adds documentation, privacy notice, icons, draft metadata and marked screenshot sources; it is not a submission package. The signed Amazon APK has been uploaded to the existing draft after certificate and alignment verification. US preview resources are excluded from release APKs. The previous pushed source also passed the GitHub verification workflow; the separate signed candidate passed local core/release/Amazon tests and Amazon release lint.

The [existing name-use finding](https://www.sparkanvil.org/apps/timesquest/), [Amazon image/metadata requirements](https://developer.amazon.com/docs/app-submission/appstore-details.html), [public-key setup](https://developer.amazon.com/docs/appstore-sdk/integrate-appstore-sdk.html) and [Fire/general-Android distribution distinction](https://developer.amazon.com/apps-and-games/blogs/2025/02/upcoming-changes-to-amazon-appstore-for-android-devices-and-coins-program) remain documented. Recheck immediately before submission; no legal clearance or certification is inferred.

Current candidate: signed 0.1.2 / code 3 accepted by Amazon draft, with foreground billing refresh fixed. Extended physical results, single startup/memory samples, sandbox coverage and the remaining declined-pending retry limitation: [fire-extended-qa-2026-10-17.md](fire-extended-qa-2026-10-17.md). The Python/website results above are unchanged prior executions; this pass reran the affected JVM suites and Amazon release lint/build.
