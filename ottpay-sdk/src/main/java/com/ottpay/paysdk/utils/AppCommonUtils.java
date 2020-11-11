package com.ottpay.paysdk.utils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Base64Utils;

import java.util.TreeMap;

/**
 * com.icardpay.bussiness.front.util
 * ott_dev
 *
 * @EMAIL:SHENGMIAO@HKRT.CN
 * @Description: <br/>
 * Date         Author        Version        Discription
 * -----------------------------------------------------------------------------------
 * 2017/3/19      SHENGMIAO          1.0             1.0
 */
public class AppCommonUtils {
    static Logger logger = LoggerFactory.getLogger(AppCommonUtils.class);
    static Gson gson = new Gson();


    /**
     * 解密
     *
     * @param data
     * @param key
     * @param md5
     * @return
     */
    public static String decipherNew(String data, String key, String md5) {

        byte[] orgData = Base64Utils.decodeFromString(data);
        String aesKeyStr = MD5Util.encrypt(md5 + key).substring(8, 24).toUpperCase();
        byte[] aesKey = aesKeyStr.getBytes();
        String decData = new String(AES.decrypt(orgData, aesKey));

        TreeMap<String, Object> treeMap = null;
        try {
            treeMap = gson.fromJson(decData, TreeMap.class);
        } catch (JsonSyntaxException e) {
            logger.error("decrypt error！exception:{}", e);
            return null;
        }
        StringBuilder stb = new StringBuilder();
        for (String tk : treeMap.keySet()) {
            stb.append(treeMap.get(tk));
        }
        logger.debug("Check the original string data：{}", stb.toString());
        String calmd5 = MD5Util.encrypt(stb.toString());
        if (calmd5.toUpperCase().equals(md5.toUpperCase())) {
            return decData;
        } else {
            throw new RuntimeException("check sign fail");
        }
    }

    /**
     * 解密
     *
     * @param data
     * @param key
     * @param md5
     * @return
     */
    public static String decipher(String data, String key, String md5) {

        byte[] orgData = Base64.decode(data);
        String aesKeyStr = MD5Util.encrypt(md5 + key).substring(8, 24).toUpperCase();
        byte[] aesKey = aesKeyStr.getBytes();
        String decData = new String(AES.decrypt(orgData, aesKey));

        TreeMap<String, Object> treeMap = null;
        try {
            treeMap = gson.fromJson(decData, TreeMap.class);
        } catch (JsonSyntaxException e) {
            logger.error("decrypt error！exception:{}", e);
            return null;
        }
        StringBuilder stb = new StringBuilder();
        for (String tk : treeMap.keySet()) {
            stb.append(treeMap.get(tk));
        }
        logger.debug("Check the original string data：{}", stb.toString());
        String calmd5 = MD5Util.encrypt(stb.toString());
        if (calmd5.toUpperCase().equals(md5.toUpperCase())) {
            return decData;
        } else {
            throw new RuntimeException("check sign fail");
        }
    }

    /**
     * 加密
     *
     * @param data json
     * @param key
     * @param md5
     * @return
     */
    public static String encrypted(String data, String key, String md5) {
        String aesKeyStr = MD5Util.encrypt(md5 + key).substring(8, 24).toUpperCase();
        byte[] encrypt = AES.encrypt(data.getBytes(), aesKeyStr.getBytes());
        return Base64.encode(encrypt);
    }

    /**
     * 加密
     *
     * @param data json
     * @param key
     * @param md5
     * @return
     */
    public static String encryptedNew(String data, String key, String md5) {
        String aesKeyStr = MD5Util.encrypt(md5 + key).substring(8, 24).toUpperCase();
        byte[] encrypt = AES.encrypt(data.getBytes(), aesKeyStr.getBytes());
        return Base64Utils.encodeToString(encrypt);
    }

    public static String getMd5(Object obj) {
        String str = gson.toJson(obj);

        TreeMap<String, Object> treeMap = gson.fromJson(str, TreeMap.class);
        StringBuffer stb = new StringBuffer();
        for (String tk : treeMap.keySet()) {
            stb.append(treeMap.get(tk));
        }
        return MD5Util.encrypt(stb.toString()).toUpperCase();
    }


}
