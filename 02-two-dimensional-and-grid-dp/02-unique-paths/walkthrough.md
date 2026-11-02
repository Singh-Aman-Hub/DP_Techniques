# Unique Paths — Walkthrough

## 1. Intuition (no code, no math — an everyday analogy)

You are a delivery rider in a city whose streets form a perfect grid. The warehouse is at the north-west corner, the customer at the south-east corner. Traffic rules allow you to ride only **east** or **south**. How many different routes exist?

Pick any intersection. You could only have arrived there from the intersection **just north** of it or the one **just west** of it. So if a friend has already written, on every intersection, "number of routes from the warehouse to here", you could fill in a new intersection by adding the two numbers written north and west of it.

Start with the easy ones: every intersection on the top street can only be reached by riding straight east — 1 route each. Every intersection on the west-most street can only be reached by riding straight south — 1 route each. Now fill the rest of the city street by street. The number at the customer's corner is the answer.

That is the whole algorithm. The rest of this walkthrough makes it precise, turns it into code, and then makes the code use less memory.

---

## 2. Problem statement in our own words

Original: [LeetCode 62 — Unique Paths](https://leetcode.com/problems/unique-paths/) (Medium).

A robot sits on the top-left cell of a board with `m` rows and `n` columns. In one move it goes one cell right or one cell down. Return the number of distinct move sequences that take it to the bottom-right cell. (The board sizes are at most 100 and the answer is guaranteed to fit in a 32-bit signed integer.)

---

## 3. Examples

| `m` (rows) | `n` (cols) | Answer | Explanation |
|------------|------------|--------|-------------|
| 3 | 3 | **6** | Our running example. Four moves: 2 downs and 2 rights in any order. |
| 3 | 7 | 28 | The board from LeetCode's first example. |
| 3 | 2 | 3 | `R D D`, `D R D`, `D D R`. |
| 1 | 1 | 1 | Start = end. The empty route counts as one route. |
| 1 | 5 | 1 | One row: the only route is straight right. |

The six routes of the 3 × 3 board (the running example used everywhere below):

```
 RRDD      RDRD      RDDR      DRRD      DRDR      DDRR
 * * *     * * .     * * .     * . .     * . .     * . .
 . . *     . * *     . * .     * * *     * * .     * . .
 . . *     . . *     . * *     . . *     . * *     * * *
```

---

## 4. Deriving the solution — the 7 questions

### Q1. What is changing?

The robot's position: its row `r` and its column `c`. Each move increases exactly one of them by 1. Two independent quantities → a 2D state (see [2D DP State Representation](../01-2d-dp-state-representation/)).

### Q2. What does the state represent?

> `paths(r, c)` = the number of different right/down routes from the start cell `(0, 0)` to the cell `(r, c)`.

The answer to the problem is `paths(m − 1, n − 1)`.

### Q3. What are the choices?

Look at the **last** move into `(r, c)`:

- it was a **down** move from `(r − 1, c)`, or
- it was a **right** move from `(r, c − 1)`.

### Q4. What is the recurrence?

The two groups of routes are different (their last move differs) and together they include every route. So we add:

```
 paths(r, c) = paths(r - 1, c)  +  paths(r, c - 1)
               ^ routes that end     ^ routes that end
                 with a down move      with a right move
```

### Q5. What are the base cases?

- `paths(0, c) = 1` for every column: on the top row you can only have come from the left, all the way from the start.
- `paths(r, 0) = 1` for every row: on the left column you can only have come from above.
- These include `paths(0, 0) = 1` (the empty route).

Why use the whole base row and column, instead of only `(0,0)` plus "off-board = 0"? Both work and give the same numbers. Using the border as the base case means the recurrence is only ever applied to cells that *have* both neighbours, so the code needs no off-board checks.

### Q6. What is the order of computation?

`paths(r, c)` reads the cell above and the cell on the left. Arrows point down and right:

```
          (r-1, c)
             |
             v
 (r, c-1) -> (r, c)
```

So filling row by row, each row left to right, is valid: when we reach `(r, c)`, row `r − 1` is finished and `(r, c − 1)` was just computed.

### Q7. Can the state space be optimized?

Row `r` uses only row `r − 1` and itself. One array of `n` numbers is enough (Section 8). Even better for this specific problem: a formula gives the answer directly (Section 8b).

---

## 5. Brute force: recursion

```java
// number of right/down routes from (0,0) to (r,c)
static int countPaths(int r, int c) {
    if (r == 0 || c == 0) return 1;                       // base row or base column
    return countPaths(r - 1, c) + countPaths(r, c - 1);   // last move was down, or right
}
// answer = countPaths(m - 1, n - 1)
```

Full file: [`solution/BruteForce.java`](solution/BruteForce.java).

### Recursion tree for `m = 3, n = 3`

```
                        paths(2,2)
                 /                     \
           paths(1,2)                paths(2,1)
           /        \                /         \
     paths(0,2)  paths(1,1)     paths(1,1)   paths(2,0)
         1       /       \      /       \        1
           paths(0,1) paths(1,0) paths(0,1) paths(1,0)
               1         1         1          1

 leaves: 1 + 1 + 1 + 1 + 1 + 1 = 6   ✓
```

`paths(1,1)` is solved twice. Grow the board and the repetition explodes: every route ends in its own leaf, so the number of leaves **equals the answer itself**. For `m = n = 17` that is 601,080,390 leaves for a board of only 289 cells.

**Why it is slow:** each call branches into two and the depth is `(m − 1) + (n − 1)`, so the work is exponential — `O(2^(m+n))` as an upper bound. The number of *different* calls, however, is only `m × n`. That gap is exactly what memoization closes.

---

## 6. Memoization (top-down)

Cache one answer per cell in `memo[m][n]`. A real path count is at least 1, so 0 can mean "not computed yet".

```java
// memo[r][c] = number of routes from (0,0) to (r,c); 0 = not computed yet
static int countPaths(int r, int c, int[][] memo) {
    if (r == 0 || c == 0) return 1;
    if (memo[r][c] != 0) return memo[r][c];               // cache hit
    memo[r][c] = countPaths(r - 1, c, memo) + countPaths(r, c - 1, memo);
    return memo[r][c];
}
```

Full file: [`solution/Memoization.java`](solution/Memoization.java).

### Dry run on 3 × 3

`·` = not computed, `b` = base cell (never stored; returned directly).

```
 call paths(2,2)
  call paths(1,2)
   paths(0,2) -> 1 (base)
   call paths(1,1)
    paths(0,1) -> 1 (base), paths(1,0) -> 1 (base)
    memo[1][1] = 2                          b b b
                                            b 2 ·
                                            b · ·
   memo[1][2] = 1 + 2 = 3                   b b b
                                            b 2 3
                                            b · ·
  call paths(2,1)
   paths(1,1) -> CACHE HIT 2       <-- the second paths(1,1) of the tree is free
   paths(2,0) -> 1 (base)
   memo[2][1] = 2 + 1 = 3                   b b b
                                            b 2 3
                                            b 3 ·
  memo[2][2] = 3 + 3 = 6                    b b b
                                            b 2 3
                                            b 3 6
 answer = 6
```

Four cells were computed once each, and one cache hit replaced a whole subtree.

---

## 7. Tabulation (bottom-up)

Replace the recursion with a table filled in the order from Q6.

```java
// dp[r][c] = number of routes from (0,0) to (r,c)
int[][] dp = new int[m][n];
for (int c = 0; c < n; c++) dp[0][c] = 1;   // base row
for (int r = 0; r < m; r++) dp[r][0] = 1;   // base column
for (int r = 1; r < m; r++)
    for (int c = 1; c < n; c++)
        dp[r][c] = dp[r - 1][c] + dp[r][c - 1];
return dp[m - 1][n - 1];
```

Full file: [`solution/Tabulation.java`](solution/Tabulation.java).

### Filling the 3 × 3 table step by step

```
 Base row and column:       (1,1) = 1 + 1          (1,2) = 1 + 2
   1  1  1                    1  1  1                1  1  1
   1  .  .                    1  2  .                1  2  3
   1  .  .                    1  .  .                1  .  .

 (2,1) = 2 + 1              (2,2) = 3 + 3
   1  1  1                    1  1  1
   1  2  3                    1  2  3
   1  3  .                    1  3  6     <- answer
```

You may recognise these numbers: they are Pascal's triangle, tilted 45°. Every number is the sum of the two "parents" above it. That is the first hint of the formula in Section 8b.

---

## 8. Space optimization

### 8a. One rolling row

While filling row `r`, the only cells read are in row `r − 1` (above) and in row `r` itself (left). Keep a single array `row` of length `n`:

- **Before** `row[c]` is overwritten it still holds `dp[r − 1][c]` — the value from above.
- `row[c − 1]` has **already** been overwritten in this pass, so it holds `dp[r][c − 1]` — the value from the left.

So one line does it:

```java
int[] row = new int[n];
Arrays.fill(row, 1);            // row 0 is all ones
for (int r = 1; r < m; r++)
    for (int c = 1; c < n; c++)
        row[c] += row[c - 1];   // new = old (above) + new left
return row[n - 1];
```

The direction **left to right** matters: if you went right to left, `row[c − 1]` would still hold the old (above-left) value and the result would be wrong.

Dry run on 3 × 3:

```
 start (row 0):            [1, 1, 1]
 r = 1: c=1: 1 + 1 = 2  -> [1, 2, 1]
        c=2: 1 + 2 = 3  -> [1, 2, 3]
 r = 2: c=1: 2 + 1 = 3  -> [1, 3, 3]
        c=2: 3 + 3 = 6  -> [1, 3, 6]
 answer = row[2] = 6
```

Full file: [`solution/Optimized.java`](solution/Optimized.java). (Tip: if `n > m`, you could transpose and keep a row of length `min(m, n)` — the answer is symmetric.)

### 8b. Aside: the combinatorics shortcut `C(m+n−2, m−1)`

Every route from `(0,0)` to `(m−1, n−1)` makes exactly `m − 1` down moves and `n − 1` right moves, `m + n − 2` moves in total. A route is fully described by **which** of those positions hold the down moves. So:

```
 number of routes = C(m + n - 2, m - 1)     ("m+n-2 choose m-1")
```

For 3 × 3: `C(4, 2) = 6` ✓. For 3 × 7: `C(8, 2) = 28` ✓.

Computing it naively with factorials overflows quickly (`20!` already exceeds a `long`). Instead build it up one factor at a time:

```java
int total = m + n - 2;
int k = Math.min(m - 1, n - 1);       // C(total, k) == C(total, total - k); use the smaller k
long result = 1;
for (int i = 1; i <= k; i++)
    result = result * (total - k + i) / i;   // after step i, result == C(total - k + i, i): exact
```

The division is always exact, because after step `i` the value is itself a binomial coefficient (an integer). With LeetCode's limits the result is at most about 2·10⁹ and every intermediate product stays far below `Long.MAX_VALUE`.

Full file: [`solution/Combinatorics.java`](solution/Combinatorics.java). This runs in `O(min(m, n))` time and `O(1)` space — but it only works because there are **no obstacles and no costs**. The moment the grid has walls (next topic), you are back to DP. Learn the DP; treat the formula as a bonus.

---

## 9. Dry run on a full example

Running example `m = 3, n = 3`, tabulation order, one row per cell computed:

| Step | Cell `(r,c)` | Above `dp[r−1][c]` | Left `dp[r][c−1]` | `dp[r][c]` | Rolling row after the step |
|------|--------------|--------------------|-------------------|-----------|----------------------------|
| 0 | base row/col | — | — | 1s | `[1, 1, 1]` |
| 1 | (1,1) | 1 | 1 | 2 | `[1, 2, 1]` |
| 2 | (1,2) | 1 | 2 | 3 | `[1, 2, 3]` |
| 3 | (2,1) | 2 | 1 | 3 | `[1, 3, 3]` |
| 4 | (2,2) | 3 | 3 | **6** | `[1, 3, 6]` |

Memoization computed the same four cells (in a different order: (1,1), (1,2), (2,1), (2,2)) and the formula gave `C(4,2) = 6`. All four approaches agree, and the solution files check this on five boards.

---

## 10. Complexity analysis

| Approach | Time | Space | Explanation |
|----------|------|-------|-------------|
| Brute force | `O(2^(m+n))` | `O(m + n)` | Each call makes two calls; the recursion depth is `m + n − 2`, which is also the stack size. |
| Memoization | `O(m · n)` | `O(m · n)` | `m · n` states, each computed once with O(1) work; the memo stores one int per state, plus an `O(m + n)` stack. |
| Tabulation | `O(m · n)` | `O(m · n)` | Two nested loops, O(1) per cell; the full table is stored. |
| One rolling row | `O(m · n)` | `O(n)` | Same loops; only one row is kept. |
| Combinatorics | `O(min(m, n))` | `O(1)` | One multiplication and one division per factor of the binomial. |

---

## 11. Why this approach works

Every route to `(r, c)` (other than the start) has a last move, and it is either down or right. Removing that last move leaves a route to `(r − 1, c)` or to `(r, c − 1)`, and every such shorter route extends to `(r, c)` in exactly one way. So the routes to `(r, c)` are in one-to-one correspondence with "routes to the cell above" plus "routes to the cell on the left", and adding the two counts counts each route exactly once. The base row and column are correct because there is only one way to stay on the border. Filling in row-major order guarantees both inputs are ready when needed.

---

## 12. Common mistakes

1. **Initialising only `dp[0][0] = 1` and starting the loops at 1.** Then the base row and column stay 0 and every answer is 0. Either fill row 0 and column 0 with 1s, or start the loops at 0 and guard the off-board neighbours.
2. **Swapping `m` and `n`.** `new int[n][m]` with `dp[r][c]` crashes on non-square boards. Test 3 × 7 *and* 7 × 3.
3. **Using 0 as "not computed" when 0 is a real answer.** Here it is safe (every count is ≥ 1), but in [Unique Paths II](../03-unique-paths-ii/) a cell can have 0 routes. Use −1 there.
4. **Updating the rolling row right to left.** `row[c − 1]` must already be the *new* value. Go left to right.
5. **Computing `C(m+n−2, m−1)` with factorials.** Overflows for small inputs. Multiply and divide step by step in a `long`.
6. **Returning `dp[m][n]`.** The last cell is `dp[m − 1][n − 1]` (unless you used a padded table).

---

## 13. When to use this technique / how to recognize similar problems

Signals: a grid, monotone moves (right/down, or down/down-left/down-right), and a question of the form "how many ways", "minimum cost" or "maximum gain" to reach a cell. The skeleton is always:

```
 for r in rows:
   for c in cols:
     dp[r][c] = combine(dp[r-1][c], dp[r][c-1]) + something about cell (r,c)
```

Change `combine` (`+` for counting, `min`/`max` for optimisation) and the cell term, and you have [Unique Paths II](../03-unique-paths-ii/), [Minimum Path Sum](../04-minimum-path-sum/) and many more.

---

## 14. Summary and revision notes

- State: `paths(r, c)` = number of routes from `(0,0)` to `(r,c)`.
- Choices: last move down or last move right → add the two counts.
- Base: row 0 and column 0 are all 1.
- Fill row by row, left to right. One rolling row of length `n` is enough.
- Shortcut without obstacles: `C(m+n−2, m−1)`, computed step by step in a `long`.

```
+--------------------------------------------------------------+
|  paths(r, c) = paths(r-1, c) + paths(r, c-1)                 |
|  paths(0, c) = paths(r, 0) = 1                               |
|  answer = paths(m-1, n-1) = C(m+n-2, m-1)                    |
|  rolling row:  row[c] += row[c-1]   (left to right)          |
+--------------------------------------------------------------+
```

---

## Exercises

**Exercise 1.** Fill the full table for `m = 3, n = 7` and confirm the answer 28.

<details>
<summary>Answer</summary>

```
 1  1  1  1  1  1  1
 1  2  3  4  5  6  7
 1  3  6 10 15 21 28
```
Row 1 is `1..7` because each cell adds 1 from above to its left neighbour. Row 2 is the running sum of row 1.
</details>

**Exercise 2.** Use the formula to compute the number of routes for `m = 4, n = 5` without a table. Then check one value of the table.

<details>
<summary>Answer</summary>

`C(4 + 5 − 2, 4 − 1) = C(7, 3) = 35`. Step by step with `k = 3`, `total = 7`: `1·5/1 = 5`, `5·6/2 = 15`, `15·7/3 = 35`. The last row of the 4 × 5 table is `1 4 10 20 35`.
</details>

**Exercise 3.** A friend's rolling-row code loops `for (int c = n - 1; c >= 1; c--) row[c] += row[c - 1];`. What does it return for `m = 3, n = 3`, and why is it wrong?

<details>
<summary>Answer</summary>

Start `[1,1,1]`. `r = 1`: `c=2`: `1+1 = 2`, `c=1`: `1+1 = 2` → `[1,2,2]`. `r = 2`: `c=2`: `2+2 = 4`, `c=1`: `2+1 = 3` → `[1,3,4]`. It returns 4 instead of 6. Going right to left, `row[c − 1]` still holds the value from the row *above* (the up-left diagonal), not the new left neighbour, so it computes a different recurrence.
</details>

**Exercise 4.** Without coding: how many routes pass through the centre cell `(1,1)` of the 3 × 3 board? Use the table from this walkthrough and the symmetry of the board.

<details>
<summary>Answer</summary>

Routes from the start to `(1,1)`: `dp[1][1] = 2`. Routes from `(1,1)` to the end: by symmetry also 2 (it is a 2 × 2 board). Through the centre: `2 × 2 = 4`. The other two routes (`RRDD`, `DDRR`) go around the edge.
</details>

---

⬅ [README](README.md) · ➡ Next topic: [Unique Paths II](../03-unique-paths-ii/)
