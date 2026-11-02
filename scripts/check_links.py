#!/usr/bin/env python3
"""Check markdown links across the repository.

1. Every relative link [text](path) must point to an existing file/folder.
2. Every LeetCode / CSES / AtCoder URL must appear in scripts/verified-problems.tsv
   (the catalog of problems whose number, title and difficulty were verified live).

Usage: python3 scripts/check_links.py
"""
import pathlib, re, sys

ROOT = pathlib.Path(__file__).resolve().parent.parent
catalog = {line.split("\t")[4].strip().rstrip("/")
           for line in (ROOT / "scripts/verified-problems.tsv").read_text().splitlines()[1:]}
LINK = re.compile(r"\[[^\]]*\]\(([^)\s]+)\)")
JUDGE = re.compile(r"https?://(?:leetcode\.com/problems/[\w-]+|cses\.fi/problemset/task/\d+|atcoder\.jp/contests/dp/tasks/dp_[a-z])/?")

broken, unverified, count = [], [], 0
for md in sorted(ROOT.rglob("*.md")):
    if ".git" in md.parts:
        continue
    text = md.read_text(encoding="utf-8")
    text_no_code = re.sub(r"```.*?```", "", text, flags=re.S)
    for target in LINK.findall(text_no_code):
        count += 1
        if re.match(r"^(https?:|mailto:|#)", target):
            continue
        path = target.split("#")[0]
        if path and not (md.parent / path).exists():
            broken.append((md.relative_to(ROOT), target))
    for url in JUDGE.findall(text):
        if url.rstrip("/") not in catalog:
            unverified.append((md.relative_to(ROOT), url))

print(f"Links checked: {count}  Broken: {len(broken)}  Unverified judge URLs: {len(unverified)}")
for f, t in broken:
    print(f"BROKEN      {f} -> {t}")
for f, u in unverified:
    print(f"UNVERIFIED  {f} -> {u}")
sys.exit(1 if broken or unverified else 0)
