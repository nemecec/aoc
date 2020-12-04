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
      "example, 1/1;3/1;5/1;7/1;1/2, 336",
      "real, 1/1;3/1;5/1;7/1;1/2, 3898725600"
  })
  void test(String caseName, String slopeStr, long expectedTotalProduct) throws IOException {
    try (Reader reader = new InputStreamReader(ResourceUtil.getResource(getClass(),caseName))) {
      Day3.Map map = Day3.Map.parse(CharStreams.readLines(reader));
      long actualTotalProduct = 1;
      String[] slopeStringPairs = slopeStr.split(";");
      for (String slopeStringPair : slopeStringPairs) {
        String[] slopeStrings = slopeStringPair.split("/");
        int columnInc = Integer.parseInt(slopeStrings[0]);
        int rowInc = Integer.parseInt(slopeStrings[1]);
        int actualTreeCount = new Day3().countTreesForSlope(columnInc, rowInc, map);
        actualTotalProduct *= actualTreeCount;
      }
      assertThat(actualTotalProduct).isEqualTo(expectedTotalProduct);
    }
  }

}