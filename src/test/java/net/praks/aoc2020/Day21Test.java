package net.praks.aoc2020;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class Day21Test {

  @ParameterizedTest
  @CsvSource({
      "example, 5, 'mxmxvkd,sqjhc,fvjkl'",
      "real, 2556, 'vcckp,hjz,nhvprqb,jhtfzk,mgkhhc,qbgbmc,bzcrknb,zmh'",
  })
  void testCountHarmless(
      @ResourceFromClassPathArgument List<String> input,
      long expectedCount,
      String expectedCanonicalDangerousIngredientList
  ) {
    Day21.FoodList foodList = Day21.FoodList.parse(input);
    Day21.FactMap factMap = foodList.calculateFactMap();
    long actualCount = foodList.countIngredientsWithNoAllergens(factMap);
    assertAll(
        () -> assertThat(foodList).hasToString(String.join("\n", input)),
        () -> assertThat(actualCount).isEqualTo(expectedCount),
        () -> assertThat(factMap.getCanonicalDangerousIngredientList())
            .isEqualTo(expectedCanonicalDangerousIngredientList)
    );
  }

}