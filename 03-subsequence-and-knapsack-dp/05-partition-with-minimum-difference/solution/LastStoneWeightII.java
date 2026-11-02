/**
 * LC 1049 Last Stone Weight II — reduced to "partition with minimum difference"
 *
 * Idea: every smash replaces y and x with y - x. Unrolling all smashes, the final stone equals
 * a sum of the original weights each with a + or - sign (the "+" ones form group 1, the "-"
 * ones group 2). So the smallest final weight is the smallest achievable |s1 - s2|, and every
 * split can actually be realised by some smashing order. Then it is exactly topic 05.
 *
 * Time complexity:  O(n * total) — n <= 30 stones, total <= 3000, so <= 30 * 1501 steps.
 * Space complexity: O(total) — one boolean array.
 *
 * Run: java LastStoneWeightII.java
 */
public class LastStoneWeightII {

    static int lastStoneWeightII(int[] stones) {
        int total = 0;
        for (int w : stones) total += w;
        int half = total / 2;
        // reachable[s] = some group of stones weighs exactly s
        boolean[] reachable = new boolean[half + 1];
        reachable[0] = true;
        for (int w : stones) {
            for (int s = half; s >= w; s--) {
                if (reachable[s - w]) reachable[s] = true;
            }
        }
        for (int s1 = half; s1 >= 0; s1--) {
            if (reachable[s1]) return total - 2 * s1;
        }
        return total;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("[2,7,4,1,8,1]", lastStoneWeightII(new int[]{2, 7, 4, 1, 8, 1}), 1);
        check("[31,26,33,21,40]", lastStoneWeightII(new int[]{31, 26, 33, 21, 40}), 5);
        check("[1,1,4,2,2] -> {1,4} vs {1,2,2}", lastStoneWeightII(new int[]{1, 1, 4, 2, 2}), 0);
        check("[1] (edge: single stone survives)", lastStoneWeightII(new int[]{1}), 1);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
