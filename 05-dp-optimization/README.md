# Module 05 — DP Optimization

> **Prerequisites:** Modules [00](../00-foundations/), [01](../01-one-dimensional-dp/), [02](../02-two-dimensional-and-grid-dp/), [03](../03-subsequence-and-knapsack-dp/) and [04](../04-advanced-dp-patterns/) · **Difficulty:** Advanced · **Estimated time:** 25–35 hours

## What this module is about

Up to now you have learned to *find* a correct DP: define a state, write a transition, pick a computation order. This module is about the next question an interviewer or an online judge will ask:

> "Your recurrence is correct. Can you make it fit in the memory limit, or run 100× faster?"

Every topic here follows the same rhythm:

1. Write the **slow DP** you already know how to derive.
2. Look closely at the transition and find a **property** (which cells it reads, a range it sums over, a monotone optimal choice, a hidden straight line, a fixed linear rule…).
3. **Argue why the property holds**, with a small numeric example.
4. Exploit it to get the **optimized DP**, and test it against the slow one on random inputs.

The optimizations fall into two families:

```
            DP optimization
           /               \
   SPACE (memory)         TIME (speed)
   01 space optimization   03 prefix / suffix sums and maxima    O(N·K²)  -> O(N·K)
   02 rolling arrays       04 monotonic queue                    O(N·K)   -> O(N)
                           05 divide and conquer                 O(K·N²)  -> O(K·N log N)
                           06 convex hull trick                  O(N²)    -> O(N) or O(N log N)
                           07 SOS DP (sum over subsets)          O(4^n)   -> O(n·2^n)
                           08 Knuth optimization                 O(n³)    -> O(n²)
                           09 matrix exponentiation              O(n·S)   -> O(S³·log n)
```

## Topic order

| # | Topic | One-line summary | Time |
|---|-------|------------------|------|
| 01 | [Space optimization](01-space-optimization/) | Read the dependency footprint of a transition and keep only the rows/cells it touches (1D → O(1), 2D → one row, knapsack reverse loop, LCS `prev` variable). | 2–3 h |
| 02 | [Rolling arrays](02-rolling-arrays/) | Keep two layers indexed by `i % 2` (or `i & 1`) and roll them, including 3D → 2D for Cherry Pickup II. | 2 h |
| 03 | [Prefix and suffix optimization](03-prefix-and-suffix-optimization/) | When a transition sums or maxes over a *range* of the previous row, precompute prefix sums / prefix maxima (Candies, Falling Path Sum II). | 3 h |
| 04 | [Monotonic queue optimization](04-monotonic-queue-optimization/) | Sliding-window maximum of earlier DP values with a monotonic deque (Jump Game VI, Constrained Subsequence Sum). | 3 h |
| 05 | [Divide and conquer optimization](05-divide-and-conquer-optimization/) | Partition DP whose best split point moves monotonically: solve a whole row with `compute(l, r, optL, optR)`. | 3–4 h |
| 06 | [Convex hull trick](06-convex-hull-trick/) | Rewrite the transition as "minimum of lines at x" and keep the lower envelope (Frog 3, Monster Game). | 4 h |
| 07 | [SOS DP](07-sos-dp/) | Sum over all submasks of every mask in O(n·2^n) by adding one bit-dimension at a time. | 3 h |
| 08 | [Knuth optimization](08-knuth-optimization/) | Interval DP where `opt(i, j-1) ≤ opt(i, j) ≤ opt(i+1, j)` cuts O(n³) to O(n²). | 3 h |
| 09 | [Matrix exponentiation](09-matrix-exponentiation/) | Turn a fixed linear recurrence into a matrix power and compute it in O(log n) multiplications. | 3 h |

Topics 01–04 are interview-relevant and should be done by everyone. Topics 05–09 are competitive-programming techniques that appear in LeetCode Hard problems and contest rounds; study them once 01–04 feel comfortable.

## How to study this module

- Do the topics in order. Topic 02 builds on 01, topic 04 builds on 03, and topics 05 and 08 share the "monotone optimal split point" idea.
- For each topic, read the README, then the walkthrough **with pen and paper**: redo the small numeric tables yourself before reading the optimized code.
- Run both solution files. Each optimized solution contains a randomized cross-check against the slow version; change the seed or the input sizes and run it again.
- Do the exercises at the end of each walkthrough before opening the answers.

## After this module you can…

- [ ] Look at any recurrence and say which earlier cells it reads, and cut memory to one row or a few variables.
- [ ] Recognise a "range of the previous row" transition and replace the inner loop with prefix sums, prefix maxima or a monotonic deque.
- [ ] Recognise partition DPs and interval DPs where the optimal split point is monotone, and apply divide and conquer or Knuth.
- [ ] Rewrite `min_j (dp[j] + f(i)·g(j) + …)` as a query on a set of lines.
- [ ] Compute sums over submasks for all masks in O(n·2^n).
- [ ] Solve a fixed-size linear recurrence for n up to 10^18 with matrix exponentiation.
- [ ] Always verify an optimized DP against the slow one with random tests.

## Navigation

⬅ Previous module: [04 — Advanced DP Patterns](../04-advanced-dp-patterns/) · ➡ Next module: [06 — LeetCode Practice](../06-leetcode-practice/) · 🗺 [Roadmap](../ROADMAP.md)
