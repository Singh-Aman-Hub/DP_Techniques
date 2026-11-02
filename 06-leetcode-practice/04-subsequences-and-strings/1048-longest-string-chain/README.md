# 1048. Longest String Chain

> **Pattern:** Subsequences & strings, LIS-style chain DP on words (sort + hash map) · **Difficulty:** Medium · **Builds on:** [Longest Increasing Subsequence](../../../03-subsequence-and-knapsack-dp/11-longest-increasing-subsequence/)

## Problem overview

You get a list of lowercase words. Word `A` is a **predecessor** of word `B` when inserting exactly one letter anywhere in `A` produces `B`. A chain is a sequence of words where each one is a predecessor of the next. Return the length of the longest chain you can build from the list.

## Link & metadata

| Field | Value |
|---|---|
| Problem | [1048. Longest String Chain](https://leetcode.com/problems/longest-string-chain/) |
| Difficulty | Medium |
| Pattern | Sort by length, chain DP keyed by word |
| Prerequisite topic | [03-subsequence-and-knapsack-dp/11-longest-increasing-subsequence](../../../03-subsequence-and-knapsack-dp/11-longest-increasing-subsequence/) |
| Also useful | [04-advanced-dp-patterns/08-dp-on-graphs](../../../04-advanced-dp-patterns/08-dp-on-graphs/) (this is the longest path in a DAG) |

## Examples

| words | Answer | A longest chain |
|---|---|---|
| `["a","b","ba","bca","bda","bdca"]` | 4 | `a → ba → bda → bdca` |
| `["xbc","pcxbcf","xb","cxbc","pcxbc"]` | 5 | `xb → xbc → cxbc → pcxbc → pcxbcf` |
| `["abcd","dbqca"]` | 1 | lengths differ by 1, but `dbqca` minus one letter is never `abcd` |
| `["a"]` | 1 | one word alone |

Running example: `["a","b","ba","bca","bda","bdca"]`.

## Brute-force intuition

Try every ordering or subset of the words and check whether it forms a chain. That is exponential.

Two observations lead to the DP:
1. Every step adds **exactly one** letter, so a chain always goes from shorter to longer words. **Sorting by length** gives a safe processing order, just as sorting does in [368](../368-largest-divisible-subset/).
2. This is "longest chain where each element links to the previous one", which is the LIS shape. The link test is now "is a predecessor of".

## Deriving the DP (7 questions)

### 1. What is changing?
The word that **ends** the chain.

### 2. What does the state represent?
> `best[w]` = the length of the longest chain that ends with word `w`.

Since the state is a word rather than an index, we store it in a `HashMap<String, Integer>`.

### 3. What are the choices?
Which word comes just before `w`. Searching all shorter words for a predecessor would be slow. Turn the question around instead: a predecessor of `w` must be **`w` with one letter deleted**. There are only `len(w)` such strings (at most 16), and each can be looked up in the map in O(len) time.

### 4. What is the recurrence?
```
best[w] = 1 + max( best[w without letter k]  for k in 0..len(w)-1, if that string is in the list )
best[w] = 1 if no deletion gives a word in the list
answer  = max over all w of best[w]
```

### 5. What are the base cases?
A word with no predecessor in the list forms a chain of length 1 by itself.

### 6. What is the order of computation?
`best[w]` needs words that are one letter shorter. **Sort the words by length** and process them in that order, so every predecessor is already in the map.

### 7. Can the state space be optimized?
Every word may be the predecessor of a later word, so all `n` map entries must stay. The map is the whole state, O(n · L) characters. There is no further reduction and no `Optimized.java`. Using deletions instead of comparing word pairs already cuts the time from O(n² · L) to O(n · L²).

## Recursive solution

```java
static int chainEndingAt(String word, Set<String> dictionary) {
    int best = 1;
    for (int k = 0; k < word.length(); k++) {
        String shorter = word.substring(0, k) + word.substring(k + 1);
        if (dictionary.contains(shorter))
            best = Math.max(best, 1 + chainEndingAt(shorter, dictionary));
    }
    return best;
}
```
Many long words share the same shorter predecessors, so without a cache the same words are explored many times.

## Memoization

Cache `chainEndingAt(word)` in a `HashMap`. Call it for every word and take the maximum. Recursion follows deletions, so it needs **no sorting**: the recursion order handles it. See [`solution/Memoization.java`](solution/Memoization.java).

## Tabulation

```java
Arrays.sort(sorted, Comparator.comparingInt(String::length));   // short words first
for (String word : sorted) {
    int chain = 1;
    for (int k = 0; k < word.length(); k++) {
        String shorter = word.substring(0, k) + word.substring(k + 1);
        Integer prev = best.get(shorter);                 // already computed: it is shorter
        if (prev != null) chain = Math.max(chain, prev + 1);
    }
    best.put(word, chain);
}
```
See [`solution/Tabulation.java`](solution/Tabulation.java).

## Space optimization

Not applicable (see question 7). The map is the state.

## Dry run

Words sorted by length: `a, b, ba, bca, bda, bdca`.

| word | deletions tried | found in map | best[word] |
|---|---|---|---|
| a | `""` | — | 1 |
| b | `""` | — | 1 |
| ba | `a`, `b` | a=1, b=1 | 2 |
| bca | `ca`, `ba`, `bc` | ba=2 | 3 |
| bda | `da`, `ba`, `bd` | ba=2 | 3 |
| bdca | `dca`, `bca`, `bda`, `bdc` | bca=3, bda=3 | **4** |

Answer = max = 4.

The words form a DAG (directed acyclic graph), with an edge from each predecessor to each word built from it:
```
 a ─┐
    ├─> ba ─┬─> bca ─┐
 b ─┘       └─> bda ─┴─> bdca
```
The DP computes the longest path in this DAG, visiting the nodes in a topological order (by length).

## Complexity

Let `n` = number of words and `L` = maximum word length (16).

| Approach | Time | Space | Why |
|---|---|---|---|
| Pairwise LIS | O(n² · L) | O(n) | compare every pair with a predecessor check |
| Memoization | O(n · L²) | O(n · L) | each word: L deletions, each builds and hashes an O(L) string |
| Tabulation | O(n log n + n · L²) | O(n · L) | sort, then the same per-word work |

## Why the optimized approach is better

The pairwise LIS version asks for every pair "is `A` a predecessor of `B`?". Generating the at most `L` candidates from `B` and looking them up turns an n² factor into L², and L ≤ 16 is tiny.

## Common mistakes

1. **Sorting alphabetically** instead of by length. A predecessor would then not be computed yet.
2. **Requiring the letter to be added at the end.** It can be inserted **anywhere**, so try deleting every position.
3. **Initializing the answer to 0.** For a non-empty list it is at least 1.
4. **Comparing with `==`** on strings in Java. Use `HashMap` or `equals`.

## Key takeaway

> When the state is an object (a word) and not an index, use a hash map as the DP table. When "who can precede me" is easier to **generate** than to **search for**, generate the candidates.

## Related problems

- [368. Largest Divisible Subset](../368-largest-divisible-subset/): sort, then chain.
- [673. Number of Longest Increasing Subsequence](../673-number-of-longest-increasing-subsequence/): the chain DP with counting.
- [354. Russian Doll Envelopes](../354-russian-doll-envelopes/): sort, then LIS.
- Course topic: [Longest Increasing Subsequence](../../../03-subsequence-and-knapsack-dp/11-longest-increasing-subsequence/)
