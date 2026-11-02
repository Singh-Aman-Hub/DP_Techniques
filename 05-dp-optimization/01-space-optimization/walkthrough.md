# Space Optimization — Walkthrough

This topic teaches **one procedure** that produces every space-saving trick you have seen so far. We use four problems you already solved in earlier modules, because the point here is not a new recurrence but a new way of *looking* at an old one:

| Problem | Earlier topic | Table | After this topic |
|---------|---------------|-------|------------------|
| House Robber (LC 198) | [House Robber](../../01-one-dimensional-dp/06-house-robber/) | `dp[n]` | 2 variables |
| Minimum Path Sum (LC 64) | [Minimum Path Sum](../../02-two-dimensional-and-grid-dp/04-minimum-path-sum/) | `dp[R][C]` | 1 row |
| 0/1 Knapsack | [0/1 Knapsack](../../03-subsequence-and-knapsack-dp/02-zero-one-knapsack/) | `dp[n+1][W+1]` | 1 array, reverse loop |
| LCS (LC 1143) | [LCS](../../03-subsequence-and-knapsack-dp/12-longest-common-subsequence/) | `dp[n+1][m+1]` | 1 row + `prev` |

Minimum Path Sum is the **running example** for sections 4–9; the other three each get their own short case study in section 8.

---

## 1. Intuition

Imagine solving a long arithmetic problem on a tiny whiteboard. You write row 1 of your working. To compute row 2 you only need row 1, so once row 2 is done you erase row 1 and write row 3 in its place. At the end the board shows only the last row — and the last row contains the answer.

You can erase something as soon as **nobody will look at it again**. The whole art of space optimization is answering one question precisely:

> "After I compute this cell, which cells will still be read later?"

Erasing too early gives wrong answers (you read a value that was already replaced). Erasing too late just wastes space. So we need a careful, mechanical way to answer that question. That is the *dependency footprint*.

---

## 2. Problem statement in our own words

