package net.praks.aoc2020;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.Value;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * <a href="https://adventofcode.com/2020/day/10">Day 10: Adapter Array</a>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Day10 {

  @RequiredArgsConstructor
  static class AdapterList {

    final List<Integer> adapters;
    @Setter boolean isDebugEnabled;

    static AdapterList parse(List<String> adapterJoltsAsStringList) {
      return new AdapterList(
          adapterJoltsAsStringList.stream().map(Integer::valueOf).sorted().collect(Collectors.toList())
      );
    }

    JoltDifferences summarizeDifferences(int startingJolts, int finalDifference) {
      JoltDifferences joltDifferences = new JoltDifferences();
      int previousJolts = startingJolts;
      for (Integer adapterJolts : adapters) {
        joltDifferences.newDifference(adapterJolts - previousJolts);
        previousJolts = adapterJolts;
      }
      joltDifferences.newDifference(finalDifference);
      return joltDifferences;
    }

    long countDistinctCombinationsWithTree(int startingJolts, int maxJoltsDiff) {
      AdapterTreeNode root = new AdapterTreeNode(startingJolts);
      adapters.forEach(jolts -> root.addAdapter(jolts, maxJoltsDiff));
      return root.countLeaves(isDebugEnabled ? new LinkedList<>() : null);
    }

    long countDistinctCombinationsWithMath(int startingJolts, int maxJoltsDiff) {
      MathCountingContext context = new MathCountingContext();
      context.addStartAdapter(startingJolts, 1);
      int previousJolt = 0;
      for (int i = 0; i < adapters.size(); i++) {
        int adapterJolt = adapters.get(i);
        if (i > 0) {
          context = context.nextAdapter(previousJolt, adapterJolt, maxJoltsDiff);
        }
        previousJolt = adapterJolt;
      }
      return context.nextAdapter(previousJolt, previousJolt + maxJoltsDiff, maxJoltsDiff).getCount(previousJolt);
    }

  }

  @Value
  static class MathCountingContext {

    //key: startJolts
    Map<Integer, MathCountingStartAdapter> startAdapters = new TreeMap<>();

    void addStartAdapter(int startJolts, long count) {
      MathCountingStartAdapter adapter =
          startAdapters.computeIfAbsent(startJolts, key -> new MathCountingStartAdapter(startJolts));
      adapter.incCount(count);
    }

    MathCountingContext nextAdapter(int nextJolts, int nextNextJolts, int maxJoltsDiff) {
      MathCountingContext newContext = new MathCountingContext();
      startAdapters.values().forEach(
          adapter -> adapter.nextAdapter(nextJolts, nextNextJolts, maxJoltsDiff, newContext)
      );
      return newContext;
    }

    long getCount(int startJolts) {
      if (startAdapters.size() > 1) {
        throw new IllegalStateException(String.format("Should contain only one adapter! %s", startAdapters));
      }
      MathCountingStartAdapter adapter = startAdapters.get(startJolts);
      if (adapter == null) {
        throw new IllegalStateException(String.format("No adapter with %s jolts! %s", startJolts, startAdapters));
      }
      return adapter.getCount();
    }

    @Override
    public String toString() {
      return startAdapters.values().stream()
          .map(MathCountingStartAdapter::toString)
          .collect(Collectors.joining("\n"));
    }

  }

  @RequiredArgsConstructor
  static class MathCountingStartAdapter {

    @Getter final int startJolts;
    @Getter long count;

    void incCount(long countInc) {
      count += countInc;
    }

    void nextAdapter(int nextJolts, int nextNextJolts, int maxJoltsDiff, MathCountingContext newContext) {
      newContext.addStartAdapter(nextJolts, count);
      if (nextNextJolts - startJolts <= maxJoltsDiff) {
        newContext.addStartAdapter(startJolts, count);
      }
    }

    @Override
    public String toString() {
      return String.format("start %s, count %s", startJolts, count);
    }

  }

  @Value
  static class JoltDifferences {

    Map<Integer, Integer> counts = new TreeMap<>();

    void newDifference(int joltsDiff) {
      Integer count = counts.getOrDefault(joltsDiff, 0);
      counts.put(joltsDiff, count + 1);
    }

    int multiplyDifferences(int jolts1, int jolts2) {
      return counts.getOrDefault(jolts1, 0) * counts.getOrDefault(jolts2, 0);
    }

  }

  @Value
  static class AdapterTreeNode {

    int jolts;
    List<AdapterTreeNode> nextPossibleAdapters = new ArrayList<>();

    void addAdapter(int newAdapterJolts, int maxJoltsDiff) {
      if (newAdapterJolts > jolts) {
        if (newAdapterJolts - jolts <= maxJoltsDiff) {
          nextPossibleAdapters.add(new AdapterTreeNode(newAdapterJolts));
        }
        nextPossibleAdapters.forEach(nextAdapter -> nextAdapter.addAdapter(newAdapterJolts, maxJoltsDiff));
      }
    }

    long countLeaves(Deque<AdapterTreeNode> nodeStack) {
      if (nodeStack != null) {
        nodeStack.addLast(this);
      }
      long leavesCount;
      if (nextPossibleAdapters.isEmpty()) {
        if (nodeStack != null) {
          System.out.println(nodeStack); //NOSONAR
        }
        leavesCount = 1;
      }
      else {
        leavesCount = nextPossibleAdapters.stream().mapToLong(node -> node.countLeaves(nodeStack)).sum();
      }
      if (nodeStack != null) {
        nodeStack.removeLast();
      }
      return leavesCount;
    }

    @Override
    public String toString() {
      return String.valueOf(jolts);
    }

  }

}
