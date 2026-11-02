# 221. Maximal Square

> **Pattern:** Grid DP where each cell summarizes "the best shape ending here" · **Builds on:** [Unique Paths](../../../02-two-dimensional-and-grid-dp/02-unique-paths/) · **Difficulty:** Medium

## Problem overview

You get a rectangular grid of `'0'` and `'1'` characters. Find the largest square made only of
`'1'`s and return its **area**.

## Link & metadata

| Field | Value |
|---|---|
| LeetCode | [221. Maximal Square](https://leetcode.com/problems/maximal-square/) |
| Difficulty | Medium |
| Pattern | 2D grid DP; each cell depends on its top, left and top-left neighbours |
| Prerequisite topic | [`02-two-dimensional-and-grid-dp/02-unique-paths`](../../../02-two-dimensional-and-grid-dp/02-unique-paths/) (and [`01-2d-dp-state-representation`](../../../02-two-dimensional-and-grid-dp/01-2d-dp-state-representation/)) |
| Why this pattern | Like Unique Paths, `dp[i][j]` is built from cells above and to the left, filled row by row |

## Examples

```
1 0 1 0 0
1 0 1 1 1        → 4   (the 2×2 block of 1s in rows 1–2, columns 3–4)
1 1 1 1 1
1 0 0 1 0
```

| matrix | Answer |
|---|---|
| grid above | 4 |
| `[[0,1],[1,0]]` | 1 |
| `[[0]]` | 0 |
| 3×3 of all `1` | 9 |

## Brute-force intuition

For every cell as a top-left corner, try growing the square by one row and column while every
new cell is `'1'`. That is O(m·n·min(m,n)²): slow, and it re-checks the same cells many times.

The DP idea is to anchor each square at its **bottom-right** corner and ask how big it can be.

```
 a a .          A square of side k ending at X exists only if
 a a b          squares of side k-1 end at the cell above (a/b region),
 . c X          the cell to the left (c region) and the top-left diagonal.
```

If the three neighbours support squares of sides `up`, `left`, `diag`, then X can support side
`1 + min(up, left, diag)`: the smallest neighbour is the bottleneck.

## Deriving the DP (7 questions)

### 1. What is changing?
The cell `(i, j)` we treat as the bottom-right corner.

### 2. What does the state represent?
`dp[i][j]` = the side length of the largest all-`1` square whose bottom-right corner is `(i, j)`.

### 3. What are the choices?
No real decision: if the cell is `'0'`, no square ends there. If it is `'1'`, the square
extends as far as all three neighbours allow.

### 4. What is the recurrence?
```
dp[i][j] = 0                                                  if matrix[i][j] == '0'
dp[i][j] = 1 + min(dp[i-1][j], dp[i][j-1], dp[i-1][j-1])      if matrix[i][j] == '1'
answer   = (max over all dp[i][j])²
```

### 5. What are the base cases?
Cells in row 0 or column 0 can be at most a 1×1 square: `dp = 1` if the cell is `'1'`. Padding
the table with an extra zero row and column makes this automatic.

### 6. What is the order of computation?
Each cell needs up, left and up-left, so go row by row, left to right.

### 7. Can the state space be optimized?
Only the previous row and the current row are read: one 1D array of length `n + 1`, plus a
variable to remember the old diagonal value before it is overwritten.

## Recursive solution

```java
static int side(int i, int j) {                       // largest square ending at (i, j)
    if (i < 0 || j < 0 || matrix[i][j] == '0') return 0;
    return 1 + Math.min(side(i - 1, j), Math.min(side(i, j - 1), side(i - 1, j - 1)));
}
```

Each call makes three calls that overlap heavily (the same cell is reached via many paths).

## Memoization

Cache `side(i, j)`. See [`solution/Memoization.java`](solution/Memoization.java).

## Tabulation

See [`solution/Tabulation.java`](solution/Tabulation.java). The table is `(m+1) × (n+1)`, so
`dp[i+1][j+1]` stores the value for cell `(i, j)` and the padding row/column are zeros.

## Space optimization

```
row:     dp[0] dp[1] ... dp[j]   dp[j+1]
          (left, already new)   (up, still old)
diagonal = the OLD dp[j] value, saved in `prevDiagonal` before we overwrote it
```

See [`solution/Optimized.java`](solution/Optimized.java).

## Dry run

Grid from the example. `dp` per cell:

```
matrix            dp
1 0 1 0 0         1 0 1 0 0
1 0 1 1 1         1 0 1 1 1
1 1 1 1 1         1 1 1 2 2
1 0 0 1 0         1 0 0 1 0
```

| cell | up | left | diag | dp |
|---|---|---|---|---|
| (1,3) | 0 | 1 | 1 | 1 + 0 = 1 |
| (2,3) | 1 | 1 | 1 | 1 + 1 = **2** |
| (2,4) | 1 | 2 | 1 | 1 + 1 = **2** |
| (3,3) | 2 | 0 | 1 | 1 + 0 = 1 |

Largest side = 2, area = **4**.

## Complexity

| Approach | Time | Space | Reason |
|---|---|---|---|
| Brute force | O(m·n·min(m,n)²) | O(1) | each corner grows up to min(m,n), checking a new row/column each time |
| Memoization | O(m·n) | O(m·n) | each cell once, O(1) work; memo + stack |
| Tabulation | O(m·n) | O(m·n) | one pass over the grid |
| Optimized | O(m·n) | O(n) | one row of length n+1 |

## Why the optimized approach is better

Same single pass but the memory is one row. For a 300×300 grid that is 301 ints instead of 90 601.

## Common mistakes

- **Returning the side instead of the area.**
- **Using `max` of neighbours.** The smallest neighbour limits the square.
- **Losing the diagonal in the 1D version**: save `dp[j+1]` into a temp before writing it.
- **Comparing `matrix[i][j] == 1`** when the input holds the characters `'1'`.

## Key takeaway

For "largest shape of 1s", anchor the shape at a corner and let `dp` store its size there. The
neighbours that would also have to hold a smaller copy of the shape form the recurrence.

## Related problems

- [1277. Count Square Submatrices with All Ones](../1277-count-square-submatrices-with-all-ones/), same table, sum instead of max
- [62. Unique Paths](https://leetcode.com/problems/unique-paths/), the first "up and left" grid DP
- Previous pattern: [918. Maximum Sum Circular Subarray](../../01-one-dimensional/918-maximum-sum-circular-subarray/) · Next: [1277. Count Square Submatrices with All Ones](../1277-count-square-submatrices-with-all-ones/)
