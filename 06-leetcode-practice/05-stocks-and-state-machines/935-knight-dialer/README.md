# 935. Knight Dialer

> **Pattern:** State-machine counting: states = keys, transitions = knight moves · **Builds on:** [State Machine DP](../../../04-advanced-dp-patterns/12-state-machine-dp/) · **Difficulty:** Medium

## Problem overview

A chess knight stands on a phone keypad (digits 0–9 laid out in the usual 4×3 grid with `*` and
`#` unusable). It may start on any digit and then makes knight jumps, each landing on a digit.
Count the distinct phone numbers of length `n` it can dial (start digit plus `n - 1` jumps),
modulo 10⁹ + 7.

## Link & metadata

| Field | Value |
|---|---|
| LeetCode | [935. Knight Dialer](https://leetcode.com/problems/knight-dialer/) |
| Difficulty | Medium |
| Pattern | Counting walks of fixed length in a small fixed graph: `count[step][state]` |
| Prerequisite topic | [`04-advanced-dp-patterns/12-state-machine-dp`](../../../04-advanced-dp-patterns/12-state-machine-dp/) |
| Why this pattern | There are 10 states (the digit you are on) and a fixed transition table between them; the number of steps is the only thing that grows |

## Examples

| n | Answer | Why |
|---|---|---|
| 1 | 10 | any single digit |
| 2 | 20 | sum of each digit's number of knight moves |
| 3 | 46 | see the dry run |
| 3131 | 136006598 | modulo 10⁹ + 7 |

## Brute-force intuition

From each digit, try every jump, recursively, `n - 1` times: up to 3^(n-1) numbers per start.
But the number of ways to finish depends only on **which digit you are on** and **how many
digits are left**: 10 × n states.

The state machine (draw this before writing any code):

```
 1 2 3          1 → 6, 8        6 → 0, 1, 7
 4 5 6          2 → 7, 9        7 → 2, 6
 7 8 9          3 → 4, 8        8 → 1, 3
   0            4 → 0, 3, 9     9 → 2, 4
                5 → (none)      0 → 4, 6
```

## Deriving the DP (7 questions)

### 1. What is changing?
The number of digits dialled so far (or still to dial), and the current digit.

### 2. What does the state represent?
`dp[len][d]` = the number of valid numbers of length `len` that **end** on digit `d`.

### 3. What are the choices?
The digit `p` we jumped **from**: any `p` that has a knight move to `d`. (Knight moves are
symmetric, so "from" and "to" lists are the same.)

### 4. What is the recurrence?
```
dp[len][d] = Σ over p in NEIGHBOURS[d] of dp[len-1][p]        (mod 10^9+7)
answer     = Σ over d of dp[n][d]
```

### 5. What are the base cases?
`dp[1][d] = 1` for every digit: a one-digit number is just the starting key.

### 6. What is the order of computation?
`len = 2, 3, …, n`, each from the previous length.

### 7. Can the state space be optimized?
Only the previous length is read: an array of 10 counts. (For enormous `n`, the 10×10 transition
matrix can be raised to a power in O(10³ log n); see
[`05-dp-optimization/09-matrix-exponentiation`](../../../05-dp-optimization/09-matrix-exponentiation/).)

## Recursive solution

```java
static long count(int remaining, int digit) {          // numbers of `remaining` more digits, starting at digit
    if (remaining == 1) return 1;
    long ways = 0;
    for (int next : NEIGHBOURS[digit]) ways += count(remaining - 1, next);
    return ways % MOD;
}
```

## Memoization

Cache `count(remaining, digit)` in a `long[n+1][10]`. See
[`solution/Memoization.java`](solution/Memoization.java).

## Tabulation

`dp[n+1][10]`, length by length. See [`solution/Tabulation.java`](solution/Tabulation.java).

## Space optimization

Two arrays of length 10. See [`solution/Optimized.java`](solution/Optimized.java).

## Dry run

`n = 3`:

| digit | neighbours | len 1 | len 2 | len 3 |
|---|---|---|---|---|
| 0 | 4, 6 | 1 | 2 | 3 + 3 = 6 |
| 1 | 6, 8 | 1 | 2 | 3 + 2 = 5 |
| 2 | 7, 9 | 1 | 2 | 2 + 2 = 4 |
| 3 | 4, 8 | 1 | 2 | 3 + 2 = 5 |
| 4 | 0, 3, 9 | 1 | 3 | 2 + 2 + 2 = 6 |
| 5 | — | 1 | 0 | 0 |
| 6 | 0, 1, 7 | 1 | 3 | 2 + 2 + 2 = 6 |
| 7 | 2, 6 | 1 | 2 | 2 + 3 = 5 |
| 8 | 1, 3 | 1 | 2 | 2 + 2 = 4 |
| 9 | 2, 4 | 1 | 2 | 2 + 3 = 5 |
| **total** | | 10 | 20 | **46** |

## Complexity

| Approach | Time | Space | Reason |
|---|---|---|---|
| Brute force | O(10 · 3^n) | O(n) | up to 3 jumps per step |
| Memoization | O(n · 10 · 3) | O(n · 10) | 10n states, ≤ 3 neighbours each |
| Tabulation | O(n) | O(n) | 10n cells, constant work each |
| Optimized | O(n) | O(1) | two arrays of 10 |

## Why the optimized approach is better

The memoized version recurses `n` deep (5000), which is risky on a default Java stack; the
tabulated and optimized versions loop. The optimized one also stores 20 numbers instead of 50 000.

## Common mistakes

- **Wrong neighbour table.** Derive it from the keypad picture; 5 has none, 0 is reached only from 4 and 6.
- **`n = 1` returning 9 or 0.** The 5 key counts as a 1-digit number.
- **Forgetting mod** between additions.

## Key takeaway

"Count sequences where each element may only follow certain elements" is a **walk count** on a
small graph. The state is the current node; each step multiplies by the transition table.

## Related problems

- [1220. Count Vowels Permutation](../1220-count-vowels-permutation/), the same idea with 5 letters
- [688. Knight Probability in Chessboard](../../02-grid/688-knight-probability-in-chessboard/), knight moves on a chessboard
- Previous pattern: [879. Profitable Schemes](../../03-knapsack/879-profitable-schemes/) · Next: [790. Domino and Tromino Tiling](../790-domino-and-tromino-tiling/)
