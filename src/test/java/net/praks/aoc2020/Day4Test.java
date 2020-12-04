package net.praks.aoc2020;

import com.google.common.io.CharStreams;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import static org.assertj.core.api.Assertions.assertThat;

class Day4Test {

  @ParameterizedTest
  @CsvSource({
      "part1-small, 2",
      "part1, 245"
  })
  void test(String caseName, long expectedCount) throws IOException {
    try (Reader reader = new InputStreamReader(ResourceUtil.getResource(getClass(), caseName))) {
      long actualCount = Day4.countValidPassports(CharStreams.toString(reader));
      assertThat(actualCount).isEqualTo(expectedCount);
    }
  }

}