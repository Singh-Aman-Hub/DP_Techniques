# 698. Partition to K Equal Sum Subsets

> **Pattern:** Bitmask DP — fill buckets one after another; the mask determines the current bucket's fill · **Difficulty:** Medium
> **Builds on:** [Bitmask DP](../../../04-advanced-dp-patterns/09-bitmask-dp/) and [Equal Sum Partition](../../../03-subsequence-and-knapsack-dp/04-equal-sum-partition/)

---

## Problem overview

Given up to 16 positive numbers and an integer `k`, decide whether the numbers can be split into `k` non-empty groups that all have the same sum.

**Why bitmask DP and not knapsack.** For `k = 2` this is [Equal Sum Partition](../../../03-subsequence-and-knapsack-dp/04-equal-sum-partition/): find one subset with half the total. For general `k` the groups interact — a number used in one bucket is gone for the others — so a "sum" state is not enough; we must know **which** numbers are used. With 16 numbers that is a 16-bit mask. The trick that keeps the state tiny: fill the buckets **one at a time**. Then the amount already in the current bucket is `sum(mask) mod target`, fully determined by the mask.

## Link & metadata

| Field | Value |
|---|---|
| LeetCode | [698. Partition to K Equal Sum Subsets](https://leetcode.com/problems/partition-to-k-equal-sum-subsets/) |
| Difficulty | Medium |
| Pattern | Bitmask DP over used items, implicit "current bucket" |
| Prerequisite topics | [Bitmask DP](../../../04-advanced-dp-patterns/09-bitmask-dp/), [Equal Sum Partition](../../../03-subsequence-and-knapsack-dp/04-equal-sum-partition/), [Subset Sum](../../../03-subsequence-and-knapsack-dp/03-subset-sum/) |

## Examples

| nums | k | Answer | Why |
|---|---|---|---|
| [4,3,2,3,5,2,1] | 4 | true | target 5: (5), (1,4), (2,3), (2,3) |
| [1,2,3,4] | 3 | false | sum 10 is not divisible by 3 |
| [1,2,3] | 2 | true | target 3: (3), (1,2) — used in the dry run |
| [10,1,1] | 2 | false | target 6, but 10 > 6 |
| [5] | 1 | true | edge case: one bucket takes everything |

## Brute-force intuition

Assign each number to one of `k` buckets: `k^n` assignments (`4^16 ≈ 4·10^9`). Backtracking with pruning helps, but bucket labels are interchangeable and many assignments reach the same "set of used numbers" with the same partial bucket — repeated work.

## Deriving the DP (7 questions)

### 1. What is changing?
The set of numbers already placed into buckets. Since buckets are filled **in order** (bucket 1 completely, then bucket 2, …), the fill of the current bucket is `sum(mask) % target` — no extra variable.

### 2. What does the state represent?
`canFinish(mask)` = true if the numbers **not** in `mask` can be placed to complete the current bucket and all remaining buckets, given that the numbers in `mask` were placed as complete buckets plus a partial current bucket of `sum(mask) % target`.

### 3. What are the choices?
Put an unused number `i` into the current bucket, if it fits: `sum(mask) % target + nums[i] ≤ target`.

### 4. What is the recurrence?
```
canFinish(mask) = OR over unused i with fill + nums[i] <= target of canFinish(mask | bit(i))
where fill = sum(mask) % target
```
When a bucket becomes exactly full, `fill` wraps to 0 and the next bucket starts automatically.

### 5. What are the base cases?
- Pre-checks: `sum % k != 0` → false; any number `> target` → false.
- `canFinish(full) = true`: every number is placed, each completed bucket summed to `target`, and since total = `k · target`, there are exactly `k` buckets.

### 6. What is the order of computation?
Transitions add a bit, so bottom-up we go `mask = 0 … 2^n − 1` increasing and mark which masks are **reachable** (`reachable[mask]`, with `fill[mask]`). The answer is `reachable[full]`.

### 7. Can the state space be optimized?
`2^n` states are needed. We store `fill[mask]` (or `-1` for unreachable) instead of a separate boolean + sum array, so one int per mask. No `Optimized.java`; sorting numbers in decreasing order is a useful pruning for backtracking but does not change the DP's complexity.

## Recursive solution

```java
boolean canFinish(int mask, int fill) {
    if (mask == full) return true;
    for (int i = 0; i < n; i++) {
        if ((mask & (1 << i)) != 0 || fill + nums[i] > target) continue;
        if (canFinish(mask | (1 << i), (fill + nums[i]) % target)) return true;
    }
    return false;
}
```

## Memoization

Cache by `mask` only (`fill` is implied by it). See [`solution/Memoization.java`](solution/Memoization.java).

## Tabulation

```
fill[0] = 0;  fill[other] = -1 (unreachable)
for mask = 0 .. full:
    if fill[mask] == -1: continue
    for each unused i with fill[mask] + nums[i] <= target:
        fill[mask | bit(i)] = (fill[mask] + nums[i]) % target
answer = fill[full] != -1
```
See [`solution/Tabulation.java`](solution/Tabulation.java).

## Space optimization

Not applicable beyond storing one int per mask.

## Dry run

`nums = [1,2,3]`, `k = 2`, `target = 3`, bottom-up (masks as sets of values):

| mask | `fill` | unused numbers that fit → new mask (new fill) |
|---|---|---|
| {} | 0 | 1 → {1} (1); 2 → {2} (2); 3 → {3} (0, bucket closed) |
| {1} | 1 | 2 → {1,2} (0, bucket closed); 3 doesn't fit (1+3 > 3) |
| {2} | 2 | 1 → {1,2} (0); 3 doesn't fit |
| {1,2} | 0 | 3 → {1,2,3} (0) |
| {3} | 0 | 1 → {1,3} (1); 2 → {2,3} (2) |
| {1,3} | 1 | 2 → {1,2,3} (0) |
| {2,3} | 2 | 1 → {1,2,3} (0) |
| {1,2,3} | 0 | full set reachable → **true** |

## Complexity

| Approach | Time | Space | Why |
|---|---|---|---|
| Assign to buckets | O(kⁿ) | O(n) | every labelled assignment |
| Memoization | O(2ⁿ·n) | O(2ⁿ) | each mask tries n numbers once |
| Tabulation | O(2ⁿ·n) | O(2ⁿ) | same, iterative |

`n = 16`: about `10^6` steps.

## Why the optimized approach is better

Filling buckets in a fixed order removes both the bucket labels and the "current fill" from the state, leaving just `2^n` masks.

## Common mistakes

- Storing `(mask, bucketIndex, fill)` — all three are derived from `mask`.
- Allowing `fill + nums[i] > target` and hoping to fix it later — a bucket can never shrink.
- Skipping the `sum % k` and `max > target` pre-checks (they make the DP's assumptions true).

## Key takeaway

When several containers must be filled exactly, **fill them one at a time**; then "how full is the current one" is `sum(mask) % target`, and the mask is the whole state.

## Related problems

- [Equal Sum Partition](../../../03-subsequence-and-knapsack-dp/04-equal-sum-partition/) — the `k = 2` special case without masks.
- [1986. Minimum Number of Work Sessions to Finish the Tasks](../1986-minimum-number-of-work-sessions-to-finish-the-tasks/) — same "fill one container at a time", minimising the number of containers.
- [464. Can I Win](../464-can-i-win/) — another state where the mask implies a running sum.
