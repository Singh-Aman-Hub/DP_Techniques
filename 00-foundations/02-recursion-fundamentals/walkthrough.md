# Recursion Fundamentals — Walkthrough

> Goal: make recursion feel ordinary. By the end you should be able to write a recursive function from a definition, predict exactly what the call stack does, and trust a recursive call without tracing it.

---

## 1. Intuition (no code, no math — an everyday analogy)

### The cinema row

You are sitting in a dark cinema and want to know which row you are in. You cannot see the row numbers. So you tap the person in front of you and ask, *"Which row are you in?"* They do not know either, so they ask the person in front of *them*, and so on.

Eventually the question reaches someone in the **first row**. They know their answer without asking anyone: *"Row 1."* That person replies to the one behind them, who adds 1 and replies "Row 2", and so on back to you.

```
 you (row ?) ──asks──> row ? ──asks──> row ? ──asks──> front row
                                                         │ "I am row 1"   <- base case
 you  <──"row 4"── row 3 <──"row 3"── row 2 <──"row 2"──┘
                        (everyone adds 1 to the answer they get)
```

That story contains everything about recursion:

1. **The same question, smaller.** Each person asks the *same question* to someone *closer to the front*.
2. **A stopping point.** The front row answers directly. This is the **base case**. Without it the question would travel forever.
3. **Waiting.** While the question travels forward, everyone is *waiting* for an answer. Nobody can finish until the person in front replies. Those waiting people are the **call stack**.
4. **Combining on the way back.** Each person does a tiny bit of work (add 1) to the answer they receive. This is the **recursive case**.
5. **Trust.** You do not care *how* the person in front figured out their row. You trust their answer and add 1. This is the **leap of faith**.

---

## 2. Problem statement in our own words

This topic has four small problems, each adding one new idea:

| Problem | New idea |
|---------|----------|
| `factorial(n)` = n × (n−1) × … × 1, with `factorial(0) = 1` | One recursive call. |
| `sumArray(values, index)` = sum of `values[index..end]` | Recursion on an array position instead of a number. |
| `fib(n)` = `fib(n-1) + fib(n-2)`, `fib(0) = 0`, `fib(1) = 1` | Two recursive calls; two base cases. |
| `gridPaths(rows, cols)` = number of ways to walk from the top-left to the bottom-right of a grid moving only right or down | One recursive call per **choice**, and the answers are added. |

All four live in [`solution/RecursionBasics.java`](solution/RecursionBasics.java). [`solution/CallStackTrace.java`](solution/CallStackTrace.java) prints the call stack as the functions run.

---

## 3. Examples (input → output, with an explanation)

| Call | Output | Explanation |
|------|--------|-------------|
| `factorial(0)` | 1 | Base case: the product of no numbers is 1. |
| `factorial(5)` | 120 | 5 × 4 × 3 × 2 × 1 |
| `sumArray([3,1,4,1,5], 0)` | 14 | 3 + 1 + 4 + 1 + 5 |
| `sumArray([], 0)` | 0 | Empty array: nothing to add. |
| `fib(6)` | 8 | 0, 1, 1, 2, 3, 5, **8** |
| `gridPaths(2, 3)` | 3 | In a 2 × 3 grid: RRD, RDR, DRR (R = right, D = down). |
| `gridPaths(3, 3)` | 6 | Any order of 2 R's and 2 D's: C(4,2) = 6. |

---

## 4. Deriving the solution — the 7 questions

We apply the questions to the most "DP-like" of the four problems, **`gridPaths`**, because it has choices. (For factorial: what changes is `n`, the base case is `n = 0`, the recurrence is `n × factorial(n-1)`.)

```
 2 x 3 grid:  S = start (top-left), E = end (bottom-right)
 +---+---+---+
 | S |   |   |
 +---+---+---+
 |   |   | E |
 +---+---+---+
```

### 4.1 What is changing?
How many rows and columns are still in front of us. Moving down removes a row; moving right removes a column.

### 4.2 What does the state represent?
`gridPaths(rows, cols)` = "the number of different right/down paths from the top-left to the bottom-right of a grid with `rows` rows and `cols` columns".

### 4.3 What are the choices?
From the top-left corner: **step down** (the remaining grid has `rows − 1` rows) or **step right** (the remaining grid has `cols − 1` columns).

