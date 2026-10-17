# Extended Fire checks - 17 October 2026

Target: physical KFTRWI, Android 9/API 28, Fire PS7331.4463N, 1920x1200, density 240. No app or IAP submitted; no real money charged. Evidence files below are private ignored artifacts, not store captures.

## Speech and accessibility

- Signed release 0.1.1: Wi-Fi off, Read the question spoke one times eight. Owner confirmed clear and understandable sound.
- Installed Ivona engine com.ivona.tts.oem version 337310, offline Amy / en_GB_amy. Isolated debug diagnostics synthesized question and hint wording to a non-silent 8.221-second WAV (22050 Hz mono PCM16; 362592 bytes; peak 28376, RMS 5365.956). This proves synthesis, not a comprehensive educational voice review.
- Real task background stopped narration and scene animation; reduced-motion preference reached SceneView. All eight measured mission buttons were at least 48dp at font scale 2.0.
- Signed release 0.1.1: 200% text in landscape and portrait retained the question and answer state, wrapped text and exposed actions. Scene illustration is shallow in portrait; no clipped action labels observed.
- Actual Fire VoiceView (com.amazon.logan/.LoganAccessibilityService) active with touch exploration: keyboard focus reached Parents, Pause, Continue and answer 18; Enter advanced and answered correctly. Signed release 0.1.2 was checked again and owner confirmed spoken control labels were clear. This is a focused navigation check, not a full accessibility audit.
- API 28 cannot run the app's API 31+ on-device recognition feature. Touch fallback passed; microphone recognition, command accuracy and permission-denial recording on a supported device remain outstanding.
- Diagnostics command: adb shell am instrument -w -e deviceChecks true com.timesquest.adventure.dev.test/com.timesquest.adventure.QuestInstrumentation. Log: artifacts/fire-extended/device-diagnostics.log. Audio metrics: tts-audio-metrics.json. Large text and VoiceView evidence PNGs are in the same folder.

## Amazon SDK sandbox

Installed official Amazon App Tester and a locally defined ENTITLED timesquest.premium mock product; enabled debug.amazon.sandboxmode debug. Used only isolated com.timesquest.adventure.dev, never the signed release for sandbox purchases. Debug gameplay access is always enabled, so assertions used the real adapter's account-scoped ownership preference and parent ownership label, not access to premium modes.

Passed: GBP 4.99 product display (also USD under US mock marketplace); app confirmation cancel; SDK checkout cancel with no ownership; successful Get Item and FULFILLED transaction; explicit restore; restore after clearing only isolated debug data; cancellation/refund revokes ownership; pending request initially stays locked; approval restores ownership; mock account 1 owned -> account 2 unowned -> account 1 owned. Screenshots/logs are in artifacts/fire-extended/.

Found and fixed: external approval/refund/account changes were not refreshed when returning to a running app. MainActivity now refreshes billing on every resume, including when the parent gate locks. StoreBilling avoids refreshing during an in-flight checkout. On candidate 0.1.2, approved pending request and cancelled entitlement updated with the same process ID, and account switching passed. Regression tests cover parent-lock resume, in-flight checkout and approved pending receipt.

Known remaining limitation: declining a pending mock request leaves ownership false, but its in-memory pending state keeps checkout disabled until app relaunch. Empty purchase updates cannot distinguish still-awaiting approval from a declined request. Do not certify the complete pending-decline retry experience; verify it in Amazon Kids LAT and resolve before release. No access was granted for the declined request.

App Tester only supports debug apps. These results do not certify the signed release's production Amazon account, wrapped binary, Amazon Kids profile or actual LAT purchase flow. Official instructions: https://developer.amazon.com/docs/in-app-purchasing/test-pending-purchases.html and https://developer.amazon.com/docs/in-app-purchasing/iap-app-tester-user-guide.html.

## Updated signed candidate and draft

Built 0.1.2 / code 3 with the same dedicated RSA signing certificate; signature and alignment passed. Core, standalone debug/release and Amazon host suites pass (70 JVM executions); Amazon release lint/build pass. Exact APK hash is in signed-release.json. Logs: foreground-fix-build.log, signed-release-012-build.log, signature-012.txt in artifacts/fire-extended/.

Installed signed 0.1.2 over 0.1.1 without clearing learning data. Saved adventure survived; with Wi-Fi off, answered 2x2 correctly, force-stopped and relaunched: same corrected question and Spark 3/5 survived. VoiceView label listening confirmed on this signed candidate. One cold-start sample: am start -W TotalTime 896ms, WaitTime 1264ms; immediate total PSS 47384KB. These are single observations, not frame-pacing, sustained-memory or performance certification.

Replaced existing Amazon draft APK with code 3. Review page shows Ready to Submit, code 3, four retained screenshots and enabled Submit App; never clicked. Proof: artifacts/fire-extended/amazon-release-012-proof.png. Existing screenshots accurately retain their code-2 capture metadata; layout is unchanged by the billing fix.

## Cleanup and remaining work

Sandbox mode set to none. Removed only the newly installed isolated debug/test packages, App Tester and our sandbox JSON. Restored font scale 1.0, automatic rotation, original accessibility-off state, Wi-Fi on and USB stay-awake off. Signed 0.1.2 remains installed with learning data intact.

Still required: declined-pending retry fix/LAT validation; authorised LAT target and Amazon Family/Kids account if testing child approval; all six premium modes in the signed store-distributed candidate; broader interruption/offline matrix; supported-device microphone testing if retained; sustained performance and other intended Fire models; educational, name and publisher/privacy review. See needed-from-owner.md. No Amazon password should be sent in chat.
