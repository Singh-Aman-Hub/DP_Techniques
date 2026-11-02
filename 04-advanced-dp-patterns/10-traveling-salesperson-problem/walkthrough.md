# Traveling Salesperson Problem — Walkthrough

The Traveling Salesperson Problem (TSP) is the most famous bitmask DP. It needs one idea beyond [Bitmask DP](../09-bitmask-dp/): the state is no longer just "which items are used", but **"which cities are visited, and where am I now?"**, written `dp[mask][last]`. The algorithm is called **Held–Karp** after the two researchers who published it in 1962.

We derive it from scratch, dry-run it on 4 cities, rebuild the actual tour from the table, and then meet two LeetCode problems that are TSP in disguise: [847 — Shortest Path Visiting All Nodes](https://leetcode.com/problems/shortest-path-visiting-all-nodes/) and [943 — Find the Shortest Superstring](https://leetcode.com/problems/find-the-shortest-superstring/).

---

## 1. Intuition

A delivery driver must visit every shop in town once and return to the depot, driving as little as possible. Halfway through the day she stops for coffee and asks, "what is the cheapest way to finish from here?". To answer it she needs only two facts:

1. **Which shops are already done.** She must not visit them again, and must still visit the others.
2. **Which shop she is standing at now.** The next drive starts here.

The order in which she visited the finished shops is **irrelevant** to the rest of the day. "Depot → A → B → C" and "depot → B → A → C" both leave her at C with {A, B, C} done, so from that point on they face the identical remaining problem. Merging such situations is exactly what `dp[mask][last]` does.

---

## 2. Problem statement in our own words

There are `n` cities and a table `dist[i][j]`, the cost of travelling directly from city `i` to city `j` (it may differ from `dist[j][i]`). Find the cheapest **tour**: a route that starts at a city, visits every other city exactly once, and returns to the start.

A tour is a cycle, so the start does not matter. We always start at **city 0**, which saves a factor of n.

There is no single LeetCode problem with exactly this statement. The two LeetCode problems at the end of the lesson are its closest relatives.

---

## 3. Examples

**Running example** (symmetric, 4 cities):

```
          to:  0    1    2    3
from 0:      [ 0,  10,  15,  20 ]
from 1:      [10,   0,  35,  25 ]
from 2:      [15,  35,   0,  30 ]
from 3:      [20,  25,  30,   0 ]

            10
     0 ------------ 1
     | \          / |
     |   \ 20  35/  |
  15 |     \   /    | 25
     |       X      |
     |     /   \    |
     |   /       \  |
     2 ------------ 3
            30
```

(The diagonals are 0–3 = 20 and 1–2 = 35.)

Every tour starting at 0 (there are 3! = 6 orders, but each cycle appears twice, once in each direction):

| Tour | Cost |
|---|---|
| 0 → 1 → 2 → 3 → 0 | 10 + 35 + 30 + 20 = 95 |
| 0 → 1 → 3 → 2 → 0 | 10 + 25 + 30 + 15 = **80** |
| 0 → 2 → 1 → 3 → 0 | 15 + 35 + 25 + 20 = 95 |
| 0 → 2 → 3 → 1 → 0 | 15 + 30 + 25 + 10 = **80** |
| 0 → 3 → 1 → 2 → 0 | 20 + 25 + 35 + 15 = 95 |
| 0 → 3 → 2 → 1 → 0 | 20 + 30 + 35 + 10 = 95 |

Answer: **80**.

**Asymmetric example:** `dist = [[0,1,15],[6,0,7],[9,6,0]]`. Tour 0 → 1 → 2 → 0 costs 1 + 7 + 9 = **17**, but the reverse 0 → 2 → 1 → 0 costs 15 + 6 + 6 = 27. Direction matters.

**Edge cases:** 1 city → 0 (nothing to travel). 2 cities `[[0,5],[5,0]]` → 10 (there and back).

---

## 4. Deriving the solution — the 7 questions

### Q1. What is changing?
As we build the route city by city, two things change: the **set of visited cities** and the **current city**.

### Q2. What does the state represent?
> `dp[mask][last]` = the minimum cost of a path that **starts at city 0**, visits **exactly** the cities in `mask` (each once), and **ends at city `last`**.

`mask` always contains city 0 (we started there) and `last`.

Why is `mask` alone (as in the previous topic) not enough? In the assignment problem, the cost of the next step depended only on *which index* came next, and that was `bitCount(mask)`. Here the cost of the next step is `dist[last][next]`, which depends on **where we are**. Two paths with the same visited set but different end cities have different futures, so `last` must be part of the state.

### Q3. What are the choices?
From state `(mask, last)`, travel to any city `next` **not** in `mask`.

### Q4. What is the recurrence?
Push form (tabulation):
```
dp[mask | (1 << next)][next] = min( itself,  dp[mask][last] + dist[last][next] )
```
Pull form, the same thing read backwards:
```
dp[mask][last] = min over prev in mask, prev != last, of  dp[mask without last][prev] + dist[prev][last]
```
And the answer closes the cycle:
```
answer = min over last != 0 of  dp[full][last] + dist[last][0]
```

### Q5. What are the base cases?
`dp[{0}][0] = dp[1][0] = 0`: we stand at city 0, have visited only city 0, and have paid nothing. Every other cell starts at infinity ("not reachable yet").

### Q6. What is the order of computation?
Each transition adds a city to the mask, which makes the number bigger. So loop `mask` from small to large. Inside a mask, the order of `last` does not matter, because transitions never stay inside the same mask.

### Q7. Can the state space be optimized?
Cells with a mask that does not contain city 0 are never used, so half the table is wasted. A careful implementation can drop bit 0 and store only `2ⁿ⁻¹ · n` cells. It halves memory without changing the big-O, so we keep the simple version for clarity. Rolling arrays do not work: a mask is extended into many larger masks, not just "the next row".

---

## 5. Brute force: recursion

Fix city 0, then try every order of the remaining cities:

```java
private static int extend(int[][] dist, boolean[] visited, int current, int visitedCount) {
    if (visitedCount == n) return dist[current][0];          // go home
    int best = Integer.MAX_VALUE;
    for (int next = 0; next < n; next++) {
        if (visited[next]) continue;
        visited[next] = true;
        best = Math.min(best, dist[current][next] + extend(dist, visited, next, visitedCount + 1));
        visited[next] = false;
    }
    return best;
}
```

Recursion tree on the running example (state written as `{visited} @ current`):

```
                                 {0}@0
              /                    |                    \
         {0,1}@1               {0,2}@2               {0,3}@3
         /      \              /      \              /      \
  {0,1,2}@2  {0,1,3}@3   {0,1,2}@1  {0,2,3}@3   {0,1,3}@1  {0,2,3}@2
      |          |           |          |           |          |
 {all}@3     {all}@2     {all}@3    {all}@1     {all}@2    {all}@1
  ^ same as ->                ^                              ^
```

`{all}@3` is reached from both `{0,1,2}@2` and `{0,1,2}@1`. `{all}@2` and `{all}@1` also appear twice. With larger n, a state like `{0,…,9}@4` is reached by every one of the 8! orders of cities 1–9 minus 4, all solving the same remaining problem. The tree has (n−1)! leaves: 19! ≈ 1.2 × 10¹⁷ for 20 cities.

---

## 6. Memoization (top-down)

Cache the "remaining cost" from each state:

> `finish(mask, last)` = the cheapest way to visit every city **not** in `mask`, starting at `last`, and then return to 0.

```java
private static int finish(int mask, int last) {
    if (mask == full) return dist[last][0];               // all visited: go home
    if (memo[mask][last] != -1) return memo[mask][last];
    int best = Integer.MAX_VALUE;
    for (int next = 0; next < n; next++) {
        if ((mask & (1 << next)) != 0) continue;
        best = Math.min(best, dist[last][next] + finish(mask | (1 << next), next));
    }
    return memo[mask][last] = best;
}
// answer = finish(1, 0)
```

Dry run (bits `b3 b2 b1 b0`). States with three cities visited have one move left before going home:

| State | Computation | finish |
|---|---|---|
| (0111, 2) | → 3: 30 + dist[3][0]=20 | 50 |
| (0111, 1) | → 3: 25 + 20 | 45 |
| (1011, 3) | → 2: 30 + dist[2][0]=15 | 45 |
| (1011, 1) | → 2: 35 + 15 | 50 |
| (1101, 3) | → 1: 25 + dist[1][0]=10 | 35 |
| (1101, 2) | → 1: 35 + 10 | 45 |
| (0011, 1) | → 2: 35 + finish(0111,2)=50 → 85; → 3: 25 + finish(1011,3)=45 → **70** | 70 |
| (0101, 2) | → 1: 35 + finish(0111,1)=45 → 80; → 3: 30 + finish(1101,3)=35 → **65** | 65 |
| (1001, 3) | → 1: 25 + finish(1011,1)=50 → **75**; → 2: 30 + finish(1101,2)=45 → **75** | 75 |
| (0001, 0) | → 1: 10 + 70 = **80**; → 2: 15 + 65 = **80**; → 3: 20 + 75 = 95 | **80** |

With only 4 cities the memo barely helps: a state with 3 visited cities has just one possible previous city, so each is requested once, and the repeated `{all}@last` states from the recursion tree are answered directly by the base case. From n = 5 on, a state such as `({0,1,2,3}, 3)` can be reached from `({0,1,2}, 1)` **and** from `({0,1,2}, 2)`, and the second request is a cache hit. The number of such repeats grows explosively: with n = 15 the memo turns about 87 billion orders into about 491,520 states.

---

## 7. Tabulation (bottom-up)

```java
dp[1][0] = 0;                                          // everything else = INF
for (int mask = 1; mask <= full; mask++) {
    if ((mask & 1) == 0) continue;                     // must contain the start city
    for (int last = 0; last < n; last++) {
        if (dp[mask][last] >= INF) continue;
        for (int next = 0; next < n; next++) {
            if ((mask & (1 << next)) != 0) continue;
            int nextMask = mask | (1 << next);
            int candidate = dp[mask][last] + dist[last][next];
            if (candidate < dp[nextMask][next]) {
                dp[nextMask][next] = candidate;
                parent[nextMask][next] = last;         // remember how we got here
            }
        }
    }
}
answer = min over last of dp[full][last] + dist[last][0];
```

Use `INF = Integer.MAX_VALUE / 2` so that `INF + dist` cannot overflow into a negative number.

### Dry run on 4 cities — the full table

Rows are masks containing city 0 (bits `b3 b2 b1 b0`); columns are `last`. A dash means unreachable. The note says which earlier cell produced the minimum.

| mask | visited | last=0 | last=1 | last=2 | last=3 |
|---|---|---|---|---|---|
| 0001 | {0} | **0** (base) | – | – | – |
| 0011 | {0,1} | – | 0+10 = **10** | – | – |
| 0101 | {0,2} | – | – | 0+15 = **15** | – |
| 1001 | {0,3} | – | – | – | 0+20 = **20** |
| 0111 | {0,1,2} | – | via 2: 15+35 = **50** | via 1: 10+35 = **45** | – |
| 1011 | {0,1,3} | – | via 3: 20+25 = **45** | – | via 1: 10+25 = **35** |
| 1101 | {0,2,3} | – | – | via 3: 20+30 = **50** | via 2: 15+30 = **45** |
| 1111 | {0,1,2,3} | – | min(via 2: 50+35, via 3: 45+25) = **70** | min(via 1: 45+35, via 3: 35+30) = **65** | min(via 1: 50+25, via 2: 45+30) = **75** |

Close the cycle:

```
last = 1:  dp[1111][1] + dist[1][0] = 70 + 10 = 80
last = 2:  dp[1111][2] + dist[2][0] = 65 + 15 = 80
last = 3:  dp[1111][3] + dist[3][0] = 75 + 20 = 95
answer = 80
```

Compare with the memoization table: `finish(0011, 1) = 70`, and here `dp[1111][1] = 70`. They are different quantities that happen to be equal here. `finish` measures the **remaining** cost and `dp` the cost **so far**. Both approaches arrive at 80.

---

## 8. Path reconstruction

The table gives the cost, but usually we also want the **route**. During the fill, every time a cell improves, we stored `parent[nextMask][next] = last`: "the best way to reach `next` with this visited set came from `last`". To rebuild the route, start from the best final state and follow the parents backwards, removing each city from the mask as we leave it:

```
best final state: (1111, last = 1)          [first of the two 80s]
  parent[1111][1] = 3   -> previous state (1111 minus city 1 = 1101, 3)
  parent[1101][3] = 2   -> previous state (1101 minus city 3 = 0101, 2)
  parent[0101][2] = 0   -> previous state (0101 minus city 2 = 0001, 0)
  parent[0001][0] = -1  -> stop: this is the start
```

Collected backwards: `1, 3, 2, 0`. Add the return home and reverse it: **0 → 2 → 3 → 1 → 0**. Check: 15 + 30 + 25 + 10 = 80 ✓. (It is the reverse of 0 → 1 → 3 → 2 → 0, which is the same cycle.)

```java
List<Integer> tour = new ArrayList<>();
tour.add(0);                                 // the final return home
int mask = full, city = bestLast;
while (city != -1) {
    tour.add(city);
    int previous = parent[mask][city];
    mask ^= (1 << city);                     // XOR removes city from the set
    city = previous;
}
Collections.reverse(tour);
```

The removal uses XOR. Bit `city` is certainly 1 at this point, and XOR with a 1 turns it into 0.

---

## 9. Dry run — what the table looks like as a picture

The states form layers by the number of visited cities. Arrows only go from one layer to the next, so the state graph is a DAG (see [DP on Graphs](../08-dp-on-graphs/)) and increasing mask order is a topological order of it. Here are the reachable states of the running example with their `dp` values, layer by layer:

```
layer 1          layer 2            layer 3                 layer 4              close the cycle
(1 city)         (2 cities)         (3 cities)              (all cities)

{0}@0 = 0  --+-> {0,1}@1 = 10 --+-> {0,1,2}@2 = 45
             |                  +-> {0,1,3}@3 = 35
             |
             +-> {0,2}@2 = 15 --+-> {0,1,2}@1 = 50          {all}@1 = 70   --> 70 + 10 = 80
             |                  +-> {0,2,3}@3 = 45          {all}@2 = 65   --> 65 + 15 = 80
             |                                              {all}@3 = 75   --> 75 + 20 = 95
             +-> {0,3}@3 = 20 --+-> {0,1,3}@1 = 45
                                +-> {0,2,3}@2 = 50
```

Every layer-3 state feeds two layer-4 states (one for each city it has not visited). For example, `{0,1,3}@3 = 35` feeds `{all}@2` with 35 + 30 = 65, which is the winning value for that cell.

## 10. Complexity analysis

| Approach | Time | Space | Why |
|---|---|---|---|
| Brute force | O(n!) | O(n) | (n−1)! orders after fixing city 0 |
| Memoization | O(2ⁿ · n²) | O(2ⁿ · n) | 2ⁿ · n states; each tries up to n next cities |
| Tabulation | O(2ⁿ · n²) | O(2ⁿ · n) | same states and transitions; plus a parent table of the same size |

How far does 2ⁿ · n² reach?

| n | 2ⁿ · n² | n! (brute force) |
|---|---|---|
| 10 | ~10⁵ | 3.6 × 10⁶ |
| 15 | ~7.4 × 10⁶ | 1.3 × 10¹² |
| 18 | ~8.5 × 10⁷ | 6.4 × 10¹⁵ |
| 20 | ~4.2 × 10⁸ | 2.4 × 10¹⁸ |

So Held–Karp handles **up to about 16–20 cities**. Memory is the other limit: for n = 20, `2²⁰ × 20` ints is about 80 MB. TSP is NP-hard, which means no known algorithm is polynomial. Held–Karp is still exponential, just far less so than n!.

---

## 11. Why this approach works

- **Optimal substructure.** Take the best path that visits set S and ends at `last`. Remove its final edge `prev → last`. What remains must be the best path that visits `S − {last}` and ends at `prev`. If a cheaper one existed, we could swap it in and get a cheaper path to `(S, last)`, a contradiction.
- **The state holds everything the future needs.** Future costs depend only on where we are (`last`) and which cities are still allowed (the complement of `mask`). The order of earlier visits affects the cost so far, which `dp` already records as a single minimum.
- **Every tour is counted.** Any tour is a sequence of single-city extensions from `{0}@0` to some `{all}@last`, followed by the return edge. The DP takes the minimum over all such sequences.

---

## 12. Relative 1 — LeetCode 847: Shortest Path Visiting All Nodes

[LeetCode 847](https://leetcode.com/problems/shortest-path-visiting-all-nodes/): an undirected, connected graph with `n ≤ 12` nodes. Find the length of the shortest walk that visits every node. You may start and end anywhere, and you may **revisit** nodes and reuse edges.

**Same state, different engine.** The state is again `(mask of visited nodes, current node)`. But two things differ from Held–Karp:

1. **Revisiting is allowed.** Moving to an already visited node leaves the mask unchanged. So transitions can stay inside one mask, and "increasing mask order" is no longer a valid computation order.
2. **Every edge costs 1.** When all edges cost the same, **BFS** finds the fewest moves to every state, in order of distance. BFS order is the computation order.

```java
for (int start = 0; start < n; start++) {               // any node can be the start
    seen[1 << start][start] = true;
    queue.add(new int[]{1 << start, start, 0});
}
while (!queue.isEmpty()) {
    int[] s = queue.poll();
    if (s[0] == full) return s[2];                     // first time all nodes are visited
    for (int next : graph[s[1]]) {
        int nextMask = s[0] | (1 << next);
        if (!seen[nextMask][next]) { seen[nextMask][next] = true; queue.add(new int[]{nextMask, next, s[2] + 1}); }
    }
}
```

Example `[[1,2,3],[0],[0],[0]]` (a star with centre 0):

```
      1
      |
  2 - 0 - 3
```

The best walk is `1 → 0 → 2 → 0 → 3`: **4** moves. Node 0 is visited twice. The masks along the walk (bits `b3 b2 b1 b0`) are `0010 → 0011 → 0111 → 0111 → 1111`. The step back to 0 leaves the mask at `0111`: a transition that stays inside the same mask, which is exactly what Held–Karp's increasing-mask loop cannot handle.

Complexity: 2ⁿ · n states, each scanning up to n neighbours: O(2ⁿ · n²). For n = 12 that is about 590,000 steps.

---

## 13. Relative 2 — LeetCode 943: Find the Shortest Superstring

[LeetCode 943](https://leetcode.com/problems/find-the-shortest-superstring/): given up to 12 words (none is a substring of another), return the shortest string that contains every word as a substring.

### Why it is TSP
Suppose the words appear in the answer in some order. Placing word `j` right after word `i` can **share** characters: if the end of `i` equals the start of `j`, those characters are written only once.

```
overlap[i][j] = the longest suffix of words[i] that is also a prefix of words[j]

"gcta" then "ctaagt":   gcta
                         ctaagt     -> overlap 3 ("cta"), combined "gctaagt"
```

The total length is `(sum of word lengths) − (sum of overlaps between consecutive words)`. Minimising the length = **maximising the total overlap along an order of all words**. Words are cities, `overlap[i][j]` is the "distance" (to maximise), and there is no return trip. That is Held–Karp with three changes:

| Held–Karp | Shortest superstring |
|---|---|
| `min` | `max` |
| start fixed at city 0 | any word may be first: `dp[1 << i][i] = 0` for every i |
| close the cycle with `dist[last][0]` | no return: answer is `max over last of dp[full][last]` |

Overlap table for `["catg","ctaagt","gcta","ttca","atgcatc"]` (row = first word, column = second):

```
            catg ctaagt gcta ttca atgcatc
catg          -     0     1    0     3
ctaagt        0     -     0    1     0
gcta          0     3     -    0     1
ttca          2     0     0    -     1
atgcatc       1     1     0    0     -
```

The best order is `gcta → ctaagt → ttca → catg → atgcatc` with overlaps 3 + 1 + 2 + 3 = 9. Total length 25 − 9 = **16**: `"gctaagttcatgcatc"`. The string itself is rebuilt with the same parent-pointer walk as in Section 8, then the words are glued while skipping each overlap.

The solution file checks only the length and that every word appears, since LeetCode accepts any shortest answer.

---

## 14. Common mistakes

1. **Forgetting `last` in the state.** `dp[mask]` alone cannot know the cost of the next edge.
2. **Not fixing the start.** Letting every city be the start for a tour multiplies work by n and gives the same answer.
3. **Forgetting the return edge** (TSP) or **adding one** (943, 847, "path" variants). Read whether the route must come back.
4. **`INF + dist` overflow.** Use `Integer.MAX_VALUE / 2` or skip unreachable states before adding.
5. **Using increasing-mask DP when revisits are allowed** (847). Transitions that keep the mask equal break that order; use BFS (unit costs) or Dijkstra (weighted).
6. **Rebuilding the path without removing the city from the mask.** Each backward step must move to `(mask ^ (1 << city), parent)`, otherwise the parent lookup reads the wrong row.
7. **Trying Held–Karp on 30+ cities.** 2³⁰ · 30² is about 10¹². Check the constraints.

---

## 15. When to use this technique / how to recognize similar problems

- "Visit **all** nodes / use **all** items, in the best **order**", and the cost of each step depends on the **previous** item. → `dp[mask][last]`.
- n ≤ about 16–20.
- The step cost is a pairwise table, even if it is hidden: word overlaps (943), travel times, setup costs between jobs.
- Revisits allowed + unit edge costs → BFS over `(mask, node)` (847).
- If the next step's cost does **not** depend on the previous item, you probably need only `dp[mask]` (previous topic).

---

## 16. Summary and revision notes

- State: `dp[mask][last]` = cheapest path from 0 that visits exactly `mask` and ends at `last`.
- Base: `dp[1][0] = 0`. Order: increasing mask. Answer: `min dp[full][last] + dist[last][0]`.
- Rebuild the route with a `parent` table, removing each city from the mask while walking back.
- Cost: O(2ⁿ · n²) time, O(2ⁿ · n) memory; practical up to n ≈ 16–20.
- 847: same state, BFS order because revisits keep the mask unchanged.
- 943: same state, `max` of overlaps, any start, no return.

```
+-----------------------------------------------------------------+
|  Held-Karp                                                      |
|    dp[1][0] = 0                                                 |
|    dp[mask | 1<<next][next] = min( ..., dp[mask][last]          |
|                                        + dist[last][next] )     |
|    answer = min over last of dp[(1<<n)-1][last] + dist[last][0] |
+-----------------------------------------------------------------+
```

---

## 17. Exercises

**Exercise 1.** Run Held–Karp on the 3-city asymmetric example `dist = [[0,1,15],[6,0,7],[9,6,0]]`. Fill `dp` for masks `011`, `101`, `111` and give the answer.

<details>
<summary>Answer</summary>

- `dp[011][1] = 0 + 1 = 1`, `dp[101][2] = 0 + 15 = 15`
- `dp[111][2] = dp[011][1] + dist[1][2] = 1 + 7 = 8`
- `dp[111][1] = dp[101][2] + dist[2][1] = 15 + 6 = 21`
- close: last 2 → 8 + dist[2][0] = 8 + 9 = **17**; last 1 → 21 + 6 = 27.

Answer **17**, tour 0 → 1 → 2 → 0.
</details>

**Exercise 2.** How many cells does the `dp` table have for n = 16, and how many of them can ever be reachable?

<details>
<summary>Answer</summary>

`2¹⁶ × 16 = 1,048,576` cells. Reachable cells must have bit 0 set (half the masks) and `last` inside the mask. Apart from `(1, 0)`, `last` is never 0, so each reachable mask has `bitCount(mask) − 1` choices of `last`. The sum over masks containing 0 is `2¹⁵ · 15 / 2 + 1 = 245,761`. So under a quarter of the table is ever used, which is why compact implementations drop bit 0.
</details>

**Exercise 3.** In LeetCode 847, why is `seen[mask][node]` correct to mark when a state is **added** to the queue, not when it is removed?

<details>
<summary>Answer</summary>

In BFS with unit costs, the first time a state is discovered is already with the fewest moves, because states are discovered in non-decreasing order of distance. Marking on discovery prevents the same state from entering the queue many times, which would blow up the queue without improving any answer.
</details>

**Exercise 4.** Compute `overlap["ttca"]["catg"]` and `overlap["catg"]["ttca"]`. Why are they different?

<details>
<summary>Answer</summary>

- `ttca` then `catg`: the suffix `"ca"` of ttca equals the prefix `"ca"` of catg → **2**.
- `catg` then `ttca`: catg ends with `g`, `tg`, `atg`, `catg`; ttca starts with `t`, `tt`, `ttc`, `ttca`. No match → **0**.

Overlap compares the **end** of the first word with the **start** of the second, so it is not symmetric. That is why 943 is an asymmetric TSP.
</details>
