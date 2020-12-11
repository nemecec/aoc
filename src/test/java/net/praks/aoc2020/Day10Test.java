package net.praks.aoc2020;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class Day10Test {

  private static final int STARTING_JOLTS = 0;
  private static final int MIN_JOLTS_DIFF = 1;
  private static final int MAX_JOLTS_DIFF = 3;

  @ParameterizedTest
  @CsvSource({
      "short, 3, 4",
      "example, 35, 8",
      "example2, 220, 19208",
      "example3, 12, 48",
      "real, 2470, 1973822685184",
  })
  void testJoltDifferences(@ResourceFromClassPathArgument List<String> input, int expectedProduct, long expectedDistinctCount) {
    Day10.AdapterList adapterList = Day10.AdapterList.parse(input);
    if (expectedDistinctCount < 10) {
      // In case of small number of combinations, print out all possible combinations while traversing the tree
      adapterList.setDebugEnabled(true);
    }
    Day10.JoltDifferences actualJoltDifferences = adapterList.summarizeDifferences(STARTING_JOLTS, MAX_JOLTS_DIFF);
    int actualMinMaxProduct = actualJoltDifferences.multiplyDifferences(MIN_JOLTS_DIFF, MAX_JOLTS_DIFF);
    long actualDistinctCombinations = adapterList.countDistinctCombinationsWithMath(STARTING_JOLTS, MAX_JOLTS_DIFF);
    assertAll(
        () -> assertThat(actualMinMaxProduct).isEqualTo(expectedProduct),
        () -> assertThat(actualDistinctCombinations).isEqualTo(expectedDistinctCount)
    );
    // Counting with tree is too time consuming with the largest dataset, so we skip it
    if (expectedDistinctCount < 100_000) {
      assertThat(adapterList.countDistinctCombinationsWithTree(STARTING_JOLTS, MAX_JOLTS_DIFF))
          .isEqualTo(expectedDistinctCount);
    }
  }

}