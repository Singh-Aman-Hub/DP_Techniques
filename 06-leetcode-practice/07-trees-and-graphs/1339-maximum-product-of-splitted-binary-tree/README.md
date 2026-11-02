# 1339. Maximum Product of Splitted Binary Tree

> **Pattern:** Tree DP — subtree sums computed bottom-up (post-order) · **Difficulty:** Medium
> **Builds on:** [DP on Trees](../../../04-advanced-dp-patterns/07-dp-on-trees/)

---

## Problem overview

You get a binary tree whose nodes hold positive integers. Cut exactly one edge. The tree falls into two parts. Multiply the sums of the two parts. Return the largest possible product, taken modulo `10^9 + 7` **only at the very end** (maximise the real value first).

**Why this belongs to tree DP.** Cutting the edge above node `v` separates exactly *the subtree rooted at `v`* from the rest. So we need the sum of every subtree. A subtree's sum is built from its children's sums — a textbook "child answers flow up to the parent" computation from [DP on Trees](../../../04-advanced-dp-patterns/07-dp-on-trees/).

## Link & metadata

| Field | Value |
|---|---|
| LeetCode | [1339. Maximum Product of Splitted Binary Tree](https://leetcode.com/problems/maximum-product-of-splitted-binary-tree/) |
| Difficulty | Medium |
| Pattern | Post-order tree DP (subtree aggregate) |
| Prerequisite topics | [DP on Trees](../../../04-advanced-dp-patterns/07-dp-on-trees/), [Recursion fundamentals](../../../00-foundations/02-recursion-fundamentals/), [Order of computation](../../../00-foundations/11-transition-dependencies-and-computation-order/) |

## Examples

Example A (level order `[1,2,3,4,5,6]`):

```
        1
      /   \
     2     3
    / \   /
   4   5 6
```
Total = 21. Cutting above node 2 gives parts 11 and 10 → **110** (the best).

Example B (level order `[1,null,2,3,4,null,null,5,6]`): total 21, best cut above node 4 (subtree sum 15) → `15 · 6 = 90`.

Edge case (`[1,1]`): only one edge, product `1 · 1 = 1`.

## Brute-force intuition

For each of the `n − 1` edges: cut it, then walk both halves to sum them. Each walk is O(n), so O(n²) total. Wasteful: the sum of the subtree below an edge is recomputed again and again even though it never changes.

## Deriving the DP (7 questions)

### 1. What is changing?
The node we are looking at. Each node `v` stands for one candidate cut: the edge between `v` and its parent.

### 2. What does the state represent?
`sub[v]` = the sum of all values in the subtree rooted at `v` (including `v`).

### 3. What are the choices?
For computing `sub[v]` there is no choice — it is an aggregate. The *choice* lives at the top level: **which node's parent-edge to cut**. Cutting above `v` gives product `sub[v] · (total − sub[v])`.

### 4. What is the recurrence?
```
sub[v] = v.val + sub[v.left] + sub[v.right]
answer  = max over v ≠ root of  sub[v] · (total − sub[v])
```
`total = sub[root]`. Including the root in the max is harmless (`sub[root]·0 = 0`).

### 5. What are the base cases?
`sub[null] = 0` — an empty subtree adds nothing.

### 6. What is the order of computation?
A node needs both children first ⇒ **post-order** (children before parent). Recursively that is a normal DFS returning a value; iteratively it is "process nodes in reverse BFS / reverse pre-order".

### 7. Can the state space be optimized?
We do two passes: one to get `total`, one to evaluate products. We can avoid a second traversal by storing all subtree sums in a list during the first pass and scanning that list. The memory is O(n) either way (recursion stack or list) — for a tree DP the stack height is the irreducible cost.

## Recursive solution

```java
static long subtreeSum(TreeNode node, List<Long> sums) {
    if (node == null) return 0;
    long sum = node.val + subtreeSum(node.left, sums) + subtreeSum(node.right, sums);
    sums.add(sum);               // remember every subtree sum for the second step
    return sum;
}
```

## Memoization

In a tree each subtree is visited exactly once by one DFS, so there are no repeated calls to cache. The "memo" is the list of subtree sums: every `sub[v]` is computed once and then reused when we evaluate cut products. See [`solution/Memoization.java`](solution/Memoization.java) (recursive post-order DFS).

## Tabulation

Bottom-up means "children before parents" without recursion:
1. BFS from the root into an array `order` (parents appear before children).
2. Walk `order` **backwards** — now every child is processed before its parent — and set `sub[v] = val + sub[left] + sub[right]`.

See [`solution/Tabulation.java`](solution/Tabulation.java). This avoids a deep recursion stack on a skewed tree of 50,000 nodes.

## Space optimization

Nothing beyond O(n) is possible in general: we must remember, for each node, its sum until its parent reads it. The list of sums and the BFS order are both O(n). No separate `Optimized.java` is needed.

## Dry run

Example A, nodes in post-order:

| Node | `sub[left]` | `sub[right]` | `sub[v]` | product `sub·(21−sub)` | best so far |
|---|---|---|---|---|---|
| 4 | 0 | 0 | 4 | 4·17 = 68 | 68 |
| 5 | 0 | 0 | 5 | 5·16 = 80 | 80 |
| 2 | 4 | 5 | 11 | 11·10 = 110 | 110 |
| 6 | 0 | 0 | 6 | 6·15 = 90 | 110 |
| 3 | 6 | 0 | 9 | 9·12 = 108 | 110 |
| 1 | 11 | 9 | 21 (= total) | 21·0 = 0 | **110** |

## Complexity

| Approach | Time | Space | Why |
|---|---|---|---|
| Brute force | O(n²) | O(h) | re-sum one half for each of n−1 cuts |
| Post-order DFS | O(n) | O(n) | each node visited once; list of n sums + stack of height h |
| Iterative (reverse BFS) | O(n) | O(n) | BFS array and sum array of size n |

## Why the optimized approach is better

Every subtree sum is computed once and reused, turning O(n²) into O(n). It is the simplest example of "compute an aggregate per subtree, then make a decision per node".

## Common mistakes

- Taking `% 1_000_000_007` **before** comparing products. The modulo destroys ordering; maximise on `long`, reduce once at the end.
- Using `int` for the product: sums reach 5·10⁸, products ~10¹⁷ — use `long`.
- Computing the product during the first pass, before `total` is known. You need `total` first (two passes, or store sums).

## Key takeaway

"Cut one edge" in a tree = "pick one subtree". Precompute every subtree aggregate in post-order, then the decision at each node is O(1).

## Related problems

- [DP on Trees](../../../04-advanced-dp-patterns/07-dp-on-trees/) — covers 543 Diameter, 124 Max Path Sum, 337 House Robber III and 834 Sum of Distances (rerooting).
- [968. Binary Tree Cameras](../968-binary-tree-cameras/) — post-order with several states per node.
- [96. Unique Binary Search Trees](../96-unique-binary-search-trees/) — root + independent subtrees, counted instead of summed.
