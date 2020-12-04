package net.praks.aoc2020;

import com.google.common.io.CharStreams;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import static org.assertj.core.api.Assertions.assertThat;

class Day1Test {

  @ParameterizedTest
  @CsvSource({
      "example, 2, 514579",
      "real, 2, 902451",
      "example, 3, 241861950",
      "real, 3, 85555470",
  })
  void test(String caseName, int countOfNumbersToSum, long expectedProduct) throws IOException {
    try (Reader reader = new InputStreamReader(ResourceUtil.getResource(getClass(), caseName))) {
      Day1 day1 = new Day1(countOfNumbersToSum, 2020);
      long actualProduct = day1.findMultipleWithSum(CharStreams.readLines(reader));
      assertThat(actualProduct).isEqualTo(expectedProduct);
    }
  }
}