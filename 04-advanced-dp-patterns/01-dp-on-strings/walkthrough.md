# DP on Strings — Walkthrough

This lesson has two parts.

- **Part A (sections 1–3):** how to recognise the two shapes of string DP you already know.
- **Part B (sections 4–14):** using the two-prefix shape on Wildcard Matching (LC 44) and Regular Expression Matching (LC 10). The whole difficulty of these two problems is the symbol `*`, so we spend a lot of time on tables that show what it does.

Practice problems LC 97 (Interleaving String) and LC 5 / LC 647 (palindromic substrings) are worked out in section 13.

---

## 1. Intuition

### 1.1 Two shapes that both look like `dp[i][j]`

Think of two ways to compare text.

- **Two readers, two bookmarks.** You and a friend each hold a different book. You each keep a bookmark (`i` in your book, `j` in theirs). At every step you decide who moves their bookmark forward. The question is always "given what we have read so far (the prefixes), what is the answer?" This is **two-prefix DP**. LCS, edit distance, distinct subsequences and shortest common supersequence all work this way.

- **One reader, two fingers.** You hold one book and put a finger on each end of a passage, `i` on the left and `j` on the right. You squeeze the passage from the outside in. The question is "what is the answer for the passage `s[i..j]`?" This is **substring DP** (also called **interval DP**). Longest palindromic subsequence works this way.

```
Two-prefix DP (two strings)            Substring DP (one string)

  A = a b c d e                          s = a b c b a
      ^^^^^ first i chars                      ^     ^
  B = a c e                                    i     j
      ^^^ first j chars                  dp[i][j] = answer for s[i..j]
  dp[i][j] = answer for A[0..i-1], B[0..j-1]
```

| Question to ask | Two-prefix DP | Substring DP |
|---|---|---|
| How many input strings? | Usually two (text and pattern, word1 and word2) | One |
| What do `i` and `j` point into? | Different strings | The same string |
| What does a cell describe? | Two prefixes | One contiguous piece |
| Typical moves | Advance `i`, advance `j`, or both | Shrink from the left, from the right, or both ends |
| Base cases | Row 0 and column 0 (an empty prefix) | Pieces of length 0 or 1 (the diagonal) |
| Fill order | Row by row, left to right | By increasing length (or `i` from right to left) |
| Answer cell | `dp[n][m]` (bottom-right corner) | `dp[0][n-1]` (top-right corner) |

A quick recognition test: **if the problem gives you two strings and you consume both from the front, use two prefixes. If it gives you one string and the answer for a piece depends on its two ends, use substrings.**

### 1.2 Pattern matching as a walk

In pattern matching you hold a text and a pattern. Each pattern symbol tells you how many text characters it may eat:

- a normal letter eats exactly that one letter;
- `?` (or `.` in regex) eats exactly one character, any character;
- `*` eats a **variable** amount — and that is where DP is needed, because we do not know in advance how much it should eat.

Think of `*` as a hungry person at a buffet who does not know when to stop. DP lets us try "stop now" and "eat one more" and remember which attempts already failed.

---

## 2. Problem statement in our own words

