# Bitmask DP — Walkthrough

Some problems ask you to remember **which items have already been used**: which workers have a job, which cities are visited, which numbers are in a bucket. The order in which they were used usually does not matter for the future, only the **set**. Bitmask DP stores that set as one integer, so the set can be used directly as an array index: `dp[set]`.

This lesson first builds the binary toolkit from zero (Section 2), then teaches the pattern on [LeetCode 1879 — Minimum XOR Sum of Two Arrays](https://leetcode.com/problems/minimum-xor-sum-of-two-arrays/) (an assignment problem), shows the counting twin [AtCoder DP O — Matching](https://atcoder.jp/contests/dp/tasks/dp_o), and finishes with [LeetCode 698 — Partition to K Equal Sum Subsets](https://leetcode.com/problems/partition-to-k-equal-sum-subsets/).

---

## 1. Intuition

Imagine a row of light switches, one per item. A switch is **on** if the item is used and **off** if not. With 3 items there are 2 × 2 × 2 = 8 possible switch patterns, and every pattern is a possible "which items are used" situation.

Now read the switches as a binary number (on = 1, off = 0). The 8 patterns become the numbers 0 to 7. Every situation now has a **number**, and numbers can index an array. That is the entire trick: a set of used items becomes an integer, the "mask", and `dp[mask]` holds the best answer for that situation.

Why does this help? A brute force that tries every **order** of n items does n! work. A DP over **sets** does about 2ⁿ · n work, because it merges all orders that lead to the same set. For n = 15: 15! ≈ 1.3 × 10¹², while 2¹⁵ · 15 ≈ 491,520.

---

## 2. Bitmask basics, one operation at a time

### 2.1 Binary numbers
A number is stored as bits (binary digits). The rightmost bit is **bit 0** and is worth 1; bit 1 is worth 2; bit 2 is worth 4; bit i is worth 2ⁱ.

```
binary  1 0 1 1      = 8 + 0 + 2 + 1 = 11
bit #   3 2 1 0
```

We write binary literals in Java with `0b`: `0b1011 == 11`.

### 2.2 A subset as a number
Number the items `0, 1, …, n-1`. Item `i` is in the subset **exactly when bit `i` is 1**.

| Subset of {0,1,2} | Bits (2 1 0) | Mask value |
|---|---|---|
| { } | 0 0 0 | 0 |
| {0} | 0 0 1 | 1 |
| {1} | 0 1 0 | 2 |
| {0,1} | 0 1 1 | 3 |
| {2} | 1 0 0 | 4 |
| {0,2} | 1 0 1 | 5 |
| {1,2} | 1 1 0 | 6 |
| {0,1,2} | 1 1 1 | 7 |

All subsets of n items are the numbers `0` to `2ⁿ − 1`. The **full set** is `(1 << n) - 1`, which is n ones.

### 2.3 The operators we need

| Operator | Name | Rule per bit |
|---|---|---|
| `a & b` | AND | 1 only if **both** bits are 1 |
| `a \| b` | OR | 1 if **either** bit is 1 |
| `a ^ b` | XOR | 1 if the bits are **different** |
| `~a` | NOT | flips every bit |
| `1 << i` | left shift | the number with **only bit i** set, i.e. 2ⁱ |
| `a >>> 1` | right shift | moves every bit one place right (drops bit 0) |

### 2.4 The five operations, explained one by one

**1. A single item: `1 << i`.**
`1 << 2` shifts the 1 two places left: `0b001 → 0b100 = 4`. This is the mask for "only item 2".

**2. Add item i (set bit i): `mask | (1 << i)`.**
OR keeps every bit that is already 1 and forces bit i to 1.
```
mask       0 1 0 1     {0, 2}
1 << 1     0 0 1 0
OR         0 1 1 1     {0, 1, 2}
```
If the item was already present, nothing changes.

**3. Is item i present? (test bit i): `(mask & (1 << i)) != 0`.**
AND with a number that has only bit i set wipes every other bit. What is left is either 0 (absent) or `1 << i` (present).
```
mask       0 1 0 1          mask       0 1 0 1
1 << 2     0 1 0 0          1 << 1     0 0 1 0
AND        0 1 0 0  != 0    AND        0 0 0 0  == 0
           -> present                  -> absent
```
Always compare with `!= 0`, not `== 1`. The result is `4` here, not `1`.

**4. Remove item i (clear bit i): `mask & ~(1 << i)`.**
`~(1 << i)` has every bit 1 **except** bit i. AND with it keeps everything else and forces bit i to 0.
```
mask         0 1 1 1     {0, 1, 2}
~(1 << 0)    1 1 1 0
AND          0 1 1 0     {1, 2}
```

**5. Loop over every submask of a mask.**
A **submask** of `mask` is a subset of it: it uses only bits that are 1 in `mask`. The standard loop is:

```java
for (int sub = mask; ; sub = (sub - 1) & mask) {
    // use sub
    if (sub == 0) break;
}
```

Why does `(sub - 1) & mask` work? Subtracting 1 turns the lowest 1-bit of `sub` into 0 and every 0 below it into 1. That is "the next smaller number". AND-ing with `mask` then deletes any bit that `mask` does not allow. The result is the next smaller submask. Trace for `mask = 0b101`:

```
sub = 101 (5)
      101 - 1 = 100, & 101 = 100 (4)
      100 - 1 = 011, & 101 = 001 (1)
      001 - 1 = 000, & 101 = 000 (0)  -> process 0, then break
submasks: 5, 4, 1, 0   (all 2^2 = 4 subsets of {0, 2})
```

The `break` after processing 0 matters: `(0 - 1) & mask` equals `mask` again, so without it the loop never ends.

Two more you will meet:

- **Count the items:** `Integer.bitCount(mask)` (the "popcount"). `BitOperationsDemo.java` also counts them by hand: look at `mask & 1`, shift right, repeat.
- **Remove the lowest item:** `mask & (mask - 1)`. Used in LeetCode 698 below to build subset sums quickly.

All of these are checked in [`BitOperationsDemo.java`](solution/BitOperationsDemo.java). Run it and change the inputs until each line feels obvious.

---

## 3. Problem statement in our own words

Original: [LeetCode 1879 — Minimum XOR Sum of Two Arrays](https://leetcode.com/problems/minimum-xor-sum-of-two-arrays/)

You get two arrays of the same length `n` (n ≤ 14). Reorder the second array however you like, then pair `nums1[i]` with `nums2[i]` and add up `nums1[i] XOR nums2[i]`. Find the smallest possible total.

In other words: **assign** each element of `nums1` to a different element of `nums2`, where each pair has a cost, and minimise the total cost. This "assign n things to n things, each used once" shape is the classic home of bitmask DP.

---

## 4. Examples

**Running example:** `nums1 = [1, 0, 3]`, `nums2 = [5, 3, 4]` → **8**.

Cost of every pair, `cost[i][j] = nums1[i] ^ nums2[j]`:

```
              nums2[0]=5  nums2[1]=3  nums2[2]=4
nums1[0]=1        4           2           5
nums1[1]=0        5           3           4
nums1[2]=3        6           0           7
```

The best assignment is `1↔5 (4)`, `0↔4 (4)`, `3↔3 (0)` → 8.

**Example 2:** `[1,2]`, `[2,3]` → **2** (`1^3 + 2^2 = 2 + 0`).
**Edge case:** `[5]`, `[5]` → **0**.
**Example 4:** `[1,2,3,4]`, `[4,3,2,1]` → **0** (the same numbers, so each can pair with itself).

---

## 5. Deriving the solution — the 7 questions

### Q1. What is changing?
We pair `nums1[0]`, then `nums1[1]`, … in order. Two things change: the index `i` of the next `nums1` element, and **which `nums2` elements are already taken**.

### Q2. What does the state represent?
The set of taken `nums2` elements is a mask. And `i` is not independent: after pairing `i` elements, exactly `i` bits are set, so `i = bitCount(mask)`. The mask alone is the state:

> `dp[mask]` = the minimum total cost of pairing the first `bitCount(mask)` elements of `nums1` with exactly the `nums2` elements whose bits are set in `mask`.

### Q3. What are the choices?
For the next element `nums1[i]` (with `i = bitCount(mask)`), choose any `j` whose bit is **not** set in `mask`.

### Q4. What is the recurrence?
"Push" form (used in tabulation):
```
for every j not in mask:
    dp[mask | (1 << j)] = min( dp[mask | (1 << j)],  dp[mask] + (nums1[i] ^ nums2[j]) )
```
"Pull" form (used in memoization, from the remaining work's point of view):
```
best(mask) = 0                                         if bitCount(mask) == n
best(mask) = min over j not in mask of ( (nums1[i] ^ nums2[j]) + best(mask | (1 << j)) )
```

### Q5. What are the base cases?
`dp[0] = 0`: nothing is paired and nothing is paid. In the pull form, `best(full) = 0`: everything is paired, so no more cost.

### Q6. What is the order of computation?
Every transition **adds** a bit, and adding a bit always gives a **bigger** number (`mask | (1 << j) > mask` when bit j was 0). So looping `mask = 0, 1, 2, …, 2ⁿ − 1` in increasing order finishes every mask before it is extended.

### Q7. Can the state space be optimized?
Not below 2ⁿ. A mask can be reached from many smaller masks and extends into many bigger ones, so there is no "only the last row is needed" shortcut. 2¹⁴ ints is 64 KB, which is tiny. The real limit is time, not memory (Section 12).

---

## 6. Brute force: recursion

Try every free `nums2` element for `nums1[0]`, then every remaining one for `nums1[1]`, and so on, with a `boolean[] used`.

```java
private static int pair(int[] nums1, int[] nums2, int i, boolean[] used) {
    if (i == nums1.length) return 0;
    int best = Integer.MAX_VALUE;
    for (int j = 0; j < nums2.length; j++) {
        if (used[j]) continue;
        used[j] = true;
        best = Math.min(best, (nums1[i] ^ nums2[j]) + pair(nums1, nums2, i + 1, used));
        used[j] = false;
    }
    return best;
}
```

Recursion tree on the running example (each edge is "nums1[i] takes nums2[j]"; the set of used j's is written in braces):

```
                              {} 
            /                  |                  \
       j=0 (4)             j=1 (2)             j=2 (5)
        {0}                  {1}                  {2}
      /      \             /      \             /      \
  j=1(3)   j=2(4)      j=0(5)   j=2(4)      j=0(5)   j=1(3)
  {0,1}    {0,2}       {0,1}    {1,2}       {0,2}    {1,2}
    |        |           |        |           |        |
  j=2(7)   j=1(0)      j=2(7)   j=0(6)      j=1(0)   j=0(6)
  =14      = 8         =14      =12         =10      =14
```

Look at the second level. `{0,1}` appears twice (reached by "0 then 1" and "1 then 0"), and so do `{0,2}` and `{1,2}`. In both copies the remaining work is identical: pair `nums1[2]` with the one element left. Only the cost **so far** differs, and the subproblem does not care about that. This is the overlap the mask captures. The tree has n! leaves; with n = 14 that is 87 billion.

---

## 7. Memoization (top-down)

Replace `boolean[] used` with an `int` mask, and cache by mask:

```java
private static int best(int[] nums1, int[] nums2, int usedMask) {
    int i = Integer.bitCount(usedMask);
    if (i == n) return 0;
    if (memo[usedMask] != -1) return memo[usedMask];      // cache hit
    int result = Integer.MAX_VALUE;
    for (int j = 0; j < n; j++) {
        if ((usedMask & (1 << j)) != 0) continue;          // test bit j
        result = Math.min(result, (nums1[i] ^ nums2[j]) + best(nums1, nums2, usedMask | (1 << j)));
    }
    return memo[usedMask] = result;
}
```

Dry run (bits are written `b2 b1 b0`, bit j = nums2[j] used). `best` values are "remaining cost":

| Call order | mask | i | Candidates | best(mask) |
|---|---|---|---|---|
| 1 | 011 | 2 | j=2: 7 + 0 | 7 |
| 2 | 101 | 2 | j=1: 0 + 0 | 0 |
| 3 | 001 | 1 | j=1: 3 + best(011)=7 → 10; j=2: 4 + best(101)=0 → 4 | 4 |
| 4 | 110 | 2 | j=0: 6 + 0 | 6 |
| 5 | 010 | 1 | j=0: 5 + best(011) **hit** = 12; j=2: 4 + best(110)=6 → 10 | 10 |
| 6 | 100 | 1 | j=0: 5 + best(101) **hit** = 5; j=1: 3 + best(110) **hit** = 9 | 5 |
| 7 | 000 | 0 | j=0: 4 + 4 = 8; j=1: 2 + 10 = 12; j=2: 5 + 5 = 10 | **8** |

Every mask is computed once. The three level-2 masks (`011`, `101`, `110`) were each needed twice, and the second request was a cache hit.

---

## 8. Tabulation (bottom-up)

```java
int[] dp = new int[1 << n];
Arrays.fill(dp, Integer.MAX_VALUE);
dp[0] = 0;
for (int mask = 0; mask < full; mask++) {
    int i = Integer.bitCount(mask);
    for (int j = 0; j < n; j++) {
        if ((mask & (1 << j)) != 0) continue;
        int next = mask | (1 << j);
        dp[next] = Math.min(dp[next], dp[mask] + (nums1[i] ^ nums2[j]));
    }
}
return dp[full];
```

Filling the table in increasing mask order (bits `b2 b1 b0`; "cost so far"):

| mask | i | Pushes | dp after this step |
|---|---|---|---|
| 000 | 0 | → 001: 0+4, → 010: 0+2, → 100: 0+5 | dp[001]=4, dp[010]=2, dp[100]=5 |
| 001 | 1 | → 011: 4+3=7, → 101: 4+4=8 | dp[011]=7, dp[101]=8 |
| 010 | 1 | → 011: 2+5=7 (tie), → 110: 2+4=6 | dp[110]=6 |
| 011 | 2 | → 111: 7+7=14 | dp[111]=14 |
| 100 | 1 | → 101: 5+5=10 (worse), → 110: 5+3=8 (worse) | – |
| 101 | 2 | → 111: 8+0=8 | dp[111]=8 |
| 110 | 2 | → 111: 6+6=12 (worse) | – |

Final table:

```
mask : 000 001 010 011 100 101 110 111
dp   :   0   4   2   7   5   8   6   8
```

Answer `dp[111]` = **8**.

Notice that **every mask is final before it pushes**. `dp[101]` gets pushes from `001` and `100`, and both are smaller numbers, so they are processed before `101` is.

---

## 9. Space optimization

Not possible below O(2ⁿ) (see Q7). One small time saving: in the tabulation, masks with `bitCount(mask) = i` only push to masks with `i + 1` bits, so you could process the masks layer by layer. That changes nothing asymptotically, so the simple increasing loop is preferred.

---

## 10. Dry run on a full example — the counting twin (AtCoder DP O)

[AtCoder DP O — Matching](https://atcoder.jp/contests/dp/tasks/dp_o): `N` men and `N` women, and a 0/1 table saying who is compatible. Count the ways to form `N` compatible couples (modulo 10⁹ + 7).

The state is identical. Only the combine operation changes from `min` to `+`:

> `ways[mask]` = number of ways to pair men `0 … bitCount(mask)−1` with exactly the women in `mask`.

```java
ways[0] = 1;
for (int mask = 0; mask < (1 << n); mask++) {
    int man = Integer.bitCount(mask);
    if (man == n) continue;
    for (int woman = 0; woman < n; woman++)
        if ((mask & (1 << woman)) == 0 && compatible[man][woman] == 1)
            ways[mask | (1 << woman)] += ways[mask];
}
```

Sample: compatible = `[[0,1,1],[1,0,1],[1,1,1]]` (man 0 likes women 1, 2; man 1 likes 0, 2; man 2 likes everyone).

| mask (b2 b1 b0) | man | pushes | ways after |
|---|---|---|---|
| 000 | 0 | → 010, → 100 | ways[010]=1, ways[100]=1 |
| 010 | 1 | woman 0 → 011; woman 2 → 110 | ways[011]=1, ways[110]=1 |
| 100 | 1 | woman 0 → 101 (woman 2 taken) | ways[101]=1 |
| 011 | 2 | woman 2 → 111 | ways[111]=1 |
| 101 | 2 | woman 1 → 111 | ways[111]=2 |
| 110 | 2 | woman 0 → 111 | ways[111]=3 |

Answer **3**. (Mask 001 is never reached because man 0 does not like woman 0, so `ways[001]` stays 0 and is skipped.)

---

## 11. Complexity analysis

| Approach | Time | Space | Why |
|---|---|---|---|
| Brute force | O(n!) | O(n) | every ordering of nums2 is tried |
| Memoization | O(2ⁿ · n) | O(2ⁿ) | 2ⁿ masks, each loops over n candidates once; memo has one cell per mask |
| Tabulation | O(2ⁿ · n) | O(2ⁿ) | same states and transitions, filled by a loop |

### The practical limit: n ≤ about 20

Count the work for `2ⁿ · n` (a computer does roughly 10⁸–10⁹ simple steps per second):

| n | 2ⁿ | 2ⁿ · n | verdict |
|---|---|---|---|
| 10 | 1,024 | ~10⁴ | instant |
| 14 | 16,384 | ~2.3 × 10⁵ | instant (LeetCode 1879's limit) |
| 16 | 65,536 | ~10⁶ | instant (LeetCode 698's limit) |
| 20 | ~10⁶ | ~2 × 10⁷ | fine |
| 21 (AtCoder DP O) | ~2 × 10⁶ | ~4.4 × 10⁷ | fine |
| 25 | ~3.4 × 10⁷ | ~8 × 10⁸ | too slow in most judges |
| 30 | ~10⁹ | ~3 × 10¹⁰ | impossible (and the table needs 4 GB) |

**Rule of thumb:** if a constraint says `n ≤ 12`, `14`, `16`, `20`, think bitmask DP. If `n` is in the hundreds, bitmask DP over those items is out. Look for a different state.

Problems with a `dp[mask][i]` state (like the next topic, TSP) cost `2ⁿ · n²`, which pushes the limit down to about n = 16–18.

---

## 12. Why this approach works

- **The future depends only on the set, not on the order.** Once the set of used `nums2` elements is fixed, the remaining `nums1` elements and the remaining `nums2` elements are fixed too, whatever order produced them. So all orderings with the same set can share one answer. That is precisely the overlap in the recursion tree.
- **The index is implied.** Because we pair `nums1` in a fixed order, `i = bitCount(mask)`. Fixing the order of one side is what keeps the state at 2ⁿ instead of 2ⁿ · n. Without it, `{0→A, 1→B}` and `{1→A, 0→B}` would be separate states.
- **Every assignment is reachable.** Any full assignment is "nums1[0] takes some j₀, nums1[1] takes some j₁, …", which is a path from mask 0 to the full mask, so the minimum over paths is the true minimum.

---

## 13. Application — LeetCode 698: Partition to K Equal Sum Subsets

[LeetCode 698](https://leetcode.com/problems/partition-to-k-equal-sum-subsets/): can the array (n ≤ 16) be split into `k` groups with equal sums?

**Setup.** Let `target = total / k`. If `total` is not divisible by `k`, or a single number exceeds `target`, the answer is immediately false.

**Key insight: the mask tells you how full the current bucket is.** Fill buckets one at a time, always completing a bucket before starting the next. If you know **which** numbers are already placed (the mask), you know their sum, and

```
fill of the current bucket = sum(mask) % target
```

because every completed bucket contributed exactly `target`. So again the mask alone is the whole state.

1. **Changing:** which numbers are placed.
2. **State:** `reachable[mask]` = true if the numbers in `mask` can be placed so that all buckets but the current one are exactly full.
3. **Choices:** add any unused number `nums[j]` that still fits: `fill + nums[j] ≤ target`.
4. **Recurrence:** if `reachable[mask]` and `nums[j]` fits, then `reachable[mask | (1 << j)] = true`.
5. **Base case:** `reachable[0] = true`.
6. **Order:** increasing mask (adding a bit makes the number bigger).
7. **Space:** O(2ⁿ) booleans, plus O(2ⁿ) subset sums. Each sum comes from a smaller mask with the lowest bit removed: `sum[mask] = sum[mask & (mask - 1)] + nums[lowest bit]`.

Dry run: `nums = [1, 2, 1, 2]`, `k = 2`, `target = 3` (bits `b3 b2 b1 b0`, bit j = nums[j] placed):

| mask | sum | fill | pushes (number that fits → new mask) |
|---|---|---|---|
| 0000 | 0 | 0 | 1→0001, 2→0010, 1→0100, 2→1000 |
| 0001 | 1 | 1 | 2→0011, 1→0101, 2→1001 |
| 0010 | 2 | 2 | 1→0011, 1→0110; nums[3]=2 does **not** fit (2+2 > 3) |
| 0011 | 3 | 0 | bucket full! 1→0111, 2→1011 |
| 0100 | 1 | 1 | 1→0101, 2→0110, 2→1100 |
| 0101 | 2 | 2 | neither 2 fits |
| 0110 | 3 | 0 | 1→0111, 2→1110 |
| 0111 | 4 | 1 | 2→1111 ✓ |
| … | | | |

`reachable[1111]` becomes true, so the answer is **true** ({1,2} and {1,2}). Mask `1010` (both 2s together) is never reached: once the first 2 is in a bucket, the second 2 does not fit. The DP never builds an invalid bucket.

Complexity: O(2ⁿ · n) time, O(2ⁿ) space. With n = 16 that is about one million steps. The solution file cross-checks the DP against a plain backtracking search.

---

## 14. Common mistakes

1. **Operator precedence.** In Java, `mask & 1 << j == 0` parses as `mask & ((1 << j) == 0)`, which does not compile. Always write `(mask & (1 << j)) == 0`.
2. **Testing with `== 1`.** `(mask & (1 << j))` is `2ʲ`, not 1. Compare with `!= 0`.
3. **Using `1 << j` with j ≥ 31.** An `int` has 32 bits, and `1 << 31` is negative. For larger sets, use `long` and `1L << j`. In practice n is small, so this rarely matters.
4. **Forgetting the implied index.** Storing `dp[i][mask]` when `i = bitCount(mask)` wastes a factor of n in memory and time.
5. **Iterating masks in the wrong order.** Pushing to `mask | bit` is safe with an increasing loop. Pulling from `mask ^ bit` (removing a bit) is also safe in increasing order, because the smaller mask is already done. A decreasing loop breaks both.
6. **An endless submask loop.** Without `if (sub == 0) break;`, `(0 - 1) & mask` restarts at `mask`.
7. **Trying bitmask DP for n = 40.** 2⁴⁰ ≈ 10¹². Check the constraints first.

---

## 15. When to use this technique / how to recognize similar problems

- n is **small** (≤ 20, often 12–16), and you must choose an order, an assignment or a grouping of all n items.
- The future depends on **which** items are used, not on how they were used.
- Phrases: "each worker gets exactly one job", "visit every node", "use every number once", "divide into groups", "every person wears a different hat".
- Brute force is n! (permutations) or kⁿ (put each item into one of k groups).

Related problems: [Beautiful Arrangement (526)](https://leetcode.com/problems/beautiful-arrangement/), [Can I Win (464)](https://leetcode.com/problems/can-i-win/), [Minimum Number of Work Sessions (1986)](https://leetcode.com/problems/minimum-number-of-work-sessions-to-finish-the-tasks/), [Smallest Sufficient Team (1125)](https://leetcode.com/problems/smallest-sufficient-team/), [Number of Ways to Wear Different Hats (1434)](https://leetcode.com/problems/number-of-ways-to-wear-different-hats-to-each-other/). The next topic, the [Traveling Salesperson Problem](../10-traveling-salesperson-problem/), adds a second state component: the last city visited.

---

## 16. Summary and revision notes

- A set of items `{0..n-1}` ↔ an integer whose bit i says "item i is in". All subsets = `0 … 2ⁿ − 1`.
- `1 << i` single item · `mask | (1<<i)` add · `(mask & (1<<i)) != 0` test · `mask & ~(1<<i)` remove · `sub = (sub-1) & mask` next submask.
- `dp[mask]` merges all orders that lead to the same set: n! → 2ⁿ · n.
- If items on one side are processed in a fixed order, the index is `bitCount(mask)` and does not need its own dimension.
- Loop masks in increasing order: adding a bit always increases the number.
- Feasible for n ≤ ~20.

```
+-------------------------------------------------------------+
|  Assignment (LeetCode 1879 / AtCoder DP O):                 |
|    i = bitCount(mask)                                       |
|    for j not in mask:                                       |
|      dp[mask | 1<<j] = min/+ ( dp[mask] (+) cost(i, j) )     |
|    dp[0] = 0 (or 1 for counting);  answer = dp[(1<<n) - 1]  |
|                                                             |
|  Partition (LeetCode 698):  fill = sum(mask) % target       |
|    nums[j] fits if fill + nums[j] <= target                 |
+-------------------------------------------------------------+
```

---

## 17. Exercises

**Exercise 1.** Write the mask for the subset `{1, 3, 4}`, then compute: add item 0, remove item 3, and test item 2.

<details>
<summary>Answer</summary>

`{1,3,4}` = bits 4, 3, 1 = `0b11010` = 16 + 8 + 2 = **26**.
- add 0: `26 | 1 = 27` (`0b11011`)
- remove 3: `27 & ~8 = 19` (`0b10011`)
- test 2: `19 & 4 = 0` → item 2 is **absent**.
</details>

**Exercise 2.** List every submask of `0b1100` in the order the standard loop produces them.

<details>
<summary>Answer</summary>

`1100 (12)`, `1000 (8)`, `0100 (4)`, `0000 (0)`. Check one step: `12 - 1 = 0b1011`, `& 0b1100 = 0b1000`.
</details>

**Exercise 3.** Fill `dp[mask]` for LeetCode 1879 with `nums1 = [1, 2]`, `nums2 = [2, 3]`.

<details>
<summary>Answer</summary>

Costs: `1^2 = 3`, `1^3 = 2`, `2^2 = 0`, `2^3 = 1`.
`dp[00]=0`, `dp[01]=3` (1↔2), `dp[10]=2` (1↔3), `dp[11] = min(dp[01] + 2^3, dp[10] + 2^2) = min(3+1, 2+0) = 2`. Answer **2**.
</details>

**Exercise 4.** For LeetCode 698, why is it enough to store `reachable[mask]` and not also "which bucket is being filled" or "how full it is"?

<details>
<summary>Answer</summary>

Both are determined by the mask. Buckets are filled one at a time and each completed bucket holds exactly `target`. So the number of completed buckets is `sum(mask) / target` and the current fill is `sum(mask) % target`. Extra state would only duplicate information, multiplying the table size for nothing.
</details>
