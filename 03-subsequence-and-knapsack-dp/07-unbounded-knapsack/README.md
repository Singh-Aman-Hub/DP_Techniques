# Unbounded Knapsack

> **Module:** 03 — Subsequence and Knapsack DP · **Difficulty:** Intermediate · **Estimated time:** 90 min

## 1. What is this topic?

The same bag and the same goal as [0/1 Knapsack](../02-zero-one-knapsack/), but every item type is available in **unlimited copies**. You may take an item zero, one, two, … times, as long as the total weight fits. The whole difference is one index in the recurrence — after taking item `i`, you **stay at `i`** — and, in the one-row version, the capacity loop runs **forward** instead of backward.

## 2. Why does this concept exist?

Many resources are not single objects but *types*: coin denominations, rod lengths, product sizes, tile shapes. When repeats are allowed, the 0/1 recurrence gives wrong (too small) answers. Unbounded knapsack is the matching model, and it is the parent of [Coin Change](../08-coin-change/), [Coin Change II](../09-coin-change-ii/) and [Rod Cutting](../10-rod-cutting/).

## 3. What problem does it solve?

- Maximum value under a weight limit with unlimited copies of each item type.
- With small changes: fewest coins for an amount (`min`), number of ways to make an amount (`+`), best way to cut a rod.

## 4. Prerequisites

- [0/1 Knapsack](../02-zero-one-knapsack/) — especially the one-row version and its backward loop
- [Subsequences Fundamentals](../01-subsequences-fundamentals/) — take/skip

## 5. What you will learn

- Why the take branch moves to `(i, c − w)` instead of `(i + 1, c − w)`, and why that is enough to allow any number of copies.
- How the table changes: "take" reads the **same row**.
- Why the one-row loop must go **forward** here — the exact bug of 0/1 knapsack becomes the correct algorithm.
- How to recover how many copies of each item were used.

## 6. Important terminology

| Term | Meaning in plain English |
|---|---|
| Item type | A kind of item available in unlimited supply (e.g. "a 3 kg box worth 50"). |
| Unbounded | No upper limit on how many copies of each type you take (only the capacity limits you). |
| Stay on `i` | After taking a copy of type `i`, the next decision is again about type `i`. |
| Same-row dependency | In the table, `dp[i][c]` reads `dp[i][c − w]` — a cell of the row being filled. |
| Forward loop | Capacities processed from small to large, so smaller capacities of the current row are already final when read. |

## 7. Where this is used in real problems

- [Coin Change](https://leetcode.com/problems/coin-change/) — fewest coins (topic 08).
- [Perfect Squares](https://leetcode.com/problems/perfect-squares/) — fewest square numbers summing to `n`.
- Cutting stock, packing identical boxes, choosing repeatable actions under a time budget.

## 8. After completing this topic you should be able to…

- [ ] Write the unbounded recurrence and point to the one index that differs from 0/1.
- [ ] Fill an unbounded knapsack table by hand, reading the take value from the current row.
- [ ] Explain why the 1-D loop runs forward here and backward in 0/1, with an example.
- [ ] Decide from a problem statement whether items are 0/1 or unbounded.

## 9. Practice problems

| # | Problem | Platform/ID | Difficulty | Pattern tested | Why it belongs here |
|---|---|---|---|---|---|
| 1 | [Perfect Squares](https://leetcode.com/problems/perfect-squares/) | LeetCode 279 | Medium | Unbounded, minimize count | The items are `1, 4, 9, …`, each reusable; a gentle first unbounded problem. |
| 2 | [Coin Combinations I](https://cses.fi/problemset/task/1635) | CSES 1635 | — | Unbounded counting where order matters | Contrast: ordered sequences need the capacity loop **outside** the coin loop. |
| 3 | [Combination Sum IV](https://leetcode.com/problems/combination-sum-iv/) | LeetCode 377 | Medium | Same as CSES 1635 | Despite the name it counts ordered sequences, not sets — a classic trap. |
| 4 | [Form Largest Integer With Digits That Add up to Target](https://leetcode.com/problems/form-largest-integer-with-digits-that-add-up-to-target/) | LeetCode 1449 | Hard | Unbounded, exact capacity, then rebuild the answer | Maximize the digit count with exact cost `target`, then reconstruct greedily from the table. |

## 10. Navigation

⬅ Previous: [Target Sum](../06-target-sum/) · ➡ Next: [Coin Change](../08-coin-change/) · 📖 [Walkthrough](walkthrough.md) · 💻 [Solutions](solution/)
