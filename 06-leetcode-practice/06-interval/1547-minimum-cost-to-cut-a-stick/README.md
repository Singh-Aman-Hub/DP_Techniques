# 1547. Minimum Cost to Cut a Stick

> **Pattern:** Interval DP over the sorted cut positions (the "last action inside a range" shape of Burst Balloons) · **Difficulty:** Hard · **Builds on:** [Interval DP](../../../04-advanced-dp-patterns/03-interval-dp/), [Burst Balloons](../../../04-advanced-dp-patterns/05-burst-balloons/)

## Problem overview

A wooden stick has length `n`, with marks at positions `0..n`. You get a list of positions where it must be cut. You may make the cuts in **any order**. Each cut costs the length of the piece being cut at that moment. Return the smallest possible total cost.

The order matters: cutting near the middle first makes the later cuts work on shorter pieces.

## Link & metadata

| Field | Value |
|---|---|
| Problem | [1547. Minimum Cost to Cut a Stick](https://leetcode.com/problems/minimum-cost-to-cut-a-stick/) |
| Difficulty | Hard |
| Pattern | Interval DP over cut indices with added endpoints `0` and `n` |
| Prerequisite topic | [04-advanced-dp-patterns/03-interval-dp](../../../04-advanced-dp-patterns/03-interval-dp/) |
| Also useful | [04-advanced-dp-patterns/05-burst-balloons](../../../04-advanced-dp-patterns/05-burst-balloons/), [04-advanced-dp-patterns/04-matrix-chain-multiplication](../../../04-advanced-dp-patterns/04-matrix-chain-multiplication/) |

## Examples

| n | cuts | Answer | One best order |
|---|---|---|---|
| 7 | `[1,3,4,5]` | 16 | cut 3 (7), then 5 (4), then 1 (3), then 4 (2) |
| 9 | `[5,6,1,4,2]` | 22 | |
| 10 | `[2,5]` | 15 | cut 5 (10), then 2 (5) |
| 2 | `[1]` | 2 | one cut on the whole stick |

Running example: `n = 7`, `cuts = [1,3,4,5]`. The order 1, 3, 4, 5 (left to right) costs 7 + 6 + 4 + 3 = 20, which is worse than 16.

## Brute-force intuition

Try every order of the `m` cuts: `m!` orders. Too many even for `m = 15`.

**Reframe:** look at one piece of stick between two points `a` and `b` (stick ends or cuts already made). Whatever happens inside it is independent of every other piece. Choose which cut inside it happens **first**: that cut costs `b − a` (the whole piece) and splits it into two independent smaller pieces. Pieces are described by **two cut positions**, so there are only O(m²) different pieces. That is interval DP.

**Trick:** sort the cuts and add the stick ends: `points = [0, sorted cuts..., n]`. A piece is now "between `points[i]` and `points[j]`", and the cuts inside it are exactly `points[i+1..j-1]`.

## Deriving the DP (7 questions)

### 1. What is changing?
The piece of stick we are working on, given by two indices `i < j` into `points`.

### 2. What does the state represent?
> `dp[i][j]` = the minimum cost to make **all** the cuts strictly between `points[i]` and `points[j]`, starting from the single piece `[points[i], points[j]]`.

### 3. What are the choices?
Which cut `k` (with `i < k < j`) is made **first** on this piece.

### 4. What is the recurrence?
```
dp[i][j] = (points[j] − points[i]) + min over i < k < j of ( dp[i][k] + dp[k][j] )
```
- `points[j] − points[i]`: the first cut always cuts the whole piece, whatever `k` is, so this term sits outside the `min`.
- `dp[i][k]` and `dp[k][j]`: the two independent pieces left after the cut.

Compare with [Burst Balloons](../../../04-advanced-dp-patterns/05-burst-balloons/): both use "pick one special action inside the open range `(i, j)`", and the ends `i`, `j` act as fixed walls. Here we pick the **first** cut. In Burst Balloons you pick the **last** balloon. In both, the chosen action splits the range into two independent parts.

### 5. What are the base cases?
`dp[i][i+1] = 0`: no cut lies strictly between two neighbouring points.

### 6. What is the order of computation?
`dp[i][j]` uses shorter index ranges `[i..k]` and `[k..j]`. Fill by increasing gap `j − i` (2, 3, …), or `i` downward with `j` upward. Answer: `dp[0][m+1]`, where `m` is the number of cuts.

### 7. Can the state space be optimized?
All shorter ranges of every start are needed, so the O(m²) table stays. **Time** can drop from O(m³) to O(m²) with [Knuth's optimization](../../../05-dp-optimization/08-knuth-optimization/), because the best `k` moves monotonically as the range grows. With `m ≤ 100` the plain O(m³) (one million steps) is already fast, so no `Optimized.java` is included.

## Recursive solution

```java
static int cost(int[] points, int i, int j) {
    if (j - i < 2) return 0;                                   // no cut inside
    int best = Integer.MAX_VALUE;
    for (int k = i + 1; k < j; k++)
        best = Math.min(best, cost(points, i, k) + cost(points, k, j));
    return best + points[j] - points[i];                       // the first cut costs the whole piece
}
```

## Memoization

Cache by `(i, j)` in an `(m+2) × (m+2)` table. Note that the state uses **indices into `points`**, not stick positions, so the table size does not depend on `n` (which can be 10⁶). See [`solution/Memoization.java`](solution/Memoization.java).

## Tabulation

```java
for (int gap = 2; gap < size; gap++)
    for (int i = 0; i + gap < size; i++) {
        int j = i + gap, best = Integer.MAX_VALUE;
        for (int k = i + 1; k < j; k++) best = Math.min(best, dp[i][k] + dp[k][j]);
        dp[i][j] = best + points[j] - points[i];
    }
```
See [`solution/Tabulation.java`](solution/Tabulation.java).

## Space optimization

Not applicable for space (see question 7). The important "optimization" is the state choice itself: indexing by cuts (O(m²) states) instead of stick positions (O(n²), up to 10¹² states).

## Dry run

`points = [0,1,3,4,5,7]` (indices 0..5).

| gap | (i, j) | piece | length | splits k → dp[i][k] + dp[k][j] | dp |
|---|---|---|---|---|---|
| 2 | (0,2) | 0–3 | 3 | k=1: 0+0 | 3 |
| 2 | (1,3) | 1–4 | 3 | k=2: 0+0 | 3 |
| 2 | (2,4) | 3–5 | 2 | k=3: 0+0 | 2 |
| 2 | (3,5) | 4–7 | 3 | k=4: 0+0 | 3 |
| 3 | (0,3) | 0–4 | 4 | k=1: 0+3, k=2: 3+0 → 3 | 7 |
| 3 | (1,4) | 1–5 | 4 | k=2: 0+2, k=3: 3+0 → 2 | 6 |
| 3 | (2,5) | 3–7 | 4 | k=3: 0+3, k=4: 2+0 → 2 | 6 |
| 4 | (0,4) | 0–5 | 5 | k=1: 0+6, k=2: 3+2, k=3: 7+0 → 5 | 10 |
| 4 | (1,5) | 1–7 | 6 | k=2: 0+6, k=3: 3+3, k=4: 6+0 → 6 | 12 |
| 5 | (0,5) | 0–7 | 7 | k=1: 0+12, k=2: 3+6, k=3: 7+3, k=4: 10+0 → 9 | **16** |

The winning first cut is `k = 2`, at position 3 (cost 7). It leaves `[0,3]` (cost 3) and `[3,7]` (cost 6): 7 + 3 + 6 = 16.

## Complexity

Let `m` = number of cuts.

| Approach | Time | Space | Why |
|---|---|---|---|
| All orders | O(m! · m) | O(m) | every permutation |
| Memoization | O(m³) | O(m²) | (m+2)²/2 ranges × up to m first cuts |
| Tabulation | O(m³ + m log m) | O(m²) | sort, then three loops |

## Why the optimized approach is better

Permutations treat "cut 1 then 4" and "cut 4 then 1" as different, even when they act on separate pieces and cannot affect each other. The DP notices that only the **current piece** matters, and there are just O(m²) pieces whose ends are cut positions.

## Common mistakes

1. **A DP over stick positions** (`dp[0..n][0..n]`). With `n` up to 10⁶ this cannot even be allocated.
2. **Forgetting to sort the cuts** or to add `0` and `n` as endpoints.
3. **Putting the length inside the `min` with a `k`-dependent value.** The piece length is the same for every first cut.
4. **Trusting a greedy such as "always cut nearest the middle".** No such rule is proven optimal here; the DP checks every first cut.

## Key takeaway

> When the order of operations matters but each operation splits the problem into independent parts, make the state "a range between two fixed walls" and choose the first (or last) operation inside it. Add sentinel walls (`0`, `n`) so every range has two ends.

## Related problems

- [1130. Minimum Cost Tree From Leaf Values](../1130-minimum-cost-tree-from-leaf-values/): split-point interval DP.
- [1039. Minimum Score Triangulation of Polygon](../1039-minimum-score-triangulation-of-polygon/): fixed walls `i`, `j` and a chosen middle vertex.
- [546. Remove Boxes](../546-remove-boxes/): an interval DP where the range alone is not enough.
- Course topic: [Burst Balloons](../../../04-advanced-dp-patterns/05-burst-balloons/)
