# Palindrome Partitioning

> **Module:** 04 — Advanced DP Patterns · **Difficulty:** Advanced · **Estimated time:** 3 hours

## 1. What is this topic?

Two related problems about cutting a string into pieces that are all palindromes:

- [LC 131 Palindrome Partitioning](https://leetcode.com/problems/palindrome-partitioning/): list **every** way to do it. This is backtracking, sped up by a precomputed `isPal[i][j]` table.
- [LC 132 Palindrome Partitioning II](https://leetcode.com/problems/palindrome-partitioning-ii/): find the **minimum number of cuts**. This can be solved as an interval DP like [MCM](../04-matrix-chain-multiplication/) in O(n³) — but a smarter state, "where does the remaining suffix start?", gives a **1D front-partition DP** in O(n²).

The main lesson: interval DP is not always the best tool for a "cut into pieces" problem. If the pieces are taken **from left to right**, one index is enough.

## 2. Why does this concept exist?

Many problems ask you to split a sequence into consecutive groups, each group valid in some way, minimising or maximising a total. If you try "every split point in every range", you get O(n³) interval DP. But consecutive groups can always be described as "the first group, then the rest", and "the rest" is just a suffix — a single index. This **front-partition** pattern turns many O(n³) solutions into O(n²) ones (LC 132, LC 1043, LC 139).

## 3. What problem does it solve?

- Enumerate all partitions of a string into palindromes (LC 131).
- Minimise the number of pieces (or cuts) in such a partition (LC 132).
- More generally: split an array into consecutive blocks under a rule, optimising a total (LC 1043 Partition Array for Maximum Sum, LC 139 Word Break).

## 4. Prerequisites

- [DP on Strings](../01-dp-on-strings/) — the `isPal[i][j]` table
- [Interval DP](../03-interval-dp/) and [Matrix Chain Multiplication](../04-matrix-chain-multiplication/) — the O(n³) version
- [Decode Ways](../../01-one-dimensional-dp/08-decode-ways/) — a 1D DP that chooses the length of the first piece
- [Recursion fundamentals](../../00-foundations/02-recursion-fundamentals/) — backtracking with add / recurse / remove

## 5. What you will learn

- How to build `isPal[i][j]` once in O(n²) and reuse it.
- Backtracking that enumerates all palindrome partitions.
- The O(n³) interval-DP solution of min cuts, and why it does too much work.
- The O(n²) front-partition DP: `pieces[start] = min over palindromic s[start..end] of 1 + pieces[end+1]`.
- How the same pattern solves LC 1043.

## 6. Important terminology

| Term | Meaning in plain English |
|---|---|
| Palindrome | A string that reads the same forwards and backwards (`"abba"`, `"c"`). |
| Partition | Cutting a string into consecutive non-empty pieces that together make the whole string. |
| Cut | A boundary between two pieces. `p` pieces need `p − 1` cuts. |
| `isPal[i][j]` | Precomputed answer to "is `s[i..j]` a palindrome?". |
| Backtracking | Recursion that builds a solution step by step and undoes the last step before trying the next option. |
| Front partition | Choosing the first piece, then solving the remaining suffix (state = one index). |
| Interval version | Choosing a split point inside any range `i..j` (state = two indices). |

## 7. Where this is used in real problems

- Text segmentation: splitting a string into dictionary words ([Word Break](https://leetcode.com/problems/word-break/)).
- Grouping consecutive items into blocks with a cost per block (LC 1043; job scheduling over days, LC 1335).
- Any "min number of pieces such that each piece is valid".

## 8. After completing this topic you should be able to…

- [ ] Build `isPal[i][j]` and explain its fill order.
- [ ] Write the backtracking solution of LC 131 and draw its recursion tree.
- [ ] Write the interval version of LC 132 and explain its O(n³) cost.
- [ ] Write the front-partition version of LC 132 in O(n²) and explain why one index is enough.
- [ ] Recognise LC 1043 and LC 139 as front-partition DPs.

## 9. Practice problems

| # | Problem | Platform/ID | Difficulty | Pattern tested | Why it belongs here |
|---|---|---|---|---|---|
| 1 | [Palindromic Substrings](https://leetcode.com/problems/palindromic-substrings/) | LeetCode 647 | Medium | `isPal[i][j]` table | The table this topic depends on. |
| 2 | [Palindrome Partitioning](https://leetcode.com/problems/palindrome-partitioning/) | LeetCode 131 | Medium | Backtracking + `isPal` | Enumerate all partitions. |
| 3 | [Word Break](https://leetcode.com/problems/word-break/) | LeetCode 139 | Medium | Front partition (boolean) | Same "first piece + rest" shape with a dictionary instead of palindromes. |
| 4 | [Partition Array for Maximum Sum](https://leetcode.com/problems/partition-array-for-maximum-sum/) | LeetCode 1043 | Medium | Front partition with a max | The same 1D pattern with pieces of length ≤ k. |
| 5 | [Palindrome Partitioning II](https://leetcode.com/problems/palindrome-partitioning-ii/) | LeetCode 132 | Hard | Front partition (min pieces) | Main DP of this topic; O(n²) instead of O(n³). |

Solutions in this folder: [PalindromePartitioning.java](solution/PalindromePartitioning.java) (LC 131), [MinCutsInterval.java](solution/MinCutsInterval.java) (LC 132, O(n³)), [MinCutsMemoization.java](solution/MinCutsMemoization.java) and [MinCutsTabulation.java](solution/MinCutsTabulation.java) (LC 132, O(n²)).

## 10. Navigation

⬅ Previous: [Burst Balloons](../05-burst-balloons/) · ➡ Next: [DP on Trees](../07-dp-on-trees/) · 📖 [Walkthrough](walkthrough.md) · 💻 [Solutions](solution/)
