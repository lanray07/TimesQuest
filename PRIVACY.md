# TimesQuest privacy notice

Last updated: 17 October 2026. Applies to TimesQuest – Multiplication Adventure, Android package `com.timesquest.adventure`, including its Amazon Appstore edition.

TimesQuest is a multiplication game designed for children around 6–11. The developer's Amazon publisher display name is **Lanray**. For support and questions about this notice, contact **banksmi@mail.com**. Do not email a child's name, learning history or voice recording when asking for help.

## Learning and settings stored on your device

The game stores multiplication attempts, first-answer correctness, use of hints, practice timing, review dates, mission progress, crystals, badges and settings in private app storage. These records help select practice questions, continue missions and show progress to a parent. They are practice indicators rather than a formal educational assessment.

The game does not ask children for a name, email address, location, photo or profile. It has no child accounts, advertisements, social features, public profiles or cloud synchronisation. TimesQuest does not operate a learning-analytics backend or send learning records to one. The Amazon SDK has its own store telemetry, described below. Android backup and device-transfer backup are disabled for app data.

## Optional speech

Written guidance and touch controls are available without microphone access. Optional voice answering is disabled initially and requires a parent setting, microphone permission and a compatible installed on-device recognition service. A tap starts a short recognition session; navigation or backgrounding stops it. TimesQuest uses only the on-device recognition API, with no online-recognition fallback. It does not save audio recordings or recognised transcripts.

Narration uses an installed TextToSpeech voice that Android identifies as not requiring a network connection. Speech availability and installed language models depend on the device and its system services. Those services are provided by the device/software provider; TimesQuest does not operate them. You can turn narration off, disable voice answering or revoke microphone permission in device settings. Missing speech services do not prevent touch play.

## Amazon purchases and restoration

The Amazon edition offers an optional one-time Full Adventure purchase through Amazon's Appstore SDK. The SDK supplies an Amazon user identifier, marketplace/product information and purchase receipts. The app caches account-scoped entitlement information in private app storage to restore ownership and allow offline access. This is separate from the child's learning progress. Amazon processes purchase, receipt and restoration requests through its services. TimesQuest does not handle payment-card details or Amazon passwords.

The bundled Amazon Appstore SDK 3.0.9 also records store-operation events and technical diagnostics. Its telemetry can include SDK session/request identifiers, timestamps, receipts/receipt identifiers, fulfillment status, device identifiers contained in receipt data, and receipt-verification, parsing or other SDK error details. It sends these to Amazon's Appstore service for store functionality, operational analytics, troubleshooting and receipt/fraud protection. This telemetry is separate from the child's multiplication answers and is not a TimesQuest advertising or learning-analytics service. Amazon user/marketplace information may include the account's marketplace or country of residence; it is not device location tracking.

The SDK checks the installed Amazon store service/provider and its package signature so it can communicate with a trusted store. The app also discovers installed speech services for optional narration/recognition. These are local service-availability/security checks, not a general inventory of installed apps sent by TimesQuest. No advertising identifier API, location permission, contacts, calendar, photo-library, messages or general file access is integrated.

Amazon operates the store and its own account/purchase records under its policies. See [Amazon's privacy notice](https://www.amazon.com/gp/help/customer/display.html?nodeId=468496) and the privacy controls for your Amazon account. TimesQuest's on-device deletion controls do not delete Amazon's purchase history or change refund rights.

The standalone Android edition has no Amazon SDK, purchase provider or Internet permission. Offline learning content is bundled in both editions; Amazon purchasing and restoration require connectivity.

## Retention and deletion

Learning records remain on the device until you delete them or remove the app's data. In the parent area, use **Delete learning data** to reset learning records, mission progress, crystals and badges. This preserves settings and store ownership. To remove all app-local records, use the device's app-storage clear-data control or uninstall the app. Amazon may restore a genuine entitlement when the same account later uses the app. Contact Amazon for deletion of records Amazon holds.

If you email the developer, the email service and the developer receive the information you choose to send, including your email address. It is used to respond to the request and retained only as needed to handle support or applicable obligations. Please use an adult's contact details and avoid sending unnecessary information about a child. You may ask about or request deletion of support correspondence using the contact above.

## Repository and website

This notice is hosted on GitHub, which may process visitor information under [GitHub's privacy statement](https://docs.github.com/en/site-policy/privacy-policies/github-general-privacy-statement). TimesQuest's separately generated website includes no client analytics, cookies, accounts or forms; its hosting provider may process access logs. App learning records are not sent to the website or repository.

## Changes and release review

Changes to app data flows will be reflected in an updated notice. The game is currently in preparation for release. Final publisher identity/contact requirements, Amazon SDK disclosures and device speech behaviour remain part of the release review; this notice does not claim that the app has been released, legally certified or tested on every Fire device.
