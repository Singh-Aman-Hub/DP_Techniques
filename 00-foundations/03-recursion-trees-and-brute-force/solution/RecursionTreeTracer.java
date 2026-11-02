/**
 * Recursion Trees and Brute Force — printing the recursion tree
 *
 * Idea: Pass the current depth into each recursive call and print the call
 *       indented by that depth. Children appear under their parent, one level
 *       further right, so the printout *is* the recursion tree turned sideways.
 *       We trace fib(4) and the take/skip tree for the array [1, 2, 3].
 *
 * Time complexity:  same as the untraced brute force, O(2^n) nodes, each printed once.
 * Space complexity: O(n) call stack, plus the printed output.
 *
 * Run: java RecursionTreeTracer.java
 */
public class RecursionTreeTracer {

    private static int nodesPrinted = 0;

    private static void printNode(int depth, String text) {
        nodesPrinted++;
        // "|  " per level makes the parent/child structure easy to follow
        System.out.println("|  ".repeat(depth) + text);
    }

    static long tracedFib(int n, int depth) {
        printNode(depth, "fib(" + n + ")" + (n <= 1 ? "  -> base case " + n : ""));
        if (n <= 1) return n;
        return tracedFib(n - 1, depth + 1) + tracedFib(n - 2, depth + 1);
    }

    /**
     * Take/skip tree: chosen holds the subset built so far, as text.
     * Returns how many leaves (complete subsets) have sum == target.
     */
    static int tracedSubsets(int[] values, int index, String chosen, int sum, int target, int depth) {
        if (index == values.length) {
            boolean hit = (sum == target);
            printNode(depth, "{" + chosen + "} sum=" + sum + (hit ? "  <-- matches target " + target : ""));
            return hit ? 1 : 0;
        }
        printNode(depth, "decide " + values[index] + "  (so far {" + chosen + "})");
        String withThis = chosen.isEmpty() ? "" + values[index] : chosen + "," + values[index];
        int take = tracedSubsets(values, index + 1, withThis, sum + values[index], target, depth + 1);
        int skip = tracedSubsets(values, index + 1, chosen, sum, target, depth + 1);
        return take + skip;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        System.out.println("Recursion tree of fib(4):");
        nodesPrinted = 0;
        long fibResult = tracedFib(4, 0);
        System.out.println();
        check("fib(4)", fibResult, 3);
        check("nodes printed for fib(4)", nodesPrinted, 9);

        System.out.println();
        System.out.println("Take/skip tree for [1, 2, 3], target 3 (left branch = take, right = skip):");
        nodesPrinted = 0;
        int matches = tracedSubsets(new int[]{1, 2, 3}, 0, "", 0, 3, 0);
        System.out.println();
        check("subsets with sum 3", matches, 2);
        check("nodes printed (2^4 - 1)", nodesPrinted, 15);

        System.out.println();
        System.out.println("Edge case, fib(0):");
        nodesPrinted = 0;
        check("fib(0)", tracedFib(0, 0), 0);
        check("nodes printed for fib(0)", nodesPrinted, 1);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
