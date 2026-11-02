# Traveling Salesperson Problem

> **Module:** 04 — Advanced DP Patterns · **Difficulty:** Advanced · **Estimated time:** 2.5–3.5 hours

## 1. What is this topic?

The Traveling Salesperson Problem (TSP) asks for the cheapest round trip that visits every city exactly once. The Held–Karp algorithm solves it with bitmask DP over the state `dp[mask][last]`: the set of cities visited so far, plus the city we are standing in. It is the standard example of a bitmask DP whose state needs a second component.

## 2. Why does this concept exist?

Trying every order of n cities costs n!, which is hopeless beyond about 11 cities. The remaining trip depends only on which cities are done and where we are now, not on the order of earlier visits. Merging all orders that end in the same `(set, city)` situation brings the cost down to 2ⁿ · n², fast enough for 16–20 cities.

## 3. What problem does it solve?

- Cheapest tour or path through all nodes when n is small.
- Best ordering of items when the cost of a step depends on the previous item (setup costs, word overlaps).
- Shortest walk covering every node of a small graph (state-space BFS over `(mask, node)`).

## 4. Prerequisites

- [Bitmask DP](../09-bitmask-dp/): subsets as integers, the bit operations, `dp[mask]`.
- [DP on Graphs](../08-dp-on-graphs/): states as a DAG and computation order.
- [State representation](../../00-foundations/09-state-representation/): deciding what a state must remember.
- [Memoization](../../00-foundations/06-memoization/) and [Tabulation](../../00-foundations/07-tabulation/).
- BFS on graphs (for LeetCode 847).

## 5. What you will learn

- Why `dp[mask]` is not enough for TSP and `dp[mask][last]` is.
- The full Held–Karp derivation, brute force → memoization → tabulation.
- A complete hand-filled table on 4 cities.
- How to rebuild the actual tour with parent pointers.
- How LeetCode 847 (BFS over `(node, mask)`) and LeetCode 943 (TSP on word overlaps) reuse the same state.

## 6. Important terminology

| Term | Meaning in plain English |
|---|---|
| Tour | a route that visits every city once and returns to the start |
| Held–Karp | the `dp[mask][last]` algorithm for TSP, O(2ⁿ · n²) |
| `last` | the city where the partial path currently ends |
| Closing the cycle | adding the cost from the last city back to the start |
| Parent pointer | for each state, the previous city on its best path; used to rebuild the route |
| Asymmetric TSP | `dist[i][j]` may differ from `dist[j][i]`; direction matters |
| NP-hard | a class of problems with no known polynomial-time algorithm; TSP is one |

## 7. Where this is used in real problems

- Route planning for a handful of stops (deliveries, a robot visiting checkpoints).
- Ordering jobs on a machine when switching from job i to job j has a setup cost.
- Genome assembly in miniature: joining fragments with maximum overlap (LeetCode 943).
- Covering every node of a small graph with the shortest walk (LeetCode 847).

## 8. After completing this topic you should be able to…

- [ ] State `dp[mask][last]` in one English sentence and explain why `last` is needed.
- [ ] Fill the Held–Karp table by hand for 4 cities.
- [ ] Rebuild the optimal tour from a parent table.
- [ ] Explain why LeetCode 847 needs BFS instead of an increasing-mask loop.
- [ ] Convert LeetCode 943 into a TSP on an overlap matrix.
- [ ] Estimate the largest n that Held–Karp can handle.

## 9. Practice problems

| # | Problem | Platform/ID | Difficulty | Pattern tested | Why it belongs here |
|---|---|---|---|---|---|
| 1 | [Shortest Path Visiting All Nodes](https://leetcode.com/problems/shortest-path-visiting-all-nodes/) | LeetCode 847 | Hard | BFS over `(mask, node)` | the TSP state with unit costs and revisits |
| 2 | [Find the Shortest Superstring](https://leetcode.com/problems/find-the-shortest-superstring/) | LeetCode 943 | Hard | Held–Karp with `max` overlap and path reconstruction | TSP in disguise; tests rebuilding the answer |
| 3 | [Minimum XOR Sum of Two Arrays](https://leetcode.com/problems/minimum-xor-sum-of-two-arrays/) | LeetCode 1879 | Hard | `dp[mask]` assignment | revision: when `last` is NOT needed |
| 4 | [Smallest Sufficient Team](https://leetcode.com/problems/smallest-sufficient-team/) | LeetCode 1125 | Hard | mask DP with reconstruction | rebuilding a chosen set from parent pointers |
| 5 | [Elevator Rides](https://cses.fi/problemset/task/1653) | CSES 1653 | – | `dp[mask]` storing a pair (rides, current load) | an ordering problem where a cleverer value replaces `last` |

## 10. Navigation

⬅ Previous: [Bitmask DP](../09-bitmask-dp/) · ➡ Next: [Digit DP](../11-digit-dp/) · 📖 [Walkthrough](walkthrough.md) · 💻 [Solutions](solution/)
