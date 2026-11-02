# 787. Cheapest Flights Within K Stops

> **Pattern:** DP on graphs with a budget dimension (layered Bellman–Ford) · **Difficulty:** Medium
> **Builds on:** [DP on Graphs](../../../04-advanced-dp-patterns/08-dp-on-graphs/) and [0/1 Knapsack](../../../03-subsequence-and-knapsack-dp/02-zero-one-knapsack/) (a "remaining budget" in the state)

---

## Problem overview

There are `n` cities and a list of one-way flights `[from, to, price]`. Find the cheapest way to fly from `src` to `dst` using **at most `k` stops** (intermediate cities), i.e. at most `k + 1` flights. Return `-1` if no such route exists.

**Why this is DP and not plain Dijkstra.** Dijkstra keeps only the cheapest cost per city. But here the cheapest way to reach a city may use too many flights, while a slightly more expensive way with fewer flights is the one that leads to the answer. So the state must remember *how many flights are left* — the same move as adding "remaining capacity" in [0/1 Knapsack](../../../03-subsequence-and-knapsack-dp/02-zero-one-knapsack/). With that extra dimension the graph may even contain cycles, because every step strictly uses up budget: `(city, flightsLeft)` states form a DAG. See [DP on Graphs](../../../04-advanced-dp-patterns/08-dp-on-graphs/).

## Link & metadata

| Field | Value |
|---|---|
| LeetCode | [787. Cheapest Flights Within K Stops](https://leetcode.com/problems/cheapest-flights-within-k-stops/) |
| Difficulty | Medium |
| Pattern | Shortest path with a limit on edges = DP over (node, edges used) |
| Prerequisite topics | [DP on Graphs](../../../04-advanced-dp-patterns/08-dp-on-graphs/), [0/1 Knapsack](../../../03-subsequence-and-knapsack-dp/02-zero-one-knapsack/), [Rolling arrays](../../../05-dp-optimization/02-rolling-arrays/) |

## Examples

```
Example A: n = 3, flights 0->1 (100), 1->2 (100), 0->2 (500), src 0, dst 2

        100        100
   0 -------> 1 -------> 2
    \_____________________^
             500
k = 1 (two flights allowed): 0 -> 1 -> 2 costs 200   -> answer 200
k = 0 (one flight allowed):  only 0 -> 2 is possible -> answer 500
```
Example B: `n = 4`, flights `0→1 100, 1→2 100, 2→0 100, 1→3 600, 2→3 200`, `k = 1` → `0→1→3 = 700`. (`0→1→2→3 = 400` needs 2 stops.)

Edge case: `dst` unreachable → `-1`.

## Brute-force intuition

DFS over every route from `src` that uses at most `k + 1` flights, keeping the cheapest that ends at `dst`. With `E` flights per city, this explores up to `E^(k+1)` routes, and the same "(city, flights left)" situation is reached by many different routes.

## Deriving the DP (7 questions)

### 1. What is changing?
The current city and the number of flights we may still take.

### 2. What does the state represent?
Top-down: `cheapest(city, flightsLeft)` = the minimum price to get from `city` to `dst` using at most `flightsLeft` flights (∞ if impossible).
Bottom-up: `cost[t][v]` = the minimum price to get from `src` to `v` using at most `t` flights.

### 3. What are the choices?
Which outgoing flight to take next (top-down), or which last flight brought us into `v` (bottom-up) — or take no more flights.

### 4. What is the recurrence?
```
cheapest(city, f) = 0                                       if city == dst
                  = min over flights city->next (price p) of p + cheapest(next, f - 1)

cost[t][v] = min( cost[t-1][v],  min over flights u->v (price p) of cost[t-1][u] + p )
```

### 5. What are the base cases?
`cheapest(dst, f) = 0`; `cheapest(city ≠ dst, 0) = ∞` (no flights left). Bottom-up: `cost[0][src] = 0`, every other `cost[0][v] = ∞`.

### 6. What is the order of computation?
Layer `t` reads only layer `t − 1`, so compute `t = 1, 2, …, k + 1`. Inside a layer the edge order does not matter **as long as you read from the previous layer**. Answer: `cost[k+1][dst]`.

### 7. Can the state space be optimized?
Only the previous layer is read ⇒ keep two arrays of size `n` (`prev`, `next`). Updating a single array in place is **wrong**: a city improved in this round could be used again in the same round, secretly spending two flights in one layer.

## Recursive solution

```java
int cheapest(int city, int flightsLeft) {
    if (city == dst) return 0;
    if (flightsLeft == 0) return INF;
    int best = INF;
    for (int[] f : outgoing[city])
        best = Math.min(best, f[1] + cheapest(f[0], flightsLeft - 1));
    return best;
}
```

## Memoization

Cache on `(city, flightsLeft)`: `n × (k + 2)` states. See [`solution/Memoization.java`](solution/Memoization.java).

## Tabulation

Fill `cost[t][v]` layer by layer, each layer copying the previous one and relaxing every flight from it. See [`solution/Tabulation.java`](solution/Tabulation.java).

## Space optimization

Keep `prev` and `next` arrays of size `n`; after each round `prev = next`. This is exactly Bellman–Ford stopped after `k + 1` rounds. See [`solution/Optimized.java`](solution/Optimized.java) and [Rolling arrays](../../../05-dp-optimization/02-rolling-arrays/).

## Dry run

Example A, `k = 1` (so layers `t = 0, 1, 2`):

| t | cost to city 0 | city 1 | city 2 | how |
|---|---|---|---|---|
| 0 | 0 | ∞ | ∞ | base case |
| 1 | 0 | 100 | 500 | from layer 0: 0→1 (0+100), 0→2 (0+500) |
| 2 | 0 | 100 | **200** | from layer 1: 1→2 (100+100) beats 500 |

Answer `cost[2][2] = 200`. With `k = 0` we stop at `t = 1` and answer 500.

## Complexity

| Approach | Time | Space | Why |
|---|---|---|---|
| Brute-force DFS | O(E^(k+1)) | O(k) | every route of ≤ k+1 flights |
| Memoization | O((k+1)·(n+E)) | O(n·k) | each (city, f) state scans that city's flights once |
| Tabulation | O((k+1)·(n+E)) | O(n·k) | k+1 layers, each copies n values and relaxes E flights |
| Two rolling arrays | O((k+1)·(n+E)) | O(n) | only two layers are alive |

## Why the optimized approach is better

Same time as the table but O(n) memory, and it is the standard "Bellman–Ford with at most K edges" template you can reuse anywhere a path has a length limit.

## Common mistakes

- Plain Dijkstra on cost only: discards a pricier path with fewer stops that is needed later.
- Single-array Bellman–Ford: lets one round chain several flights.
- Off-by-one: `k` stops means `k + 1` flights.
- `INF + price` overflowing — skip relaxations from `INF`.

## Key takeaway

When a path problem has a limit (stops, fuel, time), put the **remaining budget into the state**. The budget strictly shrinks, so the state graph is a DAG and DP works even if the original graph has cycles.

## Related problems

- [DP on Graphs](../../../04-advanced-dp-patterns/08-dp-on-graphs/) — DAG DP and shortest-path recurrences.
- [1976. Number of Ways to Arrive at Destination](../1976-number-of-ways-to-arrive-at-destination/) — shortest paths plus counting.
- [0/1 Knapsack](../../../03-subsequence-and-knapsack-dp/02-zero-one-knapsack/) — the original "budget in the state" idea.
