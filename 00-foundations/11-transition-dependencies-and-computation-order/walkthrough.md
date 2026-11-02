# Transition Dependencies and Computation Order — Walkthrough

> Main running example: **counting right/down paths in a 3 × 3 grid** (answer `6`).
> Supporting examples: Climbing Stairs `n = 5` (answer `8`), Min Cost Climbing Stairs `[10, 15, 20]` (answer `15`) and the longest palindromic subsequence of `"bbab"` (answer `3`).
> Code: [`LoopOrderDemo.java`](solution/LoopOrderDemo.java), [`IntervalOrderPreview.java`](solution/IntervalOrderPreview.java).

---

## 1. Intuition

Think about getting dressed in the morning. Some things depend on others:

```
   socks ───► shoes
   shirt ───► tie ───► jacket
   trousers ─► belt
           └─► shoes
```

You may put on the shirt before or after the socks — the order of **unrelated** things is free. But you cannot put on shoes before socks. Any morning routine that respects all the arrows "works"; any routine that breaks even one arrow leaves you with socks over your shoes.

A DP table is the same. Each cell needs some other cells to be "put on" first. The recurrence tells you exactly which ones. Your loops are the morning routine. They may visit cells in many different orders, but **every cell must come after all the cells it reads**.

Notice two more things about the clothes picture:

1. There is no cycle. Nobody needs shoes on to put on socks *and* socks on to put on shoes. If there were a cycle, no order would work.
2. Some items need nothing (socks, shirt, trousers). These are the **base cases**: you can start with them immediately.

---

## 2. Problem statement in our own words

This is a concept topic, so the "problem" is a question we ask about **every** DP:

> Given a recurrence, in what order must the states be computed so that whenever we compute a state, every state it reads is already known?

