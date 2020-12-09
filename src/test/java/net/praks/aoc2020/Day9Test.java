package net.praks.aoc2020;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class Day9Test {

  @ParameterizedTest
  @CsvSource({
      "example, 5, 127, 62",
      "real, 25, 1124361034, 129444555",
  })
  void testFindingEncodingError(@ResourceFromClassPathArgument List<String> input, int bufferSize, long expectedError, long expectedRangeSum) {
    Day9.NumberList numberList = Day9.NumberList.parse(input);
    Optional<Long> actualError = numberList.findEncodingError(bufferSize);
    Optional<Day9.NumberRange> actualRange = numberList.findRangeThatSumsUpTo(expectedError);
    assertThat(actualError).hasValue(expectedError);
    assertThat(actualRange).hasValueSatisfying(
        range -> assertThat(range.sumSmallestAndLargestValues()).hasValue(expectedRangeSum)
    );
  }

}