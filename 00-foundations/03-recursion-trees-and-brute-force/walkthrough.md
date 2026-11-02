# Recursion Trees and Brute Force — Walkthrough

> Goal: see *all* the work a recursive function does in one picture, count it, and use the same picture to design brute-force solutions that try every choice.

---

## 1. Intuition (no code, no math — an everyday analogy)

### The choose-your-own-adventure book

In a choose-your-own-adventure book, every page ends with a choice: *"To open the door, turn to page 12. To run away, turn to page 30."* If you wanted to read **every possible story**, you would have to follow every choice on every page. Drawn out, the stories form a tree:

```
                     page 1
                  /          \
           open door        run away
            /     \          /     \
         fight   talk     hide    climb
          ...     ...      ...     ...
```

- Each **page** is a node.
- Each **choice** is a branch.
- Each **ending** is a leaf.
- If every page has 2 choices and every story is 10 choices long, there are 2¹⁰ = **1024** endings.

That is exactly what a recursion tree is, and "reading every story" is exactly what **brute force** does. It is guaranteed to find the best ending (or count the happy endings), because it looks at all of them. Its weakness is also clear: the number of stories doubles with every extra choice.

---

## 2. Problem statement in our own words

We study three brute-force functions (all in [`solution/BruteForce.java`](solution/BruteForce.java)):

1. **`fib(n)`** — our running example. Its tree has two branches per node.
2. **`countSubsetsWithSum(values, target)`** — how many subsets (selections of elements, order does not matter) of `values` add up exactly to `target`? For each element we choose: **take** it or **skip** it.
3. **`maxSubsetSumAtMost(values, limit)`** — the largest total we can pick without going over `limit`. The same take/skip tree, but now we keep the *best* leaf instead of *counting* leaves.

[`solution/RecursionTreeTracer.java`](solution/RecursionTreeTracer.java) prints the trees for (1) and (2).

---

## 3. Examples (input → output, with an explanation)

| Call | Output | Explanation |
|------|--------|-------------|
| `fib(4)` | 3 | Tree has 9 nodes (drawn below). |
| `countSubsetsWithSum([1,2,3], 3)` | 2 | The subsets {1,2} and {3}. |
| `countSubsetsWithSum([1,2,3], 6)` | 1 | Only {1,2,3}. |
| `countSubsetsWithSum([1,2,3], 0)` | 1 | Only the empty subset {} (take nothing). |
| `countSubsetsWithSum([2,4,6,10], 16)` | 2 | {6,10} and {2,4,10}. |
| `maxSubsetSumAtMost([5,3,9], 10)` | 9 | {9}; {5,3} = 8 is worse; {5,9}, {3,9} are over 10. |
| `maxSubsetSumAtMost([7], 3)` | 0 | Nothing fits, so take nothing. |

---

## 4. Deriving the solution — the 7 questions

For **`countSubsetsWithSum`**. (This is your first full derivation; the same seven steps return in every problem.)

### 4.1 What is changing?
Two things: **which element we are deciding about** (`index`, from 0 to n) and **how much of the target is still missing** (`remaining`).

### 4.2 What does the state represent?
`count(index, remaining)` = "the number of ways to choose some of the elements `values[index..n-1]` so that they add up to exactly `remaining`".

### 4.3 What are the choices?
For `values[index]`: **take** it (then we need `remaining − values[index]` from the rest) or **skip** it (we still need `remaining`).

### 4.4 What is the recurrence?
```
count(index, remaining) = count(index + 1, remaining - values[index])   // take
                        + count(index + 1, remaining)                   // skip
```
Add, because a subset either contains `values[index]` or it does not — never both — so the two groups of subsets are separate and together cover everything.

### 4.5 What are the base cases?
`index == n`: all elements are decided, so we hold one complete subset. It counts as **1** if `remaining == 0` (it hit the target exactly), else **0**.

### 4.6 What is the order of computation?
Each call depends only on calls with `index + 1`. Recursion handles it; a table would fill from `index = n` down to `0`.

