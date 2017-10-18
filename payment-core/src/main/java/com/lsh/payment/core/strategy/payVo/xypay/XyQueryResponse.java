package com.lsh.payment.core.strategy.payVo.xypay;

import com.lsh.payment.core.strategy.payVo.baseVo.BasePayResponse;

/**
 * Project Name: lsh-payment
 * Created by jingyuan
 * Date: 16/10/27
 * 北京链商电子商务有限公司
 * Package com.lsh.payment.core.payChannel.qfpay
 * desc:
 */
public class XyQueryResponse extends BasePayResponse {

    /**
     * 序列化ID
     */
    private static final long serialVersionUID = 67575214382345747L;
    //    respmsg	调试信息
    private XyPayOrderQueryResult xyPayOrderQueryResult;

    public XyPayOrderQueryResult getXyPayOrderQueryResult() {
        return xyPayOrderQueryResult;
    }

    public void setXyPayOrderQueryResult(XyPayOrderQueryResult xyPayOrderQueryResult) {
        this.xyPayOrderQueryResult = xyPayOrderQueryResult;
    }
}
