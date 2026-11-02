# 1130. Minimum Cost Tree From Leaf Values

> **Pattern:** Interval DP, choosing a split point `k` inside `[i..j]` (the matrix-chain shape) · **Difficulty:** Medium · **Builds on:** [Interval DP](../../../04-advanced-dp-patterns/03-interval-dp/), [Matrix Chain Multiplication](../../../04-advanced-dp-patterns/04-matrix-chain-multiplication/)

## Problem overview

You get an array of positive integers. Build a binary tree where every internal node has exactly two children, and the leaves, read left to right (in-order), are exactly the array values. Each internal node gets the value **(largest leaf in its left subtree) × (largest leaf in its right subtree)**. Among all such trees, return the smallest possible **sum of internal node values**.

The leaves keep their order, so each internal node covers a **contiguous range** of the array, and its two children split that range in two. "Pick where to split a range" is the matrix-chain shape of interval DP.

## Link & metadata

| Field | Value |
|---|---|
| Problem | [1130. Minimum Cost Tree From Leaf Values](https://leetcode.com/problems/minimum-cost-tree-from-leaf-values/) |
| Difficulty | Medium |
| Pattern | Interval DP with a split point; a monotonic-stack greedy as the optimized version |
| Prerequisite topic | [04-advanced-dp-patterns/03-interval-dp](../../../04-advanced-dp-patterns/03-interval-dp/) |
| Also useful | [04-advanced-dp-patterns/04-matrix-chain-multiplication](../../../04-advanced-dp-patterns/04-matrix-chain-multiplication/) |

## Examples

| arr | Answer | Why |
|---|---|---|
| `[6,2,4]` | 32 | first join `2` and `4` (cost 8), then `6` with that subtree (6 × 4 = 24) |
| `[4,11]` | 44 | only one tree: 4 × 11 |
| `[3,1,5,2]` | 28 | 3·1 + 3·5 + 5·2 = 3 + 15 + 10 |
| `[1,2,3,4]` | 20 | 1·2 + 2·3 + 3·4 |

Running example: `arr = [6,2,4]`. The two possible trees:

```
     (24)                 (36)
    /    \               /    \
   6     (8)           (12)    4
         /  \          /  \
        2    4        6    2
  total = 24 + 8 = 32    total = 36 + 12 = 48
```

## Brute-force intuition

The **root** splits the leaves into a left part `arr[i..k]` and a right part `arr[k+1..j]`. Its value is `max(left part) × max(right part)`, and then each part is again a tree of the same kind. Try every split `k` recursively. The number of tree shapes is a Catalan number (exponential), and the same sub-ranges such as `[1..2]` get solved inside many different trees.

## Deriving the DP (7 questions)

### 1. What is changing?
The contiguous range of leaves `[i..j]` that a subtree must cover.

### 2. What does the state represent?
> `dp[i][j]` = the minimum sum of internal node values over all valid trees whose leaves are exactly `arr[i..j]`.

We also precompute `maxLeaf[i][j]` = the largest value in `arr[i..j]`.

### 3. What are the choices?
Where the root of this subtree splits the range: any `k` with `i ≤ k < j`, so the left subtree covers `[i..k]` and the right subtree covers `[k+1..j]`.

### 4. What is the recurrence?
```
dp[i][j] = min over k in [i, j-1] of
           dp[i][k] + dp[k+1][j] + maxLeaf[i][k] * maxLeaf[k+1][j]
```
- `dp[i][k]`: best cost of the left subtree.
- `dp[k+1][j]`: best cost of the right subtree.
- `maxLeaf[i][k] * maxLeaf[k+1][j]`: the root's own value.

The two subtrees are independent: what happens inside one does not change the cost of the other. That independence is what makes interval DP valid.

### 5. What are the base cases?
`dp[i][i] = 0`: a single leaf has no internal nodes.

### 6. What is the order of computation?
`dp[i][j]` uses strictly shorter ranges. Fill by **increasing length** (2, 3, …, n), or loop `i` from `n-1` down to 0 with `j` from `i+1` up. The answer is `dp[0][n-1]`.

### 7. Can the state space be optimized?
The DP needs the full table, because a range uses splits at every `k`. But this problem has extra structure that allows a **greedy**: the smallest leaf `a` should be paired first with its **smaller neighbour** `b`, costing `a × b`. After that, `a` can never matter again, because any larger subtree containing it also contains a bigger leaf. A **decreasing monotonic stack** finds every "smallest leaf and its smaller neighbour" pairing in one left-to-right pass, giving O(n) time. See [`solution/Optimized.java`](solution/Optimized.java).

## Recursive solution

```java
static int minCost(int[] arr, int i, int j) {
    if (i == j) return 0;                                   // single leaf
    int best = Integer.MAX_VALUE;
    for (int k = i; k < j; k++)
        best = Math.min(best, minCost(arr, i, k) + minCost(arr, k + 1, j)
                              + max(arr, i, k) * max(arr, k + 1, j));
    return best;
}
```

## Memoization

Cache `dp[i][j]` (`-1` = not computed) and precompute `maxLeaf` so each split costs O(1). See [`solution/Memoization.java`](solution/Memoization.java).

## Tabulation

```java
for (int length = 2; length <= n; length++)
    for (int i = 0; i + length - 1 < n; i++) {
        int j = i + length - 1;
        dp[i][j] = Integer.MAX_VALUE;
        for (int k = i; k < j; k++)
            dp[i][j] = Math.min(dp[i][j], dp[i][k] + dp[k + 1][j] + maxLeaf[i][k] * maxLeaf[k + 1][j]);
    }
```
See [`solution/Tabulation.java`](solution/Tabulation.java).

## Space optimization

There is no rolling-array trick for the DP, since every range reads ranges of all shorter lengths. The real improvement is the **monotonic stack** (O(n) time, O(n) space):

```java
stack.push(Integer.MAX_VALUE);                    // sentinel: "no neighbour on this side"
for (int value : arr) {
    while (stack.peek() <= value) {               // top is a local minimum: smaller than both sides
        int smallest = stack.pop();
        cost += smallest * Math.min(stack.peek(), value);   // pair it with its smaller neighbour
    }
    stack.push(value);
}
while (stack.size() > 2) cost += stack.pop() * stack.peek();  // leftovers: decreasing, pair from the right
```
Why it is correct: a leaf that is smaller than both its neighbours must be joined with one of them at some point, and joining it with the **smaller** neighbour is never worse. Removing it does not change any later product, because a larger leaf now represents that part of the tree.

## Dry run

DP on `arr = [6,2,4]`. `maxLeaf`: `[0..0]=6`, `[1..1]=2`, `[2..2]=4`, `[0..1]=6`, `[1..2]=4`.

| range | k | left cost | right cost | root = maxL × maxR | total | dp |
|---|---|---|---|---|---|---|
| [0..1] (6,2) | 0 | 0 | 0 | 6 × 2 = 12 | 12 | 12 |
| [1..2] (2,4) | 1 | 0 | 0 | 2 × 4 = 8 | 8 | 8 |
| [0..2] | 0 | dp[0][0]=0 | dp[1][2]=8 | 6 × 4 = 24 | 32 | |
| [0..2] | 1 | dp[0][1]=12 | dp[2][2]=0 | 6 × 4 = 24 | 36 | **32** |

```
        j=0   j=1   j=2
 i=0     0    12   [32]
 i=1           0     8
 i=2                 0
```

Stack on the same input (`∞` is the sentinel):

| read | stack before | action | cost |
|---|---|---|---|
| 6 | [∞] | push | 0 |
| 2 | [∞,6] | 6 > 2, push | 0 |
| 4 | [∞,6,2] | pop 2: 2 × min(6, 4) = 8; then push 4 | 8 |
| end | [∞,6,4] | pop 4: 4 × 6 = 24 | **32** |

## Complexity

| Approach | Time | Space | Why |
|---|---|---|---|
| Recursion | exponential (Catalan) | O(n) | every tree shape is explored |
| Memoization | O(n³) | O(n²) | n²/2 ranges × up to n splits; tables for dp and maxLeaf |
| Tabulation | O(n³) | O(n²) | three nested loops |
| Monotonic stack | O(n) | O(n) | each value is pushed and popped once |

With n ≤ 40, O(n³) = 64,000 steps, so the DP is perfectly fine. The stack is a bonus insight.

## Why the optimized approach is better

The DP tries every split of every range. The greedy uses a property specific to this cost function (only maxima matter) to decide each pairing locally, so no range ever needs to be revisited. When you cannot prove such a greedy property, the interval DP is the safe answer.

## Common mistakes

1. **Summing leaf values.** Only internal nodes are counted, and each is a product of two maxima.
2. **Recomputing `max(arr[i..k])` inside the triple loop.** That gives O(n⁴). Precompute `maxLeaf`.
3. **Assuming the tree can reorder leaves.** It cannot; the in-order sequence is fixed, which is exactly why ranges are contiguous.
4. **In the stack version, pairing with the larger neighbour**, or forgetting the leftover loop at the end.

## Key takeaway

> "Combine adjacent pieces of a sequence into one, cost depends on the two halves" = split-point interval DP: `dp[i][j] = min_k dp[i][k] + dp[k+1][j] + cost(i, k, j)`. Check afterwards whether the cost function allows a greedy shortcut.

## Related problems

- [1039. Minimum Score Triangulation of Polygon](../1039-minimum-score-triangulation-of-polygon/): the same split-point shape on a polygon.
- [1547. Minimum Cost to Cut a Stick](../1547-minimum-cost-to-cut-a-stick/): split-point DP where the cost is the range length.
- [1043. Partition Array for Maximum Sum](../1043-partition-array-for-maximum-sum/): range maxima again, but in a 1D partition DP.
- Course topic: [Matrix Chain Multiplication](../../../04-advanced-dp-patterns/04-matrix-chain-multiplication/)
