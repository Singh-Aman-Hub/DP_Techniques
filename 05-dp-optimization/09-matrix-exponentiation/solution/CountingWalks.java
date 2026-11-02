/**
 * Counting walks of length K in a directed graph (AtCoder dp_r Walk) — O(K·N²) DP vs A^K
 *
 * Idea: let ways_s[v] = number of walks with s edges that end at v (starting anywhere).
 * Then ways_{s+1}[v] = sum over u with an edge u->v of ways_s[u]: one step is a
 * multiplication by the adjacency matrix A. After K steps the count of walks from u to v
 * is (A^K)[u][v], and the total number of walks is the sum of all entries of A^K.
 * Repeated squaring computes A^K in O(N³ log K) instead of K separate steps.
 *
 * Time complexity:  O(K·N²) for the step-by-step DP (K steps, each sums N² edge slots);
 *                   O(N³·log K) for the matrix power (log K multiplications of N x N matrices).
 * Space complexity: O(N) for the DP (two rolling vectors), O(N²) for the matrices.
 *
 * Run: java CountingWalks.java
 */
import java.util.Random;

public class CountingWalks {

    private static final long MOD = 1_000_000_007L;

    /** Step-by-step DP: start with one empty walk at every vertex, then apply K steps. */
    static long countWalksSlow(int[][] adjacency, long steps) {
        int n = adjacency.length;
        // ways[v] = number of walks with the current number of edges that end at vertex v
        long[] ways = new long[n];
        java.util.Arrays.fill(ways, 1); // a walk of 0 edges: just stand on a vertex
        for (long s = 0; s < steps; s++) {
            long[] next = new long[n]; // fresh array each step, so no stale values
            for (int u = 0; u < n; u++)
                for (int v = 0; v < n; v++)
                    if (adjacency[u][v] == 1) next[v] = (next[v] + ways[u]) % MOD;
            ways = next;
        }
        long total = 0;
        for (long w : ways) total = (total + w) % MOD;
        return total;
    }

    static long[][] multiply(long[][] a, long[][] b) {
        int size = a.length;
        long[][] product = new long[size][size];
        for (int i = 0; i < size; i++)
            for (int k = 0; k < size; k++) {
                if (a[i][k] == 0) continue;
                for (int j = 0; j < size; j++)
                    product[i][j] = (product[i][j] + a[i][k] * b[k][j]) % MOD; // < 1e18 + 1e9, fits in long
            }
        return product;
    }

    static long[][] power(long[][] base, long exponent) {
        int size = base.length;
        long[][] result = new long[size][size];
        for (int i = 0; i < size; i++) result[i][i] = 1; // identity: A^0, walks of length 0
        long[][] square = base;
        while (exponent > 0) {
            if ((exponent & 1) == 1) result = multiply(result, square);
            square = multiply(square, square);
            exponent >>= 1;
        }
        return result;
    }

    /** Sum of all entries of A^K = number of walks with exactly K edges. */
    static long countWalksFast(int[][] adjacency, long steps) {
        int n = adjacency.length;
        long[][] a = new long[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++) a[i][j] = adjacency[i][j];
        long[][] aPowK = power(a, steps);
        long total = 0;
        for (long[] row : aPowK)
            for (long value : row) total = (total + value) % MOD;
        return total;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        // Graph: 0->1, 0->2, 1->2, 2->0 (the walkthrough's example)
        int[][] graph = { {0, 1, 1}, {0, 0, 1}, {1, 0, 0} };
        check("K=1 (just the 4 edges)", countWalksFast(graph, 1), 4);
        check("K=2 slow", countWalksSlow(graph, 2), 5);
        check("K=2 fast", countWalksFast(graph, 2), 5);
        check("K=3 slow", countWalksSlow(graph, 3), 7);
        check("K=3 fast", countWalksFast(graph, 3), 7);
        check("K=0 edge (one empty walk per vertex)", countWalksFast(graph, 0), 3);

        int[][] noEdges = { {0, 0}, {0, 0} };
        check("no edges, K=5 edge", countWalksFast(noEdges, 5), 0);

        // Complete graph with self-loops on 2 vertices: every step has 2 choices, 2 * 2^K walks.
        int[][] full = { {1, 1}, {1, 1} };
        check("2-vertex full graph K=10 -> 2^11", countWalksFast(full, 10), 2048);
        check("huge K=10^18 matches 2^(10^18+1) mod p", countWalksFast(full, 1_000_000_000_000_000_000L),
              modPow(2, 1_000_000_000_000_000_001L));

        Random random = new Random(2026);
        boolean allAgree = true;
        for (int trial = 0; trial < 200 && allAgree; trial++) {
            int n = 1 + random.nextInt(5);
            int[][] adjacency = new int[n][n];
            for (int i = 0; i < n; i++)
                for (int j = 0; j < n; j++) adjacency[i][j] = random.nextInt(2);
            long steps = random.nextInt(60);
            allAgree = countWalksSlow(adjacency, steps) == countWalksFast(adjacency, steps);
        }
        check("200 random graphs agree", allAgree ? 1 : 0, 1);
    }

    /** Plain number exponentiation by squaring, used only to produce an expected value. */
    private static long modPow(long base, long exponent) {
        long result = 1;
        base %= MOD;
        while (exponent > 0) {
            if ((exponent & 1) == 1) result = result * base % MOD;
            base = base * base % MOD;
            exponent >>= 1;
        }
        return result;
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
