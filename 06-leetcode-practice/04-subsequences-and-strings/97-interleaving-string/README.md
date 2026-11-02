# 97. Interleaving String

> **Pattern:** Subsequences & strings, two-sequence boolean DP (a third string is indexed by `i + j`) · **Difficulty:** Medium · **Builds on:** [Longest Common Subsequence](../../../03-subsequence-and-knapsack-dp/12-longest-common-subsequence/), [DP on Strings](../../../04-advanced-dp-patterns/01-dp-on-strings/)

## Problem overview

You get three strings `s1`, `s2` and `s3`. Decide whether `s3` can be built by **merging** `s1` and `s2`: use every character of both exactly once, and keep each string's own characters in their original order. You may switch between the two strings as often as you like.

## Link & metadata

| Field | Value |
|---|---|
| Problem | [97. Interleaving String](https://leetcode.com/problems/interleaving-string/) |
| Difficulty | Medium |
| Pattern | 2D boolean DP over two prefixes (a grid-path view) |
| Prerequisite topic | [03-subsequence-and-knapsack-dp/12-longest-common-subsequence](../../../03-subsequence-and-knapsack-dp/12-longest-common-subsequence/) |
| Also useful | [04-advanced-dp-patterns/01-dp-on-strings](../../../04-advanced-dp-patterns/01-dp-on-strings/), [02-two-dimensional-and-grid-dp/02-unique-paths](../../../02-two-dimensional-and-grid-dp/02-unique-paths/) |

## Examples

| s1 | s2 | s3 | Answer | Why |
|---|---|---|---|---|
| `"ab"` | `"c"` | `"acb"` | true | `a`(s1) `c`(s2) `b`(s1) |
| `"aabcc"` | `"dbbca"` | `"aadbbcbcac"` | true | one valid merge exists |
| `"aabcc"` | `"dbbca"` | `"aadbbbaccc"` | false | no merge works |
| `""` | `""` | `""` | true | merging nothing gives nothing |

Running example: `s1 = "ab"`, `s2 = "c"`, `s3 = "acb"`.

## Brute-force intuition

Build `s3` from left to right. At each position, the next character came from `s1` or from `s2`. Try both whenever both match. In the worst case (for example, all `a`s) both always match, so there are about `2^(n+m)` paths.

**Key observation:** after using `i` characters of `s1` and `j` of `s2`, we have built exactly `i + j` characters of `s3`. The position in `s3` is **not** a separate state; it is fixed by `i` and `j`. So only `(n+1)·(m+1)` distinct situations exist, while the brute force explores exponentially many paths through them.

## Deriving the DP (7 questions)

### 1. What is changing?
How many characters of `s1` (`i`) and of `s2` (`j`) have been used. The position in `s3` is `i + j`.

### 2. What does the state represent?
> `dp[i][j]` = `true` when the first `i` characters of `s1` and the first `j` characters of `s2` can be interleaved to form exactly the first `i + j` characters of `s3`.

### 3. What are the choices?
Where the **last** character `s3[i+j-1]` came from:
- from `s1`, which requires `s1[i-1] == s3[i+j-1]`, or
- from `s2`, which requires `s2[j-1] == s3[i+j-1]`.

### 4. What is the recurrence?
```
dp[i][j] = ( i > 0 && dp[i-1][j] && s1[i-1] == s3[i+j-1] )      last char from s1
        || ( j > 0 && dp[i][j-1] && s2[j-1] == s3[i+j-1] )      last char from s2
```
We use `||` because **any** valid way is enough.

### 5. What are the base cases?
- `dp[0][0] = true`: the empty prefixes form the empty prefix.
- **Global precheck:** if `n + m != len(s3)`, return `false` immediately. Without it, the DP can say "true" for a prefix of `s3` and miss the leftover characters.

Row 0 and column 0 follow from the recurrence: `dp[0][j]` is true when `s2[0..j-1]` equals `s3[0..j-1]`, and the same holds for column 0 with `s1`.

### 6. What is the order of computation?
A cell needs the cell **above** (`i-1`) and the cell to the **left** (`j-1`), just like [Unique Paths](../../../02-two-dimensional-and-grid-dp/02-unique-paths/). In fact this is a grid-path problem: moving **down** consumes a character of `s1`, moving **right** consumes one of `s2`, and a move is allowed only when that character matches `s3`. Fill rows top to bottom, each left to right. Answer: `dp[n][m]`.

### 7. Can the state space be optimized?
Only the previous row is used (above) plus the current row (left). One boolean array of size `m + 1` is enough: `row[j]` still holds "above" before we overwrite it, and `row[j-1]` already holds "left". O(m) space.

## Recursive solution

```java
static boolean canForm(String a, String b, String c, int i, int j) {
    if (i == 0 && j == 0) return true;
    char target = c.charAt(i + j - 1);
    if (i > 0 && a.charAt(i - 1) == target && canForm(a, b, c, i - 1, j)) return true;
    return j > 0 && b.charAt(j - 1) == target && canForm(a, b, c, i, j - 1);
}
```
(Call it after the length precheck.)

## Memoization

Cache the result per `(i, j)` in a `Boolean[][]`. See [`solution/Memoization.java`](solution/Memoization.java).

## Tabulation

```java
dp[0][0] = true;
for (int i = 0; i <= n; i++)
    for (int j = 0; j <= m; j++) {
        if (i == 0 && j == 0) continue;
        char target = s3.charAt(i + j - 1);
        boolean fromS1 = i > 0 && dp[i - 1][j] && s1.charAt(i - 1) == target;
        boolean fromS2 = j > 0 && dp[i][j - 1] && s2.charAt(j - 1) == target;
        dp[i][j] = fromS1 || fromS2;
    }
```
See [`solution/Tabulation.java`](solution/Tabulation.java).

## Space optimization

```java
boolean[] row = new boolean[m + 1];
for (int i = 0; i <= n; i++)
    for (int j = 0; j <= m; j++) {
        if (i == 0 && j == 0) { row[0] = true; continue; }
        char target = s3.charAt(i + j - 1);
        boolean fromS1 = i > 0 && row[j] && s1.charAt(i - 1) == target;      // row[j] = above
        boolean fromS2 = j > 0 && row[j - 1] && s2.charAt(j - 1) == target;  // row[j-1] = left
        row[j] = fromS1 || fromS2;
    }
```
See [`solution/Optimized.java`](solution/Optimized.java).

## Dry run

`s1 = "ab"` (rows: moving down uses `s1`), `s2 = "c"` (columns: moving right uses `s2`), `s3 = "acb"`. Lengths 2 + 1 = 3 ✓.

```
               j=0        j=1 (c)
 i=0           T    ──c──> F       s3[0]='a' ≠ 'c'
               │a
 i=1 (a)       T    ──c──> T       s3[1]='c' == 'c', from the left
               │b          │b
 i=2 (b)       F           T  <- answer: s3[2]='b' == s1[1], from above
```

| (i, j) | s3 char | from s1 (above & match) | from s2 (left & match) | dp |
|---|---|---|---|---|
| (0,0) | — | base | | T |
| (0,1) | s3[0]=a | — | dp[0][0]=T, c≠a | F |
| (1,0) | s3[0]=a | dp[0][0]=T, a==a | — | T |
| (1,1) | s3[1]=c | dp[0][1]=F | dp[1][0]=T, c==c | T |
| (2,0) | s3[1]=c | dp[1][0]=T, b≠c | — | F |
| (2,1) | s3[2]=b | dp[1][1]=T, b==b | dp[2][0]=F | **T** |

The true path is `(0,0) →a (1,0) →c (1,1) →b (2,1)`, which reads `a c b`.

## Complexity

| Approach | Time | Space | Why |
|---|---|---|---|
| Recursion | O(2^(n+m)) | O(n+m) | two branches when both strings match |
| Memoization | O(n·m) | O(n·m) | (n+1)(m+1) states × O(1) |
| Tabulation | O(n·m) | O(n·m) | full grid |
| Optimized | O(n·m) | O(m) | one boolean row |

## Why the optimized approach is better

The recursion explores every path through the grid. The DP only asks, for each cell, whether it is reachable, which is O(1) per cell. Memory drops to one row because each cell looks only up and left.

## Common mistakes

1. **Skipping the length check.** `s1="a"`, `s2="b"`, `s3="a"` would wrongly look fine.
2. **Adding a third index for `s3`.** It is always `i + j`, so a 3D table wastes space and invites bugs.
3. **Greedy choice** ("take from `s1` whenever it matches"). With `s1="ab"`, `s2="ac"`, `s3="acab"`, greedy takes `s1`'s `a` first and then fails, but the answer is true.
4. **Off-by-one in `s3`.** The last character of the current prefix is `s3[i+j-1]`, not `s3[i+j]`.

## Key takeaway

> Two input strings plus one target string whose position equals `i + j` means a 2D table, not 3D. When the cell value is "can we reach here?", the DP is a grid-reachability problem with `||`.

## Related problems

- [1035. Uncrossed Lines](../1035-uncrossed-lines/): another two-prefix table.
- [583. Delete Operation for Two Strings](../583-delete-operation-for-two-strings/): two-prefix table with costs.
- Course topic: [Unique Paths](../../../02-two-dimensional-and-grid-dp/02-unique-paths/), the same up/left dependency on a grid.
