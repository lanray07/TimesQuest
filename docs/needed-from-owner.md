# Exactly what is still needed from the owner

The remaining account, factual, legal and physical-device inputs cannot be invented or certified by host tests. No Amazon password is needed in this chat. Keep signing passwords and private keys in your local environment.

## Decisions and factual details

1. **Release name:** confirm that you have cleared the working name, or choose a replacement. Another children's maths product already uses [TimesQuest](https://www.sparkanvil.org/apps/timesquest/). The local project retains your requested working name; no name has been reserved or legally cleared.
2. **Publisher:** final legal/trading identity, business/contact address where required, publisher country and final privacy contact confirmation. Support email is now supplied as banksmi@mail.com; Amazon-supported countries are approved. A technical privacy notice is hosted in the GitHub repository; the final publisher/SDK review remains outstanding. No marketing website domain has been selected.
3. **Launch languages:** choose UK English first, or specify the additional languages that must launch together. UK English is the implemented base. US English is a complete but unreviewed debug preview. The other ten target packs still need actual translations and native educational review. The runtime, plural forms, number formatting, voice vocabulary and staging checks are now prepared.

## Amazon setup — public and non-secret inputs

4. **Completed:** the app-specific public **AppstoreAuthenticationKey.pem** was downloaded from the TimesQuest draft and installed at `app/src/amazon/assets/AppstoreAuthenticationKey.pem`. It is not a private signing key.
5. **Product draft completed:** **timesquest.premium** is an **ENTITLED/non-consumable** Full Adventure product, with the approved GBP 4.99 base price and Amazon-calculated other-marketplace prices. It shows Ready to Submit but has not been submitted. The package **com.timesquest.adventure** still needs registration/validation through the signed app-file upload. Runtime prices come from Amazon.
6. Provide an Amazon App Tester / Live App Testing target and perform the real account/transaction checks: success, pending/failure/cancellation, restoration, account switching, offline ownership and refunded/cancelled receipts. The host adapter tests do not complete these store checks. No paid purchase is authorised by this work.

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

8. Connect a Fire tablet or arrange a tester with the intended Fire models/Fire OS versions. Enable USB debugging when appropriate. A working Android emulator can run more tests, but physical Fire validation is still needed. The release matrix covers six modes, save interruption, airplane-mode relaunch, rotation, large text/TalkBack, reduced motion, sound and memory/frame pacing.
9. Validate installed offline TTS and, where available, Android on-device recognition. Record engine/language/device and the observed results for all voice commands, permission denial and microphone stopping. Unsupported Fire devices must keep the touch fallback and must not advertise voice answering.
10. Capture the eight actual gameplay scenes on the tested tablet/phone layouts. Host-rendered QA PNGs are labelled internal previews and must not be submitted to the store. Supply the corresponding device/build/locale evidence for each capture.
11. Obtain a competent human review of English educational explanations, narration and the eight article drafts, and native review of any additional launch languages. Keep evidence files in the repo and reference them in `localization/reviews/gameplay/<locale>.json` and `.../marketing/<locale>.json`. Review records must match both the current source and target-pack hashes. A translation provider is optional; if you want automated drafts, configure the trusted development HTTPS endpoint and key locally. No translation key is embedded in the app.
12. Complete the final publisher-specific legal/children's privacy review and support process. The technical SDK review, all privacy-label answers/purposes, content rating and public/in-app notices are now completed and saved in the Amazon draft; see sdk-privacy-disclosures.md. Final operator details, remote SDK/platform retention and device speech behaviour still need validation. Supply final policy/review approval for the separate public marketing website build; the technical assessment is not legal certification.

## Final release action

13. After these items pass, approve the specific signed app submission and the specific public website deployment. Neither publishing action has been taken or is authorised implicitly by the polishing request.

The exact current machine-readable blocker list is generated by `python tools/release_audit.py`. It intentionally exits non-zero while required inputs are missing. Presence of a file is not a substitute for its review or real device/store testing.
