# Recurrence Cheat Sheet

> **Module:** 07 · Revision and Interview Preparation · **Use:** quick revision, the night before an interview, or when you know the pattern but forgot a detail

Every major recurrence in the course on one page per pattern. For each entry:

- **State** — what one cell means, as an English sentence.
- **Recurrence** — how a cell is computed from others.
- **Base** — cells known without computation.
- **Order** — loop direction that guarantees dependencies are ready.
- **Complexity** — time / space, with the space-optimized version where one exists.

This is a *revision* tool. If an entry does not make sense, go back to the linked topic folder — the walkthrough there derives it step by step. To decide *which* entry applies to a new problem, use [DP_PATTERN_RECOGNITION.md](DP_PATTERN_RECOGNITION.md).

Notation: `n` = array/string length, `m` = second length, `W` = capacity/target, `k` = extra resource, `a[]` = input array (0-indexed). "Prefix DP" means `dp[i]` refers to the first `i` elements, so `dp` has size `n+1` and element `i` is `a[i−1]`.

---

## Contents

1. [One-dimensional (linear) DP](#1-one-dimensional-linear-dp)
2. [Grid and multi-dimensional DP](#2-grid-and-multi-dimensional-dp)
3. [Knapsack family](#3-knapsack-family)
4. [Subsequence and two-string DP](#4-subsequence-and-two-string-dp)
5. [String, partition and game DP](#5-string-partition-and-game-dp)
6. [Interval DP](#6-interval-dp)
7. [State-machine DP (stocks and friends)](#7-state-machine-dp-stocks-and-friends)
8. [Tree DP](#8-tree-dp)
9. [Graph / DAG DP](#9-graph--dag-dp)
10. [Bitmask DP](#10-bitmask-dp)
11. [Digit DP](#11-digit-dp)
12. [Optimization techniques](#12-optimization-techniques)
13. [Base-case and order quick reference](#13-base-case-and-order-quick-reference)

---

## 1. One-dimensional (linear) DP

Folder: [01-one-dimensional-dp](../01-one-dimensional-dp/)

| Problem | State | Recurrence | Base | Order | Time / Space |
|---|---|---|---|---|---|
| [Fibonacci](../01-one-dimensional-dp/01-fibonacci-number/) | `f[i]` = i-th Fibonacci number | `f[i] = f[i−1] + f[i−2]` | `f[0]=0, f[1]=1` | i ↑ | O(n) / O(1) with two variables |
| [Climbing Stairs](../01-one-dimensional-dp/02-climbing-stairs/) | `ways[i]` = distinct ways to reach step i | `ways[i] = ways[i−1] + ways[i−2]` | `ways[0]=1, ways[1]=1` | i ↑ | O(n) / O(1) |
| [Min Cost Climbing Stairs](../01-one-dimensional-dp/03-minimum-cost-climbing-stairs/) | `dp[i]` = min cost to stand on step i (top = n) | `dp[i] = min(dp[i−1]+cost[i−1], dp[i−2]+cost[i−2])` | `dp[0]=dp[1]=0` | i ↑ | O(n) / O(1) |
| [Frog Jump](../01-one-dimensional-dp/04-frog-jump/) | `dp[i]` = min energy to reach stone i | `dp[i] = min(dp[i−1]+\|h[i]−h[i−1]\|, dp[i−2]+\|h[i]−h[i−2]\|)`; k-jump version: min over `j = i−k..i−1` | `dp[0]=0` | i ↑ | O(n) (O(nk) for k jumps) / O(1) (O(k)) |
| [Max sum of non-adjacent](../01-one-dimensional-dp/05-maximum-sum-of-non-adjacent-elements/) / [House Robber](../01-one-dimensional-dp/06-house-robber/) | `dp[i]` = max loot from the first i houses | `dp[i] = max(dp[i−1], dp[i−2] + a[i−1])` | `dp[0]=0, dp[1]=a[0]` | i ↑ | O(n) / O(1) |
| [House Robber II](../01-one-dimensional-dp/07-house-robber-ii/) (circular) | as above on a sub-range | `max(rob(a[0..n−2]), rob(a[1..n−1]))` | n = 1 → `a[0]` | — | O(n) / O(1) |
| [Decode Ways](../01-one-dimensional-dp/08-decode-ways/) | `dp[i]` = ways to decode the first i chars | `dp[i] = (s[i−1]≠'0' ? dp[i−1] : 0) + (10 ≤ s[i−2..i−1] ≤ 26 ? dp[i−2] : 0)` | `dp[0]=1` (empty string decodes one way) | i ↑ | O(n) / O(1) |
| [Kadane](../01-one-dimensional-dp/09-maximum-subarray-kadane-vs-dp/) | `end[i]` = best sum of a subarray **ending at** i | `end[i] = max(a[i], end[i−1] + a[i])`; answer = `max_i end[i]` | `end[0]=a[0]` | i ↑ | O(n) / O(1) |
| Max product subarray | `hi[i]`, `lo[i]` = max / min product ending at i | `hi[i] = max(a[i], hi[i−1]·a[i], lo[i−1]·a[i])`, `lo[i]` = same with min | `hi[0]=lo[0]=a[0]` | i ↑ | O(n) / O(1) |
| Word Break | `ok[i]` = first i chars can be segmented | `ok[i] = OR_{j<i} (ok[j] && s[j..i) ∈ dict)` | `ok[0]=true` | i ↑ | O(n² · L) / O(n) |
| Perfect Squares | `dp[x]` = fewest squares summing to x | `dp[x] = 1 + min_{s²≤x} dp[x − s²]` | `dp[0]=0` | x ↑ | O(n√n) / O(n) |
| Min Cost For Tickets | `dp[d]` = min cost to cover travel days ≥ d | travel day: `min(c1+dp[d+1], c7+dp[d+7], c30+dp[d+30])`; else `dp[d+1]` | `dp[d>365]=0` | d ↓ | O(365) / O(365) |

Related practice: [06-leetcode-practice/01-one-dimensional](../06-leetcode-practice/01-one-dimensional/).

---

## 2. Grid and multi-dimensional DP

Folder: [02-two-dimensional-and-grid-dp](../02-two-dimensional-and-grid-dp/) · state design: [2D state representation](../02-two-dimensional-and-grid-dp/01-2d-dp-state-representation/)

| Problem | State | Recurrence | Base | Order | Time / Space |
|---|---|---|---|---|---|
| [Unique Paths](../02-two-dimensional-and-grid-dp/02-unique-paths/) | `dp[r][c]` = paths from (0,0) to (r,c) | `dp[r][c] = dp[r−1][c] + dp[r][c−1]` | first row and first column = 1 | r ↑, c ↑ | O(RC) / O(C) |
| [Unique Paths II](../02-two-dimensional-and-grid-dp/03-unique-paths-ii/) | same, with obstacles | obstacle → 0, else as above | `dp[0][0] = (start free ? 1 : 0)`; first row/column copy from the left/above (stop after an obstacle) | r ↑, c ↑ | O(RC) / O(C) |
| [Minimum Path Sum](../02-two-dimensional-and-grid-dp/04-minimum-path-sum/) | `dp[r][c]` = min sum from (0,0) to (r,c) | `g[r][c] + min(dp[r−1][c], dp[r][c−1])` | `dp[0][0]=g[0][0]`; first row/col = prefix sums | r ↑, c ↑ | O(RC) / O(C) |
| [Triangle](../02-two-dimensional-and-grid-dp/05-triangle/) | `dp[r][c]` = min path sum from (r,c) to the bottom | `t[r][c] + min(dp[r+1][c], dp[r+1][c+1])` | last row = itself | r ↓ | O(n²) / O(n) |
| [Max/Min Falling Path Sum](../02-two-dimensional-and-grid-dp/06-maximum-falling-path-sum/) | `dp[r][c]` = best falling path ending at (r,c) | `m[r][c] + best(dp[r−1][c−1], dp[r−1][c], dp[r−1][c+1])` (skip out-of-range) | row 0 = itself | r ↑ | O(n²) / O(n) |
| [Dungeon Game](../02-two-dimensional-and-grid-dp/07-dungeon-game/) | `need[r][c]` = min health needed **on entering** (r,c) to survive to the end | `need[r][c] = max(1, min(need[r+1][c], need[r][c+1]) − d[r][c])` | `need[R][C−1] = need[R−1][C] = 1`, other out-of-grid = ∞ | r ↓, c ↓ (backwards: future determines need) | O(RC) / O(C) |
| [3D DP intro](../02-two-dimensional-and-grid-dp/08-introduction-to-3d-dp/) — Cherry Pickup II | `dp[r][c1][c2]` = max cherries from row r down, robots at c1, c2 | `cell(r,c1) + (c1≠c2 ? cell(r,c2) : 0) + max_{d1,d2∈{−1,0,1}} dp[r+1][c1+d1][c2+d2]` | row R−1: cells only | r ↓ | O(R·C²·9) / O(C²) |
| [Cherry Pickup](../02-two-dimensional-and-grid-dp/09-cherry-pickup/) | `dp[r1][c1][r2]` = max cherries for two walkers who each took t = r1+c1 steps (`c2 = r1+c1−r2`) | cherries at both cells (once if same) + max of 4 predecessor combos (each walker came from up or left); thorn → −∞ | `dp[0][0][0] = g[0][0]` | by step t ↑ | O(n³) / O(n²) by step |
| Maximal Square | `dp[r][c]` = side of largest all-1 square with bottom-right corner (r,c) | `1 + min(up, left, upLeft)` if cell is 1, else 0 | out of grid = 0 | r ↑, c ↑ | O(RC) / O(C) |
| Out of Boundary Paths / Knight Probability | `dp[step][r][c]` = ways (probability) to be at (r,c) after `step` moves | sum over neighbours of `dp[step−1][nr][nc]` (× 1/8 for the knight) | step 0: start cell = 1 | step ↑ | O(steps·R·C·moves) / O(R·C) |

Related practice: [06-leetcode-practice/02-grid](../06-leetcode-practice/02-grid/).

---

## 3. Knapsack family

Folder: [03-subsequence-and-knapsack-dp](../03-subsequence-and-knapsack-dp/)

| Problem | State | Recurrence | Base | Order | Time / Space |
|---|---|---|---|---|---|
| [0/1 Knapsack](../03-subsequence-and-knapsack-dp/02-zero-one-knapsack/) | `dp[i][c]` = max value using the first i items with capacity c | `dp[i][c] = max(dp[i−1][c], v[i−1] + dp[i−1][c−w[i−1]])` (take only if `w ≤ c`) | `dp[0][c]=0` | i ↑; **1D: c ↓** | O(nW) / O(W) |
| [Subset Sum](../03-subsequence-and-knapsack-dp/03-subset-sum/) | `can[i][s]` = some subset of the first i items sums to s | `can[i][s] = can[i−1][s] \|\| can[i−1][s−a[i−1]]` | `can[0][0]=true`, `can[0][s>0]=false` | i ↑; 1D: s ↓ | O(nS) / O(S) |
| [Equal Sum Partition](../03-subsequence-and-knapsack-dp/04-equal-sum-partition/) | — | odd total → false; else Subset Sum with target total/2 | — | — | O(n·total) / O(total) |
| [Partition with Min Difference](../03-subsequence-and-knapsack-dp/05-partition-with-minimum-difference/) | `can[s]` for s ≤ total/2 | Subset Sum, then answer `min (total − 2s)` over reachable s ≤ total/2 | `can[0]=true` | s ↓ | O(n·total) / O(total) |
| [Target Sum](../03-subsequence-and-knapsack-dp/06-target-sum/) | `cnt[s]` = number of subsets summing to s | `P − N = target, P + N = S ⇒ P = (S+target)/2`; count subsets with sum P: `cnt[s] += cnt[s − a]` | `cnt[0]=1`; return 0 if `S+target` is odd or `\|target\| > S` | s ↓ | O(n·S) / O(S) |
| [Unbounded Knapsack](../03-subsequence-and-knapsack-dp/07-unbounded-knapsack/) | `dp[c]` = max value with capacity c, unlimited copies | `dp[c] = max(dp[c], v[i] + dp[c − w[i]])` | `dp[0..W]=0` | **c ↑** (reuse allowed) | O(nW) / O(W) |
| [Coin Change](../03-subsequence-and-knapsack-dp/08-coin-change/) (fewest coins) | `dp[a]` = fewest coins summing to a | `dp[a] = min_c (dp[a−c] + 1)` | `dp[0]=0`, others = `amount+1` ("infinity") | a ↑ | O(amount·coins) / O(amount) |
| [Coin Change II](../03-subsequence-and-knapsack-dp/09-coin-change-ii/) (combinations) | `dp[a]` = number of coin **sets** summing to a | for each coin (outer): for a = coin..amount: `dp[a] += dp[a−coin]` | `dp[0]=1` | coins outer, a ↑ | O(amount·coins) / O(amount) |
| Combination Sum IV (permutations) | `dp[a]` = number of ordered sequences summing to a | for a = 1..target (outer): for each num: `dp[a] += dp[a−num]` | `dp[0]=1` | a outer ↑ | O(target·n) / O(target) |
| [Rod Cutting](../03-subsequence-and-knapsack-dp/10-rod-cutting/) | `dp[L]` = max revenue from a rod of length L | `dp[L] = max_{1≤p≤L} (price[p] + dp[L−p])` | `dp[0]=0` | L ↑ | O(n²) / O(n) |
| Ones and Zeroes (2 budgets) | `dp[z][o]` = max strings with ≤ z zeros and ≤ o ones | `dp[z][o] = max(dp[z][o], 1 + dp[z−zeros][o−ones])` | all 0 | z ↓, o ↓ per item | O(len·m·n) / O(m·n) |
| Number of Dice Rolls | `dp[d][s]` = ways for d dice to sum to s | `dp[d][s] = Σ_{f=1..k} dp[d−1][s−f]` | `dp[0][0]=1` | d ↑ | O(d·target·k) / O(target) |

> **Memory hook.** 0/1 → **reverse** capacity loop. Unbounded → **forward**. Combinations → **items outside**. Permutations → **amount outside**.

Related practice: [06-leetcode-practice/03-knapsack](../06-leetcode-practice/03-knapsack/).

---

## 4. Subsequence and two-string DP

Folder: [03-subsequence-and-knapsack-dp](../03-subsequence-and-knapsack-dp/) · basics: [subsequences fundamentals](../03-subsequence-and-knapsack-dp/01-subsequences-fundamentals/)

| Problem | State | Recurrence | Base | Order | Time / Space |
|---|---|---|---|---|---|
| [LIS](../03-subsequence-and-knapsack-dp/11-longest-increasing-subsequence/) O(n²) | `dp[i]` = length of the longest increasing subsequence **ending at** i | `dp[i] = 1 + max_{j<i, a[j]<a[i]} dp[j]` (or 1) | each `dp[i] ≥ 1` | i ↑ | O(n²) / O(n); answer = `max dp[i]` |
| LIS O(n log n) | `tails[L]` = smallest possible tail of an increasing subsequence of length L+1 | binary-search the first `tails[L] ≥ x` and overwrite (append if none) | empty | i ↑ | O(n log n) / O(n) |
| [LCS](../03-subsequence-and-knapsack-dp/12-longest-common-subsequence/) | `dp[i][j]` = LCS of `a[0..i)` and `b[0..j)` | match: `dp[i−1][j−1]+1`; else `max(dp[i−1][j], dp[i][j−1])` | row 0, col 0 = 0 | i ↑, j ↑ | O(nm) / O(min(n,m)) |
| [Longest Palindromic Subsequence](../03-subsequence-and-knapsack-dp/13-longest-palindromic-subsequence/) | `dp[i][j]` = LPS of `s[i..j]` | `s[i]==s[j]` → `dp[i+1][j−1]+2`; else `max(dp[i+1][j], dp[i][j−1])` | `dp[i][i]=1`, `dp[i][i−1]=0` | **i ↓**, j ↑ (or LCS of s and reverse(s)) | O(n²) / O(n) |
| [Edit Distance](../03-subsequence-and-knapsack-dp/14-edit-distance/) | `dp[i][j]` = min ops to turn `a[0..i)` into `b[0..j)` | match: `dp[i−1][j−1]`; else `1 + min(dp[i−1][j] (delete), dp[i][j−1] (insert), dp[i−1][j−1] (replace))` | `dp[i][0]=i`, `dp[0][j]=j` | i ↑, j ↑ | O(nm) / O(m) |
| [Distinct Subsequences](../03-subsequence-and-knapsack-dp/15-distinct-subsequences/) | `dp[i][j]` = ways `t[0..j)` appears as a subsequence of `s[0..i)` | `dp[i−1][j] + (s[i−1]==t[j−1] ? dp[i−1][j−1] : 0)` | `dp[i][0]=1`, `dp[0][j>0]=0` | i ↑ (1D: j ↓) | O(nm) / O(m) |
| [Shortest Common Supersequence](../03-subsequence-and-knapsack-dp/16-shortest-common-supersequence/) | LCS table | length = `n + m − LCS`; string = walk back through the LCS table, emitting shared chars once | — | — | O(nm) / O(nm) (needs table to rebuild) |
| Delete Operation for Two Strings | — | `n + m − 2·LCS` | — | — | O(nm) |
| Min ASCII Delete Sum | `dp[i][j]` = min ASCII sum deleted to equalize prefixes | match: `dp[i−1][j−1]`; else `min(dp[i−1][j] + a[i−1], dp[i][j−1] + b[j−1])` | `dp[i][0]` = prefix ASCII sum of a; similarly row 0 | i ↑, j ↑ | O(nm) / O(m) |
| Interleaving String | `ok[i][j]` = `s3[0..i+j)` is an interleaving of `s1[0..i)` and `s2[0..j)` | `(ok[i−1][j] && s1[i−1]==s3[i+j−1]) \|\| (ok[i][j−1] && s2[j−1]==s3[i+j−1])` | `ok[0][0]=true` | i ↑, j ↑ | O(nm) / O(m) |
| Longest Common Substring / Repeated Subarray | `dp[i][j]` = length of common **suffix** of `a[0..i)` and `b[0..j)` | match: `dp[i−1][j−1]+1`; else **0** | 0 | i ↑, j ↑ | O(nm) / O(m); answer = max cell |

Related practice: [06-leetcode-practice/04-subsequences-and-strings](../06-leetcode-practice/04-subsequences-and-strings/).

---

## 5. String, partition and game DP

Folder: [04-advanced-dp-patterns/01-dp-on-strings](../04-advanced-dp-patterns/01-dp-on-strings/) · [06-palindrome-partitioning](../04-advanced-dp-patterns/06-palindrome-partitioning/)

| Problem | State | Recurrence | Base | Order | Time / Space |
|---|---|---|---|---|---|
| Palindromic substrings table | `pal[i][j]` = `s[i..j]` is a palindrome | `s[i]==s[j] && (j−i < 2 \|\| pal[i+1][j−1])` | length 1 and 2 handled by `j−i<2` | i ↓, j ↑ | O(n²) / O(n²) |
| Palindrome Partitioning II | `cuts[i]` = min cuts for `s[0..i]` | `cuts[i] = min over j ≤ i with pal[j][i] of (j==0 ? 0 : cuts[j−1] + 1)` | — | i ↑ | O(n²) / O(n²) |
| Partition Array for Max Sum | `dp[i]` = best sum for the first i elements | `dp[i] = max_{1≤len≤k} dp[i−len] + len·max(a[i−len..i−1])` | `dp[0]=0` | i ↑ | O(nk) / O(n) |
| Wildcard Matching | `m[i][j]` = `s[0..i)` matches `p[0..j)` | `p[j−1]=='*'` → `m[i][j−1] \|\| m[i−1][j]`; `'?'` or equal char → `m[i−1][j−1]` | `m[0][0]=true`; `m[0][j]` true while p is all `*` | i ↑, j ↑ | O(nm) / O(m) |
| Regular Expression Matching | same | `p[j−1]=='*'` → `m[i][j−2] \|\| (match(s[i−1], p[j−2]) && m[i−1][j])`; else `match && m[i−1][j−1]` | `m[0][0]=true`; `m[0][j] = p[j−1]=='*' && m[0][j−2]` | i ↑, j ↑ | O(nm) / O(m) |
| Game: Predict the Winner / Stone Game | `d[i][j]` = (current player's score − opponent's) on `a[i..j]` | `d[i][j] = max(a[i] − d[i+1][j], a[j] − d[i][j−1])` | `d[i][i]=a[i]` | length ↑ (or i ↓) | O(n²) / O(n) |

---

## 6. Interval DP

Folder: [03-interval-dp](../04-advanced-dp-patterns/03-interval-dp/) · [04-matrix-chain-multiplication](../04-advanced-dp-patterns/04-matrix-chain-multiplication/) · [05-burst-balloons](../04-advanced-dp-patterns/05-burst-balloons/)

**Always loop by interval length, shortest first.** Then every sub-interval is ready.

| Problem | State | Recurrence | Base | Time / Space |
|---|---|---|---|---|
| Matrix Chain Multiplication | `dp[i][j]` = min scalar multiplications for matrices i..j (matrix i is `d[i−1] × d[i]`) | `min_{i≤k<j} dp[i][k] + dp[k+1][j] + d[i−1]·d[k]·d[j]` | `dp[i][i]=0` | O(n³) / O(n²) |
| Slimes / merge adjacent piles (2 at a time) | `dp[i][j]` = min cost to merge `a[i..j]` into one | `min_k dp[i][k] + dp[k+1][j] + sum(i..j)` | `dp[i][i]=0` | O(n³) / O(n²) |
| Burst Balloons | pad `v` with 1 at both ends; `dp[l][r]` = max coins from bursting everything **strictly between** l and r | `max_{l<k<r} dp[l][k] + dp[k][r] + v[l]·v[k]·v[r]` (k is burst **last**) | `dp[l][l+1]=0` | O(n³) / O(n²) |
| Minimum Cost to Cut a Stick | add 0 and n to cuts, sort as `c`; `dp[i][j]` = min cost to make all cuts strictly between `c[i]` and `c[j]` | `(c[j] − c[i]) + min_{i<k<j} dp[i][k] + dp[k][j]` | `dp[i][i+1]=0` | O(m³) / O(m²) |
| Min Score Triangulation | `dp[i][j]` = min score to triangulate polygon vertices i..j | `min_{i<k<j} dp[i][k] + dp[k][j] + v[i]·v[k]·v[j]` | `dp[i][i+1]=0` | O(n³) / O(n²) |
| Unique BSTs (Catalan) | `G[n]` = number of BSTs with n nodes | `G[n] = Σ_{root=1..n} G[root−1]·G[n−root]` | `G[0]=G[1]=1` | O(n²) / O(n) |

Speed-up for some interval DPs: [Knuth optimization](../05-dp-optimization/08-knuth-optimization/) → O(n²).
Related practice: [06-leetcode-practice/06-interval](../06-leetcode-practice/06-interval/).

---

## 7. State-machine DP (stocks and friends)

Folders: [02-stock-buy-and-sell](../04-advanced-dp-patterns/02-stock-buy-and-sell/) · [12-state-machine-dp](../04-advanced-dp-patterns/12-state-machine-dp/)

Two variables per day: `hold` = best profit while holding a share, `free` = best profit while not holding one. Update from yesterday's values (use temporaries!).

| Problem | Recurrence (day i, price p) | Base | Answer | Time / Space |
|---|---|---|---|---|
| Stock I (one transaction) | `free = max(free, hold + p)`, `hold = max(hold, −p)` | `hold=−∞, free=0` | `free` | O(n) / O(1) |
| Stock II (unlimited) | `free = max(free, hold + p)`, `hold = max(hold, free_old − p)` | same | `free` | O(n) / O(1) |
| Stock III / IV (≤ k transactions) | for t = 1..k: `buy[t] = max(buy[t], sell[t−1] − p)`, `sell[t] = max(sell[t], buy[t] + p)` | `buy[t]=−∞`, `sell[t]=0` | `sell[k]` | O(nk) / O(k) |
| With cooldown | `hold[i] = max(hold[i−1], free[i−2] − p)`, `free[i] = max(free[i−1], hold[i−1] + p)` | `free[−1]=free[−2]=0`, `hold[−1]=−∞` | `free[n−1]` | O(n) / O(1) |
| With transaction fee | `free = max(free, hold + p − fee)`, `hold = max(hold, free_old − p)` | `hold=−p₀, free=0` | `free` | O(n) / O(1) |
| Count Vowels Permutation | `dp[i][v] = Σ dp[i−1][u]` over vowels u that may be followed by v | `dp[1][v]=1` | `Σ_v dp[n][v]` | O(5n) / O(1) |
| Min Swaps To Make Sequences Increasing | `keep[i]`, `swap[i]` = min swaps for prefix with position i kept/swapped; transitions depend on which orderings stay strictly increasing | `keep[0]=0, swap[0]=1` | `min(keep, swap)` | O(n) / O(1) |
| Student Attendance Record II | `dp[i][absences ∈{0,1}][trailingLate ∈{0,1,2}]` | `dp[0][0][0]=1` | sum | O(6n) / O(1) |

Related practice: [06-leetcode-practice/05-stocks-and-state-machines](../06-leetcode-practice/05-stocks-and-state-machines/).

---

## 8. Tree DP

Folder: [07-dp-on-trees](../04-advanced-dp-patterns/07-dp-on-trees/) — always **post-order** (children before parent).

| Problem | What each node returns | Combine | Answer | Time / Space |
|---|---|---|---|---|
| House Robber III | `(rob, skip)` | `rob = val + skip(L) + skip(R)`; `skip = max(L) + max(R)` | `max(root)` | O(n) / O(height) |
| Diameter of Binary Tree | `depth` = longest downward path in edges+1 | `depth = 1 + max(dL, dR)`; global `best = max(best, dL + dR)` | `best` | O(n) / O(h) |
| Binary Tree Max Path Sum | `gain` = best downward path sum starting here | `gain = val + max(0, gL, gR)`; global `best = max(best, val + max(0,gL) + max(0,gR))` | `best` | O(n) / O(h) |
| Binary Tree Cameras | one of 3 states: NOT_COVERED, COVERED_NO_CAM, HAS_CAM | child NOT_COVERED → place camera here; child HAS_CAM → covered; else not covered | +1 if root NOT_COVERED | O(n) / O(h) |
| Sum of Distances in Tree (rerooting) | pass 1: `size[v]`, `down[v]`; pass 2: `ans[child] = ans[parent] − size[child] + (n − size[child])` | two DFS passes | `ans[]` | O(n) / O(n) |
| Tree Matching (CSES) | `(matched, free)` = best with v matched to a child / v left unmatched | let `S = Σ_c max(matched_c, free_c)`; `free_v = S`; `matched_v = max_c (S − max(matched_c, free_c) + free_c + 1)` | `max` of root pair | O(n) / O(n) |

Related practice: [06-leetcode-practice/07-trees-and-graphs](../06-leetcode-practice/07-trees-and-graphs/).

---

## 9. Graph / DAG DP

Folder: [08-dp-on-graphs](../04-advanced-dp-patterns/08-dp-on-graphs/)

| Problem | State | Recurrence | Base | Order | Time / Space |
|---|---|---|---|---|---|
| Longest path in a DAG | `dp[v]` = longest path starting at v | `dp[v] = max_{v→u} 1 + dp[u]` | sinks: 0 | reverse topological order (or memo DFS) | O(V+E) / O(V) |
| Count paths in a DAG | `ways[v]` = paths from source to v | `ways[v] = Σ_{u→v} ways[u]` | `ways[src]=1` | topological order | O(V+E) / O(V) |
| Parallel Courses III | `finish[v]` = earliest finish of course v | `time[v] + max_{u→v} finish[u]` | no prereqs → `time[v]` | topo order | O(V+E) / O(V) |
| Cheapest Flights Within K Stops | `cost[t][v]` = min cost to reach v using ≤ t flights | `cost[t][v] = min(cost[t−1][v], min_{u→v} cost[t−1][u] + w)` | `cost[0][src]=0` | t ↑ (copy the previous layer!) | O(k·E) / O(V) |
| Number of Ways to Arrive at Destination | Dijkstra distances + `ways[v]` | when relaxing with an equal distance, `ways[v] += ways[u]`; strictly better → `ways[v] = ways[u]` | `ways[src]=1` | Dijkstra pop order | O(E log V) |
| Largest Color Value in a Directed Graph | `cnt[v][color]` = max count of color on a path ending at v | `cnt[v][c] = max(cnt[v][c], cnt[u][c] + (color(v)==c))` | each node counts its own color | Kahn topo order; cycle → −1 | O(26(V+E)) |

---

## 10. Bitmask DP

Folders: [09-bitmask-dp](../04-advanced-dp-patterns/09-bitmask-dp/) · [10-traveling-salesperson-problem](../04-advanced-dp-patterns/10-traveling-salesperson-problem/)

`mask` is an integer whose bit `j` says "item j is used". `mask | (1 << j)` adds item j; `(mask >> j) & 1` tests it; `Integer.bitCount(mask)` counts used items.

| Problem | State | Recurrence | Base | Order | Time / Space |
|---|---|---|---|---|---|
| TSP | `dp[mask][v]` = min cost of a path that starts at 0, visits exactly `mask`, ends at v | `dp[mask \| 1<<u][u] = min(dp[mask][v] + d[v][u])` for u ∉ mask | `dp[1][0]=0` | mask ↑ (adding bits makes it larger) | O(2ⁿ·n²) / O(2ⁿ·n) |
| Assignment (Min XOR Sum) | `dp[mask]` = min cost after matching the first `popcount(mask)` items of A to the set `mask` of B | `dp[mask \| 1<<j] = min(dp[mask] + cost(popcount(mask), j))` | `dp[0]=0` | mask ↑ | O(2ⁿ·n) / O(2ⁿ) |
| Shortest Path Visiting All Nodes | BFS over `(node, mask)` | edges of the graph; first time `mask == full` | all `(v, 1<<v)` at distance 0 | BFS layers | O(2ⁿ·n²) |
| Partition to K Equal Sum Subsets | `dp[mask]` = current fill (mod target) of the open bucket, or −1 if unreachable | add item j if `dp[mask] + a[j] ≤ target` | `dp[0]=0` | mask ↑ | O(2ⁿ·n) / O(2ⁿ) |
| Can I Win | `win[mask]` = player to move wins with numbers `mask` used | `win[mask] = OR_j (j+running ≥ target \|\| !win[mask \| 1<<j])` | — | memo DFS | O(2ⁿ·n) |
| SOS (sum over subsets) | `F[mask] = Σ_{sub ⊆ mask} A[sub]` | for each bit b: for each mask with bit b: `F[mask] += F[mask ^ (1<<b)]` | `F = A` | bit outer, mask any | O(n·2ⁿ) — see [SOS DP](../05-dp-optimization/07-sos-dp/) |

Related practice: [06-leetcode-practice/08-bitmask](../06-leetcode-practice/08-bitmask/).

---

## 11. Digit DP

Folder: [11-digit-dp](../04-advanced-dp-patterns/11-digit-dp/)

Count numbers in `[0, N]` with some property; for `[L, R]` use `count(R) − count(L−1)`.

```
f(pos, tight, started, extra):
    if pos == len(N): return (started && property(extra)) ? 1 : 0
    limit = tight ? N[pos] : 9
    total = 0
    for d in 0..limit:
        newTight   = tight && (d == limit)
        newStarted = started || (d != 0)
        newExtra   = update(extra, d, newStarted)
        total += f(pos+1, newTight, newStarted, newExtra)
    return total
```

| Property | `extra` | States |
|---|---|---|
| digit sum ≡ 0 mod D | sum mod D | len·2·2·D |
| all digits distinct | 10-bit mask of used digits | len·2·2·1024 |
| no two adjacent equal / no consecutive 1s in binary | last digit | len·2·2·10 |
| count of digit 1 (Number of Digit One) | count so far (or return a pair) | len·2·len |

Memoize only when `tight == false` (or include `tight` in the key). Time = states × 10.
Related practice: [06-leetcode-practice/09-digit](../06-leetcode-practice/09-digit/).

---

## 12. Optimization techniques

Folder: [05-dp-optimization](../05-dp-optimization/)

| Technique | Applies when the recurrence looks like | Effect | Folder |
|---|---|---|---|
| Space optimization / rolling arrays | row i only reads row i−1 (or a few earlier rows) | O(nm) → O(m) memory | [01](../05-dp-optimization/01-space-optimization/), [02](../05-dp-optimization/02-rolling-arrays/) |
| Prefix / suffix max | `dp[i][j] = best_{k≤j} (dp[i−1][k] + f(k))` | O(n·m²) → O(n·m) | [03](../05-dp-optimization/03-prefix-and-suffix-optimization/) |
| Monotonic queue | `dp[i] = a[i] + max(dp[i−k..i−1])` (sliding window) | O(nk) → O(n) | [04](../05-dp-optimization/04-monotonic-queue-optimization/) |
| Divide & conquer | `dp[t][i] = min_{j<i} dp[t−1][j] + C(j, i)` with monotone best `j` | O(k·n²) → O(k·n log n) | [05](../05-dp-optimization/05-divide-and-conquer-optimization/) |
| Convex hull trick | `dp[i] = min_j (m_j · x_i + b_j)` (lines) | O(n²) → O(n) or O(n log n) | [06](../05-dp-optimization/06-convex-hull-trick/) |
| SOS DP | sums over all submasks | O(3ⁿ) → O(n·2ⁿ) | [07](../05-dp-optimization/07-sos-dp/) |
| Knuth | interval DP with `opt[i][j−1] ≤ opt[i][j] ≤ opt[i+1][j]` | O(n³) → O(n²) | [08](../05-dp-optimization/08-knuth-optimization/) |
| Matrix exponentiation | linear recurrence with constant coefficients, huge n | O(n) → O(s³ log n) for s states | [09](../05-dp-optimization/09-matrix-exponentiation/) |

Related practice: [06-leetcode-practice/10-optimization](../06-leetcode-practice/10-optimization/).

---

## 13. Base-case and order quick reference

| If the recurrence reads … | … then loop | Example |
|---|---|---|
| `dp[i−1]`, `dp[i−2]` | i ascending | House Robber |
| `dp[i+1]`, `dp[i+k]` | i descending | Brainpower, Tickets, Triangle |
| `dp[i−1][j]`, `dp[i][j−1]`, `dp[i−1][j−1]` | i ↑ then j ↑ | LCS, Edit Distance, Unique Paths |
| `dp[i+1][j]`, `dp[i][j−1]`, `dp[i+1][j−1]` | i ↓ then j ↑ (or by length) | LPS, palindrome table |
| `dp[i][k]` and `dp[k][j]` for i<k<j | by interval length ↑ | MCM, Burst Balloons |
| `dp[prev row][c − w]` in a 1D array | c descending | 0/1 Knapsack |
| `dp[same row][c − w]` in a 1D array | c ascending | Unbounded Knapsack |
| children of a tree node | post-order DFS | Tree DP |
| `dp[mask without j]` | mask ascending | Bitmask DP |

Common base-case values:

| Meaning of the answer | Empty / impossible state |
|---|---|
| number of ways | empty target = **1** way (choose nothing); impossible = 0 |
| min cost | empty = 0; impossible = a safe "infinity" (e.g. `amount + 1` or `Integer.MAX_VALUE / 2`) |
| max value | empty = 0; impossible = a safe "−infinity" (e.g. `Integer.MIN_VALUE / 2`) |
| feasibility | empty target = **true** |

---

**See also:** [DP_PATTERN_RECOGNITION.md](DP_PATTERN_RECOGNITION.md) · [COMMON_MISTAKES.md](COMMON_MISTAKES.md) · [module README](README.md)
