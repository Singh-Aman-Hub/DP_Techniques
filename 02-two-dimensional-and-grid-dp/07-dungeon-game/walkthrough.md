# Dungeon Game — Walkthrough

## 1. Intuition (no code, no math — an everyday analogy)

You are planning a road trip through a grid of towns, always heading east or south, from the north-west corner to a friend's house in the south-east corner. Some towns charge tolls (you lose money), some pay you for a delivery (you gain money). Your card is declined the moment your balance hits zero, and then the trip is over. How much money must you start with?

If you plan from the **start**, you are stuck: at each town you would need to know both "how much money have I needed so far" and "how much do I have right now", and which of two routes is better depends on tolls you have not reached yet.

Now plan from the **end**. Your friend says: "When you arrive at my town you must have at least 1 coin left after my town's toll." Standing in the town just before, you can work out "I must arrive here with at least X". The town before that can do the same, and so on, back to the start. Each town only asks "what must I hold **on arrival here** so that the rest of the trip is survivable?", and that number does not care how you got there. That is the reverse DP we will build.

## 2. Problem statement in our own words

We get an `m × n` grid `dungeon`. The knight starts in the top-left room `(0, 0)` and must reach the princess in the bottom-right room `(m-1, n-1)`, moving only **right** or **down**. Entering a room adds its value to his health (negative values hurt, positive values heal). Both the first and the last room count. If his health is ever 0 or less, he dies. Return the minimum positive starting health that lets him reach the princess alive.

