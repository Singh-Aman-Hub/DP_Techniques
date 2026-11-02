# Rod Cutting — Walkthrough

## 1. Intuition

A carpenter has a 4-metre plank. A timber merchant pays:

```
piece length (m):   1   2   3   4
price (₹):          1   5   8   9
```

Selling the whole plank gives ₹9. But two 2-metre pieces give ₹5 + ₹5 = ₹10. A 3 and a 1 give ₹8 + ₹1 = ₹9. Four 1-metre pieces give only ₹4. The best plan is **two pieces of 2 m, for ₹10**.

To be sure we have the best plan, we must consider every way of chopping the plank, and there are many (for length `n` there are 2ⁿ⁻¹ ways to choose cut positions). The DP insight is the same one you saw in knapsack: **after cutting off one piece, what is left is a smaller plank of the same kind of problem.**

Look at the problem through knapsack glasses:

```
knapsack word        rod-cutting meaning
─────────────        ──────────────────────────────
item                 a piece length k (1..n)
weight of item       k (metres of rod it uses)
value of item        prices[k-1]
capacity             the rod length still uncut
unlimited copies?    yes — we may cut several pieces of the same length
```

So rod cutting **is** [unbounded knapsack](../07-unbounded-knapsack/), with one bonus: because a length-1 piece always exists, any leftover can always be sold, so every capacity can be filled exactly.

## 2. Problem statement in our own words

There is no official judge for this problem; it is the classic example from CLRS, section 15.1.

You are given an integer `n` (rod length) and an array `prices` of length `n`, where `prices[k-1]` is what a piece of length `k` sells for. Cut the rod into pieces whose lengths add up to exactly `n` (not cutting at all is allowed — that is one piece of length `n`). Return the maximum total price, and one list of piece lengths that achieves it.

## 3. Examples

| prices | n | max revenue | one optimal cut list | why |
|---|---|---|---|---|
| `[1, 5, 8, 9]` | 4 | 10 | `[2, 2]` | 5 + 5 beats 9 (no cut) and 8 + 1 |
| `[1, 5, 8, 9, 10, 17, 17, 20]` | 8 | 22 | `[6, 2]` | 17 + 5; the CLRS price table |
| `[3, 5, 8, 9, 10, 17, 17, 20]` | 8 | 24 | eight 1s | at ₹3 per metre, unit pieces are the best rate |
| `[2]` | 1 | 2 | `[1]` | nothing to cut |
| `[]` | 0 | 0 | `[]` | empty rod |

**Running example:** `prices = [1, 5, 8, 9]`, `n = 4` (answer 10, cuts `[2, 2]`) in Sections 5–8. Section 9 then runs the full CLRS example with `n = 8`.

## 4. Deriving the solution — the 7 questions

### 4.1 What is changing?

As in [unbounded knapsack](../07-unbounded-knapsack/), we go through the "items" (piece lengths) one at a time and decide how many of each to cut. Two things change:

1. **Which piece lengths are still allowed** — we consider lengths from the longest (`n`) down to 1, and call the current one `maxPiece`.
2. **How much rod is still uncut** — `remaining`.

### 4.2 What does the state represent?

> `best(maxPiece, remaining)` = the maximum revenue obtainable from a rod of length `remaining` if we may only cut pieces whose lengths are in `1..maxPiece`.

The answer is `best(n, n)`.

### 4.3 What are the choices?

- **Skip** length `maxPiece` — we will not cut any (more) pieces of this length: `best(maxPiece - 1, remaining)`.
- **Take** one piece of length `maxPiece` (only if `maxPiece <= remaining`): earn `prices[maxPiece-1]` and **stay** at `maxPiece`, because we may cut another piece of the same length: `prices[maxPiece-1] + best(maxPiece, remaining - maxPiece)`.

### 4.4 What is the recurrence?

We want the most money, so take the `max`:

```
best(p, r) = max( best(p - 1, r),                          // skip length p
                  prices[p-1] + best(p, r - p) )           // cut one piece of length p   (if p ≤ r)
```

### 4.5 What are the base cases?

- `remaining == 0` → **0**: no rod, no money.
- `maxPiece == 1` → **`remaining * prices[0]`**: only unit pieces are allowed, so the whole rest is cut into `remaining` unit pieces. This base case is why the rod is always used up exactly, and why we never need a "−∞ for impossible" value (unlike [Coin Change](../08-coin-change/)).

### 4.6 What is the order of computation?

`best(p, r)` needs `best(p-1, r)` (previous piece length) and `best(p, r-p)` (same piece length, shorter rod). In a table with rows `p = 1..n` and columns `r = 0..n`: fill rows **top to bottom**, each row **left to right**.

