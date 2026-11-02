# 152. Maximum Product Subarray

> **Pattern:** "best subarray ending at i" DP with two tracked values (max and min) · **Builds on:** [Maximum Subarray: Kadane vs DP](../../../01-one-dimensional-dp/09-maximum-subarray-kadane-vs-dp/) · **Difficulty:** Medium

## Problem overview

Given an integer array (positives, negatives, zeros), find the contiguous non-empty subarray whose
**product** is largest, and return that product.

## Link & metadata

| Field | Value |
|---|---|
| LeetCode | [152. Maximum Product Subarray](https://leetcode.com/problems/maximum-product-subarray/) |
| Difficulty | Medium |
| Pattern | Kadane-style "ending at i" DP, extended to carry **two** states per index |
| Prerequisite topic | [`01-one-dimensional-dp/09-maximum-subarray-kadane-vs-dp`](../../../01-one-dimensional-dp/09-maximum-subarray-kadane-vs-dp/) |
| Why this pattern | Same question as Maximum Subarray with `×` instead of `+`; the twist is that multiplying by a negative swaps "largest" and "smallest" |

## Examples

| nums | Answer | Why |
|---|---|---|
| `[2, 3, -2, 4]` | 6 | `[2, 3]` |
| `[-2, 0, -1]` | 0 | `[0]`; `[-2, -1]` is not contiguous |
| `[-2, 3, -4]` | 24 | the whole array: two negatives cancel |
| `[-2]` | -2 | a single negative element |

## Brute-force intuition

Try every pair `(start, end)` and multiply: O(n²) subarrays, O(n) or O(1) each with a running
product. Kadane's idea removes the outer loop: decide, for each index, the best subarray that
**ends exactly there**, built from the answer one index earlier.

Why one value is not enough here:

```
nums:           2     3    -2     4    -1
best ending:    2     6    -2     4    ??
```

At the last `-1`, the best product ending there is `2·3·(-2)·4·(-1) = 48`. It comes from the
**most negative** product ending at index 3 (`-48`), not from the largest (`4`). A negative
number turns the smallest product into the largest. So we must remember both.

## Deriving the DP (7 questions)

### 1. What is changing?
The index `i` where the subarray ends.

### 2. What does the state represent?
Two values per index:
- `maxEnd[i]` = the largest product of a subarray that ends exactly at `i`.
- `minEnd[i]` = the smallest (most negative) product of a subarray that ends exactly at `i`.

### 3. What are the choices?
A subarray ending at `i` either
- **starts fresh** at `i` (just `nums[i]`), or
- **extends** the best/worst subarray ending at `i-1` by multiplying in `nums[i]`.

### 4. What is the recurrence?
```
candidates = { nums[i],  maxEnd[i-1] * nums[i],  minEnd[i-1] * nums[i] }
maxEnd[i]  = max(candidates)
minEnd[i]  = min(candidates)
answer     = max over all i of maxEnd[i]
```
We try both previous extremes because we do not know the sign of `nums[i]` in advance. A zero
gives candidates `{0, 0, 0}`, which correctly "resets" both tracks.

### 5. What are the base cases?
`maxEnd[0] = minEnd[0] = nums[0]`: the only subarray ending at index 0 is `[nums[0]]`.

### 6. What is the order of computation?
Index `i` needs `i - 1`, so go left to right.

### 7. Can the state space be optimized?
Only the previous pair is read: two variables plus the running answer, O(1) space.

## Recursive solution

```java
// returns {maxEnd(i), minEnd(i)}
static int[] ending(int i) {
    if (i == 0) return new int[]{nums[0], nums[0]};
    int[] prev = ending(i - 1);
    int a = nums[i], b = prev[0] * nums[i], c = prev[1] * nums[i];
    return new int[]{max(a, b, c), min(a, b, c)};
}
```

This chain is linear, not exponential, because each call makes only one recursive call. The memo
still helps because the answer is the maximum over **all** `i`, and we query `ending(i)` for each.

## Memoization

See [`solution/Memoization.java`](solution/Memoization.java): two `Integer[]` caches, filled
on first request.

## Tabulation

See [`solution/Tabulation.java`](solution/Tabulation.java): arrays `maxEnd` and `minEnd` filled
left to right.

## Space optimization

See [`solution/Optimized.java`](solution/Optimized.java). **Save the old `maxEnd` in a temporary**
before overwriting it, because `minEnd` still needs it.

```java
int candidateA = x, candidateB = curMax * x, candidateC = curMin * x;
curMax = max(candidateA, candidateB, candidateC);
curMin = min(candidateA, candidateB, candidateC);
```

## Dry run

`nums = [-2, 3, -4]`:

| i | nums[i] | fresh | prevMax·x | prevMin·x | maxEnd | minEnd | answer |
|---|---|---|---|---|---|---|---|
| 0 | -2 | -2 | — | — | -2 | -2 | -2 |
| 1 | 3 | 3 | -6 | -6 | 3 | -6 | 3 |
| 2 | -4 | -4 | -12 | **24** | 24 | -12 | **24** |

At `i = 2`, the minimum `-6` (from `[-2, 3]`) times `-4` gives 24. A single-track Kadane would
have lost it.

## Complexity

| Approach | Time | Space | Reason |
|---|---|---|---|
| Brute force (all subarrays) | O(n²) | O(1) | every start, running product to every end |
| Memoization | O(n) | O(n) | 2n cached values, O(1) each; stack depth n |
| Tabulation | O(n) | O(n) | one pass; two arrays |
| Optimized | O(n) | O(1) | one pass; two variables |

## Why the optimized approach is better

Same single pass, no arrays, no recursion depth. It is the standard interview answer.

## Common mistakes

- **Tracking only the maximum.** Fails on `[-2, 3, -4]`.
- **Updating `curMax` first and then using the new value to compute `curMin`.** Use temporaries.
- **Starting `answer` at 0.** For `[-2]` the answer is -2; start from `nums[0]`.

## Key takeaway

If an operation can flip "best" into "worst" (multiplying by a negative), carry **both extremes**
as state. Two numbers per index is still a 1D DP.

## Related problems

- [53. Maximum Subarray](https://leetcode.com/problems/maximum-subarray/), the additive version, taught in [`09-maximum-subarray-kadane-vs-dp`](../../../01-one-dimensional-dp/09-maximum-subarray-kadane-vs-dp/)
- [918. Maximum Sum Circular Subarray](../918-maximum-sum-circular-subarray/), which also tracks max and min, for a different reason
- Previous: [279. Perfect Squares](../279-perfect-squares/) · Next: [918. Maximum Sum Circular Subarray](../918-maximum-sum-circular-subarray/)
