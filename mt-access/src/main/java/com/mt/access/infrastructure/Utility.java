package com.mt.access.infrastructure;

import static com.mt.access.domain.model.client.ClientQuery.PROJECT_ID;

public class Utility {
    public static String updateProjectId(String queryParam, String projectId) {
        if (queryParam == null) {
            return PROJECT_ID + ":" + projectId;
        }
        queryParam = queryParam + "," + PROJECT_ID + ":" + projectId;
        return queryParam;
    }
}
