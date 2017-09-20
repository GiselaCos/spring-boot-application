package com.toggle.beans;

import com.toggle.builders.ToggleBeanBuilder;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ToggleBean {


    public static String APPS_DELIMITER = ",";

    /**
     * Toggle key
     */
    private String toggleKey;

    /**
     * Toggle id
     */
    private String toggleId;

    /**
     * Toggle status
     */
    private boolean toggleStatus;

    /**
     * Toggle version
     */
    private String toggleVersion;

    /**
     *  Apps that have status true for this toggle,
     *  if status is false, status has priority over this field
     */
    private List<String> appsAlwaysTrue;

    /**
     *  Apps that have status false for this toggle
     */
    private List<String> appsAlwaysFalse;

    /**
     * Apps that can use this toggle
     */
    private List<String> appsAuthorized;


    public ToggleBean(ToggleBeanBuilder builder) {
        this.toggleKey=builder.getToggleKey();
        this.toggleId=builder.getToggleId();
        this.toggleStatus=builder.isToggleStatus();
        this.toggleVersion=builder.getToggleVersion();
        this.appsAlwaysTrue=builder.getAppsAlwaysTrue();
        this.appsAlwaysFalse=builder.getAppsAlwaysFalse();
        this.appsAuthorized=builder.getAppsAuthorized();
    }


    public String getToggleKey() {
        return toggleKey;
    }

    public String getToggleId() {
        return toggleId;
    }

    public boolean isToggleStatus() {
        return toggleStatus;
    }

    public String getToggleVersion() {
        return toggleVersion;
    }

    public List<String> getAppsAlwaysFalse() {
        return appsAlwaysFalse;
    }

    public List<String> getAppsAlwaysTrue() {
        return appsAlwaysTrue;
    }

    public List<String> getAppsAuthorized() {
        return appsAuthorized;
    }

    public String getAppsAlwaysFalseStr() {
        return StringUtils.collectionToDelimitedString(appsAlwaysFalse, APPS_DELIMITER);
    }

    public String getAppsAlwaysTrueStr() {
        return StringUtils.collectionToDelimitedString(appsAlwaysTrue, APPS_DELIMITER);
    }

    public String getAppsAuthorizedStr() {
        return StringUtils.collectionToDelimitedString(appsAuthorized, APPS_DELIMITER);
    }
}
