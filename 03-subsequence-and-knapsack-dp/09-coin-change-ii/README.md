# Coin Change II — Counting Combinations

> **Module:** Subsequence and Knapsack DP · **Difficulty:** Intermediate · **Estimated time:** 75–100 min

## 1. What is this topic?

In [Coin Change](../08-coin-change/) you found the **fewest** coins that make an amount. Here you **count** how many different *combinations* of coins make the amount, with unlimited copies of every coin. The order of the coins does not matter: `1 + 2` and `2 + 1` are the same combination.

The DP itself is the [unbounded knapsack](../07-unbounded-knapsack/) recurrence with `+` instead of `min`. The real lesson of this topic is a subtle one: **the order of your two loops decides whether you count combinations or permutations (ordered sequences)**. Get it wrong and your code silently counts the wrong thing.

## 2. Why does this concept exist?

Counting problems are everywhere in DP, and "how many ways" questions come in two flavours:

- *How many multisets?* (Which coins, how many of each — order ignored.)
- *How many sequences?* (Which coin first, which coin second… — order matters.)

Both are solved by almost the same five lines of code. A learner who understands *why* the loop order matters can switch between them on purpose; one who memorised a template will mix them up in an interview.

## 3. What problem does it solve?

[LeetCode 518 — Coin Change II](https://leetcode.com/problems/coin-change-ii/): given coin denominations and a target amount, return the number of combinations that sum to the amount (0 if none). It also explains its twin, [LeetCode 377 — Combination Sum IV](https://leetcode.com/problems/combination-sum-iv/), which (despite its name) counts **ordered** sequences, and the CSES pair [Coin Combinations I](https://cses.fi/problemset/task/1635) (ordered) and [Coin Combinations II](https://cses.fi/problemset/task/1636) (unordered).

## 4. Prerequisites

- [Unbounded Knapsack](../07-unbounded-knapsack/) — "stay on the same item after taking it".
- [Coin Change](../08-coin-change/) — the same choices, but minimising.
- [Target Sum](../06-target-sum/) and [Subset Sum](../03-subset-sum/) — counting with `+` instead of deciding with `||`.
- [Space Optimization Basics](../../00-foundations/15-space-optimization-basics/) — rolling a 2D table into 1D.

## 5. What you will learn

- How to turn a "minimum" knapsack recurrence into a "count the ways" recurrence.
- Why `remaining == 0` returns **1** (the empty combination) and not 0.
- Why *coins outer, amount inner* counts combinations and *amount outer, coins inner* counts permutations — with a full dry run of both.
- How the 2D table collapses into a single array, and why the inner loop runs left to right here.
- Why intermediate counts can exceed the 32-bit range even when the answer fits.

## 6. Important terminology

| Term | Meaning in plain English |
|---|---|
| Combination (multiset) | A bag of coins where only *how many of each* matters, not the order. `{1, 2, 2}` |
| Permutation / ordered sequence | A list of coins where position matters. `1,2,2` ≠ `2,1,2` |
| Unbounded | Each coin type can be used any number of times |
| Counting DP | A DP whose cells hold *numbers of ways*; choices are combined with `+` |
| Canonical order | A fixed order in which we agree to build every combination (e.g. all 1s, then all 2s, then all 5s), so each one is built exactly once |
| Empty combination | Using no coins at all; it is the one way to make amount 0 |

## 7. Where this is used in real problems

- Counting the ways to make change, to reach a score, or to fill a budget with repeatable items.
- Any "number of ways" knapsack: [Combination Sum IV](https://leetcode.com/problems/combination-sum-iv/), [Number of Dice Rolls With Target Sum](https://leetcode.com/problems/number-of-dice-rolls-with-target-sum/), [Coin Combinations I/II](https://cses.fi/problemset/task/1636).
- The "loop order = what you count" idea returns in [Distinct Subsequences](../15-distinct-subsequences/) and in later combinatorics DP.

## 8. After completing this topic you should be able to…

- [ ] State `ways(index, remaining)` in a full English sentence and explain each choice.
- [ ] Explain why the base case for amount 0 is 1.
- [ ] Fill the 2D table for coins `[1, 2, 5]`, amount 5 by hand.
- [ ] Explain, with an example, why swapping the two loops turns 4 into 9 for that input.
- [ ] Write the 1D solution from memory and say which loop order it uses and why.
- [ ] Decide quickly whether a new "count the ways" problem wants combinations or sequences.

## 9. Practice problems

| # | Problem | Platform/ID | Difficulty | Pattern tested | Why it belongs here |
|---|---|---|---|---|---|
| 1 | [Coin Change II](https://leetcode.com/problems/coin-change-ii/) | LeetCode 518 | Medium | Unbounded knapsack, counting combinations | The topic problem itself |
| 2 | [Coin Combinations II](https://cses.fi/problemset/task/1636) | CSES 1636 | – | Combinations, modulo arithmetic | Same as 518 but with a modulus and bigger limits |
| 3 | [Coin Combinations I](https://cses.fi/problemset/task/1635) | CSES 1635 | – | Ordered sequences | Swap the loops — practise the other half of the lesson |
| 4 | [Combination Sum IV](https://leetcode.com/problems/combination-sum-iv/) | LeetCode 377 | Medium | Ordered sequences ("permutations") | The famous twin; its name says "combination" but it counts orders |
| 5 | [Perfect Squares](https://leetcode.com/problems/perfect-squares/) | LeetCode 279 | Medium | Unbounded knapsack (min) | Same item structure, `min` instead of `+`; good contrast |
| 6 | [Number of Dice Rolls With Target Sum](https://leetcode.com/problems/number-of-dice-rolls-with-target-sum/) | LeetCode 1155 | Medium | Counting ordered sequences with a fixed length | Adds a "number of items used" dimension to the counting DP |

## 10. Navigation

⬅ Previous: [Coin Change](../08-coin-change/) · ➡ Next: [Rod Cutting](../10-rod-cutting/) · 📖 [Walkthrough](walkthrough.md) · 💻 [Solutions](solution/)
