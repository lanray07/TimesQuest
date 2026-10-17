"""Development-only translation adapter and fail-closed release validator.

Provider contract: POST {sourceLocale,targetLocale,strings,glossary,protectedTerms},
Bearer authentication; response {strings: {key: translatedValue}}. No child data.
"""
from pathlib import Path
import argparse, hashlib, json, os, re, urllib.parse
from translation_transport import translate
ROOT=Path(__file__).resolve().parents[1]
CONFIG=json.loads((ROOT/'localization/config.json').read_text(encoding='utf-8'))
SOURCE=json.loads((ROOT/CONFIG['source']).read_text(encoding='utf-8'))
PACKDIR=ROOT/'localization/packs'
def write(path, value):
    path.parent.mkdir(parents=True,exist_ok=True)
    temporary=path.with_suffix(path.suffix+'.tmp')
    temporary.write_text(json.dumps(value,ensure_ascii=False,indent=2)+'\n',encoding='utf-8')
    os.replace(temporary,path)
def placeholders(value): return sorted(re.findall(r'\{[\w]+\}|%\d*\$?[sdf]',value))
def validate(locale):
    path=PACKDIR/f'{locale}.json'
    pack=SOURCE if locale=='en-GB' else json.loads(path.read_text(encoding='utf-8')) if path.exists() else {}
    return validate_pack(pack)

def validate_pack(pack):
    if not isinstance(pack,dict):return ['Expected a string dictionary']
    errors=[]
    for key, original in SOURCE.items():
        translated=pack.get(key)
        if not isinstance(translated,str) or not translated.strip(): errors.append(f'{key}: missing');continue
        if placeholders(original)!=placeholders(translated):errors.append(f'{key}: placeholders changed')
        for term in CONFIG['protectedTerms']:
            if term in original and term not in translated:errors.append(f'{key}: protected term {term} changed')
    errors.extend(f'{key}: unknown key' for key in pack if key not in SOURCE)
    return errors
def source_hash():return hashlib.sha256(json.dumps(SOURCE,sort_keys=True).encode()).hexdigest()
def pack_hash(locale):
    pack=SOURCE if locale=='en-GB' else json.loads((PACKDIR/f'{locale}.json').read_text(encoding='utf-8'))
    return hashlib.sha256(json.dumps(pack,sort_keys=True).encode()).hexdigest()

def review_errors(locale,domain):
    approval=ROOT/'localization/reviews'/domain/f'{locale}.json'
    review=json.loads(approval.read_text(encoding='utf-8')) if approval.exists() else {}
    errors=[]
    if not isinstance(review.get('reviewer'),str) or not review.get('reviewer','').strip():errors.append('A named human reviewer is required')
    for field in ['education','narration','gameplay','voice','screenshots']:
        value=review.get(field);evidence=(ROOT/value).resolve() if isinstance(value,str) and value else None
        if evidence is None or not evidence.is_relative_to(ROOT.resolve()) or not evidence.is_file() or evidence.stat().st_size==0:errors.append(f'{field}: an existing review evidence file within the repo is required')
    if review.get('sourceSha256')!=source_hash():errors.append('Review does not match current source hash: '+source_hash())
    if review.get('packSha256')!=pack_hash(locale):errors.append('Review does not match current translation hash: '+pack_hash(locale))
    return errors,review

