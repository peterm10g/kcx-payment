package com.lsh.payment.core.service.alipay;

import com.lsh.payment.core.model.payment.PayDeal;

import java.util.Map;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/11/26.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public interface IAliNotifyService {

    /**
     * 支付宝回调接口
     * @param paramsMap  回调参数
     * @return           PayDeal
     * @throws Exception
     */
    PayDeal notifyHandle(Map<String, String> paramsMap) throws Exception;
}
