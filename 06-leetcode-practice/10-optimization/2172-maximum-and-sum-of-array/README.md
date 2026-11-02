# 2172. Maximum AND Sum of Array

> **Pattern:** Assignment bitmask DP with capacity 2 (base-3 mask, or two seats per slot) · **Builds on:** [Bitmask DP](../../../04-advanced-dp-patterns/09-bitmask-dp/) · **Difficulty:** Hard

## Problem overview

You have `n` numbers and `numSlots` slots numbered `1..numSlots` (`numSlots ≤ 9`,
`n ≤ 2 · numSlots`). Each slot holds **at most two** numbers. Put every number into some slot; a
number `x` in slot `s` earns `x AND s` (bitwise AND). Maximize the total.

Without the capacity limit this would be "each number picks its best slot". The limit couples the
choices, so we need to remember **how full every slot is**: that is the bitmask-DP idea, stretched
from "used / unused" to "0, 1 or 2 used".

## Link & metadata

| Field | Value |
|---|---|
| LeetCode | [2172. Maximum AND Sum of Array](https://leetcode.com/problems/maximum-and-sum-of-array/) |
| Difficulty | Hard |
| Pattern | Assignment DP over a set of resources: state = fill level of every slot |
| Prerequisite topic | [`04-advanced-dp-patterns/09-bitmask-dp`](../../../04-advanced-dp-patterns/09-bitmask-dp/) |
| Why this pattern | Small number of resources (≤ 9 slots), each with a tiny capacity; the future only depends on how full each one is |

## Examples

| nums | numSlots | Answer | One optimal placement |
|---|---|---|---|
| [1,2,3,4,5,6] | 3 | 9 | slot 1: {1,4} → 1+0, slot 2: {2,6} → 2+2, slot 3: {3,5} → 3+1 |
| [1,3,10,4,7,1] | 9 | 24 | slot 1: {1,1}, slot 3: {3}, slot 4: {4}, slot 7: {7}, slot 9: {10} → 1+1+3+4+7+8 |
| [1,3,2] | 2 | 5 | slot 1: {1} → 1, slot 2: {3,2} → 2+2 (the dry-run example) |
| [8,8] | 1 | 0 | 8 AND 1 = 0 |

## Brute-force intuition

Each number picks one of `numSlots` slots, respecting the capacity: up to `9¹⁸` assignments.
Far too many.

But after placing the first `i` numbers, the remaining numbers only care about **how many numbers
each slot already holds**, not which ones. Two different placements with the same fill pattern
have exactly the same best completion. That shared future is the overlapping subproblem.

## Deriving the DP (7 questions)

### 1. What is changing?
Which number we place next (`i`) and the fill level of every slot.

### 2. What does the state represent?
Encode the fill levels as a **base-3 number** `fill`: digit `s` (the `3ˢ` place) is the count in
slot `s + 1`, which is 0, 1 or 2.
`f(i, fill)` = the maximum AND sum obtainable from placing `nums[i..n−1]`, given the fill pattern.
(`i` equals the sum of the digits of `fill`, so `fill` alone identifies the state.)

### 3. What are the choices?
Put `nums[i]` into any slot `s + 1` whose digit is less than 2.

### 4. What is the recurrence?

```
f(i, fill) = max over slots s with digit_s(fill) < 2 of
             (nums[i] AND (s + 1))  +  f(i + 1, fill + 3ˢ)
```

Adding `3ˢ` increments digit `s` by one.

### 5. What are the base cases?
`f(n, fill) = 0`: every number is placed. Slots may stay empty or half full; that is allowed.

### 6. What is the order of computation?
Top-down: each call increments one digit, so `fill` strictly grows. Bottom-up (two-seat version
below): process masks in increasing numeric order; removing a bit gives a smaller mask.

### 7. Can the state space be optimized?
`3⁹ = 19 683` states, already small. The alternative **two seats per slot** view trades a slightly
larger state for simpler code: split slot `t` into seats `2t−2` and `2t−1`; each seat holds at
most one number, so the taken seats form an ordinary bitmask of `2 · numSlots` bits
(`2¹⁸ = 262 144` masks). If `mask` has `c` bits set, the first `c` numbers are seated, so `i` is
again implied.

```
dp[mask] = max over seats b in mask of  dp[mask without b] + (nums[c−1] AND slotOf(b)),
           c = bitCount(mask),  slotOf(b) = b / 2 + 1
```

## Recursive solution

```java
int solve(int index, int fill) {
    if (index == n) return 0;
    int best = 0;
    for (int s = 0; s < slots; s++) {
        int countInSlot = (fill / powerOf3[s]) % 3;
        if (countInSlot == 2) continue;
        best = Math.max(best, (nums[index] & (s + 1)) + solve(index + 1, fill + powerOf3[s]));
    }
    return best;
}
```

## Memoization

`memo[fill]` (size `3^numSlots`), `-1` = unknown. Reading digit `s` costs one division and one
modulo. See [`solution/Memoization.java`](solution/Memoization.java); `main` also compares with an
exhaustive search on 300 random small inputs.

## Tabulation

The two-seat bitmask version, filled for `mask = 1 … 2^(2S) − 1` in increasing order:
[`solution/Tabulation.java`](solution/Tabulation.java). Masks with more set bits than `n` are
skipped; the answer is the best `dp[mask]` with exactly `n` bits set.

## Space optimization

Neither table can be rolled: a state may be reached from any smaller one. The sizes (`3⁹` ints or
`2¹⁸` ints ≈ 1 MB) are fine. The base-3 encoding is the more compact one: it does not distinguish
"seat A then seat B" from "seat B then seat A" inside a slot.

## Dry run

`nums = [1, 3, 2]`, `numSlots = 2`. Fill digits are written `[slot1, slot2]`.

**Memoization** (values computed bottom of the recursion first):

| fill | next number | options (gain + f(child)) | f(fill) |
|---|---|---|---|
| [1,0] | 3 | slot 1: 3&1 = 1 + f([2,0]) = 1 + 2 → 3; slot 2: 3&2 = 2 + f([1,1]) = 2 + 2 → 4 | 4 |
| [0,1] | 3 | slot 1: 1 + f([1,1]) = 1 + 2 → 3; slot 2: 2 + f([0,2]) = 2 + 0 → 2 | 3 |
| [0,0] | 1 | slot 1: 1&1 = 1 + f([1,0]) = 5; slot 2: 1&2 = 0 + f([0,1]) = 3 | **5** |

(`f([2,0])` = 2: the 2 must go to slot 2. `f([1,1])` = 2: the 2 goes to slot 2. `f([0,2])` = 0: the 2
must go to slot 1, and 2 AND 1 = 0.)

**Tabulation** (seats 0, 1 = slot 1; seats 2, 3 = slot 2; mask written seat3..seat0):

| mask | numbers seated | dp[mask] | how |
|---|---|---|---|
| 0001 | 1 | 1 | 1 in slot 1 |
| 0100 | 1 | 0 | 1 in slot 2 |
| 0101 | 1, 3 | 3 | 1 in slot 1 (1) + 3 in slot 2 (2) |
| 1100 | 1, 3 | 2 | both in slot 2: 0 + 2 |
| 1101 | 1, 3, 2 | **5** | from 0101 (3) + 2 in seat 3 → slot 2: 2&2 = 2 |
| 1110 | 1, 3, 2 | 5 | symmetric seating, same value |

## Complexity

| Approach | Time | Space | Reason |
|---|---|---|---|
| Brute force | O(S^n) | O(n) | every assignment |
| Memoization (base 3) | O(3^S · S) | O(3^S) | 19 683 states × 9 slot choices |
| Tabulation (2 seats) | O(4^S · 2S) | O(4^S) | 262 144 masks × 18 seats |

Both are under 5 million steps.

## Why the optimized approach is better

The DP replaces "which number went where" (exponential in `n`) with "how full is each slot"
(exponential only in the small number of slots). The two encodings show a general trick: a
resource with capacity `c` can be a base-`(c+1)` digit **or** `c` separate binary seats.

## Common mistakes

- **Using a binary mask with one bit per slot**: that only allows capacity 1.
- **Slots are 1-indexed** in the AND: use `s + 1`, not `s`.
- **Requiring every slot to be full.** Slots may stay empty; only the numbers must be placed.
- **In the seat version, taking the wrong number**: the number placed last is `nums[bitCount − 1]`.

## Key takeaway

For "assign items to a few resources with small capacities", the state is the **fill vector** of
the resources. Encode it as base-`(capacity + 1)` digits, or duplicate each resource into
`capacity` binary seats and use an ordinary bitmask.

## Related problems

- [1879. Minimum XOR Sum of Two Arrays](https://leetcode.com/problems/minimum-xor-sum-of-two-arrays/): the capacity-1 version (one bit per slot).
- [1986. Minimum Number of Work Sessions to Finish the Tasks](https://leetcode.com/problems/minimum-number-of-work-sessions-to-finish-the-tasks/): bitmask DP with a capacity per session.
- [1178. Number of Valid Words for Each Puzzle](../1178-number-of-valid-words-for-each-puzzle/): bitmasks used for set queries rather than as DP state.
