/**
 * Optimal Substructure — shortest path (has it) vs. longest simple path (does not)
 *
 * Idea: Shortest path in a graph with no cycles (a DAG):
 *         dist(v) = min over every edge u -> v of ( dist(u) + weight(u, v) ).
 *       This works because the start of a shortest path is itself a shortest path.
 *       We check the recurrence against brute force (enumerate every path).
 *
 *       Longest SIMPLE path (no repeated vertex) in a graph with cycles:
 *       the same trick fails. Glueing the longest q -> r path to the longest
 *       r -> t path gives a walk that repeats vertices, so it is not allowed.
 *       Sub-answers depend on each other through "which vertices are already
 *       used", so they are not independent. We show this with brute force.
 *
 * Time complexity:  dist() plain recursion O(number of paths) — exponential in
 *                   the worst case; brute-force DFS over simple paths is also exponential.
 * Space complexity: O(V) — recursion depth / visited array.
 *
 * Run: java GraphPaths.java
 */
public class GraphPaths {

    // ---------- Part 1: shortest path in a DAG ----------
    // Vertices A=0, B=1, C=2, D=3, E=4. Each row: {from, to, weight}.
    static final int[][] DAG_EDGES = {
        {0, 1, 4},   // A -> B
        {0, 2, 1},   // A -> C
        {2, 1, 2},   // C -> B
        {1, 3, 1},   // B -> D
        {2, 3, 5},   // C -> D
        {3, 4, 3},   // D -> E
        {2, 4, 8},   // C -> E
    };
    static final int SOURCE = 0;
    static final int UNREACHABLE = Integer.MAX_VALUE;

    /** Shortest distance from SOURCE to target, built from shortest distances to its predecessors. */
    static int shortest(int target) {
        if (target == SOURCE) return 0;                    // base case: already there
        int best = UNREACHABLE;
        for (int[] edge : DAG_EDGES) {
            if (edge[1] != target) continue;               // only edges that END at target
            int toPredecessor = shortest(edge[0]);         // optimal sub-answer
            if (toPredecessor == UNREACHABLE) continue;
            best = Math.min(best, toPredecessor + edge[2]);
        }
        return best;
    }

    /** Brute force for comparison: try every path from `at` to target and keep the cheapest. */
    static int shortestByEnumeration(int at, int target, int soFar) {
        if (at == target) return soFar;
        int best = UNREACHABLE;
        for (int[] edge : DAG_EDGES) {
            if (edge[0] == at) best = Math.min(best, shortestByEnumeration(edge[1], target, soFar + edge[2]));
        }
        return best;
    }

    // ---------- Part 2: longest simple path (the counterexample) ----------
    // Vertices q=0, r=1, s=2, t=3, unweighted (each edge has length 1).
    static final int[][] CYCLIC_EDGES = {
        {0, 1},  // q -> r
        {1, 0},  // r -> q
        {0, 2},  // q -> s
        {2, 3},  // s -> t
        {3, 1},  // t -> r
        {1, 3},  // r -> t
    };

    /** Longest path from `at` to target that never repeats a vertex (brute force DFS). -1 if none. */
    static int longestSimple(int at, int target, boolean[] used) {
        if (at == target) return 0;
        used[at] = true;
        int best = -1;
        for (int[] edge : CYCLIC_EDGES) {
            if (edge[0] != at || used[edge[1]]) continue;
            int rest = longestSimple(edge[1], target, used);
            if (rest >= 0) best = Math.max(best, 1 + rest);
        }
        used[at] = false;                 // undo, so other branches may use this vertex
        return best;
    }

    static int longestSimple(int from, int to) {
        return longestSimple(from, to, new boolean[4]);
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        String[] names = {"A", "B", "C", "D", "E"};
        for (int v = 0; v < names.length; v++) {
            System.out.println("shortest(A -> " + names[v] + ") = " + shortest(v));
        }
        check("shortest A -> A  [edge: source itself]", shortest(0), 0);
        check("shortest A -> B (A->C->B)", shortest(1), 3);
        check("shortest A -> D (A->C->B->D)", shortest(3), 4);
        check("shortest A -> E (A->C->B->D->E)", shortest(4), 7);
        check("recurrence agrees with brute force for E", shortest(4), shortestByEnumeration(0, 4, 0));
        // optimal substructure: dist(E) = min(dist(D) + 3, dist(C) + 8)
        check("dist(E) rebuilt from dist(D), dist(C)", Math.min(shortest(3) + 3, shortest(2) + 8), 7);

        System.out.println();
        int qToT = longestSimple(0, 3);
        int qToR = longestSimple(0, 1);
        int rToT = longestSimple(1, 3);
        check("longest simple q -> t", qToT, 2);   // q->r->t or q->s->t
        check("longest simple q -> r", qToR, 3);   // q->s->t->r
        check("longest simple r -> t", rToT, 3);   // r->q->s->t
        // If optimal substructure held, the best q->t path through r would be qToR + rToT = 6.
        // But that walk is q->s->t->r->q->s->t, which repeats q, s and t. The real answer is 2.
        check("glued sub-answers (6) do NOT give the answer", (qToR + rToT == qToT) ? 1 : 0, 0);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
