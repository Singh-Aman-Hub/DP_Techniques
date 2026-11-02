# Top-Down vs Bottom-Up

> **Module:** 00 — Foundations · **Difficulty:** Beginner · **Estimated time:** 45–60 min

## 1. What is this topic?

The **same-example comparison**. One problem, Fibonacci, solved four ways, each in its own file with the same tests: plain recursion ([`BruteForce.java`](solution/BruteForce.java)), top-down DP ([`Memoization.java`](solution/Memoization.java)), bottom-up DP ([`Tabulation.java`](solution/Tabulation.java)), and bottom-up with O(1) space ([`Optimized.java`](solution/Optimized.java)). A side-by-side table then compares control flow, the states each version computes, stack use, how easy each is to write, and when to prefer each.

## 2. Why does this concept exist?

Topics [06](../06-memoization/) and [07](../07-tabulation/) taught memoization and tabulation separately. In practice you have to **choose** between them, and you will often write one and convert it into the other. Seeing all four versions on one problem, with identical inputs, makes their differences concrete instead of abstract.

## 3. What problem does it solve?

It gives you a decision rule: which form to write first, which to submit, and what the cost of each is. It also shows the standard progression you will follow on every problem in the course: **brute force → memoization → tabulation → space optimization**.

## 4. Prerequisites

- [06 — Memoization](../06-memoization/)
- [07 — Tabulation](../07-tabulation/)

## 5. What you will learn

- The four versions of Fibonacci, line by line, and what changes between consecutive versions.
- How each one runs on the same input (`n = 6`), with a dry run of the O(1)-space version.
- The comparison table: control flow, states computed, stack use, ease of writing, when to prefer each.
- An example where top-down computes far fewer states than bottom-up.
- How to cross-check your versions against each other.

## 6. Important terminology

| Term | Meaning in plain English |
|------|--------------------------|
| Top-down | Start from the original question, recurse to smaller ones on demand (memoization). |
| Bottom-up | Start from the base cases, build larger answers in a fixed order with loops (tabulation). |
| Control flow | Who decides the order of computation: the recursion (top-down) or your loop (bottom-up). |
| Reachable state | A state that the recursion actually asks for, starting from the original question. |
| Space optimization | Keeping only the part of the table that later cells still read (here: two variables). |
| Rolling variables | A small set of variables that "slide forward" each step, replacing a whole table. |
| Cross-check | Running two different implementations on the same inputs and confirming they agree. |

## 7. Where this is used in real problems

Every problem in Modules 01–05 is presented with the same four files, in the same order. In interviews, a common path is: explain the brute force, write the memoized version (quickest to get right), then convert to tabulation and optimize space if asked. In contests, bottom-up is usually preferred for speed and to avoid stack overflows.

## 8. After completing this topic you should be able to…

- [ ] Write all four versions of Fibonacci from memory.
- [ ] Explain exactly what changes between brute force → memo → table → two variables.
- [ ] Fill in the comparison table from memory and justify each entry.
- [ ] Choose top-down or bottom-up for a new problem and give a reason.
- [ ] Cross-check different versions of a solution with a shared set of tests.

## 9. Practice problems

Solve each problem **all four ways**, and make all versions pass the same tests.

| # | Problem | Platform/ID | Difficulty | Pattern tested | Why it belongs here |
|---|---------|-------------|------------|----------------|---------------------|
| 1 | [Fibonacci Number](https://leetcode.com/problems/fibonacci-number/) | LeetCode 509 | Easy | All four forms | This topic's example; type it yourself without looking. |
| 2 | [Climbing Stairs](https://leetcode.com/problems/climbing-stairs/) | LeetCode 70 | Easy | All four forms, counting | Same recurrence, different base cases. |
| 3 | [N-th Tribonacci Number](https://leetcode.com/problems/n-th-tribonacci-number/) | LeetCode 1137 | Easy | Space optimization with 3 variables | The rolling-variable trick with a wider window. |
| 4 | [Min Cost Climbing Stairs](https://leetcode.com/problems/min-cost-climbing-stairs/) | LeetCode 746 | Easy | All four forms with `min` | First "optimization" problem through the full progression. |

## 10. Navigation

⬅ Previous: [07 — Tabulation](../07-tabulation/) · ➡ Next: [09 — State Representation](../09-state-representation/) · 📖 [Walkthrough](walkthrough.md) · 💻 [Solutions](solution/)
