# 983. Minimum Cost For Tickets

> **Pattern:** 1D minimum-cost DP over time · **Builds on:** [Minimum Cost Climbing Stairs](../../../01-one-dimensional-dp/03-minimum-cost-climbing-stairs/) · **Difficulty:** Medium

## Problem overview

You will travel on certain days of one year (a sorted list of day numbers from 1 to 365). Passes
are sold for 1 day, 7 consecutive days and 30 consecutive days at given prices. Return the
cheapest way to have a valid pass on every travel day.

## Link & metadata

| Field | Value |
|---|---|
| LeetCode | [983. Minimum Cost For Tickets](https://leetcode.com/problems/minimum-cost-for-tickets/) |
| Difficulty | Medium |
| Pattern | 1D "minimum cost to reach day d" DP with three look-back distances (1, 7, 30) |
| Prerequisite topic | [`01-one-dimensional-dp/03-minimum-cost-climbing-stairs`](../../../01-one-dimensional-dp/03-minimum-cost-climbing-stairs/) |
| Why this pattern | Like climbing stairs with step sizes 1 and 2, here a "step" back in time is 1, 7 or 30 days, each with a price |

## Examples

| days | costs | Answer | Why |
|---|---|---|---|
| `[1,4,6,7,8,20]` | `[2,7,15]` | 11 | 1-day on day 1 (2), 7-day on day 4 covering 4–10 (7), 1-day on day 20 (2) |
| `[1,2,…,10,30,31]` | `[2,7,15]` | 17 | 30-day on day 1 (15), 1-day on day 31 (2) |
| `[1]` | `[2,7,15]` | 2 | a single day |

## Brute-force intuition

On each uncovered travel day you must buy something: 3 choices. Each choice covers some
following days, then you recurse on the next uncovered day. With up to 365 days that is up to
3³⁶⁵ paths. But the future depends only on **which travel day is the first uncovered one**, so
there are at most 365 distinct subproblems.

## Deriving the DP (7 questions)

### 1. What is changing?
The current calendar day `d` (tabulation), or the index `i` of the first uncovered travel day
(memoization). Both work; they look at the same timeline from opposite ends.

### 2. What does the state represent?
`dp[d]` = the minimum money spent so that every travel day **≤ d** is covered.

### 3. What are the choices?
If `d` is not a travel day, there is nothing to decide. If it is, the pass covering day `d` was
bought so that it ends on or after `d`. Think of it as having been bought to cover the window
ending at `d`:
- a 1-day pass covering `[d, d]`,
- a 7-day pass covering `[d-6, d]`,
- a 30-day pass covering `[d-29, d]`.

### 4. What is the recurrence?
```
not a travel day:  dp[d] = dp[d-1]
travel day:        dp[d] = min( dp[d-1]  + cost1,
                                dp[d-7]  + cost7,
                                dp[d-30] + cost30 )      // negative index → 0
```
Each term says "pay for the last pass, and everything before its window was already handled".

### 5. What are the base cases?
`dp[0] = 0`: before day 1 nothing needs covering. Any index below 0 also means "before the
year started", so it is 0 too.

### 6. What is the order of computation?
`dp[d]` reads only smaller days, so loop `d = 1 … lastTravelDay` upward.

### 7. Can the state space be optimized?
The furthest look-back is 30 days, so only the last 30 values are ever read. A circular buffer of
size 30 (`dp[d % 30]`) is enough. The space then no longer depends on the calendar length.

## Recursive solution

The memoized version walks over travel-day indices instead of calendar days:

```java
static int cover(int i) {                         // cheapest way to cover days[i..]
    if (i == days.length) return 0;
    int best = Integer.MAX_VALUE;
    for (int t = 0; t < 3; t++) {
        int j = i;
        while (j < days.length && days[j] < days[i] + DURATION[t]) j++;   // first day not covered
        best = Math.min(best, costs[t] + cover(j));
    }
    return best;
}
```

## Memoization

Cache `cover(i)`. See [`solution/Memoization.java`](solution/Memoization.java). There are `n`
states; each scans forward at most 30 travel days per pass type.

## Tabulation

Calendar version, as derived above. See [`solution/Tabulation.java`](solution/Tabulation.java).
A `boolean[] isTravel` marks travel days so the loop can check each day in O(1).

## Space optimization

Replace `dp[0..365]` with `dp[30]` indexed by `d % 30`. Reading `dp[(d-30) % 30]` happens before
writing `dp[d % 30]`, which is the same slot, so the old value is still there when we need it.
See [`solution/Optimized.java`](solution/Optimized.java).

## Dry run

`days = [1,4,6,7,8,20]`, `costs = [2,7,15]`. Non-travel days just copy the previous value.

| d | travel? | dp[d-1]+2 | dp[d-7]+7 | dp[d-30]+15 | dp[d] |
|---|---|---|---|---|---|
| 0 | — | — | — | — | 0 |
| 1 | yes | 0+2 = 2 | 0+7 = 7 | 0+15 = 15 | 2 |
| 2–3 | no | | | | 2 |
| 4 | yes | 2+2 = 4 | 0+7 = 7 | 15 | 4 |
| 5 | no | | | | 4 |
| 6 | yes | 4+2 = 6 | 0+7 = 7 | 15 | 6 |
| 7 | yes | 6+2 = 8 | dp[0]+7 = **7** | 15 | 7 |
| 8 | yes | 7+2 = 9 | dp[1]+7 = 9 | 15 | 9 |
| 9–19 | no | | | | 9 |
| 20 | yes | 9+2 = **11** | dp[13]+7 = 16 | 0+15 = 15 | **11** |

At day 7, the 7-day pass covering days 1–7 costs 7 and beats three single tickets. Then day 8
adds one more single ticket. Total 11.

## Complexity

Let `n` = number of travel days, `D` = last travel day (≤ 365).

| Approach | Time | Space | Reason |
|---|---|---|---|
| Brute force | O(3ⁿ) | O(n) | three passes to try per uncovered day |
| Memoization | O(n · 30) | O(n) | n states; each scans at most 30 days ahead per pass |
| Tabulation | O(D) | O(D) | one loop over the calendar, O(1) per day |
| Optimized | O(D) | O(1) | 30-slot buffer, independent of input size |

## Why the optimized approach is better

It keeps the simple calendar loop but bounds memory by the longest pass (30), not the calendar.
The same idea applies if the "calendar" were millions of days long.

## Common mistakes

- **Buying a pass only on travel days and moving forward from it, but forgetting the pass
  covers non-travel days too.** The windows are calendar windows, not "the next 7 travel days".
- **Negative index.** `dp[d-30]` for `d < 30` must be treated as 0.
- **Assuming the 30-day pass is always best for busy months.** Prices are arbitrary; always take
  the min of all three.

## Key takeaway

"Minimum cost with several step sizes" generalizes climbing stairs. The step sizes here are
measured in time, and the look-back is bounded by the largest step.

## Related problems

- [746. Min Cost Climbing Stairs](https://leetcode.com/problems/min-cost-climbing-stairs/), taught in [`03-minimum-cost-climbing-stairs`](../../../01-one-dimensional-dp/03-minimum-cost-climbing-stairs/)
- [322. Coin Change](https://leetcode.com/problems/coin-change/), minimum cost with step sizes = coin values
- Previous: [2140. Solving Questions With Brainpower](../2140-solving-questions-with-brainpower/) · Next: [139. Word Break](../139-word-break/)
