/**
 * Subsequences Fundamentals — Generating every subsequence with take/skip recursion
 *
 * Idea: at every index we make exactly one binary decision: TAKE the element into the
 * current subsequence, or SKIP it. When the index runs past the end, the choices made so
 * far describe one complete subsequence. n binary decisions give 2^n leaves, one per
 * subsequence (the empty one included).
 *
 * Time complexity:  O(n * 2^n) — 2^n leaves, and copying a subsequence of up to n items costs O(n).
 * Space complexity: O(n) extra for the recursion stack and the "current" list
 *                   (plus O(n * 2^n) for the result list we choose to keep).
 *
 * Run: java GenerateSubsequences.java
 */
import java.util.ArrayList;
import java.util.List;

public class GenerateSubsequences {

    /** Returns every subsequence of arr, in "take first, then skip" order. */
    static List<List<Integer>> allSubsequences(int[] arr) {
        List<List<Integer>> result = new ArrayList<>();
        generate(arr, 0, new ArrayList<>(), result);
        return result;
    }

    // current holds the elements chosen so far along this path of the recursion tree
    private static void generate(int[] arr, int index, List<Integer> current, List<List<Integer>> result) {
        if (index == arr.length) {
            // Every element has been decided: current is one finished subsequence.
            // Copy it, because current keeps changing after we return.
            result.add(new ArrayList<>(current));
            return;
        }
        // Choice 1: TAKE arr[index]
        current.add(arr[index]);
        generate(arr, index + 1, current, result);
        // Undo the take before trying the other branch ("backtracking")
        current.remove(current.size() - 1);

        // Choice 2: SKIP arr[index]
        generate(arr, index + 1, current, result);
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        int[] arr = {1, 2, 3};
        List<List<Integer>> subs = allSubsequences(arr);
        for (List<Integer> s : subs) System.out.println("  " + s);

        check("[1,2,3] has 2^3 = 8 subsequences", subs.size(), 8);
        checkText("[1,2,3] order (take before skip)", subs.toString(),
                "[[1, 2, 3], [1, 2], [1, 3], [1], [2, 3], [2], [3], []]");
        check("empty array has 1 subsequence (the empty one)", allSubsequences(new int[]{}).size(), 1);
        check("[5,5] has 4 subsequences (equal values are still different positions)",
                allSubsequences(new int[]{5, 5}).size(), 4);
        check("n = 10 gives 1024", allSubsequences(new int[10]).size(), 1024);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }

    private static void checkText(String label, String actual, String expected) {
        boolean ok = actual.equals(expected);
        System.out.println((ok ? "PASS" : "FAIL") + "  " + label + " -> " + actual);
        if (!ok) System.exit(1);
    }
}
