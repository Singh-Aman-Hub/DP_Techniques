# Coin Change — Walkthrough

## 1. Intuition (no code, no math — an everyday analogy)

Imagine a country whose coins are worth **1, 3 and 4**. A shopkeeper must give you **6** in change and wants to use as few coins as possible.

The natural habit is: "use the biggest coin that fits, then repeat". Biggest that fits in 6 is 4; 2 is left; biggest that fits is 1; then 1 again. That is **4 + 1 + 1 = 3 coins**. But **3 + 3 = 2 coins** is better. The greedy habit grabbed the 4 because it looked good *right now*, and that choice made the rest expensive.

A careful shopkeeper instead asks, for every smaller amount, "what is the fewest coins for *this*?", starting from 0. Then for 6 they can check each possible last coin: if the last coin is a 1, the rest is 5 (best for 5 is 2 coins: 4 + 1) → 3 coins; if it is a 3, the rest is 3 (1 coin) → **2 coins**; if it is a 4, the rest is 2 (2 coins) → 3 coins. Best: 2. No guessing, no regret — that is the DP.

## 2. Problem statement in our own words

Given an array `coins` of distinct positive denominations and a non-negative integer `amount`, return the fewest coins whose values add up to exactly `amount`, using each denomination as many times as you like. If no combination works, return `−1`. For `amount = 0` the answer is `0`.

