# 486. Predict the Winner

> **Pattern:** Interval DP, minimax written as a score-difference DP · **Difficulty:** Medium · **Builds on:** [Interval DP](../../../04-advanced-dp-patterns/03-interval-dp/), [877. Stone Game](../877-stone-game/)

## Problem overview

Two players take turns removing a number from **either end** of an array and adding it to their own score. Player 1 moves first, and both play optimally. Return `true` if Player 1 ends with a score **greater than or equal to** Player 2's (a tie counts as a win for Player 1).

Unlike [877](../877-stone-game/), the array can have odd length and ties are possible. The parity shortcut does not apply, so the DP is the real solution.

## Link & metadata

| Field | Value |
|---|---|
| Problem | [486. Predict the Winner](https://leetcode.com/problems/predict-the-winner/) |
| Difficulty | Medium |
| Pattern | Minimax → score difference on a range `[i..j]` |
| Prerequisite topic | [04-advanced-dp-patterns/03-interval-dp](../../../04-advanced-dp-patterns/03-interval-dp/) |

## Examples

| nums | P1 − P2 (optimal) | Answer |
|---|---|---|
| `[1,5,2]` | −2 | false: whichever end P1 takes, P2 takes 5 |
| `[1,5,233,7]` | 222 | true: P1 takes 1, then whatever P2 takes, P1 gets 233 |
| `[1,1]` | 0 | true (a tie is a win) |
| `[7]` | 7 | true |

Running example: `nums = [1,5,2]`.

## Brute-force intuition

### Classic minimax

The textbook way to solve a two-player game is **minimax**: one player tries to **maximize** a score, the other tries to **minimize** it, and the turn alternates. Using "P1 − P2" as the score:

```
best(i, j, p1Turn):
    if p1Turn:  max( +nums[i] + best(i+1, j, false),  +nums[j] + best(i, j-1, false) )
    else:       min( -nums[i] + best(i+1, j, true),   -nums[j] + best(i, j-1, true) )
```

This is correct (see [`solution/BruteForce.java`](solution/BruteForce.java)), but:
- it has **two** almost identical branches (max and min), and
- the turn flag doubles the state.

### From minimax to one recurrence
Look at the game from **the player to move**, whoever that is. Let `diff(i, j)` = (mover's total) − (other player's total) from here on. A min-step for P2 is the same as a max-step for P2 measured from P2's own side. Flipping the sign turns one into the other:
```
best(i, j, P1 to move) = diff(i, j)
best(i, j, P2 to move) = -diff(i, j)
```
So the turn flag disappears, and both players use the **same** `max`. This "negamax" idea is the key lesson of the problem.

## Deriving the DP (7 questions)

### 1. What is changing?
The remaining range `[i..j]`. Moves only remove from the ends, so what remains is always contiguous.

### 2. What does the state represent?
> `diff[i][j]` = the largest lead (own points − opponent's points) that the player to move can guarantee on `nums[i..j]`.

### 3. What are the choices?
Take `nums[i]` or take `nums[j]`.

### 4. What is the recurrence?
```
diff[i][j] = max( nums[i] - diff[i+1][j],  nums[j] - diff[i][j-1] )
```
After I take a number, my opponent is "the mover" on what remains, and their lead counts against me.

**Answer:** `diff[0][n-1] >= 0`.

### 5. What are the base cases?
`diff[i][i] = nums[i]`: the last number goes to the mover.

### 6. What is the order of computation?
Ranges of length `L` need ranges of length `L-1`. Loop `i` from `n-1` down to 0 and `j` from `i+1` up.

### 7. Can the state space be optimized?
Yes. As in 877, one array, where `dp[j]` is `diff[i+1][j]` before the overwrite and `dp[j-1]` is `diff[i][j-1]`. O(n) space.

## Recursive solution

```java
static int diff(int[] nums, int i, int j) {
    if (i == j) return nums[i];
    return Math.max(nums[i] - diff(nums, i + 1, j), nums[j] - diff(nums, i, j - 1));
}
```

## Memoization

Cache in `Integer[n][n]` (null = unknown, since a difference may be 0 or negative). See [`solution/Memoization.java`](solution/Memoization.java).

## Tabulation

```java
for (int i = n - 1; i >= 0; i--) {
    diff[i][i] = nums[i];
    for (int j = i + 1; j < n; j++)
        diff[i][j] = Math.max(nums[i] - diff[i + 1][j], nums[j] - diff[i][j - 1]);
}
return diff[0][n - 1] >= 0;
```
See [`solution/Tabulation.java`](solution/Tabulation.java).

## Space optimization

```java
int[] dp = nums.clone();                     // diagonal: diff[j][j] = nums[j]
for (int i = n - 2; i >= 0; i--)
    for (int j = i + 1; j < n; j++)
        dp[j] = Math.max(nums[i] - dp[j], nums[j] - dp[j - 1]);
return dp[n - 1] >= 0;
```
See [`solution/Optimized.java`](solution/Optimized.java).

## Dry run

`nums = [1,5,2]`:

| range | take left | take right | diff |
|---|---|---|---|
| [1], [5], [2] | | | 1, 5, 2 |
| [1,5] | 1 − 5 = −4 | 5 − 1 = 4 | 4 |
| [5,2] | 5 − 2 = 3 | 2 − 5 = −3 | 3 |
| [1,5,2] | 1 − diff[5,2]=3 → −2 | 2 − diff[1,5]=4 → −2 | **−2** |

```
         j:  0    1    2
 i=0         1    4  [-2]
 i=1              5    3
 i=2                   2
```
−2 < 0, so the answer is `false`.

The same game in minimax form, with P1 maximizing and P2 minimizing P1 − P2:
```
P1 on [1,5,2]: max( 1 + P2 on [5,2],  2 + P2 on [1,5] )
P2 on [5,2]:   min( -5 + 2, -2 + 5 ) = -3      -> 1 + (-3) = -2
P2 on [1,5]:   min( -1 + 5, -5 + 1 ) = -4      -> 2 + (-4) = -2
result: max(-2, -2) = -2                       (matches diff[0][2])
```

## Complexity

| Approach | Time | Space | Why |
|---|---|---|---|
| Minimax brute force | O(2ⁿ) | O(n) | two moves per turn, depth n |
| Memoization | O(n²) | O(n²) | n²/2 ranges, O(1) each |
| Tabulation | O(n²) | O(n²) | upper triangle |
| Optimized | O(n²) | O(n) | single row |

With n ≤ 20, even 2²⁰ ≈ 10⁶ brute force passes, but the DP is the method that scales.

## Why the optimized approach is better

The brute force replays the same sub-range for every move order that reaches it. The DP solves each range once. Negamax also halves the code and removes the turn dimension from the state.

## Common mistakes

1. **Using `> 0`.** A tie counts as a win, so use `>= 0`.
2. **Assuming the 877 shortcut** ("always true"). `[1,5,2]` returns false.
3. **Greedy play** ("take the larger end"). On `[1,5,233,7]` greedy P1 takes 7 and then P2 takes 233.
4. **Memo sentinel 0.** Differences can be 0 or negative, so use `Integer` / null.

## Key takeaway

> Minimax over "P1 − P2" with a turn flag ⇔ one recurrence `diff = max(take − diff(rest))` seen from the mover's side. That is interval DP whenever moves remove items from the ends.

## Related problems

- [877. Stone Game](../877-stone-game/): the same recurrence, with guarantees that make it always `true`.
- [375. Guess Number Higher or Lower II](../375-guess-number-higher-or-lower-ii/): minimax where you minimize and an adversary maximizes over a range.
- Course topic: [Interval DP](../../../04-advanced-dp-patterns/03-interval-dp/)
