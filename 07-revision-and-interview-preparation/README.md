# Module 07 — Revision and Interview Preparation

> **Module:** 07 of 07 · **Difficulty:** All levels · **Estimated time:** used throughout the course; about 1 dedicated week (week 16) at the end

Modules 00–05 teach DP topic by topic, and Module 06 gives you practice problems. This module is different: it has **no new topic** and **no Java solutions**. It is a set of five reference documents that tie the whole course together, help you remember it, and prepare you to use it under interview pressure.

The single most important skill this module trains is **recognition** — reading an unfamiliar problem and knowing, within a few minutes, whether it is DP, which family it belongs to, and what the state should be.

---

## The five files

| File | What it is for | When to use it |
|---|---|---|
| [DP_PATTERN_RECOGNITION.md](DP_PATTERN_RECOGNITION.md) | **The most important file.** Clues that a problem is DP; how to confirm overlapping subproblems and optimal substructure; how to choose 1D / 2D / 3D / bitmask states; how to identify every DP family (linear, grid, subsequence, knapsack, interval, tree, DAG, bitmask, digit, state machine); greedy vs. recursion vs. backtracking vs. DP; constraint → complexity table; a master flowchart; a pattern → recurrence → problem → folder table; 18 drills with hidden answers. | First read after [Module 03](../03-subsequence-and-knapsack-dp/). Re-read after [Module 04](../04-advanced-dp-patterns/). Do the drills before every mock interview. Whenever you open a new problem and don't know where to start. |
| [RECURRENCE_CHEATSHEET.md](RECURRENCE_CHEATSHEET.md) | Every major recurrence in the course on one page per pattern: state meaning, recurrence, base cases, loop order, complexity, with links to the topic folders. | At the end of each module to consolidate; the night before an interview; whenever you know the pattern but forget a detail (e.g., which way the knapsack loop runs). |
| [COMMON_MISTAKES.md](COMMON_MISTAKES.md) | Categorized beginner bugs — base cases, loop order, knapsack direction, combinations vs. permutations, off-by-one, memo keys, shared memo across tests, sentinels, overflow, −∞ arithmetic, greedy assumptions — each with buggy vs. fixed Java and a 10-point debugging checklist. | Every time a solution fails. Skim it once after [Module 00](../00-foundations/) and again after Module 03. Run the checklist before submitting. |
| [INTERVIEW_STRATEGY.md](INTERVIEW_STRATEGY.md) | A 45-minute plan for an unseen DP problem (clarify → brute force → recursion → memo → tabulate → optimize), how to say the state and recurrence aloud, how to test, what to do when stuck, and a fully scripted interview on Decode Ways. | From Module 06 onward, on every timed practice problem. Before mock and real interviews. |
| [REVISION_SCHEDULE.md](REVISION_SCHEDULE.md) | A 1/3/7/21/60-day spaced-repetition routine, a 16-week plan mapping weeks to modules, 30 must-revisit problems, and a self-assessment rubric per pattern. | **From day one.** Open it at the start of each study week to plan new topics and reviews. Self-assess at weeks 8, 13 and 16. |

---

## How this module fits into the course

```
 Week 1 ──────────────────────────────────────────────────────────────────▶ Week 16
 00 foundations │ 01 1D │ 02 grid │ 03 knapsack/subseq │ 04 advanced │ 05 optim │ 07
 ───────────────┴───────┴─────────┴────────────────────┴─────────────┴──────────┴────
 REVISION_SCHEDULE ......... used every week (spaced reviews + plan) ...............
 COMMON_MISTAKES ........... used whenever a solution fails .........................
                                    RECURRENCE_CHEATSHEET .. end of each module .....
                                              DP_PATTERN_RECOGNITION ... after 03 ....
                                                       INTERVIEW_STRATEGY .. with 06 ..
```

| After you finish… | Do this in Module 07 |
|---|---|
| [00-foundations](../00-foundations/) | Read [REVISION_SCHEDULE.md](REVISION_SCHEDULE.md) Section 1 and start your review log. Skim [COMMON_MISTAKES.md](COMMON_MISTAKES.md) sections 1, 5, 6. |
| [01-one-dimensional-dp](../01-one-dimensional-dp/) | Review cheat-sheet [Section 1](RECURRENCE_CHEATSHEET.md#1-one-dimensional-linear-dp). |
| [02-two-dimensional-and-grid-dp](../02-two-dimensional-and-grid-dp/) | Review cheat-sheet [Section 2](RECURRENCE_CHEATSHEET.md#2-grid-and-multi-dimensional-dp). Read recognition [Section 4](DP_PATTERN_RECOGNITION.md#4-choosing-the-state-1d-2d-3d-bitmask-) on state design. |
| [03-subsequence-and-knapsack-dp](../03-subsequence-and-knapsack-dp/) | Read **all** of [DP_PATTERN_RECOGNITION.md](DP_PATTERN_RECOGNITION.md); try drills 1–4, 10, 14, 15. Self-assess (week 8). |
| [04-advanced-dp-patterns](../04-advanced-dp-patterns/) | Re-read recognition Sections 5 and 8; do all 18 drills. Self-assess (week 13). |
| [05-dp-optimization](../05-dp-optimization/) | Review cheat-sheet [Section 12](RECURRENCE_CHEATSHEET.md#12-optimization-techniques). |
| [06-leetcode-practice](../06-leetcode-practice/) | Solve every problem with the [INTERVIEW_STRATEGY.md](INTERVIEW_STRATEGY.md) routine and a timer. |
| Everything | Week 16: drills, cheat sheet, three mock interviews, final self-assessment. |

---

## Prerequisites

- To *use* the schedule and the mistakes file: none — start on day one.
- To *use* the recognition guide and cheat sheet fully: Modules 00–03 at least. Sections on trees, bitmasks, digit DP and optimizations assume [Module 04](../04-advanced-dp-patterns/) and [Module 05](../05-dp-optimization/).

## After this module you should be able to…

- [ ] Look at an unseen problem and, within 5 minutes, say whether it is DP and which family it belongs to.
- [ ] Justify that choice using the statement's wording and its constraints.
- [ ] Choose a state by listing everything that changes, and add a dimension for an extra resource, agent or flag.
- [ ] Explain why greedy fails (or works) with a concrete example.
- [ ] Write the recurrence, base cases and loop order for every pattern in the cheat sheet from memory.
- [ ] Spot and fix the common bugs in your own code before running it.
- [ ] Work through a Medium DP problem in 45 minutes while explaining every step aloud.
- [ ] Keep solved problems solved with a spaced-repetition routine.

## Navigation

⬅ Previous module: [06-leetcode-practice](../06-leetcode-practice/) · 🏠 [Course README](../README.md) · 🗺 [Roadmap](../ROADMAP.md) · 📈 [Progress tracker](../PROGRESS_TRACKER.md)
