package net.praks.aoc2020;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * <a href="https://adventofcode.com/2020/day/22">Day 22: Crab Combat</a>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Day22 {

  @Value
  static class Combat {

    private static final Pattern PLAYER_PATTERN = Pattern.compile("Player (\\d+):");

    Player player1;
    Player player2;

    Combat copy() {
      return new Combat(player1.subDeck(player1.cardCount()), player2.subDeck(player2.cardCount()));
    }

    Combat subGame(int numberOfCards1, int numberOfCards2) {
      return new Combat(player1.subDeck(numberOfCards1), player2.subDeck(numberOfCards2));
    }

    static Combat parse(List<String> lines) {
      Player player1 = null;
      Player player2 = null;
      for (String line : lines) {
        Matcher matcher = PLAYER_PATTERN.matcher(line);
        if (matcher.matches()) {
          int playerNumber = Integer.parseInt(matcher.group(1));
          if (playerNumber == 1) {
            player1 = new Player(playerNumber);
          }
          else if (playerNumber == 2) {
            player2 = new Player(playerNumber);
          }
          else {
            throw new IllegalArgumentException("Unrecognized player number: " + line);
          }
        }
        else if (!line.isEmpty()) {
          if (player1 != null && player2 == null) {
            player1.add(Integer.parseInt(line));
          }
          else if (player2 != null) {
            player2.add(Integer.parseInt(line));
          }
        }
      }
      return new Combat(player1, player2);
    }

    long play() {
      while (player1.hasCards() && player2.hasCards()) {
        Integer card1 = player1.draw();
        Integer card2 = player2.draw();
        CombatResult result = new CombatResult(card1, card2).validate();
        if (result.player1Won()) {
          player1.add(card1).add(card2);
        }
        else if (card2 > card1) {
          player2.add(card2).add(card1);
        }
      }
      return player1.calculateScore() + player2.calculateScore();
    }

    long playRecursively() {
      Day22.playRecursively(this);
      return player1.calculateScore() + player2.calculateScore();
    }

    @Override
    public String toString() {
      return player1.toOriginalString() + "\n" + player2.toOriginalString().trim();
    }

  }

  private static CombatResult playRecursively(Combat game) {
    Set<Combat> previousGames = new HashSet<>();
    CombatResult result = null;
    while (game.getPlayer1().hasCards() && game.getPlayer2().hasCards()) {
      if (previousGames.contains(game)) {
        result = new CombatResult(1, 0);
        break;
      }
      previousGames.add(game.copy());
      Integer card1 = game.getPlayer1().draw();
      Integer card2 = game.getPlayer2().draw();
      if (game.getPlayer1().cardCount() >= card1 && game.getPlayer2().cardCount() >= card2) {
        result = playRecursively(game.subGame(card1, card2));
      }
      else {
        result = new CombatResult(card1, card2).validate();
      }
      if (result.player1Won()) {
        game.getPlayer1().add(card1).add(card2);
      }
      else {
        game.getPlayer2().add(card2).add(card1);
      }
    }
    if (result == null) {
      throw new IllegalStateException("Result is null? " + game);
    }
    return result;
  }

  @Value
  static class CombatResult {

    long player1Score;
    long player2Score;

    CombatResult validate() {
      if (player1Score == player2Score) {
        throw new IllegalStateException(
            String.format("Game resulted in a tie? %s vs %s", player1Score, player2Score)
        );
      }
      return this;
    }

    boolean player1Won() {
      return player1Score > player2Score;
    }

  }

  @Value
  static class Player {

    int playerNumber;
    Deque<Integer> cards = new LinkedList<>();

    Player subDeck(int numberOfCards) {
      Player newPlayer = new Player(playerNumber);
      int counter = 0;
      for (Integer card : cards) {
        if (++counter > numberOfCards) {
          break;
        }
        newPlayer.add(card);
      }
      return newPlayer;
    }

    int cardCount() {
      return cards.size();
    }

    boolean hasCards() {
      return !cards.isEmpty();
    }

    Integer draw() {
      return cards.removeFirst();
    }

    Player add(int card) {
      cards.add(card);
      return this;
    }

    public long calculateScore() {
      int multiplier = cards.size();
      long score = 0;
      for (Integer card : cards) {
        score += card * multiplier--;
      }
      return score;
    }

    @Override
    public String toString() {
      return toString(", ");
    }

    public String toOriginalString() {
      return String.format("Player %s:%n%s%n", playerNumber, toString("\n"));
    }

    public String toString(String delimiter) {
      return cards.stream().map(String::valueOf).collect(Collectors.joining(delimiter));
    }

  }

}