The running example is [Minimum Path Sum](https://leetcode.com/problems/minimum-path-sum/) (LeetCode 64): you are given a grid of non-negative numbers. Start at the top-left cell, move only right or down, finish at the bottom-right cell, and add up every number you step on. Return the smallest possible total.

Our task in this topic: solve it with **one row of memory instead of a full grid**, and learn a method that does the same for any DP.

---

## 3. Examples

Running example (the same grid is used in every section):

```
grid = 1 3 1
       1 5 1
       4 2 1
```

Output: `7`, from the path `1 → 3 → 1 → 1 → 1` (right, right, down, down).

Other examples used later:

| Problem | Input | Output | Why |
|---------|-------|--------|-----|
| House Robber | `[2, 7, 9, 3, 1]` | 12 | rob houses 0, 2, 4: 2 + 9 + 1 |
| 0/1 Knapsack | weights `[1, 3, 4]`, values `[15, 20, 30]`, W = 4 | 35 | take items of weight 1 and 3 |
| LCS | `"abcde"`, `"ace"` | 3 | `"ace"` |

---

## 4. Deriving the solution — the 7 questions

### 4.1 What is changing?

The position `(r, c)` in the grid. Every move increases `r` or `c` by one.

### 4.2 What does the state represent?

`dp[r][c]` = the smallest sum of numbers on any right/down path from `(0, 0)` to `(r, c)`, including both ends.

### 4.3 What are the choices?

The last step into `(r, c)` came either **from above** `(r-1, c)` or **from the left** `(r, c-1)`.

### 4.4 What is the recurrence?

```
dp[r][c] = grid[r][c] + min( dp[r-1][c],  dp[r][c-1] )
                             ^ from above  ^ from the left
```

We pay for the current cell, plus the cheaper way of arriving.

### 4.5 What are the base cases?

- `dp[0][0] = grid[0][0]` — the path is just the start cell.
- Row 0: only "from the left" exists, so `dp[0][c] = grid[0][c] + dp[0][c-1]`.
- Column 0: only "from above" exists, so `dp[r][0] = grid[r][0] + dp[r-1][0]`.

### 4.6 What is the order of computation?

Both dependencies point to a smaller `r` or a smaller `c`, so rows top to bottom and, inside a row, columns left to right.

### 4.7 Can the state space be optimized?

Yes — and answering this question *properly* is the whole topic. Sections 4.8 and 4.9 give the tool and the rule; section 8 applies them.

### 4.8 Prerequisite: the dependency footprint

Write the recurrence, then draw, **relative to the current cell X**, every cell it reads. That drawing is the footprint (also called a *stencil*).

```
House Robber            Minimum Path Sum         0/1 Knapsack               LCS / Edit Distance
dp[i] reads i-1, i-2    reads up, left           reads up, up-left by w     reads up, left, up-left

  . . R R X             . . R . .  <- row r-1    . R . . R . <- item i-1    . . R R .  <- row i-1
                        . R X . .  <- row r      . . . . X . <- item i      . . R X .  <- row i
                                                     <-w->
```

(`R` = read by X, `X` = being computed.)

Now read off **how far back** the footprint reaches:

| Footprint reaches back… | Live memory needed |
|-------------------------|--------------------|
| a constant `k` cells in 1D | `k` variables |
| only to the previous row (and the current row) | 2 rows, often 1 row |
| a constant `k` rows | `k + 1` rows (see [Rolling arrays](../02-rolling-arrays/)) |
| to *any* earlier cell (e.g. LIS reads all `j < i`) | no reduction possible |

A small numeric example for House Robber with `[2, 7, 9, 3, 1]`:

```
i         0   1   2   3   4
dp[i]     2   7  11  11  12
read by   1,2 2,3 3,4  4   -     <- cell 2 is read by cells 3 and 4, then never again
```

When we are computing `dp[4]`, cells 0, 1, 2 are already dead. At every moment only 2 cells are live, so 2 variables suffice.

### 4.9 The key property and why it holds: the last-reader rule

> **Last-reader rule.** It is safe to overwrite (or forget) cell `A` as soon as every cell whose footprint contains `A` has been computed.

Why it is true: a DP cell is computed once and then only *read*. If no future computation reads `A`, then the value stored there cannot influence any future cell, so it cannot influence the answer. Erasing it changes nothing.

For **in-place** updates (one array that holds two rows at once) we need a sharper version, because the array slot `row[c]` holds the *old* value `dp[r-1][c]` until we overwrite it with the *new* value `dp[r][c]`:

> **In-place rule.** When the loop reaches position `c`, every slot already visited in this row holds a **new** value; every slot not yet visited holds an **old** value. The recurrence must want exactly that.

For Minimum Path Sum, sweeping `c` left to right:

```
                       visited (new)        not yet visited (old)
row array:        [ dp[r][0] ... dp[r][c-1] | dp[r-1][c] ... dp[r-1][C-1] ]
                                      ^              ^
                       "left" = new value     "above" = old value  -> exactly what we need
```

Both values the recurrence needs are sitting in the array at the moment we need them. This is the property that makes the one-row version correct. Different footprints need different sweep directions or an extra variable — section 8 shows all three situations.

---

## 5. Brute force: recursion

```java
// minimum path sum to reach (r, c)
static int solve(int[][] grid, int r, int c) {
    if (r == 0 && c == 0) return grid[0][0];
    if (r < 0 || c < 0) return Integer.MAX_VALUE / 2;   // outside the grid: impossible
    return grid[r][c] + Math.min(solve(grid, r - 1, c), solve(grid, r, c - 1));
}
```

Recursion tree for the target `(2, 2)` (first levels):

```
                         (2,2)
                 /                  \
             (1,2)                  (2,1)
            /     \                /     \
        (0,2)    (1,1)         (1,1)    (2,0)
        ...      /   \         /   \     ...
              (0,1) (1,0)   (0,1) (1,0)          <- (1,1) solved twice already
```

Every call branches into two, so the number of calls grows like `2^(R+C)`. The same cell, e.g. `(1,1)`, is solved again and again: overlapping subproblems.

---

## 6. Memoization (top-down)

Cache `memo[r][c]`. Each of the `R·C` cells is solved once, so the time drops to `O(R·C)`, but the cache has `R·C` entries **plus** a recursion stack of depth up to `R + C`.

Memoization cannot be space-optimized with the techniques of this topic: we do not control the order in which cells are visited, so we cannot know when a cell becomes dead. Space optimization is a **tabulation** skill. That is why we convert to bottom-up first.

---

## 7. Tabulation (bottom-up)

```java
int[][] dp = new int[rows][cols];
for (int r = 0; r < rows; r++)
    for (int c = 0; c < cols; c++) {
        if (r == 0 && c == 0) dp[r][c] = grid[0][0];
        else if (r == 0) dp[r][c] = grid[r][c] + dp[r][c - 1];
        else if (c == 0) dp[r][c] = grid[r][c] + dp[r - 1][c];
        else dp[r][c] = grid[r][c] + Math.min(dp[r - 1][c], dp[r][c - 1]);
    }
return dp[rows - 1][cols - 1];
```

The filled table for our grid:

```
           c=0  c=1  c=2
   r=0      1    4    5
   r=1      2    7    6
   r=2      6    8    7     <- answer dp[2][2] = 7
```

Look at the footprint again: when we compute row 2, **row 0 is dead**. Nobody in row 2 reads it. Only one previous row is ever live.

---

## 8. Optimization: space optimization by footprint analysis

### 8.1 Case 1 — 1D with constant look-back → O(1) variables (House Robber)

Recurrence: `dp[i] = max(dp[i-1], dp[i-2] + nums[i])`. Footprint `{i-1, i-2}` → two live values. Name them by their role:

```java
int prev2 = 0;   // dp[i-2]
int prev1 = 0;   // dp[i-1]
for (int money : nums) {
    int current = Math.max(prev1, prev2 + money);
    prev2 = prev1;          // shift the window one step to the right
    prev1 = current;
}
return prev1;
```

Trace on `[2, 7, 9, 3, 1]`:

| i | money | prev2 (dp[i-2]) | prev1 (dp[i-1]) | current = max(prev1, prev2 + money) |
|---|-------|-----------------|-----------------|--------------------------------------|
| 0 | 2 | 0 | 0 | max(0, 0+2) = 2 |
| 1 | 7 | 0 | 2 | max(2, 0+7) = 7 |
| 2 | 9 | 2 | 7 | max(7, 2+9) = 11 |
| 3 | 3 | 7 | 11 | max(11, 7+3) = 11 |
| 4 | 1 | 11 | 11 | max(11, 11+1) = **12** |

The order of the two assignments matters: `prev2 = prev1` must happen **before** `prev1 = current`, otherwise both variables end up equal to `current`.

### 8.2 Case 2 — 2D, reads previous row and current row to the left → 1 row (Minimum Path Sum)

From section 4.9 we know a left-to-right sweep leaves "above" as the old value and "left" as the new value. So:

```java
int[] row = new int[cols];
for (int r = 0; r < rows; r++)
    for (int c = 0; c < cols; c++) {
        if (r == 0 && c == 0) row[c] = grid[0][0];
        else if (r == 0) row[c] = grid[r][c] + row[c - 1];
        else if (c == 0) row[c] = grid[r][c] + row[c];                  // row[c] is still "above"
        else row[c] = grid[r][c] + Math.min(row[c] /* above (old) */, row[c - 1] /* left (new) */);
    }
return row[cols - 1];
```

Watch the single array evolve (each line shows the array right after the write):

```
after row 0:         [ 1, 4, 5 ]

row 1, c=0: 1 + above 1             = 2     [ 2, 4, 5 ]
row 1, c=1: 5 + min(above 4, left 2) = 7    [ 2, 7, 5 ]
row 1, c=2: 1 + min(above 5, left 7) = 6    [ 2, 7, 6 ]

row 2, c=0: 4 + above 2             = 6     [ 6, 7, 6 ]
row 2, c=1: 2 + min(above 7, left 6) = 8    [ 6, 8, 6 ]
row 2, c=2: 1 + min(above 6, left 8) = 7    [ 6, 8, 7 ]   -> answer 7
```

At every step the array is "half new row, half old row", exactly the picture from section 4.9.

### 8.3 Case 3 — reads previous row only, further to the left → reverse loop (0/1 Knapsack)

Recurrence (`i` = item number, `c` = capacity):

```
dp[i][c] = max( dp[i-1][c],                 // skip item i
                dp[i-1][c - w_i] + v_i )    // take item i (if w_i <= c)
```

Both reads are in the **previous** row. The second one is to the left, `w_i` columns back. Put this next to the in-place rule:

- Sweep **left to right**: when we reach `c`, slot `c - w` was already visited, so it holds the **new** value `dp[i][c-w]` — a state that may already contain item `i`. Taking item `i` on top of it uses item `i` twice. ✗
- Sweep **right to left**: when we reach `c`, slot `c - w` is not visited yet, so it holds the **old** value `dp[i-1][c-w]`. ✓

Trace with weights `[1, 3, 4]`, values `[15, 20, 30]`, W = 4. Look only at the first item (weight 1, value 15), starting from `dp = [0, 0, 0, 0, 0]`:

```
CORRECT: c from 4 down to 1                  BUG: c from 1 up to 4
c=4: max(0, dp[3]=0 + 15) = 15               c=1: max(0, dp[0]=0  + 15) = 15
c=3: max(0, dp[2]=0 + 15) = 15               c=2: max(0, dp[1]=15 + 15) = 30   <- item used twice
c=2: max(0, dp[1]=0 + 15) = 15               c=3: max(0, dp[2]=30 + 15) = 45   <- three times
c=1: max(0, dp[0]=0 + 15) = 15               c=4: max(0, dp[3]=45 + 15) = 60   <- four times
dp = [0, 15, 15, 15, 15]                     dp = [0, 15, 30, 45, 60]
```

Continuing the correct version with item 2 (w 3, v 20): `c=4: max(15, dp[1]=15 + 20) = 35`, `c=3: max(15, dp[0]+20) = 20` → `[0, 15, 15, 20, 35]`. Item 3 (w 4, v 30): `c=4: max(35, dp[0]+30) = 35` → final answer **35**. The full 2D table gives the same rows:

```
             c=0 c=1 c=2 c=3 c=4
no items      0   0   0   0   0
+item (1,15)  0  15  15  15  15
+item (3,20)  0  15  15  20  35
+item (4,30)  0  15  15  20  35
```

The bug version is not useless — it is **exactly** the unbounded knapsack. In unbounded knapsack the recurrence is `dp[i][c] = max(dp[i-1][c], dp[i][c-w] + v)`: the take-branch reads the **current** row, so we *want* the new value, and the forward loop is the correct one. This is why [Coin Change II](../../03-subsequence-and-knapsack-dp/09-coin-change-ii/) loops capacities upward.

> **Rule of thumb.** Take-branch reads the previous row → loop capacity downward. Take-branch reads the current row → loop upward. Never memorise the direction; read it off the footprint.

### 8.4 Case 4 — diagonal dependency → one row plus a saved `prev` (LCS)

Recurrence (`i` over `a`, `j` over `b`):

```
dp[i][j] = dp[i-1][j-1] + 1                 if a[i-1] == b[j-1]   (diagonal)
         = max(dp[i-1][j], dp[i][j-1])      otherwise             (up, left)
```

Sweep left to right in one array. "Up" (old `row[j]`) and "left" (new `row[j-1]`) behave like Minimum Path Sum. But the **diagonal** is the *old* `row[j-1]`, and we overwrote it one step ago. The fix is to remember it just before it disappears:

```java
int[] row = new int[m + 1];
for (int i = 1; i <= n; i++) {
    int prev = 0;                       // dp[i-1][0] = 0: the diagonal for j = 1
    for (int j = 1; j <= m; j++) {
        int above = row[j];             // dp[i-1][j], about to be overwritten
        if (a.charAt(i - 1) == b.charAt(j - 1)) row[j] = prev + 1;
        else row[j] = Math.max(above, row[j - 1]);
        prev = above;                   // the diagonal for column j + 1
    }
}
```

Why `prev = above`: when we move to column `j+1`, its diagonal is `dp[i-1][j]`, which is exactly the old `row[j]` we just saved in `above`.

What goes wrong without `prev`? Take `a = "a"`, `b = "aa"`, correct answer 1. If we wrote `row[j] = row[j-1] + 1` on a match, then at `j = 2` we would read the **new** `row[1] = 1` and produce 2 — the single `'a'` of `a` got matched twice. Same disease as the knapsack forward loop: reading a new value where the recurrence wanted an old one.

The full dry run of this code is in section 9.

### 8.5 When you cannot (or should not) optimize

1. **Reconstruction.** To print the actual LCS string, the path, or the chosen items, you walk *backwards* from the answer cell and at each step look at `dp[i-1][j]`, `dp[i][j-1]` — cells from rows that the one-row version has already erased. So the reconstruction needs the full table (see `lcsString` in [LongestCommonSubsequence.java](solution/LongestCommonSubsequence.java)). Options:
   - keep the full table (simplest; fine when it fits);
   - store only the *decision* in each cell (1 byte instead of 4) — still O(n·m) but smaller;
   - Hirschberg's algorithm: a divide-and-conquer that recovers an LCS in O(n + m) space with about twice the time. Worth knowing the name; rarely needed in interviews.
2. **Unbounded footprint.** The O(n²) LIS `dp[i] = 1 + max(dp[j] : j < i, a[j] < a[i])` reads *every* earlier cell. No cell ever dies, so nothing can be dropped. Interval DP (`dp[i][j]` reads `dp[i][k]` and `dp[k+1][j]` for all `k`) is similar.
3. **Many queries on different states.** If you must answer questions about many `(i, j)` pairs after the DP, you need the table.
4. **Top-down memoization.** As explained in section 6, the visiting order is not under your control.

---

## 9. Dry run on a full example

LCS of `a = "abcde"` (rows) and `b = "ace"` (columns), one-row version. `row` starts as `[0, 0, 0, 0]`.

| i (char) | j (char) | above = old row[j] | prev (diagonal) | match? | new row[j] | prev after |
|----------|----------|--------------------|-----------------|--------|------------|------------|
| 1 (a) | 1 (a) | 0 | 0 | yes | 0 + 1 = 1 | 0 |
| 1 (a) | 2 (c) | 0 | 0 | no | max(0, 1) = 1 | 0 |
| 1 (a) | 3 (e) | 0 | 0 | no | max(0, 1) = 1 | 0 |
| 2 (b) | 1 (a) | 1 | 0 | no | max(1, 0) = 1 | 1 |
| 2 (b) | 2 (c) | 1 | 1 | no | max(1, 1) = 1 | 1 |
| 2 (b) | 3 (e) | 1 | 1 | no | max(1, 1) = 1 | 1 |
| 3 (c) | 1 (a) | 1 | 0 | no | max(1, 0) = 1 | 1 |
| 3 (c) | 2 (c) | 1 | 1 | yes | 1 + 1 = 2 | 1 |
| 3 (c) | 3 (e) | 1 | 1 | no | max(1, 2) = 2 | 1 |
| 4 (d) | 1 (a) | 1 | 0 | no | max(1, 0) = 1 | 1 |
| 4 (d) | 2 (c) | 2 | 1 | no | max(2, 1) = 2 | 2 |
| 4 (d) | 3 (e) | 2 | 2 | no | max(2, 2) = 2 | 2 |
| 5 (e) | 1 (a) | 1 | 0 | no | max(1, 0) = 1 | 1 |
| 5 (e) | 2 (c) | 2 | 1 | no | max(2, 1) = 2 | 2 |
| 5 (e) | 3 (e) | 2 | 2 | yes | 2 + 1 = **3** | 2 |

Row after each `i`: `[0,1,1,1]`, `[0,1,1,1]`, `[0,1,2,2]`, `[0,1,2,2]`, `[0,1,2,3]` — identical to the rows of the full table:

```
        ""  a  c  e
   ""    0  0  0  0
   a     0  1  1  1
   b     0  1  1  1
   c     0  1  2  2
   d     0  1  2  2
   e     0  1  2  3
```

---

## 10. Complexity analysis

| Problem | Time (all versions) | Full-table space | Optimized space | Why the optimized space is enough |
|---------|---------------------|------------------|-----------------|-----------------------------------|
| House Robber | O(n): n cells × O(1) | O(n) | O(1) | footprint reaches back 2 cells |
| Minimum Path Sum | O(R·C) | O(R·C) | O(C) | footprint reaches back 1 row |
| 0/1 Knapsack | O(n·W) | O(n·W) | O(W) | footprint reaches back 1 row |
| LCS | O(n·m) | O(n·m) | O(m) + 1 variable | 1 row + the single diagonal value |
| LCS with reconstruction | O(n·m) + O(n+m) walk back | O(n·m) | — | the walk back reads arbitrary old rows |

Space optimization **never** changes the time complexity: we perform exactly the same computations, only in recycled memory. (Tip: pick the shorter string or the smaller dimension as the row, so the row has length `min(n, m)`.)

---

## 11. Why this approach works

The DP value of a cell is a fixed function of the cells in its footprint. The optimized program performs the same computations in the same order as the table version; the only difference is *where* values are stored. Correctness therefore reduces to one claim: **every read returns the same value the table version would read.** The last-reader rule guarantees we never read a slot after it was recycled for something else, and the in-place rule (old values ahead of the sweep, new values behind it) guarantees each read sees the right row. When the footprint wants a value the sweep has already destroyed (the LCS diagonal), we copy it into a variable before it is destroyed.

---

## 12. Common mistakes

1. **Wrong loop direction in knapsack.** Forward loop for 0/1 knapsack silently computes unbounded knapsack (35 becomes 60 in our example).
2. **Forgetting the diagonal.** Using the new `row[j-1]` as `dp[i-1][j-1]` in LCS / Edit Distance double-counts characters.
3. **Resetting `prev` in the wrong place.** `prev` must be re-initialised to `dp[i-1][0]` at the start of every row, not once before all rows.
4. **Not resetting column 0.** In Edit Distance `dp[i][0] = i` changes every row; in the one-row version you must set `row[0] = i` (after saving the old `row[0]` into `prev`).
5. **Swapping variables in the wrong order.** `prev1 = current; prev2 = prev1;` makes both equal to `current`.
6. **Optimizing before the table version works.** Always write and test the full table first, then optimize and compare (every solution file here does a randomized comparison).
7. **Optimizing when you need the path.** If the problem asks for the actual subsequence or path, keep the table.

---

## 13. When to use this technique / how to recognize similar problems

- The answer is **one number** (a min, max or count), not the full path.
- The table is large compared with the memory limit (≳ 10⁷ cells), or the interviewer asks for less space.
- The footprint reaches back a **constant** number of rows or cells.

Quick recognition table:

| Footprint | Technique |
|-----------|-----------|
| `i-1, i-2, …, i-k` | k variables (or a circular buffer) |
| previous row, same or right column | 1 row, sweep left → right (or 2 rows) |
| previous row, columns to the left | 1 row, sweep right → left |
| current row to the left | 1 row, sweep left → right |
| up + left + up-left | 1 row + `prev` |
| previous k layers of a 3D DP | rolling arrays → next topic |
| all earlier cells | no reduction |

---

## 14. Summary and revision notes

- Draw the **footprint**: which cells does one cell read?
- **Last-reader rule:** a cell may be forgotten once all its readers are computed.
- **In-place rule:** in a one-array sweep, visited slots are new, unvisited slots are old; choose the sweep direction so each read gets the version the recurrence wants.
- 0/1 knapsack: take-branch reads the previous row → capacities **downward**. Unbounded: reads the current row → **upward**.
- Diagonal read → save the old value in `prev` before overwriting.
- Reconstruction needs the table (or Hirschberg).

```
+--------------------------------------------------------------+
|  Minimum Path Sum, one row:                                  |
|    row[c] = grid[r][c] + min(row[c] (above, old),            |
|                              row[c-1] (left, new))           |
|  0/1 knapsack, one array (c from W down to w):               |
|    dp[c] = max(dp[c], dp[c - w] + v)                         |
|  LCS, one row + prev:                                        |
|    above = row[j];                                           |
|    row[j] = match ? prev + 1 : max(above, row[j-1]);         |
|    prev = above;                                             |
+--------------------------------------------------------------+
```

---

## 15. Exercises

**Exercise 1.** [Triangle](https://leetcode.com/problems/triangle/) (LC 120) computed bottom-up uses `dp[r][c] = tri[r][c] + min(dp[r+1][c], dp[r+1][c+1])`. Using one array of length `n`, in which direction must `c` be swept, and why?

<details><summary>Answer</summary>

The footprint is "below" and "below-right", both in the **old** row (`r+1`). Sweeping `c` left to right, when we reach `c` the slot `c` still holds `dp[r+1][c]` (old) and slot `c+1` has not been visited yet, so it also holds the old `dp[r+1][c+1]`. Both reads see old values: sweep **left to right**, `row[c] = tri[r][c] + min(row[c], row[c+1])`. A right-to-left sweep would overwrite `row[c+1]` before it is read.
</details>

**Exercise 2.** Coin Change II (number of combinations) uses `ways[c] += ways[c - coin]` with `c` going **up**. Using the footprint, explain why that is correct here although it is a bug in 0/1 knapsack.

<details><summary>Answer</summary>

Each coin may be used unlimited times, so the 2D recurrence is `ways[i][c] = ways[i-1][c] + ways[i][c - coin_i]`: the second term is in the **current** row. With an upward sweep, slot `c - coin` has already been updated for coin `i`, i.e. it holds the current-row value. That is exactly what the recurrence wants.
</details>

**Exercise 3.** Write the one-row version of [Edit Distance](https://leetcode.com/problems/edit-distance/) (LC 72). Pay attention to `prev` and to column 0.

<details><summary>Answer</summary>

```java
int[] row = new int[m + 1];
for (int j = 0; j <= m; j++) row[j] = j;           // dp[0][j] = j insertions
for (int i = 1; i <= n; i++) {
    int prev = row[0];                              // dp[i-1][0], the first diagonal
    row[0] = i;                                     // dp[i][0] = i deletions (column 0 changes!)
    for (int j = 1; j <= m; j++) {
        int above = row[j];                         // dp[i-1][j]
        if (a.charAt(i - 1) == b.charAt(j - 1)) row[j] = prev;
        else row[j] = 1 + Math.min(prev, Math.min(above, row[j - 1]));
        prev = above;
    }
}
return row[m];
```
Two differences from LCS: `prev` starts as the old `row[0]` (which is `i-1`, not 0), and `row[0]` must be set to `i` for every row.
</details>

**Exercise 4.** Can the O(n²) LIS DP `dp[i] = 1 + max(dp[j] : j < i and a[j] < a[i])` be reduced to O(1) space? Why or why not?

<details><summary>Answer</summary>

No. The footprint of `dp[i]` is **every** earlier index `j < i`, so no cell is ever dead before the end; all `n` values must stay. (The faster O(n log n) "patience" algorithm also uses O(n) memory.)
</details>
