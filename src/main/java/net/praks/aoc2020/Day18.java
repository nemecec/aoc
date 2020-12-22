package net.praks.aoc2020;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <a href="https://adventofcode.com/2020/day/18">Day 18: Operation Order</a>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Day18 {

  private static final Pattern NUMBER = Pattern.compile("\\d+");
  private static final Pattern ANY_OPERATION = Pattern.compile("((" + NUMBER + ") (.) (" + NUMBER + "))\\b.*");

  static ExpressionList parse(List<String> lines) {
    return new ExpressionList(lines);
  }

  @Value
  static class ExpressionList {

    List<String> expressions;

    public long evaluate(EvaluationContext context) {
      return expressions.stream().mapToLong(expr -> Evaluator.evaluate(expr, context)).sum();
    }

    @Override
    public String toString() {
      return String.join("\n", expressions);
    }

  }

  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  static class Evaluator {

    private static final char OPENING_PAREN = '(';
    private static final char CLOSING_PAREN = ')';

    static long evaluate(String exprStr, EvaluationContext context) {
      return Long.parseLong(evaluateToString(exprStr, context));
    }

    private static String evaluateToString(String exprStr, EvaluationContext context) {
      int openingParenIndex;
      do {
        openingParenIndex = exprStr.indexOf(OPENING_PAREN);
        if (openingParenIndex > -1) {
          String parenContents = stringUntilClosingParen(exprStr.substring(openingParenIndex));
          exprStr = exprStr.replace(parenContents, evaluateToString(parenContents.substring(1, parenContents.length() - 1),
                                                                    context));
        }
      } while (openingParenIndex > -1);
      do {
        List<Operator> precedence = context.getOperatorPrecedence();
        String operatorEvaluationResult;
        if (precedence.isEmpty()) {
          operatorEvaluationResult = findAndEvaluateAnyOperator(exprStr);
        }
        else {
          operatorEvaluationResult = findAndEvaluateOperatorsInOrder(exprStr, precedence);
        }
        if (operatorEvaluationResult != null) {
          exprStr = operatorEvaluationResult;
        }
        else {
          Matcher numberMatcher = NUMBER.matcher(exprStr);
          if (numberMatcher.matches()) {
            return exprStr;
          }
          else {
            throw new IllegalArgumentException("Unknown residue: " + exprStr);
          }
        }
      } while (true);
    }

    private static String findAndEvaluateOperatorsInOrder(String exprStr, List<Operator> precedence) {
      boolean evaluatedSomething = false;
      for (Operator op : precedence) {
        String operatorEvaluationResult;
        do {
          operatorEvaluationResult = findAndEvaluateOperator(exprStr, op.getRegularExpression());
          if (operatorEvaluationResult != null) {
            exprStr = operatorEvaluationResult;
            evaluatedSomething = true;
          }
        } while (operatorEvaluationResult != null);
      }
      return evaluatedSomething ? exprStr : null;
    }

    private static String findAndEvaluateAnyOperator(String exprStr) {
      return findAndEvaluateOperator(exprStr, ANY_OPERATION);
    }

    private static String findAndEvaluateOperator(String exprStr, Pattern operatorPattern) {
      Matcher opMatcher = operatorPattern.matcher(exprStr);
      if (opMatcher.matches()) {
        Long v1 = Long.parseLong(opMatcher.group(2));
        Operator op = Operator.parse(opMatcher.group(3));
        Long v2 = Long.parseLong(opMatcher.group(4));
        Long result = op.getLogic().apply(v1, v2);
        String wholeOperation = opMatcher.group(1);
        String operationPattern = "\\b" + Pattern.quote(wholeOperation) + "\\b";
        return exprStr.replaceFirst(operationPattern, result.toString());
      }
      return null;
    }

    private static String stringUntilClosingParen(String exprStr) {
      int nestedParenCounter = 0;
      StringBuilder contents = new StringBuilder(exprStr.length());
      for (int i = 0; i < exprStr.length(); i++) {
        char c = exprStr.charAt(i);
        if (c == OPENING_PAREN) {
          nestedParenCounter++;
        }
        if (c == CLOSING_PAREN) {
          nestedParenCounter--;
        }
        contents.append(c);
        if (nestedParenCounter < 1) {
          break;
        }
      }
      return contents.toString();
    }

  }

  @Getter
  static class EvaluationContext {

    private final List<Operator> operatorPrecedence = new ArrayList<>();

    EvaluationContext addOperatorPrecedence(Operator op) {
      operatorPrecedence.add(op);
      return this;
    }

  }

  @Getter
  enum Operator {
    ADDITION('+', Long::sum),
    MULTIPLICATION('*', (v1, v2) -> v1 * v2);

    private final char label;
    private final BinaryOperator<Long> logic;
    private final Pattern regularExpression;

    Operator(char label, BinaryOperator<Long> logic) {
      this.label = label;
      this.logic = logic;
      this.regularExpression = Pattern.compile(
          ".*\\b(" +
              "(" + NUMBER + ") " +
              "(" + Pattern.quote(String.valueOf(label)) + ") " +
              "(" + NUMBER + ")" +
          ")\\b.*");
    }

    public static Operator parse(String str) {
      if (str.length() != 1) {
        throw new IllegalArgumentException(
            String.format("Operator has to be a single character: '%s'", str)
        );
      }
      char c = str.charAt(0);
      if (c == ADDITION.getLabel()) {
        return ADDITION;
      }
      else if (c == MULTIPLICATION.getLabel()) {
        return MULTIPLICATION;
      }
      else {
        throw new IllegalArgumentException("Unrecognized operator: " + c);
      }
    }

  }

}
