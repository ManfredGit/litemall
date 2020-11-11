package com.ottpay.paysdk.models;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PayInfo {
    String appId;
    LocalDateTime timeStamp;
    String nonceStr;
    String packageStr;
    String signType;
    String paySign;
}
