# Shortest Common Supersequence (SCS)

> **Module:** Subsequence and Knapsack DP · **Difficulty:** Advanced · **Estimated time:** 90–120 min

## 1. What is this topic?

Given two strings, build the **shortest** string that contains **both** of them as subsequences. For `"abac"` and `"cab"` the answer has length 5, for example `"cabac"`: deleting characters from `"cabac"` can give `"abac"` (drop the first `c`) and also `"cab"` (drop the last `a`, `c`).

The length comes straight from the [LCS](../12-longest-common-subsequence/): characters of the LCS can be **shared**, everything else must be written separately, so `|SCS| = n + m − LCS`. The real work of this topic is **constructing the actual string** by walking back through the LCS table — the most demanding reconstruction in this module.

## 2. Why does this concept exist?

"Merge two sequences while keeping each one's internal order, with as little duplication as possible" is a natural task: merging two edit histories, two playlists, two schedules, or two DNA reads. It is also the cleanest example of a DP whose **output is an object, not a number**, which forces you to understand what every cell and every move in the table *means*.

## 3. What problem does it solve?

[LeetCode 1092 — Shortest Common Supersequence](https://leetcode.com/problems/shortest-common-supersequence/): return any shortest string that has both `str1` and `str2` as subsequences.

## 4. Prerequisites

- [Longest Common Subsequence](../12-longest-common-subsequence/) — the table and the backtracking walk, which we extend here.
- [Edit Distance](../14-edit-distance/) — a min-cost prefix recurrence with non-zero base row/column.
- [Distinct Subsequences](../15-distinct-subsequences/) — the previous topic in this module.

## 5. What you will learn

- What a supersequence is, and why `|SCS| = n + m − LCS`.
- A direct recurrence for the SCS length (and that it agrees with the formula).
- How to build the SCS string from the LCS table: shared characters once, the others in the right order, plus leftovers.
- Why the result is not unique, and how tie-breaking picks one.

## 6. Important terminology

| Term | Meaning in plain English |
|---|---|
| Supersequence of `x` | A string from which `x` can be obtained by deleting characters |
| Common supersequence | A string that is a supersequence of both inputs |
| Shortest common supersequence | A common supersequence of minimum length (there may be several) |
| Shared character | A character of the result that serves both strings at once; shared characters form a common subsequence |
| Leftover prefix | When one string is fully used during the walk back, the remaining prefix of the other string is copied as-is |
| Superstring (contrast) | Contains each input as a **substring** — a different, much harder problem ([LC 943](https://leetcode.com/problems/find-the-shortest-superstring/)) |

## 7. Where this is used in real problems

- Merging two ordered lists with minimum total length (version histories, schedules).
- Sequence assembly heuristics in bioinformatics (pairwise merges).
- Interview "Hard" problems that are really "LCS + careful reconstruction".

## 8. After completing this topic you should be able to…

- [ ] Explain both directions of `|SCS| = n + m − LCS`.
- [ ] Write the direct length recurrence and its base cases.
- [ ] Walk back through the LCS table of `"abac"` / `"cab"` and produce `"cabac"`.
- [ ] Explain what to write at each move (diagonal, up, left) and why the leftovers are needed.
- [ ] Verify your output: correct length, and both inputs are subsequences of it.

## 9. Practice problems

| # | Problem | Platform/ID | Difficulty | Pattern tested | Why it belongs here |
|---|---|---|---|---|---|
| 1 | [Longest Common Subsequence](https://leetcode.com/problems/longest-common-subsequence/) | LeetCode 1143 | Medium | LCS table | The table this topic walks through |
| 2 | [Delete Operation for Two Strings](https://leetcode.com/problems/delete-operation-for-two-strings/) | LeetCode 583 | Medium | `n + m − 2·LCS` | The "dual" formula: delete instead of add |
| 3 | [LCS](https://atcoder.jp/contests/dp/tasks/dp_f) | AtCoder dp_f | – | Reconstruct the LCS | The same walk, writing only shared characters |
| 4 | [Shortest Common Supersequence](https://leetcode.com/problems/shortest-common-supersequence/) | LeetCode 1092 | Hard | LCS + full reconstruction | The topic problem itself |
| 5 | [Find the Shortest Superstring](https://leetcode.com/problems/find-the-shortest-superstring/) | LeetCode 943 | Hard | Bitmask DP (contrast) | Substrings instead of subsequences — see [Bitmask DP](../../04-advanced-dp-patterns/09-bitmask-dp/) |

## 10. Navigation

⬅ Previous: [Distinct Subsequences](../15-distinct-subsequences/) · ➡ Next: [DP on Strings (module 04)](../../04-advanced-dp-patterns/01-dp-on-strings/) · 📖 [Walkthrough](walkthrough.md) · 💻 [Solutions](solution/)
