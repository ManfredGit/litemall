package com.ottpay.paysdk.models;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MiniProWeChatPayParams {
    String merchant_id;
    String order_id;
    String sale_num;
    BigDecimal amount;
    PayInfo payInfo;

    public MiniProWeChatPayParams(String merchant_id, String order_id, String sale_num, BigDecimal amount, PayInfo payInfo) {
        this.merchant_id = merchant_id;
        this.order_id = order_id;
        this.sale_num = sale_num;
        this.amount = amount;
        this.payInfo = payInfo;
    }
}
