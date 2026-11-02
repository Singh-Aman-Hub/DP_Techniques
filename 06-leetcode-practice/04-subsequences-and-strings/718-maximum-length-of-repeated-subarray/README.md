# 718. Maximum Length of Repeated Subarray

> **Pattern:** Subsequences & strings, two-sequence DP (substring variant of LCS) · **Difficulty:** Medium · **Builds on:** [Longest Common Subsequence](../../../03-subsequence-and-knapsack-dp/12-longest-common-subsequence/)

## Problem overview

You get two integer arrays. Find the longest block of numbers that shows up in both arrays as a **contiguous** run, with the same values in the same order and no gaps. Return the length of that block. If the arrays share no value, the answer is 0.

This is the "longest common **substring**" problem written with arrays. It looks like LCS, but one word changes the recurrence: *contiguous*.

## Link & metadata

| Field | Value |
|---|---|
| Problem | [718. Maximum Length of Repeated Subarray](https://leetcode.com/problems/maximum-length-of-repeated-subarray/) |
| Difficulty | Medium |
| Pattern | Two-sequence DP, "ending exactly here" state |
| Prerequisite topic | [03-subsequence-and-knapsack-dp/12-longest-common-subsequence](../../../03-subsequence-and-knapsack-dp/12-longest-common-subsequence/) |
| Also useful | [04-advanced-dp-patterns/01-dp-on-strings](../../../04-advanced-dp-patterns/01-dp-on-strings/) |

## Examples

| nums1 | nums2 | Answer | Why |
|---|---|---|---|
| `[1,2,3,2,1]` | `[3,2,1,4,7]` | 3 | `[3,2,1]` appears in both as a contiguous block |
| `[0,0,0,0,0]` | `[0,0,0,0,0]` | 5 | the whole array matches |
| `[1,2]` | `[3,4]` | 0 | no value is shared |

We use `nums1 = [1,2,3,2,1]`, `nums2 = [3,2,1,4,7]` as the running example everywhere below.

## Brute-force intuition

Try every pair of starting positions `(i, j)`, one in each array. From there, walk forward while `nums1[i+k] == nums2[j+k]` and record the longest walk.

```
start (2,0): 3==3, 2==2, 1==1, then end of nums1  -> length 3
start (0,2): 1==1, 2!=4                           -> length 1
```

That costs `O(n · m · min(n, m))`. The waste: the walk from `(2,0)` re-checks `2==2` and `1==1`, and the walk from `(3,1)` checks the same pairs again. Overlapping work is the signal for DP.

## Deriving the DP (7 questions)

### 1. What is changing?
Two positions: how far we are in `nums1` (`i`) and how far we are in `nums2` (`j`). This is the same pair of indices as LCS.

### 2. What does the state represent?
This is the key step and it **differs from LCS**:

> `dp[i][j]` = the length of the longest common block that **ends exactly** at `nums1[i-1]` and `nums2[j-1]`.

(We use 1-based `i, j` so that row 0 and column 0 can mean "empty prefix".)

Why "ends exactly here"? A contiguous block cannot skip anything. If we let the state mean "best anywhere in the prefixes" (as LCS does), we could not tell whether the best block touches the current end, so we could not safely extend it. Pinning the end makes extension a simple `+1`.

### 3. What are the choices?
There is really one question: are `nums1[i-1]` and `nums2[j-1]` equal?
- **Equal:** the block ending one step earlier in both arrays grows by one.
- **Not equal:** no common block can end here, so the length is 0.

Unlike LCS, there is **no "skip one side" choice**. Skipping would create a gap, which a subarray does not allow.

### 4. What is the recurrence?
```
dp[i][j] = dp[i-1][j-1] + 1     if nums1[i-1] == nums2[j-1]
dp[i][j] = 0                    otherwise

answer   = max over all i, j of dp[i][j]
```
The answer is a **max over the whole table**, not `dp[n][m]`, because the best block can end anywhere.

Compare with LCS:
```
LCS:      equal -> dp[i-1][j-1] + 1      different -> max(dp[i-1][j], dp[i][j-1])
Subarray: equal -> dp[i-1][j-1] + 1      different -> 0
```

### 5. What are the base cases?
`dp[0][j] = dp[i][0] = 0`. An empty prefix has no last element, so no block ends there.

### 6. What is the order of computation?
`dp[i][j]` needs only the diagonal `dp[i-1][j-1]`. Any order that finishes row `i-1` before row `i` works, so plain `i` ascending, `j` ascending loops are enough.

### 7. Can the state space be optimized?
Row `i` reads only row `i-1`, one column to the left. Keep **one 1D array** and loop `j` from **right to left**, so `cur[j-1]` still holds the previous row's value when we read it. O(m) space.

## Recursive solution

Define `f(i, j)` = common block length ending at `nums1[i-1]` and `nums2[j-1]`:

```java
static int endingAt(int[] a, int[] b, int i, int j) {
    if (i == 0 || j == 0) return 0;             // empty prefix
    if (a[i - 1] != b[j - 1]) return 0;        // block broken here
    return 1 + endingAt(a, b, i - 1, j - 1);   // extend diagonally
}
// answer = max of endingAt(i, j) over all i, j
```

Calling this from every `(i, j)` repeats the same diagonal walks again and again. See `solution/BruteForce.java` for the start-and-walk version.

## Memoization

Cache `f(i, j)` in `memo[i][j]` (`-1` = not computed). Each diagonal chain is walked once. Then take the max over all cells. See [`solution/Memoization.java`](solution/Memoization.java).

The recursion depth is at most `min(n, m)`, because each call moves one step down a diagonal.

## Tabulation

Fill `dp[(n+1)][(m+1)]` row by row and keep a running maximum. See [`solution/Tabulation.java`](solution/Tabulation.java).

```java
for (int i = 1; i <= n; i++)
    for (int j = 1; j <= m; j++)
        if (a[i - 1] == b[j - 1]) {
            dp[i][j] = dp[i - 1][j - 1] + 1;
            best = Math.max(best, dp[i][j]);
        }   // else stays 0
```

## Space optimization

```java
int[] cur = new int[m + 1];
for (int i = 1; i <= n; i++)
    for (int j = m; j >= 1; j--)               // right to left!
        cur[j] = (a[i - 1] == b[j - 1]) ? cur[j - 1] + 1 : 0;
```

Going right to left means `cur[j-1]` has not yet been overwritten in this row, so it still holds `dp[i-1][j-1]`. The `: 0` reset is required. In LCS you never write 0 into a cell, but here a stale value from the previous row would be wrong. See [`solution/Optimized.java`](solution/Optimized.java).

## Dry run

`nums1 = [1,2,3,2,1]` (rows), `nums2 = [3,2,1,4,7]` (columns). A cell is non-zero only when the row value equals the column value, and then it equals the up-left diagonal + 1.

```
            j:  0   1   2   3   4   5
                    3   2   1   4   7
 i=0           0   0   0   0   0   0
 i=1  (1)      0   0   0  [1]  0   0
 i=2  (2)      0   0  [1]  0   0   0
 i=3  (3)      0  [1]  0   0   0   0
 i=4  (2)      0   0  [2]  0   0   0
 i=5  (1)      0   0   0  [3]  0   0
```

| Step | (i, j) | values | equal? | dp[i][j] | best |
|---|---|---|---|---|---|
| 1 | (1,3) | 1 vs 1 | yes | dp[0][2]+1 = 1 | 1 |
| 2 | (2,2) | 2 vs 2 | yes | dp[1][1]+1 = 1 | 1 |
| 3 | (3,1) | 3 vs 3 | yes | dp[2][0]+1 = 1 | 1 |
| 4 | (4,2) | 2 vs 2 | yes | dp[3][1]+1 = 2 | 2 |
| 5 | (5,3) | 1 vs 1 | yes | dp[4][2]+1 = 3 | **3** |

All other cells have unequal values and stay 0. The diagonal `(3,1) → (4,2) → (5,3)` is the block `[3,2,1]`.

## Complexity

| Approach | Time | Space | Why |
|---|---|---|---|
| Brute force | O(n·m·min(n,m)) | O(1) | n·m starting pairs, each walks up to min(n,m) steps |
| Memoization | O(n·m) | O(n·m) | n·m states, each computed once with O(1) work; memo table + recursion depth ≤ min(n,m) |
| Tabulation | O(n·m) | O(n·m) | two nested loops, O(1) per cell |
| Optimized | O(n·m) | O(m) | same loops, one row kept |

## Why the optimized approach is better

The brute force re-walks every diagonal from each of its starting points. The DP walks each diagonal once, because `dp[i][j]` reuses the length already computed at `dp[i-1][j-1]`. The 1D version keeps that speed and needs only O(m) memory, which matters at the upper limits (1000 × 1000).

## Common mistakes

1. **Copying the LCS "else max(...)" branch.** That gives the longest common *subsequence* (gaps allowed). For `[1,2,3,2,1]` / `[3,2,1,4,7]` it happens to also give 3, but for `[1,9,2]` / `[1,2]` LCS says 2 while the right answer is 1.
2. **Returning `dp[n][m]`.** The best block rarely ends at both last elements. Track a running max.
3. **Looping `j` left to right in the 1D version.** `cur[j-1]` is then already the *current* row's value, so the diagonal is read wrongly.
4. **Forgetting to reset to 0 in the 1D version.** The old row's value stays and blocks merge across a mismatch.

## Key takeaway

> **Substring / subarray** ⇒ the state must mean "ending **exactly** at i and j". On mismatch, reset to 0, and the answer is the max over all cells.
> **Subsequence** ⇒ the state means "best within the prefixes". On mismatch, take the max of skipping either side, and the answer is `dp[n][m]`.

## Related problems

- [1035. Uncrossed Lines](../1035-uncrossed-lines/): the same two arrays, but gaps allowed, which makes it plain LCS.
- [583. Delete Operation for Two Strings](../583-delete-operation-for-two-strings/): LCS used to count deletions.
- [5. Longest Palindromic Substring](../5-longest-palindromic-substring/): another "contiguous" problem where the state must pin both ends.
- Course topic: [Longest Common Subsequence](../../../03-subsequence-and-knapsack-dp/12-longest-common-subsequence/)
