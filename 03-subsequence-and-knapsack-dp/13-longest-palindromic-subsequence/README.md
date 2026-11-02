# Longest Palindromic Subsequence (LPS)

> **Module:** Subsequence and Knapsack DP · **Difficulty:** Intermediate · **Estimated time:** 90–120 min

## 1. What is this topic?

A **palindrome** reads the same forwards and backwards (`"level"`, `"abba"`). Given one string, find the length of its longest **subsequence** that is a palindrome. In `"bbbab"` you can delete the `a` and keep `"bbbb"`: the answer is 4.

You will solve it in two different ways and see that they agree:

1. **Reduction to LCS:** `LPS(s) = LCS(s, reverse(s))` — reuse the previous topic unchanged.
2. **Direct interval DP:** `lps(i, j)` = answer for the window `s[i..j]`, shrinking from **both ends**. This is your first **interval DP**, a pattern that returns in [module 04](../../04-advanced-dp-patterns/03-interval-dp/).

## 2. Why does this concept exist?

The topic teaches two transferable skills:

- **Reduction**: recognising that a new problem is an old problem in disguise (one string vs. its reverse).
- **Interval states**: when a problem is about matching the **two ends** of a string, the natural state is a window `(i, j)`, and the table is filled by **window length**, not left to right.

## 3. What problem does it solve?

[LeetCode 516 — Longest Palindromic Subsequence](https://leetcode.com/problems/longest-palindromic-subsequence/), and immediately [LeetCode 1312 — Minimum Insertion Steps to Make a String Palindrome](https://leetcode.com/problems/minimum-insertion-steps-to-make-a-string-palindrome/), whose answer is `n − LPS`.

## 4. Prerequisites

- [Longest Common Subsequence](../12-longest-common-subsequence/) — the table we reuse.
- [Subsequences Fundamentals](../01-subsequences-fundamentals/) — what "subsequence" means.
- [Transition Dependencies and Computation Order](../../00-foundations/11-transition-dependencies-and-computation-order/) — why the interval table is filled bottom-up by rows or by lengths.

## 5. What you will learn

- Why every palindromic subsequence of `s` is a common subsequence of `s` and `reverse(s)`, and why the lengths of the longest ones are equal.
- The interval recurrence: match the two ends, or drop one of them.
- How to fill an interval table: `i` from right to left, `j` from `i` to the right (or by window length).
- An O(n) memory version of the interval table.
- Turning LPS into "minimum insertions" and "minimum deletions" answers.

## 6. Important terminology

| Term | Meaning in plain English |
|---|---|
| Palindrome | A string equal to its own reverse |
| Window `s[i..j]` | The characters from index `i` to index `j`, both included |
| Interval DP | DP whose state is a range `(i, j)`; answers for long ranges are built from shorter ranges |
| Outer pair | The first and last characters of a palindrome; they must be equal |
| Reduction | Solving a problem by transforming it into one we already know how to solve |
| Palindromic substring | A **contiguous** palindrome (different problem: [LC 5](https://leetcode.com/problems/longest-palindromic-substring/), [LC 647](https://leetcode.com/problems/palindromic-substrings/)) |

## 7. Where this is used in real problems

- Minimum insertions or deletions to make a string a palindrome (`n − LPS` in both cases).
- "Is this string a k-palindrome?" (can you delete at most k characters to make it a palindrome?) → check `n − LPS ≤ k`.
- Interval DP in general: [palindrome partitioning](../../04-advanced-dp-patterns/06-palindrome-partitioning/), [burst balloons](../../04-advanced-dp-patterns/05-burst-balloons/), [matrix chain multiplication](../../04-advanced-dp-patterns/04-matrix-chain-multiplication/).

## 8. After completing this topic you should be able to…

- [ ] Compute LPS by calling an LCS function on `s` and `reverse(s)`.
- [ ] Explain why the interval recurrence has exactly these cases: ends match / ends differ.
- [ ] Fill the interval table for `"bbbab"` in a valid order.
- [ ] Write the two-row O(n) memory version.
- [ ] Solve LC 1312 in one line once LPS is known, and explain why.
- [ ] Tell palindromic subsequence apart from palindromic substring.

## 9. Practice problems

| # | Problem | Platform/ID | Difficulty | Pattern tested | Why it belongs here |
|---|---|---|---|---|---|
| 1 | [Longest Palindromic Subsequence](https://leetcode.com/problems/longest-palindromic-subsequence/) | LeetCode 516 | Medium | Interval DP or LCS with reverse | The topic problem itself |
| 2 | [Palindromic Substrings](https://leetcode.com/problems/palindromic-substrings/) | LeetCode 647 | Medium | Interval DP on substrings (contrast) | Same `(i, j)` window, but contiguous: `isPal[i][j] = s[i]==s[j] && isPal[i+1][j-1]` |
| 3 | [Longest Palindromic Substring](https://leetcode.com/problems/longest-palindromic-substring/) | LeetCode 5 | Medium | Interval DP / expand around centre | Contrast with subsequences; same table shape |
| 4 | [Minimum Insertion Steps to Make a String Palindrome](https://leetcode.com/problems/minimum-insertion-steps-to-make-a-string-palindrome/) | LeetCode 1312 | Hard | `n − LPS` | The "Hard" is one line once you know LPS |

## 10. Navigation

⬅ Previous: [Longest Common Subsequence](../12-longest-common-subsequence/) · ➡ Next: [Edit Distance](../14-edit-distance/) · 📖 [Walkthrough](walkthrough.md) · 💻 [Solutions](solution/)
