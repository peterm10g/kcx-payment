package com.lsh.payment.api.model.baseVo;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.io.Serializable;
import java.util.Date;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/7/6.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
@SuppressFBWarnings({"EI_EXPOSE_REP2", "EI_EXPOSE_REP"})
public class BaseResponse implements Serializable{

    /**
     * 序列化ID
     */
    private static final long serialVersionUID = -8338882831901474666L;
    /**
     * 返回信息状态码
     */
    private Integer ret;
    /**
     * 返回信息
     */
    private String msg;
    /**
     * 返回时间
     */
    private Date timestamp;

    public BaseResponse() {

    }

    public BaseResponse(Integer ret, String msg, Date timestamp) {
        this.ret = ret;
        this.msg = msg;
        this.timestamp = timestamp;
    }

    public Integer getRet() {
        return ret;
    }

    public void setRet(Integer ret) {
        this.ret = ret;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Date getTimestamp() {
        if(timestamp == null){

            timestamp = new Date();
        }

        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }


}
