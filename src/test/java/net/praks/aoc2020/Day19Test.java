package net.praks.aoc2020;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class Day19Test {

  private static final int RULE_ID = 0;

  @ParameterizedTest
  @CsvSource({
      "example-part1, 2",
      "example-part2-before, 3",
      "example-part2-after, 12",
      "real-part1, 109",
      "real-part2, 301",
  })
  void testSumExpressions(@ResourceFromClassPathArgument List<String> input, long expectedCount) {
    Day19.RulesAndMessages rulesAndMessages = Day19.RulesAndMessages.parse(input);
    long actualCount = rulesAndMessages.countValidMessages(RULE_ID);
    assertThat(actualCount).isEqualTo(expectedCount);
  }

}