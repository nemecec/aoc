package net.praks.aoc2020;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <a href="https://adventofcode.com/2020/day/6">Day 6: Custom Customs</a>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Day6 {

  @Value
  static class PlaneAnswers {

    List<GroupAnswers> groups;

    static PlaneAnswers parse(String groupAnswersStr) {
      return new PlaneAnswers(
          Arrays.stream(groupAnswersStr.split("\\n\\n"))
              .map(GroupAnswers::parse)
              .collect(Collectors.toList())
      );
    }

    long sumOfUniqueAnswersInEachGroup() {
      return groups.stream().mapToLong(GroupAnswers::uniqueAnswersCount).sum();
    }

    long sumOfCommonAnswersInEachGroup() {
      return groups.stream().mapToLong(GroupAnswers::commonAnswersCount).sum();
    }

  }

  @Value
  static class GroupAnswers {

    List<AnswerList> groups;

    static GroupAnswers parse(String groupAnswersStr) {
      return new GroupAnswers(
          Arrays.stream(groupAnswersStr.split("\\n"))
              .map(AnswerList::parse)
              .collect(Collectors.toList())
      );
    }

    Stream<Character> streamUniqueAnswers() {
      return groups.stream().flatMap(AnswerList::streamUniqueAnswers).distinct();
    }

    Stream<Character> streamCommonAnswers() {
      if (groups.isEmpty()) {
        return Stream.empty();
      }
      else {
        AnswerList firstGroup = groups.get(0);
        return groups.stream()
            .map(AnswerList::getAnswers)
            .skip(1)
            .collect(() -> new TreeSet<>(firstGroup.getAnswers()), Set::retainAll, Set::retainAll).stream();
      }
    }

    long uniqueAnswersCount() {
      return streamUniqueAnswers().count();
    }

    long commonAnswersCount() {
      return streamCommonAnswers().count();
    }

  }

  @Value
  static class AnswerList {
    Set<Character> answers;

    static AnswerList parse(String answerLine) {
      return new AnswerList(answerLine.chars().mapToObj(v -> (char) v).collect(Collectors.toSet()));
    }

    Stream<Character> streamUniqueAnswers() {
      return answers.stream();
    }

    @Override
    public String toString() {
      return answers.stream().map(String::valueOf).reduce("", (a, b) -> a + b);
    }
  }

}
