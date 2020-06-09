package de.github.kaktushose.lsmodmanager.util;

import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public class Savegame {

    private String name;
    private final List<String> mods;

    public Savegame() {
        mods = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getMods() {
        return mods;
    }

    public void addMods(NodeList nodeList) {
        for (int i = 0; i < nodeList.getLength(); i++) {
            mods.add(nodeList.item(i).getAttributes().getNamedItem("modName").getNodeValue());
        }
    }
}
