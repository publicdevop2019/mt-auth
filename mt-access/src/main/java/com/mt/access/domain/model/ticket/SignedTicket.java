package com.mt.access.domain.model.ticket;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignedTicket {
    private String value;

    public SignedTicket(String serialize) {
        this.value = serialize;
    }
}
