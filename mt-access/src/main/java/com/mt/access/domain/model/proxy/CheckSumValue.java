package com.mt.access.domain.model.proxy;

import com.mt.access.infrastructure.CheckSumService;
import com.mt.common.domain.model.validate.Validator;
import lombok.Getter;

@Getter
public class CheckSumValue {
    private String value;

    public CheckSumValue(Object object) {
        this.value = CheckSumService.getChecksum(object);
    }

    private CheckSumValue() {

    }

    public static CheckSumValue failed() {
        CheckSumValue checkSumValue = new CheckSumValue();
        checkSumValue.value = "Failed";
        return checkSumValue;
    }

    public static CheckSumValue raw(String checkSum) {
        Validator.notNull(checkSum);
        CheckSumValue checkSumValue = new CheckSumValue();
        checkSumValue.value = checkSum;
        return checkSumValue;
    }
}
