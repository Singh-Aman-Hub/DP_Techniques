# Time and Space Complexity in DP — Walkthrough

> Main running example: **0/1 knapsack** with `weights = [1, 3, 4]`, `values = [15, 20, 30]`, `capacity = 4` (answer `35`).
> Supporting examples: Climbing Stairs `n = 5` (answer `8`) and right/down paths in a 3 × 3 grid (answer `6`).
> Code: [`OperationCounter.java`](solution/OperationCounter.java) measures every count in this lesson; [`StackDepthDemo.java`](solution/StackDepthDemo.java) shows the stack limit.

---

## 1. Intuition

Imagine you must fill in a big printed form with many boxes. How long will it take?

```
time = (number of boxes) × (time to fill one box)
```

If there are 200 boxes and each takes 3 seconds, that is 600 seconds. You do not need to fill the form to know that.

A DP table is that form. Each **state** is a box. Once you are doing DP properly, you fill each box **once**, so the total time is just "boxes × time per box".

Memory has a second, hidden part. Think of a pile of plates in a canteen. Every time a recursive function calls another, it puts a plate on the pile ("come back here when you are done"). Plates come off only when calls return. With deep recursion, the pile can get taller than the shelf. In Java that is a `StackOverflowError`.

```
space = (boxes in the form) + (tallest the plate pile ever gets)
         └ table / memo        └ recursion stack
```

---

## 2. Problem statement in our own words

This is a concept topic. The question we answer for any DP is:

> Given a DP solution (recursive, memoized, tabulated or space-optimized), how much time and memory does it need, and will it fit in the limits of the problem?

The main example is the 0/1 knapsack: you have items with weights and values and a bag with a weight limit; choose a subset of items, each at most once, whose total weight fits, so that the total value is as large as possible. You will study it properly in [0/1 Knapsack](../../03-subsequence-and-knapsack-dp/02-zero-one-knapsack/). Here we only need its recurrence, which [09 · State Representation](../09-state-representation/) already introduced.

---

## 3. Examples

The numbers below are **measured** by `OperationCounter.java`, not guessed.

| Problem | Input | Answer | Brute-force calls | States computed by memo / cells filled by table |
|---------|-------|--------|-------------------|--------------------------------------------------|
| Climbing Stairs | `n = 5` | 8 | 15 | memo: 4 non-base states (9 calls); table: 6 cells |
| Climbing Stairs | `n = 20` | 10 946 | 21 891 | table: 21 cells |
| Grid paths | 3 × 3 | 6 | 11 | memo: 4 non-base states; table: 9 cells |
| Grid paths | 3 × 7 | 28 | 55 | table: 21 cells |
| 0/1 knapsack | 3 items, capacity 4 | 35 | 12 | table: 4 × 5 = 20 cells |

Look at the stairs row for `n = 20`: about 22 000 calls against 21 cells. That gap is what DP buys you.

---

## 4. Deriving the solution — the 7 questions

We answer the seven questions for the knapsack, and at each step we note **what it tells us about complexity**. This is the habit to build: the complexity falls out of the derivation for free.

### Q1. What is changing?

Two things change as we decide item by item: **which item we are looking at** (`i`) and **how much capacity is left** (`remaining`).

*Complexity note:* each changing quantity becomes a dimension of the table.

### Q2. What does the state represent?

`best(i, remaining)` = **the largest total value we can get from items `i, i+1, ..., n-1` with `remaining` capacity left.**

*Complexity note:* now count the possible values of each parameter.

- `i` goes from `0` to `n` → `n + 1` values.
- `remaining` goes from `0` to `W` (the capacity) → `W + 1` values.

So the **number of states** is `(n + 1) × (W + 1)`. For our example: `4 × 5 = 20`.

### Q3. What are the choices?

For item `i`: **skip** it, or **take** it (only if `weights[i] <= remaining`).

*Complexity note:* 2 choices → each state does **O(1)** work (a comparison and a `max`).

### Q4. What is the recurrence?

```
best(i, remaining) = max( best(i+1, remaining),                               ← skip
                          values[i] + best(i+1, remaining - weights[i]) )     ← take, if it fits
```

*Complexity note:* each term reads a state that is already computed (in memo or table), so reading it costs O(1).

### Q5. What are the base cases?

