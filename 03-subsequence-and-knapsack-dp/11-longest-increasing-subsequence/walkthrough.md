# Longest Increasing Subsequence — Walkthrough

## 1. Intuition

You are walking past a row of trees of different heights and you want to photograph as many trees as possible, **in the order you pass them**, with each photographed tree **taller than the one before**. You may skip trees.

```
height:  10   9   2   5   3   7  101  18
```

If you photograph the 10, you must skip 9, 2, 5, 3 and 7 (all shorter) and can only add 101 or 18: two photos. If you start with the 2 instead, you can take 5, then 7, then 101: **four photos**.

At every tree you face one question: *"Is this tree taller than the last one I photographed? If yes, should I take it?"* So the only thing you must remember about the past is **the last tree you photographed**. That remembered piece of information is what makes this a DP state with two parts.

## 2. Problem statement in our own words

Original: [LeetCode 300 — Longest Increasing Subsequence](https://leetcode.com/problems/longest-increasing-subsequence/).

Given an integer array `nums`, return the length of the longest subsequence whose values are strictly increasing. A subsequence keeps the original order but may skip elements. The array has at least one element. LeetCode's follow-up asks for an O(n log n) algorithm.

## 3. Examples

| nums | answer | one LIS | note |
|---|---|---|---|
| `[10, 9, 2, 5, 3, 7, 101, 18]` | 4 | `2, 5, 7, 101` (also `2, 3, 7, 18`, …) | several LIS can exist |
| `[0, 1, 0, 3, 2, 3]` | 4 | `0, 1, 2, 3` | |
| `[7, 7, 7, 7, 7, 7, 7]` | 1 | `7` | **strictly** increasing: equal values don't chain |
| `[3, 4, 5, 1]` | 3 | `3, 4, 5` | used later to show tails ≠ LIS |
| `[5]` | 1 | `5` | edge case: a single element |

**Running example everywhere below:** `nums = [10, 9, 2, 5, 3, 7, 101, 18]`, answer **4**.

## 4. Deriving the solution — the 7 questions

We derive the take/skip formulation first (it follows the subsequence recipe from [Subsequences Fundamentals](../01-subsequences-fundamentals/)), then the "ending at i" formulation.

### 4.1 What is changing?

- `index` — the element we are deciding about, moving left to right.
- `prevIndex` — the index of the last element we took (or `-1` if none). It changes whenever we take something.

Without `prevIndex` we could not decide whether `nums[index]` is allowed. This is the key difference from knapsack, where the past was summarised by a single number (remaining capacity).

### 4.2 What does the state represent?

> `longest(index, prevIndex)` = the length of the longest strictly increasing subsequence we can still build from `nums[index..n-1]`, given that the last element taken so far is `nums[prevIndex]` (no restriction if `prevIndex == -1`).

Answer: `longest(0, -1)`.

The alternative state used by the tabulation:

> `dp[i]` = the length of the longest strictly increasing subsequence that **ends exactly at index `i`** (so `nums[i]` is its last element).

Answer: `max(dp[0..n-1])` — the LIS ends *somewhere*.

### 4.3 What are the choices?

For `longest(index, prevIndex)`:

- **Skip** `nums[index]` → `longest(index + 1, prevIndex)`.
- **Take** `nums[index]`, only if `prevIndex == -1` or `nums[index] > nums[prevIndex]` → `1 + longest(index + 1, index)`.

For `dp[i]`: choose **which earlier index `j` comes just before `i`** (any `j < i` with `nums[j] < nums[i]`), or none (start a new subsequence at `i`).

### 4.4 What is the recurrence?

```
longest(index, prev) = max( longest(index+1, prev),                       // skip
                            1 + longest(index+1, index)  if allowed )     // take

dp[i] = 1 + max( dp[j] : j < i and nums[j] < nums[i] )    (or 1 if no such j)
```

`max` because we want the longest; `1 +` because taking `nums[i]` adds one element.

### 4.5 What are the base cases?

- `longest(n, prevIndex) = 0`: no elements left, nothing more to add.
- `dp[i]` starts at 1: `nums[i]` alone is an increasing subsequence of length 1. This is why the answer for any non-empty array is at least 1.

### 4.6 What is the order of computation?

- `longest(index, ·)` depends on `longest(index + 1, ·)` → bottom-up you would fill `index` from `n-1` **down** to 0.
- `dp[i]` depends on `dp[j]` for `j < i` → fill `i` from 0 **up** to `n-1`.

### 4.7 Can the state space be optimized?

- The `(index, prevIndex)` table has `n × (n+1)` cells, but `dp[i]` needs only **one number per index**: O(n) memory, O(n²) time.
- Time can drop to **O(n log n)** with a different state, the `tails` array (Section 8). That is not just "fewer rows": it is a cleverer summary of the past.

## 5. Brute force: recursion

```java
private static int longest(int[] nums, int index, int prevIndex) {
    if (index == nums.length) return 0;
    int skip = longest(nums, index + 1, prevIndex);
    int take = 0;
    if (prevIndex == -1 || nums[index] > nums[prevIndex]) {
        take = 1 + longest(nums, index + 1, index);
    }
    return Math.max(skip, take);
}
// answer: longest(nums, 0, -1)
```

Full file: [solution/BruteForce.java](solution/BruteForce.java).

### The top of the recursion tree

`L(index, prev)` with values shown as `nums[prev]` for readability (`–` = nothing taken).

```
L(0, –)                                  decide 10
├─ skip → L(1, –)                        decide 9
│         ├─ skip → L(2, –)              decide 2
│         │         ├─ skip → L(3, –)    decide 5 …
│         │         └─ take 2 → L(3, last=2)            ← reached once here
│         │                   ├─ skip → L(4, last=2)    decide 3 …
│         │                   └─ take 5 → L(4, last=5) …
│         └─ take 9 → L(2, last=9)       2 < 9: only skip allowed
│                   └─ skip → L(3, last=9) …
└─ take 10 → L(1, last=10)               9 < 10: only skip
          └─ skip → L(2, last=10)
                    └─ skip → L(3, last=10) …
```

Further down, the same state appears many times. For example `L(6, last=7)` ("decide 101 when the last taken value is 7") is reached through `2,5,7`, through `2,3,7`, through `5,7`, through `3,7` and through `7` alone — five different pasts that all leave the same future problem.

### Why it is slow

Each element is taken or skipped, so up to 2ⁿ paths. With n = 2500 (LeetCode's limit) that is hopeless.

## 6. Memoization (top-down)

The state is `(index, prevIndex)` with `index ∈ [0, n]` and `prevIndex ∈ [-1, n-1]`. Arrays can't have index −1, so we **shift** the second coordinate by one: store `longest(index, prevIndex)` in `memo[index][prevIndex + 1]`.

```java
if (memo[index][prevIndex + 1] != -1) return memo[index][prevIndex + 1];
...
memo[index][prevIndex + 1] = Math.max(skip, take);
```

Full file: [solution/Memoization.java](solution/Memoization.java).

**Cache hits on the running example.** Because our code tries "skip" before "take", the first time we reach `L(6, last=7)` is along the path that skipped everything before index 5 and took `7` alone. It computes `max(skip 101 → L(7, last=7) = 1, take 101 → 1 + L(7, last=101) = 1) = 1`. Every later path that reaches `(6, prevIndex=5)` — via `3,7`, `5,7`, `2,3,7`, `2,5,7` — reads the stored 1 immediately. Similar savings happen for every `(index, prevIndex)` pair, so there are at most `8 × 9 = 72` real computations instead of hundreds of calls.

The memo uses O(n²) memory, which is the reason to prefer the next formulation.

## 7. Tabulation (bottom-up): `dp[i]` = LIS ending at i

```java
int[] dp = new int[n];       // dp[i] = LIS length ending exactly at i
int[] parent = new int[n];   // previous index in that subsequence, -1 = none
Arrays.fill(dp, 1);
Arrays.fill(parent, -1);
for (int i = 0; i < n; i++) {
    for (int j = 0; j < i; j++) {
        if (nums[j] < nums[i] && dp[j] + 1 > dp[i]) {
            dp[i] = dp[j] + 1;
            parent[i] = j;
        }
    }
}
```

Full file: [solution/Tabulation.java](solution/Tabulation.java).

### Filling `dp` step by step

```
index:    0    1    2    3    4    5    6    7
nums:    10    9    2    5    3    7  101   18
```

| i | nums[i] | earlier `j` with `nums[j] < nums[i]` (dp[j]) | dp[i] | parent[i] |
|---|---|---|---|---|
| 0 | 10 | none | 1 | – |
| 1 | 9 | none (10 > 9) | 1 | – |
| 2 | 2 | none | 1 | – |
| 3 | 5 | j=2 (2, dp 1) | **2** | 2 |
| 4 | 3 | j=2 (2, dp 1) | **2** | 2 |
| 5 | 7 | j=2 (dp 1), j=3 (5, dp 2), j=4 (3, dp 2) | **3** | 3 (first to give 3) |
| 6 | 101 | j=0 (1), 1 (1), 2 (1), 3 (2), 4 (2), 5 (7, dp 3) | **4** | 5 |
| 7 | 18 | j=0 (1), 1 (1), 2 (1), 3 (2), 4 (2), 5 (dp 3) — not 101 | **4** | 5 |

```
dp:       1    1    1    2    2    3    4    4
parent:   –    –    –    2    2    3    5    5
```

Answer = `max(dp) = 4`. ✔

### Printing the LIS

The largest `dp` value first appears at `i = 6` (value 101). Follow parents backwards and reverse:

```
i=6 (101) → parent 5 (7) → parent 3 (5) → parent 2 (2) → parent –
collected backwards: 101, 7, 5, 2   → reversed: 2, 5, 7, 101
```

Starting from `i = 7` instead would give `2, 5, 7, 18` — equally long. The strict `>` in the update keeps the **first** best `j`, which is why we get `5` (not `3`) before the `7`.

### The `(index, prevIndex)` form bottom-up

You can also tabulate the memo directly: `next[prev+1]` arrays filled from `index = n-1` down to 0, two rows of length `n+1`. It works (O(n²) time, O(n) memory) but is harder to read than `dp[i]`, so we stop at the memo version and use `dp[i]` for tabulation.

## 8. The O(n log n) method: patience sorting and the tails array

### 8.1 The card game

Deal the numbers one by one like cards and place each card on a pile, following one rule:

> Put the card on the **leftmost** pile whose top card is **≥** the new card. If there is no such pile, start a new pile on the right.

The number of piles at the end equals the LIS length. Let's play with the running example:

```
card 10:  [10]
card 9:   [9]                          9 ≤ 10 → goes on pile 1
card 2:   [2]                          2 ≤ 9  → pile 1
card 5:   [2] [5]                      no top ≥ 5 → new pile
card 3:   [2] [3]                      3 ≤ 5 → pile 2
card 7:   [2] [3] [7]                  new pile
card 101: [2] [3] [7] [101]            new pile
card 18:  [2] [3] [7] [18]             18 ≤ 101 → pile 4
4 piles → LIS = 4 ✔
```

(Only the top card of each pile is shown; cards underneath were covered.)

### 8.2 The tails array

The pile tops, read left to right, form the **tails** array:

> `tails[k]` = the **smallest possible last value** of an increasing subsequence of length `k + 1` among the elements seen so far.

Why "smallest"? A subsequence that ends lower is easier to extend later. If one length-2 subsequence ends at 5 and another ends at 3, the one ending at 3 can accept a 4 next; the one ending at 5 cannot. So for each length we keep only the best (lowest) ending.

The pile rule in array form:

```java
for (int x : nums) {
    int pos = lowerBound(tails, 0, size, x);   // first index with tails[pos] >= x
    tails[pos] = x;                            // replace, or append if pos == size
    if (pos == size) size++;
}
return size;
```

Full file: [solution/Optimized.java](solution/Optimized.java).

| x | lower bound position | action | tails after | meaning |
|---|---|---|---|---|
| 10 | 0 (empty) | append | `[10]` | best length-1 ending: 10 |
| 9 | 0 (10 ≥ 9) | replace | `[9]` | a length-1 subsequence ending at 9 is better |
| 2 | 0 | replace | `[2]` | |
| 5 | 1 (none ≥ 5) | append | `[2, 5]` | `2,5` is a length-2 subsequence |
| 3 | 1 (5 ≥ 3) | replace | `[2, 3]` | `2,3` ends lower than `2,5` |
| 7 | 2 | append | `[2, 3, 7]` | `2,3,7` |
| 101 | 3 | append | `[2, 3, 7, 101]` | length 4 reached |
| 18 | 3 (101 ≥ 18) | replace | `[2, 3, 7, 18]` | `2,3,7,18` ends lower |

Final size 4 = LIS length.

### 8.3 Why does this work? (intuition + proof sketch)

1. **`tails` is always strictly increasing.** Suppose `tails[k] ≥ tails[k+1]`. Take the increasing subsequence of length `k+2` that ends at `tails[k+1]`. Its first `k+1` elements form an increasing subsequence of length `k+1` that ends at a value **smaller** than `tails[k+1] ≤ tails[k]` — contradicting "`tails[k]` is the smallest ending of length `k+1`". So `tails` is sorted, which is what makes binary search legal.
2. **Each update keeps the definition true.** When `x` arrives, it can extend any subsequence whose last value is `< x`. The longest such has length `pos` (because `tails[pos-1] < x ≤ tails[pos]`), so `x` creates a subsequence of length `pos + 1` ending at `x`, and `x ≤ tails[pos]`, so `x` is the new smallest ending for that length. Other lengths can't improve: shorter ones already end below `x`, longer ones would need an ending below `x`.
3. **The size is the LIS length**, because `tails` has an entry for exactly those lengths that some increasing subsequence reaches.

### 8.4 Why `tails` is NOT the LIS itself

`tails` mixes **different** subsequences: each entry is the best ending for its own length, and those best subsequences need not be the same one. Try `[3, 4, 5, 1]`:

```
3 → [3]
4 → [3, 4]
5 → [3, 4, 5]
1 → [1, 4, 5]        1 replaces 3 (best length-1 ending is now 1)
```

The final `tails` is `[1, 4, 5]`, but `1` comes **after** 4 and 5 in the array, so `1, 4, 5` is not a subsequence at all. The size (3) is right; the contents are a bookkeeping device. In the running example the final tails `[2, 3, 7, 18]` happens to be a valid LIS, which is pure luck. If you need the actual sequence, use the parent-pointer method (Section 7), or extend the tails method by storing, for each tails slot, the **index** that sits there and, for each element, the index that was in the slot to its left when it was placed.

### 8.5 Strict vs. non-strict

- Strictly increasing (LC 300): **lower bound** (first `>= x`). An equal value replaces, never extends — so `[7,7,7]` gives 1.
- Non-decreasing ("longest non-decreasing subsequence"): **upper bound** (first `> x`). Then equal values extend — `[7,7,7]` gives 3.

## 9. Dry run on a full example

All three main approaches on `[10, 9, 2, 5, 3, 7, 101, 18]` side by side, element by element:

| step | x | dp[i] (O(n²)) | best LIS so far | tails (O(n log n)) | size |
|---|---|---|---|---|---|
| 0 | 10 | 1 | 1 | `[10]` | 1 |
| 1 | 9 | 1 | 1 | `[9]` | 1 |
| 2 | 2 | 1 | 1 | `[2]` | 1 |
| 3 | 5 | 2 | 2 | `[2, 5]` | 2 |
| 4 | 3 | 2 | 2 | `[2, 3]` | 2 |
| 5 | 7 | 3 | 3 | `[2, 3, 7]` | 3 |
| 6 | 101 | 4 | 4 | `[2, 3, 7, 101]` | 4 |
| 7 | 18 | 4 | 4 | `[2, 3, 7, 18]` | 4 |

Notice: after each step, `size` equals the running maximum of `dp`. That is no coincidence — both measure "the longest increasing subsequence among the elements seen so far". And the memoized recursion returns `longest(0, -1) = 4`. All agree.

## 10. Complexity analysis

| Approach | Time | Space | Why |
|---|---|---|---|
| Brute force | O(2ⁿ) | O(n) | Take/skip for every element; recursion depth n. |
| Memo `(index, prevIndex)` | O(n²) | O(n²) | `n·(n+1)` states × O(1) work; the memo table dominates memory. |
| `dp[i]` tabulation | O(n²) | O(n) | For each `i` we scan all `j < i`: `0 + 1 + … + (n−1) = n(n−1)/2` comparisons. Two arrays of size n. |
| tails + binary search | O(n log n) | O(n) | n elements, each with one binary search over at most n tails. |

For n = 2500 (LC 300), O(n²) ≈ 3 million steps, fine. For n = 2·10⁵ ([CSES 1145](https://cses.fi/problemset/task/1145)), O(n²) = 4·10¹⁰ — far too slow; O(n log n) ≈ 3.5 million steps.

## 11. Why this approach works

`dp[i]` is correct because **every** increasing subsequence ending at `i` is either just `nums[i]`, or a shorter increasing subsequence ending at some `j < i` with `nums[j] < nums[i]`, followed by `nums[i]`. The longest one therefore uses the longest subsequence ending at the best `j` — that is optimal substructure. Since every LIS ends at *some* index, `max(dp)` is the answer.

The `(index, prevIndex)` recursion is correct for the same reason as every take/skip recursion: it explores both options for each element, and remembering the last taken element is exactly enough to know which options are legal. The tails method is justified in Section 8.3.

## 12. Common mistakes

1. **Returning `dp[n-1]` instead of `max(dp)`.** The LIS need not end at the last element (`[1, 2, 0]` → `dp[2] = 1`, answer 2).
2. **Using `<=` instead of `<`.** That computes the longest non-decreasing subsequence; `[7,7,7]` would give 3.
3. **Using upper bound in the tails method for LC 300** (same bug, different place).
4. **Reporting `tails` as the LIS.** It is not a subsequence in general (`[3,4,5,1]` → `[1,4,5]`).
5. **Indexing `memo[index][prevIndex]` with `prevIndex = -1`.** Shift by one: `memo[index][prevIndex + 1]`.
6. **Forgetting `dp[i] = 1` initialisation.** With 0, a single element would not count.

## 13. When to use this technique / how to recognize similar problems

Signals:

- "Longest/largest **chain/sequence/subset** where each next item must be **bigger / divisible / nested / one letter longer** than the previous."
- Order is given (array order), or can be imposed by **sorting** first.

Variations you should be able to derive from this topic:

- [**LC 673 — Number of Longest Increasing Subsequence**](https://leetcode.com/problems/number-of-longest-increasing-subsequence/): keep `count[i]` = how many LIS end at `i`. When `dp[j] + 1 > dp[i]`, set `count[i] = count[j]`; when `dp[j] + 1 == dp[i]`, add `count[j]`. Answer: sum of `count[i]` over all `i` with `dp[i] == max`.
- [**LC 368 — Largest Divisible Subset**](https://leetcode.com/problems/largest-divisible-subset/): sort, replace `nums[j] < nums[i]` by `nums[i] % nums[j] == 0`, and **print** with parent pointers. (Sorting makes divisibility transitive along the chain.)
- [**LC 1048 — Longest String Chain**](https://leetcode.com/problems/longest-string-chain/): sort words by length; the predecessor of a word is that word with one letter removed; store `dp` in a `HashMap<String, Integer>`.
- [**LC 354 — Russian Doll Envelopes**](https://leetcode.com/problems/russian-doll-envelopes/): sort by width **ascending** and, for equal widths, height **descending**; then run the O(n log n) LIS on heights. The descending tie-break stops two envelopes of the same width from chaining.

## 14. Summary and revision notes

- State needs the **last taken element**: `(index, prevIndex)`, or "ending at i".
- `dp[i] = 1 + max(dp[j])` over `j < i` with `nums[j] < nums[i]`; answer `max(dp)`.
- Parent pointers rebuild one LIS; ties make it non-unique.
- tails[k] = smallest ending of a length-(k+1) increasing subsequence; lower bound + replace/append; answer = size.
- tails is sorted but is **not** the LIS.

```
┌──────────────────────────────────────────────────────────────┐
│ dp[i] = 1 + max{ dp[j] : j < i, nums[j] < nums[i] }  (else 1) │
│ LIS   = max over i of dp[i]                     O(n²)        │
│                                                              │
│ for x: pos = lowerBound(tails, x); tails[pos] = x            │
│ LIS   = size(tails)                             O(n log n)   │
└──────────────────────────────────────────────────────────────┘
```

## 15. Exercises

**Exercise 1.** For `nums = [4, 10, 4, 3, 8, 9]`, fill `dp` and the final `tails`. What is the LIS length and one LIS?

<details>
<summary>Answer</summary>

`dp = [1, 2, 1, 1, 2, 3]` (8 extends 4 or 3; 9 extends 8). LIS = 3, e.g. `4, 8, 9`. tails: `[4] → [4,10] → [4,10]` (4 replaces 4) `→ [3,10] → [3,8] → [3,8,9]`, size 3.
</details>

**Exercise 2.** Run the tails method on `[2, 2, 2]` with lower bound and with upper bound. Which problem does each answer?

<details>
<summary>Answer</summary>

Lower bound: every 2 replaces `tails[0]`, final `[2]`, size 1 → strictly increasing (LC 300). Upper bound: no element is `> 2`, so each 2 appends: `[2, 2, 2]`, size 3 → longest non-decreasing subsequence.
</details>

**Exercise 3.** Using the LC 673 idea, compute `dp` and `count` for `[1, 3, 5, 4, 7]`. How many longest increasing subsequences are there?

<details>
<summary>Answer</summary>

`dp = [1, 2, 3, 3, 4]`, `count = [1, 1, 1, 1, 2]` (7 can follow 5 or 4, both with `dp = 3` and count 1). The max length is 4, reached only at index 4 with count 2: `1,3,5,7` and `1,3,4,7`. Answer 2.
</details>

**Exercise 4.** In Russian Doll Envelopes, why must equal widths be sorted by height **descending**? Give a two-envelope counter-example for ascending.

<details>
<summary>Answer</summary>

Envelopes `[3,4]` and `[3,5]` have equal width, so neither fits in the other; the answer is 1. Sorted with heights ascending, the heights are `4, 5`, whose LIS is 2 — wrong. With heights descending (`5, 4`), the LIS is 1, correct. Descending order makes envelopes of the same width impossible to chain.
</details>
