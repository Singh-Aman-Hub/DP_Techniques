# 801. Minimum Swaps To Make Sequences Increasing

> **Pattern:** Two-state DP along an array: "position i kept" vs "position i swapped" · **Builds on:** [State Machine DP](../../../04-advanced-dp-patterns/12-state-machine-dp/) · **Difficulty:** Hard

## Problem overview

Two integer arrays `nums1` and `nums2` have the same length. One operation swaps `nums1[i]` with
`nums2[i]` (same index). Find the minimum number of operations that makes **both** arrays
strictly increasing. The input guarantees this is possible.

## Link & metadata

| Field | Value |
|---|---|
| LeetCode | [801. Minimum Swaps To Make Sequences Increasing](https://leetcode.com/problems/minimum-swaps-to-make-sequences-increasing/) |
| Difficulty | Hard |
| Pattern | Linear scan with two states per index (a 2-state machine), minimizing cost |
| Prerequisite topic | [`04-advanced-dp-patterns/12-state-machine-dp`](../../../04-advanced-dp-patterns/12-state-machine-dp/) (stock problems in [`02-stock-buy-and-sell`](../../../04-advanced-dp-patterns/02-stock-buy-and-sell/) use the same "hold / not hold" idea) |
| Why this pattern | Whether index `i` is valid depends only on index `i - 1`, and only on whether `i - 1` was swapped. So the state is (index, swapped or not) |

## Examples

| nums1 | nums2 | Answer | Why |
|---|---|---|---|
| `[1,3,5,4]` | `[1,2,3,7]` | 1 | swap index 3 → `[1,3,5,7]`, `[1,2,3,4]` |
| `[0,3,5,8,9]` | `[2,1,4,6,9]` | 1 | swap index 1 → `[0,1,5,8,9]`, `[2,3,4,6,9]` |
| `[1]` | `[1]` | 0 | a single element is increasing |

## Brute-force intuition

Each index is swapped or not: 2ⁿ combinations, check both arrays for each. But the validity of
index `i` only involves the pair at `i - 1` and the pair at `i`. Once we know whether `i - 1`
was swapped, the earlier choices do not matter. Two states per index is enough.

For consecutive indices there are two useful conditions:

```
sameOK  = nums1[i-1] < nums1[i]  and  nums2[i-1] < nums2[i]   // valid if both or neither swapped
crossOK = nums1[i-1] < nums2[i]  and  nums2[i-1] < nums1[i]   // valid if exactly one is swapped
```

State machine for the step `i-1 → i`:

```
     index i-1                             index i
   ┌───────────┐   sameOK,  cost +0     ┌───────────┐
   │   KEEP    │ ─────────────────────► │   KEEP    │
   └───────────┘ ╲                   ↗  └───────────┘
                   ╲ crossOK,      ╱
                     ╲ cost +1   ╱ crossOK, cost +0
                       ╲       ╱
                         ╲   ╱
                           ╳
                         ╱   ╲
   ┌───────────┐       ╱       ↘        ┌───────────┐
   │   SWAP    │ ─────────────────────► │   SWAP    │
   └───────────┘   sameOK,  cost +1     └───────────┘

   KEEP(i-1) → KEEP(i): sameOK, +0      KEEP(i-1) → SWAP(i): crossOK, +1
   SWAP(i-1) → SWAP(i): sameOK, +1      SWAP(i-1) → KEEP(i): crossOK, +0
```
The cost is `+1` exactly when the arrow ends in SWAP, because that is when index `i` is swapped.

## Deriving the DP (7 questions)

### 1. What is changing?
The index `i`, and whether index `i` is swapped.

### 2. What does the state represent?
- `keep[i]` = minimum swaps so that both prefixes `0..i` are strictly increasing, with index `i`
  **not** swapped.
- `swap[i]` = the same, with index `i` **swapped**.

### 3. What are the choices?
For each of the two states at `i`: whether index `i - 1` was swapped or not (only the
combinations allowed by `sameOK` / `crossOK`).

### 4. What is the recurrence?
```
keep[i] = min( sameOK  ? keep[i-1]     : ∞,     // neither swapped
               crossOK ? swap[i-1]     : ∞ )    // only i-1 swapped
swap[i] = min( sameOK  ? swap[i-1] + 1 : ∞,     // both swapped
               crossOK ? keep[i-1] + 1 : ∞ )    // only i swapped
answer  = min(keep[n-1], swap[n-1])
```
`+1` counts the swap at index `i` itself.

### 5. What are the base cases?
`keep[0] = 0`, `swap[0] = 1`. A single index is always increasing.

### 6. What is the order of computation?
Left to right.

### 7. Can the state space be optimized?
Each index reads only `i - 1`: two variables.

## Recursive solution

```java
// min swaps for indices i..n-1, given whether index i-1 was swapped
static int solve(int i, boolean prevSwapped) {
    if (i == n) return 0;
    int prevA = prevSwapped ? nums2[i-1] : nums1[i-1];   // (only when i > 0)
    int prevB = prevSwapped ? nums1[i-1] : nums2[i-1];
    int best = INF;
    if (i == 0 || (prevA < nums1[i] && prevB < nums2[i])) best = solve(i + 1, false);
    if (i == 0 || (prevA < nums2[i] && prevB < nums1[i])) best = Math.min(best, 1 + solve(i + 1, true));
    return best;
}
```

## Memoization

Cache `solve(i, prevSwapped)`: `2n` states. See [`solution/Memoization.java`](solution/Memoization.java).

## Tabulation

Arrays `keep[]` and `swap[]`. See [`solution/Tabulation.java`](solution/Tabulation.java).

## Space optimization

Two variables, with temporaries so both new values use the old pair. See
[`solution/Optimized.java`](solution/Optimized.java).

## Dry run

`nums1 = [1,3,5,4]`, `nums2 = [1,2,3,7]`:

| i | pair | sameOK | crossOK | keep[i] | swap[i] |
|---|---|---|---|---|---|
| 0 | (1,1) | — | — | 0 | 1 |
| 1 | (3,2) | 1<3, 1<2 ✓ | 1<2, 1<3 ✓ | min(0, 1) = 0 | min(1+1, 0+1) = 1 |
| 2 | (5,3) | 3<5, 2<3 ✓ | 3<3 ✗ | 0 | 1+1 = 2 |
| 3 | (4,7) | 5<4 ✗ | 5<7, 3<4 ✓ | swap[2] = 2 | keep[2]+1 = **1** |

Answer `min(2, 1) = 1`.

## Complexity

| Approach | Time | Space | Reason |
|---|---|---|---|
| Brute force | O(2ⁿ · n) | O(n) | every swap pattern, checked |
| Memoization | O(n) | O(n) | 2n states; stack depth n |
| Tabulation | O(n) | O(n) | two arrays |
| Optimized | O(n) | O(1) | two variables |

## Why the optimized approach is better

`n` can be 10⁵: a loop with two variables, no recursion, no arrays.

## Common mistakes

- **Treating sameOK and crossOK as exclusive.** Both can hold; then both transitions are allowed.
- **Greedy: "swap when the current index is broken".** Sometimes it is cheaper to swap the
  *earlier* index (example 2 swaps index 1, not index 2).
- **Updating `keep` and then using the new `keep` to compute `swap`.** Use temporaries.

## Key takeaway

If a decision at index `i` is only constrained by the decision at `i - 1`, make that decision
part of the state. "Kept / swapped" here plays the same role as "holding / not holding" in the
stock problems.

## Related problems

- [309. Best Time to Buy and Sell Stock with Cooldown](https://leetcode.com/problems/best-time-to-buy-and-sell-stock-with-cooldown/), state machine over days, taught in [`02-stock-buy-and-sell`](../../../04-advanced-dp-patterns/02-stock-buy-and-sell/)
- [552. Student Attendance Record II](../552-student-attendance-record-ii/), state carrying a short history
- Previous: [552. Student Attendance Record II](../552-student-attendance-record-ii/) · Back to the [pattern overview](../README.md)
