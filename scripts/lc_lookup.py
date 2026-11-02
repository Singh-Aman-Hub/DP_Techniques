#!/usr/bin/env python3
"""Look up LeetCode problem metadata live from LeetCode's public GraphQL API.

Usage:  python3 scripts/lc_lookup.py <slug> [<slug> ...]
Output: id<TAB>title<TAB>difficulty<TAB>paidOnly<TAB>url   (or NOT_FOUND<TAB>slug)
"""
import json, sys, time, urllib.request

QUERY = "query q($s:String!){question(titleSlug:$s){questionFrontendId title difficulty isPaidOnly}}"

def lookup(slug):
    body = json.dumps({"query": QUERY, "variables": {"s": slug}}).encode()
    req = urllib.request.Request("https://leetcode.com/graphql", data=body, headers={
        "Content-Type": "application/json", "Referer": "https://leetcode.com",
        "User-Agent": "Mozilla/5.0"})
    for attempt in range(3):
        try:
            with urllib.request.urlopen(req, timeout=20) as r:
                return json.load(r)["data"]["question"]
        except Exception:
            time.sleep(1 + attempt)
    return "ERROR"

if __name__ == "__main__":
    for slug in sys.argv[1:]:
        q = lookup(slug)
        if q == "ERROR":
            print(f"ERROR\t{slug}")
        elif not q:
            print(f"NOT_FOUND\t{slug}")
        else:
            print(f"{q['questionFrontendId']}\t{q['title']}\t{q['difficulty']}\t{q['isPaidOnly']}\thttps://leetcode.com/problems/{slug}/")
        time.sleep(0.25)