Original problem: [LeetCode 174 — Dungeon Game](https://leetcode.com/problems/dungeon-game/).

## 3. Examples (input → output, with an explanation)

We use **one example throughout this lesson**:

```
            col 0   col 1   col 2
row 0   [    -2      -3       3  ]
row 1   [    -5     -10       1  ]
row 2   [    10      30      -5  ]    <- princess at (2, 2)
```

Answer: **7**, using the path right, right, down, down:

```
start health 7
  enter (0,0) -2  ->  5
  enter (0,1) -3  ->  2
  enter (0,2) +3  ->  5
  enter (1,2) +1  ->  6
  enter (2,2) -5  ->  1   alive, princess rescued
```

With 6 the knight would end at 0 and die.

There are only 6 paths in a 3×3 grid, so we can check them all. For a path, let the **lowest point** be the smallest running total of room values along it; the knight needs `1 − lowest point` health (but at least 1).

| Path | Room values | Running totals | Lowest | Health needed | Final sum |
|---|---|---|---|---|---|
| R R D D | -2, -3, 3, 1, -5 | -2, -5, -2, -1, -6 | -6 | **7** | -6 |
| R D R D | -2, -3, -10, 1, -5 | -2, -5, -15, -14, -19 | -19 | 20 | -19 |
| R D D R | -2, -3, -10, 30, -5 | -2, -5, -15, 15, 10 | -15 | 16 | 10 |
| D R R D | -2, -5, -10, 1, -5 | -2, -7, -17, -16, -21 | -21 | 22 | -21 |
| D R D R | -2, -5, -10, 30, -5 | -2, -7, -17, 13, 8 | -17 | 18 | 8 |
| D D R R | -2, -5, 10, 30, -5 | -2, -7, 3, 33, 28 | -7 | 8 | **28** |

Look at the last row: the path with by far the **largest final sum** (28) is *not* the answer. It needs 8 because it dips to -7 early. The answer path ends with a negative sum but never dips as deep. So "maximize the path sum" — the Minimum Path Sum idea — is the wrong objective. What matters is the **lowest point**, not the total.

More examples:

| Input | Output | Why |
|---|---|---|
| `[[0]]` | 1 | Health must always be positive, so at least 1. |
| `[[-3]]` | 4 | `4 − 3 = 1`. |
| `[[100]]` | 1 | The potion cannot reduce the requirement below 1: he must be alive when he enters. |
| `[[-2, -3, 3]]` | 6 | One row: `6 → 4 → 1 → 4`. |

## 4. Deriving the solution — the 7 questions

### 4.0 First attempt: the forward direction, and why it fails

The natural first idea is a forward DP like [Minimum Path Sum](../04-minimum-path-sum/): for every room, remember the "best" way to arrive there. But what does a path that has arrived at a room look like? It needs **two** numbers:

- `needSoFar`: the starting health this path has required up to now (from its lowest point so far), and
- `sumSoFar`: the net health change collected so far (how much health it carries now, relative to the start).

A forward table stores one entry per cell, so at a cell where two paths meet we must keep one. Which one? Consider these two grids, which differ only in the princess's room:

```
grid A                        grid B
[    0   -2    7 ]            [    0   -2    7 ]
[    0    0    0 ]            [    0    0    0 ]
[ -100 -100   -6 ]            [ -100 -100    0 ]
```

(The `-100` rooms are deadly; no good path goes through them.) Two paths arrive at room `(1, 2)`:

```
path P (top row, then down):   0, -2, +7, 0     needSoFar = 3   sumSoFar = +5
path Q (down, then right):     0,  0,  0, 0     needSoFar = 1   sumSoFar =  0
```

Neither path is better in both numbers: P needs more, but carries more. The next room decides:

| | P then princess | Q then princess | Correct answer |
|---|---|---|---|
| grid A (princess -6) | `max(3, 1 − (5 − 6)) = 3` | `max(1, 1 − (0 − 6)) = 7` | **3** (P wins) |
| grid B (princess 0) | `max(3, 1 − 5) = 3` | `max(1, 1 − 0) = 1` | **1** (Q wins) |

So:

- **Rule 1 — "keep the smaller `needSoFar`"** keeps Q and answers **7** on grid A (correct is 3).
- **Rule 2 — "keep the larger `sumSoFar`"** keeps P and answers **3** on grid B (correct is 1).

Any forward rule that keeps one number must throw away information that a later room may need. You can run both rules yourself: [solution/ForwardDpPitfall.java](solution/ForwardDpPitfall.java) prints exactly these values.

The deeper reason: in the forward direction the "subproblem" (arrive at a cell) has an answer that depends on the **future**. That violates the requirement from [optimal substructure](../../00-foundations/05-optimal-substructure/) that a subproblem's best answer be usable no matter what comes next.

### The fix: look from the princess backward

Ask a different question at each room: "how much health must the knight have **when he enters** this room, so that he can survive **from here to the princess**?" This depends only on the rooms from here onward — the past is irrelevant, because whatever the past was, it delivered him here with some health, and we are just asking whether that health is enough. One number per cell is enough.

### 4.1 What is changing?

The knight's position: row `i` and column `j`. Two changing quantities → 2D state. (His current health is *not* part of the state; it is what we compute.)

### 4.2 What does the state represent?

> `need[i][j]` is the minimum health the knight must have at the moment he steps into room `(i, j)` (before the room's value is applied) so that he can reach the princess alive.

The answer is `need[0][0]`.

### 4.3 What are the choices?

After room `(i, j)`, he moves either **down** to `(i+1, j)` or **right** to `(i, j+1)` (if inside the grid). He will pick whichever room needs less health on entry.

### 4.4 What is the recurrence?

Let `h` be his health on entering `(i, j)`. After the room he has `h + dungeon[i][j]`. Two conditions must hold:

1. He must be able to enter the next room: `h + dungeon[i][j] ≥ min(need[i+1][j], need[i][j+1])`.
2. He must be alive on entering this room: `h ≥ 1`.

The smallest `h` satisfying both is:

```
need[i][j] = max( 1,  min(need[i+1][j], need[i][j+1])  −  dungeon[i][j] )
                  ^   \___________________________/       \___________/
                  |    health needed after this room,    this room's
                  |    via the better next room          effect
          the clamp: never below 1
```

**Why the clamp?** A big healing room makes `min(...) − dungeon[i][j]` small or even negative. Without the clamp, a value like `-98` would propagate to earlier rooms and pretend that earlier damage can be "pre-paid" by later healing. It cannot: the knight dies the moment his health hits 0, before he ever reaches the potion. The clamp says "you need at least 1 to be standing here at all".

### 4.5 What are the base cases?

At the princess's room there is no next room; he just needs at least 1 health **after** her room's effect:

```
need[m-1][n-1] = max(1, 1 − dungeon[m-1][n-1])
```

On the last row, only "right" exists; on the last column, only "down" exists. Treat a missing neighbor as `+∞`, so `min` never picks it. (Section 7 removes these special cases with padding.)

### 4.6 What is the order of computation?

`need[i][j]` reads the cell below and the cell to the right:

```
      (i, j) ----> (i, j+1)
        |
        v
     (i+1, j)
```

So rows go from `m-1` **up** to `0`, and inside each row columns go from `n-1` **left** to `0`. The table fills from the princess's corner toward the knight's corner.

### 4.7 Can the state space be optimized?

Row `i` reads row `i + 1` (the cell below) and row `i` itself (the cell to the right, already computed in this sweep). So one array of length `n` (plus one padding slot) is enough, swept right to left. See Section 8.

## 5. Brute force: recursion

```java
static int need(int[][] dungeon, int row, int col) {
    int rows = dungeon.length, cols = dungeon[0].length;
    if (row == rows - 1 && col == cols - 1)                      // princess's room
        return Math.max(1, 1 - dungeon[row][col]);
    int needDown  = (row + 1 < rows) ? need(dungeon, row + 1, col) : Integer.MAX_VALUE;
    int needRight = (col + 1 < cols) ? need(dungeon, row, col + 1) : Integer.MAX_VALUE;
    return Math.max(1, Math.min(needDown, needRight) - dungeon[row][col]);
}
```

Full file: [solution/BruteForce.java](solution/BruteForce.java).

`Integer.MAX_VALUE` stands for "impossible". It is safe here because at least one of the two moves is always real (we are not at the princess), so `min` always returns a real value and we never subtract from the sentinel.

### Recursion tree on our example

Write `n(i,j)` for `need(dungeon, i, j)`:

```
n(0,0)
├── n(1,0)
│   ├── n(2,0)
│   │   └── n(2,1)
│   │       └── n(2,2)
│   └── n(1,1)
│       ├── n(2,1)
│       │   └── n(2,2)
│       └── n(1,2)
│           └── n(2,2)
└── n(0,1)
    ├── n(1,1)
    │   ├── n(2,1)
    │   │   └── n(2,2)
    │   └── n(1,2)
    │       └── n(2,2)
    └── n(0,2)
        └── n(1,2)
            └── n(2,2)
```

`n(1,1)` is computed twice, `n(1,2)` and `n(2,1)` three times, and `n(2,2)` six times — once per path, because the tree has one leaf per path.

### Why it is slow

The number of paths in an `m × n` grid is `C(m+n−2, m−1)`, which grows exponentially (for a 200 × 200 grid, LeetCode's limit, it has about 119 digits). The tree has a leaf per path, so the brute force is exponential, roughly `O(2^(m+n))`. But there are only `m · n` distinct rooms.

## 6. Memoization (top-down)

Cache `need(i, j)`. Every real answer is at least 1, so `0` is a safe "not computed yet" marker and a plain `int[][]` works.

```java
private static int[][] memo;   // memo[row][col] = min health on entering (row, col); 0 = unknown

static int need(int[][] dungeon, int row, int col) {
    ... base case at the princess ...
    if (memo[row][col] != 0) return memo[row][col];               // cache hit
    int needDown  = ...;
    int needRight = ...;
    memo[row][col] = Math.max(1, Math.min(needDown, needRight) - dungeon[row][col]);
    return memo[row][col];
}
```

Full file: [solution/Memoization.java](solution/Memoization.java).

### Dry run with cache hits

The code tries "down" before "right":

```
n(0,0)
  n(1,0)
    n(2,0)
      n(2,1)
        n(2,2) = max(1, 1 - (-5))         = 6     (base)
      n(2,1) = max(1, 6 - 30)             = 1     -> memo[2][1]
    n(2,0) = max(1, 1 - 10)               = 1     -> memo[2][0]
    n(1,1)
      n(2,1)   ** CACHE HIT ** 1
      n(1,2)
        n(2,2) = 6                                (base, O(1))
      n(1,2) = max(1, 6 - 1)              = 5     -> memo[1][2]
    n(1,1) = max(1, min(1, 5) - (-10))    = 11    -> memo[1][1]
  n(1,0) = max(1, min(1, 11) - (-5))      = 6     -> memo[1][0]
  n(0,1)
    n(1,1)   ** CACHE HIT ** 11
    n(0,2)
      n(1,2)   ** CACHE HIT ** 5
    n(0,2) = max(1, 5 - 3)                = 2     -> memo[0][2]
  n(0,1) = max(1, min(11, 2) - (-3))      = 5     -> memo[0][1]
n(0,0) = max(1, min(6, 5) - (-2))         = 7     -> answer 7
```

Notice `n(2,1) = 1` and `n(2,0) = 1`: the `+30` and `+10` rooms are so generous that `6 − 30` and `1 − 10` are negative, and the clamp lifts them to 1.

## 7. Tabulation (bottom-up)

To avoid three different border cases, we allocate an `(m+1) × (n+1)` table. The extra row and column hold `+∞` ("you cannot go there"), except the two padding cells **next to the princess**, which hold `1` ("after her room, 1 health is enough"). Then the princess's room uses the same formula as every other room: `max(1, min(1, ∞) − dungeon) = max(1, 1 − dungeon)`.

```java
int[][] need = new int[rows + 1][cols + 1];  // need[row][col] = min health on entering (row, col)
for (int[] line : need) Arrays.fill(line, Integer.MAX_VALUE);
need[rows][cols - 1] = 1;     // virtual cell below the princess
need[rows - 1][cols] = 1;     // virtual cell right of the princess

for (int row = rows - 1; row >= 0; row--)
    for (int col = cols - 1; col >= 0; col--)
        need[row][col] = Math.max(1, Math.min(need[row + 1][col], need[row][col + 1]) - dungeon[row][col]);

return need[0][0];
```

Full file: [solution/Tabulation.java](solution/Tabulation.java).

### Table filled step by step

`∞` marks padding. Start:

```
          c0    c1    c2  | pad
r0         ?     ?     ?  |  ∞
r1         ?     ?     ?  |  ∞
r2         ?     ?     ?  |  1
---------------------------+-----
pad        ∞     ∞     1  |  ∞
```

**Row 2, right to left:**

```
need[2][2] = max(1, min(1, 1)   − (−5)) = max(1, 6)   = 6
need[2][1] = max(1, min(∞, 6)   −  30 ) = max(1, −24) = 1
need[2][0] = max(1, min(∞, 1)   −  10 ) = max(1, −9)  = 1

          c0    c1    c2  | pad
r0         ?     ?     ?  |  ∞
r1         ?     ?     ?  |  ∞
r2         1     1     6  |  1
```

**Row 1, right to left:**

```
need[1][2] = max(1, min(6, ∞)   −   1 ) = 5
need[1][1] = max(1, min(1, 5)   − (−10)) = 11
need[1][0] = max(1, min(1, 11)  − (−5)) = 6

          c0    c1    c2  | pad
r0         ?     ?     ?  |  ∞
r1         6    11     5  |  ∞
r2         1     1     6  |  1
```

**Row 0, right to left:**

```
need[0][2] = max(1, min(5, ∞)   −   3 ) = 2
need[0][1] = max(1, min(11, 2)  − (−3)) = 5
need[0][0] = max(1, min(6, 5)   − (−2)) = 7

          c0    c1    c2  | pad
r0         7     5     2  |  ∞        <- answer need[0][0] = 7
r1         6    11     5  |  ∞
r2         1     1     6  |  1
```

Read the answer path from the table: at `(0,0)` the cheaper next room is `(0,1)` (5 < 6); from `(0,1)` it is `(0,2)` (2 < 11); then down to `(1,2)`, then `(2,2)`. That is R R D D, the path from Section 3.

## 8. Space optimization

`need[row][col]` reads `need[row+1][col]` (below) and `need[row][col+1]` (right). Keep one array `need` of length `n + 1` and sweep each row **right to left**:

- `need[col]` still holds the value of the row **below** (we have not overwritten it yet in this row);
- `need[col + 1]` already holds the **current** row's value (we just wrote it) — which is exactly the "right" neighbor we want.

```java
int[] need = new int[cols + 1];          // need[col] = min health on entering (current row, col)
Arrays.fill(need, Integer.MAX_VALUE);
need[cols - 1] = 1;                      // virtual cell below the princess

for (int row = rows - 1; row >= 0; row--)
    for (int col = cols - 1; col >= 0; col--)
        need[col] = Math.max(1, Math.min(need[col], need[col + 1]) - dungeon[row][col]);

return need[0];
```

Full file: [solution/Optimized.java](solution/Optimized.java).

Unlike [Maximum Falling Path Sum](../06-maximum-falling-path-sum/), mixing old and new values in one array is *exactly* what we want here, because one dependency is in the previous row (old value) and the other is in the same row (new value).

## 9. Dry run on a full example (table of state values per step)

The one-row array, written as `[c0, c1, c2 | pad]`:

| Step | Cell | `min(below, right)` | `− room` | Clamp | Array afterward |
|---|---|---|---|---|---|
| start | – | – | – | – | `[∞, ∞, 1 \| ∞]` |
| 1 | (2,2) | `min(1, ∞) = 1` | `1 + 5 = 6` | 6 | `[∞, ∞, 6 \| ∞]` |
| 2 | (2,1) | `min(∞, 6) = 6` | `6 − 30 = −24` | 1 | `[∞, 1, 6 \| ∞]` |
| 3 | (2,0) | `min(∞, 1) = 1` | `1 − 10 = −9` | 1 | `[1, 1, 6 \| ∞]` |
| 4 | (1,2) | `min(6, ∞) = 6` | `6 − 1 = 5` | 5 | `[1, 1, 5 \| ∞]` |
| 5 | (1,1) | `min(1, 5) = 1` | `1 + 10 = 11` | 11 | `[1, 11, 5 \| ∞]` |
| 6 | (1,0) | `min(1, 11) = 1` | `1 + 5 = 6` | 6 | `[6, 11, 5 \| ∞]` |
| 7 | (0,2) | `min(5, ∞) = 5` | `5 − 3 = 2` | 2 | `[6, 11, 2 \| ∞]` |
| 8 | (0,1) | `min(11, 2) = 2` | `2 + 3 = 5` | 5 | `[6, 5, 2 \| ∞]` |
| 9 | (0,0) | `min(6, 5) = 5` | `5 + 2 = 7` | 7 | `[7, 5, 2 \| ∞]` |

Answer: `need[0] = 7`. Every value matches the 2D table in Section 7 and the memo in Section 6.

## 10. Complexity analysis (explained, for every approach)

| Approach | Time | Space | Explanation |
|---|---|---|---|
| Brute force | exponential, about `O(2^(m+n))` | `O(m + n)` | One leaf per path, and there are `C(m+n−2, m−1)` paths. Stack depth is the path length `m + n − 1`. |
| Memoization | `O(m·n)` | `O(m·n)` | Each room is computed once with `O(1)` work; memo has `m·n` entries; stack adds `O(m + n)`. |
| Tabulation | `O(m·n)` | `O(m·n)` | Two nested loops, `O(1)` per cell; the padded table has `(m+1)(n+1)` entries. |
| Optimized | `O(m·n)` | `O(n)` | Same loops, one array of length `n + 1`. |

`O(m·n)` time is optimal: any room could be the one that kills the knight, so every room must be read.

## 11. Why this approach works (correctness argument in plain words)

Fix a room `(i, j)` and any starting health `h` on entering it. Whether the knight can survive from here to the princess depends only on `h` and the rooms from `(i, j)` onward — not on how he got here. And it is **monotone**: if `h` is enough, then `h + 1` is also enough (more health never hurts). So "the set of survivable entering healths" is exactly `{need[i][j], need[i][j] + 1, …}`, and one number describes it completely. That is why the reverse state needs just one number per cell, while the forward direction needed two.

The recurrence then follows: with health `h` on entering, he has `h + dungeon[i][j]` afterward, must be alive (`h ≥ 1`), and can continue iff `h + dungeon[i][j]` reaches the smaller of the two next requirements. The smallest such `h` is the formula in 4.4. By induction from the princess's room backward, every `need` value is correct, and `need[0][0]` is the answer.

## 12. Common mistakes

1. **Forward DP keeping one number.** Section 4.0 shows both natural rules fail.
2. **Maximizing the path sum.** The path with the biggest total (28 in our example) needs 8, not 7. The lowest point matters, not the end.
3. **Forgetting the clamp `max(1, …)`.** Healing rooms then produce zero or negative requirements that wrongly "pre-pay" earlier damage.
4. **Wrong base case.** At the princess the knight needs 1 health **after** her room: `max(1, 1 − dungeon[m-1][n-1])`, not `1` and not `−dungeon[m-1][n-1]`.
5. **Putting `0` in the padding next to the princess.** It must be `1`; with `0`, `[[−3]]` would give 3 instead of 4.
6. **Subtracting from `Integer.MAX_VALUE` when both neighbors are padding.** Only the princess's room has both real neighbors missing; the two `1` cells in the padding prevent that.
7. **Sweeping the one-row array left to right.** `need[col + 1]` would still hold the row below, not the current row's right neighbor.

## 13. When to use this technique / how to recognize similar problems

**Signals for a reverse DP:**

- The question asks for a **minimum starting amount** (health, fuel, money).
- A **running value must stay above a threshold at every step**, not only at the end.
- A forward summary needs **two or more numbers** that do not dominate one another.

**The direction test.** Before writing a state, ask: *"Is the best answer for this subproblem the same no matter what happens on the other side of it?"*

- Forward state "best way to arrive here": does its usefulness depend on the future? In Minimum Path Sum, no (the smallest sum so far is always best). In Dungeon Game, yes → try the other direction.
- Reverse state "what is needed from here on": does it depend on the past? In Dungeon Game, no → use it.

A useful alternative to know: if the **starting health were fixed**, a forward DP would work, because then "more health now" is always at least as good (Exercise 3). Combined with binary search over the starting health, that gives a correct but slower `O(m·n·log(answer))` solution. The reverse DP is simpler and faster.

Related: [Triangle](../05-triangle/) (where reverse was merely cleaner), [Longest Increasing Path in a Matrix](https://leetcode.com/problems/longest-increasing-path-in-a-matrix/) (state "longest path *starting* here").

## 14. Summary and revision notes

- Forward DP fails: an arriving path needs two numbers (required-so-far and current health), and neither dominates.
- Reverse state: `need[i][j]` = min health on **entering** `(i, j)` to survive to the princess. One number suffices because survivability is monotone in health and independent of the past.
- Clamp at 1: he must be alive to enter a room; later potions cannot pay for earlier damage.
- Fill from bottom-right to top-left. Pad with `∞`, and put `1` next to the princess.
- One array of length `n + 1`, swept right to left: `O(m·n)` time, `O(n)` space.

```
+---------------------------------------------------------------------------+
|  need[m-1][n-1] = max(1, 1 − d[m-1][n-1])                                 |
|  need[i][j]     = max(1, min(need[i+1][j], need[i][j+1]) − d[i][j])       |
|                   (missing neighbors count as +∞)                         |
|  answer         = need[0][0]                                              |
+---------------------------------------------------------------------------+
```

## Exercises

**Exercise 1.** Fill the `need` table for `[[1, -3, 3], [0, -2, 0], [-3, -3, -3]]`. What is the answer, and which path achieves it?

<details>
<summary>Answer</summary>

Row 2: `need[2][2] = max(1, 1+3) = 4`, `need[2][1] = max(1, 4+3) = 7`, `need[2][0] = max(1, 7+3) = 10`.
Row 1: `need[1][2] = max(1, 4−0) = 4`, `need[1][1] = max(1, min(7,4)+2) = 6`, `need[1][0] = max(1, min(10,6)−0) = 6`.
Row 0: `need[0][2] = max(1, 4−3) = 1`, `need[0][1] = max(1, min(6,1)+3) = 4`, `need[0][0] = max(1, min(6,4)−1) = 3`.

Answer **3**, path R R D D: `3 → 4 → 1 → 4 → 4 → 1`.
</details>

**Exercise 2.** Remove the clamp (use `need = min(...) − dungeon[i][j]` everywhere except the base case). What does it return for the single-column dungeon `[[-5], [100], [-1]]`, and what is the correct answer?

<details>
<summary>Answer</summary>

Correct: `need[2] = max(1, 1+1) = 2`, `need[1] = max(1, 2−100) = 1`, `need[0] = max(1, 1+5) = 6`. Answer **6**: he must survive the first room (`6 − 5 = 1`) before he ever sees the potion.

Without the clamp: `need[1] = 2 − 100 = −98`, `need[0] = −98 + 5 = −93`. That is nonsense — it pretends the potion in room 1 can pay for the damage in room 0.
</details>

**Exercise 3.** Suppose the starting health `H` is **given**, and you only have to answer "can the knight survive?". Explain why a forward DP storing one number per cell — the maximum health he can have after leaving that room, or "dead" — is correct here, even though forward DP failed for the original question.

<details>
<summary>Answer</summary>

With `H` fixed, every path arriving at a cell is described by just one number: its current health (the "required so far" is no longer a free variable — either the path stayed alive with `H`, or it is dead and discarded). And more current health is never worse for the future: any continuation survivable with health `x` is survivable with `x + 1`. So keeping the maximum dominates every other arrival. Recurrence: `best[i][j] = max(best[i-1][j], best[i][j-1]) + d[i][j]`, marked dead if it is `≤ 0` or if both parents are dead. Binary searching on `H` with this check also solves LeetCode 174, in `O(m·n·log H)`.
</details>

**Exercise 4.** If the knight could also move **up** and **left**, would this DP still work? Why or why not?

<details>
<summary>Answer</summary>

No. With four directions the "depends on" arrows form cycles (a room depends on its right neighbor, which depends back on it), so there is no fill order in which every dependency is ready first. Grid DP needs a dependency order without cycles. Such problems are solved with graph algorithms (for example a Dijkstra-style search, or binary search on the starting health plus a BFS), which are covered in [DP on graphs](../../04-advanced-dp-patterns/08-dp-on-graphs/).
</details>
