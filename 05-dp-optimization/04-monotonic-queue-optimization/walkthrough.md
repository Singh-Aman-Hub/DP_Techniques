# Monotonic Queue Optimization — Walkthrough

> Prerequisite example: **LeetCode 239 Sliding Window Maximum**, `nums = [1, 3, -1, -3, 5, 3, 6, 7]`, `k = 3`.
> Main DP example: **LeetCode 1696 Jump Game VI**, `nums = [1, -1, -2, 4, -7, 3]`, `k = 2`, answer `7`.
> Variant: **LeetCode 1425 Constrained Subsequence Sum**.

---

## 1. Intuition

Picture a talent show where every contestant stays on stage for exactly `k` rounds, and after each round the
judges ask, "who is the strongest person on stage right now?"

A tired judge looks at everyone on stage every round. A clever judge notices something: **if a newcomer is at
least as strong as someone who arrived earlier, that earlier person will never again be the strongest** —
the newcomer is better *and* stays longer. So the clever judge sends such people home immediately (in her
notebook). The people left in the notebook are ordered from strongest-and-oldest to weakest-and-newest, and
the answer is always the first name in the notebook.

That notebook is a **monotonic deque**. In DP, the "contestants" are the last `k` computed values
`dp[i−k..i−1]`, and every new cell asks "what is the best of them?"

---

## 2. Problem statement in our own words

