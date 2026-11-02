# Burst Balloons

> **Module:** 04 — Advanced DP Patterns · **Difficulty:** Advanced · **Estimated time:** 3–4 hours

## 1. What is this topic?

[LC 312 Burst Balloons](https://leetcode.com/problems/burst-balloons/) is the most famous interval DP "trick" problem. Balloons in a row have numbers; bursting one pays the product of it and its **current** neighbours, and then its neighbours become adjacent. You choose the bursting order to maximise the total.

The natural idea — "which balloon do I burst **first**?" — does not give independent subproblems. The insight that unlocks the problem is to ask "which balloon is burst **last** in this range?". With that change, and two padding `1`s at the ends, the problem becomes the same shape as [Matrix Chain Multiplication](../04-matrix-chain-multiplication/).

## 2. Why does this concept exist?

Many interval problems involve **removing** things, which changes who is next to whom. If you split on the first removal, the two sides still interact later through their new neighbours. Splitting on the **last** removal fixes the boundary for the whole time the inside is being processed, which makes the two sides independent. This "think backwards / choose the last action" idea is a general tool for Hard problems.

## 3. What problem does it solve?

Maximise (or minimise) the total score of removing all elements of a range, when the score of a removal depends on the elements next to it at that moment.

## 4. Prerequisites

- [Interval DP](../03-interval-dp/) — the template, shared vs disjoint splits, fill by length
- [Matrix Chain Multiplication](../04-matrix-chain-multiplication/) — the same `a[i]·a[k]·a[j]` cost shape
- [Optimal substructure](../../00-foundations/05-optimal-substructure/) — why independence matters

## 5. What you will learn

- Why "burst first" leads to subproblems that are not independent, shown on a concrete example.
- Why "burst last" makes them independent.
- How padding with `1` on both ends removes all edge cases.
- The open-interval state `dp[i][j]` = best for balloons strictly between `i` and `j`.
- How to recover the bursting order from the table.

## 6. Important terminology

| Term | Meaning in plain English |
|---|---|
| Burst | Remove a balloon and collect `left × it × right` coins, using its neighbours at that moment. |
| Neighbour | The nearest balloon still present on that side; if none, treat it as `1`. |
| Padding | Adding a balloon of value `1` at each end so every real balloon always has two neighbours. |
| Open interval `(i, j)` | The balloons strictly between positions `i` and `j`; `i` and `j` themselves are not burst in this subproblem. |
| Wall | A balloon that stays in place while everything on one side of it is burst; it separates the two sides. |
| Independent subproblems | Two subproblems whose best answers do not affect each other. |

## 7. Where this is used in real problems

- LC 312 itself (a classic interview Hard).
- Removal games where neighbours change: LC 1547 (cut a stick: cost depends on the current piece), LC 664 Strange Printer, LC 546 Remove Boxes.
- Any interval problem where "first action" fails but "last action" works.

## 8. After completing this topic you should be able to…

- [ ] Explain with an example why choosing the first balloon does not split the problem.
- [ ] Explain why choosing the last balloon does.
- [ ] Write the padded recurrence `dp[i][j] = max over k of dp[i][k] + dp[k][j] + a[i]·a[k]·a[j]`.
- [ ] Fill the table for `[3, 1, 5, 8]` by hand.
- [ ] Recover the bursting order.

## 9. Practice problems

| # | Problem | Platform/ID | Difficulty | Pattern tested | Why it belongs here |
|---|---|---|---|---|---|
| 1 | [Minimum Score Triangulation of Polygon](https://leetcode.com/problems/minimum-score-triangulation-of-polygon/) | LeetCode 1039 | Medium | Shared split `k`, product cost | Identical recurrence shape (min instead of max) — warm-up. |
| 2 | [Burst Balloons](https://leetcode.com/problems/burst-balloons/) | LeetCode 312 | Hard | Last action in an open interval | The topic itself. |
| 3 | [Minimum Cost to Cut a Stick](https://leetcode.com/problems/minimum-cost-to-cut-a-stick/) | LeetCode 1547 | Hard | Padding the ends + shared split | Add `0` and `n` to the cuts, exactly like the padding `1`s. |
| 4 | [Strange Printer](https://leetcode.com/problems/strange-printer/) | LeetCode 664 | Hard | Interval DP, merge equal ends | Another "think about what happens to `s[i]`" interval problem. |
| 5 | [Remove Boxes](https://leetcode.com/problems/remove-boxes/) | LeetCode 546 | Hard | Interval DP + extra dimension | When `(i, j)` is not enough and the state needs a third number. |

Solutions in this folder: [BruteForce.java](solution/BruteForce.java), [Memoization.java](solution/Memoization.java), [Tabulation.java](solution/Tabulation.java).

## 10. Navigation

⬅ Previous: [Matrix Chain Multiplication](../04-matrix-chain-multiplication/) · ➡ Next: [Palindrome Partitioning](../06-palindrome-partitioning/) · 📖 [Walkthrough](walkthrough.md) · 💻 [Solutions](solution/)
