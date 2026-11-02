# Partition With Minimum Difference

> **Module:** 03 — Subsequence and Knapsack DP · **Difficulty:** Intermediate · **Estimated time:** 60–90 min

## 1. What is this topic?

Split an array of non-negative integers into two groups so that the **difference between the group sums is as small as possible**, and return that difference. When a perfect split exists the answer is 0 ([topic 04](../04-equal-sum-partition/)); otherwise we want the closest one. The trick: build the subset-sum table once and **read its whole last row**, looking for the reachable sum closest to `total / 2`.

## 2. Why does this concept exist?

A DP table is not only a way to get one answer — it holds the answers to many related questions at once. Topic 04 looked at one cell (`half`). This topic looks at a whole row. Learning to ask "what else does my table already know?" saves you from writing a new DP for every variation.

## 3. What problem does it solve?

- Fairest possible split of items into two groups.
- [Last Stone Weight II](https://leetcode.com/problems/last-stone-weight-ii/) (LeetCode 1049), which looks like a simulation problem but is exactly this one in disguise.

## 4. Prerequisites

- [Subset Sum](../03-subset-sum/) — the reachable-sums row
- [Equal Sum Partition](../04-equal-sum-partition/) — the "one group decides the other" reduction

## 5. What you will learn

- Why the difference of a split is `total − 2·s1`, so only `s1` matters.
- Why it is enough to look at `s1 ≤ total / 2`.
- Brute force over all splits, memoization on `(i, s1)`, and the "scan the last row" tabulation.
- Why Last Stone Weight II reduces to this problem.
- Where this approach **breaks**: negative numbers, huge values, or a fixed group size (LeetCode 2035).

## 6. Important terminology

| Term | Meaning in plain English |
|---|---|
| `s1`, `s2` | The sums of group 1 and group 2. `s2 = total − s1`. |
| Difference | `|s1 − s2| = |total − 2·s1|`. |
| Last row | `dp[n][·]` of the subset-sum table: which sums are reachable using any of the `n` elements. |
| Closest to half | The largest reachable `s1` with `s1 ≤ total/2`; it gives the smallest difference. |
| Meet in the middle | Split the input into two halves, enumerate each half's subsets (`2^(n/2)` each), then combine — a non-DP technique for small `n` with huge values. |

## 7. Where this is used in real problems

- Balancing work between two machines, two teams, two trucks.
- [Last Stone Weight II](https://leetcode.com/problems/last-stone-weight-ii/): every smash order ends up as a signed sum of the stones.
- The same "scan the last row" idea appears whenever a problem asks for the best *reachable* total under a limit.

## 8. After completing this topic you should be able to…

- [ ] Explain why minimizing `|s1 − s2|` means finding the reachable `s1 ≤ total/2` closest to `total/2`.
- [ ] Build the subset-sum row up to `total/2` and scan it from the top.
- [ ] Explain the reduction of Last Stone Weight II in your own words.
- [ ] Recognize when the values are too large or negative for this DP and name the alternative.

## 9. Practice problems

| # | Problem | Platform/ID | Difficulty | Pattern tested | Why it belongs here |
|---|---|---|---|---|---|
| 1 | [Money Sums](https://cses.fi/problemset/task/1745) | CSES 1745 | — | Build the full reachable-sums row | The same row this topic scans; good warm-up. |
| 2 | [Last Stone Weight II](https://leetcode.com/problems/last-stone-weight-ii/) | LeetCode 1049 | Medium | Minimum-difference partition in disguise | The judge problem for this topic ([`LastStoneWeightII.java`](solution/LastStoneWeightII.java)). |
| 3 | [Partition Array Into Two Arrays to Minimize Sum Difference](https://leetcode.com/problems/partition-array-into-two-arrays-to-minimize-sum-difference/) | LeetCode 2035 | Hard | Meet in the middle (not DP) | A **warning**: negative values up to 10^7 and equal group sizes break this DP; try it after reading Section 13 of the walkthrough. |

## 10. Navigation

⬅ Previous: [Equal Sum Partition](../04-equal-sum-partition/) · ➡ Next: [Target Sum](../06-target-sum/) · 📖 [Walkthrough](walkthrough.md) · 💻 [Solutions](solution/)