### 4.7 Can the state space be optimized?

Row `p` reads row `p-1` (same column) and row `p` (to the left), so one array suffices — the same left-to-right trick as [Coin Change II](../09-coin-change-ii/). An even simpler 1D formulation exists for this problem: **"what is the first piece?"**

> `best[r]` = max over first-piece length `c` in `1..r` of `prices[c-1] + best[r - c]`.

Both are O(n²) time and O(n) memory; the first-cut form makes reconstruction especially easy (Section 8).

## 5. Brute force: recursion

```java
private static int bestPrice(int[] prices, int maxPiece, int remaining) {
    if (remaining == 0) return 0;
    if (maxPiece == 1) return remaining * prices[0];       // only unit pieces left

    int skip = bestPrice(prices, maxPiece - 1, remaining);
    int take = Integer.MIN_VALUE;                          // "not possible" unless the piece fits
    if (maxPiece <= remaining) {
        take = prices[maxPiece - 1] + bestPrice(prices, maxPiece, remaining - maxPiece);
    }
    return Math.max(skip, take);
}
// answer: bestPrice(prices, n, n)
```

Full file: [solution/BruteForce.java](solution/BruteForce.java).

### Recursion tree for prices = [1, 5, 8, 9], n = 4

Nodes are `best(p, r)`. "skip" lowers `p`; "take p" lowers `r` by `p` and earns `prices[p-1]`.

```
best(4,4)                                              = max(10, 9) = 10
├─ skip → best(3,4)                                    = max(10, 9) = 10
│         ├─ skip → best(2,4)                          = max(4, 10) = 10
│         │         ├─ skip → best(1,4) = 4×1 = 4         (four 1s)
│         │         └─ take 2 (+5) → best(2,2)          = max(2, 5) = 5
│         │                         ├─ skip → best(1,2) = 2
│         │                         └─ take 2 (+5) → best(2,0) = 0   ⇒ 5
│         │                       ⇒ 5 + 5 = 10            (2 + 2)
│         └─ take 3 (+8) → best(3,1)                    = 1
│                         └─ skip → best(2,1)            = 1
│                                   └─ skip → best(1,1) = 1
│                       ⇒ 8 + 1 = 9                     (3 + 1)
└─ take 4 (+9) → best(4,0) = 0  ⇒ 9                      (no cut)
```

Result: **10**, from the path "skip 4, skip 3, take 2, take 2".

### Why it is slow

Each call branches into two, and the depth is up to about `2n` (n skips plus up to n takes). For n = 4 the tree is tiny (11 calls, all different). For the n = 8 CLRS example it already makes **60 calls for only 39 different states**; for n = 30 it explodes. The repeated states are the same "smaller rods" reached by different sequences of cuts.

## 6. Memoization (top-down)

The state `(maxPiece, remaining)` has at most `n × (n+1)` values. Cache them:

```java
// memo[maxPiece][remaining]; -1 = not computed (all real revenues are ≥ 0)
if (memo[maxPiece][remaining] != -1) return memo[maxPiece][remaining];
...
memo[maxPiece][remaining] = Math.max(skip, take);
```

Full file: [solution/Memoization.java](solution/Memoization.java).

**Where the cache helps.** In the n = 8 example, state `best(2, 2)` ("a 2 m rod using pieces ≤ 2") is reached both by cutting a 2 from `best(2, 4)` and by skipping length 3 in `best(3, 2)` (which itself came from cutting two 3s from length 8). The first visit computes 5; every later visit is a single array read. In our tiny n = 4 example every state happens to be visited once, so memoization saves nothing there — but its table of answers is exactly what the tabulation builds next.

## 7. Tabulation (bottom-up)

`best[p][r]` = max revenue for a rod of length `r` using piece lengths `1..p`.

- Row `p = 1` is the base case: `best[1][r] = r * prices[0]`.
- For `p ≥ 2`: `best[p][r] = max(best[p-1][r], prices[p-1] + best[p][r-p])`.

```java
for (int r = 0; r <= n; r++) best[1][r] = r * prices[0];
for (int p = 2; p <= n; p++) {
    for (int r = 0; r <= n; r++) {
        best[p][r] = best[p - 1][r];                                        // skip length p
        if (p <= r) best[p][r] = Math.max(best[p][r], prices[p - 1] + best[p][r - p]);  // cut length p
    }
}
```

### Filling the table for prices = [1, 5, 8, 9], n = 4

Row 1 (only unit pieces, ₹1 each):

