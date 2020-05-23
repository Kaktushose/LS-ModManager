package de.github.kaktushose.lsmodmanager.core;

import de.github.kaktushose.lsmodmanager.json.index.IndexFile;
import de.github.kaktushose.lsmodmanager.json.index.ModpackIndex;
import de.github.kaktushose.lsmodmanager.util.Modpack;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModpackManager {

    private final IndexFile indexFile;
    private final ModpackIndex modpackIndex;
    private final App app;
    private final Map<String, Modpack> modpacks;

    public ModpackManager(App app) {
        this.app = app;
        modpacks = new HashMap<>();
        indexFile = new IndexFile();
        modpackIndex = indexFile.loadModpackIndex();
    }

    public void createModpack(String name, List<File> mods) {
        if (modpacks.containsKey(name)) {
            throw new IllegalArgumentException("A modpack with the name `" + name + "` already exists!");
        }
        int id = modpackIndex.getLastId() + 1;
        Modpack modpack = new Modpack(name, mods, app.getModpacksPath(), id);
        modpack.create();
        modpacks.put(name, modpack);
        modpackIndex.getModpacks().put(id, modpack);
        modpackIndex.setLastId(id);
        indexFile.saveModpackIndex(modpackIndex);
    }


}
