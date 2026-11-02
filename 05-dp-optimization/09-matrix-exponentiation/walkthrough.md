# Matrix Exponentiation — Walkthrough

Running example: **the n-th Fibonacci number modulo 10⁹ + 7, for n up to 10¹⁸** ([CSES 1722 Fibonacci Numbers](https://cses.fi/problemset/task/1722)). Second application: **counting walks of length K** ([AtCoder dp_r Walk](https://atcoder.jp/contests/dp/tasks/dp_r)). Third: LC 1220 for huge n, in the solution files.

---

## 1. Intuition

Picture a machine with a crank. Each turn of the crank takes today's state (say, the last two Fibonacci numbers) and produces tomorrow's. To get the state after a million turns you could crank a million times.

But suppose you could build a *new machine* that does what two turns of the old one do, in a single turn. Then build a machine that does what two turns of *that* one do — four original turns. Then 8, 16, 32… After only 60 such doublings you have a machine worth 2⁶⁰ ≈ 10¹⁸ turns. To get exactly 1 000 000 turns you combine the doubled machines whose sizes add up to a million (the binary digits of 1 000 000).

For the trick to work, "two turns of the machine" must itself be a machine *of the same kind*, and small. That is exactly what happens when one step is a **matrix**: the product of two matrices is again a matrix of the same size.

---

## 2. Problem statement in our own words

Given `n` (0 ≤ n ≤ 10¹⁸), output the Fibonacci number `F(n)` modulo 10⁹ + 7, where `F(0) = 0`, `F(1) = 1`, `F(i) = F(i-1) + F(i-2)`. (See [CSES 1722](https://cses.fi/problemset/task/1722).)

---

## 3. Examples

| n | F(n) | F(n) mod 10⁹ + 7 |
|---|------|-------------------|
| 0 | 0 | 0 |
| 1 | 1 | 1 |
| 10 | 55 | 55 |
| 50 | 12 586 269 025 | 586 268 941 |
| 10¹⁸ | (a number with ~2·10¹⁷ digits) | 209 783 453 |

The last row is the point of the topic: the exact value is unthinkably large, and even the O(n) loop modulo p would need 10¹⁸ iterations.

---

## 4. Deriving the solution — the 7 questions

### 4.1 What is changing?

The index `i` of the Fibonacci number we are building, from 0 up to n.

### 4.2 What does the state represent?

`dp[i] = F(i) mod p`. For matrix exponentiation we group the two values the recurrence needs into a **state vector**:

```
s_i = [ F(i)   ]        "everything the next step needs to know"
      [ F(i-1) ]
```

### 4.3 What are the choices?

None — Fibonacci is a counting recurrence with a single formula. (In counting problems such as Climbing Stairs, the "choices" — a step of 1 or of 2 — become the *terms* of the sum.)

### 4.4 What is the recurrence?

`F(i+1) = F(i) + F(i-1)`, and trivially `F(i) = F(i)`. Written for the whole vector:

```
F(i+1) = 1·F(i) + 1·F(i-1)
F(i)   = 1·F(i) + 0·F(i-1)
```

### 4.5 What are the base cases?

`F(0) = 0`, `F(1) = 1`, i.e. `s_1 = [1, 0]`.

### 4.6 What is the order of computation?

Increasing `i`: `s_{i+1}` needs only `s_i`.

### 4.7 Can the state space be optimized?

Space is already O(1) (two variables, see [Space optimization](../01-space-optimization/)). The problem is **time**: n steps. The observation that unlocks the speed-up is that the coefficients `1, 1, 1, 0` above **do not depend on i**. Every step is the *same* linear map. The next two subsections build the tools to exploit that.

### 4.8 Prerequisite: matrices and "one DP step = matrix × vector"

A **matrix** is a rectangular table of numbers. Multiplying matrix `A` (size a × b) by matrix `B` (size b × c) gives `C` (size a × c) with

```
C[i][j] = A[i][0]·B[0][j] + A[i][1]·B[1][j] + ... + A[i][b-1]·B[b-1][j]
        = (row i of A) "dot" (column j of B)
```

Numeric example:

```
[1 2]   [5 6]   [1·5 + 2·7   1·6 + 2·8]   [19 22]
[3 4] · [7 8] = [3·5 + 4·7   3·6 + 4·8] = [43 50]
```

A vector is a matrix with one column, so "matrix × vector" is the same rule. Now look at the Fibonacci step from 4.4 — each new entry is a row of coefficients dotted with the old vector:

```
[F(i+1)]   [1 1]   [F(i)  ]
[F(i)  ] = [1 0] · [F(i-1)]           M = [1 1]
                                          [1 0]
  s_{i+1}  =  M  ·  s_i
```

Check with `i = 5`: `s_5 = [5, 3]`, `M·s_5 = [1·5 + 1·3, 1·5 + 0·3] = [8, 5] = s_6` ✓.

Applying the step repeatedly: `s_n = M · M · … · M · s_1 = M^(n-1) · s_1`. So all we need is the matrix power `M^(n-1)`.

**Two facts about matrix multiplication** that we rely on:

1. It is **associative**: `(A·B)·C = A·(B·C)`. So `M^8 = (M^4)·(M^4)` and `M^10 = M^8 · M^2` — we may group the factors however we like. (It is *not* commutative in general, `A·B ≠ B·A`, but all our factors are powers of the same `M`, and powers of the same matrix do commute.)
2. The **identity matrix** `I` (1s on the diagonal) satisfies `I·A = A`. It plays the role of the number 1, i.e. `M^0 = I` ("zero steps").

A nice bonus for Fibonacci: by induction one can show

```
M^n = [F(n+1)  F(n)  ]          e.g.  M^1 = [1 1]  = [F(2) F(1)]
      [F(n)    F(n-1)]                      [1 0]    [F(1) F(0)]
```

(Induction step: `M^(n+1) = M^n · M`, whose first row is `[F(n+1) + F(n), F(n+1)] = [F(n+2), F(n+1)]`.) So `F(n)` is the top-right entry of `M^n`, which is what the code returns.

**General recipe — the companion matrix.** For any recurrence `f(i) = c1·f(i-1) + c2·f(i-2) + … + ck·f(i-k)`, use the state `[f(i), f(i-1), …, f(i-k+1)]` and the k × k matrix

```
[c1 c2 c3 ... ck]      <- computes the new f(i+1)
[ 1  0  0 ...  0]      <- shifts f(i)   down one slot
[ 0  1  0 ...  0]      <- shifts f(i-1) down one slot
[      ...      ]
[ 0  0 ... 1   0]
```

### 4.9 The key property and why it holds: exponentiation by squaring

First with plain numbers. To compute `3^13` the slow way takes 12 multiplications. Instead write the exponent in binary: `13 = 1101₂ = 8 + 4 + 1`, so

```
3^13 = 3^8 · 3^4 · 3^1
```

and the powers `3^1, 3^2, 3^4, 3^8` are obtained by **squaring** the previous one:

```
3^1 = 3
3^2 = 3^1 · 3^1 = 9
3^4 = 3^2 · 3^2 = 81
3^8 = 3^4 · 3^4 = 6561
3^13 = 3 · 81 · 6561 = 1 594 323      (bits of 13 from right: 1, 0, 1, 1 -> use 3^1, skip 3^2, use 3^4, use 3^8)
```

That is 3 squarings + 2 extra multiplications = 5 instead of 12. In general the exponent `n` has `⌊log₂ n⌋ + 1` bits; we do one squaring per bit and at most one extra multiplication per bit, so at most `2·(⌊log₂ n⌋ + 1)` multiplications. For `n = 10¹⁸` that is at most 120.

The loop, reading the bits from the lowest:

```
result = 1; square = x
while n > 0:
    if lowest bit of n is 1: result = result * square     # include x^(2^bit)
    square = square * square                             # x^(2^bit) -> x^(2^(bit+1))
    n = n >> 1                                           # drop the lowest bit
```

**Why the same loop works for matrices.** The derivation used only two facts: that grouping does not matter (associativity, so `x^13 = x^8·x^4·x^1` regardless of how we bracket it) and that there is a "1" to start from. Matrices have both (associativity and `I`). So we may replace `x` by `M`, `1` by `I`, and `*` by matrix multiplication. Each matrix multiplication of S × S matrices costs S³ number multiplications, giving **O(S³ · log n)** in total.

**Modular arithmetic.** Answers are "mod p" with `p = 10⁹ + 7`. Since `(a + b) mod p` and `(a·b) mod p` can be taken after every operation without changing the final remainder, we reduce every entry after every multiplication. Entries stay below p ≈ 10⁹, so a product is below 10¹⁸, which fits in a Java `long` (max ≈ 9.22·10¹⁸). Add one product at a time and reduce immediately, so the running sum never exceeds about 10¹⁸ + 10⁹.

---

## 5. Brute force: recursion

```java
static long fib(long n) {
    if (n <= 1) return n;
    return fib(n - 1) + fib(n - 2);
}
```

```
                fib(5)
             /         \
        fib(4)          fib(3)
        /    \          /    \
    fib(3)  fib(2)  fib(2)  fib(1)
    /   \
 fib(2) fib(1)        ... fib(3) computed twice, fib(2) three times
```

About φⁿ ≈ 1.618ⁿ calls (exponential). Useless beyond n ≈ 45.

---

## 6. Memoization (top-down)

Cache each `fib(i)`: n distinct states, O(1) work each → O(n) time, O(n) memory and a recursion depth of n. For n = 10¹⁸ neither the time, the memory nor the stack is possible. Skipped in the solution files for that reason.

---

## 7. Tabulation (bottom-up)

```java
static long fibLinear(long n) {
    long previous = 0, current = 1;          // F(0), F(1)
    if (n == 0) return 0;
    for (long i = 2; i <= n; i++) {
        long next = (previous + current) % MOD;
        previous = current;
        current = next;
    }
    return current;
}
```

```
i        0  1  2  3  4  5  6   7   8   9  10
F(i)     0  1  1  2  3  5  8  13  21  34  55
```

O(n) time, O(1) space. This is our **slow reference**: it is clearly correct, and the solution file compares the matrix version against it on 300 random `n < 5000`. For `n = 10¹⁸` it would need 10¹⁸ iterations.

---

## 8. Optimization: matrix exponentiation

### 8.1 The code

```java
static long[][] multiply(long[][] a, long[][] b) {
    int size = a.length;
    long[][] product = new long[size][size];
    for (int i = 0; i < size; i++)
        for (int k = 0; k < size; k++) {
            if (a[i][k] == 0) continue;               // free speed-up for sparse matrices
            for (int j = 0; j < size; j++)
                product[i][j] = (product[i][j] + a[i][k] * b[k][j]) % MOD;
        }
    return product;
}

static long[][] power(long[][] base, long exponent) {
    int size = base.length;
    long[][] result = new long[size][size];
    for (int i = 0; i < size; i++) result[i][i] = 1;   // identity = zero steps
    long[][] square = base;                            // base^(2^bit)
    while (exponent > 0) {
        if ((exponent & 1) == 1) result = multiply(result, square);
        square = multiply(square, square);
        exponent >>= 1;
    }
    return result;
}

static long fibMatrix(long n) {
    long[][] step = { {1, 1}, {1, 0} };
    return power(step, n)[0][1];          // M^n = [[F(n+1), F(n)], [F(n), F(n-1)]]
}
```

`exponent & 1` reads the lowest bit (1 if odd), and `exponent >>= 1` divides by 2, dropping that bit — the same "binary digits of n" as in section 4.9.

### 8.2 Application 2: counting walks of length K (AtCoder dp_r Walk)

Problem in our words: given a directed graph on N ≤ 50 vertices as an adjacency matrix, count the walks that use exactly K edges (K up to 10¹⁸), modulo 10⁹ + 7. See [Walk](https://atcoder.jp/contests/dp/tasks/dp_r).

**The slow DP.** `ways_s[v]` = number of walks of s edges ending at `v`. Start with `ways_0[v] = 1` (standing still), and

```
ways_{s+1}[v] = sum over u with an edge u -> v of ways_s[u]
```

O(K·N²) — fine for K = 1000, hopeless for K = 10¹⁸. But the step is linear with fixed coefficients (the adjacency matrix), so matrix power applies.

**Why `(A^K)[u][v]` counts walks from u to v.** By induction on K. For K = 1, `A[u][v]` is 1 exactly when the one-edge walk `u → v` exists. Suppose it holds for K. A walk of K + 1 edges from `u` to `v` is a walk of K edges from `u` to some vertex `w`, followed by the edge `w → v`. Summing over `w`:

```
#walks_{K+1}(u, v) = sum over w of #walks_K(u, w) · A[w][v] = (A^K · A)[u][v] = (A^(K+1))[u][v]
```

— which is exactly the matrix-multiplication formula. The total number of walks is the sum of all entries of `A^K`.

Small example: edges `0→1, 0→2, 1→2, 2→0`.

```
A   = [0 1 1]     A^2 = [1 0 1]     A^3 = [1 1 1]
      [0 0 1]           [1 0 0]           [0 1 1]
      [1 0 0]           [0 1 1]           [1 0 1]
sum:      4                 5                 7
```

Check `A^2` row 0 by listing: walks of 2 edges from 0 are `0→1→2` and `0→2→0`, so row 0 is "one walk to 2, one walk to 0" = `[1, 0, 1]` ✓. And the 7 walks of length 3: `0→1→2→0, 0→2→0→1, 0→2→0→2, 1→2→0→1, 1→2→0→2, 2→0→1→2, 2→0→2→0` ✓. [CountingWalks.java](solution/CountingWalks.java) checks these values and compares with the slow DP on 200 random graphs.

Cost for the real limits: N = 50 → one multiplication is 50³ = 125 000 operations; about 120 multiplications for K = 10¹⁸ → 1.5·10⁷ operations. Instant.

### 8.3 Application 3: string-counting state machines (LC 935, LC 1220 with huge n)

[Count Vowels Permutation](https://leetcode.com/problems/count-vowels-permutation/) counts strings of length n over the vowels where each vowel may only be followed by certain vowels. State: `count[c]` = strings of the current length ending in `c`. The "who may follow whom" table never changes, so with `T[to][from] = 1` when `from` may be followed by `to`:

```
count_{len+1} = T · count_len          answer(n) = sum of entries of T^(n-1) · [1,1,1,1,1]
```

LeetCode only asks for n ≤ 2·10⁴, so the O(n) rolling DP from [Rolling arrays](../02-rolling-arrays/) passes. But if the interviewer asks "what if n = 10¹⁸?", the answer is a 5 × 5 matrix power: 125 · 120 = 15 000 operations. [VowelPermutationHugeN.java](solution/VowelPermutationHugeN.java) implements both and cross-checks them. [Knight Dialer](https://leetcode.com/problems/knight-dialer/) works identically with a 10 × 10 matrix (exercise in section 15).

### 8.4 When does matrix exponentiation apply?

Check all four:

1. **Fixed transition.** The coefficients do not depend on the step index i. (`f(i) = f(i-1) + i` is not of this form directly — though adding a state "i" and a state "1" fixes it.)
2. **Linear transition.** New values are sums of constant multiples of old values. A step with `max` or `min` is not linear. (Advanced aside: `max`/`+` steps can use a modified "(max, +) matrix product", which is also associative — the same squaring trick works.)
3. **Small state.** S³ must be cheap: S up to about 100–200.
4. **Huge number of steps.** If n ≤ 10⁷, the plain O(n·S²) loop is simpler and usually faster.

---

## 9. Dry run on a full example

Compute `F(10)` with `power(M, 10)`. `10 = 1010₂`.

| iteration | exponent (binary) | lowest bit | action on `result` | `result` after | `square` after squaring |
|-----------|-------------------|------------|--------------------|----------------|-------------------------|
| start | 1010 | — | — | `I` | `M = [[1,1],[1,0]]` |
| 1 | 1010 | 0 | skip | `I` | `M^2 = [[2,1],[1,1]]` |
| 2 | 101 | 1 | `I · M^2` | `M^2 = [[2,1],[1,1]]` | `M^4 = [[5,3],[3,2]]` |
| 3 | 10 | 0 | skip | `M^2` | `M^8 = [[34,21],[21,13]]` |
| 4 | 1 | 1 | `M^2 · M^8` | `M^10` | `M^16` (unused) |

Last multiplication in detail:

```
M^2 · M^8 = [2 1] · [34 21]  =  [2·34 + 1·21   2·21 + 1·13]  =  [89 55]
            [1 1]   [21 13]     [1·34 + 1·21   1·21 + 1·13]     [55 34]
```

`M^10 = [[F(11), F(10)], [F(10), F(9)]] = [[89, 55], [55, 34]]`, so `F(10) = 55` ✓. Total: 4 squarings + 2 extra multiplications instead of 10 steps; for n = 10¹⁸, at most 120 multiplications instead of 10¹⁸ steps.

---

## 10. Complexity analysis

| Approach | Time | Space | Explanation |
|----------|------|-------|-------------|
| Recursion | O(φⁿ) | O(n) stack | two branches per call, heavy overlap |
| Memoization | O(n) | O(n) | each F(i) once |
| Tabulation (rolling) | O(n·S²) | O(S) | n steps; one step = S × S matrix–vector product (S = 2 for Fibonacci) |
| Matrix power | O(S³·log n) | O(S²) | ≤ 2·log₂ n multiplications of S × S matrices, S³ each |

For Fibonacci S = 2, so matrix power is O(8 · log n) = O(log n). For Walk S = N = 50. **Break-even:** matrix power wins when `S³ log n < S² n`, i.e. roughly when `n > S·log n`. For S = 50 and n = 1000 the plain loop (2.5·10⁶) and the matrix power (≈ 2.5·10⁶) are similar; for n = 10¹⁸ there is no contest.

---

## 11. Why this approach works

The DP computes `s_n = M · M · … · M · s_1` with `n - 1` factors. Matrix multiplication is associative, so grouping these identical factors into blocks of sizes 1, 2, 4, 8, … (according to the binary digits of `n - 1`) gives the same product. Exponentiation by squaring produces exactly those blocks (each is the square of the previous) and multiplies together the ones whose bit is 1. Reducing modulo p after every operation is valid because remainders are preserved by addition and multiplication. Hence `power(M, n)` contains exactly the numbers the O(n) loop would have produced, computed in O(log n) multiplications.

---

## 12. Common mistakes

1. **Overflow.** Multiplying two residues gives up to ≈10¹⁸; adding several such products without reducing overflows `long`. Reduce after each `+= a·b`. Never use `int` for the products.
2. **Off-by-one in the exponent.** Decide what the state vector at step 1 (or 0) is, then the number of steps is `n - 1` (or `n`). Verify with n = 0, 1, 2.
3. **Starting `result` at the zero matrix** instead of the identity. Everything then stays zero.
4. **Transposed matrix.** For `next = T · count`, the entry `T[to][from]` describes the move `from → to`. Writing `T[from][to]` computes the reverse process (harmless for symmetric graphs, wrong otherwise). Summing *all* entries hides this mistake, so test a non-symmetric example.
5. **Forgetting `n = 0`.** `power(M, 0)` must return `I`, and the answer for n = 0 must be read correctly.
6. **Using it when n is small.** With S = 100 one matrix multiplication costs 10⁶. For n = 1000 the plain loop costs n·S² = 10⁷, while the matrix power needs about 20 multiplications = 2·10⁷: the loop wins. Estimate both costs before choosing.
7. **Applying it to a non-linear or index-dependent recurrence.** `max`, `min`, or coefficients that change with i break the method.

---

## 13. When to use this technique / how to recognize similar problems

- n (length, number of steps, K) is enormous: 10⁹ … 10¹⁸.
- The DP over n has a **small** state (≤ ~100 numbers) per step.
- The step is "new values = fixed weighted sums of old values" — counting paths, counting strings with local rules, tilings, linear recurrences.
- The answer is asked modulo a prime, a strong hint that values are counts that grow exponentially.

Recipe:

1. Write the O(n) DP and identify the state vector.
2. Write each new state entry as a weighted sum of old entries; the weights form the matrix.
3. Check the matrix on two steps by hand against the O(n) DP.
4. Compute `M^(steps)` by squaring, apply it to the initial vector, read the answer.

---

## 14. Summary and revision notes

- One step of a fixed linear DP = multiplication by a fixed matrix `M`; n steps = `M^n`.
- Exponentiation by squaring: square repeatedly, multiply in the squares whose bit is 1; ≤ 2·log₂ n multiplications.
- Works because matrix multiplication is associative and has an identity.
- Cost O(S³ log n); worth it only when n ≫ S.
- `(A^K)[u][v]` = number of walks of K edges from u to v.
- Reduce mod p after every product; start from the identity.

```
+---------------------------------------------------------+
|  [F(i+1)]   [1 1] [F(i)  ]          M^n = [F(n+1) F(n) ] |
|  [F(i)  ] = [1 0] [F(i-1)]                [F(n)  F(n-1)] |
|                                                         |
|  power(M, n): result = I; sq = M                        |
|    while n > 0: if n odd: result = result * sq          |
|                 sq = sq * sq;  n = n / 2                |
+---------------------------------------------------------+
```

---

## 15. Exercises

**Exercise 1.** Write the matrix for [N-th Tribonacci Number](https://leetcode.com/problems/n-th-tribonacci-number/) `T(i) = T(i-1) + T(i-2) + T(i-3)` with `T(0) = 0, T(1) = 1, T(2) = 1`. Which power and which entry give `T(n)` for n ≥ 2?

<details><summary>Answer</summary>

State `s_i = [T(i), T(i-1), T(i-2)]`, matrix

```
[1 1 1]
[1 0 0]
[0 1 0]
```
`s_2 = [1, 1, 0]` and `s_n = M^(n-2) · s_2`; `T(n)` is the first entry of `s_n`. Check: `s_3 = [1+1+0, 1, 1] = [2, 1, 1]`, `s_4 = [4, 2, 1]` ✓ (T(3) = 2, T(4) = 4).
</details>

**Exercise 2.** For [Knight Dialer](https://leetcode.com/problems/knight-dialer/), describe the matrix, its size and the cost for n = 10¹⁸.

<details><summary>Answer</summary>

10 × 10 matrix with `T[to][from] = 1` when a knight can jump from digit `from` to digit `to` on the phone pad (e.g. from 1 to 6 and 8; from 5 nowhere). The answer for n digits is the sum of all entries of `T^(n-1)` (start: one number of length 1 per digit). Cost: ≤ 120 multiplications of 10³ = 1000 operations ≈ 1.2·10⁵ operations.
</details>

**Exercise 3.** [Domino and Tromino Tiling](https://leetcode.com/problems/domino-and-tromino-tiling/) satisfies `f(n) = 2·f(n-1) + f(n-3)` with `f(0) = 1, f(1) = 1, f(2) = 2`. Give the companion matrix and verify `f(3)` and `f(4)` with one and two matrix–vector steps.

<details><summary>Answer</summary>

State `[f(i), f(i-1), f(i-2)]`, matrix

```
[2 0 1]
[1 0 0]
[0 1 0]
```
From `s_2 = [2, 1, 1]`: `s_3 = [2·2 + 0·1 + 1·1, 2, 1] = [5, 2, 1]`, so f(3) = 5. `s_4 = [2·5 + 0·2 + 1·1, 5, 2] = [11, 5, 2]`, so f(4) = 11.
</details>

**Exercise 4.** How would you count walks with **at most** K edges (1 ≤ length ≤ K) in O(N³ log K)?

<details><summary>Answer</summary>

Use the 2N × 2N block matrix `B = [[A, I], [0, I]]`. By induction `B^k = [[A^k, I + A + … + A^(k-1)], [0, I]]` (multiply `B^k · B`: the top-right block becomes `A^k + (I + … + A^(k-1))`). So the top-right block of `B^(K+1)` is `I + A + … + A^K`; subtract `I` (the walks of length 0) and sum all entries. One matrix power of size 2N: O((2N)³ log K) = O(N³ log K).
</details>
