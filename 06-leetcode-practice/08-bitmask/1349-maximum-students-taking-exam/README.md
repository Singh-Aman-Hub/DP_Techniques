# 1349. Maximum Students Taking Exam

> **Pattern:** Row-profile bitmask DP ("broken profile" style): the state is the seating pattern of the previous row · **Difficulty:** Hard
> **Builds on:** [Bitmask DP](../../../04-advanced-dp-patterns/09-bitmask-dp/), [2D / grid DP](../../../02-two-dimensional-and-grid-dp/01-2d-dp-state-representation/) and [Rolling arrays](../../../05-dp-optimization/02-rolling-arrays/)

---

## Problem overview

A classroom is an `m × n` grid (`m, n ≤ 8`) of seats; `'#'` is broken, `'.'` is usable. A student can copy from the seats directly **left**, **right**, **upper-left** and **upper-right** of them (not directly in front or behind). Seat the maximum number of students so that nobody can copy from anyone.

**Why row-profile bitmask DP.** All conflicts are either inside one row (left/right) or between **two consecutive rows** (diagonals). So if we fill the room row by row, the only thing the next row needs to know is *which seats of the previous row are occupied* — an `n`-bit pattern called a **profile**. With `n ≤ 8` there are at most 256 profiles per row. This is [Bitmask DP](../../../04-advanced-dp-patterns/09-bitmask-dp/) applied to a grid: the row index is the "position" like in [grid DP](../../../02-two-dimensional-and-grid-dp/01-2d-dp-state-representation/), and the mask describes a whole row.

## Link & metadata

