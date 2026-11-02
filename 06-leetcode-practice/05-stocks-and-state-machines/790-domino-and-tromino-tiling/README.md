# 790. Domino and Tromino Tiling

> **Pattern:** Tiling DP with a "profile" state (fully covered vs. one cell sticking out) · **Builds on:** [State Machine DP](../../../04-advanced-dp-patterns/12-state-machine-dp/) · **Difficulty:** Medium

## Problem overview

Count the ways to tile a `2 × n` board using 2×1 dominoes (either orientation) and L-shaped
trominoes (any rotation). Two tilings differ if some pair of adjacent cells is covered by one
tile in one tiling and not in the other. Return the count modulo 10⁹ + 7.

## Link & metadata

| Field | Value |
|---|---|
| LeetCode | [790. Domino and Tromino Tiling](https://leetcode.com/problems/domino-and-tromino-tiling/) |
| Difficulty | Medium |
| Pattern | Two-state DP along the board: "board flush" vs "one cell overhanging" |
| Prerequisite topic | [`04-advanced-dp-patterns/12-state-machine-dp`](../../../04-advanced-dp-patterns/12-state-machine-dp/) (and [`01-one-dimensional-dp/02-climbing-stairs`](../../../01-one-dimensional-dp/02-climbing-stairs/), the domino-only version) |
| Why this pattern | With dominoes only this is Climbing Stairs. Trominoes create a second kind of boundary (a jagged edge), so the DP needs one state per boundary shape |

## Examples

| n | Answer |
|---|---|
| 1 | 1 (one vertical domino) |
| 2 | 2 (two vertical, or two horizontal) |
| 3 | 5 |
| 4 | 11 |
| 5 | 24 |

## Brute-force intuition

Fill the board left to right, always covering the leftmost empty cell, trying each tile that
fits. Many different partial tilings leave the **same boundary**: everything up to column `i`
covered, and nothing beyond, or everything up to `i` plus one cell of column `i + 1`. Only
the boundary matters for how to finish. So the boundary shape is the state.

```
FULL(i):  columns 1..i covered          PART(i): columns 1..i covered + top cell of column i+1
  ■ ■ ■ │                                 ■ ■ ■ ■
  ■ ■ ■ │                                 ■ ■ ■ │
        i                                       i
```
(By symmetry, "bottom cell sticking out" has the same count as "top cell sticking out".)

## Deriving the DP (7 questions)

### 1. What is changing?
How many columns are covered, and the shape of the right edge (flush or one cell overhanging).

### 2. What does the state represent?
- `full[i]` = number of ways to cover exactly the first `i` columns.
- `part[i]` = number of ways to cover the first `i` columns **plus the top cell** of column `i + 1`.

### 3. What are the choices?
Look at the **last tile placed** that completes the shape.

For `full[i]`:
```
a) vertical domino in column i                         ← from full[i-1]
b) two horizontal dominoes in columns i-1..i           ← from full[i-2]
c) a tromino filling column i and one cell of i-1      ← from part[i-2], top or bottom: ×2
```
For `part[i]` (top of column `i+1` sticks out):
```
d) horizontal domino on the top row, columns i..i+1    ← from part[i-1] with the BOTTOM of column i sticking out
e) tromino covering column i and the top of i+1        ← from full[i-1]
```

### 4. What is the recurrence?
```
full[i] = full[i-1] + full[i-2] + 2 · part[i-2]
part[i] = part[i-1] + full[i-1]
```
Eliminating `part` gives the famous one-liner `full[n] = 2·full[n-1] + full[n-3]` (subtract the
`full[i-1]` equation from `full[i]` and use the `part` equation). It is correct, but it is
much harder to *invent* than the two-state version, which is why we derive the states first.

### 5. What are the base cases?
`full[0] = 1` (the empty board has one tiling: no tiles), `part[0] = 0` (one lone cell cannot be
tiled). Indices below 0 count as 0.

### 6. What is the order of computation?
`full[i]` needs `i-1` and `i-2`; `part[i]` needs `i-1`. Loop `i = 1..n` upward.

### 7. Can the state space be optimized?
Only the last two `full` values and the last two `part` values are read: a few variables.

## Recursive solution

```java
static long full(int i) {
    if (i < 0) return 0;
    if (i == 0) return 1;
    return full(i - 1) + full(i - 2) + 2 * part(i - 2);
}
static long part(int i) {
    if (i <= 0) return 0;
    return part(i - 1) + full(i - 1);
}
```

## Memoization

Two caches, one per state. See [`solution/Memoization.java`](solution/Memoization.java).

## Tabulation

Arrays `full[]` and `part[]`. See [`solution/Tabulation.java`](solution/Tabulation.java).

## Space optimization

Rolling variables for the two states; the file also checks the closed form
`2·full[n-1] + full[n-3]` against it. See [`solution/Optimized.java`](solution/Optimized.java).

## Dry run

| i | full[i-1] | full[i-2] | 2·part[i-2] | **full[i]** | **part[i]** = part[i-1] + full[i-1] |
|---|---|---|---|---|---|
| 0 | | | | 1 | 0 |
| 1 | 1 | 0 | 0 | 1 | 0 + 1 = 1 |
| 2 | 1 | 1 | 0 | 2 | 1 + 1 = 2 |
| 3 | 2 | 1 | 2·1 = 2 | 5 | 2 + 2 = 4 |
| 4 | 5 | 2 | 2·2 = 4 | 11 | 4 + 5 = 9 |
| 5 | 11 | 5 | 2·4 = 8 | **24** | 9 + 11 = 20 |

Check with the closed form: `full[5] = 2·11 + 2 = 24` ✓.

## Complexity

| Approach | Time | Space | Reason |
|---|---|---|---|
| Brute force | exponential | O(n) | every tiling enumerated |
| Memoization | O(n) | O(n) | 2n states, O(1) each |
| Tabulation | O(n) | O(n) | one loop, two arrays |
| Optimized | O(n) | O(1) | rolling variables |

## Why the optimized approach is better

Same linear time, constant memory, no recursion depth (n = 1000).

## Common mistakes

- **Forgetting the factor 2** for the two tromino orientations that close a jagged edge.
- **Thinking only about dominoes** and getting the Fibonacci numbers.
- **Not taking mod after the multiplication by 2.**

## Key takeaway

When a partial solution can end in several **shapes**, give each shape its own state. Then list
"which last tile turns shape X into shape Y". That list *is* the recurrence.

## Related problems

- [70. Climbing Stairs](https://leetcode.com/problems/climbing-stairs/), the dominoes-only tiling
- [1411. Number of Ways to Paint N × 3 Grid](../1411-number-of-ways-to-paint-n-3-grid/), another "classify the boundary into a few types" DP
- Previous: [935. Knight Dialer](../935-knight-dialer/) · Next: [1220. Count Vowels Permutation](../1220-count-vowels-permutation/)
