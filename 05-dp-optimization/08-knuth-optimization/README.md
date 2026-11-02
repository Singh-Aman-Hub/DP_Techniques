# Knuth Optimization

> **Module:** 05 — DP Optimization · **Difficulty:** Advanced · **Estimated time:** 2–3 hours

## 1. What is this topic?

Knuth's optimization (often called Knuth–Yao) speeds up a family of **interval DPs** from O(n³) to **O(n²)**. It applies to recurrences of the form

```
dp[i][j] = min over i ≤ k < j of ( dp[i][k] + dp[k+1][j] ) + C(i, j)
```

where the extra cost `C(i, j)` depends only on the interval, not on the split point `k`. When `C` behaves "nicely", the best split point is **monotone**:

```
opt(i, j-1)  ≤  opt(i, j)  ≤  opt(i+1, j)
```

so each interval only needs to try the split points inside a small window, and all those windows together add up to O(n²) work.

## 2. Why does this concept exist?

Interval DP (module 04) tries every split point of every interval: about n²/2 intervals × n split points = O(n³). For n = 5000 that is ~2·10^10 steps — far too slow. Knuth observed (for optimal binary search trees, 1971) and Yao generalized (1980) that for many cost functions the optimal split points "move in one direction" as the interval grows. Using already-computed optimal splits as fences removes almost all useless candidates.

## 3. What problem does it solve?

- Merging adjacent piles where merging costs the total size (CSES Knuth Division, AtCoder Slimes).
- Cutting a stick or array where each cut costs the length / sum of the piece being cut (LeetCode 1547).
- Optimal binary search trees (cost = total access frequency of the subtree's keys).
- Any interval DP whose added cost satisfies the **quadrangle inequality** and **monotonicity** (section 4.9 of the walkthrough).

It does **not** apply when the added cost depends on `k` itself, as in Matrix Chain Multiplication (`p[i]·p[k]·p[j]`) or Burst Balloons.

## 4. Prerequisites

- [Interval DP](../../04-advanced-dp-patterns/03-interval-dp/) — states `dp[i][j]` over subarrays, filling by length.
- [Matrix Chain Multiplication](../../04-advanced-dp-patterns/04-matrix-chain-multiplication/) — the classic "try every split k" pattern.
- [Prefix and suffix optimization](../03-prefix-and-suffix-optimization/) — O(1) range sums with prefix sums.
- [Divide and conquer optimization](../05-divide-and-conquer-optimization/) — the first "monotone optimal split point" idea; Knuth is its interval-DP cousin.

## 5. What you will learn

- How to recognize the Knuth form `min_k dp[i][k] + dp[k+1][j] + C(i, j)`.
- What the quadrangle inequality and monotonicity of `C` say, in plain words, and how to check them on numbers.
- Why `opt(i, j-1) ≤ opt(i, j) ≤ opt(i+1, j)` holds (intuition and the theorem behind it).
- The telescoping-sum argument that turns the windows into O(n²) total work.
- Implementing the optimization and testing it against the O(n³) DP.

## 6. Important terminology

| Term | Meaning in plain English |
|---|---|
| Interval DP | DP whose state is a contiguous range `[i, j]`, solved from short ranges to long ranges. |
| Split point `k` | Where the interval is divided: left part `[i, k]`, right part `[k+1, j]`. |
| `opt(i, j)` | The (smallest) split point that gives the minimum for `dp[i][j]`. |
| Weight / cost `C(i, j)` | The extra price paid for the interval itself, independent of `k`. |
| Quadrangle inequality (QI) | For `a ≤ b ≤ c ≤ d`: `C(a,c) + C(b,d) ≤ C(a,d) + C(b,c)` — "two overlapping intervals cost at most the outer one plus the inner one". |
| Monotone on inclusion | If `[b, c]` is inside `[a, d]` then `C(b, c) ≤ C(a, d)`. |
| Telescoping sum | A sum where consecutive terms cancel, leaving only the first and last. |

## 7. Where this is used in real problems

- File / pile merging and "cut the array" problems with sum costs.
- Optimal binary search trees and optimal alphabetic codes.
- Text justification / segmentation variants with convex costs.
- Competitive-programming problems with n up to a few thousand on interval DPs.

## 8. After completing this topic you should be able to…

- [ ] Write the O(n³) interval DP for Knuth Division.
- [ ] Check the quadrangle inequality and monotonicity for a given `C` with small numbers.
- [ ] State the monotone split-point bounds and explain them intuitively.
- [ ] Prove the O(n²) bound with the telescoping argument.
- [ ] Implement Knuth's optimization with the right base case `opt[i][i] = i` and the clamp for length 2.
- [ ] Say why it does not apply to Matrix Chain Multiplication or Burst Balloons.

## 9. Practice problems

| # | Problem | Platform/ID | Difficulty | Pattern tested | Why it belongs here |
|---|---|---|---|---|---|
| 1 | [Minimum Score Triangulation of Polygon](https://leetcode.com/problems/minimum-score-triangulation-of-polygon/) | LeetCode 1039 | Medium | Interval DP whose cost depends on `k` | A recognition check: the cost uses the split vertex, so Knuth does **not** apply; solve it with plain O(n³). |
| 2 | [Slimes](https://atcoder.jp/contests/dp/tasks/dp_n) | AtCoder dp_n | Unrated (AtCoder) | Merge adjacent piles, cost = merged sum | The same recurrence as the walkthrough; n ≤ 400 lets the O(n³) DP pass, then rewrite it with Knuth. |
| 3 | [Minimum Cost to Cut a Stick](https://leetcode.com/problems/minimum-cost-to-cut-a-stick/) | LeetCode 1547 | Hard | Cut DP, cost = piece length | Exactly the Knuth form in boundary notation (exercise 3); good to practise translating the bounds. |
| 4 | [Knuth Division](https://cses.fi/problemset/task/2088) | CSES 2088 | Unrated (CSES) | Split cost = subarray sum, n ≤ 5000 | The walkthrough problem; O(n³) times out, Knuth's O(n²) passes. |
| 5 | [Minimum Cost to Merge Stones](https://leetcode.com/problems/minimum-cost-to-merge-stones/) | LeetCode 1000 | Hard | Merging K piles at a time | A harder relative of the merging DP; practise deciding whether the split structure still fits a standard optimization. |
| 6 | [Burst Balloons](https://leetcode.com/problems/burst-balloons/) | LeetCode 312 | Hard | Interval DP, "last element" choice | Another counterexample: the gain depends on `k`, so you must keep the O(n³) DP. |

## 10. Navigation

⬅ Previous: [SOS DP](../07-sos-dp/) · ➡ Next: [Matrix Exponentiation](../09-matrix-exponentiation/) · 📖 [Walkthrough](walkthrough.md) · 💻 [Solutions](solution/)
