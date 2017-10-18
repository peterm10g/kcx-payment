package com.lsh.payment.core.strategy.payVo.cmbcpay;

import com.lsh.payment.core.strategy.payVo.baseVo.BasePayResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Project Name: lsh-payment
 * Created by jingyuan
 * Date: 16/10/27
 * 北京链商电子商务有限公司
 * Package com.lsh.payment.core.payChannel.qfpay
 * desc:
 */
@Getter
@Setter
@NoArgsConstructor
public class CmbcQueryResponse extends BasePayResponse {

    /**
     * 序列化ID
     */
    private static final long serialVersionUID = 67575214382345747L;

    private String result;

    private String desc;

    private String amount;

    private String channelFlag;

    private String orderNo;

    private String orgTransId;

    private String orgReqId;

    private String outOrderNo;

    private String transType;

    private String transStatus;

    private String signOut;

    private String payTime;

    private String totalAmount;

    private String receiptAmount;

    private String invoiceAmount;

    private String buyerPayAmount;

    private String goodsName;

    private String extraDesc;

    private String bankType;

}
