# 1039. Minimum Score Triangulation of Polygon

> **Pattern:** Interval DP with a split point (the matrix-chain shape) · **Difficulty:** Medium · **Builds on:** [Interval DP](../../../04-advanced-dp-patterns/03-interval-dp/), [Matrix Chain Multiplication](../../../04-advanced-dp-patterns/04-matrix-chain-multiplication/)

## Problem overview

A convex polygon has `n` vertices, each labelled with a positive integer, listed in clockwise order. Cut the polygon into `n − 2` triangles using non-crossing diagonals. A triangle scores the **product** of its three vertex labels. Return the smallest possible total score.

## Link & metadata

| Field | Value |
|---|---|
| Problem | [1039. Minimum Score Triangulation of Polygon](https://leetcode.com/problems/minimum-score-triangulation-of-polygon/) |
| Difficulty | Medium |
| Pattern | Interval DP: `dp[i][j] = min over split k` |
| Prerequisite topic | [04-advanced-dp-patterns/04-matrix-chain-multiplication](../../../04-advanced-dp-patterns/04-matrix-chain-multiplication/) |
| Also useful | [04-advanced-dp-patterns/03-interval-dp](../../../04-advanced-dp-patterns/03-interval-dp/) |

## Examples

| values | Answer | Why |
|---|---|---|
| `[1,2,3]` | 6 | already a triangle: 1·2·3 |
| `[3,7,4,5]` | 144 | diagonal 0–2 gives 3·7·4 + 3·4·5 = 84 + 60 = 144; diagonal 1–3 gives 245 |
| `[1,3,1,4,1,5]` | 13 | |
| `[2,2,2,2]` | 16 | two triangles of 8 |

Running example: `values = [3,7,4,5]`.

## Brute-force intuition

The number of triangulations grows like the Catalan numbers (exponential), so listing them all is hopeless.

**The key observation.** Look at the edge between the first and last vertex, `0` and `n−1`. In **every** triangulation this edge belongs to exactly one triangle, and that triangle's third vertex is some `k` between them. Choosing `k` splits the polygon into two smaller polygons, `[0..k]` and `[k..n−1]`, which are triangulated **independently**:

```
       k                    choose triangle (i, k, j);
      / \                   left part:  polygon i..k
     /   \                  right part: polygon k..j
    i-----j
```

"Pick a split point inside a range, solve the two sides, add a cost that depends on `i`, `k`, `j`" is exactly the [Matrix Chain Multiplication](../../../04-advanced-dp-patterns/04-matrix-chain-multiplication/) shape.

## Deriving the DP (7 questions)

### 1. What is changing?
The sub-polygon formed by the consecutive vertices `i, i+1, …, j` (closed by the edge `i–j`).

### 2. What does the state represent?
> `dp[i][j]` = the minimum total score to triangulate the polygon made of vertices `i..j`.

### 3. What are the choices?
The third vertex `k` (with `i < k < j`) of the triangle that sits on the edge `i–j`.

### 4. What is the recurrence?
```
dp[i][j] = min over k in (i, j) of  dp[i][k] + dp[k][j] + values[i]*values[k]*values[j]
```
`dp[i][k]` and `dp[k][j]` are the two independent sides. The product is the triangle `(i, k, j)` itself.

### 5. What are the base cases?
`dp[i][i+1] = 0`: two vertices form only an edge, with no area and no triangle. (`j − i = 2` is a single triangle, and the recurrence produces it with `k = i+1`.)

### 6. What is the order of computation?
`dp[i][j]` needs `dp[i][k]` and `dp[k][j]`, both **shorter** ranges. Fill by increasing gap `j − i` (2, 3, …, n−1), or loop `i` from `n−1` down to 0 and `j` from `i+2` up to `n−1`.

### 7. Can the state space be optimized?
No. A cell reads cells from **many** rows (every `k`) and many columns, not just a neighbouring row. All O(n²) states must be kept, so there is no `Optimized.java`. (n ≤ 50, so this is tiny anyway.)

## Recursive solution

```java
static int minScore(int[] v, int i, int j) {
    if (j - i < 2) return 0;                         // just an edge
    int best = Integer.MAX_VALUE;
    for (int k = i + 1; k < j; k++)
        best = Math.min(best, minScore(v, i, k) + minScore(v, k, j) + v[i] * v[k] * v[j]);
    return best;
}
```

## Memoization

Cache `(i, j)` in `memo[n][n]`, where 0 means unknown (every real triangulation of 3+ vertices scores at least 1, and edges return 0 before the lookup). See [`solution/Memoization.java`](solution/Memoization.java).

## Tabulation

```java
for (int gap = 2; gap < n; gap++)
    for (int i = 0; i + gap < n; i++) {
        int j = i + gap;
        dp[i][j] = Integer.MAX_VALUE;
        for (int k = i + 1; k < j; k++)
            dp[i][j] = Math.min(dp[i][j], dp[i][k] + dp[k][j] + v[i] * v[k] * v[j]);
    }
return dp[0][n - 1];
```
See [`solution/Tabulation.java`](solution/Tabulation.java).

## Space optimization

Not applicable (see question 7).

## Dry run

`values = [3,7,4,5]` (vertices 0..3):

| gap | (i, j) | k options | dp |
|---|---|---|---|
| 2 | (0,2) | k=1: 0 + 0 + 3·7·4 = 84 | 84 |
| 2 | (1,3) | k=2: 0 + 0 + 7·4·5 = 140 | 140 |
| 3 | (0,3) | k=1: dp[0][1] + dp[1][3] + 3·7·5 = 0 + 140 + 105 = 245 | |
| | | k=2: dp[0][2] + dp[2][3] + 3·4·5 = 84 + 0 + 60 = **144** | **144** |

```
          j:  0    1    2    3
 i=0          -    0   84  [144]
 i=1               -    0   140
 i=2                    -     0
```
The optimal triangle on edge 0–3 uses `k = 2`, which corresponds to the diagonal 0–2.

## Complexity

| Approach | Time | Space | Why |
|---|---|---|---|
| Recursion | exponential (Catalan) | O(n) | every triangulation explored |
| Memoization | O(n³) | O(n²) | n²/2 states × up to n split points |
| Tabulation | O(n³) | O(n²) | three nested loops |

With n ≤ 50, n³ = 125,000 operations. The largest score is under 48 triangles × 100³ = 4.8 × 10⁷, which fits in an `int`.

## Why the optimized approach is better

Each sub-polygon `i..j` is shared by a huge number of triangulations. The DP solves it once. The split-point argument (edge `i–j` belongs to exactly one triangle) proves that no triangulation is missed and none is counted twice.

## Common mistakes

1. **Trying to choose diagonals directly.** It is hard to keep them non-crossing. Choose the triangle on a fixed edge instead.
2. **Base case `dp[i][i+2] = 0`.** Three vertices already form one triangle, whose cost is the product.
3. **Looping `i` upward with `j` inside.** `dp[k][j]` for `k > i` is not ready yet. Fill by gap.
4. **Thinking it wraps around.** The polygon is circular, but fixing edge `0–(n−1)` turns it into a linear range.

## Key takeaway

> Split a range at `k`, solve both sides independently, and pay a cost that depends on `(i, k, j)`: `dp[i][j] = min_k dp[i][k] + dp[k][j] + cost(i,k,j)`. Here the split is the triangle that owns the closing edge.

## Related problems

- [1547. Minimum Cost to Cut a Stick](../1547-minimum-cost-to-cut-a-stick/): the same split shape; the cost is the length of the piece.
- [1130. Minimum Cost Tree From Leaf Values](../1130-minimum-cost-tree-from-leaf-values/): split a range into a left and right subtree.
- [375. Guess Number Higher or Lower II](../375-guess-number-higher-or-lower-ii/): a split point with `max` instead of `+`.
- Course topic: [Matrix Chain Multiplication](../../../04-advanced-dp-patterns/04-matrix-chain-multiplication/)
