package com.mt.access.domain.model.client;

import javax.persistence.Embeddable;
import javax.persistence.MappedSuperclass;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
public class ExternalUrl extends RedirectUrl{
    public ExternalUrl(String url) {
        super(url);
    }
}
