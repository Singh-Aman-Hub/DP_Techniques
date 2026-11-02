# Overlapping Subproblems

> **Module:** 00 — Foundations · **Difficulty:** Beginner · **Estimated time:** 45–60 min

## 1. What is this topic?

A problem has **overlapping subproblems** when solving it by recursion asks for the answer to the *same* smaller problem more than once. This is the first of the two properties that make dynamic programming useful (the second is [optimal substructure](../05-optimal-substructure/)). Here you learn to *see* the repeats in a recursion tree, *measure* them with a counter, and tell them apart from recursion where nothing repeats (merge sort).

## 2. Why does this concept exist?

DP saves time by storing answers and reusing them. Storing only pays off if an answer is **needed again**. Overlapping subproblems are the reason caching works; without them, a cache costs memory and saves nothing. So before reaching for DP you should be able to answer: "does my recursion solve the same subproblem twice?"

## 3. What problem does it solve?

It tells you **whether** DP can speed up a recursive solution, and **by how much**: the gap between the total number of calls (exponential for Fibonacci) and the number of *distinct* subproblems (only n + 1 for Fibonacci) is the work DP removes.

## 4. Prerequisites

- [03 — Recursion Trees and Brute Force](../03-recursion-trees-and-brute-force/) (drawing trees, counting nodes)

## 5. What you will learn

- How to find repeated nodes in the `fib(6)` recursion tree.
- How to count the calls per argument with a counter array (`CallCounter.java`), and read the resulting table.
- The difference between *total calls* and *distinct subproblems*, and why it predicts the DP speed-up.
- Why different choice sequences often lead to the same state (take/skip example).
- Why merge sort, binary search and other divide-and-conquer algorithms do **not** have overlapping subproblems.

## 6. Important terminology

| Term | Meaning in plain English |
|------|--------------------------|
| Subproblem | A smaller instance of the same question, identified by its arguments (e.g. `fib(3)`). |
| Distinct subproblem | One particular set of arguments, counted once no matter how often it is called. |
| Overlapping subproblems | The recursion asks for the same distinct subproblem more than once. |
| Independent (disjoint) subproblems | The subproblems never repeat; each part of the input is handled once (merge sort). |
| Divide and conquer | Split the input into separate parts, solve each part, combine. Typically no overlap. |
| Redundant work | Calls that recompute an answer that was already computed earlier. |

## 7. Where this is used in real problems

- Every DP problem has this property; checking for it is the first step of [the DP framework](../16-the-dp-problem-solving-framework/).
- Counting problems (Climbing Stairs, Decode Ways, Unique Paths): many choice sequences pass through the same position.
- Selection problems (knapsack, subset sum): different selections of earlier items leave the same remaining capacity.
- Not present in: merge sort, quick sort, binary search, tree traversal, generating all subsets (each output is different).

## 8. After completing this topic you should be able to…

- [ ] Mark every repeated node in the `fib(6)` tree and say how many times each subproblem is computed.
- [ ] Add a "calls per argument" counter to any recursive function.
- [ ] Explain the difference between 25 calls and 7 distinct subproblems for `fib(6)`.
- [ ] Show two different take/skip paths that reach the same `(index, remaining)` state.
- [ ] Explain why merge sort does not benefit from caching.

## 9. Practice problems

These are not about writing new code but about measuring: write the plain recursion, add a counter per argument, and look at the numbers.

| # | Problem | Platform/ID | Difficulty | Pattern tested | Why it belongs here |
|---|---------|-------------|------------|----------------|---------------------|
| 1 | [Fibonacci Number](https://leetcode.com/problems/fibonacci-number/) | LeetCode 509 | Easy | Counting repeated calls | Reproduce this topic's table for `n = 10`. |
| 2 | [Climbing Stairs](https://leetcode.com/problems/climbing-stairs/) | LeetCode 70 | Easy | Same states reached by different step sequences | Count how often `ways(2)` is called when `n = 10`. |
| 3 | [N-th Tribonacci Number](https://leetcode.com/problems/n-th-tribonacci-number/) | LeetCode 1137 | Easy | Three-way overlap | Three recursive calls per node: the repeats grow even faster than for Fibonacci. |

## 10. Navigation

⬅ Previous: [03 — Recursion Trees and Brute Force](../03-recursion-trees-and-brute-force/) · ➡ Next: [05 — Optimal Substructure](../05-optimal-substructure/) · 📖 [Walkthrough](walkthrough.md) · 💻 [Solutions](solution/)
