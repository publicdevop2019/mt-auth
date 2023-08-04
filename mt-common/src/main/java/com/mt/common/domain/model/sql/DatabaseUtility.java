package com.mt.common.domain.model.sql;

import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;
import com.mt.common.domain.model.validate.Checker;

public class DatabaseUtility {
    public static void checkUpdate(Integer rowCount) {
        if (!Checker.equals(rowCount, 1)) {
            throw new DefinedRuntimeException("db update failed, expected 1 but got " + rowCount,
                "0064", HttpResponseCode.INTERNAL_SERVER_ERROR);
        }
    }
}
