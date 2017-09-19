package com.toggle.configuration;

import com.toggle.beans.ToggleBean;
import com.toggle.builders.ToggleBeanBuilder;
import com.toggle.exceptions.ToggleAlreadyExistsException;
import com.toggle.exceptions.ToggleForbiddenException;
import com.toggle.exceptions.ToggleNotFoundException;
import com.toggle.exceptions.ToggleStatusNotValidException;
import com.toggle.utils.ToggleConstants;
import org.apache.commons.configuration.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

import static com.toggle.utils.ToggleConstants.*;

@Component
public class TogglePropertiesManagement {

    private static String SEPARATOR = "_";

    private static final Logger logger = LoggerFactory.getLogger(TogglePropertiesManagement.class);

    @Autowired
    private TogglePropertiesConfiguration config;

    /**
     * Method responsible to return toggle status for specific app
     *
     * @param app
     * @param toggleId
     * @param toggleVersion
     * @return
     * @throws ToggleNotFoundException
     * @throws ToggleStatusNotValidException
     */
    public Boolean readToggleAppStatus(String app, String toggleId, String toggleVersion) throws ToggleNotFoundException, ToggleStatusNotValidException, ToggleForbiddenException {
        //get key
        String key = getToggleKey(toggleId, toggleVersion);
        //check if toggle is configured
        boolean isToggleConfig = isToggleConfigured(key);
        boolean status = false;

        if(isToggleConfig){

            boolean toggleStatus = getToggleStatus(key);

            ToggleBeanBuilder builder = new ToggleBeanBuilder();
            builder.setKey(toggleId, toggleVersion);
            builder.setToggleStatus(toggleStatus);
            builder.setAppsAlwaysFalse(getAppsAlwaysFalse(key));
            builder.setAppsAlwaysTrue(getAppsAlwaysTrue(key));
            builder.setAppsAuthorized(getAppsAuthorized(key));

            ToggleBean bean = builder.build();

            status = checkToggleAppStatus(app, bean);
        }else{
            throw new ToggleNotFoundException();
        }

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
        //TODO: Check application permissions

        final String key = toggleId+SEPARATOR+toggleVersion;
        boolean status = getToggleStatus(key);

        return status;
    }


    /**
     * Method responsible to add feature configuration
     *
     * @param toggleId
     * @param toggleVersion
     * @param toggleStatus
     * @param appsToApply
     * @param appsException
     */
    public Boolean addToggleStatus(String toggleId, String toggleVersion, String toggleStatus, String appsToApply, String appsException, String appsWithToggleAccess) throws ToggleStatusNotValidException, ToggleAlreadyExistsException {

        //TODO: Check application permissions
        boolean result = false;
        //get key
        String key = getToggleKey(toggleId, toggleVersion);
        boolean isKeyConfigured = isToggleConfigured(key);

        if(!isKeyConfigured){

            boolean isStatusValid = isToggleStatusValid(toggleStatus);
            if(isStatusValid){

                //adds configuration
                result = setToggleConfig(key, Boolean.parseBoolean(toggleStatus), appsToApply, appsException, appsWithToggleAccess);
            }else{
                throw new ToggleStatusNotValidException();
            }
        }else{
            throw new ToggleAlreadyExistsException();
        }

        return result;
    }

    /**
     * Method responsible to update feature configuration
     *
     * @param app
     * @param toggleId
     * @param toggleVersion
     * @param toggleStatus
     */
    public Boolean updateToggleAppStatus(String app, String toggleId, String toggleVersion, String toggleStatus) throws ToggleNotFoundException, ToggleStatusNotValidException {

        boolean result = false;
        //get key
        String key = getToggleKey(toggleId, toggleVersion);
        //check if toggle is configured
        boolean isToggleConfig = isToggleConfigured(key);

        if(isToggleConfig){

            boolean isToggleStatusValid = isToggleStatusValid(toggleStatus);
            boolean status = getToggleStatus(key);

            ToggleBeanBuilder builder = new ToggleBeanBuilder();
            builder.setKey(toggleId, toggleVersion);
            builder.setToggleStatus(status);
            builder.setAppsAlwaysFalse(getAppsAlwaysFalse(key));
            builder.setAppsAlwaysTrue(getAppsAlwaysTrue(key));
            builder.setAppsAuthorized(getAppsAuthorized(key));
            builder.updateAppStatus(app, toggleStatus);

            ToggleBean bean = builder.build();

            result = setToggleAppStatus(bean);
        }else{
            throw new ToggleNotFoundException();
        }
        return result;

    }


