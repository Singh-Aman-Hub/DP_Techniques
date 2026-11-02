# 279. Perfect Squares

> **Pattern:** 1D minimum-count DP with unlimited reuse (unbounded "coin change" in 1D) · **Builds on:** [Climbing Stairs](../../../01-one-dimensional-dp/02-climbing-stairs/) · **Difficulty:** Medium

## Problem overview

Given a positive integer `n`, return the fewest perfect squares (1, 4, 9, 16, …) that add up to
exactly `n`. A square may be used more than once.

## Link & metadata

| Field | Value |
|---|---|
| LeetCode | [279. Perfect Squares](https://leetcode.com/problems/perfect-squares/) |
| Difficulty | Medium |
| Pattern | 1D DP where `dp[i]` looks back by every allowed "step size" (here: every square ≤ i) |
| Prerequisite topic | [`01-one-dimensional-dp/02-climbing-stairs`](../../../01-one-dimensional-dp/02-climbing-stairs/) |
| Why this pattern | Climbing stairs with step sizes 1, 4, 9, 16, … and the question "fewest steps to reach the top" |

This is also exactly [Coin Change](../../../03-subsequence-and-knapsack-dp/08-coin-change/) with
the coins being the squares. You can solve it now with 1D thinking and revisit it later as an
unbounded knapsack.

## Examples

| n | Answer | Why |
|---|---|---|
| 12 | 3 | 4 + 4 + 4 (greedy 9 + 1 + 1 + 1 uses 4) |
| 13 | 2 | 4 + 9 |
| 7 | 4 | 4 + 1 + 1 + 1, no shorter way |
| 1 | 1 | 1 |

## Brute-force intuition

Pick the last square `k²` in the sum; the rest must sum to `n - k²`, and we want *that* to use
as few squares as possible. Recurse for every `k`. The tree for `n = 12` re-asks `best(8)`,
`best(3)`, `best(7)` … many times:

```
best(12)
 ├─ -1 → best(11) ─ -1 → best(10) ...
 ├─ -4 → best(8)  ─ -1 → best(7) ... , -4 → best(4)
 └─ -9 → best(3)  ─ -1 → best(2) ─ -1 → best(1) ─ -1 → best(0)
```

Greedy (always take the biggest square) fails: for 12 it gives 9+1+1+1 = 4 squares instead of 3.
When greedy fails and subproblems repeat, reach for DP.

## Deriving the DP (7 questions)

### 1. What is changing?
The remaining amount `i` we still need to express, from `n` down to 0.

### 2. What does the state represent?
`dp[i]` = the minimum number of perfect squares that sum to exactly `i`.

### 3. What are the choices?
The last square used: any `k²` with `1 ≤ k² ≤ i`.

### 4. What is the recurrence?
```
dp[i] = 1 + min over k with k*k ≤ i of dp[i - k*k]
```
The `1` counts the square `k²` we just chose; `dp[i - k²]` is the best way to build the rest.

### 5. What are the base cases?
`dp[0] = 0`: zero needs no squares. Every positive `i` is reachable (1 is a square), so no
"impossible" value is needed.

### 6. What is the order of computation?
`dp[i]` reads smaller indices only, so loop `i = 1 … n` upward.

### 7. Can the state space be optimized?
The look-back distance `k²` goes up to `i`, so the whole array is needed for the DP. But a
mathematical shortcut exists: by **Lagrange's four-square theorem** every positive integer is a sum
of at most 4 squares, and **Legendre's three-square theorem** says it needs exactly 4 iff
`n = 4^a · (8b + 7)`. That gives an O(√n), O(1)-space answer (see Space optimization).

## Recursive solution

```java
static int best(int remaining) {
    if (remaining == 0) return 0;
    int answer = Integer.MAX_VALUE;
    for (int k = 1; k * k <= remaining; k++)
        answer = Math.min(answer, 1 + best(remaining - k * k));
    return answer;
}
```

## Memoization

Cache `best(remaining)`. See [`solution/Memoization.java`](solution/Memoization.java).

## Tabulation

See [`solution/Tabulation.java`](solution/Tabulation.java).

```java
dp[0] = 0;
for (int i = 1; i <= n; i++) {
    dp[i] = Integer.MAX_VALUE;
    for (int k = 1; k * k <= i; k++) dp[i] = Math.min(dp[i], dp[i - k * k] + 1);
}
```

## Space optimization

The DP itself cannot drop below O(n) memory, so [`solution/Optimized.java`](solution/Optimized.java)
uses number theory instead:

```
1. n is a perfect square                    → 1
2. n = a² + b² for some a                   → 2
3. after removing factors of 4, n % 8 == 7  → 4
4. otherwise                                → 3
```

This is a nice reminder that DP is a general tool, not always the fastest. Learn the DP first:
in an interview, it is the version you can derive and justify on the spot.

## Dry run

`n = 12`. For each `i`, the candidates are `dp[i - 1] + 1`, `dp[i - 4] + 1`, `dp[i - 9] + 1`.

| i | i-1 | i-4 | i-9 | dp[i] |
|---|---|---|---|---|
| 0 | | | | 0 |
| 1 | dp0+1=1 | | | 1 |
| 2 | dp1+1=2 | | | 2 |
| 3 | dp2+1=3 | | | 3 |
| 4 | dp3+1=4 | dp0+1=**1** | | 1 |
| 5 | dp4+1=2 | dp1+1=2 | | 2 |
| 6 | dp5+1=3 | dp2+1=3 | | 3 |
| 7 | dp6+1=4 | dp3+1=4 | | 4 |
| 8 | dp7+1=5 | dp4+1=**2** | | 2 |
| 9 | dp8+1=3 | dp5+1=3 | dp0+1=**1** | 1 |
| 10 | dp9+1=2 | dp6+1=4 | dp1+1=2 | 2 |
| 11 | dp10+1=3 | dp7+1=5 | dp2+1=3 | 3 |
| 12 | dp11+1=4 | dp8+1=**3** | dp3+1=4 | **3** |

## Complexity

| Approach | Time | Space | Reason |
|---|---|---|---|
| Brute force | exponential | O(n) | √i branches at every level |
| Memoization | O(n√n) | O(n) | n states, each tries ≤ √n squares |
| Tabulation | O(n√n) | O(n) | same double loop, no recursion |
| Optimized (math) | O(√n) | O(1) | at most √n checks for the two-square case |

## Why the optimized approach is better

It replaces 10⁴ × 100 = 10⁶ operations by about 100. But it only works because of a theorem
specific to squares; the DP works for any set of "coins".

## Common mistakes

- **Greedy.** 12 → 9+1+1+1 is wrong.
- **Initializing `dp[i]` to 0 instead of a large value** before taking the minimum.
- **Loop condition `k < i`** instead of `k * k <= i`; it misses `i` itself being a square.

## Key takeaway

"Fewest pieces to make a total, pieces reusable" = `dp[total] = 1 + min(dp[total - piece])`.
It is climbing stairs with custom step sizes, and coin change with custom coins.

## Related problems

- [322. Coin Change](https://leetcode.com/problems/coin-change/), taught in [`08-coin-change`](../../../03-subsequence-and-knapsack-dp/08-coin-change/)
- [139. Word Break](../139-word-break/), "which piece came last?" on a string
- Previous: [139. Word Break](../139-word-break/) · Next: [152. Maximum Product Subarray](../152-maximum-product-subarray/)
