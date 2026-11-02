/**
 * LeetCode 1178 — Number of Valid Words for Each Puzzle (submask enumeration)
 *
 * Idea: a word only matters through its SET of letters, a 26-bit mask. Count how
 *       many words have each mask. For a puzzle, a valid word's mask is
 *       (first letter) + (any submask of the other 6 letters). So enumerate the
 *       2^6 = 64 submasks of the other letters, add the first-letter bit, and
 *       sum the counts. This is "sum over subsets", asked for one mask at a time.
 *
 * Time complexity:  O(W * L + P * 64) — build every word mask once (L = word length),
 *                   then each puzzle looks up exactly 64 masks in a hash map.
 * Space complexity: O(W) — the hash map holds at most one entry per distinct word mask.
 *
 * Run: java ValidWordsForPuzzle.java
 */
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ValidWordsForPuzzle {

    /** Bit c - 'a' is set when letter c appears in text. */
    static int letterMask(String text) {
        int mask = 0;
        for (int i = 0; i < text.length(); i++) {
            mask |= 1 << (text.charAt(i) - 'a');   // switch on the bit of this letter
        }
        return mask;
    }

    static int[] countValidWords(String[] words, String[] puzzles) {
        // wordsWithMask.get(m) = how many words have exactly the letter set m
        Map<Integer, Integer> wordsWithMask = new HashMap<>();
        for (String word : words) {
            int mask = letterMask(word);
            // A word with more than 7 distinct letters can never fit in a 7-letter puzzle.
            if (Integer.bitCount(mask) <= 7) wordsWithMask.merge(mask, 1, Integer::sum);
        }

        int[] answer = new int[puzzles.length];
        for (int p = 0; p < puzzles.length; p++) {
            String puzzle = puzzles[p];
            int firstBit = 1 << (puzzle.charAt(0) - 'a');
            // the 6 other letters; the first letter is forced, so it is kept out of the free choice
            int otherLetters = letterMask(puzzle.substring(1)) & ~firstBit;
            int total = 0;
            // Walk every submask of otherLetters, from largest to 0 (see SubmaskEnumeration.java).
            for (int sub = otherLetters; ; sub = (sub - 1) & otherLetters) {
                total += wordsWithMask.getOrDefault(sub | firstBit, 0);
                if (sub == 0) break;
            }
            answer[p] = total;
        }
        return answer;
    }

    // Brute force reference: test every word against every puzzle directly.
    private static int[] bruteForce(String[] words, String[] puzzles) {
        int[] answer = new int[puzzles.length];
        for (int p = 0; p < puzzles.length; p++) {
            int puzzleMask = letterMask(puzzles[p]);
            int firstBit = 1 << (puzzles[p].charAt(0) - 'a');
            for (String word : words) {
                int wordMask = letterMask(word);
                // valid = contains the first letter AND uses no letter outside the puzzle
                if ((wordMask & firstBit) != 0 && (wordMask & puzzleMask) == wordMask) answer[p]++;
            }
        }
        return answer;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        String[] words1 = {"ab", "ba", "abc", "cab", "d", "aaa"};
        String[] puzzles1 = {"abcdefg", "bacdefg", "gabcdef", "dabcefg"};
        check("walkthrough example", countValidWords(words1, puzzles1), new int[]{5, 4, 0, 1});

        String[] words2 = {"apple", "pale", "leap", "sale"};
        String[] puzzles2 = {"aelpxyz", "pselaxy", "saelpxy"};
        check("repeated letters in words", countValidWords(words2, puzzles2), new int[]{3, 3, 1});

        // Edge case: no word shares a letter with the puzzle.
        check("no valid words", countValidWords(words1, new String[]{"zyxwvut"}), new int[]{0});

        // Randomized cross-check over a small alphabet so that matches actually happen.
        Random random = new Random(1178);
        boolean allMatch = true;
        for (int trial = 0; trial < 300; trial++) {
            String[] words = new String[1 + random.nextInt(15)];
            for (int w = 0; w < words.length; w++) {
                StringBuilder sb = new StringBuilder();
                int len = 1 + random.nextInt(6);
                for (int c = 0; c < len; c++) sb.append((char) ('a' + random.nextInt(10)));
                words[w] = sb.toString();
            }
            String[] puzzles = new String[1 + random.nextInt(5)];
            for (int q = 0; q < puzzles.length; q++) {
                // 7 distinct letters from a..j: shuffle the 10 letters and keep the first 7
                char[] letters = "abcdefghij".toCharArray();
                for (int i = letters.length - 1; i > 0; i--) {
                    int j = random.nextInt(i + 1);
                    char t = letters[i]; letters[i] = letters[j]; letters[j] = t;
                }
                puzzles[q] = new String(letters, 0, 7);
            }
            if (!Arrays.equals(countValidWords(words, puzzles), bruteForce(words, puzzles))) allMatch = false;
        }
        System.out.println((allMatch ? "PASS" : "FAIL") + "  300 random cases agree with brute force");
        if (!allMatch) System.exit(1);
    }

    private static void check(String label, int[] actual, int[] expected) {
        boolean ok = Arrays.equals(actual, expected);
        System.out.println((ok ? "PASS" : "FAIL") + "  " + label + " -> " + Arrays.toString(actual)
                + " (expected " + Arrays.toString(expected) + ")");
        if (!ok) System.exit(1);
    }
}
