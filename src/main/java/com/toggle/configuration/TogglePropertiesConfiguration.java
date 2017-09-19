package com.toggle.configuration;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;

@Component
@PropertySource("classpath:application.properties")
//TODO: check - component by default scope is singleton
@Scope("singleton")
public class TogglePropertiesConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(TogglePropertiesManagement.class);

    private static PropertiesConfiguration configuration = null;

    @Value("${toggle-properties-path}")
    private String togglePropertiesPath;

    @PostConstruct
    private void init() {
        try {

            logger.debug("Loading the properties file: " + togglePropertiesPath);
            configuration = new PropertiesConfiguration(togglePropertiesPath);

            //Create new FileChangedReloadingStrategy to reload the properties file based on the given time interval
            FileChangedReloadingStrategy fileChangedReloadingStrategy = new FileChangedReloadingStrategy();
            fileChangedReloadingStrategy.setRefreshDelay(60*1000);

            configuration.setReloadingStrategy(fileChangedReloadingStrategy);
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
    }

    public Object getProperty(String key) {
        return configuration.getProperty(key);
    }

    public void clearProperty(String key) {
        configuration.clearProperty(key);
    }


    public void setProperty(String key, Object value) {
        configuration.setProperty(key, value);
    }

    public void save() throws ConfigurationException {
        configuration.save();
    }

}
