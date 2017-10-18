package com.lsh.payment.api.model.lklpay;

import com.lsh.payment.api.model.baseVo.BaseContent;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/10/30.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public class LklPayContent extends BaseContent {

    /**
     * 序列化ID
     */
    private static final long serialVersionUID = -786582831901473766L;

    private String title;

    private boolean result;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }
}