```
          r:  0   1   2   3   4
p=1           0   1   2   3   4
```

Row 2 (lengths 1–2; a 2-piece earns 5):

```
r=0: 0
r=1: 2 doesn't fit → copy above: 1
r=2: max(above 2,  5 + best[2][0]=0)  = 5
r=3: max(above 3,  5 + best[2][1]=1)  = 6       (2 + 1)
r=4: max(above 4,  5 + best[2][2]=5)  = 10      (2 + 2)
p=2           0   1   5   6  10
```

Row 3 (a 3-piece earns 8):

```
r=0..2: 3 doesn't fit → copy: 0 1 5
r=3: max(above 6,  8 + best[3][0]=0)  = 8       (3)
r=4: max(above 10, 8 + best[3][1]=1)  = 10      (still 2 + 2)
p=3           0   1   5   8  10
```

Row 4 (a 4-piece earns 9):

```
r=4: max(above 10, 9 + best[4][0]=0)  = 10
p=4           0   1   5   8  10    ← answer best[4][4] = 10
```

Full table:

```
          r:  0   1   2   3   4
p=1           0   1   2   3   4
p=2           0   1   5   6  10
p=3           0   1   5   8  10
p=4           0   1   5   8  10
```

### Reconstructing the cuts from the table

Start at the answer cell `(p, r) = (4, 4)` and ask, for each cell, *"did this value come from skipping or from cutting?"*

- If `best[p][r] == best[p-1][r]`, skipping length `p` was enough → move **up** (`p--`).
- Otherwise the value came from cutting a piece of length `p` → record `p`, move **left** by `p` (`r -= p`) and **stay on row p** (more copies are allowed).
- On row 1 every remaining metre is a unit piece.

```
(4,4) = 10, above (3,4) = 10 → equal → up
(3,4) = 10, above (2,4) = 10 → equal → up
(2,4) = 10, above (1,4) = 4  → differ → CUT 2,  r = 2
(2,2) = 5,  above (1,2) = 2  → differ → CUT 2,  r = 0
r = 0 → stop.        cuts = [2, 2], revenue 5 + 5 = 10 ✔
```

```java
int p = n, r = n;
while (r > 0) {
    if (p > 1 && best[p][r] == best[p - 1][r]) p--;
    else { pieces.add(p); r -= p; }
}
```

Full file: [solution/Tabulation.java](solution/Tabulation.java). Its tests check that the cuts add up to `n` and that their prices add up to the optimum.

## 8. Space optimization

### 8.1 Rolling the 2D table

Exactly as in [Coin Change II](../09-coin-change-ii/): row `p` uses row `p-1` above and row `p` to the left, so a single array updated left to right works:

```java
for (int p = 1; p <= n; p++)
    for (int r = p; r <= n; r++)
        best[r] = Math.max(best[r], prices[p - 1] + best[r - p]);
```

The catch: once rows are overwritten, the "came from above or from the left?" test is no longer possible, so the cuts are lost.

### 8.2 The "first cut" formulation (keeps the cuts with O(n) memory)

Think differently: every non-empty plan has a **first piece** of some length `c`. After selling it we hold a rod of length `r − c`, which we should cut optimally. So:

```
best[0] = 0
best[r] = max over c = 1..r of ( prices[c-1] + best[r - c] )
firstCut[r] = the c that achieved the max
```

`best[r - c]` has a smaller index, so filling `r = 1, 2, …, n` in order works. And `firstCut` alone is enough to rebuild the plan: cut `firstCut[n]`, then `firstCut[n - firstCut[n]]`, and so on.

```java
for (int r = 1; r <= n; r++) {
    best[r] = Integer.MIN_VALUE;
    for (int c = 1; c <= r; c++) {
        int candidate = prices[c - 1] + best[r - c];
        if (candidate > best[r]) { best[r] = candidate; firstCut[r] = c; }
    }
}
for (int r = n; r > 0; r -= firstCut[r]) pieces.add(firstCut[r]);
```

Full file: [solution/Optimized.java](solution/Optimized.java).

On the running example:

| r | candidates `prices[c-1] + best[r-c]` for c = 1, 2, 3, 4 | best[r] | firstCut[r] |
|---|---|---|---|
| 0 | — | 0 | — |
| 1 | 1+0 | 1 | 1 |
| 2 | 1+1=2, **5+0=5** | 5 | 2 |
| 3 | 1+5=6, 5+1=6, **8+0=8** | 8 | 3 |
| 4 | 1+8=9, **5+5=10**, 8+1=9, 9+0=9 | 10 | 2 |

