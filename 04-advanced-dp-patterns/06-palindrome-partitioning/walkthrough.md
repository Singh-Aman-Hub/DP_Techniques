# Palindrome Partitioning — Walkthrough

This lesson has three steps:

1. Build the `isPal[i][j]` table (a tool we use everywhere below).
2. **LC 131:** enumerate every palindrome partition with backtracking.
3. **LC 132:** the minimum number of cuts — first as an O(n³) interval DP, then as an O(n²) front-partition DP — and compare them.

Running examples: `s = "aab"` for LC 131 and `s = "abccbc"` for LC 132.

---

## 1. Intuition

Imagine a ribbon with letters printed on it. You want to cut it into pieces so that every piece is a palindrome (reads the same both ways). Single letters are always palindromes, so a solution always exists: cut between every pair of letters. The questions are: **what are all the ways?** (LC 131) and **what is the fewest number of cuts?** (LC 132).

Two ways to think about cutting:

```
Interval view (like MCM):                 Front view (like Decode Ways):
"Pick ANY cut inside the ribbon,          "Pick where the FIRST piece ends,
 then solve the two halves."                then solve whatever is left."

   a b c c b c                               a b c c b c
       ^ cut somewhere in the middle         [a]|b c c b c
   [a b] [c c b c]                            first   the rest = a suffix
   both halves are ranges i..j                piece   (one index!)
```

The interval view needs two indices per subproblem (`i` and `j`). The front view needs only one: where the remaining suffix starts. Both are correct, but the front view has far fewer states. That is the heart of this topic.

---

## 2. Problem statement in our own words