**Wildcard Matching ([LC 44](https://leetcode.com/problems/wildcard-matching/)).** You get a text `s` of lowercase letters and a pattern `p` of lowercase letters, `?` and `*`. `?` matches any one character. `*` matches any sequence of characters, including none. Return whether the pattern matches the **entire** text.

**Regular Expression Matching ([LC 10](https://leetcode.com/problems/regular-expression-matching/)).** Same idea, but the pattern uses `.` (any one character) and `*`, where `*` means "zero or more copies of the element right before it". Each `*` is guaranteed to have a valid character before it. Again the whole text must be matched.

### The two stars side by side

| Pattern | Wildcard (LC 44) meaning | Regex (LC 10) meaning |
|---|---|---|
| `*` | any string: `""`, `"a"`, `"xyz"`… | invalid on its own (always follows a character) |
| `a*` | `a` followed by anything: `"a"`, `"ab"`, `"azzz"` | zero or more `a`: `""`, `"a"`, `"aaa"` |
| `.*` | `.` is a literal letter here | any string (zero or more of "any character") |
| `*a` | anything ending in `a` | invalid |

The single most common mistake is to bring one meaning into the other problem. Remember: **in wildcards the star is a standalone token; in regex the star belongs to the character before it.**

---

## 3. Examples

Wildcard (we use the first one in every section):

| Text `s` | Pattern `p` | Result | Why |
|---|---|---|---|
| `adceb` | `*a*b` | true | first `*` = `""`, `a` = `a`, second `*` = `dce`, `b` = `b` |
| `abcd` | `a*d` | true | `*` = `bc` |
| `aa` | `a` | false | the pattern covers only one `a` |
| `cb` | `?a` | false | `?` = `c`, but `a` ≠ `b` |
| `acdcb` | `a*c?b` | false | no way to make the last three pattern symbols fit |
| `""` | `*` | true | `*` may match the empty string |
| `""` | `?` | false | `?` needs exactly one character |

Regex (we use the first one in every section):

| Text `s` | Pattern `p` | Result | Why |
|---|---|---|---|
| `aab` | `c*a*b` | true | `c*` = zero c's, `a*` = `aa`, `b` = `b` |
| `aa` | `a` | false | only one `a` in the pattern |
| `aa` | `a*` | true | two copies of `a` |
| `ab` | `.*` | true | `.*` = any string |
| `ab` | `.*c` | false | the text must end in `c` |
| `""` | `a*` | true | zero copies |

---

## 4. Deriving the solution — the 7 questions

### 4A. Wildcard Matching

#### Q1. What is changing?
Two positions: how much of the text we have matched and how much of the pattern we have used. Both only move forward.

#### Q2. What does the state represent?
We use the prefix form for the table (like LCS):

> `dp[i][j]` = **true** if the first `i` characters of `s` can be matched by the first `j` characters of `p`.

(The memoized version in section 6 uses the equivalent suffix form `f(i, j)` = "does `s[i..]` match `p[j..]`?". Both are fine; pick one and stay consistent.)

#### Q3. What are the choices?
Look at the last pattern symbol `p[j-1]`:

- a letter: it must equal `s[i-1]`, and then both are consumed;
- `?`: it consumes `s[i-1]`, whatever it is;
- `*`: two choices —
  - **the star matches nothing**: drop the star, keep all `i` text characters;
  - **the star eats `s[i-1]`**: drop that text character but **keep the star**, because it may eat more.

#### Q4. What is the recurrence?

```
if p[j-1] == '*':
    dp[i][j] = dp[i][j-1]          // star matches ""          (look LEFT)
            || dp[i-1][j]          // star eats s[i-1], stays   (look UP)
else if p[j-1] == '?' or p[j-1] == s[i-1]:
    dp[i][j] = dp[i-1][j-1]        // one-for-one               (look DIAGONAL)
else:
    dp[i][j] = false
```

Why is "the star eats one character" `dp[i-1][j]` and not `dp[i-1][j-1]`? Because after eating `s[i-1]` the star is still available to eat `s[i-2]`, `s[i-3]`, … Staying in column `j` lets one star eat a run of any length, one character per row. Using `dp[i-1][j-1]` would let the star eat exactly one character — that is `?`, not `*`.

```
              col j-1     col j  ('*')
row i-1                  [ up ]           star ate s[i-1]; it may have eaten more above
row i      [ left ]  ->  [ ?? ]           star ate nothing
```

#### Q5. What are the base cases?
- `dp[0][0] = true`: the empty pattern matches the empty text.
- `dp[i][0] = false` for `i ≥ 1`: an empty pattern cannot match a non-empty text.
- `dp[0][j]`: an empty text matches a pattern prefix only if **every** symbol in it is `*`. So `dp[0][j] = dp[0][j-1] && p[j-1] == '*'`. The first non-star turns the rest of row 0 false.

#### Q6. What is the order of computation?
`dp[i][j]` reads left (`[i][j-1]`), up (`[i-1][j]`) and diagonal (`[i-1][j-1]`). Filling rows top to bottom and each row left to right makes all three ready. The answer is `dp[n][m]`.

#### Q7. Can the state space be optimized?
Only row `i-1` and the current row are read. Two rows of length `m+1` are enough: O(m) space (section 8).

### 4B. Regular Expression Matching

#### Q1. What is changing?
Again the text position and the pattern position.

#### Q2. What does the state represent?
> `dp[i][j]` = **true** if the first `i` characters of `s` are matched by the first `j` characters of `p`.

#### Q3. What are the choices?
If `p[j-1]` is a letter or `.`, it must match `s[i-1]` one-for-one.

If `p[j-1]` is `*`, look at the **pair** `x* = p[j-2] p[j-1]`. It means "zero or more `x`":

- **zero copies**: throw away the whole pair (two pattern symbols), keep the text;
- **one more copy**: if `x` matches `s[i-1]` (`x == s[i-1]` or `x == '.'`), the pair eats `s[i-1]` and **stays**, ready to eat more.

#### Q4. What is the recurrence?

```
if p[j-1] == '*':
    x = p[j-2]
    dp[i][j] = dp[i][j-2]                                   // zero copies of x   (TWO LEFT)
            || ( (x == s[i-1] || x == '.') && dp[i-1][j] )  // one more x          (UP, if x fits)
else if p[j-1] == '.' or p[j-1] == s[i-1]:
    dp[i][j] = dp[i-1][j-1]                                 // one-for-one         (DIAGONAL)
else:
    dp[i][j] = false
```

Compare with wildcards:

| | Wildcard `*` | Regex `x*` |
|---|---|---|
| "use it zero times" | `dp[i][j-1]` — drop one symbol | `dp[i][j-2]` — drop two symbols (`x` and `*`) |
| "use it once more" | `dp[i-1][j]` — always allowed | `dp[i-1][j]` — **only if `x` matches `s[i-1]`** |

That is the entire difference. Wildcard `*` eats any character; regex `x*` eats only copies of `x`.

#### Q5. What are the base cases?
- `dp[0][0] = true`.
- `dp[i][0] = false` for `i ≥ 1`.
- Row 0: an empty text matches `a*`, `a*b*`, `.*c*`… — every pair used zero times. So `dp[0][j] = (p[j-1] == '*') && dp[0][j-2]`, and `dp[0][1] = false` (a single letter cannot match nothing).

#### Q6. What is the order of computation?
`dp[i][j]` reads `[i][j-2]` (same row, to the left), `[i-1][j]` and `[i-1][j-1]` (previous row). Row by row, left to right works.

#### Q7. Can the state space be optimized?
Yes, to two rows, exactly as for wildcards. We keep the full table in [RegexTabulation.java](solution/RegexTabulation.java) because it is easier to debug, and show the two-row idea once, for wildcards.

---

## 5. Brute force: recursion

Recursion is easiest in the **suffix form**: `f(i, j)` = "does `s[i..]` match `p[j..]`?". It starts at `f(0, 0)` and moves forward.

### Wildcard

```java
boolean f(int i, int j) {
    if (j == m) return i == n;                     // pattern finished: text must be finished too
    if (i == n) return p[j..] is all '*';          // text finished: only stars can still "match"
    if (p[j] == '*') return f(i, j + 1)            // star = ""
                         || f(i + 1, j);           // star eats s[i], stays
    if (p[j] == '?' || p[j] == s[i]) return f(i + 1, j + 1);
    return false;
}
```

Recursion tree for `s = "adceb"`, `p = "*a*b"` (✗ = false, ✓ = true; `||` stops at the first true):

```
f(0,0) '*'
├── f(0,1) 'a'=='a'
│   └── f(1,2) '*'
│       ├── f(1,3) 'b' vs 'd' ✗
│       └── f(2,2) '*'
│           ├── f(2,3) 'b' vs 'c' ✗
│           └── f(3,2) '*'
│               ├── f(3,3) 'b' vs 'e' ✗
│               └── f(4,2) '*'
│                   └── f(4,3) 'b'=='b'
│                       └── f(5,4)  both finished ✓
└── f(1,0) ...   (never explored: the left branch already returned true)
```

### Why it is slow
Every `*` branches in two. When the answer is **false**, the recursion must explore every branch. Different paths reach the same `(i, j)`. A tiny example, `s = "ab"`, `p = "**"`:

```
f(0,0)
├── f(0,1)
│   ├── f(0,2) ✗
│   └── f(1,1)  <-- here
│       ...
└── f(1,0)
    ├── f(1,1)  <-- and here again (same subproblem)
    └── f(2,0)
```

With `k` stars and a long text, the number of paths grows exponentially, even though there are only `(n+1)(m+1)` different `(i, j)` pairs.

### Regex

```java
boolean f(int i, int j) {
    if (j == m) return i == n;
    boolean first = i < n && (p[j] == '.' || p[j] == s[i]);
    if (j + 1 < m && p[j + 1] == '*')
        return f(i, j + 2)               // zero copies: skip "x*"
            || (first && f(i + 1, j));   // one copy: eat s[i], keep "x*"
    return first && f(i + 1, j + 1);
}
```

Note the **look-ahead**: in suffix form we must check `p[j+1]` before using `p[j]`, because `p[j]` might be the `x` of an `x*` pair. Recursion tree for `s = "aab"`, `p = "c*a*b"`:

```
f(0,0)  "c*" pair; 'c' vs 'a' does not fit
└── f(0,2)  zero c's.  "a*" pair
    ├── f(0,4)  zero a's; 'b' vs 'a' ✗
    └── f(1,2)  one 'a' eaten, pair stays
        ├── f(1,4)  'b' vs 'a' ✗
        └── f(2,2)  second 'a' eaten
            ├── f(2,4)  'b'=='b'
            │   └── f(3,5)  both finished ✓
            └── (not needed)
```

---

## 6. Memoization (top-down)

Cache the answer of every `(i, j)` the first time it is computed. Booleans have three states (unknown, true, false), so the solutions use a `byte[][]` with 0 = unknown, 1 = true, 2 = false.

```java
if (memo[i][j] != 0) return memo[i][j] == 1;   // cache hit
...compute result...
memo[i][j] = (byte) (result ? 1 : 2);
```

On our example `s = "adceb"`, `p = "*a*b"` the very first path succeeds (section 5), so the cache is never hit — `||` stops early. The cache pays off when answers are **false** and every branch must be explored. Run `s = "acdcb"`, `p = "a*c?b"` (answer false):

```
f(1,1) '*'  -> tries f(1,2) and f(2,1)
f(2,1) '*'  -> tries f(2,2) and f(3,1)
f(3,1) '*'  -> tries f(3,2) and f(4,1)
...
f(1,2) 'c' vs 'c' -> f(2,3) '?' -> f(3,4) 'b' vs 'c' ✗   stored as false
f(3,2) 'c' vs 'c' -> f(4,3) '?' -> f(5,4) text finished, 'b' left ✗
```

Each `f(i, 1)` (the star) is computed once. Without memoization, a pattern like `a*a*a*a*b` against `aaaaaaaaaaaa` would revisit the same `(i, j)` pairs an exponential number of times; with memoization each is solved once and later calls return immediately.

Code: [WildcardMemoization.java](solution/WildcardMemoization.java), [RegexMemoization.java](solution/RegexMemoization.java).

---

## 7. Tabulation (bottom-up)

### Wildcard: `s = "adceb"`, `p = "*a*b"`

The table has `n+1 = 6` rows (empty prefix + 5 letters) and `m+1 = 5` columns (empty pattern + 4 symbols).

**Row 0 (empty text).** `dp[0][0] = T`. Column 1 is `*` → `T`. Column 2 is `a` → `F`, and everything after is `F`.

```
        ''   *    a    *    b
  ''    T    T    F    F    F
```

**Row 1 (`a`).** `dp[1][0] = F`.
- col 1 `*`: left `dp[1][0]=F`, up `dp[0][1]=T` → **T** (the star ate `a`).
- col 2 `a`: matches `a`, diagonal `dp[0][1]=T` → **T** (star ate nothing, `a`=`a`).
- col 3 `*`: left `dp[1][2]=T` → **T**.
- col 4 `b`: `b` ≠ `a` → **F**.

```
        ''   *    a    *    b
  ''    T    T    F    F    F
  a     F    T    T    T    F
```

**Rows 2–4 (`d`, `c`, `e`).** Column 1 stays T by looking up (the first star keeps eating). Column 2 is `a`, which does not match `d`/`c`/`e`, so F. Column 3 is a star: left is F, but **up is T** — the second star keeps eating. Column 4 `b` does not match.

```
        ''   *    a    *    b
  ''    T    T    F    F    F
  a     F    T    T    T    F
  d     F    T    F    T    F      col 3: up (T) — second star ate 'd'
  c     F    T    F    T    F      col 3: up (T) — second star ate 'c'
  e     F    T    F    T    F      col 3: up (T) — second star ate 'e'
```

**Row 5 (`b`).** Col 1 T (up), col 2 F, col 3 T (up). Col 4: `b` = `b`, diagonal `dp[4][3] = T` → **T**.

```
        ''   *    a    *    b
  ''    T    T    F    F    F
  a     F    T    T    T    F
  d     F    T    F    T    F
  c     F    T    F    T    F
  e     F    T    F    T    F
  b     F    T    F    T    T   <- answer dp[5][4] = T
```

Trace the true path back from the bottom-right corner: diagonal (`b`=`b`) to `[4][3]`, up three times (star ate `e`, `c`, `d`) to `[1][3]`, left (star ate nothing more) to `[1][2]`, diagonal (`a`=`a`) to `[0][1]`, left (first star = `""`) to `[0][0]`. That is exactly the matching we described in section 3.

Code: [WildcardTabulation.java](solution/WildcardTabulation.java).

### Regex: `s = "aab"`, `p = "c*a*b"`

Section 9 fills this table step by step.

---

## 8. Space optimization

In the wildcard table every cell reads only:

```
  row i-1:   [i-1][j-1]   [i-1][j]
  row i:     [i][j-1]     [i][j]  <- being computed
```

So we keep two arrays, `prev` (row `i-1`) and `curr` (row `i`), and swap after each row. `curr[0]` is always false for `i ≥ 1` (non-empty text, empty pattern).

```java
boolean[] prev = row 0;
for (int i = 1; i <= n; i++) {
    boolean[] curr = new boolean[m + 1];
    for (int j = 1; j <= m; j++) {
        if (p[j-1] == '*')                       curr[j] = curr[j-1] || prev[j];
        else if (p[j-1] == '?' || p[j-1] == s[i-1]) curr[j] = prev[j-1];
    }
    prev = curr;
}
return prev[m];
```

Space drops from O(n·m) to O(m). The same trick works for regex (it reads `curr[j-2]`, `prev[j]`, `prev[j-1]`).

Code: [WildcardOptimized.java](solution/WildcardOptimized.java).

---

## 9. Dry run on a full example — the regex table

`s = "aab"`, `p = "c*a*b"`. Columns: `''`, `c`, `*`, `a`, `*`, `b` (j = 0..5).

**Row 0 (empty text).**

| j | symbol | rule | value |
|---|---|---|---|
| 0 | — | base | T |
| 1 | `c` | a letter cannot match nothing | F |
| 2 | `*` | pair `c*` zero times: `dp[0][0]` | T |
| 3 | `a` | letter | F |
| 4 | `*` | pair `a*` zero times: `dp[0][2]` | T |
| 5 | `b` | letter | F |

**Row 1 (`a`).**

| j | symbol | zero copies `[i][j-2]` | one more copy | value |
|---|---|---|---|---|
| 1 | `c` | — | `c` ≠ `a` | F |
| 2 | `*` (x=`c`) | `dp[1][0]` = F | `c` ≠ `a` → no | F |
| 3 | `a` | — | `a`=`a`, diagonal `dp[0][2]` = T | **T** |
| 4 | `*` (x=`a`) | `dp[1][2]` = F | `a`=`a` and up `dp[0][4]` = T | **T** |
| 5 | `b` | — | `b` ≠ `a` | F |

Read `dp[1][4] = T` as: "`a` is matched by `c*a*`" — zero c's, one a.

**Row 2 (`aa`).**

| j | symbol | zero copies | one more copy | value |
|---|---|---|---|---|
| 1 | `c` | — | no | F |
| 2 | `*` (x=`c`) | `dp[2][0]` = F | no | F |
| 3 | `a` | — | diagonal `dp[1][2]` = F | F |
| 4 | `*` (x=`a`) | `dp[2][2]` = F | `a`=`a` and up `dp[1][4]` = T | **T** |
| 5 | `b` | — | `b` ≠ `a` | F |

`dp[2][3] = F` is worth a pause: "`aa` matched by `c*a`" is false because a single `a` cannot eat two a's. Only the `a*` pair (column 4) can.

**Row 3 (`aab`).**

| j | symbol | zero copies | one more copy | value |
|---|---|---|---|---|
| 1 | `c` | — | no | F |
| 2 | `*` (x=`c`) | F | no | F |
| 3 | `a` | — | `a` ≠ `b` | F |
| 4 | `*` (x=`a`) | `dp[3][2]` = F | `a` ≠ `b` → no | F |
| 5 | `b` | — | `b`=`b`, diagonal `dp[2][4]` = T | **T** |

Final table:

```
        ''   c    *    a    *    b
  ''    T    F    T    F    T    F
  a     F    F    F    T    T    F
  a     F    F    F    F    T    F
  b     F    F    F    F    F    T   <- answer dp[3][5] = T
```

Arrows of the true path: `[3][5]` diagonal (b=b) → `[2][4]` up (a* ate an a) → `[1][4]` up (a* ate an a) → `[0][4]` two-left (a* zero more) → `[0][2]` two-left (c* zero times) → `[0][0]`.

Code: [RegexTabulation.java](solution/RegexTabulation.java).

---

## 10. Complexity analysis

| Approach | Time | Space | Why |
|---|---|---|---|
| Brute-force recursion | exponential | O(n + m) | each `*` branches in two and the same `(i, j)` is reached by many paths; depth is at most `n + m` because every call advances `i` or `j` |
| Memoization | O(n·m) | O(n·m) | `(n+1)(m+1)` states, each computed once with O(1) work (two lookups); plus recursion stack O(n + m) |
| Tabulation | O(n·m) | O(n·m) | one pass over the table, O(1) per cell |
| Two rows | O(n·m) | O(m) | same work, only two rows stored |

The one exception: in the memoized wildcard, the "text finished" base case scans the rest of the pattern, O(m). It runs at most once per `j` because it is only reached when `i == n`, so the total stays O(n·m).

---

## 11. Why this approach works

Every way of matching the pattern to the text is a sequence of decisions, one pattern symbol at a time: a letter or `?`/`.` eats one character; a star decides, character by character, whether to eat one more or stop. Our recurrence lists **all** these decisions for the last symbol and asks the smaller table cell whether the rest can be matched. If any decision leads to a true smaller cell, the whole match exists; if none does, no matching can exist, because every matching must start with one of these decisions. The table cells are smaller prefixes, so they are already correct when we read them (induction on `i + j`).

The "stay in the same column" trick is what makes the star correct: a star that eats `r` characters is represented by `r` "up" moves followed by one "left" move (wildcard) or one "two-left" move (regex).

---

## 12. Common mistakes

1. **Mixing the two stars.** In regex, `a*` can match the empty string; in wildcards, `a*` must start with `a`.
2. **Forgetting row 0.** `dp[0][j]` is not all false: `""` matches `***` (wildcard) and `a*b*` (regex). Many wrong submissions fail exactly on `s = ""`.
3. **Using `dp[i-1][j-1]` for the star.** That lets the star eat exactly one character. Use `dp[i-1][j]` so it can keep eating.
4. **Checking `x` against the wrong text character in regex.** In "one more copy" compare `p[j-2]` (the starred character), not `p[j-1]` (the star itself), with `s[i-1]`.
5. **Off-by-one between table indices and string indices.** `dp[i][j]` talks about `s[i-1]` and `p[j-1]`. Write this down at the top of your code.
6. **Partial matching.** The answer is `dp[n][m]`, not "any true cell in the last row".
7. **Regex suffix recursion without look-ahead.** In suffix form you must check whether `p[j+1] == '*'` before treating `p[j]` as a single letter.

---

## 13. When to use this technique / how to recognize similar problems

**Two-prefix DP** — two sequences consumed from the front:
- "Does pattern P match text S?" (LC 44, LC 10)
- "Transform A into B" (edit distance, delete operations LC 583, LC 712)
- "Common / interleaved / merged sequences" (LCS, LC 1035, LC 97, SCS)

**Substring DP** — one sequence, answer for a piece depends on its ends or inner pieces:
- "Palindromic …" (LC 5, LC 647, LC 516, LC 1312)
- "Split the range at some point" (topics [03](../03-interval-dp/)–[06](../06-palindrome-partitioning/))

### Practice worked out: Interleaving String ([LC 97](https://leetcode.com/problems/interleaving-string/))

Summary: given `s1`, `s2`, `s3`, decide whether `s3` can be built by merging `s1` and `s2` while keeping each one's internal order.

1. **Changing:** how much of `s1` and of `s2` we have used. The position in `s3` is always `i + j`, so it is not a separate dimension.
2. **State:** `dp[i][j]` = true if the first `i` chars of `s1` and the first `j` chars of `s2` can interleave into the first `i + j` chars of `s3`.
3. **Choices:** the last character of that `s3` prefix, `s3[i+j-1]`, came either from `s1[i-1]` or from `s2[j-1]`.
4. **Recurrence:** `dp[i][j] = (s1[i-1] == s3[i+j-1] && dp[i-1][j]) || (s2[j-1] == s3[i+j-1] && dp[i][j-1])`.
5. **Base cases:** `dp[0][0] = true`; first row/column compare one string alone with `s3`. Also return false immediately if `len(s1) + len(s2) != len(s3)`.
6. **Order:** row by row, left to right.
7. **Optimization:** one row of length `len(s2) + 1`.

This is the two-prefix shape — even though there are three strings, only two positions are free.

### Practice worked out: the `isPal[i][j]` table (LC 5, LC 647)

This is the substring shape.

1. **Changing:** the two ends of a substring.
2. **State:** `isPal[i][j]` = true if `s[i..j]` is a palindrome.
3. **Choices:** none really — just check the outer pair and the inside.
4. **Recurrence:** `isPal[i][j] = (s[i] == s[j]) && (j - i < 2 || isPal[i+1][j-1])`.
5. **Base cases:** single characters (`j == i`) are palindromes; two equal characters (`j == i + 1`) are palindromes. The `j - i < 2` test covers both.
6. **Order:** `isPal[i][j]` needs `isPal[i+1][j-1]` — the row **below**. So loop `i` from `n-1` down to `0`, and `j` from `i` up to `n-1`.
7. **Optimization:** possible (the "expand around centre" method uses O(1) extra space), but the table is reused in [Palindrome Partitioning](../06-palindrome-partitioning/), so we keep it.

Table for `s = "abba"` (only `j ≥ i` is meaningful):

```
          j=0  j=1  j=2  j=3
  i=0      T    F    F    T     "abba": a==a and inside "bb" is T
  i=1           T    T    F     "bb":   b==b, length 2
  i=2                T    F
  i=3                     T
```

LC 647 counts the true cells (6). LC 5 returns the longest true cell (`abba`). Code: [PalindromicSubstrings.java](solution/PalindromicSubstrings.java).

---

## 14. Summary and revision notes

- Two-prefix DP: two strings, `dp[i][j]` for the first `i` and first `j` characters, base row/column 0, fill row by row, answer `dp[n][m]`.
- Substring DP: one string, `dp[i][j]` for `s[i..j]`, base cases on the diagonal, fill by length (or `i` descending), answer `dp[0][n-1]`.
- Wildcard `*` is standalone and matches any run. Regex `*` makes the previous character repeat zero or more times.
- "Stay in the same column" (look **up**) lets a star eat many characters one at a time.
- Row 0 matters: empty text can still match stars.

```
Wildcard (LC 44)
  dp[i][j] = dp[i][j-1] || dp[i-1][j]                         if p[j-1] == '*'
           = dp[i-1][j-1]                                     if p[j-1] == '?' or == s[i-1]
           = false                                            otherwise

Regex (LC 10)
  dp[i][j] = dp[i][j-2] || (fits(p[j-2], s[i-1]) && dp[i-1][j])   if p[j-1] == '*'
           = dp[i-1][j-1]                                         if p[j-1] == '.' or == s[i-1]
           = false                                                otherwise
  fits(x, c) = (x == '.' || x == c)
```

---

## Exercises

**1.** Fill row 0 of the wildcard table for `p = "**a*"`.

<details><summary>Answer</summary>

Columns `''`, `*`, `*`, `a`, `*`: `T T T F F`. The two leading stars can match nothing; `a` cannot; once a cell is false, the rest of row 0 stays false because each cell needs the one to its left.
</details>

**2.** Fill row 0 of the regex table for `p = "a*b*c"` and say whether `s = ""` matches.

<details><summary>Answer</summary>

Columns `''`, `a`, `*`, `b`, `*`, `c`: `T F T F T F`. `a*` and `b*` can both be used zero times, but `c` needs a character. So `""` does not match (`dp[0][5] = F`).
</details>

**3.** Does `s = "aaa"` match the regex `p = "ab*a*c*a"`? Decide by reasoning about the pairs, then check by filling the table.

<details><summary>Answer</summary>

Yes. `a` = first `a`, `b*` = zero b's, `a*` = one `a`, `c*` = zero c's, final `a` = third `a`. In the table (columns 0..8 for `'' a b * a * c * a`), the true path is: `dp[3][8]` diagonal (final `a`) → `dp[2][7]` two-left (`c*` zero times) → `dp[2][5]` up (`a*` ate the second `a`) → `dp[1][5]` two-left (`a*` zero more) → `dp[1][3]` two-left (`b*` zero times) → `dp[1][1]` diagonal (`a` = `a`) → `dp[0][0]`.
</details>

**4.** For Interleaving String with `s1 = "ab"`, `s2 = "c"`, `s3 = "acb"`, fill the 3×2 table `dp[i][j]` (`i` = chars of `s1` used, `j` = chars of `s2` used).

<details><summary>Answer</summary>

```
         j=0   j=1
  i=0     T     F     ("" + "c" vs "a": no)
  i=1     T     T     ("a" + "c" vs "ac": c from s2)
  i=2     F     T     ("ab" + "c" vs "acb": b from s1, coming from dp[1][1])
```

`dp[2][1] = T`, so `acb` is an interleaving. `dp[2][0] = F` because `"ab"` ≠ `"ac"`.
</details>
