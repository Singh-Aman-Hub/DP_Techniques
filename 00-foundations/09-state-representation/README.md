# State Representation

> **Module:** 00 · Foundations · **Difficulty:** Beginner · **Estimated time:** 60–90 min

## 1. What is this topic?

Every DP solution is built on one decision you make before writing any code: **what is the state?**

A **state** is the smallest set of values that fully describes "the problem that is still left to solve". In Fibonacci the state was a single number `n`. In this topic you learn how to choose the state for *new* problems: how many parameters it needs, what each parameter means, and how to write it as one clear English sentence.

We use three running examples:

| Example | State | Number of parameters |
|---|---|---|
| Climbing Stairs | `ways(i)` | 1 (1D) |
| Grid paths (right/down moves) | `paths(r, c)` | 2 (2D) |
| 0/1 Knapsack | `best(i, remaining)` | 2 (2D) |

## 2. Why does this concept exist?

Memoization and tabulation (topics [06](../06-memoization/) and [07](../07-tabulation/)) only work if every subproblem has a **name** you can use as a cache key or a table index. The state *is* that name. If the name is too short, two different subproblems share one cache slot and you get wrong answers. If the name is too long, you get far more subproblems than needed and the solution becomes slow. Choosing the state well is the single most important skill in DP.

## 3. What problem does it solve?

It turns a vague question ("how many ways can I climb?") into a precise function with known inputs (`ways(i)`), so that:

- the recursion knows what to ask its children,
- the cache knows what to store,
- the table knows how many dimensions it needs,
- and you can count the subproblems to estimate the running time.

## 4. Prerequisites

- [02 · Recursion fundamentals](../02-recursion-fundamentals/)
- [04 · Overlapping subproblems](../04-overlapping-subproblems/)
- [05 · Optimal substructure](../05-optimal-substructure/)
- [06 · Memoization](../06-memoization/)
- [07 · Tabulation](../07-tabulation/)
- [08 · Top-down vs bottom-up](../08-top-down-vs-bottom-up/)

## 5. What you will learn

- What a state is: the minimum information that describes a remaining subproblem.
- How to find the parameters: list everything that *changes* as decisions are made.
- How to spot **too few** parameters (wrong answers) and **too many** parameters (wasted work).
- The difference between a 1D state (`i`) and a 2D state (`r, c` or `i, capacity`).
- How to write a state as a full English sentence, and why that sentence prevents bugs.

## 6. Important terminology

| Term | Meaning in plain English |
|---|---|
| State | The values that identify one subproblem, e.g. `(i)` or `(r, c)`. |
| State parameter | One of those values, e.g. `i` = "the step we want to reach". |
| State space | The set of all possible states; its size is how many subproblems exist. |
| Dimension | The number of parameters. One parameter → 1D table, two → 2D table. |
| Fixed input | Something that never changes during the recursion (the `cost` array, the grid size). It is *not* part of the state. |
| Derived value | Something that can be computed from other parameters. Usually leave it out of the state. |
| State sentence | `dp[i] = …` written as a complete English sentence that a stranger could understand. |

## 7. Where this is used in real problems

Every DP problem in this course starts with a state. 1D states appear in [Climbing Stairs](../../01-one-dimensional-dp/02-climbing-stairs/) and [House Robber](../../01-one-dimensional-dp/06-house-robber/). 2D grid states appear in [Unique Paths](../../02-two-dimensional-and-grid-dp/02-unique-paths/) and the rest of module 02. The `(item, capacity)` state is the heart of [0/1 Knapsack](../../03-subsequence-and-knapsack-dp/02-zero-one-knapsack/) and all of module 03. Harder problems add extra parameters such as "am I holding a stock?" ([State machine DP](../../04-advanced-dp-patterns/12-state-machine-dp/)) or "which items are used?" ([Bitmask DP](../../04-advanced-dp-patterns/09-bitmask-dp/)).

## 8. After completing this topic you should be able to…

- [ ] Explain what a state is without using the word "state".
- [ ] List the changing quantities of a problem and separate them from fixed inputs.
- [ ] Recognize when a state has too few parameters and show the bug it causes.
- [ ] Recognize when a parameter is redundant and remove it.
- [ ] Write the state for stairs, grid paths and knapsack as English sentences.
- [ ] Predict the size of the DP table from the state's parameter ranges.

## 9. Practice problems

For each problem, **only write the state sentence and the parameter ranges**. Solving them fully comes later in the course.

| # | Problem | Platform/ID | Difficulty | Pattern tested | Why it belongs here |
|---|---|---|---|---|---|
| 1 | [Climbing Stairs](https://leetcode.com/problems/climbing-stairs/) | LeetCode 70 | Easy | 1D state `(i)` | The simplest one-parameter state. |
| 2 | [Min Cost Climbing Stairs](https://leetcode.com/problems/min-cost-climbing-stairs/) | LeetCode 746 | Easy | 1D state + fixed input | Shows that the `cost` array is a fixed input, not part of the state. |
| 3 | [Frog 1](https://atcoder.jp/contests/dp/tasks/dp_a) | AtCoder dp_a | - | 1D state `(i)` | Same shape as stairs with a different meaning for the value. |
| 4 | [House Robber](https://leetcode.com/problems/house-robber/) | LeetCode 198 | Medium | 1D state `(i)` | Tempts you to add "did I rob the previous house?"; decide whether you need it. |
| 5 | [Unique Paths](https://leetcode.com/problems/unique-paths/) | LeetCode 62 | Medium | 2D state `(r, c)` | The grid example from this topic. |
| 6 | [Grid Paths I](https://cses.fi/problemset/task/1638) | CSES 1638 | - | 2D state `(r, c)` with blocked cells | Blocked cells are fixed input; the state stays `(r, c)`. |
| 7 | [Knapsack 1](https://atcoder.jp/contests/dp/tasks/dp_d) | AtCoder dp_d | - | 2D state `(i, capacity)` | The knapsack example from this topic. |

## 10. Navigation

⬅ Previous: [08 · Top-down vs bottom-up](../08-top-down-vs-bottom-up/) · ➡ Next: [10 · State transitions and base cases](../10-state-transitions-and-base-cases/) · 📖 [Walkthrough](walkthrough.md) · 💻 [Solutions](solution/)
