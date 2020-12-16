package net.praks.aoc2020;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * <a href="https://adventofcode.com/2020/day/14">Day 14: Docking Data</a>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Day14 {

  @RequiredArgsConstructor
  abstract static class Bitmask {

    protected static final char X = 'X';

    private final String source;

    abstract long process(long value);

    long[] processMulti(long value) {
      return new long[] { process(value) };
    }

    @Override
    public String toString() {
      return source;
    }

  }

  @Value
  @EqualsAndHashCode(callSuper = true)
  static class SimpleBitmask extends Bitmask {

    long bitmaskAnd;
    long bitmaskOr;

    public SimpleBitmask(String source, long bitmaskAnd, long bitmaskOr) {
      super(source);
      this.bitmaskAnd = bitmaskAnd;
      this.bitmaskOr = bitmaskOr;
    }

    static SimpleBitmask parse(String bitmaskStr) {
      return new SimpleBitmask(
          bitmaskStr,
          Long.parseLong(bitmaskStr.replace(X, '1'), 2),
          Long.parseLong(bitmaskStr.replace(X, '0'), 2)
      );
    }

    public long process(long value) {
      return (value & bitmaskAnd) | bitmaskOr;
    }

  }

  @Value
  @EqualsAndHashCode(callSuper = true)
  static class MultiOrBitmask extends Bitmask {

    long bitmaskAnd;
    long[] bitmaskOrList;

    public MultiOrBitmask(String source, long bitmaskAnd, long[] bitmaskOrList) {
      super(source);
      this.bitmaskAnd = bitmaskAnd;
      this.bitmaskOrList = bitmaskOrList;
    }

    static MultiOrBitmask parse(String bitmaskStr) {
      return new MultiOrBitmask(
          bitmaskStr,
          Long.parseLong(bitmaskStr.replace('0', '1').replace(X, '0'), 2),
          generatePossibleCombinations(bitmaskStr, "").stream().mapToLong(str -> Long.parseLong(str, 2)).toArray()
      );
    }

    private static List<String> generatePossibleCombinations(String bitmaskStr, String partialCombination) {
      if (partialCombination.length() < bitmaskStr.length()) {
        List<String> combinations = new ArrayList<>();
        char c = bitmaskStr.charAt(partialCombination.length());
        if (c == X) {
          combinations.addAll(generatePossibleCombinations(bitmaskStr, partialCombination + '0'));
          combinations.addAll(generatePossibleCombinations(bitmaskStr, partialCombination + '1'));
        }
        else {
          combinations.addAll(generatePossibleCombinations(bitmaskStr, partialCombination + c));
        }
        return combinations;
      }
      else {
        return Collections.singletonList(partialCombination);
      }
    }

    @Override
    long process(long value) {
      throw new IllegalArgumentException("Operation not supported!");
    }

    @Override
    public long[] processMulti(long value) {
      return Arrays.stream(bitmaskOrList).map(bitmaskOr -> (value & bitmaskAnd) | bitmaskOr).toArray();
    }

    @Override
    public String toString() {
      return super.toString() + ", " +
          "bitmaskAnd=" + Long.toBinaryString(bitmaskAnd) + ", " +
          "bitmaskOrList=" + Arrays.stream(bitmaskOrList)
            .mapToObj(Long::toBinaryString)
            .collect(Collectors.joining(","));
    }
  }

  interface BitmaskFactory {
    Bitmask parse(String bitmaskAsString);
  }

  @Value
  @AllArgsConstructor
  static class VirtualMachine {

    Bitmask currentBitmask;
    Map<Long, Long> heap;

    public VirtualMachine(Bitmask initialBitmask) {
      this.currentBitmask = initialBitmask;
      this.heap = new TreeMap<>();
    }

    public VirtualMachine newWithValue(long address, long value) {
      return newWithValues(new long[] { address }, value);
    }

    public VirtualMachine newWithValues(long[] addresses, long value) {
      Map<Long, Long> newHeap = new TreeMap<>(heap);
      for (long address : addresses) {
        newHeap.put(address, value);
      }
      return new VirtualMachine(currentBitmask, newHeap);
    }

    long sumHeap() {
      return heap.values().stream().mapToLong(Long::longValue).sum();
    }

  }

  @Value
  static class Program {

    List<Instruction> instructions;

    static Program parse(List<String> lines, InstructionFactory instructionFactory) {
      return new Program(lines.stream()
                             .map(instructionFactory::parse)
                             .filter(Optional::isPresent)
                             .map(Optional::get)
                             .collect(Collectors.toList())
      );
    }

    Bitmask getInitialBitmask() {
      return ((BitmaskUpdate) instructions.get(0)).getBitmask();
    }

    VirtualMachine execute(VirtualMachine vm) {
      for (Instruction instruction : instructions) {
        vm = instruction.execute(vm);
      }
      return vm;
    }

    @Override
    public String toString() {
      return instructions.stream()
          .map(Instruction::toString)
          .collect(Collectors.joining("\n"));
    }

  }

  interface Instruction {
    VirtualMachine execute(VirtualMachine vm);
  }

  interface InstructionFactory {
    Optional<Instruction> parse(String instructionAsString);
  }

  @Value
  static class InstructionFactoryV1 implements InstructionFactory {

    @Override
    public Optional<Instruction> parse(String instructionAsString) {
      return ValueUpdate.parse(
          instructionAsString,
          (vm, update) -> vm.newWithValue(update.getAddress(), vm.getCurrentBitmask().process(update.getValue()))
      ).map(Optional::of).orElse(BitmaskUpdate.parse(instructionAsString, SimpleBitmask::parse));
    }
  }

  @Value
  static class InstructionFactoryV2 implements InstructionFactory {

    @Override
    public Optional<Instruction> parse(String instructionAsString) {
      return ValueUpdate.parse(
          instructionAsString,
          (vm, update) -> vm.newWithValues(Arrays.stream(vm.getCurrentBitmask().processMulti(update.getAddress()))
                                               .toArray(),
                                           update.getValue())
      ).map(Optional::of).orElse(BitmaskUpdate.parse(instructionAsString, MultiOrBitmask::parse));
    }

  }

  @Value
  static class BitmaskUpdate implements Instruction {

    private static final Pattern PATTERN = Pattern.compile("mask\\s*=\\s*(\\w+)");

    Bitmask bitmask;

    @Override
    public VirtualMachine execute(VirtualMachine vm) {
      return new VirtualMachine(bitmask, vm.getHeap());
    }

    static Optional<Instruction> parse(String line, BitmaskFactory bitmaskFactory) {
      Matcher matcher = PATTERN.matcher(line);
      if (matcher.matches()) {
        return Optional.of(new BitmaskUpdate(bitmaskFactory.parse(matcher.group(1))));
      }
      else {
        return Optional.empty();
      }
    }

    @Override
    public String toString() {
      return "mask = " + bitmask;
    }

  }

  @Value
  static class ValueUpdate implements Instruction {

    private static final Pattern PATTERN = Pattern.compile("mem\\[(\\d+)]\\s*=\\s*(\\d+)");

    int address;
    long value;
    BiFunction<VirtualMachine, ValueUpdate, VirtualMachine> updateLogic;

    @Override
    public VirtualMachine execute(VirtualMachine vm) {
      return updateLogic.apply(vm, this);
    }

    static Optional<Instruction> parse(String line, BiFunction<VirtualMachine, ValueUpdate, VirtualMachine> updateLogic) {
      Matcher matcher = PATTERN.matcher(line);
      if (matcher.matches()) {
        return Optional.of(new ValueUpdate(
            Integer.parseInt(matcher.group(1)),
            Long.parseLong(matcher.group(2)),
            updateLogic
        ));
      }
      else {
        return Optional.empty();
      }
    }

    @Override
    public String toString() {
      return String.format("mem[%s] = %s", address, value);
    }

  }

}
