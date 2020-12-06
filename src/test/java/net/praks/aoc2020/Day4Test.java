package net.praks.aoc2020;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class Day4Test {

  @ParameterizedTest
  @CsvSource({
      "example, false, 2",
      "real, false, 245",
      "example, true, 2",
      "real, true, 133"
  })
  void testCount(@ResourceFromClassPathArgument String input, boolean validateValues, long expectedCount) {
    long actualCount = Day4.countValidPassports(input, validateValues);
    assertThat(actualCount).isEqualTo(expectedCount);
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