/**
 * 1478. Allocate Mailboxes — Memoization (median cost precompute + partition DP)
 *
 * Idea: After sorting, every mailbox serves a CONTIGUOUS group of houses (houses closer to one
 *       box than to another never interleave). For one group, the best box position is the
 *       median house, and its cost obeys cost[i][j] = cost[i+1][j-1] + (houses[j] - houses[i]):
 *       the outermost pair adds exactly its distance, whatever the inner median is.
 *       f(start, boxesLeft) = min total distance for houses start..n-1 using boxesLeft boxes
 *       = min over end of cost[start][end] + f(end+1, boxesLeft-1).
 *
 * Time complexity:  O(n^2 * k) — n*k states, up to n group ends each; cost table O(n^2).
 * Space complexity: O(n^2) for the cost table, O(n * k) for the memo.
 *
 * Run: java Memoization.java
 */
import java.util.Arrays;

public class Memoization {

    private static final int INF = Integer.MAX_VALUE / 2;
    private static int[][] cost;   // cost[i][j] = total distance of houses i..j to their median
    private static int[][] memo;   // memo[start][boxesLeft]; -1 = unknown
    private static int n;

    static int minDistance(int[] housesInput, int k) {
        int[] houses = housesInput.clone();
        Arrays.sort(houses);
        n = houses.length;
        cost = new int[n][n];
        for (int length = 2; length <= n; length++) {          // shorter groups first: cost[i+1][j-1] is ready
            for (int i = 0; i + length - 1 < n; i++) {
                int j = i + length - 1;
                cost[i][j] = (i + 1 <= j - 1 ? cost[i + 1][j - 1] : 0) + houses[j] - houses[i];
            }
        }
        memo = new int[n + 1][k + 1];
        for (int[] row : memo) Arrays.fill(row, -1);
        return solve(0, k);
    }

    private static int solve(int start, int boxesLeft) {
        if (start == n) return 0;                 // every house is served
        if (boxesLeft == 0) return INF;           // houses left but no box: invalid
        if (memo[start][boxesLeft] != -1) return memo[start][boxesLeft];
        int best = INF;
        for (int end = start; end < n; end++) {   // this box serves houses start..end
            best = Math.min(best, cost[start][end] + solve(end + 1, boxesLeft - 1));
        }
        memo[start][boxesLeft] = best;
        return best;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("[1,4,8,10,20], k=3", minDistance(new int[]{1, 4, 8, 10, 20}, 3), 5);
        check("[2,3,5,12,18], k=2", minDistance(new int[]{2, 3, 5, 12, 18}, 2), 9);
        check("[7,4,6,1], k=1 (unsorted input)", minDistance(new int[]{7, 4, 6, 1}, 1), 8);
        check("[3,6,14,10], k=4 (edge: one box per house)", minDistance(new int[]{3, 6, 14, 10}, 4), 0);
        check("[5], k=1 (edge)", minDistance(new int[]{5}, 1), 0);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
