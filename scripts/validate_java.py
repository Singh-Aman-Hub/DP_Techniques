#!/usr/bin/env python3
"""Compile and run every Java solution folder in the repository.

Each folder containing .java files is compiled in isolation (so the many
`Tabulation.java` files never clash), then every class with a main method is
executed. A solution passes when it exits with code 0 and prints no line
containing "FAIL".

Usage: python3 scripts/validate_java.py [path ...]   (default: whole repo)
"""
import pathlib, subprocess, sys, tempfile

ROOT = pathlib.Path(__file__).resolve().parent.parent
targets = [pathlib.Path(p).resolve() for p in sys.argv[1:]] or [ROOT]

dirs = sorted({f.parent for t in targets for f in (t.rglob("*.java") if t.is_dir() else [t])})
failures, ran = [], 0
for d in dirs:
    files = sorted(d.glob("*.java"))
    with tempfile.TemporaryDirectory() as out:
        c = subprocess.run(["javac", "-encoding", "UTF-8", "-d", out] + [str(f) for f in files],
                           capture_output=True, text=True)
        if c.returncode != 0:
            failures.append((d, "COMPILE", c.stderr[-2000:]))
            continue
        for f in files:
            if "static void main" not in f.read_text(encoding="utf-8"):
                continue
            ran += 1
            try:
                r = subprocess.run(["java", "-Xss64m", "-cp", out, f.stem],
                                   capture_output=True, text=True, timeout=60)
                bad = r.returncode != 0 or any("FAIL" in line for line in r.stdout.splitlines())
                if bad:
                    failures.append((f, "RUN", (r.stdout + r.stderr)[-2000:]))
            except subprocess.TimeoutExpired:
                failures.append((f, "TIMEOUT", ">60s"))

rel = lambda p: p.relative_to(ROOT) if p.is_relative_to(ROOT) else p
print(f"Folders: {len(dirs)}  Programs run: {ran}  Failures: {len(failures)}")
for path, kind, msg in failures:
    print(f"\n[{kind}] {rel(path)}\n{msg}")
sys.exit(1 if failures else 0)
