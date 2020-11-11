package com.ottpay.paysdk.models;

public class ApiInvalidResponse extends ApiResponse {
    public ApiInvalidResponse(Object result) {
        this.status = "INVALID";
        this.result = result;
    }
}
