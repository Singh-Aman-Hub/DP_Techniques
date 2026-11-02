# Subsequences Fundamentals — Walkthrough

## 1. Intuition (no code, no math — an everyday analogy)

You are at a buffet with three dishes laid out in a line: soup, rice, cake. You walk along the line once, from left to right, and at each dish you decide **yes** (put some on your plate) or **no** (walk past). You never go back.

- At the soup you have 2 options.
- Whatever you did with the soup, at the rice you again have 2 options: that makes 2 × 2 = 4 plates so far.
- At the cake, 2 more options each: 4 × 2 = **8 possible plates**, from "nothing at all" to "everything".

Every plate is a **subsequence** of the buffet line: some dishes, in the order they were laid out. The walk-and-decide process is the **take/skip recursion**, and it is the starting point for every problem in this module.

Now suppose you only care about one thing: *the total calories on your plate*. At the cake, you do not need to remember *which* dishes you took — only the running calorie total. Two different walks that both arrive at the cake with 300 calories on the plate have exactly the same future. That observation — "only the position and the running total matter" — is what turns this brute force into dynamic programming.

## 2. Problem statement in our own words

This walkthrough covers two closely related tasks on an array `arr` of `n` integers.

1. **Generate (print) every subsequence** of `arr`.
2. **Count the subsequences whose elements add up to exactly `K`.** For the DP versions we assume the elements are **non-negative** (zero or positive); Section 12 explains why.

There is no single judge problem for this lesson; the practice table in the [README](README.md) lists problems that use exactly these ideas.

### Subarray vs. subsequence vs. subset

Take `arr = [1, 2, 3]`.

```
Subarrays (contiguous blocks)       Subsequences (keep order, gaps OK)    Subsets (order irrelevant)
---------------------------------   -----------------------------------   --------------------------
[1] [2] [3]                         []                                    {}
[1,2] [2,3]                         [1] [2] [3]                           {1} {2} {3}
[1,2,3]                             [1,2] [1,3] [2,3]                     {1,2} {1,3} {2,3}
                                    [1,2,3]                               {1,2,3}
6 non-empty = n(n+1)/2              8 = 2^n (empty included)              8 = 2^n (empty included)
```

- `[1, 3]` is a subsequence but **not** a subarray (there is a gap where `2` was).
- `[3, 1]` is **not** a subsequence: the order changed.
- For "pick some items" questions (sum, weight, value) the order of the picked items does not change the answer, so a **subset** and a **subsequence** are the same thing for us: walk the items in array order and decide each one. That is why this module treats "subset" problems with the subsequence recursion.

Why `2^n`? Each of the `n` elements is independently in or out. That is `n` yes/no decisions, `2 × 2 × … × 2 = 2^n` combinations. For `n = 20` that is about one million — fine. For `n = 40` it is about 10^12 — far too many.

## 3. Examples (input → output, with an explanation)

We use one running example for everything: **`arr = [1, 2, 3]`, `K = 3`.**

| Input | Output | Explanation |
|---|---|---|
| Generate, `[1, 2, 3]` | 8 subsequences | `[1,2,3] [1,2] [1,3] [1] [2,3] [2] [3] []` |
| Count, `[1, 2, 3]`, `K = 3` | 2 | `[1, 2]` and `[3]` |
| Count, `[1, 2, 1]`, `K = 2` | 2 | `[2]` and `[1, 1]` (the first and last 1) |
| Count, `[1, 1, 1, 1]`, `K = 2` | 6 | any 2 of the 4 ones: 4·3/2 = 6 |
| Count, `[0, 1]`, `K = 1` | 2 | `[1]` and `[0, 1]` — a zero creates a second subsequence with the same sum |
| Count, `[1, 2, 3]`, `K = 0` | 1 | only the empty subsequence `[]` |

Two subsequences are **different if they use different positions**, even if the values look the same. In `[1, 2, 1]` with `K = 3`, both `[1, 2]` (positions 0, 1) and `[2, 1]` (positions 1, 2) count.

## 4. Deriving the solution — the 7 questions

We derive the counting problem. The generator is the same recursion without the counting.

### Q1. What is changing?

As we walk the array we change two things:

