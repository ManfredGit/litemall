package com.ottpay.paysdk.models;

public class ApiSuccessResponse extends ApiResponse {
    public ApiSuccessResponse(Object result) {
        this.status = "OK";
        this.result = result;
    }
}
