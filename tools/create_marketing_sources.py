"""Generate editable SVG composition sources. Missing gameplay is visibly blocked, never mocked."""
from pathlib import Path
import json,html
ROOT=Path(__file__).resolve().parents[1]
manifest=json.loads((ROOT/'marketing/screenshots/manifest.json').read_text())
out=ROOT/'marketing/screenshots/sources';out.mkdir(parents=True,exist_ok=True)
for variant,w,h in [('tablet',1920,1200),('phone',720,1280)]:
    for shot in manifest['shots']:
        # SVG source is a composition blueprint, not an exported store screenshot.
        title=html.escape(shot['headline']);support=html.escape(shot['support']);font=72 if variant=='tablet' else 32
        svg=f'''<svg xmlns="http://www.w3.org/2000/svg" width="{w}" height="{h}" viewBox="0 0 {w} {h}"><rect width="100%" height="100%" fill="#faf7ec"/><text x="5%" y="7%" fill="#1b6960" font-family="sans-serif" font-size="22">TIMESQUEST · EDITABLE SOURCE · CAPTURE REQUIRED</text><text x="5%" y="15%" fill="#193e42" font-family="sans-serif" font-weight="bold" font-size="{font}" textLength="{w*.9}" lengthAdjust="spacingAndGlyphs">{title}</text><text x="5%" y="21%" fill="#506562" font-family="sans-serif" font-size="{font*.45}" textLength="{w*.9}" lengthAdjust="spacingAndGlyphs">{support}</text><rect x="5%" y="27%" width="90%" height="67%" rx="20" fill="#e7eddf" stroke="#1b6960" stroke-dasharray="12 12"/><text x="50%" y="58%" text-anchor="middle" fill="#193e42" font-family="sans-serif" font-size="{font*.5}">Authentic {variant} gameplay capture required</text><desc>Replace the marked region with {shot['id']} from an approved real-device capture. Review text sizing and wrapping before export. Do not submit this blueprint.</desc></svg>'''
        (out/f'{shot["id"]}-{variant}.svg').write_text(svg,encoding='utf-8')
print('Created 16 editable screenshot blueprints; none is a gameplay capture.')
