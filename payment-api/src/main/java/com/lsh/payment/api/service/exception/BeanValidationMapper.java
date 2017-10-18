package com.lsh.payment.api.service.exception;

import com.alibaba.dubbo.rpc.protocol.rest.RpcExceptionMapper;
import com.alibaba.dubbo.rpc.protocol.rest.support.ContentType;
import com.lsh.payment.api.model.baseVo.BaseResponse;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Response;
import java.util.Date;

/**
 * Project Name: lsh-payment
 * Created by zhangqiang on 16/7/18.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public class BeanValidationMapper extends RpcExceptionMapper {
    @Override
    protected Response handleConstraintViolationException(ConstraintViolationException cve) {
        //获取bean validation错误信息
        StringBuffer msg = new StringBuffer();

        for (ConstraintViolation cv : cve.getConstraintViolations()) {
            msg.append("'")
                    .append(cv.getPropertyPath().toString())
                    .append("'")
                    .append(cv.getMessage())
                    .append(";");

        }

        BaseResponse res = new BaseResponse(1002001,msg.toString(),new Date());


        return Response.status(Response.Status.OK).entity(res).type(ContentType.APPLICATION_JSON_UTF_8).build();
    }
}
