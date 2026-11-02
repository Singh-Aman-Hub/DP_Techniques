# Dynamic Programming — From Zero to Hard Problems (Java)

A self-study course in Dynamic Programming (DP), built as a repository you work through one folder at a time. It is a textbook, a coding lab and a LeetCode preparation guide in one.

> **Start here:** read this page, then open [`ROADMAP.md`](ROADMAP.md) and begin with
> [`00-foundations/01-what-is-dynamic-programming`](00-foundations/01-what-is-dynamic-programming/README.md).

---

## What this repository is

- **71 topic folders, 75 fully worked practice problems, and 133 tracked problems.** Each topic folder covers exactly one concept, with a learning guide (`README.md`), a full lesson (`walkthrough.md`) and runnable Java solutions (`solution/`). Each practice problem is documented step by step.
- **A derivation-first course.** You learn *how to arrive at* a DP solution: what changes, what the state means, what the choices are, the recurrence, the base cases, the computation order, and how to optimize. You do not just memorize code.
- **Verified practice.** Every LeetCode, CSES and AtCoder problem referenced was checked against the live site for number, exact title and difficulty. See [`scripts/verified-problems.tsv`](scripts/verified-problems.tsv).
- **Self-checking code.** Every Java file runs its own test cases and prints `PASS`/`FAIL`.

## Who this is for

You, if you:

- can write loops, arrays, functions and basic recursion in Java;
- know basic data structures (arrays, lists, hash maps, stacks/queues, trees at a basic level);
- have **never** studied DP, or studied it and still freeze in front of an unfamiliar problem.

You do **not** need any DP terminology. Every term is introduced before it is used.

## Prerequisites

| You need | Where to brush up if it's shaky |
|---|---|
| Java basics: classes, static methods, arrays, `long` vs `int` | any Java intro |
| Recursion basics | taught again from scratch in [`00-foundations/02-recursion-fundamentals`](00-foundations/02-recursion-fundamentals/README.md) |
| Big-O notation (rough idea) | refreshed in [`00-foundations/12-time-and-space-complexity-in-dp`](00-foundations/12-time-and-space-complexity-in-dp/README.md) |
| JDK 11+ installed (`java -version`) | the repository is validated on Java 21 |

## Repository map

| Module | What you learn | Topics |
|---|---|---|
| [`00-foundations`](00-foundations/README.md) | Recursion, recursion trees, overlapping subproblems, optimal substructure, memoization, tabulation, states, transitions, base cases, complexity, conversions, space optimization, the 7-question framework | 16 |
| [`01-one-dimensional-dp`](01-one-dimensional-dp/README.md) | Fibonacci, Climbing Stairs, Min Cost Stairs, Frog Jump, Non-adjacent Sum, House Robber I/II, Decode Ways, Kadane vs DP | 9 |
| [`02-two-dimensional-and-grid-dp`](02-two-dimensional-and-grid-dp/README.md) | 2D states, Unique Paths I/II, Min Path Sum, Triangle, Falling Path, Dungeon Game, 3D DP, Cherry Pickup | 9 |
| [`03-subsequence-and-knapsack-dp`](03-subsequence-and-knapsack-dp/README.md) | Subsequences, 0/1 & unbounded knapsack, subset sum, partitions, target sum, coin change, rod cutting, LIS, LCS, LPS, edit distance, distinct subsequences, SCS | 16 |
| [`04-advanced-dp-patterns`](04-advanced-dp-patterns/README.md) | String matching, stocks, interval DP, MCM, burst balloons, palindrome partitioning, trees, graphs/DAGs, bitmask, TSP, digit DP, state machines | 12 |
| [`05-dp-optimization`](05-dp-optimization/README.md) | Space optimization, rolling arrays, prefix/suffix, monotonic queue, divide & conquer, convex hull trick, SOS DP, Knuth, matrix exponentiation | 9 |
| [`06-leetcode-practice`](06-leetcode-practice/README.md) | Extra problems per pattern, each fully derived. Full index in [`PROBLEM_INDEX.md`](06-leetcode-practice/PROBLEM_INDEX.md) | 75 |
| [`07-revision-and-interview-preparation`](07-revision-and-interview-preparation/README.md) | [Pattern recognition](07-revision-and-interview-preparation/DP_PATTERN_RECOGNITION.md), recurrence cheat sheet, common mistakes, interview strategy, revision schedule | 5 docs |

Root documents:

