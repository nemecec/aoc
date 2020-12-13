package net.praks.aoc2020;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class Day12Test {

  @ParameterizedTest
  @CsvSource({
      "example, 25, 286",
      "real, 420, 42073",
  })
  void testCalcDistance(
      @ResourceFromClassPathArgument List<String> input,
      int expectedDistancePart1,
      int expectedDistancePart2
  ) {
    Day12.ManoeuvreInstructions instructions = Day12.ManoeuvreInstructions.parse(input);
    Day12.MovementContext actualFinalContext = instructions.move(new Day12.MovementContext(
        new Day12.PositionWithDirection(new Day12.Position(0, 0), Day12.MoveDirection.E),
        new Day12.MovementWithWaypointContext(
            new Day12.Position(0, 0),
            new Day12.Position(10, 1)
        )
    ));
    assertAll(
        () -> assertThat(actualFinalContext.getShipPosition().calcManhattanDistance()).isEqualTo(expectedDistancePart1),
        () -> assertThat(actualFinalContext.getWithWaypointContext().getShipPosition().calcManhattanDistance()).isEqualTo(expectedDistancePart2)
    );
  }

}