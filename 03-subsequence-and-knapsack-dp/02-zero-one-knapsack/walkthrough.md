# 0/1 Knapsack — Walkthrough

## 1. Intuition (no code, no math — an everyday analogy)

You are packing a backpack for a trip. The airline allows **8 kg**. On the bed lie three things:

- a camera: 3 kg, worth 30 "happiness points";
- a laptop: 4 kg, worth 50;
- a tent: 5 kg, worth 60.

Everything together weighs 12 kg — too much. Which ones do you take?

A tempting rule is "take the most valuable thing first": tent (60), then 3 kg left, so the camera (30): total 90. Another rule is "best value per kg first": laptop (12.5/kg), then camera (10/kg): 7 kg, total 80 — worse. Rules of thumb disagree, and neither is guaranteed to be right on other inputs.

The reliable way is to go through the items **one at a time** and, for each, consider both futures: *"what if I pack it?"* and *"what if I leave it?"*. Packing it uses up some kilos, so the future depends on **how many kilos are still free**. That pair — *which item am I looking at* and *how much room is left* — is all you need to know. Two different packing histories that leave you at the same item with the same free room have the same best future. Remember those answers and you never solve the same situation twice.

## 2. Problem statement in our own words

There are `n` items. Item `i` has a positive integer weight `w[i]` and a value `v[i]`. The bag can carry total weight at most `W`. Choose a subset of the items (each item at most once) whose total weight is at most `W` and whose total value is as large as possible. Output that value.

