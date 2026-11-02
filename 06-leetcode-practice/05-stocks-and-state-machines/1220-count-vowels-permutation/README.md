# 1220. Count Vowels Permutation

> **Pattern:** State-machine counting: states = last letter, transitions = allowed "what may follow" rules · **Builds on:** [State Machine DP](../../../04-advanced-dp-patterns/12-state-machine-dp/) · **Difficulty:** Hard

## Problem overview

Count strings of length `n` made only of the vowels `a e i o u` that obey these "what may come
next" rules: after `a` only `e`; after `e` only `a` or `i`; after `i` anything except `i`; after
`o` only `i` or `u`; after `u` only `a`. Return the count modulo 10⁹ + 7.

## Link & metadata

| Field | Value |
|---|---|
| LeetCode | [1220. Count Vowels Permutation](https://leetcode.com/problems/count-vowels-permutation/) |
| Difficulty | Hard |
| Pattern | Counting walks of length `n - 1` in a 5-node directed graph |
| Prerequisite topic | [`04-advanced-dp-patterns/12-state-machine-dp`](../../../04-advanced-dp-patterns/12-state-machine-dp/) |
| Why this pattern | Whether a letter may be appended depends only on the **last** letter, so "last letter" is the complete state |

## Examples

| n | Answer | Why |
|---|---|---|
| 1 | 5 | each vowel alone |
| 2 | 10 | ae, ea, ei, ia, ie, io, iu, oi, ou, ua |
| 5 | 68 | see the dry run |
| 144 | 18208803 | modulo 10⁹ + 7 |

## Brute-force intuition

Generate all 5ⁿ strings and test the rules, or extend a string letter by letter trying only the
allowed successors. Either way, it is exponential. But to know which letters can come next, you
only need the **last letter**. All strings of the same length ending in the same letter have
identical futures, so we count them together.

The state machine:

```
 current letter        may be followed by
 ──────────────        ──────────────────
       a      ───────►  e
       e      ───────►  a, i
       i      ───────►  a, e, o, u
       o      ───────►  i, u
       u      ───────►  a

 Loops in the graph:  a ⇄ e,  e ⇄ i,  i ⇄ o,  i → u → a → e → i, ...
```

Ten edges in total (from → to): `a→e, e→a, e→i, i→a, i→e, i→o, i→u, o→i, o→u, u→a`.

For the DP we need the edges **reversed** ("which letters may precede x?"):

```
a ← e, i, u        e ← a, i        i ← e, o        o ← i        u ← i, o
```

## Deriving the DP (7 questions)

### 1. What is changing?
The length built so far, and the last letter.

### 2. What does the state represent?
`count[len][v]` = the number of valid strings of length `len` that **end** with vowel `v`.

### 3. What are the choices?
The previous letter `p`: any vowel allowed to be followed by `v`.

### 4. What is the recurrence?
```
a' = e + i + u
e' = a + i
i' = e + o
o' = i
u' = i + o            (primes = length len, plain = length len-1; all mod 10^9+7)
answer = a + e + i + o + u at length n
```

### 5. What are the base cases?
Length 1: each vowel count is 1.

### 6. What is the order of computation?
Length 2, 3, …, n.

### 7. Can the state space be optimized?
Only the previous length is read: 5 variables. For astronomically large `n`, the 5×5 transition
matrix can be exponentiated in O(5³ log n); see
[`05-dp-optimization/09-matrix-exponentiation`](../../../05-dp-optimization/09-matrix-exponentiation/).

## Recursive solution

```java
// strings of `remaining` more letters, given the last letter placed
static long count(int remaining, int last) {
    if (remaining == 0) return 1;
    long ways = 0;
    for (int next : FOLLOWERS[last]) ways += count(remaining - 1, next);
    return ways % MOD;
}
```

Here the recursion naturally uses the **forward** edges (who may follow `last`); the tabulation
uses the reversed edges. Both count the same walks.

## Memoization

Cache `count(remaining, last)`. See [`solution/Memoization.java`](solution/Memoization.java).

## Tabulation

`dp[n+1][5]` with the reversed-edge recurrence. See [`solution/Tabulation.java`](solution/Tabulation.java).

## Space optimization

Five variables, updated simultaneously (compute all five new values before overwriting any).
See [`solution/Optimized.java`](solution/Optimized.java).

## Dry run

| len | a = e+i+u | e = a+i | i = e+o | o = i | u = i+o | total |
|---|---|---|---|---|---|---|
| 1 | 1 | 1 | 1 | 1 | 1 | 5 |
| 2 | 1+1+1 = 3 | 1+1 = 2 | 1+1 = 2 | 1 | 1+1 = 2 | 10 |
| 3 | 2+2+2 = 6 | 3+2 = 5 | 2+1 = 3 | 2 | 2+1 = 3 | 19 |
| 4 | 5+3+3 = 11 | 6+3 = 9 | 5+2 = 7 | 3 | 3+2 = 5 | 35 |
| 5 | 9+7+5 = 21 | 11+7 = 18 | 9+3 = 12 | 7 | 7+3 = 10 | **68** |

Each row uses only the previous row's values.

## Complexity

| Approach | Time | Space | Reason |
|---|---|---|---|
| Brute force | O(5ⁿ) | O(n) | every string |
| Memoization | O(n) | O(n) | 5n states, ≤ 4 edges each |
| Tabulation | O(n) | O(n) | n rows of 5 |
| Optimized | O(n) | O(1) | 5 variables |

## Why the optimized approach is better

Constant memory and no recursion depth (n up to 2·10⁴ would overflow a default Java stack in the
memoized version).

## Common mistakes

- **Using forward edges in the "ending at" recurrence.** `a' = e` (a is followed by e) is
  wrong; ask "who may come **before** a?".
- **Updating `a` in place and then using the new `a` to compute `e`.** Use temporaries.
- **Forgetting mod** on the 3-term sum (`a' = e + i + u` can exceed 2³¹ before mod).

## Key takeaway

If the rule for the next element depends only on the current element, the current element is the
state. Draw the graph, reverse the edges, and each new count is a sum over incoming edges.

## Related problems

- [935. Knight Dialer](../935-knight-dialer/), the same walk-counting with 10 states
- [552. Student Attendance Record II](../552-student-attendance-record-ii/), walk-counting where the state also carries history
- Previous: [790. Domino and Tromino Tiling](../790-domino-and-tromino-tiling/) · Next: [1411. Number of Ways to Paint N × 3 Grid](../1411-number-of-ways-to-paint-n-3-grid/)
