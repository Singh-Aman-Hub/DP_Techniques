# Stock Buy and Sell — Walkthrough

We solve six problems with one idea. The plan:

1. Build the general state `(day, holding, transactionsLeft)` and its state diagram.
2. Solve LC 121 (one transaction) with it.
3. Change **one thing at a time** to get LC 122, 123, 188, 309 and 714.

All six use the same running example:

```
day:     0  1  2  3  4  5
prices: [1, 3, 2, 8, 4, 9]
```

| Problem | Rule | Answer on the example |
|---|---|---|
| LC 121 | at most 1 transaction | 8 |
| LC 122 | unlimited transactions | 13 |
| LC 123 | at most 2 transactions | 12 |
| LC 188 | at most k transactions (k = 3) | 13 |
| LC 309 | unlimited, 1-day cooldown after a sell | 8 |
| LC 714 | unlimited, fee = 2 per transaction | 8 |

---

## 1. Intuition

Imagine you are a trader who can hold **at most one share**. Every morning you look at today's price and you are in one of two moods:

- **Free** — your hands are empty. You may buy today, or do nothing.
- **Holding** — you own a share. You may sell today, or do nothing.

That is the whole game. You walk through the days, switching between two rooms:

```
                    buy  (pay price)
          +--------------------------------+
          |                                v
     +---------+                      +---------+
     |  FREE   |                      | HOLDING |
     +---------+                      +---------+
       ^    |  ^                        |    ^
       |rest|  |   sell (receive price) |    |rest
       +----+  +------------------------+    +----+
```

Each arrow is one **transition**. Each room is a **mode**. A plan for the whole period is just a path through this diagram, one step per day. DP finds the best path.

The variants of the problem only:
- limit how many times you may take the **sell** arrow (LC 121, 123, 188),
- add an extra room you must pass through after selling (LC 309 cooldown),
- put a price tag on the sell arrow (LC 714 fee).

---

## 2. Problem statement in our own words

You are given the price of one stock on each of `n` days. You may own at most one share at a time, so you must sell before buying again. Find the maximum total profit.

