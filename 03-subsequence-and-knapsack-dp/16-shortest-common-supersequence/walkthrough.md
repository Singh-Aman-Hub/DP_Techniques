# Shortest Common Supersequence — Walkthrough

## 1. Intuition

Two friends each wrote a to-do list for Saturday, in the order they want to do things:

```
Asha:  a  b  a  c        (a = groceries, b = bank, c = cinema)
Bilal: c  a  b
```

They want **one** shared schedule that respects both orders — every item of Asha's list appears in her order, every item of Bilal's appears in his — and it should be as short as possible, because doing the same errand twice wastes time.

The lazy schedule just glues the lists: `a b a c c a b` (7 items). But some errands can be done **once, for both of them**: if Asha's `a, b` and Bilal's `a, b` happen at the same moments, they share. How many items can be shared at most? The shared items must appear in both lists in the same order — they form a **common subsequence**. So the most we can share is the **LCS**, here `"ab"` (length 2), and the shortest schedule has `4 + 3 − 2 = 5` items:

```
c  a  b  a  c
↑  ↑  ↑  ↑  ↑
B  AB AB A  A        (A = serves Asha, B = serves Bilal, AB = shared)
```

Asha reads `a b a c` from positions 2–5; Bilal reads `c a b` from positions 1–3.

Finding the **length** is easy once you know LCS. Finding the **schedule itself** means walking through the LCS table and deciding, at every step, which character to write.

## 2. Problem statement in our own words

