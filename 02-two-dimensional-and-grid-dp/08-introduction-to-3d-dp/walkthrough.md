# Introduction to 3D DP — Walkthrough

We learn 3D DP through **LeetCode 1463 — Cherry Pickup II**. The goal is not only to solve this problem. The goal is to learn the question you will ask on every hard DP problem from now on: *"How many independent things change, and which of them can I derive from the others?"*

---

## 1. Intuition (no code, no math — an everyday analogy)

Picture two friends going down a staircase-shaped orchard. They both start at the top: one at the far left, one at the far right. Every minute **both** go down exactly one terrace. On each terrace each friend may step a little left, stay straight, or step a little right. They pick every cherry on the spots they stand on, and if they stand on the same spot, the cherries there can only be picked once.

Now imagine you are a coach shouting directions from above. To decide the best next move you need to know **where both friends are**. Knowing only where the left friend is would not be enough: "left friend in column 1" is a great situation if the right friend is in a rich column and a poor one if the right friend is stuck in an empty corner.

But you do *not* need to know two separate terrace numbers. Both friends always go down together, so they are always on the same terrace. That is the whole trick of this topic:

- things that change **independently** must each get their own dimension;
- things that are always **equal or derivable** are stored once.

Here: terrace (shared) + left friend's column + right friend's column = **3 numbers** = a 3D DP.

---

## 2. Problem statement in our own words

