# 879. Profitable Schemes

> **Pattern:** 0/1 knapsack counting with one capacity (members) and one **"at least"** target (profit) · **Builds on:** [0/1 Knapsack](../../../03-subsequence-and-knapsack-dp/02-zero-one-knapsack/) · **Difficulty:** Hard

## Problem overview

A gang has `n` members. Crime `i` needs `group[i]` members and yields `profit[i]`. A member can
take part in at most one crime. A **scheme** is any subset of crimes using at most `n` members in
total and earning **at least** `minProfit`. Count the schemes, modulo 10⁹ + 7.

## Link & metadata

| Field | Value |
|---|---|
| LeetCode | [879. Profitable Schemes](https://leetcode.com/problems/profitable-schemes/) |
| Difficulty | Hard |
| Pattern | Counting 0/1 knapsack; the profit dimension is **clamped** at `minProfit` |
| Prerequisite topic | [`03-subsequence-and-knapsack-dp/02-zero-one-knapsack`](../../../03-subsequence-and-knapsack-dp/02-zero-one-knapsack/) (and [`06-target-sum`](../../../03-subsequence-and-knapsack-dp/06-target-sum/) for counting subsets) |
| Why this pattern | Each crime is taken or skipped once, under a member capacity, like a knapsack item. Profit is a second dimension, like the zeros/ones in [474](../474-ones-and-zeroes/) |

## Examples

| n | minProfit | group | profit | Answer | Schemes |
|---|---|---|---|---|---|
| 5 | 3 | [2,2] | [2,3] | 2 | {1}, {0,1} |
| 10 | 5 | [2,3,5] | [6,7,8] | 7 | every non-empty subset (each earns ≥ 6, all fit in 10) |
| 1 | 0 | [2] | [1] | 1 | only the empty scheme (profit 0 ≥ 0) |
| 1 | 1 | [1,1,1] | [1,1,1] | 3 | any single crime |

## Brute-force intuition

Try all 2^L subsets, check members and profit. L = 100 makes that impossible. The future depends
only on (crime index, members still free, profit still needed), so memoize. But profit can
reach 100·100 = 10⁴. The Hard insight is that **profit beyond `minProfit` does not matter**: a
scheme that already has enough profit stays valid whatever else it adds. So the profit
dimension only needs values `0..minProfit`.

```
profit needed:  3 → take crime worth 5 → needed = max(0, 3 - 5) = 0   (clamped, not -2)
```

## Deriving the DP (7 questions)

### 1. What is changing?
The crime index `i`, the members still available, and the profit still needed.

### 2. What does the state represent?
Tabulation form: `dp[i][m][p]` = the number of subsets of the first `i` crimes that use
**exactly** `m` members and earn **at least** `p` profit (for `p` in `0..minProfit`).

### 3. What are the choices?
For crime `i-1`: **skip** it, or **commit** it if `group[i-1] ≤ m`.

### 4. What is the recurrence?
```
dp[i][m][p] = dp[i-1][m][p]                                         // skip
            + dp[i-1][m - g][ max(0, p - pr) ]   if m ≥ g          // commit (g members, pr profit)
answer      = Σ over m = 0..n of dp[L][m][minProfit]
```
The `max(0, …)` is the clamp: if this crime alone covers the needed profit, the rest only
needs "at least 0", which every subset satisfies.

### 5. What are the base cases?
`dp[0][0][0] = 1`: the empty set uses 0 members and earns 0, which is "at least 0". Every other
`dp[0][m][p]` is 0.

### 6. What is the order of computation?
Crimes outside (layer `i` reads layer `i-1`), then members and profit in any order.

### 7. Can the state space be optimized?
Drop the crime index and keep `dp[m][p]`, iterating `m` **downward** (0/1 rule): the commit term
reads row `m - g`, which must still hold the value from before this crime.

## Recursive solution

```java
// schemes using crimes i.., with membersLeft free, still needing `need` profit
static long count(int i, int membersLeft, int need) {
    if (i == L) return need == 0 ? 1 : 0;
    long ways = count(i + 1, membersLeft, need);                           // skip
    if (group[i] <= membersLeft)
        ways += count(i + 1, membersLeft - group[i], Math.max(0, need - profit[i]));  // commit
    return ways % MOD;
}
```

## Memoization

Cache by `(i, membersLeft, need)`: `L × (n+1) × (minProfit+1)` states. See
[`solution/Memoization.java`](solution/Memoization.java).

## Tabulation

See [`solution/Tabulation.java`](solution/Tabulation.java).

## Space optimization

```java
for (each crime with g members, pr profit)
    for (int m = n; m >= g; m--)                // downward: each crime used once
        for (int p = 0; p <= minProfit; p++)
            dp[m][p] = (dp[m][p] + dp[m - g][Math.max(0, p - pr)]) % MOD;
```

See [`solution/Optimized.java`](solution/Optimized.java).

## Dry run

`n = 5, minProfit = 3, group = [2,2], profit = [2,3]`. Rows `m` (members used), columns `p` (profit at least p).
Rows not shown are all 0.

Start: `dp[0] = [1, 0, 0, 0]`

Crime 0 (g = 2, pr = 2): `dp[2][p] += dp[0][max(0, p-2)]`

| m | p=0 | p=1 | p=2 | p=3 |
|---|---|---|---|---|
| 0 | 1 | 0 | 0 | 0 |
| 2 | 1 | 1 | 1 | 0 |

Crime 1 (g = 2, pr = 3), `m` from 5 down to 2:
- `m = 4`: `dp[4][p] += dp[2][max(0, p-3)]` = `dp[2][0]` = 1 for every p → `[1, 1, 1, 1]`
- `m = 2`: `dp[2][p] += dp[0][max(0, p-3)]` = `dp[0][0]` = 1 for every p → `[2, 2, 2, 1]`

| m | p=0 | p=1 | p=2 | p=3 |
|---|---|---|---|---|
| 0 | 1 | 0 | 0 | 0 |
| 2 | 2 | 2 | 2 | **1** |
| 4 | 1 | 1 | 1 | **1** |

Answer = column `p = 3` summed = 1 + 1 = **2**: `{crime 1}` (2 members) and `{crime 0, crime 1}` (4 members).

## Complexity

Let `L` = number of crimes, `P = minProfit`.

| Approach | Time | Space | Reason |
|---|---|---|---|
| Brute force | O(2^L · L) | O(L) | all subsets |
| Memoization | O(L·n·P) | O(L·n·P) | each state O(1) |
| Tabulation | O(L·n·P) | O(L·n·P) | three loops |
| Optimized | O(L·n·P) | O(n·P) | one 2D layer |

## Why the optimized approach is better

With `L = n = P = 100`, the 3D table has 10⁶ entries; the 2D layer 10⁴. Without the clamp, the
profit axis would need 10⁴ values and the time would be 100× larger.

## Common mistakes

- **Not clamping profit**, making the state space huge or indexing negative.
- **Answer = `dp[n][minProfit]` only.** `m` means "exactly m members"; sum over all `m ≤ n`.
  (Alternatively define `m` as "at most", then the single cell is right. Pick one meaning and stick with it.)
- **Forgetting the empty scheme** when `minProfit = 0`.
- **Iterating members upward** in the 2D version (reuses a crime).

## Key takeaway

When a constraint is "at least X", everything above X is equivalent. Clamp that dimension to
`0..X` with `max(0, need - gain)`, which often shrinks a Hard state space to a Medium one.

## Related problems

- [474. Ones and Zeroes](../474-ones-and-zeroes/), two-dimensional 0/1 knapsack (max instead of count)
- [494. Target Sum](https://leetcode.com/problems/target-sum/), counting subsets with a sum constraint
- Previous: [474. Ones and Zeroes](../474-ones-and-zeroes/) · Next pattern: [935. Knight Dialer](../../05-stocks-and-state-machines/935-knight-dialer/)
