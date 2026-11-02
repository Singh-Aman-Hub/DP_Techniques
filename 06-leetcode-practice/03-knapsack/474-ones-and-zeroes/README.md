# 474. Ones and Zeroes

> **Pattern:** 0/1 knapsack with a **two-dimensional capacity** · **Builds on:** [0/1 Knapsack](../../../03-subsequence-and-knapsack-dp/02-zero-one-knapsack/) · **Difficulty:** Medium

## Problem overview

You get a list of binary strings and two budgets: at most `m` zeros and at most `n` ones in
total. Pick the largest number of strings whose combined zeros and ones stay within both budgets.
Each string can be picked at most once.

## Link & metadata

| Field | Value |
|---|---|
| LeetCode | [474. Ones and Zeroes](https://leetcode.com/problems/ones-and-zeroes/) |
| Difficulty | Medium |
| Pattern | 0/1 knapsack: item = string, weight = (zeros, ones), value = 1 |
| Prerequisite topic | [`03-subsequence-and-knapsack-dp/02-zero-one-knapsack`](../../../03-subsequence-and-knapsack-dp/02-zero-one-knapsack/) |
| Why this pattern | Each string is taken or skipped once, under a capacity limit. The only change is that capacity has two coordinates, so the state gains a dimension |

## Examples

| strs | m, n | Answer | Why |
|---|---|---|---|
| `["10","0001","111001","1","0"]` | 5, 3 | 4 | {10, 0001, 1, 0}: 5 zeros, 3 ones |
| `["10","0","1"]` | 1, 1 | 2 | {0, 1} |
| `["111"]` | 1, 1 | 0 | needs 3 ones |

## Brute-force intuition

Every subset of strings: 2^L choices for `L` strings (L ≤ 600). Classic knapsack reasoning
applies. After deciding strings `0..i-1`, what matters for the rest is **which string we are at**
and **how many zeros and ones are still allowed**. That is `L × (m+1) × (n+1)` states.

Map to knapsack:

```
knapsack:      item i  | weight w[i]          | value v[i] | capacity W
this problem:  string i| (zeros[i], ones[i])  | 1          | (m, n)
```

## Deriving the DP (7 questions)

### 1. What is changing?
The index `i` of the string being considered, the zeros still allowed `z`, and the ones still
allowed `o`.

### 2. What does the state represent?
`dp[i][z][o]` = the largest number of strings choosable from the first `i` strings using at most
`z` zeros and `o` ones.

### 3. What are the choices?
For string `i-1` (with `c0` zeros and `c1` ones):
- **Skip** it.
- **Take** it, if `c0 ≤ z` and `c1 ≤ o`.

### 4. What is the recurrence?
```
dp[i][z][o] = max( dp[i-1][z][o],                          // skip
                   dp[i-1][z - c0][o - c1] + 1 )           // take, if it fits
```

### 5. What are the base cases?
`dp[0][z][o] = 0`: with no strings, nothing can be picked, whatever the budget.

### 6. What is the order of computation?
Layer `i` reads layer `i-1` only: strings outside, budgets inside.

### 7. Can the state space be optimized?
Drop the `i` dimension and keep `dp[z][o]`, **iterating `z` and `o` downward**. Going downward
means `dp[z - c0][o - c1]` still holds the value from *before* this string was considered, so
the string is used at most once. This is the same rule as 1D 0/1 knapsack, applied to both axes.

## Recursive solution

```java
static int best(int i, int zerosLeft, int onesLeft) {
    if (i == strs.length) return 0;
    int skip = best(i + 1, zerosLeft, onesLeft);
    int take = 0;
    if (zeros[i] <= zerosLeft && ones[i] <= onesLeft)
        take = 1 + best(i + 1, zerosLeft - zeros[i], onesLeft - ones[i]);
    return Math.max(skip, take);
}
```

## Memoization

Cache `best(i, zerosLeft, onesLeft)` in a 3D array. See
[`solution/Memoization.java`](solution/Memoization.java).

## Tabulation

`dp[L+1][m+1][n+1]`. See [`solution/Tabulation.java`](solution/Tabulation.java).

## Space optimization

```java
for (each string with c0 zeros, c1 ones)
    for (int z = m; z >= c0; z--)          // DOWNWARD: read old values
        for (int o = n; o >= c1; o--)
            dp[z][o] = Math.max(dp[z][o], dp[z - c0][o - c1] + 1);
```

See [`solution/Optimized.java`](solution/Optimized.java).

## Dry run

`strs = ["10","0","1"]`, `m = 1`, `n = 1`, optimized 2D table `dp[z][o]` (rows z = 0..1, cols o = 0..1):

| after string | (c0, c1) | dp[0][0] | dp[0][1] | dp[1][0] | dp[1][1] |
|---|---|---|---|---|---|
| start | | 0 | 0 | 0 | 0 |
| "10" | (1,1) | 0 | 0 | 0 | 1 |
| "0" | (1,0) | 0 | 0 | 1 | max(1, dp[0][1]+1) = 1 |
| "1" | (0,1) | 0 | 1 | 1 | max(1, dp[1][0]+1) = **2** |

While processing "1", `dp[1][1]` reads `dp[1][0] = 1`, the value left by the earlier strings,
so the step means "the best set without "1", plus "1"" → {0, 1}. Because the loops run downward,
a cell is always read before the current string can update it, so no string is counted twice.

## Complexity

Let `L` = number of strings, `S` = total characters.

| Approach | Time | Space | Reason |
|---|---|---|---|
| Brute force | O(2^L) | O(L) | all subsets |
| Memoization | O(S + L·m·n) | O(L·m·n) | count characters once; each state O(1) |
| Tabulation | O(S + L·m·n) | O(L·m·n) | three loops |
| Optimized | O(S + L·m·n) | O(m·n) | one 2D layer |

## Why the optimized approach is better

`L·m·n` can be 600·100·100 = 6·10⁶ ints (24 MB) for the 3D table; the 2D layer is 10⁴ ints.

## Common mistakes

- **Iterating budgets upward** in the 2D version: a string can then be taken twice (that is
  unbounded knapsack).
- **Treating it as "pick strings greedily by length".** Short strings may use the scarce digit.
- **Mixing up which budget belongs to zeros** (`m`) and which to ones (`n`).

## Key takeaway

A knapsack can have several capacities. Each one becomes a dimension of the state, and the
"iterate downward" rule applies to every capacity dimension.

## Related problems

- [879. Profitable Schemes](../879-profitable-schemes/), two dimensions again, with a *minimum* instead of a maximum on one of them
- [416. Partition Equal Subset Sum](https://leetcode.com/problems/partition-equal-subset-sum/), a 1-capacity 0/1 knapsack
- Previous: [377. Combination Sum IV](../377-combination-sum-iv/) · Next: [879. Profitable Schemes](../879-profitable-schemes/)
