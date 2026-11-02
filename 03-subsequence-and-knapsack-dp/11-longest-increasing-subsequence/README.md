# Longest Increasing Subsequence (LIS)

> **Module:** Subsequence and Knapsack DP · **Difficulty:** Intermediate · **Estimated time:** 120–150 min

## 1. What is this topic?

Given an array of numbers, find the length of the longest **subsequence** (elements kept in their original order, gaps allowed) whose values are **strictly increasing**. In `[10, 9, 2, 5, 3, 7, 101, 18]` one longest such subsequence is `2, 5, 7, 101`, so the answer is 4.

LIS is the first "subsequence DP" in this module where the decision at each element depends on **which element we took last**. You will meet four solutions, each teaching something different:

1. the recursive `(index, prevIndex)` take/skip search and its memoization;
2. the classic O(n²) table `dp[i]` = LIS **ending at** `i`;
3. printing an actual LIS with parent pointers;
4. the O(n log n) **patience sorting / binary search** method.

## 2. Why does this concept exist?

Many problems ask for the longest chain of items where each item must "beat" the previous one: taller and wider envelopes, words that grow by one letter, numbers that divide each other, jobs that start after the previous one ends. Whenever the rule is "the next chosen item must be compatible with the **last** chosen item", the LIS structure appears. Recognising it turns a scary-looking problem into a 10-line DP.

## 3. What problem does it solve?

[LeetCode 300 — Longest Increasing Subsequence](https://leetcode.com/problems/longest-increasing-subsequence/), and through small changes to the comparison or the counting: [Number of Longest Increasing Subsequence](https://leetcode.com/problems/number-of-longest-increasing-subsequence/), [Largest Divisible Subset](https://leetcode.com/problems/largest-divisible-subset/), [Longest String Chain](https://leetcode.com/problems/longest-string-chain/) and [Russian Doll Envelopes](https://leetcode.com/problems/russian-doll-envelopes/).

## 4. Prerequisites

- [Subsequences Fundamentals](../01-subsequences-fundamentals/) — take/skip recursion over an array.
- [0/1 Knapsack](../02-zero-one-knapsack/) — a 2-parameter state `(index, something remembered)`.
- [Maximum Subarray (Kadane vs DP)](../../01-one-dimensional-dp/09-maximum-subarray-kadane-vs-dp/) — the "best answer **ending at** i" style of state.
- [State Representation](../../00-foundations/09-state-representation/) — choosing what to remember about the past.
- Binary search on a sorted array (lower bound) for the O(n log n) method.

## 5. What you will learn

- Why the state needs `prevIndex` (or, equivalently, "ending at i").
- The O(n²) DP and how to print one LIS with a `parent` array.
- The O(n log n) tails/patience method, explained with cards, and a proof sketch of why it works.
- Why the `tails` array is **not** the LIS itself.
- How to adapt LIS to counting (LC 673), divisibility (LC 368), strings (LC 1048) and 2D items (LC 354).

## 6. Important terminology

| Term | Meaning in plain English |
|---|---|
| Subsequence | Elements picked from the array in their original order; gaps allowed |
| Strictly increasing | Every element is **greater than** the previous one (equal is not allowed) |
| LIS ending at i | The longest increasing subsequence whose **last** element is `nums[i]` |
| prevIndex | Index of the last element taken so far; `-1` means "nothing taken yet" |
| Parent pointer | `parent[i]` = the index that comes just before `i` in the best subsequence ending at `i` |
| tails array | `tails[k]` = the smallest possible last value of any increasing subsequence of length `k+1` seen so far |
| Lower bound | The first position in a sorted array whose value is `>=` a given `x` |

## 7. Where this is used in real problems

- Scheduling chains (a job can follow another only if it starts later).
- Nesting boxes/envelopes ([Russian Doll Envelopes](https://leetcode.com/problems/russian-doll-envelopes/)).
- Version or word chains ([Longest String Chain](https://leetcode.com/problems/longest-string-chain/)).
- Diff tools and the "patience diff" algorithm (used by version control systems) are built on the same patience-sorting idea.
- Minimum number of deletions to make an array sorted = `n − LIS`.

## 8. After completing this topic you should be able to…

- [ ] Write the `(index, prevIndex)` recursion and memoize it with a `prevIndex + 1` column shift.
- [ ] Define `dp[i]` precisely and fill it by hand for an 8-element array.
- [ ] Print one LIS using parent pointers.
- [ ] Run the tails/binary-search method by hand and explain each replacement.
- [ ] Explain with an example why `tails` is not necessarily a real subsequence.
- [ ] Recognise LIS in disguise (divisibility, chains, envelopes).

## 9. Practice problems

| # | Problem | Platform/ID | Difficulty | Pattern tested | Why it belongs here |
|---|---|---|---|---|---|
| 1 | [Longest Increasing Subsequence](https://leetcode.com/problems/longest-increasing-subsequence/) | LeetCode 300 | Medium | LIS, O(n²) and O(n log n) | The topic problem itself |
| 2 | [Increasing Subsequence](https://cses.fi/problemset/task/1145) | CSES 1145 | – | LIS with n up to 2·10⁵ | Forces the O(n log n) tails method |
| 3 | [Maximum Length of Pair Chain](https://leetcode.com/problems/maximum-length-of-pair-chain/) | LeetCode 646 | Medium | LIS after sorting pairs | "Compatible with the previous item" with a custom rule |
| 4 | [Longest Arithmetic Subsequence of Given Difference](https://leetcode.com/problems/longest-arithmetic-subsequence-of-given-difference/) | LeetCode 1218 | Medium | "Ending at value" DP with a hash map | Same "ending at" state, but the predecessor is unique, so O(n) |
| 5 | [Longest String Chain](https://leetcode.com/problems/longest-string-chain/) | LeetCode 1048 | Medium | LIS over strings sorted by length | Predecessor = word with one letter removed |
| 6 | [Largest Divisible Subset](https://leetcode.com/problems/largest-divisible-subset/) | LeetCode 368 | Medium | LIS with divisibility + printing | Requires the parent-pointer reconstruction from this topic |
| 7 | [Number of Longest Increasing Subsequence](https://leetcode.com/problems/number-of-longest-increasing-subsequence/) | LeetCode 673 | Medium | LIS + counting | Adds a `count[i]` array beside `dp[i]` |
| 8 | [Russian Doll Envelopes](https://leetcode.com/problems/russian-doll-envelopes/) | LeetCode 354 | Hard | 2D LIS, sort trick + O(n log n) | O(n²) times out; needs the tails method and a clever sort |

## 10. Navigation

⬅ Previous: [Rod Cutting](../10-rod-cutting/) · ➡ Next: [Longest Common Subsequence](../12-longest-common-subsequence/) · 📖 [Walkthrough](walkthrough.md) · 💻 [Solutions](solution/)