**Jump Game VI** ([LeetCode 1696](https://leetcode.com/problems/jump-game-vi/)).
You stand on index `0` of an integer array. From index `i` you may jump forward by `1` to `k` positions (never
past the end). Your score is the sum of the values of every index you land on, including the first and the
last. Reach the last index with the maximum possible score.

**Sliding Window Maximum** ([LeetCode 239](https://leetcode.com/problems/sliding-window-maximum/)) is the
prerequisite: for every window of `k` consecutive elements, report its maximum.

---

## 3. Examples

| Problem | Input | Output | Why |
|---|---|---|---|
| LC 239 | `[1,3,-1,-3,5,3,6,7]`, `k=3` | `[3,3,5,5,6,7]` | windows `[1,3,-1]`, `[3,-1,-3]`, `[-1,-3,5]`, … |
| LC 1696 | `[1,-1,-2,4,-7,3]`, `k=2` | `7` | land on `1, -1, 4, 3` (skip `-2` and `-7`) |
| LC 1696 | `[10,-5,-2,4,0,3]`, `k=3` | `17` | `10, 4, 3` |
| LC 1696 | `[5]`, `k=1` | `5` | already at the last index |

For the main example, the jumps:

```
index:   0    1    2    3    4    5
nums :   1   -1   -2    4   -7    3
path :   *----*---------*---------*        1 + (-1) + 4 + 3 = 7
            +1    +2        +2
```

---

## 4. Deriving the solution — the 7 questions

### 4.1 What is changing?
Only our position, the index `i`.

### 4.2 What does the state represent?
`dp[i]` = *the maximum score of any legal path that starts at index 0 and ends exactly at index `i`.*
The answer is `dp[n−1]`.

### 4.3 What are the choices?
How did we arrive at `i`? By a jump from some `j` with `i − k ≤ j ≤ i − 1` (and `j ≥ 0`).

### 4.4 What is the recurrence?
Whichever `j` we came from, we add `nums[i]`; we want the best `j`:

```
dp[i] = nums[i] + max( dp[j] : max(0, i−k) ≤ j ≤ i−1 )
```

The `max` ranges over a **window of the last `k` dp values**.

### 4.5 What are the base cases?
`dp[0] = nums[0]`: the path that has not moved yet scores just the starting cell.

### 4.6 What is the order of computation?
`dp[i]` needs only smaller indices, so left to right.

### 4.7 Can the state space be optimized?
**Space:** we need the last `k` values, not all `n` (a ring buffer of size `k` works), but `O(n)` is fine here.
**Time:** the window scan costs up to `k` per cell, so `O(n·k)`. With `n, k ≤ 10^5` that is `10^10` — too slow.
The windows of neighbouring cells overlap in `k−1` positions:

```
dp[i]   reads  dp[i-k   .. i-1]
dp[i+1] reads  dp[i-k+1 .. i  ]        drop one old value, add one new value
```

Can we keep "the max of the window" up to date when one value leaves and one enters?
- A running sum could do it (add the new, subtract the old) — but this is a `max`, and `max` cannot be undone
  (see [Prefix and Suffix Optimization](../03-prefix-and-suffix-optimization/), Section 4.10).
- A prefix maximum does not help either: the window's left end moves.

We need a new tool: the monotonic deque.

### 4.8 Prerequisite: the monotonic deque (taught on LC 239)

**Deque** ("deck") = double-ended queue: you can add/remove at the front and at the back in O(1).
In Java: `ArrayDeque` with `offerLast`, `pollLast`, `peekLast`, `pollFirst`, `peekFirst`.

**Monotonic** = the values it represents are always sorted. For maxima we keep them **strictly decreasing
from front to back**. We store *indices* (so we can tell when an element has left the window) and compare
their *values*.

**The domination rule.** Suppose `p < q` (q arrived later) and `nums[p] ≤ nums[q]`. Every future window
that contains `p` also contains `q` (because `q` leaves the window later than `p`). So `p` can never be the
strictly better choice. We may delete `p` forever. After deleting all dominated indices, the survivors have
strictly decreasing values from oldest to newest.

**One step for a new index `i`:**

```
1. EXPIRE : if the front index ≤ i − k, it is outside the window [i−k+1, i]; pop it from the front.
2. DOMINATE: while the back value ≤ nums[i], pop the back (it is older and not bigger).
3. PUSH   : add i at the back.
4. READ   : the front is the index of the window maximum.
```

**Trace on `nums = [1, 3, -1, -3, 5, 3, 6, 7]`, `k = 3`** (deque shows `index(value)`, front on the left):

| i | nums[i] | expire front? | pop from back | deque after push | window | max |
|---|---|---|---|---|---|---|
| 0 | 1 | — | — | `0(1)` | not full | — |
| 1 | 3 | — | `0(1)` since 1 ≤ 3 | `1(3)` | not full | — |
| 2 | −1 | — | — | `1(3) 2(−1)` | `[0..2]` | 3 |
| 3 | −3 | no (1 > 0) | — | `1(3) 2(−1) 3(−3)` | `[1..3]` | 3 |
| 4 | 5 | yes, `1` (1 ≤ 4−3) | `3(−3)`, `2(−1)` | `4(5)` | `[2..4]` | 5 |
| 5 | 3 | no | — | `4(5) 5(3)` | `[3..5]` | 5 |
| 6 | 6 | no (4 > 3) | `5(3)`, `4(5)` | `6(6)` | `[4..6]` | 6 |
| 7 | 7 | no | `6(6)` | `7(7)` | `[5..7]` | 7 |

Output `[3, 3, 5, 5, 6, 7]`. ✔

**Why it is O(n) overall, even though step 2 is a `while` loop.** Count deque operations over the *whole run*,
not per step: every index is pushed exactly once and popped at most once (from the front *or* the back).
So there are at most `n` pushes and `n` pops: `2n` operations in total. A single step may pop many elements,
but then later steps have fewer to pop. This style of counting is called **amortized analysis**: an expensive
step is "paid for" by the earlier cheap pushes.

```
total pops  ≤  total pushes  =  n          =>   whole algorithm  O(n)
```

Code: [solution/SlidingWindowMaximum.java](solution/SlidingWindowMaximum.java) (brute force + deque, with a
randomized cross-check).

> **Why not a heap?** A max-heap (`PriorityQueue`) with "lazy deletion" of expired indices also works, in
> `O(n log n)`. It is a fine fallback, but the deque is simpler and faster.

### 4.9 The key property and why it holds

The deque solves Jump Game VI because the DP transition has exactly the shape of LC 239:

> **`dp[i] = f(i) + max/min of dp[j]` over a window `L(i) ≤ j ≤ R(i)` where both ends only move forward
> (`L` and `R` are non-decreasing in `i`), and each `dp[j]` never changes after it is computed.**

1. **Values are final.** `dp[j]` is computed once and never updated, so "dominated forever" is really forever.
2. **The right end moves forward by one:** after computing `dp[i]`, index `i` becomes available to all later
   cells. It enters at the back.
3. **The left end only moves forward:** an index that leaves the window never comes back, so expiring it from
   the front is safe.
4. **Domination still holds:** if `j < j'` and `dp[j] ≤ dp[j']`, then `j` leaves every future window no later
   than `j'`, so `j'` is always at least as good. Delete `j`.

Numeric check on the main example, `k = 2`: at `i = 3` the window is `{1, 2}` with `dp[1] = 0`, `dp[2] = −1`.
After computing `dp[3] = 4`, both `1` and `2` are dominated by `3` (older and smaller). Indeed, for `i = 4` the
window is `{2, 3}` and for `i = 5` it is `{3, 4}`; index `3` wins in both.

**The order inside one DP step matters** (it differs slightly from LC 239):

```
expire indices < i − k   →   READ front to compute dp[i]   →   pop dominated   →   push i
```

We must *read before pushing* `i`, because `i` cannot jump to itself.

---

## 5. Brute force: recursion

`best(from)` = maximum score from index `from` to the last index.

```java
static long bruteForce(int[] nums, int k, int from) {
    if (from == nums.length - 1) return nums[from];
    long best = Long.MIN_VALUE;
    for (int step = 1; step <= k && from + step < nums.length; step++)
        best = Math.max(best, bruteForce(nums, k, from + step));
    return nums[from] + best;
}
```

Recursion tree for the main example (`k = 2`), nodes are indices:

```
                        0
                 /             \
               1                 2
            /     \           /     \
           2       3         3       4
          / \     / \       / \      |
         3   4   4   5     4   5     5
        / \  |   |         |
       4   5 5   5         5
       |
       5
```

(Every node `4` has the single child `5`; to keep the drawing small, that child is shown only for some of them.)
`best(3)`, `best(4)` and `best(5)` are recomputed many times. The tree has up to `k^n` paths — exponential.

---

## 6. Memoization (top-down)

Caching `best(from)` gives `n` states, each scanning up to `k` successors: `O(n·k)`, the same as the table in
Section 7. We skip a separate file — the deque speed-up needs the values in left-to-right order with a
sliding window, which is natural only in the bottom-up form.

---

## 7. Tabulation (bottom-up)

```java
long[] dp = new long[n];
dp[0] = nums[0];
for (int i = 1; i < n; i++) {
    long bestPrevious = Long.MIN_VALUE;
    for (int j = Math.max(0, i - k); j < i; j++) bestPrevious = Math.max(bestPrevious, dp[j]);  // O(k) scan
    dp[i] = nums[i] + bestPrevious;
}
return dp[n - 1];
```

Filling the table for `nums = [1, -1, -2, 4, -7, 3]`, `k = 2`:

| i | nums[i] | window `j` | window dp values | max | dp[i] |
|---|---|---|---|---|---|
| 0 | 1 | — | — | — | 1 |
| 1 | −1 | 0 | `1` | 1 | 0 |
| 2 | −2 | 0..1 | `1, 0` | 1 | −1 |
| 3 | 4 | 1..2 | `0, −1` | 0 | 4 |
| 4 | −7 | 2..3 | `−1, 4` | 4 | −3 |
| 5 | 3 | 3..4 | `4, −3` | 4 | **7** |

```
dp:  [ 1,  0, -1,  4, -3,  7 ]
```

Full code: [solution/Slow.java](solution/Slow.java) (brute force and this table).

---

## 8. Optimization: monotonic deque

Replace the `O(k)` scan by the deque from Section 4.8, storing indices with **strictly decreasing `dp`**:

```java
long[] dp = new long[n];
dp[0] = nums[0];
Deque<Integer> candidates = new ArrayDeque<>();
candidates.offerLast(0);
for (int i = 1; i < n; i++) {
    while (candidates.peekFirst() < i - k) candidates.pollFirst();          // expire
    dp[i] = nums[i] + dp[candidates.peekFirst()];                            // read the window max
    while (!candidates.isEmpty() && dp[candidates.peekLast()] <= dp[i])      // pop dominated
        candidates.pollLast();
    candidates.offerLast(i);                                                 // push
}
return dp[n - 1];
```

Full code with a 1000-case randomized cross-check against the slow table:
[solution/Optimized.java](solution/Optimized.java).

### 8.1 Variant: LeetCode 1425 Constrained Subsequence Sum

[Constrained Subsequence Sum](https://leetcode.com/problems/constrained-subsequence-sum/): choose a non-empty
subsequence so that consecutive chosen indices are at most `k` apart; maximize its sum.

Seven questions, briefly:
- State: `dp[i]` = best sum of a valid subsequence whose **last** chosen index is `i`.
- Choices: `i` is the first chosen element (nothing before it), or the previous chosen index `j` is in `[i−k, i−1]`.
- Recurrence: `dp[i] = nums[i] + max(0, max(dp[i−k..i−1]))` — the `0` means "start fresh here".
- Base: `dp[0] = nums[0]`. Answer: `max over all i of dp[i]` (the subsequence may end anywhere).
- Order: left to right. Optimization: identical window, identical deque.

Trace on `nums = [10, 2, −10, 5, 20]`, `k = 2`:

| i | nums[i] | deque before (index(dp)) | window max | dp[i] | deque after |
|---|---|---|---|---|---|
| 0 | 10 | empty | 0 (start fresh) | 10 | `0(10)` |
| 1 | 2 | `0(10)` | 10 | 12 | `1(12)` (pops `0`) |
| 2 | −10 | `1(12)` | 12 | 2 | `1(12) 2(2)` |
| 3 | 5 | `1(12) 2(2)` | 12 | 17 | `3(17)` |
| 4 | 20 | `3(17)` | 17 | 37 | `4(37)` |

Answer `37` (`10 + 2 + 5 + 20`). Code: [solution/ConstrainedSubsequenceSum.java](solution/ConstrainedSubsequenceSum.java).

### 8.2 Two problems that use the same deque outside a plain DP

- [Max Value of Equation](https://leetcode.com/problems/max-value-of-equation/) (LC 1499): points sorted by `x`,
  maximize `y_i + y_j + |x_i − x_j|` with `x_j − x_i ≤ k`. For `i < j` this is `(y_i − x_i) + (y_j + x_j)`.
  For each `j`, you want the maximum of `y_i − x_i` over earlier points whose `x_i ≥ x_j − k` — a sliding
  window maximum where the window is defined by `x` values instead of a fixed count.
- [Shortest Subarray with Sum at Least K](https://leetcode.com/problems/shortest-subarray-with-sum-at-least-k/)
  (LC 862): with prefix sums `P`, you want the latest `i < j` with `P[j] − P[i] ≥ K`. Keep a deque of indices
  with **increasing** `P`. A larger `P[i]` that is older is dominated by a newer smaller one; and once an index
  at the front has produced an answer, it can be popped because later `j` would only give longer subarrays.
  This shows the deque can be monotonic in either direction and can pop from the front for reasons other
  than "expired".

---

## 9. Dry run on a full example

Jump Game VI, `nums = [1, −1, −2, 4, −7, 3]`, `k = 2`, optimized version (deque as `index(dp)`):

| i | nums[i] | expire (`front < i−k`) | front read | dp[i] | pop from back | deque after |
|---|---|---|---|---|---|---|
| 0 | 1 | — | — | 1 | — | `0(1)` |
| 1 | −1 | `0 < −1`? no | `0(1)` | 0 | `1 ≤ 0`? no | `0(1) 1(0)` |
| 2 | −2 | `0 < 0`? no | `0(1)` | −1 | `0 ≤ −1`? no | `0(1) 1(0) 2(−1)` |
| 3 | 4 | `0 < 1` yes → pop `0` | `1(0)` | 4 | pop `2(−1)`, `1(0)` | `3(4)` |
| 4 | −7 | `3 < 2`? no | `3(4)` | −3 | no | `3(4) 4(−3)` |
| 5 | 3 | `3 < 3`? no | `3(4)` | **7** | pop `4(−3)`, `3(4)` | `5(7)` |

The dp values `[1, 0, −1, 4, −3, 7]` match the slow table in Section 7 exactly. Total pushes: 6; total pops: 5.

Note that at `i = 2` the deque briefly held `k + 1 = 3` indices: index `0` will expire at the start of the
next step. That is normal.

---

## 10. Complexity analysis

| Approach | Time | Why | Space |
|---|---|---|---|
| Brute force | `O(k^n)` worst case | each index branches into up to `k` jumps | `O(n)` stack |
| Memoization / slow table | `O(n·k)` | `n` cells × a window scan of `k` | `O(n)` |
| Monotonic deque | `O(n)` | each index is pushed once and popped at most once: ≤ `2n` deque operations in total | `O(n)` for dp, `O(k)` deque |
| Heap alternative | `O(n log n)` | each push/pop costs `log` of the heap size | `O(n)` |

For `n = k = 10^5`: slow ≈ `10^10` steps; deque ≈ `2·10^5`.

---

## 11. Why this approach works

**Invariant.** Just before computing `dp[i]`, after expiring, the deque contains (in increasing index order)
exactly those indices `j` of the window `[i−k, i−1]` such that no later index in the window has a `dp` value
`≥ dp[j]`. Their `dp` values are therefore strictly decreasing, and the front holds the window maximum.

- **Initially** the deque holds just `0`; the invariant holds trivially.
- **Popping from the back** before pushing `i` removes exactly the indices whose `dp` is `≤ dp[i]` — the ones
  that just became dominated. Everything left is `> dp[i]`, so the order stays strictly decreasing.
- **Expiring from the front** removes indices that left the window. Only the front can be expired, because
  indices are in increasing order and the oldest index leaves first.
- **No needed index is ever deleted**: a dominated index `j` has a later `j'` with `dp[j'] ≥ dp[j]` that stays in
  every window `j` could be in. So the max over the deque equals the max over the window.

Hence the optimized `dp` equals the slow `dp` at every index, which the randomized test in `Optimized.java`
also checks on 1000 inputs.

---

## 12. Common mistakes

1. **Storing values instead of indices.** You then cannot tell when the front has left the window.
2. **Reading the front after pushing `i`.** In the DP, `dp[i]` must use the window `[i−k, i−1]`; pushing `i`
   first would let `i` "jump to itself".
3. **Wrong expiry condition.** For window `[i−k, i−1]`, expire while `front < i − k`. In LC 239 (window
   `[i−k+1, i]`), expire while `front ≤ i − k`. Write the window bounds down before coding.
4. **`<` versus `≤` when popping dominated elements.** Popping on `≤` keeps values strictly decreasing and the
   deque small. Popping only on `<` is also correct, but keeps duplicates.
5. **Using a deque when the window is not monotone.** If the left end can move backwards, or old `dp` values
   change, domination is no longer permanent and the deque is wrong.
6. **Forgetting the "start fresh" `0`** in LC 1425, or returning `dp[n−1]` instead of the maximum over all `i`.
7. **Comparing `Integer` objects with `==`.** `deque.peekFirst() == deque.peekLast()` compares references for
   large values. Compare indices with `<`/`>` (auto-unboxed) or unbox explicitly.

---

## 13. When to use this technique / how to recognize similar problems

Look for a transition of the form

```
dp[i] = (something about i) + max or min of dp[j]  for j in a window [L(i), R(i)]
        where L(i) and R(i) never decrease as i increases
```

Typical wording: "jump at most `k` steps", "consecutive chosen elements at most `k` apart", "within distance
`k`", "the last `k` days". If the window always starts at 0, a prefix max is enough
([previous topic](../03-prefix-and-suffix-optimization/)). If the inner term depends on **both** `i` and `j` in
a product form like `a[i]·b[j]`, the window trick is not enough — that is the
[Convex Hull Trick](../06-convex-hull-trick/). Bounded knapsack (each item up to `c` copies) can also be sped
up with a monotonic queue per remainder class — an advanced application of the same idea.

---

## 14. Summary and revision notes

- Monotonic deque = indices whose values are strictly decreasing (for max); the front is the window max.
- Each step: expire front → (DP: read front) → pop dominated from back → push.
- Amortized `O(1)` per step because each index is pushed and popped at most once.
- Works whenever the window's both ends move forward and values never change after being computed.

```
+---------------------------------------------------------------+
|  dp[i] = nums[i] + max( dp[j] : i-k <= j <= i-1 )             |
|  front of deque = argmax over the window                      |
|  O(n·k)  ──monotonic deque──►  O(n)                           |
|  LC 1425:  dp[i] = nums[i] + max(0, window max); answer=max dp |
+---------------------------------------------------------------+
```

---

## 15. Exercises

**Exercise 1.** Run the LC 239 deque by hand on `nums = [4, 2, 12, 3, 1]`, `k = 2`. Give the deque after each
step and the output.

<details><summary>Answer</summary>

```
i=0: [0(4)]                    window not full
i=1: [0(4) 1(2)]               max 4
i=2: expire 0 (0 ≤ 0); pop 1(2) -> [2(12)]      max 12
i=3: [2(12) 3(3)]              max 12
i=4: expire 2 (2 ≤ 2) -> [3(3)]; push -> [3(3) 4(1)]   max 3
```
Output `[4, 12, 12, 3]`.
</details>

**Exercise 2.** Jump Game VI with `nums = [10, −5, −2, 4, 0, 3]`, `k = 3`. Fill `dp` using the deque.

<details><summary>Answer</summary>

`dp = [10, 5, 8, 14, 14, 17]`.
`dp[1] = −5 + 10`, `dp[2] = −2 + 10`, `dp[3] = 4 + 10` (window `0..2`, front `0`),
`dp[4] = 0 + 14` (index 0 expired; front `3`), `dp[5] = 3 + 14`. Answer **17**.
</details>

**Exercise 3.** Why is it wrong to use the deque for `dp[i] = nums[i] + max(dp[j])` over `j ∈ [i/2, i−1]`? Or is it?

<details><summary>Answer</summary>

It is actually fine: `L(i) = ⌈i/2⌉` and `R(i) = i − 1` are both non-decreasing in `i`, and the values are final.
The window may grow, but it never moves backwards, so expiring from the front and pushing at the back still
works. The deque would be wrong for a window like `[i − a[i], i−1]` with arbitrary `a[i]`, where the left end
can jump backwards and bring back an index we already deleted.
</details>

**Exercise 4.** In LC 1425 with `nums = [−1, −2, −3]`, `k = 1`, what does each `dp[i]` equal and what is the answer?

<details><summary>Answer</summary>

`dp[0] = −1`; `dp[1] = −2 + max(0, −1) = −2`; `dp[2] = −3 + max(0, −2) = −3`. Answer `max = −1` (choose only the
first element). The `max(0, …)` lets each element start fresh instead of carrying a negative prefix.
</details>
