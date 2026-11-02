# Unique Paths II — Walkthrough

## 1. Intuition (no code, no math — an everyday analogy)

Go back to the delivery rider from [Unique Paths](../02-unique-paths/), riding only east or south through a grid of streets. Today some intersections are closed for road works. You may not ride through a closed intersection.

The rule for an open intersection is the same as before: routes to here = routes to the intersection north of me + routes to the intersection west of me. For a closed intersection the answer is simply **zero**: no route may end there, so no route can continue *from* there either.

Now picture road works on the top street, two blocks east of the warehouse. Every intersection *further east* on that top street can only be reached by riding along the top street — through the closed one. So all of them are unreachable too. Yesterday you could write "1" on every top-street intersection without thinking. Today that habit gives wrong answers. That habit is the trap this topic is about.

---

## 2. Problem statement in our own words

Original: [LeetCode 63 — Unique Paths II](https://leetcode.com/problems/unique-paths-ii/) (Medium).

You get an `m × n` grid of 0s and 1s. `0` is a free cell, `1` is an obstacle. A robot starts at the top-left cell and moves only right or down, never entering an obstacle. Return the number of distinct routes to the bottom-right cell. If the start or the end is an obstacle, the answer is 0. (The answer fits in a 32-bit signed integer.)

---

## 3. Examples

Running example (used in every section):

```
 grid =  0 0 0          S . .
         0 1 0    →     . # .
         0 0 0          . . E        answer = 2
```

Without the obstacle there would be 6 routes (see [Unique Paths](../02-unique-paths/)). Four of them pass through the centre, so `6 − 4 = 2` remain: `R R D D` (along the top and down the right side) and `D D R R` (down the left side and along the bottom).

More examples:

| Grid | Answer | Why |
|------|--------|-----|
| `[[0,1],[0,0]]` | 1 | Only `D R`; the cell to the right of the start is blocked. |
| `[[1]]` | 0 | The start is an obstacle. |
| `[[0,0],[0,1]]` | 0 | The end is an obstacle. |
| `[[0]]` | 1 | Start = end, free. |
| `[[0,1,0],[0,0,0]]` | 1 | **The trap input** — see Section 4, Q5. |

---

## 4. Deriving the solution — the 7 questions

### Q1. What is changing?

The robot's row `r` and column `c`, exactly as in Unique Paths. The grid itself never changes. Two changing quantities → 2D state.

### Q2. What does the state represent?

> `paths(r, c)` = the number of right/down routes from `(0, 0)` to `(r, c)` that never enter an obstacle (including `(r, c)` itself).

The answer is `paths(m − 1, n − 1)`.

### Q3. What are the choices?

If `(r, c)` is an obstacle: **no choices** — nothing can end here.
Otherwise, the last move was down from `(r − 1, c)` or right from `(r, c − 1)`.

### Q4. What is the recurrence?

```
                 | 0                                   if grid[r][c] == 1   (blocked)
 paths(r, c) =   |
                 | paths(r-1, c) + paths(r, c-1)       otherwise
```

Notice we never need to ask "is the cell *above* an obstacle?". If it is, `paths` of that cell is already 0, and adding 0 changes nothing. **Obstacles are handled entirely by giving blocked cells the value 0.**

### Q5. What are the base cases?

- **Off the board** (`r < 0` or `c < 0`): 0 routes.
- **The start** `(0, 0)`: 1 if it is free, 0 if it is an obstacle.

And here is the trap. In Unique Paths we used "row 0 and column 0 are all 1" as base cases. With obstacles that is no longer true. A cell on the top row can only be reached from its left neighbour, so:

```
 paths(0, c) = 0                    if grid[0][c] == 1
             = paths(0, c-1)        otherwise
```

One obstacle on the top row makes **every** cell to its right 0. The same holds for column 0 going down.

#### The trap, on a real input

Grid `[[0,1,0],[0,0,0]]`:

```
 S # .
 . . E
```

A naive "fix" sets the top row to 1 except for obstacle cells:

```
 NAIVE (wrong)              CORRECT
 row 0:  1  0  1            row 0:  1  0  0     <- (0,2) is behind the obstacle
 row 1:  1  1  2            row 1:  1  1  1
 answer: 2  ✗               answer: 1  ✓
```

The naive table claims a route reaches `(0,2)` along the top row — impossible, it would have to cross `(0,1)`. The only real route is `D R R`.

The safest way to avoid this trap is to **not special-case the border at all**: compute every cell, including row 0 and column 0, with the general recurrence plus "off-board = 0". Then the border handles itself. That is what all the solution files do.

### Q6. What is the order of computation?

Same arrows as Unique Paths (from above and from the left), so row by row, left to right.

### Q7. Can the state space be optimized?

Yes. Row `r` reads only row `r − 1` and itself, so one array of length `n` suffices. The obstacle rule becomes "if blocked, set `row[c] = 0`" (Section 8).

---

## 5. Brute force: recursion

```java
// routes from (0,0) to (r,c) that avoid obstacles
static int countPaths(int[][] grid, int r, int c) {
    if (r < 0 || c < 0) return 0;          // off the board
    if (grid[r][c] == 1) return 0;         // blocked: no route can end here
    if (r == 0 && c == 0) return 1;        // the (free) start
    return countPaths(grid, r - 1, c) + countPaths(grid, r, c - 1);
}
```

Full file: [`solution/BruteForce.java`](solution/BruteForce.java).

The order of the checks matters. The obstacle check comes **before** the start check, so a blocked start correctly returns 0. Notice that this recursion has **no** base-row trap: it never assumes anything about row 0; it just keeps going until it falls off the board.

### Recursion tree on the running example

`#` marks a call that hits the obstacle `(1,1)`; `off` marks an off-board call.

```
                          paths(2,2)
                 /                          \
           paths(1,2)                    paths(2,1)
          /          \                  /          \
     paths(0,2)   paths(1,1)#      paths(1,1)#   paths(2,0)
     /       \        0                 0        /        \
  off      paths(0,1)                       paths(1,0)    off
   0       /       \                        /       \      0
         off     paths(0,0)            paths(0,0)   off
          0          1                     1         0

 total = 1 + 1 = 2   ✓
```

Without memoization, the work is still exponential in the worst case (an empty grid behaves exactly like Unique Paths): `O(2^(m+n))`.

---

## 6. Memoization (top-down)

In Unique Paths we used `0` to mean "not computed yet", because every real count was at least 1. **Here 0 is a real answer** (for blocked or cut-off cells). If we kept 0 as the marker, every cell whose answer is 0 would be recomputed every time it is asked for — still correct, but no longer `O(m·n)`. So we fill the memo with `-1` instead.

```java
// memo[r][c] = routes from (0,0) to (r,c); -1 = not computed yet (0 is a real answer here)
static int countPaths(int[][] grid, int r, int c, int[][] memo) {
    if (r < 0 || c < 0) return 0;
    if (grid[r][c] == 1) return 0;
    if (r == 0 && c == 0) return 1;
    if (memo[r][c] != -1) return memo[r][c];
    memo[r][c] = countPaths(grid, r - 1, c, memo) + countPaths(grid, r, c - 1, memo);
    return memo[r][c];
}
```

Full file: [`solution/Memoization.java`](solution/Memoization.java).

### Dry run (running example)

```
 paths(2,2)
  paths(1,2)
    paths(0,2)
      paths(0,1)
        paths(0,0) = 1           memo[0][1] = 0 + 1 = 1
      memo[0][2] = 0 + 1 = 1
    paths(1,1) -> obstacle -> 0 (no memo needed)
  memo[1][2] = 1 + 0 = 1
  paths(2,1)
    paths(1,1) -> obstacle -> 0
    paths(2,0)
      paths(1,0)
        paths(0,0) = 1           memo[1][0] = 1 + 0 = 1
      memo[2][0] = 1 + 0 = 1
  memo[2][1] = 0 + 1 = 1
 memo[2][2] = 1 + 1 = 2

 final memo (·· = never stored):    ··  1  1
                                     1  ··  1
                                     1  1   2
```

---

## 7. Tabulation (bottom-up)

Compute every cell with the general rule. The guards `r > 0` and `c > 0` play the role of "off-board = 0", and the base row and column are computed by the same code as everything else — no trap possible.

```java
// dp[r][c] = number of obstacle-free routes from (0,0) to (r,c)
int[][] dp = new int[m][n];
for (int r = 0; r < m; r++) {
    for (int c = 0; c < n; c++) {
        if (grid[r][c] == 1) { dp[r][c] = 0; continue; }     // blocked
        if (r == 0 && c == 0) { dp[r][c] = 1; continue; }    // free start
        int fromAbove = (r > 0) ? dp[r - 1][c] : 0;
        int fromLeft  = (c > 0) ? dp[r][c - 1] : 0;
        dp[r][c] = fromAbove + fromLeft;
    }
}
return dp[m - 1][n - 1];
```

Full file: [`solution/Tabulation.java`](solution/Tabulation.java).

### Filling the running example

```
 (0,0) start   (0,1) 0+1     (0,2) 0+1     (1,0) 1+0
  1 . .         1 1 .         1 1 1         1 1 1
  . # .         . # .         . # .         1 # .
  . . .         . . .         . . .         . . .

 (1,1) BLOCKED (1,2) 1+0     (2,0) 1+0     (2,1) 0+1     (2,2) 1+1
  1 1 1         1 1 1         1 1 1         1 1 1         1 1 1
  1 0 .         1 0 1         1 0 1         1 0 1         1 0 1
  . . .         . . .         1 . .         1 1 .         1 1 2   <- answer
```

Cell `(1,2)` = 1 (from above) + 0 (left is the obstacle). Cell `(2,1)` = 0 (above is the obstacle) + 1. The obstacle's 0 flows into its neighbours automatically.

---

## 8. Space optimization

One rolling row `row[c]`, updated left to right. Before the update, `row[c]` holds the value from the row above; `row[c − 1]` already holds the new left value.

```java
int[] row = new int[n];               // row[c] = routes to (r, c) for the current row r
row[0] = (grid[0][0] == 0) ? 1 : 0;   // seed: the start
for (int r = 0; r < m; r++) {
    for (int c = 0; c < n; c++) {
        if (grid[r][c] == 1) row[c] = 0;          // blocked: wipe the value inherited from above
        else if (c > 0) row[c] += row[c - 1];     // from above (old row[c]) + from the left
        // c == 0 and free: row[0] keeps the value from above (only one way in)
    }
}
return row[n - 1];
```

Full file: [`solution/Optimized.java`](solution/Optimized.java).

The row starts as `[1, 0, 0]` — "only the start is reachable so far" — and row 0 is then processed by the same loop as all other rows. The obstacle line `row[c] = 0` is essential: without it, a blocked cell would keep the value it inherited from above.

Dry run on the running example:

```
 seed:              [1, 0, 0]
 r = 0: c=0 free, keep 1        [1, 0, 0]
        c=1 free, 0 + 1 = 1     [1, 1, 0]
        c=2 free, 0 + 1 = 1     [1, 1, 1]
 r = 1: c=0 free, keep 1        [1, 1, 1]
        c=1 BLOCKED -> 0        [1, 0, 1]
        c=2 free, 1 + 0 = 1     [1, 0, 1]
 r = 2: c=0 free, keep 1        [1, 0, 1]
        c=1 free, 0 + 1 = 1     [1, 1, 1]
        c=2 free, 1 + 1 = 2     [1, 1, 2]
 answer = row[2] = 2
```

And on the trap input `[[0,1,0],[0,0,0]]`:

```
 seed [1,0,0];  r=0: [1,0,0] (c=1 blocked, c=2: 0 + 0 = 0);  r=1: [1,1,1]  -> 1 ✓
```

---

## 9. Dry run on a full example

A bigger grid to practise on (also tested in every solution file):

```
 grid:            dp table:
 0 0 0 0          1 1 1 1
 0 1 0 0          1 0 1 2
 0 0 0 1          1 1 2 0
 0 0 0 0          1 2 4 4     <- answer 4
```

| Cell | Blocked? | Above | Left | `dp` |
|------|----------|-------|------|------|
| (1,1) | yes | — | — | 0 |
| (1,2) | no | 1 | 0 | 1 |
| (1,3) | no | 1 | 1 | 2 |
| (2,1) | no | 0 | 1 | 1 |
| (2,2) | no | 1 | 1 | 2 |
| (2,3) | yes | — | — | 0 |
| (3,1) | no | 1 | 1 | 2 |
| (3,2) | no | 2 | 2 | 4 |
| (3,3) | no | 0 | 4 | **4** |

(Rows 0 and column 0 are all 1 here only because no obstacle sits on them.)

---

## 10. Complexity analysis

| Approach | Time | Space | Explanation |
|----------|------|-------|-------------|
| Brute force | `O(2^(m+n))` | `O(m + n)` | Two calls per free cell, depth `m + n − 2`. |
| Memoization | `O(m · n)` | `O(m · n)` | Each cell is solved once with O(1) work. Using −1 as the sentinel keeps cells whose answer is 0 from being recomputed. |
| Tabulation | `O(m · n)` | `O(m · n)` | One pass over the grid. |
| One rolling row | `O(m · n)` | `O(n)` | Same pass, one row of storage. |

There is no `O(1)`-space formula this time: obstacles break the binomial-coefficient shortcut.

---

## 11. Why this approach works

A route that ends on a blocked cell does not exist, so 0 is the correct count there. For a free cell, every route has a last move from above or from the left, and the routes counted through a blocked neighbour are 0, which is exactly right: no legal route passes through it. Because the border cells are computed with the same rule (with off-board neighbours contributing 0), a single obstacle on the border correctly zeros every cell that could only be reached through it. So every cell's value is exactly the number of legal routes to it.

---

## 12. Common mistakes

1. **The base-row / base-column trap.** Setting row 0 (or column 0) to all 1s, or to "1 unless this cell is blocked". Cells *after* an obstacle on the border must be 0. Compute the border with the same recurrence.
2. **Ignoring a blocked start or end.** `[[1]]` must return 0; `[[0,0],[0,1]]` must return 0.
3. **Checking "is the start" before "is blocked".** Then a blocked start returns 1.
4. **Using 0 as the memo's "not computed" value.** Correct but slow on grids full of unreachable cells; use −1.
5. **Forgetting `row[c] = 0` for an obstacle in the rolling row.** The cell would keep the count from the row above.
6. **Reusing the combinatorics formula.** It does not account for obstacles.

---

## 13. When to use this technique / how to recognize similar problems

Use it for any grid count with forbidden cells. More generally, whenever some states are **impossible**, give them the neutral value of your combine operation so they disappear from the result automatically:

| Combine | Neutral value for "impossible" |
|---------|-------------------------------|
| `+` (counting) | `0` |
| `min` (cheapest) | `+∞` (a very large number) |
| `max` (best gain) | `−∞` (a very negative number) |

You will use the `−∞` row of this table in [Cherry Pickup](../09-cherry-pickup/).

---

## 14. Summary and revision notes

- Blocked cell → 0 routes. Everything else is the Unique Paths recurrence.
- Do **not** set the border to 1s. Compute it with the same rule, using 0 for off-board neighbours.
- Check "blocked" before "is the start".
- Use −1 as the memo sentinel when 0 is a valid answer.
- Rolling row: seed `row[0]` from the start cell, then `row[c] = blocked ? 0 : row[c] + row[c−1]`.

```
+---------------------------------------------------------------+
|  paths(r, c) = 0                              if blocked      |
|              = paths(r-1, c) + paths(r, c-1)  otherwise       |
|  paths(0, 0) = 1 if free,  paths(off-board) = 0               |
+---------------------------------------------------------------+
```

---

## Exercises

**Exercise 1.** Compute the answer for this grid by filling the table:

```
 0 0 0
 1 1 0
 0 0 0
```

<details>
<summary>Answer</summary>

```
 1 1 1
 0 0 1
 0 0 1
```
Answer 1. Column 0 below the obstacle at `(1,0)` is cut off, so `(2,0)` is 0, and `(2,1)` gets 0 from above (obstacle) and 0 from the left. The only route is `R R D D`.
</details>

**Exercise 2.** Run the naive "top row = 1 unless blocked, left column = 1 unless blocked" code on `[[0,0],[1,0],[0,0]]`. What does it return, and what is correct?

<details>
<summary>Answer</summary>

Naive column 0: `1, 0, 1` (it puts 1 at `(2,0)` even though it is cut off). Table: row 1 = `0, 1` (`(1,1)` = 1 + 0); row 2 = `1, 2` → returns 2. Correct column 0 is `1, 0, 0`, row 2 = `0, 1` → answer **1** (the only route is `R D D`).
</details>

**Exercise 3.** Why does the rolling-row version not need a special loop for row 0?

<details>
<summary>Answer</summary>

The row is seeded as `[1, 0, 0, …]`, which means "the start is reachable and nothing else is yet". Processing row 0 with the normal loop then computes `row[c] = 0 + row[c−1]` for free cells (the "above" part is 0 because there is no row above) and 0 for blocked cells — exactly the correct top row, including the cells behind an obstacle.
</details>

---

⬅ [README](README.md) · ➡ Next topic: [Minimum Path Sum](../04-minimum-path-sum/)
