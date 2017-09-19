package com.toggle.builders;

import com.toggle.beans.ToggleBean;
import org.springframework.util.StringUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.toggle.beans.ToggleBean.APPS_DELIMITER;
import static com.toggle.utils.ToggleConstants.FALSE;
import static com.toggle.utils.ToggleConstants.TRUE;

public class ToggleBeanBuilder {

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
    private ArrayList<String> appsAlwaysTrue;

    /**
     *  Apps that have status false for this toggle
     */
    private ArrayList<String> appsAlwaysFalse;

    /**
     * Apps that can use this toggle
     */
    private ArrayList<String> appsAuthorized;


    public ToggleBeanBuilder setKey(String toggleId, String toggleVersion) {
        this.toggleId = toggleId;
        this.toggleVersion = toggleVersion;
        this.toggleKey = toggleId+"_"+toggleVersion;
        return this;
    }

    public ToggleBeanBuilder setToggleStatus(boolean status) {
        this.toggleStatus = status;
        return this;
    }

    public ToggleBeanBuilder setAppsAlwaysTrue(ArrayList appsAlwaysTrue) {
        this.appsAlwaysTrue = appsAlwaysTrue;
        return this;
    }

    public ToggleBeanBuilder setAppsAlwaysFalse(ArrayList appsAlwaysFalse) {
        this.appsAlwaysFalse = appsAlwaysFalse;
        return this;
    }

    public ToggleBeanBuilder setAppsAuthorized(ArrayList appsAuthorized) {
        this.appsAuthorized = appsAuthorized;
        return this;
    }

    public ToggleBeanBuilder updateAppStatus(String app, String status) {
        if(TRUE.equals(status)){
            //if in the list of apps always false removes it
            if(getAppsAlwaysFalse().contains(app)){
                getAppsAlwaysFalse().remove(app);
            }
            //if not in the list of apps always true add it
            if(!getAppsAlwaysTrue().contains(app)) {
                getAppsAlwaysTrue().add(app);
            }
        }else if(FALSE.equals(status)){
            //if in the list of apps always true removes it
            if(getAppsAlwaysTrue().contains(app)){
                getAppsAlwaysTrue().remove(app);
            }
            //if not in the list of apps always false add it
            if(!getAppsAlwaysFalse().contains(app)) {
                getAppsAlwaysFalse().add(app);
            }
        }
        return this;
    }

    public ToggleBean build(){
        return new ToggleBean(this);
    }

    /*****************************************************************************
     *
     * GETTERS AND SETTERS
     *
     *****************************************************************************/



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

    public List<String> getAppsAlwaysTrue() {
        return appsAlwaysTrue;
    }

    public List<String> getAppsAlwaysFalse() {
        return appsAlwaysFalse;
    }

    public List<String> getAppsAuthorized() {
        return appsAuthorized;
    }

}
