# Transition Dependencies and Computation Order

> **Module:** 00 · Foundations · **Difficulty:** Beginner · **Estimated time:** 60–90 min

## 1. What is this topic?

Every DP recurrence says "to know the answer for **this** state, I first need the answers for **those** states". Those "I need" relationships are called **dependencies**. If you draw every state as a dot and every dependency as an arrow, you get a **dependency graph**.

This topic teaches you to draw that graph, to see that it never loops back on itself (it is a **DAG**, a directed acyclic graph), and to read off the **order** in which a bottom-up table must be filled: left to right, right to left, row by row, or by interval length.

## 2. Why does this concept exist?

In [memoization](../06-memoization/) the computer finds the right order for you: the recursion simply calls whatever it needs, and the call stack waits until the answer comes back.

In [tabulation](../07-tabulation/) **you** write the loops, so **you** choose the order. If you fill a cell before the cells it reads, Java does not complain. The unfilled cells still hold `0`, the formula happily adds those zeros, and you get a wrong answer with no error message. That is one of the most common DP bugs, and the cure is to look at the dependency arrows before writing a single loop.

## 3. What problem does it solve?

It answers derivation question 6 of the [framework](../16-the-dp-problem-solving-framework/): *"What is the order of computation?"* After this topic you can look at any recurrence such as

```
paths[r][c] = paths[r-1][c] + paths[r][c-1]
best[i]     = cost[i] + min(best[i+1], best[i+2])
lps[i][j]   = 2 + lps[i+1][j-1]   or   max(lps[i+1][j], lps[i][j-1])
```

and say immediately which way the loops must run, and why.

## 4. Prerequisites

- [06 · Memoization](../06-memoization/) and [07 · Tabulation](../07-tabulation/): you have seen both ways of filling a cache.
- [08 · Top-Down vs Bottom-Up](../08-top-down-vs-bottom-up/): you know the two directions of thinking.
- [09 · State Representation](../09-state-representation/): what one cell `dp[...]` means.
- [10 · State Transitions and Base Cases](../10-state-transitions-and-base-cases/): how a recurrence is built from choices.

## 5. What you will learn

- How to turn a recurrence into an ASCII **dependency graph** by drawing one arrow per term.
- Why a correct DP's dependency graph is always a **DAG**, and what goes wrong if it is not.
- The rule "**every arrow must point at a cell that is already filled**" and how it fixes the loop direction.
- Four common orders: **left to right**, **right to left**, **row by row** (or column by column), and **by length** (a preview of interval DP).
- How to predict, and then see in running code, the **wrong answer** that a wrong loop order produces.

## 6. Important terminology

| Term | Meaning in plain English |
|------|--------------------------|
| Dependency | State A *depends on* state B if the formula for A reads the answer of B. |
| Dependency graph | A picture with one dot per state and an arrow from each state to every state it reads. |
| DAG (directed acyclic graph) | A graph with one-way arrows and no cycles: you can never follow arrows and come back to where you started. |
| Topological order | Any order of the states in which every state appears **after** all the states it depends on. A valid tabulation loop is exactly a topological order. |
| Computation order | The order in which the loops visit and fill the table cells. |
| Interval DP | A family of DP problems whose state is a range `[i, j]` of the input; it is filled by increasing range length. Full lesson: [Interval DP](../../04-advanced-dp-patterns/03-interval-dp/). |

## 7. Where this is used in real problems

- **Left to right:** [Climbing Stairs](../../01-one-dimensional-dp/02-climbing-stairs/), [House Robber](../../01-one-dimensional-dp/06-house-robber/), every "prefix" DP.
- **Right to left:** "from position `i` to the end" formulations such as Solving Questions With Brainpower, or Min Cost Climbing Stairs written as a suffix.
- **Row by row:** [Unique Paths](../../02-two-dimensional-and-grid-dp/02-unique-paths/), Minimum Path Sum, and [0/1 Knapsack](../../03-subsequence-and-knapsack-dp/02-zero-one-knapsack/) (one row per item).
- **Bottom row up:** Triangle, where each row reads the row below it.
- **By length:** Longest Palindromic Subsequence, [Matrix Chain Multiplication](../../04-advanced-dp-patterns/04-matrix-chain-multiplication/), [Burst Balloons](../../04-advanced-dp-patterns/05-burst-balloons/).

## 8. After completing this topic you should be able to…

- [ ] Draw the dependency arrows of a 1D or 2D recurrence in ASCII.
- [ ] Explain why a DP recurrence must not have a cycle.
- [ ] Pick a loop direction for any recurrence by asking "which indices does it read: smaller or larger?".
- [ ] Recognize that several loop orders can be valid (row by row **and** column by column for grid paths).
- [ ] Predict the wrong value a wrong loop order produces, and recognize the "answer is 0 or suspiciously small" symptom.
- [ ] Explain why interval DP is filled by length.

## 9. Practice problems

| # | Problem | Platform/ID | Difficulty | Pattern tested | Why it belongs here |
|---|---------|-------------|------------|----------------|---------------------|
| 1 | [Climbing Stairs](https://leetcode.com/problems/climbing-stairs/) | LeetCode 70 | Easy | 1D, left to right | The simplest arrows: `i` reads `i-1` and `i-2`. |
| 2 | [Min Cost Climbing Stairs](https://leetcode.com/problems/min-cost-climbing-stairs/) | LeetCode 746 | Easy | 1D, either direction | Solve it twice: prefix form (left to right) and suffix form (right to left). |
| 3 | [Unique Paths](https://leetcode.com/problems/unique-paths/) | LeetCode 62 | Medium | 2D, row by row | Two arrows per cell; try both row-major and column-major order. |
| 4 | [Solving Questions With Brainpower](https://leetcode.com/problems/solving-questions-with-brainpower/) | LeetCode 2140 | Medium | 1D, right to left | The natural state reads *later* indices, so the loop must run backwards. |
| 5 | [Triangle](https://leetcode.com/problems/triangle/) | LeetCode 120 | Medium | 2D, bottom row up | Each row reads the row below it; filling top-down is wrong. |
| 6 | [Longest Palindromic Subsequence](https://leetcode.com/problems/longest-palindromic-subsequence/) | LeetCode 516 | Medium | Interval, by length | The worked "by length" example of this topic. |

## 10. Navigation

⬅ Previous: [10 · State Transitions and Base Cases](../10-state-transitions-and-base-cases/) · ➡ Next: [12 · Time and Space Complexity in DP](../12-time-and-space-complexity-in-dp/) · 📖 [Walkthrough](walkthrough.md) · 💻 [Solutions](solution/)
