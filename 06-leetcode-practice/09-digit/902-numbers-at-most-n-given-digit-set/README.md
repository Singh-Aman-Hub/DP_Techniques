# 902. Numbers At Most N Given Digit Set

> **Pattern:** Digit DP with tight + "started" (leading-zero) flags · **Builds on:** [Digit DP](../../../04-advanced-dp-patterns/11-digit-dp/) · **Difficulty:** Hard

## Problem overview

You get a sorted set of digits (a subset of `'1'…'9'`, so never 0) and an integer `n` (up to 10⁹).
Using those digits as often as you like, how many **positive** integers `≤ n` can you write?

The difficulty is not the counting itself but the fact that the numbers have **different
lengths**. A 2-digit number is always below a 3-digit `n`; a 3-digit one may or may not be. Digit
DP handles this with a "have I started writing yet?" flag.

## Link & metadata

| Field | Value |
|---|---|
| LeetCode | [902. Numbers At Most N Given Digit Set](https://leetcode.com/problems/numbers-at-most-n-given-digit-set/) |
| Difficulty | Hard |
| Pattern | Digit DP: restricted alphabet, upper bound, variable length |
| Prerequisite topic | [`04-advanced-dp-patterns/11-digit-dp`](../../../04-advanced-dp-patterns/11-digit-dp/) |
| Why this pattern | Count numbers `≤ n` built digit by digit, where each position's choices depend on whether the prefix still equals `n`'s prefix |

## Examples

| digits | n | Answer | Why |
|---|---|---|---|
| {1,3,5,7} | 100 | 20 | 4 one-digit + 16 two-digit; every 3-digit number from the set is > 100 |
| {1,4,9} | 1 000 000 000 | 29523 | 3 + 9 + … + 3⁹; no 10-digit number fits |
| {7} | 8 | 1 | just 7 |
| {9} | 8 | 0 | 9 > 8 |
| {1,3,5,7} | 357 | 48 | worked out in the dry run |

## Brute-force intuition

Generate every number from the digit set in increasing length and stop when it exceeds `n`. With
`D` digits and `L` positions that is about `Dᴸ` numbers: for `D = 9, L = 9` almost 400 million.

Notice what the generator really decides at each position: "is my prefix still equal to `n`'s
prefix, or already smaller?" Once it is smaller, the remaining positions can be **anything**, and
there are simply `D^(remaining)` ways. Counting instead of listing is the whole speed-up.

## Deriving the DP (7 questions)

### 1. What is changing?
The position, whether the prefix is **tight** (equal to `n`'s prefix), and whether we have
**started** the number (a shorter number is a longer one with "blank" leading positions).

### 2. What does the state represent?
`f(pos, tight, started)` = the number of ways to fill positions `pos … L-1` (where `L` = number of
digits of `n`) so that the finished number is a positive integer `≤ n` using only the allowed
digits.

### 3. What are the choices?
- If not started: leave this position **blank** (the number will be shorter). A blank prefix is
  automatically smaller than `n`, so `tight` becomes false.
- Place an allowed digit `d ≤ limit`, where `limit = n[pos]` if tight, else 9.

### 4. What is the recurrence?

```
f(pos, tight, started) = [!started] · f(pos+1, false, false)
                       + Σ allowed d ≤ limit  f(pos+1, tight && d == limit, true)
```

### 5. What are the base cases?
`f(L, tight, started) = started ? 1 : 0`. All blank is the empty "number", which is not positive.

### 6. What is the order of computation?
`pos + 1` before `pos`: recursion top-down, or a loop from right to left bottom-up.

### 7. Can the state space be optimized?
Yes. Two of the states have closed forms:
- `f(pos, tight=false, started=true) = D^(L-pos)`: every remaining position is free.
- The "not started" chain adds exactly the shorter numbers: `D¹ + D² + … + D^(L-1)`.

Only the **tight, started** path needs a real table, one cell per position:

```
tightWays[L] = 1
tightWays[i] = (# allowed digits < n[i]) · D^(L-1-i)  +  (n[i] allowed ? tightWays[i+1] : 0)
answer       = (D¹ + … + D^(L-1)) + tightWays[0]
```

## Recursive solution

```java
long count(int pos, int tight, int started) {
    if (pos == L) return started;
    long total = 0;
    if (started == 0) total += count(pos + 1, 0, 0);          // blank → shorter number
    int limit = tight == 1 ? bound[pos] : 9;
    for (int d : allowed) if (d <= limit)
        total += count(pos + 1, (tight == 1 && d == limit) ? 1 : 0, 1);
    return total;
}
```

## Memoization

`memo[pos][tight][started]`: only `4L` cells. The cache matters for the non-tight, started
states, which are reached from every smaller prefix. See
[`solution/Memoization.java`](solution/Memoization.java); its `main` compares with brute force for
the set {2, 3, 8} and every `n` from 1 to 3000.

## Tabulation

The right-to-left `tightWays` table from question 7. Position `i` needs position `i + 1`, so the
loop runs from `L − 1` down to 0. See [`solution/Tabulation.java`](solution/Tabulation.java).

## Space optimization

`tightWays[i]` only reads `tightWays[i+1]`, so one variable would do. The table is `L ≤ 10` cells,
so the solution keeps it for readability.

## Dry run

digits = {1, 3, 5, 7} (`D = 4`), `n = 357` (`L = 3`).

Shorter numbers: `4¹ + 4² = 4 + 16 = 20`.

Tight table, right to left:

| i | n[i] | allowed digits < n[i] | × 4^(L-1-i) | n[i] allowed? | tightWays[i] |
|---|---|---|---|---|---|
| 3 | — | — | — | — | 1 (base) |
| 2 | 7 | 1, 3, 5 → 3 | × 1 = 3 | yes, + tightWays[3] = 1 | **4** (351, 353, 355, 357) |
| 1 | 5 | 1, 3 → 2 | × 4 = 8 | yes, + tightWays[2] = 4 | **12** |
| 0 | 3 | 1 → 1 | × 16 = 16 | yes, + tightWays[1] = 12 | **28** |

Answer = 20 + 28 = **48**.

Reading row 0 in words: "start with 1 and anything follows (16 numbers), or start with 3 and stay
tight (12 more)".

## Complexity

| Approach | Time | Space | Reason |
|---|---|---|---|
| Generate all | O(Dᴸ) | O(L) | lists every candidate |
| Memoization | O(L · 4 · D) | O(L · 4) | 4 states per position, D digits each |
| Tabulation | O(L · D) | O(L) | one cell per position, scan the set once |

`L ≤ 10` and `D ≤ 9`: about 90 steps instead of hundreds of millions.

## Why the optimized approach is better

Counting free suffixes with a power (`Dʳ`) instead of enumerating them is the core of every digit
DP. The tabulated form also shows **why** digit DP is linear in the number of digits: there is
exactly one tight path, and everything that falls off it is counted in one multiplication.

## Common mistakes

- **Counting the empty number.** The base case must return `started`, not 1.
- **Forgetting shorter numbers.** If you only build `L`-digit numbers, `n = 100` with {1,3,5,7}
  would give 0 instead of 20.
- **Continuing the tight path when `n[i]` is not allowed.** Then no number can stay equal to `n`'s
  prefix; the `+ tightWays[i+1]` term must be 0.
- **Using `d < limit` for "still tight".** Tight continues only when `d == limit`.

## Key takeaway

Two flags cover nearly every digit DP: `tight` (am I still glued to the bound?) and `started` (have
I written a real digit yet?). Anything off the tight path is counted with a formula.

## Related problems

- [788. Rotated Digits](../788-rotated-digits/): no `started` flag needed because 0 is harmless there.
- [600. Non-negative Integers without Consecutive Ones](../600-non-negative-integers-without-consecutive-ones/): the same tight walk in base 2.
- [1012. Numbers With Repeated Digits](../1012-numbers-with-repeated-digits/): `started` matters again because a leading 0 must not count as a used digit.
- [233. Number of Digit One](https://leetcode.com/problems/number-of-digit-one/): taught in the [Digit DP topic](../../../04-advanced-dp-patterns/11-digit-dp/).