- the **index** `i` of the element we are deciding on (it grows from `0` to `n`);
- the **sum of the elements taken so far**, `sumSoFar` (it grows whenever we take something).

Nothing else about the past matters for the future. To finish, we only need to know which elements are still undecided (those from `i` onward) and how far we still are from `K` (which `sumSoFar` tells us).

### Q2. What does the state represent?

> `count(i, sumSoFar)` = the number of ways to make take/skip decisions for `arr[i], arr[i+1], …, arr[n-1]` so that the final total equals `K`, given that the elements already taken from `arr[0..i-1]` add up to `sumSoFar`.

This is the **"index + accumulated quantity"** state shape. Keep it in mind: in [0/1 Knapsack](../02-zero-one-knapsack/) the accumulated quantity becomes "weight used so far" (or, equivalently, "capacity left").

An equivalent way to write the same state is with the **remaining** amount, `remaining = K - sumSoFar`. `count(i, remaining)` then means "ways to pick from `arr[i..]` a subsequence summing to `remaining`". Same states, different labels. Knapsack is usually written with the "remaining capacity" form.

### Q3. What are the choices?

At index `i` there are exactly two:

1. **Take** `arr[i]`: move to `(i + 1, sumSoFar + arr[i])`.
2. **Skip** `arr[i]`: move to `(i + 1, sumSoFar)`.

### Q4. What is the recurrence?

```
count(i, sumSoFar) = count(i + 1, sumSoFar + arr[i])     ← take
                   + count(i + 1, sumSoFar)               ← skip
```

We **add** because the two choices lead to different subsequences (one contains position `i`, the other does not), so no subsequence is counted twice and none is missed.

Compare with [Maximum Sum of Non-Adjacent Elements](../../01-one-dimensional-dp/05-maximum-sum-of-non-adjacent-elements/), where we combined take and skip with `max`. The combining operation depends on the question:

| Question | Combine with |
|---|---|
| How many ways? | `+` |
| Best value? | `max` / `min` |
| Is it possible? | `||` (logical OR) |

### Q5. What are the base cases?

- `i == n` (every element decided): we have one complete subsequence. Return `1` if `sumSoFar == K`, else `0`.
- Optional pruning when all elements are non-negative: if `sumSoFar > K`, return `0` immediately — the sum can never come back down. This also keeps `sumSoFar` inside `0..K`, which matters for the memo table.

### Q6. What is the order of computation?

`count(i, …)` needs answers from row `i + 1`. Top-down, recursion takes care of that. Bottom-up, we either fill `i` from `n` down to `0` (suffix view), or flip the meaning to a prefix view, which reads more naturally:

> `dp[i][s]` = number of subsequences of the **first `i` elements** whose sum is exactly `s`.

Then `dp[i][s] = dp[i-1][s] + dp[i-1][s - arr[i-1]]`, so row `i` needs row `i - 1`: fill rows **top to bottom**. Within a row, any column order works because a row only reads the previous row.

### Q7. Can the state space be optimized?

Row `i` reads only row `i - 1`. So we keep two arrays of length `K + 1` instead of the whole `(n + 1) × (K + 1)` table: `O(K)` memory. (In [0/1 Knapsack](../02-zero-one-knapsack/) we go down to one array, and the loop direction suddenly matters.)

## 5. Brute force: recursion

### 5a. Generating every subsequence (backtracking)

```java
// current holds the elements chosen so far along this path of the recursion tree
private static void generate(int[] arr, int index, List<Integer> current, List<List<Integer>> result) {
    if (index == arr.length) {
        result.add(new ArrayList<>(current));   // copy! current keeps changing afterwards
        return;
    }
    current.add(arr[index]);                    // TAKE
    generate(arr, index + 1, current, result);
    current.remove(current.size() - 1);         // undo the take (backtrack)

    generate(arr, index + 1, current, result);  // SKIP
}
```

Full code: [`solution/GenerateSubsequences.java`](solution/GenerateSubsequences.java).

The recursion tree for `[1, 2, 3]`. Left edges are **take**, right edges are **skip**; each node shows the list `current` at that moment.

