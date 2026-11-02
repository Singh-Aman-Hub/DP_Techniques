# 1335. Minimum Difficulty of a Job Schedule

> **Pattern:** Partition DP (cut an array into d blocks), sped up with a monotonic stack · **Builds on:** [Palindrome Partitioning](../../../04-advanced-dp-patterns/06-palindrome-partitioning/), [Monotonic Queue Optimization](../../../05-dp-optimization/04-monotonic-queue-optimization/) · **Difficulty:** Hard

## Problem overview

You have `n` jobs (up to 300) that must be done **in the given order**, over exactly `d` days, with
at least one job per day. A day's difficulty is the hardest job done that day; the schedule's
difficulty is the sum over the days. Return the minimum possible schedule difficulty, or `-1` if
`n < d`.

"In order, at least one per day" means a schedule is just a way to **cut the array into `d`
contiguous blocks**, and each block costs its maximum. That is the classic partition DP.

## Link & metadata

| Field | Value |
|---|---|
| LeetCode | [1335. Minimum Difficulty of a Job Schedule](https://leetcode.com/problems/minimum-difficulty-of-a-job-schedule/) |
| Difficulty | Hard |
| Pattern | Partition DP `dp[day][i] = min over s of dp[day−1][s−1] + cost(s..i)`; optimized with a monotonic stack |
| Prerequisite topic | [`04-advanced-dp-patterns/06-palindrome-partitioning`](../../../04-advanced-dp-patterns/06-palindrome-partitioning/) (cutting into blocks), [`05-dp-optimization/04-monotonic-queue-optimization`](../../../05-dp-optimization/04-monotonic-queue-optimization/) (monotonic structures inside a DP) |
| Why this pattern | The last block can start anywhere; everything before it is the same problem with one fewer day |

## Examples

| jobs | d | Answer | One optimal schedule |
|---|---|---|---|
| [6,5,4,3,2,1] | 2 | 7 | [6,5,4,3,2] + [1] → 6 + 1 |
| [9,9,9] | 4 | -1 | fewer jobs than days |
| [1,1,1] | 3 | 3 | one job per day |
| [7,1,7,1,7,1] | 3 | 15 | [7,1,7,1] [7] [1] → 7 + 7 + 1 (at most one day can avoid every 7) |
| [3,1,4,2,5] | 2 | 8 | [3] + [1,4,2,5] → 3 + 5 (the dry-run example) |

## Brute-force intuition

Choose `d − 1` cut points among the `n − 1` gaps: `C(n−1, d−1)` schedules, each priced in `O(n)`.
For `n = 300, d = 10` that is about 10¹⁷. Hopeless.

But look at the **last** day. If it covers jobs `s..i`, the rest of the schedule is "jobs
`0..s−1` in `d − 1` days", which is the same question, smaller. Many different full schedules share
the same prefix question, so its answer can be reused: overlapping subproblems with optimal
substructure (the best schedule ends with a best schedule of its prefix).

## Deriving the DP (7 questions)

### 1. What is changing?
How many jobs are already scheduled (a prefix `0..i`) and how many days are used (`day`).

### 2. What does the state represent?
`dp[day][i]` = the minimum total difficulty to do jobs `0..i` (inclusive) in exactly `day` days.
(The memoized version uses the mirror view `f(start, daysLeft)` = cheapest way to do jobs
`start..n−1` in `daysLeft` days.)

### 3. What are the choices?
Where the last day starts: index `s` with `day − 1 ≤ s ≤ i` (each earlier day needs a job).

### 4. What is the recurrence?

```
dp[day][i] = min over s in [day−1, i] of   dp[day−1][s−1]  +  max(jobs[s..i])
                                           └ earlier days ┘    └ last day ┘
```

Scanning `s` from `i` downwards keeps `max(jobs[s..i])` in one running variable.

### 5. What are the base cases?
`dp[1][i] = max(jobs[0..i])`: one day does everything. If `n < d` the answer is `-1` (checked first).

### 6. What is the order of computation?
Row `day` reads only row `day − 1`, so loop `day = 2..d` outside, `i` inside.

### 7. Can the state space be optimized?
**Space:** two rows (`previous`, `current`) instead of `d × n`.
**Time:** the inner `min` over `s` can be computed with a monotonic stack (next section), turning
`O(n² d)` into `O(n d)`.

## Recursive solution

```java
int solve(int start, int daysLeft) {
    if (daysLeft == 1) return max(jobs[start..n-1]);
    int best = INF, hardestToday = 0;
    for (int end = start; end <= n - daysLeft; end++) {   // leave a job for each later day
        hardestToday = Math.max(hardestToday, jobs[end]);
        best = Math.min(best, hardestToday + solve(end + 1, daysLeft - 1));
    }
    return best;
}
```

## Memoization

`memo[start][daysLeft]`: `n · d` states × `O(n)` loop = `O(n² d)`, about 900 000 steps for the
limits. See [`solution/Memoization.java`](solution/Memoization.java).

## Tabulation

The `dp[day][i]` recurrence with two rolling rows: [`solution/Tabulation.java`](solution/Tabulation.java).

### The monotonic-stack speed-up ([`solution/Optimized.java`](solution/Optimized.java))

Fix a day. For the current `i`, let `p` be the nearest index left of `i` with `jobs[p] > jobs[i]`
(its "previous greater element"). Split the possible starts `s` into two groups:

```
          p                 i
  ... [ big ] . . . . . . [ i ]
  s ≤ p: the block s..i contains jobs[p] > jobs[i], so max(s..i) = max(s..p).
         These are exactly the blocks considered for index p → their best is current[p].
  p < s ≤ i: every job in s..i is ≤ jobs[i], so max(s..i) = jobs[i].
         Best = jobs[i] + min(previous[s−1] for p < s ≤ i).
```

So `current[i] = min( current[p],  jobs[i] + minPrev )`. A stack of indices with **decreasing**
job values finds `p` (pop everything `≤ jobs[i]`), and each stack entry also stores the
`minPrev` of the starts it "owns", so popping merges those minima into `i`'s. Every index is
pushed and popped once per day: `O(n)` per day.

## Space optimization

Only `dp[day−1]` is needed to compute `dp[day]`, so both the tabulation and the optimized version
keep two arrays of length `n`: `O(n)` instead of `O(n · d)`.

## Dry run

`jobs = [3, 1, 4, 2, 5]`, `d = 2`.

Day 1 (prefix maxima): `previous = [3, 3, 4, 4, 5]`.

Day 2, plain recurrence (`s` = start of day 2):

| i | candidates `previous[s−1] + max(jobs[s..i])` | current[i] |
|---|---|---|
| 1 | s=1: 3 + 1 = 4 | 4 |
| 2 | s=1: 3+4 = 7, s=2: 3+4 = 7 | 7 |
| 3 | s=1: 3+4 = 7, s=2: 3+4 = 7, s=3: 4+2 = 6 | 6 |
| 4 | s=1: 3+5, s=2: 3+5, s=3: 4+5, s=4: 4+5 | **8** |

Day 2 with the monotonic stack (stack entries are `(index, minPrev)`, job values decreasing):

| i | jobs[i] | start minPrev = previous[i−1] | popped (job ≤ jobs[i]) | minPrev after | p | current[i] | stack after |
|---|---|---|---|---|---|---|---|
| 1 | 1 | 3 | — | 3 | none | 3 + 1 = 4 | (1,3) |
| 2 | 4 | 3 | (1,3) | 3 | none | 3 + 4 = 7 | (2,3) |
| 3 | 2 | 4 | — | 4 | 2 | min(4+2, current[2]=7) = 6 | (2,3) (3,4) |
| 4 | 5 | 4 | (3,4), (2,3) | 3 | none | 3 + 5 = **8** | (4,3) |

Both give 8: day 1 = [3], day 2 = [1, 4, 2, 5].

## Complexity

| Approach | Time | Space | Reason |
|---|---|---|---|
| Brute force | O(C(n−1, d−1) · n) | O(d) | every set of cut points |
| Memoization | O(n² · d) | O(n · d) | n·d states, O(n) choices each |
| Tabulation | O(n² · d) | O(n) | same work, two rows |
| Monotonic stack | O(n · d) | O(n) | each index pushed/popped once per day |

## Why the optimized approach is better

For the LeetCode limits `O(n² d)` already passes, so the stack version is about **understanding**
a reusable trick: when a block cost is "max of the block", starts that share the same maximum can
be merged, and a monotonic stack does the merging. `Optimized.java` keeps the `O(n² d)` DP and
compares the two on 2000 random arrays with many ties.

## Common mistakes

- **Not leaving a job for each remaining day** (loop `end ≤ n − daysLeft`). Without it the DP builds
  schedules with empty days.
- **Forgetting the `-1` case** (`n < d`).
- **Popping only strictly smaller jobs.** Equal jobs must be popped too (`≤`), otherwise `p` is not
  the previous **greater** element and the `current[p]` shortcut is wrong.
- **Using `dp[day][i]` from the same row** inside the plain recurrence. Only row `day − 1` is valid
  there (the stack trick's `current[p]` is the deliberate exception, and it is already final).

## Key takeaway

"Split an ordered list into k contiguous groups, cost per group" → `dp[k][i] = min over the last
group's start`. When the group cost is a max or min, a monotonic stack removes the inner loop.

## Related problems

- [1478. Allocate Mailboxes](../1478-allocate-mailboxes/): the same partition DP with a median cost; sped up by divide and conquer.
- [132. Palindrome Partitioning II](https://leetcode.com/problems/palindrome-partitioning-ii/): partition DP without a fixed number of blocks.
- [1696. Jump Game VI](https://leetcode.com/problems/jump-game-vi/): monotonic-deque DP, taught in the [Monotonic Queue topic](../../../05-dp-optimization/04-monotonic-queue-optimization/).
