# Target Sum

> **Module:** 03 — Subsequence and Knapsack DP · **Difficulty:** Intermediate · **Estimated time:** 90 min

## 1. What is this topic?

Put a `+` or a `−` in front of every number in an array and count how many sign patterns make the expression equal a given `target`. Two DP views solve it: a direct memo on `(index, runningSum)` with an **offset** for negative sums, and an algebra trick that turns it into "**count subsets with sum `(total + target) / 2`**". The judge problem is [Target Sum](https://leetcode.com/problems/target-sum/) (LeetCode 494).

## 2. Why does this concept exist?

Two lessons live here. First, **counting DP**: the same knapsack-shaped table, but cells hold numbers of ways and choices are combined with `+`. Second, **algebraic reduction**: a short calculation turns a problem with signs and negative running sums into a plain subset-count with small non-negative indices. Both tricks reappear constantly.

## 3. What problem does it solve?

- Counting sign assignments (or "split into two groups with a given difference") that hit a target.
- Counting subsets with a given sum, including the tricky zeros.
- Any DP whose running quantity can go **negative**, via the offset technique.

## 4. Prerequisites

- [Subsequences Fundamentals](../01-subsequences-fundamentals/) — counting subsequences with sum `K`
- [Subset Sum](../03-subset-sum/) and [Equal Sum Partition](../04-equal-sum-partition/) — the `s1 + s2 = total` reduction

## 5. What you will learn

- The brute force over `2^n` sign patterns and why states `(i, runningSum)` repeat.
- Memoization with an **offset** so that negative sums can index an array.
- The algebra `s1 − s2 = target`, `s1 + s2 = total` ⇒ `s1 = (total + target) / 2`.
- The checks that must come first: `|target| ≤ total` and `total + target` even.
- Why zeros double the count, and how to make the DP count them correctly.

## 6. Important terminology

| Term | Meaning in plain English |
|---|---|
| Sign assignment | A choice of `+` or `−` for every number; there are `2^n`. |
| `P` / `N` groups | The numbers that got `+` (sum `s1`) and those that got `−` (sum `s2`). |
| Running sum | The value of the expression for the numbers signed so far; can be negative. |
| Offset | A constant added to a possibly negative value so it becomes a valid array index (`index = runningSum + total`). |
| Parity | Whether a number is even or odd. `s1 = (total + target)/2` must be a whole number, so `total + target` must be even. |
| Counting DP | A DP whose cells count ways; choices are combined with `+`. |

## 7. Where this is used in real problems

- Splitting into two groups with a prescribed difference.
- Counting subsets with a given sum, e.g. [Two Sets II](https://cses.fi/problemset/task/1093).
- Any "running total that can go below zero" DP: score differences in games, balance of brackets with weights, profit/loss tracking.

## 8. After completing this topic you should be able to…

- [ ] Derive `s1 = (total + target) / 2` and list the conditions under which the answer is 0 immediately.
- [ ] Write the offset memo for `(i, runningSum)` and say how many columns it needs.
- [ ] Count subsets with a given sum, with zeros handled correctly.
- [ ] Explain why a "stop as soon as the remaining sum is 0" base case undercounts when zeros are present.

## 9. Practice problems

| # | Problem | Platform/ID | Difficulty | Pattern tested | Why it belongs here |
|---|---|---|---|---|---|
| 1 | [Target Sum](https://leetcode.com/problems/target-sum/) | LeetCode 494 | Medium | Count subsets with sum `(total+target)/2`; offset memo | The worked problem of this topic. |
| 2 | [Number of Dice Rolls With Target Sum](https://leetcode.com/problems/number-of-dice-rolls-with-target-sum/) | LeetCode 1155 | Medium | Counting DP on `(dice used, running sum)` | Same "index + running sum, combine with `+`" shape, with `k` choices per step and a modulo. |
| 3 | [Two Sets II](https://cses.fi/problemset/task/1093) | CSES 1093 | — | Count subsets with a fixed sum, mod 10^9+7 | Target sum with target 0 on `1..n`; watch the double counting. |
| 4 | [Profitable Schemes](https://leetcode.com/problems/profitable-schemes/) | LeetCode 879 | Hard | Counting 0/1 knapsack with two dimensions | Count subsets under a member budget with profit clamped at `minProfit`. |

## 10. Navigation

⬅ Previous: [Partition With Minimum Difference](../05-partition-with-minimum-difference/) · ➡ Next: [Unbounded Knapsack](../07-unbounded-knapsack/) · 📖 [Walkthrough](walkthrough.md) · 💻 [Solutions](solution/)
