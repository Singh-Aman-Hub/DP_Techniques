# 2D DP State Representation — Walkthrough

This is a **concept** topic. We do not solve a judge problem in full. Instead we use one tiny board as a picture, and on that picture we learn the five skills every 2D DP needs:

1. noticing that two things change,
2. naming the state `dp[r][c]` in plain English,
3. drawing the dependency arrows,
4. choosing a fill order,
5. indexing without off-by-one errors.

The example board is used in every section, so you can compare the approaches side by side.

---

## 1. Intuition (no code, no math — an everyday analogy)

Imagine a city laid out as a perfect rectangle of streets. You live at the top-left corner and your school is at the bottom-right corner. You are only allowed to walk **east** (right) or **south** (down), never back.

A friend asks: "How many different routes can you take to school?"

You could list every route by hand. That works for a tiny city. For a big city the routes explode, and you would walk the same streets in your head again and again.

Here is the DP way of thinking. Stand at any street corner. You arrived there **either from the corner directly north of you, or from the corner directly west of you**. There is no other way in. So:

> routes to *this* corner = routes to the corner above + routes to the corner on the left

To use that sentence you must say *which* corner you mean, and a corner in a grid city is named by two numbers: which street running east-west (the row) and which street running north-south (the column). One number is not enough: "corner 5" means nothing until you say how wide the city is and how you counted.

That is the whole topic in one picture: **when the place you are standing needs two numbers to describe, your table of answers needs two dimensions.**

---

## 2. Problem statement in our own words

