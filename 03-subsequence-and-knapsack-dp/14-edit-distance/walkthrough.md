# Edit Distance — Walkthrough

## 1. Intuition

You typed `horse` but meant `ros`. Your keyboard has three repair buttons:

- **Insert** a character anywhere,
- **Delete** a character,
- **Replace** a character with another.

What is the fewest button presses to fix the word? One answer:

```
horse  → replace h with r → rorse
rorse  → delete r         → rose
rose   → delete e         → ros        3 presses
```

Is 3 the minimum? Trying every sequence of presses is impossible — there are infinitely many (you could insert and delete forever). We need a smarter view.

Look only at the **last characters**. `horse` ends in `e`, `ros` ends in `s`. In the final, repaired word, the last character must be `s`. Where does it come from? There are only a few possibilities:

1. The `e` was **replaced** by `s`. Then the rest, `hors`, must become `ro`.
2. The `e` was **deleted**. Then `hors` must become all of `ros`.
3. An `s` was **inserted** at the end. Then `horse` must become `ro`.

(And if the last characters had been equal, we could just keep them, for free.)

Each possibility leaves a **smaller version of the same problem**. That is the whole DP.

## 2. Problem statement in our own words

Original: [LeetCode 72 — Edit Distance](https://leetcode.com/problems/edit-distance/) (identical: [CSES 1639](https://cses.fi/problemset/task/1639)).

Given two strings `word1` and `word2` (possibly empty, lowercase letters), return the minimum number of operations to convert `word1` into `word2`, where one operation inserts a character, deletes a character, or replaces a character.

## 3. Examples

| word1 | word2 | answer | one optimal sequence |
|---|---|---|---|
| `horse` | `ros` | 3 | replace h→r, delete r, delete e |
| `intention` | `execution` | 5 | replace the first five letters `inten` → `execu` (other 5-step sequences exist) |
| `""` | `abc` | 3 | insert a, b, c |
| `abc` | `""` | 3 | delete a, b, c |
| `abc` | `abc` | 0 | already equal |

**Running example:** `word1 = "horse"` (n = 5), `word2 = "ros"` (m = 3), answer **3**.

## 4. Deriving the solution — the 7 questions

### 4.1 What is changing?

How much of each word is still "unprocessed": we work on **prefixes** `word1[0..i)` and `word2[0..j)`, exactly as in [LCS](../12-longest-common-subsequence/). Each operation removes the last character of one or both prefixes from consideration.

### 4.2 What does the state represent?

> `dist(i, j)` = the minimum number of operations to turn the first `i` characters of `word1` into the first `j` characters of `word2`.

Answer: `dist(n, m)`.

### 4.3 What are the choices?

Compare `word1[i-1]` (last char of the source prefix) with `word2[j-1]` (last char of the target prefix).

- **Equal** → nothing to do for this pair: `dist(i-1, j-1)`, cost 0.
- **Different** → one of three operations produces the target's last character, as in the intuition.

### 4.4 What is the recurrence? (and why each operation maps to a neighbouring cell)

| Operation | What it does at the end of the strings | What is left to solve | Cell |
|---|---|---|---|
| **Replace** `word1[i-1]` by `word2[j-1]` | the two last characters now match, both are done | `word1[0..i-1)` → `word2[0..j-1)` | `(i-1, j-1)` ↖ diagonal |
| **Delete** `word1[i-1]` | the source loses its last character; the target still needs all `j` | `word1[0..i-1)` → `word2[0..j)` | `(i-1, j)` ↑ up |
| **Insert** `word2[j-1]` after `word1`'s prefix | the inserted char matches the target's last char; the source's `i` chars are untouched | `word1[0..i)` → `word2[0..j-1)` | `(i, j-1)` ← left |

```
            ┌ dist(i-1, j-1)                                  if word1[i-1] == word2[j-1]
dist(i,j) = ┤
            └ 1 + min( dist(i-1, j-1),     // replace
                       dist(i-1, j),       // delete
                       dist(i,   j-1) )    // insert
```

`1 +` pays for the operation; `min` because we want the fewest.

A memory aid in the table:

```
                 j-1            j
   i-1    [ ↖ replace ]   [ ↑ delete ]
   i      [ ← insert  ]   [   (i,j)  ]
```

*Why is the free diagonal enough when the characters match?* Neighbouring cells never differ by more than 1: one extra character on either side costs at most one extra operation (delete it or insert it). So `dist(i-1, j-1) ≤ dist(i-1, j) + 1` and `dist(i-1, j-1) ≤ dist(i, j-1) + 1`, and the free diagonal is never worse than the delete or insert options (and obviously not worse than a paid replace).

### 4.5 What are the base cases?

- `dist(0, j) = j` — turning the **empty** prefix into `j` characters needs exactly `j` **insertions**. This is the top row: `0, 1, 2, …, m`.
- `dist(i, 0) = i` — turning `i` characters into the **empty** string needs exactly `i` **deletions**. This is the left column: `0, 1, 2, …, n`.

These are not zeros as in LCS. The base row and column carry real meaning: "how much does it cost if one side is empty?"

### 4.6 What is the order of computation?

Same dependencies as LCS (diagonal, up, left): rows `i = 1..n` top to bottom, each row `j = 1..m` left to right, after setting row 0 and column 0.

### 4.7 Can the state space be optimized?

Each row reads only itself and the previous row → **two rows** of length `m + 1` (Section 8). The only extra detail compared with LCS: column 0 of each new row is `i`, not 0.

## 5. Brute force: recursion

```java
private static int dist(String word1, String word2, int i, int j) {
    if (i == 0) return j;      // insert the remaining j characters
    if (j == 0) return i;      // delete the remaining i characters
    if (word1.charAt(i - 1) == word2.charAt(j - 1)) return dist(word1, word2, i - 1, j - 1);
    int replace = dist(word1, word2, i - 1, j - 1);
    int delete  = dist(word1, word2, i - 1, j);
    int insert  = dist(word1, word2, i, j - 1);
    return 1 + Math.min(replace, Math.min(delete, insert));
}
```

Full file: [solution/BruteForce.java](solution/BruteForce.java).

### The top of the recursion tree

`word1 = h o r s e`, `word2 = r o s`.

```
dist(5,3) 'e' vs 's' differ → 1 + min(R, D, I)
├─ R: dist(4,2) "hors"→"ro"   's' vs 'o' differ → 1 + min(dist(3,1), dist(3,2), dist(4,1)) …
├─ D: dist(4,3) "hors"→"ros"  's' = 's' → dist(3,2) "hor"→"ro" …
└─ I: dist(5,2) "horse"→"ro"  'e' vs 'o' differ → 1 + min(
          dist(4,1),
          dist(4,2)   ◄── SAME STATE as the R branch above
          dist(5,1) )
```

### Why it is slow

Every mismatch spawns **three** calls, and each shrinks the problem by only one character on one or both sides. The depth can reach `n + m`, so the worst case is around 3ⁿ⁺ᵐ calls. Two 10-letter strings with no common letters already take millions of calls.

## 6. Memoization (top-down)

The pair `(i, j)` has `(n+1) × (m+1)` possible values (6 × 4 = 24 for our example). Cache them:

```java
if (i == 0) return j;
if (j == 0) return i;
if (memo[i][j] != -1) return memo[i][j];
... compute as above ...
memo[i][j] = result;
```

Full file: [solution/Memoization.java](solution/Memoization.java).

**Cache hit:** the replace branch computes `dist(4,2) = 3` ("hors" → "ro": e.g. replace h→r, delete r, delete s). Later the insert branch `dist(5,2)` needs `dist(4,2)` again and reads the stored 3 instantly. Every state is solved once.

## 7. Tabulation (bottom-up)

```java
int[][] dp = new int[n + 1][m + 1];
for (int i = 0; i <= n; i++) dp[i][0] = i;     // delete everything
for (int j = 0; j <= m; j++) dp[0][j] = j;     // insert everything
for (int i = 1; i <= n; i++) {
    for (int j = 1; j <= m; j++) {
        if (word1.charAt(i - 1) == word2.charAt(j - 1)) dp[i][j] = dp[i - 1][j - 1];
        else dp[i][j] = 1 + Math.min(dp[i - 1][j - 1], Math.min(dp[i - 1][j], dp[i][j - 1]));
    }
}
return dp[n][m];
```

Full file: [solution/Tabulation.java](solution/Tabulation.java).

### Step 0: base row and column

```
          ""   r   o   s
   ""      0   1   2   3      ← "" → "", "r", "ro", "ros": 0..3 insertions
   h       1
   o       2
   r       3
   s       4
   e       5                  ← "h…e" → "": 1..5 deletions
```

### Row 1 (`h`)

- `(1,1)` h vs r differ: 1 + min(diag 0, up 1, left 1) = **1** (replace h→r)
- `(1,2)` h vs o differ: 1 + min(diag 1, up 2, left 1) = **2**
- `(1,3)` h vs s differ: 1 + min(diag 2, up 3, left 2) = **3**

```
   h       1   1   2   3
```

### Row 2 (`o`)

- `(2,1)` o vs r differ: 1 + min(diag 1, up 1, left 2) = **2**
- `(2,2)` o = o **match**: diag `dp[1][1]` = **1** ("ho" → "ro": replace h)
- `(2,3)` o vs s differ: 1 + min(diag 2, up 3, left 1) = **2** (from the left: insert s)

```
   o       2   2   1   2
```

### Row 3 (`r`)

- `(3,1)` r = r **match**: diag `dp[2][0]` = **2** ("hor" → "r": delete h, o)
- `(3,2)` r vs o differ: 1 + min(diag 2, up 1, left 2) = **2** (from above: delete r)
- `(3,3)` r vs s differ: 1 + min(diag 1, up 2, left 2) = **2** (replace r→s)

```
   r       3   2   2   2
```

### Row 4 (`s`)

- `(4,1)` s vs r differ: 1 + min(diag 3, up 2, left 4) = **3**
- `(4,2)` s vs o differ: 1 + min(diag 2, up 2, left 3) = **3**
- `(4,3)` s = s **match**: diag `dp[3][2]` = **2**

```
   s       4   3   3   2
```

### Row 5 (`e`)

- `(5,1)` e vs r: 1 + min(diag 4, up 3, left 5) = **4**
- `(5,2)` e vs o: 1 + min(diag 3, up 3, left 4) = **4**
- `(5,3)` e vs s: 1 + min(diag 3, up 2, left 4) = **3** (from above: delete e)

### The full table

```
          ""   r   o   s
   ""      0   1   2   3
   h       1   1   2   3
   o       2   2   1   2
   r       3   2   2   2
   s       4   3   3   2
   e       5   4   4   3     ← dp[5][3] = 3
```

### Listing the operations (walking back)

From `(n, m)`, find which neighbour produced the value, record the matching operation, and move there:

```
(5,3) e vs s: 3 = up (4,3)=2 + 1            → DELETE 'e'          → (4,3)
(4,3) s = s, 2 = diag (3,2)=2               → match (free)        → (3,2)
(3,2) r vs o: 2 = up (2,2)=1 + 1            → DELETE 'r'          → (2,2)
(2,2) o = o, 1 = diag (1,1)=1               → match (free)        → (1,1)
(1,1) h vs r: 1 = diag (0,0)=0 + 1          → REPLACE 'h' with 'r'→ (0,0)
```

Read in reverse (left to right in the word): **replace h→r, delete r, delete e** — 3 operations, as in the intuition. `Tabulation.operations(...)` does this walk; it tries "match", then "replace", then "delete", then "insert", and its test checks this exact list.

## 8. Space optimization

Row `i` reads `previous[j-1]` (diagonal), `previous[j]` (up) and `current[j-1]` (left). Keep two arrays and swap after each row:

```java
int[] previous = new int[m + 1], current = new int[m + 1];
for (int j = 0; j <= m; j++) previous[j] = j;           // row 0
for (int i = 1; i <= n; i++) {
    current[0] = i;                                      // column 0 for this row!
    for (int j = 1; j <= m; j++) {
        if (word1.charAt(i - 1) == word2.charAt(j - 1)) current[j] = previous[j - 1];
        else current[j] = 1 + Math.min(previous[j - 1], Math.min(previous[j], current[j - 1]));
    }
    int[] swap = previous; previous = current; current = swap;
}
return previous[m];
```

Full file: [solution/Optimized.java](solution/Optimized.java).

The classic bug is forgetting `current[0] = i`: the left column is not 0 here. (A single-array version with a saved `diagonal` variable, as in [LCS](../12-longest-common-subsequence/), also works.) As with LCS, the rolled arrays cannot list the operations — use the full table for that.

## 9. Dry run on a full example

The two-row version on `horse` → `ros`, showing `previous` after each row:

| after row | char | previous[0..3] | note |
|---|---|---|---|
| 0 | "" | 0 1 2 3 | base row |
| 1 | h | 1 1 2 3 | |
| 2 | o | 2 2 1 2 | match at `o` copies the diagonal 1 |
| 3 | r | 3 2 2 2 | match at `r` copies the diagonal 2 |
| 4 | s | 4 3 3 2 | match at `s` copies the diagonal 2 |
| 5 | e | 5 4 4 3 | answer `previous[3] = 3` |

Each line equals the corresponding row of the full table. All four solution files return 3, 5, 3, 3, 0 on the examples in Section 3.

## 10. Complexity analysis

| Approach | Time | Space | Why |
|---|---|---|---|
| Brute force | O(3ⁿ⁺ᵐ) | O(n + m) | Three branches per mismatch; depth ≤ n + m. |
| Memoization | O(n · m) | O(n · m) | (n+1)(m+1) states × O(1) work (three lookups). |
| Tabulation | O(n · m) | O(n · m) | One cell per pair; operation listing adds O(n + m). |
| Two rows | O(n · m) | O(m) | Same cells, two arrays. |

LeetCode: strings ≤ 500 → 250 000 cells. CSES: ≤ 5000 → 25 million cells; still fine in Java with primitive arrays, and the two-row version keeps memory at a few kilobytes instead of 100 MB.

## 11. Why this approach works

Picture any sequence of edits as an **alignment**: write the two words in columns, one character (or a gap `-`) from each word per column.

```
h  o  r  s  e        column types:
r  o  -  s  -          (x, y)  x == y → match, cost 0
R  .  D  .  D          (x, y)  x != y → replace, cost 1
                       (x, -)         → delete,  cost 1
                       (-, y)         → insert,  cost 1
```

The cost of the edits is the number of non-match columns. Now look at the **last column** of an optimal alignment of `word1[0..i)` and `word2[0..j)`. It can only be:

- `(word1[i-1], word2[j-1])` — a match (free) or a replace (cost 1); removing the column leaves an alignment of `(i-1, j-1)`;
- `(word1[i-1], -)` — a delete; removing it leaves an alignment of `(i-1, j)`;
- `(-, word2[j-1])` — an insert; removing it leaves an alignment of `(i, j-1)`.

What remains must itself be optimal for the smaller prefixes (otherwise we could improve the whole alignment). The recurrence tries exactly these three possibilities and takes the cheapest, so it computes the true minimum.

## 12. Common mistakes

1. **Base row/column set to 0 (copying LCS).** `dist(0, j) = j` and `dist(i, 0) = i`.
2. **Mixing up insert and delete.** Insert lowers `j` (target consumed), delete lowers `i` (source consumed). If you swap them the answer is still correct by symmetry, but the operation list is wrong.
3. **Adding 1 on a match.** Matching characters are free: `dp[i][j] = dp[i-1][j-1]`.
4. **Forgetting `current[0] = i` in the two-row version.**
5. **Assuming the operation list is unique.** `intention → execution` has several 5-step answers.
6. **Using LCS to compute edit distance.** `n + m − 2·LCS` is the answer when **only** insert/delete are allowed ([LC 583](https://leetcode.com/problems/delete-operation-for-two-strings/)); with replace allowed it can be smaller (`"abc"` → `"abd"`: LCS formula gives 2, edit distance is 1).

## 13. When to use this technique / how to recognize similar problems

- Two strings, and you **transform** one into the other with local operations, minimising a cost.
- For every allowed operation ask: *"which characters does it consume from each string?"* — that tells you which neighbouring cell it comes from. Disallowed operation → remove that neighbour. Different costs → change the `+1`.
- Base row/column = cost when one side is empty.

Variants:
- deletions from both strings only → `1 + min(up, left)` ([LC 583](https://leetcode.com/problems/delete-operation-for-two-strings/));
- deletion cost = ASCII value → `min(up + word1[i-1], left + word2[j-1])`, base row/column = prefix sums ([LC 712](https://leetcode.com/problems/minimum-ascii-delete-sum-for-two-strings/));
- boolean "can these two strings interleave into a third?" → same neighbours with `||` ([LC 97](https://leetcode.com/problems/interleaving-string/)).

## 14. Summary and revision notes

- State: `dist(i, j)` = min ops to turn `word1[0..i)` into `word2[0..j)`.
- Match → diagonal, free. Mismatch → `1 + min(replace ↖, delete ↑, insert ←)`.
- Base: top row `0..m` (insertions), left column `0..n` (deletions).
- Fill row by row, left to right; two rows suffice; reset column 0 each row.
- Walk back from `(n, m)` to list operations.

```
┌───────────────────────────────────────────────────────────────────┐
│ dp[i][j] = dp[i-1][j-1]                       if w1[i-1] == w2[j-1]│
│          = 1 + min( dp[i-1][j-1],   replace                        │
│                     dp[i-1][j],     delete                         │
│                     dp[i][j-1] )    insert                         │
│ dp[i][0] = i,   dp[0][j] = j,   answer dp[n][m]                    │
└───────────────────────────────────────────────────────────────────┘
```

## 15. Exercises

**Exercise 1.** Fill the table for `word1 = "sea"`, `word2 = "eat"`. What is the edit distance, and which operations achieve it?

<details>
<summary>Answer</summary>

```
         ""  e  a  t
   ""     0  1  2  3
   s      1  1  2  3
   e      2  1  2  3
   a      3  2  1  2
```
Distance 2: delete `s` ("sea" → "ea"), insert `t` ("ea" → "eat"). Walk back: (3,3) a≠t, 2 = left (3,2)=1 + 1 → insert t; (3,2) a=a → diag; (2,1) e=e → diag; (1,0) → delete s.
</details>

**Exercise 2.** Only **insertions and deletions** are allowed ([LC 583](https://leetcode.com/problems/delete-operation-for-two-strings/) — deleting from either string is equivalent). Write the recurrence and compute the answer for `"sea"` / `"eat"`.

<details>
<summary>Answer</summary>

Match → `dp[i-1][j-1]`; mismatch → `1 + min(dp[i-1][j], dp[i][j-1])` (no diagonal). Base row/column unchanged. For `"sea"` / `"eat"` the answer is 2, which equals `3 + 3 − 2·LCS = 6 − 4 = 2`.
</details>

**Exercise 3.** Suppose a replacement costs **2** while insert and delete cost 1. Show that the edit distance then always equals `n + m − 2·LCS`.

<details>
<summary>Answer</summary>

A replacement now costs the same as a delete followed by an insert, so it never helps; the problem becomes "insertions and deletions only". The characters you keep untouched form a common subsequence; to minimise work you keep the longest one (LCS) and delete the other `n − LCS` characters of `word1` and insert the other `m − LCS` characters of `word2`.
</details>

**Exercise 4.** In the two-row code, what does `current[0]` hold if you forget to set it, and which answer do you get for `word1 = "ab"`, `word2 = "b"`?

<details>
<summary>Answer</summary>

After the first swap `current` is the old row 0, so `current[0]` is 0 (and it stays 0 or stale afterwards). Row 1 (`a`): `current[0] = 0` (should be 1); `j=1` a≠b → 1 + min(prev[0]=0, prev[1]=1, current[0]=0) = 1. Row 2 (`b`): `current[0]` holds the stale 0 from row 0's array (should be 2); `j=1` b=b → `previous[0]` = 0 (should be 1). The code returns **0**, but the correct answer is **1** (delete `a`).
</details>
