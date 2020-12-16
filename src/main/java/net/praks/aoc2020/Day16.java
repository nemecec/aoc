package net.praks.aoc2020;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.Value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.IntPredicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * <a href="https://adventofcode.com/2020/day/16">Day 16: Ticket Translation</a>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Day16 {

  @Value
  static class Notes {

    private static final String YOUR_TICKET = "your ticket:";
    private static final String NEARBY_TICKETS = "nearby tickets:";

    List<FieldRule> fieldRules;
    Ticket yourTicket;
    List<Ticket> nearbyTickets;

    static Notes parse(List<String> lines) {
      boolean isFieldRuleSection = true;
      boolean isYourTicketSection = false;
      boolean isNearbyTicketSection = false;
      List<FieldRule> fieldRules = new ArrayList<>();
      Ticket yourTicket = null;
      List<Ticket> nearbyTickets = new ArrayList<>();
      for (String line : lines) {
        if (isFieldRuleSection) {
          if (line.isEmpty()) {
            isFieldRuleSection = false;
          }
          else {
            fieldRules.add(FieldRule.parse(line));
          }
        }
        else if (line.equals(YOUR_TICKET)) {
          isYourTicketSection = true;
        }
        else if (isYourTicketSection) {
          yourTicket = Ticket.parse(line);
          isYourTicketSection = false;
        }
        else if (line.equals(NEARBY_TICKETS)) {
          isNearbyTicketSection = true;
        }
        else if (isNearbyTicketSection) {
          nearbyTickets.add(Ticket.parse(line));
        }
      }
      return new Notes(fieldRules, yourTicket, nearbyTickets);
    }

    int calculateTicketScanningErrorRateOfNearbyTickets() {
      return nearbyTickets.stream()
          .flatMapToInt(ticket -> ticket.getFieldValues().stream().mapToInt(Integer::intValue))
          .filter(v -> fieldRules.stream().noneMatch(rule -> rule.test(v)))
          .sum();
    }

    long calculateProductOfFieldsStartingWith(String fieldPrefix) {
      List<Ticket> validNearbyTickets = nearbyTickets.stream()
          .filter(ticket -> ticket.getFieldValues().stream().allMatch(
              v -> fieldRules.stream().anyMatch(rule -> rule.test(v))
          )).collect(Collectors.toList());

      List<List<Integer>> transposedFieldValues = IntStream.range(0, validNearbyTickets.get(0).getFieldValues().size())
          .mapToObj(i -> validNearbyTickets.stream()
              .map(ticket -> ticket.getFieldValues().get(i))
              .collect(Collectors.toList()))
          .collect(Collectors.toList());

      UniqueFieldFinder fieldFinder = new UniqueFieldFinder(
          transposedFieldValues.stream().map(valueList -> fieldRules.stream()
              .filter(rule -> valueList.stream().mapToInt(Integer::intValue).allMatch(rule.getRule()))
              .collect(Collectors.toList())).collect(Collectors.toList())
      );
      return fieldFinder.find().entrySet().stream()
          .filter(entry -> entry.getKey().startsWith(fieldPrefix))
          .mapToLong(entry -> yourTicket.getFieldValues().get(entry.getValue()))
          .reduce(1, (a, b) -> a * b);
    }

  }

  @Value
  static class UniqueFieldFinder {

    List<List<FieldRule>> fieldsWithMatchingRules;

    private Map<String, Integer> find() {
      Map<String, Integer> solvedFields = new TreeMap<>();
      while (solvedFields.size() < fieldsWithMatchingRules.size()) {
        List<String> fieldsToRemove = new ArrayList<>();
        IntStream.range(0, fieldsWithMatchingRules.size()).forEach(fieldIndex -> {
          List<FieldRule> fields = fieldsWithMatchingRules.get(fieldIndex);
          if (fields.size() == 1) {
            String fieldName = fields.get(0).getFieldName();
            solvedFields.put(fieldName, fieldIndex);
            fieldsToRemove.add(fieldName);
          }
        });
        fieldsWithMatchingRules.forEach(
            fields -> fields.removeIf(
                fieldRule -> fieldsToRemove.stream().anyMatch(fieldName -> fieldRule.getFieldName().equals(fieldName))
            )
        );
      }
      return solvedFields;
    }

  }

  @Value
  static class FieldRule implements IntPredicate {

    private static final Pattern PATTERN = Pattern.compile("([\\w\\s]+):\\s+(\\S+)\\s+or\\s+(\\S+)");

    String fieldName;

    @ToString.Exclude
    IntPredicate rule;

    static FieldRule parse(String str) {
      Matcher matcher = PATTERN.matcher(str);
      if (matcher.matches()) {
        return new FieldRule(
            matcher.group(1),
            ValueRange.parse(matcher.group(2))
                .or(ValueRange.parse(matcher.group(3)))
        );
      }
      else {
        throw new IllegalArgumentException("Invalid field rule syntax: " + str);
      }
    }

    @Override
    public boolean test(int value) {
      return rule.test(value);
    }

  }

  @Value
  static class ValueRange implements IntPredicate {

    private static final Pattern PATTERN = Pattern.compile("(\\d+)-(\\d+)");

    int start;
    int end;

    static ValueRange parse(String str) {
      Matcher matcher = PATTERN.matcher(str);
      if (matcher.matches()) {
        return new ValueRange(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)));
      }
      else {
        throw new IllegalArgumentException("Invalid value range syntax: " + str);
      }
    }

    @Override
    public boolean test(int value) {
      return value >= start && value <= end;
    }

  }

  @Value
  static class Ticket {

    private static final Pattern SEPARATOR = Pattern.compile(",");

    List<Integer> fieldValues;

    static Ticket parse(String str) {
      return new Ticket(Arrays.stream(SEPARATOR.split(str)).map(Integer::parseInt).collect(Collectors.toList()));
    }

  }

}
