package com.mt.proxy.domain;

import lombok.Data;

@Data
public class RevokeToken {
    private String targetId;
    private Long issuedAt;
}
