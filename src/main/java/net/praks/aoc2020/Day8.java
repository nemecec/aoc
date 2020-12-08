package net.praks.aoc2020;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.Value;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <a href="https://adventofcode.com/2020/day/8">Day 8: Handheld Halting</a>
 */
public class Day8 {

  @Value
  static class Program {
    List<Instruction> instructions;

    static Program parse(List<String> lines) {
      return new Program(lines.stream().map(Instruction::parse).collect(Collectors.toList()));
    }

    Heap tryVariations(InstructionReplaceOperation... mutations) {
      for (InstructionReplaceOperation mutation : mutations) {
        List<Integer> foundIndexes = mutation.findInstructions(this.instructions);
        for (Integer index : foundIndexes) {
          List<Instruction> mutatedInstructions = mutation.replaceInstruction(this.instructions, index);
          try {
            Heap heap = new Heap();
            execute(heap, new ExecutionContext(mutatedInstructions));
            return heap;
          }
          catch (EndlessLoopException e) {
            // try next
          }
        }
      }
      return null;
    }

    void execute() {
      execute(new Heap(), new ExecutionContext(instructions));
    }

    private void execute(Heap heap, ExecutionContext context) {
      while (context.hasNextInstruction()) {
        context.execute(heap);
      }
    }

  }

  @Value
  static class InstructionReplaceOperation {
    InstructionType instructionTypeToFind;
    InstructionType instructionTypeToReplaceWith;

    List<Integer> findInstructions(List<Instruction> instructions) {
      List<Integer> results = new ArrayList<>(instructions.size());
      for (int i = 0; i < instructions.size(); i++) {
        Instruction instruction = instructions.get(i);
        if (instruction.getType() == instructionTypeToFind) {
          results.add(i);
        }
      }
      return results;
    }

    List<Instruction> replaceInstruction(List<Instruction> instructions, int replaceAt) {
      List<Instruction> results = new ArrayList<>(instructions);
      Instruction oldInstruction = instructions.get(replaceAt);
      results.set(replaceAt, instructionTypeToReplaceWith.createNew(oldInstruction.getValue()));
      return results;
    }

  }

  @ToString
  @Getter
  static class Heap {
    long accumulator;

    private void incAccumulator(int increment) {
      this.accumulator += increment;
    }

    private Heap copy() {
      Heap heap = new Heap();
      heap.accumulator = this.accumulator;
      return heap;
    }

  }

  @RequiredArgsConstructor
  @ToString(onlyExplicitlyIncluded = true)
  private static class ExecutionContext {

    final List<Instruction> instructions;
    List<TraceFrame> executionTrace = new ArrayList<>();
    Set<Integer> alreadyExecutedInstructions = new HashSet<>();

    @ToString.Include
    int position;

    int currentPosition;
    Instruction currentInstruction;

    void incPosition(int increment) {
      this.position += increment;
    }

    boolean hasNextInstruction() {
      this.currentPosition = position;
      if (currentPosition == instructions.size()) {
        return false;
      }
      if (currentPosition < 0 || currentPosition > instructions.size()-1) {
        throw new IllegalStateException("Execution position out of range: " + currentPosition);
      }
      this.currentInstruction = instructions.get(currentPosition);
      return true;
    }

    void execute(Heap heap) {
      if (alreadyExecutedInstructions.contains(currentPosition)) {
        throw new EndlessLoopException(currentPosition, currentInstruction, this, heap);
      }
      currentInstruction.execute(heap, this);
      alreadyExecutedInstructions.add(currentPosition);
      executionTrace.add(new TraceFrame(currentPosition, currentInstruction, heap.copy()));
      incPosition(1);
    }

    private String getExecutionTraceAsString() {
      return executionTrace.stream().map(TraceFrame::toString).collect(Collectors.joining("\n"));
    }

  }

  @Getter
  static class EndlessLoopException extends RuntimeException {
    final int currentPosition;
    final transient Instruction currentInstruction;
    final transient ExecutionContext executionContext;
    final transient Heap heap;

    public EndlessLoopException(
        int currentPosition,
        Instruction currentInstruction,
        ExecutionContext executionContext,
        Heap heap
    ) {
      super(String.format("Endless loop detected! " +
                              "current position=%s (%s), " +
                              "execution context=%s, " +
                              "heap=%s" +
                              "\ntrace:\n%s",
                          currentPosition, currentInstruction,
                          executionContext,
                          heap,
                          executionContext.getExecutionTraceAsString()
      ));
      this.currentPosition = currentPosition;
      this.currentInstruction = currentInstruction;
      this.executionContext = executionContext;
      this.heap = heap;
    }
  }

  @Value
  private static class TraceFrame {
    int position;
    Instruction executedInstruction;
    Heap heapAfterExecution;

    @Override
    public String toString() {
      return position + ": " + executedInstruction + " | " + heapAfterExecution;
    }
  }

  @RequiredArgsConstructor @EqualsAndHashCode @Getter
  private abstract static class Instruction {

    protected final InstructionType type;
    protected final int value;

    abstract void execute(Heap heap, ExecutionContext context);

    static Instruction parse(String instructionStr) {
      String[] strings = instructionStr.split("\\s+");
      String insName = strings[0];
      String valueStr = strings[1];
      if (valueStr.startsWith("+")) {
        valueStr = valueStr.substring(1);
      }
      int value = Integer.parseInt(valueStr);
      return InstructionType.valueOf(insName).createNew(value);
    }

    @Override
    public String toString() {
      return type + " " + value;
    }

  }

  @RequiredArgsConstructor
  @SuppressWarnings("unused") // used indirectly from Instruction.parse()
  enum InstructionType {
    acc(AccInstruction::new),
    jmp(JmpInstruction::new),
    nop(NopInstruction::new);
    private final Function<Integer, Instruction> constructor;

    Instruction createNew(int value) {
      return constructor.apply(value);
    }

  }

  private static class AccInstruction extends Instruction {

    public AccInstruction(int value) {
      super(InstructionType.acc, value);
    }

    @Override
    public void execute(Heap heap, ExecutionContext context) {
      heap.incAccumulator(value);
    }

  }

  private static class JmpInstruction extends Instruction {

    public JmpInstruction(int value) {
      super(InstructionType.jmp, value);
    }

    @Override
    public void execute(Heap heap, ExecutionContext context) {
      context.incPosition(value - 1); // deduct 1 as by default we anyway advance position by 1
    }

  }

  private static class NopInstruction extends Instruction {

    public NopInstruction(int value) {
      super(InstructionType.nop, value);
    }

    @Override
    public void execute(Heap heap, ExecutionContext context) {
      // do nothing
    }

  }

}
