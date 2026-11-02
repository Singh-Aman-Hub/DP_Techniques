# Revision Schedule

> **Module:** 07 · Revision and Interview Preparation · **Use:** from day one of the course; open it at the start of every study week

Solving a problem once does not mean you can solve it in an interview six weeks later. This file gives you:

1. a **spaced-repetition** routine so solved problems stay solved;
2. a **16-week plan** that maps weeks to course modules;
3. a list of **must-revisit problems**;
4. a **self-assessment rubric** for every pattern.

---

## 1. Spaced repetition: the 1 / 3 / 7 / 21 / 60 rule

Memory fades quickly at first and more slowly after each successful recall. Reviewing just as you are about to forget is the cheapest way to make knowledge permanent.

For every problem you solve (or topic you finish), schedule reviews at these offsets from the **first solve**:

| Review | When | What to do (time box) | Pass criterion |
|---|---|---|---|
| R0 | Day 0 | Solve it, then write a 5-line note: pattern, state sentence, recurrence, base case, the trap. | — |
| R1 | Day 1 | From the note only, rewrite the recurrence and base case. (10 min) | Recurrence and base correct without looking |
| R2 | Day 3 | Re-solve from a blank editor. (≤ 25 min Medium) | Accepted, ≤ 1 bug |
| R3 | Day 7 | Re-solve, then do the space-optimized version. (≤ 25 min) | Accepted first try |
| R4 | Day 21 | Explain aloud in 3 minutes (state, recurrence, order, complexity), then code. | Clear explanation + accepted |
| R5 | Day 60 | Solve as a mock interview (Section 6 of [INTERVIEW_STRATEGY.md](INTERVIEW_STRATEGY.md)). | Done within 30 min |

**If a review fails**, restart that problem's schedule from R1 (day 1 after the failure). Failing is useful — it shows exactly what to fix.

### How to track it

Add these columns to your copy of [PROGRESS_TRACKER.md](../PROGRESS_TRACKER.md) or a spreadsheet:

```
| Problem | Pattern | First solve | R1 (+1) | R2 (+3) | R3 (+7) | R4 (+21) | R5 (+60) | Notes / trap |
|---------|---------|-------------|---------|---------|---------|----------|----------|--------------|
| 322 Coin Change | Unbounded knapsack | 2026-03-02 | ✓ 03-03 | ✓ 03-05 | ✗ 03-09 → restart | ... | ... | INF + 1 overflow |
```

**Daily budget:** about 30 % of study time for reviews, 70 % for new material. If reviews pile up beyond 45 minutes a day, slow down new material for a few days.

---

## 2. Weekly study plan (16 weeks)

Assumes about **8–10 hours per week** (e.g. 1 hour on weekdays, 2–3 hours on one weekend day). With more time, compress; with less, stretch to 18 weeks by giving Modules 03 and 04 an extra week each.

Every week also includes: spaced reviews (Section 1) and **one** problem from a *previous* module chosen at random, so old patterns keep resurfacing.

