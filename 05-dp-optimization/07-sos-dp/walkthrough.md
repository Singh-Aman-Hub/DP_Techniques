# SOS DP (Sum over Subsets) — Walkthrough

> Solution files: [`NaiveSubsetSum.java`](solution/NaiveSubsetSum.java) (O(4^n)) ·
> [`SubmaskEnumeration.java`](solution/SubmaskEnumeration.java) (O(3^n)) ·
> [`SosDp.java`](solution/SosDp.java) (O(n·2^n)) ·
> [`ValidWordsForPuzzle.java`](solution/ValidWordsForPuzzle.java) (LeetCode 1178)

---

## 1. Intuition

Imagine a small shop that sells pizza toppings: **mushroom**, **olive**, **pepper**. Every possible *combination* of toppings has a number of customers who ordered exactly that combination:

```
nothing: 3   M: 1   O: 4   M+O: 1   P: 5   M+P: 9   O+P: 2   M+O+P: 6
```

Now the owner asks, for every combination S: *"If I could only stock the toppings in S, how many customers could I still serve?"* A customer can be served when everything they ordered is in S — when their combination is a **subset** of S.

For S = {M, P} the answer is `nothing + M + P + M+P = 3 + 1 + 5 + 9 = 18`.

Answering one S is easy: add up its subsets. Answering **all** S naively repeats a lot of work — the customers of "nothing" are counted again for every single S. SOS DP organizes the counting so each customer group is "passed along" one topping at a time, and every answer is ready after just n sweeps.

The everyday analogy is a **running total**. For one list you compute prefix sums left to right. Here each topping is its own tiny "list" of length 2 (absent / present), and we compute a running total along each topping, one topping after another.

---

## 2. Problem statement in our own words

You are given `n` and an array `A` of length `2^n`. Index `mask` is read as a set of the elements `0 … n-1` (bit `i` set ⇔ element `i` is in the set). Compute, for every mask,

```
F[mask] = Σ A[sub]   over all sub ⊆ mask
```

