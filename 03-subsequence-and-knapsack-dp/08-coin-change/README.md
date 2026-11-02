# Coin Change

> **Module:** 03 — Subsequence and Knapsack DP · **Difficulty:** Intermediate · **Estimated time:** 90 min

## 1. What is this topic?

Given coin denominations (unlimited supply of each) and an amount, find the **fewest coins** that add up to exactly that amount, or report that it is impossible. It is [Unbounded Knapsack](../07-unbounded-knapsack/) with `min` instead of `max`, an **exact** target instead of "at most", and a special value for "impossible". The judge problem is [Coin Change](https://leetcode.com/problems/coin-change/) (LeetCode 322); the same DP solves [Minimizing Coins](https://cses.fi/problemset/task/1634) (CSES 1634).

## 2. Why does this concept exist?

Making change looks like a job for a simple rule — "always use the largest coin that fits" — and for everyday currencies that rule works. For arbitrary coin sets it fails, sometimes by giving too many coins and sometimes by not finding any answer at all. DP is the reliable method, and this problem is where most people first meet the practical details of **minimization DP**: sentinels for impossible states and protecting them from overflow.

## 3. What problem does it solve?

- Fewest coins (or fewest items of any kind) that hit an exact total, with unlimited reuse.
- It also answers "is the amount reachable at all?" — the answer is −1 exactly when it is not.

## 4. Prerequisites

- [Unbounded Knapsack](../07-unbounded-knapsack/) — "stay at `i`" and the forward loop
- [Subset Sum](../03-subset-sum/) — the "exactly" base row
- [Minimum Cost Climbing Stairs](../../01-one-dimensional-dp/03-minimum-cost-climbing-stairs/) — a first `min` recurrence

## 5. What you will learn

- The state `(coin index, remaining amount)` and its `min` recurrence with `1 +` for each coin used.
- How to represent "impossible" with an infinity sentinel, and two safe ways to avoid `INF + 1` overflowing.
- A full hand-filled table, and the equivalent 1-D form `dp[a] = 1 + min over coins of dp[a − coin]`.
- Why greedy fails, with two concrete counterexamples.

## 6. Important terminology

| Term | Meaning in plain English |
|---|---|
| Denomination | A coin value, such as 1, 3 or 4. Each can be used any number of times. |
| Amount | The exact total to pay. |
| Sentinel / `INF` | A special value meaning "impossible", chosen larger than any real answer. |
| Overflow | When a computation exceeds the largest `int` (2 147 483 647) and wraps to a negative number. |
| Greedy | Always making the locally best-looking choice (largest coin first) without reconsidering. |
| Canonical coin system | A coin set for which greedy happens to be optimal (like 1, 2, 5, 10, 20, 50, …). |

## 7. Where this is used in real problems

- Making change, paying with the fewest stamps, splitting a quantity into the fewest standard sizes.
- [Perfect Squares](https://leetcode.com/problems/perfect-squares/): coins are `1, 4, 9, 16, …`.
- Any "minimum number of steps where each step adds one of several fixed amounts".

## 8. After completing this topic you should be able to…

- [ ] Write the coin-change recurrence and base cases, including the impossible case.
- [ ] Pick a safe `INF` and explain why `amount + 1` works.
- [ ] Fill the table for a small input and read off both the answer and the coins used.
- [ ] Produce a counterexample to greedy and explain why DP does not fall for it.

## 9. Practice problems

| # | Problem | Platform/ID | Difficulty | Pattern tested | Why it belongs here |
|---|---|---|---|---|---|
| 1 | [Minimizing Coins](https://cses.fi/problemset/task/1634) | CSES 1634 | — | Fewest coins, unbounded | Same DP; amount up to 10^6 and 100 coins, so use the 1-D array. |
| 2 | [Coin Change](https://leetcode.com/problems/coin-change/) | LeetCode 322 | Medium | Fewest coins, `-1` if impossible | The worked problem of this topic. |
| 3 | [Perfect Squares](https://leetcode.com/problems/perfect-squares/) | LeetCode 279 | Medium | Coin change with coins = squares | You generate the coins yourself; always solvable because 1 is a square. |
| 4 | [Minimum Cost For Tickets](https://leetcode.com/problems/minimum-cost-for-tickets/) | LeetCode 983 | Medium | `min` over a few reusable options | Same "choose one of several steps, add its cost, take the minimum" shape over days. |
| 5 | [Coin Combinations I](https://cses.fi/problemset/task/1635) | CSES 1635 | — | Counting instead of minimizing | Swap `min`/`1+` for `+`; preview of [Coin Change II](../09-coin-change-ii/). |

## 10. Navigation

⬅ Previous: [Unbounded Knapsack](../07-unbounded-knapsack/) · ➡ Next: [Coin Change II](../09-coin-change-ii/) · 📖 [Walkthrough](walkthrough.md) · 💻 [Solutions](solution/)
