package net.praks.aoc2020;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day2 {

  public static void main(String[] args) throws IOException {
    long validCount = Files.readAllLines(Paths.get(args[0])).stream().filter(s -> !s.isEmpty()).map(PasswordLine::new).filter(PasswordLine::isValid).count();
    System.out.printf("Valid password count: %s%n", validCount); //NOSONAR
  }

  private static class PasswordLine {
    private static final Pattern PATTERN = Pattern.compile("(.+): (.+)");
    String line;
    PasswordPolicy passwordPolicy;
    String password;

    public PasswordLine(String line) {
      this.line = line;
      Matcher matcher = PATTERN.matcher(line);
      if (matcher.matches()) {
        this.passwordPolicy = new PasswordPolicy(matcher.group(1));
        this.password = matcher.group(2);
      }
      else {
        throw new IllegalArgumentException("Unrecognized password policy + password line: " + line);
      }
    }

    boolean isValid() {
      return passwordPolicy.isValid(password);
    }
  }

  private static class PasswordPolicy {

    private static final Pattern PATTERN = Pattern.compile("(\\d+)-(\\d+) (.)");
    int min;
    int max;
    char requiredCharacter;

    public PasswordPolicy(String policyStr) {
      Matcher matcher = PATTERN.matcher(policyStr);
      if (matcher.matches()) {
        this.min = Integer.parseInt(matcher.group(1));
        this.max = Integer.parseInt(matcher.group(2));
        this.requiredCharacter = matcher.group(3).charAt(0);
      }
      else {
        throw new IllegalArgumentException("Unrecognized policy: " + policyStr);
      }
    }

    boolean isValid(String password) {
      int count = 0;
      for (int i = 0; i < password.length(); i++) {
        char c = password.charAt(i);
        if (c == requiredCharacter) {
          count++;
        }
      }
      return count >= min && count <= max;
    }

  }

}
