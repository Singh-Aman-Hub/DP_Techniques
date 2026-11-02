# Converting Recursion to Memoization — Walkthrough

> Worked examples: **Climbing Stairs** with `n = 5` (answer `8`) and **grid paths** in a 3 × 3 grid (answer `6`).
> Extra: memoizing a parameter that can be **negative** (Target Sum style, `[1,1,1,1,1]`, target `3`, answer `5`).
> Code: [`ClimbingStairsBruteForce.java`](solution/ClimbingStairsBruteForce.java) → [`ClimbingStairsMemoization.java`](solution/ClimbingStairsMemoization.java), [`GridPathsBruteForce.java`](solution/GridPathsBruteForce.java) → [`GridPathsMemoization.java`](solution/GridPathsMemoization.java), [`OffsetIndexMemoization.java`](solution/OffsetIndexMemoization.java).

---

## 1. Intuition

A student is doing a worksheet of sums. Question 7 needs the answer to question 3. So does question 9, and question 12. A careless student works out question 3 from scratch each time. A smart student writes each answer in the margin the first time and, from then on, just looks it up.

Memoization is "writing answers in the margin". The recipe in this topic tells you exactly:

- **how big the margin must be** (one slot for every possible question),
- **what to write in an empty slot** (a marker meaning "not done yet"),
- **when to look** (at the start of each question),
- **when to write** (just before handing in the answer).

The student's method for solving a question does not change at all. That is the key promise of this topic: **you do not change the recursion's logic, you only wrap it.**

---

## 2. Problem statement in our own words

Given a recursive function that correctly solves a problem but is slow because it solves the same subproblems many times, produce a memoized version that gives the same answers and computes each subproblem only once.

We use two problems:

