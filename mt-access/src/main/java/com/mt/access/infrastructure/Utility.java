package com.mt.access.infrastructure;

import static com.mt.access.infrastructure.AppConstant.QUERY_PROJECT_IDS;

public class Utility {
    /**
     * add project id as part of query string
     * @param original query string to be appended
     * @param projectId project id
     * @return combined query string
     */
    public static String updateProjectIds(String original, String projectId) {
        if (original == null) {
            return QUERY_PROJECT_IDS + ":" + projectId;
        }
        original = original + "," + QUERY_PROJECT_IDS + ":" + projectId;
        return original;
    }
}
