import fs from 'node:fs';
import path from 'node:path';

export function validateOrigin(value, production=false) {
  const url=new URL(value);
  if(!['http:','https:'].includes(url.protocol)||url.username||url.password||url.search||url.hash||url.pathname!=='/')throw Error('SITE_URL must be an HTTP(S) origin without credentials, path, query or fragment');
  if(production&&url.protocol!=='https:')throw Error('Public builds require HTTPS');
  return url.origin;
}
export function loadReleaseConfig(env, root) {
  const production=env.PUBLISH_READY==='true';
  if(!production)return {production:false,origin:validateOrigin(env.SITE_URL||'http://127.0.0.1:4173')};
  const file=path.resolve(env.TIMESQUEST_WEBSITE_CONFIG||path.join(root,'release.config.json'));
  const config=JSON.parse(fs.readFileSync(file,'utf8'));
  const origin=validateOrigin(config.origin,true);
  for(const key of ['publisherName','supportEmail','privacyEmail'])if(typeof config[key]!=='string'||!config[key].trim())throw Error(`Public build requires ${key}`);
  for(const key of ['supportEmail','privacyEmail'])if(!/^[^\s@<>"']+@[^\s@<>"']+\.[^\s@<>"']+$/.test(config[key]))throw Error(`Invalid ${key}`);
  if(config.nameCleared!==true)throw Error('The working name requires clearance before a public build');
  for(const key of ['privacyReviewEvidence','educationReviewEvidence','privacyPolicyFile']){
    if(typeof config[key]!=='string'||!config[key])throw Error(`Public build requires ${key}`);
    const evidence=path.resolve(root,config[key]);
    if(!fs.statSync(evidence).isFile()||!fs.readFileSync(evidence,'utf8').trim())throw Error(`Empty review file: ${key}`);
  }
  const privacyText=fs.readFileSync(path.resolve(root,config.privacyPolicyFile),'utf8');
  if(!privacyText.includes(config.publisherName)||!privacyText.includes(config.privacyEmail))throw Error('Approved privacy text must identify the supplied publisher and privacy contact');
  let listing=null;
  if(config.amazonListingUrl){const url=new URL(config.amazonListingUrl);if(url.protocol!=='https:'||!(url.hostname==='amazon.com'||url.hostname.endsWith('.amazon.com')||url.hostname==='amazon.co.uk'||url.hostname.endsWith('.amazon.co.uk')))throw Error('Supply the genuine public Amazon listing URL');listing=url.href;}
  return {production:true,origin,publisherName:config.publisherName,supportEmail:config.supportEmail,privacyEmail:config.privacyEmail,privacyText,listing};
}
