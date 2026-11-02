# Converting Memoization to Tabulation — Walkthrough

> Worked examples (the same as [topic 13](../13-converting-recursion-to-memoization/)): **Climbing Stairs** with `n = 5` (answer `8`) and **grid paths** in a 3 × 3 grid (answer `6`), plus 3 × 7 (answer `28`).
> Code: [`ClimbingStairsTabulation.java`](solution/ClimbingStairsTabulation.java), [`GridPathsTabulation.java`](solution/GridPathsTabulation.java). Each file contains both the memoized and the tabulated version and checks that they agree.

---

## 1. Intuition

There are two ways to cook a complicated dish.

**Top-down (memoization):** start from the final dish. "I need the sauce." Go make the sauce. "The sauce needs chopped onions." Go chop onions. Each time you finish a component, put it in a labelled bowl so you never make it twice. You only make what the dish actually asks for, in whatever order the questions come up.

**Bottom-up (tabulation):** first lay out every bowl you will need on the counter, in the order the recipe uses them (chefs call this *mise en place*). Fill the simplest bowls first (chopped onions), then the ones that use them (sauce), and finally plate the dish. No running back and forth.

The bowls are the same in both styles. Only the **order of filling** changes. That is why converting one into the other is mechanical:

- the **labelled bowls** (memo cache) become the **counter layout** (table),
- the components that need no preparation (**base cases**) go into their bowls first,
- every "go make X" (**recursive call**) becomes "take bowl X from the counter" (**table read**),
- and the order of filling comes from "what needs what" (**dependencies**).

---

## 2. Problem statement in our own words

Given a correct memoized solution, write a loop-based solution that fills the same states in an order where every state's dependencies are already available, and returns the same answer.

Examples (same as topic 13):

