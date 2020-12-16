package net.praks.aoc2020;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

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
    private final Map<Integer, MemoryNumberStatistics> countMap = new HashMap<>(1000000);
    private int mostRecentNumber;
    private int turnCounter;

    public MemoryGame(List<Integer> initialNumbers) {
      initialNumbers.forEach(this::newTurn);
    }

    private int newTurn(int value) {
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
      int lastNumber = -1;
      while (turnCounter < turnNumberToEndAt) {
        lastNumber = nextTurn();
      }
      return lastNumber > -1 ? OptionalInt.of(lastNumber) : OptionalInt.empty();
    }

  }

  static class MemoryNumberStatistics {
    int firstTurn = -1;
    int lastTurn = -1;

    void newTurn(int spokenTurn) {
      firstTurn = lastTurn;
      lastTurn = spokenTurn;
    }

    boolean isFirstTimeSpoken() {
      return firstTurn == -1 && lastTurn != -1;
    }

    int howManyTurnsApart() {
      return lastTurn - firstTurn;
    }

  }

}