def main():
    global SOURCE, PACKDIR
    p=argparse.ArgumentParser();p.add_argument('command',choices=['extract','scaffold','validate','translate','release-check','stage']);p.add_argument('--locale',choices=CONFIG['targets']);p.add_argument('--domain',choices=['gameplay','marketing'],default='gameplay');args=p.parse_args()
    if args.domain=='marketing':
        listing=json.loads((ROOT/'marketing/store/en-GB.json').read_text(encoding='utf-8'))
        shots=json.loads((ROOT/'marketing/screenshots/manifest.json').read_text(encoding='utf-8'))['shots']
        SOURCE={key:listing[key] for key in ['displayTitle','shortDescription','longDescription','releaseNotes']}
        for i,feature in enumerate(listing['productFeatureBullets']):SOURCE[f'feature_{i}']=feature
        for shot in shots:
            SOURCE[shot['id']+'_headline']=shot['headline'];SOURCE[shot['id']+'_support']=shot['support']
        PACKDIR=ROOT/'localization/packs/marketing'
    if args.command=='extract':
        keys=set()
        for path in (ROOT/'app/src/main/java').rglob('*.java'): keys.update(re.findall(r'(?:text|get|format)\(\s*"([a-z_0-9]+)"\s*[,)]',path.read_text(encoding='utf-8')))
        if args.domain=='marketing':keys=set(SOURCE)
        # Dynamic mode/story keys are already in the source dictionary.
        missing=sorted(keys-SOURCE.keys());write(ROOT/'localization/reports/extraction.json',{'sourceKeys':len(SOURCE),'literalReferences':sorted(keys),'missingReferences':missing,'runtimeMathCatalogued':True,'remainingNotes':['Protected world and character names remain original proper nouns','Only UK English ships in release; US English is a debug preview','Additional packs require reviewed voice vocabulary and device evidence']});return int(bool(missing))
    if args.command=='scaffold':
        for locale in CONFIG['targets']:
            target=PACKDIR/f'{locale}.json'
            existing=json.loads(target.read_text(encoding='utf-8')) if target.exists() else {}
            write(target,SOURCE if locale=='en-GB' else {**{key:None for key in SOURCE},**existing})
        return 0
    if args.command=='translate':
        if not args.locale or args.locale=='en-GB':p.error('Select a target locale other than en-GB')
        endpoint=os.environ.get(CONFIG['api']['endpointEnv'],'');key=os.environ.get(CONFIG['api']['keyEnv'],'')
        if urllib.parse.urlparse(endpoint).scheme!='https' or not key:raise SystemExit('Set the development-only HTTPS translation endpoint and key in environment variables. Never put them in app resources.')
        request={'sourceLocale':'en-GB','targetLocale':args.locale,'strings':SOURCE,'glossary':json.loads((ROOT/'localization/glossary.json').read_text(encoding='utf-8')),'protectedTerms':CONFIG['protectedTerms']}
        digest=hashlib.sha256((json.dumps(request,sort_keys=True)+endpoint).encode()).hexdigest();cache=ROOT/'localization/cache'/f'{digest}.json'
        if cache.exists():response=json.loads(cache.read_text(encoding='utf-8'))
        else:
            try:response=translate(endpoint,key,request)
            except Exception:raise SystemExit('Translation request failed. Check the provider locally; no credentials or response body have been logged.')
        issues=validate_pack(response.get('strings')) if isinstance(response,dict) else ['Invalid response']
        if issues:raise SystemExit(f'Translation rejected: {len(issues)} resource issues. Existing pack and cache were preserved.')
        write(cache,{'strings':response['strings']})
        write(PACKDIR/f'{args.locale}.json',response['strings'])
    locales=[args.locale] if args.locale else CONFIG['targets'];report={locale:validate(locale) for locale in locales};write(ROOT/f'localization/reports/{args.domain}-validation.json',report)
    for locale,errors in report.items():print(f'{locale}: {len(errors)} issues')
    if args.command in ['release-check','stage']:
        for locale in locales:report[locale].extend(review_errors(locale,args.domain)[0])
        write(ROOT/f'localization/reports/{args.domain}-release.json',report)
        for locale,errors in report.items():print(f'{locale}: '+('RELEASE BLOCKED' if errors else 'review evidence accepted')+f' ({len(errors)} issues)')
    if args.command=='stage':
        if args.domain!='gameplay' or not args.locale:p.error('Stage one reviewed gameplay locale using --locale')
        if any(report.values()):return 1
        _,review=review_errors(args.locale,args.domain)
        label=review.get('nativeLabel')
        if not isinstance(label,str) or not label.strip():raise SystemExit('Staging requires a nativeLabel in the human review record')
        pack=SOURCE if args.locale=='en-GB' else json.loads((PACKDIR/f'{args.locale}.json').read_text(encoding='utf-8'))
        write(ROOT/'app/src/main/assets/i18n'/f'{args.locale}.json',pack)
        index=ROOT/'app/src/main/assets/i18n/registry.json';registry=json.loads(index.read_text(encoding='utf-8'))
        registry['languages']=[item for item in registry['languages'] if item['tag']!=args.locale]+[{'tag':args.locale,'label':label,'reviewed':True,'sourceSha256':source_hash(),'packSha256':pack_hash(args.locale)}]
        write(index,registry);print(f'Staged reviewed language {args.locale}; no app was published.')
    return int(any(report.values()))
if __name__=='__main__':raise SystemExit(main())
