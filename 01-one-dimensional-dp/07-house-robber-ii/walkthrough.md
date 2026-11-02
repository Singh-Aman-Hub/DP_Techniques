# House Robber II — Walkthrough

## 1. Intuition

The houses now stand around a roundabout. House 0's left neighbour is house 1, and its right neighbour is the **last** house. Everything else is as before.

Why can't we just run [House Robber](../06-house-robber/)? Because the line DP decides about house `n-1` by looking only at houses `n-2` and `n-3`. It has no idea whether house 0 was robbed, which now matters.

Here is the key observation. In any legal plan, houses 0 and `n-1` are neighbours, so **at least one of them is not robbed.** So every legal plan is in (at least) one of two groups:

```
Group A: house n-1 is NOT robbed        Group B: house 0 is NOT robbed
     ┌───┐                                   ┌───┐
   ┌─┤ 0 ├─┐                               ┌─┤ ✗ ├─┐
  ┌┴┐└───┘┌┴┐                             ┌┴┐└───┘┌┴┐
  │✗│     │1│                             │4│     │1│
  └┬┘     └┬┘                             └┬┘     └┬┘
  ┌┴┐     ┌┴┐                             ┌┴┐     ┌┴┐
  │3│─────│2│                             │3│─────│2│
  └─┘     └─┘                             └─┘     └─┘
  cut the circle at house 4:              cut the circle at house 0:
  a straight line 0,1,2,3                 a straight line 1,2,3,4
```

Removing a house that we already know is skipped **cuts the circle open** into a straight line, and on a straight line we already know the answer. Solve both lines, keep the better result.

## 2. Problem statement in our own words

`nums[i] ≥ 0` is the money in house `i`, and the houses form a circle (house `n-1` is next to house 0). Rob a set of houses with no two neighbours (including the pair `0`, `n-1`), and return the maximum total.

