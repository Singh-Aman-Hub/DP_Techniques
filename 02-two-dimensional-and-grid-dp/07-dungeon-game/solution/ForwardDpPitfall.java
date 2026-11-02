/**
 * Dungeon Game (LeetCode 174) — Why forward DP fails (demonstration)
 *
 * Idea: a forward DP walks from the knight's start toward the princess and keeps
 * ONE summary per cell. But a path arriving at a cell is described by TWO numbers:
 *   - needSoFar: the starting health this path has required up to now, and
 *   - sumSoFar:  the net health change collected so far.
 * Neither number alone decides which incoming path is better for the future.
 * This program runs two forward rules and shows each one is wrong on some grid:
 *   rule 1 keeps the path with the smaller needSoFar,
 *   rule 2 keeps the path with the larger sumSoFar.
 * The correct answers come from the reverse DP (see Tabulation.java).
 *
 * Time complexity:  O(m * n) per rule — one pass over the grid.
 * Space complexity: O(m * n) — two small tables of path summaries.
 *
 * Run: java ForwardDpPitfall.java
 */
public class ForwardDpPitfall {

    // Forward DP that keeps a single (needSoFar, sumSoFar) summary per cell.
    // preferSmallNeed = true  -> rule 1;  false -> rule 2.
    static int forwardGuess(int[][] dungeon, boolean preferSmallNeed) {
        int rows = dungeon.length, cols = dungeon[0].length;
        int[][] needSoFar = new int[rows][cols]; // starting health required by the kept path to survive up to here
        int[][] sumSoFar = new int[rows][cols];  // net health change of the kept path, this cell included

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int prevNeed, prevSum;
                if (row == 0 && col == 0) {
                    prevNeed = 1;
                    prevSum = 0;
                } else if (row == 0) {
                    prevNeed = needSoFar[row][col - 1];
                    prevSum = sumSoFar[row][col - 1];
                } else if (col == 0) {
                    prevNeed = needSoFar[row - 1][col];
                    prevSum = sumSoFar[row - 1][col];
                } else {
                    // Two incoming paths: keep ONE according to the rule (this is the flaw).
                    int upNeed = needSoFar[row - 1][col], upSum = sumSoFar[row - 1][col];
                    int leftNeed = needSoFar[row][col - 1], leftSum = sumSoFar[row][col - 1];
                    boolean keepUp;
                    if (preferSmallNeed) {
                        keepUp = upNeed < leftNeed || (upNeed == leftNeed && upSum >= leftSum);
                    } else {
                        keepUp = upSum > leftSum || (upSum == leftSum && upNeed <= leftNeed);
                    }
                    prevNeed = keepUp ? upNeed : leftNeed;
                    prevSum = keepUp ? upSum : leftSum;
                }
                int sum = prevSum + dungeon[row][col];
                sumSoFar[row][col] = sum;
                // Starting health h survives this room only if h + sum >= 1, i.e. h >= 1 - sum.
                needSoFar[row][col] = Math.max(prevNeed, 1 - sum);
            }
        }
        return needSoFar[rows - 1][cols - 1];
    }

    // Correct reverse DP, for reference (same as Tabulation.java without padding tricks).
    static int correct(int[][] dungeon) {
        int rows = dungeon.length, cols = dungeon[0].length;
        int[][] need = new int[rows][cols];
        for (int row = rows - 1; row >= 0; row--) {
            for (int col = cols - 1; col >= 0; col--) {
                int after;
                if (row == rows - 1 && col == cols - 1) after = 1;
                else if (row == rows - 1) after = need[row][col + 1];
                else if (col == cols - 1) after = need[row + 1][col];
                else after = Math.min(need[row + 1][col], need[row][col + 1]);
                need[row][col] = Math.max(1, after - dungeon[row][col]);
            }
        }
        return need[0][0];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        int[][] gridA = {{0, -2, 7}, {0, 0, 0}, {-100, -100, -6}};
        int[][] gridB = {{0, -2, 7}, {0, 0, 0}, {-100, -100, 0}};
        int[][] example = {{-2, -3, 3}, {-5, -10, 1}, {10, 30, -5}};

        check("correct answer, grid A", correct(gridA), 3);
        check("correct answer, grid B", correct(gridB), 1);
        check("correct answer, example", correct(example), 7);

        // Rule 1 (smallest need so far) is fooled by grid A: it throws away the +7 path.
        check("rule 1 on grid A gives the WRONG value 7", forwardGuess(gridA, true), 7);
        check("rule 1 on grid B happens to be right", forwardGuess(gridB, true), 1);

        // Rule 2 (largest sum so far) is fooled by grid B: it keeps the costly -2/+7 path.
        check("rule 2 on grid A happens to be right", forwardGuess(gridA, false), 3);
        check("rule 2 on grid B gives the WRONG value 3", forwardGuess(gridB, false), 3);
    }

    private static void check(String label, int actual, int expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
