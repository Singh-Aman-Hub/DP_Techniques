# Module 00 — Foundations of Dynamic Programming

> **Level:** Beginner (no DP knowledge assumed) · **Topics:** 16 · **Estimated time:** 16–22 hours spread over 2–3 weeks

This module takes you from "I know loops, arrays, functions and a little recursion" to "I can look at a new problem, describe its state, write its recurrence, and turn it into a working memoized or tabulated Java solution". Every later module assumes you have finished it.

There is one running example through the whole module: the **Fibonacci numbers**. Using one small problem lets you focus on the *ideas* (recursion trees, caching, table filling, state design) without also fighting a new problem statement in every topic. Climbing Stairs and a few small counting examples appear wherever a second example helps.

---

## How the module is organised

The 16 topics form three blocks:

```
 Block A: recursion, the raw material        Block B: the two DP techniques
 ┌───────────────────────────────────┐       ┌───────────────────────────────────┐
 │ 01 What is DP?                    │       │ 06 Memoization  (top-down)        │
 │ 02 Recursion fundamentals         │  -->  │ 07 Tabulation   (bottom-up)       │
 │ 03 Recursion trees & brute force  │       │ 08 Top-down vs bottom-up          │
 │ 04 Overlapping subproblems        │       └───────────────────────────────────┘
 │ 05 Optimal substructure           │                        │
 └───────────────────────────────────┘                        v
                    Block C: designing and converting DP solutions on your own
                    ┌────────────────────────────────────────────────────────┐
                    │ 09 State  · 10 Transitions & base cases · 11 Order     │
                    │ 12 Complexity · 13 Recursion -> memo · 14 Memo -> table│
                    │ 15 Space optimization · 16 The problem-solving framework│
                    └────────────────────────────────────────────────────────┘
```

## Topic order

| # | Topic | What it teaches (one line) | Time |
|---|-------|----------------------------|------|
| 01 | [What is Dynamic Programming?](01-what-is-dynamic-programming/) | DP means "never solve the same subproblem twice"; the two properties that make it possible; where DP shows up. | 45–60 min |
| 02 | [Recursion Fundamentals](02-recursion-fundamentals/) | Base case, recursive case, the call stack, trusting the recursion, and stack overflow. | 60–90 min |
| 03 | [Recursion Trees and Brute Force](03-recursion-trees-and-brute-force/) | Drawing recursion trees, counting their nodes, why brute force is O(2^n), and "try every choice" with take/skip. | 60–90 min |
| 04 | [Overlapping Subproblems](04-overlapping-subproblems/) | Spotting the same subproblem solved many times, measuring it, and contrasting with merge sort where nothing repeats. | 45–60 min |
| 05 | [Optimal Substructure](05-optimal-substructure/) | Why the best answer can be built from best answers to smaller problems — and a problem where it cannot. | 60–75 min |
| 06 | [Memoization](06-memoization/) | Top-down DP: a cache, a sentinel, the check-before-compute pattern, the pruned tree, and common bugs. | 60–90 min |
| 07 | [Tabulation](07-tabulation/) | Bottom-up DP: filling a table from base cases upward, choosing the loop order, no recursion. | 60–90 min |
| 08 | [Top-Down vs Bottom-Up](08-top-down-vs-bottom-up/) | One problem solved four ways (brute force, memo, table, O(1) space) and when to prefer each. | 45–60 min |
| 09 | [State Representation](09-state-representation/) | Choosing *what a subproblem is*: the variables that describe it and what `dp[...]` means. | 60–90 min |
| 10 | [State Transitions and Base Cases](10-state-transitions-and-base-cases/) | Turning choices into a recurrence and finding the smallest states whose answers are known. | 60–90 min |
| 11 | [Transition Dependencies and Computation Order](11-transition-dependencies-and-computation-order/) | Reading which states a cell depends on, and deriving a loop order that respects it. | 60–75 min |
| 12 | [Time and Space Complexity in DP](12-time-and-space-complexity-in-dp/) | The rule "number of states × work per state", applied to memo and tables. | 45–60 min |
| 13 | [Converting Recursion to Memoization](13-converting-recursion-to-memoization/) | A mechanical recipe for adding a cache to any brute-force recursion. | 45–60 min |
| 14 | [Converting Memoization to Tabulation](14-converting-memoization-to-tabulation/) | A mechanical recipe for turning top-down code into loops. | 60–75 min |
| 15 | [Space Optimization Basics](15-space-optimization-basics/) | Keeping only the rows or variables the recurrence still reads. | 45–60 min |
| 16 | [The DP Problem-Solving Framework](16-the-dp-problem-solving-framework/) | The full checklist (the 7 derivation questions) you will use on every problem in the rest of the course. | 60–90 min |

Do the topics **in order**. Each one links back to the topics it depends on.

## How to study each topic

1. Read the topic's `README.md` (5 minutes) to see the goal and the vocabulary.
2. Work through `walkthrough.md` with pen and paper. Redraw the recursion trees and tables yourself; do not just read them.
3. Run the programs in `solution/`:
   ```bash
   cd 00-foundations/06-memoization/solution
   java Memoization.java        # Java 11+; prints PASS/FAIL lines
   ```
   Change an input, predict the output, run again.
4. Answer the "Check your understanding" questions at the end of the walkthrough *before* opening the answers.
5. Solve the practice problems listed in the README. In this module there are only a few; their purpose is to apply the idea, not to grind.

## What you can do after this module

- [ ] Explain in plain words what dynamic programming is and why it is faster than brute force.
- [ ] Write a recursive function with correct base cases and draw its recursion tree.
- [ ] Count how many times each subproblem is solved and decide whether caching will help.
- [ ] Recognise optimal substructure, and recognise when it is missing.
- [ ] Write memoized (top-down) and tabulated (bottom-up) solutions, and cut the space when only a few previous states are needed.
- [ ] Define a state, a transition and base cases for a problem you have not seen before, and derive the loop order.
- [ ] Apply the 7-question framework that every later module uses.

## Where to go next

➡ [Module 01 — One-Dimensional DP](../01-one-dimensional-dp/) applies everything here to a sequence of classic problems (Fibonacci in full, Climbing Stairs, House Robber, Decode Ways…).

⬆ [Course home](../README.md) · 🗺 [Roadmap](../ROADMAP.md) · ✅ [Progress tracker](../PROGRESS_TRACKER.md)
