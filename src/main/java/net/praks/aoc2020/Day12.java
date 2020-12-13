package net.praks.aoc2020;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * <a href="https://adventofcode.com/2020/day/12">Day 12: Rain Risk</a>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Day12 {

  @Value
  static class ManoeuvreInstructions {

    List<ManoeuvreInstruction> instructions;

    static ManoeuvreInstructions parse(List<String> instructionStrings) {
      return new ManoeuvreInstructions(
          instructionStrings.stream().map(ManoeuvreInstruction::parse).collect(Collectors.toList())
      );
    }

    MovementContext move(MovementContext context) {
      MovementContext currentContext = context;
      for (ManoeuvreInstruction instruction : instructions) {
        currentContext = currentContext.applyInstruction(instruction);
      }
      return currentContext;
    }

  }

  @Value
  private static class ManoeuvreInstruction {

    private static final Pattern PATTERN = Pattern.compile("(\\w)(\\d+)");

    ManoeuvreInstructionType type;
    int parameter;

    static ManoeuvreInstruction parse(String instructionString) {
      Matcher matcher = PATTERN.matcher(instructionString);
      if (matcher.matches()) {
        return new ManoeuvreInstruction(
            ManoeuvreInstructionType.valueOf(matcher.group(1)),
            Integer.parseInt(matcher.group(2))
        );
      }
      else {
        throw new IllegalArgumentException("Illegal instruction: " + instructionString);
      }
    }

    @Override
    public String toString() {
      return String.valueOf(type) + parameter;
    }

  }

  @Value
  static class MovementContext {

    PositionWithDirection shipPosition;
    MovementWithWaypointContext withWaypointContext;

    private MovementContext applyInstruction(ManoeuvreInstruction instruction) {
      return instruction.getType().move(this, instruction.getParameter());
    }

  }

  @Value
  static class MovementWithWaypointContext {

    Position shipPosition;
    Position waypointPosition;

    MovementWithWaypointContext newWaypointPosition(Position newWaypointPosition) {
      return new MovementWithWaypointContext(shipPosition, newWaypointPosition);
    }

    MovementWithWaypointContext newWaypointShipPosition(Position newWaypointShipPosition) {
      return new MovementWithWaypointContext(newWaypointShipPosition, waypointPosition);
    }

  }

  @Value
  static class PositionWithDirection {

    Position position;
    MoveDirection direction;

    public int getEastPosition() {
      return position.getEastPosition();
    }

    public int getNorthPosition() {
      return position.getNorthPosition();
    }

    int calcManhattanDistance() {
      return position.calcManhattanDistance();
    }

  }

  @Value
  static class Position {

    int eastPosition;
    int northPosition;

    int calcManhattanDistance() {
      return Math.abs(eastPosition) + Math.abs(northPosition);
    }

  }

  @SuppressWarnings("unused") // used indirectly
  enum ManoeuvreInstructionType {
    N(new MoveShipInDirection(MoveDirection.N), new MoveWaypointInDirection(MoveDirection.N)),
    E(new MoveShipInDirection(MoveDirection.E), new MoveWaypointInDirection(MoveDirection.E)),
    S(new MoveShipInDirection(MoveDirection.S), new MoveWaypointInDirection(MoveDirection.S)),
    W(new MoveShipInDirection(MoveDirection.W), new MoveWaypointInDirection(MoveDirection.W)),
    L(new TurnShipInDirection(TurnDirection.L), new TurnWaypointInDirection(TurnDirection.L)),
    R(new TurnShipInDirection(TurnDirection.R), new TurnWaypointInDirection(TurnDirection.R)),
    F(new MoveShipForward(), new MoveShipForwardUsingWaypoint());

    private final ShipMoveLogic shipMoveLogic;
    private final WaypointMoveLogic waypointMoveLogic;

    ManoeuvreInstructionType(ShipMoveLogic shipMoveLogic, WaypointMoveLogic waypointMoveLogic) {
      this.shipMoveLogic = shipMoveLogic;
      this.waypointMoveLogic = waypointMoveLogic;
    }

    private MovementContext move(MovementContext context, int parameter) {
      return new MovementContext(
          shipMoveLogic.apply(context.getShipPosition(), parameter),
          waypointMoveLogic.apply(context.getWithWaypointContext(), parameter)
      );
    }

  }

  interface ShipMoveLogic {
    PositionWithDirection apply(PositionWithDirection shipPosition, int parameter);
  }

  interface WaypointMoveLogic {
    MovementWithWaypointContext apply(MovementWithWaypointContext context, int parameter);
  }

  @Value
  private static class MoveShipInDirection implements ShipMoveLogic {

    MoveDirection moveDirection;

    @Override
    public PositionWithDirection apply(PositionWithDirection shipPosition, int parameter) {
      return new PositionWithDirection(
          new Position(
              moveDirection.calcNewEastPosition(shipPosition.getEastPosition(), parameter),
              moveDirection.calcNewNorthPosition(shipPosition.getNorthPosition(), parameter)
          ),
          shipPosition.getDirection()
      );
    }

  }

  @Value
  private static class MoveWaypointInDirection implements WaypointMoveLogic {

    MoveDirection moveDirection;

    @Override
    public MovementWithWaypointContext apply(MovementWithWaypointContext context, int parameter) {
      Position waypointPosition = context.getWaypointPosition();
      return context.newWaypointPosition(
          new Position(
              moveDirection.calcNewEastPosition(waypointPosition.getEastPosition(), parameter),
              moveDirection.calcNewNorthPosition(waypointPosition.getNorthPosition(), parameter)
          )
      );
    }

  }

  private static class MoveShipForward implements ShipMoveLogic {

    @Override
    public PositionWithDirection apply(PositionWithDirection shipPosition, int parameter) {
      MoveDirection direction = shipPosition.getDirection();
      return new PositionWithDirection(
          new Position(
              direction.calcNewEastPosition(shipPosition.getEastPosition(), parameter),
              direction.calcNewNorthPosition(shipPosition.getNorthPosition(), parameter)
          ),
          direction
      );
    }

  }

  private static class MoveShipForwardUsingWaypoint implements WaypointMoveLogic {

    @Override
    public MovementWithWaypointContext apply(MovementWithWaypointContext context, int parameter) {
      Position shipPosition = context.getShipPosition();
      Position waypointPosition = context.getWaypointPosition();
      return context.newWaypointShipPosition(
          new Position(
              shipPosition.getEastPosition() + parameter * waypointPosition.getEastPosition(),
              shipPosition.getNorthPosition() + parameter * waypointPosition.getNorthPosition()
          )
      );
    }

  }

  @Value
  private static class TurnShipInDirection implements ShipMoveLogic {

    TurnDirection turnDirection;

    @Override
    public PositionWithDirection apply(PositionWithDirection shipPosition, int parameter) {
      return new PositionWithDirection(
          new Position(
              shipPosition.getEastPosition(),
              shipPosition.getNorthPosition()
          ),
          turnDirection.calcNewDirection(shipPosition.getDirection(), parameter)
      );
    }

  }

  @Value
  private static class TurnWaypointInDirection implements WaypointMoveLogic {

    TurnDirection turnDirection;

    @Override
    public MovementWithWaypointContext apply(MovementWithWaypointContext context, int parameter) {
      Position waypointPosition = context.getWaypointPosition();
      return context.newWaypointPosition(
          turnDirection.calcNewWaypointPosition(waypointPosition, parameter)
      );
    }

  }

  @Getter
  enum MoveDirection {
    N(1, 0, 0),
    E(0, 1, 90),
    S(-1, 0, 90 * 2),
    W(0, -1, 90 * 3);

    private final int northPositionIncMultiplier;
    private final int eastPositionIncMultiplier;
    private final int degreesClockwiseFromNorth;

    MoveDirection(int northPositionIncMultiplier, int eastPositionIncMultiplier, int degreesClockwiseFromNorth) {
      this.northPositionIncMultiplier = northPositionIncMultiplier;
      this.eastPositionIncMultiplier = eastPositionIncMultiplier;
      this.degreesClockwiseFromNorth = degreesClockwiseFromNorth;
    }

    int calcNewNorthPosition(int currentPosition, int positionInc) {
      return currentPosition + northPositionIncMultiplier * positionInc;
    }

    int calcNewEastPosition(int currentPosition, int positionInc) {
      return currentPosition + eastPositionIncMultiplier * positionInc;
    }

    static MoveDirection fromDegreesClockwiseFromNorth(int degreesClockwiseFromNorth) {
      if (degreesClockwiseFromNorth == N.degreesClockwiseFromNorth) {
        return N;
      }
      else if (degreesClockwiseFromNorth == E.degreesClockwiseFromNorth) {
        return E;
      }
      else if (degreesClockwiseFromNorth == S.degreesClockwiseFromNorth) {
        return S;
      }
      else if (degreesClockwiseFromNorth == W.degreesClockwiseFromNorth) {
        return W;
      }
      else {
        throw new IllegalArgumentException("Not valid degrees: " + degreesClockwiseFromNorth);
      }
    }
  }

  enum TurnDirection {
    L(-1, 1, -1),
    R(1, -1, 1);

    private static final int DEGREES_FULL_CIRCLE = 90 * 4;

    private final int degreesClockwiseFromNorthMultiplier;
    private final int waypointNorthPositionMultiplier;
    private final int waypointEastPositionMultiplier;

    TurnDirection(
        int degreesClockwiseFromNorthMultiplier,
        int waypointNorthPositionMultiplier,
        int waypointEastPositionMultiplier
    ) {
      this.degreesClockwiseFromNorthMultiplier = degreesClockwiseFromNorthMultiplier;
      this.waypointNorthPositionMultiplier = waypointNorthPositionMultiplier;
      this.waypointEastPositionMultiplier = waypointEastPositionMultiplier;
    }

    MoveDirection calcNewDirection(MoveDirection currentDirection, int degreesClockwiseFromNorthInc) {
      int newDegreesClockwiseFromNorth = currentDirection.getDegreesClockwiseFromNorth() +
          degreesClockwiseFromNorthMultiplier * degreesClockwiseFromNorthInc;
      while (newDegreesClockwiseFromNorth < 0) {
        newDegreesClockwiseFromNorth += DEGREES_FULL_CIRCLE;
      }
      return MoveDirection.fromDegreesClockwiseFromNorth(newDegreesClockwiseFromNorth % DEGREES_FULL_CIRCLE);
    }

    Position calcNewWaypointPosition(Position currentPosition, int degreesClockwiseFromNorthInc) {
      int numberOfIterations = degreesClockwiseFromNorthInc / 90;
      int newEastPosition = currentPosition.getEastPosition();
      int newNorthPosition = currentPosition.getNorthPosition();
      for (int i = 0; i < numberOfIterations; i++) {
        int east = newNorthPosition;
        int north = newEastPosition;
        newEastPosition = east * waypointEastPositionMultiplier;
        newNorthPosition = north * waypointNorthPositionMultiplier;
      }
      return new Position(newEastPosition, newNorthPosition);
    }

  }

}
