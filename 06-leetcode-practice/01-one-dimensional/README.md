# Practice: One-Dimensional DP

Problems whose state is a single index (or a single amount): `dp[i]` depends on a few earlier
entries. Do these after [Module 01: One-Dimensional DP](../../01-one-dimensional-dp/). Each folder has a
README that derives the DP with the 7 questions, plus self-checking Java solutions.

**What changes from problem to problem** is the *look-back*: fixed width (1137), take/skip
(740, 2140), several step sizes (983, 279), "where did the last piece start" (139), and
"best ending here" with two tracked values (152, 918).

| # | Problem | LeetCode | Difficulty | Pattern | Prerequisite topic |
|---|---|---|---|---|---|
| 1 | [1137. N-th Tribonacci Number](1137-n-th-tribonacci-number/) | [link](https://leetcode.com/problems/n-th-tribonacci-number/) | Easy | Fixed look-back of 3 | [01-fibonacci-number](../../01-one-dimensional-dp/01-fibonacci-number/) |
| 2 | [740. Delete and Earn](740-delete-and-earn/) | [link](https://leetcode.com/problems/delete-and-earn/) | Medium | Bucket by value, then House Robber | [06-house-robber](../../01-one-dimensional-dp/06-house-robber/) |
| 3 | [2140. Solving Questions With Brainpower](2140-solving-questions-with-brainpower/) | [link](https://leetcode.com/problems/solving-questions-with-brainpower/) | Medium | Take/skip with a variable jump (suffix DP) | [06-house-robber](../../01-one-dimensional-dp/06-house-robber/) |
| 4 | [983. Minimum Cost For Tickets](983-minimum-cost-for-tickets/) | [link](https://leetcode.com/problems/minimum-cost-for-tickets/) | Medium | Min cost with step sizes 1/7/30 days | [03-minimum-cost-climbing-stairs](../../01-one-dimensional-dp/03-minimum-cost-climbing-stairs/) |
| 5 | [139. Word Break](139-word-break/) | [link](https://leetcode.com/problems/word-break/) | Medium | Boolean prefix DP, choose the last word | [08-decode-ways](../../01-one-dimensional-dp/08-decode-ways/) |
| 6 | [279. Perfect Squares](279-perfect-squares/) | [link](https://leetcode.com/problems/perfect-squares/) | Medium | Fewest pieces, unlimited reuse | [02-climbing-stairs](../../01-one-dimensional-dp/02-climbing-stairs/) |
| 7 | [152. Maximum Product Subarray](152-maximum-product-subarray/) | [link](https://leetcode.com/problems/maximum-product-subarray/) | Medium | "Ending at i", track max **and** min | [09-maximum-subarray-kadane-vs-dp](../../01-one-dimensional-dp/09-maximum-subarray-kadane-vs-dp/) |
| 8 | [918. Maximum Sum Circular Subarray](918-maximum-sum-circular-subarray/) | [link](https://leetcode.com/problems/maximum-sum-circular-subarray/) | Medium | Kadane for max and min + complement trick | [09-maximum-subarray-kadane-vs-dp](../../01-one-dimensional-dp/09-maximum-subarray-kadane-vs-dp/) |

**How to use this folder:** read only the *Problem overview* and try the 7 questions yourself
before opening the rest of the README. Then run the solutions with `java Tabulation.java` from
each `solution/` folder.

➡ Next pattern: [02-grid](../02-grid/)
