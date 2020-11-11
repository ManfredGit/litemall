package com.ottpay.paysdk.type;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Lxa on 2016/4/5.
 *
 * @author Lxa
 */
public enum CurrencyType {
    CAD("加元"), CNY("人民币"), HKD("港元"), TWD("台币"), EUR("欧元"), USD("美元"), GBP("英镑"), JPY("日元"), OTHER("其他");
    private final String msg;

    CurrencyType(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    @Override
    public String toString() {
        return msg;
    }


    //新增代码
    private static final Map<String, CurrencyType> stringToEnum = new HashMap<String, CurrencyType>();

    static {
        for (CurrencyType rs : values())
            stringToEnum.put(rs.name(), rs);
    }

    public static CurrencyType fromString(String name) throws Exception {
        if (stringToEnum.get(name) != null) {
            return stringToEnum.get(name);
        }
        throw new Exception("CURRENCY_NOT_SUPPORT");
    }
}
