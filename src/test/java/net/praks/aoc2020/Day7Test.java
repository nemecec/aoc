package net.praks.aoc2020;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class Day7Test {

  private static final String SHINY_GOLD = "shiny gold";

  @ParameterizedTest
  @CsvSource({
      "example, 4",
      "real, 124",
  })
  void testCountOutermostBags(@ResourceFromClassPathArgument List<String> input, long expectedCount) {
    long actualCount = new Day7(input).countAllOuterMostBags(SHINY_GOLD);
    assertThat(actualCount).isEqualTo(expectedCount);
  }

  @ParameterizedTest
  @CsvSource({
      "example, 32",
      "example2, 126",
      "real, 34862",
  })
  void testCountInnerBags(@ResourceFromClassPathArgument List<String> input, long expectedCount) {
    long actualCount = new Day7(input).countAllInnerBags(SHINY_GOLD);
    assertThat(actualCount).isEqualTo(expectedCount);
  }

  @Nested
  class BagDefinitionParsing {

    private final Day7.BagFactory factory = new Day7.BagFactory();

    @Test
    void canContainNone() {
      // given
      String bagDefinition = "dim magenta bags contain no other bags.";
      String expectedColorName = "dim magenta";
      List<Day7.BagWithCount> expectedCanContain = Collections.emptyList();
      Day7.Bag expectedBag = new Day7.Bag(factory, expectedColorName, expectedCanContain);

      // when
      Day7.Bag actualBag = Day7.Bag.parse(bagDefinition, factory);

      // then
      assertThat(actualBag).isEqualTo(expectedBag);
    }

    @Test
    void canContainOne() {
      // given
      String bagDefinition = "vibrant violet bags contain 1 pale maroon bag.";
      String expectedColorName = "vibrant violet";
      List<Day7.BagWithCount> expectedCanContain = Collections.singletonList(
          new Day7.BagWithCount(1, factory.getBag("pale maroon"))
      );
      Day7.Bag expectedBag = new Day7.Bag(factory, expectedColorName, expectedCanContain);

      // when
      Day7.Bag actualBag = Day7.Bag.parse(bagDefinition, factory);

      // then
      assertThat(actualBag).isEqualTo(expectedBag);
    }

    @Test
    void canContainMany() {
      // given
      String bagDefinition = "vibrant aqua bags contain 1 shiny magenta bag, 2 muted teal bags, 1 dim magenta bag, 3 muted chartreuse bags.";
      String expectedColorName = "vibrant aqua";
      List<Day7.BagWithCount> expectedCanContain = Arrays.asList(
          new Day7.BagWithCount(1, factory.getBag("shiny magenta")),
          new Day7.BagWithCount(2, factory.getBag("muted teal")),
          new Day7.BagWithCount(1, factory.getBag("dim magenta")),
          new Day7.BagWithCount(3, factory.getBag("muted chartreuse"))
      );
      Day7.Bag expectedBag = new Day7.Bag(factory, expectedColorName, expectedCanContain);

      // when
      Day7.Bag actualBag = Day7.Bag.parse(bagDefinition, factory);

      // then
      assertThat(actualBag).isEqualTo(expectedBag);
    }

  }

}