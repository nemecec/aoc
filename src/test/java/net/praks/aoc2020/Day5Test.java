package net.praks.aoc2020;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;
import java.util.OptionalInt;

import static org.assertj.core.api.Assertions.assertThat;

class Day5Test {

  @ParameterizedTest
  @CsvSource({
      "FBFBBFF, F, B, 44",
      "RLR, L, R, 5"
  })
  void testDecode(String encodedStr, char lowerHalfIdentifier, char upperHalfIdentifier, int expectedValue) {
    Day5.BinarySpaceDecoder day5 = new Day5.BinarySpaceDecoder(lowerHalfIdentifier, upperHalfIdentifier);
    int actualValue = day5.decode(encodedStr);
    assertThat(actualValue).isEqualTo(expectedValue);
  }

  @ParameterizedTest
  @CsvSource({
      "FBFBBFFRLR, 357",
      "BFFFBBFRRR, 567",
      "FFFBBBFRRR, 119",
      "BBFFBBFRLL, 820",
  })
  void testDecodeBoardingPass(String encodedBoadingPass, int expectedId) {
    Day5.BoardingPass boardingPass = Day5.BoardingPass.parse(encodedBoadingPass);
    int actualId = boardingPass.getId();
    assertThat(actualId).isEqualTo(expectedId);
  }

  @ParameterizedTest
  @CsvSource({
      "example, 820",
      "real, 935",
  })
  void testMaxId(@ResourceFromClassPathArgument List<String> input, int expectedId) {
    OptionalInt actualId = Day5.findMaxBoardingPassId(input);
    assertThat(actualId).hasValue(expectedId);
  }

  @ParameterizedTest
  @CsvSource({
      "real, 743",
  })
  void testMissingId(@ResourceFromClassPathArgument List<String> input, int expectedId) {
    OptionalInt actualId = Day5.findMissingBoardingPassId(input);
    assertThat(actualId).hasValue(expectedId);
  }

}