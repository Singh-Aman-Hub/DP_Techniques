# Divide and Conquer Optimization

> **Module:** 05 — DP Optimization · **Difficulty:** Advanced · **Estimated time:** 2–3 hours

## 1. What is this topic?

Divide and Conquer optimization (often written "D&C DP") is a way to speed up one very common family of DP transitions, the **partition DP**:

```
dp[k][i] = min over j < i of ( dp[k-1][j] + cost(j, i) )
```

"Cut the first `i` items into `k` contiguous groups; the last group is items `j..i-1`." Computed directly, each layer `k` costs O(N²), so the whole DP costs O(K·N²). When the best cut point `j` moves only to the right as `i` grows, we can fill a whole layer in O(N log N) instead, for O(K·N log N) overall.

## 2. Why does this concept exist?

Partition DPs show up everywhere (split an array into `k` pieces, place `k` facilities, schedule jobs over `k` days). With N = 3000 and K = 3000 the direct O(K·N²) DP needs about 2.7·10¹⁰ steps, far too slow. Most of that work is wasted: for each `i` we try every `j`, even though the answer for `i` already tells us where the answer for `i+1` cannot be. D&C optimization uses that information.

## 3. What problem does it solve?

It turns an O(K·N²) partition DP into O(K·N log N) **when the optimal split point is monotone**: `opt(k, i) ≤ opt(k, i+1)`. A sufficient (and easy to check) condition is that `cost` satisfies the **quadrangle inequality**, which holds for costs such as "(sum of the group)²", "sum of distances to the group's median", and many "cost grows faster for bigger groups" functions.

## 4. Prerequisites

- [Partition-style interval and array DP](../../04-advanced-dp-patterns/06-palindrome-partitioning/) — the "where is the last cut?" way of thinking.
- [Interval DP](../../04-advanced-dp-patterns/03-interval-dp/) — states over ranges `[j, i)`.
- [Space optimization](../01-space-optimization/) and [rolling arrays](../02-rolling-arrays/) — we keep only layers `k-1` and `k`.
- [Prefix-sum tricks](../03-prefix-and-suffix-optimization/) — `cost(j, i)` must be O(1).
- Basic recursion and the idea of binary splitting (as in merge sort).

## 5. What you will learn

- How to recognize a partition DP `dp[k][i] = min_j dp[k-1][j] + cost(j, i)`.
- What "monotone optimal split point" means, seen on a real filled table.
- The quadrangle inequality in plain words, and why it forces monotonicity.
- The recursive `compute(lo, hi, optLo, optHi)` procedure and its O(N log N) per-layer analysis.
- How to test an optimized DP against its slow version with randomized cross-checks.

## 6. Important terminology

| Term | Meaning in plain English |
|------|--------------------------|
| Partition DP | A DP that cuts a sequence into contiguous groups and picks the best place for the last cut. |
| Layer | All states with the same number of groups `k`; layer `k` is computed from layer `k-1` only. |
| Split point / cut point `j` | Where the last group starts: the last group is items `j..i-1`. |
| `opt(k, i)` | The (leftmost) `j` that achieves the minimum for state `dp[k][i]`. |
| Monotone opt | `opt(k, i) ≤ opt(k, i+1)`: making the prefix longer never moves the best last cut to the left. |
| Quadrangle inequality | `cost(a, c) + cost(b, d) ≤ cost(a, d) + cost(b, c)` for `a ≤ b ≤ c ≤ d`: "two overlapping groups are cheaper than one big plus one small". |
| `compute(lo, hi, optLo, optHi)` | Fill states `lo..hi` of one layer knowing their best cuts lie in `[optLo, optHi]`. |

## 7. Where this is used in real problems

- Splitting an array into `k` subarrays to minimize a sum of convex group costs (CSES Subarray Squares).
- Placing `k` facilities on a line (mailboxes, schools, warehouses) to minimize total distance.
- Scheduling `n` jobs over `d` days where each day's cost depends on the jobs it covers.
- Many "minimize the sum over groups" competitive-programming problems with N up to 3000–10⁵ and K up to N.

## 8. After completing this topic you should be able to…

- [ ] Write the slow partition DP for a "split into k groups" problem.
- [ ] Explain, with a numeric example, why the best last cut moves right as the prefix grows.
- [ ] State the quadrangle inequality and check it for `cost = (sum)²`.
- [ ] Implement `compute(lo, hi, optLo, optHi)` and explain why each recursion level scans O(N) cut points.
- [ ] Cross-check the optimized DP against the slow one on random inputs.

## 9. Practice problems

| # | Problem | Platform/ID | Difficulty | Pattern tested | Why it belongs here |
|---|---------|-------------|------------|----------------|---------------------|
| 1 | [Minimum Difficulty of a Job Schedule](https://leetcode.com/problems/minimum-difficulty-of-a-job-schedule/) | LeetCode 1335 | Hard | Partition DP `dp[day][i]` with a max-of-group cost | The plain O(d·n²) partition DP; small limits, so practise the slow layer form first. |
| 2 | [Split Array Largest Sum](https://leetcode.com/problems/split-array-largest-sum/) | LeetCode 410 | Hard | Partition DP with a min-max objective | Same layered state as this topic; compare the DP to the binary-search solution. |
| 3 | [Allocate Mailboxes](https://leetcode.com/problems/allocate-mailboxes/) | LeetCode 1478 | Hard | Partition DP, cost = distance to the group median | The cost satisfies the quadrangle inequality, so D&C optimization applies directly. |
| 4 | [Subarray Squares](https://cses.fi/problemset/task/2086) | CSES 2086 | Unrated (CSES) | D&C optimization, cost = (group sum)² | The worked example of this topic; N, K ≤ 3000 needs O(K·N log N). |
| 5 | [Houses and Schools](https://cses.fi/problemset/task/2087) | CSES 2087 | Unrated (CSES) | D&C optimization, weighted facility placement | Harder cost function built from prefix sums; same `compute` skeleton. |

## 10. Navigation

⬅ Previous: [Monotonic Queue Optimization](../04-monotonic-queue-optimization/) · ➡ Next: [Convex Hull Trick](../06-convex-hull-trick/) · 📖 [Walkthrough](walkthrough.md) · 💻 [Solutions](solution/)
