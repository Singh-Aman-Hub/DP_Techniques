# Subsequences Fundamentals

> **Module:** 03 — Subsequence and Knapsack DP · **Difficulty:** Beginner · **Estimated time:** 90–120 min

## 1. What is this topic?

A **subsequence** of an array is what you get by deleting some (possibly zero, possibly all) elements while keeping the rest in their original order. This topic teaches the one recursion that generates every subsequence — at each index, **take** the element or **skip** it — and shows how that recursion turns into a DP once you notice that only two numbers matter: *where you are* and *what you have accumulated so far*.

## 2. Why does this concept exist?

A huge family of problems says "choose some of these items so that …": pick coins to make an amount, pick weights to fill a bag, pick numbers to reach a sum, pick signs to hit a target. Every one of them is "choose a subsequence (or subset)". Trying all choices costs `2^n`, which is hopeless for `n = 40`. The take/skip recursion is the brute force for all of them, and adding the right accumulated quantity to the state is what makes them fast.

## 3. What problem does it solve?

- Listing every subsequence of an array (when you really need all of them, `2^n` is unavoidable).
- Counting the subsequences whose sum equals `K` — the worked problem in this topic — in `O(n · K)` instead of `O(2^n)`.
- It is the bridge to [0/1 Knapsack](../02-zero-one-knapsack/): replace "sum so far" by "capacity used so far" and you have knapsack.

## 4. Prerequisites

- [Recursion Fundamentals](../../00-foundations/02-recursion-fundamentals/) and [Recursion Trees and Brute Force](../../00-foundations/03-recursion-trees-and-brute-force/)
- [Memoization](../../00-foundations/06-memoization/) and [Tabulation](../../00-foundations/07-tabulation/)
- [State Representation](../../00-foundations/09-state-representation/)
- [Maximum Sum of Non-Adjacent Elements](../../01-one-dimensional-dp/05-maximum-sum-of-non-adjacent-elements/) — your first pick/not-pick recurrence
- [2D DP State Representation](../../02-two-dimensional-and-grid-dp/01-2d-dp-state-representation/) — states with two numbers

## 5. What you will learn

- The exact difference between a subarray, a subsequence and a subset, and how many of each an array has.
- The take/skip recursion and its recursion tree with `2^n` leaves.
- How to print every subsequence with backtracking.
- How to count subsequences with sum `K` by brute force, then by memoization on the state `(index, sumSoFar)`, then by a bottom-up table, then with two rows.
- Why "index + some accumulated quantity" is the state shape of the whole knapsack family.

## 6. Important terminology

| Term | Meaning in plain English |
|---|---|
| Subarray | A *contiguous* block of the array, such as `[2, 3]` in `[1, 2, 3]`. An array of length `n` has `n(n+1)/2` non-empty subarrays. |
| Subsequence | Elements picked in their original order, gaps allowed: `[1, 3]` is a subsequence of `[1, 2, 3]`. There are `2^n` of them, including the empty one. |
| Subset | A collection of elements where order does not matter. For "pick some items" problems, subsets and subsequences are the same thing: walk the array in order and decide for each item. |
| Take / skip (pick / not-pick) | The two decisions made at every index: include the element or leave it out. |
| Backtracking | Undoing a choice (removing the element you just added) after exploring it, so the next branch starts clean. |
| Accumulated quantity | A number that summarizes all earlier choices, such as "sum of the taken elements". Adding it to the state is what makes these problems DP. |
| State `(i, s)` | "I have decided elements `0..i-1`, and the taken ones add up to `s`." |

## 7. Where this is used in real problems

- [0/1 Knapsack](../02-zero-one-knapsack/), [Subset Sum](../03-subset-sum/), [Equal Sum Partition](../04-equal-sum-partition/), [Target Sum](../06-target-sum/): all are "take/skip with an accumulated sum or weight".
- Backtracking problems such as LeetCode Subsets and Combination Sum, which really do need every subsequence.
- Later, [Longest Increasing Subsequence](../11-longest-increasing-subsequence/) and [Longest Common Subsequence](../12-longest-common-subsequence/) use the same take/skip idea on sequences.

## 8. After completing this topic you should be able to…

- [ ] Say whether a problem asks for subarrays, subsequences or subsets, and how many candidates there are.
- [ ] Draw the take/skip recursion tree for a 3-element array and list its 8 leaves.
- [ ] Write a backtracking function that prints every subsequence.
- [ ] Count subsequences with sum `K` with memoization on `(index, sumSoFar)` and explain why the cache is valid.
- [ ] Fill the table `dp[i][s]` by hand and shrink it to two rows.

## 9. Practice problems

| # | Problem | Platform/ID | Difficulty | Pattern tested | Why it belongs here |
|---|---|---|---|---|---|
| 1 | [Subsets](https://leetcode.com/problems/subsets/) | LeetCode 78 | Medium | Generate all subsequences | Exactly the take/skip generator from this topic, with distinct numbers. |
| 2 | [Combination Sum II](https://leetcode.com/problems/combination-sum-ii/) | LeetCode 40 | Medium | Take/skip with a running sum, duplicates | Lists every subset with sum = target; teaches pruning with a sorted array. |
| 3 | [Combination Sum](https://leetcode.com/problems/combination-sum/) | LeetCode 39 | Medium | Take and *stay* (reuse allowed) | Preview of the unbounded choice you will formalize in topic 07. |
| 4 | [Target Sum](https://leetcode.com/problems/target-sum/) | LeetCode 494 | Medium | `+`/`-` choice per element, count | Solve it now by brute force (2^20 is fine); topic 06 turns it into DP. |
| 5 | [Money Sums](https://cses.fi/problemset/task/1745) | CSES 1745 | — | All reachable sums | Brute force is too slow; try `(index, sum)` DP here, then compare with topic 03. |

## 10. Navigation

⬅ Previous: [Cherry Pickup](../../02-two-dimensional-and-grid-dp/09-cherry-pickup/) · ➡ Next: [0/1 Knapsack](../02-zero-one-knapsack/) · 📖 [Walkthrough](walkthrough.md) · 💻 [Solutions](solution/)
