package com.mt.access.domain.model.client;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ExternalUrl extends RedirectUrl {
    public ExternalUrl(String url) {
        super(url);
    }
}
