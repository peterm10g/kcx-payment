package com.lsh.payment.core.service.xypay;

import java.util.Map;

/**
 * Project Name: lsh-payment
 * Created by peter on 17/8/23.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public interface IXyPayNotifyService {

    /**
     * 回调接口
     * @param dataMap
     */
    void dealNotify(Map<String, String> dataMap);
}
