package com.ottpay.paysdk.models;

import com.ottpay.paysdk.type.BizType;
import com.ottpay.paysdk.type.OrderType;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.math.BigDecimal;

@Data
public class CreatePreOrderRequest {
    @Enumerated(EnumType.STRING)
    BizType bizType;

    @Enumerated(EnumType.STRING)
    OrderType payType;

    String orderId;
    String  openId;
    BigDecimal amount;
    String authCode;
    String callback;
    String returnURL;
}
