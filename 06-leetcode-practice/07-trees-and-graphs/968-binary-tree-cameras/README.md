# 968. Binary Tree Cameras

> **Pattern:** Tree DP with several states per node (3-state) · **Difficulty:** Hard
> **Builds on:** [DP on Trees](../../../04-advanced-dp-patterns/07-dp-on-trees/) (House Robber III's "take / don't take" pair) and [State-machine DP](../../../04-advanced-dp-patterns/12-state-machine-dp/)

---

## Problem overview

A camera placed on a node watches that node, its parent and its children. Place the fewest cameras so that every node of the binary tree is watched.

**Why tree DP with 3 states.** In House Robber III (taught in [DP on Trees](../../../04-advanced-dp-patterns/07-dp-on-trees/)) each node returns two numbers: "best if I am taken" and "best if I am not". Here two states are not enough, because a node without a camera can be in two very different situations: *already watched by a child* or *still unwatched, waiting for its parent*. Naming those situations is the whole trick — the same idea as naming states in [State-machine DP](../../../04-advanced-dp-patterns/12-state-machine-dp/).

## Link & metadata

| Field | Value |
|---|---|
| LeetCode | [968. Binary Tree Cameras](https://leetcode.com/problems/binary-tree-cameras/) |
| Difficulty | Hard |
| Pattern | Post-order tree DP, 3 states per node |
| Prerequisite topics | [DP on Trees](../../../04-advanced-dp-patterns/07-dp-on-trees/), [State representation](../../../00-foundations/09-state-representation/), [State-machine DP](../../../04-advanced-dp-patterns/12-state-machine-dp/) |

## Examples

```
Example A [0,0,null,0,0]      Example B [0,0,null,0,null,0,null,null,0]
      r                              r
     /                              /
    x   <- camera                  a   <- camera?   chain of 5 nodes:
   / \                            /                 cameras on b and d
  l1  l2                         b                  -> answer 2
                                /
 answer 1                      c
                              /
                             d
                              \
                               e
```
Edge case: a single node needs **1** camera (nobody else can watch it).

## Brute-force intuition

Every node either has a camera or not: `2^n` placements. Check each for full coverage and keep the smallest. Hopeless for `n = 1000`. But notice that whether a placement *inside* a subtree is good depends on the outside world only through the subtree's root: does the root have a camera, and does the root still need watching? That small "interface" is our state.

## Deriving the DP (7 questions)

### 1. What is changing?
The node `v` we are solving for (its subtree), plus the situation of `v` itself.

### 2. What does the state represent?
For each node `v`, three numbers, each = minimum cameras inside `v`'s subtree such that every node **strictly below** `v` is watched, and:
- `withCam[v]` — `v` holds a camera (so `v` is watched);
- `watched[v]` — `v` has no camera but at least one child has one (so `v` is watched);
- `waiting[v]` — `v` has no camera and no child has one: `v` is **not yet watched**, and its parent must hold a camera.

### 3. What are the choices?
Camera on `v` or not; if not, whether some child provides coverage.

### 4. What is the recurrence?
With `best(c) = min(withCam[c], watched[c], waiting[c])`:
```
withCam[v] = 1 + best(L) + best(R)          // v's camera watches L and R, so even "waiting" children are fine
waiting[v] = watched[L] + watched[R]        // children have no camera, but must already be watched
watched[v] = min( withCam[L] + min(withCam[R], watched[R]),
                  withCam[R] + min(withCam[L], watched[L]) )   // at least one child has a camera
```
A child in `waiting` is only legal when `v` has a camera — otherwise nobody will ever watch it.

### 5. What are the base cases?
For a missing child (`null`): `withCam = ∞` (you cannot put a camera there), `watched = 0` (nothing needs watching), `waiting = ∞` (it never needs a parent's help). So a leaf gets `withCam = 1`, `watched = ∞`, `waiting = 0`.

### 6. What is the order of computation?
Children before parent → post-order. Final answer: `min(withCam[root], watched[root])` — the root has no parent, so it cannot stay `waiting`.

### 7. Can the state space be optimized?
Each node only needs its two children's triples, so the DFS returns a 3-element array: O(1) extra per node besides the stack. Going further, the min-values reveal a **greedy**: never put a camera on a leaf; put one on a node exactly when some child is waiting. That gives a 3-label greedy (`Optimized.java`) with the same O(n) time but no arithmetic.

## Recursive solution

```java
int[] solve(TreeNode node) {                      // returns {withCam, watched, waiting}
    if (node == null) return new int[]{INF, 0, INF};
    int[] L = solve(node.left), R = solve(node.right);
    int withCam = 1 + min3(L) + min3(R);
    int watched = Math.min(L[0] + Math.min(R[0], R[1]), R[0] + Math.min(L[0], L[1]));
    int waiting = L[1] + R[1];
    return new int[]{withCam, watched, waiting};
}
```

## Memoization

Each node is reached once by the DFS, so the "memo" is the triple each call returns to its parent — no repeated subproblem is ever recomputed. See [`solution/Memoization.java`](solution/Memoization.java).

## Tabulation

Iterative: BFS to list nodes parent-before-child, then fill a `Map<node, int[3]>` walking the list backwards. See [`solution/Tabulation.java`](solution/Tabulation.java).

## Space optimization

The greedy view (from the DP): post-order, each node returns one label.
```
child null           -> label COVERED
some child NOT_COVERED -> I place a camera, label HAS_CAMERA
some child HAS_CAMERA  -> label COVERED
otherwise              -> label NOT_COVERED (my parent must help)
root NOT_COVERED at the end -> one extra camera
```
See [`solution/Optimized.java`](solution/Optimized.java). The DP justifies it: on a leaf `withCam = 1 > waiting = 0`, so deferring the camera to the parent is never worse.

## Dry run

Example A (`INF` written as ∞). Triples are `(withCam, watched, waiting)`:

| Node | L triple | R triple | withCam | watched | waiting |
|---|---|---|---|---|---|
| l1 | null (∞,0,∞) | null | 1+0+0 = 1 | ∞ | 0+0 = 0 |
| l2 | null | null | 1 | ∞ | 0 |
| x | (1,∞,0) | (1,∞,0) | 1+0+0 = **1** | 1+min(1,∞) = 2 | ∞+∞ = ∞ |
| r | (1,2,∞) | null (∞,0,∞) | 1+1+0 = 2 | min(1+0, ∞) = **1** | 2+0 = 2 |

Answer = `min(withCam[r], watched[r]) = min(2, 1) = 1` (camera on `x`). ✔

## Complexity

| Approach | Time | Space | Why |
|---|---|---|---|
| Brute force | O(2ⁿ · n) | O(n) | every placement, each checked in O(n) |
| 3-state DFS | O(n) | O(h) | each node combines two triples in O(1); stack height h |
| Iterative | O(n) | O(n) | BFS array + map of triples |
| Greedy labels | O(n) | O(h) | one label per node |

## Why the optimized approach is better

It is the same O(n) but simpler to code in an interview once the DP has *proved* the greedy rule. Always derive the DP first; the greedy is a consequence, not a guess.

## Common mistakes

- Only two states (camera / no camera). You lose the difference between "watched" and "waiting".
- Treating `null` as "waiting": then every leaf would force a camera on itself.
- Forgetting the root: if the root ends `waiting`, add one camera.
- `INF + INF` overflowing `int` — use a modest INF like `1_000_000`.

## Key takeaway

When "taken / not taken" is not enough, ask **what the parent needs to know about this subtree**. Each distinct answer is a state.

## Related problems

- [DP on Trees](../../../04-advanced-dp-patterns/07-dp-on-trees/) — House Robber III (2 states) is the warm-up for this problem.
- [1339. Maximum Product of Splitted Binary Tree](../1339-maximum-product-of-splitted-binary-tree/) — one value per node.
- [2246. Longest Path With Different Adjacent Characters](../2246-longest-path-with-different-adjacent-characters/) — tree DP on a general (n-ary) tree.
