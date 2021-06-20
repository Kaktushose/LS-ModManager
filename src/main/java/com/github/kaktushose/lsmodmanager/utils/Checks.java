package com.github.kaktushose.lsmodmanager.utils;

import java.nio.file.Files;
import java.nio.file.Path;

public class Checks {

    public static void notNull(Object argument, String name) {
        if (argument == null) {
            throw new IllegalArgumentException(name + " may not be null");
        }
    }

    public static void notBlank(CharSequence argument, String name) {
        notNull(argument, name);
        if (isBlank(argument)) {
            throw new IllegalArgumentException(name + " may not be blank");
        }
    }

    public static void notFile(String path, String name) {
        notNull(path, name);
        if (isFile(path)) {
            throw new IllegalArgumentException(name + " may not be a file");
        }
    }

    public static boolean isEmpty(CharSequence seq) {
        return seq == null || seq.length() == 0;
    }

    public static boolean isBlank(CharSequence seq) {
        if (isEmpty(seq))
            return true;
        for (int i = 0; i < seq.length(); i++) {
            if (!Character.isWhitespace(seq.charAt(i)))
                return false;
        }
        return true;
    }

    public static boolean isFile(String path) {
        return Files.isRegularFile(Path.of(path));
    }
}
