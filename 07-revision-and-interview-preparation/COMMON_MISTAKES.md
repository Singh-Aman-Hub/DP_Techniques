# Common DP Mistakes — Buggy vs. Fixed

> **Module:** 07 · Revision and Interview Preparation · **Use:** when a DP solution gives a wrong answer, a time-out or a crash — and as a pre-submit checklist

Most DP bugs are not about the idea; they are small mechanical slips. This file groups them by category. Each entry shows:

- **Symptom** — what you observe.
- **Buggy** Java and **Fixed** Java, side by side.
- **Why** — the one-sentence reason.

At the end there is a [debugging checklist](#debugging-checklist) you can run in under two minutes.

---

## Contents

1. [Base cases](#1-base-cases)
2. [Loop order and direction](#2-loop-order-and-direction)
3. [Knapsack: forward vs. reverse capacity loop](#3-knapsack-forward-vs-reverse-capacity-loop)
4. [Combinations vs. permutations](#4-combinations-vs-permutations)
5. [Indexing and off-by-one](#5-indexing-and-off-by-one)
6. [Memoization pitfalls](#6-memoization-pitfalls)
7. [Sentinel values](#7-sentinel-values)
8. [Overflow and infinity arithmetic](#8-overflow-and-infinity-arithmetic)
9. [Modelling mistakes](#9-modelling-mistakes)
10. [Performance and runtime errors](#10-performance-and-runtime-errors)
11. [Debugging checklist](#debugging-checklist)

---

## 1. Base cases

Background: [state transitions and base cases](../00-foundations/10-state-transitions-and-base-cases/).

### 1.1 "Zero ways" instead of "one way" for the empty target

**Symptom:** a counting DP returns 0 for every input.

```java
// BUGGY — Coin Change II: number of ways to make `amount`
long[] ways = new long[amount + 1];        // ways[0] stays 0
for (int coin : coins)
    for (int a = coin; a <= amount; a++)
        ways[a] += ways[a - coin];         // everything is built from ways[0] = 0 → all zeros
return ways[amount];
```

```java
// FIXED
long[] ways = new long[amount + 1];
ways[0] = 1;                               // exactly one way to make 0: choose no coins
for (int coin : coins)
    for (int a = coin; a <= amount; a++)
        ways[a] += ways[a - coin];
return ways[amount];
```

**Why:** every count is ultimately a sum of copies of the base case. If the base is 0, everything is 0. "The empty choice" is a valid way.

### 1.2 Base case that is true for the *count* problem but wrong for the *min* problem

```java
// BUGGY — Coin Change (fewest coins), copied the base from the counting version
int[] fewest = new int[amount + 1];
Arrays.fill(fewest, amount + 1);
fewest[0] = 1;                             // claims making 0 needs one coin
```

```java
// FIXED
fewest[0] = 0;                             // making 0 needs zero coins
```

**Why:** the base case must match the *meaning* of the state. Re-read your state sentence before writing the base.

### 1.3 Decode Ways: a leading '0'

```java
// BUGGY — treats "0" like any other single digit
dp[0] = 1;
for (int i = 1; i <= n; i++) {
    dp[i] = dp[i - 1];                     // "0" is not a valid letter!
    if (i >= 2 && Integer.parseInt(s.substring(i - 2, i)) <= 26) dp[i] += dp[i - 2];
}
```

```java
// FIXED
dp[0] = 1;
for (int i = 1; i <= n; i++) {
    if (s.charAt(i - 1) != '0') dp[i] = dp[i - 1];          // single digit 1..9
    if (i >= 2) {
        int two = Integer.parseInt(s.substring(i - 2, i));
        if (two >= 10 && two <= 26) dp[i] += dp[i - 2];     // "05" is not 5
    }
}
```

**Why:** valid single digits are 1–9 and valid pairs are 10–26. Both edge rules are part of the base/transition conditions.

### 1.4 Grid with obstacles: filling the whole first row with 1

```java
// BUGGY — Unique Paths II
for (int c = 0; c < cols; c++) paths[0][c] = 1;   // ignores an obstacle in row 0
```

```java
// FIXED
paths[0][0] = grid[0][0] == 1 ? 0 : 1;
for (int c = 1; c < cols; c++)
    paths[0][c] = grid[0][c] == 1 ? 0 : paths[0][c - 1];   // blocked once, blocked after
```

**Why:** cells after an obstacle in the first row are unreachable; copying from the left propagates the block.

---

## 2. Loop order and direction

Background: [transition dependencies and computation order](../00-foundations/11-transition-dependencies-and-computation-order/).

### 2.1 Reading a cell that has not been computed yet (palindromic range DP)

`dp[i][j]` for Longest Palindromic Subsequence reads `dp[i+1][j−1]`, `dp[i+1][j]` — the **next** row.

```java
// BUGGY — i ascending: dp[i+1][...] is still 0 when we read it
for (int i = 0; i < n; i++) {
    dp[i][i] = 1;
    for (int j = i + 1; j < n; j++)
        dp[i][j] = s.charAt(i) == s.charAt(j) ? dp[i + 1][j - 1] + 2
                                              : Math.max(dp[i + 1][j], dp[i][j - 1]);
}
```

```java
// FIXED — i descending, so row i+1 is complete before row i
for (int i = n - 1; i >= 0; i--) {
    dp[i][i] = 1;
    for (int j = i + 1; j < n; j++)
        dp[i][j] = s.charAt(i) == s.charAt(j) ? dp[i + 1][j - 1] + 2
                                              : Math.max(dp[i + 1][j], dp[i][j - 1]);
}
```

**Why:** the loop order must follow the dependency arrows. Draw the cell and the cells it reads; loop so that the arrows point at already-filled cells.

### 2.2 Interval DP looping by left endpoint instead of by length

```java
// BUGGY — MCM: dp[k+1][j] may not be ready when i is small
for (int i = 1; i < n; i++)
    for (int j = i + 1; j < n; j++)
        for (int k = i; k < j; k++) ...
```

```java
// FIXED — every sub-interval is strictly shorter, so loop by length
for (int len = 2; len < n; len++)
    for (int i = 1; i + len - 1 < n; i++) {
        int j = i + len - 1;
        dp[i][j] = Integer.MAX_VALUE;
        for (int k = i; k < j; k++)
            dp[i][j] = Math.min(dp[i][j], dp[i][k] + dp[k + 1][j] + dims[i - 1] * dims[k] * dims[j]);
    }
```

### 2.3 Updating "today" in place while still reading "yesterday" (state machines)

```java
// BUGGY — Stock II: hold uses the *new* free from today
for (int p : prices) {
    free = Math.max(free, hold + p);
    hold = Math.max(hold, free - p);       // buys and sells on the same day using updated free
}
```

```java
// FIXED — snapshot yesterday's values
for (int p : prices) {
    int prevFree = free, prevHold = hold;
    free = Math.max(prevFree, prevHold + p);
    hold = Math.max(prevHold, prevFree - p);
}
```

**Why:** for Stock II (and the fee version) the buggy order happens to give the same number, because selling and re-buying on the same day can never help. That is luck, not correctness. With a **cooldown**, `hold` must read `free` from *two* days ago; overwriting `free` in place loses that value and the answer becomes wrong. Always use snapshots: it costs nothing, and the code then matches the recurrence exactly.

---

## 3. Knapsack: forward vs. reverse capacity loop

Background: [0/1 knapsack](../03-subsequence-and-knapsack-dp/02-zero-one-knapsack/), [unbounded knapsack](../03-subsequence-and-knapsack-dp/07-unbounded-knapsack/).

```java
// BUGGY — 0/1 knapsack in 1D with an ascending loop
for (int item = 0; item < n; item++)
    for (int cap = weight[item]; cap <= W; cap++)
        best[cap] = Math.max(best[cap], value[item] + best[cap - weight[item]]);
```

Dry run: one item, weight 2, value 3, W = 4.
`best[2] = 3`, then `best[4] = 3 + best[2] = 6` → the single item was taken **twice**.

```java
// FIXED — descending: best[cap - w] still holds the value from BEFORE this item
for (int item = 0; item < n; item++)
    for (int cap = W; cap >= weight[item]; cap--)
        best[cap] = Math.max(best[cap], value[item] + best[cap - weight[item]]);
```

**Rule:** 0/1 (each item once) → **descending**. Unbounded (reuse allowed) → **ascending**. Getting it backwards in the other direction silently forbids reuse in unbounded problems.

---

## 4. Combinations vs. permutations

Background: [Coin Change II](../03-subsequence-and-knapsack-dp/09-coin-change-ii/).

Coins `{1, 2}`, amount 3. Sets: `{1,1,1}`, `{1,2}` → **2**. Ordered sequences: `1+1+1`, `1+2`, `2+1` → **3**.

```java
// BUGGY for "count sets" (Coin Change II) — amount outside counts orderings
ways[0] = 1;
for (int a = 1; a <= amount; a++)
    for (int coin : coins)
        if (coin <= a) ways[a] += ways[a - coin];      // returns 3
```

```java
// FIXED — coins outside: each set is generated in one canonical coin order
ways[0] = 1;
for (int coin : coins)
    for (int a = coin; a <= amount; a++)
        ways[a] += ways[a - coin];                     // returns 2
```

**Why:** with coins outside, by the time coin 2 is used, coin 1 is never added *after* it, so `2+1` is never generated separately from `1+2`. If the problem wants ordered sequences ([Combination Sum IV](https://leetcode.com/problems/combination-sum-iv/)), the "buggy" version is the correct one. Read the examples in the statement to see which is wanted.

---

## 5. Indexing and off-by-one

### 5.1 Mixing "prefix length" and "index" meanings

When `dp[i]` means "the first `i` characters", element `i` of the string is **`s.charAt(i − 1)`**.

```java
// BUGGY — LCS with dp sized (n+1) x (m+1) but comparing the wrong characters
for (int i = 1; i <= n; i++)
    for (int j = 1; j <= m; j++)
        if (a.charAt(i) == b.charAt(j))        // StringIndexOutOfBounds at i == n
            dp[i][j] = dp[i - 1][j - 1] + 1;
```

```java
// FIXED
        if (a.charAt(i - 1) == b.charAt(j - 1))
            dp[i][j] = dp[i - 1][j - 1] + 1;
        else
            dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
```

**Tip:** write the state sentence as a comment above the array (`// dp[i][j] = LCS of a[0..i) and b[0..j)`) and check each `charAt` against it.

### 5.2 Returning the wrong cell

```java
// BUGGY — Min Cost Climbing Stairs: the "top" is step n, not n-1
return dp[n - 1];
```

```java
// FIXED
return dp[n];     // dp has n+1 cells; the top of the staircase is beyond the last step
```

### 5.3 Returning the last cell when the answer is the maximum over all cells

```java
// BUGGY — LIS: dp[i] is "longest ending AT i", so the last element need not be in the answer
return dp[n - 1];
```

```java
// FIXED
int best = 0;
for (int length : dp) best = Math.max(best, length);
return best;
```

Same trap in Kadane, Longest Common Substring, Maximal Square: any "ending at i" state needs a global max.

---

## 6. Memoization pitfalls

Background: [memoization](../00-foundations/06-memoization/), [converting recursion to memoization](../00-foundations/13-converting-recursion-to-memoization/).

### 6.1 Memo key missing a parameter

```java
// BUGGY — 0/1 knapsack memoized only on the item index
private static Map<Integer, Integer> memo = new HashMap<>();

static int best(int item, int capacity) {
    if (item == n || capacity == 0) return 0;
    if (memo.containsKey(item)) return memo.get(item);   // ignores capacity!
    int skip = best(item + 1, capacity);
    int take = weight[item] <= capacity ? value[item] + best(item + 1, capacity - weight[item]) : 0;
    memo.put(item, Math.max(skip, take));
    return memo.get(item);
}
```

```java
// FIXED — the key must contain EVERY parameter that changes
private static int[][] memo;   // memo[item][capacity]; -1 = not computed

static int best(int item, int capacity) {
    if (item == n || capacity == 0) return 0;
    if (memo[item][capacity] != -1) return memo[item][capacity];
    int skip = best(item + 1, capacity);
    int take = weight[item] <= capacity ? value[item] + best(item + 1, capacity - weight[item]) : 0;
    return memo[item][capacity] = Math.max(skip, take);
}
```

**Why:** the cached answer for `(2, cap=5)` is returned for `(2, cap=1)`. Rule: **the memo has exactly as many dimensions as the function has changing parameters.** A hidden parameter (a global variable the recursion mutates, like "holding a stock") is also a parameter.

### 6.2 Shared mutable memo across test cases

```java
// BUGGY — static memo filled during test 1 is reused for test 2 with different input
private static long[] memo = new long[1001];
static { Arrays.fill(memo, -1); }

static long countWays(int[] steps, int n) { ... uses memo ... }

public static void main(String[] args) {
    check("steps {1,2}, n=4", countWays(new int[]{1, 2}, 4), 5);
    check("steps {1,3}, n=4", countWays(new int[]{1, 3}, 4), 3);   // FAILS: returns 5 from cached values
}
```

```java
// FIXED — allocate (or reset) the memo for every new input
private static long[] memo;

static long countWays(int[] steps, int n) {
    memo = new long[n + 1];
    Arrays.fill(memo, -1);
    return solve(steps, n);
}
```

**Why:** a memo is valid only for the input it was computed on. This is the classic "passes alone, fails when run together" bug — also on LeetCode, where one `Solution` object may serve several test cases.

### 6.3 Memoizing on a mutable object

```java
// BUGGY — key is a List that is later modified; hashCode changes, lookups fail
memo.put(currentPath, result);
currentPath.add(next);
```

```java
// FIXED — keys are immutable values: ints, longs, or a String/record built from them
long key = ((long) index << 32) | remaining;
memo.put(key, result);
```

---

## 7. Sentinel values

A **sentinel** is a special value meaning "not computed yet" or "impossible". It must never collide with a real answer.

### 7.1 "Not computed" collides with a valid answer of 0

```java
// BUGGY — 0 means "not computed", but many subproblems truly have answer 0
if (memo[i][j] != 0) return memo[i][j];      // zero-answer states are recomputed forever → TLE
```

```java
// FIXED
// memo filled with -1 (answers are never negative), or use Integer[] / Long[] with null
if (memo[i][j] != -1) return memo[i][j];
```

### 7.2 "Not computed" collides with "impossible" (Coin Change returns −1)

```java
// BUGGY — -1 used for both "not computed" and "cannot make this amount"
static int fewest(int amount) {
    if (amount == 0) return 0;
    if (amount < 0) return -1;
    if (memo[amount] != -1) return memo[amount];       // impossible amounts look "not computed"
    int best = Integer.MAX_VALUE;
    for (int coin : coins) {
        int sub = fewest(amount - coin);
        if (sub >= 0) best = Math.min(best, sub + 1);
    }
    return memo[amount] = (best == Integer.MAX_VALUE) ? -1 : best;
}
```

```java
// FIXED — a separate marker that no real answer can take
private static final int UNKNOWN = -2;       // -1 = impossible, >= 0 = real answer
// Arrays.fill(memo, UNKNOWN);
if (memo[amount] != UNKNOWN) return memo[amount];
```

**Why:** impossible states were being recomputed on every visit, which brings back exponential time.

### 7.3 Infinity that is not big enough (or too big)

```java
// BUGGY — "infinity" smaller than real answers
int INF = 1000;      // but path sums can reach 200 * 200 * 100 = 4,000,000
```

```java
// FIXED — derive it from the constraints, with headroom for one addition
int INF = Integer.MAX_VALUE / 2;     // INF + any single cost stays positive
```

---

## 8. Overflow and infinity arithmetic

### 8.1 Adding to Integer.MAX_VALUE

```java
// BUGGY
Arrays.fill(dp, Integer.MAX_VALUE);
dp[0] = 0;
for (int a = 1; a <= amount; a++)
    for (int coin : coins)
        if (coin <= a) dp[a] = Math.min(dp[a], dp[a - coin] + 1);  // MAX_VALUE + 1 = MIN_VALUE!
```

```java
// FIXED — either guard the addition, or use a smaller infinity
for (int a = 1; a <= amount; a++)
    for (int coin : coins)
        if (coin <= a && dp[a - coin] != Integer.MAX_VALUE)
            dp[a] = Math.min(dp[a], dp[a - coin] + 1);
// or: Arrays.fill(dp, amount + 1);   (no answer can exceed `amount` coins)
```

### 8.2 −infinity plus a negative number

```java
// BUGGY — max path with possible negative cells; unreachable = MIN_VALUE
int NEG_INF = Integer.MIN_VALUE;
dp[r][c] = grid[r][c] + Math.max(dp[r - 1][c], dp[r][c - 1]);   // MIN_VALUE + (-5) wraps to a huge positive
```

```java
// FIXED
int NEG_INF = Integer.MIN_VALUE / 2;         // stays very negative after adding any cell value
// or skip unreachable predecessors explicitly:
int bestPrev = Math.max(dp[r - 1][c], dp[r][c - 1]);
dp[r][c] = (bestPrev == NEG_INF) ? NEG_INF : grid[r][c] + bestPrev;
```

**Why:** in Java, `int` arithmetic wraps around silently. `MIN_VALUE − 1` becomes `MAX_VALUE`, which then wins every `max`. Same issue in Cherry Pickup (thorns) and stock DP (`hold = −∞` before any buy).

### 8.3 Counting without modulo, or with modulo too late

```java
// BUGGY — counts exceed int (and even long) before the final % MOD
int[] ways = new int[n + 1];
...
ways[i] = ways[i - 1] + ways[i - 2] + ways[i - 3];
return ways[n] % 1_000_000_007;
```

```java
// FIXED — reduce after every addition; use long for intermediate products
private static final int MOD = 1_000_000_007;
long[] ways = new long[n + 1];
...
ways[i] = (ways[i - 1] + ways[i - 2] + ways[i - 3]) % MOD;   // sum of 3 values < 3·MOD fits in long
// products: (a * b) % MOD with a, b as long — int * int overflows before the %
```

### 8.4 Subtraction under a modulus

```java
// BUGGY — count(R) - count(L-1) can be negative after both were reduced mod MOD
return (countUpTo(R) - countUpTo(L - 1)) % MOD;
```

```java
// FIXED
return ((countUpTo(R) - countUpTo(L - 1)) % MOD + MOD) % MOD;
```

---

## 9. Modelling mistakes

### 9.1 Assuming greedy works

```java
// BUGGY — "always take the largest coin" for fewest coins
Arrays.sort(coins);
int count = 0;
for (int i = coins.length - 1; i >= 0; i--) {
    count += amount / coins[i];
    amount %= coins[i];
}
return amount == 0 ? count : -1;       // coins {1,3,4}, amount 6 → 3 (4+1+1); correct is 2 (3+3)
                                        // coins {2,3},   amount 4 → -1 (3, then 1 left); correct is 2 (2+2)
```

```java
// FIXED — DP over amounts
int[] fewest = new int[amount + 1];
Arrays.fill(fewest, amount + 1);
fewest[0] = 0;
for (int a = 1; a <= amount; a++)
    for (int coin : coins)
        if (coin <= a) fewest[a] = Math.min(fewest[a], fewest[a - coin] + 1);
return fewest[amount] > amount ? -1 : fewest[amount];
```

**Habit:** before committing to greedy, spend two minutes trying to break it with 3-element inputs. See [Section 6 of the recognition guide](DP_PATTERN_RECOGNITION.md#6-greedy-vs-recursion-vs-backtracking-vs-dp).

### 9.2 State too small (forgetting a flag)

```java
// BUGGY — Maximum Product Subarray tracking only the max
int maxEnding = nums[0], best = nums[0];
for (int i = 1; i < nums.length; i++) {
    maxEnding = Math.max(nums[i], maxEnding * nums[i]);
    best = Math.max(best, maxEnding);
}                                       // [-2, 3, -4] → 3, correct is 24
```

```java
// FIXED — a negative number turns the smallest product into the largest
int maxEnding = nums[0], minEnding = nums[0], best = nums[0];
for (int i = 1; i < nums.length; i++) {
    int x = nums[i];
    int candidateMax = Math.max(x, Math.max(maxEnding * x, minEnding * x));
    int candidateMin = Math.min(x, Math.min(maxEnding * x, minEnding * x));
    maxEnding = candidateMax;
    minEnding = candidateMin;
    best = Math.max(best, maxEnding);
}
```

### 9.3 Circular input treated as linear

```java
// BUGGY — House Robber II: first and last house are neighbours
return robLine(nums, 0, n - 1);
```

```java
// FIXED — either skip the first house or skip the last one
if (n == 1) return nums[0];
return Math.max(robLine(nums, 0, n - 2), robLine(nums, 1, n - 1));
```

### 9.4 Interval DP choosing the *first* operation instead of the *last*

In Burst Balloons, if `k` is burst **first** in `(l, r)`, its neighbours afterwards are unknown, so the two halves are not independent. Choosing `k` as the **last** balloon in `(l, r)` fixes its neighbours to `l` and `r`. See [burst balloons](../04-advanced-dp-patterns/05-burst-balloons/).

---

## 10. Performance and runtime errors

### 10.1 StackOverflowError on deep recursion

```java
// BUGGY — memoized recursion with depth n = 100,000
static long solve(int i) { ... return memo[i] = solve(i - 1) + ...; }
```

```java
// FIXED — convert to tabulation (no recursion), or run in a thread with a bigger stack
long[] dp = new long[n + 1];
for (int i = 1; i <= n; i++) dp[i] = dp[i - 1] + ...;
// alternative: new Thread(null, () -> solve(n), "main", 1 << 27).start();
```

### 10.2 Using `HashMap<String, …>` keys built by concatenation in a hot loop

```java
// SLOW — builds a new String for every lookup
String key = i + "," + j + "," + k;
```

```java
// FAST — plain arrays when ranges are known
int[][][] memo = new int[n][m][2];
```

### 10.3 Allocating a full 2D table when only one row is needed

`new int[5000][5000]` = 25 million ints ≈ 100 MB. If row `i` only reads row `i−1`, keep two rows. See [rolling arrays](../05-dp-optimization/02-rolling-arrays/).

### 10.4 Recomputing an O(n) quantity inside the transition

```java
// SLOW — range sum recomputed for every (i, j, k): O(n^4)
for (...) cost = sum(a, i, j);
```

```java
// FAST — prefix sums: sum(i..j) = prefix[j + 1] - prefix[i], O(1)
```

---

## Debugging checklist

Run through this list when a DP gives a wrong answer:

```
[ ] 1. State sentence written as a comment? Does every index usage match it?
[ ] 2. Base case: does it match the meaning? (count → 1 for empty, min → 0, impossible → ±INF)
[ ] 3. Loop order: does each cell read only cells already filled?
[ ] 4. Knapsack 1D: 0/1 → descending, unbounded → ascending?
[ ] 5. Counting: combinations (items outer) or permutations (amount outer)?
[ ] 6. Memo: key contains every changing parameter? Reset per test case?
[ ] 7. Sentinels: "not computed" ≠ 0 ≠ "impossible"?
[ ] 8. Overflow: INF + x safe? MIN_VALUE + x safe? % MOD after every add, long for products?
[ ] 9. Answer: dp[n] or dp[n-1]? Or the max over all cells?
[ ] 10. Hand-trace the smallest input (n = 1) and the example from the statement.
```

Then compare against brute force on random small inputs — the most reliable bug-finder there is.

---

**See also:** [RECURRENCE_CHEATSHEET.md](RECURRENCE_CHEATSHEET.md) · [INTERVIEW_STRATEGY.md](INTERVIEW_STRATEGY.md) · [module README](README.md)
