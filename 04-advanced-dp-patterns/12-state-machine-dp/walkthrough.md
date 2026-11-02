# State Machine DP — Walkthrough

Many processes move step by step through a **small, fixed set of situations**: today's activity, the last letter written, the shape of the edge of a tiling, the key a knight stands on. Rules say which situation may follow which. A **state machine** is that picture: a few circles (states) and arrows (allowed transitions). **State machine DP** walks along the steps and keeps one number per state:

```
dp[i][state] = the best value (or the number of ways) after i steps, ending in `state`
```

You already met one state machine in [Stock Buy and Sell](../02-stock-buy-and-sell/), where the states were "holding a share", "not holding" and "in cooldown". This lesson makes the method general. The main problem is [AtCoder DP C — Vacation](https://atcoder.jp/contests/dp/tasks/dp_c), and then four LeetCode problems, each with its state diagram drawn: [1220](https://leetcode.com/problems/count-vowels-permutation/), [790](https://leetcode.com/problems/domino-and-tromino-tiling/), [552](https://leetcode.com/problems/student-attendance-record-ii/) and [935](https://leetcode.com/problems/knight-dialer/).

---

## 1. Intuition

A traffic light has three states: green, yellow, red. It may go green → yellow → red → green, but never green → red directly. If someone asks, "in how many ways can the light change over the next 10 ticks?", you do not need to remember the whole history. At every tick you only need to know **which colour it is now**, because that alone decides what can come next.

That is the key property of a state machine: **the past matters only through the current state.** So the DP needs just one number per state per step, instead of one per possible history. With 3 states and 10 steps, that is 30 numbers, not 3¹⁰ histories.

---

## 2. Modelling a process as a state machine

Three questions turn a word problem into a state machine:

1. **What is a step?** A day, a letter, a column, a move.
2. **What must I remember about the past to know which choices are allowed next?** That memory is the state. Keep it as small as possible.
3. **Which state can follow which?** Draw an arrow for every allowed transition, with the gain or cost on it if there is one.

Then the DP is always the same:

```
dp[0][s]   = starting value of state s
dp[i][s]   = combine over every arrow t -> s of ( dp[i-1][t] (+) value of the step )
answer     = combine over s of dp[n-1][s]   (or dp[n][s])
```

`combine` is `max`/`min` for optimisation and `+` for counting.

### The stocks problems, seen this way

For [Best Time to Buy and Sell Stock with Cooldown](https://leetcode.com/problems/best-time-to-buy-and-sell-stock-with-cooldown/):

```
              buy (-price)
   +------+ -------------> +---------+
   | REST |                | HOLDING |----+
   +------+ <---+          +---------+    | keep holding
     ^  |       |               |    ^----+
     |  | rest  |               | sell (+price)
     +--+       |               v
                |          +----------+
                +----------| COOLDOWN |
                 next day  +----------+
```

`dp[day][HOLDING]`, `dp[day][REST]`, `dp[day][COOLDOWN]` hold the best profit for each state. Every stock variant is the same picture with different states (a transaction counter, a fee on the sell arrow). Vacation below is the simplest possible machine, which makes the method easy to see.

---

## 3. Problem statement in our own words

Original: [AtCoder DP C — Vacation](https://atcoder.jp/contests/dp/tasks/dp_c)

A vacation lasts `N` days. Each day Taro does exactly one of three activities, A, B or C, and day `i` activity `j` gives `happiness[i][j]` points. He refuses to do the **same activity on two consecutive days**. Find the maximum total happiness.

---

## 4. Examples

**Running example (sample 1):**

```
day   A    B    C
 0   10   40   70
 1   20   50   80
 2   30   60   90
```
Answer **210**: C, B, C → 70 + 50 + 90. (C, C, C would give 240, but repeats are forbidden.)

**Sample 2 (edge):** one day `[100, 10, 1]` → **100**. With one day there is no restriction.

**Sample 3:** 7 days `[[6,7,8],[8,8,3],[2,5,2],[7,8,6],[4,6,8],[2,3,4],[7,5,1]]` → **46**.

**Trap example:** `[[9,1,1],[9,1,1]]` → **10**, not 18: A on both days is not allowed.

---

## 5. Deriving the solution — the 7 questions

### Q1. What is changing?
The **day**, and what Taro did **yesterday** (it limits today's choice).

### Q2. What does the state represent?
> `dp[day][s]` = the maximum total happiness over days `0 … day`, given that the activity on `day` is `s` (s ∈ {A, B, C}).

The state diagram has three states and an arrow between every **different** pair. There are no self-loops:

```
          +-----+           +-----+
          |  A  | <-------> |  B  |
          +-----+           +-----+
             ^                 ^
              \               /
               v             v
                  +-----+
                  |  C  |
                  +-----+

   no arrow A->A, B->B, C->C  (same activity twice in a row)
```

### Q3. What are the choices?
On `day`, choose an activity `s`. It must differ from yesterday's activity `t`.

### Q4. What is the recurrence?
```
dp[day][A] = happiness[day][A] + max(dp[day-1][B], dp[day-1][C])
dp[day][B] = happiness[day][B] + max(dp[day-1][A], dp[day-1][C])
dp[day][C] = happiness[day][C] + max(dp[day-1][A], dp[day-1][B])
answer     = max(dp[N-1][A], dp[N-1][B], dp[N-1][C])
```
Each line reads: "to end today in state s, I came from some state with an arrow into s. Take the best of those, plus today's points."

### Q5. What are the base cases?
Day 0 has no yesterday, so every activity is allowed: `dp[0][s] = happiness[0][s]`.

### Q6. What is the order of computation?
Day `day` uses only day `day − 1`. Loop days forward. Inside a day, the three states are independent.

### Q7. Can the state space be optimized?
Only the previous day is read, so three variables `a, b, c` are enough. The new values must be computed from the **old** values before any of them is overwritten.

---

## 6. Brute force: recursion

Choose an activity for each day, skipping yesterday's:

```java
private static long best(int[][] happiness, int day, int yesterday) {
    if (day == happiness.length) return 0;
    long result = 0;
    for (int activity = 0; activity < 3; activity++) {
        if (activity == yesterday) continue;           // forbidden arrow
        result = Math.max(result, happiness[day][activity] + best(happiness, day + 1, activity));
    }
    return result;
}
```

Recursion tree on the running example (`best(day, yesterday)`):

```
                          best(0, none)
          /                     |                     \
    A: best(1,A)           B: best(1,B)           C: best(1,C)
     /        \             /        \             /        \
best(2,B) best(2,C)    best(2,A) best(2,C)    best(2,A) best(2,B)
```

Level 2 has six calls but only three distinct states: `best(2,A)`, `best(2,B)`, `best(2,C)` each appear twice. In general the tree has 3 · 2^(N−1) leaves, while there are only 3 distinct states per day. With N = 10⁵ (AtCoder's limit), the brute force is hopeless.

---

## 7. Memoization (top-down)

Cache `best(day, yesterday)`: N × 4 states (yesterday can also be "none" on day 0).

```java
if (memo[day][yesterday + 1] != -1) return memo[day][yesterday + 1];
...
memo[day][yesterday + 1] = result;
```

Dry run (values are "best from this day to the end"):

| Call | Computation | Result |
|---|---|---|
| best(2, A) | max(B 60, C 90) | 90 |
| best(2, B) | max(A 30, C 90) | 90 |
| best(2, C) | max(A 30, B 60) | 60 |
| best(1, A) | max(B 50 + best(2,B) 90, C 80 + best(2,C) 60) = max(140, 140) | 140 |
| best(1, B) | max(A 20 + best(2,A) **hit** 90, C 80 + best(2,C) **hit** 60) | 140 |
| best(1, C) | max(A 20 + 90 **hit**, B 50 + 90 **hit**) | 140 |
| best(0, none) | max(A 10 + 140, B 40 + 140, C 70 + 140) | **210** |

Four of the requests are cache hits; each state is computed once.

---

## 8. Tabulation (bottom-up)

```java
for (int s = 0; s < 3; s++) dp[0][s] = happiness[0][s];
for (int day = 1; day < days; day++)
    for (int s = 0; s < 3; s++) {
        long bestPrevious = 0;
        for (int t = 0; t < 3; t++) if (t != s) bestPrevious = Math.max(bestPrevious, dp[day - 1][t]);
        dp[day][s] = happiness[day][s] + bestPrevious;
    }
```

Filling the table:

| day | dp[A] | dp[B] | dp[C] |
|---|---|---|---|
| 0 | 10 | 40 | 70 |
| 1 | 20 + max(40, 70) = **90** | 50 + max(10, 70) = **120** | 80 + max(10, 40) = **120** |
| 2 | 30 + max(120, 120) = **150** | 60 + max(90, 120) = **180** | 90 + max(90, 120) = **210** |

Answer `max(150, 180, 210)` = **210**. Follow it back: day 2 C (90) came from day 1 B (120), which came from day 0 C (70). So C, B, C ✓.

---

## 9. Space optimization

```java
long a = happiness[0][0], b = happiness[0][1], c = happiness[0][2];
for (int day = 1; day < n; day++) {
    long newA = happiness[day][0] + Math.max(b, c);
    long newB = happiness[day][1] + Math.max(a, c);
    long newC = happiness[day][2] + Math.max(a, b);
    a = newA; b = newB; c = newC;
}
```

The temporary `newA, newB, newC` matter. If you wrote `a = … max(b, c)` and then `b = … max(a, c)`, the second line would read **today's** `a`, as if Taro did A and B on the same day.

---

## 10. Dry run on a full example

Sample 3, 7 days, with the three running values after each day:

| day | happiness (A, B, C) | a | b | c |
|---|---|---|---|---|
| 0 | 6, 7, 8 | 6 | 7 | 8 |
| 1 | 8, 8, 3 | 8 + 8 = 16 | 8 + 8 = 16 | 3 + 7 = 10 |
| 2 | 2, 5, 2 | 2 + 16 = 18 | 5 + 16 = 21 | 2 + 16 = 18 |
| 3 | 7, 8, 6 | 7 + 21 = 28 | 8 + 18 = 26 | 6 + 21 = 27 |
| 4 | 4, 6, 8 | 4 + 27 = 31 | 6 + 28 = 34 | 8 + 28 = 36 |
| 5 | 2, 3, 4 | 2 + 36 = 38 | 3 + 36 = 39 | 4 + 34 = 38 |
| 6 | 7, 5, 1 | 7 + 39 = **46** | 5 + 38 = 43 | 1 + 39 = 40 |

Answer **46** ✓.

---

## 11. Complexity analysis

| Approach | Time | Space | Why |
|---|---|---|---|
| Brute force | O(3 · 2^(N−1)) | O(N) | 3 choices on day 0, then 2 per day |
| Memoization | O(N · 4 · 3) = O(N) | O(N) | N × 4 states, 3 choices each |
| Tabulation | O(N · 3 · 3) = O(N) | O(N) | 3 states per day, each reads 3 previous states |
| Three variables | O(N) | O(1) | the same work, keeping only yesterday |

In general, state machine DP costs **O(steps × number of arrows)** time and **O(number of states)** space after optimisation. Small machines make it linear in the number of steps.

---

## 12. Why this approach works

- **The state captures everything the future depends on.** Tomorrow's allowed choices depend only on today's activity. Two schedules that both end in C today have identical futures, so only the better of them needs to be kept.
- **Every schedule is a walk in the diagram.** A valid N-day schedule is exactly a walk of length N through the machine. The DP tries every arrow into every state on every day, so it considers every walk.
- **Optimal substructure.** The best schedule ending in s on `day` consists of the best schedule ending in some allowed `t` on `day − 1`, plus today. If the prefix were not the best one for `t`, we could swap in a better one.

---

## 13. Four more machines

Each problem below: what a step is, the states, the diagram, the recurrence, and a short dry run. All four are **counting** problems (combine with `+`, modulo 10⁹ + 7).

### 13.1 LeetCode 1220 — Count Vowels Permutation (states = last letter)

[LeetCode 1220](https://leetcode.com/problems/count-vowels-permutation/): count the strings of length n over the vowels a, e, i, o, u that obey "what may follow what" rules (each `a` is followed only by `e`, and so on).

- **Step:** one more letter.
- **State:** the last letter (5 states).
- **Arrows** (from the rules): `a→e`, `e→a`, `e→i`, `i→a`, `i→e`, `i→o`, `i→u`, `o→i`, `o→u`, `u→a`.

```
       +----------- i -> a -----------+
       v                              |
     +---+          +---+          +---+          +---+
     | a |  <====>  | e |  <====>  | i |  <====>  | o |
     +---+          +---+          +---+          +---+
       ^                              |              |
       |                       i -> u |              | o -> u
       |                              v              |
       |                            +---+            |
       +---------- u -> a --------- | u | <----------+
                                    +---+
```

`x <====> y` is a pair of arrows, `x -> y` and `y -> x`. Reading them **backwards** gives the recurrence ("which letters can come right before x?"):

```
a[len] = e + i + u        (e->a, i->a, u->a)
e[len] = a + i            (a->e, i->e)
i[len] = e + o            (e->i, o->i)
o[len] = i                (i->o)
u[len] = i + o            (i->u, o->u)
```

All at length `len − 1`. Base: every vowel alone, `1, 1, 1, 1, 1`.

| length | a | e | i | o | u | total |
|---|---|---|---|---|---|---|
| 1 | 1 | 1 | 1 | 1 | 1 | 5 |
| 2 | 3 | 2 | 2 | 1 | 2 | 10 |
| 3 | 6 | 5 | 3 | 2 | 3 | 19 |
| 4 | 11 | 9 | 7 | 3 | 5 | 35 |
| 5 | 21 | 18 | 12 | 7 | 10 | **68** |

n = 5 → **68** ✓. Five counters, O(n) time.

### 13.2 LeetCode 790 — Domino and Tromino Tiling (states = shape of the edge)

[LeetCode 790](https://leetcode.com/problems/domino-and-tromino-tiling/): count the ways to tile a 2 × n board with 2 × 1 dominoes and L-shaped trominoes (both may be rotated).

- **Step:** one more column.
- **State:** the shape of the right edge of what has been tiled. It can only be flat, or stick out by one cell on top, or by one cell on the bottom:

```
 FULL[i]            TOP[i]               BOTTOM[i]
 cols < i covered   cols < i covered     cols < i covered
                    + top of col i       + bottom of col i

 ##|                ##|#                 ##|.
 ##|                ##|.                 ##|#
    ^ column i         ^ column i           ^ column i
```

The machine (arrows labelled with the piece that is placed):

```
                          vertical domino, or
                          two horizontal dominoes
                               +-----+
                               v     |
                          +----------+
              +---------- |   FULL   | ----------+
              | tromino   +----------+   tromino |
              v               ^    ^             v
         +---------+          |    |        +----------+
         |   TOP   | ---------+    +------- |  BOTTOM  |
         +---------+  tromino      tromino  +----------+
              |  ^                               |  ^
              |  +----------- domino ------------+  |
              +-------------- domino ---------------+
```

The vertical domino and the trominoes out of FULL, and the dominoes between TOP and BOTTOM, advance the edge by one column. The two horizontal dominoes and the trominoes back into FULL advance it by two columns.

Recurrence (pull form):

```
full[i]   = full[i-1]                 vertical domino in column i-1
          + full[i-2]                 two horizontal dominoes in columns i-2, i-1
          + top[i-2] + bottom[i-2]    tromino filling the missing cell of column i-2 and all of column i-1
top[i]    = full[i-1]                 tromino: both cells of column i-1 + top of column i
          + bottom[i-1]               horizontal domino: top of column i-1 + top of column i
bottom[i] = full[i-1] + top[i-1]      mirror image
```

Base: `full[0] = 1` (an empty board has one tiling: place nothing); `top[0] = bottom[0] = 0`.

| i | full | top | bottom |
|---|---|---|---|
| 0 | 1 | 0 | 0 |
| 1 | 1 | 1 | 1 |
| 2 | 1 + 1 + 0 + 0 = 2 | 1 + 1 = 2 | 2 |
| 3 | 2 + 1 + 1 + 1 = **5** | 2 + 2 = 4 | 4 |
| 4 | 5 + 2 + 2 + 2 = **11** | 5 + 4 = 9 | 9 |

n = 3 → 5 ✓ (LeetCode's example). Because the board is symmetric, `top = bottom` always. The solution file checks the machine against the closed formula `f(n) = 2f(n−1) + f(n−3)` for n up to 1000. You can derive that formula from the machine, but the machine is much easier to find on your own.

### 13.3 LeetCode 552 — Student Attendance Record II (states = two counters)

[LeetCode 552](https://leetcode.com/problems/student-attendance-record-ii/): count the attendance records of length n over A (absent), L (late), P (present) with **fewer than 2 A's in total** and **never 3 L's in a row**.

- **Step:** one more day.
- **State:** what the rules need to remember: the number of A's so far (0 or 1) and the number of L's at the **end** of the record (0, 1 or 2). That is 2 × 3 = 6 states, written `(A count, trailing L's)`.

```
                   L               L
        +-------+      +-------+      +-------+
  A=0   | (0,0) | ---> | (0,1) | ---> | (0,2) |
        +-------+      +-------+      +-------+
          ^   |          |   |          |   |
          +---|----P-----+---|----P-----+   |       P: back to (0,0)
              |              |              |
              A              A              A       A: down to (1,0)
              |              |              |
              +--------------+--------------+
              |
              v    L               L
        +-------+      +-------+      +-------+
  A=1   | (1,0) | ---> | (1,1) | ---> | (1,2) |
        +-------+      +-------+      +-------+
          ^              |              |
          +--------P-----+--------P-----+           P: back to (1,0)
```

The rules in words: **P** from any state goes to `(same A, 0)`, so `(0,0)` and `(1,0)` also have a P arrow to themselves (not drawn). **A** from any `(0, l)` goes to `(1, 0)`; there is no A arrow out of the right half. **L** from `(a, l)` goes to `(a, l+1)` only if `l < 2`.

| days | (0,0) | (0,1) | (0,2) | (1,0) | (1,1) | (1,2) | total |
|---|---|---|---|---|---|---|---|
| 0 | 1 | 0 | 0 | 0 | 0 | 0 | 1 |
| 1 | 1 | 1 | 0 | 1 | 0 | 0 | 3 |
| 2 | 2 | 1 | 1 | 3 | 1 | 0 | **8** |
| 3 | 4 | 2 | 1 | 8 | 3 | 1 | 19 |
| 4 | 7 | 4 | 2 | 19 | 8 | 3 | 43 |

n = 2 → **8** ✓ (all 9 two-letter records except "AA"). Check one cell: `(1,0)` on day 2 = A from `(0,0), (0,1)` (1 + 1) + P from `(1,0)` (1) = 3 ✓.

The solution file also checks n = 10101 → 183236316 and compares n = 1 … 9 with a brute force over all 3ⁿ records.

### 13.4 LeetCode 935 — Knight Dialer (states = keys)

[LeetCode 935](https://leetcode.com/problems/knight-dialer/): a chess knight on a phone keypad dials an n-digit number by making n − 1 knight moves. How many distinct numbers can it dial?

- **Step:** one more digit.
- **State:** the key the knight stands on (10 states).
- **Arrows:** the knight moves. They are symmetric (if 1 → 6, then 6 → 1), and key 5 has none.

```
   keypad          knight moves (each line is a two-way arrow)

   1  2  3         1 - 6    1 - 8    2 - 7    2 - 9
   4  5  6         3 - 4    3 - 8    4 - 9    4 - 0
   7  8  9         6 - 7    6 - 0
      0            5: no moves
```

Drawn as a graph, the arrows form one ring through 8 keys, with 0 attached to 4 and 6, and 5 on its own:

```
     1 ------- 8 ------- 3
     |                   |
     6 ------- 0 ------- 4
     |                   |
     7                   9
      \                 /
       +----- 2 -------+

     5   (no knight move starts or ends here)
```

Recurrence: `count[len][k] = sum of count[len−1][j] over every key j that has a move to k`. Base: `count[1][k] = 1` for all 10 keys.

| len | 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 | total |
|---|---|---|---|---|---|---|---|---|---|---|---|
| 1 | 1 | 1 | 1 | 1 | 1 | 1 | 1 | 1 | 1 | 1 | 10 |
| 2 | 2 | 2 | 2 | 2 | 3 | 0 | 3 | 2 | 2 | 2 | **20** |
| 3 | 6 | 5 | 4 | 5 | 6 | 0 | 6 | 5 | 4 | 5 | **46** |

Example: `count[3][0] = count[2][4] + count[2][6] = 3 + 3 = 6`. Note `count[1][5] = 1`: a 1-digit number can be "5", but nothing ever leads to or from 5 afterwards.

---

## 14. Common mistakes

1. **Overwriting a state before the others have read it.** Compute all new values from the old ones (temporary variables or a `next` array).
2. **Missing a state.** If the rule depends on *two* facts (absences **and** trailing lates), the state needs both. A good test: can you decide whether the next step is allowed using only the state?
3. **Too much state.** Remembering the whole last k letters when only "how many trailing L's" matters makes the machine much larger for nothing.
4. **Reading the arrows the wrong way round.** "a may be followed by e" is the arrow `a → e`. For the pull recurrence of `e`, you need the arrows **into** e.
5. **Forgetting the modulo after each addition** in counting problems. Several counts added together can exceed `long` after many steps if you only reduce at the end.
6. **Wrong base case.** Vacation: day 0 has no restriction. Domino: `full[0] = 1`. Knight dialer: every key, including 5, is a valid 1-digit number.

---

## 15. When to use this technique / how to recognize similar problems

- The input is a sequence of steps (days, letters, columns, moves), and the rule for the next step depends on **a small amount of recent history**.
- Phrases: "cannot do the same thing twice in a row", "must wait one day after", "at most k consecutive", "fewer than 2 in total", "which may follow which".
- Counting strings or sequences under local rules (1220, 552), tiling boards column by column (790, 1411), walks on a small fixed graph (935).
- When n is huge (10¹⁸), the same machine can be raised to a power with [matrix exponentiation](../../05-dp-optimization/09-matrix-exponentiation/), because each step is a fixed linear map on the state vector.

---

## 16. Summary and revision notes

- A state machine = a few states + allowed arrows. Design the state as "the least I must remember to know what is allowed next".
- `dp[i][s] = combine over arrows t → s of (dp[i−1][t] (+) step value)`.
- Space: keep one value per state (O(states)); compute new values from old ones before overwriting.
- Time: O(steps × arrows).
- Stocks, Vacation, vowel strings, tilings, attendance records and the knight dialer are all the same method with different diagrams.

```
+------------------------------------------------------------+
|  dp[0][s]  = start value of state s                         |
|  dp[i][s]  = combine over t -> s of ( dp[i-1][t] (+) gain ) |
|  answer    = combine over s of dp[last][s]                  |
|                                                            |
|  Vacation: dp[d][s] = h[d][s] + max(dp[d-1][t], t != s)     |
+------------------------------------------------------------+
```

---

## 17. Exercises

**Exercise 1.** Vacation with the extra rule "activity C may not be done on two days in a row, but A and B may repeat". Draw the new diagram and write the recurrence.

<details>
<summary>Answer</summary>

Arrows: every pair, plus self-loops on A and B; no `C → C`.
```
dp[d][A] = h[d][A] + max(dp[d-1][A], dp[d-1][B], dp[d-1][C])
dp[d][B] = h[d][B] + max(dp[d-1][A], dp[d-1][B], dp[d-1][C])
dp[d][C] = h[d][C] + max(dp[d-1][A], dp[d-1][B])
```
</details>

**Exercise 2.** Continue the Count Vowels table to length 6.

<details>
<summary>Answer</summary>

From length 5 `(21, 18, 12, 7, 10)`: a = 18 + 12 + 10 = 40, e = 21 + 12 = 33, i = 18 + 7 = 25, o = 12, u = 12 + 7 = 19. Total **129**.
</details>

**Exercise 3.** In Student Attendance Record II, how many states would you need if the rules were "fewer than 3 A's in total and never 4 L's in a row"? Compute the n = 1 answer.

<details>
<summary>Answer</summary>

A count 0, 1, 2 (3 values) × trailing L's 0, 1, 2, 3 (4 values) = **12 states**. For n = 1, every single letter A, L, P is valid → **3**.
</details>

**Exercise 4.** Use the Knight Dialer table to compute the total for n = 4.

<details>
<summary>Answer</summary>

From len 3 `[6,5,4,5,6,0,6,5,4,5]`:
0 = c4 + c6 = 12; 1 = c6 + c8 = 10; 2 = c7 + c9 = 10; 3 = c4 + c8 = 10; 4 = c0 + c3 + c9 = 16; 5 = 0; 6 = c0 + c1 + c7 = 16; 7 = c2 + c6 = 10; 8 = c1 + c3 = 10; 9 = c2 + c4 = 10.
Total = 12 + 10 + 10 + 10 + 16 + 0 + 16 + 10 + 10 + 10 = **104**.
</details>
