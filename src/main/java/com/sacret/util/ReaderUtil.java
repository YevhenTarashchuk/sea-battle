package com.sacret.util;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@FieldDefaults(level = AccessLevel.PRIVATE)

public final class ReaderUtil {

    static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    private ReaderUtil() {}

    public static int readInteger(String out) {
        int i = 0;
        boolean read = false;
        while (!read) {
            try {
                System.out.print(out);
                i = Integer.parseInt(reader.readLine());
                read = true;
            } catch (IOException | NumberFormatException e) {
                System.out.println("Enter a numeric value!");
            }
        }
        return i;
    }

    public static void waiteEnter() {
        try {
            reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
