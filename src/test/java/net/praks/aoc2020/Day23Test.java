package net.praks.aoc2020;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class Day23Test {

  private static final int FIRST_LABEL = 1;

  @ParameterizedTest
  @CsvSource({
      "'389125467', 10, '92658374'",
      "'389125467', 100, '67384529'",
      "'562893147', 100, '38925764'",
  })
  void testGame(
      String cupOrderAtStart,
      long numberOfMoves,
      String expectedCupOrderAtEnd
  ) {
    Day23.CupCircle cupCircle = Day23.CupCircle.parse(cupOrderAtStart);
    assertThat(cupCircle).hasToString(cupOrderAtStart);
    cupCircle.makeMoves(numberOfMoves);
    int[] actualCupOrderAtEnd = cupCircle.getCupsStartingAfter(FIRST_LABEL);
    String actualCupOrderAtEndAsString =
        Arrays.stream(actualCupOrderAtEnd).mapToObj(String::valueOf).collect(Collectors.joining(""));
    assertThat(actualCupOrderAtEndAsString).isEqualTo(expectedCupOrderAtEnd);
  }

  @ParameterizedTest
  @CsvSource({
      "'389125467', 10000000, 149245887792",
      "'562893147', 10000000, 131152940564",
  })
  @Disabled("With current implementation, one test case will run for ~2.5 hours")
  void testGamePart2(
      String cupOrderAtStart,
      long numberOfMoves,
      long expectedProductOfTwoCupsNextToOne
  ) {
    Day23.CupCircle cupCircle = Day23.CupCircle.parse(cupOrderAtStart, 1000000);
    cupCircle.makeMoves(numberOfMoves);
    long actualProductOfTwoCupsNextToOne = cupCircle.productOfTwoLabelsNextFrom(FIRST_LABEL);
    assertThat(actualProductOfTwoCupsNextToOne).isEqualTo(expectedProductOfTwoCupsNextToOne);
  }

  @ParameterizedTest
  @CsvSource({
      "123456, 0, 1, 1",
      "123456, 0, 6, 123456",
      "123456, 1, 6, 234561",
      "123456, 5, 6, 612345",
  })
  void testGetCups(String givenCups, int startIndex, int count, String expectedCups) {
    Day23.CupCircle cupCircle = Day23.CupCircle.parse(givenCups);
    int[] actualCups = cupCircle.getCups(startIndex, count);
    String actualCupsAsString = Arrays.stream(actualCups).mapToObj(String::valueOf).collect(Collectors.joining(""));
    assertThat(actualCupsAsString).isEqualTo(expectedCups);
  }

  @ParameterizedTest
  @CsvSource({
      "389125467, 1, 5, 3, 328915467",
      "123456789, 0, 5, 3, 451236789",
      "123456789, 0, 3, 3, 123456789",
      "123456789, 6, 3, 3, 123789456",
      "123456789, 8, 3, 3, 391245678",
      "123456789, 7, 3, 3, 238914567",
  })
  void testMoveCups(String givenCups, int fromIndex, int toIndex, int count, String expectedCups) {
    Day23.CupCircle cupCircle = Day23.CupCircle.parse(givenCups);
    cupCircle.moveCups(fromIndex, toIndex, count);
    assertThat(cupCircle).hasToString(expectedCups);
  }

  @ParameterizedTest
  @CsvSource({
      "123456, 1, 0",
      "123456, 2, 1",
      "123456, 6, 5",
  })
  void testGetCupIndex(String givenCups, int givenLabel, int expectedIndex) {
    Day23.CupCircle cupCircle = Day23.CupCircle.parse(givenCups);
    int actualIndex = cupCircle.getCupIndex(givenLabel);
    assertThat(actualIndex).isEqualTo(expectedIndex);
  }

  @ParameterizedTest
  @CsvSource({
      "123456, 1, 23456",
      "123456, 6, 12345",
      "123456, 2, 34561",
      "123456, 4, 56123",
  })
  void testGetAllCupsStartingFrom(String givenCups, int givenLabel, String expectedCups) {
    Day23.CupCircle cupCircle = Day23.CupCircle.parse(givenCups);
    int[] actualCups = cupCircle.getCupsStartingAfter(givenLabel);
    String actualCupsAsString = Arrays.stream(actualCups).mapToObj(String::valueOf).collect(Collectors.joining(""));
    assertThat(actualCupsAsString).isEqualTo(expectedCups);
  }

}