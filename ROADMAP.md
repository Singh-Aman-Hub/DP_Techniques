# Learning Roadmap

The complete sequence from "what is DP?" to Hard LeetCode problems. Study in this order; each step assumes the ones before it.

**Pace assumptions:** about 8–10 hours per week. Faster is fine, but do not skip the exercises or the "write it yourself" step. Total: about **16 weeks**.

**Legend:** 🧠 concept · 🧩 core problem · 🏋️ practice set · 🔁 revision checkpoint

---

## Phase 1 — Foundations (Weeks 1–2)

*Goal: understand why DP works and convert any recursion into memoization and tabulation mechanically.*

| # | Topic | Type | Key idea you must walk away with |
|---|---|---|---|
| 1 | [What is Dynamic Programming?](00-foundations/01-what-is-dynamic-programming/README.md) | 🧠 | DP = solve each subproblem once and reuse the answer |
| 2 | [Recursion fundamentals](00-foundations/02-recursion-fundamentals/README.md) | 🧠 | Base case + recursive case; trust the recursion |
| 3 | [Recursion trees & brute force](00-foundations/03-recursion-trees-and-brute-force/README.md) | 🧠 | Draw the tree → count nodes → see the exponential cost |
| 4 | [Overlapping subproblems](00-foundations/04-overlapping-subproblems/README.md) | 🧠 | The same call appears many times in the tree |
| 5 | [Optimal substructure](00-foundations/05-optimal-substructure/README.md) | 🧠 | Best answer is built from best sub-answers |
| 6 | [Memoization](00-foundations/06-memoization/README.md) | 🧠 | Cache + sentinel + check-before-compute |
| 7 | [Tabulation](00-foundations/07-tabulation/README.md) | 🧠 | Fill a table from base cases upward |
| 8 | [Top-down vs bottom-up](00-foundations/08-top-down-vs-bottom-up/README.md) | 🧠 | Same problem, four implementations, compared |
| 9 | [State representation](00-foundations/09-state-representation/README.md) | 🧠 | The state = minimum info describing what's left |
| 10 | [Transitions & base cases](00-foundations/10-state-transitions-and-base-cases/README.md) | 🧠 | Choices → recurrence; smallest states → base cases |
| 11 | [Dependencies & computation order](00-foundations/11-transition-dependencies-and-computation-order/README.md) | 🧠 | Loop order must follow the dependency arrows |
| 12 | [Complexity in DP](00-foundations/12-time-and-space-complexity-in-dp/README.md) | 🧠 | #states × work per state |
| 13 | [Recursion → memoization](00-foundations/13-converting-recursion-to-memoization/README.md) | 🧠 | A 4-step mechanical recipe |
| 14 | [Memoization → tabulation](00-foundations/14-converting-memoization-to-tabulation/README.md) | 🧠 | Calls become table reads; base cases become init |
| 15 | [Space optimization basics](00-foundations/15-space-optimization-basics/README.md) | 🧠 | Keep only the states you still read |
| 16 | [The DP problem-solving framework](00-foundations/16-the-dp-problem-solving-framework/README.md) | 🧠 | The 7 questions, used for every problem from now on |

🔁 **Checkpoint 1:** without notes, write Fibonacci four ways and explain the complexity of each.

---

## Phase 2 — One-dimensional DP (Weeks 3–4)

*Goal: one changing index; pick/not-pick; "ending at i" vs "first i elements".*

