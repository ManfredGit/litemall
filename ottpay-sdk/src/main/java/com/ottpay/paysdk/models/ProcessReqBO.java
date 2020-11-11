package com.ottpay.paysdk.models;


import com.ottpay.paysdk.type.ActionEnums;

/**
 */
public class ProcessReqBO {
    private ActionEnums action;
    private String version;
    private String merchant_id;
    private String operator_id;
    private String terminal_no;
    private String data;
    private String md5;

    public ActionEnums getAction() {
        return action;
    }

    public ProcessReqBO setAction(ActionEnums action) {
        this.action = action;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public ProcessReqBO setVersion(String version) {
        this.version = version;
        return this;
    }

    public String getMerchant_id() {
        return merchant_id;
    }

    public ProcessReqBO setMerchant_id(String merchant_id) {
        this.merchant_id = merchant_id;
        return this;
    }

    public String getOperator_id() {
        return operator_id;
    }

    public ProcessReqBO setOperaror_id(String operator_id) {
        this.operator_id = operator_id;
        return this;
    }

    public String getTerminal_no() {
        return terminal_no;
    }

    public ProcessReqBO setTerminal_no(String terminal_no) {
        this.terminal_no = terminal_no;
        return this;
    }

    public String getData() {
        return data;
    }

    public ProcessReqBO setData(String data) {
        this.data = data;
        return this;
    }

    public String getMd5() {
        return md5;
    }

    public ProcessReqBO setMd5(String md5) {
        this.md5 = md5;
        return this;
    }
}
