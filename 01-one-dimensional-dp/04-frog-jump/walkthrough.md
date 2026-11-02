# Frog Jump — Walkthrough

## 1. Intuition

A frog sits on the first of a row of stones. The stones have different heights, and every hop hurts the frog's legs in proportion to how far up or down it goes: hopping from height 10 to height 40 costs 30, hopping from 40 to 20 costs 20. The frog wants to reach the last stone with the least total pain.

Same trick as before: stand on the **last** stone and look back. The frog arrived from one of the stones within hopping range. For each of those, "cheapest way to get there" plus "pain of that final hop" is a candidate. Take the smallest.

What is new compared with [Min Cost Climbing Stairs](../03-minimum-cost-climbing-stairs/):

1. The price of a hop depends on **where it starts and where it ends** (`|h[i] − h[i−j]|`), not on a single step.
2. In Frog 2 there are `k` possible previous stones, not two.

## 2. Problem statement in our own words

**Frog 1.** There are `N` stones numbered `0..N-1` with heights `h[0..N-1]`. The frog starts on stone 0. From stone `i` it can hop to `i+1` or `i+2`; a hop from `a` to `b` costs `|h[a] − h[b]|`. Find the minimum total cost to reach stone `N-1`.

**Frog 2.** Same, but from stone `i` it can hop to any of `i+1, i+2, …, i+K`.

