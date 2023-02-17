package com.hw.helper;

public class AppConstant {
    public static final String CLIENT_ID_LOGIN_ID = "0C8AZZ16LZB4";
    public static final String CLIENT_ID_OAUTH2_ID = "0C8AZTODP4HT";
    public static final String CLIENT_ID_REGISTER_ID = "0C8B00098WLD";
    public static final String CLIENT_ID_OM_ID = "0C8HPGMON9J5";
    public static final String CLIENT_ID_RIGHT_ROLE_NOT_SUFFICIENT_RESOURCE_ID = "0C8AZTODP4H8";
    public static final String CLIENT_ID_RESOURCE_ID = "0C8AZTODP4I0";
    public static final String CLIENT_ID_TEST_ID = "0C8B00CSATJ6";
    public static final String COMMON_CLIENT_SECRET = "root";
    public static final String EMPTY_CLIENT_SECRET = "";
    public static final String ACCOUNT_USERNAME_ADMIN = "superAdmin@duoshu.org";
    public static final String ACCOUNT_PASSWORD_ADMIN = "Password1!";
    public static final String ACCOUNT_USERNAME_MALL_ADMIN = "mall@duoshu.org";
    public static final String ACCOUNT_PASSWORD_MALL_ADMIN = "Password1!";
    public static final String ACCOUNT_USERNAME_USER = "user1@duoshu.org";
    public static final String ACCOUNT_PASSWORD_USER = "Password1!";
    public static final String SVC_NAME_AUTH = "/auth-svc";
    public static final String SVC_NAME_TEST = "/test-svc";
    public static final String OBJECT_MARKET_REDIRECT_URI = "http://localhost:4200/account";
    public static final String GRANT_TYPE_PASSWORD = "password";
    public static final String GRANT_TYPE_CLIENT_CREDENTIALS = "client_credentials";
    public static final String GRANT_TYPE_AUTHORIZATION_CODE = "authorization_code";
    public static final String AUTHORIZE_STATE = "login";
    public static final String AUTHORIZE_RESPONSE_TYPE = "code";
    public static final String PROJECT_ID = "0P8HPG99R56P";
    public static final String CLIENTS = "/projects/0P8HE307W6IO/clients";
    public static String proxyUrl = "http://localhost:" + 8111;
    public static String accessUrl = "http://localhost:" + 8080;
    public static String testUrl = "http://localhost:" + 9999;
    public static final String CLIENT_MNGMT_URL = proxyUrl + SVC_NAME_AUTH + CLIENTS;
    public static String PROXY_URL_TOKEN = proxyUrl + SVC_NAME_AUTH + "/oauth/token";
    public static final String USERS_ADMIN = "/mngmt/users";

}
