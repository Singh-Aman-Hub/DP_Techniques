# Memoization — Walkthrough

> Goal: take a slow, correct recursion and make it fast by remembering answers. You will learn the three-step pattern, see the pruned recursion tree, dry-run every cache hit, and learn the bugs that make memoized code silently slow or wrong.

---

## 1. Intuition (no code, no math — an everyday analogy)

### The sticky notes on the fridge

A child keeps asking a parent, *"How many days until my birthday?"* The first time, the parent opens the calendar and counts: 47 days. Being clever, the parent writes **"Birthday: 47 days (asked on Monday)"** on a sticky note and puts it on the fridge. The next time the question comes (on the same day), the parent glances at the fridge and answers instantly.

Three things make this work:

1. **A place to store answers** — the fridge. (In code: the **cache** or **memo**, an array.)
2. **A way to tell a missing note from a real one** — an empty spot on the fridge. (In code: a **sentinel** value such as `-1` in empty cells.)
3. **A habit: look before you work** — always check the fridge before opening the calendar. (In code: the **check-before-compute** pattern.)

The word *memoization* comes from "memo" — a written note to yourself — not from "memorization".

---

## 2. Problem statement in our own words

Compute the n-th Fibonacci number `fib(n)` (`fib(0) = 0`, `fib(1) = 1`, `fib(n) = fib(n-1) + fib(n-2)`) **fast**, starting from the plain recursion of [topic 02](../02-recursion-fundamentals/) and changing as little as possible. Same problem as [LeetCode 509 — Fibonacci Number](https://leetcode.com/problems/fibonacci-number/).

Code: [`solution/Memoization.java`](solution/Memoization.java) (with a trace of every cache hit) and [`solution/MemoBugs.java`](solution/MemoBugs.java) (common bugs next to their fixes).

---

## 3. Examples (input → output, with an explanation)

| Input | Output | Calls (plain recursion) | Calls (memoized) |
|-------|--------|-------------------------|------------------|
| `fib(0)` | 0 | 1 | 1 |
| `fib(6)` | 8 | 25 | 11 |
| `fib(10)` | 55 | 177 | 19 |
| `fib(50)` | 12,586,269,025 | ≈ 40.7 billion | 99 |
| `fib(90)` | 2,880,067,194,370,816,120 | (centuries) | 179 |

---

## 4. Deriving the solution — the 7 questions

### 4.1 What is changing?
Only `n`.

### 4.2 What does the state represent?
`fib(k)` = the k-th Fibonacci number. **The state is what the cache is indexed by**, so `memo[k]` will hold `fib(k)`.

### 4.3 What are the choices?
None; each state always combines `fib(k-1)` and `fib(k-2)`.

### 4.4 What is the recurrence?
`fib(k) = fib(k-1) + fib(k-2)`. Memoization does **not** change the recurrence at all. It only changes *how often* each state is evaluated.

### 4.5 What are the base cases?
`fib(0) = 0`, `fib(1) = 1`. These are returned directly and are not even stored (storing them would also be fine).

### 4.6 What is the order of computation?
With memoization you **do not choose** an order: the recursion computes states in whatever order it needs them, and the cache guarantees each is computed once. (In tabulation, [topic 07](../07-tabulation/), you must choose the order yourself.)

### 4.7 Can the state space be optimized?
The memo needs one cell per state: n + 1 cells. Removing the cache entirely needs a bottom-up loop ([topic 08](../08-top-down-vs-bottom-up/)).

---

## 5. Brute force: recursion

Our starting point, unchanged from earlier topics:

```java
static long fib(int n) {
    if (n <= 1) return n;
    return fib(n - 1) + fib(n - 2);
}
```

Its tree for `fib(6)` has 25 nodes, and 18 of them recompute something already computed ([topic 04](../04-overlapping-subproblems/)). Time O(2ⁿ).

---

## 6. Memoization (top-down)

### 6.1 The three-step recipe

To memoize **any** recursive function:

1. **Create a cache** with one cell per possible state, filled with a sentinel. Create it **once**, outside the recursion.
2. At the start of the function (after the base cases): **if the cache has the answer, return it.**
3. Where you used to `return` the computed answer: **store it in the cache first**, then return it.

Applied to Fibonacci:

```java
// memo[k] stores fib(k) once computed; -1 means "not computed yet".
// -1 is safe because every Fibonacci number is >= 0.
private static long[] memo;

static long fib(int n) {
    memo = new long[n + 1];           // step 1: a fresh cache for each new top-level question
    Arrays.fill(memo, -1);
    return solve(n);
}

private static long solve(int n) {
    if (n <= 1) return n;                       // base case
    if (memo[n] != -1) return memo[n];          // step 2: check before computing
    memo[n] = solve(n - 1) + solve(n - 2);      // step 3: compute once, store
    return memo[n];
}
```

Compare with the brute force: the recursive line is the same. We added one array, one `if`, and one assignment.

### 6.2 The cache

The cache is an array whose **index is the state** and whose **value is the answer** for that state.

```
index k   :   0    1    2    3    4    5    6
memo[k]   :  -1   -1   -1   -1   -1   -1   -1      <- before: everything "unknown"
memo[k]   :  -1   -1    1    2    3    5    8      <- after fib(6)
```

`memo[0]` and `memo[1]` stay `-1` because the base cases return before touching the cache. That is fine.

When the state is not a small non-negative integer (very large numbers, strings, several values that do not fit an array nicely), use a `HashMap<Key, Long>` instead: `if (memo.containsKey(n)) return memo.get(n);`. Arrays are faster and simpler whenever the states fit.

For a state with two parameters, the cache is a 2-D array, `memo[i][j]`, and so on. One cell per distinct state.

### 6.3 The sentinel

The cache needs a way to say "I don't know this yet". We fill it with a value that can **never** be a real answer:

| Problem's answers are… | Safe sentinel | Why |
|------------------------|---------------|-----|
| Counts, lengths, costs ≥ 0 (Fibonacci, ways, min cost) | `-1` | No real answer is negative. |
| Any integer, including negative | `Long.MIN_VALUE`, or a separate `boolean[] computed` | No natural "impossible" value exists. |
| `true` / `false` | `Boolean` object with `null`, or an `int` array with -1 / 0 / 1 | A plain `boolean[]` has only two values, both real answers. |

Java fills new `long[]` arrays with **0**, so if you forget `Arrays.fill(memo, -1)`, the sentinel silently becomes 0. For Fibonacci that happens to be almost harmless; for many problems it is a performance disaster (section 12, bug 2).

### 6.4 The check-before-compute pattern

Order matters inside the function:

```
1. base case?              -> return the known answer
2. memo[state] != EMPTY?   -> return memo[state]           (cache hit)
3. answer = recurrence      (recursive calls)              (cache miss)
4. memo[state] = answer
5. return answer
```

Checking the cache *after* the recursive calls would make it useless. Forgetting step 4 makes the cache stay empty forever.

### 6.5 The pruned recursion tree

Here is the plain tree of `fib(6)` (25 nodes). With memoization, every subtree whose root has already been computed is cut off and replaced by a single cache hit (`HIT`):

```
                               fib(6)
                      ┌──────────┴───────────┐
                    fib(5)                fib(4) HIT  ✂ (9-node subtree removed)
               ┌──────┴───────┐
             fib(4)        fib(3) HIT  ✂ (5-node subtree removed)
           ┌───┴────┐
         fib(3)   fib(2) HIT  ✂ (3-node subtree removed)
        ┌──┴──┐
      fib(2) fib(1)
     ┌──┴──┐
   fib(1) fib(0)
```

What is left is a **chain** down the left side (each `fib(k)` computed once) with one short stub to the right per level (a cache hit or a base case). Count: 5 computed (`fib(6)`…`fib(2)`), 3 hits, 3 base-case calls = **11 calls** instead of 25. Removed: 9 + 5 + 3 = 17 nodes, minus the 3 hit nodes that remain = 14 fewer calls.

---

## 7. Tabulation (bottom-up)

Not part of this topic. The same cache, filled with a loop from `memo[0]` upward instead of by recursion, is tabulation: [topic 07](../07-tabulation/).

## 8. Space optimization

Not for memoization: the recursion needs the cache to find answers in any order. Space optimization works with bottom-up loops ([topic 08](../08-top-down-vs-bottom-up/), [topic 15](../15-space-optimization-basics/)).

---

## 9. Dry run on a full example

`java Memoization.java` prints every call of `fib(6)`, indented by depth:

```
fib(6) not cached, computing...
  fib(5) not cached, computing...
    fib(4) not cached, computing...
      fib(3) not cached, computing...
        fib(2) not cached, computing...
          fib(1) base case = 1
          fib(0) base case = 0
        fib(2) stored memo[2] = 1
        fib(1) base case = 1
      fib(3) stored memo[3] = 2
      fib(2) CACHE HIT = 1
    fib(4) stored memo[4] = 3
    fib(3) CACHE HIT = 2
  fib(5) stored memo[5] = 5
  fib(4) CACHE HIT = 3
fib(6) stored memo[6] = 8
memo after fib(6): [-1, -1, 1, 2, 3, 5, 8]
```

The same run as a table. The memo column shows cells 2..6 after the step.

| # | Call | What happens | memo[2..6] after |
|---|------|--------------|------------------|
| 1 | fib(6) | miss → needs fib(5), fib(4) | `- - - - -` |
| 2 | fib(5) | miss → needs fib(4), fib(3) | `- - - - -` |
| 3 | fib(4) | miss → needs fib(3), fib(2) | `- - - - -` |
| 4 | fib(3) | miss → needs fib(2), fib(1) | `- - - - -` |
| 5 | fib(2) | miss → needs fib(1), fib(0) | `- - - - -` |
| 6 | fib(1) | base case → 1 | `- - - - -` |
| 7 | fib(0) | base case → 0 | `- - - - -` |
|   | fib(2) | 1 + 0 = 1, **store** | `1 - - - -` |
| 8 | fib(1) | base case → 1 | `1 - - - -` |
|   | fib(3) | 1 + 1 = 2, **store** | `1 2 - - -` |
| 9 | fib(2) | **HIT** → 1 | `1 2 - - -` |
|   | fib(4) | 2 + 1 = 3, **store** | `1 2 3 - -` |
| 10 | fib(3) | **HIT** → 2 | `1 2 3 - -` |
|   | fib(5) | 3 + 2 = 5, **store** | `1 2 3 5 -` |
| 11 | fib(4) | **HIT** → 3 | `1 2 3 5 -` |
|   | fib(6) | 5 + 3 = 8, **store** | `1 2 3 5 8` |

Notice the pattern: the recursion first dives straight down the left side (calls 1–7), then fills the cache on the way back up. Every right-hand request (`fib(k-2)`) arrives **after** `fib(k-1)` has already computed and stored it, so it is always a hit.

### A second example: Climbing Stairs

The same recipe works for any recursion. Counting the ways to climb `n` stairs with 1- or 2-steps ([topic 05](../05-optimal-substructure/)):

```java
private static long[] memo;   // memo[i] = ways to climb i stairs; -1 = unknown

static long ways(int n) {
    if (n <= 1) return 1;                       // base cases: 0 or 1 stair → 1 way
    if (memo[n] != -1) return memo[n];          // check
    memo[n] = ways(n - 1) + ways(n - 2);        // compute and store
    return memo[n];
}
```

Three lines changed from the brute force; [LeetCode 70](https://leetcode.com/problems/climbing-stairs/) goes from "Time Limit Exceeded" to instant.

---

## 10. Complexity analysis

**The rule for every memoized solution:**

```
time  = (number of distinct states) × (work per state, not counting recursive calls)
space = (cache size) + (maximum recursion depth)
```

Why? Each state is computed at most once (the first call misses, every later call hits). A hit costs O(1). So the total work is the work for each state, once.

**Fibonacci, exactly:**

- States computed: `fib(2) … fib(n)` → n − 1 states, each doing one addition: **O(n)**.
- Total calls: each computed state makes 2 calls, and the root is 1 call: 1 + 2(n − 1) = **2n − 1** calls (11 for n = 6, 179 for n = 90 — checked by the program).
- Cache: n + 1 cells. Recursion depth: n (the left chain). **Space O(n).**

| | Plain recursion | Memoized |
|---|----------------|----------|
| Calls for fib(n) | 2·fib(n+1) − 1 ≈ 1.618ⁿ | 2n − 1 |
| Time | O(2ⁿ) | **O(n)** |
| Space | O(n) stack | **O(n)** cache + O(n) stack |

The price of the speed-up is O(n) extra memory for the cache — an excellent trade.

---

## 11. Why this approach works

A subproblem's answer depends only on its state (here `n`), never on who asks or when. So the first computed value of `fib(k)` is the value every later call would compute. Returning the stored value therefore gives exactly the same result as recomputing — memoization cannot change any answer, only the amount of work. Every state is computed at most once because after its first computation the cache cell is no longer the sentinel, so all later calls return at step 2.

---

## 12. Common mistakes

`MemoBugs.java` demonstrates the first three. The scary part: **the answers stay correct** in bugs 1 and 2, so tests pass on small inputs while the program is as slow as brute force.

### Bug 1: the cache is reset inside the recursion

```java
static long fibResetBug(int n) {
    long[] memo = new long[n + 1];       // BUG: a brand-new empty cache in every call
    Arrays.fill(memo, -1);
    if (n <= 1) return n;
    if (memo[n] != -1) return memo[n];   // never true
    memo[n] = fibResetBug(n - 1) + fibResetBug(n - 2);
    return memo[n];
}
```

Each call has its own private, empty cache, so no answer is ever shared. Program output: `reset-cache fib(20) calls (no reuse at all) -> 21891` — exactly the brute-force count — versus `39` with one shared cache.

**Fix:** create the cache once, in a wrapper function or as a field, and pass/share it. A related bug in platforms like LeetCode: a `static` cache that is **not** reset between test cases, so answers from a previous input (e.g. a different array) leak into the next one. Reset the cache once per top-level question.

### Bug 2: a sentinel that is also a real answer

`jumpWays(n)` counts the ordered ways to cover exactly `n` stairs with jumps of 4 or 6. For odd `n`, **every** answer is 0 — a real answer.

```java
if (jumpMemo[n] != 0) return jumpMemo[n];   // BUG: 0 means both "unknown" and "zero ways"
```

States whose true answer is 0 look "unknown" forever and are recomputed every time. Program output for `n = 41`: **929 calls** with sentinel 0, **41 calls** with sentinel −1. With bigger `n` the gap grows exponentially.

**Fix:** use a sentinel outside the range of real answers (`-1` for counts), or a separate `boolean[] computed`.

### Bug 3: integer overflow

```java
static int fibInt(int n) { ... intMemo[n] = fibInt(n - 1) + fibInt(n - 2); ... }
```

`fib(46) = 1,836,311,903` fits in an `int` (max 2,147,483,647), but `fib(47) = 2,971,215,073` does not. Java does not raise an error; the sum wraps around to **−1,323,752,223**. The program prints both and checks that `long` gives the correct value.

**Fix:** use `long` for the cache and the arithmetic (`long` holds up to `fib(92)`). When a problem says "return the answer modulo 10⁹ + 7", take `% MOD` after every addition, and still use `long` for intermediate products.

### More bugs to watch for

| Mistake | Effect | Fix |
|---------|--------|-----|
| Forgetting to store (`return solve(n-1) + solve(n-2);` with no `memo[n] =`) | Cache never fills; brute-force speed | Always assign before returning. |
| Checking the cache after the recursive calls | The work is already done | Check right after the base cases. |
| Cache key missing a parameter (state is `(i, remaining)` but cache is `memo[i]`) | **Wrong answers**: different states overwrite each other | One cache dimension per parameter that changes. |
| Cache too small (`new long[n]` but `memo[n]` is used) | `ArrayIndexOutOfBoundsException` | Size = largest state + 1. |
| Recursion depth too large (n = 10⁶) | `StackOverflowError` even though the memo is correct | Use tabulation ([topic 07](../07-tabulation/)) or a bigger stack. |

---

## 13. When to use this technique / how to recognize similar problems

Use memoization when:

- you already have a correct recursive brute force and the subproblems overlap ([topic 04](../04-overlapping-subproblems/));
- the order in which states must be computed is awkward to work out by hand (the recursion figures it out for you);
- only a fraction of all possible states is actually reachable (the cache only fills reachable ones).

Be careful when the recursion depth can reach hundreds of thousands — prefer tabulation there. [Topic 08](../08-top-down-vs-bottom-up/) compares the two in detail, and [topic 13](../13-converting-recursion-to-memoization/) turns the recipe of section 6.1 into a checklist you can apply to any problem.

---

## 14. Summary and revision notes

- Memoization = **recursion + cache**. Top-down: start from the big question.
- Three steps: **cache filled with a sentinel (once)** → **check before compute** → **store before return**.
- Sentinel must never be a real answer (`-1` for non-negative answers).
- Pruned tree: each state computed once; repeated subtrees become O(1) hits.
- Time = states × work per state; memoized `fib(n)`: 2n − 1 calls, O(n) time, O(n) space.
- Bugs: cache recreated per call, sentinel = real answer, `int` overflow, missing store, missing cache dimension.

```
┌─────────────────────────────────────────────────────┐
│  solve(n):                                          │
│     if n <= 1:          return n                    │
│     if memo[n] != -1:   return memo[n]              │
│     memo[n] = solve(n-1) + solve(n-2)               │
│     return memo[n]                                  │
└─────────────────────────────────────────────────────┘
```

---

## Check your understanding

1. How many total calls does memoized `fib(10)` make? How many of them are cache hits?
2. You memoize a function whose answers can be any integer, positive or negative, and use `-1` as the sentinel. Describe exactly what goes wrong and when.
3. A function `count(i, remaining)` is memoized with `long[] memo = new long[n + 1]` indexed by `i` only. What kind of bug is this, and is it a speed bug or a correctness bug?
4. Why are `memo[0]` and `memo[1]` still `-1` after memoized `fib(6)` finishes? Is that a problem?

<details>
<summary>Answers</summary>

1. 2·10 − 1 = **19** calls. Computed states: `fib(2)…fib(10)` = 9. Hits: the right-hand call `fib(k-2)` for k = 4…10 = **7**. Base-case calls: `fib(1)` and `fib(0)` from `fib(2)`, and `fib(1)` from `fib(3)` = 3. Check: 9 + 7 + 3 = 19.
2. Any state whose real answer is `-1` is stored as `-1`, which looks "unknown", so that state is recomputed on every call — a speed bug that can bring back exponential time. The answers remain correct. Use `Long.MIN_VALUE` (if it can never be a real answer) or a separate `boolean[] computed`.
3. A missing cache dimension. Different states `(i, r1)` and `(i, r2)` share one cell, so a call can return the answer of a *different* state. It is a **correctness** bug: answers will be wrong. Use `memo[i][remaining]`.
4. The base cases `n <= 1` return before the cache is checked or written, so those cells are never touched. It is not a problem: those states are answered directly anyway.

</details>

---

⬅ [05 — Optimal Substructure](../05-optimal-substructure/) · ➡ [07 — Tabulation](../07-tabulation/) · 📘 [Topic README](README.md)
