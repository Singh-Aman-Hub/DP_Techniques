/**
 * 1178. Number of Valid Words for Each Puzzle — Bitmask + submask enumeration, cross-checked
 *
 * Idea: Only the SET of letters in a word matters, so turn each word into a 26-bit mask
 *       (bit c set when letter c appears) and count how many words share each mask.
 *       A word is valid for a puzzle when its mask is a SUBSET of the puzzle's mask and contains
 *       the puzzle's first letter. A puzzle has exactly 7 distinct letters, so its subsets that
 *       contain the first letter are: firstLetterBit | (any subset of the other 6 letters)
 *       = only 2^6 = 64 masks. Enumerate them with sub = (sub - 1) & rest and add the counts.
 *       The slow version checks every (word, puzzle) pair directly.
 *
 * Time complexity:  O(totalWordLength + P * 64) — build the counts once, then 64 lookups per puzzle.
 *                   The slow version is O(W * P) mask checks.
 * Space complexity: O(W) — the hash map holds at most one entry per distinct word mask.
 *
 * Run: java Optimized.java
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Optimized {

    static List<Integer> findNumOfValidWordsFast(String[] words, String[] puzzles) {
        // wordsWithMask.get(m) = how many words use exactly the letter set m
        Map<Integer, Integer> wordsWithMask = new HashMap<>();
        for (String word : words) {
            int mask = maskOf(word);
            if (Integer.bitCount(mask) > 7) continue;           // can never fit inside a 7-letter puzzle
            wordsWithMask.merge(mask, 1, Integer::sum);
        }
        List<Integer> answer = new ArrayList<>();
        for (String puzzle : puzzles) {
            int firstBit = 1 << (puzzle.charAt(0) - 'a');
            int rest = maskOf(puzzle) & ~firstBit;              // the other 6 letters
            int valid = 0;
            // Walk every subset of `rest`, from rest itself down to the empty set.
            for (int sub = rest; ; sub = (sub - 1) & rest) {
                valid += wordsWithMask.getOrDefault(sub | firstBit, 0);
                if (sub == 0) break;                            // the empty subset was just handled
            }
            answer.add(valid);
        }
        return answer;
    }

    static List<Integer> findNumOfValidWordsSlow(String[] words, String[] puzzles) {
        int[] wordMasks = new int[words.length];
        for (int w = 0; w < words.length; w++) wordMasks[w] = maskOf(words[w]);
        List<Integer> answer = new ArrayList<>();
        for (String puzzle : puzzles) {
            int puzzleMask = maskOf(puzzle), firstBit = 1 << (puzzle.charAt(0) - 'a');
            int valid = 0;
            for (int wordMask : wordMasks) {
                boolean subset = (wordMask & ~puzzleMask) == 0;  // no letter outside the puzzle
                if (subset && (wordMask & firstBit) != 0) valid++;
            }
            answer.add(valid);
        }
        return answer;
    }

    private static int maskOf(String text) {
        int mask = 0;
        for (int i = 0; i < text.length(); i++) mask |= 1 << (text.charAt(i) - 'a');
        return mask;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        String[] words1 = {"aaaa", "asas", "able", "ability", "actt", "actor", "access"};
        String[] puzzles1 = {"aboveyz", "abrodyz", "abslute", "absoryz", "actresz", "gaswxyz"};
        List<Integer> expected1 = Arrays.asList(1, 1, 3, 2, 4, 0);
        check("example 1 fast", findNumOfValidWordsFast(words1, puzzles1), expected1);
        check("example 1 slow", findNumOfValidWordsSlow(words1, puzzles1), expected1);

        String[] words2 = {"apple", "pleas", "please"};
        String[] puzzles2 = {"aelwxyz", "aelpxyz", "aelpsxy", "saelpxy", "xaelpsy"};
        List<Integer> expected2 = Arrays.asList(0, 1, 3, 2, 0);
        check("example 2 fast", findNumOfValidWordsFast(words2, puzzles2), expected2);
        check("example 2 slow", findNumOfValidWordsSlow(words2, puzzles2), expected2);

        String[] words3 = {"a"};
        String[] puzzles3 = {"abcdefg", "bacdefg"};
        List<Integer> expected3 = Arrays.asList(1, 0); // edge: first letter must be in the word
        check("edge: single word fast", findNumOfValidWordsFast(words3, puzzles3), expected3);
        check("edge: single word slow", findNumOfValidWordsSlow(words3, puzzles3), expected3);

        Random random = new Random(1178);
        for (int trial = 0; trial < 300; trial++) {
            String[] words = new String[1 + random.nextInt(40)];
            for (int w = 0; w < words.length; w++) words[w] = randomWord(random, 1 + random.nextInt(6), 10);
            String[] puzzles = new String[1 + random.nextInt(10)];
            for (int p = 0; p < puzzles.length; p++) puzzles[p] = randomPuzzle(random, 10);
            List<Integer> slow = findNumOfValidWordsSlow(words, puzzles), fast = findNumOfValidWordsFast(words, puzzles);
            if (!slow.equals(fast)) check("random trial " + trial, fast, slow);
        }
        System.out.println("PASS  submask enumeration agrees with pairwise check on 300 random inputs");
    }

    private static String randomWord(Random random, int length, int alphabet) {
        StringBuilder word = new StringBuilder();
        for (int i = 0; i < length; i++) word.append((char) ('a' + random.nextInt(alphabet)));
        return word.toString();
    }

    // 7 distinct letters from the first `alphabet` letters, in random order (as the problem promises)
    private static String randomPuzzle(Random random, int alphabet) {
        List<Character> letters = new ArrayList<>();
        for (int c = 0; c < alphabet; c++) letters.add((char) ('a' + c));
        java.util.Collections.shuffle(letters, random);
        StringBuilder puzzle = new StringBuilder();
        for (int i = 0; i < 7; i++) puzzle.append(letters.get(i));
        return puzzle.toString();
    }

    private static void check(String label, List<Integer> actual, List<Integer> expected) {
        String status = actual.equals(expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (!actual.equals(expected)) System.exit(1);
    }
}
