# 552. Student Attendance Record II

> **Pattern:** State-machine counting where the state remembers just enough history (absences so far, trailing lates) · **Builds on:** [State Machine DP](../../../04-advanced-dp-patterns/12-state-machine-dp/) · **Difficulty:** Hard

## Problem overview

An attendance record is a string over `A` (absent), `L` (late) and `P` (present). It earns an
award if it has **fewer than 2 `A`s in total** and **never 3 or more consecutive `L`s**. Count
the award-eligible records of length `n`, modulo 10⁹ + 7.

## Link & metadata

| Field | Value |
|---|---|
| LeetCode | [552. Student Attendance Record II](https://leetcode.com/problems/student-attendance-record-ii/) |
| Difficulty | Hard |
| Pattern | Counting strings accepted by a small automaton; state = (absences used, current run of L) |
| Prerequisite topic | [`04-advanced-dp-patterns/12-state-machine-dp`](../../../04-advanced-dp-patterns/12-state-machine-dp/) |
| Why this pattern | Each rule only needs a tiny summary of the past: "how many A so far (0 or 1)" and "how many L at the end (0, 1 or 2)". That summary is the state |

## Examples

| n | Answer | Why |
|---|---|---|
| 1 | 3 | A, L, P |
| 2 | 8 | all 9 strings except AA |
| 3 | 19 | 27 strings − 7 with two or more A − 1 (LLL) |
| 10101 | 183236316 | modulo 10⁹ + 7 |

## Brute-force intuition

Generate all 3ⁿ strings and check both rules: exponential. When extending a valid prefix by one
character, what must we know to decide if the extension is still valid?

- Appending `A` is allowed only if there has been no `A` yet → need **absences so far** ∈ {0, 1}.
- Appending `L` is allowed only if the prefix does not end in `LL` → need **trailing L count** ∈ {0, 1, 2}.
- Appending `P` is always allowed, and resets the trailing L count.

So 2 × 3 = **6 states** describe everything that matters about a prefix.

```
 state = (absences, trailingL)

 a = 0:   (0,0) ──L──► (0,1) ──L──► (0,2) ──L──► ✗  (LLL)
            ▲            │            │
            └─────P──────┴─────P──────┘     (P from any state returns to l = 0; also P: (0,0) → (0,0))

          every (0,·) ──A──► (1,0)

 a = 1:   (1,0) ──L──► (1,1) ──L──► (1,2) ──L──► ✗  (LLL)
            ▲            │            │
            └─────P──────┴─────P──────┘

          every (1,·) ──A──► ✗  (second absence)

 P: (a, l) → (a, 0)       L: (a, l) → (a, l+1) if l < 2       A: (0, l) → (1, 0)
```

## Deriving the DP (7 questions)

### 1. What is changing?
The length of the prefix, the number of `A`s used (0 or 1), and the length of the current `L` run (0–2).

### 2. What does the state represent?
`dp[i][a][l]` = the number of valid records of length `i` that contain `a` absences and end with
exactly `l` consecutive `L`s.

### 3. What are the choices?
The next character: `P`, `A` (only if `a = 0`), or `L` (only if `l < 2`).

### 4. What is the recurrence?
Written "pull" style (where can state `(a, l)` come from?):
```
dp[i][a][0] = Σ over l' of dp[i-1][a][l']                // appended P
            + (a == 1 ? Σ over l' of dp[i-1][0][l'] : 0)  // appended A (the first absence)
dp[i][a][1] = dp[i-1][a][0]                              // appended L after a non-L
dp[i][a][2] = dp[i-1][a][1]                              // appended a second L
answer      = Σ over all 6 states of dp[n][a][l]
```

### 5. What are the base cases?
`dp[0][0][0] = 1`: the empty record (no A, no trailing L). All other length-0 states are 0.

### 6. What is the order of computation?
Length `i` from 1 to `n`; each length reads only the previous one.

### 7. Can the state space be optimized?
Only the previous length's 6 numbers are needed: O(1) space.
(For huge `n`, the 6×6 transition matrix could be exponentiated, see
[`05-dp-optimization/09-matrix-exponentiation`](../../../05-dp-optimization/09-matrix-exponentiation/).)

## Recursive solution

```java
// ways to append `remaining` more characters, given the current (absences, trailingL)
static long ways(int remaining, int absences, int trailingL) {
    if (remaining == 0) return 1;
    long total = ways(remaining - 1, absences, 0);                       // P
    if (absences == 0) total += ways(remaining - 1, 1, 0);               // A
    if (trailingL < 2) total += ways(remaining - 1, absences, trailingL + 1);   // L
    return total % MOD;
}
```

## Memoization

Cache by `(remaining, absences, trailingL)`. See [`solution/Memoization.java`](solution/Memoization.java).
(It recurses `n` deep; the validator runs with a large stack. For `n = 10⁵`, prefer the loops.)

## Tabulation

`dp[n+1][2][3]`, filled "push" style: from every state, add its count to the states reachable by
P, A and L. See [`solution/Tabulation.java`](solution/Tabulation.java).

## Space optimization

Two `2 × 3` arrays rolled forward. See [`solution/Optimized.java`](solution/Optimized.java).

## Dry run

States written as `(absences, trailingL)`:

| len | (0,0) | (0,1) | (0,2) | (1,0) | (1,1) | (1,2) | total |
|---|---|---|---|---|---|---|---|
| 0 | 1 | 0 | 0 | 0 | 0 | 0 | 1 |
| 1 | 1 | 1 | 0 | 1 | 0 | 0 | 3 |
| 2 | 1+1+0 = 2 | 1 | 1 | (2) + (1) = 3 | 1 | 0 | 8 |
| 3 | 2+1+1 = 4 | 2 | 1 | (4) + (4) = 8 | 3 | 1 | **19** |

For `(1,0)` at length 3: P from the a=1 row (3+1+0 = 4) plus A from the a=0 row (2+1+1 = 4).

## Complexity

| Approach | Time | Space | Reason |
|---|---|---|---|
| Brute force | O(3ⁿ · n) | O(n) | every string, checked |
| Memoization | O(n) | O(n) | 6n states, ≤ 3 moves each; stack depth n |
| Tabulation | O(n) | O(n) | 6n cells |
| Optimized | O(n) | O(1) | 12 numbers |

## Why the optimized approach is better

`n` goes up to 10⁵: the optimized loop uses no recursion and constant memory.

## Common mistakes

- **Tracking total L count instead of trailing L count.** `LPL` is fine; only *consecutive* Ls matter.
- **Forgetting that `A` also resets the L run** (it is not an L).
- **Allowing A from the a = 1 row**, which would permit two absences.
- **Summing without mod** after combining several states.

## Key takeaway

To count strings obeying "local" rules, ask: *what is the least I must remember about the prefix
to check the rules for the next character?* That memory is the state; the rules are the transitions.

## Related problems

- [1220. Count Vowels Permutation](../1220-count-vowels-permutation/), state = last letter only
- [309. Best Time to Buy and Sell Stock with Cooldown](https://leetcode.com/problems/best-time-to-buy-and-sell-stock-with-cooldown/), state = holding / cooling down, taught in [`02-stock-buy-and-sell`](../../../04-advanced-dp-patterns/02-stock-buy-and-sell/)
- Previous: [1411. Number of Ways to Paint N × 3 Grid](../1411-number-of-ways-to-paint-n-3-grid/) · Next: [801. Minimum Swaps To Make Sequences Increasing](../801-minimum-swaps-to-make-sequences-increasing/)
