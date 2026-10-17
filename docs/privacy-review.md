# Children's privacy review worksheet — draft

This is an engineering assessment, not a completed legal review. The publisher, controller identity, support/privacy contact and launch countries are not yet supplied. The in-app draft and website draft must be finalised before distribution.

The intended audience is children around 6–11. Treat this as child-directed, not a general-audience app relying on a nominal age screen. Relevant starting points include [FTC COPPA guidance](https://www.ftc.gov/business-guidance/resources/complying-coppa-frequently-asked-questions), the [FTC voice-recording statement](https://www.ftc.gov/news-events/news/press-releases/2017/10/ftc-provides-additional-guidance-coppa-voice-recordings) and the [ICO Children's Code](https://ico.org.uk/for-organisations/uk-gdpr-guidance-and-resources/childrens-information/childrens-code-guidance-and-resources/). Recheck the current rules and applicability with counsel for the actual operator and markets; these sources do not certify this app.

Data flows implemented:

- Private local records: directed multiplication fact ID, attempts, first-answer correctness, assisted count, consecutive unassisted count, next review date and bounded response duration; active mission, earned crystals, badges and settings. No child name, account, contact details, device advertising ID, location, photos or public profile.
- Amazon variant: account identifier and product receipts from the Appstore SDK, cached in separate private preferences for entitlements. Amazon receives platform purchase requests. Its current SDK/platform collection, wrapped-binary behaviour, disclosures, privacy labels and child policy terms need review. No purchase credentials are handled by TimesQuest.
- Voice: explicit optional push-to-talk through the on-device Android recogniser only. No raw audio or transcript is stored. Offline TextToSpeech voices only. Confirm installed service behaviour and language models on each supported device; do not infer that all system speech services are offline.
- Standalone variant: no INTERNET permission and no Amazon SDK. No analytics, ads, cloud save, AI conversation, chat or remote child-facing links.
- Website: no client analytics, cookies or forms; hosting logs remain an operator responsibility once a host is selected.

Controls: parent gate for settings/purchasing/progress, microphone disabled initially, runtime permission, bounded recognition sessions, stop on background, independently muted sound and narration, reduced motion, local deletion and disabled backup/transfer. The arithmetic gate is a friction barrier and **is not verifiable parental consent**. The app does not use it to authorise uploading voice or personal information.

Outstanding assessment: determine whether and how COPPA and UK GDPR/Children's Code apply to the publisher's complete service, document best interests and any required DPIA, establish legal basis and notices for relevant processing, confirm Amazon processor/controller roles and any SDK retention, validate all third-party speech behaviour, publish a genuine contact and retention policy, and complete Amazon privacy labels. Do not add an external AI endpoint without a fresh review and appropriate authorisation/consent.
