# 1879. Minimum XOR Sum of Two Arrays

> **Pattern:** Bitmask assignment DP — match item `i` of one list to an unused item of the other · **Difficulty:** Hard
> **Builds on:** [Bitmask DP](../../../04-advanced-dp-patterns/09-bitmask-dp/) and [526. Beautiful Arrangement](../526-beautiful-arrangement/)

---

## Problem overview

Two arrays `nums1` and `nums2` have the same length `n ≤ 14`. You may reorder `nums2` any way you like. Then the cost is `Σ (nums1[i] XOR nums2[i])`. Find the minimum possible cost.

(`XOR`, written `^` in Java, compares two numbers bit by bit and sets a result bit to 1 where the inputs differ. Example: `1 ^ 3 = 01 ^ 11 = 10 = 2`.)

**Why bitmask DP.** Reordering `nums2` = choosing a one-to-one **assignment** (a matching) between positions of `nums1` and elements of `nums2`. If we assign `nums1[0], nums1[1], …` in order, the only thing the future needs to know is *which* `nums2` elements are taken — a mask. The index `i` of the next `nums1` element is `bitCount(mask)`, exactly like the position in [526](../526-beautiful-arrangement/). This is the classic "assignment problem" form of [Bitmask DP](../../../04-advanced-dp-patterns/09-bitmask-dp/). (XOR has no structure that a greedy could exploit here.)

## Link & metadata

| Field | Value |
|---|---|
| LeetCode | [1879. Minimum XOR Sum of Two Arrays](https://leetcode.com/problems/minimum-xor-sum-of-two-arrays/) |
| Difficulty | Hard |
| Pattern | Assignment DP over a bitmask; index = popcount |
| Prerequisite topics | [Bitmask DP](../../../04-advanced-dp-patterns/09-bitmask-dp/), [State representation](../../../00-foundations/09-state-representation/) |

## Examples

| nums1 | nums2 | Answer | Best pairing |
|---|---|---|---|
| [1,2] | [2,3] | 2 | 1↔3 (2), 2↔2 (0) |
| [1,0,3] | [5,3,4] | 8 | 1↔5 (4), 0↔4 (4), 3↔3 (0) |
| [5] | [5] | 0 | edge case: one pair, equal |

## Brute-force intuition

Try all `n!` orders of `nums2` (`14! ≈ 8.7·10^10`). Many prefixes use the **same set** of `nums2` elements for the first `i` positions; only the cheapest such prefix can matter for the future.

## Deriving the DP (7 questions)

### 1. What is changing?
Which `nums2` elements are already matched. The next `nums1` index is `i = bitCount(mask)`.

### 2. What does the state represent?
`cost[mask]` = the minimum XOR sum after matching `nums1[0 … i−1]` (with `i = bitCount(mask)`) to exactly the `nums2` elements in `mask`.

### 3. What are the choices?
Which unused `nums2[j]` to pair with `nums1[i]`.

### 4. What is the recurrence?
Bottom-up ("push"): from `mask` with `i = bitCount(mask)`, for every `j` not in `mask`:
```
cost[mask | bit(j)] = min( cost[mask | bit(j)],  cost[mask] + (nums1[i] ^ nums2[j]) )
```
Top-down ("pull" over the remaining part): `best(mask) = min over unused j of (nums1[i] ^ nums2[j]) + best(mask | bit(j))`, `best(full) = 0`.

### 5. What are the base cases?
`cost[∅] = 0` — nothing matched, nothing paid. Top-down: `best(full) = 0`.

### 6. What is the order of computation?
Every transition adds a bit → masks in increasing order (bottom-up). Answer `cost[full]`.

### 7. Can the state space be optimized?
`2^n` states are required; there is no row structure to roll. No `Optimized.java`.

## Recursive solution

```java
int best(int mask) {
    if (mask == full) return 0;
    int i = Integer.bitCount(mask);
    int result = Integer.MAX_VALUE;
    for (int j = 0; j < n; j++)
        if ((mask & (1 << j)) == 0)
            result = Math.min(result, (nums1[i] ^ nums2[j]) + best(mask | (1 << j)));
    return result;
}
```

## Memoization

Cache `best(mask)`; `2^14 = 16384` entries. See [`solution/Memoization.java`](solution/Memoization.java).

## Tabulation

Push-style table over increasing masks. See [`solution/Tabulation.java`](solution/Tabulation.java).

## Space optimization

Not applicable.

## Dry run

`nums1 = [1,2]`, `nums2 = [2,3]` (bit 0 = the `2`, bit 1 = the `3`):

| mask | i = bitCount | `cost[mask]` | pushes |
|---|---|---|---|
| 00 | 0 | 0 | j=0: 1^2 = 3 → cost[01] = 3; j=1: 1^3 = 2 → cost[10] = 2 |
| 01 | 1 | 3 | j=1: 3 + (2^3 = 1) = 4 → cost[11] = 4 |
| 10 | 1 | 2 | j=0: 2 + (2^2 = 0) = 2 → cost[11] = min(4, 2) = 2 |
| 11 | 2 | **2** | full |

## Complexity

| Approach | Time | Space | Why |
|---|---|---|---|
| All permutations | O(n!·n) | O(n) | every order of nums2 |
| Memoization | O(2ⁿ·n) | O(2ⁿ) | 2ⁿ masks × n choices |
| Tabulation | O(2ⁿ·n) | O(2ⁿ) | same, iterative |

`n = 14`: ~230 thousand steps.

## Why the optimized approach is better

The order in which earlier pairs were formed is irrelevant to the future — only the used set matters. `n!` → `2^n · n`.

## Common mistakes

- Storing `i` as a second dimension (`dp[i][mask]`): redundant, since `i = bitCount(mask)`.
- Trying a greedy "pair equal / closest numbers": XOR distance does not behave like ordinary distance.
- Using a mask over `nums1` *and* an index over `nums2` inconsistently — pick one side for the mask.

## Key takeaway

Any **one-to-one assignment** between two small lists (n ≤ ~20) is a bitmask DP: process one list in order, mask the other, and the index is the popcount.

## Related problems

- [526. Beautiful Arrangement](../526-beautiful-arrangement/) — same skeleton, counting.
- [1434. Number of Ways to Wear Different Hats to Each Other](../1434-number-of-ways-to-wear-different-hats-to-each-other/) — assignment where you must choose which side to mask.
- [Bitmask DP](../../../04-advanced-dp-patterns/09-bitmask-dp/) — the topic lesson.
