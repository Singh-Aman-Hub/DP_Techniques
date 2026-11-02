/**
 * Bitmask basics — representing subsets of {0, 1, ..., n-1} as integers
 *
 * Idea: bit i of an int is 1 when item i is in the subset. Every subset of n items
 *       is then a number from 0 to 2^n - 1, which can index an array directly.
 *       Each helper below is one operation, written out with an explanation.
 *
 * Time complexity:  O(1) for set/test/clear/toggle; O(n) for counting bits by hand;
 *                   O(number of submasks) for the submask loop (2^k for a mask with k set bits).
 * Space complexity: O(1) — apart from the list returned by submasks().
 *
 * Run: java BitOperationsDemo.java
 */
import java.util.ArrayList;
import java.util.List;

public class BitOperationsDemo {

    /** 1 << i is the number with ONLY bit i set (e.g. 1 << 2 = 0b100 = 4). */
    static int singleBit(int i) { return 1 << i; }

    /** Add item i: OR keeps every existing 1 and forces bit i to 1. */
    static int setBit(int mask, int i) { return mask | (1 << i); }

    /** Is item i present? AND with the single bit leaves 0 unless bit i is 1. */
    static boolean testBit(int mask, int i) { return (mask & (1 << i)) != 0; }

    /** Remove item i: ~(1 << i) has every bit 1 except bit i, so AND wipes only bit i. */
    static int clearBit(int mask, int i) { return mask & ~(1 << i); }

    /** Flip item i: XOR with 1 flips a bit, XOR with 0 leaves it. */
    static int toggleBit(int mask, int i) { return mask ^ (1 << i); }

    /** Number of items in the subset, counted bit by bit (Integer.bitCount does the same). */
    static int countBits(int mask) {
        int count = 0;
        while (mask != 0) {
            count += mask & 1;    // look at the lowest bit
            mask >>>= 1;          // shift the next bit into the lowest position
        }
        return count;
    }

    /** The full set of n items: n ones, i.e. 2^n - 1 (e.g. n = 3 -> 0b111 = 7). */
    static int fullMask(int n) { return (1 << n) - 1; }

    /**
     * Every submask of mask, from mask itself down to 0.
     * (sub - 1) & mask jumps to the next smaller number that uses only bits of mask:
     * subtracting 1 turns the lowest 1 into 0 and the zeros below it into 1s,
     * and AND-ing with mask throws away any 1 that is not allowed.
     */
    static List<Integer> submasks(int mask) {
        List<Integer> result = new ArrayList<>();
        for (int sub = mask; ; sub = (sub - 1) & mask) {
            result.add(sub);
            if (sub == 0) break;   // 0 is the last submask; without the break we would loop forever
        }
        return result;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("singleBit(2) = 0b100",          singleBit(2), 4);
        check("setBit(0b0101, 1) = 0b0111",    setBit(0b0101, 1), 0b0111);
        check("setBit already set is a no-op", setBit(0b0101, 0), 0b0101);
        check("testBit(0b0101, 2)",            testBit(0b0101, 2) ? 1 : 0, 1);
        check("testBit(0b0101, 1)",            testBit(0b0101, 1) ? 1 : 0, 0);
        check("clearBit(0b0111, 0) = 0b0110",  clearBit(0b0111, 0), 0b0110);
        check("toggleBit(0b0110, 3) = 0b1110", toggleBit(0b0110, 3), 0b1110);
        check("countBits(0b1011)",             countBits(0b1011), 3);
        check("countBits matches bitCount",    countBits(123456), Integer.bitCount(123456));
        check("fullMask(3) = 0b111",           fullMask(3), 7);
        check("submasks of 0b1011 count",      submasks(0b1011).size(), 8);
        check("submasks of 0b101 = [5,4,1,0]", submasks(0b101).toString().equals("[5, 4, 1, 0]") ? 1 : 0, 1);
        check("submasks of 0 = [0]",           submasks(0).size(), 1);
    }

    private static void check(String label, int actual, int expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
