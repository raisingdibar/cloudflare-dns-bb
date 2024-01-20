# cloudflare-dns-bb

## How-To

Add a `.env.edn` file next to `list_dns.clj` with your API keys:
```
{:zone "ZONE_ID_HERE",
 :token "API_TOKEN_HERE"}
```

Then start up a Babashka REPL in Calva and run comment blocks as necessary.