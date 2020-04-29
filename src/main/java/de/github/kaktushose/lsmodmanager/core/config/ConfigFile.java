package de.github.kaktushose.lsmodmanager.core.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ConfigFile {

    private File configFile;
    private Logger logger;

    public ConfigFile() {
        String workingDirectory = System.getenv("AppData") + "\\LS-ModManager";
        configFile = new File(workingDirectory + "\\config.json");
        logger = LoggerFactory.getLogger(ConfigFile.class);
        try {
            if (!configFile.exists()) {
                new File(workingDirectory).mkdir();
                configFile.createNewFile();
            }
        } catch (IOException e) {
            logger.error("There was an error creating the config file", e);
        }
    }

    public Config loadConfig() {
        Config config = new Config();
        ObjectMapper mapper = new ObjectMapper();
        try {
            config = mapper.readValue(new FileInputStream(configFile), Config.class);
        } catch (IOException e) {
            logger.error("There was an error loading the config", e);
        }
        return config;
    }

    public void saveConfig(Config config) {
        try {
            new ObjectMapper().writeValue(configFile, config);
        } catch (IOException e) {
            logger.error("There was an error saving the config", e);
        }
    }

}
