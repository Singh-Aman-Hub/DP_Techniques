# Converting Memoization to Tabulation

> **Module:** 00 · Foundations · **Difficulty:** Beginner · **Estimated time:** 60–90 min

## 1. What is this topic?

A **mechanical recipe** for turning a memoized (top-down) solution into a tabulated (bottom-up) one:

```
1. The cache becomes the table (same shape, same size).
2. Base cases become the table's initial values.
3. Recursive calls become table reads; the function body becomes the loop body.
4. The loop direction comes from the dependencies (who reads whom).
5. The answer is the cell that matches the top-level call.
```

You will apply it to the same two examples as [topic 13](../13-converting-recursion-to-memoization/), Climbing Stairs and grid paths, with the memo code and the table code side by side, and then study the pitfalls that make a table silently wrong.

## 2. Why does this concept exist?

Memoization is often the easiest DP to **write**, because you just follow the recursion. Tabulation is often the better DP to **run**:

- It has **no recursion**, so it never hits Java's stack limit (see [12 · Time and Space Complexity in DP](../12-time-and-space-complexity-in-dp/)).
- Its loops have a known order, which makes **space optimization** possible ([15 · Space Optimization Basics](../15-space-optimization-basics/)).
- It avoids function-call overhead, so it is usually a bit faster.

The recipe lets you get the best of both: think top-down, then convert to bottom-up without having to re-derive anything.

## 3. What problem does it solve?

It answers "I have a working memoized solution; how do I write the loops?" without guesswork about table size, initial values, loop direction or which cell holds the answer. Those four details are where almost all tabulation bugs come from.

## 4. Prerequisites

- [06 · Memoization](../06-memoization/), [07 · Tabulation](../07-tabulation/), [08 · Top-Down vs Bottom-Up](../08-top-down-vs-bottom-up/).
- [10 · State Transitions and Base Cases](../10-state-transitions-and-base-cases/).
- [11 · Transition Dependencies and Computation Order](../11-transition-dependencies-and-computation-order/): choosing the loop direction.
- [13 · Converting Recursion to Memoization](../13-converting-recursion-to-memoization/): the memoized code we start from.

## 5. What you will learn

- The 5-step recipe and a "memo construct → table construct" translation chart.
- How to apply it to a 1D problem (Climbing Stairs) and a 2D problem (grid paths), with tables filled step by step.
- How to handle base cases that live **outside** the table (like `ways(-1) = 0`) in a loop.
- How the loop direction flips when the state is defined "from here to the end".
- The common pitfalls, each shown with the wrong answer it produces.

## 6. Important terminology

| Term | Meaning in plain English |
|------|--------------------------|
| Tabulation | Filling a table of subproblem answers with loops, from the smallest subproblems up. |
| Table | The array that holds one answer per state. In tabulation it replaces the memo cache. |
| Initialisation | Writing the base-case values into the table before the main loop. |
| Dependency | A cell that the current cell reads. It must be filled first. |
| Top-level call | The very first call in the memoized version, e.g. `ways(n)`; its state is where the answer lives. |
| Padding | Extra cells added to the table so that "out of range" reads land on a cell holding the right base value instead of crashing. |

## 7. Where this is used in real problems

Every bottom-up solution in this course, starting with [Climbing Stairs](../../01-one-dimensional-dp/02-climbing-stairs/), [Min Cost Climbing Stairs](../../01-one-dimensional-dp/03-minimum-cost-climbing-stairs/), [Unique Paths](../../02-two-dimensional-and-grid-dp/02-unique-paths/), [Minimum Path Sum](../../02-two-dimensional-and-grid-dp/04-minimum-path-sum/) and [0/1 Knapsack](../../03-subsequence-and-knapsack-dp/02-zero-one-knapsack/). It is required whenever the recursion would be too deep for Java (inputs around 10⁵–10⁶) and before any space optimization.

## 8. After completing this topic you should be able to…

- [ ] Convert a memoized function into loops using the 5 steps.
- [ ] Size the table and write its initial values from the base cases.
- [ ] Pick the loop direction from the recurrence's reads.
- [ ] Handle out-of-range base cases with guards or padding.
- [ ] Return the right cell.
- [ ] Recognize the symptoms of the six common tabulation pitfalls.

## 9. Practice problems

Write the memoized version first, then convert it with the recipe and check that both agree on several inputs.

| # | Problem | Platform/ID | Difficulty | Pattern tested | Why it belongs here |
|---|---------|-------------|------------|----------------|---------------------|
| 1 | [Fibonacci Number](https://leetcode.com/problems/fibonacci-number/) | LeetCode 509 | Easy | 1D, left to right | Smallest possible conversion; watch the `n = 0` table size. |
| 2 | [Climbing Stairs](https://leetcode.com/problems/climbing-stairs/) | LeetCode 70 | Easy | 1D, left to right | The first worked example of this topic. |
| 3 | [N-th Tribonacci Number](https://leetcode.com/problems/n-th-tribonacci-number/) | LeetCode 1137 | Easy | 1D, three base cases | Three initial cells; small `n` edge cases. |
| 4 | [Min Cost Climbing Stairs](https://leetcode.com/problems/min-cost-climbing-stairs/) | LeetCode 746 | Easy | 1D, where is the answer? | The answer cell is the "top", one past the last index. |
| 5 | [Unique Paths](https://leetcode.com/problems/unique-paths/) | LeetCode 62 | Medium | 2D, row by row | The second worked example of this topic. |
| 6 | [Unique Paths II](https://leetcode.com/problems/unique-paths-ii/) | LeetCode 63 | Medium | 2D, base row/column with obstacles | The first row is **not** all 1s once a cell is blocked. |
| 7 | [Minimum Path Sum](https://leetcode.com/problems/minimum-path-sum/) | LeetCode 64 | Medium | 2D, min instead of + | Base row and column become running sums; same recipe. |

## 10. Navigation

⬅ Previous: [13 · Converting Recursion to Memoization](../13-converting-recursion-to-memoization/) · ➡ Next: [15 · Space Optimization Basics](../15-space-optimization-basics/) · 📖 [Walkthrough](walkthrough.md) · 💻 [Solutions](solution/)
