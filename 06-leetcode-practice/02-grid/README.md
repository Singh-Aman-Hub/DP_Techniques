# Practice: Grid DP

Problems where the state is a cell of a grid, sometimes with an extra "moves left" dimension.
Do these after [Module 02: Two-Dimensional and Grid DP](../../02-two-dimensional-and-grid-dp/).

**What changes from problem to problem:** the cell value may summarize a *shape* ending there
(221, 1277), the walk may go in any direction so a step budget joins the state (576, 688), or the
transition may need a smarter way to take a minimum over the previous row (1289).

| # | Problem | LeetCode | Difficulty | Pattern | Prerequisite topic |
|---|---|---|---|---|---|
| 1 | [221. Maximal Square](221-maximal-square/) | [link](https://leetcode.com/problems/maximal-square/) | Medium | Largest square ending at a cell: `1 + min(up, left, diag)` | [02-unique-paths](../../02-two-dimensional-and-grid-dp/02-unique-paths/) |
| 2 | [1277. Count Square Submatrices with All Ones](1277-count-square-submatrices-with-all-ones/) | [link](https://leetcode.com/problems/count-square-submatrices-with-all-ones/) | Medium | Same table as 221, summed | [02-unique-paths](../../02-two-dimensional-and-grid-dp/02-unique-paths/) |
| 3 | [576. Out of Boundary Paths](576-out-of-boundary-paths/) | [link](https://leetcode.com/problems/out-of-boundary-paths/) | Medium | 3D: moves left × row × col, counting | [08-introduction-to-3d-dp](../../02-two-dimensional-and-grid-dp/08-introduction-to-3d-dp/) |
| 4 | [688. Knight Probability in Chessboard](688-knight-probability-in-chessboard/) | [link](https://leetcode.com/problems/knight-probability-in-chessboard/) | Medium | 3D: moves left × row × col, probabilities | [08-introduction-to-3d-dp](../../02-two-dimensional-and-grid-dp/08-introduction-to-3d-dp/) |
| 5 | [1289. Minimum Falling Path Sum II](1289-minimum-falling-path-sum-ii/) | [link](https://leetcode.com/problems/minimum-falling-path-sum-ii/) | Hard | Row DP + best/second-best trick | [06-maximum-falling-path-sum](../../02-two-dimensional-and-grid-dp/06-maximum-falling-path-sum/) |

**How to use this folder:** for every problem, first draw the grid and mark which neighbouring
cells the current cell needs. That picture tells you the loop order.

⬅ Previous pattern: [01-one-dimensional](../01-one-dimensional/) · ➡ Next pattern: [03-knapsack](../03-knapsack/)
