# 664. Strange Printer

> **Pattern:** Interval DP on one string, "merge the first character's stroke with a later equal character" · **Difficulty:** Hard · **Builds on:** [Interval DP](../../../04-advanced-dp-patterns/03-interval-dp/), [Longest Palindromic Subsequence](../../../03-subsequence-and-knapsack-dp/13-longest-palindromic-subsequence/)

## Problem overview

A printer can do one thing per turn: print a run of **one repeated character** over any contiguous block of positions, overwriting what was there. Starting from blank paper, return the fewest turns needed to produce a given string.

Example: `"aba"` takes 2 turns. Print `aaa`, then print `b` over the middle.

## Link & metadata

| Field | Value |
|---|---|
| Problem | [664. Strange Printer](https://leetcode.com/problems/strange-printer/) |
| Difficulty | Hard |
| Pattern | Range DP `dp[i][j]`, where the first character's stroke is either alone or merged with an equal character `s[k]` |
| Prerequisite topic | [04-advanced-dp-patterns/03-interval-dp](../../../04-advanced-dp-patterns/03-interval-dp/) |
| Also useful | [03-subsequence-and-knapsack-dp/13-longest-palindromic-subsequence](../../../03-subsequence-and-knapsack-dp/13-longest-palindromic-subsequence/) |

## Examples

| s | Answer | Why |
|---|---|---|
| `"aaabbb"` | 2 | `aaa`, then `bbb` |
| `"aba"` | 2 | `aaa`, then `b` in the middle |
| `"abab"` | 3 | `aaa` over positions 0–2, then `b` at 1, then `b` at 3 |
| `"tbgtgb"` | 4 | |
| `"a"` | 1 | one turn |

Running example: `s = "abab"`.

## Brute-force intuition

Simulating every possible stroke (every block × every character) is a huge search. We need a structural fact instead.

**Look at the first character `s[i]` of a range.** Some stroke paints its final colour. We may assume this stroke starts exactly at `i`: inside this range nothing is to its left, so shrinking the stroke to start at `i` changes nothing. Two cases remain:

1. **The stroke is useful only at `i`.** It costs 1 turn, and the rest `s[i+1..j]` is a separate problem: `1 + f(i+1, j)`.
2. **The same stroke also produces a later character `s[k]` with `s[k] == s[i]`** (take the first such `k` where its colour survives). The stroke covers `i..k`. Everything strictly between, `s[i+1..k-1]`, is printed **on top** of it later and is an independent sub-problem. Position `i` itself costs **nothing extra**: the stroke that prints `s[k]` in the sub-problem `f(k, j)` can simply be stretched left to start at `i`. Total: `f(i+1, k-1) + f(k, j)`.

```
 s:     a  b  a  b
        ^-----^          one stroke "aaa" covers 0..2 (merged with s[2])
           b             inside part s[1..1] printed on top: f(1,1) = 1
              [a  b]     f(2,3) = 2, and its 'a' stroke is the same stroke as s[0]'s
 total = f(1,1) + f(2,3) = 1 + 2 = 3
```

The sub-problems are ranges of the same string and they overlap heavily, which makes this interval DP.

## Deriving the DP (7 questions)

### 1. What is changing?
The range `[i..j]` of the string still to be printed.

### 2. What does the state represent?
> `dp[i][j]` = the fewest turns needed to print exactly `s[i..j]` on blank paper.

### 3. What are the choices?
What happens to the stroke that prints `s[i]`:
- it stays alone, or
- it is merged with the stroke of some later `s[k]` with `s[k] == s[i]`, `i < k ≤ j`.

### 4. What is the recurrence?
```
dp[i][j] = min( 1 + dp[i+1][j],
                min over k in (i, j] with s[k] == s[i] of  dp[i+1][k-1] + dp[k][j] )
```
`dp[i+1][k-1]` is the inside, printed on top of the long stroke. It is an empty range (0) when `k = i+1`. `dp[k][j]` already pays for the stroke that prints `s[k]`, and that same stroke now also covers `i`.

### 5. What are the base cases?
- `dp[i][i] = 1`: one character takes one turn.
- An empty range (`i > j`) costs 0.

### 6. What is the order of computation?
`dp[i][j]` reads `dp[i+1][...]` (a larger start) and `dp[k][j]` with `k > i`. Loop `i` from `n-1` **down** to 0, and `j` from `i` up to `n-1`. Use an `(n+1) × (n+1)` table so that `dp[i+1][...]` with `i+1 = n` is a valid (empty, 0) cell.

### 7. Can the state space be optimized?
Row `i` reads **many** later rows (`dp[k][j]` for every `k > i`), not just row `i+1`, so a rolling row does not work. The table stays O(n²). One useful preprocessing step: **collapse runs of equal characters** (`"aaabbb"` → `"ab"`). Repeated neighbours never need an extra turn, and a shorter string speeds up the O(n³) loops. There is no separate `Optimized.java`.

## Recursive solution

```java
static int turns(String s, int i, int j) {
    if (i > j) return 0;
    int best = 1 + turns(s, i + 1, j);                         // s[i] printed alone
    for (int k = i + 1; k <= j; k++)
        if (s.charAt(k) == s.charAt(i))                        // share the stroke with s[k]
            best = Math.min(best, turns(s, i + 1, k - 1) + turns(s, k, j));
    return best;
}
```

## Memoization

Cache `(i, j)` in an `n × n` table. See [`solution/Memoization.java`](solution/Memoization.java).

## Tabulation

```java
for (int i = n - 1; i >= 0; i--)
    for (int j = i; j < n; j++) {
        dp[i][j] = 1 + dp[i + 1][j];
        for (int k = i + 1; k <= j; k++)
            if (s.charAt(k) == s.charAt(i))
                dp[i][j] = Math.min(dp[i][j], dp[i + 1][k - 1] + dp[k][j]);
    }
```
When `j == i`, the loop body gives `1 + dp[i+1][i] = 1 + 0 = 1`, the base case. When `k == i+1`, `dp[i+1][i]` is the empty range, 0. See [`solution/Tabulation.java`](solution/Tabulation.java).

## Space optimization

Not applicable (see question 7). Collapsing repeated characters shrinks `n`, but not the shape of the table.

## Dry run

`s = "abab"` (indices 0..3):

| (i, j) | substring | alone: 1 + dp[i+1][j] | merges with k (s[k]==s[i]) | dp |
|---|---|---|---|---|
| (3,3) | b | 1 + 0 | — | 1 |
| (2,2) | a | 1 | — | 1 |
| (2,3) | ab | 1 + 1 = 2 | none | 2 |
| (1,1) | b | 1 | — | 1 |
| (1,2) | ba | 1 + 1 = 2 | none | 2 |
| (1,3) | bab | 1 + dp[2][3] = 3 | k=3: dp[2][2] + dp[3][3] = 2 | 2 |
| (0,0) | a | 1 | — | 1 |
| (0,1) | ab | 1 + 1 = 2 | none | 2 |
| (0,2) | aba | 1 + dp[1][2] = 3 | k=2: dp[1][1] + dp[2][2] = 2 | 2 |
| (0,3) | abab | 1 + dp[1][3] = 3 | k=2: dp[1][1] + dp[2][3] = 1 + 2 = 3 | **3** |

```
          j:  0   1   2   3
              a   b   a   b
 i=0 (a)      1   2   2  [3]
 i=1 (b)          1   2   2
 i=2 (a)              1   2
 i=3 (b)                  1
```

## Complexity

| Approach | Time | Space | Why |
|---|---|---|---|
| Recursion | exponential | O(n) | many choices of `k` at every level, with no reuse |
| Memoization | O(n³) | O(n²) | n²/2 ranges × up to n values of `k` |
| Tabulation | O(n³) | O(n²) | same, bottom-up |

With n ≤ 100, n³ = 10⁶, which is fast.

## Why the optimized approach is better

The key fact, that the first character's stroke either stands alone or is shared with a later equal character, shrinks an enormous search over stroke sequences to O(n²) ranges with O(n) choices each.

## Common mistakes

1. **Counting character changes** (`"aba"` → 3). Overwriting allows 2.
2. **Merging `s[i]` with `s[k]` but charging `1 + ...`.** The shared stroke is already paid for inside `dp[k][j]`.
3. **Splitting like matrix chain** (`dp[i][k] + dp[k+1][j]` with no merge). That misses that one stroke can span both halves. (A correct variant of that form exists: when `s[i] == s[j]`, set `dp[i][j] = dp[i][j-1]`, because the last character rides on the first one's stroke. It is easy to get wrong, though, so learn the merge form first.)
4. **Index overflow** at `dp[i+1][...]` when `i = n-1`. Allocate `n + 1` rows.

## Key takeaway

> In range problems where one action can cover **two equal characters at once**, fix the first character and decide which later equal character it "travels with". The part between them becomes an independent inside range.

## Related problems

- [546. Remove Boxes](../546-remove-boxes/): the same "join `s[i]` with an equal later element" idea, which there needs a third state variable.
- [1312. Minimum Insertion Steps to Make a String Palindrome](../../04-subsequences-and-strings/1312-minimum-insertion-steps-to-make-a-string-palindrome/): a gentler range DP on one string.
- [1547. Minimum Cost to Cut a Stick](../1547-minimum-cost-to-cut-a-stick/): range DP where you choose the first action.
- Course topic: [Interval DP](../../../04-advanced-dp-patterns/03-interval-dp/)