```
                                   []                          index 0: decide 1
                     take 1 /            \ skip 1
                       [1]                    []               index 1: decide 2
              take 2 /     \ skip 2     take 2 /   \ skip 2
               [1,2]        [1]          [2]        []         index 2: decide 3
              /    \       /    \       /   \      /   \
         [1,2,3] [1,2]  [1,3]  [1]   [2,3]  [2]   [3]   []     index 3: leaves = answers
```

The leaves, read left to right, are exactly the output order of the program:
`[1,2,3] [1,2] [1,3] [1] [2,3] [2] [3] []`.

Why the "undo" line? `current` is one shared list. After the take-branch returns, `current` still contains `arr[index]`. If we did not remove it, the skip-branch would wrongly start with the element it was supposed to skip.

**Cost:** `2^n` leaves, and at each leaf we copy up to `n` numbers, so `O(n · 2^n)` time. This is the best possible when you must *output* every subsequence — the output itself has that size.

### 5b. Counting subsequences with sum `K`

When we only want a count, we do not need the list — just the running sum:

```java
private static long count(int[] arr, int index, int sumSoFar, int k) {
    if (index == arr.length) {
        return (sumSoFar == k) ? 1 : 0;
    }
    long take = count(arr, index + 1, sumSoFar + arr[index], k);
    long skip = count(arr, index + 1, sumSoFar, k);
    return take + skip;
}
```

Full code: [`solution/BruteForce.java`](solution/BruteForce.java).

The same tree, now labelled with the state `f(index, sumSoFar)`, for `arr = [1, 2, 3]`, `K = 3`:

```
                                    f(0,0)
                      take 1 /                \ skip 1
                       f(1,1)                    f(1,0)
             take 2 /         \ skip 2   take 2 /        \ skip 2
              f(2,3)          f(2,1)      f(2,2)          f(2,0)
             /     \          /    \      /    \          /    \
         f(3,6)  f(3,3)  f(3,4)  f(3,1) f(3,5) f(3,2)  f(3,3)  f(3,0)
           0       1       0       0      0      0       1       0
        [1,2,3]  [1,2]   [1,3]    [1]  [2,3]    [2]     [3]     []
```

Adding the leaves: `0+1+0+0+0+0+1+0 = 2`. ✔

**Why it is slow:** the tree always has `2^n` leaves, whatever `K` is. With `n = 30` that is about 10^9 calls.

**But look at the labels.** `f(3,3)` appears twice — reached by "take 1, take 2, skip 3" and by "skip 1, skip 2, take 3". Both calls ask the *same question* ("all decided, is the sum 3?") and get the same answer. With longer arrays the repeats move up the tree, where each repeat hides a whole subtree. That is the signal for memoization.

## 6. Memoization (top-down)

**What is cached:** `memo[index][sumSoFar]` = the value of `count(index, sumSoFar)`. Because elements are non-negative, we prune `sumSoFar > K`, so `sumSoFar` always lies in `0..K` and a `(n+1) × (K+1)` array is enough. `-1` means "not computed yet" (a real count is never negative).

```java
private static long count(int[] arr, int index, int sumSoFar, int k) {
    if (sumSoFar > k) return 0;                       // overshoot: no way back with non-negative numbers
    if (index == arr.length) return (sumSoFar == k) ? 1 : 0;
    if (memo[index][sumSoFar] != -1) return memo[index][sumSoFar];

    long take = count(arr, index + 1, sumSoFar + arr[index], k);
    long skip = count(arr, index + 1, sumSoFar, k);
    memo[index][sumSoFar] = take + skip;
    return memo[index][sumSoFar];
}
```

Full code: [`solution/Memoization.java`](solution/Memoization.java).

**Why caching is safe:** the answer of `count(index, sumSoFar)` depends only on the elements from `index` onward and on `sumSoFar`. It does not depend on *which* earlier elements produced `sumSoFar`. So the first computed answer is valid for every later path that reaches the same pair.

### Dry run on `[1, 2, 3]`, `K = 3`

Calls in the order they happen (indentation = recursion depth):

