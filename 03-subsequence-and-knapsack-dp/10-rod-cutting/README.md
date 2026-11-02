# Rod Cutting

> **Module:** Subsequence and Knapsack DP · **Difficulty:** Intermediate · **Estimated time:** 60–90 min

## 1. What is this topic?

You have a rod of length `n` and a price list: a piece of length 1 sells for `prices[0]`, a piece of length 2 for `prices[1]`, and so on. You may cut the rod into any pieces (any number of cuts, any lengths, repeats allowed). How much money can you make, and **which cuts** achieve it?

Rod cutting is the textbook example from *Introduction to Algorithms* (CLRS, chapter "Dynamic Programming"). It has no official online judge, but it is asked constantly in interviews and it is the cleanest example of an [unbounded knapsack](../07-unbounded-knapsack/) where the **rod length is the knapsack capacity**.

## 2. Why does this concept exist?

Two reasons:

1. It shows that the knapsack pattern hides behind very different stories. "Pieces of length k with price p" is just "items with weight k and value p".
2. It is the natural place to learn **reconstruction**: computing the best *value* is half the job; real users want the *plan* (which cuts). You will learn two ways to recover it: walking back through a 2D table, and storing a "choice" array next to a 1D table.

## 3. What problem does it solve?

Maximise total revenue from cutting a rod of length `n`, given a price for every piece length `1..n`, and output one optimal list of piece lengths. The same structure appears in [Integer Break](https://leetcode.com/problems/integer-break/) (maximise a product instead of a sum) and in any "split a resource into repeatable chunk sizes" question.

## 4. Prerequisites

- [Unbounded Knapsack](../07-unbounded-knapsack/) — the recurrence we reuse directly.
- [0/1 Knapsack](../02-zero-one-knapsack/) — the table layout and the take/skip vocabulary.
- [Coin Change II](../09-coin-change-ii/) — the previous topic; same "stay on the same item" choice.
- [State Representation](../../00-foundations/09-state-representation/) — choosing what the indices mean.

## 5. What you will learn

- How to map a new story onto unbounded knapsack (item = piece length, weight = length, value = price, capacity = rod length).
- The 2D formulation `best[p][r]` and the 1D "choose the first cut" formulation `best[r]`, and why both are correct.
- How to recover the actual cuts from either table.
- How ties make "the" optimal cut list non-unique, and how a strict `>` decides which one you print.

## 6. Important terminology

| Term | Meaning in plain English |
|---|---|
| Piece length | The length of one piece we sell; any value from 1 to n |
| Revenue | Total money from all sold pieces |
| Capacity | In knapsack language, the rod length still available to cut |
| Reconstruction | Walking back through the DP table to recover the decisions, not just the value |
| First cut | The length of the first piece in a plan; the 1D formulation branches on it |
| Choice array | An extra array that records, for each state, which option won (`firstCut[r]`) |

## 7. Where this is used in real problems

- Cutting raw material (pipes, cables, timber, fabric) into sellable sizes.
- Splitting a number or a budget into parts to maximise a sum or product ([Integer Break](https://leetcode.com/problems/integer-break/), [Perfect Squares](https://leetcode.com/problems/perfect-squares/) minimises a count).
- Any problem where "remaining length/size/money" is the capacity and chunk sizes can repeat.
- Note: [Minimum Cost to Cut a Stick](https://leetcode.com/problems/minimum-cost-to-cut-a-stick/) *sounds* similar but is an [interval DP](../../04-advanced-dp-patterns/03-interval-dp/) — there the cut positions are fixed and the cost depends on the current segment.

## 8. After completing this topic you should be able to…

- [ ] Translate rod cutting into unbounded knapsack terms, in one sentence.
- [ ] Write `best(maxPiece, remaining)` with its base cases and choices.
- [ ] Fill the 2D table for the classic price list by hand and read off 22.
- [ ] Walk back through the table to recover the cuts `[6, 2]`.
- [ ] Write the O(n) memory "first cut" version and its `firstCut` reconstruction.
- [ ] Explain why both formulations give the same optimum.

## 9. Practice problems

| # | Problem | Platform/ID | Difficulty | Pattern tested | Why it belongs here |
|---|---|---|---|---|---|
| 1 | Rod cutting (this topic, CLRS §15.1) | — (no judge; use the self-checking files) | – | Unbounded knapsack + reconstruction | The topic problem itself |
| 2 | [Integer Break](https://leetcode.com/problems/integer-break/) | LeetCode 343 | Medium | Rod cutting with product instead of sum | Same "first cut" recurrence; watch the "don't cut at all" edge |
| 3 | [Perfect Squares](https://leetcode.com/problems/perfect-squares/) | LeetCode 279 | Medium | Unbounded knapsack, min count | Pieces = square numbers; minimise pieces instead of maximising price |
| 4 | [Coin Change](https://leetcode.com/problems/coin-change/) | LeetCode 322 | Medium | Unbounded knapsack, min count | Same table, impossible states must be handled |
| 5 | [Rectangle Cutting](https://cses.fi/problemset/task/1744) | CSES 1744 | – | 2D cutting, "try every first cut" | The first-cut idea in two dimensions |
| 6 | [Minimum Cost to Cut a Stick](https://leetcode.com/problems/minimum-cost-to-cut-a-stick/) | LeetCode 1547 | Hard | Interval DP (contrast) | Looks like rod cutting but is not knapsack — good for pattern recognition |

## 10. Navigation

⬅ Previous: [Coin Change II](../09-coin-change-ii/) · ➡ Next: [Longest Increasing Subsequence](../11-longest-increasing-subsequence/) · 📖 [Walkthrough](walkthrough.md) · 💻 [Solutions](solution/)