| Field | Value |
|---|---|
| LeetCode | [1349. Maximum Students Taking Exam](https://leetcode.com/problems/maximum-students-taking-exam/) |
| Difficulty | Hard |
| Pattern | DP over rows with the previous row's occupancy mask |
| Prerequisite topics | [Bitmask DP](../../../04-advanced-dp-patterns/09-bitmask-dp/), [2D DP state representation](../../../02-two-dimensional-and-grid-dp/01-2d-dp-state-representation/), [Rolling arrays](../../../05-dp-optimization/02-rolling-arrays/) |

Bit `j` of a mask = "column `j` of this row is occupied". Three checks, all bit operations:

| Check | Code | Meaning |
|---|---|---|
| only good seats | `(mask & ~goodSeats[row]) == 0` | no student sits on a broken seat |
| no left/right neighbours | `(mask & (mask >> 1)) == 0` | no two adjacent bits are both 1 |
| no diagonal copying | `(mask & (prev << 1)) == 0 && (mask & (prev >> 1)) == 0` | nobody above-left or above-right |

## Examples

```
Example A                       Example B (5 rows x 2 cols)
 # . # # . #                     . #
 . # # # # .                     # #
 # . # # . #                     # .
answer 4 (the four '.' in        # #
rows 0 and 2)                    . #     answer 3
```
Example C: a 5×5 room `#...#`, `.#.#.`, `..#..`, `.#.#.`, `#...#` → **10**.
Edge cases: a single broken seat → `0`; an 8×8 room with no broken seats → `32` (every row takes columns 0, 2, 4, 6; diagonals never clash because the same columns repeat).

## Brute-force intuition

Every usable seat is occupied or not: `2^(m·n)`, up to `2^64` subsets. But a row's choice interacts only with the row directly above and below — the rest of the history is irrelevant.

## Deriving the DP (7 questions)

### 1. What is changing?
The row we are filling, and the seating pattern of the row above it.

### 2. What does the state represent?
`best[row][mask]` = the maximum number of students in rows `0 … row` when row `row` is seated exactly as `mask` (and everything so far is cheat-free).
Top-down equivalent: `fill(row, prevMask)` = the most students we can still seat in rows `row … m−1` if the row above is seated as `prevMask`.

### 3. What are the choices?
The seating pattern `mask` of the current row: any **valid** mask (only good seats, no adjacent bits) that is **compatible** with the previous row (no diagonal neighbours).

### 4. What is the recurrence?
```
best[row][mask] = bitCount(mask) + max over prev compatible with mask of best[row-1][prev]
answer          = max over mask of best[m-1][mask]
```

### 5. What are the base cases?
Row 0 has no row above: `best[0][mask] = bitCount(mask)` for every valid mask (equivalently, the previous row is the empty pattern `0`). Invalid masks are `−∞` (never used).

### 6. What is the order of computation?
Row `r` reads only row `r − 1` → rows top to bottom; masks in any order.

### 7. Can the state space be optimized?
Only the previous row's values are needed → two arrays of size `2^n` (rolling). Time can also be cut by precomputing the list of valid masks per row (`(mask & (mask>>1)) == 0` leaves only 55 of the 256 masks for `n = 8`).

## Recursive solution

```java
int fill(int row, int prevMask) {
    if (row == m) return 0;
    int best = 0;
    for (int mask = 0; mask < (1 << n); mask++)
        if (valid(row, mask) && compatible(mask, prevMask))
            best = Math.max(best, Integer.bitCount(mask) + fill(row + 1, mask));
    return best;
}
```

## Memoization

Cache `fill(row, prevMask)`: `m × 2^n ≤ 8 × 256` entries. See [`solution/Memoization.java`](solution/Memoization.java).

## Tabulation

`best[row][mask]` filled top to bottom, `−1` marking impossible masks. See [`solution/Tabulation.java`](solution/Tabulation.java).

## Space optimization

Keep `previous[]` and `current[]` of size `2^n` and swap after each row. See [`solution/Optimized.java`](solution/Optimized.java).

## Dry run

Example B (`n = 2`, bit 0 = left column). Good-seat masks per row: `01, 00, 10, 00, 01`.

| Row | valid masks | `best[row][mask]` | reasoning |
|---|---|---|---|
| 0 | 00, 01 | 00 → 0, 01 → 1 | base case: bitCount |
| 1 | 00 | 00 → 1 | max(prev 00 = 0, prev 01 = 1) |
| 2 | 00, 10 | 00 → 1, 10 → 2 | 10 vs prev 00: compatible, 1 + 1 |
| 3 | 00 | 00 → 2 | max(1, 2) |
| 4 | 00, 01 | 00 → 2, 01 → **3** | 01 vs prev 00: 1 + 2 |

Answer `max(2, 3) = 3`.

## Complexity

| Approach | Time | Space | Why |
|---|---|---|---|
| All seat subsets | O(2^(m·n)) | O(1) | every subset of seats |
| Memoization | O(m · 4ⁿ) | O(m · 2ⁿ) | m·2ⁿ states, each tries 2ⁿ masks |
| Tabulation | O(m · 4ⁿ) | O(m · 2ⁿ) | for every row, every (mask, prev) pair |
| Rolling arrays | O(m · 4ⁿ) | O(2ⁿ) | two rows alive |

`8 · 256 · 256 ≈ 5·10^5` checks.

## Why the optimized approach is better

Conflicts span only two adjacent rows, so the state is one row's profile instead of the whole grid: `2^64` → `8 · 2^8`.

## Common mistakes

- Forgetting that directly in front/behind is **allowed** — only the diagonals matter between rows.
- Mixing up the shift directions; checking both `prev << 1` and `prev >> 1` covers both diagonals regardless.
- Building `goodSeats` with the wrong bit order and then validating against a mirrored row (be consistent: bit `j` = column `j`).
- Starting `best` of invalid masks at 0 instead of "impossible".

## Key takeaway

When constraints connect only neighbouring rows of a narrow grid, make the **previous row's pattern** the state (row-profile DP). Bit operations check a whole row in O(1).

## Related problems

- [Bitmask DP](../../../04-advanced-dp-patterns/09-bitmask-dp/) — subset states and bit tricks.
- [House Robber](../../../01-one-dimensional-dp/06-house-robber/) — the "no two adjacent" rule in one dimension; here it is a single bit test per row.
- [1434. Number of Ways to Wear Different Hats to Each Other](../1434-number-of-ways-to-wear-different-hats-to-each-other/) — another layer-by-layer bitmask DP with a rolling array.
