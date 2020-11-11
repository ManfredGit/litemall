package com.ottpay.paysdk.models;

public class ApiErrorResponse extends ApiResponse {
    public ApiErrorResponse(Object result) {
        this.status = "ERROR";
        this.result = result;
    }
}
