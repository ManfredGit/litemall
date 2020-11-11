package com.ottpay.paysdk.models;

import lombok.Data;

@Data
public class PaymentCallbackRequest {
    String data;
    String rsp_code;
    String rsp_msg;
    String merchant_id;
    String md5;
}
