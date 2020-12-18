package net.praks.aoc2020;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class Day17Test {

  private static final int NUMBER_OF_ITERATIONS = 6;

  @ParameterizedTest
  @CsvSource({
      "example, example-expected-output-3D, 112",
      "real, real-expected-output-3D, 426",
  })
  void testCountActiveIn3D(
      @ResourceFromClassPathArgument List<String> input,
      @ResourceFromClassPathArgument String expectedGrid,
      int expectedCount
  ) {
    Day17.Grid3D grid = Day17.Grid3D.parse(input);
    Day17.Grid3D actualFinalGrid = grid.calculateNewGrid(NUMBER_OF_ITERATIONS);
    assertAll(
        () -> assertThat(grid).hasToString("z=0\n" + String.join("\n", input)),
        () -> assertThat(actualFinalGrid).hasToString(expectedGrid),
        () -> assertThat(actualFinalGrid.countActivePoints()).isEqualTo(expectedCount)
    );
  }

  @ParameterizedTest
  @CsvSource({
      "example, example-expected-output-4D, 848",
      "real, real-expected-output-4D, 1892",
  })
  void testCountActiveIn4D(
      @ResourceFromClassPathArgument List<String> input,
      @ResourceFromClassPathArgument String expectedGrid,
      int expectedCount
  ) {
    Day17.Grid4D grid = Day17.Grid4D.parse(input);
    Day17.Grid4D actualFinalGrid = grid.calculateNewGrid(NUMBER_OF_ITERATIONS);
    assertAll(
        () -> assertThat(grid).hasToString("w=0 z=0\n" + String.join("\n", input)),
        () -> assertThat(actualFinalGrid).hasToString(expectedGrid),
        () -> assertThat(actualFinalGrid.countActivePoints()).isEqualTo(expectedCount)
    );
  }

}