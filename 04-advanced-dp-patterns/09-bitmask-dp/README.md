# Bitmask DP

> **Module:** 04 — Advanced DP Patterns · **Difficulty:** Advanced · **Estimated time:** 3–4 hours

## 1. What is this topic?

Bitmask DP is dynamic programming where part of the state is a **set of items**, stored as an integer whose bits say which items are in the set. Because a set becomes a number between `0` and `2ⁿ − 1`, it can index an array directly: `dp[mask]`.

## 2. Why does this concept exist?

Many problems ask you to use every item exactly once: assign workers to jobs, visit every city, split numbers into groups. Trying every **order** costs n!. But the future usually depends only on **which** items are used, not the order they were used in. Storing the set instead of the order merges all those orders into one state and brings the cost down to about 2ⁿ · n. For n = 15 that is about 500,000 steps instead of more than a trillion.

## 3. What problem does it solve?

- Assignment: match n things to n things at minimum cost, or count the valid matchings.
- Partitioning: split a small set into groups with equal sums or limited capacity.
- Covering: choose the fewest items that together cover every requirement.
- Ordering: find the best order to visit or process all items (see the next topic, TSP).

## 4. Prerequisites

- [State representation](../../00-foundations/09-state-representation/): what information a state must carry.
- [Memoization](../../00-foundations/06-memoization/) and [Tabulation](../../00-foundations/07-tabulation/).
- [Subsequences fundamentals](../../03-subsequence-and-knapsack-dp/01-subsequences-fundamentals/): thinking in terms of take/skip over subsets.
- Binary numbers are taught from scratch in Section 2 of the walkthrough, so no prior bit-manipulation knowledge is needed.

## 5. What you will learn

- How to represent a subset as an integer, and how to add, test, remove and count items with bit operations.
- How to loop over all submasks of a mask, and why the loop works.
- The `dp[mask]` assignment pattern, where the next index is `bitCount(mask)` (LeetCode 1879, AtCoder DP O).
- How `sum(mask) % target` turns bucket filling into a pure mask state (LeetCode 698).
- How to read constraints: when `n ≤ 20` suggests bitmask DP, and when it is impossible.

## 6. Important terminology

| Term | Meaning in plain English |
|---|---|
| Bit i | the binary digit worth 2ⁱ; bit 0 is the rightmost |
| Mask | an integer used as a set: bit i is 1 when item i is in the set |
| Full mask | all n items present: `(1 << n) - 1` |
| Set / test / clear a bit | add / check / remove one item |
| Popcount | the number of 1-bits, i.e. the size of the set (`Integer.bitCount`) |
| Submask | a mask that uses only bits that are 1 in another mask (a subset of it) |
| Assignment problem | pair each item on one side with a different item on the other side |

## 7. Where this is used in real problems

- Pairing or scheduling with tiny n: workers to bikes, students to mentors, tasks to work sessions.
- Group splitting: partition to k equal subsets, fair distribution of cookies.
- Games over a shrinking pool of numbers (LeetCode 464 Can I Win).
- Covering skills or requirements with the smallest team (LeetCode 1125).
- Routing through all locations (TSP, next topic) and seat layouts row by row (LeetCode 1349).

## 8. After completing this topic you should be able to…

- [ ] Convert between a subset and its mask by hand.
- [ ] Set, test, clear and toggle a bit, count bits, and loop over submasks, explaining each.
- [ ] Explain why the mask (and not the order) is enough state for an assignment problem.
- [ ] Write `dp[mask]` for LeetCode 1879 both top-down and bottom-up.
- [ ] Turn "minimum" into "count" (AtCoder DP O) by changing only the combine step.
- [ ] Solve LeetCode 698 with `sum(mask) % target`.
- [ ] Estimate from the constraints whether 2ⁿ · n fits in time.

## 9. Practice problems

| # | Problem | Platform/ID | Difficulty | Pattern tested | Why it belongs here |
|---|---|---|---|---|---|
| 1 | [Beautiful Arrangement](https://leetcode.com/problems/beautiful-arrangement/) | LeetCode 526 | Medium | count assignments, `i = bitCount(mask)` | the gentlest assignment-by-mask problem |
| 2 | [Partition to K Equal Sum Subsets](https://leetcode.com/problems/partition-to-k-equal-sum-subsets/) | LeetCode 698 | Medium | `sum(mask) % target` | taught in the walkthrough; bucket filling as a mask state |
| 3 | [Minimum Number of Work Sessions to Finish the Tasks](https://leetcode.com/problems/minimum-number-of-work-sessions-to-finish-the-tasks/) | LeetCode 1986 | Medium | mask + current fill | 698 with "minimise the number of buckets" |
| 4 | [Can I Win](https://leetcode.com/problems/can-i-win/) | LeetCode 464 | Medium | game state as a mask | memoized win/lose over used numbers |
| 5 | [Matching](https://atcoder.jp/contests/dp/tasks/dp_o) | AtCoder DP O | – | counting assignments | 1879's DP with `+` instead of `min` |
| 6 | [Minimum XOR Sum of Two Arrays](https://leetcode.com/problems/minimum-xor-sum-of-two-arrays/) | LeetCode 1879 | Hard | min-cost assignment | the core lesson of this topic |
| 7 | [Smallest Sufficient Team](https://leetcode.com/problems/smallest-sufficient-team/) | LeetCode 1125 | Hard | mask of covered skills | the mask stores requirements instead of items |
| 8 | [Number of Ways to Wear Different Hats to Each Other](https://leetcode.com/problems/number-of-ways-to-wear-different-hats-to-each-other/) | LeetCode 1434 | Hard | choosing which side becomes the mask | the mask must go over the small side (people, not hats) |
| 9 | [Maximum Students Taking Exam](https://leetcode.com/problems/maximum-students-taking-exam/) | LeetCode 1349 | Hard | row-by-row masks | mask describes one row; transitions check neighbouring rows |

## 10. Navigation

⬅ Previous: [DP on Graphs](../08-dp-on-graphs/) · ➡ Next: [Traveling Salesperson Problem](../10-traveling-salesperson-problem/) · 📖 [Walkthrough](walkthrough.md) · 💻 [Solutions](solution/)
