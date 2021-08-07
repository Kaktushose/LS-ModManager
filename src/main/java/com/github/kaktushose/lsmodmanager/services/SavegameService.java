package com.github.kaktushose.lsmodmanager.services;

import com.github.kaktushose.lsmodmanager.services.model.Modpack;
import com.github.kaktushose.lsmodmanager.services.model.Savegame;
import com.github.kaktushose.lsmodmanager.utils.Checks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class SavegameService {

    private static final Logger log = LoggerFactory.getLogger(SettingsService.class);
    private final SettingsService settingsService;
    private final List<Savegame> savegames;

    public SavegameService(SettingsService settingsService) {
        this.settingsService = settingsService;
        savegames = new ArrayList<>();
    }

    public void indexSavegames() {
        log.debug("Indexing savegames...");
        savegames.clear();

        if (Checks.isBlank(settingsService.getFsPath())) {
            log.warn("Invalid Farming Simulator folder. Savegame indexing aborted!");
            return;
        }

        try {
            Files.list(Path.of(settingsService.getFsPath())).forEach(file -> {
                log.debug("Found savegame: \"{}\".", file);

                File savegameFile = new File(file + "\\careerSavegame.xml");
                if (!savegameFile.exists()) {
                    log.debug("No \"careerSavegame.xml\" found. Skipping \"{}\"", file);
                    return;
                }

                Savegame savegame = parse(savegameFile, file.getFileName().toString());
                log.debug("Savegame \"{}\" indexed. {} required mods found!", savegame.getName(), savegame.getModNames().size());
                savegames.add(savegame);
            });
        } catch (IOException e) {
            log.error("Unable to parse savegames!", e);
        }

        log.debug("Indexed a total of {} savegames!", savegames.size());
    }

    public long getMissingModsCount(Savegame savegame, Modpack modpack) {
        if (modpack == null) {
            return 0;
        }
        List<String> modNames = savegame.getModNames();
        return modpack.getMods().stream().filter(mod -> modNames.contains(mod.getName())).count();
    }

    public Optional<Savegame> getByName(String name) {
        return savegames.stream().filter(savegame -> savegame.getName().equals(name)).findFirst();
    }

    public List<Savegame> getAll() {
        return Collections.unmodifiableList(savegames);
    }

    private Savegame parse(File file, String savegameName) {
        Savegame savegame = new Savegame();
        Document document;
        try {
            document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
        } catch (IOException | ParserConfigurationException | SAXException e) {
            log.error("Failed to parse savegame!", e);
            log.warn("Using empty savegame to prevent errors!");
            return savegame;
        }

        // add mods
        NodeList nodeList = document.getElementsByTagName("mod");
        List<String> modNames = new ArrayList<>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            String name = nodeList.item(i).getAttributes().getNamedItem("modName").getNodeValue();
            // skip dlcs
            if (name.contains("pdlc")) {
                continue;
            }
            modNames.add(String.format("%s.zip", name));
        }
        Collections.sort(modNames);
        savegame.setModNames(modNames);

        // get map name
        Element element = (Element) document.getElementsByTagName("settings").item(0);
        String map = element.getElementsByTagName("mapTitle").item(0).getTextContent();
        savegame.setName(String.format("%s (%s)", savegameName, map));

        return savegame;
    }
}
