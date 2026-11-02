# SOS DP (Sum over Subsets)

> **Module:** 05 — DP Optimization · **Difficulty:** Advanced · **Estimated time:** 2–3 hours

## 1. What is this topic?

You have a value for every subset of an n-element set, stored at index `mask` (a bitmask, as in bitmask DP). For **every** mask at once you want the sum (or max, or count) of the values of all of its subsets:

```
F[mask] = sum of A[sub] over every sub that is a subset of mask
```

SOS DP ("Sum over Subsets") computes all 2^n answers in **O(n · 2^n)** time, instead of the obvious O(4^n), and faster than the clever O(3^n) submask enumeration.

## 2. Why does this concept exist?

Many bitmask problems end with "for each set S, combine the information of all sets contained in S". Written directly, that is a double loop over masks: 2^n × 2^n = 4^n operations. For n = 20 that is about 10^12 — hopeless. Enumerating only real submasks reduces it to 3^n ≈ 3.5 · 10^9 — still too slow. SOS DP brings it down to 20 · 2^20 ≈ 2 · 10^7 — instant.

The trick is to see subsets as points in an n-dimensional grid with side length 2. "All subsets of mask" is then a **prefix** of that grid, and a multi-dimensional prefix sum can be built **one dimension at a time** — exactly the way you already build a 2D prefix sum row by row and then column by column.

## 3. What problem does it solve?

- Sum / count / max over all submasks (or supermasks) of every mask.
- "How many items in the list are compatible with query set S?" for many queries S.
- Counting pairs `(x, y)` with `x & y == 0` or `x | y == mask`.
- As a building block: zeta / Möbius transforms used in subset convolution and inclusion–exclusion.

## 4. Prerequisites

- [Bitmask DP](../../04-advanced-dp-patterns/09-bitmask-dp/) — masks as sets, `&`, `|`, `^`, `<<`.
- [2D DP state representation](../../02-two-dimensional-and-grid-dp/01-2d-dp-state-representation/) — thinking of a table as a grid.
- [Prefix and suffix optimization](../03-prefix-and-suffix-optimization/) — prefix sums replace repeated range sums.
- [Time and space complexity in DP](../../00-foundations/12-time-and-space-complexity-in-dp/) — counting work per state.

## 5. What you will learn

- Why the naive double loop over masks costs 4^n.
- The submask-enumeration trick `sub = (sub - 1) & mask` and why its total cost is exactly 3^n.
- SOS DP as an n-dimensional prefix sum: "add one bit-dimension at a time".
- The exact invariant that makes the in-place update correct.
- The mirrored version: sums over **super**masks.
- Applying submask enumeration to LeetCode 1178.

## 6. Important terminology

| Term | Meaning in plain English |
|---|---|
| Mask | An integer whose binary digits say which elements are in a set (bit i = 1 means element i is in). |
| Submask (subset) | `sub` is a submask of `mask` when every 1-bit of `sub` is also a 1-bit of `mask`; test: `(sub & mask) == sub`. |
| Supermask | The reverse: `sup` contains every bit of `mask`. |
| popcount | Number of 1-bits in a mask (`Integer.bitCount`). |
| Submask enumeration | Visiting only the submasks of one mask with `sub = (sub - 1) & mask`. |
| Zeta transform | The formal name for "sum over subsets"; SOS DP computes it. |
| Dimension / bit pass | One outer iteration of SOS DP, handling one bit position. |

## 7. Where this is used in real problems

- Word/puzzle matching where words are letter sets (LeetCode 1178).
- Counting subsets or pairs with AND/OR constraints in competitive programming.
- Precomputing "is any item a subset of this set?" tables inside larger bitmask DPs (assignment problems, team selection).
- Inclusion–exclusion over sets of primes (square-free numbers, LeetCode 2572 style).

## 8. After completing this topic you should be able to…

- [ ] Explain why the naive approach is 4^n and submask enumeration is 3^n (binomial theorem).
- [ ] Write the submask-enumeration loop without an infinite loop at `sub = 0`.
- [ ] Write SOS DP from memory and explain why the in-place update is safe.
- [ ] Fill the 3-bit SOS table by hand, pass by pass.
- [ ] Switch between subset sums and superset sums.
- [ ] Decide between submask enumeration (few queries, small masks) and full SOS DP (all masks).

## 9. Practice problems

| # | Problem | Platform/ID | Difficulty | Pattern tested | Why it belongs here |
|---|---|---|---|---|---|
| 1 | [Number of Valid Words for Each Puzzle](https://leetcode.com/problems/number-of-valid-words-for-each-puzzle/) | LeetCode 1178 | Hard | Letter masks + submask enumeration | The cleanest "sum over subsets of a query mask" problem; solved in full in the walkthrough. |
| 2 | [Count the Number of Square-Free Subsets](https://leetcode.com/problems/count-the-number-of-square-free-subsets/) | LeetCode 2572 | Medium | Prime masks, iterating submasks of the complement | Each number is a set of primes; combining compatible numbers uses the same `(sub - 1) & mask` walk. |
| 3 | [Number of Ways to Wear Different Hats to Each Other](https://leetcode.com/problems/number-of-ways-to-wear-different-hats-to-each-other/) | LeetCode 1434 | Hard | Bitmask DP over people | Good revision of bitmask states before and after SOS; try to spot where a subset-sum table would help. |
| 4 | [Elevator Rides](https://cses.fi/problemset/task/1653) | CSES 1653 | Unrated (CSES) | Bitmask DP over subsets | Reinforces thinking of every subset as a DP state, the setting where SOS-style transforms appear. |

## 10. Navigation

⬅ Previous: [Convex Hull Trick](../06-convex-hull-trick/) · ➡ Next: [Knuth Optimization](../08-knuth-optimization/) · 📖 [Walkthrough](walkthrough.md) · 💻 [Solutions](solution/)
