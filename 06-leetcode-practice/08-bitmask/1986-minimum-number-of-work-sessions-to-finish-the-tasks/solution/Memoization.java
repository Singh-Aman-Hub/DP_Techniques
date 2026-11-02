/**
 * 1986. Minimum Number of Work Sessions to Finish the Tasks — Memoization (submask DP)
 *
 * Idea: minSessions(left) = fewest sessions for the tasks in mask 'left'. Choose the group of
 * tasks that forms one session: any submask that fits in sessionTime. To avoid trying the same
 * partition in many orders, the chosen group must contain the lowest remaining task.
 *
 * Time complexity:  O(3^n) — over all masks, the submask loops visit 3^n (mask, submask) pairs.
 * Space complexity: O(2^n) — memo and subset sums (recursion depth at most n).
 *
 * Run: java Memoization.java
 */
import java.util.Arrays;

public class Memoization {

    private static int capacity;
    private static int[] subsetSum;   // subsetSum[s] = total duration of the tasks in s
    private static int[] memo;        // memo[left] = min sessions for tasks in left; -1 = unknown

    static int minSessions(int[] tasks, int sessionTime) {
        int n = tasks.length;
        capacity = sessionTime;
        subsetSum = new int[1 << n];
        for (int s = 1; s < (1 << n); s++) {
            int low = Integer.numberOfTrailingZeros(s);
            subsetSum[s] = subsetSum[s & (s - 1)] + tasks[low];
        }
        memo = new int[1 << n];
        Arrays.fill(memo, -1);
        return solve((1 << n) - 1);
    }

    private static int solve(int left) {
        if (left == 0) return 0;                       // nothing left: no session needed
        if (memo[left] != -1) return memo[left];
        int lowestTask = left & -left;                 // isolates the lowest set bit
        int best = Integer.MAX_VALUE;
        // enumerate every non-empty submask of 'left'
        for (int group = left; group > 0; group = (group - 1) & left) {
            if ((group & lowestTask) != 0 && subsetSum[group] <= capacity) {
                best = Math.min(best, 1 + solve(left ^ group));   // left ^ group removes the group
            }
        }
        memo[left] = best;
        return best;
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
