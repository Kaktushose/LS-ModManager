package com.github.kaktushose.lsmodmanager.util;

public class Checks {

    public static void notNull(Object argument, String name) {
        if (argument == null) {
            throw new IllegalArgumentException(name + " may not be null");
        }
    }

    public static void notEmpty(CharSequence argument, String name) {
        notNull(argument, name);
        if (isEmpty(argument)) {
            throw new IllegalArgumentException(name + " may not be empty");
        }
    }

    public static void notBlank(CharSequence argument, String name) {
        notNull(argument, name);
        if (isBlank(argument)) {
            throw new IllegalArgumentException(name + " may not be blank");
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
}
