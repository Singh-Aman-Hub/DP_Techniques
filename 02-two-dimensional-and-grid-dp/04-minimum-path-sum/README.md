# Minimum Path Sum

> **Module:** Two-Dimensional and Grid DP · **Difficulty:** Intermediate · **Estimated time:** 75–100 min

## 1. What is this topic?

[LeetCode 64 — Minimum Path Sum](https://leetcode.com/problems/minimum-path-sum/): every cell of a grid has a non-negative cost. Moving only right or down from the top-left to the bottom-right cell, find the route with the **smallest total cost**.

It is the first *optimisation* problem on a grid. In [Unique Paths](../02-unique-paths/) we **counted** routes with `+`; here we **choose** the best route with `min`. The table, the arrows and the fill order stay the same.

## 2. Why does this concept exist?

Most interesting grid problems are not "how many ways" but "what is the best way": cheapest, shortest, most profitable. This topic shows that switching from counting to optimising changes only one thing — how the choices are combined — and introduces the matching idea for impossible states (`+∞` instead of 0). It is also where the **1D-row space optimisation** becomes a standard tool you will apply to almost every later grid and string DP.

## 3. What problem does it solve?

Cheapest monotone route through a cost grid: robot energy on terrain, minimum toll through a city grid, and the building block for [Triangle](../05-triangle/), [Falling Path Sum](../06-maximum-falling-path-sum/) and [Dungeon Game](../07-dungeon-game/).

## 4. Prerequisites

- [Unique Paths](../02-unique-paths/) and [Unique Paths II](../03-unique-paths-ii/) — the grid table and the border rules.
- [Minimum Cost Climbing Stairs](../../01-one-dimensional-dp/03-minimum-cost-climbing-stairs/) — the 1D version of "min over the previous states plus a cost".
- [Optimal Substructure](../../00-foundations/05-optimal-substructure/).
- [Space Optimization Basics](../../00-foundations/15-space-optimization-basics/).

## 5. What you will learn

- The `min` recurrence `best(r,c) = grid[r][c] + min(best(r−1,c), best(r,c−1))`.
- Why the first row and column are running sums, not 1s or 0s.
- `+∞` as the value of an impossible (off-board) state, and how to avoid overflow with it.
- Reconstructing the actual route from the finished table.
- The 1D-row optimisation in detail, and the "modify the grid in place" variant with its trade-off.

## 6. Important terminology

| Term | Meaning in plain English |
|------|--------------------------|
| Cost | The number written in a cell; you pay it when your route enters (or starts on) that cell. |
| Path sum | The total cost of all cells on a route, including the first and the last. |
| `+∞` (infinity) | A value larger than any real answer; used for "this state cannot be reached" so `min` ignores it. |
| Prefix sum | A running total: the first row's value at column `c` is the sum of the first `c + 1` costs. |
| Path reconstruction | Walking back through the finished table to recover *which* route gave the best value. |
| In-place DP | Storing DP values in the input array itself, to save memory. |

## 7. Where this is used in real problems

- [Triangle](../05-triangle/) and [Falling Path Sum](../06-maximum-falling-path-sum/): min/max over the previous row.
- [Dungeon Game](../07-dungeon-game/): a min-over-grid DP that must run backwards.
- Edit Distance and other string DPs in Module 03: the same "min of up, left, diagonal plus a cost" shape.

## 8. After completing this topic you should be able to…

- [ ] Derive the `min` recurrence and its base cases from the 7 questions.
- [ ] Explain why the first row is a prefix sum.
- [ ] Use `+∞` for off-board states without causing integer overflow.
- [ ] Recover the cheapest route from the table.
- [ ] Write the `O(n)`-space version and explain why updating left to right is correct.

## 9. Practice problems

| # | Problem | Platform/ID | Difficulty | Pattern tested | Why it belongs here |
|---|---------|-------------|------------|----------------|---------------------|
| 1 | [Minimum Path Sum](https://leetcode.com/problems/minimum-path-sum/) | LeetCode 64 | Medium | Min over up/left plus cell cost | The problem of this topic. |
| 2 | [Triangle](https://leetcode.com/problems/triangle/) | LeetCode 120 | Medium | Min over the row below, varying width | The next topic; same `min + cost` idea on a different shape. |
| 3 | [Minimum Falling Path Sum](https://leetcode.com/problems/minimum-falling-path-sum/) | LeetCode 931 | Medium | Min over three cells of the previous row | Same idea with three choices and any starting column. |
| 4 | [Maximal Square](https://leetcode.com/problems/maximal-square/) | LeetCode 221 | Medium | `min` of up, left, diagonal | The `min` recurrence used to measure squares instead of costs. |
| 5 | [Count Square Submatrices with All Ones](https://leetcode.com/problems/count-square-submatrices-with-all-ones/) | LeetCode 1277 | Medium | Same table as 221, summed | Reinforces the three-neighbour `min` and the one-row trick. |
| 6 | [Dungeon Game](https://leetcode.com/problems/dungeon-game/) | LeetCode 174 | Hard | Min over the grid, computed backwards | Looks like this problem but forward DP fails — try it, then read [Dungeon Game](../07-dungeon-game/). |

## 10. Navigation

⬅ Previous: [Unique Paths II](../03-unique-paths-ii/) · ➡ Next: [Triangle](../05-triangle/) · 📖 [Walkthrough](walkthrough.md) · 💻 [Solutions](solution/)
