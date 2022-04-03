package com.hw.helper.utility;

import static com.hw.helper.AppConstant.accessUrl;
import static com.hw.helper.AppConstant.proxyUrl;
import static com.hw.helper.AppConstant.testUrl;

import com.hw.helper.AppConstant;

public class UrlUtility {
    private static final boolean enableProxy = true;

    public static String getAccessUrl(String path) {
        String normalized = removeLeadingSlash(path);
        if (enableProxy) {
            return proxyUrl + "/auth-svc/" + normalized;
        }
        return accessUrl + "/"
            +
            normalized;
    }

    public static String getTestUrl(String path) {
        String normalized = removeLeadingSlash(path);
        if (enableProxy) {
            return proxyUrl + "/test-svc/" + normalized;
        }
        return testUrl + "/"
            +
            normalized;
    }

    private static String removeLeadingSlash(String path) {
        return path.replaceAll("^/+", "");
    }
}
