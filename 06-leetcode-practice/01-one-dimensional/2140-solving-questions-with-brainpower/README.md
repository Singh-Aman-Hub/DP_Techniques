# 2140. Solving Questions With Brainpower

> **Pattern:** 1D take/skip DP with a variable jump · **Builds on:** [House Robber](../../../01-one-dimensional-dp/06-house-robber/) · **Difficulty:** Medium

## Problem overview

An exam has questions in a fixed order. Question `i` is a pair `[points, brainpower]`. For each
question you either **solve** it, earning `points` but being forced to skip the next `brainpower`
questions, or **skip** it and move to the next one. Return the maximum total points.

## Link & metadata

| Field | Value |
|---|---|
| LeetCode | [2140. Solving Questions With Brainpower](https://leetcode.com/problems/solving-questions-with-brainpower/) |
| Difficulty | Medium |
| Pattern | 1D take/skip DP, where "take" jumps forward by a per-item distance |
| Prerequisite topic | [`01-one-dimensional-dp/06-house-robber`](../../../01-one-dimensional-dp/06-house-robber/) |
| Why this pattern | House Robber is the special case where every `brainpower` equals 1: take house `i`, skip exactly one |

## Examples

| questions | Answer | Why |
|---|---|---|
| `[[3,2],[4,3],[4,4],[2,5]]` | 5 | solve q0 (3 pts, skip q1, q2), solve q3 (2 pts) |
| `[[1,1],[2,2],[3,3],[4,4],[5,5]]` | 7 | solve q1 (2 pts, skip q2, q3), solve q4 (5 pts) |
| `[[10,0],[10,0]]` | 20 | brainpower 0 skips nothing, so solve both |

## Brute-force intuition

At each question, branch into "solve" and "skip". Both branches move strictly forward, so the
recursion terminates, but there are up to 2ⁿ paths. Many paths arrive at the same question
index with the same future ahead of them, and the future does not care *how* we got there.
That is exactly the condition for caching by index.

## Deriving the DP (7 questions)

### 1. What is changing?
The index `i` of the next question we are allowed to consider.

### 2. What does the state represent?
`dp[i]` = the maximum points we can still earn from questions `i, i+1, …, n-1`, given that we are
free to act on question `i`.

This is a **suffix** state. A prefix state (`best using questions 0..i`) is awkward here,
because whether question `i` is available depends on an earlier question's brainpower. Looking
forward avoids that: once we solve `i`, we know exactly where we land.

### 3. What are the choices?
- **Skip** question `i` → continue at `i + 1`.
- **Solve** question `i` → earn `points[i]` and continue at `i + brainpower[i] + 1`.

### 4. What is the recurrence?
```
dp[i] = max( dp[i + 1],                                  // skip
             points[i] + dp[i + brainpower[i] + 1] )    // solve, then jump
```

### 5. What are the base cases?
`dp[i] = 0` for every `i ≥ n`: no questions left, no points. The jump can overshoot `n`, so we
treat any index past the end as 0.

### 6. What is the order of computation?
`dp[i]` reads larger indices only, so fill from `i = n-1` **down** to 0. The answer is `dp[0]`.

### 7. Can the state space be optimized?
Not to O(1). The jump distance `brainpower[i] + 1` can be anything up to `n`, so any later cell
may be read. The O(n) array is required, which is why there is no `Optimized.java`.

## Recursive solution

```java
static long best(int i) {
    if (i >= n) return 0;
    long skip  = best(i + 1);
    long solve = q[i][0] + best(i + q[i][1] + 1);
    return Math.max(skip, solve);
}
```

## Memoization

Cache `best(i)` in a `long[]` (answers can exceed `int`: 10⁵ questions × 10⁵ points). See
[`solution/Memoization.java`](solution/Memoization.java).

## Tabulation

See [`solution/Tabulation.java`](solution/Tabulation.java). Allocate `dp[n + 1]` with `dp[n] = 0`,
and clamp the jump target with `Math.min(n, i + brainpower + 1)` so it never goes out of bounds.

## Space optimization

Not applicable (see question 7). The tabulation already uses O(n) time and space, which is
optimal for a problem whose input is O(n).

## Dry run

`questions = [[3,2],[4,3],[4,4],[2,5]]`, `n = 4`:

| i | points, bp | skip = dp[i+1] | jump to | solve = points + dp[jump] | dp[i] |
|---|---|---|---|---|---|
| 4 | — | — | — | — | 0 (base) |
| 3 | 2, 5 | 0 | 9 → past end | 2 + 0 = 2 | 2 |
| 2 | 4, 4 | 2 | 7 → past end | 4 + 0 = 4 | 4 |
| 1 | 4, 3 | 4 | 5 → past end | 4 + 0 = 4 | 4 |
| 0 | 3, 2 | 4 | 3 | 3 + dp[3] = 5 | **5** |

## Complexity

| Approach | Time | Space | Reason |
|---|---|---|---|
| Brute force | O(2ⁿ) | O(n) | two branches per question |
| Memoization | O(n) | O(n) | n states × O(1) work; memo + recursion stack |
| Tabulation | O(n) | O(n) | one backward loop; the dp array |

## Why the optimized approach is better

Tabulation is the version to submit: memoization recurses up to depth `n = 10⁵`, which can
overflow Java's default stack. The loop has the same O(n) cost with no stack risk.

## Common mistakes

- **Using `int`.** The total can reach 10¹⁰; use `long`.
- **Jumping to `i + brainpower` instead of `i + brainpower + 1`.** You skip `brainpower`
  questions *after* `i`, so you land one further.
- **Trying a forward (prefix) DP.** It needs to know which earlier question blocked you; the
  suffix formulation sidesteps that.

## Key takeaway

When a choice decides **where you go next**, define the state as "best from here to the end"
and fill it backward. The jump target is then a known index.

## Related problems

- [198. House Robber](https://leetcode.com/problems/house-robber/), the brainpower = 1 special case
- [983. Minimum Cost For Tickets](../983-minimum-cost-for-tickets/), another "a choice covers the next few items" DP
- Previous: [740. Delete and Earn](../740-delete-and-earn/) · Next: [983. Minimum Cost For Tickets](../983-minimum-cost-for-tickets/)
