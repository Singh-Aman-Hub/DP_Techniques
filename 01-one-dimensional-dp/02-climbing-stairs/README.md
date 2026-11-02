# Climbing Stairs

> **Module:** 01 · One-Dimensional DP · **Difficulty:** Beginner · **Estimated time:** 60–90 min

## 1. What is this topic?

You stand at the bottom of a staircase with `n` steps and may climb 1 or 2 steps at a time. How many different sequences of moves bring you to the top? This is the first **counting DP** in the course: instead of computing a number from a formula someone gave us (like Fibonacci), we must *discover* the recurrence by thinking about the last move.

## 2. Why does this concept exist?

Counting problems ("how many ways…") explode quickly: there are 1,836,311,903 ways to climb 45 stairs. Listing them one by one is hopeless. DP counts them without listing them, by noticing that every way to reach step `i` ends with one specific last move, and the ways *before* that move are a smaller version of the same question.

## 3. What problem does it solve?

[LeetCode 70 · Climbing Stairs](https://leetcode.com/problems/climbing-stairs/): count the distinct ordered sequences of 1-steps and 2-steps that sum to `n`. The topic then **generalizes** to steps of size `1, 2, …, k`, which covers dice-roll counting and similar problems.

## 4. Prerequisites

- [Fibonacci Number](../01-fibonacci-number/) — the recurrence you will rediscover here.
- [State transitions and base cases](../../00-foundations/10-state-transitions-and-base-cases/)
- [The DP problem-solving framework](../../00-foundations/16-the-dp-problem-solving-framework/)

## 5. What you will learn

- The **"look at the last move"** technique for finding a recurrence.
- Why counting DPs **add** the counts of different choices (the choices lead to disjoint sets of ways).
- Why `ways(0) = 1` is the right base case (the "empty way").
- Why the answer is exactly Fibonacci shifted by one.
- How to generalize to `k` step sizes, and a sliding-window trick that makes it `O(n)` instead of `O(n·k)`.

## 6. Important terminology

| Term | Meaning in plain English |
|------|--------------------------|
| Counting DP | A DP whose answer is "how many ways", built by adding counts. |
| Ordered sequence | `1+2` and `2+1` are different climbs, because the moves happen in a different order. |
| Last-move decomposition | Splitting all solutions by what their final move was. |
| Disjoint | Two groups with nothing in common; their sizes can be added safely. |
| Empty way | Doing nothing at all. It counts as one way to "reach" step 0. |
| Sliding window sum | A running sum over the last `k` cells, updated by adding one cell and removing one. |

## 7. Where this is used in real problems

Dice-sum counting, counting ways to make a sum when order matters ([Combination Sum IV](https://leetcode.com/problems/combination-sum-iv/)), tiling a `2 × n` board with dominoes, and counting messages that decode to a string ([Decode Ways](../08-decode-ways/), later in this module).

## 8. After completing this topic you should be able to…

- [ ] Explain in words why `ways(n) = ways(n-1) + ways(n-2)`.
- [ ] Justify `ways(0) = 1`.
- [ ] Show that `ways(n) = F(n+1)`.
- [ ] Write the recurrence for any set of allowed step sizes.
- [ ] Implement the generalized version in `O(n)` using a sliding sum.

## 9. Practice problems

| # | Problem | Platform/ID | Difficulty | Pattern tested | Why it belongs here |
|---|---------|-------------|------------|----------------|---------------------|
| 1 | Climbing Stairs | [LeetCode 70](https://leetcode.com/problems/climbing-stairs/) | Easy | Counting by last move, steps {1, 2} | The core problem of this topic. |
| 2 | N-th Tribonacci Number | [LeetCode 1137](https://leetcode.com/problems/n-th-tribonacci-number/) | Easy | Three-term recurrence | Same as climbing with steps {1, 2, 3} up to the base cases. |
| 3 | Dice Combinations | [CSES 1633](https://cses.fi/problemset/task/1633) | — | Steps {1..6}, answer modulo 10^9+7 | The generalized `k = 6` version, with large `n` and a modulus. |
| 4 | Combination Sum IV | [LeetCode 377](https://leetcode.com/problems/combination-sum-iv/) | Medium | Steps from an arbitrary set | Allowed "step sizes" are given as an array instead of `1..k`. |

## 10. Navigation

⬅ Previous: [Fibonacci Number](../01-fibonacci-number/) · ➡ Next: [Min Cost Climbing Stairs](../03-minimum-cost-climbing-stairs/) · 📖 [Walkthrough](walkthrough.md) · 💻 [Solutions](solution/)
