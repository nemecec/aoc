package net.praks.aoc2020;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class Day8Test {

  @ParameterizedTest
  @CsvSource({
      "example, 5",
      "real, 1867",
  })
  void testEndlessLoop(@ResourceFromClassPathArgument List<String> input, long expectedAccumulator) {
    Day8.Program program = Day8.Program.parse(input);
    try {
      program.execute();
    }
    catch (Day8.EndlessLoopException e) {
      assertThat(e.getHeap().getAccumulator()).isEqualTo(expectedAccumulator);
    }
  }

  @ParameterizedTest
  @CsvSource({
      "example, 8",
      "real, 1303",
  })
  void testEndlessLoopFix(@ResourceFromClassPathArgument List<String> input, long expectedAccumulator) {
    Day8.Program program = Day8.Program.parse(input);
    Day8.Heap heap = program.tryVariations(
        new Day8.InstructionReplaceOperation(Day8.InstructionType.nop, Day8.InstructionType.jmp),
        new Day8.InstructionReplaceOperation(Day8.InstructionType.jmp, Day8.InstructionType.nop)
    );
    assertThat(heap.getAccumulator()).isEqualTo(expectedAccumulator);
  }

}