/**
 * 1434. Number of Ways to Wear Different Hats to Each Other — Memoization
 *
 * Idea: masking 40 hats would need 2^40 states, so go hat by hat and mask the (at most 10)
 * PEOPLE who already wear a hat. ways(hat, mask) = skip this hat, or give it to one person who
 * likes it and has no hat yet. Each hat is considered once, so no hat is ever shared.
 *
 * Time complexity:  O(40 * 2^n * n) — 41 * 2^n states, each tries the people who like that hat.
 * Space complexity: O(40 * 2^n) — the memo (recursion depth at most 41).
 *
 * Run: java Memoization.java
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Memoization {

    private static final long MOD = 1_000_000_007L;
    private static final int HATS = 40;
    private static List<List<Integer>> peopleWhoLike;
    private static int fullMask;
    // memo[hat][mask] = ways to finish using hats hat..40 when 'mask' people are served; -1 = unknown
    private static long[][] memo;

    static int numberWays(List<List<Integer>> hats) {
        peopleWhoLike = invert(hats);
        fullMask = (1 << hats.size()) - 1;
        memo = new long[HATS + 2][1 << hats.size()];
        for (long[] row : memo) Arrays.fill(row, -1);
        return (int) ways(1, 0);
    }

    private static long ways(int hat, int servedMask) {
        if (servedMask == fullMask) return 1;          // everyone has a hat; remaining hats unused
        if (hat > HATS) return 0;                      // out of hats but someone is still bare-headed
        if (memo[hat][servedMask] != -1) return memo[hat][servedMask];
        long total = ways(hat + 1, servedMask);        // nobody wears this hat
        for (int person : peopleWhoLike.get(hat)) {
            if ((servedMask & (1 << person)) == 0) {
                total = (total + ways(hat + 1, servedMask | (1 << person))) % MOD;
            }
        }
        memo[hat][servedMask] = total;
        return total;
    }

    // peopleWhoLike.get(h) = people who like hat h (the input is per person; our loop is per hat)
    private static List<List<Integer>> invert(List<List<Integer>> hats) {
        List<List<Integer>> peopleWhoLike = new ArrayList<>();
        for (int h = 0; h <= HATS; h++) peopleWhoLike.add(new ArrayList<>());
        for (int person = 0; person < hats.size(); person++)
            for (int hat : hats.get(person)) peopleWhoLike.get(hat).add(person);
        return peopleWhoLike;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("[[3,4],[4,5],[5]]", numberWays(lists(new int[][]{{3, 4}, {4, 5}, {5}})), 1);
        check("[[3,5,1],[3,5]]", numberWays(lists(new int[][]{{3, 5, 1}, {3, 5}})), 4);
        check("4 people x hats [1..4]", numberWays(lists(new int[][]{{1, 2, 3, 4}, {1, 2, 3, 4}, {1, 2, 3, 4}, {1, 2, 3, 4}})), 24);
        check("[[1],[1]] (impossible)", numberWays(lists(new int[][]{{1}, {1}})), 0);
        check("[[1]] (edge: one person)", numberWays(lists(new int[][]{{1}})), 1);
        // 10 people who all like all 40 hats: 40 * 39 * ... * 31 ordered choices (mod 1e9+7)
        int[][] everyone = new int[10][40];
        for (int p = 0; p < 10; p++) for (int h = 0; h < 40; h++) everyone[p][h] = h + 1;
        long expected = 1;
        for (int k = 0; k < 10; k++) expected = expected * (40 - k) % 1_000_000_007L;
        check("10 people x 40 hats", numberWays(lists(everyone)), expected);
    }

    // LeetCode passes List<List<Integer>>; convert from a plain 2-D array for readable tests
    private static List<List<Integer>> lists(int[][] rows) {
        List<List<Integer>> result = new ArrayList<>();
        for (int[] row : rows) {
            List<Integer> list = new ArrayList<>();
            for (int x : row) list.add(x);
            result.add(list);
        }
        return result;
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
