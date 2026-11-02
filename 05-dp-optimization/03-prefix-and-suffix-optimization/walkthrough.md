# Prefix and Suffix Optimization — Walkthrough

> Main example: **AtCoder DP Contest M — Candies**, input `N = 3`, `K = 4`, `limits = [1, 2, 3]`, answer `5`.
> Second family: prefix/suffix **maxima** (LeetCode 1289 and 1937).

---

## 1. Intuition

Imagine a shop clerk who is asked, again and again, "how much money came in between 10:00 and 14:00?",
"between 11:00 and 15:00?", "between 12:00 and 16:00?". A slow clerk re-adds every receipt each time.
A smart clerk keeps a **running total** written next to every hour: "by 10:00 we had ₹500, by 14:00 we had ₹2100".
Now any question is a single subtraction: `2100 − 500`.

Many DP transitions are exactly the slow clerk. To fill one cell they re-add (or re-maximize) a whole
*stretch* of the previous row. Neighbouring cells re-add almost the same stretch. A running total of the
previous row — a **prefix sum** — turns each of those stretches into one subtraction. When the transition
takes a **maximum** instead of a sum, a running maximum (a **prefix maximum**, or a **suffix maximum** when
we run from the right) plays the same role.

Nothing about the DP *meaning* changes. We only change how fast we read the previous row.

---

## 2. Problem statement in our own words

