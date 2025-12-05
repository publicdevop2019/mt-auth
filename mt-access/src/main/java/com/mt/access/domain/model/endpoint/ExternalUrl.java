package com.mt.access.domain.model.endpoint;

import com.mt.access.domain.model.client.RedirectUrl;
import com.mt.common.domain.model.validate.Validator;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ExternalUrl extends RedirectUrl {
    public ExternalUrl(String url) {
        super(url);
    }
}
