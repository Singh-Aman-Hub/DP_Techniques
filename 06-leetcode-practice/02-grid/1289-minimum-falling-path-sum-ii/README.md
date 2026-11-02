# 1289. Minimum Falling Path Sum II

> **Pattern:** Row-by-row grid DP + "best and second-best of the previous row" optimization · **Builds on:** [Maximum Falling Path Sum](../../../02-two-dimensional-and-grid-dp/06-maximum-falling-path-sum/) · **Difficulty:** Hard

## Problem overview

Given an `n × n` integer grid, pick exactly one cell from each row, top to bottom, such that two
cells picked from **adjacent rows are never in the same column**. Return the smallest possible sum.
(This is the "non-zero shifts" version: unlike 931, the next cell may be any column except the same one.)

## Link & metadata

| Field | Value |
|---|---|
| LeetCode | [1289. Minimum Falling Path Sum II](https://leetcode.com/problems/minimum-falling-path-sum-ii/) |
| Difficulty | Hard |
| Pattern | Row DP: `dp[r][c]` from all of row `r-1` except column `c`; then reduce O(n) transitions to O(1) |
| Prerequisite topic | [`02-two-dimensional-and-grid-dp/06-maximum-falling-path-sum`](../../../02-two-dimensional-and-grid-dp/06-maximum-falling-path-sum/) |
| Why this pattern | Same "falling path, one cell per row" structure; only the set of allowed previous columns changes |

## Examples

| grid | Answer | Why |
|---|---|---|
| `[[1,2,3],[4,5,6],[7,8,9]]` | 13 | 1 → 5 → 7 (columns 0, 1, 0) |
| `[[7]]` | 7 | one row, one cell |
| `[[1,2],[3,4]]` | 5 | 1 → 4 or 2 → 3 |

## Brute-force intuition

Each row offers `n - 1` columns given the previous one: `n · (n-1)^(n-1)` paths. For `n = 200`,
impossible. But the best way to **end** at `(r, c)` depends only on `(r, c)`, not on the earlier
choices. That gives `n²` states, each looking at `n - 1` previous cells: O(n³), which is 8·10⁶
for `n = 200`. That passes, but the Hard part is seeing the O(n²) refinement.

## Deriving the DP (7 questions)

### 1. What is changing?
The row `r` we are in and the column `c` we picked in it.

### 2. What does the state represent?
`dp[r][c]` = the minimum sum of a valid falling path from row 0 that **ends** at cell `(r, c)`.

### 3. What are the choices?
Which column `c'` was picked in row `r - 1`: any column except `c`.

### 4. What is the recurrence?
```
dp[r][c] = grid[r][c] + min over c' ≠ c of dp[r-1][c']
answer   = min over c of dp[n-1][c]
```

### 5. What are the base cases?
`dp[0][c] = grid[0][c]`: a path that has only picked one cell.

### 6. What is the order of computation?
Row `r` needs all of row `r - 1`: top to bottom; within a row any order.

### 7. Can the state space be optimized?
Yes, in **time and space**. The inner `min over c' ≠ c` of a whole row has only two possible
answers:

```
previous row:   [ 6   6   7 ]
smallest  = 6 at column 0
second    = 6
for c ≠ 0:  min over c' ≠ c  = smallest (6)    ← the overall min is allowed
for c = 0:  min over c' ≠ 0  = second   (6)    ← the overall min is forbidden, take the next
```

So keep three numbers per row: `smallest`, `smallestColumn`, `secondSmallest`. Each cell then
costs O(1): O(n²) time and O(1) extra space. Watch out: if the smallest value appears twice,
`second` equals `smallest`, which is still correct.

## Recursive solution

```java
static int endAt(int r, int c) {
    if (r == 0) return grid[0][c];
    int best = Integer.MAX_VALUE;
    for (int prev = 0; prev < n; prev++)
        if (prev != c) best = Math.min(best, endAt(r - 1, prev));
    return grid[r][c] + best;
}
```

A brute-force file is included for cross-checking on small grids:
[`solution/BruteForce.java`](solution/BruteForce.java).

## Memoization

Cache `endAt(r, c)`. See [`solution/Memoization.java`](solution/Memoization.java). O(n³).

## Tabulation

Fill row by row with the O(n) inner loop. See [`solution/Tabulation.java`](solution/Tabulation.java).

## Space optimization

Keep only `(smallest, smallestColumn, secondSmallest)` of the previous row. See
[`solution/Optimized.java`](solution/Optimized.java).

## Dry run

`grid = [[1,2,3],[4,5,6],[7,8,9]]` with the optimized version:

| row | per-column value = grid + (second if c == minCol else smallest) | smallest (col) | second |
|---|---|---|---|
| 0 | 1, 2, 3 | 1 (col 0) | 2 |
| 1 | 4+**2**=6, 5+1=6, 6+1=7 | 6 (col 0) | 6 |
| 2 | 7+**6**=13, 8+6=14, 9+6=15 | **13** (col 0) | 14 |

In row 1, column 0 cannot use the smallest (1, also column 0), so it uses the second (2).

## Complexity

| Approach | Time | Space | Reason |
|---|---|---|---|
| Brute force | O(n · (n-1)^(n-1)) | O(n) | every valid path |
| Memoization | O(n³) | O(n²) | n² states × n transitions |
| Tabulation | O(n³) | O(n²) | same loops |
| Optimized | O(n²) | O(1) | every cell O(1) using the two minima |

## Why the optimized approach is better

It removes a whole factor of `n`: 40 000 operations instead of 8 000 000 for `n = 200`. The trick
("exclude one item from a min ⇒ remember the top two") appears in many other DPs, for example
painting houses where adjacent colours must differ.

## Common mistakes

- **Using `second` only when values differ.** Compare **columns**, not values.
- **`n = 1`.** There is no other column, but there is also no next row; the answer is `grid[0][0]`.
- **Forgetting to update the minima from the new row before moving on** (computing them from a
  half-updated row).

## Key takeaway

When a transition is "min over all previous states except one", precompute the best and
second-best once per row. O(n) per state becomes O(1).

## Related problems

- [931. Minimum Falling Path Sum](https://leetcode.com/problems/minimum-falling-path-sum/), the adjacent-column version from the course
- [221. Maximal Square](../221-maximal-square/), the first problem in this folder
- Previous: [688. Knight Probability in Chessboard](../688-knight-probability-in-chessboard/) · Next pattern: [1155. Number of Dice Rolls With Target Sum](../../03-knapsack/1155-number-of-dice-rolls-with-target-sum/)
