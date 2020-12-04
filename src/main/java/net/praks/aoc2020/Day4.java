package net.praks.aoc2020;

import lombok.Value;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * <a href="https://adventofcode.com/2020/day/4">Day 4: Passport Processing</a>
 */
public class Day4 {

  static long countValidPassports(String input) {
    return PassportList.parse(input).countValid();
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

    long countValid() {
      return passports.stream().filter(Passport::isValid).count();
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

    boolean isValid() {
      int totalNumberOfPossibleKeys = Key.values().length;
      if (keyValues.size() == totalNumberOfPossibleKeys) {
        return true;
      }
      else if (keyValues.size() == totalNumberOfPossibleKeys - 1) {
        // One field is missing -- this is acceptable as long as that field is "cid"
        return !keyValues.containsKey(Key.cid);
      }
      return false;
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

  }

  private enum Key {
    byr("Birth Year"),
    iyr("Issue Year"),
    eyr("Expiration Year"),
    hgt("Height"),
    hcl("Hair Color"),
    ecl("Eye Color"),
    pid("Passport ID"),
    cid("Country ID");

    private final String description;

    Key(String description) {
      this.description = description;
    }

    @Override
    public String toString() {
      return name() + " (" + description + ')';
    }
  }

}
