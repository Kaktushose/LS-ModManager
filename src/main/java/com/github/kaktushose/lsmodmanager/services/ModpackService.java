package com.github.kaktushose.lsmodmanager.services;

import com.github.kaktushose.lsmodmanager.services.model.Modpack;
import com.github.kaktushose.lsmodmanager.util.Checks;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ModpackService {

    private final SettingsService settingsService;
    private final List<Modpack> modpacks;

    public ModpackService(SettingsService settingsService) {
        this.settingsService = settingsService;
        modpacks = settingsService.getModpacks();
    }

    public Modpack create(String name) {
        Checks.notBlank(name, "name");

        int id = settingsService.getLastModpackId() + 1;
        Modpack modpack = new Modpack(id, createValidName(name));

        modpacks.add(modpack);

        settingsService.setLastModpackId(id);
        settingsService.setModpacks(modpacks);
        return modpack.copy();
    }

    public Optional<Modpack> getById(int id) {
        return modpacks.stream().filter(modpack -> modpack.getId() == id).findFirst().map(Modpack::copy);
    }

    public Optional<Modpack> getByName(String name) {
        return modpacks.stream().filter(modpack -> modpack.getName().equals(name)).findFirst().map(Modpack::copy);
    }

    public List<Modpack> getAll() {
        return Collections.unmodifiableList(modpacks);
    }

    public void updateName(int id, String newName) {
        Checks.notBlank(newName, "name");

        Modpack modpack = getById(id).orElseThrow();
        delete(id);
        modpack.setName(newName);
        modpacks.add(modpack);

        settingsService.setModpacks(modpacks);
    }

    public void updateName(String oldName, String newName) {
        Checks.notBlank(newName, "newName");

        Modpack modpack = getByName(oldName).orElseThrow();
        delete(modpack.getId());
        modpack.setName(newName);
        modpacks.add(modpack);

        settingsService.setModpacks(modpacks);
    }

    public void delete(int id) {
        modpacks.removeIf(modpack -> modpack.getId() == id);
        settingsService.setModpacks(modpacks);
    }

    public void delete(String name) {
        modpacks.removeIf(modpack -> modpack.getName().equals(name));
        settingsService.setModpacks(modpacks);
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
