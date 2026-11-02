# State Transitions and Base Cases

> **Module:** 00 · Foundations · **Difficulty:** Beginner · **Estimated time:** 75–105 min

## 1. What is this topic?

In [topic 09](../09-state-representation/) you learned to name a subproblem with a **state**. This topic teaches the other two pieces of every DP solution:

- **Transitions**: how the answer for one state is built from the answers of smaller states. Each transition is one *choice* (take 1 step, take 2 steps…), and the choices are combined with `+`, `min`, `max` or `OR`. Written as a formula, this is the **recurrence**.
- **Base cases**: the smallest states whose answers you know without any recursion. They stop the recursion and seed the table.

We work through two problems: [Climbing Stairs (LeetCode 70)](https://leetcode.com/problems/climbing-stairs/) and [Min Cost Climbing Stairs (LeetCode 746)](https://leetcode.com/problems/min-cost-climbing-stairs/).

## 2. Why does this concept exist?

A state on its own is just a name. To compute anything you need a rule that says "this state's answer = some combination of these other states' answers", plus a place where the rule stops. Almost every wrong DP answer comes from one of three places: a missing choice, the wrong combining operator, or a wrong base case. This topic gives you a checklist for each.

## 3. What problem does it solve?

It turns the state sentence into working code. Once you have the recurrence and base cases, the brute-force recursion, the memoized version and the table version are all mechanical translations (topics [13](../13-converting-recursion-to-memoization/) and [14](../14-converting-memoization-to-tabulation/)).

## 4. Prerequisites

- [02 · Recursion fundamentals](../02-recursion-fundamentals/)
- [03 · Recursion trees and brute force](../03-recursion-trees-and-brute-force/)
- [05 · Optimal substructure](../05-optimal-substructure/)
- [06 · Memoization](../06-memoization/) and [07 · Tabulation](../07-tabulation/)
- [09 · State representation](../09-state-representation/)

## 5. What you will learn

- How to list the choices at a state by thinking about the **last** (or first) decision.
- Which operator to use: `+` for counting, `min`/`max` for optimizing, `OR` for yes/no questions.
- Why counting needs choices that do not overlap, while `min`/`max` do not.
- How to write a recurrence and explain every term.
- What base cases are for, and the difference between **terminal** states and **invalid** states.
- The safe "neutral value" to return for an invalid state (`0`, `+∞`, `-∞`, `false`).
- The most common off-by-one traps, with concrete wrong answers.

## 6. Important terminology

| Term | Meaning in plain English |
|---|---|
| Transition | A move from one state to a smaller state, corresponding to one choice. |
| Choice | One option available at a state (take 1 step, skip the item…). |
| Recurrence | A formula giving a state's answer in terms of smaller states' answers. |
| Combining operator | How choices are merged: `+`, `min`, `max`, `OR`. |
| Base case | A state whose answer is known directly, without recursion. |
| Terminal state | A valid state where the process is finished (e.g. standing on the top step). |
| Invalid state | A state that cannot happen (e.g. step `-1`, or past the top). It must not contribute to the answer. |
| Neutral value | The value that does not change the result when combined: `0` for `+`, `+∞` for `min`, `-∞` for `max`, `false` for `OR`. |
| Off-by-one error | A bug where an index or base case is shifted by one position. |

## 7. Where this is used in real problems

Every DP problem in this course has a recurrence and base cases. Counting recurrences (`+`) appear in [Climbing Stairs](../../01-one-dimensional-dp/02-climbing-stairs/), [Decode Ways](../../01-one-dimensional-dp/08-decode-ways/) and [Unique Paths](../../02-two-dimensional-and-grid-dp/02-unique-paths/). Minimizing recurrences appear in [Min Cost Climbing Stairs](../../01-one-dimensional-dp/03-minimum-cost-climbing-stairs/), [Frog Jump](../../01-one-dimensional-dp/04-frog-jump/) and [Coin Change](../../03-subsequence-and-knapsack-dp/08-coin-change/). Maximizing recurrences appear in [House Robber](../../01-one-dimensional-dp/06-house-robber/) and [0/1 Knapsack](../../03-subsequence-and-knapsack-dp/02-zero-one-knapsack/). Yes/no recurrences (`OR`) appear in [Subset Sum](../../03-subsequence-and-knapsack-dp/03-subset-sum/).

## 8. After completing this topic you should be able to…

- [ ] List every choice at a state and check that none is missing.
- [ ] Pick the right combining operator from the question being asked.
- [ ] Write the recurrence for Climbing Stairs and Min Cost Climbing Stairs from scratch and explain each term.
- [ ] Choose base cases that match the state sentence exactly.
- [ ] Handle invalid states with the correct neutral value.
- [ ] Spot an off-by-one base case by testing the smallest inputs by hand.

## 9. Practice problems

| # | Problem | Platform/ID | Difficulty | Pattern tested | Why it belongs here |
|---|---|---|---|---|---|
| 1 | [Fibonacci Number](https://leetcode.com/problems/fibonacci-number/) | LeetCode 509 | Easy | `+` recurrence, two base cases | Re-derive the recurrence you already know, now naming each part. |
| 2 | [Climbing Stairs](https://leetcode.com/problems/climbing-stairs/) | LeetCode 70 | Easy | Counting with `+` | Worked example of this topic. |
| 3 | [N-th Tribonacci Number](https://leetcode.com/problems/n-th-tribonacci-number/) | LeetCode 1137 | Easy | `+` with three base cases | Practice choosing the right number of base cases. |
| 4 | [Min Cost Climbing Stairs](https://leetcode.com/problems/min-cost-climbing-stairs/) | LeetCode 746 | Easy | Optimizing with `min` | Worked example of this topic; tricky base cases. |
| 5 | [Frog 1](https://atcoder.jp/contests/dp/tasks/dp_a) | AtCoder dp_a | - | `min` over two choices | Same shape as min-cost stairs with a different cost per move. |
| 6 | [Dice Combinations](https://cses.fi/problemset/task/1633) | CSES 1633 | - | `+` over six choices, invalid states | More choices per state; invalid (negative) sums must contribute 0. |
| 7 | [House Robber](https://leetcode.com/problems/house-robber/) | LeetCode 198 | Medium | `max` over take/skip | A first `max` recurrence with a take/skip choice. |

## 10. Navigation

⬅ Previous: [09 · State representation](../09-state-representation/) · ➡ Next: [11 · Transition dependencies and computation order](../11-transition-dependencies-and-computation-order/) · 📖 [Walkthrough](walkthrough.md) · 💻 [Solutions](solution/)
