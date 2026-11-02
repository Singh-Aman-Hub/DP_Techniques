# 877. Stone Game

> **Pattern:** Interval DP, a two-player game on the ends of an array · **Difficulty:** Medium · **Builds on:** [Interval DP](../../../04-advanced-dp-patterns/03-interval-dp/)

## Problem overview

A row of piles of stones has an **even** number of piles and an **odd** total, so the game cannot end in a tie. Alice and Bob take turns, Alice first. On each turn a player takes the whole pile at the **left end or the right end** of the row. Both play optimally. Return `true` if Alice finishes with more stones.

There is a math shortcut (below) that makes the answer always `true`. We still build the DP, because it is the base model for every "take from either end" game. [486. Predict the Winner](../486-predict-the-winner/) removes the conditions that make the shortcut work.

## Link & metadata

| Field | Value |
|---|---|
| Problem | [877. Stone Game](https://leetcode.com/problems/stone-game/) |
| Difficulty | Medium |
| Pattern | Interval DP on `[i..j]`, storing the score difference |
| Prerequisite topic | [04-advanced-dp-patterns/03-interval-dp](../../../04-advanced-dp-patterns/03-interval-dp/) |

## Examples

| piles | Alice − Bob (optimal) | Answer |
|---|---|---|
| `[5,3,4,5]` | 1 (Alice 9, Bob 8) | true |
| `[3,7,2,3]` | 5 (Alice 10, Bob 5) | true |
| `[1,2]` | 1 | true |

Running example: `piles = [5,3,4,5]`.

## Brute-force intuition

At each turn the current player has two moves (left or right), so the game tree has 2ⁿ leaves. But after any sequence of moves, the remaining piles always form a **contiguous range** `piles[i..j]`. There are only about n²/2 such ranges, and the tree visits each one many times. Taking from the ends of a range is the signature of **interval DP**.

```
                 [5,3,4,5]
           take 5 /     \ take 5
          [3,4,5]       [5,3,4]
          /    \         /    \
      [4,5]  [3,4]   [3,4]  [5,3]      <- [3,4] is reached twice
```

## Deriving the DP (7 questions)

### 1. What is changing?
The remaining range `[i..j]`. Whose turn it is can be worked out from the range length, but we will not even need that (see question 2).

### 2. What does the state represent?
Tracking "Alice's score" and "Bob's score" separately would need a turn flag. A cleaner trick is to store the **difference from the point of view of whoever moves now**:
> `diff[i][j]` = (stones the player to move will collect) − (stones the opponent will collect), when the remaining piles are `piles[i..j]` and both play optimally.

The same definition works for both players, because after a move the roles swap.

### 3. What are the choices?
Take `piles[i]` (left) or take `piles[j]` (right).

### 4. What is the recurrence?
```
diff[i][j] = max( piles[i] - diff[i+1][j],      take left, then the opponent moves on [i+1..j]
                  piles[j] - diff[i][j-1] )     take right, then the opponent moves on [i..j-1]
```
Why the minus? After my move, the opponent becomes "the player to move" on the smaller range, and `diff` of that range is **their** lead over me. My lead is what I just took minus their lead.

Alice wins when `diff[0][n-1] > 0`.

### 5. What are the base cases?
`diff[i][i] = piles[i]`: one pile left, the mover takes it, and the opponent gets nothing.

### 6. What is the order of computation?
`diff[i][j]` needs ranges that are **one shorter** (`[i+1..j]`, `[i..j-1]`). Fill by increasing length, or equivalently `i` from `n-1` down to 0 and `j` from `i+1` up to `n-1`.

### 7. Can the state space be optimized?
Row `i` reads row `i+1` (the same column `j`) and its own column `j-1`. One array works: before `dp[j]` is overwritten it holds `diff[i+1][j]`, and `dp[j-1]` already holds `diff[i][j-1]`. O(n) space.

### The math shortcut (why the answer is always `true`)
Color the piles by position: even indices and odd indices. Because n is even, Alice can always take **all** even-indexed piles, or **all** odd-indexed piles. Whichever end she takes, Bob is left with two ends of the other color. One of the two color groups has a strictly larger sum (the total is odd), so Alice picks that group and wins. Hence `return true;`. This trick fails when n is odd or ties are possible, which is why the DP matters.

## Recursive solution

```java
static int diff(int[] piles, int i, int j) {
    if (i == j) return piles[i];
    return Math.max(piles[i] - diff(piles, i + 1, j),
                    piles[j] - diff(piles, i, j - 1));
}
```

## Memoization

Cache `diff(i, j)` in an `n × n` table (`Integer` null = unknown, since differences can be negative). See [`solution/Memoization.java`](solution/Memoization.java).

## Tabulation

```java
for (int i = 0; i < n; i++) diff[i][i] = piles[i];
for (int i = n - 2; i >= 0; i--)
    for (int j = i + 1; j < n; j++)
        diff[i][j] = Math.max(piles[i] - diff[i + 1][j], piles[j] - diff[i][j - 1]);
```
See [`solution/Tabulation.java`](solution/Tabulation.java).

## Space optimization

```java
int[] dp = piles.clone();                   // dp[j] = diff[j][j] for the bottom-most row
for (int i = n - 2; i >= 0; i--)
    for (int j = i + 1; j < n; j++)
        dp[j] = Math.max(piles[i] - dp[j],      // dp[j] still = diff[i+1][j]
                         piles[j] - dp[j - 1]); // dp[j-1] already = diff[i][j-1]
return dp[n - 1] > 0;
```
See [`solution/Optimized.java`](solution/Optimized.java).

## Dry run

`piles = [5,3,4,5]`, filled by length:

| range | take left | take right | diff |
|---|---|---|---|
| [5] [3] [4] [5] | | | 5, 3, 4, 5 |
| [5,3] | 5 − 3 = 2 | 3 − 5 = −2 | 2 |
| [3,4] | 3 − 4 = −1 | 4 − 3 = 1 | 1 |
| [4,5] | 4 − 5 = −1 | 5 − 4 = 1 | 1 |
| [5,3,4] | 5 − diff[3,4]=1 → 4 | 4 − diff[5,3]=2 → 2 | 4 |
| [3,4,5] | 3 − diff[4,5]=1 → 2 | 5 − diff[3,4]=1 → 4 | 4 |
| [5,3,4,5] | 5 − 4 = 1 | 5 − 4 = 1 | **1** |

```
         j:  0    1    2    3
 i=0         5    2    4   [1]
 i=1              3    1    4
 i=2                   4    1
 i=3                        5
```
Alice leads by 1 (9 vs 8), so the answer is `true`.

## Complexity

| Approach | Time | Space | Why |
|---|---|---|---|
| Game tree | O(2ⁿ) | O(n) | two moves per turn |
| Memoization | O(n²) | O(n²) | n²/2 ranges × O(1) |
| Tabulation | O(n²) | O(n²) | upper triangle |
| Optimized | O(n²) | O(n) | one row |
| Math | O(1) | O(1) | parity argument |

## Why the optimized approach is better

The DP evaluates each range once instead of once per path that reaches it. The 1D row keeps the same time with linear memory. The O(1) answer is better still, but it only works because of this problem's special guarantees.

## Common mistakes

1. **Storing only Alice's score** without a turn flag. Then you cannot write one recurrence for both players. The difference trick removes the problem.
2. **Adding instead of subtracting** the subproblem difference.
3. **Wrong loop order**: `i` must go downward (or loop by length).
4. **Using `int[][]` with `0` as "not computed"** in memoization. A difference can genuinely be 0 or negative.

## Key takeaway

> Two-player, optimal play, take from the ends ⇒ interval DP with `diff[i][j] = max(take - diff(rest))`. Storing "my lead over the opponent" makes both players use the same recurrence.

## Related problems

- [486. Predict the Winner](../486-predict-the-winner/): the same DP without the guarantees, where the shortcut no longer works.
- [1039. Minimum Score Triangulation of Polygon](../1039-minimum-score-triangulation-of-polygon/): interval DP with a split point.
- Course topic: [Interval DP](../../../04-advanced-dp-patterns/03-interval-dp/)
