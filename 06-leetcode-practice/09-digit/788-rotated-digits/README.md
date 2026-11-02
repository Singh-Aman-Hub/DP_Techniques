# 788. Rotated Digits

> **Pattern:** Digit DP (tight flag + one property flag) · **Builds on:** [Digit DP](../../../04-advanced-dp-patterns/11-digit-dp/) · **Difficulty:** Medium

## Problem overview

Turn every digit of a number upside down (rotate it 180°). Digits `0, 1, 8` stay the same,
`2 ↔ 5` and `6 ↔ 9` swap, and `3, 4, 7` become garbage. A number is **good** if every digit
survives the rotation **and** the result is a different number. Count the good numbers in `[1, n]`
(`n ≤ 10⁴`).

This is the friendliest digit-DP problem: one bound, no leading-zero trouble, and only one extra
fact to remember about the prefix ("did anything change yet?").

## Link & metadata

| Field | Value |
|---|---|
| LeetCode | [788. Rotated Digits](https://leetcode.com/problems/rotated-digits/) |
| Difficulty | Medium |
| Pattern | Digit DP: count numbers `≤ n` whose digits satisfy a per-digit rule |
| Prerequisite topic | [`04-advanced-dp-patterns/11-digit-dp`](../../../04-advanced-dp-patterns/11-digit-dp/) |
| Why this pattern | "Count x in [1, n] such that its **digits** have a property" is the exact signature of digit DP |

## Examples

| n | Answer | Why |
|---|---|---|
| 10 | 4 | 2, 5, 6, 9. (1 and 8 and 10 rotate to themselves.) |
| 1 | 0 | 1 rotates to 1: valid but not different |
| 25 | 12 | 2, 5, 6, 9, 12, 15, 16, 19, 20, 21, 22, 25 |
| 857 | 247 | from the problem page |

Classify each digit once; everything else follows from this table:

```
digit :  0  1  2  3  4  5  6  7  8  9
class :  S  S  C  X  X  C  C  X  S  C        S = same, C = changes, X = invalid
```

A number is good ⇔ it has **no X** and **at least one C**.

## Brute-force intuition

Loop `x` from 1 to `n`, look at every digit, keep two booleans (`valid`, `changed`). That is
`O(n · digits)`, and for `n ≤ 10⁴` it is honestly fast enough. So why DP?

Because the same question with `n ≤ 10¹⁸` would kill the loop, and the interview follow-up is
always "what if n is huge?". The trick that scales is to stop looking at **numbers** and look at
**digit positions**: a number below `n` is a choice of digit at each position. Two numbers whose
prefixes share the same "summary" (still tight? changed yet?) have **exactly the same set of
possible endings**, so the count of good endings is a shared subproblem. That is overlapping
subproblems, the DP signal.

## Deriving the DP (7 questions)

### 1. What is changing?
The **position** we are filling (left to right), whether the prefix is still **tight** (equal to
`n`'s prefix), and whether the prefix already **changed** (contains a C digit).

### 2. What does the state represent?
`f(pos, tight, changed)` = the number of ways to choose digits for positions `pos … L-1` so that
the full number is `≤ n` and good, given the prefix summary `(tight, changed)`.

Why these flags and nothing else? The future needs to know (a) how high the next digit may go,
which is exactly `tight`, and (b) whether the goodness condition is already satisfied, which is exactly
`changed`. The prefix never contains X: we refuse X digits immediately.

### 3. What are the choices?
The next digit `d`, from `0` to `limit`, where `limit = n[pos]` if tight, else `9`.
Skip `d ∈ {3, 4, 7}`.

### 4. What is the recurrence?

```
f(pos, tight, changed) = Σ over allowed d of
        f(pos + 1,  tight && d == limit,  changed || d ∈ {2,5,6,9})
```

We **add** because different digits at this position produce different numbers: the counts are
disjoint.

### 5. What are the base cases?
`f(L, tight, changed) = changed ? 1 : 0`. All positions are filled; the number is good only if
something changed. Leading zeros need no special state: `0` is an S digit, so a padded `0025`
behaves like `25`, and the all-zero number `0` is never counted because it never changed.

### 6. What is the order of computation?
`f(pos, …)` needs `f(pos + 1, …)`. Top-down handles it naturally; bottom-up would go from
`pos = L` down to `0`.

### 7. Can the state space be optimized?
There are only `L × 2 × 2` states, already tiny. The real improvement is noticing that **non-tight**
states have a closed form: with `r` free positions, there are `7ʳ` valid suffixes and `3ʳ` of them
use only S digits, so

```
f(pos, tight=0, changed=1) = 7ʳ
f(pos, tight=0, changed=0) = 7ʳ − 3ʳ
```

Only the single tight path along `n`'s digits needs to be walked (see `Optimized.java`).

## Recursive solution

```java
int count(int pos, int tight, int changed) {
    if (pos == L) return changed;
    int limit = tight == 1 ? digitsOfN[pos] : 9, total = 0;
    for (int d = 0; d <= limit; d++) {
        if (d == 3 || d == 4 || d == 7) continue;
        total += count(pos + 1, (tight == 1 && d == limit) ? 1 : 0,
                       (changed == 1 || d == 2 || d == 5 || d == 6 || d == 9) ? 1 : 0);
    }
    return total;
}
```

Without a cache this explores every number from 0 to `n`, one leaf per number: the brute force in
disguise.

## Memoization

Cache `memo[pos][tight][changed]`. The tight state is visited only once per position (there is
exactly one tight prefix), but the two non-tight states at each position are reached from many
prefixes and are computed only once. See [`solution/Memoization.java`](solution/Memoization.java),
whose `main` also compares the DP against brute force for every `n` from 1 to 2000.

## Tabulation

Two different bottom-up views, both in the solution folder:

1. **Table over numbers** ([`Tabulation.java`](solution/Tabulation.java)). `kind[x]` ∈
   {INVALID, SAME, GOOD} depends only on `kind[x / 10]` and `kind[x % 10]`, both smaller than `x`.
   A simple `for x = 0..n` loop fills it. `O(n)`, a nice 1D DP, but it does not scale to huge `n`.
2. **Digit walk with the closed form** ([`Optimized.java`](solution/Optimized.java)). This is the
   digit DP with the non-tight states replaced by `7ʳ` and `7ʳ − 3ʳ`.

## Space optimization

The memo has 4 cells per position, so there is nothing to shrink. The closed-form walk keeps only
`total` and the `prefixChanged` flag: `O(1)` extra space.

## Dry run

`n = 25`, digits `[2, 5]`, `L = 2`.

**Memoization**, in call order:

| Call | Digits tried | Result | Note |
|---|---|---|---|
| `f(0, T, 0)` | 0, 1, 2 | 4 + 4 + 4 = **12** | limit = 2 |
| ↳ `f(1, F, 0)` via d = 0 | 0,1,2,5,6,8,9 → changes for 2,5,6,9 | 4 | computed |
| ↳ `f(1, F, 0)` via d = 1 | | 4 | **cache hit** |
| ↳ `f(1, T, 1)` via d = 2 | 0, 1, 2, 5 (limit 5) | 4 | each leaf has changed = 1 |

**Closed-form walk**:

| pos | n's digit | smaller allowed digits | added | running total | prefixChanged after |
|---|---|---|---|---|---|
| 0 | 2 | 0 (S), 1 (S), r = 1 | (7−3) + (7−3) = 8 | 8 | true (2 is C) |
| 1 | 5 | 0, 1, 2, r = 0 (3, 4 skipped) | 1 + 1 + 1 = 3 | 11 | true |
| end | — | n itself is good | +1 | **12** | |

Both agree with the list in the Examples table.

## Complexity

| Approach | Time | Space | Reason |
|---|---|---|---|
| Brute force | O(n · L) | O(1) | every number, every digit |
| Table over numbers | O(n) | O(n) | one O(1) combine per number |
| Memoized digit DP | O(L · 4 · 10) | O(L · 4) | 4 states per position, 10 digits tried each |
| Closed-form walk | O(L · 10) | O(1) | one tight path, ≤ 10 smaller digits per position |

`L` is the number of digits of `n` (5 here), so the digit DP does about 200 steps, regardless of
how big `n` is in value.

## Why the optimized approach is better

The brute force and the `O(n)` table grow with the **value** of `n`; the digit DP grows with the
**number of digits**, i.e. `log₁₀ n`. For `n = 10¹⁸` that is 19 positions versus 10¹⁸ loop steps.

## Common mistakes

- **Counting 0.** The base case returns `changed`, so `0` (never changed) is excluded automatically.
  Returning `1` there would also count 0 and every "same" number.
- **Forgetting that tight stays tight only on `d == limit`.** Using `d == n[pos]` while not tight is
  wrong: once free, always free.
- **Treating 1 and 8 as changing digits.** They are valid but rotate to themselves.
- **Memoizing without the `tight` dimension.** The tight and non-tight subproblems have different
  answers at the same position.

## Key takeaway

Digit DP asks: *what is the smallest summary of a prefix that the rest of the number needs?*
Here it is `(tight, changed)`. The invalid-digit rule is enforced by simply not taking those
digits, so it never needs a flag.

## Related problems

- [357. Count Numbers with Unique Digits](../357-count-numbers-with-unique-digits/): the summary becomes a set of used digits.
- [902. Numbers At Most N Given Digit Set](../902-numbers-at-most-n-given-digit-set/): restricted digit alphabet plus leading-zero handling.
- [600. Non-negative Integers without Consecutive Ones](../600-non-negative-integers-without-consecutive-ones/): the same idea in base 2.
- [233. Number of Digit One](https://leetcode.com/problems/number-of-digit-one/): taught in the [Digit DP topic](../../../04-advanced-dp-patterns/11-digit-dp/).
