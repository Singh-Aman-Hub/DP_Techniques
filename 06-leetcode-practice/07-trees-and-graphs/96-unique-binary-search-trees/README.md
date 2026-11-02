# 96. Unique Binary Search Trees

> **Pattern:** Catalan / "choose a root, split into two independent halves" · **Difficulty:** Medium
> **Builds on:** [Interval DP](../../../04-advanced-dp-patterns/03-interval-dp/) (pick a split point) and [DP on Trees](../../../04-advanced-dp-patterns/07-dp-on-trees/) (a tree = root + left subtree + right subtree)

---

## Problem overview

You are given a number `n`. Count how many structurally different binary search trees (BSTs) can be built that store exactly the values `1, 2, …, n`, each value once. Only the *shape* matters, because once the shape is fixed the BST rule forces which value sits in which node.

**Why this is a tree DP problem.** Every BST is "a root, plus a BST on the left, plus a BST on the right". The two sides never interact, so the number of trees is a product of two smaller answers. That "root + independent subtrees" split is the heart of [DP on Trees](../../../04-advanced-dp-patterns/07-dp-on-trees/). Choosing *which* value becomes the root is the "choose a split point" step from [Interval DP](../../../04-advanced-dp-patterns/03-interval-dp/).

## Link & metadata

| Field | Value |
|---|---|
| LeetCode | [96. Unique Binary Search Trees](https://leetcode.com/problems/unique-binary-search-trees/) |
| Difficulty | Medium |
| Pattern | Counting DP over a split point (Catalan numbers) |
| Prerequisite topics | [State representation](../../../00-foundations/09-state-representation/), [Interval DP](../../../04-advanced-dp-patterns/03-interval-dp/), [DP on Trees](../../../04-advanced-dp-patterns/07-dp-on-trees/) |

## Examples

| `n` | Answer | Why |
|---|---|---|
| 1 | 1 | a single node |
| 2 | 2 | root 1 with 2 on its right, or root 2 with 1 on its left |
| 3 | 5 | drawn below |
| 0 | 1 | the empty tree is one (empty) arrangement — needed as a base case |

The five trees for `n = 3`:

```
 1         1           2          3       3
  \         \         / \        /       /
   2         3       1   3      2       1
    \       /                  /         \
     3     2                  1           2
```

## Brute-force intuition

Try every value `r` as the root. Values smaller than `r` *must* go to the left (BST rule) and larger values *must* go to the right. Recursively count the left shapes and the right shapes, and multiply: every left shape can be paired with every right shape.

```
count(1..3)
├── root 1: count(empty) * count(2..3)
├── root 2: count(1..1)  * count(3..3)
└── root 3: count(1..2)  * count(empty)
```

Without caching, `count(2..3)` and `count(1..2)` are recomputed many times, and the call tree grows exponentially (roughly like the answer itself, which is about `4^n`).

## Deriving the DP (7 questions)

### 1. What is changing?
The set of values we still have to arrange. The key observation: **the number of shapes depends only on how many values there are, not on which values.** `{2,3}` and `{1,2}` and `{7,8}` all give 2 shapes. So the only changing quantity is a count `k`.

### 2. What does the state represent?
`G(k)` = the number of structurally different BSTs that can be built from `k` consecutive distinct values.

### 3. What are the choices?
Which of the `k` values becomes the root. If the root is the `r`-th smallest value (`1 ≤ r ≤ k`), then `r − 1` values go left and `k − r` values go right.

### 4. What is the recurrence?
```
G(k) = Σ_{r=1..k}  G(r − 1) · G(k − r)
```
- `G(r − 1)` = ways to shape the left subtree,
- `G(k − r)` = ways to shape the right subtree,
- multiply because the choices are independent (rule of product),
- add over `r` because different roots give different trees (rule of sum).

### 5. What are the base cases?
`G(0) = 1` — there is exactly one empty tree. If this were 0, every tree with an empty side would be multiplied away. `G(1) = 1` follows from the recurrence but is handy to state.

### 6. What is the order of computation?
`G(k)` uses only `G(0) … G(k − 1)`, so compute `k = 0, 1, 2, …, n` in increasing order.

### 7. Can the state space be optimized?
Every earlier value is used, so the table itself cannot shrink below `O(n)`. But these are the **Catalan numbers**, which have the closed form `C(k+1) = C(k) · 2(2k+1) / (k+2)`. That gives O(n) time and O(1) space (see `Optimized.java`).

## Recursive solution

```java
static long count(int k) {
    if (k <= 1) return 1;
    long total = 0;
    for (int root = 1; root <= k; root++)
        total += count(root - 1) * count(k - root);
    return total;
}
```

## Memoization

Store `G(k)` the first time it is computed (`memo[k] = 0` means "not computed", which is safe because every real answer is ≥ 1). See [`solution/Memoization.java`](solution/Memoization.java).

## Tabulation

```java
long[] ways = new long[n + 1];   // ways[k] = number of BST shapes with k nodes
ways[0] = 1;
for (int k = 1; k <= n; k++)
    for (int root = 1; root <= k; root++)
        ways[k] += ways[root - 1] * ways[k - root];
```
See [`solution/Tabulation.java`](solution/Tabulation.java).

## Space optimization

The table is genuinely needed (each entry reads all previous entries). The improvement here is mathematical: use the Catalan ratio and multiply step by step, dividing exactly at each step:

```
C(0) = 1,  C(k+1) = C(k) · 2(2k+1) / (k+2)
```
The division is always exact because the intermediate result `C(k)·2(2k+1)` is a multiple of `(k+2)`. See [`solution/Optimized.java`](solution/Optimized.java).

## Dry run

Tabulation for `n = 4`:

| k | terms `G(r−1)·G(k−r)` for r = 1..k | `G(k)` |
|---|---|---|
| 0 | base case | 1 |
| 1 | 1·1 | 1 |
| 2 | 1·1 + 1·1 | 2 |
| 3 | 1·2 + 1·1 + 2·1 | 5 |
| 4 | 1·5 + 1·2 + 2·1 + 5·1 | 14 |

Closed form check: `C(1)=1·2·1/2=1`, `C(2)=1·2·3/3=2`, `C(3)=2·2·5/4=5`, `C(4)=5·2·7/5=14`. ✔

## Complexity

| Approach | Time | Space | Why |
|---|---|---|---|
| Plain recursion | exponential | O(n) stack | the same `G(k)` is recomputed in many branches |
| Memoization | O(n²) | O(n) | `n` states, each loops over up to `n` roots |
| Tabulation | O(n²) | O(n) | same work, no recursion |
| Catalan formula | O(n) | O(1) | one multiply/divide per step |

## Why the optimized approach is better

The DP is already fast for `n ≤ 19` (the LeetCode limit). The formula is shown so you recognise that a counting DP of shape `Σ G(i)·G(k−1−i)` is the Catalan sequence — which also counts balanced parentheses, polygon triangulations and more.

## Common mistakes

- Setting `G(0) = 0`. The empty subtree must count as one way.
- Using the actual value range `(lo, hi)` as the state. It works, but it is an `O(n²)` state space for no reason — only the size matters.
- Overflow: `G(19) = 1,767,263,190` exceeds `int`. Use `long`.
- In the closed form, dividing before multiplying loses precision; multiply first, then divide.

## Key takeaway

When a structure is "root + independent left part + independent right part", **count = Σ over split (left count × right count)**. If only the size of each part matters, the state is one integer.

## Related problems

- [DP on Trees](../../../04-advanced-dp-patterns/07-dp-on-trees/) — root-plus-subtrees reasoning, House Robber III, diameter, path sum.
- [Interval DP](../../../04-advanced-dp-patterns/03-interval-dp/) and [Matrix Chain Multiplication](../../../04-advanced-dp-patterns/04-matrix-chain-multiplication/) — same "choose a split point" loop, but minimising instead of counting.
- [1339. Maximum Product of Splitted Binary Tree](../1339-maximum-product-of-splitted-binary-tree/) — next problem in this folder.
