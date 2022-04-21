package com.sacret.ship;

import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

@ToString
@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(exclude = "alive")
@Accessors(chain = true)
public class Point {
    final int x;
    final int y;
    @Setter
    boolean alive = true;
}
