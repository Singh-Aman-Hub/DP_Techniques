# DP on Trees

> **Module:** 04 — Advanced DP Patterns · **Difficulty:** Advanced · **Estimated time:** 2.5–3.5 hours

## 1. What is this topic?

DP on trees solves a problem by computing an answer for **every subtree**, starting at the leaves and moving up to the root. A node's answer is built only from its children's answers. A post-order depth-first search (visit the children, then the node) visits the nodes in exactly that order, so post-order DFS is **tabulation on a tree**.

## 2. Why does this concept exist?

Trees are everywhere in interview problems: binary trees, org charts, file systems, networks with no cycles. Trying every combination of choices across the nodes is exponential. A tree has a property that arrays share: once you fix what happens at a node, its subtrees are **independent** of each other. Independence plus overlapping work is exactly when DP applies.

## 3. What problem does it solve?

- Choose a set of nodes under a parent/child constraint with the best total (House Robber III, independent sets, tree matching).
- Find the longest or heaviest **path** that may bend anywhere (diameter, maximum path sum).
- Compute a value **for every node as the root** in linear time (sum of distances, via rerooting).

## 4. Prerequisites

- [House Robber](../../01-one-dimensional-dp/06-house-robber/): the take/skip recurrence we lift onto a tree.
- [Recursion fundamentals](../../00-foundations/02-recursion-fundamentals/): you must be comfortable with recursive tree traversals.
- [Memoization](../../00-foundations/06-memoization/) and [Tabulation](../../00-foundations/07-tabulation/).
- [Transition dependencies and computation order](../../00-foundations/11-transition-dependencies-and-computation-order/).
- [State representation](../../00-foundations/09-state-representation/): adding an extra "bit" to a state.

## 5. What you will learn

- Why post-order DFS is the bottom-up order on a tree, and how to get that order without recursion (reverse BFS).
- How to return **several values** from a child (a `{take, skip}` pair) instead of peeking at grandchildren.
- The **global answer vs. returned value** split for path problems.
- **Rerooting**: two passes that give an answer for every node in O(n).

## 6. Important terminology

| Term | Meaning in plain English |
|---|---|
| Subtree of v | v and every node below it |
| Leaf | a node with no children; the base case of tree DP |
| Post-order | children first, then the node; the bottom-up order on a tree |
| Pre-order | the node first, then its children; the top-down order used by rerooting's second pass |
| Height | number of edges on the longest downward path from a node |
| Returned value | what a node hands to its parent; must be something the parent can extend |
| Global answer | a best-so-far variable updated at every node, for answers the parent cannot extend (bent paths) |
| Rerooting | computing an answer for every possible root by adjusting a parent's answer across one edge |

## 7. Where this is used in real problems

- Picking non-adjacent nodes: House Robber III, Independent Set (AtCoder DP P), Tree Matching (CSES 1130).
- Path questions: diameter of a tree or of a network, maximum path sum, longest path with constraints on adjacent labels.
- Per-node answers: sum of distances, "best place to put a server", subtree colourings for every root (AtCoder DP V).
- Cutting a tree into parts: maximum product after removing one edge (LeetCode 1339).

## 8. After completing this topic you should be able to…

- [ ] Explain why post-order DFS is a valid computation order for tree DP.
- [ ] Write House Robber III as brute force, memoization, iterative tabulation and a pair-returning DFS.
- [ ] Decide what a node should **return** and what it should **record globally** for a path problem.
- [ ] Solve LeetCode 543 and 124 without looking.
- [ ] Derive the rerooting formula for LeetCode 834 and dry-run it on a small tree.
- [ ] Avoid stack overflows on deep trees by using an iterative order.

## 9. Practice problems

| # | Problem | Platform/ID | Difficulty | Pattern tested | Why it belongs here |
|---|---|---|---|---|---|
| 1 | [Diameter of Binary Tree](https://leetcode.com/problems/diameter-of-binary-tree/) | LeetCode 543 | Easy | return height, record bent path | the simplest "global vs. returned" problem |
| 2 | [House Robber III](https://leetcode.com/problems/house-robber-iii/) | LeetCode 337 | Medium | take/skip pair per node | the core lesson of this topic |
| 3 | [Maximum Product of Splitted Binary Tree](https://leetcode.com/problems/maximum-product-of-splitted-binary-tree/) | LeetCode 1339 | Medium | subtree sums in post-order | uses one returned value (subtree sum) to evaluate every cut |
| 4 | [Independent Set](https://atcoder.jp/contests/dp/tasks/dp_p) | AtCoder DP P | – | counting version of take/skip | House Robber III with products instead of max, on a general tree |
| 5 | [Tree Matching](https://cses.fi/problemset/task/1130) | CSES 1130 | – | "is v already matched?" state | a different extra bit, same pair-returning shape |
| 6 | [Binary Tree Maximum Path Sum](https://leetcode.com/problems/binary-tree-maximum-path-sum/) | LeetCode 124 | Hard | global answer with negative values | adds clamping of arms to the 543 pattern |
| 7 | [Binary Tree Cameras](https://leetcode.com/problems/binary-tree-cameras/) | LeetCode 968 | Hard | three states per node | tests whether you can design the state set yourself |
| 8 | [Longest Path With Different Adjacent Characters](https://leetcode.com/problems/longest-path-with-different-adjacent-characters/) | LeetCode 2246 | Hard | top-two arms on a general tree | 124's idea when nodes have many children |
| 9 | [Sum of Distances in Tree](https://leetcode.com/problems/sum-of-distances-in-tree/) | LeetCode 834 | Hard | rerooting | the standard first rerooting problem |
| 10 | [Tree Distances I](https://cses.fi/problemset/task/1132) | CSES 1132 | – | rerooting with max (needs top-two) | rerooting when the change is a max instead of a sum |
| 11 | [Subtree](https://atcoder.jp/contests/dp/tasks/dp_v) | AtCoder DP V | – | rerooting with prefix/suffix products | the hardest rerooting variant in the AtCoder DP set |

## 10. Navigation

⬅ Previous: [Palindrome Partitioning](../06-palindrome-partitioning/) · ➡ Next: [DP on Graphs](../08-dp-on-graphs/) · 📖 [Walkthrough](walkthrough.md) · 💻 [Solutions](solution/)
