package com.lsh.payment.api.model.lklpay;

import com.lsh.payment.api.model.baseVo.BaseResponse;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/11/16.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public class LklQueryResponse extends BaseResponse {

    private String tradeId;

    private String tradeModule;

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public String getTradeModule() {
        return tradeModule;
    }

    public void setTradeModule(String tradeModule) {
        this.tradeModule = tradeModule;
    }
}
