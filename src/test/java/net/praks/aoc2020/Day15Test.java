package net.praks.aoc2020;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;
import java.util.OptionalInt;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class Day15Test {

  @ParameterizedTest
  @CsvSource({
      "'0,3,6', 2020, 436",
      "'1,3,2', 2020, 1",
      "'2,1,3', 2020, 10",
      "'1,2,3', 2020, 27",
      "'2,3,1', 2020, 78",
      "'3,2,1', 2020, 438",
      "'3,1,2', 2020, 1836",
      "'0,13,16,17,1,10,6', 2020, 276",
      "'0,13,16,17,1,10,6', 30000000, 31916",
  })
  void test(String startingNumbersAsString, int turnNumberToEndAt, int expectedLastNumber) {
    Pattern separator = Pattern.compile(",");
    Day15.MemoryGame game = new Day15.MemoryGame(
        Arrays.stream(separator.split(startingNumbersAsString))
            .map(Integer::parseInt).collect(Collectors.toList())
    );
    OptionalInt actualValue = game.nextNumberUntilTurn(turnNumberToEndAt);
    assertThat(actualValue).hasValue(expectedLastNumber);
  }

}