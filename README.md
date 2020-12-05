Advent Of Code 2020 solutions
===

Here you can find solutions to the [Advent of Code 2020](https://adventofcode.com/2020) puzzles,
implemented in Java.

Requirements
---

* Java 8
* Gradle 6 (Gradle wrapper included)

Code organisation
---

Solution for each day is in a separate class file, e.g. `Day1.java`.
Also, for each day, there is a JUnit test with input (from the puzzle)
and assertion that the solution is correct.

**SPOILER ALERT!** If you do not want to see the answer, do not read the source of the JUnit test.

Running
---

As each solution is validated with a unit test, you just need to run the tests:
```
./gradlew test
```