# 862. Shortest Subarray with Sum at Least K

> **Pattern:** Prefix sums + monotonic deque (optimizing a "best earlier index" DP) · **Builds on:** [Monotonic Queue Optimization](../../../05-dp-optimization/04-monotonic-queue-optimization/), [Prefix and Suffix Optimization](../../../05-dp-optimization/03-prefix-and-suffix-optimization/) · **Difficulty:** Hard

## Problem overview

Given an integer array `nums` (up to 10⁵ elements, values may be **negative**) and `k ≥ 1`, return
the length of the shortest non-empty contiguous subarray whose sum is at least `k`, or `-1` if none
exists.

With only positive numbers a two-pointer sliding window works. Negative numbers break it: growing
the window can **decrease** the sum. The fix is to reason about prefix sums and keep only the
"useful" earlier indices in a monotonic deque.

## Link & metadata

| Field | Value |
|---|---|
| LeetCode | [862. Shortest Subarray with Sum at Least K](https://leetcode.com/problems/shortest-subarray-with-sum-at-least-k/) |
| Difficulty | Hard |
| Pattern | `best[j] = j − (latest i < j with prefix[i] ≤ prefix[j] − k)`; the search over `i` is done by a monotonic deque |
| Prerequisite topic | [`05-dp-optimization/04-monotonic-queue-optimization`](../../../05-dp-optimization/04-monotonic-queue-optimization/), [`05-dp-optimization/03-prefix-and-suffix-optimization`](../../../05-dp-optimization/03-prefix-and-suffix-optimization/) |
| Why this pattern | Each end index needs the best earlier index under a condition; dominated candidates can be thrown away forever |

## Examples

| nums | k | Answer | Why |
|---|---|---|---|
| [1] | 1 | 1 | the whole array |
| [1, 2] | 4 | -1 | total is only 3 |
| [2, -1, 2] | 3 | 3 | only the whole array reaches 3 |
| [84, -37, 32, 40, 95] | 167 | 3 | 32 + 40 + 95 = 167 |
| [2, -5, 3, 1, -1, 4] | 5 | 4 | 3 + 1 − 1 + 4 = 7; no window of length ≤ 3 reaches 5 |

## Brute-force intuition

Try every pair `(start, end)` and sum: `O(n³)`, or `O(n²)` with prefix sums. With
`prefix[j] = nums[0] + … + nums[j−1]`:

```
sum(nums[i..j−1]) = prefix[j] − prefix[i]
```

So the question becomes: for each `j`, find the **largest** `i < j` with
`prefix[i] ≤ prefix[j] − k`. Scanning `i` from `j − 1` down and stopping at the first hit is the
`O(n²)` "slow DP". For `n = 10⁵` that is 5·10⁹ steps: too slow.

## Deriving the DP (7 questions)

### 1. What is changing?
The end position `j` of the subarray (as a prefix index `0..n`).

### 2. What does the state represent?
`best[j]` = the length of the shortest subarray that **ends right before index `j`** with sum
`≥ k` (∞ if none). Answer = `min over j of best[j]`.

### 3. What are the choices?
The start `i` (a prefix index `< j`).

### 4. What is the recurrence?

```
best[j] = j − max{ i < j : prefix[i] ≤ prefix[j] − k }
```

### 5. What are the base cases?
`prefix[0] = 0` (the empty prefix lets a subarray start at index 0). `best[0] = ∞`.

### 6. What is the order of computation?
Increasing `j`; each `best[j]` reads `prefix[0..j−1]`, already known.

### 7. Can the state space be optimized?
Yes: most earlier indices can never be the answer for any future `j`. Two **domination rules**:

```
(a) Back rule: if i1 < i2 and prefix[i1] ≥ prefix[i2], then i1 is useless forever:
    i2 is later (shorter subarrays) AND has a smaller-or-equal prefix (larger sums).
    → keep the candidates with strictly INCREASING prefix values.

(b) Front rule: once the oldest candidate i satisfies prefix[j] − prefix[i] ≥ k, record j − i
    and drop i. A later j' > j would give a longer subarray from the same i.
```

A deque supports both: pop dominated indices from the back, pop used-up indices from the front.

## Recursive solution

There is no natural recursion here: `best[j]` does not depend on other `best` values, only on the
prefix sums. The "DP" is the table `prefix[]` plus a search, so we go straight to the iterative form.

## Memoization

Not applicable: no subproblem is ever asked twice. The repeated work in the slow version is the
**search** over `i`, which the deque removes. That is why this folder has `Tabulation.java`
(slow) and `Optimized.java` (deque) only.

## Tabulation

Build `prefix[]`, then for each `j` scan `i = j−1, j−2, …` and stop at the first
`prefix[j] − prefix[i] ≥ k`. See [`solution/Tabulation.java`](solution/Tabulation.java). Use `long`
for prefix sums (10⁵ × 10⁵ overflows `int`).

### Monotonic deque ([`solution/Optimized.java`](solution/Optimized.java))

```java
for (int j = 0; j <= n; j++) {
    while (head < tail && prefix[j] - prefix[deque[head]] >= k) best = min(best, j - deque[head++]);
    while (head < tail && prefix[deque[tail - 1]] >= prefix[j]) tail--;
    deque[tail++] = j;
}
```

`main` compares the deque against the `O(n²)` scan on 5000 random arrays with negatives.

## Space optimization

`prefix[]` could be replaced by a running sum if the deque stored `(index, prefixValue)` pairs. The
deque itself is unavoidable: `O(n)` in the worst case (for example, a strictly increasing prefix).

## Dry run

`nums = [84, -37, 32, 40, 95]`, `k = 167`.
`prefix = [0, 84, 47, 79, 119, 214]` (indices 0..5).

| j | prefix[j] | front pops (prefix[j] − prefix[front] ≥ 167?) | back pops (prefix[back] ≥ prefix[j]?) | deque after (indices) | best |
|---|---|---|---|---|---|
| 0 | 0 | — | — | [0] | ∞ |
| 1 | 84 | 84 − 0 = 84 no | 0 < 84 keep | [0, 1] | ∞ |
| 2 | 47 | 47 no | 84 ≥ 47 → pop 1 | [0, 2] | ∞ |
| 3 | 79 | 79 no | 47 < 79 keep | [0, 2, 3] | ∞ |
| 4 | 119 | 119 no | keep | [0, 2, 3, 4] | ∞ |
| 5 | 214 | 214 − 0 = 214 yes → len 5, pop 0; 214 − 47 = 167 yes → len 3, pop 2; 214 − 79 = 135 no | keep | [3, 4, 5] | **3** |

Index 1 was popped at `j = 2` because prefix 84 > 47: any subarray starting after index 1 would
have to beat one starting after index 2, which is both later and lower. The answer 3 is
`nums[2..4] = 32 + 40 + 95`.

## Complexity

| Approach | Time | Space | Reason |
|---|---|---|---|
| All pairs | O(n²) | O(n) | every (i, j) with prefix sums |
| Tabulation (scan back) | O(n²) worst | O(n) | each j may scan all earlier i |
| Monotonic deque | O(n) | O(n) | each index is pushed once and popped at most once |

## Why the optimized approach is better

The deque never revisits a discarded index, so the total work is linear. Compare with
[1696. Jump Game VI](https://leetcode.com/problems/jump-game-vi/), where the deque holds the best
`dp` values in a window; here it holds the best **prefix sums**, and the window is defined by a
value condition instead of a distance.

## Common mistakes

- **Using a two-pointer window.** It is only correct when all numbers are non-negative.
- **Overflow:** prefix sums need `long`.
- **Back-pop with `>` instead of `≥`.** Equal prefixes: the later one is always at least as good.
- **Forgetting `prefix[0] = 0`** in the deque, which loses subarrays that start at index 0.

## Key takeaway

When a DP or a search asks "the best earlier index satisfying a condition", look for a
domination rule ("later and better ⇒ the older one is useless"). It usually means a monotonic
deque and `O(n)`.

## Related problems

- [1499. Max Value of Equation](../1499-max-value-of-equation/): deque over a distance window.
- [239. Sliding Window Maximum](https://leetcode.com/problems/sliding-window-maximum/): the base deque technique.
- [1425. Constrained Subsequence Sum](https://leetcode.com/problems/constrained-subsequence-sum/): deque inside a real DP, taught in the [Monotonic Queue topic](../../../05-dp-optimization/04-monotonic-queue-optimization/).