Our main running example is the grid-path count you will meet properly in [Unique Paths (LeetCode 62)](https://leetcode.com/problems/unique-paths/): a robot starts in the top-left cell of a grid with `rows` rows and `cols` columns, may only move **right** or **down**, and we count how many different routes reach the bottom-right cell.

---

## 3. Examples

| Recurrence | Input | Correct answer | What a wrong order returns |
|------------|-------|----------------|----------------------------|
| Grid paths, row by row | 3 × 3 grid | `6` | `0` (filled from bottom-right) |
| Grid paths | 3 × 7 grid | `28` | — |
| Climbing Stairs | `n = 5` | `8` | `0` (filled right to left) |
| Min Cost Climbing Stairs, suffix form | `[10, 15, 20]` | `15` | `10` (filled left to right) |
| Longest palindromic subsequence | `"bbab"` | `3` | `2` (rows filled from the top) |

Every "wrong" value above is produced by **the same recurrence** in a different loop order. The formula did not change; only the order did. Keep that in mind: an order bug looks like a logic bug, but it is not one.

---

## 4. Deriving the solution — the 7 questions

We answer the seven questions for the main example, the 3 × 3 grid. Questions 1–5 are a quick recap of topics [09](../09-state-representation/) and [10](../10-state-transitions-and-base-cases/); **question 6 is the subject of this topic**, so it gets the most space.

### Q1. What is changing?

The robot's position: its row `r` and its column `c`. Each move increases one of them by 1.

### Q2. What does the state represent?

`paths[r][c]` = **the number of different right/down routes from the start cell (0, 0) to cell (r, c).**

> In [09 · State Representation](../09-state-representation/) the grid state was defined the other way round: routes **from** `(r, c)` **to** the bottom-right corner. Both definitions are correct and give `6` for a 3 × 3 grid, but their arrows point in opposite directions, so their loops run in opposite directions. Here we use the "from the start" form; Section 9.2 shows the same flip on a 1D problem.

### Q3. What are the choices?

Think about the **last** move into `(r, c)`. It came either

- **from above**, from `(r-1, c)`, by moving down, or
- **from the left**, from `(r, c-1)`, by moving right.

### Q4. What is the recurrence?

The two groups of routes do not overlap (their last move differs), so we **add** them:

```
paths[r][c] = paths[r-1][c]  +  paths[r][c-1]
              └ routes whose     └ routes whose
                last move was      last move was
                "down"             "right"
```

### Q5. What are the base cases?

- `paths[0][c] = 1` for every `c`: on the top row the only way is "right, right, right...".
- `paths[r][0] = 1` for every `r`: on the left column the only way is "down, down, down...".

These cells read nothing. They are the "socks and shirt" of our morning routine.

### Q6. What is the order of computation?

This is where we draw the arrows. Write one arrow **from each cell to every cell it reads**:

```
 dependency arrows for paths[r][c]           (arrow = "needs")

        (r-1, c)
           ▲
           │
 (r, c-1) ◄── (r, c)
```

Do that for every cell of the 3 × 3 grid:

```
   (0,0) ◄── (0,1) ◄── (0,2)
     ▲         ▲         ▲
     │         │         │
   (1,0) ◄── (1,1) ◄── (1,2)
     ▲         ▲         ▲
     │         │         │
   (2,0) ◄── (2,1) ◄── (2,2)
```

(Cells on the top row and left column are base cases. The arrows drawn into them are only there to show the shape; their values are known without reading anything.)

All arrows point **up** or **left**. So a cell must be computed after the cell above it and after the cell to its left. Two orders satisfy that:

```
 row by row (row-major)            column by column (column-major)
 1 → 2 → 3                         1   4   7
 4 → 5 → 6                         2   5   8
 7 → 8 → 9                         3   6   9
```

In row-major order, when we reach cell `5 = (1,1)`, cell `2` (above) and cell `4` (left) are done. In column-major order, when we reach cell `5 = (1,1)`, cell `4` (above) and cell `2` (left) are done. Both work.

A **wrong** order would be "start at the bottom-right and go backwards", because then `(2,2)` is computed first, and the cells it reads are still empty. Section 7 shows exactly what happens.

**The rule of thumb:** look at the indices on the right-hand side of the recurrence.

| The recurrence reads… | Loop must go… |
|-----------------------|---------------|
| smaller indices (`i-1`, `i-2`) | from small to large (left to right) |
| larger indices (`i+1`, `i+2`) | from large to small (right to left) |
| the previous row (`r-1`) and same row to the left (`c-1`) | rows top to bottom, columns left to right |
| shorter ranges inside `[i, j]` | by increasing length `j - i` |

### Q7. Can the state space be optimized?

Each cell reads only the row above and the cell to its left, so we only ever need **one previous row**. That is the idea of [15 · Space Optimization Basics](../15-space-optimization-basics/). Notice that space optimization is only possible **because** we fixed the order: row-major order is what makes "only the previous row" true.

---

## 5. Brute force: recursion

Recursion never has an order problem, and it is worth seeing why.

```java
static long paths(int r, int c) {
    if (r == 0 || c == 0) return 1;          // base cases
    return paths(r - 1, c) + paths(r, c - 1); // follow the arrows
}
```

When `paths(2, 2)` runs, it **calls** `paths(1, 2)` and **waits** until that returns. It is impossible to use an answer before it exists, because the call does not return until the answer is computed. In other words, the recursion walks the dependency graph for you, depth first, following arrows until it reaches base cases.

```
                      paths(2,2)
                    /            \
             paths(1,2)         paths(2,1)
             /       \           /       \
        paths(0,2) paths(1,1) paths(1,1) paths(2,0)
           =1      /     \     /     \      =1
              paths(0,1) paths(1,0) ...
                 =1        =1
```

`paths(1,1)` appears twice: the [overlapping subproblems](../04-overlapping-subproblems/) that make brute force slow. In bigger grids this tree grows exponentially.

**Why this matters here:** the recursion tree **is** the dependency graph, unrolled. Every edge in the tree is one of the arrows from Q6.

---

## 6. Memoization (top-down)

[Memoization](../06-memoization/) adds a cache to the recursion. The order is still decided by the recursion itself:

```java
static long paths(int r, int c, long[][] memo) {
    if (r == 0 || c == 0) return 1;
    if (memo[r][c] != 0) return memo[r][c];         // cache hit
    memo[r][c] = paths(r - 1, c, memo) + paths(r, c - 1, memo);
    return memo[r][c];
}
```

The order in which cells are **finished** (stored in the memo) for the 3 × 3 grid is:

```
call stack grows downward:            finished, in this order:
paths(2,2)
  paths(1,2)
    paths(0,2) = 1 (base)
    paths(1,1)
      paths(0,1) = 1 (base)
      paths(1,0) = 1 (base)
    paths(1,1) = 2  ◄── stored            1st: (1,1) = 2
  paths(1,2) = 1 + 2 = 3  ◄── stored      2nd: (1,2) = 3
  paths(2,1)
    paths(1,1) → cache hit, 2
    paths(2,0) = 1 (base)
  paths(2,1) = 2 + 1 = 3  ◄── stored      3rd: (2,1) = 3
paths(2,2) = 3 + 3 = 6  ◄── stored        4th: (2,2) = 6
```

The finishing order `(1,1), (1,2), (2,1), (2,2)` is a valid topological order that the recursion discovered on its own. It is neither row-major nor column-major. That is fine: **any** order that respects the arrows is correct.

So the order question only becomes **your** problem when you switch to tabulation.

---

## 7. Tabulation (bottom-up)

In [tabulation](../07-tabulation/) you write the loops, so you must pick an order from Q6. Here is row by row:

```java
long[][] paths = new long[rows][cols];
for (int r = 0; r < rows; r++) {
    for (int c = 0; c < cols; c++) {
        if (r == 0 || c == 0) paths[r][c] = 1;
        else paths[r][c] = paths[r - 1][c] + paths[r][c - 1];
    }
}
```

### Filling the 3 × 3 table, step by step (correct order)

`.` means "not filled yet" (it actually holds `0` in Java).

```
step 1-3: row 0 (base cases)       step 4: (1,0) base          step 5: (1,1) = 1 + 1
      c=0 c=1 c=2                        c=0 c=1 c=2                 c=0 c=1 c=2
 r=0   1   1   1                    r=0   1   1   1             r=0   1   1   1
 r=1   .   .   .                    r=1   1   .   .             r=1   1  [2]  .
 r=2   .   .   .                    r=2   .   .   .             r=2   .   .   .

step 6: (1,2) = 1 + 2              step 7: (2,0) base          step 8: (2,1) = 2 + 1
      c=0 c=1 c=2                        c=0 c=1 c=2                 c=0 c=1 c=2
 r=0   1   1   1                    r=0   1   1   1             r=0   1   1   1
 r=1   1   2  [3]                   r=1   1   2   3             r=1   1   2   3
 r=2   .   .   .                    r=2   1   .   .             r=2   1  [3]  .

step 9: (2,2) = 3 + 3
      c=0 c=1 c=2
 r=0   1   1   1
 r=1   1   2   3
 r=2   1   3  [6]   ◄── answer
```

At every step, the two cells being added (above and left) were already filled. That is what "the order follows the dependencies" looks like on paper.

### Now the wrong order, on the same grid

Suppose someone writes the loops backwards and seeds only the start cell:

```java
paths[0][0] = 1;
for (int r = rows - 1; r >= 0; r--)
    for (int c = cols - 1; c >= 0; c--) {
        if (r == 0 && c == 0) continue;
        long fromAbove = (r > 0) ? paths[r - 1][c] : 0;
        long fromLeft  = (c > 0) ? paths[r][c - 1] : 0;
        paths[r][c] = fromAbove + fromLeft;
    }
```

Trace it. The very first cell computed is `(2,2)`:

| Step | Cell | reads above | reads left | value written |
|------|------|-------------|------------|---------------|
| 1 | (2,2) | (1,2) = 0 *(not filled)* | (2,1) = 0 *(not filled)* | **0** |
| 2 | (2,1) | (1,1) = 0 *(not filled)* | (2,0) = 0 *(not filled)* | 0 |
| 3 | (2,0) | (1,0) = 0 *(not filled)* | — | 0 |
| 4 | (1,2) | (0,2) = 0 *(not filled)* | (1,1) = 0 *(not filled)* | 0 |
| 5 | (1,1) | (0,1) = 0 *(not filled)* | (1,0) = 0 *(not filled)* | 0 |
| 6 | (1,0) | (0,0) = 1 | — | 1 |
| 7 | (0,2) | — | (0,1) = 0 *(not filled)* | 0 |
| 8 | (0,1) | — | (0,0) = 1 | 1 |

Final table:

```
      c=0 c=1 c=2
 r=0   1   1   0
 r=1   1   0   0
 r=2   0   0   0   ◄── answer 0 (should be 6)
```

The program compiles, runs, and prints `0`. Nothing crashed. The recurrence is perfect. The **order** is wrong. `LoopOrderDemo.java` asserts this exact wrong value so you can see it yourself:

```
PASS  grid 3x3, row by row -> 6 (expected 6)
PASS  grid 3x3, WRONG reversed gives 0 -> 0 (expected 0)
```

> **Symptom to remember:** an answer of `0`, or one that is "suspiciously small", in a counting or max DP very often means you read cells before writing them.

---

## 8. Space optimization

Once the order is fixed, you can ask "how far back do the arrows reach?":

- Climbing Stairs, left to right: arrows reach back **2** cells, so 2 variables are enough.
- Grid, row by row: arrows reach back **one row**, so 1 or 2 rows are enough.
- Interval DP by length: a range reads ranges **one shorter**, so in some problems one diagonal is enough (rarely worth it).

The order and the optimization are linked: if you filled the grid column by column, you would keep one previous **column** instead of one previous row. The details are in [15 · Space Optimization Basics](../15-space-optimization-basics/).

---

## 9. Dry run on a full example — four orders side by side

Here are the four standard orders, each on its own example, each with the correct trace and the wrong-order trace.

### 9.1 Left to right — Climbing Stairs, `n = 5`

Recurrence (from [topic 10](../10-state-transitions-and-base-cases/)): `ways[i] = ways[i-1] + ways[i-2]`, with `ways[0] = ways[1] = 1`.

Arrows point to **smaller** indices:

```
 ways[5] ──needs──► ways[4], ways[3]
 ways[4] ──needs──► ways[3], ways[2]
 ways[3] ──needs──► ways[2], ways[1]
 ways[2] ──needs──► ways[1], ways[0]
 ways[1], ways[0]   need nothing (base cases)
```

Correct order, `i = 2, 3, 4, 5`:

| i | ways[i-1] | ways[i-2] | ways[i] |
|---|-----------|-----------|---------|
| 2 | 1 | 1 | 2 |
| 3 | 2 | 1 | 3 |
| 4 | 3 | 2 | 5 |
| 5 | 5 | 3 | **8** |

Wrong order, `i = 5, 4, 3, 2` (table starts as `[1, 1, 0, 0, 0, 0]`):

| i | ways[i-1] | ways[i-2] | ways[i] |
|---|-----------|-----------|---------|
| 5 | 0 *(not filled)* | 0 *(not filled)* | **0** |
| 4 | 0 *(not filled)* | 0 *(not filled)* | 0 |
| 3 | 0 *(not filled)* | 1 | 1 |
| 2 | 1 | 1 | 2 |

Answer `ways[5] = 0` instead of `8`.

### 9.2 Right to left — Min Cost Climbing Stairs, suffix form

You can define the state either from the start ("cheapest way to **reach** step `i`") or from the end ("cheapest way to get **from** step `i` to the top"). The second is called the **suffix form**:

`best[i]` = cheapest total cost to go from step `i` to the top, paying `cost[i]` for standing on step `i`.

```
best[i] = cost[i] + min(best[i+1], best[i+2])
best[n] = best[n+1] = 0          (already at or past the top)
answer  = min(best[0], best[1])  (we may start on step 0 or 1)
```

Now the arrows point to **larger** indices:

```
 best[0] ─► best[1] ─► best[2] ─► best[3]=0 ─► best[4]=0
```

So we fill **right to left**. For `cost = [10, 15, 20]`, `n = 3`:

| i | cost[i] | best[i+1] | best[i+2] | best[i] |
|---|---------|-----------|-----------|---------|
| 2 | 20 | 0 | 0 | 20 |
| 1 | 15 | 20 | 0 | 15 |
| 0 | 10 | 15 | 20 | 25 |

Answer `min(25, 15) = 15`: start on step 1, pay 15, jump two steps to the top.

Wrong order, left to right (every `best[i+1]`, `best[i+2]` is still `0` when read):

| i | cost[i] | best[i+1] | best[i+2] | best[i] |
|---|---------|-----------|-----------|---------|
| 0 | 10 | 0 *(not filled)* | 0 *(not filled)* | 10 |
| 1 | 15 | 0 *(not filled)* | 0 | 15 |
| 2 | 20 | 0 | 0 | 20 |

Answer `min(10, 15) = 10`. This wrong answer is dangerous because it **looks** plausible. There is no way to get to the top for 10, but nothing in the output tells you that.

**Lesson:** the loop direction is not a property of the problem. It is a property of **how you defined the state**. The same problem can be filled left to right (prefix state) or right to left (suffix state).

### 9.3 Row by row — the grid

Done in full in Section 7: correct order gives `6`, reversed order gives `0`. And column-by-column gives `6` too: `LoopOrderDemo.java` checks `3 × 7 → 28` with both row-major and column-major order.

### 9.4 By length — a preview of interval DP

Some problems are about a **range** `[i, j]` of the input, for example "the longest palindromic subsequence of the substring `s[i..j]`". A **subsequence** keeps characters in order but may skip some; a **palindrome** reads the same both ways.

`lps[i][j]` = length of the longest palindromic subsequence of `s[i..j]`.

```
if s[i] == s[j]:  lps[i][j] = 2 + lps[i+1][j-1]       (use both ends)
else:             lps[i][j] = max(lps[i+1][j],        (drop the left end)
                                  lps[i][j-1])        (drop the right end)
base: lps[i][i] = 1                                   (one character)
```

Every arrow goes from range `[i, j]` to a range that is **strictly shorter** and lies inside it:

```
              [i, j]
            /   |    \
     [i+1, j] [i+1,j-1] [i, j-1]
      len-1    len-2     len-1
```

So the natural order is **by length**: all ranges of length 1, then 2, then 3, and so on. For `s = "bbab"` (indices 0..3):

```
length 1 (base):  lps[0][0]=1  lps[1][1]=1  lps[2][2]=1  lps[3][3]=1

length 2:
  [0,1] "bb"  b==b → 2 + (empty) = 2
  [1,2] "ba"  b!=a → max(lps[2][2], lps[1][1]) = max(1,1) = 1
  [2,3] "ab"  a!=b → max(lps[3][3], lps[2][2]) = max(1,1) = 1

length 3:
  [0,2] "bba"  b!=a → max(lps[1][2], lps[0][1]) = max(1,2) = 2
  [1,3] "bab"  b==b → 2 + lps[2][2] = 2 + 1 = 3

length 4:
  [0,3] "bbab" b==b → 2 + lps[1][2] = 2 + 1 = 3   ◄── answer
```

The finished table (only `i <= j` is meaningful):

```
        j=0  j=1  j=2  j=3
 i=0     1    2    2    3
 i=1     .    1    1    3
 i=2     .    .    1    1
 i=3     .    .    .    1
```

Lengths are the **diagonals** of this table, so "by length" means "diagonal by diagonal".

Another valid order: `i` from the **bottom** row up, and `j` left to right. Row `i` reads row `i+1`, which is already done.

The wrong order is the "obvious" one, `i` from 0 upwards. When it computes `lps[0][3]` it needs `lps[1][2]`, which belongs to row 1 and has not been computed yet:

```
row 0 computed first:
  [0,1]  b==b → 2 + (empty)        = 2
  [0,2]  b!=a → max(lps[1][2]=0 *, lps[0][1]=2) = 2
  [0,3]  b==b → 2 + lps[1][2]=0 *  = 2     ◄── wrong answer (should be 3)
  (* = not filled yet)
```

`IntervalOrderPreview.java` asserts all three: by length → 3, bottom row first → 3, top row first → 2. You will use the by-length order constantly in [Interval DP](../../04-advanced-dp-patterns/03-interval-dp/).

---

## 10. Complexity analysis

The order you choose **does not change** the complexity. Each cell is visited once and does O(1) work in every order, valid or not.

| Example | States | Work per state | Time | Space |
|---------|--------|----------------|------|-------|
| Climbing Stairs | `n + 1` | O(1): one addition | O(n) | O(n) |
| Min Cost (suffix) | `n + 2` | O(1): one min, one addition | O(n) | O(n) |
| Grid paths | `rows × cols` | O(1): one addition | O(rows · cols) | O(rows · cols) |
| Longest palindromic subsequence | `n(n+1)/2` ranges with `i ≤ j` | O(1): one comparison and a max | O(n²) | O(n²) |

A wrong order is just as fast and just as wrong. Speed tells you nothing about correctness here. The full method for counting this way is in [12 · Time and Space Complexity in DP](../12-time-and-space-complexity-in-dp/).

---

## 11. Why this approach works

**Claim:** if the loops visit the states in an order where every state comes after all the states it reads, the table ends up correct.

**Argument in plain words (induction on the visiting order):**

1. The first state visited cannot read anything that is not yet filled (otherwise the order would be broken). So it must be a base case, and base cases are set correctly.
2. Suppose every state visited so far holds the right answer. The next state reads only states that come **earlier** in the order, which are all correct by assumption. The recurrence is correct (topic [10](../10-state-transitions-and-base-cases/)), so combining correct inputs gives a correct output.
3. So every state, one after another, is correct, including the final answer.

**Why such an order always exists:** a correct DP's dependency graph has **no cycle**. Every arrow moves to a "smaller" subproblem (fewer stairs left, a cell closer to the start, a shorter range). You cannot keep getting strictly smaller and come back to where you started. A graph without cycles always has at least one topological order: repeatedly take a state whose dependencies are all done.

**What a cycle would mean:** if `f(i)` needed `f(i+1)` *and* `f(i+1)` needed `f(i)`, there would be no valid order. Memoization would recurse forever and end in a `StackOverflowError`; tabulation would read a stale value. A cycle is a sign that the state is missing information. For example, "robot at cell (r, c), may move in all four directions" has cycles; adding "steps used so far" as a state parameter breaks them, because steps only increase.

---

## 12. Common mistakes

1. **Copying the loop direction from a previous problem.** Climbing Stairs goes left to right, but the suffix form of Min Cost Climbing Stairs goes right to left. Always re-read the indices of your own recurrence.
2. **Filling interval DP with `for i, for j` from 0.** Row `i` reads row `i+1`. Use "by length" or "`i` from the bottom".
3. **Reading the answer from the wrong corner.** If you filled right to left, the answer is near index 0, not index `n`. The answer cell is the one that matches the **original** question.
4. **Seeding base cases after the main loop.** If the loop reads `ways[1]` before the line `ways[1] = 1` runs, you get zeros. Put base cases first.
5. **Assuming "it ran without errors" means "the order is right".** Unfilled cells are `0`, not an error. Always test on a small input whose answer you computed by hand.
6. **Mixing two states in one array.** When you later shrink the grid table to one row (topic [15](../15-space-optimization-basics/)), the order *inside* the row starts to matter even more. The 0/1 knapsack in [03-subsequence-and-knapsack-dp](../../03-subsequence-and-knapsack-dp/02-zero-one-knapsack/) is the classic example.

---

## 13. When to use this technique / how to recognize similar problems

Use it **every time you write a bottom-up loop**. A 30-second check:

1. Write the recurrence.
2. Circle every state on the right-hand side.
3. For each, ask: is its index smaller, larger, or a shorter range than the left-hand side?
4. Choose loops so those circled states come earlier.
5. Dry-run the first two non-base cells by hand and confirm everything they read is already filled.

Patterns you will meet:

| Shape of recurrence | Order | Where you will see it |
|---------------------|-------|-----------------------|
| `dp[i]` from `dp[i-1]`, `dp[i-2]`, ... | left to right | [01-one-dimensional-dp](../../01-one-dimensional-dp/02-climbing-stairs/) |
| `dp[i]` from `dp[i+1]`, `dp[i+k]` | right to left | "from here to the end" problems |
| `dp[r][c]` from `dp[r-1][..]`, `dp[r][c-1]` | row by row | [02-two-dimensional-and-grid-dp](../../02-two-dimensional-and-grid-dp/02-unique-paths/) |
| `dp[i][w]` from `dp[i-1][..]` | item by item | [0/1 Knapsack](../../03-subsequence-and-knapsack-dp/02-zero-one-knapsack/) |
| `dp[i][j]` from shorter `[i', j']` inside | by length | [Interval DP](../../04-advanced-dp-patterns/03-interval-dp/) |
| `dp[node]` from `dp[child]` | children before parents (post-order) | [DP on Trees](../../04-advanced-dp-patterns/07-dp-on-trees/) |

---

## 14. Summary and revision notes

- A **dependency** is "state A reads state B". Draw one arrow per term of the recurrence.
- A correct DP's dependency graph is a **DAG**: no cycles, because subproblems always get smaller.
- **Memoization** finds a valid order automatically; **tabulation** requires you to choose one.
- **Rule:** every cell must be filled after every cell it reads.
- Reads smaller indices → left to right. Reads larger indices → right to left. Reads previous row → row by row. Reads shorter ranges → by length.
- More than one order can be valid (row-major and column-major for the grid).
- A wrong order gives a **wrong answer, not an error**, often `0` or something too small.

```
┌──────────────────────────────────────────────────────────────┐
│  For every term dp[X] on the right of   dp[S] = f(dp[X], ...) │
│  the loop must visit X  BEFORE  S.                            │
│                                                               │
│  i-1, i-2      → for i = 0 .. n                               │
│  i+1, i+2      → for i = n .. 0                               │
│  r-1, c-1      → for r = 0 .. R { for c = 0 .. C }            │
│  [i+1..j-1]    → for len = 1 .. n { for i { j = i+len-1 } }   │
└──────────────────────────────────────────────────────────────┘
```

---

## 15. Check your understanding

**Exercise 1.** A recurrence is `dp[i] = max(dp[i+1], nums[i] + dp[i+3])` with `dp[n] = dp[n+1] = dp[n+2] = 0`. Which way must the loop run, and where is the answer?

<details><summary>Answer</summary>

Both terms read **larger** indices (`i+1`, `i+3`), so fill **right to left**: `for (int i = n - 1; i >= 0; i--)`. The state is "best result from index `i` to the end", so the answer for the whole array is `dp[0]`. (This is exactly the shape of LeetCode 2140, Solving Questions With Brainpower.)

</details>

**Exercise 2.** For the grid-path recurrence `paths[r][c] = paths[r-1][c] + paths[r][c-1]`, is this order valid: "visit cells by **anti-diagonal** `d = r + c` from `0` up to `rows + cols - 2`, and inside each diagonal in any order"? Why?

<details><summary>Answer</summary>

Yes. The cell above, `(r-1, c)`, has diagonal `d - 1`, and the cell to the left, `(r, c-1)`, also has diagonal `d - 1`. Both are on the **previous** diagonal, which is finished before diagonal `d` starts. Cells on the same diagonal never read each other, so their internal order does not matter. (This order is useful in parallel computing, because a whole diagonal can be computed at once.)

</details>

**Exercise 3.** Someone fills Climbing Stairs correctly from left to right, but writes the base cases `ways[0] = 1; ways[1] = 1;` **after** the loop. What does the code return for `n = 5`?

<details><summary>Answer</summary>

The loop runs with `ways = [0, 0, 0, 0, 0, 0]`, so every `ways[i]` for `i ≥ 2` is `0 + 0 = 0`. Then the base cases are written, but nobody reads them again. The answer `ways[5]` is **0**. Base cases are part of the order: they must be filled first.

</details>

**Exercise 4.** In the longest-palindromic-subsequence table, which of these orders are valid? (a) `i` from `n-1` down to `0`, `j` from `i` up to `n-1`. (b) `j` from `0` up to `n-1`, `i` from `j` down to `0`. (c) `i` from `0` up to `n-1`, `j` from `n-1` down to `i`.

<details><summary>Answer</summary>

- **(a) valid.** Row `i+1` is finished before row `i`, and `lps[i][j-1]` is earlier in the same row.
- **(b) valid.** Column `j-1` is finished before column `j`. Inside column `j`, `i` goes downward, so `lps[i+1][j]` (same column, larger `i`) is computed before `lps[i][j]`. `lps[i+1][j-1]` is in the previous column.
- **(c) invalid.** It still computes row 0 before row 1, so `lps[0][3]` reads `lps[1][2]` before it exists, exactly the bug from Section 9.4.

</details>

---

⬅ Previous: [10 · State Transitions and Base Cases](../10-state-transitions-and-base-cases/) · ➡ Next: [12 · Time and Space Complexity in DP](../12-time-and-space-complexity-in-dp/) · 🏠 [README](README.md)
