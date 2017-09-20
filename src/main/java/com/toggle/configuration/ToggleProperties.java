package com.toggle.configuration;

import com.toggle.beans.ToggleBean;
import com.toggle.beans.ToggleObserver;
import com.toggle.builders.ToggleBeanBuilder;
import com.toggle.enums.ToggleAction;
import com.toggle.exceptions.ToggleAlreadyExistsException;
import com.toggle.exceptions.ToggleNotFoundException;
import com.toggle.exceptions.ToggleStatusAlreadySettedException;
import com.toggle.exceptions.ToggleStatusNotValidException;
import com.toggle.interfaces.ToggleNotifier;
import com.toggle.utils.ToggleConstants;
import org.apache.commons.configuration.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.toggle.utils.ToggleConstants.STATUS_ALWAYS_FALSE;
import static com.toggle.utils.ToggleConstants.STATUS_ALWAYS_TRUE;
import static com.toggle.utils.ToggleConstants.TOGGLE_ACCESS;

@Component
public class ToggleProperties  implements ToggleNotifier {

    private static final Logger logger = LoggerFactory.getLogger(TogglePropertiesManager.class);

    private static String SEPARATOR = "_";

    @Autowired
    private TogglePropertiesConfiguration config;

    //Key observers
    private HashMap<String, ArrayList<ToggleObserver>> keyObservers = new HashMap<>();

    /**
     * Method responsible to return toggle bean
     *
     * @param toggleId
     * @param toggleVersion
     * @return
     * @throws ToggleNotFoundException
     */
    public ToggleBean getToggleBeanFromToggleId(String toggleId, String toggleVersion) throws ToggleNotFoundException {
        ToggleBeanBuilder builder = getToggleBuilderFromToggleId(toggleId, toggleVersion);
        ToggleBean bean = null;

        if(builder != null){
            bean = builder.build();
        }
        return bean;
    }

    /**
     * Method responsible to return ToggleBeanBuilder
     * it is used by the manager to update toggle status or toggle app status
     *
     * @param toggleId
     * @param toggleVersion
     * @return
     * @throws ToggleNotFoundException
     */
    public ToggleBeanBuilder getToggleBuilderFromToggleId(String toggleId, String toggleVersion) throws ToggleNotFoundException {
        //check if toggle is configured
        boolean isToggleConfig = isToggleConfigured(toggleId, toggleVersion);
        ToggleBeanBuilder builder = null;

        if(isToggleConfig){

            String key = getToggleKey(toggleId, toggleVersion);
            boolean toggleStatus = false;
            try {
                toggleStatus = getToggleStatus(toggleId, toggleVersion);

            } catch (ToggleStatusNotValidException e) {
                logger.error("Status invalid was saved in properties file, this should not happen we return false!");
                toggleStatus = false;
            }

            builder = new ToggleBeanBuilder();
            builder.setKey(key);
            builder.setToggleId(toggleId);
            builder.setToggleVersion(toggleVersion);
            builder.setToggleStatus(toggleStatus);
            builder.setAppsAlwaysFalse(getAppsAlwaysFalse(key));
            builder.setAppsAlwaysTrue(getAppsAlwaysTrue(key));
            builder.setAppsAuthorized(getAppsAuthorized(key));

        }else{
            throw new ToggleNotFoundException();
        }
        return builder;
    }

    /**
     * Create toggleBean, duplicated toggle configurations are not accepted
     *
     * @param toggleId
     * @param toggleVersion
     * @return
     * @throws ToggleStatusNotValidException
     * @throws ToggleAlreadyExistsException
     */
    public ToggleBean createToggleBean( String toggleId, String toggleVersion, String toggleStatus, String appsAlwaysTrueStr, String appsAlwaysFalseStr, String appsAccessStr) throws ToggleStatusNotValidException, ToggleAlreadyExistsException

