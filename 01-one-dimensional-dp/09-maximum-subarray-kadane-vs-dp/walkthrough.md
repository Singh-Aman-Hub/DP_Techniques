# Maximum Subarray: Kadane vs. DP — Walkthrough

## 1. Intuition

You run a small shop and write down each day's profit (negative on bad days). You want to frame the **best uninterrupted stretch of days**, the period with the largest total profit.

Walk through the days one at a time, carrying a running stretch with you. Each morning you ask one question: *"Is the stretch I'm carrying worth keeping?"*

- If the stretch so far has a **positive** total, today's result is better with it attached: keep it and add today.
- If the stretch so far has a **negative** total, it can only drag today down: drop it and start a fresh stretch today.

Meanwhile, remember the best total you have ever seen. When the days run out, that best total is the answer.

That is all of Kadane's algorithm. The rest of this walkthrough shows that this "drop it if it's negative" rule is not a lucky trick but the exact recurrence of a DP, and that the *state* it uses is different in an important way from every earlier topic in this module.

## 2. Problem statement in our own words

Given an integer array `nums` (at least one element, values may be negative), find the contiguous, **non-empty** block `nums[l..r]` whose sum is as large as possible, and return that sum.

Original: [LeetCode 53 · Maximum Subarray](https://leetcode.com/problems/maximum-subarray/) (there `1 ≤ n ≤ 10^5`, `|nums[i]| ≤ 10^4`).

Note "non-empty": unlike [topic 05](../05-maximum-sum-of-non-adjacent-elements/), you may **not** choose nothing. For an all-negative array the answer is the largest (least negative) element.

## 3. Examples

| `nums` | Output | Best subarray |
|--------|--------|---------------|
| `[5, 4, -1, 7, 8]` | 23 | the whole array: the `-1` is worth crossing |
| `[1]` | 1 | `[1]` |
| `[-3, -1, -2]` | -1 | `[-1]` |
| `[2, -5, 3]` | 3 | `[3]`: carrying `2, -5` (sum −3) would hurt |
| `[-2, 1, -3, 4, -1, 2, 1, -5, 4]` | 6 | `[4, -1, 2, 1]` (indices 3..6) |

**Running example: `nums = [-2, 1, -3, 4, -1, 2, 1, -5, 4]`, answer `6`.**

## 4. Choosing the state: "ending at `i`" vs. "first `i`"

Before the 7 questions, we need to talk about the state, because the obvious choice fails.

### The obvious state, and why it fails

Every earlier topic used a **prefix** state: "the best answer using only the first `i` elements" (`best(i)` in topic 05, `maxLoot(i)` in House Robber). Let's try:

> `bestIn[i]` = largest subarray sum inside `nums[0..i]`.

To compute `bestIn[i]` we need to know whether `nums[i]` can be attached to the best block found so far. But that is only possible if that block **ends at `i-1`**, and `bestIn[i-1]` doesn't tell us where its block ends.

Concrete proof that `bestIn` is not enough information. Compare two arrays:

```
A = [2, -1, 2]          B = [2, -5, 2]
bestIn[1] = 2  (block [2])      bestIn[1] = 2  (block [2])
next element: 2                  next element: 2
bestIn[2] = 3  (2 - 1 + 2)       bestIn[2] = 2  ([2] or [2])
```

Same state value (`bestIn[1] = 2`), same next element (`2`), **different** answers (3 vs. 2). A recurrence of the form `bestIn[i] = f(bestIn[i-1], nums[i])` is therefore impossible. The state has thrown away something the future needs: the value of the best block that **touches the right edge**.

> **The test for a good state:** if two situations have the same state value and receive the same next input, they must lead to the same answer. If you can find two that don't, the state is missing information.

### Why the prefix state was fine in topic 05

In [Maximum Sum of Non-Adjacent Elements](../05-maximum-sum-of-non-adjacent-elements/), the choice was a *subset* with a local constraint. "Pick `nums[i]`" only needed "the best selection of `nums[0..i-2]`", and **any** selection of that prefix works, wherever its last element is. No information about the right edge was needed. Here, contiguity means `nums[i]` can only join a block that ends **exactly** at `i-1`, so the right edge matters.

### The state that works

> `endingAt[i]` = the largest sum of a subarray whose **last element is exactly `nums[i]`**.

This state carries exactly the missing information: the best block touching the right edge. And the overall answer is still easy to recover, because the best subarray must end *somewhere*:

```
answer = max( endingAt[0], endingAt[1], …, endingAt[n-1] )
```

| | "First `i`" state | "Ending at `i`" state |
|---|---|---|
| Meaning | best solution using only `nums[0..i]`, ending anywhere | best solution whose last element is exactly `nums[i]` |
| Answer | the last cell, `dp[n-1]` | the **maximum over all cells** |
| Monotone? | yes, never decreases | no, can go down |
| Works when | the choice at `i` only needs "anything valid before" | the choice at `i` needs to know what touches `i-1` (contiguity, "previous element is smaller", …) |
| Examples | House Robber, Decode Ways, Climbing Stairs | Maximum Subarray, Longest Increasing Subsequence, longest common substring |

## 5. Deriving the solution — the 7 questions

### Q1. What is changing?

The index `i` where the subarray ends.

### Q2. What does the state represent?

> `endingAt[i]` = the largest sum of a non-empty subarray that ends exactly at index `i`.

### Q3. What are the choices?

A subarray ending at `i` is `nums[l..i]` for some `l ≤ i`. Split by whether it has anything before `nums[i]`:

- **restart**: it is just `[nums[i]]` (`l = i`);
- **extend**: it is (a subarray ending at `i-1`) followed by `nums[i]` (`l < i`).

### Q4. What is the recurrence?

For "extend", the best subarray ending at `i-1` has sum `endingAt[i-1]`, so the best "extend" option is `endingAt[i-1] + nums[i]`. Therefore:

```
endingAt[i] = max( nums[i],                      ← restart
                   endingAt[i-1] + nums[i] )     ← extend
            = nums[i] + max(0, endingAt[i-1])
```

The second form reads beautifully: **attach the best block ending at `i-1` only if its sum is positive.** That is the shopkeeper's "drop the stretch if it's negative" rule, now proven.

### Q5. What are the base cases?

```
endingAt[0] = nums[0]
```

Only one subarray ends at index 0: `[nums[0]]`. (Equivalently, treat `endingAt[-1]` as "nothing to extend", contributing 0 through the `max(0, …)`.)

### Q6. What is the order of computation?

`endingAt[i]` reads `endingAt[i-1]` → left to right. Keep a running maximum for the answer along the way.

### Q7. Can the state space be optimized?

`endingAt[i]` reads only `endingAt[i-1]`, so **one** variable holds the state, plus one for the running maximum. That is Kadane's algorithm: `O(1)` space.

## 6. Brute force

The natural brute force for subarrays is to try them all, rather than a recursion:

```java
int best = Integer.MIN_VALUE;
for (int start = 0; start < n; start++) {
    int runningSum = 0;                       // sum of nums[start..end]
    for (int end = start; end < n; end++) {
        runningSum += nums[end];
        best = Math.max(best, runningSum);
    }
}
```
Full file: [`solution/BruteForce.java`](solution/BruteForce.java).

There are `n(n+1)/2` subarrays. Summing each from scratch would be `O(n^3)`; reusing the running sum makes it `O(n^2)`. For `n = 10^5` that is 5 × 10^9 steps: too slow.

### Where is the repeated work?

For a fixed `end`, the brute force looks at `nums[0..end]`, `nums[1..end]`, …, `nums[end..end]`: all subarrays ending at `end`, which is exactly what `endingAt[end]` summarizes. Then for `end + 1` it looks at all of them again with one more element appended. DP keeps only the best of each group, because a worse block ending at `end` can never become the better block ending at `end + 1` (both get the same `nums[end+1]` added).

## 7. Memoization (top-down)

The recursion for one state is a simple chain:

```java
private static Integer[] memo;     // memo[i] = endingAt(i); null = not computed

private static int endingAt(int[] nums, int i) {
    if (i == 0) return nums[0];
    if (memo[i] != null) return memo[i];
    memo[i] = nums[i] + Math.max(0, endingAt(nums, i - 1));
    return memo[i];
}
// answer = max over i of endingAt(nums, i)
```
Full file: [`solution/Memoization.java`](solution/Memoization.java).

**Why memoize a chain with no branching?** Because we call `endingAt(i)` for **every** `i`. Without the memo, `endingAt(8)` walks down to 0, then `endingAt(7)` walks down to 0 again, and so on: `1 + 2 + … + n = O(n^2)` calls, the same as the brute force. With the memo, `endingAt(i)` finds `endingAt(i-1)` already stored (the loop computed it a moment ago), so each call is `O(1)`.

**Why `Integer[]` and `null`?** Sums can be negative, even `-1`, so no number is safe as "not computed".

In practice, this recursion can be 10^5 calls deep on LeetCode's largest inputs, which risks `StackOverflowError` in Java. Here, the main loop calls `endingAt(0)`, `endingAt(1)`, … in increasing order, so each call recurses only one level before hitting the memo. Calling `endingAt(n-1)` first would be the dangerous order.

## 8. Tabulation (bottom-up)

```java
int[] endingAt = new int[n];     // endingAt[i] = best sum of a subarray ending exactly at i
endingAt[0] = nums[0];
int best = endingAt[0];
for (int i = 1; i < n; i++) {
    endingAt[i] = nums[i] + Math.max(0, endingAt[i - 1]);
    best = Math.max(best, endingAt[i]);
}
return best;
```
Full file: [`solution/Tabulation.java`](solution/Tabulation.java).

### Filling the table

```
 i          0    1    2    3    4    5    6    7    8
 nums      -2    1   -3    4   -1    2    1   -5    4
```

| i | nums[i] | endingAt[i-1] | decision | endingAt[i] | best so far |
|---|---------|---------------|----------|-------------|-------------|
| 0 | -2 | — | base | **-2** | -2 |
| 1 | 1 | -2 (negative) | restart | 1 + 0 = **1** | 1 |
| 2 | -3 | 1 | extend | -3 + 1 = **-2** | 1 |
| 3 | 4 | -2 (negative) | restart | 4 + 0 = **4** | 4 |
| 4 | -1 | 4 | extend | -1 + 4 = **3** | 4 |
| 5 | 2 | 3 | extend | 2 + 3 = **5** | 5 |
| 6 | 1 | 5 | extend | 1 + 5 = **6** | **6** |
| 7 | -5 | 6 | extend | -5 + 6 = **1** | 6 |
| 8 | 4 | 1 | extend | 4 + 1 = **5** | 6 |

```
 endingAt  -2    1   -2    4    3    5    6    1    5
                                         ▲
                              maximum = 6 (answer)      last cell = 5 (NOT the answer)
```

Two things to notice:
1. The answer is the **maximum cell** (6 at `i = 6`), not the last cell (5). That's the price of an "ending at" state.
2. The table goes up and down. It is not monotone, unlike the prefix tables in topics 05–08.

**Recovering the subarray:** the best cell is at `i = 6`. Walk left while the decision was "extend": 6 ← 5 ← 4 ← 3, and at `i = 3` the decision was "restart". So the subarray is `nums[3..6] = [4, -1, 2, 1]`. `maxSubArrayWithBounds` in the optimized file tracks this on the fly.

## 9. Space optimization: this is Kadane's algorithm

`endingAt[i]` reads only `endingAt[i-1]`. Replace the table with one variable:

```java
int currentEnding = nums[0];     // endingAt(i), overwritten each step
int best = nums[0];
for (int i = 1; i < nums.length; i++) {
    currentEnding = nums[i] + Math.max(0, currentEnding);
    best = Math.max(best, currentEnding);
}
return best;
```
Full file: [`solution/Optimized.java`](solution/Optimized.java).

Put the usual textbook version of Kadane next to the DP:

```
Textbook Kadane                         DP recurrence
──────────────────────────────          ──────────────────────────────────────
current = max(nums[i],                   endingAt[i] = max(nums[i],
              current + nums[i])                           endingAt[i-1] + nums[i])
best    = max(best, current)             answer      = max over i of endingAt[i]
```

Line for line, they are the same. **Kadane's algorithm is the tabulated DP with the table compressed to one cell.**

### "It looks greedy. Is it?"

A greedy algorithm makes a local choice and commits to it without comparing it against the alternatives' futures. Kadane *looks* like that: at each step it "throws away" the running block whenever the block is negative, and never reconsiders.

But that choice is not a guess. It compares the **exact optimal values** of the only two possibilities (restart vs. extend the best block ending at `i-1`) and is backed by a proof:

- Any subarray ending at `i` that extends a block ending at `i-1` is at most `endingAt[i-1] + nums[i]` (the best such block is the best one to extend).
- If `endingAt[i-1] < 0`, then **every** block ending at `i-1` has a negative sum, so attaching any of them to `nums[i]` makes it worse. Dropping them loses nothing, now or later, because any later subarray that passes through `i` contains some block ending at `i` and is best served by the best one.

And the global answer is not chosen greedily at all: `best` remembers the maximum over **all** `n` states. So Kadane is DP:

- a well-defined state (`endingAt[i]`),
- a recurrence that takes the best over all options,
- a final answer that is the best over all states.

It *coincides* with a greedy-looking rule because the state has only two options and one of them can be ruled out by a sign check. Many fast "greedy" algorithms are DPs whose table has collapsed this way. When you see one, ask: *what is the state, and what is the recurrence?*

## 10. Dry run on a full example

Kadane on `nums = [-2, 1, -3, 4, -1, 2, 1, -5, 4]`, with the start index tracked as in `maxSubArrayWithBounds`:

| i | nums[i] | previous `currentEnding` | action | `currentEnding` | current start | `best` | best range |
|---|---------|--------------------------|--------|-----------------|---------------|--------|------------|
| 0 | -2 | — | base | -2 | 0 | -2 | [0..0] |
| 1 | 1 | -2 | restart | 1 | 1 | 1 | [1..1] |
| 2 | -3 | 1 | extend | -2 | 1 | 1 | [1..1] |
| 3 | 4 | -2 | restart | 4 | 3 | 4 | [3..3] |
| 4 | -1 | 4 | extend | 3 | 3 | 4 | [3..3] |
| 5 | 2 | 3 | extend | 5 | 3 | 5 | [3..5] |
| 6 | 1 | 5 | extend | 6 | 3 | **6** | **[3..6]** |
| 7 | -5 | 6 | extend | 1 | 3 | 6 | [3..6] |
| 8 | 4 | 1 | extend | 5 | 3 | 6 | [3..6] |

Answer **6**, subarray `[4, -1, 2, 1]`. The `currentEnding` column is exactly the `endingAt` row of Section 8.

## 11. Complexity analysis

| Approach | Time | Space | Why |
|----------|------|-------|-----|
| Brute force (triple loop) | `O(n^3)` | `O(1)` | `O(n^2)` subarrays, each summed in `O(n)`. |
| Brute force (running sum) | `O(n^2)` | `O(1)` | `O(n^2)` subarrays, `O(1)` each. |
| Memoization | `O(n)` | `O(n)` | `n` states, `O(1)` each; memo + stack. |
| Tabulation | `O(n)` | `O(n)` | One pass, one table. |
| Kadane (optimized) | `O(n)` | `O(1)` | One pass, two variables. |

Overflow: at most `10^5 × 10^4 = 10^9` in absolute value, which fits in `int`.

## 12. Why this approach works

1. **Each `endingAt[i]` is correct.** Every subarray ending at `i` is either `[nums[i]]` or "a subarray ending at `i-1`, plus `nums[i]`". The best of the second kind uses the best block ending at `i-1`. So the max of `nums[i]` and `endingAt[i-1] + nums[i]` is the best of all of them (induction from `i = 0`).
2. **The answer is correct.** The optimal subarray ends at some index `r`, so its sum is at most `endingAt[r]`, and `endingAt[r]` is the sum of an actual subarray. Taking the maximum over all `r` gives exactly the optimum.

## 13. Extensions

### LeetCode 918 — Maximum Sum Circular Subarray

[LeetCode 918](https://leetcode.com/problems/maximum-sum-circular-subarray/): the array is circular, so a subarray may wrap from the end back to the start. Split into cases, as in [House Robber II](../07-house-robber-ii/):

- **Doesn't wrap:** ordinary Kadane → `maxNormal`.
- **Wraps:** it takes a prefix and a suffix, i.e. everything **except** a middle block. To maximize what you keep, remove the **minimum**-sum middle block: `total − minSubarray`, where `minSubarray` is Kadane with `min` instead of `max`.

Answer `max(maxNormal, total − minSubarray)`, with one trap: if every number is negative, `minSubarray` is the whole array and `total − minSubarray = 0` would describe the **empty** subarray, which is not allowed. In that case return `maxNormal`. Example `[5, -3, 5]`: `maxNormal = 7`, `total = 7`, `minSubarray = -3`, wrap option `7 − (−3) = 10` → answer **10** (`5, 5` across the wrap).

### LeetCode 152 — Maximum Product Subarray

[LeetCode 152](https://leetcode.com/problems/maximum-product-subarray/): same question with products. The single "ending at `i`" state breaks, because a very **negative** product ending at `i-1` becomes very **positive** after multiplying by a negative `nums[i]`. Apply the state test from Section 4: two situations with the same "max product ending at `i-1`" can have different "min product ending at `i-1`", and that difference changes the answer. So carry **two** states:

```
maxEnd[i] = max( nums[i], nums[i]·maxEnd[i-1], nums[i]·minEnd[i-1] )
minEnd[i] = min( nums[i], nums[i]·maxEnd[i-1], nums[i]·minEnd[i-1] )
answer    = max over i of maxEnd[i]
```

This is the same move as the two-state House Robber in [topic 06](../06-house-robber/walkthrough.md#a-second-formulation-two-states-per-house): when one number per index loses information, keep one number per *kind* of situation.

## 14. Common mistakes

1. **Returning the last cell** (`endingAt[n-1]`) instead of the maximum. For the running example, that gives 5 instead of 6.
2. **Starting `best` at 0.** For `[-3, -1, -2]` you'd return 0, the empty subarray, which is not allowed. Start from `nums[0]` (or `Integer.MIN_VALUE`).
3. **Resetting to 0 instead of restarting at `nums[i]`.** A common "Kadane" variant sets `current = 0` when it goes negative *after* updating `best`; it works only if the update order is exactly right and breaks on all-negative arrays when written slightly differently. The `nums[i] + max(0, prev)` form has no such traps.
4. **Using a prefix ("first `i`") state** and trying to make `bestIn[i]` from `bestIn[i-1]`. See the counter-example in Section 4.
5. **Memo sentinel `-1`.** Sums can be `-1`. Use `null`.
6. **LC 918: forgetting the all-negative case**, returning 0.

## 15. When to use this technique / how to recognize similar problems

Use an **"ending at `i`"** state when:
- the answer must be **contiguous** (subarray, substring, consecutive days), or
- extending a solution depends on its **last** element (increasing sequences, "differs from the previous by …").

Then:
1. Define `end[i]` = best solution whose last element is `i`.
2. Recurrence: extend some compatible earlier ending, or start fresh at `i`.
3. Answer: best over all `end[i]`.

Use a **"first `i`"** state when the choice at `i` only needs "the best of anything valid before", as in House Robber, Climbing Stairs and Decode Ways.

## 16. Summary and revision notes

- "Best within the first `i`" can't be extended for subarrays: it forgets whether the best block touches the edge.
- State: `endingAt[i]` = best sum of a subarray ending exactly at `i`.
- Recurrence: `endingAt[i] = nums[i] + max(0, endingAt[i-1])` (extend only if the previous block is positive).
- Answer: **max over all `i`**, not the last cell. Non-empty → never return 0 by default.
- Kadane = this DP with one variable. Greedy-looking, but every step compares exact optimal values.
- Extensions: circular (918) = `max(normal, total − minSubarray)`; product (152) = track max and min.

```
┌────────────────────────────────────────────────────────────┐
│  endingAt[0] = nums[0]                                     │
│  endingAt[i] = nums[i] + max(0, endingAt[i-1])             │
│  answer = max over i of endingAt[i]                        │
│  Kadane:  cur = nums[i] + max(0, cur);  best = max(best,cur)│
└────────────────────────────────────────────────────────────┘
```

## Exercises

1. Fill the `endingAt` table for `[2, -5, 3, -1, 4]` and find the best subarray.

<details>
<summary>Answer</summary>

`endingAt = 2, -3, 3, 2, 6`.
- `i=1`: `-5 + max(0, 2) = -3`; `i=2`: `3 + max(0, -3) = 3` (restart); `i=3`: `-1 + 3 = 2`; `i=4`: `4 + 2 = 6`.

Answer **6**, subarray `[3, -1, 4]` (indices 2..4).
</details>

2. Solve LC 918 by hand for `[-3, -2, -3]` and for `[3, -1, 2, -1]`.

<details>
<summary>Answer</summary>

- `[-3, -2, -3]`: all negative → return `maxNormal = -2`. (The formula would give `total − minSubarray = -8 − (-8) = 0`, the illegal empty subarray.)
- `[3, -1, 2, -1]`: `maxNormal = 4` (`3, -1, 2`), `total = 3`, `minSubarray = -1`, wrap option `3 − (−1) = 4`. Answer **4**. (Wrapping `2, -1, 3` also gives 4.)
</details>

3. Run the two-state product DP on `[-2, 3, -4]`.

<details>
<summary>Answer</summary>

| i | nums[i] | candidates | maxEnd | minEnd |
|---|---------|-----------|--------|--------|
| 0 | -2 | — | -2 | -2 |
| 1 | 3 | 3, 3·(−2) = −6, 3·(−2) = −6 | 3 | −6 |
| 2 | −4 | −4, −4·3 = −12, −4·(−6) = 24 | 24 | −12 |

Answer `max(-2, 3, 24) = 24` (the whole array). With only `maxEnd`, step 2 would see `max(-4, -12) = -4` and miss 24.
</details>

4. For a prefix-state problem like House Robber the answer is `dp[n-1]`; here it is `max(dp)`. Explain in one sentence each why.

<details>
<summary>Answer</summary>

- House Robber: `dp[n-1]` is the best over **all** plans using houses `0..n-1`, which is the whole problem.
- Maximum Subarray: `endingAt[n-1]` only covers subarrays that end at the last index; the optimal one may end anywhere, so we take the best ending over all indices.
</details>
