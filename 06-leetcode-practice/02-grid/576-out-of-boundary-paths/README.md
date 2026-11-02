# 576. Out of Boundary Paths

> **Pattern:** 3D grid DP (moves left × row × column), counting paths · **Builds on:** [Introduction to 3D DP](../../../02-two-dimensional-and-grid-dp/08-introduction-to-3d-dp/) · **Difficulty:** Medium

## Problem overview

A ball sits on cell `(startRow, startColumn)` of an `m × n` grid. Each move pushes it one cell up,
down, left or right, and it may leave the grid (after which it stops). Using **at most**
`maxMove` moves, count the move sequences that end with the ball outside the grid. Return the
count modulo 10⁹ + 7.

## Link & metadata

| Field | Value |
|---|---|
| LeetCode | [576. Out of Boundary Paths](https://leetcode.com/problems/out-of-boundary-paths/) |
| Difficulty | Medium |
| Pattern | Grid path counting with an extra "budget" dimension, so the state is 3D |
| Prerequisite topic | [`02-two-dimensional-and-grid-dp/08-introduction-to-3d-dp`](../../../02-two-dimensional-and-grid-dp/08-introduction-to-3d-dp/) (and [`02-unique-paths`](../../../02-two-dimensional-and-grid-dp/02-unique-paths/) for path counting) |
| Why this pattern | The ball can move in all four directions and revisit cells, so position alone does not describe a subproblem; the number of moves left must be part of the state |

## Examples

| m, n, maxMove, start | Answer | Why |
|---|---|---|
| 2, 2, 2, (0,0) | 6 | 2 ways out in 1 move (up, left) + 4 ways in 2 moves |
| 1, 3, 3, (0,1) | 12 | |
| 1, 1, 1, (0,0) | 4 | a 1×1 grid: every direction exits |
| any, 0 moves | 0 | no move, no exit |

## Brute-force intuition

From any cell, try all 4 directions, recursively, until the ball exits (count 1) or the moves
run out (count 0). That is 4^maxMove sequences, up to 4⁵⁰. But the future of the ball depends
only on **where it is** and **how many moves remain**, never on how it got there. There are
only `maxMove × m × n` such situations, at most 50·50·50 = 125 000.

```
paths(2 moves, (0,0))
 ├─ up    → outside           = 1
 ├─ left  → outside           = 1
 ├─ down  → paths(1, (1,0))   = 2   (exits down or left)
 └─ right → paths(1, (0,1))   = 2   (exits up or right)
                               ---
                                6
```

## Deriving the DP (7 questions)

### 1. What is changing?
The ball's row `r`, its column `c`, and the number of moves still allowed `k`.

### 2. What does the state represent?
`f(k, r, c)` = the number of ways to push the ball out of the grid, starting from `(r, c)`, using
**at most** `k` moves.

### 3. What are the choices?
The direction of the next move: up, down, left, right.

### 4. What is the recurrence?
```
f(k, r, c) = Σ over the 4 neighbours (nr, nc) of:
                1                    if (nr, nc) is outside the grid
                f(k-1, nr, nc)       otherwise
(all mod 10^9+7)
```
We **add** because different first moves give different sequences.

### 5. What are the base cases?
`f(0, r, c) = 0` for every cell inside: with no moves left, the ball cannot leave. (The
"outside → 1" case is handled when a move crosses the border.)

### 6. What is the order of computation?
`f(k, ·, ·)` uses only `f(k-1, ·, ·)`. So compute layer `k = 1`, then `k = 2`, …, `k = maxMove`;
within a layer, any cell order works.

### 7. Can the state space be optimized?
Only the previous layer is read: two `m × n` grids instead of `maxMove` of them.

## Recursive solution

```java
static long paths(int k, int r, int c) {
    if (r < 0 || r >= m || c < 0 || c >= n) return 1;   // just left the grid
    if (k == 0) return 0;
    return (paths(k-1, r-1, c) + paths(k-1, r+1, c) + paths(k-1, r, c-1) + paths(k-1, r, c+1)) % MOD;
}
```

## Memoization

Cache by `(k, r, c)`. See [`solution/Memoization.java`](solution/Memoization.java).

## Tabulation

`dp[k][r][c]`, layer by layer. See [`solution/Tabulation.java`](solution/Tabulation.java).

## Space optimization

Keep `previous[r][c]` (layer `k-1`) and `current[r][c]` (layer `k`), swapping after each layer.
See [`solution/Optimized.java`](solution/Optimized.java).

## Dry run

`m = 2, n = 2, maxMove = 2, start (0,0)`. In a 2×2 grid every cell touches the border on two sides.

Layer `k = 1` (each cell: 2 exits, inner neighbours contribute `f(0) = 0`):

```
2 2
2 2
```

Layer `k = 2`, cell `(0,0)`:

| move | lands on | contributes |
|---|---|---|
| up | outside | 1 |
| left | outside | 1 |
| down | (1,0) | f(1, 1, 0) = 2 |
| right | (0,1) | f(1, 0, 1) = 2 |

Total **6**.

## Complexity

Let `K = maxMove`.

| Approach | Time | Space | Reason |
|---|---|---|---|
| Brute force | O(4^K) | O(K) | 4 branches per move |
| Memoization | O(K·m·n) | O(K·m·n) | each state once with 4 additions |
| Tabulation | O(K·m·n) | O(K·m·n) | K layers of m·n cells |
| Optimized | O(K·m·n) | O(m·n) | two layers |

## Why the optimized approach is better

The 3D table can hold 125 000 longs; the two-layer version holds 5 000. Same arithmetic.

## Common mistakes

- **Forgetting the modulo** after each addition (the counts grow like 4^K).
- **Treating it like Unique Paths with only right/down moves.** The ball can go back and forth;
  that is exactly why `k` must be in the state, or the recursion would never end.
- **Writing into the layer you are reading** in the 1-layer attempt: you need two layers,
  because neighbours on both sides read the old values.

## Key takeaway

When movement can revisit cells, add a **"steps remaining"** dimension. The state becomes
`(steps, row, col)`, and each layer depends only on the previous one.

## Related problems

- [688. Knight Probability in Chessboard](../688-knight-probability-in-chessboard/), same 3D shape with probabilities
- [935. Knight Dialer](../../05-stocks-and-state-machines/935-knight-dialer/), "steps remaining × position" on a keypad
- Previous: [1277. Count Square Submatrices with All Ones](../1277-count-square-submatrices-with-all-ones/) · Next: [688. Knight Probability in Chessboard](../688-knight-probability-in-chessboard/)
