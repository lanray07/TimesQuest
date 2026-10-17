"""Rasterise the original TimesQuest geometric icon at native store sizes."""
from pathlib import Path
from PIL import Image,ImageDraw
ROOT=Path(__file__).resolve().parents[1]
out=ROOT/'marketing/icons';out.mkdir(parents=True,exist_ok=True)
svg='''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 108 108"><rect width="108" height="108" fill="#143d43"/><path fill="#f5c55d" d="M54 12L65 40L95 43L72 63L79 94L54 78L29 94L36 63L13 43L43 40Z"/><path fill="#143d43" d="M40 36L54 50L68 36L74 42L60 56L74 70L68 76L54 62L40 76L34 70L48 56L34 42Z"/></svg>'''
(out/'icon.svg').write_text(svg)
star=[(54,12),(65,40),(95,43),(72,63),(79,94),(54,78),(29,94),(36,63),(13,43),(43,40)]
cross=[(40,36),(54,50),(68,36),(74,42),(60,56),(74,70),(68,76),(54,62),(40,76),(34,70),(48,56),(34,42)]
for size in [114,512]:
    # Draw each size at 4x resolution from vector geometry, then antialias.
    n=size*4;s=n/108;im=Image.new('RGBA',(n,n),'#143d43');d=ImageDraw.Draw(im)
    d.polygon([(x*s,y*s) for x,y in star],fill='#f5c55d');d.polygon([(x*s,y*s) for x,y in cross],fill='#143d43')
    im.resize((size,size),Image.Resampling.LANCZOS).save(out/f'icon-{size}.png')
print('Exported original 114px and 512px RGBA icons from vector geometry')