### 4.4 What is the recurrence?
```
gridPaths(rows, cols) = gridPaths(rows - 1, cols)     // all paths that start with "down"
                      + gridPaths(rows, cols - 1)     // all paths that start with "right"
```
We **add** because every path starts with exactly one of the two moves, so the two groups never share a path and together they contain every path.

### 4.5 What are the base cases?
If `rows == 1` or `cols == 1` the grid is a single line, and there is exactly **one** path (walk straight). This also covers `gridPaths(1, 1) = 1` (start is the end: the empty path).

### 4.6 What is the order of computation?
Each call needs calls with fewer rows or fewer columns, which recursion handles automatically. (When we fill a table in [topic 07](../07-tabulation/), we will have to choose this order ourselves.)

### 4.7 Can the state space be optimized?
Not in this topic. Later: there are only `rows × cols` different states, so caching ([topic 06](../06-memoization/)) makes this fast, and [Module 02 — Unique Paths](../../02-two-dimensional-and-grid-dp/02-unique-paths/) does exactly that.

---

## 5. Brute force: recursion

### 5.1 The anatomy of a recursive function

Every recursive function has this shape:

```java
ReturnType solve(input) {
    if (input is tiny)                      // BASE CASE: answer known directly
        return directAnswer;
    smallerAnswer = solve(smaller input);   // RECURSIVE CASE: same problem, smaller
    return combine(smallerAnswer, ...);     //   use the smaller answer to build ours
}
```

Two rules make it terminate:

1. There is **at least one base case**, and it is checked **before** any recursive call.
2. Every recursive call moves **strictly closer** to a base case (smaller `n`, larger index towards the end, fewer rows…).

### 5.2 Factorial — one call

```java
static long factorial(int n) {
    if (n == 0) return 1;                 // base case: the empty product is 1
    return n * factorial(n - 1);          // trust that factorial(n-1) is correct
}
```

### 5.3 Sum of an array — recursion on a position

There is no "smaller number" here, so we shrink the *part of the array* we look at. `index` marks where the remaining part starts.

```java
static int sumArray(int[] values, int index) {
    if (index == values.length) return 0; // base case: nothing left to add
    return values[index] + sumArray(values, index + 1);
}
```

```
values = [3, 1, 4, 1, 5]
sumArray(...,0) = 3 + sumArray(...,1)
                      = 1 + sumArray(...,2)
                            = 4 + sumArray(...,3)
                                  = 1 + sumArray(...,4)
                                        = 5 + sumArray(...,5)
                                              = 0          <- index == length
total = 3 + 1 + 4 + 1 + 5 + 0 = 14
```

Notice the base case is the **empty** remainder, not "the last element". Using the empty case makes the function also work for an empty array, and avoids a special case.

### 5.4 Fibonacci — two calls

```java
static long fib(int n) {
    if (n <= 1) return n;                 // two base cases: fib(0) = 0, fib(1) = 1
    return fib(n - 1) + fib(n - 2);
}
```

Why two base cases? The recursive case reaches back **two** steps. If we only had `fib(0)`, then `fib(1)` would call `fib(0)` and `fib(-1)`, and `fib(-1)` would never stop. A rule of thumb: **if the recurrence looks back k steps, you usually need k base cases.**

### 5.5 Grid paths — one call per choice

```java
static long gridPaths(int rows, int cols) {
    if (rows == 1 || cols == 1) return 1;
    return gridPaths(rows - 1, cols) + gridPaths(rows, cols - 1);
}
```

This is the shape you will see in almost every DP problem: **for each choice, recurse; combine the results** (here with `+` because we count; later with `min` or `max` when we optimize).

### 5.6 The call stack, frame by frame

When Java calls a method, it pushes a **frame** on the call stack holding that call's parameters and the place to continue when it gets an answer back. When the method returns, its frame is popped and its value goes to the frame below.

Here is `factorial(4)`. Read left to right; the top of each column is the frame running right now.

