# Divide and Conquer Optimization — Walkthrough

Running example for the whole lesson (it is the official sample of CSES 2086):

```
a = [2, 3, 1, 2, 2, 3, 4, 1]      n = 8,  k = 3 groups      answer = 110
```

## 1. Intuition

Imagine you must drive a long road with 8 villages and stop exactly 3 times to refuel, and each leg of the trip costs more than proportionally to its length (long legs are painful). You plan the trip village by village. Suppose that to reach village 5 the best plan has its last stop at village 2. Now you extend the trip to village 6. Would the best last stop suddenly jump *backwards*, to village 1? Intuitively no: the trip just got longer at the end, so the last leg wants to get *shorter* again, which means the last stop wants to move *forward*, not backward.

That single observation — **"the best last cut never moves left when the prefix gets longer"** — is the whole optimization. If we know the best cut for the middle village, then every village to its left can only use cuts at or before it, and every village to its right can only use cuts at or after it. Solve the middle, split the search space in two, recurse. That is divide and conquer applied to the *search for the best cut*, not to the array itself.

## 2. Problem statement in our own words

[Subarray Squares (CSES 2086)](https://cses.fi/problemset/task/2086): you get an array of `n` positive integers and a number `k`. Cut the array into exactly `k` non-empty contiguous pieces. The cost of a piece is the square of its sum, and the cost of a cutting is the total over all pieces. Output the minimum possible cost. Limits: `1 ≤ k ≤ n ≤ 3000`, values up to 10⁵.

## 3. Examples

| Input | k | Best cutting | Cost |
|-------|---|--------------|------|
| `[2,3,1,2,2,3,4,1]` | 3 | `[2,3,1] [2,2,3] [4,1]` → sums 6, 7, 5 | 36 + 49 + 25 = **110** |
| `[2,3,1,2,2,3,4,1]` | 2 | `[2,3,1,2] [2,3,4,1]` → sums 8, 10 | 64 + 100 = **164** |
| `[1,2,3]` | 1 | the whole array, sum 6 | **36** |
| `[1,2,3]` | 3 | every element alone | 1 + 4 + 9 = **14** |
| `[5]` | 1 | edge case: one element | **25** |

Why squares make this interesting: the total of all sums is fixed (18 in the running example), and squaring punishes big pieces, so the best cutting tries to make the pieces' sums **as equal as possible** (18 / 3 = 6 each; 6, 7, 5 is as close as contiguous pieces allow).

## 4. Deriving the solution — the 7 questions

### 4.1 What is changing?

Two things shrink as we make decisions from the right end: how many elements are still uncut (`i`) and how many groups we still have to form (`k`).

### 4.2 What does the state represent?

`dp[g][i]` = **the minimum cost to cut the first `i` elements `a[0..i-1]` into exactly `g` non-empty groups.**

We use prefix sums so that a group's sum is O(1):

```
prefix[i] = a[0] + ... + a[i-1]
prefix    = [0, 2, 5, 6, 8, 10, 13, 17, 18]
sum of a[j..i-1] = prefix[i] - prefix[j]
cost(j, i) = (prefix[i] - prefix[j])²
```

### 4.3 What are the choices?

For state `(g, i)`, choose where the **last group** starts: index `j`, so the last group is `a[j..i-1]`. The first `g-1` groups then cover `a[0..j-1]`, which needs `j ≥ g-1`; the last group is non-empty, which needs `j ≤ i-1`.

### 4.4 What is the recurrence?

```
dp[g][i] = min over j in [g-1, i-1] of ( dp[g-1][j] + cost(j, i) )
             ^ best way to cut the        ^ price of the
               first j elements into        last group
               g-1 groups
```

### 4.5 What are the base cases?

- `dp[0][0] = 0`: zero elements in zero groups costs nothing.
- `dp[0][i] = ∞` for `i > 0`: you cannot cover elements with zero groups.
- `dp[g][i] = ∞` for `i < g`: not enough elements for `g` non-empty groups.

### 4.6 What is the order of computation?

Layer `g` reads only layer `g-1`. So the outer loop is over `g = 1..k`; inside a layer, states do not depend on each other at all. This independence is what lets us fill a layer in any order we like — including "middle first, then recurse", which is what D&C does.

### 4.7 Can the state space be optimized?

- **Space:** only layers `g-1` and `g` are alive at once, so two arrays of size `n+1` are enough ([rolling arrays](../02-rolling-arrays/)).
- **Time:** this is the real topic. The direct DP does O(n) work per state. The rest of section 4 shows why the best `j` is monotone in `i`, which lets us skip most of those `j`.

### 4.8 Prerequisite: what is `opt`, and what does "monotone" look like?

Define `opt(g, i)` = the **smallest** `j` that achieves the minimum in the recurrence. Let us fill layer 1 and layer 2 of the running example and look at every candidate.

Layer 1 is forced: one group means the whole prefix, `dp[1][i] = prefix[i]²`.

```
i          : 1   2   3   4    5    6    7    8
dp[1][i]   : 4  25  36  64  100  169  289  324
```

Layer 2: each row lists `dp[1][j] + cost(j, i)` for every allowed `j`. The minimum is marked with `*`.

```
i=2: j=1: 4+9=13*
i=3: j=1: 4+16=20*   j=2: 25+1=26
i=4: j=1: 4+36=40    j=2: 25+9=34*    j=3: 36+4=40
i=5: j=1: 4+64=68    j=2: 25+25=50*   j=3: 36+16=52    j=4: 64+4=68
i=6: j=1: 125        j=2: 89          j=3: 85*         j=4: 89        j=5: 109
i=7: j=1: 229        j=2: 169         j=3: 157         j=4: 145*      j=5: 149   j=6: 185
i=8: j=1: 260        j=2: 194         j=3: 180         j=4: 164*      j=5: 164   j=6: 194  j=7: 290
```

Read off `opt(2, i)`:

```
i         : 2  3  4  5  6  7  8
opt(2, i) : 1  1  2  2  3  4  4      <- never decreases
```

Layer 3, same procedure (candidates `dp[2][j] + cost(j, i)`):

```
i=3: j=2: 14*
i=4: j=2: 22*   j=3: 24
i=5: j=2: 38    j=3: 36*   j=4: 38
i=6: j=2: 77    j=3: 69    j=4: 59*   j=5: 59
i=7: j=2: 157   j=3: 141   j=4: 115   j=5: 99*   j=6: 101
i=8: j=2: 182   j=3: 164   j=4: 134   j=5: 114   j=6: 110*  j=7: 146

i         : 3  4  5  6  7  8
opt(3, i) : 2  2  3  4  5  6          <- never decreases
```

Picture of layer 2: each row is a state `i`, each column a cut `j`, `*` the best cut. The stars walk down and to the right like a staircase:

```
        j=1 j=2 j=3 j=4 j=5 j=6 j=7
i=2      *
i=3      *   .
i=4      .   *   .
i=5      .   *   .   .
i=6      .   .   *   .   .
i=7      .   .   .   *   .   .
i=8      .   .   .   *   .   .   .
```

**Monotone opt:** `opt(g, i) ≤ opt(g, i+1)` for every `i`. If this is true, then once we know `opt(g, 5) = 2`, states `i < 5` only need to try `j ≤ 2` and states `i > 5` only need `j ≥ 2`.

### 4.9 The key property and why it holds

#### Step 1: the gap between two cuts only grows

Pick two cut points `j1 < j2` and watch how much better `j2` is than `j1` as `i` grows. In layer 2 with `j1 = 1`, `j2 = 2`:

```
i                        : 3    4    5    6     7     8
value with j=1           : 20   40   68   125   229   260
value with j=2           : 26   34   50   89    169   194
(j=2) - (j=1)            : +6   -6   -18  -36   -60   -66
```

The difference keeps **decreasing**: once the later cut `j2` is better, it stays better forever. Why? The `dp[1][j]` parts are fixed (they do not depend on `i`), so only the cost difference matters:

```
cost(j2, i) - cost(j1, i)
```

Let `S = prefix[j2] - prefix[j1]` (the elements between the two cuts, here `a[1] = 3`) and `T = prefix[i] - prefix[j2]` (the last group when cutting at `j2`). Then

```
cost(j1, i) = (S + T)² = S² + 2ST + T²
cost(j2, i) = T²
difference  = cost(j2, i) - cost(j1, i) = -S² - 2ST
```

As `i` grows, `T` grows (all values are positive), so `-S² - 2ST` gets more negative. Plug in `S = 3`: at `i = 4`, `T = 3`, so the cost difference is `-9 - 18 = -27`; at `i = 5`, `T = 5`, difference `-9 - 30 = -39`. Adding the fixed `dp` difference `25 - 4 = 21` gives exactly the `-6` and `-18` in the table.

#### Step 2: the quadrangle inequality (QI), stated simply

The general version of Step 1 is a property of the cost function alone. For any four positions `a ≤ b ≤ c ≤ d`:

```
cost(a, c) + cost(b, d)  ≤  cost(a, d) + cost(b, c)

   a-------c                 a-----------d
       b-------d                 b---c
   "two overlapping           "one outer + one inner
    medium groups"              group"
```

In words: **two overlapping medium groups cost no more than one big group plus one small group.** For a cost that grows faster than linearly (like a square), spreading the elements evenly is cheaper, so this is natural.

Check it for `cost = (sum)²`. Let `A = sum[a, b)`, `B = sum[b, c)`, `C = sum[c, d)`:

```
left  = (A+B)² + (B+C)²   = A² + 2AB + 2B² + 2BC + C²
right = (A+B+C)² + B²     = A² + 2AB + 2B² + 2BC + C² + 2AC
right - left = 2AC ≥ 0          (all values positive)  ✔
```

Tiny numeric check with `a=[1,2,3]` positions `a=0, b=1, c=2, d=3`: `cost(0,2) + cost(1,3) = 3² + 5² = 34` and `cost(0,3) + cost(1,2) = 6² + 2² = 40`. 34 ≤ 40 ✔.

#### Step 3: QI ⇒ monotone opt

Let `f(j, i) = dp[g-1][j] + cost(j, i)` (the value of cutting at `j`). Take `j1 < j2 = opt(g, i)`. Because `j2` is the *leftmost* optimum, `j1` is strictly worse at `i`:

```
f(j2, i) - f(j1, i) < 0
```

Apply QI with `a = j1, b = j2, c = i, d = i+1`:

```
cost(j1, i) + cost(j2, i+1) ≤ cost(j1, i+1) + cost(j2, i)
⇒ cost(j2, i+1) - cost(j1, i+1) ≤ cost(j2, i) - cost(j1, i)
```

Add `dp[g-1][j2] - dp[g-1][j1]` to both sides:

```
f(j2, i+1) - f(j1, i+1) ≤ f(j2, i) - f(j1, i) < 0
```

So at `i+1` the cut `j1` is still strictly worse than `j2`. Every cut left of `opt(g, i)` loses at `i+1`, hence `opt(g, i+1) ≥ opt(g, i)`. ∎

> The QI is a *sufficient* condition. In contests you often just trust intuition ("the cost is convex in group size"), or print `opt` from the slow DP on random inputs and check that it never decreases — exactly what our randomized test does indirectly.

#### Step 4: how monotonicity saves work

Fill one layer like this:

1. Take the middle state `mid` of the range `[lo, hi]`. Scan **all** its allowed cuts `[optLo, optHi]`, find `best = opt(mid)`.
2. States left of `mid` have `opt ≤ best`: recurse on `[lo, mid-1]` with cuts `[optLo, best]`.
3. States right of `mid` have `opt ≥ best`: recurse on `[mid+1, hi]` with cuts `[best, optHi]`.

```
compute(lo, hi, optLo, optHi):
    if lo > hi: return
    mid = (lo + hi) / 2
    scan j in [optLo, min(mid-1, optHi)] -> best value, best j
    dp[g][mid] = best value
    compute(lo, mid-1, optLo, bestJ)
    compute(mid+1, hi, bestJ, optHi)
```

The cut ranges of neighbouring calls share only one endpoint, so at each recursion depth the total number of scanned cuts is about `n` (plus the number of calls). There are `log₂ n` depths. That gives O(n log n) per layer.

## 5. Brute force: recursion

Directly encode the recurrence, with no caching:

```java
static long solve(int groups, int i) {          // first i elements into `groups` groups
    if (groups == 0) return (i == 0) ? 0 : INF;
    if (i < groups) return INF;
    long best = INF;
    for (int j = groups - 1; j < i; j++)
        best = Math.min(best, solve(groups - 1, j) + cost(j, i));
    return best;
}
```

Recursion tree for `solve(3, 8)` (first two levels only):

```
                               solve(3,8)
        /           /           |            \            \           \
  solve(2,2)  solve(2,3)  solve(2,4)  solve(2,5)  solve(2,6)  solve(2,7)
                 |            / \        / | \       ...
            solve(1,1..2)  solve(1,1..3) solve(1,1..4)
```

`solve(1, 3)` alone is called from `solve(2,4)`, `solve(2,5)`, `solve(2,6)`, `solve(2,7)`: the same subproblems are recomputed again and again. The number of calls equals the number of ways to choose cut positions, which is `C(n-1, k-1)` leaves — exponential when `k ≈ n/2`.

## 6. Memoization (top-down)

There are only `(k+1)·(n+1)` distinct states, so caching `solve(groups, i)` in a `memo[groups][i]` table brings the cost down to O(k·n²) (each state loops over O(n) cuts once). It does exactly the same work as tabulation, so we skip a separate file and go straight to the bottom-up version, which is easier to optimize layer by layer. In the tree above, the second call to `solve(1, 3)` would now return instantly from `memo[1][3] = 36`.

## 7. Tabulation (bottom-up) — the slow O(k·n²) DP

From [`solution/Slow.java`](solution/Slow.java):

```java
long[][] dp = new long[groups + 1][n + 1];
for (long[] row : dp) Arrays.fill(row, INF);
dp[0][0] = 0;
for (int g = 1; g <= groups; g++)
    for (int i = g; i <= n; i++)
        for (int j = g - 1; j < i; j++) {
            long groupSum = prefix[i] - prefix[j];
            long candidate = dp[g - 1][j] + groupSum * groupSum;
            if (candidate < dp[g][i]) dp[g][i] = candidate;
        }
return dp[groups][n];
```

Filled table for the running example (`∞` = impossible):

```
            i:  0    1    2    3    4    5    6    7    8
g = 0        :  0    ∞    ∞    ∞    ∞    ∞    ∞    ∞    ∞
g = 1        :  ∞    4   25   36   64  100  169  289  324
g = 2        :  ∞    ∞   13   20   34   50   85  145  164
g = 3        :  ∞    ∞    ∞   14   22   36   59   99  110   <- answer
```

How one cell is produced, e.g. `dp[3][8]`: the candidates (section 4.8) are 182, 164, 134, 114, **110**, 146, so `dp[3][8] = 110` with `j = 6`, i.e. the last group is `a[6..7] = [4, 1]` with sum 5. Following the `opt` values back: `opt(3,8)=6`, `opt(2,6)=3`, so the groups are `a[0..2]`, `a[3..5]`, `a[6..7]` → sums 6, 7, 5. ✔

The slow version scans `1 + 2 + … + 7 = 28` candidates in layer 2 alone; in general about `n²/2` per layer.

## 8. Optimization: divide and conquer over the split point

From [`solution/Optimized.java`](solution/Optimized.java). Only two layers are stored (`previousLayer` = layer `g-1`, `currentLayer` = layer `g`):

```java
for (int g = 1; g <= groups; g++) {
    currentLayer = new long[n + 1];
    Arrays.fill(currentLayer, INF);
    compute(g, n, g - 1, n - 1);     // states i in [g, n], cuts j in [g-1, n-1]
    previousLayer = currentLayer;
}

private static void compute(int lo, int hi, int optLo, int optHi) {
    if (lo > hi) return;
    int mid = (lo + hi) / 2;
    long best = INF;
    int bestJ = optLo;
    int lastJ = Math.min(mid - 1, optHi);      // the last group must be non-empty
    for (int j = optLo; j <= lastJ; j++) {
        long candidate = previousLayer[j] + cost(j, mid);
        if (candidate < best) {                  // strict '<' keeps the LEFTMOST optimum
            best = candidate;
            bestJ = j;
        }
    }
    currentLayer[mid] = best;
    compute(lo, mid - 1, optLo, bestJ);
    compute(mid + 1, hi, bestJ, optHi);
}
```

Details that matter:

- **Leftmost optimum.** At `i = 8` in layer 2, cuts `j = 4` and `j = 5` tie at 164. Our proof was about the *leftmost* optimum, so use strict `<` and keep the first best. (Using `≤` consistently, i.e. the rightmost optimum, also works; mixing does not.)
- **`lastJ = min(mid - 1, optHi)`.** The inherited range can extend past `mid - 1`; clipping keeps the last group non-empty.
- **Both children include `bestJ`.** The ranges `[optLo, bestJ]` and `[bestJ, optHi]` share one endpoint; that is fine and is part of the complexity count.
- `main` cross-checks this against a private copy of the slow DP on 500 random arrays with `new Random(2086)`.

## 9. Dry run on a full example

Layer 2 of the running example. States `i ∈ [2, 8]`, cuts `j ∈ [1, 7]`. Each line is one call to `compute`; "scan" lists the cuts tried and their values.

```
call                    mid  scan (j: value)                  best   opt
compute(2,8, 1,7)        5   1:68  2:50  3:52  4:68            50     2
├─ compute(2,4, 1,2)     3   1:20  2:26                        20     1
│  ├─ compute(2,2, 1,1)  2   1:13                              13     1
│  └─ compute(4,4, 1,2)  4   1:40  2:34                        34     2
└─ compute(6,8, 2,7)     7   2:169 3:157 4:145 5:149 6:185     145    4
   ├─ compute(6,6, 2,4)  6   2:89  3:85  4:89                  85     3
   └─ compute(8,8, 4,7)  8   4:164 5:164 6:194 7:290           164    4
```

Recursion shape:

```
                    [2..8] mid 5, cuts 1..4
                 /                          \
      [2..4] mid 3, cuts 1..2        [6..8] mid 7, cuts 2..6
        /            \                 /              \
 [2] cuts 1..1   [4] cuts 1..2   [6] cuts 2..4    [8] cuts 4..7
```

Result: `dp[2] = [∞, ∞, 13, 20, 34, 50, 85, 145, 164]`, identical to the slow table. Cuts scanned: 4 + 2 + 1 + 2 + 5 + 3 + 4 = **21**, versus **28** for the slow layer. On `n = 8` the saving is small; on `n = 3000` it is the difference between 4.5 million and about 40 thousand scans per layer.

Layer 3 is filled the same way from `dp[2]`, giving `dp[3][8] = 110`.

## 10. Complexity analysis

| Approach | Time | Space | Why |
|----------|------|-------|-----|
| Brute force | exponential (≈ `C(n-1, k-1)` leaves) | O(k) stack | Tries every way to place `k-1` cuts. |
| Memoization / slow tabulation | O(k·n²) | O(k·n) (or O(n) with two rows) | `k·n` states, each scans up to `n` cuts. |
| D&C optimization | O(k·n log n) | O(n) + O(log n) stack | See below. |

Why one layer costs O(n log n): the recursion halves `[lo, hi]` each time, so it has about `log₂ n` levels. On one level, the calls' cut ranges are `[o0, o1], [o1, o2], [o2, o3], …` — consecutive pieces of `[0, n]` that overlap only at their endpoints. So the total length scanned on one level is at most `n + (number of calls on that level) ≤ 2n`. `log₂ n` levels × `2n` = O(n log n). For `n = k = 3000`: about `3000 · 3000 · 12 ≈ 1.1·10⁸` simple operations, fast enough, versus `1.35·10¹⁰` for the slow DP.

## 11. Why this approach works

- The recurrence itself is the ordinary partition DP: every cutting has a last group, and the best cutting's first `g-1` groups must themselves be optimal for their prefix (optimal substructure).
- D&C only changes *which* cuts we try. We never skip the optimal cut, because of monotonicity (section 4.9): for states left of `mid`, every cut right of `opt(mid)` is not the leftmost optimum, and for states right of `mid`, every cut left of `opt(mid)` is strictly worse.
- Monotonicity is guaranteed by the quadrangle inequality of `cost`, which we verified algebraically for `(sum)²`.

## 12. Common mistakes

1. **Applying it without monotone opt.** If the cost does not satisfy QI (for example, cost can be negative in strange ways, or values can be negative so `(sum)²` is no longer "convex in length"), D&C silently returns wrong answers. Always cross-check against the slow DP.
2. **Mixing leftmost and rightmost optimum.** Pick `<` (leftmost) or `≤` (rightmost) and use it everywhere.
3. **Forgetting `min(mid - 1, optHi)`.** Without it you may evaluate an empty or negative-length last group.
4. **Wrong initial ranges.** For layer `g`, states start at `g` and cuts start at `g-1`; otherwise impossible states (`∞`) leak in and `∞ + cost` can overflow. Using `Long.MAX_VALUE / 4` as infinity avoids overflow when adding a cost.
5. **Recomputing `cost` in O(n).** The analysis assumes O(1) cost; use prefix sums.
6. **Overwriting the previous layer while reading it.** Keep two separate arrays.

## 13. When to use this technique / how to recognize similar problems

Signals:

- The DP has the shape `dp[k][i] = min_{j < i} dp[k-1][j] + cost(j, i)` — "split into exactly / at most `k` contiguous groups".
- `cost(j, i)` is O(1) (after prefix sums) and "grows faster for bigger groups": squares of sums, distance to a median, variance-like costs.
- Limits like `n ≤ 3000–10⁵`, `k ≤ n` where O(k·n²) is too slow but O(k·n log n) fits.

Related techniques:

- If the DP is an **interval** DP `dp[i][j] = min_k dp[i][k] + dp[k][j] + C(i, j)`, look at [Knuth optimization](../08-knuth-optimization/).
- If `cost(j, i)` splits into "something with `j`" × "something with `i`", the [convex hull trick](../06-convex-hull-trick/) can be even faster.
- If there is no `k` dimension and `cost` is a window maximum, see [monotonic queue optimization](../04-monotonic-queue-optimization/).

## 14. Summary and revision notes

```
+--------------------------------------------------------------+
| dp[g][i] = min_{g-1 <= j < i} dp[g-1][j] + cost(j, i)         |
| If cost satisfies a<=b<=c<=d:                                 |
|     cost(a,c) + cost(b,d) <= cost(a,d) + cost(b,c)            |
| then opt(g, i) <= opt(g, i+1), and each layer is filled by    |
|     compute(lo, hi, optLo, optHi) in O(n log n).              |
+--------------------------------------------------------------+
```

- Write the slow O(k·n²) DP first; it is your test oracle.
- Monotone opt = the best last cut never moves left as the prefix grows.
- QI in words: two overlapping medium groups ≤ one big + one small. For `(sum)²` the gap is `2AC ≥ 0`.
- Solve the middle state with a full scan; children inherit half-ranges of cuts.
- Per layer: `log n` levels × O(n) scans. Memory: two rows.
- Keep the leftmost optimum (strict `<`).

## 15. Exercises

**Exercise 1.** For `a = [1, 1, 1, 1]` and `k = 2`, list `opt(2, i)` for `i = 2, 3, 4`, and the final answer.

<details><summary>Answer</summary>

`prefix = [0,1,2,3,4]`, `dp[1] = [∞,1,4,9,16]`.
- `i=2`: only `j=1`: 1 + 1 = 2 → opt 1.
- `i=3`: `j=1`: 1 + 4 = 5; `j=2`: 4 + 1 = 5 → tie, leftmost opt 1, value 5.
- `i=4`: `j=1`: 1 + 9 = 10; `j=2`: 4 + 4 = **8**; `j=3`: 9 + 1 = 10 → opt 2.

`opt = 1, 1, 2` (non-decreasing), answer 8 (`[1,1] [1,1]`).
</details>

**Exercise 2.** Does `cost(j, i) = (i - j)` (just the group length) satisfy the quadrangle inequality? What does that tell you about the problem "split into `k` groups minimizing the sum of lengths"?

<details><summary>Answer</summary>

Yes, with equality: `(c-a) + (d-b) = (d-a) + (c-b)`. The QI holds, so D&C is valid, but the problem is trivial: every cutting costs exactly `n`. QI with equality means "no preference", and any `opt` sequence works as long as we pick consistently (leftmost).
</details>

**Exercise 3.** Allocate Mailboxes (LeetCode 1478): houses sorted on a line, `k` mailboxes, each house walks to its nearest mailbox. Write the partition DP and say what `cost(j, i)` is and how to get it in O(1).

<details><summary>Answer</summary>

Sort the houses. `dp[g][i]` = minimum total distance for the first `i` houses served by `g` mailboxes, `dp[g][i] = min_j dp[g-1][j] + cost(j, i)`, where `cost(j, i)` = total distance from houses `j..i-1` to their **median** house (the median minimizes the sum of absolute distances). With `m = (j + i - 1) / 2` and prefix sums `P` of positions: `cost = h[m]·(m - j) - (P[m] - P[j]) + (P[i] - P[m+1]) - h[m]·(i - 1 - m)`. This cost satisfies QI, so the D&C optimization applies; for LeetCode's limits (`n ≤ 100`) the slow DP is also fine.
</details>

**Exercise 4.** In the dry run of section 9, how many cuts would layer 2 scan if `opt` were *not* used at all (slow DP), and how many with D&C? Why is the gap small here?

<details><summary>Answer</summary>

Slow: `1 + 2 + … + 7 = 28`. D&C: 21. The gap is small because `n = 8` gives only 3 recursion levels; the saving grows like `n / log n`, so for `n = 3000` it is roughly a factor of 250.
</details>
