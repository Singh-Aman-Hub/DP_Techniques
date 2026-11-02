# 329. Longest Increasing Path in a Matrix

> **Pattern:** DP on a DAG hidden inside a grid (longest path, memoized DFS) · **Difficulty:** Hard
> **Builds on:** [DP on Graphs](../../../04-advanced-dp-patterns/08-dp-on-graphs/) and [2D / grid DP](../../../02-two-dimensional-and-grid-dp/01-2d-dp-state-representation/)

---

## Problem overview

Given a grid of integers, find the length (number of cells) of the longest path that moves up, down, left or right and whose values are **strictly increasing** along the way. Diagonal moves and wrapping around are not allowed.

**Why this is graph DP, not ordinary grid DP.** In [Unique Paths](../../../02-two-dimensional-and-grid-dp/02-unique-paths/) you only move right/down, so "row by row, left to right" is a valid computation order. Here you may move in all four directions, so there is no fixed loop order. What saves us is the *strictly increasing* rule: you can never come back to a cell, so the moves form a **directed acyclic graph (DAG)**. Longest path in a DAG is the classic problem of [DP on Graphs](../../../04-advanced-dp-patterns/08-dp-on-graphs/).

## Link & metadata

| Field | Value |
|---|---|
| LeetCode | [329. Longest Increasing Path in a Matrix](https://leetcode.com/problems/longest-increasing-path-in-a-matrix/) |
| Difficulty | Hard |
| Pattern | Longest path in an implicit DAG; memoized DFS or topological order |
| Prerequisite topics | [DP on Graphs](../../../04-advanced-dp-patterns/08-dp-on-graphs/), [2D DP state representation](../../../02-two-dimensional-and-grid-dp/01-2d-dp-state-representation/), [Order of computation](../../../00-foundations/11-transition-dependencies-and-computation-order/) |

## Examples

```
Example A                 Example B
 9  9  4                   3  4  5
 6  6  8                   3  2  6
 2  1  1                   2  2  1
answer 4: 1 -> 2 -> 6 -> 9 answer 4: 3 -> 4 -> 5 -> 6
```
Edge cases: a `1×1` grid → `1`; a grid where every value is equal → `1` (no move is strictly increasing).

## Brute-force intuition

From every cell, run a DFS trying all four directions whenever the neighbour is larger; record the deepest path. The same cell is re-explored from many different starting cells, and the number of increasing paths can be exponential.

## Deriving the DP (7 questions)

### 1. What is changing?
The current cell `(r, c)`. Nothing else matters: because values strictly increase, the path so far can never interfere with the path ahead (we can't revisit cells anyway), so we don't need a "visited" set in the state.

### 2. What does the state represent?
`len[r][c]` = the number of cells on the longest strictly increasing path that **starts** at `(r, c)`.

### 3. What are the choices?
Move to any of the (up to) four neighbours whose value is strictly greater, or stop here.

### 4. What is the recurrence?
```
len[r][c] = 1 + max( 0, max over neighbours (nr,nc) with grid[nr][nc] > grid[r][c] of len[nr][nc] )
answer    = max over all cells of len[r][c]
```

### 5. What are the base cases?
A cell with no larger neighbour (a "local peak") has `len = 1`.

### 6. What is the order of computation?
`len` of a cell depends on its **larger** neighbours. So larger values must be finished first:
- top-down: memoized DFS discovers the order automatically;
- bottom-up: sort cells by value **descending** and fill in that order (a topological order of the DAG). Ties never depend on each other (equal values are not an edge), so their order is irrelevant.

### 7. Can the state space be optimized?
Every cell's value may be read by up to four neighbours at different times, so the `m × n` table is needed. No separate `Optimized.java`.

## Recursive solution

```java
int longestFrom(int r, int c) {
    int best = 1;
    for (int[] d : DIRECTIONS) {
        int nr = r + d[0], nc = c + d[1];
        if (inside(nr, nc) && grid[nr][nc] > grid[r][c])
            best = Math.max(best, 1 + longestFrom(nr, nc));
    }
    return best;
}
```

## Memoization

Add `len[r][c]` (0 = not computed). Each cell is solved once; later calls return the cached value. No visited set is needed because the strict increase forbids cycles. See [`solution/Memoization.java`](solution/Memoization.java).

## Tabulation

Sort all cells by value, largest first. When a cell is processed, every strictly larger neighbour is already final. See [`solution/Tabulation.java`](solution/Tabulation.java). (Kahn's algorithm, peeling the DAG layer by layer, is an alternative topological order that gives the same answer.)

## Space optimization

Not applicable beyond the `m × n` table: unlike row-by-row grid DP, there is no "previous row only" structure because the dependency order follows values, not positions.

## Dry run

Example A, cells processed in descending value:

| Step | Cell (value) | larger neighbours (their len) | `len` |
|---|---|---|---|
| 1 | (0,0) 9 | – | 1 |
| 2 | (0,1) 9 | – | 1 |
| 3 | (1,2) 8 | – | 1 |
| 4 | (1,0) 6 | (0,0)=1 | 2 |
| 5 | (1,1) 6 | (0,1)=1, (1,2)=1 | 2 |
| 6 | (0,2) 4 | (0,1)=1, (1,2)=1 | 2 |
| 7 | (2,0) 2 | (1,0)=2 | 3 |
| 8 | (2,1) 1 | (1,1)=2, (2,0)=3 | **4** |
| 9 | (2,2) 1 | (1,2)=1 | 2 |

Maximum = **4** (path `1 → 2 → 6 → 9`).

## Complexity

| Approach | Time | Space | Why |
|---|---|---|---|
| Brute force DFS | exponential | O(mn) | the same cells are re-explored for every path through them |
| Memoized DFS | O(mn) | O(mn) | each cell computed once, 4 neighbour checks each; stack up to mn |
| Sorted tabulation | O(mn log(mn)) | O(mn) | sorting dominates; then O(1) work per cell |

## Why the optimized approach is better

Memoization collapses exponentially many paths into `m·n` states. Spotting that "strictly increasing ⇒ no cycles ⇒ DAG" is what makes DP legal at all.

## Common mistakes

- Keeping a `visited` array in the DFS and putting it into the state — unnecessary, and it breaks caching.
- Using `>=` instead of `>`: equal neighbours would create cycles and infinite recursion.
- Trying a fixed row/column loop order for tabulation — it is wrong when paths go up or left.

## Key takeaway

If moves can go in any direction, look for a quantity that strictly improves along every move. It turns the grid into a DAG, and DAG longest path is a one-line recurrence with memoization.

## Related problems

- [DP on Graphs](../../../04-advanced-dp-patterns/08-dp-on-graphs/) — DAG longest path and topological order.
- [2246. Longest Path With Different Adjacent Characters](../2246-longest-path-with-different-adjacent-characters/) — longest path on a tree.
- [2050. Parallel Courses III](../2050-parallel-courses-iii/) — longest (weighted) path on an explicit DAG.
