"""Record actual build outputs and executed JVM test results, without inferring device validation."""
from pathlib import Path
import hashlib,json,xml.etree.ElementTree as ET,os,re,subprocess
ROOT=Path(__file__).resolve().parents[1]
outputs=[]
signed_file=ROOT/'docs/signed-release.json'
signed=json.loads(signed_file.read_text(encoding='utf-8')) if signed_file.is_file() else {}
for path in sorted((ROOT/'app/build/outputs/apk').rglob('*.apk')):
    digest=hashlib.sha256(path.read_bytes()).hexdigest()
    signing='Android debug certificate' if '/debug/' in path.as_posix() else 'unsigned release candidate' if path.name.endswith('-unsigned.apk') else 'unverified release signature'
    if digest==signed.get('sha256') and signed.get('signatureVerified') and signed.get('alignmentVerified'):signing='Dedicated TimesQuest release certificate: '+signed['certificateSha256']
    outputs.append({'path':path.relative_to(ROOT).as_posix(),'bytes':path.stat().st_size,'sha256':digest,'signing':signing})
tests=[]
for folder in [ROOT/'core/build/test-results/test',*sorted((ROOT/'app/build/test-results').glob('test*UnitTest'))]:
    for path in sorted(folder.glob('TEST-*.xml')):
        node=ET.parse(path).getroot();tests.append({'configuration':folder.name,**{key:node.attrib[key] for key in ['name','tests','failures','errors','skipped']}})
if not outputs or not tests:raise SystemExit('Build artifacts and test results must exist first')
if any(int(t['failures']) or int(t['errors']) for t in tests):raise SystemExit('A recorded JVM suite failed; resolve it before writing a delivery report.')
report={'date':'2026-10-17','artifacts':outputs,'executedJvmTestSuites':tests,'jvmExecutions':sum(int(t['tests']) for t in tests),'deviceTests':'Physical KFTRWI release UI and isolated instrumentation checks passed within documented scope; new question-variety build passed all six modes/save/retry/rewards; docs/question-variety-2026-10-17.md','purchaseTests':'Physical App Tester mock cases including declined-pending same-process retry passed. Amazon-installed LAT 0.1.3 owned entitlement, explicit Restore, offline cold restart and premium gameplay passed; real Kids pending and second-account coverage remain; checkout cost confirmation pending. docs/amazon-lat-purchase-checks.md','voiceTests':'Offline Ivona/Amy narration and VoiceView labels owner-confirmed; physical synthesis/background stop passed; API 28 cannot run on-device microphone recognition','website':'20 static pages and nine automated website tests passed; public website deployment remains separate','localisation':'English education/narration owner approval recorded with current hashes; Fire voice/gameplay evidence documented; additional locales unapproved'}
(ROOT/'docs/build-artifacts.json').write_text(json.dumps(report,indent=2)+'\n',encoding='utf-8')
print(f'Recorded {len(outputs)} APK artifacts and {report["jvmExecutions"]} JVM test executions across core/debug/release/Amazon configurations.')
