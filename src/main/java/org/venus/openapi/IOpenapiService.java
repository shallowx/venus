package org.venus.openapi;

import java.util.List;

public interface IOpenapiService {
    OpenapiEntity get(String encode);
    List<OpenapiEntity> lists();

    OpenapiEntity redirect(String original);

}
