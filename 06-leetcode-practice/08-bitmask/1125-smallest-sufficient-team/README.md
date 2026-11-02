# 1125. Smallest Sufficient Team

> **Pattern:** Bitmask DP over covered skills (set cover with a small universe) + path reconstruction · **Difficulty:** Hard
> **Builds on:** [Bitmask DP](../../../04-advanced-dp-patterns/09-bitmask-dp/) and [Unbounded Knapsack / Coin Change](../../../03-subsequence-and-knapsack-dp/08-coin-change/) ("fewest items to reach a target")

---

## Problem overview

There are up to 16 required skills and up to 60 people; each person has some of the skills. Choose the smallest team such that every required skill is covered by at least one member. Return the indices of the chosen people (any smallest team is accepted). A solution is guaranteed to exist.

**Why bitmask DP — and over skills, not people.** Masking the people would mean `2^60` states: impossible. But the *goal* is about skills, and there are at most 16 of them. So the state is **the set of skills covered so far** (≤ `2^16 = 65536`), and each person is an "item" that ORs their skill mask into it. It's like [Coin Change](../../../03-subsequence-and-knapsack-dp/08-coin-change/) ("fewest coins to reach an amount"), except "adding" a coin is a bitwise OR instead of `+`. Choosing the small dimension for the mask is the central design decision of [Bitmask DP](../../../04-advanced-dp-patterns/09-bitmask-dp/).

## Link & metadata

| Field | Value |
|---|---|
| LeetCode | [1125. Smallest Sufficient Team](https://leetcode.com/problems/smallest-sufficient-team/) |
| Difficulty | Hard |
| Pattern | Bitmask DP over the target set; parent pointers to rebuild the answer |
| Prerequisite topics | [Bitmask DP](../../../04-advanced-dp-patterns/09-bitmask-dp/), [Coin Change](../../../03-subsequence-and-knapsack-dp/08-coin-change/), [State representation](../../../00-foundations/09-state-representation/) |

`skillMask[p]` = OR of `1 << indexOf(skill)` over person `p`'s skills. `covered | skillMask[p]` = skills covered after adding person `p`.

## Examples

Example A: skills `java, nodejs, reactjs`; people `0: [java]`, `1: [nodejs]`, `2: [nodejs, reactjs]` → team **[0, 2]** (size 2).

Example B: skills `algorithms, math, java, reactjs, csharp, aws`; people
`0: [algorithms, math, java]`, `1: [algorithms, math, reactjs]`, `2: [java, csharp, aws]`, `3: [reactjs, csharp]`, `4: [csharp, math]`, `5: [aws, java]` → team **[1, 2]**.

Edge case: one skill, one person who has it → `[0]`.

## Brute-force intuition

Try all `2^60` subsets of people. Even trying all teams of size ≤ 16 is astronomically many. But two partial teams that cover **the same skills** are interchangeable for the future; keep only the smaller one.

## Deriving the DP (7 questions)

### 1. What is changing?
The set of skills covered so far.

### 2. What does the state represent?
`teamSize[mask]` = the minimum number of people whose combined skills are **exactly** `mask` (bottom-up version). We also remember `parentMask[mask]` and `lastPerson[mask]` to rebuild the team.
Top-down: `need(covered)` = the fewest additional people required to cover all skills, starting from `covered`.

### 3. What are the choices?
Which person to add next. Top-down refinement: the **lowest missing skill** must be covered by someone eventually, so only people who have that skill need to be tried — this prunes heavily without losing optimality.

### 4. What is the recurrence?
```
bottom-up (push):  teamSize[mask | skillMask[p]] = min( itself, teamSize[mask] + 1 )
top-down:          need(covered) = 1 + min over persons p having the lowest missing skill of need(covered | skillMask[p])
```

### 5. What are the base cases?
`teamSize[0] = 0` (nobody needed for no skills). Top-down: `need(all) = 0`.

### 6. What is the order of computation?
`mask | x ≥ mask` numerically, so iterate masks in increasing order; a person who adds nothing (`mask | x == mask`) is skipped.

### 7. Can the state space be optimized?
`2^16` states with two small ints each is already small. Pre-processing helps time: drop people whose skills are a subset of someone else's. Not needed for correctness, so no `Optimized.java`.

## Recursive solution

```java
int need(int covered) {
    if (covered == all) return 0;
    int missing = Integer.numberOfTrailingZeros(~covered);   // index of the lowest missing skill
    int best = INF;
    for (int p = 0; p < people; p++)
        if ((skillMask[p] >> missing & 1) == 1)
            best = Math.min(best, 1 + need(covered | skillMask[p]));
    return best;
}
```

## Memoization

Cache `need(covered)` and also record which person achieved it (`choice[covered]`); walk the choices from `0` to rebuild the team. See [`solution/Memoization.java`](solution/Memoization.java).

## Tabulation

Push-style over increasing masks with parent pointers; at the end walk back from the full mask:
```
mask = all
while mask != 0: team.add(lastPerson[mask]); mask = parentMask[mask]
```
See [`solution/Tabulation.java`](solution/Tabulation.java).

## Space optimization

Not applicable — every mask may be an intermediate state, and parents must be kept to rebuild the team.

## Dry run

Example A (bit 0 = java, bit 1 = nodejs, bit 2 = reactjs; person masks `p0 = 001`, `p1 = 010`, `p2 = 110`):

| mask | `teamSize` | pushes (`mask | person` → size) | updated |
|---|---|---|---|
| 000 | 0 | p0 → 001 (1), p1 → 010 (1), p2 → 110 (1) | all three set |
| 001 | 1 | p1 → 011 (2), p2 → 111 (2) | 011 = 2 (parent 001, p1); 111 = 2 (parent 001, p2) |
| 010 | 1 | p0 → 011 (2), p2 → 110 (2) | no improvement |
| 011 | 2 | p2 → 111 (3) | no improvement |
| 110 | 1 | p0 → 111 (2) | not smaller than 2 |
| 111 | **2** | – | rebuild: 111 ← p2 ← 001 ← p0 ← 000 → **[0, 2]** |

## Complexity

| Approach | Time | Space | Why |
|---|---|---|---|
| All subsets of people | O(2^p) | O(p) | p = up to 60 people |
| Memoization (lowest-missing-skill pruning) | O(2^s · p) | O(2^s) | 2^s skill masks, each tries ≤ p people |
| Tabulation | O(2^s · p) | O(2^s) | 65536 × 60 ≈ 4·10^6 |

## Why the optimized approach is better

Masking the **16 skills** instead of the **60 people** is the difference between impossible and instant.

## Common mistakes

- Making the mask over people.
- Forgetting to store parent information — then you know the size but not the team.
- Iterating masks downward in push style (targets would be processed before they are final).

## Key takeaway

In bitmask DP, **mask the smaller set**. The "items" (people) are iterated, the "goal" (skills) is masked. Rebuild the answer with parent pointers.

## Related problems

- [Coin Change](../../../03-subsequence-and-knapsack-dp/08-coin-change/) — fewest items to reach a target with `+` instead of OR.
- [1434. Number of Ways to Wear Different Hats to Each Other](../1434-number-of-ways-to-wear-different-hats-to-each-other/) — another "which side do I mask?" problem.
- [1986. Minimum Number of Work Sessions to Finish the Tasks](../1986-minimum-number-of-work-sessions-to-finish-the-tasks/) — fewest groups covering a set.
