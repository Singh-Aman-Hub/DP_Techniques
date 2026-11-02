# Target Sum — Walkthrough

## 1. Intuition (no code, no math — an everyday analogy)

You are walking along a straight corridor with five steps to take, each exactly 1 metre long. Before each step you choose to walk **forward** or **backward**. In how many ways can you end up exactly 3 metres ahead of where you started?

Think about it without listing all 32 patterns: to end at +3 after 5 steps, the forward steps minus the backward steps must be 3, and forward plus backward must be 5. So 4 forward and 1 backward. The only freedom is *which* of the 5 steps is the backward one: **5 ways**.

That little calculation is the whole trick of this topic. "Signs that add up to a target" sounds like it needs you to track a running position that can be negative. But two equations — *difference = target* and *sum = total* — tell you exactly how big the "forward" group must be. Then you only need to **count the groups of that size** (here, groups of numbers summing to 4), which is the counting DP from [topic 01](../01-subsequences-fundamentals/).

## 2. Problem statement in our own words

Given an array `nums` of non-negative integers and an integer `target` (possibly negative), place either `+` or `−` before every number. Count the sign patterns whose expression evaluates to `target`.

Original: [Target Sum — LeetCode 494](https://leetcode.com/problems/target-sum/). Constraints: `1 ≤ n ≤ 20`, `0 ≤ nums[i] ≤ 1000`, `sum(nums) ≤ 1000`, `−1000 ≤ target ≤ 1000`.

## 3. Examples (input → output, with an explanation)

Running example: **`nums = [1, 1, 1, 1, 1]`, `target = 3`.**

| Input | Output | Explanation |
|---|---|---|
| `[1,1,1,1,1]`, `3` | 5 | choose which one of the five gets `−`: `−1+1+1+1+1`, `+1−1+1+1+1`, … |
| `[1,1,1,1,1]`, `−3` | 5 | mirror image: choose which one gets `+` |
| `[1, 2, 1]`, `0` | 2 | `+1 −2 +1` and `−1 +2 −1` |
| `[0, 0, 1]`, `1` | 4 | `±0 ±0 +1`: each zero can take either sign without changing the value |
| `[2]`, `1` | 0 | only `+2` or `−2` |
| `[100]`, `−200` | 0 | even `−100` does not reach `−200` |
| `[1]`, `1` | 1 | `+1` |

## 4. Deriving the solution — the 7 questions

We derive two DPs. **View A** follows the problem literally. **View B** uses algebra first.

### Q1. What is changing?

- View A: the index `i` of the next number to sign, and the **running sum** of the signed numbers so far (anywhere from `−total` to `+total`).
- View B: the index `i`, and the sum `s` of the numbers chosen for the `+` group.

### Q2. What does the state represent?

- View A: `count(i, runningSum)` = the number of ways to sign `nums[i..n-1]` so that the final value is `target`, given that the first `i` signed numbers add up to `runningSum`.
- View B: `dp[i][s]` = the number of subsets of the first `i` numbers whose sum is exactly `s`.

### The algebra behind View B

Let `P` be the set of numbers with a `+` and `N` the set with a `−`; write `s1 = sum(P)`, `s2 = sum(N)`. Then:

```
s1 − s2 = target        (the expression)
s1 + s2 = total         (every number is in exactly one group)
───────────────────
2·s1    = total + target
s1      = (total + target) / 2
```

Every sign pattern is exactly one choice of the `+` group `P`, and `P` must sum to `s1`. So:

> **answer = number of subsets of `nums` with sum `(total + target) / 2`.**

Two checks come **before** the DP:

1. `|target| > total` → 0. The expression lies between `−total` (all minus) and `+total` (all plus). This also guarantees `0 ≤ s1 ≤ total`, so `s1` is a valid array index.
2. `total + target` odd → 0. `s1` must be a whole number.

(Symmetrically, `s2 = (total − target) / 2`; counting subsets with sum `s2` gives the same answer, and you may pick whichever of `s1`, `s2` is smaller to make the table narrower.)

### Q3. What are the choices?

- View A: give `nums[i]` a `+` (`runningSum + nums[i]`) or a `−` (`runningSum − nums[i]`).
- View B: put `nums[i-1]` into `P` (take) or into `N` (skip).

### Q4. What is the recurrence?

```
View A:  count(i, r) = count(i + 1, r + nums[i]) + count(i + 1, r − nums[i])

View B:  dp[i][s] = dp[i-1][s] + dp[i-1][s − nums[i-1]]      (second term only if nums[i-1] ≤ s)
```

`+` because different sign patterns are different ways and we are counting all of them.

### Q5. What are the base cases?

- View A: `count(n, r) = 1` if `r == target`, else 0.
- View B: `dp[0][0] = 1` (the empty subset), `dp[0][s>0] = 0`.

**Zeros.** In View B the `s` loop must start at **0**: for `nums[i-1] = 0`, the take term at `s = 0` is `dp[i-1][0]`, which doubles the count (`+0` and `−0` are different patterns). And never use a recursive base case like "`remaining == 0` → return 1" *before* reaching the end of the array: it would stop early and miss the zeros after that point.

### Q6. What is the order of computation?

- View A: memoized recursion; the call for `i` needs `i + 1`.
- View B: rows top to bottom (row `i` needs row `i − 1`); in 1-D, `s` from `s1` **down** to `nums[i]` (each number used once, as in 0/1 knapsack).

### Q7. Can the state space be optimized?

- View A has `n × (2·total + 1)` states.
- View B needs only `(n + 1) × (s1 + 1)`, and one row of `s1 + 1` integers in 1-D. View B is the one to submit.

## 5. Brute force: recursion

```java
private static int count(int[] nums, int i, int runningSum, int target) {
    if (i == nums.length) return (runningSum == target) ? 1 : 0;
    int plus  = count(nums, i + 1, runningSum + nums[i], target);
    int minus = count(nums, i + 1, runningSum - nums[i], target);
    return plus + minus;
}
```

Full code: [`solution/BruteForce.java`](solution/BruteForce.java). With `n ≤ 20` this is about 10^6 leaves — it actually passes on LeetCode, but it is the slowest option.

The first three levels for `[1, 1, 1, 1, 1]`, labelled `count(i, runningSum)`:

```
                                  (0, 0)
                       + /                     \ −
                      (1, 1)                    (1, −1)
                   + /     \ −               + /       \ −
                (2, 2)     (2, 0)         (2, 0)        (2, −2)
               /    \      /    \         /    \        /     \
           (3,3) (3,1) (3,1) (3,−1)   (3,1) (3,−1)  (3,−1)  (3,−3)
```

`(2, 0)` appears twice ("+1 −1" and "−1 +1"), and `(3, 1)` three times. Each level `i` has only `i + 1` different running sums but `2^i` nodes. That gap is what memoization removes.

## 6. Memoization (top-down) — with an offset

`runningSum` lies in `−total … +total`, but Java array indices start at 0. Shift every sum by `offset = total`:

```
runningSum:  −5  −4  −3  −2  −1   0   1   2   3   4   5
column:       0   1   2   3   4   5   6   7   8   9  10        column = runningSum + 5
```

```java
private static Integer[][] memo;        // memo[i][runningSum + offset]; null = not computed
private static int offset;

static int findTargetSumWays(int[] nums, int target) {
    int total = 0;
    for (int x : nums) total += x;
    if (Math.abs(target) > total) return 0;
    offset = total;
    memo = new Integer[nums.length][2 * total + 1];
    return count(nums, 0, 0, target);
}

private static int count(int[] nums, int i, int runningSum, int target) {
    if (i == nums.length) return (runningSum == target) ? 1 : 0;
    if (memo[i][runningSum + offset] != null) return memo[i][runningSum + offset];
    int plus  = count(nums, i + 1, runningSum + nums[i], target);
    int minus = count(nums, i + 1, runningSum - nums[i], target);
    memo[i][runningSum + offset] = plus + minus;
    return plus + minus;
}
```

Full code: [`solution/Memoization.java`](solution/Memoization.java).

**Dry run** on `[1,1,1,1,1]`, `target = 3` (values worked out bottom-up along the first path, then reused):

| State `(i, r)` | Meaning: remaining ones / needed extra | Value | How |
|---|---|---|---|
| `(4, 2)` | 1 one left, need +1 | 1 | `(5,3)=1` + `(5,1)=0` |
| `(4, 4)` | 1 left, need −1 | 1 | `(5,5)=0` + `(5,3)=1` |
| `(3, 3)` | 2 left, need 0 | 2 | `(4,4)` + `(4,2)` = 1 + 1 |
| `(3, 1)` | 2 left, need +2 | 1 | `(4,2)` + `(4,0)` = 1 + 0 |
| `(2, 2)` | 3 left, need +1 | 3 | `(3,3)` + `(3,1)` = 2 + 1 |
| `(2, 0)` | 3 left, need +3 | 1 | `(3,1)` + `(3,−1)` = 1 + 0 |
| `(1, 1)` | 4 left, need +2 | 4 | `(2,2)` + `(2,0)` = 3 + 1 |
| `(1, −1)` | 4 left, need +4 | 1 | `(2,0)` **cache hit** = 1, + `(2,−2)` = 0 |
| `(0, 0)` | the answer | **5** | `(1,1)` + `(1,−1)` = 4 + 1 |

The memo has `n × (2·total + 1) = 5 × 11 = 55` slots, and only 15 of them are ever filled (level `i` has `i + 1` reachable sums). For such a tiny input that is no better than the 32 leaves of the tree, but the gap grows fast: with `n = 20` and `total = 1000` the memo has `20 × 2001 ≈ 4·10^4` slots while the tree has `2^20 ≈ 10^6` leaves.

## 7. Tabulation (bottom-up) — View B

For `[1,1,1,1,1]`, `target = 3`: `total = 5`, `|3| ≤ 5` ✔, `5 + 3 = 8` even ✔, so `s1 = 4`. Count the subsets with sum 4.

```java
int[][] dp = new int[n + 1][s1 + 1];
dp[0][0] = 1;
for (int i = 1; i <= n; i++) {
    int value = nums[i - 1];
    for (int s = 0; s <= s1; s++) {               // s starts at 0 — needed for zeros
        dp[i][s] = dp[i - 1][s];
        if (value <= s) dp[i][s] += dp[i - 1][s - value];
    }
}
return dp[n][s1];
```

Full code: [`solution/Tabulation.java`](solution/Tabulation.java).

### Table, step by step

Each row adds one more `1`. `dp[i][s] = dp[i-1][s] + dp[i-1][s-1]` — the numbers of Pascal's triangle.

```
              s:  0   1   2   3   4
i=0  {}           1   0   0   0   0
i=1  {1}          1   1   0   0   0      dp[1][1] = 0 + 1
i=2  {1,1}        1   2   1   0   0      dp[2][1] = 1 + 1,  dp[2][2] = 0 + 1
i=3  {1,1,1}      1   3   3   1   0
i=4  {1,1,1,1}    1   4   6   4   1
i=5  all five     1   5  10  10   5      ← dp[5][4] = 5
```

`dp[5][4] = dp[4][4] + dp[4][3] = 1 + 4 = 5`: the subset of four ones either skips the fifth one (1 way: the first four) or takes it (4 ways to pick the other three from the first four).

### The zeros example: `[0, 0, 1]`, `target = 1`

`total = 1`, `s1 = (1 + 1)/2 = 1`.

```
              s:  0   1
i=0  {}           1   0
i=1  {0}          2   0     dp[1][0] = dp[0][0] + dp[0][0−0] = 1 + 1   ← "+0" and "−0"
i=2  {0,0}        4   0     doubles again
i=3  {0,0,1}      4   4     dp[3][1] = dp[2][1] + dp[2][0] = 0 + 4
```

Answer 4 ✔. If the `s` loop started at 1, the zero rows would never double and you would get 1.

## 8. Space optimization

One counting array, backward loop:

```java
int[] ways = new int[s1 + 1];
ways[0] = 1;
for (int value : nums) {
    for (int s = s1; s >= value; s--) {
        ways[s] += ways[s - value];
    }
}
return ways[s1];
```

Full code: [`solution/Optimized.java`](solution/Optimized.java).

For `value = 0` the loop runs down to `s = 0` and does `ways[s] += ways[s]` — every count doubles, exactly the `+0/−0` choice. (Going backward matters for positive values, as in [0/1 Knapsack](../02-zero-one-knapsack/walkthrough.md#8-space-optimization); for 0 the direction makes no difference because it reads the same cell it writes.) The test with eight zeros and a 1 checks that the answer is `2^8 = 256`.

## 9. Dry run on a full example

One array, `[1,1,1,1,1]`, `s1 = 4`. The loop runs `s = 4, 3, 2, 1` for each number.

| After number # | `ways[0..4]` |
|---|---|
| start | `1 0 0 0 0` |
| 1 | `1 1 0 0 0` |
| 2 | `1 2 1 0 0` |
| 3 | `1 3 3 1 0` |
| 4 | `1 4 6 4 1` |
| 5 | `1 5 10 10 5` |

Detail of the last pass (backward, so each read is still the previous row):

```
s=4: ways[4] = 1 + ways[3]=4  → 5
s=3: ways[3] = 4 + ways[2]=6  → 10
s=2: ways[2] = 6 + ways[1]=4  → 10
s=1: ways[1] = 4 + ways[0]=1  → 5
```

Answer `ways[4] = 5`. Brute force, offset memo, table and one array all return 5 (and 4 for the zeros example).

## 10. Complexity analysis (explained, for every approach)

| Approach | Time | Space | Why |
|---|---|---|---|
| Brute force | `O(2^n)` | `O(n)` | two signs per number |
| Offset memo (View A) | `O(n · total)` | `O(n · total)` | `n × (2·total + 1)` states, `O(1)` each |
| Table (View B) | `O(n · s1)` | `O(n · s1)` | `(n+1)(s1+1)` cells; `s1 ≤ total` |
| One array (View B) | `O(n · s1)` | `O(s1)` | one row of `s1 + 1` counts |

View B is about twice as narrow as View A in the worst case (`s1 ≤ total` versus `2·total + 1` columns), and often much narrower.

Overflow: the count is at most `2^20 ≈ 10^6` here, so `int` is safe. In counting problems with larger `n` (like [Two Sets II](https://cses.fi/problemset/task/1093)) the problem asks for the answer modulo `10^9 + 7`; then do `ways[s] = (ways[s] + ways[s - value]) % MOD`.

## 11. Why this approach works (correctness argument in plain words)

There is a one-to-one match between sign patterns and subsets: the subset is "the numbers with a `+`". A pattern evaluates to `target` exactly when its `+` subset sums to `(total + target)/2` (Section 4 algebra, which works in both directions). So counting the right patterns is the same as counting subsets with that sum. The counting table counts each subset exactly once, because each subset is described by one unique sequence of take/skip decisions. The two pre-checks only return 0 in cases where no pattern can exist.

## 12. Common mistakes

1. **Missing the range check**: `target = −200`, `total = 100` gives `s1 = −50` — a negative array size or index.
2. **Missing the parity check**: `(total + target)/2` silently rounds down and counts subsets of a wrong sum.
3. **Starting the `s` loop at 1** (or the 1-D loop stopping at `s ≥ 1`): zeros are no longer doubled.
4. **"remaining == 0 → return 1" base case** in a recursive subset count: stops before trailing zeros, undercounting.
5. **Offset memo with the wrong width**: you need `2·total + 1` columns (from `−total` to `+total`), not `total + 1`.
6. **Using `abs(target)` but forgetting the sign elsewhere**: the counts for `target` and `−target` are equal (flip every sign), so using `|target|` is fine — but be consistent.

## 13. When to use this technique / how to recognize similar problems

- **Assign `+`/`−` (or "group A / group B") to every item and hit a target** → write the two equations, reduce to "count subsets with sum `(total + target)/2`".
- **A running quantity that can go negative** in a DP state → add an offset; the width is (max − min + 1).
- **Count, don't optimize** → same table, `+` instead of `max` or `||`; think about zeros and about a modulus.

Close relatives: [Number of Dice Rolls With Target Sum](https://leetcode.com/problems/number-of-dice-rolls-with-target-sum/) (counting on `(index, running sum)`), [Two Sets II](https://cses.fi/problemset/task/1093) (target = 0 on `1..n`), and next, [Coin Change II](../09-coin-change-ii/) (counting with unlimited items).

## 14. Summary and revision notes

- `s1 − s2 = target`, `s1 + s2 = total` ⇒ `s1 = (total + target) / 2`.
- Return 0 if `|target| > total` or `total + target` is odd.
- Count subsets with sum `s1`: `dp[0][0] = 1`, combine with `+`, `s` loop from 0 (zeros double).
- Direct alternative: memo on `(i, runningSum)` with `offset = total`, `2·total + 1` columns.
- Time `O(n · total)`, space `O(s1)` with one array.

```
if |target| > total or (total + target) is odd:  return 0
s1 = (total + target) / 2
ways[0] = 1
for each x:  for s = s1 down to x:  ways[s] += ways[s - x]
return ways[s1]
```

## Exercises

1. Count the sign patterns for `nums = [2, 1, 1]`, `target = 2` using View B. Show `s1` and the 1-D array after each number.

<details>
<summary>Answer</summary>

`total = 4`, `s1 = (4 + 2)/2 = 3`. Start `[1,0,0,0]`. After 2: `[1,0,1,0]`. After 1: `[1,1,1,1]`. After 1: `[1,2,2,2]`. Answer `ways[3] = 2`: `{2, 1a}` and `{2, 1b}`, i.e. `+2 +1 −1` and `+2 −1 +1`.
</details>

2. For `nums = [5, 3]`, which targets give a non-zero answer? Use only the two pre-checks and the subset sums.

<details>
<summary>Answer</summary>

`total = 8`. Subsets sums `s1 ∈ {0, 3, 5, 8}` give `target = 2·s1 − total ∈ {−8, −2, 2, 8}`, each with exactly 1 way. Every other target is 0 (for example `target = 1`: `8 + 1` is odd; `target = 4`: `s1 = 6` is not reachable).
</details>

3. In View A, how many columns does the memo need for `nums = [3, 0, 4]`, and which column stores `runningSum = −3`?

<details>
<summary>Answer</summary>

`total = 7`, so `2·7 + 1 = 15` columns (sums `−7 … 7`), `offset = 7`. `runningSum = −3` is stored in column `−3 + 7 = 4`.
</details>

4. Explain in one sentence why `[0, 0, 1]` with `target = 1` has 4 answers, and what a buggy solution that ignores zeros would return.

<details>
<summary>Answer</summary>

Each zero can be `+0` or `−0` independently without changing the value, so the single way to reach 1 with the `1` is multiplied by `2 × 2 = 4`; a solution whose sum loop starts at 1 (or that stops recursing as soon as the remaining sum is 0) returns 1.
</details>
