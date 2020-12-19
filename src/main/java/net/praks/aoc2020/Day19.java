package net.praks.aoc2020;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * <a href="https://adventofcode.com/2020/day/19">Day 19: Monster Messages</a>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Day19 {

  @Value
  static class RulesAndMessages {

    RuleStore ruleStore;
    MessageStore messageStore;

    static RulesAndMessages parse(List<String> lines) {
      List<Rule> ruleList = new ArrayList<>();
      List<String> messages = null;
      for (String line : lines) {
        if (line.length() == 0) {
          messages = new ArrayList<>();
        }
        else if (messages == null) {
          ruleList.add(Rule.parse(line));
        }
        else {
          messages.add(line);
        }
      }
      return new RulesAndMessages(RuleStore.newStore(ruleList), new MessageStore(messages));
    }

    long countValidMessages(int ruleId) {
      String patternStr = ruleStore.getRule(ruleId).toRegExp(ruleStore);
      Pattern pattern = Pattern.compile("^" + patternStr + "$");
      Predicate<String> valueFilter = pattern.asPredicate();
      return messageStore.getMessages().stream().filter(valueFilter).count();
    }

  }

  @Value
  static class RuleStore {

    Map<Integer, Rule> rules;

    static RuleStore newStore(List<Rule> ruleList) {
      return new RuleStore(ruleList.stream().collect(Collectors.toMap(Rule::getId, rule -> rule)));
    }

    Rule getRule(Integer ruleId) {
      return rules.computeIfAbsent(ruleId, key -> {throw new IllegalArgumentException("Unknown rule: " + ruleId);});
    }

  }

  @Value
  static class Rule {

    private static final Pattern PATTERN = Pattern.compile("(\\d+):\\s+(.+)");
    private static final Pattern WORD = Pattern.compile("\"(\\w+)\"");
    private static final Pattern CONSECUTIVE_SPLIT = Pattern.compile("\\s+");
    private static final Pattern OR_SPLIT = Pattern.compile("\\s+\\|\\s+");

    Integer id;
    String definition;

    static Rule parse(String ruleStr) {
      Matcher matcher = PATTERN.matcher(ruleStr);
      if (matcher.matches()) {
        return new Rule(Integer.valueOf(matcher.group(1)), matcher.group(2));
      }
      else {
        throw new IllegalArgumentException("Illegal rule syntax: " + ruleStr);
      }
    }

    private String toRegExp(RuleStore store) {
      Matcher matcher = WORD.matcher(definition);
      if (matcher.matches()) {
        return matcher.group(1);
      }
      String[] elements = OR_SPLIT.split(definition);
      if (elements.length > 1) {
        return orRule(store, elements);
      }
      elements = CONSECUTIVE_SPLIT.split(definition);
      if (elements.length > 0) {
        return Arrays.stream(elements).map(
            ruleIdStr -> store.getRule(Integer.valueOf(ruleIdStr)).toRegExp(store)
        ).collect(Collectors.joining(""));
      }
      else {
        throw new IllegalArgumentException("Illegal rule pattern: " + definition);
      }
    }

    private String orRule(RuleStore store, String[] elements) {
      if (elements.length != 2) {
        throw new IllegalArgumentException("Only one OR allowed: " + definition);
      }
      List<List<Integer>> ruleIds = Arrays.stream(elements).map(
          subList -> Arrays.stream(CONSECUTIVE_SPLIT.split(subList)).map(Integer::valueOf).collect(Collectors.toList())
      ).collect(Collectors.toList());
      // Check for recursion
      if (ruleIds.stream().flatMap(Collection::stream).anyMatch(v -> v.equals(getId()))) {
        return orRuleWithRecursion(store, ruleIds);
      }
      else {
        // No recursion
        return ruleIds.stream().map(
            subList -> subList.stream().map(
                ruleId -> store.getRule(ruleId).toRegExp(store)
            ).collect(Collectors.joining(""))
        ).collect(Collectors.joining("|", "(?:", ")"));
      }
    }

    private String orRuleWithRecursion(RuleStore store, List<List<Integer>> ruleIds) {
      List<Integer> rulesOnLeft = ruleIds.get(0);
      List<Integer> rulesOnRight = ruleIds.get(1);
      // We only support specific recursion patterns
      boolean containsAll = rulesOnRight.containsAll(rulesOnLeft);
      if (rulesOnLeft.size() == 1 && rulesOnRight.size() == 2 && containsAll) {
        return store.getRule(rulesOnLeft.get(0)).toRegExp(store) + "+";
      }
      else if (rulesOnLeft.size() == 2 && rulesOnRight.size() == 3 && containsAll &&
          rulesOnRight.get(1).equals(getId())) {
        String regexpOnLeft = store.getRule(rulesOnLeft.get(0)).toRegExp(store);
        String regexpOnRight = store.getRule(rulesOnLeft.get(1)).toRegExp(store);
        // 4 is a magic value suitable for the given dataset - the pattern does not repeat more than that
        return IntStream.rangeClosed(1, 4)
            .mapToObj(n -> regexpOnLeft + "{" + n + "}" + regexpOnRight + "{" + n + "}")
            .collect(Collectors.joining("|", "(?:", ")"));
      }
      else {
        throw new IllegalArgumentException("Recursion pattern not supported: " + definition);
      }
    }

  }

  @Value
  static class MessageStore {

    List<String> messages;

  }

}
