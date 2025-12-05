package com.mt.common;

public class CommonConstant {
    public static final String HTTP_HEADER_ERROR_ID = "x-mt-error-id";
    public static final String HTTP_HEADER_ERROR_CODE = "error-code";
    public static final String HTTP_HEADER_SUPPRESS = "suppressEx";
    public static final String HTTP_HEADER_SUPPRESS_REASON_INTEGRITY_VIOLATION =
        "integrityViolation";
    public static final String HTTP_PARAM_QUERY = "query";
    public static final String HTTP_PARAM_PAGE = "page";
    public static final String HTTP_PARAM_SKIP_COUNT = "config";
    public static final String HTTP_HEADER_CHANGE_ID = "changeId";
    public static final String HTTP_HEADER_AUTHORIZATION = "authorization";
    public static final String HTTP_PARAM_LANG = "lang";
    public static final String PATCH_OP_TYPE_REMOVE = "remove";
    public static final String PATCH_OP_TYPE_SUM = "sum";
    public static final String PATCH_OP_TYPE_ADD = "add";
    public static final String PATCH_OP_TYPE_DIFF = "diff";
    public static final String PATCH_OP_TYPE_REPLACE = "replace";
    public static final String COMMON_ENTITY_ID = "id";
    public static final String QUERY_DELIMITER = ":";
    public static final String QUERY_OR_DELIMITER = ".";
    public static final String CHANGE_REVOKED = "_REVOKED";
    public static final String EXCHANGE_ROLLBACK = "rollback";
    public static final String CACHE_QUERY_PREFIX = "-query";
    public static final String CACHE_ID_PREFIX = "-id";
    public static final String EXCHANGE_NAME = "mt_global_exchange";
    public static final String EXCHANGE_NAME_ALT = "mt_global_exchange_alt";
    public static final String EXCHANGE_NAME_REJECT = "mt_global_exchange_reject";
    public static final String EXCHANGE_NAME_DELAY = "mt_global_exchange_delay";
    public static final String QUEUE_NAME_ALT = "unhandled_msg";
    public static final String QUEUE_NAME_REJECT = "rejected_msg";
    public static final String QUEUE_NAME_DELAY = "delay_msg";
    public static final String DOMAIN_ID = "domainId";
    public static final String VERSION = "version";
    public static final String APP_ID_PROXY = "0C8AZTODP4HT";
    public static final String APP_NAME_OAUTH = "OAUTH";

    private CommonConstant() {
    }
}
