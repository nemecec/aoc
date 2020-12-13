package net.praks.aoc2020;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class Day11Test {

  @ParameterizedTest
  @CsvSource({
      "example, ADJACENT, example-adjacent-expected-output, 37",
      "real, ADJACENT, real-adjacent-expected-output, 2126",
      "example, RELAXED, example-relaxed-expected-output, 26",
      "real, RELAXED, real-relaxed-expected-output, 1914",
  })
  void testCountOccupiedSeatsAdjacent(
      @ResourceFromClassPathArgument List<String> input,
      Day11.SeatMapper seatMapper,
      @ResourceFromClassPathArgument String expectedStableMap,
      int expectedCount
  ) {
    Day11.SeatMap seatMap = Day11.SeatMap.parse(input);
    Day11.SeatMap actualStableMap = seatMap.calculateNewMapsUntilStable(seatMapper);
    assertAll(
        () -> assertThat(seatMap).hasToString(String.join("\n", input)),
        () -> assertThat(actualStableMap).hasToString(expectedStableMap),
        () -> assertThat(actualStableMap.countOccupiedSeats()).isEqualTo(expectedCount)
    );
  }

  @ParameterizedTest
  @CsvSource({
      "part2-eight, 4, 3, 8",
      "part2-leftmost, 1, 1, 0",
      "part2-middle, 3, 3, 0",
  })
  void testCountOccupiedSeatsRelaxed(@ResourceFromClassPathArgument List<String> input,
                                     int seatRowIndex,
                                     int seatColumnIndex,
                                     int expectedCount) {
    Day11.SeatMap seatMap = Day11.SeatMap.parse(input);
    int actualCount = seatMap.countOccupiedSeatsInAllDirections(
        new Day11.Coordinates(seatRowIndex, seatColumnIndex)
    );
    assertAll(
        () -> assertThat(seatMap).hasToString(String.join("\n", input)),
        () -> assertThat(actualCount).isEqualTo(expectedCount)
    );
  }

}