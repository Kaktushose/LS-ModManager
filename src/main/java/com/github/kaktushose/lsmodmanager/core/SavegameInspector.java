package com.github.kaktushose.lsmodmanager.core;

import com.github.kaktushose.lsmodmanager.util.Savegame;
import com.github.kaktushose.lsmodmanager.util.SavegameParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SavegameInspector {

    private final SavegameParser parser;
    private final List<Savegame> savegames;
    private final App app;
    private final Logger logger;

    public SavegameInspector(App app) {
        parser = new SavegameParser();
        savegames = new ArrayList<>();
        this.app = app;
        logger = LoggerFactory.getLogger(SavegameInspector.class);
    }

    @SuppressWarnings("ConstantConditions")
    public void indexSavegames() {
        logger.debug("Indexing savegames...");
        if (app.getLsPath().isEmpty()) {
            logger.warn("Invalid LS path. Savegame indexing aborted!");
            return;
        }
        File lsFolder = new File(app.getLsPath());
        for (File file : lsFolder.listFiles(file -> file.getName().startsWith("savegame"))) {
            logger.debug("Found savegame: " + file);
            File savegameFile = new File(file + "\\careerSavegame.xml");
            if (!savegameFile.exists()) {
                logger.debug("Invalid. Skipping " + file);
                continue;
            }
            Savegame savegame = parser.parse(savegameFile);
            logger.debug(String.format("Savegame \"%s\" indexed. %d required mods found", savegame.getName(), savegame.getMods().size()));
            savegames.add(savegame);
        }
        logger.info("Done! Savegames indexed");
    }

    public List<Savegame> getSavegames() {
        return savegames;
    }

}
