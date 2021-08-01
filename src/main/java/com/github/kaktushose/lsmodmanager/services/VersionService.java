package com.github.kaktushose.lsmodmanager.services;

import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class VersionService {

    private static final Logger log = LoggerFactory.getLogger(VersionService.class);
    private final SettingsService settingsService;
    private String latestVersion;

    public VersionService(SettingsService settingsService) {
        this.settingsService = settingsService;
        latestVersion = "N/A";
    }

    public boolean isNewVersionAvailable() {
        return !latestVersion.equals(settingsService.getVersion());
    }

    public void retrieveLatestVersion() {
        try {
            URL url = new URL("https://api.github.com/repos/Kaktushose/LS-ModManager/releases/latest");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = reader.readLine()) != null) {
                response.append(responseLine.trim());
            }
            connection.disconnect();
            reader.close();
            latestVersion = JsonParser.parseString(response.toString()).getAsJsonObject().get("tag_name").getAsString();
        } catch (IOException e) {
            log.error("Unable to query latest release version!", e);
        }
    }
}
