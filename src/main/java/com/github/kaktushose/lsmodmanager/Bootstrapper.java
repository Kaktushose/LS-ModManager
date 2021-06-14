package com.github.kaktushose.lsmodmanager;

import com.github.kaktushose.lsmodmanager.core.App;
import com.github.kaktushose.lsmodmanager.util.Constants;
import javafx.application.Application;

public class Bootstrapper {

    public static void main(String[] args) {
        System.setProperty("lsmm.log", Constants.LOGGING_PATH);
        Application.launch(App.class);
    }

}
