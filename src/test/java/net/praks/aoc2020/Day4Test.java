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
      "example, false, 2",
      "real, false, 245",
      "example, true, 2",
      "real, true, 133"
  })
  void testWithoutValueValidation(String caseName, boolean validateValues, long expectedCount) throws IOException {
    try (Reader reader = new InputStreamReader(ResourceUtil.getResource(getClass(), caseName))) {
      long actualCount = Day4.countValidPassports(CharStreams.toString(reader), validateValues);
      assertThat(actualCount).isEqualTo(expectedCount);
    }
  }

  @ParameterizedTest
  @CsvSource({
      "byr, 2002, true",
      "byr, 2003, false",
      "hgt, 60in, true",
      "hgt, 190cm, true",
      "hgt, 190in, false",
      "hgt, 190, false",
      "hcl, #123abc, true",
      "hcl, #123abz, false",
      "hcl, 123abc, false",
      "ecl, brn, true",
      "ecl, wat, false",
      "pid, 000000001, true",
      "pid, 0123456789, false",
  })
  void testKeyValidation(Day4.Key givenKey, String givenValue, boolean isExpectedToBeValid) {
    boolean isActuallyValid = givenKey.isValid(givenValue);
    assertThat(isActuallyValid).isEqualTo(isExpectedToBeValid);
  }

}