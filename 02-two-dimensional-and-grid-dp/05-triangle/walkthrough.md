# Triangle — Walkthrough

## 1. Intuition (no code, no math — an everyday analogy)

Imagine a hillside vineyard laid out in terraces. The top terrace has one gate, the next has two, the next has three, and so on. Every gate charges a toll. From a gate you may walk down to the gate directly below you or the one just below-right. You want to get from the single top gate to *any* gate on the bottom terrace while paying as little as possible.

Standing at the top, you cannot see which route is cheapest; the cheap gates near the top might lead to expensive ones near the bottom. But a person standing on the **second-to-last** terrace has an easy job: each of them sees exactly two gates below and simply picks the cheaper one. Once every person on that terrace knows "my cheapest way down costs X", the people one terrace higher can do the same thing, looking only one step down. The knowledge flows **upward**, terrace by terrace, until the person at the top knows the answer.

That is the whole algorithm. The rest of this lesson makes it precise, shows why filling from the bottom is cleaner than filling from the top, and squeezes it into a single array.

## 2. Problem statement in our own words

We are given a triangle of integers with `n` rows. Row `r` (counting from 0) holds `r + 1` numbers. A path starts at the top number and moves down one row at a time; from position `c` in a row, it may move to position `c` or position `c + 1` in the next row. The cost of a path is the sum of the numbers it visits. Return the smallest possible cost of a path that reaches the last row.

