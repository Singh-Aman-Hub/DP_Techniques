# House Robber II

> **Module:** 01 · One-Dimensional DP · **Difficulty:** Intermediate · **Estimated time:** 60–90 min

## 1. What is this topic?

The same robber, but the houses now stand in a **circle**: the first and the last house are neighbours. The rule "never rob two neighbours" now also forbids robbing both ends.

The topic teaches a general technique: **when a constraint breaks the simple left-to-right structure, split the problem into a few cases that each have the simple structure, solve each case with the DP you already know, and combine.**

## 2. Why does this concept exist?

A DP over a line works because each index only interacts with the ones just before it. A circle adds one interaction that "wraps around" from the end back to the start, so when you are deciding about the last house, the answer depends on a decision made at the very beginning. Instead of inventing a new DP, we remove the wrap-around by **fixing** a decision about one end in advance. Each fixed case is an ordinary line.

## 3. What problem does it solve?

[LeetCode 213 · House Robber II](https://leetcode.com/problems/house-robber-ii/): maximum loot from houses arranged in a circle, no two adjacent houses robbed.

## 4. Prerequisites

- [House Robber](../06-house-robber/) — the line version, reused unchanged here.
- [Maximum Sum of Non-Adjacent Elements](../05-maximum-sum-of-non-adjacent-elements/) — the pick/not-pick proof.

## 5. What you will learn

- The "run the line DP twice" trick: exclude the first house, or exclude the last.
- A careful argument for why those two runs cover **every** valid plan and **only** valid plans.
- Why overlapping cases are harmless for `max` but dangerous for counting.
- How to write a reusable "solve on a sub-range" helper.
- The special case `n = 1`.

## 6. Important terminology

| Term | Meaning in plain English |
|------|--------------------------|
| Circular array | An array whose last element is considered next to its first. |
| Wrap-around constraint | The extra rule between the last and first elements. |
| Case split | Dividing all possible solutions into a few groups and solving each group separately. |
| Cover | Every valid solution belongs to at least one group. |
| Sound | Every solution found in a group is valid for the original problem. |
| Sub-range `[lo..hi]` | The part of the array from index `lo` to index `hi`, inclusive. |

## 7. Where this is used in real problems

Any line DP whose input is circular: circular seating, circular routes, circular arrays in [Maximum Sum Circular Subarray](https://leetcode.com/problems/maximum-sum-circular-subarray/) (covered at the end of [topic 09](../09-maximum-subarray-kadane-vs-dp/)). The same "fix a decision, then solve the rest" idea also handles constraints like "the first item must be taken".

## 8. After completing this topic you should be able to…

- [ ] Explain why at least one of the two end houses is always skipped.
- [ ] Prove that `max(line(0..n-2), line(1..n-1))` is the correct answer.
- [ ] Handle `n = 1` and `n = 2` correctly.
- [ ] Explain why the same split would double-count in a counting problem.

## 9. Practice problems

| # | Problem | Platform/ID | Difficulty | Pattern tested | Why it belongs here |
|---|---------|-------------|------------|----------------|---------------------|
| 1 | House Robber | [LeetCode 198](https://leetcode.com/problems/house-robber/) | Medium | The line DP reused here | Re-solve it as a `robRange(lo, hi)` helper first. |
| 2 | House Robber II | [LeetCode 213](https://leetcode.com/problems/house-robber-ii/) | Medium | Circular constraint by case split | The core problem of this topic. |
| 3 | House Robber III | [LeetCode 337](https://leetcode.com/problems/house-robber-iii/) | Medium | No-adjacent selection on a tree | The next shape after line and circle; solve it after [DP on trees](../../04-advanced-dp-patterns/07-dp-on-trees/). |

## 10. Navigation

⬅ Previous: [House Robber](../06-house-robber/) · ➡ Next: [Decode Ways](../08-decode-ways/) · 📖 [Walkthrough](walkthrough.md) · 💻 [Solutions](solution/)
