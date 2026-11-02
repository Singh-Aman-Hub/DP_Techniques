/**
 * 1434. Number of Ways to Wear Different Hats to Each Other — Tabulation
 *
 * Idea: ways[h][mask] = ways to hand out hats 1..h so that exactly the people in mask wear one.
 * Hat h is either unused (ways[h-1][mask]) or given to a person p in mask who likes it
 * (ways[h-1][mask without p]). Layer h depends only on layer h-1.
 *
 * Time complexity:  O(40 * 2^n * n) — 40 layers x 2^n masks x people who like the hat.
 * Space complexity: O(40 * 2^n) — the full table.
 *
 * Run: java Tabulation.java
 */
import java.util.ArrayList;
import java.util.List;

public class Tabulation {

    private static final long MOD = 1_000_000_007L;
    private static final int HATS = 40;

    static int numberWays(List<List<Integer>> hats) {
        int n = hats.size();
        List<List<Integer>> peopleWhoLike = invert(hats);
        // ways[h][mask] = assignments using hats 1..h that serve exactly the people in mask
        long[][] ways = new long[HATS + 1][1 << n];
        ways[0][0] = 1;                                // no hats, nobody served: one empty assignment
        for (int hat = 1; hat <= HATS; hat++) {
            for (int mask = 0; mask < (1 << n); mask++) {
                long total = ways[hat - 1][mask];      // hat unused
                for (int person : peopleWhoLike.get(hat)) {
                    if ((mask & (1 << person)) != 0) {
                        total += ways[hat - 1][mask ^ (1 << person)];   // this hat went to 'person'
                    }
                }
                ways[hat][mask] = total % MOD;
            }
        }
        return (int) ways[HATS][(1 << n) - 1];
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
