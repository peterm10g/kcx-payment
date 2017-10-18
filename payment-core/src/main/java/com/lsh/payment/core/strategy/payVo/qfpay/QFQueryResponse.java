package com.lsh.payment.core.strategy.payVo.qfpay;

import com.lsh.payment.core.strategy.payVo.baseVo.BasePayResponse;

import java.util.List;

/**
 * Project Name: lsh-payment
 * Created by jingyuan
 * Date: 16/10/27
 * 北京链商电子商务有限公司
 * Package com.lsh.payment.core.payChannel.qfpay
 * desc:
 */
public class QFQueryResponse extends BasePayResponse {

    /**
     * 序列化ID
     */
    private static final long serialVersionUID = 67575214382345747L;
    //    respmsg	调试信息
    private String respmsg;

    //    resperr	错误描述
    private String resperr;

    //    respcd 交易返回码 0000表示交易成功； 1143、1145表示交易中，需要继续查询交易结果； 其他返回码表示交易失败
    private String respcd;

    //    data	交易数据，为list，其中字段描述如下
    private List<QFData> data;

    //    page	页数
    private String page;

    //    page_size	每页显示数量
    private String page_size;

    public List<QFData> getData() {
        return data;
    }

    public void setData(List<QFData> data) {
        this.data = data;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getPage_size() {
        return page_size;
    }

    public void setPage_size(String page_size) {
        this.page_size = page_size;
    }

    public String getRespcd() {
        return respcd;
    }

    public void setRespcd(String respcd) {
        this.respcd = respcd;
    }

    public String getResperr() {
        return resperr;
    }

    public void setResperr(String resperr) {
        this.resperr = resperr;
    }

    public String getRespmsg() {
        return respmsg;
    }

    public void setRespmsg(String respmsg) {
        this.respmsg = respmsg;
    }
}
