# Unique Paths

> **Module:** Two-Dimensional and Grid DP · **Difficulty:** Beginner · **Estimated time:** 60–90 min

## 1. What is this topic?

Your first complete 2D DP problem, [LeetCode 62 — Unique Paths](https://leetcode.com/problems/unique-paths/). A robot starts in the top-left cell of an `m × n` board and may only move right or down. You count how many different routes reach the bottom-right cell.

You will solve it four ways — plain recursion, memoization, tabulation and a one-row table — on the same 3 × 3 board, and then see a short mathematical shortcut that counts the paths with a single binomial coefficient.

## 2. Why does this concept exist?

Counting paths on a grid is the "Climbing Stairs of 2D DP". It has the simplest possible recurrence (add the cell above and the cell to the left), no obstacles and no costs, so all your attention can go to the new 2D mechanics: a 2D memo, a 2D table, a base row and a base column, a row-major fill order and the one-row space trick. Every later grid problem reuses this skeleton and changes only the formula inside the loop.

## 3. What problem does it solve?

It answers "in how many ways can I get from one corner to the other with monotone moves?". The same question appears in disguise as "how many ways to arrange `a` right-moves and `b` down-moves", in lattice-path counting, and as the base layer of harder grid problems with obstacles, costs or extra state.

## 4. Prerequisites

- [2D DP State Representation](../01-2d-dp-state-representation/) — the state `(r, c)`, arrows and fill order.
- [Climbing Stairs](../../01-one-dimensional-dp/02-climbing-stairs/) — counting ways by adding the counts of the previous states.
- [Memoization](../../00-foundations/06-memoization/) and [Tabulation](../../00-foundations/07-tabulation/).
- [Space Optimization Basics](../../00-foundations/15-space-optimization-basics/).

## 5. What you will learn

- The full 7-question derivation for a 2D counting problem.
- How the base row and base column fall out of the recurrence.
- A 2D memo and a 2D table, filled in row-major order.
- Shrinking the `m × n` table to one array of length `n`, and why the in-place update is correct.
- The combinatorics shortcut `C(m+n−2, m−1)` and how to compute it without overflow.

## 6. Important terminology

| Term | Meaning in plain English |
|------|--------------------------|
| Monotone path | A path that only moves right or down, never back. |
| `m`, `n` | LeetCode's names: `m` = number of rows, `n` = number of columns. |
| Base row / base column | Row 0 and column 0. Every cell there has exactly one path. |
| Rolling row | A single 1D array that holds the current row of the table and is overwritten row after row. |
| Binomial coefficient `C(k, r)` | The number of ways to choose `r` items out of `k`. Read "k choose r". |

## 7. Where this is used in real problems

- [Unique Paths II](../03-unique-paths-ii/) adds obstacles to the same table.
- [Minimum Path Sum](../04-minimum-path-sum/) replaces "+" with "min of the two plus the cell cost".
- Counting problems with a modulus (CSES Grid Paths, AtCoder Grid 1) use exactly this recurrence.
- Lattice-path counting with blocked cells, as in AtCoder Grid 2, combines this DP with the binomial formula.

## 8. After completing this topic you should be able to…

- [ ] Derive `dp[r][c] = dp[r−1][c] + dp[r][c−1]` from the 7 questions without looking.
- [ ] Explain why every cell of row 0 and column 0 is 1.
- [ ] Write the memoized, tabulated and one-row solutions from a blank file.
- [ ] Explain why `row[c] += row[c−1]` is correct when done left to right.
- [ ] Compute the answer as `C(m+n−2, m−1)` and explain where the formula comes from.

## 9. Practice problems

| # | Problem | Platform/ID | Difficulty | Pattern tested | Why it belongs here |
|---|---------|-------------|------------|----------------|---------------------|
| 1 | [Unique Paths](https://leetcode.com/problems/unique-paths/) | LeetCode 62 | Medium | Count right/down paths | The problem of this topic. Submit all four versions. |
| 2 | [Grid 1](https://atcoder.jp/contests/dp/tasks/dp_h) | AtCoder dp_h | Unrated (easy) | Path count with walls, modulo 10^9+7 | Same recurrence; a wall cell simply contributes 0. |
| 3 | [Grid Paths I](https://cses.fi/problemset/task/1638) | CSES 1638 | Unrated (easy) | Path count with traps, modulo | Same again; the start itself may be a trap. |
| 4 | [Unique Paths II](https://leetcode.com/problems/unique-paths-ii/) | LeetCode 63 | Medium | Path count with obstacles | The next topic; try it first on your own. |
| 5 | [Grid 2](https://atcoder.jp/contests/dp/tasks/dp_y) | AtCoder dp_y | Unrated (hard) | Huge grid, few walls: binomials + DP over walls | Uses the `C(m+n−2, m−1)` aside from this topic as its main tool. |

## 10. Navigation

⬅ Previous: [2D DP State Representation](../01-2d-dp-state-representation/) · ➡ Next: [Unique Paths II](../03-unique-paths-ii/) · 📖 [Walkthrough](walkthrough.md) · 💻 [Solutions](solution/)
