package com.lsh.payment.api.service.weChatPay;

import com.lsh.payment.api.model.wxpay.WxNotifyResponse;

import javax.servlet.http.HttpServletRequest;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/10/27.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public interface IWeChatPayRestService {

//    BaseResponse getWxChatCode(HttpServletRequest request);

    WxNotifyResponse weChatNotify(HttpServletRequest request);

//    WeChatNotifyResponse weChatNotify(WxpayRequest wxpayRequest);

}
