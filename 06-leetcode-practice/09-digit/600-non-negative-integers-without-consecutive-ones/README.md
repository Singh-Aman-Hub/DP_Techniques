# 600. Non-negative Integers without Consecutive Ones

> **Pattern:** Digit DP in base 2 (tight + previous-bit state) · **Builds on:** [Digit DP](../../../04-advanced-dp-patterns/11-digit-dp/) · **Difficulty:** Hard

## Problem overview

Given a positive integer `n` (up to 10⁹), count the integers `x` with `0 ≤ x ≤ n` whose binary
representation never has two 1-bits next to each other. `5 = 101₂` is fine, `6 = 110₂` is not.

Digit DP is not tied to base 10. Here the "digits" are bits, the per-position rule looks at the
previous bit, and the counts of free bit strings turn out to be Fibonacci numbers.

## Link & metadata

| Field | Value |
|---|---|
| LeetCode | [600. Non-negative Integers without Consecutive Ones](https://leetcode.com/problems/non-negative-integers-without-consecutive-ones/) |
| Difficulty | Hard |
| Pattern | Digit DP over bits; the free-suffix count is a Fibonacci-style 1D DP |
| Prerequisite topic | [`04-advanced-dp-patterns/11-digit-dp`](../../../04-advanced-dp-patterns/11-digit-dp/); the free count reuses [Fibonacci](../../../01-one-dimensional-dp/01-fibonacci-number/) |
| Why this pattern | "Count x ≤ n whose digits (bits) avoid a local pattern" |

## Examples

| n | binary | Answer | Valid numbers |
|---|---|---|---|
| 1 | 1 | 2 | 0, 1 |
| 5 | 101 | 5 | 0, 1, 2, 4, 5 (3 = 11₂ fails) |
| 8 | 1000 | 6 | 0, 1, 2, 4, 5, 8 |
| 13 | 1101 | 8 | 0, 1, 2, 4, 5, 8, 9, 10 |

## Brute-force intuition

`x & (x >> 1)` is non-zero exactly when two adjacent bits are both 1 (shift the number by one and
the overlapping 1s line up). Looping `x` from 0 to `n` is one billion iterations for `n = 10⁹`: too
slow.

Building the number bit by bit instead, the only thing the remaining bits care about is
**the last bit we placed** (could the next one be 1?) and **whether we are still equal to n's
prefix**. Many different prefixes share that summary.

## Deriving the DP (7 questions)

### 1. What is changing?
The bit position (from the most significant bit of `n` downwards), the previous bit, and the
tight flag.

### 2. What does the state represent?
`f(pos, prevBit, tight)` = the number of ways to choose bits `pos … B-1` (with `B` the bit length
of `n`) so that the whole number is `≤ n` and has no "11", given the previous bit and whether the
prefix still equals `n`'s prefix.

### 3. What are the choices?
Place `0` (always allowed), or place `1` if `prevBit = 0` and (not tight, or `n` has a 1 here).

### 4. What is the recurrence?

```
f(pos, prev, tight) = f(pos+1, 0, tight && n[pos] == 0)
                    + [prev == 0 and (!tight or n[pos] == 1)] · f(pos+1, 1, tight)
```

### 5. What are the base cases?
`f(B, prev, tight) = 1`: all bits placed and no "11" was ever created. Leading zeros are fine (they
never create "11"), so `0` itself is counted, as the problem wants.

### 6. What is the order of computation?
Each call moves to `pos + 1`. Top-down recursion, or bottom-up from the last bit.

### 7. Can the state space be optimized?
Yes. A non-tight state depends only on how many bits are left, `k`, and on the previous bit. Let
`free[k]` be the number of `k`-bit strings without "11" (leading zeros allowed):

```
a valid string starts with 0 → followed by any free[k-1] string
                 or with 10  → followed by any free[k-2] string
free[k] = free[k-1] + free[k-2],   free[0] = 1, free[1] = 2   (Fibonacci!)
```

Then walk `n`'s bits from the top. At each 1-bit with `i` bits below it, placing a **0** there
instead makes the number smaller than `n`, and the `i` lower bits are free: add `free[i]`. (The bit
after a placed 0 may be anything, which is exactly what `free[i]` counts.) If `n` itself contains
"11", stop at the second 1: no tight number can continue. If the walk survives, add 1 for `n`.

## Recursive solution

```java
int count(int pos, int prevBit, int tight) {
    if (pos == B) return 1;
    int limit = tight == 1 ? bits[pos] : 1, total = 0;
    for (int bit = 0; bit <= limit; bit++) {
        if (bit == 1 && prevBit == 1) continue;
        total += count(pos + 1, bit, (tight == 1 && bit == limit) ? 1 : 0);
    }
    return total;
}
```

## Memoization

`memo[pos][prevBit][tight]`: `31 × 2 × 2` cells. See
[`solution/Memoization.java`](solution/Memoization.java); its `main` compares against
`x & (x >> 1)` brute force for every `n` up to 5000.

## Tabulation

Fill `free[0..31]` left to right (Fibonacci), then do the single bit walk described above. See
[`solution/Tabulation.java`](solution/Tabulation.java).

## Space optimization

`free[k]` needs only the two previous values, but the walk reads `free[i]` for decreasing `i`, so
keeping the 32-entry table is simpler than recomputing. It is constant size either way.

## Dry run

`free[] = 1, 2, 3, 5, 8, …`

**n = 13 = 1101₂** (bits at positions 3, 2, 1, 0):

| bit position i | n's bit | action | added | total | previous bit |
|---|---|---|---|---|---|
| 3 | 1 | put 0 here → 3 free bits: `0xxx` | free[3] = 5 | 5 | 1 |
| 2 | 1 | put 0 here → `10xx`: free[2] = 3; previous bit was 1 → `11` in n, **stop** | 3 | **8** | — |

The 5 numbers `0xxx` are 0, 1, 2, 4, 5; the 3 numbers `10xx` are 8, 9, 10. Total 8 ✓.

**n = 5 = 101₂**:

| i | n's bit | added | total | previous bit |
|---|---|---|---|---|
| 2 | 1 | free[2] = 3 (`0xx`: 0, 1, 2) | 3 | 1 |
| 1 | 0 | — | 3 | 0 |
| 0 | 1 | free[0] = 1 (`100` = 4) | 4 | 1 |
| end | no "11" in n | +1 (5 itself) | **5** | |

## Complexity

| Approach | Time | Space | Reason |
|---|---|---|---|
| Brute force | O(n) | O(1) | every number |
| Memoization | O(B · 4 · 2) | O(B · 4) | B ≤ 31 bits, 4 states, 2 choices |
| Tabulation | O(B) | O(B) | Fibonacci table + one walk |

## Why the optimized approach is better

`O(log n)` instead of `O(n)`: about 31 steps instead of a billion. The tabulated version also
reveals the structure: the answer is a sum of Fibonacci numbers picked by the 1-bits of `n`
(a cousin of the Zeckendorf representation).

## Common mistakes

- **Not stopping when `n` has "11".** Without the `return`, the walk would keep adding counts for
  prefixes that already contain "11".
- **Adding `free[i]` at 0-bits.** Placing a smaller bit is only possible where `n` has a 1.
- **Forgetting `+1` for `n` itself** (when `n` is valid), or forgetting that 0 is counted.
- **Thinking the 0 we place at a 1-bit restricts the next bit.** It does not: after a 0, the next
  bit may be 0 or 1, which is why the count is plain `free[i]`.

## Key takeaway

Digit DP works in any base. When the rule is local (depends on the previous digit), the state is
`(position, previous digit, tight)`, and the free-suffix count is usually a tiny linear recurrence.

## Related problems

- [788. Rotated Digits](../788-rotated-digits/): the same walk with a closed-form free count in base 10.
- [902. Numbers At Most N Given Digit Set](../902-numbers-at-most-n-given-digit-set/): free counts are powers instead of Fibonacci numbers.
- [70. Climbing Stairs](https://leetcode.com/problems/climbing-stairs/): the same Fibonacci recurrence as `free[k]`.
