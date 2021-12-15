package com.mt.access.application.client.representation;

import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class ClientAutoApproveRepresentation {
    private Long id;
    private Boolean autoApprove;

    public ClientAutoApproveRepresentation(Object client) {
        BeanUtils.copyProperties(client, this);
    }
}
