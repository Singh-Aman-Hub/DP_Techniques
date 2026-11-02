# Knuth Optimization — Walkthrough

> Solution files: [`Slow.java`](solution/Slow.java) (O(n³) interval DP) ·
> [`Optimized.java`](solution/Optimized.java) (Knuth, O(n²), cross-checked against the slow DP)

---

## 1. Intuition

You run a sawmill. A log is made of segments with different weights, and you must cut it into single segments. Every time you cut a piece, the saw charges you the **weight of the piece you are cutting**. The order of cuts changes the total bill: cutting the heaviest piece many times is expensive, so you want to "get rid of" weight early.

For each piece of the log, there is a best place for the first cut. Now picture the piece growing by one segment on the right. Where does the best first cut go? Common sense says it moves **right or stays put** — the new weight is on the right, so balancing pulls the cut that way. It never jumps left. Similarly, if the piece loses a segment on the left, the best cut cannot move left either.

That one observation — *best cut points move in one direction* — is Knuth's optimization. Instead of trying every cut position for every piece, we only try positions between two fences that we already know.

---

## 2. Problem statement in our own words

**Knuth Division** ([CSES 2088](https://cses.fi/problemset/task/2088)): you are given an array of n positive numbers. You must split it until every piece has length 1. Splitting a piece into two non-empty contiguous parts costs the **sum** of that piece. Find the minimum total cost.

Equivalent backwards view: start with n single elements and repeatedly **merge two neighbouring pieces**, paying the sum of the merged piece (this is AtCoder [Slimes](https://atcoder.jp/contests/dp/tasks/dp_n)). Each split in one story is a merge in the other, with the same cost.

---

## 3. Examples

**Our running example:** `a = [3, 1, 5, 2, 4]` (n = 5). **Answer: 34.**

One optimal plan:

```
[3 1 5 2 4]              cut after 5   -> cost 3+1+5+2+4 = 15
[3 1 5] [2 4]            cut after 1   -> cost 3+1+5     =  9
[3 1] [5] [2 4]          cut after 3   -> cost 3+1       =  4
[3] [1] [5] [2 4]        cut after 2   -> cost 2+4       =  6
[3] [1] [5] [2] [4]                       total          = 34
```

A worse plan, for comparison: always cut off the first element: 15 + 12 + 11 + 6 = 44.

Other tests used in the code:

| a | answer | why |
|---|---|---|
| `[7]` | 0 | edge case: nothing to cut |
| `[2, 3]` | 5 | one cut, costs the whole sum |
| `[4, 4, 4, 4]` | 32 | cut in halves: 16 + 8 + 8 |
| `[1, 1, 2, 3, 5]` | 25 | computed by the O(n³) DP |

---

## 4. Deriving the solution — the 7 questions

### 4.1 What is changing?

The piece we are working on: a contiguous range `a[i..j]`. Cutting it creates two smaller ranges.

### 4.2 What does the state represent?

> `cost[i][j]` = the minimum total cost to split the piece `a[i..j]` (inclusive) all the way into single elements.

### 4.3 What are the choices?

The position of the **first** cut: after index `k`, for any `k` with `i ≤ k < j`. That produces the left piece `a[i..k]` and the right piece `a[k+1..j]`, which are then solved independently.

### 4.4 What is the recurrence?

```
cost[i][j] = sum(i..j) + min over i ≤ k < j of ( cost[i][k] + cost[k+1][j] )
```

- `sum(i..j)`: the price of the first cut — it is paid no matter where we cut. This is our `C(i, j)`.
- `cost[i][k] + cost[k+1][j]`: finishing both parts optimally (optimal substructure: the parts never interact again).
- `min`: pick the best first cut.

`sum(i..j)` comes from prefix sums in O(1): `prefix[j+1] - prefix[i]`.

### 4.5 What are the base cases?

`cost[i][i] = 0` — a single element needs no cut.

### 4.6 What is the order of computation?

`cost[i][j]` reads only strictly shorter intervals, so fill by **increasing length** `len = 2, 3, …, n`, and inside one length any order of `i`. (Knuth's version additionally reads `opt[i][j-1]` and `opt[i+1][j]`, both of length `len - 1` — already done.)

### 4.7 Can the state space be optimized?

Memory: no — the final interval reads intervals of every length, so all O(n²) cells are needed (plus the O(n²) `opt` table for Knuth). **Time** is what we optimize: from O(n³) to O(n²). Sections 4.8 and 4.9 explain why that is possible.

### 4.8 Prerequisite: the optimal split point `opt(i, j)` and what it looks like

Define `opt(i, j)` = the **smallest** `k` that achieves the minimum in 4.4. (Using the smallest one is a consistent tie rule; the largest would also work, but mixing them would not.)

Here is the full slow DP for our example, with every candidate. Prefix sums: `prefix = [0, 3, 4, 9, 11, 15]`.

| interval | sum | candidates `k: cost[i][k] + cost[k+1][j]` | best | cost | opt |
|---|---|---|---|---|---|
| [0,1] | 4 | 0: 0+0 | 0 | 4 | 0 |
| [1,2] | 6 | 1: 0+0 | 0 | 6 | 1 |
| [2,3] | 7 | 2: 0+0 | 0 | 7 | 2 |
| [3,4] | 6 | 3: 0+0 | 0 | 6 | 3 |
| [0,2] | 9 | 0: 0+6=6, **1: 4+0=4** | 4 | 13 | 1 |
| [1,3] | 8 | 1: 0+7=7, **2: 6+0=6** | 6 | 14 | 2 |
| [2,4] | 11 | **2: 0+6=6**, 3: 7+0=7 | 6 | 17 | 2 |
| [0,3] | 11 | 0: 0+14=14, **1: 4+7=11**, 2: 13+0=13 | 11 | 22 | 1 |
| [1,4] | 12 | 1: 0+17=17, **2: 6+6=12**, 3: 14+0=14 | 12 | 24 | 2 |
| [0,4] | 15 | 0: 0+24=24, 1: 4+17=21, **2: 13+6=19**, 3: 22+0=22 | 19 | **34** | 2 |

Now lay the `opt` values out as a triangle (row i, column j):

```
         j=0  j=1  j=2  j=3  j=4
i=0       0    0    1    1    2
i=1            1    2    2    2
i=2                 2    2    2
i=3                      3    3
i=4                           4
```

Look at it: **along every row, values never decrease left→right; along every column, values never decrease top→bottom.** That is

```
opt(i, j-1)  ≤  opt(i, j)  ≤  opt(i+1, j)
   (left neighbour)          (neighbour below)
```

So once the two neighbours are known, `opt(i, j)` is trapped in the window `[opt(i, j-1), opt(i+1, j)]`. For `[1,4]` the window is `[opt(1,3), opt(2,4)] = [2, 2]`: a single candidate instead of three.

### 4.9 The key property and why it holds

**Intuition first.**

- *Growing on the right* (`[i, j-1] → [i, j]`): the new element only adds weight to the **right** part of every split. Moving the cut left would make the right part even bigger and costlier; there is no new reason to do that. So the best cut stays or moves right: `opt(i, j-1) ≤ opt(i, j)`.
- *Shrinking on the left* (`[i, j] → [i+1, j]`): the left part lost weight, so the cut has no reason to move left: `opt(i, j) ≤ opt(i+1, j)`.

**When is the intuition guaranteed?** Yao (1980) proved: if the weight `C(i, j)` satisfies two conditions, the monotone bounds hold for the DP of 4.4.

1. **Quadrangle inequality (QI).** For `a ≤ b ≤ c ≤ d`:

   ```
   C(a, c) + C(b, d)  ≤  C(a, d) + C(b, c)

   a-----------c                    a------------------d
         b-----------d     ≤               b-----c
   (two overlapping intervals)      (outer + inner interval)
   ```

   In words: *"crossing" intervals cost no more than "nested" ones.* It is the discrete version of convexity.

2. **Monotone on inclusion.** If `[b, c]` lies inside `[a, d]` then `C(b, c) ≤ C(a, d)`. A bigger piece never costs less.

**Check them for our `C(i, j) = sum(i..j)`** with `a = [3, 1, 5, 2, 4]`, using `a=0, b=1, c=2, d=4`:

```
C(0,2) + C(1,4) = 9 + 12 = 21
C(0,4) + C(1,2) = 15 + 6 = 21          21 ≤ 21  ✓ (equality)
```

For sums QI is always an **equality**, because both sides count every element in `[a, d]` once and every element in `[b, c]` twice. Monotonicity holds because all numbers are positive (a bigger interval has a bigger sum). So Knuth applies to Knuth Division — no surprise given the name.

More examples:
- `C = (sum)²` also satisfies both (squaring a sum is convex, numbers non-negative): `9² + 12² = 225 ≤ 15² + 6² = 261` ✓.
- `C = √(sum)` **fails** QI (concave) — exercise 2.
- Matrix Chain cost `p[i]·p[k+1]·p[j+1]` is not even of the form `C(i, j)`: it depends on `k`. Knuth's argument does not apply.

**Optional: why the bound follows (one-line algebra).** Yao showed that QI + monotonicity of `C` make `cost` itself satisfy QI. Our table confirms it, e.g. `cost(0,2) + cost(1,3) = 13 + 14 = 27 ≤ cost(0,3) + cost(1,2) = 22 + 6 = 28`. Given that, let `k' = opt(i, j-1)` and take any `k < k'`. Write `f_j(k) = cost[i][k] + cost[k+1][j]`. QI on `cost` with `k+1 ≤ k'+1 ≤ j-1 ≤ j` gives

```
cost[k+1][j-1] + cost[k'+1][j]  ≤  cost[k+1][j] + cost[k'+1][j-1]
```

Rearranging: `f_j(k') - f_j(k) ≤ f_{j-1}(k') - f_{j-1}(k) < 0` (the last `< 0` because `k'` is the *smallest* best split for `[i, j-1]`, so it beats every smaller `k` strictly). So for `[i, j]` every `k < k'` is strictly worse than `k'`: `opt(i, j) ≥ k'`. The upper bound is symmetric.

**Why O(n²): the telescoping sum.** For a fixed length `L`, the intervals are `[i, i+L-1]` for `i = 0 … n-L`. The work for interval `i` is its window size:

```
work(i) = opt(i+1, j) - opt(i, j-1) + 1        with j = i+L-1
```

Add these for consecutive `i`. The upper end of interval `i`, `opt(i+1, i+L-1)`, is exactly the lower end of interval `i+1`, `opt(i+1, (i+1)+L-2)`. They cancel:

```
  [opt(1, L-1) - opt(0, L-2)]
+ [opt(2, L)   - opt(1, L-1)]
+ [opt(3, L+1) - opt(2, L)  ]
+ ...
= opt(n-L+1, n-1) - opt(0, L-2)  +  (number of intervals)
≤        n        +        n        =  O(n)
```

So each length costs O(n), and there are n lengths: **O(n²) total**.

Check with our example, length 4: windows `[1,2]` and `[2,2]` → sizes 2 + 1 = 3. Telescoped: `opt(2,4) - opt(0,2) + 2 = 2 - 1 + 2 = 3` ✓.

---

## 5. Brute force: recursion

```java
static long solve(int i, int j) {
    if (i == j) return 0;                              // single element
    long best = Long.MAX_VALUE;
    for (int k = i; k < j; k++)                        // try every first cut
        best = Math.min(best, solve(i, k) + solve(k + 1, j));
    return best + sum(i, j);
}
```

Recursion tree for `[0,4]` (first two levels only):

```
                                  [0,4]
        ┌──────────────┬────────────┴───────────┬──────────────┐
     k=0│           k=1│                     k=2│           k=3│
   [0,0] [1,4]    [0,1] [2,4]           [0,2] [3,4]    [0,3] [4,4]
          │              │               │                │
     [1,1][2,4] ...  [2,2][3,4] ...   [0,0][1,2] ...  [0,0][1,3] ...
```

`[2,4]` already appears twice in the first two levels, `[1,2]`, `[2,3]`, … many more times deeper. The number of calls grows exponentially (it is related to the Catalan numbers), so plain recursion is only usable for tiny n.

---

## 6. Memoization (top-down)

Skipped as a separate file: caching `solve(i, j)` gives exactly the O(n³) interval DP from [module 04](../../04-advanced-dp-patterns/03-interval-dp/). Knuth's optimization needs `opt` of the two shorter neighbouring intervals **before** the current one, which a length-by-length bottom-up loop guarantees naturally, so we go straight to tabulation.

---

## 7. Tabulation (bottom-up): the O(n³) DP

From [`Slow.java`](solution/Slow.java):

```java
long[][] cost = new long[n][n];                  // cost[i][i] = 0
for (int len = 2; len <= n; len++) {
    for (int i = 0; i + len - 1 < n; i++) {
        int j = i + len - 1;
        long best = Long.MAX_VALUE;
        for (int k = i; k < j; k++)              // EVERY split point
            best = Math.min(best, cost[i][k] + cost[k + 1][j]);
        cost[i][j] = best + (prefix[j + 1] - prefix[i]);
    }
}
```

Filling order — diagonals of the upper triangle, shortest first:

```
            j=0  j=1  j=2  j=3  j=4
len=1 →  i=0  0
len=2 →  i=0  0    4                      diagonal len=2:  4  6  7  6
len=3 →  i=0  0    4   13                 diagonal len=3: 13 14 17
len=4 →  i=0  0    4   13   22            diagonal len=4: 22 24
len=5 →  i=0  0    4   13   22   34       diagonal len=5: 34

final table:
         j=0  j=1  j=2  j=3  j=4
  i=0     0    4   13   22   34
  i=1          0    6   14   24
  i=2               0    7   17
  i=3                    0    6
  i=4                         0
```

Split points examined: length 2: 4×1, length 3: 3×2, length 4: 2×3, length 5: 1×4 → **20** (the test in `Slow.java` checks this count). In general about n³/6.

---

## 8. Optimization: Knuth's optimization

From [`Optimized.java`](solution/Optimized.java):

```java
int[][] opt = new int[n][n];                     // opt[i][j] = smallest best split of a[i..j]
for (int i = 0; i < n; i++) opt[i][i] = i;       // base: lets the window formula work for len = 2

for (int len = 2; len <= n; len++) {
    for (int i = 0; i + len - 1 < n; i++) {
        int j = i + len - 1;
        int from = opt[i][j - 1];                  // fence 1: interval without a[j]
        int to = Math.min(opt[i + 1][j], j - 1);   // fence 2: interval without a[i]; k must be < j
        long best = Long.MAX_VALUE;
        int bestK = from;
        for (int k = from; k <= to; k++) {
            long candidate = cost[i][k] + cost[k + 1][j];
            if (candidate < best) { best = candidate; bestK = k; }   // strict: keep smallest k
        }
        cost[i][j] = best + (prefix[j + 1] - prefix[i]);
        opt[i][j] = bestK;
    }
}
```

Three details worth understanding:

1. **Base `opt[i][i] = i`.** For length 2 the window is `[opt(i,i), opt(i+1,i+1)] = [i, i+1]`, but `k` must be `< j = i+1`, so we clamp the top to `j - 1` and get the single correct candidate `k = i`.
2. **The clamp only matters for length 2.** For longer intervals, `opt[i+1][j]` is already a valid split of `[i+1, j]`, so it is at most `j - 1`.
3. **Strict `<`.** Keeps the smallest optimal `k`, matching the tie rule used in the proof. Using `<=` (largest optimal k) also works, as long as you use it everywhere.

The `main` method of `Optimized.java` runs 500 random arrays (seed 2088, half with tiny values to create many ties) against a private copy of the O(n³) DP, and shows that for n = 2000 only about 4·10^6 split points are examined, versus ~1.33·10^9 for the cubic DP.

---

## 9. Dry run on a full example

`a = [3, 1, 5, 2, 4]`. Same table as 4.8, but now each interval only tries its window `[opt(i,j-1), min(opt(i+1,j), j-1)]`.

| interval | window | tried | values | cost | opt | skipped vs slow |
|---|---|---|---|---|---|---|
| [0,1] | [0, min(1,0)] = [0,0] | 0 | 0 | 4 | 0 | 0 |
| [1,2] | [1,1] | 1 | 0 | 6 | 1 | 0 |
| [2,3] | [2,2] | 2 | 0 | 7 | 2 | 0 |
| [3,4] | [3,3] | 3 | 0 | 6 | 3 | 0 |
| [0,2] | [opt(0,1), opt(1,2)] = [0,1] | 0, 1 | 6, **4** | 13 | 1 | 0 |
| [1,3] | [opt(1,2), opt(2,3)] = [1,2] | 1, 2 | 7, **6** | 14 | 2 | 0 |
| [2,4] | [opt(2,3), opt(3,4)] = [2,3] | 2, 3 | **6**, 7 | 17 | 2 | 0 |
| [0,3] | [opt(0,2), opt(1,3)] = [1,2] | 1, 2 | **11**, 13 | 22 | 1 | k=0 |
| [1,4] | [opt(1,3), opt(2,4)] = [2,2] | 2 | **12** | 24 | 2 | k=1, k=3 |
| [0,4] | [opt(0,3), opt(1,4)] = [1,2] | 1, 2 | 21, **19** | **34** | 2 | k=0, k=3 |

Split points examined: 4 + 6 + 3 + 2 = **15** instead of 20 (checked in `Optimized.java`). The saving looks small at n = 5 but grows with n: the slow DP does ~n³/6 work, Knuth ~n² — at n = 2000 that is 4·10^6 vs 1.33·10^9.

Reconstructing the plan from `opt`: `opt(0,4) = 2` → first cut after index 2: `[3 1 5 | 2 4]`. Then `opt(0,2) = 1` → `[3 1 | 5]`, `opt(0,1) = 0` → `[3 | 1]`, `opt(3,4) = 3` → `[2 | 4]`. This is exactly the plan in section 3.

---

## 10. Complexity analysis

| Approach | Time | Why | Space |
|---|---|---|---|
| Plain recursion | exponential | the same intervals are recomputed along many paths | O(n) stack |
| Interval DP (`Slow.java`) | O(n³) | ~n²/2 intervals × up to n split points each (≈ n³/6) | O(n²) table |
| Knuth (`Optimized.java`) | O(n²) | per length, windows telescope to ≤ 2n candidates; n lengths | O(n²) for `cost` + `opt` |

For CSES 2088 (n ≤ 5000) the cubic DP needs ~2·10^10 steps — too slow — while Knuth needs ~5·10^7. Memory: two 5000 × 5000 tables (`long` + `int`) ≈ 300 MB in Java, which is heavy; a common trick is to index by `(len, i)` or to store `opt` in a `short`/`int` and `cost` in a single flattened `long[]`. The algorithm is the same.

---

## 11. Why this approach works

1. The interval DP is correct by optimal substructure: after the first cut the two parts never interact.
2. Because `C = sum` satisfies the quadrangle inequality and is monotone on inclusion, `cost` also satisfies the quadrangle inequality (Yao's theorem), which forces `opt(i, j-1) ≤ opt(i, j) ≤ opt(i+1, j)` (section 4.9).
3. So the true optimal `k` always lies inside the window we search: we never skip the answer, we only skip candidates that cannot win.
4. Both fences come from intervals of length `len - 1`, which the length-by-length order computes first.
5. The windows telescope, giving O(n²) total work.

---

## 12. Common mistakes

1. **Applying Knuth when the cost depends on k.** Matrix Chain Multiplication, Burst Balloons and Polygon Triangulation add a term with `k` in it. The monotone-opt property is not guaranteed; the code may silently return wrong answers.
2. **Forgetting `opt[i][i] = i`.** Then length-2 windows read garbage (default 0), and the windows for longer intervals become too wide or empty.
3. **No clamp for length 2.** Without `min(..., j - 1)`, `k = j` is tried and `cost[j+1][j]` is read — out of the valid triangle (or out of bounds).
4. **Inconsistent tie-breaking.** Store the smallest (or the largest) optimal `k` everywhere; do not mix `<` and `<=` between code paths.
5. **Filling in the wrong order.** Iterating `i` from 0 upward and `j` inside (row by row) reads `opt[i+1][j]` before it exists. Fill by length, or by `i` descending with `j` ascending.
6. **Forgetting `long`.** One interval sum can reach 5000 · 10^9 = 5·10^12, and the total adds many such sums: far beyond `int`.
7. **Trusting the property without a check.** When unsure, cross-check against the O(n³) DP on random small inputs, exactly as `Optimized.java` does.

---

## 13. When to use this technique / how to recognize similar problems

Ask these questions about an interval DP:

```
Is it  dp[i][j] = min_k ( dp[i][k] + dp[k+1][j] ) + C(i, j)  ?
  │   (or the boundary form  dp[i][j] = min_{i<k<j} dp[i][k] + dp[k][j] + C(i, j))
  ├─ no: the extra term uses k  ──► Knuth does not apply (MCM, Burst Balloons, LC 1039)
  └─ yes ─► does C satisfy QI and monotonicity?
              ├─ C = sum of positive values, length, (sum)², convex function of a sum ... ─► yes
              └─ not sure ─► cross-check opt monotonicity on random tests, or keep O(n³)
         ─► n up to a few thousand?  ──► use Knuth for O(n²)
```

Relation to other optimizations in this module:
- [Divide and conquer optimization](../05-divide-and-conquer-optimization/) handles **layered** partition DPs `dp[k][i] = min_j dp[k-1][j] + cost(j, i)` with monotone opt; Knuth handles **interval** DPs.
- For the layered partition DP, Knuth-style windows `opt[k-1][i] ≤ opt[k][i] ≤ opt[k][i+1]` also work and give O(n²) total; that is another way to solve CSES [Subarray Squares](https://cses.fi/problemset/task/2086).

---

## 14. Summary and revision notes

- Form: `dp[i][j] = min_{i ≤ k < j} dp[i][k] + dp[k+1][j] + C(i, j)`, with `C` independent of `k`.
- Conditions on C: quadrangle inequality `C(a,c) + C(b,d) ≤ C(a,d) + C(b,c)` and monotone on inclusion.
- Consequence: `opt(i, j-1) ≤ opt(i, j) ≤ opt(i+1, j)`.
- Search only that window; base `opt[i][i] = i`; clamp the top to `j - 1`.
- Windows telescope along each diagonal: O(n) per length, **O(n²)** total.
- Always cross-check against the O(n³) DP.

```
┌─────────────────────────────────────────────────────────────────────┐
│  cost[i][i] = 0,  opt[i][i] = i                                       │
│  for len = 2..n, for i:  j = i+len-1                                  │
│     for k in [ opt[i][j-1] , min(opt[i+1][j], j-1) ]:                 │
│         cost[i][j] = min(cost[i][k] + cost[k+1][j]) + sum(i..j)       │
│     opt[i][j] = the (smallest) k achieving it                         │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 15. Exercises

**Exercise 1.** Run Knuth's optimization by hand on `a = [4, 4, 4, 4]`. Give the final answer, the `opt` table, and how many split points were examined (the slow DP examines 3 + 4 + 3 = 10).

<details><summary>Answer</summary>

- Length 2: `cost = 8` for [0,1], [1,2], [2,3]; `opt = 0, 1, 2`. (3 candidates)
- [0,2]: window [opt(0,1), opt(1,2)] = [0,1]: k=0 → 0+8 = 8, k=1 → 8+0 = 8. Tie → smallest, opt = 0, cost = 8 + 12 = 20.
- [1,3]: window [1,2]: tie again, opt = 1, cost = 20. (4 candidates for length 3)
- [0,3]: window [opt(0,2), opt(1,3)] = [0,1]: k=0 → 0 + 20 = 20, k=1 → 8 + 8 = 16. opt = 1, cost = 16 + 16 = **32**. (2 candidates)

Total examined: 3 + 4 + 2 = 9. Plan: cut in the middle (16), then each half (8 + 8).
</details>

**Exercise 2.** Does `C(i, j) = √(sum(i..j))` satisfy the quadrangle inequality? Test it with all values equal to 1 and `a = 0, b = 1, c = 1, d = 2`.

<details><summary>Answer</summary>

With all ones, `C(i, j) = √(j - i + 1)`.
Left side: `C(0,1) + C(1,2) = √2 + √2 ≈ 2.828`.
Right side: `C(0,2) + C(1,1) = √3 + √1 ≈ 2.732`.
`2.828 ≤ 2.732` is **false**, so QI fails. A concave function of the sum rewards nesting less than crossing, the opposite of what Knuth needs. Do not use the optimization with this cost without further proof.
</details>

**Exercise 3.** LeetCode 1547 ([Minimum Cost to Cut a Stick](https://leetcode.com/problems/minimum-cost-to-cut-a-stick/)) uses boundary notation: sort the cut positions and add 0 and the stick length, giving points `p[0] < p[1] < … < p[m]`. Write the recurrence and the Knuth window. Then solve a stick of length 6 with cuts at 1 and 3.

<details><summary>Answer</summary>

`dp[i][j]` = min cost to make every cut strictly between points `p[i]` and `p[j]`.

```
dp[i][i+1] = 0
dp[i][j]   = (p[j] - p[i]) + min over i < k < j of ( dp[i][k] + dp[k][j] )
```

`C(i, j) = p[j] - p[i]` is a length: QI holds with equality, and it is monotone. Window: `opt(i, j-1) ≤ opt(i, j) ≤ opt(i+1, j)`; with base `opt[i][i+1] = i+1` and the top clamped to `j - 1`, length 2 (`j = i + 2`) gets the single candidate `k = i + 1`.

Example: `p = [0, 1, 3, 6]`.
- `dp[0][2] = 3 + (dp[0][1] + dp[1][2]) = 3`, `dp[1][3] = 5 + 0 = 5`.
- `dp[0][3] = 6 + min(k=1: 0 + 5, k=2: 3 + 0) = 6 + 3 = 9`. Cut at 3 first (cost 6), then at 1 (cost 3).
</details>

**Exercise 4.** Matrix Chain Multiplication has `dp[i][j] = min_k dp[i][k] + dp[k+1][j] + p[i]·p[k+1]·p[j+1]`. Why can't you simply plug in Knuth's windows?

<details><summary>Answer</summary>

The extra term depends on `k`, so it is not a weight `C(i, j)` of the interval alone, and Yao's theorem says nothing about it. The optimal split of MCM is not guaranteed to be monotone, so restricting `k` to a window can skip the true optimum. (MCM does have a special O(n log n) algorithm by Hu and Shing, but it is a completely different method.) Keep the O(n³) DP for MCM-style problems unless you can prove monotonicity.
</details>

---

⬅ Back to [topic README](README.md)
