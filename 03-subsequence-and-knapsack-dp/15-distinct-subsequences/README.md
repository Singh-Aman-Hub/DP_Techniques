# Distinct Subsequences

> **Module:** Subsequence and Knapsack DP · **Difficulty:** Advanced · **Estimated time:** 90–120 min

## 1. What is this topic?

Given a long string `s` and a short string `t`, count **in how many different ways** you can pick characters of `s` (keeping their order) that spell exactly `t`. Two ways are different if they use a different set of **positions** in `s`. For `s = "babgbag"`, `t = "bag"` there are 5 ways.

So far in this module's string DPs you **maximised** ([LCS](../12-longest-common-subsequence/)) or **minimised** ([Edit Distance](../14-edit-distance/)). Here you **count**, so choices are combined with `+` — exactly the switch you made from [Coin Change](../08-coin-change/) to [Coin Change II](../09-coin-change-ii/), now on a two-string table.

## 2. Why does this concept exist?

Counting occurrences of a pattern as a subsequence comes up in text analysis, bioinformatics (how many ways a motif can be embedded in a sequence) and combinatorics. More importantly for learning, it trains two habits:

- seeing that "take this character" and "skip this character" are **disjoint** ways, so their counts add;
- recognising when a 1D rolled array must be updated **right to left** (each character of `s` can be used at most once — the 0/1 knapsack rule).

It also forces you to think about **overflow** of intermediate counts, which LeetCode's guarantee does not cover.

## 3. What problem does it solve?

[LeetCode 115 — Distinct Subsequences](https://leetcode.com/problems/distinct-subsequences/): count the distinct subsequences of `s` equal to `t`. The answer is guaranteed to fit in a 32-bit signed integer.

## 4. Prerequisites

- [Longest Common Subsequence](../12-longest-common-subsequence/) — two-string prefix table.
- [Coin Change II](../09-coin-change-ii/) — counting with `+`, base case "empty = one way", overflow discussion.
- [0/1 Knapsack](../02-zero-one-knapsack/) — why a 1D array is updated right to left when each item is used at most once.
- [Is Subsequence](https://leetcode.com/problems/is-subsequence/) — the yes/no version of the question (a good warm-up).

## 5. What you will learn

- The counting state `ways(i, j)` on prefixes and the "skip always / use if equal" recurrence.
- Why the base column is 1 (empty `t`) and the base row is 0 (empty `s`, non-empty `t`).
- A full table dry run and the right-to-left 1D update.
- What can overflow, why Java still gets the right answer, and how to stay safe in general.

## 6. Important terminology

| Term | Meaning in plain English |
|---|---|
| Distinct subsequence (here) | A choice of **positions** in `s`; two choices are different if their position sets differ, even if they spell the same string |
| Source / pattern | `s` (we pick from it) / `t` (what we must spell) |
| Use / skip | Use `s[i-1]` to match `t[j-1]`, or ignore `s[i-1]` |
| Counting DP | Cells hold numbers of ways; disjoint options are added |
| Overflow | A number exceeding the range of its type (int: about 2.1·10⁹; long: about 9.2·10¹⁸) |

## 7. Where this is used in real problems

- Counting embeddings of a pattern in a text (motif finding).
- A building block for counting problems such as [Distinct Subsequences II](https://leetcode.com/problems/distinct-subsequences-ii/) (count all distinct subsequence *strings*) and many "number of ways to form a target string" problems.
- The same "use at most once → iterate right to left" rule shows up in every rolled 0/1-knapsack-style counting DP.

## 8. After completing this topic you should be able to…

- [ ] Write `ways(i, j)` and justify why the two options add.
- [ ] Explain both base cases with a concrete example.
- [ ] Fill the `"babgbag"` / `"bag"` table by hand and get 5.
- [ ] Write the 1D version and explain why `j` must go from `m` down to 1.
- [ ] Explain why intermediate overflow does not break the Java answer, and how to guard against it anyway.

## 9. Practice problems

| # | Problem | Platform/ID | Difficulty | Pattern tested | Why it belongs here |
|---|---|---|---|---|---|
| 1 | [Is Subsequence](https://leetcode.com/problems/is-subsequence/) | LeetCode 392 | Easy | Two pointers / boolean prefix DP | The yes/no version: is the count at least 1? |
| 2 | [Longest Common Subsequence](https://leetcode.com/problems/longest-common-subsequence/) | LeetCode 1143 | Medium | Two-string prefix DP (max) | Same table shape; compare `max` with `+` |
| 3 | [Distinct Subsequences](https://leetcode.com/problems/distinct-subsequences/) | LeetCode 115 | Hard | Two-string counting DP | The topic problem itself |
| 4 | [Distinct Subsequences II](https://leetcode.com/problems/distinct-subsequences-ii/) | LeetCode 940 | Hard | Counting distinct strings, removing duplicates | Harder counting: equal strings from different positions count once |

## 10. Navigation

⬅ Previous: [Edit Distance](../14-edit-distance/) · ➡ Next: [Shortest Common Supersequence](../16-shortest-common-supersequence/) · 📖 [Walkthrough](walkthrough.md) · 💻 [Solutions](solution/)
