# 2246. Longest Path With Different Adjacent Characters

> **Pattern:** Tree DP on a general tree — "longest downward chain" + "best two children" (diameter style) · **Difficulty:** Hard
> **Builds on:** [DP on Trees](../../../04-advanced-dp-patterns/07-dp-on-trees/) (Diameter of Binary Tree / Binary Tree Maximum Path Sum)

---

## Problem overview

A rooted tree with `n` nodes is given as a `parent` array (`parent[0] = -1` is the root), and every node `i` carries the letter `s[i]`. Find the number of nodes on the longest path in which **no two neighbouring nodes have the same letter**. The path may bend at any node; it does not have to pass through the root.

**Why tree DP.** It is the diameter problem from [DP on Trees](../../../04-advanced-dp-patterns/07-dp-on-trees/) with two twists: (1) the tree is n-ary, not binary, and (2) an edge can only be used if its two letters differ. The "pass a downward chain up, combine the best two at the bend" idea carries over unchanged.

## Link & metadata

| Field | Value |
|---|---|
| LeetCode | [2246. Longest Path With Different Adjacent Characters](https://leetcode.com/problems/longest-path-with-different-adjacent-characters/) |
| Difficulty | Hard |
| Pattern | Post-order tree DP, top-two children |
| Prerequisite topics | [DP on Trees](../../../04-advanced-dp-patterns/07-dp-on-trees/), [Order of computation](../../../00-foundations/11-transition-dependencies-and-computation-order/) |

## Examples

Example A: `parent = [-1,0,0,1,1,2]`, `s = "abacbe"`

```
            0(a)
           /    \
        1(b)    2(a)      edge 0-2 is unusable (a-a)
        /  \       \
     3(c)  4(b)    5(e)   edge 1-4 is unusable (b-b)
```
Longest valid path `3 → 1 → 0` has **3** nodes.

Example B: `parent = [-1,0,0,0]`, `s = "aabc"` → path `2 → 0 → 3` (b-a-c), **3** nodes.

Edge cases: a single node → `1`; `"aaa"` on a chain → `1` (no edge can be used).

## Brute-force intuition

Start a search from every node and explore every simple path, keeping the longest valid one. O(n²) paths in a chain — too slow for `n = 10^5`. But every path has a single highest node (its "bend"), and the two arms hanging from the bend are *downward chains*. If every node knows its longest valid downward chain, each bend can be evaluated in time proportional to its children.

## Deriving the DP (7 questions)

### 1. What is changing?
The node `v` whose subtree we are looking at.

### 2. What does the state represent?
`down[v]` = the number of nodes on the longest valid path that **starts at `v` and goes only downward** into `v`'s subtree.

### 3. What are the choices?
For `down[v]`: continue into one child `c` whose letter differs from `s[v]`, or stop at `v`.
For the global answer: at bend `v`, choose up to **two** such children to form the two arms.

### 4. What is the recurrence?
```
down[v] = 1 + max( 0, max over children c with s[c] != s[v] of down[c] )
answer  = max over v of  1 + best1(v) + best2(v)
```
`best1, best2` are the two largest `down[c]` among children with a different letter (0 if missing).

### 5. What are the base cases?
A leaf: `down = 1`, contributes path length 1. Children with the same letter are simply ignored (as if they contributed 0).

### 6. What is the order of computation?
Children before parents (post-order). Recursion on `n = 10^5` nodes may overflow the default stack in a chain-shaped tree, so the iterative version processes nodes in **reverse BFS order**.

### 7. Can the state space be optimized?
A parent reads only `down[c]` of its own children, and only the top two matter, so per node we track two numbers while scanning its children. `down` itself must be stored (O(n)) until the parent reads it. No separate `Optimized.java`: the DP is already O(n) time.

## Recursive solution

```java
int dfs(int v) {                                 // returns down[v]
    int best1 = 0, best2 = 0;
    for (int c : children[v]) {
        int chain = dfs(c);                      // always recurse: c's subtree may hold the answer
        if (s.charAt(c) == s.charAt(v)) continue;
        if (chain > best1) { best2 = best1; best1 = chain; }
        else if (chain > best2) best2 = chain;
    }
    answer = Math.max(answer, 1 + best1 + best2);
    return 1 + best1;
}
```

## Memoization

Each node is visited exactly once; its `down` value is returned to (and so cached by) its parent. See [`solution/Memoization.java`](solution/Memoization.java).

## Tabulation

1. BFS from node 0 to get `order` (parents before children).
2. Walk `order` backwards. For node `v` the `down` values of all children are final; scan them, keep top-two, update the answer, set `down[v]`.

Instead of scanning children, an equivalent trick is to let each child *push* its value to its parent: walking backwards, node `c` updates `best1/best2[parent[c]]`. See [`solution/Tabulation.java`](solution/Tabulation.java).

## Space optimization

O(n) arrays (`down`, `best1`, `best2`, `order`) are the minimum: each value must survive until its parent is processed. The recursion stack is removed in the tabulated version, which is the practical optimization here.

## Dry run

Example A, in reverse BFS order `5, 4, 3, 2, 1, 0`:

| v | letter | valid children (down) | best1 | best2 | path through v | down[v] | answer |
|---|---|---|---|---|---|---|---|
| 5 | e | – | 0 | 0 | 1 | 1 | 1 |
| 4 | b | – | 0 | 0 | 1 | 1 | 1 |
| 3 | c | – | 0 | 0 | 1 | 1 | 1 |
| 2 | a | 5(1) | 1 | 0 | 2 | 2 | 2 |
| 1 | b | 3(1); 4 skipped (b=b) | 1 | 0 | 2 | 2 | 2 |
| 0 | a | 1(2); 2 skipped (a=a) | 2 | 0 | **3** | 3 | **3** |

## Complexity

| Approach | Time | Space | Why |
|---|---|---|---|
| Brute force from every node | O(n²) | O(n) | one DFS per start node |
| Recursive DFS | O(n) | O(n) | every node and edge once; stack up to n |
| Reverse-BFS tabulation | O(n) | O(n) | BFS + one backward pass, no recursion |

## Why the optimized approach is better

One pass replaces n searches. The tabulated version also survives a 10^5-node chain where naive recursion could overflow the stack.

## Common mistakes

- Skipping recursion into a same-letter child. Its own subtree can still contain the best path; only the *edge* is unusable.
- Keeping only the best child (that gives the longest downward chain, not the longest path).
- Counting edges instead of nodes (answer is `1 + best1 + best2` nodes).

## Key takeaway

"Longest path in a tree" = for each node, **best two downward chains + 1**, while passing only the **best one** upward. Edge restrictions just filter which children count.

## Related problems

- [DP on Trees](../../../04-advanced-dp-patterns/07-dp-on-trees/) — 543 Diameter and 124 Maximum Path Sum use exactly this pattern on binary trees.
- [968. Binary Tree Cameras](../968-binary-tree-cameras/) — tree DP with several states per node.
- [329. Longest Increasing Path in a Matrix](../329-longest-increasing-path-in-a-matrix/) — longest path again, but on a DAG instead of a tree.
