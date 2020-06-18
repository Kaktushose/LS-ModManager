package de.github.kaktushose.lsmodmanager.core;

import de.github.kaktushose.lsmodmanager.json.index.IndexFile;
import de.github.kaktushose.lsmodmanager.json.index.ModpackIndex;
import de.github.kaktushose.lsmodmanager.util.Modpack;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class ModpackManager {

    private final IndexFile indexFile;
    private final ModpackIndex modpackIndex;
    private final App app;
    private final HashMap<String, Modpack> modpacks;
    private final Logger logger;

    public ModpackManager(App app) {
        this.app = app;
        modpacks = new HashMap<>();
        indexFile = new IndexFile();
        modpackIndex = indexFile.loadModpackIndex();
        logger = LoggerFactory.getLogger(ModpackManager.class);
    }

    public void createModpack(String name, List<File> mods) {
        logger.debug("Creating new modpack...");
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
        logger.info(String.format("Created modpack \"%s\" with id %d", name, id));
    }

    public void deleteModpack(Modpack modpack) {
        modpacks.remove(modpack.getName());
        modpackIndex.getModpacks().remove(modpack.getId());
        modpack.delete();
        indexFile.saveModpackIndex(modpackIndex);
        logger.info(String.format("Deleted modpack \"%s\" (id %d)", modpack.getName(), modpack.getId()));
    }

    public void setModpack(Modpack oldValue, Modpack newValue) {
        modpacks.remove(oldValue.getName());
        modpacks.put(newValue.getName(), newValue);
        modpackIndex.getModpacks().put(newValue.getId(), newValue);
        indexFile.saveModpackIndex(modpackIndex);
    }

    public Modpack getModpack(String name) {
        return modpacks.get(name);
    }

    public Modpack getModpackById(int id) {
        if (id < 0) return null;
        return modpacks.get(modpackIndex.getModpacks().get(id).getName());
    }

    public void unloadCurrentModpack() {
        int id = app.getLoadedModpackId();
        if (id < 0) return;
        unloadModpack(getModpackById(id));
    }

    public void unloadModpack(Modpack modpack) {
        app.setLoadedModpackId(-1);
        File source = new File(app.getLsPath() + "\\mods");
        try {
            FileUtils.moveDirectory(source, modpack.getFolder());
            logger.debug("Moved " + source + " to " + modpack.getFolder());
            source.mkdirs();
            logger.debug("Created mod folder");
        } catch (IOException e) {
            logger.error(String.format("An error has occurred while unloading the modpack \"%s\" (id %d)", modpack.getName(), modpack.getId()), e);
        }
        logger.info(String.format("Successfully unloaded the modpack \"%s\" (id %d)", modpack.getName(), modpack.getId()));
    }

    public void loadModpack(Modpack modpack) {
        app.setLoadedModpackId(modpack.getId());
        File target = new File(app.getLsPath() + "\\mods");
        try {
            Files.deleteIfExists(target.toPath());
            logger.debug("Deleted mod folder");
            FileUtils.moveDirectory(modpack.getFolder(), target);
            logger.debug("Moved " + modpack.getFolder() + " to " + target);
        } catch (IOException e) {
            logger.error(String.format("An error has occurred while loading the modpack \"%s\" (id %d)", modpack.getName(), modpack.getId()), e);
        }
        logger.info(String.format("Successfully loaded the modpack \"%s\" (id %d)", modpack.getName(), modpack.getId()));
    }

    public void indexModpacks() {
        logger.debug("Indexing modpacks...");
        modpackIndex.getModpacks().forEach((id, modpack) -> {
            logger.debug(String.format("Loading modpack \"%s\" (id %d)", modpack.getName(), id));
            modpack.setId(id);
            File[] mods;
            if (id == app.getLoadedModpackId()) {
                mods = new File(app.getLsPath() + "//mods").listFiles();
            } else {
                mods = modpack.getFolder().listFiles();
            }
            if (mods != null)
                modpack.setMods(Arrays.asList(mods));
            modpacks.put(modpack.getName(), modpack);
        });
        logger.info("Done! Modpacks indexed");
    }

    // returns a immutable map, thus changes to the modpacks can only be made via the corresponding methods in this class
    public Map<String, Modpack> getModpacks() {
        return Collections.unmodifiableMap(modpacks);
    }

    public boolean modpackExists(String name) {
        return modpacks.containsKey(name);
    }

}
