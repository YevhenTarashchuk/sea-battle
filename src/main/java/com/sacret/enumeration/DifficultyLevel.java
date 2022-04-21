package com.sacret.enumeration;

import java.util.Arrays;

public enum DifficultyLevel {
    CHEATER(0), EASY(1), NORMAL(2), HARDCORE(3);

    private final int value;

    DifficultyLevel(int value) {
        this.value = value;
    }

    public static DifficultyLevel valueOf(int value) {
        return Arrays.stream(values())
                .filter(difficultyLevel -> difficultyLevel.value == value)
                .findFirst()
                .orElse(NORMAL);
    }
}