    /**
     * Method responsible to update feature configuration
     *
     * @param toggleId
     * @param toggleVersion
     * @param toggleStatus
     */
    public Boolean updateToggleStatus(String toggleId, String toggleVersion, String toggleStatus) throws ToggleNotFoundException, ToggleStatusNotValidException {

        boolean result = false;
        //get key
        String key = getToggleKey(toggleId, toggleVersion);
        //check if feature exists and get current status
        boolean status = getToggleStatus(key);
        boolean isStatusValid = isToggleStatusValid(toggleStatus);

        if(isStatusValid){
            logger.info("Toggle status updated from "+status+" to "+toggleStatus);
            //adds configuration
            result = updateToggleStatus(key, toggleStatus);
        }else{
            throw new ToggleStatusNotValidException();
        }
        return result;
    }

    /**
     * Method responsible to update feature configuration
     *
     * @param toggle
     */
    public Boolean updateToggleBean(ToggleBean toggle) throws ToggleNotFoundException, ToggleStatusNotValidException {

        boolean result = setToggleConfig(toggle.getToggleKey(), toggle.isToggleStatus(), toggle.getAppsAlwaysTrueStr(), toggle.getAppsAlwaysFalseStr(), toggle.getAppsAuthorizedStr());

        return result;

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

        final String key = toggleId+SEPARATOR+toggleVersion;
        boolean result = false;

        boolean status = getToggleStatus(key);
        logger.info("Remove toggle "+toggleId+" with status: "+status);
        config.clearProperty(key);
        config.clearProperty(key + SEPARATOR+ STATUS_ALWAYS_TRUE);
        config.clearProperty(key + SEPARATOR+ STATUS_ALWAYS_FALSE);
        config.clearProperty(key + SEPARATOR+ TOGGLE_ACCESS);
        try {
            config.save();
            result = true;
        } catch (ConfigurationException e) {
            logger.error("Error removing toggle configuration "+key);
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Method responsible to read feature configuration
     *
     * @param toggleId
     * @param toggleVersion
     * @return
     * @throws ToggleNotFoundException
     * @throws ToggleStatusNotValidException
     */
    public String readToggleConfig(String toggleId, String toggleVersion) throws ToggleNotFoundException, ToggleStatusNotValidException {
        //TODO: Check application permissions
        //TODO: Check if feature ID already exists

        String key = getToggleKey(toggleId, toggleVersion);
        boolean featureStatus = getToggleStatus(key);
        ArrayList appsAlwaysTrue = getAppsAlwaysTrue(key);
        ArrayList appsAlwaysFalse = getAppsAlwaysFalse(key);
        ArrayList appsAuthorized = getAppsAuthorized(key);

        return featureStatus + " - " + appsAuthorized.toString()+" - "+appsAlwaysTrue.toString() + " - " + appsAlwaysFalse.toString();
    }

    /*************************************************************************
     *
     * PRIVATE METHODS
     *
     *************************************************************************/

    /**
     * Method responsible to add toggle configuration
     *
     * @param key
     * @param toggleStatus
     * @param appsToApply
     * @param appsException
     * @return
     */
    private Boolean setToggleConfig(String key, Boolean toggleStatus, String appsToApply, String appsException, String appsWithToggleAccess){
        boolean result = false;

        config.setProperty(key, toggleStatus);
        config.setProperty(key + SEPARATOR + STATUS_ALWAYS_TRUE, appsToApply);
        config.setProperty(key + SEPARATOR + STATUS_ALWAYS_FALSE, appsException);
        config.setProperty(key + SEPARATOR + TOGGLE_ACCESS, appsWithToggleAccess);
        try {
            config.save();
            result = true;
        } catch (ConfigurationException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Method responsible to update toggle status
     *
     * @param key
     * @param toggleStatus
     * @return
     */
    private Boolean updateToggleStatus(String key, String toggleStatus){
        boolean result = false;

        config.setProperty(key, toggleStatus);

        try {
            config.save();
            result = true;
        } catch (ConfigurationException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Method responsible to check toggle app status
     *
     * @param app
     * @param toggle
     */
    public Boolean checkToggleAppStatus(String app, ToggleBean toggle) throws ToggleForbiddenException, ToggleNotFoundException {

        boolean status = false;

        //if toggle is active let's check if app has authorization
        //if no config every app has access, if config only configured apps have access
        if (toggle.getAppsAuthorized() == null || toggle.getAppsAuthorized().size()==0 || toggle.getAppsAuthorized().contains(app)) {
            //If toggle is inactive, no one can access it
            if(toggle.isToggleStatus()) {
                //if app has access
                if(toggle.getAppsAlwaysTrue().contains(app)){
                    status = true;
                }else if(toggle.getAppsAlwaysFalse().contains(app)){
                    status = false;
                }else{
                    status = getToggleStatus(toggle.getToggleKey());
                }
            }
        } else {
            throw new ToggleForbiddenException();
        }
        return status;

    }

    /**
     * Method responsible to update toggle application status
     *
     * @param toggle
     */
    private Boolean setToggleAppStatus(ToggleBean toggle) {

        boolean result = false;
        config.setProperty(toggle.getToggleKey() + SEPARATOR + STATUS_ALWAYS_TRUE, toggle.getAppsAlwaysTrueStr());
        config.setProperty(toggle.getToggleKey() + SEPARATOR + STATUS_ALWAYS_FALSE, toggle.getAppsAlwaysFalseStr());

        try {
            config.save();
            result = true;
        } catch (ConfigurationException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        return result;

    }

    /**
     * Method responsible to return the apps that have status
     * ALWAYS true
     *
     * @param key
     * @return
     */
    private ArrayList<String> getAppsAlwaysTrue(String key){

        Object value = config.getProperty(key+SEPARATOR+STATUS_ALWAYS_TRUE);
        ArrayList<String> appsAlwaysTrue = new ArrayList();
        if(value instanceof String){
            String valueStr = (String) value;

            if(!valueStr.isEmpty()){
                appsAlwaysTrue.add(valueStr);
            }
        }else if(value instanceof ArrayList){
            appsAlwaysTrue = (ArrayList<String>) value;
        }

        return appsAlwaysTrue;
    }

    /**
     * Method responsible to return the apps that have status
     * ALWAYS false
     *
     * @param key
     * @return
     */
    private ArrayList getAppsAlwaysFalse(String key){

        Object value = config.getProperty(key+SEPARATOR+STATUS_ALWAYS_FALSE);
        ArrayList<String> appsAlwaysFalse = new ArrayList();
        if(value instanceof String){
            String valueStr = (String) value;

            if(!valueStr.isEmpty()){
                appsAlwaysFalse.add(valueStr);
            }
        }else if(value instanceof ArrayList){
            appsAlwaysFalse = (ArrayList<String>) value;
        }

        return appsAlwaysFalse;
    }

    /**
     * Method responsible to return apps that have autorization to access this toggle
     *
     * @param key
     * @return
     */
    private ArrayList getAppsAuthorized(String key){
        Object value = config.getProperty(key+SEPARATOR+TOGGLE_ACCESS);
        ArrayList<String> appsAuthorized = new ArrayList();
        if(value instanceof String){
            String valueStr = (String) value;

            if(!valueStr.isEmpty()){
                appsAuthorized.add(valueStr);
            }
        }else if(value instanceof ArrayList){
            appsAuthorized = (ArrayList<String>) value;
        }

        return appsAuthorized;
    }

    /**
     * Method responsible to return feature key
     *
     * @param toggleId
     * @param toggleVersion
     * @return
     */
    private String getToggleKey(String toggleId, String toggleVersion){
        return toggleId+SEPARATOR+toggleVersion;
    }

    /**
     * Method responsible to get feature status
     *
     * @param key
     * @return
     * @throws ToggleNotFoundException
     */
    private boolean getToggleStatus(String key) throws ToggleNotFoundException {
        Object propertyValue = config.getProperty(key);
        boolean status = false;

        if (propertyValue != null){
            //FIXME
            if(propertyValue instanceof String) {
                String value = (String) propertyValue;
                boolean isValid = isToggleStatusValid(value);
                if (isValid) {
                    status = Boolean.parseBoolean(value);
                }
            }else if(propertyValue instanceof Boolean){
                status = (Boolean) propertyValue;
            }
        }else{
            throw new ToggleNotFoundException();
        }

        return status;
    }

    /**
     * Method responsible to check if feature exists
     *
     * @param key
     * @return
     * @throws ToggleNotFoundException
     */
    private boolean isToggleConfigured(String key) {
        Object propertyValue = config.getProperty(key);
        boolean isToggleConfig = false;

        if (propertyValue != null){
            isToggleConfig = true;
        }

        return isToggleConfig;
    }

    /**
     * Method responsible to check if toggle status is valid
     *
     * @param value
     * @return
     */
    private boolean isToggleStatusValid(String value){
        boolean isValid = false;

        if(ToggleConstants.TRUE.equals(value) || ToggleConstants.FALSE.equals(value)){
            isValid = true;
        }
        return isValid;
    }
}
