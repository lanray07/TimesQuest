# SDK privacy disclosure evidence — 17 October 2026

Scope: the signed TimesQuest Amazon edition and bundled `com.amazon.device:amazon-appstore-sdk:3.0.9`. The Amazon draft questionnaire contains 39 data types / 78 collection-and-transfer answers, with 14 applicable purpose sections. All were filled and saved using **Save and Close**; Amazon reports **Saved for review**. No app or IAP submission was made.

## Reviewed evidence

- [Amazon privacy-label definitions and questionnaire workflow](https://developer.amazon.com/docs/app-submission/appstore-privacy-labels.html), checked 17 October 2026. These require including third-party SDK processing, distinguish collection from transfer, exclude payment-card details handled entirely by an external payment service, and allow saving a new-app questionnaire for later review.
- App source: `StoreBilling.java`, `GameStore.java`, `MainActivity.java`, the merged Amazon manifest and dependency declarations. Account IDs and entitlement receipts are handled by the Amazon adapter; learning records remain private and are not passed to that adapter. Optional recognition is on-device only, without retained audio/transcripts. No general storage, location, contacts, calendar, SMS, camera or advertising-ID integration exists.
- Actual SDK JAR SHA-256: `a179aaeec903d6a871dec732d94f16c67d12691fd0aa076995bd3943dc79ca09`. Inspected its IAP metric, receipt, package-validation and diagnostic paths. Local inspection logs are in ignored `artifacts/sdk-privacy-*.txt`; no SDK binary was modified.
- `MetricManager` generates a random SDK session UUID, batches named events/timestamps, and includes request IDs, receipt lists and fulfillment/GPU-reset fields when present. `SyncMetricsTask` routes the batch to the Amazon store service. Events cover user/product/purchase-update/purchase/fulfillment calls and their responses. `MetricsHelper` can submit receipt-signature failures, parsing errors and exception details; receipt parsing includes a device identifier. Metrics are conditional on store-service support/configuration; potentially collected data is disclosed even when a particular device does not send it.
- The SDK discovers the Amazon command service and checks package/signature information. This is narrow installed-store/service detection, not a general inventory. The app also checks speech-service availability.
- The shipped API returns Amazon user/marketplace data, potentially including residence-country metadata. This is account/store information, not GPS or device location tracking. The app does not use Simple Sign-In despite its classes being present in the SDK.

## Affirmative answers

| Data type | Collected | Transferred | Purpose and evidence |
|---|---|---|---|
| User IDs | Yes | Yes | Store access/ownership/restoration and receipt security; account-scoped ID handling and automatic Amazon store requests |
| Other user info | Yes | No | Amazon marketplace/residence-country metadata supplied with user data for store functionality; no separate forwarding by app code |
| Purchase history | Yes | Yes | Entitlements, receipt verification/fulfillment, operational SDK metrics and fraud protection |
| Device or other ID | Yes | Yes | SDK session/request and receipt device identifiers; store functionality, SDK analytics and security |
| Voice or sound recordings | Yes | No | Optional transient on-device utterances for answer input; no saved recording, transcript or remote recognition fallback |
| Diagnostics | Yes | Yes | SDK parsing/verification/exception and operational diagnostics; store functionality and operational analytics |
| App interactions | Yes | Yes | SDK store-operation events; functionality and operational analytics. Multiplication answers are not sent to Amazon |
| Installed apps | Yes | No | Narrow local store/speech service discovery and package trust checks; functionality/security, no inventory upload |
| Other actions | Yes | No | Local gameplay/progress records for mission continuity and adaptive revision; functionality/personalisation |

Amazon is treated as a recipient for automatic SDK telemetry, rather than treating every SDK event as an exempt, explicit user-initiated purchase. No advertising/marketing purpose is selected. The affirmative audio/gameplay/service-discovery answers make local/transient processing visible rather than claiming the app processes no such data. Availability of recognition is device-dependent; no claim is made that Fire devices provide it.

The remaining data types are answered No for both columns: personal names, email addresses, addresses, phone numbers, race/ethnicity, sexual orientation, political/religious beliefs, payment-card/account details, credit scores, other financial data, advertising IDs, health/biometric or fitness information, physical location, browsing history, photos/videos, music/other audio files, crash logs, other performance data, in-app searches, other user-generated content, messages, general files/documents, calendars and contacts/phone logs. SDK handled-error diagnostics are disclosed under Diagnostics; no app crash-reporting or uncaught-exception collection is integrated. Support email correspondence happens outside the app and is described separately in the public notice.

`amazon-privacy-answers.json` records every entered answer and purpose from the form DOM. It contains no child data, receipts, credentials or private signing material. Public and in-app notices were corrected to disclose SDK telemetry; version 0.1.1 / code 2 carries the updated text. This technical review does not establish the publisher's complete legal compliance or certify Amazon's remote retention/wrapped-binary behaviour. Real-device/store verification and final publisher review remain necessary.
