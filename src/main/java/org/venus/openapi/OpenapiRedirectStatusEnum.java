package org.venus.openapi;

import lombok.Getter;

@Getter
public enum OpenapiRedirectStatusEnum {

    UNKNOWN((short) -1), UN_ACTIVE((short) 0), ACTIVE((short) 1) ;

    public final short code;

    OpenapiRedirectStatusEnum(short code) {
        this.code = code;
    }

    public static OpenapiRedirectStatusEnum of(short code) {
        for (OpenapiRedirectStatusEnum status : OpenapiRedirectStatusEnum.values()) {
            if (status.code == code) {
                return status;
            }
        }
        return UNKNOWN;
    }
}
