# DP on Graphs — Walkthrough

Every DP you have written so far had a hidden picture: states are dots, and an arrow goes from each state to the states it depends on. In Fibonacci, `5 → 4` and `5 → 3`. In a grid, `(r,c) → (r-1,c)` and `(r,c-1)`. This lesson makes that picture explicit.

> **A DP works only if its dependency graph has no cycle.** A graph with directed edges and no cycle is called a **DAG** (Directed Acyclic Graph). A **topological order** of a DAG is an order in which every arrow points forward, and that is exactly the order in which to fill the DP table.

Main problem: [AtCoder DP G — Longest Path](https://atcoder.jp/contests/dp/tasks/dp_g). Then we apply the same idea to [LeetCode 329 — Longest Increasing Path in a Matrix](https://leetcode.com/problems/longest-increasing-path-in-a-matrix/) and to counting paths. We finish with what to do when the graph *does* have cycles: [LeetCode 787 — Cheapest Flights Within K Stops](https://leetcode.com/problems/cheapest-flights-within-k-stops/), solved with Bellman–Ford viewed as a DP.

---

## 1. Intuition

You are getting dressed. Some items must go on before others: socks before shoes, shirt before tie, trousers before shoes and belt. Draw an arrow from each item to every item that must come after it. You can never draw a cycle ("socks before shoes before socks" is impossible), so this is a DAG.

Now ask: "What is the longest chain of items that must be put on one after another?" Trousers → belt → jacket, maybe. To answer it for the jacket, you only need the answers for the things that come right before it. Those must be known first, so you work through the items in a valid dressing order. That valid order is a **topological order**, and walking through it while filling a table is **DP on a DAG**.

If there were a cycle, "the answer for socks needs the answer for shoes, which needs the answer for socks" would never finish. That is why DP needs a DAG.

---

## 2. Problem statement in our own words

Original: [AtCoder DP G — Longest Path](https://atcoder.jp/contests/dp/tasks/dp_g)

You get a directed graph with `N` vertices and `M` edges, guaranteed to contain no directed cycle. Find the number of **edges** in the longest directed path.

### Graph vocabulary

| Term | Meaning |
|---|---|
| vertex | a dot (also called a node) |
| directed edge `u → v` | a one-way arrow from u to v |
| path | a sequence of vertices joined by edges in their direction |
| cycle | a path that returns to its starting vertex |
| DAG | a directed graph with no cycle |
| in-degree of v | the number of edges pointing **into** v |
| topological order | a list of all vertices where every edge goes from an earlier vertex to a later one |

---

## 3. Examples

**Running example** (used in every section; 0-based vertices):

```
edges: 0→1  0→2  1→2  1→3  2→3  2→4  3→4

  +-----------+                    0 -> 2 drawn above
  |           v
  0 --> 1 --> 2 --> 3 --> 4        0->1, 1->2, 2->3, 3->4 along the line
        |     |     ^     ^
        +-----|-----+     |        1 -> 3 drawn below
              +-----------+        2 -> 4 drawn below
```

Answer: **4**, via `0 → 1 → 2 → 3 → 4`.

**AtCoder sample 1:** 4 vertices, edges `1→2, 1→3, 3→2, 2→4, 3→4` → **3** (`1 → 3 → 2 → 4`).

**AtCoder sample 2:** 6 vertices, edges `2→3, 4→5, 5→6` → **2** (`4 → 5 → 6`). The graph does not need to be connected.

**Edge case:** no edges at all → **0** (every path is a single vertex).

---

## 4. Deriving the solution — the 7 questions

### Q1. What is changing?
The **vertex we are currently at**. A path moves from vertex to vertex along edges.

### Q2. What does the state represent?
> `longestFrom[v]` = the number of edges on the longest path that **starts** at `v`.

(The mirror version, `longestTo[v]` = longest path that **ends** at `v`, works just as well. We use it in the bottom-up version.)

### Q3. What are the choices?
At vertex `v`, choose **which outgoing edge `v → w` to follow next**, or stop here.

### Q4. What is the recurrence?
```
longestFrom[v] = max over every edge v → w of ( 1 + longestFrom[w] )
               = 0 if v has no outgoing edge
answer         = max over all v of longestFrom[v]
```
Following `v → w` costs 1 edge, and from `w` we continue in the best possible way, which is `longestFrom[w]` by definition.

### Q5. What are the base cases?
A vertex with no outgoing edges (a **sink**) has `longestFrom = 0`: the path that starts there can only be the vertex itself.

### Q6. What is the order of computation?
`longestFrom[v]` needs `longestFrom[w]` for every edge `v → w`. So every **successor** must be done before `v`. That is a **reverse topological order**.

Equivalently, for `longestTo[v] = max over u → v of (1 + longestTo[u])`, every **predecessor** must be done first, which is a plain **topological order**. Section 7 builds one with Kahn's algorithm.

Why must the graph be acyclic? Suppose `a → b → a`. Then `longestFrom[a]` needs `longestFrom[b]`, which needs `longestFrom[a]`. No order can put `a` both before and after `b`. The recursion would never end, and the "longest path" would be infinite (go around the cycle forever).

### Q7. Can the state space be optimized?
Not in general. In an array DP, `dp[i]` reads only the last one or two entries, so we keep two variables. In a graph, an edge can jump from the first vertex to the last, so any earlier value might still be needed. We keep one number per vertex: O(V) space, which is already as small as the input.

---

## 5. Brute force: recursion

```java
static int longestFrom(int v) {
    int best = 0;
    for (int next : graph.get(v)) best = Math.max(best, 1 + longestFrom(next));
    return best;
}
```

This terminates **because** the graph is a DAG: every call moves forward along an edge and can never come back. But it repeats work. Recursion tree from vertex 0 in the running example:

```
from(0)
├── from(1)
│   ├── from(2)
│   │   ├── from(3)
│   │   │   └── from(4)
│   │   └── from(4)                  <- repeated
│   └── from(3)                      <- repeated (whole subtree again)
│       └── from(4)
└── from(2)                          <- repeated (whole subtree again)
    ├── from(3)
    │   └── from(4)
    └── from(4)
```

The number of calls equals the number of **paths**, and a DAG can have exponentially many. A chain of `k` "diamonds" (each vertex splits into two and merges again) has 2^k paths. With k = 30 that is over a billion calls.

---

## 6. Memoization (top-down)

**What is cached:** `memo[v] = longestFrom(v)`, with `-1` meaning "not computed".

```java
private static int longestFrom(int v) {
    if (memo[v] != -1) return memo[v];
    int best = 0;
    for (int next : graph.get(v)) best = Math.max(best, 1 + longestFrom(next));
    memo[v] = best;
    return best;
}
// answer = max over all v of longestFrom(v)
```

Dry run, calling `longestFrom(0)` first:

| Step | Event | memo after |
|---|---|---|
| 1 | from(0) → from(1) → from(2) → from(3) → from(4): sink, = 0 | memo[4]=0 |
| 2 | from(3) = 1 + memo[4] = 1 | memo[3]=1 |
| 3 | from(2): edge to 3 gives 2; edge to 4 is a **cache hit** (0) giving 1 → 2 | memo[2]=2 |
| 4 | from(1): edge to 2 gives 3; edge to 3 is a **cache hit** giving 2 → 3 | memo[1]=3 |
| 5 | from(0): edge to 1 gives 4; edge to 2 is a **cache hit** giving 3 → 4 | memo[0]=4 |
| 6 | outer loop calls from(1..4): all cache hits | – |

Answer = max(4, 3, 2, 1, 0) = **4**.

Notice that the DFS **discovered a reverse topological order by itself**: vertices finish in the order 4, 3, 2, 1, 0. A memoized DFS on a DAG always does this. You never have to compute the order explicitly.

---

## 7. Tabulation (bottom-up)

To fill a table with a loop, we need the order up front. **Kahn's algorithm** produces a topological order:

1. Count every vertex's in-degree.
2. Put every vertex with in-degree 0 into a queue. Nothing points to it, so it can go first.
3. Repeatedly take a vertex `u` from the queue, append it to the order, and "delete" its outgoing edges: decrease the in-degree of each `v` with `u → v`. When a vertex's in-degree reaches 0, all its predecessors are done, so it joins the queue.

We fill `longestTo` while running Kahn: when `u` leaves the queue, `longestTo[u]` is final (all its in-edges were processed), so we push it forward along each edge.

```java
int[] longestTo = new int[n];                     // longest path ENDING at v
for (int v = 0; v < n; v++) if (inDegree[v] == 0) ready.add(v);
while (!ready.isEmpty()) {
    int u = ready.poll();
    best = Math.max(best, longestTo[u]);
    for (int v : adj.get(u)) {
        longestTo[v] = Math.max(longestTo[v], longestTo[u] + 1);
        if (--inDegree[v] == 0) ready.add(v);
    }
}
```

Step by step on the running example. Initial in-degrees: `0:0, 1:1, 2:2, 3:2, 4:2`.

| Pop | Edges relaxed | longestTo [0,1,2,3,4] | in-degrees [0,1,2,3,4] | queue after |
|---|---|---|---|---|
| start | – | [0,0,0,0,0] | [0,1,2,2,2] | [0] |
| 0 | 0→1, 0→2 | [0,**1**,**1**,0,0] | [0,0,1,2,2] | [1] |
| 1 | 1→2, 1→3 | [0,1,**2**,**2**,0] | [0,0,0,1,2] | [2] |
| 2 | 2→3, 2→4 | [0,1,2,**3**,**3**] | [0,0,0,0,1] | [3] |
| 3 | 3→4 | [0,1,2,3,**4**] | [0,0,0,0,0] | [4] |
| 4 | – | [0,1,2,3,4] | – | [] |

Answer = max of `longestTo` = **4**. The topological order was `0, 1, 2, 3, 4`.

**Bonus — cycle detection for free.** If the graph has a cycle, the vertices on it never reach in-degree 0, so fewer than `n` vertices get popped. Counting the pops tells you whether the input really was a DAG. [LeetCode 1857](https://leetcode.com/problems/largest-color-value-in-a-directed-graph/) requires exactly this check.

---

## 8. Space optimization

Not applicable beyond O(V), as explained in Q7. An edge may connect any earlier vertex to any later one in the order, so no value can be thrown away early.

---

## 9. Dry run on a full example — and the same pattern for counting

Change one word in the question: instead of the **longest** path from 0 to 4, **count** the paths from 0 to 4.

> `ways[v]` = number of different paths from the source to `v`. `ways[source] = 1`. Each edge `u → v` adds `ways[u]` to `ways[v]`, because every path to `u` extends by that edge to a path to `v`.

The order is the same topological order; only `max(…, +1)` becomes `+=`:

| Pop | Edges | ways [0,1,2,3,4] |
|---|---|---|
| start | – | [1,0,0,0,0] |
| 0 | 0→1, 0→2 | [1,1,1,0,0] |
| 1 | 1→2, 1→3 | [1,1,2,1,0] |
| 2 | 2→3, 2→4 | [1,1,2,3,2] |
| 3 | 3→4 | [1,1,2,3,5] |
| 4 | – | [1,1,2,3,5] |

**5 paths**: 0-1-2-3-4, 0-1-2-4, 0-1-3-4, 0-2-3-4, 0-2-4. `CountPathsInDag.java` checks this against a brute force that walks every path.

This one table shows the whole "DP on a DAG" family. Pick the combine operation and the order stays the same:

| Question | Combine along edge u → v | Start value |
|---|---|---|
| longest path | `to[v] = max(to[v], to[u] + w)` | 0 |
| shortest path | `to[v] = min(to[v], to[u] + w)` | 0 at source, ∞ elsewhere |
| number of paths | `to[v] += to[u]` | 1 at source, 0 elsewhere |
| is v reachable | `to[v] = to[v] OR to[u]` | true at source |

---

## 10. Complexity analysis

| Approach | Time | Space | Why |
|---|---|---|---|
| Brute force DFS | O(number of paths), exponential in the worst case | O(V) stack | every path is walked separately |
| Memoized DFS | O(V + E) | O(V + E) | each vertex is solved once; solving it looks at each of its out-edges once |
| Kahn + tabulation | O(V + E) | O(V + E) | each vertex enters the queue once; each edge is relaxed once |

The memoized DFS can recurse V levels deep (a long chain). With V = 10⁵ in Java, you need a bigger stack or the iterative Kahn version. That is why Kahn is the safer default for big inputs.

---

## 11. Why this approach works

- **Every path into `v` comes from some predecessor `u`.** The longest path ending at `v` is the longest path ending at some `u` plus the edge `u → v`. The prefix must itself be a longest path to `u`, otherwise we could swap in a longer one. That is optimal substructure.
- **Topological order makes every read safe.** When we pop `u`, every edge into `u` has already been processed, so `longestTo[u]` will never change again. Anything we push forward from `u` is final.
- **No cycle means termination.** Each recursive call strictly advances in the topological order, so it cannot go on forever.

---

## 12. Application 1 — LeetCode 329: a DAG hidden in a grid

[LeetCode 329 — Longest Increasing Path in a Matrix](https://leetcode.com/problems/longest-increasing-path-in-a-matrix/): from any cell, you may step up, down, left or right to a cell with a **strictly larger** value. Find the length (number of cells) of the longest such walk.

### Why there are no cycles
Draw an edge from each cell to every neighbour with a strictly larger value. Along any walk the values strictly increase: `a < b < c < …`. To return to a cell you would need its value to be larger than itself, which is impossible. So the graph **is a DAG**, even though the grid lets you move in all four directions. That is the whole insight: no `visited` array is needed, and memoized DFS is safe.

(If the rule were "larger **or equal**", two equal neighbours would form a cycle, and the DP would break.)

### The 7 questions, briefly
1. **Changing:** the current cell `(r, c)`.
2. **State:** `longest[r][c]` = number of cells on the longest increasing walk **starting** at `(r, c)`.
3. **Choices:** step to any of the up to 4 strictly larger neighbours, or stop.
4. **Recurrence:** `longest[r][c] = 1 + max(longest[neighbour])` over larger neighbours; `1` if there are none.
5. **Base case:** a cell with no larger neighbour (a local maximum) has value 1.
6. **Order:** larger values first. Memoized DFS finds this order automatically. Alternatively, sort cells by value descending and fill in that order.
7. **Space:** one cell per grid cell; no reduction, for the same reason as Q7 above.

### Filled memo table on `[[9,9,4],[6,6,8],[2,1,1]]`

```
matrix            longest (memo after all DFS calls)
9 9 4             1 1 2
6 6 8             2 2 1
2 1 1             3 4 2
```

- `9`s and the `8` are local maxima → 1.
- `4` (top right) can step to 9 or 8 → 2.
- each `6` can step to the 9 above it → 2.
- `2` (bottom left) → 6 → 9 gives 3.
- the middle `1` at the bottom → 2 → 6 → 9 gives **4**, the answer.

Complexity: every cell is solved once and checks 4 neighbours: O(R·C) time and O(R·C) space. The brute force (no memo) re-explores the same cells from every starting point, which is exponential on a "snake" grid. `LongestIncreasingPathInMatrix.java` compares both.

---

## 13. When the graph has cycles: Bellman–Ford as DP over "edges used"

Shortest paths in a general graph cannot use `dist[v] = min over u → v of dist[u] + w` directly. With a cycle `0 → 1 → 2 → 0`, `dist[0]` depends on `dist[2]`, which depends on `dist[0]`: no valid order exists.

**The fix: add a dimension that only ever increases.** Add "how many edges have been used so far" to the state. Every transition goes from `t - 1` edges to `t` edges, so the new state graph is layered, and layers are acyclic even when the original graph is not.

```
layer t=0      layer t=1      layer t=2
  0  ----------> 1 ----------> 2 ...       arrows only go left -> right,
  1  ----------> 2 ----------> 0           so there is no cycle between layers
  2  ----------> 0 ----------> 1
```

That layered DP is the **Bellman–Ford** algorithm.

### LeetCode 787 — Cheapest Flights Within K Stops

[LeetCode 787](https://leetcode.com/problems/cheapest-flights-within-k-stops/): `n` cities and priced one-way flights. Find the cheapest price from `src` to `dst` with **at most `k` stops** (intermediate cities), or `-1`. At most `k` stops means **at most `k + 1` flights**, and the problem itself limits the number of edges, so the "edges used" dimension is part of the question.

1. **Changing:** the current city and the number of flights taken.
2. **State:** `cost[t][v]` = cheapest price to reach `v` from `src` using **at most** `t` flights.
3. **Choices:** take no extra flight, or take one more flight `u → v` from a city reached in `t - 1` flights.
4. **Recurrence:** `cost[t][v] = min( cost[t-1][v], min over flights u → v of cost[t-1][u] + price )`.
5. **Base case:** `cost[0][src] = 0`, `cost[0][other] = ∞`.
6. **Order:** layer by layer, `t = 1, 2, …, k+1`. Inside a layer, edges can be processed in any order because they read only layer `t - 1`.
7. **Space:** layer `t` reads only layer `t - 1`, so keep two rows (`previous`, `current`).

```java
int[] previous = new int[n];  Arrays.fill(previous, INF);  previous[src] = 0;
for (int t = 1; t <= k + 1; t++) {
    int[] current = previous.clone();              // "at most t" includes "at most t-1"
    for (int[] f : flights)
        if (previous[f[0]] != INF)
            current[f[1]] = Math.min(current[f[1]], previous[f[0]] + f[2]);
    previous = current;
}
```

### Dry run
`n = 4`, flights `0→1 (100), 1→2 (100), 2→0 (100), 1→3 (600), 2→3 (200)`, `src = 0`, `dst = 3`, `k = 1` (so at most 2 flights). Note the cycle 0 → 1 → 2 → 0.

| t | cost[0] | cost[1] | cost[2] | cost[3] | what changed |
|---|---|---|---|---|---|
| 0 | 0 | ∞ | ∞ | ∞ | base case |
| 1 | 0 | 100 | ∞ | ∞ | 0→1 |
| 2 | 0 | 100 | 200 | 700 | 1→2 = 200, 1→3 = 700 (2→3 reads cost[1][2] = ∞) |

Answer `cost[2][3]` = **700** (0 → 1 → 3). The cheaper route 0 → 1 → 2 → 3 costs 400 but needs 3 flights, so it becomes the answer only when `k = 2`.

### The classic bug: updating in place
If you update one array in place, a value written earlier in round `t` can be read later **in the same round**, using two flights in one round. With the edge order above, round 1 sets `cost[1] = 100`, then immediately `cost[2] = 200`, then `cost[3] = 400` — three flights while "at most one" is allowed. The `previous.clone()` is not a micro-detail; it is what keeps the layers separate.

Complexity: `k + 1` rounds × (copy `n` cells + relax `F` flights) = O((k+1)(n + F)) time, O(n) space.

---

## 14. Related problems (mentioned, not solved here)

- [LeetCode 1976 — Number of Ways to Arrive at Destination](https://leetcode.com/problems/number-of-ways-to-arrive-at-destination/): run Dijkstra. The edges that lie on some shortest path form a DAG (distance strictly increases along them), and processing vertices in the order Dijkstra finalises them is a topological order of that DAG. Count paths on it with `ways[v] += ways[u]`, exactly as in Section 9.
- [LeetCode 2050 — Parallel Courses III](https://leetcode.com/problems/parallel-courses-iii/): courses with durations and prerequisites. The earliest finish time of a course is its duration plus the latest finish among its prerequisites: a weighted longest path in a DAG, filled in Kahn order.
- [LeetCode 1857 — Largest Color Value in a Directed Graph](https://leetcode.com/problems/largest-color-value-in-a-directed-graph/): state `count[v][colour]` = the most nodes of that colour on any path ending at `v`, pushed along edges in Kahn order. Return -1 when Kahn pops fewer than `n` vertices (a cycle).

---

## 15. Common mistakes

1. **Running DP on a graph that has cycles.** Memoized DFS will recurse forever (or return wrong cached values from a half-finished call). Check acyclicity, or add a dimension that makes the state graph acyclic (edges used, time, a strictly increasing value).
2. **Starting only from vertex 0.** The longest path can start anywhere. Take the max over all vertices, or seed Kahn's queue with **every** in-degree-0 vertex.
3. **Reading a vertex before it is final.** In tabulation, push a vertex's value forward only when it leaves the queue (in-degree 0), not when it is first touched.
4. **"Larger or equal" in LeetCode 329.** Equal neighbours create cycles. The problem says strictly increasing; keep the `>`.
5. **In-place Bellman–Ford with an edge limit.** Always read the previous layer when the number of edges matters.
6. **`k` stops vs. `k` flights.** `k` stops = `k + 1` flights.
7. **Overflow with `INF = Integer.MAX_VALUE`.** `INF + price` wraps to a negative number. Skip unreachable sources before adding.

---

## 16. When to use this technique / how to recognize similar problems

- The input is a directed graph described as "prerequisites", "dependencies", "tasks that must happen before", or the problem promises "no cycles". → DP in topological order.
- A grid or graph where you may move only to **strictly** larger / later / higher items. → a hidden DAG; memoized DFS.
- "Number of ways" or "longest/shortest" path on a DAG. → the table in Section 9.
- Shortest path with a **limit on the number of edges** (stops, moves, steps). → Bellman–Ford layers, `dp[t][v]`.
- General shortest path with non-negative weights and no edge limit → Dijkstra (not DP in the table-filling sense, but its finalisation order can drive a DP, as in 1976).

---

## 17. Summary and revision notes

- DP needs an **acyclic dependency order**. On a graph, that is a **topological order** of a DAG.
- Memoized DFS on a DAG finds a reverse topological order by itself. Kahn's algorithm finds one iteratively and detects cycles.
- Longest path, shortest path, counting paths and reachability on a DAG are all one loop with different combine operations.
- A graph with cycles can still support DP if the state gains a dimension that only increases (Bellman–Ford: number of edges used).

```
+---------------------------------------------------------------+
|  DAG:      to[v] = combine over u -> v of ( to[u] (+) w(u,v) )  |
|            fill in topological order (Kahn / memo DFS)          |
|                                                               |
|  Bellman-Ford (edge-limited):                                  |
|     cost[t][v] = min( cost[t-1][v],                            |
|                       min over u -> v of cost[t-1][u] + w )    |
|     cost[0][src] = 0;  read ONLY layer t-1 when writing t      |
+---------------------------------------------------------------+
```

---

## 18. Exercises

**Exercise 1.** Give a topological order of the running example different from `0,1,2,3,4`, or explain why there is none.

<details>
<summary>Answer</summary>

There is none. Edges `0→1`, `1→2`, `2→3`, `3→4` form a chain through all five vertices, so the order is forced. A graph has a unique topological order exactly when its order contains a path through every vertex (a Hamiltonian path).
</details>

**Exercise 2.** Count the paths from 0 to 4 in the DAG with edges `0→1, 0→2, 0→3, 1→4, 2→4, 3→4, 1→2, 2→3`.

<details>
<summary>Answer</summary>

Topological order 0, 1, 2, 3, 4. `ways = [1, 1, 1+1 = 2, 1+2 = 3, 1+2+3 = 6]`. **6 paths**: 0-1-4, 0-2-4, 0-3-4, 0-1-2-4, 0-2-3-4, 0-1-2-3-4.
</details>

**Exercise 3.** In LeetCode 329, why would a `visited[][]` array (as in an ordinary DFS) make the memoized solution *wrong*?

<details>
<summary>Answer</summary>

`visited` blocks a cell after the first visit. The memo **must** be allowed to answer for a cell reached again from another start; that reuse is the whole speed-up. There is no cycle to protect against, because values strictly increase along every step, so `visited` is useless at best. If it makes the DFS skip a cell instead of reading its memo, it gives wrong (too short) answers.
</details>

**Exercise 4.** Run the Bellman–Ford DP for LeetCode 787 on the 4-city example with `k = 2`. What is the answer, and in which round does it appear?

<details>
<summary>Answer</summary>

Round 3 (t = 3) reads `cost[2][2] = 200` and relaxes `2→3`: `cost[3][3] = min(700, 200 + 200) = 400`. Answer **400** via 0 → 1 → 2 → 3. (Round 3 also tries `2→0`: 300, which loses to 0.)
</details>
