package com.github.kaktushose.lsmodmanager.services;

import com.github.kaktushose.lsmodmanager.services.model.Modpack;
import com.github.kaktushose.lsmodmanager.utils.Checks;
import com.github.kaktushose.lsmodmanager.utils.Constants;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ModpackService {

    private static final Logger log = LoggerFactory.getLogger(ModpackService.class);
    private final SettingsService settingsService;
    private final List<Modpack> modpacks;

    public ModpackService(SettingsService settingsService) {
        this.settingsService = settingsService;
        modpacks = settingsService.getModpacks();
    }

    public void indexModpacks() {
        modpacks.forEach(modpack -> {
            try {
                List<File> mods = Files.list(Path.of(modpack.getFolder())).map(Path::toFile).collect(Collectors.toList());
                modpack.setMods(mods);
            } catch (IOException e) {
                throw new RuntimeException(String.format("An error has occurred while indexing the modpack %s!", modpack.getName()), e);
            }
        });
    }

    public Modpack create(String name, List<File> mods) {
        Checks.notBlank(name, "name");
        log.debug("Creating new modpack...");

        int id = settingsService.getLastModpackId() + 1;
        Modpack modpack = new Modpack(id, createValidName(name));

        Path folder = Path.of(settingsService.getModpackPath() + Constants.MOD_FOLDER_PATH + id);
        File packageInfo = new File(folder + "//package-info.txt");

        try {
            Files.createDirectory(folder);
            log.debug("Created base folder.");

            FileWriter fileWriter = new FileWriter(packageInfo);
            fileWriter.write("Automatically generated folder by the LS-ModManager.\n" +
                    "Don't change, move or delete anything unless you really know what you're doing.\n" +
                    "Visit https://github.com/Kaktushose/LS-ModManager for details.\n" +
                    "id: " + id +
                    "\noriginal name: " + name +
                    "\ncreated at: " +
                    new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
            fileWriter.close();
            log.debug("Created package-info.");

            for (File file : mods) {
                log.debug("Copying file {}", file);
                Files.copy(Path.of(file.getAbsolutePath()), Path.of(String.format("%s\\%s", folder, file.getName())));
            }
            log.debug("Copied {} files", mods.size());

        } catch (IOException e) {
            throw new RuntimeException(String.format("An error has occurred creating the modpack %s!", name), e);
        }

        modpack.setFolder(folder.toString());
        modpack.setMods(mods);

        modpacks.add(modpack);

        settingsService.setLastModpackId(id);
        settingsService.setModpacks(modpacks);

        log.info("Created new {}", modpack);
        return modpack.copy();
    }

    public Modpack getById(int id) {
        return modpacks.stream().filter(modpack -> modpack.getId() == id).findFirst().map(Modpack::copy).orElse(null);
    }

    public Modpack getByName(String name) {
        return modpacks.stream().filter(modpack -> modpack.getName().equals(name)).findFirst().map(Modpack::copy).orElse(null);
    }

    public List<Modpack> getAll() {
        return Collections.unmodifiableList(modpacks);
    }

    public void updateModpack(int id, Modpack newValue) {
        Checks.notBlank(newValue.getName(), "name");
        Checks.notFile(newValue.getFolder(), "modpack path");

        Modpack modpack = getById(id);
        delete(id);
        modpack.setName(newValue.getName());
        modpack.setMods(newValue.getMods());
        modpack.setFolder(newValue.getFolder());
        modpacks.add(modpack);

        settingsService.setModpacks(modpacks);
    }

    public void delete(int id) {
        delete(getById(id));
    }

    public void delete(String name) {
        delete(getByName(name));
    }

    public void delete(Modpack modpack) {
        modpacks.removeIf(m -> m.getId() == modpack.getId());
        settingsService.setModpacks(modpacks);

        try {
            FileUtils.deleteDirectory(new File(modpack.getFolder()));
        } catch (IOException e) {
            throw new RuntimeException(String.format("Unable to delete the modpack %s!", modpack.getName()), e);
        }
        log.debug("Deleted modpack {}", modpack);
    }

    public boolean existsById(int id) {
        return modpacks.stream().anyMatch(modpack -> modpack.getId() == id);
    }

    public boolean existsByName(String name) {
        return modpacks.stream().anyMatch(modpack -> modpack.getName().equals(name));
    }

    public void load() {

    }

    public void unload() {

    }

    private String createValidName(String name) {
        if (name == null) {
            name = "null";
        }
        return makeUnique(name, 0);
    }

    // returns a unique name following the pattern "name (count)"
    private String makeUnique(String name, int count) {
        String newName = count > 0 ? String.format("%s (%d)", name, count) : name;
        if (existsByName(newName)) {
            newName = makeUnique(name, count + 1);
        }
        return newName;
    }
}