Original: [Coin Change — LeetCode 322](https://leetcode.com/problems/coin-change/) (`n ≤ 12`, coins up to 2^31 − 1, `amount ≤ 10^4`). Same task with different limits: [Minimizing Coins — CSES 1634](https://cses.fi/problemset/task/1634) (`n ≤ 100`, target ≤ 10^6).

## 3. Examples (input → output, with an explanation)

Running example: **`coins = [1, 3, 4]`, `amount = 6`.**

| Input | Output | Explanation |
|---|---|---|
| `[1, 3, 4]`, 6 | 2 | 3 + 3 (greedy would use 4 + 1 + 1) |
| `[1, 2, 5]`, 11 | 3 | 5 + 5 + 1 |
| `[5, 3]`, 9 | 3 | 3 + 3 + 3 (greedy takes 5 and then cannot pay 4) |
| `[2]`, 3 | −1 | only even totals are possible |
| `[1]`, 0 | 0 | nothing to pay |
| `[186, 419, 83, 408]`, 6249 | 20 | found by the DP; impossible to guess by hand |

## 4. Deriving the solution — the 7 questions

### Q1. What is changing?

As in [unbounded knapsack](../07-unbounded-knapsack/): the coin index `i` we are deciding about, and the **amount still to pay**, `a`.

### Q2. What does the state represent?

> `minCoins(i, a)` = the fewest coins, using only denominations `coins[i..n-1]` (unlimited), that add up to **exactly** `a`; `INF` if it cannot be done.

Answer: `minCoins(0, amount)`, reported as `−1` if it is `INF`.

### Q3. What are the choices?

1. **Done with coin `i`**: `minCoins(i + 1, a)`.
2. **Use one coin `i`** (if `coins[i] ≤ a`): that costs 1 coin and leaves `a − coins[i]`, still allowed to use coin `i` again: `minCoins(i, a − coins[i])`.

### Q4. What is the recurrence?

```
minCoins(i, a) = min( minCoins(i + 1, a),                   ← stop using coin i
                      1 + minCoins(i, a − coins[i]) )        ← one more coin i  (if coins[i] ≤ a)
```

Compared with unbounded knapsack: `max` → `min`, and the item's "value" is **1** (one coin), so `v[i] + …` becomes `1 + …`.

### Q5. What are the base cases?

- `a == 0` → `0`: nothing left to pay, zero more coins. (Check this **first**.)
- `i == n` and `a > 0` → `INF`: we still owe money but no denominations are left.

The second base case is the "**exactly**" condition — compare with knapsack's "at most", where running out of items simply gave 0. Here, ending with money still owed is a **failure**, and `INF` makes sure `min` never chooses a failed path when a real one exists.

### Q6. What is the order of computation?

Prefix table `dp[i][a]` = fewest coins using the first `i` denominations to pay exactly `a`:

```
dp[0][0] = 0,   dp[0][a > 0] = INF
dp[i][a] = min( dp[i-1][a],  1 + dp[i][a − coins[i-1]] )      (second term only if coins[i-1] ≤ a)
```

Rows top to bottom; within a row, `a` **left to right** (same-row dependency, as in unbounded knapsack).

### Q7. Can the state space be optimized?

One array `dp[a]`, forward loop per coin. Equivalently — and this is how many people first write it — 

```
dp[a] = 1 + min over all coins c ≤ a of dp[a − c]
```

"The last coin is one of the denominations; the rest is a smaller amount." For a `min` question both loop orders (coins outside or amounts outside) give the same answer.

### Choosing `INF` safely

`INF` must be bigger than any real answer **and** must survive `1 + INF` without overflowing.

- `Integer.MAX_VALUE` is tempting, but `1 + Integer.MAX_VALUE` wraps around to `−2 147 483 648`, which `min` would happily pick: garbage answers. If you use it, guard every addition: only compute `1 + rest` when `rest != INF` (that is what [`BruteForce.java`](solution/BruteForce.java) and [`Memoization.java`](solution/Memoization.java) do).
- **`amount + 1`** is the neat choice for tables. No real answer can exceed `amount` coins (every coin is at least 1), so `amount + 1` already means "impossible", and `1 + (amount + 1)` is nowhere near overflow. At the end, anything `≥ amount + 1` means `−1`. [`Tabulation.java`](solution/Tabulation.java) and [`Optimized.java`](solution/Optimized.java) use this.

## 5. Brute force: recursion

```java
private static final int INF = Integer.MAX_VALUE;

private static int minCoins(int[] coins, int i, int amount) {
    if (amount == 0) return 0;
    if (i == coins.length) return INF;
    int skip = minCoins(coins, i + 1, amount);
    int take = INF;
    if (coins[i] <= amount) {
        int rest = minCoins(coins, i, amount - coins[i]);
        if (rest != INF) take = 1 + rest;        // never compute 1 + INF
    }
    return Math.min(skip, take);
}
```

Full code: [`solution/BruteForce.java`](solution/BruteForce.java).

The top of the tree for `[1, 3, 4]`, amount 6, as `(i, a)`. Left = use one more coin `i` (stay), right = done with coin `i`.

```
                                 (0,6)
                 use 1 /                         \ done with 1
                    (0,5)                           (1,6)
              use 1 /    \ done          use 3 /           \ done with 3
                (0,4)    (1,5)              (1,3)              (2,6)
                 ...      ...         use 3 /    \ done    use 4 /    \ done
                                       (1,0)=0   (2,3)      (2,2)     (3,6)=INF
                                                 4>3:       4>2:
                                                (3,3)=INF  (3,2)=INF

(1,3) = min( (2,3)=INF , 1 + (1,0)=1 ) = 1        one 3-coin pays 3
(2,6) = min( (3,6)=INF , 1 + (2,2)=INF ) = INF    only 4-coins cannot pay 6
(1,6) = min( (2,6)=INF , 1 + (1,3)=2 )  = 2       3 + 3
(0,6) = min( (1,6)=2  , 1 + (0,5)=3 )   = 2
```

**Repeats:** `(1, 3)` is reached by "use 3 from `(1,6)`" and also by "use three 1-coins, then done with 1" (`(0,6) → (0,5) → (0,4) → (0,3) → (1,3)`). With larger amounts, the same `(i, a)` pairs are reached by huge numbers of paths.

## 6. Memoization (top-down)

```java
private static int[][] memo;     // memo[i][a]: fewest coins, INF = impossible, -1 = not computed

private static int minCoins(int[] coins, int i, int amount) {
    if (amount == 0) return 0;
    if (i == coins.length) return INF;
    if (memo[i][amount] != -1) return memo[i][amount];
    int skip = minCoins(coins, i + 1, amount);
    int take = INF;
    if (coins[i] <= amount) {
        int rest = minCoins(coins, i, amount - coins[i]);
        if (rest != INF) take = 1 + rest;
    }
    memo[i][amount] = Math.min(skip, take);
    return memo[i][amount];
}
```

Full code: [`solution/Memoization.java`](solution/Memoization.java).

Note the **three** kinds of values in the memo: `-1` (not computed), `INF` (computed: impossible) and real counts `≥ 0`. Mixing up "not computed" and "impossible" is a classic bug: if `INF` were also the "not computed" marker, impossible states would be recomputed every time.

**Dry run** (the order in which states are finished):

| State | skip | take | Stored |
|---|---|---|---|
| `(2,2)` | `(3,2)` = INF | 4 > 2 | INF |
| `(2,6)` | `(3,6)` = INF | `1 + (2,2)` → INF | INF |
| `(1,0)` | — | — | 0 (base case) |
| `(2,3)` | `(3,3)` = INF | 4 > 3 | INF |
| `(1,3)` | `(2,3)` = INF | `1 + (1,0)` = 1 | 1 |
| `(1,6)` | `(2,6)` = INF | `1 + (1,3)` = 2 | 2 |
| … | | | |
| `(0,3)` | `(1,3)` = 1 **(cache hit)** | `1 + (0,2)` = 3 | 1 |
| `(0,6)` | `(1,6)` = 2 | `1 + (0,5)` = 3 | **2** |

(The code actually explores the "use 1" spine first, but the stored values are the same whatever the order.)

## 7. Tabulation (bottom-up)

```java
int inf = amount + 1;
int[][] dp = new int[n + 1][amount + 1];
Arrays.fill(dp[0], inf);
dp[0][0] = 0;
for (int i = 1; i <= n; i++) {
    int coin = coins[i - 1];
    for (int a = 0; a <= amount; a++) {
        dp[i][a] = dp[i - 1][a];
        if (coin <= a) dp[i][a] = Math.min(dp[i][a], 1 + dp[i][a - coin]);
    }
}
return (dp[n][amount] >= inf) ? -1 : dp[n][amount];
```

Full code: [`solution/Tabulation.java`](solution/Tabulation.java).

### Filling the table for `[1, 3, 4]`, amount 6 (`INF` shown as `∞`, internally 7)

**Row 0** — no coins: only amount 0 is payable.

```
             a:   0   1   2   3   4   5   6
i=0  {}           0   ∞   ∞   ∞   ∞   ∞   ∞
```

**Row 1** — coin 1: `dp[1][a] = min(∞, 1 + dp[1][a−1])`, so `a` coins of value 1.

```
i=1  {1}          0   1   2   3   4   5   6
```

**Row 2** — coin 3:

```
a=0..2: 3 > a, copy:                       0   1   2
a=3: min(dp[1][3]=3, 1 + dp[2][0]=0) = 1   one 3
a=4: min(4, 1 + dp[2][1]=1) = 2            3 + 1
a=5: min(5, 1 + dp[2][2]=2) = 3            3 + 1 + 1
a=6: min(6, 1 + dp[2][3]=1) = 2            3 + 3   (dp[2][3] is this row: one 3 already)
```

**Row 3** — coin 4:

```
a=0..3: copy:                              0   1   2   1
a=4: min(2, 1 + dp[3][0]=0) = 1            one 4
a=5: min(3, 1 + dp[3][1]=1) = 2            4 + 1
a=6: min(2, 1 + dp[3][2]=2) = 2            3 + 3 stays best (4 + 1 + 1 = 3)
```

**Final table:**

```
             a:   0   1   2   3   4   5   6
i=0  {}           0   ∞   ∞   ∞   ∞   ∞   ∞
i=1  {1}          0   1   2   3   4   5   6
i=2  {1,3}        0   1   2   1   2   3   2
i=3  {1,3,4}      0   1   2   1   1   2   2   ← dp[3][6] = 2
```

**Which coins?** Walk back as in unbounded knapsack: `(3,6)`: equals `dp[2][6]` → go up. `(2,6)`: 2 ≠ `dp[1][6]` = 6 → one coin 3, `a = 3`. `(2,3)`: 1 ≠ 3 → another coin 3, `a = 0`. Coins: 3 + 3.

## 8. Space optimization

```java
int inf = amount + 1;
int[] dp = new int[amount + 1];
Arrays.fill(dp, inf);
dp[0] = 0;
for (int coin : coins) {
    for (int a = coin; a <= amount; a++) {            // forward: unlimited copies
        dp[a] = Math.min(dp[a], 1 + dp[a - coin]);
    }
}
return (dp[amount] >= inf) ? -1 : dp[amount];
```

Full code: [`solution/Optimized.java`](solution/Optimized.java). This exact code also passes [CSES 1634](https://cses.fi/problemset/task/1634): 100 coins × 10^6 amounts = 10^8 simple steps.

Note `1 + dp[a − coin]` may be `1 + inf = amount + 2` for an unreachable amount. That is still `≥ inf`, so it still means "impossible", and it is far from overflow. No guard needed.

## 9. Dry run on a full example

One array, `[1, 3, 4]`, amount 6:

| After coin | `dp[0..6]` |
|---|---|
| start | `0 ∞ ∞ ∞ ∞ ∞ ∞` |
| 1 | `0 1 2 3 4 5 6` |
| 3 | `0 1 2 1 2 3 2` |
| 4 | `0 1 2 1 1 2 2` |

Answer `dp[6] = 2`. All four DP files agree on all tests (2, 3, 3, −1, 0, and 20 for the big LeetCode example).

### Why greedy fails

[`Greedy.java`](solution/Greedy.java) runs "largest coin first" next to the DP:

| Coins, amount | Greedy | DP | What happened |
|---|---|---|---|
| `[1, 3, 4]`, 6 | 4 + 1 + 1 = **3** | 3 + 3 = **2** | greedy's first choice (4) made the rest expensive |
| `[5, 3]`, 9 | 5, then stuck at 4 → **−1** | 3 + 3 + 3 = **3** | greedy does not even find a valid answer |
| `[1, 2, 5]`, 11 | 5 + 5 + 1 = 3 | 3 | right by luck: `{1, 2, 5}` is a canonical system |

Greedy commits to one choice per step and never reconsiders. The DP considers **every** possible last coin for **every** amount and keeps the best, so a locally attractive but globally bad coin can never trap it. Greedy is correct only for special ("canonical") coin systems such as real-world currencies, and proving that a system is canonical is harder than just running the DP.

## 10. Complexity analysis (explained, for every approach)

| Approach | Time | Space | Why |
|---|---|---|---|
| Brute force | exponential | `O(n + amount)` | each coin can repeat up to `amount / coin` times on a path |
| Memoization | `O(n · amount)` | `O(n · amount)` | `n × (amount+1)` states, two choices each; recursion up to `n + amount / min coin` deep |
| Tabulation | `O(n · amount)` | `O(n · amount)` | each cell once |
| One array | `O(n · amount)` | `O(amount)` | each coin sweeps the amounts once |
| Greedy (wrong in general) | `O(n log n)` | `O(n)` | sort once, one pass |

For LeetCode 322: `12 × 10^4 = 1.2·10^5` steps. For CSES 1634: `10^8` steps — fine for the 1-D array, and the 2-D table would need 400 MB, so use 1-D there.

## 11. Why this approach works (correctness argument in plain words)

Take an optimal set of coins for `(i, a)`. If it uses no coin `i`, it is an optimal answer for `(i+1, a)`. If it uses at least one, removing one coin `i` leaves a valid answer for `(i, a − coins[i])` with one coin fewer, and that remainder must itself be optimal (a better remainder would give a better total). So the optimum is one of the two terms in the `min`. If neither term is possible, both are `INF` and the state is correctly `INF`. Base cases: paying 0 needs 0 coins, and with no denominations a positive amount is impossible.

## 12. Common mistakes

1. **`1 + Integer.MAX_VALUE`** overflowing to a negative number and winning every `min`. Use `amount + 1` or guard the addition.
2. **Initializing `dp` with 0 instead of `INF`**: every amount then looks payable with 0 coins.
3. **Forgetting `dp[0] = 0`**: nothing is ever reachable and the answer is always −1.
4. **Using the 0/1 (backward) loop**: each coin may then be used at most once — a different problem. `[1, 3, 4]`, 6 would return −1 (no set of *distinct* coins sums to 6), and so would `[5, 3]`, 9.
5. **Returning `INF` instead of −1** at the end, or testing `== INF` when the stored value may be `INF + 1`: test `>= inf`.
6. **Trusting greedy** because it passes the examples you tried.
7. **Huge coins**: LeetCode allows coins up to 2^31 − 1; they simply never fit (`coin > a`), but writing `a - coin` without the `coin <= a` check, or `a + coin` in a "push forward" style, can overflow.

## 13. When to use this technique / how to recognize similar problems

- "**Minimum number** of items (coins, squares, steps, stamps) to reach **exactly** a total, items reusable" → this topic.
- If there is a cost per item instead of "1 per item", replace `1 +` with `cost[i] +`.
- If items are **not** reusable, use the 0/1 version (backward loop) with `min`.
- If the question is **how many ways**, switch to counting → [Coin Change II](../09-coin-change-ii/).
- If you need the coins themselves, keep the table (or a `lastCoin[a]` array) and walk back.

## 14. Summary and revision notes

- Coin change = unbounded knapsack with `min`, "value" 1 per coin, **exact** amount.
- Base: `dp[0] = 0`, every other amount starts as `INF`; impossible states stay `INF` → answer −1.
- Safe `INF = amount + 1`; or guard `1 + rest` when using `Integer.MAX_VALUE`.
- 1-D forward loop, `O(n · amount)` time, `O(amount)` space.
- Greedy fails: `[1,3,4]`, 6 (3 vs 2) and `[5,3]`, 9 (−1 vs 3).

```
dp[a] = fewest coins that pay exactly a          (INF = amount + 1 means impossible)

dp[0] = 0,  dp[a > 0] = INF
for each coin c:  for a = c up to amount:  dp[a] = min(dp[a], 1 + dp[a - c])
answer = dp[amount] >= INF ? -1 : dp[amount]
```

## Exercises

1. Fill the 1-D array for `coins = [2, 5]`, `amount = 8`. What is the answer?

<details>
<summary>Answer</summary>

`INF = 9`. Start `[0 ∞ ∞ ∞ ∞ ∞ ∞ ∞ ∞]`. After 2: `[0 ∞ 1 ∞ 2 ∞ 3 ∞ 4]`. After 5: `a=5: min(∞, 1+dp[0]) = 1`, `a=6: min(3, 1+dp[1]=∞) = 3`, `a=7: min(∞, 1+dp[2]=1) = 2`, `a=8: min(4, 1+dp[3]=∞) = 4`. Answer **4** (2 + 2 + 2 + 2). Amounts 1 and 3 stay impossible.
</details>

2. Find coins and an amount where greedy uses 2 more coins than optimal.

<details>
<summary>Answer</summary>

Coins `[1, 4, 5]`, amount 8: greedy takes 5 + 1 + 1 + 1 = 4 coins, the optimum is 4 + 4 = 2 coins. (With coins `[1, 5, 6]` and amount 10 the gap is even 3: greedy 6 + 1 + 1 + 1 + 1, optimum 5 + 5.)
</details>

3. In `Memoization.java`, why is the "not computed" marker `-1` and not `INF`?

<details>
<summary>Answer</summary>

`INF` is a legitimate *computed* answer ("impossible"). If it also meant "not computed", every impossible state would be recomputed on every visit, and with many impossible states (e.g. `coins = [2]`, odd amounts) the memoization would lose its benefit. `-1` is never a real answer, so it is a safe third value.
</details>

4. How would you change the 1-D code to also return **which** coins to use?

<details>
<summary>Answer</summary>

Keep an array `lastCoin[a]`. Whenever `1 + dp[a − c]` improves `dp[a]`, set `lastCoin[a] = c`. At the end, if `dp[amount]` is finite, repeat `print lastCoin[a]; a -= lastCoin[a]` until `a = 0`. For `[1, 3, 4]`, 6: `lastCoin[6] = 3`, `lastCoin[3] = 3` → 3 + 3.
</details>
