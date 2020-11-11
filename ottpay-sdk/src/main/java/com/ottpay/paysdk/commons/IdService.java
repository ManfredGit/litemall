package com.ottpay.paysdk.commons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.ApplicationScope;

import java.util.Date;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@ApplicationScope
@Service
public class IdService {
    private static final Logger logger = LoggerFactory.getLogger(IdService.class);

    Random random = new Random();

    String prefix = "EC_";

    private AtomicInteger clientOrderSubId = new AtomicInteger(0);
    private AtomicInteger storeOrderSubId = new AtomicInteger(0);
    private AtomicInteger paymentSubId = new AtomicInteger(0);

    private Long createId(AtomicInteger baseSubId) {
        return new Date().getTime() << 3 | baseSubId.incrementAndGet() & 0x7;
    }

    public Long createClientOrderId() {
        return createId(clientOrderSubId);
    }

    public Long createStoreOrderId() {
        return createId(storeOrderSubId);
    }

    public Long createPaymentId() {
        return createId(paymentSubId);
    }

    public String generateOrderID() {
        StringBuilder sb = new StringBuilder();
        long curDate=System.currentTimeMillis();
        int s=random.nextInt(999);
        sb.append(curDate).append(s);
        return sb.toString();
    }

    public String generateUniqueID(String source,String bizType) {
        StringBuilder sb = new StringBuilder();
        long curDate=System.currentTimeMillis();
        sb.append(source).append("-").append(bizType).append("-").append(curDate);
        return sb.toString();
    }

    public String createShareId() {
        String shareId =  UUID.randomUUID().toString().replaceAll("-", "");
        return shareId;
    }

    public String createTradeNum(int orderId)
    {
        StringBuilder sb = new StringBuilder(prefix);
        long curDate=System.currentTimeMillis();
        int s=random.nextInt(999);
        sb.append(curDate).append(s);
        sb.append(orderId);
        return sb.toString();
    }
}
