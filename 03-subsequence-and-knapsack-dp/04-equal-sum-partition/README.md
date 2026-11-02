# Equal Sum Partition

> **Module:** 03 — Subsequence and Knapsack DP · **Difficulty:** Intermediate · **Estimated time:** 60 min

## 1. What is this topic?

Can an array of positive integers be split into **two groups with the same sum**? This topic shows how to **reduce** that question to [Subset Sum](../03-subset-sum/) with target `total / 2`, plus a one-line shortcut for odd totals. The judge problem is [Partition Equal Subset Sum](https://leetcode.com/problems/partition-equal-subset-sum/) (LeetCode 416).

## 2. Why does this concept exist?

Many interview problems are not new DPs at all — they are known DPs wearing a costume. The skill this topic trains is **reduction**: rewriting a problem with a little algebra until it becomes one you already know. "Two equal halves" sounds like it needs to track two groups at once, but one group decides the other, so tracking one sum is enough.

## 3. What problem does it solve?

- Splitting items fairly into two equal parts (load balancing, fair division, balanced teams).
- LeetCode 416 directly, and the counting version [Two Sets II](https://cses.fi/problemset/task/1093) (CSES 1093).

## 4. Prerequisites

- [Subset Sum](../03-subset-sum/) — the boolean DP we reduce to
- [0/1 Knapsack](../02-zero-one-knapsack/) — the reverse 1-D loop

## 5. What you will learn

- Why "two equal groups" ⇔ "one subset with sum `total / 2`".
- The odd-total shortcut and other quick rejections.
- The full brute force → memo → table → 1-D path on LeetCode's example.
- An early-exit trick for the 1-D version.

## 6. Important terminology

| Term | Meaning in plain English |
|---|---|
| Partition | Splitting the array into two groups so that every element is in exactly one group. |
| Complement | The elements *not* in a chosen subset. Choosing one group fixes the other as its complement. |
| Reduction | Turning a new problem into one you can already solve, by a short argument. |
| `half` | `total / 2`, the sum each group must reach. |

## 7. Where this is used in real problems

- Fair split of tasks between two machines or two people.
- The first check in [Partition to K Equal Sum Subsets](https://leetcode.com/problems/partition-to-k-equal-sum-subsets/) (total must divide by `k`).
- The "closest possible split" version is [topic 05](../05-partition-with-minimum-difference/).

## 8. After completing this topic you should be able to…

- [ ] Prove in two sentences that equal partition ⇔ subset with sum `total / 2`.
- [ ] Reject odd totals immediately and explain why.
- [ ] Solve LeetCode 416 with a one-array boolean DP and state its complexity from the constraints.
- [ ] Count equal partitions (Two Sets II) by switching `||` to `+` and handling the double counting.

## 9. Practice problems

| # | Problem | Platform/ID | Difficulty | Pattern tested | Why it belongs here |
|---|---|---|---|---|---|
| 1 | [Partition Equal Subset Sum](https://leetcode.com/problems/partition-equal-subset-sum/) | LeetCode 416 | Medium | Subset sum with `total/2` | The worked problem of this topic. |
| 2 | [Two Sets II](https://cses.fi/problemset/task/1093) | CSES 1093 | — | Count equal partitions (mod 10^9+7) | Same reduction, counting with `+`; each partition is found twice — see exercise 3. |
| 3 | [Last Stone Weight II](https://leetcode.com/problems/last-stone-weight-ii/) | LeetCode 1049 | Medium | Closest partition | When an equal split is impossible, how close can you get? Leads into topic 05. |
| 4 | [Partition to K Equal Sum Subsets](https://leetcode.com/problems/partition-to-k-equal-sum-subsets/) | LeetCode 698 | Medium | `k` groups | The two-group reduction no longer suffices; needs backtracking or bitmask DP. |

## 10. Navigation

⬅ Previous: [Subset Sum](../03-subset-sum/) · ➡ Next: [Partition With Minimum Difference](../05-partition-with-minimum-difference/) · 📖 [Walkthrough](walkthrough.md) · 💻 [Solutions](solution/)
