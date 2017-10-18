package com.lsh.payment.api.service.qfPay;

import javax.servlet.http.HttpServletRequest;

/**
 * Project Name: lsh-payment
 * Created by jingyuan
 * Date: 16/10/31
 * 北京链商电子商务有限公司
 * Package com.lsh.payment.api.service.qfPay
 * desc:
 */
public interface IQFPayRestService {
    /**
     * @param request
     * @return
     */
    String qfNotify(HttpServletRequest request);
}
