# Fibonacci Number

> **Module:** 01 · One-Dimensional DP · **Difficulty:** Beginner · **Estimated time:** 45–60 min

## 1. What is this topic?

The Fibonacci numbers are the sequence `0, 1, 1, 2, 3, 5, 8, 13, …`, where every number is the sum of the two before it. Computing the `n`-th one is the smallest real problem that DP solves, so this topic uses it to run the **complete 7-question framework** from start to finish for the first time.

You already met Fibonacci in the foundations module as an *illustration*. Here it is a *problem to solve*: you will write all four solution styles (brute force, memoization, tabulation, space-optimized) and check them against each other.

## 2. Why does this concept exist?

Every 1D DP problem in this module has the same skeleton: one changing index `i`, an answer for `i` built from a few smaller indices, a couple of base cases, and a loop from small to large. Fibonacci is that skeleton with nothing else attached. Once you can produce it on autopilot, later problems only change the *recurrence*, not the procedure.

## 3. What problem does it solve?

[LeetCode 509 · Fibonacci Number](https://leetcode.com/problems/fibonacci-number/): given `n`, return `F(n)`, where `F(0) = 0`, `F(1) = 1` and `F(n) = F(n-1) + F(n-2)`.

## 4. Prerequisites

- [Recursion fundamentals](../../00-foundations/02-recursion-fundamentals/)
- [Recursion trees and brute force](../../00-foundations/03-recursion-trees-and-brute-force/)
- [Memoization](../../00-foundations/06-memoization/) and [Tabulation](../../00-foundations/07-tabulation/)
- [Space optimization basics](../../00-foundations/15-space-optimization-basics/)
- [The DP problem-solving framework (7 questions)](../../00-foundations/16-the-dp-problem-solving-framework/)

## 5. What you will learn

- How to answer the 7 derivation questions on a real problem, in writing.
- How the same recurrence becomes four programs: recursion → memoization → tabulation → two variables.
- How to cross-check four implementations against the same tests.
- The "two rolling variables" pattern that most of this module ends with.

## 6. Important terminology

| Term | Meaning in plain English |
|------|--------------------------|
| State | One subproblem, named by the values that change. Here: a single integer `i`. |
| `dp[i]` | The stored answer for state `i`. Here: the `i`-th Fibonacci number. |
| Recurrence | The formula that builds `dp[i]` from smaller states: `dp[i] = dp[i-1] + dp[i-2]`. |
| Base case | A state whose answer we know without the recurrence: `dp[0] = 0`, `dp[1] = 1`. |
| Rolling variables | Keeping only the last few `dp` values in variables instead of a whole array. |
| 1D DP | DP where the state is a single index, so the table is a single array. |

## 7. Where this is used in real problems

The "answer at `i` = combination of answers at `i-1` and `i-2`" shape appears in Climbing Stairs, Min Cost Climbing Stairs, Frog Jump, House Robber, Decode Ways, and in tiling problems. Fibonacci-like recurrences also appear in counting binary strings without two consecutive 1s and in the analysis of algorithms.

## 8. After completing this topic you should be able to…

- [ ] Write the 7 answers for Fibonacci from memory.
- [ ] Draw the recursion tree for `fib(5)` and point out the repeated calls.
- [ ] Convert the recursion into memoization and then tabulation without looking.
- [ ] Reduce the table to two variables and explain why that is safe.
- [ ] Explain why brute force is `O(2^n)` but the DP is `O(n)`.

## 9. Practice problems

| # | Problem | Platform/ID | Difficulty | Pattern tested | Why it belongs here |
|---|---------|-------------|------------|----------------|---------------------|
| 1 | Fibonacci Number | [LeetCode 509](https://leetcode.com/problems/fibonacci-number/) | Easy | Two-term linear recurrence | The core problem of this topic; submit all four versions. |
| 2 | N-th Tribonacci Number | [LeetCode 1137](https://leetcode.com/problems/n-th-tribonacci-number/) | Easy | Three-term linear recurrence | Same skeleton with three previous states, so three rolling variables. |
| 3 | Fibonacci Numbers | [CSES 1722](https://cses.fi/problemset/task/1722) | — (challenge) | Fibonacci for `n` up to 10^18, modulo 10^9+7 | Shows where `O(n)` is too slow; solve it after [Matrix exponentiation](../../05-dp-optimization/09-matrix-exponentiation/). |

## 10. Navigation

⬅ Previous: [The DP problem-solving framework](../../00-foundations/16-the-dp-problem-solving-framework/) · ➡ Next: [Climbing Stairs](../02-climbing-stairs/) · 📖 [Walkthrough](walkthrough.md) · 💻 [Solutions](solution/)