Originals: [AtCoder dp_a · Frog 1](https://atcoder.jp/contests/dp/tasks/dp_a), [AtCoder dp_b · Frog 2](https://atcoder.jp/contests/dp/tasks/dp_b). (AtCoder numbers stones from 1; we use 0-based indices, as Java arrays do.) Limits: `N` up to 100,000, heights up to 10,000, and in Frog 2 `K` up to 100.

### The input format

AtCoder does not call a method of yours. It runs your program and gives it plain text on **standard input**; you print the answer on standard output.

```
Frog 1 input            Frog 2 input
4                       5 3
10 30 40 20             10 30 40 50 20
│  └─ N heights         │ │ └─ N heights
└─ N                    │ └─ K
                        └─ N
```

## 3. Examples

| Problem | Input | Output | Cheapest route |
|---------|-------|--------|----------------|
| Frog 1 | `h = [10, 30, 40, 20]` | 30 | 0 → 1 → 3: `|10−30| + |30−20| = 20 + 10` |
| Frog 1 | `h = [10, 10]` | 0 | 0 → 1, equal heights |
| Frog 1 | `h = [30, 10, 60, 10, 60, 50]` | 40 | 0 → 2 → 4 → 5: `30 + 0 + 10` |
| Frog 2 | `K = 3, h = [10, 30, 40, 50, 20]` | 30 | 0 → 1 → 4: `20 + 10` |
| Frog 2 | `K = 1, h = [10, 20, 10]` | 20 | forced: `10 + 10` |

**Running examples:** Frog 1 with `h = [10, 30, 40, 20]` (answer 30), then Frog 2 with `K = 3, h = [10, 30, 40, 50, 20]` (answer 30).

## 4. Deriving the solution — the 7 questions

### Q1. What is changing?

The stone `i` the frog is on.

### Q2. What does the state represent?

> `minCost[i]` = the minimum total cost for the frog to land on stone `i`, starting from stone 0.

### Q3. What are the choices?

The previous stone. In Frog 1 it is `i-1` or `i-2`. In Frog 2 it is `i-j` for any `j = 1..K` with `i-j ≥ 0`.

### Q4. What is the recurrence?

Frog 1:
```
minCost[i] = min( minCost[i-1] + |h[i] - h[i-1]|,
                  minCost[i-2] + |h[i] - h[i-2]| )      (second option only if i ≥ 2)
```

Frog 2:
```
minCost[i] = min over j = 1..K, i-j ≥ 0 of  ( minCost[i-j] + |h[i] - h[i-j]| )
```

Each term reads: "best way to reach the previous stone" + "price of the final hop from it". The transition cost now uses `h[i]` **and** `h[i-j]`, which is fine: both heights are known input; only `minCost` values come from the DP.

### Q5. What are the base cases?

```
minCost[0] = 0
```

The frog starts on stone 0, so it has paid nothing. Only **one** base case is needed this time (compare with two in Min Cost Climbing Stairs). Why? Stone 1 can be computed by the general rule: its only option is `j = 1`, giving `minCost[0] + |h[1] − h[0]|`. The guard `i − j ≥ 0` takes care of the missing options. In general: *you need base cases only for states the recurrence cannot compute*, and guarding the choice loop often shrinks that set to just the start.

### Q6. What is the order of computation?

All dependencies are to smaller indices → loop `i = 1..N-1` from left to right; inside, loop over `j`.

### Q7. Can the state space be optimized?

- Frog 1 reads the last 2 cells → two variables, `O(1)` extra space.
- Frog 2 reads the last `K` cells → keep only `K + 1` values in a ring buffer, `O(K)` extra space.

## 5. Brute force: recursion

```java
private static long minCost(int[] h, int i, int k) {
    if (i == 0) return 0;
    long best = Long.MAX_VALUE;
    for (int j = 1; j <= k && i - j >= 0; j++) {
        best = Math.min(best, minCost(h, i - j, k) + Math.abs(h[i] - h[i - j]));
    }
    return best;
}
// Frog 1 = minCost(h, n-1, 2);  Frog 2 = minCost(h, n-1, K)
```
Full file: [`solution/BruteForce.java`](solution/BruteForce.java). One function covers both problems: Frog 1 is Frog 2 with `K = 2`.

Starting `best` at `Long.MAX_VALUE` means "no option seen yet"; any real option is smaller and replaces it. Since `i ≥ 1` always has the option `j = 1`, the result is never left at `MAX_VALUE`.

### Recursion tree, Frog 1, `h = [10, 30, 40, 20]`

```
                         mc(3) [30]
             +|20-40|=20 ┌───┴───┐ +|20-30|=10
                     mc(2) [30]  mc(1) [20]
          +|40-30|=10 ┌──┴──┐ +|40-10|=30    │ +20
                 mc(1) [20]  mc(0) [0]     mc(0) [0]
                  │ +20
                 mc(0) [0]

mc(3) = min(30 + 20, 20 + 10) = 30
```

`mc(1)` is computed twice already with four stones. With `K` choices, the tree branches up to `K` ways per level, so the brute force is roughly `O(K^N)`. It is only usable for tiny inputs.

## 6. Memoization (top-down)

Add a memo array of size `N` (one cell per stone):

```java
private static long[] memo;       // memo[i] = cheapest cost to land on stone i; -1 = unknown

private static long minCost(int[] h, int i, int k) {
    if (i == 0) return 0;
    if (memo[i] != -1) return memo[i];
    long best = Long.MAX_VALUE;
    for (int j = 1; j <= k && i - j >= 0; j++) {
        best = Math.min(best, minCost(h, i - j, k) + Math.abs(h[i] - h[i - j]));
    }
    return memo[i] = best;
}
```
Full file: [`solution/Memoization.java`](solution/Memoization.java).

### Dry run, Frog 1, `h = [10, 30, 40, 20]`

| Order finished | State | Options | Stored |
|----------------|-------|---------|--------|
| 1 | `mc(1)` | from 0: `0 + 20` | `memo[1] = 20` |
| 2 | `mc(2)` | from 1: `20 + 10 = 30`; from 0: `0 + 30 = 30` | `memo[2] = 30` |
| 3 | `mc(3)` | from 2: `30 + 20 = 50`; from 1: **hit** `20 + 10 = 30` | `memo[3] = 30` |

A practical warning: on AtCoder `N` can be 100,000, which means a recursion 100,000 calls deep. Java's default thread stack often overflows at that depth. For judges, prefer the tabulation below (or run the recursion in a `Thread` with a bigger stack). A depth of a few thousand, like the 2,000-stone test in the memoized file, is fine with the default stack.

## 7. Tabulation (bottom-up)

Frog 1:
```java
long[] minCost = new long[n];     // minCost[i] = cheapest cost to land on stone i
minCost[0] = 0;
for (int i = 1; i < n; i++) {
    long fromOne = minCost[i - 1] + Math.abs(h[i] - h[i - 1]);
    long fromTwo = (i >= 2) ? minCost[i - 2] + Math.abs(h[i] - h[i - 2]) : Long.MAX_VALUE;
    minCost[i] = Math.min(fromOne, fromTwo);
}
return minCost[n - 1];
```

Frog 2 (inner loop over the hop length):
```java
for (int i = 1; i < n; i++) {
    minCost[i] = Long.MAX_VALUE;
    for (int j = 1; j <= k && i - j >= 0; j++) {
        minCost[i] = Math.min(minCost[i], minCost[i - j] + Math.abs(h[i] - h[i - j]));
    }
}
```
Full file: [`solution/Tabulation.java`](solution/Tabulation.java).

### Frog 1 table, `h = [10, 30, 40, 20]`

```
 stone i      0     1     2     3
 h           10    30    40    20
 minCost      0     .     .     .

 i=1: from 0: 0+|30-10| = 20                          → 20
 minCost      0    20     .     .

 i=2: from 1: 20+|40-30| = 30;  from 0: 0+|40-10| = 30 → 30
 minCost      0    20    30     .

 i=3: from 2: 30+|20-40| = 50;  from 1: 20+|20-30| = 30 → 30
 minCost      0    20    30    30     ← answer
```

### Frog 2 table, `K = 3, h = [10, 30, 40, 50, 20]`

Each row lists the candidates `minCost[i-j] + |h[i] − h[i-j]|` for `j = 1, 2, 3`:

| i | h[i] | j = 1 | j = 2 | j = 3 | minCost[i] |
|---|------|-------|-------|-------|------------|
| 0 | 10 | — | — | — | **0** (base) |
| 1 | 30 | 0 + 20 = 20 | — | — | **20** |
| 2 | 40 | 20 + 10 = 30 | 0 + 30 = 30 | — | **30** |
| 3 | 50 | 30 + 10 = 40 | 20 + 20 = 40 | 0 + 40 = 40 | **40** |
| 4 | 20 | 40 + 30 = 70 | 30 + 20 = 50 | 20 + 10 = 30 | **30** |

Answer `minCost[4] = 30`: hop 0 → 1 (cost 20), then a 3-hop 1 → 4 (cost 10).

## 8. Reading standard input in Java

The solution files keep the logic in methods (`frog1(int[] h)`, `frog2(int[] h, int k)`) so `main` can test them. For a judge, you only add a thin input layer. `Tabulation.java` contains `frog1FromText` / `frog2FromText`, which parse the judge's text format from a `String` and are tested with the sample input. To submit, you read from `System.in` instead.

**Simple version (fine for this problem):**
```java
import java.util.Scanner;

public class Main {                       // AtCoder requires the class to be named Main
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int n = in.nextInt();
        int[] h = new int[n];
        for (int i = 0; i < n; i++) h[i] = in.nextInt();
        System.out.println(frog1(h));
    }
    static long frog1(int[] h) { /* paste the tabulation method here */ return 0; }
}
```

**Faster version (use it when the input has hundreds of thousands of numbers):** `Scanner` parses with regular expressions and is slow. `BufferedReader` reads whole lines, and `StringTokenizer` splits them on spaces:
```java
import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        int n = Integer.parseInt(st.nextToken());
        int k = Integer.parseInt(st.nextToken());          // Frog 2 only
        int[] h = new int[n];
        st = new StringTokenizer(br.readLine());
        for (int i = 0; i < n; i++) h[i] = Integer.parseInt(st.nextToken());
        System.out.println(frog2(h, k));
    }
    static long frog2(int[] h, int k) { /* paste the tabulation method here */ return 0; }
}
```

Keep this separation in your own practice: **parsing in `main`, logic in a method.** You can then test the method directly, and switching between LeetCode (method signature) and AtCoder/CSES (stdin) becomes copy-paste.

## 9. Space optimization

**Frog 1:** `minCost[i]` reads `minCost[i-1]` and `minCost[i-2]` only → two variables (the heights array is input and stays).

```java
long twoBack = 0, oneBack = 0;          // minCost(i-2), minCost(i-1)
for (int i = 1; i < h.length; i++) {
    long fromOne = oneBack + Math.abs(h[i] - h[i - 1]);
    long fromTwo = (i >= 2) ? twoBack + Math.abs(h[i] - h[i - 2]) : Long.MAX_VALUE;
    long current = Math.min(fromOne, fromTwo);
    twoBack = oneBack;
    oneBack = current;
}
return oneBack;
```

**Frog 2:** `minCost[i]` reads the last `K` values. Keep a ring buffer of `K + 1` slots; stone `i` lives in slot `i % (K+1)`:

```
K = 3, ring of 4 slots
stone:   0  1  2  3 | 4  5  6  7 | 8 …
slot:    0  1  2  3 | 0  1  2  3 | 0 …
                      ▲
   computing stone 4 reads slots of stones 3, 2, 1 and then writes slot 0,
   which held stone 0 — 4 positions back, out of hopping range forever.
```

Full file: [`solution/Optimized.java`](solution/Optimized.java). With `K ≤ 100` this saves memory, but the time is still `O(N·K)`.

## 10. Dry run on a full example

Frog 1, `h = [10, 30, 40, 20]`, two-variable version:

| i | fromOne | fromTwo | current | twoBack | oneBack |
|---|---------|---------|---------|---------|---------|
| start | — | — | — | 0 | 0 |
| 1 | 0 + 20 = 20 | (none) | 20 | 0 | 20 |
| 2 | 20 + 10 = 30 | 0 + 30 = 30 | 30 | 20 | 30 |
| 3 | 30 + 20 = 50 | 20 + 10 = 30 | 30 | 30 | 30 |

Answer **30**, matching the table.

## 11. Complexity analysis

| Approach | Time | Space | Why |
|----------|------|-------|-----|
| Brute force | `O(2^N)` (Frog 1), up to `O(K^N)` (Frog 2) | `O(N)` | Each call branches 2 (or `K`) ways with no reuse; stack depth `N`. |
| Memoization | `O(N·K)` | `O(N)` | `N` states, each loops over up to `K` choices once. |
| Tabulation | `O(N·K)` | `O(N)` | Outer loop `N`, inner loop `K`. |
| Optimized | `O(N·K)` | `O(1)` extra (Frog 1), `O(K)` extra (Frog 2) | Same loops; only the live window is stored. |

Explaining `O(N·K)`: the general DP cost rule is **(number of states) × (work per state)**. There are `N` stones and each one examines up to `K` previous stones. With `N = 10^5` and `K = 100` that is `10^7` simple operations, well within a second. Frog 1 is the special case `K = 2`, i.e. `O(N)`.

Overflow: the worst total is about `N × 10^4 = 10^9`, which barely fits in `int`. We use `long` so there is no doubt.

## 12. Why this approach works

Take a cheapest hop sequence to stone `i`. Its last hop came from some stone `i − j` in range. The part before that hop is a hop sequence to `i − j`, and it must be a cheapest one; otherwise replacing it would make the whole route cheaper. So the best route to `i` is "best route to some `i − j`" plus `|h[i] − h[i−j]|`, minimized over the allowed `j`. That is the recurrence, and it is evaluated only after all stones `< i` are final.

## 13. Common mistakes

1. **Forgetting `i − j ≥ 0`** in Frog 2 → `ArrayIndexOutOfBoundsException` near the start.
2. **Initializing `best = 0`** instead of `Long.MAX_VALUE` → `min` always returns 0.
3. **Using `h[i] − h[i−j]` without `Math.abs`** → negative costs, wrong answers when the frog goes downhill.
4. **Deep recursion on the judge** → `StackOverflowError` for `N = 10^5`. Use tabulation.
5. **Class not named `Main`** on AtCoder → compile error on the judge.
6. **Using `Scanner` for huge inputs** → time limit exceeded in some problems; switch to `BufferedReader`.

## 14. When to use this technique / how to recognize similar problems

Signals:
- positions in a line, start at one end, finish at the other;
- from each position, a **range** of moves (fixed `K`, or given per position);
- each move has a price that may depend on both endpoints.

Template: `best[i] = min over allowed previous positions p of (best[p] + price(p → i))`. If the allowed range is large (say `K` up to `10^5`), the inner loop is too slow; that is when you reach for a monotonic queue or a segment tree (module 05).

## 15. Summary and revision notes

- State: `minCost[i]` = cheapest cost to land on stone `i`; base `minCost[0] = 0`.
- Frog 1: 2 choices. Frog 2: `K` choices → inner loop, `O(N·K)`.
- Transition cost `|h[i] − h[i−j]|` uses input on both ends; that is allowed.
- Guard the choice loop with `i − j ≥ 0`; then one base case is enough.
- Judges: parse stdin in `main`, keep the solution in a method; class `Main` on AtCoder.

```
┌──────────────────────────────────────────────────────────────────┐
│  minCost[0] = 0                                                  │
│  minCost[i] = min_{1 ≤ j ≤ K, j ≤ i} ( minCost[i-j]              │
│                                       + |h[i] - h[i-j]| )         │
│  Frog 1: K = 2      answer: minCost[N-1]                         │
└──────────────────────────────────────────────────────────────────┘
```

## Exercises

1. Fill the Frog 1 table for `h = [30, 10, 60, 10, 60, 50]` and reconstruct the route.

<details>
<summary>Answer</summary>

`minCost = 0, 20, 30, 20, 30, 40`.
- `i=1`: `0+20 = 20`
- `i=2`: `min(20+50, 0+30) = 30`
- `i=3`: `min(30+50, 20+0) = 20`
- `i=4`: `min(20+50, 30+0) = 30`
- `i=5`: `min(30+10, 20+40) = 40`

Back-tracking: 40 came from stone 4 (30 + 10), 30 at stone 4 came from stone 2 (30 + 0), 30 at stone 2 came from stone 0. Route: **0 → 2 → 4 → 5**, cost 30 + 0 + 10 = 40.
</details>

2. In Frog 2 with `K ≥ N − 1`, the frog could hop from stone 0 straight to the last stone. Does that make the answer always `|h[N-1] − h[0]|`?

<details>
<summary>Answer</summary>

Yes. The direct hop costs `|h[N-1] − h[0]|`. By the triangle inequality `|a − c| ≤ |a − b| + |b − c|`, replacing one hop by two hops through any middle stone can never be cheaper, so no multi-hop route beats the direct one. The answer **is** `|h[N-1] − h[0]|`. The DP finds this automatically; it's a good sanity check for your code. (Try `K = 100`, `h = [10, 30, 40, 50, 20]`: answer `|20 − 10| = 10`.)
</details>

3. Variation: the cost of a hop of length `j` from `p` to `i` is `|h[i] − h[p]| + j` (longer hops tire the frog). Which of the 7 answers change?

<details>
<summary>Answer</summary>

Only Q4: `minCost[i] = min_j (minCost[i−j] + |h[i] − h[i−j]| + j)`. The state, choices, base case, order and space optimization are identical. This is typical: most variations only change the transition cost.
</details>
