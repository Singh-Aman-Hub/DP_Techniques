# Matrix Chain Multiplication — Walkthrough

---

## 1. Intuition

### What does multiplying two matrices cost?

A matrix is a rectangle of numbers. If `X` has `p` rows and `q` columns (`p × q`) and `Y` is `q × r`, then `X × Y` is a `p × r` matrix. Each of its `p · r` entries is a sum of `q` products. So:

```
cost(X × Y) = p · q · r  scalar multiplications          result: p × r

     X            Y               X·Y
  p [####]  ×  q [######]  =  p [######]
     q             r              r
```

You only need this one rule for the whole topic.

### Why the order matters — a numeric example

Take three matrices:

```
A: 10 × 30        B: 30 × 5        C: 5 × 60
```

**Order 1: `(AB)C`**
- `AB`: 10 · 30 · 5 = **1,500**, result is 10 × 5.
- `(AB)C`: 10 · 5 · 60 = **3,000**, result is 10 × 60.
- Total: **4,500**.

**Order 2: `A(BC)`**
- `BC`: 30 · 5 · 60 = **9,000**, result is 30 × 60.
- `A(BC)`: 10 · 30 · 60 = **18,000**, result is 10 × 60.
- Total: **27,000**.

Same answer, **six times** more work. Order 1 is cheap because `AB` collapses into a small 10 × 5 matrix early. Order 2 builds a big 30 × 60 intermediate.

With 3 matrices there are only 2 orders. With `n` matrices the number of orders is the Catalan number C(n−1): 5 orders for 4 matrices, 42 for 6, 4,862 for 10, and over 1.7 billion for 20. Checking them all is hopeless. DP will do it in O(n³).

### The key idea: think about the **last** multiplication

However you bracket `A_1 A_2 … A_n`, there is one final multiplication. It multiplies "everything to the left of some point `k`" by "everything to the right":

```
   (A_1 A_2 ... A_k) × (A_{k+1} ... A_n)
    \_____________/     \_______________/
     a smaller chain     a smaller chain
     (bracket it          (bracket it
      optimally)           optimally)
```

The left chain and the right chain are computed separately, so each should be bracketed optimally on its own. The last step costs `rows of the left result × shared size × columns of the right result`. Try every `k` and keep the cheapest. That is interval DP.

---

## 2. Problem statement in our own words

There are `n` matrices `A_1, …, A_n`. Matrix `A_m` has size `d[m-1] × d[m]`, so the whole chain is described by an array `d` of length `n + 1` (consecutive matrices share a dimension, which is why they can be multiplied). Find the minimum total number of scalar multiplications to compute `A_1 × A_2 × … × A_n`, and one bracketing that achieves it.

