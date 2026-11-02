# DP on Trees — Walkthrough

This lesson has one big idea: **on a tree, the children of a node are its subproblems, so a post-order DFS (children first, then the node) is tabulation in the right order.**

We learn it through one main problem, [House Robber III](https://leetcode.com/problems/house-robber-iii/), and follow it through every stage (brute force, memoization, tabulation, optimized). After that, two short add-ons show two more skills you will need on almost every tree problem:

- **Global answer vs. returned value** — [Diameter of Binary Tree](https://leetcode.com/problems/diameter-of-binary-tree/) and [Binary Tree Maximum Path Sum](https://leetcode.com/problems/binary-tree-maximum-path-sum/).
- **Rerooting** — computing an answer for *every* node as the root in O(n), with [Sum of Distances in Tree](https://leetcode.com/problems/sum-of-distances-in-tree/).

---

## 1. Intuition

Picture a company org chart. The CEO wants to know "how many people work under me?". She does not count everyone herself. She asks each direct report, "how many people are under *you*?", and each of them asks their own reports, all the way down to the interns, who answer "just me: 1". The answers flow **upward**: a manager can answer only after all of their reports have answered.

That is all "DP on trees" is:

- every node's answer is built from the answers of its children;
- the leaves are the base cases;
- the computation order is "children before parent", which is exactly a **post-order** traversal.

There is one twist. Sometimes a single number per node is not enough for the parent to decide. In House Robber III, a parent needs to know two things about each child: "what is your best if you *are* robbed?" and "what is your best if you are *not* robbed?". So each child reports a **pair** of numbers upward. Learning to return several values from a child is the heart of this topic.

---

## 2. Problem statement in our own words

Original: [LeetCode 337 — House Robber III](https://leetcode.com/problems/house-robber-iii/)

Houses are arranged as a binary tree, and each house holds some money (a non-negative number). A thief may rob any set of houses, as long as **no two robbed houses are directly connected** (a parent and its child cannot both be robbed). Return the largest total the thief can collect.

This is [House Robber](../../01-one-dimensional-dp/06-house-robber/) moved from a line onto a tree. In the line version, "adjacent" meant `i` and `i+1`. Here, "adjacent" means parent and child.

### A tiny tree vocabulary

| Term | Meaning |
|---|---|
| node | one house; it has a value and up to two children |
| root | the top node; it has no parent |
| leaf | a node with no children |
| subtree of v | v together with everything below it |
| height | number of edges on the longest path from a node down to a leaf |
| post-order | visit left subtree, then right subtree, then the node itself |

---

## 3. Examples

**Example A** (our running example, used in every section):

```
          3
        /   \
       4     5
      / \     \
     1   3     1
```
Level-order (LeetCode format): `[3,4,5,1,3,null,1]` → answer **9**.
Rob `4` and `5` (4 + 5 = 9). They are siblings, not parent/child, so that is allowed. Robbing the root `3` forces us to skip `4` and `5` and take only grandchildren: 3 + 1 + 3 + 1 = 8, which is worse.

**Example B:** `[3,2,3,null,3,null,1]` → **7** (rob the root 3 and the two grandchildren 3 and 1).

**Example C (edge):** an empty tree → **0**. A single house `[5]` → **5**.

**Example D:** a left-leaning path `4 → 1 → 2 → 3` → **7** (rob 4 and 3). On a path, this is exactly the original House Robber.

---

## 4. Deriving the solution — the 7 questions

### Q1. What is changing?
The **node we are standing at**. Each decision moves us down to a smaller subtree. Nothing else changes: there is no capacity, no index, no budget.

### Q2. What does the state represent?
First attempt, one value per node:

> `rob(v)` = the most money we can take from the subtree rooted at `v`.

This works (it is what the brute force uses), but to compute it we must look at **grandchildren**, which is clumsy. A cleaner state carries one extra bit — *is v itself robbed?*:

> `take[v]` = the most money from v's subtree **given that v is robbed**.
> `skip[v]` = the most money from v's subtree **given that v is not robbed**.

Two numbers per node. This is the "multiple values from a child" idea.

### Q3. What are the choices?
At node `v` there are exactly two choices: **rob v** or **don't rob v**.

### Q4. What is the recurrence?
Let `L` and `R` be the children of `v` (a missing child contributes `take = skip = 0`).

```
take[v] = v.val + skip[L] + skip[R]
skip[v] = max(take[L], skip[L]) + max(take[R], skip[R])
answer  = max(take[root], skip[root])
```

- If we rob `v`, its children are neighbours, so they must be in their `skip` state.
- If we don't rob `v`, each child is free: it picks whichever of its own two states is better. The left and right subtrees do not touch each other, so their best values simply add up.

### Q5. What are the base cases?
An empty subtree (`null`) has `take = skip = 0`: there is nothing to steal. A leaf then automatically gets `take = leaf.val`, `skip = 0`.

### Q6. What is the order of computation?
`take[v]` and `skip[v]` need the values of `v`'s children. So **every child must be finished before its parent**. Two orders guarantee that:

- recursive **post-order DFS** (left, right, then node);
- iterative: list the nodes in BFS (level) order, which puts parents before children, then process the list **backwards**.

This is the same rule you met in [transition dependencies and computation order](../../00-foundations/11-transition-dependencies-and-computation-order/): compute a state only after everything it depends on.

### Q7. Can the state space be optimized?
Each pair is used exactly once, by the node's parent, immediately after it is computed. So we do not need a table at all: each recursive call can simply **return** its pair. Memory drops from O(n) table entries to O(h) stack frames, where `h` is the tree's height.

---

## 5. Brute force: recursion

With the one-value state `rob(v)`, the two choices are:

- rob `v`: get `v.val`, skip the children, continue at the **four grandchildren**;
- skip `v`: continue at the **two children**.

```java
static int rob(TreeNode node) {
    if (node == null) return 0;
    int robThis = node.val;
    if (node.left != null)  robThis += rob(node.left.left)  + rob(node.left.right);
    if (node.right != null) robThis += rob(node.right.left) + rob(node.right.right);
    int skipThis = rob(node.left) + rob(node.right);
    return Math.max(robThis, skipThis);
}
```

Recursion tree on Example A (nodes written by value; `4`, `5` are the root's children, `1a`, `3b` are 4's children, `1c` is 5's right child):

```
rob(3)
├── robThis: rob(1a)  rob(3b)  rob(null)  rob(1c)      <- grandchildren
└── skipThis:
    ├── rob(4)
    │   ├── robThis: (4 has no grandchildren)
    │   └── skipThis: rob(1a)  rob(3b)                   <- 1a, 3b solved AGAIN
    └── rob(5)
        ├── robThis: rob(null)...
        └── skipThis: rob(null)  rob(1c)                 <- 1c solved AGAIN
```

Every node is solved once on behalf of its parent and once on behalf of its grandparent. On a long path of length `n`, the number of calls satisfies `T(n) = T(n-1) + T(n-2) + O(1)` — the Fibonacci recurrence — so it grows exponentially (about 1.618^n). A path of 50 houses already needs billions of calls.

**Why it is slow:** overlapping subproblems. The same subtree is asked the same question many times. That is the signal for memoization (see [overlapping subproblems](../../00-foundations/04-overlapping-subproblems/)).

---

## 6. Memoization (top-down)

**What is cached?** The answer `rob(v)` for each node `v`. A subtree is fully identified by its root node, so the node object itself is the cache key. Arrays need integer indices, and tree nodes have none, so we use a `HashMap<TreeNode, Integer>`.

```java
private static int rob(TreeNode node) {
    if (node == null) return 0;
    Integer cached = memo.get(node);
    if (cached != null) return cached;          // cache hit
    ... same two choices as before ...
    memo.put(node, best);
    return best;
}
```

Dry run on Example A, in the order calls finish:

| Step | Call | Result | Note |
|---|---|---|---|
| 1 | rob(1a) | 1 | leaf: max(1, 0) |
| 2 | rob(3b) | 3 | leaf |
| 3 | rob(1c) | 1 | leaf |
| 4 | rob(4) | 4 | robThis = 4; skipThis = rob(1a)+rob(3b) = **cache hit** 1 + **cache hit** 3 = 4 |
| 5 | rob(5) | 5 | robThis = 5; skipThis = rob(1c) = **cache hit** 1 |
| 6 | rob(3) | 9 | robThis = 3+1+3+1 = 8; skipThis = rob(4)+rob(5) = 4+5 = 9 |

Each node is computed once; every repeat is a map lookup. Total work: O(n).

---

## 7. Tabulation (bottom-up)

On an array, "bottom-up" means a `for` loop from small indices to large. On a tree, "small" means "deep": leaves first, root last. We need a list of nodes where **every child comes before its parent**.

The easy way: a BFS lists parents before children, so the BFS list **reversed** is a valid order.

```
BFS order:     3, 4, 5, 1a, 3b, 1c
reversed:      1c, 3b, 1a, 5, 4, 3      <- children always before parents
```

We now switch to the two-value state, which needs only children (no grandchildren):

```java
for (int i = order.size() - 1; i >= 0; i--) {
    TreeNode node = order.get(i);
    ... read leftTake, leftSkip, rightTake, rightSkip (0 for a missing child) ...
    take.put(node, node.val + leftSkip + rightSkip);
    skip.put(node, Math.max(leftTake, leftSkip) + Math.max(rightTake, rightSkip));
}
return Math.max(take.get(root), skip.get(root));
```

The "table" is two maps. Filling it step by step on Example A:

| Step | Node | take = val + skip(L) + skip(R) | skip = best(L) + best(R) | best(node) |
|---|---|---|---|---|
| 1 | 1c | 1 + 0 + 0 = **1** | 0 + 0 = **0** | 1 |
| 2 | 3b | 3 + 0 + 0 = **3** | **0** | 3 |
| 3 | 1a | 1 + 0 + 0 = **1** | **0** | 1 |
| 4 | 5 | 5 + 0 (no left) + skip(1c)=0 = **5** | 0 + best(1c)=1 = **1** | 5 |
| 5 | 4 | 4 + skip(1a)=0 + skip(3b)=0 = **4** | best(1a)=1 + best(3b)=3 = **4** | 4 |
| 6 | 3 (root) | 3 + skip(4)=4 + skip(5)=1 = **8** | best(4)=4 + best(5)=5 = **9** | **9** |

Answer: max(8, 9) = **9**.

Picture of the finished table drawn on the tree as `(take, skip)`:

```
               3 (8, 9)
             /          \
        4 (4, 4)       5 (5, 1)
        /     \              \
   1 (1, 0)  3 (3, 0)      1 (1, 0)
```

Notice node `4`: `take = skip = 4`. Robbing it alone gives 4; skipping it and robbing both children also gives 4. The root does not care which one; it just reads the numbers.

---

## 8. Space optimization

Who reads `take[v]` and `skip[v]`? Only `v`'s parent, and only once. So instead of storing them in maps, the recursive function **returns them**:

```java
/** Returns {take, skip} for the subtree rooted at node. */
private static int[] solve(TreeNode node) {
    if (node == null) return new int[]{0, 0};
    int[] left = solve(node.left);           // children first ...
    int[] right = solve(node.right);
    int take = node.val + left[1] + right[1];  // ... then this node
    int skip = Math.max(left[0], left[1]) + Math.max(right[0], right[1]);
    return new int[]{take, skip};
}
```

This is the version you should write in an interview. It is the same DP as the table in Section 7. Post-order recursion simply *is* the "children before parent" order, and the call stack holds the values while they are needed.

Why is this not just memoization? Memoization caches a value because it might be asked for again. Here nobody asks twice: the two-value state removed the grandchild lookups, so every subproblem has exactly one consumer. **No cache is needed when every state is used once.**

---

## 9. Dry run on a full example

Example B `[3,2,3,null,3,null,1]`:

```
        3
       / \
      2   3
       \   \
        3   1
```

Post-order visits: `3` (right child of 2), `2`, `1`, `3` (right child of root), root `3`.

| Visit | Node | left pair | right pair | take | skip |
|---|---|---|---|---|---|
| 1 | 3 (under 2) | (0,0) | (0,0) | 3 | 0 |
| 2 | 2 | (0,0) | (3,0) | 2 + 0 + 0 = 2 | 0 + max(3,0) = 3 |
| 3 | 1 (under right 3) | (0,0) | (0,0) | 1 | 0 |
| 4 | 3 (right child) | (0,0) | (1,0) | 3 + 0 + 0 = 3 | 0 + 1 = 1 |
| 5 | 3 (root) | (2,3) | (3,1) | 3 + 3 + 1 = **7** | max(2,3) + max(3,1) = **6** |

Answer: max(7, 6) = **7**. The root is robbed together with both grandchildren.

---

## 10. Complexity analysis

| Approach | Time | Space | Why |
|---|---|---|---|
| Brute force | exponential (≈1.618^h on a path) | O(h) | each node is re-solved by its parent and its grandparent; on a path that is the Fibonacci recurrence |
| Memoization | O(n) | O(n) | n states, each computed once with O(1) work; the map holds n entries; stack depth h |
| Tabulation | O(n) | O(n) | one BFS + one backward pass; two maps with n entries each |
| Pair-returning DFS | O(n) | O(h) | each node visited once; only the call stack, which is as deep as the tree |

`h` is the height: about log₂ n for a balanced tree, but up to `n` for a path. For very deep trees in Java, the recursion can overflow the stack; then the iterative tabulation version is the safe choice.

---

## 11. Why this approach works

- **Independence of subtrees.** Once we fix whether `v` is robbed, the left subtree and the right subtree no longer influence each other: no edge connects them except through `v`. So the best total is the best of the left side plus the best of the right side. This is [optimal substructure](../../00-foundations/05-optimal-substructure/).
- **The extra bit carries exactly the information that crosses the boundary.** The only constraint between a subtree and the rest of the tree is the edge from `v` to its parent. The parent needs to know only one thing about that edge: is `v` robbed? Storing both answers (`take`, `skip`) means the parent can choose either way without re-exploring the subtree.
- **Every valid robbery plan is counted.** At each node we consider both possibilities, and every plan is "some choice at each node", so the maximum over the choices is the true maximum.

---

## 12. Pattern 2 — the global answer vs. the returned value

Many tree problems ask for the best **path**. A path can go up from one subtree, bend at some node, and go down into another subtree. The trick is to notice that a node must handle **two different quantities**:

1. **What it returns to its parent:** something the parent can *extend*. A path that the parent extends must be a straight line going down from this node, using **at most one** child.
2. **What it records as a candidate answer:** the best path whose **highest node** is this node. That path may use **both** children (it bends here). The parent cannot extend it (a path cannot fork), so it is written into a **global** variable instead of being returned.

```
   returned to parent         recorded in global answer
   (one arm only)             (bends here, both arms)

         p                            p
         |                            x   <- parent cannot use this
         v                            v
        /                            / \
       a                            a   b
      /                            /     \
     .                            .       .
```

Every path in the tree has exactly one highest node. So if every node records "the best path that bends at me", the maximum over all nodes is the answer.

### 12.1 Diameter of Binary Tree (LeetCode 543)

[LeetCode 543 — Diameter of Binary Tree](https://leetcode.com/problems/diameter-of-binary-tree/): find the number of edges on the longest path between any two nodes. The path does not have to pass through the root.

- Returned value: `height(v)` = edges on the longest downward path starting at `v`. With `height(null) = -1`, a leaf gets `1 + max(-1, -1) = 0`.
- Recorded value: `(height(L) + 1) + (height(R) + 1)` = the left arm plus the right arm, both bending at `v`.

```java
private static int height(TreeNode node) {
    if (node == null) return -1;
    int leftHeight = height(node.left);
    int rightHeight = height(node.right);
    bestDiameter = Math.max(bestDiameter, (leftHeight + 1) + (rightHeight + 1));  // global
    return 1 + Math.max(leftHeight, rightHeight);                                  // returned
}
```

Dry run on `[1,2,3,4,5]`:

```
        1
       / \
      2   3
     / \
    4   5
```

| Node | height(L) | height(R) | bend = (hL+1)+(hR+1) | best so far | returns |
|---|---|---|---|---|---|
| 4 | -1 | -1 | 0 | 0 | 0 |
| 5 | -1 | -1 | 0 | 0 | 0 |
| 2 | 0 | 0 | 2 | 2 | 1 |
| 3 | -1 | -1 | 0 | 2 | 0 |
| 1 | 1 | 0 | 2 + 1 = 3 | **3** | 2 |

Answer **3** (path 4–2–1–3). In the solution file, a test case has its diameter deep inside the left subtree, which shows why the global maximum over *all* nodes is needed, not just the value at the root.

### 12.2 Binary Tree Maximum Path Sum (LeetCode 124)

[LeetCode 124 — Binary Tree Maximum Path Sum](https://leetcode.com/problems/binary-tree-maximum-path-sum/): node values can be negative; find the largest sum of values along any non-empty path.

Two changes from the diameter:

- We add values, not count edges.
- A negative arm makes the sum worse, and we may simply not use it. So each arm is clamped: `max(0, gain(child))`.

```java
private static int gain(TreeNode node) {
    if (node == null) return 0;
    int leftArm = Math.max(0, gain(node.left));
    int rightArm = Math.max(0, gain(node.right));
    bestSum = Math.max(bestSum, node.val + leftArm + rightArm);   // global: bends here
    return node.val + Math.max(leftArm, rightArm);                // returned: one arm
}
```

Dry run on `[-10,9,20,null,null,15,7]`:

```
        -10
        /  \
       9    20
           /  \
          15   7
```

| Node | leftArm | rightArm | bend = val + L + R | bestSum | returns val + max(L,R) |
|---|---|---|---|---|---|
| 9 | 0 | 0 | 9 | 9 | 9 |
| 15 | 0 | 0 | 15 | 15 | 15 |
| 7 | 0 | 0 | 7 | 15 | 7 |
| 20 | 15 | 7 | 42 | **42** | 35 |
| -10 | 9 | 35 | 34 | 42 | 25 |

Answer **42** (15 → 20 → 7). The root's own bent path (34) is smaller, so the answer lives below the root.

Two details beginners miss:

- Start `bestSum` at `Integer.MIN_VALUE`, not 0. For the tree `[-3]` the answer is -3. The path must contain at least one node, even if every value is negative.
- The clamp `max(0, …)` applies to the **arms**, never to the node itself. The node is always part of the path that bends at it.

The solution file also contains an O(n²) `bruteForce` that recomputes every arm from scratch. It is used to cross-check the fast version on all test trees.

---

## 13. Pattern 3 — rerooting: the answer for every possible root

Some problems ask for one number **per node**, where each number treats that node as the root. Running a fresh DFS from every node costs O(n) each, so O(n²) total. **Rerooting** gets all n answers in O(n) with two passes:

1. **Pass 1 (down, post-order):** root the tree anywhere (node 0) and compute subtree information bottom-up, as in everything above.
2. **Pass 2 (up, pre-order):** move the root from a parent `p` to a child `c` and work out **how the answer changes** across that one edge, using what Pass 1 already knows.

### 13.1 The problem

[LeetCode 834 — Sum of Distances in Tree](https://leetcode.com/problems/sum-of-distances-in-tree/): an undirected tree has `n` nodes labelled `0..n-1`. For every node `v`, return the sum of the distances (number of edges) from `v` to all other nodes.

Running example (`n = 6`, edges `0-1, 0-2, 2-3, 2-4, 2-5`):

```
        0
       / \
      1   2
         /|\
        3 4 5
```

Expected answer: `[8, 12, 6, 10, 10, 10]`. Check node 0: distances to 1,2,3,4,5 are 1,1,2,2,2 → 8.

### 13.2 Step 1 — what Pass 1 computes

Root the tree at 0. For each node `v`:

- `size[v]` = the number of nodes in `v`'s subtree (including `v`).
- `downSum[v]` = the sum of distances from `v` to the nodes **in its own subtree**.

Recurrence (child `c` of `v`):

```
size[v]    = 1 + sum of size[c]
downSum[v] = sum over children c of ( downSum[c] + size[c] )
```

Why `+ size[c]`? Every node in `c`'s subtree is exactly **one edge farther** from `v` than from `c`. There are `size[c]` such nodes, so moving from `c` up to `v` adds `size[c]` to their total.

Post-order fill:

| Node | children | size | downSum |
|---|---|---|---|
| 1 | – | 1 | 0 |
| 3 | – | 1 | 0 |
| 4 | – | 1 | 0 |
| 5 | – | 1 | 0 |
| 2 | 3, 4, 5 | 1+1+1+1 = 4 | (0+1)+(0+1)+(0+1) = 3 |
| 0 | 1, 2 | 1+1+4 = 6 | (0+1) + (3+4) = 8 |

`downSum[0] = 8` covers the **whole** tree (0's subtree is everything), so `answer[0] = 8`. For any other node, `downSum` only counts its own subtree, so it is not yet the answer.

### 13.3 Step 2 — moving the root across one edge

Suppose we already know `answer[p]` and `c` is a child of `p`. What happens to each distance when the root moves from `p` to `c`?

```
     root at p                    root at c
   +-----------+                +-----------+
   |  the rest |  n - size[c]   |  the rest |   each one step FARTHER (+1)
   |  (incl. p)|  nodes         |           |
   +-----+-----+                +-----+-----+
         |                            |
   +-----+-----+                +-----+-----+
   | c-subtree |  size[c] nodes | c-subtree |   each one step CLOSER  (-1)
   +-----------+                +-----------+
```

- The `size[c]` nodes in `c`'s subtree each get 1 closer: total change `-size[c]`.
- The other `n - size[c]` nodes each get 1 farther: total change `+(n - size[c])`.

```
answer[c] = answer[p] - size[c] + (n - size[c])
```

The formula uses only `answer[p]` (already known, because we go top-down) and `size[c]` (from Pass 1). It is O(1) per edge.

### 13.4 Step 3 — Pass 2 dry run

Go top-down (parents before children):

| Node c | parent p | answer[p] | size[c] | answer[c] = answer[p] − size[c] + (6 − size[c]) |
|---|---|---|---|---|
| 0 | – | – | – | 8 (from Pass 1) |
| 1 | 0 | 8 | 1 | 8 − 1 + 5 = **12** |
| 2 | 0 | 8 | 4 | 8 − 4 + 2 = **6** |
| 3 | 2 | 6 | 1 | 6 − 1 + 5 = **10** |
| 4 | 2 | 6 | 1 | **10** |
| 5 | 2 | 6 | 1 | **10** |

Result `[8, 12, 6, 10, 10, 10]`. Spot-check node 3: distances to 2, 4, 5, 0, 1 are 1, 2, 2, 2, 3 → 10. ✓

### 13.5 The 7 questions, briefly, for rerooting

1. **Changing:** the node (and, in Pass 2, which node is the root).
2. **State:** `size[v]`, `downSum[v]` (subtree-only information) and `answer[v]` (whole-tree information with `v` as the root).
3. **Choices:** none; this is a counting/summing DP, not an optimization.
4. **Recurrence:** the two formulas above.
5. **Base cases:** a leaf has `size = 1`, `downSum = 0`; `answer[root] = downSum[root]`.
6. **Order:** Pass 1 children-before-parent; Pass 2 parent-before-children.
7. **Space:** O(n) arrays, needed because Pass 2 reads Pass 1's `size` values.

The solution file runs both passes **iteratively** (BFS order, then reversed BFS order), so a path of 30,000 nodes cannot overflow Java's call stack. It is cross-checked against an O(n²) "BFS from every node" brute force on a 200-node tree.

### 13.6 The general recipe for rerooting

1. Pick a root, compute "subtree" values in post-order.
2. Write the answer for the root.
3. Find the **O(1) or O(degree) change** when the root moves one edge from `p` to `c`, using `p`'s whole-tree answer and `c`'s subtree value.
4. Apply it in pre-order.

When the "change" is harder (for example, max instead of sum), you also need the best and second-best child values. That is a later skill; the two-pass shape stays the same.

---

## 14. Common mistakes

1. **Looking at grandchildren instead of returning more state.** It works, but it is slower to reason about. If you need to know something about a child (robbed or not), make the child return it.
2. **Returning the bent path to the parent.** In 543/124, the parent may extend only a one-armed path. Returning `left + right + val` builds "paths" that fork, and the answer comes out too large.
3. **Reading the answer only at the root.** The best path may bend at any node. Keep a global maximum updated at every node.
4. **Starting the maximum at 0 when values can be negative** (124). Use `Integer.MIN_VALUE`.
5. **Clamping the node's own value.** Only the arms are optional; the node at which the path bends is always included.
6. **Treating a general tree like a directed one.** In 834 the edges are undirected. Without a `parent` / `visited` check, the DFS walks back up and loops forever.
7. **Deep recursion on a path-shaped tree.** A 10⁵-node path overflows Java's default stack. Use the iterative BFS-order technique.
8. **Mixing up `size[c]` and `n - size[c]`** in the rerooting formula. Draw the two-box picture and ask: which side got closer?

---

## 15. When to use this technique / how to recognize similar problems

Signals:

- The input is a **tree** (binary tree, or `n` nodes with `n-1` edges and no cycles).
- The answer for a subtree can be built from the answers for the child subtrees.
- A constraint links a node to its parent (no two adjacent chosen, cover every edge, …): add a state bit like "is this node chosen?" and return a pair.
- The answer is a **path** that may bend: returned value = one arm, global value = two arms.
- The question wants a value **for every node as the root**: rerooting.

Same pattern on general (non-binary) trees:

- [AtCoder DP P — Independent Set](https://atcoder.jp/contests/dp/tasks/dp_p): count the ways to colour the nodes black/white with no two adjacent blacks. It is House Robber III with **counting** instead of max: `white[v] = ∏ (white[c] + black[c])`, `black[v] = ∏ white[c]`.
- [CSES 1130 — Tree Matching](https://cses.fi/problemset/task/1130): choose the most edges with no shared endpoint. State: "is `v` already matched to one of its children?"
- [AtCoder DP V — Subtree](https://atcoder.jp/contests/dp/tasks/dp_v): a counting problem that needs rerooting with prefix/suffix products, because division is not allowed under a non-prime modulus. Attempt it after you are comfortable with 834.

---

## 16. Summary and revision notes

- On a tree, **children are the subproblems** and **post-order DFS is the computation order**. Leaves (or `null`) are the base cases.
- When a parent needs to know something about a child's own choice, make the child **return several values** (a pair like `{take, skip}`).
- A tree DP that returns values from recursion is already bottom-up. It needs no memo table, because each state has a single consumer.
- For paths: **return** the best one-arm value; **record** the best two-arm value in a global answer.
- For "answer for every root": **reroot**. Pass 1 computes subtree values, Pass 2 pushes whole-tree answers from parent to child with an O(1) adjustment.

```
+----------------------------------------------------------------+
|  House Robber III                                              |
|    take[v] = v.val + skip[L] + skip[R]                         |
|    skip[v] = max(take[L], skip[L]) + max(take[R], skip[R])     |
|    answer  = max(take[root], skip[root]);  null -> (0, 0)      |
|                                                                |
|  Path problems:  return  val + max(armL, armR)                 |
|                  record  val + armL + armR   (global max)      |
|                                                                |
|  Rerooting (834):  answer[c] = answer[p] - size[c] + (n - size[c]) |
+----------------------------------------------------------------+
```

---

## 17. Exercises

**Exercise 1.** Compute `(take, skip)` for every node of `[2,1,3,null,4]` and give the House Robber III answer.

<details>
<summary>Answer</summary>

```
      2
     / \
    1   3
     \
      4
```
- 4: (4, 0) · 3: (3, 0)
- 1: take = 1 + 0 = 1; skip = max(4,0) = 4 → (1, 4)
- 2: take = 2 + skip(1)=4 + skip(3)=0 = 6; skip = max(1,4) + max(3,0) = 7 → (6, 7)

Answer **7** (rob 4 and 3).
</details>

**Exercise 2.** Why does LeetCode 543 use `height(null) = -1` instead of 0? What would you change if you used 0?

<details>
<summary>Answer</summary>

With -1, a leaf's height comes out as 0 edges, and the bent path is `(hL + 1) + (hR + 1)` edges. If you count **nodes** instead (`depth(null) = 0`, a leaf has depth 1), the bent path through `v` has `depthL + depthR` edges: the `+1`s disappear because the depth already counts the edge into each child. Both are correct; mixing the two conventions is the classic off-by-one bug.
</details>

**Exercise 3.** In LeetCode 124, a student writes `return node.val + leftArm + rightArm;`. Give a tree where this returns a wrong answer.

<details>
<summary>Answer</summary>

`[1,2,null,3,4]`: node 2 has children 3 and 4, and node 1 is 2's parent. The true best path is 3 → 2 → 4 = 9, or 4 → 2 → 1 = 7, so the answer is 9. The buggy return value at node 2 is 2 + 3 + 4 = 9, and then the root records 1 + 9 = 10, a "path" 3–2–4 *and* 1 that forks at 2. That is not a path, so 10 is wrong.
</details>

**Exercise 4.** Use the rerooting formula to compute LeetCode 834 for the path `0-1-2-3`. Pass 1 gives `downSum[0] = 6`, sizes (rooted at 0): `size[1]=3, size[2]=2, size[3]=1`.

<details>
<summary>Answer</summary>

- answer[0] = 6
- answer[1] = 6 − 3 + (4 − 3) = 4
- answer[2] = 4 − 2 + (4 − 2) = 4
- answer[3] = 4 − 1 + (4 − 1) = 6

Result `[6, 4, 4, 6]`. This is one of the test cases in `SumOfDistancesInTree.java`.
</details>
