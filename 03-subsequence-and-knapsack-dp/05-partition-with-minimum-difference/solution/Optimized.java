/**
 * Partition With Minimum Difference — One boolean array
 *
 * Idea: the scan only needs the LAST row of the subset-sum table, and the one-row
 * reverse-loop version of subset sum produces exactly that row.
 *
 * Time complexity:  O(n * total) — each element sweeps sums total/2 .. value once.
 * Space complexity: O(total) — one boolean array of length total/2 + 1.
 *
 * Run: java Optimized.java
 */
public class Optimized {

    static int minDifference(int[] arr) {
        int total = 0;
        for (int x : arr) total += x;
        int half = total / 2;
        // reachable[s] = some subset of the elements seen so far sums to s (only s <= half matter)
        boolean[] reachable = new boolean[half + 1];
        reachable[0] = true;
        for (int value : arr) {
            for (int s = half; s >= value; s--) {
                if (reachable[s - value]) reachable[s] = true;
            }
        }
        for (int s1 = half; s1 >= 0; s1--) {
            if (reachable[s1]) return total - 2 * s1;
        }
        return total;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("[1,6,11,5] -> {1,11}=12 vs {6,5}=11", minDifference(new int[]{1, 6, 11, 5}), 1);
        check("[3,9,7,3] -> {3,7}=10 vs {9,3}=12", minDifference(new int[]{3, 9, 7, 3}), 2);
        check("[1,2,3,9] -> {1,2,3}=6 vs {9}=9", minDifference(new int[]{1, 2, 3, 9}), 3);
        check("[1,1] -> perfect split", minDifference(new int[]{1, 1}), 0);
        check("[5] (edge: one element, other side empty)", minDifference(new int[]{5}), 5);
        check("[] (edge: empty array, both groups 0)", minDifference(new int[]{}), 0);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
