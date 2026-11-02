# Contributing Guide

This repository is a self-study course in Dynamic Programming (DP). Every new topic or problem must look and teach like the rest of the repository, so that a learner can move from one folder to the next without relearning how material is laid out.

Read this whole file before you add anything.

---

## 1. Guiding principles

1. **Teach, don't dump.** The reader is a beginner. Introduce every term before you use it. Start with intuition, then add notation.
2. **One concept per topic folder.** If a walkthrough needs two new ideas, split it into two topics.
3. **Never skip prerequisites.** Link to the earlier topic that introduced an idea instead of assuming the reader knows it.
4. **Show how to derive the solution.** Every problem must answer the seven derivation questions (Section 5) before any code appears.
5. **Use the same example across approaches.** Brute force, memoization, tabulation and space optimization of one topic must all be explained on the *same* input.
6. **Explain complexity; don't just state it.** "O(n) because each of the n states is computed once and does O(1) work."
7. **Be accurate.** Never invent problem numbers, titles, URLs or difficulties (see Section 7).
8. **Never copy problem statements.** Link to the original problem and write a short summary in your own words.

---

## 2. Folder layout

```
NN-module-name/
├── README.md                      # module overview: topic order, what each topic teaches
└── NN-topic-name/
    ├── README.md                  # introduction and learning guide (Section 3)
    ├── walkthrough.md             # the main lesson (Section 4)
    └── solution/
        ├── BruteForce.java        # plain recursion / exhaustive search
        ├── Memoization.java       # top-down DP
        ├── Tabulation.java        # bottom-up DP
        └── Optimized.java         # space- or time-optimized DP
```

Rules:

- Folder names are `kebab-case`, prefixed with a two-digit order number (`03-minimum-cost-climbing-stairs`).
- Create **only the solution files that make sense** for the topic. A topic about Convex Hull Trick may only need `Tabulation.java` and `Optimized.java`. A pure-concept topic may have a single `Demo.java`, or extra, clearly named files (for example `RecursionTreeTracer.java`).
- A topic that covers several closely related problems (for example the six stock problems) may use sub-folders such as `solution/stock-iii/` or class names such as `StockIIITabulation.java`. Keep every folder independently compilable.
- Practice problems in `06-leetcode-practice/` use the layout in Section 6.

---

## 3. `README.md` template (topic introduction)

Use these headings in this order:

```markdown
# <Topic Name>

> **Module:** <module name> · **Difficulty:** Beginner | Intermediate | Advanced · **Estimated time:** <e.g. 60–90 min>

## 1. What is this topic?
## 2. Why does this concept exist?
## 3. What problem does it solve?
## 4. Prerequisites
   (links to earlier topic folders)
## 5. What you will learn
## 6. Important terminology
   (table: Term | Meaning in plain English)
## 7. Where this is used in real problems
## 8. After completing this topic you should be able to…
   (checklist)
## 9. Practice problems
   (table, easiest first: # | Problem | Platform/ID | Difficulty | Pattern tested | Why it belongs here)
## 10. Navigation
   ⬅ Previous: <link> · ➡ Next: <link> · 📖 [Walkthrough](walkthrough.md) · 💻 [Solutions](solution/)
```

---

## 4. `walkthrough.md` template (the main lesson)

Use these sections, in this order. Skip one only when it truly does not apply, and say why in one line.

```markdown
# <Topic Name> — Walkthrough

## 1. Intuition (no code, no math — an everyday analogy)
## 2. Problem statement in our own words  (+ link to original, if any)
## 3. Examples (input → output, with an explanation)
## 4. Deriving the solution — the 7 questions   (see Section 5)
## 5. Brute force: recursion
   - code excerpt, recursion tree (ASCII), why it is slow
## 6. Memoization (top-down)
   - what is cached, dry run showing cache hits
## 7. Tabulation (bottom-up)
   - table filling order, ASCII table filled step by step
## 8. Space optimization
   - which rows/cells are actually needed, and why
## 9. Dry run on a full example (table of state values per step)
## 10. Complexity analysis (explained, for every approach)
## 11. Why this approach works (correctness argument in plain words)
## 12. Common mistakes
## 13. When to use this technique / how to recognize similar problems
## 14. Summary and revision notes (short bullet list + the recurrence in a box)
```

Diagrams: use ASCII in fenced code blocks. Tables: use markdown tables.

---

## 5. The seven derivation questions (mandatory for every problem)

Every walkthrough and every practice problem answers these, in this order, **before** any optimized code appears:

