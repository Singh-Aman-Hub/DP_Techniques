/**
 * Partition With Minimum Difference — Brute force
 *
 * Idea: every element goes to group 1 (take) or group 2 (skip). Track s1 = sum of group 1.
 * At the end, group 2 sums to total - s1, so the difference is |total - 2 * s1|.
 * Try all 2^n assignments and keep the smallest difference.
 *
 * Time complexity:  O(2^n) — every assignment is tried.
 * Space complexity: O(n) — recursion depth.
 *
 * Run: java BruteForce.java
 */
public class BruteForce {

    static int minDifference(int[] arr) {
        int total = 0;
        for (int x : arr) total += x;
        return explore(arr, 0, 0, total);
    }

    private static int explore(int[] arr, int i, int s1, int total) {
        if (i == arr.length) return Math.abs(total - 2 * s1);    // |s1 - s2| with s2 = total - s1
        int putInGroup1 = explore(arr, i + 1, s1 + arr[i], total);
        int putInGroup2 = explore(arr, i + 1, s1, total);
        return Math.min(putInGroup1, putInGroup2);
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("[1,6,11,5] -> {1,11}=12 vs {6,5}=11", minDifference(new int[]{1, 6, 11, 5}), 1);
        check("[3,9,7,3] -> {3,7}=10 vs {9,3}=12", minDifference(new int[]{3, 9, 7, 3}), 2);
        check("[1,2,3,9] -> {1,2,3}=6 vs {9}=9", minDifference(new int[]{1, 2, 3, 9}), 3);
        check("[1,1] -> perfect split", minDifference(new int[]{1, 1}), 0);
        check("[5] (edge: one element, other side empty)", minDifference(new int[]{5}), 5);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
