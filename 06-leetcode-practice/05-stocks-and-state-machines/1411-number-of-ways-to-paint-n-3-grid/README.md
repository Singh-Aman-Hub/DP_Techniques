# 1411. Number of Ways to Paint N × 3 Grid

> **Pattern:** Row-profile DP, then compress the profiles into two state classes · **Builds on:** [State Machine DP](../../../04-advanced-dp-patterns/12-state-machine-dp/) · **Difficulty:** Hard

## Problem overview

Paint every cell of an `n × 3` grid with one of three colours so that no two cells sharing an
edge (horizontally or vertically) have the same colour. Count the colourings modulo 10⁹ + 7.

## Link & metadata

| Field | Value |
|---|---|
| LeetCode | [1411. Number of Ways to Paint N × 3 Grid](https://leetcode.com/problems/number-of-ways-to-paint-n-3-grid/) |
| Difficulty | Hard |
| Pattern | Each row is a state (one of 12 valid patterns); compatible rows are transitions; symmetry collapses 12 states to 2 |
| Prerequisite topic | [`04-advanced-dp-patterns/12-state-machine-dp`](../../../04-advanced-dp-patterns/12-state-machine-dp/) |
| Why this pattern | Whether row `r+1` is allowed depends only on row `r`, so "the previous row's colouring" is the complete state, exactly like "the last letter" in 1220 |

## Examples

| n | Answer |
|---|---|
| 1 | 12 |
| 2 | 54 |
| 3 | 246 |
| 5000 | 30228214 |

## Brute-force intuition

All `3^(3n)` colourings, checked one by one: hopeless. Build row by row instead. A single row
of 3 cells with no equal neighbours has `3 · 2 · 2 = 12` patterns. Given a row's pattern, the
next row only needs to differ **column by column** from it (and be valid itself). So:

- state = the pattern of the last row (12 possibilities),
- transition = "pattern q may sit below pattern p".

That is already a fine O(12 · 12 · n) DP. The Hard insight is that the 12 patterns fall into
**two families** that behave identically:

```
ABA type (first == last colour):   RGR RBR GRG GBG BRB BGB      → 6 patterns
ABC type (all three different):    RGB RBG GRB GBR BRG BGR      → 6 patterns
```

Count the patterns that can go under one of each type:

```
below an ABA row (e.g. RGR):  GRG GBG BRB  (ABA ×3)   GRB BRG  (ABC ×2)
below an ABC row (e.g. RGB):  GRG GBG      (ABA ×2)   GBR BRG  (ABC ×2)
```

Every pattern of the same type has the same counts (rename the colours), so we only need to
track **how many colourings end in an ABA row** and **how many end in an ABC row**.

## Deriving the DP (7 questions)

### 1. What is changing?
The row index, and the type (or the exact pattern) of the last painted row.

### 2. What does the state represent?
- Full version: `dp[r][p]` = colourings of rows `0..r` whose row `r` has pattern `p` (one of 12).
- Compressed: `aba[r]`, `abc[r]` = colourings of rows `0..r` whose last row is of that type.

### 3. What are the choices?
The pattern of the next row, among those compatible with the current one.

### 4. What is the recurrence?
```
full:        dp[r][q] = Σ over p compatible with q of dp[r-1][p]

compressed:  aba[r] = 3·aba[r-1] + 2·abc[r-1]
             abc[r] = 2·aba[r-1] + 2·abc[r-1]
answer = aba[n-1] + abc[n-1]                     (all mod 10^9+7)
```

### 5. What are the base cases?
Row 0: each of the 12 patterns once, so `aba[0] = 6`, `abc[0] = 6`.

### 6. What is the order of computation?
Top row to bottom row.

### 7. Can the state space be optimized?
Yes, twice: from 12 states to 2 by symmetry, and from `n` rows to the last one. Two variables.

## Recursive solution

```java
// ways to paint rows `row`..n-1 given that the row above has pattern `prev`
static long ways(int row, int prev) {
    if (row == n) return 1;
    long total = 0;
    for (int next = 0; next < 12; next++)
        if (compatible[prev][next]) total += ways(row + 1, next);
    return total % MOD;
}
```

## Memoization

The 12-pattern version, cached by `(row, prev)`. It generates the 12 patterns and their
compatibility table in code, so nothing is hard-coded. See
[`solution/Memoization.java`](solution/Memoization.java).

## Tabulation

The 12-pattern table `dp[n][12]`. See [`solution/Tabulation.java`](solution/Tabulation.java).

## Space optimization

The two-type recurrence with two variables. See [`solution/Optimized.java`](solution/Optimized.java).

## Dry run

Compressed version:

| row | aba = 3·aba + 2·abc | abc = 2·aba + 2·abc | total |
|---|---|---|---|
| 0 | 6 | 6 | 12 |
| 1 | 3·6 + 2·6 = 30 | 2·6 + 2·6 = 24 | 54 |
| 2 | 3·30 + 2·24 = 138 | 2·30 + 2·24 = 108 | **246** |

## Complexity

| Approach | Time | Space | Reason |
|---|---|---|---|
| Brute force | O(3^(3n)) | O(n) | every colouring |
| Memoization (12 patterns) | O(n · 144) | O(n · 12) | 12n states × 12 candidate next rows |
| Tabulation (12 patterns) | O(n · 144) | O(n · 12) | same loops |
| Optimized (2 types) | O(n) | O(1) | two variables, four multiplications per row |

## Why the optimized approach is better

The 12-pattern DP is the one you can derive mechanically; the 2-type DP is 70× less work and
fits in two variables. In an interview, derive the first and then look for symmetry.

## Common mistakes

- **Only checking vertical neighbours** and forgetting that the pattern itself must have no equal
  horizontal neighbours.
- **Getting the 3/2/2/2 counts wrong.** Enumerate them once by hand (as above) or by code.
- **Overflow:** `3 · aba` with `aba < 10⁹+7` fits in `long`, but not in `int`.

## Key takeaway

Profile DP: when each row interacts only with the row above, the previous row's pattern is the
state. Then look for **symmetry**: patterns that behave identically can share one state.

## Related problems

- [790. Domino and Tromino Tiling](../790-domino-and-tromino-tiling/), boundary-shape states for tiling
- [1220. Count Vowels Permutation](../1220-count-vowels-permutation/), small fixed state graph
- Previous: [1220. Count Vowels Permutation](../1220-count-vowels-permutation/) · Next: [552. Student Attendance Record II](../552-student-attendance-record-ii/)
