# Distinct Subsequences — Walkthrough

## 1. Intuition

A row of letter tiles lies on a table:

```
position:  0  1  2  3  4  5  6
tile:      b  a  b  g  b  a  g
```

You want to spell `bag` by picking up three tiles **from left to right** (a `b`, then an `a` further right, then a `g` further right). How many different triples of positions work?

```
position:   0  1  2  3  4  5  6
tile:       b  a  b  g  b  a  g
(0,1,3)     b  a     g               ✔
(0,1,6)     b  a              g      ✔
(0,5,6)     b              a  g      ✔
(2,5,6)           b        a  g      ✔
(4,5,6)                 b  a  g      ✔
```

**5** ways. All of them spell the same word; they differ in *which tiles* were picked.

How do we count without listing? Look at the **last tile**, the `g` at position 6. Every way either:

- **does not use** this tile — then it is a way of spelling `bag` from the first six tiles; or
- **uses** it as the final `g` of `bag` — then the rest is a way of spelling `ba` from the first six tiles.

No way can be in both groups (a way either picks tile 6 or it doesn't), so the total is the **sum** of the two counts. The same reasoning applies to every smaller problem.

## 2. Problem statement in our own words

Original: [LeetCode 115 — Distinct Subsequences](https://leetcode.com/problems/distinct-subsequences/).

Given strings `s` and `t` (English letters), return the number of distinct subsequences of `s` that equal `t` — i.e., the number of different sets of positions in `s` whose characters, read in order, spell `t`. LeetCode guarantees that **the answer** fits in a 32-bit signed integer (it does not promise this for intermediate values).

## 3. Examples

| s | t | answer | why |
|---|---|---|---|
| `babgbag` | `bag` | 5 | the five position triples above |
| `rabbbit` | `rabbit` | 3 | choose which two of the three `b`s to use: C(3,2) = 3 |
| `abc` | `""` | 1 | the empty pattern is spelled by picking nothing — one way |
| `ab` | `abc` | 0 | the pattern is longer than the source |
| `aaaa…a` (30) | `aaaa…a` (15) | 155 117 520 | choose any 15 of 30 positions: C(30, 15) |

**Running example:** `s = "babgbag"` (n = 7), `t = "bag"` (m = 3), answer **5**.

## 4. Deriving the solution — the 7 questions

### 4.1 What is changing?

How much of `s` we may still pick from, and how much of `t` we still need to spell. As in [LCS](../12-longest-common-subsequence/) we use **prefixes**: the first `i` characters of `s` and the first `j` of `t`.

### 4.2 What does the state represent?

> `ways(i, j)` = the number of ways to choose positions in `s[0..i)` that spell exactly `t[0..j)`.

Answer: `ways(n, m)`.

### 4.3 What are the choices?

Focus on the last available source character, `s[i-1]`:

- **Skip it** (always allowed): spell all of `t[0..j)` from `s[0..i-1)`.
- **Use it** (only if `s[i-1] == t[j-1]`): it becomes the last character of the spelling, so spell `t[0..j-1)` from `s[0..i-1)`.

Unlike LCS, a match does **not** force us to use the character: in `"babgbag"`, skipping the last `g` and using the earlier `g` at position 3 is a perfectly good (different) way.

### 4.4 What is the recurrence?

```
                ┌ ways(i-1, j) + ways(i-1, j-1)     if s[i-1] == t[j-1]
ways(i, j)  =   ┤
                └ ways(i-1, j)                      otherwise
```

We **add** because the two options are disjoint (one uses position `i-1`, the other doesn't) and together they cover every way.

### 4.5 What are the base cases?

- `ways(i, 0) = 1` for every `i` — the empty pattern: pick nothing. Exactly one way (this is the "empty pile pays 0" idea from [Coin Change II](../09-coin-change-ii/)).
- `ways(0, j) = 0` for `j > 0` — nothing to pick from, but something to spell: impossible.

Order of checks in recursion: test `j == 0` first, so `ways(0, 0) = 1`.

### 4.6 What is the order of computation?

`ways(i, j)` needs only row `i-1` (columns `j` and `j-1`). Fill rows `i = 1..n` top to bottom. Within a row, any column order works for the 2D table.

### 4.7 Can the state space be optimized?

Yes — one array `ways[j]`. Because row `i` reads `ways[j-1]` from row `i-1`, we must update `j` from **right to left** so that `ways[j-1]` hasn't been overwritten yet (Section 8). Memory O(m).

## 5. Brute force: recursion

```java
private static long ways(String s, String t, int i, int j) {
    if (j == 0) return 1;          // empty pattern: one way
    if (i == 0) return 0;          // pattern left, source used up
    long skip = ways(s, t, i - 1, j);
    long use = 0;
    if (s.charAt(i - 1) == t.charAt(j - 1)) use = ways(s, t, i - 1, j - 1);
    return skip + use;
}
```

Full file: [solution/BruteForce.java](solution/BruteForce.java).

### The top of the recursion tree

`s = b a b g b a g` (positions 0–6), `t = b a g`. Node = `ways(i, j)` with the source character `s[i-1]` and the pattern character `t[j-1]`.

```
ways(7,3) 'g' vs 'g' equal → skip + use
├─ skip: ways(6,3) 'a' vs 'g' → skip only
│        └─ ways(5,3) 'b' vs 'g' → skip only
│                 └─ ways(4,3) 'g' vs 'g' equal
│                          ├─ skip: ways(3,3) … = 0
│                          └─ use:  ways(3,2)  ◄──────────────┐
└─ use:  ways(6,2) 'a' vs 'a' equal                            │
         ├─ skip: ways(5,2) 'b' vs 'a' → ways(4,2) 'g' vs 'a'  │
         │                              → ways(3,2)  ◄─────────┘ same state twice
         └─ use:  ways(5,1) 'b' vs 'b' equal → ways(4,1) + ways(4,0)=1 …
```

### Why it is slow

Every match splits the call in two, and only `i` shrinks by one per level. For `s = "aaaa…a"` and `t = "aaa…a"` every comparison matches: about 2ⁿ calls (and it really does count up to C(n, m) ways one by one). Repeated states like `ways(3,2)` above are everywhere.

## 6. Memoization (top-down)

There are `(n+1) × (m+1)` states (8 × 4 = 32 here). Cache them in a `long[][]`:

```java
if (j == 0) return 1;
if (i == 0) return 0;
if (memo[i][j] != -1) return memo[i][j];
long result = ways(s, t, i - 1, j);
if (s.charAt(i - 1) == t.charAt(j - 1)) result += ways(s, t, i - 1, j - 1);
memo[i][j] = result;
return result;
```

Full file: [solution/Memoization.java](solution/Memoization.java).

In the tree above, `ways(3,2)` ("how many ways to spell `ba` from `bab`?" = 1, positions (0,1)) is computed first inside the left branch and then read from the memo when the right branch reaches it.

## 7. Tabulation (bottom-up)

```java
long[][] dp = new long[n + 1][m + 1];
for (int i = 0; i <= n; i++) dp[i][0] = 1;           // empty pattern
for (int i = 1; i <= n; i++) {
    for (int j = 1; j <= m; j++) {
        dp[i][j] = dp[i - 1][j];                                         // skip s[i-1]
        if (s.charAt(i - 1) == t.charAt(j - 1)) dp[i][j] += dp[i - 1][j - 1];   // use it
    }
}
return dp[n][m];
```

Full file: [solution/Tabulation.java](solution/Tabulation.java).

### Filling the table row by row

Rows are prefixes of `s = "babgbag"`, columns are prefixes of `t = "bag"`.

```
         ""   b   a   g
  ""      1   0   0   0       base: empty source
```

- **Row 1 (`b`).** `j=1` b=b: up 0 + diag 1 = **1**. `j=2,3`: copy up (0).
- **Row 2 (`a`).** `j=1` a≠b: copy 1. `j=2` a=a: up 0 + diag `dp[1][1]`=1 = **1** ("ba" from positions 0,1). `j=3`: 0.
- **Row 3 (`b`).** `j=1` b=b: up 1 + diag 1 = **2** (two `b`s so far). `j=2`: copy 1. `j=3`: 0.
- **Row 4 (`g`).** `j=1`: 2. `j=2`: 1. `j=3` g=g: up 0 + diag `dp[3][2]`=1 = **1** (positions 0,1,3).
- **Row 5 (`b`).** `j=1` b=b: up 2 + diag 1 = **3**. `j=2`: 1. `j=3`: 1.
- **Row 6 (`a`).** `j=1`: 3. `j=2` a=a: up 1 + diag `dp[5][1]`=3 = **4** (each of the three `b`s can precede this `a`). `j=3`: 1.
- **Row 7 (`g`).** `j=1`: 3. `j=2`: 4. `j=3` g=g: up 1 + diag `dp[6][2]`=4 = **5**.

The complete table:

```
         ""   b   a   g
  ""      1   0   0   0
  b       1   1   0   0
  a       1   1   1   0
  b       1   2   1   0
  g       1   2   1   1
  b       1   3   1   1
  a       1   3   4   1
  g       1   3   4   5    ← answer dp[7][3] = 5
```

Read the last cell: **1** way that doesn't use the final `g` (positions 0,1,3) **+ 4** ways that do (each of the 4 ways to spell `ba` from `babgba`, followed by tile 6).

## 8. Space optimization

Row `i` uses only row `i-1`: `dp[i][j] = dp[i-1][j] + (match ? dp[i-1][j-1] : 0)`. With one array `ways[j]`:

- `ways[j]` before the update is `dp[i-1][j]` — fine, we add to it in place.
- We also need `dp[i-1][j-1]`, the **old** value of `ways[j-1]`.

If `j` goes **left to right**, `ways[j-1]` has already been overwritten with row `i` — the current character `s[i-1]` would effectively be used twice. Going **right to left**, `ways[j-1]` is still the old value when we read it. This is exactly the [0/1 knapsack](../02-zero-one-knapsack/) rule: each source character can be used at most once.

```java
long[] ways = new long[m + 1];
ways[0] = 1;
for (int i = 1; i <= n; i++) {
    char current = s.charAt(i - 1);
    for (int j = m; j >= 1; j--) {                 // RIGHT TO LEFT
        if (current == t.charAt(j - 1)) ways[j] += ways[j - 1];
    }
}
return ways[m];
```

Full file: [solution/Optimized.java](solution/Optimized.java).

The bug if you go left to right, on `s = "a"`, `t = "aa"` (correct answer 0):

```
start            ways = [1, 0, 0]
'a', j=1:  ways[1] += ways[0] → 1
'a', j=2:  ways[2] += ways[1] → 1     ✘ ways[1] is already the NEW value:
                                        the single 'a' was used for both t[0] and t[1]
```

Right to left: `j=2` reads the old `ways[1] = 0` → stays 0; then `j=1` becomes 1. Answer 0 ✔.

## 9. Dry run on a full example

The 1D array on the running example, after each character of `s` (changes in bold):

| after `s[i-1]` | ways[0] | ways[1] (`b`) | ways[2] (`ba`) | ways[3] (`bag`) |
|---|---|---|---|---|
| start | 1 | 0 | 0 | 0 |
| b | 1 | **1** | 0 | 0 |
| a | 1 | 1 | **1** | 0 |
| b | 1 | **2** | 1 | 0 |
| g | 1 | 2 | 1 | **1** |
| b | 1 | **3** | 1 | 1 |
| a | 1 | 3 | **4** | 1 |
| g | 1 | 3 | 4 | **5** |

Each row matches the 2D table row for the same prefix.

Second example, `s = "rabbbit"`, `t = "rabbit"` (answer 3). The full table:

```
         ""  r  a  b  b  i  t
  ""      1  0  0  0  0  0  0
  r       1  1  0  0  0  0  0
  a       1  1  1  0  0  0  0
  b       1  1  1  1  0  0  0
  b       1  1  1  2  1  0  0
  b       1  1  1  3  3  0  0
  i       1  1  1  3  3  3  0
  t       1  1  1  3  3  3  3   ← 3
```

The interesting cells: after three `b`s, `rab` can be spelled 3 ways (any one `b`) and `rabb` 3 ways (any two of three `b`s: C(3,2) = 3).

## 10. Complexity analysis

| Approach | Time | Space | Why |
|---|---|---|---|
| Brute force | O(2ⁿ) | O(n) | Two branches per match; `i` drops by one per level. |
| Memoization | O(n · m) | O(n · m) | (n+1)(m+1) states × O(1). |
| Tabulation | O(n · m) | O(n · m) | One cell per pair. |
| 1D right-to-left | O(n · m) | O(m) | n passes over m columns. |

With LeetCode's limits (`n, m ≤ 1000`) that is 10⁶ cells.

### Overflow — what LeetCode does and doesn't promise

LeetCode promises that the **final** answer fits in a signed 32-bit integer. It says nothing about the other cells, and they can be enormous. Example (one of our tests): `s = "b" + 40 × "a"`, `t = 20 × "a" + "b"`. The cell "spell 20 `a`s from 40 `a`s" holds C(40, 20) = 137 846 528 820 — more than 60 times `Integer.MAX_VALUE` — yet the final answer is 0 (the only `b` in `s` is at the front). With 1000 `a`s the intermediate C(1000, 500) has about 300 digits and overflows even `long`.

Why the Java answer is still right:

1. The recurrence only **adds non-negative numbers**.
2. Java `int`/`long` arithmetic wraps around **modulo 2³² / 2⁶⁴** — it never crashes, and addition modulo 2ᵏ is exact modulo 2ᵏ. So the final cell is correct modulo 2⁶⁴ (with `long`).
3. The true answer is less than 2³¹ < 2⁶⁴, so "correct modulo 2⁶⁴" means "correct".

(Another way to see it: every cell that actually **feeds** the final answer is at most the final answer, because all contributions are non-negative; huge cells never feed it.)

What to do in practice:

- **Java:** use `long` (our files do). It is correct for the reason above, and it keeps moderate intermediates exact, which helps debugging.
- **C++:** signed overflow is *undefined behaviour*, so the same reasoning is not allowed. Use `unsigned long long` (defined wrap-around) or `double`, which is why you see those in C++ solutions.
- **Any language:** you may cap values (`min(value, Integer.MAX_VALUE)`): a capped cell can never feed a final answer that fits.

## 11. Why this approach works

Every way of spelling `t[0..j)` from `s[0..i)` is a set of positions. Split all such sets by one yes/no question: **does it contain position `i-1`?**

- No → it is a way of spelling `t[0..j)` from `s[0..i-1)`. All of those are counted by `ways(i-1, j)`, and each exactly once.
- Yes → position `i-1` is the last chosen position, so it spells the last character `t[j-1]` (requires `s[i-1] == t[j-1]`), and the remaining positions spell `t[0..j-1)` from `s[0..i-1)`: counted by `ways(i-1, j-1)`.

The two groups don't overlap and cover everything, so the sum is exactly right. The base cases count the empty spelling (1 way) and impossible spellings (0 ways).

## 12. Common mistakes

1. **Base case `ways(i, 0) = 0`.** Then everything is 0. The empty pattern has exactly one way.
2. **On a match, using only the "use" option** (LCS habit). You'd miss every way that uses a later or earlier copy of the character.
3. **Left-to-right 1D update.** One source character gets used for several pattern positions (see the `"a"` / `"aa"` demo).
4. **`int` in C++ without thinking.** Intermediate cells overflow; in C++ that is undefined behaviour. In Java it wraps harmlessly, but understand why.
5. **Swapping roles of `s` and `t`.** Rows walk over the **source** `s`; columns over the **pattern** `t`.

## 13. When to use this technique / how to recognize similar problems

- The question is **"how many ways"** to obtain one sequence from another by **deleting** elements (subsequence), or to embed a pattern in a text.
- Two sequences, prefix state, and an "always skip, use if it matches" choice.
- If the question asks how many **different strings** (not position sets) appear as subsequences, you need extra care to avoid double counting → [Distinct Subsequences II](https://leetcode.com/problems/distinct-subsequences-ii/).
- When rolling to 1D, ask: *"may the current item be used more than once?"* No → right to left (this topic, 0/1 knapsack). Yes → left to right ([Coin Change II](../09-coin-change-ii/)).

## 14. Summary and revision notes

- State: `ways(i, j)` = ways to spell `t[0..j)` from `s[0..i)`.
- Skip always; use when `s[i-1] == t[j-1]`; **add** them.
- Base: column 0 is all 1, row 0 (except `[0][0]`) is 0.
- 1D: `for j = m..1: if match, ways[j] += ways[j-1]` — **right to left**.
- Intermediate cells can be astronomically large; Java wraps safely because only additions are used and the answer fits; use `long`, and in C++ use unsigned or capping.

```
┌───────────────────────────────────────────────────────────────┐
│ dp[i][j] = dp[i-1][j] + (s[i-1] == t[j-1] ? dp[i-1][j-1] : 0) │
│ dp[i][0] = 1,  dp[0][j] = 0 (j > 0),   answer dp[n][m]         │
│ 1D: for each char c of s: for j = m..1: if c == t[j-1]:        │
│                                ways[j] += ways[j-1]            │
└───────────────────────────────────────────────────────────────┘
```

## 15. Exercises

**Exercise 1.** Compute the number of ways for `s = "aabb"`, `t = "ab"` using the 1D array. List the position pairs.

<details>
<summary>Answer</summary>

`[1,0,0]` → `a`: `[1,1,0]` → `a`: `[1,2,0]` → `b`: `ways[2] += ways[1] = 2` → `[1,2,2]` → `b`: `ways[2] += 2` → `[1,2,4]`. Answer **4**: (0,2), (0,3), (1,2), (1,3).
</details>

**Exercise 2.** What does the left-to-right (buggy) 1D update return for `s = "aa"`, `t = "aa"`? What is the correct answer?

<details>
<summary>Answer</summary>

Correct: 1 (positions (0,1)). Buggy: first `a`: `ways[1] = 1`, then `ways[2] += ways[1]` (new) `= 1`; second `a`: `ways[1] = 2`, `ways[2] += 2` → **3**. The bug lets one `a` play both roles.
</details>

**Exercise 3.** How would you use this DP to decide [Is Subsequence](https://leetcode.com/problems/is-subsequence/) (`t` is a subsequence of `s`)? Is it a good idea?

<details>
<summary>Answer</summary>

`t` is a subsequence of `s` iff `ways(n, m) ≥ 1`. It works, but it costs O(n·m) and risks huge intermediate numbers; a greedy two-pointer scan answers the yes/no question in O(n + m). Use the counting DP only when you need the count.
</details>
