package de.github.kaktushose.lsmodmanager.util;

import org.w3c.dom.NodeList;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Savegame {

    private final List<String> savegameMods;
    private String name;

    public Savegame() {
        name = "N/A";
        savegameMods = new ArrayList<>();
    }

    public List<String> getMissingModNames(Modpack modpack) {
        List<String> modList = modpack.getMods().stream().map(File::getName).collect(Collectors.toList());
        savegameMods.removeIf(modList::contains);
        return savegameMods;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getMods() {
        return savegameMods;
    }

    public void addMods(NodeList nodeList) {
        for (int i = 0; i < nodeList.getLength(); i++) {
            savegameMods.add(nodeList.item(i).getAttributes().getNamedItem("modName").getNodeValue());
        }
    }
}
