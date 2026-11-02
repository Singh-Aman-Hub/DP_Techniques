# House Robber — Walkthrough

## 1. Intuition

You are a (fictional!) robber on a street. Each house has a known amount of cash. The houses' alarms are linked to their neighbours: robbing two houses next to each other triggers the alarm. You want the largest total haul.

Standing in front of the last house, you have the same dilemma as in the [previous topic](../05-maximum-sum-of-non-adjacent-elements/walkthrough.md#1-intuition):

- **skip it**, and your haul is the best haul from the street without this house; or
- **rob it**, and you take its cash, but you must have skipped its neighbour, so your haul is this cash plus the best haul from the street without the last **two** houses.

Pick the larger. Nothing else is needed.

## 2. Problem statement in our own words

You get an array `nums` where `nums[i] ≥ 0` is the money in house `i`. Choose houses to rob so that no two chosen houses are adjacent, and return the maximum total money.

Original: [LeetCode 198 · House Robber](https://leetcode.com/problems/house-robber/) (there `1 ≤ n ≤ 100`, `0 ≤ nums[i] ≤ 400`).

### Mapping the story onto the pattern

This is the habit to build. Write the mapping explicitly:

| Story | Pattern from topic 05 |
|-------|-----------------------|
| house `i` | element `arr[i]` |
| money in house `i` | value `arr[i]` |
| "alarm if two adjacent houses are robbed" | "no two chosen indices adjacent" |
| total money stolen | sum of the selection |
| maximize the haul | maximize the sum |
| rob nothing (allowed) | empty selection (allowed) |

Every row matches, so the recurrence is the one we already proved. The only difference: amounts are **never negative**. That makes "skip" less important (you'd never skip a house just because of its own value), but skipping is still essential because of the neighbour rule.

## 3. Examples

| `nums` | Output | Houses robbed |
|--------|--------|---------------|
| `[1, 2, 3, 1]` | 4 | 0 and 2 → 1 + 3 |
| `[2, 1, 1, 2]` | 4 | 0 and 3 → 2 + 2 |
| `[5]` | 5 | 0 |
| `[0, 0]` | 0 | none (or either) |
| `[2, 7, 9, 3, 1]` | 12 | 0, 2, 4 → 2 + 9 + 1 |

**Running example: `nums = [2, 7, 9, 3, 1]`, answer `12`.** It shows that the most valuable house to start with (7) is not part of the best plan.

`[2, 1, 1, 2]` is a classic trap: "rob all even-indexed houses or all odd-indexed houses" gives 3, but robbing the two ends gives 4.

## 4. Deriving the solution — the 7 questions

### Q1. What is changing?

The last house we still have to decide about, `i`.

### Q2. What does the state represent?

> `maxLoot(i)` = the largest amount that can be robbed from houses `0..i` without robbing two adjacent houses.

### Q3. What are the choices?

Skip house `i`, or rob house `i` (which forces house `i-1` to be skipped).

### Q4. What is the recurrence?

```
maxLoot(i) = max( maxLoot(i-1),             ← skip house i
                  nums[i] + maxLoot(i-2) )  ← rob house i
```

### Q5. What are the base cases?

```
maxLoot(i) = 0   for i < 0      (no houses → no money)
```

From this, `maxLoot(0) = nums[0]` and `maxLoot(1) = max(nums[0], nums[1])` follow automatically. Many solutions write those two as base cases instead; that is equivalent, just more code.

### Q6. What is the order of computation?

Left to right, `i = 0..n-1`.

### Q7. Can the state space be optimized?

Two variables, `O(1)` space.

## 5. Brute force: recursion

```java
private static int maxLoot(int[] nums, int i) {
    if (i < 0) return 0;
    int skip = maxLoot(nums, i - 1);
    int robIt = nums[i] + maxLoot(nums, i - 2);
    return Math.max(skip, robIt);
}
```
Full file: [`solution/BruteForce.java`](solution/BruteForce.java).

### Recursion tree for `[2, 7, 9, 3, 1]` (top three levels)

```
                               L(4) [12]
                 skip ┌───────────┴───────────┐ rob +1
                    L(3) [11]               L(2) [11]
           skip ┌──────┴──────┐ rob +3   skip ┌─┴─┐ rob +9
              L(2) [11]     L(1) [7]      L(1)   L(0)
            ┌───┴───┐       ┌──┴──┐       [7]    [2]
          L(1)    L(0)    L(0)  L(-1)
          [7]     [2]     [2]    [0]
           …
```

`L(2)` already appears twice, `L(1)` three times. With 100 houses the brute force makes about `1.618^100 ≈ 8 × 10^20` calls. It will never finish.

## 6. Memoization (top-down)

```java
private static int[] memo;   // memo[i] = maxLoot(i); -1 = not computed

private static int maxLoot(int[] nums, int i) {
    if (i < 0) return 0;
    if (memo[i] != -1) return memo[i];
    memo[i] = Math.max(maxLoot(nums, i - 1), nums[i] + maxLoot(nums, i - 2));
    return memo[i];
}
```
Full file: [`solution/Memoization.java`](solution/Memoization.java). Since amounts are non-negative, every answer is `≥ 0`, so `-1` is a safe "not computed" marker here (compare with the `Long[]`/`null` used in topic 05 when values could be negative).

| Order finished | State | skip | rob | Stored |
|----------------|-------|------|-----|--------|
| 1 | `L(0)` | 0 | 2 + 0 = 2 | 2 |
| 2 | `L(1)` | 2 | 7 + 0 = 7 | 7 |
| 3 | `L(2)` | 7 | 9 + **L(0)=2** (hit) = 11 | 11 |
| 4 | `L(3)` | 11 | 3 + **L(1)=7** (hit) = 10 | 11 |
| 5 | `L(4)` | 11 | 1 + **L(2)=11** (hit) = 12 | **12** |

## 7. Tabulation (bottom-up)

```java
int[] maxLoot = new int[n];       // maxLoot[i] = best from houses 0..i
for (int i = 0; i < n; i++) {
    int skip = (i >= 1) ? maxLoot[i - 1] : 0;
    int robIt = nums[i] + ((i >= 2) ? maxLoot[i - 2] : 0);
    maxLoot[i] = Math.max(skip, robIt);
}
return maxLoot[n - 1];
```
Full file: [`solution/Tabulation.java`](solution/Tabulation.java).

```
 house i       0    1    2    3    4
 nums          2    7    9    3    1
 ─────────────────────────────────────
 i=0: max(0,  2+0)          →  2
 i=1: max(2,  7+0)          →  7
 i=2: max(7,  9+2)          → 11
 i=3: max(11, 3+7)          → 11
 i=4: max(11, 1+11)         → 12
 ─────────────────────────────────────
 maxLoot       2    7   11   11   12     ← answer 12
```

Walking back (as in topic 05): `12 ≠ 11` → rob 4, go to 2; `11 ≠ 7` → rob 2, go to 0; `2 ≠ 0` → rob 0. Houses {0, 2, 4}.

### A second formulation: two states per house

Another way to think: at each house, the robber is in one of two **statuses**: "I robbed this house" or "I skipped this house". Keep the best loot for each status:

> `robbed[i]` = best loot from houses `0..i` **given that house `i` is robbed**.
> `skipped[i]` = best loot from houses `0..i` **given that house `i` is skipped**.

Transitions, read as "which status may come right before this one?":

```
robbed[i]  = skipped[i-1] + nums[i]              a robbed house must follow a skipped one
skipped[i] = max(robbed[i-1], skipped[i-1])      a skipped house may follow either

base: robbed[0] = nums[0],  skipped[0] = 0
answer: max(robbed[n-1], skipped[n-1])
```

As a picture (a **state machine**: circles are statuses, arrows are allowed moves from house `i-1` to house `i`):

```
             +nums[i]
   ┌─────────┐ ───────────────▶ ┌─────────┐
   │ SKIPPED │                  │ ROBBED  │
   └─────────┘ ◀─────────────── └─────────┘
     │    ▲         +0
     └────┘ +0                   (no ROBBED → ROBBED arrow: that would be two adjacent robberies)
```

Filled for `[2, 7, 9, 3, 1]`:

| i | nums[i] | robbed[i] = skipped[i-1] + nums[i] | skipped[i] = max(robbed[i-1], skipped[i-1]) |
|---|---------|---------------------|---------------------|
| 0 | 2 | 2 | 0 |
| 1 | 7 | 0 + 7 = 7 | max(2, 0) = 2 |
| 2 | 9 | 2 + 9 = 11 | max(7, 2) = 7 |
| 3 | 3 | 7 + 3 = 10 | max(11, 7) = 11 |
| 4 | 1 | 11 + 1 = 12 | max(10, 11) = 11 |

Answer `max(12, 11) = 12`. Notice `max(robbed[i], skipped[i])` equals `maxLoot[i]` in every row (2, 7, 11, 11, 12): the two formulations are the same DP, sliced differently. `robTwoStates` in the tabulation file implements it.

Why learn both? The one-state version is shorter here. The two-state version **scales**: when a problem has more statuses (three activities in AtCoder Vacation, holding/not holding a stock, cooldown days), you simply add a row per status and an arrow per allowed transition. See [State-machine DP](../../04-advanced-dp-patterns/12-state-machine-dp/).

## 8. Space optimization

```java
int lootTwoBack = 0, lootOneBack = 0;
for (int money : nums) {
    int current = Math.max(lootOneBack, money + lootTwoBack);
    lootTwoBack = lootOneBack;
    lootOneBack = current;
}
return lootOneBack;
```
Full file: [`solution/Optimized.java`](solution/Optimized.java). The two-state version can also be reduced to two variables (`robbed`, `skipped`), since row `i` only reads row `i-1`.

## 9. Dry run on a full example

`nums = [2, 7, 9, 3, 1]`, optimized version:

| money | skip = lootOneBack | rob = money + lootTwoBack | current | lootTwoBack | lootOneBack |
|-------|-----|-----|----|----|----|
| start | — | — | — | 0 | 0 |
| 2 | 0 | 2 | 2 | 0 | 2 |
| 7 | 2 | 7 | 7 | 2 | 7 |
| 9 | 7 | 11 | 11 | 7 | 11 |
| 3 | 11 | 10 | 11 | 11 | 11 |
| 1 | 11 | 12 | 12 | 11 | 12 |

Answer **12**.

## 10. Complexity analysis

| Approach | Time | Space | Why |
|----------|------|-------|-----|
| Brute force | `O(2^n)` | `O(n)` | Two branches per house; stack depth `n`. |
| Memoization | `O(n)` | `O(n)` | `n` states × `O(1)`. |
| Tabulation (either form) | `O(n)` | `O(n)` | One pass; one or two arrays of length `n`. |
| Optimized | `O(n)` | `O(1)` | Two variables. |

The largest possible answer is `50 × 400 = 20,000`, so `int` is plenty.

## 11. Why this approach works

It is the proof from [topic 05](../05-maximum-sum-of-non-adjacent-elements/walkthrough.md#11-why-this-approach-works): every valid plan for houses `0..i` either robs house `i` (then it skips `i-1` and its remaining part is a valid plan for `0..i-2`) or does not (then it is a valid plan for `0..i-1`). Taking the best of the best in each group gives the best overall. For the two-state version, the same split is made explicit: `robbed[i]` is the best plan in the first group, `skipped[i]` the best in the second.

## 12. Common mistakes

1. **Even/odd shortcut.** `[2, 1, 1, 2]` breaks it (3 instead of 4).
2. **Assuming `maxLoot[1] = nums[1]`.** It is `max(nums[0], nums[1])`: you can rob house 0 instead.
3. **`n = 1` crashes** when code sets `dp[1]` unconditionally. Guard it, or use the "negative index = 0" style used here.
4. **Two-state version: `robbed[i] = robbed[i-1] + nums[i]`.** That allows two adjacent robberies. The arrow into ROBBED must come from SKIPPED only.
5. **Returning `robbed[n-1]`** instead of the max of both statuses: the last house might be better left alone.

## 13. When to use this technique / how to recognize similar problems

When a story boils down to "choose items in a row; choosing one forbids its neighbour(s); maximize the total", it is House Robber. Variants change one piece:

| Variant | What changes |
|---------|--------------|
| Houses in a circle | First and last are neighbours → [House Robber II](../07-house-robber-ii/) |
| Houses in a binary tree | Parent and child are neighbours → [House Robber III](https://leetcode.com/problems/house-robber-iii/), see [DP on trees](../../04-advanced-dp-patterns/07-dp-on-trees/) |
| Equal values merged into buckets | Preprocess, then House Robber → [Delete and Earn](https://leetcode.com/problems/delete-and-earn/) |
| More than two statuses per index | Two-state → multi-state → [Vacation](https://atcoder.jp/contests/dp/tasks/dp_c) |

## 14. Summary and revision notes

- House Robber = Maximum Sum of Non-Adjacent Elements with non-negative values.
- One-state: `maxLoot(i) = max(maxLoot(i-1), nums[i] + maxLoot(i-2))`, `maxLoot(<0) = 0`.
- Two-state: `robbed[i] = skipped[i-1] + nums[i]`, `skipped[i] = max(robbed[i-1], skipped[i-1])`.
- Both are `O(n)` time and reducible to `O(1)` space.

```
┌──────────────────────────────────────────────────────────┐
│  maxLoot(i) = 0                             for i < 0    │
│  maxLoot(i) = max( maxLoot(i-1),                         │
│                    nums[i] + maxLoot(i-2) )              │
│  answer: maxLoot(n-1)                                    │
└──────────────────────────────────────────────────────────┘
```

## Exercises

1. Fill both the one-state and two-state tables for `[2, 1, 1, 2]`.

<details>
<summary>Answer</summary>

One-state: `maxLoot = 2, 2, 3, 4`.
Two-state: `robbed = 2, 1, 3, 4`, `skipped = 0, 2, 2, 3`. Answer `max(4, 3) = 4` (houses 0 and 3).
</details>

2. A new rule: after robbing a house, the robber must skip the next **two** houses. Change the one-state recurrence.

<details>
<summary>Answer</summary>

`maxLoot(i) = max(maxLoot(i-1), nums[i] + maxLoot(i-3))`, with `maxLoot(<0) = 0`. Only the landing spot of the "rob" branch moves. With a different skip length per house, this becomes [Solving Questions With Brainpower](https://leetcode.com/problems/solving-questions-with-brainpower/), which is easiest to write right to left.
</details>

3. In the two-state version, the robber must rob the **first** house. What one line changes?

<details>
<summary>Answer</summary>

The base case: `skipped[0]` becomes impossible. Set `skipped[0] = -infinity` (in practice a very negative number such as `Integer.MIN_VALUE / 2`, so adding to it cannot overflow). Everything else is unchanged. Forcing or forbidding a status at the start through the base case is exactly the idea behind the next topic.
</details>