- **Climbing Stairs** ([LeetCode 70](https://leetcode.com/problems/climbing-stairs/)): count the ways to climb `n` steps when each move is 1 or 2 steps. Full lesson: [01-one-dimensional-dp/02-climbing-stairs](../../01-one-dimensional-dp/02-climbing-stairs/).
- **Grid paths**, in the style of [Unique Paths (LeetCode 62)](https://leetcode.com/problems/unique-paths/): count the routes from the top-left to the bottom-right of a grid with `rows` rows and `cols` columns, moving only right or down. Full lesson: [02-two-dimensional-and-grid-dp/02-unique-paths](../../02-two-dimensional-and-grid-dp/02-unique-paths/).

---

## 3. Examples

| Problem | Input | Answer | Why |
|---------|-------|--------|-----|
| Climbing Stairs | `n = 5` | 8 | e.g. 1+1+1+1+1, 2+2+1, 1+2+2, ... (8 orderings in total) |
| Climbing Stairs | `n = 1` | 1 | only "1" |
| Climbing Stairs | `n = 10` | 89 | |
| Grid paths | 3 × 3 | 6 | arrangements of 2 rights and 2 downs: RRDD, RDRD, RDDR, DRRD, DRDR, DDRR |
| Grid paths | 2 × 3 | 3 | RRD, RDR, DRR |
| Grid paths | 1 × 1 | 1 | already at the target: the empty route |
| Grid paths | 3 × 7 | 28 | |

---

## 4. Deriving the solution — the 7 questions

We answer them for **Climbing Stairs**; the grid answers follow in brackets. Questions 1 and 2 matter most in this topic, because **the cache is built directly from them**.

### Q1. What is changing?

The step we are trying to reach, `i`. *(Grid: the cell `(r, c)`.)* These are exactly the **changing parameters** of step 1 of the recipe.

### Q2. What does the state represent?

`ways(i)` = **the number of distinct ways to reach step `i` from step 0, using moves of 1 or 2 steps.** The range is `0 ≤ i ≤ n`, so there are `n + 1` states. *(Grid: `paths(r, c)` = the number of right/down routes from `(0, 0)` to `(r, c)`; `0 ≤ r < rows`, `0 ≤ c < cols`, so `rows × cols` states.)*

### Q3. What are the choices?

The last move into step `i` was a 1-step (from `i-1`) or a 2-step (from `i-2`). *(Grid: the last move was "down" from `(r-1, c)` or "right" from `(r, c-1)`.)*

### Q4. What is the recurrence?

The two groups of plans do not overlap (their last move differs), so we add them:

```
ways(i)    = ways(i-1) + ways(i-2)
paths(r,c) = paths(r-1,c) + paths(r,c-1)
```

### Q5. What are the base cases?

`ways(0) = 1` (the empty plan) and `ways(1) = 1` (a single 1-step). *(Grid: `paths(r, c) = 1` when `r == 0` or `c == 0`: a straight line.)* See [10 · State Transitions and Base Cases](../10-state-transitions-and-base-cases/) for why `ways(0)` is 1, not 0.

### Q6. What is the order of computation?

For memoization, **you do not choose it**: the recursion computes whatever it needs, when it needs it (see [11 · Transition Dependencies and Computation Order](../11-transition-dependencies-and-computation-order/)). This is one reason the conversion is so mechanical.

### Q7. Can the state space be optimized?

Not while it is a memoized recursion: the recursion may come back to any cell, so the whole cache must stay. After converting to a table in [14](../14-converting-memoization-to-tabulation/), [15 · Space Optimization Basics](../15-space-optimization-basics/) reduces it to two variables (stairs) or one row (grid).

---

## 5. Brute force: recursion

### Climbing Stairs — "before"

```java
static long ways(int n) {
    if (n <= 1) return 1;
    return ways(n - 1) + ways(n - 2);
}
```

Recursion tree for `n = 5`. Repeated subtrees are marked:

```
                         ways(5)
                 /                     \
            ways(4)                   ways(3)  ◄── repeat
           /       \                 /      \
      ways(3)     ways(2) ◄──    ways(2) ◄── ways(1)
      /     \      /    \         /    \
  ways(2) ways(1) w(1) w(0)     w(1)  w(0)
   /   \
 w(1) w(0)
```

`ways(3)` is computed twice, `ways(2)` three times. In total 15 calls for 8 ways. For `n = 45` it is about 3.7 billion calls ([topic 12](../12-time-and-space-complexity-in-dp/) explains the count `2 · ways(n) − 1`).

### Grid paths — "before"

```java
static long paths(int r, int c) {
    if (r == 0 || c == 0) return 1;
    return paths(r - 1, c) + paths(r, c - 1);
}
// answer: paths(rows - 1, cols - 1)
```

Tree for the 3 × 3 grid (target `(2, 2)`):

```
                   paths(2,2)
              /                 \
        paths(1,2)            paths(2,1)
        /       \             /        \
  paths(0,2)  paths(1,1)  paths(1,1)  paths(2,0)
     =1        /     \     ◄── repeat      =1
         paths(0,1) paths(1,0)
            =1         =1
```

`paths(1,1)` is solved twice. In a 10 × 10 grid there are 97 239 calls for 100 cells.

Both are **correct** but **slow** for the same reason: overlapping subproblems ([topic 04](../04-overlapping-subproblems/)). That is precisely the situation the recipe is for.

---

## 6. Memoization (top-down) — the recipe

### Step 0 (before the recipe): is the function pure?

Memoization says "same parameters → same answer, so reuse it". That is only true if the function's answer depends **only on its parameters**. Check:

- It does not read a global variable that changes during the recursion (like a running total or a `visited` set that is modified).
- Everything that changes between calls **is a parameter**.

Both of our functions pass: `ways` depends only on `n`; `paths` only on `(r, c)`. Read-only inputs such as an array `nums` that never changes are fine. They are the same for every call, so they are not part of the key.

If the check fails, fix the recursion first: turn the hidden changing value into a parameter. (Example: a recursion that keeps "items used so far" in a global list is not pure. Pass "remaining capacity" as a parameter instead.)

### Step 1: identify the changing parameters and their ranges

| Function | Changing parameters | Range of each | Number of states |
|----------|---------------------|---------------|------------------|
| `ways(n)` | `n` | `0 .. n` | `n + 1` |
| `paths(r, c)` | `r`, `c` | `r: 0 .. rows-1`, `c: 0 .. cols-1` | `rows × cols` |

Find the range by looking at the **top-level call** (largest value) and the **base cases** (smallest value that reaches the cache).

### Step 2: create a cache sized by those ranges, with a sentinel

- **One array dimension per changing parameter.**
- **Size of each dimension = (largest value − smallest value + 1).** For `0 .. n` that is `n + 1`, not `n`.
- Fill it with a **sentinel**: a value that can never be a real answer. Path and way counts are always at least 1 here, so `-1` is safe.

```java
long[] memo = new long[n + 1];             Arrays.fill(memo, -1);
long[][] memo = new long[rows][cols];      for (long[] row : memo) Arrays.fill(row, -1);
```

> **Picking the sentinel.** Use `-1` when answers are never negative. If `0` is a possible answer (for example a blocked grid cell in Unique Paths II, or "0 ways" in Target Sum), `0` is **not** a safe sentinel: the code would recompute every 0-answer forever. If answers can be negative, use a separate `boolean[] computed`, or `Long.MIN_VALUE`, or a `Long[]` array where `null` means "not computed".

### Step 3: check the cache at the start (after the base cases)

```java
if (memo[n] != -1) return memo[n];
```

Put it **after** the base cases. Base cases are already O(1), and some base cases handle values that are **outside the cache** (like a negative index, Section 6.4), which would crash if checked against the array first.

### Step 4: store before returning

```java
long result = ways(n - 1) + ways(n - 2);   // the ORIGINAL recurrence, unchanged
memo[n] = result;
return result;
```

Every path out of the function that computes a new answer must store it. A common bug is to have two `return` statements and store in only one.

### 6.1 Climbing Stairs — before and after, side by side

```
BEFORE (brute force)                         AFTER (memoized)
─────────────────────────────────────────    ─────────────────────────────────────────────
                                             static long[] memo;           // step 2
                                             static long climbStairs(int n) {
                                                 memo = new long[n + 1];   // step 2: size n+1
                                                 Arrays.fill(memo, -1);    // step 2: sentinel
                                                 return ways(n);
                                             }
static long ways(int n) {                    static long ways(int n) {
    if (n <= 1) return 1;                        if (n <= 1) return 1;
                                                 if (memo[n] != -1) return memo[n];   // step 3
    return ways(n - 1) + ways(n - 2);            long result = ways(n - 1) + ways(n - 2);
                                                 memo[n] = result;                    // step 4
                                                 return result;
}                                            }
```

Count what changed: one array, one fill, one `if`, one assignment. The base case and the recurrence are **identical**. That is why this conversion is safe: if the brute force was correct, the memoized version is too.

### 6.2 Grid paths — before and after

```
BEFORE (brute force)                         AFTER (memoized)
─────────────────────────────────────────    ─────────────────────────────────────────────
                                             static long[][] memo;
                                             static long uniquePaths(int rows, int cols) {
                                                 memo = new long[rows][cols];          // step 2
                                                 for (long[] row : memo)
                                                     Arrays.fill(row, -1);             // step 2
                                                 return paths(rows - 1, cols - 1);
                                             }
static long paths(int r, int c) {            static long paths(int r, int c) {
    if (r == 0 || c == 0) return 1;              if (r == 0 || c == 0) return 1;
                                                 if (memo[r][c] != -1) return memo[r][c]; // step 3
    return paths(r - 1, c)                       long result = paths(r - 1, c)
         + paths(r, c - 1);                                  + paths(r, c - 1);
                                                 memo[r][c] = result;                   // step 4
                                                 return result;
}                                            }
```

Two changing parameters → a **2D** cache. Their ranges `0..rows-1` and `0..cols-1` → size `[rows][cols]`.

### 6.3 Dry run of cache hits — Climbing Stairs, `n = 5`

`memo` starts as `[-1, -1, -1, -1, -1, -1]` (indices 0..5; cells 0 and 1 are never used because base cases return first).

| # | Call | What happens | memo[2..5] afterwards |
|---|------|--------------|------------------------|
| 1 | `ways(5)` | miss → calls `ways(4)` | `-1 -1 -1 -1` |
| 2 | `ways(4)` | miss → calls `ways(3)` | `-1 -1 -1 -1` |
| 3 | `ways(3)` | miss → calls `ways(2)` | `-1 -1 -1 -1` |
| 4 | `ways(2)` | miss → calls `ways(1)` = 1 (base), `ways(0)` = 1 (base) → store 2 | ` 2 -1 -1 -1` |
| 5 | back in `ways(3)` | calls `ways(1)` = 1 (base) → 2 + 1 = 3, store | ` 2  3 -1 -1` |
| 6 | back in `ways(4)` | calls `ways(2)` → **HIT**, 2 → 3 + 2 = 5, store | ` 2  3  5 -1` |
| 7 | back in `ways(5)` | calls `ways(3)` → **HIT**, 3 → 5 + 3 = 8, store | ` 2  3  5  8` |

Answer **8**. The two hits replaced the entire repeated subtrees of the brute-force tree:

```
                ways(5)
               /       \
           ways(4)    ways(3) ◄── HIT (3)
           /     \
       ways(3)  ways(2) ◄── HIT (2)
       /    \
   ways(2) ways(1)
   /    \
 w(1)  w(0)
```

9 calls instead of 15. The tree has become a **chain** with short side branches; its size grows linearly with `n`.

### 6.4 Dry run of cache hits — grid 3 × 3

`memo` is 3 × 3, all `-1`. Only cells with `r ≥ 1` and `c ≥ 1` are ever stored.

| # | Call | What happens | memo afterwards (`.` = -1) |
|---|------|--------------|----------------------------|
| 1 | `paths(2,2)` | miss → calls `paths(1,2)` | all `.` |
| 2 | `paths(1,2)` | miss → `paths(0,2)` = 1 (base), calls `paths(1,1)` | all `.` |
| 3 | `paths(1,1)` | miss → `paths(0,1)` = 1, `paths(1,0)` = 1 → store 2 | `(1,1)=2` |
| 4 | back in `paths(1,2)` | 1 + 2 = 3, store | `(1,1)=2 (1,2)=3` |
| 5 | back in `paths(2,2)` | calls `paths(2,1)`: miss → `paths(1,1)` **HIT** 2, `paths(2,0)` = 1 → store 3 | `(1,1)=2 (1,2)=3 (2,1)=3` |
| 6 | back in `paths(2,2)` | 3 + 3 = 6, store | `+ (2,2)=6` |

Final cache:

```
       c=0  c=1  c=2
 r=0    .    .    .      (row 0 and column 0 are base cases, never stored)
 r=1    .    2    3
 r=2    .    3    6   ◄── answer
```

Answer **6**.

### 6.5 Negative and offset indices

Array indices start at 0. Parameters do not always. Three situations come up again and again.

#### (a) The recursion can step **below** the smallest index

Allow steps of size 1, 2 **or 3** in Climbing Stairs and write the base cases as "landing exactly on 0 is one way, overshooting is zero ways":

```java
static long ways3(int i) {
    if (i < 0)  return 0;                      // overshot: no valid plan     ◄── FIRST
    if (i == 0) return 1;                      // exactly at the ground
    if (memoNeg[i] != -1) return memoNeg[i];   // safe: i is now >= 1
    memoNeg[i] = ways3(i - 1) + ways3(i - 2) + ways3(i - 3);
    return memoNeg[i];
}
```

`ways3(1)` calls `ways3(-1)` and `ways3(-2)`. If the cache check came before `i < 0`, Java would throw `ArrayIndexOutOfBoundsException: Index -1`. **Rule: base cases that handle out-of-range values come before the cache check.** `ClimbingStairsMemoization.java` tests this: sizes `{1, 2, 3}` give 1, 1, 2, 4, 7, 13 for `n = 0..5`.

#### (b) A parameter starts at `-1` (or `1`)

In many problems a parameter means "the index of the previously chosen element", and `-1` means "nothing chosen yet". This happens in the classic recursion for [Longest Increasing Subsequence](../../03-subsequence-and-knapsack-dp/11-longest-increasing-subsequence/). The range is `-1 .. n-1`, so:

```
size   = (n - 1) - (-1) + 1 = n + 1
index  = prev + 1                     (prev = -1 → index 0, prev = n-1 → index n)
memo[i][prev + 1]
```

The same trick in reverse applies to a parameter that starts at 1: either allocate `n + 1` and leave index 0 unused (simplest), or store at `value - 1`.

#### (c) A parameter can be **truly negative**: the offset trick

Target Sum style: put `+` or `-` in front of each number of `nums` and count the sign choices whose total equals `target`. The recursion is

```java
static long ways(int i, int sum) {          // sum = running total of the signed numbers so far
    if (i == nums.length) return (sum == target) ? 1 : 0;
    return ways(i + 1, sum + nums[i])        // '+'
         + ways(i + 1, sum - nums[i]);       // '-'
}
```

The changing parameters are `i` (range `0 .. n`) and `sum`. The running sum can be anything from `-total` to `+total`, where `total` is the sum of all `nums`. For `nums = [1,1,1,1,1]`, `sum` ranges from `-5` to `+5`.

There is no `memo[i][-5]`. So **shift** every sum by `offset = total`:

```
real sum:     -5  -4  -3  -2  -1   0   1   2   3   4   5
array index:   0   1   2   3   4   5   6   7   8   9  10      index = sum + offset
```

Size of that dimension: `5 - (-5) + 1 = 11 = 2 · total + 1`.

```java
offset = total;
memo = new long[n + 1][2 * total + 1];
...
int column = sum + offset;
if (memo[i][column] != -1) return memo[i][column];
```

Two details:

1. Only the **parameter** is shifted. The base case still compares the **real** `sum` with `target`, so a negative target like `-5` works unchanged.
2. **The sentinel cannot be 0** here, because "0 ways" is a common real answer. `-1` is safe since counts are never negative.

For `[1,1,1,1,1]` and target `3` the answer is **5**: exactly one number must get a minus sign (4 − 1 = 3), and there are five numbers to choose from.

#### (d) The alternative: a `HashMap`

When ranges are awkward, unknown or huge but sparse, use the state itself as a map key:

```java
Map<String, Long> cache = new HashMap<>();
String key = i + "," + sum;               // negative values are fine in a key
Long saved = cache.get(key);
if (saved != null) return saved;          // null plays the role of the sentinel
...
cache.put(key, result);
```

Same big-O, but each entry costs much more memory and time than an array cell (string building, hashing, boxing). Prefer an array whenever the ranges are known and reasonably small. `OffsetIndexMemoization.java` implements both and checks that they agree with the brute force for every target from −10 to 10.

---

## 7. Tabulation (bottom-up)

Not covered here: turning this cache into loops is the next topic, [14 · Converting Memoization to Tabulation](../14-converting-memoization-to-tabulation/). Notice, though, that the final memo for the grid in Section 6.4 is already the table that tabulation will fill. Tabulation simply fills it in a planned order instead of the order the recursion happens to visit.

---

## 8. Space optimization

Does not apply to a memoized recursion: the recursion can return to any earlier state at any time, so the whole cache must be kept. Space optimization works on the table version; see [15 · Space Optimization Basics](../15-space-optimization-basics/).

---

## 9. Dry run on a full example

Here is the complete before-and-after effect on the two examples, as call counts (all checked by the programs):

| Input | Brute-force calls | Memo: states computed | Memo: total calls (including hits and base cases) |
|-------|-------------------|-----------------------|---------------------------------------------------|
| Stairs `n = 5` | 15 | 4 (`ways(2..5)`) | 9 |
| Stairs `n = 45` | ≈ 3.67 × 10⁹ | 44 | 89 |
| Grid 3 × 3 | 11 | 4 (`(1,1) (1,2) (2,1) (2,2)`) | 9 |
| Grid 10 × 10 | 97 239 | 81 | 163 |

Each computed state makes exactly 2 calls, plus 1 for the root: `calls = 2 · states + 1`. The calls grow with the number of states, not with the number of answers.

And the trace of the Target Sum example `[1,1,1,1,1]`, target 3, shows why the offset cache helps. The brute force makes `2⁵⁺¹ − 1 = 63` calls. The memo has at most `6 × 11 = 66` cells, but only reachable ones are filled. For example, at `i = 2` the running sum can only be `-2, 0, 2`, so only 3 cells of that row are ever used, and each is reached twice (sum 0 is reached as `+1-1` and `-1+1`):

```
i = 2, sum = 0  ← reached by "+1 -1" (miss: computed)  and by "-1 +1" (HIT)
```

With 5 numbers this saves little; with 20 numbers the brute force needs about 2 million calls and the memo stays at a few hundred states.

---

## 10. Complexity analysis

Use the rule from [topic 12](../12-time-and-space-complexity-in-dp/): time = states × work per state; space = cache + recursion depth.

| Version | Time | Space | Why |
|---------|------|-------|-----|
| Stairs brute force | O(2ⁿ) | O(n) | every call branches in two; stack depth `n` |
| Stairs memo | **O(n)** | O(n) | `n + 1` states × O(1) work; cache `n + 1` + stack `n` |
| Grid brute force | O(2^(rows+cols)) | O(rows + cols) | branching tree; depth = steps to the edge |
| Grid memo | **O(rows · cols)** | O(rows · cols) | one state per cell × O(1); cache dominates the stack |
| Target Sum brute force | O(2ⁿ) | O(n) | two sign choices per number |
| Target Sum memo (offset) | **O(n · total)** | O(n · total) | `(n + 1) × (2·total + 1)` states × O(1) |
| Target Sum memo (HashMap) | O(n · total) expected | O(reachable states) | same states; only reachable ones are stored, each with extra overhead |

---

## 11. Why this approach works

1. **Pure function.** Because the answer depends only on the parameters, the first time we compute `f(x)` we get **the** answer for `x`. Returning it again later is identical to recomputing it.
2. **Unchanged logic.** The base cases and recurrence are copied as they are, so the memoized function computes the same values as the brute force, which we already trusted.
3. **Every state fits in the cache.** Step 1 found the full range of every parameter, and step 2 sized the cache to cover it (with an offset where needed), so no state is missing and no index is out of bounds.
4. **The sentinel never lies.** It cannot be a real answer, so "cell equals sentinel" means "not computed yet", and never "computed, and the answer happens to be this value".
5. **Each state is computed at most once.** After the first computation the cell holds a non-sentinel value, so every later call returns at step 3. That gives the "states × work" running time.

---

## 12. Common mistakes

1. **Cache one too small.** Range `0..n` needs `n + 1` cells. `new long[n]` crashes on `memo[n]`.
2. **Using 0 as the sentinel when 0 is a real answer.** The program is still correct, but every 0-state is recomputed, and it can become exponential again. Typical in Unique Paths II and Target Sum.
3. **Checking the cache before handling out-of-range parameters.** `memo[-1]` throws. Handle `i < 0`, `r < 0`, "out of the grid" first.
4. **Storing in one branch only.** Each `return` that computes a new value must store it.
5. **Hidden changing state.** The function reads a global variable that changes (a running sum, a mutable list). The cache then mixes answers from different situations. Make it a parameter.
6. **Putting a constant input into the key.** `nums` or `target` that never change do not need a dimension. Adding one wastes memory.
7. **Not resetting a static cache between test cases.** If `memo` is a static field, allocate it again in the public entry method (as our files do) or the second test reads stale answers from the first.
8. **Shifting the target as well as the parameter.** With an offset, compare the **real** sum to the **real** target, and shift only when indexing the array.
9. **Overflow.** Counts grow fast. Use `long`, or take the modulo the problem asks for, inside the store step.

---

## 13. When to use this technique / how to recognize similar problems

Use the recipe whenever:

- you have a recursive solution that is **correct** on small inputs but too slow, and
- drawing its recursion tree shows the **same calls repeated** (overlapping subproblems), and
- the function is (or can be made) **pure**.

It is often the fastest way to a working DP in an interview: think recursively, write the brute force, apply the four steps. If recursion depth becomes a problem in Java (about 10⁴ or more, see [topic 12](../12-time-and-space-complexity-in-dp/)), continue with [14 · Converting Memoization to Tabulation](../14-converting-memoization-to-tabulation/).

---

## 14. Summary and revision notes

- First make sure the recursion is **correct** and **pure**.
- **Step 1:** list the changing parameters and their ranges.
- **Step 2:** cache with one dimension per parameter, size `max − min + 1`, filled with a sentinel that is never a real answer.
- **Step 3:** after the base cases, `if (cached) return cached;`
- **Step 4:** `memo[...] = result; return result;`
- Base cases and recurrence stay exactly the same.
- Negative or shifted parameters: index with `value + offset`; or use a `HashMap` key.
- Result: time = states × work; space = cache + recursion depth.

```
┌───────────────────────────────────────────────────────────────┐
│  static T[] memo = new T[range];  fill(memo, SENTINEL);       │
│                                                               │
│  T f(params) {                                                │
│      if (base case / out of range) return known;   ← first    │
│      if (memo[key(params)] != SENTINEL)                       │
│          return memo[key(params)];                 ← check    │
│      T result = <original recurrence, unchanged>;             │
│      memo[key(params)] = result;                   ← store    │
│      return result;                                           │
│  }                                                            │
│  key(params) = params shifted by offsets so the minimum is 0  │
└───────────────────────────────────────────────────────────────┘
```

---

## 15. Check your understanding

**Exercise 1.** Apply steps 1 and 2 to this recursion. What cache do you allocate?

```java
// best(i, k): best score using items i..n-1 when k skips are still allowed (0 <= k <= K)
static int best(int i, int k) {
    if (i == n) return 0;
    int take = score[i] + best(i + 1, k);
    int skip = (k > 0) ? best(i + 1, k - 1) : Integer.MIN_VALUE;
    return Math.max(take, skip);
}
// top-level call: best(0, K)
```

<details><summary>Answer</summary>

Changing parameters: `i` with range `0..n` (`n + 1` values; `i == n` is a base case, so `n` cells would also be enough, but `n + 1` is the safe habit) and `k` with range `0..K` (`K + 1` values). `score`, `n` and `K` never change, so they are not in the key. Allocate `int[][] memo = new int[n + 1][K + 1]`. Scores could be negative, so `-1` is not a safe sentinel: use `Integer.MIN_VALUE` (if it cannot be a real answer) or a separate `boolean[][] computed`.

</details>

**Exercise 2.** A student memoizes Unique Paths II (some grid cells are blocked) and uses `0` as the "not computed" marker. All answers are correct, but a large grid with many blocked cells times out. Why?

<details><summary>Answer</summary>

Blocked cells, and cells that can only be reached through blocked cells, have the real answer **0**. With `0` as the sentinel, the check `memo[r][c] != 0` fails for them every time, so they are recomputed on every visit, together with their whole subtrees. The cache never "remembers" them, and the running time can become exponential again. Fill the cache with `-1` instead.

</details>

**Exercise 3.** A parameter `prev` ranges from `-1` to `n - 1`, and a parameter `diff` ranges from `-50` to `50`. What array size and index expressions do you use?

<details><summary>Answer</summary>

`prev`: size `(n − 1) − (−1) + 1 = n + 1`, index `prev + 1`. `diff`: size `50 − (−50) + 1 = 101`, index `diff + 50`. So `memo = new long[n + 1][101]` and the lookup is `memo[prev + 1][diff + 50]`.

</details>

**Exercise 4.** Why is this function **not** safe to memoize as `memo[i]`?

```java
static int total = 0;               // global
static int f(int i) {
    if (i == n) return total;
    total += nums[i];
    int a = f(i + 1);
    total -= nums[i];
    int b = f(i + 1);
    return Math.max(a, b);
}
```

<details><summary>Answer</summary>

The result of `f(i)` depends on `i` **and** on the global `total`, which differs depending on which numbers were added before reaching `i`. Two calls `f(3)` with different `total` values have different answers, so caching by `i` alone would return the wrong one. Make it pure by passing `total` as a parameter, `f(i, total)`, and memoize on both (with an offset if `total` can be negative).

</details>

---

⬅ Previous: [12 · Time and Space Complexity in DP](../12-time-and-space-complexity-in-dp/) · ➡ Next: [14 · Converting Memoization to Tabulation](../14-converting-memoization-to-tabulation/) · 🏠 [README](README.md)
