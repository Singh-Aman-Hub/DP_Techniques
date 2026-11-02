# Recursion Trees and Brute Force

> **Module:** 00 — Foundations · **Difficulty:** Beginner · **Estimated time:** 60–90 min

## 1. What is this topic?

A **recursion tree** is a picture of every call a recursive function makes: each call is a node, and each call it makes is a child. This topic teaches you to draw these trees, count their nodes, and turn that count into a running time such as O(2ⁿ). It also introduces **brute force** in its most useful form for DP: "at every step, try every choice", most often **take or skip**.

## 2. Why does this concept exist?

The call stack ([topic 02](../02-recursion-fundamentals/)) shows what is happening *at one moment*. The recursion tree shows *everything that will ever happen*. It is the tool that lets you see how much work a recursive function does, and — in the next topic — spot the repeated work that DP removes.

## 3. What problem does it solve?

- **Estimating time:** the number of nodes in the tree is the number of calls, which (times the work per call) is the running time.
- **Designing brute force:** a correct, slow, exhaustive solution is the starting point of every DP solution in this course. Brute force tells you the state, the choices and the base cases; DP only makes it fast.

## 4. Prerequisites

- [02 — Recursion Fundamentals](../02-recursion-fundamentals/) (base case, recursive case, call stack, leap of faith)

## 5. What you will learn

- How to draw a recursion tree, by hand and with a program (`RecursionTreeTracer.java`).
- How to count its nodes: by level, and with a recurrence for the count.
- Why plain recursive Fibonacci takes O(2ⁿ) time (and the more precise ≈ 1.618ⁿ).
- Brute force as "try every choice": the take/skip pattern on a small array.
- Using the same tree to **count** solutions (`+`) or find the **best** one (`max`).

## 6. Important terminology

| Term | Meaning in plain English |
|------|--------------------------|
| Recursion tree | A drawing with one node per function call; a node's children are the calls it makes. |
| Root | The first call (the original question). |
| Leaf | A call that makes no further calls (a base case). |
| Level / depth | How many calls separate a node from the root. The root is at level 0. |
| Branching factor | How many children a node has (2 for Fibonacci and take/skip). |
| Brute force | Trying every possible sequence of choices and checking each outcome. |
| Take/skip | The choice "include this element in my selection or not". Over n elements it produces all 2ⁿ subsets. |
| Subset | Any selection of elements from the array (possibly none, possibly all). |
| Exponential time | Running time like O(2ⁿ): adding one element to the input multiplies the work. |

## 7. Where this is used in real problems

- Every DP problem in this course starts as a brute-force recursion with a tree like these.
- Take/skip is the backbone of [House Robber](../../01-one-dimensional-dp/06-house-robber/), [0/1 Knapsack](../../03-subsequence-and-knapsack-dp/02-zero-one-knapsack/), [Subset Sum](../../03-subsequence-and-knapsack-dp/03-subset-sum/), [Target Sum](../../03-subsequence-and-knapsack-dp/06-target-sum/) and all subsequence problems ([Subsequences Fundamentals](../../03-subsequence-and-knapsack-dp/01-subsequences-fundamentals/)).
- Backtracking problems (generating subsets, permutations, N-Queens) are recursion trees explored on purpose.

## 8. After completing this topic you should be able to…

- [ ] Draw the recursion tree for `fib(5)` and for take/skip on a 3-element array.
- [ ] Count the nodes of a recursion tree level by level, and with a recurrence.
- [ ] Explain why plain recursive Fibonacci is exponential, with both an upper bound (2ⁿ) and a lower bound (2^(n/2)).
- [ ] Write a take/skip brute force that counts subsets with a given sum, and one that maximizes a total under a limit.
- [ ] Explain why brute force is still worth writing even though it is slow.

## 9. Practice problems

| # | Problem | Platform/ID | Difficulty | Pattern tested | Why it belongs here |
|---|---------|-------------|------------|----------------|---------------------|
| 1 | [Fibonacci Number](https://leetcode.com/problems/fibonacci-number/) | LeetCode 509 | Easy | Two-branch recursion tree | Draw the tree for `n = 5`, count its nodes, check against 2·fib(n+1) − 1. |
| 2 | [Climbing Stairs](https://leetcode.com/problems/climbing-stairs/) | LeetCode 70 | Easy | Choice tree (1 step / 2 steps) | Write the brute force and watch it time out for large `n`: the tree is the reason. Fix it in [topic 06](../06-memoization/). |
| 3 | [Subsets](https://leetcode.com/problems/subsets/) | LeetCode 78 | Medium | Take/skip, every leaf is an answer | The take/skip tree of this topic with "collect the leaf" instead of "count the leaf". Not DP — it needs all 2ⁿ outputs. |
| 4 | [Dice Combinations](https://cses.fi/problemset/task/1633) | CSES 1633 | Intro | Six-way choice tree, counting | Brute force for small `n` (each throw has six choices); see how fast the tree explodes. Solved with DP in Module 01. |

## 10. Navigation

⬅ Previous: [02 — Recursion Fundamentals](../02-recursion-fundamentals/) · ➡ Next: [04 — Overlapping Subproblems](../04-overlapping-subproblems/) · 📖 [Walkthrough](walkthrough.md) · 💻 [Solutions](solution/)
