# 583. Delete Operation for Two Strings

> **Pattern:** Subsequences & strings, two-sequence DP (LCS / edit-distance family) · **Difficulty:** Medium · **Builds on:** [Longest Common Subsequence](../../../03-subsequence-and-knapsack-dp/12-longest-common-subsequence/), [Edit Distance](../../../03-subsequence-and-knapsack-dp/14-edit-distance/)

## Problem overview

You get two strings. In one step you may delete one character from either string. Return the fewest steps needed to make the two strings identical.

What survives the deletions must be a common subsequence of both strings. To delete as little as possible, keep as much as possible, which means keeping the **longest** common subsequence.

## Link & metadata

| Field | Value |
|---|---|
| Problem | [583. Delete Operation for Two Strings](https://leetcode.com/problems/delete-operation-for-two-strings/) |
| Difficulty | Medium |
| Pattern | Two-sequence DP: LCS, or edit distance restricted to deletions |
| Prerequisite topic | [03-subsequence-and-knapsack-dp/12-longest-common-subsequence](../../../03-subsequence-and-knapsack-dp/12-longest-common-subsequence/) |
| Also useful | [03-subsequence-and-knapsack-dp/14-edit-distance](../../../03-subsequence-and-knapsack-dp/14-edit-distance/) |

## Examples

| word1 | word2 | Answer | Why |
|---|---|---|---|
| `"sea"` | `"eat"` | 2 | delete `s` from the first and `t` from the second, leaving `"ea"` |
| `"leetcode"` | `"etco"` | 4 | keep `"etco"`, delete the other 4 letters of `"leetcode"` |
| `"a"` | `"b"` | 2 | nothing in common, so delete both |

Running example: `word1 = "sea"`, `word2 = "eat"`.

## Brute-force intuition

Compare the last characters:
- If they are equal, keep both, since deleting a matching pair can never help.
- Otherwise one of them must go: delete from `word1` **or** from `word2`, and try both.

Every mismatch splits into two branches, so the plain recursion is exponential. It also reaches the same pair of prefixes along many different paths.

There are two ways to see the answer. Both give the same number:
```
(a) answer = n + m - 2 * LCS(word1, word2)     keep the LCS, delete everything else
(b) a direct DP counting deletions             edit distance with only "delete" allowed
```
The code teaches (b), because it generalizes to [712](../712-minimum-ascii-delete-sum-for-two-strings/), where each deletion has a different cost.

## Deriving the DP (7 questions)

### 1. What is changing?
The prefix lengths `i` (of `word1`) and `j` (of `word2`) still being considered.

### 2. What does the state represent?
> `dp[i][j]` = the minimum number of deletions that make `word1[0..i-1]` and `word2[0..j-1]` equal.

### 3. What are the choices?
- Last characters equal: keep both, with no cost.
- Different: delete `word1[i-1]` (cost 1) **or** delete `word2[j-1]` (cost 1).

### 4. What is the recurrence?
```
dp[i][j] = dp[i-1][j-1]                          if word1[i-1] == word2[j-1]
dp[i][j] = 1 + min(dp[i-1][j], dp[i][j-1])       otherwise
```
`dp[i-1][j]` is "the first string lost its last character", and `dp[i][j-1]` is the same for the second.

### 5. What are the base cases?
- `dp[i][0] = i`: to match an empty string, delete all `i` characters.
- `dp[0][j] = j`, for the same reason.

These are **not** zeros as in LCS. This is the most common bug here.

### 6. What is the order of computation?
A cell needs up, left and up-left. Fill row by row, left to right. Answer: `dp[n][m]`.

### 7. Can the state space be optimized?
Only the previous row is used, so keep one 1D array plus a saved diagonal. Remember that `row[0]` must be set to `i` at the start of each row. O(m) space.

## Recursive solution

```java
static int minDel(String a, String b, int i, int j) {
    if (i == 0) return j;
    if (j == 0) return i;
    if (a.charAt(i - 1) == b.charAt(j - 1)) return minDel(a, b, i - 1, j - 1);
    return 1 + Math.min(minDel(a, b, i - 1, j), minDel(a, b, i, j - 1));
}
```

## Memoization

Cache by `(i, j)` in an `(n+1) x (m+1)` table filled with `-1`. See [`solution/Memoization.java`](solution/Memoization.java).

## Tabulation

Initialize row 0 and column 0 with `j` and `i`, then apply the recurrence. See [`solution/Tabulation.java`](solution/Tabulation.java).

## Space optimization

```java
int[] row = new int[m + 1];
for (int j = 0; j <= m; j++) row[j] = j;          // dp[0][j]
for (int i = 1; i <= n; i++) {
    int diagonal = row[0];                        // dp[i-1][0]
    row[0] = i;                                   // dp[i][0]
    for (int j = 1; j <= m; j++) {
        int above = row[j];
        row[j] = (a[i-1] == b[j-1]) ? diagonal : 1 + Math.min(above, row[j - 1]);
        diagonal = above;
    }
}
```
See [`solution/Optimized.java`](solution/Optimized.java).

## Dry run

`word1 = "sea"` (rows), `word2 = "eat"` (columns):

```
              j:  0   1   2   3
                      e   a   t
 i=0              0   1   2   3
 i=1  (s)         1   2   3   4
 i=2  (e)         2  [1]  2   3
 i=3  (a)         3   2  [1]  2   <- answer
```

| (i, j) | chars | rule | value |
|---|---|---|---|
| (1,1) | s vs e | 1 + min(up 1, left 1) | 2 |
| (1,2) | s vs a | 1 + min(2, 2) | 3 |
| (1,3) | s vs t | 1 + min(3, 3) | 4 |
| (2,1) | e vs e | diagonal dp[1][0] | 1 |
| (2,2) | e vs a | 1 + min(up 3, left 1) | 2 |
| (2,3) | e vs t | 1 + min(up 4, left 2) | 3 |
| (3,1) | a vs e | 1 + min(up 1, left 3) | 2 |
| (3,2) | a vs a | diagonal dp[2][1] | 1 |
| (3,3) | a vs t | 1 + min(up 3, left 1) | **2** |

Check with the formula: LCS(`"sea"`, `"eat"`) = `"ea"` = 2, and 3 + 3 − 2·2 = 2. ✓

## Complexity

| Approach | Time | Space | Why |
|---|---|---|---|
| Recursion | O(2^(n+m)) | O(n+m) | two branches per mismatch |
| Memoization | O(n·m) | O(n·m) | n·m states, O(1) each |
| Tabulation | O(n·m) | O(n·m) | full table |
| Optimized | O(n·m) | O(m) | one row |

## Why the optimized approach is better

Every pair of prefixes is solved exactly once instead of exponentially often. The 1D version keeps the O(n·m) time and needs only O(m) memory.

## Common mistakes

1. **Zero base cases.** `dp[i][0]` is `i`, not 0, because those characters still have to be deleted.
2. **Adding a "replace" option.** That is [Edit Distance](../../../03-subsequence-and-knapsack-dp/14-edit-distance/). Here only deletions are allowed.
3. **Forgetting `row[0] = i`** in the 1D version.
4. **Using `n + m - LCS`** instead of `n + m - 2·LCS`. The LCS characters are kept in **both** strings.

## Key takeaway

> "Make two strings equal with deletions only" = keep the LCS. The direct DP is the LCS table with costs instead of lengths, and its base cases are `i` and `j`, not zeros.

## Related problems

- [712. Minimum ASCII Delete Sum for Two Strings](../712-minimum-ascii-delete-sum-for-two-strings/): the same DP where each deletion costs the character's ASCII value.
- [1035. Uncrossed Lines](../1035-uncrossed-lines/): plain LCS in disguise.
- [1312. Minimum Insertion Steps to Make a String Palindrome](../1312-minimum-insertion-steps-to-make-a-string-palindrome/): the "n − LCS-like quantity" idea on a single string.
- Course topic: [Edit Distance](../../../03-subsequence-and-knapsack-dp/14-edit-distance/)
