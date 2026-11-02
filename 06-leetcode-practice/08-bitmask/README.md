# Practice — Bitmask DP

Problems where part of the state is a **set** stored in the bits of an integer: bit `i` is 1 when item `i` is in the set. This works when the set has at most ~20 elements (`2^20 ≈ 10^6` states). Learn the technique first in [Bitmask DP](../../04-advanced-dp-patterns/09-bitmask-dp/) and the `(visited set, last node)` state in [Traveling Salesperson Problem](../../04-advanced-dp-patterns/10-traveling-salesperson-problem/).

Recurring questions these problems train:
- **Which side do I mask?** Always the small one (1125: skills not people; 1434: people not hats).
- **What does the mask already imply?** Position = popcount (526, 1879); running sum = sum of the mask (464, 698).
- **Is the state graph acyclic?** If moves can return to the same state, use BFS or a first-visit formulation (847).

Every problem folder has a README (derivation via the 7 questions, dry run, mistakes) and a `solution/` folder with self-checking Java.

| # | Problem | LeetCode | Difficulty | Pattern | Prerequisite topic |
|---|---|---|---|---|---|
| 1 | [526. Beautiful Arrangement](526-beautiful-arrangement/) | [link](https://leetcode.com/problems/beautiful-arrangement/) | Medium | Count permutations; mask of used numbers, position = popcount | [Bitmask DP](../../04-advanced-dp-patterns/09-bitmask-dp/) |
| 2 | [464. Can I Win](464-can-i-win/) | [link](https://leetcode.com/problems/can-i-win/) | Medium | Win/lose game DP memoized on a mask | [Bitmask DP](../../04-advanced-dp-patterns/09-bitmask-dp/) |
| 3 | [698. Partition to K Equal Sum Subsets](698-partition-to-k-equal-sum-subsets/) | [link](https://leetcode.com/problems/partition-to-k-equal-sum-subsets/) | Medium | Fill buckets in order; fill = sum(mask) % target | [Bitmask DP](../../04-advanced-dp-patterns/09-bitmask-dp/) |
| 4 | [1986. Minimum Number of Work Sessions to Finish the Tasks](1986-minimum-number-of-work-sessions-to-finish-the-tasks/) | [link](https://leetcode.com/problems/minimum-number-of-work-sessions-to-finish-the-tasks/) | Medium | Set partition: submask enumeration, then (sessions, fill) pair | [Bitmask DP](../../04-advanced-dp-patterns/09-bitmask-dp/) |
| 5 | [1879. Minimum XOR Sum of Two Arrays](1879-minimum-xor-sum-of-two-arrays/) | [link](https://leetcode.com/problems/minimum-xor-sum-of-two-arrays/) | Hard | Assignment DP, index = popcount | [Bitmask DP](../../04-advanced-dp-patterns/09-bitmask-dp/) |
| 6 | [1125. Smallest Sufficient Team](1125-smallest-sufficient-team/) | [link](https://leetcode.com/problems/smallest-sufficient-team/) | Hard | Set cover over a skill mask + reconstruction | [Bitmask DP](../../04-advanced-dp-patterns/09-bitmask-dp/) |
| 7 | [847. Shortest Path Visiting All Nodes](847-shortest-path-visiting-all-nodes/) | [link](https://leetcode.com/problems/shortest-path-visiting-all-nodes/) | Hard | (node, visited) state: BFS or first-visit TSP | [Traveling Salesperson Problem](../../04-advanced-dp-patterns/10-traveling-salesperson-problem/) |
| 8 | [943. Find the Shortest Superstring](943-find-the-shortest-superstring/) | [link](https://leetcode.com/problems/find-the-shortest-superstring/) | Hard | Held–Karp TSP on overlaps + path reconstruction | [Traveling Salesperson Problem](../../04-advanced-dp-patterns/10-traveling-salesperson-problem/) |
| 9 | [1434. Number of Ways to Wear Different Hats to Each Other](1434-number-of-ways-to-wear-different-hats-to-each-other/) | [link](https://leetcode.com/problems/number-of-ways-to-wear-different-hats-to-each-other/) | Hard | Iterate the large side (hats), mask the small side (people) | [Bitmask DP](../../04-advanced-dp-patterns/09-bitmask-dp/) |
| 10 | [1349. Maximum Students Taking Exam](1349-maximum-students-taking-exam/) | [link](https://leetcode.com/problems/maximum-students-taking-exam/) | Hard | Row-profile DP: previous row's seating as the mask | [Bitmask DP](../../04-advanced-dp-patterns/09-bitmask-dp/) |
