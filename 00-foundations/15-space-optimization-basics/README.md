# Space Optimization Basics

> **Module:** 00 · Foundations · **Difficulty:** Beginner · **Estimated time:** 60–90 min

## 1. What is this topic?

A tabulated DP usually keeps **every** state it has ever computed. Most of them are never read again. **Space optimization** means keeping only the states that the recurrence will still read, and throwing the rest away.

This topic teaches one question that drives every space optimization — *"which earlier states does the recurrence actually read?"* — and applies it three ways:

- **Two variables** instead of a 1D array (Climbing Stairs / Fibonacci).
- **Two rows, then one row** instead of a 2D grid (grid paths).
- **One array reused in reverse order** instead of a 2D knapsack table (a preview of 0/1 knapsack).

It also shows when you **cannot** throw states away: when you must reconstruct *which* choices produced the answer.

## 2. Why does this concept exist?

In [12 · Time and Space Complexity in DP](../12-time-and-space-complexity-in-dp/) you saw that a table's memory is "number of states". Sometimes that is too much: a 0/1 knapsack with 1 000 items and capacity 10⁵ needs 10⁸ cells. But the recurrence for row `i` reads only row `i - 1`, so 999 of those 1 000 rows are dead weight. Space optimization removes them, often turning `O(n · W)` memory into `O(W)` or `O(n)` into `O(1)`, without changing the time.

Interviewers also love the follow-up "can you do it in less memory?". This topic makes the answer mechanical.

## 3. What problem does it solve?

- Memory limits: tables that do not fit become tables that do.
- Interview follow-ups: "O(n) space → O(1) space".
- It prepares you for the many DP solutions online that are **already** space-optimized and look mysterious (`best[w]` loops running backwards, `prev`/`curr` swaps). After this topic you can read them.

## 4. Prerequisites

- [07 · Tabulation](../07-tabulation/): filling a table bottom-up.
- [11 · Transition Dependencies and Computation Order](../11-transition-dependencies-and-computation-order/): which cells each cell reads, and loop direction.
- [12 · Time and Space Complexity in DP](../12-time-and-space-complexity-in-dp/): measuring memory.
- [14 · Converting Memoization to Tabulation](../14-converting-memoization-to-tabulation/): you need a working table before you can shrink it.

## 5. What you will learn

- To draw the **read window** of a recurrence: how far back it looks.
- To replace a 1D table by **two variables** and update them in the right order.
- To replace a 2D table by **two rows** (`previous`, `current`) and then by **one row** overwritten in place.
- Why the 0/1 knapsack's one-row version must loop capacity **from high to low**, and the exact wrong answer (`60` instead of `35`) that the forward loop produces.
- When **not** to optimize: path or choice reconstruction needs the full table.

## 6. Important terminology

| Term | Meaning in plain English |
|------|--------------------------|
| Read window | The set of earlier states the recurrence reads, e.g. "the two previous cells" or "the row above". |
| Rolling variables | A few variables that slide forward, each time dropping the oldest value. |
| Rolling array / two rows | Keeping only the previous row and the current row of a 2D table. |
| In-place update | Overwriting a single array so that each cell changes from "old row" to "new row" as the loop passes. |
| Stale value | A value from the previous row that is still in the array because it has not been overwritten yet. Sometimes you *need* it stale. |
| Reconstruction | Recovering *which* choices (items, moves) produced the optimal answer, not just its value. |

## 7. Where this is used in real problems

- 1D DPs with a fixed look-back: [Fibonacci Number](../../01-one-dimensional-dp/01-fibonacci-number/), [Climbing Stairs](../../01-one-dimensional-dp/02-climbing-stairs/), [House Robber](../../01-one-dimensional-dp/06-house-robber/) all go to O(1) space.
- Grid DPs: [Unique Paths](../../02-two-dimensional-and-grid-dp/02-unique-paths/), Minimum Path Sum, Triangle go to O(cols).
- Knapsack DPs: [0/1 Knapsack](../../03-subsequence-and-knapsack-dp/02-zero-one-knapsack/), Subset Sum, Partition Equal Subset Sum use one array with a reverse loop; [Unbounded Knapsack](../../03-subsequence-and-knapsack-dp/07-unbounded-knapsack/) and Coin Change use the **forward** loop on purpose.
- The advanced versions of these tricks are in [05-dp-optimization/01-space-optimization](../../05-dp-optimization/01-space-optimization/) and [02-rolling-arrays](../../05-dp-optimization/02-rolling-arrays/).

## 8. After completing this topic you should be able to…

- [ ] Look at a recurrence and say which earlier states it reads.
- [ ] Rewrite Climbing Stairs with two variables, updating them in the correct order.
- [ ] Rewrite a grid DP with two rows, and then with one row.
- [ ] Explain why the 0/1 knapsack one-row loop goes from high capacity to low.
- [ ] Predict the wrong answer of the forward loop on a small knapsack.
- [ ] Decide when to keep the full table because the choices must be reconstructed.

## 9. Practice problems

For each one, first write the full table version, then shrink it, and check that both agree.

| # | Problem | Platform/ID | Difficulty | Pattern tested | Why it belongs here |
|---|---------|-------------|------------|----------------|---------------------|
| 1 | [Fibonacci Number](https://leetcode.com/problems/fibonacci-number/) | LeetCode 509 | Easy | Two variables | The purest "read the last two" recurrence. |
| 2 | [Climbing Stairs](https://leetcode.com/problems/climbing-stairs/) | LeetCode 70 | Easy | Two variables | Same window as Fibonacci; the worked example of this topic. |
| 3 | [Min Cost Climbing Stairs](https://leetcode.com/problems/min-cost-climbing-stairs/) | LeetCode 746 | Easy | Two variables | Two-variable window with a `min` instead of a sum. |
| 4 | [House Robber](https://leetcode.com/problems/house-robber/) | LeetCode 198 | Medium | Two variables | Same window with a `max`; a favorite interview follow-up. |
| 5 | [Unique Paths](https://leetcode.com/problems/unique-paths/) | LeetCode 62 | Medium | Two rows → one row | The worked grid example of this topic. |
| 6 | [Minimum Path Sum](https://leetcode.com/problems/minimum-path-sum/) | LeetCode 64 | Medium | One row | Same window as Unique Paths, with `min` and cell costs. |
| 7 | [Partition Equal Subset Sum](https://leetcode.com/problems/partition-equal-subset-sum/) | LeetCode 416 | Medium | One array, reverse loop | 0/1 "take each number at most once": the forward loop is a bug. |
| 8 | [Book Shop](https://cses.fi/problemset/task/1158) | CSES 1158 | - | One array, reverse loop | 0/1 knapsack where the full table (10⁸ cells) is too big for comfort. |

## 10. Navigation

⬅ Previous: [14 · Converting Memoization to Tabulation](../14-converting-memoization-to-tabulation/) · ➡ Next: [16 · The DP Problem-Solving Framework](../16-the-dp-problem-solving-framework/) · 📖 [Walkthrough](walkthrough.md) · 💻 [Solutions](solution/)
