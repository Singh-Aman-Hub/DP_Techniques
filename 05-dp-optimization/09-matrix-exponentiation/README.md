# Matrix Exponentiation

> **Module:** 05 — DP Optimization · **Difficulty:** Advanced · **Estimated time:** 3 hours

## 1. What is this topic?

Many DPs move a **small, fixed-size state vector** one step forward with the **same linear rule** every time: Fibonacci `(F(i), F(i-1)) → (F(i+1), F(i))`, "how many strings of length i end in each vowel", "how many walks of i edges end at each vertex". A linear rule that never changes is a **matrix** `M`. Doing `n` steps is multiplying by `M` `n` times, i.e. by `M^n`, and `M^n` can be computed with about `2·log₂ n` matrix multiplications using **exponentiation by squaring**. That turns an O(n) DP into O(S³ log n), where S is the size of the state vector — fast enough for n = 10¹⁸.

## 2. Why does this concept exist?

The O(n) tabulation you already know is perfect for n ≤ 10⁸. But some problems ask for n = 10⁹ or 10¹⁸ ("the 10¹⁸-th Fibonacci number modulo 10⁹ + 7"). Even at a billion simple operations per second, 10¹⁸ steps would take about 30 years. When the state is small and the transition is fixed, matrix exponentiation computes the same answer in about 60 squarings.

## 3. What problem does it solve?

- Linear recurrences with constant coefficients: Fibonacci, Tribonacci, `f(n) = 2f(n-1) + 3f(n-3) + …`.
- Counting walks of exactly K edges in a small graph.
- Counting strings / tilings / dialled numbers of huge length when each step only depends on a small "last state" (Knight Dialer, Count Vowels Permutation, Domino and Tromino Tiling).

## 4. Prerequisites

- [Fibonacci Number](../../01-one-dimensional-dp/01-fibonacci-number/)
- [State machine DP](../../04-advanced-dp-patterns/12-state-machine-dp/) (states and allowed transitions)
- [DP on graphs](../../04-advanced-dp-patterns/08-dp-on-graphs/)
- [Rolling arrays](../02-rolling-arrays/) (the O(n) version we are speeding up)
- Basic modular arithmetic: `(a + b) mod p`, `(a · b) mod p`.

## 5. What you will learn

- Matrix multiplication from scratch, and why one DP step is "matrix × vector".
- How to build the transition matrix of any fixed linear recurrence.
- Exponentiation by squaring, first for numbers, then for matrices, and why it needs only O(log n) multiplications.
- Why `(A^K)[u][v]` counts the walks of length K from `u` to `v`.
- How to multiply safely modulo 10⁹ + 7 in Java `long`s.
- When the technique applies (and when it does not: non-linear rules, large states, small n).

## 6. Important terminology

| Term | Meaning in plain English |
|------|--------------------------|
| State vector | The few numbers that fully describe "where the DP is" at step i, e.g. `(F(i), F(i-1))`. |
| Linear transition | Each new state number is a fixed weighted sum of the old ones (no max, min, if). |
| Transition matrix | The table of those fixed weights; multiplying it by the state vector performs one step. |
| Identity matrix `I` | 1 on the diagonal, 0 elsewhere; multiplying by it changes nothing ("zero steps"). |
| Matrix power `M^n` | `M` multiplied by itself n times; performs n steps at once. |
| Exponentiation by squaring | Computing `x^n` by repeatedly squaring `x` and combining the squares that match the 1-bits of n. |
| Adjacency matrix | `A[u][v] = 1` if there is an edge `u → v`, else 0. |
| Walk | A sequence of edges where each starts where the previous ended; vertices may repeat. |

## 7. Where this is used in real problems

- "Answer modulo 10⁹ + 7 for n up to 10¹⁸" with a recurrence of small order.
- Counting paths/walks of a given length in small graphs (Walk, knight moves on a phone pad).
- Tiling and string-counting problems whose state machine has a handful of states.
- Competitive programming: CSES Fibonacci Numbers, Throwing Dice, Graph Paths.

## 8. After completing this topic you should be able to…

- [ ] Multiply two matrices by hand and in code.
- [ ] Write the transition matrix of a recurrence like `f(n) = f(n-1) + 2f(n-2) + f(n-3)`.
- [ ] Implement `power(M, n)` with repeated squaring and explain each line.
- [ ] Explain why the number of multiplications is about `2·log₂ n`.
- [ ] Count walks of length K with `A^K`.
- [ ] Decide whether a given DP can be sped up this way.

## 9. Practice problems

| # | Problem | Platform/ID | Difficulty | Pattern tested | Why it belongs here |
|---|---------|-------------|------------|----------------|---------------------|
| 1 | [Fibonacci Number](https://leetcode.com/problems/fibonacci-number/) | LeetCode 509 | Easy | 2×2 matrix power | Smallest example; n is small here, so treat it as a warm-up. |
| 2 | [Climbing Stairs](https://leetcode.com/problems/climbing-stairs/) | LeetCode 70 | Easy | 2×2 matrix power | Same matrix as Fibonacci with shifted base cases. |
| 3 | [N-th Tribonacci Number](https://leetcode.com/problems/n-th-tribonacci-number/) | LeetCode 1137 | Easy | 3×3 companion matrix | Practise building the matrix of a 3-term recurrence. |
| 4 | [Fibonacci Numbers](https://cses.fi/problemset/task/1722) | CSES 1722 | Unrated (CSES) | 2×2 matrix power, n ≤ 10¹⁸ | Walked through in this topic; impossible without the technique. |
| 5 | [Walk](https://atcoder.jp/contests/dp/tasks/dp_r) | AtCoder dp_r | Unrated (AtCoder) | Adjacency matrix power | Counting walks of length K ≤ 10¹⁸ in a 50-vertex graph. |
| 6 | [Knight Dialer](https://leetcode.com/problems/knight-dialer/) | LeetCode 935 | Medium | 10×10 transition matrix | O(n) passes on LeetCode; solve it again pretending n = 10¹⁸. |
| 7 | [Domino and Tromino Tiling](https://leetcode.com/problems/domino-and-tromino-tiling/) | LeetCode 790 | Medium | Recurrence → 3×3 matrix | Derive `f(n) = 2f(n-1) + f(n-3)`, then power it. |
| 8 | [Count Vowels Permutation](https://leetcode.com/problems/count-vowels-permutation/) | LeetCode 1220 | Hard | 5×5 transition matrix | Solved for huge n in this topic's solutions. |
| 9 | [Number of Ways to Paint N × 3 Grid](https://leetcode.com/problems/number-of-ways-to-paint-n-3-grid/) | LeetCode 1411 | Hard | 2×2 matrix on pattern types | Reduce 12 row colourings to 2 pattern classes first. |
| 10 | [Student Attendance Record II](https://leetcode.com/problems/student-attendance-record-ii/) | LeetCode 552 | Hard | 6×6 state machine matrix | States = (absences, trailing lates); a fixed linear step. |

## 10. Navigation

⬅ Previous: [Knuth optimization](../08-knuth-optimization/) · ➡ Next: [LeetCode practice](../../06-leetcode-practice/) · 📖 [Walkthrough](walkthrough.md) · 💻 [Solutions](solution/)
