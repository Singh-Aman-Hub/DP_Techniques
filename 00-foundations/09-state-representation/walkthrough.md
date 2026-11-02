# State Representation — Walkthrough

> Code for this lesson: [`StairsState.java`](solution/StairsState.java) · [`GridState.java`](solution/GridState.java) · [`KnapsackState.java`](solution/KnapsackState.java)

In topics 01–08 every subproblem had an obvious name. Fibonacci's subproblems were `fib(0)`, `fib(1)`, …, `fib(n)`. The single number `n` told you exactly which subproblem you meant. That number is the **state**.

Most problems do not hand you the state. You have to design it. This lesson teaches you how.

---

## 1. Intuition

Imagine you are playing a long board game and need to stop halfway through. You take a photo of the board so you can continue tomorrow.

- If the photo shows **too little** (for example only your token, not how much money you have), you cannot continue correctly tomorrow. You would have to guess, and you might guess wrong.
- If the photo shows **too much** (the weather, what you ate for lunch, how many turns you have already taken), it still works, but it is cluttered. Two games that are really in the same position look "different" only because of irrelevant details.
- The **perfect photo** shows exactly the things that affect how the rest of the game can go, and nothing else.

A DP state is that perfect photo. It records the **minimum information needed to describe the problem that is still left**.

```
   decisions already made            decisions still to make
  ───────────────────────────►│◄────────────────────────────────
                              │
                       the STATE lives here:
            "what do I need to know to finish the job optimally?"
```

The past matters **only** through what it left behind. If you stand on step 3 of a staircase, it does not matter whether you got there by 1+1+1 or 1+2 or 2+1. The remaining climb is the same. So "how I got here" is *not* part of the state; "where I am" is.

---

## 2. Problem statement in our own words

This is a concept topic, so instead of one problem we use three small ones. All three are solved with memoization in the `solution/` folder.

