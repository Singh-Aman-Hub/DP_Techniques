# Tabulation

> **Module:** 00 — Foundations · **Difficulty:** Beginner · **Estimated time:** 60–90 min

## 1. What is this topic?

**Tabulation** is the second DP technique. Instead of starting at the big question and recursing down (memoization), you start at the **smallest** subproblems, write their answers into a **table** (an array), and fill the rest of the table **with a loop**, in an order where every cell's ingredients are already filled. When the loop finishes, the answer is in the last cell. Because it builds from the base cases upward, it is also called **bottom-up DP**.

## 2. Why does this concept exist?

Memoization ([topic 06](../06-memoization/)) is fast, but it still uses recursion: one stack frame per pending call, a cache check on every call, and a `StackOverflowError` when the recursion is very deep (for example `n = 1,000,000`). Tabulation computes the same values with a plain `for` loop: no recursion, no stack, no "is it cached?" check. It also makes the order of computation explicit, which is what later enables space optimization.

## 3. What problem does it solve?

- It removes recursion overhead and the risk of stack overflow.
- It computes every state exactly once in a predictable order, which makes the running time obvious (count the loop iterations).
- It sets up [space optimization](../15-space-optimization-basics/): once you see which earlier cells each cell reads, you can throw the others away.

## 4. Prerequisites

- [05 — Optimal Substructure](../05-optimal-substructure/) (the recurrence is correct)
- [06 — Memoization](../06-memoization/) (the same states, cached top-down)

## 5. What you will learn

- How to turn a recurrence into a table: meaning of a cell, table size, base cells, loop.
- How to choose the **loop order**, and what happens when it is wrong.
- An ASCII table for `fib(6)` filled step by step, and the same for Climbing Stairs.
- Why tabulation has no recursion overhead, and what it costs instead.
- The pitfalls: off-by-one table sizes, unhandled tiny inputs, wrong loop direction.

## 6. Important terminology

| Term | Meaning in plain English |
|------|--------------------------|
| Tabulation | Computing all subproblem answers in a table with loops, smallest first. |
| Bottom-up | Starting from the base cases and building towards the original question. |
| DP table / `dp` array | The array that holds the answers; `dp[i]` is the answer for state `i`. |
| Cell | One entry of the table, i.e. one state's answer. |
| Base cells | The cells filled directly from the base cases before the loop starts. |
| Loop order / fill order | The order in which cells are computed; it must put every cell after the cells it reads. |
| Dependency | A cell that another cell reads (`dp[i]` depends on `dp[i-1]` and `dp[i-2]`). |
| Recursion overhead | The extra cost of function calls: creating frames, passing arguments, returning. |

## 7. Where this is used in real problems

- The standard final form of most DP solutions in interviews and contests; every problem in Modules 01–05 has a `Tabulation.java`.
- Essential when the input is large: [CSES Dice Combinations](https://cses.fi/problemset/task/1633) has n up to 10⁶ — a memoized recursion that deep overflows the default stack, a loop does not.
- 2-D tables for grids and two strings ([Unique Paths](../../02-two-dimensional-and-grid-dp/02-unique-paths/), [Longest Common Subsequence](../../03-subsequence-and-knapsack-dp/12-longest-common-subsequence/)), where the loop order is the key design decision ([topic 11](../11-transition-dependencies-and-computation-order/)).

## 8. After completing this topic you should be able to…

- [ ] Write the tabulated version of Fibonacci and Climbing Stairs from the recurrence alone.
- [ ] State what one cell means, how big the table is, and which cells are base cells.
- [ ] Choose a correct loop direction by looking at which cells the recurrence reads.
- [ ] Draw the table being filled, step by step, for a small input.
- [ ] Explain the advantages and costs of tabulation compared with memoization.

## 9. Practice problems

| # | Problem | Platform/ID | Difficulty | Pattern tested | Why it belongs here |
|---|---------|-------------|------------|----------------|---------------------|
| 1 | [Fibonacci Number](https://leetcode.com/problems/fibonacci-number/) | LeetCode 509 | Easy | 1-D table, two base cells | This topic's main example. |
| 2 | [Climbing Stairs](https://leetcode.com/problems/climbing-stairs/) | LeetCode 70 | Easy | Counting table | Second example of this topic; write it without recursion. |
| 3 | [N-th Tribonacci Number](https://leetcode.com/problems/n-th-tribonacci-number/) | LeetCode 1137 | Easy | Three base cells | Practice sizing the table and guarding tiny inputs. |
| 4 | [Min Cost Climbing Stairs](https://leetcode.com/problems/min-cost-climbing-stairs/) | LeetCode 746 | Easy | `min` table | Tabulate the recurrence from [topic 05](../05-optimal-substructure/). |
| 5 | [Dice Combinations](https://cses.fi/problemset/task/1633) | CSES 1633 | Intro | Table with 6 dependencies, modulo | n up to 10⁶: recursion is too deep, a loop is required. |

## 10. Navigation

⬅ Previous: [06 — Memoization](../06-memoization/) · ➡ Next: [08 — Top-Down vs Bottom-Up](../08-top-down-vs-bottom-up/) · 📖 [Walkthrough](walkthrough.md) · 💻 [Solutions](solution/)