| # | Topic | Type | Judge |
|---|---|---|---|
| 1 | [Fibonacci Number](01-one-dimensional-dp/01-fibonacci-number/README.md) | 🧩 | LC 509 (Easy) |
| 2 | [Climbing Stairs](01-one-dimensional-dp/02-climbing-stairs/README.md) | 🧩 | LC 70 (Easy) |
| 3 | [Min Cost Climbing Stairs](01-one-dimensional-dp/03-minimum-cost-climbing-stairs/README.md) | 🧩 | LC 746 (Easy) |
| 4 | [Frog Jump](01-one-dimensional-dp/04-frog-jump/README.md) | 🧩 | AtCoder EDPC A, B |
| 5 | [Max Sum of Non-Adjacent Elements](01-one-dimensional-dp/05-maximum-sum-of-non-adjacent-elements/README.md) | 🧩 | pick / not-pick |
| 6 | [House Robber](01-one-dimensional-dp/06-house-robber/README.md) | 🧩 | LC 198 (Medium) |
| 7 | [House Robber II](01-one-dimensional-dp/07-house-robber-ii/README.md) | 🧩 | LC 213 (Medium) |
| 8 | [Decode Ways](01-one-dimensional-dp/08-decode-ways/README.md) | 🧩 | LC 91 (Medium) |
| 9 | [Maximum Subarray: Kadane vs DP](01-one-dimensional-dp/09-maximum-subarray-kadane-vs-dp/README.md) | 🧩 | LC 53 (Medium) |
| — | [Practice: 1D](06-leetcode-practice/01-one-dimensional/README.md) | 🏋️ | 8 problems |

🔁 **Checkpoint 2:** solve LC 740 Delete and Earn without hints (it reduces to House Robber).

---

## Phase 3 — Grid, 2D and 3D DP (Weeks 5–6)

| # | Topic | Type | Judge |
|---|---|---|---|
| 1 | [2D DP state representation](02-two-dimensional-and-grid-dp/01-2d-dp-state-representation/README.md) | 🧠 | — |
| 2 | [Unique Paths](02-two-dimensional-and-grid-dp/02-unique-paths/README.md) | 🧩 | LC 62 (Medium) |
| 3 | [Unique Paths II](02-two-dimensional-and-grid-dp/03-unique-paths-ii/README.md) | 🧩 | LC 63 (Medium) |
| 4 | [Minimum Path Sum](02-two-dimensional-and-grid-dp/04-minimum-path-sum/README.md) | 🧩 | LC 64 (Medium) |
| 5 | [Triangle](02-two-dimensional-and-grid-dp/05-triangle/README.md) | 🧩 | LC 120 (Medium) |
| 6 | [Maximum Falling Path Sum](02-two-dimensional-and-grid-dp/06-maximum-falling-path-sum/README.md) | 🧩 | LC 931 (Medium, min version) |
| 7 | [Dungeon Game](02-two-dimensional-and-grid-dp/07-dungeon-game/README.md) | 🧩 | LC 174 (Hard) |
| 8 | [Introduction to 3D DP](02-two-dimensional-and-grid-dp/08-introduction-to-3d-dp/README.md) | 🧠🧩 | LC 1463 (Hard) |
| 9 | [Cherry Pickup](02-two-dimensional-and-grid-dp/09-cherry-pickup/README.md) | 🧩 | LC 741 (Hard) |
| — | [Practice: Grid](06-leetcode-practice/02-grid/README.md) | 🏋️ | 5 problems |

🔁 **Checkpoint 3:** explain why Dungeon Game must be solved from the bottom-right corner.

---

## Phase 4 — Subsequences and Knapsack (Weeks 7–10)

*This is the largest and most interview-relevant phase. Take your time.*