- [`ROADMAP.md`](ROADMAP.md): the full learning sequence, week by week.
- [`PROGRESS_TRACKER.md`](PROGRESS_TRACKER.md): checkboxes for concepts, walkthroughs, implementations, independent solves, revision.
- [`LEETCODE_TRACKER.md`](LEETCODE_TRACKER.md): one row per problem: number, name, difficulty, pattern, date, status, revision.
- [`CONTRIBUTING.md`](CONTRIBUTING.md): the content standard for adding topics and problems.

## How every topic folder is organized

```
NN-topic-name/
├── README.md         ← read first: what, why, prerequisites, terminology, practice list
├── walkthrough.md    ← the lesson: intuition → 7 questions → recursion → memo → table → optimized → dry run
└── solution/
    ├── BruteForce.java     plain recursion (slow, but shows the idea)
    ├── Memoization.java    top-down DP
    ├── Tabulation.java     bottom-up DP
    └── Optimized.java      space/time optimized
```

Only the files that make sense for a topic exist. Concept topics may contain a single demo, and advanced optimizations often contain `Slow.java` + `Optimized.java`.

## How to study each topic (the loop)

1. **Read `README.md`** (5 min). Check the prerequisites. If one is unfamiliar, go back first.
2. **Read `walkthrough.md` slowly** (30–90 min). Do every dry run on paper *before* reading the filled table.
3. **Close the walkthrough. Write the recursive solution yourself.** Then memoize it, then tabulate it.
4. **Run the reference solutions** and compare:
   ```bash
   cd 01-one-dimensional-dp/06-house-robber/solution
   java Memoization.java      # Java 11+ runs a single source file directly
   ```
5. **Do the exercises** at the end of the walkthrough (answers are hidden in `<details>`).
6. **Solve the practice problems** listed in the topic README, easiest first, on the judge site.
7. **Tick the boxes** in [`PROGRESS_TRACKER.md`](PROGRESS_TRACKER.md) and log problems in [`LEETCODE_TRACKER.md`](LEETCODE_TRACKER.md).

## How to practice problems

For every new problem, before writing any code, answer the **seven questions** on paper. The worksheet is in [`00-foundations/16-the-dp-problem-solving-framework`](00-foundations/16-the-dp-problem-solving-framework/README.md).

1. What is changing?  2. What does the state mean?  3. What are the choices?  4. What is the recurrence?
5. What are the base cases?  6. What order do I compute in?  7. Can I shrink the state?

Then:

- **Give each problem 30–45 minutes of honest effort.** If you're stuck, read only the "Deriving the DP" section of the problem's README, then try again.
- **Mark `Needs revision`** in the LeetCode tracker if you needed the solution. Re-solve it after 3, 7 and 21 days (see [`REVISION_SCHEDULE.md`](07-revision-and-interview-preparation/REVISION_SCHEDULE.md)).
- **Count a problem as "solved independently"** only if you derived the state and recurrence yourself.

## Recommended learning sequence

```
00 Foundations ──► 01 1D DP ──► 02 Grid / 3D ──► 03 Subsequence & Knapsack ──► 04 Advanced patterns ──► 05 Optimization
      │               │               │                     │                          │
      └───────────────┴───────────────┴─────────────────────┴──────────────────────────┴──► 06 Practice (in parallel, per pattern)
                                                                                         └──► 07 Revision (continuously)
```

Modules 00–03 are essential for interviews. Module 04 is needed for Medium/Hard LeetCode. Module 05 is mainly for competitive programming and the hardest LeetCode problems. Week-by-week detail is in [`ROADMAP.md`](ROADMAP.md).

## Completion criteria

You have finished the course when you can:

- [ ] Explain overlapping subproblems and optimal substructure using a recursion tree you draw yourself.
- [ ] Turn any recursive solution into memoization and then tabulation **mechanically**.
- [ ] For an unseen problem, state the DP state in one English sentence and write its recurrence and base cases.
- [ ] Recognize the pattern (1D, grid, knapsack, subsequence, interval, tree, bitmask, digit, state machine) from the statement and constraints.
- [ ] Solve every Easy/Medium problem in [`LEETCODE_TRACKER.md`](LEETCODE_TRACKER.md) without hints, and at least 70% of the Hard ones.
- [ ] Optimize space where possible and explain why it is safe.
- [ ] Pass the drill in [`DP_PATTERN_RECOGNITION.md`](07-revision-and-interview-preparation/DP_PATTERN_RECOGNITION.md) with ≥ 90% correct.

## Tooling

```bash
python3 scripts/validate_java.py           # compile + run every solution folder (isolated), report failures
python3 scripts/validate_java.py 01-one-dimensional-dp   # just one module
python3 scripts/check_links.py             # every internal link resolves; every judge URL is verified
python3 scripts/lc_lookup.py house-robber  # verify a LeetCode problem's id/title/difficulty live
```
