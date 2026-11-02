# Coin Change II — Walkthrough

## 1. Intuition

You are at a shop counter with an unlimited supply of 1-rupee, 2-rupee and 5-rupee coins, and you must pay exactly 5 rupees. How many different **handfuls** of coins could you put on the counter?

A handful is just a pile: the shopkeeper does not care whether you dropped the 1 before the 2. So these are the only possibilities:

```
{5}            one 5-coin
{2, 2, 1}      two 2-coins and a 1-coin
{2, 1, 1, 1}   one 2-coin and three 1-coins
{1, 1, 1, 1, 1}
```

That is **4** handfuls.

A safe way to list handfuls without repeats is to sort the coins in your hand: first decide how many 1s, then how many 2s, then how many 5s. Once you have moved on from 1s to 2s, you never pick up a 1 again. Every handful is built in exactly one way this way, so nothing is counted twice.

Hold on to that sentence: **"once you have moved on from a coin type, you never come back to it."** The whole topic, including the loop-order question, follows from it.

## 2. Problem statement in our own words

Original: [LeetCode 518 — Coin Change II](https://leetcode.com/problems/coin-change-ii/).

You get an array `coins` of distinct positive denominations and a non-negative `amount`. Each coin can be used as many times as you like. Return how many different **combinations** (multisets) of coins add up to exactly `amount`. Two combinations are the same if they use the same number of each coin, whatever the order. If no combination works, return 0. LeetCode promises that the final answer fits in a signed 32-bit integer.

## 3. Examples

| coins | amount | answer | why |
|---|---|---|---|
| `[1, 2, 5]` | 5 | 4 | `{5}`, `{2,2,1}`, `{2,1,1,1}`, `{1,1,1,1,1}` |
| `[1, 2]` | 3 | 2 | `{1,1,1}`, `{1,2}` (note: `{2,1}` is the same pile) |
| `[2]` | 3 | 0 | 2-coins only make even amounts |
| `[10]` | 10 | 1 | `{10}` |
| `[7]` | 0 | 1 | the empty pile `{}` pays 0 — exactly one way |

The last row surprises many people. Paying 0 means "put nothing on the counter", and that is one valid action. We will need this fact as our base case.

We use **coins = [1, 2, 5], amount = 5** as the running example in every approach below. For the loop-order discussion we also use the tiny **coins = [1, 2], amount = 3**.

## 4. Deriving the solution — the 7 questions

### 4.1 What is changing?

Two things move as we decide:

1. **Which coin type we are currently deciding about** — an index into `coins`.
2. **How much money is still unpaid** — the remaining amount.

Following the intuition, we decide coin types in array order and never go back.

### 4.2 What does the state represent?

> `ways(index, remaining)` = the number of combinations that pay exactly `remaining`, using only the coin types `coins[index], coins[index+1], …, coins[n-1]` (unlimited copies of each).

The answer to the problem is `ways(0, amount)`.

### 4.3 What are the choices?

Standing at coin type `index` with `remaining` unpaid:

- **Skip** — "I will use no more of this coin type." Move on: `ways(index + 1, remaining)`.
- **Take** — "I put one more of this coin on the counter." Pay it and **stay on the same index**, because we may take it again: `ways(index, remaining - coins[index])`. Allowed only if `coins[index] <= remaining`.

This is exactly the [unbounded knapsack](../07-unbounded-knapsack/) choice pair.

### 4.4 What is the recurrence?

The two choices lead to **disjoint** sets of combinations: in the "take" branch the pile contains at least one more copy of `coins[index]`; in the "skip" branch it contains no more. A combination can't be in both, so we **add** the counts:

```
ways(index, remaining) = ways(index + 1, remaining)                    // skip
                       + ways(index, remaining - coins[index])         // take (if it fits)
```

Compare with [Coin Change](../08-coin-change/): there the two options were combined with `min` because we wanted the single best pile. Here we want *all* piles, so we use `+`.

### 4.5 What are the base cases?

- `remaining == 0` → **1**. Everything has been paid; the pile is complete. Choosing "no more coins" is exactly one way. (Return 0 here and every count collapses to 0.)
- `index == n` and `remaining > 0` → **0**. Money is still owed and no coin types are left.

Check order matters: test `remaining == 0` first, so `ways(n, 0)` correctly returns 1.

### 4.6 What is the order of computation?

`ways(index, remaining)` needs `ways(index + 1, ·)` (a later coin type) and `ways(index, smaller remaining)` (same coin type, less money). In a bottom-up table that means: finish one coin type's row before the next one uses it, and inside a row go from small amounts to large amounts.

In the tabulation below we flip the meaning to "the **first** `i` coin types" (a prefix instead of a suffix), which is the more common bottom-up form. The dependency becomes "row `i - 1`, and row `i` to the left" — so rows top to bottom, columns left to right.

### 4.7 Can the state space be optimized?

Row `i` reads only row `i - 1` (same column) and row `i` itself (earlier columns). One array is enough: before processing coin `i`, it still holds row `i - 1`; walking left to right overwrites it with row `i`. That gives O(amount) memory — and it is exactly where the loop-order question appears (Section 8).

## 5. Brute force: recursion

```java
private static int countWays(int[] coins, int index, int remaining) {
    if (remaining == 0) return 1;            // pile complete: one way
    if (index == coins.length) return 0;     // money left, no coin types left

    int skip = countWays(coins, index + 1, remaining);
    int take = 0;
    if (coins[index] <= remaining) {
        take = countWays(coins, index, remaining - coins[index]);   // stay on index
    }
    return skip + take;
}
```

Full file: [solution/BruteForce.java](solution/BruteForce.java).

### Recursion tree for coins = [1, 2, 5], amount = 5

Each node is `(index, remaining)`. Left edge = skip (index + 1), right edge = take (remaining − coin). Leaves show their value. Coin at index 0 is 1, index 1 is 2, index 2 is 5.

```
(0,5)
├─ skip → (1,5)                              coins {2,5} for 5
│        ├─ skip → (2,5)                     coins {5} for 5
│        │        ├─ skip → (3,5) = 0
│        │        └─ take → (2,0) = 1        pile {5}
│        └─ take → (1,3)                     coins {2,5} for 3
│                 ├─ skip → (2,3)
│                 │        ├─ skip → (3,3) = 0
│                 │        └─ take: 5 > 3, not allowed (0)
│                 └─ take → (1,1)
│                          ├─ skip → (2,1) → … = 0
│                          └─ take: 2 > 1, not allowed (0)
└─ take → (0,4)                              one 1 used, 4 left
         ├─ skip → (1,4)                     coins {2,5} for 4
         │        ├─ skip → (2,4) → … = 0
         │        └─ take → (1,2)
         │                 ├─ skip → (2,2) → … = 0
         │                 └─ take → (1,0) = 1          pile {1,2,2}
         └─ take → (0,3)
                  ├─ skip → (1,3)   ← SAME STATE as above (= 0)
                  └─ take → (0,2)
                           ├─ skip → (1,2)   ← SAME STATE as above (= 1)  pile {1,1,1,2}
                           └─ take → (0,1)
                                    ├─ skip → (1,1)   ← SAME STATE (= 0)
                                    └─ take → (0,0) = 1   pile {1,1,1,1,1}
```

Adding up the leaves that returned 1: `{5}`, `{1,2,2}`, `{1,1,1,2}`, `{1,1,1,1,1}` → **4**.

Notice how every leaf corresponds to one pile, and each pile is reached by exactly one path. That is the "never come back to a coin type" rule doing its job.

### Why it is slow

Every call branches into two. With coin 1 and amount 5000, the "take 1" chain alone is 5000 levels deep, and every level also branches into "skip". The number of calls grows exponentially. The tree above already shows repeated states (`(1,3)`, `(1,2)`, `(1,1)`), and for larger inputs the repetition is massive.

## 6. Memoization (top-down)

There are only `n × (amount + 1)` different `(index, remaining)` pairs. We store each answer in `memo[index][remaining]` the first time and reuse it.

```java
private static long countWays(int[] coins, int index, int remaining) {
    if (remaining == 0) return 1;
    if (index == coins.length) return 0;
    if (memo[index][remaining] != -1) return memo[index][remaining];   // cache hit

    long skip = countWays(coins, index + 1, remaining);
    long take = 0;
    if (coins[index] <= remaining) take = countWays(coins, index, remaining - coins[index]);
    memo[index][remaining] = skip + take;
    return memo[index][remaining];
}
```

Full file: [solution/Memoization.java](solution/Memoization.java). We use `long` for the counts; Section 10 explains why that is a sensible habit.

### Dry run: where the cache hits happen

The calls happen in the same order as the tree (skip branch first). The memo fills up like this:

| Step | Call | Result | Note |
|---|---|---|---|
| 1 | `(2,5)` | 1 | computed inside `(1,5)` |
| 2 | `(2,1)`, `(1,1)` | 0, 0 | computed inside `(1,3)` |
| 3 | `(2,3)`, `(1,3)` | 0, 0 | |
| 4 | `(1,5)` | 1 | = `(2,5)` + `(1,3)` = 1 + 0 |
| 5 | `(2,4)`, `(2,2)`, `(1,2)`, `(1,4)` | 0, 0, 1, 1 | computed inside `(0,4)`'s skip branch |
| 6 | `(1,3)` from `(0,3)` | **hit → 0** | no recomputation |
| 7 | `(1,2)` from `(0,2)` | **hit → 1** | |
| 8 | `(1,1)` from `(0,1)` | **hit → 0** | |
| 9 | `(0,1)`, `(0,2)`, `(0,3)`, `(0,4)`, `(0,5)` | 1, 2, 2, 3, 4 | unwinding |

Final memo (row = index, i.e. "coins from here on"; column = remaining; column 0 is the base case):

```
                    remaining:  1  2  3  4  5
index 2  (coins {5})            0  0  0  0  1
index 1  (coins {2,5})          0  1  0  1  1
index 0  (coins {1,2,5})        1  2  2  3  4   ← answer ways(0,5) = 4
```

## 7. Tabulation (bottom-up)

For the table we use the **prefix** form, which reads more naturally left to right:

> `ways[i][a]` = number of combinations that pay exactly `a` using only the first `i` coin types `coins[0..i-1]`.

- `ways[0][0] = 1` (no coin types, pay 0: the empty pile); `ways[0][a] = 0` for `a > 0`.
- `ways[i][a] = ways[i-1][a]` (don't use coin type `i` at all) `+ ways[i][a - coin]` (use it at least once; same row because more copies may follow).

```java
long[][] ways = new long[n + 1][amount + 1];
ways[0][0] = 1;
for (int i = 1; i <= n; i++) {
    int coin = coins[i - 1];
    for (int a = 0; a <= amount; a++) {
        ways[i][a] = ways[i - 1][a];
        if (a >= coin) ways[i][a] += ways[i][a - coin];
    }
}
return ways[n][amount];
```

Full file: [solution/Tabulation.java](solution/Tabulation.java).

### Filling the table step by step (coins = [1, 2, 5], amount = 5)

Start:

```
                 a:  0  1  2  3  4  5
i=0 (no coins)       1  0  0  0  0  0
```

Row 1, coin = 1. Each cell = cell above + cell one step to the left in this row:

```
a=0: 1 (above)            a=3: 0 + ways[1][2]=1 → 1
a=1: 0 + ways[1][0]=1 → 1 a=4: 0 + 1 → 1
a=2: 0 + ways[1][1]=1 → 1 a=5: 0 + 1 → 1

i=1 (+coin 1)        1  1  1  1  1  1      only 1s: exactly one pile per amount
```

Row 2, coin = 2. Cell = above + the cell two steps to the left in this row:

```
a=0: 1          a=1: 1 (2 doesn't fit)
a=2: 1 + ways[2][0]=1 → 2     {1,1}, {2}
a=3: 1 + ways[2][1]=1 → 2     {1,1,1}, {1,2}
a=4: 1 + ways[2][2]=2 → 3     {1,1,1,1}, {1,1,2}, {2,2}
a=5: 1 + ways[2][3]=2 → 3     {1×5}, {1,1,1,2}, {1,2,2}

i=2 (+coin 2)        1  1  2  2  3  3
```

Row 3, coin = 5. Only `a = 5` can use it:

```
a=0..4: copy from above
a=5: 3 + ways[3][0]=1 → 4     the three piles above, plus {5}

i=3 (+coin 5)        1  1  2  2  3  4   ← answer ways[3][5] = 4
```

Complete table:

```
                 a:  0  1  2  3  4  5
i=0 (no coins)       1  0  0  0  0  0
i=1 (+coin 1)        1  1  1  1  1  1
i=2 (+coin 2)        1  1  2  2  3  3
i=3 (+coin 5)        1  1  2  2  3  4
```

Compare with the memo table in Section 6: the memo counts "coins from index on" (suffixes) and the table counts "the first i coins" (prefixes). The intermediate numbers differ, but the final answer is the same, 4. Both are valid; we chose suffixes for recursion because it starts naturally at index 0, and prefixes for loops because they run naturally upward.

## 8. Space optimization — and the loop-order lesson

### 8.1 Collapsing to one array

Row `i` reads `ways[i-1][a]` (directly above) and `ways[i][a - coin]` (to the left, already in the new row). Use one array `ways[a]`:

- before the inner loop for coin `i`, `ways[a]` still holds row `i - 1`;
- walking `a` **left to right**, when we reach `a`, `ways[a - coin]` has already been updated to row `i` — which is exactly what "use this coin again" needs.

```java
long[] ways = new long[amount + 1];
ways[0] = 1;
for (int coin : coins) {                    // OUTER: coin types
    for (int a = coin; a <= amount; a++) {  // INNER: amounts, left to right
        ways[a] += ways[a - coin];
    }
}
return ways[amount];
```

Full file: [solution/Optimized.java](solution/Optimized.java).

(Contrast with [0/1 knapsack](../02-zero-one-knapsack/), where the inner loop runs **right to left** so that each item is used at most once. Here left to right is deliberate: unlimited reuse.)

### 8.2 What if we swap the loops?

It is tempting to write the "obvious" one-dimensional recurrence instead:

> "The number of ways to pay `a` = sum over every coin `c` of the number of ways to pay `a − c`."

```java
for (int a = 1; a <= amount; a++) {     // OUTER: amounts
    for (int coin : coins) {            // INNER: coin types
        if (coin <= a) ways[a] += ways[a - coin];
    }
}
```

The update line is identical. Run both on **coins = [1, 2], amount = 3**, where the correct number of piles is 2 (`{1,1,1}`, `{1,2}`).

**Version A — coins outer, amount inner (combinations):**

```
start              ways = [1, 0, 0, 0]
coin 1:  a=1: ways[1] += ways[0] → 1        {1}
         a=2: ways[2] += ways[1] → 1        {1,1}
         a=3: ways[3] += ways[2] → 1        {1,1,1}
                   ways = [1, 1, 1, 1]
coin 2:  a=2: ways[2] += ways[0] → 2        {1,1}, {2}
         a=3: ways[3] += ways[1] → 2        {1,1,1}, {1}+2 = {1,2}
                   ways = [1, 1, 2, 2]      answer 2 ✔
```

When coin 2 is processed, every pile we extend contains only 1s. So a 2 is always added *after* all the 1s. Every pile is built in the canonical order "1s, then 2s", hence exactly once.

**Version B — amount outer, coins inner (ordered sequences):**

Here `ways[a]` asks "how many sequences sum to `a`?", and each sequence is classified by its **last** coin.

```
start              ways = [1, 0, 0, 0]
a=1: coin 1: ways[1] += ways[0] → 1         "1"
     coin 2: too big
a=2: coin 1: ways[2] += ways[1] → 1         "1,1"            (last coin 1)
     coin 2: ways[2] += ways[0] → 2         "2"              (last coin 2)
a=3: coin 1: ways[3] += ways[2] → 2         "1,1,1", "2,1"   (last coin 1)
     coin 2: ways[3] += ways[1] → 3         "1,2"            (last coin 2)
                   ways = [1, 1, 2, 3]      answer 3 ✘ for LC 518
```

`2,1` and `1,2` were both counted: when we finish amount 3 we try *every* coin as the last one, including a 1 after a 2. Nothing stops us from "coming back" to an earlier coin type. That is the right answer for a different question — **how many ordered sequences** — and the wrong answer for piles.

### 8.3 The rule, in one picture

```
                         coins OUTER, amount INNER        amount OUTER, coins INNER
what one cell means      piles using coin types seen      sequences, classified by
                         so far                           their last coin
can an earlier coin      no — its loop is finished        yes — every coin is tried
follow a later one?                                       at every amount
counts                   COMBINATIONS (multisets)         PERMUTATIONS (ordered)
[1,2,5], amount 5        4                                9
problems                 LC 518, CSES 1636                LC 377, CSES 1635
```

The 9 for `[1,2,5], 5`: the permutation array becomes `[1, 1, 2, 3, 5, 9]`; e.g. `ways[5] = ways[4] + ways[3] + ways[0] = 5 + 3 + 1` (last coin 1, 2 or 5). The 4 piles expand into 9 orderings: `{5}` → 1, `{1,2,2}` → 3, `{1,1,1,2}` → 4, `{1,1,1,1,1}` → 1; total 9 ✔.

Both versions are in [solution/CombinationsVsPermutations.java](solution/CombinationsVsPermutations.java), including the LC 377 example (`nums = [1,2,3]`, target 4 → 7).

**Comparing the twins:**

| | LC 518 Coin Change II | LC 377 Combination Sum IV | CSES 1636 Coin Combinations II | CSES 1635 Coin Combinations I |
|---|---|---|---|---|
| Counts | piles (order ignored) | sequences (order matters) | piles | sequences |
| Loops | coins outer | target outer | coins outer | sum outer |
| Answer size | fits in 32-bit (promised) | fits in 32-bit (promised) | modulo 10⁹+7 | modulo 10⁹+7 |

Do not trust the problem's name: LC 377 is called "Combination Sum IV", but its examples count `(1,1,2)` and `(1,2,1)` separately, so it wants sequences. Always read the examples.

## 9. Dry run on a full example

The 1D (combinations) version on coins = [1, 2, 5], amount = 5. Each line shows the array after one coin's pass; the bold changes come from `ways[a] += ways[a − coin]`.

| After processing | ways[0] | ways[1] | ways[2] | ways[3] | ways[4] | ways[5] |
|---|---|---|---|---|---|---|
| start | 1 | 0 | 0 | 0 | 0 | 0 |
| coin 1 | 1 | **1** | **1** | **1** | **1** | **1** |
| coin 2 | 1 | 1 | **2** (1+1) | **2** (1+1) | **3** (1+2) | **3** (1+2) |
| coin 5 | 1 | 1 | 2 | 2 | 3 | **4** (3+1) |

Each row is exactly the corresponding row of the 2D table in Section 7 — the 1D array is the 2D table, one row at a time.

## 10. Complexity analysis

| Approach | Time | Space | Why |
|---|---|---|---|
| Brute force | exponential | O(amount + n) | Two branches per call; the "take" chain can be `amount / minCoin` deep. Stack depth bounds memory. |
| Memoization | O(n · amount) | O(n · amount) | `n · (amount+1)` states, each solved once with two lookups and an add. Plus the recursion stack. |
| Tabulation | O(n · amount) | O(n · amount) | `(n+1)(amount+1)` cells, O(1) each. |
| 1D optimized | O(n · amount) | O(amount) | Same updates, one array. |

For LeetCode's limits (`amount ≤ 5000`, up to 300 coins) that is at most 1.5 million cheap updates.

**About overflow.** LeetCode promises that the *final* answer fits in a signed 32-bit `int`, but it does **not** promise this for every intermediate cell: `ways[a]` for some `a < amount` can be much larger than `ways[amount]`. In C++ signed overflow is undefined behaviour, which is why you see C++ solutions using `unsigned` or `double`. In Java, `int` and `long` overflow is well defined: it wraps around modulo 2³² (or 2⁶⁴). Because this DP only ever **adds**, the final result is correct modulo 2³²; and since the true answer is smaller than 2³¹, the wrapped result equals the true answer. Our files use `long` anyway — it is the habit that keeps you safe when a problem *does* subtract, compare or multiply intermediate counts. CSES avoids the issue by asking for the answer modulo 10⁹+7; then you take `% MOD` after every addition.

## 11. Why this approach works

Two facts make the count exact:

1. **Every pile is counted.** A pile is described by how many copies of each coin type it uses. The recursion can make exactly those decisions: take coin 0 that many times, skip, take coin 1 that many times, skip, and so on. So some path produces it.
2. **No pile is counted twice.** Two different paths either differ in how many copies of some coin type they took (so they produce different piles) or they are the same path. Because a skipped coin type is never revisited, there is only one path per pile.

The tabulation and the 1D array compute exactly the same numbers in a different order, so they inherit the same guarantee. The permutation loop breaks fact 2 *on purpose*: it classifies by the last coin, and different orders of the same pile are different paths.

## 12. Common mistakes

1. **Base case `ways(·, 0) = 0`.** Then nothing ever contributes and the answer is always 0. Paying zero has exactly one way.
2. **Swapping the loops in the 1D version.** You silently count ordered sequences (9 instead of 4 on the running example).
3. **Running the inner loop right to left.** That is the 0/1-knapsack direction; each coin could then be used at most once. On `[1,2,5], 5` you would get 1 (only `{5}`).
4. **Moving to `index + 1` after "take".** That forbids using a coin twice — again a 0/1 knapsack, not unbounded.
5. **Checking `index == n` before `remaining == 0`.** Then `ways(n, 0)` returns 0 and piles that finish exactly on the last coin are lost.
6. **Assuming 32-bit safety for intermediate cells.** It holds in Java because of wrap-around, but understand *why* before relying on it.

## 13. When to use this technique / how to recognize similar problems

Use this pattern when the problem:

- asks **"how many ways"** to reach a total;
- lets each item be used **any number of times**;
- and says (or its examples show) that **order does not matter** → coins outer.

If the examples count `1+2` and `2+1` separately — "sequences", "steps", "dice rolls in order", "number of ways to climb" — it is the permutation form → target outer. [Climbing Stairs](../../01-one-dimensional-dp/02-climbing-stairs/) is in fact a permutation count with coins `{1, 2}`!

Quick decision test: write out all answers for a tiny case by hand. If your list contains two entries that are rearrangements of each other, you need the target-outer loop.

## 14. Summary and revision notes

- State: `ways(index, remaining)` = piles paying `remaining` with coin types from `index` on.
- Choices: skip the coin type for good, or take one more and **stay**.
- Combine with `+` because the two branches are disjoint.
- Base: `remaining == 0 → 1`, no coin types left → 0.
- 1D: coins outer, amounts inner, **left to right**.
- Swapping the loops counts ordered sequences (LC 377 / CSES 1635).

```
┌──────────────────────────────────────────────────────────────────┐
│ ways[i][a] = ways[i-1][a] + ways[i][a - coin_i]   (a ≥ coin_i)    │
│ ways[0][0] = 1                                                   │
│                                                                  │
│ 1D, combinations:  for coin: for a = coin..amount:               │
│                        ways[a] += ways[a - coin]                 │
│ 1D, permutations:  for a = 1..amount: for coin ≤ a:              │
│                        ways[a] += ways[a - coin]                 │
└──────────────────────────────────────────────────────────────────┘
```

## 15. Exercises

**Exercise 1.** Using the 1D combinations loop, compute the final array for coins = [2, 3], amount = 7. How many piles pay 7? List them.

<details>
<summary>Answer</summary>

Start `[1,0,0,0,0,0,0,0]`. After coin 2: `[1,0,1,0,1,0,1,0]` (only even amounts). After coin 3: `a=3: 0+ways[0]=1`, `a=4: 1+ways[1]=1`, `a=5: 0+ways[2]=1`, `a=6: 1+ways[3]=2`, `a=7: 0+ways[4]=1`. Final `[1,0,1,1,1,1,2,1]`. One pile pays 7: `{2,2,3}`.
</details>

**Exercise 2.** For the same coins [2, 3] and amount 7, how many **ordered sequences** are there? Compute it with the amount-outer loop and list them.

<details>
<summary>Answer</summary>

`ways = [1, 0, 1, 1, 1, 2, 2, 3]` (e.g. `ways[7] = ways[5] + ways[4] = 2 + 1`). The 3 sequences are `2,2,3`, `2,3,2`, `3,2,2` — all orderings of the single pile `{2,2,3}`.
</details>

**Exercise 3.** A friend writes the 1D combinations version but initialises `ways[0] = 0` and adds `ways[coin] += 1` for each coin before the loop "to count the coin itself". Does that work? Explain.

<details>
<summary>Answer</summary>

No. Try coins `[1, 2]`, amount 3 (correct answer 2). Their array starts `[0, 1, 1, 0]` (the pre-seeded `{1}` and `{2}`). Coin-1 pass: `a=2: 1+1=2`, `a=3: 0+2=2`. Coin-2 pass: `a=2: 2+0=2`, `a=3: 2+ways[1]=3`. They get **3**. The pre-seeded pile `{2}` already existed during the coin-1 pass, so it was extended by a 1 ("2 then 1"), and the coin-2 pass also built "1 then 2" — the same pile `{1,2}` twice. Pre-seeding breaks the canonical order. The empty pile `ways[0] = 1` is the clean base case.
</details>

**Exercise 4.** CSES 1636 has up to 100 coins and sums up to 10⁶ and wants the answer modulo 10⁹+7. Which line of the 1D solution changes, and is the time limit a concern?

<details>
<summary>Answer</summary>

Only the update: `ways[a] = (ways[a] + ways[a - coin]) % MOD`. The work is `n · x = 100 · 10⁶ = 10⁸` simple operations, which runs in about a second in Java with a primitive `int`/`long` array — keep the inner loop tight (no boxing, no 2D table).
</details>
