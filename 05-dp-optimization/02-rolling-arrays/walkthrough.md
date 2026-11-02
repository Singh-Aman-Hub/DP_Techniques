# Rolling Arrays — Walkthrough

Running example: **Cherry Pickup II** (a 3D DP), rolled down to two 2D layers. Case study for the most common rolling-array bug: **Out of Boundary Paths** (section 8.4).

---

## 1. Intuition

Think of two whiteboards standing side by side. You write today's plan on board A while reading yesterday's plan from board B. Tomorrow you erase B and write tomorrow's plan on it, reading from A. You never need a third board, because each day's plan depends only on the day before. Which board is "today" alternates: even days on A, odd days on B. That is `dp[day % 2]`.

There is one trap: when you start writing on a board, it still shows the plan from **two days ago**. If you only *add* notes to it instead of rewriting it, the old notes mix in. You must wipe it first. That is the reset pitfall.

---

## 2. Problem statement in our own words

[Cherry Pickup II](https://leetcode.com/problems/cherry-pickup-ii/) (LeetCode 1463): a grid holds cherry counts. Two robots start in row 0, one in the leftmost column and one in the rightmost column. In every step each robot moves down one row and to the column left, the same column, or right (staying inside the grid). Each robot picks the cherries of every cell it visits; if both robots stand on the same cell, the cherries are counted only once. Return the largest total the two robots can collect when they reach the bottom row.

---

## 3. Examples

Running example (used everywhere below):

```
grid =  3 1 1        robot 1 starts at (0,0), robot 2 at (0,2)
        2 5 1
        1 5 5
        2 1 1
```

Output: **24**. Robot 1 takes `3 → 2 → 5 → 2` = 12 (columns 0, 0, 1, 0); robot 2 takes `1 → 5 → 5 → 1` = 12 (columns 2, 1, 2, 2). The two paths never share a cell, so the total is 24.

Edge cases: a single row `[[4, 7]]` gives 11 (both start cells, no moves); a single column `[[5], [6]]` gives 11 (both robots are always on the same cell, counted once).

---

## 4. Deriving the solution — the 7 questions

### 4.1 What is changing?

The current row `r` (both robots are always in the same row, because each step moves both down by one) and the two columns `c1`, `c2`.

### 4.2 What does the state represent?

`dp[r][c1][c2]` = the largest number of cherries the robots can still collect from row `r` down to the last row, given that robot 1 stands at `(r, c1)` and robot 2 at `(r, c2)` (row `r`'s cherries included).

### 4.3 What are the choices?

Each robot moves by `d1, d2 ∈ {-1, 0, +1}`: 3 × 3 = 9 move pairs, minus those that leave the grid.

### 4.4 What is the recurrence?

```
dp[r][c1][c2] = here(r, c1, c2) + max over d1, d2 of dp[r+1][c1+d1][c2+d2]

here(r, c1, c2) = grid[r][c1]                    if c1 == c2   (shared cell counts once)
                = grid[r][c1] + grid[r][c2]      otherwise
```

### 4.5 What are the base cases?

The last row: `dp[R-1][c1][c2] = here(R-1, c1, c2)`. There is nothing below to collect.

### 4.6 What is the order of computation?

Layer `r` reads only layer `r + 1`, so compute rows from the bottom (`R-1`) up to 0. Inside a layer, any order of `(c1, c2)` works, because cells of the same layer never read each other. The answer is `dp[0][0][C-1]`.

### 4.7 Can the state space be optimized?

Yes. The footprint of layer `r` is **only layer `r + 1`**. Layer `r + 2` and everything below it are dead once layer `r + 1` exists. Two layers are enough. Sections 4.8 and 4.9 explain why one layer would *not* be enough, and how the two layers are indexed.

### 4.8 Prerequisite: parity indexing with `i % 2` and `i & 1`

We want two physical slots, 0 and 1, and we want consecutive layers to land in *different* slots. The remainder `i % 2` does exactly that:

```
logical layer i :  0  1  2  3  4  5 ...
slot i % 2      :  0  1  0  1  0  1 ...
```

Layers `i` and `i - 1` always have different parity, so they never collide, and layer `i` reuses the slot of layer `i - 2`, which is dead.

`i & 1` gives the same result for non-negative `i`. In binary, `i & 1` keeps only the last bit, and the last bit of a number *is* its remainder modulo 2: `6 = 110₂ → 0`, `7 = 111₂ → 1`. It is a habit from competitive programming; `% 2` is equally fast in practice and clearer, so use whichever you read more easily. (Careful: for negative `i`, Java's `i % 2` can be `-1`; `i & 1` is still 0 or 1. Our indices are never negative.)

Generalisation: if layer `i` reads layers `i-1, …, i-k`, then `k + 1` layers are live at once and `i % (k + 1)` picks the slot.

### 4.9 The key property and why it holds: why two layers and not one

In [Space optimization](../01-space-optimization/) we packed a 2D DP into **one** row by choosing a sweep direction so that every read sees the right version (old or new). That works only if every needed *old* value lies on the not-yet-visited side of the sweep.

Look at the footprint of Cherry Pickup II, shown for the simpler one-robot version (one column index):

```
layer r+1:   . R R R .        cell c reads c-1, c, c+1 of the old layer
layer r  :   . . X . .
```

Sweep left to right in one array: when we compute `c`, slot `c - 1` was already overwritten with layer `r` → wrong. Sweep right to left: slot `c + 1` was already overwritten → wrong. **No direction works**, because the footprint needs old values on *both* sides. (A single saved `prev` variable rescues this 1D case, but with two robots the footprint is a 3 × 3 block in a 2D slice and the bookkeeping becomes unreadable.)

With two layers, the old layer is **read-only** for the whole computation of the new one, so every read is automatically correct, whatever the order. That is the property: *if layer `r` reads only layer `r + 1`, and we never write to the slot holding layer `r + 1` while computing layer `r`, then every read returns exactly what the full table would return.*

Small numeric check with our grid. The full table's layer 3 (last row `2 1 1`) is:

```
dp[3][c1][c2]   c2=0 c2=1 c2=2
      c1=0        2    3    3
      c1=1        3    1    2
      c1=2        3    2    1
```

(`dp[3][0][1] = 2 + 1 = 3`; `dp[3][1][1] = 1` because the shared cell counts once.) Layer 3 goes into slot `3 % 2 = 1`. Layer 2 goes into slot 0 and reads slot 1. Layer 1 goes into slot 1 — overwriting layer 3, which is dead because only layer 2 reads it.

---

## 5. Brute force: recursion

```java
static int solve(int[][] grid, int r, int c1, int c2) {
    int cols = grid[0].length;
    if (c1 < 0 || c1 >= cols || c2 < 0 || c2 >= cols) return Integer.MIN_VALUE; // fell off
    int here = (c1 == c2) ? grid[r][c1] : grid[r][c1] + grid[r][c2];
    if (r == grid.length - 1) return here;
    int best = Integer.MIN_VALUE;
    for (int d1 = -1; d1 <= 1; d1++)
        for (int d2 = -1; d2 <= 1; d2++)
            best = Math.max(best, solve(grid, r + 1, c1 + d1, c2 + d2));
    return here + best;
}
```

Recursion tree (first level only — it already has 9 children):

```
                          (r=0, 0, 2)
   /     /     /     /     |     \     \     \     \
(1,-1,1)(1,-1,2)(1,-1,3)(1,0,1)(1,0,2)(1,0,3)(1,1,1)(1,1,2)(1,1,3)
   x       x       x     ok     ok     x     ok     ok     x      (x = outside the grid)
```

Up to 9 children per call and `R` levels → about `9^R` calls. For `R = 70` that is astronomically large, yet there are only `R · C · C` distinct states: heavy overlap.

---

## 6. Memoization (top-down)

Cache `memo[r][c1][c2]`. Every state is computed once with 9 reads: `O(R·C²)` time, `O(R·C²)` memory plus recursion depth `R`. As in the previous topic, memoization cannot drop layers because we do not control the visiting order, so we move to tabulation.

---

## 7. Tabulation (bottom-up)

```java
int[][][] dp = new int[rows][cols][cols];
for (int r = rows - 1; r >= 0; r--)
    for (int c1 = 0; c1 < cols; c1++)
        for (int c2 = 0; c2 < cols; c2++) {
            int bestBelow = 0;
            if (r + 1 < rows) {
                bestBelow = Integer.MIN_VALUE;
                for (int d1 = -1; d1 <= 1; d1++)
                    for (int d2 = -1; d2 <= 1; d2++) {
                        int n1 = c1 + d1, n2 = c2 + d2;
                        if (n1 < 0 || n1 >= cols || n2 < 0 || n2 >= cols) continue;
                        bestBelow = Math.max(bestBelow, dp[r + 1][n1][n2]);
                    }
            }
            dp[r][c1][c2] = here(r, c1, c2) + bestBelow;
        }
return dp[0][0][cols - 1];
```

All four layers for our grid (rows `c1`, columns `c2`):

```
layer 3 (row 2 1 1)    layer 2 (row 1 5 5)    layer 1 (row 2 5 1)    layer 0 (row 3 1 1)
   2   3   3              4   9   9             11  20  16             23  24  24
   3   1   2              9   8  13             20  18  19             24  21  22
   3   2   1              9  13   7             16  19  14             24  22  20
```

Check two cells by hand:

- `dp[2][0][2]`: here = 1 + 5 = 6. Robot 1 can go to columns {0, 1}, robot 2 to {1, 2}. Candidates in layer 3: `dp[3][0][1]=3, dp[3][0][2]=3, dp[3][1][1]=1, dp[3][1][2]=2` → best 3. Total **9** ✓.
- `dp[0][0][2]`: here = 3 + 1 = 4. Candidates in layer 1: `dp[1][0][1]=20, dp[1][0][2]=16, dp[1][1][1]=18, dp[1][1][2]=19` → best 20. Total **24** ✓ — the answer.

Only one previous layer is ever read. Time to roll.

---

## 8. Optimization: rolling arrays

### 8.1 The code

Replace the first index `r` by `r % 2` everywhere, and keep two named references for readability:

```java
int[][][] layer = new int[2][cols][cols];
for (int r = rows - 1; r >= 0; r--) {
    int[][] current = layer[r % 2];          // being written: dp[r]
    int[][] below   = layer[(r + 1) % 2];    // read-only:     dp[r + 1]
    for (int c1 = 0; c1 < cols; c1++)
        for (int c2 = 0; c2 < cols; c2++) {
            int bestBelow = 0;
            if (r + 1 < rows) {
                bestBelow = Integer.MIN_VALUE;
                for (int d1 = -1; d1 <= 1; d1++)
                    for (int d2 = -1; d2 <= 1; d2++) {
                        int n1 = c1 + d1, n2 = c2 + d2;
                        if (n1 < 0 || n1 >= cols || n2 < 0 || n2 >= cols) continue;
                        bestBelow = Math.max(bestBelow, below[n1][n2]);
                    }
            }
            current[c1][c2] = here(r, c1, c2) + bestBelow;   // every cell overwritten
        }
}
return layer[0][0][cols - 1];   // row 0 lives in slot 0 % 2 = 0
```

### 8.2 Which slot holds what, step by step

| Step | computing layer | writes slot `r % 2` | reads slot `(r+1) % 2` | slot 0 contains | slot 1 contains |
|------|-----------------|---------------------|------------------------|-----------------|-----------------|
| 1 | 3 (base) | 1 | — | garbage (zeros) | layer 3 |
| 2 | 2 | 0 | 1 | layer 2 | layer 3 |
| 3 | 1 | 1 | 0 | layer 2 | layer 1 (layer 3 dead, overwritten) |
| 4 | 0 | 0 | 1 | layer 0 → answer `[0][2] = 24` | layer 1 |

### 8.3 Do we need to reset the reused layer?

Here: **no**, and it is worth understanding why. This is a *pull-style* DP: every cell `current[c1][c2]` is assigned exactly once with `=`, using only values from `below`. Whatever stale numbers the slot held before are overwritten without ever being read.

The danger appears in *push-style* DPs, where old cells **add** into new cells (`next[x] += current[y]`), or in pull-style code that uses `current[x] = max(current[x], …)` and relies on an initial value. Then the stale content of the reused slot leaks into the result. Section 8.4 shows that bug on a real problem.

> **Reset rule.** Before filling a reused layer, ask: "Does my code *read* a cell of this layer before assigning it?" (`+=`, `max(cell, …)`, `min(cell, …)`, `if (cell == 0)`…). If yes, reset the layer to the neutral value first: 0 for counts and sums, −∞ for maxima, +∞ for minima, `false` for reachability.

### 8.4 Case study: the reset pitfall in Out of Boundary Paths (LC 576)

[Out of Boundary Paths](https://leetcode.com/problems/out-of-boundary-paths/): a ball starts on cell `(startRow, startCol)` of an `m × n` grid and may make at most `maxMove` moves up/down/left/right. Count the move sequences that push the ball out of the grid (modulo 10⁹ + 7).

State: `ways[s][r][c]` = number of move sequences of length `s` that keep the ball inside and end on `(r, c)`. It is natural to **push**: every cell sends its count to its four neighbours, and a neighbour outside the grid adds to the answer instead. Layer `s + 1` reads only layer `s` → two layers.

```java
for (int s = 0; s < maxMove; s++) {
    long[][] current = layer[s & 1];
    long[][] next = layer[(s + 1) & 1];
    for (long[] row : next) Arrays.fill(row, 0);    // <- THE line people forget
    for (int r = 0; r < m; r++)
        for (int c = 0; c < n; c++) {
            if (current[r][c] == 0) continue;
            for (int[] d : DIRECTIONS) {
                int nr = r + d[0], nc = c + d[1];
                if (nr < 0 || nr >= m || nc < 0 || nc >= n) answer = (answer + current[r][c]) % MOD;
                else next[nr][nc] = (next[nr][nc] + current[r][c]) % MOD;
            }
        }
}
```

Dry run with `m = 1, n = 3, maxMove = 3`, start at column 1 (a 1 × 3 strip; up and down always leave the grid). Correct answer: 12.

```
                     CORRECT (reset)                         BUG (no reset)
s=0: current=[0,1,0]  -> out +2,  next=[1,0,1]    current=[0,1,0] -> out +2, next(slot1)=[1,0,1]
     answer = 2                                    answer = 2
s=1: current=[1,0,1]  -> out +3+3, next=[0,2,0]   current=[1,0,1] -> out +6,
     answer = 8                                    next = slot 0 = STILL [0,1,0] from s=0!
                                                   adds 1+1 to middle -> [0,3,0]
                                                   answer = 8
s=2: current=[0,2,0]  -> out +2+2                 current=[0,3,0] -> out +3+3
     answer = 12  ✓                                answer = 14  ✗
```

The bug added a ghost ball: the count from step 0 was never wiped, so it was counted again at step 2. [OutOfBoundaryPaths.java](solution/OutOfBoundaryPaths.java) contains both versions and asserts that the buggy one returns 14.

### 8.5 Alternative style: swapping references

Instead of `% 2`, keep two named arrays and swap them after each layer:

```java
int[][] current = new int[cols][cols], next = new int[cols][cols];
for (...) {
    // fill next from current (reset next first if you push or accumulate!)
    int[][] tmp = current; current = next; next = tmp;   // O(1): swaps references, copies nothing
}
```

Both styles are equivalent. `% 2` makes "which layer is this?" explicit; swapping avoids index arithmetic. Never *copy* a layer with a loop (`current[x][y] = next[x][y]`): it works but costs an extra O(C²) per layer for nothing.

---

## 9. Dry run on a full example

Slot contents of the rolling version on the running grid, after each outer iteration (each slot shown as a 3 × 3 block, rows `c1`, columns `c2`):

```
after r=3 (write slot 1)      after r=2 (write slot 0)      after r=1 (write slot 1)      after r=0 (write slot 0)
slot0     slot1               slot0     slot1               slot0     slot1               slot0     slot1
0 0 0     2 3 3               4  9  9   2 3 3               4  9  9   11 20 16            23 24 24  11 20 16
0 0 0     3 1 2               9  8 13   3 1 2               9  8 13   20 18 19            24 21 22  20 18 19
0 0 0     3 2 1               9 13  7   3 2 1               9 13  7   16 19 14            24 22 20  16 19 14
```

Answer = slot 0, `c1 = 0`, `c2 = 2` → **24**. Every slot content equals the corresponding layer of the full table in section 7.

---

## 10. Complexity analysis

| Approach | Time | Space | Explanation |
|----------|------|-------|-------------|
| Brute force | O(9^R) | O(R) stack | up to 9 branches per row |
| Memoization | O(R·C²·9) = O(R·C²) | O(R·C²) + O(R) stack | each state once, 9 reads each |
| Tabulation | O(R·C²) | O(R·C²) | same work, no recursion |
| Rolling | O(R·C²) | O(C²) (2 layers) | same work; only layers r and r+1 stored |

For the LeetCode limits (R, C ≤ 70) the full table has 70·70·70 = 343 000 ints (≈1.4 MB, fine), and the rolling version 2·70·70 = 9 800 ints. The saving matters when R is large: with R = 10⁵ the full table would need 490 million ints (≈2 GB) while the rolling one still needs 9 800.

Out of Boundary Paths: O(maxMove·m·n) time for both versions; space O(maxMove·m·n) → O(m·n).

---

## 11. Why this approach works

The rolling version performs the same assignments in the same order as the full table. The only question is whether every read returns the same value. A read of `below[n1][n2]` while computing layer `r` reads slot `(r+1) % 2`. The last write to that slot was layer `r + 1` (the previous outer iteration), and nothing writes to it during layer `r`, because layer `r` writes only slot `r % 2 ≠ (r+1) % 2`. So the read returns `dp[r+1][n1][n2]`, exactly as in the full table. When the code also reads the *written* slot before assigning it (push style, accumulation), the stale contents from layer `r + 2` would be read — which is why a reset to the neutral value is required in that case.

---

## 12. Common mistakes

1. **No reset in push or accumulate style.** Ghost counts from two layers ago (14 instead of 12 above).
2. **Resetting with the wrong neutral value.** For a `max` DP with unreachable states, reset to −∞ (or a sentinel), not 0 — 0 may be a reachable, real value.
3. **Reading the answer from `dp[n]` instead of `dp[n % 2]`.** With bottom-up rows (`r` from R−1 down to 0) the answer is in slot `0 % 2`; with forward steps (`s` from 0 to maxMove) the final layer is in slot `maxMove % 2`.
4. **Mixing up `% 2` slots inside the loop.** Bind `current` and `below`/`next` to local variables at the top of the outer loop and only use those names inside.
5. **Using `i % 2` with negative indices.** In Java `-1 % 2 == -1`. Shift your indices to be non-negative or use `i & 1`.
6. **Trying a single in-place layer when the footprint has old values on both sides.** That is exactly the situation that needs two layers.
7. **Copying layers instead of swapping.** Correct but wastes time.

---

## 13. When to use this technique / how to recognize similar problems

- There is an outer index (row, step, time, item) and the recurrence for index `i` reads **only** index `i - 1` (or a constant number of previous indices).
- The full table is too big, typically because there are three or more dimensions.
- The answer is a value, not a path (as in the previous topic, reconstruction needs the full table).

| Situation | Layers to keep | Index |
|-----------|----------------|-------|
| reads `i - 1` only | 2 | `i % 2` or `i & 1` |
| reads `i - 1` and `i - 2` | 3 | `i % 3` |
| reads `i - 1, …, i - k` | `k + 1` | `i % (k + 1)` |
| footprint of `i - 1` has old cells on one side only | 1 (in place, previous topic) | — |

---

## 14. Summary and revision notes

- Rolling array = keep the last `k + 1` layers and index layer `i` by `i % (k + 1)`; for `k = 1` that is `dp[i % 2]` = `dp[i & 1]`.
- Two layers are needed when the footprint has old values on **both** sides; the old layer is then read-only.
- 3D → 2D: Cherry Pickup II from `R·C²` to `2·C²` cells.
- **Reset** the reused layer whenever the code reads it before assigning it (`+=`, `max=`, push style).
- The final layer is in slot `last % 2`.

```
+-----------------------------------------------------------------+
|  for r = R-1 down to 0:                                         |
|     cur   = layer[r % 2]                                        |
|     below = layer[(r + 1) % 2]                                  |
|     (reset cur if it is accumulated into)                       |
|     cur[c1][c2] = here(r,c1,c2) + max_{d1,d2} below[c1+d1][c2+d2]|
|  answer = layer[0 % 2][0][C-1]                                  |
+-----------------------------------------------------------------+
```

---

## 15. Exercises

**Exercise 1.** [Knight Dialer](https://leetcode.com/problems/knight-dialer/) (LC 935): `count[s][d]` = number of length-`s` numbers ending on digit `d`. Write the rolling update. Is a reset needed?

<details><summary>Answer</summary>

With the pull style `next[d] = sum of current[p] over digits p that a knight can reach d from`, every `next[d]` is assigned with `=`, so no reset is needed. If you write it push style (`next[q] += current[d]` for each knight move `d → q`), you must set `next` to all zeros first. Space O(10) instead of O(10·n).
</details>

**Exercise 2.** A DP reads layers `i - 1` and `i - 3`. How many layers do you keep, and how do you index them?

<details><summary>Answer</summary>

Layers `i, i-1, i-2, i-3` can all be live at the same time (layer `i-2` is not read by `i`, but it will be read by `i+1` as its `i-1`). So 4 layers, indexed by `i % 4`. Layers `i` and `i - 4` share a slot, and layer `i - 4` is dead because nobody at index ≥ `i` reads it.
</details>

**Exercise 3.** In Cherry Pickup II you decide to compute *forward*: `best[r][c1][c2]` = most cherries collected from row 0 to row `r`, ending at `(c1, c2)`, with `-∞` for unreachable states, using the push update `next[n1][n2] = max(next[n1][n2], cur[c1][c2] + here(r+1, n1, n2))`. What must you do to the reused layer, and with which value?

<details><summary>Answer</summary>

The update reads `next[n1][n2]` before improving it, so the reused layer must be reset at the start of each step, to **−∞** (e.g. `Integer.MIN_VALUE / 2`), not 0. Resetting to 0 would make unreachable states (for example robot 1 at column C−1 in row 1) look reachable with 0 cherries, and those states could then propagate wrong totals.
</details>

**Exercise 4.** Why can Minimum Path Sum (previous topic) use **one** row but Out of Boundary Paths needs **two** layers?

<details><summary>Answer</summary>

Minimum Path Sum reads the old value directly above and the *new* value to the left, which a left-to-right sweep provides in one array. In Out of Boundary Paths each new cell depends on the old values of all four neighbours (up, down, left, right in the same old layer), so whatever order we fill a single array, some neighbour would already be overwritten. Two layers keep the old one intact.
</details>