    {
        //check if toggle is configured
        boolean isToggleConfig = isToggleConfigured(toggleId, toggleVersion);
        ToggleBean toggleBean = null;

        if(!isToggleConfig){
            //get key
            String key = getToggleKey(toggleId, toggleVersion);
            boolean isStatusValid = isToggleStatusValid(toggleStatus);
            //only creates toggles with valid status
            if(isStatusValid){
                boolean status = Boolean.parseBoolean(toggleStatus);

                ToggleBeanBuilder builder = new ToggleBeanBuilder();
                builder.setKey(key);
                builder.setToggleId(toggleId);
                builder.setToggleVersion(toggleVersion);
                builder.setToggleStatus(status);

                ArrayList<String> appsAlwaysTrue = new ArrayList<String>(Arrays.asList(appsAlwaysTrueStr.split(",")));
                ArrayList<String> appsAlwaysFalse = new ArrayList<String>(Arrays.asList(appsAlwaysFalseStr.split(",")));
                ArrayList<String> appsAuthorized = new ArrayList<String>(Arrays.asList(appsAccessStr.split(",")));

                builder.setAppsAlwaysTrue(appsAlwaysTrue);
                builder.setAppsAlwaysFalse(appsAlwaysFalse);
                builder.setAppsAuthorized(appsAuthorized);

                toggleBean = builder.build();
            }
        }else{
            throw new ToggleAlreadyExistsException();
        }
        return toggleBean;
    }

    /**
     * Method responsible to update toggle configuration
     * Remove toggles, update toggle status and update app toggle status
     *
     * @param toggle
     * @param action
     *
     * @return
     */
    public Boolean updateToggleConfiguration(ToggleBean toggle, ToggleAction action) {

        boolean result = false;
        //just save results in case there is a change
        boolean saveResults = false;

        //we only update the needed keys
        switch (action) {
            case CREATE_TOGGLE_CONFIG:
                saveResults = true;

                config.setProperty(toggle.getToggleKey(), toggle.isToggleStatus());
                config.setProperty(toggle.getToggleKey() + SEPARATOR + STATUS_ALWAYS_TRUE, toggle.getAppsAlwaysTrueStr());
                config.setProperty(toggle.getToggleKey() + SEPARATOR + STATUS_ALWAYS_FALSE, toggle.getAppsAlwaysFalseStr());
                config.setProperty(toggle.getToggleKey() + SEPARATOR + TOGGLE_ACCESS, toggle.getAppsAuthorizedStr());

                //always notifies, new toggle config created
                notifyKeyObservers(toggle.getToggleKey());
                break;
            case REMOVE_TOGGLE_CONFIG:
                //remove toggle configuration
                saveResults = true;
                config.clearProperty(toggle.getToggleKey());
                config.clearProperty(toggle.getToggleKey() + SEPARATOR + STATUS_ALWAYS_TRUE);
                config.clearProperty(toggle.getToggleKey() + SEPARATOR + STATUS_ALWAYS_FALSE);
                config.clearProperty(toggle.getToggleKey() + SEPARATOR + TOGGLE_ACCESS);

                //always notifies, new toggle config removed
                notifyKeyObservers(toggle.getToggleKey());
                break;
            case UPDATE_APP_STATUS:
                //update app toggle status
                saveResults = true;
                config.setProperty(toggle.getToggleKey() + SEPARATOR + STATUS_ALWAYS_TRUE, toggle.getAppsAlwaysTrueStr());
                config.setProperty(toggle.getToggleKey() + SEPARATOR + STATUS_ALWAYS_FALSE, toggle.getAppsAlwaysFalseStr());

                //TODO: Notifies if changes on app status, since only the APP can change this status this is not a priority
                //if the APP changes the value, it should not be necessary to inform the change happened
                break;
            }

        if(saveResults) {
            result = saveResults();
        }
        return result;

    }

    /**
     * Method responsible to update toggle configuration
     * Remove toggles, update toggle status and update app toggle status
     *
     * @param toggle
     *
     * @return
     */
    public Boolean updateToggleStatus(ToggleBean toggle) throws ToggleStatusAlreadySettedException, ToggleNotFoundException {

        boolean result = false;
        boolean saveResults = false;

        //update toggle status
        Boolean oldValue = null;
        try {
            oldValue = getToggleStatus(toggle.getToggleId(), toggle.getToggleVersion());
        } catch (ToggleStatusNotValidException e) {
            logger.error("Status invalid was saved in properties file, this should not happen at this stage, we proceed with update to correct the value!");
        }

        if (toggle.isToggleStatus() != oldValue){
            config.setProperty(toggle.getToggleKey(), toggle.isToggleStatus());

            saveResults = true;
            //notifies toggle status update
            notifyKeyObservers(toggle.getToggleKey());
        }else{
            throw new ToggleStatusAlreadySettedException();
        }

        if(saveResults) {
            result = saveResults();
        }

        return result;

    }

