# Fire device testing and screenshot evidence — 17 October 2026

## Candidate and target

The physical Amazon Fire tablet reports model **KFTRWI**, device/product `trona`, Android API 28 / Android 9, build `PS7331.4463N`, density 240 dpi, and a native landscape framebuffer of **1920×1200**. This record covers this one device only.

Installed the unchanged dedicated-key signed Amazon release **0.1.1 / version code 2**, package `com.timesquest.adventure`, SHA-256 `ada6347026f124ec27fd4aab306cf5524749f65674bdec2b0336ea376ce2bebc`, identical to the APK already accepted into the Amazon draft. Installation initially failed with insufficient internal storage (484 MB free). Android's `pm trim-caches 1G` reclaimed disposable caches, leaving about 1 GB free; installation then succeeded. No personal files or installed apps were removed. The official Amazon USB driver was registered on Windows; the owner enabled and authorised ADB on the tablet.

## Executed release checks

- Launched the actual signed Amazon build and inspected its landscape layout and touch controls.
- Completed five-question Monster Math Battles, Treasure Hunt and Times Table Jump missions through the visible UI, with correct feedback, crystal restoration and individual badges. The map retained three earned crystals after force-stop/relaunch.
- Selected the free 3-times-table practice. Deliberately answered 3×5 incorrectly with 8: the app explained addition versus multiplication, equal groups, repeated addition and fact family. Corrected it to 15; inspected the real 3×5 dot array.
- Force-stopped and relaunched the corrected, hinted question: the same fact, hint and answered state survived. Also exercised Pause and Back to the islands.
- Disabled Wi-Fi, force-stopped/relaunched the release, confirmed persisted progress, opened a new Monster Math mission and answered 1×8 correctly. Restored Wi-Fi afterward. This is an offline launch/new-question smoke check, not an airplane-mode test of every mode or purchasing.
- No crash was observed in these UI flows. Automated UI hierarchy extraction intermittently returned no root on Fire; fresh screenshots were used to inspect state and no stale hierarchy was treated as a successful action.

## Isolated physical-device instrumentation

Installed the existing standalone debug APK and its instrumentation APK into the separate `.dev` package. `adb shell am instrument -w com.timesquest.adventure.dev.test/com.timesquest.adventure.QuestInstrumentation` returned `INSTRUMENTATION_STATUS_CODE: 0` and **PASS: six game modes, first-answer tracking, explanations, mission rewards, save reload and deletion.** This test directly drives activity methods on the device and clears only its isolated `.dev` learning data. It is separate from the three visible-UI signed-release mission runs, makes no store purchase, and supplies no store screenshots. Local log: `artifacts/fire-qa/instrumentation.log`.

## Native captures and Amazon result

The user explicitly authorised capture and upload to the existing **unsubmitted draft**. Four unedited `adb exec-out screencap -p` PNGs were uploaded to the mandatory default English (U.S.) multimedia slot (the actual app strings are en-GB):

| Local capture in `marketing/screenshots/captures/en-GB/` | Scene | Amazon asset ID |
|---|---|---|
| `01-adventure.png` | Island map with one genuinely earned crystal | `amzn1.dex.asset.4fa6af04f8124d36a03e5e8e0d188486` |
| `03-battles.png` | Corrected 3×5 battle and real dot-array hint | `amzn1.dex.asset.6907dcb5454045c19ec95ceb1a8240f5` |
| `09-treasure.png` | Treasure Hunt question and numbered locks | `amzn1.dex.asset.31f7aa171ab94c428cdbd3448573bd33` |
| `10-jump.png` | Times Table Jump question and stepping stones | `amzn1.dex.asset.56f89f13af2141ff915bfe0ade09e9d7` |

Each capture is 1920×1200. Adjacent JSON records identify device, candidate hash, image hash, method and draft-only review status. These are actual release gameplay screens, with no seeded progress, premium entitlement bypass, resizing, generated replacement UI or debug watermark removal. The battle hint is a naturally scrollable screen; its continuation extends below the viewport.

Amazon's review page shows **Ready to Submit**, Multimedia **Complete**, four screenshots and an enabled Submit App button. That button was not clicked. No app or IAP was submitted. Local proof: `artifacts/fire-qa/amazon-screenshots-proof.png`.

## Remaining validation

Human educational/locale review remains pending; these owner-requested draft assets do not assert full locale approval. `approvedLocales` remains empty in the screenshot manifest. Final human-approved marketing exports remain governed by the capture tool's existing review gate.

Follow-up speech, VoiceView, 200% text/rotation and App Tester results are recorded in [fire-extended-qa-2026-10-17.md](fire-extended-qa-2026-10-17.md). LAT, microphone recognition on a supported target, sustained performance, other models and final publisher/legal/name review remain outstanding. API 28 does not meet this app's API 31 on-device recognition requirement; no voice-answering capture or claim was added. No paid purchase was made.