This classic problem has no LeetCode judge; [Slimes](https://atcoder.jp/contests/dp/tasks/dp_n) and [LC 1039](https://leetcode.com/problems/minimum-score-triangulation-of-polygon/) are judged problems with the same structure.

---

## 3. Examples

**Main example (used in every section):**

```
d = [40, 20, 30, 10, 30]

A: 40 × 20     B: 20 × 30     C: 30 × 10     D: 10 × 30
```

Answer: **26,000** with the bracketing `((A(BC))D)`:
- `BC`: 20 · 30 · 10 = 6,000 → 20 × 10
- `A(BC)`: 40 · 20 · 10 = 8,000 → 40 × 10
- `(A(BC))D`: 40 · 10 · 30 = 12,000 → 40 × 30
- total 6,000 + 8,000 + 12,000 = 26,000.

Compare with the naive left-to-right `((AB)C)D`: 40·20·30 + 40·30·10 + 40·10·30 = 24,000 + 12,000 + 12,000 = 48,000.

Other tests:

| `d` | Answer | Best order |
|---|---|---|
| `[10, 30, 5, 60]` | 4,500 | `((AB)C)` |
| `[10, 20, 30, 40, 30]` | 30,000 | `(((AB)C)D)` |
| `[10, 20, 30]` | 6,000 | `(AB)` |
| `[10, 20]` (one matrix) | 0 | `A` |

---

## 4. Deriving the solution — the 7 questions

### Q1. What is changing?
The sub-chain we are currently bracketing: its first matrix `i` and last matrix `j`.

### Q2. What does the state represent?
> `f(i, j)` = the minimum number of scalar multiplications needed to compute the product `A_i × A_{i+1} × … × A_j`.

The product of this sub-chain is a `d[i-1] × d[j]` matrix, whatever the bracketing.

### Q3. What are the choices?
Where the **last** multiplication splits the chain: `k = i, i+1, …, j−1`, meaning

```
(A_i … A_k) × (A_{k+1} … A_j)
   d[i-1] × d[k]     d[k] × d[j]
```

This is a **disjoint** split: matrix `k` belongs to the left part only.

### Q4. What is the recurrence?

```
f(i, j) = min over k = i .. j-1 of
          f(i, k)                    // best cost for the left chain
        + f(k+1, j)                  // best cost for the right chain
        + d[i-1] * d[k] * d[j]       // cost of the final multiplication
```

The last term uses the rule `p · q · r` with `p = d[i-1]` (rows of the left result), `q = d[k]` (shared size), `r = d[j]` (columns of the right result).

### Q5. What are the base cases?
`f(i, i) = 0`: a single matrix is already "computed"; nothing to multiply.

### Q6. What is the order of computation?
`f(i, j)` needs `f(i, k)` and `f(k+1, j)`, both shorter chains. Fill by chain length: 2, 3, …, n. The answer is `f(1, n)`.

### Q7. Can the state space be optimized?
Not simply. As in every split-point interval DP, a cell reads part of its row and part of its column, so the full O(n²) table is kept. (For special cost functions, [Knuth optimization](../../05-dp-optimization/08-knuth-optimization/) can cut the time; MCM's cost does not qualify in general.)

---

## 5. Brute force: recursion

```java
static long best(int[] d, int i, int j) {
    if (i == j) return 0;
    long cheapest = Long.MAX_VALUE;
    for (int k = i; k < j; k++) {
        long cost = best(d, i, k) + best(d, k + 1, j) + (long) d[i - 1] * d[k] * d[j];
        cheapest = Math.min(cheapest, cost);
    }
    return cheapest;
}
```

Recursion tree for `d = [40, 20, 30, 10, 30]` (4 matrices, calls `(i,j)`):

```
                                   (1,4)
             _______________________/ | \_______________________
            /  k=1                    | k=2                     \  k=3
     (1,1)+(2,4)               (1,2)+(3,4)                (1,3)+(4,4)
              |                                               |
            (2,4)                                           (1,3)
          /       \                                       /       \
   (2,2)+(3,4)  (2,3)+(4,4)                       (1,1)+(2,3)  (1,2)+(3,3)
          ^^^^^   ^^^^^                                  ^^^^^   ^^^^^
```

`(3,4)` is solved under `(2,4)` and again directly under `(1,4)` (k = 2). `(2,3)` is solved under `(2,4)` and again under `(1,3)`. `(1,2)` appears twice too. With 4 matrices the waste is small, but the tree grows like the Catalan numbers while there are only `n(n+1)/2` different `(i, j)` pairs.

Code: [BruteForce.java](solution/BruteForce.java).

---

## 6. Memoization (top-down)

```java
// memo[i][j]: min multiplications for A_i..A_j; -1 = not computed
if (i == j) return 0;
if (memo[i][j] != -1) return memo[i][j];
... same loop over k ...
return memo[i][j] = cheapest;
```

Dry run order on the main example (depth-first, `k` ascending):

| step | call | event |
|---|---|---|
| 1 | (1,4), k=1 | needs (2,4) |
| 2 | (2,4), k=2 | needs (3,4) → computed: 9,000 |
| 3 | (2,4), k=3 | needs (2,3) → computed: 6,000; (2,4) = 12,000 stored |
| 4 | (1,4), k=2 | needs (1,2) → computed: 24,000; needs (3,4) → **cache hit** 9,000 |
| 5 | (1,4), k=3 | needs (1,3): k=1 uses (2,3) → **cache hit** 6,000; k=2 uses (1,2) → **cache hit** 24,000; (1,3) = 14,000 |
| 6 | (1,4) | min of 36,000, 69,000, 26,000 = **26,000** |

Every range is computed once; the repeats from the tree become cache hits.

Code: [Memoization.java](solution/Memoization.java).

---

## 7. Tabulation (bottom-up)

```java
long[][] dp = new long[n + 1][n + 1];            // dp[i][i] = 0
for (int length = 2; length <= n; length++) {
    for (int i = 1; i + length - 1 <= n; i++) {
        int j = i + length - 1;
        dp[i][j] = Long.MAX_VALUE;
        for (int k = i; k < j; k++) {
            dp[i][j] = Math.min(dp[i][j], dp[i][k] + dp[k + 1][j] + (long) d[i - 1] * d[k] * d[j]);
        }
    }
}
return dp[1][n];
```

Fill order for 4 matrices (numbers = the order in which cells are computed):

```
          j=1   j=2   j=3   j=4
   i=1     0     1     4     6
   i=2           0     2     5
   i=3                 0     3
   i=4                       0
           ^ diagonal of zeros = base cases
```

Code: [Tabulation.java](solution/Tabulation.java).

---

## 8. Space optimization

Skipped: each cell needs cells from its whole row segment and whole column segment, so the O(n²) table cannot be reduced. (The `split` table in section 9 is also O(n²).)

---

## 9. Dry run on a full example

`d = [40, 20, 30, 10, 30]`: `A = 40×20`, `B = 20×30`, `C = 30×10`, `D = 10×30`.

### Length 2 (one split each)

| cell | k | dp[i][k] + dp[k+1][j] + d[i-1]·d[k]·d[j] | value | split |
|---|---|---|---|---|
| dp[1][2] (AB) | 1 | 0 + 0 + 40·20·30 | **24,000** | 1 |
| dp[2][3] (BC) | 2 | 0 + 0 + 20·30·10 | **6,000** | 2 |
| dp[3][4] (CD) | 3 | 0 + 0 + 30·10·30 | **9,000** | 3 |

### Length 3

| cell | k | calculation | candidate |
|---|---|---|---|
| dp[1][3] (ABC) | 1 | dp[1][1] + dp[2][3] + 40·20·10 = 0 + 6,000 + 8,000 | **14,000** ← min |
| | 2 | dp[1][2] + dp[3][3] + 40·30·10 = 24,000 + 0 + 12,000 | 36,000 |
| dp[2][4] (BCD) | 2 | dp[2][2] + dp[3][4] + 20·30·30 = 0 + 9,000 + 18,000 | 27,000 |
| | 3 | dp[2][3] + dp[4][4] + 20·10·30 = 6,000 + 0 + 6,000 | **12,000** ← min |

### Length 4

| cell | k | calculation | candidate |
|---|---|---|---|
| dp[1][4] (ABCD) | 1 | dp[1][1] + dp[2][4] + 40·20·30 = 0 + 12,000 + 24,000 | 36,000 |
| | 2 | dp[1][2] + dp[3][4] + 40·30·30 = 24,000 + 9,000 + 36,000 | 69,000 |
| | 3 | dp[1][3] + dp[4][4] + 40·10·30 = 14,000 + 0 + 12,000 | **26,000** ← min |

### Final tables

```
 dp (min cost)                           split (best k)
          j=1     j=2     j=3     j=4             j=2   j=3   j=4
   i=1     0    24000   14000   26000      i=1     1     1     3
   i=2             0     6000   12000      i=2           2     3
   i=3                     0     9000      i=3                 3
   i=4                             0
```

### Printing the optimal parenthesization

Store `split[i][j] = k` whenever a new minimum is found. Then rebuild recursively:

```java
String bracket(int i, int j) {
    if (i == j) return "" + (char) ('A' + i - 1);           // a single matrix: its letter
    int k = split[i][j];
    return "(" + bracket(i, k) + bracket(k + 1, j) + ")";
}
```

Trace for our example:

```
bracket(1,4): split = 3  ->  "(" + bracket(1,3) + bracket(4,4) + ")"
  bracket(1,3): split = 1  ->  "(" + bracket(1,1) + bracket(2,3) + ")"
    bracket(1,1) = "A"
    bracket(2,3): split = 2  ->  "(" + "B" + "C" + ")" = "(BC)"
    -> "(A(BC))"
  bracket(4,4) = "D"
  -> "((A(BC))D)"
```

Result: `((A(BC))D)`, cost 26,000, matching section 3.

Code: [PrintParenthesization.java](solution/PrintParenthesization.java).

---

## 10. Complexity analysis

| Approach | Time | Space | Why |
|---|---|---|---|
| Brute force | exponential (≈ Catalan numbers, about 4ⁿ / n^1.5) | O(n) | tries every bracketing; recursion depth ≤ n |
| Memoization | O(n³) | O(n²) | n(n−1)/2 ranges with `i < j`, each loops over up to n−1 values of `k` |
| Tabulation | O(n³) | O(n²) | three nested loops: length, i, k |
| Printing | O(n) extra | O(n²) for `split` | the rebuild visits each of the 2n−1 nodes of the bracket tree once |

Exact count of inner-loop steps: Σ over lengths L of (n − L + 1)(L − 1) = (n³ − n)/6. For `n = 100` that is about 166,000.

---

## 11. Why this approach works

Every full bracketing of `A_i … A_j` (with `i < j`) has exactly one last multiplication, which splits it at some `k`. The cost of the bracketing is: cost of the left part's bracketing + cost of the right part's bracketing + the last multiplication. The last term depends only on `i, k, j`, not on how the parts were bracketed. So for a fixed `k`, the cheapest total uses the cheapest left bracketing and the cheapest right bracketing — optimal substructure. Taking the minimum over all `k` considers every possible bracketing. Shorter chains are computed first, so by induction on length every value we read is already optimal.

---

## 12. Common mistakes

1. **Off-by-one with dimensions.** Matrix `m` is `d[m-1] × d[m]`. The final cost is `d[i-1] · d[k] · d[j]`, not `d[i] · d[k] · d[j]`.
2. **Using `k = j` as a split.** Then the right part is empty. `k` runs from `i` to `j − 1`.
3. **Shared-split confusion.** MCM uses `dp[k+1][j]`, not `dp[k][j]` (compare with [triangulation](../03-interval-dp/), which shares `k`).
4. **`int` overflow.** Costs grow quickly; use `long`.
5. **Row-by-row fill from the top.** Fill by length (or `i` descending).
6. **Printing with `<=` instead of `<`.** Both give an optimal answer, but tests that expect one specific bracketing on ties will differ.

---

## 13. When to use this technique / how to recognize similar problems

MCM-style partition DP fits when:
- you combine adjacent items repeatedly until one remains, or you cut a range into pieces;
- the cost of one combine depends only on the **boundaries** of the two pieces (and maybe on the split point);
- the order of combining is free.

Translation table:

| Problem | Range | Last action | Join cost |
|---|---|---|---|
| MCM | matrices `i..j` | last multiplication at `k` | `d[i-1]·d[k]·d[j]` |
| [Slimes](https://atcoder.jp/contests/dp/tasks/dp_n) | slimes `i..j` | last merge at `k` | sum of `i..j` |
| [LC 1039](https://leetcode.com/problems/minimum-score-triangulation-of-polygon/) | vertices `i..j` | triangle `(i,k,j)` | `v[i]·v[k]·v[j]` (shared `k`) |
| [LC 1130](https://leetcode.com/problems/minimum-cost-tree-from-leaf-values/) | leaves `i..j` | root splits at `k` | `max(i..k) · max(k+1..j)` |
| [LC 1547](https://leetcode.com/problems/minimum-cost-to-cut-a-stick/) | cuts `i..j` (with ends added) | **first** cut at `k` | `cuts[j] − cuts[i]` (shared `k`) |
| [LC 312](https://leetcode.com/problems/burst-balloons/) | balloons strictly inside `(i, j)` | **last** balloon `k` | `a[i]·a[k]·a[j]` (shared `k`) |

---

## 14. Summary and revision notes

- Cost of `p × q` times `q × r` is `p·q·r`; order can change the total enormously.
- A chain of `n` matrices is given by `d[0..n]`; matrix `m` is `d[m-1] × d[m]`.
- Think about the **last** multiplication: it splits the chain at `k` into two independent chains.
- Fill by chain length; answer `dp[1][n]`; keep `split[i][j]` to print brackets.
- O(n³) time, O(n²) space.

```
dp[i][i] = 0
dp[i][j] = min over k in [i, j-1] of  dp[i][k] + dp[k+1][j] + d[i-1] * d[k] * d[j]
answer   = dp[1][n]
bracket(i, j) = (i == j) ? letter(i) : "(" + bracket(i, split[i][j]) + bracket(split[i][j]+1, j) + ")"
```

---

## Exercises

**1.** Compute both bracketings for `d = [10, 30, 5, 60]` and confirm that the DP picks the cheaper one.

<details><summary>Answer</summary>

`dp[1][2] = 10·30·5 = 1,500`, `dp[2][3] = 30·5·60 = 9,000`.
`dp[1][3] = min(k=1: 0 + 9,000 + 10·30·60 = 27,000, k=2: 1,500 + 0 + 10·5·60 = 4,500) = 4,500` with `split = 2` → `((AB)C)`.
</details>

**2.** Fill the table for `d = [10, 20, 30, 40, 30]` and print the bracketing.

<details><summary>Answer</summary>

Length 2: `dp[1][2] = 6,000`, `dp[2][3] = 24,000`, `dp[3][4] = 36,000`.
Length 3: `dp[1][3] = min(0 + 24,000 + 10·20·40 = 32,000, 6,000 + 0 + 10·30·40 = 18,000) = 18,000` (k=2); `dp[2][4] = min(0 + 36,000 + 20·30·30 = 54,000, 24,000 + 0 + 20·40·30 = 48,000) = 48,000` (k=3).
Length 4: `dp[1][4] = min(k=1: 0 + 48,000 + 10·20·30 = 54,000, k=2: 6,000 + 36,000 + 10·30·30 = 51,000, k=3: 18,000 + 0 + 10·40·30 = 30,000) = 30,000` (k=3).
Bracketing `(((AB)C)D)`.
</details>

**3.** How many scalar multiplications does `A(B(CD))` cost for the main example `d = [40, 20, 30, 10, 30]`? Is it optimal?

<details><summary>Answer</summary>

`CD` = 30·10·30 = 9,000 (30×30); `B(CD)` = 20·30·30 = 18,000 (20×30); `A(B(CD))` = 40·20·30 = 24,000. Total 51,000. Not optimal (26,000). This is the `k = 1` branch of `dp[1][4]` but with the right part bracketed as `B(CD)` (27,000) instead of the best `(BC)D` (12,000).
</details>

**4.** Rewrite the MCM recurrence for Slimes (merge adjacent slimes; merging costs the total size). What changes and what stays the same?

<details><summary>Answer</summary>

`dp[i][j] = min over k in [i, j-1] of dp[i][k] + dp[k+1][j] + (a[i] + … + a[j])`, with `dp[i][i] = 0`. The state, the split type (disjoint), the fill order and the complexity are identical; only the join cost changes from `d[i-1]·d[k]·d[j]` to the range sum (computed with prefix sums), which does not even depend on `k`.
</details>
