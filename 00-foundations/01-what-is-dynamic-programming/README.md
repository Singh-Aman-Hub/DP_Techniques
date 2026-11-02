# What is Dynamic Programming?

> **Module:** 00 — Foundations · **Difficulty:** Beginner · **Estimated time:** 45–60 min

## 1. What is this topic?

Your first look at dynamic programming (DP). No techniques yet, only the big idea: **when a problem keeps asking the same smaller question again and again, answer each smaller question once, write the answer down, and look it up next time.** We see this idea in everyday life, then in code, using the Fibonacci numbers.

## 2. Why does this concept exist?

Many problems are easiest to describe recursively: "the answer for size *n* is built from the answers for smaller sizes". A direct recursive program for such a description is often correct but painfully slow, because it recomputes the same smaller answers an exponential number of times. Computing `fib(50)` that way makes about 40 billion calls. DP exists to keep the simple recursive description while removing the repeated work. The same `fib(50)` then takes fewer than 100 steps.

## 3. What problem does it solve?

DP turns exponential-time "try everything" solutions into polynomial-time solutions (for example O(2ⁿ) → O(n)) for problems that have two properties:

1. **Overlapping subproblems:** the same smaller question is asked many times.
2. **Optimal substructure:** the answer to the big question can be built from the answers to smaller questions.

These two properties get one topic each later ([04](../04-overlapping-subproblems/) and [05](../05-optimal-substructure/)). Here you only need the intuition.

## 4. Prerequisites

- Java basics: variables, `if`, loops, arrays, methods.
- Having seen a function call itself at least once. If recursion is new to you, that is fine: the next topic, [02 — Recursion Fundamentals](../02-recursion-fundamentals/), teaches it from zero.

## 5. What you will learn

- What "dynamic programming" means, in plain words and through everyday analogies.
- Why the naive recursive Fibonacci program is slow, and how one small change (a notebook of answers) makes it fast.
- The two properties a problem needs for DP to apply.
- The two styles of DP (top-down and bottom-up), in one sentence each.
- The kinds of problems where DP shows up, and the path this course takes.

## 6. Important terminology

| Term | Meaning in plain English |
|------|--------------------------|
| Problem / subproblem | The question you want answered / a smaller version of the same question (e.g. `fib(5)` is a subproblem of `fib(7)`). |
| Recursion | A function that solves a problem by calling itself on smaller inputs. |
| Brute force | Solving a problem by trying every possibility, without being clever. |
| Overlapping subproblems | The same subproblem is needed many times while solving the big problem. |
| Optimal substructure | The best (or total) answer for a problem can be built from the answers of its subproblems. |
| Memoization | "Writing it on a memo": store each subproblem's answer the first time, reuse it after. Also called *top-down DP*. |
| Tabulation | Filling a table of answers from the smallest subproblems upward with loops. Also called *bottom-up DP*. |
| State | The set of values that identifies one subproblem (for Fibonacci, just `n`). |
| Recurrence | The formula that gives a state's answer from smaller states, e.g. `fib(n) = fib(n-1) + fib(n-2)`. |

## 7. Where this is used in real problems

- **Counting:** how many ways to climb stairs, decode a message, make change with coins, walk across a grid.
- **Optimizing:** minimum cost, maximum profit, shortest/longest something (house robber, knapsack, stock trading).
- **Comparing strings:** spell checkers and `diff` tools use edit distance and longest common subsequence; DNA alignment uses the same ideas.
- **Real systems:** route planning (shortest paths), text justification in word processors, speech recognition (Viterbi algorithm), resource allocation and scheduling.

## 8. After completing this topic you should be able to…

- [ ] Explain DP to a friend using a non-programming analogy.
- [ ] Say why the naive recursive Fibonacci is slow and why the cached one is fast.
- [ ] Name the two properties a problem needs for DP to work.
- [ ] Describe the difference between top-down (memoization) and bottom-up (tabulation) in one sentence each.
- [ ] Run `Demo.java` and explain the difference in the call counts it prints.

## 9. Practice problems

This topic is about the idea, not about technique, so there is one warm-up. Try writing the naive recursion and then add a notebook array, exactly like the walkthrough. (It is taught in full in [Module 01 → Fibonacci Number](../../01-one-dimensional-dp/01-fibonacci-number/).)

| # | Problem | Platform/ID | Difficulty | Pattern tested | Why it belongs here |
|---|---------|-------------|------------|----------------|---------------------|
| 1 | [Fibonacci Number](https://leetcode.com/problems/fibonacci-number/) | LeetCode 509 | Easy | Plain recursion → cached recursion | The exact example of this topic; submit both versions and compare their run times. |

## 10. Navigation

⬅ Previous: [Module 00 overview](../README.md) · ➡ Next: [02 — Recursion Fundamentals](../02-recursion-fundamentals/) · 📖 [Walkthrough](walkthrough.md) · 💻 [Solutions](solution/)
