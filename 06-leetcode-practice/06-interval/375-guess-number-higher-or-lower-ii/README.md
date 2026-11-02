# 375. Guess Number Higher or Lower II

> **Pattern:** Interval DP with minimax (minimize over your guess, maximize over the adversary's answer) · **Difficulty:** Medium · **Builds on:** [Interval DP](../../../04-advanced-dp-patterns/03-interval-dp/), [486. Predict the Winner](../486-predict-the-winner/)

## Problem overview

Someone picks a secret number from `1` to `n`. You guess. Each **wrong** guess `x` costs you `x` dollars, and you are told whether the secret is higher or lower. Return the smallest amount of money that **guarantees** a win, whatever the secret is.

"Guarantees" is the key word. We are not minimizing the average cost; we are minimizing the **worst case**. Picture an adversary who chooses the secret, or changes it, to hurt you as much as possible.

## Link & metadata

| Field | Value |
|---|---|
| Problem | [375. Guess Number Higher or Lower II](https://leetcode.com/problems/guess-number-higher-or-lower-ii/) |
| Difficulty | Medium |
| Pattern | Interval DP: `min over split x of (x + max(left, right))` |
| Prerequisite topic | [04-advanced-dp-patterns/03-interval-dp](../../../04-advanced-dp-patterns/03-interval-dp/) |
| Also useful | [04-advanced-dp-patterns/04-matrix-chain-multiplication](../../../04-advanced-dp-patterns/04-matrix-chain-multiplication/) |

## Examples

| n | Answer | Why |
|---|---|---|
| 1 | 0 | the only number, right on the first try |
| 2 | 1 | guess 1: correct, or pay 1 and the answer is 2 |
| 3 | 2 | guess 2: correct, or pay 2 and you know the answer |
| 4 | 4 | e.g. guess 1 first, then range [2..4] costs 3 more (guess 3) |
| 10 | 16 | |

Running example: `n = 4`.

## Brute-force intuition

Binary search is **not** optimal here, because guesses have different prices: a wrong guess of 9 costs much more than a wrong guess of 1. So try every first guess `x`:
- If `x` is right, you pay nothing more.
- If not, you pay `x`, and the adversary sends you into the **more expensive** side, `[lo..x−1]` or `[x+1..hi]`.

That side is again a range of candidates, a smaller copy of the same problem. A range shrinking around a chosen split point is interval DP. Different guess orders reach the same range, so we cache.

## Deriving the DP (7 questions)

### 1. What is changing?
The range of numbers that could still be the secret: `[lo..hi]`.

### 2. What does the state represent?
> `dp[lo][hi]` = the minimum money that guarantees finding the secret when it is known to lie in `[lo..hi]`.

### 3. What are the choices?
Your guess `x`, any number in `[lo..hi]`. The adversary then "chooses" which side the secret is on.

### 4. What is the recurrence?
```
dp[lo][hi] = min over x in [lo..hi] of  ( x + max( dp[lo][x-1], dp[x+1][hi] ) )
```
- `min` over `x`: **you** choose the guess that is best for you.
- `max` of the two sides: the **adversary** picks the worse side for you.
- `+ x`: in the worst case this guess is wrong, so you pay for it.

This is the same **minimax** idea as in [486](../486-predict-the-winner/). There, both players picked moves. Here you pick a guess and the "opponent" picks a branch.

### 5. What are the base cases?
- `lo >= hi`: zero or one candidate, so you know the answer and pay 0.

### 6. What is the order of computation?
`dp[lo][hi]` needs ranges strictly inside it. Fill by increasing length, or loop `lo` from `n` down to 1 and `hi` from `lo+1` up to `n`. Use an `(n+2) × (n+2)` table so that `dp[x+1][hi]` with `x = hi` (an empty range) is a valid index holding 0.

### 7. Can the state space be optimized?
Not by rows: `dp[lo][hi]` reads the whole row `lo` and the whole column `hi`. All O(n²) states are needed, so there is no `Optimized.java`. With n ≤ 200 the O(n³) table is already fast.

## Recursive solution

```java
static int cost(int lo, int hi) {
    if (lo >= hi) return 0;
    int best = Integer.MAX_VALUE;
    for (int x = lo; x <= hi; x++)
        best = Math.min(best, x + Math.max(cost(lo, x - 1), cost(x + 1, hi)));
    return best;
}
```

## Memoization

Cache `cost(lo, hi)` in `memo[n+2][n+2]`. Ranges with `lo >= hi` return 0 before the lookup, and real ranges cost at least `lo ≥ 1`, so 0 can mean "unknown". See [`solution/Memoization.java`](solution/Memoization.java).

## Tabulation

```java
for (int lo = n; lo >= 1; lo--)
    for (int hi = lo + 1; hi <= n; hi++) {
        dp[lo][hi] = Integer.MAX_VALUE;
        for (int x = lo; x <= hi; x++)
            dp[lo][hi] = Math.min(dp[lo][hi], x + Math.max(dp[lo][x - 1], dp[x + 1][hi]));
    }
return dp[1][n];
```
`dp[lo][lo-1]` (when `x = lo`) is 0 because nothing writes cells below the diagonal. See [`solution/Tabulation.java`](solution/Tabulation.java).

## Space optimization

Not applicable (see question 7).

## Dry run

`n = 4`:

| range | guess x → x + max(left, right) | dp |
|---|---|---|
| [1,2] | 1 + max(0, [2]=0) = 1; 2 + max([1]=0, 0) = 2 | 1 |
| [2,3] | 2 + 0 = 2; 3 + 0 = 3 | 2 |
| [3,4] | 3; 4 | 3 |
| [1,3] | 1 + [2,3]=2 → 3; **2 + max(0,0) → 2**; 3 + [1,2]=1 → 4 | 2 |
| [2,4] | 2 + [3,4]=3 → 5; **3 + max(0,0) → 3**; 4 + [2,3]=2 → 6 | 3 |
| [1,4] | **1 + [2,4]=3 → 4**; 2 + max(0, [3,4]=3) → 5; **3 + max([1,2]=1, 0) → 4**; 4 + [1,3]=2 → 6 | **4** |

```
         hi:  1    2    3    4
 lo=1         0    1    2   [4]
 lo=2              0    2    3
 lo=3                   0    3
 lo=4                        0
```
The best first guess is 1 or 3, and either one guarantees a total of at most 4.

## Complexity

| Approach | Time | Space | Why |
|---|---|---|---|
| Recursion | exponential | O(n) | every guess sequence |
| Memoization | O(n³) | O(n²) | n²/2 ranges × up to n guesses |
| Tabulation | O(n³) | O(n²) | three nested loops |

With n ≤ 200, n³ = 8 × 10⁶, which is fast.

## Why the optimized approach is better

The same range `[lo..hi]` is reached by many different guess histories. The DP prices it once. The minimax structure (min outside, max inside) is captured exactly, so the answer is a true worst-case guarantee.

## Common mistakes

1. **Using binary search**, or always guessing the middle. The costs are not uniform.
2. **Summing both sides** instead of taking the `max`. Only one side is ever explored.
3. **Taking `min` of the sides.** That assumes a friendly secret, not a guarantee.
4. **Index out of bounds** at `x = hi` (`dp[hi+1][hi]`). Size the table `n + 2`.

## Key takeaway

> "Guarantee / worst case" ⇒ `max` over what the adversary controls, `min` over what you control. Inside a range with a split point, that becomes `dp[lo][hi] = min_x (x + max(dp[lo][x-1], dp[x+1][hi]))`.

## Related problems

- [486. Predict the Winner](../486-predict-the-winner/): minimax between two players.
- [1039. Minimum Score Triangulation of Polygon](../1039-minimum-score-triangulation-of-polygon/): the same split-point shape with `+` instead of `max`.
- [1547. Minimum Cost to Cut a Stick](../1547-minimum-cost-to-cut-a-stick/): another split-point interval DP.
- Course topic: [Interval DP](../../../04-advanced-dp-patterns/03-interval-dp/)
