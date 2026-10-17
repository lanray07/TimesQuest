"""Inspect local release prerequisites. Missing human/account/device inputs remain explicit blockers."""
from pathlib import Path
import hashlib,json,os,zipfile
import localize
ROOT=Path(__file__).resolve().parents[1]
checks=[]
def check(name,passed,needed):checks.append({'check':name,'passed':bool(passed),'needed':None if passed else needed})
key=ROOT/'app/src/amazon/assets/AppstoreAuthenticationKey.pem'
check('Amazon public authentication key',key.is_file() and key.stat().st_size>0,'Provide the app-specific public AppstoreAuthenticationKey.pem from your developer console.')
variables=['TIMESQUEST_KEYSTORE','TIMESQUEST_STORE_PASSWORD','TIMESQUEST_KEY_ALIAS','TIMESQUEST_KEY_PASSWORD']
signed_file=ROOT/'docs/signed-release.json'
signed=json.loads(signed_file.read_text(encoding='utf-8')) if signed_file.is_file() else {}
signed_apk=ROOT/signed.get('path','missing-signed-apk')
verified_signed=signed.get('signatureVerified') and signed.get('alignmentVerified') and signed_apk.is_file() and hashlib.sha256(signed_apk.read_bytes()).hexdigest()==signed.get('sha256')
check('Publisher release signing',all(os.environ.get(v) for v in variables) or verified_signed,'Configure the signing environment or build and verify the dedicated signed release; never paste passwords or private keys in chat.')
check('Website publisher configuration',(ROOT/'website/release.config.json').is_file(),'Supply the approved publisher name, support/privacy emails, HTTPS origin, name decision and reviewed privacy/education evidence in release.config.json.')
review=ROOT/'localization/reviews/gameplay/en-GB.json'
review_issues=localize.review_errors('en-GB','gameplay')[0]
check('English human review record',not review_issues,'Provide named reviewers and actual evidence, matching the current source and translation hashes. Details: '+'; '.join(review_issues))
captures=ROOT/'marketing/screenshots/captures/en-GB'
check('Authentic gameplay captures',captures.is_dir() and len(list(captures.glob('*.png')))>=8,'A running Fire/Android test target is needed for the eight documented real gameplay captures.')
verification=ROOT/'artifacts/verification.json'
if verification.is_file():
    results=json.loads(verification.read_text(encoding='utf-8')).get('checks',[])
    check('Latest local verification',bool(results) and all(c.get('passed') for c in results),'Fix any failing checks listed in artifacts/verification.json.')
else:check('Latest local verification',False,'Run python tools/verify.py.')
packages=[]
for apk in sorted((ROOT/'app/build/outputs/apk').glob('*/release/*.apk')):
    with zipfile.ZipFile(apk) as z:
        names=set(z.namelist());packages.append({'file':apk.relative_to(ROOT).as_posix(),'sha256':hashlib.sha256(apk.read_bytes()).hexdigest(),'hasUsDraft':'assets/i18n/en-US-preview.json' in names})
check('No US draft in release APKs',bool(packages) and all(not p['hasUsDraft'] for p in packages),'Build release APKs and remove debug-only language packs from release assets.')
report={'status':'BLOCKED' if any(not c['passed'] for c in checks) else 'Prerequisites present — still requires actual device/store QA and explicit publishing authorisation','checks':checks,'releasePackages':packages,'note':'Presence checks do not prove legal approval, signing validity, a cleared name, real-device compatibility or Amazon transaction success. No publishing is performed.'}
(ROOT/'docs/release-audit.json').write_text(json.dumps(report,indent=2)+'\n',encoding='utf-8')
for c in checks:print(('PASS' if c['passed'] else 'NEEDED')+': '+c['check'])
raise SystemExit(1 if report['status']=='BLOCKED' else 0)
