# 377. Combination Sum IV

> **Pattern:** Unbounded counting where **order matters** (permutations) · **Builds on:** [Coin Change II](../../../03-subsequence-and-knapsack-dp/09-coin-change-ii/) · **Difficulty:** Medium

## Problem overview

Given distinct positive integers `nums` and a `target`, count the **ordered** sequences of numbers
from `nums` (each usable any number of times) that add up to `target`. Despite the word
"combination" in the title, `1+2` and `2+1` are counted separately.

## Link & metadata

| Field | Value |
|---|---|
| LeetCode | [377. Combination Sum IV](https://leetcode.com/problems/combination-sum-iv/) |
| Difficulty | Medium |
| Pattern | Unbounded knapsack counting, permutation version: amount loop **outside**, item loop **inside** |
| Prerequisite topic | [`03-subsequence-and-knapsack-dp/09-coin-change-ii`](../../../03-subsequence-and-knapsack-dp/09-coin-change-ii/) (and [`07-unbounded-knapsack`](../../../03-subsequence-and-knapsack-dp/07-unbounded-knapsack/)) |
| Why this pattern | Same items, same reuse, same target as Coin Change II. The only difference, ordered vs unordered, is decided by the loop order |

## Examples

| nums | target | Answer | Sequences |
|---|---|---|---|
| `[1,2,3]` | 4 | 7 | 1111, 112, 121, 211, 22, 13, 31 |
| `[1,2]` | 3 | 3 | 111, 12, 21 |
| `[9]` | 3 | 0 | nothing fits |
| `[1]` | 1 | 1 | 1 |

## Brute-force intuition

"What is the **last** number of the sequence?" Any `x` in `nums` with `x ≤ target`. Then
the rest is any ordered sequence summing to `target - x`. Different last numbers give different
sequences, so add. The recursion re-asks the same remaining sums constantly (`count(2)` is
reached from 3 via −1, from 4 via −2, …), so cache by the remaining sum.

## The 518 contrast (read this carefully)

| | 518. Coin Change II (combinations) | 377. Combination Sum IV (permutations) |
|---|---|---|
| counts | `{1,2}` once for target 3 (as 1+2) | `1+2` and `2+1` separately |
| state | `dp[i][t]`: using only coins `0..i` | `dp[t]`: any numbers |
| loops | `for coin` **outside**, `for t` inside | `for t` **outside**, `for num` inside |
| `[1,2]`, target 3 | **2** (111, 12) | **3** (111, 12, 21) |

Why does the loop order decide it?
- **Coins outside:** when coin 2 is being processed, all uses of coin 1 were already decided.
  Every sum is built "all 1s first, then all 2s", one canonical order per multiset, so each
  combination is counted once.
- **Amount outside:** for each amount `t`, every number may be the last one. A sequence ending
  in 1 and a sequence ending in 2 are both counted, so each order is a separate path.

[`solution/Tabulation.java`](solution/Tabulation.java) contains both loops side by side and
checks that `[1,2]`, target 3 gives 3 vs 2.

## Deriving the DP (7 questions)

### 1. What is changing?
The remaining target `t`.

### 2. What does the state represent?
`dp[t]` = the number of ordered sequences of numbers from `nums` summing to exactly `t`.

### 3. What are the choices?
The **last** number of the sequence: any `num` in `nums` with `num ≤ t`.

### 4. What is the recurrence?
```
dp[t] = Σ over num in nums with num ≤ t of dp[t - num]
```

### 5. What are the base cases?
`dp[0] = 1`: the empty sequence is the one way to reach 0. It is what makes `dp[num] ≥ 1` for a
single-number sequence.

### 6. What is the order of computation?
`dp[t]` reads smaller `t`: loop `t = 1..target` **outside**, numbers inside.

### 7. Can the state space be optimized?
It is already a single 1D array of size `target + 1`, and any smaller `t` can be read. There is
no `Optimized.java`: tabulation is the optimal form. (Sorting `nums` lets the inner loop stop
early, a constant-factor gain only.)

## Recursive solution

```java
static int count(int remaining) {
    if (remaining == 0) return 1;
    int total = 0;
    for (int num : nums) if (num <= remaining) total += count(remaining - num);
    return total;
}
```

## Memoization

See [`solution/Memoization.java`](solution/Memoization.java). Uses `Integer[]` with `null` as
"unknown". Intermediate counts can wrap around in `int` (see Common mistakes), so `-1` would not
be a safe sentinel.

## Tabulation

```java
dp[0] = 1;
for (int t = 1; t <= target; t++)          // amount OUTSIDE  → order matters
    for (int num : nums)
        if (num <= t) dp[t] += dp[t - num];
```

## Space optimization

Not applicable beyond the 1D array (see question 7).

## Dry run

`nums = [1,2,3]`, `target = 4`:

| t | via 1: dp[t-1] | via 2: dp[t-2] | via 3: dp[t-3] | dp[t] |
|---|---|---|---|---|
| 0 | | | | 1 |
| 1 | 1 | — | — | 1 |
| 2 | 1 | 1 | — | 2 |
| 3 | 2 | 1 | 1 | 4 |
| 4 | 4 | 2 | 1 | **7** |

Same input under the 518 loop order gives 4: {1111, 112, 22, 13}.

## Complexity

Let `m = |nums|`, `T = target`.

| Approach | Time | Space | Reason |
|---|---|---|---|
| Brute force | exponential | O(T) | every ordered sequence is generated |
| Memoization | O(T·m) | O(T) | T states × m numbers |
| Tabulation | O(T·m) | O(T) | same double loop |

## Why the optimized approach is better

Tabulation avoids a recursion depth of up to `T` (1000) and is the clearest place to see the
loop-order rule.

## Common mistakes

- **Swapping the loops** and getting the combinations count (518's answer).
- **Overflow:** LeetCode promises the *final* answer fits in 32 bits, but `dp[t]` for some
  other `t` may not. In Java, `int` addition wraps around predictably (arithmetic mod 2³²), and
  since the true final answer is below 2³¹, the wrapped result is still correct. Do not "fix" it
  with `Math.addExact`, which throws.
- **`dp[0] = 0`.** Then every count stays 0.

## Key takeaway

For unbounded counting: **items outside = combinations; amount outside = permutations.** Write
the loop order on purpose, never by habit.

## Related problems

- [518. Coin Change II](https://leetcode.com/problems/coin-change-ii/), the combinations version, taught in [`09-coin-change-ii`](../../../03-subsequence-and-knapsack-dp/09-coin-change-ii/)
- [70. Climbing Stairs](https://leetcode.com/problems/climbing-stairs/), which is exactly this problem with `nums = [1,2]`
- Previous: [1155. Number of Dice Rolls With Target Sum](../1155-number-of-dice-rolls-with-target-sum/) · Next: [474. Ones and Zeroes](../474-ones-and-zeroes/)
