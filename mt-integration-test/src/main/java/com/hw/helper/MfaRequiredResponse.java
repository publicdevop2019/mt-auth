package com.hw.helper;

import lombok.Data;

@Data
public class MfaRequiredResponse {
    String message;
    String mfaId;
}