Reconstruct: `r=4 → cut 2 → r=2 → cut 2 → r=0`. Cuts `[2, 2]` ✔.

**Why are these two formulations equal?** Both search all multisets of piece lengths that sum to `r`. The 2D form decides "how many of each length, longest first"; the 1D form decides "which piece comes first" and recurses. The 1D form explores some plans in several orders (`[1, 3]` and `[3, 1]`), but for a **max** that is harmless — duplicates can't change a maximum. (For a *count* they would matter: that is the permutation trap of [Coin Change II](../09-coin-change-ii/).)

**Ties.** When two cuts give the same revenue, the strict `>` keeps the first (shortest) winning `c`. That is why `Optimized.java` prints `[2, 6]` for the n = 8 example while `Tabulation.java`, which walks from the longest length down, prints `[6, 2]`. Both are optimal.

## 9. Dry run on a full example (CLRS, n = 8)

prices = `[1, 5, 8, 9, 10, 17, 17, 20]`.

2D table `best[p][r]` (rows = longest allowed piece):

```
          r:  0   1   2   3   4   5   6   7   8
p=1           0   1   2   3   4   5   6   7   8
p=2           0   1   5   6  10  11  15  16  20
p=3           0   1   5   8  10  13  16  18  21
p=4           0   1   5   8  10  13  16  18  21
p=5           0   1   5   8  10  13  16  18  21
p=6           0   1   5   8  10  13  17  18  22
p=7           0   1   5   8  10  13  17  18  22
p=8           0   1   5   8  10  13  17  18  22   ← 22
```

A few cells explained:

- `best[2][8] = max(8, 5 + best[2][6]=15) = 20` — four 2s.
- `best[3][8] = max(20, 8 + best[3][5]=13) = 21` — a 3 plus the best for 5 using ≤3 (`3 + 2`, ₹13) → `3 + 3 + 2`.
- `best[6][6] = max(16, 17 + 0) = 17` — selling a whole 6 beats `3 + 3` (16).
- `best[6][8] = max(21, 17 + best[6][2]=5) = 22` — a 6 plus a 2.

Walk back from `(8, 8)`: equal to above until row 6; `(6,8)=22 ≠ (5,8)=21` → cut 6, r = 2; then up to row 2: `(2,2)=5 ≠ (1,2)=2` → cut 2. **Cuts `[6, 2]`, revenue 17 + 5 = 22.**

1D first-cut arrays for the same prices:

| r | 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 |
|---|---|---|---|---|---|---|---|---|---|
| best[r] | 0 | 1 | 5 | 8 | 10 | 13 | 17 | 18 | 22 |
| firstCut[r] | – | 1 | 2 | 3 | 2 | 2 | 6 | 1 | 2 |

Note that `best[r]` equals the **last row** of the 2D table — the 1D array is that row. Reconstruct: `8 → cut 2 → 6 → cut 6 → 0`, giving `[2, 6]`.

## 10. Complexity analysis

| Approach | Time | Space | Why |
|---|---|---|---|
| Brute force | O(2ⁿ) roughly | O(n) | Two branches per call; depth ≤ 2n. |
| Memoization | O(n²) | O(n²) | `n·(n+1)` states, O(1) work each, plus O(n) stack. |
| Tabulation (2D) | O(n²) | O(n²) | `n·(n+1)` cells; reconstruction walks at most `2n` steps. |
| Rolled 1D (knapsack) | O(n²) | O(n) | Same updates, one array — but no reconstruction. |
| First-cut 1D | O(n²) | O(n) | For each `r` we try `r` first cuts: `1 + 2 + … + n = n(n+1)/2`. Reconstruction is O(n). |

## 11. Why this approach works

**Optimal substructure.** Take any optimal plan for a rod of length `r` and remove its first piece `c`. What remains is a plan for length `r − c`. If it were *not* optimal for `r − c`, we could swap in a better plan for `r − c`, keep the piece `c`, and beat the "optimal" plan for `r` — a contradiction. So the best plan for `r` is always "some first piece + the best plan for the rest", and trying every first piece finds it.

The 2D version's argument is the [unbounded knapsack](../07-unbounded-knapsack/) one: every plan either contains no piece of length `p` (then it is a plan using `1..p-1`) or contains at least one (remove one, and the rest is a plan for `r − p` still using `1..p`). Both cases are covered, and the `max` picks the better.

## 12. Common mistakes

