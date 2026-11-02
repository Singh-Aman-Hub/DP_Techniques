/**
 * 368. Largest Divisible Subset — Memoization (top-down)
 *
 * Idea: after sorting, a divisible subset is a chain where each element divides
 * the next (divisibility is transitive). f(i) = size of the best chain whose
 * largest element is nums[i]; parent[i] remembers the previous element so the
 * chain can be rebuilt.
 *
 * Time complexity:  O(n^2) — n states, each scans up to n smaller elements once (plus O(n log n) sort)
 * Space complexity: O(n) — size and parent arrays; recursion depth up to n
 *
 * Run: java Memoization.java
 */
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
public class Memoization {

    // size[i] = largest chain ending at sorted nums[i]; 0 = not computed yet
    private static int[] size;
    // parent[i] = index of the previous chain element, -1 if nums[i] starts the chain
    private static int[] parent;
    private static int[] sorted;

    static List<Integer> largestDivisibleSubset(int[] nums) {
        sorted = nums.clone();
        Arrays.sort(sorted);                              // smaller divisors come first
        int n = sorted.length;
        size = new int[n];
        parent = new int[n];
        int bestEnd = 0;
        for (int i = 0; i < n; i++) {
            if (chainEndingAt(i) > size[bestEnd]) bestEnd = i;
        }
        LinkedList<Integer> chain = new LinkedList<>();
        for (int k = bestEnd; k != -1; k = parent[k]) chain.addFirst(sorted[k]);  // largest -> smallest
        return chain;
    }

    private static int chainEndingAt(int i) {
        if (size[i] != 0) return size[i];
        int best = 1, bestParent = -1;
        for (int j = 0; j < i; j++) {
            if (sorted[i] % sorted[j] == 0 && chainEndingAt(j) + 1 > best) {
                best = size[j] + 1;
                bestParent = j;
            }
        }
        size[i] = best;
        parent[i] = bestParent;
        return best;
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