This is a classic textbook task rather than one judge problem. Afterwards we apply the idea to [LeetCode 1178 — Number of Valid Words for Each Puzzle](https://leetcode.com/problems/number-of-valid-words-for-each-puzzle/): given words and 7-letter puzzles, count for each puzzle the words that contain the puzzle's first letter and use only letters of the puzzle.

---

## 3. Examples

We use **one example for the whole lesson**, with n = 3 (bit 0 = M, bit 1 = O, bit 2 = P):

```
mask (binary):  000  001  010  011  100  101  110  111
mask (decimal):   0    1    2    3    4    5    6    7
A[mask]:          3    1    4    1    5    9    2    6
```

Expected output, computed by listing subsets:

| mask | subsets of mask | F[mask] |
|---|---|---|
| 000 | 000 | 3 |
| 001 | 000, 001 | 3+1 = **4** |
| 010 | 000, 010 | 3+4 = **7** |
| 011 | 000, 001, 010, 011 | 3+1+4+1 = **9** |
| 100 | 000, 100 | 3+5 = **8** |
| 101 | 000, 001, 100, 101 | 3+1+5+9 = **18** |
| 110 | 000, 010, 100, 110 | 3+4+5+2 = **14** |
| 111 | all eight | 3+1+4+1+5+9+2+6 = **31** |

So `F = [3, 4, 7, 9, 8, 18, 14, 31]`.

Edge cases: n = 0 → `A = [5]`, `F = [5]` (the empty set is its own only subset). n = 2 with all ones → `F = [1, 2, 2, 4]`, because a mask with c bits has 2^c subsets.

---

## 4. Deriving the solution — the 7 questions

The obvious solution has no DP at all (it just adds). The DP appears when we ask *how to share work between masks*. We derive that DP here; sections 4.8 and 4.9 then build the intuition for it before any code.

### 4.1 What is changing?

Two things: the `mask` we are answering for, and **how many bit positions we have "opened up" so far**. Call that number `i` (0 ≤ i ≤ n). Opening bit `j` means: subsets are now allowed to differ from mask at position j.

### 4.2 What does the state represent?

> `F_i[mask]` = the sum of `A[sub]` over all `sub` that are subsets of `mask` **and agree with mask on every bit from i upward**. In other words, sub may only drop bits among positions `0 … i-1`.

Check the two ends:
- `F_0[mask]`: no bit may be dropped, so sub = mask and `F_0[mask] = A[mask]`.
- `F_n[mask]`: any bit may be dropped, so this is the full answer `F[mask]`.

### 4.3 What are the choices?

When we open bit `i` (going from `F_i` to `F_{i+1}`):
- If `mask` does **not** have bit i, a subset cannot have it either. One option: nothing changes.
- If `mask` **has** bit i, a subset can **keep** bit i or **drop** it. Two options.

### 4.4 What is the recurrence?

```
F_{i+1}[mask] = F_i[mask]                              if bit i of mask is 0
F_{i+1}[mask] = F_i[mask] + F_i[mask without bit i]    if bit i of mask is 1
```

- `F_i[mask]` covers subsets that **keep** bit i (they agree with mask at bit i).
- `F_i[mask ^ (1 << i)]` covers subsets that **drop** bit i: they agree with "mask without bit i" on bits i and up, and may differ only in bits below i — exactly the definition.
- The two groups do not overlap (bit i is 1 in one, 0 in the other) and together cover everything, so adding is correct.

### 4.5 What are the base cases?

`F_0[mask] = A[mask]` for every mask — with nothing opened, the only allowed subset is mask itself.

### 4.6 What is the order of computation?

Layer `i+1` reads only layer `i`, so process `i = 0, 1, …, n-1`. Inside a layer the masks may be visited in any order (each reads the previous layer only).

### 4.7 Can the state space be optimized?

Yes — a single array suffices. In pass `i` a mask with bit i reads `mask ^ (1 << i)`, a mask **without** bit i. Masks without bit i are **never written** in pass i. So when we read them, they still hold their layer-`i` value. We can overwrite in place: `F[mask] += F[mask ^ (1 << i)]`. Memory drops from `(n+1)·2^n` to `2^n`.

### 4.8 Prerequisite: masks, submasks, and why the naive approach is 4^n

**Bit operations used in this lesson** (all on ints):

| Expression | Meaning | Example (mask = 101 = 5) |
|---|---|---|
| `1 << i` | a number with only bit i set | `1 << 2` = 100 |
| `mask & (1 << i)` | non-zero iff bit i is in mask | `5 & 4` = 4 ≠ 0 → bit 2 present |
| `mask ^ (1 << i)` | flip bit i (here: remove it) | `5 ^ 4` = 001 |
| `mask \| (1 << i)` | add bit i | `1 \| 4` = 101 |
| `(sub & mask) == sub` | sub is a submask of mask | `(4 & 5) == 4` → yes; `(2 & 5) = 0 ≠ 2` → no |

**Naive cost.** For every one of the 2^n masks, test every one of the 2^n candidates: `2^n · 2^n = 4^n` tests.

| n | 4^n |
|---|---|
| 3 | 64 |
| 10 | ≈ 10^6 |
| 20 | ≈ 10^12 (too slow) |

**Visiting only real submasks.** Most of those tests fail. If mask has `c` set bits, it has exactly `2^c` submasks (each set bit is kept or dropped). There is a neat trick that jumps from one submask to the next smaller one:

```
sub = (sub - 1) & mask
```

Why it works, on `mask = 1101` (13):
- `sub - 1` turns the lowest 1-bit of sub into 0 and all bits below it into 1. That is the "next smaller number".
- `& mask` deletes the bits that are not allowed (positions where mask is 0).
- The result is the largest submask that is smaller than sub.

```
sub = 1101   sub-1 = 1100   & 1101 = 1100
sub = 1100   sub-1 = 1011   & 1101 = 1001
sub = 1001   sub-1 = 1000   & 1101 = 1000
sub = 1000   sub-1 = 0111   & 1101 = 0101
sub = 0101   sub-1 = 0100   & 1101 = 0100
sub = 0100   sub-1 = 0011   & 1101 = 0001
sub = 0001   sub-1 = 0000   & 1101 = 0000
sub = 0000   -> stop (0 - 1 = ...1111, & mask would wrap back to 1101)
```

Exactly 8 = 2^3 submasks, none wasted. (It behaves like counting down in binary using only the "allowed" digits.)

**Why the total is 3^n.** Add up `2^popcount(mask)` over all masks. For n = 3:

| popcount c | masks with c bits | submasks each | total |
|---|---|---|---|
| 0 | 1 | 1 | 1 |
| 1 | 3 | 2 | 6 |
| 2 | 3 | 4 | 12 |
| 3 | 1 | 8 | 8 |
| | | **sum** | **27 = 3^3** |

In general the sum is `Σ_c C(n, c) · 2^c = (1 + 2)^n = 3^n` by the binomial theorem. A friendlier argument: a pair (sub, mask) with sub ⊆ mask is decided element by element, and each element has **3** states — *in neither*, *in mask only*, *in both*. So there are 3^n pairs. (It can never be "in sub only".)

3^n is much better than 4^n (for n = 20: 3.5·10^9 vs 10^12), but still too slow when we need **all** masks. That is the job of SOS DP.

### 4.9 The key property: subsets are a prefix in n dimensions

**One dimension.** A prefix sum `P[x] = A[0] + … + A[x]` sums everything "≤ x".

**Two dimensions.** For a grid, `P[r][c]` = sum of all cells with row ≤ r and column ≤ c. You can build it in two sweeps: first prefix-sum along each row, then prefix-sum along each column. After the first sweep each cell holds a sum over its row-prefix; the second sweep stacks those row-prefixes.

**Masks are grid points.** Write a 2-bit mask as (bit 1, bit 0) and place it in a 2 × 2 grid:

```
             bit0 = 0   bit0 = 1
bit1 = 0  [   00    |    01    ]
bit1 = 1  [   10    |    11    ]
```

"sub ⊆ mask" means *every coordinate of sub is ≤ the matching coordinate of mask* (a bit can be 0 ≤ 1 but not 1 ≤ 0). That is precisely "sub lies in the prefix rectangle of mask". So **F is a 2D prefix sum** on this tiny grid.

A numeric 2-bit example with `A = [1, 2, 3, 4]` (A[00]=1, A[01]=2, A[10]=3, A[11]=4):

```
start             sweep along bit 0           sweep along bit 1
[ 1 | 2 ]   ->    [ 1 | 1+2=3 ]       ->      [ 1     | 3      ]
[ 3 | 4 ]         [ 3 | 3+4=7 ]               [ 1+3=4 | 3+7=10 ]
```

Check: F[11] = 1+2+3+4 = 10 ✓, F[10] = A[00]+A[10] = 4 ✓.

**n dimensions.** With n bits, a mask is a point in an n-dimensional grid whose every side has length 2 (a cube for n = 3). Subsets of mask form the "prefix box" of that point. Just like in 2D, the n-dimensional prefix sum is built with **one sweep per dimension**, and along a side of length 2 a sweep is a single addition: "the 1-cell adds the 0-cell". That sweep is exactly the recurrence of 4.4:

```
for each bit i:                    // one dimension at a time
    for each mask with bit i = 1:  // the "1" cell along this dimension
        F[mask] += F[mask ^ (1<<i)] // ...adds its "0" neighbour
```

n sweeps × 2^n masks = **O(n · 2^n)**. For n = 20: about 2·10^7 additions.

The 3-bit cube for our example (each corner labelled mask:A):

```
              110:2 --------- 111:6
              /|              /|
            /  |            /  |
         100:5 --------- 101:9 |
           |  010:4 -------|- 011:1          bit 0 = left/right
           |  /            |  /              bit 1 = back/front (depth)
           |/              |/                bit 2 = bottom/top
         000:3 --------- 001:1
```

Pass 0 pushes values left → right, pass 1 pushes front → back, pass 2 pushes bottom → top; the full pass-by-pass table is in [section 9](#9-dry-run-on-a-full-example).

---

## 5. Brute force: test every pair

From [`NaiveSubsetSum.java`](solution/NaiveSubsetSum.java):

```java
for (int mask = 0; mask < size; mask++) {
    for (int sub = 0; sub < size; sub++) {
        if ((sub & mask) == sub) {          // every bit of sub is inside mask
            subsetSum[mask] += values[sub];
        }
    }
}
```

There is no recursion here, so instead of a recursion tree, here is the "test grid" for n = 3. Row = mask, column = sub, `✓` = subset (added), `.` = wasted test:

```
         sub: 000 001 010 011 100 101 110 111
mask 000      ✓   .   .   .   .   .   .   .
mask 001      ✓   ✓   .   .   .   .   .   .
mask 010      ✓   .   ✓   .   .   .   .   .
mask 011      ✓   ✓   ✓   ✓   .   .   .   .
mask 100      ✓   .   .   .   ✓   .   .   .
mask 101      ✓   ✓   .   .   ✓   ✓   .   .
mask 110      ✓   .   ✓   .   ✓   .   ✓   .
mask 111      ✓   ✓   ✓   ✓   ✓   ✓   ✓   ✓
```

64 tests, only 27 ✓ (that is the 3^3 from 4.8). **Why it is slow:** (1) 37 tests are wasted; (2) even the useful ones repeat: `A[000]` is added 8 separate times, once per row.

### 5.1 A smarter brute force: submask enumeration

Skipping the wasted tests gives [`SubmaskEnumeration.java`](solution/SubmaskEnumeration.java):

```java
for (int mask = 0; mask < size; mask++) {
    for (int sub = mask; ; sub = (sub - 1) & mask) {
        subsetSum[mask] += values[sub];
        if (sub == 0) break;     // 0 is the last submask
    }
}
```

Now only the 27 ✓ cells are visited. The loop must be written with the `break` at the bottom because 0 is a valid submask that must still be added; a loop like `for (sub = mask; sub > 0; ...)` would forget it.

Problem 2 — the repetition — remains: `A[000]` is still added 8 times. Sharing that work is what DP is for.

---

## 6. Memoization (top-down)

Skipped: every layer `F_{i+1}` needs **all** masks of layer `F_i`, so there is nothing to prune lazily; a top-down version would visit the same states as the table, with recursion overhead added.

---

## 7. Tabulation (bottom-up): the layered table

Before squeezing to one array, here is the honest layered DP from 4.4: a table `F[i][mask]` with `n + 1` rows.

```java
long[][] layer = new long[n + 1][size];
layer[0] = A.clone();                                  // base case: F_0 = A
for (int i = 0; i < n; i++)
    for (int mask = 0; mask < size; mask++) {
        layer[i + 1][mask] = layer[i][mask];           // subsets that keep bit i (or bit i absent)
        if ((mask & (1 << i)) != 0)
            layer[i + 1][mask] += layer[i][mask ^ (1 << i)];   // subsets that drop bit i
    }
// answer: layer[n]
```

Filling order: row by row, top to bottom; inside a row, any order. The filled table for our example is shown in section 9 — each row there is one `layer[i]`.

This is already O(n · 2^n) time; it just uses O(n · 2^n) memory, which section 8 removes.

---

## 8. Optimization: SOS DP (in place)

From [`SosDp.java`](solution/SosDp.java):

```java
long[] F = Arrays.copyOf(values, size);        // F starts as layer 0 = A
for (int bit = 0; bit < bits; bit++) {         // open one dimension at a time
    for (int mask = 0; mask < size; mask++) {
        if ((mask & (1 << bit)) != 0) {        // bit is switched on in mask
            F[mask] += F[mask ^ (1 << bit)];   // add the "bit switched off" neighbour
        }
    }
}
```

**Which cells are actually needed?** In pass `bit`, a write goes to a mask **with** the bit, and a read comes from a mask **without** it. A cell without the bit is never written during this pass, so it still holds the previous layer's value when read — the in-place update reproduces the layered table exactly (see 4.7).

**Loop order matters only between passes.** The outer loop must be over bits. If you swap the loops (masks outside, bits inside), a mask may read a neighbour that has not finished all of its passes yet — the result is wrong. (Try it on the example with masks in increasing order: F[010] becomes 7 and F[001] becomes 4 first; then F[011] = 1 + F[010] + F[001] = 1 + 7 + 4 = 12 instead of 9, because A[000] = 3 arrives twice — once through 010 and once through 001.)

### 8.1 The mirrored version: sums over supermasks

Sometimes you need `G[mask] = Σ A[sup]` over all `sup ⊇ mask`. Flip the direction of each sweep: a mask **without** the bit collects from the mask **with** it.

```java
if ((mask & (1 << bit)) == 0) G[mask] += G[mask | (1 << bit)];
```

This is `supersetSums` in `SosDp.java` (see exercise 1).

### 8.2 Application: LeetCode 1178 — Number of Valid Words for Each Puzzle

**Summary in our own words.** You get a list of words and a list of puzzles. Each puzzle has 7 distinct letters. A word is *valid* for a puzzle when it contains the puzzle's first letter and every letter of the word appears in the puzzle. Return, for each puzzle, how many words are valid. ([problem link](https://leetcode.com/problems/number-of-valid-words-for-each-puzzle/))

**Reduce to masks.** A word only matters through its letter set: `"cab"` and `"abc"` and `"abca"` are the same mask `0b111`. So:

1. Count words per mask: `wordsWithMask[m]`.
2. For a puzzle with first letter `f` and other letters `R` (6 letters), a valid word mask is `{f} ∪ S` for some `S ⊆ R`.
3. Answer = `Σ wordsWithMask[{f} ∪ S]` over the `2^6 = 64` submasks S of R.

Step 3 is "sum over subsets" for one query mask — submask enumeration is perfect: only 64 lookups per puzzle. (A full SOS table over 26 bits would need 26·2^26 ≈ 1.7·10^9 operations — far worse here. Use full SOS when you need **every** mask and n ≤ ~22; use submask enumeration for a few small query masks.)

**Example** (used in the tests). Letters: a = bit 0, b = bit 1, c = bit 2, d = bit 3.

```
words:  "ab"  "ba"  "abc"  "cab"  "d"   "aaa"
masks:  0011  0011  0111   0111   1000  0001
wordsWithMask = { 0011: 2, 0111: 2, 1000: 1, 0001: 1 }
```

| puzzle | first letter | masks found among `{first} ∪ S` | answer |
|---|---|---|---|
| abcdefg | a | 0001 (1) + 0011 (2) + 0111 (2) | **5** |
| bacdefg | b | 0011 (2) + 0111 (2) | **4** |
| gabcdef | g | none contains g | **0** |
| dabcefg | d | 1000 (1) | **1** |

For "bacdefg": first bit = b; R = {a, c, d, e, f, g}. Of the 64 submasks S, only S = {a} gives `{a,b}` = 0011 (count 2) and S = {a, c} gives 0111 (count 2). All other lookups return 0. Total 4.

Code core from [`ValidWordsForPuzzle.java`](solution/ValidWordsForPuzzle.java):

```java
int firstBit = 1 << (puzzle.charAt(0) - 'a');
int otherLetters = letterMask(puzzle.substring(1)) & ~firstBit;
int total = 0;
for (int sub = otherLetters; ; sub = (sub - 1) & otherLetters) {
    total += wordsWithMask.getOrDefault(sub | firstBit, 0);   // force the first letter in
    if (sub == 0) break;
}
```

The file cross-checks against a brute force that tests every word against every puzzle on 300 random cases (seed 1178).

---

## 9. Dry run on a full example

`A = [3, 1, 4, 1, 5, 9, 2, 6]`, n = 3. Each row is the array after a pass; **bold** cells changed in that pass. Arrows show what was added.

**Pass 0 (bit 0, value 1)** — masks 001, 011, 101, 111 add their neighbour with bit 0 cleared:

```
F[001] += F[000]:  1 + 3 = 4
F[011] += F[010]:  1 + 4 = 5
F[101] += F[100]:  9 + 5 = 14
F[111] += F[110]:  6 + 2 = 8
```

**Pass 1 (bit 1, value 2)** — masks 010, 011, 110, 111:

```
F[010] += F[000]:  4 + 3  = 7
F[011] += F[001]:  5 + 4  = 9
F[110] += F[100]:  2 + 5  = 7
F[111] += F[101]:  8 + 14 = 22
```

**Pass 2 (bit 2, value 4)** — masks 100, 101, 110, 111:

```
F[100] += F[000]:  5  + 3 = 8
F[101] += F[001]:  14 + 4 = 18
F[110] += F[010]:  7  + 7 = 14
F[111] += F[011]:  22 + 9 = 31
```

| after | 000 | 001 | 010 | 011 | 100 | 101 | 110 | 111 |
|---|---|---|---|---|---|---|---|---|
| start (F_0 = A) | 3 | 1 | 4 | 1 | 5 | 9 | 2 | 6 |
| pass 0 (F_1) | 3 | **4** | 4 | **5** | 5 | **14** | 2 | **8** |
| pass 1 (F_2) | 3 | 4 | **7** | **9** | 5 | 14 | **7** | **22** |
| pass 2 (F_3) | 3 | 4 | 7 | 9 | **8** | **18** | **14** | **31** |

The last row equals the expected `[3, 4, 7, 9, 8, 18, 14, 31]` from section 3. ✓

Check the invariant on one cell: after pass 0, `F_1[101] = 14`. By definition it sums subsets of 101 that may drop only bit 0: {101, 100} → 9 + 5 = 14 ✓. After pass 1 the value is unchanged (101 has no bit 1, so there is nothing new to drop). After pass 2 it may also drop bit 2: adds {001, 000} → 4, total 18 ✓.

**How A[000] = 3 reaches F[111].** In the naive method it is added directly. In SOS DP it travels along a path of the cube: 000 → 001 (pass 0) → 011 (pass 1) → 111 (pass 2). Every submask reaches every supermask along **exactly one** path, because the path flips the missing bits in increasing order. That is why nothing is double-counted.

```
pass 0         pass 1          pass 2
000 ──► 001 ──────► 011 ─────────► 111
```

---

## 10. Complexity analysis

| Approach | Time | Why | Extra space |
|---|---|---|---|
| Naive pair test | O(4^n) | 2^n masks × 2^n candidates, each an O(1) test | O(2^n) answers |
| Submask enumeration | O(3^n) | mask with c bits visits 2^c submasks; Σ C(n,c)·2^c = 3^n | O(2^n) |
| Layered SOS table | O(n·2^n) | n layers × 2^n masks × O(1) | O(n·2^n) |
| In-place SOS DP | O(n·2^n) | same work, one array | O(2^n) |
| LC 1178 | O(W·L + P·64) | each word scanned once; each puzzle does 64 hash lookups | O(W) map |

Concrete numbers for n = 20: 4^n ≈ 1.1·10^12, 3^n ≈ 3.5·10^9, n·2^n ≈ 2.1·10^7.

---

## 11. Why this approach works

- **Grouping by the first opened bit.** Every subset of mask differs from mask in some set of positions D. The invariant `F_i` collects exactly the subsets whose D lies inside {0, …, i-1}. Opening bit i splits the new candidates by whether i ∈ D, and the two halves are exactly `F_i[mask]` and `F_i[mask without i]`. Induction on i gives `F_n = F`.
- **No double counting.** The two halves disagree at bit i, so they are disjoint.
- **In-place is safe.** Reads in pass i only touch masks without bit i, which pass i never writes.
- **Submask trick is complete.** `(sub - 1) & mask` produces the largest submask smaller than sub, so starting at mask and stopping after 0 visits every submask once, in decreasing order.

---

## 12. Common mistakes

1. **Swapping the loops.** The bit loop must be outside. With masks outside, a mask reads neighbours that have not finished their passes.
2. **Forgetting submask 0.** `for (sub = mask; sub > 0; sub = (sub-1) & mask)` skips the empty subset. Use the `if (sub == 0) break;` pattern.
3. **Infinite loop at 0.** Continuing after `sub = 0` computes `(-1) & mask = mask` and starts over forever.
4. **Wrong direction for supermasks.** Subset sums: the 1-cell adds the 0-cell. Superset sums: the 0-cell adds the 1-cell.
5. **Using `int` for sums.** With 2^20 values up to 10^9, sums overflow `int`; use `long` (and a modulus if the problem asks).
6. **Running full SOS on 26 bits.** In LC 1178, 26·2^26 operations plus a 2^26 `long` array (512 MB) is far too much. The query masks are tiny, so enumerate their submasks instead.
7. **Operator precedence.** In Java, `mask & 1 << i == 0` parses as `mask & ((1 << i) == 0)` — a compile error. Always parenthesize: `(mask & (1 << i)) == 0`.

---

## 13. When to use this technique / how to recognize similar problems

Look for these signals:
- The data is indexed by **sets** of a small universe (n ≤ ~20–22 for full SOS, any n for a 2^7-sized query).
- The question is "for each set, aggregate over all **subsets** (or supersets)".
- The aggregation is associative and has a "sum" flavour: `+`, `max`, `min`, `or`, counts.
- Pair conditions like `x & y == x` (x ⊆ y) or `x & y == 0` (y ⊆ complement of x).

Decision guide:

```
need answers for ALL 2^n masks?
  ├─ yes ─► n ≤ ~22 ─► SOS DP, O(n·2^n)
  └─ no, only for some query masks with few bits ─► submask enumeration per query
need every (mask, submask) PAIR individually (e.g. bitmask DP that splits a set)?
  └─► submask enumeration, O(3^n) — SOS cannot help because the pair matters, not a sum
```

---

## 14. Summary and revision notes

- Naive: test all pairs, **4^n**.
- Submask walk `sub = (sub - 1) & mask`, stop after 0: **3^n** total (each element: in neither / mask only / both).
- SOS DP = **n-dimensional prefix sum** over a 2 × 2 × … × 2 grid; one sweep per bit: **O(n·2^n)**.
- Bit loop outside, mask loop inside; in place is safe.
- Supermask sums: flip the direction.
- For a few small query masks (LC 1178), plain submask enumeration wins.

```
┌──────────────────────────────────────────────────────────────┐
│  F = A                                                        │
│  for i in 0..n-1:                                             │
│      for mask in 0..2^n-1:                                    │
│          if mask has bit i:  F[mask] += F[mask ^ (1 << i)]    │
│  // now F[mask] = Σ A[sub] over all sub ⊆ mask               │
└──────────────────────────────────────────────────────────────┘
```

---

## 15. Exercises

**Exercise 1.** Compute the superset sums `G[mask] = Σ A[sup]` over `sup ⊇ mask` for our example `A = [3, 1, 4, 1, 5, 9, 2, 6]`.

<details><summary>Answer</summary>

Run the mirrored sweep (a mask without bit i adds the mask with bit i), or list directly:

| mask | supermasks | G |
|---|---|---|
| 000 | all | 31 |
| 001 | 001, 011, 101, 111 | 1+1+9+6 = 17 |
| 010 | 010, 011, 110, 111 | 4+1+2+6 = 13 |
| 011 | 011, 111 | 1+6 = 7 |
| 100 | 100, 101, 110, 111 | 5+9+2+6 = 22 |
| 101 | 101, 111 | 9+6 = 15 |
| 110 | 110, 111 | 2+6 = 8 |
| 111 | 111 | 6 |

`G = [31, 17, 13, 7, 22, 15, 8, 6]` — this is checked in `SosDp.java`.
</details>

**Exercise 2.** List the submasks of `mask = 10110` (22) in the order the trick `(sub - 1) & mask` produces them. How many are there?

<details><summary>Answer</summary>

mask has 3 set bits, so 2^3 = 8 submasks:
`10110 (22), 10100 (20), 10010 (18), 10000 (16), 00110 (6), 00100 (4), 00010 (2), 00000 (0)`.
Example step: `22 - 1 = 10101`, `& 10110 = 10100` = 20.
</details>

**Exercise 3.** Given an array `nums` of numbers below 2^20, count for each `x` in `nums` how many `y` in `nums` satisfy `x & y == y`. How do you use SOS DP?

<details><summary>Answer</summary>

`x & y == y` means y ⊆ x. Let `A[mask]` = how many times mask appears in nums. Run SOS DP (subset sums) once, O(20·2^20). Then the answer for x is just `F[x]`. Total ≈ 2·10^7 operations instead of O(N²) pair tests.
</details>

**Exercise 4.** Replace `+=` with `max`. What does `F[mask]` mean now, and does the in-place argument still hold?

<details><summary>Answer</summary>

`F[mask] = max A[sub]` over all sub ⊆ mask. Yes: the argument never used anything special about addition except that the two halves (keep bit / drop bit) together cover all subsets. Max is even more forgiving: overlapping halves would not hurt it. (Subtraction-based tricks, like inclusion–exclusion inverses, do need the disjointness.)
</details>

---

⬅ Back to [topic README](README.md)
