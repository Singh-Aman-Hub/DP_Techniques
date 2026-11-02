# Climbing Stairs — Walkthrough

## 1. Intuition

Imagine you are already standing on the top step and someone asks, "How did you get here?" There are only two honest answers: "My last move was a single step, from the step just below," or "My last move was a double step, from two steps below." Nothing else is possible.

So the number of ways to reach the top equals:

```
(number of ways to reach the step just below)  +  (number of ways to reach two steps below)
```

You never need to list the climbs. You only need to count the climbs to two smaller staircases, which is the same kind of question again. That is the whole idea.

## 2. Problem statement in our own words

A staircase has `n` steps. Starting from the ground, each move climbs either 1 or 2 steps. Count the number of different ordered sequences of moves that end exactly on step `n`.

Original: [LeetCode 70 · Climbing Stairs](https://leetcode.com/problems/climbing-stairs/) (there `1 ≤ n ≤ 45`).

## 3. Examples

| `n` | Output | All the ways |
|-----|--------|--------------|
| 1 | 1 | `1` |
| 2 | 2 | `1+1`, `2` |
| 3 | 3 | `1+1+1`, `1+2`, `2+1` |
| 4 | 5 | `1+1+1+1`, `1+1+2`, `1+2+1`, `2+1+1`, `2+2` |
| 5 | 8 | see below |

**Running example: `n = 5`, answer `8`.** Grouped by the last move:

```
last move = 1  (came from step 4: the 5 ways to reach 4, each + "1")
    1+1+1+1+1   1+1+2+1   1+2+1+1   2+1+1+1   2+2+1
last move = 2  (came from step 3: the 3 ways to reach 3, each + "2")
    1+1+1+2     1+2+2     2+1+2
                                              total = 5 + 3 = 8
```

Look at this grouping carefully; it *is* the recurrence.

## 4. Deriving the solution — the 7 questions

### Q1. What is changing?

The step we are standing on, `i`. Asking "how many ways to reach `i`" leads to the same question for smaller `i`.

### Q2. What does the state represent?

> `ways[i]` = the number of distinct move sequences that start on the ground and end exactly on step `i`.

### Q3. What are the choices?

Look at the **last** move into step `i`:

- it was a **1-step**, from step `i-1`; or
- it was a **2-step**, from step `i-2` (only possible when `i ≥ 2`).

### Q4. What is the recurrence?

```
ways[i] = ways[i-1] + ways[i-2]
```

Why **add**? This is the key rule of counting DP:

1. **Every** way to reach `i` ends with a 1-step or a 2-step, so the two groups together cover everything (nothing is missed).
2. No way ends with both, so the two groups are **disjoint** (nothing is counted twice).
3. The ways in the "last move = 1" group correspond one-to-one with the ways to reach `i-1` (just remove the final 1). So that group has exactly `ways[i-1]` members. Same for the other group with `ways[i-2]`.

Covered + disjoint + one-to-one ⇒ the total is the sum.

### Q5. What are the base cases?

```
ways[0] = 1
ways[1] = 1
```

`ways[1] = 1` is obvious (a single 1-step). `ways[0] = 1` surprises people: why is there one way to climb zero steps? Because "make no moves" is a valid, complete sequence: the **empty way**. You can also check it is the only value that keeps the recurrence honest: `ways[2]` must be 2 (`1+1` and `2`), and `ways[2] = ways[1] + ways[0] = 1 + ways[0]` forces `ways[0] = 1`. The "2" climb is exactly "the empty climb to step 0, followed by a 2-step".

Two base cases, because the recurrence looks back two steps (same rule as [Fibonacci](../01-fibonacci-number/walkthrough.md#q5-what-are-the-base-cases)).

### Q6. What is the order of computation?

`ways[i]` needs `ways[i-1]` and `ways[i-2]`, both smaller. Loop `i = 2, 3, …, n`, left to right.

### Q7. Can the state space be optimized?

Only the last two cells are ever read, so two rolling variables suffice: `O(1)` space ([Space optimization basics](../../00-foundations/15-space-optimization-basics/)).

### Why this is Fibonacci

Compare the two tables:

```
i          0  1  2  3  4  5  6
F(i)       0  1  1  2  3  5  8
ways[i]    1  1  2  3  5  8  13
```

Same recurrence, but the base cases start one position later: `ways[0] = 1 = F(1)` and `ways[1] = 1 = F(2)`. By induction, if `ways[i-1] = F(i)` and `ways[i-2] = F(i-1)`, then `ways[i] = F(i) + F(i-1) = F(i+1)`. So

```
ways[n] = F(n + 1)
```

This is a good lesson: **two very different stories can have the same state and recurrence.** Recognizing the recurrence, not the story, is what lets you reuse solutions.

## 5. Brute force: recursion

```java
static long climbStairs(int n) {
    if (n <= 1) return 1;                         // ways(0) = ways(1) = 1
    return climbStairs(n - 1) + climbStairs(n - 2);
}
```
Full file: [`solution/BruteForce.java`](solution/BruteForce.java).

### Recursion tree for `n = 5` (values returned in brackets)

```
                               w(5) [8]
                  ┌───────────────┴──────────────┐
               w(4) [5]                        w(3) [3]
           ┌──────┴──────┐                  ┌────┴────┐
        w(3) [3]       w(2) [2]          w(2) [2]   w(1) [1]
       ┌───┴───┐       ┌──┴──┐           ┌──┴──┐
    w(2)[2]  w(1)[1] w(1)  w(0)        w(1)  w(0)
    ┌──┴──┐           [1]   [1]         [1]   [1]
  w(1)  w(0)
  [1]   [1]
```

Each leaf returns 1, and every leaf is one complete climb: there are exactly 8 leaves. That shows why brute force is slow: **it counts the ways one by one**, so its running time is at least the answer, which grows like `1.618^n`. For `n = 45` that is almost 2 billion leaves.

## 6. Memoization (top-down)

```java
private static long[] memo;   // memo[i] = ways to reach step i, -1 = not computed

private static long ways(int i) {
    if (i <= 1) return 1;
    if (memo[i] != -1) return memo[i];
    memo[i] = ways(i - 1) + ways(i - 2);
    return memo[i];
}
```
Full file: [`solution/Memoization.java`](solution/Memoization.java).

### Dry run for `n = 5`

| Order | Call | Result | Note |
|-------|------|--------|------|
| 1 | `ways(5)` | waits | needs `ways(4)`, `ways(3)` |
| 2 | `ways(4)` | waits | needs `ways(3)`, `ways(2)` |
| 3 | `ways(3)` | waits | needs `ways(2)`, `ways(1)` |
| 4 | `ways(2)` | `1 + 1 = 2` | stores `memo[2] = 2` |
| 5 | `ways(1)` | 1 | base case |
| 6 | `ways(3)` finishes | `2 + 1 = 3` | stores `memo[3] = 3` |
| 7 | `ways(2)` | 2 | **cache hit** |
| 8 | `ways(4)` finishes | `3 + 2 = 5` | stores `memo[4] = 5` |
| 9 | `ways(3)` | 3 | **cache hit** |
| 10 | `ways(5)` finishes | `5 + 3 = 8` | answer |

Now the work is proportional to the number of *states* (`n + 1`), not to the number of *ways*. That is the core difference between brute force and DP in counting problems.

## 7. Tabulation (bottom-up)

```java
long[] ways = new long[n + 1];  // ways[i] = number of climbs ending on step i
ways[0] = 1;
ways[1] = 1;
for (int i = 2; i <= n; i++) {
    ways[i] = ways[i - 1] + ways[i - 2];
}
return ways[n];
```
Full file: [`solution/Tabulation.java`](solution/Tabulation.java).

### Filling the table for `n = 5`

```
 i       0   1   2   3   4   5
start    1   1   .   .   .   .
i = 2    1   1   2   .   .   .     2 = 1 + 1
i = 3    1   1   2   3   .   .     3 = 2 + 1
i = 4    1   1   2   3   5   .     5 = 3 + 2
i = 5    1   1   2   3   5   8     8 = 5 + 3   ← answer
```

## 8. Space optimization

Same window as Fibonacci: only `ways[i-1]` and `ways[i-2]` are alive.

```java
long twoBelow = 1, oneBelow = 1;   // ways(0), ways(1)
for (int i = 2; i <= n; i++) {
    long current = oneBelow + twoBelow;
    twoBelow = oneBelow;
    oneBelow = current;
}
return oneBelow;
```
Full file: [`solution/Optimized.java`](solution/Optimized.java). Notice that no special case is needed for `n = 0` or `n = 1`: the loop simply does not run and `oneBelow = 1` is already correct.

## 9. Dry run on a full example

`n = 5`, optimized version (values at the end of each iteration):

| i | current | twoBelow | oneBelow |
|---|---------|----------|----------|
| start | — | 1 | 1 |
| 2 | 1 + 1 = 2 | 1 | 2 |
| 3 | 2 + 1 = 3 | 2 | 3 |
| 4 | 3 + 2 = 5 | 3 | 5 |
| 5 | 5 + 3 = 8 | 5 | 8 |

Answer: **8**, matching the table and the hand enumeration in Section 3.

## 10. Complexity analysis

| Approach | Time | Space | Why |
|----------|------|-------|-----|
| Brute force | `O(2^n)` (≈ `1.618^n`) | `O(n)` | The tree has one leaf per climb, and the number of climbs is `F(n+1)`. Stack depth `n`. |
| Memoization | `O(n)` | `O(n)` | `n+1` states × `O(1)` work; memo + stack. |
| Tabulation | `O(n)` | `O(n)` | One loop, one array. |
| Optimized | `O(n)` | `O(1)` | One loop, two variables. |

`ways(45) = 1,836,311,903` still fits in an `int`, but `ways(46)` does not; the files use `long` to stay safe.

## 11. Generalization: steps of size 1, 2, …, k

Now allow any step size from 1 to `k`. Run the 7 questions again; only Q3–Q5 change.

- **Q3 (choices):** the last move had size `j` for some `j` in `1..k`, as long as `j ≤ i`.
- **Q4 (recurrence):** the groups "last move = j" are still covering and disjoint, so

  ```
  ways[i] = ways[i-1] + ways[i-2] + … + ways[i-k]      (skip indices below 0)
  ```
- **Q5 (base case):** a single one, `ways[0] = 1`. Every other cell can be computed from the sum by skipping negative indices, so `ways[1] = ways[0] = 1` comes out automatically.

### Example: `n = 4`, `k = 3`

```
 i        0   1   2   3   4
ways      1   1   2   4   7
                  │   │   └ ways[3]+ways[2]+ways[1] = 4+2+1 = 7
                  │   └ ways[2]+ways[1]+ways[0] = 2+1+1 = 4
                  └ ways[1]+ways[0] = 2       (only j = 1, 2 fit)
```

With `k = 6` this is "how many ordered dice-roll sequences sum to `n`" ([CSES 1633](https://cses.fi/problemset/task/1633)).

### From `O(n·k)` to `O(n)` with a sliding sum

The direct loop does `k` additions per cell. But consecutive sums overlap almost entirely:

```
ways[i]   = ways[i-1] + ways[i-2] + … + ways[i-k]
ways[i+1] = ways[i]   + ways[i-1] + … + ways[i-k+1]
```

Going from one to the next, one cell **enters** the window (`ways[i]`) and one cell **leaves** it (`ways[i-k]`). Keep the window total in a variable:

```java
long windowSum = 0;                     // sum of ways[i-k .. i-1]
for (int i = 1; i <= n; i++) {
    windowSum += ways[i - 1];                         // enters
    if (i - 1 - k >= 0) windowSum -= ways[i - 1 - k]; // leaves
    ways[i] = windowSum;
}
```

Full file with both versions checked against each other: [`solution/GeneralizedKSteps.java`](solution/GeneralizedKSteps.java). This "keep a running aggregate of the last `k` states" idea comes back in [Prefix and suffix optimization](../../05-dp-optimization/03-prefix-and-suffix-optimization/) and [Monotonic queue optimization](../../05-dp-optimization/04-monotonic-queue-optimization/).

## 12. Why this approach works

Every climb to step `i` has exactly one last move, and removing it leaves a valid climb to a smaller step. So the set of climbs to `i` splits into non-overlapping groups, one per possible last move, and each group has the same size as the set of climbs to the corresponding smaller step. Adding the group sizes therefore counts every climb exactly once. The base case `ways[0] = 1` correctly counts the empty climb from which every first move starts.

## 13. Common mistakes

1. **`ways[0] = 0`.** Then `ways[2] = 1`, which forgets the single 2-step climb. The empty climb counts.
2. **Treating `1+2` and `2+1` as the same.** This problem counts *ordered* sequences. Counting unordered combinations needs a different DP (see [Coin Change II](../../03-subsequence-and-knapsack-dp/09-coin-change-ii/)).
3. **Using `max` instead of `+`.** Counting problems combine disjoint groups with `+`; optimization problems use `max`/`min`.
4. **Reading negative indices** in the generalized version. Always guard `j ≤ i`.
5. **Brute force on large `n`.** It is not "a bit slow", it is unusable past about `n = 40`.

## 14. When to use this technique / how to recognize similar problems

Signals:

- "In how many ways…", "count the number of distinct sequences…".
- The process happens in moves, and the moves are from a small fixed set.
- **Order matters** (`1+2 ≠ 2+1`).

Method: split all solutions by their **last move**, check the groups are disjoint and cover everything, then add.

## 15. Summary and revision notes

- State: `ways[i]` = number of climbs ending on step `i`.
- Last-move decomposition → `ways[i] = ways[i-1] + ways[i-2]`.
- `ways[0] = 1` (the empty climb).
- `ways[n] = F(n+1)`: same recurrence as Fibonacci, shifted base cases.
- General steps `1..k`: sum of the previous `k` cells; sliding window makes it `O(n)`.

```
┌──────────────────────────────────────────────────────┐
│  ways[0] = 1                                         │
│  ways[i] = Σ ways[i-j]  for j = 1..k, j ≤ i          │
│  (k = 2:  ways[i] = ways[i-1] + ways[i-2])           │
│  answer: ways[n]                                     │
└──────────────────────────────────────────────────────┘
```

## Exercises

1. Steps of size 1 or 3 only (no 2-steps). Write the recurrence and base cases, and compute `ways[6]`.

<details>
<summary>Answer</summary>

`ways[i] = ways[i-1] + (i ≥ 3 ? ways[i-3] : 0)`, `ways[0] = 1`.
Table: `ways = 1, 1, 1, 2, 3, 4, 6` for `i = 0..6`, so **`ways[6] = 6`**. (Check: `111111`, `1113` in 4 orders, `33` → 1 + 4 + 1 = 6.)
</details>

2. Step 4 is broken and cannot be stepped on (with steps 1 or 2). How many ways to reach step 6?

<details>
<summary>Answer</summary>

Force `ways[4] = 0`, keep the recurrence everywhere else: `ways = 1, 1, 2, 3, 0, 3, 3` for `i = 0..6`. So **3 ways** (`ways[5] = ways[4] + ways[3] = 0 + 3`, `ways[6] = ways[5] + ways[4] = 3 + 0`). The climbs all pass through step 3 and jump over 4: `…3 → 5 → 6`, with 3 ways to reach step 3.
</details>

3. Using the sliding-window version with `k = 3`, write the value of `windowSum` just before `ways[i]` is assigned, for `i = 1..5`.

<details>
<summary>Answer</summary>

`ways = 1, 1, 2, 4, 7, 13`.
`i=1`: 1 · `i=2`: 1+1 = 2 · `i=3`: 2+2 = 4 · `i=4`: 4+4 = 8, minus `ways[0]=1` → 7 · `i=5`: 7+7 = 14, minus `ways[1]=1` → 13.
</details>

4. Why is it wrong to say "`ways[n] = 2^(n-1)` because at each step you choose 1 or 2"?

<details>
<summary>Answer</summary>

The number of moves is not fixed: a climb with more 2-steps has fewer moves, and a 2-step cannot be taken when only one step remains. The choices are not independent, so you cannot multiply. For `n = 5`, `2^4 = 16 ≠ 8`.
</details>
