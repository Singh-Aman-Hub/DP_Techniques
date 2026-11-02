# 647. Palindromic Substrings

> **Pattern:** Subsequences & strings, palindrome DP on a range `[i..j]` · **Difficulty:** Medium · **Builds on:** [Longest Palindromic Subsequence](../../../03-subsequence-and-knapsack-dp/13-longest-palindromic-subsequence/), [Interval DP](../../../04-advanced-dp-patterns/03-interval-dp/)

## Problem overview

Count how many substrings of a string are palindromes, meaning they read the same forwards and backwards. Substrings at different positions count separately, even if their text is the same. In `"aaa"` the three single `"a"`s count as three.

## Link & metadata

| Field | Value |
|---|---|
| Problem | [647. Palindromic Substrings](https://leetcode.com/problems/palindromic-substrings/) |
| Difficulty | Medium |
| Pattern | Palindrome table `isPal[i][j]`, filled from short ranges to long ones |
| Prerequisite topic | [03-subsequence-and-knapsack-dp/13-longest-palindromic-subsequence](../../../03-subsequence-and-knapsack-dp/13-longest-palindromic-subsequence/) |
| Also useful | [04-advanced-dp-patterns/03-interval-dp](../../../04-advanced-dp-patterns/03-interval-dp/), [04-advanced-dp-patterns/01-dp-on-strings](../../../04-advanced-dp-patterns/01-dp-on-strings/) |

## Examples

| s | Answer | The palindromes |
|---|---|---|
| `"abc"` | 3 | `a`, `b`, `c` |
| `"aaa"` | 6 | `a`,`a`,`a`, `aa`,`aa`, `aaa` |
| `"abba"` | 6 | `a`,`b`,`b`,`a`, `bb`, `abba` |
| `"a"` | 1 | `a` |

Running example: `s = "abba"`.

## Brute-force intuition

There are `n(n+1)/2` substrings. Check each with two pointers moving inward: O(n) per check, O(n³) total.

The waste: checking `"abba"` compares `a/a` and then checks `"bb"`, but `"bb"` was already checked on its own. **A range is a palindrome exactly when its ends match and its inside is a palindrome.** The inside is a smaller range, which is the overlapping subproblem.

## Deriving the DP (7 questions)

### 1. What is changing?
The two ends of a substring: a left index `i` and a right index `j`. This is a **range** state, as in [interval DP](../../../04-advanced-dp-patterns/03-interval-dp/). Unlike LCS, both indices point into the **same** string.

### 2. What does the state represent?
> `isPal[i][j]` = `true` when `s[i..j]` (both ends included) is a palindrome.

The answer counts the `true` cells.

### 3. What are the choices?
There is no real choice here. This is a yes/no check. The state "decides" by looking at the outer pair `s[i]`, `s[j]` and the inner range `s[i+1..j-1]`.

### 4. What is the recurrence?
```
isPal[i][j] = (s[i] == s[j]) && (j - i < 2  ||  isPal[i+1][j-1])
```
- `s[i] == s[j]`: the outer characters must match.
- `j - i < 2`: lengths 1 and 2 have no inside to check (or an empty one).
- `isPal[i+1][j-1]`: otherwise the inside must already be a palindrome.

### 5. What are the base cases?
- Length 1 (`i == j`): always a palindrome.
- Length 2 (`j == i+1`): a palindrome when the two characters are equal.

Both are covered by the `j - i < 2` clause.

### 6. What is the order of computation?
`isPal[i][j]` needs `isPal[i+1][j-1]`, which has a **larger** `i` and a smaller `j`. So:
- loop `i` from `n-1` **down** to `0`, and
- loop `j` from `i` **up** to `n-1`.

(Looping by length, shortest first, also works.)

```
      j ->
 i  [ .  .  .  . ]      (i, j) reads (i+1, j-1): the row below, one column left
 |  [    .  .  . ]      so rows must be filled bottom-up
 v  [       .  . ]
    [          . ]
```

### 7. Can the state space be optimized?
Row `i` reads only row `i+1`, so one row of booleans is enough (O(n)). There is an even better idea: **expand around the center**. Every palindrome has a center, which is either a character or the gap between two characters, giving 2n−1 centers. Grow outward from each center while the ends match, and count every step. That is O(n²) time and **O(1)** space. See [`solution/Optimized.java`](solution/Optimized.java).

## Recursive solution

```java
static boolean isPal(String s, int i, int j) {
    if (i >= j) return true;                       // length 0 or 1
    return s.charAt(i) == s.charAt(j) && isPal(s, i + 1, j - 1);
}
// count = number of (i, j) with i <= j and isPal(s, i, j)
```

## Memoization

Store `isPal(i, j)` in a `Boolean[][]` (`null` = not computed) and count the true results over all pairs. See [`solution/Memoization.java`](solution/Memoization.java).

## Tabulation

```java
for (int i = n - 1; i >= 0; i--)
    for (int j = i; j < n; j++)
        if (s.charAt(i) == s.charAt(j) && (j - i < 2 || isPal[i + 1][j - 1])) {
            isPal[i][j] = true;
            count++;
        }
```
See [`solution/Tabulation.java`](solution/Tabulation.java).

## Space optimization

```java
for (int center = 0; center < 2 * n - 1; center++) {
    int left = center / 2, right = left + center % 2;   // odd centers: a gap
    while (left >= 0 && right < n && s.charAt(left) == s.charAt(right)) {
        count++; left--; right++;
    }
}
```
This is the same recurrence read from the inside out. Each successful step outward is one `true` cell `isPal[left][right]` whose inside was the previous step.

## Dry run

`s = "abba"` (indices 0..3). Rows are filled in the order `i = 3, 2, 1, 0`:

```
          j:   0     1     2     3
               a     b     b     a
 i=0 (a)       T     F     F     T      <- s[0]==s[3] and isPal[1][2]
 i=1 (b)             T     T     F      <- "bb": length 2, ends equal
 i=2 (b)                   T     F
 i=3 (a)                         T
```

| Order | (i, j) | substring | ends equal? | inside | isPal | count |
|---|---|---|---|---|---|---|
| 1 | (3,3) | a | yes | length 1 | T | 1 |
| 2 | (2,2) | b | yes | length 1 | T | 2 |
| 3 | (2,3) | ba | no | | F | 2 |
| 4 | (1,1) | b | yes | length 1 | T | 3 |
| 5 | (1,2) | bb | yes | length 2 | T | 4 |
| 6 | (1,3) | bba | no | | F | 4 |
| 7 | (0,0) | a | yes | length 1 | T | 5 |
| 8 | (0,1) | ab | no | | F | 5 |
| 9 | (0,2) | abb | no | | F | 5 |
| 10 | (0,3) | abba | yes | isPal[1][2] = T | T | **6** |

## Complexity

| Approach | Time | Space | Why |
|---|---|---|---|
| Brute force | O(n³) | O(1) | n² substrings × O(n) check |
| Memoization | O(n²) | O(n²) | n² states, O(1) each, plus a Boolean table |
| Tabulation | O(n²) | O(n²) | one pass over the upper triangle |
| Expand around center | O(n²) | O(1) | 2n−1 centers, each expands at most n/2 times |

## Why the optimized approach is better

The DP removes the O(n) re-check of every inside. Expand-around-center does the same work but visits only the cells that are actually `true`, plus one failing step per center. It needs no table, and it usually runs faster in practice.

## Common mistakes

1. **Looping `i` upward.** Then `isPal[i+1][j-1]` has not been computed yet and still reads as `false`.
2. **Missing the even-length centers.** Expanding only around characters misses `"bb"` and `"abba"`.
3. **Counting distinct palindromes.** The problem counts positions, not distinct strings.
4. **Mixing this up with Longest Palindromic Subsequence.** A subsequence can skip characters (so it uses `max` of shrinking either end). A substring cannot.

## Key takeaway

> A palindrome check on `s[i..j]` reduces to the ends plus `s[i+1..j-1]`. Fill `i` downward and `j` upward, or expand from the 2n−1 centers.

## Related problems

- [5. Longest Palindromic Substring](../5-longest-palindromic-substring/): the same table, keeping the longest `true` cell instead of counting.
- [1312. Minimum Insertion Steps to Make a String Palindrome](../1312-minimum-insertion-steps-to-make-a-string-palindrome/): the subsequence-style palindrome range DP.
- [718. Maximum Length of Repeated Subarray](../718-maximum-length-of-repeated-subarray/): another contiguous-block DP.
- Course topic: [Longest Palindromic Subsequence](../../../03-subsequence-and-knapsack-dp/13-longest-palindromic-subsequence/)
