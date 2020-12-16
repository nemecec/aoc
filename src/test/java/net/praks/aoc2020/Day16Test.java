package net.praks.aoc2020;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class Day16Test {

  @ParameterizedTest
  @CsvSource({
      "example, 71",
      "real, 28882",
  })
  void testCalculateTicketScanningErrorRateOfNearbyTickets(
      @ResourceFromClassPathArgument List<String> input, int expectedRate) {
    Day16.Notes notes = Day16.Notes.parse(input);
    int actualRate = notes.calculateTicketScanningErrorRateOfNearbyTickets();
    assertThat(actualRate).isEqualTo(expectedRate);
  }

  @ParameterizedTest
  @CsvSource({
      "example2, class, 12",
      "example2, row, 11",
      "example2, seat, 13",
      "real, departure, 1429779530273",
  })
  void testCalculateProductOfFieldsStartingWith(
      @ResourceFromClassPathArgument List<String> input, String fieldPrefix, long expectedProduct) {
    Day16.Notes notes = Day16.Notes.parse(input);
    long actualProduct = notes.calculateProductOfFieldsStartingWith(fieldPrefix);
    assertThat(actualProduct).isEqualTo(expectedProduct);
  }

}