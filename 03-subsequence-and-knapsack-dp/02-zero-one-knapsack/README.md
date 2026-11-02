# 0/1 Knapsack

> **Module:** 03 — Subsequence and Knapsack DP · **Difficulty:** Intermediate · **Estimated time:** 2–3 hours

## 1. What is this topic?

You have `n` items, each with a **weight** and a **value**, and a bag that holds at most `W` weight. Choose a set of items — each item **at most once** ("0 or 1 copies", hence the name) — so that the total weight fits and the total value is as large as possible. This is the parent problem of topics 03–06 of this module.

## 2. Why does this concept exist?

"Pick some things under a budget to get the most out of them" is everywhere: features to fit in a release, ads in a time slot, files on a disk, tasks in a working day. Trying every subset is `2^n`. Greedy rules ("take the best value per kg first") look clever but can be wrong. Knapsack DP gives the exact answer in `O(n · W)` by remembering only *which item we are at* and *how much room is left*.

## 3. What problem does it solve?

- Maximum total value under a weight limit when each item can be used once.
- With small changes: "can we hit this exact sum?" ([Subset Sum](../03-subset-sum/)), "can we split evenly?" ([Equal Sum Partition](../04-equal-sum-partition/)), "how many ways?" ([Target Sum](../06-target-sum/)).
- The judge problem used here is [Knapsack 1](https://atcoder.jp/contests/dp/tasks/dp_d) (AtCoder DP Contest, task D). Its sibling [Knapsack 2](https://atcoder.jp/contests/dp/tasks/dp_e) (task E) has a huge capacity and needs a value-indexed DP, also covered here.

## 4. Prerequisites

- [Subsequences Fundamentals](../01-subsequences-fundamentals/) — take/skip and the "index + accumulated quantity" state
- [Maximum Sum of Non-Adjacent Elements](../../01-one-dimensional-dp/05-maximum-sum-of-non-adjacent-elements/) — pick/not-pick with `max`
- [2D DP State Representation](../../02-two-dimensional-and-grid-dp/01-2d-dp-state-representation/)
- [Space Optimization Basics](../../00-foundations/15-space-optimization-basics/)

## 5. What you will learn

- The state `(item index, remaining capacity)` and the take/skip recurrence with `max`.
- Brute force → memoization → full table, with a complete hand-filled table.
- How to recover *which* items were chosen from the table.
- The 2-row version, then the 1-row version with the **reverse** capacity loop — and exactly what goes wrong if you loop forward.
- The value-indexed variant for huge capacities (AtCoder dp_e).

## 6. Important terminology

| Term | Meaning in plain English |
|---|---|
| Item | Something you may put in the bag; it has a weight `w[i]` and a value `v[i]`. |
| Capacity `W` | The most total weight the bag can carry. |
| 0/1 | Each item is either left out (0) or put in once (1). No fractions, no repeats. |
| Remaining capacity | How much weight can still be added. Equivalent to "weight used so far", seen from the other side. |
| Feasible choice | A choice that respects the limit: you may only take item `i` if `w[i] ≤` remaining capacity. |
| Rolling array | Keeping only the last row (or two) of a DP table because older rows are never read again. |
| Pseudo-polynomial | Running time that grows with a numeric value of the input (here `W`), not just with `n`. |
| Value-indexed DP | Swapping the roles: index the table by value and store the minimum weight. Useful when `W` is huge. |

## 7. Where this is used in real problems

- Budget selection: maximize benefit under a cost limit ([Book Shop](https://cses.fi/problemset/task/1158)).
- Two budgets at once — the state just gains another capacity dimension ([Ones and Zeroes](https://leetcode.com/problems/ones-and-zeroes/)).
- Every "choose a subset with a sum condition" problem in topics 03–06.

## 8. After completing this topic you should be able to…

- [ ] State `best(i, c)` in one English sentence and write its recurrence and base case.
- [ ] Fill a knapsack table by hand and read off both the best value and the chosen items.
- [ ] Explain why the 1-row version must loop capacity from high to low, using a concrete example of the bug.
- [ ] Estimate the running time from the constraints and decide whether `O(n · W)` fits.
- [ ] Switch to the value-indexed DP when `W` is too large but the total value is small.

## 9. Practice problems

| # | Problem | Platform/ID | Difficulty | Pattern tested | Why it belongs here |
|---|---|---|---|---|---|
| 1 | [Knapsack 1](https://atcoder.jp/contests/dp/tasks/dp_d) | AtCoder dp_d | — | Classic 0/1 knapsack, `O(n·W)` | The exact problem of this walkthrough; needs `long` for the answer. |
| 2 | [Book Shop](https://cses.fi/problemset/task/1158) | CSES 1158 | — | 0/1 knapsack, 1-row reverse loop | Price = weight, pages = value; `n·x = 10^8`, so the 1-row version matters in Java. |
| 3 | [Knapsack 2](https://atcoder.jp/contests/dp/tasks/dp_e) | AtCoder dp_e | — | Value-indexed knapsack | `W` up to 10^9 breaks dp_d's table; swap weight and value. |
| 4 | [Ones and Zeroes](https://leetcode.com/problems/ones-and-zeroes/) | LeetCode 474 | Medium | 0/1 knapsack with two capacities | Each string costs zeros *and* ones; both capacity loops run in reverse. |
| 5 | [Profitable Schemes](https://leetcode.com/problems/profitable-schemes/) | LeetCode 879 | Hard | Counting knapsack with a capped extra dimension | Capacity = members, plus a profit dimension clamped at `minProfit`. |

## 10. Navigation

⬅ Previous: [Subsequences Fundamentals](../01-subsequences-fundamentals/) · ➡ Next: [Subset Sum](../03-subset-sum/) · 📖 [Walkthrough](walkthrough.md) · 💻 [Solutions](solution/)
