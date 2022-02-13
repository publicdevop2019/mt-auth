package com.mt.access.application.client.representation;

import com.mt.access.domain.model.client.Client;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class ClientAutoApproveRepresentation {
    private Long id;
    private Boolean autoApprove;

    public ClientAutoApproveRepresentation(Client client) {
        id= client.getId();
        autoApprove=client.getAutoApprove();
    }
}
