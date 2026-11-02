# Overlapping Subproblems — Walkthrough

> Goal: learn to *see* and *measure* repeated work in a recursion, and to recognise when there is none. This is the property that makes caching (DP) worthwhile.

---

## 1. Intuition (no code, no math — an everyday analogy)

### The forgetful tour guide

A tour guide is asked by each tourist, one by one, *"How far is it from here to the castle?"* The guide walks to the castle with a measuring wheel every single time. Fifty tourists, fifty walks — all to measure **the same distance**.

That is an overlapping subproblem: the *same question* (distance from here to the castle) comes up again and again. The obvious fix is to measure once and remember. That fix is DP.

### The jigsaw puzzle crew

Now picture four friends building a 1,000-piece puzzle. They split the pieces into four separate piles; each friend assembles one corner, then they join the corners. Nobody ever works on someone else's pieces. There is nothing to remember, because **no question is ever asked twice**. Splitting work into *separate* parts is "divide and conquer" — recursion without overlap. Remembering answers would not make it faster.

The skill this topic teaches is telling these two situations apart.

---

## 2. Problem statement in our own words

Given a recursive function, find out:

1. Which subproblems (argument values) does it solve **more than once**?
2. **How many times** is each one solved?
3. How does the total number of calls compare with the number of **distinct** subproblems?

We answer these for Fibonacci (lots of overlap), for take/skip subset counting (overlap through different choices), and for merge sort (no overlap). Code: [`solution/CallCounter.java`](solution/CallCounter.java).

---

## 3. Examples (input → output, with an explanation)

| Recursion | Total calls | Distinct subproblems | Overlap? |
|-----------|-------------|----------------------|----------|
| `fib(6)` | 25 | 7 (`fib(0)` … `fib(6)`) | Yes — 18 calls repeat earlier work |
| `fib(20)` | 21,891 | 21 | Yes — `fib(1)` alone is computed 6,765 times |
| merge sort, 8 elements | 15 | 15 ranges | No — every range is sorted exactly once |

---

## 4. Deriving the solution — the 7 questions

Here the "problem" is the analysis itself, so we run the questions on `fib` with an eye on repeats.

### 4.1 What is changing?
The argument `n`. It is the *only* thing that identifies a subproblem.

### 4.2 What does the state represent?
`fib(k)` = the k-th Fibonacci number. Because the state is just one number between 0 and n, there are only **n + 1 distinct states**.

### 4.3 What are the choices?
None to make; each call always asks for `fib(k-1)` and `fib(k-2)`.