| # | Topic | Type | Judge |
|---|---|---|---|
| 1 | [Subsequences fundamentals](03-subsequence-and-knapsack-dp/01-subsequences-fundamentals/README.md) | 🧠 | — |
| 2 | [0/1 Knapsack](03-subsequence-and-knapsack-dp/02-zero-one-knapsack/README.md) | 🧩 | AtCoder EDPC D |
| 3 | [Subset Sum](03-subsequence-and-knapsack-dp/03-subset-sum/README.md) | 🧩 | CSES 1745 |
| 4 | [Equal Sum Partition](03-subsequence-and-knapsack-dp/04-equal-sum-partition/README.md) | 🧩 | LC 416 (Medium) |
| 5 | [Partition with Minimum Difference](03-subsequence-and-knapsack-dp/05-partition-with-minimum-difference/README.md) | 🧩 | LC 1049 (Medium) |
| 6 | [Target Sum](03-subsequence-and-knapsack-dp/06-target-sum/README.md) | 🧩 | LC 494 (Medium) |
| 7 | [Unbounded Knapsack](03-subsequence-and-knapsack-dp/07-unbounded-knapsack/README.md) | 🧩 | — |
| 8 | [Coin Change](03-subsequence-and-knapsack-dp/08-coin-change/README.md) | 🧩 | LC 322 (Medium) |
| 9 | [Coin Change II](03-subsequence-and-knapsack-dp/09-coin-change-ii/README.md) | 🧩 | LC 518 (Medium) |
| 10 | [Rod Cutting](03-subsequence-and-knapsack-dp/10-rod-cutting/README.md) | 🧩 | — |
| 11 | [Longest Increasing Subsequence](03-subsequence-and-knapsack-dp/11-longest-increasing-subsequence/README.md) | 🧩 | LC 300 (Medium) |
| 12 | [Longest Common Subsequence](03-subsequence-and-knapsack-dp/12-longest-common-subsequence/README.md) | 🧩 | LC 1143 (Medium) |
| 13 | [Longest Palindromic Subsequence](03-subsequence-and-knapsack-dp/13-longest-palindromic-subsequence/README.md) | 🧩 | LC 516 (Medium) |
| 14 | [Edit Distance](03-subsequence-and-knapsack-dp/14-edit-distance/README.md) | 🧩 | LC 72 (Medium) |
| 15 | [Distinct Subsequences](03-subsequence-and-knapsack-dp/15-distinct-subsequences/README.md) | 🧩 | LC 115 (Hard) |
| 16 | [Shortest Common Supersequence](03-subsequence-and-knapsack-dp/16-shortest-common-supersequence/README.md) | 🧩 | LC 1092 (Hard) |
| — | [Practice: Knapsack](06-leetcode-practice/03-knapsack/README.md) · [Practice: Subsequences & strings](06-leetcode-practice/04-subsequences-and-strings/README.md) | 🏋️ | 16 problems |

🔁 **Checkpoint 4 (big one):** re-solve LC 416, 494, 322, 518, 300, 1143 and 72 from a blank file in one sitting. Then do the first half of the drill in [DP_PATTERN_RECOGNITION.md](07-revision-and-interview-preparation/DP_PATTERN_RECOGNITION.md).

---

## Phase 5 — Advanced DP Patterns (Weeks 11–14)

| # | Topic | Type | Judge |
|---|---|---|---|
| 1 | [DP on Strings (matching)](04-advanced-dp-patterns/01-dp-on-strings/README.md) | 🧩 | LC 44, LC 10 (Hard) |
| 2 | [Stock Buy & Sell family](04-advanced-dp-patterns/02-stock-buy-and-sell/README.md) | 🧩 | LC 121, 122, 123, 188, 309, 714 |
| 3 | [Interval DP](04-advanced-dp-patterns/03-interval-dp/README.md) | 🧠🧩 | LC 1039 / 1547 |
| 4 | [Matrix Chain Multiplication](04-advanced-dp-patterns/04-matrix-chain-multiplication/README.md) | 🧩 | classic |
| 5 | [Burst Balloons](04-advanced-dp-patterns/05-burst-balloons/README.md) | 🧩 | LC 312 (Hard) |
| 6 | [Palindrome Partitioning](04-advanced-dp-patterns/06-palindrome-partitioning/README.md) | 🧩 | LC 131, LC 132 |
| 7 | [DP on Trees](04-advanced-dp-patterns/07-dp-on-trees/README.md) | 🧠🧩 | LC 337, 543, 124, 834 |
| 8 | [DP on Graphs](04-advanced-dp-patterns/08-dp-on-graphs/README.md) | 🧠🧩 | EDPC G, LC 329, LC 787 |
| 9 | [Bitmask DP](04-advanced-dp-patterns/09-bitmask-dp/README.md) | 🧠🧩 | LC 1879, LC 698 |
| 10 | [Traveling Salesperson](04-advanced-dp-patterns/10-traveling-salesperson-problem/README.md) | 🧩 | LC 847, LC 943 |
| 11 | [Digit DP](04-advanced-dp-patterns/11-digit-dp/README.md) | 🧠🧩 | LC 233, LC 2376 |
| 12 | [State Machine DP](04-advanced-dp-patterns/12-state-machine-dp/README.md) | 🧠🧩 | EDPC C, LC 1220, 790, 552 |
| — | Practice: [Stocks & state machines](06-leetcode-practice/05-stocks-and-state-machines/README.md) · [Interval](06-leetcode-practice/06-interval/README.md) · [Trees & graphs](06-leetcode-practice/07-trees-and-graphs/README.md) · [Bitmask](06-leetcode-practice/08-bitmask/README.md) · [Digit](06-leetcode-practice/09-digit/README.md) | 🏋️ | ~40 problems |

