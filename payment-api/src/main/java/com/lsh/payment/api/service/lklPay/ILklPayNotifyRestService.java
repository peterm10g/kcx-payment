package com.lsh.payment.api.service.lklPay;

import com.lsh.payment.api.model.lklpay.LklPayNotifyResponse;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/11/2.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public interface ILklPayNotifyRestService {
    /**
     * 拉卡拉支付回调接口
     * @return
     */
    LklPayNotifyResponse lklNotify(String sign,String data);
}
