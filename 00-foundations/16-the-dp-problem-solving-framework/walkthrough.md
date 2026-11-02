# The DP Problem-Solving Framework — Walkthrough

> Main running example: **CSES 1633 Dice Combinations**, solved end to end (answers: `n = 3 → 4`, `n = 7 → 63`, `n = 10 → 492`).
> Second example, at sketch level: **LeetCode 198 House Robber** (`[2, 7, 9, 3, 1] → 12`).
> Code: [`DiceCombinationsBruteForce.java`](solution/DiceCombinationsBruteForce.java), [`DiceCombinationsMemoization.java`](solution/DiceCombinationsMemoization.java), [`DiceCombinationsTabulation.java`](solution/DiceCombinationsTabulation.java), [`DiceCombinationsOptimized.java`](solution/DiceCombinationsOptimized.java), [`HouseRobberFramework.java`](solution/HouseRobberFramework.java).
> Blank worksheet to copy: [`WORKSHEET.md`](WORKSHEET.md).

---

## 1. Intuition

Think of a recipe card. A good cook does not invent the steps for bread every morning. The card says: measure, mix, knead, rest, shape, bake, cool. The ingredients change from recipe to recipe, but the **kinds** of steps hardly change, and if the bread comes out flat you can look at the card and find the step you rushed.

The 7 questions are the recipe card for DP:

```
   ┌──────────────────────────────────────────────────────────────────┐
   │  1. What is changing?          ┐                                 │
   │  2. What does the state mean?  │  "design"  → what a cell means  │
   │  3. What are the choices?      │                                 │
   │  4. What is the recurrence?    │  "logic"   → how cells connect  │
   │  5. What are the base cases?   ┘                                 │
   │  6. What is the order?            "engineering" → how to fill    │
   │  7. Can the space shrink?         "polish"  → how little to keep │
   └──────────────────────────────────────────────────────────────────┘
          then: complexity → tests by hand → code (brute → memo → table → optimized)
```

Each question was taught in its own topic. This lesson is about using them **together, in order, on a problem you have not seen**.

---

## 2. Problem statement in our own words

### The framework itself

> Given an unfamiliar problem, decide whether it is a DP problem, and if it is, derive a correct and efficient solution by answering the seven questions in order, then code it in stages.

### Main example: Dice Combinations

