# Maximum Falling Path Sum — Walkthrough

## 1. Intuition (no code, no math — an everyday analogy)

Picture a pachinko board or a ski slope divided into rows of gates. You can drop in through **any** gate at the top. Each time you pass a row you may drift one gate to the left, stay in line, or drift one gate to the right. Every gate you pass hands you some points (or takes some away, if the number is negative). Where should you start, and how should you drift, to collect the most points by the time you reach the bottom?

Think about a single gate in the middle of the board. Whoever passes through it came from one of the three gates diagonally above it or straight above it. If each of those three gates already knows "the best score anyone can have when arriving here", then this gate can work out its own best arrival score at once: take the best of the three, add my points. Do that row by row from the top, and the bottom row ends up knowing the best possible arrival score at each of its gates. The answer is the biggest of those.

## 2. Problem statement in our own words

We are given an `n × n` grid of integers. A falling path picks one cell in every row, from row `0` to row `n-1`. If it is in column `c` in one row, then in the next row it must be in column `c - 1`, `c` or `c + 1`, and it may never leave the grid. The path may start in any column of row 0. Its sum is the sum of the cells it visits.

- **Maximum version (taught first):** return the largest sum of any falling path.
- **Minimum version — the judge problem:** [LeetCode 931 — Minimum Falling Path Sum](https://leetcode.com/problems/minimum-falling-path-sum/) asks for the **smallest** sum of any falling path. The algorithm is identical with `max` replaced by `min` (Section 9b).

## 3. Examples (input → output, with an explanation)

We use **one example throughout this lesson**:

```
          col 0  col 1  col 2
row 0  [    2      1      3  ]
row 1  [    6      5      4  ]
row 2  [    7      8      9  ]
```

**Maximum falling path sum = 17**, along `3 → 5 → 9`:

```
row 0    2    1   [3]
                  /
row 1    6   [5]   4
               \
row 2    7    8   [9]
```

`3` is at column 2, `5` is at column 1 (a down-left move), `9` is at column 2 (a down-right move). All moves shift by at most one column.

Notice what a tempting wrong idea would do. "Take the largest number in each row" gives `3 + 6 + 9 = 18`, but `3` (column 2) to `6` (column 0) jumps two columns, which is not allowed. The movement rule is what makes the problem interesting.

**Minimum falling path sum on the same grid = 13** (two paths tie: `1 → 5 → 7` and `1 → 4 → 8`).

More examples (maximum version):

| Grid | Max | Min | Why |
|---|---|---|---|
| `[[5]]` | 5 | 5 | One cell: the only path. |
| `[[1,2,3],[4,5,6],[7,8,9]]` | 18 | 12 | Max `3 → 6 → 9`; min `1 → 4 → 7`. |
| `[[-19,57],[-40,-5]]` | 52 | -59 | Max `57 → -5`; min `-19 → -40`. |

**Why greedy fails.** "Start at the largest top cell, then always move to the largest reachable cell below" looks reasonable. On

```
[ 5   1   0 ]
[ 2   1   0 ]
[ 1   1 100 ]
```

greedy picks `5`, then `2` (the larger of `2` and `1`), then `1` (only columns 0 and 1 are reachable), total `8`. The best path is `5 → 1 → 100 = 106`: a slightly worse middle step leads to a much better final step. Only by considering all paths (which DP does efficiently) do we find it.

## 4. Deriving the solution — the 7 questions

### 4.1 What is changing?

As the path falls, two things change: the **row** (it increases by exactly 1 each step) and the **column** (it changes by -1, 0 or +1). Two changing quantities → a 2D state `(row, col)`.

### 4.2 What does the state represent?

> `dp[r][c]` is the largest sum of a falling path that starts anywhere in row 0 and **ends at** cell `(r, c)`.

Why "ends at" and not "starts at"? Both work, because the path is free at both ends. With "ends at", the free start becomes a simple base row and the free end becomes a max over the last row. (You will build the "starts at" version in Exercise 4.)

### 4.3 What are the choices?

To arrive at `(r, c)` with `r > 0`, the previous cell was one of:

1. `(r-1, c-1)` — we moved down-right into `(r, c)` (only if `c - 1 ≥ 0`),
2. `(r-1, c)` — we moved straight down (always exists),
3. `(r-1, c+1)` — we moved down-left (only if `c + 1 ≤ n - 1`).

```
     (r-1,c-1)  (r-1,c)  (r-1,c+1)
            \      |      /
             \     |     /
              v    v    v
                 (r, c)
```

### 4.4 What is the recurrence?

```
dp[r][c] = grid[r][c] + max( dp[r-1][c-1],  dp[r-1][c],  dp[r-1][c+1] )
                             \_ if c > 0 _/              \_ if c < n-1 _/
```

We add `grid[r][c]` because every path ending here collects this cell, and we take `max` over the parents because we want the best way to arrive. Parents outside the grid are simply skipped. (Equivalently, treat them as `−∞`, which can never win a `max` — but be careful never to *add* to `Integer.MIN_VALUE` in Java, or it overflows to a huge positive number.)

### 4.5 What are the base cases?

The path may start at any top cell, so a path ending in row 0 is just that one cell:

```
dp[0][c] = grid[0][c]     for every c
```

And because the path may end at any bottom cell:

```
answer = max over c of dp[n-1][c]
```

### 4.6 What is the order of computation?

`dp[r][...]` reads only row `r - 1`. So fill row 0, then row 1, …, then row `n-1`. Inside a row the cells are independent, so the column order does not matter.

### 4.7 Can the state space be optimized?

Only the previous row is ever read, so two arrays of length `n` are enough (`prev` and `cur`). Unlike [Triangle](../05-triangle/), a single in-place array does not work: `cur[c]` needs `prev[c-1]`, `prev[c]` and `prev[c+1]`, and whichever direction we sweep, one of the diagonal neighbors would already be overwritten. Section 8 shows this.

## 5. Brute force: recursion

`f(r, c)` = best sum of a falling path ending at `(r, c)`. Try every parent, recursively.

```java
static int bestEndingAt(int[][] matrix, int row, int col) {
    if (row == 0) return matrix[0][col];                 // base: path is one cell
    int n = matrix.length;
    int bestParent = Integer.MIN_VALUE;
    for (int parentCol = col - 1; parentCol <= col + 1; parentCol++) {
        if (parentCol >= 0 && parentCol < n) {           // skip parents outside the grid
            bestParent = Math.max(bestParent, bestEndingAt(matrix, row - 1, parentCol));
        }
    }
    return matrix[row][col] + bestParent;                 // bestParent is always a real value
}

static int maxFallingPathSum(int[][] matrix) {
    int best = Integer.MIN_VALUE;
    for (int col = 0; col < matrix.length; col++)        // free end: try every last-row cell
        best = Math.max(best, bestEndingAt(matrix, matrix.length - 1, col));
    return best;
}
```

Full file: [solution/BruteForce.java](solution/BruteForce.java).

Note that `bestParent` always ends up as a real value, because the straight-up parent `(row-1, col)` always exists. That is why adding `matrix[row][col]` to it is safe.

### Recursion tree on our example

The top-level loop calls `f(2,0)`, `f(2,1)` and `f(2,2)`:

```
f(2,0)                     f(2,1)                          f(2,2)
 ├─ f(1,0)                  ├─ f(1,0)                       ├─ f(1,1)
 │   ├─ f(0,0)=2            │   ├─ f(0,0)=2                 │   ├─ f(0,0)=2
 │   └─ f(0,1)=1            │   └─ f(0,1)=1                 │   ├─ f(0,1)=1
 └─ f(1,1)                  ├─ f(1,1)                       │   └─ f(0,2)=3
     ├─ f(0,0)=2            │   ├─ f(0,0)=2                 └─ f(1,2)
     ├─ f(0,1)=1            │   ├─ f(0,1)=1                     ├─ f(0,1)=1
     └─ f(0,2)=3            │   └─ f(0,2)=3                     └─ f(0,2)=3
                            └─ f(1,2)
                                ├─ f(0,1)=1
                                └─ f(0,2)=3
```

`f(1,1)` is computed **three** times, `f(1,0)` and `f(1,2)` twice each.

### Why it is slow

Each call branches into up to 3 calls, for `n - 1` levels, and we start `n` such trees. That is up to `n · 3^(n-1)` calls, written `O(n · 3^n)`. For `n = 100` (LeetCode's limit) this is astronomically large, yet there are only `100 × 100 = 10,000` distinct cells.

## 6. Memoization (top-down)

Cache `f(r, c)` in `memo[r][c]`. Sums can be zero or negative, so we use `Integer[][]` with `null` meaning "not computed yet".

```java
private static Integer[][] memo;   // memo[row][col] = best falling-path sum ending at (row, col)

static int bestEndingAt(int[][] matrix, int row, int col) {
    if (row == 0) return matrix[0][col];
    if (memo[row][col] != null) return memo[row][col];       // cache hit
    ... same loop over the three parents ...
    memo[row][col] = matrix[row][col] + bestParent;
    return memo[row][col];
}
```

Full file: [solution/Memoization.java](solution/Memoization.java).

### Dry run with cache hits

```
answer loop, col 0:
  f(2,0)
    f(1,0) = 6 + max(f(0,0)=2, f(0,1)=1)          = 8    -> memo[1][0] = 8
    f(1,1) = 5 + max(2, 1, f(0,2)=3)               = 8    -> memo[1][1] = 8
  f(2,0) = 7 + max(8, 8)                           = 15   -> memo[2][0] = 15
answer loop, col 1:
  f(2,1)
    f(1,0)  ** CACHE HIT ** 8
    f(1,1)  ** CACHE HIT ** 8
    f(1,2) = 4 + max(f(0,1)=1, f(0,2)=3)           = 7    -> memo[1][2] = 7
  f(2,1) = 8 + max(8, 8, 7)                        = 16   -> memo[2][1] = 16
answer loop, col 2:
  f(2,2)
    f(1,1)  ** CACHE HIT ** 8
    f(1,2)  ** CACHE HIT ** 7
  f(2,2) = 9 + max(8, 7)                           = 17   -> memo[2][2] = 17
answer = max(15, 16, 17) = 17
```

Four cache hits on a 3×3 grid. On a 100×100 grid nearly every call is a hit.

## 7. Tabulation (bottom-up)

Fill row 0 from the grid, then each following row from the one above, then scan the last row.

```java
int[][] dp = new int[n][n];      // dp[row][col] = best falling-path sum ending at (row, col)
for (int col = 0; col < n; col++) dp[0][col] = matrix[0][col];

for (int row = 1; row < n; row++) {
    for (int col = 0; col < n; col++) {
        int bestParent = dp[row - 1][col];                                    // straight above
        if (col > 0)     bestParent = Math.max(bestParent, dp[row - 1][col - 1]);  // above-left
        if (col < n - 1) bestParent = Math.max(bestParent, dp[row - 1][col + 1]);  // above-right
        dp[row][col] = matrix[row][col] + bestParent;
    }
}
// answer = max of dp[n-1][*]
```

Full file: [solution/Tabulation.java](solution/Tabulation.java).

Starting `bestParent` from the straight-above parent (which always exists) avoids any sentinel at all.

### Table filled step by step

**Step 0 — base row (copy row 0 of the grid):**

```
          col 0  col 1  col 2
row 0  [    2      1      3  ]
row 1  [    ?      ?      ?  ]
row 2  [    ?      ?      ?  ]
```

**Step 1 — row 1:**

```
dp[1][0] = 6 + max(      dp[0][0]=2, dp[0][1]=1) = 6 + 2 = 8     (no above-left)
dp[1][1] = 5 + max(dp[0][0]=2, dp[0][1]=1, dp[0][2]=3) = 5 + 3 = 8
dp[1][2] = 4 + max(dp[0][1]=1, dp[0][2]=3      ) = 4 + 3 = 7     (no above-right)

row 0  [    2      1      3  ]
row 1  [    8      8      7  ]
row 2  [    ?      ?      ?  ]
```

**Step 2 — row 2:**

```
dp[2][0] = 7 + max(      8, 8) = 15
dp[2][1] = 8 + max(8,    8, 7) = 16
dp[2][2] = 9 + max(8,    7   ) = 17

row 0  [    2      1      3  ]
row 1  [    8      8      7  ]
row 2  [   15     16     17  ]   -> answer = max(15, 16, 17) = 17
```

Every value matches the memo values from Section 6.

## 8. Space optimization

Row `r` reads only row `r - 1`, so we keep two arrays:

```java
int[] prev = matrix[0] values;               // prev[col] = best sum ending at (row-1, col)
for (int row = 1; row < n; row++) {
    int[] cur = new int[n];                  // cur[col]  = best sum ending at (row, col)
    for (int col = 0; col < n; col++) {
        int bestParent = prev[col];
        if (col > 0)     bestParent = Math.max(bestParent, prev[col - 1]);
        if (col < n - 1) bestParent = Math.max(bestParent, prev[col + 1]);
        cur[col] = matrix[row][col] + bestParent;
    }
    prev = cur;
}
// answer = max of prev[*]
```

Full file: [solution/Optimized.java](solution/Optimized.java).

### Why one in-place array is not enough here

Suppose we tried to overwrite `prev` in place, left to right, on row 1 of our example. `prev` starts as `[2, 1, 3]`:

```
col 0: 6 + max(2, 1)    = 8   -> [8, 1, 3]
col 1: 5 + max(8, 1, 3) = 13  -> WRONG: read prev[0] = 8, which is row 1's new value,
                                 not row 0's old value 2. The correct dp[1][1] is 8.
```

Right to left fails the same way, mirrored (`prev[col + 1]` is already new). In [Triangle](../05-triangle/) each cell read only `col` and `col + 1`, so a left-to-right sweep was safe; here each cell reads **both** diagonals, so we need a second array. (An advanced trick keeps one array plus a single saved variable holding the old `prev[col - 1]`, but two arrays are clearer and still `O(n)`.)

## 9. Dry run on a full example (table of state values per step)

Using the two-row version on our example:

| Step | Row | `prev` before | Computations | `cur` after |
|---|---|---|---|---|
| 0 | base | – | copy row 0 | `[2, 1, 3]` |
| 1 | 1 | `[2, 1, 3]` | `6+max(2,1)=8`, `5+max(2,1,3)=8`, `4+max(1,3)=7` | `[8, 8, 7]` |
| 2 | 2 | `[8, 8, 7]` | `7+max(8,8)=15`, `8+max(8,8,7)=16`, `9+max(8,7)=17` | `[15, 16, 17]` |
| end | – | – | `max(15, 16, 17)` | **17** |

**Recovering the path** (needs the full table): end at the largest last-row cell, `(2,2) = 17`. Its parents are `(1,1) = 8` and `(1,2) = 7`; `17 − 9 = 8` came from `(1,1)`. Its parents `(0,0)=2, (0,1)=1, (0,2)=3`; `8 − 5 = 3` came from `(0,2)`. Path: `3 → 5 → 9`.

### 9b. The minimum version: LeetCode 931

[LeetCode 931 — Minimum Falling Path Sum](https://leetcode.com/problems/minimum-falling-path-sum/) asks for the **minimum** sum, not the maximum. The whole derivation carries over word for word with "largest" replaced by "smallest":

```
dp[0][c] = grid[0][c]
dp[r][c] = grid[r][c] + min( dp[r-1][c-1], dp[r-1][c], dp[r-1][c+1] )   (valid parents only)
answer   = min over c of dp[n-1][c]
```

On our grid:

```
row 0  [    2      1      3  ]
row 1  [  6+1=7  5+1=6  4+1=5 ]          dp[1][0] = 6 + min(2,1)   = 7
                                         dp[1][1] = 5 + min(2,1,3) = 6
                                         dp[1][2] = 4 + min(1,3)   = 5
row 2  [ 7+6=13 8+5=13 9+5=14 ]          dp[2][0] = 7 + min(7,6)   = 13
                                         dp[2][1] = 8 + min(7,6,5) = 13
                                         dp[2][2] = 9 + min(6,5)   = 14
answer = min(13, 13, 14) = 13
```

Ready-to-submit code: [solution/MinFallingPathSum931.java](solution/MinFallingPathSum931.java) (both the table version and the two-row version).

When switching between max and min, change **three** things together: the combine function (`max` → `min`), the final scan (`max` → `min`), and any sentinel (`MIN_VALUE` → `MAX_VALUE`). Forgetting the sentinel is the classic bug: `min(Integer.MIN_VALUE, x)` always returns the sentinel. The code here avoids sentinels entirely by starting from the straight-up parent.

### 9c. A harder variant: LeetCode 1289

[LeetCode 1289 — Minimum Falling Path Sum II](https://leetcode.com/problems/minimum-falling-path-sum-ii/) changes the movement rule: from column `c` you may go to **any column except `c`** in the next row. The state is the same `dp[r][c]`, but each cell now has `n - 1` parents, so the direct recurrence costs `O(n)` per cell and `O(n³)` overall. The fix: for each row, remember only the **smallest** and **second-smallest** values of the previous row (and the column of the smallest). A cell uses the smallest unless it sits in that same column, in which case it uses the second-smallest. That brings it back to `O(n²)`. Try it after this topic.

## 10. Complexity analysis (explained, for every approach)

| Approach | Time | Space | Explanation |
|---|---|---|---|
| Brute force | `O(n · 3^n)` | `O(n)` | `n` starting calls, each branching up to 3 ways for `n − 1` levels. Stack depth is `n`. |
| Memoization | `O(n²)` | `O(n²)` | `n²` states, each computed once with at most 3 lookups. Memo is `n × n`; stack adds `O(n)`. |
| Tabulation | `O(n²)` | `O(n²)` | Two nested loops over `n²` cells, `O(1)` work each, plus an `O(n)` final scan. |
| Optimized | `O(n²)` | `O(n)` | Same work; only two rows of length `n` are kept. |

`O(n²)` time is optimal: every cell might be on the best path, so every cell must be read.

## 11. Why this approach works (correctness argument in plain words)

Take the best falling path that ends at `(r, c)`, with `r > 0`. Its second-to-last cell is one of the valid parents `(r-1, p)`. The part of the path up to that parent is itself a falling path ending at `(r-1, p)`. If that part were not the best possible path ending at `(r-1, p)`, we could swap in a better one and get a better path ending at `(r, c)` — a contradiction. So the best path ending at `(r, c)` = `grid[r][c]` + the best path ending at its best parent, which is the recurrence. Row 0 is correct by definition (free start). Every falling path ends at some cell of the last row, so the max over the last row is the overall best. The same argument with "smallest" proves the minimum version.

## 12. Common mistakes

1. **Submitting the max version to LeetCode 931.** The judge asks for the minimum.
2. **Changing `max` to `min` but not the sentinel.** `min(Integer.MIN_VALUE, x)` is always the sentinel; with `Integer.MAX_VALUE` in a `max` version the same bug appears mirrored.
3. **Adding to a sentinel.** `matrix[r][c] + Integer.MIN_VALUE` overflows for negative cells and becomes a huge positive number that wins every `max`. Skip invalid parents instead of adding to them.
4. **Returning `dp[n-1][n-1]` or `dp[n-1][0]`.** The path can end in any column; scan the whole last row.
5. **Starting only from column 0.** The start is free; the whole top row is the base.
6. **Overwriting a single array in place.** Both diagonals are read, so a second array is needed (Section 8).
7. **Using greedy.** See the counterexample in Section 3.

## 13. When to use this technique / how to recognize similar problems

Signals:

- One choice **per row (layer)**, and consecutive choices are **constrained by distance** (here at most 1 column).
- The start and/or the end are **free**.
- The goal is a max or min total.

Recipe: `dp[layer][position]` = best total of a valid sequence ending at that position. Base layer = the values themselves (free start). Answer = best over the last layer (free end). The number of parents decides the per-cell cost; if it is large (as in LeetCode 1289), look for a summary of the previous row (best and second-best, prefix maxima) to keep each cell `O(1)`.

## 14. Summary and revision notes

- State: `dp[r][c]` = best falling-path sum ending at `(r, c)`.
- Base: `dp[0][c] = grid[0][c]` (free start). Answer: best of the last row (free end).
- Recurrence: own value + best of up to three parents `(r-1, c-1..c+1)`, skipping out-of-grid parents.
- Max ↔ min: change the combine, the final scan and any sentinel together. **LeetCode 931 is the minimum version.**
- Space: two rows of length `n`; a single in-place row breaks because both diagonals are read.
- Harder variant: LeetCode 1289, solved in `O(n²)` with best/second-best of the previous row.

```
+-----------------------------------------------------------------------------+
|  dp[0][c] = g[0][c]                                                         |
|  dp[r][c] = g[r][c] + max( dp[r-1][c-1], dp[r-1][c], dp[r-1][c+1] )         |
|                              (only parents inside the grid)                 |
|  answer   = max over c of dp[n-1][c]        (min everywhere for LC 931)     |
+-----------------------------------------------------------------------------+
```

## Exercises

**Exercise 1.** Compute both the maximum and the minimum falling path sum of

```
[ 1   -2    3 ]
[-4    5   -6 ]
[ 7   -8    9 ]
```

<details>
<summary>Answer</summary>

Max: row 1 = `[-4+max(1,-2), 5+max(1,-2,3), -6+max(-2,3)] = [-3, 8, -3]`; row 2 = `[7+max(-3,8), -8+max(-3,8,-3), 9+max(8,-3)] = [15, 0, 17]` → **17** (`3 → 5 → 9`).

Min: row 1 = `[-4+min(1,-2), 5+min(1,-2,3), -6+min(-2,3)] = [-6, 3, -8]`; row 2 = `[7+min(-6,3), -8+min(-6,3,-8), 9+min(3,-8)] = [1, -16, 1]` → **-16** (`-2 → -6 → -8`, columns 1 → 2 → 1).
</details>

**Exercise 2.** The rule changes: from column `c` you may move to any column from `c - 2` to `c + 2`. What changes in the recurrence, the complexity and the space optimization?

<details>
<summary>Answer</summary>

Each cell now has up to 5 parents `(r-1, c-2 .. c+2)`, still bounds-checked. Time stays `O(n²)` (5 is a constant), space for the two-row version stays `O(n)`. In general, a window of width `w` gives `O(n² · w)`; for a large `w` you would use a sliding-window maximum (see [Monotonic queue optimization](../../05-dp-optimization/04-monotonic-queue-optimization/)).
</details>

**Exercise 3.** Run the LeetCode 1289 rule ("next column must differ from the current one") as a minimum on `[[1,2,3],[4,5,6],[7,8,9]]`. Use the best/second-best trick.

<details>
<summary>Answer</summary>

Row 0: `[1, 2, 3]`; smallest 1 (column 0), second-smallest 2.
Row 1: column 0 cannot use column 0, so uses 2: `4+2 = 6`; column 1: `5+1 = 6`; column 2: `6+1 = 7` → `[6, 6, 7]`; smallest 6 (say column 0), second-smallest 6.
Row 2: column 0 uses second-smallest 6: `7+6 = 13`; column 1: `8+6 = 14`; column 2: `9+6 = 15`. Answer **13** (for example `1 → 5 → 7`).
</details>

**Exercise 4.** Define the state the other way: `down[r][c]` = best sum of a falling path that **starts at** `(r, c)` and goes to the last row. Write the recurrence, base case, fill order and answer, and fill the table for our example (maximum).

<details>
<summary>Answer</summary>

`down[n-1][c] = g[n-1][c]`; `down[r][c] = g[r][c] + max(down[r+1][c-1], down[r+1][c], down[r+1][c+1])` over valid columns; fill from row `n-1` upward; answer = `max over c of down[0][c]`.

Example: row 2 = `[7, 8, 9]`; row 1 = `[6+8, 5+9, 4+9] = [14, 14, 13]`; row 0 = `[2+14, 1+14, 3+14] = [16, 15, 17]` → **17**, the same answer. Because both ends are free, both directions are equally clean here (unlike Triangle, where only one end was free).
</details>
