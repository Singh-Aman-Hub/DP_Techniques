# Practice: Digit DP

Digit DP counts the integers in a range (`≤ n`, or `[lo, hi]`) whose **digits** satisfy some
property, without looping over the numbers. You build the number one digit at a time and carry a
small summary of the prefix: **tight** (still equal to the bound's prefix?), often **started**
(past the leading zeros?), plus whatever the property needs (a flag, a set of used digits, a
running sum, the previous digit).

Learn the technique first in [`04-advanced-dp-patterns/11-digit-dp`](../../04-advanced-dp-patterns/11-digit-dp/),
then work through this table from top to bottom. Each step adds one new idea.

| # | Problem | LeetCode link | Difficulty | Pattern | Prerequisite topic folder |
|---|---|---|---|---|---|
| 1 | [788. Rotated Digits](788-rotated-digits/) | [788](https://leetcode.com/problems/rotated-digits/) | Medium | tight + one property flag | [`11-digit-dp`](../../04-advanced-dp-patterns/11-digit-dp/) |
| 2 | [357. Count Numbers with Unique Digits](357-count-numbers-with-unique-digits/) | [357](https://leetcode.com/problems/count-numbers-with-unique-digits/) | Medium | used-digit mask, no bound; collapses to a product | [`11-digit-dp`](../../04-advanced-dp-patterns/11-digit-dp/) |
| 3 | [902. Numbers At Most N Given Digit Set](902-numbers-at-most-n-given-digit-set/) | [902](https://leetcode.com/problems/numbers-at-most-n-given-digit-set/) | Hard | tight + started, restricted alphabet | [`11-digit-dp`](../../04-advanced-dp-patterns/11-digit-dp/) |
| 4 | [600. Non-negative Integers without Consecutive Ones](600-non-negative-integers-without-consecutive-ones/) | [600](https://leetcode.com/problems/non-negative-integers-without-consecutive-ones/) | Hard | base-2 digit DP, previous-bit state | [`11-digit-dp`](../../04-advanced-dp-patterns/11-digit-dp/) |
| 5 | 233. Number of Digit One (taught in the topic) | [233](https://leetcode.com/problems/number-of-digit-one/) | Hard | count digit occurrences, not numbers | [`11-digit-dp`](../../04-advanced-dp-patterns/11-digit-dp/) |
| 6 | 2376. Count Special Integers (taught in the topic) | [2376](https://leetcode.com/problems/count-special-integers/) | Hard | tight + started + used-digit mask | [`11-digit-dp`](../../04-advanced-dp-patterns/11-digit-dp/) |
| 7 | [1012. Numbers With Repeated Digits](1012-numbers-with-repeated-digits/) | [1012](https://leetcode.com/problems/numbers-with-repeated-digits/) | Hard | complement counting: `n − distinct(n)` | [`11-digit-dp`](../../04-advanced-dp-patterns/11-digit-dp/) |
| 8 | [2719. Count of Integers](2719-count-of-integers/) | [2719](https://leetcode.com/problems/count-of-integers/) | Hard | two bounds + digit-sum state, modulo | [`11-digit-dp`](../../04-advanced-dp-patterns/11-digit-dp/) |

Problems 5 and 6 are worked through in full inside the Digit DP topic, so they have no folder here.
Solve them there before attempting 1012, which is 2376 seen from the other side.

## How to use these problems

1. Before coding, write the state as a sentence: "`f(pos, …)` = number of ways to fill positions
   `pos..L−1` so that …". List exactly which facts about the prefix the future needs.
2. Decide whether you need `started` (does a leading zero behave differently from a real 0?).
3. Write the memoized version first. Then look for free (non-tight) states with a closed form:
   that is how the bottom-up "walk along the bound" versions in these folders are derived.
4. Every solution folder cross-checks the DP against brute force on small inputs. Do the same with
   your own code.
