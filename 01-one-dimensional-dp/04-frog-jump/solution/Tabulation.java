/**
 * Frog Jump (AtCoder EDPC dp_a / dp_b) — Tabulation (bottom-up DP)
 *
 * Idea: fill minCost[0..n-1] left to right; minCost[i] looks back at up to k
 * earlier stones, all already final.
 * Also shows how to read the AtCoder input format from text, so the same
 * method can be fed from System.in in a real submission.
 *
 * Time complexity:  O(n*k) — n cells, up to k candidates each (O(n) for Frog 1).
 * Space complexity: O(n) — the table.
 *
 * Run: java Tabulation.java
 */
import java.util.Scanner;

public class Tabulation {

    static long frog1(int[] h) {
        int n = h.length;
        // minCost[i] = cheapest total cost to land on stone i
        long[] minCost = new long[n];
        minCost[0] = 0;
        for (int i = 1; i < n; i++) {
            long fromOne = minCost[i - 1] + Math.abs(h[i] - h[i - 1]);
            long fromTwo = (i >= 2) ? minCost[i - 2] + Math.abs(h[i] - h[i - 2]) : Long.MAX_VALUE;
            minCost[i] = Math.min(fromOne, fromTwo);
        }
        return minCost[n - 1];
    }

    static long frog2(int[] h, int k) {
        int n = h.length;
        long[] minCost = new long[n];   // same meaning as in frog1
        minCost[0] = 0;
        for (int i = 1; i < n; i++) {
            minCost[i] = Long.MAX_VALUE;
            for (int j = 1; j <= k && i - j >= 0; j++) {
                minCost[i] = Math.min(minCost[i], minCost[i - j] + Math.abs(h[i] - h[i - j]));
            }
        }
        return minCost[n - 1];
    }

    /** Frog 1 input: first N, then N heights. Works on System.in too: new Scanner(System.in). */
    static long frog1FromText(String input) {
        Scanner in = new Scanner(input);
        int n = in.nextInt();
        int[] h = new int[n];
        for (int i = 0; i < n; i++) h[i] = in.nextInt();
        return frog1(h);
    }

    /** Frog 2 input: first N and K, then N heights. */
    static long frog2FromText(String input) {
        Scanner in = new Scanner(input);
        int n = in.nextInt();
        int k = in.nextInt();
        int[] h = new int[n];
        for (int i = 0; i < n; i++) h[i] = in.nextInt();
        return frog2(h, k);
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        // Frog 1 (jumps of 1 or 2)
        check("Frog 1: [10,30,40,20] (walkthrough example)", frog1(new int[]{10, 30, 40, 20}), 30);
        check("Frog 1: [10,10] (edge: equal heights)", frog1(new int[]{10, 10}), 0);
        check("Frog 1: [30,10,60,10,60,50]", frog1(new int[]{30, 10, 60, 10, 60, 50}), 40);
        check("Frog 1: [7] (edge: already on the last stone)", frog1(new int[]{7}), 0);
        // Frog 2 (jumps of 1..k)
        check("Frog 2: k=3 [10,30,40,50,20]", frog2(new int[]{10, 30, 40, 50, 20}, 3), 30);
        check("Frog 2: k=1 [10,20,10] (edge: only 1-jumps)", frog2(new int[]{10, 20, 10}, 1), 20);
        check("Frog 2: k=100 [10,10] (edge: k larger than n)", frog2(new int[]{10, 10}, 100), 0);
        check("Frog 2: k=4 [40,10,20,70,80,10,20,70,80,60]", frog2(new int[]{40, 10, 20, 70, 80, 10, 20, 70, 80, 60}, 4), 40);
        check("Frog 2 with k=2 equals Frog 1", frog2(new int[]{30, 10, 60, 10, 60, 50}, 2), 40);
        // the same tests fed as raw judge-style input text
        check("Frog 1 from text", frog1FromText("4\n10 30 40 20\n"), 30);
        check("Frog 2 from text", frog2FromText("5 3\n10 30 40 50 20\n"), 30);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
