/**
 * 1986. Minimum Number of Work Sessions to Finish the Tasks — Tabulation (submask DP)
 *
 * Idea: sessions[mask] = fewest sessions to finish exactly the tasks in mask
 *       = min over non-empty submasks g of mask with sum(g) <= sessionTime of sessions[mask ^ g] + 1.
 * mask ^ g is smaller than mask, so iterating masks upward has every needed value ready.
 *
 * Time complexity:  O(3^n) — total number of (mask, submask) pairs.
 * Space complexity: O(2^n) — the table and the subset sums.
 *
 * Run: java Tabulation.java
 */
public class Tabulation {

    static int minSessions(int[] tasks, int sessionTime) {
        int n = tasks.length;
        int states = 1 << n;
        int[] subsetSum = new int[states];               // subsetSum[s] = total duration of tasks in s
        for (int s = 1; s < states; s++) {
            subsetSum[s] = subsetSum[s & (s - 1)] + tasks[Integer.numberOfTrailingZeros(s)];
        }
        // sessions[mask] = minimum sessions to finish exactly the tasks in mask
        int[] sessions = new int[states];
        for (int mask = 1; mask < states; mask++) {
            sessions[mask] = Integer.MAX_VALUE;
            for (int group = mask; group > 0; group = (group - 1) & mask) {
                if (subsetSum[group] <= sessionTime) {
                    sessions[mask] = Math.min(sessions[mask], sessions[mask ^ group] + 1);
                }
            }
        }
        return sessions[states - 1];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("[1,2,3], 3", minSessions(new int[]{1, 2, 3}, 3), 2);
        check("[3,1,3,1,1], 8", minSessions(new int[]{3, 1, 3, 1, 1}, 8), 2);
        check("[1,2,3,4,5], 15", minSessions(new int[]{1, 2, 3, 4, 5}, 15), 1);
        check("[5], 5 (edge: one task)", minSessions(new int[]{5}, 5), 1);
        check("[4,4,4,4], 5", minSessions(new int[]{4, 4, 4, 4}, 5), 4);
        // sum 48 needs >= 5; packing: (10) (8,2) (7,3) (6,4) (5,2,1)
        check("10 tasks, session 10", minSessions(new int[]{1, 5, 7, 10, 3, 8, 4, 2, 6, 2}, 10), 5);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
