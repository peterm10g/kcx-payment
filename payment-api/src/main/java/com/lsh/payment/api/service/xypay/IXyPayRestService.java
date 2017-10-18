package com.lsh.payment.api.service.xypay;

import javax.servlet.http.HttpServletRequest;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/10/27.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public interface IXyPayRestService {

    String xyNotify(HttpServletRequest request);

}
