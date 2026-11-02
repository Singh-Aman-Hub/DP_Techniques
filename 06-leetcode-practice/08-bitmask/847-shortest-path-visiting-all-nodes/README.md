# 847. Shortest Path Visiting All Nodes

> **Pattern:** Bitmask state (current node, visited set) — BFS over states, or TSP DP over shortest distances · **Difficulty:** Hard
> **Builds on:** [Traveling Salesperson Problem](../../../04-advanced-dp-patterns/10-traveling-salesperson-problem/) and [Bitmask DP](../../../04-advanced-dp-patterns/09-bitmask-dp/)

---

## Problem overview

An undirected, connected graph has `n ≤ 12` nodes, given as adjacency lists. Find the length (number of edges) of the shortest walk that visits every node at least once. You may start and end anywhere, and you may revisit nodes and reuse edges.

**Why bitmask DP.** "Visit every node" means the future depends on **where I am** and **which nodes I have already seen** — the state `(node, visitedMask)` from the [Traveling Salesperson Problem](../../../04-advanced-dp-patterns/10-traveling-salesperson-problem/). Two differences from classic TSP: revisits are allowed, and every edge costs 1.

There is a catch for memoization: in the `(node, mask)` state graph you can walk `a → b → a` without changing the mask, so states **form cycles** and a naive recursive DP never terminates. Two clean ways out:
1. **BFS over states.** All edges cost 1, so BFS layers *are* the DP layers by distance — the natural tabulation.
2. **TSP on shortest distances.** Precompute `dist[u][v]` by BFS from each node, then only record *first visits*: `dp[mask][v]` with `v` the last newly visited node. Every transition adds a bit, so the state graph is acyclic and memoization works.

## Link & metadata

