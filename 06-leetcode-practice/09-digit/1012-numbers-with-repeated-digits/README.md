# 1012. Numbers With Repeated Digits

> **Pattern:** Digit DP + complement counting (used-digit bitmask) · **Builds on:** [Digit DP](../../../04-advanced-dp-patterns/11-digit-dp/) · **Difficulty:** Hard

## Problem overview

Given `n` (up to 10⁹), count the integers in `[1, n]` that have **at least one** digit appearing
twice or more (for example 11, 100, 1231).

"At least one repeat" is awkward to track directly: which digit repeated, and has it already
happened? Its opposite, "**all digits distinct**", needs only the set of digits used so far. So we
count the opposite and subtract: this is **complement counting**.

## Link & metadata

| Field | Value |
|---|---|
| LeetCode | [1012. Numbers With Repeated Digits](https://leetcode.com/problems/numbers-with-repeated-digits/) |
| Difficulty | Hard |
| Pattern | Digit DP with tight + started + used-digit mask, answered through the complement |
| Prerequisite topic | [`04-advanced-dp-patterns/11-digit-dp`](../../../04-advanced-dp-patterns/11-digit-dp/), which teaches [2376. Count Special Integers](https://leetcode.com/problems/count-special-integers/) (the distinct-digit count) in full |
| Why this pattern | Counting `x ≤ n` by a property of its digits; the complement property depends on the set of used digits |

## Examples

| n | Answer | Why |
|---|---|---|
| 1 | 0 | nothing repeats |
| 20 | 1 | only 11 |
| 100 | 10 | 11, 22, …, 99 (9 numbers) and 100 |
| 135 | 25 | 135 − 110 distinct-digit numbers (see the dry run) |
| 1000 | 262 | from the problem page |

## Brute-force intuition

For each `x ≤ n`, scan its digits with a `seen` mask. `10⁹ × 10` steps: too slow.

The complement view makes the future simple. If the prefix so far is `3 7 _ _` with distinct
digits, then whether the number ends up distinct depends only on {3, 7} (and on tightness), not on
the order `37` vs `73`. Shared futures → overlapping subproblems.

```
answer(n) = n − distinct(n)        distinct(n) = #{ x in [1, n] : all digits differ }
```

## Deriving the DP (7 questions)

### 1. What is changing?
The position in `n`'s digit string, the **set** of digits used, whether the prefix is **tight**,
and whether the number has **started** (leading zeros are not used digits).

### 2. What does the state represent?
`g(pos, mask, tight, started)` = the number of ways to fill positions `pos … L-1` so that the whole
number is in `[1, n]` and has all-distinct digits, given the used-digit set `mask` (bit `d` set =
digit `d` used) and the two flags.

### 3. What are the choices?
The digit `d` from 0 to `limit` (`n[pos]` if tight, else 9):
- `d = 0` before starting: a leading zero, nothing is used;
- otherwise `d` must not be in `mask`.

### 4. What is the recurrence?

```
g(pos, mask, tight, started) =
      Σ d ≤ limit, leading zero   g(pos+1, mask,          tight && d==limit, false)
    + Σ d ≤ limit, d ∉ mask       g(pos+1, mask | (1<<d), tight && d==limit, true)
```

### 5. What are the base cases?
`g(L, mask, tight, started) = started ? 1 : 0`. The all-zero string is the number 0, outside `[1, n]`.

### 6. What is the order of computation?
`pos + 1` before `pos`. Top-down recursion is the natural way to write it.

### 7. Can the state space be optimized?
Only memoize states that are **not tight and already started**: `memo[pos][mask]`. Tight states
form one path (visited once each), and non-started states have an empty mask, so caching them
gives nothing. Further, in a free (non-tight) state the count only depends on **how many** digits
are used: filling `r` positions from `10 − |mask|` unused digits gives the permutation count
`P(10 − |mask|, r) = (10−|mask|) · (10−|mask|−1) · …`. That turns the DP into a single walk
(Tabulation).

## Recursive solution

```java
int countDistinct(int pos, int mask, boolean tight, boolean started) {
    if (pos == L) return started ? 1 : 0;
    int limit = tight ? bound[pos] : 9, total = 0;
    for (int d = 0; d <= limit; d++) {
        boolean nextTight = tight && d == limit;
        if (!started && d == 0) total += countDistinct(pos + 1, mask, nextTight, false);
        else if ((mask & (1 << d)) == 0) total += countDistinct(pos + 1, mask | (1 << d), nextTight, true);
    }
    return total;
}
// answer = n - countDistinct(0, 0, true, false)
```

## Memoization

`memo[pos][mask]` for `!tight && started`: at most `10 × 1024` cells, each doing 10 digit checks.
See [`solution/Memoization.java`](solution/Memoization.java); `main` compares with brute force for
every `n` up to 3000.

## Tabulation

Precompute `perm[a][b] = a·(a−1)·…·(a−b+1)` (an 11 × 11 table, row by row since
`perm[a][b] = perm[a][b−1] · (a−b+1)`). Then:

1. **Shorter numbers** (length `k < L`): first digit 1..9 (9 ways), the other `k − 1` from the 9
   digits left: `9 · perm[9][k−1]`.
2. **Same length**: walk `n`'s digits. At position `i`, every unused `d < n[i]` (not 0 at `i = 0`)
   drops below `n`; the `L−1−i` positions after it take distinct digits from the `10 − (i+1)`
   still unused: `perm[10−(i+1)][L−1−i]`. Then fix `d = n[i]`; if `n[i]` was already used, stop.
3. If the walk finishes, `n` itself is distinct: `+1`.

See [`solution/Tabulation.java`](solution/Tabulation.java).

## Space optimization

The memo is `O(L · 2¹⁰)`; the permutation walk uses a constant 11 × 11 table and one mask: `O(1)`.

## Dry run

`n = 135`, `L = 3`.

Shorter numbers: `k = 1`: `9 · perm[9][0] = 9`; `k = 2`: `9 · perm[9][1] = 81`. Sum **90**.

Same length:

| i | n[i] | used before | smaller unused digits tried | each adds | added | used after |
|---|---|---|---|---|---|---|
| 0 | 1 | {} | none (0 not allowed first) | — | 0 | {1} |
| 1 | 3 | {1} | 0, 2 | perm[8][1] = 8 | 16 | {1, 3} |
| 2 | 5 | {1, 3} | 0, 2, 4 | perm[7][0] = 1 | 3 | {1, 3, 5} |
| end | | 135 has distinct digits | | | +1 | |

`distinct(135) = 90 + 16 + 3 + 1 = 110`, so the answer is `135 − 110 = 25`.

Check the `i = 1` row in words: numbers `10x` and `12x` with `x` not equal to the earlier digits:
8 choices each → 16 (for example 102, 103, …, 109 and 120, 123, …).

## Complexity

| Approach | Time | Space | Reason |
|---|---|---|---|
| Brute force | O(n · L) | O(1) | every number |
| Memoization | O(L · 2¹⁰ · 10) | O(L · 2¹⁰) | each cached (pos, mask) once, 10 digits each |
| Permutation walk | O(L · 10) | O(1) | one tight path, ≤ 10 digits per position |

## Why the optimized approach is better

Both DP versions are logarithmic in `n`. The permutation walk is the memoized DP with every free
state replaced by its closed form `perm[unused][remaining]`, which is possible precisely because a
free state cares only about the **size** of the mask.

## Common mistakes

- **Counting repeats directly.** You would need to remember "has a repeat happened yet?" *and* the
  mask anyway; the complement is simpler.
- **Including 0.** `[1, n]` excludes 0; the base case returns `started`.
- **Leading zeros in the mask.** `0` written as padding must not block a real `0` later (100 vs 0100).
- **Off-by-one in the remaining count**: after fixing position `i`, there are `i + 1` used digits and
  `L − 1 − i` positions left.

## Key takeaway

If a condition says "**at least one** bad thing", count the "**no** bad thing" numbers and subtract
from the total. The complement often has a much smaller state.

## Related problems

- [2376. Count Special Integers](https://leetcode.com/problems/count-special-integers/): exactly `distinct(n)`; taught in the [Digit DP topic](../../../04-advanced-dp-patterns/11-digit-dp/).
- [357. Count Numbers with Unique Digits](../357-count-numbers-with-unique-digits/): the same mask with the bound `10ⁿ − 1`.
- [902. Numbers At Most N Given Digit Set](../902-numbers-at-most-n-given-digit-set/): the same tight walk with powers instead of permutations.
- [2719. Count of Integers](../2719-count-of-integers/): next step, two bounds and a digit-sum state.
