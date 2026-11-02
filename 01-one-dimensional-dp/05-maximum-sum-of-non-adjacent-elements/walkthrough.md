# Maximum Sum of Non-Adjacent Elements — Walkthrough

## 1. Intuition

You walk along a row of boxes. Each box holds some money, and some boxes hold **debts** (negative money). You may open any boxes you like, with one rule: you may never open two boxes that stand next to each other. You want to leave with as much money as possible. You are also allowed to open nothing at all and leave with 0.

How would you decide? Think about the **last** box only. There are just two possibilities:

- **You don't open it.** Then the last box doesn't matter at all, and you face the same puzzle on the row without it.
- **You open it.** You collect its value, but now its neighbour (the second-to-last box) is forbidden. You face the same puzzle on the row without the last **two** boxes.

Whichever of those two gives more money is the answer. That is **pick / not-pick**: one decision about one element, two smaller puzzles of the same kind.

```
row:      [ b0  b1  b2  …  b(i-2)  b(i-1)  b(i) ]

not pick:  [ b0  b1  b2  …  b(i-2)  b(i-1) ]   b(i)       ← solve the row up to i-1
pick:      [ b0  b1  b2  …  b(i-2) ]  ✗b(i-1)  ✓b(i)      ← solve the row up to i-2, add b(i)
```

## 2. Problem statement in our own words

Given an integer array `arr` (values may be negative, zero or positive), choose a set of indices such that no two chosen indices are consecutive. Return the largest possible sum of the chosen values. The empty choice is allowed and has sum 0, so the answer is never negative.

