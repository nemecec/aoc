package net.praks.aoc2020;

import com.google.common.io.CharStreams;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import static org.assertj.core.api.Assertions.assertThat;

class Day3Test {

  @ParameterizedTest
  @CsvSource({
      "part1-small, 3/1, 7",
      "part1, 3/1, 272"
  })
  void test(String caseName, String slopeStr, int expectedTreeCount) throws IOException {
    try (Reader reader = new InputStreamReader(ResourceUtil.getResource(getClass(),caseName))) {
      Day3.Map map = Day3.Map.parse(CharStreams.readLines(reader));
      String[] slopeStrings = slopeStr.split("/");
      int columnInc = Integer.parseInt(slopeStrings[0]);
      int rowInc = Integer.parseInt(slopeStrings[1]);
      int actualTreeCount = new Day3().countTreesForSlope(columnInc, rowInc, map);
      assertThat(actualTreeCount).isEqualTo(expectedTreeCount);
    }
  }

}