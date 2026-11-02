# 1986. Minimum Number of Work Sessions to Finish the Tasks

> **Pattern:** Bitmask DP — "cover a set with the fewest groups" (submask enumeration, then a (sessions, fill) pair) · **Difficulty:** Medium
> **Builds on:** [Bitmask DP](../../../04-advanced-dp-patterns/09-bitmask-dp/) and [698. Partition to K Equal Sum Subsets](../698-partition-to-k-equal-sum-subsets/)

---

## Problem overview

You have up to 14 tasks with given durations and a work session length `sessionTime` (every task fits in one session). A task must be done inside a single session; a session may hold several tasks as long as their total is at most `sessionTime`. Find the minimum number of sessions to finish all tasks.

**Why bitmask DP.** This is bin packing — no greedy is correct, and the sessions are interchangeable. The future depends only on **which tasks are still left**, a 14-bit mask. Two natural formulations:
1. "Remove one whole session at a time": try every subset that fits in one session — submask enumeration, `O(3^n)`.
2. "Fill sessions one after another" (the trick from [698](../698-partition-to-k-equal-sum-subsets/)): keep the pair (sessions opened, fill of the last session) per mask — `O(2^n · n)`.
Both are standard tools of [Bitmask DP](../../../04-advanced-dp-patterns/09-bitmask-dp/).

## Link & metadata

