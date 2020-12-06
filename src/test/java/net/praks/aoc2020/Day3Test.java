package net.praks.aoc2020;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class Day3Test {

  @ParameterizedTest
  @CsvSource({
      "example, 3/1, 7",
      "real, 3/1, 272",
      "example, 1/1;3/1;5/1;7/1;1/2, 336",
      "real, 1/1;3/1;5/1;7/1;1/2, 3898725600",
  })
  void test(@ResourceFromClassPathArgument List<String> input, String slopeStr, long expectedTotalProduct) {
    Day3.TobogganMap tobogganMap = Day3.TobogganMap.parse(input);
    List<Day3.Slope> slopes = Arrays.stream(slopeStr.split(";"))
        .map(slopeStringPair -> {
          String[] slopeStrings = slopeStringPair.split("/");
          int columnInc = Integer.parseInt(slopeStrings[0]);
          int rowInc = Integer.parseInt(slopeStrings[1]);
          return new Day3.Slope(columnInc, rowInc);
        }).collect(Collectors.toList());
    long actualTotalProduct = Day3.multiplyTreeCountScenarios(tobogganMap, slopes);
    assertThat(actualTotalProduct).isEqualTo(expectedTotalProduct);
  }

}