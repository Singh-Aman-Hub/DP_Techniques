# Equal Sum Partition — Walkthrough

## 1. Intuition (no code, no math — an everyday analogy)

Two friends come back from fruit picking with four bags weighing 1, 5, 11 and 5 kg. They want to carry **exactly the same weight** each, without opening any bag. Is it possible?

The total is 22 kg, so each friend must carry 11 kg. Here is the key insight: **you only need to plan one friend's load.** If the first friend's bags weigh exactly 11 kg, the second friend gets all the other bags, and those must weigh 22 − 11 = 11 kg automatically. So the question "can we split fairly?" is really "**can one friend pick bags that weigh exactly 11?**" — and that is [Subset Sum](../03-subset-sum/). (Yes: 1 + 5 + 5 = 11, leaving the 11 kg bag for the other friend.)

And if the total had been 23 kg, you would know instantly that it is impossible: you cannot split 23 into two equal whole numbers.

## 2. Problem statement in our own words

Given an array `nums` of positive integers, decide whether it can be divided into two groups (every element in exactly one group) whose sums are equal.

Original: [Partition Equal Subset Sum — LeetCode 416](https://leetcode.com/problems/partition-equal-subset-sum/). Constraints: `1 ≤ n ≤ 200`, `1 ≤ nums[i] ≤ 100`, so `total ≤ 20 000` and `half ≤ 10 000`.

## 3. Examples (input → output, with an explanation)

Running example: **`nums = [1, 5, 11, 5]`**.

| Input | Output | Explanation |
|---|---|---|
| `[1, 5, 11, 5]` | `true` | total 22, half 11: `{1, 5, 5}` and `{11}` |
| `[1, 2, 3, 5]` | `false` | total 11 is odd — rejected without any DP |
| `[1, 2, 5]` | `false` | total 8, half 4, but no subset makes 4 (sums: 0,1,2,3,5,6,7,8) |
| `[3, 3, 3, 4, 5]` | `true` | total 18, half 9: `{4, 5}` and `{3, 3, 3}` |
| `[1]` | `false` | a single element cannot be split into two equal non-negative parts with sum 1 |

## 4. Deriving the solution — the 7 questions

### The reduction first

Let `total` be the sum of all elements and suppose the array splits into groups `A` and `B` with `sum(A) = sum(B)`. Since `sum(A) + sum(B) = total`, we get `2 · sum(A) = total`, so:

1. `total` must be **even**, and
2. `sum(A) = total / 2`.

Conversely, if some subset `A` has sum `total / 2`, its complement `B` (all other elements) has sum `total − total/2 = total/2`. So:

> **An equal partition exists ⇔ `total` is even and some subset sums to `half = total / 2`.**

Everything below is subset sum with target `half`.

### Q1. What is changing?

The index `i` of the element we are deciding (goes into group A or not), and how much of `half` is still `remaining` for group A.

### Q2. What does the state represent?

> `canMake(i, r)` = `true` if some subset of `nums[i..n-1]` sums to exactly `r`.

Answer: `total` is even **and** `canMake(0, half)`.

### Q3. What are the choices?

Put `nums[i]` into group A (take: `r` drops by `nums[i]`, allowed if `nums[i] ≤ r`) or into group B (skip: `r` unchanged).

### Q4. What is the recurrence?

```
canMake(i, r) = canMake(i + 1, r)  ||  ( nums[i] ≤ r  &&  canMake(i + 1, r - nums[i]) )
```

### Q5. What are the base cases?

`r == 0` → `true` (group A is complete; everything else goes to B). `i == n` with `r > 0` → `false`. Plus the shortcut **before** any DP: odd `total` → `false`.

### Q6. What is the order of computation?

Prefix table `dp[i][s]` (first `i` numbers, sum exactly `s`), rows top to bottom; or the 1-D array with `s` from `half` down to `nums[i]`.

### Q7. Can the state space be optimized?

Yes: one boolean array of size `half + 1`. We can also **stop early** as soon as `reachable[half]` becomes true.

## 5. Brute force: recursion

```java
static boolean canPartition(int[] nums) {
    int total = 0;
    for (int x : nums) total += x;
    if (total % 2 != 0) return false;           // odd total: impossible
    int half = total / 2;
    return canMake(nums, 0, half);
}

private static boolean canMake(int[] nums, int i, int remaining) {
    if (remaining == 0) return true;
    if (i == nums.length) return false;
    boolean skip = canMake(nums, i + 1, remaining);
    boolean take = nums[i] <= remaining && canMake(nums, i + 1, remaining - nums[i]);
    return skip || take;
}
```

Full code: [`solution/BruteForce.java`](solution/BruteForce.java).

The top of the recursion tree for `[1, 5, 11, 5]`, `half = 11` (left = put in group A, right = put in group B):

```
                                   (0,11)
                    take 1 /                      \ skip 1
                     (1,10)                         (1,11)
           take 5 /         \ skip 5       take 5 /         \ skip 5
             (2,5)            (2,10)          (2,6)            (2,11)
        ✗11 /  \ skip      ✗11 / \ skip    ✗11 / \ skip     take 11 / \ skip
               (3,5)            (3,10)           (3,6)       (3,0)=TRUE  (3,11)
         take 5/  \           take 5/ \        take5/ \                  take5/ \
        (4,0)=TRUE (4,5)=F   (4,5)=F (4,10)=F (4,1)=F (4,6)=F       (4,6)=F  (4,11)=F
```

`✗11` means 11 does not fit in the remaining amount, so only the skip branch exists. Two leaves are true: `{1, 5, 5}` (take 1, take 5, skip 11, take 5) and `{11}` (skip 1, skip 5, take 11). Either one proves the answer.

**Why it is slow:** `2^200` paths for the largest input. **Repeats:** states are `(i, r)` with `r ≤ 10 000`, so at most `201 × 10 001 ≈ 2 · 10^6` distinct ones.

## 6. Memoization (top-down)

Cache `canMake(i, r)` in a `Boolean[n+1][half+1]` (`null` = unknown):

```java
private static boolean canMake(int[] nums, int i, int remaining) {
    if (remaining == 0) return true;
    if (i == nums.length) return false;
    if (memo[i][remaining] != null) return memo[i][remaining];
    boolean skip = canMake(nums, i + 1, remaining);
    boolean take = nums[i] <= remaining && canMake(nums, i + 1, remaining - nums[i]);
    memo[i][remaining] = skip || take;
    return memo[i][remaining];
}
```

Full code: [`solution/Memoization.java`](solution/Memoization.java).

Dry run (skip first, as in the code): `(0,11)` → skip → `(1,11)` → skip → `(2,11)` → skip → `(3,11)` → skip `(4,11)=F`, take `(4,6)=F` → `memo[3][11]=F`. Back at `(2,11)`: take 11 → `(3,0)` = **true** → `memo[2][11]=T` → `memo[1][11]=T` → `memo[0][11]=T`. The solver found the group `{11}` first; the other group is then `{1, 5, 5}`.

## 7. Tabulation (bottom-up)

```java
boolean[][] dp = new boolean[n + 1][half + 1];
dp[0][0] = true;
for (int i = 1; i <= n; i++) {
    int value = nums[i - 1];
    for (int s = 0; s <= half; s++) {
        dp[i][s] = dp[i - 1][s] || (value <= s && dp[i - 1][s - value]);
    }
}
return dp[n][half];
```

Full code: [`solution/Tabulation.java`](solution/Tabulation.java).

### The table for `[1, 5, 11, 5]`, `half = 11`

`T` = true, `.` = false.

```
                 s: 0  1  2  3  4  5  6  7  8  9 10 11
i=0  {}             T  .  .  .  .  .  .  .  .  .  .  .
i=1  {1}            T  T  .  .  .  .  .  .  .  .  .  .     0→1
i=2  {1,5}          T  T  .  .  .  T  T  .  .  .  .  .     0→5, 1→6
i=3  {1,5,11}       T  T  .  .  .  T  T  .  .  .  .  T     0→11
i=4  {1,5,11,5}     T  T  .  .  .  T  T  .  .  .  T  T     5→10, 6→11 (already T)
                                                    ↑
                                         dp[4][11] = true
```

Cell details for the last row:

```
dp[4][10] = dp[3][10] || dp[3][5]  = F || T = T     (5 + 5)
dp[4][11] = dp[3][11] || dp[3][6]  = T || T = T     ({11}, or {1,5} + 5)
dp[4][5]  = dp[3][5]  || dp[3][0]  = T || T = T
```

## 8. Space optimization

```java
boolean[] reachable = new boolean[half + 1];
reachable[0] = true;
for (int value : nums) {
    for (int s = half; s >= value; s--) {          // backward: each number used at most once
        if (reachable[s - value]) reachable[s] = true;
    }
    if (reachable[half]) return true;              // early exit
}
return reachable[half];
```

Full code: [`solution/Optimized.java`](solution/Optimized.java).

**Why the early exit is safe:** once some subset of the numbers seen so far reaches `half`, the remaining numbers simply go to group B. Nothing later can "un-reach" `half`.

Note the loop for the number 11 when `half = 11`: it runs only for `s = 11`, reading `reachable[0]`. Numbers larger than `half` do not enter the loop at all — they must be in group B anyway. (If any number is larger than `half`, the answer is `false`: its group would already exceed `half`.)

## 9. Dry run on a full example

One-array version, `[1, 5, 11, 5]`, `half = 11`:

| Step | Number | Loop `s` | `reachable` true at | `reachable[11]`? |
|---|---|---|---|---|
| start | — | — | 0 | no |
| 1 | 1 | 11 → 1 | 0, 1 | no |
| 2 | 5 | 11 → 5 | 0, 1, 5, 6 | no |
| 3 | 11 | 11 → 11 | 0, 1, 5, 6, **11** | **yes → return true** |

The early exit stops before the last 5 is processed. All four solutions agree on every test.

### A "false" example: `[1, 2, 5]`

`total = 8` is even, so the odd shortcut does not help; `half = 4`.

```
               s: 0  1  2  3  4
i=0  {}           T  .  .  .  .
i=1  {1}          T  T  .  .  .
i=2  {1,2}        T  T  T  T  .      0→2, 1→3
i=3  {1,2,5}      T  T  T  T  .      5 > 4: the 5 can only go to group B
```

`dp[3][4]` is false → no equal split. Intuitively: the 5 alone is already more than half, so whichever group gets it is too heavy. (This is the "element larger than `half`" rejection from Section 8, discovered by the table.)

### Recovering the two groups

The boolean table can also tell you **which** elements go into group A. Start at `(n, half)` and walk up:

- If `dp[i-1][s]` is true, group A can be completed without `nums[i-1]`: put `nums[i-1]` in group B and go to `(i-1, s)`.
- Otherwise `nums[i-1]` must be in group A: go to `(i-1, s − nums[i-1])`.

For `[1, 5, 11, 5]`:

```
(4, 11): dp[3][11] = T   → last 5 goes to B,  stay at s = 11
(3, 11): dp[2][11] = F   → 11 goes to A,     s = 11 − 11 = 0
(2, 0):  dp[1][0]  = T   → 5 goes to B
(1, 0):  dp[0][0]  = T   → 1 goes to B
Group A = {11}, group B = {1, 5, 5}.
```

This needs the full 2-D table; the one-array version only knows *whether* a split exists. That trade-off (less memory vs. the ability to rebuild the answer) comes up in almost every DP.

## 10. Complexity analysis (explained, for every approach)

| Approach | Time | Space | Why |
|---|---|---|---|
| Brute force | `O(2^n)` | `O(n)` | two branches per number |
| Memoization | `O(n · half)` | `O(n · half)` | `≤ (n+1)(half+1)` states, `O(1)` each; ≈ 2·10^6 for LC limits |
| Tabulation | `O(n · half)` | `O(n · half)` | every cell once |
| One array | `O(n · half)` | `O(half)` | each number sweeps at most `half` sums; early exit can only make it faster |

Computing `total` and the odd check costs `O(n)` and can save the whole DP.

## 11. Why this approach works (correctness argument in plain words)

The reduction (Section 4) shows that an equal partition and a subset with sum `total/2` are the same thing seen from two sides: a partition gives such a subset (either group), and such a subset gives a partition (it and its complement). The subset-sum DP is correct by the argument in [Subset Sum §11](../03-subset-sum/walkthrough.md#11-why-this-approach-works-correctness-argument-in-plain-words). So the combined algorithm answers exactly the partition question.

## 12. Common mistakes

1. **Running the DP with target `total`** instead of `total / 2` — always true (take everything).
2. **Forgetting the odd check** — then `total / 2` rounds down and you may answer `true` wrongly: `[1, 2]` has total 3, `half = 1`, and `{1}` reaches 1, but `{1}` vs `{2}` is not equal.
3. **Forward inner loop** — reuses a number: `[1, 2, 5]` has `half = 4`; a forward loop over the first number 1 marks 1, then 2 (= 1 + 1), 3 and 4 — the single 1 used up to four times — wrongly answering `true`.
4. **Integer overflow** is not an issue here (`total ≤ 20 000`), but in other partition problems with large values, compute the total in `long`.
5. **Treating it as greedy** (sort and alternate, or always add to the lighter group) — fails on `[3, 3, 2, 2, 2]` (total 12): greedy-by-lighter-group gives `3+2+2 = 7` vs `3+2 = 5`, yet `{3, 3}` vs `{2, 2, 2}` is a perfect split.

## 13. When to use this technique / how to recognize similar problems

- "Split into **two** groups with **equal** sums" → subset sum with `total / 2`.
- "Split into two groups and minimize the difference" → read the last row ([topic 05](../05-partition-with-minimum-difference/)).
- "Assign + or − to reach a target" → subset sum with `(total + target) / 2` ([topic 06](../06-target-sum/)).
- More than two groups → not a single subset-sum; see [Bitmask DP](../../04-advanced-dp-patterns/09-bitmask-dp/).

General habit: when a problem talks about **two complementary groups**, write `A + B = total` and the condition on `A` and `B`; usually one group can be eliminated.

## 14. Summary and revision notes

- Equal partition ⇔ `total` even **and** subset with sum `total / 2`.
- Odd total → `false` immediately; any element > `half` → `false`.
- Then plain subset sum: `O(n · half)` time, `O(half)` space with the backward loop.
- Early exit when `half` becomes reachable.

```
if (total is odd) return false
half = total / 2
reachable[0] = true
for each x:  for s = half down to x:  reachable[s] |= reachable[s - x]
return reachable[half]
```

## Exercises

1. Is `[2, 2, 3, 5]` partitionable? Show the 1-D array after each number.

<details>
<summary>Answer</summary>

Total 12, half 6. After 2: `{0, 2}`. After 2: `{0, 2, 4}`. After 3: `{0, 2, 3, 4, 5}`. After 5: adds `5` (already there) — `s = 6` needs `reachable[1]`, false. Final: 6 not reachable → **false**. (Subsets of `{2, 2, 3, 5}` sum to 0, 2, 3, 4, 5, 7, 8, 9, 10, 12 — never 6.)
</details>

2. Without running any DP, answer for `[1, 1, 1, 100]` and explain.

<details>
<summary>Answer</summary>

Total 103 is odd → **false**. Even if it were even, 100 > half would already make it impossible.
</details>

3. In [Two Sets II](https://cses.fi/problemset/task/1093) you must **count** the ways to split `1, 2, …, n` into two sets with equal sums, modulo 10^9+7. If you count subsets of `{1..n}` with sum `half`, every split is counted twice. How do you fix that without dividing under a modulo?

<details>
<summary>Answer</summary>

Each split `{A, B}` is counted once as `A` and once as `B`. Fix the number `n` to always be in group B: count subsets of `{1, …, n−1}` with sum `half` (those are exactly the groups A that do not contain `n`). That counts each split once. (Alternative: multiply by the modular inverse of 2, which is 500000004 for 10^9+7.) For `n = 7`: total 28, half 14, and the answer is 4.
</details>
