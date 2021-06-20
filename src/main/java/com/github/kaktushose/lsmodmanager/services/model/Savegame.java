package com.github.kaktushose.lsmodmanager.services.model;

import java.util.ArrayList;
import java.util.List;

public class Savegame {

    private String name;
    private List<String> modNames;

    public Savegame() {
        this("N/A", new ArrayList<>());
    }

    public Savegame(String name, List<String> modNames) {
        this.modNames = modNames;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getModNames() {
        return modNames;
    }

    public void setModNames(List<String> modNames) {
        this.modNames = modNames;
    }
}
