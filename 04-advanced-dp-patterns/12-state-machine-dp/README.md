# State Machine DP

> **Module:** 04 — Advanced DP Patterns · **Difficulty:** Intermediate–Advanced · **Estimated time:** 2–3 hours

## 1. What is this topic?

State machine DP models a process as a **small, fixed set of states** (circles) and **allowed transitions** between them (arrows). A step-by-step DP keeps one value per state per step: `dp[i][state]` = the best value, or the number of ways, after `i` steps, ending in `state`. Each new value combines the previous values of the states that have an arrow into it.

## 2. Why does this concept exist?

Many problems restrict what can happen next based on a little recent history: "not the same activity twice in a row", "a cooldown day after selling", "never three lates in a row". Enumerating histories is exponential. But if the rule only needs a few facts about the past, those facts form a finite set of states, and the DP needs only `steps × states` values. Drawing the machine first also makes the recurrence almost write itself.

## 3. What problem does it solve?

- Optimising over sequences of choices with "what may follow what" rules (Vacation, stock trading with cooldown or fees).
- Counting strings or sequences that obey local rules (vowel strings, attendance records).
- Counting tilings column by column, where the state is the shape of the edge (dominoes and trominoes, 3-colour grids).
- Counting walks on a small fixed graph (knight dialer).

## 4. Prerequisites

- [Stock Buy and Sell](../02-stock-buy-and-sell/): the first state machines you met (holding / not holding / cooldown).
- [House Robber](../../01-one-dimensional-dp/06-house-robber/): a two-state machine in disguise (robbed the last house or not).
- [State transitions and base cases](../../00-foundations/10-state-transitions-and-base-cases/).
- [Space optimization basics](../../00-foundations/15-space-optimization-basics/): keeping only the previous row.

## 5. What you will learn

- A three-question method for turning a word problem into states and arrows.
- How to read a state diagram as a recurrence (arrows **into** a state = the terms of its sum or max).
- The Vacation problem through brute force, memoization, tabulation and O(1) space.
- Four different kinds of state: last item (1220), edge shape (790), a pair of counters (552), a position on a graph (935).
- Why new values must be computed from old ones before overwriting.

## 6. Important terminology

| Term | Meaning in plain English |
|---|---|
| State | one situation the process can be in, e.g. "did activity C today" |
| Transition / arrow | an allowed move from one state to the next state on the next step |
| State diagram | a picture of the states (boxes) and transitions (arrows) |
| Self-loop | an arrow from a state to itself (e.g. "keep holding the stock") |
| Step | one unit of progress: a day, a letter, a column, a move |
| Pull form | compute a state from all states with arrows **into** it |
| Push form | send each state's value along its arrows **out of** it |

## 7. Where this is used in real problems

- Trading rules (cooldown, fees, transaction limits): the six stock problems.
- Scheduling with "no repeat" or "rest after" rules.
- Generating or counting codes, passwords or strings with forbidden patterns.
- Tiling and painting grids one column or row at a time.
- Counting walks of a given length on a small graph; with huge lengths, the same machine is combined with matrix exponentiation.

## 8. After completing this topic you should be able to…

- [ ] Identify the step and the minimal state for a sequence problem.
- [ ] Draw the state diagram and turn it into a recurrence.
- [ ] Solve AtCoder DP C (Vacation) four ways and explain each.
- [ ] Explain the stock problems as state machines.
- [ ] Solve LeetCode 1220, 790, 552 and 935 from their diagrams.
- [ ] Update all states from the previous step without overwriting values too early.

## 9. Practice problems

| # | Problem | Platform/ID | Difficulty | Pattern tested | Why it belongs here |
|---|---|---|---|---|---|
| 1 | [Vacation](https://atcoder.jp/contests/dp/tasks/dp_c) | AtCoder DP C | – | 3 states, no self-loops | the core lesson of this topic |
| 2 | [Best Time to Buy and Sell Stock with Cooldown](https://leetcode.com/problems/best-time-to-buy-and-sell-stock-with-cooldown/) | LeetCode 309 | Medium | hold / sold / rest machine | revision: the stock problems as a machine |
| 3 | [Best Time to Buy and Sell Stock with Transaction Fee](https://leetcode.com/problems/best-time-to-buy-and-sell-stock-with-transaction-fee/) | LeetCode 714 | Medium | 2 states, cost on an arrow | a gain/cost attached to a transition |
| 4 | [Knight Dialer](https://leetcode.com/problems/knight-dialer/) | LeetCode 935 | Medium | states = keys, arrows = knight moves | counting walks on a small fixed graph |
| 5 | [Domino and Tromino Tiling](https://leetcode.com/problems/domino-and-tromino-tiling/) | LeetCode 790 | Medium | states = shape of the tiled edge | designing states that are not obvious from the statement |
| 6 | [Count Vowels Permutation](https://leetcode.com/problems/count-vowels-permutation/) | LeetCode 1220 | Hard | states = last letter | reading "may follow" rules as arrows |
| 7 | [Student Attendance Record II](https://leetcode.com/problems/student-attendance-record-ii/) | LeetCode 552 | Hard | states = (absences, trailing lates) | a state built from two counters |
| 8 | [Number of Ways to Paint N × 3 Grid](https://leetcode.com/problems/number-of-ways-to-paint-n-3-grid/) | LeetCode 1411 | Hard | states = colour pattern of a row | grouping many patterns into 2 state types |
| 9 | [Minimum Swaps To Make Sequences Increasing](https://leetcode.com/problems/minimum-swaps-to-make-sequences-increasing/) | LeetCode 801 | Hard | states = swapped / not swapped at i | an optimisation machine with conditional arrows |

## 10. Navigation

⬅ Previous: [Digit DP](../11-digit-dp/) · ➡ Next: [Space Optimization (Module 05)](../../05-dp-optimization/01-space-optimization/) · 📖 [Walkthrough](walkthrough.md) · 💻 [Solutions](solution/)
