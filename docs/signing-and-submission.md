# Build, sign and submit

Use Java 17, Gradle wrapper 8.13, Android Gradle Plugin 8.11.1 and SDK 36. `local.properties` is machine-specific and ignored. Build outputs are unsigned release candidates, not upload-ready releases. Keep keystores and passwords outside the repository. Never use another project's existing keystore by accident.

```powershell
.\gradlew.bat :core:test :app:assembleAmazonRelease
```

The current unsigned output is `app/build/outputs/apk/amazon/release/app-amazon-release-unsigned.apk`. Create or choose the publisher's dedicated release/upload key through Android Studio's Generate Signed Bundle / APK flow. Alternatively use Android SDK `zipalign` and `apksigner`, supplying passwords through interactive prompts or secure environment handling, never literal command history. Verify the final signature and aligned output before upload. The generated debug key is for local testing only. No publisher signing key has been created or selected.

Gradle also supports a dedicated local signing configuration using all four environment variables: `TIMESQUEST_KEYSTORE` (absolute keystore path), `TIMESQUEST_STORE_PASSWORD`, `TIMESQUEST_KEY_ALIAS` and `TIMESQUEST_KEY_PASSWORD`. Set them privately outside source control. With all absent the release is unsigned; a partial configuration fails. Never paste secrets into chat. After a signed rebuild, rerun signature/alignment verification, device/store checks and regenerate the artifact hashes and private review bundle for that exact binary.

Amazon setup:

1. Confirm the legal publisher, developer account, tax/payment information, support details, app/package ownership and name availability. The display title is a proposal, not a reserved name.
2. Download the app-specific **public** AppstoreAuthenticationKey.pem from the console. Put it in `app/src/amazon/assets/`. It is a public authentication key, not a translation/API secret. The app shows an unavailable store without it.
3. Create `timesquest.premium` as a non-consumable entitlement (ENTITLED), with the planned GBP 4.99 test base price and reviewed marketplace pricing. Runtime prices come from Amazon ProductData.
4. Test with Amazon App Tester and then Live App Testing. Cover purchase, pending/error/cancel, already purchased, restoration pagination, account change, cached offline access and refund/cancellation. The source reducer tests do not replace these checks.
5. Review the SDK/platform's child data and privacy implications; finalise privacy labels, content rating and parental purchase controls.
6. Run the complete device matrix, then capture authentic gameplay in each approved locale. Keep the debug development label out of a release only by capturing the actual validated release candidate with a genuine test entitlement.
7. Supply the signed binary, real icons/screenshots and reviewed listing fields. Review console validation and device filters.
8. Obtain explicit publishing authorisation before submitting the app or making it live. No submission or purchase has been performed here.

Current documentation: [Amazon SDK integration](https://developer.amazon.com/docs/appstore-sdk/integrate-appstore-sdk.html), [IAP implementation](https://developer.amazon.com/docs/in-app-purchasing/iap-implement-iap.html), [Appstore details and image specifications](https://developer.amazon.com/docs/app-submission/appstore-details.html), [international distribution](https://www.developer.amazon.com/docs/app-submission/distribute-your-app-internationally.html). Checked 17 October 2026. Revalidate immediately before submission.