Original problem: [LeetCode 120 — Triangle](https://leetcode.com/problems/triangle/). Values can be negative.

## 3. Examples (input → output, with an explanation)

We use **one example throughout this lesson**:

```
row 0:          2
row 1:        3   4
row 2:      6   5   7
row 3:    4   1   8   3
```

As a jagged array: `[[2], [3, 4], [6, 5, 7], [4, 1, 8, 3]]` → **11**.

The cheapest path is `2 → 3 → 5 → 1 = 11`:

```
              [2]
            [3]  4
           6  [5]  7
         4  [1]  8   3
```

Note that the greedy choice "always step to the smaller child" happens to work here (2 → 3 → 5 → 1), but it fails in general. Example: `[[1], [2, 3], [9, 9, 1]]`. Greedy goes 1 → 2 → 9 = 12, but 1 → 3 → 1 = 5 is better. A small number now can lead into a costly neighborhood later; DP looks at everything below.

More examples:

| Input | Output | Why |
|---|---|---|
| `[[-10]]` | -10 | One row: the path is just the apex. |
| `[[1], [2, 3]]` | 3 | 1 → 2. |
| `[[-1], [2, 3], [1, -1, -3]]` | -1 | -1 → 3 → -3 = -1 (beats -1 → 2 → -1 = 0). |

## 4. Deriving the solution — the 7 questions

### 4.1 What is changing?

As the path descends, two things change: the **row** we are on and the **position (column)** inside that row. Two changing quantities means a 2D state, exactly as in [2D DP state representation](../01-2d-dp-state-representation/). The difference from a rectangle is that the valid columns depend on the row: in row `r` the column runs from `0` to `r`.

### 4.2 What does the state represent?

There are two natural choices, and choosing well is the main lesson of this topic.

- **Option A (from the apex):** `top[r][c]` = the minimum cost of a path that starts at the apex and **ends at** cell `(r, c)`.
- **Option B (to the bottom):** `dp[r][c]` = the minimum cost of a path that **starts at** cell `(r, c)` and ends anywhere in the last row.

We will use **Option B**:

> `dp[r][c]` is the smallest total of the numbers on any path that begins at row `r`, column `c`, and moves down until it reaches the last row.

The final answer is `dp[0][0]`.

### 4.3 What are the choices?

At cell `(r, c)` (not in the last row) there are exactly two moves:

1. Step **straight down** to `(r + 1, c)`.
2. Step **down-right** to `(r + 1, c + 1)`.

Both always exist. Row `r + 1` has columns `0 .. r + 1`, and `c ≤ r`, so `c + 1 ≤ r + 1`. This little fact is what makes Option B clean.

### 4.4 What is the recurrence?

We pay for the current cell, then continue along whichever child's best path is cheaper:

```
dp[r][c] = triangle[r][c] + min( dp[r+1][c], dp[r+1][c+1] )
           \_____________/        \________/   \__________/
            toll of this cell    best if we    best if we
                                 go straight   go down-right
```

We use `min` because we want the cheapest cost, and `+` because the cost of a path is the sum of its cells.

### 4.5 What are the base cases?

The last row has no children. A path starting there is just that one cell:

```
dp[n-1][c] = triangle[n-1][c]      for every c in 0 .. n-1
```

### 4.6 What is the order of computation?

`dp[r][...]` reads only row `r + 1`. So row `n-1` must be finished first, then row `n-2`, and so on up to row 0. Within a row, cells do not depend on each other, so any column order works.

```
Dependency arrows (each cell points to the cells it reads):

   (r, c)
    |   \
    v    v
 (r+1,c) (r+1,c+1)

Fill order: row n-1  →  row n-2  →  ...  →  row 0
```

### 4.7 Can the state space be optimized?

Row `r` reads only row `r + 1`. Once row `r` is done, row `r + 1` is never read again. So we need only one row's worth of values, which is an array of length `n` (the width of the widest row). Section 8 shows that we can even update that one array in place.

### Why not Option A?

Option A works too, but look at its recurrence. The parents of `(r, c)` are `(r-1, c-1)` and `(r-1, c)`. Neither parent is guaranteed to exist:

```
top[r][0] = top[r-1][0]   + t[r][0]          (left edge: no up-left parent)
top[r][r] = top[r-1][r-1] + t[r][r]          (right edge: no straight-up parent,
                                              because row r-1 has no column r)
top[r][c] = t[r][c] + min(top[r-1][c-1], top[r-1][c])   for 0 < c < r
answer    = min over c of top[n-1][c]        (extra scan of the last row)
```

That is three cases plus a final scan. Forgetting either edge case gives an `ArrayIndexOutOfBoundsException` (reading `top[r-1][r]`) or a wrong answer. Option B has **one** case and returns its answer in one cell. The general rule:

> If every state has all its "next" states available but not all its "previous" states, define the state as "best from here to the end" and fill from the end.

## 5. Brute force: recursion

Option B translates directly into recursion: "best from `(r, c)`" = my value + the better of my two children's best.

```java
static int minPathFrom(int[][] triangle, int row, int col) {
    if (row == triangle.length - 1) {
        return triangle[row][col];          // base case: last row
    }
    int goStraightDown = minPathFrom(triangle, row + 1, col);
    int goDownRight    = minPathFrom(triangle, row + 1, col + 1);
    return triangle[row][col] + Math.min(goStraightDown, goDownRight);
}
```

Full file: [solution/BruteForce.java](solution/BruteForce.java).

### Recursion tree on our example

Write `f(r,c)` for `minPathFrom(triangle, r, c)`.

```
                              f(0,0)
                   /                          \
              f(1,0)                          f(1,1)
            /        \                      /        \
       f(2,0)        f(2,1)            f(2,1)        f(2,2)
       /    \        /    \            /    \        /    \
   f(3,0) f(3,1) f(3,1) f(3,2)    f(3,1) f(3,2) f(3,2) f(3,3)
     4      1      1      8         1      8      8      3
```

Count the repeats:

- `f(2,1)` is computed **twice** (once under `f(1,0)`, once under `f(1,1)`).
- `f(3,1)` and `f(3,2)` are each computed **three** times.

### Why it is slow

Every non-base call makes two calls, and the tree is `n` levels deep, so there are `1 + 2 + 4 + … + 2^(n-1) = 2^n − 1` calls. For our 4-row triangle that is 15 calls for only 10 distinct cells. For `n = 200` (LeetCode's limit) it is about `2^200` calls — far beyond any computer. But there are only `n(n+1)/2 = 20,100` distinct cells. The waste comes from **overlapping subproblems**: two different parents share a child.

## 6. Memoization (top-down)

We cache `f(r, c)` the first time we compute it. The cache has the same jagged shape as the triangle.

One detail: the values can be negative, so no integer is safe as a "not computed" marker. We use `Integer[][]`, where `null` means "not computed yet".

```java
private static Integer[][] memo;   // memo[row][col] = best path from (row, col) to the bottom

static int minPathFrom(int[][] triangle, int row, int col) {
    if (row == triangle.length - 1) return triangle[row][col];
    if (memo[row][col] != null) return memo[row][col];      // cache hit
    int goStraightDown = minPathFrom(triangle, row + 1, col);
    int goDownRight    = minPathFrom(triangle, row + 1, col + 1);
    memo[row][col] = triangle[row][col] + Math.min(goStraightDown, goDownRight);
    return memo[row][col];
}
```

Full file: [solution/Memoization.java](solution/Memoization.java).

### Dry run with cache hits

The calls happen in this order (indentation = recursion depth):

```
f(0,0)
  f(1,0)
    f(2,0)
      f(3,0) = 4        (base)
      f(3,1) = 1        (base)
    f(2,0) = 6 + min(4, 1) = 7          -> memo[2][0] = 7
    f(2,1)
      f(3,1) = 1        (base)
      f(3,2) = 8        (base)
    f(2,1) = 5 + min(1, 8) = 6          -> memo[2][1] = 6
  f(1,0) = 3 + min(7, 6) = 9            -> memo[1][0] = 9
  f(1,1)
    f(2,1)  ** CACHE HIT ** returns 6   (no recursion below it)
    f(2,2)
      f(3,2) = 8        (base)
      f(3,3) = 3        (base)
    f(2,2) = 7 + min(8, 3) = 10         -> memo[2][2] = 10
  f(1,1) = 4 + min(6, 10) = 10          -> memo[1][1] = 10
f(0,0) = 2 + min(9, 10) = 11            -> answer 11
```

The cache hit on `f(2,1)` saved a whole subtree. In a 200-row triangle such hits save almost all of the work.

## 7. Tabulation (bottom-up)

Now we drop recursion and fill the table directly in the order from Section 4.6: last row first, then upward.

```java
int[][] dp = new int[rows][];
for (int r = 0; r < rows; r++) dp[r] = new int[r + 1];        // jagged, like the triangle

for (int col = 0; col < rows; col++)                           // base case
    dp[rows - 1][col] = triangle[rows - 1][col];

for (int row = rows - 2; row >= 0; row--)
    for (int col = 0; col <= row; col++)
        dp[row][col] = triangle[row][col] + Math.min(dp[row + 1][col], dp[row + 1][col + 1]);

return dp[0][0];
```

Full file: [solution/Tabulation.java](solution/Tabulation.java). It also contains `minimumTotalFromApex`, the Option A version, so you can compare the two side by side.

### Table filled step by step

**Step 0 — base case (copy the last row):**

```
row 0:            ?
row 1:          ?    ?
row 2:        ?    ?    ?
row 3:      4    1    8    3
```

**Step 1 — row 2:**

```
dp[2][0] = 6 + min(dp[3][0], dp[3][1]) = 6 + min(4, 1) =  7
dp[2][1] = 5 + min(dp[3][1], dp[3][2]) = 5 + min(1, 8) =  6
dp[2][2] = 7 + min(dp[3][2], dp[3][3]) = 7 + min(8, 3) = 10

row 0:            ?
row 1:          ?    ?
row 2:        7    6   10
row 3:      4    1    8    3
```

**Step 2 — row 1:**

```
dp[1][0] = 3 + min(dp[2][0], dp[2][1]) = 3 + min(7, 6)  =  9
dp[1][1] = 4 + min(dp[2][1], dp[2][2]) = 4 + min(6, 10) = 10

row 0:            ?
row 1:          9   10
row 2:        7    6   10
row 3:      4    1    8    3
```

**Step 3 — row 0:**

```
dp[0][0] = 2 + min(dp[1][0], dp[1][1]) = 2 + min(9, 10) = 11

row 0:           11          <- answer
row 1:          9   10
row 2:        7    6   10
row 3:      4    1    8    3
```

Every number here equals the matching memo value from Section 6. Memoization and tabulation compute the same function; only the order differs.

### For contrast: the apex-first table (Option A)

```
row 0:            2
row 1:          5    6                 5 = 2+3 (left edge),  6 = 2+4 (right edge)
row 2:       11   10   13              11 = 5+6 (edge), 10 = 5 + min(5,6), 13 = 6+7 (edge)
row 3:     15   11   18   16           15 = 11+4 (edge), 11 = 1 + min(11,10),
                                       18 = 8 + min(10,13), 16 = 13+3 (edge)
answer = min(15, 11, 18, 16) = 11
```

Same answer, but six of the nine non-apex cells needed the special edge formula, and we had to scan the last row at the end.

## 8. Space optimization

Row `r` reads only row `r + 1`. So we keep a single array `below` of length `n`. It starts as the last row. For each row from `n-2` up to `0`, we overwrite `below[col]` for `col = 0 .. row`:

```java
int[] below = new int[rows];
for (int col = 0; col < rows; col++) below[col] = triangle[rows - 1][col];

for (int row = rows - 2; row >= 0; row--)
    for (int col = 0; col <= row; col++)
        below[col] = triangle[row][col] + Math.min(below[col], below[col + 1]);

return below[0];
```

Full file: [solution/Optimized.java](solution/Optimized.java).

### Why the in-place update is safe

When we compute `below[col]` for the current row, we read `below[col]` and `below[col + 1]`. Going left to right:

- `below[col]` still holds row `r + 1`'s value (we are about to overwrite it, and we read it first).
- `below[col + 1]` still holds row `r + 1`'s value (we have not reached it yet in this row).

So both reads see the old row, exactly as the 2D recurrence requires. If you went right to left, `below[col + 1]` would already contain the *new* row's value when you computed `below[col]`, which is wrong.

```
row 2 pass, left to right:
            index:  0    1    2    3
   start         [  4,   1,   8,   3 ]   (row 3)
   col 0: 6+min(4,1)=7   -> [ 7,   1,   8,   3 ]
   col 1: 5+min(1,8)=6   -> [ 7,   6,   8,   3 ]
   col 2: 7+min(8,3)=10  -> [ 7,   6,  10,   3 ]
                               ^read  ^read  were still row-3 values when read
```

Position 3 is left over from row 3 and is never read again: row `r` only touches indices `0 .. r + 1`.

## 9. Dry run on a full example (table of state values per step)

The optimized array after each row of the example:

| Step | Row processed | Computations | `below` afterward |
|---|---|---|---|
| 0 | base (row 3) | copy `[4, 1, 8, 3]` | `[4, 1, 8, 3]` |
| 1 | row 2, col 0 | `6 + min(4, 1) = 7` | `[7, 1, 8, 3]` |
| 2 | row 2, col 1 | `5 + min(1, 8) = 6` | `[7, 6, 8, 3]` |
| 3 | row 2, col 2 | `7 + min(8, 3) = 10` | `[7, 6, 10, 3]` |
| 4 | row 1, col 0 | `3 + min(7, 6) = 9` | `[9, 6, 10, 3]` |
| 5 | row 1, col 1 | `4 + min(6, 10) = 10` | `[9, 10, 10, 3]` |
| 6 | row 0, col 0 | `2 + min(9, 10) = 11` | `[11, 10, 10, 3]` |

Answer: `below[0] = 11`.

**Recovering the path.** Start at `(0,0)`. At each row, move to the child with the smaller `dp` value (use the 2D table from Section 7): `dp[1][0] = 9 < dp[1][1] = 10` → go to `(1,0)`; `dp[2][1] = 6 < dp[2][0] = 7` → go to `(2,1)`; `dp[3][1] = 1 < dp[3][2] = 8` → go to `(3,1)`. Path: `2 → 3 → 5 → 1`. This "follow the better child" trick only works with the full table; the 1D array has thrown the rows away.

## 10. Complexity analysis (explained, for every approach)

Let `n` be the number of rows. The triangle has `1 + 2 + … + n = n(n+1)/2` cells, which is `O(n²)`.

| Approach | Time | Space | Explanation |
|---|---|---|---|
| Brute force | `O(2^n)` | `O(n)` | Each call spawns two calls for `n` levels: `2^n − 1` calls. The stack holds one frame per row. |
| Memoization | `O(n²)` | `O(n²)` | Each of the `n(n+1)/2` cells is computed once with `O(1)` work (two lookups, a `min`, an add). The memo has one slot per cell; the stack adds `O(n)`. |
| Tabulation | `O(n²)` | `O(n²)` | Two nested loops visit each cell once with `O(1)` work. The table has one slot per cell. |
| Optimized | `O(n²)` | `O(n)` | Same loops, but only one array of length `n` is stored. |

We cannot do better than `O(n²)` time, because every number in the triangle might be on the best path, so every number must be read at least once.

## 11. Why this approach works (correctness argument in plain words)

Take any path starting at `(r, c)`. Its first step goes to one of the two children. After that step, the rest of the path is itself a path starting at that child. So the cost of the whole path is `triangle[r][c] + (cost of a path from the child)`. To make the total as small as possible, the remainder must be as cheap as possible **from that child** — and that is exactly `dp` of the child. Taking the cheaper of the two children covers both possible first steps. Nothing about how we arrived at `(r, c)` affects which paths are possible below it, so the subproblem is self-contained. This is the [optimal substructure](../../00-foundations/05-optimal-substructure/) property, and by induction from the last row upward every `dp` value is correct.

## 12. Common mistakes

1. **Index out of bounds in the apex-first version.** Reading `top[r-1][c]` when `c == r`: row `r-1` has no column `r`. If you fill from the top, the right edge needs its own case.
2. **Forgetting the final scan in the apex-first version.** `top[n-1][n-1]` is *not* the answer; the path may end at any cell of the last row.
3. **Using greedy.** "Always step to the smaller child" fails (`[[1],[2,3],[9,9,1]]`: greedy 12, correct 5).
4. **Using `-1` or `0` as the memo marker.** The values can be negative or zero, so a real answer can equal your marker. Use `Integer` with `null`, or a separate `boolean[][] computed`.
5. **Updating the 1D array right to left.** `below[col + 1]` would already be overwritten with the current row's value.
6. **Allocating a full `n × n` square.** It works, but half of it is unused. Jagged rows match the problem's shape and make the indexing obvious.
7. **Modifying the input triangle in place without being allowed to.** It saves memory, but callers may not expect their input to change. Prefer a separate array unless told otherwise.

## 13. When to use this technique / how to recognize similar problems

Signals:

- You move through **levels** (rows, days, stages), one level per step.
- From a position you may move only to a **few nearby positions** on the next level.
- You want the best total, and the path may **end anywhere** on the last level (or start anywhere on the first).

Then define `dp[level][pos]` as "the best total from here to the end" and fill from the last level backward. The answer then sits in one cell, or — when the path may also start anywhere — in a min/max over the first row.

Related problems:

- [Minimum Falling Path Sum](https://leetcode.com/problems/minimum-falling-path-sum/) (LC 931): the next topic; three children instead of two, and the start can be any top cell.
- [Minimum Falling Path Sum II](https://leetcode.com/problems/minimum-falling-path-sum-ii/) (LC 1289): every cell in the next row except one is a child.
- [Dungeon Game](https://leetcode.com/problems/dungeon-game/) (LC 174): the backward direction is the *only* one that works there.

## 14. Summary and revision notes

- State: `dp[r][c]` = cheapest path from `(r, c)` down to the last row.
- Both children `(r+1, c)` and `(r+1, c+1)` always exist, so there are **no edge cases**.
- Base: the last row equals the triangle's last row. Fill rows upward. Answer: `dp[0][0]`.
- The apex-first direction needs two edge formulas per row plus a final scan.
- One array of length `n`, updated left to right, is enough; `O(n²)` time, `O(n)` space.

```
+---------------------------------------------------------------+
|  dp[n-1][c] = t[n-1][c]                                       |
|  dp[r][c]   = t[r][c] + min( dp[r+1][c], dp[r+1][c+1] )       |
|  answer     = dp[0][0]                                        |
+---------------------------------------------------------------+
```

## Exercises

**Exercise 1.** Change the problem to *maximum* total. What is the answer for our example triangle, and along which path?

<details>
<summary>Answer</summary>

Replace `min` with `max`. Bottom-up: row 3 = `[4, 1, 8, 3]`; row 2 = `[6+4, 5+8, 7+8] = [10, 13, 15]`; row 1 = `[3+13, 4+15] = [16, 19]`; row 0 = `2 + 19 = 21`. Path: `2 → 4 → 7 → 8 = 21`.
</details>

**Exercise 2.** How many different top-to-bottom paths does a triangle with `n` rows have? Why does that number explain the brute force running time?

<details>
<summary>Answer</summary>

`2^(n-1)`: each of the `n-1` steps has two independent choices. The brute force explores every path separately, so its work is proportional to the number of paths (the recursion tree has `2^(n-1)` leaves and `2^n − 1` nodes in total).
</details>

**Exercise 3.** In the optimized solution, a classmate loops `for (int col = row; col >= 0; col--)` (right to left). Run their code on `[[0], [-10, -10], [100, 100, 0]]`. What do they print, and what is the correct answer?

<details>
<summary>Answer</summary>

Correct answer: the four paths cost `0-10+100 = 90`, `90`, `90` and `0-10+0 = -10`, so the answer is **-10**.

Right to left: `below` starts as `[100, 100, 0]`. Row 1, `col 1`: `-10 + min(100, 0) = -10` → `[100, -10, 0]`. Row 1, `col 0`: `-10 + min(100, below[1])`, but `below[1]` is now the *row 1* value `-10`, so it computes `-10 + (-10) = -20` → `[-20, -10, 0]`. Row 0: `0 + min(-20, -10) = -20`. They print **-20**.

The value -20 belongs to an illegal path `0 → -10 → (sideways) -10 → 0` that moves along row 1. Right-to-left order lets a cell read its right neighbour's *new* value, which is a sideways move. Left to right, every read still sees the row below.
</details>

**Exercise 4.** Rewrite the recurrence so the path may start at **any** cell of the first row of a *rectangular* grid and end anywhere in the last row, moving down or down-right. Where is the answer now?

<details>
<summary>Answer</summary>

`dp[r][c] = grid[r][c] + min(dp[r+1][c], dp[r+1][c+1])`, with `dp[r+1][c+1]` treated as "not available" (+∞) when `c` is the last column, because a rectangle's next row is not wider. The answer is `min over c of dp[0][c]`, since the start is free. This is one step away from the falling-path problem in the next topic.
</details>
