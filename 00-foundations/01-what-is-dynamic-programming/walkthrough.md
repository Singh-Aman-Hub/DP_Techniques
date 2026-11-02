# What is Dynamic Programming? — Walkthrough

> This is your first contact with DP. Nothing is assumed except basic Java. Every new word is explained the first time it appears. Take your time; the ideas here come back in every later topic.

---

## 1. Intuition (no code, no math — an everyday analogy)

### Analogy 1: the maths homework

A teacher asks you: *"What is 1 + 1 + 1 + 1 + 1 + 1 + 1 + 1?"* You count and say **8**.

The teacher writes another **+ 1** at the end and asks again. You do not count all nine ones from the start. You say **9** immediately, because you *remembered* the previous answer (8) and only added one more.

That is dynamic programming in one sentence:

> **Do not solve the same smaller problem twice. Solve it once, remember the answer, and reuse it.**

### Analogy 2: the family tree question

Imagine you are asked "how many ancestors do I have going back 10 generations?" and you ask your parents, who ask their parents, and so on. Your mother's side and your father's side are different people, so no work repeats. That is ordinary recursion and it is fine.

Now imagine a village where everyone keeps asking the *same* elders the *same* question. The elder gets asked "how old is the village well?" 500 times and walks to the archive 500 times to check. A smart elder writes the answer on a board by the door after the first walk. Every later visitor reads the board. The board is the **cache** (also called the **memo**), and checking the board before walking to the archive is **memoization**.

### Analogy 3: the staircase of answers

Another way to avoid repeated work is to *not wait for the question at all*. A student preparing for an exam about multiplication writes the full times table, row by row, starting from 1 × 1. When any question comes, the answer is already in the table. Building answers from the smallest upward in a table is **tabulation**.

So there are two styles of DP, and you will learn both:

```
  Top-down (memoization)                Bottom-up (tabulation)
  ----------------------                ----------------------
  Start from the big question.          Start from the smallest questions.
  Ask smaller questions as needed.      Fill a table upward with a loop.
  Write each answer on the board        When you reach the big question,
  the first time you get it.            its answer is already in the table.
```

---

## 2. Problem statement in our own words

Our running example for the entire module is the **Fibonacci sequence**:

```
index n :  0  1  2  3  4  5  6   7   8   9  10
fib(n)  :  0  1  1  2  3  5  8  13  21  34  55
```

Each number is the sum of the two before it. The first two are fixed: `fib(0) = 0`, `fib(1) = 1`.

**Task:** given a whole number `n ≥ 0`, return `fib(n)`.

