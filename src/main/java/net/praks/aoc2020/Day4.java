package net.praks.aoc2020;

import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * <a href="https://adventofcode.com/2020/day/4">Day 4: Passport Processing</a>
 */
public class Day4 {

  static long countValidPassports(String input, boolean validateValues) {
    return PassportList.parse(input).countValid(validateValues);
  }

  @Value
  private static class PassportList {
    private static final Pattern SEPARATOR = Pattern.compile("\n\n");

    List<Passport> passports;

    public static PassportList parse(String passportListStr) {
      return new PassportList(
          Arrays.stream(SEPARATOR.split(passportListStr.trim())).map(Passport::parse).collect(Collectors.toList())
      );
    }

    long countValid(boolean validateValues) {
      return passports.stream().filter(passport -> passport.isValid(validateValues)).count();
    }
  }

  @Value
  private static class Passport {
    private static final Pattern SEPARATOR = Pattern.compile("\\s+");

    Map<Key, KeyValue> keyValues;

    public static Passport parse(String passportStr) {
      return new Passport(
          Arrays.stream(SEPARATOR.split(passportStr.trim()))
              .map(KeyValue::parse)
              .collect(Collectors.toMap(KeyValue::getKey, keyValue -> keyValue))
      );
    }

    boolean isValid(boolean validateValues) {
      int totalNumberOfPossibleKeys = Key.values().length;
      boolean isValid = false;
      if (keyValues.size() == totalNumberOfPossibleKeys) {
        isValid = true;
      }
      else if (keyValues.size() == totalNumberOfPossibleKeys - 1) {
        // One field is missing -- this is acceptable as long as that field is "cid"
        isValid = !keyValues.containsKey(Key.cid);
      }
      if (isValid && validateValues) {
        return keyValues.values().stream().allMatch(KeyValue::isValid);
      }
      return isValid;
    }

  }

  @Value
  private static class KeyValue {
    private static final Pattern PATTERN = Pattern.compile("(.+):(.+)");

    Key key;
    String value;

    public static KeyValue parse(String keyValueStr) {
      Matcher matcher = PATTERN.matcher(keyValueStr);
      if (matcher.matches()) {
        return new KeyValue(Key.valueOf(matcher.group(1)), matcher.group(2));
      }
      else {
        throw new IllegalArgumentException("Invalid key-value pair format: " + keyValueStr);
      }
    }

    boolean isValid() {
      return key.isValid(value);
    }

  }

  @RequiredArgsConstructor
  private static class NumberValidator implements Predicate<String> {

    static NumberValidator plainNumber(int atLeast, int atMost) {
      return new NumberValidator(Pattern.compile("(\\d+)"), atLeast, atMost);
    }

    static NumberValidator numberWithSuffix(String suffix, int atLeast, int atMost) {
      return new NumberValidator(Pattern.compile("(\\d+)" + suffix), atLeast, atMost);
    }

    private final Pattern pattern;
    private final int atLeast;
    private final int atMost;

    @Override
    public boolean test(String v) {
      Matcher matcher = pattern.matcher(v);
      if (matcher.matches()) {
        String cleanValue = matcher.group(1);
        int intValue = Integer.parseInt(cleanValue);
        return intValue >= atLeast && intValue <= atMost;
      }
      return false;
    }

    @Override
    public String toString() {
      return pattern + ", " + atLeast + "-" + atMost;
    }

  }

  enum Key {
    byr("Birth Year", NumberValidator.plainNumber(1920, 2002)),
    iyr("Issue Year", NumberValidator.plainNumber(2010, 2020)),
    eyr("Expiration Year", NumberValidator.plainNumber(2020, 2030)),
    hgt("Height", NumberValidator.numberWithSuffix("cm", 150, 193).or(NumberValidator.numberWithSuffix("in", 59, 76))),
    hcl("Hair Color", v -> Pattern.matches("#[0-9a-f]{6}", v)),
    ecl("Eye Color", v -> Pattern.matches("amb|blu|brn|gry|grn|hzl|oth", v)),
    pid("Passport ID", v -> Pattern.matches("\\d{9}", v)),
    cid("Country ID", v -> true);

    private final String description;
    private final Predicate<String> validator;

    Key(String description, Predicate<String> validator) {
      this.description = description;
      this.validator = validator;
    }

    boolean isValid(String value) {
      return validator.test(value);
    }

    @Override
    public String toString() {
      return name() + " (" + description + ')';
    }
  }

}
