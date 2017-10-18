package com.lsh.payment.core.model.Async;

import com.lsh.payment.api.model.baseVo.BaseRequest;
import com.lsh.payment.api.model.baseVo.BaseResponse;

/**
 * Project Name: lsh-payment
 * Created by jingyuan
 * Date: 16/12/26
 * 北京链商电子商务有限公司
 * Package com.lsh.payment.core.model.Async
 * desc:
 */
public class PayMonitorInterfaceModel {
    private Object baseRequest;
    private boolean resultFlag;
    private String wasteTime;
    private String className;

    public PayMonitorInterfaceModel(Object baseRequest, String wasteTime, String className) {
        this.baseRequest=baseRequest;
        this.wasteTime=wasteTime;
        this.className=className;
        this.setResultFlag(true);
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Object getBaseRequest() {
        return baseRequest;
    }

    public void setBaseRequest(Object baseRequest) {
        this.baseRequest = baseRequest;
    }

    public String getWasteTime() {
        return wasteTime;
    }

    public void setWasteTime(String wasteTime) {
        this.wasteTime = wasteTime;
    }

    public boolean isResultFlag() {
        return resultFlag;
    }

    public void setResultFlag(boolean resultFlag) {
        this.resultFlag = resultFlag;
    }
}