🔁 **Checkpoint 5:** finish the full pattern-recognition drill; solve 3 unseen Hard problems from the tracker.

---

## Phase 6 — DP Optimization (Weeks 15–16, optional for interviews)

Recommended for competitive programming and for the hardest LeetCode problems.

| # | Topic | Prerequisite math/DS |
|---|---|---|
| 1 | [Space optimization](05-dp-optimization/01-space-optimization/README.md) | dependency footprint |
| 2 | [Rolling arrays](05-dp-optimization/02-rolling-arrays/README.md) | modular indexing |
| 3 | [Prefix & suffix optimization](05-dp-optimization/03-prefix-and-suffix-optimization/README.md) | prefix sums |
| 4 | [Monotonic queue optimization](05-dp-optimization/04-monotonic-queue-optimization/README.md) | deque, sliding window max |
| 5 | [Divide & conquer optimization](05-dp-optimization/05-divide-and-conquer-optimization/README.md) | monotone opt, recursion |
| 6 | [Convex hull trick](05-dp-optimization/06-convex-hull-trick/README.md) | lines, lower envelopes |
| 7 | [SOS DP](05-dp-optimization/07-sos-dp/README.md) | bitmasks |
| 8 | [Knuth optimization](05-dp-optimization/08-knuth-optimization/README.md) | interval DP, quadrangle inequality |
| 9 | [Matrix exponentiation](05-dp-optimization/09-matrix-exponentiation/README.md) | matrix multiplication |
| — | [Practice: Optimization](06-leetcode-practice/10-optimization/README.md) | 🏋️ |

---

## Continuous — Revision & Interview Preparation

Use from Phase 2 onward:

- [DP_PATTERN_RECOGNITION.md](07-revision-and-interview-preparation/DP_PATTERN_RECOGNITION.md): identify the pattern from the statement.
- [RECURRENCE_CHEATSHEET.md](07-revision-and-interview-preparation/RECURRENCE_CHEATSHEET.md): every recurrence on one page per pattern.
- [COMMON_MISTAKES.md](07-revision-and-interview-preparation/COMMON_MISTAKES.md): read before each checkpoint.
- [INTERVIEW_STRATEGY.md](07-revision-and-interview-preparation/INTERVIEW_STRATEGY.md): how to talk through DP in 45 minutes.
- [REVISION_SCHEDULE.md](07-revision-and-interview-preparation/REVISION_SCHEDULE.md): spaced repetition.

## If you are short on time (interview track, ~8 weeks)

Phase 1 (all) → Phase 2 (all) → Phase 3 (topics 1–5, 7) → Phase 4 (all) → Phase 5 (topics 2, 3, 5, 6, 7, 12) → pattern-recognition drill → interview strategy.
