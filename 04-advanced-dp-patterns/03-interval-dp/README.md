# Interval DP

> **Module:** 04 — Advanced DP Patterns · **Difficulty:** Advanced · **Estimated time:** 3–4 hours

## 1. What is this topic?

**Interval DP** solves problems about a contiguous range of items — a stretch of an array, a substring, a chain of matrices, a polygon's vertices — by defining

```
dp[i][j] = the best answer for the range i..j
```

and computing it by **trying every split point `k`** inside the range: the range is cut into a left part and a right part, both smaller ranges whose answers are already known, plus a cost for joining them. Because every range depends only on shorter ranges, the table is filled **by increasing length**.

The worked example is [Minimum Score Triangulation of Polygon (LC 1039)](https://leetcode.com/problems/minimum-score-triangulation-of-polygon/).

## 2. Why does this concept exist?

In earlier modules the state moved in one direction (from the start, or from the end). Some problems do not work that way: the best decision is made **in the middle**, and it splits the problem into two independent halves. Cutting a stick, merging piles, multiplying matrices, triangulating a polygon, bursting balloons — all of them are "choose where to split, then solve both sides". Interval DP is the general recipe for this.

## 3. What problem does it solve?

Optimisation or counting over a sequence when:
- the answer for a range depends on answers for smaller **sub-ranges**, and
- some choice inside the range (a split point, a last action, a root) separates it into independent pieces.

## 4. Prerequisites

- [DP on Strings](../01-dp-on-strings/) — the substring-shaped table `dp[i][j]`
- [Longest Palindromic Subsequence](../../03-subsequence-and-knapsack-dp/13-longest-palindromic-subsequence/) — an interval DP you have already met
- [Transition dependencies and computation order](../../00-foundations/11-transition-dependencies-and-computation-order/)
- [Rod Cutting](../../03-subsequence-and-knapsack-dp/10-rod-cutting/) — cutting something into pieces

## 5. What you will learn

- The interval DP template: `dp[i][j] = best over k of (dp[left part] + dp[right part] + cost(i, k, j))`.
- Two flavours of split: a **shared** split point (`dp[i][k]`, `dp[k][j]`) and a **disjoint** split (`dp[i][k]`, `dp[k+1][j]`).
- Why the table must be filled **by length**, illustrated with a diagonal diagram.
- Why interval DP is usually O(n³) time and O(n²) space.
- How to recognise interval DP in a problem statement.

## 6. Important terminology

| Term | Meaning in plain English |
|---|---|
| Interval / range | All items from index `i` to index `j`, contiguous. |
| Length of a range | How many items it contains: `j - i + 1`. |
| Split point `k` | An index inside the range where we cut it into a left and a right part. |
| Merge / join cost | The extra cost paid when the two parts are combined. |
| Fill by length | Compute all ranges of length 1, then 2, then 3, … so smaller ranges are always ready. |
| Diagonal | The cells `dp[i][i + d]` for a fixed gap `d`; in the table they form a diagonal line. |
| Triangulation | Cutting a convex polygon into triangles using non-crossing diagonals. |

## 7. Where this is used in real problems

- Merging piles at a cost equal to their total (AtCoder dp_n Slimes; the classic "merge stones").
- Optimal order of matrix multiplication ([MCM](../04-matrix-chain-multiplication/)), optimal binary search trees.
- Cutting sticks or strings (LC 1547), polygon triangulation (LC 1039).
- Parsing: the CYK algorithm checks whether a sentence fits a grammar with this exact table shape.
- Two-player games where players take from either end (LC 877, LC 486, AtCoder dp_l Deque).

## 8. After completing this topic you should be able to…

- [ ] Write the interval DP template and explain each term.
- [ ] Decide whether the split is "shared point" or "disjoint halves".
- [ ] Fill a table by length by hand, and draw the diagonal order.
- [ ] Explain the O(n³) time and O(n²) space.
- [ ] Solve LC 1039 and AtCoder Slimes.

## 9. Practice problems

| # | Problem | Platform/ID | Difficulty | Pattern tested | Why it belongs here |
|---|---|---|---|---|---|
| 1 | [Stone Game](https://leetcode.com/problems/stone-game/) | LeetCode 877 | Medium | Interval game, take from either end | Smallest interval DP: `dp[i][j]` reads only `dp[i+1][j]` and `dp[i][j-1]`. |
| 2 | [Predict the Winner](https://leetcode.com/problems/predict-the-winner/) | LeetCode 486 | Medium | Interval game (score difference) | Same table as LC 877 with a real answer to compute. |
| 3 | [Removal Game](https://cses.fi/problemset/task/1097) | CSES 1097 | — | Interval game, maximise own score | Same idea, larger input; practise fill by length. |
| 4 | [Deque](https://atcoder.jp/contests/dp/tasks/dp_l) | AtCoder dp_l | — | Interval game, score difference | AtCoder's version; good to compare with LC 486. |
| 5 | [Slimes](https://atcoder.jp/contests/dp/tasks/dp_n) | AtCoder dp_n | — | Split point + range-sum cost | The purest "try every split" problem. |
| 6 | [Minimum Score Triangulation of Polygon](https://leetcode.com/problems/minimum-score-triangulation-of-polygon/) | LeetCode 1039 | Medium | Split at a shared vertex | The worked example of this topic. |
| 7 | [Minimum Cost Tree From Leaf Values](https://leetcode.com/problems/minimum-cost-tree-from-leaf-values/) | LeetCode 1130 | Medium | Split + range maximum | Join cost needs the max of each side. |
| 8 | [Guess Number Higher or Lower II](https://leetcode.com/problems/guess-number-higher-or-lower-ii/) | LeetCode 375 | Medium | Split point, minimax | Choose a guess `k` that splits the range; worst case of both sides. |
| 9 | [Minimum Cost to Cut a Stick](https://leetcode.com/problems/minimum-cost-to-cut-a-stick/) | LeetCode 1547 | Hard | Split on cut positions | Interval DP over the sorted cut list, not over the stick. |

Solutions in this folder (LC 1039): [BruteForce.java](solution/BruteForce.java), [Memoization.java](solution/Memoization.java), [Tabulation.java](solution/Tabulation.java).

## 10. Navigation

⬅ Previous: [Stock Buy and Sell](../02-stock-buy-and-sell/) · ➡ Next: [Matrix Chain Multiplication](../04-matrix-chain-multiplication/) · 📖 [Walkthrough](walkthrough.md) · 💻 [Solutions](solution/)
