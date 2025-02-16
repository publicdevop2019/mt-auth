package com.mt.access.infrastructure;

import java.util.regex.Pattern;

/**
 * app constant.
 */
public class AppConstant {
    public static final String DATA_VALIDATION_JOB_NAME = "DATA_VALIDATION";
    public static final String PROXY_VALIDATION_JOB_NAME = "PROXY_VALIDATION";
    public static final String ACCESS_DATA_PROCESSING_JOB_NAME = "ACCESS_DATA_PROCESSING";
    public static final String KEEP_WS_CONNECTION_JOB_NAME = "KEEP_WS_CONNECTION";
    public static final String DEFAULT_AUTO_ACTOR = "SYSTEM";
    public static final String QUERY_PROJECT_IDS = "projectIds";
    public static final String QUERY_ID = "id";
    public static final Pattern COUNTRY_CODE_REGEX = Pattern.compile("^[0-9]{1,3}$");
    public static final Pattern MOBILE_NUMBER_REGEX = Pattern.compile("^[0-9]{10,11}$");
    public static String MAIN_USER_ROLE_ID = "0Z8HHJ489SEE";
    public static String MAIN_PROJECT_ID = "0P8HE307W6IO";
}
