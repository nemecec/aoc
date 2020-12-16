package net.praks.aoc2020;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

class Day14Test {

  private static final String SIMPLE_BITMASK = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXX1XXXX0X";

  @ParameterizedTest
  @CsvSource({
      "example, 165",
      "real, 14954914379452",
  })
  void testProgramV1(@ResourceFromClassPathArgument List<String> input, long expectedSum) {
    Day14.Program program = Day14.Program.parse(input, new Day14.InstructionFactoryV1());
    Day14.VirtualMachine vm = new Day14.VirtualMachine(program.getInitialBitmask());
    Day14.VirtualMachine finalVm = program.execute(vm);
    long actualSum = finalVm.sumHeap();
    assertThat(actualSum).isEqualTo(expectedSum);
  }

  @ParameterizedTest
  @CsvSource({
      "example2, 208",
      "real, 3415488160714",
  })
  void testProgramV2(@ResourceFromClassPathArgument List<String> input, long expectedSum) {
    Day14.Program program = Day14.Program.parse(input, new Day14.InstructionFactoryV2());
    Day14.VirtualMachine vm = new Day14.VirtualMachine(program.getInitialBitmask());
    Day14.VirtualMachine finalVm = program.execute(vm);
    long actualSum = finalVm.sumHeap();
    assertThat(actualSum).isEqualTo(expectedSum);
  }

  @ParameterizedTest
  @CsvSource({
      "000000000000000000000000000000001011, 000000000000000000000000000001001001",
      "000000000000000000000000000001100101, 000000000000000000000000000001100101",
      "000000000000000000000000000000000000, 000000000000000000000000000001000000",
  })
  void testSimpleBitMask(String givenValue, String expectedValue) {
    Day14.SimpleBitmask bitmask = Day14.SimpleBitmask.parse(SIMPLE_BITMASK);
    String actualValue = toBinaryString(bitmask.process(Long.parseLong(givenValue, 2)));
    assertThat(actualValue).isEqualTo(expectedValue);
  }

  @ParameterizedTest
  @CsvSource({
      "000000000000000000000000000000X1001X, 42, '26,27,58,59'",
      "00000000000000000000000000000000X0XX, 26, '16,17,18,19,24,25,26,27'",
  })
  void testMultiOrBitmask(String bitmaskStr, int givenValue, String expectedValuesAsString) {
    Day14.MultiOrBitmask bitmask = Day14.MultiOrBitmask.parse(bitmaskStr);
    long[] actualValues = bitmask.processMulti(givenValue);
    Pattern separator = Pattern.compile(",");
    assertThat(actualValues).containsExactly(Arrays.stream(separator.split(expectedValuesAsString)).mapToLong(Long::parseLong).toArray());
  }

  private static String toBinaryString(long value) {
    return String.format("%36s", Long.toBinaryString(value)).replace(" ", "0");
  }

}