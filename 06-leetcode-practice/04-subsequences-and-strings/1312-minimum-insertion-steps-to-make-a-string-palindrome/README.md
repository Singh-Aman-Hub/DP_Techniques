# 1312. Minimum Insertion Steps to Make a String Palindrome

> **Pattern:** Subsequences & strings, palindrome range DP (the Longest Palindromic Subsequence in disguise) · **Difficulty:** Hard · **Builds on:** [Longest Palindromic Subsequence](../../../03-subsequence-and-knapsack-dp/13-longest-palindromic-subsequence/), [Interval DP](../../../04-advanced-dp-patterns/03-interval-dp/)

## Problem overview

You may insert any character at any position of a string, as many times as you like. Return the fewest insertions that turn the string into a palindrome.

It is labelled Hard, but once you see the link to the **Longest Palindromic Subsequence (LPS)** it is a short DP:
```
answer = n − LPS(s)
```
The characters of the LPS already mirror each other. Every other character needs one inserted partner on the opposite side.

## Link & metadata

| Field | Value |
|---|---|
| Problem | [1312. Minimum Insertion Steps to Make a String Palindrome](https://leetcode.com/problems/minimum-insertion-steps-to-make-a-string-palindrome/) |
| Difficulty | Hard |
| Pattern | Range DP `dp[i][j]` on one string, where ends match or one end is fixed |
| Prerequisite topic | [03-subsequence-and-knapsack-dp/13-longest-palindromic-subsequence](../../../03-subsequence-and-knapsack-dp/13-longest-palindromic-subsequence/) |
| Also useful | [04-advanced-dp-patterns/03-interval-dp](../../../04-advanced-dp-patterns/03-interval-dp/) |

## Examples

| s | Answer | One result |
|---|---|---|
| `"zzazz"` | 0 | already a palindrome |
| `"mbadm"` | 2 | `"mbdadbm"`: insert `d` and `b` |
| `"leetcode"` | 5 | LPS is `"eee"` (length 3), and 8 − 3 = 5 |
| `"ab"` | 1 | `"aba"` |
| `"a"` | 0 | one character |

Running example: `s = "mbadm"`.

## Brute-force intuition

Look at the two **ends** of the current range `s[i..j]`:
- If `s[i] == s[j]`, they already mirror each other. Solve the inside `s[i+1..j-1]`.
- If not, one end needs a partner. Either insert a copy of `s[i]` after position `j` (now `s[i]` is matched, so solve `s[i+1..j]`), or insert a copy of `s[j]` before `i` (solve `s[i..j-1]`). Each costs 1, and we try both.

On mismatch the recursion branches in two, which makes it exponential, and the ranges overlap heavily.

## Deriving the DP (7 questions)

### 1. What is changing?
The range of the string still to be fixed: left end `i` and right end `j`. This is a **range state** on one string, the shape of [interval DP](../../../04-advanced-dp-patterns/03-interval-dp/).

### 2. What does the state represent?
> `dp[i][j]` = the minimum number of insertions that make `s[i..j]` a palindrome.

### 3. What are the choices?
- Ends equal: no choice needed, keep both.
- Ends different: pair `s[i]` with a new copy (cost 1, remaining range `[i+1..j]`) **or** pair `s[j]` with a new copy (cost 1, remaining `[i..j-1]`).

### 4. What is the recurrence?
```
dp[i][j] = dp[i+1][j-1]                         if s[i] == s[j]
dp[i][j] = 1 + min(dp[i+1][j], dp[i][j-1])      otherwise
```
Compare with LPS: `lps[i][j] = 2 + lps[i+1][j-1]` when the ends match, otherwise `max(lps[i+1][j], lps[i][j-1])`. Both tables have the same shape and dependencies, with "min insertions" in place of "max kept length". That is why `n − LPS` gives the same answer.

### 5. What are the base cases?
- `i == j` (one character): 0 insertions.
- `i > j` (empty range, reached when two equal neighbours are removed): 0.

### 6. What is the order of computation?
`dp[i][j]` reads `dp[i+1][*]` (the row **below**) and `dp[i][j-1]` (to the **left**). Loop `i` from `n-1` **down** to `0`, and `j` from `i+1` **up** to `n-1`. Filling by increasing range length also works, and it is the classic interval-DP order.

### 7. Can the state space be optimized?
Row `i` reads only row `i+1` plus the current row. Keep one array `row[j]` (holding row `i+1`'s values until they are overwritten) and save the diagonal `dp[i+1][j-1]` in a variable before overwriting `row[j-1]`. O(n) space.

## Recursive solution

```java
static int minInsert(String s, int i, int j) {
    if (i >= j) return 0;
    if (s.charAt(i) == s.charAt(j)) return minInsert(s, i + 1, j - 1);
    return 1 + Math.min(minInsert(s, i + 1, j), minInsert(s, i, j - 1));
}
```

## Memoization

Cache `(i, j)` in an `n × n` table filled with `-1`. See [`solution/Memoization.java`](solution/Memoization.java).

## Tabulation

```java
for (int i = n - 2; i >= 0; i--)
    for (int j = i + 1; j < n; j++)
        dp[i][j] = (s.charAt(i) == s.charAt(j)) ? dp[i + 1][j - 1]
                                                : 1 + Math.min(dp[i + 1][j], dp[i][j - 1]);
```
The diagonal (`i == j`) and the lower triangle stay 0, which covers both base cases. See [`solution/Tabulation.java`](solution/Tabulation.java).

## Space optimization

```java
int[] row = new int[n];                     // row[j] = dp[i+1][j] before being overwritten
for (int i = n - 2; i >= 0; i--) {
    int diagonal = 0;                       // dp[i+1][i] (empty range) = 0
    for (int j = i + 1; j < n; j++) {
        int below = row[j];                 // dp[i+1][j]
        row[j] = (s.charAt(i) == s.charAt(j)) ? diagonal : 1 + Math.min(below, row[j - 1]);
        diagonal = below;                   // becomes dp[i+1][j] = "dp[i+1][(j+1)-1]" for the next j
    }
}
return row[n - 1];
```
`row[i]` (the diagonal cell `dp[i][i]`) is never written and stays 0, which is correct. See [`solution/Optimized.java`](solution/Optimized.java).

## Dry run

`s = "mbadm"` (indices 0..4), filled by range length:

| length | range | ends | rule | dp |
|---|---|---|---|---|
| 2 | mb [0,1] | m≠b | 1 + min(b, m) = 1 + 0 | 1 |
| 2 | ba [1,2] | b≠a | 1 + 0 | 1 |
| 2 | ad [2,3] | a≠d | 1 + 0 | 1 |
| 2 | dm [3,4] | d≠m | 1 + 0 | 1 |
| 3 | mba [0,2] | m≠a | 1 + min(ba=1, mb=1) | 2 |
| 3 | bad [1,3] | b≠d | 1 + min(ad=1, ba=1) | 2 |
| 3 | adm [2,4] | a≠m | 1 + min(dm=1, ad=1) | 2 |
| 4 | mbad [0,3] | m≠d | 1 + min(bad=2, mba=2) | 3 |
| 4 | badm [1,4] | b≠m | 1 + min(adm=2, bad=2) | 3 |
| 5 | mbadm [0,4] | m==m | inside bad [1,3] | **2** |

```
          j:  0   1   2   3   4
              m   b   a   d   m
 i=0 (m)      0   1   2   3  [2]
 i=1 (b)          0   1   2   3
 i=2 (a)              0   1   2
 i=3 (d)                  0   1
 i=4 (m)                      0
```
Check: LPS(`"mbadm"`) = `"mam"` (or `"mbm"`, `"mdm"`) = 3, and 5 − 3 = 2. ✓

## Complexity

| Approach | Time | Space | Why |
|---|---|---|---|
| Recursion | O(2ⁿ) | O(n) | two branches per mismatch |
| Memoization | O(n²) | O(n²) | n²/2 ranges × O(1) |
| Tabulation | O(n²) | O(n²) | upper triangle |
| Optimized | O(n²) | O(n) | one row + a saved diagonal |

## Why the optimized approach is better

There are only n²/2 distinct ranges, so the DP turns exponential branching into about 125,000 cell updates for n = 500. The rolling row keeps memory linear.

## Common mistakes

1. **Using substring logic** (as in [647](../647-palindromic-substrings/)). Here characters may be "skipped" (given a partner), so this is subsequence-style.
2. **Looping `i` upward.** Row `i` needs row `i+1`, which must already be done.
3. **Counting `n − LPS` but computing the longest palindromic *substring*.** For `"mbadm"` the longest palindromic substring has length 1, which would wrongly give 4.
4. **Forgetting the empty range** (`i > j`) when two equal neighbours such as `"aa"` are removed.

## Key takeaway

> "Fewest characters to add or remove to make a palindrome" = `n − LPS`. Directly, it is a range DP: equal ends shrink both sides for free, and unequal ends cost 1 and shrink one side.

## Related problems

- Course topic: [Longest Palindromic Subsequence](../../../03-subsequence-and-knapsack-dp/13-longest-palindromic-subsequence/): the same table, maximizing kept characters.
- [583. Delete Operation for Two Strings](../583-delete-operation-for-two-strings/): the same "total − LCS-like quantity" idea across two strings.
- [5. Longest Palindromic Substring](../5-longest-palindromic-substring/): the contiguous version, a different recurrence.
- [664. Strange Printer](../../06-interval/664-strange-printer/): a harder range DP on one string.
