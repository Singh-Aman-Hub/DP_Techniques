# 1499. Max Value of Equation

> **Pattern:** Split the formula into "me" + "best partner in a window", then a monotonic deque · **Builds on:** [Monotonic Queue Optimization](../../../05-dp-optimization/04-monotonic-queue-optimization/) · **Difficulty:** Hard

## Problem overview

You get points `(x, y)` sorted by strictly increasing `x` (up to 10⁵ points) and an integer `k`.
Over all pairs `i < j` with `x_j − x_i ≤ k`, maximize `y_i + y_j + |x_i − x_j|`. At least one valid
pair is guaranteed.

The trick is algebra: because `x` is sorted, the absolute value disappears and the expression
splits into a part that depends only on `j` and a part that depends only on `i`. Then "best `i` for
each `j`" is a sliding-window maximum.

## Link & metadata

| Field | Value |
|---|---|
| LeetCode | [1499. Max Value of Equation](https://leetcode.com/problems/max-value-of-equation/) |
| Difficulty | Hard |
| Pattern | `best[j] = (y_j + x_j) + max over the window of (y_i − x_i)`; window max via a monotonic deque |
| Prerequisite topic | [`05-dp-optimization/04-monotonic-queue-optimization`](../../../05-dp-optimization/04-monotonic-queue-optimization/) |
| Why this pattern | Same shape as [1696. Jump Game VI](https://leetcode.com/problems/jump-game-vi/): each index takes the max over a sliding window of earlier values |

## Examples

| points | k | Answer | Best pair |
|---|---|---|---|
| [[1,3],[2,0],[5,10],[6,-10]] | 1 | 4 | (1,3) & (2,0): 3 + 0 + 1 |
| [[0,0],[3,0],[9,2]] | 3 | 3 | (0,0) & (3,0): 0 + 0 + 3 |
| [[1,1],[2,2]] | 1 | 4 | the only pair: 1 + 2 + 1 |
| [[-3,5],[-1,-2],[0,4],[2,1],[6,7]] | 4 | 12 | (-3,5) & (0,4): 9 + 3, or (2,1) & (6,7): 8 + 4 |

## Brute-force intuition

Check every pair inside the distance limit: `O(n²)` for `n = 10⁵` is 5·10⁹ pairs. Too slow.

Rewrite the value for `i < j` (so `x_i < x_j`):

```
y_i + y_j + |x_i − x_j|  =  y_i + y_j + x_j − x_i  =  (y_j + x_j)  +  (y_i − x_i)
                                                       └ only j ┘     └ only i ┘
```

For a fixed `j` we just need the **largest `y_i − x_i`** among the points with
`x_i ≥ x_j − k`. That window slides to the right as `j` grows: a sliding-window maximum.

## Deriving the DP (7 questions)

### 1. What is changing?
The right point `j`, and with it the window of allowed left points.

### 2. What does the state represent?
`best[j]` = the maximum value of the equation over pairs whose **right** point is `j`
(−∞ if no left point is close enough). Answer = `max over j of best[j]`.

### 3. What are the choices?
The left partner `i < j` with `x_j − x_i ≤ k`.

### 4. What is the recurrence?

```
best[j] = (y_j + x_j) + max{ y_i − x_i : i < j, x_j − x_i ≤ k }
```

### 5. What are the base cases?
`best[0] = −∞` (no point to its left). A `j` with an empty window contributes nothing.

### 6. What is the order of computation?
Increasing `j`. The window's left edge only moves right because `x` is sorted.

### 7. Can the state space be optimized?
Keep a deque of candidate indices with **strictly decreasing** `y_i − x_i`:

```
- pop the FRONT while it is out of the window (x_j − x_front > k): it will never be in range again;
- the front is now the best partner for j;
- pop the BACK while its (y − x) ≤ (y_j − x_j): j is newer (stays in range longer) and at least as
  good, so the old one can never win again; then push j.
```

## Recursive solution

No recursion is needed: `best[j]` depends on the input values in the window, not on other `best`
values. The "DP" is the per-index maximum, and the work to optimize is the window search.

## Memoization

Not applicable, for the same reason: no subproblem repeats. This folder therefore contains
`Tabulation.java` (the `O(n²)` scan) and `Optimized.java` (the deque).

## Tabulation

For each `j`, scan `i = j−1, j−2, …` while `x_j − x_i ≤ k` and keep the max of `y_i − x_i`. Because
`x` is sorted, the scan can stop as soon as a point is too far. See
[`solution/Tabulation.java`](solution/Tabulation.java). Worst case `O(n²)` (when `k` is huge).

### Deque version ([`solution/Optimized.java`](solution/Optimized.java))

```java
for (int j = 0; j < n; j++) {
    while (head < tail && x[j] - x[deque[head]] > k) head++;
    if (head < tail) best = max(best, y[j] + x[j] + (y[deque[head]] - x[deque[head]]));
    while (head < tail && y[deque[tail-1]] - x[deque[tail-1]] <= y[j] - x[j]) tail--;
    deque[tail++] = j;
}
```

Note the order: read the best partner **before** pushing `j`, since `j` cannot pair with itself.
`main` compares the deque with the slow scan on 5000 random inputs.

## Space optimization

The deque holds at most `n` indices; the slow version already uses `O(1)` extra. No table is
stored in either version, since the answer is a running maximum.

## Dry run

`points = [[1,3],[2,0],[5,10],[6,-10]]`, `k = 1`. Values `y − x`: `2, −2, 5, −16`.

| j | (x, y) | front pops (too far) | partner (front) | candidate value | best | back pops | deque after |
|---|---|---|---|---|---|---|---|
| 0 | (1, 3) | — | none | — | −∞ | — | [0] |
| 1 | (2, 0) | 2 − 1 = 1 ≤ 1 keep | 0 (y−x = 2) | (0 + 2) + 2 = 4 | 4 | 2 ≤ −2? no | [0, 1] |
| 2 | (5, 10) | 5−1 = 4 > 1 pop 0; 5−2 = 3 > 1 pop 1 | none | — | 4 | — | [2] |
| 3 | (6, −10) | 6 − 5 = 1 keep | 2 (y−x = 5) | (−10 + 6) + 5 = 1 | **4** | 5 ≤ −16? no | [2, 3] |

## Complexity

| Approach | Time | Space | Reason |
|---|---|---|---|
| All pairs | O(n²) | O(1) | every pair |
| Tabulation (window scan) | O(n · w) ≤ O(n²) | O(1) | w = points inside the window |
| Monotonic deque | O(n) | O(n) | each index pushed and popped at most once |

## Why the optimized approach is better

The deque answers each window-max query in amortized `O(1)`, so the run time no longer depends on
how wide the window is. The real insight, though, is the algebraic split: without it, the "best
partner" depends on both `i` and `j` together and no data structure helps.

## Common mistakes

- **Keeping the absolute value.** Use the sorted order: `|x_i − x_j| = x_j − x_i` for `i < j`.
- **Pushing `j` before reading the front.** Then `j` may pair with itself.
- **Storing `y + x` in the deque** instead of `y − x` (the left point's part has a minus sign).
- **Assuming the deque is non-empty**: a `j` whose window is empty must be skipped.

## Key takeaway

Separate a pair objective into `f(j) + g(i)`. If the valid `i` form a sliding window, the max of
`g(i)` is a monotonic-deque query and the whole problem becomes `O(n)`.

## Related problems

- [862. Shortest Subarray with Sum at Least K](../862-shortest-subarray-with-sum-at-least-k/): deque on prefix sums with a value condition.
- [239. Sliding Window Maximum](https://leetcode.com/problems/sliding-window-maximum/): the plain window-max deque.
- [1696. Jump Game VI](https://leetcode.com/problems/jump-game-vi/) and [1425. Constrained Subsequence Sum](https://leetcode.com/problems/constrained-subsequence-sum/): window max inside a DP, taught in the [Monotonic Queue topic](../../../05-dp-optimization/04-monotonic-queue-optimization/).
