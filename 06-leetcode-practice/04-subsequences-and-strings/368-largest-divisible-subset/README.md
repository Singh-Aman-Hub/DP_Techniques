# 368. Largest Divisible Subset

> **Pattern:** Subsequences & strings, LIS shape after sorting, plus reconstruction · **Difficulty:** Medium · **Builds on:** [Longest Increasing Subsequence](../../../03-subsequence-and-knapsack-dp/11-longest-increasing-subsequence/)

## Problem overview

You get a set of distinct positive integers. Return the largest subset in which, for **every** pair, one number divides the other. If several subsets have the maximum size, any one is accepted.

Two new ideas appear here: the order is not given (**we create it by sorting**), and we must return the **subset itself**, not just its size.

## Link & metadata

| Field | Value |
|---|---|
| Problem | [368. Largest Divisible Subset](https://leetcode.com/problems/largest-divisible-subset/) |
| Difficulty | Medium |
| Pattern | Sort, then an LIS-style chain DP with parent pointers |
| Prerequisite topic | [03-subsequence-and-knapsack-dp/11-longest-increasing-subsequence](../../../03-subsequence-and-knapsack-dp/11-longest-increasing-subsequence/) |

## Examples

| nums | Answer | Why |
|---|---|---|
| `[3,4,16,8]` | `[4,8,16]` | 4 \| 8 \| 16; 3 divides none of them |
| `[1,2,4,8]` | `[1,2,4,8]` | each element divides the next |
| `[5,9,18,54,108,540,90,180,360,720]` | `[9,18,90,180,360,720]` | the unique largest chain (size 6) |
| `[7]` | `[7]` | a single number |

(Tests use inputs with a unique answer, so every approach returns the same list.)

Running example: `nums = [3,4,16,8]`.

## Brute-force intuition

Try all 2ⁿ subsets, check every pair, and keep the largest valid one. That is O(2ⁿ · n²), hopeless for n = 1000.

**Key observation:** after sorting ascending, a set where every pair divides is just a **chain** `a₁ | a₂ | a₃ | …`. Divisibility is transitive: if `a | b` and `b | c`, then `a | c`. So we only need to check **neighbours** in sorted order:
```
sorted chain:   4  ->  8  ->  16
                4|8    8|16         ⇒ 4|16 comes for free
```
Now the problem has the LIS shape: "longest chain where each element is linked to the previous one", with "divides" in place of "is smaller than".

## Deriving the DP (7 questions)

### 1. What is changing?
After sorting, the index `i` of the **largest** (last) element of the chain.

### 2. What does the state represent?
> `size[i]` = the size of the largest divisible chain whose largest element is `nums[i]`.
> `parent[i]` = the index of the element just before `nums[i]` in that chain (`-1` if none).

### 3. What are the choices?
The previous element of the chain: any `j < i` with `nums[i] % nums[j] == 0`, or none at all (the chain starts at `i`).

### 4. What is the recurrence?
```
size[i] = 1 + max( size[j] : j < i and nums[i] % nums[j] == 0 )     (or 1 if no such j)
parent[i] = the j that achieved the max
```

### 5. What are the base cases?
Every element alone is a valid chain: `size[i] = 1`, `parent[i] = -1`.

### 6. What is the order of computation?
`i` reads only smaller indices, so loop `i` left to right over the **sorted** array.

### 7. Can the state space be optimized?
No. Any earlier `j` may be the parent, and reconstruction needs every `parent` entry. Two O(n) arrays are already small, so there is no `Optimized.java`.

**Reconstruction:** find `best = argmax size[i]`, then follow `parent` pointers back to `-1`. The pointers lead from the largest element to the smallest, so reverse the list (or insert at the front).

## Recursive solution

```java
// size of the best chain ending at sorted index i
static int chainEndingAt(int[] nums, int i) {
    int best = 1;
    for (int j = 0; j < i; j++)
        if (nums[i] % nums[j] == 0)
            best = Math.max(best, 1 + chainEndingAt(nums, j));
    return best;
}
```

## Memoization

Cache `size[i]` and `parent[i]` (`size[i] == 0` means not computed). Call it for every `i`, take the best and walk the parents. See [`solution/Memoization.java`](solution/Memoization.java).

## Tabulation

```java
Arrays.sort(nums);
for (int i = 0; i < n; i++) {
    size[i] = 1; parent[i] = -1;
    for (int j = 0; j < i; j++)
        if (nums[i] % nums[j] == 0 && size[j] + 1 > size[i]) { size[i] = size[j] + 1; parent[i] = j; }
}
```
See [`solution/Tabulation.java`](solution/Tabulation.java).

## Space optimization

Not applicable (see question 7).

## Dry run

`nums = [3,4,16,8]` → sorted `[3,4,8,16]`:

| i | nums[i] | candidates j (nums[j] divides nums[i]) | size[i] | parent[i] |
|---|---|---|---|---|
| 0 | 3 | none | 1 | -1 |
| 1 | 4 | 3∤4 | 1 | -1 |
| 2 | 8 | j=1 (4 \| 8, size 1) | 2 | 1 |
| 3 | 16 | j=1 (size 1 → 2), j=2 (8 \| 16, size 2 → **3**) | 3 | 2 |

Best size 3 at i = 3. Walk the parents: `3 (16) → 2 (8) → 1 (4) → -1`. Reversed: `[4, 8, 16]`. ✓

## Complexity

| Approach | Time | Space | Why |
|---|---|---|---|
| Brute force | O(2ⁿ · n²) | O(n) | every subset, every pair checked |
| Memoization | O(n²) | O(n) | n states, each scans up to n predecessors once |
| Tabulation | O(n log n + n²) = O(n²) | O(n) | sort, then double loop |

## Why the optimized approach is better

Sorting turns "every pair divides" into "each neighbour divides". That local condition is exactly what an LIS-style DP can extend one element at a time. Without the sort, no simple state captures "compatible with everything chosen so far".

## Common mistakes

1. **Forgetting to sort.** Without sorting, `j < i` does not mean `nums[j] ≤ nums[i]`, and the transitivity argument breaks.
2. **Checking `nums[j] % nums[i]`** (the wrong direction). After sorting, the **smaller** one must divide the **larger** one.
3. **Returning only the size.** The problem wants the subset, so keep `parent`.
4. **Building the answer in the wrong order.** Parent pointers go from large to small.

## Key takeaway

> If a pairwise condition is **transitive** after sorting, the problem becomes a chain, and a chain is an LIS-style DP. Keep parent pointers whenever the answer is the object itself and not a number.

## Related problems

- [673. Number of Longest Increasing Subsequence](../673-number-of-longest-increasing-subsequence/): the same O(n²) chain DP, counting instead of reconstructing.
- [1048. Longest String Chain](../1048-longest-string-chain/): sort by length, then chain words.
- [354. Russian Doll Envelopes](../354-russian-doll-envelopes/): sort by one dimension, run LIS on the other.
- Course topic: [Longest Increasing Subsequence](../../../03-subsequence-and-knapsack-dp/11-longest-increasing-subsequence/)
