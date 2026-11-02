# 1155. Number of Dice Rolls With Target Sum

> **Pattern:** Counting knapsack: "items" are dice, each must contribute exactly one face value · **Builds on:** [Subset Sum](../../../03-subsequence-and-knapsack-dp/03-subset-sum/) · **Difficulty:** Medium

## Problem overview

You roll `n` dice, each with faces `1..k`. Count the ordered outcomes (die 1 shows this, die 2
shows that, …) whose faces add up to exactly `target`. Return the count modulo 10⁹ + 7.

## Link & metadata

| Field | Value |
|---|---|
| LeetCode | [1155. Number of Dice Rolls With Target Sum](https://leetcode.com/problems/number-of-dice-rolls-with-target-sum/) |
| Difficulty | Medium |
| Pattern | Knapsack-style counting over (items processed, remaining sum); each item has k options instead of take/skip |
| Prerequisite topic | [`03-subsequence-and-knapsack-dp/03-subset-sum`](../../../03-subsequence-and-knapsack-dp/03-subset-sum/) (and [`02-zero-one-knapsack`](../../../03-subsequence-and-knapsack-dp/02-zero-one-knapsack/)) |
| Why this pattern | Subset Sum asks "can items 0..i reach sum t?"; here we ask "how many ways can dice 1..i reach sum t?", with every die forced to contribute 1..k |

## Examples

| n, k, target | Answer | Why |
|---|---|---|
| 1, 6, 3 | 1 | the single die shows 3 |
| 2, 6, 7 | 6 | (1,6) (2,5) (3,4) (4,3) (5,2) (6,1) |
| 1, 2, 3 | 0 | a two-sided die cannot show 3 |
| 30, 30, 500 | 222616187 | (mod 10⁹+7) |

## Brute-force intuition

Try every face for every die: kⁿ outcomes (30³⁰ in the worst case). However, after deciding the
first `i` dice, the only thing that matters for the rest is **how much of the target is left**.
So there are only `n × target` distinct subproblems.

```
ways(dice=2, left=7)
 ├─ face 1 → ways(1, 6) = 1
 ├─ face 2 → ways(1, 5) = 1
 ├─ ...
 └─ face 6 → ways(1, 1) = 1          total 6
```

## Deriving the DP (7 questions)

### 1. What is changing?
The number of dice still to roll `i`, and the sum still needed `t`. (Same two dimensions as
knapsack: items, capacity.)

### 2. What does the state represent?
`dp[i][t]` = the number of ways that `i` dice can show faces summing to exactly `t`.

### 3. What are the choices?
The face `f` of the current die: `1, 2, …, k` (only if `f ≤ t`).

### 4. What is the recurrence?
```
dp[i][t] = Σ for f = 1..min(k, t) of dp[i-1][t-f]        (mod 10^9+7)
```
Different faces give different outcomes, so we add.

### 5. What are the base cases?
`dp[0][0] = 1`: zero dice make sum 0 in exactly one way (roll nothing). `dp[0][t > 0] = 0`.

### 6. What is the order of computation?
Row `i` needs row `i - 1`: loop `i = 1..n` outside, `t` inside (any direction, since we read the
previous row only).

### 7. Can the state space be optimized?
- **Space:** only row `i - 1` is read → two 1D arrays.
- **Time:** `dp[i][t]` is a **sum of a window** `dp[i-1][t-k .. t-1]`. Moving from `t` to `t+1`
  adds one element and removes one, so a running window sum gives each cell in O(1). Total
  O(n · target) instead of O(n · target · k).

## Recursive solution

```java
static long ways(int dice, int remaining) {
    if (dice == 0) return remaining == 0 ? 1 : 0;
    long total = 0;
    for (int face = 1; face <= k && face <= remaining; face++)
        total = (total + ways(dice - 1, remaining - face)) % MOD;
    return total;
}
```

## Memoization

Cache `ways(dice, remaining)`. See [`solution/Memoization.java`](solution/Memoization.java).

## Tabulation

`dp[n+1][target+1]`, filled row by row. See [`solution/Tabulation.java`](solution/Tabulation.java).

## Space optimization

Two rows plus a sliding window sum:

```
previous: t-k-1 [t-k ........ t-1]  t
                 └── window ───┘      current[t] = window
moving to t+1:  add previous[t], drop previous[t-k]
```

See [`solution/Optimized.java`](solution/Optimized.java).

## Dry run

`n = 2, k = 6, target = 7`.

| i \ t | 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 |
|---|---|---|---|---|---|---|---|---|
| 0 | 1 | 0 | 0 | 0 | 0 | 0 | 0 | 0 |
| 1 | 0 | 1 | 1 | 1 | 1 | 1 | 1 | 0 |
| 2 | 0 | 0 | 1 | 2 | 3 | 4 | 5 | **6** |

`dp[2][7] = dp[1][6] + dp[1][5] + … + dp[1][1] = 6`. `dp[1][7] = 0` because one die cannot show 7.

## Complexity

| Approach | Time | Space | Reason |
|---|---|---|---|
| Brute force | O(kⁿ) | O(n) | every outcome |
| Memoization | O(n·target·k) | O(n·target) | n·target states × k faces |
| Tabulation | O(n·target·k) | O(n·target) | same three loops |
| Optimized | O(n·target) | O(target) | window sum makes each cell O(1); two rows |

## Why the optimized approach is better

For `n = k = 30`, `target = 1000`, it does 3·10⁴ instead of 9·10⁵ steps and stores two rows.
The window-sum trick works whenever a transition sums a contiguous range of the previous row.

## Common mistakes

- **Base case `dp[0][t] = 1` for all t.** Only sum 0 is reachable with zero dice.
- **Letting a die show 0.** Faces start at 1.
- **Forgetting mod when subtracting** in the window version: add `MOD` before `%`.

## Key takeaway

Knapsack is not only "take or skip". When each item must contribute one of several values, the
state is still (items done, amount left) and the choice loop just runs over those values.

## Related problems

- [494. Target Sum](https://leetcode.com/problems/target-sum/), counting with a forced ± choice per item
- [377. Combination Sum IV](../377-combination-sum-iv/), counting ordered sequences without a fixed number of items
- Previous pattern: [1289. Minimum Falling Path Sum II](../../02-grid/1289-minimum-falling-path-sum-ii/) · Next: [377. Combination Sum IV](../377-combination-sum-iv/)
