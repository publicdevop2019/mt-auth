package com.mt.helper.pojo;

import lombok.Data;

@Data
public class ProtectedEndpoint {
    private String id;
    private String name;

    private String permissionId;
}
