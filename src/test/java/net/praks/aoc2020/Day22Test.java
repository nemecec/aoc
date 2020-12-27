package net.praks.aoc2020;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class Day22Test {

  @ParameterizedTest
  @CsvSource({
      "example, 306",
      "real, 29764",
  })
  void testPlayGame(@ResourceFromClassPathArgument List<String> input, long expectedScore) {
    Day22.Combat game = Day22.Combat.parse(input);
    assertThat(game).hasToString(String.join("\n", input));
    long actualScore = game.play();
    assertThat(actualScore).isEqualTo(expectedScore);
  }

  @ParameterizedTest
  @CsvSource({
      "example, 291",
      "real, 32588",
  })
  void testPlayGameRecursively(@ResourceFromClassPathArgument List<String> input, long expectedScore) {
    Day22.Combat game = Day22.Combat.parse(input);
    assertThat(game).hasToString(String.join("\n", input));
    long actualScore = game.playRecursively();
    assertThat(actualScore).isEqualTo(expectedScore);
  }

}