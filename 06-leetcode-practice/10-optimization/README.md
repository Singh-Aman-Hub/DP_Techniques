# Practice: DP Optimization

These problems already have a correct but slow DP (usually `O(n²)` or `O(n² k)`). The work is to
spot **why** the slow DP repeats effort and pick the matching speed-up:

| Signal in the recurrence | Speed-up |
|---|---|
| `max`/`min` over a **sliding window** of earlier states | monotonic deque |
| "best earlier index" where older candidates can be **dominated** | monotonic deque / stack |
| block cost = `max` of the block in a partition DP | monotonic stack |
| partition DP whose optimal split **moves monotonically** | divide & conquer optimization |
| a sum over all **subsets** of a mask | submask enumeration / SOS DP |
| resources with small capacity | bitmask (or base-3) state |

Each problem folder keeps the slow DP next to the optimized one and cross-checks them in `main`
on random inputs.

| # | Problem | LeetCode link | Difficulty | Pattern | Prerequisite topic folder |
|---|---|---|---|---|---|
| 1 | 1696. Jump Game VI (taught in the topic) | [1696](https://leetcode.com/problems/jump-game-vi/) | Medium | DP + sliding-window max deque | [`04-monotonic-queue-optimization`](../../05-dp-optimization/04-monotonic-queue-optimization/) |
| 2 | 1425. Constrained Subsequence Sum (taught in the topic) | [1425](https://leetcode.com/problems/constrained-subsequence-sum/) | Hard | DP + window max, take-or-restart | [`04-monotonic-queue-optimization`](../../05-dp-optimization/04-monotonic-queue-optimization/) |
| 3 | [1335. Minimum Difficulty of a Job Schedule](1335-minimum-difficulty-of-a-job-schedule/) | [1335](https://leetcode.com/problems/minimum-difficulty-of-a-job-schedule/) | Hard | partition DP; monotonic stack for max-cost blocks | [`06-palindrome-partitioning`](../../04-advanced-dp-patterns/06-palindrome-partitioning/), [`04-monotonic-queue-optimization`](../../05-dp-optimization/04-monotonic-queue-optimization/) |
| 4 | [1499. Max Value of Equation](1499-max-value-of-equation/) | [1499](https://leetcode.com/problems/max-value-of-equation/) | Hard | split into f(j) + g(i), window max deque | [`04-monotonic-queue-optimization`](../../05-dp-optimization/04-monotonic-queue-optimization/) |
| 5 | [862. Shortest Subarray with Sum at Least K](862-shortest-subarray-with-sum-at-least-k/) | [862](https://leetcode.com/problems/shortest-subarray-with-sum-at-least-k/) | Hard | prefix sums + monotonic deque with domination | [`03-prefix-and-suffix-optimization`](../../05-dp-optimization/03-prefix-and-suffix-optimization/), [`04-monotonic-queue-optimization`](../../05-dp-optimization/04-monotonic-queue-optimization/) |
| 6 | [1478. Allocate Mailboxes](1478-allocate-mailboxes/) | [1478](https://leetcode.com/problems/allocate-mailboxes/) | Hard | median-cost precompute + partition DP; D&C optimization | [`05-divide-and-conquer-optimization`](../../05-dp-optimization/05-divide-and-conquer-optimization/) |
| 7 | [2172. Maximum AND Sum of Array](2172-maximum-and-sum-of-array/) | [2172](https://leetcode.com/problems/maximum-and-sum-of-array/) | Hard | base-3 mask / two seats per slot | [`09-bitmask-dp`](../../04-advanced-dp-patterns/09-bitmask-dp/) |
| 8 | [1178. Number of Valid Words for Each Puzzle](1178-number-of-valid-words-for-each-puzzle/) | [1178](https://leetcode.com/problems/number-of-valid-words-for-each-puzzle/) | Hard | letter-set masks + submask enumeration | [`07-sos-dp`](../../05-dp-optimization/07-sos-dp/) |

Problems 1 and 2 are worked through in full inside the Monotonic Queue Optimization topic, so they
have no folder here. Do them first: problems 3–5 reuse the same deque/stack reasoning.

## How to use these problems

1. Write the slow DP first and make it pass on small inputs. It is your test oracle.
2. Point at the inner loop and ask: *which candidates can never win again?* That question leads to
   a deque or stack. *Does the best split only move right?* That leads to divide & conquer.
3. Implement the fast version and compare it with the slow one on many random small inputs, exactly
   as each `Optimized.java` here does.
