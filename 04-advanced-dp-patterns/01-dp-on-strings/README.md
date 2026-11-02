# DP on Strings

> **Module:** 04 — Advanced DP Patterns · **Difficulty:** Advanced · **Estimated time:** 4–5 hours

## 1. What is this topic?

A consolidation topic. You have already solved many string problems with DP — LCS, longest palindromic subsequence, edit distance, distinct subsequences, shortest common supersequence. This topic steps back and shows that almost every string DP has one of **two shapes**:

1. **Two strings, two prefixes:** `dp[i][j]` = answer for the first `i` characters of string A and the first `j` characters of string B (LCS, edit distance).
2. **One string, one substring:** `dp[i][j]` = answer for the substring `s[i..j]` (longest palindromic subsequence, palindromic substrings).

Both are written `dp[i][j]`, but they mean different things, are filled in different orders, and have different base cases. Then you use the first shape on two famous Hard problems — **Wildcard Matching (LC 44)** and **Regular Expression Matching (LC 10)** — where the whole difficulty is understanding what `*` means.

## 2. Why does this concept exist?

When you meet a new string problem, the hardest step is choosing the state. If you can say in five seconds "this is two-prefix DP" or "this is substring DP", the recurrence often writes itself. Pattern matching is the best training ground because a single symbol, `*`, behaves differently in two problems and forces you to think carefully about what one cell of the table means.

## 3. What problem does it solve?

- Does a text match a pattern containing wildcards (`?`, `*`) or regex symbols (`.`, `*`)?
- Can a third string be formed by interleaving two others (LC 97)?
- Which substrings are palindromes (LC 5, LC 647)? This `isPal[i][j]` table is reused in [Palindrome Partitioning](../06-palindrome-partitioning/).

## 4. Prerequisites

- [Longest Common Subsequence](../../03-subsequence-and-knapsack-dp/12-longest-common-subsequence/) — the two-prefix table
- [Longest Palindromic Subsequence](../../03-subsequence-and-knapsack-dp/13-longest-palindromic-subsequence/) — the substring table
- [Edit Distance](../../03-subsequence-and-knapsack-dp/14-edit-distance/) — reading "up", "left" and "diagonal" as actions
- [Distinct Subsequences](../../03-subsequence-and-knapsack-dp/15-distinct-subsequences/)
- [Transition dependencies and computation order](../../00-foundations/11-transition-dependencies-and-computation-order/)

## 5. What you will learn

- A checklist to tell two-prefix DP from substring (interval) DP.
- The exact meaning of `*` in wildcard matching ("any run of characters") and in regex matching ("zero or more copies of the previous character").
- How each meaning becomes a recurrence, and how to read it off a table as arrows (left, up, diagonal, two-left).
- How to compress a two-prefix table to one or two rows.
- How the `isPal[i][j]` table is built in O(n²).

## 6. Important terminology

| Term | Meaning in plain English |
|---|---|
| Prefix | The first `i` characters of a string, `s[0..i-1]`. The empty prefix has `i = 0`. |
| Suffix | Everything from position `i` to the end, `s[i..]`. |
| Substring | A contiguous piece `s[i..j]`. |
| Two-prefix DP | A table where `dp[i][j]` describes the first `i` chars of one string and the first `j` chars of another. |
| Interval (substring) DP | A table where `dp[i][j]` describes the piece `s[i..j]` of a single string. |
| Wildcard `?` | Matches exactly one character, any character. |
| Wildcard `*` (LC 44) | Matches any sequence of characters, including the empty one. It stands alone. |
| Regex `.` | Matches exactly one character, any character. |
| Regex `*` (LC 10) | Means "zero or more copies of the character just before me". It is glued to that character. |
| Full match | The pattern must cover the **whole** text, not just part of it. |

## 7. Where this is used in real problems

- File globbing (`*.java`), search filters, and simple regex engines.
- Diff tools and spell checkers (edit distance and its relatives).
- DNA alignment (two-prefix DP with scores).
- Any problem that says "two strings" (LC 97, LC 583, LC 712, LC 1035) or "substring / palindrome" (LC 5, LC 647, LC 132).

## 8. After completing this topic you should be able to…

- [ ] Say whether a string problem needs a two-prefix table or a substring table, and justify it.
- [ ] Write the wildcard recurrence and explain why `*` reads "left OR up".
- [ ] Write the regex recurrence and explain why `x*` reads "two-left OR (match AND up)".
- [ ] Fill both tables by hand on a small example, including row 0.
- [ ] Reduce the wildcard table to two rows.
- [ ] Build `isPal[i][j]` and use it to solve LC 5 and LC 647.

## 9. Practice problems

| # | Problem | Platform/ID | Difficulty | Pattern tested | Why it belongs here |
|---|---|---|---|---|---|
| 1 | [Palindromic Substrings](https://leetcode.com/problems/palindromic-substrings/) | LeetCode 647 | Medium | Substring DP, `isPal[i][j]` | The simplest one-string interval table; count its true cells. |
| 2 | [Longest Palindromic Substring](https://leetcode.com/problems/longest-palindromic-substring/) | LeetCode 5 | Medium | Substring DP, `isPal[i][j]` | Same table, now track the longest true cell. |
| 3 | [Interleaving String](https://leetcode.com/problems/interleaving-string/) | LeetCode 97 | Medium | Two-prefix DP | `dp[i][j]` on two prefixes with the third string's position implied by `i + j`. |
| 4 | [Minimum Insertion Steps to Make a String Palindrome](https://leetcode.com/problems/minimum-insertion-steps-to-make-a-string-palindrome/) | LeetCode 1312 | Hard | Substring DP | Shows that a "Hard" can be one recurrence on `s[i..j]`. |
| 5 | [Wildcard Matching](https://leetcode.com/problems/wildcard-matching/) | LeetCode 44 | Hard | Two-prefix DP with `*` = any run | Main problem of this topic. |
| 6 | [Regular Expression Matching](https://leetcode.com/problems/regular-expression-matching/) | LeetCode 10 | Hard | Two-prefix DP with `x*` = repeat | Main problem of this topic; harder `*`. |

Solutions in this folder: [WildcardMemoization.java](solution/WildcardMemoization.java), [WildcardTabulation.java](solution/WildcardTabulation.java), [WildcardOptimized.java](solution/WildcardOptimized.java), [RegexMemoization.java](solution/RegexMemoization.java), [RegexTabulation.java](solution/RegexTabulation.java), [PalindromicSubstrings.java](solution/PalindromicSubstrings.java) (LC 5 and LC 647).

## 10. Navigation

⬅ Previous: [Shortest Common Supersequence](../../03-subsequence-and-knapsack-dp/16-shortest-common-supersequence/) · ➡ Next: [Stock Buy and Sell](../02-stock-buy-and-sell/) · 📖 [Walkthrough](walkthrough.md) · 💻 [Solutions](solution/)
