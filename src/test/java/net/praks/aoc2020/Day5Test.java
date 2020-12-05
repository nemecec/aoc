package net.praks.aoc2020;

import com.google.common.io.CharStreams;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.OptionalInt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

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
  void testMaxId(String caseName, int expectedId) throws IOException {
    try (Reader reader = new InputStreamReader(ResourceUtil.getResource(getClass(), caseName))) {
      OptionalInt actualId = Day5.findMaxBoardingPassId(CharStreams.readLines(reader));
      assertThat(actualId).hasValue(expectedId);
    }
  }

  @ParameterizedTest
  @CsvSource({
      "real, 743",
  })
  void testMissingId(String caseName, int expectedId) throws IOException {
    try (Reader reader = new InputStreamReader(ResourceUtil.getResource(getClass(), caseName))) {
      OptionalInt actualId = Day5.findMissingBoardingPassId(CharStreams.readLines(reader));
      assertThat(actualId).hasValue(expectedId);
    }
  }

}