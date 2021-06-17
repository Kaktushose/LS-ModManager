package com.github.kaktushose.lsmodmanager.services.model;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public class Modpack implements Comparable<Modpack> {

    private int id;
    private String name;
    private transient Path folder;
    private transient List<String> mods;

    public Modpack(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Path getFolder() {
        return folder;
    }

    public void setFolder(Path folder) {
        this.folder = folder;
    }

    public List<String> getMods() {
        return mods;
    }

    public void setMods(List<String> mods) {
        this.mods = mods;
    }

    public Modpack copy() {
        return new Modpack(id, name);
    }

    @Override
    public int compareTo(Modpack modpack) {
        int otherId = modpack.getId();
        if (id < otherId) {
            return -1;
        } else if (id == otherId) {
            return 0;
        }
        return 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Modpack modpack = (Modpack) o;
        return id == modpack.id && Objects.equals(name, modpack.name);
    }

    @Override
    public String toString() {
        return "Modpack{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
