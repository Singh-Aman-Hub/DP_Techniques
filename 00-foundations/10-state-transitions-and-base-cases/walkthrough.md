# State Transitions and Base Cases — Walkthrough

> Code for this lesson: [`ClimbingStairsRecurrence.java`](solution/ClimbingStairsRecurrence.java) · [`MinCostClimbingStairsRecurrence.java`](solution/MinCostClimbingStairsRecurrence.java)

A DP solution has three parts:

```
   STATE            what subproblem am I talking about?      (topic 09)
   TRANSITIONS      how is its answer built from smaller ones?  (this topic)
   BASE CASES       where does the building stop?               (this topic)
```

You already know the state for stairs from [topic 09](../09-state-representation/). Now we write the rule that computes it, and the floor that the rule stands on.

---

## 1. Intuition

Think of how you would answer "how many ways can I get to the 5th floor?" if the building has stairs that let you go up one or two floors at a time.

You do not need to imagine every journey from the ground. Just ask: **"what was my very last move?"**

- Either I came up **one** floor, from floor 4,
- or I came up **two** floors, from floor 3.

There is no other possibility. So "ways to reach floor 5" = "ways to reach floor 4" + "ways to reach floor 3". Each of those is the same question with a smaller number. That rule is a **transition**: it moves from a big question to smaller questions, one for each possible last choice.

But you cannot keep shrinking forever. At some point the question is so small that you just *know* the answer: "how many ways to be on the ground floor when you start there? One: do nothing." That is a **base case**: the floor that the whole tower of reasoning stands on.

```
   ways(5)  ── "last move was +1" ──►  ways(4)
      │
      └──── "last move was +2" ──►  ways(3)
                                      ...
                                    ways(1) = 1   ◄── base cases: known directly
                                    ways(0) = 1   ◄──
```

---

## 2. Problem statement in our own words

