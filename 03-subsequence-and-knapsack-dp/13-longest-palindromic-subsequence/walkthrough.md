# Longest Palindromic Subsequence — Walkthrough

## 1. Intuition

Write a word on a strip of paper. You may cut out letters (keeping the rest in order) and you want what's left to read the same from both ends. How many letters can you keep at most?

```
b b b a b
```

Cut out the `a` and you keep `b b b b` — 4 letters, a palindrome.

Two ways of thinking lead to a solution:

**View 1 — the mirror.** Hold the strip up to a mirror: you see `b a b b b`. A palindrome is the same forwards and backwards, so whatever palindrome you keep can be read *both* on the strip and in the mirror image, in order. It is a **common subsequence** of the word and its reverse. So "longest palindromic subsequence" sounds a lot like "longest common subsequence of the word and its mirror image" — and indeed the two numbers are always equal (Section 4.8).

**View 2 — two fingers.** Put one finger on the first letter and one on the last. If the two letters are equal, they can be the **outer pair** of the palindrome: keep both, move both fingers inwards. If they differ, they can't both be kept as the outer pair, so at least one of them is out: move one finger inwards (try both). This is a DP on the **window between the fingers**.

## 2. Problem statement in our own words

Original: [LeetCode 516 — Longest Palindromic Subsequence](https://leetcode.com/problems/longest-palindromic-subsequence/).

Given a non-empty string `s` of lowercase letters, return the length of the longest subsequence of `s` that is a palindrome.

## 3. Examples

| s | answer | one LPS | why |
|---|---|---|---|
| `bbbab` | 4 | `bbbb` | drop the `a` |
| `cbbd` | 2 | `bb` | no three letters can mirror each other |
| `agbdba` | 5 | `abdba` | drop the `g` |
| `abcd` | 1 | `a` | all letters distinct: any single letter |
| `a` | 1 | `a` | edge case: one letter is a palindrome |

**Running example:** `s = "bbbab"` (n = 5), answer **4**.

## 4. Deriving the solution — the 7 questions

We derive the **direct interval DP** (View 2). The LCS view needs no new derivation — it is the [previous topic](../12-longest-common-subsequence/) applied to `s` and `reverse(s)`; Section 4.8 explains why it is valid.

### 4.1 What is changing?

The two ends of the part of the string we still consider: a left index `i` and a right index `j`. Each decision moves `i` right, `j` left, or both.

### 4.2 What does the state represent?

> `lps(i, j)` = the length of the longest palindromic subsequence of the window `s[i..j]` (indices `i` through `j`, inclusive).

Answer: `lps(0, n-1)`.

### 4.3 What are the choices?

- `s[i] == s[j]` → use them as the **outer pair**: they contribute 2 and the inside `s[i+1..j-1]` is solved recursively.
- `s[i] != s[j]` → they can't both be in the palindrome as first and last letters (a palindrome's first and last letters are equal). So **drop the left end** or **drop the right end**.

### 4.4 What is the recurrence?

```
              ┌ 2 + lps(i+1, j-1)                    if s[i] == s[j]
lps(i, j) =   ┤
              └ max( lps(i+1, j), lps(i, j-1) )      otherwise
```

Compare with LCS: "match → diagonal + something; mismatch → max of dropping one side". The shape is the same, but the two "strings" are the two ends of one window.

### 4.5 What are the base cases?

- `i > j` → **0**: empty window. (Happens when a matching pair is next to each other: `lps(i, i+1)` with equal letters calls `lps(i+1, i)`.)
- `i == j` → **1**: a single letter is a palindrome.

### 4.6 What is the order of computation?

`lps(i, j)` depends on windows that are **shorter by one or two**: `(i+1, j-1)`, `(i+1, j)` and `(i, j-1)`. In a table with row `i` and column `j`:

```
              j-1        j
   i      [ (i,j-1) ] [ (i,j) ]      ← needs left
   i+1    [(i+1,j-1)] [(i+1,j)]      ← needs the row BELOW
```

So fill rows from the **bottom** (`i = n-1`) up to `i = 0`, and inside a row go `j = i, i+1, …, n-1` left to right. An equivalent order is "by window length": all windows of length 1, then length 2, and so on.

### 4.7 Can the state space be optimized?

Row `i` reads only row `i+1` and itself → keep two rows of length n: O(n) memory (Section 8). The LCS view can use the single-array LCS from the previous topic.

