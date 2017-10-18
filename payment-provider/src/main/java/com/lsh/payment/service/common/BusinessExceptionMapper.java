package com.lsh.payment.service.common;

import com.lsh.payment.api.model.baseVo.CommonResult;
import com.lsh.payment.core.exception.BusinessException;
import org.apache.log4j.Logger;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

/**
 * Project Name: lsh-payment
 * Created by huangdong on 16/7/15.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public class BusinessExceptionMapper implements ExceptionMapper<BusinessException> {

    private static final Logger logger = Logger.getLogger(BusinessExceptionMapper.class);

    /**
     *
     * @param e 异常信息
     * @return Response
     */
    public Response toResponse(BusinessException e) {
        logger.error(e.getMessage(), e);
        return Response.ok().entity(new CommonResult<Integer>(e.getCode(), e.getMessage())).build();
    }
}
