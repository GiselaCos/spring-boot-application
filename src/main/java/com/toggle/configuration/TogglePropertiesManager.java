package com.toggle.configuration;

import com.toggle.beans.ToggleBean;
import com.toggle.beans.ToggleObserver;
import com.toggle.builders.ToggleBeanBuilder;
import com.toggle.enums.ToggleAction;
import com.toggle.exceptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TogglePropertiesManager {

    private static final Logger logger = LoggerFactory.getLogger(TogglePropertiesManager.class);

    @Autowired
    ToggleProperties toggleProperties;


    /**
     * Method responsible to add observers
     *
     * @param app
     * @param toggleId
     * @param toggleVersion
     * @return
     * @throws ToggleNotFoundException
     * @throws ToggleStatusNotValidException
     */
    public Boolean addToggleStatusObservers(String app, String toggleId, String toggleVersion) throws ToggleNotFoundException, ToggleStatusNotValidException, ToggleForbiddenException {

        ToggleObserver toggleObs = new ToggleObserver(app, toggleId, toggleVersion);
        toggleProperties.addObserver(toggleObs);

        return true;
    }


    /**
     * Method responsible to return toggle status for a specific app
     *
     * @param app
     * @param toggleId
     * @param toggleVersion
     * @return
     * @throws ToggleNotFoundException
     * @throws ToggleStatusNotValidException
     */
    public Boolean readToggleAppStatus(String app, String toggleId, String toggleVersion) throws ToggleNotFoundException, ToggleStatusNotValidException, ToggleForbiddenException {

        ToggleBean toggleBean = toggleProperties.getToggleBeanFromToggleId(toggleId, toggleVersion);

        boolean status = checkToggleAppStatus(app, toggleBean);

        return status;
    }

    /**
     * Method responsible to return toggle status
     *
     * @param toggleId
     * @param toggleVersion
     * @return
     * @throws ToggleNotFoundException
     */
    public Boolean readToggleStatus(String toggleId, String toggleVersion) throws ToggleNotFoundException {

        boolean status = false;
        try {
            status = toggleProperties.getToggleStatus(toggleId, toggleVersion);
        } catch (ToggleStatusNotValidException e) {
            logger.error("Status invalid was saved in properties file, this should not happen we return false!");
            status = false;
        }

        return status;
    }


    /**
     * Method responsible to add toggle configuration
     *
     * @param toggleId
     * @param toggleVersion
     * @param toggleStatus
     * @param appsAlwaysTrue
     * @param appsAlwaysFalse
     * @param appsAccess
     *
     * @return
     */
    public Boolean addToggleStatus(String toggleId, String toggleVersion, String toggleStatus, String appsAlwaysTrue, String appsAlwaysFalse, String appsAccess) throws ToggleStatusNotValidException, ToggleAlreadyExistsException {

        ToggleBean toggleBean = toggleProperties.createToggleBean(toggleId, toggleVersion, toggleStatus, appsAlwaysTrue, appsAlwaysFalse, appsAccess);

        boolean result = toggleProperties.updateToggleConfiguration(toggleBean, ToggleAction.CREATE_TOGGLE_CONFIG);

        return result;
    }

    /**
     * Method responsible to update toggle application status
     *
     * @param app
     * @param toggleId
     * @param toggleVersion
     * @param toggleStatus
     */
    public Boolean updateToggleAppStatus(String app, String toggleId, String toggleVersion, String toggleStatus) throws ToggleNotFoundException, ToggleStatusNotValidException {

        ToggleBeanBuilder toggleBuilder = toggleProperties.getToggleBuilderFromToggleId(toggleId, toggleVersion);
        boolean result = false;

        if(toggleBuilder != null){
            toggleBuilder.updateAppStatus(app, toggleStatus);
            ToggleBean bean = toggleBuilder.build();

            result = toggleProperties.updateToggleConfiguration(bean, ToggleAction.UPDATE_APP_STATUS);
        }

        return result;

    }


    /**
     * Method responsible to update toggle status
     *
     * @param toggleId
     * @param toggleVersion
     * @param toggleStatus
     */
    public Boolean updateToggleStatus(String toggleId, String toggleVersion, String toggleStatus) throws ToggleNotFoundException, ToggleStatusNotValidException, ToggleStatusAlreadySettedException {

        ToggleBeanBuilder toggleBuilder = toggleProperties.getToggleBuilderFromToggleId(toggleId, toggleVersion);
        boolean result = false;

        if(toggleBuilder != null){
            boolean isStatusValid = toggleProperties.isToggleStatusValid(toggleStatus);
            if(isStatusValid) {
                toggleBuilder.setToggleStatus(Boolean.parseBoolean(toggleStatus));
                ToggleBean bean = toggleBuilder.build();

                result = toggleProperties.updateToggleStatus(bean);
            }
        }

        return result;
    }

    /**
     * Method responsible to read toggle configuration
     *
     * @param toggleId
     * @param toggleVersion
     * @return
     * @throws ToggleNotFoundException
     * @throws ToggleStatusNotValidException
     */
    public String readToggleConfig(String toggleId, String toggleVersion) throws ToggleNotFoundException, ToggleStatusNotValidException {
        ToggleBean toggleBean = toggleProperties.getToggleBeanFromToggleId(toggleId, toggleVersion);

        return toggleBean.getToggleKey() + " - " + toggleBean.isToggleStatus() + " - " + toggleBean.getAppsAlwaysTrue().toString()+" - "+toggleBean.getAppsAlwaysFalse().toString() + " - " + toggleBean.getAppsAuthorized().toString();
    }


    /**
     * Method responsible to remove toggle configuration
     *
     * @param toggleId
     * @param toggleVersion
     * @return
     * @throws ToggleNotFoundException
     */
    public Boolean removeToggleConfig(String toggleId, String toggleVersion) throws ToggleNotFoundException {
        ToggleBean toggleBean = toggleProperties.getToggleBeanFromToggleId(toggleId, toggleVersion);

        boolean result = toggleProperties.updateToggleConfiguration(toggleBean, ToggleAction.REMOVE_TOGGLE_CONFIG);

        return result;
    }


    /*************************************************************************
     *
     * PRIVATE METHODS
     *
     *************************************************************************/

    /**
     * Method responsible to check toggle app status
     *
     * @param app
     * @param toggle
     */
    private Boolean checkToggleAppStatus(String app, ToggleBean toggle) throws ToggleForbiddenException, ToggleNotFoundException {

        boolean status = false;

        //if toggle is active let's check if app has authorization
        //if no config every app has access, if config only configured apps have access
        if (toggle.getAppsAuthorized() == null || toggle.getAppsAuthorized().size()==0 || toggle.getAppsAuthorized().contains(app)) {
            //If toggle is inactive, no one can access it
            if(toggle.isToggleStatus()) {
                //by default uses toggle status
                status = true;
                //if specific config for this app, uses the specific configuration
                if(toggle.getAppsAlwaysTrue().contains(app)){
                    status = true;
                }else if(toggle.getAppsAlwaysFalse().contains(app)){
                    status = false;
                }
            }
        } else {
            throw new ToggleForbiddenException();
        }
        return status;

    }

}
