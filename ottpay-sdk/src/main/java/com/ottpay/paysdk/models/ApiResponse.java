package com.ottpay.paysdk.models;

public class ApiResponse {
    String status;
    Object result;

    public ApiResponse() {
    }

    public ApiResponse(String status, Object result) {
        this.status = status;
        this.result = result;
    }

    public String getStatus() {
        return status;
    }

    public Object getResult() {
        return result;
    }
}
