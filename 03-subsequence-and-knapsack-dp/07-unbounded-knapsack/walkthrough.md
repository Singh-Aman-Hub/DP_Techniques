# Unbounded Knapsack — Walkthrough

## 1. Intuition (no code, no math — an everyday analogy)

You run a stall at a market and have a van that carries **8 crates' worth of weight**. The wholesaler sells three kinds of crate, and you can buy **as many of each kind as you like**:

- apples: 1 unit of weight, profit 15;
- cheese: 3 units, profit 50;
- honey: 4 units, profit 60.

In [0/1 Knapsack](../02-zero-one-knapsack/) each item existed once. Now the shelves are endless. Two cheeses (6 units, profit 100) plus two apples (2 units, profit 30) fill the van for **130** — better than anything you could do with one of each (1 + 3 + 4 = 8 units, profit 125).

How do you think about it item by item? Stand in front of the cheese shelf. Either you are **done with cheese** (walk on to the honey shelf), or you **put one cheese in the van and stay standing in front of the cheese shelf**, free to take another. That "stay where you are" is the entire difference from 0/1 knapsack.

## 2. Problem statement in our own words

There are `n` item **types**. Type `i` has weight `w[i] > 0` and value `v[i]`, and you may take any non-negative number of copies of each type. The total weight must be at most `W`. Maximize the total value.