1. **Climbing Stairs** ([LeetCode 70](https://leetcode.com/problems/climbing-stairs/)). A staircase has `n` steps. Each move climbs 1 or 2 steps. Count the different sequences of moves that end exactly on step `n`.
2. **Grid paths** (the idea behind [LeetCode 62, Unique Paths](https://leetcode.com/problems/unique-paths/)). A robot stands in the top-left cell of a grid with `rows` rows and `cols` columns. Each move goes one cell right or one cell down. Count the different routes to the bottom-right cell.
3. **0/1 Knapsack** (taught fully in [module 03](../../03-subsequence-and-knapsack-dp/02-zero-one-knapsack/)). You have items, each with a weight and a value, and a bag that holds at most `W` weight. Each item can be taken at most once. Find the largest total value that fits.

For each problem our job here is **only to design the state**. The transitions and base cases get a full treatment in [topic 10](../10-state-transitions-and-base-cases/).

---

## 3. Examples

| Problem | Input | Output | Why |
|---|---|---|---|
| Climbing Stairs | `n = 5` | `8` | 1+1+1+1+1, 2+1+1+1 (4 orders), 2+2+1 (3 orders) → 1 + 4 + 3 = 8 |
| Climbing Stairs | `n = 1` | `1` | Only a single 1-step |
| Grid paths | `3 × 3` | `6` | Any route has 2 rights and 2 downs; there are C(4,2) = 6 orders |
| Grid paths | `3 × 7` | `28` | 2 downs and 6 rights: C(8,2) = 28 |
| Grid paths | `1 × 1` | `1` | Already at the target: the empty route |
| Knapsack | weights `[1,3,4]`, values `[15,20,30]`, `W = 4` | `35` | Take items 0 and 1: weight 1 + 3 = 4, value 15 + 20 = 35 |

Check the knapsack answer by listing every subset that fits in weight 4:

```
{}        weight 0  value 0
{0}       weight 1  value 15
{1}       weight 3  value 20
{2}       weight 4  value 30
{0,1}     weight 4  value 35   <-- best
{0,2}     weight 5  too heavy
{1,2}     weight 7  too heavy
{0,1,2}   weight 8  too heavy
```

---

## 4. Deriving the solution — the 7 questions

Our main running example for this lesson is **grid paths on a 3 × 3 grid** (answer `6`). It is the smallest example that needs a 2D state, and every cell can be filled by hand. We return to stairs and knapsack in later sections.

Cells are named `(r, c)`: row `r` counted from the top, column `c` from the left, both starting at 0. The robot starts at `(0, 0)` and the target is `(2, 2)`.

```
        c=0     c=1     c=2
      +-------+-------+-------+
 r=0  | START |       |       |
      +-------+-------+-------+
 r=1  |       |       |       |
      +-------+-------+-------+
 r=2  |       |       |  END  |
      +-------+-------+-------+
```

### Q1. What is changing?

Freeze the robot halfway through some route. What could be different between two frozen moments?

- **Its row** changes when it moves down.
- **Its column** changes when it moves right.
- The grid size (3 × 3) never changes. It is a **fixed input**.
- The number of moves made so far changes too, but it always equals `r + c`, so it is a **derived value** that adds no new information.

So two things change: `r` and `c`.

### Q2. What does the state represent?

> **`paths(r, c)` = the number of different right/down routes from cell `(r, c)` to the bottom-right cell `(rows-1, cols-1)`.**

The answer to the original question is `paths(0, 0)`.

Notice how much the sentence says: what is counted (routes), which moves are allowed (right/down), where the route starts (`(r, c)`) and where it ends (the target). If you cannot write a sentence like this, you do not have a state yet.

### Q3. What are the choices?

From `(r, c)` the robot can move **right** to `(r, c+1)` or **down** to `(r+1, c)`.

### Q4. What is the recurrence?

Every route from `(r, c)` starts with exactly one of the two moves, and routes that start differently are different routes. So we **add**:

```
paths(r, c) = paths(r, c+1) + paths(r+1, c)
              ^^^^^^^^^^^^^   ^^^^^^^^^^^^^
              routes that      routes that
              start "right"    start "down"
```

### Q5. What are the base cases?

- `paths(rows-1, cols-1) = 1`. Standing on the target, there is exactly one route: do nothing.
- If `r >= rows` or `c >= cols`, the robot has walked off the grid. That is not a route, so return `0`.

### Q6. What is the order of computation?

`paths(r, c)` reads the cell to its **right** and the cell **below** it. So in a table, those must be filled first: go from the bottom row up, and inside each row from right to left. [Topic 11](../11-transition-dependencies-and-computation-order/) covers this in depth.

### Q7. Can the state space be optimized?

Each row reads only itself and the row below it, so two rows (even one row) are enough. [Topic 15](../15-space-optimization-basics/) shows how. For this lesson the important point is different: **the state space has `rows × cols` states**, because `r` ranges over `rows` values and `c` over `cols` values. That number comes straight from the state design.

---

## 5. Brute force: recursion

Translate the state sentence and recurrence directly into code (`GridState.java` without the cache):

```java
static long paths(int r, int c) {
    if (r >= rows || c >= cols) return 0;            // off the grid
    if (r == rows - 1 && c == cols - 1) return 1;    // at the target
    return paths(r, c + 1) + paths(r + 1, c);        // right + down
}
```

Recursion tree for the 3 × 3 grid (off-grid calls that return 0 are left out to keep it readable):

```
                              (0,0)
                    ┌───────────┴───────────┐
                  (0,1)                   (1,0)
               ┌────┴────┐             ┌────┴────┐
             (0,2)     (1,1)*        (1,1)*     (2,0)
               │      ┌──┴──┐       ┌──┴──┐       │
             (1,2)  (1,2)  (2,1)  (1,2)  (2,1)  (2,1)
               │      │      │      │      │      │
             (2,2)  (2,2)  (2,2)  (2,2)  (2,2)  (2,2)
```

Six leaves reach `(2,2)`, so the answer is 6. The starred node `(1,1)` is solved **twice**, and `(1,2)` and `(2,1)` three times each. That repetition is exactly the overlapping-subproblems property from [topic 04](../04-overlapping-subproblems/).

The tree grows exponentially with the grid size. Every route of length `(rows-1) + (cols-1)` is explored separately, and there can be about `2^(rows+cols)` of them.

**The key observation:** the tree has many *nodes* but only `3 × 3 = 9` *distinct labels*. The labels are the states. Good state design keeps the number of distinct labels small.

---

## 6. Memoization (top-down)

Because the state is two integers, the cache is a 2D array: `memo[r][c]`.

```java
// memo[r][c] = number of right/down paths from (r, c) to the target; -1 = not computed
private static long[][] memo;

private static long paths(int r, int c) {
    if (r >= rows || c >= cols) return 0;
    if (r == rows - 1 && c == cols - 1) return 1;
    if (memo[r][c] != -1) return memo[r][c];
    memo[r][c] = paths(r, c + 1) + paths(r + 1, c);
    return memo[r][c];
}
```

**The shape of the cache comes from the state.** One parameter with `n+1` values → `new long[n+1]`. Two parameters with `rows` and `cols` values → `new long[rows][cols]`. This is why designing the state comes before writing the memo.

Dry run on 3 × 3, in the order the calls finish (H = cache hit):

| Step | Call | Reads | Result | Note |
|---|---|---|---|---|
| 1 | `paths(1,2)` | `(1,3)=0`, `(2,2)=1` | 1 | stored |
| 2 | `paths(0,2)` | `(0,3)=0`, `(1,2)=1` | 1 | stored |
| 3 | `paths(2,1)` | `(2,2)=1`, `(3,1)=0` | 1 | stored |
| 4 | `paths(1,1)` | `(1,2)=1`, `(2,1)=1` | 2 | `(1,2)` **H** (stored in step 1) |
| 5 | `paths(0,1)` | `(0,2)=1`, `(1,1)=2` | 3 | stored |
| 6 | `paths(2,0)` | `(2,1)=1`, `(3,0)=0` | 1 | **H** |
| 7 | `paths(1,0)` | `(1,1)=2`, `(2,0)=1` | 3 | `(1,1)` **H** |
| 8 | `paths(0,0)` | `(0,1)=3`, `(1,0)=3` | **6** | answer |

(Step 1 is reached from `paths(0,2)`, which is reached from `paths(0,1)`; the table lists calls in the order they *finish*.)

Each of the 8 non-target cells is computed once. The second time `(1,1)` is needed, it is a single array read.

---

## 7. Tabulation (bottom-up)

The same state becomes a table `dp[r][c]` with the same sentence: "routes from `(r, c)` to the target". Fill it bottom row first, right to left (Q6).

```
Start: target cell = 1                Fill row 2 (only "right" exists)
     c=0  c=1  c=2                         c=0  c=1  c=2
r=0   .    .    .                    r=0    .    .    .
r=1   .    .    .                    r=1    .    .    .
r=2   .    .    1                    r=2    1    1    1

Fill row 1 (right + down)             Fill row 0
     c=0  c=1  c=2                         c=0  c=1  c=2
r=0   .    .    .                    r=0    6    3    1
r=1   3    2    1                    r=1    3    2    1
r=2   1    1    1                    r=2    1    1    1

dp[1][2] = 0 + dp[2][2] = 1          dp[0][2] = 0 + dp[1][2] = 1
dp[1][1] = dp[1][2] + dp[2][1] = 2   dp[0][1] = dp[0][2] + dp[1][1] = 3
dp[1][0] = dp[1][1] + dp[2][0] = 3   dp[0][0] = dp[0][1] + dp[1][0] = 6
```

The answer sits in `dp[0][0] = 6`. The table has exactly `rows × cols = 9` cells: the size of the state space.

---

## 8. Space optimization

Row `r` only reads row `r` and row `r+1`, so only two rows (or one row updated in place) need to be kept. This lesson does not implement it; see [topic 15](../15-space-optimization-basics/). The point to remember now: **you can only see this saving after you have written the state and the recurrence**, because the recurrence tells you which states each state reads.

---

## 9. Dry run on a full example — three states side by side

Now let us apply the same design process to all three examples and compare.

### 9.1 Climbing Stairs: a 1D state

**What changes?** Only the step we are on. The number of moves made so far is irrelevant (many different move counts reach the same step, and the future does not care). The value `n` is a fixed input.

> **`ways(i)` = the number of distinct sequences of 1-steps and 2-steps that go from the ground (step 0) to step `i`.**

The answer is `ways(n)`. The state space is `i = 0, 1, …, n`: `n + 1` states, a 1D array.

```
step i :  0   1   2   3   4   5
ways(i):  1   1   2   3   5   8
```

(How we got these numbers, and why `ways(0) = 1`, is the subject of [topic 10](../10-state-transitions-and-base-cases/).)

> **Direction is a design choice.** In the grid we defined the state as "routes *from* `(r, c)` *to the end*" (a **suffix** state). For stairs we defined it as "ways *from the start to* step `i`" (a **prefix** state). Both styles are correct and common. What matters is that the sentence is precise and the base cases match it. Stairs could equally be "ways from step `i` to the top", with base case "at the top: 1 way".

### 9.2 Grid paths: a 2D state

Already derived in Section 4. The state needs two numbers because position in a grid needs two numbers. A single number such as "how many moves made" does not identify a cell: after 2 moves the robot could be at `(0,2)`, `(1,1)` or `(2,0)`, which have different answers (1, 2 and 1).

```
moves made = 2  ──►  (0,2) → 1 route left
                     (1,1) → 2 routes left
                     (2,0) → 1 route left
```

Same "moves made", different answers. So "moves made" alone is **too little information**.

### 9.3 0/1 Knapsack: a 2D state where one dimension is not a position

Walk through the items in order, deciding take or skip for each. Freeze in the middle. What do you need to know to finish optimally?

- **Which item is next** (`i`). Items before `i` are decided; items from `i` on are still open.
- **How much room is left** (`remaining`). This decides which of the remaining items can still fit.
- Which exact items were taken? **Not needed.** Only their total weight matters for the future, and that is captured by `remaining`. The value collected so far is not needed either: we return the *best future value* and add it to the past value outside.

> **`best(i, remaining)` = the largest total value we can collect from items `i, i+1, …, n-1` using at most `remaining` units of capacity.**

The answer is `best(0, W)`. The state space is `i ∈ [0, n]` × `remaining ∈ [0, W]`: `(n+1) × (W+1)` states.

Filled table for weights `[1,3,4]`, values `[15,20,30]`, `W = 4`. Row `i = 3` means "no items left", so it is all zeros.

```
               remaining →
 i              0    1    2    3    4
 0 (w1,v15)     0   15   15   20   35   <- answer best(0,4) = 35
 1 (w3,v20)     0    0    0   20   30
 2 (w4,v30)     0    0    0    0   30
 3 (none)       0    0    0    0    0
```

Two sample cells, worked out:

```
best(1, 4) = max( skip item 1: best(2, 4)          = 30,
                  take item 1: 20 + best(2, 4-3=1) = 20 + 0 = 20 )  = 30
best(0, 4) = max( skip item 0: best(1, 4)          = 30,
                  take item 0: 15 + best(1, 4-1=3) = 15 + 20 = 35 ) = 35
```

### 9.4 What goes wrong with too few parameters

Suppose we forget `remaining` and cache by `i` alone. `KnapsackState.java` contains exactly this bug in `bestTooFewParameters`:

```java
private static long[] badMemo;                   // badMemo[i] -- but for WHICH capacity?

private static long bestTooFewParameters(int i, int remaining) {
    if (i == weights.length) return 0;
    if (badMemo[i] != -1) return badMemo[i];     // BUG: may belong to another capacity
    ...
}
```

Trace with `W = 4`. The "skip" branch is explored first, so slots get filled with answers for **large** capacities, then reused for **small** ones:

```
bad(0, rem 4)
├─ skip item 0 → bad(1, rem 4)
│   ├─ skip item 1 → bad(2, rem 4)
│   │    ├─ skip → bad(3) = 0
│   │    └─ take item 2 (w4): 30 + bad(3) = 30
│   │    badMemo[2] = 30          (true for rem 4 only!)
│   └─ take item 1 (w3) → 20 + bad(2, rem 1)
│        bad(2, rem 1) hits the cache → 30   ✗ (true answer is 0: item 2 does not fit in 1)
│        = 50
│   badMemo[1] = max(30, 50) = 50
└─ take item 0 (w1) → 15 + bad(1, rem 3)
     bad(1, rem 3) hits the cache → 50       ✗ (true answer is 20)
     = 65
answer = max(50, 65) = 65                    ✗ (correct answer is 35)
```

65 is the value of **all three items** (weight 8) in a bag of size 4. The program does not crash or warn; it just silently returns an impossible answer. That is why a missing state parameter is one of the most dangerous DP bugs.

> **Test for "too few parameters":** can two different situations end up with the same state but different true answers? Here `(i = 2, remaining = 4)` and `(i = 2, remaining = 1)` have answers 30 and 0. If your state cannot tell them apart, it is missing a parameter.

### 9.5 What happens with too many parameters

Now the opposite mistake. Suppose for stairs we use `ways(i, movesMade)`. It still gives correct answers, because every extra parameter just splits a state into copies that all have the same answer. But:

- The table becomes `(n+1) × (n+1)` instead of `n+1`. For `n = 10 000` that is 100 million cells instead of 10 001.
- Memoization finds fewer cache hits, because `ways(3, 2)` and `ways(3, 3)` are cached separately even though the remaining climb from step 3 is the same.

Another example: a grid state `paths(r, c, movesMade)`. Since `movesMade = r + c` always, the third parameter is **derived**. It adds no information, only size.

> **Test for "too many parameters":** for each parameter, ask "if I change only this value, can the answer change?" If not (because it is fixed, irrelevant to the future, or derived from the others), remove it.

### 9.6 Summary table

| Problem | Changing quantities | Kept in state | Left out (and why) | State space size |
|---|---|---|---|---|
| Stairs | current step, moves made | `i` | moves made: does not affect the future | `n + 1` |
| Grid paths | row, column, moves made | `r, c` | moves made: equals `r + c` (derived) | `rows × cols` |
| Knapsack | next item, room left, items taken, value so far | `i, remaining` | items taken: only their weight matters, captured by `remaining`; value so far: added outside | `(n+1) × (W+1)` |

---

## 10. Complexity analysis

The number of states is decided entirely by the state design, so complexity analysis starts here. (Full method in [topic 12](../12-time-and-space-complexity-in-dp/).)

| Example | States | Work per state | Time | Space |
|---|---|---|---|---|
| Stairs `ways(i)` | `n + 1` | O(1): two reads and an add | **O(n)** | O(n) cache + O(n) recursion stack |
| Grid `paths(r, c)` | `rows × cols` | O(1): two reads and an add | **O(rows · cols)** | O(rows · cols) cache + O(rows + cols) stack (each call moves one cell closer) |
| Knapsack `best(i, rem)` | `(n+1)(W+1)` | O(1): two reads and a max | **O(n · W)** | O(n · W) cache + O(n) stack (each call moves to the next item) |
| Brute force grid (no cache) | — | — | about O(2^(rows+cols)) | O(rows + cols) stack |

A redundant parameter multiplies the state count by its range. A missing parameter does not make the code faster in any useful way; it makes it wrong.

---

## 11. Why this approach works

Memoization and tabulation both rely on one promise: **"the answer for a state depends only on the state."** If that is true, computing it once and reusing it is safe.

- A state with all the necessary parameters keeps that promise. Any two situations with the same `(i, remaining)` truly have the same best future, so sharing the cached value is correct.
- A state with a missing parameter breaks the promise. Two situations share a label but have different answers, and the cache hands one of them the wrong value (Section 9.4).
- A state with extra parameters keeps the promise but makes more labels than needed, so it is correct but slower.

That is the whole theory of state design: **include everything the future depends on; include nothing else.**

---

## 12. Common mistakes

1. **Leaving out a resource that limits future choices** (capacity, remaining budget, number of transactions left). Symptom: answers that are too good to be true, like 65 in the knapsack trace.
2. **Putting the whole history in the state** (a list of chosen items, the full path). The state space explodes. Ask what the history *summarizes to*; usually a single number.
3. **Including a fixed input** such as the `cost` array or `n` as a parameter. It never changes, so it is not part of the state. Store it in a field or pass it along unchanged.
4. **Vague state sentences** like "`dp[i]` = the answer for `i`". This hides off-by-one questions: is it ways to reach step `i`, or ways from step `i` to the top? Is `i` inclusive? Write the full sentence.
5. **Mixing prefix and suffix meanings** in the same solution: defining `dp[i]` as "from the start to `i`" but writing base cases for "from `i` to the end".
6. **Sizing the cache from the input length instead of the parameter range.** For knapsack, the second dimension is `W + 1`, not `n`.

---

## 13. When to use this technique / how to recognize similar problems

Use this design process for every DP problem, before any code:

```
1. Imagine solving the problem one decision at a time.
2. Freeze halfway through.
3. List everything that differs between two frozen moments.
4. Cross out what is fixed, what does not affect the future, and what is derived.
5. What is left are your state parameters.
6. Write the state sentence. Name the answer cell (e.g. best(0, W)).
7. Test: same state => same answer, always?
```

Common parameter shapes you will meet in this course:

| Shape | Typical meaning | Example |
|---|---|---|
| `i` | position in an array or string | stairs, house robber, decode ways |
| `(r, c)` | cell in a grid | unique paths, minimum path sum |
| `(i, j)` | position in two strings | LCS, edit distance |
| `(i, remaining)` | item index + resource left | knapsack, coin change |
| `(l, r)` | a sub-interval | interval DP, burst balloons |
| `(i, flag)` | position + small status | stock problems (holding or not) |
| `(mask)` | set of used elements | bitmask DP, TSP |

---

## 14. Summary and revision notes

- A **state** is the minimum information that describes the remaining subproblem.
- Find parameters by listing what **changes**, then removing what is **fixed**, **irrelevant to the future**, or **derived**.
- **Too few** parameters → different subproblems share a cache slot → silently wrong answers.
- **Too many** parameters → correct, but more states and fewer cache hits → slower.
- The number of parameters is the number of table dimensions; their ranges are the table sizes.
- Always write the state as an English sentence and point to the cell that holds the final answer.

```
+-------------------------------------------------------------------------+
|  Stairs:    ways(i)            = # ways from step 0 to step i     (1D)  |
|  Grid:      paths(r, c)        = # right/down routes (r,c) -> end (2D)  |
|  Knapsack:  best(i, remaining) = max value from items i.. with           |
|                                  at most `remaining` capacity     (2D)  |
|  Rule:      include everything the future depends on, nothing else.     |
+-------------------------------------------------------------------------+
```

---

## 15. Check your understanding

**Exercise 1.** In [Min Cost Climbing Stairs](https://leetcode.com/problems/min-cost-climbing-stairs/) you pay `cost[i]` when you leave step `i`, and climb 1 or 2 steps. Should the state be `f(i)` or `f(i, cost)`? Write the state sentence.

<details><summary>Answer</summary>

`f(i)`. The `cost` array never changes during the recursion, so it is a fixed input, not a state parameter. One possible sentence: "`minCost(i)` = the cheapest total fee to arrive at position `i`, where position `n` is the top." Topic 10 builds the full recurrence for exactly this state.

</details>

**Exercise 2.** A friend writes grid paths as `paths(movesMade)` to save memory. For the 3 × 3 grid, give two cells that share `movesMade` but have different true answers.

<details><summary>Answer</summary>

After 2 moves, `(1,1)` has 2 routes left but `(0,2)` and `(2,0)` each have 1. All three have `movesMade = 2`, so the one-parameter state cannot tell them apart. The state is missing information, and a cache keyed by `movesMade` would return wrong answers.

</details>

**Exercise 3.** A robot moves right or down on a grid, but it may make **at most `k` down moves** in total. What is the state now? How many states are there for a `rows × cols` grid?

<details><summary>Answer</summary>

Position alone is not enough. Two robots at the same cell may have used different numbers of down moves. But the number of down moves used so far is exactly `r` (every down move increases the row by 1), so it is **derived** from the position. The state stays `paths(r, c)`, with `rows × cols` states. (In fact every complete route uses exactly `rows - 1` down moves, so the answer is simply 0 when `k < rows - 1` and unchanged otherwise. The lesson is that "down moves used" did not need its own parameter.)

(If the limit were on something *not* determined by the position, such as "at most `k` direction changes", you would need an extra parameter such as `(r, c, changesUsed, lastDirection)`.)

</details>

**Exercise 4.** For knapsack with `n = 100` items and `W = 10 000`, how many cells does the `best(i, remaining)` table have? What if someone uses `best(i, remaining, valueSoFar)` where values can sum to 50 000?

<details><summary>Answer</summary>

`(100 + 1) × (10 000 + 1) ≈ 1.01 million` cells: fine. Adding `valueSoFar` multiplies that by about 50 001, giving roughly 50 billion cells: far too many. `valueSoFar` is not needed, because `best` returns the best *future* value and the caller adds the past value itself.

</details>

---

⬅ [README](README.md) · ➡ Next topic: [10 · State transitions and base cases](../10-state-transitions-and-base-cases/)
