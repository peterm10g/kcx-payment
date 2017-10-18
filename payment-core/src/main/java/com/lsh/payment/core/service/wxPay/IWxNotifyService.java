package com.lsh.payment.core.service.wxPay;

import java.util.Map;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/11/26.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public interface IWxNotifyService {
    /**
     * 微信回调接口
     * @param dataMap
     */
    void weChatNotify(Map<String, String> dataMap);
}
