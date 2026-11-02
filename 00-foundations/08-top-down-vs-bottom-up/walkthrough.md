# Top-Down vs Bottom-Up — Walkthrough

> Goal: put the four forms of a DP solution next to each other on one example and one input, so the differences become concrete. This is the progression you will follow for every problem in the rest of the course: **brute force → memoization → tabulation → space optimization**.

---

## 1. Intuition (no code, no math — an everyday analogy)

### Two ways to prepare for an exam

**The question-driven student (top-down).** Starts with the exam question itself: "To answer question 6, I need facts 5 and 4." Looks those up; each of those needs earlier facts; and so on. Every fact found is written in a notebook so it is never looked up twice. The student only ever learns facts that the question actually needs — but has to keep a growing stack of "things I was in the middle of" in their head.

**The syllabus-driven student (bottom-up).** Opens the textbook at chapter 1 and works forward: chapter 1, then 2 (which uses 1), then 3 (which uses 1 and 2)… By the time the exam question arrives, everything it needs has already been learned. No stack of half-finished thoughts — but the student studies **every** chapter, even ones the exam never asks about.

**The minimalist (space-optimized bottom-up).** Notices that each chapter only uses the previous two. So the student keeps only the last two chapters' notes on the desk and recycles the rest. Same result, almost no desk space.

All three get the same correct answer. They differ in **who decides the order**, **how much they compute**, and **how much they have to hold at once**.

---

## 2. Problem statement in our own words