| Field | Value |
|---|---|
| LeetCode | [1986. Minimum Number of Work Sessions to Finish the Tasks](https://leetcode.com/problems/minimum-number-of-work-sessions-to-finish-the-tasks/) |
| Difficulty | Medium |
| Pattern | Bitmask set-partition DP; submask enumeration |
| Prerequisite topics | [Bitmask DP](../../../04-advanced-dp-patterns/09-bitmask-dp/), [0/1 Knapsack](../../../03-subsequence-and-knapsack-dp/02-zero-one-knapsack/) (capacity thinking) |

**Submask enumeration** — to visit every non-empty subset `s` of `mask`:
```java
for (int s = mask; s > 0; s = (s - 1) & mask) { ... }
```
`(s − 1) & mask` is the next smaller number whose bits are all inside `mask`. Summed over all masks this visits `3^n` pairs (each task is: not in mask / in mask but not in s / in s).

## Examples

| tasks | sessionTime | Answer | One optimal packing |
|---|---|---|---|
| [1,2,3] | 3 | 2 | (1,2), (3) |
| [3,1,3,1,1] | 8 | 2 | (3,3,1,1), (1) |
| [1,2,3,4,5] | 15 | 1 | everything |
| [4,4,4,4] | 5 | 4 | each alone |
| [5] | 5 | 1 | edge case |

## Brute-force intuition

Assign each task to session 1 … n and keep the best valid assignment: `n^n`. The session labels are interchangeable, so most assignments are duplicates.

## Deriving the DP (7 questions)

### 1. What is changing?
The set of tasks already scheduled (or, equivalently, still left).

### 2. What does the state represent?
Formulation 1: `sessions[mask]` = the minimum number of sessions needed to finish **exactly** the tasks in `mask`.
Formulation 2: `best[mask]` = the lexicographically smallest pair `(opened, lastFill)` after scheduling the tasks in `mask` session by session.

### 3. What are the choices?
F1: which group `s ⊆ mask` (with total ≤ `sessionTime`) forms one session.
F2: which unscheduled task goes next — into the current session if it fits, else into a new one.

### 4. What is the recurrence?
```
F1: sessions[mask] = min over s ⊆ mask, s ≠ ∅, sum(s) ≤ sessionTime of sessions[mask \ s] + 1
F2: from (opened, fill) adding task t:
        fill + t ≤ sessionTime  ->  (opened, fill + t)
        otherwise               ->  (opened + 1, t)
    best[mask | t] = lexicographic min of all candidates
```
Why lexicographic is safe in F2: fewer sessions is always at least as good (one more session can absorb any future fill), and with equal sessions a lower fill leaves more room.

### 5. What are the base cases?
F1: `sessions[∅] = 0`. F2: `best[∅] = (1, 0)` — one empty session open (so the first task never opens a second one).

### 6. What is the order of computation?
Both read only masks that are subsets (numerically smaller) → loop `mask` increasing.

### 7. Can the state space be optimized?
F1 costs `O(3^n)` time (`4.8·10^6` for n = 14). F2 keeps the same `2^n` states but each does only `n` work: `O(2^n · n)` (`2.3·10^5`). That is our `Optimized.java`.

## Recursive solution

```java
int minSessions(int left) {                      // F1, top-down, over the tasks still left
    if (left == 0) return 0;
    int low = left & -left;                      // the lowest task MUST go in some session; put it in this one
    int best = INF;
    for (int s = left; s > 0; s = (s - 1) & left)
        if ((s & low) != 0 && sum[s] <= sessionTime)
            best = Math.min(best, 1 + minSessions(left ^ s));
    return best;
}
```
Forcing the lowest remaining task into the chosen session avoids trying the same partition in many orders.

## Memoization

Cache `minSessions(left)`. See [`solution/Memoization.java`](solution/Memoization.java).

## Tabulation

F1 bottom-up over increasing masks with submask enumeration; `sum[mask]` precomputed. See [`solution/Tabulation.java`](solution/Tabulation.java).

## Space optimization

F2 as described: two arrays `opened[mask]`, `fill[mask]`. See [`solution/Optimized.java`](solution/Optimized.java).

## Dry run

`tasks = [1,2,3]`, `sessionTime = 3`, F1 (masks shown as task values):

| mask | sessions that fit (`s`) → `sessions[mask \ s] + 1` | `sessions[mask]` |
|---|---|---|
| {} | base case | 0 |
| {1} | {1} → 0+1 | 1 |
| {2} | {2} → 1 | 1 |
| {1,2} | {1,2} → 1; {2} → 2; {1} → 2 | 1 |
| {3} | {3} → 1 | 1 |
| {1,3} | {3} → 2; {1} → 2 ({1,3} sums to 4, too big) | 2 |
| {2,3} | {3} → 2; {2} → 2 | 2 |
| {1,2,3} | {3} → `sessions[{1,2}]`+1 = 2; {1,2} → `sessions[{3}]`+1 = 2; others larger | **2** |

F2 on the same input, task order 1, 2, 3: `{1}` → (1,1); `{1,2}` → (1,3); `{1,2,3}` → 3 doesn't fit in fill 3 → (2,3). Answer `opened = 2`. ✔

## Complexity

| Approach | Time | Space | Why |
|---|---|---|---|
| Assign labels | O(nⁿ) | O(n) | every labelled assignment |
| F1 memo / table | O(3ⁿ) | O(2ⁿ) | every (mask, submask) pair |
| F2 pair DP | O(2ⁿ·n) | O(2ⁿ) | each mask tries n tasks |

## Why the optimized approach is better

`2^n · n` vs `3^n`: for `n = 14` that is ~230 thousand vs ~4.8 million steps. F1 is still worth knowing: submask enumeration is the general tool when "fill in order" is not valid.

## Common mistakes

- Greedy (largest first into the first session that fits) — fails on some inputs.
- F2 starting from `(0, 0)` and then comparing incorrectly; starting from `(1, 0)` keeps the rule simple.
- Submask loop `for (s = mask; s >= 0; ...)` — never terminates because `(0 − 1) & mask = mask`.

## Key takeaway

"Partition a small set into the fewest groups with a capacity" → bitmask DP. Use submask enumeration (`3^n`) in general, or "fill one group at a time" (`2^n · n`) when groups are interchangeable.

## Related problems

- [698. Partition to K Equal Sum Subsets](../698-partition-to-k-equal-sum-subsets/) — decision version with exact sums.
- [1125. Smallest Sufficient Team](../1125-smallest-sufficient-team/) — fewest items to cover a set of skills.
- [Bitmask DP](../../../04-advanced-dp-patterns/09-bitmask-dp/) and [SOS DP](../../../05-dp-optimization/07-sos-dp/) — subset/submask techniques.
