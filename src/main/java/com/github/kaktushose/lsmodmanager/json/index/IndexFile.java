package com.github.kaktushose.lsmodmanager.json.index;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kaktushose.lsmodmanager.util.CloseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class IndexFile {

    private final File indexFile;
    private final Logger logger;

    public IndexFile() {
        String workingDirectory = System.getenv("AppData") + "\\LS-ModManager";
        indexFile = new File(workingDirectory + "\\index.json");
        logger = LoggerFactory.getLogger(IndexFile.class);
        try {
            if (!indexFile.exists()) {
                new File(workingDirectory).mkdir(); // if the index file doesn't exist, the folder probably won't exist neither
                indexFile.createNewFile();
                saveModpackIndex(new ModpackIndex());
            }
        } catch (IOException e) {
            logger.error("There was an error creating the modpack index file!");
            new CloseEvent(e, 2).perform();
        }
    }

    // returns empty index, if loading fails, in order to prevent app crash
    public ModpackIndex loadModpackIndex() {
        ModpackIndex modpackIndex = new ModpackIndex();
        ObjectMapper mapper = new ObjectMapper();
        try {
            modpackIndex = mapper.readValue(new FileInputStream(indexFile), ModpackIndex.class);
        } catch (IOException e) {
            logger.error("There was an error loading the modpack index!", e);
            logger.warn("Starting with empty index!");
        }
        return modpackIndex;
    }

    public void saveModpackIndex(ModpackIndex modpackIndex) {
        try {
            new ObjectMapper().writeValue(indexFile, modpackIndex);
        } catch (IOException e) {
            logger.error("There was an error saving the modpack index!", e);
        }
    }

}
