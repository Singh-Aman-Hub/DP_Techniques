/**
 * 1125. Smallest Sufficient Team — Tabulation (push over skill masks + parent pointers)
 *
 * Idea: teamSize[mask] = fewest people whose combined skills are exactly mask. From each
 * reachable mask, adding person p leads to mask | skillMask[p] (never smaller), so iterating
 * masks upward is a valid order. parentMask/lastPerson let us walk back and list the team.
 *
 * Time complexity:  O(2^s * p) — every mask tries every person.
 * Space complexity: O(2^s) — three ints per mask.
 *
 * Run: java Tabulation.java
 */
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Tabulation {

    static int[] smallestSufficientTeam(String[] reqSkills, String[][] people) {
        Map<String, Integer> skillIndex = new HashMap<>();
        for (int i = 0; i < reqSkills.length; i++) skillIndex.put(reqSkills[i], i);
        int[] skillMask = new int[people.length];
        for (int p = 0; p < people.length; p++)
            for (String s : people[p]) skillMask[p] |= 1 << skillIndex.get(s);

        int states = 1 << reqSkills.length;
        int[] teamSize = new int[states];     // teamSize[mask] = fewest people covering exactly mask
        int[] parentMask = new int[states];   // mask before the last person was added
        int[] lastPerson = new int[states];   // the person added to reach mask
        Arrays.fill(teamSize, Integer.MAX_VALUE);
        teamSize[0] = 0;
        for (int mask = 0; mask < states; mask++) {
            if (teamSize[mask] == Integer.MAX_VALUE) continue;
            for (int p = 0; p < people.length; p++) {
                int next = mask | skillMask[p];
                if (next == mask) continue;                       // p adds no new skill
                if (teamSize[mask] + 1 < teamSize[next]) {
                    teamSize[next] = teamSize[mask] + 1;
                    parentMask[next] = mask;
                    lastPerson[next] = p;
                }
            }
        }
        int[] team = new int[teamSize[states - 1]];
        int mask = states - 1;
        for (int i = 0; mask != 0; i++) {                         // walk the parent pointers back to 0
            team[i] = lastPerson[mask];
            mask = parentMask[mask];
        }
        return team;
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
