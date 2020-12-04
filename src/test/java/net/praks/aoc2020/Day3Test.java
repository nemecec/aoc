package net.praks.aoc2020;

import com.google.common.io.CharStreams;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
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
  void test(String caseName, String slopeStr, long expectedTotalProduct) throws IOException {
    try (Reader reader = new InputStreamReader(ResourceUtil.getResource(getClass(),caseName))) {
      Day3.TobogganMap tobogganMap = Day3.TobogganMap.parse(CharStreams.readLines(reader));
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

}