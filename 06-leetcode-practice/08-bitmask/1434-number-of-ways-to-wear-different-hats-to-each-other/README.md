# 1434. Number of Ways to Wear Different Hats to Each Other

> **Pattern:** Bitmask DP — iterate over the large set (hats), mask the small set (people) · **Difficulty:** Hard
> **Builds on:** [Bitmask DP](../../../04-advanced-dp-patterns/09-bitmask-dp/) and [0/1 Knapsack](../../../03-subsequence-and-knapsack-dp/02-zero-one-knapsack/) (each item used at most once, 1-D array iterated backwards)

---

## Problem overview

There are `n ≤ 10` people and 40 kinds of hats (numbered 1–40). `hats[i]` lists the hats person `i` likes. Count the ways to give every person exactly one hat they like so that no two people wear the same hat. Return the count modulo `10^9 + 7`.

**Why bitmask DP — and why the mask is over people, not hats.** The natural first idea is "go person by person and remember which hats are taken". That mask has 40 bits: `2^40 ≈ 10^12` states — hopeless. Flip the roles: go **hat by hat** (40 steps) and remember **which people already have a hat** — a 10-bit mask, `2^10 = 1024` states. The constraint "no two people share a hat" is automatic, because each hat is considered once and given to at most one person. Rule of thumb from [Bitmask DP](../../../04-advanced-dp-patterns/09-bitmask-dp/): **mask the small side, iterate the large side.**

## Link & metadata

