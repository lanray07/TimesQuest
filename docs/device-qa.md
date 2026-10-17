Follow-up: [extended Fire QA](fire-extended-qa-2026-10-17.md) records offline speech and VoiceView listening confirmation, 200% text/rotation, device diagnostics and actual debug App Tester coverage. Signed 0.1.2 is installed and uploaded; LAT and the declined-pending retry limitation remain. Historical pending rows below are superseded only by those specific results.

# Device and feature validation

Update: physical KFTRWI Fire testing and four authentic draft screenshots are now recorded in [fire-device-qa-2026-10-17.md](fire-device-qa-2026-10-17.md). Three free signed-release missions, hint/correction, force-stop recovery and an offline launch/question check passed. The original host-only baseline below remains historical; unexecuted procedures still require validation.

Initial check on 17 October 2026: no Android device was connected (`adb devices -l` returned an empty list). The two configured API 36 AVDs are CVMT_Tablet_API36 (1280×800, 160dpi) and LRM_API36. Normal emulator launch failed because the Android Emulator hypervisor driver is not installed. A software-emulation launch with `-accel off` also failed to produce an adb device; no runtime pass is claimed. Logs are in ignored `artifacts/`.

Do not change Windows virtualisation/security settings automatically. Use a connected test device, or have the machine owner configure a supported emulator accelerator using [Android's emulator guidance](https://developer.android.com/studio/run/emulator-acceleration). Real Fire testing remains necessary even after an Android emulator passes.

Host verification now passes 66 JVM executions across the core, standalone debug/release and Amazon adapter suites, plus 11 Python and nine website tests. Robolectric exercises mission loops, save/recreation, parent locking, deletion, entitlement boundaries and locale selection. Native Skia host renders cover tablet/phone layouts, hints and 200% text; touch targets are checked. Labelled QA images are in ignored `artifacts/host-previews/` and are not authentic device screenshots or store assets. None of these results changes the outstanding physical-device procedures below.

| Test | Required procedure | Current status |
|---|---|---|
| Six mission loops | Run the compiled instrumentation package against the isolated standalone .dev app | Passed on physical KFTRWI; release free modes also completed through UI |
| Offline learning | Install, disable connectivity, force-stop, relaunch, complete all available missions, inspect progress | Wi-Fi-off relaunch, saved progress and new answer passed on KFTRWI; full all-mode airplane check pending |
| Save interruption | Force-stop on wrong answer, hint, corrected answer and completion; verify no duplicate attempts/rewards | Hinted/corrected question and completed rewards persisted on KFTRWI; isolated first-answer accounting test passed; remaining interruption matrix pending |
| Fire HD 8 / HD 10 / supported newer models | Install on actual models, record Fire OS/API, density, RAM, target filtering | No model certified |
| Android phone portrait / tablet landscape | Test text wrapping, back navigation, rotation and scroll recovery | Compiled; runtime pending |
| Accessibility | TalkBack focus order, every button at least 48dp, 200% text, contrast, reduce motion | Code checks only; assistive-tech testing pending |
| Narration | Record installed TTS engine/voice/language; verify offline tutorial, question and hint output | Not executed |
| Recognition | Check installed on-device engine/language, parent setting, permission denial, session stop/background, all commands and 0–144 number words | Parser tests executed; actual speech pending |
| Amazon IAP | Configure public key and non-consumable; Amazon App Tester then Live App Testing; success/failure/cancel/restore/account switch/offline/refund | Reducer tests executed; store flow pending |
| Localisation | Review all strings/grammar; Arabic RTL; numbers, expanded text and actual voice support | English structural checks only |
| Performance | Profile frame pacing, startup, memory and repeated navigation on lower-powered Fire hardware | Not measured |

Device test command after connecting a disposable test target:

```powershell
.\gradlew.bat :app:connectedStandaloneDebugAndroidTest
```

The custom instrumentation resets only the isolated `.dev` app's local learning records, exercises six modes, checks first-attempt accounting and save/reload/deletion, and never calls purchasing or the microphone. The APK uses no native `.so` binaries, so there is no app-native ABI dependency to package; verify Amazon's wrapped binary in Live App Testing.
