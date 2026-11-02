# 712. Minimum ASCII Delete Sum for Two Strings

> **Pattern:** Subsequences & strings, weighted two-sequence DP · **Difficulty:** Medium · **Builds on:** [Longest Common Subsequence](../../../03-subsequence-and-knapsack-dp/12-longest-common-subsequence/), [Edit Distance](../../../03-subsequence-and-knapsack-dp/14-edit-distance/)

## Problem overview

You get two lowercase strings. Deleting a character costs its ASCII code (`'a'` = 97, `'b'` = 98, …). Delete characters from either string until the two are equal, and return the smallest total cost.

This is [583. Delete Operation for Two Strings](../583-delete-operation-for-two-strings/) with **weights**. In 583 every deletion costs 1. Here a deletion costs the character's value, so the cheapest result is no longer the longest common subsequence: we keep the common subsequence with the **largest ASCII sum**.

## Link & metadata

| Field | Value |
|---|---|
| Problem | [712. Minimum ASCII Delete Sum for Two Strings](https://leetcode.com/problems/minimum-ascii-delete-sum-for-two-strings/) |
| Difficulty | Medium |
| Pattern | Two-sequence DP with weighted costs |
| Prerequisite topic | [03-subsequence-and-knapsack-dp/12-longest-common-subsequence](../../../03-subsequence-and-knapsack-dp/12-longest-common-subsequence/) |
| Also useful | [03-subsequence-and-knapsack-dp/14-edit-distance](../../../03-subsequence-and-knapsack-dp/14-edit-distance/) |

## Examples

| s1 | s2 | Answer | Why |
|---|---|---|---|
| `"sea"` | `"eat"` | 231 | delete `s` (115) and `t` (116) |
| `"delete"` | `"leet"` | 403 | keep `"let"`; delete `d,e,e` (100+101+101) and `e` (101) |
| `"a"` | `"b"` | 195 | delete both: 97 + 98 |

Running example: `s1 = "sea"`, `s2 = "eat"`. ASCII values used: `s`=115, `e`=101, `a`=97, `t`=116.

Why not "keep the longest"? For `"delete"` / `"leet"`, both `"let"` and `"eet"` have length 3. `"eet"` = 101+101+116 = 318 and `"let"` = 108+101+116 = 325. Keeping `"let"` keeps more value, so we delete less. A length-only LCS cannot tell these apart.

## Brute-force intuition

As in 583, look at the last characters. If they are equal, keep both (free). Otherwise delete one of them, paying its ASCII value, and try both options. That is exponential with heavy overlap, so we use DP.

## Deriving the DP (7 questions)

### 1. What is changing?
The prefix lengths `i` (of `s1`) and `j` (of `s2`).

### 2. What does the state represent?
> `dp[i][j]` = the minimum total ASCII cost of deletions that make `s1[0..i-1]` and `s2[0..j-1]` equal.

### 3. What are the choices?
- Last characters equal: keep both, cost 0.
- Different: delete `s1[i-1]` (cost `s1[i-1]`) **or** delete `s2[j-1]` (cost `s2[j-1]`).

### 4. What is the recurrence?
```
dp[i][j] = dp[i-1][j-1]                                  if s1[i-1] == s2[j-1]
dp[i][j] = min( s1[i-1] + dp[i-1][j],                    delete from s1
                s2[j-1] + dp[i][j-1] )                   delete from s2
```
The only change from 583 is that the `1` becomes the character's value, and the value **differs per branch**, so it cannot be pulled out of the `min`.

### 5. What are the base cases?
- `dp[0][0] = 0`.
- `dp[i][0] = dp[i-1][0] + s1[i-1]`: delete the whole prefix of `s1` (a running ASCII sum).
- `dp[0][j] = dp[0][j-1] + s2[j-1]`, the same for `s2`.

### 6. What is the order of computation?
Each cell needs up, left and up-left. Fill row by row, left to right. Answer: `dp[n][m]`.

### 7. Can the state space be optimized?
Yes, one row plus a saved diagonal. Here `row[0]` at row `i` is the running sum of `s1[0..i-1]`, so update it at the start of every row. O(m) space.

## Recursive solution

```java
static int cost(String a, String b, int i, int j) {
    if (i == 0) { int s = 0; for (int k = 0; k < j; k++) s += b.charAt(k); return s; }
    if (j == 0) { int s = 0; for (int k = 0; k < i; k++) s += a.charAt(k); return s; }
    if (a.charAt(i - 1) == b.charAt(j - 1)) return cost(a, b, i - 1, j - 1);
    return Math.min(a.charAt(i - 1) + cost(a, b, i - 1, j),
                    b.charAt(j - 1) + cost(a, b, i, j - 1));
}
```

## Memoization

Cache `(i, j)` results. The base cases above are computed directly. See [`solution/Memoization.java`](solution/Memoization.java).

## Tabulation

Fill row 0 and column 0 with prefix ASCII sums, then apply the recurrence. See [`solution/Tabulation.java`](solution/Tabulation.java).

## Space optimization

```java
int[] row = new int[m + 1];
for (int j = 1; j <= m; j++) row[j] = row[j - 1] + b.charAt(j - 1);
for (int i = 1; i <= n; i++) {
    int diagonal = row[0];
    row[0] += a.charAt(i - 1);                    // dp[i][0]
    for (int j = 1; j <= m; j++) {
        int above = row[j];
        row[j] = (a.charAt(i-1) == b.charAt(j-1)) ? diagonal
               : Math.min(a.charAt(i-1) + above, b.charAt(j-1) + row[j - 1]);
        diagonal = above;
    }
}
```
See [`solution/Optimized.java`](solution/Optimized.java).

## Dry run

`s1 = "sea"` (rows), `s2 = "eat"` (columns):

```
               j:  0     1     2     3
                         e     a     t
 i=0               0   101   198   314
 i=1  (s=115)    115   216   313   429
 i=2  (e=101)    216  [115]  212   328
 i=3  (a=97)     313   212  [115]  231   <- answer
```

| (i, j) | chars | delete s1 char | delete s2 char | value |
|---|---|---|---|---|
| (1,1) | s/e | 115 + 101 = 216 | 101 + 115 = 216 | 216 |
| (1,2) | s/a | 115 + 198 = 313 | 97 + 216 = 313 | 313 |
| (1,3) | s/t | 115 + 314 = 429 | 116 + 313 = 429 | 429 |
| (2,1) | e/e | equal → dp[1][0] | | 115 |
| (2,2) | e/a | 101 + 313 = 414 | 97 + 115 = 212 | 212 |
| (2,3) | e/t | 101 + 429 = 530 | 116 + 212 = 328 | 328 |
| (3,1) | a/e | 97 + 115 = 212 | 101 + 313 = 414 | 212 |
| (3,2) | a/a | equal → dp[2][1] | | 115 |
| (3,3) | a/t | 97 + 328 = 425 | 116 + 115 = 231 | **231** |

231 = 115 (`s`) + 116 (`t`). ✓

## Complexity

| Approach | Time | Space | Why |
|---|---|---|---|
| Recursion | O(2^(n+m)) | O(n+m) | two branches per mismatch |
| Memoization | O(n·m) | O(n·m) | n·m states × O(1) |
| Tabulation | O(n·m) | O(n·m) | full table |
| Optimized | O(n·m) | O(m) | one row |

The largest possible answer is (1000 + 1000) × 122 = 244,000, which fits easily in an `int`.

## Why the optimized approach is better

Each prefix pair is solved once instead of exponentially often. The rolling row cuts memory from about a million cells to about a thousand.

## Common mistakes

1. **Computing the plain LCS and subtracting.** Length is the wrong measure; you need the maximum-*ASCII* common subsequence.
2. **Base cases set to 0 or to `i`.** They must be prefix **ASCII sums**.
3. **Writing `1 + min(...)` from 583.** The cost depends on which character you delete.

## Key takeaway

> When every deletion has its own price, put the price **inside** each branch of the `min`. The table shape and the fill order stay those of LCS.

## Related problems

- [583. Delete Operation for Two Strings](../583-delete-operation-for-two-strings/): the unit-cost version.
- [1035. Uncrossed Lines](../1035-uncrossed-lines/): plain LCS.
- Course topic: [Edit Distance](../../../03-subsequence-and-knapsack-dp/14-edit-distance/)
