package com.ottpay.paysdk.models;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class PassivePayResponse {
    String buyerLoginId;
    String orderId;
    BigDecimal exchangeRate;
    String operator;
    String bizType;
    String orderStatus;
    Date tradeTime;

    public PassivePayResponse(String buyerLoginId, String orderId, BigDecimal exchangeRate, String operator, String bizType, String orderStatus, Date tradeTime) {
        this.buyerLoginId = buyerLoginId;
        this.orderId = orderId;
        this.exchangeRate = exchangeRate;
        this.operator = operator;
        this.bizType = bizType;
        this.orderStatus = orderStatus;
        this.tradeTime = tradeTime;
    }
}
