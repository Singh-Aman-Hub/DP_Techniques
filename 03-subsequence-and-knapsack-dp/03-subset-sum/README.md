# Subset Sum

> **Module:** 03 — Subsequence and Knapsack DP · **Difficulty:** Beginner–Intermediate · **Estimated time:** 60–90 min

## 1. What is this topic?

Given positive integers and a **target** `T`, decide whether some subset of them adds up to **exactly** `T`. It is [0/1 Knapsack](../02-zero-one-knapsack/) with the value question replaced by a yes/no question: the target plays the role of the capacity, and the table stores `true`/`false` instead of a best value.

## 2. Why does this concept exist?

Many problems do not ask "what is the best?" but "is it possible?". Boolean DP answers feasibility questions with the same state and the same loops as knapsack, but with `||` (logical OR) instead of `max`. Its full last row also gives you **every** reachable sum at once, which topics 04 and 05 build on.

## 3. What problem does it solve?

- "Can these numbers make exactly `T`?" in `O(n · T)` instead of `O(2^n)`.
- "Which totals can these coins make?" — [Money Sums](https://cses.fi/problemset/task/1745) (CSES 1745), solved in this topic.
- The core check inside [Equal Sum Partition](../04-equal-sum-partition/) and [Partition With Minimum Difference](../05-partition-with-minimum-difference/).

## 4. Prerequisites

- [Subsequences Fundamentals](../01-subsequences-fundamentals/) — take/skip with a running sum
- [0/1 Knapsack](../02-zero-one-knapsack/) — the state `(i, capacity)` and the reverse 1-D loop

## 5. What you will learn

- How to turn the knapsack recurrence into a boolean recurrence with `||`.
- Why the base case is `dp[0][0] = true` and all other `dp[0][s] = false` ("exactly" vs "at most").
- How to memoize booleans (a `Boolean` object with `null` for "unknown").
- How the last row of the table lists every reachable sum, and how that solves Money Sums.
- The reverse loop for one boolean array, and the bug a forward loop causes.

## 6. Important terminology

| Term | Meaning in plain English |
|---|---|
| Target `T` | The exact total we want some subset to reach. |
| Reachable sum | A number `s` such that some subset adds up to exactly `s`. 0 is always reachable (empty subset). |
| Boolean DP | A DP whose cells hold `true`/`false`. Choices are combined with `||` (at least one choice works). |
| Feasibility question | "Is it possible?" as opposed to "what is the best?" or "how many?". |
| "Exactly" vs "at most" | Knapsack asks "weight at most W"; subset sum asks "sum exactly T". This changes the base row. |

## 7. Where this is used in real problems

- Can a set of tasks fill a time slot exactly? Can weights balance a scale?
- [Partition Equal Subset Sum](https://leetcode.com/problems/partition-equal-subset-sum/) is subset sum with `T = total / 2` (topic 04).
- [Last Stone Weight II](https://leetcode.com/problems/last-stone-weight-ii/) reads the whole reachable-sums row (topic 05).

## 8. After completing this topic you should be able to…

- [ ] Write `dp[i][s]` for subset sum in one sentence and give its recurrence and base row.
- [ ] Fill a boolean subset-sum table by hand.
- [ ] Explain why the 1-D boolean loop runs from `T` down to `value`.
- [ ] List all reachable sums of a set from the last row of the table.
- [ ] Tell from the constraints whether `O(n · T)` is fast enough.

## 9. Practice problems

| # | Problem | Platform/ID | Difficulty | Pattern tested | Why it belongs here |
|---|---|---|---|---|---|
| 1 | [Money Sums](https://cses.fi/problemset/task/1745) | CSES 1745 | — | All reachable sums | The last row of the subset-sum table *is* the answer; solved in [`MoneySums.java`](solution/MoneySums.java). |
| 2 | [Partition Equal Subset Sum](https://leetcode.com/problems/partition-equal-subset-sum/) | LeetCode 416 | Medium | Subset sum with `T = total/2` | One reduction step on top of this topic (taught in topic 04). |
| 3 | [Last Stone Weight II](https://leetcode.com/problems/last-stone-weight-ii/) | LeetCode 1049 | Medium | Reachable sums, closest to half | Uses the whole last row, not just one cell (taught in topic 05). |
| 4 | [Partition to K Equal Sum Subsets](https://leetcode.com/problems/partition-to-k-equal-sum-subsets/) | LeetCode 698 | Medium | Generalization beyond two groups | Shows the limit of subset sum: with `k` groups you need backtracking or bitmask DP ([Bitmask DP](../../04-advanced-dp-patterns/09-bitmask-dp/)). |

## 10. Navigation

⬅ Previous: [0/1 Knapsack](../02-zero-one-knapsack/) · ➡ Next: [Equal Sum Partition](../04-equal-sum-partition/) · 📖 [Walkthrough](walkthrough.md) · 💻 [Solutions](solution/)
