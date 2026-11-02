/**
 * 1130. Minimum Cost Tree From Leaf Values — Space-optimized DP
 *
 * Idea: monotonic-stack greedy. A leaf smaller than both neighbours must be
 * merged with one of them; merging it with the SMALLER neighbour is never
 * worse, and afterwards it never affects another product. A decreasing stack
 * finds these local minima in one pass. Not a DP, but it relies on the same
 * "only range maxima matter" structure the DP exposes.
 *
 * Time complexity:  O(n) — every value is pushed once and popped once
 * Space complexity: O(n) — the stack
 *
 * Run: java Optimized.java
 */
import java.util.ArrayDeque;
import java.util.Deque;
public class Optimized {

    static int mctFromLeafValues(int[] arr) {
        Deque<Integer> stack = new ArrayDeque<>();            // values in decreasing order
        stack.push(Integer.MAX_VALUE);                        // sentinel: "no neighbour on this side"
        int cost = 0;
        for (int value : arr) {
            while (stack.peek() <= value) {                   // top is smaller than both neighbours
                int smallest = stack.pop();
                cost += smallest * Math.min(stack.peek(), value);
            }
            stack.push(value);
        }
        while (stack.size() > 2) {                            // leftovers are decreasing: pair each with its left
            int smallest = stack.pop();
            cost += smallest * stack.peek();
        }
        return cost;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("example [6,2,4]", mctFromLeafValues(new int[]{6, 2, 4}), 32);
        check("[3,1,5,2]", mctFromLeafValues(new int[]{3, 1, 5, 2}), 28);
        check("increasing [1,2,3,4]", mctFromLeafValues(new int[]{1, 2, 3, 4}), 20);
        check("two leaves (edge)", mctFromLeafValues(new int[]{4, 11}), 44);
    }

    private static void check(String label, int actual, int expected) {
        boolean ok = actual == expected;
        System.out.println((ok ? "PASS" : "FAIL") + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (!ok) System.exit(1);
    }
}
