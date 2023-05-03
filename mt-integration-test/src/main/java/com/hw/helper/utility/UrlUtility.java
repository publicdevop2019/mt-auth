package com.hw.helper.utility;

import static com.hw.helper.AppConstant.ACCESS_URL;
import static com.hw.helper.AppConstant.PROXY_URL;
import static com.hw.helper.AppConstant.TEST_URL;

public class UrlUtility {
    private static final boolean enableProxy = true;

    public static String getAccessUrl(String path) {
        String normalized = removeLeadingSlash(path);
        if (enableProxy) {
            return PROXY_URL + "/auth-svc/" + normalized;
        }
        return ACCESS_URL + "/"
            +
            normalized;
    }

    public static String getPageQuery(int pageNum, int size) {
        return "page=num:" + pageNum + ",size:" + size;
    }

    public static String appendPath(String url, String path) {
        String normalized = removeLeadingSlash(path);
        return url + "/" + normalized;
    }

    public static String appendQuery(String url, String query) {
        String normalized = removeLeadingQuestionMark(query);
        return url + "?" + normalized;
    }

    public static String combinePath(String path1, String path2) {
        String normalized1 = removeLeadingSlash(path1);
        String normalized2 = removeLeadingSlash(path2);
        return normalized1 + "/" + normalized2;
    }

    public static String combinePath(String path1, String path2, String path3) {
        String normalized1 = removeLeadingSlash(path1);
        String normalized2 = removeLeadingSlash(path2);
        String normalized3 = removeLeadingSlash(path3);
        return normalized1 + "/" + normalized2 + "/" + normalized3;
    }

    public static String getTestUrl(String path) {
        String normalized = removeLeadingSlash(path);
        if (enableProxy) {
            return PROXY_URL + "/test-svc/" + normalized;
        }
        return TEST_URL + "/"
            +
            normalized;
    }

    private static String removeLeadingSlash(String path) {
        return path.replaceAll("^/+", "");
    }

    private static String removeLeadingQuestionMark(String path) {
        return path.replaceAll("^/?+", "");
    }
}
