/**
 * 1125. Smallest Sufficient Team — Memoization (bitmask over skills)
 *
 * Idea: need(covered) = fewest extra people to cover all skills, given the skill mask already
 * covered. The lowest missing skill must be covered by someone, so only people having that
 * skill are tried. choice[covered] remembers the best person, so the team can be rebuilt.
 *
 * Time complexity:  O(2^s * p) — 2^s skill masks, each tries at most p people.
 * Space complexity: O(2^s) — memo and choice arrays (recursion depth at most s).
 *
 * Run: java Memoization.java
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Memoization {

    private static final int INF = 1_000_000;
    private static int[] skillMask;     // skillMask[p] = required skills person p has
    private static int allSkills;
    private static int[] memo;          // memo[covered] = min extra people; -1 = unknown
    private static int[] choice;        // choice[covered] = person picked at this state

    static int[] smallestSufficientTeam(String[] reqSkills, String[][] people) {
        Map<String, Integer> skillIndex = new HashMap<>();
        for (int i = 0; i < reqSkills.length; i++) skillIndex.put(reqSkills[i], i);
        skillMask = new int[people.length];
        for (int p = 0; p < people.length; p++)
            for (String s : people[p]) skillMask[p] |= 1 << skillIndex.get(s);
        allSkills = (1 << reqSkills.length) - 1;
        memo = new int[1 << reqSkills.length];
        choice = new int[1 << reqSkills.length];
        Arrays.fill(memo, -1);
        need(0);
        List<Integer> team = new ArrayList<>();
        for (int covered = 0; covered != allSkills; covered |= skillMask[choice[covered]]) {
            team.add(choice[covered]);    // follow the recorded best choices
        }
        int[] result = new int[team.size()];
        for (int i = 0; i < result.length; i++) result[i] = team.get(i);
        return result;
    }

    private static int need(int covered) {
        if (covered == allSkills) return 0;
        if (memo[covered] != -1) return memo[covered];
        int missing = Integer.numberOfTrailingZeros(~covered);   // lowest skill not yet covered
        int best = INF;
        for (int p = 0; p < skillMask.length; p++) {
            if ((skillMask[p] & (1 << missing)) == 0) continue;   // p can't cover that skill
            int candidate = 1 + need(covered | skillMask[p]);
            if (candidate < best) { best = candidate; choice[covered] = p; }
        }
        memo[covered] = best;
        return best;
    }

    // ---------- self-check ----------
    // Any smallest team is accepted, so we check: (1) the team covers every skill, (2) its size.
    public static void main(String[] args) {
        checkTeam("java/nodejs/reactjs",
                new String[]{"java", "nodejs", "reactjs"},
                new String[][]{{"java"}, {"nodejs"}, {"nodejs", "reactjs"}}, 2);
        checkTeam("six skills, six people",
                new String[]{"algorithms", "math", "java", "reactjs", "csharp", "aws"},
                new String[][]{{"algorithms", "math", "java"}, {"algorithms", "math", "reactjs"},
                        {"java", "csharp", "aws"}, {"reactjs", "csharp"}, {"csharp", "math"}, {"aws", "java"}}, 2);
        checkTeam("one skill, one person (edge)", new String[]{"go"}, new String[][]{{"go"}}, 1);
        checkTeam("everyone has one distinct skill", new String[]{"a", "b", "c"},
                new String[][]{{"a"}, {"b"}, {"c"}, {}}, 3);
        checkTeam("one person covers everything", new String[]{"a", "b", "c"},
                new String[][]{{"a"}, {"b"}, {"a", "b", "c"}}, 1);
    }

    private static void checkTeam(String label, String[] skills, String[][] people, int expectedSize) {
        int[] team = smallestSufficientTeam(skills, people);
        java.util.Set<String> covered = new java.util.HashSet<>();
        for (int p : team) covered.addAll(java.util.Arrays.asList(people[p]));
        boolean coversAll = covered.containsAll(java.util.Arrays.asList(skills));
        boolean ok = coversAll && team.length == expectedSize;
        System.out.println((ok ? "PASS" : "FAIL") + "  " + label + " -> team " + java.util.Arrays.toString(team)
                + " (expected size " + expectedSize + ", covers all = " + coversAll + ")");
        if (!ok) System.exit(1);
    }
}
