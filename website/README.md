# Local marketing website

No deployment has been performed. The site is independent of the Android runtime and uses Node.js built-ins with no npm dependencies.

```powershell
cd website
npm run build
npm test
npm run dev
```

Open http://127.0.0.1:4173. `dist/` contains 20 static HTML pages, local artwork, an interactive array example, canonical links, en-GB language annotations, sitemap.xml and robots.txt. One substantive table guide covers all twelve tables with distinct strategies and all 144 correct facts. Eight educational articles have primary NCETM references and are marked editorial drafts until educator review. The array tool is a website example, not an APK screenshot.

The current local build has `noindex,nofollow` and robots `Disallow: /`. For a reviewed public build, copy `release.config.example.json` to the ignored `release.config.json` and supply the cleared name decision, real publisher/support/privacy contacts, HTTPS origin, nonempty education/privacy review evidence files and an approved privacy policy file identifying that publisher and privacy contact. Paths are relative to this website directory. Set `PUBLISH_READY=true` only after review; `TIMESQUEST_WEBSITE_CONFIG` optionally selects a different config file. The build rejects missing facts or evidence. Add the genuine public Amazon listing URL once available. `SITE_URL` only overrides the development origin. Inspect the resulting public copy, canonical/sitemap URLs and internal links before deployment. A successful build is not legal or educational approval.

The site has WebSite, SoftwareApplication/MobileApplication, BreadcrumbList and visible FAQPage JSON-LD. Reviewed production configuration supplies the real publisher Organization and visible contact details; development builds invent none. There is no Offer or aggregateRating because there is no live listing or verified rating. Consequently this is not claimed to qualify for Google's software-app rich results. Use [Google's actual requirements](https://developers.google.com/search/docs/appearance/structured-data/software-app) and test the final public URLs when available. Valid JSON-LD alone does not establish rich-result eligibility, ranking, or AI-search inclusion.

The loopback preview server rejects malformed URLs, traversal/hidden paths and unsupported methods, serves a genuine 404 response, supports HEAD and sends CSP/nosniff headers. Nine automated site tests cover generated content, all 144 interactive arrays, input recovery, release configuration and preview routing. Production hosting still needs its own header and redirect configuration.

With explicit deployment authorisation, upload `dist/` to the selected static host, or use the Sites workflow to register and package this local checkout. Configure HTTPS, headers, canonical redirects, a true 404 page and the host's access log retention. Keep language annotations limited to actually reviewed pages. No speculative AI metadata or llms.txt visibility promise is included.
