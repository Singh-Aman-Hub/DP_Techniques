/**
 * Optimal Substructure — stairs: a "min" problem and a "count" problem
 *
 * Idea: minCost(i) = cheapest total cost to stand on step i, where you pay
 *       cost[j] when you leave step j and may climb 1 or 2 steps. The cheapest
 *       way to reach step i must arrive from step i-1 or step i-2, and the part
 *       of the route before that last move must itself be the cheapest way to
 *       reach i-1 (or i-2). So the optimal answer is built from optimal
 *       answers to smaller problems: optimal substructure.
 *       waysToClimb(n) shows that counting problems decompose the same way:
 *       every way to reach n ends with a 1-step or a 2-step, so the counts add.
 *
 *       Plain recursion is used on purpose: this topic is about WHY the
 *       recurrence is correct, not yet about making it fast (that is topic 06).
 *
 * Time complexity:  O(2^n) for both — two recursive calls per step, nothing cached.
 * Space complexity: O(n) — recursion depth.
 *
 * Run: java StairsSubstructure.java
 */
public class StairsSubstructure {

    /**
     * Cheapest cost to stand on step `step`. Steps 0 and 1 are free starting points.
     * The "top" of a staircase with cost.length steps is step cost.length.
     */
    static int minCost(int[] cost, int step) {
        if (step == 0 || step == 1) return 0;               // you may start here for free
        int fromOneBelow = minCost(cost, step - 1) + cost[step - 1];
        int fromTwoBelow = minCost(cost, step - 2) + cost[step - 2];
        return Math.min(fromOneBelow, fromTwoBelow);       // the best route uses the best sub-route
    }

    /** Rebuilds the list of steps visited on one cheapest route to the top (for display). */
    static String cheapestRoute(int[] cost, int step) {
        if (step == 0 || step == 1) return "start at " + step;
        int fromOneBelow = minCost(cost, step - 1) + cost[step - 1];
        int fromTwoBelow = minCost(cost, step - 2) + cost[step - 2];
        int previous = (fromOneBelow <= fromTwoBelow) ? step - 1 : step - 2;
        return cheapestRoute(cost, previous) + " -> " + step;
    }

    /** Number of different ways to climb to step n using moves of 1 or 2 steps. */
    static long waysToClimb(int n) {
        if (n == 0) return 1;     // standing at the bottom: one way (do nothing)
        if (n == 1) return 1;     // only a single 1-step
        // every way ends with either a 1-step (from n-1) or a 2-step (from n-2);
        // those two groups never overlap, so the counts add up
        return waysToClimb(n - 1) + waysToClimb(n - 2);
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        int[] small = {10, 15, 20};
        check("minCost to top of [10,15,20]", minCost(small, small.length), 15);
        System.out.println("      route: " + cheapestRoute(small, small.length));

        int[] longer = {1, 100, 1, 1, 1, 100, 1, 1, 100, 1};
        check("minCost to top of [1,100,1,1,1,100,1,1,100,1]", minCost(longer, longer.length), 6);
        System.out.println("      route: " + cheapestRoute(longer, longer.length));

        int[] twoSteps = {5, 7};
        check("minCost to top of [5,7]  [edge: two steps]", minCost(twoSteps, twoSteps.length), 5);

        // optimal substructure check: the answer for the top equals
        // min(best(top-1) + cost, best(top-2) + cost) using the sub-answers
        int top = longer.length;
        int rebuilt = Math.min(minCost(longer, top - 1) + longer[top - 1],
                               minCost(longer, top - 2) + longer[top - 2]);
        check("top answer rebuilt from optimal sub-answers", rebuilt, minCost(longer, top));

        check("waysToClimb(0)  [edge]", waysToClimb(0), 1);
        check("waysToClimb(1)", waysToClimb(1), 1);
        check("waysToClimb(3)", waysToClimb(3), 3);
        check("waysToClimb(4)", waysToClimb(4), 5);
        check("waysToClimb(10)", waysToClimb(10), 89);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
