package net.praks.aoc2020;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class Day18Test {

  @ParameterizedTest
  @CsvSource({
      "example, 26335",
      "real, 4491283311856",
  })
  void testSumExpressions(@ResourceFromClassPathArgument List<String> input, long expectedSum) {
    Day18.ExpressionList expressions = Day18.parse(input);
    long actualSum = expressions.evaluate(new Day18.EvaluationContext());
    assertAll(
        () -> assertThat(expressions).hasToString(String.join("\n", input)),
        () -> assertThat(actualSum).isEqualTo(expectedSum)
    );
  }

  @ParameterizedTest
  @CsvSource({
      "example, 693891",
      "real, 68852578641904",
  })
  void testSumExpressionsWithPrecedence(@ResourceFromClassPathArgument List<String> input, long expectedSum) {
    Day18.ExpressionList expressions = Day18.parse(input);
    long actualSum = expressions.evaluate(getContextWithPrecedence());
    assertAll(
        () -> assertThat(expressions).hasToString(String.join("\n", input)),
        () -> assertThat(actualSum).isEqualTo(expectedSum)
    );
  }

  @ParameterizedTest
  @CsvSource({
      "1 + 2 * 3 + 4 * 5 + 6, 71",
      "1 + (2 * 3) + (4 * (5 + 6)), 51",
      "2 * 3 + (4 * 5), 26",
      "5 + (8 * 3 + 9 + 3 * 4 * 3), 437",
      "5 * 9 * (7 * 3 * 3 + 9 * 3 + (8 + 6 * 4)), 12240",
      "((2 + 4 * 9) * (6 + 9 * 8 + 6) + 6) + 2 + 4 * 2, 13632",
      "5 + (8 + 7 + (6 + 8 * 6 + 8 + 4) + (7 * 3 + 4 * 6) + 6 + 4), 276",
  })
  void testParseAndEvaluate(String expressionStr, long expectedResult) {
    long actualResult = Day18.Evaluator.evaluate(expressionStr, new Day18.EvaluationContext());
    assertThat(actualResult).isEqualTo(expectedResult);
  }

  @ParameterizedTest
  @CsvSource({
      "1 + (2 * 3) + (4 * (5 + 6)), 51",
      "2 * 3 + (4 * 5), 46",
      "5 + (8 * 3 + 9 + 3 * 4 * 3), 1445",
      "5 * 9 * (7 * 3 * 3 + 9 * 3 + (8 + 6 * 4)), 669060",
      "((2 + 4 * 9) * (6 + 9 * 8 + 6) + 6) + 2 + 4 * 2, 23340",
      "5 + (8 + 7 + (6 + 8 * 6 + 8 + 4) + (7 * 3 + 4 * 6) + 6 + 4), 576",
      "(3 * (4 * 8) * 5 * 7 * 3) + 8, 10088",
      "5 * (6 * (7 + 9)) + (9 + 9 + 2) + (2 * 2 + (2 * 9 * 6 + 8 * 2) + 4) * 6 + 2, 45440"
  })
  void testParseAndEvaluateWithPrecedence(String expressionStr, long expectedResult) {
    long actualResult = Day18.Evaluator.evaluate(expressionStr, getContextWithPrecedence());
    assertThat(actualResult).isEqualTo(expectedResult);
  }

  private Day18.EvaluationContext getContextWithPrecedence() {
    return new Day18.EvaluationContext()
        .addOperatorPrecedence(Day18.Operator.ADDITION)
        .addOperatorPrecedence(Day18.Operator.MULTIPLICATION);
  }

}