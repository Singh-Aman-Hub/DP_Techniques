/**
 * 546. Remove Boxes — Memoization (top-down)
 *
 * Idea: a range alone is not enough, because a range's value depends on how
 * many same-coloured boxes wait outside it. f(l, r, k) = best score for
 * boxes[l..r] when k boxes of colour boxes[l] are glued to its left. Either
 * remove boxes[l] with its k companions now, or clear boxes[l+1..m-1] and
 * glue them all onto a later box m of the same colour.
 *
 * Time complexity:  O(n^4) — up to n^3 states (l, r, k), each tries up to n merge partners m
 * Space complexity: O(n^3) — the memo cube; recursion depth up to about 2n
 *
 * Run: java Memoization.java
 */
public class Memoization {

    // memo[l][r][k] = best score for boxes[l..r] with k glued boxes of colour boxes[l]; 0 = not computed
    private static int[][][] memo;
    private static int[] colours;

    static int removeBoxes(int[] boxes) {
        colours = boxes;
        int n = boxes.length;
        memo = new int[n][n][n];
        return best(0, n - 1, 0);
    }

    private static int best(int l, int r, int k) {
        if (l > r) return 0;                                  // nothing left in this range
        if (memo[l][r][k] != 0) return memo[l][r][k];         // every real state scores >= 1
        // option 1: remove boxes[l] together with its k glued companions right now
        int result = (k + 1) * (k + 1) + best(l + 1, r, 0);
        // option 2: clear the middle first, then glue everything onto a later same-colour box m
        for (int m = l + 1; m <= r; m++) {
            if (colours[m] == colours[l]) {
                result = Math.max(result, best(l + 1, m - 1, 0) + best(m, r, k + 1));
            }
        }
        memo[l][r][k] = result;
        return result;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("example [1,2,1]", removeBoxes(new int[]{1, 2, 1}), 5);
        check("[1,3,2,2,2,3,4,3,1]", removeBoxes(new int[]{1, 3, 2, 2, 2, 3, 4, 3, 1}), 23);
        check("[1,2,1,2,1]", removeBoxes(new int[]{1, 2, 1, 2, 1}), 11);
        check("all same [1,1,1]", removeBoxes(new int[]{1, 1, 1}), 9);
        check("single box (edge)", removeBoxes(new int[]{1}), 1);
    }

    private static void check(String label, int actual, int expected) {
        boolean ok = actual == expected;
        System.out.println((ok ? "PASS" : "FAIL") + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (!ok) System.exit(1);
    }
}
