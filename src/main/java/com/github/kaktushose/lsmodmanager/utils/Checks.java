package com.github.kaktushose.lsmodmanager.utils;

import com.github.kaktushose.lsmodmanager.exceptions.FileOperationException;
import org.apache.commons.io.file.PathUtils;

import java.io.File;
import java.io.IOException;
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

    public static void emptyDirectory(String path, String name) {
        notNull(path, name);
        notFile(path, name);
        if (!isEmptyDirectory(path)) {
            throw new IllegalArgumentException(name + " must be an empty directory");
        }
    }

    public static void notSubDirectory(String parent, String child, String name) {
        notNull(child, "parentDirectory");
        notNull(child, "childDirectory");
        if (isSubDirectory(parent, child)) {
            throw new IllegalArgumentException(name + " may not be a sub directory of " + parent);
        }
    }

    public static void notModsFolder(String path, String name) {
        notNull(path, name);
        if (isModsFolder(path)) {
            throw new IllegalArgumentException(name + " may not be the mods folder");
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

    public static boolean isModsFolder(String path) {
        return path.matches("[A-Z]:\\\\.+?(?<=\\\\FarmingSimulator[0-9]{4})\\\\mods");
    }

    public static boolean isEmptyDirectory(String path) {
        try {
            return PathUtils.isEmptyDirectory(Path.of(path));
        } catch (IOException e) {
            throw new FileOperationException("Unable to check directory", e);
        }
    }

    public static boolean isSubDirectory(String parent, String child) {
        try {
            return new File(child).getCanonicalPath().contains(new File(parent).getCanonicalPath() + File.separator);
        } catch (IOException e) {
            throw new FileOperationException("Unable to check directory", e);
        }
    }
}
