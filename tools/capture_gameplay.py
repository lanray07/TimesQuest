"""Capture an already-navigated real Android screen; no seeded progress or purchase bypass."""
from pathlib import Path
import argparse,json,subprocess,os,struct,datetime
ROOT=Path(__file__).resolve().parents[1]
p=argparse.ArgumentParser();p.add_argument('--serial',required=True);p.add_argument('--shot',required=True);p.add_argument('--locale',default='en-GB');p.add_argument('--evidence',required=True,help='QA record identifying build and feature validation');a=p.parse_args()
manifest=json.loads((ROOT/'marketing/screenshots/manifest.json').read_text())
if a.shot not in [s['id'] for s in manifest['shots']]:p.error('Unknown shot ID')
if a.locale not in manifest['approvedLocales']:p.error('Locale is not approved for marketing screenshots. Complete review and update the manifest first.')
evidence=Path(a.evidence).resolve()
if not evidence.is_file():p.error('QA evidence must exist')
adb=Path(os.environ.get('ANDROID_HOME',str(Path.home()/'AppData/Local/Android/Sdk')))/'platform-tools'/('adb.exe' if os.name=='nt' else 'adb')
raw=subprocess.check_output([str(adb),'-s',a.serial,'exec-out','screencap','-p'])
if raw[:8]!=b'\x89PNG\r\n\x1a\n':raise SystemExit('Device did not return PNG')
w,h=struct.unpack('>II',raw[16:24]);allowed=manifest['amazonAllowedLandscapeSizes']
if [w,h] not in allowed and [h,w] not in allowed:raise SystemExit(f'Actual capture {w}x{h} is outside the documented sizes; configure a supported capture device.')
out=ROOT/'marketing/screenshots/captures'/a.locale;out.mkdir(parents=True,exist_ok=True);(out/(a.shot+'.png')).write_bytes(raw)
metadata={'serial':a.serial,'locale':a.locale,'shot':a.shot,'width':w,'height':h,'evidence':str(evidence),'capturedAt':datetime.datetime.now(datetime.timezone.utc).isoformat()}
(out/(a.shot+'.json')).write_text(json.dumps(metadata,indent=2));print(f'Saved authentic capture: {out/(a.shot+".png")}')
