package com.github.kaktushose.lsmodmanager.utils;

import java.util.ArrayList;
import java.util.Locale;

public class Constants {

    public static final String LOCAL_APPDATA = System.getenv("LocalAppData");
    public static final String APP_PATH = LOCAL_APPDATA + "\\LS-ModManager";
    public static final String SETTINGS_PATH = APP_PATH + "\\settings.json";
    public static final String LOGGING_PATH = APP_PATH + "\\logs";
    public static final String MY_GAMES = System.getProperty("user.home") + "\\Documents\\My Games";
    public static final String MOD_FOLDER_PATH = "\\lsmm-modpack-";
    public static final String VERSION = "1.1.0";
    public static final ArrayList<Locale> AVAILABLE_LANGUAGES = new ArrayList<>() {{
        add(Locale.ENGLISH);
        add(Locale.GERMAN);
    }};

}
