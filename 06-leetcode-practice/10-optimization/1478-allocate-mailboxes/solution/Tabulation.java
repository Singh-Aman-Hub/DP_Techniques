/**
 * 1478. Allocate Mailboxes — Tabulation (prefix version of the partition DP)
 *
 * Idea: dp[b][j] = min total distance for the first j sorted houses (indices 0..j-1) with b boxes.
 *       dp[0][0] = 0, dp[0][j>0] = INF.
 *       dp[b][j] = min over i in [b-1, j-1] of dp[b-1][i] + cost[i][j-1]   (last box serves i..j-1).
 *       Row b only reads row b-1, so two rows suffice.
 *
 * Time complexity:  O(n^2 * k) — k rows, n cells each, up to n split points per cell.
 * Space complexity: O(n^2) for the cost table, O(n) for the two rows.
 *
 * Run: java Tabulation.java
 */
import java.util.Arrays;

public class Tabulation {

    private static final int INF = Integer.MAX_VALUE / 2;

    static int minDistance(int[] housesInput, int k) {
        int[] houses = housesInput.clone();
        Arrays.sort(houses);
        int n = houses.length;
        int[][] cost = medianCosts(houses);

        // previous[j] = best for the first j houses using (b - 1) boxes
        int[] previous = new int[n + 1];
        Arrays.fill(previous, INF);
        previous[0] = 0;
        for (int b = 1; b <= k; b++) {
            int[] current = new int[n + 1];
            Arrays.fill(current, INF);
            current[0] = 0; // zero houses need nothing (lets us use fewer boxes than k)
            for (int j = 1; j <= n; j++) {
                for (int i = 0; i < j; i++) {
                    if (previous[i] == INF) continue;
                    current[j] = Math.min(current[j], previous[i] + cost[i][j - 1]);
                }
            }
            previous = current;
        }
        return previous[n];
    }

    // cost[i][j] = sum of |houses[t] - median| for t in i..j, built from shorter groups
    static int[][] medianCosts(int[] houses) {
        int n = houses.length;
        int[][] cost = new int[n][n];
        for (int length = 2; length <= n; length++) {
            for (int i = 0; i + length - 1 < n; i++) {
                int j = i + length - 1;
                cost[i][j] = (i + 1 <= j - 1 ? cost[i + 1][j - 1] : 0) + houses[j] - houses[i];
            }
        }
        return cost;
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
