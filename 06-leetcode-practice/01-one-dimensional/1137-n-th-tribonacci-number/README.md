# 1137. N-th Tribonacci Number

> **Pattern:** 1D linear DP (fixed look-back of 3) · **Builds on:** [Fibonacci Number](../../../01-one-dimensional-dp/01-fibonacci-number/) · **Difficulty:** Easy

## Problem overview

The Tribonacci sequence starts with `T0 = 0`, `T1 = 1`, `T2 = 1`. Every later term is the sum of
the **three** terms before it. Given `n` (0 ≤ n ≤ 37), return `Tn`.

It is Fibonacci with one more term in the sum, which makes it the ideal first practice problem:
the DP shape is identical, only the "look-back window" grows from 2 to 3.

## Link & metadata

| Field | Value |
|---|---|
| LeetCode | [1137. N-th Tribonacci Number](https://leetcode.com/problems/n-th-tribonacci-number/) |
| Difficulty | Easy |
| Pattern | 1D linear DP, each state depends on a fixed number of previous states |
| Prerequisite topic | [`01-one-dimensional-dp/01-fibonacci-number`](../../../01-one-dimensional-dp/01-fibonacci-number/) |
| Why this pattern | The answer for `n` is built only from answers for smaller `n`, one index, one direction |

## Examples

| n | Tn | Why |
|---|----|-----|
| 0 | 0 | base case |
| 4 | 4 | 0, 1, 1, 2, **4** → T4 = T3 + T2 + T1 = 2 + 1 + 1 |
| 6 | 13 | 0, 1, 1, 2, 4, 7, **13** |
| 25 | 1389537 | large enough that plain recursion becomes painfully slow |

## Brute-force intuition

Translate the definition straight into recursion:

```
trib(n) = trib(n-1) + trib(n-2) + trib(n-3)
```

Every call spawns three more calls. The recursion tree for `trib(5)`:

```
                        trib(5)
            /              |              \
       trib(4)          trib(3)         trib(2)=1
      /   |   \         /  |  \
 trib(3) trib(2) trib(1) t(2) t(1) t(0)
 / | \
t2 t1 t0
```

`trib(3)` is computed twice already at `n = 5`; `trib(2)` four times. The number of calls grows
roughly like 1.84ⁿ (the "tribonacci constant"), so `n = 37` needs billions of calls. The same
small subproblems are solved again and again: **overlapping subproblems**, the signal for DP.

## Deriving the DP (7 questions)

### 1. What is changing?
Only the index `i` of the term we want. It moves from `n` down to the base cases.

### 2. What does the state represent?
`dp[i]` = the i-th Tribonacci number.

### 3. What are the choices?
There is no real decision here: each term is *forced* to be the sum of the previous three. (That
is why this is the easiest practice problem. Later problems add genuine choices.)

### 4. What is the recurrence?
```
dp[i] = dp[i-1] + dp[i-2] + dp[i-3]      for i ≥ 3
```
Each term is one of the three predecessors the definition tells us to add.

### 5. What are the base cases?
`dp[0] = 0`, `dp[1] = 1`, `dp[2] = 1`. We need three, because the recurrence reaches back three
steps; with fewer, `dp[3]` would read an index that does not exist.

### 6. What is the order of computation?
`dp[i]` needs `i-1`, `i-2`, `i-3`, all smaller. So fill `i = 3, 4, …, n` left to right.

### 7. Can the state space be optimized?
Yes. Only the last three values are ever read. Keep three variables and slide them forward:
O(1) space instead of O(n).

## Recursive solution

```java
static long trib(int n) {
    if (n == 0) return 0;
    if (n <= 2) return 1;
    return trib(n - 1) + trib(n - 2) + trib(n - 3);
}
```

Correct but exponential. We include it only to motivate the cache.

## Memoization

Store each answer the first time it is computed; return it immediately on later calls.
See [`solution/Memoization.java`](solution/Memoization.java).

```java
if (memo[n] != -1) return memo[n];          // cache hit: no further recursion
memo[n] = solve(n - 1) + solve(n - 2) + solve(n - 3);
```

With the cache, `trib(5)` makes the left-most chain of calls `5 → 4 → 3 → 2/1/0` once, and every
other branch is a cache hit. Each index is computed once.

## Tabulation

Fill an array from the base cases upward. See [`solution/Tabulation.java`](solution/Tabulation.java).

```java
dp[0] = 0; dp[1] = 1; dp[2] = 1;
for (int i = 3; i <= n; i++) dp[i] = dp[i - 1] + dp[i - 2] + dp[i - 3];
```

## Space optimization

The table row is only read through a window of width 3:

```
index:  0  1  2  3  4  5  6
value:  0  1  1  2  4  7  13
                 [a  b  c] → next = a + b + c, then slide the window one step right
```

See [`solution/Optimized.java`](solution/Optimized.java).

## Dry run

`n = 6`, using the optimized version (`a = T(i-3)`, `b = T(i-2)`, `c = T(i-1)`):

| i | a | b | c | next = a+b+c | after slide (a, b, c) |
|---|---|---|---|--------------|------------------------|
| 3 | 0 | 1 | 1 | 2 | 1, 1, 2 |
| 4 | 1 | 1 | 2 | 4 | 1, 2, 4 |
| 5 | 1 | 2 | 4 | 7 | 2, 4, 7 |
| 6 | 2 | 4 | 7 | 13 | 4, 7, 13 |

Answer: `c = 13`.

## Complexity

| Approach | Time | Space | Reason |
|---|---|---|---|
| Plain recursion | O(1.84ⁿ) | O(n) | three branches per call, recursion depth n |
| Memoization | O(n) | O(n) | n+1 states, each O(1) work once; memo array plus call stack |
| Tabulation | O(n) | O(n) | one loop over n indices; the dp array |
| Optimized | O(n) | O(1) | same loop; only three variables survive |

## Why the optimized approach is better

It does the same n additions as tabulation but throws away values that will never be read again.
There is also no recursion, so no risk of stack overflow for large `n`.

## Common mistakes

- **Only two base cases.** Forgetting `T2 = 1` makes `dp[3]` read `dp[-1]`.
- **Allocating `dp[n+1]` and then writing `dp[2]` when `n < 2`.** Return early for `n ≤ 2`.
- **Wrong slide order.** Compute `next` first, then shift `a = b; b = c; c = next;`. Shifting
  first loses a value.

## Key takeaway

When a term depends on the previous **k** terms, you need **k** base cases and **k** rolling
variables. Fibonacci is k = 2, Tribonacci is k = 3, and the template does not change.

## Related problems

- [509. Fibonacci Number](https://leetcode.com/problems/fibonacci-number/), the k = 2 version, taught in [`01-fibonacci-number`](../../../01-one-dimensional-dp/01-fibonacci-number/)
- [70. Climbing Stairs](https://leetcode.com/problems/climbing-stairs/), the same recurrence shape disguised as counting ways
- Next in this folder: [740. Delete and Earn](../740-delete-and-earn/)
