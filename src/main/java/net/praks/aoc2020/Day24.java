package net.praks.aoc2020;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <a href="https://adventofcode.com/2020/day/24">Day 24: Lobby Layout</a>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Day24 {

  static List<List<TileDirection>> parseDirections(List<String> lines) {
    return lines.stream().map(Day24::parseDirectionList).collect(Collectors.toList());
  }

  static List<TileDirection> parseDirectionList(String line) {
    List<TileDirection> directionList = new ArrayList<>();
    StringBuilder sb = new StringBuilder(2);
    for (int i = 0; i < line.length(); i++) {
      char c = line.charAt(i);
      if (c == 's' || c == 'n') {
        sb.append(c);
      }
      else {
        sb.append(c);
        directionList.add(TileDirection.valueOf(sb.toString()));
        sb.setLength(0);
      }
    }
    return directionList;
  }

  @Value
  static class Tiles {

    private static final Tile WHITE_TILE = new Tile(new Position(0, 0), false);

    Map<Position, Tile> tileMap = new HashMap<>();

    void put(Tile tile) {
      tileMap.put(tile.getPosition(), tile);
    }

    void flip(Position position) {
      Tile newTile = tileMap.computeIfPresent(position, (pos, tile) -> tile.flip());
      if (newTile == null) {
        newTile = new Tile(position, true);
        tileMap.put(position, newTile);
      }
    }

    void flip(List<TileDirection> directions) {
      flip(convertDirectionsToPosition(directions));
    }

    static Position convertDirectionsToPosition(List<TileDirection> directions) {
      Position position = new Position(0, 0);
      for (TileDirection direction : directions) {
        position = position.nextPosition(direction);
      }
      return position;
    }

    void flipAll(List<List<TileDirection>> directions) {
      directions.forEach(this::flip);
    }

    Tiles loopNextDayArt(int dayCount) {
      Tiles tiles = this;
      for (int i = 1; i <= dayCount; i++) {
        tiles = tiles.nextDayArt();
      }
      return tiles;
    }

    Tiles nextDayArt() {
      Tiles newTiles = new Tiles();
      tileMap.values().stream().filter(Tile::isBlack).forEach(tile -> {
        List<Position> adjacentPositions = tile.getPosition().adjacentPositions();
        long blackTilesCount = countBlackTiles(adjacentPositions);
        if (blackTilesCount == 1 || blackTilesCount == 2) {
          newTiles.put(tile);
        }
        adjacentPositions.stream()
            .filter(pos -> tileMap.getOrDefault(pos, WHITE_TILE).isWhite())
            .forEach(pos -> {
              long blackTilesCountAdjacentToWhite = countBlackTiles(pos.adjacentPositions());
              if (blackTilesCountAdjacentToWhite == 2) {
                newTiles.put(new Tile(pos, true));
              }
            });
      });
      return newTiles;
    }

    private long countBlackTiles(List<Position> positions) {
      return positions.stream()
          .filter(pos -> tileMap.getOrDefault(pos, WHITE_TILE).isBlack())
          .count();
    }

    long countBlackTiles() {
      return tileMap.values().stream().filter(Tile::isBlack).count();
    }

  }

  @Value
  static class Tile {

    Position position;
    boolean isBlack;

    Tile flip() {
      return new Tile(position, !isBlack);
    }

    boolean isWhite() {
      return !isBlack;
    }

  }

  @Value
  static class Position {

    int eastPosition;
    int northPosition;

    Position nextPosition(TileDirection direction) {
      return new Position(
          eastPosition + direction.getEastPositionInc(),
          northPosition + direction.getNorthPositionInc()
      );
    }

    List<Position> adjacentPositions() {
      return Arrays.stream(TileDirection.values()).map(this::nextPosition).collect(Collectors.toList());
    }

  }

  @Getter
  enum TileDirection {
    e(10, 0),
    se(5, -10),
    sw(-5, -10),
    w(-10, 0),
    nw(-5, 10),
    ne(5, 10);

    final int eastPositionInc;
    final int northPositionInc;

    TileDirection(int eastPositionInc, int northPositionInc) {
      this.eastPositionInc = eastPositionInc;
      this.northPositionInc = northPositionInc;
    }

  }

}
