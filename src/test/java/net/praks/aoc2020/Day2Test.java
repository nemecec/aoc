package net.praks.aoc2020;

import com.google.common.io.CharStreams;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import static org.assertj.core.api.Assertions.assertThat;

class Day2Test {

  @ParameterizedTest
  @CsvSource({
      "example, COUNTING, 2",
      "real, COUNTING, 614",
      "example, POSITIONAL, 1",
      "real, POSITIONAL, 354",
  })
  void test(String caseName, Day2.PasswordValidationStrategy passwordValidationStrategy, long expectedCount) throws IOException {
    try (Reader reader = new InputStreamReader(ResourceUtil.getResource(getClass(), caseName))) {
      Day2 day2 = new Day2(passwordValidationStrategy);
      long actualCount = day2.countValidPasswords(CharStreams.readLines(reader));
      assertThat(actualCount).isEqualTo(expectedCount);
    }
  }

}