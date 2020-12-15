package net.praks.aoc2020;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class Day13Test {

  @ParameterizedTest
  @CsvSource({
      "example, 295",
      "real, 410",
  })
  void testCalcDistance(@ResourceFromClassPathArgument List<String> input, int expectedProduct) {
    Day13.BusScheduler scheduler = Day13.BusScheduler.parse(input);
    Optional<Integer> actualProduct = scheduler.getEarliestBusIdMultipliedByWaitingTime();
    assertThat(actualProduct).hasValue(expectedProduct);
  }

  @ParameterizedTest
  @CsvSource({
      "example, 1068781",
      "real, 600691418730595",
  })
  void testCalcEarliestTimestamp(@ResourceFromClassPathArgument List<String> input, long expectedTimestamp) {
    List<Day13.NumberWithOffset> numbersWithRemainders = Day13.parseNumbersWithRemainder(input);
    long actualTimestamp = Day13.calcEarliestTimestamp(numbersWithRemainders);
    assertThat(actualTimestamp).isEqualTo(expectedTimestamp);
  }

}