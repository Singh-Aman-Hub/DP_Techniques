# 354. Russian Doll Envelopes

> **Pattern:** Subsequences & strings, 2D LIS (sort one dimension, run LIS on the other in O(n log n)) · **Difficulty:** Hard · **Builds on:** [Longest Increasing Subsequence](../../../03-subsequence-and-knapsack-dp/11-longest-increasing-subsequence/)

## Problem overview

Each envelope has a width and a height. One envelope fits inside another only when it is **strictly smaller in both** width and height, and envelopes cannot be rotated. Return the largest number of envelopes you can nest one inside another, like Russian dolls.

## Link & metadata

| Field | Value |
|---|---|
| Problem | [354. Russian Doll Envelopes](https://leetcode.com/problems/russian-doll-envelopes/) |
| Difficulty | Hard |
| Pattern | Sort by width (ties: height **descending**), then a strict LIS on heights |
| Prerequisite topic | [03-subsequence-and-knapsack-dp/11-longest-increasing-subsequence](../../../03-subsequence-and-knapsack-dp/11-longest-increasing-subsequence/) |
| Also useful | [05-dp-optimization](../../../05-dp-optimization/) (replacing an O(n²) DP with binary search) |

## Examples

| envelopes `[w,h]` | Answer | A best nesting |
|---|---|---|
| `[[5,4],[6,4],[6,7],[2,3]]` | 3 | `[2,3] → [5,4] → [6,7]` |
| `[[4,5],[4,6],[6,7],[2,3],[1,1]]` | 4 | `[1,1] → [2,3] → [4,5] → [6,7]` |
| `[[1,2],[1,3],[1,4]]` | 1 | same width, so none fits in another |
| `[[1,1],[1,1],[1,1]]` | 1 | identical envelopes do not nest |
| `[[1,1]]` | 1 | one envelope |

Running example: `[[5,4],[6,4],[6,7],[2,3]]`.

## Brute-force intuition

A nesting is a chain where both coordinates strictly increase. That is an LIS, but in **two** dimensions. Checking every subset is O(2ⁿ). A direct O(n²) chain DP (as in [368](../368-largest-divisible-subset/)) works once we sort, but n can be 100,000, so n² ≈ 10¹⁰ is too slow. We need the O(n log n) LIS.

**Reduce 2D to 1D:** sort by width. Now any chain must move left to right in the sorted list, so the widths take care of themselves, and we only need the heights to strictly increase. That is a plain 1D LIS on the height sequence, **with one catch: ties in width**.

### The descending tie-break (the heart of the problem)

Envelopes with the **same width** can never nest. But if equal widths are sorted by height **ascending**, their heights look increasing, and the LIS on heights would chain them:
```
[[1,2],[1,3],[1,4]]  sorted ascending by (w, h)  -> heights 2, 3, 4  -> LIS = 3   WRONG (answer is 1)
                     sorted w asc, h DESC          -> heights 4, 3, 2  -> LIS = 1   correct
```
Sorting equal widths by height **descending** makes their heights a decreasing run. A strictly increasing subsequence can then pick **at most one** envelope from each width group. That is exactly the rule we need, with no extra check.

## Deriving the DP (7 questions)

### 1. What is changing?
After the sort, the position `i` in the list of envelopes (equivalently, in the height sequence `h[0..n-1]`).

### 2. What does the state represent?
O(n²) version:
> `chain[i]` = the most envelopes in a nesting whose **outermost** envelope is `i`.

O(n log n) version, the "tails" array from the LIS topic:
> `tails[k]` = the **smallest possible height** of the last envelope of a valid nesting with `k + 1` envelopes, among the envelopes processed so far.

### 3. What are the choices?
- O(n²): which envelope `j < i` sits directly inside `i` (requires `h[j] < h[i]`; widths are handled by the sort), or none.
- O(n log n): for each new height `x`, either **extend** the longest chain (if `x` is larger than every tail) or **replace** the first tail that is `≥ x`, giving that chain length a smaller (better) ending.

### 4. What is the recurrence?
```
chain[i] = 1 + max( chain[j] : j < i, h[j] < h[i] )      (1 if none)
answer   = max chain[i]
```
Tails update for each height `x`:
```
pos = first index with tails[pos] >= x        (lower bound: strict increase)
tails[pos] = x;   if pos == size: size++
answer = size
```
Why keep the **smallest** tail? A chain that ends lower can accept more future envelopes, so it is never worse to keep it.

### 5. What are the base cases?
`chain[i] = 1` (an envelope alone). `tails` starts empty (size 0).

### 6. What is the order of computation?
Process envelopes in the sorted order: width ascending, then height descending. `chain[i]` needs only earlier indices.

### 7. Can the state space be optimized?
The O(n²) table cannot be shrunk usefully. The real optimization is in **time**: the tails array is sorted, so each update is a binary search, giving O(n log n) with O(n) space. That is [`solution/Optimized.java`](solution/Optimized.java).

## Recursive solution

```java
// most envelopes in a nesting whose outermost envelope is sorted index i
static int chainEndingAt(int[] h, int i) {
    int best = 1;
    for (int j = 0; j < i; j++)
        if (h[j] < h[i]) best = Math.max(best, 1 + chainEndingAt(h, j));
    return best;
}
```

## Memoization

Cache `chainEndingAt(i)` in an array (0 = not computed) after sorting. This is O(n²), fine for small tests and for understanding. See [`solution/Memoization.java`](solution/Memoization.java).

## Tabulation

The bottom-up O(n²) LIS over sorted heights. See [`solution/Tabulation.java`](solution/Tabulation.java).

## Space optimization

Here the optimization is in **time**:
```java
int[] tails = new int[n];
int size = 0;
for (int[] env : sorted) {
    int x = env[1];
    int lo = 0, hi = size;                  // find first tails[pos] >= x
    while (lo < hi) { int mid = (lo + hi) / 2; if (tails[mid] < x) lo = mid + 1; else hi = mid; }
    tails[lo] = x;
    if (lo == size) size++;
}
return size;
```
`tails` holds the best ending heights, **not** an actual nesting. Its length is the answer.

## Dry run

Input `[[5,4],[6,4],[6,7],[2,3]]` → sorted by width ascending, height descending:
`[2,3], [5,4], [6,7], [6,4]` → heights `3, 4, 7, 4`.

Tails (O(n log n)):

| step | envelope | x | lower bound in tails | action | tails | size |
|---|---|---|---|---|---|---|
| 1 | [2,3] | 3 | pos 0 (empty) | append | [3] | 1 |
| 2 | [5,4] | 4 | pos 1 (end) | append | [3,4] | 2 |
| 3 | [6,7] | 7 | pos 2 (end) | append | [3,4,7] | 3 |
| 4 | [6,4] | 4 | pos 1 (tails[1]=4 ≥ 4) | replace | [3,4,7] | 3 |

Answer 3. In step 4, `[6,4]` could not extend `[6,7]`: they have the same width, and the descending order put `7` first, so `4` arrived too late to follow it.

O(n²) `chain` on the same heights `3, 4, 7, 4`: `chain = [1, 2, 3, 2]`, so the max is 3. ✓

## Complexity

| Approach | Time | Space | Why |
|---|---|---|---|
| Brute force | O(2ⁿ · n) | O(n) | all subsets |
| Memoization | O(n log n + n²) | O(n) | sort + n states × n predecessors |
| Tabulation | O(n log n + n²) | O(n) | same, bottom-up |
| Optimized | O(n log n) | O(n) | sort + one binary search per envelope |

With n = 10⁵, n² is about 10¹⁰ operations (far too slow), while n log n is about 1.7 × 10⁶.

## Why the optimized approach is better

The O(n²) DP compares every pair. The tails array summarizes all chains of each length by **one** number, the best possible ending height. Because tails stays sorted, one binary search decides where each new envelope goes.

## Common mistakes

1. **Sorting heights ascending on equal widths.** Same-width envelopes then chain (see the tie-break section).
2. **Using upper bound** (first `> x`) instead of lower bound (first `≥ x`). That allows equal heights to chain, but nesting must be **strict**.
3. **Treating `tails` as the actual nesting.** It is not. Only its length means anything.
4. **Running the O(n²) DP on the full limits.** It gets Time Limit Exceeded.

## Key takeaway

> 2D strictly-increasing chain → sort by the first key ascending and the **second key descending on ties**, then do a 1D strict LIS on the second key with binary search.

## Related problems

- [368. Largest Divisible Subset](../368-largest-divisible-subset/): sort, then O(n²) chain with reconstruction.
- [1048. Longest String Chain](../1048-longest-string-chain/): sort by length, then chain.
- [673. Number of Longest Increasing Subsequence](../673-number-of-longest-increasing-subsequence/): the O(n²) LIS that also counts.
- Course topic: [Longest Increasing Subsequence](../../../03-subsequence-and-knapsack-dp/11-longest-increasing-subsequence/)