**Candies** ([AtCoder DP Contest M](https://atcoder.jp/contests/dp/tasks/dp_m)).
There are `N` children and exactly `K` candies to hand out. Child `i` is willing to take anywhere from
`0` up to `limits[i]` candies. Every candy must be given away. Count the number of different distributions,
modulo `1 000 000 007`. Two distributions differ if some child gets a different number of candies.
Limits: `N ≤ 100`, `K ≤ 100 000`.

---

## 3. Examples

| Input | Output | Why |
|---|---|---|
| `N=3, K=4, limits=[1,2,3]` | `5` | `(0,1,3) (0,2,2) (1,0,3) (1,1,2) (1,2,1)` |
| `N=1, K=10, limits=[9]` | `0` | the only child can hold at most 9 |
| `N=2, K=0, limits=[0,0]` | `1` | give nobody anything — one way |
| `N=4, K=100000, limits=[100000]*4` | `665683269` | huge; only the fast version finishes |

Let us double-check the first row by listing. Child 1 takes `x1 ∈ {0,1}`.

```
x1 = 0  -> children 2,3 share 4:  x2 ∈ {0,1,2}, x3 ∈ {0..3}, x2+x3=4  -> (1,3) (2,2)       = 2 ways
x1 = 1  -> children 2,3 share 3:                               x2+x3=3  -> (0,3) (1,2) (2,1) = 3 ways
                                                                                    total = 5
```

---

## 4. Deriving the solution — the 7 questions

### 4.1 What is changing?
Two things: **which child** we are deciding for (we go child by child), and **how many candies have been
used so far** (equivalently, how many are left).

### 4.2 What does the state represent?
`dp[i][j]` = *the number of ways to give exactly `j` candies in total to the first `i` children, each within
their limit.* The answer is `dp[N][K]`.

### 4.3 What are the choices?
For child `i` (1-based), choose how many candies `x` they take: `x = 0, 1, …, limits[i-1]`, but never more
than `j` (we cannot have used a negative number of candies before them).

### 4.4 What is the recurrence?
If child `i` takes `x`, the first `i−1` children must have taken exactly `j − x`. Different `x` give different
distributions, so we **add**:

```
dp[i][j] = Σ  dp[i-1][j - x]          for x = 0 .. min(limits[i-1], j)
         = dp[i-1][j - a] + dp[i-1][j - a + 1] + ... + dp[i-1][j]     where a = min(limits[i-1], j)
```

Read the second line slowly: cell `(i, j)` adds a **contiguous window** of the previous row that **ends at
column `j`** and has length up to `limits[i-1] + 1`.

### 4.5 What are the base cases?
`dp[0][0] = 1` (zero children, zero candies: exactly one way — do nothing), and `dp[0][j] = 0` for `j > 0`
(zero children cannot absorb candies).

### 4.6 What is the order of computation?
Row `i` reads only row `i−1`. So fill rows top to bottom; inside a row any column order works.

### 4.7 Can the state space be optimized?
Space: only the previous row is read, so two rows of length `K+1` suffice
(see [Space Optimization](../01-space-optimization/) and [Rolling Arrays](../02-rolling-arrays/)).
Time: this is the new idea of this topic. Each cell costs up to `K+1` additions, so the table costs
`O(N · K · K) = 100 · 10^5 · 10^5 = 10^12` operations in the worst case — hopeless. Sections 4.8–4.9
show why we can make each cell O(1).

### 4.8 Prerequisite: prefix sums

A **prefix sum** array `P` of an array `A` stores running totals:

```
P[0] = 0
P[t] = A[0] + A[1] + ... + A[t-1]        (sum of the first t elements)
```

Small example, `A = [3, 1, 4, 1, 5]`:

```
index t :   0   1   2   3   4   5
A[t]    :   3   1   4   1   5
P[t]    :   0   3   4   8   9  14
```

**Range sum in O(1).** The sum of `A[lo..hi]` (inclusive) is `P[hi+1] − P[lo]`, because `P[hi+1]` contains
everything up to `hi`, and subtracting `P[lo]` removes everything before `lo`.

```
A[1..3] = 1 + 4 + 1 = 6        P[4] - P[1] = 9 - 3 = 6   ✔
A[0..4] = 14                   P[5] - P[0] = 14 - 0 = 14 ✔
```

Building `P` costs O(length) once; after that every range sum is a single subtraction.

**The extra `P[0] = 0` slot** is not decoration: it lets a range that starts at index 0 use the same formula
(`P[hi+1] − P[0]`) with no special case.

**Prefix sums under a modulus.** In Candies we store everything modulo `M = 10^9+7`. Then
`P[hi+1] − P[lo]` can be *negative* even though the true sum is positive (e.g. `P[hi+1] = 2`, `P[lo] = M−1`).
Always write `(P[hi+1] − P[lo] + M) % M`.

### 4.9 The key property and why it holds

> **If a transition adds up a contiguous range of the previous row, and that row is fully known before the
> current row starts, then one prefix-sum array of the previous row answers every cell's range in O(1).**

Why it holds for Candies:

1. **The range is contiguous.** `x` runs over consecutive integers `0..a`, so `j − x` runs over consecutive
   columns `j−a .. j`. No gaps.
2. **The previous row never changes while we fill the current row.** Row `i−1` is final before row `i`
   begins (question 4.6), so we can build its prefix array once and reuse it for all `K+1` cells of row `i`.
3. **Addition can be undone by subtraction.** That is what makes `P[hi+1] − P[lo]` legal. (Remember this —
   it fails for `max`, see 4.10.)

So:

```
dp[i][j] = P_{i-1}[j + 1] − P_{i-1}[max(0, j − limits[i-1])]
```

where `P_{i-1}` is the prefix-sum array of row `i−1`.

Numeric check with our example. Row 2 is `dp[2] = [1, 2, 2, 1, 0]` (we derive it in Section 7). Its prefix
array is `P = [0, 1, 3, 5, 6, 6]`. Child 3 has limit 3:

```
dp[3][4] = P[5] − P[max(0,4−3)] = P[5] − P[1] = 6 − 1 = 5   (= dp[2][1]+dp[2][2]+dp[2][3]+dp[2][4] = 2+2+1+0) ✔
dp[3][3] = P[4] − P[0]          = 6 − 0     = 6   (= 1+2+2+1) ✔
```

### 4.10 The same trick for maxima and minima: prefix/suffix extremes

When the transition **maximizes** (or minimizes) over a range of the previous row, subtraction no longer works:
knowing `max(A[0..5]) = 9` and `max(A[0..2]) = 9` tells you *nothing* about `max(A[3..5])`. Max has no inverse.

But two special range shapes still work with a single running value:

| Range shape | Tool | Recurrence |
|---|---|---|
| everything **up to** `c` (`0..c`) | prefix maximum | `pre[c] = max(pre[c−1], A[c])` |
| everything **from** `c` (`c..n−1`) | suffix maximum | `suf[c] = max(suf[c+1], A[c])` |
| everything **except** `c` | prefix max of `0..c−1` and suffix max of `c+1..n−1`, **or** best + second best | see below |

Small example, `A = [4, 9, 2, 7]`:

```
c     :  0  1  2  3
A[c]  :  4  9  2  7
pre[c]:  4  9  9  9      (max of A[0..c])
suf[c]:  9  9  7  7      (max of A[c..3])
"all except c" = max(pre[c-1], suf[c+1]):
         c=0: suf[1]=9   c=1: max(4, 7)=7   c=2: max(9,7)=9   c=3: pre[2]=9
```

**Best and second best.** "Max of everything except position `c`" needs only three numbers: the largest
value, the column where it occurs, and the second largest value. If `c` is the column of the largest, the
answer is the second largest; otherwise it is the largest. For `A = [4, 9, 2, 7]`: best `9` at column 1,
second best `7`. Except `c=1` → `7`; except any other column → `9`. Same results as above, with O(1) memory.
This is exactly LeetCode 1289 (Section 8.2).

A **window that slides** (a fixed-length range whose both ends move, like `c−k..c−1`) is *not* covered by
prefix/suffix extremes. That needs a monotonic deque — the next topic,
[Monotonic Queue Optimization](../04-monotonic-queue-optimization/).

---

## 5. Brute force: recursion

`ways(child, remaining)` = number of ways to give exactly `remaining` candies to children `child..N−1`.

```java
static long bruteForce(int[] limits, int child, int remaining) {
    if (child == limits.length) return remaining == 0 ? 1 : 0;
    long ways = 0;
    for (int give = 0; give <= Math.min(limits[child], remaining); give++)
        ways += bruteForce(limits, child + 1, remaining - give);
    return ways % MOD;
}
```

Recursion tree for `limits = [1,2,3]`, `K = 4` (each node is `(child, remaining)`):

```
                                  (0,4)
                      give 0 /            \ give 1
                        (1,4)                (1,3)
                 0/     1|     \2        0/    1|    \2
              (2,4)   (2,3)   (2,2)   (2,3)   (2,2)  (2,1)
               |        |       |       |       |      |
          needs 4>3   give 3  give 2  give 3  give 2  give 1
             0 ways   1 way   1 way   1 way   1 way   1 way     -> total 5
```

Already `(2,3)` and `(2,2)` appear twice: overlapping subproblems. With `N` children and limits up to `K`,
the tree has up to `(K+1)^N` leaves — exponential.

---

## 6. Memoization (top-down)

Cache `ways(child, remaining)` in a `(N+1) × (K+1)` table. There are `N·(K+1)` distinct states, but each
state still runs a loop of up to `K+1` iterations, so memoization is `O(N·K²)` — exactly as slow as the
tabulation of Section 7. We skip a separate memoized file: it teaches nothing new here, and the speed-up
we want (prefix sums) is much more natural in the bottom-up form, where the whole previous row is
available at once.

---

## 7. Tabulation (bottom-up)

```java
long[] prev = new long[K + 1];
prev[0] = 1;
for (int limit : limits) {
    long[] cur = new long[K + 1];
    for (int j = 0; j <= K; j++) {
        long sum = 0;
        for (int x = 0; x <= Math.min(limit, j); x++) sum += prev[j - x];   // the expensive inner loop
        cur[j] = sum % MOD;
    }
    prev = cur;
}
return prev[K];
```

Filling the table for `limits = [1,2,3]`, `K = 4`. Each new cell adds the window of the row above that ends at
the same column and has length `limit + 1`.

```
            j:   0   1   2   3   4
dp[0]          [ 1   0   0   0   0 ]        base case

child 1, limit 1  (window length 2: columns j-1..j)
dp[1][0] = dp0[0]                = 1
dp[1][1] = dp0[0] + dp0[1]       = 1
dp[1][2] = dp0[1] + dp0[2]       = 0
dp[1][3] = 0 ,  dp[1][4] = 0
dp[1]          [ 1   1   0   0   0 ]

child 2, limit 2  (window length 3: columns j-2..j)
dp[2][0] = 1
dp[2][1] = 1 + 1                 = 2
dp[2][2] = 1 + 1 + 0             = 2
dp[2][3] = 1 + 0 + 0             = 1
dp[2][4] = 0 + 0 + 0             = 0
dp[2]          [ 1   2   2   1   0 ]

child 3, limit 3  (window length 4: columns j-3..j)
dp[3][0] = 1
dp[3][1] = 1 + 2                 = 3
dp[3][2] = 1 + 2 + 2             = 5
dp[3][3] = 1 + 2 + 2 + 1         = 6
dp[3][4] = 2 + 2 + 1 + 0         = 5     <- answer
dp[3]          [ 1   3   5   6   5 ]
```

Look at how much work repeats. `dp[3][3]` adds `1+2+2+1`, and `dp[3][4]` adds `2+2+1+0` — three of the four
terms are shared. With `K = 10^5` and generous limits, each row repeats ~`10^5` additions `10^5` times.

```
 row i-1:  [ . . . . . . . . . . . . . . . ]
                    |<----- window for j ----->|
                      |<----- window for j+1 ----->|
                     the windows overlap almost entirely
```

---

## 8. Optimization: prefix sums of the previous row

Build the prefix array of `prev` once per row, then read every window in O(1):

```java
long[] prefix = new long[K + 2];
for (int limit : limits) {
    prefix[0] = 0;
    for (int t = 0; t <= K; t++) prefix[t + 1] = (prefix[t] + prev[t]) % MOD;   // O(K), once per row
    long[] cur = new long[K + 1];
    for (int j = 0; j <= K; j++) {
        int lo = Math.max(0, j - limit);                         // first column of the window
        cur[j] = (prefix[j + 1] - prefix[lo] + MOD) % MOD;       // window sum prev[lo..j]
    }
    prev = cur;
}
return prev[K];
```

Full code with a randomized cross-check against the slow version: [solution/Optimized.java](solution/Optimized.java).
Slow versions: [solution/Slow.java](solution/Slow.java).

> **Alternative you will meet:** instead of a separate prefix array, keep a running window sum while
> scanning `j` upward: add `prev[j]` as it enters the window, subtract `prev[j − limit − 1]` as it leaves.
> It is the same idea (a sum can be undone), with one array fewer.

### 8.1 Where else range sums appear: AtCoder DP T — Permutation

In [AtCoder DP Contest T — Permutation](https://atcoder.jp/contests/dp/tasks/dp_t) you count permutations of
`1..N` that follow a given pattern of `<` and `>` signs. The standard state is
`dp[i][j]` = number of ways to arrange the first `i+1` positions so that the last placed value is the
`j`-th smallest (0-based) among those `i+1` values. The transition is

```
sign '<' :  dp[i][j] = Σ dp[i-1][j']   for j' = 0 .. j-1        (a PREFIX range)
sign '>' :  dp[i][j] = Σ dp[i-1][j']   for j' = j .. i-1        (a SUFFIX range)
```

Without prefix sums it is `O(N³)`; with a prefix (or suffix) sum of row `i−1` it is `O(N²)`. Same trick,
different story. You will fill this table by hand in Exercise 3.

### 8.2 Prefix/suffix minimum: LeetCode 1289 (best and second best)

[Minimum Falling Path Sum II](https://leetcode.com/problems/minimum-falling-path-sum-ii/): in an `n × n` grid,
pick one cell per row, top to bottom, never using the same column in two consecutive rows; minimize the
sum. (It extends [Maximum Falling Path Sum](../../02-two-dimensional-and-grid-dp/06-maximum-falling-path-sum/).)

```
dp[r][c] = grid[r][c] + min over c' ≠ c of dp[r-1][c']        slow: O(n) per cell -> O(n³)
```

"Minimum of the previous row except one column" is the shape from Section 4.10. Keep the smallest value of
the previous row, its column, and the second smallest:

```java
int above = (c == bestCol) ? second : best;   // O(1) instead of an O(n) scan
```

Dry run on `grid = [[1,2,3],[4,5,6],[7,8,9]]`:

```
row 0: dp = [1, 2, 3]        best = 1 (col 0), second = 2
row 1: col 0 is best's column -> 4 + second(2) = 6
       col 1                   -> 5 + best(1)   = 6
       col 2                   -> 6 + best(1)   = 7
       dp = [6, 6, 7]        best = 6 (col 0), second = 6 (col 1)   <- ties are fine
row 2: col 0 -> 7 + second(6) = 13 ; col 1 -> 8 + 6 = 14 ; col 2 -> 9 + 6 = 15
answer = min(13, 14, 15) = 13      (path 1 -> 5 -> 7)
```

Code with cross-check: [solution/MinFallingPathSumII.java](solution/MinFallingPathSumII.java).

### 8.3 Prefix/suffix maximum with a distance penalty: LeetCode 1937

[Maximum Number of Points with Cost](https://leetcode.com/problems/maximum-number-of-points-with-cost/):
pick one cell per row; you earn the cell values but lose `|c − c'|` points between consecutive rows.

```
dp[r][c] = points[r][c] + max over c' of ( dp[r-1][c'] − |c − c'| )
```

The absolute value is the obstacle. Split by side:

```
c' ≤ c :  dp[r-1][c'] − (c − c')     -> left[c]  = max( left[c-1] − 1 , prev[c] )    (prefix sweep →)
c' ≥ c :  dp[r-1][c'] − (c' − c)     -> right[c] = max( right[c+1] − 1, prev[c] )    (suffix sweep ←)
dp[r][c] = points[r][c] + max(left[c], right[c])
```

Why `left[c−1] − 1`? Every candidate that was in `left[c−1]` is now one more column away, so each loses one
more point — and they all lose the *same* amount, so the best one stays the best.

Example `points = [[1,2,3],[1,5,1],[3,1,1]]`:

```
row 0: prev  = [1, 2, 3]
       left  = [1, max(0,2)=2, max(1,3)=3]      right = [max(1,1)=1, max(2,2)=2, 3]
row 1: cur   = [1+1, 5+2, 1+3] = [2, 7, 4]
       left  = [2, max(1,7)=7, max(6,4)=6]      right = [max(6,2)=6, max(3,7)=7, 4]
row 2: cur   = [3+6, 1+7, 1+6] = [9, 8, 7]      answer 9
```

Code with cross-check: [solution/MaxPointsWithCost.java](solution/MaxPointsWithCost.java).

---

## 9. Dry run on a full example

Candies, `limits = [1,2,3]`, `K = 4`, with the optimized method. For each child: the previous row, its prefix
array, and each new cell as `prefix[j+1] − prefix[lo]`.

| child (limit) | prev row | prefix array `P` | new row computation | new row |
|---|---|---|---|---|
| 1 (1) | `[1,0,0,0,0]` | `[0,1,1,1,1,1]` | j0: P1−P0=1; j1: P2−P0=1; j2: P3−P1=0; j3: P4−P2=0; j4: P5−P3=0 | `[1,1,0,0,0]` |
| 2 (2) | `[1,1,0,0,0]` | `[0,1,2,2,2,2]` | j0: P1−P0=1; j1: P2−P0=2; j2: P3−P0=2; j3: P4−P1=1; j4: P5−P2=0 | `[1,2,2,1,0]` |
| 3 (3) | `[1,2,2,1,0]` | `[0,1,3,5,6,6]` | j0: P1−P0=1; j1: P2−P0=3; j2: P3−P0=5; j3: P4−P0=6; j4: P5−P1=5 | `[1,3,5,6,5]` |

Answer `dp[3][4] = 5`, matching the slow table in Section 7 cell by cell.

---

## 10. Complexity analysis

| Approach | Time | Why | Space |
|---|---|---|---|
| Brute force | `O((K+1)^N)` worst case | every child branches into up to `K+1` choices | `O(N)` stack |
| Memoization / slow table | `O(N·K²)` | `N·(K+1)` cells × up to `K+1` additions each | `O(K)` with two rows |
| Prefix sums | `O(N·K)` | per child: `O(K)` to build `P`, then `K+1` cells × O(1) | `O(K)`: `prev`, `cur`, `P` |
| LC 1289 slow / fast | `O(n³)` / `O(n²)` | O(n) scan per cell vs O(1) lookup | `O(n)` / `O(n)` |
| LC 1937 slow / fast | `O(m·n²)` / `O(m·n)` | O(n) scan per cell vs two O(n) sweeps per row | `O(n)` |

For the largest Candies input: slow `≈ 100 · 10^5 · 10^5 = 10^12` steps (hours); fast `≈ 2 · 100 · 10^5 = 2·10^7`
steps (milliseconds).

---

## 11. Why this approach works

The optimized code computes **exactly the same numbers** as the slow code; it only computes each window sum
differently. `P[j+1] − P[lo]` equals `prev[lo] + … + prev[j]` by the definition of a prefix sum (all terms up to
`j` minus all terms before `lo`). Since every cell of the slow table equals that window sum, and the prefix
array is built from the *final* previous row, every cell of the fast table equals the slow one. Modular
arithmetic does not break this: `(a − b) mod M` is the same whether you reduce before or after, as long as
you add `M` to avoid a negative result.

For the maximum versions: `left[c]` is by induction the maximum over all `c' ≤ c` of `prev[c'] − (c − c')` —
true for `c = 0`, and moving from `c−1` to `c` subtracts 1 from every old candidate and adds one new candidate
`prev[c]`. The best/second-best rule is correct because removing one element from a set changes its minimum
only if that element *was* the minimum, and then the new minimum is the second smallest.

---

## 12. Common mistakes

1. **Off-by-one in the prefix array.** With `P[t] = A[0] + … + A[t−1]`, the range `lo..hi` is `P[hi+1] − P[lo]`,
   not `P[hi] − P[lo]`. Write the definition as a comment next to the array.
2. **Negative results under modulo.** `(P[hi+1] − P[lo]) % MOD` can be negative in Java. Add `MOD` first.
3. **Building the prefix array from the row you are writing.** The prefix array must come from the
   *previous* row. If you update `dp` in place while summing, the window reads new values.
4. **Forgetting to clamp the window.** `lo = j − limit` can be negative; use `max(0, j − limit)`.
5. **Using prefix maxima for a sliding window.** Prefix maxima only answer ranges that start at index 0
   (or, for suffixes, end at the last index). A window `j−k..j−1` needs a monotonic deque.
6. **Best/second-best with ties.** If two columns tie for the minimum, `second` must equal that same value
   (not the next larger one). Updating with `if (v < best) {second = best; …} else if (v < second) …` handles it.

---

## 13. When to use this technique / how to recognize similar problems

Ask: **"Does the inner loop of my transition walk over a contiguous range of an already finished row?"**

- `Σ dp[i-1][j'] for j' in [L(j), R(j)]` → prefix sums (Candies, Permutation, counting with bounds).
- `max/min dp[i-1][j'] for j' ≤ j` or `j' ≥ j` → prefix/suffix extremes.
- `max/min dp[i-1][j'] for j' ≠ j` → best + second best (LC 1289, "paint houses with no equal neighbours").
- `max dp[i-1][j'] − |j − j'|` → left sweep + right sweep (LC 1937).
- `max dp[j'] for j'` in a window that **slides** → not this topic; use a monotonic deque.

Typical warning sign: a correct DP that is one factor of `n` or `K` too slow, with an inner `for` loop that
does nothing but `sum +=` or `best = max(...)`.

---

## 14. Summary and revision notes

- A transition that reads a **contiguous range** of a finished row can be answered in O(1) per cell.
- Sums: prefix sums, `range(lo..hi) = P[hi+1] − P[lo]`; under a modulus add `MOD` before `%`.
- Max/min: prefix max (ranges starting at 0), suffix max (ranges ending at the end), best + second best
  ("all except one"), left/right sweeps for distance penalties.
- It removes one factor from the running time (`O(N·K²) → O(N·K)`, `O(n³) → O(n²)`) and never changes the answer.

```
+------------------------------------------------------------------+
|  Candies:  dp[i][j] = Σ_{x=0..min(a_i, j)} dp[i-1][j-x]          |
|                     = P[j+1] - P[max(0, j - a_i)]   (mod M)       |
|            P[t] = dp[i-1][0] + ... + dp[i-1][t-1]                 |
|  O(N·K²)  ──prefix sums──►  O(N·K)                               |
+------------------------------------------------------------------+
```

---

## 15. Exercises

**Exercise 1.** Solve Candies by hand with prefix sums for `limits = [2, 2]`, `K = 2`.

<details><summary>Answer</summary>

`dp[0] = [1,0,0]`, `P = [0,1,1,1]`. Child 1 (limit 2): j0 `P1−P0=1`, j1 `P2−P0=1`, j2 `P3−P0=1` → `[1,1,1]`.
`P = [0,1,2,3]`. Child 2 (limit 2): j2 `P3−P0 = 3`. Answer **3**: `(0,2) (1,1) (2,0)`.
</details>

**Exercise 2.** A friend proposes: "for `max` over a window `lo..hi`, use `preMax[hi] − preMax[lo−1]`, just like
sums." Give a counterexample and say what you would use instead.

<details><summary>Answer</summary>

`A = [9, 1, 2]`, window `1..2`. True max is `2`. `preMax = [9, 9, 9]`, and `9 − 9 = 0` — nonsense. Max cannot be
"undone" by subtraction. If the window always starts at 0 use a prefix max; if it slides, use a monotonic
deque ([next topic](../04-monotonic-queue-optimization/)); for "everything except one" use best + second best.
</details>

**Exercise 3.** AtCoder DP T (Permutation) with signs `"<><"` (`N = 4`). Using the transition in Section 8.1,
fill the rows `dp[0..3]` and give the answer (the sum of the last row).

<details><summary>Answer</summary>

```
dp[0] = [1]
'<' : dp[1][j] = Σ dp[0][0..j-1]  -> [0, 1]
'>' : dp[2][j] = Σ dp[1][j..1]    -> [0+1, 1, 0] = [1, 1, 0]
'<' : dp[3][j] = Σ dp[2][0..j-1]  -> [0, 1, 2, 2]
```
Answer `0 + 1 + 2 + 2 =` **5** (the five permutations are 1324, 1423, 2314, 2413, 3412).
</details>

**Exercise 4.** In LC 1289 a learner stores only `best` and `bestCol`, and when `c == bestCol` scans the row
again for the minimum. What is the worst-case running time, and why is the second-best version better?

<details><summary>Answer</summary>

Only one column per row equals `bestCol`, so the extra scan happens once per row: `O(n)` per row, `O(n²)` total —
still fine! But the scan needs the whole previous row stored. The second-best version needs only three numbers
and no rescan, which is simpler and generalizes to problems with more colors/rows where rescanning is costly.
</details>