Our illustration (a small version of [LeetCode 62 — Unique Paths](https://leetcode.com/problems/unique-paths/), solved in full in the [next topic](../02-unique-paths/)):

> A robot stands on the top-left cell of a board with `rows` rows and `cols` columns. Each move takes it one cell **right** or one cell **down**. Count how many different move sequences bring it to the bottom-right cell.

Throughout this walkthrough we use the board with **2 rows and 3 columns**:

```
          col 0   col 1   col 2
        +-------+-------+-------+
 row 0  | START |       |       |
        +-------+-------+-------+
 row 1  |       |       |  END  |
        +-------+-------+-------+
```

---

## 3. Examples

| Board (rows × cols) | Answer | The paths (R = right, D = down) |
|---------------------|--------|---------------------------------|
| 2 × 3 | 3 | `R R D`, `R D R`, `D R R` |
| 1 × 1 | 1 | the empty path: you are already at the end |
| 1 × 4 | 1 | `R R R` only — a single row has one path |
| 3 × 2 | 3 | `D D R`, `D R D`, `R D D` |

Notice 2 × 3 and 3 × 2 both give 3. Swapping rows and columns mirrors the board, so the count is the same. Keep that in mind: it is also why a bug that swaps `rows` and `cols` can hide in tests that only use square boards.

Let us trace the three paths of the 2 × 3 board. A `*` marks a visited cell:

```
   R R D            R D R            D R R
  * * *            * * .            * . .
  . . *            . * *            * * *
```

The same paths as cell sequences `(row, col)`:

```
 R R D :  (0,0) → (0,1) → (0,2) → (1,2)
 R D R :  (0,0) → (0,1) → (1,1) → (1,2)
 D R R :  (0,0) → (1,0) → (1,1) → (1,2)
```

---

## 4. Deriving the solution — the 7 questions

### Q1. What is changing?

Each move changes the robot's position. A position is a **row** `r` (0 … rows−1) and a **column** `c` (0 … cols−1). Moving right increases `c`; moving down increases `r`. They change **independently**: after two moves you may be at `(0,2)`, `(1,1)` or `(2,0)`.

So **two** quantities change. That is the signal for a 2D state.

#### Why not squeeze it into one number?

A tempting idea: "the state is the number of moves made so far, `k`". After `k` moves, though, the robot can be at several different cells (all cells with `r + c = k`). If the board had a wall or a cost on one of them, `dp[k]` could not tell which cell you are on, and the answer would be wrong. The number of moves is a *summary* of the position, not the position itself.

```
 cells reachable after k = 1 moves:  (0,1) and (1,0)   <- two different places
 cells reachable after k = 2 moves:  (0,2), (1,1)      <- still two places
```

Rule of thumb: **count the quantities you need to fully describe "where I am" in the decision process. That count is the number of table dimensions.**

### Q2. What does the state represent?

We can phrase it two ways. Both are correct; they lead to two different recursions (Section 5).

- **Backward state:** `ways(r, c)` = *the number of different paths from the start `(0, 0)` to the cell `(r, c)`*.
  The answer is `ways(rows−1, cols−1)`.
- **Forward state:** `toEnd(r, c)` = *the number of different paths from the cell `(r, c)` to the end `(rows−1, cols−1)`*.
  The answer is `toEnd(0, 0)`.

As a table, `dp[r][c]` has exactly the shape of the board. Row index first, column index second — always.

```
 dp has 2 rows and 3 columns, just like the board:

          c=0        c=1        c=2
 r=0  dp[0][0]   dp[0][1]   dp[0][2]
 r=1  dp[1][0]   dp[1][1]   dp[1][2]
```

### Q3. What are the choices?

- In the **backward** view we ask "what was the *last* move into `(r, c)`?" Either a **down** move from `(r−1, c)` or a **right** move from `(r, c−1)`.
- In the **forward** view we ask "what is the *next* move from `(r, c)`?" Either **right** to `(r, c+1)` or **down** to `(r+1, c)`.

### Q4. What is the recurrence?

We are counting, and the two choices lead to completely different paths (they differ in one move), so we **add**:

```
 backward:  ways(r, c)  = ways(r-1, c)  + ways(r, c-1)
                          (from above)    (from the left)

 forward:   toEnd(r, c) = toEnd(r, c+1) + toEnd(r+1, c)
                          (go right)      (go down)
```

### Q5. What are the base cases?

Backward:

- `ways(0, 0) = 1`. Standing on the start, there is exactly one path: the empty one.
- Any cell **outside** the board (`r < 0` or `c < 0`) has `ways = 0`. You cannot come from a cell that does not exist.

Forward:

- `toEnd(rows−1, cols−1) = 1`. Standing on the end, one (empty) path.
- Any cell outside the board (`r ≥ rows` or `c ≥ cols`) has `toEnd = 0`.

A consequence worth seeing: the whole **first row** has `ways = 1` (you can only have come from the left, all the way), and so does the whole **first column**. These are called the *base row* and *base column*.

### Q6. What is the order of computation?

This is the new skill of this topic, so it gets its own picture. Draw an arrow **from** each cell the formula reads **to** the cell being computed.

Backward recurrence `ways(r, c) = ways(r−1, c) + ways(r, c−1)`:

```
            c-1        c
          +-----+   +-----+
   r-1    |     |   |  A  |      A = (r-1, c)  "from above"
          +-----+   +--|--+
                       v
          +-----+   +-----+
   r      |  B  |-->| r,c |      B = (r, c-1)  "from the left"
          +-----+   +-----+
```

Every arrow points **down** or **right**. So a cell can be computed as soon as the cell above it and the cell to its left are done. Any order that respects this is valid:

```
 Row-major (most common)      Column-major              Anti-diagonals
 1  2  3                      1  3  5                   1  2  4
 4  5  6                      2  4  6                   3  5  6
```

(Numbers show the order in which cells are filled on our 2 × 3 board.)

An **invalid** order would be, for example, filling row 1 before row 0: `dp[1][0]` would read `dp[0][0]` before it has a value.

The forward recurrence reads `(r, c+1)` and `(r+1, c)`, so its arrows point **up** and **left**. Its fill order must run **from the bottom-right corner backwards**: rows from `rows−1` down to 0, columns from `cols−1` down to 0.

#### The three classic arrow shapes

Grid problems almost always use some of these three arrows. Learn to read them off a recurrence:

```
  up + left               up + left + diagonal         row below only
  (Unique Paths,          (Maximal Square,             (Triangle, Falling Path,
   Min Path Sum)           LCS, Edit Distance)          read from row r+1 or r-1)

    ↓                      ↘ ↓                          ↓ ↓ ↓   (three cells of the
  → X                      → X                            X      previous row)
```

The rule for all of them is the same: **fill every source of an arrow before its target.** With up/left/diagonal arrows, plain row-major order works.

### Q7. Can the state space be optimized?

Look at the arrows again: row `r` reads only row `r` (left neighbour) and row `r−1` (above). Rows `r−2`, `r−3`, … are never read again. So we only need **one row** of length `cols`, updated in place. We will do exactly that in [Unique Paths](../02-unique-paths/) and [Minimum Path Sum](../04-minimum-path-sum/). Section 8 below shows why it works.

---

## 5. Brute force: recursion

Two recursions, one for each state direction. Both are in [`solution/Demo.java`](solution/Demo.java).

### Backward: recurse from `(rows−1, cols−1)` toward `(0, 0)`

```java
// number of paths from (0,0) to (r,c)
static long waysBackward(int r, int c) {
    if (r < 0 || c < 0) return 0;     // fell off the top or left edge
    if (r == 0 && c == 0) return 1;   // reached the start
    return waysBackward(r - 1, c) + waysBackward(r, c - 1);
}
// answer = waysBackward(rows - 1, cols - 1)
```

### Forward: recurse from `(0, 0)` toward `(rows−1, cols−1)`

```java
// number of paths from (r,c) to the bottom-right cell
static long pathsForward(int r, int c, int rows, int cols) {
    if (r >= rows || c >= cols) return 0;            // fell off the bottom or right edge
    if (r == rows - 1 && c == cols - 1) return 1;    // reached the end
    return pathsForward(r, c + 1, rows, cols) + pathsForward(r + 1, c, rows, cols);
}
// answer = pathsForward(0, 0, rows, cols)
```

Notice what changes between them:

| | Backward | Forward |
|---|---|---|
| Meaning | paths **from start to here** | paths **from here to end** |
| Starting call | `(rows−1, cols−1)` | `(0, 0)` |
| Base case | the start cell | the end cell |
| Off-board test | `r < 0 \|\| c < 0` | `r >= rows \|\| c >= cols` |
| Needs `rows`, `cols` inside? | no | yes |

Neither is "more correct". The backward version often reads more naturally for "reach this cell" problems; the forward version is more natural when the answer depends on what is still *ahead* of you. In [Dungeon Game](../07-dungeon-game/) only the forward-from-the-cell direction works, so practise both now.

### Recursion tree on the 2 × 3 board (backward)

```
                         ways(1,2)
                    /                \
             ways(0,2)              ways(1,1)
             /      \              /         \
       ways(-1,2) ways(0,1)    ways(0,1)    ways(1,0)
          0       /    \       /    \        /      \
           ways(-1,1) ways(0,0) ways(-1,1) ways(0,0) ways(0,0) ways(1,-1)
              0         1          0         1         1        0

 Total = 1 + 1 + 1 = 3  ✓
```

`ways(0,1)` is computed **twice** and `ways(0,0)` three times. On a 2 × 3 board that is harmless. On a 15 × 15 board the number of paths is 40,116,600, and the plain recursion visits a leaf for every one of them. The running time is exponential, roughly `O(2^(rows+cols))`, because each call branches into two and the depth is `rows + cols − 2`.

Why is it slow? **Overlapping subproblems** ([Foundations 04](../../00-foundations/04-overlapping-subproblems/)): there are only `rows × cols` distinct cells, but the tree visits them over and over.

---

## 6. Memoization (top-down)

Because the state is two numbers, the cache is a **2D array** `memo[rows][cols]`. One cell of `memo` holds the answer for one cell of the board. We use `-1` to mean "not computed yet" (a path count is never negative).

```java
long[][] memo = new long[rows][cols];
for (long[] row : memo) Arrays.fill(row, -1);

static long waysMemo(int r, int c) {
    if (r < 0 || c < 0) return 0;
    if (r == 0 && c == 0) return 1;
    if (memo[r][c] != -1) return memo[r][c];          // cache hit
    memo[r][c] = waysMemo(r - 1, c) + waysMemo(r, c - 1);
    return memo[r][c];
}
```

Dry run on 2 × 3 (`·` = not computed yet):

```
 call ways(1,2)
   call ways(0,2)
     call ways(0,1)
       ways(0,0) = 1 (base)
       memo[0][1] = 0 + 1 = 1          memo:  ·  1  ·
                                              ·  ·  ·
     memo[0][2] = 0 + 1 = 1            memo:  ·  1  1
                                              ·  ·  ·
   call ways(1,1)
     ways(0,1)  -> CACHE HIT = 1      <- the repeated work from the tree is skipped
     call ways(1,0)
       ways(0,0) = 1 (base)
       memo[1][0] = 1 + 0 = 1          memo:  ·  1  1
                                              1  ·  ·
     memo[1][1] = 1 + 1 = 2            memo:  ·  1  1
                                              1  2  ·
   memo[1][2] = 1 + 2 = 3              memo:  ·  1  1
                                              1  2  3
 answer = 3
```

Each cell is computed once, so the work is `rows × cols` cells × O(1) each.

A common beginner mistake is to make the cache 1D with "some number" as the key, for example `memo[r + c]`. Two different cells such as `(0,2)` and `(1,1)` have the same `r + c = 2` but different answers (1 and 2). They would overwrite each other. **The cache must be indexed by the full state.**

---

## 7. Tabulation (bottom-up)

Tabulation fills the same table without recursion, in an order that respects the arrows. With up/left arrows, row-major order works.

```java
long[][] dp = new long[rows][cols];
for (int r = 0; r < rows; r++) {
    for (int c = 0; c < cols; c++) {
        if (r == 0 && c == 0) { dp[r][c] = 1; continue; }   // base case
        long fromAbove = (r > 0) ? dp[r - 1][c] : 0;         // off-board -> 0
        long fromLeft  = (c > 0) ? dp[r][c - 1] : 0;
        dp[r][c] = fromAbove + fromLeft;
    }
}
// answer = dp[rows - 1][cols - 1]
```

Filling the 2 × 3 table step by step:

```
 step 1: (0,0) base          step 2: (0,1) = 0 + 1     step 3: (0,2) = 0 + 1
   1  ·  ·                     1  1  ·                    1  1  1
   ·  ·  ·                     ·  ·  ·                    ·  ·  ·

 step 4: (1,0) = 1 + 0       step 5: (1,1) = 1 + 1     step 6: (1,2) = 1 + 2
   1  1  1                     1  1  1                    1  1  1
   1  ·  ·                     1  2  ·                    1  2  3
```

### The padding trick (an indexing alternative)

The `(r > 0) ? … : 0` checks are easy to get wrong. Another option is to allocate **one extra row and column** of zeros, so that "the cell above row 0" really exists and holds 0:

```
 dpPad has (rows+1) x (cols+1) cells. Board cell (r,c) lives at dpPad[r+1][c+1].

            pad   c=0  c=1  c=2
     pad  [  0     0    0    0 ]
     r=0  [  0     1    1    1 ]
     r=1  [  0     1    2    3 ]
```

Then the loop has no `if`s except the start cell, but **every access must add 1 to both indices**. Mixing padded and unpadded indices in one program is a classic bug. Pick one style per solution and stick to it.

### The forward table

The forward state fills from the bottom-right corner:

```
 toEnd table (paths from (r,c) to the END):
          c=0  c=1  c=2
   r=0     3    2    1
   r=1     1    1    1      <- base: the end cell is 1; the last row and last column are all 1

 fill order: r from 1 down to 0, c from 2 down to 0
```

Both tables agree on the final answer: `dp[1][2] = 3` and `toEnd[0][0] = 3`. In general `ways(r,c) × toEnd(r,c)` = the number of full paths that pass through `(r, c)`. On our board, cell `(1,1)`: 2 × 1 = 2, and indeed two of the three paths (`R D R`, `D R R`) pass through `(1,1)`.

---

## 8. Space optimization

Look at what the row-major loop reads while computing row `r`:

```
 row r-1 :  [ done ][ done ][ done ]    <- read (from above)
 row r   :  [ done ][  ??  ][      ]    <- read the left neighbour, write the current cell
 rows < r-1: never read again
```

So we can keep a single array `row[cols]`. Before we overwrite `row[c]`, it still holds the value from row `r−1` (the "from above" value), and `row[c−1]` already holds the new row-`r` value (the "from the left" value):

```java
long[] row = new long[cols];
row[0] = 1;
for (int r = 0; r < rows; r++)
    for (int c = 1; c < cols; c++)
        row[c] = row[c] + row[c - 1];   // old row[c] = from above, row[c-1] = from the left
```

On the 2 × 3 board:

```
 after r = 0:  [1, 1, 1]
 after r = 1:  [1, 2, 3]   -> answer row[2] = 3
```

Space falls from `rows × cols` to `cols`. We build this properly in the next topics.

---

## 9. Dry run on a full example

All approaches on the 2 × 3 board, cell by cell, in row-major order:

| Step | Cell | From above | From left | `dp` value | Memo note |
|------|------|-----------|-----------|-----------|-----------|
| 1 | (0,0) | — | — | 1 | base case |
| 2 | (0,1) | 0 (off board) | 1 | 1 | |
| 3 | (0,2) | 0 (off board) | 1 | 1 | |
| 4 | (1,0) | 1 | 0 (off board) | 1 | |
| 5 | (1,1) | 1 | 1 | 2 | memo: `(0,1)` was a cache hit here |
| 6 | (1,2) | 1 | 2 | **3** | answer |

[`solution/Demo.java`](solution/Demo.java) checks that backward recursion, forward recursion, memoization, tabulation and the one-row version all give 3 on this board (and agree on other boards too). It also contains a small checker that decides whether a proposed fill order is valid for the up/left arrows.

---

## 10. Complexity analysis

Let `R = rows`, `C = cols`.

| Approach | Time | Space | Why |
|----------|------|-------|-----|
| Plain recursion | exponential, about `O(2^(R+C))` | `O(R + C)` | Every call branches twice and the path length is `R + C − 2`; the stack depth is at most that length. |
| Memoization | `O(R · C)` | `O(R · C)` | There are `R · C` distinct states; each is computed once with O(1) work. The memo has one slot per state, plus `O(R + C)` stack. |
| Tabulation | `O(R · C)` | `O(R · C)` | Two nested loops visit each cell once with O(1) work; the table has `R · C` cells. |
| One row | `O(R · C)` | `O(C)` | Same loops; only one row is stored. |

The general rule for any 2D DP: **time = (number of states) × (work per state)**. Here `R · C` states × 2 reads = `O(R · C)`.

---

## 11. Why this approach works

Every path to `(r, c)` has a last move. That last move is either "down from `(r−1, c)`" or "right from `(r, c−1)`" — never both, never neither (except at the start). So the set of paths to `(r, c)` splits cleanly into two groups with no overlap, and each group has exactly as many paths as the cell it came from. Adding the two counts therefore counts every path exactly once.

The fill order is valid because every cell the formula reads is above or to the left, and row-major order finishes all of those first. The base cases cover exactly the cells the formula cannot handle (the start, and imaginary off-board cells).

---

## 12. Common mistakes

1. **Swapping `m` and `n`.** LeetCode often calls rows `m` and columns `n`. Write `new int[m][n]` and index `dp[r][c]` with `r < m`, `c < n`. Test with a non-square board (2 × 3 *and* 3 × 2); a square board hides the bug.
2. **Reading an off-board neighbour.** `dp[r-1][c]` when `r = 0` throws `ArrayIndexOutOfBoundsException`. Guard it, or use padding.
3. **Mixing padded and unpadded indices.** If `dp` is `(R+1) × (C+1)`, board cell `(r, c)` is `dp[r+1][c+1]` everywhere — including in the final `return`.
4. **A cache keyed by part of the state.** `memo[r]` or `memo[r + c]` mixes different subproblems. Use `memo[r][c]`.
5. **Wrong fill order for the recurrence you wrote.** A forward recurrence (reads `r+1`, `c+1`) filled top-left first reads zeros. Draw the arrows before writing the loops.
6. **Forgetting the start is a real path.** `ways(0,0)` is 1, not 0. With 0 everything becomes 0.

---

## 13. When to use this technique / how to recognize similar problems

Use a 2D state when, after listing **what changes** with each decision, you find two quantities that move independently. Signals:

- "grid", "matrix", "board", "move right/down", "from top-left to bottom-right";
- two strings or two arrays compared position by position;
- an index plus a remaining budget (capacity, moves, transactions);
- a range `[left, right]` that shrinks from both ends.

Then go through the same five steps: name the state, draw the board, draw the arrows, pick the fill order, set the border.

---

## 14. Summary and revision notes

- Count the quantities that describe "where I am". Two quantities → 2D table.
- Write the state as a sentence: "`dp[r][c]` = number of paths from the start to `(r, c)`".
- Draw arrows from the cells the formula reads to the cell it writes; fill sources before targets.
- Up/left (and diagonal) arrows → row-major order. Down/right arrows → fill from the bottom-right corner.
- Base cases live on the border: the start cell and the imaginary off-board cells.
- A row that depends only on itself and the row above can be stored in one 1D array.

```
+-------------------------------------------------------------+
|  ways(r, c) = ways(r-1, c) + ways(r, c-1)                   |
|  ways(0, 0) = 1,   ways(off-board) = 0                      |
|  answer = ways(rows-1, cols-1),  fill row by row, left->right|
+-------------------------------------------------------------+
```

---

## Exercises

**Exercise 1.** For a board with 3 rows and 3 columns, fill the backward table `ways(r, c)` by hand. What is the answer?

<details>
<summary>Answer</summary>

```
 1  1  1
 1  2  3
 1  3  6
```
The answer is `ways(2,2) = 6`. Each cell is the sum of the cell above and the cell to the left.
</details>

**Exercise 2.** A recurrence is `dp[r][c] = dp[r-1][c-1] + dp[r-1][c+1]` (it reads the two diagonal cells of the row above). Draw its arrows. Is row-major, left-to-right order valid? Is "fill row `r`, right-to-left" also valid?

<details>
<summary>Answer</summary>

Both arrows come from row `r − 1`. Nothing in row `r` is read while row `r` is being filled. So any order *inside* a row works, as long as rows are filled top to bottom: both left-to-right and right-to-left are valid. (Be careful with columns `0` and `cols − 1`, where one diagonal neighbour is off the board.)
</details>

**Exercise 3.** Someone writes `long[][] dp = new long[cols][rows];` and then uses `dp[r][c]`. For which boards does the program still work, and what happens on the others?

<details>
<summary>Answer</summary>

It works only when `rows == cols` (square boards). On a board with more rows than columns, `dp[r]` for `r ≥ cols` throws `ArrayIndexOutOfBoundsException`; with more columns than rows, `dp[r][c]` for `c ≥ rows` throws. This is why tests must include non-square boards.
</details>

**Exercise 4.** Write the forward table `toEnd(r, c)` for the 3 × 3 board, then compute `ways(1,1) × toEnd(1,1)`. What does that number mean?

<details>
<summary>Answer</summary>

```
 6  3  1
 3  2  1
 1  1  1
```
`ways(1,1) = 2` (from Exercise 1) and `toEnd(1,1) = 2`, so 2 × 2 = 4: four of the six paths pass through the centre cell. (The two that avoid it go along the outer edge: `R R D D` and `D D R R`.)
</details>

---

⬅ [README](README.md) · ➡ Next topic: [Unique Paths](../02-unique-paths/)
