package com.hw.helper;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RevokeToken {
    private String targetId;
    private Long issuedAt;
    private String type;

}
