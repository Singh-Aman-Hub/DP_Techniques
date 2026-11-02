# Maximum Subarray: Kadane vs. DP

> **Module:** 01 · One-Dimensional DP · **Difficulty:** Intermediate · **Estimated time:** 90–120 min

## 1. What is this topic?

Given an array of integers, find the contiguous block (subarray) with the largest sum. The famous answer is **Kadane's algorithm**, a five-line loop that is often taught as a clever trick. In this topic you will **derive** it: it is a 1D DP whose state is "the best subarray **ending at** index `i`", with the table squeezed into one variable.

The most important idea here is not Kadane itself but the choice of state. Every earlier topic in this module used "the best answer **considering the first `i` elements**". That kind of state does not work for subarrays, and understanding why is a big step towards designing your own DP states.

## 2. Why does this concept exist?

Whenever the answer must be **contiguous** (a subarray, a substring, a run of days), a decision at index `i` depends on whether the answer touches index `i-1`. "Best within the first `i`" forgets that information; "best ending exactly at `i`" keeps it. This "ending at `i`" state is used in Longest Increasing Subsequence, longest common substring, stock problems and many more.

## 3. What problem does it solve?

[LeetCode 53 · Maximum Subarray](https://leetcode.com/problems/maximum-subarray/): return the largest sum of a non-empty contiguous subarray. Extensions covered briefly: [LeetCode 918 · Maximum Sum Circular Subarray](https://leetcode.com/problems/maximum-sum-circular-subarray/) and [LeetCode 152 · Maximum Product Subarray](https://leetcode.com/problems/maximum-product-subarray/).

## 4. Prerequisites

- [Maximum Sum of Non-Adjacent Elements](../05-maximum-sum-of-non-adjacent-elements/) — a "first `i` elements" state, to contrast with.
- [State representation](../../00-foundations/09-state-representation/) — what information a state must carry.
- [Space optimization basics](../../00-foundations/15-space-optimization-basics/)

## 5. What you will learn

- The difference between **"ending at `i`"** states and **"considering the first `i`"** states, and how to choose.
- How to test whether a state carries enough information (same state + same next input must give the same answer).
- The recurrence `endingAt[i] = nums[i] + max(0, endingAt[i-1])`.
- Why the answer is the **maximum over the table**, not the last cell.
- Why Kadane's algorithm *looks* greedy but is DP with `O(1)` space.
- How the same idea extends to circular arrays (LC 918) and products (LC 152).

## 6. Important terminology

| Term | Meaning in plain English |
|------|--------------------------|
| Subarray | A contiguous, non-empty block `nums[l..r]`. |
| Subsequence | Elements picked in order but not necessarily next to each other (topic 05 picked a subsequence). |
| "Ending at `i`" state | The best answer among solutions whose **last** element is exactly `nums[i]`. |
| "First `i`" (prefix) state | The best answer among all solutions that use only `nums[0..i]`. |
| Extend / restart | Append `nums[i]` to the best subarray ending at `i-1`, or start a new subarray at `i`. |
| Kadane's algorithm | The `O(n)` time, `O(1)` space loop for maximum subarray. |
| Greedy algorithm | One that commits to a locally best choice without comparing futures. |

## 7. Where this is used in real problems

Best stretch of trading days, largest gain over a period, longest/biggest contiguous segment problems, image processing (maximum-sum rectangle uses Kadane inside a loop), and the "ending at `i`" pattern in [Longest Increasing Subsequence](../../03-subsequence-and-knapsack-dp/11-longest-increasing-subsequence/) and [Best Time to Buy and Sell Stock](../../04-advanced-dp-patterns/02-stock-buy-and-sell/).

## 8. After completing this topic you should be able to…

- [ ] Explain why "best subarray within the first `i`" is not a usable state on its own.
- [ ] Derive `endingAt[i] = nums[i] + max(0, endingAt[i-1])` from "extend or restart".
- [ ] Write Kadane's algorithm and point to the DP table it replaces.
- [ ] Handle the all-negative case correctly.
- [ ] Explain why Kadane is DP and not a greedy guess.
- [ ] Sketch the changes for LC 918 and LC 152.

## 9. Practice problems

| # | Problem | Platform/ID | Difficulty | Pattern tested | Why it belongs here |
|---|---------|-------------|------------|----------------|---------------------|
| 1 | Best Time to Buy and Sell Stock | [LeetCode 121](https://leetcode.com/problems/best-time-to-buy-and-sell-stock/) | Easy | "Ending at `i`" thinking: best sell on day `i` | Kadane on the daily price differences. |
| 2 | Maximum Subarray | [LeetCode 53](https://leetcode.com/problems/maximum-subarray/) | Medium | "Ending at `i`" state, extend or restart | The core problem of this topic. |
| 3 | Maximum Sum Circular Subarray | [LeetCode 918](https://leetcode.com/problems/maximum-sum-circular-subarray/) | Medium | Case split: wrapping vs. not wrapping | Combines this topic with the circular idea from [House Robber II](../07-house-robber-ii/). |
| 4 | Maximum Product Subarray | [LeetCode 152](https://leetcode.com/problems/maximum-product-subarray/) | Medium | Two "ending at `i`" states (max and min) | A negative number turns the smallest product into the largest, so one state is not enough. |

## 10. Navigation

⬅ Previous: [Decode Ways](../08-decode-ways/) · ➡ Next: [2D DP state representation](../../02-two-dimensional-and-grid-dp/01-2d-dp-state-representation/) · 📖 [Walkthrough](walkthrough.md) · 💻 [Solutions](solution/)
