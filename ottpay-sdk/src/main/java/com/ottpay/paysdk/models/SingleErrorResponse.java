package com.ottpay.paysdk.models;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class SingleErrorResponse extends ApiResponse{

    public SingleErrorResponse(String error) {
        this.status = "ERROR";
        this.result = error;
    }
}
