/**
 * Fibonacci Numbers modulo 1e9+7 (CSES 1722) — O(n) DP vs O(log n) matrix exponentiation
 *
 * Idea: the step (F(i), F(i-1)) -> (F(i+1), F(i)) is the same linear map every time:
 *     [F(i+1)]   [1 1] [F(i)  ]
 *     [F(i)  ] = [1 0] [F(i-1)]
 * So n steps = multiplying by M = [[1,1],[1,0]] n times = multiplying by M^n, and
 * M^n can be computed with about 2·log2(n) matrix multiplications by repeated squaring.
 * In fact M^n = [[F(n+1), F(n)], [F(n), F(n-1)]], so F(n) is the top-right entry.
 *
 * Time complexity:  O(n) for the loop (one addition per step);
 *                   O(2³ · log n) = O(log n) for the matrix version (log n squarings of a 2x2 matrix,
 *                   8 multiplications each).
 * Space complexity: O(1) for both (two variables / a few 2x2 matrices).
 *
 * Run: java FibonacciMatrix.java
 */
import java.util.Random;

public class FibonacciMatrix {

    private static final long MOD = 1_000_000_007L;

    /** Slow but obviously correct: walk the recurrence n times, keeping two variables. */
    static long fibLinear(long n) {
        long previous = 0, current = 1; // F(0), F(1)
        if (n == 0) return 0;
        for (long i = 2; i <= n; i++) {
            long next = (previous + current) % MOD;
            previous = current;
            current = next;
        }
        return current;
    }

    /** Product of two square matrices modulo MOD. */
    static long[][] multiply(long[][] a, long[][] b) {
        int size = a.length;
        long[][] product = new long[size][size];
        for (int i = 0; i < size; i++) {
            for (int k = 0; k < size; k++) {
                if (a[i][k] == 0) continue; // skipping zeros is a free speed-up for sparse matrices
                for (int j = 0; j < size; j++) {
                    // a[i][k], b[k][j] < MOD ~ 1e9, so their product < 1.0e18 fits in a long (max ~9.2e18).
                    // We reduce after every addition so the running sum never overflows either.
                    product[i][j] = (product[i][j] + a[i][k] * b[k][j]) % MOD;
                }
            }
        }
        return product;
    }

    /** base^exponent by repeated squaring: O(size³ · log exponent). */
    static long[][] power(long[][] base, long exponent) {
        int size = base.length;
        long[][] result = new long[size][size];
        for (int i = 0; i < size; i++) result[i][i] = 1; // identity matrix = "zero steps"
        long[][] square = base;                          // holds base^(2^bit) for the current bit
        while (exponent > 0) {
            if ((exponent & 1) == 1) result = multiply(result, square); // this bit of the exponent is 1
            square = multiply(square, square);                          // base^(2^bit) -> base^(2^(bit+1))
            exponent >>= 1;                                             // move to the next bit
        }
        return result;
    }

    static long fibMatrix(long n) {
        long[][] step = { {1, 1}, {1, 0} };
        return power(step, n)[0][1]; // M^n = [[F(n+1), F(n)], [F(n), F(n-1)]]
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("F(0) edge", fibMatrix(0), 0);
        check("F(1)", fibMatrix(1), 1);
        check("F(2)", fibMatrix(2), 1);
        check("F(10)", fibMatrix(10), 55);
        check("F(50) mod 1e9+7 (12586269025 mod 1e9+7)", fibMatrix(50), 12586269025L % MOD);
        check("F(1000) matrix == linear", fibMatrix(1000), fibLinear(1000));
        // F(10^18) mod 1e9+7, value computed independently with the fast-doubling formulas.
        check("F(10^18)", fibMatrix(1_000_000_000_000_000_000L), 209783453L);

        Random random = new Random(1722);
        boolean allAgree = true;
        for (int trial = 0; trial < 300 && allAgree; trial++) {
            long n = random.nextInt(5000);
            allAgree = fibMatrix(n) == fibLinear(n);
        }
        check("300 random n < 5000 agree", allAgree ? 1 : 0, 1);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
