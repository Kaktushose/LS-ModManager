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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@JsonIgnoreProperties(value = {"mods", "id"})
public class Modpack {

    private int id;
    private File folder;
    private String name;
    private List<File> mods;

    public Modpack() {
        id = -1;
        folder = null;
        name = "N/A";
        mods = new ArrayList<>();
    }

    public Modpack(Modpack modpack) {
        name = modpack.name;
        mods = new ArrayList<>(modpack.mods);
        folder = modpack.folder;
        id = modpack.id;
    }

    public Modpack(String name, List<File> mods, String lsPath, int id) {
        this.name = name;
        this.mods = new ArrayList<>(mods);
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
                    "id: " + id +
                    "\noriginal name: " + name +
                    "\ncreated at: " +
                    new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void delete() {
        delete(folder);
    }

    private void delete(File dir) {
        for (File file : dir.listFiles()) {
            if (file.isDirectory())
                delete(file);
            file.delete();
        }
        dir.delete();
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

    /*
    Updates the mod list.
    Deletes all files that are in mods but not in newValue.
    Copies all files that are in newValue but not in mods.
     */
    public void updateMods(Collection<File> newValue) {
        newValue.forEach(System.out::println);
        System.out.println("---");
        List<File> toAdd = newValue.stream().filter(file -> !mods.contains(file)).collect(Collectors.toList());
        List<File> toRemove = mods.stream().filter(file -> !newValue.contains(file)).collect(Collectors.toList());
        toAdd.forEach(file -> {
                    try {
                        System.out.println("to add: " + file);
                        Files.copy(Paths.get(file.getAbsolutePath()),
                                Paths.get(folder.getAbsolutePath() + "//" + file.getName()),
                                StandardCopyOption.REPLACE_EXISTING);
                        mods.add(new File(folder.getAbsolutePath() + "//" + file.getName()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        );
        toRemove.forEach(file -> {
            System.out.println("to remove: " + file);
            try {
                Files.delete(file.toPath());
                mods.remove(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        System.out.println("---");
    }

    // default setter for jackson
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
