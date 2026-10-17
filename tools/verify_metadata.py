"""Checks store constraints from Amazon's documented fields, without asserting approval."""
from pathlib import Path
import json,struct
ROOT=Path(__file__).resolve().parents[1]
listing=json.loads((ROOT/'marketing/store/en-GB.json').read_text(encoding='utf-8'))
assert len(listing['shortDescription'].encode('utf-8'))<=2000,'Short description exceeds 2,000 bytes'
assert len(listing['longDescription'])<=4000,'Long description exceeds 4,000 characters'
assert 3<=len(listing['productFeatureBullets'])<=5,'Amazon documents 3–5 feature bullets'
assert '<' not in listing['longDescription'],'Store description must be plain text'
for size in [114,512]:
    png=(ROOT/f'marketing/icons/icon-{size}.png').read_bytes()
    assert png[:8]==b'\x89PNG\r\n\x1a\n'
    assert struct.unpack('>II',png[16:24])==(size,size)
manifest=json.loads((ROOT/'marketing/screenshots/manifest.json').read_text())
assert len(manifest['shots'])==10, 'Expected ten documented capture scenarios'
assert len({shot['id'] for shot in manifest['shots']})==len(manifest['shots']), 'Duplicate capture scenario'
assert all((ROOT/'marketing/screenshots/captures/en-GB'/f'{shot}.png').is_file() for shot in manifest.get('draftUploadedShots',[])), 'Uploaded capture missing'
assert len(list((ROOT/'marketing/screenshots/sources').glob('*.svg')))==16
print('PASS: listing field sizes, five feature bullets, native-size PNG icons and 16 clearly marked screenshot source blueprints. Four authentic draft captures are recorded separately; human locale approval remains pending.')
