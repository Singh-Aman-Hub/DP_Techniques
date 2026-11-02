# Edit Distance

> **Module:** Subsequence and Knapsack DP · **Difficulty:** Intermediate · **Estimated time:** 120–150 min

## 1. What is this topic?

How many single-character edits does it take to turn one word into another, if an edit is **insert** a character, **delete** a character, or **replace** a character? Turning `"horse"` into `"ros"` takes 3 (replace `h`→`r`, delete `r`, delete `e`). This number is called the **edit distance** or **Levenshtein distance**.

The DP table is the same `(n+1) × (m+1)` prefix table as [LCS](../12-longest-common-subsequence/), but now a mismatch has **three** neighbours to choose from — one per operation — and the empty-prefix row and column are no longer zero.

## 2. Why does this concept exist?

Measuring how "far apart" two strings are is a basic need: spell checkers suggest words with a small edit distance, search engines tolerate typos, biologists measure mutations between DNA strands, and `diff`-like tools count changes. Brute-forcing all edit sequences is hopeless; the DP solves it in O(n·m).

It is also the best exercise for the most important skill in string DP: **translating each allowed operation into a move between neighbouring cells of the table**.

## 3. What problem does it solve?

[LeetCode 72 — Edit Distance](https://leetcode.com/problems/edit-distance/) and its identical twin [CSES 1639 — Edit Distance](https://cses.fi/problemset/task/1639), plus restricted versions such as [Delete Operation for Two Strings](https://leetcode.com/problems/delete-operation-for-two-strings/) (deletions only) and [Minimum ASCII Delete Sum for Two Strings](https://leetcode.com/problems/minimum-ascii-delete-sum-for-two-strings/) (weighted deletions).

## 4. Prerequisites

- [Longest Common Subsequence](../12-longest-common-subsequence/) — the prefix table and the "compare last characters" habit.
- [Minimum Path Sum](../../02-two-dimensional-and-grid-dp/04-minimum-path-sum/) — min over neighbouring cells in a grid.
- [State Transitions and Base Cases](../../00-foundations/10-state-transitions-and-base-cases/) — non-zero base rows.

## 5. What you will learn

- The state `dist(i, j)` on prefixes and what each operation does to it.
- Why replace ↔ diagonal, delete ↔ up, insert ↔ left.
- What the base row (`dist(0, j) = j`) and base column (`dist(i, 0) = i`) mean.
- A full table dry run for `"horse"` → `"ros"`, and how to list the actual operations by walking back.
- Two-row space optimization.

## 6. Important terminology

| Term | Meaning in plain English |
|---|---|
| Edit operation | Insert one character, delete one character, or replace one character with another |
| Edit (Levenshtein) distance | The minimum number of edit operations to turn `word1` into `word2` |
| Source / target | `word1` (we edit it) / `word2` (what we want to obtain) |
| Prefix `word1[0..i)` | The first `i` characters of `word1` |
| Alignment | A way of lining up the two strings column by column, using gaps for insertions/deletions |
| Free match | Equal last characters: no operation needed, move diagonally at cost 0 |

## 7. Where this is used in real problems

- Spell checking and "did you mean…?" suggestions.
- Fuzzy search and record de-duplication (names with typos).
- DNA/protein alignment (with operation-specific costs).
- Variants: deletions only ([LC 583](https://leetcode.com/problems/delete-operation-for-two-strings/)), weighted deletions ([LC 712](https://leetcode.com/problems/minimum-ascii-delete-sum-for-two-strings/)), and pattern matching with `?`/`*` ([Wildcard Matching](https://leetcode.com/problems/wildcard-matching/), in [DP on Strings](../../04-advanced-dp-patterns/01-dp-on-strings/)).

## 8. After completing this topic you should be able to…

- [ ] State `dist(i, j)` precisely and give its base row and column with reasons.
- [ ] Explain, for each operation, which neighbouring cell it leads to and why.
- [ ] Fill the `"horse"` / `"ros"` table by hand and get 3.
- [ ] Walk back through the table and list a valid sequence of operations.
- [ ] Write the two-row version, remembering to reset `current[0] = i` each row.
- [ ] Adapt the recurrence when only some operations are allowed or costs differ.

## 9. Practice problems

| # | Problem | Platform/ID | Difficulty | Pattern tested | Why it belongs here |
|---|---|---|---|---|---|
| 1 | [Delete Operation for Two Strings](https://leetcode.com/problems/delete-operation-for-two-strings/) | LeetCode 583 | Medium | Edit distance with deletions only | Remove the diagonal "replace" option; or use `n + m − 2·LCS` |
| 2 | [Edit Distance](https://leetcode.com/problems/edit-distance/) | LeetCode 72 | Medium | Three-operation prefix DP | The topic problem itself |
| 3 | [Edit Distance](https://cses.fi/problemset/task/1639) | CSES 1639 | – | Same DP, strings up to 5000 | Practise the O(m) memory version and fast input |
| 4 | [Minimum ASCII Delete Sum for Two Strings](https://leetcode.com/problems/minimum-ascii-delete-sum-for-two-strings/) | LeetCode 712 | Medium | Weighted deletions | Base row/column become prefix sums of ASCII codes |
| 5 | [Interleaving String](https://leetcode.com/problems/interleaving-string/) | LeetCode 97 | Medium | Boolean prefix DP over two strings | Same table and neighbour moves, with `||` instead of `min` |

## 10. Navigation

⬅ Previous: [Longest Palindromic Subsequence](../13-longest-palindromic-subsequence/) · ➡ Next: [Distinct Subsequences](../15-distinct-subsequences/) · 📖 [Walkthrough](walkthrough.md) · 💻 [Solutions](solution/)
