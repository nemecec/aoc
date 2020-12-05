Advent Of Code solutions
===

Here you can find solutions to the [Advent of Code](https://adventofcode.com/) puzzles,
implemented in Java.

[![Java CI with Gradle](https://github.com/nemecec/aoc/workflows/Java%20CI%20with%20Gradle/badge.svg)](https://github.com/nemecec/aoc/actions?query=workflow%3A%22Java+CI+with+Gradle%22)

Requirements
---

* Java 8+
* Gradle 6 (Gradle wrapper included)

Code organisation
---

* Each year is in a separate package (for now, there is only 2020).
* Solution for each day is in a separate class file, e.g. `Day1.java`.
* For each day, there is also a JUnit test with input (from the puzzle)
  and assertion that the solution is correct.

**SPOILER ALERT!** If you do not want to see the answer, do not read the source of the JUnit test.

Running
---

As each solution is validated with a unit test, you just need to run the tests:
```
./gradlew test
```