| Field | Value |
|---|---|
| LeetCode | [847. Shortest Path Visiting All Nodes](https://leetcode.com/problems/shortest-path-visiting-all-nodes/) |
| Difficulty | Hard |
| Pattern | (node, mask) state; multi-source BFS / Held–Karp TSP |
| Prerequisite topics | [Traveling Salesperson Problem](../../../04-advanced-dp-patterns/10-traveling-salesperson-problem/), [Bitmask DP](../../../04-advanced-dp-patterns/09-bitmask-dp/), [DP on Graphs](../../../04-advanced-dp-patterns/08-dp-on-graphs/) |

## Examples

```
Example A (star)               Example B
     1                          0 - 1 - 2 - 3
     |                              \   /
 2 - 0 - 3                            4
answer 4: 1-0-2-0-3            graph [[1],[0,2,4],[1,3,4],[2],[1,2]]
                               answer 4: 0-1-4-2-3
```
Edge case: one node → `0`. A path `0-1-2-3` → `3`.

## Brute-force intuition

Try every visiting order of the nodes (`n!`) and join consecutive nodes by shortest paths. `12! ≈ 4.8·10^8` orders, each costing `n` steps. The cost of the rest of a route only depends on the current node and which nodes are already seen — the same `(node, mask)` repeats across many orders.

## Deriving the DP (7 questions)

### 1. What is changing?
The current node and the set of nodes visited so far.

### 2. What does the state represent?
BFS form: `steps[node][mask]` = the fewest edges of a walk that ends at `node` and has visited exactly the nodes in `mask`.
TSP form: `best[mask][last]` = the shortest walk that **first-visits** the nodes of `mask`, with `last` being the most recent new node (walking between new nodes along shortest paths).

### 3. What are the choices?
BFS form: move to any neighbour. TSP form: which unvisited node becomes the next new node.

### 4. What is the recurrence?
```
BFS form:  steps[nb][mask | bit(nb)] = steps[node][mask] + 1      for each neighbour nb (first time reached)
TSP form:  best[mask][v] = min over u in mask, u != v of best[mask \ {v}][u] + dist[u][v]
answer   = min over v of best[full][v]
```
Why TSP form is correct: any walk visits nodes for the first time in some order; between two consecutive new nodes it costs at least their shortest distance, and following shortest paths achieves that.

### 5. What are the base cases?
BFS: every `(v, {v})` is a start state at distance 0 (we may start anywhere — **multi-source BFS**).
TSP: `best[{v}][v] = 0`.

### 6. What is the order of computation?
BFS: by distance layer (queue order) — the first time a state is popped its distance is final. TSP: masks in increasing order (removing a bit gives a smaller mask). The answer for BFS is the first popped state with `mask = full`.

### 7. Can the state space be optimized?
Both use `n · 2^n` states (`12 · 4096 = 49152`). BFS needs no all-pairs distances but explores edges (`O(2^n · E)`); TSP needs `dist` (`O(n · E)`) and does `O(2^n · n²)` work. Both are tiny here; no separate `Optimized.java`.

## Recursive solution

TSP form (acyclic, so recursion is safe):
```java
int best(int mask, int last) {                    // shortest walk first-visiting 'mask', ending at 'last'
    if (mask == (1 << last)) return 0;
    int prevMask = mask ^ (1 << last), result = INF;
    for (int u = 0; u < n; u++)
        if ((prevMask >> u & 1) == 1)
            result = Math.min(result, best(prevMask, u) + dist[u][last]);
    return result;
}
```

## Memoization

Cache `best(mask, last)` in an `n × 2^n` table. See [`solution/Memoization.java`](solution/Memoization.java).

## Tabulation

Multi-source BFS over `(node, mask)` with a `seen[node][mask]` table. BFS processes states in increasing distance — that queue order is the computation order of this DP. See [`solution/Tabulation.java`](solution/Tabulation.java).

## Space optimization

Not applicable — every `(node, mask)` may be needed.

## Dry run

Example A (star, centre 0, leaves 1, 2, 3), BFS layers. A state is written `node/{mask}`:

| Distance | New states reached (selection) |
|---|---|
| 0 | 0/{0}, 1/{1}, 2/{2}, 3/{3} (all starts) |
| 1 | 1/{0,1}, 2/{0,2}, 3/{0,3}, 0/{0,1}, 0/{0,2}, 0/{0,3} |
| 2 | 2/{0,1,2}, 3/{0,1,3}, 1/{0,1,2}, 3/{0,2,3}, 1/{0,1,3}, 2/{0,2,3} |
| 3 | 0/{0,1,2}, 0/{0,1,3}, 0/{0,2,3} |
| 4 | 3/{0,1,2,3} ← **full mask, answer 4** |

TSP form agrees: first-visit order `1, 0, 2, 3` costs `dist(1,0) + dist(0,2) + dist(2,3) = 1 + 1 + 2 = 4`.

## Complexity

| Approach | Time | Space | Why |
|---|---|---|---|
| All visiting orders | O(n!·n) | O(n) | every permutation |
| TSP memo / table | O(n·E + 2ⁿ·n²) | O(2ⁿ·n) | all-pairs BFS, then n·2ⁿ states × n predecessors |
| BFS over states | O(2ⁿ·(n + E)) | O(2ⁿ·n) | each state is enqueued once and scans its node's edges |

## Why the optimized approach is better

Both collapse `n!` orders into `n · 2^n` states. BFS is the shortest code when edges have unit weight; the TSP form generalises to weighted graphs (see [943](../943-find-the-shortest-superstring/)).

## Common mistakes

- Plain memoized DFS on `(node, mask)` — cycles in the state graph cause infinite recursion or wrong cached values.
- Starting BFS from node 0 only — the best walk may start elsewhere.
- Marking only `node` as visited instead of `(node, mask)` — you must be allowed to revisit a node with a bigger mask.

## Key takeaway

"Visit all nodes" ⇒ state `(current node, visited set)`. If moves can revisit states without progress, use **BFS** (unit costs) or switch to **first-visit TSP over shortest distances**, which makes the state graph acyclic.

## Related problems

- [Traveling Salesperson Problem](../../../04-advanced-dp-patterns/10-traveling-salesperson-problem/) — Held–Karp DP.
- [943. Find the Shortest Superstring](../943-find-the-shortest-superstring/) — TSP with weighted (overlap) edges and path reconstruction.
- [DP on Graphs](../../../04-advanced-dp-patterns/08-dp-on-graphs/) — why DP needs an acyclic state graph.
