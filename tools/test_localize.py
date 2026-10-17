import unittest,tempfile,json
from pathlib import Path
from unittest.mock import patch
import localize

class LocaleValidationTests(unittest.TestCase):
    def setUp(self):
        self.tmp=tempfile.TemporaryDirectory();self.root=Path(self.tmp.name);(self.root/'localization/packs').mkdir(parents=True)
        self.source={'count':'Pip found {count} sparks','format':'TimesQuest %1$s'}
        self.patch=patch.multiple(localize,ROOT=self.root,SOURCE=self.source,PACKDIR=self.root/'localization/packs');self.patch.start()
    def tearDown(self):self.patch.stop();self.tmp.cleanup()
    def pack(self,data):(self.root/'localization/packs/fr.json').write_text(json.dumps(data),encoding='utf-8')
    def test_valid_placeholders_and_terms(self):self.pack({'count':'Pip a trouvé {count} étincelles','format':'TimesQuest %1$s'});self.assertEqual([],localize.validate('fr'))
    def test_missing_strings_are_reported(self):self.pack({'count':None});self.assertEqual(2,len(localize.validate('fr')))
    def test_placeholder_corruption_is_rejected(self):self.pack({'count':'Pip {nombre}','format':'TimesQuest %1$s'});self.assertTrue(any('placeholders' in e for e in localize.validate('fr')))
    def test_brand_terms_are_protected(self):self.pack({'count':'Dragon {count}','format':'TimesQuest %1$s'});self.assertTrue(any('protected term Pip' in e for e in localize.validate('fr')))
    def test_unknown_source_keys_are_reported(self):self.pack({**self.source,'unknown':'x'});self.assertTrue(any('unknown key' in e for e in localize.validate('fr')))
    def approved_fixture(self):
        self.pack({'count':'Pip a trouvé {count} étincelles','format':'TimesQuest %1$s'})
        (self.root/'evidence.md').write_text('Unit-test fixture only; not a real review',encoding='utf-8')
        review={'reviewer':'Unit-test fixture','nativeLabel':'Français','sourceSha256':localize.source_hash(),'packSha256':localize.pack_hash('fr')}
        review.update({key:'evidence.md' for key in ['education','narration','gameplay','voice','screenshots']})
        folder=self.root/'localization/reviews/gameplay';folder.mkdir(parents=True)
        (folder/'fr.json').write_text(json.dumps(review),encoding='utf-8')
        return review,folder/'fr.json'
    def test_changed_translation_invalidates_previous_review(self):
        self.approved_fixture();self.assertEqual([],localize.review_errors('fr','gameplay')[0]);self.pack({'count':'Pip a découvert {count} étincelles','format':'TimesQuest %1$s'});self.assertTrue(any('translation hash' in e for e in localize.review_errors('fr','gameplay')[0]))
    def test_boolean_claim_is_not_review_evidence(self):
        review,path=self.approved_fixture();review['education']=True;path.write_text(json.dumps(review),encoding='utf-8');self.assertTrue(any('education:' in e for e in localize.review_errors('fr','gameplay')[0]))
if __name__=='__main__':unittest.main()