```
 time ──────────────────────────────────────────────────────────────────────────>

 push      push      push      push      push      pop       pop       pop       pop       pop
                                         ┌──────┐
                               ┌──────┐  │ f(0) │  ┌──────┐
                     ┌──────┐  │ f(1) │  │ f(1) │  │ f(1) │  ┌──────┐
           ┌──────┐  │ f(2) │  │ f(2) │  │ f(2) │  │ f(2) │  │ f(2) │  ┌──────┐
 ┌──────┐  │ f(3) │  │ f(3) │  │ f(3) │  │ f(3) │  │ f(3) │  │ f(3) │  │ f(3) │  ┌──────┐
 │ f(4) │  │ f(4) │  │ f(4) │  │ f(4) │  │ f(4) │  │ f(4) │  │ f(4) │  │ f(4) │  │ f(4) │
 └──────┘  └──────┘  └──────┘  └──────┘  └──────┘  └──────┘  └──────┘  └──────┘  └──────┘
                                         f(0)=1    f(1)=1    f(2)=2    f(3)=6    f(4)=24
                                         returns   returns   returns   returns   returns
```

What each frame is **waiting** for while it sits on the stack:

| Frame | Waiting for | Then computes |
|-------|-------------|---------------|
| `factorial(4)` | `factorial(3)` | 4 × 6 = **24** |
| `factorial(3)` | `factorial(2)` | 3 × 2 = 6 |
| `factorial(2)` | `factorial(1)` | 2 × 1 = 2 |
| `factorial(1)` | `factorial(0)` | 1 × 1 = 1 |
| `factorial(0)` | nothing (base case) | 1 |

At the deepest moment there are **5 frames** on the stack at once. That number is the **recursion depth**, and it is what costs memory.

Run `java CallStackTrace.java` to see the same thing printed by the program. Each line is indented by the current stack depth:

```
call   factorial(4)
  call   factorial(3)
    call   factorial(2)
      call   factorial(1)
        call   factorial(0)
        return factorial(0) = 1
      return factorial(1) = 1
    return factorial(2) = 2
  return factorial(3) = 6
return factorial(4) = 24
```

With **two** recursive calls the stack goes down, comes back up part of the way, and goes down again. Here is `fib(4)` from the same program:

```
call   fib(4)
  call   fib(3)
    call   fib(2)
      call   fib(1)
      return fib(1) = 1
      call   fib(0)
      return fib(0) = 0
    return fib(2) = 1
    call   fib(1)
    return fib(1) = 1
  return fib(3) = 2
  call   fib(2)
    call   fib(1)
    return fib(1) = 1
    call   fib(0)
    return fib(0) = 0
  return fib(2) = 1
return fib(4) = 3
```

Important observation: even though `fib(4)` makes **9** calls in total, the stack never holds more than **4** frames at once (`fib(4) → fib(3) → fib(2) → fib(1)`). The left call must *finish* (and its frame be popped) before the right call starts. So:

> **Time** depends on the *total* number of calls. **Stack space** depends only on the *deepest chain* of calls.

### 5.7 The leap of faith

Tracing every call is useful once, to understand the stack. It is useless for writing or checking recursive code: the trace of `fib(30)` has over a million lines. Instead, use the **leap of faith**, which is really just mathematical induction:

1. **Check the base cases** by hand. (`factorial(0) = 1`? ✔)
2. **Assume** the recursive call on any *smaller* input returns the correct answer. Do not trace it.
3. **Check one level**: if the smaller answer is correct, is my combination correct? (`factorial(n-1)` is (n−1)!, and n × (n−1)! = n! ✔)
4. **Check progress**: does every recursive call move closer to a base case? (n − 1 < n, and we stop at 0 ✔ — for n ≥ 0.)

If all four hold, the function is correct for every input. Apply it to `gridPaths`:

1. Base: a 1 × c or r × 1 grid has one path ✔
2. Assume `gridPaths(rows-1, cols)` correctly counts paths in the smaller grid below, and `gridPaths(rows, cols-1)` in the grid to the right.
3. Every path from the top-left starts with D or R. Paths starting with D correspond one-to-one to paths of the grid below; same for R. Adding gives all paths, each counted once ✔
4. Each call reduces `rows` or `cols` by one and stops when either is 1 ✔

That is how you will design every recurrence in this course: **define what the function returns, trust it for smaller inputs, and get one level right.**

### 5.8 Stack overflow

The stack has a fixed size (typically a few thousand to tens of thousands of frames for simple methods, depending on the JVM settings). Two things exhaust it:

**Cause 1: a missing or unreachable base case.**

