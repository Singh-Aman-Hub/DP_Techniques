# 357. Count Numbers with Unique Digits

> **Pattern:** Digit DP with a "used digits" bitmask (no tight bound) · **Builds on:** [Digit DP](../../../04-advanced-dp-patterns/11-digit-dp/) · **Difficulty:** Medium

## Problem overview

Given `n` (0 ≤ n ≤ 8), count the integers `x` with `0 ≤ x < 10ⁿ` whose digits are all different.
For example, `120` counts but `121` does not.

This problem isolates the **"which digits have I used?"** state of digit DP without the
complication of a tight bound: the bound `10ⁿ − 1 = 99…9` never forbids any digit. Once you see the
state, it also collapses into a one-line product, which is a good lesson in when a DP becomes
plain combinatorics.

## Link & metadata

| Field | Value |
|---|---|
| LeetCode | [357. Count Numbers with Unique Digits](https://leetcode.com/problems/count-numbers-with-unique-digits/) |
| Difficulty | Medium |
| Pattern | Digit DP over a set of used digits; reduces to a 1D product recurrence |
| Prerequisite topic | [`04-advanced-dp-patterns/11-digit-dp`](../../../04-advanced-dp-patterns/11-digit-dp/), and [bitmask DP](../../../04-advanced-dp-patterns/09-bitmask-dp/) for the mask idea |
| Why this pattern | Counting numbers by a property of their digits; the property "all distinct" needs the set of used digits |

## Examples

| n | Answer | Why |
|---|---|---|
| 0 | 1 | only the number 0 |
| 1 | 10 | 0..9, all single digits are unique |
| 2 | 91 | 100 numbers below 100, minus 11, 22, …, 99 (9 of them) |
| 3 | 739 | 1 + 9 + 81 + 648 (see the dry run) |

## Brute-force intuition

Check every `x < 10ⁿ` with a `seen[10]` array. That is `10ⁿ × n` work: 800 million digit
checks for `n = 8`. Too slow, and wasteful: the numbers `12_ _ _` and `21_ _ _` have the same
**set** of used digits `{1, 2}`, so they have exactly the same number of valid endings. Only the
set matters, not the order. Same future = overlapping subproblems.

## Deriving the DP (7 questions)

### 1. What is changing?
The position being filled, the set of digits used so far, and whether we have written a non-zero
digit yet (leading zeros are not "used").

### 2. What does the state represent?
Write every `x` as exactly `n` digits with leading zeros (`7` → `007` for `n = 3`).
`f(pos, mask, started)` = the number of ways to fill positions `pos … n-1` so that the real digits
of the whole number are distinct, given that the digits in `mask` are already used and `started`
says whether a real (non-leading-zero) digit appeared.

`mask` is a 10-bit integer: bit `d` is 1 when digit `d` is used. That is the bitmask trick from
the bitmask-DP topic: a set of up to 10 small items stored in one `int`.

### 3. What are the choices?
The next digit `d ∈ 0..9`:
- if not started and `d = 0`: another leading zero, the mask is unchanged;
- otherwise: allowed only if bit `d` is not in `mask`; it is added to the mask and `started` becomes true.

### 4. What is the recurrence?

```
f(pos, mask, started) =   [!started]           f(pos+1, mask, false)            ← leading zero
                        + Σ d not in mask,
                          (d ≠ 0 or started)   f(pos+1, mask | (1<<d), true)
```

Sum, because different digits give different numbers.

### 5. What are the base cases?
`f(n, mask, started) = 1`. A complete digit string is one valid number (all zeros is the number 0,
which is allowed because 0 ≤ x).

### 6. What is the order of computation?
`pos` increases in every call, so `pos + 1` must be ready first; recursion handles it, a table
would loop `pos` from `n` down to 0.

### 7. Can the state space be optimized?
Yes, dramatically. Look at what the mask is used for: only **how many** digits it contains, never
**which ones**. A k-digit number with no leading zero has:

```
first digit: 9 choices (1..9)
second:      9 choices (0..9 minus the first)
third:       8 choices
...          the k-th digit has 10 − (k − 1) = 11 − k choices
```

So `exactly[k] = exactly[k−1] × (11 − k)` with `exactly[1] = 9`, and the answer is
`1 + exactly[1] + … + exactly[n]`. The `2¹⁰`-mask DP collapses to one variable.

## Recursive solution

```java
int count(int pos, int mask, int started) {
    if (pos == n) return 1;
    int total = 0;
    for (int d = 0; d <= 9; d++) {
        if (started == 0 && d == 0)        total += count(pos + 1, mask, 0);
        else if ((mask & (1 << d)) == 0)   total += count(pos + 1, mask | (1 << d), 1);
    }
    return total;
}
```

## Memoization

`memo[pos][mask][started]`, with `-1` for "not computed". At most `9 × 1024 × 2` states, each
looping over 10 digits. See [`solution/Memoization.java`](solution/Memoization.java).

## Tabulation

`exactly[k]` from the observation in question 7, filled for `k = 1 … n` left to right, since each
cell uses only the previous one. See [`solution/Tabulation.java`](solution/Tabulation.java).

## Space optimization

`exactly[k]` only reads `exactly[k−1]`, so keep a single running product and a running sum:
[`solution/Optimized.java`](solution/Optimized.java), `O(1)` space.

## Dry run

**Memoization**, `n = 2`:

| Call | What happens | Result |
|---|---|---|
| `f(0, {}, F)` | d = 0 → `f(1, {}, F)`; d = 1..9 → `f(1, {d}, T)` | 10 + 9 × 9 = **91** |
| `f(1, {}, F)` | d = 0 → `f(2)` = 1 (the number 0); d = 1..9 → 1 each | 10 |
| `f(1, {3}, T)` (for example) | any d ≠ 3: 9 digits, each `f(2)` = 1 | 9 |

**Tabulation**, `n = 3`:

| k | choices for the k-th digit | exactly[k] | running answer |
|---|---|---|---|
| — | the number 0 | — | 1 |
| 1 | 9 (1..9) | 9 | 10 |
| 2 | 9 (11 − 2) | 81 | 91 |
| 3 | 8 (11 − 3) | 648 | **739** |

## Complexity

| Approach | Time | Space | Reason |
|---|---|---|---|
| Brute force | O(10ⁿ · n) | O(1) | every number, every digit |
| Memoization | O(n · 2¹⁰ · 2 · 10) | O(n · 2¹⁰ · 2) | each (pos, mask, started) once, 10 digits each |
| Tabulation | O(n) | O(n) | one multiplication per length |
| Optimized | O(n) | O(1) | running product |

For `n ≥ 11` every number has a repeated digit (pigeonhole: only 10 digits), so `exactly[k] = 0`
from `k = 11`: the formula handles it because the factor `11 − 11 = 0` appears.

## Why the optimized approach is better

The mask DP is general: it still works when there is an upper bound like "≤ 4729" (that is
[2376. Count Special Integers](https://leetcode.com/problems/count-special-integers/)). But when the
bound is all 9s, the count of endings depends only on the **size** of the mask, so a product
formula replaces 20 000 states.

## Common mistakes

- **Marking leading zeros as used.** Then `102` (padded `0102` for n = 4) would be rejected because
  0 appears "twice". That is why the `started` flag exists.
- **Forgetting the number 0** (answer for `n = 0` is 1, not 0).
- **Using `10 − k` instead of `11 − k`.** The k-th digit avoids the `k − 1` digits before it.

## Key takeaway

When the state contains a set, ask whether the future depends on **which** elements are in it or
only on **how many**. If only the count matters, the set collapses to a number.

## Related problems

- [1012. Numbers With Repeated Digits](../1012-numbers-with-repeated-digits/): the same mask with a real upper bound, plus complement counting.
- [2376. Count Special Integers](https://leetcode.com/problems/count-special-integers/): taught in full in the [Digit DP topic](../../../04-advanced-dp-patterns/11-digit-dp/).
- [788. Rotated Digits](../788-rotated-digits/): a simpler digit property (one flag instead of a mask).
