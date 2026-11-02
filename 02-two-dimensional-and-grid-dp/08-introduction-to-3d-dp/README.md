# Introduction to 3D DP

> **Module:** Two-Dimensional and Grid DP · **Difficulty:** Advanced · **Estimated time:** 90–120 min

## 1. What is this topic?

So far every grid problem in this module had **one** walker, and its position `(row, col)` was the whole state, so a 2D table was enough. This topic is about what happens when a problem has a **third quantity that changes independently**. The state then needs three numbers, the DP table becomes a 3D array `dp[a][b][c]`, and you have to think about loop order and memory in three dimensions.

We learn it through one problem: **LeetCode 1463 — Cherry Pickup II**, where two robots walk down the same grid at the same time.

## 2. Why does this concept exist?

DP works by giving a name to "where am I in the problem right now" (the state) and solving each state once. If two things can be in different places at the same moment, then "where am I" is no longer one position but a *combination* of positions. Two robots on a 7-column grid can be in 7 × 7 = 49 column combinations on every row. A 2D table indexed by one robot's position cannot tell those 49 situations apart, so it would mix up answers that are different. The third dimension exists so that every truly different situation gets its own cell.

## 3. What problem does it solve?

It solves problems where:

- **several agents move at once** (two robots, two people, a robot and a guard), or
- **one walker carries extra information** that changes the future (keys collected, moves left, items used), or
- an index plus **two budgets** shrink independently (for example "first i items, weight left w, count left k").

Cherry Pickup II is the cleanest first example: two robots, both moving down one row per step, collect cherries, and a cell they share is counted once. The best answer depends on **both** columns at once.

## 4. Prerequisites

- [2D DP State Representation](../01-2d-dp-state-representation/) — rows and columns as state, fill order.
- [Minimum Path Sum](../04-minimum-path-sum/) — rolling a 2D table down to one row.
- [Maximum Falling Path Sum](../06-maximum-falling-path-sum/) — moves "down-left / down / down-right", which each robot uses here.
- [Dungeon Game](../07-dungeon-game/) — choosing to fill a table from the bottom up.
- [Space Optimization Basics](../../00-foundations/15-space-optimization-basics/) — keeping only the layers you still need.

## 5. What you will learn

- How to recognize that a state needs a third number ("what else changes independently?").
- How to spot a **shared** quantity that lets you *avoid* a fourth dimension (both robots are always on the same row).
- How to write `f(row, col1, col2)` as a recursion, then memoize it in a 3D array.
- How to fill a 3D table layer by layer, and why the order inside a layer does not matter.
- How to cut memory from `rows × cols × cols` down to `cols × cols` by keeping one layer.
- How to handle "two agents on the same cell" without double counting.

## 6. Important terminology

| Term | Meaning in plain English |
|---|---|
| Dimension (of a DP) | One number in the state. `dp[i]` is 1D, `dp[i][j]` is 2D, `dp[i][j][k]` is 3D. |
| Agent | Something that moves through the problem and makes choices: a robot, a person, a pointer. |
| Layer | All cells of a 3D table with the same first index, e.g. `dp[2][*][*]`. It is a 2D grid. |
| Shared parameter | A number that is the same for every agent at every moment (here: the row), so it is stored once. |
| Combined choice | One move for every agent taken together. 3 moves × 3 moves = 9 combined choices. |
| Collision | Two agents standing on the same cell. The cell's reward must be counted once. |
| State space | The set of all possible states. Its size is the product of the ranges of each dimension. |

## 7. Where this is used in real problems

- **Two walkers on a grid:** Cherry Pickup II (this topic), Cherry Pickup (next topic).
- **Walker + moves left:** Out of Boundary Paths and Knight Probability in Chessboard use `(moves left, row, col)`.
- **Strings with a budget:** "edit two strings with at most k operations" uses `(i, j, k)`.
- **Knapsack with two limits:** weight and count limits together give `(item, weight, count)`.
- **Stock trading with a transaction limit** (later, in module 04) uses `(day, transactions left, holding?)`.

## 8. After completing this topic you should be able to…

- [ ] Explain in one sentence why one robot needs a 2D state but two robots need a 3D state.
- [ ] Explain why we do **not** need `row1` and `row2` separately.
- [ ] Write the recurrence for Cherry Pickup II, including the "same cell counts once" rule.
- [ ] Fill a 3D table bottom-up and say which layer each layer depends on.
- [ ] Reduce the memory to two `cols × cols` layers and explain why that is safe.
- [ ] Count the states and the work per state to get `O(rows · cols² · 9)`.

## 9. Practice problems

| # | Problem | Platform/ID | Difficulty | Pattern tested | Why it belongs here |
|---|---|---|---|---|---|
| 1 | [Minimum Path Sum](https://leetcode.com/problems/minimum-path-sum/) | LeetCode 64 | Medium | 2D grid DP, one walker | Warm-up: re-check the 2D baseline before adding a dimension. |
| 2 | [Out of Boundary Paths](https://leetcode.com/problems/out-of-boundary-paths/) | LeetCode 576 | Medium | `(moves left, row, col)` counting | A third dimension that is a *budget*, not a second agent. |
| 3 | [Knight Probability in Chessboard](https://leetcode.com/problems/knight-probability-in-chessboard/) | LeetCode 688 | Medium | `(moves left, row, col)` probability | Same 3D shape as 576 but with probabilities; layer-by-layer filling. |
| 4 | [Cherry Pickup II](https://leetcode.com/problems/cherry-pickup-ii/) | LeetCode 1463 | Hard | Two agents, shared row, `(row, col1, col2)` | The problem taught in this topic; solve it again from scratch. |
| 5 | [Cherry Pickup](https://leetcode.com/problems/cherry-pickup/) | LeetCode 741 | Hard | Two agents with a derived coordinate | The next topic: two walkers again, but the shared quantity is the step count. |

## 10. Navigation

⬅ Previous: [Dungeon Game](../07-dungeon-game/) · ➡ Next: [Cherry Pickup](../09-cherry-pickup/) · 📖 [Walkthrough](walkthrough.md) · 💻 [Solutions](solution/)
