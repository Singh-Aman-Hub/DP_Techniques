/**
 * 368. Largest Divisible Subset — Tabulation (bottom-up)
 *
 * Idea: sort, then run the O(n^2) LIS-style chain DP where "nums[j] divides
 * nums[i]" replaces "nums[j] < nums[i]". Keep parent pointers and walk them
 * back from the index with the largest chain.
 *
 * Time complexity:  O(n^2) — double loop (the O(n log n) sort is smaller)
 * Space complexity: O(n) — size and parent arrays
 *
 * Run: java Tabulation.java
 */
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
public class Tabulation {

    static List<Integer> largestDivisibleSubset(int[] nums) {
        int[] sorted = nums.clone();
        Arrays.sort(sorted);
        int n = sorted.length;
        int[] size = new int[n];     // size[i] = largest divisible chain ending at sorted[i]
        int[] parent = new int[n];   // parent[i] = previous index in that chain, -1 if none
        int bestEnd = 0;
        for (int i = 0; i < n; i++) {
            size[i] = 1;
            parent[i] = -1;
            for (int j = 0; j < i; j++) {
                if (sorted[i] % sorted[j] == 0 && size[j] + 1 > size[i]) {
                    size[i] = size[j] + 1;
                    parent[i] = j;
                }
            }
            if (size[i] > size[bestEnd]) bestEnd = i;
        }
        LinkedList<Integer> chain = new LinkedList<>();
        for (int k = bestEnd; k != -1; k = parent[k]) chain.addFirst(sorted[k]);
        return chain;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("example [3,4,16,8]", largestDivisibleSubset(new int[]{3, 4, 16, 8}).toString(), "[4, 8, 16]");
        check("[1,2,4,8]", largestDivisibleSubset(new int[]{1, 2, 4, 8}).toString(), "[1, 2, 4, 8]");
        check("ten numbers", largestDivisibleSubset(new int[]{5, 9, 18, 54, 108, 540, 90, 180, 360, 720}).toString(),
                "[9, 18, 90, 180, 360, 720]");
        check("single number (edge)", largestDivisibleSubset(new int[]{7}).toString(), "[7]");
    }

    private static void check(String label, String actual, String expected) {
        boolean ok = actual.equals(expected);
        System.out.println((ok ? "PASS" : "FAIL") + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (!ok) System.exit(1);
    }
}
