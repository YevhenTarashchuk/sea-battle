package com.sacret.board;


import com.sacret.exception.ExistingPointException;
import com.sacret.exception.OutOfBoardException;
import com.sacret.ship.Point;
import com.sacret.ship.Ship;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@ToString
@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Board {

    int row = 10;
    int colum = 10;
    List<Ship> ships = new ArrayList<>();
    List<Point>  misses = new ArrayList<>();
    List<Point> hits =  new ArrayList<>();

    public void print(boolean showShips) {
        System.out.print("\t   ");

        for (int x = 0; x < colum; x++) {
            System.out.print(x + " ");
        }

        System.out.print("\n\t   ");

        for (int x = 0; x < colum; x++) {
            System.out.print("_ ");
        }

        System.out.println();

        for (int y = 0; y < row; y++) {
            System.out.print("\t" + y + " |");
            for (int x = 0; x < colum; x++) {
                boolean notFound = true;
                for (Ship ship : ships) {
                    for(Point point: ship.getPoints()) {
                        if (x == point.getX() && y == point.getY()) {
                            if (point.isAlive()) {
                                if (showShips) {
                                    System.out.print("# ");
                                } else {
                                    System.out.print(". ");
                                }
                            } else {
                                System.out.print("x ");
                            }
                            notFound = false;
                        }
                    }
                    if (notFound) {
                        for (Point point : misses) {
                            if (x == point.getX() && y == point.getY()) {
                                System.out.print("o ");
                                notFound = false;
                            }
                        }
                    }
                }
                if (notFound) {
                    System.out.print(". ");
                }
            }

            int finalY = y + 1;
            if (finalY < 5) {
                System.out.printf("   %s-deck ships on the board: %s",
                        finalY, (int) this.getShips().stream().filter(Ship::isAlive).filter(ship -> ship.getPoints().size() == finalY).count());
            }
            System.out.println();
        }
        System.out.println();
    }

    public boolean placeShip(Ship ship) {
        try {
            ship.getPoints().forEach(point -> {
                if (point.getX() >= colum || point.getX() < 0) {
                    throw new OutOfBoardException(String.format("x: %s is out of columns", point.getX()));
                } else if (point.getY() >= row || point.getY() < 0) {
                    throw new OutOfBoardException(String.format("y: %s is out of rows", point.getY()));
                }
                ships.forEach(existingShip ->
                        existingShip.getPoints().forEach(existingPoint -> {
                            if ((point.getX() == existingPoint.getX() || point.getX() == existingPoint.getX() + 1 ||  point.getX() == existingPoint.getX() - 1)
                                    && (point.getY() == existingPoint.getY() || point.getY() == existingPoint.getY() + 1 || point.getY() == existingPoint.getY() -1)) {
                                throw new ExistingPointException(String.format("Location of Point(%s, %s) is incorrect for existing ships", point.getX(), point.getY()));
                            }
                        }));
            });
        } catch (OutOfBoardException | ExistingPointException e) {
            System.out.println(e.getMessage());
            return false;
        }

        System.out.println("Ship was successfully placed");
        return ships.add(ship);
    }
}
