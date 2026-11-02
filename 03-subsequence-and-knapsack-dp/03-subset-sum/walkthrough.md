# Subset Sum — Walkthrough

## 1. Intuition (no code, no math — an everyday analogy)

You want to buy a gift card worth exactly **9 euros** and you have coins of 3, 4, 5 and 2 euros in your pocket — one of each. Can you pay exactly 9 without receiving change?

You could try every handful of coins (16 handfuls). Or you could think like this: put the coins on the table one at a time and keep a list of **every amount you can pay so far**.

- No coins yet: you can pay 0.
- Add the 3: you can pay 0 or 3.
- Add the 4: each old amount either stays as it is or grows by 4: `0, 3, 4, 7`.
- Add the 5: `0, 3, 4, 7` plus `5, 8, 9, 12` → 9 is in the list. Yes!

That growing list of "payable amounts" is the subset-sum DP. Each coin either joins an existing payment or not — the same take/skip choice as before — and you only need to remember *which totals are possible*, not *how* you made them.

## 2. Problem statement in our own words

Given an array `arr` of `n` positive integers and a non-negative integer `T`, return `true` if some subset of `arr` sums to **exactly** `T`, and `false` otherwise. The empty subset is allowed, so `T = 0` is always `true`.

There is no single LeetCode problem with exactly this statement. The CSES problem [Money Sums](https://cses.fi/problemset/task/1745) asks a close relative — list **every** sum that some non-empty set of the coins can make — and we solve it at the end of this walkthrough.

## 3. Examples (input → output, with an explanation)

Running example: **`arr = [3, 4, 5, 2]`, `T = 9`.**

| Input | Output | Explanation |
|---|---|---|
| `[3, 4, 5, 2]`, `T = 9` | `true` | `4 + 5 = 9` (also `3 + 4 + 2`) |
| `[3, 4, 5, 2]`, `T = 13` | `false` | the total is 14; 13 would mean leaving out a 1, and there is none |
| `[3, 4, 5, 2]`, `T = 1` | `false` | every number is bigger than 1 |
| `[3, 4, 5, 2]`, `T = 0` | `true` | the empty subset |
| `[3, 34, 4, 12, 5, 2]`, `T = 30` | `false` | 34 is too big; the others add up to only 26 |

## 4. Deriving the solution — the 7 questions

### Q1. What is changing?

The index `i` of the number we are deciding on, and the amount of the target that is **still uncovered**, `remaining` (it starts at `T` and drops each time we take a number).

### Q2. What does the state represent?

> `canMake(i, remaining)` = `true` if some subset of `arr[i..n-1]` sums to exactly `remaining`.

The answer is `canMake(0, T)`. It is the knapsack state `(i, capacity)` with "capacity" renamed to "remaining".

### Q3. What are the choices?

1. **Skip** `arr[i]`: ask `canMake(i + 1, remaining)`.
2. **Take** `arr[i]`, allowed only if `arr[i] ≤ remaining`: ask `canMake(i + 1, remaining - arr[i])`.

### Q4. What is the recurrence?

```
canMake(i, r) = canMake(i + 1, r)                                ← skip
             || ( arr[i] ≤ r  &&  canMake(i + 1, r - arr[i]) )    ← take
```

We combine with `||` because **one** working way is enough. Compare: knapsack used `max` (best of two), counting used `+` (sum of two).

### Q5. What are the base cases?

- `r == 0` → `true`: we have covered the target exactly (take nothing more).
- `i == n` and `r > 0` → `false`: no numbers left but something still uncovered.

The order of these two checks matters: `canMake(n, 0)` must be `true`.

In the bottom-up prefix view, the base row is:

```
dp[0][0] = true          the empty subset makes 0
dp[0][s] = false (s>0)   no numbers cannot make a positive sum
```

This is the "**exactly**" base row. Knapsack ("weight **at most** W") had all zeros in row 0 instead, because the empty bag is valid for every capacity.

### Q6. What is the order of computation?

Prefix view: `dp[i][s]` = some subset of the **first `i` numbers** sums to exactly `s`.

```
dp[i][s] = dp[i-1][s]  ||  ( arr[i-1] ≤ s  &&  dp[i-1][s - arr[i-1]] )
```

Row `i` needs only row `i - 1`: fill rows top to bottom; any column order works while we keep the full table.

### Q7. Can the state space be optimized?

Exactly as in knapsack: one boolean array of length `T + 1`, inner loop from `T` **down** to `arr[i]`.

## 5. Brute force: recursion

```java
private static boolean canMake(int[] arr, int i, int remaining) {
    if (remaining == 0) return true;
    if (i == arr.length) return false;
    boolean skip = canMake(arr, i + 1, remaining);
    boolean take = arr[i] <= remaining && canMake(arr, i + 1, remaining - arr[i]);
    return skip || take;
}
```

Full code: [`solution/BruteForce.java`](solution/BruteForce.java).

The recursion tree for `[3, 4, 5, 2]`, `T = 9`, written as `(i, remaining)`. Left = take, right = skip. `✗` = the number does not fit.

```
                                         (0,9)
                         take 3 /                      \ skip 3
                           (1,6)                          (1,9)
                take 4 /          \ skip 4        take 4 /       \ skip 4
                  (2,2)            (2,6)            (2,5)            (2,9)
              ✗5 /    \        take5/  \skip   take5/   \skip    take5/   \skip
                     (3,2)      (3,1)  (3,6)    (3,0)   (3,5)    (3,4)    (3,9)
                    t2/  \s     ✗2 \   t2/ \s    TRUE   t2/ \s    t2/ \s   t2/ \s
                 (4,0) (4,2)    (4,1) (4,4)(4,6)       (4,3)(4,5)(4,2)(4,4)(4,7)(4,9)
                 TRUE   F         F    F    F            F    F    F    F    F    F
```

Two leaves are `true`: "take 3, take 4, skip 5, take 2" (`3+4+2 = 9`) and "skip 3, take 4, take 5" (`4+5 = 9`). One is enough, so the answer is `true`. (The real program stops early: `skip || take` does not evaluate `take` once `skip` is already `true`.)

**Why it is slow:** up to `2^n` leaves. **Where the repeats are:** states are pairs `(i, r)` with `0 ≤ r ≤ T`, so there are at most `(n + 1)(T + 1)` different ones. In this small tree each appears once, but with `[2, 3, 5, 7, …]` the histories "take 2, take 3, skip 5" and "skip 2, skip 3, take 5" both arrive at `(3, T - 5)`.

## 6. Memoization (top-down)

**What is cached:** `memo[i][r]` = `canMake(i, r)`. A plain `boolean` has no third value for "not computed yet", so we use the wrapper `Boolean`, where `null` means unknown.

```java
private static Boolean[][] memo;     // null = not computed yet

private static boolean canMake(int[] arr, int i, int remaining) {
    if (remaining == 0) return true;
    if (i == arr.length) return false;
    if (memo[i][remaining] != null) return memo[i][remaining];

    boolean skip = canMake(arr, i + 1, remaining);
    boolean take = arr[i] <= remaining && canMake(arr, i + 1, remaining - arr[i]);
    memo[i][remaining] = skip || take;
    return memo[i][remaining];
}
```

Full code: [`solution/Memoization.java`](solution/Memoization.java). (An alternative is an `int` memo with `-1` unknown, `0` false, `1` true.)

**Dry run** (skip is explored first, as in the code):

```
(0,9) → skip → (1,9) → skip → (2,9) → skip → (3,9) → skip (4,9)=F, take (4,7)=F → memo[3][9]=F
                                      take 5 → (3,4) → skip (4,4)=F, take (4,2)=F → memo[3][4]=F
                              memo[2][9]=F
                       take 4 → (2,5) → skip → (3,5) → skip (4,5)=F, take (4,3)=F → memo[3][5]=F
                                        take 5 → (3,0) = TRUE (base case)
                              memo[2][5]=T
               memo[1][9]=T
memo[0][9]=T  (take branch of (0,9) never runs: || already has true)
```

## 7. Tabulation (bottom-up)

```java
boolean[][] dp = new boolean[n + 1][target + 1];
dp[0][0] = true;
for (int i = 1; i <= n; i++) {
    int value = arr[i - 1];
    for (int s = 0; s <= target; s++) {
        dp[i][s] = dp[i - 1][s] || (value <= s && dp[i - 1][s - value]);
    }
}
return dp[n][target];
```

Full code: [`solution/Tabulation.java`](solution/Tabulation.java).

### Filling the table for `[3, 4, 5, 2]`, `T = 9`

`T` = true, `.` = false (dots make the true cells stand out).

**Row 0:** only sum 0.

```
             s: 0  1  2  3  4  5  6  7  8  9
i=0  {}         T  .  .  .  .  .  .  .  .  .
```

**Row 1** (number 3): every true cell of row 0 stays true (skip) and also lights the cell 3 to its right (take).

```
i=1  {3}        T  .  .  T  .  .  .  .  .  .        0 → 3
```

**Row 2** (number 4): copy row 1, then for each true `s` in row 1 mark `s + 4`.

```
i=2  {3,4}      T  .  .  T  T  .  .  T  .  .        0 → 4,  3 → 7
```

**Row 3** (number 5):

```
i=3  {3,4,5}    T  .  .  T  T  T  .  T  T  T        0 → 5,  3 → 8,  4 → 9,  (7 → 12 is beyond T)
```

`dp[3][9]` is already true (4 + 5).

**Row 4** (number 2):

```
i=4  {3,4,5,2}  T  .  T  T  T  T  T  T  T  T        0 → 2, 3 → 5, 4 → 6, 5 → 7, 7 → 9
```

Final table:

```
             s: 0  1  2  3  4  5  6  7  8  9
i=0  {}         T  .  .  .  .  .  .  .  .  .
i=1  {3}        T  .  .  T  .  .  .  .  .  .
i=2  {3,4}      T  .  .  T  T  .  .  T  .  .
i=3  {3,4,5}    T  .  .  T  T  T  .  T  T  T
i=4  {3,4,5,2}  T  .  T  T  T  T  T  T  T  T    ← dp[4][9] = true
```

One cell in detail: `dp[4][6] = dp[3][6] || dp[3][6-2] = false || true = true` (`4 + 2`).

**The last row is a bonus.** It tells you every sum up to `T` that some subset can reach: here `0, 2, 3, 4, 5, 6, 7, 8, 9` — everything except 1. Topic 05 reads exactly this row.

## 8. Space optimization

One boolean array, `reachable[s]`, and the **backward** loop from [0/1 Knapsack](../02-zero-one-knapsack/walkthrough.md#8-space-optimization):

```java
boolean[] reachable = new boolean[target + 1];
reachable[0] = true;
for (int value : arr) {
    for (int s = target; s >= value; s--) {
        if (reachable[s - value]) reachable[s] = true;
    }
}
return reachable[target];
```

Full code: [`solution/Optimized.java`](solution/Optimized.java).

**Why backward, on a tiny example.** `arr = [2]`, `T = 4`. Only `{}` and `{2}` exist, so 4 is **not** reachable.

```
Forward (WRONG):   s=2: reachable[0]=T → reachable[2]=T
                   s=3: reachable[1]=F
                   s=4: reachable[2]=T (just set in this pass!) → reachable[4]=T   ✗ used the 2 twice
Backward (RIGHT):  s=4: reachable[2]=F (not yet set) → stays F
                   s=3: reachable[1]=F
                   s=2: reachable[0]=T → reachable[2]=T                           ✔
```

## 9. Dry run on a full example

`reachable[0..9]` after each number (backward loop), `arr = [3, 4, 5, 2]`, `T = 9`:

| After | `reachable` (s = 0..9) | Newly true | Matches row |
|---|---|---|---|
| start | `T . . . . . . . . .` | 0 | 0 |
| 3 | `T . . T . . . . . .` | 3 | 1 |
| 4 | `T . . T T . . T . .` | 4, 7 | 2 |
| 5 | `T . . T T T . T T T` | 5, 8, 9 | 3 |
| 2 | `T . T T T T T T T T` | 2, 6 | 4 |

Answer `reachable[9] = true`. All four solution files agree on all tests.

### Money Sums (CSES 1745)

Money Sums gives `n ≤ 100` coins, each `≤ 1000`, and asks for every sum that some non-empty set of coins can make, in increasing order. Run the same DP with `T` = total of all coins; then every `s` from 1 to total with `reachable[s] = true` is an answer.

```java
int total = sum of coins;
boolean[] reachable = new boolean[total + 1];
reachable[0] = true;
for (int coin : coins)
    for (int s = total; s >= coin; s--)
        if (reachable[s - coin]) reachable[s] = true;
// print every s in 1..total with reachable[s]
```

For the sample coins `4 2 5 2` (total 13), the reachable sums are `2 4 5 6 7 8 9 11 13` — 9 of them. Work: `100 × 100 000 = 10^7` steps. Full code: [`solution/MoneySums.java`](solution/MoneySums.java).

## 10. Complexity analysis (explained, for every approach)

| Approach | Time | Space | Why |
|---|---|---|---|
| Brute force | `O(2^n)` | `O(n)` | two branches per number; stack depth `n` |
| Memoization | `O(n · T)` | `O(n · T)` | at most `(n+1)(T+1)` states, each `O(1)`; table plus `O(n)` stack |
| Tabulation | `O(n · T)` | `O(n · T)` | every cell once |
| One array | `O(n · T)` | `O(T)` | each number sweeps `T..value` once |

Pseudo-polynomial again: the cost depends on the size of `T`. For `n = 100`, `T = 10^5`: 10^7 steps — fast. For `T = 10^9`: too slow, and then you need a different idea (for small `n`, "meet in the middle"; see the warning in [topic 05](../05-partition-with-minimum-difference/walkthrough.md)).

## 11. Why this approach works (correctness argument in plain words)

A subset of the first `i` numbers either contains `arr[i-1]` or it does not. If some subset sums to `s` **without** `arr[i-1]`, then `dp[i-1][s]` is true. If one sums to `s` **with** `arr[i-1]`, removing that number leaves a subset of the first `i-1` numbers with sum `s - arr[i-1]`, so `dp[i-1][s - arr[i-1]]` is true. Conversely, either true cell gives a valid subset for `dp[i][s]`. So the recurrence is true exactly when a subset exists. Row 0 is correct by definition, and induction on `i` does the rest.

## 12. Common mistakes

1. **Wrong base row.** Setting `dp[0][s] = true` for all `s` (copied from "at most" knapsack thinking) makes everything reachable.
2. **Checking `i == n` before `r == 0`** in the recursion: `canMake(n, 0)` must be true.
3. **Forward 1-D loop**: reuses a number; see the `[2]`, `T = 4` example.
4. **Primitive `boolean[][]` as a memo**: `false` cannot mean both "not computed" and "computed: no". Use `Boolean` with `null`, or an `int` with three values.
5. **Forgetting to bound `s - value ≥ 0`**: index out of range.
6. **Negative numbers**: the array cannot be indexed by a negative sum; you would need an offset, and the "exactly" table must span from the most negative to the most positive total.

## 13. When to use this technique / how to recognize similar problems

- The question is **"is it possible to choose some of these numbers to get exactly X?"**
- Or **"which totals are possible?"** (read the last row).
- Each number is used at most once and the totals are small enough to index an array.

Variations: split into two equal halves ([topic 04](../04-equal-sum-partition/)), closest split ([topic 05](../05-partition-with-minimum-difference/)), count the subsets instead of asking yes/no ([topic 06](../06-target-sum/), using `+` instead of `||`).

## 14. Summary and revision notes

- Subset sum = 0/1 knapsack with a yes/no question; target = capacity.
- Combine with `||`. Base row: only `dp[0][0]` is true ("exactly").
- Memoize booleans with `Boolean` (`null` = unknown).
- 1-D: one boolean array, loop `s` from `T` down to `value`.
- The last row lists every reachable sum ≤ `T` — that is Money Sums, and the key to topic 05.

```
dp[i][s] = some subset of the first i numbers sums to exactly s

dp[0][0] = true,   dp[0][s>0] = false
dp[i][s] = dp[i-1][s]  ||  ( arr[i-1] <= s  &&  dp[i-1][s - arr[i-1]] )

1-D:  reachable[0] = true;  for each x:  for s = T down to x:  reachable[s] |= reachable[s - x]
```

## Exercises

1. Fill the one-array DP for `arr = [2, 2, 3]`, `T = 6`, after each number. Is 6 reachable? Which sums from 0 to 6 are not?

<details>
<summary>Answer</summary>

```
start: T . . . . . .
2:     T . T . . . .
2:     T . T . T . .
3:     T . T T T T .
```
6 is **not** reachable (2 + 2 + 3 = 7, 2 + 3 = 5, 2 + 2 = 4). Unreachable in 0..6: 1 and 6.
</details>

2. Why does `MoneySums.java` start printing from `s = 1` instead of `s = 0`?

<details>
<summary>Answer</summary>

`reachable[0]` is always true because of the empty subset, but the problem asks for sums made from at least one coin. Since every coin is positive, any non-empty set has sum ≥ 1, so starting at 1 removes exactly the empty set.
</details>

3. Change the recurrence to compute the **number** of subsets that sum to `T`. What changes in the table type, base row and combine step? What is the count for `[3, 4, 5, 2]`, `T = 9`?

<details>
<summary>Answer</summary>

Use `int`/`long` cells, base `dp[0][0] = 1`, other `dp[0][s] = 0`, and combine with `+`. For `T = 9`: the subsets are `{4, 5}` and `{3, 4, 2}` → **2**. This is the counting DP from [topic 01](../01-subsequences-fundamentals/) and the core of [Target Sum](../06-target-sum/).
</details>
