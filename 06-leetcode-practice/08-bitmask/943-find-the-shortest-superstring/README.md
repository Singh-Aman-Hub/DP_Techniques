# 943. Find the Shortest Superstring

> **Pattern:** TSP (Held–Karp) over words with "overlap" edge weights + path reconstruction · **Difficulty:** Hard
> **Builds on:** [Traveling Salesperson Problem](../../../04-advanced-dp-patterns/10-traveling-salesperson-problem/) and [Bitmask DP](../../../04-advanced-dp-patterns/09-bitmask-dp/)

---

## Problem overview

Given up to 12 distinct words (none is a substring of another), build the **shortest** string that contains every word as a substring, and return it (any shortest one is accepted).

**Why it is TSP.** In a shortest superstring the words appear in some order, and each word overlaps the previous one as much as possible. If word `j` follows word `i`, we save `overlap[i][j]` characters, where `overlap[i][j]` = the longest suffix of `i` that is also a prefix of `j`. So: **total length = Σ lengths − Σ overlaps along the order**. Minimising length = finding the order (a path visiting every word once) with the **maximum total overlap** — a path version of the [Traveling Salesperson Problem](../../../04-advanced-dp-patterns/10-traveling-salesperson-problem/), solved with the `(visited mask, last word)` state from [Bitmask DP](../../../04-advanced-dp-patterns/09-bitmask-dp/). The extra twist is that we must return the string, so we need **parent pointers**.

## Link & metadata

| Field | Value |
|---|---|
| LeetCode | [943. Find the Shortest Superstring](https://leetcode.com/problems/find-the-shortest-superstring/) |
| Difficulty | Hard |
| Pattern | Held–Karp path TSP maximising overlap; reconstruct the order |
| Prerequisite topics | [Traveling Salesperson Problem](../../../04-advanced-dp-patterns/10-traveling-salesperson-problem/), [Bitmask DP](../../../04-advanced-dp-patterns/09-bitmask-dp/), [Shortest Common Supersequence](../../../03-subsequence-and-knapsack-dp/16-shortest-common-supersequence/) (rebuilding a string from a DP) |

## Examples

| words | one answer | length |
|---|---|---|
| ["alex","loves","leetcode"] | "alexlovesleetcode" | 17 (no overlaps exist) |
| ["catg","ctaagt","gcta","ttca","atgcatc"] | "gctaagttcatgcatc" | 16 |
| ["ab","bc","cd"] | "abcd" | 4 |
| ["abc"] | "abc" | 3 (edge case) |

## Brute-force intuition

Try all `n!` orders (`12! ≈ 4.8·10^8`) and glue each with maximum overlaps. The best way to finish the string depends only on **which words are already used** and **which word is last** (its suffix decides the next overlap) — not on the order of the earlier words.

## Deriving the DP (7 questions)

### 1. What is changing?
The set of words already placed and the last word placed.

### 2. What does the state represent?
`gain[mask][last]` = the maximum total overlap of an ordering of exactly the words in `mask` that **ends with** word `last`.

### 3. What are the choices?
Which word `prev` (in `mask`, not `last`) came just before `last`.

### 4. What is the recurrence?
```
gain[mask][last] = max over prev in mask \ {last} of gain[mask \ {last}][prev] + overlap[prev][last]
```
At the end: pick `last` maximising `gain[full][last]`, and the answer length is `Σ len − that gain`.

### 5. What are the base cases?
`gain[{i}][i] = 0` — a single word overlaps nothing.

### 6. What is the order of computation?
`mask \ {last}` is a smaller number than `mask` → iterate masks increasing.

### 7. Can the state space be optimized?
`12 · 4096` states are needed **plus** a parent table `parent[mask][last] = prev` to rebuild the order, so nothing can be dropped. No `Optimized.java`.

## Recursive solution

Forward form (top-down): `extra(mask, last)` = max overlap obtainable by appending all remaining words after `last`.
```java
int extra(int mask, int last) {
    if (mask == full) return 0;
    int best = 0;
    for (int next = 0; next < n; next++)
        if ((mask & (1 << next)) == 0)
            best = Math.max(best, overlap[last][next] + extra(mask | (1 << next), next));
    return best;
}
```

## Memoization

Cache `extra(mask, last)` and remember the best `next` in `choice[mask][last]`. Rebuild by starting from the best first word and following `choice`. See [`solution/Memoization.java`](solution/Memoization.java).

## Tabulation

Fill `gain[mask][last]` with `parent[mask][last]`, then walk back from the best `last` of the full mask, collecting the order in reverse:
```
order = []; mask = full; cur = bestLast
while cur != -1: order.addFirst(cur); prev = parent[mask][cur]; mask ^= bit(cur); cur = prev
string = order[0] + for each next word: word.substring(overlap[previous][word])
```
See [`solution/Tabulation.java`](solution/Tabulation.java).

## Space optimization

Not applicable — parents are needed for reconstruction.

## Dry run

`words = ["ab","bc","cd"]` (indices 0, 1, 2). Overlaps: `ab→bc = 1`, `bc→cd = 1`, every other pair 0.

| mask | last | best prev | `gain` |
|---|---|---|---|
| {0} / {1} / {2} | itself | – | 0 |
| {0,1} | 1 | 0 | 0 + overlap(ab,bc) = 1 |
| {0,1} | 0 | 1 | 0 + overlap(bc,ab) = 0 |
| {1,2} | 2 | 1 | 1 |
| {0,2} | 0 or 2 | – | 0 |
| {0,1,2} | 2 | 1 | `gain[{0,1}][1]` + overlap(bc,cd) = 1 + 1 = **2** |
| {0,1,2} | 0 or 1 | … | ≤ 1 |

Best ending: `last = 2`, gain 2. Walk back: `2 ← 1 ← 0` → order `ab, bc, cd` → `"ab" + "c" + "d" = "abcd"`, length `6 − 2 = 4`.

## Complexity

| Approach | Time | Space | Why |
|---|---|---|---|
| All orders | O(n!·L) | O(n·L) | L = word length for gluing |
| Overlap table | O(n²·L²) | O(n²) | each ordered pair, each candidate overlap length |
| Held–Karp (memo / table) | O(2ⁿ·n²) | O(2ⁿ·n) | n·2ⁿ states × n predecessors; plus parents |

`n = 12`: about `600,000` transitions.

## Why the optimized approach is better

Classic Held–Karp: `n!` → `2^n · n²`, and the parent table turns the number into the actual string.

## Common mistakes

- Minimising length directly while forgetting that the first word adds its full length — maximising overlap avoids this bookkeeping.
- Computing `overlap[i][j]` as any common substring — it must be **suffix of i = prefix of j**.
- Reconstructing in the wrong direction (parents give the order from the end; reverse it).

## Key takeaway

"Order these items to optimise a sum of pairwise costs between neighbours" (n ≤ ~16) is **TSP on a bitmask**: state `(visited, last)`, and keep parent pointers if you must output the order.

## Related problems

- [Traveling Salesperson Problem](../../../04-advanced-dp-patterns/10-traveling-salesperson-problem/) — the topic lesson.
- [847. Shortest Path Visiting All Nodes](../847-shortest-path-visiting-all-nodes/) — the same `(mask, last)` state with unit-weight edges.
- [Shortest Common Supersequence](../../../03-subsequence-and-knapsack-dp/16-shortest-common-supersequence/) — rebuilding a string from a DP table.
