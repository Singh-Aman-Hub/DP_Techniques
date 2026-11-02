# Minimum Path Sum — Walkthrough

## 1. Intuition (no code, no math — an everyday analogy)

You are crossing a field divided into square plots, from the north-west corner to the south-east corner, stepping only east or south. Every plot has a number: how many minutes it takes to cross it (mud is slow, grass is fast). You want the fastest crossing.

A first instinct is to be greedy: "at each step, take the faster of the two next plots". That can walk you into a corner of mud. Looking only one step ahead is not enough.

The DP way: for **every** plot, figure out "the fastest time to get from the start to the end of this plot". You reached it from the plot north of it or the one west of it, so:

> fastest to here = (time of this plot) + the smaller of (fastest to the plot north) and (fastest to the plot west)

If you fill these numbers in plot by plot, starting from the corner, the number in the far corner is the answer. You never guessed; you compared every option at every plot, but each plot was worked out only once.

---

## 2. Problem statement in our own words

Original: [LeetCode 64 — Minimum Path Sum](https://leetcode.com/problems/minimum-path-sum/) (Medium).

You get an `m × n` grid of non-negative integers. Starting on the top-left cell and moving only right or down, reach the bottom-right cell. The cost of a route is the sum of all cells on it, including the start and the end. Return the minimum possible cost.

---

## 3. Examples

Running example (used everywhere below):

```
 grid =  1 3 1
         1 5 1
         4 2 1        answer = 7, route 1 → 3 → 1 → 1 → 1
```

```
 route:  * * *
         . . *
         . . *
```

All six routes of a 3 × 3 board and their costs:

| Route | Cells | Cost |
|-------|-------|------|
| R R D D | 1, 3, 1, 1, 1 | **7** |
| R D R D | 1, 3, 5, 1, 1 | 11 |
| R D D R | 1, 3, 5, 2, 1 | 12 |
| D R R D | 1, 1, 5, 1, 1 | 9 |
| D R D R | 1, 1, 5, 2, 1 | 10 |
| D D R R | 1, 1, 4, 2, 1 | 9 |

More examples:

| Grid | Answer | Why |
|------|--------|-----|
| `[[1,2,3],[4,5,6]]` | 12 | `1 → 2 → 3 → 6` |
| `[[5]]` | 5 | Start = end; you still pay its cost. |
| `[[1,2,3]]` | 6 | One row: you must cross every cell. |
| `[[2],[3],[4]]` | 9 | One column. |

#### Greedy fails

Greedy "always step onto the cheaper next cell" on the running example: from `1`, right is 3 and down is 1 → go down. From there, right is 5 and down is 4 → go down. Then right 2, right 1. Route `1 → 1 → 4 → 2 → 1` = **9**, not 7. A cheap step now led into expensive cells later. We need to compare *complete* options, which is what DP does efficiently.

---

## 4. Deriving the solution — the 7 questions

### Q1. What is changing?

Our position: row `r` and column `c`. Two quantities → a 2D state.

### Q2. What does the state represent?

> `best(r, c)` = the minimum total cost of a right/down route from `(0, 0)` to `(r, c)`, counting the costs of both `(0, 0)` and `(r, c)`.

The answer is `best(m − 1, n − 1)`.

### Q3. What are the choices?

The last move into `(r, c)` came either from above, `(r − 1, c)`, or from the left, `(r, c − 1)`.

### Q4. What is the recurrence?

Whichever way we arrived, we pay `grid[r][c]` for this cell. To make the total as small as possible we arrive by the cheaper of the two predecessors:

```
 best(r, c) = grid[r][c] + min( best(r-1, c), best(r, c-1) )
              ^ pay this     ^ cheapest way   ^ cheapest way
                cell           from above       from the left
```

Compare with Unique Paths: `+` became `min`, and we add the cell's own cost.

### Q5. What are the base cases?

- `best(0, 0) = grid[0][0]`: the route is just the start cell.
- Off-board cells are **impossible** predecessors. For a `min`, "impossible" must be a value that never wins: `+∞`. (For counting it was 0 — see the table in [Unique Paths II](../03-unique-paths-ii/), Section 13.)

What does that give for the border?

- Top row: the only predecessor is the left cell (above is off-board, `+∞`), so `best(0, c) = grid[0][c] + best(0, c − 1)` — a **running sum** of the first row.
- Left column: likewise a running sum going down.

```
 first row  of the running example:  1, 1+3 = 4, 4+1 = 5
 first column:                       1, 1+1 = 2, 2+4 = 6
```

A frequent bug is to initialise the border to the cell values themselves (`best(0, c) = grid[0][c]`), forgetting that you had to walk through all earlier cells to get there.

#### Implementing `+∞` safely

`Integer.MAX_VALUE` works as `+∞` as long as you never **add** to it. In our recurrence we take `min` first and add `grid[r][c]` afterwards, and the `min` is never `+∞` for a real cell other than the start (every other cell has at least one on-board predecessor). So `grid[r][c] + min(...)` is safe. If you ever write `best(r−1, c) + grid[r][c]` with an off-board `best`, you get overflow to a negative number, which then *wins* the `min`. Keep the `+∞` inside the `min`.

### Q6. What is the order of computation?

Arrows come from above and from the left → row by row, left to right, exactly like [Unique Paths](../02-unique-paths/).

### Q7. Can the state space be optimized?

Row `r` reads row `r − 1` and itself only, so one array of length `n` is enough (Section 8). You can even reuse the input grid (Section 8, last part), at the cost of destroying it.

---

## 5. Brute force: recursion

```java
static final int INF = Integer.MAX_VALUE;

// minimum cost of a route from (0,0) to (r,c), including both ends
static int best(int[][] grid, int r, int c) {
    if (r < 0 || c < 0) return INF;              // off the board: an impossible predecessor
    if (r == 0 && c == 0) return grid[0][0];     // the start
    return grid[r][c] + Math.min(best(grid, r - 1, c), best(grid, r, c - 1));
}
```

Full file: [`solution/BruteForce.java`](solution/BruteForce.java).

### Recursion tree on the running example

Values in brackets are the results each call returns. `INF` = off-board.

```
                              best(2,2) [7]
                   /                              \
           best(1,2) [6]                       best(2,1) [8]
          /            \                      /             \
   best(0,2) [5]    best(1,1) [7]       best(1,1) [7]    best(2,0) [6]
     /      \         /        \          (repeat of      /        \
   INF  best(0,1)[4] best(0,1)[4] best(1,0)[2]  the left  best(1,0)[2]  INF
                                                 subtree)
 and below them:
   best(0,1) = 3 + min(INF, best(0,0)=1) = 4
   best(1,0) = 1 + min(best(0,0)=1, INF) = 2
```

`best(1,1)` is computed twice, `best(0,1)` three times, `best(1,0)` three times. The number of root-to-start chains equals the number of routes, `C(m+n−2, m−1)`, so the running time is exponential: `O(2^(m+n))`.

At the root: `best(2,2) = 1 + min(6, 8) = 7`. The route came from `(1,2)`.

---

## 6. Memoization (top-down)

Store each `best(r, c)` the first time it is computed. A path sum can be 0 (an all-zero grid), so use `-1` as "not computed yet".

```java
// memo[r][c] = minimum cost from (0,0) to (r,c); -1 = not computed yet
static int best(int[][] grid, int r, int c, int[][] memo) {
    if (r < 0 || c < 0) return INF;
    if (r == 0 && c == 0) return grid[0][0];
    if (memo[r][c] != -1) return memo[r][c];
    memo[r][c] = grid[r][c] + Math.min(best(grid, r - 1, c, memo), best(grid, r, c - 1, memo));
    return memo[r][c];
}
```

Full file: [`solution/Memoization.java`](solution/Memoization.java).

### Dry run (order in which memo cells are written)

```
 best(2,2) -> best(1,2) -> best(0,2) -> best(0,1) -> best(0,0)=1
   memo[0][1] = 3 + min(INF, 1) = 4
   memo[0][2] = 1 + min(INF, 4) = 5
 best(1,2) -> best(1,1) -> best(0,1) = 4 (CACHE HIT), best(1,0) -> best(0,0)=1
   memo[1][0] = 1 + min(1, INF) = 2
   memo[1][1] = 5 + min(4, 2)   = 7
   memo[1][2] = 1 + min(5, 7)   = 6
 best(2,1) -> best(1,1) = 7 (CACHE HIT), best(2,0) -> best(1,0) = 2 (CACHE HIT)
   memo[2][0] = 4 + min(2, INF) = 6
   memo[2][1] = 2 + min(7, 6)   = 8
 memo[2][2] = 1 + min(6, 8) = 7

 memo:   -1  4  5
          2  7  6
          6  8  7
```

Three cache hits replace three whole subtrees. Each of the 9 cells is computed at most once.

---

## 7. Tabulation (bottom-up)

Fill the table in row-major order. Handle the border as running sums, then use the recurrence inside.

```java
// dp[r][c] = minimum cost of a right/down route from (0,0) to (r,c), both ends included
int[][] dp = new int[m][n];
dp[0][0] = grid[0][0];
for (int c = 1; c < n; c++) dp[0][c] = dp[0][c - 1] + grid[0][c];   // top row: running sum
for (int r = 1; r < m; r++) dp[r][0] = dp[r - 1][0] + grid[r][0];   // left column: running sum
for (int r = 1; r < m; r++)
    for (int c = 1; c < n; c++)
        dp[r][c] = grid[r][c] + Math.min(dp[r - 1][c], dp[r][c - 1]);
return dp[m - 1][n - 1];
```

Full file: [`solution/Tabulation.java`](solution/Tabulation.java).

### Filling the running example step by step

```
 grid:      1 3 1
            1 5 1
            4 2 1

 border (running sums)     (1,1) = 5 + min(4,2) = 7    (1,2) = 1 + min(5,7) = 6
   1  4  5                   1  4  5                     1  4  5
   2  .  .                   2  7  .                     2  7  6
   6  .  .                   6  .  .                     6  .  .

 (2,1) = 2 + min(7,6) = 8    (2,2) = 1 + min(6,8) = 7
   1  4  5                     1  4  5
   2  7  6                     2  7  6
   6  8  .                     6  8  7    <- answer
```

### Recovering the route

The table tells us the cost; to find the route, walk **backwards** from the end, each time stepping to the predecessor that produced the `min`:

```
 at (2,2)=7: above (1,2)=6, left (2,1)=8  -> came from above  (1,2)
 at (1,2)=6: above (0,2)=5, left (1,1)=7  -> came from above  (0,2)
 at (0,2)=5: top row, only left            ->                  (0,1)
 at (0,1)=4: top row, only left            ->                  (0,0)

 reversed: (0,0) → (0,1) → (0,2) → (1,2) → (2,2)   costs 1 + 3 + 1 + 1 + 1 = 7 ✓
```

`Tabulation.java` contains a `bestRoute` method that does exactly this; its self-check confirms the route `1 3 1 1 1`. Reconstruction needs the **full** table, which is one reason to keep the 2D version even when a 1D one exists.

---

## 8. Space optimization

### The rolling row

Look at what computing row `r` reads:

```
 row r-1 :  [ ready ][ ready ][ ready ]     read: the "from above" value at column c
 row r   :  [ new   ][  c   ][       ]      read: the "from the left" value at column c-1
```

So keep a single array `row` of length `n`:

- `row[c]` **before** the update = `dp[r − 1][c]` (from above),
- `row[c − 1]` (already updated this pass) = `dp[r][c − 1]` (from the left).

```java
// row[c] = minimum cost to reach (r, c) for the row r being processed
int[] row = new int[n];
row[0] = grid[0][0];
for (int c = 1; c < n; c++) row[c] = row[c - 1] + grid[0][c];    // row 0: running sum
for (int r = 1; r < m; r++) {
    row[0] += grid[r][0];                                       // column 0: only from above
    for (int c = 1; c < n; c++)
        row[c] = grid[r][c] + Math.min(row[c], row[c - 1]);     // above (old) vs left (new)
}
return row[n - 1];
```

Full file: [`solution/Optimized.java`](solution/Optimized.java).

Dry run on the running example:

```
 row 0 (running sum):                           [1, 4, 5]
 r = 1: c=0: 1 + 1           = 2               [2, 4, 5]
        c=1: 5 + min(4, 2)   = 7               [2, 7, 5]
        c=2: 1 + min(5, 7)   = 6               [2, 7, 6]
 r = 2: c=0: 2 + 4           = 6               [6, 7, 6]
        c=1: 2 + min(7, 6)   = 8               [6, 8, 6]
        c=2: 1 + min(6, 8)   = 7               [6, 8, 7]
 answer = row[2] = 7
```

Compare each finished row with the rows of the 2D table: they are identical. We just stopped remembering the old ones.

Must the inner loop go left to right? Yes. `row[c − 1]` has to be the *new* value (row `r`) when `row[c]` is computed. Going right to left would read row `r − 1`'s value at `c − 1` — the diagonal, not the left neighbour.

Tip: if `m < n`, you can run the same idea over columns instead of rows and keep an array of length `min(m, n)`.

### In-place: `O(1)` extra space

Since `grid[r][c]` is read only once — when `dp[r][c]` is computed — you can overwrite `grid[r][c]` with `dp[r][c]`. That uses no extra array at all. The trade-off: the caller's grid is destroyed. In an interview, say so and ask whether modifying the input is allowed; in production code, prefer the rolling row.

---

## 9. Dry run on a full example

A second grid, `[[1,2,3],[4,5,6]]`, all approaches:

```
 grid:  1 2 3          dp:  1  3  6
        4 5 6               5  8 12     <- answer 12
```

| Step | Cell | Above | Left | `grid` | `dp` = grid + min | Rolling row after |
|------|------|-------|------|--------|-------------------|-------------------|
| 1 | (0,0) | — | — | 1 | 1 | `[1, ·, ·]` |
| 2 | (0,1) | ∞ | 1 | 2 | 3 | `[1, 3, ·]` |
| 3 | (0,2) | ∞ | 3 | 3 | 6 | `[1, 3, 6]` |
| 4 | (1,0) | 1 | ∞ | 4 | 5 | `[5, 3, 6]` |
| 5 | (1,1) | 3 | 5 | 5 | 8 | `[5, 8, 6]` |
| 6 | (1,2) | 6 | 8 | 6 | **12** | `[5, 8, 12]` |

Route: from `(1,2)`, above is 6 < left 8 → `(0,2)` → `(0,1)` → `(0,0)`: `1 → 2 → 3 → 6`.

---

## 10. Complexity analysis

| Approach | Time | Space | Explanation |
|----------|------|-------|-------------|
| Brute force | `O(2^(m+n))` | `O(m + n)` | Two calls per cell, depth `m + n − 2`; it effectively tries every route. |
| Memoization | `O(m · n)` | `O(m · n)` | `m · n` states × O(1) work (one `min`, one addition); memo plus `O(m + n)` stack. |
| Tabulation | `O(m · n)` | `O(m · n)` | One pass over the grid; full table (needed if you want the route). |
| Rolling row | `O(m · n)` | `O(n)` | Same pass, one row. |
| In-place | `O(m · n)` | `O(1)` extra | Reuses the input grid; destroys it. |

---

## 11. Why this approach works

Take the cheapest route to `(r, c)`. Remove its last cell: what remains is a route to the cell above or the cell to the left. That remaining route must itself be the cheapest route to *that* cell — if a cheaper one existed, swapping it in would give a cheaper route to `(r, c)`, a contradiction. This is **optimal substructure** ([Foundations 05](../../00-foundations/05-optimal-substructure/)). So the cheapest route to `(r, c)` is "the cheapest route to one of its two predecessors, plus this cell", and taking the `min` over both predecessors finds it. The base cases are the single-cell route at the start and `+∞` for impossible predecessors; the fill order guarantees predecessors are finished first.

Note that this argument needs every route to `(r, c)` to pay the same `grid[r][c]` no matter how it arrived. That is true here. In [Dungeon Game](../07-dungeon-game/) the value of arriving depends on *more* than the total so far, and this argument breaks — which is why that problem needs a different direction.

---

## 12. Common mistakes

1. **Border initialised to the cell values** instead of running sums. `dp[0][c]` must include every earlier cell of the row.
2. **Adding to `Integer.MAX_VALUE`.** `INF + grid[r][c]` overflows to a negative number that wins every `min`. Add the cell cost *after* the `min`.
3. **Forgetting the start's cost.** The answer for `[[5]]` is 5, not 0.
4. **Greedy step-by-step choice.** Picks 9 instead of 7 on the running example.
5. **Using 0 as "not computed" in the memo.** A sum can be 0 on an all-zero grid; use −1.
6. **Right-to-left update of the rolling row.** Reads the diagonal instead of the left neighbour.
7. **Trying to reconstruct the route from the rolling row.** The old rows are gone; keep the 2D table if the route is needed.

---

## 13. When to use this technique / how to recognize similar problems

Signals: a grid of costs or gains, monotone moves, "minimum/maximum total". The template:

```
 dp[r][c] = cell(r, c) + min/max over predecessors of dp[predecessor]
 impossible predecessor = +∞ for min, −∞ for max
```

Change the predecessors and you get the rest of this module: the row below in [Triangle](../05-triangle/), three cells of the previous row in [Falling Path Sum](../06-maximum-falling-path-sum/), and the cells *after* you (reverse direction) in [Dungeon Game](../07-dungeon-game/).

---

## 14. Summary and revision notes

- State: `best(r, c)` = cheapest route cost from `(0,0)` to `(r,c)`, both ends included.
- `+` (counting) becomes `min` (optimising); add the cell's own cost.
- Border cells are running sums; off-board predecessors are `+∞`.
- Fill row by row, left to right. One row of length `n` is enough for the cost.
- Keep the full table if you need the route; walk back choosing the smaller predecessor.

```
+-------------------------------------------------------------------+
|  best(r, c) = grid[r][c] + min( best(r-1, c), best(r, c-1) )      |
|  best(0, 0) = grid[0][0],   best(off-board) = +INF                |
|  rolling row:  row[c] = grid[r][c] + min(row[c], row[c-1])        |
+-------------------------------------------------------------------+
```

---

## Exercises

**Exercise 1.** Fill the table for `[[1,2],[1,1]]` and give the answer and the route.

<details>
<summary>Answer</summary>

```
 dp:  1 3
      2 3
```
Answer 3. At `(1,1)`: above 3, left 2 → from the left `(1,0)`, which came from `(0,0)`. Route `1 → 1 → 1` (down, then right).
</details>

**Exercise 2.** Change the problem to **maximum** path sum (same moves). What changes in the recurrence and the base cases? What is the answer for the running example?

<details>
<summary>Answer</summary>

Use `max` instead of `min`, and `−∞` instead of `+∞` for off-board predecessors (border cells stay running sums). Table:
```
 1  4  5
 2  9 10
 6 11 12
```
Answer 12, route `1 → 3 → 5 → 2 → 1` (right, down, down, right).
</details>

**Exercise 3.** In the rolling-row code, someone removes the line `row[0] += grid[r][0];`. What does the program return on the running example?

<details>
<summary>Answer</summary>

`row[0]` stays 1 for every row. `r = 1`: `c=1`: `5 + min(4,1) = 6`, `c=2`: `1 + min(5,6) = 6` → `[1,6,6]`. `r = 2`: `c=1`: `2 + min(6,1) = 3`, `c=2`: `1 + min(6,3) = 4` → returns **4**, which is wrong (7 is correct). The left column must also accumulate its costs.
</details>

**Exercise 4.** Why is it safe to compute `grid[r][c] + Math.min(above, left)` with `INF = Integer.MAX_VALUE`, but not `Math.min(above + grid[r][c], left + grid[r][c])`?

<details>
<summary>Answer</summary>

Every cell other than the start has at least one real predecessor, so the `min` of the two is a real (finite) value before any addition happens. In the second form, the off-board `INF` gets `grid[r][c]` added *first*: `Integer.MAX_VALUE + 3` overflows to a large negative number, which then wins the `min` and corrupts the answer.
</details>

---

⬅ [README](README.md) · ➡ Next topic: [Triangle](../05-triangle/)
