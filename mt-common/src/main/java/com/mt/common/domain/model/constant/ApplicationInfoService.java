package com.mt.common.domain.model.constant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ApplicationInfoService {
    @Value("${spring.application.name}")
    String applicationId;

    public String getApplicationId() {
        return applicationId;
    }
}
