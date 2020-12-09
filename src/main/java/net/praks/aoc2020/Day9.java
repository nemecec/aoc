package net.praks.aoc2020;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.stream.Collectors;

/**
 * <a href="https://adventofcode.com/2020/day/9">Day 9: Encoding Error</a>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Day9 {

  @Value
  static class NumberList {

    List<Long> numbers;

    static NumberList parse(List<String> numbersAsStrings) {
      return new NumberList(numbersAsStrings.stream().map(Long::parseLong).collect(Collectors.toList()));
    }

    Optional<Long> findEncodingError(int bufferSize) {
      XmasBuffer buffer = new XmasBuffer(bufferSize);
      return numbers.stream().filter(value -> {
        boolean hasSum = !buffer.hasSum(value);
        buffer.addValue(value);
        return hasSum;
      }).findFirst();
    }

    Optional<NumberRange> findRangeThatSumsUpTo(long sumToFind) {
      for (int rangeStartIndex = 0; rangeStartIndex < numbers.size(); rangeStartIndex++) {
        long currentSum = numbers.get(rangeStartIndex);
        for (int rangeEndIndex = rangeStartIndex + 1; rangeEndIndex < numbers.size(); rangeEndIndex++) {
          currentSum += numbers.get(rangeEndIndex);
          if (rangeEndIndex - rangeStartIndex > 1 && currentSum == sumToFind) {
            return Optional.of(new NumberRange(numbers.subList(rangeStartIndex, rangeEndIndex + 1)));
          }
          if (currentSum > sumToFind) {
            // no point in looking further, the sum is already larger than we need
            break;
          }
        }
      }
      return Optional.empty();
    }

  }

  @Value
  static class NumberRange {

    List<Long> numbers;

    OptionalLong sumSmallestAndLargestValues() {
      OptionalLong min = numbers.stream().mapToLong(Long::longValue).min();
      OptionalLong max = numbers.stream().mapToLong(Long::longValue).max();
      if (min.isPresent()) {
        return OptionalLong.of(min.getAsLong() + max.getAsLong());
      }
      else {
        return OptionalLong.empty();
      }
    }

  }

  @RequiredArgsConstructor
  private static class XmasBuffer {

    private final int maxSize;
    Deque<Long> buffer = new LinkedList<>();

    void addValue(long value) {
      buffer.addLast(value);
      if (buffer.size() > maxSize) {
        buffer.removeFirst();
      }
    }

    boolean hasSum(long sum) {
      if (buffer.size() < maxSize) {
        return true;
      }
      for (long add1 : buffer) {
        for (long add2 : buffer) {
          if (add1 != add2 && add1 + add2 == sum) {
            return true;
          }
        }
      }
      return false;
    }

  }

}
