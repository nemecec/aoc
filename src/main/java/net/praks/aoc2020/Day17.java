package net.praks.aoc2020;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * <a href="https://adventofcode.com/2020/day/17">Day 17: Conway Cubes</a>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Day17 {

  interface ElementWithCoordinate {
    int getCoordinate();
  }

  @Value
  static class GridSlice<T extends ElementWithCoordinate> implements ElementWithCoordinate {

    int coordinate;

    Map<Integer, T> elements = new TreeMap<>();

    Optional<T> get(int subCoordinate) {
      return Optional.ofNullable(elements.get(subCoordinate));
    }

    T getOrCreate(int subCoordinate, Function<Integer, T> constructor) {
      return elements.computeIfAbsent(subCoordinate, constructor);
    }

    void add(T element) {
      T previousElement = elements.putIfAbsent(element.getCoordinate(), element);
      if (previousElement != null) {
        throw new IllegalArgumentException(
            String.format("Element already exists: %s", element)
        );
      }
    }

    Stream<T> stream() {
      return elements.values().stream();
    }

  }

  interface Grid<T extends Coordinates> {
    int countAdjacentActivePoints(T coordinates);
  }

  @Value
  static class Grid3D implements Grid<Coordinates3D> {

    private static final int INITIAL_Z = 0;

    PointMapper<Coordinates3D> pointMapper = new PointMapper<>();
    // z, y, x
    GridSlice<GridSlice<GridSlice<Point<Coordinates3D>>>> points;

    public Grid3D(GridSlice<GridSlice<GridSlice<Point<Coordinates3D>>>> points) {
      this.points = points;
    }

    static Grid3D parse(List<String> stateLines) {
      GridSlice<GridSlice<GridSlice<Point<Coordinates3D>>>> points = new GridSlice<>(0);
      GridSlice<GridSlice<Point<Coordinates3D>>> yxPoints = new GridSlice<>(INITIAL_Z);
      points.add(yxPoints);
      for (int y = 0; y < stateLines.size(); y++) {
        final int yCoordinate = y;
        yxPoints.add(Point.parsePoints(
            stateLines.get(y),
            new GridSlice<>(y),
            x -> new Coordinates3D(INITIAL_Z, yCoordinate, x)
        ));
      }
      return new Grid3D(points);
    }

    Grid3D calculateNewGrid(int numberOfIterations) {
      Grid3D newGrid = this;
      for (int i = 0; i < numberOfIterations; i++) {
        newGrid = newGrid.calculateNewGrid();
      }
      return newGrid;
    }

    void addPoint(Point<Coordinates3D> point) {
      Coordinates3D coordinates = point.getCoordinates();
      GridSlice<GridSlice<Point<Coordinates3D>>> zSlice = points.getOrCreate(coordinates.getZ(), GridSlice::new);
      GridSlice<Point<Coordinates3D>> ySlice = zSlice.getOrCreate(coordinates.getY(), GridSlice::new);
      ySlice.add(point);
    }

    Grid3D calculateNewGrid() {
      GridSlice<GridSlice<GridSlice<Point<Coordinates3D>>>> newPoints = new GridSlice<>(0);
      Grid3D newGrid = new Grid3D(newPoints);
      activePoints().forEach(oldPoint -> newGrid.addPoint(pointMapper.apply(this, oldPoint)));
      activePoints().forEach(oldPoint -> oldPoint.coordinates.getAdjacent().forEach(adjacentCoordinates -> {
        Optional<Point<Coordinates3D>> existingPoint = newGrid.getPoint((Coordinates3D) adjacentCoordinates);
        if (!existingPoint.isPresent()) {
          newGrid.addPoint(pointMapper.apply(
              this,
              new Point<>(PointState.INACTIVE, (Coordinates3D) adjacentCoordinates)
          ));
        }
      }));
      return newGrid;
    }

    Optional<Point<Coordinates3D>> getPoint(Coordinates3D coordinates) {
      return points.get(coordinates.getZ()).flatMap(
          zSlice -> zSlice.get(coordinates.getY()).flatMap(
              ySlice -> ySlice.get(coordinates.getX())
          )
      );
    }

    Stream<Point<Coordinates3D>> activePoints() {
      return points.stream()
          .flatMap(zSlice -> zSlice.stream().flatMap(GridSlice::stream))
          .filter(Point::isActive);
    }

    public int countAdjacentActivePoints(Coordinates3D coordinates) {
      return (int) coordinates.getAdjacent().stream()
          .map(coords -> getPoint((Coordinates3D) coords))
          .filter(Optional::isPresent)
          .map(Optional::get)
          .filter(Point::isActive)
          .count();
    }

    int countActivePoints() {
      return points.stream()
          .flatMapToInt(
              zSlice -> zSlice.stream().mapToInt(
                  ySlice -> (int) ySlice.stream().filter(Point::isActive).count()
              )
          ).sum();
    }

    @Override
    public String toString() {
      return points.stream()
          .map(
              zSlice -> String.format("z=%s%n", zSlice.getCoordinate()) + zSlice.stream().map(
                  ySlice -> ySlice.stream().map(Point::toString).collect(Collectors.joining(""))
              ).collect(Collectors.joining("\n"))
          ).collect(Collectors.joining("\n\n"));
    }

  }

  @Value
  static class Grid4D implements Grid<Coordinates4D> {

    private static final int INITIAL_W = 0;
    private static final int INITIAL_Z = 0;

    PointMapper<Coordinates4D> pointMapper = new PointMapper<>();

    // w, z, y, x
    GridSlice<GridSlice<GridSlice<GridSlice<Point<Coordinates4D>>>>> points;

    public Grid4D(GridSlice<GridSlice<GridSlice<GridSlice<Point<Coordinates4D>>>>> points) {
      this.points = points;
    }

    static Grid4D parse(List<String> stateLines) {
      GridSlice<GridSlice<GridSlice<GridSlice<Point<Coordinates4D>>>>> points = new GridSlice<>(0);
      GridSlice<GridSlice<GridSlice<Point<Coordinates4D>>>> zyxPoints = new GridSlice<>(INITIAL_W);
      points.add(zyxPoints);
      GridSlice<GridSlice<Point<Coordinates4D>>> yxPoints = new GridSlice<>(INITIAL_Z);
      zyxPoints.add(yxPoints);
      for (int y = 0; y < stateLines.size(); y++) {
        final int yCoordinate = y;
        yxPoints.add(Point.parsePoints(
            stateLines.get(y),
            new GridSlice<>(y),
            x -> new Coordinates4D(INITIAL_W, INITIAL_Z, yCoordinate, x)
        ));
      }
      return new Grid4D(points);
    }

    Grid4D calculateNewGrid(int numberOfIterations) {
      Grid4D newGrid = this;
      for (int i = 0; i < numberOfIterations; i++) {
        newGrid = newGrid.calculateNewGrid();
      }
      return newGrid;
    }

    void addPoint(Point<Coordinates4D> point) {
      Coordinates4D coordinates = point.getCoordinates();
      GridSlice<GridSlice<GridSlice<Point<Coordinates4D>>>> wSlice = points.getOrCreate(coordinates.getW(), GridSlice::new);
      GridSlice<GridSlice<Point<Coordinates4D>>> zSlice = wSlice.getOrCreate(coordinates.getZ(), GridSlice::new);
      GridSlice<Point<Coordinates4D>> ySlice = zSlice.getOrCreate(coordinates.getY(), GridSlice::new);
      ySlice.add(point);
    }

    Grid4D calculateNewGrid() {
      GridSlice<GridSlice<GridSlice<GridSlice<Point<Coordinates4D>>>>> newPoints = new GridSlice<>(0);
      Grid4D newGrid = new Grid4D(newPoints);
      activePoints().forEach(oldPoint -> newGrid.addPoint(pointMapper.apply(this, oldPoint)));
      activePoints().forEach(oldPoint -> oldPoint.coordinates.getAdjacent().forEach(adjacentCoordinates -> {
        Optional<Point<Coordinates4D>> existingPoint = newGrid.getPoint((Coordinates4D) adjacentCoordinates);
        if (!existingPoint.isPresent()) {
          newGrid.addPoint(pointMapper.apply(
              this,
              new Point<>(PointState.INACTIVE, (Coordinates4D) adjacentCoordinates)
          ));
        }
      }));
      return newGrid;
    }

    Optional<Point<Coordinates4D>> getPoint(Coordinates4D coordinates) {
      return points.get(coordinates.getW()).flatMap(
          wSlice -> wSlice.get(coordinates.getZ()).flatMap(
              zSlice -> zSlice.get(coordinates.getY()).flatMap(
                  ySlice -> ySlice.get(coordinates.getX())
              )
          )
      );
    }

    Stream<Point<Coordinates4D>> activePoints() {
      return points.stream()
          .flatMap(wSlice -> wSlice.stream().flatMap(
              zSlice -> zSlice.stream().flatMap(GridSlice::stream)
          )).filter(Point::isActive);
    }

    public int countAdjacentActivePoints(Coordinates4D coordinates) {
      return (int) coordinates.getAdjacent().stream()
          .map(coords -> getPoint((Coordinates4D) coords))
          .filter(Optional::isPresent)
          .map(Optional::get)
          .filter(Point::isActive)
          .count();
    }

    int countActivePoints() {
      return points.stream()
          .flatMapToInt(
              wSlice -> wSlice.stream().flatMapToInt(
                  zSlice -> zSlice.stream().mapToInt(
                      ySlice -> (int) ySlice.stream().filter(Point::isActive).count()
                  )
              )
          ).sum();
    }

    @Override
    public String toString() {
      return points.stream()
          .map(
              wSlice -> wSlice.stream().map(
                  zSlice -> String.format("w=%s z=%s%n", wSlice.getCoordinate(), zSlice.getCoordinate()) +
                      zSlice.stream().map(
                          ySlice -> ySlice.stream().map(Point::toString).collect(Collectors.joining(""))
                      ).collect(Collectors.joining("\n"))
              ).collect(Collectors.joining("\n\n"))
          ).collect(Collectors.joining(""));
    }

  }

  static class PointMapper<T extends Coordinates> implements BiFunction<Grid<T>, Point<T>, Point<T>> {

    public Point<T> apply(Grid<T> grid, Point<T> point) {
      PointState newState;
      int activePointCount = grid.countAdjacentActivePoints(point.getCoordinates());
      PointState pointState = point.getState();
      if (pointState == PointState.ACTIVE && activePointCount != 2 && activePointCount != 3) {
        newState = PointState.INACTIVE;
      }
      else if (pointState == PointState.INACTIVE && activePointCount == 3) {
        newState = PointState.ACTIVE;
      }
      else {
        newState = pointState;
      }
      return point.newPointWithState(newState);
    }

  }

  @Value
  static class Point<T extends Coordinates> implements ElementWithCoordinate {

    PointState state;
    T coordinates;

    static <T extends Coordinates> GridSlice<Point<T>> parsePoints(
        String pointRow,
        GridSlice<Point<T>> points,
        IntFunction<T> coordinatesFactory
    ) {
      char[] pointRowChars = pointRow.toCharArray();
      for (int x = 0; x < pointRowChars.length; x++) {
        points.add(new Point<>(PointState.parse(pointRowChars[x]), coordinatesFactory.apply(x)));
      }
      return points;
    }

    Point<T> newPointWithState(PointState newState) {
      return new Point<>(newState, coordinates);
    }

    boolean isActive() {
      return this.state == PointState.ACTIVE;
    }

    @Override
    public int getCoordinate() {
      return coordinates.getX();
    }

    @Override
    public String toString() {
      return String.valueOf(state.identifier);
    }

  }

  interface Coordinates {
    int getX();
  }

  @Value
  static class Coordinates3D implements Coordinates {

    int z;
    int y;
    int x;

    public List<Coordinates> getAdjacent() {
      Set<Coordinates> coordinates = new HashSet<>(26);
      IntStream.rangeClosed(-1, 1).forEach(
          zInc -> IntStream.rangeClosed(-1, 1).forEach(
              yInc -> IntStream.rangeClosed(-1, 1).forEach(
                  xInc -> {
                    if (zInc != 0 || yInc != 0 || xInc != 0) {
                      coordinates.add(new Coordinates3D(z + zInc, y + yInc, x + xInc));
                    }
                  }
              )
          )
      );
      return new ArrayList<>(coordinates);
    }

  }

  @Value
  static class Coordinates4D implements Coordinates {

    int w;
    int z;
    int y;
    int x;

    public List<Coordinates> getAdjacent() {
      Set<Coordinates> coordinates = new HashSet<>(26);
      IntStream.rangeClosed(-1, 1).forEach(
          wInc -> IntStream.rangeClosed(-1, 1).forEach(
              zInc -> IntStream.rangeClosed(-1, 1).forEach(
                  yInc -> IntStream.rangeClosed(-1, 1).forEach(
                      xInc -> {
                        if (wInc != 0 || zInc != 0 || yInc != 0 || xInc != 0) {
                          coordinates.add(new Coordinates4D(w + wInc, z + zInc, y + yInc, x + xInc));
                        }
                      }
                  )
              )
          )
      );
      return new ArrayList<>(coordinates);
    }

  }

  enum PointState {
    ACTIVE('#'),
    INACTIVE('.');

    private final char identifier;

    PointState(char identifier) {
      this.identifier = identifier;
    }

    static PointState parse(char c) {
      if (c == ACTIVE.identifier) {
        return ACTIVE;
      }
      else if (c == INACTIVE.identifier) {
        return INACTIVE;
      }
      else {
        throw new IllegalArgumentException("Unknown state identifier: " + c);
      }
    }

  }

}