This exact task is [LeetCode 509 — Fibonacci Number](https://leetcode.com/problems/fibonacci-number/). It is tiny on purpose: the problem itself is trivial, so all your attention can go to *how* we compute it.

---

## 3. Examples (input → output, with an explanation)

| Input `n` | Output | Why |
|-----------|--------|-----|
| 0 | 0 | Defined directly. |
| 1 | 1 | Defined directly. |
| 2 | 1 | `fib(1) + fib(0) = 1 + 0` |
| 6 | 8 | `fib(5) + fib(4) = 5 + 3` |
| 10 | 55 | `fib(9) + fib(8) = 34 + 21` |
| 50 | 12,586,269,025 | Too big for `int`, so we use `long`. |

---

## 4. Deriving the solution — the 7 questions

Every problem in this course is approached with the same seven questions. You will learn each of them properly in later topics (state in [09](../09-state-representation/), transitions and base cases in [10](../10-state-transitions-and-base-cases/), order in [11](../11-transition-dependencies-and-computation-order/), space in [15](../15-space-optimization-basics/)). For now, just see how they look on Fibonacci.

### 4.1 What is changing?
Only the number `n`. Each step asks about a smaller `n`.

### 4.2 What does the state represent?
`fib(n)` = "the n-th Fibonacci number". One number `n` describes one subproblem completely.

### 4.3 What are the choices?
There are no real "choices" here: the definition always uses both `fib(n-1)` and `fib(n-2)`. (Problems with choices, like "take or skip", come in [topic 03](../03-recursion-trees-and-brute-force/).)

### 4.4 What is the recurrence?
`fib(n) = fib(n-1) + fib(n-2)` — the two previous values are added.

### 4.5 What are the base cases?
`fib(0) = 0` and `fib(1) = 1`. They are given by the definition and cannot be broken down further.

### 4.6 What is the order of computation?
`fib(n)` needs `fib(n-1)` and `fib(n-2)`, which are smaller. So answers must be ready in increasing order of `n`: 0, 1, 2, 3, …

### 4.7 Can the state space be optimized?
Yes: each value only needs the previous two, so we will eventually keep just two variables (topic [08](../08-top-down-vs-bottom-up/)).

---

## 5. Brute force: recursion

The definition translates directly into Java:

```java
static long naiveFib(int n) {
    if (n <= 1) return n;                     // fib(0) = 0, fib(1) = 1
    return naiveFib(n - 1) + naiveFib(n - 2);
}
```

It is correct. But watch what happens for `fib(5)`. Each box is one function call; the lines show which call made which:

```
                          fib(5)
                  /                  \
             fib(4)                    fib(3)
           /        \                 /      \
       fib(3)        fib(2)       fib(2)    fib(1)
       /    \        /    \       /    \
   fib(2) fib(1) fib(1) fib(0) fib(1) fib(0)
   /    \
fib(1) fib(0)
```

Count the repeats:

- `fib(3)` is computed **2** times,
- `fib(2)` is computed **3** times,
- `fib(1)` is computed **5** times.

For `fib(5)` that is only 15 calls in total. But the tree roughly **doubles** every time `n` grows by one or two. For `fib(35)` the program makes **29,860,703** calls; for `fib(50)` about **40 billion**. The work is not hard; it is *repeated*. (Topic [03](../03-recursion-trees-and-brute-force/) shows how to count these calls exactly.)

---

## 6. Memoization (top-down)

The fix is the "board by the door". Before computing `fib(k)`, look in a notebook. If the answer is there, return it. If not, compute it and **write it down**.

```java
// notebook[i] holds fib(i) once it is known; -1 means "not written down yet".
private static long[] notebook;

static long cachedFib(int n) {
    if (n <= 1) return n;
    if (notebook[n] != -1) return notebook[n];    // already solved: just read it
    notebook[n] = cachedFib(n - 1) + cachedFib(n - 2);
    return notebook[n];
}
```

Why `-1`? It is a **sentinel**: a value that can never be a real answer (Fibonacci numbers are never negative), so it safely means "empty". Topic [06](../06-memoization/) covers this in detail.

With the notebook, the tree for `fib(5)` collapses. The second time `fib(3)` or `fib(2)` is asked for, it is a lookup (marked `✓`) and its whole subtree disappears:

```
              fib(5)
             /      \
         fib(4)      fib(3) ✓   (read from notebook)
        /      \
    fib(3)     fib(2) ✓
    /     \
 fib(2)   fib(1)
 /    \
fib(1) fib(0)
```

Now each value is computed **once**. For `fib(35)` the call count drops from about 30 million to **69**.

---

## 7. Tabulation (bottom-up)

The "times table" style: fill an array from the bottom up with a loop. No recursion at all.

```java
long[] dp = new long[n + 1];   // dp[i] = fib(i)
dp[0] = 0;
dp[1] = 1;
for (int i = 2; i <= n; i++) dp[i] = dp[i - 1] + dp[i - 2];
return dp[n];
```

```
i     :  0  1  2  3  4  5  6
dp[i] :  0  1  1  2  3  5  8      each cell = the two cells to its left, added
```

Topic [07](../07-tabulation/) teaches this style properly.

---

## 8. Space optimization

Look at the table: to fill cell `i` we only read cells `i-1` and `i-2`. Everything further left is never read again. So two variables are enough. This is the last improvement you will make on most DP problems; topic [08](../08-top-down-vs-bottom-up/) shows the code.

---

## 9. Dry run on a full example

Let us compare the two approaches on `n = 6` by counting how many times each value is *computed* (not looked up):

| Value | Naive recursion | With notebook |
|-------|-----------------|---------------|
| fib(6) | 1 | 1 |
| fib(5) | 1 | 1 |
| fib(4) | 2 | 1 |
| fib(3) | 3 | 1 |
| fib(2) | 5 | 1 |
| fib(1) | 8 | (base case) |
| fib(0) | 5 | (base case) |
| **Total calls** | **25** | **11** (5 computed, 3 lookups, 3 base cases) |

The run `java Demo.java` in [`solution/`](solution/) prints the same kind of comparison for `n = 35`:

```
fib(35) naive : 9227465  calls = 29860703  time ~ 200 ms
fib(35) cached: 9227465  calls = 69  time ~ 25 microseconds
```

(Your times will differ; the call counts will not.) Increase `n` to 45 in the code and the naive version takes many seconds while the cached version stays instant.

---

## 10. Complexity analysis

"Complexity" means how the running time and memory grow as the input grows. We write it with *big-O*: O(n) means "grows in proportion to n", O(2ⁿ) means "roughly doubles each time n goes up by one".

| Approach | Time | Why | Space | Why |
|----------|------|-----|-------|-----|
| Naive recursion | O(2ⁿ) | Each call makes two more calls, so the number of calls roughly doubles per level of the tree. | O(n) | At most `n` calls are waiting on the call stack at once (one chain from `fib(n)` down to `fib(1)`). |
| Notebook (memoization) | O(n) | Each of `fib(2) … fib(n)` is computed once with one addition; every other call is a O(1) lookup. | O(n) | The notebook has `n + 1` cells, and the recursion is still `n` deep. |
| Table (tabulation) | O(n) | One loop step per cell. | O(n) | The table. No recursion. |
| Two variables | O(n) | Same loop. | O(1) | Only two numbers are stored. |

---

## 11. Why this approach works

Caching cannot change an answer because `fib(k)` **always has the same value**, no matter who asks for it or when. A function like that (same input → same output, no side effects) is called a *pure* function. If a subproblem's answer could change depending on what happened earlier, a stored answer could be stale, and DP would not apply. Keep this in mind: *"the answer to a subproblem depends only on the subproblem's state"* is exactly what makes DP correct.

---

## 12. Common mistakes

- **Thinking DP is a specific algorithm.** It is a *strategy* (reuse answers to repeated subproblems). Each problem gets its own state and recurrence.
- **Thinking DP means "use an array".** The array is just the storage. The real work is finding the subproblems and the recurrence.
- **Applying DP when nothing repeats.** If the subproblems never overlap (merge sort, binary search), caching adds memory and saves nothing. See [topic 04](../04-overlapping-subproblems/).
- **Using `int` for Fibonacci.** `fib(47)` is already larger than `int` can hold. Use `long` (good up to `fib(92)`).
- **Jumping straight to the table.** Beginners who skip the recursive description usually get the recurrence wrong. In this course you will always write the recursion first.

---

## 13. When to use this technique / how to recognize similar problems

Signals that a problem *might* be DP (you will practise this in [topic 16](../16-the-dp-problem-solving-framework/) and in the revision module):

- It asks for a **count** ("how many ways…"), a **best value** ("minimum cost", "maximum profit", "longest…"), or a **yes/no** ("is it possible…").
- The answer for a big input can be described using answers for **smaller inputs** (shorter prefix, fewer items, smaller target, smaller grid).
- A brute-force search makes **choices** step by step (take/skip, go left/right, use this coin or not) and the same situations come up via different choice sequences.

### The course path

```
00 Foundations            <- you are here: recursion, memo, tables, states, framework
01 One-dimensional DP     <- dp[i]: stairs, house robber, decode ways
02 Grid DP                <- dp[r][c]: paths in a grid, triangle, dungeon
03 Subsequences/knapsack  <- dp[i][capacity], dp[i][j] on two strings
04 Advanced patterns      <- intervals, trees, bitmasks, digits, state machines
05 Optimization           <- making correct DP faster or smaller
06 LeetCode practice      <- mixed problems by pattern
07 Revision & interviews  <- cheat sheets, common mistakes, strategy
```

---

## 14. Summary and revision notes

- DP = **solve each subproblem once, store its answer, reuse it.**
- It works when the problem has **overlapping subproblems** and **optimal substructure**.
- Two styles: **top-down** (recursion + cache = memoization) and **bottom-up** (loop + table = tabulation).
- Naive Fibonacci: O(2ⁿ) time. With DP: O(n) time; with two variables, O(1) space.
- Always: write the recursive description first, then add DP.

```
┌──────────────────────────────────────────────┐
│  fib(0) = 0,  fib(1) = 1                     │
│  fib(n) = fib(n-1) + fib(n-2)   for n >= 2   │
└──────────────────────────────────────────────┘
```

---

## Check your understanding

1. In your own words, why is the naive recursive `fib(40)` slow even though each call does only one addition?
2. A friend says "DP just means using an array". What is wrong with that statement?
3. Which of these could benefit from DP? (a) finding the maximum of an array, (b) counting the ways to climb 30 stairs taking 1 or 2 steps at a time, (c) binary search.
4. Why is `-1` a safe "empty" marker in the Fibonacci notebook? Would it still be safe for a problem whose answers can be negative?

<details>
<summary>Answers</summary>

1. The work is repeated, not hard. The call tree roughly doubles in size with every increase of `n`, and most calls recompute values (like `fib(2)`) that were already computed elsewhere in the tree. `fib(40)` makes over 300 million calls (exactly 2·fib(41) − 1 = 331,160,281).
2. The array is only the storage. DP is the strategy of identifying subproblems, writing a recurrence, and reusing each subproblem's answer. You can also store answers in a `HashMap`, or in two variables.
3. Only (b). Climbing stairs: the number of ways to reach stair `n` is the ways to reach `n-1` plus the ways to reach `n-2`, and these smaller questions repeat many times. (a) is a single loop with no repeated subproblems. (c) looks at a different half each time, so no subproblem is ever asked twice.
4. Fibonacci numbers are never negative, so `-1` can never be confused with a real answer. If answers can be negative, `-1` might be a real answer; you would need another marker (such as `Long.MIN_VALUE`) or a separate `boolean[] computed` array.

</details>

---

⬅ [Module overview](../README.md) · ➡ [02 — Recursion Fundamentals](../02-recursion-fundamentals/) · 📘 [Topic README](README.md)
