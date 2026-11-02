# Decode Ways — Walkthrough

## 1. Intuition

A friend sends you a secret note, written by replacing every letter with its position in the alphabet (`A=1, B=2, …, Z=26`), but they forgot to put spaces between the numbers. You receive `1226`. How many different notes could your friend have meant?

Think about the **last letter** of the original note. It was written either with **one** digit (the final `6` → `F`) or with **two** digits (the final `26` → `Z`). In the first case, the rest of the note is a decoding of `122`; in the second case, of `12`. So:

```
decodings("1226") = decodings("122")   +   decodings("12")
                    (last letter "6")      (last letter "26")
```

That is the [Climbing Stairs](../02-climbing-stairs/) idea: the last move has length 1 or 2. The difference is that a move is only allowed if the digits it covers **spell a real letter**. `"0"` is not a letter, `"27"` is not a letter, and `"06"` is not a letter either. Those three facts are the whole difficulty of this problem.

## 2. Problem statement in our own words

You get a string `s` of digits. A *decoding* splits `s` into consecutive pieces such that every piece is a number from 1 to 26 written in the usual way (so `"6"` is fine but `"06"` is not). Count the decodings. If there are none, return 0.

Original: [LeetCode 91 · Decode Ways](https://leetcode.com/problems/decode-ways/) (there `1 ≤ s.length ≤ 100`, and the answer fits in a 32-bit integer).

## 3. Examples

| `s` | Output | Decodings |
|-----|--------|-----------|
| `"12"` | 2 | `1,2` (AB) · `12` (L) |
| `"226"` | 3 | `2,2,6` · `22,6` · `2,26` |
| `"27"` | 1 | `2,7` only — `27` is not a letter |
| `"1226"` | 5 | `1,2,2,6` · `12,2,6` · `1,22,6` · `1,2,26` · `12,26` |
| `"10"` | 1 | `10` (J) — the `0` cannot stand alone |
| `"11106"` | 2 | `1,1,10,6` · `11,10,6` |

**Running example: `s = "1226"`, answer `5`.** Then a second, zero-heavy example `"2101"` (answer 1) to study the edge cases.

### The `'0'` rules, collected

| Situation | Example | Why | Effect |
|-----------|---------|-----|--------|
| `0` alone | `"0"` → 0 | no letter has code 0 | single-digit move not allowed |
| `0` after `1` or `2` | `"10"`, `"20"` → 1 | `10` = J, `20` = T | **only** the pair move is allowed |
| `0` after `3`–`9` | `"30"` → 0 | `30` > 26 and `0` alone is invalid | no move at all → dead |
| `0` after `0` | `"100"` → 0 | `00` is not a letter, `0` alone is not | no move at all → dead |
| leading `0` | `"06"` → 0 | the first piece would be `0` or `06` | dead from the start |
| `0` as the first digit of a pair | `"101"`: the pair `01` | `01` has a leading zero | pair move not allowed, even though 1 ≤ 26 |

Every row above is handled automatically by two small checks; you will not need any special-case `if` for zeros beyond them.

## 4. Deriving the solution — the 7 questions

### Q1. What is changing?

How many characters of the string we have decoded so far. We use **prefix length** `i`: the prefix `s[0..i-1]` has `i` characters, and `i` goes from 0 (empty prefix) to `n` (whole string).

Why prefix length instead of "index of the last character"? Because it lets us talk about the **empty** prefix (`i = 0`) without negative indices, exactly like `ways[0]` in Climbing Stairs. You will see this indexing in most string DPs.

### Q2. What does the state represent?

> `ways[i]` = the number of ways to decode the first `i` characters `s[0..i-1]`.

The answer is `ways[n]`.

### Q3. What are the choices?

The last letter of a decoding of `s[0..i-1]` came from:

- **one digit** `s[i-1]` — allowed iff `s[i-1]` is `'1'`…`'9'` (i.e. not `'0'`);
- **two digits** `s[i-2] s[i-1]` — allowed iff `i ≥ 2`, `s[i-2] ≠ '0'`, and the two-digit number is between 10 and 26.

```java
boolean validSingle(char c)          { return c != '0'; }
boolean validPair(char a, char b)    { int v = (a-'0')*10 + (b-'0');
                                       return a != '0' && v >= 10 && v <= 26; }
```

(The `a != '0'` test is technically implied by `v >= 10`, since a pair starting with `0` has a value below 10. It is written out to make the "no leading zero" rule visible.)

### Q4. What is the recurrence?

```
ways[i] = (validSingle(s[i-1])         ? ways[i-1] : 0)
        + (i ≥ 2 && validPair(s[i-2], s[i-1]) ? ways[i-2] : 0)
```

Why `+`? Same reasoning as in [Climbing Stairs](../02-climbing-stairs/walkthrough.md#q4-what-is-the-recurrence): every decoding ends with a 1-digit piece or a 2-digit piece (cover), never both (disjoint), and removing the last piece gives a decoding of the shorter prefix (one-to-one).

When a move is **not** allowed, it contributes 0. That is how validity enters counting DP: invalid transitions are simply missing terms.

### Q5. What are the base cases?

```
ways[0] = 1
```

The empty prefix has exactly one decoding: the empty message. It is the starting point that every first piece extends. Check: `"12"` must give 2. `ways[1] = ways[0] = 1` (piece `1`), `ways[2] = ways[1] + ways[0] = 2` (pieces `1,2` and `12`). With `ways[0] = 0`, the decoding `12` would be lost.

There is no separate base case for `ways[1]`; the `i ≥ 2` guard handles it, and `ways[1]` comes out as 1 or 0 depending on whether `s[0]` is `'0'`. That is what makes `"0"` and `"06"` return 0 with no special code.

### Q6. What is the order of computation?

`ways[i]` reads `ways[i-1]` and `ways[i-2]` → `i = 1, 2, …, n`, left to right.

### Q7. Can the state space be optimized?

Two rolling variables, `O(1)` space.

## 5. Brute force: recursion

```java
private static int ways(String s, int i) {          // decodings of the first i chars
    if (i == 0) return 1;
    int count = 0;
    if (validSingle(s.charAt(i - 1))) count += ways(s, i - 1);
    if (i >= 2 && validPair(s.charAt(i - 2), s.charAt(i - 1))) count += ways(s, i - 2);
    return count;
}
// answer: ways(s, s.length())
```
Full file: [`solution/BruteForce.java`](solution/BruteForce.java).

### Recursion tree for `"1226"`

Edge labels show the last piece removed.

```
                                 w(4) "1226"  [5]
                       "6" ┌──────────┴──────────┐ "26"
                    w(3) "122" [3]           w(2) "12" [2]
                "2" ┌─────┴─────┐ "22"    "2" ┌───┴───┐ "12"
             w(2) "12" [2]   w(1) "1" [1]  w(1) [1]  w(0) [1]
          "2" ┌───┴───┐ "12"    │ "1"        │ "1"
           w(1) [1]  w(0) [1]  w(0) [1]    w(0) [1]
             │ "1"
           w(0) [1]

5 leaves w(0) = 5 decodings. Reading the edge labels from a leaf up to the root
gives one decoding, e.g. leftmost: 1, 2, 2, 6.
```

`w(2)` is solved twice and `w(1)` three times. For a string of 40 ones, every position allows both moves, and the tree has `F(41) = 165,580,141` leaves. The brute force is exponential.

### Recursion tree with a zero: `"2101"`

```
                    w(4) "2101" [1]
              "1" ┌──────┴──────┐ "01" ✗ (leading zero)
             w(3) "210" [1]        (0)
     "0" ✗ ┌──────┴──────┐ "10"
          (0)          w(1) "2" [1]
                          │ "2"
                        w(0) [1]

only decoding: 2, 10, 1
```

Invalid moves are pruned (they contribute 0), so they don't even create a branch.

## 6. Memoization (top-down)

```java
private static int[] memo;   // memo[i] = ways(i); -1 = not computed

private static int ways(String s, int i) {
    if (i == 0) return 1;
    if (memo[i] != -1) return memo[i];
    int count = 0;
    if (validSingle(s.charAt(i - 1))) count += ways(s, i - 1);
    if (i >= 2 && validPair(s.charAt(i - 2), s.charAt(i - 1))) count += ways(s, i - 2);
    return memo[i] = count;
}
```
Full file: [`solution/Memoization.java`](solution/Memoization.java).

**Watch the sentinel.** In counting problems, 0 is a perfectly real answer (the prefix `"10" + "0"` has 0 decodings). If you used 0 to mean "not computed", every dead prefix would be recomputed every time it is reached. That's not wrong, but it can bring back exponential time. Use `-1`.

| Order finished | State | single move | pair move | Stored |
|----------------|-------|-------------|-----------|--------|
| 1 | `w(1)` "1" | `'1'` ✓ → `w(0) = 1` | — | 1 |
| 2 | `w(2)` "12" | `'2'` ✓ → `w(1) = 1` | `"12"` ✓ → `w(0) = 1` | 2 |
| 3 | `w(3)` "122" | `'2'` ✓ → `w(2) = 2` | `"22"` ✓ → `w(1) = 1` (**hit**) | 3 |
| 4 | `w(4)` "1226" | `'6'` ✓ → `w(3) = 3` | `"26"` ✓ → `w(2) = 2` (**hit**) | **5** |

## 7. Tabulation (bottom-up)

```java
int[] ways = new int[n + 1];    // ways[i] = decodings of the first i characters
ways[0] = 1;
for (int i = 1; i <= n; i++) {
    if (validSingle(s.charAt(i - 1))) ways[i] += ways[i - 1];
    if (i >= 2 && validPair(s.charAt(i - 2), s.charAt(i - 1))) ways[i] += ways[i - 2];
}
return ways[n];
```
Full file: [`solution/Tabulation.java`](solution/Tabulation.java).

### Table for `"1226"`

```
 i            0     1     2     3     4
 prefix      ""    "1"  "12" "122" "1226"
 last char    —    '1'   '2'   '2'   '6'
 last pair    —     —   "12"  "22"  "26"

 i=1: '1' ok → +ways[0]=1                         → 1
 i=2: '2' ok → +ways[1]=1;  "12" ok → +ways[0]=1  → 2
 i=3: '2' ok → +ways[2]=2;  "22" ok → +ways[1]=1  → 3
 i=4: '6' ok → +ways[3]=3;  "26" ok → +ways[2]=2  → 5

 ways         1     1     2     3     5      ← answer 5
```

With no zeros and every pair ≤ 26, both moves are always allowed, and the table is the Fibonacci sequence again (1, 1, 2, 3, 5). Validity conditions are what make Decode Ways *different* from Climbing Stairs.

### Table for `"2101"` (zeros in the middle)

```
 i            0     1     2      3      4
 prefix      ""    "2"  "21"  "210"  "2101"
 last char    —    '2'   '1'    '0'    '1'
 last pair    —     —   "21"   "10"   "01"

 i=1: '2' ok → +1                                  → 1
 i=2: '1' ok → +ways[1]=1;  "21" ok → +ways[0]=1   → 2
 i=3: '0' ✗  → +0;          "10" ok → +ways[1]=1   → 1
 i=4: '1' ok → +ways[3]=1;  "01" ✗ (leading zero)  → 1

 ways         1     1     2     1     1      ← answer 1  (2, 10, 1)
```

Look at `i = 3`: the `0` *forced* the pair `10`, which "used up" the `1` and threw away the two decodings of `"21"` (`2,1` and `21`); only the decodings of `"2"` survive. Zeros glue themselves to the digit before them.

### Table for `"100"` (a dead prefix)

```
 i           0    1    2    3
 last char   —   '1'  '0'  '0'
 last pair   —    —   "10" "00"
 ways        1    1    1    0       i=3: '0' ✗ and "00" ✗ → 0
```

Once a prefix has 0 decodings **and** the next cell can't skip over it with a pair, the zero spreads: no later prefix can be decoded either.

## 8. Space optimization

```java
int waysTwoBack = 0;          // ways[i-2]
int waysOneBack = 1;          // ways[i-1], starts as ways[0]
for (int i = 1; i <= s.length(); i++) {
    int current = 0;
    if (validSingle(s.charAt(i - 1))) current += waysOneBack;
    if (i >= 2 && validPair(s.charAt(i - 2), s.charAt(i - 1))) current += waysTwoBack;
    waysTwoBack = waysOneBack;
    waysOneBack = current;
}
return waysOneBack;
```
Full file: [`solution/Optimized.java`](solution/Optimized.java).

Note `current` starts at 0 each iteration and only gains terms from **valid** moves. A common bug is to start it at `waysOneBack` ("copy the previous value") and then subtract when the digit is zero; building up from 0 is much harder to get wrong.

## 9. Dry run on a full example

`s = "11106"`, optimized version:

| i | char | pair | single adds | pair adds | current | twoBack | oneBack |
|---|------|------|-------------|-----------|---------|---------|---------|
| start | — | — | — | — | — | 0 | 1 |
| 1 | `1` | — | +1 | — | 1 | 1 | 1 |
| 2 | `1` | `11` ✓ | +1 | +1 | 2 | 1 | 2 |
| 3 | `1` | `11` ✓ | +2 | +1 | 3 | 2 | 3 |
| 4 | `0` ✗ | `10` ✓ | 0 | +2 | 2 | 3 | 2 |
| 5 | `6` | `06` ✗ | +2 | 0 | 2 | 2 | 2 |

Answer **2**: `1,1,10,6` and `11,10,6`. At `i = 4`, the zero forces the pair `10`, so only the decodings of `"11"` (two of them) survive. At `i = 5`, `06` is not a letter, so `6` must stand alone.

## 10. Complexity analysis

| Approach | Time | Space | Why |
|----------|------|-------|-----|
| Brute force | `O(2^n)` | `O(n)` | Up to two branches per character; one leaf per decoding. |
| Memoization | `O(n)` | `O(n)` | `n+1` states, two `O(1)` checks each; memo + stack. |
| Tabulation | `O(n)` | `O(n)` | One pass, table of `n+1`. |
| Optimized | `O(n)` | `O(1)` | One pass, two variables. |

## 11. Why this approach works

Every decoding of `s[0..i-1]` has a well-defined last piece, of length 1 or 2. If that piece is valid, removing it leaves a decoding of the shorter prefix, and conversely, appending a valid piece to any decoding of the shorter prefix gives a decoding of `s[0..i-1]`. So the decodings ending with a 1-digit piece are in one-to-one correspondence with the decodings of `s[0..i-2]` (when `s[i-1]` is a valid single), and likewise for 2-digit pieces. Groups are disjoint, so their sizes add. Invalid pieces correspond to no decodings at all, so they add 0.

## 12. Common mistakes

1. **Treating `"0"` as a letter.** `validSingle('0')` must be false.
2. **Accepting pairs like `"06"`.** `6 ≤ 26`, but a leading zero is not allowed. Check the first digit is not `'0'` (or that the value is at least 10).
3. **Accepting `"27"`–`"99"` as pairs.** The upper bound is 26.
4. **Special-casing `s.charAt(0) == '0'` and forgetting zeros elsewhere.** The general checks handle every position; extra special cases tend to hide bugs.
5. **Using 0 as the memo's "not computed" value.** 0 is a real count here.
6. **Initializing `current = waysOneBack`** and trying to "undo" it for zeros. Build `current` from 0 with only valid terms.
7. **`ways[0] = 0`.** Then nothing can ever be decoded.

## 13. When to use this technique / how to recognize similar problems

Signals:
- "in how many ways can the string be split / decoded / segmented";
- each piece must satisfy a rule (a letter code, a dictionary word, a palindrome);
- the answer is a count (use `+`) or a yes/no (use `||`).

Template over prefix lengths:
```
ways[0] = 1
ways[i] = Σ over valid last pieces s[j..i-1]  of  ways[j]
```
Decode Ways has pieces of length 1–2; [Word Break](https://leetcode.com/problems/word-break/) has pieces of any length that are dictionary words (and asks yes/no); [Palindrome Partitioning](../../04-advanced-dp-patterns/06-palindrome-partitioning/) has palindromic pieces and asks for the minimum number of cuts.

## 14. Summary and revision notes

- State: `ways[i]` = number of decodings of the first `i` characters; `ways[0] = 1`.
- Single move valid iff the digit is `1`–`9`. Pair move valid iff the two digits form `10`–`26`.
- Invalid moves contribute 0; zeros are handled with no extra code.
- A `0` must attach to a preceding `1` or `2`; otherwise the answer is 0.
- Use `-1` as the memo sentinel, since 0 is a real count.

```
┌────────────────────────────────────────────────────────────────┐
│  ways[0] = 1                                                   │
│  ways[i] = [ s[i-1] ∈ '1'..'9' ]          · ways[i-1]          │
│          + [ i ≥ 2 and s[i-2..i-1] ∈ 10..26 ] · ways[i-2]      │
│  answer: ways[n]           ([cond] = 1 if true, 0 if false)    │
└────────────────────────────────────────────────────────────────┘
```

## Exercises

1. Predict the answers without code: `"27"`, `"2020"`, `"301"`.

<details>
<summary>Answer</summary>

- `"27"` → **1**: `27 > 26`, so only `2,7`.
- `"2020"` → **1**: each `0` must join the `2` before it, giving `20,20`. Table: `ways = 1, 1, 1, 1, 1`.
- `"301"` → **0**: the `0` can't stand alone and `30 > 26`, so `ways[2] = 0`; then `ways[3]` adds `ways[2] = 0` (piece `1`) and nothing for the invalid pair `01`.
</details>

2. Fill the table for `"1201234"`.

<details>
<summary>Answer</summary>

| i | 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 |
|---|---|---|---|---|---|---|---|---|
| last char | — | 1 | 2 | 0 | 1 | 2 | 3 | 4 |
| last pair | — | — | 12 ✓ | 20 ✓ | 01 ✗ | 12 ✓ | 23 ✓ | 34 ✗ |
| ways | 1 | 1 | 2 | 1 | 1 | 2 | 3 | 3 |

- `i=3`: `0` ✗, `20` ✓ → `ways[1] = 1`.
- `i=4`: `1` ✓ → `ways[3] = 1`; `01` ✗ → 1.
- `i=5`: `2` ✓ → 1; `12` ✓ → `ways[3] = 1` → 2.
- `i=6`: `3` ✓ → 2; `23` ✓ → `ways[4] = 1` → 3.
- `i=7`: `4` ✓ → 3; `34` ✗ → 3.

Answer **3**: `1,20,1,2,3,4` · `1,20,12,3,4` · `1,20,1,23,4`.
</details>

3. Rewrite the DP over **suffixes**: `suf[i]` = number of decodings of `s[i..n-1]`. Give the base case, recurrence, order and answer.

<details>
<summary>Answer</summary>

- Base: `suf[n] = 1` (empty suffix).
- Recurrence: `suf[i] = (s[i] ≠ '0' ? suf[i+1] : 0) + (i+1 < n && validPair(s[i], s[i+1]) ? suf[i+2] : 0)`.
- Order: `i = n-1` down to `0` (dependencies point right).
- Answer: `suf[0]`.

Here the decision is about the **first** piece instead of the last. Both directions are correct; pick the one that feels natural, and make sure the loop direction follows the dependencies.
</details>

4. Why can't a string containing `"00"` ever be decoded?

<details>
<summary>Answer</summary>

Say the two zeros are at positions `p` and `p+1`. Some piece of the decoding must contain position `p+1`, and there are only three candidates:
- the single piece `"0"`: invalid;
- the pair at `p, p+1`, which is `"00"`: invalid;
- the pair at `p+1, p+2`, which starts with `0`: invalid (leading zero).

No piece can cover position `p+1`, so no decoding exists. In the table, `ways[p+2] = 0`, and since no piece can jump over position `p+1`, every later cell is 0 too.
</details>
