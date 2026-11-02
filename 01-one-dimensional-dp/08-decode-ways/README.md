# Decode Ways

> **Module:** 01 · One-Dimensional DP · **Difficulty:** Intermediate · **Estimated time:** 90–120 min

## 1. What is this topic?

Letters are encoded as numbers: `A → 1`, `B → 2`, …, `Z → 26`. A message was encoded and the letters were glued together, so `"1226"` could have come from `ABBF` (1,2,2,6), `LBF` (12,2,6), `AVF` (1,22,6), `ABZ` (1,2,26) or `LZ` (12,26). How many different messages could a digit string have come from?

This is **counting DP with validity conditions**: the recurrence is the [Climbing Stairs](../02-climbing-stairs/) recurrence ("the last piece has length 1 or 2"), but each move is allowed only when the digits it covers form a valid letter. The character `'0'` makes those conditions subtle, and handling it correctly is the main lesson.

## 2. Why does this concept exist?

Many string problems ask "in how many ways can this string be split into valid pieces?" or "can it be split at all?". The pieces might be letters, dictionary words or palindromes. The DP shape is always the same: `ways[i]` for each prefix, summed over the possible last pieces that are **valid**. Decode Ways is the smallest version, where the only possible pieces have length 1 or 2.

## 3. What problem does it solve?

[LeetCode 91 · Decode Ways](https://leetcode.com/problems/decode-ways/): count the number of ways to split a digit string into pieces, each piece being a number from 1 to 26 written without leading zeros.

## 4. Prerequisites

- [Climbing Stairs](../02-climbing-stairs/) — counting by the last move, the empty-prefix base case `ways[0] = 1`.
- [State transitions and base cases](../../00-foundations/10-state-transitions-and-base-cases/)
- [State representation](../../00-foundations/09-state-representation/) — prefix-length states ("first `i` characters").

## 5. What you will learn

- Counting DP where each transition has a **validity condition**.
- Prefix-length indexing: `ways[i]` = answer for the first `i` characters.
- All the `'0'` edge cases: `"0"`, `"06"`, `"10"`, `"100"`, `"30"`, `"2101"`.
- Why a zero in the middle of the table makes every later cell's count depend on a different path, and when the whole answer becomes 0.

## 6. Important terminology

| Term | Meaning in plain English |
|------|--------------------------|
| Encoding | The map `A=1 … Z=26`. |
| Decoding | One way of splitting the digit string into valid letter codes. |
| Piece | A group of 1 or 2 consecutive digits that becomes one letter. |
| Valid single | A one-digit piece `1`–`9` (`0` is not a letter). |
| Valid pair | A two-digit piece `10`–`26` (no leading zero like `06`). |
| Prefix of length `i` | The first `i` characters, `s[0..i-1]`. |
| Dead prefix | A prefix with 0 decodings; anything built only on it is also impossible. |

## 7. Where this is used in real problems

Word Break (split into dictionary words), counting ways to split a string into palindromes or valid numbers, parsing ambiguous encodings, and Decode Ways II (with a `*` wildcard).

## 8. After completing this topic you should be able to…

- [ ] Write the recurrence with both validity conditions.
- [ ] Explain why `ways[0] = 1`.
- [ ] Predict the answer for any of the `'0'` edge cases before running code.
- [ ] Fill the table by hand for a string with zeros in the middle.
- [ ] Rewrite the DP over suffixes instead of prefixes.

## 9. Practice problems

| # | Problem | Platform/ID | Difficulty | Pattern tested | Why it belongs here |
|---|---------|-------------|------------|----------------|---------------------|
| 1 | Decode Ways | [LeetCode 91](https://leetcode.com/problems/decode-ways/) | Medium | Counting with validity conditions | The core problem of this topic. |
| 2 | Word Break | [LeetCode 139](https://leetcode.com/problems/word-break/) | Medium | Prefix DP over valid last pieces (boolean) | The last piece is any dictionary word instead of 1–2 digits; `+` becomes `OR`. |
| 3 | Decode Ways II | [LeetCode 639](https://leetcode.com/problems/decode-ways-ii/) | Hard | Counting with weighted transitions, modulo | `*` stands for 1–9, so each transition is multiplied by the number of letters it can be. |

## 10. Navigation

⬅ Previous: [House Robber II](../07-house-robber-ii/) · ➡ Next: [Maximum Subarray: Kadane vs. DP](../09-maximum-subarray-kadane-vs-dp/) · 📖 [Walkthrough](walkthrough.md) · 💻 [Solutions](solution/)
