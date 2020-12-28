package net.praks.aoc2020;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * <a href="https://adventofcode.com/2020/day/23">Day 23: Crab Cups</a>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Day23 {

  static class CupCircle {

    private static final int CUPS_TO_MOVE = 3;
    private final int[] cups1;
    private final int[] cups2;
    private final int cupCount;
    private boolean useCups1 = true;
    private int currentCupIndex;

    public CupCircle(int size) {
      this.cups1 = new int[size];
      this.cups2 = new int[size];
      this.cupCount = size;
    }

    static CupCircle parse(String input) {
      return parse(input, input.length());
    }

    static CupCircle parse(String input, int size) {
      CupCircle circle = new CupCircle(size);
      int maxValue = 0;
      for (int i = 0; i < input.length(); i++) {
        char c = input.charAt(i);
        int value = c - '0';
        circle.cups1[i] = value;
        maxValue = Math.max(maxValue, value);
      }
      for (int i = maxValue; i < size; i++) {
        circle.cups1[i] = i + 1;
      }
      return circle;
    }

    int getCupIndex(int label) {
      int[] cups = getCups();
      for (int i = 0; i < cups.length; i++) {
        if (cups[i] == label) {
          return i;
        }
      }
      throw new IllegalArgumentException(String.format("Cannot find cup %s in %s", label, this));
    }

    int getCup(int index) {
      return getCups()[index];
    }

    int[] getCups(int startIndex, int count) {
      int[] results = new int[count];
      int[] cups = getCups();
      for (int i = startIndex; i < startIndex + count; i++) {
        results[i - startIndex] = cups[i % cups.length];
      }
      return results;
    }

    int[] getCupsStartingAfter(int cupLabel) {
      int cupIndex = getCupIndex(cupLabel);
      return getCups((cupIndex + 1) % cupCount, cupCount - 1);
    }

    long productOfTwoLabelsNextFrom(int label) {
      int cupIndex = getCupIndex(label);
      int[] cups = getCups((cupIndex + 1) % cupCount, 2);
      return cups[0] * (long) cups[1];
    }

    void moveCups(int fromIndex, int toIndex, int numOfCups) {
      if (fromIndex == toIndex) {
        return;
      }
      if (fromIndex + numOfCups > cupCount) {
        moveFromEndToBeginning(fromIndex);
        int increment = cupCount - fromIndex;
        toIndex += increment;
        currentCupIndex += increment;
        fromIndex = 0;
      }
      int[] fromCups = getCups();
      int[] toCups = getOtherCups();
      int mutationStartIndex;
      int mutationMidIndex;
      int mutationEndIndex;
      if (fromIndex < toIndex) {
        mutationStartIndex = fromIndex;
        mutationMidIndex = fromIndex + numOfCups;
        mutationEndIndex = toIndex;
        if (currentCupIndex >= mutationMidIndex && currentCupIndex < mutationEndIndex) {
          currentCupIndex -= numOfCups;
        }
      }
      else {
        mutationStartIndex = toIndex;
        mutationMidIndex = fromIndex;
        mutationEndIndex = fromIndex + numOfCups;
        if (currentCupIndex >= mutationStartIndex && currentCupIndex < mutationMidIndex) {
          currentCupIndex += numOfCups;
        }
      }
      System.arraycopy(fromCups, 0, toCups, 0, mutationStartIndex);
      System.arraycopy(fromCups, mutationStartIndex, toCups, mutationStartIndex + (mutationEndIndex - mutationMidIndex), mutationMidIndex - mutationStartIndex);
      System.arraycopy(fromCups, mutationMidIndex, toCups, mutationStartIndex, mutationEndIndex - mutationMidIndex);
      System.arraycopy(fromCups, mutationEndIndex, toCups, mutationEndIndex, fromCups.length - mutationEndIndex);
      flipCupsArray();
    }

    private void moveFromEndToBeginning(int fromIndex) {
      int[] fromCups = getCups();
      int[] toCups = getOtherCups();
      int countOfCupsToCopy = fromCups.length - fromIndex;
      System.arraycopy(fromCups, fromIndex, toCups, 0, countOfCupsToCopy);
      System.arraycopy(fromCups, 0, toCups, countOfCupsToCopy, fromCups.length - countOfCupsToCopy);
      flipCupsArray();
    }

    public void makeMoves(long numberOfMoves) {
      long totalStartTime = System.currentTimeMillis();
      long startTime = System.currentTimeMillis();
      for (long i = 1; i <= numberOfMoves; i++) {
        move(i, numberOfMoves < 100);
        if (i % 10000 == 0) {
          long currentTime = System.currentTimeMillis();
          System.out.printf("Made %s moves in %s (%s) seconds%n", i, (currentTime - startTime) / 1000, (currentTime - totalStartTime) / 1000);
          startTime = currentTime;
        }
      }
    }

    private void move(long moveNumber, boolean isDebugEnabled) {
      if (isDebugEnabled) {
        System.out.printf("-- move %s --%n", moveNumber);
        System.out.printf("cups: %s%n", toStringWithCurrentCup());
      }
      int currentCupLabel = getCup(currentCupIndex);
      int[] pickedUpCups = getCups(currentCupIndex + 1, CUPS_TO_MOVE);
      if (isDebugEnabled) {
        System.out.printf("pick up: %s%n", Arrays.stream(pickedUpCups).mapToObj(String::valueOf).collect(Collectors.joining(", ")));
      }
      Arrays.sort(pickedUpCups);
      int destinationCupLabel = currentCupLabel > 1 ? currentCupLabel - 1 : cupCount;
      while (Arrays.binarySearch(pickedUpCups, destinationCupLabel) >= 0) {
        if (--destinationCupLabel < 1) {
          destinationCupLabel = cupCount;
        }
      }
      int destinationCupIndex = getCupIndex(destinationCupLabel);
      if (isDebugEnabled) {
        System.out.printf("destination: %s (%s)%n%n", destinationCupLabel, destinationCupIndex);
      }
      moveCups((currentCupIndex + 1) % cupCount, (destinationCupIndex + 1) % cupCount, CUPS_TO_MOVE);
      this.currentCupIndex = (currentCupIndex + 1) % cupCount;
    }

    private int[] getCups() {
      return useCups1 ? cups1 : cups2;
    }

    private int[] getOtherCups() {
      return useCups1 ? cups2 : cups1;
    }

    private void flipCupsArray() {
      this.useCups1 = !this.useCups1;
    }

    @Override
    public String toString() {
      return toString("");
    }

    public String toString(String delimiter) {
      int[] cups = getCups();
      if (cups.length > 100) {
        return cups.length + " cups";
      }
      return Arrays.stream(cups).mapToObj(String::valueOf).collect(Collectors.joining(delimiter));
    }

    public String toStringWithCurrentCup() {
      int[] cups = getCups();
      if (cups.length > 100) {
        return cups.length + " cups";
      }
      StringBuilder sb = new StringBuilder(cups.length * 2 + 2);
      for (int i = 0; i < cups.length; i++) {
        int cup = cups[i];
        if (i > 0) {
          sb.append(" ");
        }
        if (i == currentCupIndex) {
          sb.append("(");
        }
        sb.append(cup);
        if (i == currentCupIndex) {
          sb.append(")");
        }
      }
      return sb.toString();
    }

  }

}