**Problem A: Climbing Stairs** ([LeetCode 70](https://leetcode.com/problems/climbing-stairs/)). A staircase has `n` steps. Each move climbs either 1 or 2 steps. Count the different sequences of moves that land exactly on step `n`.

**Problem B: Min Cost Climbing Stairs** ([LeetCode 746](https://leetcode.com/problems/min-cost-climbing-stairs/)). You get an array `cost` of length `n` (at least 2). `cost[i]` is the fee you pay when you climb *off* step `i`; after paying you go up 1 or 2 steps. You may begin on step 0 or on step 1 for free. The "top" is the position just past the last step, position `n`. Find the smallest total fee to reach the top.

Both are taught in full later in [module 01](../../01-one-dimensional-dp/02-climbing-stairs/). Here we use them to study transitions and base cases only.

---

## 3. Examples

**Climbing Stairs**

| `n` | Answer | All sequences |
|---|---|---|
| 1 | 1 | `1` |
| 2 | 2 | `1+1`, `2` |
| 3 | 3 | `1+1+1`, `1+2`, `2+1` |
| 5 | 8 | `11111`, `2111`, `1211`, `1121`, `1112`, `221`, `212`, `122` |
| 10 | 89 | (too many to list) |

**Min Cost Climbing Stairs**

| `cost` | Answer | Cheapest plan |
|---|---|---|
| `[10, 15, 20]` | 15 | Start on step 1, pay 15, jump 2 → top. |
| `[1,100,1,1,1,100,1,1,100,1]` | 6 | Start on step 0 and leave only steps 0, 2, 4, 6, 7, 9 (six fees of 1); dry run in Section 9 |
| `[5, 7]` | 5 | Start on step 0, pay 5, jump 2 → top. |

Picture for `[10, 15, 20]`:

```
                                 TOP (position 3)
                       ┌────────┐
              ┌────────┤ step 2 │  cost 20
     ┌────────┤ step 1 │        │  cost 15
     │ step 0 │        │        │  cost 10
─────┴────────┴────────┴────────┴─────
  start free on step 0 or step 1
```

---

## 4. Deriving the solution — the 7 questions

Our main running example is **Climbing Stairs with `n = 5`** (answer `8`). Min Cost Climbing Stairs gets the same treatment in Section 9.

### Q1. What is changing?

Only the step we are on. The `n` itself is fixed.

### Q2. What does the state represent?

> **`ways(i)` = the number of distinct sequences of 1-steps and 2-steps that go from the ground (step 0) to step `i`.**

The final answer is `ways(n)`.

### Q3. What are the choices?

**Transitions are choices.** For each state, ask: "what decision was made at the boundary of this subproblem?" There are two natural ways to look at it:

- **Last decision** ("how did I arrive?"): fits *prefix* states like "from the start to `i`".
- **First decision** ("where do I go next?"): fits *suffix* states like "from `i` to the end".

Our state is a prefix state, so we ask about the last move onto step `i`:

| Choice | Came from | Possible when |
|---|---|---|
| Last move was a 1-step | step `i - 1` | `i - 1 >= 0` |
| Last move was a 2-step | step `i - 2` | `i - 2 >= 0` |

Two checks every list of choices must pass:

1. **Complete.** Every valid sequence ending at `i` uses one of these choices as its last move. (Yes: the only moves are 1 and 2.)
2. **For counting: disjoint.** No sequence is counted under two choices. (Yes: a sequence's last move cannot be both 1 and 2.)

### Q4. What is the recurrence?

**Pick the operator from the question:**

| The question asks… | Combine choices with | Neutral value for an impossible choice |
|---|---|---|
| "How many ways?" | `+` (sum) | `0` |
| "What is the minimum cost?" | `min` | `+∞` |
| "What is the maximum value?" | `max` | `-∞` |
| "Is it possible?" | `OR` | `false` |

Climbing Stairs asks "how many", so we add one term per choice:

```
ways(i) =  ways(i - 1)      +      ways(i - 2)
           ^^^^^^^^^^              ^^^^^^^^^^
           sequences whose         sequences whose
           last move is +1         last move is +2
```

Why `+` works: every sequence to step `i` is exactly "a sequence to step `i-1`, then +1" or "a sequence to step `i-2`, then +2". The two groups do not overlap and together cover everything. Counting a set split into non-overlapping parts = adding the part sizes.

> **Why disjointness matters only for `+`.** If a sequence were counted in both groups, `+` would count it twice. With `min` or `max`, seeing the same candidate twice does no harm: `min(5, 5) = 5`. So for optimization problems overlapping choices are fine, but for counting they are a bug.

### Q5. What are the base cases?

The recurrence reads `i - 1` and `i - 2`. If we keep applying it, the index goes 5 → 4 → 3 → 2 → 1 → 0 → -1 → … forever. **Base cases exist to stop that.** We need answers for every state that the recurrence would otherwise push below zero. Applying the recurrence at `i = 1` would read `ways(-1)`, and at `i = 0` it would read `ways(-1)` and `ways(-2)`. So `i = 0` and `i = 1` must be base cases.

- **`ways(0) = 1`.** Read the state sentence: "number of sequences from step 0 to step 0". There is exactly one: the **empty** sequence (make no moves). This feels odd at first, but it is what makes `ways(2) = ways(1) + ways(0) = 1 + 1 = 2` correct (`1+1` and `2`). The `2` sequence is "the one way to stand on step 0, then +2".
- **`ways(1) = 1`.** Only the sequence `1`.

Check the base cases against the smallest real inputs by hand: `n = 1 → 1` ✓, `n = 2 → 2` ✓, `n = 3 → 3` ✓.

### Q6. What is the order of computation?

`ways(i)` needs `ways(i-1)` and `ways(i-2)`, both smaller indices. So a table fills **left to right**, from `i = 2` up to `i = n`. ([Topic 11](../11-transition-dependencies-and-computation-order/) studies this in general.)

### Q7. Can the state space be optimized?

Each state reads only the two states just before it, so two variables are enough instead of an array. [Topic 15](../15-space-optimization-basics/) builds this.

---

## 5. Brute force: recursion

The recurrence and base cases translate word for word:

```java
static long waysRecursive(int i) {
    if (i == 0) return 1;   // standing on the ground: one way (the empty plan)
    if (i == 1) return 1;   // only one way: a single 1-step
    return waysRecursive(i - 1) + waysRecursive(i - 2);
}
```

Recursion tree for `n = 5`. Leaves are base cases.

```
                              ways(5)
                 ┌───────────────┴───────────────┐
              ways(4)                         ways(3)
          ┌──────┴──────┐                 ┌──────┴──────┐
       ways(3)       ways(2)           ways(2)       ways(1)=1
      ┌───┴───┐      ┌──┴──┐           ┌──┴──┐
  ways(2)  ways(1)  w(1)  w(0)        w(1)  w(0)
  ┌──┴──┐     =1     =1    =1          =1    =1
 w(1) w(0)
  =1   =1
```

Count the leaves: 8 leaves, each worth 1, so `ways(5) = 8`. Each leaf corresponds to one sequence of moves.

The tree has 15 nodes for `n = 5`. Since each call branches into two, the number of calls grows roughly like `1.6^n` (the same growth as Fibonacci, see [topic 03](../03-recursion-trees-and-brute-force/)). `ways(3)` is solved twice and `ways(2)` three times.

---

## 6. Memoization (top-down)

Add a cache keyed by the state `i` ([topic 06](../06-memoization/)):

```java
// memo[i] = number of ways to reach step i; -1 means "not computed yet"
private static long waysMemoHelper(int i) {
    if (i == 0 || i == 1) return 1;          // base cases are checked BEFORE the cache
    if (memo[i] != -1) return memo[i];
    memo[i] = waysMemoHelper(i - 1) + waysMemoHelper(i - 2);
    return memo[i];
}
```

Dry run for `n = 5`:

| Call finishes | Reads | Stores | Cache hit? |
|---|---|---|---|
| `ways(2)` | `ways(1)=1` (base), `ways(0)=1` (base) | `memo[2] = 2` | — |
| `ways(3)` | `ways(2)=2`, `ways(1)=1` | `memo[3] = 3` | — |
| `ways(4)` | `ways(3)=3`, `ways(2)=2` | `memo[4] = 5` | `ways(2)` **hit** |
| `ways(5)` | `ways(4)=5`, `ways(3)=3` | `memo[5] = 8` | `ways(3)` **hit** |

Four computed states instead of a 15-node tree.

---

## 7. Tabulation (bottom-up)

**Base cases become the first cells of the table; the recurrence becomes the loop body.**

```java
long[] dp = new long[Math.max(n + 1, 2)];   // dp[i] = ways to reach step i
dp[0] = 1;                                   // base case
dp[1] = 1;                                   // base case
for (int i = 2; i <= n; i++) dp[i] = dp[i - 1] + dp[i - 2];
return dp[n];
```

Filling the table for `n = 5`:

```
after base cases:   i :  0   1   2   3   4   5
                  dp  :  1   1   .   .   .   .

i = 2: dp[2] = dp[1] + dp[0] = 1 + 1 = 2
                  dp  :  1   1   2   .   .   .
i = 3: dp[3] = dp[2] + dp[1] = 2 + 1 = 3
                  dp  :  1   1   2   3   .   .
i = 4: dp[4] = dp[3] + dp[2] = 3 + 2 = 5
                  dp  :  1   1   2   3   5   .
i = 5: dp[5] = dp[4] + dp[3] = 5 + 3 = 8
                  dp  :  1   1   2   3   5   8   <- answer
```

Note the array size `Math.max(n + 1, 2)`. With `n = 0` a plain `new long[n + 1]` has one cell, and `dp[1] = 1` would crash. Base cases must fit in the table even for the smallest input.

---

## 8. Space optimization

`dp[i]` reads only `dp[i-1]` and `dp[i-2]`, so two variables are enough and space drops from O(n) to O(1). The technique is the subject of [topic 15](../15-space-optimization-basics/); this lesson does not repeat it.

---

## 9. Dry run on a full example — Min Cost Climbing Stairs

Now a second problem, to see the **`min`** operator and trickier base cases. Code: [`MinCostClimbingStairsRecurrence.java`](solution/MinCostClimbingStairsRecurrence.java).

### 9.1 The 7 questions, briefly

| Question | Answer |
|---|---|
| Q1 What changes? | The position `i` we are trying to reach. `cost` is fixed input. |
| Q2 State | `minCost(i)` = the cheapest total fee to **arrive** at position `i` (position `n` is the top). Answer: `minCost(n)`. |
| Q3 Choices | The last move onto `i` came from `i-1` (paying `cost[i-1]` to leave it) or from `i-2` (paying `cost[i-2]`). |
| Q4 Recurrence | `minCost(i) = min( minCost(i-1) + cost[i-1],  minCost(i-2) + cost[i-2] )` |
| Q5 Base cases | `minCost(0) = 0`, `minCost(1) = 0`: we may start on either for free. |
| Q6 Order | Left to right, `i = 2 … n`. |
| Q7 Optimize | Only the previous two values are read → two variables. |

Each term explained:

```
minCost(i) = min(  minCost(i-1)  +  cost[i-1]   ,   minCost(i-2)  +  cost[i-2]  )
                   ^^^^^^^^^^^^     ^^^^^^^^^        ^^^^^^^^^^^^     ^^^^^^^^^
                   cheapest way     fee to leave     cheapest way     fee to leave
                   to stand on      step i-1         to stand on      step i-2
                   step i-1         (then +1)        step i-2         (then +2)
```

Why `min` and not `+`? The question is "what is the cheapest", not "how many". We pick the better of the two ways to arrive.

Why are the base cases 0 and not `cost[0]` and `cost[1]`? Read the state sentence again: "cheapest fee to **arrive** at position `i`". Arriving on step 0 or step 1 is free because we are allowed to start there. The fee for a step is paid when we *leave* it, and that is already inside the recurrence (`+ cost[i-1]`). If the base cases also charged it, we would pay twice.

### 9.2 Recursion tree for `cost = [10, 15, 20]` (`n = 3`)

```
                       minCost(3)
             ┌────────────┴────────────┐
       minCost(2) + 20           minCost(1) + 15
      ┌──────┴──────┐                 = 0 + 15 = 15
 minCost(1)+15  minCost(0)+10
   = 0+15=15      = 0+10=10
      min = 10
   10 + 20 = 30
                    min(30, 15) = 15
```

### 9.3 Table for `cost = [10, 15, 20]`

```
position i :    0     1     2     3 (top)
cost[i]    :   10    15    20     -

dp[0] = 0                                   (base: free start)
dp[1] = 0                                   (base: free start)
dp[2] = min(dp[1] + cost[1], dp[0] + cost[0]) = min(0+15, 0+10) = 10
dp[3] = min(dp[2] + cost[2], dp[1] + cost[1]) = min(10+20, 0+15) = 15   <- answer
```

### 9.4 Table for `cost = [1,100,1,1,1,100,1,1,100,1]` (`n = 10`)

| `i` | `dp[i-1] + cost[i-1]` | `dp[i-2] + cost[i-2]` | `dp[i]` |
|---|---|---|---|
| 0 | — | — | 0 (base) |
| 1 | — | — | 0 (base) |
| 2 | 0 + 100 = 100 | 0 + 1 = 1 | 1 |
| 3 | 1 + 1 = 2 | 0 + 100 = 100 | 2 |
| 4 | 2 + 1 = 3 | 1 + 1 = 2 | 2 |
| 5 | 2 + 1 = 3 | 2 + 1 = 3 | 3 |
| 6 | 3 + 100 = 103 | 2 + 1 = 3 | 3 |
| 7 | 3 + 1 = 4 | 3 + 100 = 103 | 4 |
| 8 | 4 + 1 = 5 | 3 + 1 = 4 | 4 |
| 9 | 4 + 100 = 104 | 4 + 1 = 5 | 5 |
| 10 (top) | 5 + 1 = 6 | 4 + 100 = 104 | **6** |

Which steps does the cheapest route use? Trace back from `dp[10]`, each time following the term that won the `min`: `dp[10]` came from `i = 9` (`5 + 1`), `dp[9]` from `i = 7` (`4 + 1`), `dp[7]` from `i = 6` (`3 + 1`), `dp[6]` from `i = 4` (`2 + 1`), `dp[4]` from `i = 2` (`1 + 1`), `dp[2]` from `i = 0` (`0 + 1`). So the route leaves steps **0, 2, 4, 6, 7, 9**: six fees of 1 each, total 6. Tracing back like this is the reliable way to read a route out of a table.

---

### 9.5 Base cases in depth: terminal states, invalid states and off-by-one traps

#### 9.5.1 Two kinds of "edge" states

When a recursion reaches the edge of the problem it lands in one of two kinds of state.

- A **terminal state** is a *valid* situation where nothing is left to decide. Its answer is a real value: "on the ground, there is 1 way", "at the target cell, there is 1 route", "no items left, the best value is 0".
- An **invalid state** is a situation that cannot happen: step `-1`, a cell off the grid, a negative remaining capacity. It must not contribute to the answer, so it returns the **neutral value** of the combining operator.

Here is Climbing Stairs written as a *suffix* state, `from(i)` = "ways to go from step `i` to step `n`". This version naturally has both kinds:

```java
static long from(int i) {
    if (i == n) return 1;    // TERMINAL: exactly on the top, one way (stop here)
    if (i > n)  return 0;    // INVALID: overshot the top, contributes nothing
    return from(i + 1) + from(i + 2);
}
```

```
from(3) with n = 4:
            from(3)
          ┌───┴───┐
      from(4)   from(5)
      = 1       = 0      <- overshooting with a 2-step from step 3 is not a valid way
  total = 1
```

#### 9.5.2 The neutral value depends on the operator

The invalid-state value must be something that **can never win** and **never changes the result**.

| Operator | Neutral value | Why |
|---|---|---|
| `+` | `0` | `x + 0 = x` |
| `min` | `+∞` (e.g. `Long.MAX_VALUE / 2`, or skip the choice) | `min(x, +∞) = x` |
| `max` | `-∞` (e.g. `Long.MIN_VALUE`, or skip the choice) | `max(x, -∞) = x` |
| `OR` | `false` | `x OR false = x` |

A classic bug is returning `0` for an invalid state in a **`min`** problem. Then the impossible option looks like the cheapest one (cost 0) and wins every comparison. Using `Long.MAX_VALUE` directly is risky too, because adding a cost to it overflows to a negative number. Either use a large but safe value like `Long.MAX_VALUE / 2`, or simply do not consider the choice when it is invalid (as `KnapsackState.java` in topic 09 does with "take only if it fits").

#### 9.5.3 Off-by-one traps, with the wrong answers they produce

These are all in the solution files or easy to reproduce. Each one *compiles and runs*, which is what makes them dangerous.

**Trap 1: `ways(0) = 0`** ("zero steps means zero ways").

```
correct:  i :  0  1  2  3  4  5          buggy:  i :  0  1  2  3  4  5
          ways 1  1  2  3  5  8                  ways 0  1  1  2  3  5
```

The whole sequence shifts by one, and `ways(5)` returns **5 instead of 8** (`waysBuggyBase` in `ClimbingStairsRecurrence.java`). The fix is to read the state sentence: "sequences from step 0 to step 0" — the empty sequence counts.

**Trap 2: charging for the starting step** in Min Cost Climbing Stairs: `dp[1] = cost[0]` ("I start on step 0, so reaching step 1 costs `cost[0]`").

```
dp[0] = 0, dp[1] = 10 (bug), dp[2] = min(10+15, 0+10) = 10, dp[3] = min(10+20, 10+15) = 25
```

The answer becomes **25 instead of 15** (`minCostBuggyBase` in `MinCostClimbingStairsRecurrence.java`). It forgot that starting on step 1 is free.

**Trap 3: returning the wrong cell.** The top in Min Cost Climbing Stairs is position `n`, not `n-1`. Returning `dp[n-1]` for `[10, 15, 20]` gives `dp[2] = 10`, which is the cost to stand on the last step, not to get past it.

**Trap 4: too few base cases.** With only `if (i == 1) return 1;`, the call `ways(2)` calls `ways(0)`, which calls `ways(-1)`, `ways(-2)`, … and never stops: a `StackOverflowError`. Rule: if the recurrence reaches back `k` steps (`i-1`, …, `i-k`), you need `k` base cases (or an invalid-state guard like `if (i < 0) return 0`).

**Trap 5: base cases that do not fit the table.** `new long[n + 1]` followed by `dp[1] = 1` crashes when `n = 0`. Size the table so every base-case cell exists, or return early for tiny inputs.

> **The habit that catches all five:** after writing the base cases, compute the answer **by hand** for the smallest two or three inputs (`n = 0, 1, 2, 3`) and compare with what the code returns.

---

## 10. Complexity analysis

| Approach | Time | Why | Space | Why |
|---|---|---|---|---|
| Recursion | O(2^n) (more precisely about O(1.62^n)) | each call makes two calls; the tree has Fibonacci-many nodes | O(n) | recursion depth: each call lowers `i` by at least 1 |
| Memoization | O(n) | `n + 1` states, each computed once with O(1) work | O(n) | cache of `n + 1` cells plus a stack up to `n` deep |
| Tabulation | O(n) | one loop over `i = 2 … n`, O(1) per step | O(n) | the `dp` array |

The same analysis holds for Min Cost Climbing Stairs: `n + 1` states, O(1) work each (one `min` of two sums).

General rule (developed in [topic 12](../12-time-and-space-complexity-in-dp/)): **time = number of states × number of choices per state.** Here 2 choices per state, so O(2n) = O(n).

---

## 11. Why this approach works

The argument is a chain, like dominoes:

1. **Base cases are correct** because we checked them directly against the state sentence (one empty sequence to stand on step 0; starting on step 0 or 1 is free).
2. **If the answers for all smaller states are correct, the recurrence gives the correct answer for the next state.** For counting: the choices are complete and disjoint, so the sum counts every sequence exactly once. For `min`: the choices are complete, and the cheapest route to `i` must arrive through one of them; the best way to reach that previous step is itself the cheapest (optimal substructure, [topic 05](../05-optimal-substructure/)).
3. So `dp[2]` is correct, therefore `dp[3]` is correct, therefore … `dp[n]` is correct.

This is mathematical induction. You do not need to write it formally, but you should be able to say both halves in words: "the base cases are right" and "each step is right if the earlier ones are".

---

## 12. Common mistakes

1. **Missing a choice.** If moves of 1, 2 *or 3* steps are allowed, forgetting the `ways(i-3)` term gives too small an answer. List choices from the problem statement, not from memory of a similar problem.
2. **Overlapping choices in a counting problem.** If two choices can produce the same sequence, `+` counts it twice.
3. **Wrong operator.** "Minimum number of moves" needs `min` (plus 1 for the move), not `+`.
4. **Forgetting the cost of the transition itself.** In Min Cost Climbing Stairs, writing `min(dp[i-1], dp[i-2])` without adding the fee.
5. **Base case that disagrees with the state sentence** (Traps 1 and 2 above).
6. **Invalid state returning 0 in a `min` problem**, so impossible moves look free.
7. **Using `Long.MAX_VALUE` as infinity and then adding to it**, which overflows to a negative number.
8. **Checking the cache before the base cases with an index that can be negative**, causing an `ArrayIndexOutOfBoundsException`. Handle invalid and base states first.

---

## 13. When to use this technique / how to recognize similar problems

Every DP problem needs a transition and base cases. To write them quickly:

```
1. Read the state sentence aloud.
2. Ask "what was the last (or next) decision?" and list every option.
3. For each option, write the smaller state it leads to and any cost/value it adds.
4. Pick the operator from the question:  how many → +,  min/max → min/max,  possible? → OR.
5. Find the states where the recurrence would read outside the valid range.
   Make them base cases (terminal: real value) or guards (invalid: neutral value).
6. Hand-check n = 0, 1, 2, 3.
```

---

## 14. Summary and revision notes

- A **transition** is one choice; it links a state to a smaller state.
- The **recurrence** combines all choices with the operator the question asks for: `+`, `min`, `max`, `OR`.
- For counting, choices must be **complete and disjoint**; for `min`/`max` they only need to be complete.
- **Base cases** stop the recursion and seed the table. Derive them from the state sentence, not from intuition.
- **Terminal** states return a real answer; **invalid** states return the neutral value (`0`, `+∞`, `-∞`, `false`).
- Always hand-check the smallest inputs.

```
+-----------------------------------------------------------------------------+
|  Climbing Stairs:  ways(i) = ways(i-1) + ways(i-2)                           |
|                    ways(0) = 1,  ways(1) = 1,      answer ways(n)            |
|                                                                             |
|  Min Cost Stairs:  minCost(i) = min(minCost(i-1) + cost[i-1],               |
|                                     minCost(i-2) + cost[i-2])               |
|                    minCost(0) = 0,  minCost(1) = 0,  answer minCost(n)       |
+-----------------------------------------------------------------------------+
```

---

## 15. Check your understanding

**Exercise 1.** You may climb 1, 2 **or 3** steps at a time. Write the recurrence and base cases for `ways(i)`, and compute `ways(4)`.

<details><summary>Answer</summary>

`ways(i) = ways(i-1) + ways(i-2) + ways(i-3)` with base cases `ways(0) = 1`, `ways(1) = 1`, `ways(2) = 2` (three base cases, because the recurrence reaches back three steps). Alternatively keep only `ways(0) = 1` and add the guard `ways(negative) = 0`.

`ways(3) = 2 + 1 + 1 = 4` (`111`, `12`, `21`, `3`), `ways(4) = 4 + 2 + 1 = 7`.

</details>

**Exercise 2.** Some steps are **broken** and cannot be stood on. You want to know only *whether* you can reach step `n` with 1- and 2-steps. Which operator do you use, and what do broken steps return?

<details><summary>Answer</summary>

The question is yes/no, so combine with `OR`: `canReach(i) = canReach(i-1) OR canReach(i-2)`. A broken step is an invalid state, so `canReach(broken) = false` (the neutral value for `OR`). Base case: `canReach(0) = true`, and `canReach(negative) = false`.

</details>

**Exercise 3.** In Min Cost Climbing Stairs a classmate writes the suffix version `f(i)` = "cheapest fee to go from step `i` to the top", with `f(n) = 0` and `f(n+1) = 0`, and `f(i) = cost[i] + min(f(i+1), f(i+2))`. Is `f(n+1) = 0` a terminal or an invalid state? Is the code still correct? What is the final answer in terms of `f`?

<details><summary>Answer</summary>

Position `n+1` means "jumped 2 from the last step `n-1`", which also lands past the top. In this problem, overshooting the top is allowed (the top is "anywhere past the last step"), so `n+1` is a **terminal** state with real answer 0, and the code is correct. The final answer is `min(f(0), f(1))`, because we may start on step 0 or step 1. (Contrast Climbing Stairs, where overshooting is **invalid** and returns 0 as the neutral value for `+`.)

</details>

**Exercise 4.** A variant of Min Cost Climbing Stairs says you **must** start on step 0 (step 1 is no longer a free start). Which base cases change? What is the answer for `cost = [10, 15, 20]`?

<details><summary>Answer</summary>

Only `minCost(1)` changes. The only way to arrive at step 1 is now a 1-step from step 0, paying `cost[0]`, so `minCost(0) = 0` and `minCost(1) = cost[0]`. The recurrence is unchanged. For `[10, 15, 20]`: `dp = [0, 10, 10, 25]`, answer **25**.

Notice this is exactly "Trap 2" from Section 9.5.3. The same line of code is a bug in one problem and correct in another. Base cases come from the problem statement and the state sentence, never from habit.

</details>

---

⬅ [README](README.md) · ➡ Next topic: [11 · Transition dependencies and computation order](../11-transition-dependencies-and-computation-order/)
