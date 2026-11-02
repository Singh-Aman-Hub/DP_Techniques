/**
 * 2050. Parallel Courses III — Tabulation (Kahn's topological order)
 *
 * Idea: process courses in topological order. start[v] collects the latest finish among the
 * prerequisites processed so far; when v's in-degree drops to 0, all its prerequisites are done,
 * so finish[v] = start[v] + time[v] is final. No recursion, so long chains are safe.
 *
 * Time complexity:  O(n + m) — every course is queued once and every relation relaxed once.
 * Space complexity: O(n + m) — follower lists, in-degrees, start times, and the queue.
 *
 * Run: java Tabulation.java
 */
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class Tabulation {

    static int minimumTime(int n, int[][] relations, int[] time) {
        List<List<Integer>> followers = new ArrayList<>();
        for (int i = 0; i < n; i++) followers.add(new ArrayList<>());
        int[] inDegree = new int[n];
        for (int[] r : relations) {
            followers.get(r[0] - 1).add(r[1] - 1);
            inDegree[r[1] - 1]++;
        }
        // start[v] = latest finish among v's prerequisites seen so far (earliest start of v)
        int[] start = new int[n];
        Deque<Integer> ready = new ArrayDeque<>();
        for (int v = 0; v < n; v++) if (inDegree[v] == 0) ready.add(v);
        int months = 0;
        while (!ready.isEmpty()) {
            int course = ready.poll();
            int finish = start[course] + time[course];
            months = Math.max(months, finish);
            for (int next : followers.get(course)) {
                start[next] = Math.max(start[next], finish);   // next must wait for the slowest prerequisite
                if (--inDegree[next] == 0) ready.add(next);
            }
        }
        return months;
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
