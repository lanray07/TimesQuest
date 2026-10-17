# Declined pending purchase retry - 17 October 2026

Fixed in signed **0.1.3 / version code 4**. Uploaded to the existing Amazon draft; nothing submitted. No real purchase or charge was made.

## Cause and behavior

The app used the transient PENDING flag as a permanent checkout prohibition. Amazon provides no decline/expiry notification or new transaction for these requests ([official documentation](https://developer.amazon.com/docs/in-app-purchasing/implement-pending-purchases.html)). Therefore an empty successful purchase-history response cannot classify a request as declined rather than still awaiting approval.

PENDING now remains informational. A parent can explicitly retry after a complete, successful purchase-history snapshot has been saved and no entitlement is owned. Refresh, incomplete pagination, failed restoration and an in-flight checkout disable retry. Pending requests and empty snapshots never grant entitlement. The existing parent gate and purchase confirmation still apply. Every retry is an explicit action; the app never automatically resubmits a request. A valid active entitlement receipt remains necessary for ownership and fulfilment.

## Regression loop

Before changing production code, ran:

```powershell
.\gradlew.bat :app:testAmazonDebugUnitTest --tests '*.AmazonBillingHostTest.declinedOrExpiredPendingRequestCanRetryWithoutRestart' --console=plain
```

It failed at the retry-enabled assertion on 0.1.2 logic. After the fix, all **14** Amazon adapter tests pass, including declined/expired retry, duplicate-tap blocking, cancellation during retry, incomplete/failed snapshots, pending approval, account scope and fulfilment. Core and standalone release suites plus Amazon release lint/build also pass. Logs: `artifacts/pending-retry/regression-before.log`, `fix-build.log`, `signed-build.log`.

## Physical Fire result

KFTRWI, Android 9/API 28, Fire PS7331.4463N. Official Amazon App Tester, mock user 1/US, ENTITLED timesquest.premium at mock US$4.99, isolated com.timesquest.adventure.dev. App Tester supports debug APKs only; these are sandbox adapter checks, not a signed store-distributed LAT purchase or an Amazon Family/Kids account test.

1. Requested purchase; adapter ownership stayed false, App Tester displayed PENDING.
2. Pressed Decline Request in App Tester.
3. Returned to TimesQuest and solved the parent gate. Ownership stayed false and Unlock was enabled.
4. Without force-stop, data clear, reinstall or process restart, pressed Unlock and confirmed. SDK checkout opened again.
5. Completed Get Item in the sandbox and closed its success dialog. Adapter ownership became true, parent dashboard displayed owned and disabled Unlock.
6. App Tester transaction displayed FULFILLED. Process ID remained 14171 from pending through decline and successful retry.

Private evidence: `artifacts/pending-retry/results.json`, `same-process.log`, PNGs `01-pending` through `06-fulfilled`. Debug gameplay allows premium modes by design, so ownership assertions used actual account-scoped adapter preferences and parent ownership UI, not mode access. No ownership values were edited or fabricated.

## Signed release and Amazon draft

Verified the new release with apksigner and zipalign (16KB check); APK v2, same dedicated RSA 3072 certificate. Exact SHA-256 and certificate digest: [signed-release.json](signed-release.json). Installed 0.1.3 over signed 0.1.2 without clearing data; the corrected 2x2 question and Spark 3/5 survived. Evidence: `07-signed-release-updated.png`.

Replaced the existing Amazon draft APK, preserving 12 compatible Fire profiles. Amazon displays code 4 and Ready to Submit; four screenshots and Saved for review privacy disclosures remain. Proof: `artifacts/pending-retry/amazon-code4-proof.png`. Submit App was never clicked; neither app nor IAP submitted. Existing authentic store captures retain their original code-2 metadata; gameplay layout is unchanged.

Disabled sandbox mode, removed only our isolated debug app, App Tester and mock JSON. Signed 0.1.3 remains installed with learning data intact. No device preferences were changed during this pass.

Still outstanding: authorised signed Amazon Live App Testing (including actual Kids approval/decline/expiry), broader supported-device validation and publisher/human reviews listed in [needed-from-owner.md](needed-from-owner.md). The declined-pending relaunch limitation is resolved in this candidate.
