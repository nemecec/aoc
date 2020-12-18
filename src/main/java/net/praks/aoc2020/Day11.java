package net.praks.aoc2020;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * <a href="https://adventofcode.com/2020/day/11">Day 11: Seating System</a>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Day11 {

  @Value
  static class SeatMap {

    List<List<Seat>> seatRows;

    public SeatMap(List<List<Seat>> seatRows) {
      this.seatRows = seatRows;
    }

    static SeatMap parse(List<String> mapLines) {
      List<List<Seat>> seatRows = new ArrayList<>(mapLines.size());
      for (int i = 0; i < mapLines.size(); i++) {
        seatRows.add(Seat.parseSeats(mapLines.get(i), i));
      }
      return new SeatMap(seatRows);
    }

    SeatMap calculateNewMapsUntilStable(SeatMapper seatMapper) {
      SeatMap previousMap;
      SeatMap newMap = this;
      do {
        previousMap = newMap;
        newMap = previousMap.calculateNewMap(seatMapper);
      } while (!previousMap.equals(newMap));
      return previousMap;
    }

    SeatMap calculateNewMap(SeatMapper seatMapper) {
      return new SeatMap(
          seatRows.stream()
              .map(row -> row.stream().map(seat -> seatMapper.map(this, seat)).collect(Collectors.toList()))
              .collect(Collectors.toList())
      );
    }

    Optional<Seat> getSeat(Coordinates coordinates) {
      if (coordinates.getRowIndex() >= 0 && coordinates.getRowIndex() < seatRows.size()) {
        List<Seat> row = seatRows.get(coordinates.getRowIndex());
        if (coordinates.getColumnIndex() >= 0 && coordinates.getColumnIndex() < row.size()) {
          return Optional.of(row.get(coordinates.getColumnIndex()));
        }
      }
      return Optional.empty();
    }

    int countAdjacentOccupiedSeats(Coordinates coordinates) {
      return (int) Arrays.stream(Direction.values())
          .map(coordinates::getAdjacent)
          .map(this::getSeat)
          .filter(Optional::isPresent)
          .map(Optional::get)
          .filter(seat -> seat.hasState(SeatState.OCCUPIED))
          .count();
    }

    boolean hasFirstOccupiedSeatInDirection(Coordinates coordinates, Direction direction) {
      Seat previousSeat = getSeat(coordinates)
          .orElseThrow(() -> new IllegalArgumentException("Illegal coordinates: " + coordinates));
      do {
        Optional<Seat> seat = getSeat(previousSeat.getCoordinates().getAdjacent(direction));
        if (!seat.isPresent()) {
          break;
        }
        previousSeat = seat.get();
        if (!previousSeat.hasState(SeatState.FLOOR)) {
          return previousSeat.hasState(SeatState.OCCUPIED);
        }
      } while (true);
      return false;
    }

    int countOccupiedSeatsInAllDirections(Coordinates coordinates) {
      return (int) Arrays.stream(Direction.values())
          .filter(direction -> hasFirstOccupiedSeatInDirection(coordinates, direction))
          .count();
    }

    int countOccupiedSeats() {
      return seatRows.stream()
          .mapToInt(row -> (int) row.stream().filter(seat -> seat.hasState(SeatState.OCCUPIED)).count()).sum();
    }

    @Override
    public String toString() {
      return seatRows.stream()
          .map(row -> row.stream().map(Seat::toString).collect(Collectors.joining("")))
          .collect(Collectors.joining("\n"));
    }
  }

  @SuppressWarnings("unused") // used via reflection
  enum SeatMapper {
    ADJACENT((seatMap, seat) -> {
      SeatState newState;
      int occupiedSeatCount = seatMap.countAdjacentOccupiedSeats(seat.getCoordinates());
      SeatState seatState = seat.getState();
      if (seatState == SeatState.EMPTY && occupiedSeatCount == 0) {
        newState = SeatState.OCCUPIED;
      }
      else if (seatState == SeatState.OCCUPIED && occupiedSeatCount >= 4) {
        newState = SeatState.EMPTY;
      }
      else {
        newState = seatState;
      }
      return seat.newSeatWithState(newState);
    }),
    RELAXED((seatMap, seat) -> {
      SeatState newState;
      int occupiedSeatCount = seatMap.countOccupiedSeatsInAllDirections(seat.getCoordinates());
      SeatState seatState = seat.getState();
      if (seatState == SeatState.EMPTY && occupiedSeatCount == 0) {
        newState = SeatState.OCCUPIED;
      }
      else if (seatState == SeatState.OCCUPIED && occupiedSeatCount >= 5) {
        newState = SeatState.EMPTY;
      }
      else {
        newState = seatState;
      }
      return seat.newSeatWithState(newState);
    });

    private final BiFunction<SeatMap, Seat, Seat> newSeatMapper;

    SeatMapper(BiFunction<SeatMap, Seat, Seat> newSeatMapper) {
      this.newSeatMapper = newSeatMapper;
    }

    Seat map(SeatMap seatMap, Seat seat) {
      return newSeatMapper.apply(seatMap, seat);
    }

  }

  @Value
  static class Seat {

    SeatState state;
    Coordinates coordinates;

    static List<Seat> parseSeats(String seatRow, int rowIndex) {
      List<Seat> seats = new ArrayList<>(seatRow.length());
      char[] seatRowChars = seatRow.toCharArray();
      for (int i = 0; i < seatRowChars.length; i++) {
        seats.add(new Seat(SeatState.parse(seatRowChars[i]), new Coordinates(rowIndex, i)));
      }
      return seats;
    }

    Seat newSeatWithState(SeatState newState) {
      return new Seat(newState, coordinates);
    }

    boolean hasState(SeatState state) {
      return this.state == state;
    }

    @Override
    public String toString() {
      return String.valueOf(state.identifier);
    }

  }

  @Getter
  enum Direction {
    NW(-1, -1),
    N(-1, 0),
    NE(-1, 1),
    W(0, -1),
    E(0, 1),
    SW(1, -1),
    S(1, 0),
    SE(1, 1);

    private final int rowInc;
    private final int columnInc;

    Direction(int rowInc, int columnInc) {
      this.rowInc = rowInc;
      this.columnInc = columnInc;
    }

  }

  @Value
  static class Coordinates {

    int rowIndex;
    int columnIndex;

    Coordinates getAdjacent(Direction direction) {
      return new Coordinates(rowIndex + direction.getRowInc(), columnIndex + direction.getColumnInc());
    }

  }

  enum SeatState {
    FLOOR('.'),
    EMPTY('L'),
    OCCUPIED('#');

    private final char identifier;

    SeatState(char identifier) {
      this.identifier = identifier;
    }

    static SeatState parse(char c) {
      if (c == EMPTY.identifier) {
        return EMPTY;
      }
      else if (c == FLOOR.identifier) {
        return FLOOR;
      }
      else if (c == OCCUPIED.identifier) {
        return OCCUPIED;
      }
      else {
        throw new IllegalArgumentException("Unknown state identifier: " + c);
      }
    }

  }

}
