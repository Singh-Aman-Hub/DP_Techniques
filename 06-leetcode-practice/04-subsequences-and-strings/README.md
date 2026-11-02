# Practice: Subsequences & Strings

These problems use the DP families from the subsequence module: **LCS** (two sequences, `dp[i][j]` over two prefixes), **LIS** (one sequence, "best chain ending at `i`"), and **palindrome DP** (one string, a range `[i..j]`). Each problem changes one detail of a pattern you already know: a different mismatch rule, a weight, a sort first, or a count alongside the length.

## How to recognize this pattern

| Clue in the statement | Family | Typical state |
|---|---|---|
| two strings or arrays, "common", "match", "delete to make equal", "merge" | LCS / two-prefix | `dp[i][j]` over prefixes of both |
| **contiguous** block ("substring", "subarray") shared by two sequences | LCS, substring variant | `dp[i][j]` = block **ending exactly** at i, j |
| "longest chain / sequence where each next item is bigger / divisible / one letter longer" | LIS | `best[i]` = best chain ending at `i` (sort first if no order is given) |
| "palindrome", "reads the same both ways" | range DP on one string | `dp[i][j]` over `s[i..j]` |

Before starting, work through these topics: [Longest Increasing Subsequence](../../03-subsequence-and-knapsack-dp/11-longest-increasing-subsequence/), [Longest Common Subsequence](../../03-subsequence-and-knapsack-dp/12-longest-common-subsequence/), [Longest Palindromic Subsequence](../../03-subsequence-and-knapsack-dp/13-longest-palindromic-subsequence/), [Edit Distance](../../03-subsequence-and-knapsack-dp/14-edit-distance/), and [DP on Strings](../../04-advanced-dp-patterns/01-dp-on-strings/).

## Problems (easiest → hardest)

| # | Problem | LeetCode link | Difficulty | Pattern | Prerequisite topic folder |
|---|---|---|---|---|---|
| 1 | [1035. Uncrossed Lines](1035-uncrossed-lines/) | [LC 1035](https://leetcode.com/problems/uncrossed-lines/) | Medium | LCS in disguise | [12-longest-common-subsequence](../../03-subsequence-and-knapsack-dp/12-longest-common-subsequence/) |
| 2 | [718. Maximum Length of Repeated Subarray](718-maximum-length-of-repeated-subarray/) | [LC 718](https://leetcode.com/problems/maximum-length-of-repeated-subarray/) | Medium | LCS, substring variant (reset to 0) | [12-longest-common-subsequence](../../03-subsequence-and-knapsack-dp/12-longest-common-subsequence/) |
| 3 | [583. Delete Operation for Two Strings](583-delete-operation-for-two-strings/) | [LC 583](https://leetcode.com/problems/delete-operation-for-two-strings/) | Medium | LCS / deletion-only edit distance | [14-edit-distance](../../03-subsequence-and-knapsack-dp/14-edit-distance/) |
| 4 | [712. Minimum ASCII Delete Sum for Two Strings](712-minimum-ascii-delete-sum-for-two-strings/) | [LC 712](https://leetcode.com/problems/minimum-ascii-delete-sum-for-two-strings/) | Medium | weighted LCS | [14-edit-distance](../../03-subsequence-and-knapsack-dp/14-edit-distance/) |
| 5 | [647. Palindromic Substrings](647-palindromic-substrings/) | [LC 647](https://leetcode.com/problems/palindromic-substrings/) | Medium | palindrome range table / expand around center | [13-longest-palindromic-subsequence](../../03-subsequence-and-knapsack-dp/13-longest-palindromic-subsequence/) |
| 6 | [5. Longest Palindromic Substring](5-longest-palindromic-substring/) | [LC 5](https://leetcode.com/problems/longest-palindromic-substring/) | Medium | palindrome range table / expand around center | [13-longest-palindromic-subsequence](../../03-subsequence-and-knapsack-dp/13-longest-palindromic-subsequence/) |
| 7 | [673. Number of Longest Increasing Subsequence](673-number-of-longest-increasing-subsequence/) | [LC 673](https://leetcode.com/problems/number-of-longest-increasing-subsequence/) | Medium | LIS + count of ways | [11-longest-increasing-subsequence](../../03-subsequence-and-knapsack-dp/11-longest-increasing-subsequence/) |
| 8 | [368. Largest Divisible Subset](368-largest-divisible-subset/) | [LC 368](https://leetcode.com/problems/largest-divisible-subset/) | Medium | sort + LIS-style chain + reconstruction | [11-longest-increasing-subsequence](../../03-subsequence-and-knapsack-dp/11-longest-increasing-subsequence/) |
| 9 | [1048. Longest String Chain](1048-longest-string-chain/) | [LC 1048](https://leetcode.com/problems/longest-string-chain/) | Medium | sort by length + chain DP in a hash map | [11-longest-increasing-subsequence](../../03-subsequence-and-knapsack-dp/11-longest-increasing-subsequence/) |
| 10 | [97. Interleaving String](97-interleaving-string/) | [LC 97](https://leetcode.com/problems/interleaving-string/) | Medium | two-prefix boolean DP (grid reachability) | [01-dp-on-strings](../../04-advanced-dp-patterns/01-dp-on-strings/) |
| 11 | [1312. Minimum Insertion Steps to Make a String Palindrome](1312-minimum-insertion-steps-to-make-a-string-palindrome/) | [LC 1312](https://leetcode.com/problems/minimum-insertion-steps-to-make-a-string-palindrome/) | Hard | range DP = n − LPS | [13-longest-palindromic-subsequence](../../03-subsequence-and-knapsack-dp/13-longest-palindromic-subsequence/) |
| 12 | [354. Russian Doll Envelopes](354-russian-doll-envelopes/) | [LC 354](https://leetcode.com/problems/russian-doll-envelopes/) | Hard | 2D LIS: sort (ties descending) + O(n log n) LIS | [11-longest-increasing-subsequence](../../03-subsequence-and-knapsack-dp/11-longest-increasing-subsequence/) |

Within the same difficulty, problems are ordered by how far they move from the base pattern.

## Suggested pairs to compare

- **1035 vs 718:** the same input shape. Gaps allowed ⇒ `max(up, left)`. Contiguous ⇒ reset to 0 and take the max over all cells.
- **583 vs 712:** the same table. Unit cost vs per-character cost inside the `min`.
- **647 vs 5:** the same `isPal[i][j]` table. Count the `true` cells vs keep the longest.
- **5 vs 1312:** a palindrome substring (no skipping) vs a palindrome subsequence (skipping costs an insertion).
- **368 vs 1048 vs 354:** "sort first, then chain". The link test is divisibility, then one-letter deletion, then a smaller height with a descending tie-break.

Each problem folder contains a README (the 7 derivation questions, a dry run, complexity and mistakes) and a `solution/` folder with self-checking Java files (`java Tabulation.java`).
