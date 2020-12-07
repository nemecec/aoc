package net.praks.aoc2020;

import lombok.ToString;
import lombok.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <a href="https://adventofcode.com/2020/day/7">Day 7: Handy Haversacks</a>
 */
public class Day7 {

  private final BagFactory factory = new BagFactory();

  public Day7(List<String> bagDefinitions) {
    bagDefinitions.forEach(def -> Bag.parse(def, factory));
  }

  long countAllOuterMostBags(String colorName) {
    return factory.bags.values().stream()
        .filter(bag -> bag.containsOnSomeLevel(colorName))
        .count();
  }

  long countAllInnerBags(String colorName) {
    return factory.getBag(colorName).countContainedBags();
  }

  @Value
  static class BagFactory {

    Map<String, Bag> bags = new HashMap<>();

    Bag getBag(String colorName) {
      return bags.computeIfAbsent(colorName, key -> new Bag(this, key, new ArrayList<>()));
    }

  }

  @Value
  static class BagWithCount {

    int count;
    Bag bag;

    public long countContainedBags() {
      return count * (bag.countContainedBags() + 1);
    }

  }

  @Value
  static class Bag {

    static final Pattern CAN_CONTAIN_PATTERN = Pattern.compile("(\\d+) ([\\s\\w]+) bags?");
    static final Pattern CAN_CONTAIN_SPLIT_PATTERN = Pattern.compile(",\\s");
    private static final Pattern PATTERN = Pattern.compile(
        "([\\s\\w]+) bags contain ((?:" + CAN_CONTAIN_PATTERN + "(?:" + CAN_CONTAIN_SPLIT_PATTERN + ")?)+|no other bags)\\.");

    @ToString.Exclude BagFactory factory;
    String colorName;
    List<BagWithCount> canContain;

    static Bag parse(String bagDefinition, BagFactory factory) {
      Matcher matcher = PATTERN.matcher(bagDefinition);
      if (matcher.matches()) {
        String colorName = matcher.group(1);
        Bag bag = factory.getBag(colorName);
        String canContainStr = matcher.group(2);
        if (!canContainStr.equals("no other bags")) {
          String[] canContainArray = CAN_CONTAIN_SPLIT_PATTERN.split(canContainStr);
          for (String canContainElement : canContainArray) {
            Matcher canContainMatcher = CAN_CONTAIN_PATTERN.matcher(canContainElement);
            if (canContainMatcher.matches()) {
              int childCount = Integer.parseInt(canContainMatcher.group(1));
              String childColorName = canContainMatcher.group(2);
              bag.canContain.add(new BagWithCount(childCount, factory.getBag(childColorName)));
            }
            else {
              throw new IllegalArgumentException(
                  "Unrecognized bag definition 'can contain' format: " + canContainElement);
            }
          }
        }
        return bag;
      }
      else {
        throw new IllegalArgumentException("Unrecognized bag definition format: " + bagDefinition);
      }
    }

    boolean isOrContainsOnSomeLevel(String colorName) {
      return this.colorName.equals(colorName) || containsOnSomeLevel(colorName);
    }

    boolean containsOnSomeLevel(String colorName) {
      return canContain.stream().anyMatch(bagWithCount -> bagWithCount.getBag().isOrContainsOnSomeLevel(colorName));
    }

    public long countContainedBags() {
      return canContain.stream().mapToLong(BagWithCount::countContainedBags).sum();
    }
  }

}