| Field | Value |
|---|---|
| LeetCode | [1434. Number of Ways to Wear Different Hats to Each Other](https://leetcode.com/problems/number-of-ways-to-wear-different-hats-to-each-other/) |
| Difficulty | Hard |
| Pattern | Counting assignments; DP over (hat index, mask of people) |
| Prerequisite topics | [Bitmask DP](../../../04-advanced-dp-patterns/09-bitmask-dp/), [0/1 Knapsack](../../../03-subsequence-and-knapsack-dp/02-zero-one-knapsack/), [Space optimization basics](../../../00-foundations/15-space-optimization-basics/) |

## Examples

| hats | Answer | Why |
|---|---|---|
| [[3,4],[4,5],[5]] | 1 | person 2 must take 5, so person 1 takes 4, person 0 takes 3 |
| [[3,5,1],[3,5]] | 4 | (3,5), (5,3), (1,3), (1,5) |
| four people each liking [1,2,3,4] | 24 | `4!` |
| [[1],[1]] | 0 | both want the only hat |
| [[1]] | 1 | edge case |

## Brute-force intuition

Backtrack person by person, trying each liked hat that is still free. Worst case `40·39·…·31 ≈ 10^15` assignments. If we memoize on "which hats are taken", there are up to `2^40` keys. The state is too big on that side.

## Deriving the DP (7 questions)

### 1. What is changing?
The hat we are deciding about (1 → 40) and the set of people who already wear a hat.

### 2. What does the state represent?
`ways[h][mask]` = the number of ways to hand out hats among hats `1 … h` so that **exactly** the people in `mask` have a hat (each of them a liked hat, all distinct).

### 3. What are the choices?
For hat `h`: give it to nobody, or to one person `p` who likes `h` and does not have a hat yet.

### 4. What is the recurrence?
```
ways[h][mask] = ways[h-1][mask]                                         // hat h unused
              + Σ over p in mask who like hat h of ways[h-1][mask \ {p}] // hat h goes to p
```

### 5. What are the base cases?
`ways[0][∅] = 1` (no hats considered, nobody served: one empty assignment); `ways[0][mask ≠ ∅] = 0`. Answer: `ways[40][full]`.

### 6. What is the order of computation?
Hat layer `h` uses only layer `h − 1` → loop `h = 1 … 40` outside, masks inside (any order with two layers).

### 7. Can the state space be optimized?
Only the previous hat layer is read ⇒ one 1-D array over masks. Exactly like 0/1 knapsack, iterate masks **from large to small**: `mask \ {p}` is smaller than `mask`, so when we update `mask` the smaller masks still hold the previous hat's values — hat `h` cannot be given twice. See `Optimized.java`.

## Recursive solution

```java
long ways(int hat, int mask) {                  // mask = people who already wear a hat
    if (mask == full) return 1;                 // everyone served; the remaining hats stay unused
    if (hat > 40) return 0;
    long total = ways(hat + 1, mask);           // skip this hat
    for (int p : peopleWhoLike[hat])
        if ((mask & (1 << p)) == 0) total += ways(hat + 1, mask | (1 << p));
    return total % MOD;
}
```

## Memoization

Cache on `(hat, mask)`: `41 × 1024` entries. See [`solution/Memoization.java`](solution/Memoization.java). Pre-build `peopleWhoLike[hat]` — the input is per person, but our loop is per hat.

## Tabulation

The 2-D table `ways[h][mask]` from questions 2–6. See [`solution/Tabulation.java`](solution/Tabulation.java).

## Space optimization

```java
long[] ways = new long[1 << n]; ways[0] = 1;
for (int hat = 1; hat <= 40; hat++)
    for (int mask = full; mask > 0; mask--)            // large to small: like 0/1 knapsack
        for (int p : peopleWhoLike[hat])
            if ((mask & (1 << p)) != 0) ways[mask] = (ways[mask] + ways[mask ^ (1 << p)]) % MOD;
```
See [`solution/Optimized.java`](solution/Optimized.java).

## Dry run

`hats = [[3,5,1],[3,5]]` (person 0 = bit 0, person 1 = bit 1). 1-D array `[ways(00), ways(01), ways(10), ways(11)]` after each hat:

| Hat | who likes it | update (masks high → low) | array after |
|---|---|---|---|
| start | – | – | [1, 0, 0, 0] |
| 1 | P0 | 11 += ways(10)=0; 01 += ways(00)=1 | [1, 1, 0, 0] |
| 2 | nobody | – | [1, 1, 0, 0] |
| 3 | P0, P1 | 11 += ways(10)+ways(01) = 0+1; 10 += ways(00)=1; 01 += ways(00)=1 | [1, 2, 1, 1] |
| 4 | nobody | – | [1, 2, 1, 1] |
| 5 | P0, P1 | 11 += ways(10)+ways(01) = 1+2; 10 += 1; 01 += 1 | [1, 3, 2, **4**] |
| 6–40 | nobody | – | answer **4** |

## Complexity

| Approach | Time | Space | Why |
|---|---|---|---|
| Backtracking by person | up to 40!/30! | O(n) | every distinct assignment |
| Mask over hats | O(n · 2^40) | O(2^40) | infeasible |
| Memoization (mask over people) | O(40 · 2ⁿ · n) | O(40 · 2ⁿ) | 41·1024 states × ≤ n people per hat |
| Tabulation | O(40 · 2ⁿ · n) | O(40 · 2ⁿ) | same |
| 1-D optimized | O(40 · 2ⁿ · n) | O(2ⁿ) | one layer |

`40 · 1024 · 10 ≈ 4·10^5` steps.

## Why the optimized approach is better

Choosing the right side for the mask turns `2^40` into `2^10`. Then the knapsack-style backwards loop saves the 40× memory.

## Common mistakes

- Masking hats instead of people.
- In the 1-D version, looping masks upward: hat `h` could then be handed to two people.
- Forgetting the modulo after each addition, or using `int` sums before the modulo.
- Forgetting to invert the input into `peopleWhoLike[hat]`.

## Key takeaway

When an assignment has a **small side and a large side**, iterate over the large side and **mask the small side**. Each element of the large side is then used at most once automatically.

## Related problems

- [1125. Smallest Sufficient Team](../1125-smallest-sufficient-team/) — 60 people, 16 skills: mask the skills.
- [1879. Minimum XOR Sum of Two Arrays](../1879-minimum-xor-sum-of-two-arrays/) — both sides small, popcount gives the index.
- [0/1 Knapsack](../../../03-subsequence-and-knapsack-dp/02-zero-one-knapsack/) — why the 1-D loop runs backwards.
