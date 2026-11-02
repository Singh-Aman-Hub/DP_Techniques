# Maximum Sum of Non-Adjacent Elements

> **Module:** 01 · One-Dimensional DP · **Difficulty:** Beginner → Intermediate · **Estimated time:** 90–120 min

## 1. What is this topic?

Given an array of integers (negative numbers allowed), choose some of its elements so that **no two chosen elements are next to each other**, and make their sum as large as possible. Choosing nothing is allowed and gives sum 0.

This topic introduces the single most important pattern in the course: **pick / not-pick** (also called *take / skip*). For each element you ask, "Do I use it or not?", and each answer leads to a smaller problem.

## 2. Why does this concept exist?

In the earlier topics the choices were *moves* (1 step or 2 steps). In a huge number of problems the choices are instead *decisions about items*: include this item in the answer, or leave it out. Trying all `2^n` subsets is far too slow. Pick/not-pick turns the decision about the **last** item into a recurrence, and constraints between items (here "no two neighbours") become rules about which smaller state each choice leads to.

You will reuse this pattern in House Robber, in every knapsack problem, in subset sum, in subsequence problems and in many interview questions.

## 3. What problem does it solve?

The generic version of "select non-adjacent items to maximize the total". It is the abstract form of [LeetCode 198 · House Robber](https://leetcode.com/problems/house-robber/), with negative values allowed so that *skipping* becomes a real, sometimes necessary, decision.

## 4. Prerequisites

- [Climbing Stairs](../02-climbing-stairs/) and [Min Cost Climbing Stairs](../03-minimum-cost-climbing-stairs/) — "look back one or two indices" recurrences.
- [Recursion trees and brute force](../../00-foundations/03-recursion-trees-and-brute-force/) — enumerating choices with recursion.
- [Optimal substructure](../../00-foundations/05-optimal-substructure/)

## 5. What you will learn

- The pick/not-pick recursion and how a constraint ("no neighbours") decides where each branch goes.
- Why `max(skip, take)` handles negative numbers automatically.
- Why greedy rules ("take the biggest", "take every other one") fail.
- How to recover the chosen elements from the DP table.
- How to define base cases for "before the start of the array" (the empty selection).

## 6. Important terminology

| Term | Meaning in plain English |
|------|--------------------------|
| Selection / subset | Any group of elements chosen from the array (possibly empty). |
| Adjacent | Next to each other: indices `i` and `i+1`. |
| Pick / take | Include the current element in the selection. |
| Not pick / skip | Leave the current element out. |
| Prefix `arr[0..i]` | The first `i+1` elements of the array. |
| Empty selection | Choosing nothing; its sum is 0. |
| Greedy | Making the locally best-looking choice without looking ahead. |

## 7. Where this is used in real problems

House Robber and its variants, "delete and earn", choosing non-conflicting jobs or questions, picking non-adjacent seats, and (with a capacity instead of an adjacency rule) every [0/1 knapsack](../../03-subsequence-and-knapsack-dp/02-zero-one-knapsack/) problem.

## 8. After completing this topic you should be able to…

- [ ] State the two choices for the last element and where each one leads.
- [ ] Write `best(i) = max(best(i-1), arr[i] + best(i-2))` and explain both terms.
- [ ] Explain why the base cases before index 0 are 0.
- [ ] Give an array on which "take every other element" fails.
- [ ] Reconstruct the chosen indices from a filled table.

## 9. Practice problems

| # | Problem | Platform/ID | Difficulty | Pattern tested | Why it belongs here |
|---|---------|-------------|------------|----------------|---------------------|
| 1 | House Robber | [LeetCode 198](https://leetcode.com/problems/house-robber/) | Medium | Pick/not-pick with no two adjacent | This topic with non-negative values; taught in full in the [next topic](../06-house-robber/). |
| 2 | Solving Questions With Brainpower | [LeetCode 2140](https://leetcode.com/problems/solving-questions-with-brainpower/) | Medium | Pick/not-pick where picking skips a variable number of items | "Pick" jumps forward by a distance given per element instead of by 2. |
| 3 | Delete and Earn | [LeetCode 740](https://leetcode.com/problems/delete-and-earn/) | Medium | Reduce to non-adjacent selection over values | After grouping equal numbers, picking value `v` forbids `v−1` and `v+1`: exactly this topic. |

## 10. Navigation

⬅ Previous: [Frog Jump](../04-frog-jump/) · ➡ Next: [House Robber](../06-house-robber/) · 📖 [Walkthrough](walkthrough.md) · 💻 [Solutions](solution/)
