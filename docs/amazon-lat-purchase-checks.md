# LAT purchase checks - 17 October 2026

**No signed LAT purchase check has passed yet. Public app remains unsubmitted; no purchase was initiated.**

Amazon console now shows the tester invitation as Delivered. On the Fire, the Amazon.co.uk thank-you page confirms TEST 1 invitation redemption. The selected automatic delivery target was a different registered Fire (9th Fire); the USB-connected target is the 8th Fire, KFTRWI / Fire HD 10 (11th generation), Android 9/API 28.

The connected device still had the earlier dedicated-key sideload: 0.1.3/code 4, installer null, previous update time. Sandbox mode is none; no App Tester is installed. Its billing screen shows store unavailable, so this cannot be certified as an Amazon-installed LAT test.

Opened the actual TEST 1 product in the connected Fire Appstore using the observed retail ASIN B0HN8BZVWP. The page offers Free Download. With the existing APK present, the Appstore merely opens the old copy. Tried uninstalling while retaining all local data (-k), then downloading the actual test copy. Amazon rejected installation with SignatureMismatchException: its APK signature differs from the retained sideload signature.

Restored the original signed APK and enabled it for the owner profile. Verified the same corrected 7x8 question and Spark 4/5 remain. No learning data was erased. Backup is disabled by the app manifest; no claim of an exportable learning-data backup is made.

The owner explicitly approved the full TimesQuest uninstall/data reset. Fully uninstalled only TimesQuest; ADB returned Success and the package is absent. The Fire subsequently locked itself at its PIN screen. Amazon installation and purchase checks are waiting for the owner to unlock it; the PIN has not been requested or bypassed.

Private evidence: artifacts/lat-purchase/install-verification.json, test1-free-download.png, sideload-store-unavailable.png and progress-preserved.png. Only the fixed signature error was retained as diagnostic evidence; no signed download links, account identifiers, addresses or log payloads belong in public documentation.

Next: install the existing TEST 1 product through Amazon, verify Appstore installer and version, then perform zero-cost purchase/cancel/restore/offline checks and the remaining Kids/second-account matrix. Do not submit or promote the public app/IAP.
