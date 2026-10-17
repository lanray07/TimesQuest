"""Run local/CI checks without a device, account credentials, purchases or deployment."""
from pathlib import Path
import json,os,subprocess,sys,time
ROOT=Path(__file__).resolve().parents[1]
OUT=ROOT/'artifacts';OUT.mkdir(exist_ok=True)
wrapper=str(ROOT/('gradlew.bat' if os.name=='nt' else 'gradlew'))
checks=[
 ('android',[wrapper,':core:test',':app:testStandaloneDebugUnitTest',':app:testStandaloneReleaseUnitTest',':app:testAmazonDebugUnitTest',':app:lintStandaloneDebug',':app:lintAmazonDebug',':app:assembleStandaloneDebug',':app:assembleStandaloneRelease',':app:assembleAmazonDebug',':app:assembleAmazonRelease',':app:assembleStandaloneDebugAndroidTest','--console=plain'],ROOT),
 ('python',[sys.executable,'-m','unittest','discover','-s','tools','-p','test_*.py','-v'],ROOT),
 ('strings',[sys.executable,'tools/localize.py','extract'],ROOT),
 ('english',[sys.executable,'tools/localize.py','validate','--locale','en-GB'],ROOT),
 ('metadata',[sys.executable,'tools/verify_metadata.py'],ROOT),
 ('website-build',['node','build.mjs'],ROOT/'website'),
 ('website-tests',['node','--test',*[p.name for p in sorted((ROOT/'website').glob('test*.mjs'))]],ROOT/'website')
]
results=[]
for name,command,cwd in checks:
    begin=time.monotonic();log=OUT/f'{name}-verification.log'
    with log.open('w',encoding='utf-8') as output:
        try:code=subprocess.run(command,cwd=cwd,stdout=output,stderr=subprocess.STDOUT,timeout=1200).returncode
        except subprocess.TimeoutExpired:code=124
    results.append({'check':name,'passed':code==0,'exitCode':code,'seconds':round(time.monotonic()-begin,2),'log':log.relative_to(ROOT).as_posix()})
    print(f'{name}: {"PASS" if code==0 else "FAIL"} ({results[-1]["seconds"]}s)',flush=True)
    if code:print(f'Inspect {log} for details.',flush=True)
(OUT/'verification.json').write_text(json.dumps({'checks':results,'scope':'Host checks only; physical devices, speech services and Amazon store transactions are not certified.'},indent=2)+'\n',encoding='utf-8')
raise SystemExit(0 if all(r['passed'] for r in results) else 1)
