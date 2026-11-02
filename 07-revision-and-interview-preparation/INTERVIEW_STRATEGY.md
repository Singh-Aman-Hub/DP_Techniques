# Interview Strategy for DP Problems

> **Module:** 07 · Revision and Interview Preparation · **Use:** before mock interviews and real ones; practise the routine on every problem in Module 06

Interviewers rarely expect you to *know* the problem. They want to watch you **derive** a solution in a structured, explainable way. The routine below turns the [DP problem-solving framework](../00-foundations/16-the-dp-problem-solving-framework/) you learned in Module 00 into a 45-minute plan with words to say at each step.

---

## Contents

1. [The 45-minute plan](#1-the-45-minute-plan)
2. [Step by step: what to do and what to say](#2-step-by-step-what-to-do-and-what-to-say)
3. [Communicating the state and recurrence aloud](#3-communicating-the-state-and-recurrence-aloud)
4. [Testing your solution](#4-testing-your-solution)
5. [When you are stuck](#5-when-you-are-stuck)
6. [Scripted example: Decode Ways (Medium)](#6-scripted-example-decode-ways-medium)
7. [Follow-up questions to expect](#7-follow-up-questions-to-expect)
8. [Practice protocol](#8-practice-protocol)

---

## 1. The 45-minute plan

```
 0 ─────── 5 ──────── 10 ─────────── 20 ─────────────── 32 ────────── 38 ──────── 45 min
 │ CLARIFY │ EXAMPLES  │ BRUTE FORCE → │ MEMOIZATION       │ TEST         │ TABULATE /
 │         │ + brute   │ STATE + RECUR │ (code it)         │ (trace it)   │ OPTIMIZE / Q&A
```

| Phase | Minutes | Output you should have at the end |
|---|---|---|
| 1. Clarify | 0–5 | Input sizes, edge cases, return type and what "valid" means, written as notes |
| 2. Examples + brute force | 5–10 | One hand-worked example; the recursive idea in one sentence |
| 3. State and recurrence | 10–20 | `f(...)` in English, the recurrence, base cases, complexity estimate — **agreed with the interviewer before coding** |
| 4. Code (memoization) | 20–32 | Working top-down code |
| 5. Test | 32–38 | Traced on a small example and two edge cases; bugs fixed |
| 6. Improve | 38–45 | Tabulation and/or space optimization, or discussion of follow-ups |

Why memoization first? It is the shortest correct path from the recursion you just explained. Tabulation is an *improvement* you add if time remains. If you already find bottom-up natural for this problem, start there — but never skip stating the recurrence.

---

## 2. Step by step: what to do and what to say

### Phase 1 — Clarify (≈ 5 min)

Ask before you think hard. Good questions:

- "What are the bounds on n and on the values?" (→ [constraint table](DP_PATTERN_RECOGNITION.md#7-constraint--complexity--technique-table))
- "Can the input be empty? Can values be negative or zero?"
- "If no answer exists, what should I return?"
- "Should the count be returned modulo something?"
- "Do I need the optimal *value* or the actual *choice/path*?" (reconstruction changes the code)
- "Is order significant — is `1+2` different from `2+1`?" (combinations vs. permutations)

> **Say:** "Let me restate the problem to make sure I have it: …"

### Phase 2 — Examples and brute force (≈ 5 min)

- Work one small example **by hand** on the whiteboard. Narrate the choices you make.
- Notice the decision you make repeatedly. That decision becomes the recursion.

> **Say:** "At each position I either do X or Y. If I try both and recurse, that's a correct brute force, exponential: about 2ⁿ calls."

Mentioning brute force early is valuable: it shows you can always get *a* correct answer, and it is the starting point of the DP.

### Phase 3 — State and recurrence (≈ 10 min)

Go through the [seven derivation questions](../00-foundations/16-the-dp-problem-solving-framework/) aloud:

1. What changes? → parameters
2. What does `f(params)` mean? → one English sentence
3. What are the choices?
4. Recurrence
5. Base cases
6. Order of computation
7. Can it be optimized?

Then **show overlap** quickly: "`f(3)` is reached from `f(1)` and from `f(2)`, so memoizing removes repeated work." Then estimate complexity: states × work per state.

> **Ask the interviewer:** "Does this approach sound good before I code it?" — this checkpoint prevents you from spending 15 minutes coding the wrong idea.

### Phase 4 — Code (≈ 12 min)

- Write the state sentence as a comment above the memo array.
- Base cases first, then the memo check, then the choices.
- Use meaningful names (`waysFrom`, `remainingCapacity`).
- Keep talking, but briefly: "Now the memo check… now the two choices…"

### Phase 5 — Test (≈ 6 min)

See [Section 4](#4-testing-your-solution).

### Phase 6 — Improve (≈ 7 min)

- Convert to tabulation: "The recursion only reads larger indices, so I'll loop from n down to 0."
- Space optimize: "Each cell reads only the next two cells, so two variables suffice."
- State the final complexity with a reason.

---

## 3. Communicating the state and recurrence aloud

Interviewers grade **clarity** as much as correctness. Use precise, complete sentences.

| Weak | Strong |
|---|---|
| "dp of i is the answer." | "`dp[i]` is the number of ways to decode the suffix starting at index `i`." |
| "Then we take the max." | "We either skip house `i`, keeping `dp[i+1]`, or rob it and jump to `dp[i+2]` plus its money. We take the larger." |
| "Base case is zero." | "`dp[n] = 1` because an empty suffix has exactly one decoding — decode nothing." |
| "It's O(n²)." | "There are n² states `(i, j)` and each does constant work, so O(n²) time. We keep one row, so O(n) space." |

A useful template:

> "Let **f(i, j)** be **[quantity]** for **[sub-input described by i, j]**. To compute it, I consider **[choices]**. Each choice leads to **[smaller state]**, so **f(i, j) = [combine] of [terms]**. The smallest case is **[base]**, whose answer is **[value]** because **[reason]**. There are **[#states]** states with **[work]** each, so **[complexity]**."

Draw while you speak: a tiny table, an arrow from a cell to the cells it depends on. Pictures make the loop order self-evident.

---

## 4. Testing your solution

Do not say "I think it works." Prove it on the board.

1. **Trace the given example** through your code (not through your idea). Fill the memo/table cells as you go.
2. **Edge cases** — choose two or three:
   - smallest input (n = 0 or 1, empty string)
   - all equal / all zero / all negative values
   - impossible case (answer −1 / 0 / false)
   - maximum size (reason about time and overflow, don't trace)
3. **Check the dangerous lines** against [COMMON_MISTAKES.md](COMMON_MISTAKES.md): base case value, loop direction, index offsets (`i−1`), sentinel collisions, overflow.
4. If you find a bug, say so calmly, fix it, and re-run the trace from the affected cell.

> **Say:** "Let me trace `226`… `dp[3] = 1`, `dp[2] = 1`, `dp[1] = dp[2] + dp[3] = 2`, `dp[0] = dp[1] + dp[2] = 3`. Matches the expected 3."

---

## 5. When you are stuck

Being stuck is normal; going silent is the real mistake. Use these moves in order and narrate each one.

| Stuck on… | Try this |
|---|---|
| "Is this even DP?" | Run the [recognition checklist](DP_PATTERN_RECOGNITION.md#11-recognition-checklist-one-screen). Write the brute force anyway; look for repeated arguments. |
| Can't find the state | **List everything you would need to know halfway through.** Try "answer for the prefix ending at i" or "answer for the suffix starting at i". For pairs of strings, try `(i, j)`. |
| Recurrence gives wrong answers on an example | The state is probably too small. Find two histories that reach the same state but need different futures — that difference is your missing dimension (e.g., a "holding" flag). |
| Can't decide the last choice | Think about the **last** step instead of the first (last item, last balloon burst, last cut). |
| Solution is too slow | Compute states × work. Can the inner loop be replaced by a prefix max, a sliding-window max, or binary search? (Module 05.) |
| Completely blocked | Solve a **simpler version** aloud: smaller constraints, one fewer dimension, k = 1 instead of k. Then generalize. Ask: "Would it be acceptable to first solve the version without X?" |

Accepting a hint well is a positive signal. Repeat the hint in your own words, connect it to your current state, and continue.

---

## 6. Scripted example: Decode Ways (Medium)

**Problem:** [Decode Ways](https://leetcode.com/problems/decode-ways/) (LeetCode 91, Medium).
**Our summary:** letters A–Z are encoded as `1`–`26`. Given a string of digits, count how many ways it can be decoded back into letters. Taught in full in [01-one-dimensional-dp/08-decode-ways](../01-one-dimensional-dp/08-decode-ways/).

The dialogue below is idealized but realistic. **I** = interviewer, **C** = candidate.

---

**[0:00 — Clarify]**

**I:** Here is the problem. Take a moment.

**C:** Let me restate it. I get a string of digits. Each letter is written as its position, 1 to 26. I need the number of different letter strings that encode to exactly this digit string. A few questions:
- Can the string contain `0`? — *I: Yes.*
- So `"0"` alone, or `"06"`, can't be decoded, because no letter is `0` or `06`? — *I: Correct, return 0.*
- Length bounds? — *I: Up to 100.*
- Does the answer fit in an int? — *I: Yes, it's guaranteed.*

**[0:04 — Example and brute force]**

**C:** Let me work `"226"` by hand. At the start I can read one digit, `2`, or two digits, `22`.
- `2` then `"26"`: either `2,6` or `26` → 2 ways.
- `22` then `"6"`: `6` → 1 way.
Total 3: `BBF`, `BZ`, `VF`.

So at each position I have at most two choices: take one digit if it's 1–9, or take two digits if they form 10–26. A brute-force recursion tries both. That's up to 2ⁿ calls — far too slow for n = 100.

**[0:08 — Overlap]**

**C:** Let me check whether calls repeat. Let `f(i)` be the number of ways to decode the suffix starting at `i`.

```
              f(0)
           /        \
       f(1)          f(2)      ← f(0) took "2" / took "22"
      /    \
   f(2)    f(3)                ← f(1) took "2" / took "26"
```

`f(2)` is computed twice already, and on longer strings the repetition explodes, like Fibonacci. The only thing that changes is `i`, so there are just n+1 different subproblems. That's DP.

**[0:11 — State and recurrence]**

**C:** Formally:
- **State:** `f(i)` = number of ways to decode `s[i..n−1]`.
- **Choices:** take `s[i]` alone if it isn't `'0'`; take `s[i..i+1]` if `i+1 < n` and the number is between 10 and 26.
- **Recurrence:** `f(i) = (s[i] ≠ '0' ? f(i+1) : 0) + (10 ≤ s[i..i+1] ≤ 26 ? f(i+2) : 0)`.
- **Base case:** `f(n) = 1` — the empty suffix has exactly one decoding, namely decoding nothing. That's what makes a complete decoding count as 1.
- **Answer:** `f(0)`.
- **Complexity:** n+1 states, O(1) work each → O(n) time, O(n) for the memo plus recursion stack.

Does that sound reasonable before I code it?

**I:** Yes, go ahead.

**[0:15 — Code]**

```java
class Solution {
    private String digits;
    private Integer[] memo;   // memo[i] = ways to decode digits[i..]; null = not computed

    public int numDecodings(String s) {
        digits = s;
        memo = new Integer[s.length() + 1];   // fresh memo for every call
        return waysFrom(0);
    }

    private int waysFrom(int i) {
        if (i == digits.length()) return 1;    // empty suffix: one way
        if (digits.charAt(i) == '0') return 0; // no letter starts with 0
        if (memo[i] != null) return memo[i];

        int ways = waysFrom(i + 1);            // take one digit (1..9)
        if (i + 1 < digits.length()) {
            int twoDigit = (digits.charAt(i) - '0') * 10 + (digits.charAt(i + 1) - '0');
            if (twoDigit <= 26) ways += waysFrom(i + 2);  // 10..26 (first digit is not 0 here)
        }
        return memo[i] = ways;
    }
}
```

**C:** I handled `'0'` before the memo check: a suffix starting with `0` has zero decodings, which also covers the "two-digit number starting with 0" case, so the second check only needs `≤ 26`. I used `Integer[]` so that `null` means "not computed" and a real answer of 0 can be cached without colliding with the sentinel.

**[0:24 — Test]**

**C:** Trace `"226"`:
- `waysFrom(3) = 1` (base).
- `waysFrom(2)`: `'6'` → `waysFrom(3) = 1`; no two-digit option → **1**.
- `waysFrom(1)`: `'2'` → `waysFrom(2) = 1`; `26 ≤ 26` → + `waysFrom(3) = 1` → **2**.
- `waysFrom(0)`: `'2'` → `waysFrom(1) = 2`; `22 ≤ 26` → + `waysFrom(2) = 1` (memo hit) → **3**. ✓

Edge cases:
- `"0"` → `waysFrom(0)` returns 0 immediately. ✓
- `"10"` → `waysFrom(0)`: one digit gives `waysFrom(1)` = 0 (starts with `0`); two digits `10` gives `waysFrom(2)` = 1 → **1**. ✓
- `"2101"` → expected 1 (`2,10,1`). `waysFrom(3)=1`, `waysFrom(2)=0` (starts with `0`), `waysFrom(1)`: `1` → 0, plus `10` → `waysFrom(3)`=1 → 1; `waysFrom(0)`: `2` → 1, plus `21` → `waysFrom(2)`=0 → **1**. ✓

**[0:31 — Improve]**

**I:** Can you do it without recursion?

**C:** Yes. `f(i)` only depends on `f(i+1)` and `f(i+2)`, i.e. larger indices, so I fill from right to left. And because only the next two values are used, I can keep two variables instead of an array.

```java
public int numDecodings(String s) {
    int n = s.length();
    int next1 = 1;   // f(i+1), starts as f(n) = 1
    int next2 = 0;   // f(i+2), f(n+1) is unused, 0 is safe
    for (int i = n - 1; i >= 0; i--) {
        int current = 0;
        if (s.charAt(i) != '0') {
            current = next1;
            if (i + 1 < n && (s.charAt(i) - '0') * 10 + (s.charAt(i + 1) - '0') <= 26)
                current += next2;
        }
        next2 = next1;
        next1 = current;
    }
    return next1;    // f(0)
}
```

**C:** O(n) time — one pass with constant work per index — and O(1) extra space.

**I:** What if the string could also contain `*` meaning any digit 1–9?

**C:** The state stays the same, but each choice now has a *multiplicity*: a single `*` contributes 9 ways times `f(i+1)`, and a pair like `1*` contributes 9, `2*` contributes 6, and `**` contributes 15. The counts become large, so I'd take them modulo 10^9+7 and use `long`. That's [Decode Ways II](https://leetcode.com/problems/decode-ways-ii/).

---

**What made this a strong interview:** the candidate clarified the `0` rule, showed overlap with a tiny tree, stated the state as a full sentence and justified the base case, asked for agreement before coding, traced the code on the example and three edge cases, and finished with an O(1)-space version and a sensible extension.

---

## 7. Follow-up questions to expect

| Follow-up | How to answer |
|---|---|
| "Can you reduce the space?" | Identify which earlier rows/cells are read → keep only those ([space optimization](../00-foundations/15-space-optimization-basics/)). |
| "Return the actual solution, not just the value." | Keep the full table (or a `choice[]` / `parent[]` array) and walk back from the answer cell. Note this usually prevents space optimization. |
| "What if n is 10^5 / 10^9?" | Look for an O(n log n) structure (binary search, monotonic queue) or matrix exponentiation. See [Module 05](../05-dp-optimization/). |
| "What if items can be reused / used once?" | Switch between unbounded and 0/1 knapsack (capacity loop direction). |
| "Top-down or bottom-up — which is better here?" | Top-down computes only reachable states and is easier to derive; bottom-up avoids recursion depth limits and enables space optimization. See [top-down vs bottom-up](../00-foundations/08-top-down-vs-bottom-up/). |
| "Why is greedy not enough?" | Give a concrete counterexample (e.g., coins `{1,3,4}`, amount 6). |
| "Prove it's correct." | Each state's value is the best over *all* choices, each choice leads to a correctly computed smaller state (induction on the computation order), and the base cases are correct by definition. |

---

## 8. Practice protocol

Practising the routine matters more than the number of problems solved.

1. **Timer on.** 45 minutes per Medium, 60 per Hard.
2. **Speak aloud** (or write the narration) even when alone. Recording yourself is very effective.
3. **Write the 7 answers before code**, every time.
4. **Code on a plain editor first** (no autocomplete), then submit.
5. **After each problem**, log in [PROGRESS_TRACKER.md](../PROGRESS_TRACKER.md): pattern, time taken, where you got stuck, which mistake from [COMMON_MISTAKES.md](COMMON_MISTAKES.md) you made.
6. **Weekly mock:** have a friend pick an unseen problem from [06-leetcode-practice](../06-leetcode-practice/) and play the interviewer using this file.
7. Schedule re-solves with [REVISION_SCHEDULE.md](REVISION_SCHEDULE.md).

---

**See also:** [DP_PATTERN_RECOGNITION.md](DP_PATTERN_RECOGNITION.md) · [RECURRENCE_CHEATSHEET.md](RECURRENCE_CHEATSHEET.md) · [module README](README.md)
