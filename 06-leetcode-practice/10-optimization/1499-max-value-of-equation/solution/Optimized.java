/**
 * 1499. Max Value of Equation — Monotonic deque (sliding-window maximum), cross-checked
 *
 * Idea: bestEndingAt[j] = (yj + xj) + max of (yi - xi) over earlier points with xi >= xj - k.
 *       That is a sliding-window maximum, and the window only moves right, so a deque of indices
 *       with DECREASING (yi - xi) answers it in O(1) amortized:
 *       pop the front while it is too far left (xj - x_front > k),
 *       read the front as the best partner,
 *       pop the back while its (y - x) <= (yj - xj) (j is newer and at least as good), push j.
 *
 * Time complexity:  O(n) — each point is pushed and popped at most once.
 * Space complexity: O(n) — the deque.
 *
 * Run: java Optimized.java
 */
import java.util.Random;

public class Optimized {

    static int findMaxValueOfEquationFast(int[][] points, int k) {
        int n = points.length;
        int[] deque = new int[n];     // point indices; (y - x) strictly decreasing from front to back
        int head = 0, tail = 0;
        long best = Long.MIN_VALUE;
        for (int j = 0; j < n; j++) {
            int xj = points[j][0], yj = points[j][1];
            while (head < tail && xj - points[deque[head]][0] > k) head++;   // left the window
            if (head < tail) {
                int i = deque[head];
                best = Math.max(best, (long) yj + xj + points[i][1] - points[i][0]);
            }
            while (head < tail && points[deque[tail - 1]][1] - points[deque[tail - 1]][0] <= yj - xj) tail--;
            deque[tail++] = j;
        }
        return (int) best;
    }

    // The O(n^2) window scan from Tabulation.java, kept for cross-checking.
    static int findMaxValueOfEquationSlow(int[][] points, int k) {
        long best = Long.MIN_VALUE;
        for (int j = 1; j < points.length; j++) {
            for (int i = j - 1; i >= 0 && points[j][0] - points[i][0] <= k; i--) {
                best = Math.max(best, (long) points[j][1] + points[j][0] + points[i][1] - points[i][0]);
            }
        }
        return (int) best;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        int[][][] inputs = {{{1, 3}, {2, 0}, {5, 10}, {6, -10}}, {{0, 0}, {3, 0}, {9, 2}}, {{1, 1}, {2, 2}},
                            {{-3, 5}, {-1, -2}, {0, 4}, {2, 1}, {6, 7}}};
        int[] windows = {1, 3, 1, 4};
        int[] expected = {4, 3, 4, 12};
        for (int t = 0; t < inputs.length; t++) {
            check("fast, test " + (t + 1) + " k=" + windows[t], findMaxValueOfEquationFast(inputs[t], windows[t]), expected[t]);
            check("slow, test " + (t + 1) + " k=" + windows[t], findMaxValueOfEquationSlow(inputs[t], windows[t]), expected[t]);
        }
        Random random = new Random(1499);
        int compared = 0;
        while (compared < 5000) {
            int n = 2 + random.nextInt(12), k = 1 + random.nextInt(8);
            int[][] points = new int[n][2];
            int x = random.nextInt(10) - 5;
            for (int i = 0; i < n; i++) {
                x += 1 + random.nextInt(3);             // strictly increasing x, as the problem promises
                points[i][0] = x;
                points[i][1] = random.nextInt(41) - 20;
            }
            int slow = findMaxValueOfEquationSlow(points, k);
            if (slow == (int) Long.MIN_VALUE) continue;  // no valid pair; the problem guarantees one exists
            int fast = findMaxValueOfEquationFast(points, k);
            if (slow != fast) check("random input #" + compared, fast, slow);
            compared++;
        }
        System.out.println("PASS  deque solution agrees with O(n^2) scan on 5000 random inputs");
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
