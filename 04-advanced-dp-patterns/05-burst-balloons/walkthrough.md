# Burst Balloons — Walkthrough

---

## 1. Intuition

### The game

Balloons stand in a row, each with a number. You burst them one at a time, in any order. When you burst a balloon you collect

```
(left neighbour) × (the balloon) × (right neighbour)
```

using the neighbours **that are still there at that moment**. A missing neighbour (beyond the ends) counts as `1`. After a burst, the balloons on both sides close the gap and become neighbours.

```
   [3] [1] [5] [8]        burst 1:  3 × 1 × 5 = 15
   [3]     [5] [8]        now 3 and 5 are neighbours
```

### Why "which balloon first?" does not work

A natural interval DP would say: "in the range `i..j`, try every balloon `k` as the **first** to burst, then solve the left part and the right part". Let us see what goes wrong on `[3, 1, 5, 8]`.

Burst `1` first. We collect 15, and we are left with a left part `[3]` and a right part `[5, 8]`:

```
   left part: [3]        right part: [5] [8]
```

Can we now solve `[3]` and `[5, 8]` separately? No. When we later burst `3`, its right neighbour is **whatever is left of the right part at that time**: `5` if we burst 3 before 5, or `8` if 5 is already gone, or `1` (the end) if both are gone. The left part's score depends on the order chosen in the right part, and vice versa. The two "subproblems" are **not independent**, so "best of left + best of right" is meaningless.

```
   burst 3 first:            1 × 3 × 5 = 15     (5 still there)
   burst 3 after 5:          1 × 3 × 8 = 24     (8 is now its neighbour)
   burst 3 after 5 and 8:    1 × 3 × 1 = 3
   -> the value of "solving [3]" is not a fixed number
```

### Why "which balloon last?" works

Now try the opposite question: in the range, which balloon `k` is burst **last**?

While all the other balloons of the range are being burst, `k` is still standing. It acts as a **wall**:

```
   i  [ ...left side... ]  k  [ ...right side... ]  j
                           ^
               still standing until the very end
```

- A balloon on the left side can never have a neighbour from the right side — `k` is always in between. So the left side's bursts only ever see neighbours from the left side, `i`, or `k`.
- The same holds for the right side, with `k` and `j`.

So the left side and the right side **are** independent: each is a smaller problem of the same kind, with fixed boundary balloons on both ends. And when `k` is finally burst, everything between `i` and `j` is gone, so its neighbours are exactly the boundaries `i` and `j`:

```
   coins for k = a[i] × a[k] × a[j]
```

### Padding with 1s

The "missing neighbour counts as 1" rule is awkward. Put a balloon of value `1` at each end:

```
   nums   =    [3, 1, 5, 8]
   padded = [1, 3, 1, 5, 8, 1]
   index     0  1  2  3  4  5
```

These two extra balloons are **never burst**. They are the outer walls. Now every real balloon always has two real neighbours, and the whole problem is "burst everything strictly between index 0 and index 5".

---

## 2. Problem statement in our own words

