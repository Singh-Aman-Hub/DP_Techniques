# Unique Paths II

> **Module:** Two-Dimensional and Grid DP · **Difficulty:** Beginner–Intermediate · **Estimated time:** 60–90 min

## 1. What is this topic?

[LeetCode 63 — Unique Paths II](https://leetcode.com/problems/unique-paths-ii/) is [Unique Paths](../02-unique-paths/) with **obstacles**: some cells of the board are blocked, and a route may not pass through them. You still count right/down routes from the top-left to the bottom-right cell.

The recurrence barely changes. What changes is how carefully you must treat the **base row and base column**, and that is the real lesson of this topic.

## 2. Why does this concept exist?

Real grids are rarely empty. Once a cell can be "forbidden", two things break in the obstacle-free solution:

1. The formula `C(m+n−2, m−1)` no longer applies — you need the DP.
2. The shortcut "row 0 and column 0 are all 1" becomes **wrong**: one obstacle on the top row cuts off *every* cell to its right, not just itself.

Learning to handle "a state that is impossible" (count 0) and to propagate that through the base cases is a skill you will reuse in knapsack (an impossible sum), in [Dungeon Game](../07-dungeon-game/) and in [Cherry Pickup](../09-cherry-pickup/) (blocked cells).

## 3. What problem does it solve?

Counting paths on a grid with forbidden cells: maze routes, robot navigation with walls, and the AtCoder/CSES "grid with walls" family (which add a modulus).

## 4. Prerequisites

- [Unique Paths](../02-unique-paths/) — the base problem, all four versions.
- [2D DP State Representation](../01-2d-dp-state-representation/) — off-board cells and fill order.
- [State Transitions and Base Cases](../../00-foundations/10-state-transitions-and-base-cases/).

## 5. What you will learn

- How an obstacle turns into a single rule: "a blocked cell has 0 routes".
- The base-row / base-column trap, with a concrete input where the naive code gives the wrong answer.
- Edge cases: blocked start, blocked end, single row, single column.
- Why `0` cannot be used as "not computed" in the memo any more.
- A one-row version that handles the border with no special loops at all.

## 6. Important terminology

| Term | Meaning in plain English |
|------|--------------------------|
| Obstacle | A cell marked `1` in the input grid. A route may not enter it. |
| Free cell | A cell marked `0`. |
| Reachable | A cell with at least one route from the start to it. |
| Base-row trap | The bug of setting every top-row cell to 1, ignoring that an obstacle blocks all cells after it. |
| Sentinel | A special value (here `-1`) meaning "not computed yet", chosen so it can never be a real answer. |

## 7. Where this is used in real problems

- Grid path counting with walls and a modulus: AtCoder Grid 1, CSES Grid Paths I.
- Any DP where some states are impossible: "impossible" becomes 0 for counting, `+∞` for minimising, `−∞` for maximising (see [Cherry Pickup](../09-cherry-pickup/)).
- The same border care appears in [Minimum Path Sum](../04-minimum-path-sum/), where the first row is a running sum instead of all 1s.

## 8. After completing this topic you should be able to…

- [ ] State the recurrence with the obstacle rule in one line.
- [ ] Explain, with an example, why "top row = all 1s" is wrong once obstacles exist.
- [ ] Handle a blocked start and a blocked end correctly.
- [ ] Choose a correct "not computed" sentinel for a memo where 0 is a valid answer.
- [ ] Write the one-row solution that needs no separate base-row loop.

## 9. Practice problems

| # | Problem | Platform/ID | Difficulty | Pattern tested | Why it belongs here |
|---|---------|-------------|------------|----------------|---------------------|
| 1 | [Unique Paths II](https://leetcode.com/problems/unique-paths-ii/) | LeetCode 63 | Medium | Path count with obstacles | The problem of this topic. |
| 2 | [Grid 1](https://atcoder.jp/contests/dp/tasks/dp_h) | AtCoder dp_h | Unrated (easy) | Path count with walls, modulo 10^9+7 | Same DP with `#` walls and a modulus. |
| 3 | [Grid Paths I](https://cses.fi/problemset/task/1638) | CSES 1638 | Unrated (easy) | Path count with traps, modulo | The start cell itself may be a trap — test your edge cases. |
| 4 | [Out of Boundary Paths](https://leetcode.com/problems/out-of-boundary-paths/) | LeetCode 576 | Medium | Path counting with a move budget | Counting on a grid when a third quantity (moves left) changes. |
| 5 | [Grid 2](https://atcoder.jp/contests/dp/tasks/dp_y) | AtCoder dp_y | Unrated (hard) | Huge grid, few walls | Obstacle counting when the grid is too big for a table; combines binomials with DP over the walls. |

## 10. Navigation

⬅ Previous: [Unique Paths](../02-unique-paths/) · ➡ Next: [Minimum Path Sum](../04-minimum-path-sum/) · 📖 [Walkthrough](walkthrough.md) · 💻 [Solutions](solution/)
