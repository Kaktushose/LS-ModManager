package com.github.kaktushose.lsmodmanager.core;

import com.github.kaktushose.lsmodmanager.services.SettingsService;
import com.github.kaktushose.lsmodmanager.util.Savegame;
import com.github.kaktushose.lsmodmanager.util.SavegameParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SavegameInspector {

    private static final Logger log = LoggerFactory.getLogger(SavegameInspector.class);
    private final SavegameParser parser;
    private final List<Savegame> savegames;
    private final SettingsService settingsService;

    public SavegameInspector(SettingsService settingsService) {
        parser = new SavegameParser();
        savegames = new ArrayList<>();
        this.settingsService = settingsService;
    }

    @SuppressWarnings("ConstantConditions")
    public void indexSavegames() {
        log.debug("Indexing savegames...");

        if (settingsService.getFsPath().isEmpty()) {
            log.warn("Invalid Farming Simulator path. Savegame indexing aborted!");
            return;
        }

        File fsFolder = new File(settingsService.getFsPath());
        for (File file : fsFolder.listFiles(file -> file.getName().startsWith("savegame"))) {
            log.debug("Found savegame: \"{}\"", file);

            File savegameFile = new File(file + "\\careerSavegame.xml");
            if (!savegameFile.exists()) {
                log.debug("Invalid. Skipping \"{}\"!", file);
                continue;
            }

            Savegame savegame = parser.parse(savegameFile);
            log.debug("Savegame \"{}\" indexed. {} required mods found!", savegame.getName(), savegame.getMods().size());
            savegames.add(savegame);
        }

        log.info("Indexed a total of {} savegames!", savegames.size());
    }

    public List<Savegame> getSavegames() {
        return savegames;
    }

}
