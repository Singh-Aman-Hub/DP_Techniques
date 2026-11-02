# Memoization

> **Module:** 00 — Foundations · **Difficulty:** Beginner · **Estimated time:** 60–90 min

## 1. What is this topic?

**Memoization** is the first DP technique: keep your recursive brute-force function exactly as it is, and add a **cache** (the "memo") that stores the answer of every subproblem the first time it is computed. Before computing anything, the function **checks the cache**. Because the recursion still starts at the big question and works down to smaller ones, memoization is also called **top-down DP**.

## 2. Why does this concept exist?

[Topic 04](../04-overlapping-subproblems/) showed that plain recursion recomputes the same subproblems an exponential number of times: `fib(6)` makes 25 calls for 7 distinct values. Memoization removes all of that repeated work with a few extra lines, while keeping the recursive code that you already wrote and trust.

## 3. What problem does it solve?

It turns exponential-time recursive solutions into polynomial-time ones whenever subproblems overlap. For Fibonacci: O(2ⁿ) → O(n). In general the running time becomes **(number of distinct states) × (work per state)**.

## 4. Prerequisites

- [02 — Recursion Fundamentals](../02-recursion-fundamentals/)
- [03 — Recursion Trees and Brute Force](../03-recursion-trees-and-brute-force/)
- [04 — Overlapping Subproblems](../04-overlapping-subproblems/)
- [05 — Optimal Substructure](../05-optimal-substructure/)

## 5. What you will learn

- The three ingredients: a **cache**, a **sentinel** value meaning "not computed yet", and the **check-before-compute** pattern.
- How the recursion tree gets **pruned** to a thin chain, drawn as ASCII.
- A full dry run of `fib(6)` showing every cache hit.
- Exact complexity: why memoized `fib(n)` makes exactly 2n − 1 calls.
- Common bugs: the cache reset inside the function, a wrong sentinel, `int` overflow, and a few more.

## 6. Important terminology

| Term | Meaning in plain English |
|------|--------------------------|
| Memoization | Storing the result of each subproblem the first time it is computed, and returning the stored result on later calls. |
| Memo / cache | The storage itself, usually an array indexed by the state (`memo[n]`), sometimes a `HashMap`. |
| Sentinel | A special value that can never be a real answer, stored in empty cache cells to mean "not computed yet" (often `-1`). |
| Cache hit | A call that finds its answer already in the cache and returns it immediately. |
| Cache miss | A call that does not find its answer and has to compute it. |
| Top-down | Start from the original question and recurse down to smaller ones as needed. |
| Pruned tree | The recursion tree after memoization: repeated subtrees are cut off at their root, which becomes a cache hit. |
| Overflow | A number grows past what its type can hold (`int` max is 2,147,483,647) and wraps around to a wrong value. |

## 7. Where this is used in real problems

- Almost every DP problem in the course can be solved first with memoization: write the brute force, add a cache. Module 01 does this for each problem ([Fibonacci Number](../../01-one-dimensional-dp/01-fibonacci-number/), [Climbing Stairs](../../01-one-dimensional-dp/02-climbing-stairs/), …).
- Memoization shines when only **some** states are reachable (sparse states, e.g. in game problems or [digit DP](../../04-advanced-dp-patterns/11-digit-dp/)), because it only computes the states the recursion actually visits.
- Outside DP: caching function results in real software (web page caches, compilers, `functools.cache` in Python).

## 8. After completing this topic you should be able to…

- [ ] Convert a plain recursive function into a memoized one in three steps.
- [ ] Choose a safe sentinel for a problem and explain why it is safe.
- [ ] Draw the pruned recursion tree of memoized `fib(6)` and mark the cache hits.
- [ ] State and explain the time and space complexity of a memoized solution.
- [ ] Recognise and fix: cache created inside the recursion, wrong sentinel, overflow, cache key missing a parameter.

## 9. Practice problems

Solve each one first with plain recursion (it will time out on large inputs), then add memoization.

| # | Problem | Platform/ID | Difficulty | Pattern tested | Why it belongs here |
|---|---------|-------------|------------|----------------|---------------------|
| 1 | [Fibonacci Number](https://leetcode.com/problems/fibonacci-number/) | LeetCode 509 | Easy | Memoize a one-parameter recursion | Exactly this topic's code. |
| 2 | [Climbing Stairs](https://leetcode.com/problems/climbing-stairs/) | LeetCode 70 | Easy | Memoize a counting recursion | Brute force times out at n = 45; with a memo it is instant. |
| 3 | [N-th Tribonacci Number](https://leetcode.com/problems/n-th-tribonacci-number/) | LeetCode 1137 | Easy | Three base cases, three recursive calls | Same pattern, more base cases to get right. |
| 4 | [Min Cost Climbing Stairs](https://leetcode.com/problems/min-cost-climbing-stairs/) | LeetCode 746 | Easy | Memoize a `min` recursion | The recursion from [topic 05](../05-optimal-substructure/), made fast. |

## 10. Navigation

⬅ Previous: [05 — Optimal Substructure](../05-optimal-substructure/) · ➡ Next: [07 — Tabulation](../07-tabulation/) · 📖 [Walkthrough](walkthrough.md) · 💻 [Solutions](solution/)
