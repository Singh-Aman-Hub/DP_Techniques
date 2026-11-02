# 1178. Number of Valid Words for Each Puzzle

> **Pattern:** Bitmask compression + submask enumeration (a sum-over-subsets query) · **Builds on:** [SOS DP](../../../05-dp-optimization/07-sos-dp/), [Bitmask DP](../../../04-advanced-dp-patterns/09-bitmask-dp/) · **Difficulty:** Hard

## Problem overview

You get up to 10⁵ words and up to 10⁴ puzzles. Every puzzle has exactly 7 **distinct** letters. A
word is valid for a puzzle when (1) the word contains the puzzle's **first** letter and (2) every
letter of the word appears in the puzzle. For each puzzle, return how many words are valid.

Checking every (word, puzzle) pair is 10⁹ checks. The two ideas that fix it: a word is just a
**set of letters** (a 26-bit mask), and a 7-letter puzzle has only **64** subsets that contain its
first letter.

## Link & metadata

| Field | Value |
|---|---|
| LeetCode | [1178. Number of Valid Words for Each Puzzle](https://leetcode.com/problems/number-of-valid-words-for-each-puzzle/) |
| Difficulty | Hard |
| Pattern | Count words per letter-set mask, then sum the counts over the subsets of each puzzle mask |
| Prerequisite topic | [`05-dp-optimization/07-sos-dp`](../../../05-dp-optimization/07-sos-dp/) (sum over subsets), [`04-advanced-dp-patterns/09-bitmask-dp`](../../../04-advanced-dp-patterns/09-bitmask-dp/) (sets as integers) |
| Why this pattern | "Is set A inside set B?" asked for many pairs = a subset-sum query over masks |

## Examples

| words | puzzles | Answer |
|---|---|---|
| aaaa, asas, able, ability, actt, actor, access | aboveyz, abrodyz, abslute, absoryz, actresz, gaswxyz | [1, 1, 3, 2, 4, 0] |
| apple, pleas, please | aelwxyz, aelpxyz, aelpsxy, saelpxy, xaelpsy | [0, 1, 3, 2, 0] |
| a | abcdefg, bacdefg | [1, 0] (the second puzzle's first letter is b) |

Example 2 explained: `apple` uses {a,e,l,p}; `pleas` and `please` both use {a,e,l,p,s}.
`aelpsxy` contains both sets and starts with `a` → 3. `saelpxy` must have `s` in the word → only the
two {a,e,l,p,s} words → 2. `xaelpsy` needs an `x` → 0.

## Brute-force intuition

For every puzzle, test every word: `wordMask ⊆ puzzleMask` is one AND
(`(wordMask & ~puzzleMask) == 0`), plus a check for the first letter. That is `W × P = 10⁹`
operations: too slow.

Flip the direction. Instead of asking "which words fit this puzzle?" by scanning words, **list the
letter sets that would fit** and look each one up:

```
valid sets for a puzzle = { firstLetter } ∪ S,   S any subset of the other 6 letters  → 2⁶ = 64 sets
```

If we know how many words have each exact letter set, the answer is the sum of 64 lookups.

## Deriving the DP (7 questions)

### 1. What is changing?
The subset `S` of the puzzle's other six letters that a word may use.

### 2. What does the state represent?
`count[m]` = the number of words whose set of letters is **exactly** mask `m`
(bit `c` = letter `'a' + c`). The answer for a puzzle with first-letter bit `f` and other letters
`R` is

```
answer(puzzle) = Σ over S ⊆ R of count[f | S]
```

This is a **sum over subsets** (SOS) query, which the SOS DP topic computes for all masks at once.
Here the universe has 26 letters (2²⁶ masks: too many to tabulate) but each query has only 64
subsets, so we enumerate them directly.

### 3. What are the choices?
For each of the 6 non-first letters: in the word, or not.

### 4. What is the recurrence?
Generating the subsets of `R` in decreasing order uses the standard step

```
sub = R;  repeat: use sub;  if sub == 0 stop;  sub = (sub − 1) & R
```

Why it works: `sub − 1` clears the lowest set bit of `sub` and sets all bits below it; `& R` keeps
only bits that belong to `R`. The result is the next smaller subset of `R`, so every subset is
visited exactly once.

### 5. What are the base cases?
The empty subset `sub = 0` still counts: it corresponds to words made only of the first letter
(like `aaaa`). The loop handles it before stopping.

### 6. What is the order of computation?
First build `count[]` from all words (one pass). Then answer puzzles independently in any order.

### 7. Can the state space be optimized?
Store `count` in a hash map, not a 2²⁶ array: at most one entry per distinct word mask. Words with
more than 7 distinct letters can never fit a puzzle; skip them.

## Recursive solution

The 64 subsets can also be generated recursively, letter by letter (take / skip), which is the
same decision tree as the subsets topic:

```java
int sumSubsets(int[] otherLetters, int idx, int mask) {
    if (idx == otherLetters.length) return count.getOrDefault(mask, 0);
    return sumSubsets(otherLetters, idx + 1, mask)                              // skip letter
         + sumSubsets(otherLetters, idx + 1, mask | (1 << otherLetters[idx]));  // take letter
}
// answer = sumSubsets(otherSix, 0, firstBit)
```

## Memoization

Nothing repeats inside one puzzle (each subset is visited once). Across puzzles, the shared work
is already captured by `count[]`, which is computed once and reused by all puzzles: that table
**is** the cached part. So there is no separate memoized file.

## Tabulation

[`solution/Optimized.java`](solution/Optimized.java) contains both versions:
- `findNumOfValidWordsSlow`: every (word, puzzle) pair, `O(W · P)`;
- `findNumOfValidWordsFast`: `count` map + the `(sub − 1) & rest` loop, `O(total letters + 64 P)`.

`main` checks both on the examples and then compares them on 300 random inputs.

## Space optimization

The hash map holds at most `W` entries (fewer when many words share a letter set, like `pleas`
and `please`). A 2²⁶-entry `int` array would cost 256 MB.

## Dry run

Words → masks: `apple` → {a,e,l,p}: 1 word; `pleas`, `please` → {a,e,l,p,s}: 2 words.

Puzzle `aelpsxy`: first = a, rest R = {e,l,p,s,x,y} (64 subsets). Only the subsets that appear in
the map matter:

| sub (⊆ R) | looked-up mask `a ∪ sub` | count |
|---|---|---|
| {e,l,p,s,x,y} | {a,e,l,p,s,x,y} | 0 |
| … (subsets containing x or y) | … | 0 |
| {e,l,p,s} | {a,e,l,p,s} | 2 |
| {e,l,p} | {a,e,l,p} | 1 |
| … (other subsets) | … | 0 |
| {} | {a} | 0 |
| **total** | | **3** |

Puzzle `saelpxy`: first = s, rest = {a,e,l,p,x,y}. `{s} ∪ {a,e,l,p}` = {a,e,l,p,s} → 2, and no
other subset is in the map → **2**. The word `apple` is never looked up because its mask lacks `s`.

Submask stepping on a small `R` = {b, d} (bits 1 and 3, `R = 1010₂`):

```
sub = 1010 → (1010 − 1) & 1010 = 1001 & 1010 = 1000 → (0111) & 1010 = 0010 → (0001) & 1010 = 0000 → stop
       {b,d}                          {d}                         {b}                        {}
```

## Complexity

| Approach | Time | Space | Reason |
|---|---|---|---|
| Pairwise | O(W · P + total letters) | O(W) | 10⁵ × 10⁴ = 10⁹ checks |
| Mask counts + submasks | O(total letters + 64 · P) | O(W) | 64 hash lookups per puzzle |
| SOS DP over all 2²⁶ masks | O(26 · 2²⁶) | O(2²⁶) | correct, but ~1.7·10⁹ steps and 256 MB: not worth it here |

## Why the optimized approach is better

`64 × 10⁴ = 640 000` lookups instead of 10⁹ comparisons. The general lesson from SOS DP applies:
when a query is "sum over all subsets of a mask", either enumerate the subsets (small masks) or
precompute SOS for every mask (small universe). Here the masks are small (7 bits), so enumeration
wins.

## Common mistakes

- **Forgetting the empty subset** (words like `aaaa` made only of the first letter).
- **Enumerating subsets of the full puzzle** (128) and then checking the first letter: it works but
  doubles the work; force the first bit instead.
- **Counting letters with multiplicity.** Only the set matters: `asas` and `as` have the same mask.
- **Infinite loop** in `for (sub = R; sub >= 0; sub = (sub − 1) & R)`: `(0 − 1) & R = R` again. Break
  explicitly after handling `sub = 0`.

## Key takeaway

Compress objects to bitmasks when only membership matters, then turn "is X a subset of Y?" into
"sum of counts over the subsets of Y". That query is what SOS DP is built for.

## Related problems

- [2172. Maximum AND Sum of Array](../2172-maximum-and-sum-of-array/): bitmask DP where the mask is the state.
- [1879. Minimum XOR Sum of Two Arrays](https://leetcode.com/problems/minimum-xor-sum-of-two-arrays/): letters-as-bits thinking applied to an assignment DP.
- The [SOS DP topic](../../../05-dp-optimization/07-sos-dp/): how to answer the same subset-sum query for every mask at once.
