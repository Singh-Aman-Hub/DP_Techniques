# Cherry Pickup

> **Module:** Two-Dimensional and Grid DP · **Difficulty:** Advanced · **Estimated time:** 120–150 min

## 1. What is this topic?

This topic solves **LeetCode 741 — Cherry Pickup**: walk from the top-left corner of a square grid to the bottom-right corner moving only right or down, then walk back moving only left or up, collecting as many cherries as possible while avoiding thorns. It is the capstone of the grid module because the obvious ideas (solve the two trips separately, or take the best path twice) are **wrong**, and the right idea is a change of viewpoint: the return trip is just a second forward trip, and the two trips can be walked **at the same time**.

## 2. Why does this concept exist?

Many problems describe a process in time order ("first do this, then come back and do that"). The natural DP follows that order, but here the two halves are not independent: what the first trip takes changes what the second trip can get. Trying to remember "which cherries are already gone" would need the whole path in the state — far too big.

The **reframing** technique exists to escape this. We find an equivalent problem whose state is small: two walkers moving forward together. Because they move in lockstep, the only place they can ever share a cell is at the same moment, which makes the "already picked" problem trivial to handle.

## 3. What problem does it solve?

- Collecting rewards on **two paths that interact** (shared cells must not be double counted).
- Any "go there and come back" grid problem where moves are monotone (only right/down, or only left/up).
- More generally, it shows how to **derive one coordinate from others** (column = step − row) to keep a 3D DP instead of a 4D or 5D one.

## 4. Prerequisites

- [Introduction to 3D DP](../08-introduction-to-3d-dp/) — two agents, a shared parameter, a 3D table, layer-by-layer filling.
- [Unique Paths II](../03-unique-paths-ii/) — blocked cells in a grid DP.
- [Minimum Path Sum](../04-minimum-path-sum/) — best single right/down path.
- [Dungeon Game](../07-dungeon-game/) — choosing the direction of the DP.
- [Optimal Substructure](../../00-foundations/05-optimal-substructure/) — why "best total" must be built from best sub-answers, and when greedy is not enough.

## 5. What you will learn

- Why "best path, erase cherries, best path again" can lose, with a 4 × 4 counterexample.
- How to turn "go and come back" into "two people go forward together".
- How to use the **step number** so that columns are derived: `c = step − r`.
- How to count a shared cell exactly once (`r1 == r2` at the same step means the same cell).
- How to use a **minus-infinity sentinel** for thorns and unreachable states without integer overflow.
- How to fill the 3D table step by step, then keep only one `n × n` layer per step.

## 6. Important terminology

| Term | Meaning in plain English |
|---|---|
| Thorn | A cell with value `-1`; nobody may step on it. |
| Round trip | The walk down to `(n-1, n-1)` and back to `(0, 0)`. |
| Reframing | Replacing a problem with an equivalent one that is easier to model. |
| Step (time) | How many moves each walker has made. After `step` moves, `row + col = step`. |
| Anti-diagonal | All cells with the same `row + col`. Both walkers are always on the same anti-diagonal. |
| Derived coordinate | A number you can compute from the state, so you don't store it (`col = step − row`). |
| Sentinel (minus infinity) | A special very negative value meaning "impossible", which `max` never picks. |
| Greedy | Making the choice that looks best right now without considering later effects. |

## 7. Where this is used in real problems

- **Cherry Pickup** itself and its family (Cherry Pickup II from the previous topic).
- "Two paths through a grid with maximum total, shared cells counted once" — a classic contest problem.
- Any DP where several pointers advance in lockstep: two pointers into two strings in LCS-type problems advance independently, but in "walk two sequences together" problems a shared time index removes a dimension.
- The reasoning "greedy twice fails, joint DP succeeds" reappears in scheduling and in problems with two resources.

## 8. After completing this topic you should be able to…

- [ ] Show on a concrete grid why taking the best path twice is not optimal.
- [ ] Explain why a return path (left/up) is the same as a forward path (right/down) read backwards.
- [ ] Write the state `(step, r1, r2)` and derive `c1`, `c2` from it.
- [ ] Explain why two walkers can only share a cell at the same step, and handle it with `r1 == r2`.
- [ ] Use a minus-infinity sentinel safely and return `max(0, answer)`.
- [ ] Fill the table forward by step and reduce memory to `O(n²)`.

## 9. Practice problems

| # | Problem | Platform/ID | Difficulty | Pattern tested | Why it belongs here |
|---|---|---|---|---|---|
| 1 | [Minimum Path Sum](https://leetcode.com/problems/minimum-path-sum/) | LeetCode 64 | Medium | Best single right/down path | The single-path building block that the greedy idea uses twice. |
| 2 | [Unique Paths II](https://leetcode.com/problems/unique-paths-ii/) | LeetCode 63 | Medium | Grid DP with blocked cells | Practice handling cells that can't be entered, like thorns. |
| 3 | [Cherry Pickup II](https://leetcode.com/problems/cherry-pickup-ii/) | LeetCode 1463 | Hard | Two agents, shared row | Same "two walkers at once" idea with a simpler shared coordinate. |
| 4 | [Cherry Pickup](https://leetcode.com/problems/cherry-pickup/) | LeetCode 741 | Hard | Two walkers, derived column, sentinel for blocked states | The problem taught here; re-solve it from a blank file. |
| 5 | [Minimum Falling Path Sum II](https://leetcode.com/problems/minimum-falling-path-sum-ii/) | LeetCode 1289 | Hard | Row-by-row DP with a constraint between choices | Another Hard grid DP where the naive transition must be rethought. |
| 6 | [Longest Increasing Path in a Matrix](https://leetcode.com/problems/longest-increasing-path-in-a-matrix/) | LeetCode 329 | Hard | Memoized DFS on a grid, no fixed fill order | Grid DP where the order of computation comes from the values, not from rows/columns. |

## 10. Navigation

⬅ Previous: [Introduction to 3D DP](../08-introduction-to-3d-dp/) · ➡ Next: [Subsequences Fundamentals](../../03-subsequence-and-knapsack-dp/01-subsequences-fundamentals/) · 🏠 [Module overview](../README.md) · 📖 [Walkthrough](walkthrough.md) · 💻 [Solutions](solution/)
