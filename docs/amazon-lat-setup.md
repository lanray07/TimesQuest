# TimesQuest Amazon Live App Testing - 17 October 2026

**Test 1 created and privately submitted. Public app remains Ready to Submit and has not been submitted.**

Release name: TimesQuest 0.1.3 - Fire purchase checks. Copied the latest signed 0.1.3 / version code 4 APK and existing metadata; verified code 4, English, icon, content rating, previously owner-confirmed export certification and 12 compatible Fire tablet profiles (zero TV/automotive). No new APK build or runtime changes.

Channel: `amzn1.channel.123ed68a836f4209bc6e2c7dd59127ab`. Dashboard status: **Test submitted**. Amazon processing must finish before testers can access it; no publish-complete claim is made. The two unrelated existing tester groups were left unselected. **Zero testers assigned; no invitations sent.**

Submitted the existing `timesquest.premium` entitlement using **Submit to Test**, not Submit to Test and Live. Amazon displays **TEST / Approved**. GBP 4.99 base/catalog price is unchanged. LAT transactions are free for eligible invited testers; this does not make ordinary public transactions free. The public app and IAP have not been promoted or submitted for public release.

Private screenshots: `artifacts/lat-setup/lat-test-submitted.png` and `test-iap-approved.png`. Machine-readable record: [amazon-lat-setup.json](amazon-lat-setup.json).

## Exactly what the owner must supply or do

1. **Identity supplied:** the owner supplied the Fire-account email and first/last name; the email matches an existing Amazon tester. Prepared a dedicated TimesQuest Fire Purchase Test group and filled the supplied identity. Amazon requires confirmation that the publisher has the right to share those details, the tester consents to Amazon communications and the tester is at least 18. The owner explicitly confirmed all three; the declaration was checked and Add completed successfully. TimesQuest Fire Purchase Test is now saved with one existing tester. No LAT invitation has been sent. Tester identity is kept out of this public repository. The actual marketplace link will be chosen during invitation redemption.
2. **Accept the invitation:** when Amazon finishes processing and the tester is assigned, open the invitation's matching marketplace link while signed into that same Amazon account. Redeem/Get App for the connected Fire. Complete sign-in/OTP yourself if required; no password, OTP or payment data in chat. The test installed from Amazon must replace the sideloaded candidate before LAT billing is certified. No installed app data was removed by this setup.
3. **Amazon Kids flow:** have an existing parent-managed Kids child profile available on the Fire, add the LAT app to it and enable its in-app purchases. Be present on the Parent Dashboard to decline one request and approve a later one. Confirm the profile/permission setup when ready; no child's name or birth date is needed in this chat. No Amazon Family/profile changes were made by this setup.
4. **Cross-account isolation:** a second authorised adult Amazon test account, also invited to this LAT, is needed for a real account-switch comparison. Prefer a separate registered Fire to avoid disrupting the current tablet's account. This is additional coverage; the first account is enough to begin purchase and restore checks.

The Fire is connected and USB debugging works. Keep it unlocked and online during installation/testing. App Tester/debug packages were already removed and sandbox mode disabled in the prior pass; no mock ownership is used for LAT.

## Purchase checklist after the invitation is redeemed

- Confirm Amazon-installed test track/version and TEST distribution before checkout. Confirm the transaction is zero cost; hand control back if any real charge is shown.
- Product available/local price, confirmation cancel and store cancel: no entitlement.
- Successful LAT purchase: actual signed release unlocks the premium islands/modes and persists ownership; no debug bypass.
- Relaunch, Restore, reinstall where safe, and offline cached ownership.
- Reset the tester's LAT entitlement through Manage testers to test first purchase again; this is a test entitlement reset, not a production refund. Test a genuine revocation response where Amazon supports it and label the evidence accurately.
- Kids pending request: no unlock; decline; same-process return and retry; then approve another request and verify ownership refresh/fulfilment.
- Second invited account: no ownership leakage; original account restores its own entitlement.

These have passed in debug App Tester within documented scope, but signed LAT checks remain pending. Neither LAT setup nor a TEST-approved SKU is proof that real store purchase checks passed.

## Sources

[Create LAT](https://developer.amazon.com/docs/app-testing/live-app-testing-getting-started.html), [test-only IAP and no-charge LAT transactions](https://developer.amazon.com/docs/in-app-purchasing/iap-lat.html), [tester management and entitlement resets](https://developer.amazon.com/docs/app-testing/manage-testers.html), [invitation redemption and device account](https://developer.amazon.com/docs/app-testing/prepare-app-testers.html), [Kids pending purchase setup](https://developer.amazon.com/docs/in-app-purchasing/test-pending-purchases.html).

Tester form follow-up: account-level tester already exists, so reuse it rather than creating a duplicate. The old group memberships remain unchanged. A direct inline name edit failed in Amazon UI without saving; the supplied names were filled in the Add-to-group dialog instead. The group was saved after explicit declaration confirmation. Proof: artifacts/lat-setup/tester-group-saved.png. Test-level Manage testers was still disabled with Test submitted status at the last check.

Latest state after group save: Test 1 still shows Test submitted, and test-level Manage testers remains disabled. Account group has one member; LAT still has zero assigned testers and no invitation has been sent. Assign the saved group once Amazon enables Manage testers. The saved tester consent authorises this private TimesQuest invitation; do not ask for the same confirmation again.

## LAT active and tester assigned

Amazon now shows **Test in-progress** and enabled Manage testers. Assigned only **TimesQuest Fire Purchase Test (1)** to Test 1 and saved. Expanded its member row and verified the supplied first/last name and email; invitation status is **Pending**. Amazon has queued the automatic invitation; delivery is not yet confirmed. No duplicate resend was requested. Public app still shows Ready to Submit; no public app/IAP submission or promotion. Screenshot: artifacts/lat-setup/invitation-status.png. The zero-tester/processing statements above describe earlier setup states and are superseded by this section.