`best(n, remaining) = 0`: no items left, no value to add.

*Complexity note:* base cases are states too; they are already included in the `n + 1` count.

### Q6. What is the order of computation?

`best(i, ...)` reads only `best(i+1, ...)`, so fill `i` from `n` down to `0` (or, equivalently, the "first `i` items" form row by row from the top, as in Section 7). See [11 · Transition Dependencies and Computation Order](../11-transition-dependencies-and-computation-order/).

*Complexity note:* the order does not change the count. Every order visits each cell once.

### Q7. Can the state space be optimized?

Row `i` reads only row `i+1`, so we can keep one row of `W + 1` cells instead of `n + 1` rows (see [15 · Space Optimization Basics](../15-space-optimization-basics/)).

*Complexity note:* space drops from `O(n · W)` to `O(W)`. Time stays `O(n · W)`: we still compute every state; we just forget old rows.

**Result:** time `O(n · W)`, space `O(n · W)` for the table or `O(W)` optimized.

---

## 5. Brute force: recursion

Without a cache, a recursive solution makes one **call** per node of its recursion tree. So:

```
brute-force time = (number of nodes in the recursion tree) × (work per node)
```

### Climbing Stairs, `n = 5`

```java
static long ways(int n) {
    if (n <= 1) return 1;
    return ways(n - 1) + ways(n - 2);
}
```

```
ways(5)
├── ways(4)
│   ├── ways(3)
│   │   ├── ways(2)
│   │   │   ├── ways(1)
│   │   │   └── ways(0)
│   │   └── ways(1)
│   └── ways(2)
│       ├── ways(1)
│       └── ways(0)
└── ways(3)
    ├── ways(2)
    │   ├── ways(1)
    │   └── ways(0)
    └── ways(1)
```

Count the lines: **15 calls**. In general the number of calls is `2 · ways(n) − 1`, because every leaf contributes exactly 1 to the answer (so there are `ways(n)` leaves) and a tree where every inner node has two children has one fewer inner node than leaves.

`ways(n)` grows roughly like `1.618ⁿ` (the golden ratio), so:

| n | ways(n) | brute-force calls |
|---|---------|-------------------|
| 5 | 8 | 15 |
| 20 | 10 946 | 21 891 |
| 40 | 165 580 141 | about 331 million |
| 50 | 20 365 011 074 | about 40 billion |

At a rough 10⁸ simple operations per second, `n = 50` would take minutes. We write this as **O(2ⁿ)** (a safe upper bound: each call makes at most 2 calls, depth at most `n`).

### Knapsack, our example

```java
static int best(int i, int remaining) {
    if (i == n) return 0;
    int skip = best(i + 1, remaining);
    int take = (weights[i] <= remaining) ? values[i] + best(i + 1, remaining - weights[i]) : MIN;
    return Math.max(skip, take);
}
```

```
                         best(0,4)
                   /                    \
           skip item0                 take item0 (w=1)
           best(1,4)                    best(1,3)
          /         \                  /         \
   best(2,4)      best(2,1)      best(2,3)     best(2,0)
    /     \          |               |             |
best(3,4) best(3,0) best(3,1)    best(3,3)     best(3,0)
```

**12 calls** (the same number `OperationCounter.java` measures). Each item doubles the branches when it fits, so with `n` items the worst case is about `2ⁿ` calls: one per subset of items. That is **O(2ⁿ)**.

Notice `best(3,0)` appears twice. With more items, repeats explode.

### Space of brute force

Brute force stores no table, but the **stack** holds one frame per active call. The deepest chain is root-to-leaf: `n` for stairs, `n + 1` levels for knapsack. So brute-force space is **O(n)**, not O(1).

---

## 6. Memoization (top-down)

With a cache, each state's body runs **once**. Any later call for the same state is a **cache hit** that returns in O(1).

```
memo time = (number of states) × (work per state)  +  (cache hits, each O(1))
```

Cache hits do not change the big-O, because each computed state makes a fixed number of calls (its choices), so hits ≤ states × choices.

### Climbing Stairs `n = 5`, measured

- States computed: `ways(2), ways(3), ways(4), ways(5)` → **4** (`ways(0)`, `ways(1)` are base cases).
- Total calls: 1 (the root) + 2 per computed state = **9**.

