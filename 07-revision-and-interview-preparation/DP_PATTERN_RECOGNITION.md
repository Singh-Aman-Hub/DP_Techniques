# DP Pattern Recognition — How to Tell *Which* DP (and Whether DP at All)

> **Module:** 07 · Revision and Interview Preparation · **Difficulty:** All levels · **Estimated time:** 3–4 hours the first time, then 20 minutes per revision

Solving a DP problem is two separate skills:

1. **Recognition** — reading an unfamiliar statement and realizing "this is DP, and it is *this kind* of DP, with *this* state."
2. **Execution** — writing the recurrence, base cases, loop order and code.

Modules 00–05 mostly train execution. This file trains recognition. Read it once after Module 03, again after Module 04, and use the drills at the end (Section 12) before every interview.

Every term used here was introduced earlier in the course. If one feels shaky, follow the link:
[state](../00-foundations/09-state-representation/) ·
[transition and base case](../00-foundations/10-state-transitions-and-base-cases/) ·
[overlapping subproblems](../00-foundations/04-overlapping-subproblems/) ·
[optimal substructure](../00-foundations/05-optimal-substructure/) ·
[memoization](../00-foundations/06-memoization/) ·
[tabulation](../00-foundations/07-tabulation/) ·
[the DP problem-solving framework](../00-foundations/16-the-dp-problem-solving-framework/).

---

## Table of contents

