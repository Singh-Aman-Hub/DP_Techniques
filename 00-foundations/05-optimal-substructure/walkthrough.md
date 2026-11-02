# Optimal Substructure — Walkthrough

> Goal: understand *why* a DP recurrence gives the right answer. Overlapping subproblems made caching **fast**; optimal substructure is what makes combining sub-answers **correct**.

---

## 1. Intuition (no code, no math — an everyday analogy)

### The road trip

You drive from Delhi to Mumbai along the fastest possible route, and the route happens to pass through Jaipur.

Claim: **the Delhi → Jaipur part of your route is the fastest way from Delhi to Jaipur.**

Why? Suppose there were a faster Delhi → Jaipur road. Then you could drive that faster road to Jaipur and continue exactly as before to Mumbai. Your whole trip would get faster — but your trip was already the fastest possible. Contradiction. So the piece must be optimal too.

That is **optimal substructure**: *every piece of a best solution is itself a best solution of the smaller problem it solves*. It gives us a way to build best solutions: find the best way to each nearby city first, then extend.

### Where it breaks: the scenic tour

Now you want the **longest** drive from Delhi to Mumbai that never passes through the same city twice (a tourist wanting to see as much as possible). Suppose the longest such route passes through Jaipur. Is its Delhi → Jaipur part the longest Delhi → Jaipur drive?

Not necessarily. The longest Delhi → Jaipur drive might wander through Ahmedabad — and then the second half of your trip can no longer use Ahmedabad. The two halves **compete for the same cities**. The best first half can ruin the second half. The pieces are **not independent**, and optimal substructure fails.

---

## 2. Problem statement in our own words

We study four small problems (code in [`solution/StairsSubstructure.java`](solution/StairsSubstructure.java) and [`solution/GraphPaths.java`](solution/GraphPaths.java)):