Original: [LeetCode 213 · House Robber II](https://leetcode.com/problems/house-robber-ii/) (there `1 ≤ n ≤ 100`).

## 3. Examples

| `nums` (circle) | Output | Plan | Note |
|-----------------|--------|------|------|
| `[2, 3, 2]` | 3 | rob house 1 | 2 + 2 is illegal: houses 0 and 2 are neighbours |
| `[1, 2, 3, 1]` | 4 | houses 0, 2 | same as the line answer |
| `[200, 3, 140, 20, 10]` | 340 | houses 0, 2 | the plan avoids the last house |
| `[5]` | 5 | house 0 | one house has no neighbours |
| `[2, 7, 9, 3, 1]` | 11 | houses 0, 2 | the line answer 12 used houses 0 and 4, now illegal |

**Running example: `nums = [2, 7, 9, 3, 1]`, answer `11`.** It is the same input as the [House Robber walkthrough](../06-house-robber/walkthrough.md), where the answer was 12 by robbing houses {0, 2, 4}. In a circle, 0 and 4 are neighbours, so that plan is no longer allowed.

## 4. Deriving the solution — the 7 questions

The plan has two layers: an outer **case split**, and the ordinary line DP inside each case.

### Outer layer: why two cases are enough

Let `line(lo, hi)` be the House Robber answer on the straight street `nums[lo..hi]`. We claim:

```
circle answer = max( line(0, n-2), line(1, n-1) )        for n ≥ 2
```

We must check two things.

**1. Cover — the best circular plan is found.** Take the best legal circular plan `P`. Houses 0 and `n-1` are neighbours, so `P` does not rob both.
- If `P` skips house `n-1`, every house it robs is in `0..n-2`, and no two of them are neighbours on that line. So `P` is a legal plan for `line(0, n-2)`, and `line(0, n-2) ≥ value(P)`.
- Otherwise `P` skips house 0, and in the same way `line(1, n-1) ≥ value(P)`.

So the max of the two runs is **at least** the true answer.

**2. Soundness — nothing illegal is counted.** Any plan found by `line(0, n-2)` never robs house `n-1`, so the wrap-around pair (0, `n-1`) can never both be robbed, and all other neighbour pairs are already respected by the line DP. It is a legal circular plan. The same holds for `line(1, n-1)`, which never robs house 0.

So the max of the two runs is **at most** the true answer. At least and at most → equal.

**What about plans that skip both ends?** They belong to both groups and are considered twice. For a **maximum** that is harmless: `max(x, x) = x`. For a **counting** version ("how many legal plans?") it would count those plans twice; there you would need `count(0..n-2) + count(1..n-1) − count(1..n-2)` (inclusion–exclusion). Always ask whether your cases overlap and whether that matters for the operation you use.

**Why not three cases** ("rob 0", "rob n-1", "rob neither")? You could, but it's unnecessary: "skip `n-1`" already contains both "rob 0" and "rob neither".

### Inner layer: the line DP on `nums[lo..hi]`

#### Q1. What is changing?
The last house `i` still to decide, with `lo ≤ i ≤ hi`.

#### Q2. What does the state represent?
> `best(i)` = maximum loot from houses `lo..i` of the current straight street.

#### Q3. What are the choices?
Skip house `i`, or rob it (then skip `i-1`).

#### Q4. What is the recurrence?
```
best(i) = max( best(i-1), nums[i] + best(i-2) )
```

#### Q5. What are the base cases?
`best(i) = 0` for `i < lo` (no houses of this street left). Note the boundary is `lo`, not 0: in the run on `1..n-1`, house 0 does not exist.

#### Q6. What is the order of computation?
Left to right from `lo` to `hi`.

#### Q7. Can the state space be optimized?
Two variables per run → `O(1)` space overall.

### Special case `n = 1`

With one house, the two ranges are `0..-1` and `1..0`, both empty, and the formula gives 0. But one house has no neighbour (it is not next to itself), so the answer is `nums[0]`. Handle it before the split. (With `n = 2`, the ranges are `[0]` and `[1]`, giving `max(nums[0], nums[1])`, which is correct.)

## 5. Brute force: recursion

```java
static int rob(int[] nums) {
    int n = nums.length;
    if (n == 1) return nums[0];
    return Math.max(robLine(nums, 0, n - 2), robLine(nums, 1, n - 1));
}

private static int robLine(int[] nums, int lo, int i) {
    if (i < lo) return 0;
    return Math.max(robLine(nums, lo, i - 1), nums[i] + robLine(nums, lo, i - 2));
}
```
Full file: [`solution/BruteForce.java`](solution/BruteForce.java).

### The two recursion trees for `[2, 7, 9, 3, 1]`

```
Run A: houses 0..3 = [2, 7, 9, 3]                Run B: houses 1..4 = [7, 9, 3, 1]
           A(3) [11]                                      B(4) [10]
      skip ┌──┴──┐ rob +3                            skip ┌──┴──┐ rob +1
        A(2)    A(1)                                   B(3)     B(2)
        [11]    [7]                                    [10]     [9]
       ┌─┴─┐    ┌─┴─┐                                 ┌─┴─┐    ┌─┴─┐
     A(1) A(0) A(0) A(-1)                          B(2) B(1) B(1) B(0)
      …                                              …              ← B(0) is "before lo=1": 0
```

Each run is the exponential House Robber recursion on `n-1` houses, so the total is still `O(2^n)`.

## 6. Memoization (top-down)

The memo must be **reset between the two runs**. In run A, `memo[2]` means "best from houses 0..2"; in run B, it means "best from houses 1..2". Same index, different question. Reusing the cache would mix answers to different subproblems.

```java
private static int robRange(int[] nums, int lo, int hi) {
    memo = new int[nums.length];          // fresh memo per run
    Arrays.fill(memo, -1);
    return robLine(nums, lo, hi);
}

private static int robLine(int[] nums, int lo, int i) {
    if (i < lo) return 0;
    if (memo[i] != -1) return memo[i];
    memo[i] = Math.max(robLine(nums, lo, i - 1), nums[i] + robLine(nums, lo, i - 2));
    return memo[i];
}
```
Full file: [`solution/Memoization.java`](solution/Memoization.java).

This is a useful general lesson: **the memo key must include everything the answer depends on.** Here the answer depends on `i` *and* `lo`. Since `lo` takes only two values, resetting the memo per run is simpler than a 2D memo.

## 7. Tabulation (bottom-up)

```java
private static int robRange(int[] nums, int lo, int hi) {
    int len = hi - lo + 1;
    int[] best = new int[len];        // best[k] = max loot from houses lo..lo+k
    for (int k = 0; k < len; k++) {
        int skip = (k >= 1) ? best[k - 1] : 0;
        int robIt = nums[lo + k] + ((k >= 2) ? best[k - 2] : 0);
        best[k] = Math.max(skip, robIt);
    }
    return best[len - 1];
}
```
Full file: [`solution/Tabulation.java`](solution/Tabulation.java). Indexing the table by position *inside the range* (`k`) keeps the base-case guards identical to House Robber.

### Both tables for `[2, 7, 9, 3, 1]`

```
Run A: houses 0..3                       Run B: houses 1..4
 house    0    1    2    3                house    1    2    3    4
 money    2    7    9    3                money    7    9    3    1
 best     2    7   11   11                best     7    9   10   10

 k=0: max(0, 2)      = 2                  k=0: max(0, 7)       = 7
 k=1: max(2, 7)      = 7                  k=1: max(7, 9)       = 9
 k=2: max(7, 9+2)    = 11                 k=2: max(9, 3+7)     = 10
 k=3: max(11, 3+7)   = 11                 k=3: max(10, 1+9)    = 10

answer = max(11, 10) = 11   (run A: houses 0 and 2 → 2 + 9)
```

## 8. Space optimization

Each run is the two-variable House Robber loop over a sub-range:

```java
private static int robRange(int[] nums, int lo, int hi) {
    int lootTwoBack = 0, lootOneBack = 0;
    for (int i = lo; i <= hi; i++) {
        int current = Math.max(lootOneBack, nums[i] + lootTwoBack);
        lootTwoBack = lootOneBack;
        lootOneBack = current;
    }
    return lootOneBack;
}
```
Full file: [`solution/Optimized.java`](solution/Optimized.java). Writing `robRange(lo, hi)` once and calling it twice is cleaner than copying arrays with `Arrays.copyOfRange`, and uses no extra memory.

## 9. Dry run on a full example

`nums = [200, 3, 140, 20, 10]`, optimized version.

Run A (houses 0..3 = `[200, 3, 140, 20]`):

| house | money | skip | rob | current | twoBack | oneBack |
|-------|-------|------|-----|---------|---------|---------|
| start | — | — | — | — | 0 | 0 |
| 0 | 200 | 0 | 200 | 200 | 0 | 200 |
| 1 | 3 | 200 | 3 | 200 | 200 | 200 |
| 2 | 140 | 200 | 340 | 340 | 200 | 340 |
| 3 | 20 | 340 | 220 | 340 | 340 | 340 |

Run B (houses 1..4 = `[3, 140, 20, 10]`):

| house | money | skip | rob | current | twoBack | oneBack |
|-------|-------|------|-----|---------|---------|---------|
| start | — | — | — | — | 0 | 0 |
| 1 | 3 | 0 | 3 | 3 | 0 | 3 |
| 2 | 140 | 3 | 140 | 140 | 3 | 140 |
| 3 | 20 | 140 | 23 | 140 | 140 | 140 |
| 4 | 10 | 140 | 150 | 150 | 140 | 150 |

Answer `max(340, 150) = 340`.

## 10. Complexity analysis

| Approach | Time | Space | Why |
|----------|------|-------|-----|
| Brute force | `O(2^n)` | `O(n)` | Two exponential line recursions on `n-1` houses. |
| Memoization | `O(n)` | `O(n)` | Two runs × `(n-1)` states × `O(1)`. |
| Tabulation | `O(n)` | `O(n)` | Two linear passes; one table at a time. |
| Optimized | `O(n)` | `O(1)` | Two passes with two variables each. |

Running the DP twice doubles the work, but `2 × O(n)` is still `O(n)`.

## 11. Why this approach works

The full argument is in Section 4 ("Cover" and "Soundness"). In one sentence: every legal circular plan skips at least one end, so it is a legal plan of one of the two lines; and every plan of either line skips an end, so it is a legal circular plan. The best over both lines is therefore exactly the best circular plan.

## 12. Common mistakes

1. **Forgetting `n = 1`.** Both ranges are empty and you return 0 instead of `nums[0]`.
2. **Running the line DP on the whole array and "fixing" the result**, e.g. subtracting `min(nums[0], nums[n-1])` when both ends were robbed. The best plan may be completely different once an end is forbidden.
3. **Sharing the memo between runs.** Answers for the same index mean different things in the two runs.
4. **Base case `i < 0` instead of `i < lo`** in the run on `1..n-1`: the recursion would wander into house 0, which was supposed to be excluded.
5. **Applying the same split to a counting problem** without subtracting the overlap.

## 13. When to use this technique / how to recognize similar problems

Signals: "arranged in a circle", "the first and last are adjacent", "the array is circular".

Method:
1. Find a decision about one position that, once fixed, turns the circle into a line (here: "is the last/first house skipped?").
2. List cases that together cover all solutions.
3. Run the line DP on each case; combine with `max`/`min` (or with inclusion–exclusion for counting).

You will see a variation of this idea in [Maximum Subarray](../09-maximum-subarray-kadane-vs-dp/), where the circular version ([LeetCode 918](https://leetcode.com/problems/maximum-sum-circular-subarray/)) splits into "the best subarray does not wrap" and "it wraps".

## 14. Summary and revision notes

- Circle = line + one extra neighbour pair (first, last).
- At least one end is always skipped → answer `max(line(0..n-2), line(1..n-1))`.
- Cover + soundness prove it; overlap is fine for `max`, not for counting.
- `n = 1` → `nums[0]`.
- Reset the memo between runs; base case is `i < lo`.

```
┌──────────────────────────────────────────────────────────────┐
│  n = 1:  answer = nums[0]                                    │
│  n ≥ 2:  answer = max( line(0, n-2), line(1, n-1) )          │
│  line(lo, hi):  best(i) = max(best(i-1), nums[i]+best(i-2)), │
│                 best(i) = 0 for i < lo,  result best(hi)     │
└──────────────────────────────────────────────────────────────┘
```

## Exercises

1. Solve `[2, 3, 2]` by hand with both runs.

<details>
<summary>Answer</summary>

Run A on `[2, 3]` (houses 0..1): `best = 2, 3` → 3. Run B on `[3, 2]` (houses 1..2): `best = 3, 3` → 3. Answer `max(3, 3) = 3`. (Robbing houses 0 and 2 for 4 is illegal: they are neighbours on the circle.)
</details>

2. Another correct approach uses a flag: run the line DP once forcing house 0 to be **robbed** (then house `n-1` is forbidden), once forcing it to be **skipped**. Write the two calls in terms of `line(lo, hi)`.

<details>
<summary>Answer</summary>

- House 0 robbed: `nums[0] + line(2, n-2)` (houses 1 and `n-1` are both its neighbours, so both are skipped).
- House 0 skipped: `line(1, n-1)`.

Answer = the max of the two. These cases do not overlap, so this version would also work for counting.
</details>

3. Counting version: how many legal plans (including robbing nothing) are there for 4 houses in a circle? Use inclusion–exclusion with line counts. (The number of legal plans on a line of `m` houses is `F(m+2)`: 1, 2, 3, 5, 8 for `m = 0..4`.)

<details>
<summary>Answer</summary>

`count(0..2) + count(1..3) − count(1..2) = F(5) + F(5) − F(4) = 5 + 5 − 3 = 7`.
Check by listing: {}, {0}, {1}, {2}, {3}, {0,2}, {1,3} → 7. ({0,3} is illegal on the circle.)
</details>
