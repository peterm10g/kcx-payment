package com.lsh.payment.api.model.baseVo;


import com.lsh.base.common.exception.BusinessException;

/**
 * lsh-payment
 * Created by peter on 16/8/3.
 */
public enum ExceptionStatus {

    /** 操作成功 */
    SUCCESS("OK","0"),

    /** 操作成功 */
    SUCCESS_S("SUCCESS","0"),

    /** 操作成功 */
    FAIL_S("FAIL","1001"),

    /** 参数错误 */
    E1001001("参数异常","1001001"),

    /** 业务参数异常 */
    E1002001("业务参数异常","1002001"),

    /** 业务参数为空 */
    E1002002("业务参数为空","1002002"),

    /** 创建预支付订单异常 */
    E2001001("创建预支付订单异常","2001001"),

    /** 支付参数错误 */
    E2001002("支付参数错误","2001002"),

    /** 支付参数错误 */
    E2001003("重复支付","2001003"),

    /** 支付平台订单数据不存在 */
    E2001004("支付平台订单数据不存在","2001004"),

    /**调用第三方支付系统下单失败*/
    E2001005("支付下单失败","2001005"),

    /**支付回调参数 支付平台流水号不存在*/
    E2001006("支付回调参数,支付平台流水号不存在","2001006"),

    /**支付回调更新支付记录数据失败*/
    E2001007("支付回调更新支付记录数据失败","2001007"),

    /**订单金额与支付金额不相等*/
    E2001008("订单金额与支付金额不相等","2001008"),

    /**支付回调参数,支付时间不存在*/
    E2001009("支付回调参数,支付时间不存在","2001009"),

    /**支付回调参数,支付时间不存在*/
    E2001010("修改数据库支付授权状态失败","2001010"),

    /**支付回调参数,支付时间不存在*/
    E2001011("请求第三方授权失败","2001011"),

    /**支付回调参数,支付时间不存在*/
    E2001012("业务订单号对应的支付记录不存在","2001012"),

    /**支付查询结果为空*/
    E2001013("支付查询结果为空","2001013"),

    /**拉卡拉签名验证异常*/
    E2001014("拉卡拉签名验证异常","2001014"),

    /**参数验证错误*/
    E2001015("参数验证错误","2001015"),

    /**  读取配置文件错误*/
    E2002001("读取配置文件错误","2002001"),

    /**  文件操作失败*/
    E2002002("文件操作失败","2002002"),

    /**  读取配置文件错误*/
    E2003001("数据库异常或数据错误","2003001"),

    /**微信公众号,下载对账单失败*/
    E2004001("微信公众号下载对账单失败","E2004001"),

    /**微信开放平台,下载对账单失败*/
    E2004002("微信开放平台下载对账单失败","E2004002"),

    /**钱方,下载对账单失败*/
    E2004003("钱方下载对账单失败","E2004003"),

    /**支付宝,下载对账单*/
    E2004004("支付宝下载对账单失败","E2004004"),

    /**拉卡拉,下载对账单失败*/
    E2004005("拉卡拉下载对账单失败","E2004005"),

    /** 业务参数异常 */
    E3001001("系统异常","3001001");


    private final String message;

    private final String code;

    public String getMessage() {
        return message;
    }

    public String  getCode() {
        return code;
    }

    ExceptionStatus(String message, String code) {
        this.message = message;
        this.code = code;
    }

    public BusinessException exception() {
        return new BusinessException(this.getCode(), this.getMessage());
    }

    public BusinessException exception(String message) {
        return new BusinessException(this.getCode(), new StringBuilder(this.message).append("\n").append(message).toString());
    }

    public BusinessException exception(Throwable e) {
        return new BusinessException(this.getCode(), new StringBuilder(this.message).append("\n").append(e.getMessage()).toString(), e);
    }

}
