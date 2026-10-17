"""Create a private local review bundle of built APKs, truthful status reports and original store icons."""
from pathlib import Path
import hashlib,json,zipfile
ROOT=Path(__file__).resolve().parents[1]
out=ROOT/'artifacts/delivery';out.mkdir(parents=True,exist_ok=True)
files=[]
for folder in ['app/build/outputs/apk','docs','marketing/icons','marketing/store','marketing/screenshots/sources']:
    for path in (ROOT/folder).rglob('*'):
        if path.is_file() and path.suffix in ['.apk','.md','.json','.svg','.png','.xml']:files.append(path)
manifest={'status':'PRIVATE REVIEW BUNDLE — not approved for submission','files':[{'path':p.relative_to(ROOT).as_posix(),'sha256':hashlib.sha256(p.read_bytes()).hexdigest()} for p in sorted(files)]}
target=out/'TimesQuest-review.zip'
with zipfile.ZipFile(target,'w',zipfile.ZIP_DEFLATED) as archive:
    archive.writestr('REVIEW-MANIFEST.json',json.dumps(manifest,indent=2))
    for path in sorted(files):archive.write(path,path.relative_to(ROOT).as_posix())
print(f'Created {target} with {len(files)} files. No private keys, account credentials, environment files or child data are included.')
