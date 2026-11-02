# Tabulation — Walkthrough

> Goal: compute the same answers as memoization, but with a loop that fills a table from the smallest subproblem upward. You will learn how to design the table, how to choose the loop order, and how to watch the table fill step by step.

---

## 1. Intuition (no code, no math — an everyday analogy)

### Building a staircase of bricks

Picture a builder stacking a tower of bricks, where each new layer rests on the two layers below it. The builder **cannot** start with the top layer — there is nothing for it to rest on. So the builder lays the bottom two layers first (the foundations), then the third on top of them, then the fourth, and so on, always building on layers that are already there. When the last layer is placed, the tower is done.

```
 layer 6   [ 8 ]   <- rests on layers 5 and 4
 layer 5   [ 5 ]
 layer 4   [ 3 ]
 layer 3   [ 2 ]
 layer 2   [ 1 ]
 layer 1   [ 1 ]   <- foundation
 layer 0   [ 0 ]   <- foundation
```

That is tabulation. **Memoization** was like a manager at the top shouting down "I need layer 6!", which makes someone shout "I need layer 5!", and so on down to the ground, with answers passed back up. **Tabulation** skips the shouting: the builder simply knows that the lower layers are always needed first, and builds them in order.

---

## 2. Problem statement in our own words

1. Compute `fib(n)` (`fib(0) = 0`, `fib(1) = 1`, `fib(n) = fib(n-1) + fib(n-2)`) with **no recursion**. ([LeetCode 509](https://leetcode.com/problems/fibonacci-number/))
2. Count the ways to climb `n` stairs with 1- or 2-steps, with no recursion. ([LeetCode 70](https://leetcode.com/problems/climbing-stairs/))

Code: [`solution/Tabulation.java`](solution/Tabulation.java). It prints the Fibonacci table after every step, and also contains a version with the **wrong** loop order so you can see it fail.

---

## 3. Examples (input → output, with an explanation)

| Input | Output | Table when done |
|-------|--------|-----------------|
| `fib(6)` | 8 | `[0, 1, 1, 2, 3, 5, 8]` |
| `fib(1)` | 1 | (answered directly, no table needed) |
| `fib(50)` | 12,586,269,025 | 51 cells |
| `waysToClimb(5)` | 8 | `[1, 1, 2, 3, 5, 8]` |
| `waysToClimb(1)` | 1 | `[1, 1]` |

---

## 4. Deriving the solution — the 7 questions

For Fibonacci. Notice how each answer becomes a concrete decision about the **table**.

### 4.1 What is changing?
`n`. So the table is **one-dimensional**: one cell per value of `n`.

### 4.2 What does the state represent?
`dp[i]` = "the i-th Fibonacci number". Write this sentence as a comment above the array declaration — always.

### 4.3 What are the choices?
None; each cell always combines two others.

### 4.4 What is the recurrence?
`dp[i] = dp[i-1] + dp[i-2]` — the same recurrence as the recursion, with `fib(…)` replaced by `dp[…]`.

### 4.5 What are the base cases?
`dp[0] = 0`, `dp[1] = 1`. These are the **base cells**, written before the loop. The table needs indices 0…n, so its size is **n + 1**.

### 4.6 What is the order of computation?
`dp[i]` reads `dp[i-1]` and `dp[i-2]`, which have **smaller** indices. So we fill in **increasing** order of `i`: `for (int i = 2; i <= n; i++)`. The loop starts at 2 because 0 and 1 are base cells.

### 4.7 Can the state space be optimized?
Each cell reads only the two cells right before it, so two variables suffice ([topic 08](../08-top-down-vs-bottom-up/)).

---

## 5. Brute force: recursion

The starting point, from [topic 02](../02-recursion-fundamentals/): `fib(n) = fib(n-1) + fib(n-2)` with plain recursion. O(2ⁿ) time because of overlapping subproblems ([topic 04](../04-overlapping-subproblems/)).

---

## 6. Memoization (top-down)

From [topic 06](../06-memoization/): the same recursion with a cache. O(n) time, but n stack frames deep and a cache check per call.

Look at the **order in which memoization fills its cache** (from topic 06's dry run): `memo[2]`, then `memo[3]`, `memo[4]`, `memo[5]`, `memo[6]`. Smallest first! The recursion dives to the bottom and fills the cache on the way up. Tabulation does the same filling **directly**, without the dive.

---

## 7. Tabulation (bottom-up)

### 7.1 The recipe

Given a recurrence, a tabulated solution is always built the same way:

1. **Meaning:** decide what one cell means, and write it as a comment.
2. **Size:** make the table large enough for every state, including the answer's state (here `n + 1` cells for indices 0…n).
3. **Base cells:** fill them directly.
4. **Loop:** visit the remaining cells in an order where everything a cell reads is already filled, and apply the recurrence.
5. **Answer:** return the cell for the original question (`dp[n]`).

### 7.2 The code

```java
static long fib(int n) {
    if (n <= 1) return n;                 // the table needs at least 2 cells; answer directly
    // dp[i] = the i-th Fibonacci number
    long[] dp = new long[n + 1];
    dp[0] = 0;                            // base cases first
    dp[1] = 1;
    for (int i = 2; i <= n; i++) {        // small to large: dp[i-1] and dp[i-2] are ready
        dp[i] = dp[i - 1] + dp[i - 2];
    }
    return dp[n];
}
```

The guard `if (n <= 1) return n;` matters: for `n = 0` the table would have 1 cell, and `dp[1] = 1` would throw `ArrayIndexOutOfBoundsException`.

### 7.3 Watching the table fill

`java Tabulation.java` prints the table after every step for `n = 6`:

```
start      [0, 1, 0, 0, 0, 0, 0]
fill i = 2   [0, 1, 1, 0, 0, 0, 0]   dp[2] = 1 + 0
fill i = 3   [0, 1, 1, 2, 0, 0, 0]   dp[3] = 1 + 1
fill i = 4   [0, 1, 1, 2, 3, 0, 0]   dp[4] = 2 + 1
fill i = 5   [0, 1, 1, 2, 3, 5, 0]   dp[5] = 3 + 2
fill i = 6   [0, 1, 1, 2, 3, 5, 8]   dp[6] = 5 + 3
```

The same, drawn as a table with the two cells being read marked `^` and the cell being written marked `*`:

```
             i:   0    1    2    3    4    5    6
 base         :   0    1    .    .    .    .    .
 i = 2        :   0^   1^   1*   .    .    .    .
 i = 3        :   0    1^   1^   2*   .    .    .
 i = 4        :   0    1    1^   2^   3*   .    .
 i = 5        :   0    1    1    2^   3^   5*   .
 i = 6        :   0    1    1    2    3^   5^   8*      answer = dp[6] = 8
```

At every step the two `^` cells are to the **left** of `*` — they were filled earlier. That is what "correct loop order" means.

### 7.4 Loop order: what happens if it is wrong

`Tabulation.java` also contains this version, which runs the loop the **other** way:

```java
for (int i = n; i >= 2; i--) {        // BUG: dp[i-1] has not been filled yet (still 0)
    dp[i] = dp[i - 1] + dp[i - 2];
}
```

Trace for `n = 6`. Java initialises the array to zeros, so unfilled cells read as 0:

```
             i:   0    1    2    3    4    5    6
 base         :   0    1    0    0    0    0    0
 i = 6        :   0    1    0    0    0^   0^   0*     reads two unfilled cells!
 i = 5        :   0    1    0    0^   0^   0*   0
 i = 4        :   0    1    0^   0^   0*   0    0
 i = 3        :   0    1^   0^   1*   0    0    0     dp[3] = 0 + 1, too late to help
 i = 2        :   0^   1^   1*   1    0    0    0
                                                    returns dp[6] = 0   (wrong; should be 8)
```

No error, no crash — just a wrong answer. The program checks that this version does **not** return 8.

**How to choose the order, in general:** look at the recurrence and list the cells it reads.

| Recurrence reads… | Those cells have… | So loop… |
|-------------------|-------------------|----------|
| `dp[i-1]`, `dp[i-2]` | smaller indices | `i` upward |
| `dp[i+1]`, `dp[i+2]` (e.g. "from position i to the end") | larger indices | `i` downward |
| `dp[i-1][j]`, `dp[i][j-1]` (grids) | smaller row or column | rows upward, columns upward |

[Topic 11](../11-transition-dependencies-and-computation-order/) covers this for harder cases.

### 7.5 Second example: Climbing Stairs

Same recipe, different meaning and different base cells.

```java
static long waysToClimb(int n) {
    // ways[i] = number of ways to reach stair i using 1- or 2-steps
    long[] ways = new long[Math.max(n + 1, 2)];   // at least 2 cells so ways[1] exists
    ways[0] = 1;                          // stand still at the bottom
    ways[1] = 1;                          // one single step
    for (int i = 2; i <= n; i++) {
        ways[i] = ways[i - 1] + ways[i - 2];   // last move was a 1-step or a 2-step
    }
    return ways[n];
}
```

Filling it for `n = 5`:

```
 i         :   0    1    2    3    4    5
 base      :   1    1    .    .    .    .
 i = 2     :   1^   1^   2*   .    .    .     {11, 2}
 i = 3     :   1    1^   2^   3*   .    .     {111, 12, 21}
 i = 4     :   1    1    2^   3^   5*   .
 i = 5     :   1    1    2    3^   5^   8*    answer = 8
```

Here the guard is done differently: the table always has at least 2 cells, so `ways[1] = 1` is always safe, and the loop simply does not run when `n < 2`. Both styles are fine; pick one and be consistent.

### 7.6 No recursion overhead

What exactly does tabulation save compared with memoization?

| Cost | Memoization | Tabulation |
|------|-------------|------------|
| Function calls | 2n − 1 calls, each creating and destroying a stack frame | 0 (one loop) |
| Cache checks | one `if (memo[k] != -1)` per call | none: every cell is filled exactly once |
| Call stack | n frames deep → `StackOverflowError` around n ≈ 10⁴–10⁵ with default settings | O(1) stack |
| Memory for answers | n + 1 cells | n + 1 cells |

Both are O(n) time. Tabulation is typically a few times faster in practice (a tight loop is very friendly to the CPU), and it never overflows the stack. That is why [CSES Dice Combinations](https://cses.fi/problemset/task/1633) (n up to 10⁶) is solved with a table.

The cost: you must work out the loop order yourself, and tabulation computes **every** state, even ones the recursion would never have needed (for Fibonacci that is all of them anyway).

---

## 8. Space optimization

The `^` marks in 7.3 show that `dp[i]` only ever reads the **two** cells just before it. Cells further left are never read again. So instead of an array, two variables are enough — O(1) space. The code is in [topic 08](../08-top-down-vs-bottom-up/), and the general technique in [topic 15](../15-space-optimization-basics/).

---

## 9. Dry run on a full example

`fib(10)`, the table after each iteration:

| Step | i | dp[i−2] | dp[i−1] | dp[i] = dp[i−1] + dp[i−2] | Table so far (indices 0…10) |
|------|---|---------|---------|---------------------------|-----------------------------|
| base | — | — | — | — | `0 1 . . . . . . . . .` |
| 1 | 2 | 0 | 1 | 1 | `0 1 1 . . . . . . . .` |
| 2 | 3 | 1 | 1 | 2 | `0 1 1 2 . . . . . . .` |
| 3 | 4 | 1 | 2 | 3 | `0 1 1 2 3 . . . . . .` |
| 4 | 5 | 2 | 3 | 5 | `0 1 1 2 3 5 . . . . .` |
| 5 | 6 | 3 | 5 | 8 | `0 1 1 2 3 5 8 . . . .` |
| 6 | 7 | 5 | 8 | 13 | `0 1 1 2 3 5 8 13 . . .` |
| 7 | 8 | 8 | 13 | 21 | `0 1 1 2 3 5 8 13 21 . .` |
| 8 | 9 | 13 | 21 | 34 | `0 1 1 2 3 5 8 13 21 34 .` |
| 9 | 10 | 21 | 34 | **55** | `0 1 1 2 3 5 8 13 21 34 55` |

9 loop iterations (n − 1), one addition each. Answer: `dp[10] = 55`.

---

## 10. Complexity analysis

**Time.** Count loop iterations × work per iteration. The loop runs for `i = 2 … n`: **n − 1 iterations**, each doing one addition and one array write, O(1). Total **O(n)**. Unlike recursion, there is nothing hidden: what you see in the loop is all the work.

**Space.** The table has **n + 1** cells: **O(n)**. There is no recursion, so the call stack stays O(1).

General rule for tabulation:

```
time  = (number of cells) × (work to fill one cell)
space = (number of cells)          -- before any space optimization
```

| Approach | Time | Extra space | Stack |
|----------|------|-------------|-------|
| Plain recursion | O(2ⁿ) | — | O(n) |
| Memoization | O(n) | O(n) cache | O(n) |
| **Tabulation** | **O(n)** | **O(n) table** | **O(1)** |

---

## 11. Why this approach works

Each cell is computed by the same recurrence as the recursive version, so it gets the same value — **provided its ingredients are already correct when it is computed**. The base cells are correct by definition. The loop visits cells in increasing `i`, and `dp[i]` only reads smaller indices, which were all filled (correctly) in earlier iterations. By induction on `i`, every cell is correct when it is written, and in particular `dp[n]`.

This is why loop order is not a detail: it is the induction step of the correctness proof.

---

## 12. Common mistakes

| Mistake | Symptom | Fix |
|---------|---------|-----|
| Loop in the wrong direction | Wrong answer (often 0), no error | Loop so that every cell read is already filled (7.4). |
| Table of size `n` instead of `n + 1` | `ArrayIndexOutOfBoundsException` at `dp[n]` | Size = largest index needed + 1. |
| Writing base cell `dp[1]` when `n = 0` | `ArrayIndexOutOfBoundsException` | Guard tiny inputs, or allocate at least as many cells as base cells. |
| Starting the loop at 0 or 1 | Reads `dp[-1]` → exception, or overwrites a base cell | Start the loop after the last base cell. |
| Returning `dp[n-1]` or the last cell of a padded table | Off-by-one answer | Return the cell whose *meaning* is the original question. |
| Forgetting the meaning of a cell | Wrong recurrence or base cells | Write `// dp[i] = ...` above the array, always. |
| `int` table for fast-growing values | Overflow | `long`, or `% MOD` if the problem asks. |

---

## 13. When to use this technique / how to recognize similar problems

Prefer tabulation when:

- the recursion would be **deep** (n in the hundreds of thousands or more);
- the order of states is **easy to see** (indices that only grow, grids filled row by row);
- you will want to **optimize space** afterwards (only possible with an explicit order);
- you need maximum speed (contests, tight time limits).

Prefer memoization when the order is hard to work out, or when only a small part of the state space is reachable. [Topic 08](../08-top-down-vs-bottom-up/) compares both side by side, and [topic 14](../14-converting-memoization-to-tabulation/) gives a step-by-step method for converting one into the other.

---

## 14. Summary and revision notes

- Tabulation = **table + loop**, filled from the base cells upward. Bottom-up.
- Recipe: **meaning** of a cell → **size** → **base cells** → **loop in dependency order** → **return the answer cell**.
- The loop order must put every cell **after** the cells it reads. Wrong order = silent wrong answers.
- No recursion: no call overhead, no cache checks, no stack overflow.
- Time = cells × work per cell; space = cells (before optimization).

```
┌────────────────────────────────────────────────┐
│  // dp[i] = the i-th Fibonacci number          │
│  dp[0] = 0;  dp[1] = 1;                        │
│  for i = 2 .. n:   dp[i] = dp[i-1] + dp[i-2]   │
│  return dp[n]                                  │
└────────────────────────────────────────────────┘
```

---

## Check your understanding

1. Write the tabulated solution for the Tribonacci numbers: `t(0) = 0`, `t(1) = 1`, `t(2) = 1`, `t(n) = t(n-1) + t(n-2) + t(n-3)`. What is the table size, which cells are base cells, where does the loop start, and what is `t(6)`?
2. A recurrence is `f(i) = f(i+1) + f(i+2)` for `i < n`, with `f(n) = 1` and `f(n+1) = 0`, and the answer is `f(0)`. Which direction must the loop run? What size must the table have?
3. Fill the min-cost-stairs table `best[0..3]` for `cost = [10, 15, 20]` using `best[i] = min(best[i-1] + cost[i-1], best[i-2] + cost[i-2])` with `best[0] = best[1] = 0`.
4. Why does tabulation never throw `StackOverflowError`, while memoization can, even though both are O(n) time?

<details>
<summary>Answers</summary>

1. Size `n + 1`; base cells `t[0] = 0, t[1] = 1, t[2] = 1`; loop `i = 3 … n`; guard `n <= 2` (return the base value directly). Table: 0, 1, 1, 2, 4, 7, **13** → `t(6) = 13`.
2. `f(i)` reads larger indices, so the loop must run **downward**: `for (int i = n - 1; i >= 0; i--)`. The table needs indices 0…n+1: size **n + 2**.
3. `best = [0, 0, min(0+15, 0+10) = 10, min(10+20, 0+15) = 15]` → answer **15**.
4. Tabulation uses a loop, so there is only one stack frame no matter how large n is. Memoization calls itself recursively; the first chain of calls goes n frames deep before anything returns, and the stack has a fixed size.

</details>

---

⬅ [06 — Memoization](../06-memoization/) · ➡ [08 — Top-Down vs Bottom-Up](../08-top-down-vs-bottom-up/) · 📘 [Topic README](README.md)
