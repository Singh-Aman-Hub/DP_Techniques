# Longest Common Subsequence — Walkthrough

## 1. Intuition

Two friends each wrote down the list of cities they visited on a road trip, in order:

```
Asha:   Agra, Bhopal, Chennai, Bhopal, Delhi, Agra, Bhopal
Bilal:  Bhopal, Delhi, Chennai, Agra, Bhopal, Agra
```

They want the longest list of cities that **both** visited **in the same order** (each may have extra stops in between). For example `Bhopal, Chennai, Bhopal, Agra` works for both lists. Is there a list of 5? How would you be sure?

Compare the **last** entries of the two lists. Asha's ends with Bhopal, Bilal's with Agra. They differ, so *at least one of these two last stops is not the last city of the common list*. Either Asha's final Bhopal is useless (cross it out and solve the shorter problem), or Bilal's final Agra is useless (cross that out instead). We try both and keep the better result.

If the last entries had been equal, we could simply pair them up as the final city of the common list and solve the rest.

That one comparison — **"are the last characters equal?"** — drives the whole algorithm. Let's write the cities as letters: Asha = `abcbdab`, Bilal = `bdcaba`.

## 2. Problem statement in our own words

Original: [LeetCode 1143 — Longest Common Subsequence](https://leetcode.com/problems/longest-common-subsequence/).

Given two strings `text1` and `text2` of lowercase letters, return the length of their longest common subsequence: the longest string that can be obtained from **both** by deleting zero or more characters without reordering the rest. Return 0 if they share no character. [AtCoder dp_f](https://atcoder.jp/contests/dp/tasks/dp_f) asks for one such string instead of its length.

## 3. Examples

| text1 | text2 | LCS length | one LCS | why |
|---|---|---|---|---|
| `abcbdab` | `bdcaba` | 4 | `bcba` | also `bdab`, `bcab` — several exist |
| `abcde` | `ace` | 3 | `ace` | all of `ace` appears in order in `abcde` |
| `abc` | `abc` | 3 | `abc` | identical strings |
| `abc` | `def` | 0 | `""` | no common character (edge case) |
| `a` | `a` | 1 | `a` | smallest non-trivial case |

**Running example:** `text1 = "abcbdab"` (n = 7), `text2 = "bdcaba"` (m = 6), answer **4**.

## 4. Deriving the solution — the 7 questions

### 4.1 What is changing?

Two positions move: how much of `text1` we still consider and how much of `text2`. We will look at **prefixes**: the first `i` characters of `text1` and the first `j` characters of `text2`. Each decision shortens one or both prefixes.

### 4.2 What does the state represent?

> `lcs(i, j)` = the length of the longest common subsequence of the prefixes `text1[0..i)` and `text2[0..j)` (the first `i` and first `j` characters).

Answer: `lcs(n, m)`. Using lengths `i, j` (rather than last indices) lets `0` mean "empty prefix", which keeps the base case clean.

### 4.3 What are the choices?

Look at the last characters of the two prefixes, `text1[i-1]` and `text2[j-1]`:

- **Equal** → pair them as the last character of the common subsequence, and solve the prefixes without them.
- **Different** → they cannot *both* end the common subsequence (it has only one last character, and it can't equal two different letters). So one of them is unused:
  - drop `text1[i-1]` → `lcs(i-1, j)`, or
  - drop `text2[j-1]` → `lcs(i, j-1)`.

### 4.4 What is the recurrence?

```
            ┌ 1 + lcs(i-1, j-1)                    if text1[i-1] == text2[j-1]
lcs(i, j) = ┤
            └ max( lcs(i-1, j), lcs(i, j-1) )      otherwise
```

- `1 +` counts the matched character.
- `max` because we want the longest, and we don't know in advance which character to drop.

*Is it safe to always pair equal last characters?* Yes. Take any LCS of the two prefixes. If it does not use both of these last characters, we can change its final character to be matched with them (the final character of the common subsequence can be re-matched to the last occurrences without breaking order), or, if it uses neither, append them — never getting shorter. So pairing them is never worse.

### 4.5 What are the base cases?

- `lcs(0, j) = 0` and `lcs(i, 0) = 0`: an empty prefix has no common subsequence with anything.

In the table this is a row of zeros on top and a column of zeros on the left.

### 4.6 What is the order of computation?

`lcs(i, j)` needs `(i-1, j-1)` (diagonal), `(i-1, j)` (up) and `(i, j-1)` (left):

```
          j-1      j
  i-1  [ diag ] [  up  ]
  i    [ left ] [ (i,j)]
```

So fill rows `i = 1..n` top to bottom, and within a row, `j = 1..m` left to right.

### 4.7 Can the state space be optimized?

Each cell reads only the current row and the row above → two rows, or even **one row plus one saved variable** for the diagonal (Section 8). Memory O(min(n, m)). Reconstruction of the string, however, needs the full table.

## 5. Brute force: recursion

```java
private static int lcs(String text1, String text2, int i, int j) {
    if (i == 0 || j == 0) return 0;
    if (text1.charAt(i - 1) == text2.charAt(j - 1)) {
        return 1 + lcs(text1, text2, i - 1, j - 1);
    }
    return Math.max(lcs(text1, text2, i - 1, j), lcs(text1, text2, i, j - 1));
}
// answer: lcs(text1, text2, n, m)
```

Full file: [solution/BruteForce.java](solution/BruteForce.java).

### The top of the recursion tree (running example)

`text1 = a b c b d a b`, `text2 = b d c a b a`. Each node is `lcs(i, j)` with the two last characters shown.

```
lcs(7,6)  'b' vs 'a'  mismatch
├─ lcs(6,6)  'a' vs 'a'  MATCH → 1 + lcs(5,5)
│            lcs(5,5)  'd' vs 'b'  mismatch
│            ├─ lcs(4,5)  'b' vs 'b'  MATCH → 1 + lcs(3,4) …
│            └─ lcs(5,4)  'd' vs 'a'  mismatch
│                         ├─ lcs(4,4) …
│                         └─ lcs(5,3)  ◄─────────────┐
└─ lcs(7,5)  'b' vs 'b'  MATCH → 1 + lcs(6,4)         │ same state,
             lcs(6,4)  'a' vs 'a'  MATCH → 1 + lcs(5,3) ┘ solved twice
```

`lcs(5,3)` is reached by two completely different routes, and below it the duplication multiplies.

### Why it is slow

On every mismatch the call splits into two, and each branch shrinks only **one** of the two strings by one character. A path can be up to `n + m` steps long, so the worst case is about 2ⁿ⁺ᵐ calls. Two strings of length 30 with no common character already need over 10¹⁷ calls.

## 6. Memoization (top-down)

There are only `(n+1) × (m+1)` different `(i, j)` pairs (8 × 7 = 56 for the running example). Cache them:

```java
private static int lcs(String text1, String text2, int i, int j) {
    if (i == 0 || j == 0) return 0;
    if (memo[i][j] != -1) return memo[i][j];              // computed before: reuse
    if (text1.charAt(i - 1) == text2.charAt(j - 1)) memo[i][j] = 1 + lcs(text1, text2, i - 1, j - 1);
    else memo[i][j] = Math.max(lcs(text1, text2, i - 1, j), lcs(text1, text2, i, j - 1));
    return memo[i][j];
}
```

Full file: [solution/Memoization.java](solution/Memoization.java).

**Cache hit in the running example.** The left branch of the tree reaches `lcs(5,3)` first (inside `lcs(5,4)`) and stores its value, `lcs("abcbd", "bdc") = 2` (`"bc"` or `"bd"`). When the right branch arrives at `lcs(6,4) → 1 + lcs(5,3)`, it reads that 2 immediately: `lcs(6,4) = 3`, `lcs(7,5) = 4`. The whole top call then returns `max(lcs(6,6), lcs(7,5)) = max(4, 4) = 4`.

## 7. Tabulation (bottom-up)

```java
int[][] dp = new int[n + 1][m + 1];          // row 0 and column 0 stay 0
for (int i = 1; i <= n; i++) {
    for (int j = 1; j <= m; j++) {
        if (text1.charAt(i - 1) == text2.charAt(j - 1)) dp[i][j] = dp[i - 1][j - 1] + 1;
        else dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
    }
}
return dp[n][m];
```

Full file: [solution/Tabulation.java](solution/Tabulation.java).

### Filling the table row by row

Columns are `text2 = b d c a b a`, rows are `text1 = a b c b d a b`. Row 0 / column 0 are the empty prefixes.

**Row 1 (`a`).** Only `text2`'s `a`s match: at `j = 4` (`dp[0][3] + 1 = 1`) and `j = 6` (`dp[0][5] + 1 = 1`). Mismatches copy the max of up/left.

```
        ""  b  d  c  a  b  a
  ""     0  0  0  0  0  0  0
  a      0  0  0  0  1  1  1
```

**Row 2 (`b`).** `j=1` match `b`: diag 0 + 1 = 1. `j=2,3,4`: max(up, left) = 1. `j=5` match `b`: diag `dp[1][4] = 1` + 1 = **2** (`"ab"`). `j=6`: max(1, 2) = 2.

```
  b      0  1  1  1  1  2  2
```

**Row 3 (`c`).** `j=3` match `c`: diag `dp[2][2] = 1` + 1 = **2** (`"bc"`). The rest: max(up, left).

```
  c      0  1  1  2  2  2  2
```

**Row 4 (`b`).** `j=1` match: diag 0 + 1 = 1. `j=5` match: diag `dp[3][4] = 2` + 1 = **3** (`"bcb"`).

```
  b      0  1  1  2  2  3  3
```

**Row 5 (`d`).** `j=2` match `d`: diag `dp[4][1] = 1` + 1 = **2** (`"bd"`). Others: max(up, left).

```
  d      0  1  2  2  2  3  3
```

**Row 6 (`a`).** `j=4` match: diag `dp[5][3] = 2` + 1 = 3. `j=6` match: diag `dp[5][5] = 3` + 1 = **4** (`"bcba"`).

```
  a      0  1  2  2  3  3  4
```

**Row 7 (`b`).** `j=1` match: 1. `j=5` match: diag `dp[6][4] = 3` + 1 = **4** (`"bdab"` or `"bcab"`). `j=6`: max(up 4, left 4) = 4.

```
  b      0  1  2  2  3  4  4
```

Complete table:

```
        ""  b  d  c  a  b  a
  ""     0  0  0  0  0  0  0
  a      0  0  0  0  1  1  1
  b      0  1  1  1  1  2  2
  c      0  1  1  2  2  2  2
  b      0  1  1  2  2  3  3
  d      0  1  2  2  2  3  3
  a      0  1  2  2  3  3  4
  b      0  1  2  2  3  4  4   ← dp[7][6] = 4
```

### Reconstructing the LCS string

Start at `(n, m)` and repeat until a prefix is empty:

- if the characters match → this character is in the LCS; record it, move **diagonally**;
- else move to the neighbour (up or left) holding the **larger** value — that is where the number came from; on a tie choose up (any choice gives *some* LCS).

The characters come out last-to-first, so reverse at the end.

```
(7,6) 'b' vs 'a' differ; up (6,6)=4, left (7,5)=4 → tie → up
(6,6) 'a' = 'a'  → take 'a', go to (5,5)
(5,5) 'd' vs 'b' differ; up (4,5)=3 > left (5,4)=2 → up
(4,5) 'b' = 'b'  → take 'b', go to (3,4)
(3,4) 'c' vs 'a' differ; up (2,4)=1 < left (3,3)=2 → left
(3,3) 'c' = 'c'  → take 'c', go to (2,2)
(2,2) 'b' vs 'd' differ; up (1,2)=0 < left (2,1)=1 → left
(2,1) 'b' = 'b'  → take 'b', go to (1,0) → stop
collected: a, b, c, b  → reversed: "bcba"
```

The path on the table (`*` = matched character taken):

```
        ""  b  d  c  a  b  a
  ""     0  0  0  0  0  0  0
  a      0  0  0  0  1  1  1
  b      0 *1← 1  1  1  2  2
  c      0  1  1 *2← 2  2  2
  b      0  1  1  2  2 *3  3
  d      0  1  2  2  2  3↑ 3
  a      0  1  2  2  3  3 *4
  b      0  1  2  2  3  4  4↑
```

This walk is what [AtCoder dp_f](https://atcoder.jp/contests/dp/tasks/dp_f) asks for. It takes at most `n + m` steps.

## 8. Space optimization

To compute the length only, row `i` needs row `i-1` and itself. The classic trick uses **one array** `row[j]`:

- Before we overwrite `row[j]`, it still holds `dp[i-1][j]` (**up**).
- `row[j-1]` was overwritten one step earlier, so it already holds `dp[i][j-1]` (**left**).
- The **diagonal** `dp[i-1][j-1]` was the *old* `row[j-1]` — destroyed one step ago. So we save it in a variable before overwriting.

```java
int[] row = new int[m + 1];
for (int i = 1; i <= n; i++) {
    int diagonal = 0;                       // dp[i-1][0] is always 0
    for (int j = 1; j <= m; j++) {
        int above = row[j];                 // dp[i-1][j], about to be overwritten
        if (text1.charAt(i - 1) == text2.charAt(j - 1)) row[j] = diagonal + 1;
        else row[j] = Math.max(above, row[j - 1]);
        diagonal = above;                   // next column's diagonal
    }
}
return row[m];
```

Full file: [solution/Optimized.java](solution/Optimized.java). It also swaps the strings so the array runs over the shorter one.

Update of row 2 (`b`), starting from row 1 = `[0, 0, 0, 0, 1, 1, 1]`:

| j | text2[j-1] | above (old row[j]) | left (row[j-1]) | diagonal | match? | new row[j] |
|---|---|---|---|---|---|---|
| 1 | b | 0 | 0 | 0 | yes | 1 |
| 2 | d | 0 | 1 | 0 | no | 1 |
| 3 | c | 0 | 1 | 0 | no | 1 |
| 4 | a | 1 | 1 | 0 | no | 1 |
| 5 | b | 1 | 1 | 1 | yes | 2 |
| 6 | a | 1 | 2 | 1 | no | 2 |

Result `[0, 1, 1, 1, 1, 2, 2]` — exactly row 2 of the full table.

## 9. Dry run on a full example

The value of the final cell in each row tells you the LCS of a growing prefix of `text1` with all of `text2 = "bdcaba"`:

| i | prefix of text1 | dp[i][6] | one LCS with "bdcaba" |
|---|---|---|---|
| 1 | a | 1 | a |
| 2 | ab | 2 | ab |
| 3 | abc | 2 | ab (or bc) |
| 4 | abcb | 3 | bcb |
| 5 | abcbd | 3 | bcb |
| 6 | abcbda | 4 | bcba |
| 7 | abcbdab | 4 | bcba |

All four implementations agree on the tests in their `main` methods: 4, 3, 3, 0, 1.

## 10. Complexity analysis

| Approach | Time | Space | Why |
|---|---|---|---|
| Brute force | O(2ⁿ⁺ᵐ) | O(n + m) | Two branches per mismatch; depth up to n + m. |
| Memoization | O(n · m) | O(n · m) | (n+1)(m+1) states × O(1) work; plus O(n + m) stack. |
| Tabulation | O(n · m) | O(n · m) | One O(1) cell per pair; reconstruction adds O(n + m) time. |
| 1D optimized | O(n · m) | O(min(n, m)) | Same cells; one array over the shorter string. |

With LeetCode's limit of 1000 characters each, that is 10⁶ cells — fast. AtCoder dp_f allows 3000 × 3000 = 9·10⁶ cells, still fine, but keep the full table because you must print the string.

## 11. Why this approach works

Every common subsequence of `text1[0..i)` and `text2[0..j)` falls into exactly one of these cases:

1. It does not use `text1[i-1]` → it is a common subsequence of `text1[0..i-1)` and `text2[0..j)`, covered by `lcs(i-1, j)`.
2. It does not use `text2[j-1]` → covered by `lcs(i, j-1)`.
3. It uses both → then they are matched to each other as its last character (they are the last characters of both prefixes), so they must be equal, and the rest is a common subsequence of the shorter prefixes: `1 + lcs(i-1, j-1)`.

The recurrence considers all applicable cases and takes the best, so it is exact. (When the characters match we skip cases 1 and 2 because, as argued in 4.4, case 3 is never worse.)

## 12. Common mistakes

1. **Off-by-one between lengths and indices.** `dp[i][j]` talks about prefixes of length `i` and `j`, so compare `text1.charAt(i-1)` with `text2.charAt(j-1)`.
2. **On a match, using `max(up, left) + 1`.** That can count one character twice. The match must come from the diagonal.
3. **Confusing subsequence with substring.** For the longest common **substring** ([LC 718](https://leetcode.com/problems/maximum-length-of-repeated-subarray/)) a mismatch resets the cell to 0 and the answer is the maximum cell, not the corner.
4. **Forgetting to save the diagonal in the 1D version.** Reading `row[j-1]` gives the *new* value (left), not the old one (diagonal).
5. **Reconstructing from the 1D array.** It is impossible; you need the full table (or Hirschberg's divide-and-conquer, beyond this course).
6. **Expecting a unique LCS.** Tie-breaking (up vs. left) changes which LCS you print; graders such as AtCoder accept any.

## 13. When to use this technique / how to recognize similar problems

Signals:

- **Two sequences** (strings or arrays) and a question about what they **share in order**, or how to **transform/merge** one into the other.
- Elements can be skipped (subsequence), and order matters.

Then define `f(i, j)` on prefixes and compare the last elements. Variants:

- Delete characters from both to make them equal → `n + m − 2·LCS` ([LC 583](https://leetcode.com/problems/delete-operation-for-two-strings/)).
- Same, but each deletion costs the character's ASCII value → weighted LCS ([LC 712](https://leetcode.com/problems/minimum-ascii-delete-sum-for-two-strings/)).
- Arrays instead of strings → [Uncrossed Lines](https://leetcode.com/problems/uncrossed-lines/).
- One string against its reverse → [Longest Palindromic Subsequence](../13-longest-palindromic-subsequence/).
- Allow replacements/insertions with cost → [Edit Distance](../14-edit-distance/).
- Count occurrences instead of maximizing → [Distinct Subsequences](../15-distinct-subsequences/).
- Build the shortest string containing both → [Shortest Common Supersequence](../16-shortest-common-supersequence/).

## 14. Summary and revision notes

- State on prefixes: `dp[i][j]` = LCS of the first `i` chars of `text1` and first `j` chars of `text2`.
- Match → diagonal + 1. Mismatch → max(up, left).
- Row 0 and column 0 are 0 (empty prefix).
- Fill top-to-bottom, left-to-right. Answer at `dp[n][m]`.
- Backtrack from `(n, m)` to print one LCS; reverse at the end.
- 1D: one array + a saved `diagonal`.

```
┌──────────────────────────────────────────────────────────────┐
│ dp[i][j] = dp[i-1][j-1] + 1                if a[i-1] == b[j-1]│
│          = max(dp[i-1][j], dp[i][j-1])     otherwise          │
│ dp[0][*] = dp[*][0] = 0           answer dp[n][m]             │
└──────────────────────────────────────────────────────────────┘
```

## 15. Exercises

**Exercise 1.** Fill the LCS table for `text1 = "abcde"`, `text2 = "ace"` and backtrack to get the LCS string.

<details>
<summary>Answer</summary>

```
        ""  a  c  e
  ""     0  0  0  0
  a      0  1  1  1
  b      0  1  1  1
  c      0  1  2  2
  d      0  1  2  2
  e      0  1  2  3
```
Backtrack: (5,3) e=e take → (4,2) d≠c, up (3,2)=2 ≥ left (4,1)=1 → up → (3,2) c=c take → (2,1) b≠a, up (1,1)=1 ≥ left (2,0)=0 → up → (1,1) a=a take. Reversed: `"ace"`.
</details>

**Exercise 2.** Use the LCS to answer [Delete Operation for Two Strings](https://leetcode.com/problems/delete-operation-for-two-strings/) for `"sea"` and `"eat"`: the fewest characters to delete (from either string) to make them equal.

<details>
<summary>Answer</summary>

LCS(`sea`, `eat`) = 2 (`"ea"`). Everything outside the LCS must be deleted: `(3 − 2) + (3 − 2) = 2` (delete `s` from "sea" and `t` from "eat").
</details>

**Exercise 3.** Change the recurrence to compute the longest common **substring** of `"abcbdab"` and `"bdcaba"`. What is the answer?

<details>
<summary>Answer</summary>

`sub[i][j] = sub[i-1][j-1] + 1` if the characters match, else **0**; the answer is the maximum over all cells. The common substrings of length 2 are `"bd"`, `"ab"` and `"ba"` (e.g. `"ab"` appears in both), and no length-3 substring is shared, so the answer is 2.
</details>

**Exercise 4.** In the 1D version, what goes wrong if you write `row[j] = row[j-1] + 1` on a match instead of `diagonal + 1`? Test it on `text1 = "a"`, `text2 = "aa"`.

<details>
<summary>Answer</summary>

`row[j-1]` is the **new** left cell `dp[i][j-1]`, not the diagonal `dp[i-1][j-1]`. Row 1 (`a`): `j=1` match → `row[1] = row[0] + 1 = 1`; `j=2` match → `row[2] = row[1] + 1 = 2`. The true LCS is 1: the single `a` of `text1` has been matched twice. With the saved diagonal, `j=2` uses `dp[0][1] = 0` and correctly gives 1.
</details>