### 4.7 Can the state space be optimized?
Not yet. Notice, though, that two different paths can reach the same `(index, remaining)` pair — the seed of [topic 04](../04-overlapping-subproblems/). Turning this into DP is [Subset Sum](../../03-subsequence-and-knapsack-dp/03-subset-sum/).

---

## 5. Brute force: recursion

### 5.1 Drawing a recursion tree by hand

Recipe:

1. Write the first call at the top. That is the **root**.
2. Under each node, draw one child for each recursive call it makes, **left to right in the order the code makes them**.
3. Stop at base cases. These are the **leaves**. Write their return values.
4. To get the answer, combine values **bottom-up**: each node's value is computed from its children.

`fib(4)`:

```
                        fib(4)=3
                   /               \
             fib(3)=2               fib(2)=1
            /        \              /       \
       fib(2)=1    fib(1)=1    fib(1)=1   fib(0)=0
       /      \
  fib(1)=1  fib(0)=0
```

**9 nodes** (= 9 calls), **5 leaves**, depth 3.

`fib(5)` adds a whole `fib(4)` subtree on the left and a `fib(3)` subtree on the right:

```
                                  fib(5)
                    /                               \
                fib(4)                              fib(3)
            /           \                       /          \
        fib(3)          fib(2)              fib(2)        fib(1)
       /      \         /     \             /     \
   fib(2)   fib(1)  fib(1)  fib(0)      fib(1)  fib(0)
   /    \
fib(1) fib(0)
```

**15 nodes.** That observation is the key to counting.

### 5.2 Printing the tree with a program

Hand-drawing gets tiring quickly. A trick: pass the depth into each call and indent the output by it. Children then appear under their parent, one step to the right — the tree turned sideways.

```java
static long tracedFib(int n, int depth) {
    printNode(depth, "fib(" + n + ")" + (n <= 1 ? "  -> base case " + n : ""));
    if (n <= 1) return n;
    return tracedFib(n - 1, depth + 1) + tracedFib(n - 2, depth + 1);
}
```

Output of `java RecursionTreeTracer.java`:

```
fib(4)
|  fib(3)
|  |  fib(2)
|  |  |  fib(1)  -> base case 1
|  |  |  fib(0)  -> base case 0
|  |  fib(1)  -> base case 1
|  fib(2)
|  |  fib(1)  -> base case 1
|  |  fib(0)  -> base case 0
```

Rotate it 90° in your head and you get the drawing above. Use this trick on any recursive function you want to understand.

### 5.3 Counting nodes — method 1: a recurrence for the count

Let `N(n)` be the number of nodes in the tree of `fib(n)`. The root is one node, and below it hang the complete trees of `fib(n-1)` and `fib(n-2)`:

```
N(0) = 1,  N(1) = 1                    (a leaf by itself)
N(n) = 1 + N(n-1) + N(n-2)             (the root + the two subtrees)
```

| n | 0 | 1 | 2 | 3 | 4 | 5 | 6 | 10 | 20 | 30 | 40 |
|---|---|---|---|---|---|---|---|----|----|----|----|
| N(n) | 1 | 1 | 3 | 5 | 9 | 15 | 25 | 177 | 21,891 | 2,692,537 | 331,160,281 |

These match the program's counters. (In fact N(n) = 2·fib(n+1) − 1; you can check it in the table. You do not need to memorize that.)

### 5.4 From the count to O(2ⁿ)

We want to know how fast N(n) grows. Two simple bounds:

**Upper bound.** Each node has at most 2 children, and the tree is at most n levels deep (each call lowers n by at least 1). A tree like that has at most 1 + 2 + 4 + … + 2ⁿ = 2ⁿ⁺¹ − 1 nodes. So **N(n) = O(2ⁿ)**.

```
level 0:   1 node                  fib(n)
level 1:   ≤ 2 nodes            fib(n-1) fib(n-2)
level 2:   ≤ 4 nodes          ...
level k:   ≤ 2^k nodes
           -------------
total      ≤ 2^(n+1) - 1
```

