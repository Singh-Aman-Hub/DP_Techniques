# 06 — LeetCode Practice

Extra problems for every pattern in the course. Each one is written up to show **how to arrive at** the solution, not just what the code is.

> **Full list:** [`PROBLEM_INDEX.md`](PROBLEM_INDEX.md) has every problem in the course (core and practice), by pattern, easiest first.
> **Your log:** record attempts in [`LEETCODE_TRACKER.md`](../LEETCODE_TRACKER.md).

## How this module relates to the topics

The **core** problem of each topic (House Robber, LCS, Burst Balloons…) is taught in full inside its topic folder in modules 01–05. This module holds **additional** problems that test the same patterns in new forms. A problem that is already taught in a topic is linked, not repeated.

Do a pattern's practice set **right after** finishing the matching topics. You don't need to wait until the end of the course.

| Folder | Do it after | Problems |
|---|---|---|
| [`01-one-dimensional`](01-one-dimensional/README.md) | [01-one-dimensional-dp](../01-one-dimensional-dp/README.md) | 8 |
| [`02-grid`](02-grid/README.md) | [02-two-dimensional-and-grid-dp](../02-two-dimensional-and-grid-dp/README.md) | 5 |
| [`03-knapsack`](03-knapsack/README.md) | [03 topics 01–10](../03-subsequence-and-knapsack-dp/README.md) | 4 |
| [`04-subsequences-and-strings`](04-subsequences-and-strings/README.md) | [03 topics 11–16](../03-subsequence-and-knapsack-dp/README.md) | 12 |
| [`05-stocks-and-state-machines`](05-stocks-and-state-machines/README.md) | [04 stocks](../04-advanced-dp-patterns/02-stock-buy-and-sell/README.md) + [04 state machines](../04-advanced-dp-patterns/12-state-machine-dp/README.md) | 6 |
| [`06-interval`](06-interval/README.md) | [04 topics 03–06](../04-advanced-dp-patterns/README.md) | 9 |
| [`07-trees-and-graphs`](07-trees-and-graphs/README.md) | [04 trees](../04-advanced-dp-patterns/07-dp-on-trees/README.md) + [graphs](../04-advanced-dp-patterns/08-dp-on-graphs/README.md) | 9 |
| [`08-bitmask`](08-bitmask/README.md) | [04 bitmask](../04-advanced-dp-patterns/09-bitmask-dp/README.md) + [TSP](../04-advanced-dp-patterns/10-traveling-salesperson-problem/README.md) | 10 |
| [`09-digit`](09-digit/README.md) | [04 digit DP](../04-advanced-dp-patterns/11-digit-dp/README.md) | 6 |
| [`10-optimization`](10-optimization/README.md) | [05-dp-optimization](../05-dp-optimization/README.md) | 6 |

## What each problem folder contains

```
<id>-<leetcode-slug>/
├── README.md          problem overview (own words) · link · examples · brute-force intuition ·
│                      deriving the DP (7 questions) · recursion · memo · tabulation · space optimization ·
│                      dry run · complexity · why optimized is better · common mistakes · key takeaway
└── solution/          Memoization.java · Tabulation.java · Optimized.java (self-checking)
```

## How to practice (important)

1. **Open only the LeetCode link first.** Do not open the README.
2. Spend **10 minutes** answering the seven questions on paper ([worksheet](../00-foundations/16-the-dp-problem-solving-framework/README.md)).
3. Code the memoized version, then the tabulated version. Submit.
4. **Stuck after 30–45 minutes?** Read *only* the "Deriving the DP" section of the README, close it, and try again.
5. After you get it accepted, read the whole README. Compare your state and recurrence with it, and read the "Common mistakes".
6. Log the attempt in the [tracker](../LEETCODE_TRACKER.md). If you needed hints, mark it `Needs revision` and re-solve on day 3, 7 and 21.

## Running the solutions

```bash
cd 06-leetcode-practice/01-one-dimensional/740-delete-and-earn/solution
java Tabulation.java        # prints PASS/FAIL for built-in test cases
```

The LeetCode method signatures are kept the same, so you can paste the method into LeetCode's editor.
