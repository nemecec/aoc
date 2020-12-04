package net.praks.aoc2020;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <a href="https://adventofcode.com/2020/day/2">Day 2: Password Philosophy</a>
 */
@RequiredArgsConstructor
public class Day2 {

  private final PasswordValidationStrategy passwordValidationStrategy;

  long countValidPasswords(List<String> passwordDefinitions) {
    return passwordDefinitions.stream()
        .filter(s -> !s.isEmpty())
        .map(PasswordLine::new)
        .filter(passwordLine -> passwordLine.isValid(passwordValidationStrategy))
        .count();
  }

  private static class PasswordLine {
    private static final Pattern PATTERN = Pattern.compile("(.+): (.+)");
    String line;
    PasswordPolicyDefinition passwordPolicyDefinition;
    String password;

    public PasswordLine(String line) {
      this.line = line;
      Matcher matcher = PATTERN.matcher(line);
      if (matcher.matches()) {
        this.passwordPolicyDefinition = new PasswordPolicyDefinition(matcher.group(1));
        this.password = matcher.group(2);
      }
      else {
        throw new IllegalArgumentException("Unrecognized password policy + password line: " + line);
      }
    }

    boolean isValid(PasswordValidationStrategy passwordValidationStrategy) {
      return passwordValidationStrategy.isValid(passwordPolicyDefinition, password);
    }
  }

  @Getter
  private static class PasswordPolicyDefinition {

    private static final Pattern PATTERN = Pattern.compile("(\\d+)-(\\d+) (.)");
    int value1;
    int value2;
    char requiredCharacter;

    public PasswordPolicyDefinition(String policyStr) {
      Matcher matcher = PATTERN.matcher(policyStr);
      if (matcher.matches()) {
        this.value1 = Integer.parseInt(matcher.group(1));
        this.value2 = Integer.parseInt(matcher.group(2));
        this.requiredCharacter = matcher.group(3).charAt(0);
      }
      else {
        throw new IllegalArgumentException("Unrecognized policy: " + policyStr);
      }
    }

  }

  @RequiredArgsConstructor
  @SuppressWarnings("unused") // used via reflection
  enum PasswordValidationStrategy {
    /**
     * Part 1
     */
    COUNTING((passwordPolicyDefinition, password) -> {
      int count = 0;
      for (int i = 0; i < password.length(); i++) {
        char c = password.charAt(i);
        if (c == passwordPolicyDefinition.getRequiredCharacter()) {
          count++;
        }
      }
      return count >= passwordPolicyDefinition.getValue1() && count <= passwordPolicyDefinition.getValue2();
    }),
    /**
     * Part 2
     */
    POSITIONAL((passwordPolicyDefinition, password) -> {
      int count = 0;
      if (password.charAt(passwordPolicyDefinition.getValue1() - 1) == passwordPolicyDefinition.getRequiredCharacter()) {
        count++;
      }
      if (password.charAt(passwordPolicyDefinition.getValue2() - 1) == passwordPolicyDefinition.getRequiredCharacter()) {
        count++;
      }
      return count == 1;
    });

    private final BiPredicate<PasswordPolicyDefinition, String> validationPolicy;

    boolean isValid(PasswordPolicyDefinition passwordPolicyDefinition, String password) {
      return validationPolicy.test(passwordPolicyDefinition, password);
    }
  }

}
