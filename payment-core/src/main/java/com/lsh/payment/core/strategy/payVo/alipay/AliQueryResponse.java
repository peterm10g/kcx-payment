package com.lsh.payment.core.strategy.payVo.alipay;

import com.alipay.api.response.AlipayTradeQueryResponse;
import com.lsh.payment.core.strategy.payVo.baseVo.BasePayResponse;

/**
 * Project Name: lsh-payment
 * Created by jingyuan
 * Date: 16/10/27
 * 北京链商电子商务有限公司
 * Package com.lsh.payment.core.payChannel.alipay
 * desc:支付宝查询返回报文
 */
public class AliQueryResponse extends BasePayResponse {

    private AlipayTradeQueryResponse resp;

    public AlipayTradeQueryResponse getResp() {
        return resp;
    }

    public void setResp(AlipayTradeQueryResponse resp) {
        this.resp = resp;
    }
}
