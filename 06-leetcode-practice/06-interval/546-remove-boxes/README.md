# 546. Remove Boxes

> **Pattern:** Interval DP with an extra "carried context" dimension, `dp[l][r][k]` · **Difficulty:** Hard · **Builds on:** [Interval DP](../../../04-advanced-dp-patterns/03-interval-dp/), [Burst Balloons](../../../04-advanced-dp-patterns/05-burst-balloons/)

## Problem overview

A row of boxes, each with a colour (a positive integer). In one move you remove a **contiguous group of same-coloured boxes**, say `t` of them, and score `t × t` points. The boxes on both sides then close the gap and become neighbours. Keep going until no boxes are left, and return the highest possible total score.

Because the score is **squared**, it pays to remove other boxes first so that equal colours come together and can be removed as one big group.

## Link & metadata

| Field | Value |
|---|---|
| Problem | [546. Remove Boxes](https://leetcode.com/problems/remove-boxes/) |
| Difficulty | Hard |
| Pattern | Interval DP + a third index `k` describing what is attached from outside the range |
| Prerequisite topic | [04-advanced-dp-patterns/03-interval-dp](../../../04-advanced-dp-patterns/03-interval-dp/) |
| Also useful | [04-advanced-dp-patterns/05-burst-balloons](../../../04-advanced-dp-patterns/05-burst-balloons/), [664. Strange Printer](../664-strange-printer/) |

## Examples

| boxes | Answer | Why |
|---|---|---|
| `[1,2,1]` | 5 | remove `2` (1), then `1,1` together (4) |
| `[1,1,1]` | 9 | one group of 3 |
| `[1,3,2,2,2,3,4,3,1]` | 23 | see the sequence below |
| `[1,2,1,2,1]` | 11 | remove both `2`s singly (1+1), then `1,1,1` (9) |
| `[1]` | 1 | one box |

For the third example, one optimal sequence is remove `2,2,2` (9) → `[1,3,3,4,3,1]`, remove `4` (1) → `[1,3,3,3,1]`, remove `3,3,3` (9) → `[1,1]`, remove `1,1` (4). Total 9 + 1 + 9 + 4 = 23.

Running example: `[1,2,1]`.

## Brute-force intuition

Try every possible group to remove first, then recurse on what is left. The leftover rows are arbitrary subsets of the original boxes, so there are exponentially many states and memoizing them by content is hopeless.

## Why two indices are not enough

The first instinct is `f(l, r)` = best score for `boxes[l..r]`. Try it on `[1,2,1]`.

Removing the `2` first makes the two `1`s neighbours, worth `2² = 4`. Now look at the sub-range `[2..2]`, the last `1`. **On its own** it is worth `1² = 1`. **In context** it is removed together with the first `1`, and the pair is worth 4. The value of a sub-range depends on **how many boxes of the same colour are waiting outside it to join**, and `f(l, r)` has no way to know that.

Any split of `[1,2,1]` into independent ranges scores at most `1 + (1 + 1) = 3`, but the true answer is 5. A two-index interval DP **cannot represent** "the first `1` is waiting to be merged with the last one". So the state needs one more number.

## Deriving the DP (7 questions)

### 1. What is changing?
The range `[l..r]` still present, **and** how many boxes of colour `boxes[l]` are already glued to the left of `l`, waiting to be removed together with `boxes[l]`. Call that number `k`.

### 2. What does the state represent?
> `dp[l][r][k]` = the maximum score for removing `boxes[l..r]`, given that `k` extra boxes with the same colour as `boxes[l]` are attached directly to the left of `l` (they must be removed in the same group as `boxes[l]`).

The answer is `dp[0][n-1][0]`.

### 3. What are the choices?
What happens to `boxes[l]` together with its `k` glued companions:
- **Remove them now** as one group of `k + 1` boxes.
- **Wait and merge** with a later box `boxes[m]` of the same colour (`l < m ≤ r`). First clear everything between them, `boxes[l+1..m-1]`. Then `boxes[l]` and its `k` companions join `boxes[m]`, so `m` now has `k + 1` glued boxes.

### 4. What is the recurrence?
```
dp[l][r][k] = max( (k+1)² + dp[l+1][r][0],                                       remove now
                   max over m in (l, r] with boxes[m] == boxes[l] of
                       dp[l+1][m-1][0] + dp[m][r][k+1] )                          merge with m
```
- `(k+1)²`: the group made of `boxes[l]` and its `k` companions.
- `dp[l+1][m-1][0]`: the middle part is cleared on its own. Nothing outside is glued to it, so `k = 0`.
- `dp[m][r][k+1]`: `boxes[m]` now carries `k + 1` glued boxes.

### 5. What are the base cases?
- An empty range (`l > r`) scores 0, whatever `k` is.
- A single box with `k` companions scores `(k+1)²`. The recurrence gives this automatically.

### 6. What is the order of computation?
`dp[l][r][k]` reads states with a **larger `l`** (`l+1` or `m`). Loop `l` from `n-1` **down** to 0, `r` from `l` up to `n-1`, and `k` from 0 to `l` (at most `l` boxes can sit to the left of `l`). This top-down structure is complicated, which is why **memoization is the natural way to write this problem**: the recursion only visits states that can actually occur.

### 7. Can the state space be optimized?
The cube of states is needed, because `dp[m][r][k+1]` jumps to arbitrary `m` and `k`. No rolling dimension works, so there is no `Optimized.java`. A practical speed-up for memoization: before branching, **absorb the run of equal boxes** right after `l` into `k` (if `boxes[l+1] == boxes[l]`, move to `(l+1, r, k+1)`). This cuts the search but not the worst case.

## Recursive solution

```java
static int best(int[] boxes, int l, int r, int k) {
    if (l > r) return 0;
    int result = (k + 1) * (k + 1) + best(boxes, l + 1, r, 0);          // remove now
    for (int m = l + 1; m <= r; m++)
        if (boxes[m] == boxes[l])                                       // wait and merge with m
            result = Math.max(result, best(boxes, l + 1, m - 1, 0) + best(boxes, m, r, k + 1));
    return result;
}
```

## Memoization

Cache in `int[n][n][n]` (0 = not computed, because every non-empty state scores at least 1). See [`solution/Memoization.java`](solution/Memoization.java).

## Tabulation

```java
for (int l = n - 1; l >= 0; l--)
    for (int r = l; r < n; r++)
        for (int k = 0; k <= l; k++) {
            int result = (k + 1) * (k + 1) + dp[l + 1][r][0];
            for (int m = l + 1; m <= r; m++)
                if (boxes[m] == boxes[l])
                    result = Math.max(result, dp[l + 1][m - 1][0] + dp[m][r][k + 1]);
            dp[l][r][k] = result;
        }
```
The table is `(n+1) × (n+1) × (n+1)`, so `dp[l+1][...]` with `l+1 = n` and empty ranges such as `dp[l+1][l]` read as 0. See [`solution/Tabulation.java`](solution/Tabulation.java).

## Space optimization

Not applicable (see question 7). The O(n³) table (10⁶ ints for n = 100, about 4 MB) is fine.

## Dry run

`boxes = [1,2,1]`, written as `f(l, r, k)`:

| state | meaning | remove now | merge options | value |
|---|---|---|---|---|
| f(2,2,0) | last `1`, alone | 1² + 0 | — | 1 |
| f(2,2,1) | last `1` + 1 glued `1` | 2² + 0 | — | 4 |
| f(1,1,0) | the `2` | 1 | — | 1 |
| f(1,2,0) | `2,1` | 1² + f(2,2,0) = 2 | no other `2` | 2 |
| f(0,2,0) | `1,2,1` | 1² + f(1,2,0) = 3 | m=2: f(1,1,0) + f(2,2,1) = 1 + 4 = 5 | **5** |

The winning branch says: clear the middle `[2]` alone (1 point), then the first `1` glues onto the last `1` (`k` becomes 1), and the pair scores 4. This is exactly the information a two-index state was missing.

## Complexity

| Approach | Time | Space | Why |
|---|---|---|---|
| Brute force | exponential | O(n) | every removal order |
| Memoization | O(n⁴) | O(n³) | n³ states (l, r, k) × up to n merge partners m |
| Tabulation | O(n⁴) | O(n³) | same, every state computed |

For n = 100, n⁴ = 10⁸ simple steps in the worst case. Memoization usually visits far fewer states.

## Why the optimized approach is better

Brute force remembers the whole leftover row. The DP shows that only three numbers matter: the range, plus how many same-coloured boxes are waiting on its left. That collapses an exponential state space to n³.

## Common mistakes

1. **Using `dp[l][r]` only.** It fails on `[1,2,1]` (it gives 3, but the answer is 5).
2. **Passing `k` into the middle part.** The middle `boxes[l+1..m-1]` is cleared **before** the merge, and nothing is glued to it, so its `k` is 0.
3. **Forgetting to carry `k + 1` into `dp[m][r][...]`.** It is `k + 1` (the old companions plus `boxes[l]`), not 1.
4. **Allocating `k` too small.** `k` can reach `n − 1`.

## Key takeaway

> If the value of a range depends on something **outside** it (here, waiting boxes of the same colour), add that outside information to the state. Ask: "what must my sub-problem know about the world around it?". The answer is the missing dimension.

## Related problems

- [664. Strange Printer](../664-strange-printer/): the same "merge `s[l]` with a later equal element `m`" choice, without the squared bonus, so two indices suffice.
- [1547. Minimum Cost to Cut a Stick](../1547-minimum-cost-to-cut-a-stick/): interval DP with a choice inside the range.
- [1130. Minimum Cost Tree From Leaf Values](../1130-minimum-cost-tree-from-leaf-values/): a classic two-index split DP for contrast.
- Course topic: [Burst Balloons](../../../04-advanced-dp-patterns/05-burst-balloons/)