- **Climbing Stairs** ([LeetCode 70](https://leetcode.com/problems/climbing-stairs/)): count the ways to climb `n` steps with moves of 1 or 2.
- **Grid paths**, in the style of [Unique Paths (LeetCode 62)](https://leetcode.com/problems/unique-paths/): count right/down routes from the top-left to the bottom-right of a `rows × cols` grid.

---

## 3. Examples

| Problem | Input | Answer |
|---------|-------|--------|
| Climbing Stairs | `n = 5` | 8 |
| Climbing Stairs | `n = 0` | 1 (the empty plan; also a table-size edge case) |
| Climbing Stairs | `n = 1` | 1 |
| Climbing Stairs | `n = 45` | 1 836 311 903 |
| Grid paths | 3 × 3 | 6 |
| Grid paths | 3 × 7 | 28 |
| Grid paths | 1 × 1 | 1 |
| Grid paths | 1 × 5 and 5 × 1 | 1 (a single straight route) |

---

## 4. Deriving the solution — the 7 questions

Answered for the **grid**, our main 2D example. The recipe uses each answer directly, so we note where it goes.

### Q1. What is changing?

The row `r` and the column `c` of the cell we want to reach. → **Table dimensions:** two, so a 2D table.

### Q2. What does the state represent?

`paths[r][c]` = **the number of right/down routes from `(0, 0)` to `(r, c)`**, for `0 ≤ r < rows` and `0 ≤ c < cols`. → **Table size:** `[rows][cols]`, exactly the memo's size.

### Q3. What are the choices?

The last move into `(r, c)` was down (from `(r-1, c)`) or right (from `(r, c-1)`).

### Q4. What is the recurrence?

`paths[r][c] = paths[r-1][c] + paths[r][c-1]`. → **Loop body:** this line, with calls replaced by reads.

### Q5. What are the base cases?

`paths[r][c] = 1` when `r == 0` or `c == 0`. → **Initialisation:** fill row 0 and column 0 with 1.

### Q6. What is the order of computation?

Both reads point **up** or **left**, so: rows from top to bottom, and inside a row, columns from left to right (topic [11](../11-transition-dependencies-and-computation-order/)). → **Loop direction:** `r` increasing, `c` increasing.

### Q7. Can the state space be optimized?

Each row reads only the row above, so one row is enough. That is the next topic, [15 · Space Optimization Basics](../15-space-optimization-basics/), and it only becomes possible **after** this conversion, because only tabulation has a fixed order.

---

## 5. Brute force: recursion

Already done in [topic 13](../13-converting-recursion-to-memoization/), Section 5. Quick reminder: `ways(5)` makes 15 calls and the 3 × 3 grid makes 11, and both grow exponentially. We start this topic from the memoized versions.

---

## 6. Memoization (top-down) — our starting point

```java
// Climbing Stairs
static long[] memo;                                  // memo[i] = ways to reach step i; -1 = unknown
static long climbStairs(int n) {
    memo = new long[n + 1];
    Arrays.fill(memo, -1);
    return ways(n);                                  // top-level call
}
static long ways(int i) {
    if (i <= 1) return 1;                            // base cases
    if (memo[i] != -1) return memo[i];
    memo[i] = ways(i - 1) + ways(i - 2);             // recurrence
    return memo[i];
}
```

```java
// Grid paths
static long[][] memo;                                // memo[r][c] = routes (0,0)→(r,c); -1 = unknown
static long uniquePaths(int rows, int cols) {
    memo = new long[rows][cols];
    for (long[] row : memo) Arrays.fill(row, -1);
    return paths(rows - 1, cols - 1);                // top-level call
}
static long paths(int r, int c) {
    if (r == 0 || c == 0) return 1;                  // base cases
    if (memo[r][c] != -1) return memo[r][c];
    memo[r][c] = paths(r - 1, c) + paths(r, c - 1);  // recurrence
    return memo[r][c];
}
```

Underline four things in each: the **cache allocation**, the **base cases**, the **recurrence**, the **top-level call**. The recipe converts exactly those four, plus one new decision: the loop direction.

---

## 7. Tabulation (bottom-up) — the recipe

### The translation chart

| In the memoized version | Becomes, in the tabulated version | Recipe step |
|-------------------------|-----------------------------------|-------------|
| `memo = new long[...]` | `table = new long[...]`, **same shape and size** | 1 |
| `Arrays.fill(memo, -1)` | nothing: no sentinel needed, every cell gets written before it is read | 1 |
| `if (base condition) return value;` | write `value` into those cells **before** the loop | 2 |
| a call `f(x)` inside the recurrence | a read `table[x]` | 3 |
| the body of `f` | the body of the loop | 3 |
| (the recursion chooses the order) | loops ordered so every read cell is already written | 4 |
| `return f(top-level arguments);` | `return table[top-level arguments];` | 5 |
| `if (memo[...] != -1) return ...;` | delete: the loop visits each cell exactly once | — |

### 7.1 Climbing Stairs, side by side

```
MEMOIZED                                      TABULATED
──────────────────────────────────────────    ──────────────────────────────────────────────
memo = new long[n + 1];                       long[] ways = new long[Math.max(n + 1, 2)];  // 1
Arrays.fill(memo, -1);                        // (no sentinel)

if (i <= 1) return 1;                         ways[0] = 1;                                 // 2
                                              ways[1] = 1;                                 // 2

                                              for (int i = 2; i <= n; i++) {               // 4
if (memo[i] != -1) return memo[i];            //   (no check needed)
memo[i] = ways(i - 1) + ways(i - 2);              ways[i] = ways[i - 1] + ways[i - 2];     // 3
                                              }

return ways(n);                               return ways[n];                              // 5
```

Notes on each step:

- **Step 1.** Same size `n + 1`. One subtle addition: when `n = 0` the memo had 1 cell and never touched index 1 (the base case returned first). The table **writes** `ways[1] = 1` unconditionally, so it needs at least 2 cells: `Math.max(n + 1, 2)`. Base cases that *return early* in recursion become *unconditional writes* in tabulation, and those writes must fit.
- **Step 2.** "`i <= 1` returns 1" means cells 0 and 1 hold 1.
- **Step 3.** `ways(i - 1)` → `ways[i - 1]`. Parentheses become brackets.
- **Step 4.** Cell `i` reads `i - 1` and `i - 2`, smaller indices, so `i` goes **up**. It starts at 2, the first cell that is not a base case.
- **Step 5.** The top-level call was `ways(n)`, so the answer is `ways[n]`.

#### Filling the table for `n = 5`, step by step

```
after initialisation:   i:    0   1   2   3   4   5
                        ways: 1   1   .   .   .   .

i = 2: ways[1] + ways[0] = 1 + 1 = 2      ways: 1   1   2   .   .   .
i = 3: ways[2] + ways[1] = 2 + 1 = 3      ways: 1   1   2   3   .   .
i = 4: ways[3] + ways[2] = 3 + 2 = 5      ways: 1   1   2   3   5   .
i = 5: ways[4] + ways[3] = 5 + 3 = 8      ways: 1   1   2   3   5   8   ◄── answer
```

Compare with the memo dry run in [topic 13, Section 6.3](../13-converting-recursion-to-memoization/walkthrough.md): the memo stored `2, 3, 5, 8` into cells `2, 3, 4, 5`, in exactly this order. For this problem the recursion's finishing order and the loop's order coincide. That is not always so (see the grid below), and it does not need to be.

### 7.2 Grid paths, side by side

```
MEMOIZED                                          TABULATED
──────────────────────────────────────────────    ────────────────────────────────────────────────
memo = new long[rows][cols];                      long[][] paths = new long[rows][cols];        // 1
for (long[] row : memo) Arrays.fill(row, -1);     // (no sentinel)

if (r == 0 || c == 0) return 1;                   for (int c = 0; c < cols; c++) paths[0][c] = 1; // 2
                                                  for (int r = 0; r < rows; r++) paths[r][0] = 1; // 2

                                                  for (int r = 1; r < rows; r++)                // 4
                                                      for (int c = 1; c < cols; c++)            // 4
if (memo[r][c] != -1) return memo[r][c];                  // (no check needed)
memo[r][c] = paths(r - 1, c) + paths(r, c - 1);          paths[r][c] = paths[r - 1][c]          // 3
                                                                     + paths[r][c - 1];

return paths(rows - 1, cols - 1);                 return paths[rows - 1][cols - 1];             // 5
```

- **Step 2** is the interesting one here. The base condition `r == 0 || c == 0` describes a whole **row and column**, so the initialisation is two loops.
- **Step 4.** The loops start at 1 because row 0 and column 0 are already filled.

#### Filling the 3 × 3 table, step by step

```
after initialisation (step 2):
        c=0  c=1  c=2
 r=0     1    1    1
 r=1     1    .    .
 r=2     1    .    .

r=1, c=1:  above 1 + left 1 = 2          r=1, c=2:  above 1 + left 2 = 3
        c=0  c=1  c=2                            c=0  c=1  c=2
 r=0     1    1    1                      r=0     1    1    1
 r=1     1   [2]   .                      r=1     1    2   [3]
 r=2     1    .    .                      r=2     1    .    .

r=2, c=1:  above 2 + left 1 = 3          r=2, c=2:  above 3 + left 3 = 6
        c=0  c=1  c=2                            c=0  c=1  c=2
 r=0     1    1    1                      r=0     1    1    1
 r=1     1    2    3                      r=1     1    2    3
 r=2     1   [3]   .                      r=2     1    3   [6]   ◄── answer
```

The memo in topic 13 finished the cells in the order `(1,1), (1,2), (2,1), (2,2)`; the loops fill them in the same order here, but for bigger grids the two orders differ. Both are valid topological orders of the same dependency graph.

### 7.3 Variant: when the state is defined "from here to the end"

In [09 · State Representation](../09-state-representation/) the grid state was defined the other way round: `toEnd(r, c)` = routes from `(r, c)` **to** the bottom-right corner. Its memoized version:

```java
static long toEnd(int r, int c) {
    if (r == rows - 1 || c == cols - 1) return 1;     // last row or last column
    if (memo[r][c] != -1) return memo[r][c];
    memo[r][c] = toEnd(r + 1, c) + toEnd(r, c + 1);
    return memo[r][c];
}
// top-level call: toEnd(0, 0)
```

Apply the recipe without thinking twice:

- Step 2: fill the **last** row and **last** column with 1.
- Step 3: `toEnd(r + 1, c)` → `table[r + 1][c]`, `toEnd(r, c + 1)` → `table[r][c + 1]`.
- Step 4: reads point **down** and **right**, so `r` goes from `rows - 2` **down** to 0, and `c` from `cols - 2` **down** to 0.
- Step 5: the top-level call was `toEnd(0, 0)`, so return `table[0][0]`.

The same problem, the same answer (6), opposite loops. The recipe handles it because it reads the direction from the recurrence, not from habit.

### 7.4 Variant: base cases outside the table

In topic 13 we memoized stairs with step sizes 1, 2 and 3, using the base cases `i < 0 → 0` and `i == 0 → 1`. In a table there is no cell `-1`. Two fixes:

**(a) Guard each read** (keeps indices natural):

```java
ways[0] = 1;
for (int i = 1; i <= n; i++) {
    ways[i] = ways[i - 1]
            + (i >= 2 ? ways[i - 2] : 0)      // "i - 2 < 0" → the base value 0
            + (i >= 3 ? ways[i - 3] : 0);
}
```

**(b) Pad the table** with extra cells that hold the out-of-range base value:

```java
int pad = 3;                                   // largest step size
long[] t = new long[n + 1 + pad];              // t[i + pad] stores ways(i); t[0..2] = ways(-3..-1) = 0
t[pad] = 1;                                    // ways(0) = 1
for (int i = 1; i <= n; i++) t[i + pad] = t[i + pad - 1] + t[i + pad - 2] + t[i + pad - 3];
return t[n + pad];
```

Both give 1, 1, 2, 4, 7, 13 for `n = 0..5`. Guards are clearer for beginners; padding keeps the loop body identical to the recurrence and is common in 2D problems (an extra row and column of zeros).

---

## 8. Space optimization

Not part of this recipe, but this is where it pays off. Look at the tables above: when filling `ways[5]`, only `ways[4]` and `ways[3]` were read; when filling row 2 of the grid, only row 1 was read. [15 · Space Optimization Basics](../15-space-optimization-basics/) turns that observation into two variables and one row.

---

## 9. Dry run on a full example — the 3 × 7 grid

The same recipe on a bigger input. After initialisation, row 0 and column 0 are all 1. Then row by row:

```
row 1:  c=1: 1+1=2   c=2: 1+2=3   c=3: 1+3=4   c=4: 1+4=5   c=5: 1+5=6   c=6: 1+6=7
row 2:  c=1: 2+1=3   c=2: 3+3=6   c=3: 4+6=10  c=4: 5+10=15 c=5: 6+15=21 c=6: 7+21=28
```

Final table:

```
        c=0  c=1  c=2  c=3  c=4  c=5  c=6
 r=0     1    1    1    1    1    1    1
 r=1     1    2    3    4    5    6    7
 r=2     1    3    6   10   15   21   28   ◄── answer paths[2][6] = 28
```

`GridPathsTabulation.java` checks this value, and also compares the tabulated and memoized answers for every grid from 1 × 1 to 12 × 12. `ClimbingStairsTabulation.java` does the same for every `n` from 0 to 60.

| Cell order (loops) | Reads | Value |
|--------------------|-------|-------|
| (1,1) | (0,1)=1, (1,0)=1 | 2 |
| (1,2) | (0,2)=1, (1,1)=2 | 3 |
| … | … | … |
| (1,6) | (0,6)=1, (1,5)=6 | 7 |
| (2,1) | (1,1)=2, (2,0)=1 | 3 |
| (2,2) | (1,2)=3, (2,1)=3 | 6 |
| (2,3) | (1,3)=4, (2,2)=6 | 10 |
| (2,4) | (1,4)=5, (2,3)=10 | 15 |
| (2,5) | (1,5)=6, (2,4)=15 | 21 |
| (2,6) | (1,6)=7, (2,5)=21 | **28** |

---

## 10. Complexity analysis

| Version | Time | Space | Why |
|---------|------|-------|-----|
| Stairs memo | O(n) | O(n) cache + O(n) stack | `n + 1` states, O(1) each; recursion depth `n` |
| Stairs table | O(n) | O(n) table, O(1) stack | the loop runs `n − 1` times with one addition |
| Grid memo | O(rows · cols) | O(rows · cols) + O(rows + cols) stack | one state per cell |
| Grid table | O(rows · cols) | O(rows · cols) | each cell written once |

Same big-O. The real differences:

- **Stack:** the table uses none, so `n = 10⁶` works (topic [12](../12-time-and-space-complexity-in-dp/) showed the memo version overflowing).
- **Unreachable states:** the table fills **every** cell; memoization fills only cells the recursion actually reaches. For stairs and grids every cell is reachable, so no difference. In some problems (for example Target Sum in [topic 13](../13-converting-recursion-to-memoization/walkthrough.md), where many `(i, sum)` pairs can never happen), memoization does less work.
- **Constant factor:** loops avoid call overhead and are friendlier to the CPU cache, so tables are usually somewhat faster.

---

## 11. Why this approach works

1. **Same states, same values.** The table has exactly the memo's cells, and each cell is computed by the same formula from the same base values. So each cell ends up holding the same number the memo would have stored.
2. **Reads are never stale.** Step 4 orders the loops so that every cell a formula reads is written earlier ([topic 11](../11-transition-dependencies-and-computation-order/), Section 11 gives the induction argument). In the memo version the call stack guaranteed this; in the table version the loop order guarantees it.
3. **Base cases are written first.** Step 2 places them before the loop, so the first cells the loop reads are correct.
4. **The answer cell matches the question.** The top-level call asked for one particular state; step 5 returns that state's cell.

---

## 12. Common mistakes

Each pitfall below compiles and runs, and gives a wrong answer or a crash. The numbers are for our examples.

1. **Table one cell too small.** `new long[n]` instead of `new long[n + 1]` → `ArrayIndexOutOfBoundsException` at `ways[n]`. Size = range of the parameter = `max − min + 1`.
2. **Unconditional base writes on tiny inputs.** `new long[n + 1]` with `ways[1] = 1` crashes for `n = 0`. Use `Math.max(n + 1, 2)` or return early for small `n`.
3. **Forgetting a base case.** Climbing Stairs with `ways[0] = 1` but no `ways[1] = 1` (the cell stays 0), loop from 2: the table becomes `1, 0, 1, 1, 2, 3` and the answer for `n = 5` is **3** instead of 8. Grid with row 0 initialised but column 0 forgotten: the table becomes

   ```
   1 1 1
   0 1 2
   0 1 3     → 3 instead of 6
   ```

4. **Wrong loop direction.** Copying "`i` goes up" into a recurrence that reads `i + 1` reads unfilled cells. Topic [11](../11-transition-dependencies-and-computation-order/) shows `[10, 15, 20]` returning 10 instead of 15 this way.
5. **Returning the wrong cell.** `ways[n - 1]` (answers the question for `n - 1` steps), or `paths[rows][cols]` (out of bounds). Always return the cell named by the top-level call.
6. **Starting the loop at 0 and reading index −1.** The recursion's base case caught `i <= 1` before reading `i - 2`; the loop body has no such protection. Either start the loop after the base cells, or guard/pad (Section 7.4).
7. **A sentinel leaking into the table.** Copying `Arrays.fill(table, -1)` from the memo version is harmless only if every cell is overwritten before being read. If a base cell is forgotten, the `-1` is silently added into answers. Tables do not need a sentinel; leave them at Java's default 0 and write every base cell explicitly.
8. **Overflow.** `int` overflows for Climbing Stairs above `n = 45` (`ways(46) = 2 971 215 073 > 2³¹ − 1`). Use `long` or the required modulo.

---

## 13. When to use this technique / how to recognize similar problems

Convert to tabulation when:

- the recursion depth could exceed about 10⁴ in Java,
- you want to reduce memory next ([15](../15-space-optimization-basics/)),
- the problem is a known pattern whose table order is obvious (stairs, grids, knapsack).

Stay with memoization when:

- only a small fraction of the states are reachable (the table would waste time on the rest),
- the dependency order is hard to see (for example DP on a general graph, where the order is a topological sort; see [DP on Graphs](../../04-advanced-dp-patterns/08-dp-on-graphs/)),
- you are short on time in an interview and the input is small.

A good workflow for new problems: **brute force → memoization (topic 13 recipe) → tabulation (this recipe) → space optimization (topic 15)**, testing that all versions agree at each step.

---

## 14. Summary and revision notes

- Memo cache → table of the **same shape and size**; no sentinel needed.
- Base cases → **initial values written before the loop**; make sure the table is big enough for them.
- Recursive calls `f(x)` → table reads `t[x]`; the function body becomes the loop body.
- Loop direction comes from the **reads**: smaller indices → loop up; larger indices → loop down.
- Answer = `t[top-level arguments]`.
- Base cases outside the table (`f(-1) = 0`): guard each read, or pad the table.
- Always test the table against the memo on many small inputs.

```
┌────────────────────────────────────────────────────────────────┐
│  memo = new T[size]           →  t = new T[size]   (no fill)    │
│  if (base) return v;          →  t[base] = v;      (before loop)│
│  f(x)                         →  t[x]                           │
│  (recursion picks the order)  →  for (...) in dependency order  │
│  return f(top);               →  return t[top];                 │
└────────────────────────────────────────────────────────────────┘
```

---

## 15. Check your understanding

**Exercise 1.** Convert this memoized Min Cost Climbing Stairs to tabulation, and fill the table for `cost = [10, 15, 20]`.

```java
// minCost(i) = cheapest total fee to arrive at position i (the top is position n)
static int minCost(int i) {
    if (i <= 1) return 0;                    // we may start on step 0 or step 1 for free
    if (memo[i] != -1) return memo[i];
    memo[i] = Math.min(minCost(i - 1) + cost[i - 1], minCost(i - 2) + cost[i - 2]);
    return memo[i];
}
// top-level call: minCost(n)
```

<details><summary>Answer</summary>

```java
int n = cost.length;
int[] dp = new int[n + 1];          // step 1: same size as the memo (positions 0..n)
dp[0] = 0; dp[1] = 0;               // step 2 (n >= 2 in this problem, so index 1 exists)
for (int i = 2; i <= n; i++) {      // step 4: reads i-1, i-2 → go up
    dp[i] = Math.min(dp[i - 1] + cost[i - 1], dp[i - 2] + cost[i - 2]);   // step 3
}
return dp[n];                       // step 5
```

For `[10, 15, 20]`: `dp[0] = 0`, `dp[1] = 0`, `dp[2] = min(0 + 15, 0 + 10) = 10`, `dp[3] = min(10 + 20, 0 + 15) = 15`. Answer **15**.

</details>

**Exercise 2.** A memoized function has the recurrence `f(i) = f(i + 1) + f(i + 2)`, base cases `f(n) = 1` and `f(i) = 0` for `i > n`, and top-level call `f(0)`. Write the loop header, the initialisation and the return statement.

<details><summary>Answer</summary>

Reads point to **larger** indices, so the loop goes **down**. Pad the table with one extra cell for `f(n + 1) = 0`:

```java
long[] t = new long[n + 2];   // indices 0..n+1
t[n] = 1;                     // t[n + 1] = 0 by default
for (int i = n - 1; i >= 0; i--) t[i] = t[i + 1] + t[i + 2];
return t[0];                  // the top-level call was f(0)
```

(This is Climbing Stairs counted from the other end: `f(i)` = ways to get from step `i` to step `n`.)

</details>

**Exercise 3.** This tabulation returns 3 for a 3 × 3 grid. Find the bug.

```java
long[][] p = new long[rows][cols];
for (int c = 0; c < cols; c++) p[0][c] = 1;
for (int r = 1; r < rows; r++)
    for (int c = 1; c < cols; c++)
        p[r][c] = p[r - 1][c] + p[r][c - 1];
return p[rows - 1][cols - 1];
```

<details><summary>Answer</summary>

Only half of the base case was converted. The memo's `r == 0 || c == 0` covers row 0 **and** column 0, but column 0 was never set to 1, so `p[1][0]` and `p[2][0]` stay 0. Add `for (int r = 0; r < rows; r++) p[r][0] = 1;`. The broken table is `1 1 1 / 0 1 2 / 0 1 3`, which is where the 3 comes from.

</details>

**Exercise 4.** Why can a memoized solution sometimes be **faster** than its tabulated version, even though both have the same big-O on paper?

<details><summary>Answer</summary>

The table fills **every** state in its range, while memoization only computes states the recursion actually reaches from the top-level call. If most states are unreachable (for example, sums that no combination of signs can produce), the table wastes time on them. The big-O bound counts all possible states, so it is the same for both, but the actual work can differ a lot.

</details>

---

⬅ Previous: [13 · Converting Recursion to Memoization](../13-converting-recursion-to-memoization/) · ➡ Next: [15 · Space Optimization Basics](../15-space-optimization-basics/) · 🏠 [README](README.md)