### 4.8 Why LPS(s) = LCS(s, reverse(s)) (for the curious)

- **LPS ≤ LCS.** A palindromic subsequence of `s`, read backwards, is itself (it's a palindrome), and reading a subsequence of `s` backwards gives a subsequence of `reverse(s)`. So it is common to both.
- **LPS ≥ LCS.** An LCS of `s` and `reverse(s)` is not always a palindrome itself, but one of the same length always exists. Let the common subsequence have length `L`, matched at positions `p₁ < p₂ < … < p_L` in `s` and, mapped back to `s`, at positions `q₁ > q₂ > … > q_L` (reversed order), with `s[p_k] = s[q_k]`. Because `p_k` increases and `q_k` decreases, the pairs with `p_k < q_k` come first; say there are `t` of them, possibly followed by one pair with `p = q`, then the rest with `p_k > q_k`. The first `t` pairs give positions `p₁ < … < p_t < q_t < … < q₁`, a palindrome of length `2t` (+1 if a middle pair `p = q` exists). The last pairs give another palindrome the same way. The two lengths add up to `2L`, so one of them is at least `L`.

## 5. Brute force: recursion

```java
private static int lps(String s, int i, int j) {
    if (i > j) return 0;
    if (i == j) return 1;
    if (s.charAt(i) == s.charAt(j)) return 2 + lps(s, i + 1, j - 1);
    return Math.max(lps(s, i + 1, j), lps(s, i, j - 1));
}
// answer: lps(s, 0, n - 1)
```

Full file: [solution/BruteForce.java](solution/BruteForce.java).

### Recursion tree for "bbbab"

Indices: `0:b 1:b 2:b 3:a 4:b`.

```
lps(0,4) "bbbab"  b = b → 2 + lps(1,3)                          = 2 + 2 = 4
         lps(1,3) "bba"  b ≠ a → max(lps(2,3), lps(1,2))         = max(1, 2) = 2
                  ├─ lps(2,3) "ba"  b ≠ a → max(lps(3,3), lps(2,2)) = max(1, 1) = 1
                  └─ lps(1,2) "bb"  b = b → 2 + lps(2,1)          = 2 + 0 = 2
                                            lps(2,1) = 0  (empty window)
```

### Why it is slow

This example matched its outer pair immediately, so the tree is tiny. On a string with **no** matching ends, every call splits in two and each branch shrinks the window by only one: up to 2ⁿ calls. And the branches overlap: in `"cbbd"`, `lps(0,3)` calls `lps(1,3)` and `lps(0,2)`, and **both** of those call `lps(1,2)`:

```
lps(0,3) "cbbd"
├─ lps(1,3) "bbd" → lps(2,3), lps(1,2) ◄──┐
└─ lps(0,2) "cbb" → lps(1,2), lps(0,1)  ◄─┘  same window, solved twice
```

In general, "drop left then right" and "drop right then left" always lead to the same window `(i+1, j-1)`.

## 6. Memoization (top-down)

The window `(i, j)` with `i ≤ j` has `n(n+1)/2` possible values. Cache them:

```java
if (i > j) return 0;
if (i == j) return 1;
if (memo[i][j] != -1) return memo[i][j];
memo[i][j] = (s.charAt(i) == s.charAt(j)) ? 2 + lps(s, i + 1, j - 1)
                                          : Math.max(lps(s, i + 1, j), lps(s, i, j - 1));
return memo[i][j];
```

Full file: [solution/Memoization.java](solution/Memoization.java).

On `"cbbd"`: `lps(1,3)` is computed first and stores `lps(1,2) = 2` along the way; when `lps(0,2)` asks for `lps(1,2)`, it is a **cache hit**. On `"bbbab"` the memo ends up holding `lps(0,4)=4, lps(1,3)=2, lps(2,3)=1, lps(1,2)=2` — every window visited is solved once.

## 7. Tabulation (bottom-up)

```java
int[][] dp = new int[n][n];          // dp[i][j] = LPS of s[i..j]; i > j stays 0
for (int i = n - 1; i >= 0; i--) {
    dp[i][i] = 1;
    for (int j = i + 1; j < n; j++) {
        if (s.charAt(i) == s.charAt(j)) dp[i][j] = 2 + dp[i + 1][j - 1];
        else dp[i][j] = Math.max(dp[i + 1][j], dp[i][j - 1]);
    }
}
return dp[0][n - 1];
```

Full file: [solution/Tabulation.java](solution/Tabulation.java).

Only the **upper triangle** (`i ≤ j`) is meaningful. Cells below the diagonal stay 0 and stand for empty windows — that is exactly what `dp[i+1][j-1]` reads when `j = i+1`.

### Filling the table for "bbbab", row by row from the bottom

```
s:    0:b  1:b  2:b  3:a  4:b
```

Row `i = 4` (window starts at the last `b`): `dp[4][4] = 1`.

Row `i = 3` (`a`):
- `dp[3][3] = 1`
- `dp[3][4]` = "ab": a ≠ b → max(dp[4][4]=1, dp[3][3]=1) = **1**

Row `i = 2` (`b`):
- `dp[2][2] = 1`
- `dp[2][3]` = "ba": b ≠ a → max(dp[3][3]=1, dp[2][2]=1) = **1**
- `dp[2][4]` = "bab": b = b → 2 + dp[3][3]=1 = **3**

Row `i = 1` (`b`):
- `dp[1][1] = 1`
- `dp[1][2]` = "bb": b = b → 2 + dp[2][1]=0 = **2**
- `dp[1][3]` = "bba": b ≠ a → max(dp[2][3]=1, dp[1][2]=2) = **2**
- `dp[1][4]` = "bbab": b = b → 2 + dp[2][3]=1 = **3**

Row `i = 0` (`b`):
- `dp[0][0] = 1`
- `dp[0][1]` = "bb": 2 + dp[1][0]=0 = **2**
- `dp[0][2]` = "bbb": 2 + dp[1][1]=1 = **3**
- `dp[0][3]` = "bbba": b ≠ a → max(dp[1][3]=2, dp[0][2]=3) = **3**
- `dp[0][4]` = "bbbab": b = b → 2 + dp[1][3]=2 = **4**

Complete table (`.` = empty window, value 0):

```
 i \ j    0    1    2    3    4
   0      1    2    3    3    4   ← answer dp[0][4] = 4
   1      .    1    2    2    3
   2      .    .    1    1    3
   3      .    .    .    1    1
   4      .    .    .    .    1
```

### The same table filled by window length

```
length 1: dp[0][0] dp[1][1] dp[2][2] dp[3][3] dp[4][4]        = 1 1 1 1 1
length 2: dp[0][1] dp[1][2] dp[2][3] dp[3][4]                 = 2 2 1 1
length 3: dp[0][2] dp[1][3] dp[2][4]                          = 3 2 3
length 4: dp[0][3] dp[1][4]                                   = 3 3
length 5: dp[0][4]                                            = 4
```

Each diagonal of the table is one window length. Both orders are correct because both compute every shorter window before any longer one that needs it. The length order is the one you will use for harder interval DPs in [module 04](../../04-advanced-dp-patterns/03-interval-dp/).

### The LCS view on the same input

`s = "bbbab"`, `reverse(s) = "babbb"`. The LCS table from [topic 12](../12-longest-common-subsequence/):

```
        ""  b  a  b  b  b
  ""     0  0  0  0  0  0
  b      0  1  1  1  1  1
  b      0  1  1  2  2  2
  b      0  1  1  2  3  3
  a      0  1  2  2  3  3
  b      0  1  2  3  3  4   ← LCS = 4 = LPS ✔
```

Backtracking (ties → up) gives `"bbbb"`. The code is in [solution/ViaLCS.java](solution/ViaLCS.java).

## 8. Space optimization

Row `i` of the interval table needs:

- `dp[i+1][j]` and `dp[i+1][j-1]` — the row **below**;
- `dp[i][j-1]` — the current row, to the left.

So keep two arrays, `below` (row `i+1`) and `current` (row `i`), and swap after each row:

```java
int[] below = new int[n], current = new int[n];
for (int i = n - 1; i >= 0; i--) {
    current[i] = 1;
    for (int j = i + 1; j < n; j++) {
        if (s.charAt(i) == s.charAt(j)) {
            int inner = (j - 1 >= i + 1) ? below[j - 1] : 0;   // empty window when j == i+1
            current[j] = 2 + inner;
        } else {
            current[j] = Math.max(below[j], current[j - 1]);
        }
    }
    int[] swap = below; below = current; current = swap;
}
return below[n - 1];
```

Full file: [solution/Optimized.java](solution/Optimized.java).

The one subtlety: in the 2D table the cell below the diagonal is a clean 0, but in the rolled array `below[i]` holds a leftover value from an older row. So the "empty window" case `j == i+1` must be handled explicitly.

## 9. Dry run on a full example

A second input, `s = "agbdba"` (answer 5), using the interval table by window length. Indices `0:a 1:g 2:b 3:d 4:b 5:a`.

| window | chars | ends equal? | computation | value |
|---|---|---|---|---|
| (0,1) | ag | no | max(1,1) | 1 |
| (1,2) | gb | no | max(1,1) | 1 |
| (2,3) | bd | no | max(1,1) | 1 |
| (3,4) | db | no | max(1,1) | 1 |
| (4,5) | ba | no | max(1,1) | 1 |
| (0,2) | agb | no | max(dp[1][2]=1, dp[0][1]=1) | 1 |
| (1,3) | gbd | no | max(1, 1) | 1 |
| (2,4) | bdb | **yes** | 2 + dp[3][3]=1 | 3 |
| (3,5) | dba | no | max(1, 1) | 1 |
| (0,3) | agbd | no | max(1, 1) | 1 |
| (1,4) | gbdb | no | max(dp[2][4]=3, dp[1][3]=1) | 3 |
| (2,5) | bdba | no | max(dp[3][5]=1, dp[2][4]=3) | 3 |
| (0,4) | agbdb | no | max(dp[1][4]=3, dp[0][3]=1) | 3 |
| (1,5) | gbdba | no | max(dp[2][5]=3, dp[1][4]=3) | 3 |
| (0,5) | agbdba | **yes** | 2 + dp[1][4]=3 | **5** |

The palindrome is `a` + `bdb` + `a` = `"abdba"`. All five files in `solution/` return 5 for this input.

### Bonus: minimum insertions (LC 1312)

[Minimum Insertion Steps to Make a String Palindrome](https://leetcode.com/problems/minimum-insertion-steps-to-make-a-string-palindrome/) asks for the fewest characters to insert anywhere so that `s` becomes a palindrome. Answer: **`n − LPS`**.

- *Enough:* keep the LPS as the skeleton. Every other character gets a mirror partner inserted at the matching position on the other side. That's `n − LPS` insertions.
- *Necessary:* in the final palindrome, original characters that are mirrored by **original** characters form a palindromic subsequence of `s`, so there are at most `LPS` of them; every other original character needs an inserted partner.

Examples: `"zzazz"` → 0, `"mbadm"` → 5 − 3 = 2 (LPS `"mam"` or `"mbm"`), `"leetcode"` → 8 − 3 = 5. The same `n − LPS` is also the minimum number of **deletions** to make `s` a palindrome. See `minInsertions` in [solution/ViaLCS.java](solution/ViaLCS.java).

## 10. Complexity analysis

| Approach | Time | Space | Why |
|---|---|---|---|
| Brute force | O(2ⁿ) | O(n) | Two branches per mismatch; window shrinks by ≥ 1 per call. |
| Memoization | O(n²) | O(n²) | n(n+1)/2 windows × O(1) work; recursion depth ≤ n. |
| Tabulation (interval) | O(n²) | O(n²) | Upper triangle of an n × n table. |
| Two rows (interval) | O(n²) | O(n) | Same cells, two arrays. |
| Via LCS | O(n²) | O(n) | n × n LCS with a single array. |

The interval table does about **half** the work of the LCS view (only `i ≤ j`), but both are O(n²); with n ≤ 1000 either is instant.

## 11. Why this approach works

Every palindromic subsequence of `s[i..j]` either uses both `s[i]` and `s[j]`, or misses at least one of them.

- If it misses `s[i]`, it lives in `s[i+1..j]` → covered by `lps(i+1, j)`. If it misses `s[j]` → covered by `lps(i, j-1)`.
- If it uses both, they are its first and last letters, so they're equal, and what's between them is a palindromic subsequence of `s[i+1..j-1]` → `2 + lps(i+1, j-1)`.

When `s[i] == s[j]`, using both ends is never worse than dropping one. Take any palindrome `P` that misses an end, so it lies inside `s[i+1..j]` or `s[i..j-1]`. If `|P| ≤ 1`, the pair `s[i] s[j]` alone is already as long. Otherwise strip `P`'s own outer pair: what remains, `P'`, has length `|P| − 2` and lies strictly between those two positions, hence inside `s[i+1..j-1]`. Then `s[i] + P' + s[j]` is a palindrome of length `|P|` that uses both ends. So `2 + lps(i+1, j-1)` is at least as good as either drop.

## 12. Common mistakes

1. **Wrong fill order.** Filling `i` from 0 upward reads `dp[i+1][…]` before it exists. Fill `i` from `n-1` down, or by window length.
2. **Forgetting the empty window.** `"bb"` must give 2: `2 + lps(1, 0)` where `lps(1, 0) = 0`. In the rolled version, handle `j == i+1` explicitly.
3. **Confusing subsequence with substring.** For `"bbbab"`, the longest palindromic **substring** is `"bbb"` (3), but the subsequence answer is 4.
4. **Returning `dp[n-1][0]` (the wrong corner).** The whole string is window `(0, n-1)`.
5. **Assuming the LCS of `s` and `reverse(s)` is always itself a palindrome.** Its length equals the LPS, but a backtracked LCS string may not be a palindrome for some inputs; if you need the actual palindrome, backtrack the interval table instead.

## 13. When to use this technique / how to recognize similar problems

- The question is about **one** string and its **palindromic** structure → think "string vs. its reverse" (LCS) or "window `(i, j)`" (interval).
- The decision involves **both ends** of a range at once → interval DP; fill by increasing length.
- "Minimum insertions / deletions to make a palindrome" → `n − LPS`.
- Substrings instead of subsequences → the same `(i, j)` window, but a boolean `isPal[i][j] = s[i]==s[j] && isPal[i+1][j-1]` ([LC 647](https://leetcode.com/problems/palindromic-substrings/), [LC 5](https://leetcode.com/problems/longest-palindromic-substring/)).

## 14. Summary and revision notes

- `LPS(s) = LCS(s, reverse(s))` — a one-line reduction.
- Interval form: `lps(i, j)`; equal ends → `2 + inside`; different ends → drop one.
- Base: empty window 0, single letter 1.
- Fill `i` from right to left and `j` from `i` rightwards — or by window length.
- O(n) memory with two rows (watch the `j == i+1` case).
- Minimum insertions (LC 1312) = minimum deletions = `n − LPS`.

```
┌───────────────────────────────────────────────────────────────┐
│ lps(i, j) = 2 + lps(i+1, j-1)                 if s[i] == s[j]  │
│           = max(lps(i+1, j), lps(i, j-1))     otherwise        │
│ lps(i, i) = 1,   lps(i, j) = 0 for i > j                       │
│ answer lps(0, n-1)  =  LCS(s, reverse(s))                      │
└───────────────────────────────────────────────────────────────┘
```

## 15. Exercises

**Exercise 1.** Fill the interval table for `s = "cbbd"`. What is the LPS?

<details>
<summary>Answer</summary>

```
 i \ j   0  1  2  3
   0     1  1  2  2
   1     .  1  2  2
   2     .  .  1  1
   3     .  .  .  1
```
`dp[1][2] = 2` ("bb"), `dp[0][2] = max(dp[1][2], dp[0][1]) = 2`, `dp[1][3] = max(dp[2][3], dp[1][2]) = 2`, `dp[0][3] = max(dp[1][3], dp[0][2]) = 2`. LPS = 2 (`"bb"`).
</details>

**Exercise 2.** How many insertions does `"mbadm"` need to become a palindrome? Give one resulting palindrome.

<details>
<summary>Answer</summary>

LPS(`mbadm`) = 3 (`"mam"`, using the `m`, the `a`, the `m`), so `5 − 3 = 2` insertions. Keep the skeleton `m _ a _ m`; `b` and `d` each need a partner: `m b d a d b m` = `"mbdadbm"`.
</details>

**Exercise 3.** A string is a *k-palindrome* if deleting at most `k` characters makes it a palindrome. Is `"abcdeca"` a 2-palindrome?

<details>
<summary>Answer</summary>

LPS(`abcdeca`) = 5 (`"acdca"` or `"aceca"`). Minimum deletions = `7 − 5 = 2 ≤ 2`, so yes (delete `b` and `e`, leaving `"acdca"`).
</details>

**Exercise 4.** For `"bbbab"`, compute the longest palindromic **substring** and compare with the LPS. Which cell rule changes?

<details>
<summary>Answer</summary>

The longest palindromic substring is `"bbb"` (length 3) or `"bab"` (length 3); the LPS is 4. For substrings, a window is a palindrome only if `s[i] == s[j]` **and** the inside window is a palindrome (`isPal[i+1][j-1]`); there is no "drop one end" option, because a substring cannot skip characters.
</details>
