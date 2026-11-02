# 918. Maximum Sum Circular Subarray

> **Pattern:** Kadane "ending at i" DP, run for both max and min, plus a complement trick · **Builds on:** [Maximum Subarray: Kadane vs DP](../../../01-one-dimensional-dp/09-maximum-subarray-kadane-vs-dp/) · **Difficulty:** Medium

## Problem overview

The array is **circular**: after the last element comes the first one again. Find the largest sum
of a non-empty contiguous piece of this circle. Each element may be used at most once, so the
piece can wrap around the end, but cannot go all the way round twice.

## Link & metadata

| Field | Value |
|---|---|
| LeetCode | [918. Maximum Sum Circular Subarray](https://leetcode.com/problems/maximum-sum-circular-subarray/) |
| Difficulty | Medium |
| Pattern | Two Kadane DPs (max-ending-here and min-ending-here) combined with "total minus the part you leave out" |
| Prerequisite topic | [`01-one-dimensional-dp/09-maximum-subarray-kadane-vs-dp`](../../../01-one-dimensional-dp/09-maximum-subarray-kadane-vs-dp/) |
| Why this pattern | Every circular subarray is either an ordinary subarray, or the complement of an ordinary subarray; both are Kadane questions |

## Examples

| nums | Answer | Why |
|---|---|---|
| `[1, -2, 3, -2]` | 3 | `[3]` |
| `[5, -3, 5]` | 10 | wrap: `5` (last) + `5` (first) |
| `[-3, -2, -3]` | -2 | all negative: best single element |
| `[3, -1, 2, -1]` | 4 | `[3, -1, 2]` or wrap `[2, -1, 3]` |

## Brute-force intuition

Try every start index and every length `1..n`, walking around the circle with `% n`: O(n²).
That is fine for checking, but too slow for `n = 3·10⁴`. See
[`solution/BruteForce.java`](solution/BruteForce.java).

The key picture: a wrapping subarray is "everything **except** a middle block".

```
index:      0    1    2    3    4
           [ 5 ][-3 ][-1 ][ 4 ][ 6 ]
            ^^^  ----------  ^^^^^^^^
            kept  left out    kept         wrap sum = total - (left-out block)
```

To make the wrap sum **as large as possible**, make the left-out middle block **as small as
possible**. And "smallest sum of a contiguous block" is Kadane with `min` instead of `max`.

## Deriving the DP (7 questions)

### 1. What is changing?
The index `i` where the (non-wrapping) subarray ends.

### 2. What does the state represent?
- `maxEnd[i]` = the largest sum of a non-empty subarray ending exactly at `i`.
- `minEnd[i]` = the smallest sum of a non-empty subarray ending exactly at `i`.

### 3. What are the choices?
For each of the two tracks: start fresh at `i`, or extend the subarray that ended at `i - 1`.

### 4. What is the recurrence?
```
maxEnd[i] = max(nums[i], maxEnd[i-1] + nums[i])
minEnd[i] = min(nums[i], minEnd[i-1] + nums[i])

bestNormal = max over i of maxEnd[i]
bestWrap   = total - (min over i of minEnd[i])
answer     = bestNormal                 if bestNormal < 0   (all numbers negative)
           = max(bestNormal, bestWrap)  otherwise
```

Why the special case? If every number is negative, the minimum block is the **whole array**, and
`total - total = 0` describes the **empty** subarray, which is not allowed.

### 5. What are the base cases?
`maxEnd[0] = minEnd[0] = nums[0]`.

### 6. What is the order of computation?
Left to right; each index reads only `i - 1`.

### 7. Can the state space be optimized?
Yes: two running values, two running bests and the total. O(1) space.

## Recursive solution

```java
static int maxEnd(int i) { return i == 0 ? nums[0] : Math.max(nums[i], maxEnd(i - 1) + nums[i]); }
static int minEnd(int i) { return i == 0 ? nums[0] : Math.min(nums[i], minEnd(i - 1) + nums[i]); }
```

Without caching, asking `maxEnd(i)` for every `i` repeats the whole chain each time: O(n²).

## Memoization

Cache both functions. See [`solution/Memoization.java`](solution/Memoization.java).

## Tabulation

Fill `maxEnd[]` and `minEnd[]` left to right, tracking the running best of each. See
[`solution/Tabulation.java`](solution/Tabulation.java).

## Space optimization

Replace the arrays by `curMax` and `curMin`. See [`solution/Optimized.java`](solution/Optimized.java).

## Dry run

`nums = [5, -3, 5]`, `total = 7`:

| i | nums[i] | maxEnd | bestNormal | minEnd | minBlock |
|---|---|---|---|---|---|
| 0 | 5 | 5 | 5 | 5 | 5 |
| 1 | -3 | max(-3, 5-3) = 2 | 5 | min(-3, 5-3) = -3 | -3 |
| 2 | 5 | max(5, 2+5) = 7 | 7 | min(5, -3+5) = 2 | -3 |

`bestWrap = 7 - (-3) = 10`; `bestNormal = 7 ≥ 0`, so the answer is `max(7, 10) = 10`.

Edge case `[-3, -2, -3]`: `bestNormal = -2`, `minBlock = -8 = total`, wrap would give 0 (empty).
Because `bestNormal < 0`, we return -2.

## Complexity

| Approach | Time | Space | Reason |
|---|---|---|---|
| Brute force | O(n²) | O(1) | n starts × n lengths, running sum |
| Memoization | O(n) | O(n) | 2n states once each; memo + stack |
| Tabulation | O(n) | O(n) | one pass, two arrays |
| Optimized | O(n) | O(1) | one pass, a few variables |

## Why the optimized approach is better

One linear pass and constant memory, versus quadratic work for the brute force. The complement
trick avoids the messy alternative of "doubling the array and limiting the window to length n".

## Common mistakes

- **Forgetting the all-negative case**, returning 0.
- **Concatenating the array to itself and running plain Kadane.** That allows windows longer than
  `n`, using an element twice.
- **Computing the min block on a different array range than the max.** Both run over the same
  `n` elements.

## Key takeaway

When a "wrapping" answer is hard to describe directly, describe its **complement**. Here, max
circular sum = total − min ordinary sum, and both pieces are Kadane.

## Related problems

- [53. Maximum Subarray](https://leetcode.com/problems/maximum-subarray/), the non-circular base problem
- [152. Maximum Product Subarray](../152-maximum-product-subarray/), another "track the max and the min" DP
- [213. House Robber II](https://leetcode.com/problems/house-robber-ii/), a different way to handle a circular array (split into two linear runs)
- Previous: [152. Maximum Product Subarray](../152-maximum-product-subarray/) · Next pattern: [221. Maximal Square](../../02-grid/221-maximal-square/)
