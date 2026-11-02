# 1976. Number of Ways to Arrive at Destination

> **Pattern:** Dijkstra + counting DP on the shortest-path DAG · **Difficulty:** Medium
> **Builds on:** [DP on Graphs](../../../04-advanced-dp-patterns/08-dp-on-graphs/) and counting DP from [Unique Paths](../../../02-two-dimensional-and-grid-dp/02-unique-paths/)

---

## Problem overview

A city has `n` intersections (`0 … n−1`) joined by two-way roads, each with a travel time. Count how many **different shortest-time routes** go from intersection `0` to intersection `n − 1`. The count can be huge, so return it modulo `10^9 + 7`.

**Why DP on a graph.** Counting paths is easy in a DAG: `ways(v) = Σ ways(u)` over edges into `v` — the same "add the ways from every predecessor" rule as [Unique Paths](../../../02-two-dimensional-and-grid-dp/02-unique-paths/). The roads here form an undirected graph with cycles, but if we keep **only the edges that lie on some shortest path** (`dist[u] + w == dist[v]`), the result is a DAG, because `dist` strictly increases along every kept edge (all times are positive). Dijkstra gives `dist` *and* processes nodes in exactly the topological order of that DAG. See [DP on Graphs](../../../04-advanced-dp-patterns/08-dp-on-graphs/).

## Link & metadata

| Field | Value |
|---|---|
| LeetCode | [1976. Number of Ways to Arrive at Destination](https://leetcode.com/problems/number-of-ways-to-arrive-at-destination/) |
| Difficulty | Medium |
| Pattern | Shortest-path DAG + path-counting DP |
| Prerequisite topics | [DP on Graphs](../../../04-advanced-dp-patterns/08-dp-on-graphs/), [Unique Paths](../../../02-two-dimensional-and-grid-dp/02-unique-paths/), [Order of computation](../../../00-foundations/11-transition-dependencies-and-computation-order/) |

## Examples

Example A: `n = 7`, roads `0-6 (7), 0-1 (2), 1-2 (3), 1-3 (3), 6-3 (3), 3-5 (1), 6-5 (1), 2-5 (1), 0-4 (5), 4-6 (2)`.

```
Shortest time 0 -> 6 is 7. The four shortest routes:
  0 -> 6
  0 -> 4 -> 6
  0 -> 1 -> 2 -> 5 -> 6
  0 -> 1 -> 3 -> 5 -> 6          answer 4
```
Example B: `n = 2`, road `1-0 (10)` → `1`.
Edge case: `n = 1` (start = destination) → `1` (the empty route).

## Brute-force intuition

Enumerate every simple path from 0 to n−1, compute its time, keep the minimum and count how many paths achieve it. The number of simple paths is exponential.

## Deriving the DP (7 questions)

### 1. What is changing?
The intersection `v` we have reached (along a shortest route).

### 2. What does the state represent?
`dist[v]` = the shortest travel time from 0 to `v` (computed by Dijkstra), and
`ways[v]` = the number of different routes from 0 to `v` whose time equals `dist[v]`.

### 3. What are the choices?
The **last road** used to enter `v`. Only roads `u–v` with `dist[u] + time == dist[v]` can be the last road of a shortest route.

### 4. What is the recurrence?
```
ways[v] = Σ over roads (u, v, time) with dist[u] + time == dist[v] of ways[u]   (mod 1e9+7)
```
Each shortest route to `v` = a shortest route to some valid `u` + that road; different `u` give different routes, so we add.

### 5. What are the base cases?
`ways[0] = 1` (the empty route), `dist[0] = 0`.

### 6. What is the order of computation?
`ways[v]` needs `ways[u]` for every predecessor `u`, and every predecessor has a strictly smaller `dist`. So process nodes in increasing `dist` — exactly the order in which Dijkstra finalises them. When Dijkstra pops `u`, `ways[u]` is complete and can be pushed along its edges:
- found a strictly shorter time to `v` → `dist[v] = new time`, `ways[v] = ways[u]` (old routes are no longer shortest);
- found an equal time → `ways[v] += ways[u]`.

### 7. Can the state space be optimized?
Both `dist` and `ways` are needed for all nodes (O(n)); the priority queue is O(E). Nothing smaller is possible, so no separate `Optimized.java`.

## Recursive solution

After a plain Dijkstra fills `dist`:
```java
long ways(int v) {
    if (v == 0) return 1;
    long total = 0;
    for (int[] road : adj[v])                       // road = {u, time}
        if (dist[road[0]] + road[1] == dist[v]) total += ways(road[0]);
    return total % MOD;
}
```

## Memoization

Cache `ways(v)`. The recursion only follows shortest-path edges, which strictly decrease `dist`, so it always terminates. See [`solution/Memoization.java`](solution/Memoization.java).

## Tabulation

Fold the counting into Dijkstra itself (single pass), as in question 6. See [`solution/Tabulation.java`](solution/Tabulation.java).

## Space optimization

Not applicable: every node keeps one distance and one count, and the graph must be stored.

## Dry run

Example A, nodes in the order Dijkstra finalises them:

| Order | Node | `dist` | valid last roads `u → v` (`ways[u]`) | `ways` |
|---|---|---|---|---|
| 1 | 0 | 0 | base case | 1 |
| 2 | 1 | 2 | 0→1 (1) | 1 |
| 3 | 2 | 5 | 1→2 (1) | 1 |
| 4 | 3 | 5 | 1→3 (1) | 1 |
| 5 | 4 | 5 | 0→4 (1) | 1 |
| 6 | 5 | 6 | 2→5 (1), 3→5 (1) | 2 |
| 7 | 6 | 7 | 0→6 (1), 4→6 (1), 5→6 (2) | **4** |

Road `3→6` is not used: `dist[3] + 3 = 8 ≠ 7`.

## Complexity

| Approach | Time | Space | Why |
|---|---|---|---|
| Enumerate all paths | exponential | O(n) | number of simple paths |
| Dijkstra + memoized count | O((n + E) log n) | O(n + E) | Dijkstra dominates; counting touches each edge once |
| Dijkstra with counting inside | O((n + E) log n) | O(n + E) | one pass |

## Why the optimized approach is better

One Dijkstra run both finds the shortest distance and counts the routes, because Dijkstra's pop order *is* the topological order of the shortest-path DAG.

## Common mistakes

- `int` distances: times up to `10^9` over many roads overflow — use `long`.
- On a strictly shorter distance, *adding* to `ways[v]` instead of *resetting* it.
- Skipping stale queue entries incorrectly: if a popped entry's distance is larger than `dist[u]`, ignore it, or you will add `ways[u]` twice.
- Forgetting the modulo on every addition.

## Key takeaway

"Count the optimal paths" = compute the optimum first, keep only the edges that achieve it (a DAG), then run path-counting DP in topological order.

## Related problems

- [DP on Graphs](../../../04-advanced-dp-patterns/08-dp-on-graphs/) — counting paths in a DAG.
- [787. Cheapest Flights Within K Stops](../787-cheapest-flights-within-k-stops/) — shortest path when Dijkstra alone is not enough.
- [2050. Parallel Courses III](../2050-parallel-courses-iii/) — DP in topological order on an explicit DAG.
