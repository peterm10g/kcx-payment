package com.lsh.payment.api.service.exception;

import com.alibaba.dubbo.rpc.protocol.rest.support.ContentType;
import com.lsh.payment.api.model.baseVo.BaseResponse;
import com.lsh.base.common.exception.BizCheckedException;
import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import java.util.Date;

/**
 * Project Name: lsh-payment
 * Created by fuhao
 * Date: 16/7/16
 * 北京链商电子商务有限公司
 * Package com.lsh.payment.api.service.exception.
 * desc:类功能描述
 */
public class BizExceptionMapper implements ExceptionMapper<BizCheckedException> {
    public Response toResponse(BizCheckedException ex) {
        BaseResponse responseBaseVo = new BaseResponse();
        responseBaseVo.setTimestamp(new Date());

        if(StringUtils.isNotBlank(ex.getCode())){
            responseBaseVo.setRet(Integer.valueOf(ex.getCode()));
        }else{
            responseBaseVo.setRet(ExceptionConstant.RES_CODE_500);
        }

//        responseBaseVo.setRet(ex.getCode()!=null?Integer.parseInt(ex.getCode()): );
        /* StringBuffer msg = new StringBuffer();
         msg.append(ex.getMessage());
         msg.append(" case by :");
         msg.append(ex.getExceptionStackInfo());
         responseBaseVo.setMsg(msg.toString());*/ //todo 业务异常不抛出异常堆栈
        responseBaseVo.setMsg(ex.getMessage());
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseBaseVo).type(ContentType.APPLICATION_JSON_UTF_8).build();
    }
}
