# Monotonic Queue Optimization

> **Module:** 05 — DP Optimization · **Difficulty:** Advanced · **Estimated time:** 90–120 min

## 1. What is this topic?

A time optimization for DP transitions of the form `dp[i] = f(i) + max (or min) of dp[j]` over a **sliding
window** of earlier indices, such as `j ∈ [i−k, i−1]`. A **monotonic deque** keeps the useful candidates of the
window in sorted order, so the best one is always at the front and each step costs amortized O(1).

## 2. Why does this concept exist?

The direct transition rescans the window for every cell: `O(n·k)`. Neighbouring windows share `k−1` elements,
but a `max` cannot be updated by "subtracting" the element that leaves (unlike a sum), and a prefix maximum
cannot handle a window whose left end moves. The monotonic deque is the data structure that maintains a
sliding-window maximum efficiently.

## 3. What problem does it solve?

It turns `O(n·k)` DPs into `O(n)` DPs: LeetCode 1696 Jump Game VI and LeetCode 1425 Constrained Subsequence Sum.
The same deque solves LeetCode 239 Sliding Window Maximum, 1499 Max Value of Equation and 862 Shortest
Subarray with Sum at Least K.

## 4. Prerequisites

- [Frog Jump](../../01-one-dimensional-dp/04-frog-jump/) (the "jump up to `k` steps back" transition)
- [Maximum Subarray: Kadane vs DP](../../01-one-dimensional-dp/09-maximum-subarray-kadane-vs-dp/) ("extend or start fresh", used in LC 1425)
- [Time and Space Complexity in DP](../../00-foundations/12-time-and-space-complexity-in-dp/)
- [Prefix and Suffix Optimization](../03-prefix-and-suffix-optimization/) (why `max` over a moving range needs a new tool)
- The deque itself is taught from scratch in the walkthrough (Section 4.8) on LeetCode 239.

## 5. What you will learn

- What a deque is and how to use Java's `ArrayDeque` from both ends.
- The domination rule: an older, not-larger element can never be a window maximum again.
- The four-step loop: expire → read → pop dominated → push, and why reading comes before pushing in a DP.
- Amortized analysis: why a `while` loop inside a `for` loop is still `O(n)` in total.
- The exact conditions (monotone window ends, final values) under which the deque is correct.

## 6. Important terminology

| Term | Meaning in plain English |
|---|---|
| Deque | A double-ended queue: add and remove at both the front and the back in O(1). |
| Monotonic deque | A deque whose elements' values stay sorted (here: strictly decreasing from front to back). |
| Sliding window | A range `[L(i), R(i)]` of indices whose ends move forward as `i` grows. |
| Expire | Remove the front index because it is no longer inside the window. |
| Dominated | An index that is older and has a value no better than a newer index; it can be discarded forever. |
| Amortized O(1) | A single step may be slow, but the total over all `n` steps is `O(n)`. |

## 7. Where this is used in real problems

- "Jump at most `k` steps" and "chosen elements at most `k` apart" DPs.
- Scheduling DPs where the previous event must be within the last `k` days.
- Bounded knapsack (each item up to `c` copies), one deque per remainder class — an advanced use.
- Non-DP problems: sliding window maximum/minimum, shortest subarray with a sum constraint.

## 8. After completing this topic you should be able to…

- [ ] Implement sliding window maximum with a deque and trace it by hand.
- [ ] Explain the domination rule and the amortized `O(n)` bound.
- [ ] Recognize `dp[i] = f(i) + max(dp[i−k..i−1])` and optimize it from `O(n·k)` to `O(n)`.
- [ ] Get the expiry condition and the read-before-push order right.
- [ ] Say when a deque is *not* valid (non-monotone window ends, changing values, product terms `a[i]·b[j]`).
- [ ] Cross-check a deque DP against the slow version with randomized tests.

## 9. Practice problems

| # | Problem | Platform/ID | Difficulty | Pattern tested | Why it belongs here |
|---|---|---|---|---|---|
| 1 | [Sliding Window Maximum](https://leetcode.com/problems/sliding-window-maximum/) | LeetCode 239 | Hard | Monotonic deque (no DP) | The prerequisite: learn the deque here before using it inside a DP. |
| 2 | [Jump Game VI](https://leetcode.com/problems/jump-game-vi/) | LeetCode 1696 | Medium | `dp[i] = nums[i] + max(dp[i−k..i−1])` | The main example of this topic. |
| 3 | [Jump Game VII](https://leetcode.com/problems/jump-game-vii/) | LeetCode 1871 | Medium | Sliding window over reachable states | Window `[i−maxJump, i−minJump]`; a running count (or deque/pointer) replaces the scan. |
| 4 | [Constrained Subsequence Sum](https://leetcode.com/problems/constrained-subsequence-sum/) | LeetCode 1425 | Hard | Window max + "start fresh" | Same deque, plus `max(0, …)` and the answer as a max over all `i`. |
| 5 | [Max Value of Equation](https://leetcode.com/problems/max-value-of-equation/) | LeetCode 1499 | Hard | Deque over a window defined by `x` values | Requires rewriting `|x_i − x_j|` to separate `i` and `j` first. |
| 6 | [Shortest Subarray with Sum at Least K](https://leetcode.com/problems/shortest-subarray-with-sum-at-least-k/) | LeetCode 862 | Hard | Increasing deque over prefix sums | The deque pops from the front for a reason other than expiry; hardest twist. |

## 10. Navigation

⬅ Previous: [Prefix and Suffix Optimization](../03-prefix-and-suffix-optimization/) · ➡ Next: [Divide and Conquer Optimization](../05-divide-and-conquer-optimization/) · 📖 [Walkthrough](walkthrough.md) · 💻 [Solutions](solution/)
