package org.venus.openapi;

public interface IOpenapiService {
    OpenapiEntity get(String original);

    RedirectEntity redirect(String original);
}
