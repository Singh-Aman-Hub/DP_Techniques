# 526. Beautiful Arrangement

> **Pattern:** Bitmask DP over "which items are already used" (permutation counting) · **Difficulty:** Medium
> **Builds on:** [Bitmask DP](../../../04-advanced-dp-patterns/09-bitmask-dp/)

---

## Problem overview

Arrange the numbers `1 … n` in positions `1 … n` (each used once). An arrangement is *beautiful* if, at every position `i`, the number placed there divides `i` or is divisible by `i`. Count the beautiful arrangements (`n ≤ 15`).

**Why bitmask DP.** We fill positions left to right. What do we need to remember about the past? Not the *order* in which numbers were placed — only *which* numbers are already used. A set of up to 15 numbers fits in the bits of one integer (a **mask**), so there are only `2^15 = 32768` situations instead of `15!` orders. That "set of used items as an integer" is the core idea of [Bitmask DP](../../../04-advanced-dp-patterns/09-bitmask-dp/).

## Link & metadata

| Field | Value |
|---|---|
| LeetCode | [526. Beautiful Arrangement](https://leetcode.com/problems/beautiful-arrangement/) |
| Difficulty | Medium |
| Pattern | Bitmask DP, position derived from the mask |
| Prerequisite topics | [Bitmask DP](../../../04-advanced-dp-patterns/09-bitmask-dp/), [State representation](../../../00-foundations/09-state-representation/) |

**Bit operations used** (bit `k` stands for the number `k + 1`):

| Code | Meaning |
|---|---|
| `mask & (1 << k)` | is number `k+1` already used? (non-zero = yes) |
| `mask \| (1 << k)` | the same set with number `k+1` added |
| `Integer.bitCount(mask)` | how many numbers are used = how many positions are filled |
| `(1 << n) - 1` | the full set: all `n` numbers used |

## Examples

| n | Answer | Notes |
|---|---|---|
| 1 | 1 | `[1]` |
| 2 | 2 | `[1,2]` and `[2,1]` (1 divides everything) |
| 3 | 3 | `[1,2,3]`, `[2,1,3]`, `[3,2,1]` |
| 4 | 8 | |
| 15 | 24679 | largest input |

## Brute-force intuition

Generate all `n!` permutations and test each one — `15! ≈ 1.3·10^12`. Backtracking with pruning (only place valid numbers) helps, but many different partial orders reach the *same* set of used numbers at the same position, and the future is identical for all of them.

## Deriving the DP (7 questions)

### 1. What is changing?
The set of numbers already placed. The next position is not a separate variable: it equals `bitCount(mask) + 1`.

### 2. What does the state represent?
`ways(mask)` = the number of ways to fill the **remaining** positions `bitCount(mask)+1 … n` with the numbers **not** in `mask`, keeping every position beautiful.

### 3. What are the choices?
Which unused number `k` goes into position `pos = bitCount(mask) + 1` — only those with `k % pos == 0` or `pos % k == 0`.

### 4. What is the recurrence?
```
ways(mask) = Σ over unused k with (k % pos == 0 || pos % k == 0) of ways(mask | bit(k))
```
Different choices for the current position lead to different arrangements, so we add.

### 5. What are the base cases?
`ways(full) = 1` — every position is filled; that is one complete beautiful arrangement.

### 6. What is the order of computation?
`ways(mask)` reads only masks with one more bit. Top-down handles this naturally. Bottom-up we flip the direction: `count[mask]` = ways to fill the **first** `bitCount(mask)` positions using exactly the numbers in `mask`. Every transition adds a bit, so the new mask is numerically larger: loop `mask = 0 … 2^n − 1` in increasing order.

### 7. Can the state space be optimized?
`2^n` states are needed (any subset can be the used set). The table cannot be layered more cheaply, so no `Optimized.java`. Precomputing which `(pos, k)` pairs are compatible saves time but not space.

## Recursive solution

```java
int ways(int mask) {
    if (mask == full) return 1;
    int pos = Integer.bitCount(mask) + 1;
    int total = 0;
    for (int k = 1; k <= n; k++)
        if ((mask & (1 << (k - 1))) == 0 && (k % pos == 0 || pos % k == 0))
            total += ways(mask | (1 << (k - 1)));
    return total;
}
```

## Memoization

Cache `ways(mask)` in an array of size `2^n` (`-1` = not computed). See [`solution/Memoization.java`](solution/Memoization.java).

## Tabulation

```
count[0] = 1
for mask in 0 .. 2^n - 1:
    pos = bitCount(mask) + 1
    for each unused k compatible with pos:
        count[mask | bit(k)] += count[mask]
answer = count[full]
```
See [`solution/Tabulation.java`](solution/Tabulation.java).

## Space optimization

Not applicable: the dependency is between arbitrary subsets, not neighbouring rows.

## Dry run

`n = 3` (masks written as sets), bottom-up. `pos` = position being filled from this mask:

| mask | `count` | pos | compatible unused numbers → push to |
|---|---|---|---|
| {} | 1 | 1 | 1, 2, 3 → {1}, {2}, {3} get +1 |
| {1} | 1 | 2 | 2 → {1,2} +1 (3 fails: 3∤2, 2∤3) |
| {2} | 1 | 2 | 1 → {1,2} +1 |
| {1,2} | 2 | 3 | 3 → {1,2,3} +2 |
| {3} | 1 | 2 | 1, 2 → {1,3} +1, {2,3} +1 |
| {1,3} | 1 | 3 | 2 fails → nothing |
| {2,3} | 1 | 3 | 1 → {1,2,3} +1 |
| {1,2,3} | **3** | – | full set |

## Complexity

| Approach | Time | Space | Why |
|---|---|---|---|
| All permutations | O(n!·n) | O(n) | every order is generated |
| Memoization | O(2ⁿ·n) | O(2ⁿ) | 2ⁿ masks, each tries n numbers |
| Tabulation | O(2ⁿ·n) | O(2ⁿ) | same states, iterative |

For `n = 15`: about `32768 · 15 ≈ 5·10^5` steps.

## Why the optimized approach is better

`n!` orders collapse into `2^n` sets because the future depends only on the set. That is the whole point of bitmask DP.

## Common mistakes

- Storing `pos` as a separate dimension: it is redundant (`bitCount(mask) + 1`).
- Mixing 0-based bits with 1-based numbers — decide that bit `k−1` means number `k` and stick to it.
- In tabulation, iterating masks in a different order. Increasing order is safe because adding a bit always increases the mask.

## Key takeaway

If the future depends only on **which** items are used (not their order) and there are ≤ ~20 items, make the used-set a bitmask. Often the "position" is just the popcount.

## Related problems

- [Bitmask DP](../../../04-advanced-dp-patterns/09-bitmask-dp/) — the topic lesson.
- [1879. Minimum XOR Sum of Two Arrays](../1879-minimum-xor-sum-of-two-arrays/) — the same "position = popcount" assignment DP, minimising instead of counting.
- [464. Can I Win](../464-can-i-win/) — mask of used numbers in a two-player game.