Compute the n-th Fibonacci number: `fib(0) = 0`, `fib(1) = 1`, `fib(n) = fib(n-1) + fib(n-2)` ([LeetCode 509](https://leetcode.com/problems/fibonacci-number/)). We solve it four times, in four self-contained files under [`solution/`](solution/), and run all four on the **same input, `n = 6`**, throughout this walkthrough.

---

## 3. Examples (input → output, with an explanation)

All four files check these values (the brute force skips 50 and 90; it would take far too long):

| n | fib(n) | Note |
|---|--------|------|
| 0 | 0 | edge: base case |
| 1 | 1 | edge: base case |
| 2 | 1 | first value from the recurrence |
| 6 | 8 | **our running input** |
| 10 | 55 | |
| 20 | 6,765 | |
| 30 | 832,040 | brute force: ~2.7 million calls, still OK |
| 50 | 12,586,269,025 | needs `long` |
| 90 | 2,880,067,194,370,816,120 | near the top of `long` (fib(92) is the largest that fits) |

---

## 4. Deriving the solution — the 7 questions

### 4.1 What is changing?
The index `n`.

### 4.2 What does the state represent?
`fib(i)` / `dp[i]` = the i-th Fibonacci number.

### 4.3 What are the choices?
None — each state always combines the two previous ones.

### 4.4 What is the recurrence?
`fib(i) = fib(i-1) + fib(i-2)`.

### 4.5 What are the base cases?
`fib(0) = 0`, `fib(1) = 1`.

### 4.6 What is the order of computation?
Top-down: the recursion finds it. Bottom-up: increasing `i`, because each state reads smaller indices.

### 4.7 Can the state space be optimized?
Yes. `dp[i]` reads only `dp[i-1]` and `dp[i-2]`, so we keep two variables: O(1) space.

Every one of the four versions below implements **exactly these answers**. Only the *execution strategy* changes.

---

## 5. Brute force: recursion

[`BruteForce.java`](solution/BruteForce.java)

```java
static long fib(int n) {
    if (n <= 1) return n;                 // base cases: fib(0) = 0, fib(1) = 1
    return fib(n - 1) + fib(n - 2);       // the recurrence, recomputing freely
}
```

On `n = 6`: **25 calls**. `fib(2)` alone is computed 5 times ([topic 04](../04-overlapping-subproblems/)).

```
fib(6) ─┬─ fib(5) ─┬─ fib(4) ─┬─ fib(3) ─ ...
        │          │          └─ fib(2) ─ ...       repeated
        │          └─ fib(3) ─ ...                  repeated
        └─ fib(4) ─ ... (the whole 9-call subtree again)
```

---

## 6. Memoization (top-down)

[`Memoization.java`](solution/Memoization.java). **What changed from brute force:** a cache array, one check, one store.

```java
// memo[i] stores fib(i); -1 means "not computed yet" (Fibonacci values are never negative)
private static long[] memo;

static long fib(int n) {
    memo = new long[n + 1];
    Arrays.fill(memo, -1);
    return solve(n);
}

private static long solve(int n) {
    if (n <= 1) return n;                   // base cases
    if (memo[n] != -1) return memo[n];      // check before computing
    memo[n] = solve(n - 1) + solve(n - 2);  // compute once, store
    return memo[n];
}
```

On `n = 6`: **11 calls** — 5 states computed (`fib(2)`…`fib(6)`), 3 cache hits, 3 base-case calls. Stack depth 6. The cache fills in the order 2, 3, 4, 5, 6.

```
fib(6) ─┬─ fib(5) ─┬─ fib(4) ─┬─ fib(3) ─┬─ fib(2) ─┬─ fib(1)
        │          │          │          │          └─ fib(0)
        │          │          │          └─ fib(1)
        │          │          └─ fib(2)  HIT
        │          └─ fib(3)  HIT
        └─ fib(4)  HIT
```

---

## 7. Tabulation (bottom-up)

[`Tabulation.java`](solution/Tabulation.java). **What changed from memoization:** the recursion is gone. We fill the cache ourselves, in the order the recursion used to fill it.

```java
static long fib(int n) {
    if (n <= 1) return n;                  // avoids writing dp[1] into a 1-cell table
    // dp[i] = the i-th Fibonacci number
    long[] dp = new long[n + 1];
    dp[0] = 0;
    dp[1] = 1;
    for (int i = 2; i <= n; i++) {         // increasing i: dp[i-1], dp[i-2] already filled
        dp[i] = dp[i - 1] + dp[i - 2];
    }
    return dp[n];
}
```

Mapping from memoization to tabulation, line by line:

| Memoization | Tabulation |
|-------------|------------|
| `memo` array of size n + 1 | `dp` array of size n + 1 |
| `if (n <= 1) return n;` | `dp[0] = 0; dp[1] = 1;` (base cells) |
| `if (memo[n] != -1) return memo[n];` | (not needed: each cell is filled once) |
| `memo[n] = solve(n-1) + solve(n-2);` | `dp[i] = dp[i-1] + dp[i-2];` |
| the recursion decides the order | `for (i = 2; i <= n; i++)` decides the order |

On `n = 6`: **5 loop iterations**, 0 function calls, stack depth 1.

```
 i:     0   1   2   3   4   5   6
 dp:    0   1   1   2   3   5   8
            └─┬─┘
         base cells, then each cell = sum of the two to its left
```

---

## 8. Space optimization

[`Optimized.java`](solution/Optimized.java). **What changed from tabulation:** the array is replaced by two variables, because `dp[i]` never reads anything older than `dp[i-2]`.

```
 table:    dp[0]  dp[1]  dp[2]  dp[3]  dp[4]  dp[5]  dp[6]
 needed when computing dp[5]:          ^^^^^  ^^^^^
                                      (dp[0..2] will never be read again)
```

```java
static long fib(int n) {
    if (n <= 1) return n;
    long previous = 0;   // fib(i-2), starts as fib(0)
    long current = 1;    // fib(i-1), starts as fib(1)
    for (int i = 2; i <= n; i++) {
        long next = previous + current;   // fib(i)
        previous = current;               // slide the window forward by one
        current = next;
    }
    return current;                       // after the loop, current = fib(n)
}
```

The order of the three assignments matters: compute `next` **before** overwriting anything, then shift `current` into `previous`, then `next` into `current`. Writing `previous = current; current = previous + current;` would add `current` to itself — a classic bug.

---

## 9. Dry run on a full example

All four versions on **`n = 6`**.

### 9.1 The space-optimized loop, step by step

| Iteration | i | previous (fib(i−2)) | current (fib(i−1)) | next = previous + current | after shift: previous, current |
|-----------|---|---------------------|--------------------|---------------------------|--------------------------------|
| start | — | 0 | 1 | — | 0, 1 |
| 1 | 2 | 0 | 1 | 1 | 1, 1 |
| 2 | 3 | 1 | 1 | 2 | 1, 2 |
| 3 | 4 | 1 | 2 | 3 | 2, 3 |
| 4 | 5 | 2 | 3 | 5 | 3, 5 |
| 5 | 6 | 3 | 5 | 8 | 5, **8** |

Return `current = 8`. The two variables are a "window" of width 2 sliding along the table of section 7.

### 9.2 The four versions side by side on n = 6

| | Brute force | Memoization | Tabulation | Optimized |
|---|------------|-------------|------------|-----------|
| Answer | 8 | 8 | 8 | 8 |
| Function calls | 25 | 11 | 1 | 1 |
| Additions performed | 12 | 5 | 5 | 5 |
| Values stored | 0 | 7 cells (5 used) | 7 cells | 2 variables |
| Max stack depth | 6 | 6 | 1 | 1 |
| Order of computation | 2, 3, 2, 4, 2, 3, 2, … (repeats) | 2, 3, 4, 5, 6 (recursion discovers it) | 2, 3, 4, 5, 6 (loop states it) | 2, 3, 4, 5, 6 |

(12 additions for brute force: one per non-base call; there are 25 − 13 = 12 such calls, since the `fib(6)` tree has 13 leaves.)

### 9.3 Cross-checking

All four `main` methods use the same expected values (section 3). Running

```bash
cd 00-foundations/08-top-down-vs-bottom-up/solution
java BruteForce.java && java Memoization.java && java Tabulation.java && java Optimized.java
```

must print only `PASS` lines. Whenever you write several versions of a solution, make them share tests. If two versions disagree on any input, at least one is wrong, and the brute force (slow but simple) is usually the one to trust on small inputs.

---

## 10. Complexity analysis

| Version | Time | Why | Extra space | Why |
|---------|------|-----|-------------|-----|
| Brute force | O(2ⁿ) | 2·fib(n+1) − 1 calls (≈ 1.618ⁿ), O(1) each | O(n) | recursion depth n |
| Memoization | O(n) | n − 1 states computed once, O(1) each; 2n − 1 calls in all | O(n) | n + 1 cache cells + n stack frames |
| Tabulation | O(n) | n − 1 loop iterations, O(1) each | O(n) | n + 1 table cells, no stack |
| Optimized | O(n) | same loop | **O(1)** | two variables |

Memoization and tabulation have the **same big-O**. Their difference is in constants (function calls vs. a tight loop), in stack usage, and in *which* states get computed (next section).

---

## 11. Why this approach works

All four versions evaluate the same recurrence with the same base cases, so they compute the same values. They only differ in how many times each value is computed and where it is kept:

- brute force recomputes values but each computation is correct (topic [02](../02-recursion-fundamentals/));
- memoization returns a stored value that equals what recomputing would give (topic [06](../06-memoization/));
- tabulation computes each cell after its dependencies, so by induction each cell is correct (topic [07](../07-tabulation/));
- the optimized version keeps exactly the cells that the remaining iterations will read, so discarding the others cannot change any result.

### The comparison table

This is the summary of the whole topic. Learn it.

| | **Top-down (memoization)** | **Bottom-up (tabulation)** | **Bottom-up + space optimization** |
|---|---------------------------|----------------------------|-----------------------------------|
| **Control flow** | Recursion. Starts at the original question; the call order discovers which states are needed and in what order. | Loops. Starts at the base cases; *you* choose an order in which every state comes after the states it reads. | Same loops as tabulation. |
| **States computed** | Only states **reachable** from the original question. Can be far fewer than all states. | **All** states in the table, needed or not. | All states, but only a sliding window is kept. |
| **Stack use** | O(recursion depth) — n for Fibonacci. Risk of `StackOverflowError` for depth ≳ 10⁴–10⁵. | O(1). No recursion. | O(1). |
| **Extra memory** | Cache for all states + stack. | Table for all states. | Only the cells still needed (here 2 variables). |
| **Speed in practice** | Slower constant: a function call and a cache check per request. | Faster: a tight loop, no checks. | Fastest, and cache-friendly. |
| **Ease of writing** | **Easiest**: add three lines to the brute force. No need to work out an order. | Medium: must choose table size, base cells and loop order correctly. | Hardest: must know exactly which cells are read, and update variables in the right order. |
| **Ease of debugging** | Easy to compare with brute force; trace with prints. | Easy to print the table after each step. | Harder: intermediate values are overwritten. |
| **Prefer when…** | First working version; the order of states is complicated (trees, intervals, game states, irregular jumps); only a few states are reachable. | Deep recursion (n large); simple order (indices that grow, grids); you want speed; you plan to optimize space. | Memory is limited or the table is huge, and each cell reads only a few recent cells. Usually the final submission. |

### When top-down computes far fewer states

For Fibonacci, every state 0…n is needed, so "reachable" and "all" coincide. That is not always true. Consider

```
f(0) = 1
f(n) = f(n / 2) + f(n / 3)        (integer division)
```

For `n = 12` the recursion only ever reaches 12, 6, 4, 3, 2, 1, 0 — **7 states**, not 13. For `n = 1,000,000` it reaches only **108** distinct states, and for `n = 10¹²` only **438**. A memoized version (with a `HashMap` as the cache) finishes instantly. A bottom-up table would need a cell for every value from 0 to n — a trillion cells for `n = 10¹²`. Here top-down is the clear winner.

---

## 12. Common mistakes

- **Optimizing too early.** Writing the two-variable version first and getting it wrong. Go in order: brute force → memo → table → variables. Each step is small and checkable.
- **Wrong variable update order** in the optimized version (`previous = current; current = previous + current;`). Compute the new value into a temporary first.
- **Assuming top-down and bottom-up compute the same set of states.** They do for Fibonacci, not in general (section 11).
- **Using memoization for very deep recursion** (n = 10⁶) and getting `StackOverflowError`. Switch to tabulation.
- **Not sharing tests across versions.** Different expected values in different files hide bugs. Use one set of tests for all versions.
- **Space-optimizing when you need the full table later**, e.g. to reconstruct *which* choices led to the optimum (like the route in [topic 05](../05-optimal-substructure/)). Keep the table if you need to walk back through it.

---

## 13. When to use this technique / how to recognize similar problems

A practical routine for every DP problem from now on:

1. Write the **brute force** recursion. Test it on small inputs. It defines the state, choices, recurrence and base cases.
2. Add **memoization**. Now it is fast enough for most judges. Keep the brute force for cross-checking.
3. If the recursion can be deep, or the judge is strict, convert to **tabulation** (method in [topic 14](../14-converting-memoization-to-tabulation/)).
4. If each cell reads only a few recent cells, **optimize space** (method in [topic 15](../15-space-optimization-basics/)).
5. Run the same tests on every version.

Stop at step 2 when the state order is complicated or few states are reachable. Go to step 4 when memory matters.

---

## 14. Summary and revision notes

- Four forms, one recurrence: **brute force → memoization → tabulation → space-optimized**.
- Top-down: recursion decides the order; computes only reachable states; uses the stack; easiest to write.
- Bottom-up: loops in an order you choose; computes all states; no stack; faster; enables space optimization.
- Space optimization: keep only the cells still read (for Fibonacci: 2 variables, O(1) space).
- Same big-O for memo and table (O(n) here); different constants, stack use and state sets.
- Always cross-check versions with the same tests.

```
┌──────────────────────────────────────────────────────────────────────────────────┐
│  brute force  fib(n) = fib(n-1) + fib(n-2)               time O(2^n)  space O(n) │
│  memoization  + check & store memo[n]                    time O(n)    space O(n) │
│  tabulation   dp[i] = dp[i-1] + dp[i-2], i = 2..n        time O(n)    space O(n) │
│  optimized    next = prev + cur; prev = cur; cur = next  time O(n)    space O(1) │
└──────────────────────────────────────────────────────────────────────────────────┘
```

---

## Check your understanding

1. In one sentence each, what changes going from brute force → memoization, from memoization → tabulation, and from tabulation → optimized?
2. A problem's recursion reaches only about 200 of its 10⁹ possible states. Which approach do you choose, and which data structure for the cache?
3. You must compute a Fibonacci-like value for `n = 5,000,000` (modulo 10⁹ + 7). Your memoized solution crashes with `StackOverflowError`. Why, and what do you change?
4. A student writes the optimized loop as `for (...) { previous = current; current = previous + current; }`. What does it return for `n = 6`, and why?

<details>
<summary>Answers</summary>

1. Brute force → memoization: add a cache, check it before computing and store after. Memoization → tabulation: remove the recursion and fill the cache with a loop in dependency order. Tabulation → optimized: keep only the cells the loop still reads (two variables) instead of the whole array.
2. **Top-down memoization**, with a `HashMap` as the cache (an array of 10⁹ cells would be far too large, and most would never be used). Tabulation would compute all 10⁹ states.
3. The first chain of recursive calls goes about 5,000,000 frames deep, more than the call stack can hold. Switch to tabulation (a loop has no recursion depth), and apply the space optimization so you do not need a 5-million-cell array either.
4. After `previous = current`, both variables hold the same value, so `current` just doubles each iteration: starting from 1, the five iterations (i = 2…6) give 2 → 4 → 8 → 16 → 32, and it returns **32** instead of 8. The old `previous` was lost before it was used. Compute `next = previous + current` first, then shift.

</details>

---

⬅ [07 — Tabulation](../07-tabulation/) · ➡ [09 — State Representation](../09-state-representation/) · 📘 [Topic README](README.md)
