package net.praks.aoc2020;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <a href="https://adventofcode.com/2020/day/1">Day 1: Report Repair</a>
 */
@RequiredArgsConstructor
class Day1 {

  private final int countOfNumbersToSum;
  private final int sumToFind;

  long findMultipleWithSum(List<String> numbersAsStrings) {
    List<Long> numbers = numbersAsStrings.stream().filter(s -> !s.isEmpty()).map(Long::parseLong).collect(
        Collectors.toList());
    try {
      findSum(numbers, new LinkedList<>(), 0);
    }
    catch (SumFoundException e) {
      return e.getOperands().stream().reduce(1L, (a, b) -> a * b);
    }
    throw new IllegalArgumentException("No match found!");
  }

  private void findSum(List<Long> numbers, Deque<Long> operands, int recursionLevel) throws SumFoundException {
    if (recursionLevel > 0 && recursionLevel == countOfNumbersToSum) {
      if (operands.stream().reduce(Long::sum).orElse(0L) == sumToFind) {
        throw new SumFoundException(new ArrayList<>(operands));
      }
    }
    else {
      for (Long number : numbers) {
        operands.push(number);
        findSum(numbers, operands, recursionLevel + 1);
        operands.pop();
      }
    }
  }

  @Getter
  private static class SumFoundException extends Exception {

    private final List<Long> operands;

    private SumFoundException(List<Long> operands) {
      this.operands = operands;
    }

  }

}
