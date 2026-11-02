# The DP Problem-Solving Framework

> **Module:** 00 · Foundations · **Difficulty:** Beginner · **Estimated time:** 90–120 min

## 1. What is this topic?

The capstone of the foundations module. Topics 01–15 each taught one piece: states, transitions, base cases, computation order, complexity, the conversions between recursion, memoization and tabulation, and space optimization. This topic puts those pieces in **one fixed order** — the **7-question framework** — and turns it into a checklist and a [blank worksheet](WORKSHEET.md) you will use for every DP problem in the rest of the course.

You will then run the whole framework, end to end, on two problems you have not studied in detail: **Dice Combinations (CSES 1633)** in full, and **House Robber (LeetCode 198)** as a sketch.

## 2. Why does this concept exist?

Beginners often "see" a DP solution only after reading it. Then, facing a new problem, they stare at a blank screen, because they learned *answers*, not a *process*. A fixed process removes the blank-screen problem: you always know the next question to ask. Pilots use checklists for the same reason: not because they cannot fly, but because a checklist catches the step you would otherwise skip under pressure.

## 3. What problem does it solve?

- "I do not know where to start" → start at question 1.
- "My recurrence is almost right but the answer is off" → questions 4–5 (recurrence, base cases) catch most of these.
- "My table gives 0" → question 6 (order).
- "It is correct but too slow / uses too much memory" → the complexity step and question 7.
- "Is this even a DP problem?" → the recognition signals in Section 13 of the walkthrough.

## 4. Prerequisites

All of module 00, in particular:

- [04 · Overlapping Subproblems](../04-overlapping-subproblems/) and [05 · Optimal Substructure](../05-optimal-substructure/): the two signs of DP.
- [09 · State Representation](../09-state-representation/) and [10 · State Transitions and Base Cases](../10-state-transitions-and-base-cases/): questions 1–5.
- [11 · Transition Dependencies and Computation Order](../11-transition-dependencies-and-computation-order/): question 6.
- [12 · Time and Space Complexity in DP](../12-time-and-space-complexity-in-dp/).
- [13 · Converting Recursion to Memoization](../13-converting-recursion-to-memoization/) and [14 · Converting Memoization to Tabulation](../14-converting-memoization-to-tabulation/).
- [15 · Space Optimization Basics](../15-space-optimization-basics/): question 7.

## 5. What you will learn

- The **7-question framework** as one reusable checklist, with a printable template.
- How to go from a problem statement to brute force, memoization, tabulation and an optimized version **without guessing**.
- A complete end-to-end solution of **Dice Combinations**, and a sketch of **House Robber**.
- **Signals** that a problem is a DP problem, and signals that it is not (preview of the [pattern recognition guide](../../07-revision-and-interview-preparation/DP_PATTERN_RECOGNITION.md)).
- How to use the [WORKSHEET.md](WORKSHEET.md) for every new problem.

## 6. Important terminology

| Term | Meaning in plain English |
|------|--------------------------|
| Framework | A fixed sequence of questions you answer, in order, for every problem. |
| The 7 questions | What is changing? What does the state mean? What are the choices? What is the recurrence? What are the base cases? What is the order? Can the space be reduced? |
| Worksheet | A blank page with those questions (plus complexity and tests) that you fill in before coding. |
| DP signal | A feature of a problem statement that suggests DP: "count the ways", "minimum/maximum", "can you reach", choices that affect the future, small numeric limits. |
| Ordered vs unordered counting | Whether `1+2` and `2+1` count as different ways. It changes the recurrence; always decide it explicitly. |
| Modulo answer | "Print the answer modulo 10⁹+7": the count is huge, so keep every value reduced to stay inside a `long`. |

## 7. Where this is used in real problems

Everywhere after this point. Every topic in [01-one-dimensional-dp](../../01-one-dimensional-dp/01-fibonacci-number/), [02-two-dimensional-and-grid-dp](../../02-two-dimensional-and-grid-dp/01-2d-dp-state-representation/), [03-subsequence-and-knapsack-dp](../../03-subsequence-and-knapsack-dp/01-subsequences-fundamentals/) and beyond is derived with these seven questions, and every practice problem in [06-leetcode-practice](../../06-leetcode-practice/README.md) has them as its "Deriving the DP" section. In interviews, saying the answers to the seven questions out loud is also a clear way to explain your solution.

## 8. After completing this topic you should be able to…

- [ ] Recite the 7 questions in order and say what each one is for.
- [ ] Fill in the worksheet for a new 1D problem without help.
- [ ] Derive, code and test brute force, memoization, tabulation and an optimized version of Dice Combinations.
- [ ] Sketch House Robber with the framework in under 10 minutes.
- [ ] List at least five signals that a problem is DP, and two signals that it is not.
- [ ] Explain why counting problems often ask for the answer modulo 10⁹+7.

## 9. Practice problems

Use a fresh copy of the [worksheet](WORKSHEET.md) for each one. Fill in all seven answers **before** writing code.

| # | Problem | Platform/ID | Difficulty | Pattern tested | Why it belongs here |
|---|---------|-------------|------------|----------------|---------------------|
| 1 | [N-th Tribonacci Number](https://leetcode.com/problems/n-th-tribonacci-number/) | LeetCode 1137 | Easy | 1D, read window of 3 | A gentle first worksheet: every answer is short. |
| 2 | [Frog 1](https://atcoder.jp/contests/dp/tasks/dp_a) | AtCoder dp_a | - | 1D, min of 2 choices | Same shape as Min Cost Climbing Stairs, new story. |
| 3 | [Dice Combinations](https://cses.fi/problemset/task/1633) | CSES 1633 | - | 1D counting, 6 choices, modulo | The full worked example of this topic; submit your own version. |
| 4 | [House Robber](https://leetcode.com/problems/house-robber/) | LeetCode 198 | Medium | 1D, take/skip with a constraint | The sketched example; finish it into code. |
| 5 | [Removing Digits](https://cses.fi/problemset/task/1637) | CSES 1637 | - | 1D, choices depend on the state | The number of choices changes from state to state. |
| 6 | [Coin Change](https://leetcode.com/problems/coin-change/) | LeetCode 322 | Medium | 1D min, "impossible" states | A trap for greedy thinking; shows why "not DP?" must be checked. |
| 7 | [Delete and Earn](https://leetcode.com/problems/delete-and-earn/) | LeetCode 740 | Medium | Reduce to House Robber | Q1–Q2 require rethinking the input before a state appears. |
| 8 | [Word Break](https://leetcode.com/problems/word-break/) | LeetCode 139 | Medium | 1D boolean (OR) | A yes/no DP over string prefixes. |

## 10. Navigation

⬅ Previous: [15 · Space Optimization Basics](../15-space-optimization-basics/) · ➡ Next: [01 · Fibonacci Number](../../01-one-dimensional-dp/01-fibonacci-number/) · 📖 [Walkthrough](walkthrough.md) · 📝 [Worksheet](WORKSHEET.md) · 💻 [Solutions](solution/)