There is no single canonical judge problem for this exact statement; [LeetCode 198 · House Robber](https://leetcode.com/problems/house-robber/) is the same problem with non-negative values (next topic).

> **A note on the "empty choice" rule.** Some versions require choosing at least one element. Then an all-negative array has a negative answer (its largest element). Exercise 1 at the end handles that version. Read the statement carefully: this one detail changes the base cases.

## 3. Examples

| `arr` | Output | Best choice (indices) |
|-------|--------|-----------------------|
| `[2, 1, 4, 9]` | 11 | `{0, 3}` → 2 + 9 |
| `[3, 2, 5, 10, 7]` | 15 | `{0, 2, 4}` → 3 + 5 + 7 |
| `[-3, -1, -2]` | 0 | `{}` → choose nothing |
| `[7]` | 7 | `{0}` |
| `[]` | 0 | `{}` |
| `[5, -2, 4, 7, -1, 3]` | 15 | `{0, 3, 5}` → 5 + 7 + 3 |

**Running example: `arr = [5, -2, 4, 7, -1, 3]`, answer `15`.** It is chosen on purpose: index 2 holds a 4 that looks attractive but conflicts with the 7, and two negatives must be skipped.

### Why greedy ideas fail

Before writing a DP, it is worth seeing why "simple rules" don't work. That is what tells you a DP is needed.

| Greedy rule | Counter-example | Rule gives | Correct |
|-------------|-----------------|------------|---------|
| Always take the largest available element | `[5, 6, 5]` | 6 (blocks both 5s) | 10 |
| Take all even indices or all odd indices, whichever is larger | `[5, 1, 1, 5]` | 6 | 10 (indices 0 and 3) |
| Take every positive element that isn't blocked, left to right | `[1, 9, 1]` | 2 (takes 1 and 1) | 9 |

Every rule fails because taking an element changes what is available **later**. We need to compare both futures, not commit early. That is exactly what `max(pick, notPick)` does.

## 4. Deriving the solution — the 7 questions

### Q1. What is changing?

How much of the array we still need to decide about. We decide from the right end: at each step we look at the last undecided index `i`, and the undecided part is the prefix `arr[0..i]`.

### Q2. What does the state represent?

> `best(i)` = the largest sum of a selection from the prefix `arr[0..i]` in which no two chosen indices are adjacent (empty selection allowed).

`best(n-1)` is the answer to the whole problem.

### Q3. What are the choices?

For element `arr[i]`:

| Choice | Value collected | What remains to be decided | Why |
|--------|-----------------|----------------------------|-----|
| **not pick** | 0 | `arr[0..i-1]` | `arr[i]` is not used, so it puts no restriction on `arr[i-1]` |
| **pick** | `arr[i]` | `arr[0..i-2]` | `arr[i]` is used, so `arr[i-1]` is forbidden; we jump over it |

This is the essence of the pattern: **the constraint decides where the "pick" branch lands.** No-adjacent → land at `i-2`. (In later problems "pick" might land at `i-3`, or at `i + skip[i]`, or reduce a capacity.)

### Q4. What is the recurrence?

```
best(i) = max( best(i-1),               ← not pick arr[i]
               arr[i] + best(i-2) )     ← pick arr[i]
```

Why `max`? The two choices cover every valid selection of `arr[0..i]` (each one either contains `i` or doesn't), and we want the best of all of them.

**What about negative numbers?** Nothing special is needed. If `arr[i] < 0`, then `arr[i] + best(i-2) < best(i-2) ≤ best(i-1)`. (The last step holds because any selection of `arr[0..i-2]` is also a valid selection of `arr[0..i-1]`.) So `max` automatically chooses "not pick". The negative element is simply never taken. That is the advantage of comparing both options instead of following a rule.

### Q5. What are the base cases?

The recursion reaches `best(-1)` (from "not pick" at `i = 0`, or "pick" at `i = 1`) and `best(-2)` (from "pick" at `i = 0`). Both mean "no elements at all":

```
best(i) = 0   for i < 0        (the empty selection)
```

Check with one element: `best(0) = max(best(-1), arr[0] + best(-2)) = max(0, arr[0])`. That is correct: take it if it's positive, otherwise take nothing.

In the table version we cannot have negative indices, so we read "index below 0" as 0 with a small guard (see Section 7).

### Q6. What is the order of computation?

`best(i)` needs `best(i-1)` and `best(i-2)` → fill `i = 0, 1, …, n-1` from left to right.

### Q7. Can the state space be optimized?

Only `best(i-1)` and `best(i-2)` are read → two variables, `O(1)` space. (But keep the table if you need to know *which* elements were picked.)

## 5. Brute force: recursion

```java
private static long best(int[] arr, int i) {
    if (i < 0) return 0;                       // nothing left: empty selection
    long notPick = best(arr, i - 1);           // skip arr[i]
    long pick = arr[i] + best(arr, i - 2);     // take arr[i], jump over arr[i-1]
    return Math.max(notPick, pick);
}
// answer: best(arr, arr.length - 1)
```
Full file: [`solution/BruteForce.java`](solution/BruteForce.java).

### Recursion tree on the prefix `[5, -2, 4, 7]` (call `best(3)`)

The full tree for six elements has 41 calls, too big to draw. Here are the first four elements. Left branch = not pick, right branch = pick (value added on the edge).

```
                                best(3) [12]
                    skip ┌───────────┴───────────┐ pick +7
                     best(2) [9]              best(1) [5]
              skip ┌──────┴──────┐ pick +4  skip ┌──┴──┐ pick -2
               best(1) [5]    best(0) [5]   best(0)[5]  best(-1)[0]
          skip ┌──┴──┐ pick -2  ┌──┴──┐       ┌──┴──┐
          best(0)  best(-1)  b(-1)  b(-2)   b(-1)  b(-2)
           [5]      [0]      [0]   +5:[0]   [0]   +5:[0]
          ┌──┴──┐
        b(-1) b(-2)
         [0]  +5:[0]

best(0)  = max(0, 5 + 0)   = 5
best(1)  = max(5, -2 + 0)  = 5      ← the -2 is skipped automatically
best(2)  = max(5, 4 + 5)   = 9
best(3)  = max(9, 7 + 5)   = 12
```

`best(1)` is computed twice and `best(0)` three times. Each root-to-leaf path is one valid selection. The number of valid selections of `n` elements is the Fibonacci number `F(n+2)` (21 for six elements), so the brute force is about `1.618^n`: exponential.

## 6. Memoization (top-down)

```java
private static Long[] memo;    // memo[i] = best(i); null = not computed

private static long best(int[] arr, int i) {
    if (i < 0) return 0;
    if (memo[i] != null) return memo[i];
    long notPick = best(arr, i - 1);
    long pick = arr[i] + best(arr, i - 2);
    memo[i] = Math.max(notPick, pick);
    return memo[i];
}
```
Full file: [`solution/Memoization.java`](solution/Memoization.java).

**Why `Long[]` and `null`?** In earlier topics `-1` meant "not computed" because no real answer could be `-1`. When inputs can be negative it's easy to get that wrong, so this file uses a `Long` object array where `null` means "unknown". It is a safe habit for any problem whose answers might be negative ([Maximum Subarray](../09-maximum-subarray-kadane-vs-dp/) is one).

### Dry run on `[5, -2, 4, 7, -1, 3]`

| Order finished | State | notPick | pick | Stored | Cache hits used |
|----------------|-------|---------|------|--------|-----------------|
| 1 | `best(0)` | `best(-1) = 0` | `5 + best(-2) = 5` | 5 | — |
| 2 | `best(1)` | `best(0) = 5` | `-2 + best(-1) = -2` | 5 | — |
| 3 | `best(2)` | `best(1) = 5` | `4 + best(0) = 9` | 9 | `best(0)` |
| 4 | `best(3)` | `best(2) = 9` | `7 + best(1) = 12` | 12 | `best(1)` |
| 5 | `best(4)` | `best(3) = 12` | `-1 + best(2) = 8` | 12 | `best(2)` |
| 6 | `best(5)` | `best(4) = 12` | `3 + best(3) = 15` | **15** | `best(3)` |

Each state's "pick" branch hits the cache. Total work: 6 states × O(1).

## 7. Tabulation (bottom-up)

```java
long[] dp = new long[n];   // dp[i] = best(i)
for (int i = 0; i < n; i++) {
    long notPick = (i >= 1) ? dp[i - 1] : 0;          // dp[-1] = 0
    long pick = arr[i] + ((i >= 2) ? dp[i - 2] : 0);  // dp[-2] = dp[-1] = 0
    dp[i] = Math.max(notPick, pick);
}
return dp[n - 1];
```
Full file: [`solution/Tabulation.java`](solution/Tabulation.java). The guards `(i >= 1) ? … : 0` are the base case `best(negative) = 0` written for an array.

### Filling the table

```
 i            0     1     2     3     4     5
 arr          5    -2     4     7    -1     3

 i=0: notPick = 0          pick =  5 + 0     =  5   → 5
 dp           5     .     .     .     .     .

 i=1: notPick = dp[0] = 5  pick = -2 + 0     = -2   → 5
 dp           5     5     .     .     .     .

 i=2: notPick = dp[1] = 5  pick =  4 + dp[0] =  9   → 9
 dp           5     5     9     .     .     .

 i=3: notPick = dp[2] = 9  pick =  7 + dp[1] = 12   → 12
 dp           5     5     9    12     .     .

 i=4: notPick = dp[3] = 12 pick = -1 + dp[2] =  8   → 12
 dp           5     5     9    12    12     .

 i=5: notPick = dp[4] = 12 pick =  3 + dp[3] = 15   → 15
 dp           5     5     9    12    12    15     ← answer
```

Notice `dp` never decreases. Every cell is "at least as good as the previous one", because skipping is always allowed.

### Recovering which elements were picked

Walk from the right. At index `i`, if `dp[i]` equals the "not pick" value `dp[i-1]`, skipping was (at least as) good: move to `i-1`. Otherwise `arr[i]` must have been picked: record it and move to `i-2`.

```
i=5: dp[5]=15, dp[4]=12  → different → PICK 5, go to i=3
i=3: dp[3]=12, dp[2]=9   → different → PICK 3, go to i=1
i=1: dp[1]=5,  dp[0]=5   → equal     → skip,   go to i=0
i=0: dp[0]=5,  "dp[-1]"=0 → different → PICK 0, go to i=-2 → stop

picked indices {0, 3, 5}: 5 + 7 + 3 = 15 ✓
```

`chosenIndices` in the tabulation file implements this walk and is tested.

## 8. Space optimization

```java
long bestTwoBack = 0;   // dp[i-2]  (0 = empty selection before the array)
long bestOneBack = 0;   // dp[i-1]
for (int value : arr) {
    long current = Math.max(bestOneBack, value + bestTwoBack);
    bestTwoBack = bestOneBack;
    bestOneBack = current;
}
return bestOneBack;
```
Full file: [`solution/Optimized.java`](solution/Optimized.java).

Starting both variables at 0 *is* the base case `best(-1) = best(-2) = 0`, which is why this version needs no `if` guards and handles the empty array (the loop does not run, answer 0).

## 9. Dry run on a full example

`arr = [5, -2, 4, 7, -1, 3]`, optimized version:

| step | value | notPick = bestOneBack | pick = value + bestTwoBack | current | bestTwoBack | bestOneBack |
|------|-------|------|------|----|----|----|
| start | — | — | — | — | 0 | 0 |
| 0 | 5 | 0 | 5 + 0 = 5 | 5 | 0 | 5 |
| 1 | -2 | 5 | -2 + 0 = -2 | 5 | 5 | 5 |
| 2 | 4 | 5 | 4 + 5 = 9 | 9 | 5 | 9 |
| 3 | 7 | 9 | 7 + 5 = 12 | 12 | 9 | 12 |
| 4 | -1 | 12 | -1 + 9 = 8 | 12 | 12 | 12 |
| 5 | 3 | 12 | 3 + 12 = 15 | 15 | 12 | 15 |

Answer **15**. The `current` column is exactly the `dp` row of Section 7.

## 10. Complexity analysis

| Approach | Time | Space | Why |
|----------|------|-------|-----|
| Brute force | `O(2^n)` (≈ `1.618^n`) | `O(n)` | One leaf per valid selection, and there are `F(n+2)` of them; stack depth `n`. |
| Memoization | `O(n)` | `O(n)` | `n` states × `O(1)`; memo + stack. |
| Tabulation | `O(n)` | `O(n)` | One pass; table of `n`. Reconstruction is another `O(n)` pass. |
| Optimized | `O(n)` | `O(1)` | One pass; two variables. |

## 11. Why this approach works

Every valid selection of `arr[0..i]` either contains index `i` or does not.
- If it does not, it is a valid selection of `arr[0..i-1]`, and the best such is `best(i-1)`.
- If it does, it cannot contain `i-1`, so removing `i` leaves a valid selection of `arr[0..i-2]`; the best one is `best(i-2)`, and adding `arr[i]` gives the best selection that contains `i`.

The best overall is the larger of these two best cases. The base case is right because the only selection of an empty prefix is the empty one, with sum 0. By induction from left to right, every `best(i)` is correct.

## 12. Common mistakes

1. **"Pick" lands at `i-1`.** Writing `arr[i] + best(i-1)` lets you take neighbours. The constraint must move the pick branch to `i-2`.
2. **Wrong base case for negatives.** Writing `best(0) = arr[0]` gives `-5` for `[-5]`, but choosing nothing (0) is allowed. Use `max(0, arr[0])`, or simply `best(<0) = 0` and let the recurrence work.
3. **Forgetting the empty array.** `dp[n-1]` with `n = 0` crashes; return 0 first (the optimized version handles it naturally).
4. **Trying a greedy rule.** See the counter-examples in Section 3.
5. **Using `-1` as the memo's "unknown" marker in problems whose answers can be `-1`.** Use `null` (or a separate `boolean[] computed`).
6. **Reconstructing from the space-optimized version.** The older values are gone; you need the table.

## 13. When to use this technique / how to recognize similar problems

Use pick/not-pick when:
- the answer is a **subset** of the input (which items to use);
- there is a **constraint between items** (no neighbours, total weight limit, a cooldown after taking an item);
- you need the best (max/min) or the number of valid subsets.

How to adapt it:
1. Decide on the last undecided item.
2. "Not pick" → same problem without that item.
3. "Pick" → add the item's value, and move to the state that the **constraint** allows.

Coming up: [House Robber](../06-house-robber/) is this exact recurrence with non-negative values; [House Robber II](../07-house-robber-ii/) adds a circular constraint; [0/1 Knapsack](../../03-subsequence-and-knapsack-dp/02-zero-one-knapsack/) replaces "skip the neighbour" by "reduce the remaining capacity".

## 14. Summary and revision notes

- State: `best(i)` = best non-adjacent sum within `arr[0..i]` (empty allowed).
- Choices for `arr[i]`: not pick → `best(i-1)`; pick → `arr[i] + best(i-2)`.
- Base: `best(<0) = 0`. Negatives are skipped automatically by `max`.
- Greedy rules fail; compare both futures.
- Reconstruction: walk back; `dp[i] == dp[i-1]` means skip, otherwise pick and jump 2.

```
┌──────────────────────────────────────────────────────────┐
│  best(i) = 0                              for i < 0      │
│  best(i) = max( best(i-1),            ← not pick         │
│                 arr[i] + best(i-2) )  ← pick             │
│  answer: best(n-1)                                       │
└──────────────────────────────────────────────────────────┘
```

## Exercises

1. Change the rule: you must choose **at least one** element. What is the answer for `[-3, -1, -2]`, and how do you adapt the solution with the least change?

<details>
<summary>Answer</summary>

Answer: **-1** (choose only the `-1`). The smallest change: if every element is negative, return the largest element; otherwise return the usual DP answer. Why this works: if some element `x ≥ 0` exists, the usual DP answer `D` satisfies `D ≥ x ≥ 0`. If `D > 0`, it comes from a non-empty selection. If `D = 0`, then `x = 0`, and the one-element selection `{x}` also reaches 0. Either way `D` is reachable without the empty selection. If all elements are negative, the best non-empty selection is a single element, because adding another negative only lowers the sum, so the answer is the maximum element.
</details>

2. Fill the `dp` table for `[3, 2, 5, 10, 7]` and recover the picked indices.

<details>
<summary>Answer</summary>

`dp = 3, 3, 8, 13, 15`.
- `i=2`: `max(3, 5+3) = 8`; `i=3`: `max(8, 10+3) = 13`; `i=4`: `max(13, 7+8) = 15`.

Walk back: `dp[4]=15 ≠ dp[3]=13` → pick 4, go to 2. `dp[2]=8 ≠ dp[1]=3` → pick 2, go to 0. `dp[0]=3 ≠ 0` → pick 0. Indices **{0, 2, 4}**: 3 + 5 + 7 = 15.
</details>

3. Stricter rule: any two chosen indices must differ by **at least 3** (so `i` and `i+2` also conflict). Write the new recurrence and solve `[5, -2, 4, 7, -1, 3]`.

<details>
<summary>Answer</summary>

Only the landing spot of "pick" changes: `best(i) = max(best(i-1), arr[i] + best(i-3))`, `best(<0) = 0`.
`best = 5, 5, 5, 12, 12, 12` (e.g. `best(3) = max(5, 7 + best(0)) = 12`, `best(5) = max(12, 3 + best(2)) = 12`).
Answer **12**, indices {0, 3}.
</details>

4. Why does `dp[i] ≥ dp[i-1]` always hold here, and why would this be false in [Maximum Subarray](../09-maximum-subarray-kadane-vs-dp/)'s "ending at `i`" state?

<details>
<summary>Answer</summary>

Here `dp[i]` is the best over the whole prefix `arr[0..i]`, and every selection of the shorter prefix is still allowed for the longer one, so the best can only stay or grow. In Maximum Subarray, the state "best subarray **ending exactly at** `i`" is forced to include `arr[i]`, so a negative `arr[i]` can make it smaller than the previous value. Topic 09 explains this difference in detail.
</details>
