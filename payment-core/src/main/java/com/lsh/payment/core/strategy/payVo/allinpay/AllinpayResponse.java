package com.lsh.payment.core.strategy.payVo.allinpay;


import com.lsh.payment.core.strategy.payVo.baseVo.BasePayResponse;
import lombok.Getter;
import lombok.Setter;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/10/25.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
@Getter
@Setter
public class AllinpayResponse extends BasePayResponse {

    // TODO
    /**
     * 交易返回码 0000表示交易成功； 1143、1145表示交易中，需要继续查询交易结果； 其他返回码表示交易失败
     * 9999	系统繁忙
     * 0000	成功
     * 1000	参数异常
     * 3002	订单号有误
     * 3004	商户不存在
     * 3006	Md5验证失败
     * 3009	请求流水号重复
     * 0007	参数错误等，主要是后端返回的验证错误
     * 0018	失败
     * 0019	商户被禁止，请联系业务人员
     * 0020	商户未审核通过，请联系业务人员
     * 0021	大客户不能发起交易
     * 0024	接入渠道已被禁止
     * 0025	支付渠道没有找到相对应的接入渠道
     * 0026	支付渠道对应的接入渠道已被禁止
     * 0029	商户没有在支付渠道进行注册
     * 0030	没有该业务
     * 0042	订单状态未知
     * 0045	商户未入住接入渠道
     * 0046	交易不是正扫，无法关闭
     * 1026	此渠道当日可用余额不足，无法退货
     */
    private String retcode;
    /**
     * 返回码描述
     */
    private String retmsg;
    /**
     * 平台方唯一交易请求流水号
     */
    private String trxid;
    /**
     * 请求交易的流水号
     */
    private String reqsn;
    /**
     * 商户订单号
     */
    private String orderNo;
    /**
     * 支付宝交易号
     */
    private String chnltrxid;

    private String sign;

    private String trxstatus;

    private String fintime;
    /**
     * 外部订单号，开发者平台订单号
     */
    private String randomstr;
    /**
     * 扫码时返回，二维码的url
     */
    private String payinfo;

}