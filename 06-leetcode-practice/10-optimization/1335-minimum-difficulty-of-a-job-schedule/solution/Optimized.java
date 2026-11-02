/**
 * 1335. Minimum Difficulty of a Job Schedule — Monotonic-stack optimization, cross-checked
 *
 * Idea: Same recurrence as Tabulation: current[i] = min over s of previous[s-1] + max(jobs[s..i]).
 *       For a fixed i, all starts s after the "previous greater element" p of i (jobs[p] > jobs[i])
 *       share the same block maximum jobs[i]; so we only need the MIN of previous[s-1] over them.
 *       Starts s <= p have block max = max(jobs[s..p]), exactly the blocks that ended at p,
 *       so their best is already current[p].
 *       A stack of indices with decreasing job values stores, for each index t, the min of
 *       previous[s-1] over the starts s that belong to t. Popping smaller-or-equal jobs merges
 *       their minima into i. Every index is pushed and popped once per day.
 *
 * Time complexity:  O(n * d) — each day is one amortized O(n) stack pass.
 * Space complexity: O(n) — two rows plus the stack.
 *
 * Run: java Optimized.java
 */
import java.util.Random;

public class Optimized {

    private static final int INF = Integer.MAX_VALUE / 2;

    static int minDifficultyFast(int[] jobs, int d) {
        int n = jobs.length;
        if (n < d) return -1;
        int[] previous = new int[n];
        previous[0] = jobs[0];
        for (int i = 1; i < n; i++) previous[i] = Math.max(previous[i - 1], jobs[i]);

        int[] stackIndex = new int[n];
        int[] stackMinPrev = new int[n]; // min of previous[s-1] over the starts s "owned" by this stack entry
        for (int day = 2; day <= d; day++) {
            int[] current = new int[n];
            java.util.Arrays.fill(current, INF);
            int top = 0; // stack size
            for (int i = day - 1; i < n; i++) {
                int minPrev = previous[i - 1];  // start s = i: the last day is job i alone
                while (top > 0 && jobs[stackIndex[top - 1]] <= jobs[i]) {
                    minPrev = Math.min(minPrev, stackMinPrev[top - 1]); // those starts now have max jobs[i]
                    top--;
                }
                current[i] = minPrev + jobs[i];
                if (top > 0) current[i] = Math.min(current[i], current[stackIndex[top - 1]]); // starts at or before the bigger job
                stackIndex[top] = i;
                stackMinPrev[top] = minPrev;
                top++;
            }
            previous = current;
        }
        return previous[n - 1];
    }

    // The O(n^2 * d) DP, kept here so main can cross-check the fast version.
    static int minDifficultySlow(int[] jobs, int d) {
        int n = jobs.length;
        if (n < d) return -1;
        int[] previous = new int[n];
        previous[0] = jobs[0];
        for (int i = 1; i < n; i++) previous[i] = Math.max(previous[i - 1], jobs[i]);
        for (int day = 2; day <= d; day++) {
            int[] current = new int[n];
            java.util.Arrays.fill(current, INF);
            for (int i = day - 1; i < n; i++) {
                int hardest = 0;
                for (int s = i; s >= day - 1; s--) {
                    hardest = Math.max(hardest, jobs[s]);
                    current[i] = Math.min(current[i], previous[s - 1] + hardest);
                }
            }
            previous = current;
        }
        return previous[n - 1];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        int[][] inputs = {{6, 5, 4, 3, 2, 1}, {9, 9, 9}, {1, 1, 1}, {7, 1, 7, 1, 7, 1}, {11, 111, 22, 222, 33, 333, 44, 444}};
        int[] days = {2, 4, 3, 3, 6};
        int[] expected = {7, -1, 3, 15, 843};
        for (int t = 0; t < inputs.length; t++) {
            check("fast, " + java.util.Arrays.toString(inputs[t]) + " d=" + days[t], minDifficultyFast(inputs[t], days[t]), expected[t]);
            check("slow, " + java.util.Arrays.toString(inputs[t]) + " d=" + days[t], minDifficultySlow(inputs[t], days[t]), expected[t]);
        }
        Random random = new Random(1335);
        for (int trial = 0; trial < 2000; trial++) {
            int n = 1 + random.nextInt(12), d = 1 + random.nextInt(6);
            int[] jobs = new int[n];
            for (int i = 0; i < n; i++) jobs[i] = random.nextInt(10); // small values force many ties
            int slow = minDifficultySlow(jobs, d), fast = minDifficultyFast(jobs, d);
            if (slow != fast) check("random " + java.util.Arrays.toString(jobs) + " d=" + d, fast, slow);
        }
        System.out.println("PASS  monotonic-stack DP agrees with O(n^2 d) DP on 2000 random inputs");
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
