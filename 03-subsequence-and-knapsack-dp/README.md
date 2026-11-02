# Module 03 — Subsequence and Knapsack DP

> **Prerequisites:** [Module 00 — Foundations](../00-foundations/), [Module 01 — One-Dimensional DP](../01-one-dimensional-dp/), [Module 02 — Two-Dimensional and Grid DP](../02-two-dimensional-and-grid-dp/) · **Estimated time:** 4–6 weeks at 1–2 hours a day

## What this module is about

Until now every state had a fixed shape: "position `i` in an array" or "cell `(r, c)` in a grid". In this module the state starts to carry **what you have done so far**:

- "I am at item `i` and the bag still has room for `c` kg" (knapsack),
- "I am at index `i` and the chosen numbers add up to `s`" (subset sum, target sum),
- "I am at position `i` in the first string and `j` in the second" (LCS, edit distance).

Almost every medium/hard DP problem on LeetCode is a variation of one of these shapes. The module has two halves.

**Part A — choosing a subset (topics 01–10).** You walk through a list of items and decide, for each, *take it* or *skip it*. This is the pick/not-pick idea you met in [Maximum Sum of Non-Adjacent Elements](../01-one-dimensional-dp/05-maximum-sum-of-non-adjacent-elements/), plus one extra number in the state (capacity, target, amount). You learn the 0/1 knapsack, its many disguises (subset sum, partitions, target sum), and the unbounded version where each item can be used again (coins, rods).

**Part B — subsequences of sequences and strings (topics 11–16).** The state becomes one or two positions inside sequences, and the choices are "match these characters" or "move past one of them". This covers LIS, LCS and the classic string problems built on LCS.

## Topic order

| # | Topic | What it teaches in one line |
|---|---|---|
| 01 | [Subsequences Fundamentals](01-subsequences-fundamentals/) | Subarray vs subsequence vs subset; the take/skip recursion; turning "index + running sum" into a DP state. |
| 02 | [0/1 Knapsack](02-zero-one-knapsack/) | The parent problem: state `(item, capacity)`, full table, 2-row and 1-row versions with the reverse loop. |
| 03 | [Subset Sum](03-subset-sum/) | Knapsack with yes/no answers: the target plays the role of capacity. |
| 04 | [Equal Sum Partition](04-equal-sum-partition/) | Reduce "split into two equal halves" to subset sum with `total / 2`. |
| 05 | [Partition With Minimum Difference](05-partition-with-minimum-difference/) | Read the whole last row of the subset-sum table to find the closest split. |
| 06 | [Target Sum](06-target-sum/) | Turn `+/-` signs into "count subsets with sum `(total + target) / 2`"; zeros and offsets. |
| 07 | [Unbounded Knapsack](07-unbounded-knapsack/) | Items can be reused: stay on index `i` after taking; the forward capacity loop. |
| 08 | [Coin Change](08-coin-change/) | Fewest coins for an amount: unbounded knapsack with `min`, infinity sentinels, why greedy fails. |
| 09 | [Coin Change II](09-coin-change-ii/) | Count the ways to make an amount; why loop order decides combinations vs permutations. |
| 10 | [Rod Cutting](10-rod-cutting/) | Unbounded knapsack where piece lengths are the items and prices the values. |
| 11 | [Longest Increasing Subsequence](11-longest-increasing-subsequence/) | The O(n²) DP over "last element chosen", then the O(n log n) patience idea. |
| 12 | [Longest Common Subsequence](12-longest-common-subsequence/) | Two indices, one per string: the template for every two-string DP. |
| 13 | [Longest Palindromic Subsequence](13-longest-palindromic-subsequence/) | LCS of a string with its reverse, and the direct interval view. |
| 14 | [Edit Distance](14-edit-distance/) | Insert / delete / replace as three transitions on the LCS grid. |
| 15 | [Distinct Subsequences](15-distinct-subsequences/) | Counting (not optimizing) on the two-string grid; big-number care. |
| 16 | [Shortest Common Supersequence](16-shortest-common-supersequence/) | Rebuild an actual answer string by walking back through the LCS table. |

Study the topics in order. Topics 03–06 are short once 02 makes sense, because each one is "0/1 knapsack with a different question". Topics 07–10 are the same story for unbounded knapsack.

## How long it takes

| Part | Topics | Suggested time |
|---|---|---|
| Subsequences and 0/1 knapsack | 01–02 | 4–6 hours (do not rush 02; it is the parent of everything up to 10) |
| 0/1 knapsack family | 03–06 | 5–7 hours |
| Unbounded knapsack family | 07–10 | 4–6 hours |
| Sequence and string subsequences | 11–16 | 10–14 hours |

Add the practice problems at the end of each topic README: about 2–3 extra hours per topic if you solve all of them.

## How to study each topic

1. Read the topic `README.md` (5 minutes) to see what is new.
2. Work through `walkthrough.md` with paper. Fill in the DP tables yourself before you look at the filled versions.
3. Run the files in `solution/` (`java Tabulation.java`), then change a test and predict the output before running again.
4. Do the exercises at the end of the walkthrough without looking at the answers.
5. Solve at least the first two practice problems in the README before you move on.

## After this module you can…

- [ ] Recognize "choose a subset of items under a limit" problems, even when they are disguised as partitions, signs or coins.
- [ ] Write the state `(index, remaining capacity)` or `(index, running sum)` and its take/skip recurrence without help.
- [ ] Decide between the 0/1 recurrence (`i + 1` after taking) and the unbounded one (`i` after taking), and pick the matching loop direction in 1-D.
- [ ] Choose the right combining operation: `max` / `min` for optimization, `||` for feasibility, `+` for counting.
- [ ] Handle sentinels (`INF`, `-1`), offsets for negative sums, and parity / range checks before running a DP.
- [ ] Solve two-sequence problems (LCS, edit distance and friends) with a 2-D table indexed by one position in each sequence.

## Navigation

⬅ Previous module: [Two-Dimensional and Grid DP](../02-two-dimensional-and-grid-dp/) · ➡ Next module: [Advanced DP Patterns](../04-advanced-dp-patterns/) · 🚀 Start: [01 — Subsequences Fundamentals](01-subsequences-fundamentals/)
