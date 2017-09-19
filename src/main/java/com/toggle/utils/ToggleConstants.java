package com.toggle.utils;

public class ToggleConstants {

    /**
     * Name of the app that requests feature information
     */
    public static final String APP_NAME = "appName";

    /**
     * Id of the feature
     */
    public static final String TOGGLE_ID = "toggleId";

    /**
     * Version of the feature
     */
    public static final String TOGGLE_VERSION = "toggleVersion";

    /**
     * Status of the feature
     */
    public static final String TOGGLE_STATUS = "toggleStatus";

    /**
     * Restrict toggle to app
     */
    public static final String TOGGLE_ACCESS = "toggleAccess";

    /**
     * Name of the applications that have status always true
     * even if toggleStatus is false
     */
    public static final String STATUS_ALWAYS_TRUE = "statusAlwaysTrue";

    /**
     * Name of the applications that have status always false
     * even if toggleStatus is true
     */
    public static final String STATUS_ALWAYS_FALSE = "statusAlwaysFalse";

    /**
     * Values used to configure features
     */
    //public static final String ALL = "all";
    //public static final String NONE = "none";
    public static final String TRUE = "true";
    public static final String FALSE = "false";
}
