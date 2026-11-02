# Dungeon Game

> **Module:** Two-Dimensional and Grid DP · **Difficulty:** Advanced · **Estimated time:** 90–120 min

## 1. What is this topic?

This topic solves [LeetCode 174 — Dungeon Game](https://leetcode.com/problems/dungeon-game/). A knight walks from the top-left room of a grid to the princess in the bottom-right room, moving only right or down. Each room either drains or restores health, and the knight dies the instant his health drops to 0. What is the smallest starting health that lets him reach the princess alive?

The grid and the moves look exactly like [Minimum Path Sum](../04-minimum-path-sum/). The difference is hidden in the question, and it breaks the usual "fill from the start" DP. The topic is really a lesson in **choosing the direction of the state**.

## 2. Why does this concept exist?

Until now, every grid DP could be filled in the same direction the path travels. Here, a forward DP needs to remember **two** facts about the past (how much starting health the path has required so far, and how much health it currently has), and neither fact alone tells you which of two paths is better — it depends on rooms you have not seen yet. Turning the problem around and asking "how much health do I need **when I enter** this room to survive **from here to the end**?" gives a state described by a single number whose value does not depend on how you got there. Recognizing when to flip direction is a skill you will use for the rest of the course.

## 3. What problem does it solve?

Problems with a **"never drop below a threshold along the way"** constraint, where the answer is a minimum *initial* resource: starting health, initial fuel, minimum starting money to never go bankrupt. The running total must stay positive at every step, not just at the end, and that "at every step" is what makes the reverse direction necessary.

## 4. Prerequisites

- [Minimum Path Sum](../04-minimum-path-sum/) — the same grid and moves, with a min recurrence
- [Triangle](../05-triangle/) — defining the state as "best from here to the end" and filling from the end
- [Optimal substructure](../../00-foundations/05-optimal-substructure/) — why a subproblem must not depend on the path that reached it
- [Transition dependencies and computation order](../../00-foundations/11-transition-dependencies-and-computation-order/)

## 5. What you will learn

- Why the natural forward DP fails, with concrete grids where each forward rule gives a wrong answer.
- The reverse state "minimum health needed on entering cell `(i, j)`" and its recurrence `need = max(1, min(next needs) − room)`.
- Why the `max(1, …)` clamp is essential.
- The padding trick that removes all border cases, and a one-row space optimization.
- A general test for picking the state direction.

## 6. Important terminology

| Term | Meaning in plain English |
|---|---|
| Room value | The number in a cell: negative drains health, positive restores it, zero does nothing. |
| Health on entering | The knight's health just before a room's effect is applied. |
| `need[i][j]` | The minimum health the knight must have when he steps into room `(i, j)` so that he can reach the princess alive. |
| Clamp | Forcing a value to stay within a limit; here `max(1, …)` keeps `need` at least 1. |
| Forward / reverse DP | Filling the table in the direction the path travels / in the opposite direction. |
| Dominates | Path summary X dominates Y if X is at least as good in every respect; then Y can be thrown away. |

## 7. Where this is used in real problems

- LeetCode 174 itself.
- "Minimum starting value so that a running sum never drops below 1" problems on arrays and grids.
- The same reverse-thinking appears in game DP ("what must I have from this position onward?") and in later topics such as [Stock buy and sell](../../04-advanced-dp-patterns/02-stock-buy-and-sell/) state machines, where you must decide what a state remembers.

## 8. After completing this topic you should be able to…

- [ ] Explain, with a concrete grid, why a forward DP that keeps one number per cell gives wrong answers.
- [ ] Define `need[i][j]` precisely and derive `need[i][j] = max(1, min(need[i+1][j], need[i][j+1]) − dungeon[i][j])`.
- [ ] Explain the base case at the princess's room and the role of the clamp.
- [ ] Fill the table from the bottom-right corner, and reduce it to one row.
- [ ] Decide, for a new problem, whether the state should look forward or backward.

## 9. Practice problems

| # | Problem | Platform/ID | Difficulty | Pattern tested | Why it belongs here |
|---|---|---|---|---|---|
| 1 | [Minimum Path Sum](https://leetcode.com/problems/minimum-path-sum/) | LeetCode 64 | Medium | Forward min-path DP | Warm-up: the same grid and moves where forward DP *does* work. Compare it with Dungeon Game. |
| 2 | [Triangle](https://leetcode.com/problems/triangle/) | LeetCode 120 | Medium | "Best from here to the end" state | Practise defining the state from the end before needing it. |
| 3 | [Dungeon Game](https://leetcode.com/problems/dungeon-game/) | LeetCode 174 | Hard | Reverse DP with a clamp | The problem taught in this topic. Solve it from a blank file in `O(n)` space. |
| 4 | [Longest Increasing Path in a Matrix](https://leetcode.com/problems/longest-increasing-path-in-a-matrix/) | LeetCode 329 | Hard | "Best path starting here", memoized | Again the state looks forward from a cell ("longest path starting here"); no simple loop order exists, so memoization decides the order. |

## 10. Navigation

⬅ Previous: [Maximum Falling Path Sum](../06-maximum-falling-path-sum/) · ➡ Next: [Introduction to 3D DP](../08-introduction-to-3d-dp/) · 📖 [Walkthrough](walkthrough.md) · 💻 [Solutions](solution/)
