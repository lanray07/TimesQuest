"""Bounded, non-redirecting development translation transport. Never logs credentials."""
import json
import urllib.error
import urllib.parse
import urllib.request

MAX_RESPONSE_BYTES=1_048_576

class NoRedirect(urllib.request.HTTPRedirectHandler):
    def redirect_request(self,request,fp,code,message,headers,new_url):
        raise urllib.error.HTTPError(request.full_url,code,'Translation redirects are disabled',headers,fp)

def translate(endpoint,key,body):
    parsed=urllib.parse.urlsplit(endpoint)
    if parsed.scheme!='https' or not parsed.hostname or parsed.username or parsed.password or parsed.fragment:
        raise ValueError('Use an HTTPS translation endpoint without URL credentials or fragments')
    request=urllib.request.Request(endpoint,data=json.dumps(body).encode('utf-8'),headers={'Authorization':f'Bearer {key}','Content-Type':'application/json; charset=utf-8'})
    opener=urllib.request.build_opener(NoRedirect())
    with opener.open(request,timeout=30) as response:
        raw=response.read(MAX_RESPONSE_BYTES+1)
    if len(raw)>MAX_RESPONSE_BYTES:raise ValueError('Translation response exceeds the configured size limit')
    decoded=json.loads(raw.decode('utf-8'))
    if not isinstance(decoded,dict) or not isinstance(decoded.get('strings'),dict):raise ValueError('Invalid translation dictionary')
    return {'strings':decoded['strings']}
