package net.praks.aoc2020;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * <a href="https://adventofcode.com/2020/day/13">Day 13: Shuttle Search</a>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Day13 {

  private static final Pattern BUS_LIST_SEPARATOR = Pattern.compile(",");

  public static long calcEarliestTimestamp(List<NumberWithOffset> numbers) {
    return ChineseRemainderTheorem.chineseRemainder(
        numbers.stream().mapToInt(NumberWithOffset::getNumber).toArray(),
        numbers.stream().mapToInt(num -> num.getNumber() - num.getOffset()).toArray()
    );
  }

  // Adapted from https://rosettacode.org/wiki/Chinese_remainder_theorem
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  static class ChineseRemainderTheorem {

    public static long chineseRemainder(int[] n, int[] a) {

      long prod = Arrays.stream(n).asLongStream().reduce(1, (i, j) -> i * j);

      long p;
      long sm = 0;
      for (int i = 0; i < n.length; i++) {
        p = prod / n[i];
        sm += a[i] * mulInv(p, n[i]) * p;
      }
      return sm % prod;
    }

    private static long mulInv(long a, long b) {
      long b0 = b;
      long x0 = 0;
      long x1 = 1;

      if (b == 1)
        return 1;

      while (a > 1) {
        long q = a / b;
        long amb = a % b;
        a = b;
        b = amb;
        long xqx = x1 - q * x0;
        x1 = x0;
        x0 = xqx;
      }

      if (x1 < 0)
        x1 += b0;

      return x1;
    }

  }

  static List<NumberWithOffset> parseNumbersWithRemainder(List<String> notes) {
    String numbersLine = notes.get(1);
    String[] numbersAsString = BUS_LIST_SEPARATOR.split(numbersLine);
    List<NumberWithOffset> numbers = new ArrayList<>();
    for (int i = 0; i < numbersAsString.length; i++) {
      String numberAsString = numbersAsString[i];
      if (!numberAsString.equals("x")) {
        numbers.add(new NumberWithOffset(Integer.parseInt(numberAsString), i));
      }
    }
    return numbers;
  }

  @Value
  static class NumberWithOffset {
    int number;
    int offset;
  }

  @Value
  static class BusScheduler {

    long earliestDepartureTimestamp;
    List<Bus> availableBuses;

    static BusScheduler parse(List<String> notes) {
      return new BusScheduler(
          Integer.parseInt(notes.get(0)),
          Bus.parseBusList(notes.get(1))
      );
    }

    Optional<Integer> getEarliestBusIdMultipliedByWaitingTime() {
      return findEarliestBus()
          .map(bus -> bus.waitingTimeUntilNextDepartureAtOrAfter(earliestDepartureTimestamp) * bus.getId());
    }

    Optional<Bus> findEarliestBus() {
      return availableBuses.stream()
          .min(Comparator.comparingInt(bus -> bus.waitingTimeUntilNextDepartureAtOrAfter(earliestDepartureTimestamp)));

    }

  }

  @Value
  static class Bus {

    int id;

    int waitingTimeUntilNextDepartureAtOrAfter(long timestamp) {
      int minutesSinceLastDeparture = Math.toIntExact(timestamp % id);
      return minutesSinceLastDeparture == 0 ? 0 : id - minutesSinceLastDeparture;
    }

    static List<Bus> parseBusList(String note) {
      return Arrays.stream(BUS_LIST_SEPARATOR.split(note))
          .filter(str -> !str.equals("x"))
          .map(Integer::parseInt)
          .map(Bus::new)
          .collect(Collectors.toList());
    }

  }

}
