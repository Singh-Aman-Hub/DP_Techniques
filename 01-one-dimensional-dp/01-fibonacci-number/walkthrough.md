# Fibonacci Number — Walkthrough

> The theory (what a state is, why memoization works, why tabulation needs an order) was covered in [00-foundations](../../00-foundations/). This walkthrough is deliberately short on theory and complete on procedure: it is the template that every later topic in this module follows.

## 1. Intuition

Picture a staircase of dominoes where each domino's height is the sum of the two dominoes before it. To know the height of domino 40, you could ask "what are dominoes 39 and 38?", and for each of those ask again, and again… and you would end up re-measuring domino 3 millions of times. Or you could simply walk from domino 0 upward, writing each height on a sticky note, and every new height is one addition away.

That second way is dynamic programming: **solve each small question once, write the answer down, reuse it.**

## 2. Problem statement in our own words

Given a non-negative integer `n`, return the `n`-th Fibonacci number. The sequence starts with `F(0) = 0` and `F(1) = 1`, and every later term is the sum of the two terms right before it.

Original: [LeetCode 509 · Fibonacci Number](https://leetcode.com/problems/fibonacci-number/) (there `0 ≤ n ≤ 30`).

## 3. Examples

| Input `n` | Output | Explanation |
|-----------|--------|-------------|
| 0 | 0 | Base case given by the definition. |
| 1 | 1 | Base case given by the definition. |
| 2 | 1 | `F(1) + F(0) = 1 + 0` |
| 5 | 5 | `0, 1, 1, 2, 3, 5` — the term at index 5 is 5 |
| 10 | 55 | `…, 21, 34, 55` |

**Running example for the whole lesson: `n = 5`, answer `5`.**

## 4. Deriving the solution — the 7 questions

This is the [7-question framework](../../00-foundations/16-the-dp-problem-solving-framework/) applied for real. Write these answers down *before* any code, every time.

### Q1. What is changing?

Only one quantity: the index `i` of the term we want. To answer for `i` we need answers for smaller indices, so `i` shrinks as we go deeper.

### Q2. What does the state represent?

> `dp[i]` = the `i`-th Fibonacci number.

One integer identifies the subproblem completely, so this is **one-dimensional DP** (see [State representation](../../00-foundations/09-state-representation/)).

### Q3. What are the choices?

There is no real decision here: `F(i)` is *defined* from `F(i-1)` and `F(i-2)`. Fibonacci is a pure "combine the dependencies" problem. In the next topics a state will have genuine choices (take one step or two, rob this house or skip it), but the mechanics stay the same.

### Q4. What is the recurrence?

```
dp[i] = dp[i-1] + dp[i-2]        for i ≥ 2
```

- `dp[i-1]` — the term right before `i`.
- `dp[i-2]` — the term two places before `i`.
- `+` — the definition says to add them.

### Q5. What are the base cases?

```
dp[0] = 0
dp[1] = 1
```

Why two of them? The recurrence reaches back **two** steps. For `i = 1` it would ask for `dp[-1]`, which does not exist, so `dp[1]` must be given directly. The rule of thumb from [State transitions and base cases](../../00-foundations/10-state-transitions-and-base-cases/): *a recurrence that looks back `k` steps needs `k` base cases.*

### Q6. What is the order of computation?

`dp[i]` depends on `dp[i-1]` and `dp[i-2]`, which are **smaller** indices. So we fill from small to large: `i = 2, 3, …, n`.

```
dp[0]  dp[1]  dp[2]  dp[3]  dp[4]  dp[5]
  │      │     ▲ ▲
  └──────┼─────┘ │        each cell's arrows come from the left,
         └───────┘        so a left-to-right loop is always safe
```

### Q7. Can the state space be optimized?

Yes. `dp[i]` reads only the **two** previous cells. Once `dp[i]` is computed, `dp[i-2]` is never read again. So two variables are enough: `O(n)` space → `O(1)` space.

## 5. Brute force: recursion

```java
static int fib(int n) {
    if (n <= 1) return n;            // base cases
    return fib(n - 1) + fib(n - 2);  // recurrence
}
```
Full file: [`solution/BruteForce.java`](solution/BruteForce.java).

### Recursion tree for `fib(5)`

```
                              fib(5)
                    ┌───────────┴───────────┐
                 fib(4)                   fib(3)
             ┌─────┴─────┐             ┌────┴────┐
          fib(3)       fib(2)       fib(2)     fib(1)
         ┌──┴──┐       ┌─┴─┐        ┌─┴─┐
      fib(2) fib(1) fib(1) fib(0) fib(1) fib(0)
      ┌─┴─┐
   fib(1) fib(0)
```

Count the repeated work:

| Call | Times computed |
|------|----------------|
| `fib(3)` | 2 |
| `fib(2)` | 3 |
| `fib(1)` | 5 |
| `fib(0)` | 3 |
| **total calls** | **15** for a problem with only 6 distinct states |

### Why it is slow

Let `C(n)` be the number of calls to compute `fib(n)`. Then `C(n) = C(n-1) + C(n-2) + 1`, which grows just like the Fibonacci numbers themselves — roughly `1.618^n`. For `n = 30` that is about 2.7 million calls; for `n = 50` it is about 40 billion. The tree has [overlapping subproblems](../../00-foundations/04-overlapping-subproblems/): the same small questions are asked over and over.

## 6. Memoization (top-down)

Keep the recursion, add a notebook. Before computing `fib(k)`, look it up; after computing it, write it down.

```java
private static long[] memo;           // memo[k] = F(k), or -1 if not computed yet

private static long solve(int k) {
    if (k <= 1) return k;
    if (memo[k] != -1) return memo[k];      // cache hit
    memo[k] = solve(k - 1) + solve(k - 2);
    return memo[k];
}
```
Full file: [`solution/Memoization.java`](solution/Memoization.java).

**What is cached?** One number per index `k`: `F(k)`. We use `-1` as "empty" because no Fibonacci number is negative.

### Dry run for `n = 5`

Calls happen in this order (`→` means "calls", `✓` means "returns"):

| Step | Call | What happens | memo after the step |
|------|------|--------------|---------------------|
| 1 | `solve(5)` | not cached, calls `solve(4)` | `[-1,-1,-1,-1,-1,-1]` |
| 2 | `solve(4)` | not cached, calls `solve(3)` | unchanged |
| 3 | `solve(3)` | not cached, calls `solve(2)` | unchanged |
| 4 | `solve(2)` | not cached, calls `solve(1)` ✓1 and `solve(0)` ✓0 | `memo[2] = 1` |
| 5 | back in `solve(3)` | calls `solve(1)` ✓1 → `memo[3] = 1 + 1` | `memo[3] = 2` |
| 6 | back in `solve(4)` | calls `solve(2)` → **cache hit**, returns 1 | `memo[4] = 2 + 1 = 3` |
| 7 | back in `solve(5)` | calls `solve(3)` → **cache hit**, returns 2 | `memo[5] = 3 + 2 = 5` |

The tree collapses into a single path down the left side plus two cache hits:

```
fib(5)
 ├─ fib(4)
 │   ├─ fib(3)
 │   │   ├─ fib(2)
 │   │   │   ├─ fib(1)  base
 │   │   │   └─ fib(0)  base
 │   │   └─ fib(1)      base
 │   └─ fib(2)          HIT (memo[2] = 1)
 └─ fib(3)              HIT (memo[3] = 2)
```

9 calls instead of 15. For `n = 50`, it makes about 100 calls instead of 40 billion.

## 7. Tabulation (bottom-up)

Turn the recursion inside out: start at the base cases and loop upward (see [Converting memoization to tabulation](../../00-foundations/14-converting-memoization-to-tabulation/)).

```java
long[] dp = new long[n + 1];   // dp[i] = F(i)
dp[0] = 0;
dp[1] = 1;
for (int i = 2; i <= n; i++) {
    dp[i] = dp[i - 1] + dp[i - 2];
}
return dp[n];
```
Full file: [`solution/Tabulation.java`](solution/Tabulation.java).

### Filling the table for `n = 5`

```
start (base cases only)
 i     0   1   2   3   4   5
 dp    0   1   .   .   .   .

i = 2: dp[2] = dp[1] + dp[0] = 1 + 0 = 1
 dp    0   1   1   .   .   .

i = 3: dp[3] = dp[2] + dp[1] = 1 + 1 = 2
 dp    0   1   1   2   .   .

i = 4: dp[4] = dp[3] + dp[2] = 2 + 1 = 3
 dp    0   1   1   2   3   .

i = 5: dp[5] = dp[4] + dp[3] = 3 + 2 = 5
 dp    0   1   1   2   3   5      ← answer dp[5] = 5
```

Note the guard `if (n <= 1) return n;` in the real file: for `n = 0` the array has one cell, so writing `dp[1]` would crash.

## 8. Space optimization

Look at the table above: when computing `dp[5]`, cells `dp[0]`, `dp[1]` and `dp[2]` are dead — nothing will ever read them again. Only a **window of two cells** is alive at any time.

```
window slides right each iteration:
 [0 1] 1  2  3  5
  0 [1 1] 2  3  5
  0  1 [1 2] 3  5
  0  1  1 [2 3] 5
  0  1  1  2 [3 5]   → answer is the right end of the window
```

```java
long prev2 = 0, prev1 = 1;          // F(i-2), F(i-1)
for (int i = 2; i <= n; i++) {
    long current = prev1 + prev2;
    prev2 = prev1;                   // shift first...
    prev1 = current;                 // ...then store the new value
}
return prev1;
```
Full file: [`solution/Optimized.java`](solution/Optimized.java).

The order of the two assignments matters: if you write `prev1 = current` first, you lose the old `prev1` before copying it into `prev2`.

## 9. Dry run on a full example

`n = 5`, space-optimized version, showing variable values **at the end** of each iteration:

| i | current = prev1 + prev2 | prev2 | prev1 |
|---|-------------------------|-------|-------|
| start | — | 0 | 1 |
| 2 | 1 + 0 = 1 | 1 | 1 |
| 3 | 1 + 1 = 2 | 1 | 2 |
| 4 | 2 + 1 = 3 | 2 | 3 |
| 5 | 3 + 2 = 5 | 3 | 5 |

Answer `prev1 = 5`. It matches the tabulation table cell by cell, which is exactly what we want: the optimized version is the same DP with less memory.

## 10. Complexity analysis

| Approach | Time | Space | Why |
|----------|------|-------|-----|
| Brute force | `O(2^n)` (≈ `1.618^n`) | `O(n)` | Each call spawns two calls; the call stack is at most `n` deep. |
| Memoization | `O(n)` | `O(n)` | `n+1` states, each computed once with `O(1)` work; memo array + recursion stack. |
| Tabulation | `O(n)` | `O(n)` | One loop of `n-1` iterations; one array of `n+1` cells. |
| Optimized | `O(n)` | `O(1)` | Same loop; two variables regardless of `n`. |

A note on overflow: `F(46) = 1,836,311,903` is the largest that fits in an `int`. The DP files use `long` so they can be tested up to `F(50) = 12,586,269,025`; `long` itself overflows after `F(92)`. LeetCode only asks up to `n = 30`, so `int` is enough there.

## 11. Why this approach works

- **Every state is computed correctly:** the base cases are correct by definition, and the recurrence is the definition. If `dp[i-1]` and `dp[i-2]` are right, then `dp[i]` is right. Starting from correct base cases and moving one index at a time, every cell is right (this is induction).
- **Every state is computed before it is needed:** the loop goes left to right and all dependencies point left.
- **Nothing is lost by optimizing:** the two variables hold exactly the cells that the recurrence still needs.

## 12. Common mistakes

1. **Only one base case.** Writing `if (n == 0) return 0;` alone makes `fib(1)` call `fib(-1)` → infinite recursion or stack overflow.
2. **Array of size `n` instead of `n + 1`.** You need an index `n`, so the array needs `n + 1` cells.
3. **Writing `dp[1]` when `n = 0`.** Guard small `n` before creating the table.
4. **Wrong update order** in the two-variable version (overwriting `prev1` before copying it).
5. **Using `int` for large `n`.** Overflow is silent in Java; the result just becomes wrong (and may be negative).
6. **Re-creating the memo inside the recursive function** instead of once before the first call — then nothing is ever reused.

## 13. When to use this technique / how to recognize similar problems

Recognize the Fibonacci skeleton when:

- the input is a single number `n` or a single array/string scanned left to right;
- the answer for position `i` depends on a **fixed, small number** of earlier positions (`i-1`, `i-2`, maybe `i-3`);
- the base cases are the first one or two positions.

The next topic, [Climbing Stairs](../02-climbing-stairs/), looks like a word problem but turns out to be *exactly* this recurrence.

## 14. Summary and revision notes

- State: `dp[i]` = the `i`-th Fibonacci number. One index → 1D DP.
- Base cases: two, because the recurrence looks back two steps.
- Order: left to right.
- Optimization: only the last two values are alive → two variables.
- Brute force `O(2^n)`; every DP version `O(n)` time; optimized `O(1)` space.

```
┌──────────────────────────────────────────┐
│  dp[0] = 0,  dp[1] = 1                   │
│  dp[i] = dp[i-1] + dp[i-2]   (i ≥ 2)     │
│  answer: dp[n]                           │
└──────────────────────────────────────────┘
```

## Exercises

1. Without running code, how many total calls does the brute-force `fib(6)` make?

<details>
<summary>Answer</summary>

Use `C(n) = C(n-1) + C(n-2) + 1` with `C(0) = C(1) = 1`: `C(2)=3, C(3)=5, C(4)=9, C(5)=15, C(6)=25`. So **25 calls**.
</details>

2. The Tribonacci sequence is `T(0)=0, T(1)=1, T(2)=1, T(n)=T(n-1)+T(n-2)+T(n-3)`. Answer Q5 and Q7 for it.

<details>
<summary>Answer</summary>

Q5: the recurrence looks back three steps, so it needs **three base cases** `T(0), T(1), T(2)`. Q7: only the last three values are alive, so **three rolling variables** give `O(1)` space. This is [LeetCode 1137](https://leetcode.com/problems/n-th-tribonacci-number/).
</details>

3. A friend's optimized loop is `for (i = 2..n) { prev1 = prev1 + prev2; prev2 = prev1; }`. What does it return for `n = 5`, and what is the bug?

<details>
<summary>Answer</summary>

After the first line `prev1` is already the new value, so `prev2 = prev1` copies the **new** value, not the old one. The pairs become (prev2, prev1): start (0,1) → (1,1) → (2,2) → (4,4) → (8,8). It returns **8** instead of 5. Fix: save the new value in a temporary variable, shift `prev2 = prev1`, then assign `prev1 = current`.
</details>
