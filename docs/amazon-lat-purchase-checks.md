# LAT purchase checks - 17 October 2026

**Amazon-installed LAT entitlement, explicit restore and offline premium access have now been observed successfully. Public app remains unsubmitted. Owner confirmation of checkout authentication and cost is pending.**

Amazon console now shows the tester invitation as Delivered. On the Fire, the Amazon.co.uk thank-you page confirms TEST 1 invitation redemption. The selected automatic delivery target was a different registered Fire (9th Fire); the USB-connected target is the 8th Fire, KFTRWI / Fire HD 10 (11th generation), Android 9/API 28.

The connected device initially had the earlier dedicated-key sideload: 0.1.3/code 4, installer null, previous update time. Sandbox mode is none; no App Tester is installed. Its billing screen showed store unavailable, so the earlier checks were not Amazon-installed LAT tests.

Opened the actual TEST 1 product in the connected Fire Appstore using the observed retail ASIN B0HN8BZVWP. The page offers Free Download. With the existing APK present, the Appstore merely opens the old copy. Tried uninstalling while retaining all local data (-k), then downloading the actual test copy. Amazon rejected installation with SignatureMismatchException: its APK signature differs from the retained sideload signature.

Restored the original signed APK and enabled it for the owner profile. Verified the same corrected 7x8 question and Spark 4/5 remain. No learning data was erased. Backup is disabled by the app manifest; no claim of an exportable learning-data backup is made.

The owner explicitly approved the full TimesQuest uninstall/data reset. Fully uninstalled only TimesQuest; ADB returned Success. The owner then unlocked the Fire locally. Amazon installed TEST 1 successfully: version 0.1.3/code 4, installer com.amazon.venezia, first/last installation 12:11:08 device time. No PIN was requested or bypassed.

## Amazon-installed checks

- Product retrieval and initial purchase-history check succeeded; catalog price GBP 4.99 and Unlock control became available.
- Amazon checkout opened for TimesQuest Full Adventure. It displayed GBP 4.99, Verify and Pay, and phone/password verification. The agent did not press Verify and Pay or enter credentials. Cancellation/back was attempted; cancellation is not recorded as passed because entitlement subsequently appeared owned, apparently following independent tablet interaction. Owner confirmation is pending.
- Console independently confirms the product is Live with a TEST label, and the invited tester is Delivered. [Amazon documents LAT purchases as free](https://developer.amazon.com/docs/in-app-purchasing/iap-lat.html). A displayed catalog price is not evidence of an actual charge; no actual-cost claim is made pending owner confirmation.
- Full adventure owned on this Amazon account appeared in the app, with Unlock disabled. Explicit Restore Amazon purchase retained the owned state.
- Disabled Wi-Fi temporarily, force-stopped and relaunched the app. Owned status persisted. Opened premium Build-a-Robot and answered a question successfully offline. Wi-Fi was re-enabled afterwards.

Private proof includes amazon-installed-ready.png, checkout-price-hold.png, lat-owned.png, lat-restored-owned.png, lat-offline-owned-after-restart.png and lat-premium-offline-gameplay.png in artifacts/lat-purchase. No account credentials, receipts or personal account screenshots are tracked.

Private evidence: artifacts/lat-purchase/install-verification.json, test1-free-download.png, sideload-store-unavailable.png and progress-preserved.png. Only the fixed signature error was retained as diagnostic evidence; no signed download links, account identifiers, addresses or log payloads belong in public documentation.

Remaining: confirm the owner's checkout interaction/cost, isolate cancellation and retry with a disposable LAT entitlement reset, verify reset/revocation and fresh restore, and complete Kids pending approval/decline and second-account checks. Do not submit or promote the public app/IAP. The question-variety source changes are separate from TEST 1's 0.1.3 build.