**Lower bound.** N(n−1) ≥ N(n−2), so N(n) = 1 + N(n−1) + N(n−2) ≥ 2·N(n−2). Every 2 steps of n, the count at least doubles: **N(n) ≥ 2^(n/2)** ≈ 1.41ⁿ.

So the true growth is between 1.41ⁿ and 2ⁿ (it is about 1.618ⁿ, the "golden ratio" to the power n). Either way it is **exponential**: going from n = 40 to n = 50 multiplies the work by about 120. In everyday talk we just say **plain recursive Fibonacci is O(2ⁿ)** — a correct upper bound.

### 5.5 Counting nodes — method 2: level by level (the take/skip tree)

When every node has exactly 2 children until a fixed depth, count level by level. Here is the take/skip tree for `[1, 2, 3]` with target 3. Left branch = **take**, right branch = **skip**; each node shows the subset chosen so far.

```
                                   {}  decide 1
                    take 1 /                       \ skip 1
                 {1} decide 2                        {} decide 2
            take /         \ skip              take /         \ skip
       {1,2} decide 3    {1} decide 3      {2} decide 3     {} decide 3
        /       \          /      \          /     \          /      \
   {1,2,3}    {1,2}     {1,3}     {1}     {2,3}    {2}      {3}      {}
    sum 6     sum 3 ✔   sum 4    sum 1    sum 5   sum 2   sum 3 ✔   sum 0
```

| Level | Nodes | Meaning |
|-------|-------|---------|
| 0 | 1 | nothing decided |
| 1 | 2 | element 1 decided |
| 2 | 4 | elements 1, 2 decided |
| 3 | 8 | all decided: **every subset appears exactly once as a leaf** |
| **total** | **15 = 2⁴ − 1** | |

For n elements: 2ⁿ leaves (all subsets) and 2ⁿ⁺¹ − 1 nodes in total. **Time O(2ⁿ).** Two leaves have sum 3, so the answer is **2**.

The program prints the same tree:

```
decide 1  (so far {})
|  decide 2  (so far {1})
|  |  decide 3  (so far {1,2})
|  |  |  {1,2,3} sum=6
|  |  |  {1,2} sum=3  <-- matches target 3
|  |  decide 3  (so far {1})
|  |  |  {1,3} sum=4
|  |  |  {1} sum=1
|  decide 2  (so far {})
|  |  decide 3  (so far {2})
|  |  |  {2,3} sum=5
|  |  |  {2} sum=2
|  |  decide 3  (so far {})
|  |  |  {3} sum=3  <-- matches target 3
|  |  |  {} sum=0
```

### 5.6 Brute force = "try every choice"

Here is the code for the counting version:

```java
static long countSubsetsWithSum(int[] values, int index, int remaining) {
    if (index == values.length) return (remaining == 0) ? 1 : 0;   // a complete subset
    long take = countSubsetsWithSum(values, index + 1, remaining - values[index]);
    long skip = countSubsetsWithSum(values, index + 1, remaining);
    return take + skip;
}
```

And the "best value" version on the **same tree**. Only two things change: the combination (`max` instead of `+`), and a rule that forbids taking an element that does not fit.

```java
static int maxSubsetSumAtMost(int[] values, int index, int limit) {
    if (index == values.length) return 0;                  // nothing left to add
    int skip = maxSubsetSumAtMost(values, index + 1, limit);
    int take = Integer.MIN_VALUE;                          // "impossible" unless it fits
    if (values[index] <= limit)
        take = values[index] + maxSubsetSumAtMost(values, index + 1, limit - values[index]);
    return Math.max(take, skip);
}
```

This is the general template for brute force in DP problems:

```
solve(state):
    if state is final:          return the value of this complete choice sequence
    for each allowed choice c:  result_c = (gain from c) + solve(next state after c)
    return combine(results)     // + to count, max/min to optimize, || to check "possible?"
```