Compare: 15 brute-force calls. For `n = 40`: 79 memo calls against 331 million.

### Space of memoization

Two parts:

```
memo space = cache size          +  maximum recursion depth
           = O(number of states) +  O(longest chain of calls)
```

| Problem | Cache | Max depth | Total |
|---------|-------|-----------|-------|
| Stairs | `n + 1` cells | `n` (5 → 4 → 3 → 2 → 1) | O(n) |
| Grid `R × C` | `R · C` cells | `R + C` (each call moves one step) | O(R · C) |
| Knapsack | `(n+1)(W+1)` cells | `n + 1` (each call moves to the next item) | O(n · W) |

For these problems the stack does not change the big-O, because it is smaller than the cache. But it is a **different kind** of memory, with a much smaller limit. Section 10.3 explains why that matters in Java.

---

## 7. Tabulation (bottom-up)

In tabulation the count is the easiest of all: the loops visit each cell once.

```
tabulation time  = (number of cells) × (work per cell)
tabulation space = (number of cells)          ← no recursion stack
```

### Knapsack table, step by step

Here we use the "first `i` items" form, `table[i][w]` = best value using only the first `i` items with capacity `w`. It has the same `(n+1) × (W+1)` cells.

```
table[i][w] = max( table[i-1][w],                                 ← skip item i-1
                   values[i-1] + table[i-1][w - weights[i-1]] )   ← take it, if it fits
```

Row 0 means "no items": all zeros.

```
                       w=0  w=1  w=2  w=3  w=4
 i=0 (no items)          0    0    0    0    0
```

Row 1 adds item 0 (weight 1, value 15). Every capacity `w ≥ 1` can hold it:

```
 i=1 (+ w1, v15)         0   15   15   15   15
```

Row 2 adds item 1 (weight 3, value 20):

- `w = 3`: skip → 15; take → 20 + table[1][0] = 20. Max **20**.
- `w = 4`: skip → 15; take → 20 + table[1][1] = 35. Max **35**.

```
 i=2 (+ w3, v20)         0   15   15   20   35
```

Row 3 adds item 2 (weight 4, value 30):

- `w = 4`: skip → 35; take → 30 + table[2][0] = 30. Max **35**.

```
 i=3 (+ w4, v30)         0   15   15   20   35   ◄── answer table[3][4] = 35
```

Count: **4 rows × 5 columns = 20 cells**, each one `max` of at most two numbers. That is the `O(n · W)` we predicted in Q2–Q3, made visible. The answer is to take items 0 and 1 (weights 1 + 3 = 4, values 15 + 20 = 35).

---

## 8. Space optimization

Space optimization changes **space**, never the number of states computed. A quick reference:

| Problem | Table | Reads | Optimized space |
|---------|-------|-------|-----------------|
| Stairs | `n + 1` cells | 2 previous cells | 2 variables → **O(1)** |
| Grid `R × C` | `R · C` cells | row above + cell to the left | one row → **O(C)** |
| Knapsack | `(n+1)(W+1)` cells | the row above only | one row of `W + 1` → **O(W)** |

All three still take the same **time**: O(n), O(R · C), O(n · W). How to do it safely is the subject of [15 · Space Optimization Basics](../15-space-optimization-basics/).