[Dice Combinations (CSES 1633)](https://cses.fi/problemset/task/1633): you throw an ordinary six-sided die again and again, adding up the faces. Count how many different **sequences** of throws give a total of exactly `n`. Order matters: throwing 1 then 2 is different from 2 then 1. The count is huge, so the answer is given modulo `10⁹ + 7`. `n` can be as large as one million.

### Second example: House Robber

[House Robber (LeetCode 198)](https://leetcode.com/problems/house-robber/): houses stand in a row, each with some amount of money. You may take money from any houses you like, except that you may never take from two houses that are next to each other. What is the largest total you can collect? (The full lesson is [01-one-dimensional-dp/06-house-robber](../../01-one-dimensional-dp/06-house-robber/); here we only sketch it, to practice the framework.)

---

## 3. Examples

### Dice Combinations

| n | Answer | The sequences |
|---|--------|---------------|
| 1 | 1 | `1` |
| 2 | 2 | `1+1`, `2` |
| 3 | 4 | `1+1+1`, `1+2`, `2+1`, `3` |
| 4 | 8 | `1+1+1+1`, `1+1+2`, `1+2+1`, `2+1+1`, `2+2`, `1+3`, `3+1`, `4` |
| 6 | 32 | (too many to list) |
| 7 | 63 | (note: **not** 64 — see Section 9) |
| 10 | 492 | |

### House Robber

| Houses | Answer | Why |
|--------|--------|-----|
| `[2, 7, 9, 3, 1]` | 12 | rob houses 0, 2, 4: `2 + 9 + 1` |
| `[1, 2, 3, 1]` | 4 | rob houses 0 and 2: `1 + 3` |
| `[5]` | 5 | only one house |
| `[2, 1, 1, 2]` | 4 | rob houses 0 and 3: `2 + 2` (skipping two in a row is allowed) |

Before any derivation, always work out two or three small examples by hand, like these. They become your tests, and listing them often reveals the structure: in the `n = 4` list, the sequences group by their **last** throw (`...+1`, `...+2`, `...+3`, `4`). That grouping is question 3 in disguise.

---

## 4. Deriving the solution — the 7 questions

### The checklist

| # | Question | What a good answer looks like | Taught in |
|---|----------|-------------------------------|-----------|
| 0 | Is it DP? | Counting / min / max / yes-no over a sequence of decisions, with repeated subproblems | [04](../04-overlapping-subproblems/), [05](../05-optimal-substructure/), Section 13 below |
| 1 | What is changing? | A short list: index, remaining amount, position... | [09](../09-state-representation/) |
| 2 | What does the state represent? | A full English sentence `f(...) = ...`, with parameter ranges, and which state is the answer | [09](../09-state-representation/) |
| 3 | What are the choices? | Every possible decision at one state, with any conditions | [10](../10-state-transitions-and-base-cases/) |
| 4 | What is the recurrence? | `f(...) = combine(choices)`, using `+`, `min`, `max` or `OR`, each term explained | [10](../10-state-transitions-and-base-cases/) |
| 5 | What are the base cases? | Terminal states (answer known) and invalid states (return 0, ∞, false...) | [10](../10-state-transitions-and-base-cases/) |
| 6 | What is the order of computation? | "Reads smaller `i` → loop up", etc. | [11](../11-transition-dependencies-and-computation-order/) |
| 7 | Can the state space be optimized? | The read window and the smaller storage, or "no, need reconstruction" | [15](../15-space-optimization-basics/) |
| + | Complexity | states × work; table + stack | [12](../12-time-and-space-complexity-in-dp/) |
| + | Code in stages | brute → memo → table → optimized, all agreeing on the same tests | [13](../13-converting-recursion-to-memoization/), [14](../14-converting-memoization-to-tabulation/) |

### The printable template

Copy this block into your notes, or use the longer [WORKSHEET.md](WORKSHEET.md).

```
PROBLEM: ______________________   LIMITS: ____________   ASKS: count / min / max / yes-no

0. DP?        decisions in sequence?  repeated subproblems?  greedy counterexample?
1. CHANGING:  ______________________________________________
2. STATE:     f(___) = ________________________________________
              ranges: ______________     answer = f(___)
3. CHOICES:   a) ____________  b) ____________  c) ____________
4. RECURRENCE:
              f(___) = ________________________________________
5. BASE:      terminal: _______________  invalid: _______________
6. ORDER:     f reads ______ → loop ______________________
7. SPACE:     reads back ____ → keep ____________  (reconstruct? y/n)
   TIME = ____ states × ____ work = ______     SPACE = ______   DEPTH = ____
TESTS (by hand):  ______ → ____   ______ → ____   edge ______ → ____
```

Now we fill it in for Dice Combinations, one question at a time.

### Step 0. Is this a DP problem?

- It asks to **count** ways. ✔
- Each throw is a **decision** (which face) that changes what is left to reach. ✔
- Trying all sequences is exponential (there are 492 of them already for `n = 10`, and the count roughly doubles with every +1), and the same question "how many ways to make sum `s`?" appears inside many different sequences. ✔
- `n ≤ 10⁶` can be an array size. ✔

All signals say DP. Go on.

### Q1. What is changing?

As we throw, the **total so far** grows, or equivalently, the **amount still missing** shrinks. One number. We will use the target sum `s` we still need to make.

### Q2. What does the state represent?

> `ways(s)` = **the number of ordered sequences of throws (each face 1 to 6) whose faces add up to exactly `s`.**

- Range: `s` from `0` to `n` → `n + 1` states.
- The answer is `ways(n)`.

Writing the full sentence forces you to decide things that are easy to skip, such as "ordered" (`1+2` and `2+1` are different). If the problem were unordered, this state would be **wrong** (see Exercise 3).

### Q3. What are the choices?

Look at the **last** throw of a sequence that sums to `s`. It shows some face `d`, and `d` can be `1, 2, 3, 4, 5` or `6`. That is six choices. A choice is only allowed if `d ≤ s` (you cannot overshoot).

### Q4. What is the recurrence?

If the last throw is `d`, the throws before it form a sequence that sums to `s - d`, and **any** such sequence works. Different last throws give different sequences, so we **add**:

```
ways(s) = ways(s-1) + ways(s-2) + ways(s-3) + ways(s-4) + ways(s-5) + ways(s-6)
          └ last = 1  └ last = 2  └ last = 3  └ last = 4  └ last = 5  └ last = 6
          (terms with s - d < 0 are left out, or count as 0)
          all modulo 1 000 000 007
```

Check with `n = 3`: `ways(3) = ways(2) + ways(1) + ways(0) = 2 + 1 + 1 = 4`. ✔ (matches the list in Section 3)

### Q5. What are the base cases?

- **Terminal:** `ways(0) = 1`. A total of 0 is made by exactly one sequence: throwing nothing. This feels odd, but it is what makes `ways(3)` count the single-throw sequence `3`: the term `ways(3-3) = ways(0)` must be 1. (Topic [10](../10-state-transitions-and-base-cases/) explained why "the empty way" counts as one way.)
- **Invalid:** `ways(negative) = 0`. We overshot, so that is not a valid sequence.

Quick check of a wrong choice: if we set `ways(0) = 0`, every count would be 0. If we instead made `ways(1) = 1` the base case and forgot `ways(0)`, then `ways(2)` would miss the single throw `2`. Always test the base case by computing the first two or three states by hand.

### Q6. What is the order of computation?

`ways(s)` reads `ways(s-1) ... ways(s-6)`: all **smaller** sums. So fill the table **left to right**, `s = 1, 2, ..., n`, after setting `ways[0] = 1`.

```
 ways[s-6] ways[s-5] ways[s-4] ways[s-3] ways[s-2] ways[s-1]  ways[s]
    ▲         ▲         ▲         ▲         ▲         ▲          │
    └─────────┴─────────┴─────────┴─────────┴─────────┴──────────┘
                      all arrows point left → loop s upward
```

### Q7. Can the state space be optimized?

The read window is the **last six** values. So we only need six numbers, not `n + 1`. We keep them in a small circular array where `ways[s]` lives in slot `s % 6` (Section 8). Space: `O(n)` → `O(1)`. We only want the count, not the sequences themselves, so nothing needs to be reconstructed.

### Complexity (filled in before coding)

- States: `n + 1`. Work per state: at most 6 additions. **Time `O(6n) = O(n)`**: about 6 million additions for `n = 10⁶`. Fine.
- Table space `O(n)`: 10⁶ `long`s = 8 MB. Fine. Optimized: `O(1)`.
- Memo recursion depth: about `n` = 10⁶. **Too deep for Java's default stack** ([topic 12](../12-time-and-space-complexity-in-dp/)). Plan: submit the tabulated or optimized version.

### Filled template

```
PROBLEM: Dice Combinations (CSES 1633)   LIMITS: n ≤ 10^6   ASKS: count (mod 1e9+7)

0. DP?        yes: count, sequence of throws, ways(s) reused by many sequences
1. CHANGING:  the sum still to make, s
2. STATE:     ways(s) = # ordered throw sequences (faces 1..6) summing to exactly s
              ranges: 0 ≤ s ≤ n     answer = ways(n)
3. CHOICES:   last throw d = 1..6 (only if d ≤ s)
4. RECURRENCE:
              ways(s) = Σ_{d=1..6, d≤s} ways(s - d)   (mod 1e9+7)
5. BASE:      terminal: ways(0) = 1   invalid: ways(<0) = 0
6. ORDER:     reads smaller s → for s = 1..n
7. SPACE:     reads back 6 → keep 6 values (circular)   (reconstruct? n)
   TIME = (n+1) states × 6 work = O(n)   SPACE = O(n) → O(1)   DEPTH = n (too deep for memo)
TESTS (by hand):  3 → 4   4 → 8   7 → 63   edge 1 → 1
```

From here on, writing the code is translation, not invention.

---

## 5. Brute force: recursion

Translate Q2–Q5 literally:

```java
static long ways(int sum) {
    if (sum == 0) return 1;  // terminal: exactly reached the target
    if (sum < 0) return 0;   // invalid: overshot
    long total = 0;
    for (int face = 1; face <= 6; face++) {
        total += ways(sum - face);   // last throw shows `face`
    }
    return total;
}
```

Recursion tree for `n = 3` (branches that go negative are not drawn; each returns 0):

```
ways(3)
├── last 1 → ways(2)
│            ├── last 1 → ways(1)
│            │            └── last 1 → ways(0) = 1      sequence 1+1+1
│            └── last 2 → ways(0) = 1                   sequence 2+1
├── last 2 → ways(1)
│            └── last 1 → ways(0) = 1                   sequence 1+2
└── last 3 → ways(0) = 1                                 sequence 3
```

Reading a root-to-leaf path from the **leaf up** gives the throws in order. The four leaves are `1+1+1`, `2+1`, `1+2` and `3`: exactly the four sequences.

**Why it is slow:** every call branches up to 6 ways, so the tree grows exponentially. `ways(1)` already appears twice in this tiny tree; for `n = 30` the same small sums are recomputed hundreds of millions of times. The brute-force file tests only `n ≤ 10`.

---

## 6. Memoization (top-down)

Apply the recipe from [13 · Converting Recursion to Memoization](../13-converting-recursion-to-memoization/): the changing parameter is `sum` in `0..n`, so a `long[n + 1]` cache with sentinel `-1` (a count is never negative).

```java
static long ways(int sum) {
    if (sum == 0) return 1;
    if (sum < 0) return 0;
    if (memo[sum] != -1) return memo[sum];            // check
    long total = 0;
    for (int face = 1; face <= 6; face++)
        total = (total + ways(sum - face)) % MOD;
    memo[sum] = total;                                // store
    return total;
}
```

Dry run for `n = 4`, showing cache hits (H). Negative sums are skipped in the table.

| Order | Call | Reads | Result | Note |
|-------|------|-------|--------|------|
| 1 | ways(1) | ways(0)=1 | 1 | stored |
| 2 | ways(2) | ways(1)=1 **H**, ways(0)=1 | 2 | stored |
| 3 | ways(3) | ways(2)=2 **H**, ways(1)=1 **H**, ways(0)=1 | 4 | stored |
| 4 | ways(4) | ways(3)=4 **H**, ways(2)=2 **H**, ways(1)=1 **H**, ways(0)=1 | 8 | stored |

(The first call `ways(4)` descends `ways(3) → ways(2) → ways(1) → ways(0)` before anything is stored; the table lists states in the order they **finish**.)

Each state is computed once; every later request is an O(1) hit. Time `O(n)`, but the recursion is `n` calls deep. The memo file tests up to `n = 1000`; for `n = 10⁶`, use the table.

---

## 7. Tabulation (bottom-up)

Apply the recipe from [14 · Converting Memoization to Tabulation](../14-converting-memoization-to-tabulation/): base case → table init; recursive calls → table reads; loop direction from Q6.

```java
long[] ways = new long[n + 1];
ways[0] = 1;                                           // base case
for (int s = 1; s <= n; s++)                           // Q6: left to right
    for (int face = 1; face <= 6 && face <= s; face++) // face <= s replaces "sum < 0 → 0"
        ways[s] = (ways[s] + ways[s - face]) % MOD;
return ways[n];
```

Filling the table for `n = 7`, step by step:

```
 s:        0    1    2    3    4    5    6    7
 start:    1    .    .    .    .    .    .    .
 s=1:      1   [1]                                    = w0
 s=2:      1    1   [2]                               = w1+w0
 s=3:      1    1    2   [4]                          = w2+w1+w0
 s=4:      1    1    2    4   [8]                     = w3+w2+w1+w0
 s=5:      1    1    2    4    8  [16]                = w4+...+w0
 s=6:      1    1    2    4    8   16  [32]           = w5+...+w0  (six terms)
 s=7:      1    1    2    4    8   16   32  [63]      = w6+...+w1  (six terms, w0 drops out)
```

No recursion, so `n = 10⁶` runs in a fraction of a second. `DiceCombinationsTabulation.java` checks `n = 1, 3, 4, 6, 7, 10, 50` and `n = 1 000 000` (answer `874273980`).

---

## 8. Space optimization

Q7 said the read window is six values. Keep them in `last[0..5]`, with `ways[s]` stored in slot `s % 6`. When we compute `ways[s]`, slot `s % 6` still holds `ways[s-6]`, which is the oldest value, and after this step nobody needs it: safe to overwrite.

```java
long[] last = new long[6];
last[0] = 1;                                           // ways[0]
for (int s = 1; s <= n; s++) {
    long total = 0;
    for (int face = 1; face <= 6 && face <= s; face++)
        total += last[(s - face) % 6];
    last[s % 6] = total % MOD;                         // overwrites ways[s-6]
}
return last[n % 6];
```

Trace for `n = 7` (slot contents after each step; `[x]` = just written):

```
 slot:        0     1     2     3     4     5
 start:      [1]    0     0     0     0     0      ways0
 s=1:         1    [1]    0     0     0     0      ways1 = 1
 s=2:         1     1    [2]    0     0     0      ways2 = 1+1
 s=3:         1     1     2    [4]    0     0      ways3 = 2+1+1
 s=4:         1     1     2     4    [8]    0      ways4
 s=5:         1     1     2     4     8   [16]     ways5
 s=6:       [32]    1     2     4     8    16      ways6 = 16+8+4+2+1+1, overwrites ways0
 s=7:        32   [63]    2     4     8    16      ways7 = 32+16+8+4+2+1, overwrites ways1
 answer: last[7 % 6] = last[1] = 63
```

Space `O(1)`, same `O(n)` time. (Another common trick is to keep a running sum of the last six values and update it by adding the newest and subtracting the oldest; it is faster but easier to get wrong with the modulo, so we do not need it here.)

---

## 9. Dry run on a full example

### 9.1 Dice Combinations, `n = 7`, all four versions agree

| Version | Result for `n = 7` | How it got there |
|---------|-------------------|------------------|
| Brute force | 63 | sum over all leaves of the recursion tree |
| Memoization | 63 | 7 states computed once each, rest are hits |
| Tabulation | 63 | table `1 1 2 4 8 16 32 63` |
| Optimized | 63 | circular six-slot window |

Why is `ways(7) = 63`, not `64`? Up to `s = 6`, every earlier sum is inside the window, so each value is the sum of **all** previous values, which doubles (1, 2, 4, 8, 16, 32). At `s = 7`, the window `s-6..s-1 = 1..6` no longer includes `ways(0) = 1`. Real meaning: "a single throw of 7" is impossible. So `ways(7) = 64 - 1 = 63`. A hand check like this is exactly how you catch a wrong window size (for example a loop `face <= 7`).

### 9.2 House Robber, the framework as a sketch

Here is the framework run quickly, the way you would in an interview, on `nums = [2, 7, 9, 3, 1]`.

```
PROBLEM: House Robber (LC 198)   LIMITS: n ≤ 100   ASKS: max

0. DP?        yes: max over a sequence of take/skip decisions; a greedy rule like
              "take the biggest house" fails: [2, 3, 2] → greedy 3, best 2+2 = 4
1. CHANGING:  which house we are deciding about, i
2. STATE:     rob(i) = most money from houses 0..i, never two neighbours
              ranges: 0 ≤ i < n     answer = rob(n-1)
3. CHOICES:   skip house i     |     rob house i (then house i-1 is off limits)
4. RECURRENCE:
              rob(i) = max( rob(i-1),  nums[i] + rob(i-2) )
5. BASE:      rob(0) = nums[0]   rob(-1) = 0 (no houses)
6. ORDER:     reads smaller i → for i = 1..n-1
7. SPACE:     reads back 2 → two variables
   TIME = n × O(1) = O(n)   SPACE = O(n) → O(1)   DEPTH = n (fine, n ≤ 100)
TESTS:  [2,7,9,3,1] → 12   [1,2,3,1] → 4   edge [5] → 5
```

Table, to confirm the tests by hand:

| i | nums[i] | skip: rob(i-1) | take: nums[i] + rob(i-2) | rob(i) |
|---|---------|----------------|--------------------------|--------|
| 0 | 2 | — | 2 + 0 | 2 |
| 1 | 7 | 2 | 7 + 0 | 7 |
| 2 | 9 | 7 | 9 + 2 = 11 | 11 |
| 3 | 3 | 11 | 3 + 7 = 10 | 11 |
| 4 | 1 | 11 | 1 + 11 = 12 | **12** |

`HouseRobberFramework.java` implements all four stages (brute force, memo, table, two variables) and checks them on `[2,7,9,3,1] → 12`, `[1,2,3,1] → 4`, `[5] → 5` and `[2,1,1,2] → 4`. The full lesson, with recursion trees and every detail, is in [House Robber](../../01-one-dimensional-dp/06-house-robber/).

Notice how similar the two filled templates are. Dice Combinations and House Robber look nothing alike as stories, but both are "1D state, a fixed small read window, left-to-right loop, rolling variables". The framework makes that similarity visible.

---

## 10. Complexity analysis

### Dice Combinations

| Approach | Time | Space | Why |
|----------|------|-------|-----|
| Brute force | O(6ⁿ) bound (in fact about O(2ⁿ)) | O(n) stack | up to 6 branches per call, depth `n`; the number of leaves equals the answer, which roughly doubles per step |
| Memoization | O(6n) = O(n) | O(n) cache + O(n) stack | `n + 1` states, ≤ 6 terms each; recursion depth `n` |
| Tabulation | O(n) | O(n) | `n` cells × ≤ 6 additions |
| Optimized | O(n) | O(1) | same additions, six slots kept |

### House Robber

| Approach | Time | Space | Why |
|----------|------|-------|-----|
| Brute force | O(2ⁿ) | O(n) stack | two branches per house |
| Memoization | O(n) | O(n) + O(n) stack | `n` states, 2 terms each |
| Tabulation | O(n) | O(n) | one cell per house |
| Two variables | O(n) | O(1) | read window of 2 |

---

## 11. Why this approach works

**Why the framework produces correct solutions.** Each question establishes one part of a correctness argument:

1. **Q1–Q2 (state):** we define precisely what each number means. Without a precise meaning, there is nothing to be "correct" about.
2. **Q3 (choices):** we split all possible solutions for a state into groups, one per choice, with **every** solution in exactly one group (for counting) or at least one group (for min/max). For dice: every sequence has exactly one last throw.
3. **Q4 (recurrence):** each group's answer is a smaller state's answer (plus a fixed cost or value). For counting we add the groups; for optimization we take the best group. This step is valid when the problem has [optimal substructure](../05-optimal-substructure/).
4. **Q5 (base cases):** the smallest states are correct by direct reasoning.
5. **Q6 (order):** every state is computed after the states it reads, so, by induction from the base cases, every state is correct ([topic 11](../11-transition-dependencies-and-computation-order/)).
6. **Q7 (space):** we only discard values after their last read, so nothing changes ([topic 15](../15-space-optimization-basics/)).

**Why it is efficient.** Each state is computed once, and the number of states is the product of the parameter ranges from Q2. That is why choosing a **small** but **complete** state in Q2 matters so much: too few parameters gives wrong answers (topic [09](../09-state-representation/)), too many makes the table needlessly large.

**Why we code in stages.** Each stage is a mechanical translation of the previous one, and each must give the same answers on the same tests. If the table disagrees with the brute force on `n = 4`, you know the bug is in the translation (order, base case, indices), not in the idea.

---

## 12. Common mistakes

These are **process** mistakes, the ones a checklist is designed to catch.

1. **Coding before answering Q2.** Writing loops before you can say what `dp[i]` means in one sentence. Result: an index that "sort of" means two things, and off-by-one bugs.
2. **Skipping Step 0.** Some problems that look like DP have a simple greedy or math answer; some that look greedy need DP (Coin Change with coins `[1, 3, 4]` and amount 6: greedy `4+1+1` uses 3 coins, the best is `3+3` with 2).
3. **Not deciding ordered vs unordered** for counting problems. Dice Combinations counts orders; CSES Coin Combinations II does not, and needs a different state.
4. **Missing an invalid state.** Forgetting "sum < 0 → 0" gives an `ArrayIndexOutOfBoundsException` or garbage.
5. **A wrong `ways(0)`.** The "empty way" must count as 1 for counting problems; check with the smallest real input.
6. **Forgetting the modulo** after an addition (overflow after `n ≈ 90` for doubling counts), or applying it only at the end.
7. **Submitting deep memoization in Java** for `n = 10⁶`. Check the recursion depth in the complexity step.
8. **Not writing tests first.** If you have no hand-computed answers, you cannot tell a wrong solution from a right one.

---

## 13. When to use this technique / how to recognize similar problems

### Use the framework for every problem

Even easy ones. On easy problems it takes two minutes and builds the habit; on hard problems the habit is what saves you.

### Signals that a problem is DP (preview)

The full guide with many more patterns is [DP Pattern Recognition](../../07-revision-and-interview-preparation/DP_PATTERN_RECOGNITION.md). For now, learn these:

| Signal in the statement | Example | Typical DP shape |
|-------------------------|---------|------------------|
| "count the number of ways" | Climbing Stairs, Dice Combinations | sum over choices |
| "minimum / maximum cost, profit, length" | Min Cost Climbing Stairs, House Robber | min/max over choices |
| "is it possible", "can you reach" | Word Break, Partition Equal Subset Sum | OR over choices |
| a sequence of decisions where each one limits the future | "no two adjacent", "at most k", "remaining capacity" | the limit becomes a state parameter |
| move in a grid only right/down (one direction) | Unique Paths | 2D state `(r, c)` |
| two strings or sequences compared | LCS, Edit Distance | `(i, j)` over both |
| an answer for a range `[i, j]` built from smaller ranges | Burst Balloons | interval DP |
| a number in the input small enough to be an array index (sum ≤ 10⁴, capacity ≤ 10⁵) | knapsack, coin change | that number becomes a dimension |
| `n ≤ 20` and "choose a subset / order" | Traveling Salesperson | bitmask DP |
| brute force is exponential and you notice the same sub-question repeating | almost all of the above | memoize it |

### Signals that it is probably not DP

- **No real choices.** "Find the maximum element" or "sum the array": one pass, nothing to decide.
- **A simple greedy rule is provably right.** Example: [Jump Game](https://leetcode.com/problems/jump-game/) can be solved by DP, but tracking the farthest reachable index is O(n) with O(1) space and much simpler. Try to break the greedy rule with a small counterexample; if you cannot and you can argue why it works, prefer it.
- **Subproblems do not repeat.** Merge sort splits into halves that never overlap: divide and conquer, not DP.
- **The "state graph" has cycles you cannot remove.** Shortest paths in a general graph with free movement are graph algorithms (BFS, Dijkstra), unless you add a parameter that only grows (like "steps used", as in Cheapest Flights Within K Stops).
- **The state would be enormous.** If the only complete state is "the whole set of items used so far" and `n = 1000`, look for another idea (greedy, sorting, math), or a smarter state.

When in doubt: write the brute force, draw its recursion tree for a small input, and look for repeated nodes. Repeated nodes plus a clean state → DP.

---

## 14. Summary and revision notes

- DP solutions are **derived**, not remembered. Use the same seven questions every time, in order.
- **Before coding:** state in a full sentence, choices, recurrence, base cases, order, space, complexity, hand-computed tests.
- **Coding in stages:** brute force → memoization → tabulation → optimized; all must agree.
- Dice Combinations: `ways(s) = Σ_{d=1..6} ways(s-d)`, `ways(0) = 1`, left to right, six values kept, O(n) time, O(1) space; use tabulation in Java for `n = 10⁶`.
- House Robber: `rob(i) = max(rob(i-1), nums[i] + rob(i-2))`, two variables.
- Recognize DP by: count / min / max / possible, sequences of decisions, repeated subproblems, small numeric dimensions.
- Use the [worksheet](WORKSHEET.md) for every problem from now on.

```
┌──────────────────────────────────────────────────────────────────┐
│ 0 DP?  1 changing  2 state (sentence + answer cell)  3 choices    │
│ 4 recurrence  5 base (terminal + invalid)  6 order  7 space       │
│ + complexity  + hand tests  → brute → memo → table → optimized    │
│                                                                   │
│ Dice:   ways(s) = ways(s-1)+...+ways(s-6),  ways(0)=1, ways(<0)=0 │
│ Robber: rob(i)  = max(rob(i-1), nums[i] + rob(i-2)), rob(-1)=0    │
└──────────────────────────────────────────────────────────────────┘
```

---

## 15. Check your understanding

**Exercise 1.** Change Dice Combinations so the die has only faces 1, 2 and 3. Fill in Q2–Q5 and compute the answer for `n = 4` by hand.

<details><summary>Answer</summary>

- Q2: `ways(s)` = number of ordered sequences of throws with faces 1–3 summing to exactly `s`. Answer `ways(n)`.
- Q3: the last throw is 1, 2 or 3 (if it fits).
- Q4: `ways(s) = ways(s-1) + ways(s-2) + ways(s-3)`.
- Q5: `ways(0) = 1`, `ways(negative) = 0`.

Table: `ways(0)=1`, `ways(1)=1`, `ways(2)=1+1=2`, `ways(3)=2+1+1=4`, `ways(4) = ways(3) + ways(2) + ways(1) = 4 + 2 + 1 = 7`. Answer **7**. (This is the Tribonacci recurrence with a different start; the read window is 3, so three variables suffice.)

</details>

**Exercise 2.** In House Robber, a student defines `rob(i)` = "most money if we **must** rob house `i`". Is that a valid state? What would the recurrence and the final answer be?

<details><summary>Answer</summary>

It is valid, but it changes everything downstream. If house `i` is robbed, the previous robbed house is some `j ≤ i - 2`, so `rob(i) = nums[i] + max(0, rob(0), ..., rob(i-2))`. The final answer is no longer a single cell: it is `max(rob(n-1), rob(n-2))`, because the last robbed house is one of the last two. It still works (and can be made O(n) by keeping a running maximum), but it is more complicated. That is why Q2 matters: the "best from houses 0..i, robbing or not" state gives a two-term recurrence and the answer in one cell.

</details>

**Exercise 3.** CSES has a related problem, Coin Combinations II, where `1+2` and `2+1` count as the **same** way. Why is `ways(s) = Σ ways(s - coin)` wrong for it? Check with coins `{1, 2}` and `s = 3`.

<details><summary>Answer</summary>

The recurrence groups sequences by their **last** element, so it counts every **order** separately. With coins `{1, 2}`: `ways(0)=1, ways(1)=1, ways(2)=2, ways(3)=ways(2)+ways(1)=3`, counting `1+1+1`, `1+2`, `2+1`. But the unordered answer is **2** (`{1,1,1}` and `{1,2}`). To count unordered combinations, the state must also remember **which coins we are still allowed to use** (for example `ways(i, s)` using only coins `i..k-1`), so each combination is built in one fixed order. Q2 is where this decision is made.

</details>

**Exercise 4.** For each problem, say "DP", "probably not DP", and why: (a) find the largest number in an array; (b) count the number of ways to climb `n` stairs when you may take 1, 3 or 5 steps; (c) find the minimum number of coins for an amount, coins `[1, 3, 4]`.

<details><summary>Answer</summary>

- (a) **Not DP.** No decisions, no subproblems: one pass with a running maximum.
- (b) **DP.** "Count the ways", the last step is a choice among 1, 3, 5, and `ways(n - k)` repeats: `ways(n) = ways(n-1) + ways(n-3) + ways(n-5)`, `ways(0) = 1`.
- (c) **DP.** It looks greedy ("take the biggest coin"), but for amount 6 greedy gives `4+1+1` (3 coins) while `3+3` uses 2. Because greedy fails, use `best(a) = 1 + min over coins c ≤ a of best(a - c)`, `best(0) = 0`.

</details>

---

⬅ Previous: [15 · Space Optimization Basics](../15-space-optimization-basics/) · ➡ Next: [01 · Fibonacci Number](../../01-one-dimensional-dp/01-fibonacci-number/) · 🏠 [README](README.md)
