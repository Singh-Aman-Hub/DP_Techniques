# Module 02 — Two-Dimensional and Grid DP

> **Prerequisites:** [Module 00 — Foundations](../00-foundations/) and [Module 01 — One-Dimensional DP](../01-one-dimensional-dp/) · **Estimated time:** 14–20 hours over 1–2 weeks

## What this module is about

In Module 01 every problem had **one** quantity that changed as you made decisions: the index of a stair, a house or a character. One number described "where you are", so a 1D array `dp[i]` held every answer.

Many problems need **two** numbers to say where you are. The most natural example is a grid: a robot standing on a board is at *row* `r` and *column* `c`, and you cannot describe its position with a single number without losing information. As soon as two quantities change independently, the table of answers becomes a 2D table `dp[r][c]`.

This module teaches 2D DP on grids, because grids let you *see* the table: the DP table has the same shape as the board. Once that picture is in your head, the same skills carry over to 2D DP on strings and on knapsacks in Module 03, where the table is invisible.

The module ends with the next step up: problems where **three** quantities change (two robots walking at the same time), which leads to 3D tables and to a classic Hard problem, Cherry Pickup.

## Topic order

| # | Topic | What it teaches (one line) | Main problem |
|---|-------|----------------------------|--------------|
| 01 | [2D DP State Representation](01-2d-dp-state-representation/) | Why two changing parameters need a 2D table; drawing the grid, dependency arrows, fill order and indexing. | (concept topic) |
| 02 | [Unique Paths](02-unique-paths/) | The first full grid DP: count paths with `dp[r][c] = up + left`, plus the combinatorics shortcut. | LC 62 |
| 03 | [Unique Paths II](03-unique-paths-ii/) | Obstacles inside the grid and the base-row / base-column trap. | LC 63 |
| 04 | [Minimum Path Sum](04-minimum-path-sum/) | The first *minimum*-over-grid problem and the 1D-row space optimization. | LC 64 |
| 05 | [Triangle](05-triangle/) | Rows of different widths; why filling from the last row up removes every special case. | LC 120 |
| 06 | [Maximum Falling Path Sum](06-maximum-falling-path-sum/) | Start anywhere, three moves per step; the max version and its min twin. | LC 931 |
| 07 | [Dungeon Game](07-dungeon-game/) | Why forward DP fails and the answer is to run the DP backwards from the goal. | LC 174 |
| 08 | [Introduction to 3D DP](08-introduction-to-3d-dp/) | Two agents moving together means a third state dimension; 3D table and 2D space optimization. | LC 1463 |
| 09 | [Cherry Pickup](09-cherry-pickup/) | "Go and come back" turned into two walkers moving forward at the same time. | LC 741 |

Work through the topics **in order**. Each one adds exactly one new idea to the previous one.

## How each topic is organised

Every topic folder contains:

- `README.md` — what the topic is, why it matters, terminology and a practice table.
- `walkthrough.md` — the full lesson: intuition, the 7 derivation questions, brute force → memoization → tabulation → space optimization on the same example, dry runs, mistakes, and exercises with hidden answers.
- `solution/` — runnable, self-checking Java files (`java Tabulation.java` prints PASS/FAIL lines).

## Suggested schedule

| Day | Topics | Time |
|-----|--------|------|
| 1 | 01 State representation | 1.5–2 h |
| 2 | 02 Unique Paths, 03 Unique Paths II | 2–3 h |
| 3 | 04 Minimum Path Sum, 05 Triangle | 2–3 h |
| 4 | 06 Maximum Falling Path Sum | 1.5–2 h |
| 5 | 07 Dungeon Game | 2–3 h |
| 6 | 08 Introduction to 3D DP | 2–3 h |
| 7 | 09 Cherry Pickup, then revise the whole module | 3–4 h |

If a Hard topic (07–09) does not click the first time, that is normal. Re-read the "7 questions" section, then re-implement the tabulation from a blank file without looking.

## After this module you should be able to…

- [ ] Decide whether a problem needs a 1D, 2D or 3D state by listing what changes.
- [ ] Draw the DP table for a grid problem, mark the dependency arrows and pick a valid fill order.
- [ ] Write the recursion, memoization and tabulation for any "move right/down" grid problem.
- [ ] Handle obstacles, first-row/first-column base cases and out-of-bounds cells without off-by-one errors.
- [ ] Reduce an `m × n` table to a single row of length `n` when each row only depends on the row above.
- [ ] Recognise when the DP must run *backwards* from the goal (Dungeon Game).
- [ ] Model two agents moving at once as one state with extra dimensions, and remove a dimension that can be derived from the others (Cherry Pickup).

## Where to go next

➡ [Module 03 — Subsequence and Knapsack DP](../03-subsequence-and-knapsack-dp/). The 2D table stays, but the rows and columns stop being a visible board: they become "first `i` items" and "remaining capacity", or "first `i` characters of one string" and "first `j` characters of another".

⬅ Back to [Module 01 — One-Dimensional DP](../01-one-dimensional-dp/).
