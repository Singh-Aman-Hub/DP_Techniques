# Min Cost Climbing Stairs

> **Module:** 01 · One-Dimensional DP · **Difficulty:** Beginner · **Estimated time:** 60–90 min

## 1. What is this topic?

The same staircase as [Climbing Stairs](../02-climbing-stairs/), but now every step has a price that you pay when you leave it, and you want the **cheapest** way to the top instead of the **number** of ways. It is the first **optimization DP** in the course: the recurrence uses `min` instead of `+`.

## 2. Why does this concept exist?

Many problems ask for the best (cheapest, shortest, largest) way to do something, not for the number of ways. The trick that makes DP work for them is the same: split by the last move. The new part is *how to combine*: among the possible last moves, keep only the best one.

The problem also has a classic trap: the "top of the floor" is **one position past the last array index**. Getting that index right is a skill you will need in almost every 1D problem.

## 3. What problem does it solve?

[LeetCode 746 · Min Cost Climbing Stairs](https://leetcode.com/problems/min-cost-climbing-stairs/): given the price of each step, start on step 0 or step 1 for free, move 1 or 2 steps at a time paying the price of the step you leave, and minimize the total price to get past the last step.

## 4. Prerequisites

- [Climbing Stairs](../02-climbing-stairs/) — the same moves, counted instead of optimized.
- [Optimal substructure](../../00-foundations/05-optimal-substructure/) — why "best overall" can be built from "best for smaller states".
- [State transitions and base cases](../../00-foundations/10-state-transitions-and-base-cases/)

## 5. What you will learn

- How a `min` recurrence differs from a counting `+` recurrence.
- How to place the "top of the floor" at index `n` and size the table `n + 1`.
- Why "start on step 0 or 1 for free" becomes two base cases equal to 0.
- How to reconstruct the cheapest path from the filled table.
- Two equivalent state definitions (cost *to reach* `i` vs. cost *from* `i` to the top).

## 6. Important terminology

| Term | Meaning in plain English |
|------|--------------------------|
| Optimization DP | A DP whose answer is a best value (min or max), built with `min`/`max`. |
| Top of the floor | The position just above the last step, index `n` when steps are `0..n-1`. |
| Off-by-one error | Being wrong by exactly one index, here usually returning `dp[n-1]` instead of `dp[n]`. |
| Optimal substructure | The best path to `i` contains a best path to the step it came from. |
| Path reconstruction | Walking back through the table to recover *which* choices produced the best value. |

## 7. Where this is used in real problems

Cheapest route through positions in a line, minimum number of operations to reduce a number, minimum cost to cover days with tickets, and every "minimum cost path" problem in the [grid module](../../02-two-dimensional-and-grid-dp/04-minimum-path-sum/).

## 8. After completing this topic you should be able to…

- [ ] Define `reach[i]` precisely, including what `reach[n]` means.
- [ ] Explain why both `reach[0]` and `reach[1]` are 0.
- [ ] Write the `min` recurrence and explain each term.
- [ ] Reconstruct the cheapest path from the table.
- [ ] Solve the problem again with the "cost from `i` to the top" state.

## 9. Practice problems

| # | Problem | Platform/ID | Difficulty | Pattern tested | Why it belongs here |
|---|---------|-------------|------------|----------------|---------------------|
| 1 | Min Cost Climbing Stairs | [LeetCode 746](https://leetcode.com/problems/min-cost-climbing-stairs/) | Easy | `min` over the last move, top at index `n` | The core problem of this topic. |
| 2 | Removing Digits | [CSES 1637](https://cses.fi/problemset/task/1637) | — | `min` over a variable set of moves | Each number's "moves" are its digits; same "min over last move" shape. |
| 3 | Perfect Squares | [LeetCode 279](https://leetcode.com/problems/perfect-squares/) | Medium | `min` over moves of size `j²` | Like climbing with step sizes 1, 4, 9, …, minimizing the number of steps. |
| 4 | Minimum Cost For Tickets | [LeetCode 983](https://leetcode.com/problems/minimum-cost-for-tickets/) | Medium | `min` over three "jump" options with prices | Each ticket is a move that covers 1, 7 or 30 days for a price. |

## 10. Navigation

⬅ Previous: [Climbing Stairs](../02-climbing-stairs/) · ➡ Next: [Frog Jump](../04-frog-jump/) · 📖 [Walkthrough](walkthrough.md) · 💻 [Solutions](solution/)
