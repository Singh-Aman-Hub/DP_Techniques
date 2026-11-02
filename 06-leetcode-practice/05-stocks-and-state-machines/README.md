# Practice: Stocks and State Machines

Problems where, at every step, you are in one of a **small fixed set of states** (the key a
knight stands on, the last letter, "swapped or not", the shape of a tiling's edge…) and rules say
which state may follow which. The DP counts or optimizes over walks through that state graph. Do
these after [State Machine DP](../../04-advanced-dp-patterns/12-state-machine-dp/).

## The six stock problems

The classic state-machine family, **Best Time to Buy and Sell Stock** (121, 122, 123, 188, 309,
714), is taught in full in
[`04-advanced-dp-patterns/02-stock-buy-and-sell`](../../04-advanced-dp-patterns/02-stock-buy-and-sell/).
They are not repeated here. Solve them there first. The problems below reuse the same idea
outside of trading.

## Problems

| # | Problem | LeetCode | Difficulty | Pattern | Prerequisite topic |
|---|---|---|---|---|---|
| 1 | [935. Knight Dialer](935-knight-dialer/) | [link](https://leetcode.com/problems/knight-dialer/) | Medium | Walk counting on a 10-node graph | [12-state-machine-dp](../../04-advanced-dp-patterns/12-state-machine-dp/) |
| 2 | [790. Domino and Tromino Tiling](790-domino-and-tromino-tiling/) | [link](https://leetcode.com/problems/domino-and-tromino-tiling/) | Medium | Tiling with boundary-shape states (flush / overhang) | [12-state-machine-dp](../../04-advanced-dp-patterns/12-state-machine-dp/) |
| 3 | [1220. Count Vowels Permutation](1220-count-vowels-permutation/) | [link](https://leetcode.com/problems/count-vowels-permutation/) | Hard | Walk counting on a 5-node graph (reverse the edges) | [12-state-machine-dp](../../04-advanced-dp-patterns/12-state-machine-dp/) |
| 4 | [1411. Number of Ways to Paint N × 3 Grid](1411-number-of-ways-to-paint-n-3-grid/) | [link](https://leetcode.com/problems/number-of-ways-to-paint-n-3-grid/) | Hard | Row-profile states, compressed by symmetry (12 → 2) | [12-state-machine-dp](../../04-advanced-dp-patterns/12-state-machine-dp/) |
| 5 | [552. Student Attendance Record II](552-student-attendance-record-ii/) | [link](https://leetcode.com/problems/student-attendance-record-ii/) | Hard | State = (absences used, trailing lates) | [12-state-machine-dp](../../04-advanced-dp-patterns/12-state-machine-dp/) |
| 6 | [801. Minimum Swaps To Make Sequences Increasing](801-minimum-swaps-to-make-sequences-increasing/) | [link](https://leetcode.com/problems/minimum-swaps-to-make-sequences-increasing/) | Hard | Two states per index (kept / swapped), minimizing | [12-state-machine-dp](../../04-advanced-dp-patterns/12-state-machine-dp/) |

**How to use this folder:** for each problem, **draw the state diagram first**: circles for
states, arrows for allowed transitions. The recurrence is then "sum (or min) over incoming
arrows". The counting problems (935, 1220, 552) can also be sped up to O(log n) with
[matrix exponentiation](../../05-dp-optimization/09-matrix-exponentiation/).

⬅ Previous pattern: [04-subsequences-and-strings](../04-subsequences-and-strings/) · ➡ Next pattern: [06-interval](../06-interval/)
