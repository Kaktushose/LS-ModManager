package de.github.kaktushose.lsmodmanager.util;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@JsonIgnoreProperties(value = {"mods", "id"})
public class Modpack {

    private final Logger logger;
    private int id;
    private File folder;
    private String name;
    private List<File> mods;

    public Modpack() {
        id = -1;
        folder = null;
        name = "N/A";
        mods = new ArrayList<>();
        logger = LoggerFactory.getLogger(Modpack.class);
    }

    public Modpack(Modpack modpack) {
        name = modpack.name;
        mods = new ArrayList<>(modpack.mods);
        folder = modpack.folder;
        id = modpack.id;
        logger = LoggerFactory.getLogger(Modpack.class);
    }

    public Modpack(String name, List<File> mods, String lsPath, int id) {
        this.name = name;
        this.mods = new ArrayList<>(mods);
        this.folder = new File(lsPath + "//lsmm-modpack-" + id);
        this.id = id;
        logger = LoggerFactory.getLogger(Modpack.class);
    }

    public void create() {
        folder.mkdir();
        mods.forEach(file -> {
                    try {
                        logger.debug("copying file: " + file);
                        Files.copy(Paths.get(file.getAbsolutePath()),
                                Paths.get(folder.getAbsolutePath() + "//" + file.getName()),
                                StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        logger.error("An error has occurred while copying the file" + file, e);
                    }
                }
        );
        logger.debug("Creating package-info.txt");
        File packageInfo = new File(folder.getAbsolutePath() + "//package-info.txt");
        try (final BufferedWriter bw = new BufferedWriter(new FileWriter(packageInfo))) {
            bw.write("Automatically generated folder by the LS-ModManager.\n" +
                    "Don't change, move or delete anything unless you really know what you're doing.\n" +
                    "Visit https://github.com/Kaktushose/LS-ModManager for details.\n" +
                    "id: " + id +
                    "\noriginal name: " + name +
                    "\ncreated at: " +
                    new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
        } catch (IOException e) {
            logger.error("An error has occurred while creating the package-info.txt", e);
        }
    }

    public void delete() {
        try {
            FileUtils.deleteDirectory(folder);
        } catch (IOException e) {
            logger.error(String.format("An error has occurred while deleting the modpack \"%s\" (id %d)", name, id), e);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<File> getMods() {
        return mods;
    }

    // default setter for jackson
    public void setMods(List<File> mods) {
        this.mods = mods;
    }

    /*
    Updates the mod list.
    Deletes all files that are in mods but not in newValue.
    Copies all files that are in newValue but not in mods.
     */
    public void updateMods(Collection<File> newValue) {
        logger.debug(String.format("Updating the modpack \"%s\" (id %d)...", name, id));
        List<File> toAdd = newValue.stream().filter(file -> !mods.contains(file)).collect(Collectors.toList());
        List<File> toRemove = mods.stream().filter(file -> !newValue.contains(file)).collect(Collectors.toList());
        toAdd.forEach(file -> {
                    try {
                        logger.debug("Adding file: " + file);
                        Files.copy(Paths.get(file.getAbsolutePath()),
                                Paths.get(folder.getAbsolutePath() + "//" + file.getName()),
                                StandardCopyOption.REPLACE_EXISTING);
                        mods.add(new File(folder.getAbsolutePath() + "//" + file.getName()));
                    } catch (IOException e) {
                        logger.error("An error has occurred while copying the file" + file, e);
                    }
                }
        );
        toRemove.forEach(file -> {
            try {
                logger.debug("Removing file: " + file);
                Files.delete(file.toPath());
                mods.remove(file);
            } catch (IOException e) {
                logger.error("An error has occurred while deleting the file" + file, e);
            }
        });
        logger.debug("Done! Modpack updated");
    }

    public File getFolder() {
        return folder;
    }

    public void setFolder(File folder) {
        this.folder = folder;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