[LC 312](https://leetcode.com/problems/burst-balloons/): you get an array `nums` of balloon values (values may be 0). Bursting balloon `i` earns `nums[left] · nums[i] · nums[right]`, where `left` and `right` are its current neighbours (an out-of-range neighbour is 1). After bursting, the balloon disappears. Burst all balloons and return the maximum total coins.

---

## 3. Examples

**Main example (used in every section):** `nums = [3, 1, 5, 8]` → **167**.

One optimal order: burst `1`, then `5`, then `3`, then `8`:

```
   [3, 1, 5, 8]   burst 1:  3 × 1 × 5 =  15
   [3, 5, 8]      burst 5:  3 × 5 × 8 = 120
   [3, 8]         burst 3:  1 × 3 × 8 =  24
   [8]            burst 8:  1 × 8 × 1 =   8
                                  total 167
```

Other tests:

| `nums` | Answer | Why |
|---|---|---|
| `[1, 5]` | 10 | burst 1: 1·1·5 = 5, then 5: 1·5·1 = 5 |
| `[2, 4, 3]` | 33 | burst 4: 2·4·3 = 24, then 2: 1·2·3 = 6, then 3: 1·3·1 = 3 |
| `[5]` (edge) | 5 | 1·5·1 |

---

## 4. Deriving the solution — the 7 questions

Let `a` be the padded array of length `n + 2` (`a[0] = a[n+1] = 1`).

### Q1. What is changing?
Which stretch of balloons we are still bursting, described by its two **boundary** balloons `i` and `j` (which stay standing).

### Q2. What does the state represent?
> `dp[i][j]` = the maximum coins obtainable by bursting **all balloons strictly between `i` and `j`**, given that `a[i]` and `a[j]` are still standing the whole time.

This is an **open interval**: `i` and `j` are not burst in this subproblem. The answer is `dp[0][n+1]`.

### Q3. What are the choices?
Which balloon `k` (with `i < k < j`) is burst **last** among those between `i` and `j`.

### Q4. What is the recurrence?

```
dp[i][j] = max over k = i+1 .. j-1 of
           dp[i][k]                 // burst everything between i and k  (k is the right wall)
         + dp[k][j]                 // burst everything between k and j  (k is the left wall)
         + a[i] * a[k] * a[j]       // finally burst k; its neighbours are now i and j
```

This is a **shared** split (`k` is the boundary of both sides), exactly like [triangulation](../03-interval-dp/).

### Q5. What are the base cases?
`dp[i][i+1] = 0`: no balloon strictly between two adjacent positions.

### Q6. What is the order of computation?
`dp[i][j]` reads `dp[i][k]` and `dp[k][j]`, which have a smaller gap `j − i`. Fill by increasing gap: 2, 3, …, n + 1.

### Q7. Can the state space be optimized?
No simple reduction (same reason as all split-point interval DPs). O(n²) space.

---

## 5. Brute force: recursion

The honest brute force simulates every bursting order: try each remaining balloon as the next one, remove it, recurse.

```java
static int best(List<Integer> balloons) {
    if (balloons.isEmpty()) return 0;
    int most = 0;
    for (int idx = 0; idx < balloons.size(); idx++) {
        int left  = idx > 0 ? balloons.get(idx - 1) : 1;
        int right = idx + 1 < balloons.size() ? balloons.get(idx + 1) : 1;
        List<Integer> remaining = copy of balloons without position idx;
        most = Math.max(most, left * balloons.get(idx) * right + best(remaining));
    }
    return most;
}
```

Top of the tree for `[3, 1, 5, 8]`:

```
                                [3,1,5,8]
        ______________________/  |    |  \______________________
  burst 3: 3                burst 1: 15  burst 5: 40       burst 8: 40
     [1,5,8]                  [3,5,8]      [3,1,8]            [3,1,5]
     /  |  \                  /  |  \       ...                 ...
   ...                     [5,8] [3,8] [3,5]
```

This tries all `n!` orders (24 for 4 balloons, 3.6 million for 10). Can we memoize it? The state here is "which balloons remain" — an arbitrary **subset**, e.g. `{3, 8}` after bursting 1 and 5. There are `2ⁿ` subsets, still exponential. The "first balloon" idea cannot shrink this to ranges, because the remaining balloons are not a contiguous range of the original problem with fixed walls. The "last balloon" idea can.

Code: [BruteForce.java](solution/BruteForce.java) (tested only on tiny inputs).

### The "last balloon" recursion

```java
int best(int i, int j) {                      // open interval (i, j) of the padded array
    if (j - i < 2) return 0;
    int most = 0;
    for (int k = i + 1; k < j; k++)
        most = Math.max(most, best(i, k) + best(k, j) + a[i] * a[k] * a[j]);
    return most;
}
```

Without a cache this still repeats work (e.g. `best(1, 3)` is reached from `best(0, 3)` and from `best(1, 4)`), but now the states are only pairs `(i, j)`: O(n²) of them.

---

## 6. Memoization (top-down)

Cache `best(i, j)` in `memo[i][j]` (−1 = unknown):

```java
if (j - i < 2) return 0;
if (memo[i][j] != -1) return memo[i][j];
... loop over k ...
return memo[i][j] = most;
```

On the padded array `[1, 3, 1, 5, 8, 1]`, the call `best(0, 5)` asks for `best(0, k)` and `best(k, 5)` for `k = 1..4`. Ranges like `(1, 3)`, `(2, 4)` and `(1, 4)` are requested by several parents; after the first time, each is a cache hit. Exactly 10 non-trivial ranges exist (gap 2: 4 of them, gap 3: 3, gap 4: 2, gap 5: 1), and each is computed once.

Code: [Memoization.java](solution/Memoization.java).

---

## 7. Tabulation (bottom-up)

```java
int[][] dp = new int[n + 2][n + 2];                // zeros = base cases
for (int gap = 2; gap <= n + 1; gap++) {
    for (int i = 0; i + gap <= n + 1; i++) {
        int j = i + gap;
        for (int k = i + 1; k < j; k++)
            dp[i][j] = Math.max(dp[i][j], dp[i][k] + dp[k][j] + a[i] * a[k] * a[j]);
    }
}
return dp[0][n + 1];
```

Since all values are non-negative, starting `dp[i][j]` at 0 is safe for a maximum.

Fill order on the padded indices `0..5` (numbers = order of computation):

```
          j=0  j=1  j=2  j=3  j=4  j=5
   i=0     .    0    1    5    8   10   <- answer
   i=1          .    0    2    6    9
   i=2               .    0    3    7
   i=3                    .    0    4
   i=4                         .    0
   i=5                              .
   ('.' unused, 0 = base case: adjacent positions, nothing in between)
```

Code: [Tabulation.java](solution/Tabulation.java).

---

## 8. Space optimization

Skipped: each cell reads a row segment and a column segment, so the O(n²) table must be kept.

---

## 9. Dry run on a full example

Padded `a = [1, 3, 1, 5, 8, 1]` (indices 0–5).

### Gap 2 (exactly one balloon inside)

| cell | k | a[i]·a[k]·a[j] | dp |
|---|---|---|---|
| (0,2) | 1 | 1·3·1 | **3** |
| (1,3) | 2 | 3·1·5 | **15** |
| (2,4) | 3 | 1·5·8 | **40** |
| (3,5) | 4 | 5·8·1 | **40** |

### Gap 3 (two balloons inside)

| cell | k | dp[i][k] + dp[k][j] + a[i]·a[k]·a[j] | candidate |
|---|---|---|---|
| (0,3) | 1 | 0 + 15 + 1·3·5 | **30** |
| | 2 | 3 + 0 + 1·1·5 | 8 |
| (1,4) | 2 | 0 + 40 + 3·1·8 | 64 |
| | 3 | 15 + 0 + 3·5·8 | **135** |
| (2,5) | 3 | 0 + 40 + 1·5·1 | 45 |
| | 4 | 40 + 0 + 1·8·1 | **48** |

Read `dp[1][4] = 135`: with walls 3 (left) and 8 (right), burst `1` then `5`: 3·1·5 = 15, then 3·5·8 = 120.

### Gap 4

| cell | k | calculation | candidate |
|---|---|---|---|
| (0,4) | 1 | 0 + 135 + 1·3·8 | **159** |
| | 2 | 3 + 40 + 1·1·8 | 51 |
| | 3 | 30 + 0 + 1·5·8 | 70 |
| (1,5) | 2 | 0 + 48 + 3·1·1 | 51 |
| | 3 | 15 + 40 + 3·5·1 | 70 |
| | 4 | 135 + 0 + 3·8·1 | **159** |

### Gap 5 (the whole problem)

| k (last balloon) | dp[0][k] + dp[k][5] + 1·a[k]·1 | total |
|---|---|---|
| 1 (value 3) | 0 + 159 + 3 | 162 |
| 2 (value 1) | 3 + 48 + 1 | 52 |
| 3 (value 5) | 30 + 40 + 5 | 75 |
| 4 (value 8) | 159 + 0 + 8 | **167** |

### Final table

```
          j=0   j=1   j=2   j=3   j=4   j=5
   i=0     .     0     3    30   159   167
   i=1           .     0    15   135   159
   i=2                 .     0    40    48
   i=3                       .     0    40
   i=4                             .     0
```

### Recovering the bursting order

Follow the best `k` choices from the top, remembering they are the **last** bursts:

```
(0,5): last = index 4 (value 8)
  (0,4): last = index 1 (value 3)
    (0,1): empty
    (1,4): last = index 3 (value 5)
      (1,3): last = index 2 (value 1)
      (3,4): empty
  (4,5): empty
```

Inner decisions happen first, so the bursting order is: `1` (index 2), `5` (index 3), `3` (index 1), `8` (index 4) — exactly the order in section 3, total 15 + 120 + 24 + 8 = 167.

---

## 10. Complexity analysis

| Approach | Time | Space | Why |
|---|---|---|---|
| Simulate every order | O(n · n!) | O(n²) | n! orders, O(n) to copy the list at each step; n levels, each with a list copy |
| Memo on subsets (not shown) | O(n · 2ⁿ) | O(2ⁿ) | the "first balloon" state is a subset — still exponential |
| "Last balloon" memoization | O(n³) | O(n²) | about n²/2 open intervals, each tries up to n values of `k` |
| "Last balloon" tabulation | O(n³) | O(n²) | three nested loops: gap, i, k |

LC 312 allows `n ≤ 300`, so n³/6 ≈ 4.5 million steps — fast.

---

## 11. Why this approach works

Take any bursting order for the balloons strictly between `i` and `j` (with `i` and `j` standing). Let `k` be the last of them to burst. Before that moment, `k` stands between the left group and the right group, so no balloon on one side ever sees a neighbour on the other side: the left group's coins depend only on the left group, `a[i]` and `a[k]`; the right group's coins depend only on the right group, `a[k]` and `a[j]`. The two groups' orders can be interleaved in any way without changing any coin value. So the total for this order is (left group's total) + (right group's total) + `a[i]·a[k]·a[j]`, and to maximise it we maximise each group independently — which is exactly `dp[i][k]` and `dp[k][j]`. Trying all `k` covers every order. By induction on the gap, all smaller values are correct.

The same argument does **not** work for the first balloon, because after it bursts, the left and right groups become neighbours of each other.

---

## 12. Common mistakes

1. **Choosing the first balloon.** Leads to dependent subproblems and wrong answers. Choose the last.
2. **Closed interval confusion.** Here `dp[i][j]` excludes `i` and `j`. If you use a closed interval `[i, j]`, the boundary values become `a[i-1]` and `a[j+1]`; mixing the two conventions causes off-by-one bugs.
3. **Forgetting the padding.** Without the two `1`s, edge balloons need special cases.
4. **Using `dp[k+1][j]`.** The split is shared: `k` is the wall on both sides, so it is `dp[i][k] + dp[k][j]`.
5. **Multiplying by `a[k-1]` and `a[k+1]`.** Those are the original neighbours, not the neighbours at the time `k` is burst last. Use `a[i]` and `a[j]`.
6. **Filling row by row from the top.** Fill by gap.

---

## 13. When to use this technique / how to recognize similar problems

Use "choose the last action" when:
- elements are **removed** and the cost of a removal depends on its **current** neighbours;
- choosing the first removal leaves pieces that still interact.

Then the last element becomes a wall, and the state is an open interval between two walls. Pad the ends so the outermost walls exist.

Related:
- [LC 1547 Minimum Cost to Cut a Stick](https://leetcode.com/problems/minimum-cost-to-cut-a-stick/): add `0` and `n` to the sorted cut positions (the "padding"); `dp[i][j]` = min cost to make all cuts strictly between cut `i` and cut `j`; cost of the **first** cut there is `cuts[j] − cuts[i]` (for cutting, the first cut is the one whose cost is fixed by the walls).
- [LC 1039](https://leetcode.com/problems/minimum-score-triangulation-of-polygon/): identical recurrence with `min`.
- [LC 664 Strange Printer](https://leetcode.com/problems/strange-printer/) and [LC 546 Remove Boxes](https://leetcode.com/problems/remove-boxes/): harder relatives where you reason about what happens to one end element.

---

## 14. Summary and revision notes

- Bursting changes neighbours, so "first balloon" splits are not independent.
- The **last** balloon `k` in `(i, j)` is a wall: the sides are independent, and `k` pays `a[i]·a[k]·a[j]`.
- Pad with `1` on both ends; the padded ends are never burst.
- Open interval DP, shared split, fill by gap, answer `dp[0][n+1]`.
- O(n³) time, O(n²) space.

```
a = [1] + nums + [1]
dp[i][i+1] = 0
dp[i][j] = max over k in (i, j) of  dp[i][k] + dp[k][j] + a[i] * a[k] * a[j]
answer = dp[0][n+1]
```

---

## Exercises

**1.** Fill the table for `nums = [2, 4, 3]` (padded `[1, 2, 4, 3, 1]`) and confirm the answer 33.

<details><summary>Answer</summary>

Gap 2: `dp[0][2] = 1·2·4 = 8`, `dp[1][3] = 2·4·3 = 24`, `dp[2][4] = 4·3·1 = 12`.
Gap 3: `dp[0][3] = max(k=1: 0 + 24 + 1·2·3 = 30, k=2: 8 + 0 + 1·4·3 = 20) = 30`; `dp[1][4] = max(k=2: 0 + 12 + 2·4·1 = 20, k=3: 24 + 0 + 2·3·1 = 30) = 30`.
Gap 4: `dp[0][4] = max(k=1: 0 + 30 + 2 = 32, k=2: 8 + 12 + 4 = 24, k=3: 30 + 0 + 3 = 33) = **33**`. Last balloon is 3; before that, 2 is the last in `(0,3)`; first 4 is burst (2·4·3 = 24), then 2 (1·2·3 = 6), then 3 (1·3·1 = 3).
</details>

**2.** Using the example `[3, 1, 5, 8]`, show numerically that the "first balloon" split gives a wrong answer if you pretend the sides are independent with fixed outer neighbours.

<details><summary>Answer</summary>

Suppose you burst `5` first (1·5·8 = 40) and then treat `[3, 1]` and `[8]` as separate problems, each with outer neighbours 1. The best for `[3, 1]` alone is 6 (burst 1: 3·1·1 = 3, then 3: 1·3·1 = 3), and `[8]` alone gives 8: "total" 40 + 6 + 8 = 54. But that is not the value of any real order: after 5 is gone, 1 and 8 are neighbours. The true best after bursting 5 first is 40 + 56 = 96 (burst 1: 3·1·8 = 24, then 3: 1·3·8 = 24, then 8: 8). The pieces interact, so the "independent" sum is meaningless.
</details>

**3.** What would `dp[0][n+1]` be if all balloons had value 0 except one balloon of value `v`?

<details><summary>Answer</summary>

`v`, if that balloon is burst last (its neighbours are then the padding 1s: 1·v·1). Every other burst involves a 0 and earns nothing. Bursting it earlier would put a 0 next to it (if it has a zero-valued neighbour) and earn 0, so "last" is optimal — the DP finds this with `k` = that balloon at the top level.
</details>