Space optimization can be the difference between passing and failing. CSES [Book Shop](https://cses.fi/problemset/task/1158) allows 1 000 items and capacity 10⁵: the full table has 10⁸ `int` cells (about 400 MB), one row has 10⁵ cells (about 400 KB).

---

## 9. Dry run on a full example — the work, counted three ways

All values below come from running `OperationCounter.java`.

```
PASS  stairs brute n=5 calls -> 15 (expected 15)
PASS  stairs memo n=5 states computed -> 4 (expected 4)
PASS  stairs memo n=5 calls -> 9 (expected 9)
PASS  stairs tab n=5 cells filled -> 6 (expected 6)
PASS  grid brute 3x3 calls -> 11 (expected 11)
PASS  grid memo 3x3 states computed -> 4 (expected 4)
PASS  grid tab 3x3 cells filled -> 9 (expected 9)
PASS  knapsack brute calls -> 12 (expected 12)
PASS  knapsack tab cells filled -> 20 (expected 20)
```

Put them side by side with the formula:

| Problem | Formula "states × work" | Predicted | Measured (table) |
|---------|------------------------|-----------|------------------|
| Stairs `n = 5` | `(n + 1) × 1` | 6 | 6 |
| Grid 3 × 3 | `R · C × 1` | 9 | 9 |
| Knapsack `n = 3, W = 4` | `(n+1)(W+1) × 1` | 20 | 20 |

The formula predicted the work exactly. For small examples the brute force can look competitive (12 calls versus 20 cells for the knapsack), but brute force doubles with every new item while the table grows by only one row of `W + 1` cells.

---

## 10. Complexity analysis

### 10.1 Summary for the three running examples

| Approach | Stairs (1D) | Grid `R × C` (2D) | Knapsack `n` items, capacity `W` |
|----------|-------------|-------------------|-----------------------------------|
| Brute force time | O(2ⁿ) | O(2^(R+C)) | O(2ⁿ) |
| Brute force space (stack) | O(n) | O(R + C) | O(n) |
| Memo time | O(n) | O(R · C) | O(n · W) |
| Memo space | O(n) cache + O(n) stack | O(R · C) + O(R + C) stack | O(n · W) + O(n) stack |
| Tabulation time | O(n) | O(R · C) | O(n · W) |
| Tabulation space | O(n) | O(R · C) | O(n · W) |
| Space-optimized | O(1) space | O(C) space | O(W) space |

**Why each one:**

- **Stairs memo/tab, O(n):** there are `n + 1` states (`0..n`), each adds two numbers.
- **Grid memo/tab, O(R · C):** one state per cell, each adds two numbers.
- **Grid brute force:** every root-to-leaf path is one right/down route, and there are `C(R+C−2, R−1)` of them, which grows exponentially; `2^(R+C)` is a simple upper bound.
- **Knapsack memo/tab, O(n · W):** `(n + 1)(W + 1)` states, each a `max` of two options.

### 10.2 When work per state is not O(1)

Many beginners say "one state, so O(1)". Count the choices instead:

| Recurrence | States | Work per state | Time |
|------------|--------|----------------|------|
| `ways[i] = ways[i-1] + ways[i-2]` | n | 2 terms → O(1) | O(n) |
| Dice: `ways[i] = ways[i-1] + ... + ways[i-6]` | n | 6 terms → O(1) | O(n) |
| Coin change: `best[a] = min over each coin c of best[a-c] + 1` | amount | k coins → O(k) | O(amount · k) |
| LIS: `lis[i] = 1 + max over j < i of lis[j]` | n | up to n terms → O(n) | O(n²) |
| Interval: `dp[i][j] = min over split k of ...` | n² | up to n splits → O(n) | O(n³) |

### 10.3 Stack depth limits in Java

Each active call uses a **stack frame** (its parameters, local variables and return address). Java gives every thread a fixed amount of stack memory. On a typical 64-bit desktop JVM the default is around 512 KB to 1 MB per thread. A small recursive method uses tens of bytes per frame, so you can usually count on **a few thousand to a few tens of thousands** of nested calls, not millions. Beyond that you get:

```
Exception in thread "main" java.lang.StackOverflowError
```

The algorithm is not wrong; the stack is just too small for the depth. For memoized Climbing Stairs the depth is `n`, so `n = 1 000 000` will overflow with default settings, while the same algorithm as a loop runs fine.

`StackDepthDemo.java` shows this deterministically by running the memoized code on a thread with a stack size we choose:

```java
Thread worker = new Thread(null, task, "dp-worker", 256L * 1024);  // 256 KB stack
```

```
PASS  memo n=10 on 256 KB stack -> 89 (expected 89)
PASS  memo n=1,000,000 on 256 KB stack overflows -> -1 (expected -1)
PASS  memo n=200,000 on 256 MB stack equals tabulation -> 835799900 (expected 835799900)
PASS  tabulation n=1,000,000 finished (result in [0, MOD)) -> 1 (expected 1)
```

(Answers are taken modulo 1 000 000 007 so they fit in a `long`.)

**Three fixes, best first:**

1. **Convert to tabulation** ([14 · Converting Memoization to Tabulation](../14-converting-memoization-to-tabulation/)). No recursion, no stack problem. This is the standard fix.
2. **Run the recursion in a thread with a large stack**, as in the demo: `new Thread(null, task, "solver", 1L << 28)` (256 MB). Common in competitive programming when a recursive solution is much easier to write.
3. **Raise the default stack size** with the JVM flag `-Xss`, for example `java -Xss64m Main`. This works only when you control how the program is launched, which you usually do not on LeetCode.

**Rule of thumb:** if the maximum recursion depth can exceed about 10⁴, prefer tabulation in Java.

### 10.4 Pseudo-polynomial time, gently

The knapsack runs in `O(n · W)`. That looks like `O(n²)`, a polynomial. But look at what `W` is: a **number** in the input, not a count of things.

How much input does it take to write down `W`? Only its digits:

| W | digits to write it | table columns `W + 1` |
|---|--------------------|-----------------------|
| 4 | 1 | 5 |
| 10 000 | 5 | 10 001 |
| 1 000 000 000 | 10 | 1 000 000 001 |

Adding one digit to `W` makes the input one character longer but multiplies the table width by 10. So the running time grows **exponentially in the length of the input**, even though it is a polynomial in the **value** of `W`. That combination is called **pseudo-polynomial**.

What it means in practice:

- If the problem says `W ≤ 10⁴`, an `O(n · W)` table is great.
- If the problem says `W ≤ 10⁹`, the same table is hopeless (10⁹ columns). You must look for a **different state**. For example, AtCoder [Knapsack 2](https://atcoder.jp/contests/dp/tasks/dp_e) swaps the roles: "minimum weight to reach value `v`", which is small when values are small.

So the very first thing to check in any knapsack-like problem: **is the number I want to use as a dimension small enough to be an array size?**

### 10.5 From input limits to allowed complexity

A rough guide, assuming about 10⁸ simple operations per second:

| Largest input size | Complexity that usually fits | Typical DP shape |
|--------------------|-----------------------------|------------------|
| n ≤ 12 | O(n!) | try all orders |
| n ≤ 20 | O(2ⁿ · n) | [bitmask DP](../../04-advanced-dp-patterns/09-bitmask-dp/) |
| n ≤ 500 | O(n³) | interval DP |
| n ≤ 5 000 | O(n²) | 2D table, LIS |
| n ≤ 10⁶ | O(n) or O(n log n) | 1D DP |
| n · W ≤ 10⁷–10⁸ | O(n · W) | knapsack |

---

## 11. Why this approach works

**Why "states × work" is correct for memoization and tabulation.**

- Each state's body runs **at most once**: in memoization the cache check returns early on every later visit; in tabulation the loops visit each cell once.
- Inside the body we do a fixed amount of work for each choice, and every recursive read either hits the cache (O(1)) or triggers the one-time computation of another state, which we are already counting separately for that state.
- So adding up the bodies of all states gives the total: `Σ (work of each state) = states × work per state` when every state has the same number of choices.

**Why the stack is included.** Memory that is in use at the same time counts. During memoized recursion, every unfinished call keeps its frame, so the peak memory is the cache plus the longest chain of unfinished calls.

**Why brute force is exponential.** Without a cache, the same state is recomputed every time it is reached, and the number of ways to reach it grows with the number of paths in the dependency graph, which is exponential for these problems (see [04 · Overlapping Subproblems](../04-overlapping-subproblems/)).

---

## 12. Common mistakes

1. **Forgetting work per state.** "There are `n` states, so O(n)" is wrong for LIS, where each state loops over up to `n` earlier states: O(n²).
2. **Counting recursive calls instead of states for memoization.** Calls include cache hits; the clean count is states × choices, which has the same big-O.
3. **Forgetting the recursion stack** in memoization's space, and then being surprised by `StackOverflowError`.
4. **Calling `O(n · W)` "polynomial" without checking `W`.** It is only fast when `W` is small.
5. **Ignoring memory limits.** A `long[5000][5000]` table is 200 MB. Check `cells × bytes per cell` (`int` 4, `long` 8) before allocating.
6. **Thinking space optimization speeds things up.** It saves memory; time stays the same (sometimes slightly faster thanks to the CPU cache, but not in big-O).
7. **Using a `HashMap` memo when an array would do.** Same big-O, but each entry costs dozens of bytes and boxing time. Use arrays when the parameter ranges are known and small.

---

## 13. When to use this technique / how to recognize similar problems

Always. The checklist to run after you have the recurrence:

1. **List the state parameters** and the range of each. Multiply the ranges: that is the state count.
2. **Count the choices** in the recurrence: that is the work per state.
3. **Time** = state count × work per state.
4. **Space** = state count (or less after optimization) + recursion depth (memo only).
5. **Compare with the limits** (table in 10.5). If it does not fit, you need a smaller state, a faster transition (see [05-dp-optimization](../../05-dp-optimization/01-space-optimization/)) or a different idea.
6. **In Java**, if recursion depth can go past about 10⁴, plan for tabulation.

---

## 14. Summary and revision notes

- **Time = number of states × work per state.** Count states by multiplying the ranges of the parameters.
- **Space = table/cache + recursion stack.** Tabulation has no recursion stack.
- 1D stairs: O(n) time; grid: O(R · C); knapsack: O(n · W).
- Brute force is exponential because it recomputes states; its space is the recursion depth.
- Work per state is the number of choices; it is not always O(1).
- **Pseudo-polynomial:** O(n · W) is fast only when the **value** `W` is small.
- **Java stack:** a few thousand to tens of thousands of nested calls by default. Fix deep recursion with tabulation, a big-stack thread, or `-Xss`.

```
┌──────────────────────────────────────────────────────────────┐
│ time  = (#states) × (work per state)                         │
│ space = (#cells kept) + (max recursion depth, memo only)     │
│                                                              │
│ #states = product of the ranges of the state parameters      │
│ work    = number of choices in the recurrence                │
└──────────────────────────────────────────────────────────────┘
```

---

## 15. Check your understanding

**Exercise 1.** A DP has state `dp[i][j]` with `0 ≤ i < n` and `0 ≤ j < m`, and the recurrence takes the minimum over all `k` from `0` to `j`. What is the time complexity?

<details><summary>Answer</summary>

States: `n · m`. Work per state: up to `m` values of `k`, so O(m). Time: **O(n · m²)**. (The table alone would suggest O(n · m), which is the classic mistake of forgetting the work per state.)

</details>

**Exercise 2.** Memoized Climbing Stairs is called with `n = 100 000` in Java and crashes, although the same code works for `n = 1 000`. What is happening, and what are two fixes?

<details><summary>Answer</summary>

The recursion depth is about `n`, because `ways(100000)` calls `ways(99999)`, and so on, before anything returns. 100 000 nested frames do not fit in the default thread stack, so Java throws `StackOverflowError`. The time and the cache are fine. Fixes: (1) rewrite it as a bottom-up loop (tabulation), which needs no stack; (2) run the recursion in `new Thread(null, task, "solver", bigStackSizeInBytes)`, or launch with `-Xss` if you control the JVM flags.

</details>

**Exercise 3.** Problem A: 0/1 knapsack with `n = 100` items and capacity `W = 10⁵`. Problem B: same, but `W = 10⁹` and every item's value is at most 1 000. Which one can use the `O(n · W)` table, and what would you try for the other?

<details><summary>Answer</summary>

A: `100 × 10⁵ = 10⁷` cells, fine. B: `100 × 10⁹ = 10¹¹` cells, impossible. This is the pseudo-polynomial trap: `W` is huge in value although it is short to write. For B, change the state to use the **small** number as a dimension: the total value is at most `100 × 1 000 = 10⁵`, so use `minWeight[v]` = smallest total weight that achieves value exactly `v`, and answer with the largest `v` whose `minWeight[v] ≤ W`. That is `O(n · 10⁵)`.

</details>

**Exercise 4.** For the grid of `R` rows and `C` columns, what is the maximum recursion depth of the memoized solution, and why is it `R + C` rather than `R · C`?

<details><summary>Answer</summary>

Each recursive call moves one step up or one step left, so a chain of nested calls from `(R−1, C−1)` can be at most `(R − 1) + (C − 1)` steps long before it reaches row 0 or column 0. The memo **table** has `R · C` cells, but they are not all on the stack at the same time: only the current chain is. So the stack depth is O(R + C), which is small next to the O(R · C) cache.

</details>

---

⬅ Previous: [11 · Transition Dependencies and Computation Order](../11-transition-dependencies-and-computation-order/) · ➡ Next: [13 · Converting Recursion to Memoization](../13-converting-recursion-to-memoization/) · 🏠 [README](README.md)
