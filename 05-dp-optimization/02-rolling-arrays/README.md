# Rolling Arrays

> **Module:** 05 — DP Optimization · **Difficulty:** Advanced · **Estimated time:** 2 hours

## 1. What is this topic?

A **rolling array** keeps only the last few *layers* of a DP table and reuses them in a cycle. The classic form keeps two layers and picks one with `dp[i % 2]` (or the equivalent `dp[i & 1]`): layer `i` is written into one slot while layer `i - 1` is read from the other, then the roles swap. It is the general tool behind "turn a 3D DP into 2D" and "turn a 2D DP into two rows".

## 2. Why does this concept exist?

In [Space optimization](../01-space-optimization/) we squeezed a table into **one** row by sweeping in a clever direction. That trick fails when a cell needs old values on *both* sides of itself (for example the cell above-left **and** above-right), because any sweep direction overwrites one of them too early. A second layer removes the problem entirely: the old layer is never written while we read it. Rolling arrays give you that safety for the price of one extra layer, and they extend naturally from 2D to 3D and beyond.

## 3. What problem does it solve?

- 3D DPs such as [Cherry Pickup II](https://leetcode.com/problems/cherry-pickup-ii/) where `R × C × C` cells do not fit but `2 × C × C` do.
- "Simulate k steps" DPs (a ball or a knight moving on a board) where step `s + 1` depends only on step `s`.
- Any DP whose footprint reaches back a constant number of layers `k`: keep `k + 1` layers and index with `i % (k + 1)`.

## 4. Prerequisites

- [Space optimization](../01-space-optimization/) (dependency footprint, last-reader rule)
- [Introduction to 3D DP](../../02-two-dimensional-and-grid-dp/08-introduction-to-3d-dp/)
- [Cherry Pickup](../../02-two-dimensional-and-grid-dp/09-cherry-pickup/)
- [Transition dependencies and computation order](../../00-foundations/11-transition-dependencies-and-computation-order/)

## 5. What you will learn

- The `dp[i % 2]` / `dp[i & 1]` pattern and why `i & 1 == i % 2` for non-negative `i`.
- When two layers are needed instead of one in-place row.
- How to roll a 3D DP down to two 2D layers (Cherry Pickup II).
- The **reset pitfall**: a reused layer still contains values from two layers ago.
- Where the final answer lives (`dp[n % 2]`, not `dp[n]`).
- Rolling with `k + 1` layers and the "swap references" alternative.

## 6. Important terminology

| Term | Meaning in plain English |
|------|--------------------------|
| Layer | All states that share the same value of the "outer" index (one row of a 2D table, one 2D slice of a 3D table). |
| Rolling array | An array of only a few layers that is reused cyclically as the outer index advances. |
| Parity | Whether a number is even (0) or odd (1); `i % 2` and `i & 1` both compute it. |
| Pull style | Each new cell is computed by *reading* its predecessors and is written exactly once. |
| Push style | Each old cell *adds/sends* its value to its successors (`next[x] += cur[y]`). |
| Stale value | A value left in a reused layer from an earlier round that no longer means anything. |
| Reset | Filling the reused layer with the neutral value (0 for counts, −∞ for max) before writing into it. |

## 7. Where this is used in real problems

- Multi-agent grid DPs: Cherry Pickup, Cherry Pickup II.
- Step simulations: Out of Boundary Paths, Knight Probability in Chessboard, Knight Dialer.
- Short-string DPs with an extra dimension (knapsack with two capacities such as Ones and Zeroes).
- Counting DPs over positions with a value dimension (CSES Array Description).

## 8. After completing this topic you should be able to…

- [ ] Recognise when a DP's footprint reaches only the previous layer.
- [ ] Rewrite any `dp[i][...]` that reads `dp[i-1][...]` as `dp[i % 2][...]` reading `dp[(i-1) % 2][...]`.
- [ ] Roll a 3D DP to two 2D layers.
- [ ] Decide whether the reused layer must be reset, and reset it with the right neutral value.
- [ ] Read the answer from the correct parity slot.

## 9. Practice problems

| # | Problem | Platform/ID | Difficulty | Pattern tested | Why it belongs here |
|---|---------|-------------|------------|----------------|---------------------|
| 1 | [Vacation](https://atcoder.jp/contests/dp/tasks/dp_c) | AtCoder dp_c | Unrated (AtCoder) | Two rows of 3 states | Smallest rolling DP: day `i` reads only day `i - 1`. |
| 2 | [Out of Boundary Paths](https://leetcode.com/problems/out-of-boundary-paths/) | LeetCode 576 | Medium | 3D → 2D, push style | Walked through here; the reset pitfall in action. |
| 3 | [Knight Probability in Chessboard](https://leetcode.com/problems/knight-probability-in-chessboard/) | LeetCode 688 | Medium | 3D → 2D, push style | Same structure with probabilities and 8 moves. |
| 4 | [Knight Dialer](https://leetcode.com/problems/knight-dialer/) | LeetCode 935 | Medium | Two layers of 10 states | Step `s + 1` depends only on step `s`. |
| 5 | [Array Description](https://cses.fi/problemset/task/1746) | CSES 1746 | Unrated (CSES) | Position × value, two rows | Value `v` reads `v - 1, v, v + 1` of the previous row. |
| 6 | [Cherry Pickup II](https://leetcode.com/problems/cherry-pickup-ii/) | LeetCode 1463 | Hard | 3D → 2D, pull style | The running example of the walkthrough. |
| 7 | [Count Vowels Permutation](https://leetcode.com/problems/count-vowels-permutation/) | LeetCode 1220 | Hard | Two layers of 5 states | Rolling in O(n); topic 09 revisits it for huge n. |
| 8 | [Cherry Pickup](https://leetcode.com/problems/cherry-pickup/) | LeetCode 741 | Hard | 3D (step, r1, r2) → 2D | Roll over the step number `t = r + c`. |

## 10. Navigation

⬅ Previous: [Space optimization](../01-space-optimization/) · ➡ Next: [Prefix and suffix optimization](../03-prefix-and-suffix-optimization/) · 📖 [Walkthrough](walkthrough.md) · 💻 [Solutions](solution/)
