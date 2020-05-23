package de.github.kaktushose.lsmodmanager.util;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@JsonIgnoreProperties(value = {"mods", "id"})
public class Modpack {

    private int id;
    private File folder;
    private String name;
    private List<File> mods;

    public Modpack() {
        this.id = -1;
    }

    public Modpack(String name, List<File> mods, String lsPath, int id) {
        this.name = name;
        this.mods = mods;
        this.folder = new File(lsPath + "//lsmm-modpack-" + id);
        this.id = id;
    }

    public void create() {
        folder.mkdir();
        mods.forEach(file -> {
                    try {
                        Files.copy(Paths.get(file.getAbsolutePath()),
                                Paths.get(folder.getAbsolutePath() + "//" + file.getName()),
                                StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        );
        File packageInfo = new File(folder.getAbsolutePath() + "//package-info.txt");
        try (final BufferedWriter bw = new BufferedWriter(new FileWriter(packageInfo))) {
            bw.write("Automatically generated folder by the LS-ModManager.\n" +
                    "Don't change, move or delete anything unless you really know what you're doing.\n" +
                    "Visit https://github.com/Kaktushose/LS-ModManager for details.\n" +
                    "modpack name: " + name + "\ncreated at: " +
                    new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void delete() {

    }

    public void move() {

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

    public void setMods(List<File> mods) {
        this.mods = mods;
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