1. **What is changing?** Which quantities shrink or move as we make decisions (index, remaining capacity, position in the grid…)?
2. **What does the state represent?** Write `dp[i] = …` or `f(i, j) = …` as a complete English sentence.
3. **What are the choices?** At each state, list every decision (take/skip, move right/down…).
4. **What is the recurrence?** Combine the choices (`max`, `min`, `+`, `||`), and explain each term.
5. **What are the base cases?** The smallest states whose answers are known directly, and why.
6. **What is the order of computation?** Which states must be ready before the current one, and so which loop direction to use.
7. **Can the state space be optimized?** Which earlier states are actually used, so how many rows or variables we really need.

---

## 6. Practice problem layout (`06-leetcode-practice/`)

```
06-leetcode-practice/<NN-pattern>/<id>-<slug>/
├── README.md
└── solution/
    ├── Memoization.java
    ├── Tabulation.java
    └── Optimized.java          # when applicable
```

`README.md` headings: **Problem overview** (own words) · **Link & metadata** · **Examples** · **Brute-force intuition** · **Deriving the DP (7 questions)** · **Recursive solution** · **Memoization** · **Tabulation** · **Space optimization** · **Dry run** · **Complexity** · **Why the optimized approach is better** · **Common mistakes** · **Key takeaway** · **Related problems**.

When a practice problem is already taught in full inside a topic folder, do **not** duplicate it. Link to that topic instead.

---

## 7. Problem metadata — accuracy rules

- Every LeetCode / CSES / AtCoder problem referenced must appear in [`scripts/verified-problems.tsv`](scripts/verified-problems.tsv). Its rows were checked against the live sites.
- To add a new LeetCode problem, verify it first:
  ```bash
  python3 scripts/lc_lookup.py <problem-slug>      # prints id, exact title, difficulty, premium flag
  ```
  then append the row to `scripts/verified-problems.tsv`.
- Use the title **exactly** as LeetCode spells it, and the URL form `https://leetcode.com/problems/<slug>/`.
- Do not add premium-only problems to the core path. Learners may not be able to open them.
- `python3 scripts/check_links.py` fails if a judge URL is not in the catalog.

---

## 8. Java conventions

Every `.java` file must be **self-contained and runnable on its own**:

```java
/**
 * <Problem / topic> — <Approach name>
 *
 * Idea: <2–4 lines explaining the approach and why this DP technique is used>
 *
 * Time complexity:  O(...) — <one-line reason>
 * Space complexity: O(...) — <one-line reason>
 *
 * Run: java Memoization.java
 */
import java.util.Arrays;

public class Memoization {

    // memo[i] stores the answer for subproblem i; -1 means "not computed yet"
    private static long[] memo;

    static long solve(int n) { ... }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("n = 10", solve(10), 55);
        check("n = 0",  solve(0), 0);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
```

Rules:

- **No `package` declaration.** The class name matches the file name. Use only the standard library.
- Run with `java FileName.java` from the `solution/` folder (Java 11+ single-file launch). The repository is validated on Java 21.
- `main` runs **at least three test cases**, including an edge case, against hard-coded expected answers. It prints `PASS`/`FAIL` lines and exits with code 1 on any failure.
- Use meaningful names (`maxLoot`, `remainingCapacity`, not `a`, `b`, `x`). Loop variables `i`, `j`, `k` are fine when they mean array indices.
- Comment the *why*, not the obvious *what*. Every DP array gets a comment saying what one cell means.
- Avoid clever syntax: no streams in core DP logic, no lambdas where a loop is clearer, no bit tricks without an explanation.
- Brute-force solutions must still finish quickly. Test them only on small inputs.

Validate everything before committing:

```bash
python3 scripts/validate_java.py            # compiles each folder in isolation and runs every main
python3 scripts/check_links.py              # relative links + verified judge URLs
```

---

## 9. Adding a new topic: checklist

- [ ] Folder created in the right module with the next order number
- [ ] `README.md` follows Section 3, with prerequisites and next topics linked
- [ ] `walkthrough.md` follows Section 4 and answers the seven questions
- [ ] Solution files are self-checking and pass `validate_java.py`
- [ ] Practice problems are in the catalog, ordered easiest → hardest
- [ ] Module `README.md`, root [`ROADMAP.md`](ROADMAP.md), [`PROGRESS_TRACKER.md`](PROGRESS_TRACKER.md) and [`LEETCODE_TRACKER.md`](LEETCODE_TRACKER.md) updated
- [ ] `check_links.py` passes
