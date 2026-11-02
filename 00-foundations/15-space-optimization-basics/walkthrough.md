# Space Optimization Basics — Walkthrough

> Main running example: **counting right/down paths in a 3 × 3 grid** (answer `6`), shrunk from a full table to two rows and then to one row.
> Supporting examples: Climbing Stairs `n = 5` (answer `8`) with two variables, and the 0/1 knapsack `weights = [1, 3, 4]`, `values = [15, 20, 30]`, capacity `4` (answer `35`) with one array.
> Code: [`StairsTwoVariables.java`](solution/StairsTwoVariables.java), [`GridPathsRolling.java`](solution/GridPathsRolling.java), [`KnapsackOneRow.java`](solution/KnapsackOneRow.java), [`ReconstructionNeedsTable.java`](solution/ReconstructionNeedsTable.java).

---

## 1. Intuition

Imagine you are walking up a long staircase and counting something as you go. A friend tells you: "to know the count at the next step, you only need the counts at the **last two** steps." Would you write down the count for every step you ever passed, all the way back to the ground floor? No. You would keep two numbers in your head and update them as you climb.

```
 step:     0   1   2   3   4   5
 count:    1   1   2   3   5   8
                       └───┴── only these two are needed for step 5
           └───┴───┴── never read again: forget them
```

That is all space optimization is: **remember only what you will read again.**

The skill is in answering two questions honestly:

1. **What does the recurrence read?** (Its *read window*.)
2. **If I overwrite memory, is the value I read still the one I need?** (The order question from [topic 11](../11-transition-dependencies-and-computation-order/), now even stricter.)

---

## 2. Problem statement in our own words

This is a concept topic. The question we ask of any working tabulated DP is:

> Given a correct table solution, which cells are still needed at each moment, and how can we store only those, without changing any answer?

