# Time and Space Complexity in DP

> **Module:** 00 · Foundations · **Difficulty:** Beginner · **Estimated time:** 60–90 min

## 1. What is this topic?

A simple, reliable way to work out how fast a DP solution runs and how much memory it uses, **before** you write it:

```
time  = (number of states) × (work done per state)
space = (size of the table or cache) + (depth of the recursion stack, if you recurse)
```

You will apply these two formulas to a 1D problem (Climbing Stairs), a 2D problem (grid paths) and the 0/1 knapsack, meet the idea of **pseudo-polynomial** time, and learn why deep recursion can crash a Java program even when the algorithm is fine.

## 2. Why does this concept exist?

In [04 · Overlapping Subproblems](../04-overlapping-subproblems/) you saw that plain recursion repeats work and can take exponential time, and in [06 · Memoization](../06-memoization/) you saw that caching removes the repeats. But "it got faster" is not enough for an interview or a judge with a time limit. You need to say **how fast**, and to check, from the input limits alone, whether your idea will pass.

DP has a big advantage here: once each state is computed only once, counting the work is just counting the states. This topic makes that counting mechanical.

## 3. What problem does it solve?

- "Will an O(n · W) knapsack pass with `n = 100` and `W = 10⁹`?" (No, and you will see why.)
- "My memoized solution is correct but crashes with `StackOverflowError` on large inputs. Why, and what do I do?"
- "Is this DP O(n) or O(n²)?" (It depends on the work per state, which beginners often forget.)

## 4. Prerequisites

- [03 · Recursion Trees and Brute Force](../03-recursion-trees-and-brute-force/): counting the nodes of a recursion tree.
- [04 · Overlapping Subproblems](../04-overlapping-subproblems/) and [06 · Memoization](../06-memoization/).
- [07 · Tabulation](../07-tabulation/).
- [09 · State Representation](../09-state-representation/): what a state is and the ranges of its parameters.
- [11 · Transition Dependencies and Computation Order](../11-transition-dependencies-and-computation-order/).

## 5. What you will learn

- To count states from the **ranges** of the state parameters.
- To find the **work per state** by counting the choices in the recurrence.
- To include the **recursion stack** in the space of a memoized solution.
- Worked complexity for 1D (`O(n)`), 2D (`O(rows · cols)`) and knapsack (`O(n · W)`).
- What **pseudo-polynomial** means, gently, and why it matters when a number in the input is huge.
- Java **stack depth limits**: `StackOverflowError`, the `-Xss` flag and running recursion in a thread with a bigger stack.
- A quick table to translate input limits into the complexity you can afford.

## 6. Important terminology

| Term | Meaning in plain English |
|------|--------------------------|
| Big-O, `O(f(n))` | How the running time (or memory) grows as the input grows, ignoring constant factors. |
| State count | How many different subproblems exist: multiply the number of possible values of each state parameter. |
| Work per state | The time spent inside one state, *not counting* the time to compute the states it reads. Usually the number of choices. |
| Call stack | The memory Java uses to remember "where to return to" for every function call that has not finished yet. |
| Recursion depth | The longest chain of calls that are active at the same time. Stack memory is proportional to it. |
| `StackOverflowError` | The error Java throws when the call stack runs out of room. |
| Polynomial time | Time bounded by a power of the input **size**, e.g. `O(n²)`. |
| Pseudo-polynomial time | Time that is polynomial in the **numeric value** of an input number (like capacity `W`) but not in the number of digits needed to write it. |

## 7. Where this is used in real problems

- Every DP problem: the complexity line in an interview answer is always "states × work".
- Knapsack family ([0/1 Knapsack](../../03-subsequence-and-knapsack-dp/02-zero-one-knapsack/), Subset Sum, Coin Change): pseudo-polynomial `O(n · W)` solutions.
- Long 1D inputs (CSES Dice Combinations has `n` up to 10⁶): memoization in Java may overflow the stack; tabulation does not.
- Problems where work per state is not O(1): Longest Increasing Subsequence (`O(n)` per state → `O(n²)`), interval DP (`O(n)` per state → `O(n³)`).

## 8. After completing this topic you should be able to…

- [ ] Count the states of any DP from the ranges of its parameters.
- [ ] Count the work per state from the recurrence.
- [ ] State time and space for brute force, memoization, tabulation and space-optimized versions, and explain each.
- [ ] Explain why `O(n · W)` knapsack is "pseudo-polynomial".
- [ ] Predict when recursion depth will be a problem in Java and give two fixes.
- [ ] Look at input limits and decide whether a planned complexity will pass.

## 9. Practice problems

For each problem, write down the state count, work per state, time, space and maximum recursion depth **before** coding.

| # | Problem | Platform/ID | Difficulty | Pattern tested | Why it belongs here |
|---|---------|-------------|------------|----------------|---------------------|
| 1 | [Climbing Stairs](https://leetcode.com/problems/climbing-stairs/) | LeetCode 70 | Easy | 1D, O(n) | The baseline: `n + 1` states, O(1) work each. |
| 2 | [Dice Combinations](https://cses.fi/problemset/task/1633) | CSES 1633 | - | 1D, 6 choices per state | `n ≤ 10⁶`: a memoized Java solution risks stack overflow; tabulation is safe. |
| 3 | [Unique Paths](https://leetcode.com/problems/unique-paths/) | LeetCode 62 | Medium | 2D, O(rows · cols) | Count states as rows × cols; compare with the exponential brute force. |
| 4 | [Coin Change](https://leetcode.com/problems/coin-change/) | LeetCode 322 | Medium | Pseudo-polynomial O(coins · amount) | Work per state is the number of coins, not O(1). |
| 5 | [Partition Equal Subset Sum](https://leetcode.com/problems/partition-equal-subset-sum/) | LeetCode 416 | Medium | Pseudo-polynomial O(n · sum) | Feasible only because the sum of values is small. |
| 6 | [Book Shop](https://cses.fi/problemset/task/1158) | CSES 1158 | - | 0/1 knapsack, memory limits | `n · x` is up to 10⁸ cells (about 400 MB as `int`), while a 1D array of `x + 1` cells needs about 400 KB. |

## 10. Navigation

⬅ Previous: [11 · Transition Dependencies and Computation Order](../11-transition-dependencies-and-computation-order/) · ➡ Next: [13 · Converting Recursion to Memoization](../13-converting-recursion-to-memoization/) · 📖 [Walkthrough](walkthrough.md) · 💻 [Solutions](solution/)
