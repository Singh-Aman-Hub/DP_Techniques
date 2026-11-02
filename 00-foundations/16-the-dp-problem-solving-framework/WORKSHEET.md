# DP Problem Worksheet

> Copy this file for every new problem (for example into your own notes as `worksheet-<problem>.md`) and fill it in **before** writing code. The framework behind it is explained in the [walkthrough](walkthrough.md). Leave nothing blank: an empty line is a question you have not answered yet.

---

## 0. The problem

- **Name / link:**
- **In my own words (2–4 sentences):**
- **Input limits** (largest n, largest values):
- **What exactly is asked?** (count / minimum / maximum / yes-no / the actual choices?)
- **Modulo?** (yes / no — which number?)

## 0b. Is it DP? (tick what applies)

- [ ] It asks to **count** ways, or for a **min / max**, or **whether** something is possible
- [ ] I make a **sequence of decisions**, and each one changes what is possible later
- [ ] Trying all possibilities is exponential, and the **same smaller question** appears again and again
- [ ] An optimal answer is built from optimal answers to smaller versions
- [ ] Some numbers in the input are small enough to be array sizes
- [ ] A simple greedy rule fails on a small example I tried: ______________________

---

## 1. What is changing?

(Which quantities move or shrink as I make decisions? index, remaining amount, position, ...)

```

```

## 2. What does the state represent?

(One complete English sentence. Include the range of every parameter.)

```
f(        ) = 

parameter ranges:
```

**Which state is the final answer?**

## 3. What are the choices?

(At one state, list every possible decision. Is there any choice that is not always allowed?)

```
1.
2.
3.
```

## 4. What is the recurrence?

(Combine the choices with min / max / + / OR. Explain each term.)

```
f(        ) = 
```

## 5. What are the base cases?

(The smallest states, answered directly. Also: which states are invalid, and what do they return?)

```
terminal: 
invalid:  
```

## 6. What is the order of computation?

(Which states does f read: smaller or larger indices, previous row, shorter ranges? So which loop direction?)

```
reads:
loops:
```

## 7. Can the state space be optimized?

(How far back does the recurrence read? Do I need to reconstruct the choices?)

```
read window:
optimized storage:
```

---

## Complexity

| | Count | Why |
|--|-------|-----|
| Number of states | | |
| Work per state | | |
| **Time** | | |
| **Space (table)** | | |
| **Space (optimized)** | | |
| Max recursion depth (memo) | | Safe in Java? (≲ 10⁴) |

Does it fit the input limits? ____

---

## Hand-computed test cases (before coding)

| # | Input | Expected output | How I got it |
|---|-------|-----------------|--------------|
| 1 | smallest / edge case | | |
| 2 | the example from the statement | | |
| 3 | a case I traced through my table by hand | | |
| 4 | a tricky case (zeros, impossible, all equal...) | | |

Small table traced by hand:

```

```

---

## Code checklist

- [ ] Brute force written and passing on tiny inputs
- [ ] Memoization (cache sized from the parameter ranges, sentinel value chosen)
- [ ] Tabulation (base cases first, loops follow question 6, answer cell matches question 2)
- [ ] Optimized version (if question 7 allows it)
- [ ] All versions agree on all test cases
- [ ] Overflow checked (`long`? modulo after every addition?)

---

## Reflection (after solving)

- Where did I get stuck, and which question would have unblocked me?
- What mistake did I make (state, base case, order, overflow...)? Add it to my personal mistakes list.
- Which problem I already know has the same shape?
- One sentence I want to remember from this problem:
