# 1035. Uncrossed Lines

> **Pattern:** Subsequences & strings, LCS in disguise · **Difficulty:** Medium · **Builds on:** [Longest Common Subsequence](../../../03-subsequence-and-knapsack-dp/12-longest-common-subsequence/)

## Problem overview

Two integer arrays are written on two horizontal lines, one above the other. You may draw a straight segment between `nums1[i]` and `nums2[j]` when the two values are equal. Segments may not cross, and each number can be an endpoint of at most one segment. Return the largest number of segments you can draw.

The story is about geometry, but underneath it is exactly the **Longest Common Subsequence (LCS)** of the two arrays. The main lesson is learning to see that.

## Link & metadata

| Field | Value |
|---|---|
| Problem | [1035. Uncrossed Lines](https://leetcode.com/problems/uncrossed-lines/) |
| Difficulty | Medium |
| Pattern | Two-sequence DP (LCS) |
| Prerequisite topic | [03-subsequence-and-knapsack-dp/12-longest-common-subsequence](../../../03-subsequence-and-knapsack-dp/12-longest-common-subsequence/) |

## Examples

| nums1 | nums2 | Answer | Why |
|---|---|---|---|
| `[1,4,2]` | `[1,2,4]` | 2 | connect 1–1 and either 4–4 or 2–2; you cannot have both, they would cross |
| `[2,5,1,2,5]` | `[10,5,2,1,5,2]` | 3 | e.g. 5–5, 1–1, 5–5 (the common subsequence `[5,1,5]`) |
| `[1]` | `[2]` | 0 | nothing to connect |

Running example: `nums1 = [1,4,2]`, `nums2 = [1,2,4]`.

## Why this is LCS (the disguise)

```
 nums1:   1     4     2
          |      \   /         4-4 and 2-2 would cross:
 nums2:   1     2  X  4        the line from top-index 1 goes to bottom-index 2,
                               the line from top-index 2 goes to bottom-index 1
```

Two segments `(i1, j1)` and `(i2, j2)` with `i1 < i2` do **not cross** exactly when `j1 < j2`. So a set of non-crossing segments pairs up positions that increase in **both** arrays at the same time, and every pair has equal values. That is the definition of a common subsequence. More segments = a longer common subsequence. Maximum segments = LCS length.

Once you see "match equal items in two sequences while keeping order", think LCS.

## Brute-force intuition

Look at the last element of each array:
- If they are equal, connecting them never hurts: no later segment exists to cross it.
- If not, at least one of them is unused, so drop the last of `nums1` **or** the last of `nums2` and try both.

The "try both" branch doubles the work at each level, giving about `O(2^(n+m))` calls. The same `(i, j)` pairs show up over and over.

## Deriving the DP (7 questions)

### 1. What is changing?
The prefix lengths still being considered: `i` elements of `nums1`, `j` elements of `nums2`.

### 2. What does the state represent?
> `dp[i][j]` = the maximum number of non-crossing segments using only the first `i` numbers of `nums1` and the first `j` numbers of `nums2`.

### 3. What are the choices?
- If `nums1[i-1] == nums2[j-1]`: connect them (one segment) and solve the smaller prefixes.
- Otherwise: leave `nums1[i-1]` unused, **or** leave `nums2[j-1]` unused.

### 4. What is the recurrence?
```
dp[i][j] = dp[i-1][j-1] + 1                    if nums1[i-1] == nums2[j-1]
dp[i][j] = max(dp[i-1][j], dp[i][j-1])         otherwise
```
`dp[i-1][j-1] + 1`: the new segment plus the best for what lies to its left. Each `max` term means "ignore the last number on one side".

Why is connecting equal last elements always safe? Any optimal answer can be changed to use that segment without losing anything: whatever segment touched either endpoint can be swapped for this one.

### 5. What are the base cases?
`dp[0][j] = dp[i][0] = 0`. With an empty array there is nothing to connect.

### 6. What is the order of computation?
`dp[i][j]` needs the cell above, the cell to the left and the up-left diagonal. Fill rows top to bottom, each row left to right. The answer is `dp[n][m]`.

### 7. Can the state space be optimized?
Only the previous row is read. Keep one 1D array plus one variable `diagonal` that remembers `dp[i-1][j-1]` before it is overwritten. O(m) space.

## Recursive solution

```java
static int lines(int[] a, int[] b, int i, int j) {
    if (i == 0 || j == 0) return 0;
    if (a[i - 1] == b[j - 1]) return 1 + lines(a, b, i - 1, j - 1);
    return Math.max(lines(a, b, i - 1, j), lines(a, b, i, j - 1));
}
```

## Memoization

There are only `(n+1)·(m+1)` distinct `(i, j)` pairs. Store each result in `memo[i][j]` (`-1` = unknown). See [`solution/Memoization.java`](solution/Memoization.java).

## Tabulation

```java
for (int i = 1; i <= n; i++)
    for (int j = 1; j <= m; j++)
        dp[i][j] = (a[i-1] == b[j-1]) ? dp[i-1][j-1] + 1
                                      : Math.max(dp[i-1][j], dp[i][j-1]);
```
See [`solution/Tabulation.java`](solution/Tabulation.java).

## Space optimization

```java
int[] row = new int[m + 1];                 // row[j] = dp[previous i][j] until overwritten
for (int i = 1; i <= n; i++) {
    int diagonal = 0;                       // dp[i-1][0]
    for (int j = 1; j <= m; j++) {
        int above = row[j];                 // dp[i-1][j], save before overwriting
        row[j] = (a[i-1] == b[j-1]) ? diagonal + 1 : Math.max(above, row[j - 1]);
        diagonal = above;                   // becomes dp[i-1][j-1] for the next j
    }
}
```
See [`solution/Optimized.java`](solution/Optimized.java).

## Dry run

`nums1 = [1,4,2]` (rows), `nums2 = [1,2,4]` (columns):

```
            j:  0   1   2   3
                    1   2   4
 i=0            0   0   0   0
 i=1  (1)       0  [1]  1   1
 i=2  (4)       0   1   1  [2]
 i=3  (2)       0   1  [2]  2   <- answer dp[3][3] = 2
```

| (i, j) | values | rule | dp |
|---|---|---|---|
| (1,1) | 1 vs 1 | diag 0 + 1 | 1 |
| (1,2) | 1 vs 2 | max(up 0, left 1) | 1 |
| (1,3) | 1 vs 4 | max(0, 1) | 1 |
| (2,1) | 4 vs 1 | max(up 1, left 0) | 1 |
| (2,2) | 4 vs 2 | max(1, 1) | 1 |
| (2,3) | 4 vs 4 | diag dp[1][2]=1, +1 | 2 |
| (3,1) | 2 vs 1 | max(1, 0) | 1 |
| (3,2) | 2 vs 2 | diag dp[2][1]=1, +1 | 2 |
| (3,3) | 2 vs 4 | max(up 2, left 2) | 2 |

## Complexity

| Approach | Time | Space | Why |
|---|---|---|---|
| Recursion | O(2^(n+m)) | O(n+m) | two branches per mismatch, depth n+m |
| Memoization | O(n·m) | O(n·m) | each of n·m states solved once, O(1) work |
| Tabulation | O(n·m) | O(n·m) | nested loops over the table |
| Optimized | O(n·m) | O(m) | one row + one saved diagonal |

## Why the optimized approach is better

The recursion recomputes the same prefix pair exponentially often. The DP solves each pair once (500 × 500 = 250,000 cells at the limits). The 1D version gives the same answer with one row of memory.

## Common mistakes

1. **Trying to simulate geometry** (checking every pair of segments for crossings). The ordering argument replaces all of it.
2. **Using the substring recurrence** (`else 0`) from [718](../718-maximum-length-of-repeated-subarray/). Here gaps are allowed.
3. **Losing the diagonal in the 1D version.** Save `row[j]` in a temporary before overwriting it.

## Key takeaway

> "Match equal items across two sequences without crossing / while preserving order" **is** LCS. Rename the story, keep the recurrence.

## Related problems

- [718. Maximum Length of Repeated Subarray](../718-maximum-length-of-repeated-subarray/): contiguous version, with the opposite mismatch rule.
- [583. Delete Operation for Two Strings](../583-delete-operation-for-two-strings/): LCS again, read as deletions.
- [712. Minimum ASCII Delete Sum for Two Strings](../712-minimum-ascii-delete-sum-for-two-strings/): weighted LCS.
- Course topic: [Longest Common Subsequence](../../../03-subsequence-and-knapsack-dp/12-longest-common-subsequence/)