Original: [LeetCode 1463 — Cherry Pickup II](https://leetcode.com/problems/cherry-pickup-ii/) (Hard).

You get a grid of non-negative numbers; each number is the cherries in that cell. Robot 1 starts at the top-left cell and robot 2 at the top-right cell. In every step both robots move one row down, and each one can go to the column on its left, the same column, or the column on its right, without leaving the grid. Each robot collects the cherries of every cell it visits; if both robots are in the same cell, those cherries are collected only once. Both robots must reach the bottom row. Return the largest total number of cherries they can collect.

---

## 3. Examples

**Example 1 (our running example, used in every section below)**

```
         col0 col1 col2
row 0:    3    1    1        robot 1 starts at (0,0), robot 2 at (0,2)
row 1:    2    5    1
row 2:    1    5    5
row 3:    2    1    1
```

Output: **24**.

One best plan:

```
row 0:  [3]   1   [1]     R1 col 0, R2 col 2   -> 3 + 1 = 4
row 1:  [2]  [5]   1      R1 col 0, R2 col 1   -> 2 + 5 = 7
row 2:   1   [5]  [5]     R1 col 1, R2 col 2   -> 5 + 5 = 10
row 3:  [2]  [1]   1      R1 col 0, R2 col 1   -> 2 + 1 = 3
                                          total = 24
```

Robot 1 collects 3 + 2 + 5 + 2 = 12 and robot 2 collects 1 + 5 + 5 + 1 = 12.

**Example 2 (from LeetCode)** — a 5 × 7 grid

```
1 0 0 0 0 0 1
2 0 0 0 0 3 0
2 0 9 0 0 0 0
0 3 0 5 4 0 0
1 0 2 3 0 0 6
```

Output: **28** (robot 1: 1 + 9 + 5 + 2 = 17, robot 2: 1 + 3 + 4 + 3 = 11).

**Edge example** — the smallest legal shape, 2 × 2:

```
1 1
1 1
```

Output: **4**. Robot 1 goes down to (1,0), robot 2 to (1,1); all four cells are collected. If instead both went to the same bottom cell, its cherry would count once and the total would be 3.

---

## 4. Deriving the solution — the 7 questions

### Q1. What is changing?

As the robots move, these quantities change:

- the row of robot 1 and the row of robot 2;
- the column of robot 1 (`col1`) and the column of robot 2 (`col2`).

That looks like **four** numbers. But look closer: every step moves *both* robots down exactly one row. They start in row 0 together, so after `k` steps both are in row `k`. Their rows are **always equal**. So the four numbers collapse to three:

```
  (row1, col1, row2, col2)   with row1 == row2 always
            ↓
  (row, col1, col2)
```

**How to see that two moving agents means more dimensions.** Ask: "If I froze the problem at one moment, what must I write down so that someone else could continue optimally?" With one robot you write its cell (2 numbers). With two robots you must write down *both* cells, because the future depends on both (they share cherries, they may collide). Then remove every number that can be computed from the others. Each agent usually adds the coordinates that make it different from the others — here, one extra column.

### Q2. What does the state represent?

> `f(row, col1, col2)` = the largest number of cherries the two robots can still collect from row `row` down to the last row, given that robot 1 is in column `col1` and robot 2 is in column `col2` of row `row` (the cherries of these current cells included).

The answer to the whole problem is `f(0, 0, cols - 1)`.

### Q3. What are the choices?

Each robot independently picks a column change `d ∈ {-1, 0, +1}`:

```
robot 1:  ↙  ↓  ↘          robot 2:  ↙  ↓  ↘
          -1  0  +1                  -1  0  +1
```

Together that is **3 × 3 = 9 combined choices**:

```
(move1, move2) ∈ { (-1,-1) (-1,0) (-1,+1)
                   ( 0,-1) ( 0,0) ( 0,+1)
                   (+1,-1) (+1,0) (+1,+1) }
```

A choice is illegal if it takes a robot outside columns `0 … cols-1`.

### Q4. What is the recurrence?

```
here = grid[row][col1] + (col1 != col2 ? grid[row][col2] : 0)

f(row, col1, col2) = here + max over the 9 (move1, move2) of
                            f(row + 1, col1 + move1, col2 + move2)
```

- `here` is what the robots collect **right now**. The `col1 != col2` test is the collision rule: one cell, one harvest.
- The `max` picks the best combined move, because we want the most cherries.
- We add `here` to the best future, because cherries now and cherries later simply add up.

Illegal moves (outside the grid) return "minus infinity" so `max` never picks them.

### Q5. What are the base cases?

- **Last row:** `f(rows - 1, col1, col2) = here`. There is no row below, so there is no future to add.
- **Outside the grid:** if `col1` or `col2` is outside `0 … cols-1`, the plan is impossible; return a very negative number. At least one of the 9 moves always stays inside (the move `(0,0)`), so a legal state never ends up with only impossible futures.

### Q6. What is the order of computation?

`f(row, …)` reads only `f(row + 1, …)`. So:

```
layer row 3  (base, no dependencies)
     ↑ read by
layer row 2
     ↑ read by
layer row 1
     ↑ read by
layer row 0  -> answer at [0][0][cols-1]
```

Bottom-up we loop `row` from `rows - 1` **down** to `0`. Inside one layer, every cell reads only the layer below, so the order of the `col1` and `col2` loops does not matter.

### Q7. Can the state space be optimized?

Layer `row` reads only layer `row + 1`. Once layer `row` is finished, layer `row + 1` is never read again. So instead of `rows` layers we keep **two** `cols × cols` layers: `below` and `current`. Memory goes from `O(rows · cols²)` to `O(cols²)`.

---

## 5. Brute force: recursion

```java
private static int collect(int[][] grid, int row, int col1, int col2) {
    if (col1 < 0 || col1 >= cols || col2 < 0 || col2 >= cols) {
        return Integer.MIN_VALUE / 2;            // impossible plan
    }
    int here = grid[row][col1];
    if (col1 != col2) here += grid[row][col2];   // shared cell counted once
    if (row == rows - 1) return here;            // bottom row

    int bestFuture = Integer.MIN_VALUE / 2;
    for (int move1 = -1; move1 <= 1; move1++)
        for (int move2 = -1; move2 <= 1; move2++)
            bestFuture = Math.max(bestFuture, collect(grid, row + 1, col1 + move1, col2 + move2));
    return here + bestFuture;
}
```

Why `Integer.MIN_VALUE / 2` and not `Integer.MIN_VALUE`? Because the caller adds `here` to the result. `MIN_VALUE + 4` is fine, but if we ever subtracted we could wrap around. Halving leaves room and is still far below any real answer.

**Recursion tree on Example 1** (states written as `(row, col1, col2)`; `✗` = outside the grid, returns at once). Of the 9 children of the root, only 4 are inside the grid:

```
(0,0,2)
├── (1,-1,1) ✗   (1,-1,2) ✗   (1,-1,3) ✗   (1,0,3) ✗   (1,1,3) ✗
├── (1,0,1)
│   ├── (2,0,0)  (2,0,1)  (2,0,2)
│   └── (2,1,0)  (2,1,1)  (2,1,2)      <- each of these expands 9 more ways into row 3
├── (1,0,2)
│   ├── (2,0,1)  (2,0,2)               <- solved again!
│   └── (2,1,1)  (2,1,2)               <- solved again!
├── (1,1,1)
│   ├── (2,0,0)  (2,0,1)  (2,0,2)      <- solved again!
│   ├── (2,1,0)  (2,1,1)  (2,1,2)      <- solved again!
│   └── (2,2,0)  (2,2,1)  (2,2,2)
└── (1,1,2)
    ├── (2,0,1)  (2,0,2)               <- solved again!
    ├── (2,1,1)  (2,1,2)               <- solved again!
    └── (2,2,1)  (2,2,2)               <- solved again!
```

The state `(2,1,2)` already appears under `(1,0,1)`, `(1,0,2)` and `(1,1,2)` — and under `(1,1,1)` too. Each time, the brute force re-solves the whole subtree below it.

**Why it is slow.** Each level branches up to 9 ways, and there are `rows` levels, so the work grows like `9^rows`. Running it on Example 1 makes **271** calls (174 of them inside the grid) for a problem that has only 4 × 3 × 3 = 36 possible states. On a 70 × 70 grid (LeetCode's limit), `9^70` is hopeless.

---

## 6. Memoization (top-down)

**What is cached:** `memo[row][col1][col2]` holds `f(row, col1, col2)` once it has been computed. Cherries are never negative, so `-1` safely means "not computed yet".

```java
if (memo[row][col1][col2] != -1) return memo[row][col1][col2];   // cache hit
...
memo[row][col1][col2] = answer;
```

**Dry run with cache hits (Example 1).** The recursion explores `(0,0,2)` → `(1,0,1)` first, which explores all of its row-2 children, and each of those explores row 3. When it later reaches `(1,0,2)`:

```
call (1,0,2)
  child (2,0,1)   -> HIT  (already solved while exploring (1,0,1)) = 9
  child (2,0,2)   -> HIT  = 9
  child (2,1,1)   -> HIT  = 8
  child (2,1,2)   -> HIT  = 13
  best future = 13, here = 3 + 1 = 4  -> f(1,0,2) = 16, stored
```

`(1,0,2)` costs only 4 lookups instead of 4 full subtrees. Counting everything on Example 1: **127** calls, of which **23** compute a new state and **56** are cache hits (the rest are off-grid calls that return immediately). Only 23 of the 36 states are reachable from the start: 1 in row 0, 4 in row 1, 9 in row 2, 9 in row 3.

---

## 7. Tabulation (bottom-up)

We create `dp[rows][cols][cols]` and fill it **layer by layer from the bottom**:

```java
for (int row = rows - 1; row >= 0; row--)
    for (int col1 = 0; col1 < cols; col1++)
        for (int col2 = 0; col2 < cols; col2++) {
            int here = grid[row][col1] + (col1 != col2 ? grid[row][col2] : 0);
            if (row == rows - 1) { dp[row][col1][col2] = here; continue; }
            int bestFuture = 0;
            for (move1 …) for (move2 …)               // 9 neighbours in layer row+1
                if (inside) bestFuture = Math.max(bestFuture, dp[row + 1][next1][next2]);
            dp[row][col1][col2] = here + bestFuture;
        }
return dp[0][0][cols - 1];
```

`bestFuture` starts at 0 here (not minus infinity) because every in-grid cell of the layer below holds a value `≥ 0`, and the move `(0,0)` always exists. Starting at 0 cannot win against a real neighbour that is larger, and no real neighbour is smaller than 0.

**How to draw a 3D table.** Draw one small 2D grid per layer. Rows of the small grid are `col1` (robot 1's column), columns are `col2` (robot 2's column):

```
layer = row r
              col2=0  col2=1  col2=2
  col1 = 0      .       .       .
  col1 = 1      .       .       .
  col1 = 2      .       .       .
```

The diagonal `col1 == col2` is where the robots collide; those cells count the grid cell once.

**Layer 3** (base; grid row 3 = `2 1 1`). `dp = grid[3][col1] + grid[3][col2]`, or just `grid[3][col1]` on the diagonal:

```
              col2=0  col2=1  col2=2
  col1 = 0      2       3       3
  col1 = 1      3       1       2
  col1 = 2      3       2       1
```

**Layer 2** (grid row 2 = `1 5 5`). Each cell = `here` + the best of up to 9 neighbours in layer 3. A neighbour of `(col1, col2)` is any `(col1 ± 0/1, col2 ± 0/1)` inside 0..2. Some cells in detail:

```
(0,0): here = 1 (collision)      neighbours col1∈{0,1}, col2∈{0,1}: 2,3,3,1 -> 3   => 4
(0,2): here = 1 + 5 = 6          col1∈{0,1}, col2∈{1,2}: 3,3,1,2 -> 3             => 9
(1,2): here = 5 + 5 = 10         col1∈{0,1,2}, col2∈{1,2}: 3,3,1,2,2,1 -> 3       => 13
(1,1): here = 5 (collision)      all 9 cells of layer 3 -> 3                      => 8
```

Full layer 2:

```
              col2=0  col2=1  col2=2
  col1 = 0      4       9       9
  col1 = 1      9       8      13
  col1 = 2      9      13       7
```

**Layer 1** (grid row 1 = `2 5 1`):

```
(0,1): here = 2 + 5 = 7          col1∈{0,1}, col2∈{0,1,2}: 4,9,9,9,8,13 -> 13   => 20
(0,2): here = 2 + 1 = 3          col1∈{0,1}, col2∈{1,2}: 9,9,8,13 -> 13          => 16
(1,1): here = 5 (collision)      all of layer 2 -> 13                            => 18
(1,2): here = 5 + 1 = 6          col1∈{0,1,2}, col2∈{1,2}: 9,9,8,13,13,7 -> 13   => 19
```

Full layer 1:

```
              col2=0  col2=1  col2=2
  col1 = 0     11      20      16
  col1 = 1     20      18      19
  col1 = 2     16      19      14
```

**Layer 0** (grid row 0 = `3 1 1`). We only *need* `(0,2)`, but the loop fills the whole layer:

```
(0,2): here = 3 + 1 = 4          col1∈{0,1}, col2∈{1,2}: 20,16,18,19 -> 20       => 24
```

```
              col2=0  col2=1  col2=2
  col1 = 0     23      24     [24]   <- answer dp[0][0][2] = 24
  col1 = 1     24      21      22
  col1 = 2     24      22      20
```

Notice the table is **symmetric** (`dp[r][a][b] == dp[r][b][a]`): swapping the robots' names never changes the cherries. You could exploit that to halve the work, but it is not needed.

---

## 8. Space optimization

Look at what the tabulation actually reads:

```
computing layer 1  reads  layer 2 only
computing layer 0  reads  layer 1 only
```

When we work on layer 1, layer 3 is already useless. So we keep two `cols × cols` arrays:

```
below   = layer row+1 (finished)
current = layer row   (being filled)
after the row:  below = current
```

```java
int[][] below = /* layer rows-1, the base */;
for (int row = rows - 2; row >= 0; row--) {
    int[][] current = new int[cols][cols];
    ... current[col1][col2] = here + best of below[next1][next2] ...
    below = current;
}
return below[0][cols - 1];
```

Why can't we use just **one** `cols × cols` array and overwrite in place (like the 1-row trick in Minimum Path Sum)? Because cell `(col1, col2)` of the new layer reads up to 9 old cells around `(col1, col2)`, including cells that come *after* it in loop order and cells that come *before* it. Overwriting in place would make later cells read a mix of old and new values. Two layers keep "old" and "new" clean.

Memory on a 70 × 70 grid: full table 70 · 70 · 70 = 343,000 ints; two layers 2 · 70 · 70 = 9,800 ints.

---

## 9. Dry run on a full example

Following the stored values from the top, we can also recover the robots' paths. At each layer we move to the neighbour that gave the max:

| Row | State `(col1, col2)` | `here` | Best next state | `dp` value | Running total |
|---|---|---|---|---|---|
| 0 | (0, 2) | 3 + 1 = 4 | (0, 1) in layer 1 = 20 | 24 | 4 |
| 1 | (0, 1) | 2 + 5 = 7 | (1, 2) in layer 2 = 13 | 20 | 11 |
| 2 | (1, 2) | 5 + 5 = 10 | (0, 1) in layer 3 = 3 | 13 | 21 |
| 3 | (0, 1) | 2 + 1 = 3 | — (base) | 3 | 24 |

Robot 1 columns: 0 → 0 → 1 → 0. Robot 2 columns: 2 → 1 → 2 → 1. Every move changes the column by at most 1, so the plan is legal. Total **24**, matching the program.

Running the space-optimized version, the two arrays go through these values:

```
start:          below = layer 3
row 2 done:     below = layer 2   (layer 3 dropped)
row 1 done:     below = layer 1   (layer 2 dropped)
row 0 done:     below = layer 0   -> below[0][2] = 24
```

---

## 10. Complexity analysis

| Approach | Time | Space | Why |
|---|---|---|---|
| Brute force | `O(9^rows)` | `O(rows)` | Each call spawns up to 9 calls, one level per row; only the recursion stack is stored. |
| Memoization | `O(rows · cols² · 9)` | `O(rows · cols²)` | There are `rows · cols · cols` states; each is computed once and looks at 9 neighbours. The memo has one cell per state; the stack is `rows` deep. |
| Tabulation | `O(rows · cols² · 9)` | `O(rows · cols²)` | Three nested loops cover every state once; the inner 9 checks are constant work. |
| Space-optimized | `O(rows · cols² · 9)` | `O(cols²)` | Same work, but only two layers are alive at once. |

For LeetCode's limits (70 × 70): `70 · 70 · 70 · 9 ≈ 3.1 million` simple steps — instant.

The general rule for any DP: **time = (number of states) × (work per state)**. Adding a dimension multiplies the number of states by that dimension's range, which is why you should never add a dimension you don't need.

---

## 11. Why this approach works

- **Complete state.** `(row, col1, col2)` contains everything the future depends on: which cells the robots can still reach and whether they can collide. Nothing about the past (which cells they visited before) changes what they can collect in later rows, because they can never go back up. So the best future from a state is a fixed number — exactly what DP needs.
- **Optimal substructure.** If the best plan from `(row, col1, col2)` first moves to `(row+1, a, b)`, then the rest of that plan must be the best plan from `(row+1, a, b)`. If it were not, swapping in the better continuation would give a better total — contradiction.
- **No double counting.** Two robots are in the same cell only if they are in the same row and the same column. Since rows are always equal, `col1 == col2` is exactly the collision test, and we add the cell once. Different rows are visited at different times, so no cell can be counted twice across rows.

---

## 12. Common mistakes

1. **Using a 4D state** `(row1, col1, row2, col2)`. It works but wastes a factor of `rows` in time and memory, and it hides the key insight. Always ask: "Are any of these always equal?"
2. **Adding the collision cell twice.** Forgetting `col1 != col2` overcounts. The 2 × 2 edge example catches it: both robots ending on the same bottom cell would wrongly score 4.
3. **Using `Integer.MIN_VALUE` and then adding to it.** `MIN_VALUE + x` is fine, but `MIN_VALUE - 1` wraps to a huge positive number. Use `MIN_VALUE / 2` or test for the sentinel explicitly.
4. **Wrong start columns.** Robot 2 starts at `cols - 1`, not at `cols`.
5. **Filling top-down in a bottom-up table.** `f(row)` needs `f(row + 1)`, so the `row` loop must go **downward** from `rows - 1`.
6. **Overwriting one layer in place.** Each new cell reads neighbours on both sides; use two layers.
7. **Allocating `rows × cols × cols` with big limits and `long`.** Fine here, but in other 3D problems memory can explode — compute `rows · cols² · bytes` before you allocate.

---

## 13. When to use this technique / how to recognize similar problems

Ask these questions when you read a new problem:

- **"Is there more than one thing moving?"** Two robots, two people, two pointers into two strings, a player and an opponent → probably one coordinate per agent.
- **"Do the agents move in lockstep?"** If they always take the same number of steps, a shared "time" coordinate replaces separate ones (here, the row; in the next topic, the step count).
- **"Is there a budget that shrinks?"** "At most k moves", "exactly k transactions", "k keys" → the budget is a dimension (LeetCode 576, 688).
- **"Can one coordinate be computed from the others?"** If yes, drop it. In the next topic, column = step − row, so columns are never stored.

Rule of thumb: the number of DP dimensions = the number of **independent** quantities you would need to write down to pause the problem and hand it to someone else.

---

## 14. Summary and revision notes

- One walker on a grid → 2D state. Two walkers → one more coordinate per extra walker, **minus** anything shared or derivable.
- Both robots go down one row per step, so the row is shared: state `(row, col1, col2)`.
- Collision rule: if `col1 == col2`, the cell is counted once.
- Fill layers from the bottom row up; order inside a layer is free.
- Keep only two `cols × cols` layers to save memory.
- Cost = states × work per state = `rows · cols² · 9`.

```
┌─────────────────────────────────────────────────────────────────────────┐
│ here(r, c1, c2) = grid[r][c1] + (c1 != c2 ? grid[r][c2] : 0)            │
│                                                                         │
│ f(r, c1, c2) = here                                    if r = rows - 1  │
│ f(r, c1, c2) = here + max_{d1,d2 ∈ {-1,0,1}} f(r+1, c1+d1, c2+d2)        │
│                                                        otherwise        │
│ answer = f(0, 0, cols - 1)                                              │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## Exercises

**Exercise 1.** On Example 1, what is `dp[1][1][1]` (both robots in row 1, column 1)? Compute it by hand from layer 2.

<details>
<summary>Answer</summary>

`here = 5` (collision, counted once). Both robots can move to any column 0..2, so all 9 cells of layer 2 are neighbours; the largest is 13. `dp[1][1][1] = 5 + 13 = 18`, which matches the table.

</details>

**Exercise 2.** Suppose there were **three** robots, starting at the left, middle and right of the top row, all moving down one row per step. What is the state, how many combined choices are there, and what is the time complexity?

<details>
<summary>Answer</summary>

State `(row, col1, col2, col3)` — still one shared row, one column per robot, so 4D. Combined choices: `3 · 3 · 3 = 27`. Time `O(rows · cols³ · 27)`. The collision rule must count each distinct cell once (e.g. add `grid[row][col2]` only if `col2 != col1`, and `grid[row][col3]` only if `col3 != col1 && col3 != col2`).

</details>

**Exercise 3.** In the 2 × 2 grid `[[1,1],[1,1]]`, fill both layers of the table and read off the answer.

<details>
<summary>Answer</summary>

Layer 1 (bottom row `1 1`): `(0,0)=1, (0,1)=2, (1,0)=2, (1,1)=1`.
Layer 0: `(0,1)` has `here = 2`; every layer-1 cell is a neighbour; best = 2. So `dp[0][0][1] = 4`. The answer is 4.

</details>

**Exercise 4.** Why is it wrong to solve this problem by first finding the best single path for robot 1, removing its cherries, and then finding the best path for robot 2?

<details>
<summary>Answer</summary>

Robot 1's greedy choice can take cherries that robot 2 could have reached more easily, and push robot 1 away from cherries only robot 1 could reach. The robots' best plans depend on each other, which is exactly why both columns must be in the state. The next topic, [Cherry Pickup](../09-cherry-pickup/), shows a concrete grid where this greedy idea loses.

</details>

---

⬅ Back to [topic README](README.md) · ➡ Next lesson: [Cherry Pickup](../09-cherry-pickup/)
