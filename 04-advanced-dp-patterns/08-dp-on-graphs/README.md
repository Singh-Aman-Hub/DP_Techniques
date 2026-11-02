# DP on Graphs

> **Module:** 04 — Advanced DP Patterns · **Difficulty:** Advanced · **Estimated time:** 2.5–3 hours

## 1. What is this topic?

Every DP has a dependency graph: states are vertices, and an arrow joins each state to the states it needs. This topic makes that graph the actual input. When the input graph is a **DAG** (directed, no cycles), we can fill one value per vertex in **topological order**. That gives longest paths, shortest paths and path counts in linear time. When the graph has cycles, we add a dimension (such as "number of edges used") that turns it back into a DAG. That is Bellman–Ford seen as a DP.

## 2. Why does this concept exist?

DP needs an order in which every state is computed after the states it depends on. Arrays and grids give that order for free (left to right, top to bottom). Graphs do not. A topological order is the general answer to "in what order do I fill the table?", and understanding it explains *why* DP fails on cyclic dependencies.

## 3. What problem does it solve?

- Longest / shortest / weighted path in a DAG (task scheduling, course prerequisites).
- Counting paths in a DAG.
- Longest walks in grids where every step must increase (a hidden DAG).
- Shortest paths with a limit on the number of edges, even when the graph has cycles.

## 4. Prerequisites

- [Transition dependencies and computation order](../../00-foundations/11-transition-dependencies-and-computation-order/): the key idea this topic generalises.
- [Memoization](../../00-foundations/06-memoization/) and [Tabulation](../../00-foundations/07-tabulation/).
- [Unique Paths](../../02-two-dimensional-and-grid-dp/02-unique-paths/): counting paths in a grid, the simplest DAG.
- [DP on Trees](../07-dp-on-trees/): a tree is a special DAG; post-order was its topological order.
- Basic graph representation (adjacency lists) and BFS.

## 5. What you will learn

- Why DP requires an acyclic dependency graph, and what goes wrong with a cycle.
- How memoized DFS discovers a reverse topological order by itself.
- Kahn's algorithm for a topological order (and for detecting cycles).
- One template that answers longest path, shortest path, counting and reachability on a DAG.
- Why LeetCode 329's grid has no cycles.
- Bellman–Ford as `dp[edges used][vertex]`, and why the previous layer must be kept separate.

## 6. Important terminology

| Term | Meaning in plain English |
|---|---|
| Directed edge `u → v` | a one-way arrow; here, "v can be reached from u" or "u must come before v" |
| Cycle | a path that returns to where it started |
| DAG | Directed Acyclic Graph: a directed graph without cycles |
| In-degree | number of arrows pointing into a vertex |
| Topological order | a list of all vertices where every arrow points forward |
| Kahn's algorithm | repeatedly removes a vertex with in-degree 0 to build a topological order |
| Relaxing an edge | updating the value at `v` using the value at `u` across the edge `u → v` |
| Bellman–Ford | shortest paths computed in rounds; after round t, every path with at most t edges has been considered |

## 7. Where this is used in real problems

- Build systems and course planners: earliest finish time = longest weighted path in a DAG (LeetCode 2050).
- Counting routes in a network that only moves "forward" (LeetCode 1976 after Dijkstra).
- Strictly increasing walks in grids (LeetCode 329) and chains of items where each step must grow.
- Flight or network routing with a hop limit (LeetCode 787).

## 8. After completing this topic you should be able to…

- [ ] Explain why a cycle in the dependency graph makes a DP impossible.
- [ ] Build a topological order with Kahn's algorithm and use it as the loop order.
- [ ] Solve AtCoder DP G both with memoized DFS and with Kahn.
- [ ] Count paths in a DAG.
- [ ] Explain why LeetCode 329 needs no `visited` array.
- [ ] Write the two-row Bellman–Ford for LeetCode 787 and explain the in-place bug.

## 9. Practice problems

| # | Problem | Platform/ID | Difficulty | Pattern tested | Why it belongs here |
|---|---|---|---|---|---|
| 1 | [Longest Path](https://atcoder.jp/contests/dp/tasks/dp_g) | AtCoder DP G | – | longest path in a DAG | the core lesson of this topic |
| 2 | [Cheapest Flights Within K Stops](https://leetcode.com/problems/cheapest-flights-within-k-stops/) | LeetCode 787 | Medium | Bellman–Ford layers `dp[t][v]` | DP on a graph with cycles via an extra dimension |
| 3 | [Number of Ways to Arrive at Destination](https://leetcode.com/problems/number-of-ways-to-arrive-at-destination/) | LeetCode 1976 | Medium | counting paths on the shortest-path DAG | Dijkstra's order as a topological order |
| 4 | [Longest Increasing Path in a Matrix](https://leetcode.com/problems/longest-increasing-path-in-a-matrix/) | LeetCode 329 | Hard | memoized DFS on a hidden DAG | recognising that a grid is acyclic |
| 5 | [Parallel Courses III](https://leetcode.com/problems/parallel-courses-iii/) | LeetCode 2050 | Hard | weighted longest path, Kahn order | the scheduling form of longest path |
| 6 | [Largest Color Value in a Directed Graph](https://leetcode.com/problems/largest-color-value-in-a-directed-graph/) | LeetCode 1857 | Hard | `dp[v][colour]` + cycle detection | adds a second state dimension and needs Kahn's cycle check |

## 10. Navigation

⬅ Previous: [DP on Trees](../07-dp-on-trees/) · ➡ Next: [Bitmask DP](../09-bitmask-dp/) · 📖 [Walkthrough](walkthrough.md) · 💻 [Solutions](solution/)