Our main example is the grid-path count from [Unique Paths (LeetCode 62)](https://leetcode.com/problems/unique-paths/): a robot in the top-left cell of a grid may move only right or down; count the routes to the bottom-right cell.

> Note: [topic 09](../09-state-representation/) defined the grid state "from `(r, c)` **to the end**"; topics 11–12 and this one use the mirror form "from the start **to** `(r, c)`", which fills top-down. Both give the same answers; this form makes "the row above" the one we read.

---

## 3. Examples

| Problem | Input | Answer | Full table memory | Optimized memory |
|---------|-------|--------|-------------------|------------------|
| Climbing Stairs | `n = 5` | 8 | 6 cells | 2 variables |
| Climbing Stairs | `n = 10` | 89 | 11 cells | 2 variables |
| Grid paths | 3 × 3 | 6 | 9 cells | 3 cells (one row) |
| Grid paths | 3 × 7 | 28 | 21 cells | 7 cells (one row) |
| Grid paths | 10 × 10 | 48 620 | 100 cells | 10 cells |
| 0/1 knapsack | capacity 4 | 35 | 4 × 5 = 20 cells | 5 cells |
| 0/1 knapsack, **forward loop (bug)** | capacity 4 | **60** (wrong) | — | 5 cells |

Every optimized version returns exactly the same answer as its full table, except the last row, which is the bug this lesson teaches you to avoid.

---

## 4. Deriving the solution — the 7 questions

We answer the seven questions for the main example, the 3 × 3 grid. Questions 1–6 recap earlier topics; **question 7 is the subject of this topic.**

### Q1. What is changing?

The robot's row `r` and column `c`.

### Q2. What does the state represent?

`paths[r][c]` = **the number of different right/down routes from the start cell (0, 0) to cell (r, c).**

### Q3. What are the choices?

The last move into `(r, c)` came either from **above** `(r-1, c)` or from the **left** `(r, c-1)`.

### Q4. What is the recurrence?

```
paths[r][c] = paths[r-1][c] + paths[r][c-1]
```

The two groups of routes are different (different last move), so we add them.

### Q5. What are the base cases?

`paths[0][c] = 1` (top row) and `paths[r][0] = 1` (left column): only one straight route reaches each of them.

### Q6. What is the order of computation?

Row by row, left to right inside each row (see [topic 11](../11-transition-dependencies-and-computation-order/)).

### Q7. Can the state space be optimized?

Draw the **read window** of one cell:

```
           column c-1   column c
 row r-1      .        [ above ]      ← read
 row r     [ left ]     (r, c)        ← being computed
```

Cell `(r, c)` reads only **row `r-1`** and **row `r`**. It never reads row `r-2` or anything older. So:

- While computing row `r`, rows `0 .. r-2` are dead. We need at most **two rows** → `O(cols)` space instead of `O(rows · cols)`.
- Look closer: from row `r-1` we read only column `c`, and from row `r` only column `c-1`. If we overwrite a **single row** from left to right, then at the moment we update position `c`:
  - position `c` still holds the old value, which is the cell **above**;
  - position `c-1` already holds the new value, which is the cell to the **left**.

  That is exactly what the recurrence needs, so **one row** is enough.

Answer to Q7: **one row of `cols` cells**, updated left to right with `row[c] = row[c] + row[c-1]`. Time is unchanged.

---

## 5. Brute force: recursion

```java
static long paths(int r, int c) {
    if (r == 0 || c == 0) return 1;
    return paths(r - 1, c) + paths(r, c - 1);
}
```

Brute force has no table, so there is nothing to shrink. Its memory is the recursion stack, `O(rows + cols)` deep (see [topic 12](../12-time-and-space-complexity-in-dp/)), but its time is exponential, so it is not a serious option. Space optimization is a step that comes **after** you have a fast DP.

---

## 6. Memoization (top-down)

Memoization fills its cache in whatever order the recursion happens to visit states. You cannot easily say "this row is finished, throw it away", because a later call might still ask for it. That is why **space optimization is almost always done on the tabulated version**. If you have a memoized solution and need less memory, first convert it with [14 · Converting Memoization to Tabulation](../14-converting-memoization-to-tabulation/).

---

## 7. Tabulation (bottom-up)

The full table we start from (from [topic 11](../11-transition-dependencies-and-computation-order/)):

```java
long[][] paths = new long[rows][cols];
for (int r = 0; r < rows; r++)
    for (int c = 0; c < cols; c++)
        paths[r][c] = (r == 0 || c == 0) ? 1 : paths[r - 1][c] + paths[r][c - 1];
return paths[rows - 1][cols - 1];
```

```
      c=0 c=1 c=2
 r=0   1   1   1
 r=1   1   2   3
 r=2   1   3   6   ◄── answer
```

Nine cells. Now look at **when each row is last read**:

```
 row 0 is read while computing row 1, then never again
 row 1 is read while computing row 2, then never again
 row 2 holds the answer
```

So at any moment only two rows are "alive". That observation is the whole optimization.

---

## 8. Space optimization

### 8.1 Warm-up: two variables for Climbing Stairs

`ways[i] = ways[i-1] + ways[i-2]`. Read window: **the two previous cells**.

```
 ways[]:  1   1   2   3   5   8
                      ▲   ▲   ▲
                twoBack oneBack current
```

```java
long twoBack = 1;  // ways[i-2]
long oneBack = 1;  // ways[i-1]
for (int i = 2; i <= n; i++) {
    long current = oneBack + twoBack;
    twoBack = oneBack;   // slide: the old "one back" becomes "two back"
    oneBack = current;   // the new value becomes "one back"
}
return oneBack;
```

Trace for `n = 5`:

| i | twoBack (before) | oneBack (before) | current | twoBack (after) | oneBack (after) |
|---|------------------|------------------|---------|-----------------|-----------------|
| 2 | 1 | 1 | 2 | 1 | 2 |
| 3 | 1 | 2 | 3 | 2 | 3 |
| 4 | 2 | 3 | 5 | 3 | 5 |
| 5 | 3 | 5 | **8** | 5 | 8 |

The **order of the two assignments matters**. If you write `oneBack = current;` first and then `twoBack = oneBack;`, both variables end up equal to `current` and the old value of `oneBack` is lost:

| i | current | oneBack, twoBack after (wrong order) |
|---|---------|---------------------------------------|
| 2 | 1 + 1 = 2 | 2, 2 |
| 3 | 2 + 2 = 4 | 4, 4 |
| 4 | 4 + 4 = 8 | 8, 8 |
| 5 | 8 + 8 = 16 | 16, 16 |

The wrong order returns `16` instead of `8`. Rule: **move the oldest value first**, like people stepping back in a queue.

Space: `O(n)` → `O(1)`. Time: still `O(n)`. `StairsTwoVariables.java` checks the table and the two-variable versions on `n = 0, 1, 5, 10, 45`.

### 8.2 Grid: two rows

Keep `previous` (row `r-1`) and `current` (row `r`):

```java
long[] previous = new long[cols];
Arrays.fill(previous, 1);                 // row 0
for (int r = 1; r < rows; r++) {
    long[] current = new long[cols];
    current[0] = 1;                       // column 0
    for (int c = 1; c < cols; c++)
        current[c] = previous[c] + current[c - 1];   // above + left
    previous = current;                   // move down one row
}
return previous[cols - 1];
```

Trace on 3 × 3:

```
 start:              previous = [1, 1, 1]          (row 0)

 r = 1:  current[0] = 1
         current[1] = previous[1] + current[0] = 1 + 1 = 2
         current[2] = previous[2] + current[1] = 1 + 2 = 3
                     previous = [1, 2, 3]          (row 1)

 r = 2:  current[0] = 1
         current[1] = previous[1] + current[0] = 2 + 1 = 3
         current[2] = previous[2] + current[1] = 3 + 3 = 6
                     previous = [1, 3, 6]          (row 2)  → answer 6
```

Space: `O(rows · cols)` → `O(cols)` (two rows, so `2 · cols` cells).

### 8.3 Grid: one row, updated in place

Now merge `previous` and `current` into one array `row`. Before the update at position `c`, `row[c]` is still the **old** value (from the row above). `row[c-1]` has already been updated (it is the **new** left neighbor).

```java
long[] row = new long[cols];
Arrays.fill(row, 1);                      // row 0
for (int r = 1; r < rows; r++)
    for (int c = 1; c < cols; c++)
        row[c] = row[c] + row[c - 1];     // old row[c] = above, new row[c-1] = left
return row[cols - 1];
```

Trace on 3 × 3. `[x]` marks the cell just updated; cells to its right still hold the previous row.

```
 start (row 0):           [1, 1, 1]

 r = 1, c = 1:  1 + 1  →  [1, [2], 1]        row[2] is still row 0's value
 r = 1, c = 2:  1 + 2  →  [1, 2, [3]]        now the array IS row 1

 r = 2, c = 1:  2 + 1  →  [1, [3], 3]        row[2] = 3 is still row 1's value
 r = 2, c = 2:  3 + 3  →  [1, 3, [6]]        answer 6
```

At every step, the array is half "new row" (left of `c`) and half "old row" (from `c` on). That mix is exactly the read window from Q7.

Space: `O(cols)` with just `cols` cells.

Could the inner loop run right to left instead? No: then `row[c-1]` would still be the **old** value (the cell above-left), not the left neighbor, and the answer would be wrong. With one array, **the loop direction decides whether you read old or new values.** Hold on to that idea; the knapsack depends on it.

### 8.4 Knapsack preview: one array, loop in reverse

The 0/1 knapsack table (full lesson in [0/1 Knapsack](../../03-subsequence-and-knapsack-dp/02-zero-one-knapsack/)):

`best[i][w]` = best value using only the first `i` items with capacity `w`.

```
best[i][w] = max( best[i-1][w],                                  ← skip item i-1
                  values[i-1] + best[i-1][w - weights[i-1]] )    ← take it (if it fits)
```

Read window: **only row `i-1`**, at column `w` and at the **smaller** column `w - weight`.

```
            column w-weight        column w
 row i-1       [ take ]           [ skip ]     ← both from the OLD row
 row i                             (i, w)
```

Unlike the grid, *both* reads must come from the **old** row. With one array, a position still holds the old value only until we overwrite it. So we must overwrite **large `w` first**: when we update `best[w]`, the smaller position `best[w - weight]` has not been touched in this round, so it is still old.

```java
int[] best = new int[capacity + 1];
for (int i = 0; i < n; i++)
    for (int w = capacity; w >= weights[i]; w--)          // HIGH to LOW
        best[w] = Math.max(best[w], values[i] + best[w - weights[i]]);
```

Trace on `weights = [1, 3, 4]`, `values = [15, 20, 30]`, capacity `4`:

```
 start:                          w:  0   1   2   3   4
                                     0   0   0   0   0

 item 0 (w=1, v=15), w = 4,3,2,1:
   w=4: max(0, 15 + best[3]=0) = 15
   w=3: max(0, 15 + best[2]=0) = 15
   w=2: max(0, 15 + best[1]=0) = 15
   w=1: max(0, 15 + best[0]=0) = 15
                                     0  15  15  15  15

 item 1 (w=3, v=20), w = 4,3:
   w=4: max(15, 20 + best[1]=15) = 35
   w=3: max(15, 20 + best[0]=0)  = 20
                                     0  15  15  20  35

 item 2 (w=4, v=30), w = 4:
   w=4: max(35, 30 + best[0]=0)  = 35
                                     0  15  15  20  35   → answer 35
```

Each line equals the corresponding row of the full 2D table: the one array really is "the current row".

### 8.5 The forward-loop bug

Now run the **same** code with the capacity loop going low to high:

```java
for (int w = weights[i]; w <= capacity; w++)              // LOW to HIGH — wrong for 0/1
    best[w] = Math.max(best[w], values[i] + best[w - weights[i]]);
```

```
 item 0 (w=1, v=15), w = 1,2,3,4:
   w=1: max(0, 15 + best[0]=0)   = 15
   w=2: max(0, 15 + best[1]=15)  = 30    ← best[1] was JUST set using item 0
   w=3: max(0, 15 + best[2]=30)  = 45    ← item 0 used a third time
   w=4: max(0, 15 + best[3]=45)  = 60    ← and a fourth
                                     0  15  30  45  60

 item 1 (w=3, v=20):  w=3: max(45, 20+0)  = 45;   w=4: max(60, 20+15) = 60
 item 2 (w=4, v=30):  w=4: max(60, 30+0)  = 60
                                     0  15  30  45  60   → answer 60 (WRONG)
```

`60` means "item 0 four times", which the 0/1 rule forbids. The forward loop reads values that were already updated with the **current** item, so an item can be reused. `KnapsackOneRow.java` asserts both `35` (reverse) and `60` (forward).

Fun fact: that "bug" is exactly the right code for the **unbounded** knapsack, where each item may be used any number of times ([Unbounded Knapsack](../../03-subsequence-and-knapsack-dp/07-unbounded-knapsack/), [Coin Change](../../03-subsequence-and-knapsack-dp/08-coin-change/)). The direction of one loop is the difference between two different problems.

### 8.6 When you can't (or shouldn't) optimize: reconstruction

The one-row knapsack tells you the best **value**, 35. Suppose the question is "**which** items?". To answer, walk back through the full table from the answer cell:

```
 full table best[i][w]:     w=0  w=1  w=2  w=3  w=4
   i=0 (no items)            0    0    0    0    0
   i=1 (+ item 0)            0   15   15   15   15
   i=2 (+ item 1)            0   15   15   20   35
   i=3 (+ item 2)            0   15   15   20   35
```

| at | compare | meaning | move to |
|----|---------|---------|---------|
| `best[3][4] = 35` | `best[2][4] = 35`, equal | item 2 not needed | `i = 2, w = 4` |
| `best[2][4] = 35` | `best[1][4] = 15`, different | item 1 **taken** | `i = 1, w = 4 - 3 = 1` |
| `best[1][1] = 15` | `best[0][1] = 0`, different | item 0 **taken** | `i = 0, w = 0` |

Chosen items: **0 and 1** (weights 1 + 3 = 4, values 15 + 20 = 35). `ReconstructionNeedsTable.java` does this walk.

The walk visits **every row**, in reverse. If we had kept only one row, rows 0–2 would be gone and the walk would be impossible. So:

- If the problem asks only for the **value** (max, min, count, true/false), optimize freely.
- If it asks for the **choices** (which items, the actual path, the actual subsequence), keep the full table, or store a separate "choice" record per state, which costs the same memory.

(There are advanced techniques that reconstruct with less memory, such as divide-and-conquer reconstruction, but they are well beyond this module.)

Other reasons not to optimize: it makes code harder to read and debug, and in interviews it is often better to present the clear table first and optimize only when asked.

---

## 9. Dry run on a full example — 3 × 7 grid, three ways

`GridPathsRolling.java` runs all three versions on the same inputs. Here is the 3 × 7 grid (answer `28`).

**Full table (21 cells):**

```
      c=0 c=1 c=2 c=3 c=4 c=5 c=6
 r=0   1   1   1   1   1   1   1
 r=1   1   2   3   4   5   6   7
 r=2   1   3   6  10  15  21  28
```

**Two rows (14 cells):**

| after row | previous |
|-----------|----------|
| 0 | `[1, 1, 1, 1, 1, 1, 1]` |
| 1 | `[1, 2, 3, 4, 5, 6, 7]` |
| 2 | `[1, 3, 6, 10, 15, 21, 28]` |

**One row (7 cells)**, row 2 in detail. Before it starts, `row = [1, 2, 3, 4, 5, 6, 7]` (row 1).

| c | old row[c] (above) | row[c-1] (left, new) | new row[c] | array afterwards |
|---|--------------------|----------------------|------------|------------------|
| 1 | 2 | 1 | 3 | `[1, 3, 3, 4, 5, 6, 7]` |
| 2 | 3 | 3 | 6 | `[1, 3, 6, 4, 5, 6, 7]` |
| 3 | 4 | 6 | 10 | `[1, 3, 6, 10, 5, 6, 7]` |
| 4 | 5 | 10 | 15 | `[1, 3, 6, 10, 15, 6, 7]` |
| 5 | 6 | 15 | 21 | `[1, 3, 6, 10, 15, 21, 7]` |
| 6 | 7 | 21 | **28** | `[1, 3, 6, 10, 15, 21, 28]` |

All three agree: `28`. The program checks `3×3 → 6`, `3×7 → 28`, `2×3 → 3`, `1×1 → 1` and `10×10 → 48620` for every version.

---

## 10. Complexity analysis

| Problem | Version | Time | Space | Why |
|---------|---------|------|-------|-----|
| Stairs | table | O(n) | O(n) | `n + 1` cells, O(1) each |
| Stairs | two variables | O(n) | O(1) | still `n` additions; only 2 numbers kept |
| Grid `R × C` | table | O(R · C) | O(R · C) | one cell per position |
| Grid `R × C` | two rows | O(R · C) | O(C) | every cell still computed; 2 rows kept |
| Grid `R × C` | one row | O(R · C) | O(C) | same, `C` cells kept |
| Knapsack `n`, `W` | table | O(n · W) | O(n · W) | `(n+1)(W+1)` cells |
| Knapsack `n`, `W` | one row, reverse | O(n · W) | O(W) | every cell still computed; `W + 1` kept |
| Knapsack + reconstruction | table + walk back | O(n · W) + O(n) | O(n · W) | the walk needs every row |

Notice the time column never changes. **Space optimization trades nothing in time; it only forgets states that will not be read.** (It can even be slightly faster in practice, because a small array stays in the CPU cache, but the big-O is the same.)

A tip for the grid: if `cols` is much larger than `rows`, fill column by column and keep one **column** instead: `O(min(R, C))` space.

---

## 11. Why this approach works

**Claim:** if we discard a state only after the last moment it is read, every remaining read sees the same value the full table would have shown, so the answer is identical.

**Argument in plain words:**

1. The full table is correct ([topic 14](../14-converting-memoization-to-tabulation/) and [topic 11](../11-transition-dependencies-and-computation-order/)).
2. The optimized version performs the **same** computations in the **same** order; only the storage location of each value changes.
3. A computation gives the same result as long as each value it reads is the same. So we must check, for each read, that the location still holds the intended value:
   - **Two variables:** `twoBack` and `oneBack` always hold `ways[i-2]` and `ways[i-1]` at the start of step `i` (checked in the trace, and kept true by moving the older value first).
   - **One row (grid):** at position `c`, `row[c]` has not yet been written in this row (so it is the cell above) and `row[c-1]` has been (so it is the left cell).
   - **One row (knapsack), reverse:** at capacity `w`, the position `w - weight < w` has not been written yet in this round, so it holds the old row's value, which the 0/1 recurrence requires.
4. In the forward knapsack loop, point 3 fails: `w - weight` was already written in this round, so the read sees a value that includes the current item. That is why the result changes.

---

## 12. Common mistakes

1. **Updating rolling variables in the wrong order.** `oneBack = current; twoBack = oneBack;` loses a value (Section 8.1: 16 instead of 8). Move the oldest first, or use a temporary variable.
2. **Forward loop in the 0/1 knapsack.** Lets items be reused (Section 8.5: 60 instead of 35). 0/1 → capacity high to low; unbounded → low to high.
3. **Forgetting to reset or re-initialize the boundary** of the new row. In the two-row grid, `current[0] = 1` must be set for every row; in other problems the first column may need a different value per row.
4. **Returning the wrong variable.** After the two-variable loop the answer is `oneBack`, not `current` (which is out of scope) or `twoBack`. Check small inputs like `n = 0` and `n = 1`, where the loop does not run at all.
5. **Aliasing two rows by accident.** `previous = current;` followed by writing into `current` without creating a new array means both names point to the same array. Either allocate a new `current` each row or swap the two references deliberately.
6. **Optimizing when the answer needs reconstruction.** If the problem asks for the path or the chosen items, keep the table (Section 8.6).
7. **Optimizing too early.** Get the full table right first, test it, then shrink it and check that both versions agree on the same tests.

---

## 13. When to use this technique / how to recognize similar problems

After you have a correct table, ask:

1. **Which earlier states does the recurrence read?** Write the read window.
2. **Is it a fixed, small window?** (last 2 cells, last `k` cells, previous row) → keep only that window.
3. **With one array, does each read need the OLD value or the NEW value?** Choose the loop direction to match.
4. **Does the problem ask for the choices, not just the value?** If yes, keep the table.

| Read window | Optimized storage | Examples |
|-------------|-------------------|----------|
| last 2 cells | 2 variables | Fibonacci, Climbing Stairs, House Robber, Min Cost Climbing Stairs |
| last `k` cells | `k` variables or a circular array of size `k` | Tribonacci (`k = 3`), Dice Combinations (`k = 6`, see [topic 16](../16-the-dp-problem-solving-framework/)) |
| previous row + left cell | one row, left to right | Unique Paths, Minimum Path Sum |
| previous row at same and smaller column, both OLD | one row, right to left | 0/1 knapsack, Subset Sum, Partition Equal Subset Sum |
| same row at smaller column, NEW | one row, left to right | unbounded knapsack, Coin Change II |

More advanced cases (rolling over two dimensions, keeping two rows when you need diagonals, like LCS) are in [05-dp-optimization/01-space-optimization](../../05-dp-optimization/01-space-optimization/) and [02-rolling-arrays](../../05-dp-optimization/02-rolling-arrays/).

---

## 14. Summary and revision notes

- **Keep only what will be read again.** The read window of the recurrence decides what to keep.
- Last two cells → **two variables**; update the older one first.
- Previous row → **two rows**, then often **one row** overwritten in place.
- With one array, the **loop direction** decides whether a read sees the old value or the new one.
- 0/1 knapsack with one array: capacity **high to low**. Forward gives the unbounded knapsack (60 instead of 35 on our example).
- **Time never changes;** only space does.
- If you must **reconstruct** the choices, keep the full table.
- Optimize a **tabulated** solution, not a memoized one, and only after the full table passes your tests.

```
┌───────────────────────────────────────────────────────────────────┐
│ ways[i] = ways[i-1] + ways[i-2]      → two variables, O(1)         │
│ paths[r][c] = paths[r-1][c] + paths[r][c-1]                        │
│                                      → row[c] += row[c-1], O(cols) │
│ best[i][w] = max(best[i-1][w], v + best[i-1][w-wt])                │
│                                      → for w = W down to wt:       │
│                                          best[w] = max(best[w],    │
│                                                v + best[w-wt])     │
└───────────────────────────────────────────────────────────────────┘
```

---

## 15. Check your understanding

**Exercise 1.** House Robber uses `rob[i] = max(rob[i-1], nums[i] + rob[i-2])`. How many variables do you need, and in what order do you update them?

<details><summary>Answer</summary>

The read window is the last two states, so **two variables**: `prev2 = rob[i-2]` and `prev1 = rob[i-1]`. Each step: `long current = Math.max(prev1, nums[i] + prev2); prev2 = prev1; prev1 = current;`. The older variable (`prev2`) must be overwritten first, with the old `prev1`. The answer is `prev1` after the loop. Space O(1).

</details>

**Exercise 2.** In the one-row grid version, a student writes the inner loop as `for (int c = cols - 1; c >= 1; c--) row[c] = row[c] + row[c - 1];`. What does it return for a 3 × 3 grid, and why?

<details><summary>Answer</summary>

Going right to left, `row[c-1]` has **not** been updated yet in this row, so the code adds "above" and "above-left" instead of "above" and "left".
Start `[1, 1, 1]`. Row 1: `c=2: 1+1=2`, `c=1: 1+1=2` → `[1, 2, 2]`. Row 2: `c=2: 2+2=4`, `c=1: 2+1=3` → `[1, 3, 4]`. It returns **4** instead of 6. The grid recurrence needs the **new** left value, so the loop must go left to right.

</details>

**Exercise 3.** Run the forward (wrong) one-array knapsack loop by hand on `weights = [2]`, `values = [10]`, capacity `5`. What does it return, and what does the correct reverse loop return?

<details><summary>Answer</summary>

Forward: `w=2: 10 + best[0]=0 → 10`, `w=3: 10 + best[1]=0 → 10`, `w=4: 10 + best[2]=10 → 20`, `w=5: 10 + best[3]=10 → 20`. It returns **20** (the item taken twice). Reverse: `w=5: 10 + best[3]=0 → 10`, `w=4: 10`, `w=3: 10`, `w=2: 10`. It returns **10**, the right answer: one item, used once.

</details>

**Exercise 4.** A problem asks for the **minimum path sum** in a grid **and** the actual path (the list of cells). Can you use the one-row optimization?

<details><summary>Answer</summary>

Not by itself. The one row gives the minimum **sum**, but to list the cells you walk back from the bottom-right corner, at each cell asking "did I come from above or from the left?", which needs the values (or choices) of every row. Keep the full table, or keep a full table of choices ("came from above / left"), which is also `O(rows · cols)`. If only the sum is asked, one row is fine.

</details>

---

⬅ Previous: [14 · Converting Memoization to Tabulation](../14-converting-memoization-to-tabulation/) · ➡ Next: [16 · The DP Problem-Solving Framework](../16-the-dp-problem-solving-framework/) · 🏠 [README](README.md)