1. **Off-by-one in prices.** `prices[k]` is the price of length `k + 1`. Writing `prices[p]` for length `p` either skips the first price or reads past the end.
2. **Moving to `p − 1` after cutting a piece of length `p`.** That allows each length only once (0/1 knapsack). On `[1,5,8,9]`, n = 4 you would get 9 instead of 10, because `2 + 2` uses length 2 twice.
3. **Initialising `best[r] = 0` in the first-cut loop and forgetting that some price might be 0 or negative.** With non-negative prices 0 is fine, but `Integer.MIN_VALUE` (as in our code) is the safe "nothing chosen yet" value.
4. **Reconstructing from the rolled 1D knapsack array.** After rolling, the table that told you "skip or cut" is gone. Keep the 2D table or use the first-cut array with `firstCut`.
5. **Thinking every rod-cutting-sounding problem is knapsack.** If the cut **positions** are fixed and the cost depends on the current segment ([Minimum Cost to Cut a Stick](https://leetcode.com/problems/minimum-cost-to-cut-a-stick/)), it is interval DP.

## 13. When to use this technique / how to recognize similar problems

Look for these signals:

- A total size (length, amount, number) that must be **split into parts**.
- Each part size has its own **value**, and part sizes **can repeat**.
- You want the **max/min** total value (or the plan that achieves it).

Then it is unbounded knapsack with "remaining size" as the capacity, and the "first part" recurrence `best[r] = max_c (value(c) + best[r − c])` is often the quickest to write. [Integer Break](https://leetcode.com/problems/integer-break/) uses it with a product; [Perfect Squares](https://leetcode.com/problems/perfect-squares/) with a count and `min`.

## 14. Summary and revision notes

- Rod cutting = unbounded knapsack: piece length = weight, price = value, rod length = capacity.
- 2D: `best[p][r] = max(best[p-1][r], prices[p-1] + best[p][r-p])`, base row `best[1][r] = r·prices[0]`.
- Reconstruct by walking back: equal to the cell above → skip; otherwise cut `p` and stay on the row.
- 1D first-cut form: `best[r] = max_c (prices[c-1] + best[r-c])`, plus `firstCut[r]` for the plan.
- Duplicated orders are harmless for `max`, fatal for counting.

```
┌──────────────────────────────────────────────────────────────────┐
│ best[p][r] = max( best[p-1][r],  prices[p-1] + best[p][r-p] )     │
│ best[1][r] = r * prices[0]                                       │
│                                                                  │
│ best[r] = max over c in 1..r of ( prices[c-1] + best[r-c] )      │
│ best[0] = 0,   cuts: r → r - firstCut[r]                         │
└──────────────────────────────────────────────────────────────────┘
```

## 15. Exercises

**Exercise 1.** prices = `[2, 5, 7, 8, 9]`, n = 5. Fill the first-cut arrays `best[0..5]` and `firstCut[1..5]` (strict `>`, so the smallest winning `c` is kept). What is the answer and the cut list?

<details>
<summary>Answer</summary>

`best = [0, 2, 5, 7, 10, 12]`. r=1: 2 (c=1). r=2: max(2+2, 5) = 5 (c=2). r=3: max(2+5, 5+2, 7) = 7 (c=1, the first to reach 7). r=4: max(2+7, 5+5, 7+2, 8) = 10 (c=2). r=5: max(2+10, 5+7, 7+5, 8+2, 9) = 12 (c=1). `firstCut[1..5] = [1, 2, 1, 2, 1]`. Reconstruction: r=5 cut 1 → r=4 cut 2 → r=2 cut 2 → r=0. Cuts `[1, 2, 2]`, revenue 2 + 5 + 5 = 12.
</details>

**Exercise 2.** Modify the problem: every cut costs a fixed fee `F` (selling the rod whole costs nothing). Which formulation adapts most easily, and how?

<details>
<summary>Answer</summary>

The first-cut form. Let "no cut" be a special option: `best[r] = max(prices[r-1], max over c in 1..r-1 of (prices[c-1] + best[r-c] - F))`. Each time we split off a first piece (and something remains) we perform exactly one cut, so we subtract `F` once per split.
</details>

**Exercise 3.** [Integer Break](https://leetcode.com/problems/integer-break/) asks to split `n ≥ 2` into at least two positive integers and maximise their product. Write the first-cut recurrence. Why can't you use `best[r - c]` directly for the remaining part?

<details>
<summary>Answer</summary>

`best[r] = max over c in 1..r-1 of c × max(r − c, best[r − c])`. `best[r − c]` is defined as "split into at least two parts", but for the remainder we may also keep it whole (`r − c` itself). For example `best[3] = 2` (`1 × 2`), yet in `6 = 3 + 3` we want to keep each 3 whole: `3 × 3 = 9`. So we take the larger of "keep it whole" and "split it further".
</details>
