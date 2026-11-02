# Recursion Fundamentals

> **Module:** 00 — Foundations · **Difficulty:** Beginner · **Estimated time:** 60–90 min

## 1. What is this topic?

Recursion is a function solving a problem by calling **itself** on a smaller version of the same problem. Every DP solution in this course starts life as a recursive function, so this topic makes sure recursion feels natural: how to write one, how Java runs it (the call stack), how to reason about it without tracing every call (the "leap of faith"), and what goes wrong when it never stops (stack overflow).

## 2. Why does this concept exist?

Many problems are defined in terms of smaller copies of themselves: `n! = n × (n-1)!`, `fib(n) = fib(n-1) + fib(n-2)`, "the paths from here = paths if I go right + paths if I go down". Recursion lets you write code that mirrors such a definition line by line. The code is short and, if the definition is right, correct.

## 3. What problem does it solve?

It lets you solve a problem by describing only **one step**: how to reduce the problem to smaller instances, and what to do when it is so small that the answer is obvious. You do not have to plan out every step in advance. For DP, the recursive description is where the *state* and the *recurrence* come from.

## 4. Prerequisites

- [01 — What is Dynamic Programming?](../01-what-is-dynamic-programming/)
- Java methods, parameters and return values; arrays.

## 5. What you will learn

- The two parts of every recursive function: **base case** and **recursive case**.
- How the **call stack** works, drawn as ASCII, frame by frame.
- The **leap of faith**: trusting the recursive call to be correct so you only reason about one level.
- Recursion with one call (factorial, array sum), two calls (Fibonacci) and one call per **choice** (grid paths).
- **Stack overflow**: what it is, why it happens, and how to avoid it.

## 6. Important terminology

| Term | Meaning in plain English |
|------|--------------------------|
| Recursive function | A function that calls itself. |
| Base case | An input so small that the function returns the answer directly, without calling itself. It stops the recursion. |
| Recursive case | The part that calls the function on a smaller input and uses the result. |
| Call stack | The memory area where Java keeps one *frame* per unfinished call. |
| Stack frame | One call's private copy of its parameters, local variables and "where to continue" when it resumes. |
| Leap of faith | Assuming the recursive call on a smaller input already returns the right answer, and only checking that you combine it correctly. |
| Recursion depth | The largest number of frames that are on the stack at the same time. |
| Stack overflow | The error (`StackOverflowError` in Java) when the stack runs out of space, usually because a base case is missing or the depth is huge. |

## 7. Where this is used in real problems

- Every top-down DP solution (memoization, [topic 06](../06-memoization/)) is a recursive function with a cache.
- Tree and graph algorithms (depth-first search, tree DP in [Module 04](../../04-advanced-dp-patterns/07-dp-on-trees/)).
- Divide and conquer: merge sort, quick sort, binary search.
- Backtracking: generating subsets, permutations, solving puzzles.

## 8. After completing this topic you should be able to…

- [ ] Point out the base case(s) and recursive case(s) in any recursive function.
- [ ] Draw the call stack for `factorial(4)` and say what each frame is waiting for.
- [ ] Write factorial, array sum, Fibonacci and grid-path counting recursively, without looking.
- [ ] Explain the leap of faith and use it to check a recursive function's correctness.
- [ ] Explain what causes a `StackOverflowError` and how to fix the common causes.

## 9. Practice problems

| # | Problem | Platform/ID | Difficulty | Pattern tested | Why it belongs here |
|---|---------|-------------|------------|----------------|---------------------|
| 1 | [Reverse String](https://leetcode.com/problems/reverse-string/) | LeetCode 344 | Easy | One recursive call on a smaller range | Solve it recursively (swap the ends, recurse on the middle) to practise base case + smaller input. |
| 2 | [Power of Two](https://leetcode.com/problems/power-of-two/) | LeetCode 231 | Easy | Recursion with a shrinking number | `n` is a power of two if it is 1, or if it is even and `n/2` is a power of two. Watch the base cases for 0 and negatives. |
| 3 | [Fibonacci Number](https://leetcode.com/problems/fibonacci-number/) | LeetCode 509 | Easy | Two recursive calls | The running example; write it recursively and draw the call stack for `n = 4`. |
| 4 | [Pow(x, n)](https://leetcode.com/problems/powx-n/) | LeetCode 50 | Medium | Halving the input; depth O(log n) | Shows how *how much* you shrink the input changes the recursion depth; careful with negative `n`. |

## 10. Navigation

⬅ Previous: [01 — What is Dynamic Programming?](../01-what-is-dynamic-programming/) · ➡ Next: [03 — Recursion Trees and Brute Force](../03-recursion-trees-and-brute-force/) · 📖 [Walkthrough](walkthrough.md) · 💻 [Solutions](solution/)