This is the textbook form; the judged problems that use it ([Coin Change](https://leetcode.com/problems/coin-change/), [Perfect Squares](https://leetcode.com/problems/perfect-squares/), [Rod Cutting](../10-rod-cutting/)) change only the question (`min`, counting) and are covered in the next topics and the practice table.

## 3. Examples (input → output, with an explanation)

Running example: **`W = 8`, types `(1, 15)`, `(3, 50)`, `(4, 60)`.**

| Input | Output | Best choice |
|---|---|---|
| `W = 8`, `(1,15) (3,50) (4,60)` | **130** | 2 × (3,50) + 2 × (1,15): weight 8 |
| `W = 7`, same types | 115 | 2 × (3,50) + 1 × (1,15) |
| `W = 7`, one type `(2, 3)` | 9 | three copies (weight 6); a fourth would weigh 8 |
| `W = 0` | 0 | nothing fits |
| `W = 4`, one type `(5, 10)` | 0 | the only type is too heavy |
| `W = 8`, same types, **0/1 rules** | 125 | (1,15) + (3,50) + (4,60) — shown for contrast |

## 4. Deriving the solution — the 7 questions

### Q1. What is changing?

As in 0/1 knapsack: the item type `i` we are deciding about, and the remaining capacity `c`.

### Q2. What does the state represent?

> `best(i, c)` = the maximum value obtainable from item types `i, i+1, …, n-1`, each in unlimited supply, with `c` units of capacity left.

Answer: `best(0, W)`.

Notice: the state does **not** need to remember how many copies of type `i` we already took. The past matters only through the capacity it used — the same reason that made the 0/1 state work.

### Q3. What are the choices?

1. **Done with type `i`** (skip): go to `best(i + 1, c)`.
2. **Take one more copy** of type `i`, if `w[i] ≤ c`: gain `v[i]` and go to `best(i, c − w[i])` — **still at type `i`**.

Taking "one copy and staying" repeated `k` times is the same as taking `k` copies. So two choices are enough; we never need a loop over "how many copies".

### Q4. What is the recurrence?

```
best(i, c) = max( best(i + 1, c),                    ← stop taking type i
                  v[i] + best(i, c − w[i]) )          ← one more copy, stay at i   (if w[i] ≤ c)
```

Side by side with 0/1:

```
0/1:        take → v[i] + best(i + 1, c − w[i])        move on: each item once
unbounded:  take → v[i] + best(i,     c − w[i])        stay:    item may repeat
```

Does staying at `i` risk infinite recursion? No: each take reduces `c` by `w[i] > 0`, so after at most `c / w[i]` takes the item no longer fits and only skip remains.

### Q5. What are the base cases?

`best(n, c) = 0`: no types left, nothing more to gain. (As before, "capacity 0" needs no special case: nothing fits, so only skips happen.)

### Q6. What is the order of computation?

Prefix view: `dp[i][c]` = the best value using types `0..i-1` (unlimited) with capacity `c`.

```
dp[0][c] = 0
dp[i][c] = max( dp[i-1][c],  v[i-1] + dp[i][c − w[i-1]] )     ← SAME row i for the take
```

`dp[i][c]` reads row `i − 1` (above) and row `i` at a **smaller** capacity (to the left). So: rows top to bottom, and **within a row, `c` from left to right**, so that `dp[i][c − w]` is already final when we need it.

### Q7. Can the state space be optimized?

One array. Because the take term wants the **current** row's value at `c − w` (which may already include copies of the current type), the one-row loop must go **forward** — the opposite of 0/1.

## 5. Brute force: recursion

```java
private static long best(int[] weight, long[] value, int i, int capacity) {
    if (i == weight.length) return 0;
    long skip = best(weight, value, i + 1, capacity);
    long take = Long.MIN_VALUE;
    if (weight[i] <= capacity) {
        take = value[i] + best(weight, value, i, capacity - weight[i]);   // i, not i + 1
    }
    return Math.max(skip, take);
}
```

Full code: [`solution/BruteForce.java`](solution/BruteForce.java).

The top of the tree for our example. Left = take one more copy (stay), right = done with this type. Types: `#0 = (1,15)`, `#1 = (3,50)`, `#2 = (4,60)`.

```
                                    best(0,8)
                 take #0 (stay) /                \ done with #0
                      best(0,7)                    best(1,8)
             take #0 /      \ done       take #1 /         \ done
              best(0,6)   best(1,7)        best(1,5)          best(2,8)
                 ...      /      \        /       \           /      \
                    best(1,4)  best(2,7) best(1,2) best(2,5) best(2,4) best(3,8)=0
                       ...        ...      ...       ...       ...
```

The left spine "take #0, take #0, …" goes 8 levels deep (eight apples). Every type can repeat, so the tree is much larger than the 0/1 tree's `2^n` — its size grows with `W` as well.

**Repeats are everywhere:** `best(1, 5)` is reached by "take #1 once from `best(1,8)`" and also by "three apples, then done with #0" (`best(0,8) → best(0,7) → best(0,6) → best(0,5) → best(1,5)`). There are only `n × (W + 1)` different states.

## 6. Memoization (top-down)

```java
private static long best(int[] weight, long[] value, int i, int capacity) {
    if (i == weight.length) return 0;
    if (memo[i][capacity] != -1) return memo[i][capacity];
    long skip = best(weight, value, i + 1, capacity);
    long take = Long.MIN_VALUE;
    if (weight[i] <= capacity) {
        take = value[i] + best(weight, value, i, capacity - weight[i]);
    }
    memo[i][capacity] = Math.max(skip, take);
    return memo[i][capacity];
}
```

Full code: [`solution/Memoization.java`](solution/Memoization.java).

**What is cached:** `memo[i][c] = best(i, c)`, `-1` = not computed. The recursion depth can reach about `W / (smallest weight) + n` (a long chain of takes), which is fine for small `W`; for very large `W` prefer the table.

**Dry run, a few entries** (the computation runs deep down the apple spine first; values are filled bottom-up as calls return):

```
best(2, c) for type (4,60) only:   c: 0 1 2 3  4  5  6  7   8
                                      0 0 0 0 60 60 60 60 120
best(1, 5) = max(best(2,5)=60, 50 + best(1,2))
           = max(60, 50 + max(best(2,2)=0, 3 > 2 → no take) ) = max(60, 50) = 60
best(1, 8) = max(best(2,8)=120, 50 + best(1,5)=60 → 110) = 120
...
best(0, 8) = max(best(1,8)=120, 15 + best(0,7)=115 → 130) = 130
```

(`best(1, c)` uses only cheese and honey: for `c = 8` that is two honeys = 120. Adding apples finally reaches 130.)

## 7. Tabulation (bottom-up)

```java
long[][] dp = new long[n + 1][capacity + 1];
for (int i = 1; i <= n; i++) {
    int w = weight[i - 1];
    for (int c = 0; c <= capacity; c++) {                 // left to right
        dp[i][c] = dp[i - 1][c];
        if (w <= c) dp[i][c] = Math.max(dp[i][c], value[i - 1] + dp[i][c - w]);   // same row
    }
}
return dp[n][capacity];
```

Full code: [`solution/Tabulation.java`](solution/Tabulation.java).

### Filling the table, step by step

**Row 1** — type (1,15). Each cell: `max(0, 15 + dp[1][c−1])`, and `dp[1][c−1]` was just computed in this row:

```
                 c:   0    1    2    3    4    5    6    7    8
i=0  (none)           0    0    0    0    0    0    0    0    0
i=1  (1,15)           0   15   30   45   60   75   90  105  120      c apples
```

**Row 2** — type (3,50):

```
c=0..2: 3 > c, copy:              0   15   30
c=3: max(45,  50 + dp[2][0]=0 )  = 50     one cheese
c=4: max(60,  50 + dp[2][1]=15)  = 65     cheese + apple
c=5: max(75,  50 + dp[2][2]=30)  = 80
c=6: max(90,  50 + dp[2][3]=50)  = 100    dp[2][3] is THIS row: it already holds one cheese → two cheeses
c=7: max(105, 50 + dp[2][4]=65)  = 115
c=8: max(120, 50 + dp[2][5]=80)  = 130    two cheeses + two apples
```

**Row 3** — type (4,60):

```
c=0..3: copy:                     0   15   30   50
c=4: max(65,  60 + dp[3][0]=0 )  = 65
c=5: max(80,  60 + dp[3][1]=15)  = 80
c=6: max(100, 60 + dp[3][2]=30)  = 100
c=7: max(115, 60 + dp[3][3]=50)  = 115
c=8: max(130, 60 + dp[3][4]=65)  = 130    honey does not help
```

**Final table** (and, for contrast, the 0/1 table on the same items):

```
UNBOUNDED        c:   0    1    2    3    4    5    6    7    8
i=0                   0    0    0    0    0    0    0    0    0
i=1  (1,15)           0   15   30   45   60   75   90  105  120
i=2  (3,50)           0   15   30   50   65   80  100  115  130
i=3  (4,60)           0   15   30   50   65   80  100  115  130   ← 130

0/1              c:   0    1    2    3    4    5    6    7    8
i=1  (1,15)           0   15   15   15   15   15   15   15   15
i=2  (3,50)           0   15   15   50   65   65   65   65   65
i=3  (4,60)           0   15   15   50   65   75   75  110  125   ← 125
```

The arrows show the only structural difference:

```
0/1:        row i-1:  [c-w]      [c]          unbounded:  row i-1:              [c]
                         \        |                                              |
            row i:        -----> [c]                      row i:   [c-w] -----> [c]
```

### Which items, how many copies?

Walk back from `dp[3][8]`. If `dp[i][c] == dp[i-1][c]`, type `i-1` adds nothing more: move **up**. Otherwise one copy of type `i-1` was taken: subtract its weight and **stay in the row**.

```
(3,8): 130 = dp[2][8]           → no honey, go up
(2,8): 130 ≠ dp[1][8]=120       → one cheese, c = 5   (stay in row 2)
(2,5):  80 ≠ dp[1][5]=75        → another cheese, c = 2
(2,2):  30 = dp[1][2]           → go up
(1,2):  30 ≠ 0                  → one apple, c = 1
(1,1):  15 ≠ 0                  → another apple, c = 0
Result: 2 cheeses + 2 apples = 100 + 30 = 130 ✔
```

## 8. Space optimization

One array, **forward** loop:

```java
long[] dp = new long[capacity + 1];
for (int i = 0; i < n; i++) {
    for (int c = weight[i]; c <= capacity; c++) {          // low → high
        dp[c] = Math.max(dp[c], value[i] + dp[c - weight[i]]);
    }
}
return dp[capacity];
```

Full code: [`solution/Optimized.java`](solution/Optimized.java) (it also contains the 0/1 version, differing only in the loop direction, and checks 130 vs 125).

**Why forward is right here.** When the loop reaches `c`, every smaller capacity has already been updated *for the current type*. So `dp[c − w]` is "best with the current type allowed, possibly already used" — exactly the same-row value `dp[i][c − w]` of the table. In [0/1 Knapsack](../02-zero-one-knapsack/walkthrough.md#8-space-optimization) this same effect was a bug (it let one item be taken twice); here, taking a type twice is the whole point.

```
             0/1 knapsack                     unbounded knapsack
loop c:      W  → w   (backward)              w → W   (forward)
dp[c - w]:   still the PREVIOUS row           already the CURRENT row
meaning:     item i used at most once         item i may be used again
```

## 9. Dry run on a full example

Forward loop, `W = 8`, types `(1,15) (3,50) (4,60)`:

| After type | `dp[0..8]` |
|---|---|
| start | `0 0 0 0 0 0 0 0 0` |
| (1,15) | `0 15 30 45 60 75 90 105 120` |
| (3,50) | `0 15 30 50 65 80 100 115 130` |
| (4,60) | `0 15 30 50 65 80 100 115 130` |

Detail of the (3,50) pass:

| `c` | old `dp[c]` | `dp[c−3]` (already updated this pass?) | `50 + dp[c−3]` | new `dp[c]` |
|---|---|---|---|---|
| 3 | 45 | `dp[0]` = 0 (no) | 50 | 50 |
| 4 | 60 | `dp[1]` = 15 (no) | 65 | 65 |
| 5 | 75 | `dp[2]` = 30 (no) | 80 | 80 |
| 6 | 90 | `dp[3]` = 50 (**yes**: one cheese) | 100 | 100 |
| 7 | 105 | `dp[4]` = 65 (**yes**) | 115 | 115 |
| 8 | 120 | `dp[5]` = 80 (**yes**) | 130 | 130 |

All four solutions print **130**.

**Does the loop order of items vs capacities matter?** For a `max` (or `min`) question, no: you may also loop `c` outside and items inside (`dp[c] = max over items of v + dp[c − w]`). For **counting** it matters a lot — items outside counts combinations, capacity outside counts ordered sequences. That is the core of [Coin Change II](../09-coin-change-ii/) and of [Combination Sum IV](https://leetcode.com/problems/combination-sum-iv/).

## 10. Complexity analysis (explained, for every approach)

| Approach | Time | Space | Why |
|---|---|---|---|
| Brute force | exponential (grows with `n` and `W`) | `O(n + W)` | each type can be taken up to `W / w` times along a path; deepest path ≈ `W` takes + `n` skips |
| Memoization | `O(n · W)` | `O(n · W)` | `n × (W+1)` states, two choices each; plus the deep recursion stack |
| Tabulation | `O(n · W)` | `O(n · W)` | each cell once |
| One array | `O(n · W)` | `O(W)` | each type sweeps the capacities once |

The same `O(n · W)` as 0/1: allowing repeats costs nothing extra, because "one more copy" is a single transition, not a loop over counts.

## 11. Why this approach works (correctness argument in plain words)

Take an optimal selection for types `i..n-1` and capacity `c`. Either it contains **no** copy of type `i` — then it is an optimal selection for types `i+1..n-1` with capacity `c`, value `best(i+1, c)` — or it contains **at least one**. Remove one copy: what is left is a valid selection for types `i..n-1` (type `i` still allowed) with capacity `c − w[i]`, and it must be optimal for that state (otherwise swapping in a better one would beat the optimum). So the optimum equals one of the two terms. Every path ends because each take lowers the capacity by a positive weight.

## 12. Common mistakes

1. **Writing `i + 1` in the take branch** — that is 0/1 knapsack; you get 125 instead of 130.
2. **Backward 1-D loop** — same mistake in disguise: it forbids repeats.
3. **Reading `dp[i-1][c − w]` in the table** instead of `dp[i][c − w]` — again 0/1.
4. **Allowing a zero-weight item** — "take and stay" would never shrink the capacity: infinite recursion (and an unbounded answer if its value is positive). Real problems guarantee positive weights; check it.
5. **Assuming loop order never matters** — true for `max`/`min`, false for counting (topic 09).
6. **Deep recursion** — the take chain can be `W` calls deep; for large `W` use the table or the 1-D array.

## 13. When to use this technique / how to recognize similar problems

Look for words like **"unlimited supply"**, **"each coin can be used any number of times"**, **"you may cut as many pieces as you like"**, **"infinite number of each"**. Then:

- maximize value → this topic;
- fewest items to reach an exact total → [Coin Change](../08-coin-change/) (`min`, exact capacity, `INF` for impossible);
- number of ways → [Coin Change II](../09-coin-change-ii/) (`+`, mind the loop order);
- cut a length into pieces with prices → [Rod Cutting](../10-rod-cutting/).

If each item can be used **at most once**, go back to [0/1 Knapsack](../02-zero-one-knapsack/). If each item has its own limit `k[i]` copies ("bounded knapsack"), you can treat it as `k[i]` separate 0/1 items (or group them in powers of two).

## 14. Summary and revision notes

- Unbounded = unlimited copies of each type.
- Take → `best(i, c − w)`: **stay at `i`**. Skip → `best(i+1, c)`.
- Table: take reads the **same row**; fill each row left to right.
- 1-D: **forward** capacity loop (0/1 uses backward). Only this direction differs.
- `O(n · W)` time, `O(W)` space.
- For counting problems, item-outer vs capacity-outer changes the meaning.

```
dp[i][c] = best value from types 0..i-1 (unlimited) with capacity c

dp[0][c] = 0
dp[i][c] = max( dp[i-1][c],  v[i-1] + dp[i][c - w[i-1]] )      (second term only if w[i-1] <= c)

1-D:  for each type:  for c = w up to W:  dp[c] = max(dp[c], v + dp[c - w])
```

## Exercises

1. Fill the 1-D array for `W = 6`, types `(2, 5)` and `(3, 8)`. What is the answer, and which copies are used?

<details>
<summary>Answer</summary>

After (2,5): `[0, 0, 5, 5, 10, 10, 15]`. After (3,8): `c=3: max(5, 8+0) = 8`, `c=4: max(10, 8+0) = 10`, `c=5: max(10, 8+dp[2]=5) = 13`, `c=6: max(15, 8+dp[3]=8) = 16`. Final `[0, 0, 5, 8, 10, 13, 16]`. Answer **16** = two copies of (3,8).
</details>

2. Run the **backward** loop on the same input. What does it return, and which problem did it actually solve?

<details>
<summary>Answer</summary>

After (2,5) backward: only one copy allowed → `[0, 0, 5, 5, 5, 5, 5]`. After (3,8) backward: `c=6: max(5, 8+dp[3]=5) = 13`, `c=5: max(5, 8+dp[2]=5) = 13`, `c=4: 8`, `c=3: 8`. Returns **13** — the 0/1 knapsack answer (one of each item: 2 + 3 = 5 ≤ 6, value 13).
</details>

3. Why does the unbounded recursion terminate even though the take branch does not advance `i`?

<details>
<summary>Answer</summary>

Every take lowers `c` by `w[i] ≥ 1`. After at most `c / w[i]` takes, `w[i] > c`, the take branch is no longer allowed, and only the skip branch (which advances `i`) remains. Since `i` can advance at most `n` times, every path is finite. This relies on all weights being positive.
</details>
