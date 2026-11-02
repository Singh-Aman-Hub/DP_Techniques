# Module 04 — Advanced DP Patterns

> **Level:** Intermediate → Advanced · **Estimated time:** 5–7 weeks at 6–8 hours per week · **Topics:** 12

## What this module is about

In modules 00–03 you learned the core DP toolkit: defining a state, writing a recurrence, memoizing it, turning it into a table, and shrinking that table. Every problem so far moved along an index, a grid, or a "take / skip" decision over items.

This module teaches the **shapes of state** that appear in harder problems — the patterns behind most LeetCode Hard DP questions:

| Pattern family | The state looks like… | Topics |
|---|---|---|
| Two strings / one string | `(i, j)` = positions in two strings, or a substring `s[i..j]` | 01 |
| State machines | `(day, holding, transactionsLeft)` — a small set of "modes" you move between | 02, 12 |
| Interval DP | `dp[i][j]` over a contiguous range, trying every split point `k` | 03, 04, 05, 06 |
| Structures | a tree node, a graph vertex in topological order | 07, 08 |
| Sets as numbers | a bitmask of which items are already used | 09, 10 |
| Numbers digit by digit | `(position, tight, …)` while building a number | 11 |

The first six topics in this module build one big idea step by step: **interval DP**. Topic 01 consolidates string DP and shows the difference between "two prefixes" and "one substring". Topic 02 is a detour into state design (the stock family), and topics 03–06 then teach interval DP from its general form to its three most famous problems.

## Topic order

| # | Topic | One-line description |
|---|---|---|
| 01 | [DP on Strings](01-dp-on-strings/) | Tell two-string `(i, j)` prefix DP apart from one-string interval DP; pattern matching with `?`, `.` and `*` (LC 44, LC 10). |
| 02 | [Stock Buy and Sell](02-stock-buy-and-sell/) | One state `(day, holding, transactionsLeft)` solves all six stock problems; each variant changes only one state or edge. |
| 03 | [Interval DP](03-interval-dp/) | `dp[i][j]` over a range, try every split `k`, fill by increasing length (LC 1039). |
| 04 | [Matrix Chain Multiplication](04-matrix-chain-multiplication/) | The classic partition DP, a full table dry run, and printing the optimal brackets. |
| 05 | [Burst Balloons](05-burst-balloons/) | Choose the balloon burst **last**, not first, so the two sides become independent (LC 312). |
| 06 | [Palindrome Partitioning](06-palindrome-partitioning/) | Backtracking with an `isPal` table (LC 131), then min cuts as an O(n²) front-partition DP (LC 132). |
| 07 | [DP on Trees](07-dp-on-trees/) | Compute each node's answer from its children's answers in a post-order traversal. |
| 08 | [DP on Graphs](08-dp-on-graphs/) | DP over a directed acyclic graph in topological order; when shortest-path ideas meet DP. |
| 09 | [Bitmask DP](09-bitmask-dp/) | Use the bits of an integer to remember which items are already used. |
| 10 | [Traveling Salesperson Problem](10-traveling-salesperson-problem/) | The textbook bitmask DP: `dp[mask][last]` over visited cities. |
| 11 | [Digit DP](11-digit-dp/) | Count numbers in a range digit by digit with a "tight" flag. |
| 12 | [State Machine DP](12-state-machine-dp/) | Generalise the stock idea: model a problem as a few states and allowed transitions. |

Topics 01–06 should be done in order. Topics 07–12 depend mostly on module 00 and topic 02; within them, do 09 before 10.

## How long it takes

| Topics | Typical time |
|---|---|
| 01 DP on Strings | 4–5 hours (two Hard problems, read the `*` tables slowly) |
| 02 Stock Buy and Sell | 4–5 hours (six problems, but each is a one-line change) |
| 03–05 Interval DP, MCM, Burst Balloons | 3–4 hours each |
| 06 Palindrome Partitioning | 3 hours |
| 07–12 | 3–5 hours each |

Add 1–2 hours per topic for the practice problems.

## Prerequisites

You should have finished:

- [Module 00 — Foundations](../00-foundations/) (states, transitions, memoization, tabulation, computation order)
- [Module 01 — One-dimensional DP](../01-one-dimensional-dp/)
- [Module 02 — Two-dimensional and grid DP](../02-two-dimensional-and-grid-dp/)
- [Module 03 — Subsequence and knapsack DP](../03-subsequence-and-knapsack-dp/) — especially [LCS](../03-subsequence-and-knapsack-dp/12-longest-common-subsequence/), [LPS](../03-subsequence-and-knapsack-dp/13-longest-palindromic-subsequence/) and [Edit Distance](../03-subsequence-and-knapsack-dp/14-edit-distance/)

## After this module you can…

- [ ] Look at a string problem and decide between a two-prefix table `dp[i][j]` and a substring table `dp[i][j]`.
- [ ] Explain exactly what `*` means in wildcard matching and in regular-expression matching, and why their recurrences differ.
- [ ] Add a dimension to a state (holding, transactions left, cooldown) to encode a new rule, and draw the state diagram.
- [ ] Recognise interval DP, write `dp[i][j] = best over k of (left part, right part, cost of joining)`, and fill it by length.
- [ ] Explain why "the last action in a range" often gives independent subproblems when "the first action" does not.
- [ ] Turn an O(n³) interval DP into an O(n²) front-partition DP when only prefixes or suffixes matter.
- [ ] Use trees, DAGs, bitmasks and digits as DP states (topics 07–12).

## Navigation

⬅ Previous module: [03 — Subsequence and Knapsack DP](../03-subsequence-and-knapsack-dp/) · ➡ Next module: [05 — DP Optimization](../05-dp-optimization/) · 🏁 Start: [01 — DP on Strings](01-dp-on-strings/)
