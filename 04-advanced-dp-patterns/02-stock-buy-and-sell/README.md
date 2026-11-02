# Stock Buy and Sell

> **Module:** 04 — Advanced DP Patterns · **Difficulty:** Intermediate → Advanced · **Estimated time:** 4–5 hours

## 1. What is this topic?

LeetCode has a family of six "Best Time to Buy and Sell Stock" problems. They look different (one transaction, unlimited, at most two, at most `k`, with a cooldown, with a fee), but they are **one DP** with one state:

```
(day, holding, transactionsLeft)
```

Each variant changes only one thing: drops a dimension, adds a dimension, or edits one transition. This topic builds the family in order — LC 121 → 122 → 123 → 188 → 309 → 714 — so you see exactly which line changes each time.

## 2. Why does this concept exist?

Until now, a state was "where am I" (an index, a cell, a remaining capacity). Here the state also includes **what mode I am in** — holding a share or not, cooling down or not. Adding a small "mode" dimension to a state is one of the most powerful ideas in DP. It reappears in [State Machine DP](../12-state-machine-dp/) and in many Hard problems.

## 3. What problem does it solve?

Given daily prices, choose when to buy and sell (one share at a time) to maximise profit under a rule: a transaction limit, a cooldown after selling, or a fee per transaction.

## 4. Prerequisites

- [State representation](../../00-foundations/09-state-representation/)
- [State transitions and base cases](../../00-foundations/10-state-transitions-and-base-cases/)
- [House Robber](../../01-one-dimensional-dp/06-house-robber/) — "take or skip each day"
- [Introduction to 3D DP](../../02-two-dimensional-and-grid-dp/08-introduction-to-3d-dp/) — a third dimension in the state
- [Space optimization basics](../../00-foundations/15-space-optimization-basics/)

## 5. What you will learn

- How to put a "mode" (holding / not holding) into a DP state.
- How to read a transition as an edge in a small **state diagram**.
- How each of the six stock problems is the same recurrence with one change.
- How to roll the table into a handful of variables (O(1) space) by running the state machine forward day by day.

## 6. Important terminology

| Term | Meaning in plain English |
|---|---|
| Transaction | One buy followed later by one sell. |
| Holding | We currently own one share (we bought and have not sold yet). |
| Free | We own nothing and may buy. |
| `transactionsLeft` | How many more sells we are still allowed to make. |
| Cooldown | A day after selling on which buying is forbidden (LC 309). |
| Fee | Money paid once per completed transaction (LC 714). |
| State machine | A small set of modes and the allowed moves between them. |
| Cash balance | Profit so far: money received from sells minus money paid for buys and fees. |

## 7. Where this is used in real problems

- The six stock problems themselves (common in interviews).
- Any problem with "modes" and rules for switching: holding/not holding, on/off, last action was A or B (LC 801, LC 552, LC 1220 — see [State Machine DP](../12-state-machine-dp/)).
- Scheduling with rest days, rentals with fees, resource limits on the number of actions.

## 8. After completing this topic you should be able to…

- [ ] Write `f(day, holding, transactionsLeft)` and its recurrence from memory.
- [ ] Explain which dimension or edge changes for each of LC 121, 122, 123, 188, 309, 714.
- [ ] Draw the buy/sell/rest state diagram, including the cooldown state.
- [ ] Turn the table into O(1) or O(k) variables and explain the update order.
- [ ] Recognise when `k` is large enough to drop the transaction limit.

## 9. Practice problems

| # | Problem | Platform/ID | Difficulty | Pattern tested | Why it belongs here |
|---|---|---|---|---|---|
| 1 | [Best Time to Buy and Sell Stock](https://leetcode.com/problems/best-time-to-buy-and-sell-stock/) | LeetCode 121 | Easy | `(day, holding)`, one transaction | The base case of the family. |
| 2 | [Best Time to Buy and Sell Stock II](https://leetcode.com/problems/best-time-to-buy-and-sell-stock-ii/) | LeetCode 122 | Medium | `(day, holding)`, unlimited | Sell now returns to "free" instead of ending. |
| 3 | [Best Time to Buy and Sell Stock with Transaction Fee](https://leetcode.com/problems/best-time-to-buy-and-sell-stock-with-transaction-fee/) | LeetCode 714 | Medium | Edge cost changes | Only the sell edge changes: `+ price - fee`. |
| 4 | [Best Time to Buy and Sell Stock with Cooldown](https://leetcode.com/problems/best-time-to-buy-and-sell-stock-with-cooldown/) | LeetCode 309 | Medium | Extra state / skip a day | Sell jumps to `day + 2`, or add a "cooling" state. |
| 5 | [Best Time to Buy and Sell Stock III](https://leetcode.com/problems/best-time-to-buy-and-sell-stock-iii/) | LeetCode 123 | Hard | Full 3D state, k = 2 | Needs the `transactionsLeft` dimension. |
| 6 | [Best Time to Buy and Sell Stock IV](https://leetcode.com/problems/best-time-to-buy-and-sell-stock-iv/) | LeetCode 188 | Hard | Full 3D state, general k | LC 123 with `2` replaced by `k`. |

Solutions are in [solution/](solution/), one independently compilable sub-folder per problem: [stock-i](solution/stock-i/) (121), [stock-ii](solution/stock-ii/) (122), [stock-iii](solution/stock-iii/) (123), [stock-iv](solution/stock-iv/) (188), [stock-cooldown](solution/stock-cooldown/) (309), [stock-fee](solution/stock-fee/) (714).

## 10. Navigation

⬅ Previous: [DP on Strings](../01-dp-on-strings/) · ➡ Next: [Interval DP](../03-interval-dp/) · 📖 [Walkthrough](walkthrough.md) · 💻 [Solutions](solution/)