### 4.4 What is the recurrence?
`fib(k) = fib(k-1) + fib(k-2)`. Because **both** `fib(k)` and `fib(k-1)` ask for `fib(k-2)` (one directly, one through `fib(k-1)`'s own call), the same states are requested from different places — that is the overlap.

### 4.5 What are the base cases?
`fib(0) = 0`, `fib(1) = 1`. They are cheap, but even they are called thousands of times for large n.

### 4.6 What is the order of computation?
Recursion visits states in a messy order and repeatedly. The fix in topics [06](../06-memoization/) and [07](../07-tabulation/) is to compute each state once, in increasing order.

### 4.7 Can the state space be optimized?
The key number is: **n + 1 distinct states vs. ~2ⁿ calls**. Any approach that computes each state once will cost O(n). That gap is exactly what DP removes.

---

## 5. Brute force: recursion

### 5.1 Spotting repeated nodes in the `fib(6)` tree

Here is the full recursion tree of `fib(6)` (sideways: children are indented under their parent). Every call to `fib(k)` with `k ≥ 2` that was **already computed earlier in the tree** is marked:

```
fib(6)
|  fib(5)
|  |  fib(4)
|  |  |  fib(3)
|  |  |  |  fib(2)
|  |  |  |  |  fib(1)
|  |  |  |  |  fib(0)
|  |  |  |  fib(1)
|  |  |  fib(2)   <-- REPEAT #1 of fib(2)
|  |  |  |  fib(1)
|  |  |  |  fib(0)
|  |  fib(3)   <-- REPEAT #1 of fib(3)
|  |  |  fib(2)   <-- REPEAT #2 of fib(2)
|  |  |  |  fib(1)
|  |  |  |  fib(0)
|  |  |  fib(1)
|  fib(4)   <-- REPEAT #1 of fib(4)
|  |  fib(3)   <-- REPEAT #2 of fib(3)
|  |  |  fib(2)   <-- REPEAT #3 of fib(2)
|  |  |  |  fib(1)
|  |  |  |  fib(0)
|  |  |  fib(1)
|  |  fib(2)   <-- REPEAT #4 of fib(2)
|  |  |  fib(1)
|  |  |  fib(0)
```

Look at the **second half**: the whole `fib(4)` subtree (9 nodes) under the root is an exact copy of the `fib(4)` subtree computed earlier inside `fib(5)`. Everything in it is wasted work.

The same picture drawn top-down, with repeats in brackets:

```
                                   fib(6)
                   ┌─────────────────┴──────────────────┐
                 fib(5)                              [fib(4)]  <- whole subtree repeated
          ┌────────┴─────────┐                     ┌────┴─────┐
        fib(4)            [fib(3)]               fib(3)     fib(2)
      ┌───┴────┐          ┌──┴──┐               ┌──┴──┐     ┌─┴─┐
    fib(3)  [fib(2)]    fib(2) fib(1)         fib(2) fib(1) f(1) f(0)
   ┌──┴──┐   ┌─┴─┐      ┌─┴─┐                 ┌─┴─┐
 fib(2) f(1) f(1) f(0)  f(1) f(0)             f(1) f(0)
 ┌─┴─┐
f(1) f(0)
```

### 5.2 Counting calls per argument

Spotting repeats by eye stops working for large trees. Instead, add a counter array to the function: `callsFor[k]++` at the start of every call to `fib(k)`.

```java
// callsFor[k] = number of times fib(k) was entered
private static long[] callsFor;

static long countedFib(int n) {
    callsFor[n]++;
    if (n <= 1) return n;
    return countedFib(n - 1) + countedFib(n - 2);
}
```

Running `java CallCounter.java` prints:

```
fib(6): how many times each subproblem is computed
  k  | calls to fib(k)
 ----+----------------
  6  | 1
  5  | 1
  4  | 2
  3  | 3
  2  | 5
  1  | 8
  0  | 5
  total calls = 25
```

### 5.3 Reading the table

| k | Calls to `fib(k)` | Needed | Wasted |
|---|-------------------|--------|--------|
| 6 | 1 | 1 | 0 |
| 5 | 1 | 1 | 0 |
| 4 | 2 | 1 | 1 |
| 3 | 3 | 1 | 2 |
| 2 | 5 | 1 | 4 |
| 1 | 8 | 1 | 7 |
| 0 | 5 | 1 | 4 |
| **total** | **25** | **7** | **18** |

Two patterns stand out:

1. **The counts are Fibonacci numbers themselves** (1, 1, 2, 3, 5, 8). Why? `fib(k)` is called once by every call to `fib(k+1)` and once by every call to `fib(k+2)`, so `calls(k) = calls(k+1) + calls(k+2)` — the Fibonacci rule running backwards. (`fib(0)` is the exception: only `fib(2)` calls it, so `calls(0) = calls(2) = 5`.)
2. **The smaller the subproblem, the more often it is recomputed.** For `fib(20)`, `fib(1)` is computed 6,765 times; for `fib(50)`, over 12 billion times.

So for `fib(6)` only **7 of 25** calls do new work. For `fib(30)`, only 31 of 2,692,537. The overlap gets worse as n grows.

### 5.4 Overlap through different choices (take/skip)

Overlap is not special to Fibonacci. Any recursion where **different sequences of choices can lead to the same state** has it. Take `countSubsetsWithSum([1, 2, 3, 4], target = 5)` from [topic 03](../03-recursion-trees-and-brute-force/). The state is `(index, remaining)`.

```
choices for 1, 2, 3        sum so far    state reached (index, remaining)
take 1, take 2, skip 3        3              (3, 2)
skip 1, skip 2, take 3        3              (3, 2)    <-- the same state!
```

Two different paths arrive at "decide element 4 with 2 still missing". From there the future is identical — the brute force solves `(3, 2)` twice. With more elements and small numbers, many paths share states, and caching pays off. (This becomes [Subset Sum](../../03-subsequence-and-knapsack-dp/03-subset-sum/) in Module 03.)

The general rule:

> **Overlap appears when the state forgets *how* you got there.** Fibonacci's state (`n`) and the subset state (`index`, `remaining`) do not record the path, so different paths collapse into the same state.

### 5.5 Contrast: merge sort has no overlap

Merge sort splits the array into a left half and a right half, sorts each recursively, and merges them. The subproblems are **index ranges**:

```
                         [0..7]
              ┌────────────┴────────────┐
           [0..3]                     [4..7]
        ┌─────┴─────┐              ┌─────┴─────┐
     [0..1]       [2..3]        [4..5]       [6..7]
     ┌─┴─┐        ┌─┴─┐         ┌─┴─┐        ┌─┴─┐
   [0] [1]      [2] [3]       [4] [5]      [6] [7]
```

Every range appears **exactly once**. The left half and the right half share no elements, so no range can ever be reached from two places. `CallCounter.java` counts calls per `(lo, hi)` and confirms it:

```
merge sort on 8 elements: 15 distinct ranges, each sorted at most 1 time(s)
```

A cache for merge sort would be filled and never read. That is why merge sort is "divide and conquer", not DP.

| | Fibonacci | Merge sort |
|---|-----------|-----------|
| Subproblems | `fib(n-1)`, `fib(n-2)` — **share** `fib(n-3)`, `fib(n-4)`… | left half, right half — **disjoint** |
| Same subproblem reached twice? | Yes, many times | Never |
| Total calls vs distinct subproblems | ~2ⁿ vs n + 1 | 2n − 1 vs 2n − 1 |
| Does caching help? | Hugely (exponential → linear) | Not at all |

---

## 6. Memoization (top-down)

The obvious fix for the waste in the table: store each `fib(k)` the first time, return it on every later call. Each subproblem is then computed once: 7 computations instead of 25 calls. Topic [06 — Memoization](../06-memoization/) does exactly this.

## 7. Tabulation (bottom-up)

Alternatively, compute the 7 distinct subproblems in order `fib(0), fib(1), …, fib(6)` with a loop. Topic [07 — Tabulation](../07-tabulation/).

## 8. Space optimization

Not part of this topic ([topic 15](../15-space-optimization-basics/)).

---

## 9. Dry run on a full example

Let us trace the **first** time each subproblem is finished versus how many times it is requested, for `fib(5)`, step by step in call order. "New" means the value is computed for the first time in the whole run; "Again" means the same value was already computed earlier.

| # | Call | New or again? | Returns |
|---|------|---------------|---------|
| 1 | fib(5) | new | 5 |
| 2 | fib(4) | new | 3 |
| 3 | fib(3) | new | 2 |
| 4 | fib(2) | new | 1 |
| 5 | fib(1) | new (base) | 1 |
| 6 | fib(0) | new (base) | 0 |
| 7 | fib(1) | again | 1 |
| 8 | fib(2) | **again** | 1 |
| 9 | fib(1) | again | 1 |
| 10 | fib(0) | again | 0 |
| 11 | fib(3) | **again** | 2 |
| 12 | fib(2) | again | 1 |
| 13 | fib(1) | again | 1 |
| 14 | fib(0) | again | 0 |
| 15 | fib(1) | again | 1 |

Only calls 1–6 are new. From call 7 onward **every** call recomputes something already known. A cache would turn call 8 and call 11 into one-step lookups, and calls 9–10 and 12–15 would never happen.

---

## 10. Complexity analysis

The useful numbers for any recursion:

```
total calls            = how long brute force takes
distinct subproblems   = the minimum work if each one is solved once
work per subproblem    = how much each one costs (excluding recursive calls)

DP time ≈ distinct subproblems × work per subproblem
```

| Recursion | Total calls (brute force) | Distinct subproblems | Work each | Time with DP |
|-----------|---------------------------|----------------------|-----------|--------------|
| `fib(n)` | 2·fib(n+1) − 1 = O(2ⁿ) | n + 1 | O(1) | **O(n)** |
| `countSubsetsWithSum` (n items, target T) | O(2ⁿ) | at most (n + 1)·(T + 1) | O(1) | **O(n·T)** |
| merge sort | 2n − 1 | 2n − 1 | — | no change: O(n log n) either way |

The **ratio** between total calls and distinct subproblems tells you how much DP can save. For Fibonacci it is exponential; for merge sort it is 1.

The counter itself costs O(n) extra memory and O(1) per call, so measuring does not change the brute force's complexity.

---

## 11. Why this approach works

Counting calls per argument is a direct measurement: every call runs `callsFor[k]++` exactly once, so the array holds the exact number of times each subproblem was solved. Since a subproblem's answer depends only on its arguments ([topic 01, section 11](../01-what-is-dynamic-programming/walkthrough.md#11-why-this-approach-works)), every call after the first recomputes an answer we already had. Hence "calls − distinct subproblems" is exactly the wasted work.

---

## 12. Common mistakes

- **Confusing "recursive" with "overlapping".** Recursion alone does not mean DP. Merge sort, binary search and tree traversals are recursive with no overlap.
- **Thinking subproblems must be called by the same parent to overlap.** In `fib`, `fib(3)` is requested by `fib(5)` and by `fib(4)` — different parents. Overlap is about the same *arguments*, from anywhere in the tree.
- **Including path information in the state.** If your recursion carries the whole list of choices made so far (e.g. a `List<Integer>` of taken items), no two calls ever have the same arguments, so nothing overlaps and caching cannot help. Keep only what the *future* needs (`index`, `remaining`).
- **Measuring with a global counter only.** A single total tells you it is slow, not *why*. Count per argument to see which subproblems repeat.
- **Assuming "generate all subsets" can be sped up with DP.** When the output itself has 2ⁿ items, you must produce all of them; overlap in the *counting* version does not help the *listing* version.

---

## 13. When to use this technique / how to recognize similar problems

Ask: **"Can two different sequences of decisions lead to the same remaining situation?"** If yes, subproblems overlap.

- "Number of ways to reach step n" — reaching step 5 via 1+2+2 or 2+1+2 leaves the same situation.
- "Pick items with total weight ≤ W" — different picks can leave the same remaining capacity.
- "Edit one string into another" — different edit sequences can reach the same pair of positions.
- Divide and conquer that splits into **disjoint** parts (sorting, searching, tree traversal) — no overlap; do not use DP.

---

## 14. Summary and revision notes

- **Overlapping subproblems:** the recursion requests the same subproblem (same arguments) more than once.
- Measure it with a **per-argument call counter**.
- `fib(6)`: 25 calls, 7 distinct subproblems. Counts per k are 1, 1, 2, 3, 5, 8, 5.
- Overlap arises when the state forgets the path (different choices → same state).
- Merge sort: disjoint halves, each range sorted once → no overlap → DP useless.
- DP time ≈ (distinct subproblems) × (work per subproblem).

```
┌────────────────────────────────────────────────────────────────────┐
│  calls(k) inside fib(n):  calls(k) = calls(k+1) + calls(k+2)       │
│  fib(n): ~2^n calls  but only  n + 1  distinct subproblems         │
└────────────────────────────────────────────────────────────────────┘
```

---

## Check your understanding

1. In the recursion tree of `fib(7)`, how many times is `fib(5)` computed? `fib(4)`? `fib(3)`?
2. Binary search on a sorted array is recursive. Does it have overlapping subproblems? Explain.
3. A brute force for "count the ways to reach stair n with steps of 1, 2 or 3" passes the full list of steps taken so far as a parameter. Why would adding a cache keyed on all the parameters not help? What should the state be instead?
4. For `countSubsetsWithSum([2, 2, 2], target = 2)`, find two different take/skip paths that reach the same state `(index, remaining)`.

<details>
<summary>Answers</summary>

1. Counts follow the Fibonacci pattern from the top: `fib(7)`:1, `fib(6)`:1, `fib(5)`:**2**, `fib(4)`:**3**, `fib(3)`:**5**.
2. No. Each call looks at either the left half or the right half, never both, so the recursion is a single path (O(log n) calls) and no range is ever visited twice.
3. Two different step sequences have different lists, so every call has unique arguments and the cache never hits. The future only depends on **which stair you are on**, so the state should be just `n` (stairs remaining). Then 1+2 and 2+1 both reach "stair 3" and share one cached answer.
4. "take first 2, skip second 2" → `(2, 0)`; "skip first 2, take second 2" → `(2, 0)`. Same state: index 2, nothing missing. (So does nothing else: "skip, skip" gives `(2, 2)`.)

</details>

---

⬅ [03 — Recursion Trees and Brute Force](../03-recursion-trees-and-brute-force/) · ➡ [05 — Optimal Substructure](../05-optimal-substructure/) · 📘 [Topic README](README.md)