### 5.7 Why bother writing something this slow?

1. **It is correct by construction.** It looks at every possibility, so it cannot miss the answer. You can use it to test faster solutions on small inputs.
2. **It reveals the state.** The parameters of the brute-force function (`index`, `remaining`) become the dimensions of the DP table.
3. **It reveals the choices and base cases.** These become the recurrence.
4. **DP is "brute force without the repeats".** The next two topics show that the slow part is solving the same `(index, remaining)` more than once, and [memoization](../06-memoization/) fixes exactly that, keeping this very code.

---

## 6. Memoization (top-down)

Not part of this topic. Look again at the `fib(5)` tree: `fib(3)` appears twice and `fib(2)` three times. [Topic 04](../04-overlapping-subproblems/) counts these repeats and [topic 06](../06-memoization/) caches them.

## 7. Tabulation (bottom-up)

Not part of this topic ([topic 07](../07-tabulation/)).

## 8. Space optimization

Not part of this topic. Note only that a brute-force recursion already uses little memory: O(depth), see section 10.

---

## 9. Dry run on a full example

`maxSubsetSumAtMost([5, 3, 9], 10)`. Each row is one call; `limit` is what is still allowed.

| Call (index, limit) | Element | Skip result | Take allowed? | Take result | Returns |
|---------------------|---------|-------------|---------------|-------------|---------|
| (3, any) | — | — | — | — | 0 (base) |
| (2, 10) | 9 | (3,10) = 0 | 9 ≤ 10 yes | 9 + (3,1) = 9 | **9** |
| (2, 7) | 9 | (3,7) = 0 | 9 ≤ 7 no | — | **0** |
| (1, 10) | 3 | (2,10) = 9 | 3 ≤ 10 yes | 3 + (2,7) = 3 | **9** |
| (2, 5) | 9 | (3,5) = 0 | no | — | 0 |
| (2, 2) | 9 | 0 | no | — | 0 |
| (1, 5) | 3 | (2,5) = 0 | 3 ≤ 5 yes | 3 + (2,2) = 3 | **3** |
| (0, 10) | 5 | (1,10) = 9 | 5 ≤ 10 yes | 5 + (1,5) = 8 | **9** |

The best is to skip 5, skip 3 and take 9. The "take 5 and 3" branch gives only 8.

---

## 10. Complexity analysis

**Time = number of nodes × work per node.**

| Function | Nodes in the tree | Work per node | Time |
|----------|-------------------|---------------|------|
| `fib(n)` | 2·fib(n+1) − 1, between 2^(n/2) and 2ⁿ⁺¹ | O(1) | **O(2ⁿ)** |
| `countSubsetsWithSum` (n elements) | exactly 2ⁿ⁺¹ − 1 | O(1) | **O(2ⁿ)** |
| `maxSubsetSumAtMost` (n elements) | at most 2ⁿ⁺¹ − 1 (fewer when "take" is not allowed) | O(1) | **O(2ⁿ)** |

**Space = maximum depth** (the stack holds one root-to-leaf path at a time): O(n) for all three.

What does O(2ⁿ) mean in practice? With ~10⁸ simple operations per second:

| n | 2ⁿ | time |
|---|----|------|
| 20 | ≈ 10⁶ | instant |
| 30 | ≈ 10⁹ | seconds |
| 40 | ≈ 10¹² | hours |
| 50 | ≈ 10¹⁵ | months |

That is why brute force is a *starting point*, never the final answer, when n can be large.

---

## 11. Why this approach works

Brute force is correct because the tree contains **every** possible sequence of choices exactly once: at each node the branches cover all options (take or skip) and the options do not overlap. So every subset is one leaf, and counting the matching leaves (or keeping the best one) gives the exact answer. The leap of faith from [topic 02](../02-recursion-fundamentals/) proves it level by level.

---

## 12. Common mistakes

