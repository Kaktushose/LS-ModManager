package de.github.kaktushose.lsmodmanager.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class SavegameParser {

    private Logger logger = LoggerFactory.getLogger(SavegameParser.class);

    public Savegame parse(File file) {
        Savegame savegame = new Savegame();
        Document document;
        try {
            document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
        } catch (IOException | ParserConfigurationException | SAXException e) {
            logger.error("Failed to parse savegame!", e);
            logger.warn("Using empty savegame to prevent errors");
            return savegame;
        }
        // add mods
        savegame.addMods(document.getElementsByTagName("mod"));

        // get name
        Element element = (Element) document.getElementsByTagName("settings").item(0);
        String name = element.getElementsByTagName("savegameName").item(0).getTextContent();
        savegame.setName(name);

        return savegame;
    }

}
