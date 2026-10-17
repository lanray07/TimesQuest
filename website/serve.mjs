import http from 'node:http';
import fs from 'node:fs';
import path from 'node:path';
import {fileURLToPath} from 'node:url';
const types={'.html':'text/html; charset=utf-8','.css':'text/css; charset=utf-8','.js':'text/javascript; charset=utf-8','.png':'image/png','.svg':'image/svg+xml','.xml':'application/xml; charset=utf-8','.txt':'text/plain; charset=utf-8','.json':'application/json'};
export function createPreviewServer(directory){
  const root=fs.realpathSync(directory);
  return http.createServer((req,res)=>{
    res.setHeader('X-Content-Type-Options','nosniff');res.setHeader('Cache-Control','no-store');
    res.setHeader('Content-Security-Policy',"default-src 'self'; img-src 'self' data:; script-src 'self'; style-src 'self' 'unsafe-inline'; object-src 'none'; base-uri 'none'; frame-ancestors 'none'");
    if(!['GET','HEAD'].includes(req.method)){res.writeHead(405,{'Allow':'GET, HEAD'}).end();return;}
    let route;try{route=decodeURIComponent(new URL(req.url,'http://localhost').pathname);}catch{res.writeHead(400).end('Invalid URL');return;}
    if(route.includes('\\')||route.includes('\0')||route.split('/').some(part=>part.startsWith('.'))){res.writeHead(403).end('Forbidden');return;}
    let file=path.resolve(root,'.'+route);
    if(file!==root&&!file.startsWith(root+path.sep)){res.writeHead(403).end('Forbidden');return;}
    try{
      if(fs.statSync(file).isDirectory())file=path.join(file,'index.html');
      const real=fs.realpathSync(file);if(!real.startsWith(root+path.sep)){res.writeHead(403).end('Forbidden');return;}
      const bytes=fs.readFileSync(real);res.writeHead(200,{'Content-Type':types[path.extname(real)]||'application/octet-stream','Content-Length':bytes.length});res.end(req.method==='HEAD'?undefined:bytes);
    }catch(error){
      if(!['ENOENT','ENOTDIR'].includes(error.code)){res.writeHead(500).end('Unable to read this page');return;}
      res.writeHead(404,{'Content-Type':'text/html; charset=utf-8'});const fallback=path.join(root,'404.html');res.end(req.method==='HEAD'?undefined:fs.existsSync(fallback)?fs.readFileSync(fallback):'Page not found');
    }
  });
}
if(process.argv[1]&&path.resolve(process.argv[1])===fileURLToPath(import.meta.url))createPreviewServer(path.resolve('dist')).listen(4173,'127.0.0.1',()=>console.log('Local: http://127.0.0.1:4173'));
