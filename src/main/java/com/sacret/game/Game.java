package com.sacret.game;

import com.sacret.board.Board;
import com.sacret.enumeration.DifficultyLevel;
import com.sacret.exception.OutOfBoardException;
import com.sacret.exception.ReAttackPointException;
import com.sacret.ship.Point;
import com.sacret.ship.Ship;
import com.sacret.enumeration.Location;
import com.sacret.util.ReaderUtil;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.util.*;
import java.util.stream.Collectors;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Game {

    Random random = new Random();

    public void start() {
        Board userBoard = new Board();
        Board computerBoard = new Board();
        this.autoPlaceShips(computerBoard, false, false);

        DifficultyLevel level = DifficultyLevel.valueOf(
                ReaderUtil.readInteger("\tWelcome to Sea Battle. \n\nSelect game difficulty:  " +
                        "0 - cheater, 1 - easy, 2 - normal, 3 - hardcore: ")
        );

        int choice = ReaderUtil.readInteger("\nIf you want to place ships yourself press 0, automatically press 1: ");

        if (choice == 0) {
            this.placeShips(userBoard);
        } else {
            this.autoPlaceShips(userBoard, true, true);
        }

        boolean winner;
        while (!(winner = isGameOver(computerBoard)) && !isGameOver(userBoard)) {
            System.out.print("\033[H\033[2J");
            System.out.printf("Press \"Enter\" between rounds. Game difficulty: %s\n", level.name());
            System.out.println("\n\tEnemy:\n");

            if (DifficultyLevel.CHEATER.equals(level)) {
                computerBoard.print(true);
            } else {
                computerBoard.print(false);
            }

            System.out.println("\t______________________\n");
            System.out.println("\tYou:\n");

            userBoard.print(true);

            System.out.println();

            Point attackPoint = this.buildAttackPoint();

            while (!this.attackShip(computerBoard, attackPoint, level)) {
                attackPoint = this.buildAttackPoint();
            }

            this.autoAttackShip(userBoard, level);

            ReaderUtil.waiteEnter();
        }

        System.out.print("\033[H\033[2J");

        if (winner) {
            System.out.print("\n\n\t YOU WIN! ");
        } else {
            System.out.print("\n\n\t GAME OVER. ");
        }
        System.out.println("Russian battle ship go fuck yourself!!!");

        ReaderUtil.waiteEnter();
    }

    private void autoPlaceShips(Board board, boolean showShips, boolean showBoard) {
        for (int i = 4, j = 1; i > 0; i--, j++) {
            for (int count = 0; count < j; count++ ) {

                int x = random.nextInt(board.getColum());
                int y = random.nextInt(board.getRow());

                Location location = null;
                if (i != 1) {
                    location = Location.valueOf(random.nextInt(2));
                }

                while (!(i == 1 ? board.placeShip(Ship.buildShip(new Point(x, y)))
                        : board.placeShip(Ship.buildShip(new Point(x, y), Objects.requireNonNull(location), i)))) {

                    x = random.nextInt(board.getColum());
                    y = random.nextInt(board.getRow());

                    if (i != 1) {
                        location = Location.valueOf(random.nextInt(2));
                    }
                }
            }
        }
        if (showBoard) {
            board.print(showShips);
        } else {
            System.out.print("\033[H\033[2J");
        }
    }

    private void placeShips(Board board) {
        System.out.print("\033[H\033[2J");
        board.print(true);

        for (int i = 4, j = 1; i > 0; i--, j++) {

            for (int count = 0; count < j; count++ ) {

                System.out.printf("place %s deck ship", i);
                System.out.println();

                int x = ReaderUtil.readInteger("Enter X of ship head: ");
                int y = ReaderUtil.readInteger("Enter Y of ship head: ");

                Location location = null;
                if (i != 1) {
                    location = Location.valueOf(ReaderUtil.readInteger("Enter location of ship (for HORIZONTAL - 0, for VERTICAL - 1): "));
                }

                while (!(i == 1 ? board.placeShip(Ship.buildShip(new Point(x, y)))
                        : board.placeShip(Ship.buildShip(new Point(x, y), location, i)))) {

                    System.out.printf("place %s deck ship", i);
                    System.out.println();

                    x = ReaderUtil.readInteger("Enter X of ship head: ");
                    y = ReaderUtil.readInteger("Enter Y of ship head: ");

                    if (i != 1) {
                        location = Location.valueOf(ReaderUtil.readInteger("Enter location of ship (for HORIZONTAL - 0, for VERTICAL - 1): "));
                    }
                }

                System.out.print("\033[H\033[2J");
                board.print(true);
            }
        }
    }

    private void autoAttackShip(Board board, DifficultyLevel level) {
        int x;
        int y;

        Point attack = null;

        if (!DifficultyLevel.EASY.equals(level)) {
            Optional<Ship> hitShip = board.getShips().stream()
                    .filter(Ship::isAlive)
                    .filter(ship -> ship.getPoints().stream().anyMatch(point -> !point.isAlive()))
                    .findFirst();

            if (hitShip.isPresent()) {
                List<Point> attackPoints = new ArrayList<>();
                List<Point> hitPoints = hitShip.get().getPoints().stream()
                        .filter(point -> !point.isAlive())
                        .collect(Collectors.toList());

                if (hitPoints.size() > 1 && hitPoints.get(0).getX() == hitPoints.get(hitPoints.size() - 1).getX()) {

                    if (
                            (hitPoints.get(hitPoints.size() - 1).getY() + 1 < board.getRow())
                                    && !board.getMisses().contains(new Point(hitPoints.get(hitPoints.size() - 1).getX(), hitPoints.get(hitPoints.size() - 1).getY() + 1))
                                    && !board.getHits().contains(new Point(hitPoints.get(hitPoints.size() - 1).getX(), hitPoints.get(hitPoints.size() - 1).getY() + 1))
                    ) {
                        attackPoints.add(new Point(hitPoints.get(hitPoints.size() - 1).getX(), hitPoints.get(hitPoints.size() - 1).getY() + 1));
                    }

                    if (
                            (hitPoints.get(0).getY() - 1 >= 0)
                                    && !board.getMisses().contains(new Point(hitPoints.get(0).getX(), hitPoints.get(0).getY() - 1))
                                    && !board.getHits().contains(new Point(hitPoints.get(0).getX(), hitPoints.get(0).getY() - 1))
                    ) {
                        attackPoints.add(new Point(hitPoints.get(0).getX(), hitPoints.get(0).getY() - 1));
                    }

                    attackPoints.removeAll(board.getHits());
                    attackPoints.removeAll(board.getMisses());

                    attack = attackPoints.get(random.nextInt(attackPoints.size()));

                } else if (hitPoints.size() > 1 && hitPoints.get(0).getY() == hitPoints.get(hitPoints.size() - 1).getY()) {

                    if (
                            (hitPoints.get(hitPoints.size() - 1).getX() + 1 < board.getColum())
                                    && !board.getMisses().contains(new Point(hitPoints.get(hitPoints.size() - 1).getX() + 1, hitPoints.get(hitPoints.size() - 1).getY()))
                                    && !board.getHits().contains(new Point(hitPoints.get(hitPoints.size() - 1).getX() + 1, hitPoints.get(hitPoints.size() - 1).getY()))
                    ) {
                        attackPoints.add(new Point(hitPoints.get(hitPoints.size() - 1).getX() + 1, hitPoints.get(hitPoints.size() - 1).getY()));

                    }

                    if (
                            (hitPoints.get(0).getX() - 1 >= 0)
                                    && !board.getMisses().contains(new Point(hitPoints.get(0).getX() - 1, hitPoints.get(0).getY()))
                                    && !board.getHits().contains(new Point(hitPoints.get(0).getX() - 1, hitPoints.get(0).getY()))
                    ) {
                        attackPoints.add(new Point(hitPoints.get(0).getX() - 1, hitPoints.get(0).getY()));
                    }

                    attackPoints.removeAll(board.getHits());
                    attackPoints.removeAll(board.getMisses());

                    attack = attackPoints.get(random.nextInt(attackPoints.size()));

                } else {
                    Point hitPoint = hitPoints.get(0);

                    if (hitPoint.getX() + 1 < board.getColum()) {
                        attackPoints.add(new Point(hitPoint.getX() + 1, hitPoint.getY()));
                    }

                    if (hitPoint.getX() - 1 >= 0) {
                        attackPoints.add(new Point(hitPoint.getX() - 1, hitPoint.getY()));
                    }

                    if (hitPoint.getY() + 1 < board.getRow()) {
                        attackPoints.add(new Point(hitPoint.getX(), hitPoint.getY() + 1));
                    }

                    if (hitPoint.getY() - 1 >= 0) {
                        attackPoints.add(new Point(hitPoint.getX(), hitPoint.getY() - 1));
                    }

                    attackPoints.removeAll(board.getHits());
                    attackPoints.removeAll(board.getMisses());

                    attack = attackPoints.get(random.nextInt(attackPoints.size()));
                }
            }


        }

        if (Objects.isNull(attack)) {
            x = random.nextInt(board.getColum());
            y = random.nextInt(board.getColum());
            attack = new Point(x, y);
        }

        while (board.getMisses().contains(attack) || board.getHits().contains(attack)) {
            x = random.nextInt(board.getColum());
            y = random.nextInt(board.getColum());
            attack = new Point(x, y);
        }

        Point finalAttack = attack;
        Optional<Point> partOfShip = board.getShips().stream()
                .flatMap(ship -> ship.getPoints().stream())
                .filter(point -> point.equals(finalAttack))
                .findFirst();

        System.out.printf("\nThe enemy attacked the coordinates (%s, %s)%n", attack.getX(), attack.getY());

        if (partOfShip.isPresent()) {
            partOfShip.get().setAlive(false);
            board.getHits().add(attack);
            System.out.println("\nEnemy hit :(");
        } else {
            board.getMisses().add(attack);
        }

        setDestroyed(board, attack, level);
    }

    private boolean attackShip(Board board, Point attack, DifficultyLevel level) {
        try {
            if (attack.getX() >= board.getColum() || attack.getX() < 0) {
                throw new OutOfBoardException(String.format("x: %s is out of columns", attack.getX()));
            } else if (attack.getY() >= board.getRow() || attack.getY() < 0) {
                throw new OutOfBoardException(String.format("y: %s is out of rows", attack.getY()));
            }

            Optional<Point> partOfShip = board.getShips().stream()
                    .flatMap(ship -> ship.getPoints().stream())
                    .filter(point -> point.equals(attack))
                    .findFirst();

            if (partOfShip.isPresent()) {
                if (partOfShip.get().isAlive()) {
                    partOfShip.get().setAlive(false);
                    System.out.println("\nYou hit! :)");
                } else {
                    throw new ReAttackPointException("The point was attacked earlier");
                }
            } else {
                if (!board.getMisses().contains(attack)) {
                    board.getMisses().add(attack);
                } else {
                    throw new ReAttackPointException("The point was attacked earlier");
                }
            }
        } catch (ReAttackPointException | OutOfBoardException e) {
            System.out.println(e.getMessage());
            return false;
        }
        setDestroyed(board, attack, level);

        return true;
    }

    private Point buildAttackPoint() {
        int x = ReaderUtil.readInteger("Enter coordinate X to attack: ");
        int y = ReaderUtil.readInteger("Enter coordinate Y to attack: ");

        return new Point(x, y);
    }

    private void setDestroyed(Board board, Point attack, DifficultyLevel level) {

        board.getShips().stream()
                .filter(ship -> ship.getPoints().contains(attack) && ship.getPoints().stream().noneMatch(Point::isAlive))
                .findFirst()
                .ifPresent(ship -> {
                    ship.setAlive(false);

                    List<Point> misses = new ArrayList<>();

                    if (DifficultyLevel.HARDCORE.equals(level)) {
                        if (
                                ship.getPoints().size() > 1 &&  ship.getPoints().get(0).getX() == ship.getPoints().get(ship.getPoints().size() - 1).getX()
                        ) {
                            if (ship.getPoints().get(0).getX() - 1 >= 0 && ship.getPoints().get(0).getY() - 1 >= 0) {
                                misses.add(new Point(ship.getPoints().get(0).getX() - 1,ship.getPoints().get(0).getY() - 1));
                            }
                            if (ship.getPoints().get(0).getX() + 1  < board.getColum() && ship.getPoints().get(0).getY() - 1 >= 0) {
                                misses.add(new Point(ship.getPoints().get(0).getX() + 1,ship.getPoints().get(0).getY() - 1));
                            }
                            if (ship.getPoints().get(ship.getPoints().size() - 1).getX() - 1 >= 0 && ship.getPoints().get(ship.getPoints().size() - 1).getY() + 1 < board.getRow()) {
                                misses.add(new Point(ship.getPoints().get(ship.getPoints().size() - 1).getX() - 1,ship.getPoints().get(ship.getPoints().size() - 1).getY() + 1));
                            }
                            if (ship.getPoints().get(ship.getPoints().size() - 1).getX() + 1 < board.getColum() && ship.getPoints().get(ship.getPoints().size() - 1).getY() + 1 < board.getRow()) {
                                misses.add(new Point(ship.getPoints().get(ship.getPoints().size() - 1).getX() + 1,ship.getPoints().get(ship.getPoints().size() - 1).getY() + 1));
                            }
                        } else if (
                                ship.getPoints().size() > 1 &&  ship.getPoints().get(0).getY() == ship.getPoints().get(ship.getPoints().size() - 1).getY()
                        ) {
                            if (ship.getPoints().get(0).getX() - 1 >= 0 && ship.getPoints().get(0).getY() - 1 >= 0) {
                                misses.add(new Point(ship.getPoints().get(0).getX() - 1, ship.getPoints().get(0).getY() - 1));
                            }
                            if (ship.getPoints().get(0).getX() - 1 >= 0 && ship.getPoints().get(0).getY() + 1 < board.getRow()) {
                                misses.add(new Point(ship.getPoints().get(0).getX() - 1, ship.getPoints().get(0).getY() + 1));
                            }
                            if (ship.getPoints().get(ship.getPoints().size() - 1).getX() + 1 < board.getColum() && ship.getPoints().get(ship.getPoints().size() - 1).getY() - 1 >= 0) {
                                misses.add(new Point(ship.getPoints().get(ship.getPoints().size() - 1).getX() + 1, ship.getPoints().get(ship.getPoints().size() - 1).getY() - 1));
                            }
                            if (ship.getPoints().get(ship.getPoints().size() - 1).getX() + 1 < board.getColum() && ship.getPoints().get(ship.getPoints().size() - 1).getY() + 1 < board.getRow()) {
                                misses.add(new Point(ship.getPoints().get(ship.getPoints().size() - 1).getX() + 1, ship.getPoints().get(ship.getPoints().size() - 1).getY() + 1));
                            }
                        } else {
                            if (ship.getPoints().get(0).getX() - 1 >= 0 && ship.getPoints().get(0).getY() - 1 >= 0) {
                                misses.add(new Point(ship.getPoints().get(0).getX() - 1, ship.getPoints().get(0).getY() - 1));
                            }
                            if (ship.getPoints().get(0).getX() - 1 >= 0 && ship.getPoints().get(0).getY() + 1 < board.getRow()) {
                                misses.add(new Point(ship.getPoints().get(0).getX() - 1, ship.getPoints().get(0).getY() + 1));
                            }
                            if (ship.getPoints().get(0).getX() + 1 < board.getColum() && ship.getPoints().get(0).getY() + 1 < board.getRow()) {
                                misses.add(new Point(ship.getPoints().get(0).getX() + 1, ship.getPoints().get(0).getY() + 1));
                            }
                            if (ship.getPoints().get(0).getX() + 1 < board.getColum() && ship.getPoints().get(0).getY() - 1 >= 0) {
                                misses.add(new Point(ship.getPoints().get(0).getX() + 1, ship.getPoints().get(0).getY() - 1));
                            }
                        }
                    }

                    ship.getPoints().forEach(point -> {
                        if ((point.getX() + 1) < board.getColum()) {
                            misses.add(new Point(point.getX() + 1, point.getY()));
                        }
                        if ((point.getX() - 1) >= 0 ) {
                            misses.add(new Point(point.getX() - 1, point.getY()));
                        }
                        if ((point.getY() + 1) < board.getRow()) {
                            misses.add(new Point(point.getX(), point.getY() + 1));
                        }
                        if ((point.getY() - 1) >= 0) {
                            misses.add(new Point(point.getX(), point.getY() - 1));
                        }
                    });

                    misses.removeAll(ship.getPoints());
                    board.getMisses().removeAll(misses);
                    board.getMisses().addAll(misses);

                    System.out.println("\n\tShip was destroyed!!!");
                });
    }

    private boolean isGameOver(Board board) {
        return board.getShips().stream().noneMatch(Ship::isAlive);
    }
}
