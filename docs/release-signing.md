# Dedicated TimesQuest release signing

Created on 17 October 2026 at the owner's explicit request. The key is dedicated to TimesQuest; no existing project's key was reused.

- Keystore: `C:\Users\User\.android\signing\TimesQuest\timesquest-release.p12`
- Alias: `timesquest-release`
- Algorithm: RSA 3072, SHA256withRSA; certificate expires 4 March 2054.
- Public certificate SHA-256: `b0582cdf6eeb641b6a6fd96c3ba697c0f07bba60980ef72466d1eb53e7d15966`
- Encrypted password: `password.dpapi` in that same private directory, encrypted for the current Windows user/machine. No password is printed or committed.
- Directory permissions allow only the current Windows user and SYSTEM. Signing files are outside the repository and are excluded from delivery ZIPs and Amazon uploads.

Rebuild locally in PowerShell:

```powershell
& 'C:\Users\User\.android\signing\TimesQuest\Build-TimesQuestRelease.ps1'
```

The helper decrypts the password only for the process, sets all four Gradle signing variables, runs core/release/Amazon host tests and Amazon release lint, builds the signed APK and clears the temporary signing variables. See `BACKUP-AND-REBUILD.txt` in the private directory for local recovery instructions.

**Owner backup action:** back up the keystore securely and record the password separately in your password manager before moving to another machine/user or reinstalling Windows. The DPAPI file is not a portable password backup. Do not paste the password into chat or upload the keystore anywhere. Losing the key or password can prevent future updates signed with this identity.

Signed output: `app/build/outputs/apk/amazon/release/app-amazon-release.apk`. Exact APK hash, public certificate fingerprint and verification results are in `signed-release.json`. Signature verification and 16 KB alignment checks passed; the certificate was matched against the newly generated public certificate. The APK is not debuggable, has the correct Amazon public key, and excludes the US debug preview. Device and real store checks remain outstanding.
