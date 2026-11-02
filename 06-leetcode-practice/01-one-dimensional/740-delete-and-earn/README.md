# 740. Delete and Earn

> **Pattern:** 1D take/skip DP after a reduction · **Builds on:** [House Robber](../../../01-one-dimensional-dp/06-house-robber/) · **Difficulty:** Medium

## Problem overview

You have an array of positive integers. In one operation you pick a value `v`, earn `v` points,
and every copy of `v - 1` and `v + 1` is destroyed. Return the most points you can earn.

The trick of this problem is a **reduction**: once you see that it is House Robber in disguise,
the DP is one you have already written.

## Link & metadata

| Field | Value |
|---|---|
| LeetCode | [740. Delete and Earn](https://leetcode.com/problems/delete-and-earn/) |
| Difficulty | Medium |
| Pattern | Transform the input, then 1D "take or skip, no two adjacent" DP |
| Prerequisite topic | [`01-one-dimensional-dp/06-house-robber`](../../../01-one-dimensional-dp/06-house-robber/) |
| Why this pattern | Picking value `v` forbids values `v-1` and `v+1`, exactly like robbing house `i` forbids houses `i-1` and `i+1` |

## Examples

| nums | Answer | Why |
|---|---|---|
| `[3, 4, 2]` | 6 | take 4 (destroys 3), then take 2 → 4 + 2 |
| `[2, 2, 3, 3, 3, 4]` | 9 | take all three 3s (destroys 2s and 4) → 9 |
| `[1]` | 1 | only one value |

## Brute-force intuition

Two observations shrink the problem.

1. **If you take one copy of `v`, take all of them.** Taking the first copy already destroyed
   every `v-1` and `v+1`; the remaining copies of `v` cost nothing more. So the decision is per
   *value*, not per element.
2. **Bucket the values.** Let `points[v] = v × (count of v)`. Now the question is: choose a set
   of values, no two consecutive, with maximum total `points`.

```
nums   = [2, 2, 3, 3, 3, 4]
value  :   0   1   2   3   4
points : [ 0,  0,  4,  9,  4 ]     ← a street of houses; adjacent houses conflict
```

That is House Robber on `points`. A brute force tries "take" or "skip" for every value:
2^(maxValue) combinations, the same exponential tree as in House Robber.

## Deriving the DP (7 questions)

### 1. What is changing?
The value `v` we are deciding about, going from `maxValue` down to 0 (or up, for tabulation).

### 2. What does the state represent?
`dp[v]` = the maximum points obtainable using only values `0..v`.

### 3. What are the choices?
- **Skip** value `v`: the best is whatever `0..v-1` gives.
- **Take** value `v`: earn `points[v]`, and value `v-1` is now forbidden, so add the best of `0..v-2`.

### 4. What is the recurrence?
```
dp[v] = max( dp[v-1],              // skip v
             dp[v-2] + points[v] ) // take v, so v-1 is off-limits
```

### 5. What are the base cases?
`dp[0] = points[0] = 0` (there are no zeros, since nums are ≥ 1) and `dp[1] = max(points[0], points[1])`.
In the code we treat any negative index as 0 points, which makes the base cases automatic.

### 6. What is the order of computation?
`dp[v]` reads `dp[v-1]` and `dp[v-2]`, so loop `v` upward from 2 to `maxValue`.

### 7. Can the state space be optimized?
Only two previous values are read, so two variables suffice. The `points` bucket array itself
still needs O(maxValue) space; that is the price of the reduction.

## Recursive solution

```java
static int best(int v) {                 // best total using values 0..v
    if (v < 0) return 0;
    return Math.max(best(v - 1), best(v - 2) + points[v]);
}
```

## Memoization

Cache `best(v)`. See [`solution/Memoization.java`](solution/Memoization.java). Each value
0..maxValue is solved once.

## Tabulation

See [`solution/Tabulation.java`](solution/Tabulation.java).

```java
dp[0] = points[0];
dp[1] = Math.max(points[0], points[1]);
for (int v = 2; v <= maxValue; v++)
    dp[v] = Math.max(dp[v - 1], dp[v - 2] + points[v]);
```

## Space optimization

Keep `skipPrev2` (= dp[v-2]) and `bestPrev` (= dp[v-1]). See
[`solution/Optimized.java`](solution/Optimized.java).

## Dry run

`nums = [2, 2, 3, 3, 3, 4]`, `points = [0, 0, 4, 9, 4]`:

| v | points[v] | skip = dp[v-1] | take = dp[v-2] + points[v] | dp[v] |
|---|---|---|---|---|
| 0 | 0 | — | — | 0 |
| 1 | 0 | 0 | 0 + 0 = 0 | 0 |
| 2 | 4 | 0 | 0 + 4 = 4 | 4 |
| 3 | 9 | 4 | 0 + 9 = 9 | 9 |
| 4 | 4 | 9 | 4 + 4 = 8 | **9** |

Answer: 9 (take the 3s).

## Complexity

Let `n` = length of `nums`, `M` = largest value.

| Approach | Time | Space | Reason |
|---|---|---|---|
| Brute force over subsets of values | O(2^M) | O(M) | two choices per value |
| Memoization | O(n + M) | O(M) | building buckets is O(n); M+1 states, O(1) each; memo + stack |
| Tabulation | O(n + M) | O(M) | same work, no recursion |
| Optimized | O(n + M) | O(M) | dp is O(1), but the `points` array is still O(M) |

## Why the optimized approach is better

It removes the recursion stack (depth up to `M`, which can be 10⁴) and the dp array. If `M` were
huge compared to `n`, you could instead sort the distinct values and run the same recurrence
over them, treating two values as "adjacent" only when they differ by exactly 1.

## Common mistakes

- **Deciding per element instead of per value.** That misses observation 1 and blows up the state.
- **Using `count[v]` instead of `v × count[v]`.** You earn the value each time, not 1.
- **Forgetting that `v` and `v+2` do not conflict.** Only neighbours by exactly 1 are destroyed.

## Key takeaway

Before inventing a new DP, ask: *"Can I re-shape the input so it becomes a problem I already
know?"* Bucketing by value turned an unfamiliar rule into House Robber.

## Related problems

- [198. House Robber](https://leetcode.com/problems/house-robber/), the target of the reduction
- [213. House Robber II](https://leetcode.com/problems/house-robber-ii/), taught in [`07-house-robber-ii`](../../../01-one-dimensional-dp/07-house-robber-ii/)
- Previous: [1137. N-th Tribonacci Number](../1137-n-th-tribonacci-number/) · Next: [2140. Solving Questions With Brainpower](../2140-solving-questions-with-brainpower/)