```
f(0,0)
  f(1,1)                         take 1
    f(2,3)                       take 2
      f(3,6) -> 0                pruned: 6 > 3
      f(3,3) -> 1                base case: sum is 3
    memo[2][3] = 1
    f(2,1)                       skip 2
      f(3,4) -> 0                pruned
      f(3,1) -> 0                base case: sum 1 ≠ 3
    memo[2][1] = 0
  memo[1][1] = 1
  f(1,0)                         skip 1
    f(2,2)                       take 2
      f(3,5) -> 0                pruned
      f(3,2) -> 0
    memo[2][2] = 0
    f(2,0)                       skip 2
      f(3,3) -> 1                same question as before — a base case, so O(1) anyway
      f(3,0) -> 0
    memo[2][0] = 1
  memo[1][0] = 1
memo[0][0] = 2   ← answer
```

In this tiny example the only repeated state is a base case, so the cache does not save anything yet. Here is where it pays off — `arr = [1, 1, 1, 1]`, `K = 2`:

```
f(0,0) ─ take ─> f(1,1) ─ skip ─> f(2,1)    computed once: 2 ways ({a2} or {a3}), stored in memo[2][1]
f(0,0) ─ skip ─> f(1,0) ─ take ─> f(2,1)    CACHE HIT — whole subtree skipped
```

The number of distinct states is at most `(n + 1) × (K + 1)`. For 30 ones and `K = 15` that is 31 × 16 = 496 states instead of 2^30 ≈ 10^9 calls; [`Memoization.java`](solution/Memoization.java) checks that the answer is C(30, 15) = 155 117 520.

## 7. Tabulation (bottom-up)

We switch to the **prefix view** (Q6): `dp[i][s]` = number of subsequences of the first `i` elements with sum exactly `s`.

- Base row: `dp[0][0] = 1` (the empty subsequence has sum 0), `dp[0][s] = 0` for `s > 0`.
- Transition for `i ≥ 1` with `value = arr[i-1]`:

```
dp[i][s] = dp[i-1][s]                       ← skip arr[i-1]
         + dp[i-1][s - value]  (if s ≥ value) ← take arr[i-1]: the rest must sum to s - value
```

```java
long[][] dp = new long[n + 1][k + 1];
dp[0][0] = 1;
for (int i = 1; i <= n; i++) {
    int value = arr[i - 1];
    for (int s = 0; s <= k; s++) {
        long skip = dp[i - 1][s];
        long take = (s >= value) ? dp[i - 1][s - value] : 0;
        dp[i][s] = skip + take;
    }
}
return dp[n][k];
```

Full code: [`solution/Tabulation.java`](solution/Tabulation.java).

### Filling the table for `[1, 2, 3]`, `K = 3`, step by step

Start (only row 0 known):

```
                  s=0  s=1  s=2  s=3
i=0  {}            1    0    0    0
i=1  {1}           ?    ?    ?    ?
i=2  {1,2}         ?    ?    ?    ?
i=3  {1,2,3}       ?    ?    ?    ?
```

Row 1 (element `1`):

```
dp[1][0] = dp[0][0]                 = 1        (skip 1: the empty subsequence)
dp[1][1] = dp[0][1] + dp[0][0]      = 0+1 = 1  ({1})
dp[1][2] = dp[0][2] + dp[0][1]      = 0
dp[1][3] = dp[0][3] + dp[0][2]      = 0
```

Row 2 (element `2`):

```
dp[2][0] = dp[1][0]                 = 1
dp[2][1] = dp[1][1]                 = 1        (2 > 1, cannot take)
dp[2][2] = dp[1][2] + dp[1][0]      = 0+1 = 1  ({2})
dp[2][3] = dp[1][3] + dp[1][1]      = 0+1 = 1  ({1,2})
```

Row 3 (element `3`):

```
dp[3][0] = 1, dp[3][1] = 1, dp[3][2] = 1       (3 too big to take, copy from above)
dp[3][3] = dp[2][3] + dp[2][0]      = 1+1 = 2  ({1,2} without the 3, or {} + 3 = {3})
```

Final table:

```
                  s=0  s=1  s=2  s=3
i=0  {}            1    0    0    0
i=1  {1}           1    1    0    0
i=2  {1,2}         1    1    1    1
i=3  {1,2,3}       1    1    1    2   ← answer dp[3][3] = 2
```

Notice the picture: each cell looks **straight up** (skip) and **up and `value` columns to the left** (take).

```
row i-1:   ... [s - value] ........ [s] ...
                     \               |
                      \ take         | skip
                       \             v
row i:                  ---------> [s]
```

