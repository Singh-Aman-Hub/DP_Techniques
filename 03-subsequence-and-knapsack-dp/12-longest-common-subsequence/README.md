# Longest Common Subsequence (LCS)

> **Module:** Subsequence and Knapsack DP · **Difficulty:** Intermediate · **Estimated time:** 120–150 min

## 1. What is this topic?

Given two strings, find the length of the longest sequence of characters that appears in **both**, in the same order, though not necessarily next to each other. For `"abcde"` and `"ace"` the LCS is `"ace"` (length 3).

This is the first **two-string DP** in the course: the state is a pair `(i, j)` — how much of the first string and how much of the second string we are still considering. Almost every string DP you will meet later (edit distance, distinct subsequences, shortest common supersequence, palindromic subsequence, wildcard matching…) is a variation of the LCS table, so this topic is the foundation of the next four.

## 2. Why does this concept exist?

Comparing two sequences is everywhere: `diff` tools show which lines two files share, biologists align DNA strands, spell checkers compare words, plagiarism detectors compare documents. The question underneath is always: *"what is the biggest part these two sequences have in common, respecting order?"* LCS answers it in O(n·m) time, where trying all subsequences would take O(2ⁿ).

## 3. What problem does it solve?

[LeetCode 1143 — Longest Common Subsequence](https://leetcode.com/problems/longest-common-subsequence/) (length only), and [AtCoder DP Contest F — LCS](https://atcoder.jp/contests/dp/tasks/dp_f), which asks you to **print** one LCS — exactly the backtracking skill taught here.

## 4. Prerequisites

- [Subsequences Fundamentals](../01-subsequences-fundamentals/) — what a subsequence is, take/skip.
- [Longest Increasing Subsequence](../11-longest-increasing-subsequence/) — the previous topic; reconstruction with "where did this value come from?".
- [2D DP State Representation](../../02-two-dimensional-and-grid-dp/01-2d-dp-state-representation/) — reading and filling a 2D table.
- [Unique Paths](../../02-two-dimensional-and-grid-dp/02-unique-paths/) — a table where each cell looks up, left (and here also diagonally).

## 5. What you will learn

- The prefix state `lcs(i, j)` for two strings and the "compare the last characters" recurrence.
- Why a mismatch means "drop one of the two last characters", and why trying both is enough.
- Filling the full `(n+1) × (m+1)` table by hand, with the empty-prefix row and column.
- Rebuilding the LCS string by walking back through the table.
- The 1D optimization with a saved `diagonal` variable.

## 6. Important terminology

| Term | Meaning in plain English |
|---|---|
| Common subsequence | A sequence that is a subsequence of both strings |
| Prefix `s[0..i)` | The first `i` characters of `s` (`i = 0` means the empty prefix) |
| Match | The two characters we are comparing are equal |
| Diagonal / up / left | In the table, cell `(i-1, j-1)`, `(i-1, j)` and `(i, j-1)` |
| Backtracking (reconstruction) | Walking from the bottom-right cell back to the top-left to recover the actual LCS |
| Substring vs subsequence | A substring must be contiguous; a subsequence need not be |

## 7. Where this is used in real problems

- Diff utilities and version control (the lines two files have in common).
- DNA / protein sequence alignment (with weights instead of +1).
- As a building block: [Delete Operation for Two Strings](https://leetcode.com/problems/delete-operation-for-two-strings/) (`n + m − 2·LCS`), [Shortest Common Supersequence](../16-shortest-common-supersequence/) (`n + m − LCS`), [Longest Palindromic Subsequence](../13-longest-palindromic-subsequence/) (`LCS(s, reverse(s))`), [Uncrossed Lines](https://leetcode.com/problems/uncrossed-lines/) (LCS of arrays).

## 8. After completing this topic you should be able to…

- [ ] Define `lcs(i, j)` in one English sentence using prefixes.
- [ ] Explain both branches of the recurrence and the base row/column.
- [ ] Fill a 7 × 6 LCS table by hand without mistakes.
- [ ] Backtrack the table to print one LCS, and explain why ties don't matter for the length.
- [ ] Write the single-array version with the `diagonal` variable and explain why it is needed.
- [ ] Tell LCS (subsequence) apart from longest common **substring** ([LC 718](https://leetcode.com/problems/maximum-length-of-repeated-subarray/)).

## 9. Practice problems

| # | Problem | Platform/ID | Difficulty | Pattern tested | Why it belongs here |
|---|---|---|---|---|---|
| 1 | [Is Subsequence](https://leetcode.com/problems/is-subsequence/) | LeetCode 392 | Easy | Two pointers / LCS special case | `s` is a subsequence of `t` iff `LCS(s, t) == len(s)` — warm-up |
| 2 | [Longest Common Subsequence](https://leetcode.com/problems/longest-common-subsequence/) | LeetCode 1143 | Medium | Two-string prefix DP | The topic problem itself |
| 3 | [LCS](https://atcoder.jp/contests/dp/tasks/dp_f) | AtCoder dp_f | – | LCS + reconstruction | Must print the string, not just the length |
| 4 | [Uncrossed Lines](https://leetcode.com/problems/uncrossed-lines/) | LeetCode 1035 | Medium | LCS on integer arrays | Same DP hidden behind a geometry story |
| 5 | [Delete Operation for Two Strings](https://leetcode.com/problems/delete-operation-for-two-strings/) | LeetCode 583 | Medium | `n + m − 2·LCS` | Direct application of the LCS length |
| 6 | [Minimum ASCII Delete Sum for Two Strings](https://leetcode.com/problems/minimum-ascii-delete-sum-for-two-strings/) | LeetCode 712 | Medium | Weighted LCS | Replace "+1 per match" with character costs |
| 7 | [Maximum Length of Repeated Subarray](https://leetcode.com/problems/maximum-length-of-repeated-subarray/) | LeetCode 718 | Medium | Longest common **substring** | Contrast: on mismatch the cell becomes 0, not max(up, left) |
| 8 | [Max Dot Product of Two Subsequences](https://leetcode.com/problems/max-dot-product-of-two-subsequences/) | LeetCode 1458 | Hard | LCS-shaped DP with weights and "non-empty" | Same three neighbours, harder base cases |

## 10. Navigation

⬅ Previous: [Longest Increasing Subsequence](../11-longest-increasing-subsequence/) · ➡ Next: [Longest Palindromic Subsequence](../13-longest-palindromic-subsequence/) · 📖 [Walkthrough](walkthrough.md) · 💻 [Solutions](solution/)
