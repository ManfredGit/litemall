package com.ottpay.paysdk.models;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class QueryOrderStatusResponse {
    String bizpay_order_id;
    String order_id;
    BigDecimal total_amount;
    BigDecimal refund_fee;
    String order_status;
    LocalDateTime trade_time; //"Aug 21, 2020 11:44:18 AM"
    BigDecimal tip;
    BigDecimal exchange_rate;
    PayInfo payInfo;
    String return_code;
}
