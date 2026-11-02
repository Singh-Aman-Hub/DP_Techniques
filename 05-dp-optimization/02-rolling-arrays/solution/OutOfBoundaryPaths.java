/**
 * Out of Boundary Paths (LC 576) — rolling layers and the "reset the reused layer" pitfall
 *
 * Idea: ways[s][r][c] = number of move sequences of length s that keep the ball inside
 * the grid and leave it on (r, c). Layer s+1 is built by PUSHING each cell's count to its
 * 4 neighbours (or out of the grid, which adds to the answer). Layer s+1 reads only
 * layer s, so two layers indexed by s & 1 suffice. Because we ADD into the next layer,
 * it must be cleared to 0 first; otherwise it still holds the counts of layer s-1.
 *
 * Time complexity:  O(maxMove·m·n) — each of maxMove steps touches every cell with 4 moves.
 * Space complexity: O(maxMove·m·n) for the full table, O(m·n) for the rolling version.
 *
 * Run: java OutOfBoundaryPaths.java
 */
import java.util.Random;

public class OutOfBoundaryPaths {

    private static final int MOD = 1_000_000_007;
    private static final int[][] DIRECTIONS = { {1, 0}, {-1, 0}, {0, 1}, {0, -1} };

    /** Full 3D table: one m x n layer per number of moves made. */
    static int findPathsFull(int m, int n, int maxMove, int startRow, int startCol) {
        // ways[s][r][c] = sequences of s moves that stay inside and end on (r, c)
        long[][][] ways = new long[maxMove + 1][m][n];
        ways[0][startRow][startCol] = 1;
        long answer = 0;
        for (int s = 0; s < maxMove; s++) {
            for (int r = 0; r < m; r++) {
                for (int c = 0; c < n; c++) {
                    if (ways[s][r][c] == 0) continue;
                    for (int[] d : DIRECTIONS) {
                        int nr = r + d[0], nc = c + d[1];
                        if (nr < 0 || nr >= m || nc < 0 || nc >= n) answer = (answer + ways[s][r][c]) % MOD;
                        else ways[s + 1][nr][nc] = (ways[s + 1][nr][nc] + ways[s][r][c]) % MOD;
                    }
                }
            }
        }
        return (int) answer;
    }

    /** Rolling version: layer[s & 1] is the current step, layer[(s + 1) & 1] the next one. */
    static int findPathsRolling(int m, int n, int maxMove, int startRow, int startCol) {
        return rolling(m, n, maxMove, startRow, startCol, true);
    }

    /** The same code WITHOUT clearing the reused layer — a deliberate bug for demonstration. */
    static int findPathsRollingNoReset(int m, int n, int maxMove, int startRow, int startCol) {
        return rolling(m, n, maxMove, startRow, startCol, false);
    }

    private static int rolling(int m, int n, int maxMove, int startRow, int startCol, boolean resetNextLayer) {
        long[][][] layer = new long[2][m][n];
        layer[0][startRow][startCol] = 1;
        long answer = 0;
        for (int s = 0; s < maxMove; s++) {
            long[][] current = layer[s & 1];        // s & 1 is s % 2 for non-negative s: the last bit
            long[][] next = layer[(s + 1) & 1];
            if (resetNextLayer) {
                // "next" still holds layer s-1. We are about to ADD into it, so wipe it first.
                for (long[] row : next) java.util.Arrays.fill(row, 0);
            }
            for (int r = 0; r < m; r++) {
                for (int c = 0; c < n; c++) {
                    if (current[r][c] == 0) continue;
                    for (int[] d : DIRECTIONS) {
                        int nr = r + d[0], nc = c + d[1];
                        if (nr < 0 || nr >= m || nc < 0 || nc >= n) answer = (answer + current[r][c]) % MOD;
                        else next[nr][nc] = (next[nr][nc] + current[r][c]) % MOD;
                    }
                }
            }
        }
        return (int) answer;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("full    m=2 n=2 moves=2 start(0,0)", findPathsFull(2, 2, 2, 0, 0), 6);
        check("rolling m=2 n=2 moves=2 start(0,0)", findPathsRolling(2, 2, 2, 0, 0), 6);
        check("full    m=1 n=3 moves=3 start(0,1)", findPathsFull(1, 3, 3, 0, 1), 12);
        check("rolling m=1 n=3 moves=3 start(0,1)", findPathsRolling(1, 3, 3, 0, 1), 12);
        check("rolling maxMove=0 edge", findPathsRolling(3, 3, 0, 1, 1), 0);
        check("rolling 1x1 grid, 1 move (all 4 directions leave)", findPathsRolling(1, 1, 1, 0, 0), 4);

        // The missing reset over-counts: stale counts from two steps ago leak into the next layer.
        int buggy = findPathsRollingNoReset(1, 3, 3, 0, 1);
        check("no-reset bug differs from the correct 12 (bug gives " + buggy + ")", buggy != 12 ? 1 : 0, 1);

        Random random = new Random(576);
        boolean allAgree = true;
        for (int trial = 0; trial < 500 && allAgree; trial++) {
            int m = 1 + random.nextInt(5), n = 1 + random.nextInt(5), moves = random.nextInt(8);
            int sr = random.nextInt(m), sc = random.nextInt(n);
            allAgree = findPathsFull(m, n, moves, sr, sc) == findPathsRolling(m, n, moves, sr, sc);
        }
        check("500 random instances agree", allAgree ? 1 : 0, 1);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
