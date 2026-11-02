# Space Optimization

> **Module:** 05 — DP Optimization · **Difficulty:** Advanced · **Estimated time:** 2–3 hours

## 1. What is this topic?

A systematic method for shrinking the memory of a DP table. Instead of remembering tricks problem by problem ("House Robber uses two variables", "knapsack loops backwards"), you learn **one analysis** — the *dependency footprint* — and derive every trick from it:

- 1D table → O(1) variables,
- 2D table → one row,
- 0/1 knapsack → one array with a **reverse** loop,
- LCS / Edit Distance → one row plus a saved **diagonal** variable `prev`,
- and the cases where you **must not** throw rows away (rebuilding the actual answer path).

You met the basic idea in [Space optimization basics](../../00-foundations/15-space-optimization-basics/). This topic turns it into a procedure you can apply to any recurrence.

## 2. Why does this concept exist?

A DP table usually has one cell per state, but each cell is read only by a few *later* cells. Once those readers are done, the cell is dead weight. A 2D table of 10⁴ × 10⁴ `int`s is 400 MB — far beyond a typical 256 MB judge limit — while one row of it is 40 KB. Same algorithm, same answer, 10 000× less memory. Smaller arrays also fit in the CPU cache, so the optimized version is often faster too.

## 3. What problem does it solve?

- "Memory Limit Exceeded" on problems with large `n × m` or `n × W` tables.
- Interview follow-ups: "Can you do it in O(n) / O(1) extra space?"
- It also forces you to understand **exactly which states a transition reads**, which is the first step of every time optimization in this module.

## 4. Prerequisites

- [Space optimization basics](../../00-foundations/15-space-optimization-basics/)
- [Transition dependencies and computation order](../../00-foundations/11-transition-dependencies-and-computation-order/)
- [House Robber](../../01-one-dimensional-dp/06-house-robber/)
- [Minimum Path Sum](../../02-two-dimensional-and-grid-dp/04-minimum-path-sum/)
- [0/1 Knapsack](../../03-subsequence-and-knapsack-dp/02-zero-one-knapsack/)
- [Longest Common Subsequence](../../03-subsequence-and-knapsack-dp/12-longest-common-subsequence/)

## 5. What you will learn

- How to draw the **dependency footprint** (stencil) of any recurrence.
- The **last-reader rule**: a cell may be overwritten once every cell that reads it has been computed.
- How loop direction decides whether a value is still "old" or already "new" when you read it.
- Why 0/1 knapsack loops capacities **downward** and unbounded knapsack loops **upward**.
- How to rescue a diagonal dependency with one extra variable.
- When space optimization is impossible or costs you something (reconstruction, random access to old states).

## 6. Important terminology

| Term | Meaning in plain English |
|------|--------------------------|
| Dependency footprint (stencil) | The set of earlier cells that one cell's recurrence reads, drawn relative to the cell, e.g. "up and left". |
| Live cell | A cell that some not-yet-computed cell will still read. |
| Dead cell | A cell nobody will read again; its memory can be reused. |
| Last-reader rule | You may overwrite a cell only after its last reader has been computed. |
| In-place update | Writing the new row into the same array that holds the old row. |
| Old value / new value | In a one-row array, the value from the previous row (not yet overwritten) vs. the value already computed for the current row. |
| Diagonal dependency | A recurrence reading `dp[i-1][j-1]`; in one row this cell is overwritten before we need it. |
| Reconstruction | Recovering *which choices* produced the optimum (the path, the items, the subsequence), not just its value. |

## 7. Where this is used in real problems

- Every 1D "look back a constant number of steps" DP: Fibonacci, Climbing Stairs, House Robber, Decode Ways.
- Grid DPs: Unique Paths, Minimum Path Sum, Triangle, Falling Path Sum.
- Knapsack family: Partition Equal Subset Sum, Target Sum, Coin Change, Ones and Zeroes.
- Two-string DPs: LCS, Edit Distance, Distinct Subsequences, Interleaving String.

## 8. After completing this topic you should be able to…

- [ ] Draw the footprint of a recurrence and count how many rows/variables are live.
- [ ] Convert a 1D DP with a constant look-back to O(1) variables.
- [ ] Convert a 2D DP that reads only the previous row (and the current row to the left) to one array.
- [ ] Choose the correct loop direction for 0/1 vs unbounded knapsack and explain why.
- [ ] Use a `prev` variable to keep the diagonal in LCS / Edit Distance.
- [ ] Explain why rebuilding the optimal path needs the full table (or a cleverer algorithm).

## 9. Practice problems

| # | Problem | Platform/ID | Difficulty | Pattern tested | Why it belongs here |
|---|---------|-------------|------------|----------------|---------------------|
| 1 | [Climbing Stairs](https://leetcode.com/problems/climbing-stairs/) | LeetCode 70 | Easy | 1D → two variables | The smallest possible footprint `{i-1, i-2}`. |
| 2 | [House Robber](https://leetcode.com/problems/house-robber/) | LeetCode 198 | Medium | 1D → two variables | Walked through in this topic. |
| 3 | [Unique Paths](https://leetcode.com/problems/unique-paths/) | LeetCode 62 | Medium | 2D → one row | Up + left footprint, same shape as Minimum Path Sum. |
| 4 | [Minimum Path Sum](https://leetcode.com/problems/minimum-path-sum/) | LeetCode 64 | Medium | 2D → one row | Walked through in this topic. |
| 5 | [Triangle](https://leetcode.com/problems/triangle/) | LeetCode 120 | Medium | 2D → one row, bottom-up | Footprint "below and below-right" decides the sweep direction. |
| 6 | [Knapsack 1](https://atcoder.jp/contests/dp/tasks/dp_d) | AtCoder dp_d | Unrated (AtCoder) | 0/1 knapsack, reverse loop | Pure test of the reverse-loop rule. |
| 7 | [Book Shop](https://cses.fi/problemset/task/1158) | CSES 1158 | Unrated (CSES) | 0/1 knapsack, reverse loop | A 2D table of n·x ints is ~400 MB; one array is required. |
| 8 | [Partition Equal Subset Sum](https://leetcode.com/problems/partition-equal-subset-sum/) | LeetCode 416 | Medium | Boolean 0/1 knapsack, reverse loop | Same rule with `||` instead of `max`. |
| 9 | [Longest Common Subsequence](https://leetcode.com/problems/longest-common-subsequence/) | LeetCode 1143 | Medium | One row + `prev` diagonal | Walked through in this topic. |
| 10 | [Edit Distance](https://leetcode.com/problems/edit-distance/) | LeetCode 72 | Medium | One row + `prev` diagonal | Diagonal plus a non-zero column 0 that must be reset each row. |

## 10. Navigation

⬅ Previous: [State machine DP](../../04-advanced-dp-patterns/12-state-machine-dp/) · ➡ Next: [Rolling arrays](../02-rolling-arrays/) · 📖 [Walkthrough](walkthrough.md) · 💻 [Solutions](solution/)
