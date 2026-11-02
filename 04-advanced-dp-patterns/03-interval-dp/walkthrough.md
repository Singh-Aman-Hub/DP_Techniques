# Interval DP — Walkthrough

The plan: first the general idea of interval DP, with the "fill by length" order. Then one complete worked example, [LC 1039 Minimum Score Triangulation of Polygon](https://leetcode.com/problems/minimum-score-triangulation-of-polygon/), from brute force to tabulation. The next three topics (MCM, Burst Balloons, Palindrome Partitioning) are all variations of what you learn here.

---

## 1. Intuition

### Cutting a chocolate bar

You have a chocolate bar of `n` squares in a row. Breaking a piece costs something that depends on the piece. You want to break it all the way down as cheaply as possible.

Where should the **first** break go? You do not know — so you try every position. Once you break at position `k`, you are left with two smaller bars, a left one and a right one. They have nothing to do with each other any more: breaking the left bar never changes the cost of the right bar. So:

```
best(whole bar) = min over every break position k of
                  [ cost of this break + best(left bar) + best(right bar) ]
```

The left and right bars are again **contiguous ranges** of the original bar. That is the key property: every subproblem is a range `i..j`, so the state is a pair `(i, j)`.

```
  whole range:      [ i  .  .  .  k  .  .  .  j ]
  split at k:       [ i  .  .  k ] [ k+1 .  .  j ]
                       left part      right part
```

### The template

```
dp[i][j] = best answer for the range i..j

dp[i][j] = best over k in the range of
           dp[left part] + dp[right part] + cost(i, k, j)

base cases: ranges too small to split (length 1, sometimes 2)
answer:     dp[0][n-1]
```

### Two flavours of split

| Flavour | Left part | Right part | Example |
|---|---|---|---|
| **Disjoint halves** — `k` belongs to the left | `i..k` | `k+1..j` | merging slimes, matrix chains |
| **Shared point** — `k` belongs to both | `i..k` | `k..j` | polygon triangulation, burst balloons |

In a polygon, vertex `k` is a corner of both smaller polygons, so it is shared. In a row of slimes, each slime is in exactly one half. Always ask: "is the split point used on both sides or only on one?"

### Why fill by length?

`dp[i][j]` needs `dp[i][k]` and `dp[k][j]` (or `dp[k+1][j]`). Both are **shorter** than `i..j`. So if we compute all ranges of length 2, then all of length 3, then length 4, …, every range we read has already been computed.

In the table, ranges of the same length lie on the same **diagonal**:

```
            j=0   j=1   j=2   j=3   j=4   j=5
    i=0  [  L1    L2    L3    L4    L5    L6  ]   <- top-right corner = the whole range
    i=1  [   .    L1    L2    L3    L4    L5  ]
    i=2  [   .     .    L1    L2    L3    L4  ]
    i=3  [   .     .     .    L1    L2    L3  ]
    i=4  [   .     .     .     .    L1    L2  ]
    i=5  [   .     .     .     .     .    L1  ]

    Lx = a range of length x = j - i + 1.   Cells with i > j are unused.

    Fill order:  diagonal L1 (base cases) -> L2 -> L3 -> ... -> L6
                 i.e. sweep diagonals from the main diagonal toward the top-right corner.
```

Which cells does one cell read? For `dp[1][4]` with a disjoint split (`k = 1, 2, 3`):

```
            j=1    j=2    j=3    j=4
    i=1  [  L1     L2     L3      ?   ]     L1 = dp[1][1], L2 = dp[1][2], L3 = dp[1][3]   (same ROW, to the left)
    i=2  [   .      .      .     R1   ]     R1 = dp[2][4]
    i=3  [   .      .      .     R2   ]     R2 = dp[3][4]                                (same COLUMN, below)
    i=4  [   .      .      .     R3   ]     R3 = dp[4][4]

    k = 1:  L1 + R1        k = 2:  L2 + R2        k = 3:  L3 + R3
```

Each candidate pairs one cell **to the left in the same row** with one cell **below in the same column**. All of them are on shorter diagonals, so the length order is safe. A plain row-by-row fill from the top would **not** be safe: it would read cells below that are not computed yet. (Filling `i` from bottom to top and `j` from left to right also works — try to see why — but "by length" is the order that never needs an argument.)

---

## 2. Problem statement in our own words

[LC 1039](https://leetcode.com/problems/minimum-score-triangulation-of-polygon/): you have a convex polygon with `n` vertices, labelled `0..n-1` in order, and each vertex has a positive value. You must cut the polygon into `n − 2` triangles using diagonals that do not cross. A triangle's score is the product of its three vertex values; the total score is the sum over all triangles. Return the smallest possible total.

---

## 3. Examples

**Small picture — 4 vertices `[3, 7, 4, 5]`.** A quadrilateral has exactly two triangulations:

```
   v0(3) ------ v1(7)            v0(3) ------ v1(7)
     |  \         |                |         /  |
     |    \       |                |       /    |
     |      \     |                |     /      |
   v3(5) ------ v2(4)            v3(5) ------ v2(4)

  diagonal 0-2:                  diagonal 1-3:
  (0,1,2) = 3*7*4 = 84           (0,1,3) = 3*7*5 = 105
  (0,2,3) = 3*4*5 = 60           (1,2,3) = 7*4*5 = 140
  total 144  <- minimum          total 245
```

**Main example used everywhere below — 6 vertices `[1, 3, 1, 4, 1, 5]` → 13.**

Other test cases:
- `[1, 2, 3]` → 6 (a triangle: one choice, 1·2·3).
- `[1, 2, 3, 4]` → 18.

---

## 4. Deriving the solution — the 7 questions

### Q1. What is changing?
The piece of the polygon we are still triangulating. We will see that it is always a run of consecutive vertices `i, i+1, …, j` closed by the edge `(i, j)`.

### Q2. What does the state represent?
> `dp[i][j]` = the minimum total score to triangulate the polygon formed by vertices `i, i+1, …, j` (with the edge `(i, j)` closing it).

The answer is `dp[0][n-1]`.

### Q3. What are the choices?
The edge `(i, j)` is a side of this sub-polygon, so in **any** triangulation it belongs to exactly one triangle. That triangle's third corner is some vertex `k` with `i < k < j`. We try every `k`.

```
                 k
               /   \
             /       \
      left  /  (i,k,j)  \  right
   polygon /   triangle  \ polygon
   i..k   i ------------- j   k..j
```

Choosing `k` cuts the sub-polygon into: the triangle `(i, k, j)`, the polygon `i..k` (on one side) and the polygon `k..j` (on the other). Vertex `k` is **shared** by both sides.

### Q4. What is the recurrence?

```
dp[i][j] = min over k = i+1 .. j-1 of
           dp[i][k] + dp[k][j] + values[i] * values[k] * values[j]
```

- `dp[i][k]`: best way to triangulate the left piece;
- `dp[k][j]`: best way to triangulate the right piece;
- `values[i] * values[k] * values[j]`: the score of the triangle we just chose.

The two pieces are independent — no triangle can cross the triangle `(i, k, j)` — so adding their best scores is correct.

### Q5. What are the base cases?
`dp[i][i+1] = 0`: two vertices form an edge, not a polygon; nothing to triangulate. (Also `dp[i][i] = 0`, never used.) Ranges of 3 vertices have exactly one choice of `k`.

### Q6. What is the order of computation?
`dp[i][j]` reads `dp[i][k]` and `dp[k][j]`, which have fewer vertices. Fill by number of vertices: 3, then 4, …, then `n` (the diagonals of section 1).

### Q7. Can the state space be optimized?
Not in a simple way. One cell reads its whole row to the left and its whole column below, so we cannot throw away old rows or diagonals. Interval DP is normally O(n²) space. (Some interval problems allow speed-ups like [Knuth optimization](../../05-dp-optimization/08-knuth-optimization/), covered in module 05.)

---

## 5. Brute force: recursion

```java
static int best(int[] values, int i, int j) {
    if (j - i < 2) return 0;                      // fewer than 3 vertices
    int cheapest = Integer.MAX_VALUE;
    for (int k = i + 1; k < j; k++) {
        int cost = best(values, i, k) + best(values, k, j) + values[i] * values[k] * values[j];
        cheapest = Math.min(cheapest, cost);
    }
    return cheapest;
}
```

Part of the recursion tree for `[1, 3, 1, 4, 1, 5]` (calls written as `(i,j)`):

```
                              (0,5)
        _____________________/ |  |  \______________________
       /                 k=2   |  | k=3                     \ k=4
   k=1: (0,1)+(1,5)    (0,2)+(2,5)  (0,3)+(3,5)          (0,4)+(4,5)
              |                                             |
            (1,5)                                         (0,4)
     /     |       \                                /      |      \
 (1,2)+(2,5) (1,3)+(3,5) (1,4)+(4,5)          (0,1)+(1,4) (0,2)+(2,4) (0,3)+(3,4)
                          ^^^^^                      ^^^^^
                          (1,4) solved here      ... and again here
```

`(1,4)`, `(2,5)`, `(3,5)`, `(0,3)` … each appear several times. The number of triangulations of an `n`-gon is the Catalan number C(n−2) (for `n = 20` that is about 477 million), and the plain recursion explores a tree of similar size. But there are only about `n²/2` different `(i, j)` pairs.

Code: [BruteForce.java](solution/BruteForce.java).

---

## 6. Memoization (top-down)

Add a table `memo[i][j]` (−1 = not computed). Before computing, check it; after computing, store it.

```java
if (j - i < 2) return 0;
if (memo[i][j] != -1) return memo[i][j];     // cache hit
... loop over k ...
return memo[i][j] = cheapest;
```

On our example, `(1,4)` is first computed inside `(1,5)` (value 7). When `(0,4)` later needs `(1,4)` for `k = 1`, it gets 7 instantly. In total exactly 10 ranges with 3 or more vertices are computed (4 of length 3, 3 of length 4, 2 of length 5, 1 of length 6), each once.

Code: [Memoization.java](solution/Memoization.java).

---

## 7. Tabulation (bottom-up)

```java
int[][] dp = new int[n][n];                         // zeros = base cases
for (int length = 3; length <= n; length++) {       // number of vertices in the range
    for (int i = 0; i + length - 1 < n; i++) {
        int j = i + length - 1;
        dp[i][j] = Integer.MAX_VALUE;
        for (int k = i + 1; k < j; k++) {
            dp[i][j] = Math.min(dp[i][j], dp[i][k] + dp[k][j] + values[i] * values[k] * values[j]);
        }
    }
}
return dp[0][n - 1];
```

The three nested loops are: which diagonal (`length`), where on the diagonal (`i`, which fixes `j`), and which split (`k`).

Small table for `[3, 7, 4, 5]`:

```
length 3:  dp[0][2] = 3*7*4 = 84        dp[1][3] = 7*4*5 = 140
length 4:  dp[0][3] = min( k=1: dp[0][1] + dp[1][3] + 3*7*5 = 0 + 140 + 105 = 245,
                           k=2: dp[0][2] + dp[2][3] + 3*4*5 = 84 + 0  + 60  = 144 ) = 144

          j=0   j=1   j=2   j=3
   i=0     0     0    84   144
   i=1           0     0   140
   i=2                 0     0
   i=3                       0
```

Code: [Tabulation.java](solution/Tabulation.java).

---

## 8. Space optimization

Skipped: as explained in Q7, each cell reads a full row segment and a full column segment, so no row or diagonal can be discarded early. O(n²) is the normal space for interval DP.

---

## 9. Dry run on a full example

`values = [1, 3, 1, 4, 1, 5]` (`v0..v5`).

**Length 3** (one choice of `k`, the middle vertex):

| range | k | dp[i][k] + dp[k][j] + product | dp |
|---|---|---|---|
| (0,2) | 1 | 0 + 0 + 1·3·1 = 3 | **3** |
| (1,3) | 2 | 0 + 0 + 3·1·4 = 12 | **12** |
| (2,4) | 3 | 0 + 0 + 1·4·1 = 4 | **4** |
| (3,5) | 4 | 0 + 0 + 4·1·5 = 20 | **20** |

**Length 4:**

| range | k = first option | k = second option | dp |
|---|---|---|---|
| (0,3) | k=1: 0 + 12 + 1·3·4 = 24 | k=2: 3 + 0 + 1·1·4 = 7 | **7** |
| (1,4) | k=2: 0 + 4 + 3·1·1 = 7 | k=3: 12 + 0 + 3·4·1 = 24 | **7** |
| (2,5) | k=3: 0 + 20 + 1·4·5 = 40 | k=4: 4 + 0 + 1·1·5 = 9 | **9** |

**Length 5:**

| range | k options | dp |
|---|---|---|
| (0,4) | k=1: 0 + 7 + 1·3·1 = 10; k=2: 3 + 4 + 1·1·1 = **8**; k=3: 7 + 0 + 1·4·1 = 11 | **8** |
| (1,5) | k=2: 0 + 9 + 3·1·5 = 24; k=3: 12 + 20 + 3·4·5 = 92; k=4: 7 + 0 + 3·1·5 = **22** | **22** |

**Length 6:**

| k | dp[0][k] + dp[k][5] + 1·v[k]·5 | total |
|---|---|---|
| 1 | 0 + 22 + 15 | 37 |
| 2 | 3 + 9 + 5 | 17 |
| 3 | 7 + 20 + 20 | 47 |
| 4 | 8 + 0 + 5 | **13** |

Final table:

```
          j=0   j=1   j=2   j=3   j=4   j=5
   i=0     0     0     3     7     8    13   <- answer
   i=1           0     0    12     7    22
   i=2                 0     0     4     9
   i=3                       0     0    20
   i=4                             0     0
   i=5                                   0
```

Reconstructing the triangles: `(0,5)` chose `k=4` → triangle (0,4,5) = 5. `(0,4)` chose `k=2` → triangle (0,2,4) = 1, and sub-polygons (0,2) → triangle (0,1,2) = 3 and (2,4) → triangle (2,3,4) = 4. Total 5 + 1 + 3 + 4 = **13**. All triangles use vertex 0 or vertex 2 or 4 — the vertices with value 1 — which makes sense: small values make small products.

---

## 10. Complexity analysis

| Approach | Time | Space | Why |
|---|---|---|---|
| Brute force | exponential (Catalan-like) | O(n) | every call branches over all `k`, and ranges are recomputed; depth ≤ n |
| Memoization | O(n³) | O(n²) | about n²/2 ranges, each loops over up to n split points once; recursion depth O(n) |
| Tabulation | O(n³) | O(n²) | three nested loops (length, i, k), each up to n |

A more careful count: the number of `(i, k, j)` triples with `i < k < j` is C(n, 3) ≈ n³/6. For `n = 50` (the LC 1039 limit) that is about 20,000 operations — tiny.

---

## 11. Why this approach works

Every triangulation of the polygon `i..j` contains exactly one triangle that uses the edge `(i, j)`; call its third corner `k`. Removing that triangle leaves two smaller polygons, `i..k` and `k..j`, and the rest of the triangulation is a triangulation of each of them, chosen independently. So the best total for `i..j` with this `k` is the best for `i..k` plus the best for `k..j` plus the triangle. Trying every `k` covers every triangulation. Since `i..k` and `k..j` are shorter, by induction on length their `dp` values are already correct.

The general lesson: **interval DP works when some choice inside the range splits it into independent sub-ranges.** If the two sides could still affect each other, the sum would be wrong (that is exactly the trap in [Burst Balloons](../05-burst-balloons/)).

---

## 12. Common mistakes

1. **Filling row by row from the top.** `dp[0][5]` would read `dp[1][5]`, not yet computed. Fill by length (or `i` from bottom to top).
2. **Wrong split flavour.** Using `dp[k+1][j]` in a shared-vertex problem (or `dp[k][j]` in a disjoint one) double-counts or loses an element.
3. **Wrong `k` range.** For triangulation `k` runs strictly between `i` and `j`. For disjoint halves `k` runs from `i` to `j−1`.
4. **Initialising `dp[i][j]` to 0 before taking a minimum.** Start from `Integer.MAX_VALUE` (but leave base cases at 0).
5. **Overflow.** Products of three values can be large; LC 1039 fits in `int`, but MCM-style costs often need `long`.
6. **Thinking the answer is `dp[n-1][0]` or `dp[n][n]`.** The whole range is `dp[0][n-1]`, the top-right corner.

---

## 13. When to use this technique / how to recognize similar problems

Look for:
- "merge adjacent …", "cut into pieces", "remove/burst elements and neighbours change", "parenthesize", "triangulate";
- constraints with `n` up to about 500 (O(n³) ≈ 10⁸ is the upper limit) — a strong hint;
- the answer for a range depends on how you split it, and after the split the parts never interact.

Two common interval-DP families:

| Family | Transition | Problems |
|---|---|---|
| **Split point** | `dp[i][j] = best over k (dp[i][k], dp[k(+1)][j]) + cost` | LC 1039, Slimes, [MCM](../04-matrix-chain-multiplication/), LC 1547, LC 1130, LC 375, [Burst Balloons](../05-burst-balloons/) |
| **Shrink from the ends** | `dp[i][j]` from `dp[i+1][j]`, `dp[i][j-1]`, `dp[i+1][j-1]` | LC 516, LC 5/647, games LC 877, LC 486, CSES 1097, AtCoder dp_l |

The second family is O(n²) because there is no `k` loop.

### Practice sketch: Slimes ([AtCoder dp_n](https://atcoder.jp/contests/dp/tasks/dp_n))

`n` slimes in a row with sizes `a[0..n-1]`. Merging two **adjacent** slimes of sizes `x` and `y` costs `x + y` and gives one slime of size `x + y`. Find the minimum total cost to merge all of them into one.

- **State:** `dp[i][j]` = minimum cost to merge slimes `i..j` into one slime.
- **Choice:** the **last** merge joins the slime made from `i..k` with the slime made from `k+1..j` (disjoint halves).
- **Recurrence:** `dp[i][j] = min over k in [i, j-1] of dp[i][k] + dp[k+1][j] + sum(i..j)`. The last merge costs the total size of the range, whatever `k` is.
- **Base:** `dp[i][i] = 0`.
- **Range sums** in O(1) with a prefix-sum array `prefix[j+1] − prefix[i]`.

---

## 14. Summary and revision notes

- State: `dp[i][j]` = best answer for the contiguous range `i..j`.
- Transition: try every split `k`; add the two sides and the join cost.
- Shared split (`dp[i][k]`, `dp[k][j]`) vs disjoint split (`dp[i][k]`, `dp[k+1][j]`).
- Fill by length (diagonals), answer in the top-right corner `dp[0][n-1]`.
- O(n³) time, O(n²) space; works for `n` up to a few hundred.

```
dp[i][j] = min over k (i < k < j) of  dp[i][k] + dp[k][j] + v[i] * v[k] * v[j]      (LC 1039)
dp[i][i+1] = 0
for length = 3..n:  for i:  j = i + length - 1:  for k ...
answer = dp[0][n-1]
```

---

## Exercises

**1.** Fill the LC 1039 table for `values = [1, 2, 3, 4]` and give the minimum score.

<details><summary>Answer</summary>

`dp[0][2] = 1·2·3 = 6`, `dp[1][3] = 2·3·4 = 24`. `dp[0][3] = min(k=1: 0 + 24 + 1·2·4 = 32, k=2: 6 + 0 + 1·3·4 = 18) = 18`. Answer **18** (diagonal 0–2).
</details>

**2.** Solve Slimes for `a = [10, 20, 30, 40]` by hand.

<details><summary>Answer</summary>

Length 2: `dp[0][1] = 30`, `dp[1][2] = 50`, `dp[2][3] = 70`.
Length 3: `dp[0][2] = min(0 + 50, 30 + 0) + 60 = 90`; `dp[1][3] = min(0 + 70, 50 + 0) + 90 = 140`.
Length 4: `dp[0][3] = min(0 + 140, 30 + 70, 90 + 0) + 100 = 90 + 100 = **190**` (merge 10+20, then +30, then +40).
</details>

**3.** In the "fill by length" diagram, the cell `dp[2][5]` belongs to which diagonal, and which cells does it read in a disjoint-split problem?

<details><summary>Answer</summary>

Length `5 − 2 + 1 = 4`. With `k = 2, 3, 4` it reads the pairs `(dp[2][2], dp[3][5])`, `(dp[2][3], dp[4][5])`, `(dp[2][4], dp[5][5])` — cells of lengths 1–3, all on earlier diagonals.
</details>

**4.** Why does filling `i` from `n−1` down to `0`, and `j` from `i` up to `n−1`, also work?

<details><summary>Answer</summary>

`dp[i][j]` reads cells in row `i` with a smaller `j` (computed earlier in the same row, because `j` increases) and cells in column `j` with a larger row index (`k` or `k+1` > `i`), which belong to rows already finished because `i` decreases. So every dependency is ready.
</details>
