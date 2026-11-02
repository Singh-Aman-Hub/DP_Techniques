/**
 * 1986. Minimum Number of Work Sessions to Finish the Tasks — Optimized (sessions, fill) pair DP
 *
 * Idea: schedule tasks one by one, filling sessions in order. For each mask keep the best pair
 * (opened sessions, fill of the last session), compared lexicographically: fewer sessions is
 * always at least as good, and with equal sessions a smaller fill leaves more room.
 * Adding task t: if it fits, fill += t; otherwise open a new session holding just t.
 *
 * Time complexity:  O(2^n * n) — each mask tries each task once.
 * Space complexity: O(2^n) — two ints per mask.
 *
 * Run: java Optimized.java
 */
import java.util.Arrays;

public class Optimized {

    static int minSessions(int[] tasks, int sessionTime) {
        int n = tasks.length;
        int states = 1 << n;
        int[] opened = new int[states];   // opened[mask] = sessions used so far (best pair, first part)
        int[] fill = new int[states];     // fill[mask]   = time used in the last session (second part)
        Arrays.fill(opened, Integer.MAX_VALUE);
        opened[0] = 1;                    // start with one empty session open
        fill[0] = 0;
        for (int mask = 0; mask < states; mask++) {
            if (opened[mask] == Integer.MAX_VALUE) continue;
            for (int t = 0; t < n; t++) {
                if ((mask & (1 << t)) != 0) continue;
                int next = mask | (1 << t);
                int newOpened, newFill;
                if (fill[mask] + tasks[t] <= sessionTime) { newOpened = opened[mask]; newFill = fill[mask] + tasks[t]; }
                else { newOpened = opened[mask] + 1; newFill = tasks[t]; }
                // keep the lexicographically smaller (opened, fill) pair
                if (newOpened < opened[next] || (newOpened == opened[next] && newFill < fill[next])) {
                    opened[next] = newOpened;
                    fill[next] = newFill;
                }
            }
        }
        return opened[states - 1];
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
