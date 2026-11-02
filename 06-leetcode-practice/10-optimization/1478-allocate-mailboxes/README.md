# 1478. Allocate Mailboxes

> **Pattern:** Partition DP with a precomputed median cost; Divide & Conquer optimization · **Builds on:** [Divide and Conquer Optimization](../../../05-dp-optimization/05-divide-and-conquer-optimization/) · **Difficulty:** Hard

## Problem overview

Houses stand at distinct positions on a street (`n ≤ 100`). You may build `k` mailboxes anywhere.
Each house walks to its **nearest** mailbox. Minimize the total walking distance.

Two facts turn this geometry question into a textbook partition DP:

1. After sorting, each mailbox serves a **contiguous** group of houses.
2. For one group, the best mailbox spot is the group's **median** house.

## Link & metadata

| Field | Value |
|---|---|
| LeetCode | [1478. Allocate Mailboxes](https://leetcode.com/problems/allocate-mailboxes/) |
| Difficulty | Hard |
| Pattern | `dp[b][j] = min over i of dp[b−1][i] + cost(i..j−1)`; cost = distance to the median; optimal split is monotone → D&C optimization |
| Prerequisite topic | [`05-dp-optimization/05-divide-and-conquer-optimization`](../../../05-dp-optimization/05-divide-and-conquer-optimization/); partition DP from [`04-advanced-dp-patterns/06-palindrome-partitioning`](../../../04-advanced-dp-patterns/06-palindrome-partitioning/) |
| Why this pattern | Cut a sorted array into k blocks, each block has an independent cost |

## Examples

| houses | k | Answer | One optimal choice |
|---|---|---|---|
| [1,4,8,10,20] | 3 | 5 | boxes at 4, 8 (or 10), 20: groups {1,4} {8,10} {20} → 3 + 2 + 0 |
| [2,3,5,12,18] | 2 | 9 | boxes at 3 and 12 (or anywhere in 12..18): {2,3,5} → 1+0+2, {12,18} → 6 |
| [7,4,6,1] | 1 | 8 | sorted [1,4,6,7], box at 4 or 6: 3+0+2+3 |
| [3,6,14,10] | 4 | 0 | one box per house |

## Brute-force intuition

Try every way to cut the sorted houses into `k` groups (`C(n−1, k−1)`), price each group. Up to
`C(99, 49) ≈ 5·10²⁸`: impossible.

As in every partition problem, the **last group** `i..n−1` leaves a smaller problem: the first `i`
houses with `k − 1` boxes. Its best answer does not depend on how we handle the last group: optimal
substructure, with heavy overlap between different cut sequences.

### Why contiguous groups, and why the median?

```
 sorted:  1    4         8   10              20
          ^----^         ^----^               ^
          box A          box B              box C
```

If a house `x` used box B while a house `y > x` used box A (to the left of B), swapping their
boxes never increases the total, so in an optimal answer groups never interleave. Inside a group,
moving the box one step toward the side with **more** houses lowers the sum; the movement stops at
the median.

### The median-cost table

`cost[i][j]` = total distance of houses `i..j` to their median. The outermost pair `(i, j)` is
always on opposite sides of the median and contributes exactly `houses[j] − houses[i]`, no
matter where between them the box is. So

```
cost[i][j] = cost[i+1][j−1] + (houses[j] − houses[i]),   cost[i][i] = 0
```

computed by increasing length. `O(n²)`, no sorting or median search per group.

## Deriving the DP (7 questions)

### 1. What is changing?
How many sorted houses are already served (a prefix of length `j`) and how many boxes are used (`b`).

### 2. What does the state represent?
`dp[b][j]` = the minimum total distance for the first `j` sorted houses using at most `b` boxes.
(Memoized form: `f(start, boxesLeft)` = best for houses `start..n−1` with `boxesLeft` boxes.)

### 3. What are the choices?
Where the last box's group begins: `i` in `[0, j−1]`; the group is houses `i..j−1`.

### 4. What is the recurrence?

```
dp[b][j] = min over 0 ≤ i < j of   dp[b−1][i]  +  cost[i][j−1]
```

### 5. What are the base cases?
`dp[b][0] = 0` (no houses, no cost); `dp[0][j > 0] = ∞` (houses but no box).

### 6. What is the order of computation?
Row `b` reads only row `b − 1`: loop `b = 1..k` outside. `cost` must be fully built first.

### 7. Can the state space be optimized?
**Space:** two rows. **Time:** let `opt[j]` be the best `i` for column `j` in row `b`. Because the
median cost satisfies the **quadrangle inequality**
(`cost(a, c) + cost(b, d) ≤ cost(a, d) + cost(b, c)` for `a ≤ b ≤ c ≤ d`, intuitively: adding a house
on the far right never makes an earlier split more attractive), `opt[j]` never decreases as `j`
grows. Divide & Conquer exploits this: solve the middle column, then search only
`[optLo, opt[mid]]` on the left half and `[opt[mid], optHi]` on the right half.

## Recursive solution

```java
int solve(int start, int boxesLeft) {
    if (start == n) return 0;
    if (boxesLeft == 0) return INF;
    int best = INF;
    for (int end = start; end < n; end++)
        best = Math.min(best, cost[start][end] + solve(end + 1, boxesLeft - 1));
    return best;
}
```

## Memoization

`memo[start][boxesLeft]`: `n · k` states, each looping `O(n)` ends: `O(n² k)`. See
[`solution/Memoization.java`](solution/Memoization.java).

## Tabulation

Rows `b = 1..k` over prefix length `j`, as in question 4:
[`solution/Tabulation.java`](solution/Tabulation.java). `O(n² k)` time.

### Divide & Conquer ([`solution/Optimized.java`](solution/Optimized.java))

```java
void computeRow(int lo, int hi, int optLo, int optHi) {
    if (lo > hi) return;
    int mid = (lo + hi) / 2;
    // best split for column mid, searching only [optLo, min(optHi, mid−1)]
    ...
    computeRow(lo, mid - 1, optLo, bestSplit);
    computeRow(mid + 1, hi, bestSplit, optHi);
}
```

Each recursion level scans `O(n)` candidate splits in total and there are `log n` levels, so a row
costs `O(n log n)`. `main` cross-checks it against the plain DP on 3000 random inputs.

## Space optimization

Row `b` uses only row `b − 1`: two arrays of length `n + 1`. The `cost` table stays `O(n²)`
(10 000 ints for `n = 100`).

## Dry run

`houses = [1, 4, 8, 10, 20]`, `k = 3`.

Median-cost table `cost[i][j]` (rows `i`, columns `j`):

```
        j=0  j=1  j=2  j=3  j=4
 i=0     0    3    7   13   25      cost[0][2] = cost[1][1] + (8 − 1) = 0 + 7
 i=1          0    4    6   18      cost[1][3] = cost[2][2] + (10 − 4) = 6
 i=2               0    2   12      cost[0][4] = cost[1][3] + (20 − 1) = 6 + 19 = 25
 i=3                    0   10
 i=4                         0
```

DP rows (`dp[b][j]`, `j` = number of houses) with the winning split `opt[j]`:

| b | j=0 | j=1 | j=2 | j=3 | j=4 | j=5 | opt[1..5] |
|---|---|---|---|---|---|---|---|
| 1 | 0 | 0 | 3 | 7 | 13 | 25 | 0 0 0 0 0 |
| 2 | 0 | 0 | 0 | 3 | 5 | 13 | 0 1 2 2 4 |
| 3 | 0 | 0 | 0 | 0 | 2 | **5** | 0 1 2 2 4 |

`dp[3][5] = dp[2][4] + cost[4][4] = 5 + 0`: houses {1,4} {8,10} cost 5 with two boxes, and 20 gets
its own box. Notice `opt` is non-decreasing in every row, the property D&C relies on.

## Complexity

| Approach | Time | Space | Reason |
|---|---|---|---|
| Brute force | O(C(n−1, k−1) · n) | O(k) | every cut set |
| Memoization | O(n² k) | O(n² + n k) | n·k states × n ends; cost table |
| Tabulation | O(n² k) | O(n²) | k rows × n columns × n splits |
| D&C optimization | O(n² + k n log n) | O(n²) | cost table, then n log n per row |

## Why the optimized approach is better

For `n = 100` the plain DP is already fast; the D&C version is the one that survives `n = 10⁵`
style variants (with an `O(1)` prefix-sum cost instead of the table). The pattern to remember:
**partition DP + cost satisfying the quadrangle inequality ⇒ monotone optimal split ⇒ D&C.**

## Common mistakes

- **Not sorting the houses.** The contiguity argument needs sorted positions; the input is not sorted.
- **Placing the box at the mean** instead of the median.
- **Recomputing each group's cost in O(n)** inside the DP: `O(n³ k)`. Precompute `cost`.
- **D&C with a wrong search window**: the split for column `mid` must be `< mid` (`min(optHi, mid−1)`).

## Key takeaway

Turn the geometry into "cut a sorted array into `k` groups, each with a closed-form cost", then use
the partition DP. When the optimal cut moves monotonically, divide and conquer cuts a factor of `n`
down to `log n`.

## Related problems

- [1335. Minimum Difficulty of a Job Schedule](../1335-minimum-difficulty-of-a-job-schedule/): same partition DP, block cost = max.
- [410. Split Array Largest Sum](https://leetcode.com/problems/split-array-largest-sum/): partition into k groups with a min-max objective.
- [1000. Minimum Cost to Merge Stones](https://leetcode.com/problems/minimum-cost-to-merge-stones/): interval DP cousin.
