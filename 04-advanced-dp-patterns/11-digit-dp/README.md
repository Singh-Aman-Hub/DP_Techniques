# Digit DP

> **Module:** 04 — Advanced DP Patterns · **Difficulty:** Advanced · **Estimated time:** 3–4 hours

## 1. What is this topic?

Digit DP counts the integers in a range `[L, R]` that have some property decided by their digits: a digit sum, which digits appear, how adjacent digits relate. Instead of visiting every number, it builds numbers **digit by digit** from the left, with a small state that summarises the prefix written so far, plus a `tight` flag that stops it from going above the limit.

## 2. Why does this concept exist?

R is often 10⁹, 10¹⁸, or a number with thousands of digits. Looping over every number is impossible. Many numbers share the same "situation" after a few digits (same position, same digit sum mod D, already below the limit), so their completions can be counted once for all of them. The work then depends on the **number of digits** (about log R), not on R itself.

## 3. What problem does it solve?

- "How many numbers ≤ N / in `[L, R]` satisfy P?" for huge N.
- "What is the sum of f(x) over all x ≤ N?", where f depends on the digits (count of 1s, digit sums).
- Counting numbers built from a restricted set of digits, numbers with distinct digits, numbers without equal neighbouring digits, numbers divisible by m.

## 4. Prerequisites

- [Memoization](../../00-foundations/06-memoization/) and [Tabulation](../../00-foundations/07-tabulation/).
- [State representation](../../00-foundations/09-state-representation/): choosing the smallest summary of the past.
- [Bitmask DP](../09-bitmask-dp/): used for the "distinct digits" state in LeetCode 2376.
- Comfort with taking digits of a number (`x % 10`, `x / 10`) and modular arithmetic.

## 5. What you will learn

- Why looping to 10¹⁸ is impossible, and how digit-by-digit construction avoids it.
- The `tight` flag, worked through on N = 235 until it is obvious.
- The `started` flag for leading zeros: when it is needed and when it is not.
- `count(R) − count(L − 1)` for ranges.
- Four variations of the extra state: remainder of the digit sum, count of a digit, used-digit mask, previous digit.

## 6. Important terminology

| Term | Meaning in plain English |
|---|---|
| Position (`pos`) | which digit we are choosing, counted from the left (most significant) |
| Padding / leading zeros | zeros added on the left so every number has the limit's length (7 → `007`) |
| `tight` | every digit so far equals the limit's digit, so the next digit is capped by the limit |
| Loose | not tight: the number is already smaller than the limit, so later digits are free (0–9) |
| `started` | a non-zero digit has been placed; before that, zeros are only padding |
| count(X) | how many valid numbers are in `[0, X]` |
| Extra state | the summary of the prefix the problem needs (sum mod D, used digits, previous digit…) |

## 7. Where this is used in real problems

- Counting IDs, licence plates or codes in a numeric range that satisfy digit rules.
- Competitive programming range-count problems with limits like 10¹⁸.
- LeetCode Hard problems on digit counts (233), distinct digits (2376, 1012, 357), restricted digit sets (902), binary-digit rules (600), digit-sum ranges (2719).

## 8. After completing this topic you should be able to…

- [ ] Explain why a loop up to R is too slow and how many steps digit DP needs instead.
- [ ] Trace the `tight` flag through a 3-digit limit and count how many numbers each branch covers.
- [ ] Decide whether a problem needs the `started` flag.
- [ ] Solve AtCoder DP S with memoization, tabulation and two rows.
- [ ] Write `count(R) − count(L − 1)` correctly, including `L = 0`.
- [ ] Choose the extra state for a new digit property.

## 9. Practice problems

| # | Problem | Platform/ID | Difficulty | Pattern tested | Why it belongs here |
|---|---|---|---|---|---|
| 1 | [Count Numbers with Unique Digits](https://leetcode.com/problems/count-numbers-with-unique-digits/) | LeetCode 357 | Medium | distinct digits, no tight limit | warm-up: the limit is a power of 10, so only `started` and a mask matter |
| 2 | [Rotated Digits](https://leetcode.com/problems/rotated-digits/) | LeetCode 788 | Medium | allowed digits + "changed" flag | a gentle tight + one-flag problem |
| 3 | [Digit Sum](https://atcoder.jp/contests/dp/tasks/dp_s) | AtCoder DP S | – | `sum % D`, tight | the core lesson of this topic |
| 4 | [Counting Numbers](https://cses.fi/problemset/task/2220) | CSES 2220 | – | previous digit, `started`, range | the cleanest `count(R) − count(L−1)` problem |
| 5 | [Numbers At Most N Given Digit Set](https://leetcode.com/problems/numbers-at-most-n-given-digit-set/) | LeetCode 902 | Hard | restricted digits, shorter numbers | `started` as "blank" positions |
| 6 | [Number of Digit One](https://leetcode.com/problems/number-of-digit-one/) | LeetCode 233 | Hard | summing a quantity over all numbers | the DP returns a total, not a count |
| 7 | [Count Special Integers](https://leetcode.com/problems/count-special-integers/) | LeetCode 2376 | Hard | used-digit mask + `started` | leading zeros break the mask without `started` |
| 8 | [Numbers With Repeated Digits](https://leetcode.com/problems/numbers-with-repeated-digits/) | LeetCode 1012 | Hard | complement of 2376 | counts the opposite set: n − special(n) |
| 9 | [Non-negative Integers without Consecutive Ones](https://leetcode.com/problems/non-negative-integers-without-consecutive-ones/) | LeetCode 600 | Hard | digit DP in base 2 | the same skeleton on binary digits |
| 10 | [Count of Integers](https://leetcode.com/problems/count-of-integers/) | LeetCode 2719 | Hard | digit-sum range on huge string bounds | combines a sum state with `count(R) − count(L−1)` on strings |

## 10. Navigation

⬅ Previous: [Traveling Salesperson Problem](../10-traveling-salesperson-problem/) · ➡ Next: [State Machine DP](../12-state-machine-dp/) · 📖 [Walkthrough](walkthrough.md) · 💻 [Solutions](solution/)
