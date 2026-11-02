# 673. Number of Longest Increasing Subsequence

> **Pattern:** Subsequences & strings, LIS with a second "count" state · **Difficulty:** Medium · **Builds on:** [Longest Increasing Subsequence](../../../03-subsequence-and-knapsack-dp/11-longest-increasing-subsequence/)

## Problem overview

Given an integer array, find how many different **strictly increasing subsequences** have the maximum possible length. Two subsequences are different when they use different index sets, even if the values match.

You already know how to find the **length** of the LIS. The new skill is to carry a **count** alongside the length and update both together.

## Link & metadata

| Field | Value |
|---|---|
| Problem | [673. Number of Longest Increasing Subsequence](https://leetcode.com/problems/number-of-longest-increasing-subsequence/) |
| Difficulty | Medium |
| Pattern | LIS O(n²) DP + counting ways (two parallel arrays) |
| Prerequisite topic | [03-subsequence-and-knapsack-dp/11-longest-increasing-subsequence](../../../03-subsequence-and-knapsack-dp/11-longest-increasing-subsequence/) |

## Examples

| nums | Answer | Why |
|---|---|---|
| `[1,3,5,4,7]` | 2 | length 4: `1,3,5,7` and `1,3,4,7` |
| `[2,2,2,2,2]` | 5 | LIS length is 1, and any of the 5 single elements works |
| `[1,2,4,3,5,4,7,2]` | 3 | length 5: `1,2,4,5,7`, `1,2,3,5,7`, `1,2,3,4,7` |
| `[1]` | 1 | one element |

Running example: `nums = [1,3,5,4,7]`.

## Brute-force intuition

List all 2ⁿ subsequences, keep the strictly increasing ones, find the maximum length and count how many have it. For n = 2000 that is impossible.

The LIS lesson already gives the key structure: **every increasing subsequence ends somewhere**. If we know, for each ending index, both "how long can it be" and "in how many ways", we can extend to the next index without listing anything.

## Deriving the DP (7 questions)

### 1. What is changing?
The index `i` where the subsequence ends.

### 2. What does the state represent?
Two numbers per index:
> `length[i]` = the length of the longest increasing subsequence that **ends exactly at** `nums[i]`.
> `count[i]` = **how many** increasing subsequences end at `nums[i]` and have length `length[i]`.

### 3. What are the choices?
The element before `nums[i]` in the subsequence: any `j < i` with `nums[j] < nums[i]`, or nothing at all (`nums[i]` starts a new subsequence).

### 4. What is the recurrence?
Start with `length[i] = 1`, `count[i] = 1` (just `nums[i]` by itself). Then for each valid `j`:
```
if length[j] + 1 >  length[i]:   length[i] = length[j] + 1;  count[i] = count[j]   // new best: replace
if length[j] + 1 == length[i]:   count[i] += count[j]                              // tie: add the ways
```
Why replace in the first case? All the earlier ways counted in `count[i]` gave a **shorter** length, so they no longer count as "longest ending at `i`".

Final answer:
```
L = max(length[i]);   answer = sum of count[i] over all i with length[i] == L
```

### 5. What are the base cases?
Each element alone: `length[i] = 1`, `count[i] = 1`.

### 6. What is the order of computation?
`i` reads every `j < i`, so loop `i` from left to right, with an inner loop over `j` from 0 to `i-1`.

### 7. Can the state space be optimized?
Not in a useful way. Index `i` may read **any** earlier index, not just the last few, so we must keep all `n` pairs (O(n) space, which is already small). Getting the **time** down to O(n log n) needs a Fenwick tree or segment tree over values that stores (best length, count) pairs. That belongs to [05-dp-optimization](../../../05-dp-optimization/), so there is no `Optimized.java` here.

## Recursive solution

```java
// returns {length, count} for subsequences ending at index i
static int[] endingAt(int[] nums, int i) {
    int bestLength = 1, ways = 1;
    for (int j = 0; j < i; j++) {
        if (nums[j] >= nums[i]) continue;
        int[] prev = endingAt(nums, j);
        if (prev[0] + 1 > bestLength) { bestLength = prev[0] + 1; ways = prev[1]; }
        else if (prev[0] + 1 == bestLength) ways += prev[1];
    }
    return new int[]{bestLength, ways};
}
```
Without caching, `endingAt(j)` is recomputed for every larger `i`, which is exponential.

## Memoization

Cache `length[i]` and `count[i]` after the first computation (`length[i] == 0` means not computed). See [`solution/Memoization.java`](solution/Memoization.java).

## Tabulation

```java
for (int i = 0; i < n; i++) {
    length[i] = 1; count[i] = 1;
    for (int j = 0; j < i; j++) {
        if (nums[j] >= nums[i]) continue;
        if (length[j] + 1 > length[i])      { length[i] = length[j] + 1; count[i] = count[j]; }
        else if (length[j] + 1 == length[i]) { count[i] += count[j]; }
    }
}
```
See [`solution/Tabulation.java`](solution/Tabulation.java).

## Space optimization

Skipped, as question 7 explains. Every earlier index may be needed, so the two O(n) arrays are the minimum for this approach.

## Dry run

`nums = [1,3,5,4,7]`:

| i | nums[i] | valid j (nums[j] < nums[i]) | updates | length[i] | count[i] |
|---|---|---|---|---|---|
| 0 | 1 | none | start | 1 | 1 |
| 1 | 3 | j=0 (len 1) | 1+1=2 > 1 → replace | 2 | 1 |
| 2 | 5 | j=0 (len 1), j=1 (len 2) | j=0 → 2, cnt 1; j=1 → 3 > 2, replace | 3 | 1 |
| 3 | 4 | j=0, j=1 (5 is not < 4) | j=0 → 2; j=1 → 3, replace | 3 | 1 |
| 4 | 7 | j=0,1,2,3 | j=0 → 2; j=1 → 3; j=2 → 4 replace (cnt 1); **j=3 → 4 tie: cnt += 1** | 4 | 2 |

Max length L = 4. Only index 4 has length 4, so the answer is `count[4] = 2`. ✓

`[2,2,2,2,2]`: no `j` is strictly smaller, so every index has length 1 and count 1. L = 1 and the sum is 5. ✓

## Complexity

| Approach | Time | Space | Why |
|---|---|---|---|
| Brute force | O(2ⁿ · n) | O(n) | every subset, each checked in O(n) |
| Memoization | O(n²) | O(n) | n states, each scans up to n predecessors once |
| Tabulation | O(n²) | O(n) | double loop |
| Fenwick tree (not shown) | O(n log n) | O(n) | see the optimization module |

The counts fit in an `int`: the problem guarantees the answer fits in a 32-bit integer.

## Why the optimized approach is better

The DP replaces "list every subsequence" with "summarize all subsequences ending at `i` by two numbers". That works because extending a subsequence depends only on its **last value** and its **length**, not on the elements before them.

## Common mistakes

1. **Adding on a strict improvement.** When a strictly longer length appears, **replace** the count; do not add to it.
2. **Returning `count` at the last index**, or only one count. You must sum over **every** index that reaches the global maximum length.
3. **Using `<=`** for the "increasing" test. The problem asks for strictly increasing.
4. **Trying the n log n "tails" trick.** The tails array finds the length but loses the counts.

## Key takeaway

> To **count** optimal solutions, keep `(best value, number of ways)` per state. A strictly better candidate **replaces** both, and an equal candidate **adds** its ways.

## Related problems

- [368. Largest Divisible Subset](../368-largest-divisible-subset/): the LIS shape with "divides" as the link.
- [1048. Longest String Chain](../1048-longest-string-chain/): the LIS shape on words.
- [354. Russian Doll Envelopes](../354-russian-doll-envelopes/): LIS in 2D with the n log n method.
- Course topic: [Longest Increasing Subsequence](../../../03-subsequence-and-knapsack-dp/11-longest-increasing-subsequence/)
