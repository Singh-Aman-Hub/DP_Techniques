# Optimal Substructure

> **Module:** 00 — Foundations · **Difficulty:** Beginner · **Estimated time:** 60–75 min

## 1. What is this topic?

A problem has **optimal substructure** when its best answer can be **built from the best answers to its subproblems**. Example: the cheapest way to reach the top of a staircase ends with a last step from one of the two stairs below, and the route up to that stair must itself be the cheapest. This is the second property DP needs (the first was [overlapping subproblems](../04-overlapping-subproblems/)). This topic also shows that **counting** problems split up in the same way, and gives a famous problem where the property **fails**: the longest simple path.

## 2. Why does this concept exist?

A recurrence such as `best(i) = min(best(i-1) + cost, best(i-2) + cost)` only gives the right answer if "best overall" really is made of "best for the smaller part". If that is false, storing and combining sub-answers produces wrong results, however fast. Optimal substructure is what makes the recurrence **correct**; overlapping subproblems is what makes caching it **fast**.

## 3. What problem does it solve?

It lets you **trust** a recurrence before writing code: if you can argue "any optimal solution, with its last choice removed, is an optimal solution of the smaller problem", then combining optimal sub-answers is guaranteed to give the optimal answer. It also warns you when a problem needs a richer state (or no DP at all).

## 4. Prerequisites

- [03 — Recursion Trees and Brute Force](../03-recursion-trees-and-brute-force/) (choices, combining with `min`/`max`/`+`)
- [04 — Overlapping Subproblems](../04-overlapping-subproblems/)

## 5. What you will learn

- What "optimal substructure" means, with a road-trip analogy and with code.
- Min Cost Climbing Stairs: building the cheapest route from the cheapest sub-routes.
- Shortest paths: why the beginning of a shortest path is a shortest path (the "cut and paste" argument).
- The counterexample: longest simple path, and *why* glueing optimal pieces breaks there.
- That counting problems ("how many ways") decompose just like optimization problems.

## 6. Important terminology

| Term | Meaning in plain English |
|------|--------------------------|
| Optimal | The best possible: minimum cost, maximum profit, shortest length… |
| Optimal substructure | An optimal solution contains optimal solutions to its subproblems, so best answers can be combined into the best answer. |
| Cut and paste argument | A proof: "if the piece were not optimal, swap in a better piece and get a better overall solution — impossible." |
| Independent subproblems | Solving one subproblem does not restrict how the other may be solved. Needed for optimal substructure. |
| Simple path | A path in a graph that never visits the same vertex twice. |
| DAG | Directed Acyclic Graph: a graph with one-way edges and no cycles. |
| Decomposition (counting) | Splitting all solutions into separate groups by their last choice, then adding the group sizes. |

## 7. Where this is used in real problems

- Minimum/maximum problems: [Min Cost Climbing Stairs](../../01-one-dimensional-dp/03-minimum-cost-climbing-stairs/), [House Robber](../../01-one-dimensional-dp/06-house-robber/), [Minimum Path Sum](../../02-two-dimensional-and-grid-dp/04-minimum-path-sum/), [0/1 Knapsack](../../03-subsequence-and-knapsack-dp/02-zero-one-knapsack/), [Edit Distance](../../03-subsequence-and-knapsack-dp/14-edit-distance/).
- Shortest paths: Dijkstra, Bellman–Ford and shortest paths in DAGs ([DP on graphs](../../04-advanced-dp-patterns/08-dp-on-graphs/)) all rely on it.
- Counting problems: Climbing Stairs, Unique Paths, Decode Ways, Coin Change II.
- Where it fails: longest simple path in general graphs, problems where earlier choices restrict later ones in ways the state does not capture.

## 8. After completing this topic you should be able to…

- [ ] Define optimal substructure in one sentence and give an example.
- [ ] Prove with a cut-and-paste argument that Min Cost Climbing Stairs has optimal substructure.
- [ ] Explain why shortest paths have the property and longest simple paths do not.
- [ ] Explain why counting problems decompose by "last choice" and why the parts are added.
- [ ] Spot when a state is missing information that would make sub-answers independent.

## 9. Practice problems

| # | Problem | Platform/ID | Difficulty | Pattern tested | Why it belongs here |
|---|---------|-------------|------------|----------------|---------------------|
| 1 | [Climbing Stairs](https://leetcode.com/problems/climbing-stairs/) | LeetCode 70 | Easy | Counting decomposes by last step | The counting twin of this topic's stairs example. |
| 2 | [Min Cost Climbing Stairs](https://leetcode.com/problems/min-cost-climbing-stairs/) | LeetCode 746 | Easy | Min of optimal sub-answers | The topic's main example; write the recursion and argue its correctness. |
| 3 | [Frog 1](https://atcoder.jp/contests/dp/tasks/dp_a) | AtCoder DP A | Intro | Min over two previous stones | Same structure with jump costs \|h[i] − h[j]\|. |
| 4 | [Minimum Path Sum](https://leetcode.com/problems/minimum-path-sum/) | LeetCode 64 | Medium | Optimal substructure in a grid | The cheapest path to a cell comes from the cheapest path to the cell above or to the left. |

## 10. Navigation

⬅ Previous: [04 — Overlapping Subproblems](../04-overlapping-subproblems/) · ➡ Next: [06 — Memoization](../06-memoization/) · 📖 [Walkthrough](walkthrough.md) · 💻 [Solutions](solution/)
