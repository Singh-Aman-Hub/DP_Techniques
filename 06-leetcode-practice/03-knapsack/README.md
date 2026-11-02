# Practice: Knapsack DP

Problems where items are chosen under a capacity, and the state is (items considered, capacity
left). Do these after the knapsack topics of
[Module 03: Subsequence and Knapsack DP](../../03-subsequence-and-knapsack-dp/).

**What changes from problem to problem:** each item may contribute one of several values (1155),
order may matter so the loop order flips (377), the capacity may have two coordinates (474), or one
dimension may be an "at least" target that gets clamped (879).

| # | Problem | LeetCode | Difficulty | Pattern | Prerequisite topic |
|---|---|---|---|---|---|
| 1 | [1155. Number of Dice Rolls With Target Sum](1155-number-of-dice-rolls-with-target-sum/) | [link](https://leetcode.com/problems/number-of-dice-rolls-with-target-sum/) | Medium | Counting; each item contributes one of k values | [03-subset-sum](../../03-subsequence-and-knapsack-dp/03-subset-sum/) |
| 2 | [377. Combination Sum IV](377-combination-sum-iv/) | [link](https://leetcode.com/problems/combination-sum-iv/) | Medium | Unbounded counting of **permutations** (contrast with 518) | [09-coin-change-ii](../../03-subsequence-and-knapsack-dp/09-coin-change-ii/) |
| 3 | [474. Ones and Zeroes](474-ones-and-zeroes/) | [link](https://leetcode.com/problems/ones-and-zeroes/) | Medium | 0/1 knapsack with a 2D capacity | [02-zero-one-knapsack](../../03-subsequence-and-knapsack-dp/02-zero-one-knapsack/) |
| 4 | [879. Profitable Schemes](879-profitable-schemes/) | [link](https://leetcode.com/problems/profitable-schemes/) | Hard | 0/1 counting; capacity + clamped "at least" profit | [02-zero-one-knapsack](../../03-subsequence-and-knapsack-dp/02-zero-one-knapsack/) |

**Two rules to keep in mind:**
- In a 1D (rolled) knapsack, iterate capacity **downward** for 0/1 items and **upward** for unbounded items.
- For unbounded counting, **items outside** counts combinations and **amount outside** counts permutations.

⬅ Previous pattern: [02-grid](../02-grid/) · ➡ Next pattern: [04-subsequences-and-strings](../04-subsequences-and-strings/)
