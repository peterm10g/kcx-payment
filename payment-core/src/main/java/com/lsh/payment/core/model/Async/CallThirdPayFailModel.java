package com.lsh.payment.core.model.Async;

/**
 * Project Name: lsh-payment
 * Created by jingyuan
 * Date: 16/12/27
 * 北京链商电子商务有限公司
 * Package com.lsh.payment.core.model.Async
 * desc:
 */
public class CallThirdPayFailModel {
    String flag;
    String errorMsg;
    Object req;

    public CallThirdPayFailModel(String flag, String errorMsg, Object req) {
        this.flag = flag;
        this.errorMsg = errorMsg;
        this.req = req;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public Object getReq() {
        return req;
    }

    public void setReq(Object req) {
        this.req = req;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }
}
