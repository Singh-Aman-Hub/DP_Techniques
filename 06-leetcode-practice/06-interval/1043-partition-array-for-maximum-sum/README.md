# 1043. Partition Array for Maximum Sum

> **Pattern:** Partition DP, choosing the last block (a "front partition" over a prefix) · **Difficulty:** Medium · **Builds on:** [Palindrome Partitioning](../../../04-advanced-dp-patterns/06-palindrome-partitioning/), [Interval DP](../../../04-advanced-dp-patterns/03-interval-dp/)

## Problem overview

Cut an array into contiguous blocks, each of length at most `k`. Then every element of a block is replaced by that block's maximum. Return the largest possible total sum of the resulting array.

## Link & metadata

| Field | Value |
|---|---|
| Problem | [1043. Partition Array for Maximum Sum](https://leetcode.com/problems/partition-array-for-maximum-sum/) |
| Difficulty | Medium |
| Pattern | Partition DP on a prefix (try every length for the last block) |
| Prerequisite topic | [04-advanced-dp-patterns/06-palindrome-partitioning](../../../04-advanced-dp-patterns/06-palindrome-partitioning/) |
| Also useful | [04-advanced-dp-patterns/03-interval-dp](../../../04-advanced-dp-patterns/03-interval-dp/), [03-subsequence-and-knapsack-dp/10-rod-cutting](../../../03-subsequence-and-knapsack-dp/10-rod-cutting/) |

## Examples

| arr | k | Answer | Best split |
|---|---|---|---|
| `[1,15,7,9,2,5,10]` | 3 | 84 | `[1,15,7] [9] [2,5,10]` → 15·3 + 9 + 10·3 |
| `[1,4,1,5,7,3,6,1,9,9,3]` | 4 | 83 | |
| `[5,1]` | 2 | 10 | one block → 5·2 |
| `[1]` | 1 | 1 | |

Running example: `arr = [1,15,7,9,2,5,10]`, `k = 3`.

## Why it is in the interval folder, and why it needs only one index

"Cut a sequence into contiguous pieces and score each piece" belongs to the **partition** family, a close cousin of interval DP. A general interval DP like [1039](../1039-minimum-score-triangulation-of-polygon/) needs **two** indices `[i..j]`, because the cost of a piece depends on how its inside is split again.

Here a block's value (`length × max`) depends **only on the block itself**, and blocks never interact. So we can fix the left edge at 0 and only ask "where does the **last** block start?". One index is enough. This is the "front partition" pattern from [Palindrome Partitioning](../../../04-advanced-dp-patterns/06-palindrome-partitioning/), which cuts a prefix after the last piece. Spotting when a range DP collapses to a prefix DP is a key skill.

## Brute-force intuition

Every gap between neighbours can be a cut or not, subject to the `≤ k` rule. That gives up to 2ⁿ⁻¹ splits. Many splits share the same prefix of blocks, and the best way to split a prefix does not depend on what comes after it, so we have overlapping subproblems with optimal substructure.

## Deriving the DP (7 questions)

### 1. What is changing?
How long a prefix has been split: `i` = the number of leading elements already handled.

### 2. What does the state represent?
> `dp[i]` = the maximum total sum obtainable from the first `i` elements `arr[0..i-1]`.

### 3. What are the choices?
The length `len` of the **last** block, ending at element `i-1`: `len = 1, 2, …, min(k, i)`.

### 4. What is the recurrence?
```
dp[i] = max over len in 1..min(k, i) of
          dp[i - len]  +  len * max(arr[i-len .. i-1])
```
`dp[i-len]` is the best for everything before the block. `len * max` is the block's value. Compute the running `max` while `len` grows, so each choice costs O(1).

### 5. What are the base cases?
`dp[0] = 0`: an empty prefix contributes nothing.

### 6. What is the order of computation?
`dp[i]` needs `dp[i-1] … dp[i-k]`, all smaller indices. Loop `i` from 1 to n. The answer is `dp[n]`.

### 7. Can the state space be optimized?
Only the last `k` values are read. A circular buffer of size `k + 1` (index `i % (k+1)`) gives O(k) space. That matters little in practice (n ≤ 500), but it is a good exercise in "how far back does the recurrence look?". See [`solution/Optimized.java`](solution/Optimized.java).

## Recursive solution

```java
// best sum for the prefix arr[0..i-1]
static int best(int[] arr, int k, int i) {
    if (i == 0) return 0;
    int result = 0, blockMax = 0;
    for (int len = 1; len <= Math.min(k, i); len++) {
        blockMax = Math.max(blockMax, arr[i - len]);
        result = Math.max(result, best(arr, k, i - len) + len * blockMax);
    }
    return result;
}
```

## Memoization

Cache `best(i)` in `memo[i]` (`-1` = unknown). See [`solution/Memoization.java`](solution/Memoization.java).

## Tabulation

```java
for (int i = 1; i <= n; i++) {
    int blockMax = 0;
    for (int len = 1; len <= Math.min(k, i); len++) {
        blockMax = Math.max(blockMax, arr[i - len]);
        dp[i] = Math.max(dp[i], dp[i - len] + len * blockMax);
    }
}
```
See [`solution/Tabulation.java`](solution/Tabulation.java).

## Space optimization

Replace `dp[x]` with `ring[x % (k + 1)]`. While `dp[i]` is being computed, the slot `i % (k+1)` holds the stale value `dp[i-k-1]`, which is never read. Compute the new value in a local variable and store it at the end.

## Dry run

`arr = [1,15,7,9,2,5,10]`, `k = 3`:

| i | last element | len=1 | len=2 | len=3 | dp[i] |
|---|---|---|---|---|---|
| 0 | | | | | 0 |
| 1 | 1 | dp0 + 1·1 = 1 | | | 1 |
| 2 | 15 | dp1 + 15 = 16 | dp0 + 2·15 = 30 | | 30 |
| 3 | 7 | dp2 + 7 = 37 | dp1 + 2·15 = 31 | dp0 + 3·15 = 45 | 45 |
| 4 | 9 | dp3 + 9 = 54 | dp2 + 2·9 = 48 | dp1 + 3·15 = 46 | 54 |
| 5 | 2 | dp4 + 2 = 56 | dp3 + 2·9 = 63 | dp2 + 3·9 = 57 | 63 |
| 6 | 5 | dp5 + 5 = 68 | dp4 + 2·5 = 64 | dp3 + 3·9 = 72 | 72 |
| 7 | 10 | dp6 + 10 = 82 | dp5 + 2·10 = 83 | dp4 + 3·10 = **84** | **84** |

Following the winning choices back: `dp7` used a block of length 3 (`[2,5,10]`), then `dp4` used length 1 (`[9]`), then `dp3` used length 3 (`[1,15,7]`).

## Complexity

| Approach | Time | Space | Why |
|---|---|---|---|
| Brute force | O(2ⁿ) | O(n) | every valid split (at most 2ⁿ⁻¹) is explored |
| Memoization | O(n·k) | O(n) | n states × k block lengths, running max makes each O(1) |
| Tabulation | O(n·k) | O(n) | same loops |
| Optimized | O(n·k) | O(k) | ring buffer of the last k+1 values |

## Why the optimized approach is better

The DP reuses the best value of each prefix instead of re-splitting it. The running maximum avoids an extra O(k) scan per block, so the total time is O(n·k) instead of O(n·k²).

## Common mistakes

1. **Forgetting the `len ≤ i` bound** and reading `arr[-1]`.
2. **Recomputing the block maximum from scratch** for each `len` (O(k²) per `i`).
3. **Using a full 2D interval table.** It works, but it is O(n³) and unnecessary, because blocks are independent.
4. **Updating the ring slot before all reads are done** in the O(k) version.

## Key takeaway

> If a partition's pieces are scored **independently**, you only need the prefix length: `dp[i] = best over the last piece`. You need `[i..j]` interval states only when a piece's cost depends on how its inside is split.

## Related problems

- [1039. Minimum Score Triangulation of Polygon](../1039-minimum-score-triangulation-of-polygon/): a true two-index interval DP.
- [1547. Minimum Cost to Cut a Stick](../1547-minimum-cost-to-cut-a-stick/): cutting where the cost **does** depend on the current piece, so two indices are needed.
- Course topic: [Palindrome Partitioning](../../../04-advanced-dp-patterns/06-palindrome-partitioning/)
