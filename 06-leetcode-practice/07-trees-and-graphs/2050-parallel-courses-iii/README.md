# 2050. Parallel Courses III

> **Pattern:** DP on a DAG in topological order (longest weighted path / critical path) · **Difficulty:** Hard
> **Builds on:** [DP on Graphs](../../../04-advanced-dp-patterns/08-dp-on-graphs/) and [Order of computation](../../../00-foundations/11-transition-dependencies-and-computation-order/)

---

## Problem overview

There are `n` courses (numbered `1 … n`). Course `i` takes `time[i−1]` months. Each relation `[a, b]` says course `a` must be finished before course `b` starts. You may take any number of courses at once. Return the minimum number of months needed to finish all of them. The relations are guaranteed to have no cycle.

**Why DP on a DAG.** A course can start only when its *slowest* prerequisite finishes. "Earliest finish of `v`" depends on the earliest finishes of its prerequisites — values on the incoming edges of a DAG. This is the longest-path-in-a-DAG problem from [DP on Graphs](../../../04-advanced-dp-patterns/08-dp-on-graphs/), with weights on the nodes. The only challenge is the **order of computation**: prerequisites first, i.e. a topological order ([why order matters](../../../00-foundations/11-transition-dependencies-and-computation-order/)).

## Link & metadata

| Field | Value |
|---|---|
| LeetCode | [2050. Parallel Courses III](https://leetcode.com/problems/parallel-courses-iii/) |
| Difficulty | Hard |
| Pattern | DAG DP, max over predecessors, Kahn's topological order |
| Prerequisite topics | [DP on Graphs](../../../04-advanced-dp-patterns/08-dp-on-graphs/), [Order of computation](../../../00-foundations/11-transition-dependencies-and-computation-order/), [State transitions](../../../00-foundations/10-state-transitions-and-base-cases/) |

## Examples

```
Example A: n = 3, relations [1,3], [2,3], time [3,2,5]

   1 (3 months) --\
                   >--> 3 (5 months)        3 starts at month max(3,2) = 3
   2 (2 months) --/                         and ends at 3 + 5 = 8   -> answer 8

Example B: n = 5, relations [1,5],[2,5],[3,5],[3,4],[4,5], time [1,2,3,4,5]

   1 ---------------\
   2 ---------------->  5          critical path 3 -> 4 -> 5 : 3 + 4 + 5 = 12
   3 ---> 4 --------/
```
Edge cases: a single course → its own time; no relations → the longest single course.

## Brute-force intuition

Simulate month by month, starting every course whose prerequisites are done. That works but costs O(total months × n). Or recursively compute each course's finish time from scratch, re-computing shared prerequisites again and again (exponential in a "diamond chain").

## Deriving the DP (7 questions)

### 1. What is changing?
The course `v` whose earliest finish time we want.

### 2. What does the state represent?
`finish[v]` = the earliest month at which course `v` can be completed, if every course starts as soon as it is allowed.

### 3. What are the choices?
There is no free choice — the course must wait for **all** prerequisites, so we take the latest of them. (DP is still the right tool: it is an optimum, "max", over dependent subproblems.)

### 4. What is the recurrence?
```
finish[v] = time[v] + max( 0, max over prerequisites u of v of finish[u] )
answer    = max over all v of finish[v]
```

### 5. What are the base cases?
A course with no prerequisite: `finish[v] = time[v]`.

### 6. What is the order of computation?
All prerequisites before the course ⇒ topological order. Two ways:
- top-down: memoized recursion over the *prerequisite* lists discovers the order itself;
- bottom-up: **Kahn's algorithm** — start with courses of in-degree 0; when a course is finished, push its finish time to each follower and decrease the follower's in-degree; a follower with in-degree 0 is ready.

### 7. Can the state space be optimized?
Every course's `finish` may be read by many followers at different times, so the O(n) array stays. No separate `Optimized.java`.

## Recursive solution

```java
int finish(int course) {
    int latestPrereq = 0;
    for (int u : prerequisites[course]) latestPrereq = Math.max(latestPrereq, finish(u));
    return time[course] + latestPrereq;
}
```

## Memoization

Cache `finish[course]` (0 = not computed, since every time is ≥ 1). Each course and each relation is processed once. See [`solution/Memoization.java`](solution/Memoization.java).

## Tabulation

Kahn's algorithm, carrying `start[v]` = latest finish among already-processed prerequisites:

```
queue <- all courses with in-degree 0
while queue not empty:
    u <- pop;  finish[u] = start[u] + time[u]
    for each follower v of u:
        start[v] = max(start[v], finish[u])
        if --indegree[v] == 0: push v
```
See [`solution/Tabulation.java`](solution/Tabulation.java). It avoids recursion, which matters for a chain of 5·10^4 courses.

## Space optimization

Not applicable: adjacency lists (O(n + m)) and one value per course are required.

## Dry run

Example B, Kahn's order:

| Pop | Course | `start` (latest prereq finish) | `finish = start + time` | followers updated |
|---|---|---|---|---|
| 1 | 1 | 0 | 1 | start[5] = 1 |
| 2 | 2 | 0 | 2 | start[5] = 2 |
| 3 | 3 | 0 | 3 | start[5] = 3, start[4] = 3 (4 now ready) |
| 4 | 4 | 3 | 7 | start[5] = 7 (5 now ready) |
| 5 | 5 | 7 | **12** | – |

Answer `max(1, 2, 3, 7, 12) = 12`.

## Complexity

| Approach | Time | Space | Why |
|---|---|---|---|
| Recompute recursively | exponential | O(n) | shared prerequisites recomputed |
| Memoization | O(n + m) | O(n + m) | each course once, each relation once; stack up to n |
| Kahn's tabulation | O(n + m) | O(n + m) | each course enqueued once, each relation relaxed once |

`m` = number of relations.

## Why the optimized approach is better

Linear time, no recursion depth risk, and the same Kahn loop also detects cycles (if fewer than `n` courses get popped) — useful in the next problem.

## Common mistakes

- Summing the prerequisites' times instead of taking the **max** (they run in parallel).
- Returning `finish[n]` (the last-numbered course) instead of the max over all courses.
- Forgetting the 1-based course numbers in `relations`.

## Key takeaway

"Things that run in parallel but wait for dependencies" = **max over predecessors + own cost**, computed in topological order. This is the critical-path method.

## Related problems

- [DP on Graphs](../../../04-advanced-dp-patterns/08-dp-on-graphs/) — topological order and DAG longest path.
- [329. Longest Increasing Path in a Matrix](../329-longest-increasing-path-in-a-matrix/) — DAG longest path on an implicit graph.
- [1857. Largest Color Value in a Directed Graph](../1857-largest-color-value-in-a-directed-graph/) — DAG DP with 26 values per node and cycle detection.