- **Drawing children in the wrong order.** The tree must follow the order of calls in the code, or your dry runs will disagree with the program. Left child = first call.
- **Counting only leaves.** Time is the number of *all* nodes (calls), not just leaves. For binary trees they differ by a factor of about 2, which does not change the big-O but matters for exact counts.
- **Stopping early on a match.** For `countSubsetsWithSum` with `remaining == 0` at `index < n`, returning 1 immediately would miss subsets that also include zeros later. Decide every element and check at the end.
- **Forgetting the "skip everything" leaf.** The empty subset is a real subset. `count([1,2,3], 0) = 1`, not 0.
- **Allowing a "take" that breaks a constraint.** In `maxSubsetSumAtMost` you must check `values[index] <= limit` before taking, or the result can exceed the limit.
- **Concluding the problem "needs" 2ⁿ time.** Brute force needs it. The *problem* often does not — that is the whole point of DP.

---

## 13. When to use this technique / how to recognize similar problems

- **Draw the tree** whenever you write a recursive function and want to know how much work it does, or whether work repeats.
- **Write take/skip brute force** when a problem talks about choosing a *subset* or a *subsequence* of elements: "pick some items", "select a subsequence", "partition into two groups", "assign + or −".
- **Write multi-way choice brute force** when each step has a fixed set of moves: "1 or 2 steps", "right or down", "one of these coins", "a die shows 1..6".

---

## 14. Summary and revision notes

- Recursion tree: node = call, child = call it makes, leaf = base case.
- **Time = (number of nodes) × (work per node). Space = depth.**
- Fibonacci: N(n) = 1 + N(n−1) + N(n−2), which is exponential (between 2^(n/2) and 2ⁿ). Say O(2ⁿ).
- Take/skip over n elements: 2ⁿ leaves = all subsets, 2ⁿ⁺¹ − 1 nodes.
- Brute force = try every choice; combine with `+` (count), `max`/`min` (optimize), `||` (possible?).
- Brute force reveals the **state, choices and base cases** that DP will reuse.

```
┌──────────────────────────────────────────────────────────────────────┐
│ count(i, rem) = count(i+1, rem - v[i])  +  count(i+1, rem)           │
│                       take v[i]               skip v[i]              │
│ count(n, rem) = (rem == 0) ? 1 : 0                                   │
└──────────────────────────────────────────────────────────────────────┘
```

---

## Check your understanding

1. Draw (or print with the tracer) the recursion tree of `fib(3)`. How many nodes and how many leaves does it have?
2. A take/skip brute force runs on an array of 10 elements. How many leaves and how many nodes does its tree have? How many if the array has 20 elements?
3. A function makes **three** recursive calls, each on `n − 1`, and stops at `n = 0`. How many nodes does its tree have for `n = 3`? What is its time complexity?
4. Change `countSubsetsWithSum` so it answers "is there **any** subset with sum equal to target?" (true/false). What changes in the combine step and the base case?

<details>
<summary>Answers</summary>

1. `fib(3)` → `fib(2)`, `fib(1)`; `fib(2)` → `fib(1)`, `fib(0)`. **5 nodes**, **3 leaves** (`fib(1)`, `fib(0)`, `fib(1)`).
2. 10 elements: 2¹⁰ = **1,024 leaves**, 2¹¹ − 1 = **2,047 nodes**. 20 elements: 2²⁰ = 1,048,576 leaves, 2²¹ − 1 = 2,097,151 nodes.
3. Levels have 1, 3, 9, 27 nodes: total **40** = (3⁴ − 1)/2. Time **O(3ⁿ)**.
4. Return `boolean`. Base case: `index == n` → `remaining == 0`. Combine with `||`: `return take || skip;` (Java's `||` even skips the second call when the first is already `true`.)

</details>

---

⬅ [02 — Recursion Fundamentals](../02-recursion-fundamentals/) · ➡ [04 — Overlapping Subproblems](../04-overlapping-subproblems/) · 📘 [Topic README](README.md)
