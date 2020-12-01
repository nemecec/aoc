package net.praks.aoc2020;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class Day1 {

  public static void main(String[] args) throws IOException {
    List<Long> numbers = Files.readAllLines(Paths.get(args[0])).stream().filter(s -> !s.isEmpty()).map(Long::parseLong).collect(
        Collectors.toList());
    for (Long number1 : numbers) {
      for (Long number2 : numbers) {
        for (Long number3 : numbers) {
          if (number1 + number2 + number3 == 2020) {
            System.out.printf("%s * %s * %s = %s%n", number1, number2, number3, number1 * number2 * number3);
          }
        }
      }
    }
  }

}
