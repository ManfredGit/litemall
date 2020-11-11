package com.ottpay.paysdk.models;

import lombok.Data;

@Data
public class MerchantInfo {
    String merchantId;
    String operatorId;
    String signKey;
    String signStr;
    String appId;

}