| Week | Module / topics | Main goal | Practice focus |
|---|---|---|---|
| 1 | [00-foundations](../00-foundations/) 01–05 | What DP is; recursion trees; spot overlap and optimal substructure | Draw recursion trees for Fibonacci, stairs, grid paths |
| 2 | [00-foundations](../00-foundations/) 06–11 | Memoization, tabulation, state, transitions, base cases, computation order | Convert the same problem three ways |
| 3 | [00-foundations](../00-foundations/) 12–16 | Complexity, recursion → memo → table conversions, space optimization, the framework | Apply the 7 questions to 5 easy problems |
| 4 | [01-one-dimensional-dp](../01-one-dimensional-dp/) 01–05 | Linear DP with fixed look-back | Fibonacci, Climbing Stairs, Min Cost Climbing Stairs, Frog Jump |
| 5 | [01-one-dimensional-dp](../01-one-dimensional-dp/) 06–09 | Take/skip, circular arrays, string decoding, Kadane | House Robber I/II, Decode Ways, Maximum Subarray |
| 6 | [02-two-dimensional-and-grid-dp](../02-two-dimensional-and-grid-dp/) 01–05 | 2D state design, grid paths | Unique Paths I/II, Minimum Path Sum, Triangle |
| 7 | [02-two-dimensional-and-grid-dp](../02-two-dimensional-and-grid-dp/) 06–09 | Reverse-direction DP, 3D DP, two agents | Falling Path, Dungeon Game, Cherry Pickup I/II |
| 8 | [03-subsequence-and-knapsack-dp](../03-subsequence-and-knapsack-dp/) 01–06 | 0/1 knapsack and its subset-sum variants | Subset Sum, Partition, Target Sum, Last Stone Weight II |
| 9 | [03-subsequence-and-knapsack-dp](../03-subsequence-and-knapsack-dp/) 07–11 | Unbounded knapsack, combinations vs. permutations, LIS | Coin Change I/II, Combination Sum IV, Rod Cutting, LIS |
| 10 | [03-subsequence-and-knapsack-dp](../03-subsequence-and-knapsack-dp/) 12–16 | Two-string DP | LCS, LPS, Edit Distance, Distinct Subsequences, SCS |
| 11 | [04-advanced-dp-patterns](../04-advanced-dp-patterns/) 01–03 + 12 | String DP, stocks, state machines, interval DP intro | All six stock problems; Wildcard Matching |
| 12 | [04-advanced-dp-patterns](../04-advanced-dp-patterns/) 04–06 | Interval DP | MCM, Burst Balloons, Palindrome Partitioning II, Min Cost to Cut a Stick |
| 13 | [04-advanced-dp-patterns](../04-advanced-dp-patterns/) 07–11 | Trees, graphs, bitmask, TSP, digit DP | House Robber III, Max Path Sum, TSP, a digit DP problem |
| 14 | [05-dp-optimization](../05-dp-optimization/) 01–05 | Rolling arrays, prefix max, monotonic queue, D&C | Jump Game VI, Max Points with Cost, Min Difficulty of a Job Schedule |
| 15 | [05-dp-optimization](../05-dp-optimization/) 06–09 + mixed review | CHT, SOS, Knuth, matrix power; mixed sets from [06-leetcode-practice](../06-leetcode-practice/) | 3 unseen Mediums + 1 Hard, timed |
| 16 | [07-revision-and-interview-preparation](README.md) | Pattern drills, cheat sheet, mock interviews | All 18 [recognition drills](DP_PATTERN_RECOGNITION.md#12-drill-identify-the-pattern-18-questions); 3 mocks |

**Optional weeks 17–18** (recommended before interviews): one Hard per day from [06-leetcode-practice](../06-leetcode-practice/), plus R4/R5 reviews of the must-revisit list.

### A typical week

| Day | Activity |
|---|---|
| Mon | Read README + walkthrough of topic A; run its Java solutions; R-reviews due today |
| Tue | Re-derive topic A from the 7 questions without looking; solve its first practice problem |
| Wed | Topic B (same as Mon/Tue, compressed) |
| Thu | Practice problems for A and B; log mistakes |
| Fri | Topic C or catch-up; R-reviews |
| Sat | Timed set: 2 Mediums from this week + 1 random older problem; read [COMMON_MISTAKES.md](COMMON_MISTAKES.md) for any bug you hit |
| Sun | Rest, or a light review of [RECURRENCE_CHEATSHEET.md](RECURRENCE_CHEATSHEET.md) for this week's patterns |

---

## 3. Must-revisit problems

These problems each anchor a pattern. If you can re-solve all of them from a blank page, you can handle most interview DP. Put each one on the 1/3/7/21/60 schedule.

| # | Problem | Difficulty | Pattern it anchors | The thing to remember |
|---|---|---|---|---|
| 1 | [Climbing Stairs](https://leetcode.com/problems/climbing-stairs/) | Easy | Linear counting | `dp[0] = 1` (empty path counts) |
| 2 | [House Robber](https://leetcode.com/problems/house-robber/) | Medium | Take / skip | `max(skip, take + dp[i−2])` |
| 3 | [House Robber II](https://leetcode.com/problems/house-robber-ii/) | Medium | Circular reduction | Two linear runs |
| 4 | [Decode Ways](https://leetcode.com/problems/decode-ways/) | Medium | Linear with validity checks | Zeros |
| 5 | [Maximum Product Subarray](https://leetcode.com/problems/maximum-product-subarray/) | Medium | Kadane with min/max | State too small without min |
| 6 | [Word Break](https://leetcode.com/problems/word-break/) | Medium | Linear, full look-back | `ok[0] = true` |
| 7 | [Unique Paths II](https://leetcode.com/problems/unique-paths-ii/) | Medium | Grid counting | Obstacles in first row/column |
| 8 | [Dungeon Game](https://leetcode.com/problems/dungeon-game/) | Hard | Reverse grid DP | Compute from the end; `max(1, …)` |
| 9 | [Cherry Pickup II](https://leetcode.com/problems/cherry-pickup-ii/) | Hard | 3D, two agents | Shared cell counted once |
| 10 | [Partition Equal Subset Sum](https://leetcode.com/problems/partition-equal-subset-sum/) | Medium | 0/1 knapsack (boolean) | Reverse loop |
| 11 | [Target Sum](https://leetcode.com/problems/target-sum/) | Medium | Knapsack counting via transformation | `(S + target) / 2`, parity check |
| 12 | [Coin Change](https://leetcode.com/problems/coin-change/) | Medium | Unbounded min | Safe infinity |
| 13 | [Coin Change II](https://leetcode.com/problems/coin-change-ii/) | Medium | Combinations | Coins outer loop |
| 14 | [Combination Sum IV](https://leetcode.com/problems/combination-sum-iv/) | Medium | Permutations | Amount outer loop |
| 15 | [Longest Increasing Subsequence](https://leetcode.com/problems/longest-increasing-subsequence/) | Medium | LIS (n² and n log n) | Answer = max over all i |
| 16 | [Longest Common Subsequence](https://leetcode.com/problems/longest-common-subsequence/) | Medium | Two-string DP | `charAt(i − 1)` |
| 17 | [Edit Distance](https://leetcode.com/problems/edit-distance/) | Medium | Two-string with 3 operations | `dp[i][0] = i` |
| 18 | [Longest Palindromic Subsequence](https://leetcode.com/problems/longest-palindromic-subsequence/) | Medium | Palindromic range | i descending |
| 19 | [Best Time to Buy and Sell Stock with Cooldown](https://leetcode.com/problems/best-time-to-buy-and-sell-stock-with-cooldown/) | Medium | State machine | Snapshot previous values |
| 20 | [Best Time to Buy and Sell Stock IV](https://leetcode.com/problems/best-time-to-buy-and-sell-stock-iv/) | Hard | State machine + resource k | `buy[t]` uses `sell[t−1]` |
| 21 | [Burst Balloons](https://leetcode.com/problems/burst-balloons/) | Hard | Interval DP | Choose the *last* balloon |
| 22 | [Palindrome Partitioning II](https://leetcode.com/problems/palindrome-partitioning-ii/) | Hard | Partition DP | Precompute palindrome table |
| 23 | [House Robber III](https://leetcode.com/problems/house-robber-iii/) | Medium | Tree DP | Return a pair |
| 24 | [Binary Tree Maximum Path Sum](https://leetcode.com/problems/binary-tree-maximum-path-sum/) | Hard | Tree DP with global answer | Return one branch, record both |
| 25 | [Parallel Courses III](https://leetcode.com/problems/parallel-courses-iii/) | Hard | DAG DP | Topological order |
| 26 | [Partition to K Equal Sum Subsets](https://leetcode.com/problems/partition-to-k-equal-sum-subsets/) | Medium | Bitmask DP | `dp[mask]` = fill of open bucket |
| 27 | [Find the Shortest Superstring](https://leetcode.com/problems/find-the-shortest-superstring/) | Hard | TSP-style bitmask | `dp[mask][last]` + reconstruction |
| 28 | [Numbers At Most N Given Digit Set](https://leetcode.com/problems/numbers-at-most-n-given-digit-set/) | Hard | Digit DP | `tight` flag |
| 29 | [Jump Game VI](https://leetcode.com/problems/jump-game-vi/) | Medium | Monotonic queue optimization | Deque of indices |
| 30 | [CSES Book Shop](https://cses.fi/problemset/task/1158) | — | 0/1 knapsack at full scale | 1D array; descending loop for speed |

---

## 4. Self-assessment rubric

Rate yourself honestly for each pattern at the end of weeks 8, 13 and 16. Aim for **level 3 in every core pattern** and **level 2 in every advanced pattern** before interviews.

| Level | Meaning |
|---|---|
| **0 — Unfamiliar** | I can't say what the pattern is. |
| **1 — Recognizes** | I recognize it when told the pattern name, and can follow a written solution. |
| **2 — Reproduces** | I can re-solve the anchor problem from a blank page, maybe with one bug. |
| **3 — Transfers** | I can solve an *unseen* Medium of this pattern in ≤ 30 min and explain state, recurrence, order and complexity aloud. |
| **4 — Masters** | I can solve an unseen Hard, optimize space/time, and explain why simpler approaches (greedy, smaller state) fail. |

### Per-pattern checklist

Copy this table and fill in a level each time you assess.

| Pattern | Level 2 test (reproduce) | Level 3 test (transfer) | Level 4 test (master) | Wk 8 | Wk 13 | Wk 16 |
|---|---|---|---|---|---|---|
| Recognizing DP & state design | Explain overlap + optimal substructure with a recursion tree | Score ≥ 14/18 on the [recognition drills](DP_PATTERN_RECOGNITION.md#12-drill-identify-the-pattern-18-questions) | Explain the longest-simple-path counterexample and how a bigger state fixes max-product | | | |
| Linear 1D | House Robber | Delete and Earn | Decode Ways II | | | |
| Kadane-style | Maximum Subarray | Maximum Product Subarray | Maximum Sum Circular Subarray | | | |
| Grid | Unique Paths II | Minimum Falling Path Sum | Dungeon Game | | | |
| Multi-agent / 3D | — | Cherry Pickup II | Cherry Pickup | | | |
| 0/1 knapsack | Partition Equal Subset Sum | Target Sum | Profitable Schemes | | | |
| Unbounded knapsack | Coin Change | Coin Change II vs Combination Sum IV (explain the loop order) | Form Largest Integer With Digits That Add up to Target | | | |
| LIS | LIS O(n²) | Largest Divisible Subset | Russian Doll Envelopes (n log n) | | | |
| Two-string | LCS | Edit Distance | Distinct Subsequences / Regular Expression Matching | | | |
| Palindromes | Longest Palindromic Subsequence | Palindromic Substrings | Palindrome Partitioning II | | | |
| State machine | Stock II | Stock with Cooldown | Stock IV; Student Attendance Record II | | | |
| Interval | MCM | Minimum Score Triangulation of Polygon | Burst Balloons; Strange Printer | | | |
| Game | — | Predict the Winner | Stone Game / Can I Win | | | |
| Tree | Diameter of Binary Tree | House Robber III | Binary Tree Cameras; Sum of Distances in Tree | | | |
| DAG / graph | AtCoder Longest Path | Cheapest Flights Within K Stops | Largest Color Value in a Directed Graph | | | |
| Bitmask | — | Partition to K Equal Sum Subsets | Find the Shortest Superstring | | | |
| Digit | — | Count Numbers with Unique Digits | Number of Digit One | | | |
| Optimization techniques | Rolling array on LCS | Jump Game VI | Minimum Difficulty of a Job Schedule (D&C) or a CHT problem | | | |

**Turning the rubric into action:** any pattern below target at a checkpoint gets two extra problems next week from the matching [06-leetcode-practice](../06-leetcode-practice/) folder, and its anchor problem goes back to R1 on the spaced-repetition schedule.

---

**See also:** [INTERVIEW_STRATEGY.md](INTERVIEW_STRATEGY.md) · [DP_PATTERN_RECOGNITION.md](DP_PATTERN_RECOGNITION.md) · [module README](README.md)