- [LC 121](https://leetcode.com/problems/best-time-to-buy-and-sell-stock/): at most one transaction.
- [LC 122](https://leetcode.com/problems/best-time-to-buy-and-sell-stock-ii/): as many transactions as you like.
- [LC 123](https://leetcode.com/problems/best-time-to-buy-and-sell-stock-iii/): at most two transactions.
- [LC 188](https://leetcode.com/problems/best-time-to-buy-and-sell-stock-iv/): at most `k` transactions (`k` is an input).
- [LC 309](https://leetcode.com/problems/best-time-to-buy-and-sell-stock-with-cooldown/): unlimited, but the day after a sell you may not buy.
- [LC 714](https://leetcode.com/problems/best-time-to-buy-and-sell-stock-with-transaction-fee/): unlimited, but each transaction costs a fixed fee.

If no trade makes money, the answer is 0 (do nothing).

---

## 3. Examples

On `[1, 3, 2, 8, 4, 9]`:

- **One transaction (121):** buy at 1 (day 0), sell at 9 (day 5) → **8**.
- **Unlimited (122):** take every rise: 1→3 (+2), 2→8 (+6), 4→9 (+5) → **13**.
- **Two transactions (123):** 1→8 (+7) and 4→9 (+5) → **12**. (Three rises cannot all be taken; merging 1→3 and 2→8 into 1→8 loses only 1.)
- **k = 3 (188):** same as unlimited here → **13**.
- **Cooldown (309):** selling 1→3 on day 1 forces a rest on day 2 (so we cannot buy at 2); the best follow-up is 4→9, total 2 + 5 = 7. Selling 1→8 on day 3 forces a rest on day 4 (so we cannot buy at 4) → 7. Simply holding 1→9 gives **8**, the best.
- **Fee 2 (714):** 1→8 (+7 − 2 = 5) and 4→9 (+5 − 2 = 3) → **8**. Taking 1→3 as well would earn 2 − 2 = 0, nothing.

Other inputs used in the tests:
- `[7, 6, 4, 3, 1]` (falling) → 0 for every variant.
- `[5]` (one day) → 0.

---

## 4. Deriving the solution — the 7 questions

We derive the **general** version (LC 188 with limit `k`) once. Every other problem is a special case (section 4.8).

### Q1. What is changing?
- the **day** we are deciding about (moves forward one at a time);
- whether we are **holding** a share (0 or 1);
- how many **transactions are left** (goes down by one at each sell).

### Q2. What does the state represent?

> `f(day, holding, left)` = the **maximum profit we can still earn from day `day` to the end**, given that we are currently holding (`holding = 1`) or not (`holding = 0`), and we may still complete `left` transactions.

"Still earn" means future money only: if we are holding, the purchase price was already paid in the past and is not counted here.

### Q3. What are the choices?
On day `day`:
- **Rest** (do nothing): stay in the same mode, go to the next day.
- If free: **buy** — pay `prices[day]`, become holding.
- If holding: **sell** — receive `prices[day]`, become free, and use up one transaction.

We count a transaction at the **sell**. (Counting at the buy also works; just be consistent.)

### Q4. What is the recurrence?

```
f(day, 0, left) = max( f(day+1, 0, left),                       // rest while free
                       -prices[day] + f(day+1, 1, left) )        // buy

f(day, 1, left) = max( f(day+1, 1, left),                       // rest while holding
                        prices[day] + f(day+1, 0, left-1) )      // sell: one transaction used
```

Answer: `f(0, 0, k)` — day 0, empty-handed, all `k` transactions available.

### Q5. What are the base cases?
- `f(n, *, *) = 0`: no days left, nothing more to earn. (If we are still holding, the share is simply worthless to us; the max will never choose this path because selling earlier is always at least as good as never selling.)
- `f(*, *, 0) = 0`: no transactions left, so we cannot sell; buying would only lose money.

### Q6. What is the order of computation?
`f(day, …)` reads only `f(day+1, …)`. So in a table, fill `day` from `n-1` **down to** 0. Within one day, `holding` and `left` can be filled in any order because they only read the next day.

### Q7. Can the state space be optimized?
Each day reads only the next day. We need one "layer" of size `2 × (k+1)` instead of `n` layers: O(k) space. When we run the machine **forward** (day 0 to n-1) instead, each state becomes "best cash balance after today", and LC 121/122/714 need just two variables (section 8).

### 4.8 One table: what each variant changes

| Problem | Change from the general state | State | Transition change |
|---|---|---|---|
| LC 188 | — (general case) | `(day, holding, left)` | — |
| LC 123 | `k = 2` | `(day, holding, left ≤ 2)` | — |
| LC 121 | `k = 1`; after the only sell nothing more happens | `(day, holding)` | sell: `prices[day]` (no future) |
| LC 122 | `k = ∞` → `left` never runs out, **drop it** | `(day, holding)` | sell: `prices[day] + f(day+1, 0)` |
| LC 309 | LC 122 + cooldown | `(day, holding)` | sell: `prices[day] + f(day+2, 0)` |
| LC 714 | LC 122 + fee | `(day, holding)` | sell: `prices[day] - fee + f(day+1, 0)` |

Read this table twice. It is the whole topic.

---

## 5. Brute force: recursion

Plain recursion on the state, without caching (shown for LC 122, the simplest unlimited version):

```java
int f(int day, int holding) {
    if (day == n) return 0;
    int rest = f(day + 1, holding);
    int act = (holding == 0) ? -prices[day] + f(day + 1, 1)
                             :  prices[day] + f(day + 1, 0);
    return Math.max(rest, act);
}
```

Recursion tree for the first three days of `[1, 3, 2, …]` (F = free, H = holding):

```
                         f(0,F)
                /                      \
         rest  /                        \ buy at 1
          f(1,F)                       f(1,H)
         /      \                     /      \
   rest /        \ buy at 3     rest /        \ sell at 3
   f(2,F)       f(2,H)        f(2,H)         f(2,F)
     ...          ...           ...            ...
```

`f(2,F)` appears twice (do nothing for two days, or buy-then-sell) and `f(2,H)` appears twice (buy on day 1, or buy on day 0 and keep holding). Every level doubles the number of calls: **2ⁿ calls** for only `2n` different states.

---

## 6. Memoization (top-down)

Store each `(day, holding, left)` result. With `Integer.MIN_VALUE` meaning "unknown":

```java
private static int best(int day, int holding, int left) {
    if (day == prices.length || left == 0) return 0;
    if (memo[day][holding][left] != Integer.MIN_VALUE) return memo[day][holding][left];
    int rest = best(day + 1, holding, left);
    int act = (holding == 0)
            ? -prices[day] + best(day + 1, 1, left)
            :  prices[day] + best(day + 1, 0, left - 1);
    return memo[day][holding][left] = Math.max(rest, act);
}
```

In the tree above, the second time `f(2,F)` or `f(2,H)` is requested, the answer comes from the memo and the whole subtree below it is skipped. Each of the `n × 2 × (k+1)` states is computed once.

Each variant's memoized code differs from this one in a single line — compare:

| File | The line that differs |
|---|---|
| [stock-i/Memoization.java](solution/stock-i/Memoization.java) | `act = prices[day];` (sell ends the game) |
| [stock-ii/Memoization.java](solution/stock-ii/Memoization.java) | `prices[day] + best(day + 1, 0)` (no `left`) |
| [stock-iii/Memoization.java](solution/stock-iii/Memoization.java) | `prices[day] + best(day + 1, 0, left - 1)` with `left ≤ 2` |
| [stock-cooldown/Memoization.java](solution/stock-cooldown/Memoization.java) | `prices[day] + best(day + 2, 0)` |
| [stock-fee/Memoization.java](solution/stock-fee/Memoization.java) | `prices[day] - fee + best(day + 1, 0)` |

---

## 7. Tabulation (bottom-up)

Turn `f` into a table `dp[day][holding][left]` and fill days from `n-1` down to `0`. Row `n` is all zeros (base case), and so is `left = 0`.

```java
int[][][] dp = new int[n + 1][2][k + 1];      // all zeros = base cases
for (int day = n - 1; day >= 0; day--) {
    for (int left = 1; left <= k; left++) {
        dp[day][0][left] = Math.max(dp[day+1][0][left], -prices[day] + dp[day+1][1][left]);
        dp[day][1][left] = Math.max(dp[day+1][1][left],  prices[day] + dp[day+1][0][left-1]);
    }
}
return dp[0][0][k];
```

### LC 122 table (no `left` dimension) on `[1, 3, 2, 8, 4, 9]`

Fill from the bottom (day 6 = past the end) upwards:

| day | price | free = max(rest, buy) | holding = max(rest, sell) |
|---|---|---|---|
| 6 | — | 0 (base) | 0 (base) |
| 5 | 9 | max(0, −9 + 0) = **0** | max(0, 9 + 0) = **9** |
| 4 | 4 | max(0, −4 + 9) = **5** | max(9, 4 + 0) = **9** |
| 3 | 8 | max(5, −8 + 9) = **5** | max(9, 8 + 5) = **13** |
| 2 | 2 | max(5, −2 + 13) = **11** | max(13, 2 + 5) = **13** |
| 1 | 3 | max(11, −3 + 13) = **11** | max(13, 3 + 11) = **14** |
| 0 | 1 | max(11, −1 + 14) = **13** | max(14, 1 + 11) = **14** |

Answer `dp[0][free] = 13`. Read `dp[3][holding] = 13` as "if I wake up on day 3 holding a share, I can still earn 13": sell at 8, then buy at 4 and sell at 9.

### LC 123 table (k = 2) on the same prices

Now each day has four numbers: free/holding × left = 1/2.

| day | price | free, left=1 | free, left=2 | holding, left=1 | holding, left=2 |
|---|---|---|---|---|---|
| 6 | — | 0 | 0 | 0 | 0 |
| 5 | 9 | 0 | 0 | 9 | 9 |
| 4 | 4 | 5 | 5 | 9 | 9 |
| 3 | 8 | 5 | 5 | 9 | max(9, 8 + free(4,left=1)=5) = **13** |
| 2 | 2 | 7 | max(5, −2 + 13) = **11** | 9 | 13 |
| 1 | 3 | 7 | 11 | 9 | 13 |
| 0 | 1 | max(7, −1 + 9) = **8** | max(11, −1 + 13) = **12** | 9 | 13 |

Answer `dp[0][free][2] = 12`. Notice that the `left = 1` column is exactly LC 121 (answer 8), which confirms that LC 121 is "k = 1".

Code: [stock-ii/Tabulation.java](solution/stock-ii/Tabulation.java), [stock-iii/Tabulation.java](solution/stock-iii/Tabulation.java), [stock-iv/Tabulation.java](solution/stock-iv/Tabulation.java).

---

## 8. Space optimization

### Running the state machine forward

Instead of "profit still to earn from today on", track "best cash balance at the end of today" for each mode, walking from day 0 to day `n-1`:

```
free_today = max(free_yesterday,            hold_yesterday + price)     // rest, or sell
hold_today = max(hold_yesterday,            free_yesterday - price)     // rest, or buy
```

Start with `free = 0` and `hold = −∞` (you cannot hold anything before day 0). The answer is the final `free`. Only two numbers are needed, because today reads only yesterday.

**Important:** compute both new values from the **old** ones. Use temporaries (`newFree`, `newHold`), or update in an order that never reads a value you already overwrote today.

### Forward traces on `[1, 3, 2, 8, 4, 9]`

LC 121 — one transaction, so a buy always starts from 0 cash: `hold = max(hold, −price)`.

| price | 1 | 3 | 2 | 8 | 4 | 9 |
|---|---|---|---|---|---|---|
| hold | −1 | −1 | −1 | −1 | −1 | −1 |
| free | 0 | 2 | 2 | 7 | 7 | **8** |

`hold` is just "minus the lowest price so far" — the classic min-price trick is this DP in disguise.

LC 122 — buys may use earlier profit: `hold = max(hold, free − price)`.

| price | 1 | 3 | 2 | 8 | 4 | 9 |
|---|---|---|---|---|---|---|
| hold | −1 | −1 | 0 | 0 | 4 | 4 |
| free | 0 | 2 | 2 | 8 | 8 | **13** |

LC 714 (fee 2) — the sell edge pays `price − 2`.

| price | 1 | 3 | 2 | 8 | 4 | 9 |
|---|---|---|---|---|---|---|
| hold | −1 | −1 | −1 | −1 | 1 | 1 |
| free | 0 | 0 | 0 | 5 | 5 | **8** |

On day 1 the sale `−1 + 3 − 2 = 0` does not beat `free = 0`, so the small rise is ignored.

### LC 309 (cooldown): a third room

```
                    buy
     +--------+ ----------> +---------+
     |  FREE  |             | HOLDING |
     +--------+             +---------+
       ^  |  ^                 |   ^
  rest |__|  |                 |   | rest
             |   (forced)      |sell
             |    rest    +---------+
             +----------- | COOLING |
                          +---------+
```

`COOLING` = "I sold yesterday". From it the only move is to `FREE` the next day.

```
hold_today    = max(hold, free - price)     // buy only from FREE, never from COOLING
cooling_today = hold + price                // sold today
free_today    = max(free, cooling)          // yesterday's cooldown is over
```

| price | 1 | 3 | 2 | 8 | 4 | 9 |
|---|---|---|---|---|---|---|
| hold | −1 | −1 | −1 | −1 | −1 | −1 |
| cooling | −∞ | 2 | 1 | 7 | 3 | **8** |
| free | 0 | 0 | 2 | 2 | 7 | 7 |

Answer `max(free, cooling) = 8`. Look at day 4: `free = 7` came from the sale at 8 on day 3, but we could not buy at 4 on day 4 (`hold` uses yesterday's `free = 2`, giving `2 − 4 = −2 < −1`).

### LC 123 / 188: one pair of variables per transaction

```
buy[t]  = max(buy[t],  sell[t-1] - price)    // t-th buy uses money from the (t-1)-th sell
sell[t] = max(sell[t], buy[t]    + price)
```

LC 123 (`t = 1, 2`) on our prices:

| price | 1 | 3 | 2 | 8 | 4 | 9 |
|---|---|---|---|---|---|---|
| buy1 | −1 | −1 | −1 | −1 | −1 | −1 |
| sell1 | 0 | 2 | 2 | 7 | 7 | 8 |
| buy2 | −1 | −1 | 0 | 0 | 3 | 3 |
| sell2 | 0 | 2 | 2 | 8 | 8 | **12** |

`buy2 = 3` on day 4 means "sold 1→8 for 7, then bought at 4". Then `sell2 = 3 + 9 = 12`.

LC 188 does the same with arrays `buy[1..k]`, `sell[1..k]`: O(k) space. One more trick: a transaction needs two different days, so if `k ≥ n/2` the limit can never bind and LC 188 becomes LC 122. [stock-iv/Optimized.java](solution/stock-iv/Optimized.java) uses this shortcut.

```
            sell[0]=0
               |  -price
               v
  buy[1] --(+price)--> sell[1] --(-price)--> buy[2] --(+price)--> sell[2] ... sell[k]
```

---

## 9. Dry run on a full example

LC 188 with `k = 3` on `[1, 3, 2, 8, 4, 9]`, forward version. `−∞` written as `-`.

| day | price | buy1 | sell1 | buy2 | sell2 | buy3 | sell3 |
|---|---|---|---|---|---|---|---|
| start | | - | 0 | - | 0 | - | 0 |
| 0 | 1 | −1 | 0 | −1 | 0 | −1 | 0 |
| 1 | 3 | −1 | 2 | −1 | 2 | −1 | 2 |
| 2 | 2 | −1 | 2 | 0 | 2 | 0 | 2 |
| 3 | 8 | −1 | 7 | 0 | 8 | 0 | 8 |
| 4 | 4 | −1 | 7 | 3 | 8 | 4 | 8 |
| 5 | 9 | −1 | 8 | 3 | 12 | 4 | **13** |

How to read the last row: `sell3 = buy3 + 9 = 4 + 9 = 13`, and `buy3 = 4` on day 4 is `sell2 (8) − 4` — two transactions already made 8 (1→3 and 2→8), then a third buy at 4. The answer is `sell[k] = 13`, equal to LC 122's answer because three transactions are enough to take all three rises.

(The [stock-iv/Optimized.java](solution/stock-iv/Optimized.java) file would take the `k ≥ n/2` shortcut for this input, since `3 ≥ 6/2`; the table above is what the loop computes without the shortcut, and both give 13.)

---

## 10. Complexity analysis

| Problem | Memo / Tabulation time | Table space | Optimized space | Why |
|---|---|---|---|---|
| LC 121, 122, 309, 714 | O(n) | O(n) | O(1) | `2n` states (3n for cooldown), O(1) work each; each day reads only the previous one |
| LC 123 | O(n) | O(n) | O(1) | `n × 2 × 3` states, k is a constant 2 |
| LC 188 | O(n·k) | O(n·k) | O(k) | `n × 2 × (k+1)` states; forward version keeps `2k` variables |
| Brute force | O(2ⁿ) | O(n) stack | — | two choices per day, no caching |

Each state does constant work: at most two lookups and one `max`. So time = number of states.

---

## 11. Why this approach works

Any trading plan is a sequence of daily actions (rest, buy, sell) that obeys the rules. The state `(day, holding, left)` captures **everything the future needs to know**: what day it is, whether we can sell or must buy next, and how many sells remain. Two different pasts that end in the same state have exactly the same set of possible futures, so only the better past matters — that is optimal substructure, and it is why the max at each state is safe.

For the cooldown, "did I sell yesterday?" also affects the future, so either we encode it in the transition (jump to `day + 2`) or as a third mode (`COOLING`). Whenever a new rule depends on the past, ask: **what extra fact about the past must the state remember?** Add exactly that.

---

## 12. Common mistakes

1. **Counting a transaction twice** — decrementing `left` at both buy and sell. Pick one.
2. **Initialising `hold` to 0** in the forward version. That pretends you own a free share. Use `−∞` (or `Integer.MIN_VALUE / 2` to avoid overflow when adding a price).
3. **Overwriting before reading.** In the forward loop, `newHold` must use yesterday's `free`, not today's. Use temporaries or the safe update order.
4. **LC 121: buying with earlier profit.** With one transaction, a buy must start from 0 (`hold = max(hold, −price)`), not from `free − price`.
5. **Cooldown: buying from `COOLING`.** Only `FREE` may buy.
6. **Fee charged twice.** Charge it once per transaction, at the buy or at the sell, not both.
7. **LC 188 with huge `k`.** A `n × k` table may be too big; use the `k ≥ n/2` shortcut.

---

## 13. When to use this technique / how to recognize similar problems

Signals:
- You perform actions over time, and **what you may do next depends on what you did before** (you hold something, you just rested, you used up some allowance).
- The rule can be described by a small set of modes and arrows.

Recipe:
1. List the modes (holding / free / cooling / …).
2. Draw the arrows with their gains or costs.
3. `dp[day][mode] = best over incoming arrows`.
4. Add a counter dimension if an arrow may be used only a limited number of times.

You will see this again in [State Machine DP](../12-state-machine-dp/) (LC 801, LC 552, LC 1220) and in House Robber (modes "robbed yesterday" / "did not").

---

## 14. Summary and revision notes

- One state for the whole family: `(day, holding, transactionsLeft)`.
- 121: k = 1. 122: k = ∞ (drop the dimension). 123: k = 2. 188: general k. 309: sell jumps two days (or a COOLING mode). 714: subtract a fee on the sell edge.
- Backward table: `dp[day]` reads `dp[day+1]`. Forward variables: today reads yesterday.
- Forward start: `free = 0`, `hold = −∞`.
- Big `k` (≥ n/2) = unlimited.

```
f(day, 0, left) = max( f(day+1, 0, left),  -prices[day] + f(day+1, 1, left)   )
f(day, 1, left) = max( f(day+1, 1, left),   prices[day] + f(day+1, 0, left-1) )
f(n, *, *) = 0,  f(*, *, 0) = 0,  answer = f(0, 0, k)

cooldown:  sell -> f(day+2, 0)        fee:  sell -> prices[day] - fee + ...
```

---

## Exercises

**1.** Compute LC 714 on `[1, 3, 7, 5, 10, 3]` with fee 3 using the forward variables. Why is one long transaction better than two short ones here?

<details><summary>Answer</summary>

| price | 1 | 3 | 7 | 5 | 10 | 3 |
|---|---|---|---|---|---|---|
| hold | −1 | −1 | −1 | −1 | −1 | 3 |
| free | 0 | 0 | 3 | 3 | **6** | 6 |

Answer 6: buy at 1, sell at 10, pay 3 once. Two transactions 1→7 and 5→10 earn (6 − 3) + (5 − 3) = 5: the dip from 7 to 5 is only 2, smaller than the extra fee of 3, so it is not worth splitting. (`hold` stays −1 until the last day because `free − price` does not beat −1 earlier: e.g. day 3 gives 3 − 5 = −2. On the last day 6 − 3 = 3, but there is no later day to sell.)
</details>

**2.** Suppose the cooldown after a sell were **2 days** instead of 1. Change the memoized recurrence and the forward state machine.

<details><summary>Answer</summary>

Memoized: sell becomes `prices[day] + f(day + 3, 0)`. Forward: use two cooling rooms, `cool1` (sold yesterday) and `cool2` (sold two days ago): `cool1_today = hold + price`, `cool2_today = cool1`, `free_today = max(free, cool2)`. Only `free` may buy.
</details>

**3.** What is LC 188's answer for `k = 2`, prices `[3, 2, 6, 5, 0, 3]`? Fill the forward table.

<details><summary>Answer</summary>

| price | 3 | 2 | 6 | 5 | 0 | 3 |
|---|---|---|---|---|---|---|
| buy1 | −3 | −2 | −2 | −2 | 0 | 0 |
| sell1 | 0 | 0 | 4 | 4 | 4 | 4 |
| buy2 | −3 | −2 | −2 | −1 | 4 | 4 |
| sell2 | 0 | 0 | 4 | 4 | 4 | **7** |

Answer 7: 2→6 (+4) and 0→3 (+3).
</details>

**4.** Why does LC 121's forward update use `hold = max(hold, −price)` while LC 122 uses `hold = max(hold, free − price)`? What would go wrong in LC 121 with the LC 122 formula?

<details><summary>Answer</summary>

In LC 121 the buy is the first and only buy, so the cash before it is 0. With `free − price`, a buy could start from profit earned by an earlier sale — a second transaction — and on `[1, 3, 2, 8, 4, 9]` you would get 13 instead of 8. In the 3D language: LC 121 is `left = 1`, and buying from `sell[0] = 0` is exactly `−price`.
</details>
