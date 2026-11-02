# 139. Word Break

> **Pattern:** 1D boolean prefix DP ("can the first i characters be split?") · **Builds on:** [Decode Ways](../../../01-one-dimensional-dp/08-decode-ways/) · **Difficulty:** Medium

## Problem overview

Given a string `s` and a list of dictionary words, decide whether `s` can be cut into pieces so
that every piece is a dictionary word. Words may be reused any number of times.

## Link & metadata

| Field | Value |
|---|---|
| LeetCode | [139. Word Break](https://leetcode.com/problems/word-break/) |
| Difficulty | Medium |
| Pattern | 1D prefix DP over a string, where the last "piece" has variable length |
| Prerequisite topic | [`01-one-dimensional-dp/08-decode-ways`](../../../01-one-dimensional-dp/08-decode-ways/) |
| Why this pattern | Decode Ways asks "how many ways to cut a string into valid 1- or 2-char pieces"; Word Break asks "is there any way to cut it into valid pieces of any length" |

## Examples

| s | wordDict | Answer | Why |
|---|---|---|---|
| `"leetcode"` | `[leet, code]` | true | leet + code |
| `"applepenapple"` | `[apple, pen]` | true | apple + pen + apple (reuse allowed) |
| `"catsandog"` | `[cats, dog, sand, and, cat]` | false | "cats"+"and" leaves "og"; "cat"+"sand" leaves "og" |
| `"a"` | `[b]` | false | nothing matches |

## Brute-force intuition

Try every possible first word: for each prefix of `s` that is in the dictionary, recurse on the
rest. For a string like `"aaaa…ab"` with dictionary `[a, aa, aaa]`, the number of ways to split
the `a`s grows exponentially, and every one of them fails at the final `b`. But the recursion only
ever asks one question, *"can the suffix starting at index k be split?"*, and there are only
`n + 1` possible values of `k`.

```
canSplit(0)  "aaab"
 ├─ "a"   → canSplit(1)
 │          ├─ "a"  → canSplit(2) ─ ...
 │          └─ "aa" → canSplit(3)        ← asked again below
 ├─ "aa"  → canSplit(2)                  ← repeat
 └─ "aaa" → canSplit(3)                  ← repeat
```

## Deriving the DP (7 questions)

### 1. What is changing?
How much of the string has already been split: a position `i` from 0 to `n`.

### 2. What does the state represent?
`dp[i]` = `true` if the first `i` characters `s[0..i)` can be split entirely into dictionary
words.

### 3. What are the choices?
The **last word** of the split of `s[0..i)` starts at some position `j < i`. Every `j` is a
candidate: the last word is `s[j..i)`.

### 4. What is the recurrence?
```
dp[i] = OR over j in [0, i) of ( dp[j]  AND  s[j..i) is in the dictionary )
```
`dp[j]` says the part before the last word is fine; the dictionary check says the last word is
fine. We need **any** such `j`, hence OR.

### 5. What are the base cases?
`dp[0] = true`: the empty prefix is trivially split (into zero words). Without it, no first word
could ever be accepted.

### 6. What is the order of computation?
`dp[i]` reads `dp[j]` for `j < i`, so loop `i` from 1 to `n`. The answer is `dp[n]`.

### 7. Can the state space be optimized?
Not in the number of states: `dp[i]` may need any earlier `dp[j]`. But the **work** can be cut:
the last word is at most `maxWordLength` long, so only `j ≥ i - maxWordLength` can succeed. That
turns O(n²) substring checks into O(n · L).

## Recursive solution

```java
static boolean canSplit(int start) {               // can s[start..] be split?
    if (start == s.length()) return true;
    for (int end = start + 1; end <= s.length(); end++)
        if (dict.contains(s.substring(start, end)) && canSplit(end)) return true;
    return false;
}
```

## Memoization

Cache `canSplit(start)` in a `Boolean[]` (`null` = unknown). See
[`solution/Memoization.java`](solution/Memoization.java). This is the suffix view; tabulation
below uses the prefix view. Both have `n + 1` states.

## Tabulation

See [`solution/Tabulation.java`](solution/Tabulation.java).

```java
dp[0] = true;
for (int i = 1; i <= n; i++)
    for (int j = 0; j < i; j++)
        if (dp[j] && dict.contains(s.substring(j, i))) { dp[i] = true; break; }
```

The `break` is safe: once one valid split is found, `dp[i]` cannot become "more true".

## Space optimization

The dp array stays, but `j` only runs over `[i - maxLen, i)`. See
[`solution/Optimized.java`](solution/Optimized.java).

## Dry run

`s = "leetcode"`, dictionary `{leet, code}`, `maxLen = 4`. Index `i` means "first i chars".

| i | prefix | successful j (dp[j] true and s[j..i) a word) | dp[i] |
|---|---|---|---|
| 0 | "" | base | T |
| 1 | l | none | F |
| 2 | le | none | F |
| 3 | lee | none | F |
| 4 | leet | j = 0: dp[0] = T, "leet" ✓ | T |
| 5 | leetc | none | F |
| 6 | leetco | none | F |
| 7 | leetcod | none | F |
| 8 | leetcode | j = 4: dp[4] = T, "code" ✓ | **T** |

## Complexity

Let `n = |s|`, `L` = longest dictionary word, `W` = number of words.

| Approach | Time | Space | Reason |
|---|---|---|---|
| Brute force | O(2ⁿ · n) | O(n) | each of the n-1 gaps is "cut" or "not cut" |
| Memoization | O(n³) | O(n + W·L) | n states × n end positions × O(n) substring/hash |
| Tabulation | O(n³) | O(n + W·L) | same pairs (j, i), each costing a substring |
| Optimized | O(n · L²) | O(n + W·L) | only L choices of j per i, each substring O(L) |

The extra factor in each row is the substring creation and hashing, which costs its length.

## Why the optimized approach is better

With `n = 300` and `L = 20`, the inner loop drops from up to 300 to at most 20 iterations.
In general it removes work that can never succeed because no word is that long.

## Common mistakes

- **Greedy matching** (always take the longest/first matching word). `"catsandog"` style inputs
  break it, and so does `"aaaaaaa"` with `[aaaa, aaa]`.
- **Forgetting `dp[0] = true`.** Then nothing is ever true.
- **Using a `List` for the dictionary.** `contains` becomes O(W); use a `HashSet`.

## Key takeaway

For "can this string be cut into valid pieces?", let the state be a prefix length and let the
choice be **where the last piece starts**. Bounding the piece length bounds the work.

## Related problems

- [91. Decode Ways](https://leetcode.com/problems/decode-ways/), the counting cousin with pieces of length 1 or 2
- [279. Perfect Squares](../279-perfect-squares/), the same "choose the last piece" shape with numbers instead of strings
- Previous: [983. Minimum Cost For Tickets](../983-minimum-cost-for-tickets/) · Next: [279. Perfect Squares](../279-perfect-squares/)
