# Convex Hull Trick

> **Module:** 05 — DP Optimization · **Difficulty:** Advanced · **Estimated time:** 2–3 hours

## 1. What is this topic?

The Convex Hull Trick (CHT) speeds up DP transitions of the form

```
dp[i] = min over j < i of ( m_j · x_i + b_j )  +  (terms that depend only on i)
```

where every earlier state `j` contributes a **line** `y = m_j·x + b_j`, and state `i` asks "which line is lowest at the point `x_i`?". Instead of trying every `j` (O(n) per state), we keep only the lines that can ever be the lowest — the **lower envelope** — and answer each question in amortized O(1) (monotone deque) or O(log n) (Li Chao tree).

## 2. Why does this concept exist?

Many DPs have a cost that multiplies something about `j` with something about `i`: `(h_i - h_j)²` contains `h_i·h_j`, "strength of monster i × factor of j", "price of item i × quantity accumulated up to j". Such a product prevents prefix sums or sliding windows from helping, so the direct DP is O(n²). With n up to 2·10⁵ that is 4·10¹⁰ steps. CHT notices that, seen from `i`, each candidate `j` is just a straight line, and picking the minimum of many lines is a geometry problem with a fast answer.

## 3. What problem does it solve?

It turns an O(n²) DP into O(n) (when slopes are added in sorted order and queries arrive in sorted order) or O(n log C) (the general case with a Li Chao tree), provided the transition can be rewritten algebraically into "line evaluated at a point" form.

## 4. Prerequisites

- [Monotonic queue optimization](../04-monotonic-queue-optimization/) — the same "pop from the front, pop from the back" deque discipline.
- [Divide and conquer optimization](../05-divide-and-conquer-optimization/) — another way to exploit structure in `min_j` transitions.
- [Frog Jump (1D DP)](../../01-one-dimensional-dp/04-frog-jump/) — the original frog problems that Frog 3 generalizes.
- [Time and space complexity in DP](../../00-foundations/12-time-and-space-complexity-in-dp/).
- School algebra: expanding `(a - b)²`, the equation of a line `y = mx + b`, solving `m₁x + b₁ = m₂x + b₂`.

## 5. What you will learn

- How to rewrite a DP transition into line form, step by step.
- What a lower envelope is, and why most lines are useless.
- The monotone-slope deque: when to pop from the back (a line became useless) and from the front (a line is beaten for all future queries).
- How to compare intersections without division and without overflow.
- When to switch to a Li Chao tree (unsorted slopes or queries).

## 6. Important terminology

| Term | Meaning in plain English |
|------|--------------------------|
| Line `(m, b)` | The function `y = m·x + b`; `m` is the slope (steepness), `b` the intercept (height at `x = 0`). |
| Query point `x` | The value at which state `i` evaluates all lines (`x = h_i` in Frog 3). |
| Lower envelope | For each `x`, the lowest of all lines. It is made of pieces of a few lines and bends upward like a cup (it is concave). |
| Useless line | A line that is never the lowest at any `x`; it can be deleted forever. |
| Intersection `x(1,2)` | The `x` where lines 1 and 2 have equal height: `(b₂ - b₁) / (m₁ - m₂)`. |
| Monotone CHT | The deque version: slopes inserted in sorted order and queries in sorted order. Amortized O(1). |
| Li Chao tree | A segment tree over `x` storing one line per node; supports any insertion/query order in O(log C). |

## 7. Where this is used in real problems

- Jumps whose cost is a square of a difference (Frog 3, many "minimize sum of squared gaps" problems).
- "Fight monsters with a weapon factor" DPs where cost = strength × factor (CSES Monster Game I/II).
- Partition DPs with squared group sums: each layer of Subarray Squares can be done with CHT in O(n).
- Batch scheduling, "buy land" style problems and other classic O(n²) → O(n) speed-ups in contests.

## 8. After completing this topic you should be able to…

- [ ] Expand a transition and identify slope, intercept, query point and the leftover `i`-only term.
- [ ] Draw the lower envelope of three lines and point out a useless one.
- [ ] Implement the monotone deque CHT with a division-free "useless middle line" test.
- [ ] Argue why each line is pushed and popped at most once (amortized O(1)).
- [ ] Explain when the deque breaks and a Li Chao tree is needed.

## 9. Practice problems

| # | Problem | Platform/ID | Difficulty | Pattern tested | Why it belongs here |
|---|---------|-------------|------------|----------------|---------------------|
| 1 | [Frog 3](https://atcoder.jp/contests/dp/tasks/dp_z) | AtCoder dp_z | Unrated (AtCoder) | Monotone CHT, squared jump cost | The worked example: sorted heights give sorted slopes and sorted queries. |
| 2 | [Monster Game I](https://cses.fi/problemset/task/2084) | CSES 2084 | Unrated (CSES) | Monotone CHT, cost = strength × factor | The transition is already a line; the input's sorted guarantees allow the deque. |
| 3 | [Monster Game II](https://cses.fi/problemset/task/2085) | CSES 2085 | Unrated (CSES) | General CHT (Li Chao tree) | Same DP as Game I but without the full sortedness guarantee, so the deque is not enough. |
| 4 | [Subarray Squares](https://cses.fi/problemset/task/2086) | CSES 2086 | Unrated (CSES) | Layered DP + CHT per layer | Solved with D&C in the previous topic; expanding `(P_i - P_j)²` gives lines, an O(k·n) alternative. |

## 10. Navigation

⬅ Previous: [Divide and Conquer Optimization](../05-divide-and-conquer-optimization/) · ➡ Next: [SOS DP](../07-sos-dp/) · 📖 [Walkthrough](walkthrough.md) · 💻 [Solutions](solution/)
