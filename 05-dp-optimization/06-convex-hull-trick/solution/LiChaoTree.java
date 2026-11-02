/**
 * Frog 3 generalized (heights in ANY order) — Li Chao tree, O(N log H)
 *
 * Idea: the monotone-deque convex hull trick needs slopes inserted in sorted order and
 * sorted queries. If heights are not sorted, neither holds. A Li Chao tree is a segment tree
 * over the x-range [minX, maxX]; every node keeps ONE line, the one that is lowest at the
 * node's midpoint. Inserting a new line: compare it with the stored line at the midpoint,
 * keep the winner, and push the loser into the ONE half where it can still win (two lines
 * cross at most once). A query walks root-to-leaf and takes the minimum of the lines seen.
 *
 * Time complexity:  O(N log H) — each insert and each query visits one node per tree level,
 *                   and there are log2(maxX - minX + 1) levels.
 * Space complexity: O(H) — about 4*(maxX - minX + 1) node slots for the segment tree.
 *
 * Run: java LiChaoTree.java
 */
import java.util.Arrays;
import java.util.Random;

public class LiChaoTree {

    private static final long NO_LINE = Long.MAX_VALUE;

    private static long[] nodeSlope;
    private static long[] nodeIntercept;
    private static boolean[] hasLine;
    private static long minX, maxX;

    private static void init(long low, long high) {
        minX = low;
        maxX = high;
        int size = (int) (4 * (high - low + 1));
        nodeSlope = new long[size];
        nodeIntercept = new long[size];
        hasLine = new boolean[size];
    }

    private static void insert(long slope, long intercept) {
        insert(1, minX, maxX, slope, intercept);
    }

    private static void insert(int node, long left, long right, long slope, long intercept) {
        if (!hasLine[node]) {
            hasLine[node] = true;
            nodeSlope[node] = slope;
            nodeIntercept[node] = intercept;
            return;
        }
        long mid = (left + right) >> 1;
        boolean newWinsLeft = slope * left + intercept < nodeSlope[node] * left + nodeIntercept[node];
        boolean newWinsMid = slope * mid + intercept < nodeSlope[node] * mid + nodeIntercept[node];
        if (newWinsMid) { // keep the line that is lower at mid in this node; carry the other one down
            long tmpSlope = nodeSlope[node], tmpIntercept = nodeIntercept[node];
            nodeSlope[node] = slope;
            nodeIntercept[node] = intercept;
            slope = tmpSlope;
            intercept = tmpIntercept;
        }
        if (left == right) return;
        // the loser can only still win on the side where the winner changed at the left end
        if (newWinsLeft != newWinsMid) insert(2 * node, left, mid, slope, intercept);
        else insert(2 * node + 1, mid + 1, right, slope, intercept);
    }

    private static long query(long x) {
        int node = 1;
        long left = minX, right = maxX, best = NO_LINE;
        while (true) {
            if (!hasLine[node]) return best;
            best = Math.min(best, nodeSlope[node] * x + nodeIntercept[node]);
            if (left == right) return best;
            long mid = (left + right) >> 1;
            if (x <= mid) { node = 2 * node; right = mid; }
            else { node = 2 * node + 1; left = mid + 1; }
        }
    }

    /** Same recurrence as Frog 3, but heights may go up and down. */
    static long minFrogCostAnyOrder(long[] heights, long jumpCost) {
        int n = heights.length;
        long low = Long.MAX_VALUE, high = Long.MIN_VALUE;
        for (long h : heights) { low = Math.min(low, h); high = Math.max(high, h); }
        init(low, high);
        long[] dp = new long[n]; // dp[i] = cheapest total cost to stand on stone i
        insert(-2 * heights[0], dp[0] + heights[0] * heights[0]);
        for (int i = 1; i < n; i++) {
            long x = heights[i];
            dp[i] = query(x) + x * x + jumpCost;
            insert(-2 * x, dp[i] + x * x);
        }
        return dp[n - 1];
    }

    // ---------- private copy of the slow O(N^2) DP, used only for cross-checking ----------
    private static long slowAnyOrder(long[] heights, long jumpCost) {
        int n = heights.length;
        long[] dp = new long[n];
        for (int i = 1; i < n; i++) {
            dp[i] = Long.MAX_VALUE;
            for (int j = 0; j < i; j++) {
                long d = heights[i] - heights[j];
                dp[i] = Math.min(dp[i], dp[j] + d * d + jumpCost);
            }
        }
        return dp[n - 1];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("walkthrough example (sorted heights), C=5",
                minFrogCostAnyOrder(new long[]{1, 3, 4, 5, 10, 11, 12, 13}, 5), 62);
        // h=[5,1,4], C=2: dp1 = 16+2 = 18; dp2 = min(0+1+2, 18+9+2) = 3
        check("unsorted [5,1,4], C=2", minFrogCostAnyOrder(new long[]{5, 1, 4}, 2), 3);
        // h=[3,10,2,8], C=1: dp1=50, dp2=min(1+1, 50+64+1)=2, dp3=min(25+1, 50+4+1, 2+36+1)=26
        check("unsorted [3,10,2,8], C=1", minFrogCostAnyOrder(new long[]{3, 10, 2, 8}, 1), 26);
        check("edge: one stone", minFrogCostAnyOrder(new long[]{9}, 4), 0);

        Random random = new Random(12345);
        int trials = 500;
        for (int t = 0; t < trials; t++) {
            int n = 1 + random.nextInt(40);
            long[] heights = new long[n];
            for (int i = 0; i < n; i++) heights[i] = 1 + random.nextInt(100); // any order, repeats allowed
            long jumpCost = random.nextInt(100);
            long fast = minFrogCostAnyOrder(heights, jumpCost);
            long slow = slowAnyOrder(heights, jumpCost);
            if (fast != slow) {
                System.out.println("FAIL  random trial " + t + ": fast=" + fast + " slow=" + slow
                        + " heights=" + Arrays.toString(heights) + " C=" + jumpCost);
                System.exit(1);
            }
        }
        System.out.println("PASS  " + trials + " random unsorted inputs: Li Chao tree == slow DP");
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
