# 2719. Count of Integers

> **Pattern:** Digit DP with two bounds and a digit-sum state · **Builds on:** [Digit DP](../../../04-advanced-dp-patterns/11-digit-dp/) · **Difficulty:** Hard

## Problem overview

You get two numbers as strings, `num1 ≤ num2` (up to 10²², far beyond `long`), and a range
`[minSum, maxSum]` (up to 400). Count the integers `x` with `num1 ≤ x ≤ num2` whose **digit sum**
lies in `[minSum, maxSum]`. The answer is returned modulo 10⁹ + 7.

This is the "final exam" of the digit-DP sub-folder: there is a **lower** bound as well as an upper
one, and the state carries a **number** (the running sum) instead of a flag or a set.

## Link & metadata

| Field | Value |
|---|---|
| LeetCode | [2719. Count of Integers](https://leetcode.com/problems/count-of-integers/) |
| Difficulty | Hard |
| Pattern | Digit DP: `(pos, sum, tightLow, tightHigh)`, or `count(≤ hi) − count(≤ lo) + [lo is good]` |
| Prerequisite topic | [`04-advanced-dp-patterns/11-digit-dp`](../../../04-advanced-dp-patterns/11-digit-dp/) |
| Why this pattern | Count integers in a range by a property of their digits; the inputs are too large to loop over |

## Examples

| num1 | num2 | minSum | maxSum | Answer | Why |
|---|---|---|---|---|---|
| 1 | 12 | 1 | 8 | 11 | 1..12 except 9 (sum 9) |
| 1 | 5 | 1 | 5 | 5 | 1, 2, 3, 4, 5 |
| 10 | 25 | 3 | 5 | 6 | 12, 13, 14, 21, 22, 23 |
| 7 | 7 | 1 | 1 | 0 | the only number has sum 7 |
| 1 | 10²² | 1 | 400 | 490000 | every x qualifies (max sum is 9·22 = 198), and 10²² mod (10⁹+7) = 490000 |

## Brute-force intuition

Loop from `num1` to `num2` and sum digits: up to 10²² iterations, hopeless. Also, the numbers do not
fit in a `long`, which is a strong hint to work **on the digit string**.

What does the rest of the number need to know about a prefix? Only the **sum so far** (to judge the
final sum) and whether the prefix is still glued to either bound. Two prefixes like `13…` and `31…`
have the same sum, so (once free) they have the same number of valid endings.

## Deriving the DP (7 questions)

### 1. What is changing?
The position, the running digit sum, and two tightness flags: one for each bound.

### 2. What does the state represent?
Pad `num1` with leading zeros to the length `L` of `num2`. Then
`f(pos, sum, tightLow, tightHigh)` = the number of ways to fill positions `pos … L-1` so that the
whole number is in `[num1, num2]` and its digit sum is in `[minSum, maxSum]`, given the sum of
the digits already placed and whether the prefix still equals `num1`'s / `num2`'s prefix.

### 3. What are the choices?
A digit `d` from `from` to `to`, where `from = num1[pos]` if tightLow else 0, and
`to = num2[pos]` if tightHigh else 9.

### 4. What is the recurrence?

```
f(pos, sum, tL, tH) = Σ d = from..to   f(pos+1, sum + d, tL && d == from, tH && d == to)
```

Leading zeros need no `started` flag here: a 0 adds nothing to the sum, so `007` and `7` behave the
same.

### 5. What are the base cases?
- `sum > maxSum` → 0 (digits are never negative, so the sum can only grow: prune early).
- `pos == L` → 1 if `sum ≥ minSum`, else 0.

### 6. What is the order of computation?
`pos + 1` before `pos`. Top-down recursion is the easiest to write.

### 7. Can the state space be optimized?
The sum dimension is at most `maxSum + 1 ≤ 401`, positions `≤ 23`, flags `2 × 2`: about 37 000
cells. A second formulation removes the lower bound entirely:

```
answer = countAtMost(num2) − countAtMost(num1) + (num1 is good ? 1 : 0)
```

(we add `num1` back instead of computing `num1 − 1` on a string). `countAtMost(X)` is a one-bound
walk that uses a free-suffix table:

```
free[r][s] = number of r-digit strings (leading zeros allowed) with digit sum exactly s
free[0][0] = 1,   free[r][s] = Σ d = 0..9, d ≤ s   free[r−1][s−d]
```

## Recursive solution

```java
int solve(int pos, int sum, int tL, int tH) {
    if (sum > maxSum) return 0;
    if (pos == L) return sum >= minSum ? 1 : 0;
    int from = tL == 1 ? low[pos] : 0, to = tH == 1 ? high[pos] : 9;
    long total = 0;
    for (int d = from; d <= to; d++)
        total += solve(pos + 1, sum + d, (tL == 1 && d == from) ? 1 : 0, (tH == 1 && d == to) ? 1 : 0);
    return (int) (total % MOD);
}
```

## Memoization

`memo[pos][sum][tL][tH]` with `-1` as "unknown". See
[`solution/Memoization.java`](solution/Memoization.java). Its `main` also compares against a brute
force on more than 400 small ranges, and includes the 23-digit example.

## Tabulation

Build `free[r][s]` row by row (row `r` needs only row `r − 1`). Then `countAtMost(X)`: at each
position `i`, every digit `d < X[i]` makes the number smaller, so the `r = L−1−i` digits after it
are free and must have a sum in `[minSum − prefixSum − d, maxSum − prefixSum − d]`; add the matching
`free[r][s]` entries. Then fix `X[i]` and continue. See
[`solution/Tabulation.java`](solution/Tabulation.java).

## Space optimization

`free[r]` only reads `free[r−1]`, but the walk later reads every row, so the full `L × S` table is
kept (about 9 000 longs). The memo version could drop the `tightLow/tightHigh` dimensions from the
cache (only the free state is reused), but at this size it is not worth the complexity.

## Dry run

`num1 = "1"`, `num2 = "12"`, sums `[1, 8]`.

**Two-bound memoization** (`low = "01"`, `high = "12"`):

| Call | from..to | Children | Result |
|---|---|---|---|
| `f(0, 0, T, T)` | 0..1 | d=0 → `f(1, 0, T, F)`; d=1 → `f(1, 1, F, T)` | 8 + 3 = **11** |
| `f(1, 0, T, F)` | 1..9 (low digit is 1) | x = 1..9, sums 1..9, sum 9 fails | 8 |
| `f(1, 1, F, T)` | 0..2 (high digit is 2) | x = 10, 11, 12, sums 1, 2, 3 | 3 |

**Prefix-count difference**:

| Step | Detail | Value |
|---|---|---|
| `countAtMost("12")`, i = 0 | d = 0, 1 free digit, need sum 1..8 → `free[1][1..8]` | 8 |
| i = 1 (prefix sum 1) | d = 0 → 10 (sum 1); d = 1 → 11 (sum 2), r = 0 | 2 |
| end | 12 itself, sum 3 | 1 → total 11 |
| `countAtMost("1")` | d = 0 → only the empty suffix, sum 0 < 1; then "1" itself | 1 |
| combine | 11 − 1 + (1 is good: +1) | **11** |

## Complexity

| Approach | Time | Space | Reason |
|---|---|---|---|
| Brute force | O(num2 − num1) | O(1) | astronomically large |
| Memoization | O(L · S · 4 · 10) | O(L · S · 4) | each state once, 10 digits each; L ≤ 23, S ≤ 401 |
| Tabulation | O(L · S · 10) | O(L · S) | building `free`; each walk is O(L · 10 · S) for the range sums |

Roughly 370 000 steps, no matter how large the numbers are.

## Why the optimized approach is better

The cost depends on the **length** of the numbers and the **range of sums**, never on their value.
The two-bound state is worth learning: it handles any `[lo, hi]` range in one pass, which avoids
the "subtract one from a 23-digit string" chore.

## Common mistakes

- **Computing `count(num2) − count(num1)`** and forgetting that `num1` itself may be good.
- **Forgetting the modulo** inside the sum (or subtracting and getting a negative number: add `MOD`
  before the final `%`).
- **Not padding `num1`** in the two-bound version: positions must line up.
- **Resetting `tightLow` wrongly**: it stays true only when `d == from` (num1's digit), not when
  `d == to`.

## Key takeaway

A range `[lo, hi]` in digit DP is handled either by **two tight flags** in one pass, or by
`F(hi) − F(lo) + [lo is good]`. A numeric property such as a digit sum becomes a bounded integer
dimension of the state.

## Related problems

- [1012. Numbers With Repeated Digits](../1012-numbers-with-repeated-digits/): one bound, set-valued state.
- [902. Numbers At Most N Given Digit Set](../902-numbers-at-most-n-given-digit-set/): the one-bound walk with a free-suffix count.
- [233. Number of Digit One](https://leetcode.com/problems/number-of-digit-one/): a counting DP where each state returns a sum, taught in the [Digit DP topic](../../../04-advanced-dp-patterns/11-digit-dp/).
