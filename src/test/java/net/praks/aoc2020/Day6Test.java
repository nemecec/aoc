package net.praks.aoc2020;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class Day6Test {

  @ParameterizedTest
  @CsvSource({
      "example, 11, 6",
      "real, 6457, 3260",
  })
  void testAnswersSum(@ResourceFromClassPathArgument String input, long expectedUniqueSum, long expectedCommonSum) {
    Day6.PlaneAnswers planeAnswers = Day6.PlaneAnswers.parse(input);
    long actualUniqueSum = planeAnswers.sumOfUniqueAnswersInEachGroup();
    long actualCommonSum = planeAnswers.sumOfCommonAnswersInEachGroup();

    assertAll(
        () -> assertThat(actualUniqueSum).isEqualTo(expectedUniqueSum),
        () -> assertThat(actualCommonSum).isEqualTo(expectedCommonSum)
    );
  }

  @Test
  void testPlaneAnswersParsing() {
    String input = "abc\nbcd\n\nxyz";
    Day6.PlaneAnswers actual = Day6.PlaneAnswers.parse(input);
    Day6.AnswerList expectedAnswers1 = makeAnswerList('a', 'b', 'c');
    Day6.AnswerList expectedAnswers2 = makeAnswerList('b', 'c', 'd');
    Day6.AnswerList expectedAnswers3 = makeAnswerList('x', 'y', 'z');
    Day6.GroupAnswers expectedGroupAnswers1 = new Day6.GroupAnswers(Arrays.asList(expectedAnswers1, expectedAnswers2));
    Day6.GroupAnswers expectedGroupAnswers2 = new Day6.GroupAnswers(Collections.singletonList(expectedAnswers3));
    Day6.PlaneAnswers expected = new Day6.PlaneAnswers(Arrays.asList(expectedGroupAnswers1, expectedGroupAnswers2));
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  void testGroupAnswersParsing() {
    String input = "abc\nbcd";
    Day6.GroupAnswers actual = Day6.GroupAnswers.parse(input);
    Day6.AnswerList expectedAnswers1 = makeAnswerList('a', 'b', 'c');
    Day6.AnswerList expectedAnswers2 = makeAnswerList('b', 'c', 'd');
    Day6.GroupAnswers expected = new Day6.GroupAnswers(Arrays.asList(expectedAnswers1, expectedAnswers2));
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  void testAnswersParsing() {
    String input = "abc";
    Day6.AnswerList actual = Day6.AnswerList.parse(input);
    Day6.AnswerList expectedAnswers = makeAnswerList('a', 'b', 'c');
    assertThat(actual).isEqualTo(expectedAnswers);
  }

  private Day6.AnswerList makeAnswerList(char a, char b, char c) {
    return new Day6.AnswerList(new TreeSet<>(Arrays.asList(a, b, c)));
  }

}