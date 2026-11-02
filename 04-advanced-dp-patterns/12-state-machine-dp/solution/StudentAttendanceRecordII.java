/**
 * Student Attendance Record II (LeetCode 552) — state machine with 6 states
 *
 * Idea: a record of length n over {A (absent), L (late), P (present)} is rewardable if it has
 *       fewer than 2 A's in total and never 3 L's in a row. While writing the record left to
 *       right we only need to remember:
 *         absences  = A's so far (0 or 1; a second A is a dead end)
 *         lateRun   = how many L's the record currently ENDS with (0, 1 or 2; a third is a dead end)
 *       count[absences][lateRun] = number of valid records of the current length in that state.
 *       Transitions for the next day:
 *         P: (a, l) -> (a, 0)          always allowed, resets the late run
 *         A: (0, l) -> (1, 0)          only if no absence yet, resets the late run
 *         L: (a, l) -> (a, l + 1)      only if l < 2
 *       bruteForce enumerates all 3^n records for small n as a cross-check.
 *
 * Time complexity:  O(n) — n steps, 6 states, at most 3 transitions each.
 * Space complexity: O(1) — two 2 x 3 tables.
 *
 * Run: java StudentAttendanceRecordII.java
 */
public class StudentAttendanceRecordII {

    private static final long MOD = 1_000_000_007L;

    static int checkRecord(int n) {
        long[][] count = new long[2][3];
        count[0][0] = 1;                                  // empty record: no A, no trailing L
        for (int day = 0; day < n; day++) {
            long[][] next = new long[2][3];
            for (int absences = 0; absences <= 1; absences++) {
                for (int lateRun = 0; lateRun <= 2; lateRun++) {
                    long ways = count[absences][lateRun];
                    if (ways == 0) continue;
                    // P: present
                    next[absences][0] = (next[absences][0] + ways) % MOD;
                    // A: absent, allowed only before the first absence
                    if (absences == 0) next[1][0] = (next[1][0] + ways) % MOD;
                    // L: late, allowed only if it does not make 3 in a row
                    if (lateRun < 2) next[absences][lateRun + 1] = (next[absences][lateRun + 1] + ways) % MOD;
                }
            }
            count = next;
        }
        long total = 0;
        for (long[] row : count) for (long ways : row) total += ways;
        return (int) (total % MOD);
    }

    static int bruteForce(int n) {
        return enumerate(new StringBuilder(), n);
    }

    private static int enumerate(StringBuilder record, int n) {
        if (record.length() == n) {
            String s = record.toString();
            int absences = 0;
            for (int k = 0; k < s.length(); k++) if (s.charAt(k) == 'A') absences++;
            return (absences < 2 && !s.contains("LLL")) ? 1 : 0;
        }
        int total = 0;
        for (char c : new char[]{'A', 'L', 'P'}) {
            record.append(c);
            total += enumerate(record, n);
            record.deleteCharAt(record.length() - 1);
        }
        return total;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("n = 1 (A, L, P)",  checkRecord(1), 3);
        check("n = 2 (all but AA)", checkRecord(2), 8);
        check("n = 10101",       checkRecord(10101), 183236316);
        for (int n = 1; n <= 9; n++) check("n = " + n + " vs brute", checkRecord(n), bruteForce(n));
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
