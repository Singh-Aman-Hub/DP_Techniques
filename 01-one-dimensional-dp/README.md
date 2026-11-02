# Module 01 · One-Dimensional DP

> **Prerequisite:** [00 · Foundations](../00-foundations/) · **Topics:** 9 · **Estimated time:** 12–16 hours of study, plus 8–12 hours for the practice problems

## What this module is about

In [00-foundations](../00-foundations/) you learned the machinery: recursion trees, memoization, tabulation, states, transitions, base cases, space optimization and the [7-question framework](../00-foundations/16-the-dp-problem-solving-framework/). This module is where you **use** that machinery on real problems for the first time.

Every problem here has a **one-dimensional state**: a single index `i` (a step, a stone, a house, a position in a string or array) is enough to describe a subproblem, so the DP table is a single array, and almost every solution ends as a loop with two or three rolling variables.

The topics are ordered so that each one adds exactly **one** new idea to the previous one:

```
Fibonacci ──▶ Climbing Stairs ──▶ Min Cost Climbing Stairs ──▶ Frog Jump
 (template)    (counting by the      (min instead of +,           (cost on both ends,
               last move)            top at index n)              k choices)
                                                                       │
     ┌─────────────────────────────────────────────────────────────────┘
     ▼
Max Sum of Non-Adjacent ──▶ House Robber ──▶ House Robber II
 (pick / not-pick)           (recognize the     (circle → two lines)
                             pattern in a story)
                                                     │
     ┌───────────────────────────────────────────────┘
     ▼
Decode Ways ──▶ Maximum Subarray (Kadane vs. DP)
 (counting with   ("ending at i" states vs.
  validity rules)   "first i" states)
```

## Topic order

| # | Topic | Core problem | What it adds | Time |
|---|-------|--------------|--------------|------|
| 01 | [Fibonacci Number](01-fibonacci-number/) | [LeetCode 509](https://leetcode.com/problems/fibonacci-number/) | The full 7-question template on the simplest recurrence, in four code styles. | 45–60 min |
| 02 | [Climbing Stairs](02-climbing-stairs/) | [LeetCode 70](https://leetcode.com/problems/climbing-stairs/) | Counting by the last move; `ways[0] = 1`; why it equals Fibonacci; generalizing to steps `1..k`. | 60–90 min |
| 03 | [Min Cost Climbing Stairs](03-minimum-cost-climbing-stairs/) | [LeetCode 746](https://leetcode.com/problems/min-cost-climbing-stairs/) | First `min` DP; the "top of the floor" is index `n`; path reconstruction. | 60–90 min |
| 04 | [Frog Jump](04-frog-jump/) | [AtCoder dp_a](https://atcoder.jp/contests/dp/tasks/dp_a), [dp_b](https://atcoder.jp/contests/dp/tasks/dp_b) | Transition cost depends on both ends; `k` choices → `O(n·k)`; reading stdin in Java. | 75–100 min |
| 05 | [Maximum Sum of Non-Adjacent Elements](05-maximum-sum-of-non-adjacent-elements/) | generic array, negatives allowed | The **pick / not-pick** pattern; why greedy fails; reconstructing the chosen items. | 90–120 min |
| 06 | [House Robber](06-house-robber/) | [LeetCode 198](https://leetcode.com/problems/house-robber/) | Mapping a story onto a known pattern; the two-state (robbed / skipped) formulation. | 45–75 min |
| 07 | [House Robber II](07-house-robber-ii/) | [LeetCode 213](https://leetcode.com/problems/house-robber-ii/) | Circular constraint → case split into two line problems; proof that the cases cover everything. | 60–90 min |
| 08 | [Decode Ways](08-decode-ways/) | [LeetCode 91](https://leetcode.com/problems/decode-ways/) | Counting with validity conditions; every `'0'` edge case; prefix-length indexing. | 90–120 min |
| 09 | [Maximum Subarray: Kadane vs. DP](09-maximum-subarray-kadane-vs-dp/) | [LeetCode 53](https://leetcode.com/problems/maximum-subarray/) | "Ending at `i`" vs. "first `i`" states; Kadane derived as `O(1)`-space DP; LC 918 and LC 152. | 90–120 min |

## How to study each topic

1. Read the topic's `README.md` (5 minutes) to know what you are about to learn.
2. Read `walkthrough.md` with a pen. **Answer the 7 questions yourself before reading the answers.**
3. Close the walkthrough and write the four solutions (brute force → memoization → tabulation → optimized) yourself. Compare with the files in `solution/` and run them: `java Tabulation.java`.
4. Do the exercises at the end of the walkthrough.
5. Solve the practice problems in the README's table, easiest first.

## Patterns you will own after this module

| Pattern | Recurrence shape | First seen in |
|---------|------------------|---------------|
| Linear recurrence | `dp[i] = dp[i-1] + dp[i-2]` | 01, 02 |
| Count by last move | `ways[i] = Σ ways[i - move]` | 02 |
| Min/max over last move | `best[i] = min over moves (best[prev] + price)` | 03, 04 |
| Pick / not-pick | `best[i] = max(best[i-1], value[i] + best[i-2])` | 05, 06 |
| Case split for circles | `max(line(0..n-2), line(1..n-1))` | 07 |
| Counting with validity | `ways[i] = [valid]·ways[i-1] + [valid]·ways[i-2]` | 08 |
| "Ending at `i`" state | `end[i] = x[i] + max(0, end[i-1])`, answer `max(end)` | 09 |

## After this module you can…

- [ ] Apply the 7 questions to any problem with a single changing index, in writing, before coding.
- [ ] Tell counting problems (`+`), optimization problems (`min`/`max`) and feasibility problems (`||`) apart, and choose base cases accordingly (`ways[0] = 1`, `cost[0] = 0`, `best(<0) = 0`).
- [ ] Use the pick/not-pick pattern and explain where the "pick" branch lands.
- [ ] Turn a circular problem into line problems and prove the split is correct.
- [ ] Choose between "first `i`" and "ending at `i`" states, and test whether a state carries enough information.
- [ ] Reduce any of these DPs to `O(1)` extra space, and say when you must keep the table (to reconstruct the answer).
- [ ] Read judge input from stdin in Java while keeping your solution testable.
- [ ] Solve LeetCode Easy and most 1D LeetCode Medium DP problems on your own.

## Practice

Each topic's README lists its practice problems. Full write-ups for them live in [06-leetcode-practice/01-one-dimensional](../06-leetcode-practice/01-one-dimensional/).

## Navigation

⬅ Previous module: [00 · Foundations](../00-foundations/) · ➡ Next module: [02 · Two-Dimensional and Grid DP](../02-two-dimensional-and-grid-dp/) · 🏁 Start: [Fibonacci Number](01-fibonacci-number/)
