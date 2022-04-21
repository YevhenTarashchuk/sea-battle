package com.sacret.enumeration;


import java.util.Arrays;

public enum Location {
    HORIZONTAL(0), VERTICAL(1);

    private final int value;

    Location(int value) {
        this.value = value;
    }

    public static Location valueOf(int value) {
        return Arrays.stream(values())
                .filter(location -> location.value == value)
                .findFirst()
                .orElse(VERTICAL);
    }
}
