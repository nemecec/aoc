package net.praks.aoc2020;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.OptionalLong;

import static org.assertj.core.api.Assertions.assertThat;

class Day25Test {

  @ParameterizedTest
  @CsvSource({
      "7, 8, 5764801",
      "7, 11, 17807724",
      "17807724, 8, 14897079",
      "5764801, 11, 14897079",
      "11404017, 8516638, 18862163",
      "13768789, 11710225, 18862163",
  })
  void testCalculatePublicKey(long givenSubjectNumber, long givenLoopSize, long expectedPublicKey) {
    long actualPublicKey = Day25.calculatePublicKey(givenSubjectNumber, givenLoopSize);
    assertThat(actualPublicKey).isEqualTo(expectedPublicKey);
  }

  @ParameterizedTest
  @CsvSource({
      "7, 5764801, 8",
      "7, 17807724, 11",
      "7, 11404017, 11710225",
      "7, 13768789, 8516638",
  })
  void testGuessLoopSize(long givenSubjectNumber, long givenPublicKey, long expectedLoopSize) {
    OptionalLong actualLoopSize = Day25.guessLoopSize(givenSubjectNumber, givenPublicKey);
    assertThat(actualLoopSize).hasValue(expectedLoopSize);
  }

}