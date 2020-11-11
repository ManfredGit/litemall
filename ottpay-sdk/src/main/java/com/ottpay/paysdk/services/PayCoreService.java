package com.ottpay.paysdk.services;

import com.google.gson.Gson;
import com.ottpay.paysdk.commons.ProcessFeign;
import com.ottpay.paysdk.exceptions.ApiException;
import com.ottpay.paysdk.models.*;
import com.ottpay.paysdk.type.ActionEnums;
import com.ottpay.paysdk.type.BizType;
import com.ottpay.paysdk.utils.AppCommonUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class PayCoreService {
    private static Log logger = LogFactory.getLog(PayCoreService.class);
    String unionpayHeader = "hQVDUFY";

    //@Value("${app.order.call.back.url}")
    //private String orderCallBackUrl;

    @Autowired
    private ProcessFeign processFeign;

    Gson gson = new Gson();

    public String activePayGetUrl(String url, String orderCallBackUrl, String orderId, BigDecimal amount, BizType bizType, MerchantInfo merchant, String comment) throws ApiException {
        if(merchant == null) throw new ApiException("error.invalid.merchant.id");
        String merchantId = merchant.getMerchantId();
        String operatorId = merchant.getOperatorId();
        String merchantSignKey = merchant.getSignKey();
        if(StringUtils.isEmpty(merchantId)) throw new ApiException("error.invalid.merchant.merchantId");
        if(StringUtils.isEmpty(operatorId)) throw new ApiException("error.invalid.merchant.operatorId");
        if(StringUtils.isEmpty(merchantSignKey)) throw new ApiException("error.invalid.merchant.signkey");

        Map<String, String> data = new HashMap<>();
        data.put("order_id", orderId);
        data.put("operator_id", operatorId);
        data.put("amount", amount.movePointRight(2).toString());
        data.put("biz_type", bizType.toString());
        data.put("order_remark", comment);
        data.put("call_back_url",orderCallBackUrl);
        String md5 = AppCommonUtils.getMd5(data);

        ProcessReqBO processReqBO = new ProcessReqBO().setAction(ActionEnums.ACTIVEPAY)
                .setMd5(md5).setMerchant_id(merchantId).setVersion("1.1").setOperaror_id(operatorId)
                .setData(AppCommonUtils.encrypted(gson.toJson(data), merchantSignKey, md5));

        ProcessRespBO process = processFeign.process(processReqBO, url);
        if(process.getRsp_code().equalsIgnoreCase("SUCCESS")) {
            String decipher = AppCommonUtils.decipher(process.getData(), merchantSignKey, process.getMd5());
            Map<String, String> map = gson.fromJson(decipher, Map.class);
            return map.get("code_url");
        } else {
            logger.info(String.format("process return code: %s", process.getRsp_code()));
            throw new ApiException("error.failed.to.create.order");
        }
    }


    private String getBizTypeFromAuthCode(String authCode) throws ApiException {
        if(StringUtils.isEmpty(authCode)) throw new ApiException("error.empty.authCode");

        int len = authCode.length();
        if(len < 16) throw new ApiException("error.invalid.authCode");
        logger.info("getBizTypeFromAuthCode(), authCode="+authCode);
        String codeHeader = authCode.substring(0, 2);
        List<String> wechatPayHeaderList = Arrays.asList("10", "11", "12", "13", "14", "15");
        List<String> alipayHeaderList = Arrays.asList("25", "26", "27", "28", "29", "30");
        if(wechatPayHeaderList.contains(codeHeader) && len == 18) {
            return "WECHATPAY";
        } else if(alipayHeaderList.contains(codeHeader) && (len >= 16 && len <= 24)) {
            return "ALIPAY";
        } else if(authCode.startsWith(unionpayHeader)) {
            return "UNIONPAY";
        } else {
            throw new ApiException("error.unknown.authCode");
        }
    }

    public PassivePayResponse passivePay(String url, String authCode, String orderId, BigDecimal amount, BigDecimal tip, MerchantInfo merchant, String operatorId, String operTermId, String saleNum) throws ApiException {
        if(amount == null) throw new ApiException("error.amount.is.required");
        if(merchant == null) throw new ApiException("error.invalid.merchant.id");
        String merchantId = merchant.getMerchantId();
        String merchantSignKey = merchant.getSignKey();
        if(StringUtils.isEmpty(merchantId)) throw new ApiException("error.invalid.merchant.merchantId");
        if(StringUtils.isEmpty(merchantSignKey)) throw new ApiException("error.invalid.merchant.signkey");

        Map<String, String> data = new HashMap<>();
        data.put("order_id", orderId);
        if(operatorId != null) data.put("operator_id", operatorId);

        data.put("amount", amount.movePointRight(2).toString());
        data.put("tip", tip == null ?  null : tip.movePointRight(2).toString());
        data.put("sale_num",saleNum);
        data.put("auth_code", authCode);
        String bizType = getBizTypeFromAuthCode(authCode);
        data.put("biz_type", bizType);
        String md5 = AppCommonUtils.getMd5(data);

        ProcessReqBO processReqBO = new ProcessReqBO().setAction(ActionEnums.PASSIVEPAY)
                .setMd5(md5).setMerchant_id(merchantId).setVersion("1.1").setOperaror_id(operatorId)
                .setData(AppCommonUtils.encrypted(gson.toJson(data), merchantSignKey, md5));

        ProcessRespBO process = processFeign.process(processReqBO, url);
        if(process.getRsp_code().equalsIgnoreCase("SUCCESS")) {
            String decipher = AppCommonUtils.decipher(process.getData(), merchantSignKey, process.getMd5());
            Map<String, String> map = gson.fromJson(decipher, Map.class);
            String buyerLoginId = map.get("buyer_logon_id");
            BigDecimal exchangeRate = null;
            try {
                exchangeRate = map.get("exchange_rate") == null ? null : new BigDecimal(map.get("exchange_rate"));
            } catch (Exception e) {
                logger.warn("failed to get exchange rate: " + map.get("exchange_rate"));
            }
            String orderStatus = map.get("order_status");
            Date tradeTime = map.get("trade_time") == null ? null : new Date(map.get("trade_time"));

            return new PassivePayResponse(buyerLoginId, orderId, exchangeRate, operTermId, bizType, orderStatus, tradeTime);
        } else if(process.getRsp_code().equalsIgnoreCase("AUTH_CODE_INVALID")) {
            throw new ApiException("error.invalid.authCode");
        } else {
            logger.info(String.format("process return code: %s", process.getRsp_code()));
            throw new ApiException("error.failed.to.passive.pay");
        }
    }

    // 获取WeChat Mini Program 支付参数
    public MiniProWeChatPayParams createWeChatMiniProgramPayParams(String url, String openId, String orderId, BigDecimal amount, BigDecimal tip, MerchantInfo merchant, String callback) throws ApiException {
        if(StringUtils.isEmpty(openId)) throw new ApiException("error.invalid.merchant.openId");
        if(StringUtils.isEmpty(orderId)) throw new ApiException("error.invalid.merchant.orderId");
        if(amount == null) throw new ApiException("error.amount.is.required");
        if(merchant == null) throw new ApiException("error.invalid.merchant.id");
        String merchantId = merchant.getMerchantId();
        String merchantSignKey = merchant.getSignKey();
        String appId = merchant.getAppId();
        if(StringUtils.isEmpty(merchantId)) throw new ApiException("error.invalid.merchant.merchantId");
        if(StringUtils.isEmpty(merchantSignKey)) throw new ApiException("error.invalid.merchant.signkey");
        if(StringUtils.isEmpty(appId)) throw new ApiException("error.invalid.merchant.appId");

        Map<String, String> data = new HashMap<>();
        data.put("order_id", orderId);
        data.put("open_id", openId);
        data.put("amount", amount.movePointRight(2).toString());
        data.put("tip", tip == null ?  null : tip.movePointRight(2).toString());
        data.put("app_id", appId);
        data.put("operator_id", merchant.getOperatorId());
        data.put("biz_type", "WECHATPAY");
        data.put("call_back_url", callback);
        String md5 = AppCommonUtils.getMd5(data);

        ProcessReqBO processReqBO = new ProcessReqBO().setAction(ActionEnums.MAPPPAY)
                .setMd5(md5).setMerchant_id(merchantId).setVersion("1.0")
                .setData(AppCommonUtils.encrypted(gson.toJson(data), merchantSignKey, md5));

        ProcessRespBO process = processFeign.process(processReqBO, url);
        if(process.getRsp_code().equalsIgnoreCase("SUCCESS")) {
            String decipher = AppCommonUtils.decipher(process.getData(), merchantSignKey, process.getMd5());
            Map<String, String> map = gson.fromJson(decipher, Map.class);
            map.keySet().stream().forEach(System.out::println);

            String sale_num = map.get("sale_num");

            PayInfo payInfo =  getPayInfo(map);
            //System.out.println("map= "+map.keySet().toString());
/*
{"payInfo":"
   {"appId": "wx5f89682208cf8e88",
    "timeStamp":"2018091015",
    "nonceStr":"fGzn4apm4g33UnYU",
    "packageStr":"prepay_id\\u003dwx11030407018895349c421ab82302468473",
    "signType":"MD5",
    "paySign":"0961D43010DF42EA6021D76915F36F7A"
   }",
   "merchant_id":"ON00009999",
   "order_id":"TEST0000008",
   "amount":1,
   "sale_num":"1536606245849717"
}
 */
            return new MiniProWeChatPayParams(merchantId, orderId, sale_num, amount, payInfo);
            //return new PassivePayResponse(buyerLoginId, orderId, exchangeRate, operTermId, bizType, orderStatus, tradeTime);
        } else if(process.getRsp_code().equalsIgnoreCase("AUTH_CODE_INVALID")) {
            throw new ApiException("error.invalid.authCode");
        } else {
            logger.info(String.format("process return code: %s", process.getRsp_code()));
            throw new ApiException("error.failed.to.passive.pay");
        }
    }

    public QueryOrderStatusResponse queryOrderStatus(String url, String orderId, MerchantInfo merchant) throws ApiException {
        if(StringUtils.isEmpty(orderId)) throw new ApiException("error.invalid.orderId");
        String merchantId = merchant.getMerchantId();
        String merchantSignKey = merchant.getSignKey();
        if(StringUtils.isEmpty(merchantId)) throw new ApiException("error.invalid.merchant.merchantId");
        if(StringUtils.isEmpty(merchantSignKey)) throw new ApiException("error.invalid.merchant.signkey");

        Map<String, String> data = new HashMap<>();
        data.put("order_id", orderId);
        String md5 = AppCommonUtils.getMd5(data);

        ProcessReqBO processReqBO = new ProcessReqBO().setAction(ActionEnums.STATUS_QUERY)
                .setMd5(md5).setMerchant_id(merchantId).setVersion("1.0")
                .setData(AppCommonUtils.encrypted(gson.toJson(data), merchantSignKey, md5));

        ProcessRespBO process = processFeign.process(processReqBO, url);
        if(process.getRsp_code().equalsIgnoreCase("SUCCESS")) {
            String decipher = AppCommonUtils.decipher(process.getData(), merchantSignKey, process.getMd5());
            Map<String, String> map = gson.fromJson(decipher, Map.class);
            map.keySet().stream().forEach(System.out::println);
            //System.out.println("map= "+map.keySet().toString());

            QueryOrderStatusResponse res = getQueryOrderStatusResponse(map);
/*
{
    "order_id":"2020041468976O124O000857",
    "total_amount":1,
    "refund_fee": 0,
    "order_status":"init",
    "trade_time":"Apr 14, 2020 12:08:58 AM",
    "tip": 0
}
data:
    [bizpay_order_id] => WX12243195213632230
    [order_id] => wc_2020082111441708871
    [total_amount] => 275900
    [refund_fee] => 0
    [order_status] => orderclosed
    [trade_time] => Aug 21, 2020 11:44:18 AM
    [tip] => 0
    [exchange_rate] => 5.24740000
    [payInfo] =>
    [return_code] => SUCCESS

 */
            return res;
            //return new PassivePayResponse(buyerLoginId, orderId, exchangeRate, operTermId, bizType, orderStatus, tradeTime);
        } else if(process.getRsp_code().equalsIgnoreCase("AUTH_CODE_INVALID")) {
            throw new ApiException("error.invalid.authCode");
        } else {
            logger.info(String.format("process return code: %s", process.getRsp_code()));
            throw new ApiException("error.failed.to.passive.pay");
        }
    }

    private QueryOrderStatusResponse getQueryOrderStatusResponse(Map<String, String> map) {
        QueryOrderStatusResponse res = new QueryOrderStatusResponse();
        res.setBizpay_order_id(map.get("biz_order_id"));
        res.setOrder_id(map.get("order_id"));
        //res.setTrade_time(map.get("trade_time"));
        res.setExchange_rate(new BigDecimal(map.get("exchange_rate")));
        res.setTotal_amount(new BigDecimal(map.get("total_amount")));
        res.setRefund_fee(new BigDecimal(map.get("refund_fee")));
        res.setTip(new BigDecimal(map.get("tip")));
        res.setReturn_code(map.get("return_code"));
        return res;

    }

    private PayInfo getPayInfo(Map<String, String> map){
        PayInfo payInfo =  new PayInfo();
        payInfo.setAppId(map.get("appId"));
        //payInfo.setTimeStamp(map.get("timeStamp"));
        payInfo.setNonceStr(map.get("nonceStr"));
        payInfo.setPackageStr(map.get("packageStr"));
        payInfo.setSignType(map.get("signType"));
        payInfo.setPaySign(map.get("paySign"));
        return payInfo;
    }

}
