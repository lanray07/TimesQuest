# Amazon submission draft — 17 October 2026

App: `amzn1.devportal.mobileapp.a56d1e9d78304af2b803ff2217ce840b`

Release: `amzn1.devportal.apprelease.ef6d675ac85e42b999072bd68b5b8014`

Entitlement: `amzn1.dex.title.5ad33c9db70b461dae89ce78f17cbae8`

The user authorised filling submission forms, using banksmi@mail.com as the public support contact, creating `timesquest.premium` at GBP 4.99, Amazon-supported countries, and pushing this project to https://github.com/lanray07/TimesQuest.

Saved app title uses an ordinary hyphen because Amazon rejected the en dash. Category: Games / Brain & Puzzle / Casual. English is the only selected implemented language. The mandatory default English (U.S.) listing contains the prepared English copy; that listing locale does not claim a separately reviewed US gameplay pack. App pricing is free with an optional one-time entitlement. Descriptions, feature bullets and keywords were filled and the prepared 512/114px icons uploaded. Four authentic physical Fire screenshots are now uploaded; see fire-device-qa-2026-10-17.md.

The app-specific public PEM was downloaded from this draft and added to `app/src/amazon/assets/AppstoreAuthenticationKey.pem`. It is a public authentication key, not a private signing key or account credential. Do not substitute a PEM downloaded for another app.

The entitlement draft is named TimesQuest Full Adventure, SKU `timesquest.premium`. It has a description, both store icons, a GBP 4.99 base price and Amazon-calculated prices for the other marketplaces. It has not been submitted or made live.

The audience is restricted to 0–12, with educational content, gentle cartoon/fantasy violence, no realistic violence, drugs, nudity, sexual content, intolerance, profanity, advertising, gambling, location features or user-to-user content. The personal-information question is answered yes to reflect the Amazon account/receipt data, despite no app-created accounts. The SDK privacy disclosures are saved for review; final publisher/legal review remains outstanding. The app allows deletion of local learning data and has no child account creation.

The privacy URL is https://github.com/lanray07/TimesQuest/blob/main/PRIVACY.md. The notice describes current technical data flows, the observed publisher display name and the supplied support contact. The deletion URL points to its retention-and-deletion section. Both URLs and the repository support website are saved in Amazon. The review summary confirms the category, English listing, worldwide availability and complete content rating. Reviewer testing instructions are saved. Final publisher/legal/SDK review is still required. All Amazon-supported countries are approved by the owner. No signed release file, authentic screenshot, final legal approval or physical-device/store result has been fabricated. Nothing has been submitted for review or published on the Appstore.

Signed APK completed: the owner authorised creation of a dedicated TimesQuest key, signing and draft upload. Version 0.1.0 / code 1 was replaced with **0.1.1 / code 2** to correct the in-app privacy notice after inspecting SDK telemetry. Amazon accepted the replacement `app-amazon-release.apk`, package `com.timesquest.adventure`, with the same dedicated signing certificate. All 86 local test executions passed; the signed release also passed release tests/lint, signature and alignment verification. The review summary displays the uploaded file, DRM Applied: No, and 12 Amazon-compatible Fire tablet profiles; no Fire TV or automotive profiles are selected. These are manifest compatibility results, not physical-device certification. Signing handover is in `release-signing.md`; exact hashes and verification are in `signed-release.json`.

SDK privacy disclosures completed: 39 data types / 78 collection-and-transfer answers and all 14 applicable purpose sections are filled. Amazon shows **Saved for review**, including after the APK replacement. See `sdk-privacy-disclosures.md` for the inspected SDK/data-flow evidence and `amazon-privacy-answers.json` for every answer/purpose. The public notice, app notice and website draft now describe Amazon SDK telemetry accurately.

Required console fields are now satisfied: four actual 1920×1200 screenshots are saved, Multimedia shows Complete, and the app shows **Ready to Submit** with Submit App enabled. No submission was attempted. Optional promotional image, video, phone assets, app SKU, publishing schedule and the optional porting-experience survey remain blank. Final publisher/human review and the outstanding physical-device/store validation remain necessary before release. See fire-device-qa-2026-10-17.md for exact candidate, target, checks, image hashes and Amazon asset IDs.

Export certification: a page-positioning action accidentally selected the certification. Clearing it produced a validation error and Amazon retained the selection after reload. This was disclosed to the owner. The owner then explicitly replied “I confirm the export certification” after being shown its meaning. The selected certification is now authorised; reviewer instructions can be saved with it. App submission/publishing remains unauthorised and has not occurred.
