package net.praks.aoc2020;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class Day2Test {

  @ParameterizedTest
  @CsvSource({
      "example, COUNTING, 2",
      "real, COUNTING, 614",
      "example, POSITIONAL, 1",
      "real, POSITIONAL, 354",
  })
  void test(@ResourceFromClassPathArgument List<String> input, Day2.PasswordValidationStrategy passwordValidationStrategy, long expectedCount) {
    Day2 day2 = new Day2(passwordValidationStrategy);
    long actualCount = day2.countValidPasswords(input);
    assertThat(actualCount).isEqualTo(expectedCount);
  }

}