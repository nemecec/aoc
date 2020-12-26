package net.praks.aoc2020;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.OptionalLong;

/**
 * <a href="https://adventofcode.com/2020/day/25">Day 25: Combo Breaker</a>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Day25 {

  private static final int MAGIC_VALUE = 20201227;

  static long calculatePublicKey(long subjectNumber, long loopSize) {
    long result = 1;
    for (long i = 0; i < loopSize; i++) {
      result *= subjectNumber;
      result = result % MAGIC_VALUE;
    }
    return result;
  }

  static OptionalLong guessLoopSize(long subjectNumber, long publicKey) {
    long publicKeyCandidate = 1;
    for (long loopSize = 1; loopSize < Long.MAX_VALUE; loopSize++) {
      publicKeyCandidate *= subjectNumber;
      publicKeyCandidate = publicKeyCandidate % MAGIC_VALUE;
      if (publicKeyCandidate == publicKey) {
        return OptionalLong.of(loopSize);
      }
    }
    return OptionalLong.empty();
  }

}
