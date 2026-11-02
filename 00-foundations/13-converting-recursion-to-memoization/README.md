# Converting Recursion to Memoization

> **Module:** 00 · Foundations · **Difficulty:** Beginner · **Estimated time:** 60–90 min

## 1. What is this topic?

A **mechanical recipe** that turns a correct but slow recursive solution into a fast memoized one, without rethinking the problem:

```
1. Identify the parameters that change between calls (and their ranges).
2. Create a cache sized by those ranges, filled with a "not computed yet" marker.
3. Check the cache at the start of the function (after the base cases).
4. Store the result in the cache before returning it.
```

You will apply it, line by line, to **Climbing Stairs** and to **counting grid paths** (Unique Paths style), and learn how to handle parameters that can be **negative** or that do not start at 0, using an **offset** or a `HashMap`.

## 2. Why does this concept exist?

In [06 · Memoization](../06-memoization/) you saw memoization applied to Fibonacci. Real problems have more parameters, odd ranges, and answers that can be 0 or negative, and beginners often get the conversion slightly wrong: the cache is one cell too small, the "empty" marker collides with a real answer, or a negative index crashes the program.

A recipe you can follow the same way every time removes those mistakes. It also lets you work in two clean steps: first get **any** correct recursion (thinking), then memoize it (mechanical).

## 3. What problem does it solve?

It turns exponential-time recursions into polynomial-time ones. For Climbing Stairs with `n = 45`, the brute force makes about 3.7 billion calls; the memoized version computes 44 states. For a 10 × 10 grid, the brute force makes 97 239 calls; the memoized version fills at most 100 cells.

## 4. Prerequisites

- [02 · Recursion Fundamentals](../02-recursion-fundamentals/) and [03 · Recursion Trees and Brute Force](../03-recursion-trees-and-brute-force/).
- [04 · Overlapping Subproblems](../04-overlapping-subproblems/): why the recursion repeats work.
- [06 · Memoization](../06-memoization/): the idea of a cache, on Fibonacci.
- [09 · State Representation](../09-state-representation/) and [10 · State Transitions and Base Cases](../10-state-transitions-and-base-cases/).
- [12 · Time and Space Complexity in DP](../12-time-and-space-complexity-in-dp/): how to count states.

## 5. What you will learn

- The 4-step recipe, plus the one condition the recursion must meet first (it must be a **pure function** of its parameters).
- How to read the cache **shape and size** directly from the parameter ranges.
- How to choose a safe **sentinel** ("not computed yet") value.
- Before/after code for Climbing Stairs and grid paths, with a dry run of every cache hit.
- How to memoize parameters that are **negative** (offset, `HashMap`) or start at `-1` or `1`.

## 6. Important terminology

| Term | Meaning in plain English |
|------|--------------------------|
| Memoization | Saving the answer of each subproblem the first time it is computed, and reusing it afterwards. |
| Cache / memo | The array (or map) that stores those saved answers. |
| Changing parameter | A function parameter whose value differs between recursive calls (like `n` or `(r, c)`). Only these go into the cache key. |
| Pure function | A function whose result depends **only** on its parameters, not on global variables that change. Only pure functions can be memoized safely. |
| Sentinel | A special value, like `-1`, that means "this cell has not been computed yet". It must never be a real answer. |
| Cache hit / miss | A hit finds a stored answer and returns it at once; a miss has to compute it. |
| Offset | A constant added to a parameter so that its smallest possible value lands on array index 0. |

## 7. Where this is used in real problems

Almost every top-down DP solution you will write in this course starts as a recursion and is converted with this recipe: [Climbing Stairs](../../01-one-dimensional-dp/02-climbing-stairs/), [House Robber](../../01-one-dimensional-dp/06-house-robber/), [Unique Paths](../../02-two-dimensional-and-grid-dp/02-unique-paths/), [0/1 Knapsack](../../03-subsequence-and-knapsack-dp/02-zero-one-knapsack/), [Target Sum](../../03-subsequence-and-knapsack-dp/06-target-sum/) (negative running sums, needs an offset), [Longest Increasing Subsequence](../../03-subsequence-and-knapsack-dp/11-longest-increasing-subsequence/) (a "previous index" of `-1`, needs a shift).

## 8. After completing this topic you should be able to…

- [ ] Check that a recursion is safe to memoize (pure, no hidden changing state).
- [ ] List its changing parameters and their exact ranges.
- [ ] Allocate a cache of the right shape and size, with a safe sentinel.
- [ ] Add the check and the store in the right places.
- [ ] Memoize a parameter that can be negative, using an offset or a `HashMap`.
- [ ] Explain the time and space of the memoized version.

## 9. Practice problems

Write the brute-force recursion first, then apply the 4 steps without changing anything else.

| # | Problem | Platform/ID | Difficulty | Pattern tested | Why it belongs here |
|---|---------|-------------|------------|----------------|---------------------|
| 1 | [Fibonacci Number](https://leetcode.com/problems/fibonacci-number/) | LeetCode 509 | Easy | 1 parameter, 1D cache | Warm-up: the recipe on the example from topic 06. |
| 2 | [Climbing Stairs](https://leetcode.com/problems/climbing-stairs/) | LeetCode 70 | Easy | 1 parameter, 1D cache | The first worked example of this topic. |
| 3 | [N-th Tribonacci Number](https://leetcode.com/problems/n-th-tribonacci-number/) | LeetCode 1137 | Easy | 1 parameter, three recursive calls | Same recipe, one more call; check the cache size for `n = 0`. |
| 4 | [Unique Paths](https://leetcode.com/problems/unique-paths/) | LeetCode 62 | Medium | 2 parameters, 2D cache | The second worked example of this topic. |
| 5 | [Unique Paths II](https://leetcode.com/problems/unique-paths-ii/) | LeetCode 63 | Medium | 2D cache where 0 is a real answer | Blocked cells give 0 paths, so `0` cannot be the sentinel. |
| 6 | [Target Sum](https://leetcode.com/problems/target-sum/) | LeetCode 494 | Medium | Negative parameter, offset | The running sum can be negative: use an offset or a `HashMap`. |

## 10. Navigation

⬅ Previous: [12 · Time and Space Complexity in DP](../12-time-and-space-complexity-in-dp/) · ➡ Next: [14 · Converting Memoization to Tabulation](../14-converting-memoization-to-tabulation/) · 📖 [Walkthrough](walkthrough.md) · 💻 [Solutions](solution/)
