import io,json,unittest
from unittest.mock import patch
import urllib.error
import urllib.request
import translation_transport

class TranslationTransportTests(unittest.TestCase):
    def test_cross_origin_redirect_never_forwards_a_bearer_token(self):
        handler=translation_transport.NoRedirect();request=urllib.request.Request('https://translator.example/v1',headers={'Authorization':'Bearer TEST-ONLY'})
        with self.assertRaises(urllib.error.HTTPError):handler.redirect_request(request,None,302,'Found',{},'https://other.example/collect')
    def test_url_credentials_and_non_https_endpoints_are_rejected(self):
        for url in ['http://translator.example/','https://user:pass@translator.example/','https://translator.example/#fragment']:
            with self.assertRaises(ValueError):translation_transport.translate(url,'TEST-ONLY',{})
    @patch('translation_transport.urllib.request.build_opener')
    def test_oversized_provider_response_is_rejected(self,opener):
        opener.return_value.open.return_value.__enter__.return_value.read.return_value=b'x'*(translation_transport.MAX_RESPONSE_BYTES+1)
        with self.assertRaises(ValueError):translation_transport.translate('https://translator.example/','TEST-ONLY',{})
    @patch('translation_transport.urllib.request.build_opener')
    def test_only_translated_strings_are_retained(self,opener):
        response={'strings':{'hello':'Bonjour'},'debug':'provider metadata is deliberately discarded'}
        opener.return_value.open.return_value.__enter__.return_value.read.return_value=json.dumps(response).encode()
        self.assertEqual({'strings':{'hello':'Bonjour'}},translation_transport.translate('https://translator.example/','TEST-ONLY',{}))
if __name__=='__main__':unittest.main()
