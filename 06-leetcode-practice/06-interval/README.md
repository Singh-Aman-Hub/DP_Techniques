# Practice: Interval DP

**Interval DP** solves a problem on a contiguous range `[i..j]` by combining answers for **smaller ranges inside it**. Most problems here use one of three moves:

| Move | Recurrence shape | Problems |
|---|---|---|
| Take from an end | `dp[i][j] = best(take i → dp[i+1][j], take j → dp[i][j-1])` | 877, 486 |
| Choose a split point `k` | `dp[i][j] = min_k  dp[i][k] ⊕ dp[k][j] + cost(i, k, j)` | 1039, 375, 1130, 1547 |
| Merge equal items across the range | `dp[i][j]` (or `dp[i][j][k]`) reuses work when `s[i]` matches some `s[m]` inside | 664, 546 |

1043 is the contrast case. Its pieces are scored independently, so the range collapses to a **prefix** `dp[i]` (front partition).

## How to recognize it

- Items are removed or merged from a **row**, and what remains is always contiguous.
- The statement says "cut", "merge", "burst", "triangulate", "split into a left and right part", or "parenthesize".
- The cost of combining two parts depends on the **boundaries** of the range (`i`, `j`, the split `k`), not just on the two answers.
- Fill order: by increasing length, or `i` downward and `j` upward. Typical complexity is O(n³) time and O(n²) space.

Before starting, work through these topics: [Interval DP](../../04-advanced-dp-patterns/03-interval-dp/), [Matrix Chain Multiplication](../../04-advanced-dp-patterns/04-matrix-chain-multiplication/), [Burst Balloons](../../04-advanced-dp-patterns/05-burst-balloons/), and [Palindrome Partitioning](../../04-advanced-dp-patterns/06-palindrome-partitioning/).

## Problems (easiest → hardest)

| # | Problem | LeetCode link | Difficulty | Pattern | Prerequisite topic folder |
|---|---|---|---|---|---|
| 1 | [877. Stone Game](877-stone-game/) | [LC 877](https://leetcode.com/problems/stone-game/) | Medium | take-from-ends game, score difference | [03-interval-dp](../../04-advanced-dp-patterns/03-interval-dp/) |
| 2 | [486. Predict the Winner](486-predict-the-winner/) | [LC 486](https://leetcode.com/problems/predict-the-winner/) | Medium | minimax → score-difference DP | [03-interval-dp](../../04-advanced-dp-patterns/03-interval-dp/) |
| 3 | [1043. Partition Array for Maximum Sum](1043-partition-array-for-maximum-sum/) | [LC 1043](https://leetcode.com/problems/partition-array-for-maximum-sum/) | Medium | front-partition DP (range collapses to a prefix) | [06-palindrome-partitioning](../../04-advanced-dp-patterns/06-palindrome-partitioning/) |
| 4 | [1039. Minimum Score Triangulation of Polygon](1039-minimum-score-triangulation-of-polygon/) | [LC 1039](https://leetcode.com/problems/minimum-score-triangulation-of-polygon/) | Medium | split point, MCM shape | [04-matrix-chain-multiplication](../../04-advanced-dp-patterns/04-matrix-chain-multiplication/) |
| 5 | [375. Guess Number Higher or Lower II](375-guess-number-higher-or-lower-ii/) | [LC 375](https://leetcode.com/problems/guess-number-higher-or-lower-ii/) | Medium | split point with minimax (`min` of `max`) | [03-interval-dp](../../04-advanced-dp-patterns/03-interval-dp/) |
| 6 | [1130. Minimum Cost Tree From Leaf Values](1130-minimum-cost-tree-from-leaf-values/) | [LC 1130](https://leetcode.com/problems/minimum-cost-tree-from-leaf-values/) | Medium | split into left/right subtree; monotonic-stack greedy | [04-matrix-chain-multiplication](../../04-advanced-dp-patterns/04-matrix-chain-multiplication/) |
| 7 | [1547. Minimum Cost to Cut a Stick](1547-minimum-cost-to-cut-a-stick/) | [LC 1547](https://leetcode.com/problems/minimum-cost-to-cut-a-stick/) | Hard | split point over sorted cut positions | [05-burst-balloons](../../04-advanced-dp-patterns/05-burst-balloons/) |
| 8 | [664. Strange Printer](664-strange-printer/) | [LC 664](https://leetcode.com/problems/strange-printer/) | Hard | merge equal characters inside the range | [03-interval-dp](../../04-advanced-dp-patterns/03-interval-dp/) |
| 9 | [546. Remove Boxes](546-remove-boxes/) | [LC 546](https://leetcode.com/problems/remove-boxes/) | Hard | interval + extra "k attached boxes" state | [05-burst-balloons](../../04-advanced-dp-patterns/05-burst-balloons/) |

Within the same difficulty, problems are ordered by how many new ideas they add on top of the base interval-DP template.

## Suggested path

1. **877 → 486:** learn the score-difference (negamax) trick on the easiest range state.
2. **1043:** see why some partition problems do **not** need two indices.
3. **1039 → 375 → 1130 → 1547:** the split-point family. Only the cost term changes: a product, `x + max`, a max-leaf product, the piece length.
4. **664 → 546:** the hardest step. The state must remember something **outside** the range (546's extra `k`) because the range alone is not enough to decide the cost.

Each problem folder contains a README (the 7 derivation questions, a dry run, complexity and mistakes) and a `solution/` folder with self-checking Java files (`java Tabulation.java`).
