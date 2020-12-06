package net.praks.aoc2020;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class Day1Test {

  @ParameterizedTest
  @CsvSource({
      "example, 2, 514579",
      "real, 2, 902451",
      "example, 3, 241861950",
      "real, 3, 85555470",
  })
  void test(@ResourceFromClassPathArgument List<String> input, int countOfNumbersToSum, long expectedProduct) {
    Day1 day1 = new Day1(countOfNumbersToSum, 2020);
    long actualProduct = day1.findMultipleWithSum(input);
    assertThat(actualProduct).isEqualTo(expectedProduct);
  }

}