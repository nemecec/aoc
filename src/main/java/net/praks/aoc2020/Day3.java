package net.praks.aoc2020;

import lombok.Value;

import java.util.List;

/**
 * <a href="https://adventofcode.com/2020/day/3">Day 3: Toboggan Trajectory</a>
 */
public class Day3 {

  int countTreesForSlope(int columnInc, int rowInc, Map map) {
    int currentRow = 1;
    int currentColumn = 1;
    int rowCount = map.getRowCount();
    int treeCount = 0;
    while (currentRow <= rowCount) {
      MapPoint point = map.getPointAt(currentRow, currentColumn);
      if (point.isTree()) {
        treeCount++;
      }
      currentColumn += columnInc;
      currentRow += rowInc;
    }
    return treeCount;
  }

  @Value
  static class Map {

    private static final char TREE = '#';
    private static final char EMPTY = '.';

    MapPoint[][] points;

    MapPoint getPointAt(int row, int column) {
      int rowIndex = normalize(row, getRowCount());
      int columnIndex = normalize(column, getColumnCount());
      return points[rowIndex][columnIndex];
    }

    int getRowCount() {
      return points.length;
    }

    int getColumnCount() {
      return points[0].length;
    }

    private int normalize(int coordinate, int coordinateLength) {
      return (coordinate - 1) % coordinateLength;
    }

    static Map parse(List<String> mapRowsAsString) {
      MapPoint[][] points = new MapPoint[mapRowsAsString.size()][];
      for (int i = 0; i < mapRowsAsString.size(); i++) {
        String mapRowStr = mapRowsAsString.get(i).trim();
        points[i] = new MapPoint[mapRowStr.length()];
        for (int j = 0; j < mapRowStr.length(); j++) {
          char mapPointChar = mapRowStr.charAt(j);
          points[i][j] = new MapPoint(mapPointChar == TREE);
        }
      }
      return new Map(points);
    }

    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder(getRowCount() * getColumnCount());
      for (MapPoint[] rowPoints : points) {
        for (MapPoint point : rowPoints) {
          sb.append(point);
        }
        sb.append('\n');
      }
      return sb.toString();
    }
  }

  @Value
  private static class MapPoint {

    boolean isTree;

    @Override
    public String toString() {
      return String.valueOf(isTree ? Map.TREE : Map.EMPTY);
    }
  }

}