1. [Clues that a problem is DP](#1-clues-that-a-problem-is-dp)
2. [Confirming overlapping subproblems](#2-confirming-overlapping-subproblems)
3. [Confirming optimal substructure (and a counterexample)](#3-confirming-optimal-substructure-and-a-counterexample)
4. [Choosing the state: 1D, 2D, 3D, bitmask, …](#4-choosing-the-state-1d-2d-3d-bitmask-)
5. [Identifying each DP family](#5-identifying-each-dp-family)
6. [Greedy vs. recursion vs. backtracking vs. DP](#6-greedy-vs-recursion-vs-backtracking-vs-dp)
7. [Constraint → complexity → technique table](#7-constraint--complexity--technique-table)
8. [Master decision flowchart](#8-master-decision-flowchart)
9. [Pattern → template recurrence → canonical problems → folder](#9-pattern--template-recurrence--canonical-problems--folder)
10. [Worked decoding: five statements, start to finish](#10-worked-decoding-five-statements-start-to-finish)
11. [Recognition checklist (one screen)](#11-recognition-checklist-one-screen)
12. [Drill: identify the pattern (18 questions)](#12-drill-identify-the-pattern-18-questions)

---

## 1. Clues that a problem is DP

No single word proves a problem is DP. Look for **several clues together**. The more boxes you tick, the more confident you can be.

### 1.1 Wording clues

| Phrase in the statement | What it usually means | Typical DP "combine" operator |
|---|---|---|
| "number of ways", "how many distinct …", "count the …" (often "modulo 10^9+7") | **Counting DP.** Add up the counts of every choice. | `+` |
| "minimum cost / fewest steps / least number of …" | **Optimization DP** (min). | `min` |
| "maximum profit / score / largest sum" | **Optimization DP** (max). | `max` |
| "is it possible", "can you reach", "return true if …" | **Feasibility DP.** | `\|\|` (OR) |
| "longest / shortest subsequence / substring / path" | Sequence DP (LIS, LCS, palindromes, grid paths). | `max` / `min` |
| "the k-th / at most k transactions / using at most k …" | An extra **resource** dimension in the state. | any |
| "probability that …", "expected number of …" | **Probability DP** — weighted sum over choices. | `+` with weights |
| "both players play optimally" | **Game DP** — score difference or win/lose state. | `max` of (mine − theirs) |

The "modulo 10^9+7" hint deserves special mention: the setter is telling you the count is astronomically large, so you cannot list solutions one by one. Counting without listing is exactly what DP does.

### 1.2 Structural clues

- **You make a sequence of decisions**, and each decision has a small number of options: take or skip an item, move right or down, buy / sell / rest, cut here or not.
- **The answer depends on earlier decisions only through a small summary.** House Robber: whether you may rob house `i` depends only on whether you robbed house `i−1`, not on the whole history. That small summary *is* your state.
- **Trying "the obvious best move" can be wrong.** If you can find a small input where the locally best move loses (Section 6), greedy is out and DP is likely.
- **The input is a sequence, a string, a pair of strings, a grid, a tree, a DAG, or a small set** — the shapes DP handles well.

### 1.3 Constraint clues

Constraints are the setter's hint about the intended complexity. Assume roughly **10^8 simple operations per second** in Java.

| Constraint you see | What it suggests |
|---|---|
| `n ≤ 20` (sometimes ≤ 16 or ≤ 24) | Exponential in n is allowed → **bitmask DP** (O(2^n · n) or O(2^n · n²)) or backtracking. |
| `n ≤ 100 … 500` | **O(n³)** is allowed → interval DP, "split point" DP, Floyd-style DP. |
| `n ≤ 1000 … 5000` | **O(n²)** → two-index DP (LCS, edit distance, LIS O(n²), "pair of indices" DP). |
| `n ≤ 10^5 … 10^6` | **O(n)** or **O(n log n)** → 1D DP, LIS with binary search, monotonic-queue DP. |
| `n` up to 10^9 or 10^18 | Iterating over n is impossible → **matrix exponentiation** (linear recurrence) or **digit DP** (count numbers ≤ N; only ~19 digits). |
| `n ≤ 100` items **and** sums/capacities/values `≤ 10^4` | **Pseudo-polynomial knapsack**: O(n · capacity) is ~10^6. The *value* bound is the hint. |
| A small extra limit: "at most k = 2 transactions", "k ≤ 100 stops" | Add **k as a dimension**. |

> **Rule:** multiply the sizes of all state dimensions by the work per state. If that product is ≤ ~10^8, your state design is probably what the setter intended.

### 1.4 Paraphrased examples and how to decode them

| Statement (paraphrased) | Clues spotted | Decoding |
|---|---|---|
| "A staircase has n ≤ 45 steps; you climb 1 or 2 at a time. In how many distinct ways can you reach the top?" | "how many ways", choice at each step | Counting, 1D. `ways[i] = ways[i−1] + ways[i−2]`. → [Climbing Stairs](../01-one-dimensional-dp/02-climbing-stairs/) |
| "Houses in a row hold money; robbing two neighbours triggers an alarm. Maximize the loot." | "maximize", take/skip, previous decision matters | Max, 1D. → [House Robber](../01-one-dimensional-dp/06-house-robber/) |
| "Given ≤ 200 positive integers each ≤ 100, can they be split into two groups with equal sums?" | "is it possible", values ≤ 100 so total ≤ 20 000 | Feasibility knapsack over sums. → [Equal Sum Partition](../03-subsequence-and-knapsack-dp/04-equal-sum-partition/) |
| "Two words of length ≤ 500. Fewest single-character insert/delete/replace operations to turn one into the other." | "fewest", two strings, n ≤ 500 | 2D string DP, O(n·m). → [Edit Distance](../03-subsequence-and-knapsack-dp/14-edit-distance/) |
| "n ≤ 15 cities with pairwise distances; find the shortest tour visiting each exactly once." | "shortest", n ≤ 15 | Bitmask DP. → [TSP](../04-advanced-dp-patterns/10-traveling-salesperson-problem/) |
| "Count integers between 1 and 10^18 that have no two equal adjacent digits." | count, N ≤ 10^18 | Digit DP. → [Digit DP](../04-advanced-dp-patterns/11-digit-dp/) |

---

## 2. Confirming overlapping subproblems

A problem has **overlapping subproblems** when a plain recursive solution calls itself with the *same arguments* many times. Memoization only helps when this happens. See [00-foundations/04](../00-foundations/04-overlapping-subproblems/) for the full lesson.

### 2.1 The two-level test

1. Write the brute-force recursion as a function of its **changing parameters** only, e.g. `f(i, capacity)`.
2. Draw the recursion tree for a small input, **two or three levels deep**.
3. Write each call as its parameter tuple. **If any tuple appears twice, the problem has overlapping subproblems.**

#### Example A — Unique paths in a grid

`f(r, c)` = number of paths from the top-left corner to cell `(r, c)` moving only right/down. `f(r, c) = f(r−1, c) + f(r, c−1)`.

```
                      f(2,2)
                 /             \
            f(1,2)             f(2,1)
           /      \           /      \
       f(0,2)   [f(1,1)]  [f(1,1)]   f(2,0)
                    ^          ^
                    └── same tuple, computed twice
```

Already at depth 2, `(1,1)` repeats. In a 20×20 grid the same cell is reached by a combinatorial number of call paths — that is the waste memoization removes.

#### Example B — 0/1 knapsack (two parameters, repeat appears at depth 3)

Weights `[1, 2, 3]`, capacity 6. `f(i, cap)` = best value using items `i..2` with remaining capacity `cap`. Left child = skip item `i`, right child = take it.

```
                         f(0,6)
               skip /             \ take w=1
               f(1,6)              f(1,5)
          skip/      \take w=2  skip/     \take w=2
        f(2,6)      f(2,4)     f(2,5)     f(2,3)
       /     \      /    \     /    \     /     \
   f(3,6) [f(3,3)] f(3,4) f(3,1) f(3,5) f(3,2) [f(3,3)] f(3,0)
```

`f(3,3)` is reached by "skip 1, skip 2, take 3" and by "take 1, take 2, skip 3": two different histories that leave the **same remaining problem**. That is the essence of overlap: *different paths, same future*.

### 2.2 When there is **no** overlap

- **Merge sort / binary search**: each call works on a disjoint half. Tuples never repeat → plain divide and conquer, no memo needed.
- **"List every subset / permutation"** ([Subsets](https://leetcode.com/problems/subsets/)): the output itself has 2^n entries, so no cache can make it faster than the output size. That is backtracking, not DP.
- **The state includes the whole history** (e.g. "the list of items taken so far"). Two paths never share a full history, so nothing repeats. Ask: *can I compress the history into a few numbers?* If yes, overlap appears; if no, DP does not apply.

### 2.3 Quick mental shortcut

Count the **distinct** tuples versus the **number of calls**. Unique paths on an r×c grid: r·c distinct tuples, but about C(r+c, r) calls. When distinct tuples ≪ calls, memoization wins.

---

## 3. Confirming optimal substructure (and a counterexample)

A problem has **optimal substructure** when an optimal answer to the whole problem can be **assembled from optimal answers to its subproblems**. See [00-foundations/05](../00-foundations/05-optimal-substructure/).

### 3.1 How to check it

Ask: *"If I fix my first (or last) decision, is the rest of the problem the same kind of problem, and must it be solved optimally too?"*

- **Shortest path in a graph with non-negative weights:** if the best route from A to C passes through B, then its A→B part must be a shortest A→B path. Otherwise, swap in a shorter A→B path and the whole route gets shorter — contradiction. ✔ Optimal substructure (this "cut and paste" argument is the standard proof).
- **Coin change (fewest coins):** if the best way to make 6 uses a coin of 3 last, the remaining 3 must be made with the fewest coins possible. ✔

### 3.2 Counterexample: longest *simple* path

A *simple* path never repeats a vertex. Take the 4-cycle (undirected):

```
   A ─── B
   │     │
   D ─── C
```

- Longest simple path A→B: `A–D–C–B` (3 edges).
- Longest simple path B→C: `B–A–D–C` (3 edges).
- Gluing them: `A–D–C–B–A–D–C` repeats A, D, C — **not a simple path**.
- The real longest simple path A→C is `A–B–C` or `A–D–C` (2 edges), which is *not* built from the optimal sub-answers.

The two subproblems **share resources** (vertices), so solving each optimally on its own breaks the other. When subproblems interfere like this, plain DP fails. (On a **DAG** there are no cycles, the interference disappears, and longest path *is* a DP — see [DP on graphs](../04-advanced-dp-patterns/08-dp-on-graphs/).)

### 3.3 A softer counterexample: the state was too small

[Maximum Product Subarray](https://leetcode.com/problems/maximum-product-subarray/): define `best[i]` = largest product of a subarray ending at `i`. For `[2, −3, −4]`:

- `best[0] = 2`, `best[1] = max(−3, 2·−3) = −3`, `best[2] = max(−4, −3·−4) = 12`. Fine here, but try `[−2, 3, −4]`:
- `best[0] = −2`, `best[1] = max(3, −6) = 3`, `best[2] = max(−4, 3·−4) = −4`. **Wrong** — the true answer is `−2·3·−4 = 24`.

The optimal answer at `i` came from the **smallest** product at `i−1`, which we threw away. Fix: keep both `maxEnding[i]` and `minEnding[i]`. Lesson: *if optimal substructure seems to fail, first try enlarging the state before abandoning DP.*

---

## 4. Choosing the state: 1D, 2D, 3D, bitmask, …

### 4.1 The method: "list everything that changes"

Imagine you are halfway through solving the problem by hand. Write down **every piece of information you need to finish correctly**. Each independent piece becomes one dimension.

1. Write the brute-force recursion.
2. Look at its parameters. Remove anything that never changes (the input array itself).
3. Remove anything that can be **computed** from the other parameters (Cherry Pickup: `c2 = r1 + c1 − r2`).
4. What is left is your state. Its size = product of the ranges.

### 4.2 Rules of thumb

| Situation | State shape | Example |
|---|---|---|
| One pointer moving through one array/string | `dp[i]` (1D) | House Robber, Decode Ways, Word Break |
| Two strings / two arrays, one pointer in each | `dp[i][j]` (2D) | LCS, Edit Distance, Interleaving String |
| A grid with a single walker | `dp[r][c]` (2D) | Unique Paths, Minimum Path Sum |
| A contiguous piece `[i..j]` of one array | `dp[i][j]` (2D, interval) | Burst Balloons, Longest Palindromic Subsequence |
| Items + a capacity/sum | `dp[i][cap]` (2D, often squeezed to 1D) | 0/1 Knapsack, Subset Sum |
| **Extra resource** (k transactions, k stops, budget) | **+1 dimension** | Stock IV: `dp[i][k][holding]` |
| **Extra agent** (two robots, two walkers) | **+1 or +2 dimensions** | Cherry Pickup II: `dp[row][c1][c2]` |
| **Extra flag** (holding a stock? used the deletion? started the number?) | **+1 small dimension (size 2–3)** | Stock with cooldown, digit DP `started` |
| A **set** of ≤ 20 items already used | **bitmask** `dp[mask]` or `dp[mask][last]` | TSP, assignment problems |
| A rooted tree | `dp[node][small flag]`, computed bottom-up | House Robber III, Binary Tree Cameras |
| A number N up to 10^18 | `dp[pos][tight][extra]` (digit DP) | Count numbers with a digit property |

### 4.3 Worked example of the method

*"Buy and sell a stock over n days; at most k transactions; you may hold one share at a time. Maximize profit."*

What do I need to know on day `i` to finish optimally?
- Which day it is → `i` (n values)
- How many transactions I may still start → `t` (k+1 values)
- Whether I currently hold a share → `holding` (2 values)

State `dp[i][t][holding]`, size `n·(k+1)·2`, O(1) work each → O(n·k). Nothing else matters (the price I paid is already subtracted from profit when I bought). → [Stock buy and sell](../04-advanced-dp-patterns/02-stock-buy-and-sell/)

### 4.4 Too-small vs. too-large states

- **Too small** → wrong answers (Section 3.3). Symptom: a counterexample where two histories with the same state need different futures.
- **Too large** → correct but slow, or memory blows up. Symptom: a dimension that can be derived from the others, or one that only ever takes one value.

---

## 5. Identifying each DP family

For each family: **signals**, a **paraphrased example**, the **state**, and **where it is taught**.

### 5.1 Linear (1D) DP
- **Signals:** one array/string, scanned left to right; the decision at `i` depends on a fixed number of previous positions.
- **Example:** "Each day you may solve a question for points, but then must skip the next `b[i]` questions. Maximize points." → `dp[i] = max(dp[i+1], points[i] + dp[i + b[i] + 1])` ([Solving Questions With Brainpower](https://leetcode.com/problems/solving-questions-with-brainpower/)).
- **Taught in:** [01-one-dimensional-dp](../01-one-dimensional-dp/).

### 5.2 Grid DP
- **Signals:** a 2D board; movement restricted to directions that never revisit a cell (right/down, or downward rows) so the dependency graph has no cycles.
- **Example:** "Move from the top-left to the bottom-right of a number grid, only right or down, minimizing the sum." → `dp[r][c] = grid[r][c] + min(dp[r−1][c], dp[r][c−1])`.
- **Warning:** if movement is in all four directions, the cell graph has cycles → this is shortest path (BFS/Dijkstra), **unless** a strict ordering exists such as "move only to a strictly larger neighbour" ([Longest Increasing Path in a Matrix](https://leetcode.com/problems/longest-increasing-path-in-a-matrix/) — memo DFS works because values strictly increase).
- **Taught in:** [02-two-dimensional-and-grid-dp](../02-two-dimensional-and-grid-dp/).

### 5.3 Subsequence DP
A *subsequence* keeps order but may skip elements; a *substring/subarray* is contiguous.
- **Signals:** "subsequence", "delete some characters", "keep the relative order", one or two sequences.
- **Sub-types:**
  - *One sequence, pick an ordered chain* → LIS-style: `dp[i]` = best chain **ending at** `i`.
  - *Two sequences, match them* → LCS-style: `dp[i][j]` over prefixes.
  - *One sequence vs. its reverse / both ends* → palindromic: `dp[i][j]` over a range.
- **Example:** "Envelopes fit inside each other if both width and height are strictly smaller; longest nesting chain?" → sort, then LIS ([Russian Doll Envelopes](https://leetcode.com/problems/russian-doll-envelopes/)).
- **Taught in:** [subsequences fundamentals](../03-subsequence-and-knapsack-dp/01-subsequences-fundamentals/), [LIS](../03-subsequence-and-knapsack-dp/11-longest-increasing-subsequence/), [LCS](../03-subsequence-and-knapsack-dp/12-longest-common-subsequence/).

### 5.4 Knapsack DP
- **Signals:** a collection of items, each **chosen or not** (or chosen several times), subject to a **budget/target sum**; bounded values (≤ 10^4) for the budget.
- **Bounded (0/1) vs. unbounded:**

| | 0/1 knapsack | Unbounded knapsack |
|---|---|---|
| Each item used | at most once | any number of times |
| 2D recurrence "take" term | `dp[i−1][cap − w]` (previous row) | `dp[i][cap − w]` (same row) |
| 1D loop over capacity | **descending** (so each item is counted once) | **ascending** (so an item can be reused) |
| Examples | Subset Sum, Partition, Target Sum, Ones and Zeroes | Coin Change, Coin Change II, Rod Cutting |

- **Counting combinations vs. permutations** (only matters for counting problems):

| | Combinations (order does not matter: {1,2} = {2,1}) | Permutations (order matters: 1+2 ≠ 2+1) |
|---|---|---|
| Outer loop | **items** | **amount** |
| Inner loop | amount | items |
| Example | [Coin Change II](https://leetcode.com/problems/coin-change-ii/) | [Combination Sum IV](https://leetcode.com/problems/combination-sum-iv/) |

  Why: with items outside, coin `c` is fully processed before coin `c+1`, so every counted sequence lists coins in a fixed order — each set counted once. With amount outside, every last-coin choice is tried at every amount, so each ordering is counted.

- **Example:** "Assign + or − to each number so the expression equals target; count assignments." → transform to "count subsets with sum (total + target)/2" ([Target Sum](https://leetcode.com/problems/target-sum/)).
- **Taught in:** [0/1 knapsack](../03-subsequence-and-knapsack-dp/02-zero-one-knapsack/), [unbounded knapsack](../03-subsequence-and-knapsack-dp/07-unbounded-knapsack/), [coin change II](../03-subsequence-and-knapsack-dp/09-coin-change-ii/).

### 5.5 Interval DP
- **Signals:** the answer for a contiguous segment `[i..j]` is built by choosing a **split point `k`** inside it, or by peeling from either end; operations **merge neighbours** or **remove an element so its neighbours become adjacent**; n ≤ 500.
- **Key trick:** think about the **last** operation inside the interval, not the first (Burst Balloons: "which balloon is burst *last* in (l, r)?" — then its neighbours are exactly `l` and `r`).
- **Example:** "Piles of slimes in a row; merging two adjacent piles costs their total; merge everything at minimum cost." → `dp[i][j] = min_k dp[i][k] + dp[k+1][j] + sum(i..j)` ([AtCoder Slimes](https://atcoder.jp/contests/dp/tasks/dp_n)).
- **Order:** by increasing interval length.
- **Taught in:** [interval DP](../04-advanced-dp-patterns/03-interval-dp/), [MCM](../04-advanced-dp-patterns/04-matrix-chain-multiplication/), [burst balloons](../04-advanced-dp-patterns/05-burst-balloons/).

### 5.6 Tree DP
- **Signals:** input is a tree (or a graph with n nodes and n−1 edges); "choose nodes with constraints between parent and child", "longest path in a tree", "sum over all nodes of …".
- **Shape:** post-order DFS; each node returns a small tuple computed from its children's tuples.
- **Example:** "Houses form a binary tree; you cannot rob a node and its direct child. Maximize." → each node returns `(robThis, skipThis)` ([House Robber III](https://leetcode.com/problems/house-robber-iii/)).
- **Extension:** "answer for every node as root" → **rerooting** ([Sum of Distances in Tree](https://leetcode.com/problems/sum-of-distances-in-tree/)).
- **Taught in:** [DP on trees](../04-advanced-dp-patterns/07-dp-on-trees/).

### 5.7 DP on DAGs (and graphs)
- **Signals:** directed graph with **no cycles** (prerequisites, "longest path", "number of paths"); or a cyclic graph with a **bounded step count** ("at most k stops") that unrolls it into layers.
- **Insight:** *every* DP is a DAG: states are nodes, dependencies are edges. Tabulation is processing that DAG in topological order.
- **Example:** "Courses with prerequisites and durations; courses run in parallel; minimum total time?" → `finish[v] = time[v] + max(finish[u])` over prerequisites `u` ([Parallel Courses III](https://leetcode.com/problems/parallel-courses-iii/)).
- **Taught in:** [DP on graphs](../04-advanced-dp-patterns/08-dp-on-graphs/).

### 5.8 Bitmask DP
- **Signals:** **n ≤ 20**; "each item used exactly once", "assign each worker to a task", "visit all nodes", "partition into groups".
- **State:** `dp[mask]` (which items are used — the next item index is often `popcount(mask)`), or `dp[mask][last]` when the order matters through the last element (TSP).
- **Example:** "Pair each element of `a` with a distinct element of `b` (n ≤ 14) to minimize the sum of XORs." → `dp[mask]` = best cost after matching the first `popcount(mask)` elements of `a` to the set `mask` of `b` ([Minimum XOR Sum of Two Arrays](https://leetcode.com/problems/minimum-xor-sum-of-two-arrays/)).
- **Taught in:** [bitmask DP](../04-advanced-dp-patterns/09-bitmask-dp/), [TSP](../04-advanced-dp-patterns/10-traveling-salesperson-problem/).

### 5.9 Digit DP
- **Signals:** "count integers in [L, R] (R up to 10^9 or 10^18) whose digits satisfy …".
- **State:** `f(pos, tight, started, extra)` where `tight` = "the prefix so far equals N's prefix", `started` = "we have placed a non-zero digit" (handles leading zeros), `extra` = whatever the property needs (digit sum mod D, mask of used digits, last digit).
- **Range trick:** `count(L..R) = count(≤ R) − count(≤ L−1)`.
- **Example:** "How many numbers from 1 to N have all distinct digits?" → `extra` = 10-bit mask of used digits ([Count Special Integers](https://leetcode.com/problems/count-special-integers/)).
- **Taught in:** [digit DP](../04-advanced-dp-patterns/11-digit-dp/).

### 5.10 State-machine DP
- **Signals:** at each step you are in one of a few **modes** (holding / not holding / cooling down; last letter was a vowel X; absent count and late streak), and rules say which mode can follow which.
- **How to build it:** draw the modes as circles and allowed moves as arrows. Each arrow is one term of the recurrence.

```
                  buy (−price)
   ┌────────┐ ─────────────────▶ ┌─────────┐
   │  FREE  │                    │ HOLDING │
   └────────┘ ◀───────────────── └─────────┘
      ▲   │      sell (+price)       │  ▲
 rest └───┘   (with cooldown, sell   └──┘ rest
              goes to COOLDOWN, which
              returns to FREE next day)
```

- **Example:** "A string of length n over vowels where each vowel may only be followed by certain vowels; count strings." → `dp[i][lastVowel]` ([Count Vowels Permutation](https://leetcode.com/problems/count-vowels-permutation/)).
- **Taught in:** [state-machine DP](../04-advanced-dp-patterns/12-state-machine-dp/), [stocks](../04-advanced-dp-patterns/02-stock-buy-and-sell/).

### 5.11 Other families worth recognizing
- **Partition DP** ("split the array into contiguous groups"): `dp[i] = best over last group start j` — e.g. [Partition Array for Maximum Sum](https://leetcode.com/problems/partition-array-for-maximum-sum/), [Palindrome Partitioning II](https://leetcode.com/problems/palindrome-partitioning-ii/).
- **Game DP** ("two players, optimal play"): store the **score difference** for the player to move — e.g. [Predict the Winner](https://leetcode.com/problems/predict-the-winner/).
- **Probability DP**: like counting DP but each transition is weighted by its probability — e.g. [Knight Probability in Chessboard](https://leetcode.com/problems/knight-probability-in-chessboard/).
- **Huge n, tiny linear recurrence** → [matrix exponentiation](../05-dp-optimization/09-matrix-exponentiation/).
- **Transition is "max/min over a sliding window"** → [monotonic queue optimization](../05-dp-optimization/04-monotonic-queue-optimization/).

---

## 6. Greedy vs. recursion vs. backtracking vs. DP

### 6.1 Definitions in one line each

- **Greedy:** make the locally best choice and never reconsider. Fast, but only correct when you can *prove* the local choice is always safe (an "exchange argument").
- **Plain recursion:** try every choice by calling yourself on smaller inputs. Correct; exponential if subproblems repeat.
- **Backtracking:** recursion that builds a partial solution, **undoes** choices on the way back, and **prunes** branches that cannot succeed. Use it when you must *list* solutions or when the state cannot be compressed.
- **DP:** recursion + "remember answers to repeated subproblems" (or the bottom-up table equivalent). Needs overlapping subproblems and optimal substructure.

### 6.2 Decision table

| Question to ask | If **yes** → lean towards |
|---|---|
| Must I output **every** solution (all subsets, all paths, all partitions)? | **Backtracking** (output can be exponential; DP cannot beat output size) |
| Can I prove that one local choice is always safe (exchange argument), and do I have a counterexample-free small test? | **Greedy** |
| Do subproblems repeat (Section 2) and does an optimal answer combine optimal sub-answers (Section 3)? | **DP** |
| Do subproblems split into **disjoint** halves with no repetition? | **Divide and conquer / plain recursion** |
| Is n ≤ 20 with "use each item once" constraints and an optimum/count asked? | **Bitmask DP** (or backtracking if listing) |
| Is the graph cyclic with non-negative weights and you need a shortest path? | **Dijkstra/BFS** (not DP over the raw graph) |

### 6.3 Same problem, four lenses: Coin Change with coins `{1, 3, 4}`, amount `6`

**Greedy** (always take the largest coin that fits):
```
6 → take 4 → 2 left → take 1 → 1 left → take 1 → 0      coins used: 3   ✗ WRONG
```
Optimal is `3 + 3` = **2 coins**. Greedy fails because taking 4 "used up" the amount in a way that left an awkward remainder. (Greedy *does* work for "canonical" systems like {1, 5, 10, 25}, but you cannot assume that.)

**Plain recursion** — `f(a) = 1 + min(f(a−1), f(a−3), f(a−4))`, `f(0) = 0`:
```
                    f(6)
           /         |         \
        f(5)       f(3)        f(2)
       / | \       / | \       /
    f(4) f(2) f(1) f(2) f(0) . f(1) ...
          ^          ^
          └──────────┴── f(2) recomputed; grows exponentially with the amount
```
Correct (answer 2), but exponential.

**Backtracking** — build coin lists in non-increasing order, prune when the current count already ≥ best found:
```
[4] → [4,1] → [4,1,1] = 6 ✓ best = 3
[3] → [3,3] = 6 ✓ best = 2
[3,1,...] pruned once count reaches 2
```
Correct and can *list* the coin multiset, but still exponential in the worst case.

**DP** (tabulation), `dp[a]` = fewest coins to make `a`:

| a | 0 | 1 | 2 | 3 | 4 | 5 | 6 |
|---|---|---|---|---|---|---|---|
| dp[a] | 0 | 1 | 2 | 1 | 1 | 2 | **2** |

`dp[6] = 1 + min(dp[5], dp[3], dp[2]) = 1 + min(2, 1, 2) = 2`. Each of the 7 states is computed once with 3 coin checks → O(amount × coins).
→ [Coin Change](../03-subsequence-and-knapsack-dp/08-coin-change/)

### 6.4 When greedy *is* right — and DP still works

[Maximum Length of Pair Chain](https://leetcode.com/problems/maximum-length-of-pair-chain/): choose the most pairs `(a, b)` forming a chain `b₁ < a₂`. Sorting by end and greedily taking the pair that ends earliest is provably optimal (O(n log n)); an LIS-style DP also works (O(n²)). In interviews, mention both and justify greedy with the exchange argument: "the pair ending earliest leaves the most room for everything after it."

[Jump Game](https://leetcode.com/problems/jump-game/) is similar: DP over "can I reach i" works in O(n²); tracking the furthest reachable index is an O(n) greedy.

> **Tip:** if you suspect greedy, spend two minutes trying to break it with a 3–4 element input. If you cannot, and you can state why the choice is safe, use greedy. Otherwise, use DP.

---

## 7. Constraint → complexity → technique table

Assume ~10^8 simple operations per second in Java and a 1–2 s limit.

| Largest n | Feasible complexity | Likely techniques |
|---|---|---|
| ≤ 10–11 | O(n!) | Permutation backtracking |
| ≤ 20 (up to ~24) | O(2^n · n) to O(2^n · n²) | Bitmask DP, subset backtracking, [SOS DP](../05-dp-optimization/07-sos-dp/) |
| ≤ 40 | O(2^(n/2) · n) | Meet in the middle ([Partition Array Into Two Arrays to Minimize Sum Difference](https://leetcode.com/problems/partition-array-into-two-arrays-to-minimize-sum-difference/)) |
| ≤ 100–500 | O(n³) | Interval DP, MCM, split-point DP, Floyd–Warshall |
| ≤ 1 000–5 000 | O(n²) or O(n² log n) | 2D DP over (i, j): LCS, edit distance, LIS O(n²), partition DP |
| ≤ 10^5–10^6 | O(n log n) or O(n) | 1D DP, LIS with binary search, monotonic queue DP, prefix sums, greedy |
| ≤ 10^9–10^18 | O(log n) or O(digits) | Matrix exponentiation, digit DP, math |
| n ≤ 100 **and** capacity/sum ≤ 10^4–10^5 | O(n · capacity) | Knapsack over sums ("pseudo-polynomial") |
| n ≤ 100 but values ≤ 10^9 and **value total** ≤ 10^5 | O(n · totalValue) | Swap the roles: `dp[value] = min weight` ([AtCoder Knapsack 2](https://atcoder.jp/contests/dp/tasks/dp_e)) |
| Two lengths n, m ≤ 1000 each | O(n · m) | Two-string DP |
| n ≤ 10^4, k ≤ 100 | O(n · k) or O(n · k · log n) | Layered DP, [divide & conquer optimization](../05-dp-optimization/05-divide-and-conquer-optimization/) |

Memory check: an `int[5000][5000]` is 25 million ints ≈ 100 MB — usually too much. That is a signal to [space-optimize to two rows](../05-dp-optimization/02-rolling-arrays/).

---

## 8. Master decision flowchart

```
                         ┌───────────────────────────────┐
                         │  Read statement + constraints │
                         └───────────────┬───────────────┘
                                         ▼
                  ┌────────────────────────────────────────────┐
                  │ Must you LIST all solutions (all subsets,  │── yes ──▶ BACKTRACKING
                  │ all paths, all partitions)?                │
                  └───────────────────┬────────────────────────┘
                                      │ no  (count / min / max / possible?)
                                      ▼
                  ┌────────────────────────────────────────────┐
                  │ Is a local choice provably safe?           │── yes ──▶ GREEDY
                  │ (exchange argument + no small counterex.)  │           (sort + scan)
                  └───────────────────┬────────────────────────┘
                                      │ no / not sure
                                      ▼
                  ┌────────────────────────────────────────────┐
                  │ Write brute-force recursion f(params).     │
                  │ Do param tuples repeat in the tree?        │── no ───▶ DIVIDE & CONQUER /
                  └───────────────────┬────────────────────────┘           plain recursion
                                      │ yes  → it is DP. Now pick the family:
                                      ▼
     ┌─────────────────────────────────────────────────────────────────────────────┐
     │ What does the input look like?                                              │
     └──┬──────────┬──────────┬──────────┬──────────┬──────────┬──────────┬────────┘
        ▼          ▼          ▼          ▼          ▼          ▼          ▼
     one array  two seqs    grid      items +     tree      DAG /      number
     / string   / strings  (no back-  budget/            k-limited    N ≤ 10^18
        │          │       tracking)   target              graph        │
        │          ▼          ▼          ▼          ▼          ▼          ▼
        │      LCS-style   GRID DP   KNAPSACK   TREE DP    DAG DP    DIGIT DP
        │      dp[i][j]    dp[r][c]  dp[i][cap] post-order topo order f(pos,tight,..)
        │                            ├ reuse? → unbounded (ascending loop)
        │                            └ count? → order matters? → permutations
        │                                                     else combinations
        ▼
   ┌───────────────────────────────────────────────────────────┐
   │ One sequence — which sub-pattern?                         │
   │  • answer on a segment [i..j], split point / merge   → INTERVAL DP (n ≤ 500)
   │  • ordered chain, "subsequence", ends at i            → LIS-style dp[i]
   │  • few modes (holding, cooldown, last letter)         → STATE-MACHINE DP
   │  • cut into contiguous groups                         → PARTITION DP
   │  • otherwise: look back a fixed number of steps       → LINEAR 1D DP
   └───────────────────────────────────────────────────────────┘
                                      │
                                      ▼
                  ┌────────────────────────────────────────────┐
                  │ n ≤ 20 and "each item exactly once"?       │── yes ──▶ add BITMASK to state
                  ├────────────────────────────────────────────┤
                  │ Extra resource/agent/flag?                 │── yes ──▶ add a DIMENSION
                  ├────────────────────────────────────────────┤
                  │ states × work > 10^8 ?                     │── yes ──▶ OPTIMIZE (Module 05):
                  └────────────────────────────────────────────┘           rolling rows, prefix max,
                                                                           monotonic queue, D&C, CHT,
                                                                           Knuth, matrix power
```

---

## 9. Pattern → template recurrence → canonical problems → folder

Full details (base cases, order, complexity) are in [RECURRENCE_CHEATSHEET.md](RECURRENCE_CHEATSHEET.md).

| Pattern | Template recurrence | Canonical problems | Course folder |
|---|---|---|---|
| Linear, fixed look-back | `dp[i] = dp[i−1] ⊕ dp[i−2]` | [Climbing Stairs](https://leetcode.com/problems/climbing-stairs/), [House Robber](https://leetcode.com/problems/house-robber/), [Decode Ways](https://leetcode.com/problems/decode-ways/) | [01-one-dimensional-dp](../01-one-dimensional-dp/) |
| Linear, look-back over all j | `dp[i] = best_j<i (dp[j] + cost(j,i))` | [Word Break](https://leetcode.com/problems/word-break/), [Perfect Squares](https://leetcode.com/problems/perfect-squares/) | [06/01-one-dimensional](../06-leetcode-practice/01-one-dimensional/) |
| Kadane (best ending here) | `end[i] = max(a[i], end[i−1] + a[i])` | [Maximum Subarray](https://leetcode.com/problems/maximum-subarray/), [Maximum Product Subarray](https://leetcode.com/problems/maximum-product-subarray/) | [01/09](../01-one-dimensional-dp/09-maximum-subarray-kadane-vs-dp/) |
| Grid paths | `dp[r][c] = cell ⊕ min/+(dp[r−1][c], dp[r][c−1])` | [Unique Paths](https://leetcode.com/problems/unique-paths/), [Minimum Path Sum](https://leetcode.com/problems/minimum-path-sum/), [Triangle](https://leetcode.com/problems/triangle/) | [02-two-dimensional-and-grid-dp](../02-two-dimensional-and-grid-dp/) |
| Grid, multiple agents | `dp[r][c1][c2] = cells + max over 9 moves` | [Cherry Pickup II](https://leetcode.com/problems/cherry-pickup-ii/), [Cherry Pickup](https://leetcode.com/problems/cherry-pickup/) | [02/09](../02-two-dimensional-and-grid-dp/09-cherry-pickup/) |
| 0/1 knapsack | `dp[i][c] = max(dp[i−1][c], v + dp[i−1][c−w])` | [Partition Equal Subset Sum](https://leetcode.com/problems/partition-equal-subset-sum/), [Target Sum](https://leetcode.com/problems/target-sum/), [Ones and Zeroes](https://leetcode.com/problems/ones-and-zeroes/) | [03/02](../03-subsequence-and-knapsack-dp/02-zero-one-knapsack/) |
| Unbounded knapsack (min) | `dp[a] = min_c (dp[a−c] + 1)` | [Coin Change](https://leetcode.com/problems/coin-change/), [Perfect Squares](https://leetcode.com/problems/perfect-squares/) | [03/08](../03-subsequence-and-knapsack-dp/08-coin-change/) |
| Count combinations | items outer: `dp[a] += dp[a−c]` | [Coin Change II](https://leetcode.com/problems/coin-change-ii/) | [03/09](../03-subsequence-and-knapsack-dp/09-coin-change-ii/) |
| Count permutations | amount outer: `dp[a] += dp[a−c]` | [Combination Sum IV](https://leetcode.com/problems/combination-sum-iv/), [CSES Dice Combinations](https://cses.fi/problemset/task/1633) | [03/09](../03-subsequence-and-knapsack-dp/09-coin-change-ii/) |
| LIS | `dp[i] = 1 + max_{j<i, a[j]<a[i]} dp[j]` | [Longest Increasing Subsequence](https://leetcode.com/problems/longest-increasing-subsequence/), [Russian Doll Envelopes](https://leetcode.com/problems/russian-doll-envelopes/) | [03/11](../03-subsequence-and-knapsack-dp/11-longest-increasing-subsequence/) |
| Two-string (LCS family) | match → `dp[i−1][j−1] + …`, else combine `dp[i−1][j]`, `dp[i][j−1]` | [Longest Common Subsequence](https://leetcode.com/problems/longest-common-subsequence/), [Edit Distance](https://leetcode.com/problems/edit-distance/), [Distinct Subsequences](https://leetcode.com/problems/distinct-subsequences/) | [03/12](../03-subsequence-and-knapsack-dp/12-longest-common-subsequence/), [04/01](../04-advanced-dp-patterns/01-dp-on-strings/) |
| Palindromic range | `dp[i][j]` from `dp[i+1][j−1]`, `dp[i+1][j]`, `dp[i][j−1]` | [Longest Palindromic Subsequence](https://leetcode.com/problems/longest-palindromic-subsequence/), [Palindromic Substrings](https://leetcode.com/problems/palindromic-substrings/) | [03/13](../03-subsequence-and-knapsack-dp/13-longest-palindromic-subsequence/) |
| Interval (split point) | `dp[i][j] = min_k dp[i][k] + dp[k+1][j] + cost` | [Burst Balloons](https://leetcode.com/problems/burst-balloons/), [Minimum Cost to Cut a Stick](https://leetcode.com/problems/minimum-cost-to-cut-a-stick/) | [04/03](../04-advanced-dp-patterns/03-interval-dp/) |
| Partition | `dp[i] = best_j (dp[j] + cost(j+1..i))` | [Palindrome Partitioning II](https://leetcode.com/problems/palindrome-partitioning-ii/), [Partition Array for Maximum Sum](https://leetcode.com/problems/partition-array-for-maximum-sum/) | [04/06](../04-advanced-dp-patterns/06-palindrome-partitioning/) |
| Game (score difference) | `d[i][j] = max(a[i] − d[i+1][j], a[j] − d[i][j−1])` | [Predict the Winner](https://leetcode.com/problems/predict-the-winner/), [Stone Game](https://leetcode.com/problems/stone-game/) | [04/03](../04-advanced-dp-patterns/03-interval-dp/) |
| State machine | `dp[i][mode] = best over arrows into mode` | [Best Time to Buy and Sell Stock with Cooldown](https://leetcode.com/problems/best-time-to-buy-and-sell-stock-with-cooldown/), [Count Vowels Permutation](https://leetcode.com/problems/count-vowels-permutation/) | [04/12](../04-advanced-dp-patterns/12-state-machine-dp/) |
| Tree | node returns tuple from children's tuples | [House Robber III](https://leetcode.com/problems/house-robber-iii/), [Binary Tree Maximum Path Sum](https://leetcode.com/problems/binary-tree-maximum-path-sum/) | [04/07](../04-advanced-dp-patterns/07-dp-on-trees/) |
| DAG | `dp[v] = best over edges (dp[u] + w)` in topo order | [AtCoder Longest Path](https://atcoder.jp/contests/dp/tasks/dp_g), [Parallel Courses III](https://leetcode.com/problems/parallel-courses-iii/) | [04/08](../04-advanced-dp-patterns/08-dp-on-graphs/) |
| Bitmask | `dp[mask \| 1<<j][j] = best(dp[mask][i] + d[i][j])` | [Find the Shortest Superstring](https://leetcode.com/problems/find-the-shortest-superstring/), [Minimum XOR Sum of Two Arrays](https://leetcode.com/problems/minimum-xor-sum-of-two-arrays/) | [04/09](../04-advanced-dp-patterns/09-bitmask-dp/) |
| Digit | `f(pos, tight, started, extra)` | [Number of Digit One](https://leetcode.com/problems/number-of-digit-one/), [Numbers At Most N Given Digit Set](https://leetcode.com/problems/numbers-at-most-n-given-digit-set/) | [04/11](../04-advanced-dp-patterns/11-digit-dp/) |
| Sliding-window transition | `dp[i] = a[i] + max(dp[i−k..i−1])` with a deque | [Jump Game VI](https://leetcode.com/problems/jump-game-vi/), [Constrained Subsequence Sum](https://leetcode.com/problems/constrained-subsequence-sum/) | [05/04](../05-dp-optimization/04-monotonic-queue-optimization/) |
| Linear recurrence, huge n | `[f(n+1), f(n)]ᵀ = M · [f(n), f(n−1)]ᵀ` | [CSES Fibonacci Numbers](https://cses.fi/problemset/task/1722) | [05/09](../05-dp-optimization/09-matrix-exponentiation/) |

---

## 10. Worked decoding: five statements, start to finish

Each walk-through follows the same five moves: **clues → brute force → overlap? → state → family**.

### 10.1 "Minimum cost to buy travel passes"
*You travel on certain days of the year. A 1-day pass, 7-day pass and 30-day pass each have a price. Minimum total cost to cover all travel days?* ([Minimum Cost For Tickets](https://leetcode.com/problems/minimum-cost-for-tickets/))

- **Clues:** "minimum", three choices at each travel day, days ≤ 365.
- **Brute force:** at the first uncovered travel day, try each pass, recurse on the next uncovered day.
- **Overlap:** buying a 7-day pass then a 1-day pass may land on the same "next uncovered day" as a 30-day pass from an earlier day → yes.
- **State:** `dp[d]` = min cost to cover all travel days ≥ `d`. One moving pointer → 1D.
- **Family:** linear DP with look-back of 1, 7, 30.

### 10.2 "Split a string into dictionary words"
*Can the string be segmented into a sequence of dictionary words?* ([Word Break](https://leetcode.com/problems/word-break/))

- **Clues:** "can", choice of where the next word ends.
- **State:** `ok[i]` = the first `i` characters can be segmented. Each `ok[i]` checks every split `j < i`.
- **Family:** linear DP with full look-back (partition flavour). O(n²) substring checks.

### 10.3 "Two robots collect cherries"
*Two robots start at the top corners of a grid and move down one row per step (down-left, down, down-right). Same cell counts once. Max cherries?* ([Cherry Pickup II](https://leetcode.com/problems/cherry-pickup-ii/))

- **List what changes:** row (shared — both robots move together), column of robot 1, column of robot 2 → `dp[row][c1][c2]`.
- **Family:** grid DP + extra agent → 3D. 9 transitions per state. O(rows · cols² · 9).

### 10.4 "Smallest team covering all skills"
*≤ 16 required skills, ≤ 60 people each with a subset of skills. Smallest team covering all skills.* ([Smallest Sufficient Team](https://leetcode.com/problems/smallest-sufficient-team/))

- **Clues:** "≤ 16 skills" → the *skills* (not the people) form the mask.
- **State:** `dp[mask]` = smallest team covering exactly the skill set `mask` (store team or its size + parent).
- **Family:** bitmask knapsack: each person is an item that ORs their skills into the mask. O(2^16 · 60).

### 10.5 "Fewest swaps to make both sequences increasing"
*Two equal-length arrays; you may swap `a[i]` with `b[i]`. Fewest swaps so both become strictly increasing.* ([Minimum Swaps To Make Sequences Increasing](https://leetcode.com/problems/minimum-swaps-to-make-sequences-increasing/))

- **List what changes:** index `i`, and *whether position `i−1` was swapped* (that decides what `a[i−1]` and `b[i−1]` are).
- **State:** `keep[i]`, `swap[i]` → state machine with 2 modes.
- **Family:** state-machine DP. O(n).

---

## 11. Recognition checklist (one screen)

```
[ ] What is asked?  count (+) / min / max / possible (OR) / list (→ backtracking)
[ ] Constraints?    n ≤ 20 → mask   n ≤ 500 → n³   n ≤ 5000 → n²   n ≥ 10^5 → n log n
                    values ≤ 10^4 → knapsack over sums   N ≤ 10^18 → digit DP / matrix power
[ ] Input shape?    array | 2 strings | grid | items+budget | tree | DAG | number | small set
[ ] Choices?        list them; they become the terms of the recurrence
[ ] Greedy safe?    try to break it with a 3-element example first
[ ] Overlap?        two levels of the recursion tree; repeated tuple?
[ ] State?          list everything that changes; drop constants and derived values
[ ] Extra dim?      resource (k) / second agent / flag (holding, started, tight)
[ ] Size check?     (#states × work per state) ≤ 10^8
[ ] Order?          which states feed the current one → loop direction
```

---

## 12. Drill: identify the pattern (18 questions)

For each statement, decide **(a)** DP or not, **(b)** the family, **(c)** the state, **(d)** the recurrence shape. Write your answer *before* opening the hint. Statements are paraphrased; some match a real problem (linked in the answer).

**1.** You roll a die repeatedly; count the ordered sequences of rolls whose sum is exactly n (n ≤ 10^6), modulo 10^9+7.
<details><summary>Answer</summary>

Counting **permutations** (order of rolls matters) → linear 1D. `dp[s] = Σ_{d=1..6} dp[s−d]`, `dp[0] = 1`. O(6n). Compare [CSES Dice Combinations](https://cses.fi/problemset/task/1633).
</details>

**2.** Given coin values and a target, count the *sets* of coins (unlimited copies) that sum to the target; `{1,2}` and `{2,1}` are the same.
<details><summary>Answer</summary>

Unbounded knapsack, counting **combinations** → loop over coins outside, amounts inside (ascending). `dp[a] += dp[a − coin]`, `dp[0] = 1`. [Coin Change II](https://leetcode.com/problems/coin-change-ii/).
</details>

**3.** A bookshop sells n ≤ 1000 books, each with a price and a page count. With budget x ≤ 10^5, maximize total pages; each book at most once.
<details><summary>Answer</summary>

**0/1 knapsack** (budget = capacity, pages = value). 1D `dp[c]`, loop budget **descending**. O(n·x) = 10^8 simple ops — tight but intended. [CSES Book Shop](https://cses.fi/problemset/task/1158).
</details>

**4.** Fewest characters to insert anywhere in a string (length ≤ 500) to make it a palindrome.
<details><summary>Answer</summary>

Palindromic range / interval DP. Answer = `n − LPS(s)` where LPS = longest palindromic subsequence, or directly `dp[i][j] = s[i]==s[j] ? dp[i+1][j−1] : 1 + min(dp[i+1][j], dp[i][j−1])`. O(n²). [Minimum Insertion Steps to Make a String Palindrome](https://leetcode.com/problems/minimum-insertion-steps-to-make-a-string-palindrome/).
</details>

**5.** n ≤ 16 workers and n jobs, `cost[w][j]` given; each worker does exactly one job. Minimize total cost.
<details><summary>Answer</summary>

**Bitmask DP**. `dp[mask]` = min cost after assigning workers `0..popcount(mask)−1` to the job set `mask`. Transition: try each free job for the next worker. O(2^n · n). (Hungarian algorithm also works, but bitmask is the DP answer.)
</details>

**6.** Count integers from 1 to N (N ≤ 10^18) whose digit sum is a multiple of D (D ≤ 100).
<details><summary>Answer</summary>

**Digit DP**. `f(pos, tight, sumMod)`; ~19 · 2 · 100 states × 10 digits. Subtract 1 if you counted 0. [AtCoder Digit Sum](https://atcoder.jp/contests/dp/tasks/dp_s).
</details>

**7.** In a binary tree, choose a set of nodes with no chosen node adjacent to a chosen parent; maximize the sum of values.
<details><summary>Answer</summary>

**Tree DP**. Each node returns `(take, skip)`: `take = val + skip(L) + skip(R)`, `skip = max(take(L), skip(L)) + max(take(R), skip(R))`. O(n). [House Robber III](https://leetcode.com/problems/house-robber-iii/).
</details>

**8.** Daily stock prices; unlimited transactions, but after selling you must wait one day before buying again. Maximize profit.
<details><summary>Answer</summary>

**State machine** with modes HOLDING / FREE (and the cooldown handled by using `free[i−2]` when buying). `hold[i] = max(hold[i−1], free[i−2] − p[i])`, `free[i] = max(free[i−1], hold[i−1] + p[i])`. O(n). [Best Time to Buy and Sell Stock with Cooldown](https://leetcode.com/problems/best-time-to-buy-and-sell-stock-with-cooldown/).
</details>

**9.** Maximum sum of a non-empty subarray, where you may delete **at most one** element from it (the result must stay non-empty).
<details><summary>Answer</summary>

Kadane + a **flag** dimension (state machine). `noDel[i] = max(a[i], noDel[i−1] + a[i])`, `oneDel[i] = max(oneDel[i−1] + a[i], noDel[i−1])` (the second term deletes `a[i]`). Answer = max over all i of both. O(n). Lesson: "at most one exception" → +1 dimension of size 2.
</details>

**10.** Minimum number of insert/delete/replace operations to convert word A into word B (lengths ≤ 500).
<details><summary>Answer</summary>

**Two-string DP**. `dp[i][j]` over prefixes; match → `dp[i−1][j−1]`, else `1 + min(delete, insert, replace)`. O(n·m). [Edit Distance](https://leetcode.com/problems/edit-distance/).
</details>

**11.** n ≤ 400 slimes in a row; merging two **adjacent** slimes costs the sum of their sizes; merge all into one at minimum cost.
<details><summary>Answer</summary>

**Interval DP** with a split point: `dp[i][j] = min_k (dp[i][k] + dp[k+1][j]) + sum(i..j)`. O(n³) = 6.4·10^7. [AtCoder Slimes](https://atcoder.jp/contests/dp/tasks/dp_n). Note: **not** Huffman greedy, because only adjacent slimes may merge.
</details>

**12.** A grid of cherries; two robots start at the two top corners and each step both move down one row (diagonally or straight). A shared cell is collected once. Max cherries.
<details><summary>Answer</summary>

**Grid DP + extra agent** → `dp[row][c1][c2]`, 9 transitions. O(R·C²·9). [Cherry Pickup II](https://leetcode.com/problems/cherry-pickup-ii/).
</details>

**13.** A directed graph with no cycles (n, m ≤ 10^5). Length (in edges) of the longest directed path.
<details><summary>Answer</summary>

**DAG DP**. `dp[v] = max over v→u of 1 + dp[u]`, computed by memo DFS or in reverse topological order. O(n + m). [AtCoder Longest Path](https://atcoder.jp/contests/dp/tasks/dp_g). (On a graph *with* cycles this would be NP-hard — see Section 3.2.)
</details>

**14.** Given n intervals, choose the maximum number of pairwise non-overlapping ones.
<details><summary>Answer</summary>

**Not DP — greedy.** Sort by end, take every interval whose start is after the last taken end. Exchange argument: the earliest-ending interval leaves the most room. O(n log n). (A DP also works but is slower.) Similar: [Maximum Length of Pair Chain](https://leetcode.com/problems/maximum-length-of-pair-chain/).
</details>

**15.** Return **every** combination of candidate numbers (reusable) that sums to target.
<details><summary>Answer</summary>

**Not DP — backtracking.** You must output each combination, so the work is at least the output size. Prune when the running sum exceeds the target. [Combination Sum](https://leetcode.com/problems/combination-sum/). (If the question only asked *how many*, it would be Coin Change II.)
</details>

**16.** Count the ways to tile a 2 × n board with dominoes, n ≤ 10^18, modulo 10^9+7.
<details><summary>Answer</summary>

The DP is `f(n) = f(n−1) + f(n−2)` (Fibonacci), but n is too large to loop. → **Matrix exponentiation** of `[[1,1],[1,0]]`, O(2³ · log n). [Matrix exponentiation](../05-dp-optimization/09-matrix-exponentiation/).
</details>

**17.** Array of n ≤ 10^5 integers, start at index 0, each jump moves forward 1..k steps (k ≤ 10^5); your score is the sum of visited values. Maximize the score at the last index.
<details><summary>Answer</summary>

Linear DP `dp[i] = a[i] + max(dp[i−k..i−1])` — O(n·k) = 10^10 is too slow, so keep a **monotonic deque** of the window maximum → O(n). [Jump Game VI](https://leetcode.com/problems/jump-game-vi/).
</details>

**18.** Split an array of n ≤ 1000 numbers into exactly m ≤ 50 contiguous parts so the largest part-sum is as small as possible.
<details><summary>Answer</summary>

**Partition DP**: `dp[k][i] = min_j max(dp[k−1][j], sum(j+1..i))`, O(m·n²) = 5·10^7 — fine. Alternative non-DP: binary search on the answer + greedy check, O(n log(sum)). Mention both. [Split Array Largest Sum](https://leetcode.com/problems/split-array-largest-sum/).
</details>

---

**Next:** memorize the templates in [RECURRENCE_CHEATSHEET.md](RECURRENCE_CHEATSHEET.md) · avoid the traps in [COMMON_MISTAKES.md](COMMON_MISTAKES.md) · practise the full routine with [INTERVIEW_STRATEGY.md](INTERVIEW_STRATEGY.md) · back to the [module README](README.md).
