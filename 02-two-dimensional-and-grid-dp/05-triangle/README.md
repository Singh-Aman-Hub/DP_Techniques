# Triangle

> **Module:** Two-Dimensional and Grid DP · **Difficulty:** Intermediate · **Estimated time:** 60–90 min

## 1. What is this topic?

This topic solves [LeetCode 120 — Triangle](https://leetcode.com/problems/triangle/): find the cheapest path from the top of a triangle of numbers to its bottom row, where each step goes to one of the two numbers directly below.

It is the first grid problem in this module whose rows have **different widths**: row 0 has 1 cell, row 1 has 2 cells, row `n-1` has `n` cells. That one change forces a new question: *which direction should the table be filled in?* Filling from the top creates awkward edge cases on both sides of every row; filling from the **last row upward** makes every edge case disappear.

## 2. Why does this concept exist?

In [Minimum Path Sum](../04-minimum-path-sum/) every cell had a neighbor above and a neighbor to the left, except on the border. In a triangle the shape itself is irregular, so a top-down fill has to ask "does this cell have a parent on the left? on the right?" for every row. Many real DP problems have irregular shapes like this, and the cleanest fix is often not more `if` statements but a **different fill direction**. This topic teaches you to look for that direction.

## 3. What problem does it solve?

Any problem where you descend through levels, each level offers a few positions, and from a position you may only move to "nearby" positions on the next level. You want the best (minimum or maximum) total collected along the way. The bottom-up idea "the best answer from here = my value + best answer from my children" is the core of many later topics, including DP on trees.

## 4. Prerequisites

- [2D DP state representation](../01-2d-dp-state-representation/) — rows and columns as state, fill order
- [Minimum Path Sum](../04-minimum-path-sum/) — the first min-over-grid problem and the 1D-row space trick
- [Transition dependencies and computation order](../../00-foundations/11-transition-dependencies-and-computation-order/)
- [Space optimization basics](../../00-foundations/15-space-optimization-basics/)

## 5. What you will learn

- How to index a triangle stored as a jagged array (`row r` has `r + 1` cells).
- The two possible state definitions: "best path **from the apex to** this cell" and "best path **from this cell to** the bottom".
- Why the second definition, filled from the last row upward, needs no edge cases and returns the answer in a single cell.
- How to compress the table into one array of length `n`, updated in place.

## 6. Important terminology

| Term | Meaning in plain English |
|---|---|
| Triangle / jagged array | A 2D array whose rows have different lengths. Here row `r` has exactly `r + 1` entries. |
| Apex | The single cell at the top, `(0, 0)`. |
| Children of `(r, c)` | The two cells it can step to: `(r + 1, c)` and `(r + 1, c + 1)`. |
| Parents of `(r, c)` | The cells that can step into it: `(r - 1, c - 1)` and `(r - 1, c)` — but only those that exist. |
| Bottom-up from the last row | Filling the DP table starting at the last row and moving toward row 0. |
| In-place update | Overwriting an array entry with its new value while other entries still hold old values. |

## 7. Where this is used in real problems

- Any "descend through levels" path problem: LC 120 itself, and the falling-path problems in the next topic.
- Decision trees where each node has a fixed set of children and you want the best root-to-leaf total.
- The habit of asking "is the reversed direction simpler?" returns in [Dungeon Game](../07-dungeon-game/), where the reversed direction is not just simpler but the only one that works.

## 8. After completing this topic you should be able to…

- [ ] Explain what `dp[r][c]` means in both directions and which one gives the answer in `dp[0][0]`.
- [ ] Write the bottom-up recurrence `dp[r][c] = t[r][c] + min(dp[r+1][c], dp[r+1][c+1])` from memory.
- [ ] List the two edge cases a top-down fill needs and explain why the bottom-up fill has none.
- [ ] Reduce the memory to one array of length `n` and explain why the left-to-right in-place update is safe.

## 9. Practice problems

| # | Problem | Platform/ID | Difficulty | Pattern tested | Why it belongs here |
|---|---|---|---|---|---|
| 1 | [Minimum Path Sum](https://leetcode.com/problems/minimum-path-sum/) | LeetCode 64 | Medium | Min path in a rectangle | Warm-up: same min recurrence on a regular grid; compare its edge handling with the triangle's. |
| 2 | [Triangle](https://leetcode.com/problems/triangle/) | LeetCode 120 | Medium | Min path, variable-width rows | The problem taught in this topic. Solve it again from a blank file, then in O(n) space. |
| 3 | [Minimum Falling Path Sum](https://leetcode.com/problems/minimum-falling-path-sum/) | LeetCode 931 | Medium | Three children instead of two | Same "value + best of the cells below" idea with an extra diagonal; previews the next topic. |
| 4 | [Minimum Falling Path Sum II](https://leetcode.com/problems/minimum-falling-path-sum-ii/) | LeetCode 1289 | Hard | Many children per cell | Every cell of the next row is a child except one; teaches how to avoid an O(n^3) transition. |

## 10. Navigation

⬅ Previous: [Minimum Path Sum](../04-minimum-path-sum/) · ➡ Next: [Maximum Falling Path Sum](../06-maximum-falling-path-sum/) · 📖 [Walkthrough](walkthrough.md) · 💻 [Solutions](solution/)