1. **Min cost stairs** (the idea behind [LeetCode 746 — Min Cost Climbing Stairs](https://leetcode.com/problems/min-cost-climbing-stairs/)). Stair `j` has a price `cost[j]`, paid when you step off it. You may start on stair 0 or stair 1 for free and climb 1 or 2 stairs at a time. Find the cheapest total to reach the "top", just past the last stair.
2. **Shortest path in a small graph without cycles**, from vertex A to every other vertex.
3. **Longest simple path** in a small graph with cycles — the counterexample.
4. **Climbing stairs, counting**: the number of different ways to reach stair n with 1- or 2-steps.

---

## 3. Examples (input → output, with an explanation)

| Input | Output | Explanation |
|-------|--------|-------------|
| stairs `cost = [10, 15, 20]` | 15 | Start on stair 1, pay 15, jump 2 to the top. |
| stairs `cost = [1,100,1,1,1,100,1,1,100,1]` | 6 | Route 0 → 2 → 4 → 6 → 7 → 9 → top, paying 1 six times, avoiding every 100. |
| stairs `cost = [5, 7]` | 5 | Start on stair 0, pay 5, jump 2 to the top. |
| shortest A → E in the graph below | 7 | A → C → B → D → E = 1 + 2 + 1 + 3. |
| longest simple q → t (second graph) | 2 | Not 6, even though the longest q → r and r → t are 3 each. |
| ways to climb 4 stairs | 5 | 1111, 112, 121, 211, 22. |

---

## 4. Deriving the solution — the 7 questions

For **min cost stairs**.

### 4.1 What is changing?
Which stair we are on (`step`, from 0 up to `n` = the top).

### 4.2 What does the state represent?
`best(step)` = "the minimum total cost paid to arrive standing on stair `step`".

### 4.3 What are the choices?
How did we arrive at `step`? Either with a **1-step from stair `step − 1`** (paying `cost[step − 1]`) or with a **2-step from stair `step − 2`** (paying `cost[step − 2]`).

### 4.4 What is the recurrence?
```
best(step) = min( best(step - 1) + cost[step - 1],      // arrived with a 1-step
                  best(step - 2) + cost[step - 2] )     // arrived with a 2-step
```
This line *uses optimal substructure*: it assumes the cheapest way to `step` consists of the cheapest way to the previous stair plus one move. Section 5.2 proves that assumption.

### 4.5 What are the base cases?
`best(0) = 0` and `best(1) = 0` — you may start on either for free.

### 4.6 What is the order of computation?
`best(step)` needs `best(step − 1)` and `best(step − 2)`, so smaller stairs first.

### 4.7 Can the state space be optimized?
Only the two previous values are used — two variables would do. (Later: [Module 01 — Min Cost Climbing Stairs](../../01-one-dimensional-dp/03-minimum-cost-climbing-stairs/).)

---

## 5. Brute force: recursion

### 5.1 The recursive code

```java
static int minCost(int[] cost, int step) {
    if (step == 0 || step == 1) return 0;               // you may start here for free
    int fromOneBelow = minCost(cost, step - 1) + cost[step - 1];
    int fromTwoBelow = minCost(cost, step - 2) + cost[step - 2];
    return Math.min(fromOneBelow, fromTwoBelow);       // the best route uses the best sub-route
}
```

Call it with `step = cost.length` (the top). We use plain recursion on purpose: this topic is about **why** the recurrence is right. Making it fast is [topic 06](../06-memoization/).

### 5.2 Proof that it is correct: the cut-and-paste argument

Take any cheapest route to the top. Look at its **last move**. It came from stair `top − 1` or `top − 2`; say `top − 1`.

```
 cheapest route to the top:
   [ start ... → top-1 ]  →  top
   \_________________/
     piece P (a route to top-1)          total = cost(P) + cost[top-1]
```

Claim: piece P is a cheapest route to `top − 1`.
Suppose not: some route P' to `top − 1` is cheaper. **Cut** P out and **paste** P' in. The new route is "P', then the same last move", and costs `cost(P') + cost[top-1] < cost(P) + cost[top-1]`. That beats the cheapest route — impossible. So P is cheapest, its cost is `best(top − 1)`, and the route costs `best(top − 1) + cost[top − 1]`.

The same argument works if the last move came from `top − 2`. We do not know in advance which last move the optimum uses, so we **try both and take the min**. That is exactly the recurrence.

Notice what made the argument work: swapping P for P' did **not affect** the last move. The piece and the rest are independent. Hold on to that — it is what fails in section 5.5.

### 5.3 Seeing it on an example

`cost = [10, 15, 20]`, top = 3:

```
best(0) = 0                 (free start)
best(1) = 0                 (free start)
best(2) = min( best(1) + cost[1], best(0) + cost[0] ) = min(0 + 15, 0 + 10) = 10
best(3) = min( best(2) + cost[2], best(1) + cost[1] ) = min(10 + 20, 0 + 15) = 15
```

The answer 15 is built from the optimal sub-answer `best(1) = 0`. The program also rebuilds the route: `start at 1 -> 3`.

### 5.4 Shortest path: the classic example

A small one-way road network without cycles (a **DAG**). Numbers are travel times.

```
   A ──4──> B ──1──> D ──3──> E
   │        ↑        ↑        ↑
   1        2        5        8
   │        │        │        │
   └──────> C ───────┴────────┘

   C has three outgoing edges: C→B (2), C→D (5), C→E (8)
```

Edges: A→B 4, A→C 1, C→B 2, B→D 1, C→D 5, D→E 3, C→E 8.

**Recurrence:** the shortest route to `v` arrives through its *last edge* `u → v`, and (by the road-trip argument) the part before it is a shortest route to `u`:

```
dist(A) = 0
dist(v) = min over every edge u → v of ( dist(u) + weight(u, v) )
```

```java
static int shortest(int target) {
    if (target == SOURCE) return 0;
    int best = UNREACHABLE;
    for (int[] edge : DAG_EDGES) {
        if (edge[1] != target) continue;               // only edges that END at target
        int toPredecessor = shortest(edge[0]);         // optimal sub-answer
        if (toPredecessor == UNREACHABLE) continue;
        best = Math.min(best, toPredecessor + edge[2]);
    }
    return best;
}
```

| Vertex | Incoming edges | Computation | dist |
|--------|----------------|-------------|------|
| A | — | base case | 0 |
| C | A→C (1) | 0 + 1 | 1 |
| B | A→B (4), C→B (2) | min(0 + 4, 1 + 2) | 3 |
| D | B→D (1), C→D (5) | min(3 + 1, 1 + 5) | 4 |
| E | D→E (3), C→E (8) | min(4 + 3, 1 + 8) | **7** |

The program checks this against a brute force that walks every path from A to E (A→B→D→E = 8, A→C→B→D→E = 7, A→C→D→E = 9, A→C→E = 9) — both say **7**.

### 5.5 The counterexample: longest simple path

Now a graph **with cycles**, all edges length 1. We want the longest path that never repeats a vertex.

```
          q ⇄ r          q → r and r → q
          ↓   ⇅          r → t and t → r
          s → t          q → s, s → t
```

Brute force (`GraphPaths.longestSimple`) gives:

| From → to | Longest simple path | Length |
|-----------|---------------------|--------|
| q → r | q → s → t → r | 3 |
| r → t | r → q → s → t | 3 |
| q → t | q → r → t   (or q → s → t) | **2** |

If optimal substructure held, the longest q → t path through r would be "longest q → r" + "longest r → t" = 3 + 3 = **6**. Glue them:

```
q → s → t → r   +   r → q → s → t   =   q → s → t → r → q → s → t
                                         ^   ^   ^       ^   ^   ^
                                         q, s, t each appear twice: NOT a simple path
```

The glued "solution" is not allowed, and the real answer is 2. The cut-and-paste argument fails because the two halves are **not independent**: the first half used up s and t, which the second half needs. The best answer to one piece depends on which vertices the other piece already used — information that the state `(from, to)` does not contain.

> **Lesson:** optimal substructure is a property of the problem **together with the state you chose**. Longest simple path *can* be solved with DP if the state also records the set of visited vertices (bitmask DP, [Module 04](../../04-advanced-dp-patterns/09-bitmask-dp/)), but then the state space is exponential.

(Longest path in a graph **without** cycles — a DAG — does have optimal substructure, because a path in a DAG can never revisit a vertex anyway. See [AtCoder DP G — Longest Path](https://atcoder.jp/contests/dp/tasks/dp_g) in Module 04.)

### 5.6 Counting problems decompose too

"Optimal" suggests min/max, but **counting** problems rely on the same idea: the answer is built from sub-answers by looking at the last choice.

How many ways to climb 4 stairs with 1- or 2-steps? Group every way by its **last move**:

```
 last move is a 1-step (came from stair 3):    111+1   12+1   21+1      -> ways(3) = 3
 last move is a 2-step (came from stair 2):    11+2    2+2              -> ways(2) = 2
                                                                         ------------
                                                                  ways(4) = 5
```

```java
static long waysToClimb(int n) {
    if (n == 0) return 1;     // standing at the bottom: one way (do nothing)
    if (n == 1) return 1;
    return waysToClimb(n - 1) + waysToClimb(n - 2);
}
```

Why **add**? The two groups have **no way in common** (a way cannot end with both a 1-step and a 2-step) and **together contain every way**. Counting rule: *if the groups are disjoint and cover everything, the total is the sum of the group sizes.*

For counting, the "substructure" condition is: **every way to reach `n` is exactly one way to reach a smaller stair plus one last move, and every such combination is a valid way.** Check both directions whenever you write a counting recurrence; most counting bugs (double-counting, missing cases) break one of them.

| Problem type | Combine with | Condition on the groups |
|--------------|--------------|-------------------------|
| minimum / maximum | `min` / `max` | the best overall uses a best piece (cut and paste) |
| counting | `+` | groups by last choice are disjoint and cover everything |
| feasibility (yes/no) | `\|\|` | possible overall ⇔ possible via some last choice |

---

## 6. Memoization (top-down)

Not part of this topic. All three recursions here have overlapping subproblems (e.g. `minCost(cost, 5)` is reached from steps 6 and 7), so caching will make them O(n): [topic 06](../06-memoization/).

## 7. Tabulation (bottom-up)

Not part of this topic. The table in 5.4 is already a bottom-up computation, done by hand in order A, C, B, D, E ([topic 07](../07-tabulation/), and computation order in [topic 11](../11-transition-dependencies-and-computation-order/)).

## 8. Space optimization

Not part of this topic.

---

## 9. Dry run on a full example

`cost = [1, 100, 1, 1, 1, 100, 1, 1, 100, 1]` (n = 10). Each row uses the two rows above it.

| step | from step−1: best(step−1) + cost[step−1] | from step−2: best(step−2) + cost[step−2] | best(step) | came from |
|------|------------------------------------------|------------------------------------------|------------|-----------|
| 0 | — | — | 0 | start |
| 1 | — | — | 0 | start |
| 2 | 0 + 100 = 100 | 0 + 1 = 1 | **1** | 0 |
| 3 | 1 + 1 = 2 | 0 + 100 = 100 | **2** | 2 |
| 4 | 2 + 1 = 3 | 1 + 1 = 2 | **2** | 2 |
| 5 | 2 + 1 = 3 | 2 + 1 = 3 | **3** | 4 (tie) |
| 6 | 3 + 100 = 103 | 2 + 1 = 3 | **3** | 4 |
| 7 | 3 + 1 = 4 | 3 + 100 = 103 | **4** | 6 |
| 8 | 4 + 1 = 5 | 3 + 1 = 4 | **4** | 6 |
| 9 | 4 + 100 = 104 | 4 + 1 = 5 | **5** | 7 |
| 10 (top) | 5 + 1 = 6 | 4 + 100 = 104 | **6** | 9 |

Follow "came from" backwards from the top: 10 ← 9 ← 7 ← 6 ← 4 ← 2 ← 0. That is the route `start at 0 -> 2 -> 4 -> 6 -> 7 -> 9 -> 10` printed by the program. Every row's answer was built from the optimal answers of earlier rows — optimal substructure in action.

---

## 10. Complexity analysis

| Function | Time (plain recursion) | Why | Space |
|----------|------------------------|-----|-------|
| `minCost(cost, n)` | O(2ⁿ) | two calls per stair, no caching — same tree shape as Fibonacci | O(n) depth |
| `waysToClimb(n)` | O(2ⁿ) | identical tree to `fib(n+1)` | O(n) depth |
| `shortest(v)` (plain recursion) | up to the number of paths into v — exponential in bad graphs | each call re-solves its predecessors | O(V) depth |
| `longestSimple` brute force | O(number of simple paths) — exponential | must try every simple path, there is no shortcut | O(V) |

With DP (next topics), the first three become **states × work per state**: O(n), O(n), and O(V + E) (each vertex computed once, each edge looked at once). The longest-simple-path problem has no such shortcut: it is **NP-hard** in general graphs, meaning no known algorithm solves it in polynomial time.

---

## 11. Why this approach works

Combining optimal sub-answers is correct because of the cut-and-paste argument (section 5.2): any optimal solution can be split at its last choice into "one last move" + "a solution to a smaller problem", and that smaller solution must be optimal — otherwise swapping in a better one improves the whole. The argument requires that the swap is always allowed, i.e. the subproblem's choices do not restrict the rest. For counting, correctness comes from splitting the set of all solutions into disjoint groups by last choice that together cover everything.

---

## 12. Common mistakes

- **Assuming every "best" problem has optimal substructure.** Check with cut and paste. If replacing a piece can make the rest invalid (shared vertices, shared items, a budget used elsewhere), the state is missing information.
- **Greedy confusion.** Optimal substructure does *not* mean "take the locally cheapest next step". In the stairs example, from stair 0 the cheapest *next* stair is not necessarily on the best route. DP compares **all** last choices; greedy commits to one.
- **Double-counting in counting problems.** Grouping by something that is not a single, well-defined "last choice" can count one way in two groups (for example, counting combinations as if order mattered). Make the groups disjoint.
- **Missing cases in counting problems.** If some ways do not fit any group (for example, forgetting the 2-step), the total is too small. Make the groups cover everything.
- **Forgetting that the state matters.** "Longest path" fails with state `(from, to)` but works with `(current vertex, visited set)`. When a recurrence gives wrong answers, ask: "what else does the future depend on?" and add it to the state.

---

## 13. When to use this technique / how to recognize similar problems

Ask two questions about any optimization problem:

1. **"What is the last choice of an optimal solution?"** (the last stair, the last edge, the last item taken or skipped)
2. **"After removing it, is the rest an optimal solution of a smaller version of the same problem — and is that smaller problem independent of the last choice?"**

If both answers are clear, write `best(state) = min/max over last choices of (best(smaller state) + cost of the choice)`. For counting problems, ask instead whether grouping by the last choice is disjoint and complete, and add.

Warning signs that the property may fail: "simple path / no repeated vertex", "each item can be used once" without tracking which items are used, "the two halves share a resource".

---

## 14. Summary and revision notes

- **Optimal substructure:** an optimal solution is made of optimal solutions to subproblems. It makes the recurrence *correct*.
- Prove it with **cut and paste**: a non-optimal piece could be swapped for a better one, improving the whole — contradiction.
- Shortest paths ✔, min cost stairs ✔, longest **simple** path ✘ (the halves compete for vertices).
- **Counting** decomposes by the last choice; the groups must be disjoint and complete, and we **add**.
- The property depends on the **state**: missing information breaks independence.

```
┌───────────────────────────────────────────────────────────────────────────────┐
│ best(step) = min( best(step-1) + cost[step-1],  best(step-2) + cost[step-2] ) │
│ best(0) = best(1) = 0                                                         │
│ ways(n)    = ways(n-1) + ways(n-2),   ways(0) = ways(1) = 1                   │
└───────────────────────────────────────────────────────────────────────────────┘
```

---

## Check your understanding

1. In the shortest-path graph, the shortest A → E route passes through D. Without computing, what can you say about its A → D part?
2. Why is "from where you stand, always move to the cheaper of the next two stairs" (greedy) not the same as the DP recurrence? Check it on `cost = [0, 1, 2, 100, 0]`, starting from stair 0.
3. "Count the ways to climb n stairs with steps of 1, 2 or 3." Write the recurrence and base cases, and justify the `+`.
4. A friend writes `longest(q, t) = max over neighbours r of (1 + longest(r, t))` for longest simple paths. Using the graph in 5.5, what does this formula return for `longest(q, t)`, and why is it wrong?

<details>
<summary>Answers</summary>

1. It must be a shortest A → D route (length 4: A → C → B → D). Otherwise swapping in a shorter A → D route would shorten the whole A → E trip.
2. Greedy commits to the locally cheapest next stair; DP compares the best *complete* routes via sub-answers. On `[0, 1, 2, 100, 0]` (top = 5) greedy goes 0 → 1 (1 < 2) → 2 (2 < 100) → 4 (0 < 100) → top, paying 0 + 1 + 2 + 0 = **3**. DP finds 0 → 2 → 4 → top, paying 0 + 2 + 0 = **2**: stepping on the "more expensive" stair 2 directly skips paying for stair 1.
3. `ways(n) = ways(n-1) + ways(n-2) + ways(n-3)`, with `ways(0) = 1` and `ways(negative) = 0` (equivalently `ways(1) = 1`, `ways(2) = 2`). Every way ends with exactly one of the three step sizes (disjoint), and every way to a smaller stair plus the matching last step is valid (complete), so the counts add.
4. From q, neighbours are r and s. `1 + longest(r, t)` = 1 + 3 = 4 (using r → q → s → t), giving the "path" q → r → q → s → t, which repeats q. The formula returns 4 instead of 2, because `longest(r, t)` does not know that q is already used. The state must include the visited set.

</details>

---

⬅ [04 — Overlapping Subproblems](../04-overlapping-subproblems/) · ➡ [06 — Memoization](../06-memoization/) · 📘 [Topic README](README.md)
