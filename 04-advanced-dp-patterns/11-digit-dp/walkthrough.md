# Digit DP — Walkthrough

Digit DP answers questions of the form **"how many integers between L and R have property P?"**, where R can be as large as 10¹⁸, or even a number with 10,000 digits. Instead of visiting the numbers one by one, we **build them digit by digit** and count whole groups of numbers at once.

The lesson first builds the three ideas every digit DP uses: building digit by digit, the `tight` flag, and the `started` flag. It then applies them to the main problem, [AtCoder DP S — Digit Sum](https://atcoder.jp/contests/dp/tasks/dp_s), through every stage, and finishes with four applications: [LeetCode 233](https://leetcode.com/problems/number-of-digit-one/), [LeetCode 2376](https://leetcode.com/problems/count-special-integers/), [LeetCode 902](https://leetcode.com/problems/numbers-at-most-n-given-digit-set/) and [CSES 2220](https://cses.fi/problemset/task/2220).

---

## 1. Intuition

Think of a car's odometer with 3 wheels, each showing a digit 0–9. Every number from `000` to `999` is one setting of the wheels. Suppose someone asks: "How many settings up to `235` have a digit sum divisible by 4?"

You could roll the odometer 236 times and check each setting. Or you could reason wheel by wheel:

- "If the first wheel shows 0 or 1, the other two wheels can show **anything** (00–99), because the number is certainly below 235."
- "If the first wheel shows 2, the second wheel can only go up to 3. Otherwise we would pass 235."

Once a wheel is strictly below the limit's digit, all the wheels to its right are **free**. Free wheels behave the same for every prefix that has the same "relevant summary" (here: the digit sum so far, modulo 4). That shared behaviour is the overlapping subproblem, and it is what makes digit DP fast.

---

## 2. Why we cannot just loop

A computer does roughly 10⁸–10⁹ simple steps per second.

| Upper limit R | Loop over every number | Digit DP (≈ digits × states × 10) |
|---|---|---|
| 10⁶ | 0.01 s | instant |
| 10⁹ | a few seconds | instant |
| 10¹⁸ | about 30 years | instant |
| a 10,000-digit number (AtCoder DP S) | longer than the age of the universe | about 10⁷ steps |

The loop's cost grows with the **value** of R. Digit DP's cost grows with the **number of digits** of R, which is about log₁₀ R. That exponential gap is the whole point.

---

## 3. Three building blocks

### 3.1 Building a number digit by digit

Write every number with the same number of digits as the limit, padding with **leading zeros**. With the limit `235`, the number 7 is written `007` and 42 is `042`. Now every candidate is a string of exactly 3 digits, and we choose its digits left to right: position 0 (hundreds), position 1 (tens), position 2 (ones).

Choosing digits left to right is a sequence of decisions, just like take/skip in knapsack. A DP state describes "where we are" after some of the decisions.

### 3.2 The `tight` flag — explained carefully

We must never build a number **larger** than the limit. Whether a digit is allowed depends on what we have written so far:

> **`tight` is true** when every digit written so far is **exactly equal** to the limit's digit in the same position. Then the next digit may not exceed the limit's next digit.
>
> **`tight` is false** when some earlier digit was **strictly smaller** than the limit's digit. The number is already guaranteed to be smaller than the limit, whatever comes next, so the next digit can be anything from 0 to 9.

Why does a smaller earlier digit free everything after it? Numbers with the same length compare like words in a dictionary: the first position where they differ decides. If our number has a 1 where the limit has a 2, our number is smaller, and no later digit can change that. `199 < 235` even though `9 > 3` and `9 > 5`.

**Worked example: limit N = 235.**

At the start nothing is written, and the empty prefix equals the limit's empty prefix, so we begin **tight**.

```
position:          0 (hundreds)      1 (tens)        2 (ones)
limit digit:            2                3               5
```

**Position 0, tight = true.** Allowed digits: 0 … 2 (the limit's digit).

| Digit | Prefix | Compared with "2" | New tight |
|---|---|---|---|
| 0 | `0__` | smaller | false: the rest is free, `000`–`099` |
| 1 | `1__` | smaller | false: the rest is free, `100`–`199` |
| 2 | `2__` | equal | **true**: still on the edge |

**Position 1, after prefix `2`, tight = true.** Allowed digits: 0 … 3.

| Digit | Prefix | Compared with "23" | New tight |
|---|---|---|---|
| 0, 1, 2 | `20_`, `21_`, `22_` | smaller | false: last digit free |
| 3 | `23_` | equal | **true** |

**Position 1, after prefix `0` or `1`, tight = false.** Allowed digits: 0 … 9, and the result stays false.

**Position 2, after prefix `23`, tight = true.** Allowed digits: 0 … 5. Digits 0–4 make it false. Digit 5 keeps it true, and that string is `235` itself.

Count everything, to check that no number is missed or double-counted:

```
prefix 0 or 1 (loose)     : 2 x 10 x 10 = 200 numbers  (000-199)
prefix 20, 21, 22 (loose) : 3 x 10      =  30 numbers  (200-229)
prefix 23, last 0..5      : 6           =   6 numbers  (230-235)
                                         -----
                                          236 = all of 000..235  ✓
```

The rule fits in two lines of code, which appear in every digit DP:

```java
int limit = tight ? digits[pos] : 9;
boolean nextTight = tight && (digit == limit);
```

Note that `nextTight` uses **AND**. Once tight becomes false, it can **never** become true again: a number that is already smaller stays smaller.

What if we forgot `tight` and always allowed 0–9? We would count every 3-digit string, `000`–`999`: 1000 numbers instead of 236.

### 3.3 The `started` flag — leading zeros

Padding with leading zeros is convenient, but sometimes it lies. In the string `007`:

- for **digit sum**, the zeros add 0, so they are harmless;
- for **"how many 1-digits"**, a 0 is not a 1, so they are harmless;
- for **"all digits distinct"** (LeetCode 2376), the two zeros would look like a repeated 0, and 7 would be wrongly rejected;
- for **"no two adjacent digits equal"** (CSES 2220), `00` would look like two equal neighbours;
- for **"only digits from a set"** (LeetCode 902), 0 is usually not in the set, so the padding zeros would make every short number invalid.

The fix is one more boolean:

> **`started`** is true once we have placed a **non-zero** digit. Before that, a 0 means "no digit here yet" and must not update the rest of the state.

```java
if (!started && digit == 0) {
    // still padding: the number has not begun; state stays as it was
} else {
    // a real digit: apply the problem's rule, and started = true
}
```

At the end, a string that never started is the number 0. Count it or not, depending on the problem.

### 3.4 Ranges: count(R) − count(L − 1)

Digit DP naturally answers "how many in `[0, X]`?". Call that `count(X)`. For a range `[L, R]`:

```
how many in [L, R]  =  count(R) − count(L − 1)
```

Everything up to R, minus everything strictly below L. Define `count(−1) = 0` so that `L = 0` works.

Example (CSES 2220, numbers without equal adjacent digits): `count(321) = 273` and `count(122) = 102`, so `[123, 321]` contains `273 − 102 = 171`.

---

## 4. Problem statement in our own words

Original: [AtCoder DP S — Digit Sum](https://atcoder.jp/contests/dp/tasks/dp_s)

Given `K` (up to 10,000 digits, so it arrives as a string) and `D` (≤ 100), count the integers from 1 to K whose digit sum is a multiple of D. Print the count modulo 10⁹ + 7.

---

## 5. Examples

**Running example:** `K = 30`, `D = 4` → **6**. The numbers are 4, 8, 13, 17, 22, 26 (digit sums 4, 8, 4, 8, 4, 8).

**Edge cases:** `K = 1, D = 2` → 0 (the only number, 1, has an odd digit sum). `K = 1000000009, D = 1` → every number counts: 1000000009 mod (10⁹+7) = **2**.

**Large:** `K = 98765432109876543210, D = 58` → **635270834** (official sample; the solution files check it).

---

## 6. Deriving the solution — the 7 questions

### Q1. What is changing?
The **position** we are filling, the **digit sum so far**, and whether we are still **tight** against K.

### Q2. What does the state represent?
We do not need the exact digit sum, only its remainder modulo D, because only divisibility matters at the end. And we do not need the digits themselves.

> `ways(pos, sumMod, tight)` = the number of ways to choose digits for positions `pos … L−1` so that the **whole** number's digit sum is divisible by D, given that the digits already chosen sum to `sumMod` (mod D) and `tight` says whether they equal K's first `pos` digits.

`started` is not needed: leading zeros add 0 to the sum, so `007` and `7` behave identically.

### Q3. What are the choices?
The digit at position `pos`: any `digit` from 0 to `limit`, where `limit = K[pos]` if tight, else 9.

### Q4. What is the recurrence?
```
ways(pos, sumMod, tight) = sum over digit = 0 .. limit of
                           ways(pos + 1, (sumMod + digit) % D, tight && digit == limit)
```
Each digit choice leads to a different set of numbers, so the counts **add**.

### Q5. What are the base cases?
`pos == L` (all digits chosen): the number is complete. Return 1 if `sumMod == 0`, else 0.

The DP counts `000…0`, which is the number 0 (digit sum 0, divisible by everything). The problem counts from 1, so **subtract 1** at the end.

### Q6. What is the order of computation?
`ways(pos, …)` depends on `ways(pos + 1, …)`. Top-down recursion handles this automatically. Bottom-up, we can go either way. The tabulation below runs **forward** (position 0 to L), counting how many *prefixes* reach each state, which is the more natural direction for digit DP.

### Q7. Can the state space be optimized?
Position `pos + 1` reads only position `pos`. Keep two rows of size `2 × D`. And `tight = true` describes exactly **one** prefix at every position (K's own prefix), so it could even be handled outside the table. The two-row version is clean enough.

---

## 7. Brute force

```java
for (long x = 1; x <= k; x++)
    if (digitSum(x) % d == 0) count++;
```

On `K = 30` it checks 30 numbers and finds 6. It is correct and useful as a reference, but K has up to 10,000 digits. Even `K = 10¹⁸` would take decades (Section 2). Notice also that the brute force repeats work: `13` and `113` and `213` all end with the same suffix and are judged separately.

---

## 8. Memoization (top-down)

```java
private static long ways(int pos, int sumMod, int tight) {
    if (pos == digits.length) return sumMod == 0 ? 1 : 0;
    if (memo[pos][sumMod][tight] != -1) return memo[pos][sumMod][tight];
    int limit = (tight == 1) ? digits[pos] : 9;
    long total = 0;
    for (int digit = 0; digit <= limit; digit++) {
        int nextTight = (tight == 1 && digit == limit) ? 1 : 0;
        total += ways(pos + 1, (sumMod + digit) % divisor, nextTight);
    }
    return memo[pos][sumMod][tight] = total % MOD;
}
// answer = ways(0, 0, 1) - 1
```

Recursion tree for `K = 30`, `D = 4` (T = tight, L = loose):

```
ways(0, 0, T)                      limit = 3
├── digit 0 -> ways(1, 0, L)       limit 9: digits 0,4,8 give sum%4 = 0  -> 3   (00, 04, 08)
├── digit 1 -> ways(1, 1, L)       digits 3,7                            -> 2   (13, 17)
├── digit 2 -> ways(1, 2, L)       digits 2,6                            -> 2   (22, 26)
└── digit 3 -> ways(1, 3, T)       limit 0: only digit 0, sum 3          -> 0   (30)
                                                              total = 7
answer = 7 - 1 (remove 00) = 6
```

With only two digits, no state repeats. Repeats appear as soon as K is longer. With `K = 235`, the state `ways(2, 0, L)` ("one digit left, sum so far ≡ 0, free") is reached by every loose two-digit prefix whose digit sum is a multiple of 4: `00`, `04`, `08`, `13`, `17`, and `22` (which left the tight branch at position 1). All six share one cached answer. In general, the memo has only `L × D × 2` cells, but they summarise up to 10^L numbers.

---

## 9. Tabulation (bottom-up, forward)

Instead of "how many ways to finish", count "how many prefixes are in each situation":

> `count[pos][tight][sumMod]` = the number of ways to choose the first `pos` digits so that they sum to `sumMod` (mod D), and are tight or loose.

Start: `count[0][T][0] = 1` (the empty prefix). Every prefix in a state is extended by every allowed digit:

```java
for (int pos = 0; pos < length; pos++)
    for (tight in {L, T}) for (sumMod in 0..D-1) {
        long ways = count[pos][tight][sumMod];
        int limit = tight == T ? K[pos] : 9;
        for (int digit = 0; digit <= limit; digit++) {
            int nextTight = (tight == T && digit == K[pos]) ? T : L;
            count[pos + 1][nextTight][(sumMod + digit) % D] += ways;
        }
    }
answer = count[length][L][0] + count[length][T][0] - 1;
```

Filling the table for `K = 30`, `D = 4`:

**pos = 0** (nothing chosen yet):

| | mod 0 | mod 1 | mod 2 | mod 3 |
|---|---|---|---|---|
| loose | 0 | 0 | 0 | 0 |
| tight | **1** | 0 | 0 | 0 |

**pos = 1** (first digit chosen; tight limit was 3). From tight mod 0: digits 0, 1, 2 become loose with mods 0, 1, 2; digit 3 stays tight with mod 3.

| | mod 0 | mod 1 | mod 2 | mod 3 |
|---|---|---|---|---|
| loose | 1 (`0`) | 1 (`1`) | 1 (`2`) | 0 |
| tight | 0 | 0 | 0 | 1 (`3`) |

**pos = 2** (second digit chosen). Each loose prefix takes all digits 0–9. Group the digits by remainder: `{0,4,8}` ≡ 0 (3 digits), `{1,5,9}` ≡ 1 (3), `{2,6}` ≡ 2 (2), `{3,7}` ≡ 3 (2). The tight prefix `3` may only take digit 0 (K's digit).

| target mod | from loose mod 0 | from loose mod 1 | from loose mod 2 | from tight mod 3 | total |
|---|---|---|---|---|---|
| loose 0 | +3 (digits ≡0) | +2 (≡3) | +2 (≡2) | – | **7** |
| loose 1 | +3 (≡1) | +3 (≡0) | +2 (≡3) | – | **8** |
| loose 2 | +2 (≡2) | +3 (≡1) | +3 (≡0) | – | **8** |
| loose 3 | +2 (≡3) | +2 (≡2) | +3 (≡1) | – | **7** |
| tight 3 | – | – | – | +1 (`30`) | **1** |

Check: 7 + 8 + 8 + 7 + 1 = 31 = all numbers 0 … 30 ✓.

Answer: `count[2][L][0] + count[2][T][0] − 1 = 7 + 0 − 1 =` **6** ✓.

---

## 10. Space optimization

Row `pos + 1` is built only from row `pos`. Keep `current` and `next`, each `2 × D`:

```java
long[][] current = new long[2][d];
current[TIGHT][0] = 1;
for (int pos = 0; pos < k.length(); pos++) {
    long[][] next = new long[2][d];
    ... same transitions, reading current, writing next ...
    current = next;
}
```

For AtCoder's limits (10,000 digits, D = 100) the full table has 2 million cells, while the two-row version has 400. `DigitSumTabulation.java` contains both and checks that they agree.

---

## 11. Complexity analysis

Let L be the number of digits of K.

| Approach | Time | Space | Why |
|---|---|---|---|
| Brute force | O(K · L) | O(1) | every number up to K, split into digits |
| Memoization | O(L · D · 2 · 10) | O(L · D · 2) | each of the `L·D·2` states is computed once and tries ≤ 10 digits |
| Tabulation | O(L · 2 · D · 10) | O(L · 2 · D) | same states, filled forward |
| Two rows | O(L · 2 · D · 10) | O(2 · D) | only the current and next rows |

For L = 10,000 and D = 100: 10,000 × 100 × 2 × 10 = 2 × 10⁷ steps. Fast.

The general pattern: **time = (number of positions) × (number of extra states) × (number of flag combinations) × 10.** Digit DP is fast whenever the "extra state" (here, the remainder) is small.

---

## 12. Why this approach works

- **Every number from 0 to K appears exactly once** as a padded digit string reached by one path of choices, and the `tight` limit excludes exactly the strings greater than K (Section 3.2 counted 236 for K = 235).
- **The future depends only on the state.** Two prefixes with the same position, the same remainder and both loose have identical sets of valid completions: any digits, with the same remainder requirement. So they can share one count.
- **Counts add** because different digit choices produce different numbers. Nothing is counted twice.

---

## 13. Applications

Each application uses the same skeleton. Only the **extra state** and the **rule at each digit** change.

### 13.1 LeetCode 233 — Number of Digit One (sum a quantity, not count numbers)

[LeetCode 233](https://leetcode.com/problems/number-of-digit-one/): count the total number of digit 1s written in all integers from 0 to n. For n = 13: the 1s in 1, 10, 11 (two), 12, 13 → **6**.

The twist: we are not counting numbers that satisfy a property, we are **adding up** a quantity over all numbers. Carry the number of 1s written so far, and let each finished number report it:

```
ones(pos, onesSoFar, tight):
    if pos == L: return onesSoFar           // this number contains onesSoFar ones
    sum over digit 0..limit of ones(pos+1, onesSoFar + (digit == 1 ? 1 : 0), nextTight)
```

`started` is not needed: a padding 0 is never a 1. States: L × (L+1) × 2, each with 10 digits. For n ≤ 10⁹ that is about 2,000 steps.

Sanity check from the tests: n = 99 → 20 (ten 1s in the ones place: 1, 11, …, 91; ten in the tens place: 10–19), and n = 100 adds one more → 21.

### 13.2 LeetCode 2376 — Count Special Integers (a mask state + `started`)

[LeetCode 2376](https://leetcode.com/problems/count-special-integers/): count the integers in `[1, n]` whose digits are all distinct.

Extra state: which digits are already used, as a 10-bit mask (see [Bitmask DP](../09-bitmask-dp/)). Here `started` is essential. Without it, `007` would mark 0 as used at the first zero and reject the second zero, so 7 would not be counted.

```
special(pos, usedMask, tight, started):
    if pos == L: return started ? 1 : 0        // the never-started string is 0, not in [1, n]
    for digit in 0..limit:
        if !started and digit == 0:  special(pos+1, usedMask, nextTight, false)    // padding
        else if digit not in usedMask: special(pos+1, usedMask | 1<<digit, nextTight, true)
```

For n = 135 the answer is **110**. Check by hand: 1–9 (9 numbers) + 10–99 except 11, 22, …, 99 (90 − 9 = 81) + 100–135 with distinct digits (102, 103, …, 109: 8; 120, 123, …, 129 except 121, 122: 8; 130, 132, 134, 135: 4) = 9 + 81 + 20 = 110 ✓.

States: 10 positions × 1024 masks × 2 × 2, about 41,000, each trying 10 digits.

### 13.3 LeetCode 902 — Numbers At Most N Given Digit Set (restricted digits)

[LeetCode 902](https://leetcode.com/problems/numbers-at-most-n-given-digit-set/): you may write numbers using only digits from a given set (from 1–9, so no 0). How many positive integers ≤ n can you write?

Two changes from the skeleton:

- The loop runs over the **allowed digits** only, skipping any above the limit.
- Shorter numbers are handled with `started`. Before the number starts we may leave a position **blank**. A blank is smaller than n's leading digit, so after a blank, `tight` becomes false.

```
build(pos, tight, started):
    if pos == L: return started ? 1 : 0
    total = 0
    if !started: total += build(pos+1, tight = false, started = false)    // blank
    for d in allowed, d <= limit: total += build(pos+1, tight && d == limit, true)
```

`{1,3,5,7}`, n = 100 → **20**: the 4 one-digit numbers plus 4 × 4 = 16 two-digit numbers. No three-digit number made of these digits is ≤ 100.

The state is tiny: L × 2 × 2. This problem is a good reminder that digit DP does not always need an extra state.

### 13.4 CSES 2220 — Counting Numbers (a range, previous digit, `started`)

[CSES 2220](https://cses.fi/problemset/task/2220): count the integers in `[a, b]` (up to 10¹⁸) where no two adjacent digits are equal.

- Range → `count(b) − count(a − 1)`, with `count(−1) = 0`.
- Extra state: the **previous digit** (0–9, or 10 meaning "none yet").
- `started` is essential: in `0012`, the padding zeros are not neighbours of anything.

```
valid(pos, previous, tight, started):
    if pos == L: return 1              // includes the number 0 (all padding), which is valid
    for digit in 0..limit:
        if !started and digit == 0: valid(pos+1, none, nextTight, false)
        else if started and digit == previous: skip
        else: valid(pos+1, digit, nextTight, true)
```

Sample `[123, 321]`: `count(321) = 273`, `count(122) = 102`, answer **171**. The solution file also checks `[0, 10¹⁸ − 1]` against the closed form: every d-digit valid number has 9 choices for the first digit (1–9) and 9 for each later one (anything except the previous digit), so the answer is `1 + 9 + 9² + … + 9¹⁸`.

Use `long` everywhere: values reach 10¹⁸.

---

## 14. Common mistakes

1. **Making `tight` come back.** `nextTight = tight && digit == limit`, never `digit == limit` alone. Once smaller, always smaller.
2. **Using the wrong limit digit.** The limit at position `pos` is the limit number's digit at `pos`, counted from the **left** (most significant). Convert the number to a string or a digit array first.
3. **Forgetting `started` when zeros matter.** Ask: "would padding zeros change my extra state or break my rule?" Distinct digits, adjacent digits, allowed digit sets, "contains a zero" → yes. Digit sum, count of 1s → no.
4. **Counting 0 by accident.** Digit DP over `[0, X]` includes 0. Subtract it when the range starts at 1 (AtCoder DP S), or return `started` at the end (LeetCode 2376).
5. **Off-by-one in ranges.** It is `count(R) − count(L − 1)`, not `count(R) − count(L)`.
6. **Storing the full digit sum instead of `sum % D`.** It works but multiplies the state by up to 9 × L.
7. **Memoizing with a stale memo.** Each `count(X)` call has a different digit array, so `count(b)` and `count(a − 1)` each need a fresh memo. (Advanced trick: index loose states by the number of *remaining* digits; they then no longer depend on X and can be shared between calls. Not needed here.)
8. **Modulo with subtraction.** `(x − 1) % MOD` can be negative when x is 0 mod MOD; write `(x − 1 + MOD) % MOD`.

---

## 15. When to use this technique / how to recognize similar problems

- "Count the integers in `[L, R]` (or `≤ N`) such that…", with R far too large to loop over (10⁹, 10¹⁸, or given as a string).
- The property is decided **digit by digit**: digit sums, digit counts, which digits appear, adjacent digits, divisibility of the number (remainder of the number so far mod m).
- "Sum of f(x) over all x ≤ N", where f depends on the digits (LeetCode 233).

Choosing the extra state: ask **what you must remember about the prefix** to finish the job. Digit sum → `sum % D`. Distinct digits → used-digits mask. Adjacent rule → previous digit. Divisible by m → `number % m` (update with `(r * 10 + digit) % m`).

---

## 16. Summary and revision notes

- Build numbers left to right with padding zeros. A state = (position, small summary of the prefix, `tight`, maybe `started`).
- `limit = tight ? digit[pos] : 9`, `nextTight = tight && digit == limit`.
- `started` is false while only padding zeros have been written. Use it when zeros would break the rule or the extra state.
- Ranges: `count(R) − count(L − 1)`. Decide whether 0 is counted.
- Cost ≈ positions × extra states × flags × 10, independent of the value of R.

```
+---------------------------------------------------------------+
|  f(pos, extra, tight, started):                               |
|    if pos == L: return (does this finished number count?)     |
|    limit = tight ? digit[pos] : 9                             |
|    for d in 0..limit:                                         |
|      nt = tight && d == limit                                 |
|      if !started && d == 0: f(pos+1, extra, nt, false)        |
|      else if rule allows d: f(pos+1, update(extra, d), nt, true) |
|  answer on [L, R] = count(R) - count(L - 1)                   |
+---------------------------------------------------------------+
```

---

## 17. Exercises

**Exercise 1.** For the limit N = 4071, list the allowed digits at each position along the **tight** path, and say how many numbers in `0000 … 4071` start with the prefix `40`.

<details>
<summary>Answer</summary>

Tight path: position 0 allows 0–4; after `4`, position 1 allows 0–0 (only 0); after `40`, position 2 allows 0–7; after `407`, position 3 allows 0–1.
Numbers starting with `40`: prefixes `400`–`406` are loose (7 × 10 = 70 numbers: 4000–4069) plus `4070`, `4071` (2 numbers) = **72**.
</details>

**Exercise 2.** Which of these problems need the `started` flag? (a) count numbers ≤ N whose digit sum is 10; (b) count numbers ≤ N containing at least one digit 0; (c) count numbers ≤ N whose digits are non-decreasing.

<details>
<summary>Answer</summary>

(a) **No**: padding zeros add 0 to the sum.
(b) **Yes**: without it, padding zeros would make every short number "contain a 0" (7 = `007`).
(c) **No**: padding zeros come first, and 0 ≤ every digit, so they can never break a non-decreasing order. Treating padding as "previous digit = 0" is exactly right.

The test is always the same: could a padding zero change the answer or the extra state? If yes, add `started`.
</details>

**Exercise 3.** Fill `count[pos][tight][sumMod]` for AtCoder DP S with `K = 12`, `D = 3`, and give the answer.

<details>
<summary>Answer</summary>

- pos 0: tight mod 0 = 1.
- pos 1 (limit 1): digit 0 → loose mod 0 = 1; digit 1 → tight mod 1 = 1.
- pos 2: loose prefix `0` takes 0–9: mods 0,1,2 receive 4 (`0,3,6,9`), 3 (`1,4,7`), 3 (`2,5,8`). Tight prefix `1` takes 0–2 (limit 2): `10` (mod 1) loose, `11` (mod 2) loose, `12` (mod 0) tight.
- Final: loose mod 0 = 4, tight mod 0 = 1 → 5, minus the number 0 → **4** (the numbers 3, 6, 9, 12).
</details>

**Exercise 4.** Using `count(R) − count(L − 1)`, how many numbers in `[10, 20]` have no two equal adjacent digits? Compute `count(20)` and `count(9)`.

<details>
<summary>Answer</summary>

`count(9) = 10` (0–9, all single digits are valid). `count(20) = 10 + 10 = 20`: 10–20 contains 11 numbers, of which only 11 is invalid. Answer `20 − 10 =` **10**, matching the test in `CountingNumbers.java`.
</details>
