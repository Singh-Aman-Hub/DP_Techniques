# Maximum Falling Path Sum

> **Module:** Two-Dimensional and Grid DP · **Difficulty:** Intermediate · **Estimated time:** 60–90 min

## 1. What is this topic?

A **falling path** starts at any cell of the top row of a square grid and moves down one row at a time. From column `c` it may go to column `c - 1`, `c` or `c + 1` in the next row (down-left, straight down, down-right), as long as it stays inside the grid. The **maximum falling path sum** is the largest total a falling path can collect from the top row to the bottom row.

We teach the **maximum** version first because it is the classic textbook form. Then we show that [LeetCode 931 — Minimum Falling Path Sum](https://leetcode.com/problems/minimum-falling-path-sum/) is the identical pattern with `min` instead of `max`. **LeetCode 931 asks for the minimum**, and it is the judge problem for this topic: submit the minimum version there.

## 2. Why does this concept exist?

The earlier grid problems all had a fixed start cell (top-left corner or apex). Here the path may **start anywhere** in the top row and **end anywhere** in the bottom row, and each cell has **three** possible predecessors instead of two. These two twists appear constantly in real problems: "choose a starting point freely" and "move to a neighbor within distance 1". This topic shows how both fit into the same `dp[row][col]` table with no new machinery.

## 3. What problem does it solve?

Any layered problem where you pick one position per layer, consecutive positions may differ by at most a fixed amount, and you want the best total. Examples: steering a vehicle across lanes (change at most one lane per step), or choosing one item per day where the choice can only shift slightly from yesterday's choice.

## 4. Prerequisites

- [2D DP state representation](../01-2d-dp-state-representation/) — rows and columns as state, dependency arrows
- [Minimum Path Sum](../04-minimum-path-sum/) — min over a grid, 1D-row space optimization
- [Triangle](../05-triangle/) — descending through rows, edge cases at row borders
- [Space optimization basics](../../00-foundations/15-space-optimization-basics/)

## 5. What you will learn

- How a free start ("any cell in the top row") becomes a base row, and a free end becomes a max over the last row.
- How to handle three parents, two of which may fall outside the grid.
- Why the max and min versions are the same algorithm, and how to switch between them safely.
- Why a two-row optimization is needed here instead of a single in-place array.
- What changes in the harder variant, LeetCode 1289.

## 6. Important terminology

| Term | Meaning in plain English |
|---|---|
| Falling path | A path that visits exactly one cell per row, top to bottom, shifting by at most one column per step. |
| Parents of `(r, c)` | The cells a path can come from: `(r-1, c-1)`, `(r-1, c)`, `(r-1, c+1)`, whichever are inside the grid. |
| Free start / free end | The path may begin at any top-row cell and end at any bottom-row cell. |
| Sentinel | A placeholder value (such as `Integer.MIN_VALUE`) meaning "no valid value here"; it must never be added to. |
| Two-row optimization | Keeping only the previous row and the current row of the table. |

## 7. Where this is used in real problems

- [LeetCode 931](https://leetcode.com/problems/minimum-falling-path-sum/) directly (minimum version).
- [LeetCode 1289](https://leetcode.com/problems/minimum-falling-path-sum-ii/): the next row's column must simply be **different** from the current one; every other column is a parent.
- Square-detection problems such as [Maximal Square](https://leetcode.com/problems/maximal-square/), where each cell also reads three neighbors (up, left, up-left).

## 8. After completing this topic you should be able to…

- [ ] Define `dp[r][c]` for a falling path in one sentence and state its base row and answer cell(s).
- [ ] Write the three-parent recurrence with correct bounds checks.
- [ ] Convert the max version to the min version (and explain why the sentinel must change too).
- [ ] Solve LeetCode 931 in `O(n²)` time and `O(n)` extra space.
- [ ] Explain why LeetCode 1289 needs a "best and second-best of the previous row" trick.

## 9. Practice problems

| # | Problem | Platform/ID | Difficulty | Pattern tested | Why it belongs here |
|---|---|---|---|---|---|
| 1 | [Triangle](https://leetcode.com/problems/triangle/) | LeetCode 120 | Medium | Row-by-row descent, two parents | Warm-up from the previous topic: same layered idea with a fixed start. |
| 2 | [Minimum Falling Path Sum](https://leetcode.com/problems/minimum-falling-path-sum/) | LeetCode 931 | Medium | Falling path, three parents, free start/end | The judge problem for this topic. It asks for the **minimum**; use `MinFallingPathSum931.java`. |
| 3 | [Maximal Square](https://leetcode.com/problems/maximal-square/) | LeetCode 221 | Medium | Cell depends on three neighbors | Same "look at three neighbors in the table" shape, applied to geometry instead of sums. |
| 4 | [Count Square Submatrices with All Ones](https://leetcode.com/problems/count-square-submatrices-with-all-ones/) | LeetCode 1277 | Medium | Three-neighbor recurrence, summing the table | Reuses the Maximal Square table; the answer is the sum of all cells instead of one max. |
| 5 | [Minimum Falling Path Sum II](https://leetcode.com/problems/minimum-falling-path-sum-ii/) | LeetCode 1289 | Hard | Falling path with "any different column" parents | Harder variant: `n - 1` parents per cell; needs the best/second-best trick to stay `O(n²)`. |

## 10. Navigation

⬅ Previous: [Triangle](../05-triangle/) · ➡ Next: [Dungeon Game](../07-dungeon-game/) · 📖 [Walkthrough](walkthrough.md) · 💻 [Solutions](solution/)
