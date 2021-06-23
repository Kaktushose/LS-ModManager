package com.github.kaktushose.lsmodmanager.services;

import com.github.kaktushose.lsmodmanager.exceptions.FileOperationException;
import com.github.kaktushose.lsmodmanager.services.model.Modpack;
import com.github.kaktushose.lsmodmanager.utils.Checks;
import com.github.kaktushose.lsmodmanager.utils.Constants;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.file.PathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
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
                throw new FileOperationException(String.format("An error has occurred while indexing the modpack %s!", modpack.getName()), e);
            }
        });
    }

    public void create(String name, List<File> mods) {
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
                Files.copy(file.toPath(), Path.of(String.format("%s\\%s", folder, file.getName())));
            }
            log.debug("Copied {} files", mods.size());

        } catch (IOException e) {
            throw new FileOperationException(String.format("An error has occurred creating the modpack %s!", name), e);
        }

        modpack.setFolder(folder.toString());
        modpack.setMods(mods);

        modpacks.add(modpack);

        settingsService.setLastModpackId(id);
        settingsService.setModpacks(modpacks);

        log.info("Created new {}", modpack);
    }

    public Modpack getById(int id) {
        return modpacks.stream().filter(modpack -> modpack.getId() == id).findFirst().map(Modpack::copy).orElse(null);
    }

    public Modpack getByName(String name) {
        return modpacks.stream().filter(modpack -> modpack.getName().equals(name)).findFirst().map(Modpack::copy).orElse(null);
    }

    public Modpack getLoadedModpack() {
        return getById(settingsService.getLoadedModpackId());
    }

    public List<Modpack> getAll() {
        return Collections.unmodifiableList(modpacks);
    }

    public void moveModpackFolder(Path targetDirectory) {
        if (targetDirectory.toString().equals(settingsService.getModpackPath())) {
            return;
        }
        log.debug("Attempting to relocate modpack folder...");
        Path sourceDirectory = Path.of(settingsService.getModpackPath());
        Checks.emptyDirectory(targetDirectory.toString(), "modpacksPath");
        Checks.notSubDirectory(sourceDirectory.toString(), targetDirectory.toString(), "modpacksPath");

        try {
            FileUtils.copyDirectory(sourceDirectory.toFile(), targetDirectory.toFile());
            log.debug("Moved modpack folder");
            FileUtils.cleanDirectory(sourceDirectory.toFile());
        } catch (IOException e) {
            throw new FileOperationException("An error has occurred moving the modpack folder", e);
        }
        modpacks.forEach(modpack -> modpack.setFolder(targetDirectory + Constants.MOD_FOLDER_PATH + modpack.getId()));
        settingsService.setModpacks(modpacks);
        log.debug("Updated modpack folder paths");
        log.info("Successfully relocated the modpack folder");
    }

    public void updateModpack(int id, Modpack newValue) {
        log.debug("Updating modpack with id {}.", id);
        Checks.notBlank(newValue.getName(), "name");
        Checks.notFile(newValue.getFolder(), "modpack path");

        Modpack modpack = getById(id);
        modpacks.removeIf(m -> m.getId() == modpack.getId());
        modpack.setName(newValue.getName());
        modpack.setFolder(newValue.getFolder());

        log.debug("Updating files...");
        List<File> toAdd = newValue.getMods().stream().filter(file -> !modpack.getMods().contains(file)).collect(Collectors.toList());
        List<File> toRemove = modpack.getMods().stream().filter(file -> !newValue.getMods().contains(file)).collect(Collectors.toList());
        String folder = modpack.getFolder();
        try {
            for (File file : toAdd) {
                log.debug("Copying file {}", file);
                Files.copy(file.toPath(), Path.of(String.format("%s\\%s", folder, file.getName())));
                modpack.getMods().add(file);
            }

            for (File file : toRemove) {
                log.debug("Deleting file {}", file);
                Files.delete(file.toPath());
                modpack.getMods().remove(file);
            }

            modpack.setMods(newValue.getMods());
        } catch (IOException e) {
            throw new FileOperationException(String.format("An error has occurred updating the modpack %s!", modpack.getName()), e);
        }
        log.debug("All files updated.");

        modpacks.add(modpack);
        settingsService.setModpacks(modpacks);
        log.debug("Successfully updated {}", modpack);
    }

    public void delete(Modpack modpack) {
        modpacks.removeIf(m -> m.getId() == modpack.getId());
        settingsService.setModpacks(modpacks);
        try {
            PathUtils.deleteDirectory(Path.of(modpack.getFolder()));
        } catch (IOException e) {
            throw new FileOperationException(String.format("Unable to delete the modpack %s!", modpack.getName()), e);
        }
        log.debug("Deleted {}", modpack);
    }

    public boolean existsByName(String name) {
        return modpacks.stream().anyMatch(modpack -> modpack.getName().equals(name));
    }

    public boolean isLoadedModpack(int id) {
        return settingsService.getLoadedModpackId() == id;
    }

    public void load(int id) {
        log.debug("Attempting to load modpack with id {}...", id);

        int loadedId = settingsService.getLoadedModpackId();
        if (loadedId > 0) {
            log.debug("Unloading current modpack before proceeding...");
            unload(loadedId);
        }

        Modpack modpack = getById(id);
        Path sourceDirectory = Path.of(modpack.getFolder());
        Path targetDirectory = Path.of(settingsService.getFsPath() + "\\mods");
        Path backupDirectory = Path.of(
                String.format("%s\\backup-%s",
                        settingsService.getModpackPath(),
                        new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss").format(new Date())
                ));

        try {
            if (!Files.isDirectory(targetDirectory)) {
                Files.createDirectory(targetDirectory);
                log.warn("The mod folder doesn't exist! Created a new one!");
            }

            if (!PathUtils.isEmptyDirectory(targetDirectory)) {
                PathUtils.copyDirectory(targetDirectory, backupDirectory);
                log.warn("The mod folder wasn't empty! Created a backup at \"{}\"", backupDirectory);
            }
            PathUtils.deleteDirectory(targetDirectory);
            log.debug("Deleted mod folder");
            PathUtils.copyDirectory(sourceDirectory, targetDirectory);
            log.debug("Copied modpack files");
        } catch (IOException e) {
            throw new FileOperationException(String.format("Unable to load the modpack %s!", modpack.getName()), e);
        }

        settingsService.setLoadedModpackId(id);
        log.info("Successfully loaded {}", modpack);
    }

    public void unload(int id) {
        log.debug("Attempting to unload modpack with id {}...", id);
        Modpack modpack = getById(id);
        Path sourceDirectory = Path.of(settingsService.getFsPath() + "\\mods");
        Path targetDirectory = Path.of(modpack.getFolder());

        try {
            PathUtils.copyDirectory(sourceDirectory, targetDirectory, StandardCopyOption.REPLACE_EXISTING);
            log.debug("Copied modpack files");
            PathUtils.cleanDirectory(sourceDirectory);
            log.debug("Cleared mod folder");
        } catch (IOException e) {
            throw new FileOperationException(String.format("Unable to unload the modpack %s!", modpack.getName()), e);
        }

        settingsService.setLoadedModpackId(-1);
        log.info("Successfully unloaded {}", modpack);
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