Original: [LeetCode 1092 — Shortest Common Supersequence](https://leetcode.com/problems/shortest-common-supersequence/).

Given two non-empty strings `str1` and `str2` of lowercase letters, return a shortest string that has both `str1` and `str2` as subsequences. If several shortest strings exist, any of them is accepted.

## 3. Examples

| str1 | str2 | LCS | SCS length | one SCS (our code) |
|---|---|---|---|---|
| `abac` | `cab` | `ab` (2) | 4 + 3 − 2 = 5 | `cabac` |
| `aaaaaaaa` | `aaaaaaaa` | 8 | 8 | `aaaaaaaa` |
| `abc` | `def` | 0 | 6 | `defabc` (`abcdef` is equally good) |
| `geek` | `eke` | `ek` (2) | 5 | `gekek` |
| `a` | `b` | 0 | 2 | `ba` (edge case: nothing shared) |

**Running example:** `str1 = "abac"` (n = 4), `str2 = "cab"` (m = 3), answer **`"cabac"`** (length 5).

## 4. Deriving the solution — the 7 questions

We first derive the **length** directly (so we see the DP), then show how the LCS table gives both the length and the string.

### 4.1 What is changing?

As in every two-string DP of this module: how much of each string is still to be covered. Use prefixes `str1[0..i)` and `str2[0..j)`.

### 4.2 What does the state represent?

> `len(i, j)` = the length of a shortest common supersequence of `str1[0..i)` and `str2[0..j)`.

Answer: `len(n, m)`. For construction we will use the LCS table `lcs[i][j]` from [topic 12](../12-longest-common-subsequence/), because `len(i, j) = i + j − lcs[i][j]`.

### 4.3 What are the choices?

Think about the **last character** of the supersequence of the two prefixes:

- If `str1[i-1] == str2[j-1]`, write that character **once** — it serves both strings — and cover the rest: `(i-1, j-1)`.
- Otherwise the last character is either `str1[i-1]` (then `str2[0..j)` must still be fully covered by the part before it: `(i-1, j)`) or `str2[j-1]` (`(i, j-1)`). Pick the shorter.

### 4.4 What is the recurrence?

```
              ┌ 1 + len(i-1, j-1)                      if str1[i-1] == str2[j-1]
len(i, j)  =  ┤
              └ 1 + min( len(i-1, j), len(i, j-1) )    otherwise
```

`1 +` because we write one character either way; `min` because we want the shortest.

Compare with LCS: `lcs = max(...)` on a mismatch and `+1` only on a match. In fact `len(i, j) = i + j − lcs(i, j)` holds cell by cell — try plugging it in: on a match `i + j − (lcs(i-1,j-1) + 1) = 1 + ((i-1) + (j-1) − lcs(i-1,j-1))`. ✔

### 4.5 What are the base cases?

- `len(0, j) = j` — only `str2`'s prefix is left, the shortest cover is the prefix itself.
- `len(i, 0) = i` — likewise for `str1`.

These are exactly the **leftover** steps of the construction (Section 7).

### 4.6 What is the order of computation?

Diagonal, up and left neighbours → rows top to bottom, left to right — the LCS order.

### 4.7 Can the state space be optimized?

For the **length**, one row is enough (`n + m − LCS`, with the 1D LCS). For the **string**, we need the full table to walk back: O(n·m) memory. (Linear-memory reconstruction exists — Hirschberg's divide-and-conquer — but is beyond this course.)

### 4.8 Why |SCS| = n + m − LCS, in both directions

- **Achievable (≤).** Take an LCS of length `L`. Write the two strings "zipped" along it: between consecutive LCS characters, write the unshared characters of `str1`, then those of `str2`; write each LCS character once. Total `(n − L) + (m − L) + L = n + m − L`.
- **Optimal (≥).** In any common supersequence `S`, mark which characters `str1` uses and which `str2` uses. Characters used by **both** appear in both strings in the same order: they form a common subsequence, so there are at most `L` of them. Every character of `str1` and `str2` uses some position of `S`, so `|S| ≥ n + m − (shared positions) ≥ n + m − L`.

## 5. Brute force: recursion

Directly build the string with the recurrence (keeping the first option on ties):

```java
private static String scs(String str1, String str2, int i, int j) {
    if (i == 0) return str2.substring(0, j);
    if (j == 0) return str1.substring(0, i);
    if (str1.charAt(i - 1) == str2.charAt(j - 1)) {
        return scs(str1, str2, i - 1, j - 1) + str1.charAt(i - 1);
    }
    String endWithStr1 = scs(str1, str2, i - 1, j) + str1.charAt(i - 1);
    String endWithStr2 = scs(str1, str2, i, j - 1) + str2.charAt(j - 1);
    return (endWithStr1.length() <= endWithStr2.length()) ? endWithStr1 : endWithStr2;
}
```

Full file: [solution/BruteForce.java](solution/BruteForce.java).

### The top of the recursion tree (running example)

```
scs(4,3)  "abac" | "cab"   'c' vs 'b' differ
├─ end with 'c': scs(3,3) "aba" | "cab"   'a' vs 'b' differ
│     ├─ end with 'a': scs(2,3) "ab" | "cab"  'b' = 'b' → scs(1,2) + 'b'
│     │                scs(1,2) "a" | "ca"  'a' = 'a' → scs(0,1) + 'a' = "c" + "a"
│     │                ⇒ "cab"  ⇒ "caba"
│     └─ end with 'b': scs(3,2) "aba" | "ca" …  ⇒ longer
│     ⇒ "caba"  ⇒ "cabac"  (length 5)
└─ end with 'b': scs(4,2) "abac" | "ca" …   (length 5) ⇒ + 'b' = length 6, longer
⇒ "cabac"
```

### Why it is slow

Two branches per mismatch and the same `(i, j)` reached along many paths (`scs(3,2)` appears under both top-level branches), so about 2ⁿ⁺ᵐ calls — and each call also builds strings of length up to `n + m`. Fine for 4 and 3 characters, hopeless for 1000.

## 6. Memoization (top-down)

Memoizing **strings** would work but wastes memory and time (each cell stores a string). The standard approach memoizes the **length** and reconstructs afterwards:

```java
private static int len(String str1, String str2, int i, int j) {
    if (i == 0) return j;
    if (j == 0) return i;
    if (memo[i][j] != -1) return memo[i][j];
    if (str1.charAt(i - 1) == str2.charAt(j - 1)) memo[i][j] = 1 + len(str1, str2, i - 1, j - 1);
    else memo[i][j] = 1 + Math.min(len(str1, str2, i - 1, j), len(str1, str2, i, j - 1));
    return memo[i][j];
}
```

Full file: [solution/Memoization.java](solution/Memoization.java).

The memo table for the running example (row 0 / column 0 are base cases):

```
          ""   c   a   b
   ""      0   1   2   3
   a       1   2   2   3
   b       2   3   3   3
   a       3   4   4   4
   c       4   4   5   5    ← len(4,3) = 5
```

Every cell equals `i + j − lcs[i][j]` (compare with the LCS table below). For example `len(3,2) = 4` is computed once inside the first branch and reused by the second.

## 7. Tabulation (bottom-up) — and building the string

### 7.1 The LCS table

Fill the ordinary [LCS](../12-longest-common-subsequence/) table:

```
          ""   c   a   b
   ""      0   0   0   0
   a       0   0   1   1
   b       0   0   1   2
   a       0   0   1   2
   c       0   1   1   2    ← LCS = 2, so |SCS| = 4 + 3 − 2 = 5
```

### 7.2 The walk back

Start at `(i, j) = (n, m)` and build the answer **backwards** (last character first):

| situation at `(i, j)` | write | move | reason |
|---|---|---|---|
| `str1[i-1] == str2[j-1]` | that character, **once** | ↖ `(i-1, j-1)` | shared LCS character |
| differ, `lcs[i-1][j] ≥ lcs[i][j-1]` | `str1[i-1]` | ↑ `(i-1, j)` | the LCS of this cell doesn't need `str1[i-1]`; it must still appear, so write it on its own |
| differ, otherwise | `str2[j-1]` | ← `(i, j-1)` | symmetric |
| `i == 0` (or `j == 0`) | the rest of the other string | stop | only one string left to cover |

Following the **larger LCS neighbour** keeps us on a path that shares as many characters as possible, which is what makes the result shortest.

```java
while (i > 0 && j > 0) {
    if (str1.charAt(i - 1) == str2.charAt(j - 1)) { out.append(str1.charAt(i - 1)); i--; j--; }
    else if (lcs[i - 1][j] >= lcs[i][j - 1])     { out.append(str1.charAt(i - 1)); i--; }
    else                                          { out.append(str2.charAt(j - 1)); j--; }
}
while (i > 0) { out.append(str1.charAt(i - 1)); i--; }   // leftover of str1
while (j > 0) { out.append(str2.charAt(j - 1)); j--; }   // leftover of str2
return out.reverse().toString();
```

Full file: [solution/Tabulation.java](solution/Tabulation.java).

### 7.3 The walk on the running example

```
(4,3) 'c' vs 'b' differ; up lcs(3,3)=2 ≥ left lcs(4,2)=1 → write 'c' (from str1), i=3
(3,3) 'a' vs 'b' differ; up lcs(2,3)=2 ≥ left lcs(3,2)=1 → write 'a' (from str1), i=2
(2,3) 'b' = 'b'                                          → write 'b' (shared),    (1,2)
(1,2) 'a' = 'a'                                          → write 'a' (shared),    (0,1)
i = 0: leftover of str2 = "c"                            → write 'c'
written (backwards): c, a, b, a, c   → reversed: "cabac"
```

Path on the table (`*` shared, `↑` wrote a `str1` char, `◄` leftover):

```
          ""   c   a   b
   ""      0  ◄0   0   0
   a       0   0  *1   1
   b       0   0   1  *2
   a       0   0   1   2↑
   c       0   1   1   2↑
```

Check: `"cabac"` has length 5 = `4 + 3 − 2`; deleting the first `c` gives `"abac"`; deleting the last `a` and `c` gives `"cab"`. ✔ The tests in `Tabulation.java` check this exact string **and** that both inputs are subsequences of it.

### 7.4 Why the leftovers are necessary

The loop stops as soon as **one** string is exhausted, but the other may still have characters. In the example, `str2`'s first character `c` was never written inside the loop. Forgetting the leftover loops is the most common bug in this problem (you'd output `"abac"`, which doesn't contain `"cab"`).

## 8. Space optimization

- **Length only:** `n + m − LCS`, and the LCS needs one row plus a `diagonal` variable — O(min(n, m)) memory. See [solution/Optimized.java](solution/Optimized.java).
- **The string:** the walk needs to look at `lcs[i-1][j]` and `lcs[i][j-1]` for arbitrary `(i, j)` on the path, so the whole table is kept. With LeetCode's limit of 1000 characters that is 10⁶ `int`s (4 MB), perfectly fine.

## 9. Dry run on a full example

`str1 = "geek"`, `str2 = "eke"`.

LCS table:

```
          ""   e   k   e
   ""      0   0   0   0
   g       0   0   0   0
   e       0   1   1   1
   e       0   1   1   2
   k       0   1   2   2    ← LCS = 2 → |SCS| = 4 + 3 − 2 = 5
```

Walk:

| at | chars | decision | write | next |
|---|---|---|---|---|
| (4,3) | k vs e | differ; up 2 ≥ left 2 | k | (3,3) |
| (3,3) | e = e | shared | e | (2,2) |
| (2,2) | e vs k | differ; up 0 < left 1 | k (from str2) | (2,1) |
| (2,1) | e = e | shared | e | (1,0) |
| (1,0) | — | leftover str1 | g | done |

Written backwards: `k, e, k, e, g` → **`"gekek"`**. Check: `g e e k` ⊂ `g e k e k` (positions 0, 1, 3, 4) and `e k e` ⊂ `g e k e k` (positions 1, 2, 3). ✔

## 10. Complexity analysis

| Approach | Time | Space | Why |
|---|---|---|---|
| Brute force (strings) | O(2ⁿ⁺ᵐ · (n+m)) | O((n+m)²) | Two branches per mismatch; each call builds strings up to `n+m` long. |
| Memoized length | O(n · m) | O(n · m) | (n+1)(m+1) states × O(1). |
| LCS table + walk | O(n · m) | O(n · m) | Table fill dominates; the walk is O(n + m) steps, plus O(n + m) for the output. |
| Length only, 1 row | O(n · m) | O(min(n, m)) | Rolling LCS row. |

## 11. Why this approach works

During the walk, the invariant is: *the characters still to be written form a shortest common supersequence of `str1[0..i)` and `str2[0..j)`, and its length is `i + j − lcs[i][j]`.*

- On a match we write one character and move to `(i-1, j-1)`: the remaining length drops by exactly 1, because `lcs[i][j] = lcs[i-1][j-1] + 1`.
- On a mismatch with `lcs[i-1][j] ≥ lcs[i][j-1]`, we know `lcs[i][j] = lcs[i-1][j]`, so moving up keeps `lcs` the same while `i + j` drops by 1: again exactly one character for one step. Same for the left move.
- The leftovers cost exactly `i` (or `j`) characters, matching `len(i, 0) = i`.

Every step writes one character and lowers the target length by one, so the final string has length exactly `n + m − LCS`, which Section 4.8 showed is optimal. Each written character comes from `str1`, `str2` or both, in the order of the strings, so both are subsequences of the result.

## 12. Common mistakes

1. **Forgetting the leftover loops** after the main loop.
2. **Moving toward the smaller LCS neighbour.** The result is still a common supersequence, but it can be longer than necessary.
3. **Writing a matched character twice.** A shared character is written once and both indices move.
4. **Forgetting to reverse.** The walk produces the string from the end.
5. **Expecting a unique answer.** `"abc"` / `"def"` has `"defabc"`, `"abcdef"`, `"adbecf"`, … — all of length 6. LeetCode accepts any; your own tests should check length and the subsequence property (as ours do), not just one string.
6. **Confusing supersequence with superstring.** Requiring the inputs as **contiguous** substrings is [a different problem](https://leetcode.com/problems/find-the-shortest-superstring/).

## 13. When to use this technique / how to recognize similar problems

- "Shortest string/sequence containing both", "merge two sequences preserving order with minimum length" → SCS via LCS.
- "Print the actual answer" for any two-string DP → keep the full table and walk back from `(n, m)`, asking at every cell *"which neighbour produced this value, and what does that move mean?"* This is the same skill as printing the [LCS](../12-longest-common-subsequence/) and listing [edit operations](../14-edit-distance/).
- The formulas to remember: `SCS = n + m − LCS`, `minimum deletions to make equal = n + m − 2·LCS`, `LPS = LCS(s, reverse(s))`.

## 14. Summary and revision notes

- `|SCS| = n + m − LCS`: shared characters form a common subsequence; at most LCS of them.
- Direct recurrence: match → `1 + len(i-1, j-1)`; mismatch → `1 + min(len(i-1, j), len(i, j-1))`; bases `len(0, j) = j`, `len(i, 0) = i`.
- Construction: walk the LCS table from `(n, m)`; match → write once ↖; else write the char of the side you leave, moving toward the larger LCS neighbour; then copy leftovers; reverse.
- Keep the full table for the string; one row suffices for the length.

```
┌──────────────────────────────────────────────────────────────────┐
│ len(i,j) = 1 + len(i-1,j-1)                 if a[i-1] == b[j-1]   │
│          = 1 + min(len(i-1,j), len(i,j-1))  otherwise             │
│ len(i,0) = i,  len(0,j) = j                                      │
│ |SCS| = n + m − LCS(a, b)                                        │
└──────────────────────────────────────────────────────────────────┘
```

## 15. Exercises

**Exercise 1.** What is the length of the SCS of `"AGGTAB"` and `"GXTXAYB"`? Give one SCS.

<details>
<summary>Answer</summary>

LCS = `"GTAB"` (4), so the length is `6 + 7 − 4 = 9`. One SCS: `"AGXGTXAYB"` — `A G G T A B` appears at positions 0, 1, 3, 4, 6, 8 and `G X T X A Y B` at positions 1, 2, 4, 5, 6, 7, 8.
</details>

**Exercise 2.** Run the walk on `str1 = "abc"`, `str2 = "def"` but break ties toward the **left** (`>` instead of `>=`). What string do you get?

<details>
<summary>Answer</summary>

The LCS table is all zeros, so every comparison is a tie. With "ties → left" the walk writes `f, e, d` (from `str2`) and then the leftover `c, b, a` from `str1`: backwards `f e d c b a` → **`"abcdef"`**. With "ties → up" (our code) you get `"defabc"`. Both are valid shortest answers.
</details>

**Exercise 3.** A friend's code prints `"abac"` for `str1 = "abac"`, `str2 = "cab"`. What did they forget, and how can a self-check catch this?

<details>
<summary>Answer</summary>

They forgot the leftover loop for `str2`: the walk ends at `(0, 1)` with the `c` of `"cab"` not yet written. A self-check that verifies **both** inputs are subsequences of the output catches it immediately: `"cab"` is not a subsequence of `"abac"`. The length check also fails (4 instead of 5).
</details>
