package com.lsh.payment.core.strategy.payVo.wxpay;

import com.lsh.payment.core.strategy.payVo.baseVo.BasePayResponse;

/**
 * Project Name: lsh-payment
 * Created by jingyuan
 * Date: 16/10/28
 * 北京链商电子商务有限公司
 * Package com.lsh.payment.core.payChannel.weChatPay
 * desc:
 */
public class WxBasePayResponse extends BasePayResponse {

    //    返回状态码	return_code	是	String(16)	SUCCESS/FAIL此字段是通信标识，非交易标识，交易是否成功需要查看trade_state来判断
    private String return_code;

    //    返回信息	return_msg	否	String(128)	签名失败
    private String return_msg;

    //    公众账号ID	appid	是	String(32)	wxd678efh567hg6787	微信分配的公众账号ID
    private String appid;

    //    商户号	mch_id	是	String(32)	1230000109	微信支付分配的商户号
    private String mch_id;

    //    随机字符串	nonce_str	是	String(32)	5K8264ILTKCH16CQ2502SI8ZNMTM67VS	随机字符串，不长于32位。推荐随机数生成算法
    private String nonce_str;

    //    签名	sign	是	String(32)	C380BEC2BFD727A4B6845133519F3AD6	签名，详见签名生成算法
    private String sign;

    //    业务结果	result_code	是	String(16)	SUCCESS	SUCCESS/FAIL
    private String result_code;

    //    错误代码	err_code	否	String(32)	SYSTEMERROR	错误码
    private String err_code;

    //    错误代码描述	err_code_des	否	String(128)	系统错误	结果信息描述
    private String err_code_des;

    //    以下字段在return_code 和result_code都为SUCCESS的时候有返回
    //    设备号	device_info	否	String(32)	013467007045764	微信支付分配的终端设备号，
    private String device_info;

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getDevice_info() {
        return device_info;
    }

    public void setDevice_info(String device_info) {
        this.device_info = device_info;
    }

    public String getErr_code() {
        return err_code;
    }

    public void setErr_code(String err_code) {
        this.err_code = err_code;
    }

    public String getErr_code_des() {
        return err_code_des;
    }

    public void setErr_code_des(String err_code_des) {
        this.err_code_des = err_code_des;
    }

    public String getMch_id() {
        return mch_id;
    }

    public void setMch_id(String mch_id) {
        this.mch_id = mch_id;
    }

    public String getNonce_str() {
        return nonce_str;
    }

    public void setNonce_str(String nonce_str) {
        this.nonce_str = nonce_str;
    }

    public String getResult_code() {
        return result_code;
    }

    public void setResult_code(String result_code) {
        this.result_code = result_code;
    }

    public String getReturn_code() {
        return return_code;
    }

    public void setReturn_code(String return_code) {
        this.return_code = return_code;
    }

    public String getReturn_msg() {
        return return_msg;
    }

    public void setReturn_msg(String return_msg) {
        this.return_msg = return_msg;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
