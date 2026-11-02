# 2D DP State Representation

> **Module:** Two-Dimensional and Grid DP · **Difficulty:** Beginner · **Estimated time:** 75–110 min

## 1. What is this topic?

This topic teaches you how to think about a DP problem whose state needs **two numbers** instead of one. You will learn to draw the DP table as a grid, to mark which neighbouring cells each cell depends on (the *dependency arrows*), to choose an order that fills every cell after the cells it needs, and to avoid the indexing mistakes that cause most bugs in 2D DP.

There is no single LeetCode problem here. We use a tiny "count the paths on a board" example as a picture to think with. The full problem (LeetCode 62) is solved in the next topic.

## 2. Why does this concept exist?

In Module 01 one number described every subproblem: "the best loot from house `i` onward", "the number of ways to reach stair `i`". That works only because **one** thing changed as you made decisions.

On a board, a robot that moves right or down changes *two* things: its row and its column. Knowing only the row does not tell you how many moves are left; knowing only the column does not either. If you try to squeeze the state into one number you either lose information (wrong answers) or end up re-encoding the two numbers anyway. A 2D table `dp[r][c]` is the honest way to store "the answer for the subproblem described by `r` and `c`".

## 3. What problem does it solve?

It gives you a repeatable recipe for any problem where two quantities change independently:

- position on a grid (row, column),
- position in two strings (index in string A, index in string B),
- item index and remaining capacity (knapsack),
- day and "holding a stock or not".

The recipe is always the same: name the two quantities, draw the table, draw the arrows, choose a fill order, set the base cases on the border, fill.

## 4. Prerequisites

- [State Representation](../../00-foundations/09-state-representation/)
- [State Transitions and Base Cases](../../00-foundations/10-state-transitions-and-base-cases/)
- [Transition Dependencies and Computation Order](../../00-foundations/11-transition-dependencies-and-computation-order/)
- [Converting Memoization to Tabulation](../../00-foundations/14-converting-memoization-to-tabulation/)
- [Climbing Stairs](../../01-one-dimensional-dp/02-climbing-stairs/) (the 1D version of "count the ways")

## 5. What you will learn

- How to tell, by listing "what changes", that a problem needs a 2D state.
- How to read `dp[r][c]` as a sentence and draw it as a board.
- How to draw dependency arrows (from above, from the left, from the diagonal) and why they decide the fill order.
- Row-by-row, column-by-column and diagonal fill orders, and how to check that one is valid.
- Two directions of recursion: backward ("how did I get *here* from the start?") and forward ("how do I get from *here* to the goal?").
- The classic indexing pitfalls: `m` vs `n`, `dp[r][c]` vs `dp[c][r]`, out-of-bounds neighbours, and the extra padding row/column.

## 6. Important terminology

| Term | Meaning in plain English |
|------|--------------------------|
| 2D state | A subproblem described by two numbers, such as `(row, col)`. |
| DP table / grid | The 2D array `dp[rows][cols]` that stores one answer per state. |
| Cell | One entry `dp[r][c]` of the table. |
| Dependency arrow | An arrow from cell A to cell B meaning "B's formula reads A". |
| Fill order | The order in which tabulation computes cells. Valid if every arrow points from an earlier cell to a later one. |
| Row-major order | Fill row 0 left→right, then row 1 left→right, and so on. |
| Base row / base column | The first row and first column, whose cells are often known without a formula. |
| Padding | An extra row and/or column (index 0) added so that "the neighbour above row 0" exists and holds a neutral value. |
| Backward recursion | `f(r, c)` = answer for reaching `(r, c)` from the start; recursion moves toward `(0, 0)`. |
| Forward recursion | `g(r, c)` = answer for going from `(r, c)` to the goal; recursion moves toward the goal. |

## 7. Where this is used in real problems

- Every grid problem in this module (Unique Paths, Minimum Path Sum, Triangle, Falling Path Sum, Dungeon Game).
- 2D string DP: Longest Common Subsequence, Edit Distance (`dp[i][j]` over two strings) in Module 03.
- 0/1 knapsack (`dp[item][capacity]`) in Module 03.
- Interval DP (`dp[left][right]`) in Module 04, where the fill order is by interval length instead of by row.

## 8. After completing this topic you should be able to…

- [ ] Explain why a robot on a board needs a 2D state, in one sentence.
- [ ] Write `dp[r][c] = …` as a complete English sentence for a grid problem.
- [ ] Draw the dependency arrows of a recurrence and name a valid fill order.
- [ ] Say which cells are base cases and why.
- [ ] Write both the backward and the forward recursion for the same grid question.
- [ ] Spot an out-of-bounds neighbour or a swapped `m`/`n` before running the code.

## 9. Practice problems

These are warm-ups for the idea of a 2D state. Try them after reading the walkthrough, or come back after [Unique Paths](../02-unique-paths/).

| # | Problem | Platform/ID | Difficulty | Pattern tested | Why it belongs here |
|---|---------|-------------|------------|----------------|---------------------|
| 1 | [Grid 1](https://atcoder.jp/contests/dp/tasks/dp_h) | AtCoder dp_h | Unrated (easy) | Count right/down paths with walls | The purest 2D state `(row, col)` with up/left arrows. |
| 2 | [Grid Paths I](https://cses.fi/problemset/task/1638) | CSES 1638 | Unrated (easy) | Same as above with traps, modulo | Same state, practise base cases on a blocked start. |
| 3 | [Unique Paths](https://leetcode.com/problems/unique-paths/) | LeetCode 62 | Medium | Count paths, no obstacles | The example of this topic, solved fully in the next topic. |
| 4 | [Count Square Submatrices with All Ones](https://leetcode.com/problems/count-square-submatrices-with-all-ones/) | LeetCode 1277 | Medium | Cell depends on up, left **and diagonal** | Practises the three-arrow dependency picture. |
| 5 | [Out of Boundary Paths](https://leetcode.com/problems/out-of-boundary-paths/) | LeetCode 576 | Medium | `(row, col)` plus moves left | A preview: a third changing quantity appears (see [Introduction to 3D DP](../08-introduction-to-3d-dp/)). |

## 10. Navigation

⬅ Previous: [Maximum Subarray: Kadane vs DP](../../01-one-dimensional-dp/09-maximum-subarray-kadane-vs-dp/) · ➡ Next: [Unique Paths](../02-unique-paths/) · 📖 [Walkthrough](walkthrough.md) · 💻 [Solutions](solution/)
