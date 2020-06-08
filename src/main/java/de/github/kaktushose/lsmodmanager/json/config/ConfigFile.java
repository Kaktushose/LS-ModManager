package de.github.kaktushose.lsmodmanager.json.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.github.kaktushose.lsmodmanager.util.CloseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ConfigFile {

    private final File configFile;
    private final Logger logger;

    public ConfigFile() {
        String workingDirectory = System.getenv("AppData") + "\\LS-ModManager";
        configFile = new File(workingDirectory + "\\config.json");
        logger = LoggerFactory.getLogger(ConfigFile.class);
        try {
            if (!configFile.exists()) {
                logger.debug("No config file found. Creating new one");
                new File(workingDirectory).mkdir(); // if the config file doesn't exist, the folder probably won't exist neither
                configFile.createNewFile();
                saveConfig(new Config());
            }
        } catch (IOException e) {
            logger.error("There was an error creating the config file!");
            new CloseEvent(e, 1).perform();
        }
    }

    // returns default config, if loading fails, in order to prevent app crash
    public Config loadConfig() {
        logger.debug("Loading config...");
        Config config = new Config();
        ObjectMapper mapper = new ObjectMapper();
        try {
            config = mapper.readValue(new FileInputStream(configFile), Config.class);
        } catch (IOException e) {
            logger.error("There was an error loading the config!", e);
            logger.warn("Starting with default config!");
        }
        logger.debug("Done! Config loaded");
        return config;
    }

    public void saveConfig(Config config) {
        try {
            new ObjectMapper().writeValue(configFile, config);
        } catch (IOException e) {
            logger.error("There was an error saving the config!", e);
        }
    }

}
