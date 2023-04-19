package com.mt.access.infrastructure;

import static com.mt.access.domain.model.client.ClientQuery.PROJECT_ID;

public class Utility {
    /**
     * add project id as part of query string
     * @param original query string to be appended
     * @param projectId project id
     * @return combined query string
     */
    public static String updateProjectIds(String original, String projectId) {
        if (original == null) {
            return PROJECT_ID + ":" + projectId;
        }
        original = original + "," + PROJECT_ID + ":" + projectId;
        return original;
    }
}
