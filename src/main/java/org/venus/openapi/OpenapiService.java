package org.venus.openapi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OpenapiService implements IOpenapiService {
    @Override
    public OpenapiEntity get(String original) {
        return null;
    }

    @Override
    public RedirectEntity redirect(String original) {
        return null;
    }
}
