# 1277. Count Square Submatrices with All Ones

> **Pattern:** Grid DP, "largest shape ending here" reused for counting · **Builds on:** [Unique Paths](../../../02-two-dimensional-and-grid-dp/02-unique-paths/) · **Difficulty:** Medium

## Problem overview

Given an `m × n` grid of 0s and 1s, count **all** square submatrices (of every size) that contain
only 1s.

Solve [221. Maximal Square](../221-maximal-square/) first: this problem uses the identical table
and changes only what we do with it.

## Link & metadata

| Field | Value |
|---|---|
| LeetCode | [1277. Count Square Submatrices with All Ones](https://leetcode.com/problems/count-square-submatrices-with-all-ones/) |
| Difficulty | Medium |
| Pattern | 2D grid DP (top, left, top-left dependencies) + summing the table |
| Prerequisite topic | [`02-two-dimensional-and-grid-dp/02-unique-paths`](../../../02-two-dimensional-and-grid-dp/02-unique-paths/) |
| Why this pattern | Each cell's value comes from three already-filled neighbours, filled row by row |

## Examples

```
0 1 1 1
1 1 1 1     → 15   (10 squares of side 1, 4 of side 2, 1 of side 3)
0 1 1 1
```

| matrix | Answer |
|---|---|
| grid above | 15 |
| `[[1,0,1],[1,1,0],[1,1,0]]` | 7 (6 of side 1, 1 of side 2) |
| `[[1,1],[1,1]]` | 5 |
| `[[0]]` | 0 |

## Brute-force intuition

Enumerate every top-left corner and every size, check every cell: O(m·n·k³) for `k = min(m, n)`.
The insight that makes it DP:

> If the largest all-1 square with bottom-right corner `(i, j)` has side `s`, then exactly `s`
> squares have their bottom-right corner at `(i, j)`: sides `1, 2, …, s`.

```
s = 3 at X:   . . .      side 1:  X alone
              . . .      side 2:  the 2×2 ending at X
              . . X      side 3:  the 3×3 ending at X      → 3 squares end at X
```

So the count is just the **sum** of the Maximal Square table.

## Deriving the DP (7 questions)

### 1. What is changing?
The bottom-right corner `(i, j)`.

### 2. What does the state represent?
`dp[i][j]` = the side of the largest all-1 square ending at `(i, j)` = the number of all-1
squares whose bottom-right corner is `(i, j)`.

### 3. What are the choices?
None to decide: a 0 cell ends no squares; a 1 cell extends as far as its neighbours allow.

### 4. What is the recurrence?
```
dp[i][j] = 0                                                  if matrix[i][j] == 0
dp[i][j] = 1 + min(dp[i-1][j], dp[i][j-1], dp[i-1][j-1])      if matrix[i][j] == 1
answer   = sum of all dp[i][j]
```

### 5. What are the base cases?
Row 0 and column 0: `dp = matrix[i][j]` (at most a 1×1 square). Out-of-grid neighbours count as 0.

### 6. What is the order of computation?
Row by row, left to right.

### 7. Can the state space be optimized?
Yes. Because a cell's own input value is no longer needed after we compute its `dp`, we can even
write `dp` **into the input grid** for O(1) extra space. We keep a separate 1D row in
`Optimized.java` so the input stays untouched, which is good practice.

## Recursive solution

```java
static int squaresEndingAt(int i, int j) {
    if (i < 0 || j < 0 || matrix[i][j] == 0) return 0;
    return 1 + Math.min(squaresEndingAt(i - 1, j),
               Math.min(squaresEndingAt(i, j - 1), squaresEndingAt(i - 1, j - 1)));
}
```

## Memoization

See [`solution/Memoization.java`](solution/Memoization.java).

## Tabulation

See [`solution/Tabulation.java`](solution/Tabulation.java); a zero-padded `(m+1) × (n+1)` table.

## Space optimization

One row plus a saved diagonal, as in 221. See [`solution/Optimized.java`](solution/Optimized.java).

## Dry run

`[[0,1,1,1],[1,1,1,1],[0,1,1,1]]`:

```
matrix        dp           row sums
0 1 1 1       0 1 1 1      3
1 1 1 1       1 1 2 2      6
0 1 1 1       0 1 2 3      6
                           ---
                           15
```

| cell | up | left | diag | dp |
|---|---|---|---|---|
| (1,2) | 1 | 1 | 1 | 2 |
| (1,3) | 1 | 2 | 1 | 2 |
| (2,2) | 2 | 1 | 1 | 2 |
| (2,3) | 2 | 2 | 2 | **3** |

Cell (2,3) ends three squares: the 1×1, the 2×2 and the 3×3.

## Complexity

| Approach | Time | Space | Reason |
|---|---|---|---|
| Brute force | O(m·n·k³) | O(1) | corner × size × cells checked |
| Memoization | O(m·n) | O(m·n) | each cell once |
| Tabulation | O(m·n) | O(m·n) | one pass |
| Optimized | O(m·n) | O(n) | one row |

## Why the optimized approach is better

Counting squares of every size looks like it needs a loop over sizes, but the DP value already
*is* that count. One pass, one row of memory.

## Common mistakes

- **Counting only `dp[i][j] == 1` cells, or only the largest square.** Add the full value.
- **Adding `dp` values of 0 cells as 1.** A 0 ends no squares.
- **Overflow worry.** At most 300·300·300 ≈ 2.7·10⁷, which fits in `int`.

## Key takeaway

A DP table often answers more than one question. "Largest square ending here" gives both the
maximum (221) and, summed, the total count (1277).

## Related problems

- [221. Maximal Square](../221-maximal-square/), same table, maximum instead of sum
- [62. Unique Paths](https://leetcode.com/problems/unique-paths/), the counting grid DP from the course
- Previous: [221. Maximal Square](../221-maximal-square/) · Next: [576. Out of Boundary Paths](../576-out-of-boundary-paths/)
