/**
 * 2050. Parallel Courses III — Memoization (DFS over prerequisites)
 *
 * Idea: finish(v) = time[v] + the latest finish among v's prerequisites. Prerequisites form a
 * DAG, so the recursion terminates; caching finish(v) makes each course computed once.
 * The answer is the largest finish time over all courses.
 *
 * Time complexity:  O(n + m) — each course is solved once and each relation read once.
 * Space complexity: O(n + m) — prerequisite lists, the memo, and recursion up to n deep.
 *
 * Run: java Memoization.java
 */
import java.util.ArrayList;
import java.util.List;

public class Memoization {

    private static List<List<Integer>> prerequisites;   // prerequisites.get(v) = courses that must finish before v
    private static int[] durations;
    private static int[] memo;                           // memo[v] = earliest finish of v; 0 = not computed

    static int minimumTime(int n, int[][] relations, int[] time) {
        prerequisites = new ArrayList<>();
        for (int i = 0; i < n; i++) prerequisites.add(new ArrayList<>());
        for (int[] r : relations) prerequisites.get(r[1] - 1).add(r[0] - 1);   // convert to 0-based
        durations = time;
        memo = new int[n];
        int months = 0;
        for (int v = 0; v < n; v++) months = Math.max(months, finish(v));
        return months;
    }

    private static int finish(int course) {
        if (memo[course] != 0) return memo[course];
        int latestPrerequisite = 0;                      // no prerequisites: may start at month 0
        for (int before : prerequisites.get(course)) {
            latestPrerequisite = Math.max(latestPrerequisite, finish(before));
        }
        memo[course] = latestPrerequisite + durations[course];
        return memo[course];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("n=3, [[1,3],[2,3]], time [3,2,5]", minimumTime(3, new int[][]{{1, 3}, {2, 3}}, new int[]{3, 2, 5}), 8);
        check("n=5 critical path 3->4->5", minimumTime(5, new int[][]{{1, 5}, {2, 5}, {3, 5}, {3, 4}, {4, 5}}, new int[]{1, 2, 3, 4, 5}), 12);
        check("single course (edge)", minimumTime(1, new int[][]{}, new int[]{7}), 7);
        check("no relations: longest course", minimumTime(3, new int[][]{}, new int[]{2, 9, 4}), 9);
        check("chain of 4", minimumTime(4, new int[][]{{1, 2}, {2, 3}, {3, 4}}, new int[]{1, 1, 1, 1}), 4);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
