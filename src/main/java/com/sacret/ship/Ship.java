package com.sacret.ship;

import com.sacret.enumeration.Location;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
public class Ship {
    List<Point> points;
    @Setter
    boolean alive = true;

    private Ship() {}

    public static Ship buildShip(@NonNull Point head, @NonNull Location location, int size) {
        Ship ship = new Ship();
        ship.points = new ArrayList<>(size);

        if (Location.HORIZONTAL.equals(location)) {
            for (int x = head.getX(); x < head.getX() + size; x++) {
                ship.points.add(
                        new Point(x, head.getY()));
            }
        } else {
            for (int y = head.getY(); y < head.getY() + size; y++) {
                ship.points.add(new Point(head.getX(),y));
            }
        }

        return ship;
    }

    public static Ship buildShip(@NonNull Point head) {
        Ship ship = new Ship();
        ship.points = new ArrayList<>(1);
        ship.points.add(head);

        return ship;
    }
}
