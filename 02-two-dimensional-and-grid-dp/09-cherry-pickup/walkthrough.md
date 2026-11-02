# Cherry Pickup — Walkthrough

This is the hardest problem of the grid module, and the most instructive. You will see two natural ideas fail, then fix them with one change of viewpoint: **a round trip is two forward trips, and two forward trips can be walked at the same time.**

---

## 1. Intuition (no code, no math — an everyday analogy)

You must carry baskets of fruit from the farmhouse in the top-left corner of an orchard to the barn in the bottom-right corner and then walk back. Paths only go right/down on the way there and left/up on the way back. Some squares are thorn bushes.

Walking back from the barn to the farmhouse along left/up moves visits the same squares as *some* walk from the farmhouse to the barn along right/down moves — just in reverse order. Cherries don't care about the order in which they are picked. So the whole task is really:

> Choose **two** right/down routes from the farmhouse to the barn. You collect every cherry that lies on at least one route.

Now imagine sending **two friends** along those two routes at the same time, one square per minute. After the same number of minutes they are always the same number of squares away from the farmhouse. If they are ever on the same square, they are there **at the same minute** — so "don't pick the same cherry twice" becomes a simple check: "are my two friends on the same square right now?"

---

## 2. Problem statement in our own words

Original: [LeetCode 741 — Cherry Pickup](https://leetcode.com/problems/cherry-pickup/) (Hard).

You get an `n × n` grid. A cell holds `0` (empty), `1` (one cherry) or `-1` (a thorn you cannot step on). Start at `(0, 0)`, go to `(n-1, n-1)` moving only right or down through non-thorn cells, then return to `(0, 0)` moving only left or up. Every cherry you pass is picked and the cell becomes empty. Return the maximum number of cherries you can pick; if there is no valid path to `(n-1, n-1)`, return `0`. The start cell is never a thorn.

---

## 3. Examples

**Example 1 (our running example)**

```
        c0  c1  c2
  r0:    0   1  -1
  r1:    1   0  -1
  r2:    1   1   1
```

Output: **5** — every cherry in the grid. One way: go down `(0,0)→(1,0)→(2,0)→(2,1)→(2,2)` picking 4 cherries, come back `(2,2)→(2,1)→(1,1)→(0,1)→(0,0)` picking the cherry at `(0,1)`.

**Example 2 (no way through)**

```
   1   1  -1
   1  -1   1
  -1   1   1
```

Output: **0**. Every cell with `row + col = 2` — `(0,2)`, `(1,1)`, `(2,0)` — is a thorn, and every right/down path must pass through one of them.

**Edge example (1 × 1):** `[[1]]` → **1**. Start = end; the single cherry is picked once.

**The greedy trap (4 × 4)** — we will meet it in Section 4:

```
   0  0  0  0
   0  0  1  0
   1  1  1  1
   0  1  0  0
```

Output: **6**.

---

## 4. Deriving the solution — the 7 questions

### Two tempting ideas that fail

**Wrong idea A: solve "down" and "back" with two separate DPs.** The best way back depends on which cherries the way down took, so the two halves are not independent subproblems. Storing "which cells are already empty" in the state would need the entire path — exponentially many possibilities.

**Wrong idea B: greedy — take the best single path, erase it, take the best path again.** It sounds reasonable: "do the best you can now, then the best you can with what is left". Run it on the trap grid:

```
First pass: the best single path takes all of row 2.

   0  0  0  0          path:  (0,0)↓(1,0)↓(2,0)→(2,1)→(2,2)→(2,3)↓(3,3)
   0  0  1  0
  [1][1][1][1]         cherries = 4   (the only path with 4)
   0  1  0  0

After erasing:         Left over: (1,2) and (3,1).

   0  0  0  0          (1,2) is in column 2; (3,1) is lower but in column 1.
   0  0  1  0          A right/down path can never move left, so no single
   0  0  0  0          path visits both. The second pass gets only 1.
   0  1  0  0
                        Greedy total = 4 + 1 = 5
```

The best pair of paths **splits row 2 between them**:

```
Path A: (0,0)↓(1,0)↓(2,0)→(2,1)↓(3,1)→(3,2)→(3,3)    picks (2,0) (2,1) (3,1) = 3
Path B: (0,0)→(0,1)→(0,2)↓(1,2)↓(2,2)→(2,3)↓(3,3)    picks (1,2) (2,2) (2,3) = 3
                                                     total = 6
```

Neither path A nor path B is the best *single* path (each gets only 3), yet together they beat greedy. The greedy first choice grabbed `(2,0)…(2,3)` and in doing so cut off `(1,2)` from `(3,1)`. **The two paths must be chosen jointly.** `GreedyCounterexample.java` in the solution folder runs both methods on this grid and checks `greedy = 5 < 6 = optimal`.

### The reframing

1. Reverse the return trip: a left/up path from `(n-1,n-1)` to `(0,0)` is a right/down path from `(0,0)` to `(n-1,n-1)` read backwards. So we choose **two right/down paths** and collect the union of their cherries.
2. Walk both paths **at the same time**, one move each per step.

### Q1. What is changing?

Walker 1 is at `(r1, c1)`, walker 2 at `(r2, c2)`. That is four numbers. But both walkers have made the same number of moves, `step`, and every move increases `row + col` by exactly 1 (a move down adds 1 to the row, a move right adds 1 to the column). So:

```
r1 + c1 = step      =>   c1 = step - r1
r2 + c2 = step      =>   c2 = step - r2
```

Given `step`, `r1`, `r2`, the columns are **derived**. The state is `(step, r1, r2)` — three numbers.

```
Anti-diagonals (cells with the same row + col = same step):

  step:  0  1  2  3            Both walkers always stand on the
         1  2  3  4            same anti-diagonal. They can only
         2  3  4  5            ever share a cell at the same step.
         3  4  5  6
```

Compare with [Introduction to 3D DP](../08-introduction-to-3d-dp/): there the robots shared the **row**. Here they share the **step** (the anti-diagonal), and the column is derived.

### Q2. What does the state represent?

We use the state in two directions, and both are correct:

> **Top-down (memoization):** `f(step, r1, r2)` = the most cherries the two walkers can collect from now until both reach `(n-1, n-1)`, given that after `step` moves walker 1 is in row `r1` and walker 2 is in row `r2` (current cells included). Answer: `f(0, 0, 0)`.

> **Bottom-up (tabulation):** `dp[step][r1][r2]` = the most cherries the two walkers can have collected **so far**, starting from `(0,0)` and ending after `step` moves in rows `r1` and `r2` (current cells included). Answer: `dp[2n-2][n-1][n-1]`.

Why can we choose either direction here, when [Dungeon Game](../07-dungeon-game/) forced us to go backwards? In Dungeon Game the quantity (minimum health) depended on what happens *later*. Here cherries simply add up along the route, so "best total so far" and "best total from here on" are both well defined.

### Q3. What are the choices?

Each walker moves down or right, so there are `2 × 2 = 4` combined moves. In terms of rows:

```
walker 1: down -> r1 + 1      right -> r1 (column grows instead)
walker 2: down -> r2 + 1      right -> r2

(down, down)  -> (step+1, r1+1, r2+1)
(down, right) -> (step+1, r1+1, r2  )
(right, down) -> (step+1, r1,   r2+1)
(right, right)-> (step+1, r1,   r2  )
```

### Q4. What is the recurrence?

```
c1 = step - r1,   c2 = step - r2
here = grid[r1][c1] + (r1 != r2 ? grid[r2][c2] : 0)

f(step, r1, r2) = here + max( f(step+1, r1+1, r2+1),
                              f(step+1, r1+1, r2  ),
                              f(step+1, r1,   r2+1),
                              f(step+1, r1,   r2  ) )
```

- **`here`** is what the walkers pick right now.
- **The shared cell:** at the same step, `r1 == r2` forces `c1 == c2`, i.e. the same cell. We add its cherry once. And because a cell `(r, c)` can only be visited at step `r + c`, walkers can never visit the same cell at *different* steps — so this one check prevents all double counting. This is the payoff of walking together.
- **`max`** picks the best joint move.

Forward version (for tabulation):

```
dp[step][r1][r2] = here + max over (p1 ∈ {r1-1, r1}, p2 ∈ {r2-1, r2}) of dp[step-1][p1][p2]
```

(`p = r - 1` means the walker came from above; `p = r` means it came from the left.)

### Q5. What are the base cases?

- **Impossible states → minus infinity.** If a derived cell is outside the grid or is a thorn, the state cannot be part of any valid plan. We return `NEG_INF`.
- **End:** at `step = 2n-2`, the only valid state is `r1 = r2 = n-1` (both at the corner). `f = here` there.
- **Start (forward version):** `dp[0][0][0] = grid[0][0]`.
- **If every continuation is impossible**, the state is impossible too: return `NEG_INF`, don't add `here` to it.
- **Final answer:** `max(0, f(0,0,0))`. If no route reaches the corner, the problem says return 0.

**Using minus infinity safely in Java.** We use `NEG_INF = Integer.MIN_VALUE`. We never add to it (`MIN_VALUE + 1` is fine, but `MIN_VALUE - 1` would wrap to `+2147483647`, and a careless `here + NEG_INF` with a negative `here` could do exactly that). Instead we check `if (best == NEG_INF) return NEG_INF;` before adding.

Why not use `-1` as "blocked"? Because `-1` is too close to real values: `max(-1, 0)` would happily pick 0 from a different branch, but `here + (-1)` would silently turn an impossible plan into a slightly worse "possible" one.

### Q6. What is the order of computation?

- Top-down: `f(step)` needs `f(step + 1)`; recursion handles it.
- Bottom-up: `dp[step]` needs only `dp[step - 1]`, so loop `step` from `1` **up** to `2n-2`. Inside a step, `r1` and `r2` can go in any order. Only rows with a column inside the grid are valid: `max(0, step-(n-1)) ≤ r ≤ min(n-1, step)`.

### Q7. Can the state space be optimized?

`dp[step]` reads only `dp[step-1]`. Keep two `n × n` arrays, `previous` and `current`, and swap after each step: `O(n²)` memory instead of `O(n³)`.

---

## 5. Brute force: recursion

```java
private static int collect(int[][] grid, int step, int r1, int r2) {
    int c1 = step - r1, c2 = step - r2;
    if (r1 >= n || c1 >= n || r2 >= n || c2 >= n) return NEG_INF;     // off the grid
    if (grid[r1][c1] == -1 || grid[r2][c2] == -1) return NEG_INF;     // thorn
    int here = grid[r1][c1] + (r1 != r2 ? grid[r2][c2] : 0);
    if (step == 2 * n - 2) return here;                               // both at the corner
    int bestFuture = max of the 4 calls collect(step+1, …);
    if (bestFuture == NEG_INF) return NEG_INF;                        // all blocked
    return here + bestFuture;
}
```

We never get `r < 0` or `c < 0` because rows only grow and `c = step - r` with `r ≤ step`.

**Recursion tree on Example 1** (states `(step, r1, r2)`, with the two cells in brackets; `✗` = thorn or off the grid):

```
(0,0,0) [(0,0)(0,0)] here=0
├── (1,1,1) [(1,0)(1,0)] here=1
│   ├── (2,2,2) [(2,0)(2,0)] here=1 ── (3,2,2) [(2,1)(2,1)] ── (4,2,2) [(2,2)]
│   ├── (2,2,1) [(2,0)(1,1)] here=1 ── (3,2,2) ...                <- repeated
│   ├── (2,1,2) [(1,1)(2,0)] here=1 ── (3,2,2) ...                <- repeated
│   └── (2,1,1) [(1,1)(1,1)] here=0 ── (3,2,2) ...                <- repeated
├── (1,1,0) [(1,0)(0,1)] here=2
│   ├── (2,2,1) ...                                                <- repeated
│   ├── (2,2,0) [(2,0)(0,2)] ✗ thorn
│   ├── (2,1,1) ...                                                <- repeated
│   └── (2,1,0) [(1,1)(0,2)] ✗ thorn
├── (1,0,1) [(0,1)(1,0)] here=2       (mirror image of (1,1,0))
└── (1,0,0) [(0,1)(0,1)] here=1
    ├── (2,1,1) ...                                                <- repeated
    └── (2,1,0) (2,0,1) (2,0,0)  ✗ all touch the thorn (0,2)
```

Notice `(1,1,0)` and `(1,0,1)` are mirror images (walkers swapped), and `(3,2,2)` is reached many times. The brute force makes **93** calls on this tiny 3 × 3 grid and **2725** on the 4 × 4 trap grid. Each step branches 4 ways and there are `2n-2` steps, so the work grows like `4^(2n-2)`. With `n = 50` (LeetCode's limit) that is `4^98` — impossible.

---

## 6. Memoization (top-down)

**What is cached:** `memo[step][r1][r2]` = `f(step, r1, r2)`. We need a separate `computed` boolean array, because every `int` — including `NEG_INF` — can be a real stored answer, so no value can mean "empty".

**Dry run with cache hits (Example 1).** The recursion goes deep first along `(1,1,1)`:

```
f(4,2,2) = 1                       cell (2,2)
f(3,2,2) = 1 + 1 = 2               cell (2,1), only child (4,2,2)
f(2,2,2) = 1 + 2 = 3               cell (2,0); children (3,3,*) off grid, (3,2,2)=2
f(2,2,1) = (1 + 0) + 2 = 3         cells (2,0),(1,1); (3,2,2) HIT; others touch thorn (1,2)
f(2,1,2) = 3                       mirror: (3,2,2) HIT
f(2,1,1) = 0 + 2 = 2               (3,2,2) HIT
f(1,1,1) = 1 + max(3,3,3,2) = 4
```

Then the second child of the root:

```
f(1,1,0) = (1 + 1) + max( f(2,2,1)=3 HIT, f(2,2,0)=✗, f(2,1,1)=2 HIT, f(2,1,0)=✗ ) = 5
f(1,0,1) = 5                       children (2,1,2) HIT, (2,1,1) HIT
f(1,0,0) = 1 + f(2,1,1)=2 HIT  = 3
f(0,0,0) = 0 + max(4, 5, 5, 3) = 5
```

On Example 1 the memoized version makes **41** calls, computes **11** distinct states and gets **8** cache hits.

---

## 7. Tabulation (bottom-up)

We fill `dp[step][r1][r2]` **forward**, starting from `dp[0][0][0] = grid[0][0] = 0`, everything else `NEG_INF` (shown as `-`). Each step's layer is a small table: rows = `r1`, columns = `r2`. Remember that `(r1, r2)` means walker 1 at `(r1, step-r1)`, walker 2 at `(r2, step-r2)`.

**Step 0** (only `(0,0)` is possible):

```
          r2=0  r2=1  r2=2
  r1=0      0     -     -
  r1=1      -     -     -
  r1=2      -     -     -
```

**Step 1** — cells on this anti-diagonal: `(0,1)` = 1, `(1,0)` = 1.

```
(0,0): both at (0,1), shared -> 1;     prev best dp0[0][0] = 0  -> 1
(0,1): (0,1) and (1,0)       -> 2;     prev dp0[0][0] = 0       -> 2
(1,0): mirror                                                 -> 2
(1,1): both at (1,0), shared -> 1                               -> 1

          r2=0  r2=1  r2=2
  r1=0      1     2     -        (row 2 would need column -1: invalid at step 1)
  r1=1      2     1     -
  r1=2      -     -     -
```

**Step 2** — cells: `(0,2)` = thorn, `(1,1)` = 0, `(2,0)` = 1. Any state with `r = 0` touches the thorn.

```
(1,1): both at (1,1), here 0;  prev from {(1,1)=1,(1,0)=2,(0,1)=2,(0,0)=1} -> 2 + 0 = 2
(1,2): (1,1) + (2,0) = 1;      prev from {(1,2)=-,(1,1)=1,(0,2)=-,(0,1)=2} -> 2 + 1 = 3
(2,1): mirror                                                              -> 3
(2,2): both at (2,0), here 1;  prev from {(2,2)=-,(2,1)=-,(1,2)=-,(1,1)=1} -> 1 + 1 = 2

          r2=0  r2=1  r2=2
  r1=0      -     -     -
  r1=1      -     2     3
  r1=2      -     3     2
```

**Step 3** — cells: `(1,2)` = thorn, `(2,1)` = 1. Only `(2,2)` survives.

```
(2,2): both at (2,1), here 1;  prev from {(2,2)=2,(2,1)=3,(1,2)=3,(1,1)=2} -> 3 + 1 = 4

          r2=0  r2=1  r2=2
  r1=0      -     -     -
  r1=1      -     -     -
  r1=2      -     -     4
```

**Step 4** — the corner `(2,2)` = 1.

```
(2,2): here 1;  prev from {(2,2)=4, others -}  -> 4 + 1 = 5

  dp[4][2][2] = 5   ->  answer max(0, 5) = 5
```

**Example 2 in the same style.** At step 2 every cell of the anti-diagonal is a thorn, so the whole step-2 layer stays `-`, every later layer stays `-`, and the answer is `max(0, NEG_INF) = 0`. The sentinel carries "impossible" all the way to the end without any special code.

---

## 8. Space optimization

The forward table only ever looks one step back:

```
dp[1] reads dp[0]
dp[2] reads dp[1]
dp[3] reads dp[2]    <- dp[0], dp[1] are dead by now
dp[4] reads dp[3]
```

So we keep `previous` (step − 1) and `current` (step), each `n × n`:

```java
int[][] previous = new int[n][n];  fill with NEG_INF;  previous[0][0] = grid[0][0];
for (int step = 1; step <= 2 * n - 2; step++) {
    int[][] current = new int[n][n];  fill with NEG_INF;
    ... current[r1][r2] = here + best previous[p1][p2] ...
    previous = current;
}
return Math.max(0, previous[n - 1][n - 1]);
```

`current` must be refilled with `NEG_INF` at every step — otherwise a stale value from two steps ago could look like a reachable state. Memory drops from `(2n-1)·n·n` to `2·n·n`: for `n = 50`, from 245,000 ints to 5,000.

---

## 9. Dry run on a full example

Following the forward table backwards from the answer recovers the two routes:

| Step | `(r1, r2)` | Walker 1 cell | Walker 2 cell | `here` | Came from | `dp` |
|---|---|---|---|---|---|---|
| 0 | (0, 0) | (0,0) | (0,0) | 0 (shared) | — | 0 |
| 1 | (0, 1) | (0,1) | (1,0) | 1 + 1 = 2 | (0,0) | 2 |
| 2 | (1, 2) | (1,1) | (2,0) | 0 + 1 = 1 | (0,1) | 3 |
| 3 | (2, 2) | (2,1) | (2,1) | 1 (shared) | (1,2) | 4 |
| 4 | (2, 2) | (2,2) | (2,2) | 1 (shared) | (2,2) | 5 |

Walker 1: `(0,0)→(0,1)→(1,1)→(2,1)→(2,2)`. Walker 2: `(0,0)→(1,0)→(2,0)→(2,1)→(2,2)`. As a round trip: go down along walker 2's route, come back along walker 1's route reversed. All 5 cherries; the shared cells `(2,1)` and `(2,2)` were counted once.

On the trap grid, the same table ends at `dp[6][3][3] = 6`, while the greedy two-pass method gives 5 — both values are checked by `GreedyCounterexample.java`.

---

## 10. Complexity analysis

| Approach | Time | Space | Why |
|---|---|---|---|
| Brute force | `O(4^(2n))` | `O(n)` | `2n-2` steps, 4 branches each, nothing reused; the stack is `2n-1` frames deep. |
| Memoization | `O(n³)` | `O(n³)` | `(2n-1)·n·n` states, each computed once with 4 lookups. The memo (and `computed` flags) have one cell per state. |
| Tabulation | `O(n³)` | `O(n³)` | Same states, filled by three loops (step, r1, r2) with 4 predecessors each. |
| Space-optimized | `O(n³)` | `O(n²)` | Same work; only two `n × n` layers alive. |

For `n = 50`: about `99 · 50 · 50 · 4 ≈ 1 million` simple operations.

A 4D state `(r1, c1, r2, c2)` would also be `O(n⁴)` states — 50 times more. Deriving the columns from the step is what makes the solution `O(n³)`.

---

## 11. Why this approach works

1. **Round trip = two forward paths.** Every valid round trip gives two right/down paths (the second one reversed), and every pair of right/down paths gives a valid round trip. The cherries picked are the union of the cells on the two paths in both cases. So maximizing over pairs of paths is the same problem.
2. **Walking in lockstep loses nothing.** The union of cells does not depend on *when* each path visits a cell, so we may as well move both walkers one cell per step.
3. **Shared cells counted exactly once.** A cell `(r, c)` lies on anti-diagonal `r + c` and is only visited at step `r + c`. If both paths contain it, both walkers stand on it at that step — exactly when `r1 == r2`. We add it once there and never see it again.
4. **The state is complete.** The future depends only on the two current cells: which cells remain reachable, and which future cells could be shared. Past cells can never be revisited (moves only go right/down), so no "already picked" memory is needed.
5. **Optimal substructure.** The best plan from a state continues with the best plan from the next state it moves to; otherwise swapping in the better continuation would improve it.

Greedy fails exactly because point 4 is false for one path at a time: the best *second* path depends on which cells the first took, and greedy commits to the first path without looking at the second.

---

## 12. Common mistakes

1. **Running the greedy two-pass method.** It passes some examples (it even gets Example 1 right) and fails on grids like the trap grid.
2. **Counting a shared cell twice.** Forgetting `r1 != r2` overcounts; on the 1 × 1 grid you would return 2.
3. **Comparing `c1 == c2` instead of `r1 == r2`.** It is equivalent at the same step, but mixing in a column from a different step is a bug source — derive both columns from the same `step`.
4. **Adding to `Integer.MIN_VALUE`.** Check for `NEG_INF` before adding; never compute `here + NEG_INF` blindly.
5. **Returning `NEG_INF` (or a negative number) when no route exists.** The problem wants `0`: return `max(0, result)`.
6. **Using `-1` as "not computed" in the memo.** Real answers can be `NEG_INF`, and in variants could be any value; use a separate boolean array.
7. **Not resetting `current` to `NEG_INF` in the space-optimized version.** Stale values make unreachable states look reachable.
8. **Wrong valid range of rows per step.** Rows outside `max(0, step-(n-1)) … min(n-1, step)` give columns outside the grid.

---

## 13. When to use this technique / how to recognize similar problems

- **"Go there and come back"** on a grid with monotone moves → reverse the return, look for two simultaneous forward walkers.
- **"Two paths, collect rewards, shared cells once"** → walk them in lockstep so collisions happen at the same time.
- **"Take the best, then the best of the rest"** → be suspicious. Try to build a small counterexample before trusting greedy; if the choices interact, you need a joint DP.
- **Several coordinates that move together** → look for an invariant like `row + col = step` that lets you derive one coordinate and drop a dimension.

---

## 14. Summary and revision notes

- Round trip = two right/down paths; cherries = union of their cells.
- Walk both paths at once: `step = r + c` for both walkers, so state `(step, r1, r2)` and `c = step - r`.
- Shared cell ⇔ `r1 == r2` at the same step → count once.
- Thorn or off-grid → `NEG_INF`; never add to `NEG_INF`; answer `max(0, …)`.
- Greedy "best path twice" fails (trap grid: greedy 5, optimal 6).
- Time `O(n³)`; memory `O(n³)` → `O(n²)` with two layers.

```
┌───────────────────────────────────────────────────────────────────────────┐
│ c1 = s - r1,  c2 = s - r2                                                 │
│ invalid if off grid or thorn  ->  f = NEG_INF                             │
│ here = grid[r1][c1] + (r1 != r2 ? grid[r2][c2] : 0)                       │
│                                                                           │
│ f(s, r1, r2) = here                                       if s = 2n - 2   │
│ f(s, r1, r2) = here + max( f(s+1, r1+1, r2+1), f(s+1, r1+1, r2),          │
│                            f(s+1, r1,   r2+1), f(s+1, r1,   r2) )         │
│               (NEG_INF if all four are NEG_INF)                           │
│ answer = max(0, f(0, 0, 0))                                               │
└───────────────────────────────────────────────────────────────────────────┘
```

---

## Exercises

**Exercise 1.** In Example 1, what does the state `(step=2, r1=1, r2=2)` mean in terms of cells, and what is `dp[2][1][2]`?

<details>
<summary>Answer</summary>

Walker 1 is at `(1, 2-1) = (1,1)` and walker 2 at `(2, 2-2) = (2,0)`. `here = 0 + 1 = 1`. The best previous value among `dp[1][0][1] = 2`, `dp[1][1][1] = 1` (the other two predecessors are invalid) is 2, so `dp[2][1][2] = 3`.

</details>

**Exercise 2.** Change the trap grid so that the cherry at `(3,1)` becomes a thorn:

```
0  0  0  0
0  0  1  0
1  1  1  1
0 -1  0  0
```

What does the greedy two-pass method return now, and what is the true answer?

<details>
<summary>Answer</summary>

Greedy's first path is still all of row 2 (4 cherries), ending `(2,3)↓(3,3)`. The only cherry left is `(1,2)`, reachable by `(0,0)→(0,1)→(0,2)↓(1,2)↓(2,2)→(2,3)↓(3,3)`, so the second pass gets 1. Greedy = 5. There are only 5 cherries in the grid, so the true answer is also 5. Greedy is not *always* wrong — it was the cherry at `(3,1)`, unreachable together with `(1,2)` by one path, that made it fail. A method that is right on some inputs and wrong on others is still wrong; the DP is right on all of them.

</details>

**Exercise 3.** Why do we not need to check whether the two walkers visited the same cell at *different* steps?

<details>
<summary>Answer</summary>

A cell `(r, c)` can only be reached after exactly `r + c` moves, because each move increases `row + col` by one. So any walker on that cell is there at step `r + c`. Two walkers on the same cell are therefore always there at the same step, and the `r1 == r2` check at that step catches it.

</details>

**Exercise 4.** How would the state change if the two walkers did **not** have to move at the same time — say walker 1 could make two moves while walker 2 waits? Would the 3D state still work?

<details>
<summary>Answer</summary>

Not directly: the walkers would no longer be on the same anti-diagonal, so one shared `step` could not derive both columns. You would need both full positions `(r1, c1, r2, c2)` — a 4D state — and shared cells could be visited at different times, which breaks the simple `r1 == r2` collision rule. But because the cherries collected depend only on the two *sets* of cells, we are free to choose the lockstep schedule; that freedom is exactly what makes the 3D state possible.

</details>

---

⬅ Back to [topic README](README.md) · 🏠 [Module overview](../README.md) · ➡ Next module: [Subsequences Fundamentals](../../03-subsequence-and-knapsack-dp/01-subsequences-fundamentals/)