```java
static long brokenCountdown(int n) {
    return 1 + brokenCountdown(n - 1);   // no base case: never stops
}
```

`CallStackTrace.java` calls this and catches the resulting `java.lang.StackOverflowError`. Other ways to get the same bug: base case `if (n == 0)` but the call is `f(n - 2)` with odd `n` (it jumps over 0); or a recursive call that does not shrink the input (`f(n)` calling `f(n)`).

**Cause 2: correct recursion that is simply too deep.** `sumArray` on an array of 1,000,000 elements needs 1,000,001 frames. Fixes: use a loop (bottom-up, which is what tabulation does in [topic 07](../07-tabulation/)), or run with a bigger stack (`java -Xss64m ...`; the repository's validator does this).

---

## 6. Memoization (top-down)

Not part of this topic — recursion comes first. Our `fib` and `gridPaths` repeat a lot of work; [topic 04](../04-overlapping-subproblems/) measures it and [topic 06](../06-memoization/) removes it.

## 7. Tabulation (bottom-up)

Not part of this topic. Note for later: a recursive function on a number that only shrinks can always be rewritten as a loop that grows from the base case. That is tabulation ([topic 07](../07-tabulation/)).

## 8. Space optimization

Not part of this topic. The only "space" here is the call stack, whose size equals the recursion depth (section 10).

---

## 9. Dry run on a full example

`gridPaths(2, 3)` — every call, in the order Java runs them. "D" means the first term (step down), "R" the second (step right).

| Step | Call | Stack depth | What happens | Returns |
|------|------|-------------|--------------|---------|
| 1 | `gridPaths(2,3)` | 1 | not a base case → first ask D: `gridPaths(1,3)` | (waits) |
| 2 | `gridPaths(1,3)` | 2 | rows == 1 → base case | 1 |
| 3 | `gridPaths(2,3)` | 1 | got D = 1; now ask R: `gridPaths(2,2)` | (waits) |
| 4 | `gridPaths(2,2)` | 2 | not a base case → ask D: `gridPaths(1,2)` | (waits) |
| 5 | `gridPaths(1,2)` | 3 | base case | 1 |
| 6 | `gridPaths(2,2)` | 2 | got D = 1; ask R: `gridPaths(2,1)` | (waits) |
| 7 | `gridPaths(2,1)` | 3 | cols == 1 → base case | 1 |
| 8 | `gridPaths(2,2)` | 2 | 1 + 1 | 2 |
| 9 | `gridPaths(2,3)` | 1 | 1 + 2 | **3** |

The three paths are D-then-RR (step 2), and R followed by the two paths of the 2 × 2 grid (steps 5 and 7): RDR and RRD. Maximum stack depth is 3 = rows + cols − 2.

---

## 10. Complexity analysis

To find the running time of recursion: **(number of calls) × (work per call, not counting the recursive calls)**. To find the stack space: **the maximum depth**.

| Function | Calls | Work per call | Time | Max depth | Stack space |
|----------|-------|---------------|------|-----------|-------------|
| `factorial(n)` | n + 1 (n, n−1, …, 0) | O(1) | **O(n)** | n + 1 | **O(n)** |
| `sumArray(a, 0)` | n + 1 (indices 0…n) | O(1) | **O(n)** | n + 1 | **O(n)** |
| `fib(n)` | 2·fib(n+1) − 1 (e.g. 25 for n = 6) | O(1) | **O(2ⁿ)** | n | **O(n)** |
| `gridPaths(r, c)` | grows like 2^(r+c) | O(1) | **O(2^(r+c))** | r + c − 2 (for r, c ≥ 2) | **O(r + c)** |

Where do the exponential numbers come from? Each `fib` call makes two calls, so the number of calls can roughly double at each level of depth. The next topic, [03 — Recursion Trees and Brute Force](../03-recursion-trees-and-brute-force/), draws these trees and counts their nodes precisely.

---

## 11. Why this approach works

Recursion is correct for the same reason proof by induction is: if the base cases are right, and "smaller answers are right" implies "this answer is right", and every call gets smaller, then by following the chain down to the base cases, every answer is right. The leap-of-faith checklist in 5.7 *is* that argument.

---

## 12. Common mistakes

| Mistake | Symptom | Fix |
|---------|---------|-----|
| No base case, or base case after the recursive call | `StackOverflowError` | Put the base-case `if` **first**. |
| Base case can be skipped (`f(n-2)` with only `if (n == 0)`) | `StackOverflowError` for odd n | Cover every way the input can reach the bottom (`n <= 1`, or `n < 0` returns 0). |
| Input does not shrink (`sumArray(values, index)` instead of `index + 1`) | Infinite recursion | Make sure every call is on a strictly smaller problem. |
| Too few base cases for a recurrence that looks back 2 steps | Wrong answers or overflow | One base case per step the recurrence reaches back. |
| Ignoring the returned value (`fib(n - 1);` on its own line) | Wrong answers | Use or combine every result you request. |
| Using `int` where results grow fast | Negative or wrong numbers (`factorial(13)` > `int` range) | Use `long`; for huge counts, the problem will ask for "modulo 10⁹+7". |
| Tracing every call to check correctness | Confusion, wasted time | Use the leap-of-faith checklist instead. |

---

## 13. When to use this technique / how to recognize similar problems

Write a recursive solution when you can phrase the answer as "**the answer for this input = (some combination of) the answers for smaller inputs**". Phrases that signal it:

- "for each element, either … or …" (choices → one call per choice);
- "the number of ways to…" (add the calls);
- "the minimum / maximum …" (take `min` / `max` over the calls);
- defined sequences (factorial, Fibonacci, Tribonacci).

For DP problems this recursive version is always step one, even if your final code is a loop.

---

## 14. Summary and revision notes

- A recursive function = **base case(s)** checked first + **recursive case(s)** on strictly smaller inputs.
- Java keeps one **frame** per unfinished call on the **call stack**. Stack space = maximum depth, not total calls.
- **Leap of faith**: check base cases, assume smaller calls are right, verify one level, verify progress.
- One call per **choice**, combined with `+` (count), `min`/`max` (optimize) or `||` (feasibility).
- `StackOverflowError` = missing/skipped base case, non-shrinking input, or simply too deep.

```
┌─────────────────────────────────────────────────────────────┐
│  solve(x):                                                  │
│     if x is a base case:   return known answer              │
│     for each choice c:     r_c = solve(smaller(x, c))       │
│     return combine(r_1, r_2, ...)   // +, min, max, ||      │
└─────────────────────────────────────────────────────────────┘
```

---

## Check your understanding

1. What does this print for `mystery(4)`, and what is its maximum recursion depth?
   ```java
   static int mystery(int n) {
       if (n == 0) return 0;
       return n + mystery(n - 1);
   }
   ```
2. A student writes `static long fib(int n) { if (n == 0) return 0; return fib(n-1) + fib(n-2); }`. What happens for `fib(2)` and why?
3. Write a recursive function `countEvens(int n)` that returns how many even numbers are in `1..n`. State its base case and recursive case.
4. `fib(25)` makes 242,785 calls. Roughly how many frames are on the stack at the deepest moment? Why is it so much smaller?

<details>
<summary>Answers</summary>

1. It returns 4 + 3 + 2 + 1 + 0 = **10**. Frames at the deepest point: `mystery(4), (3), (2), (1), (0)` = **5**.
2. `fib(2)` calls `fib(1)`, which is not a base case here, so it calls `fib(0)` (fine, returns 0) and `fib(-1)`. `fib(-1)` calls `fib(-2)`, `fib(-3)`, … forever, so the program crashes with `StackOverflowError`. The recurrence looks back two steps, so it needs two base cases (`n <= 1` returns `n`).
3. ```java
   static int countEvens(int n) {
       if (n == 0) return 0;                                   // base case: no numbers
       return (n % 2 == 0 ? 1 : 0) + countEvens(n - 1);        // is n even? + evens in 1..n-1
   }
   ```
   Base case: `n == 0` → 0. Recursive case: count `n` if it is even, plus the evens below it.
4. **25** frames (`fib(25) → fib(24) → … → fib(1)`). Stack space only depends on the longest chain of calls that are waiting at the same time; a call's frame is removed when it returns, before its sibling starts.

</details>

---

⬅ [01 — What is DP?](../01-what-is-dynamic-programming/) · ➡ [03 — Recursion Trees and Brute Force](../03-recursion-trees-and-brute-force/) · 📘 [Topic README](README.md)
