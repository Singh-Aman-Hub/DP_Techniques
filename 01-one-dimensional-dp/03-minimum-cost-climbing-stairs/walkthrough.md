# Min Cost Climbing Stairs — Walkthrough

## 1. Intuition

You are crossing a river on a row of stepping stones. Each stone has a toll collector who charges you when you **leave** that stone. You may start on the first or the second stone for free, and from any stone you can hop to the next stone or skip one. You want to reach the far bank as cheaply as possible.

Stand on the far bank and look back. You arrived either from the last stone or from the stone before it. If you already knew the cheapest way to reach each of those two stones, you would just add each one's toll and take the **smaller** total. That is the whole algorithm. It is [Climbing Stairs](../02-climbing-stairs/) with `min` in place of `+`.

## 2. Problem statement in our own words

You get an array `cost` of length `n ≥ 2`. `cost[i]` is the price you pay when you step **off** step `i`. You start on step 0 or step 1 (your choice, free). Each move goes up 1 or 2 steps. The goal is to get to the "top of the floor", which is the position just **after** the last step (index `n`). Return the minimum total price.

Original: [LeetCode 746 · Min Cost Climbing Stairs](https://leetcode.com/problems/min-cost-climbing-stairs/).

## 3. Examples

| `cost` | Output | Cheapest route |
|--------|--------|----------------|
| `[10, 15, 20]` | 15 | start on 1, pay 15, jump 2 to the top |
| `[5, 7]` | 5 | start on 0, pay 5, jump 2 to the top |
| `[1,100,1,1,1,100,1,1,100,1]` | 6 | step on every `1`, avoid every `100` |
| `[2, 5, 1, 4, 3]` | 6 | 0 → 2 → 4 → top, paying 2 + 1 + 3 |

**Running example: `cost = [2, 5, 1, 4, 3]`, answer `6`.**

### Where exactly is the top? (the off-by-one)

```
 index:    0     1     2     3     4   │   5
 cost:     2     5     1     4     3   │  TOP (no cost, not in the array)
          ▲     ▲                      │
       free starts                     the goal is index n = 5
```

The top is **not** the last step (index 4). If the goal were the last step, `[10, 15, 20]` would cost 10 (start on 0, pay 10, jump to step 2). The real answer is 15, because you must get *past* step 2, and leaving step 2 costs 20. So:

- positions run from `0` to `n` (that is `n + 1` positions);
- the DP table has `n + 1` cells;
- the answer is in cell `n`, not `n - 1`.

## 4. Deriving the solution — the 7 questions

### Q1. What is changing?

The position `i` we are standing on, from `0` up to `n` (the top).

### Q2. What does the state represent?

> `reach[i]` = the minimum total price paid to **stand on** position `i` (before paying `cost[i]` to leave it). `reach[n]` is the price of reaching the top.

Writing "before paying `cost[i]`" is important. It decides where each price is added, and getting this vague is the most common source of off-by-one bugs here.

### Q3. What are the choices?

How did we arrive at `i`?

- **From `i-1`** with a 1-step, paying `cost[i-1]` to leave it.
- **From `i-2`** with a 2-step, paying `cost[i-2]` to leave it.

### Q4. What is the recurrence?

```
reach[i] = min( reach[i-1] + cost[i-1],      ← came from one below
                reach[i-2] + cost[i-2] )     ← came from two below
```

Why `min`? Each choice gives a **complete** price for a different family of routes. We want the cheapest route, so we keep the cheaper family's best. Why is it safe to use the *best* way to reach `i-1`, instead of every way? Because whatever route got us to `i-1`, the remaining price to finish from there is the same, so a cheaper way to reach `i-1` can only help. That is [optimal substructure](../../00-foundations/05-optimal-substructure/).

**Counting vs. optimizing, side by side:**

| | Climbing Stairs | Min Cost Climbing Stairs |
|---|---|---|
| Question | how many routes? | cheapest route? |
| Combine choices with | `+` (all routes count) | `min` (only the best survives) |
| Edge term | `ways[i-1]` | `reach[i-1] + cost[i-1]` (add the move's price) |

### Q5. What are the base cases?

```
reach[0] = 0
reach[1] = 0
```

The problem lets us start on step 0 **or** step 1 for free. So standing on either one costs nothing. Note that `reach[1]` is *not* `min(0, reach[0] + cost[0])`; it is simply 0, because we can start there directly.

### Q6. What is the order of computation?

`reach[i]` uses `reach[i-1]` and `reach[i-2]`: loop `i = 2, 3, …, n` (inclusive of `n`!).

### Q7. Can the state space be optimized?

Yes: only the last two values are read → two variables, `O(1)` space.

## 5. Brute force: recursion

```java
private static int reach(int[] cost, int i) {
    if (i <= 1) return 0;                         // free starting steps
    int fromOneBelow = reach(cost, i - 1) + cost[i - 1];
    int fromTwoBelow = reach(cost, i - 2) + cost[i - 2];
    return Math.min(fromOneBelow, fromTwoBelow);
}
// answer: reach(cost, cost.length)
```
Full file: [`solution/BruteForce.java`](solution/BruteForce.java).

### Recursion tree for `cost = [2, 5, 1, 4, 3]` (call `reach(5)`)

Edge labels show the price added on that branch; brackets show the returned value.

```
                                reach(5) [6]
                    +3 ┌───────────┴───────────┐ +4
                   reach(4) [3]             reach(3) [3]
              +4 ┌──────┴──────┐ +1     +1 ┌────┴────┐ +5
            reach(3) [3]    reach(2) [2]  reach(2)[2] reach(1)[0]
         +1 ┌───┴───┐ +5    +5┌──┴──┐+2   +5┌──┴──┐+2
       reach(2)[2] reach(1)  r(1)  r(0)    r(1)  r(0)
      +5┌──┴──┐+2    [0]     [0]   [0]     [0]   [0]
       r(1)  r(0)
       [0]   [0]
```

`reach(3)` is computed 2 times and `reach(2)` 3 times: the same overlap as Fibonacci, so the same `O(2^n)` blow-up.

## 6. Memoization (top-down)

```java
private static int[] memo;    // memo[i] = cheapest price to stand on i; -1 = unknown

private static int reach(int[] cost, int i) {
    if (i <= 1) return 0;
    if (memo[i] != -1) return memo[i];
    memo[i] = Math.min(reach(cost, i - 1) + cost[i - 1],
                       reach(cost, i - 2) + cost[i - 2]);
    return memo[i];
}
```
Full file: [`solution/Memoization.java`](solution/Memoization.java).

`-1` is a safe "unknown" marker because prices are never negative, so a real answer is never `-1`. The memo has `n + 1` cells so that index `n` (the top) fits.

### Dry run (cache hits)

| Order | Call | Computation | Stored |
|-------|------|-------------|--------|
| 1 | `reach(2)` | `min(reach(1)+5, reach(0)+2) = min(5, 2)` | `memo[2] = 2` |
| 2 | `reach(3)` | `min(reach(2)+1, reach(1)+5) = min(3, 5)` | `memo[3] = 3` |
| 3 | `reach(4)` | `min(reach(3)+4, reach(2)+1)` — `reach(2)` is a **hit** → `min(7, 3)` | `memo[4] = 3` |
| 4 | `reach(5)` | `min(reach(4)+3, reach(3)+4)` — `reach(3)` is a **hit** → `min(6, 7)` | `memo[5] = 6` |

(The rows are listed in the order the values are *finished*; the calls themselves start from `reach(5)` and go down the left side first.)

## 7. Tabulation (bottom-up)

```java
int[] reach = new int[n + 1];          // reach[i] = cheapest price to stand on i
reach[0] = 0;
reach[1] = 0;
for (int i = 2; i <= n; i++) {
    reach[i] = Math.min(reach[i - 1] + cost[i - 1],
                        reach[i - 2] + cost[i - 2]);
}
return reach[n];
```
Full file: [`solution/Tabulation.java`](solution/Tabulation.java).

### Filling the table for `cost = [2, 5, 1, 4, 3]`

```
 i            0    1    2    3    4    5(top)
 cost         2    5    1    4    3    —
 reach        0    0    .    .    .    .        base cases

 i = 2: min(reach[1]+cost[1], reach[0]+cost[0]) = min(0+5, 0+2) = 2
 reach        0    0    2    .    .    .

 i = 3: min(reach[2]+cost[2], reach[1]+cost[1]) = min(2+1, 0+5) = 3
 reach        0    0    2    3    .    .

 i = 4: min(reach[3]+cost[3], reach[2]+cost[2]) = min(3+4, 2+1) = 3
 reach        0    0    2    3    3    .

 i = 5: min(reach[4]+cost[4], reach[3]+cost[3]) = min(3+3, 3+4) = 6
 reach        0    0    2    3    3    6        ← answer reach[5] = 6
```

### Reconstructing the route

Walk back from the top, each time asking "which option produced this minimum?":

```
reach[5] = 6 came from reach[4] + cost[4] = 3 + 3   → previous position 4
reach[4] = 3 came from reach[2] + cost[2] = 2 + 1   → previous position 2
reach[2] = 2 came from reach[0] + cost[0] = 0 + 2   → previous position 0 (a free start)

route: start on 0 → 2 → 4 → top,  price 2 + 1 + 3 = 6
```

## 8. Space optimization

```java
int twoBelow = 0, oneBelow = 0;        // reach(0), reach(1)
for (int i = 2; i <= cost.length; i++) {
    int current = Math.min(oneBelow + cost[i - 1], twoBelow + cost[i - 2]);
    twoBelow = oneBelow;
    oneBelow = current;
}
return oneBelow;                       // reach(n)
```
Full file: [`solution/Optimized.java`](solution/Optimized.java).

The two-variable version cannot reconstruct the route, because the older values are gone. If you need the route, keep the table. This trade-off (less memory vs. ability to backtrack) appears again and again.

## 9. Dry run on a full example

`cost = [2, 5, 1, 4, 3]`, optimized version:

| i | option A: `oneBelow + cost[i-1]` | option B: `twoBelow + cost[i-2]` | current | twoBelow | oneBelow |
|---|------|------|---|---|---|
| start | — | — | — | 0 | 0 |
| 2 | 0 + 5 = 5 | 0 + 2 = 2 | 2 | 0 | 2 |
| 3 | 2 + 1 = 3 | 0 + 5 = 5 | 3 | 2 | 3 |
| 4 | 3 + 4 = 7 | 2 + 1 = 3 | 3 | 3 | 3 |
| 5 | 3 + 3 = 6 | 3 + 4 = 7 | 6 | 3 | 6 |

Answer **6**, same as the table.

## 10. Complexity analysis

| Approach | Time | Space | Why |
|----------|------|-------|-----|
| Brute force | `O(2^n)` | `O(n)` | Two calls per level, no reuse; stack depth `n`. |
| Memoization | `O(n)` | `O(n)` | `n+1` states, each one `min` of two sums; memo + stack. |
| Tabulation | `O(n)` | `O(n)` | One loop over `n-1` cells; table of `n+1`. |
| Optimized | `O(n)` | `O(1)` | Same loop; two variables. |

Overflow is not a concern: LeetCode limits `n ≤ 1000` and `cost[i] ≤ 999`, so totals stay below one million.

## 11. Why this approach works

Take any cheapest route to position `i`. Its last move came from `i-1` or `i-2`. The part of the route before that move must itself be a cheapest route to that earlier position; otherwise we could swap in a cheaper one and get a cheaper route to `i`, which is impossible. So the cheapest route to `i` is "cheapest to `i-1` plus `cost[i-1]`" or "cheapest to `i-2` plus `cost[i-2]`", whichever is smaller. That is exactly the recurrence. The base cases are correct because starting on 0 or 1 is free.

## 12. Common mistakes

1. **Returning `reach[n-1]`.** The top is index `n`. With `[10, 15, 20]` you would return `reach[2] = 10` instead of 15; with `[5, 7]` you would return `reach[1] = 0` instead of 5.
2. **Table of size `n`.** Then index `n` does not exist; you need `n + 1` cells.
3. **Adding `cost[i]` instead of `cost[i-1]`/`cost[i-2]`.** With our state ("before paying to leave `i`"), the price added belongs to the step you *came from*. Mixing the two conventions double-counts or skips a price.
4. **`reach[1] = cost[0]`.** You can start on step 1 for free; you don't have to walk there from step 0.
5. **Using `+` instead of `min`.** That would add up all routes' prices, which means nothing.

## 13. When to use this technique / how to recognize similar problems

Signals:

- "minimum cost / minimum number of moves to get from the start to the end";
- moves of a few fixed sizes along a line;
- each move or position carries a price.

Template: `best[i] = min over moves (best[previous position] + price of the move)`. Next, [Frog Jump](../04-frog-jump/) keeps this template but makes the price depend on *both* ends of the jump, and then allows up to `k`-sized jumps.

## 14. Summary and revision notes

- State: `reach[i]` = cheapest price to stand on position `i`; positions `0..n`, top = `n`.
- Free starts → `reach[0] = reach[1] = 0`.
- Combine choices with `min`; add the price of the step you leave.
- Table size `n + 1`; answer `reach[n]`.
- Keep the table if you need the route; two variables if you need only the value.

```
┌─────────────────────────────────────────────────────────────┐
│  reach[0] = reach[1] = 0                                    │
│  reach[i] = min(reach[i-1] + cost[i-1],                     │
│                 reach[i-2] + cost[i-2])     for 2 ≤ i ≤ n   │
│  answer: reach[n]          (n = cost.length = the top)      │
└─────────────────────────────────────────────────────────────┘
```

## Exercises

1. Fill the `reach` table for `cost = [10, 15, 20]` and reconstruct the route.

<details>
<summary>Answer</summary>

`reach[0] = 0`, `reach[1] = 0`, `reach[2] = min(0+15, 0+10) = 10`, `reach[3] = min(10+20, 0+15) = 15`.
`reach[3] = 15` came from `reach[1] + cost[1]`, so the route is: start on 1, pay 15, jump 2 to the top.
</details>

2. Solve it with the other state: `from[i]` = minimum price to get from standing on `i` to the top, **including** paying `cost[i]`. Write the recurrence, base cases and answer, then fill it for `[2, 5, 1, 4, 3]`.

<details>
<summary>Answer</summary>

`from[i] = cost[i] + min(from[i+1], from[i+2])`, with `from[n] = 0` and `from[n+1] = 0` (already at or past the top). Fill **right to left**. Answer `min(from[0], from[1])`.

`from[5]=0, from[4]=3+min(0,0)=3, from[3]=4+min(3,0)=4, from[2]=1+min(4,3)=4, from[1]=5+min(4,4)=9, from[0]=2+min(9,4)=6`.
Answer `min(6, 9) = 6`. Same answer, opposite direction: the order of computation always follows the direction of the dependencies.
</details>

3. Suppose you must start on step 0 (step 1 is no longer a free start). What changes?

<details>
<summary>Answer</summary>

Only the base case: `reach[1] = reach[0] + cost[0] = cost[0]`. For `[10, 15, 20]`: `reach[0] = 0`, `reach[1] = 10`, `reach[2] = min(10+15, 0+10) = 10`, `reach[3] = min(10+20, 10+15) = 25`. Answer **25**, from the route 0 → 1 → top (pay 10, then 15). The other routes cost 0 → 2 → top = 30 and 0 → 1 → 2 → top = 45.
</details>