## 8. Space optimization

Row `i` uses only row `i - 1`. So keep two arrays, `prev` and `curr`:

```java
long[] prev = new long[k + 1];
prev[0] = 1;
for (int value : arr) {
    long[] curr = new long[k + 1];
    for (int s = 0; s <= k; s++) {
        curr[s] = prev[s] + ((s >= value) ? prev[s - value] : 0);
    }
    prev = curr;
}
return prev[k];
```

Full code: [`solution/Optimized.java`](solution/Optimized.java).

Memory drops from `(n + 1) × (K + 1)` numbers to `2 × (K + 1)`. You can shrink further to **one** array, but then the inner loop must run from high `s` to low `s`. That trick, and why it is needed, is the main lesson of the [next topic](../02-zero-one-knapsack/walkthrough.md#8-space-optimization).

## 9. Dry run on a full example

Two-row version on `[1, 2, 3]`, `K = 3` — the arrays after each element:

| Step | Element | `prev` before | `curr` after (`s = 0..3`) | What changed |
|---|---|---|---|---|
| start | — | — | `[1, 0, 0, 0]` | only the empty subsequence |
| 1 | `1` | `[1, 0, 0, 0]` | `[1, 1, 0, 0]` | `{1}` gives sum 1 |
| 2 | `2` | `[1, 1, 0, 0]` | `[1, 1, 1, 1]` | `{2}` gives 2, `{1,2}` gives 3 |
| 3 | `3` | `[1, 1, 1, 1]` | `[1, 1, 1, 2]` | `{3}` gives 3 (sums above 3 are not tracked) |

Answer: `curr[3] = 2`. Brute force, memoization, tabulation and two rows all give **2**.

## 10. Complexity analysis (explained, for every approach)

| Approach | Time | Space | Why |
|---|---|---|---|
| Generate all | `O(n · 2^n)` | `O(n)` extra + output | `2^n` leaves, each copied in `O(n)`; stack depth `n` |
| Brute-force count | `O(2^n)` | `O(n)` | every call makes two calls until depth `n`; stack depth `n` |
| Memoization | `O(n · K)` | `O(n · K)` | at most `(n+1)(K+1)` states, each computed once with `O(1)` work; table plus `O(n)` stack |
| Tabulation | `O(n · K)` | `O(n · K)` | the double loop visits each of the `(n+1)(K+1)` cells once |
| Two rows | `O(n · K)` | `O(K)` | same cells, but only two rows stored |

Note what `O(n · K)` means: the running time grows with the **value** of `K`, not just the number of elements. With `n = 100` and `K = 10^5`, that is 10^7 steps — fast. With `K = 10^12` it would be hopeless, even though the input is tiny. Algorithms like this are called **pseudo-polynomial**. Topic 02 shows a trick for when the capacity is huge but another quantity is small.

## 11. Why this approach works (correctness argument in plain words)

1. **Every subsequence is produced exactly once.** A subsequence is fully described by its `n` take/skip decisions, and the recursion explores every combination of decisions, each along a separate root-to-leaf path.
2. **The count is right.** Each leaf contributes 1 exactly when its sum is `K`; the inner nodes add their two children, and the two children cover disjoint sets of subsequences (with and without `arr[i]`).
3. **Caching is valid.** The future of a call depends only on `(i, sumSoFar)`, so equal pairs have equal answers.
4. **Pruning is valid** only for non-negative elements: once the sum exceeds `K`, adding more non-negative numbers can never reduce it back to `K`.

## 12. Common mistakes

1. **Forgetting to copy** the current list at a leaf (`result.add(current)`): every stored entry is the same list object and ends up empty.
2. **Forgetting to backtrack** (`current.remove(...)`): the skip-branch carries the element it was supposed to skip.
3. **Excluding the empty subsequence.** For `K = 0` with positive elements the answer is `1`, not `0`. Decide on purpose whether the problem counts it.
4. **Memoizing with negative numbers.** With negatives, `sumSoFar` can be below 0 (array index out of bounds) and the pruning `sumSoFar > K` is wrong (a later negative could bring the sum back). You need an offset; [Target Sum](../06-target-sum/walkthrough.md) shows how.
5. **Stopping early when the sum hits `K`.** With zeros in the array, `[0, 1]` and `K = 1` has two answers: `[1]` and `[0, 1]`. A base case like "if `sumSoFar == K` return 1" (before reaching `i == n`) misses the second one.
6. **Counting values instead of positions.** `[1, 2, 1]` with `K = 3` has 2 subsequences (`[1,2]` and `[2,1]`), even though both use the values 1 and 2.

## 13. When to use this technique / how to recognize similar problems

Use take/skip with an accumulated state when the problem:

- gives you a list of items and asks you to **choose some of them** (subset, subsequence, selection);
- has a **numeric limit or target** on the chosen items (sum, weight, cost, count);
- asks for a **count**, a **best value**, or **yes/no**.

Then ask: "What single number summarizes my past choices well enough to decide the future?" That number (sum, weight, amount) goes into the state next to the index. If its range is small (≤ about 10^5–10^6), DP is fast.

If you must **list** all valid subsequences, DP does not help — the output itself is exponential. Use backtracking.

## 14. Summary and revision notes

- Subarray = contiguous; subsequence = keep order, gaps allowed (`2^n` of them); subset = order irrelevant. For selection problems, subset = subsequence.
- Take/skip recursion: two branches per index, `2^n` leaves.
- Generating all subsequences: backtracking, `O(n · 2^n)`; copy at leaves, undo after take.
- Counting with sum `K`: state = index + accumulated sum. Same pair ⇒ same answer ⇒ cache.
- Combine with `+` for counts, `max`/`min` for best, `||` for feasibility.
- Pseudo-polynomial: `O(n · K)` depends on the value `K`.

```
dp[i][s] = number of subsequences of arr[0..i-1] whose sum is exactly s

dp[0][0] = 1           dp[0][s] = 0 for s > 0
dp[i][s] = dp[i-1][s] + dp[i-1][s - arr[i-1]]     (second term only if s >= arr[i-1])
answer   = dp[n][K]    time O(n·K), space O(K) with two rows
```

## Exercises

1. How many subsequences does `[4, 4, 4]` have, and how many of them have each possible sum?

<details>
<summary>Answer</summary>

`2^3 = 8` subsequences (positions differ even though the values are equal). Sum 0: 1 (empty). Sum 4: 3 (any single 4). Sum 8: 3 (any pair). Sum 12: 1 (all three). Total 1 + 3 + 3 + 1 = 8.
</details>

2. Fill the table `dp[i][s]` for `arr = [2, 1, 1]`, `K = 2`. What is the answer?

<details>
<summary>Answer</summary>

```
           s=0 s=1 s=2
i=0         1   0   0
i=1 (2)     1   0   1
i=2 (1)     1   1   1
i=3 (1)     1   2   2
```
Answer `dp[3][2] = 2`: `[2]` and `[1, 1]`.
</details>

3. Change the counting recurrence so that it answers "**does** at least one subsequence with sum `K` exist?" What changes in the base case, the combine step and the table type?

<details>
<summary>Answer</summary>

The table holds `boolean`. Base case: `dp[0][0] = true`, `dp[0][s>0] = false`. Combine with `||` instead of `+`: `dp[i][s] = dp[i-1][s] || (s ≥ arr[i-1] && dp[i-1][s - arr[i-1]])`. That is exactly [Subset Sum](../03-subset-sum/), topic 03.
</details>

4. For `arr = [-1, 1]` and `K = 0`, what is the correct count, and which line of `Memoization.java` would break?

<details>
<summary>Answer</summary>

The answer is 2: `[]` and `[-1, 1]`. Two things break:

- Taking `-1` first gives `sumSoFar = -1`, and `memo[index][-1]` throws `ArrayIndexOutOfBoundsException`.
- The pruning `if (sumSoFar > k) return 0;` is unsafe once negatives exist. In `[1, -1]` with `K = 0`, taking `1` makes the sum 1 > 0, yet taking `-1` next returns to 0 — pruning would lose that subsequence.

Fix: remove the pruning and store `sumSoFar + offset` in the memo, where `offset` is the sum of the absolute values of the negative numbers. [Target Sum](../06-target-sum/walkthrough.md) uses exactly this offset idea.
</details>