**[LC 131](https://leetcode.com/problems/palindrome-partitioning/):** given a string `s`, return all ways to split it into consecutive pieces such that every piece is a palindrome.

**[LC 132](https://leetcode.com/problems/palindrome-partitioning-ii/):** given a string `s`, return the minimum number of cuts needed so that every piece is a palindrome.

---

## 3. Examples

LC 131:

| `s` | All palindrome partitions |
|---|---|
| `"aab"` | `[a, a, b]`, `[aa, b]` |
| `"abba"` | `[a, b, b, a]`, `[a, bb, a]`, `[abba]` |
| `"a"` | `[a]` |

LC 132:

| `s` | Min cuts | One best partition |
|---|---|---|
| `"abccbc"` (main) | 2 | `a | bccb | c` |
| `"aab"` | 1 | `aa | b` |
| `"banana"` | 1 | `b | anana` |
| `"racecar"` | 0 | already a palindrome |
| `"a"` | 0 | — |

---

## 4. Deriving the solution — the 7 questions

### 4A. The helper table `isPal[i][j]`

Checking "is `s[i..j]` a palindrome?" from scratch costs O(n). We will ask it O(n²) times, so we precompute all answers once (this is the substring DP from [DP on Strings](../01-dp-on-strings/)):

```
isPal[i][j] = (s[i] == s[j]) && (j - i < 2 || isPal[i+1][j-1])
```

`isPal[i][j]` needs the cell **one row down, one column left**, so fill `i` from `n−1` down to `0`, and `j` from `i` upward.

`isPal` for `"abccbc"` (indices `a0 b1 c2 c3 b4 c5`; `.` = unused):

```
          j=0  j=1  j=2  j=3  j=4  j=5
   i=0     T    F    F    F    F    F
   i=1     .    T    F    F    T    F     "bccb" (1..4)
   i=2     .    .    T    T    F    F     "cc"   (2..3)
   i=3     .    .    .    T    F    T     "cbc"  (3..5)
   i=4     .    .    .    .    T    F
   i=5     .    .    .    .    .    T
```

### 4B. LC 132 with the front-partition DP

#### Q1. What is changing?
Where the part of the string we have not yet cut starts. Everything before it is already split into palindromes.

#### Q2. What does the state represent?
> `pieces[start]` = the minimum number of palindrome pieces needed to split the suffix `s[start..n-1]`.

Answer: `pieces[0] − 1` cuts (`p` pieces have `p − 1` cuts between them).

#### Q3. What are the choices?
Where the **first piece** of this suffix ends: any `end` with `start ≤ end ≤ n−1` such that `s[start..end]` is a palindrome.

#### Q4. What is the recurrence?

```
pieces[start] = min over end in [start, n-1] with isPal[start][end] of
                1 + pieces[end + 1]
```

`1` counts the first piece; `pieces[end+1]` is the best for what remains.

#### Q5. What are the base cases?
`pieces[n] = 0`: the empty suffix needs zero pieces. A choice always exists because `isPal[start][start]` is always true.

#### Q6. What is the order of computation?
`pieces[start]` reads `pieces[end+1]` with `end + 1 > start`. Fill `start` from `n−1` down to `0`.

#### Q7. Can the state space be optimized?
`pieces` is already 1D (O(n)). The `isPal` table is the O(n²) part. (It can be avoided with the "expand around every centre" trick, which updates `pieces` while growing palindromes outward — an O(1)-extra-space refinement you can try after this topic.)

### 4C. LC 132 with interval DP (for comparison)

- **State:** `cuts[i][j]` = min cuts to split `s[i..j]` into palindromes.
- **Choices:** if `s[i..j]` is a palindrome, 0 cuts. Otherwise, the position `k` of **some** cut (`i ≤ k < j`, cut between `k` and `k+1`).
- **Recurrence:** `cuts[i][j] = 0` if `isPal[i][j]`, else `min over k of cuts[i][k] + 1 + cuts[k+1][j]`.
- **Base:** `cuts[i][i] = 0`.
- **Order:** by length.
- **Cost:** O(n²) states × O(n) splits = **O(n³)**.

It is correct, but it computes answers for every substring `s[i..j]`, while the question only ever needs suffixes. Section 8 compares them.

---

## 5. Brute force: recursion

### LC 131 — backtracking

To list all partitions, we must actually build them. Choose the first piece, recurse on the rest, and **undo** the choice before trying the next one:

```java
void backtrack(int start, List<String> pieces) {
    if (start == n) { result.add(copy of pieces); return; }
    for (int end = start; end < n; end++) {
        if (!isPal[start][end]) continue;          // first piece must be a palindrome
        pieces.add(s.substring(start, end + 1));   // choose
        backtrack(end + 1, pieces);                // explore
        pieces.remove(pieces.size() - 1);          // un-choose
    }
}
```

Recursion tree for `"aab"` (each edge = the first piece chosen):

```
                        start=0, []
             _________/     |      \_________
         "a"               "aa"              "aab"  (not a palindrome: skipped)
          |                  |
    start=1, [a]       start=2, [aa]
      /        \             |
   "a"        "ab" (skip)   "b"
    |                        |
 start=2, [a,a]         start=3 -> record [aa, b]
    |
   "b"
    |
 start=3 -> record [a, a, b]
```

Output: `[[a, a, b], [aa, b]]`.

Backtracking cannot be replaced by DP here, because the **output itself** can be exponential: for `"aaaa…a"` (n letters) every one of the `2^(n−1)` ways to cut is valid. DP helps only with the palindrome checks (the `isPal` table).

Code: [PalindromePartitioning.java](solution/PalindromePartitioning.java).

### LC 132 — plain recursion

```java
int minPieces(int start) {
    if (start == n) return 0;
    int best = MAX;
    for (int end = start; end < n; end++)
        if (isPal[start][end]) best = Math.min(best, 1 + minPieces(end + 1));
    return best;
}
```

For `"abccbc"`, `minPieces(5)` is reached from `minPieces(4)` (piece `"b"`) and from `minPieces(1)` (piece `"bccb"`); `minPieces(4)` is reached from `minPieces(3)` (piece `"c"`) and from `minPieces(2)` (piece `"cc"`). Each repeated call re-solves its whole subtree. For `"aaaa…"`, the number of calls doubles with every extra letter.

---

## 6. Memoization (top-down)

Only `n + 1` different values of `start` exist, so cache them:

```java
if (start == n) return 0;
if (memo[start] != -1) return memo[start];
... loop over end ...
return memo[start] = best;
```

Dry run on `"abccbc"` (calls in the order they finish):

| call | palindromic first pieces `s[start..end]` | candidates `1 + pieces[end+1]` | result |
|---|---|---|---|
| minPieces(5) | `c` | 1 + 0 | 1 |
| minPieces(4) | `b` | 1 + minPieces(5) = 2 | 2 |
| minPieces(3) | `c`, `cbc` | 1 + minPieces(4) = 3; 1 + minPieces(6) = 1 | 1 |
| minPieces(2) | `c`, `cc` | 1 + 1 = 2; 1 + minPieces(4) **(cache hit)** = 3 | 2 |
| minPieces(1) | `b`, `bccb` | 1 + 2 = 3; 1 + minPieces(5) **(cache hit)** = 2 | 2 |
| minPieces(0) | `a` | 1 + minPieces(1) = 3 | 3 |

`pieces[0] = 3` → **2 cuts**.

Code: [MinCutsMemoization.java](solution/MinCutsMemoization.java).

---

## 7. Tabulation (bottom-up)

```java
int[] pieces = new int[n + 1];          // pieces[n] = 0
for (int start = n - 1; start >= 0; start--) {
    pieces[start] = Integer.MAX_VALUE;
    for (int end = start; end < n; end++)
        if (isPal[start][end])
            pieces[start] = Math.min(pieces[start], 1 + pieces[end + 1]);
}
return pieces[0] - 1;
```

Filled array for `"abccbc"`:

```
 start:    0    1    2    3    4    5    6
 char:     a    b    c    c    b    c    (end)
 pieces:   3    2    2    1    2    1    0
```

Code: [MinCutsTabulation.java](solution/MinCutsTabulation.java).

---

## 8. Space optimization — and the O(n³) vs O(n²) comparison

The front-partition DP already uses only an O(n) array besides `isPal`. The bigger optimisation in this topic is **in time**, by choosing the 1D state instead of the 2D one.

The interval table `cuts[i][j]` for `"abccbc"` (from [MinCutsInterval.java](solution/MinCutsInterval.java)):

```
          j=0  j=1  j=2  j=3  j=4  j=5
   i=0     0    1    2    2    1    2    <- answer 2
   i=1          0    1    1    0    1
   i=2               0    0    1    1
   i=3                    0    1    0
   i=4                         0    1
   i=5                              0
```

It has 21 cells and each non-palindromic cell tries up to 5 split points. The front-partition array has 7 cells, each scanning up to 6 ends. Look at the top row of the interval table: `cuts[0][j]` is the answer for **prefixes**; the right column `cuts[i][5]` is the answer for **suffixes** — it equals `pieces[i] − 1` (2, 1, 1, 0, 1, 0). The front DP computes only that column; the interval DP computes the whole triangle, which is never needed.

| | Interval DP | Front-partition DP |
|---|---|---|
| State | `cuts[i][j]`, any substring | `pieces[start]`, suffixes only |
| Number of states | ~n²/2 | n + 1 |
| Work per state | O(n) split points | O(n) first-piece ends |
| Time | **O(n³)** | **O(n²)** |
| Extra space | O(n²) | O(n) (+ O(n²) for `isPal`, shared by both) |

**Why is one index enough?** Any partition of the whole string has a first piece. Whatever the first piece is, the rest is a partition of a **suffix**. So every subproblem we ever need is a suffix — never a middle substring. Interval DP is the right tool only when the split can create a middle piece whose cost depends on both of its ends (MCM: the cost of the last multiplication depends on `i`, `k`, `j`).

**Rule of thumb:** if the pieces are simply consecutive blocks and the total is a **sum (or min/max) over blocks**, where each block's cost depends only on the block itself, use the front-partition 1D DP.

---

## 9. Dry run on a full example

Tabulation on `"abccbc"`, `start` from 5 down to 0, with every palindromic first piece:

| start | suffix | palindromic first pieces (end) | candidates `1 + pieces[end+1]` | pieces[start] |
|---|---|---|---|---|
| 6 | "" | — | base case | 0 |
| 5 | `c` | `c` (5) | 1 + 0 = 1 | **1** |
| 4 | `bc` | `b` (4) | 1 + 1 = 2 | **2** |
| 3 | `cbc` | `c` (3), `cbc` (5) | 1 + 2 = 3; 1 + 0 = 1 | **1** |
| 2 | `ccbc` | `c` (2), `cc` (3) | 1 + 1 = 2; 1 + 2 = 3 | **2** |
| 1 | `bccbc` | `b` (1), `bccb` (4) | 1 + 2 = 3; 1 + 1 = 2 | **2** |
| 0 | `abccbc` | `a` (0) | 1 + 2 = 3 | **3** |

`pieces[0] = 3` pieces → **2 cuts**. Following the choices: start 0 takes `a`, start 1 takes `bccb`, start 5 takes `c` → `a | bccb | c`.

Note the greedy trap at start 2: taking the longest palindrome `cc` gives 3, while the shorter `c` gives 2 (because `cbc` follows). Neither "always longest" nor "always shortest" is safe; the DP tries all.

---

## 10. Complexity analysis

| Approach | Time | Space | Why |
|---|---|---|---|
| `isPal` table | O(n²) | O(n²) | n(n+1)/2 cells, O(1) each |
| LC 131 backtracking | O(n · 2ⁿ) worst case | O(n²) + output | up to 2^(n−1) partitions (all-equal letters), each copied in O(n) |
| LC 132 plain recursion | exponential | O(n) stack | the same suffix is re-solved through many paths |
| LC 132 interval DP | O(n³) | O(n²) | ~n²/2 ranges × up to n split points |
| LC 132 front memo / tab | O(n²) | O(n²) for `isPal`, O(n) for `pieces` | n states × up to n ends, each check O(1) thanks to `isPal` |

For LC 132's limit `n ≤ 2000`: O(n²) ≈ 4 million steps; O(n³) ≈ 1.3 billion — too slow. The choice of state is the difference between accepted and Time Limit Exceeded.

---

## 11. Why this approach works

Every palindrome partition of `s[start..]` begins with some palindromic piece `s[start..end]`, followed by a palindrome partition of `s[end+1..]`. Its piece count is `1 +` (pieces in the rest). For a fixed first piece, the best total uses the best partition of the rest, which is `pieces[end+1]` — and the rest's partition does not depend on how we cut before `start`. Trying every palindromic first piece covers every partition. Since `end + 1 > start`, those values are already final when we fill right to left.

---

## 12. Common mistakes

1. **Checking palindromes inside the DP loop with a fresh O(n) scan.** That makes LC 132 O(n³) again. Precompute `isPal`.
2. **Filling `isPal` with `i` ascending.** `isPal[i][j]` needs `isPal[i+1][j-1]`; fill `i` descending.
3. **Pieces vs cuts.** Return `pieces[0] − 1`. (Or define `cuts[n] = −1` and add 1 per piece.)
4. **Forgetting to copy the list** in LC 131 (`result.add(pieces)` stores a reference that later changes). Add `new ArrayList<>(pieces)`.
5. **Greedy longest palindrome.** Fails on `"abccbc"` at index 2 (see section 9).
6. **Using interval DP by reflex.** It works but is O(n³); check whether only prefixes/suffixes are needed.

---

## 13. When to use this technique / how to recognize similar problems

**Front-partition DP** fits when:
- the sequence is split into **consecutive** blocks;
- each block has a validity rule or a cost that depends only on the block;
- the total combines block costs (sum, count, min, max, or "all valid").

Template (prefix form, equivalent to our suffix form):

```
dp[i] = best answer for the first i elements
dp[i] = best over the last block s[j..i-1] (valid) of  dp[j] (+) cost(j, i-1)
dp[0] = neutral value
```

Examples:
- **[LC 1043 Partition Array for Maximum Sum](https://leetcode.com/problems/partition-array-for-maximum-sum/):** split into blocks of length at most `k`; each block becomes its maximum repeated. `dp[i] = max over len = 1..min(k, i) of dp[i − len] + len · max(arr[i−len..i−1])`. Keep a running max while `len` grows so each step is O(1): O(n·k) total. On `[1, 15, 7, 9, 2, 5, 10]`, `k = 3`, the answer is 84 (`[15, 15, 15, 9, 10, 10, 10]`).
- **[LC 139 Word Break](https://leetcode.com/problems/word-break/):** `dp[i]` = can the first `i` characters be split into dictionary words.
- **[Decode Ways](../../01-one-dimensional-dp/08-decode-ways/):** blocks of length 1 or 2.

**Interval DP** is needed instead when the cost of combining depends on both neighbours or on the split point in a way that is not "sum of independent blocks" — MCM, burst balloons, triangulation.

---

## 14. Summary and revision notes

- Build `isPal[i][j]` once: `s[i] == s[j] && (j − i < 2 || isPal[i+1][j−1])`, `i` descending.
- LC 131: backtracking (choose first palindromic piece, recurse, undo). Output can be exponential.
- LC 132 interval DP: `cuts[i][j]` with split `k` → O(n³).
- LC 132 front partition: `pieces[start] = min over palindromic s[start..end] of 1 + pieces[end+1]` → O(n²).
- Consecutive blocks with block-local costs → one index is enough.

```
pieces[n] = 0
pieces[start] = min { 1 + pieces[end+1] : start <= end < n, isPal[start][end] }
min cuts = pieces[0] - 1
```

---

## Exercises

**1.** List all palindrome partitions of `"aaa"`. How many are there, and how many would there be for `"aaaa"`?

<details><summary>Answer</summary>

`[a, a, a]`, `[a, aa]`, `[aa, a]`, `[aaa]` — 4 = 2². For `"aaaa"` every one of the 3 gaps can be cut or not: 2³ = 8 partitions.
</details>

**2.** Fill `pieces[]` for `"banana"` and give the minimum cuts.

<details><summary>Answer</summary>

Palindromes starting at each index: 0: `b`; 1: `a`, `ana`, `anana`; 2: `n`, `nan`; 3: `a`, `ana`; 4: `n`; 5: `a`.
`pieces[6] = 0`, `pieces[5] = 1`, `pieces[4] = 2`, `pieces[3] = min(1 + 2, 1 + 0) = 1`, `pieces[2] = min(1 + 1, 1 + 1) = 2`, `pieces[1] = min(1 + 2, 1 + 2, 1 + 0) = 1`, `pieces[0] = 1 + 1 = 2`. Min cuts = **1** (`b | anana`).
</details>

**3.** Write the front-partition recurrence for LC 1043 in suffix form (`dp[start]` = best for `arr[start..]`).

<details><summary>Answer</summary>

`dp[n] = 0`; `dp[start] = max over len = 1..k with start + len ≤ n of len · max(arr[start..start+len−1]) + dp[start + len]`. Fill `start` from `n−1` down to 0, keeping a running maximum as `len` grows.
</details>

**4.** In the interval table for `"abccbc"`, `cuts[1][3] = 1`. Which split gives it, and why is the front-partition DP never interested in this cell?

<details><summary>Answer</summary>

`s[1..3] = "bcc"` is not a palindrome; split after index 1: `b | cc` → 0 + 1 + 0 = 1. The front DP only ever needs suffixes `s[start..5]`, and `"bcc"` is a middle substring (it ends at 3, not 5), so no partition of the whole string ever asks for it as a subproblem.
</details>
