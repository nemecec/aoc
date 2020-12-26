package net.praks.aoc2020;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class Day24Test {

  private static final int ART_DAY_COUNT = 100;

  @ParameterizedTest
  @CsvSource({
      "example, 10, 2208",
      "real, 495, 4012",
  })
  void testCountBlackTiles(
      @ResourceFromClassPathArgument List<String> input,
      long expectedCount,
      long expectedAfterArtDaysCount
  ) {
    List<List<Day24.TileDirection>> directions = Day24.parseDirections(input);
    Day24.Tiles tiles = new Day24.Tiles();
    tiles.flipAll(directions);
    long actualCount = tiles.countBlackTiles();
    long actualblackTilesAfterArtDays = tiles.loopNextDayArt(ART_DAY_COUNT).countBlackTiles();
    assertAll(
        () -> assertThat(actualCount).isEqualTo(expectedCount),
        () -> assertThat(actualblackTilesAfterArtDays).isEqualTo(expectedAfterArtDaysCount)
    );
  }

  @ParameterizedTest
  @CsvSource({
      "se, -10, 5",
      "sesw, -20, 0",
      "seswne, -10, 5",
      "seswnesw, -20, 0",
      "seswneswsw, -30, -5",
      "seswneswswse, -40, 0",
      "seswneswswsenw, -30, -5",
      "seswneswswsenww, -30, -15",
      "seswneswswsenwwnw, -20, -20",
      "seswneswswsenwwnwse, -30, -15",
  })
  void testDirectionsToPosition(String directionsAsString, int expectedNorthPos, int expectedEastPos) {
    Day24.Position position = Day24.Tiles.convertDirectionsToPosition(Day24.parseDirectionList(directionsAsString));
    assertAll(
        () -> assertThat(position.getEastPosition()).isEqualTo(expectedEastPos),
        () -> assertThat(position.getNorthPosition()).isEqualTo(expectedNorthPos)
    );
  }

}