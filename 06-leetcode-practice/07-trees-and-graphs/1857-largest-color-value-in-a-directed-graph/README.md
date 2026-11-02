# 1857. Largest Color Value in a Directed Graph

> **Pattern:** DAG DP with a vector of 26 values per node + cycle detection (Kahn) · **Difficulty:** Hard
> **Builds on:** [DP on Graphs](../../../04-advanced-dp-patterns/08-dp-on-graphs/) and [Parallel Courses III](../2050-parallel-courses-iii/) (Kahn's order)

---

## Problem overview

A directed graph has `n` nodes; node `i` is painted with the letter `colors[i]`. For any path, its *color value* is how many times its most frequent color appears on it. Return the largest color value over all paths — or `-1` if the graph contains a cycle (then a path could loop forever).

**Why DAG DP with 26 values.** "The best count of the most frequent color" is not decomposable on its own: the most frequent color of a long path may be a rare color in each of its pieces. The fix is to **split the problem by color**: for each of the 26 letters separately, find the path that contains the most of that letter. Each of those is a plain longest-path-in-a-DAG problem from [DP on Graphs](../../../04-advanced-dp-patterns/08-dp-on-graphs/). We solve all 26 at once by storing a vector of 26 numbers per node. Kahn's algorithm (seen in [2050](../2050-parallel-courses-iii/)) gives the order *and* detects cycles.

## Link & metadata

| Field | Value |
|---|---|
| LeetCode | [1857. Largest Color Value in a Directed Graph](https://leetcode.com/problems/largest-color-value-in-a-directed-graph/) |
| Difficulty | Hard |
| Pattern | DAG DP over (node, color), topological order, cycle detection |
| Prerequisite topics | [DP on Graphs](../../../04-advanced-dp-patterns/08-dp-on-graphs/), [State representation](../../../00-foundations/09-state-representation/), [Order of computation](../../../00-foundations/11-transition-dependencies-and-computation-order/) |

## Examples

```
Example A: colors = "abaca", edges 0->1, 0->2, 2->3, 3->4

   0(a) --> 1(b)
     \
      --> 2(a) --> 3(c) --> 4(a)

Path 0 -> 2 -> 3 -> 4 has colors a,a,c,a: 'a' appears 3 times  -> answer 3
```
Example B: `colors = "a"`, edge `0→0` (a self-loop is a cycle) → `-1`.
Edge case: one node, no edges → `1` (the path consisting of the node alone).

## Brute-force intuition

Enumerate every path, count the letters on it, keep the best. Exponentially many paths; and with a cycle the enumeration never ends — which is why the problem asks for `-1`.

## Deriving the DP (7 questions)

### 1. What is changing?
The node where the path currently ends, and **which color we are counting**.

### 2. What does the state represent?
`count[v][c]` = the largest number of nodes with color `c` on any path that **ends at `v`** (bottom-up version).
(Top-down version: on any path that **starts at `v`** — symmetric.)

### 3. What are the choices?
Which incoming edge `u → v` the path used to reach `v` — or none (the path starts at `v`).

### 4. What is the recurrence?
```
count[v][c] = (colors[v] == c ? 1 : 0) + max( 0, max over edges u->v of count[u][c] )
answer      = max over all v, c of count[v][c]
```
Because each color is handled independently, a different predecessor may be best for different colors — that is fine and correct, since each color's best path is separate.

### 5. What are the base cases?
A node with no incoming edges: `count[v][c] = 1` for its own color, `0` for all others.

### 6. What is the order of computation?
All predecessors before `v` ⇒ topological order (Kahn). If Kahn finishes with fewer than `n` nodes processed, some nodes are stuck in a cycle ⇒ return `-1`.
Top-down: DFS with three marks (unvisited / on the current path / done); reaching a node that is *on the current path* means a cycle.

### 7. Can the state space be optimized?
We need 26 numbers per node: O(26·n). They cannot be dropped early in general because a node's vector is read by all of its successors. No separate `Optimized.java`.

## Recursive solution

```java
int[] best(int v) {                           // best[v][c] for paths starting at v
    int[] result = new int[26];
    for (int w : next[v]) {
        int[] sub = best(w);
        for (int c = 0; c < 26; c++) result[c] = Math.max(result[c], sub[c]);
    }
    result[colors.charAt(v) - 'a']++;
    return result;
}
```

## Memoization

Cache each node's 26-vector, and mark nodes `ON_PATH` while their recursion is active; meeting an `ON_PATH` node signals a cycle. See [`solution/Memoization.java`](solution/Memoization.java).

## Tabulation

Kahn's algorithm. When node `u` is popped, all its predecessors are done, so first add its own color: `count[u][colors[u]]++`. Then push its vector to every successor `v`: `count[v][c] = max(count[v][c], count[u][c])`. See [`solution/Tabulation.java`](solution/Tabulation.java).

## Space optimization

Not applicable — see question 7.

## Dry run

Example A, Kahn's order `0, 1, 2, 3, 4` (columns are counts of a / b / c, *after* adding the node's own color):

| Pop | Node (color) | received from predecessors | own color added | `count` (a, b, c) | best so far |
|---|---|---|---|---|---|
| 1 | 0 (a) | – | a | (1, 0, 0) | 1 |
| 2 | 1 (b) | from 0: (1,0,0) | b | (1, 1, 0) | 1 |
| 3 | 2 (a) | from 0: (1,0,0) | a | (2, 0, 0) | 2 |
| 4 | 3 (c) | from 2: (2,0,0) | c | (2, 0, 1) | 2 |
| 5 | 4 (a) | from 3: (2,0,1) | a | (**3**, 0, 1) | **3** |

All 5 nodes were processed, so there is no cycle; answer **3**.

## Complexity

| Approach | Time | Space | Why |
|---|---|---|---|
| Enumerate paths | exponential | O(n) | number of paths |
| Memoized DFS | O(26·(n + m)) | O(26·n + m) | each node once, each edge merges a 26-vector |
| Kahn tabulation | O(26·(n + m)) | O(26·n + m) | same work, no recursion |

## Why the optimized approach is better

Splitting by color turns one non-decomposable objective into 26 decomposable ones that share a single pass. The constant 26 is small, so the solution is linear in practice.

## Common mistakes

- Tracking only the single best count per node: the best color can change along a path.
- Adding the node's own color *before* all predecessors have pushed (in Kahn, add it when the node is popped).
- Forgetting cycle detection (including self-loops like `0 → 0`).

## Key takeaway

If the objective is "max over categories of something", **put the category in the state** and solve all categories in the same pass.

## Related problems

- [2050. Parallel Courses III](../2050-parallel-courses-iii/) — the same Kahn pass with one value per node.
- [DP on Graphs](../../../04-advanced-dp-patterns/08-dp-on-graphs/) — topological DP and cycle detection.
- [329. Longest Increasing Path in a Matrix](../329-longest-increasing-path-in-a-matrix/) — DAG longest path where acyclicity is guaranteed by values.
