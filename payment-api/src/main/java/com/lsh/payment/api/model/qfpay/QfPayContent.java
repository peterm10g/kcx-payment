package com.lsh.payment.api.model.qfpay;

import com.alibaba.fastjson.JSONObject;
import com.lsh.payment.api.model.baseVo.BaseContent;

/**
 * Project Name: lsh-payment
 * Created by jingyuan
 * Date: 16/10/31
 * 北京链商电子商务有限公司
 * Package com.lsh.payment.api.payVo.qfpay
 * desc:
 */
public class QfPayContent extends BaseContent{

    private String title;
    private String haspayed;
    private String trade_id;
    //调用钱方返回的JSON串
    private JSONObject result;

    public String getHaspayed() {
        return haspayed;
    }

    public void setHaspayed(String haspayed) {
        this.haspayed = haspayed;
    }

    public JSONObject getResult() {
        return result;
    }

    public void setResult(JSONObject result) {
        this.result = result;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTrade_id() {
        return trade_id;
    }

    public void setTrade_id(String trade_id) {
        this.trade_id = trade_id;
    }

}
