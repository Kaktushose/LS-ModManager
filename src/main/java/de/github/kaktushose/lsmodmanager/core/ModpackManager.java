package de.github.kaktushose.lsmodmanager.core;

import de.github.kaktushose.lsmodmanager.json.index.IndexFile;
import de.github.kaktushose.lsmodmanager.json.index.ModpackIndex;
import de.github.kaktushose.lsmodmanager.util.Modpack;

import java.io.File;
import java.util.*;

public class ModpackManager {

    private final IndexFile indexFile;
    private final ModpackIndex modpackIndex;
    private final App app;
    private final HashMap<String, Modpack> modpacks;

    public ModpackManager(App app) {
        this.app = app;
        modpacks = new HashMap<>();
        indexFile = new IndexFile();
        modpackIndex = indexFile.loadModpackIndex();
        indexModpacks();
    }

    public void createModpack(String name, List<File> mods) {
        if (modpacks.containsKey(name)) {
            throw new IllegalArgumentException("A modpack with the name `" + name + "` already exists!");
        }
        int id = modpackIndex.getLastId() + 1;
        Modpack modpack = new Modpack(name, mods, app.getModpackPath(), id);
        modpack.create();
        modpacks.put(name, modpack);
        modpackIndex.getModpacks().put(id, modpack);
        modpackIndex.setLastId(id);
        indexFile.saveModpackIndex(modpackIndex);
    }

    public void deleteModpack(Modpack modpack) {
        modpacks.remove(modpack.getName());
        modpackIndex.getModpacks().remove(modpack.getId());
        modpack.delete();
        modpackIndex.getModpacks().keySet().forEach(System.out::println);
        indexFile.saveModpackIndex(modpackIndex);
    }

    public void setModpack(Modpack oldValue, Modpack newValue) {
        modpacks.remove(oldValue.getName());
        modpacks.put(newValue.getName(), newValue);
        modpackIndex.getModpacks().put(newValue.getId(), newValue);
        indexFile.saveModpackIndex(modpackIndex);
    }

    private void indexModpacks() {
        modpackIndex.getModpacks().forEach((id, modpack) -> {
            modpack.setId(id);
            File[] mods = modpack.getFolder().listFiles();
            if (mods != null)
                modpack.setMods(Arrays.asList(mods));
            modpacks.put(modpack.getName(), modpack);
        });
    }

    // returns a immutable map, thus changes to the modpacks can only be made via the corresponding methods in this class
    public Map<String, Modpack> getModpacks() {
        return Collections.unmodifiableMap(modpacks);
    }

    public boolean modpackExists(String name) {
        return modpacks.containsKey(name);
    }

}
