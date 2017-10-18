package com.lsh.payment.core.strategy.payVo.allinpay;

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
public class AllinQueryResponse extends BasePayResponse {

    /**
     * 序列化ID
     */
    private static final long serialVersionUID = 67575214382345747L;

    private String retcode;

    private String retmsg;

    private String cusid;

    private String appid;

    private String trxid;

    private String chnltrxid;

    private String reqsn;

    private String trxcode;

    private String trxamt;

    private String trxstatus;

    private String fintime;

    private String randomstr;

    private String sign;

    private String errmsg;

}
