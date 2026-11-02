# Prefix and Suffix Optimization

> **Module:** 05 — DP Optimization · **Difficulty:** Advanced · **Estimated time:** 90–120 min

## 1. What is this topic?

A time optimization for DP transitions that **sum, maximize or minimize over a contiguous range of the
previous row**. Instead of re-scanning the range for every cell, we pre-compute running totals (prefix sums)
or running extremes (prefix/suffix maxima, best + second best) of the previous row once, and then answer
each cell in O(1).

## 2. Why does this concept exist?

Many correct DPs are one factor too slow: `N·K` states, but each state loops over up to `K` earlier states.
Neighbouring states loop over almost the same range, so most of that work is repeated. Prefix structures
remove the repetition without changing the recurrence at all.

## 3. What problem does it solve?

It turns `O(N·K²)` into `O(N·K)` (AtCoder Candies), `O(n³)` into `O(n²)` (LeetCode 1289 Minimum Falling Path
Sum II, AtCoder Permutation), and `O(m·n²)` into `O(m·n)` (LeetCode 1937 Maximum Number of Points with Cost).

## 4. Prerequisites

- [Tabulation](../../00-foundations/07-tabulation/) and [Transition Dependencies and Computation Order](../../00-foundations/11-transition-dependencies-and-computation-order/)
- [Minimum Path Sum](../../02-two-dimensional-and-grid-dp/04-minimum-path-sum/) and [Maximum Falling Path Sum](../../02-two-dimensional-and-grid-dp/06-maximum-falling-path-sum/) (row-by-row DP)
- [Coin Change II](../../03-subsequence-and-knapsack-dp/09-coin-change-ii/) (counting DPs with a modulus)
- [Space Optimization](../01-space-optimization/) and [Rolling Arrays](../02-rolling-arrays/) (keeping only the previous row)
- Prefix sums are taught from scratch in the walkthrough (Section 4.8).

## 5. What you will learn

- How to spot a transition that reads a contiguous range of an already finished row.
- Prefix sums, range sums in O(1), and the `+ MOD` rule for subtraction under a modulus.
- Prefix maxima, suffix maxima, and the best + second-best trick for "all columns except mine".
- The left-sweep / right-sweep trick for `max(prev[c'] − |c − c'|)`.
- Why these tricks do **not** handle a sliding window of maxima (that is the next topic).

## 6. Important terminology

| Term | Meaning in plain English |
|---|---|
| Prefix sum `P[t]` | Total of the first `t` elements: `A[0] + … + A[t−1]`, with `P[0] = 0`. |
| Range sum | Sum of `A[lo..hi]`, computed as `P[hi+1] − P[lo]`. |
| Prefix maximum | Largest value among `A[0..c]`. |
| Suffix maximum | Largest value among `A[c..n−1]`. |
| Best / second best | The smallest (or largest) value of a row, its position, and the runner-up; enough to answer "min of all except one position". |
| Transition range | The set of previous-row columns that one cell reads. |
| Invertible operation | An operation you can undo (sum: by subtraction). `max` and `min` are not invertible. |

## 7. Where this is used in real problems

- Counting distributions with upper bounds (Candies, dice with a target sum).
- Counting permutations with `<`/`>` patterns (AtCoder Permutation).
- Grid DPs where the next row may be reached from "any column except this one" or "any column, with a
  distance penalty".
- As a first step before harder optimizations: once you see a range in a transition, you will ask whether a
  prefix, a deque ([next topic](../04-monotonic-queue-optimization/)) or a line container is needed.

## 8. After completing this topic you should be able to…

- [ ] Write the slow transition and point at the contiguous range it reads.
- [ ] Build a prefix-sum array with the `P[0] = 0` convention and use `P[hi+1] − P[lo]` without off-by-one errors.
- [ ] Subtract correctly under a modulus.
- [ ] Replace "min over all columns except `c`" with best + second best.
- [ ] Split `|c − c'|` into a left sweep and a right sweep.
- [ ] Explain why prefix maxima cannot answer a sliding window.
- [ ] Verify an optimized DP against the slow version with randomized tests.

## 9. Practice problems

| # | Problem | Platform/ID | Difficulty | Pattern tested | Why it belongs here |
|---|---|---|---|---|---|
| 1 | [Number of Dice Rolls With Target Sum](https://leetcode.com/problems/number-of-dice-rolls-with-target-sum/) | LeetCode 1155 | Medium | Range sum of previous row | `dp[i][t]` sums `dp[i−1][t−k..t−1]`; prefix sums make each cell O(1). |
| 2 | [Candies](https://atcoder.jp/contests/dp/tasks/dp_m) | AtCoder dp_m | Unrated (AtCoder) | Prefix sums under a modulus | The main example of this topic: `O(N·K²) → O(N·K)`. |
| 3 | [Minimum Falling Path Sum II](https://leetcode.com/problems/minimum-falling-path-sum-ii/) | LeetCode 1289 | Hard | Best + second best | "Min of the row except my column" in O(1). |
| 4 | [Maximum Number of Points with Cost](https://leetcode.com/problems/maximum-number-of-points-with-cost/) | LeetCode 1937 | Medium | Prefix and suffix maxima with a distance penalty | Splitting `|c − c'|` into two sweeps; a step up in the algebra. |
| 5 | [Permutation](https://atcoder.jp/contests/dp/tasks/dp_t) | AtCoder dp_t | Unrated (AtCoder) | Prefix and suffix sums | Hardest state design here; the transition is a prefix or a suffix range. |

## 10. Navigation

⬅ Previous: [Rolling Arrays](../02-rolling-arrays/) · ➡ Next: [Monotonic Queue Optimization](../04-monotonic-queue-optimization/) · 📖 [Walkthrough](walkthrough.md) · 💻 [Solutions](solution/)