    /**
     * Method responsible to get toggle status
     *
     * @param toggleId
     * @param toggleVersion
     *
     * @return status
     * @throws ToggleNotFoundException
     * @throws ToggleStatusNotValidException
     */
    public boolean getToggleStatus(String toggleId, String toggleVersion) throws ToggleNotFoundException, ToggleStatusNotValidException {
        String key = getToggleKey(toggleId, toggleVersion);
        Object propertyValue = config.getProperty(key);
        boolean status = false;

        if (propertyValue != null) {
            //FIXME
            if (propertyValue instanceof String) {
                String value = (String) propertyValue;
                boolean isValid = isToggleStatusValid(value);
                if (isValid) {
                    status = Boolean.parseBoolean(value);
                }
            } else if (propertyValue instanceof Boolean) {
                status = (Boolean) propertyValue;
            }
        } else {
            throw new ToggleNotFoundException();
        }

        return status;
    }

    /**
     * Method responsible to check if toggle status is valid
     *
     * @param value
     * @return
     */
    public boolean isToggleStatusValid(String value) throws ToggleStatusNotValidException {
        boolean isValid = false;

        if(ToggleConstants.TRUE.equals(value) || ToggleConstants.FALSE.equals(value)){
            isValid = true;
        }else{
            throw new ToggleStatusNotValidException();
        }
        return isValid;
    }


    /*************************************************************************
     *
     * PRIVATE METHODS
     *
     *************************************************************************/

    private boolean saveResults(){
        boolean result = false;
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
     * Method responsible to return toggle key
     *
     * @param toggleId
     * @param toggleVersion
     * @return
     */
    private String getToggleKey(String toggleId, String toggleVersion){
        return toggleId+SEPARATOR+toggleVersion;
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
     * Method responsible to check if feature exists
     *
     * @param toggleId
     * @param toggleVersion
     *
     * @return
     * @throws ToggleNotFoundException
     */
    private boolean isToggleConfigured(String toggleId, String toggleVersion) {
        //get key
        String key = getToggleKey(toggleId, toggleVersion);
        Object propertyValue = config.getProperty(key);
        boolean isToggleConfig = false;

        if (propertyValue != null){
            isToggleConfig = true;
        }

        return isToggleConfig;
    }

    /*************************************************************************
     *
     * NOTIFIER METHODS
     *
     *************************************************************************/

    @Override
    public void addObserver(ToggleObserver toggleObserver) throws ToggleNotFoundException {

        //check if toggle is configured
        boolean isToggleConfig = isToggleConfigured(toggleObserver.getToggleId(), toggleObserver.getToggleVersion());
        boolean status = false;

        if(isToggleConfig){

            String key = getToggleKey(toggleObserver.getToggleId(), toggleObserver.getToggleVersion());
            ArrayList<ToggleObserver> observers = keyObservers.get(key);
            if (observers == null) {
                observers = new ArrayList<>();
            }
            observers.add(toggleObserver);

        }else{
            throw new ToggleNotFoundException();
        }
    }

    @Override
    public void removeObserver(ToggleObserver toggleObserver) {

        String key = getToggleKey(toggleObserver.getToggleId(), toggleObserver.getToggleVersion());
        ArrayList<ToggleObserver> observers = keyObservers.get(key);
        if (observers != null) {
            observers.remove(toggleObserver);
        }
    }

    @Override
    public void notifyKeyObservers(String key) {
        ArrayList<ToggleObserver> observers = keyObservers.get(key);

        if(observers != null){
            Iterator<ToggleObserver> iterator = observers.iterator();
            while (iterator.hasNext()){
                ToggleObserver observer = iterator.next();
                //TODO: Notify observers
                logger.info("Notifying observer app: "+observer.getApp()+", change on toggle: "+observer.getToggleId()+" - "+observer.getToggleVersion());
                //use ToggleRunner to send notification to observers
            }
        }
    }

}
