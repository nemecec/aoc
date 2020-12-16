package net.praks.aoc2020;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;

/**
 * <a href="https://adventofcode.com/2020/day/15">Day 15: Rambunctious Recitation</a>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Day15 {

  static class MemoryGame {

    private static final MemoryNumberStatistics DEFAULT_VALUE = new MemoryNumberStatistics();
    // key: spoken number, value: statistics about those spoken numbers
    private final Map<Integer, MemoryNumberStatistics> countMap = new HashMap<>();
    private int mostRecentNumber;
    private int turnCounter;

    public MemoryGame(List<Integer> initialNumbers) {
      initialNumbers.forEach(this::newTurn);
    }

    private Integer newTurn(int value) {
      countMap.computeIfAbsent(value, k -> new MemoryNumberStatistics()).newTurn(++turnCounter);
      mostRecentNumber = value;
      return value;
    }

    int nextTurn() {
      MemoryNumberStatistics statistics = countMap.getOrDefault(mostRecentNumber, DEFAULT_VALUE);
      if (statistics.isFirstTimeSpoken()) {
        return newTurn(0);
      }
      else {
        return newTurn(statistics.howManyTurnsApart());
      }
    }

    OptionalInt nextNumberUntilTurn(int turnNumberToEndAt) {
      OptionalInt lastNumber = OptionalInt.empty();
      while (turnCounter < turnNumberToEndAt) {
        lastNumber = OptionalInt.of(nextTurn());
      }
      return lastNumber;
    }

  }

  @Value
  static class MemoryNumberStatistics {
    Deque<Integer> spokenTurns = new ArrayDeque<>(2);

    void newTurn(int spokenTurn) {
      if (spokenTurns.size() > 1) {
        spokenTurns.removeFirst();
      }
      spokenTurns.addLast(spokenTurn);
    }

    boolean isFirstTimeSpoken() {
      return spokenTurns.size() == 1;
    }

    int howManyTurnsApart() {
      return spokenTurns.getLast() - spokenTurns.getFirst();
    }

  }

}
