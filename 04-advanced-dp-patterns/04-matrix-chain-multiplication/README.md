# Matrix Chain Multiplication

> **Module:** 04 — Advanced DP Patterns · **Difficulty:** Advanced · **Estimated time:** 3–4 hours

## 1. What is this topic?

You must multiply a chain of matrices `A × B × C × D × …`. Matrix multiplication is **associative** — `(AB)C` and `A(BC)` give the same result — but the **amount of work** can differ enormously. **Matrix Chain Multiplication (MCM)** asks: in which order should we multiply to do the fewest scalar multiplications?

It is the textbook example of interval DP and the origin of the name "partition DP": the state is a sub-chain `i..j`, and we try every place `k` to put the **last** multiplication. There is no LeetCode judge for the classic problem, so the solutions here are self-checking programs.

## 2. Why does this concept exist?

MCM is the cleanest problem where:
- the subproblems are ranges `i..j`,
- the choice is a split point,
- the cost of joining depends on the range's endpoints and the split point.

Once you understand MCM, you can read almost any "partition" problem as "MCM with a different join cost": [triangulation](../03-interval-dp/), [burst balloons](../05-burst-balloons/), [palindrome partitioning](../06-palindrome-partitioning/), cutting sticks, merging stones.

## 3. What problem does it solve?

Given the dimensions of `n` matrices, find the minimum number of scalar multiplications needed to compute their product, and the parenthesization (bracket placement) that achieves it.

## 4. Prerequisites

- [Interval DP](../03-interval-dp/) — the template and the fill-by-length order
- [Recursion trees and brute force](../../00-foundations/03-recursion-trees-and-brute-force/)
- [Converting memoization to tabulation](../../00-foundations/14-converting-memoization-to-tabulation/)

No linear algebra is needed beyond the rule in section 6 below.

## 5. What you will learn

- Why the multiplication order changes the cost, with numbers.
- How a chain of `n` matrices is described by `n + 1` dimensions.
- The partition recursion `f(i, j) = min over k of f(i, k) + f(k+1, j) + d[i-1]·d[k]·d[j]`.
- A complete hand-filled table for 4 matrices.
- How to store the best split and print the optimal parenthesization.

## 6. Important terminology

| Term | Meaning in plain English |
|---|---|
| Matrix `p × q` | A grid of numbers with `p` rows and `q` columns. |
| Compatible | `X (p × q)` can multiply `Y (q × r)` only if the inner sizes match (`q`). The result is `p × r`. |
| Scalar multiplication | One multiplication of two numbers. Multiplying `p × q` by `q × r` takes `p · q · r` of them. |
| Chain | A sequence of matrices to multiply in the given order. |
| Dimension array `d` | `d[0..n]`; matrix `m` (1-indexed) has size `d[m-1] × d[m]`. |
| Parenthesization | Where the brackets go, i.e. the order of the multiplications. |
| Split point `k` | The last multiplication computes `(A_i … A_k) × (A_{k+1} … A_j)`. |

## 7. Where this is used in real problems

- Compilers and numeric libraries choose evaluation orders for products of matrices (and tensors).
- Database query optimisers choose the order of joins with the same kind of DP.
- Every "split a range at a point and pay to combine" problem: LC 1039, LC 1130, LC 1547, AtCoder Slimes, optimal binary search trees.

## 8. After completing this topic you should be able to…

- [ ] Compute the cost of a given parenthesization by hand.
- [ ] Derive the MCM recurrence from the "last multiplication" idea.
- [ ] Fill the MCM table by chain length, by hand, for 4 matrices.
- [ ] Keep a `split` table and print the optimal brackets.
- [ ] Explain O(n³) time and O(n²) space.

## 9. Practice problems

| # | Problem | Platform/ID | Difficulty | Pattern tested | Why it belongs here |
|---|---|---|---|---|---|
| 1 | Matrix Chain Multiplication (classic) | this folder | — | Partition DP, disjoint split | The topic itself; check your answers with the solution files. |
| 2 | [Slimes](https://atcoder.jp/contests/dp/tasks/dp_n) | AtCoder dp_n | — | Partition DP, cost = range sum | MCM with the join cost replaced by the range total. |
| 3 | [Minimum Score Triangulation of Polygon](https://leetcode.com/problems/minimum-score-triangulation-of-polygon/) | LeetCode 1039 | Medium | Partition DP, shared split | Same `d[i]·d[k]·d[j]` cost shape as MCM. |
| 4 | [Minimum Cost Tree From Leaf Values](https://leetcode.com/problems/minimum-cost-tree-from-leaf-values/) | LeetCode 1130 | Medium | Partition DP, cost = max·max | Build a tree by choosing where the root splits the leaves. |
| 5 | [Minimum Cost to Cut a Stick](https://leetcode.com/problems/minimum-cost-to-cut-a-stick/) | LeetCode 1547 | Hard | Partition DP over cut positions | MCM in disguise: add the stick ends to the cut list and sort. |

Solutions in this folder: [BruteForce.java](solution/BruteForce.java), [Memoization.java](solution/Memoization.java), [Tabulation.java](solution/Tabulation.java), [PrintParenthesization.java](solution/PrintParenthesization.java).

## 10. Navigation

⬅ Previous: [Interval DP](../03-interval-dp/) · ➡ Next: [Burst Balloons](../05-burst-balloons/) · 📖 [Walkthrough](walkthrough.md) · 💻 [Solutions](solution/)
