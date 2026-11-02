# 5. Longest Palindromic Substring

> **Pattern:** Subsequences & strings, palindrome DP on a range `[i..j]` · **Difficulty:** Medium · **Builds on:** [Longest Palindromic Subsequence](../../../03-subsequence-and-knapsack-dp/13-longest-palindromic-subsequence/), [Interval DP](../../../04-advanced-dp-patterns/03-interval-dp/)

## Problem overview

Return the longest contiguous piece of a string that reads the same forwards and backwards. If several have the same maximum length, any one of them is accepted.

Solve [647. Palindromic Substrings](../647-palindromic-substrings/) first. It builds the same `isPal[i][j]` table and only **counts** the `true` cells. Here we **remember the longest** one.

## Link & metadata

| Field | Value |
|---|---|
| Problem | [5. Longest Palindromic Substring](https://leetcode.com/problems/longest-palindromic-substring/) |
| Difficulty | Medium |
| Pattern | Palindrome table on ranges, or expand around center |
| Prerequisite topic | [03-subsequence-and-knapsack-dp/13-longest-palindromic-subsequence](../../../03-subsequence-and-knapsack-dp/13-longest-palindromic-subsequence/) |
| Also useful | [04-advanced-dp-patterns/03-interval-dp](../../../04-advanced-dp-patterns/03-interval-dp/) |

## Examples

| s | Answer | Why |
|---|---|---|
| `"cbbd"` | `"bb"` | the only palindrome longer than 1 |
| `"racecarxyz"` | `"racecar"` | length 7 |
| `"forgeeksskeegfor"` | `"geeksskeeg"` | an even-length palindrome of length 10 |
| `"a"` | `"a"` | a single character is a palindrome |

(The tests use inputs with a **unique** longest answer, so every approach must return the same string. For an input like `"babad"`, both `"bab"` and `"aba"` are correct.)

Running example: `s = "cbbd"`.

## Brute-force intuition

Check all `n(n+1)/2` substrings with two pointers and keep the longest palindrome: O(n³). The repeated work is the same as in 647. Checking `s[i..j]` re-checks `s[i+1..j-1]`, which was already examined.

**Subsequence vs substring:** the [Longest Palindromic Subsequence](../../../03-subsequence-and-knapsack-dp/13-longest-palindromic-subsequence/) topic lets you skip characters, so its state stores a **length** and its mismatch rule is `max(drop left, drop right)`. A substring cannot skip, so the state becomes a **yes/no** and a mismatch simply means `false`.

## Deriving the DP (7 questions)

### 1. What is changing?
The left end `i` and right end `j` of a candidate substring.

### 2. What does the state represent?
> `isPal[i][j]` = `true` when `s[i..j]` is a palindrome.

Alongside the table we keep `bestStart` and `bestLength`, the longest `true` cell found so far.

### 3. What are the choices?
None. As in 647, the state is a check: do the outer characters match, and is the inside a palindrome?

### 4. What is the recurrence?
```
isPal[i][j] = (s[i] == s[j]) && (j - i < 2 || isPal[i+1][j-1])
if isPal[i][j] and (j - i + 1) > bestLength: record (i, j - i + 1)
```

### 5. What are the base cases?
Lengths 1 and 2 are decided by the ends alone (`j - i < 2`). Every single character is a palindrome, so `bestLength` is at least 1 for a non-empty string.

### 6. What is the order of computation?
`isPal[i][j]` reads `isPal[i+1][j-1]`, so loop `i` from `n-1` down to `0`, and `j` from `i` up to `n-1`.

### 7. Can the state space be optimized?
Yes. **Expand around the center:** for each of the 2n−1 centers (a character, or the gap between two neighbours), grow `left--` and `right++` while `s[left] == s[right]`. The last successful window is the longest palindrome with that center. That is O(1) extra space and the same O(n²) time. (A linear-time method, Manacher's algorithm, exists, but it is beyond this course.)

## Recursive solution

```java
static boolean isPal(String s, int i, int j) {
    if (i >= j) return true;
    return s.charAt(i) == s.charAt(j) && isPal(s, i + 1, j - 1);
}
// try every (i, j); keep the longest that isPal
```

## Memoization

Cache `isPal(i, j)` in a `Boolean[][]`. Scan all pairs and keep the longest true one. See [`solution/Memoization.java`](solution/Memoization.java).

## Tabulation

```java
for (int i = n - 1; i >= 0; i--)
    for (int j = i; j < n; j++)
        if (s.charAt(i) == s.charAt(j) && (j - i < 2 || isPal[i + 1][j - 1])) {
            isPal[i][j] = true;
            if (j - i + 1 > bestLength) { bestStart = i; bestLength = j - i + 1; }
        }
return s.substring(bestStart, bestStart + bestLength);
```
See [`solution/Tabulation.java`](solution/Tabulation.java).

## Space optimization

```java
for (int center = 0; center < 2 * n - 1; center++) {
    int left = center / 2, right = left + center % 2;
    while (left >= 0 && right < n && s.charAt(left) == s.charAt(right)) { left--; right++; }
    // the loop stopped one step too far: the palindrome is s[left+1 .. right-1]
    int length = right - left - 1;
    if (length > bestLength) { bestStart = left + 1; bestLength = length; }
}
```
See [`solution/Optimized.java`](solution/Optimized.java).

## Dry run

Tabulation on `s = "cbbd"`:

```
          j:   0     1     2     3
               c     b     b     d
 i=0 (c)       T     F     F     F
 i=1 (b)             T    [T]    F
 i=2 (b)                   T     F
 i=3 (d)                         T
```

| Order | (i, j) | substring | isPal | best so far |
|---|---|---|---|---|
| 1 | (3,3) | d | T | `"d"` (len 1) |
| 2 | (2,2) | b | T | len 1, not longer |
| 3 | (2,3) | bd | F | |
| 4 | (1,1) | b | T | |
| 5 | (1,2) | bb | T | **`"bb"` (len 2)** |
| 6 | (1,3) | bbd | F (b≠d) | |
| 7 | (0,0) | c | T | |
| 8 | (0,1) | cb | F | |
| 9 | (0,2) | cbb | F | |
| 10 | (0,3) | cbbd | F | |

Expand around center on the same input:

| center | start (L, R) | expansions | palindrome |
|---|---|---|---|
| 0 | (0,0) | `c` ok, then out of bounds | `c` |
| 1 | (0,1) | `c≠b` | none |
| 2 | (1,1) | `b`, then `c≠b` | `b` |
| 3 | (1,2) | `b==b`, then `c≠d` | **`bb`** |
| 4 | (2,2) | `b`, then `b≠d` | `b` |
| 5 | (2,3) | `b≠d` | none |
| 6 | (3,3) | `d` | `d` |

## Complexity

| Approach | Time | Space | Why |
|---|---|---|---|
| Brute force | O(n³) | O(1) | n² substrings × O(n) check |
| Memoization | O(n²) | O(n²) | each (i, j) answered once in O(1) |
| Tabulation | O(n²) | O(n²) | upper triangle of the table |
| Expand around center | O(n²) | O(1) | 2n−1 centers × at most n/2 steps each |

With `n ≤ 1000`, the table holds a million booleans. That is fine, but the center method needs none of it.

## Why the optimized approach is better

Both DPs stop the O(n) re-check of every inside range. Expand-around-center also drops the table and does almost no wasted work: each center stops at its first mismatch.

## Common mistakes

1. **Updating the best on `>=`.** That is still correct, but it changes which tie is returned. Know that any longest palindrome is accepted.
2. **Off-by-one after expanding.** When the `while` loop stops, `left` and `right` are one step **outside** the palindrome.
3. **Reversing the string and taking the longest common substring.** This fails on inputs like `"abacdfgdcaba"`, where `"abacd"` reversed also appears but is not a palindrome.
4. **Wrong loop direction** in the table (see 647).

## Key takeaway

> 647 counts the `true` cells of `isPal[i][j]` and problem 5 keeps the longest one. In both, the fastest practical method is expanding from 2n−1 centers.

## Related problems

- [647. Palindromic Substrings](../647-palindromic-substrings/): count instead of maximize.
- [1312. Minimum Insertion Steps to Make a String Palindrome](../1312-minimum-insertion-steps-to-make-a-string-palindrome/): a palindrome range DP that is allowed to skip characters.
- Course topic: [Longest Palindromic Subsequence](../../../03-subsequence-and-knapsack-dp/13-longest-palindromic-subsequence/)
