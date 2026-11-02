/**
 * Coin Change II vs Combination Sum IV — the loop order decides what is counted
 *
 * Idea: The SAME update `ways[a] += ways[a - coin]` counts two different things:
 *   - coins outer, amount inner  -> combinations (LeetCode 518, CSES 1636):
 *       coins are introduced one type at a time, so every multiset is built in one
 *       fixed order (all 1s, then all 2s, then all 5s) and is counted once.
 *   - amount outer, coins inner  -> ordered sequences / permutations
 *       (LeetCode 377, CSES 1635): for every amount we try every coin as the LAST
 *       coin, so 1+2 and 2+1 are different "last coin" choices and both are counted.
 *
 * Time complexity:  O(n * amount) for each function — two nested loops.
 * Space complexity: O(amount) — one array each.
 *
 * Run: java CombinationsVsPermutations.java
 */
public class CombinationsVsPermutations {

    // ways[a] = number of multisets (order ignored) of the coins seen so far that sum to a
    static long countCombinations(int[] coins, int amount) {
        long[] ways = new long[amount + 1];
        ways[0] = 1;
        for (int coin : coins) {
            for (int a = coin; a <= amount; a++) {
                ways[a] += ways[a - coin];
            }
        }
        return ways[amount];
    }

    // ways[a] = number of ordered sequences of coins that sum to a
    static long countPermutations(int[] coins, int amount) {
        long[] ways = new long[amount + 1];
        ways[0] = 1;
        for (int a = 1; a <= amount; a++) {
            for (int coin : coins) {
                if (coin <= a) ways[a] += ways[a - coin];   // `coin` is the last coin of the sequence
            }
        }
        return ways[amount];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("combinations [1,2], 3   {1,1,1} {1,2}", countCombinations(new int[]{1, 2}, 3), 2);
        check("permutations [1,2], 3   111 12 21", countPermutations(new int[]{1, 2}, 3), 3);
        check("combinations [1,2,5], 5", countCombinations(new int[]{1, 2, 5}, 5), 4);
        check("permutations [1,2,5], 5", countPermutations(new int[]{1, 2, 5}, 5), 9);
        check("LC 377 example nums [1,2,3], target 4", countPermutations(new int[]{1, 2, 3}, 4), 7);
        check("combinations [9], 3 (edge: impossible)", countCombinations(new int[]{9}, 3), 0);
        check("permutations [9], 3 (edge: impossible)", countPermutations(new int[]{9}, 3), 0);
        check("combinations, amount 0", countCombinations(new int[]{1, 2}, 0), 1);
        check("permutations, amount 0", countPermutations(new int[]{1, 2}, 0), 1);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
