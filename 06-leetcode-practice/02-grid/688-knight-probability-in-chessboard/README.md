# 688. Knight Probability in Chessboard

> **Pattern:** 3D grid DP (moves left × row × column) over probabilities · **Builds on:** [Introduction to 3D DP](../../../02-two-dimensional-and-grid-dp/08-introduction-to-3d-dp/) · **Difficulty:** Medium

## Problem overview

A chess knight starts at `(row, column)` on an `n × n` board and makes exactly `k` moves. Each
move picks one of the 8 knight moves uniformly at random, even ones that leave the board. Once
the knight is off the board it stays off. Return the probability that it is still on the board
after `k` moves.

## Link & metadata

| Field | Value |
|---|---|
| LeetCode | [688. Knight Probability in Chessboard](https://leetcode.com/problems/knight-probability-in-chessboard/) |
| Difficulty | Medium |
| Pattern | "steps remaining × position" DP where values are probabilities (averages) instead of counts |
| Prerequisite topic | [`02-two-dimensional-and-grid-dp/08-introduction-to-3d-dp`](../../../02-two-dimensional-and-grid-dp/08-introduction-to-3d-dp/) |
| Why this pattern | Same structure as [576. Out of Boundary Paths](../576-out-of-boundary-paths/): the knight may revisit squares, so moves-left is part of the state |

## Examples

| n, k, start | Answer | Why |
|---|---|---|
| 3, 2, (0,0) | 0.0625 | 2 of 8 first moves stay on; from each, 2 of 8 again: (2/8)·(2/8) |
| 1, 0, (0,0) | 1.0 | no moves, still on |
| 1, 1, (0,0) | 0.0 | every move leaves a 1×1 board |
| 8, 1, (0,0) | 0.25 | 2 of 8 moves from a corner stay on |

## Brute-force intuition

Enumerate all 8^k move sequences and count those that never leave the board; divide by 8^k.
With `k = 100` that is hopeless. But the probability of surviving the **rest** of the walk
depends only on the current square and the number of moves left: `k · n²` situations, at most
100 · 625 = 62 500.

**Counting vs probability.** Instead of counting surviving sequences and dividing at the end
(the counts would overflow), average as we go: each move has probability 1/8.

## Deriving the DP (7 questions)

### 1. What is changing?
The number of moves left `k`, and the knight's square `(r, c)`.

### 2. What does the state represent?
`f(k, r, c)` = the probability that a knight standing on `(r, c)` is still on the board after
making `k` more random moves.

### 3. What are the choices?
The 8 knight moves `(±1, ±2)` and `(±2, ±1)`, each taken with probability 1/8.

### 4. What is the recurrence?
```
f(k, r, c) = Σ over the 8 moves landing on (nr, nc) INSIDE the board of  f(k-1, nr, nc) / 8
```
A move that leaves the board contributes 0 (the knight is gone), so it is simply skipped. This is
the **law of total probability**: average the outcome over the equally likely first moves.

### 5. What are the base cases?
`f(0, r, c) = 1` for every square on the board: with no moves left, the knight is certainly on.

### 6. What is the order of computation?
Layer `k` reads only layer `k-1`: compute `k = 1, 2, …` in order.

### 7. Can the state space be optimized?
Two `n × n` layers are enough.

## Recursive solution

```java
static double stay(int k, int r, int c) {
    if (r < 0 || r >= n || c < 0 || c >= n) return 0.0;   // fell off
    if (k == 0) return 1.0;
    double p = 0;
    for (int[] m : MOVES) p += stay(k - 1, r + m[0], c + m[1]) / 8.0;
    return p;
}
```

## Memoization

Cache by `(k, r, c)` in a `double[][][]` with `-1` meaning unknown. See
[`solution/Memoization.java`](solution/Memoization.java).

## Tabulation

`dp[k][r][c]`, with layer 0 filled with 1.0. See [`solution/Tabulation.java`](solution/Tabulation.java).

## Space optimization

Two layers, swapped after each step. See [`solution/Optimized.java`](solution/Optimized.java).

## Dry run

`n = 3, k = 2, start (0,0)`. On a 3×3 board, the centre has no legal moves, and every other square
has exactly 2.

Layer 1 (`f(1, r, c)` = legal moves / 8):

```
0.25  0.25  0.25
0.25  0.00  0.25
0.25  0.25  0.25
```

Layer 2 at `(0,0)`: legal moves go to `(1,2)` and `(2,1)`.

| move to | f(1, ·) | contribution (/8) |
|---|---|---|
| (1,2) | 0.25 | 0.03125 |
| (2,1) | 0.25 | 0.03125 |
| 6 others | off board | 0 |

`f(2, 0, 0) = 0.0625`.

## Complexity

| Approach | Time | Space | Reason |
|---|---|---|---|
| Brute force | O(8^k) | O(k) | 8 branches per move |
| Memoization | O(k·n²) | O(k·n²) | each state once, 8 moves each |
| Tabulation | O(k·n²) | O(k·n²) | k layers |
| Optimized | O(k·n²) | O(n²) | two layers |

## Why the optimized approach is better

Same arithmetic, but memory no longer grows with `k`.

## Common mistakes

- **Counting sequences with `long` and dividing by 8^k.** 8¹⁰⁰ overflows; average step by step.
- **Base case 0 instead of 1.** Probability of survival with no moves left is 1.
- **Comparing doubles with `==` in tests.** Use a small tolerance.
- **Missing one of the 8 knight moves.** List them explicitly.

## Key takeaway

A probability DP is a counting DP where each choice is weighted by its probability. The state
design (moves left × position) is identical to path counting.

## Related problems

- [576. Out of Boundary Paths](../576-out-of-boundary-paths/), the counting twin (paths that leave)
- [935. Knight Dialer](../../05-stocks-and-state-machines/935-knight-dialer/), knight moves on a phone keypad
- Previous: [576. Out of Boundary Paths](../576-out-of-boundary-paths/) · Next: [1289. Minimum Falling Path Sum II](../1289-minimum-falling-path-sum-ii/)
