# Convex Hull Trick — Walkthrough

Running example for the whole lesson (the third official sample of Frog 3):

```
heights h = [1, 3, 4, 5, 10, 11, 12, 13]     (stones 0..7)
C = 5                                        answer = 62
```

## 1. Intuition

You are at a market with many sellers. Seller `j` charges a fixed fee `b_j` plus `m_j` per kilogram. Every customer `i` buys a different weight `x_i` and wants the cheapest seller. For one customer you can check every seller. For a hundred thousand customers that is too slow, so you think about the sellers once:

- A seller with a big fixed fee but a low price per kilogram is great for **big** orders.
- A seller with a small fee but a high price per kilogram is great for **small** orders.
- Some sellers are **never** the cheapest for any weight — someone else always beats them. Ignore them forever.

If customers arrive in order of increasing weight, the "currently cheapest" seller only moves from small-order sellers towards big-order sellers. Each seller becomes the best once, stays best for a while, then is beaten forever.

Each seller is a **line** `cost = m_j·x + b_j`. "Cheapest seller for weight `x`" is "lowest line at `x`". The Convex Hull Trick is this market analysis applied to a DP whose candidates turn out to be lines.

## 2. Problem statement in our own words

[Frog 3 (AtCoder DP Contest, Task Z)](https://atcoder.jp/contests/dp/tasks/dp_z): there are `N` stones with **strictly increasing** heights `h_1 < h_2 < … < h_N`. A frog starts on stone 1 and wants to reach stone `N`. From stone `j` it may jump to **any** later stone `i`, paying `(h_i - h_j)² + C`. Find the minimum total cost. Limits: `N ≤ 2·10⁵`, `h_i ≤ 10⁶`, `C ≤ 10¹²`.

(We index stones from 0 in the code and in this lesson.)

## 3. Examples

| Heights | C | Best route | Cost |
|---------|---|------------|------|
| `[1, 2, 3, 4, 5]` | 6 | 1 → 3 → 5: `(4+6) + (4+6)` | **20** |
| `[500000, 1000000]` | 10¹² | one jump: `500000² + 10¹²` | **1 250 000 000 000** |
| `[1, 3, 4, 5, 10, 11, 12, 13]` | 5 | 1 → 3 → 5 → 10 → 13 | **62** |
| `[7]` | 100 | edge case: already there | **0** |

Running example route check: `(3-1)²+5 = 9`, `(5-3)²+5 = 9`, `(10-5)²+5 = 30`, `(13-10)²+5 = 14`; total `9 + 9 + 30 + 14 = 62`.

The trade-off: every jump pays `C`, so you want few jumps; but the cost is squared, so you want short jumps. Stone 1 (height 3) and stone 3 (height 5) split the cluster `1, 3, 4, 5`; stone 4 (height 10) breaks the big gap from 5 to 13.

## 4. Deriving the solution — the 7 questions

### 4.1 What is changing?

The stone the frog stands on, `i`.

### 4.2 What does the state represent?

`dp[i]` = **the minimum total cost for the frog to reach stone `i` starting from stone 0.**

### 4.3 What are the choices?

The last jump into stone `i` came from some earlier stone `j`, `0 ≤ j < i`.

### 4.4 What is the recurrence?

```
dp[i] = min over j < i of ( dp[j] + (h_i - h_j)² + C )
          ^ reach j       ^ the last jump
```

### 4.5 What are the base cases?

`dp[0] = 0`: the frog starts on stone 0.

### 4.6 What is the order of computation?

`dp[i]` needs all `dp[j]` with `j < i`, so compute `i = 1, 2, …, N-1` left to right.

### 4.7 Can the state space be optimized?

Space: no, every earlier `dp[j]` can be the best predecessor, so all must stay available (but we will compress each of them into a line). Time: yes — the rest of section 4 rewrites the transition so that it becomes "lowest line at a point", which removes the O(n) scan.

### 4.8 Prerequisite: lines and the lower envelope

A **line** is `y = m·x + b`. `m` (slope) says how fast `y` changes when `x` grows; `b` (intercept) is `y` at `x = 0`.

Two lines `(m₁, b₁)` and `(m₂, b₂)` with `m₁ ≠ m₂` meet where `m₁x + b₁ = m₂x + b₂`, i.e.

```
x(1,2) = (b₂ - b₁) / (m₁ - m₂)
```

Take three lines and evaluate them at a few points:

```
A: y = -1x + 6       B: y = -2x + 9       C: y = -3x + 15

 x :   0    1    2    3    4    5    6    7    8
 A :   6    5    4    3    2    1    0   -1   -2
 B :   9    7    5    3    1   -1   -3   -5   -7
 C :  15   12    9    6    3    0   -3   -6   -9
min:   A    A    A   A/B   B    B   B/C   C    C
```

The **lower envelope** is the function `min(A, B, C)`. Sketch (not to scale):

```
 y
15 |C
   | \
 9 |B \
   | \ \
 6 |A \ \
   |  `-.\_\            A is lowest for x <= 3
 3 |      `*.\          B is lowest for 3 <= x <= 6
   |         `*.        C is lowest for x >= 6
 0 |------------*-.------------------> x
   |            6  `-.  C
   |                   `-._ B
   |                        `- A  (above the others here)
```

Two facts to notice:

1. **For a minimum, as `x` grows the winner moves to lines with smaller (more negative) slope.** A steeper downward line eventually goes below everything else. So if we list the lines by decreasing slope (`A, B, C`), the envelope uses them in exactly that order from left to right.
2. **Some lines are useless.** Replace `C` by `C': y = -3x + 11`:

```
 x :   0    1    2    3    4
 A :   6    5    4    3    2
 B :   9    7    5    3    1
 C':  11    8    5    2   -1
min:   A    A    A    C'   C'
```

`B` is never the strict minimum: before `x = 3` line `A` is lower, from about `x = 2` on line `C'` is lower than `B`. Test with intersections: `C'` overtakes `A` at `x(A,C') = (11-6)/(-1+3) = 2.5`, which is **before** `B` overtakes `A` at `x(A,B) = (9-6)/(-1+2) = 3`. When the next line catches the first one no later than the middle line does, the middle line never gets a turn. That is the **useless middle line test**:

```
with slopes m₁ > m₂ > m₃:   line 2 is useless  ⇔  x(1,3) ≤ x(1,2)
```

### 4.9 The key property: rewriting Frog 3 as lines (step by step)

Start from the transition and expand the square (`(a - b)² = a² - 2ab + b²`):

```
dp[i] = min_j [ dp[j] + (h_i - h_j)² + C ]
      = min_j [ dp[j] + h_i² - 2·h_i·h_j + h_j² + C ]
```

Split the terms by what they depend on:

```
 term          depends on
 dp[j]         j only
 h_j²          j only
 -2·h_i·h_j    both  (a product of an i-thing and a j-thing)
 h_i²          i only
 C             nothing
```

Terms that depend only on `i` (or nothing) are the same for every `j`, so they can leave the `min`:

```
dp[i] = h_i² + C + min_j [ (-2·h_j) · h_i  +  (dp[j] + h_j²) ]
                            \______/   \_/     \___________/
                            slope m_j   x        intercept b_j
```

So each earlier stone `j` becomes the line

```
m_j = -2·h_j          b_j = dp[j] + h_j²          query point x = h_i
```

and `dp[i] = h_i² + C + (lowest line at x = h_i)`.

Numeric check on the running example, `i = 4` (`h = 10`) and `j = 3` (`h = 5`, `dp[3] = 18`):

```
direct:      dp[3] + (10 - 5)² + 5     = 18 + 25 + 5  = 48
line form:   m = -10, b = 18 + 25 = 43
             line at x = 10: -100 + 43 = -57
             -57 + 10² + 5             = 48            ✔ same
```

All lines of the running example (each is created once `dp[j]` is known):

```
 j  :   0    1    2    3    4    5    6    7
 h_j:   1    3    4    5   10   11   12   13
dp_j:   0    9   14   18   48   54   57   62
 m_j:  -2   -6   -8  -10  -20  -22  -24  -26
 b_j:   1   18   30   43  148  175  201  231
```

### 4.10 Why a deque is enough here

Because heights strictly increase:

- **Slopes are added in decreasing order** (`m_j = -2h_j` gets more negative as `j` grows). New lines go to the **back** of the deque, the side that wins for large `x`.
- **Queries arrive in increasing order** (`x = h_i` grows). The best line therefore moves from the **front** towards the back and never returns.

Two pop rules follow:

1. **Pop front.** If the second line is at least as low as the first at the current `x`, it stays at least as low for every larger `x` (it has the smaller slope). The first line will never be the answer again: remove it.
2. **Pop back.** Before pushing a new line, check the last two lines in the deque together with the new one. If the middle one is useless (section 4.8 test), remove it and repeat.

The intersection test without division (both denominators `m₁ - m₂` and `m₁ - m₃` are positive, so cross-multiplying keeps the direction):

```
x(1,3) ≤ x(1,2)
⇔ (b₃ - b₁) / (m₁ - m₃) ≤ (b₂ - b₁) / (m₁ - m₂)
⇔ (b₃ - b₁)·(m₁ - m₂) ≤ (b₂ - b₁)·(m₁ - m₃)
```

**Overflow check.** `dp[j]` is at most the cost of jumping directly from stone 0 to `j`, so `dp[j] ≤ 10¹² + 10¹²`, and `b_j = dp[j] + h_j² ≤ 3·10¹²`. Slope differences are at most `2·10⁶`. The products are below `6·10¹⁸`, and `Long.MAX_VALUE ≈ 9.22·10¹⁸`, so plain `long` arithmetic is safe. (In other problems where this bound fails, compare the intersections with `double`, or use `Math.multiplyHigh`, or switch to a Li Chao tree, which never multiplies two big numbers together.)

## 5. Brute force: recursion

```java
static long reach(int i) {                  // cheapest cost to reach stone i
    if (i == 0) return 0;
    long best = Long.MAX_VALUE;
    for (int j = 0; j < i; j++)
        best = Math.min(best, reach(j) + (h[i]-h[j])*(h[i]-h[j]) + C);
    return best;
}
```

Recursion tree for `reach(3)`:

```
                    reach(3)
           /           |          \
      reach(0)     reach(1)      reach(2)
                      |          /      \
                   reach(0)  reach(0)  reach(1)
                                          |
                                       reach(0)
```

`reach(i)` calls every smaller index, so the number of calls doubles with each new stone: `T(i) = 1 + T(0) + … + T(i-1) = 2^i`. For 8 stones that is already 128 calls; for 2·10⁵ it is hopeless.

## 6. Memoization (top-down)

Caching `reach(i)` in `memo[i]` makes each of the `n` states compute once, each scanning `O(n)` predecessors: O(n²). In the tree above, the second `reach(1)` becomes a cache hit returning `9`. It is the same work as the tabulation below, so no separate file; we go straight to bottom-up because CHT needs states in increasing order anyway.

## 7. Tabulation (bottom-up) — the slow O(n²) DP

From [`solution/Slow.java`](solution/Slow.java):

```java
long[] dp = new long[n];
dp[0] = 0;
for (int i = 1; i < n; i++) {
    dp[i] = Long.MAX_VALUE;
    for (int j = 0; j < i; j++) {
        long diff = heights[i] - heights[j];
        long candidate = dp[j] + diff * diff + jumpCost;
        if (candidate < dp[i]) dp[i] = candidate;
    }
}
```

All candidates `dp[j] + (h_i - h_j)² + 5` for the running example (`*` = minimum):

```
i=1 (h=3) : j0: 9*
i=2 (h=4) : j0: 14*  j1: 15
i=3 (h=5) : j0: 21   j1: 18*  j2: 20
i=4 (h=10): j0: 86   j1: 63   j2: 55   j3: 48*
i=5 (h=11): j0: 105  j1: 78   j2: 68   j3: 59   j4: 54*
i=6 (h=12): j0: 126  j1: 95   j2: 83   j3: 72   j4: 57*  j5: 60
i=7 (h=13): j0: 149  j1: 114  j2: 100  j3: 87   j4: 62*  j5: 63  j6: 63

dp = [0, 9, 14, 18, 48, 54, 57, 62]
```

Notice how many candidates are hopeless (`j0` at `i = 7` costs 149). The slow DP evaluates all `1 + 2 + … + 7 = 28` of them.

## 8. Optimization: convex hull trick with a monotone deque

From [`solution/Optimized.java`](solution/Optimized.java). The deque is stored in two arrays, `slope[head..tail-1]` and `intercept[head..tail-1]`.

```java
slope[tail] = -2 * heights[0];                       // line for stone 0
intercept[tail] = dp[0] + heights[0] * heights[0];
tail++;

for (int i = 1; i < n; i++) {
    long x = heights[i];
    // 1) pop front: queries only move right, a beaten front line stays beaten
    while (tail - head >= 2 && valueAt(head + 1, x) <= valueAt(head, x)) head++;
    dp[i] = valueAt(head, x) + x * x + jumpCost;

    // 2) push the new line, first dropping useless lines at the back
    long newSlope = -2 * heights[i];
    long newIntercept = dp[i] + heights[i] * heights[i];
    slope[tail] = newSlope;
    intercept[tail] = newIntercept;
    while (tail - head >= 2 && isUseless(tail - 2, tail - 1, tail)) {
        slope[tail - 1] = newSlope;                 // overwrite the middle line
        intercept[tail - 1] = newIntercept;
        tail--;
    }
    tail++;
}

private static boolean isUseless(int first, int middle, int last) {
    long left  = (intercept[last]   - intercept[first]) * (slope[first] - slope[middle]);
    long right = (intercept[middle] - intercept[first]) * (slope[first] - slope[last]);
    return left <= right;                           // x(first,last) <= x(first,middle)
}
```

`main` checks the official samples, an extreme-magnitude input, and 500 random strictly increasing inputs (`new Random(26)`) against a private copy of the slow DP.

### 8.1 The general case: Li Chao tree

The deque needs sorted slopes **and** sorted queries. If the heights were in any order (a harder variant where the frog may still jump to any later stone), both break. A **Li Chao tree** handles any order:

- It is a segment tree over the range of possible `x` values. Every node stores **one** line: the one that is lowest at the node's midpoint.
- **Insert** a new line at a node: compare it with the stored one at the midpoint; keep the winner in the node. Two lines cross at most once, so the loser can only still win on **one** side (left or right half). Push the loser into that half only. One node per level → O(log C).
- **Query** `x`: walk from the root to the leaf containing `x` and take the minimum of the lines stored on the path. O(log C).

From [`solution/LiChaoTree.java`](solution/LiChaoTree.java):

```java
long mid = (left + right) >> 1;
boolean newWinsLeft = slope * left + intercept < nodeSlope[node] * left + nodeIntercept[node];
boolean newWinsMid  = slope * mid  + intercept < nodeSlope[node] * mid  + nodeIntercept[node];
if (newWinsMid) { /* swap: the new line stays here, the old one travels down */ }
if (left == right) return;
if (newWinsLeft != newWinsMid) insert(2 * node, left, mid, slope, intercept);   // crossing is in the left half
else                           insert(2 * node + 1, mid + 1, right, slope, intercept);
```

It is cross-checked against the slow DP on 500 random **unsorted** height arrays, e.g. `h = [5, 1, 4], C = 2`: `dp = [0, 18, 3]` (jumping 5 → 4 directly costs `1 + 2 = 3`). Use Li Chao when the problem gives no ordering guarantees (CSES Monster Game II), and the deque when it does (Frog 3, Monster Game I). The deque is O(n) and has a tiny constant; Li Chao is O(n log C) but much harder to get wrong.

## 9. Dry run on a full example

Lines are written `Lj = (m_j, b_j)`. Values are `m·x + b` at the current query `x`.

| i | x = h_i | front check (pop front?) | best line, value | dp[i] = value + x² + 5 | new line | back check (pop back?) | deque after |
|---|---------|--------------------------|------------------|------------------------|----------|------------------------|-------------|
| 0 | — | — | — | 0 | L0 = (-2, 1) | — | L0 |
| 1 | 3 | one line only | L0: -6+1 = **-5** | -5+9+5 = **9** | L1 = (-6, 18) | only 2 lines | L0 L1 |
| 2 | 4 | L0: -7, L1: -6 → keep L0 | L0: **-7** | -7+16+5 = **14** | L2 = (-8, 30) | x(0,2)=29/6≈4.83 > x(0,1)=17/4=4.25 → keep L1 | L0 L1 L2 |
| 3 | 5 | L0: -9, L1: -12 → pop L0; L1: -12, L2: -10 → stop | L1: **-12** | -12+25+5 = **18** | L3 = (-10, 43) | x(1,3)=25/4=6.25 > x(1,2)=12/2=6 → keep L2 | L1 L2 L3 |
| 4 | 10 | L1: -42, L2: -50 → pop L1; L2: -50, L3: -57 → pop L2 | L3: **-57** | -57+100+5 = **48** | L4 = (-20, 148) | only 2 lines | L3 L4 |
| 5 | 11 | L3: -67, L4: -72 → pop L3 | L4: **-72** | -72+121+5 = **54** | L5 = (-22, 175) | only 2 lines | L4 L5 |
| 6 | 12 | L4: -92, L5: -89 → keep L4 | L4: **-92** | -92+144+5 = **57** | L6 = (-24, 201) | x(4,6)=53/4=13.25 ≤ x(4,5)=27/2=13.5 → **pop L5** | L4 L6 |
| 7 | 13 | L4: -112, L6: -111 → keep L4 | L4: **-112** | -112+169+5 = **62** | L7 = (-26, 231) | x(4,7)=83/6≈13.83 > 13.25 → keep L6 | L4 L6 L7 |

`dp = [0, 9, 14, 18, 48, 54, 57, 62]` — identical to the slow table, answer **62**.

Look at row 6: line L5 (stone 5, height 11) is thrown away for good. It is never the best predecessor for any later stone because L6 overtakes L4 (at `x = 13.25`) before L5 would (at `x = 13.5`). Indeed in the slow table, `j5` never wins after `i = 6`.

Lines evaluated: 1 + 2 + 3 + 3 + 2 + 2 + 2 = 15 evaluations in total over all queries, and each pop is paid for once; versus 28 candidate evaluations in the slow DP. For `n = 2·10⁵` that is about `10⁶` versus `2·10¹⁰`.

## 10. Complexity analysis

| Approach | Time | Space | Why |
|----------|------|-------|-----|
| Brute force | O(2ⁿ) | O(n) stack | Each `reach(i)` calls all smaller indices. |
| Memoization / slow tabulation | O(n²) | O(n) | `n` states, each scans up to `n` predecessors. |
| Monotone deque CHT | O(n) | O(n) | Each line is pushed once and popped at most once (front or back); every while-loop iteration pays for one pop, so all loops together run O(n) times. |
| Li Chao tree | O(n log C) | O(C) (or O(n) with coordinate compression) | One root-to-leaf path per insert and per query; `C` = size of the `x` range. |

## 11. Why this approach works

- **The algebra is exact.** `dp[j] + (h_i - h_j)² + C` equals `m_j·h_i + b_j + h_i² + C` for every `j`, so the minimum over `j` is the minimum over lines plus a constant.
- **Front pops are safe.** If line `k+1` (smaller slope) is ≤ line `k` at `x`, then for every `x' > x` the gap only grows in favour of `k+1`, and all future queries are ≥ `x`.
- **Back pops are safe.** If `x(1,3) ≤ x(1,2)`, then for `x < x(1,2)` line 1 beats line 2, and for `x ≥ x(1,2) ≥ x(1,3)` line 3 beats-or-ties line 2. Line 2 is never strictly needed.
- So the deque always contains every line that could still be the unique lowest at some future query, in left-to-right envelope order, and the front is the lowest at the current query.

## 12. Common mistakes

1. **Wrong sign of the slope.** The product term is `-2·h_i·h_j`; the slope is `-2·h_j`, not `2·h_j`.
2. **Forgetting the `i`-only terms.** `dp[i]` is the envelope value **plus** `h_i² + C`.
3. **Using the deque without monotonicity.** If slopes or queries are unsorted, the deque returns wrong answers silently; use Li Chao (or binary search on the hull when only queries are unsorted).
4. **Dividing to compare intersections.** Integer division rounds; floating point can mis-compare equal values. Cross-multiply, after checking overflow.
5. **Overflow in the cross products.** Bound `|b|` and `|m|` first (section 4.10). Here `< 6·10¹⁸` is safe; in other problems it may not be.
6. **Equal slopes.** With strictly increasing heights they cannot occur. In problems where they can, keep only the line with the smaller intercept before running the useless-line test (otherwise `m₁ - m₂ = 0`).
7. **Pop-front with `<` instead of `≤`.** Both are correct for ties, but `≤` keeps the deque shorter.

## 13. When to use this technique / how to recognize similar problems

Signals:

- A 1D DP `dp[i] = min/max over j < i of (…)` where the expression contains a **product of an `i`-quantity and a `j`-quantity** (often from expanding a square), and everything else depends on `i` only or `j` only.
- After rewriting, slope depends on `j`, query point depends on `i`.
- Limits like `n ≥ 10⁵` that rule out O(n²).

Choosing the variant:

| Slopes added in sorted order? | Queries in sorted order? | Use |
|------|------|------|
| yes | yes | monotone deque, O(n) |
| yes | no | keep the hull, binary search each query, O(n log n) |
| no | any | Li Chao tree, O(n log C) |

For a **maximum** instead of a minimum, negate everything (`max f = -min(-f)`) or flip the comparisons. For partition DPs with a `k` dimension, CHT can be applied per layer (compare with [divide and conquer optimization](../05-divide-and-conquer-optimization/)).

## 14. Summary and revision notes

```
+---------------------------------------------------------------+
| dp[i] = h_i² + C + min_j ( m_j · x + b_j )   at x = h_i        |
|         m_j = -2·h_j      b_j = dp[j] + h_j²                    |
| Slopes decreasing + queries increasing  ⇒  monotone deque:     |
|   pop front while next line ≤ front at x                       |
|   pop back while (b3-b1)(m1-m2) ≤ (b2-b1)(m1-m3)               |
| O(n) total; Li Chao tree O(n log C) when no order is given.    |
+---------------------------------------------------------------+
```

- Expand the cost, separate `i`-only, `j`-only and product terms.
- Each previous state is a line; each new state is a query.
- Lower envelope = lowest line for each `x`; useless lines never return.
- Every line enters and leaves the deque once → amortized O(1).
- Check overflow before cross-multiplying.
- Monster Game I fits the deque; Monster Game II needs the general version.

## 15. Exercises

**Exercise 1.** Lines `P: y = -x + 10`, `Q: y = -2x + 13`, `R: y = -4x + 20`, added in this order. Is `Q` useless?

<details><summary>Answer</summary>

`x(P,Q) = (13-10)/(-1+2) = 3`, `x(P,R) = (20-10)/(-1+4) = 10/3 ≈ 3.33`. Since `3.33 > 3`, `R` overtakes `P` after `Q` does, so `Q` is the lowest on `[3, 3.5]` (where `x(Q,R) = (20-13)/(-2+4) = 3.5`). Check at `x = 3.25`: P = 6.75, Q = 6.5, R = 7. `Q` is **useful**. Cross-multiplied: `(20-10)·(-1+2) = 10` vs `(13-10)·(-1+4) = 9`; `10 ≤ 9` is false → keep.
</details>

**Exercise 2.** CSES Monster Game I has a DP of the form `dp[i] = min_j dp[j] + s_i · f_j`. Identify slope, intercept and query point.

<details><summary>Answer</summary>

It is already a line: slope `m_j = f_j`, intercept `b_j = dp[j]`, query `x = s_i`, and there is no extra `i`-only term. No algebra needed — the only question is whether the slopes and queries come in sorted order (in Game I the input guarantees it; in Game II not, so use a Li Chao tree).
</details>

**Exercise 3.** Rewrite the Subarray Squares layer transition `dp[g][i] = min_j dp[g-1][j] + (P_i - P_j)²` (with `P` the prefix sums) in line form. Are slopes and queries monotone?

<details><summary>Answer</summary>

`(P_i - P_j)² = P_i² - 2P_iP_j + P_j²`, so `dp[g][i] = P_i² + min_j [ (-2P_j)·P_i + (dp[g-1][j] + P_j²) ]`. Slope `-2P_j`, intercept `dp[g-1][j] + P_j²`, query `x = P_i`. The values are positive, so `P` is strictly increasing: slopes strictly decrease and queries strictly increase. The monotone deque gives O(n) per layer, O(k·n) total — better than D&C's O(k·n log n).
</details>

**Exercise 4.** In the dry run, why can line L5 be deleted at `i = 6` even though stone 5 was the *second-best* predecessor of stone 6 (cost 60)?

<details><summary>Answer</summary>

Deleting a line only claims that it will never be the **best** for any **future** query `x ≥ 12`. Before `x = 13.25` line L4 is lower than L5, and from `x = 13.25` on line L6 is lower than L5 (L5 would only beat L4 from `13.5`). So for every future stone some other line is at least as good. Being second-best now is irrelevant.
</details>
