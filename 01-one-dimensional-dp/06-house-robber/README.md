# House Robber

> **Module:** 01 · One-Dimensional DP · **Difficulty:** Beginner → Intermediate · **Estimated time:** 45–75 min

## 1. What is this topic?

A robber walks down a street of houses, each holding some money. Robbing two neighbouring houses on the same night sets off an alarm. What is the most money the robber can take?

If you just finished [Maximum Sum of Non-Adjacent Elements](../05-maximum-sum-of-non-adjacent-elements/), you will notice this is the same problem in a story. That is the point of this topic: **practise recognizing a known pattern under a new story**, then solve it quickly and confidently. You will also learn a second, equivalent way to write the state ("was house `i` robbed or not?"), which is the seed of state-machine DP.

## 2. Why does this concept exist?

Interview and contest problems rarely say "pick non-adjacent elements". They tell a story. The real skill is to strip the story away and see the structure underneath. House Robber is the most famous example of that structure, and it is the base for House Robber II (circular street), House Robber III (houses in a tree) and many other variations.

## 3. What problem does it solve?

[LeetCode 198 · House Robber](https://leetcode.com/problems/house-robber/): given non-negative amounts `nums[i]`, maximize the total of a selection that contains no two adjacent indices.

## 4. Prerequisites

- [Maximum Sum of Non-Adjacent Elements](../05-maximum-sum-of-non-adjacent-elements/) — the pick/not-pick pattern this topic applies.
- [State representation](../../00-foundations/09-state-representation/) — how one problem can have several valid state definitions.

## 5. What you will learn

- How to map a story problem onto a known pattern, step by step.
- What changes (and what doesn't) when all values are non-negative.
- The **two-state formulation**: `robbed[i]` and `skipped[i]`.
- How to check that two different DP formulations agree.

## 6. Important terminology

| Term | Meaning in plain English |
|------|--------------------------|
| Loot | The total money taken. |
| Adjacent houses | Houses `i` and `i+1`. |
| Pattern mapping | Translating each part of a story into the parts of a known DP. |
| Two-state DP | Keeping two values per index, one for each possible "status" of that index. |
| State machine | A picture of the statuses (robbed / skipped) and which status may follow which. |

## 7. Where this is used in real problems

Choosing non-conflicting time slots or seats, scheduling with a mandatory rest after each task, maximizing points when taking an item forbids its neighbours (Delete and Earn), and, through the two-state idea, the whole family of [stock buy-and-sell](../../04-advanced-dp-patterns/02-stock-buy-and-sell/) and [state-machine DP](../../04-advanced-dp-patterns/12-state-machine-dp/) problems.

## 8. After completing this topic you should be able to…

- [ ] Map House Robber onto the pick/not-pick pattern in one sentence.
- [ ] Solve LeetCode 198 in all four styles in under 15 minutes.
- [ ] Write the two-state version and explain each transition.
- [ ] Draw the two-state "state machine" with its allowed transitions.

## 9. Practice problems

| # | Problem | Platform/ID | Difficulty | Pattern tested | Why it belongs here |
|---|---------|-------------|------------|----------------|---------------------|
| 1 | House Robber | [LeetCode 198](https://leetcode.com/problems/house-robber/) | Medium | Pick/not-pick, no two adjacent | The core problem of this topic. |
| 2 | Vacation | [AtCoder dp_c](https://atcoder.jp/contests/dp/tasks/dp_c) | — | Several states per day, "not the same as yesterday" | Generalizes the two-state version to three activities per index. |
| 3 | Delete and Earn | [LeetCode 740](https://leetcode.com/problems/delete-and-earn/) | Medium | Transform the input, then House Robber | The house-robber structure is hidden behind a preprocessing step. |
| 4 | Solving Questions With Brainpower | [LeetCode 2140](https://leetcode.com/problems/solving-questions-with-brainpower/) | Medium | Pick/not-pick with a per-item skip length | "Rob" forbids the next `k[i]` items instead of just one. |

## 10. Navigation

⬅ Previous: [Maximum Sum of Non-Adjacent Elements](../05-maximum-sum-of-non-adjacent-elements/) · ➡ Next: [House Robber II](../07-house-robber-ii/) · 📖 [Walkthrough](walkthrough.md) · 💻 [Solutions](solution/)
