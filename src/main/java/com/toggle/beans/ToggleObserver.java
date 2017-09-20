package com.toggle.beans;

import java.util.Observable;
import java.util.Observer;

public class ToggleObserver implements Observer {

    private final String app;

    public String getToggleId() {
        return toggleId;
    }

    public String getToggleVersion() {
        return toggleVersion;
    }

    private final String toggleId;
    private final String toggleVersion;

    public String getApp() {
        return app;
    }

    public ToggleObserver(String app, String toggleId, String toggleVersion) {
        this.app = app;
        this.toggleId = toggleId;
        this.toggleVersion = toggleVersion;
    }

    @Override
    public void update(Observable o, Object arg) {

    }
}
