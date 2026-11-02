# Frog Jump

> **Module:** 01 · One-Dimensional DP · **Difficulty:** Beginner · **Estimated time:** 75–100 min

## 1. What is this topic?

A frog hops along a row of stones of different heights. Each hop costs the height difference between the two stones. The frog wants to reach the last stone as cheaply as possible. In **Frog 1** it may hop 1 or 2 stones; in **Frog 2** it may hop anywhere from 1 to `k` stones.

This topic takes the `min` recurrence from [Min Cost Climbing Stairs](../03-minimum-cost-climbing-stairs/) and makes two changes: the price depends on **both ends** of the move, and the number of choices grows from 2 to `k`. It is also your first problem from a competitive programming judge (AtCoder), which reads input from standard input.

## 2. Why does this concept exist?

Real problems rarely have exactly two choices. When a state has `k` choices, the recurrence becomes a **loop** over choices, and the running time becomes "number of states × number of choices". Frog 2 is the cleanest place to learn that cost model, and to see that space optimization still works when you look back `k` cells instead of 2.

## 3. What problem does it solve?

- [AtCoder EDPC A · Frog 1](https://atcoder.jp/contests/dp/tasks/dp_a): hops of length 1 or 2, minimize total `|h[i] − h[j]|`.
- [AtCoder EDPC B · Frog 2](https://atcoder.jp/contests/dp/tasks/dp_b): hops of length 1 to `k`.

## 4. Prerequisites

- [Min Cost Climbing Stairs](../03-minimum-cost-climbing-stairs/) — the `min`-over-last-move recurrence.
- [Climbing Stairs](../02-climbing-stairs/) — the generalization from 2 moves to `k` moves.
- [Time and space complexity in DP](../../00-foundations/12-time-and-space-complexity-in-dp/) — states × work per state.

## 5. What you will learn

- A recurrence whose cost term depends on the source and the destination: `|h[i] − h[i−j]|`.
- Turning "`k` choices" into an inner loop, and why the time becomes `O(n·k)`.
- Guarding the loop so it never reads a stone before index 0.
- Reading competitive-programming input from `System.in` in Java, while keeping the solution in a testable method.
- A ring buffer for "look back `k`" space optimization.

## 6. Important terminology

| Term | Meaning in plain English |
|------|--------------------------|
| EDPC | AtCoder's "Educational DP Contest": 26 classic DP problems, `dp_a` to `dp_z`. |
| Standard input (stdin) | The text a judge feeds to your program, read with `System.in`. |
| Transition cost | The price of moving from one state to another, here `|h[i] − h[i−j]|`. |
| Fan-in | How many earlier states one state reads (2 in Frog 1, `k` in Frog 2). |
| Ring buffer | A small array reused in a circle: slot `i % size` stores item `i`. |

## 7. Where this is used in real problems

Any "move along a line with variable jump lengths and a price per jump": minimum coins to make a sum (jumps = coin values), jump games with scores, scheduling with variable-length tasks. Frog 2's `O(n·k)` will later be sped up with [Monotonic queue optimization](../../05-dp-optimization/04-monotonic-queue-optimization/) for problems like Jump Game VI.

## 8. After completing this topic you should be able to…

- [ ] Write the Frog 1 and Frog 2 recurrences from the 7 questions.
- [ ] Explain why Frog 2 is `O(n·k)` time.
- [ ] Read AtCoder input with `Scanner` or `BufferedReader` and call your solution method.
- [ ] Space-optimize Frog 1 to two variables and Frog 2 to `O(k)`.
- [ ] Recognize Climbing Stairs, Min Cost Climbing Stairs and Frog 1 as one family.

## 9. Practice problems

| # | Problem | Platform/ID | Difficulty | Pattern tested | Why it belongs here |
|---|---------|-------------|------------|----------------|---------------------|
| 1 | Frog 1 | [AtCoder dp_a](https://atcoder.jp/contests/dp/tasks/dp_a) | — | `min` over 2 moves, cost depends on both ends | The core problem of this topic. |
| 2 | Frog 2 | [AtCoder dp_b](https://atcoder.jp/contests/dp/tasks/dp_b) | — | `min` over `k` moves | The generalization in this topic. |
| 3 | Minimizing Coins | [CSES 1634](https://cses.fi/problemset/task/1634) | — | `min` over moves of coin-sized lengths | Frog 2 where the allowed jumps are the coin values and each jump costs 1. |
| 4 | Jump Game | [LeetCode 55](https://leetcode.com/problems/jump-game/) | Medium | Reachability with a per-position jump range | The jump range differs at every stone; a boolean DP works, and a greedy shortcut exists. |
| 5 | Jump Game VI | [LeetCode 1696](https://leetcode.com/problems/jump-game-vi/) | Medium | `max` over the last `k` states | Frog 2 with `max` and large `k`; the `O(n·k)` DP is too slow and needs a monotonic queue. |

## 10. Navigation

⬅ Previous: [Min Cost Climbing Stairs](../03-minimum-cost-climbing-stairs/) · ➡ Next: [Maximum Sum of Non-Adjacent Elements](../05-maximum-sum-of-non-adjacent-elements/) · 📖 [Walkthrough](walkthrough.md) · 💻 [Solutions](solution/)
