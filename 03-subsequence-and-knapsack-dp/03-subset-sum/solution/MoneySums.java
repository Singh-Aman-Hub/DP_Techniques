/**
 * CSES 1745 Money Sums — list every sum that some non-empty subset of coins can form
 *
 * Idea: run the subset-sum DP with target = total of all coins. Afterwards reachable[s] is
 * true for every achievable sum s, so the answer is just every s in 1..total with
 * reachable[s] = true. (The empty subset makes 0 reachable, but 0 is not asked for.)
 *
 * Time complexity:  O(n * total) — for CSES limits, 100 coins * 10^5 total = 10^7 steps.
 * Space complexity: O(total) — one boolean array.
 *
 * Run: java MoneySums.java
 */
import java.util.ArrayList;
import java.util.List;

public class MoneySums {

    static List<Integer> allSums(int[] coins) {
        int total = 0;
        for (int c : coins) total += c;
        // reachable[s] = some subset of the coins processed so far sums to s
        boolean[] reachable = new boolean[total + 1];
        reachable[0] = true;
        for (int coin : coins) {
            for (int s = total; s >= coin; s--) {          // reverse: each coin used at most once
                if (reachable[s - coin]) reachable[s] = true;
            }
        }
        List<Integer> sums = new ArrayList<>();
        for (int s = 1; s <= total; s++) {
            if (reachable[s]) sums.add(s);
        }
        return sums;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        // CSES sample: coins 4 2 5 2 -> 9 sums
        checkText("coins [4,2,5,2]", allSums(new int[]{4, 2, 5, 2}).toString(), "[2, 4, 5, 6, 7, 8, 9, 11, 13]");
        checkText("coins [3,4,5,2] (every sum 2..12 plus 14)", allSums(new int[]{3, 4, 5, 2}).toString(),
                "[2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 14]");
        checkText("single coin [7] (edge)", allSums(new int[]{7}).toString(), "[7]");
        checkText("coins [1,1,1]", allSums(new int[]{1, 1, 1}).toString(), "[1, 2, 3]");
    }

    private static void checkText(String label, String actual, String expected) {
        boolean ok = actual.equals(expected);
        System.out.println((ok ? "PASS" : "FAIL") + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (!ok) System.exit(1);
    }
}