Judge: [Knapsack 1 — AtCoder DP Contest, task D](https://atcoder.jp/contests/dp/tasks/dp_d). Its limits are `n ≤ 100`, `W ≤ 10^5`, `v[i] ≤ 10^9`. With up to 100 values of 10^9, the answer can reach 10^11, so it needs a `long`.

## 3. Examples (input → output, with an explanation)

Our running example (the first sample of the judge problem): **`W = 8`, items `(weight, value)` = `(3, 30)`, `(4, 50)`, `(5, 60)`.**

| Choice | Weight | Value | Fits? |
|---|---|---|---|
| nothing | 0 | 0 | yes |
| item 0 | 3 | 30 | yes |
| item 1 | 4 | 50 | yes |
| item 2 | 5 | 60 | yes |
| items 0 + 1 | 7 | 80 | yes |
| **items 0 + 2** | **8** | **90** | **yes — best** |
| items 1 + 2 | 9 | 110 | no (9 > 8) |
| all three | 12 | 140 | no |

Answer: **90**.

More examples (all checked by the solution files):

| Input | Output | Why |
|---|---|---|
| `W = 15`, items `(6,5) (5,6) (6,4) (6,6) (3,5) (7,2)` | 17 | items `(5,6) (6,6) (3,5)`: weight 14, value 17 |
| `W = 0`, same 3 items | 0 | nothing fits |
| `W = 9`, one item `(10, 99)` | 0 | the only item is too heavy |

## 4. Deriving the solution — the 7 questions

### Q1. What is changing?

We decide the items one by one, so the **item index** `i` moves from `0` to `n`. Each "take" reduces the **remaining capacity** `c`. Nothing else about the past matters: the items we already took affect the future *only* through how much room they used.

### Q2. What does the state represent?

> `best(i, c)` = the maximum total value we can still collect from items `i, i+1, …, n-1`, if the bag has `c` units of free capacity.

The answer to the whole problem is `best(0, W)`.

Compare with [topic 01](../01-subsequences-fundamentals/walkthrough.md#q2-what-does-the-state-represent): it is the same "index + accumulated quantity" shape. There, the accumulated quantity was the sum of taken numbers; here it is the weight of taken items, stored as "what is left".

### Q3. What are the choices?

For item `i`:

1. **Skip** it. Go to item `i + 1` with the same capacity `c`. Value gained: 0.
2. **Take** it — only allowed if `w[i] ≤ c`. Gain `v[i]`, go to item `i + 1` with capacity `c - w[i]`.

Taking moves to `i + 1`, not `i`: item `i` cannot be taken a second time. That single detail is what makes this the **0/1** knapsack. (In [Unbounded Knapsack](../07-unbounded-knapsack/) we will stay at `i`.)

### Q4. What is the recurrence?

```
best(i, c) = max( best(i + 1, c),                       ← skip item i
                  v[i] + best(i + 1, c - w[i]) )         ← take item i   (only if w[i] ≤ c)
```

We use `max` because we want the better of the two futures. If the item does not fit, only the skip term exists.

### Q5. What are the base cases?

- `i == n`: no items left, so no more value can be added: `best(n, c) = 0` for every `c ≥ 0`.

We never need a "capacity became negative" base case, because we only take an item when it fits.

### Q6. What is the order of computation?

`best(i, c)` depends on `best(i + 1, ·)`. In the bottom-up table we use the more common **prefix** view:

> `dp[i][c]` = the best value using only the **first `i` items** (items `0..i-1`) with capacity `c`.

```
dp[0][c] = 0                                                   (no items, no value)
dp[i][c] = max( dp[i-1][c],  v[i-1] + dp[i-1][c - w[i-1]] )    (second term only if w[i-1] ≤ c)
```

Row `i` needs only row `i - 1`, so we fill **rows top to bottom**. Within a row, the order of `c` does not matter while we keep the full table (we change that in Section 8).

### Q7. Can the state space be optimized?

Row `i` reads row `i - 1` at column `c` (straight up) and at column `c - w` (up and to the left). Nothing older. So we need **two rows**, and with a careful loop direction, **one row**: `O(W)` memory instead of `O(n · W)`.

## 5. Brute force: recursion

```java
private static long best(int[] weight, long[] value, int i, int capacity) {
    if (i == weight.length) return 0;                // no items left
    long skip = best(weight, value, i + 1, capacity);
    long take = Long.MIN_VALUE;                       // "impossible" unless it fits
    if (weight[i] <= capacity) {
        take = value[i] + best(weight, value, i + 1, capacity - weight[i]);
    }
    return Math.max(skip, take);
}
```

Full code: [`solution/BruteForce.java`](solution/BruteForce.java).

The recursion tree for our example. A node is `best(i, c)`. Left edge = take, right edge = skip. The number under a leaf or node is its returned value.

```
                                     best(0,8) = 90
                       take #0 (3kg) /              \ skip #0
                            best(1,5) = 60              best(1,8) = 60
              take #1 (4kg) /      \ skip     take #1 /      \ skip
                   best(2,1)=0   best(2,5)=60    best(2,4)=0   best(2,8)=60
                     |  (5>1,      /     \         | (5>4,       /     \
                     |  no take)  take   skip      | no take)  take   skip
                 best(3,1)   best(3,0) best(3,5) best(3,4) best(3,3) best(3,8)
                    0           0        0         0          0        0

 best(2,5) = max(skip 0, take 60 + 0) = 60       best(1,5) = max(skip 60, take 50 + best(2,1)=0 → 50) = 60
 best(2,8) = max(0, 60 + 0)          = 60       best(1,8) = max(skip 60, take 50 + best(2,4)=0 → 50) = 60
 best(0,8) = max(skip best(1,8) = 60, take 30 + best(1,5) = 30 + 60 = 90) = 90
```

**Why it is slow:** each level doubles the number of calls (except where an item does not fit), so there are up to `2^n` leaves. With `n = 100` that is 10^30 calls.

**Where the repeats are:** there are only `n × (W + 1)` different pairs `(i, c)`, while there are `2^n` paths. Different paths that use the same weight land on the same pair. For example, with weights `[2, 3, 5, …]` and `W = 10`:

```
take 2, take 3, skip 5   → arrives at best(3, 10 - 5) = best(3, 5)
skip 2, skip 3, take 5   → arrives at best(3, 10 - 5) = best(3, 5)    same question!
```

Both histories used 5 kg and are now looking at item 3. Their futures are identical.

## 6. Memoization (top-down)

**What is cached:** `memo[i][c]` = `best(i, c)`. `-1` means "not computed yet" (every real answer is ≥ 0).

```java
private static long best(int[] weight, long[] value, int i, int capacity) {
    if (i == weight.length) return 0;
    if (memo[i][capacity] != -1) return memo[i][capacity];

    long skip = best(weight, value, i + 1, capacity);
    long take = Long.MIN_VALUE;
    if (weight[i] <= capacity) {
        take = value[i] + best(weight, value, i + 1, capacity - weight[i]);
    }
    memo[i][capacity] = Math.max(skip, take);
    return memo[i][capacity];
}
```

Full code: [`solution/Memoization.java`](solution/Memoization.java).

**Dry run on our example.** Each state is computed once and stored. In order of completion:

| Step | State | skip | take | Stored |
|---|---|---|---|---|
| 1 | `best(2,1)` | `best(3,1) = 0` | 5 > 1, not allowed | 0 |
| 2 | `best(2,5)` | `best(3,5) = 0` | `60 + best(3,0) = 60` | 60 |
| 3 | `best(1,5)` | `best(2,5) = 60` | `50 + best(2,1) = 50` | 60 |
| 4 | `best(2,4)` | `best(3,4) = 0` | 5 > 4, not allowed | 0 |
| 5 | `best(2,8)` | `best(3,8) = 0` | `60 + best(3,3) = 60` | 60 |
| 6 | `best(1,8)` | `best(2,8) = 60` | `50 + best(2,4) = 50` | 60 |
| 7 | `best(0,8)` | `best(1,8) = 60` | `30 + best(1,5) = 90` | **90** |

With only 3 items no state repeats, so there are no cache hits yet. With 100 items there are at most `100 × (W + 1)` stored states and every repeat is answered in `O(1)`.

## 7. Tabulation (bottom-up)

```java
long[][] dp = new long[n + 1][capacity + 1];      // row 0 stays all zeros
for (int i = 1; i <= n; i++) {
    int w = weight[i - 1];
    long v = value[i - 1];
    for (int c = 0; c <= capacity; c++) {
        dp[i][c] = dp[i - 1][c];                                   // skip
        if (w <= c) dp[i][c] = Math.max(dp[i][c], v + dp[i - 1][c - w]);  // take
    }
}
return dp[n][capacity];
```

Full code: [`solution/Tabulation.java`](solution/Tabulation.java).

### Filling the table, step by step

Columns are capacities `0..8`. Row `i` means "the first `i` items are available".

**Row 0** — no items: all zeros.

```
                c:   0   1   2   3   4   5   6   7   8
i=0  (none)          0   0   0   0   0   0   0   0   0
```

**Row 1** — item 0 = (3 kg, 30). For `c < 3` it does not fit, copy from above. For `c ≥ 3`: `max(dp[0][c], 30 + dp[0][c-3]) = max(0, 30) = 30`.

```
i=1  (3,30)          0   0   0  30  30  30  30  30  30
```

**Row 2** — item 1 = (4 kg, 50).

```
c=0..3: 4 > c, copy:            0   0   0  30
c=4: max(dp[1][4]=30, 50 + dp[1][0]=0)  = 50     laptop alone
c=5: max(30, 50 + dp[1][1]=0)           = 50
c=6: max(30, 50 + dp[1][2]=0)           = 50
c=7: max(30, 50 + dp[1][3]=30)          = 80     laptop + camera
c=8: max(30, 50 + dp[1][4]=30)          = 80
```

```
i=2  (4,50)          0   0   0  30  50  50  50  80  80
```

**Row 3** — item 2 = (5 kg, 60).

```
c=0..4: 5 > c, copy:            0   0   0  30  50
c=5: max(dp[2][5]=50, 60 + dp[2][0]=0)  = 60     tent alone
c=6: max(50, 60 + dp[2][1]=0)           = 60
c=7: max(80, 60 + dp[2][2]=0)           = 80     camera + laptop still better
c=8: max(80, 60 + dp[2][3]=30)          = 90     tent + camera
```

**Final table:**

```
                c:   0   1   2   3   4   5   6   7   8
i=0  (none)          0   0   0   0   0   0   0   0   0
i=1  (3,30)          0   0   0  30  30  30  30  30  30
i=2  (4,50)          0   0   0  30  50  50  50  80  80
i=3  (5,60)          0   0   0  30  50  60  60  80  90   ← dp[3][8] = 90
```

Every cell reads two cells of the row above:

```
row i-1:   ...  [c - w]  ...........  [c]
                    \                  |
          take: +v   \                 |  skip
                      v                v
row i:                 ------------>  [c]   = max of the two
```

### Which items were chosen?

Walk back from `dp[n][W]`. If `dp[i][c] == dp[i-1][c]`, skipping item `i-1` already gives this value, so treat it as skipped. Otherwise item `i-1` was taken: subtract its weight.

```
i=3, c=8: dp[3][8]=90 ≠ dp[2][8]=80  → item 2 (5,60) taken, c = 8-5 = 3
i=2, c=3: dp[2][3]=30 = dp[1][3]=30  → item 1 skipped
i=1, c=3: dp[1][3]=30 ≠ dp[0][3]=0   → item 0 (3,30) taken, c = 0
Chosen: items 0 and 2, weight 8, value 90.
```

## 8. Space optimization

### Step 1: two rows

Row `i` reads only row `i - 1`. Keep `prev` (row `i-1`) and `curr` (row `i`):

```java
long[] prev = new long[capacity + 1];
for (int i = 0; i < n; i++) {
    long[] curr = new long[capacity + 1];
    for (int c = 0; c <= capacity; c++) {
        curr[c] = prev[c];
        if (weight[i] <= c) curr[c] = Math.max(curr[c], value[i] + prev[c - weight[i]]);
    }
    prev = curr;
}
return prev[capacity];
```

Memory: `2 × (W + 1)` longs. This is always safe because we never write into the array we are reading from.

### Step 2: one row — and why the loop must go **backwards**

Can we drop `curr` and update `dp` in place? When we compute the new `dp[c]` we need two **old** values (from before item `i`): `dp[c]` itself and `dp[c - w]`.

- `dp[c]` is fine: we read it right before overwriting it.
- `dp[c - w]` is the danger. It lies to the **left** of `c`. If we have already overwritten it in this pass, we read a value that may **already include item `i`**, and then we add item `i` again.

**What goes wrong with a forward loop.** Item 0 = (3 kg, 30), starting from all zeros, `c` from 3 up to 8:

```
c=3: dp[3] = max(0, 30 + dp[0]=0)  = 30       ok: item 0 once
c=4: dp[4] = max(0, 30 + dp[1]=0)  = 30
c=5: dp[5] = max(0, 30 + dp[2]=0)  = 30
c=6: dp[6] = max(0, 30 + dp[3]=30) = 60       ✗ dp[3] was ALREADY updated: item 0 taken twice!
c=7: dp[7] = max(0, 30 + dp[4]=30) = 60       ✗
c=8: dp[8] = max(0, 30 + dp[5]=30) = 60       ✗

after item 0:  [0, 0, 0, 30, 30, 30, 60, 60, 60]     but only one camera exists!
```

Finishing all three items with the forward loop gives `dp[8] = 100` (the laptop twice: 50 + 50). That is the answer to a *different* problem — where items can be reused. [`Optimized.java`](solution/Optimized.java) keeps this buggy version as `forwardLoopBug` and checks that it really prints 100.

**The backward loop.** Same item, `c` from 8 down to 3:

```
c=8: dp[8] = max(0, 30 + dp[5]=0) = 30      dp[5] not touched yet → still "before item 0"
c=7: dp[7] = max(0, 30 + dp[4]=0) = 30
c=6: dp[6] = max(0, 30 + dp[3]=0) = 30      dp[3] not touched yet → correct
c=5: 30,  c=4: 30,  c=3: 30

after item 0:  [0, 0, 0, 30, 30, 30, 30, 30, 30]     = row 1 of the table ✔
```

Going from high `c` to low `c`, every cell we read on the left (`c - w < c`) has **not been visited yet in this pass**, so it still holds the previous row's value. The single array therefore behaves exactly like two rows.

```java
long[] dp = new long[capacity + 1];
for (int i = 0; i < n; i++) {
    for (int c = capacity; c >= weight[i]; c--) {     // high → low
        dp[c] = Math.max(dp[c], value[i] + dp[c - weight[i]]);
    }
}
return dp[capacity];
```

The loop stops at `c = weight[i]`: below that, the item cannot fit and the cell keeps its old value, which is exactly "skip".

**Remember the rule:** in 1-D, **0/1 → capacity loop backwards**; **unbounded → forwards** ([topic 07](../07-unbounded-knapsack/)). The forward loop is not a random bug; it is the unbounded knapsack.

## 9. Dry run on a full example

The one-row array after each item (backward loop), for `W = 8`, items `(3,30) (4,50) (5,60)`:

| After item | `dp[0..8]` | Matches table row |
|---|---|---|
| start | `0 0 0 0 0 0 0 0 0` | row 0 |
| `(3,30)` | `0 0 0 30 30 30 30 30 30` | row 1 |
| `(4,50)` | `0 0 0 30 50 50 50 80 80` | row 2 |
| `(5,60)` | `0 0 0 30 50 60 60 80 90` | row 3 |

Detail of the last pass (item (5,60), `c` from 8 down to 5):

| `c` | old `dp[c]` | `dp[c-5]` (still old) | `60 + dp[c-5]` | new `dp[c]` |
|---|---|---|---|---|
| 8 | 80 | `dp[3]` = 30 | 90 | **90** |
| 7 | 80 | `dp[2]` = 0 | 60 | 80 |
| 6 | 50 | `dp[1]` = 0 | 60 | 60 |
| 5 | 50 | `dp[0]` = 0 | 60 | 60 |

Brute force, memoization, the full table, two rows and one row all print **90**.

## 10. Complexity analysis (explained, for every approach)

| Approach | Time | Space | Why |
|---|---|---|---|
| Brute force | `O(2^n)` | `O(n)` | two branches per item, `n` levels; stack depth `n` |
| Memoization | `O(n · W)` | `O(n · W)` | `n × (W+1)` states, each computed once with `O(1)` work; memo table plus `O(n)` stack |
| Tabulation | `O(n · W)` | `O(n · W)` | each of the `(n+1)(W+1)` cells filled once |
| Two rows | `O(n · W)` | `O(W)` | same cells, only two rows kept |
| One row | `O(n · W)` | `O(W)` | same cells, one row kept (and slightly fewer cells: only `c ≥ w[i]`) |

For dp_d: `100 × 100 001 ≈ 10^7` steps — well under a second. The full table would be `101 × 100 001` longs ≈ 80 MB, which is why the one-row version is the one people submit.

### Variant: huge capacity — AtCoder dp_e (Knapsack 2)

[Knapsack 2](https://atcoder.jp/contests/dp/tasks/dp_e) has the same question, but `W` can be 10^9 while each value is at most 10^3 (and `n ≤ 100`). A table with 10^9 columns is impossible. But the **total value** is at most `100 × 1000 = 10^5`. So swap roles:

> `minWeight[v]` = the smallest total weight needed to collect value **exactly** `v`.

```
minWeight[0] = 0, every other entry = INF
for each item (w, val):  for v from totalValue down to val:     ← backward: still 0/1
    minWeight[v] = min(minWeight[v], minWeight[v - val] + w)
answer = the largest v with minWeight[v] ≤ W
```

Time `O(n · totalValue) ≈ 10^7`. Full code: [`solution/ValueIndexed.java`](solution/ValueIndexed.java). The lesson: **the DP dimension should be whichever quantity is small.** Read the constraints before you choose the state.

## 11. Why this approach works (correctness argument in plain words)

Look at any optimal packing for items `i..n-1` with capacity `c`. Either it contains item `i` or it does not.

- If it does not, the rest of it is a packing of items `i+1..n-1` with capacity `c`, and it must be an optimal one (otherwise we could swap in a better one and beat the "optimal" packing). Its value is `best(i+1, c)`.
- If it does, the rest is a packing of items `i+1..n-1` in the remaining `c - w[i]`, and by the same swap argument it is optimal: value `v[i] + best(i+1, c - w[i])`.

So the optimum is one of the two terms, and taking the `max` finds it. This is [optimal substructure](../../00-foundations/05-optimal-substructure/). The base case is correct because with no items the best value is 0. Since `i` always increases, the recursion ends.

## 12. Common mistakes

1. **Forward capacity loop in the 1-row version.** It silently computes unbounded knapsack. On our example it prints 100 instead of 90.
2. **Using `int` for values.** In dp_d the answer can be `100 × 10^9 = 10^11`, which overflows `int`.
3. **Taking an item that does not fit.** Always guard `w[i] ≤ c` before reading `dp[c - w[i]]` (otherwise: negative index exception).
4. **Initializing with `-INF` when the question is "at most W".** For "total weight ≤ W", every `dp[0][c]` is 0 (the empty bag fits anywhere). `-INF` everywhere except `dp[0][0]` would mean "exactly `c`" — a different question, sometimes the one you need ([Subset Sum](../03-subset-sum/) is an "exactly" question).
5. **Allocating the full table at dp_d's limits** in a tight memory limit, or using a value-indexed DP when `W` is small (or vice versa). Read the constraints first.
6. **Greedy by value/weight ratio.** It is optimal for the *fractional* knapsack (where you may cut items), not for 0/1. Our example: ratio-greedy takes the laptop and the camera (80), the optimum is 90.

## 13. When to use this technique / how to recognize similar problems

Signs of 0/1 knapsack:

- A list of items, each usable **at most once**.
- A **budget / capacity / target** that each chosen item consumes.
- A question about the best total, whether a total is reachable, or how many selections exist.
- The budget is a reasonably small integer (≤ about 10^5–10^6), so a table indexed by it fits.

Disguises you will meet next: "split the array into two parts" ([Equal Sum Partition](../04-equal-sum-partition/), [Minimum Difference](../05-partition-with-minimum-difference/)), "assign + or −" ([Target Sum](../06-target-sum/)), "two budgets" ([Ones and Zeroes](https://leetcode.com/problems/ones-and-zeroes/)).

## 14. Summary and revision notes

- State: `best(i, c)` = best value from items `i..n-1` with free capacity `c`; or prefix form `dp[i][c]`.
- Choices: skip (`i+1`, `c`) or take if it fits (`i+1`, `c - w`) — **`i + 1` after taking because each item is used once**.
- Combine with `max`; base `dp[0][c] = 0`.
- Time `O(n·W)`; space `O(n·W)` → `O(W)`.
- 1-D: **backward** capacity loop for 0/1. Forward = unbounded (a bug here, the right answer in topic 07).
- Huge `W`, small values → index by value, store min weight (dp_e).
- Recover chosen items by walking back: `dp[i][c] ≠ dp[i-1][c]` ⇒ item `i-1` was taken.

```
dp[i][c] = best value from the first i items with capacity c

dp[0][c] = 0
dp[i][c] = max( dp[i-1][c],  v[i-1] + dp[i-1][c - w[i-1]] )     (second term only if w[i-1] <= c)

1-D:  for each item:  for c = W down to w:  dp[c] = max(dp[c], v + dp[c - w])
```

## Exercises

1. Fill the table for `W = 5`, items `(1, 1)`, `(2, 6)`, `(3, 10)`, `(5, 16)`. What is the answer, and which items are chosen?

<details>
<summary>Answer</summary>

```
            c: 0  1  2  3  4  5
i=0             0  0  0  0  0  0
i=1 (1,1)       0  1  1  1  1  1
i=2 (2,6)       0  1  6  7  7  7
i=3 (3,10)      0  1  6 10 11 16
i=4 (5,16)      0  1  6 10 11 16
```
Answer 16. Walking back: `dp[4][5] = dp[3][5]` → item (5,16) skipped; `dp[3][5]=16 ≠ dp[2][5]=7` → (3,10) taken, `c = 2`; `dp[2][2]=6 ≠ dp[1][2]=1` → (2,6) taken. Items (2,6) + (3,10). (Taking (5,16) alone also gives 16 — ties are fine.)
</details>

2. Run the **forward** 1-row loop on `W = 4` with the single item `(2, 3)`. What does it output and why is that wrong?

<details>
<summary>Answer</summary>

`c=2: dp[2] = 3`, `c=3: dp[3] = 3 + dp[1] = 3`, `c=4: dp[4] = 3 + dp[2] = 6`. Output 6, i.e. the item taken twice; with only one copy the answer is 3. The backward loop gives `c=4: 3 + dp[2]=0 → 3`, `c=3: 3`, `c=2: 3` → 3.
</details>

3. In dp_d, why is `dp[c]` declared as `long`, and why is the full 2-D table a bad idea in Java at the maximum limits?

<details>
<summary>Answer</summary>

Values go up to 10^9 and up to 100 items can be taken, so the answer can be 10^11 > 2^31 − 1. The full table has `101 × 100 001 ≈ 10^7` longs = about 80 MB, which can exceed the memory limit; the one-row array is 800 KB.
</details>

4. A problem gives `n = 100`, `W = 10^9`, weights up to 10^9 and values up to 10^3. Which DP do you use, what is its table size, and what loop direction?

<details>
<summary>Answer</summary>

The value-indexed DP of dp_e: `minWeight[v]` for `v = 0..totalValue`, at most `100 × 1000 = 10^5` entries. It is still 0/1 (each item once), so the `v` loop runs **backward**. Answer = largest `v` with `minWeight[v] ≤ W`.
</details>
