# Exactly what is still needed from the owner

The remaining account, factual, legal and physical-device inputs cannot be invented or certified by host tests. No Amazon password is needed in this chat. Keep signing passwords and private keys in your local environment.

## Decisions and factual details

1. **Release name:** confirm that you have cleared the working name, or choose a replacement. Another children's maths product already uses [TimesQuest](https://www.sparkanvil.org/apps/timesquest/). The local project retains your requested working name; no name has been reserved or legally cleared.
2. **Publisher:** final legal/trading identity, business/contact address where required, publisher country and final privacy contact confirmation. Support email is now supplied as banksmi@mail.com; Amazon-supported countries are approved. A technical privacy notice is hosted in the GitHub repository; the final publisher/SDK review remains outstanding. No marketing website domain has been selected.
3. **Launch languages:** choose UK English first, or specify the additional languages that must launch together. UK English is the implemented base. US English is a complete but unreviewed debug preview. The other ten target packs still need actual translations and native educational review. The runtime, plural forms, number formatting, voice vocabulary and staging checks are now prepared.

## Amazon setup — public and non-secret inputs

4. **Completed:** the app-specific public **AppstoreAuthenticationKey.pem** was downloaded from the TimesQuest draft and installed at `app/src/amazon/assets/AppstoreAuthenticationKey.pem`. It is not a private signing key.
5. **Product draft completed:** **timesquest.premium** is an **ENTITLED/non-consumable** Full Adventure product, with the approved GBP 4.99 base price and Amazon-calculated other-marketplace prices. It is now approved with a TEST label after submission to LAT only; not submitted/promoted for public sale. The signed package **com.timesquest.adventure** / version code 4 has been accepted into the existing draft. Runtime prices come from Amazon.
6. App Tester mock success, pending approval, cancellation/refund, restoration and account switching now passed on the Fire. Private LAT Test 1 has been submitted with signed 0.1.3/code 4, and the IAP is TEST-approved. Tester identity is now supplied and matches an existing Amazon tester. The owner confirmed Amazon's rights/communications/age-18 declaration, and the dedicated one-member TimesQuest group is saved. LAT Test 1 is now in progress, the group is assigned, and its automatic invitation is Pending. Once delivered, the owner must redeem it and have a parent-managed Kids profile ready for approval/decline. A second invited account is needed for cross-account coverage. See [amazon-lat-setup.md](amazon-lat-setup.md). No password or payment details in chat. The signed store-distributed candidate still needs LAT purchase/restore/offline/refund validation; declined-pending retry is fixed and physically verified in 0.1.3. No paid purchase is authorised.

## Signing — configure locally

7. **Completed:** a dedicated publisher signing keystore was created at your request and the Amazon release was signed and verified. See `release-signing.md`. Back up this private key securely and record its password separately in your password manager; the Windows-encrypted password file is tied to this user/machine. The private rebuild helper supplies:

```text
TIMESQUEST_KEYSTORE
TIMESQUEST_STORE_PASSWORD
TIMESQUEST_KEY_ALIAS
TIMESQUEST_KEY_PASSWORD
```

The first variable is the local keystore path; the others are its credentials/alias. They are set temporarily by the private rebuild helper and cleared on exit, never printed or stored in the repository/chat. With no variables an ordinary build remains unsigned; partial configuration fails rather than silently choosing a different key. No unrelated project's key is used.

## A real test target and review evidence

8. **Target supplied:** a KFTRWI Fire tablet is connected; three free release missions, save recovery and an offline question check passed. See fire-device-qa-2026-10-17.md. Complete the remaining device matrix and arrange any additional intended Fire models/Fire OS versions. Enable USB debugging when appropriate. A working Android emulator can run more tests, but physical Fire validation is still needed. The release matrix covers six modes, save interruption, airplane-mode relaunch, rotation, large text/TalkBack, reduced motion, sound and memory/frame pacing.
9. Offline Ivona/Amy question speech and VoiceView labels are owner-confirmed clear; synthesis, background stop, 200% text and focused VoiceView navigation passed. Provide an API 31+ target with installed on-device recognition if microphone answering is to be validated. Record engine/language/device and the observed results for all voice commands, permission denial and microphone stopping. Unsupported Fire devices must keep the touch fallback and must not advertise voice answering.
10. **Amazon minimum completed:** four authentic 1920×1200 Fire gameplay screenshots were captured and uploaded to the unsubmitted draft. Additional premium/voice/phone scenes are optional and must be captured only after their corresponding validation. Host-rendered QA PNGs are labelled internal previews and must not be submitted to the store. Supply the corresponding device/build/locale evidence for each capture.
11. Obtain a competent human review of English educational explanations, narration and the eight article drafts, and native review of any additional launch languages. Keep evidence files in the repo and reference them in `localization/reviews/gameplay/<locale>.json` and `.../marketing/<locale>.json`. Review records must match both the current source and target-pack hashes. A translation provider is optional; if you want automated drafts, configure the trusted development HTTPS endpoint and key locally. No translation key is embedded in the app.
12. Complete the final publisher-specific legal/children's privacy review and support process. The technical SDK review, all privacy-label answers/purposes, content rating and public/in-app notices are now completed and saved in the Amazon draft; see sdk-privacy-disclosures.md. Final operator details, remote SDK/platform retention and device speech behaviour still need validation. Supply final policy/review approval for the separate public marketing website build; the technical assessment is not legal certification.

## Final release action

13. After these items pass, approve the specific signed app submission and the specific public website deployment. Neither publishing action has been taken or is authorised implicitly by the polishing request.

The exact current machine-readable blocker list is generated by `python tools/release_audit.py`. It intentionally exits non-zero while required inputs are missing. Presence of a file is not a substitute for its review or real device/store testing.

Declined-request fix and same-process Fire retry evidence: [fire-pending-retry-2026-10-17.md](fire-pending-retry-2026-10-17.md).

Extended results and limits: [fire-extended-qa-2026-10-17.md](fire-extended-qa-2026-10-17.md). Temporary sandbox tooling was removed; signed 0.1.3 remains on the Fire.
