# Practice — Trees and Graphs

Problems where the DP state lives on the nodes of a **tree** or a **directed acyclic graph (DAG)**. The two big ideas:

- **Tree DP:** solve each subtree, pass a small summary up to the parent (post-order). Taught in [DP on Trees](../../04-advanced-dp-patterns/07-dp-on-trees/).
- **Graph DP:** find (or build) a DAG and process nodes in topological order — or let memoized DFS find that order. Taught in [DP on Graphs](../../04-advanced-dp-patterns/08-dp-on-graphs/).

Fully taught inside the topic, so not repeated here: 543 Diameter of Binary Tree, 337 House Robber III, 124 Binary Tree Maximum Path Sum and 834 Sum of Distances in Tree — see [DP on Trees](../../04-advanced-dp-patterns/07-dp-on-trees/).

Every problem folder has a README (derivation via the 7 questions, dry run, mistakes) and a `solution/` folder with self-checking Java.

| # | Problem | LeetCode | Difficulty | Pattern | Prerequisite topic |
|---|---|---|---|---|---|
| 1 | [96. Unique Binary Search Trees](96-unique-binary-search-trees/) | [link](https://leetcode.com/problems/unique-binary-search-trees/) | Medium | Catalan: root + independent subtrees, count by split point | [Interval DP](../../04-advanced-dp-patterns/03-interval-dp/), [DP on Trees](../../04-advanced-dp-patterns/07-dp-on-trees/) |
| 2 | [1339. Maximum Product of Splitted Binary Tree](1339-maximum-product-of-splitted-binary-tree/) | [link](https://leetcode.com/problems/maximum-product-of-splitted-binary-tree/) | Medium | Post-order subtree sums, decide per node | [DP on Trees](../../04-advanced-dp-patterns/07-dp-on-trees/) |
| 3 | [787. Cheapest Flights Within K Stops](787-cheapest-flights-within-k-stops/) | [link](https://leetcode.com/problems/cheapest-flights-within-k-stops/) | Medium | (node, flights left) DP = layered Bellman–Ford | [DP on Graphs](../../04-advanced-dp-patterns/08-dp-on-graphs/) |
| 4 | [1976. Number of Ways to Arrive at Destination](1976-number-of-ways-to-arrive-at-destination/) | [link](https://leetcode.com/problems/number-of-ways-to-arrive-at-destination/) | Medium | Dijkstra + path counting on the shortest-path DAG | [DP on Graphs](../../04-advanced-dp-patterns/08-dp-on-graphs/) |
| 5 | [968. Binary Tree Cameras](968-binary-tree-cameras/) | [link](https://leetcode.com/problems/binary-tree-cameras/) | Hard | Tree DP with 3 states per node | [DP on Trees](../../04-advanced-dp-patterns/07-dp-on-trees/) |
| 6 | [2246. Longest Path With Different Adjacent Characters](2246-longest-path-with-different-adjacent-characters/) | [link](https://leetcode.com/problems/longest-path-with-different-adjacent-characters/) | Hard | n-ary tree diameter, top-two children | [DP on Trees](../../04-advanced-dp-patterns/07-dp-on-trees/) |
| 7 | [329. Longest Increasing Path in a Matrix](329-longest-increasing-path-in-a-matrix/) | [link](https://leetcode.com/problems/longest-increasing-path-in-a-matrix/) | Hard | Longest path in an implicit DAG (memoized DFS) | [DP on Graphs](../../04-advanced-dp-patterns/08-dp-on-graphs/) |
| 8 | [2050. Parallel Courses III](2050-parallel-courses-iii/) | [link](https://leetcode.com/problems/parallel-courses-iii/) | Hard | DAG longest weighted path in Kahn's order | [DP on Graphs](../../04-advanced-dp-patterns/08-dp-on-graphs/) |
| 9 | [1857. Largest Color Value in a Directed Graph](1857-largest-color-value-in-a-directed-graph/) | [link](https://leetcode.com/problems/largest-color-value-in-a-directed-graph/) | Hard | DAG DP over (node, color) + cycle detection | [DP on Graphs](../../04-advanced-dp-patterns/08-dp-on-graphs/) |

**Suggested order:** finish [DP on Trees](../../04-advanced-dp-patterns/07-dp-on-trees/) before rows 1, 2, 5, 6, and [DP on Graphs](../../04-advanced-dp-patterns/08-dp-on-graphs/) before rows 3, 4, 7, 8, 9.
