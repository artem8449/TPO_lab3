package ru.ifmo.lab3;

import java.util.UUID;

public class RandomString {
    public static String get() {
        return UUID.randomUUID().toString()
                .replace("-", "").substring(0, 10);
    }
}
