# 464. Can I Win

> **Pattern:** Game DP (win/lose states) + bitmask memo · **Difficulty:** Medium
> **Builds on:** [Bitmask DP](../../../04-advanced-dp-patterns/09-bitmask-dp/) and [State representation](../../../00-foundations/09-state-representation/)

---

## Problem overview

Two players take turns picking a number from `1 … maxChoosableInteger`; a number cannot be picked twice. Each pick is added to a shared running total. The player whose pick makes the total reach or exceed `desiredTotal` wins. Both play perfectly. Does the first player have a winning strategy? (`maxChoosableInteger ≤ 20`, `desiredTotal ≤ 300`.)

**Why bitmask + game DP.** The position of the game is fully described by *which numbers are gone*: that set fits into 20 bits, giving `2^20 ≈ 10^6` positions. In a game, a position is **winning for the player to move** if some move leads to a position that is **losing for the opponent**. That "win if any child is a loss" rule is a DP over positions, and the set of positions is a [bitmask](../../../04-advanced-dp-patterns/09-bitmask-dp/).

## Link & metadata

| Field | Value |
|---|---|
| LeetCode | [464. Can I Win](https://leetcode.com/problems/can-i-win/) |
| Difficulty | Medium |
| Pattern | Minimax game DP over a bitmask of used numbers |
| Prerequisite topics | [Bitmask DP](../../../04-advanced-dp-patterns/09-bitmask-dp/), [State representation](../../../00-foundations/09-state-representation/), [Memoization](../../../00-foundations/06-memoization/) |

Bit `i − 1` set in `mask` means "number `i` has been picked" (`mask & (1 << (i-1))` tests it, `mask | (1 << (i-1))` adds it).

## Examples

| max | desired | Answer | Why |
|---|---|---|---|
| 10 | 11 | false | whatever P1 picks (`x`), P2 picks `11 − x` (≤ 10) and wins |
| 10 | 0 | true | the total already reaches 0 — first player wins immediately |
| 3 | 5 | true | P1 picks 1; P2 cannot reach 4 with 2 or 3, and then P1 finishes |
| 3 | 4 | false | every first pick lets P2 finish |
| 5 | 50 | false | `1+…+5 = 15 < 50`: nobody can ever win, so P1 does not win |

## Brute-force intuition

Explore the full game tree: P1 tries each number, then P2 tries each remaining number, and so on. That is up to `20!` move sequences. But sequences `1 then 2` and `2 then 1` lead to **the same position** (same numbers used, same total), so we keep solving the same positions.

## Deriving the DP (7 questions)

### 1. What is changing?
The set of numbers already used. The running total looks like a second variable, but it is **determined by the mask**: total = sum of the numbers in the mask. So the mask alone is the state. Also, who is to move does not need storing: we always evaluate "the player to move".

### 2. What does the state represent?
`canWin(mask)` = true if the player **about to move**, with the numbers in `mask` already used, can force a win.

### 3. What are the choices?
Pick any unused number `i`.

### 4. What is the recurrence?
With `remaining = desiredTotal − sum(mask)`:
```
canWin(mask) = OR over unused i of ( i >= remaining  OR  NOT canWin(mask | bit(i)) )
```
Either the pick ends the game right now, or it hands the opponent a position from which *they* cannot win.

### 5. What are the base cases?
- Pre-checks: `desiredTotal ≤ 0` → true. `1 + … + max < desiredTotal` → false (nobody can win).
- Inside the recursion: if no unused number satisfies the formula, the result is false (all moves lose). Running out of numbers cannot happen before the total is reached, thanks to the second pre-check.

### 6. What is the order of computation?
`canWin(mask)` reads only masks with one more bit (numerically larger). Bottom-up: loop `mask` from `2^max − 1` **down to** 0.

### 7. Can the state space be optimized?
`2^20` booleans (1 MB) are required. Bottom-up we also need `sum(mask)`, computed incrementally (`sum[mask] = sum[mask without lowest bit] + that number`). Top-down usually visits far fewer states thanks to short-circuiting `OR`, which is why the memoized version is the practical one. No separate `Optimized.java`.

## Recursive solution

```java
boolean canWin(int mask, int remaining) {
    for (int i = 1; i <= max; i++) {
        int bit = 1 << (i - 1);
        if ((mask & bit) != 0) continue;
        if (i >= remaining || !canWin(mask | bit, remaining - i)) return true;
    }
    return false;
}
```
`remaining` is passed only for convenience — it is a function of `mask`, so the cache key is `mask` alone.

## Memoization

`Boolean[] memo = new Boolean[1 << max]` (`null` = unknown). See [`solution/Memoization.java`](solution/Memoization.java).

## Tabulation

Precompute `sum[mask]`, then fill `win[mask]` for masks in decreasing order. Masks whose sum already reaches the target are never a "player to move" position; their value is never consulted because the `i >= remaining` test fires first. See [`solution/Tabulation.java`](solution/Tabulation.java).

## Space optimization

Not applicable — every subset may be needed.

## Dry run

`max = 3`, `desired = 5`, bottom-up (masks as sets; W = player to move wins, L = loses):

| mask | sum | remaining | moves examined | result |
|---|---|---|---|---|
| {1,3} | 4 | 1 | 2 ≥ 1 → immediate win | W |
| {1,2} | 3 | 2 | 3 ≥ 2 → immediate win | W |
| {3} | 3 | 2 | 1: opponent at {1,3}=W ✗; 2 ≥ 2 ✓ | W |
| {2} | 2 | 3 | 3 ≥ 3 ✓ | W |
| {1} | 1 | 4 | 2: opponent at {1,2}=W ✗; 3: opponent at {1,3}=W ✗ | **L** |
| {} | 0 | 5 | 1: opponent at {1}=L ✓ | **W** |

So the first player wins by picking 1.

## Complexity

| Approach | Time | Space | Why |
|---|---|---|---|
| Full game tree | O(n!) | O(n) | every move order |
| Memoization | O(2ⁿ·n) worst case | O(2ⁿ) | each mask tries n numbers once; usually far fewer masks are reached |
| Tabulation | O(2ⁿ·n) | O(2ⁿ) | every mask is computed |

With `n = 20`: about `2·10^7` simple steps in the worst case.

## Why the optimized approach is better

Order-independence of the game position collapses `n!` move sequences into `2^n` sets.

## Common mistakes

- Using `(mask, remaining)` as a 2D cache key — wasteful, `remaining` is implied by `mask`.
- Forgetting the pre-check `sum < desiredTotal`. Without it the recursion runs out of numbers, scores that as a loss for the player to move, and so wrongly declares the *other* player a winner of a game nobody can win.
- Forgetting `desiredTotal ≤ 0 → true`.
- Bottom-up in increasing mask order: children (larger masks) would not be ready.

## Key takeaway

Game DP: **win(position) = some move leads to lose(opponent's position)**. If the position is "which items are used", make it a bitmask, and drop anything that the mask already determines.

## Related problems

- [Bitmask DP](../../../04-advanced-dp-patterns/09-bitmask-dp/) — the topic lesson.
- [526. Beautiful Arrangement](../526-beautiful-arrangement/) — same mask of used numbers, counting instead of win/lose.
- [698. Partition to K Equal Sum Subsets](../698-partition-to-k-equal-sum-subsets/) — another case where the mask determines a running sum.
