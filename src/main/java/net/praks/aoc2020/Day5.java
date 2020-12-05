package net.praks.aoc2020;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.List;
import java.util.OptionalInt;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <a href="https://adventofcode.com/2020/day/5">Day 5: Binary Boarding</a>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Day5 {

  static OptionalInt findMissingBoardingPassId(List<String> encodedBoardingPasses) {
    Set<Integer> ids = encodedBoardingPasses.stream()
        .map(BoardingPass::parse)
        .map(BoardingPass::getId)
        .collect(Collectors.toSet());
    Integer lastId = null;
    for (Integer id : ids) {
      if (lastId != null && lastId + 2 == id) {
        return OptionalInt.of(id - 1);
      }
      lastId = id;
    }
    return OptionalInt.empty();
  }

  static OptionalInt findMaxBoardingPassId(List<String> encodedBoardingPasses) {
    return encodedBoardingPasses.stream().map(BoardingPass::parse).mapToInt(BoardingPass::getId).max();
  }

  @RequiredArgsConstructor
  static class BoardingPass {

    private static final BinarySpaceDecoder ROW_DECODER = new BinarySpaceDecoder('F', 'B');
    private static final BinarySpaceDecoder COLUMN_DECODER = new BinarySpaceDecoder('L', 'R');
    private static final int COUNT_OF_ROW_CHARACTERS = 7;

    final int row;
    final int column;

    static BoardingPass parse(String encodedBoardingPass) {
      return new BoardingPass(
          ROW_DECODER.decode(encodedBoardingPass.substring(0, COUNT_OF_ROW_CHARACTERS)),
          COLUMN_DECODER.decode(encodedBoardingPass.substring(COUNT_OF_ROW_CHARACTERS))
      );
    }

    int getId() {
      return row * 8 + column;
    }

  }

  @RequiredArgsConstructor
  static class BinarySpaceDecoder {

    final char lowerHalfIdentifier;
    final char upperHalfIdentifier;

    int decode(String encodedStr) {
      BinarySpacePartition partition = new BinarySpacePartition(0, (int) Math.pow(2, encodedStr.length()));
      for (int i = 0; i < encodedStr.length() && partition.hasNextRange(); i++) {
        char encodedChar = encodedStr.charAt(i);
        partition = partition.subSpace(encodedChar);
      }
      return partition.getRangeMin();
    }

    @Value
    private class BinarySpacePartition {

      int rangeMin;
      int rangeMax;

      BinarySpacePartition subSpace(char halfIdentifier) {
        int midPoint = calculateMidPoint();
        if (halfIdentifier == lowerHalfIdentifier) {
          return new BinarySpacePartition(rangeMin, midPoint);
        }
        else if (halfIdentifier == upperHalfIdentifier) {
          return new BinarySpacePartition(midPoint, rangeMax);
        }
        else {
          throw new IllegalArgumentException("Unrecognized half identifier: " + halfIdentifier);
        }
      }

      int calculateMidPoint() {
        return rangeMin + rangeSize() / 2;
      }

      int rangeSize() {
        return this.rangeMax - rangeMin;
      }

      boolean hasNextRange() {
        return rangeSize() > 1;
      }

      @Override
      public String toString() {
        return rangeMin + "-" + rangeMax;
      }

    }

  }

}
