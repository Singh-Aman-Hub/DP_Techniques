# Partition With Minimum Difference — Walkthrough

## 1. Intuition (no code, no math — an everyday analogy)

Back to the two friends from [topic 04](../04-equal-sum-partition/walkthrough.md), now with bags of 1, 6, 11 and 5 kg. The total is 23 kg — odd — so a perfectly fair split is impossible. They still want it **as fair as possible**.

Again, plan only friend A's load. If A carries `s1` kg, friend B carries `23 − s1`. The unfairness is the gap between them. If A carries 11, B carries 12: gap 1. If A carries 7, B carries 16: gap 9. The closer A's load is to half the total (11.5), the smaller the gap.

So the question becomes: **of all the loads A could possibly carry, which is closest to half?** And "all the loads A could possibly carry" is exactly the list of reachable sums that the subset-sum DP builds for free. We just look through that list.

## 2. Problem statement in our own words

Given an array of non-negative integers, divide it into two groups (every element in exactly one group; a group may be empty) and return the smallest possible value of `|sum(group 1) − sum(group 2)|`.

This exact statement is a classic textbook problem without a dedicated LeetCode page. Its judged form here is [Last Stone Weight II — LeetCode 1049](https://leetcode.com/problems/last-stone-weight-ii/), explained in Section 9.

## 3. Examples (input → output, with an explanation)

Running example: **`arr = [1, 6, 11, 5]`** (total 23).

| Input | Output | Best split |
|---|---|---|
| `[1, 6, 11, 5]` | 1 | `{1, 11}` = 12 vs `{6, 5}` = 11 (also `{11}` vs `{1, 6, 5}`) |
| `[3, 9, 7, 3]` | 2 | `{3, 7}` = 10 vs `{9, 3}` = 12 |
| `[1, 2, 3, 9]` | 3 | `{1, 2, 3}` = 6 vs `{9}` = 9 |
| `[1, 1]` | 0 | perfect split |
| `[5]` | 5 | `{5}` vs `{}` |

## 4. Deriving the solution — the 7 questions

### The key algebra

With `s2 = total − s1`:

```
|s1 − s2| = |s1 − (total − s1)| = |total − 2·s1|
```

The difference depends on **one number only**: `s1`. And by symmetry we may call the *lighter* group "group 1", so `s1 ≤ total / 2`, which makes the difference `total − 2·s1` (never negative). To make it small, make `s1` **as large as possible, but not above `total / 2`**, among sums that some subset can actually reach.

### Q1. What is changing?

The index `i` (which element goes where next) and `s1`, the sum of group 1 so far.

### Q2. What does the state represent?

Two equivalent formulations; we use both:

- Top-down: `explore(i, s1)` = the smallest final difference achievable when elements `0..i-1` are already placed and group 1 currently sums to `s1`.
- Bottom-up: `dp[i][s]` = `true` if some subset of the first `i` elements sums to exactly `s` — the plain [subset-sum](../03-subset-sum/) table.

### Q3. What are the choices?

Put element `i` into group 1 (`s1` grows by `arr[i]`) or into group 2 (`s1` unchanged).

### Q4. What is the recurrence?

Top-down:

```
explore(i, s1) = min( explore(i + 1, s1 + arr[i]),     ← element i joins group 1
                      explore(i + 1, s1) )             ← element i joins group 2
```

Bottom-up: the subset-sum recurrence, then a final scan:

```
dp[i][s] = dp[i-1][s] || (arr[i-1] ≤ s && dp[i-1][s - arr[i-1]])
answer   = total − 2 · max{ s ≤ total/2 : dp[n][s] is true }
```

### Q5. What are the base cases?

- Top-down: `explore(n, s1) = |total − 2·s1|` (everything placed; measure the gap).
- Bottom-up: `dp[0][0] = true`, `dp[0][s>0] = false`. The scan always finds `s = 0` (empty group 1), so it never fails.

### Q6. What is the order of computation?

Rows of the subset-sum table top to bottom (or the 1-D backward loop), **then** scan the last row from `total/2` downwards. The scan must come after the whole table is built, because only the last row covers all elements.

### Q7. Can the state space be optimized?

We only read the last row, so the one-array subset sum is enough: `O(total/2)` space.

## 5. Brute force: recursion

```java
private static int explore(int[] arr, int i, int s1, int total) {
    if (i == arr.length) return Math.abs(total - 2 * s1);
    int putInGroup1 = explore(arr, i + 1, s1 + arr[i], total);
    int putInGroup2 = explore(arr, i + 1, s1, total);
    return Math.min(putInGroup1, putInGroup2);
}
```

Full code: [`solution/BruteForce.java`](solution/BruteForce.java).

The recursion tree has 16 leaves for `[1, 6, 11, 5]`. Instead of drawing all of them, here is every leaf (= every split) with its `s1` and difference:

| Group 1 | `s1` | `s2` | `|total − 2·s1|` |
|---|---|---|---|
| `{1,6,11,5}` | 23 | 0 | 23 |
| `{1,6,11}` | 18 | 5 | 13 |
| `{1,6,5}` | 12 | 11 | **1** |
| `{1,6}` | 7 | 16 | 9 |
| `{1,11,5}` | 17 | 6 | 11 |
| `{1,11}` | 12 | 11 | **1** |
| `{1,5}` | 6 | 17 | 11 |
| `{1}` | 1 | 22 | 21 |
| `{6,11,5}` | 22 | 1 | 21 |
| `{6,11}` | 17 | 6 | 11 |
| `{6,5}` | 11 | 12 | **1** |
| `{6}` | 6 | 17 | 11 |
| `{11,5}` | 16 | 7 | 9 |
| `{11}` | 11 | 12 | **1** |
| `{5}` | 5 | 18 | 13 |
| `{}` | 0 | 23 | 23 |

The minimum is **1**. Notice the symmetry: each split appears twice (as group 1 and as group 2), with `s1` and `23 − s1`. That is why it is enough to look at `s1 ≤ 11`.

Top of the tree, labelled `explore(i, s1)`:

```
                         explore(0,0)
              group1 1 /             \ group2 1
               explore(1,1)            explore(1,0)
          +6 /          \          +6 /          \
     explore(2,7)    explore(2,1)  explore(2,6)  explore(2,0)
        ...             ...           ...           ...
```

## 6. Memoization (top-down)

`explore(i, s1)` depends only on `(i, s1)`, and for non-negative input `0 ≤ s1 ≤ total`. Cache it in `int[n+1][total+1]`, `-1` = unknown:

```java
private static int explore(int[] arr, int i, int s1, int total) {
    if (i == arr.length) return Math.abs(total - 2 * s1);
    if (memo[i][s1] != -1) return memo[i][s1];
    int putInGroup1 = explore(arr, i + 1, s1 + arr[i], total);
    int putInGroup2 = explore(arr, i + 1, s1, total);
    memo[i][s1] = Math.min(putInGroup1, putInGroup2);
    return memo[i][s1];
}
```

Full code: [`solution/Memoization.java`](solution/Memoization.java).

Where it saves work: in `[1, 6, 11, 5]` all states happen to be different (every subset of `{1, 6, 11}` has a different sum), so nothing is reused. As soon as two different groups have the same sum, their states merge. With `[1, 2, 3, 4]`, the histories "1 and 2 in group 1, 3 in group 2" and "1 and 2 in group 2, 3 in group 1" both reach `explore(3, 3)`, and the second one is a cache hit. In general there are at most `(n + 1)(total + 1)` distinct states versus `2^n` paths.

## 7. Tabulation (bottom-up)

Build subset sum **only up to `half = total / 2`**, then scan:

```java
int half = total / 2;
boolean[][] dp = new boolean[n + 1][half + 1];
dp[0][0] = true;
for (int i = 1; i <= n; i++) {
    int value = arr[i - 1];
    for (int s = 0; s <= half; s++) {
        dp[i][s] = dp[i - 1][s] || (value <= s && dp[i - 1][s - value]);
    }
}
for (int s1 = half; s1 >= 0; s1--) {
    if (dp[n][s1]) return total - 2 * s1;
}
```

Full code: [`solution/Tabulation.java`](solution/Tabulation.java).

### The table for `[1, 6, 11, 5]`, `total = 23`, `half = 11`

```
                 s: 0  1  2  3  4  5  6  7  8  9 10 11
i=0  {}             T  .  .  .  .  .  .  .  .  .  .  .
i=1  {1}            T  T  .  .  .  .  .  .  .  .  .  .
i=2  {1,6}          T  T  .  .  .  .  T  T  .  .  .  .     0→6, 1→7
i=3  {1,6,11}       T  T  .  .  .  .  T  T  .  .  .  T     0→11
i=4  {1,6,11,5}     T  T  .  .  .  T  T  T  .  .  .  T     0→5, 1→6, 6→11
                                                       ↑ scan starts here
```

**The scan** over the last row, from `s1 = 11` down:

```
s1 = 11: dp[4][11] = T  →  difference = 23 − 2·11 = 1.  Stop.
```

Here the very first cell hits. In general the scan may walk left past several false cells. For `[1, 2, 3, 9]` (total 15, half 7), the last row is true at `0, 1, 2, 3, 4, 5, 6` but false at 7, so the scan stops at `s1 = 6`: difference `15 − 12 = 3`.

Why scan from the top? Bigger `s1` (still ≤ half) means smaller `total − 2·s1`. The first true cell from the top is the best.

## 8. Space optimization

The scan only reads the last row, which the one-array subset sum produces directly:

```java
boolean[] reachable = new boolean[half + 1];
reachable[0] = true;
for (int value : arr)
    for (int s = half; s >= value; s--)
        if (reachable[s - value]) reachable[s] = true;
for (int s1 = half; s1 >= 0; s1--)
    if (reachable[s1]) return total - 2 * s1;
```

Full code: [`solution/Optimized.java`](solution/Optimized.java). Space `O(total/2)`.

## 9. Dry run on a full example — and Last Stone Weight II

### One-array dry run on `[1, 6, 11, 5]`, `half = 11`

| After | `reachable` true at | Notes |
|---|---|---|
| start | 0 | |
| 1 | 0, 1 | |
| 6 | 0, 1, 6, 7 | |
| 11 | 0, 1, 6, 7, 11 | only `s = 11` is in range for the number 11 |
| 5 | 0, 1, 5, 6, 7, 11 | `5 = 0+5`, `6+5 = 11` (already true); `7+5 = 12` is above half |

Scan: `s1 = 11` true → answer **1**. Brute force, memo, table and one array all return 1.

### A dry run where the scan has to walk: `[1, 2, 3, 9]`

`total = 15`, `half = 7`.

```
               s: 0  1  2  3  4  5  6  7
i=0  {}           T  .  .  .  .  .  .  .
i=1  {1}          T  T  .  .  .  .  .  .
i=2  {1,2}        T  T  T  T  .  .  .  .
i=3  {1,2,3}      T  T  T  T  T  T  T  .
i=4  {1,2,3,9}    T  T  T  T  T  T  T  .      9 > 7: does not enter the loop
```

Scan from 7: `dp[4][7]` false; `dp[4][6]` true → difference `15 − 2·6 = 3`.

### Recovering the groups

As in [topic 04](../04-equal-sum-partition/walkthrough.md#recovering-the-two-groups), walk up the 2-D table from `(n, s1)`: if the cell above is true the element goes to group 2, otherwise to group 1 (and `s` drops by its value).

```
(4, 6): dp[3][6] = T  → 9 goes to group 2
(3, 6): dp[2][6] = F  → 3 goes to group 1, s = 3
(2, 3): dp[1][3] = F  → 2 goes to group 1, s = 1
(1, 1): dp[0][1] = F  → 1 goes to group 1, s = 0
Group 1 = {1, 2, 3} = 6,  group 2 = {9},  difference 3.
```

### Last Stone Weight II (LeetCode 1049)

[The problem](https://leetcode.com/problems/last-stone-weight-ii/), in our words: you have stones with positive weights. Repeatedly pick any two stones `x ≤ y` and smash them: if equal both vanish, otherwise one stone of weight `y − x` remains. At the end at most one stone is left; return the smallest possible weight it can have (0 if none).

**Why it is our problem.** Follow one stone's weight through the smashes. Each smash *subtracts* one stone from another. Expand all the subtractions and the final weight is a sum of the original weights, each with a `+` or a `−` sign:

```
stones: 2, 7, 4, 1, 8, 1   (call the two 1s "1a" and "1b")

smash 8 and 7   → 1           =  8 − 7
smash 4 and 2   → 2           =  4 − 2
smash that 2 and 1a → 1       =  4 − 2 − 1a
smash (8 − 7) and (4 − 2 − 1a): equal, both vanish
last stone: 1b

final = 1b + (8 − 7) − (4 − 2 − 1a)  =  8 + 2 + 1 + 1  −  7 − 4  =  12 − 11  =  1
         '+' group {8, 2, 1a, 1b} = 12,   '−' group {7, 4} = 11
```

So the final weight is always `|s1 − s2|` for **some** split into a `+` group and a `−` group: it can never be smaller than the minimum difference `D`. And `D` is actually achievable: take an optimal split, and keep smashing one stone from the heavier group against one from the lighter group; each smash keeps `heavier − lighter = D` unchanged (the remainder stays in whichever group its bigger stone came from). When the lighter group runs out, the heavier group's stones sum to `D`, and smashing among them can only end at `D` or less. Since nothing can go below `D`, the answer is exactly `D`.

**Dry run** for `[2, 7, 4, 1, 8, 1]`: total 23, half 11. Reachable sums ≤ 11 include 11 (`7 + 4`), so the answer is `23 − 22 = 1`. For `[31, 26, 33, 21, 40]`: total 151, half 75; the largest reachable `s1 ≤ 75` is 73 (`40 + 33`), giving `151 − 146 = 5`. Full code: [`solution/LastStoneWeightII.java`](solution/LastStoneWeightII.java).

## 10. Complexity analysis (explained, for every approach)

| Approach | Time | Space | Why |
|---|---|---|---|
| Brute force | `O(2^n)` | `O(n)` | every split is a leaf |
| Memoization | `O(n · total)` | `O(n · total)` | `(n+1)(total+1)` states `(i, s1)`, `O(1)` each |
| Tabulation | `O(n · total)` | `O(n · total)` | `n × (total/2 + 1)` cells plus an `O(total)` scan |
| One array | `O(n · total)` | `O(total)` | each element sweeps at most `total/2` sums |

For LC 1049: `n ≤ 30`, weights ≤ 100, so `total ≤ 3000` and the DP is at most `30 × 1501` steps — instant.

## 11. Why this approach works (correctness argument in plain words)

Every split corresponds to a group-1 sum `s1` that some subset reaches, and every reachable `s1` corresponds to a split (the subset and its complement). The difference of that split is `|total − 2·s1|`, which is symmetric around `total/2`, so the best split has a version with `s1 ≤ total/2`, where the difference `total − 2·s1` strictly decreases as `s1` grows. The subset-sum row lists exactly the reachable `s1` values, so the largest true cell ≤ `total/2` gives the minimum.

## 12. Common mistakes

1. **Scanning from 0 upwards and stopping at the first true cell** — that is always `s1 = 0`, the worst split.
2. **Building the table only up to `half` but scanning above it** — index out of bounds; or building to `total` and forgetting to restrict the scan to `≤ half`, then computing `total − 2·s1` as negative.
3. **Returning `half − s1`** instead of `total − 2·s1`. With odd totals these differ in a way that is easy to miss: for total 23 and `s1 = 11`, `half − s1 = 0` but the true difference is 1.
4. **Simulating Last Stone Weight II greedily** (always smash the two heaviest). That is the *first* Last Stone Weight problem; here it fails: for `[31, 26, 33, 21, 40]` greedy gives 9 instead of 5.
5. **Using this DP with negative numbers or huge values** — see the next section.

## 13. When to use this technique / how to recognize similar problems

Use it when the problem asks to split items into two groups to make the sums **as close as possible**, or to minimize/maximize something that depends only on **one group's sum**, and the total is small enough to index an array (≤ about 10^6).

**Warning — when it breaks.** [LeetCode 2035](https://leetcode.com/problems/partition-array-into-two-arrays-to-minimize-sum-difference/) (Hard) asks for the same minimum difference, but:

- values can be **negative** and as large as 10^7 in absolute value, so the range of sums is about 3 · 10^8 wide — far too big for a table;
- both groups must have **exactly `n` elements** (the array has `2n ≤ 30`), adding a size constraint.

There the right tool is **meet in the middle**: split the array into two halves of 15 elements, list all `2^15` subset sums of each half grouped by how many elements they use, sort them, and for each choice on the left binary-search the best partner on the right. That is `O(2^n · n)` work and does not depend on the size of the values. Rule of thumb: **small totals → subset-sum DP; small `n` but huge or negative values → meet in the middle.**

## 14. Summary and revision notes

- Difference of a split = `total − 2·s1` with `s1` = the lighter group's sum.
- Build subset sum up to `half = total / 2`; scan the last row from `half` down; first true `s1` wins.
- Last Stone Weight II = this problem (smashing = assigning `+`/`−` signs).
- Time `O(n · total)`, space `O(total)`.
- Negative or huge values, or a fixed group size → not this DP (LC 2035 uses meet in the middle).

```
half = total / 2
reachable[0] = true
for each x:  for s = half down to x:  reachable[s] |= reachable[s - x]
for s1 = half down to 0:  if reachable[s1]:  return total - 2*s1
```

## Exercises

1. Find the minimum difference for `[8, 6, 5]` using the one-array method. Show the reachable set.

<details>
<summary>Answer</summary>

Total 19, half 9. After 8: `{0, 8}`. After 6: `{0, 6, 8}` (14 is above half). After 5: `{0, 5, 6, 8}` (11, 13 above half). Scan from 9: 9 false, 8 true → difference `19 − 16 = 3` (`{8}` vs `{6, 5}`).
</details>

2. Why is the scan guaranteed to find a true cell?

<details>
<summary>Answer</summary>

`reachable[0]` is always true (the empty group), and the scan goes all the way down to `s1 = 0`. It returns `total − 0 = total` only when no positive sum ≤ `half` is reachable, i.e. every positive element is larger than `half`. Two such elements would add up to more than `total`, so this happens only when there is at most one non-zero element, such as `[5]` or `[0, 7, 0]`.
</details>

3. Last Stone Weight II: compute the answer for `[1, 1, 4, 2, 2]` and give one smashing order that achieves it.

<details>
<summary>Answer</summary>

Total 10, half 5; 5 is reachable (`1 + 4`), so the answer is `10 − 10 = 0`. Groups: `{1, 4}` vs `{1, 2, 2}`. One order: smash 4 and 2 → 2; smash 2 and 2 → nothing; smash 1 and 1 → nothing. No stones remain: 0.
</details>
